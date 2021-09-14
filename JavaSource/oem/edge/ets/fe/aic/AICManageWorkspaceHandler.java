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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProjectInfoBean;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.wspace.ETSSendWorkspaceEvent;
import oem.edge.ets.fe.wspace.ETSWorkspaceDAO;

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AICManageWorkspaceHandler {


	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";

	private ETSParams params;
	private String header = "";
	private EdgeAccessCntrl es = null;


	public AICManageWorkspaceHandler(ETSParams parameters, EdgeAccessCntrl es1) {

		this.params = parameters;
		this.es = es1;

	}

	public void handleRequest() throws SQLException, Exception {

		try {

			Connection con = this.params.getConnection();
			PrintWriter out = this.params.getWriter();
			ETSProj proj = this.params.getETSProj();
			EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
			HttpServletRequest request = this.params.getRequest();

			String sWFlag = request.getParameter("manage");

			if (sWFlag == null || sWFlag.trim().equals("")) {
				sWFlag = "";
			} else {
				sWFlag = sWFlag.trim();
			}

			if (sWFlag.trim().equals("")){
				displayManageWorkspace();
					//this.params.getResponse().sendRedirect("ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=inter&set=" + sSetMetID + "&linkid=" + params.getLinkId());
			} else if (sWFlag.trim().equals("archive")){
				archiveWorkspace();
			} else if (sWFlag.trim().equals("archive-confirm")){
				archiveWorkspaceConfirm();
			} else if (sWFlag.trim().equals("delete")){
				deleteWorkspace();
			} else if (sWFlag.trim().equals("delete-confirm")){
				deleteWorkspaceConfirm();
			} else if (sWFlag.trim().equals("request_transfer")){
				requestNewWorkspaceOwner();
			} else if (sWFlag.trim().equals("request_transfer-confirm")){
				requestNewWorkspaceOwnerConfirm();
			} else if (sWFlag.trim().equals("transfer")){
				transferWorkspaceOwner("");
			} else if (sWFlag.trim().equals("transfer-confirm")){
				String sError = validateTransferWorkspaceOwner();
				if (!sError.trim().equalsIgnoreCase("")) {
					transferWorkspaceOwner(sError);
				} else {
					transferWorkspaceOwnerConfirm();
				}

			} else if (sWFlag.trim().equals("transfer-confirm2")){
				transferWorkspaceOwnerFinal();
			} else if (sWFlag.trim().equals("request_subworkspace")){
				requestSubWorkspace();
			} else if (sWFlag.trim().equals("request_subworkspace-confirm")){
				requestSubWorkspaceConfirm();
			} else if (sWFlag.trim().equals("promote")){
				promoteWorkspaceToProject();
			} else if (sWFlag.trim().equals("promote-confirm")){
				promoteWorkspaceToProjectConfirm();
			} else if (sWFlag.trim().equals("promote-confirm2")){
				promoteWorkspaceToProjectFinal();
			} else if (sWFlag.trim().equals("promote_public")){
				promoteWorkspaceToPublic();
			} else if (sWFlag.trim().equals("promote_restricted")){
				promoteWorkspaceToRestricted();
			} else if (sWFlag.trim().equals("promote_private")){
				promoteWorkspaceToPrivate();
			} else if (sWFlag.trim().equals("public-confirm")){
				promoteWorkspaceToPublicConfirm();
			} else if (sWFlag.trim().equals("restricted-confirm")){
				promoteWorkspaceToRestrictedConfirm();
			} else if (sWFlag.trim().equals("private-confirm")){
				promoteWorkspaceToPrivateConfirm();
			}else if (sWFlag.trim().equals("promote_external")){
				promoteWorkspaceToExternalUsers();
			}else if (sWFlag.trim().equals("external_confirm")){
				promoteWorkspaceToExternalUsersConfirm();
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
	private void displayManageWorkspace() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		//AccessCntrlFuncs es = params.getEdgeAccessCntrl();
		boolean bAdmin = false;
		boolean bOEMSales = false;
		String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,es.gIR_USERN);
		Vector userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

		if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
			bAdmin = true;
		} else if (userents.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT) ||
					userents.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT) || 
					userents.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT)) {
			bOEMSales = true;
		}

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col1\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) 
				|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKFLOW_ADMIN) 
				|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {

			out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

			out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
			out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col2\" align=\"left\" >Please select one of the options below and select <b>Continue</b>.</td>");
			out.println("</tr>");
			out.println("</table>");

			printGreyDottedLine(out);
			out.println("<br />");


			// Promote workspace from Public to Private/Restricted
			if (!proj.getProject_status().equalsIgnoreCase("A")) {
				if (proj.getIsPrivate().equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PUBLIC)){
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					out.println("<td headers=\"col3\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"promote_restricted\" id=\"label_restricted\" /><label for=\"label_restricted\">Change this workspace access from Public to Restricted</label></td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					out.println("<td headers=\"col4\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"promote_private\" id=\"label_private\" /><label for=\"label_private\">Change this workspace access from Public to Private</label></td>");
					out.println("</tr>");
					out.println("</table>");
				} else if (proj.getIsPrivate().equalsIgnoreCase(Defines.AIC_IS_PRIVATE_RESTRICTED)){
					if (bAdmin || bOEMSales) {
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"col5\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"promote_public\" id=\"label_public\" /><label for=\"label_public\">Change this workspace access from Restricted to Public</label></td>");
						out.println("</tr>");
						out.println("</table>");
					}
					
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					out.println("<td headers=\"col6\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"promote_private\" id=\"label_private\" /><label for=\"label_private\">Change this workspace access from Restricted to Private</label></td>");
					out.println("</tr>");
					out.println("</table>");
				} else if (proj.getIsPrivate().equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PRIVATE)){
					if (bAdmin || bOEMSales) {
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"col7\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"promote_public\" id=\"label_public\" /><label for=\"label_public\">Change this workspace access from Private to Public</label></td>");
						out.println("</tr>");
						out.println("</table>");
					}
					
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					out.println("<td headers=\"col8\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"promote_restricted\" id=\"label_restricted\" /><label for=\"label_restricted\">Change this workspace access from Private to Restricted</label></td>");
					out.println("</tr>");
					out.println("</table>");

				}

			}
			if (!proj.getProject_status().equalsIgnoreCase("A")) {
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"col9\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"archive\" id=\"label_archive\" /><label for=\"label_archive\">Archive this workspace</label></td>");
				out.println("</tr>");
				out.println("</table>");
			}

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col10\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"delete\" id=\"label_delete\" /><label for=\"label_delete\">Delete this workspace</label></td>");
			out.println("</tr>");
			out.println("</table>");

			// dont display to super workspace admin
			// removed in 5.2.1 because workspace owner can now change the owner of the workspace, so this is not required..
//			if (!proj.getProject_status().equalsIgnoreCase("A")) {
//				if (!ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
//					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
//					out.println("<tr>");
//					out.println("<td headers=\"col11\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"request_transfer\" id=\"label_rtransfer\" /><label for=\"label_rtransfer\">Request transfer of ownership</label></td>");
//					out.println("</tr>");
//					out.println("</table>");
//				}
//			}

			if (!proj.getProject_status().equalsIgnoreCase("A")) {
				// display only to super workspace admin
				// also display to workspace owner.. changed in 5.2.1 release...
				//if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					out.println("<td headers=\"col12\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"transfer\" id=\"label_transfer\" /><label for=\"label_transfer\">Change workspace owner for this workspace</label></td>");
					out.println("</tr>");
					out.println("</table>");
				//}
			}

			boolean bIsSubWorkspace = ETSWorkspaceDAO.isSubWorkspace(con,proj.getParent_id());

			if (!proj.getProject_status().equalsIgnoreCase("A")) {
				// display only to proposal.
				if (proj.getProjectOrProposal().trim().equalsIgnoreCase("O") && bIsSubWorkspace == false) {
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					out.println("<td headers=\"col13\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"promote\" id=\"label_promote\" /><label for=\"label_promote\">Promote this proposal to project</label></td>");
					out.println("</tr>");
					out.println("</table>");
				}
			}
			
			String isProjectIBMOnly = AICWorkspaceDAO.isProjectIBMOnly(proj.getProjectId(),con);
			
//			if (isProjectIBMOnly.equalsIgnoreCase("Y")){
//				if (!proj.getIsPrivate().equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PUBLIC)){
//				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
//				out.println("<tr>");
//				out.println("<td headers=\"col14\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"promote_external\" id=\"label_external\" /><label for=\"label_external\">Promote workspace access for External users also</label></td>");
//				out.println("</tr>");
//				out.println("</table>");
//			  }
//			}
			// not archived and not client care...
			// Removed in 5.2.1 as all internal users can create workspace or sub workspace now...
//			if (!proj.getProject_status().equalsIgnoreCase("A") && !proj.getProjectOrProposal().equalsIgnoreCase("C")) {
//				// not sub workspace..
//				if (bIsSubWorkspace == false) {
//					if (!ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
//						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
//						out.println("<tr>");
//						out.println("<td headers=\"col14\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"request_subworkspace\" id=\"label_rworkspace\" /><label for=\"label_rworkspace\">Request to create a new sub-workspace</label></td>");
//						out.println("</tr>");
//						out.println("</table>");
//					}
//				}
//			}

			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col15\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
			out.println("<td headers=\"col16\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col17\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col18\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("</form>");

		} else {

			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col19\" align=\"left\"><b><span style=\"color:#ff3333\">You are not authorized to access this functionality.</span></b></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col20\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
			out.println("</tr>");
			out.println("</table>");

		}

	}

	/**
	 *
	 */
	private void promoteWorkspaceToProject() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col21\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"promote-confirm\" />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col22\" align=\"left\" >You have selected the option to change this proposal workspace to a project. Please select the options below and click on <b>Continue</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");

		printGreyDottedLine(out);
		out.println("<br />");


		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col23\" align=\"left\" >Please select the tabs that you wish to be carried over to the project workspace.</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col24\" align=\"left\" ><span style=\"color:#ff3333\"><b>Note: If you do not select a tab that exists currently, the data related to the tab will be deleted permanently and cannot be retrieved back.</b></span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		int iCount = ETSWorkspaceDAO.checkSubWorkspaces(con,proj.getProjectId());

		if (iCount > 0) {

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col25\" align=\"left\" ><span style=\"color:#ff3333\"><b>Note: This workspace has sub-workspaces. The sub-workspaces will be deleted once this proposal is promoted to a project.</b></span></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

		}

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col26\" align=\"left\" width=\"130\"><b><label for=\"label_name\">Workspace name:</label></b></td>");
		out.println("<td headers=\"col27\" align=\"left\" ><input type=\"text\" name=\"workspace_name\" class=\"iform\" id=\"label_name\" value=\"" + proj.getName() + "\" style=\"width:300px\" width=\"300px\" maxlength=\"100\" /></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		boolean bDocuments = false;
		boolean bIssues = false;

		// display the graph tabs for selection along with the project related tabs.

		Vector graphTabs = ETSDatabaseManager.getTopCats(proj.getProjectId());


		for (int i = 0; i < graphTabs.size(); i++) {

			ETSCat cat = (ETSCat) graphTabs.elementAt(i);

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			if (cat.getViewType() == Defines.MAIN_VT) {
				out.println("<tr>");
				out.println("<td headers=\"col28\" align=\"left\" valign=\"top\" width=\"130\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"col29\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Main\" border=\"0\" /></td>");
				out.println("<td headers=\"col30\" align=\"left\" ><b>Main</b><input type=\"hidden\" name=\"tab_main\" value=\"Y\" /></td>");
				out.println("</tr>");
				// display meetings by default
				out.println("<tr>");
				out.println("<td headers=\"col31\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"col32\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meetings\" /></td>");
				out.println("<td headers=\"col33\" align=\"left\" ><b><label for=\"label_meetings\">Meetings</label></b></td>");
				out.println("</tr>");

			} else if (cat.getViewType() == Defines.CONTRACTS_VT) {
				out.println("<tr>");
				out.println("<td headers=\"col34\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"col35\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_contracts\" value=\"Y\" id=\"label_contract\" /></td>");
				out.println("<td headers=\"col36\" align=\"left\" ><b><label for=\"label_contract\">Contracts</label></b></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.DOCUMENTS_VT) {
				bDocuments = true;
				out.println("<tr>");
				out.println("<td headers=\"col37\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"col38\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Documents\" border=\"0\" /></td>");
				out.println("<td headers=\"col39\" align=\"left\" ><b>&nbsp;Documents</b><input type=\"hidden\" name=\"tab_documents\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.ISSUES_CHANGES_VT) {
				bIssues = true;
				out.println("<tr>");
				out.println("<td headers=\"col40\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"col41\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_issues\" /></td>");
				out.println("<td headers=\"col42\" align=\"left\" ><b><label for=\"label_issues\">Issues/Changes</label></b></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.TEAM_VT) {
				out.println("<tr>");
				out.println("<td headers=\"col43\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"col44\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"col45\" align=\"left\" ><b>&nbsp;Team</b><input type=\"hidden\" name=\"tab_team\" value=\"Y\" /></td>");
				out.println("</tr>");
			}
			out.println("</table>");
		}

		if (!bDocuments) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col46\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
			out.println("<td headers=\"col47\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_documents\" /></td>");
			out.println("<td headers=\"col48\" align=\"left\" ><b><label for=\"label_documents\">Documents</label></b></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		if (!bIssues) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col49\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
			out.println("<td headers=\"col50\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_issues\" /></td>");
			out.println("<td headers=\"col51\" align=\"left\" ><b><label for=\"label_issues\">Issues/Changes</label></b></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col52\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void promoteWorkspaceToProjectConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		String sWorkspaceName = request.getParameter("workspace_name");

		String sTabMain = request.getParameter("tab_main");
		if (sTabMain == null || sTabMain.trim().equalsIgnoreCase("")) {
			sTabMain = "N";
		} else {
			sTabMain = sTabMain.trim();
		}
		String sTabMeetings = request.getParameter("tab_meetings");
		if (sTabMeetings == null || sTabMeetings.trim().equalsIgnoreCase("")) {
			sTabMeetings = "N";
		} else {
			sTabMeetings = sTabMeetings.trim();
		}
		String sTabContracts = request.getParameter("tab_contracts");
		if (sTabContracts == null || sTabContracts.trim().equalsIgnoreCase("")) {
			sTabContracts = "N";
		} else {
			sTabContracts = sTabContracts.trim();
		}
		String sTabDocuments = request.getParameter("tab_documents");
		if (sTabDocuments == null || sTabDocuments.trim().equalsIgnoreCase("")) {
			sTabDocuments = "N";
		} else {
			sTabDocuments = sTabDocuments.trim();
		}
		String sTabIssues = request.getParameter("tab_issues");
		if (sTabIssues == null || sTabIssues.trim().equalsIgnoreCase("")) {
			sTabIssues = "N";
		} else {
			sTabIssues = sTabIssues.trim();
		}
		String sTabTeam = request.getParameter("tab_team");
		if (sTabTeam == null || sTabTeam.trim().equalsIgnoreCase("")) {
			sTabTeam = "N";
		} else {
			sTabTeam = sTabTeam.trim();
		}

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col53\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"promote-confirm2\" />");
		out.println("<input type=\"hidden\" name=\"workspace_name\" value=\"" + sWorkspaceName + "\" />");
		out.println("<input type=\"hidden\" name=\"tab_main\" value=\"" + sTabMain + "\" />");
		out.println("<input type=\"hidden\" name=\"tab_meetings\" value=\"" + sTabMeetings + "\" />");
		out.println("<input type=\"hidden\" name=\"tab_contracts\" value=\"" + sTabContracts + "\" />");
		out.println("<input type=\"hidden\" name=\"tab_documents\" value=\"" + sTabDocuments + "\" />");
		out.println("<input type=\"hidden\" name=\"tab_issues\" value=\"" + sTabIssues + "\" />");
		out.println("<input type=\"hidden\" name=\"tab_team\" value=\"" + sTabTeam + "\" />");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col54\" align=\"left\" ><b>Please review your changes and click on <b>Submit</b>.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col55\" align=\"left\" width=\"130\" ><b>Workspace name:</b></td>");
		out.println("<td headers=\"col56\" align=\"left\"  >" + sWorkspaceName + "</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col57\" align=\"left\" width=\"130\" ><b>Workspace tabs:</b></td>");
		out.println("<td headers=\"col58\" align=\"left\"  ><b>Main</b></td>");
		out.println("</tr>");
		out.println("</table>");

		if (sTabMeetings.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col59\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"col60\" align=\"left\"  ><b>Meetings</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		if (sTabContracts.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col61\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"col62\" align=\"left\"  ><b>Contracts</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		if (sTabDocuments.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col63\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"col64\" align=\"left\"  ><b>Documents</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		if (sTabIssues.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col65\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"col66\" align=\"left\"  ><b>Issues/Changes</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		if (sTabTeam.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col67\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"col68\" align=\"left\"  ><b>Team</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}


		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col69\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void promoteWorkspaceToProjectFinal() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		String sWorkspaceName = request.getParameter("workspace_name");

		String sTabMain = request.getParameter("tab_main");
		if (sTabMain == null || sTabMain.trim().equalsIgnoreCase("")) {
			sTabMain = "N";
		} else {
			sTabMain = sTabMain.trim();
		}
		String sTabMeetings = request.getParameter("tab_meetings");
		if (sTabMeetings == null || sTabMeetings.trim().equalsIgnoreCase("")) {
			sTabMeetings = "N";
		} else {
			sTabMeetings = sTabMeetings.trim();
		}
		String sTabContracts = request.getParameter("tab_contracts");
		if (sTabContracts == null || sTabContracts.trim().equalsIgnoreCase("")) {
			sTabContracts = "N";
		} else {
			sTabContracts = sTabContracts.trim();
		}
		String sTabDocuments = request.getParameter("tab_documents");
		if (sTabDocuments == null || sTabDocuments.trim().equalsIgnoreCase("")) {
			sTabDocuments = "N";
		} else {
			sTabDocuments = sTabDocuments.trim();
		}
		String sTabIssues = request.getParameter("tab_issues");
		if (sTabIssues == null || sTabIssues.trim().equalsIgnoreCase("")) {
			sTabIssues = "N";
		} else {
			sTabIssues = sTabIssues.trim();
		}
		String sTabTeam = request.getParameter("tab_team");
		if (sTabTeam == null || sTabTeam.trim().equalsIgnoreCase("")) {
			sTabTeam = "N";
		} else {
			sTabTeam = sTabTeam.trim();
		}

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col70\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"transfer-confirm2\" />");


		printGreyDottedLine(out);
		out.println("<br />");

		/**
		 * 1. Change the proposal to project
		 * 2. Get already available tabs and check against the selected tabs
		 * 3. Create new ones not available already
		 * 4. Delete tabs that are not selected
		 * 5. send email to all members informing the change.
		 *
		 */

		try {

			// get the graph tabs for this proposal.
			Vector graphTabs = ETSDatabaseManager.getTopCats(proj.getProjectId());

			con.setAutoCommit(false);

			int iOrderType = 20;		// 10 is for main which is already there.

			if (sTabMeetings.trim().equalsIgnoreCase("Y")) {
				// meetings tab has been selected.
				// check if meetings already exists
				boolean bExists = false;
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.MEETINGS_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						bExists = true;
						iOrderType = iOrderType + 10;
						break;
					}
				}

				if (!bExists) {
					// create this tab.
					ETSWorkspaceDAO.createTab(con,proj.getProjectId(),Defines.MEETINGS_VT,iOrderType,es.gIR_USERN);
					iOrderType = iOrderType + 10;
				}
			} else {
				// check if meetings already exists
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.MEETINGS_VT) {
						// delete the tab...
						ETSWorkspaceDAO.deleteTab(con,proj.getProjectId(),cat.getId());
						// delete meeting entries also...
						ETSWorkspaceDAO.deleteMeetingsForProject(con,proj.getProjectId());
						break;
					}
				}
			}

			if (sTabContracts.trim().equalsIgnoreCase("Y")) {
				// Contracts tab has been selected.
				// check if contracts already exists
				boolean bExists = false;
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.CONTRACTS_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						bExists = true;
						iOrderType = iOrderType + 10;
						break;
					}
				}

				if (!bExists) {
					// create this tab.
					ETSWorkspaceDAO.createTab(con,proj.getProjectId(),Defines.CONTRACTS_VT,iOrderType,es.gIR_USERN);
					iOrderType = iOrderType + 10;
				}
			} else {
				// check if contracts already exists
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.CONTRACTS_VT) {
						// delete the tab...
						ETSWorkspaceDAO.deleteTab(con,proj.getProjectId(),cat.getId());
						ETSWorkspaceDAO.deleteContractsForProject(con,proj.getProjectId());
						break;
					}
				}
			}

			if (sTabDocuments.trim().equalsIgnoreCase("Y")) {
				// documents tab has been selected.
				// check if documents already exists
				boolean bExists = false;
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.DOCUMENTS_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						bExists = true;
						iOrderType = iOrderType + 10;
					}
				}
				if (!bExists) {
					// create this tab.
					ETSWorkspaceDAO.createTab(con,proj.getProjectId(),Defines.DOCUMENTS_VT,iOrderType,es.gIR_USERN);
					iOrderType = iOrderType + 10;
				}

			} else {
				// check if documents already exists
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.DOCUMENTS_VT) {
						// delete the tab...
						ETSWorkspaceDAO.deleteTab(con,proj.getProjectId(),cat.getId());
						break;
					}
				}
			}

			if (sTabIssues.trim().equalsIgnoreCase("Y")) {
				// issues tab has been selected.
				// check if issues already exists
				boolean bExists = false;
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.ISSUES_CHANGES_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						bExists = true;
						iOrderType = iOrderType + 10;
					}
				}
				if (!bExists) {
					// create this tab.
					ETSWorkspaceDAO.createTab(con,proj.getProjectId(),Defines.ISSUES_CHANGES_VT,iOrderType,es.gIR_USERN);
					iOrderType = iOrderType + 10;
				}

			} else {
				// check if issues already exists
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.ISSUES_CHANGES_VT) {
						// delete the tab...
						ETSWorkspaceDAO.deleteTab(con,proj.getProjectId(),cat.getId());
						break;
					}
				}
			}


			if (sTabTeam.trim().equalsIgnoreCase("Y")) {
				// team tab has been selected.
				// check if team already exists
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.ISSUES_CHANGES_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						iOrderType = iOrderType + 10;
					}
				}
			}


			// update the project name and the project type...

			ETSWorkspaceDAO.updateProposalToProject(con,proj.getProjectId(),sWorkspaceName);

			// check if this proposal has sub workspaces..if so, mark them for delete..
			int iCount = ETSWorkspaceDAO.checkSubWorkspaces(con,proj.getProjectId());

			if (iCount > 0) {
				ETSWorkspaceDAO.deleteSubWorkspaces(con,proj.getProjectId());
			}

			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			con.rollback();
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}

		// send email notification about the promotion

		boolean bSent = sendPromoteWorkspaceNotificationEmail(con,proj,sWorkspaceName,es);

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col71\" align=\"left\" ><b>The proposal workspace has been promoted to project successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");
		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col72\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void archiveWorkspace() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col73\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"archive-confirm\" />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col74\" align=\"left\" >You have selected the option to archive this workspace. Please read the details below and click on <b>Submit</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col75\" align=\"left\" ><b>Please note that the following happens when a workspace gets archived</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"col76\" align=\"left\" >");
		out.println("<ol>");
		out.println("<li>All members will not be able to perform any operations on this workspace.</li>");
		out.println("<li>You will not be able to make this workspace active again.</li>");
		out.println("</ol>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col77\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"col78\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col79\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col80\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void archiveWorkspaceConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		try {

			con.setAutoCommit(false);

			boolean bSuccess = ETSWorkspaceDAO.updateWorkspaceStatus(con,proj.getProjectId(),Defines.WORKSPACE_ARCHIVE);

			if (bSuccess) {
				bSuccess = ETSWorkspaceDAO.archiveWorkspaceRoles(con,proj.getProjectId());
			}

			con.commit();
			con.setAutoCommit(true);
			
			ETSSendWorkspaceEvent wrkspcEvent = new ETSSendWorkspaceEvent();
			wrkspcEvent.sendWrkspaceEvent(con,proj.getProjectId(),"Archive Workspace Event");


		} catch (SQLException e) {
			con.rollback();
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}

		boolean bSent = sendArchiveWorkspaceNotificationEmail(con,proj,es);

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col81\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"archive-confirm\" />");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col82\" align=\"left\" ><b>This workspace has been archived successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col83\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void deleteWorkspace() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col84\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"delete-confirm\" />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col85\" align=\"left\" >You have selected the option to delete this workspace. Please read the details below and click on <b>Submit</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col86\" align=\"left\" ><b>Please note that the following happens when a workspace gets deleted</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"col87\" align=\"left\" >");
		out.println("<ol>");
		out.println("<li>This workspace will not be available to access to any user.</li>");
		out.println("<li>You will not be able to retrieve any documents from this workspace.</li>");
		out.println("</ol>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		int iCount = ETSWorkspaceDAO.checkSubWorkspaces(con,proj.getProjectId());

		if (iCount > 0) {

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col88\" align=\"left\" ><span style=\"color:#ff3333\"><b>Note: This workspace has sub-workspaces. The sub-workspaces will also be deleted once this workspace is deleted.</b></span></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

		}

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col89\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"col90\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col91\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col92\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void deleteWorkspaceConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		try {

			con.setAutoCommit(false);

			boolean bSuccess = ETSWorkspaceDAO.updateWorkspaceStatus(con,proj.getProjectId(),Defines.WORKSPACE_DELETE);

			// check if this workspace has sub workspaces..if so, mark them for delete..
			int iCount = ETSWorkspaceDAO.checkSubWorkspaces(con,proj.getProjectId());

			if (iCount > 0) {
				ETSWorkspaceDAO.deleteSubWorkspaces(con,proj.getProjectId());
			}

			con.commit();
			con.setAutoCommit(true);
			
			ETSSendWorkspaceEvent wrkspcEvent = new ETSSendWorkspaceEvent();
			wrkspcEvent.sendWrkspaceEvent(con,proj.getProjectId(),"Delete Workspace Event");


		} catch (SQLException e) {
			con.rollback();
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}

		// notify users?
		boolean bSent = sendDeleteWorkspaceNotificationEmail(con,proj,es);

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col93\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");


		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"delete-confirm\" />");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col94\" align=\"left\" ><b>This workspace has been deleted.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col95\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "AICConnectServlet.wss?linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void requestNewWorkspaceOwner() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col96\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"request_transfer-confirm\" />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col97\" align=\"left\" >You have selected to request to have a new workspace owner. Once this new owner is assigned, your role in the workspace will be reassigned. Please select the new workspace owner and click on <b>Submit</b> to send a request to change workspace owner for this workspace.<br /><br /></td>");
		out.println("</tr>");
		out.println("</table>");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col98\" align=\"left\" width=\"130\"><b><label for=\"label_owner\">Workspace owner:</label></b></td>");
		out.println("<td headers=\"col99\" align=\"left\" >");

		Vector vMembers = ETSWorkspaceDAO.getInternalUsersInWorkspace(con,proj.getProjectId());

		if (vMembers != null && vMembers.size() > 0) {

			out.println("<select name=\"workspace_owner\" class=\"iform\" id=\"label_owner\" style=\"width:300px\" width=\"300px\" >");

			out.println("<option value=\"\">Select a new workspace owner</option>");

			for (int i = 0; i < vMembers.size(); i++) {

				ETSUser user = (ETSUser) vMembers.elementAt(i);

				out.println("<option value=\"" + user.getUserId() + "\">" + user.getUserName() + " [ID:" + user.getUserId() + "]</option>");
			}

			out.println("</select>");

		}

		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col100\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"col101\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col102\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col103\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void requestNewWorkspaceOwnerConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		String sWorkspaceOwner = request.getParameter("workspace_owner");

		boolean bSent = sendTransferRequestNotificationEmail(con,proj, sWorkspaceOwner,es);

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col104\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");


		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"request_transfer-confirm\" />");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col105\" align=\"left\" ><b>Your request to change workspace owner has been submitted successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col106\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void transferWorkspaceOwner(String sError) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		try {

			String sWorkspaceOwner = ETSUtils.checkNull(request.getParameter("workspace_owner"));
			String sRoleStr = ETSUtils.checkNull(request.getParameter("roles"));


			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"col107\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			if (!sError.trim().equalsIgnoreCase("")) {

				out.println("<br />");

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"col108\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			}

			out.println("<br />");

			out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

			out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
			out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
			out.println("<input type=\"hidden\" name=\"manage\" value=\"transfer-confirm\" />");

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col109\" align=\"left\" >You have selected to change the workspace owner for this workspace. Once this new owner is assigned, the old workspace owner has to be reassigned to a different role. Please select the new workspace owner and click on <b>Continue</b> to send a request to change workspace owner for this workspace.<br /><br /></td>");
			out.println("</tr>");
			out.println("</table>");

			printGreyDottedLine(out);
			out.println("<br />");

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col110\" align=\"left\" width=\"130\"><b><label for=\"label_owner\">New workspace owner:</label></b></td>");
			out.println("<td headers=\"col111\" align=\"left\" >");

			// get the current workspace owner and dont display him in the list...

			Vector vOwners = ETSDatabaseManager.getUsersByProjectPriv(proj.getProjectId(),Defines.OWNER,con);
			
			boolean bTeamroomWrkspc = false;
			if (proj.getIsPrivate().equals(Defines.AIC_IS_PRIVATE_TEAMROOM) || proj.getIsPrivate().equals(Defines.AIC_IS_RESTRICTED_TEAMROOM)) {
				bTeamroomWrkspc = true;
			}
			Vector vMembers = new Vector();
			if (bTeamroomWrkspc) {
				vMembers = AICWorkspaceDAO.getAllProjectAdmins(proj.getProjectId(),con);
			} else {
				vMembers = ETSWorkspaceDAO.getInternalUsersInWorkspace(con,proj.getProjectId());
			}

			if (vMembers != null && vMembers.size() > 0) {

				out.println("<select name=\"workspace_owner\" class=\"iform\" id=\"label_owner\" style=\"width:300px\" width=\"300px\" >");

				out.println("<option value=\"\">Select a new workspace owner</option>");

				for (int i = 0; i < vMembers.size(); i++) {

					ETSUser user = (ETSUser) vMembers.elementAt(i);

					boolean bOwner = false;

					if (vOwners != null && vOwners.size() > 0) {

						for (int j = 0; j < vOwners.size(); j++) {
							ETSUser owner = (ETSUser) vOwners.elementAt(j);
							if (owner.getUserId().trim().equalsIgnoreCase(user.getUserId().trim())) {
								bOwner = true;
								break;
							}
						}
					}

					if (!bOwner) {
						if (sWorkspaceOwner.trim().equalsIgnoreCase(user.getUserId().trim())) {
							out.println("<option value=\"" + user.getUserId() + "\" selected=\"selected\">" + user.getUserName() + " [ID:" + user.getUserId() + "]</option>");
						} else {
							out.println("<option value=\"" + user.getUserId() + "\">" + user.getUserName() + " [ID:" + user.getUserId() + "]</option>");
						}

					}
				}

				out.println("</select>");

			}

			out.println("</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("</table>");


			out.println("<br />");
			out.println("<br />");

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col112\" align=\"left\" >Select the role the current workspace owner gets assigned once the new workspace owner is assigned.</td>");
			out.println("</tr>");
			out.println("</table>");

			printGreyDottedLine(out);
			out.println("<br />");

			Vector r = ETSDatabaseManager.getRolesPrivs(proj.getProjectId(),con);
			ETSProjectInfoBean projBean = ETSUtils.getProjInfoBean(con);
			out.println("<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">");


			for (int i = 0; i<r.size(); i++){
				String[] rp = (String[])r.elementAt(i);
				int roleid = (new Integer(rp[0])).intValue();
				String rolename = rp[1];
				//String privs = rp[2];
				String privids = rp[3];

				if (!(ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.OWNER,con)) && !bTeamroomWrkspc){
					out.println("<tr>");
					int iRole = -1;
					if (!sRoleStr.trim().equalsIgnoreCase("")) {
						iRole = Integer.parseInt(sRoleStr);
					}
					if (iRole == roleid) {
						out.println("<td headers=\"col113\" align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_"+i+"\" type=\"radio\" name=\"roles\" value=\""+roleid+"\" checked=\"checked\" /></td>");
					} else {
						out.println("<td headers=\"col114\" align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_"+i+"\" type=\"radio\" name=\"roles\" value=\""+roleid+"\" /></td>");
					}

					out.println("<td headers=\"col115\" align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"role_"+i+"\">"+rolename+"</label></td>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td>&nbsp;</td>");
					//out.println("<td headers=\"col116\" align=\"left\" valign=\"top\">Privileges: "+privs+"</td>");
					out.println("<td headers=\"col117\" align=\"left\" valign=\"top\">Privileges: ");
					String priv_desc = "";
					StringTokenizer st = new StringTokenizer(privids, ",");
					Vector privs = new Vector();
					while (st.hasMoreTokens()){
						String priv = st.nextToken();
						privs.addElement(priv);
					}
					for (int j = 0; j < privs.size(); j++){
						String s = (String)privs.elementAt(j);
						String  desc = projBean.getInfoDescription("PRIV_"+s,0);
						if (!desc.equals("")){
							if(!priv_desc.equals("")){
								priv_desc = priv_desc+"; "+desc;
							}
							else{
								priv_desc = desc;
							}
						}
					}
					out.println(priv_desc);
					out.println("</td>");
					out.println("</tr>");
					out.println("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
				} else if ((bTeamroomWrkspc) && !(ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.OWNER,con))
						&& (ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.ADMIN,con))) {
					out.println("<tr>");
					int iRole = -1;
					if (!sRoleStr.trim().equalsIgnoreCase("")) {
						iRole = Integer.parseInt(sRoleStr);
					}
					if (iRole == roleid) {
						out.println("<td headers=\"col113\" align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_"+i+"\" type=\"radio\" name=\"roles\" value=\""+roleid+"\" checked=\"checked\" /></td>");
					} else {
						out.println("<td headers=\"col114\" align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_"+i+"\" type=\"radio\" name=\"roles\" value=\""+roleid+"\" /></td>");
					}

					out.println("<td headers=\"col115\" align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"role_"+i+"\">"+rolename+"</label></td>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td>&nbsp;</td>");
					//out.println("<td headers=\"col116\" align=\"left\" valign=\"top\">Privileges: "+privs+"</td>");
					out.println("<td headers=\"col117\" align=\"left\" valign=\"top\">Privileges: ");
					String priv_desc = "";
					StringTokenizer st = new StringTokenizer(privids, ",");
					Vector privs = new Vector();
					while (st.hasMoreTokens()){
						String priv = st.nextToken();
						privs.addElement(priv);
					}
					for (int j = 0; j < privs.size(); j++){
						String s = (String)privs.elementAt(j);
						String  desc = projBean.getInfoDescription("PRIV_"+s,0);
						if (!desc.equals("")){
							if(!priv_desc.equals("")){
								priv_desc = priv_desc+"; "+desc;
							}
							else{
								priv_desc = desc;
							}
						}
					}
					out.println(priv_desc);
					out.println("</td>");
					out.println("</tr>");
					out.println("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
					
				}
			}

			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col119\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
			out.println("<td headers=\"col120\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col121\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col122\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("</form>");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 *
	 */
	private void transferWorkspaceOwnerConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		String sWorkspaceOwner = request.getParameter("workspace_owner");
		String sRoleStr = request.getParameter("roles");
		int roleid = (new Integer(sRoleStr)).intValue();



		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col123\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"transfer-confirm2\" />");
		out.println("<input type=\"hidden\" name=\"workspace_owner\" value=\"" + sWorkspaceOwner + "\" />");
		out.println("<input type=\"hidden\" name=\"roles\" value=\"" + sRoleStr + "\" />");


		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col124\" align=\"left\" ><b>Please review your changes and click on <b>Submit</b>.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col125\" align=\"left\" width=\"130\" ><b>New workspace owner:</b></td>");
		out.println("<td headers=\"col126\" align=\"left\"  >" + ETSUtils.getUsersName(con,sWorkspaceOwner) + "</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col127\" align=\"left\" width=\"130\" ><b>Role to old workspace owner:</b></td>");
		out.println("<td headers=\"col128\" align=\"left\"  >" + ETSDatabaseManager.getRoleName(con,Integer.parseInt(sRoleStr)) + "</td>");
		out.println("</tr>");
		out.println("</table>");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col129\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"col130\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col131\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col132\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private String validateTransferWorkspaceOwner() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		StringBuffer sError = new StringBuffer("");

		String sWorkspaceOwner = ETSUtils.checkNull(request.getParameter("workspace_owner"));
		String sRoleStr = ETSUtils.checkNull(request.getParameter("roles"));

		if (sWorkspaceOwner.trim().equalsIgnoreCase("")) {
			 sError.append("You have to select the new workspace owner. <br />");
		}

		if (sRoleStr.trim().equalsIgnoreCase("")) {
			 sError.append("You have to select the role for the current workspace owner. <br />");
		}


		return sError.toString();

	}

	/**
	 *
	 */
	private void transferWorkspaceOwnerFinal() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		boolean ownerFlag = true;
		int iWorkspaceOwnerRoleID = 0;

		try {



			String sWorkspaceOwner = request.getParameter("workspace_owner");
			String sRoleStr = request.getParameter("roles");
			int roleid = (new Integer(sRoleStr)).intValue();
			String isProjectIBMOnly = AICWorkspaceDAO.isProjectIBMOnly(proj.getProjectId(),con);

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"col133\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

			out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
			out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
			out.println("<input type=\"hidden\" name=\"manage\" value=\"transfer-confirm2\" />");
			out.println("<input type=\"hidden\" name=\"workspace_owner\" value=\"" + sWorkspaceOwner + "\" />");
			out.println("<input type=\"hidden\" name=\"roles\" value=\"" + sRoleStr + "\" />");


			printGreyDottedLine(out);
			out.println("<br />");

			/**
			 * 1. Get the workspace owner role id for this workspace.
			 * 2. Assign the new owner to this role id
			 * 3. Assign the old workspace owner to the role selected on the screen
			 * 4. send email to the old and new workspace owner informing the change.
			 * 5. check if the new workspace owner has MULTIPOC entitlement for external customers workspace.
			 * 6. if no, then send a request to decaf...
			 * 7. Make new workspace owner as primary contact for the Team room workspace
			 */

			Vector owners = ETSDatabaseManager.getUsersByProjectPriv(proj.getProjectId(), Defines.OWNER, con);
			ETSUser own =  new ETSUser();
			if (owners != null && owners.size() >0) {
				own = (ETSUser) owners.elementAt(0);
			}else{
				ownerFlag = false;
				String query ="select  distinct role_id from ets.ets_roles where PROJECT_ID = '"+proj.getProjectId()+"' and role_name ='Workspace Owner'";
				Statement stmt = con.createStatement();
				ResultSet resultSet = stmt.executeQuery(query);
				while (resultSet.next()){
					iWorkspaceOwnerRoleID = Integer.parseInt(resultSet.getString("role_id"));
				}
			 stmt = null;
			 resultSet = null;
			
			}

			con.setAutoCommit(false);

			// get the workspace owner role id
			if(iWorkspaceOwnerRoleID == 0)
			 iWorkspaceOwnerRoleID = ETSWorkspaceDAO.getWorkspaceOwnerRoleID(con,proj.getProjectId(),own.getUserId());

			// assign the new workspace owner
			boolean bAssigned = ETSWorkspaceDAO.assignNewWorkspaceOwner(con,proj.getProjectId(),sWorkspaceOwner,iWorkspaceOwnerRoleID);

			if (bAssigned  && ownerFlag == true) {
				// assign the old workspace owner to the newly selected role.
				boolean bSuccess  = ETSWorkspaceDAO.assignRoleToOldWorkspaceOwner(con,proj.getProjectId(),own.getUserId(),roleid);
			}
			
			//mark the WO as primary contact
			if (proj.getIsPrivate().equals(Defines.AIC_IS_PRIVATE_TEAMROOM) || proj.getIsPrivate().equals(Defines.AIC_IS_RESTRICTED_TEAMROOM)) {
				AICWorkspaceDAO.editPrimaryContact(proj.getProjectId(),sWorkspaceOwner,own.getUserId(),con);
			}
			con.commit();
			con.setAutoCommit(true);

			boolean bPOCEntRequested = false;

			// check if the new workspace entered has MULTIPOC entitlement- for external user projects,
			// if not request for one.
			if (isProjectIBMOnly.equalsIgnoreCase("N")) {
				boolean bHasPOCEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sWorkspaceOwner,Defines.MULTIPOC);
				if (!bHasPOCEnt ) {

					ETSUserAccessRequest uar = new ETSUserAccessRequest();
					uar.setTmIrId(sWorkspaceOwner);
					uar.setPmIrId(es.gIR_USERN);
					uar.setDecafEntitlement(Defines.MULTIPOC);
					uar.setIsAProject(false);
					uar.setUserCompany("");
					ETSStatus status = uar.request(con);

					if (status.getErrCode() == 0) {
						bPOCEntRequested = true;
					} else {
						bPOCEntRequested  = false;
					}
				}
			}
			// send email notification to the new owner

			boolean bSent = sendChangeNewOwnerNotificationEmail(con,proj,own.getUserId(),sWorkspaceOwner,es);

			// send email notification to the old owner..
			if(ownerFlag == true){
				boolean bSent1 = sendChangeOldOwnerNotificationEmail(con,proj,own.getUserId(),sWorkspaceOwner,es);
			}
			if (bPOCEntRequested) {
				sendPOCEmail(con,proj,es,sWorkspaceOwner);
			}

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col134\" align=\"left\" ><b>New workspace owner has been assigned successfully. An email notification also has been send to the owner regarding the change.</b></td>");
			out.println("</tr>");
			if (bPOCEntRequested) {
				out.println("<tr>");
				out.println("<td headers=\"col135\" align=\"left\" ><b>Additionally, a customer Point of Contact (POC) entitlement request has been sent to the new workspace owner's manager for approval.</b></td>");
				out.println("</tr>");
			}
			out.println("</table>");

			out.println("<br />");
			printGreyDottedLine(out);

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col1136\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("</form>");

		} catch (SQLException e) {
			con.rollback();
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}


	}

	/**
	 *
	 */
	private void requestSubWorkspace() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		UnbrandedProperties prop = this.params.getUnbrandedProperties();

		try {

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"col137\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

			out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
			out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
			out.println("<input type=\"hidden\" name=\"manage\" value=\"request_subworkspace-confirm\" />");

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col138\" align=\"left\" >You can request to add a sub-workspace under this current workspace. Simply submit the name you would like for your sub-workspace and an email will be sent to an " + prop.getAppName() + " administrator alerting them of your request.<br /><br /></td>");
			out.println("</tr>");
			out.println("</table>");

			printGreyDottedLine(out);
			out.println("<br />");

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"col139\" align=\"left\" width=\"150\"><b><label for=\"label_sub\">Sub-workspace name:</label></b></td>");
			out.println("<td headers=\"col140\" align=\"left\" ><input type=\"text\" name=\"subworkspace_name\" class=\"iform\" id=\"label_subWS\" value=\"\" style=\"width:250px\" width=\"250px\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"col141\" colspan=\"2\" >&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"col142\" headers=\"\" align=\"left\" width=\"150\"><b><label for=\"label_owner\">Workspace owner:</label></b></td>");
			out.println("<td headers=\"col143\" align=\"left\" >");

			Vector vMembers = ETSWorkspaceDAO.getInternalUsersInWorkspace(con,proj.getProjectId());

			if (vMembers != null && vMembers.size() > 0) {

				out.println("<select name=\"workspace_owner\" class=\"iform\" id=\"label_owner\" style=\"width:255px\" width=\"255px\" >");

				out.println("<option value=\"\">Select a new workspace owner</option>");

				for (int i = 0; i < vMembers.size(); i++) {

					ETSUser user = (ETSUser) vMembers.elementAt(i);

					out.println("<option value=\"" + user.getUserId() + "\">" + user.getUserName() + " [ID:" + user.getUserId() + "]</option>");
				}

				out.println("</select>");

			}

			out.println("</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"col144\" colspan=\"2\" >&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"col145\" align=\"left\" width=\"150\" valign=\"top\"><b><label for=\"label_desc\">Description:</label></b></td>");
			out.println("<td headers=\"col146\" align=\"left\" ><textarea name=\"description\" id=\"label_desc\" cols=\"39\" rows=\"5\"  class=\"iform\" maxlength=\"1000\"></textarea></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			printGreyDottedLine(out);
			out.println("<br />");
			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col147\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" border=\"0\" /></td>");
			out.println("<td headers=\"col148\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col149\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col150\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("</form>");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

	}



	/**
	 *
	 */
	private void requestSubWorkspaceConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		String sSubWorkspaceName = request.getParameter("subworkspace_name");
		String sWorkspaceOwner = request.getParameter("workspace_owner");
		String sDescription = request.getParameter("description");

		boolean bSent = sendSubWorkspaceRequestNotificationEmail(con,proj,sWorkspaceOwner,sSubWorkspaceName,sDescription,es);


		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col151\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"delete-confirm\" />");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col152\" align=\"left\" ><b>Your request for creation of sub-workspace has been sent successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col153\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	private static void printGreyDottedLine(PrintWriter out) {

		out.println("<!-- Gray dotted line -->");
		out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col154\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("<td headers=\"col155\" background=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		out.println("<td headers=\"col156\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<!-- End Gray dotted line -->");


	}

	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendTransferRequestNotificationEmail(Connection con, ETSProj proj, String sNewWorkspaceOwner, EdgeAccessCntrl es) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties unBrandprop = PropertyFactory.getProperty(proj.getProjectType());

			String sEmailSubject = unBrandprop.getAppName() + " - Request transfer of workspace owner";

			sEmailStr.append("A request to transfer ownership of a workspace has been generated.\n\n");

			sEmailStr.append("The details of the transfer are as follows: \n");

			sEmailStr.append("===============================================================\n");

			sEmailStr.append("  From:           " + ETSUtils.getUsersName(con,es.gIR_USERN) + "\n");
			sEmailStr.append("  Workspace:      " + proj.getName() + "\n");
			sEmailStr.append("  New owner:      " + ETSUtils.getUsersName(con,sNewWorkspaceOwner) + " [ID: " + sNewWorkspaceOwner.trim() + "]\n\n");

			sEmailStr.append("To setup the new workspace owner above, please do the following steps:\n");
			sEmailStr.append("===============================================================\n\n");

			sEmailStr.append("1. Log on to " + unBrandprop.getAppName() + " and select the above workspace.\n");
			sEmailStr.append("2. Select \"Manage workspace\" link.\n");
			sEmailStr.append("3. Select \"Change Workspace owner for this workspace\" option.\n");
			sEmailStr.append("4. Select the new workspace owner defined above, assign a role to the old workspace owner and click continue.\n");
			sEmailStr.append("5. Select \"Submit\" at the confirmation message to confirm the transfer of ownership of the workspace.\n\n\n");

			sEmailStr.append("To log on to " + unBrandprop.getAppName() + " and access the workspace, click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/" + unBrandprop.getLandingPageURL()) + "?linkid=" + unBrandprop.getLinkID() + "\n\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(unBrandprop.getAppName()));
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("Delivered by E&TS Connect. \n");
//			sEmailStr.append("This is a system generated email. \n");
//			sEmailStr.append("===============================================================\n\n");

			String sToList = unBrandprop.getAdminEmailID();

			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
			ETSUtils.insertEmailLog(con,"WORKSPACE","REQUEST","TRANSFER",es.gEMAIL,"",sEmailSubject,sToList,"");

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendSubWorkspaceRequestNotificationEmail(Connection con, ETSProj proj, String sWorkspaceOwner, String sSubWorkspaceName,String sDescription, EdgeAccessCntrl es) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());

			String sEmailSubject = unBrandedprop.getAppName() + " - Request to create a sub workspace";

			sEmailStr.append("A request to create sub-workspace has been generated.\n\n");

			sEmailStr.append("The details of the request are as follows: \n");

			sEmailStr.append("===============================================================\n");

			sEmailStr.append("  From:           " + ETSUtils.getUsersName(con,es.gIR_USERN) + "\n");
			sEmailStr.append("  Main workspace: " + proj.getName() + "\n\n");

			sEmailStr.append("  Sub-workspace:  " + sSubWorkspaceName + "\n");
			sEmailStr.append("  Owner:          " + ETSUtils.getUsersName(con,sWorkspaceOwner) + " [ID: " + sWorkspaceOwner.trim() + "]\n");
			sEmailStr.append("  Description:    " + ETSUtils.formatEmailStr(sDescription) + "\n\n\n");

			sEmailStr.append("To log on to " + unBrandedprop.getAppName() + ", click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/" + unBrandedprop.getLandingPageURL()) + "?linkid=" + unBrandedprop.getLinkID() + "\n\n");
			
			sEmailStr.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("Delivered by E&TS Connect. \n");
//			sEmailStr.append("This is a system generated email. \n");
//			sEmailStr.append("===============================================================\n\n");

			String sToList = unBrandedprop.getAdminEmailID();

			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
			ETSUtils.insertEmailLog(con,"SUB-WORKSPACE","REQUEST","CREATE",es.gEMAIL,"",sEmailSubject,sToList,"");

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendChangeOldOwnerNotificationEmail(Connection con, ETSProj proj, String sOldWorkspaceOwner, String sNewOwner,EdgeAccessCntrl es) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

			String sEmailSubject = prop.getAppName() + " - Change in workspace owner";

			sEmailStr.append("A change in workspace owner has been made.\n\n");

			sEmailStr.append("The details of the change are as follows: \n");

			sEmailStr.append("===============================================================\n");

			sEmailStr.append("  Workspace:      " + proj.getName() + "\n");
			sEmailStr.append("  New owner:      " + ETSUtils.getUsersName(con,sNewOwner) + " [ID: " + sNewOwner.trim() + "]\n\n");

			sEmailStr.append("To log on to " + prop.getAppName() + ", click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/" + prop.getLandingPageURL()) + "?linkid=" + prop.getLinkID() + "\n\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("Delivered by E&TS Connect. \n");
//			sEmailStr.append("This is a system generated email. \n");
//			sEmailStr.append("===============================================================\n\n");

			String sToList = ETSUtils.getUserEmail(con,sOldWorkspaceOwner);

			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
			ETSUtils.insertEmailLog(con,"WORKSPACE","CHANGE","OWNER",es.gEMAIL,"",sEmailSubject,sToList,"");

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendChangeNewOwnerNotificationEmail(Connection con, ETSProj proj, String sOldWorkspaceOwner, String sNewOwner,EdgeAccessCntrl es) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

			String sEmailSubject = prop.getAppName() + " - Change in workspace owner";

			sEmailStr.append("You have been assigned as the workspace owner for the following workspace.\n\n");

			sEmailStr.append("The details of the workspace are as follows: \n");

			sEmailStr.append("===============================================================\n");

			sEmailStr.append("  Workspace:      " + proj.getName() + "\n");
			sEmailStr.append("  New owner:      " + ETSUtils.getUsersName(con,sNewOwner) + " [ID: " + sNewOwner.trim() + "]\n\n");

			sEmailStr.append("To log on to " + prop.getAppName() + ", click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/" + prop.getLandingPageURL()) + "?linkid=" + prop.getLinkID() + "\n\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("Delivered by E&TS Connect. \n");
//			sEmailStr.append("This is a system generated email. \n");
//			sEmailStr.append("===============================================================\n\n");

			String sToList = ETSUtils.getUserEmail(con,sNewOwner);

			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
			ETSUtils.insertEmailLog(con,"WORKSPACE","CHANGE","OWNER",es.gEMAIL,"",sEmailSubject,sToList,"");

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendArchiveWorkspaceNotificationEmail(Connection con, ETSProj proj, EdgeAccessCntrl es) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

			String sEmailSubject = prop.getAppName() + " - Workspace has been archived";

			sEmailStr.append("A workspace on " + prop.getAppName() + " has been archived.\n\n");

			sEmailStr.append("The details of the workspace are as follows: \n");

			sEmailStr.append("===============================================================\n");
			sEmailStr.append("  Workspace:      " + proj.getName() + "\n");
			sEmailStr.append("  Archived by:    " + ETSUtils.getUsersName(con,es.gIR_USERN) + " [ID: " + es.gIR_USERN.trim() + "]\n\n");

			sEmailStr.append("To log on to " + prop.getAppName() + ", click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/" + prop.getLandingPageURL()) + "?linkid=" + prop.getLinkID() + "\n\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("Delivered by E&TS Connect. \n");
//			sEmailStr.append("This is a system generated email. \n");
//			sEmailStr.append("===============================================================\n\n");

			String sToList = "";

			Vector vMembers = ETSDatabaseManager.getProjMembers(proj.getProjectId(),con);
			if (vMembers != null && vMembers.size() > 0) {
				for (int i = 0; i < vMembers.size(); i++) {
					ETSUser user = (ETSUser) vMembers.elementAt(i);
					if (sToList.trim().equals("")) {
						sToList = ETSUtils.getUserEmail(con,user.getUserId());
					} else {
						sToList = sToList + "," + ETSUtils.getUserEmail(con,user.getUserId());
					}
				}
			}


			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
			ETSUtils.insertEmailLog(con,"WORKSPACE","ARCHIVE","ARCHIVE",es.gEMAIL,"",sEmailSubject,sToList,"");

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendDeleteWorkspaceNotificationEmail(Connection con, ETSProj proj,EdgeAccessCntrl es) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

			String sEmailSubject = prop.getAppName() + " - Workspace has been deleted";

			sEmailStr.append("A workspace on " + prop.getAppName() + " has been deleted.\n\n");

			sEmailStr.append("The details of the workspace are as follows: \n");

			sEmailStr.append("===============================================================\n");
			sEmailStr.append("  Workspace:      " + proj.getName() + "\n");
			sEmailStr.append("  Deleted by:     " + ETSUtils.getUsersName(con,es.gIR_USERN) + " [ID: " + es.gIR_USERN.trim() + "]\n\n");

			sEmailStr.append("To log on to " + prop.getAppName() + ", click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/" + prop.getLandingPageURL()) + "?linkid=" + prop.getLinkID() + "\n\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));

			String sToList = "";

			Vector vMembers = ETSDatabaseManager.getProjMembers(proj.getProjectId(),con);
			if (vMembers != null && vMembers.size() > 0) {
				for (int i = 0; i < vMembers.size(); i++) {
					ETSUser user = (ETSUser) vMembers.elementAt(i);
					if (sToList.trim().equals("")) {
						sToList = ETSUtils.getUserEmail(con,user.getUserId());
					} else {
						sToList = sToList + "," + ETSUtils.getUserEmail(con,user.getUserId());
					}
				}
			}


			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
			ETSUtils.insertEmailLog(con,"WORKSPACE","DELETE","DELETE",es.gEMAIL,"",sEmailSubject,sToList,"");

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendPromoteWorkspaceNotificationEmail(Connection con, ETSProj proj,String sNewWorkspaceName, EdgeAccessCntrl es) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

			String sEmailSubject = prop.getAppName() + " - Proposal workspace has been promoted to a project";

			sEmailStr.append("A proposal workspace on " + prop.getAppName() + " has been promoted as a project.\n\n");

			sEmailStr.append("The details of the promotion are as follows: \n");

			sEmailStr.append("===============================================================\n");
			sEmailStr.append("  Proposal Name:  " + proj.getName() + "\n\n");

			sEmailStr.append("  New Project:    " + sNewWorkspaceName + "\n");
			sEmailStr.append("  By:             " + ETSUtils.getUsersName(con,es.gIR_USERN) + " [ID: " + es.gIR_USERN.trim() + "]\n\n");

			sEmailStr.append("To log on to " + prop.getAppName() + ", click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/" + prop.getLandingPageURL()) + "?linkid=" + prop.getLinkID() + "\n\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("Delivered by E&TS Connect. \n");
//			sEmailStr.append("This is a system generated email. \n");
//			sEmailStr.append("===============================================================\n\n");

			String sToList = "";

			Vector vMembers = ETSDatabaseManager.getProjMembers(proj.getProjectId(),con);
			if (vMembers != null && vMembers.size() > 0) {
				for (int i = 0; i < vMembers.size(); i++) {
					ETSUser user = (ETSUser) vMembers.elementAt(i);
					if (sToList.trim().equals("")) {
						sToList = ETSUtils.getUserEmail(con,user.getUserId());
					} else {
						sToList = sToList + "," + ETSUtils.getUserEmail(con,user.getUserId());
					}
				}
			}


			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
			ETSUtils.insertEmailLog(con,"WORKSPACE","PROMOTE","PROPOSAL",es.gEMAIL,"",sEmailSubject,sToList,"");

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendPOCEmail(Connection con, ETSProj proj, EdgeAccessCntrl es, String sIRId) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

			String sName = ETSUtils.getUsersName(con,sIRId);

			String sEmailSubject = prop.getAppName() + " - MultiPOC entitlement request for ( " + sName + " )";

			sEmailStr.append(prop.getAppName() + " has requested MultiPOC for the following user \n");
			sEmailStr.append("who is going to be managing a workspace and will need to give\n");
			sEmailStr.append("external users access to it.\n\n");

			sEmailStr.append("The details of the request are as follows: \n");
			sEmailStr.append("  ID                 : " + sIRId + "\n");
			sEmailStr.append("  Name               : " + sName + "\n\n");
			sEmailStr.append("  Requested by id    : " + es.gIR_USERN + "\n");
			sEmailStr.append("  Requested by       : " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + "\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
//			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
//			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
//			sEmailStr.append("of on demand tools that is available online 24/7.\n");
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
//			sEmailStr.append("===============================================================\n\n");

			String sToList = prop.getPOCEmail();

			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

	private void promoteWorkspaceToPublic() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col157\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"public-confirm\" />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col158\" align=\"left\" >You have selected the option to change this workspace access to Public. Please read the details below and click on <b>Submit</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col159\" align=\"left\" ><b>Please note that the following happens when a workspace access is changed to Public</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"col160\" align=\"left\" >");
		out.println("<ol>");
		out.println("<li>" + this.showPresentAccessForWS() + "</li>");
		out.println("<li>After the change all members from sales team will be able to access & perform operations on this workspace even if they are not exclusively the member for this Workspace.</li>");
		out.println("</ol>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col161\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"col162\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col163\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col164\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void promoteWorkspaceToPublicConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		String sOldWorkSpaceAccessType = proj.getIsPrivate();


		try {

			con.setAutoCommit(false);

			boolean bSuccess = AICWorkspaceDAO.updateWorkspaceAccessType(con,proj.getProjectId(),Defines.AIC_IS_PRIVATE_PUBLIC);


			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			con.rollback();
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}

		boolean bSent = sendWSAccessChangeNotificationEmail(con,proj,es,Defines.AIC_IS_PRIVATE_PUBLIC,sOldWorkSpaceAccessType);

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col165\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"public-confirm\" />");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col166\" align=\"left\" ><b>This workspace access has been changed to Public successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col167\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	private void promoteWorkspaceToRestricted() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col168\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"restricted-confirm\" />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col169\" align=\"left\" >You have selected the option to change this workspace access to Restricted. Please read the details below and click on <b>Submit</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col170\" align=\"left\" ><b>Please note that the following happens when a workspace access is changed to Restricted</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"col71\" headers=\"\" align=\"left\" >");
		out.println("<ol>");
		out.println("<li>" + this.showPresentAccessForWS() + "</li>");
		out.println("<li>After the change every user has to  request explicit membership to this workspaces or must be explicitly be added to the workspace. This workspace will be available for viewing and querying on the Collab center landing page. </li>");
		out.println("</ol>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col72\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"col73\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col174\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col175\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void promoteWorkspaceToRestrictedConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		String sOldWorkSpaceAccessType = proj.getIsPrivate();


		try {

			con.setAutoCommit(false);

			boolean bSuccess = AICWorkspaceDAO.updateWorkspaceAccessType(con,proj.getProjectId(),Defines.AIC_IS_PRIVATE_RESTRICTED);


			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			con.rollback();
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}

		boolean bSent = sendWSAccessChangeNotificationEmail(con,proj,es,Defines.AIC_IS_PRIVATE_RESTRICTED,sOldWorkSpaceAccessType);

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col176\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"restricted-confirm\" />");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col177\" align=\"left\" ><b>This workspace access has been changed to Restricted successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col178\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}


	private void promoteWorkspaceToPrivate() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col179\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"private-confirm\" />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col180\" align=\"left\" >You have selected the option to change this workspace access to Private. Please read the details below and click on <b>Submit</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col181\" align=\"left\" ><b>Please note that the following happens when a workspace access is changed to Private</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"col182\" align=\"left\" >");
		out.println("<ol>");
		out.println("<li>" + this.showPresentAccessForWS() + "</li>");
		out.println("<li>After the change every user must be explicitly added to the workspace if they need access. This workspaces will not be available for viewing and querying on the Collab center landing page for any user who is not an explicit member of this workspace.</li>");
		out.println("</ol>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col83\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"col84\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col185\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col186\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}

	/**
	 *
	 */
	private void promoteWorkspaceToPrivateConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		String sOldWorkSpaceAccessType = proj.getIsPrivate();


		try {

			con.setAutoCommit(false);

			boolean bSuccess = AICWorkspaceDAO.updateWorkspaceAccessType(con,proj.getProjectId(),Defines.AIC_IS_PRIVATE_PRIVATE);


			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			con.rollback();
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}

		boolean bSent = sendWSAccessChangeNotificationEmail(con,proj,es,Defines.AIC_IS_PRIVATE_PRIVATE,sOldWorkSpaceAccessType);

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col187\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"private-confirm\" />");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col188\" align=\"left\" ><b>This workspace access has been changed to Private successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col189\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}
    
	private void promoteWorkspaceToExternalUsers() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();


		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col179\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"external_confirm\" />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col180\" align=\"left\" >You have selected the option to change this workspace access to External users also. Please read the details below and click on <b>Submit</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col182\" align=\"left\" >");
		out.println("<ol>");
		out.println("<li>Please note: The workspace owner is responsible for security and integrity of the access granted to the members.</li>");
		out.println("<li>This Action cannot be undone.</li>");
		out.println("</ol>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col83\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"col84\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col185\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col186\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}
    
	private void promoteWorkspaceToExternalUsersConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		String sOldWorkSpaceAccessType = proj.getIsPrivate();


		try {

			con.setAutoCommit(false);
			
			boolean bSuccess = AICWorkspaceDAO.updateWorkspaceToExternalUsers(con,proj.getProjectId());
			 
			con.commit();
			con.setAutoCommit(true);

		} catch (SQLException e) {
			con.rollback();
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"col187\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"external_confirm\" />");

		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col188\" align=\"left\" ><b>The workspace access has been promoted successfully for adding External users also.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col189\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</form>");

	}


	private String showPresentAccessForWS() throws SQLException, Exception {
		ETSProj proj = this.params.getETSProj();
		String information = "";
		if (proj.getIsPrivate().equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PUBLIC)){
			information ="Presently everyone from the Sales team is having access to this workspace even if they are not exclusively the member for this Workspace.";
		} else if (proj.getIsPrivate().equalsIgnoreCase(Defines.AIC_IS_PRIVATE_RESTRICTED)){
			information ="Presently every user has to  request explicit membership to these workspaces or must be explicitly be added to the workspace. This workspace is available for viewing and querying on the Collab center landing page. ";
		} else if (proj.getIsPrivate().equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PRIVATE)){
			information ="Presently every user must be explicitly be added to the workspace. This workspaces is not available for viewing and querying on the Collab center landing page for any user who is not an explicit member of this workspace.";
		}
		return information;
	}

	private String getWSTypeName(String sWSType) throws SQLException, Exception {

		String sWSTypeName = "";
		if (sWSType.equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PUBLIC)){
			sWSTypeName ="Public Workspace";
		} else if (sWSType.equalsIgnoreCase(Defines.AIC_IS_PRIVATE_RESTRICTED)){
			sWSTypeName ="Restricted Workspace";
		} else if (sWSType.equalsIgnoreCase(Defines.AIC_IS_PRIVATE_PRIVATE)){
			sWSTypeName ="Private Workspace";
		}
		return sWSTypeName;
	}

	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendWSAccessChangeNotificationEmail(Connection con, ETSProj proj, EdgeAccessCntrl es, String newWSType, String oldWSType) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

			String sEmailSubject = prop.getAppName() + " - Workspace Access Type has been changed";

			sEmailStr.append("A workspace on " + prop.getAppName() + " access has been changed from " + getWSTypeName(oldWSType) + " to " + getWSTypeName(newWSType) + " .\n\n");

			sEmailStr.append("The details of the workspace are as follows: \n");

			sEmailStr.append("===============================================================\n");
			sEmailStr.append("  Workspace:      " + proj.getName() + "\n");
			sEmailStr.append("  Workspace Type changed by:    " + ETSUtils.getUsersName(con,es.gIR_USERN) + " [ID: " + es.gIR_USERN.trim() + "]\n\n");

			sEmailStr.append("To log on to " + prop.getAppName() + ", click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/" + prop.getLandingPageURL()) + "?linkid=" + prop.getLinkID() + "\n\n");

			
			sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));

			String sToList = "";

			Vector vMembers = ETSDatabaseManager.getProjMembers(proj.getProjectId(),con);
			if (vMembers != null && vMembers.size() > 0) {
				for (int i = 0; i < vMembers.size(); i++) {
					ETSUser user = (ETSUser) vMembers.elementAt(i);
					if (sToList.trim().equals("")) {
						sToList = ETSUtils.getUserEmail(con,user.getUserId());
					} else {
						sToList = sToList + "," + ETSUtils.getUserEmail(con,user.getUserId());
					}
				}
			}


			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
			ETSUtils.insertEmailLog(con,"WORKSPACE",getWSTypeName(oldWSType),getWSTypeName(newWSType),es.gEMAIL,"",sEmailSubject,sToList,"");

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

}
