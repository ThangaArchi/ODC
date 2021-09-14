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


package oem.edge.ets.fe.wspace;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProjectInfoBean;
import oem.edge.ets.fe.ETSUnAuthorizedServlet;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSManageWorkspaceHandler {


	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.26";

	private ETSParams params;
	private String header = "";
	private EdgeAccessCntrl es = null;
	

	public ETSManageWorkspaceHandler(ETSParams parameters, EdgeAccessCntrl es1) {
		
		this.params = parameters;
		this.es = es1;
	
	}

	public void handleRequest() throws SQLException, Exception {


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
		
		// Basic check. If user is not WS_OWNER / SUPER ADMIN, do not allow
		if (!ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) && !ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
			ETSUnAuthorizedServlet.printMsg(params.getWriter());
			return;
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
			promoteWorkspaceToProject("");
		} else if (sWFlag.trim().equals("promote-confirm")){
			String sError = validateWorkspaceCount();
			if (sError.trim().equalsIgnoreCase("")) {
				promoteWorkspaceToProjectConfirm();
			} else {
				promoteWorkspaceToProject(sError);
			}
			
		} else if (sWFlag.trim().equals("promote-confirm2")){
			promoteWorkspaceToProjectFinal();
		} else if (sWFlag.trim().equals("assign_rpm_link")){
			displayRPMLink("");
		} else if (sWFlag.trim().equals("assign_rpm_link_confirm")) {
			String error = validateRPMLink();
			if (!error.trim().equals("")) {
				displayRPMLink(error);
			} else {
				assignRPMLink();	
			}
		} else if (sWFlag.trim().equals("change_ws_name")) {
			showWorkspaceNameChange("");
		} else if (sWFlag.trim().equals("change_ws_name_confirm")) {
			String error = validateChangeWorkspaceName();
			if (error.trim().equals("")) {
				updateWorkspaceNameChange();
			} else {
				showWorkspaceNameChange(error);
			}
		} else if (sWFlag.trim().equals("change_ws_geo")) {
			showGeographyAndOther("");	
		} else if (sWFlag.trim().equals("change_ws_geo_confirm")) {
			String error = validateGeographyAndOther();
			if (error.trim().equals("")) {
				updateGeographyAndOther();
			} else {
				showGeographyAndOther(error);	
			}
		} else if (sWFlag.trim().equals("change_ws_tabs")) {
			changeWorkspaceTabs("");	
		} else if (sWFlag.trim().equals("change_ws_tabs_confirm")) {
			String error = validateChangeWorkspaceTabs();
			if (error.trim().equals("")) {
				changeWorkspaceTabsFinal();
			} else {
				changeWorkspaceTabs(error);	
			}
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

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
			
			out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
			out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
			out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
				
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" >Please select one of the options below.</td>");
			out.println("</tr>");
			out.println("</table>");
	
			printGreyDottedLine(out);
			out.println("<br />");
			
			if (!proj.getProject_status().equalsIgnoreCase("A")) {
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=archive\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Archive this workspace\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=archive\">Archive this workspace</a></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
			}
	
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=delete\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Delete this workspace\" border=\"0\" /></a></td>");
			out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=delete\">Delete this workspace</a></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");
	

			if (!proj.getProject_status().equalsIgnoreCase("A")) {
				// display only to super workspace admin
				// also display to workspace owner.. changed in 5.2.1 release...
				//if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					//out.println("<td headers=\"\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"transfer\" id=\"label_transfer\" /><label for=\"label_transfer\">Change workspace owner for this workspace</label></td>");
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=transfer\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Change workspace owner for this workspace\" border=\"0\" /></a></td>");
					out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=transfer\">Change workspace owner for this workspace</a></td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<br />");
				//}
			}
	
			boolean bIsSubWorkspace = ETSWorkspaceDAO.isSubWorkspace(con,proj.getParent_id());
			
			if (!proj.getProject_status().equalsIgnoreCase("A")) {
				// display only to proposal.
				if (proj.getProjectOrProposal().trim().equalsIgnoreCase("O") && bIsSubWorkspace == false) {
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					//out.println("<td headers=\"\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"promote\" id=\"label_promote\" /><label for=\"label_promote\">Promote this proposal to project</label></td>");
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=promote\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Change workspace owner for this workspace\" border=\"0\" /></a></td>");
					out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=promote\">Promote this proposal to project</a></td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<br />");
				}
			}

			// added for 6.1.1 change workspace tabs function
			if (!proj.getProject_status().equalsIgnoreCase("A")) {			
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				//out.println("<td headers=\"\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"change_ws_name\" id=\"label_change_ws_name\" /><label for=\"label_change_ws_name\">Change workspace name</label></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=change_ws_name\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Change workspace name\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=change_ws_name\">Change workspace name</a></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
			}
			
			// added for 6.1.1 change workspace name function
			if (!proj.getProject_status().equalsIgnoreCase("A")) {			
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				//out.println("<td headers=\"\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"change_ws_tabs\" id=\"label_change_ws_tabs\" /><label for=\"label_change_ws_tabs\">Change workspace tabs</label></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=change_ws_tabs\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Change workspace tabs\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=change_ws_tabs\">Change workspace tabs</a></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
			}

			// added for 6.1.1 update geo, delivery and industry
			if (!proj.getProject_status().equalsIgnoreCase("A")) {
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				//out.println("<td headers=\"\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"change_ws_geo\" id=\"label_change_ws_geo\" /><label for=\"label_change_ws_geo\">Update geography, delivery team and industry</label></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=change_ws_geo\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Update geography, delivery team and industry\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=change_ws_geo\">Update geography, delivery team and industry</a></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
			}
	
			if (!proj.getProject_status().equalsIgnoreCase("A")) {
				String hasParent = proj.getParent_id();
				if (hasParent.equalsIgnoreCase("0")) {
					String projType = proj.getProjectOrProposal();
					String urlString = "";
					String itarString = "N";
					if (!projType.equalsIgnoreCase("C")) {
						if (projType.equalsIgnoreCase("O")) {
							urlString = "Proposal-Sub";
						} else {
							urlString = "Project-Sub";
						}
						if (proj.isITAR()) {
							itarString = "Y";
						}
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						//out.println("<td headers=\"\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"change_ws_geo\" id=\"label_change_ws_geo\" /><label for=\"label_change_ws_geo\">Update geography, delivery team and industry</label></td>");
						out.println("<td headers=\"\" align=\"left\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSCreateWorkspaceServlet.wss?op=second&linkid=" + params.getLinkId() + "&workspace_company=" + URLEncoder.encode(proj.getCompany()) + "&workspace_cat=" + urlString + "&workspace_main=" + proj.getProjectId() + "&geography=" + URLEncoder.encode(proj.getGeography()) + "&delivery=" + URLEncoder.encode(proj.getDeliveryTeam()) + "&industry=" + URLEncoder.encode(proj.getIndustry()) + "&itar=" + itarString + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Create sub-workspace\" border=\"0\" /></a></td>");
						out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSCreateWorkspaceServlet.wss?op=second&linkid=" + params.getLinkId() + "&workspace_company=" + URLEncoder.encode(proj.getCompany()) + "&workspace_cat=" + urlString + "&workspace_main=" + proj.getProjectId() + "&geography=" + URLEncoder.encode(proj.getGeography()) + "&delivery=" + URLEncoder.encode(proj.getDeliveryTeam()) + "&industry=" + URLEncoder.encode(proj.getIndustry()) + "&itar=" + itarString + "\">Create sub-workspace</a></td>");
						out.println("</tr>");
						out.println("</table>");
						out.println("<br />");
					}
				}
			}

			// display RPM link - CSR 9528 - Sathish
			if (!proj.getProject_status().equalsIgnoreCase("A") && proj.getProjectOrProposal().equalsIgnoreCase("P") && !proj.isITAR()) {
				
				// check if the RPM Project code has been assigned already.. 
				if (proj.getLotusProject() == null || proj.getLotusProject().trim().equals("")) {
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					//out.println("<td headers=\"\" align=\"left\" ><input type=\"radio\" name=\"manage\" value=\"assign_rpm_link\" id=\"label_rpm\" /><label for=\"label_rpm\">Assign project code</label></td>");
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=assign_rpm_link\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Assign project code\" border=\"0\" /></a></td>");
					out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?etsop=manage_wspace&linkid=" + params.getLinkId() + "&tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&manage=assign_rpm_link\">Assign project code</a></td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<br />");
				}
			}

	
			//out.println("<br />");
				
			printGreyDottedLine(out);
			
			out.println("<br />");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" >Back</a></td></tr></table></td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("</form>");
			
		} else {
			
			out.println("<br />");
			
			printGreyDottedLine(out);
			
			out.println("<br />");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b><span style=\"color:#ff3333\">You are not authorized to access this functionality.</span></b></td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
			printGreyDottedLine(out);
			
			out.println("<br />");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
			out.println("</tr>");
			out.println("</table>");
			
		}
		
	}

	/**
	 * 
	 */
	private void promoteWorkspaceToProject(String sError) throws SQLException, Exception {
		
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
	
		boolean bITAR = proj.isITAR();
	
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Promote this proposal to project</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" >You have selected the option to promote this proposal workspace to a project. Please select the options below and click on <b>Continue</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");
	
		printGreyDottedLine(out);
		out.println("<br />");
		
		if (!sError.trim().equals("")) {
				
			out.println("<br />");
				
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + sError + "</span></td>");
			out.println("</tr>");
			out.println("</table>");
				
			out.println("<br />");
				
			printGreyDottedLine(out);
			
			out.println("<br />");
				
		}
		
		
		if (!bITAR) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" >Please select the tabs (maximum of five) that you wish to be carried over to the project workspace.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" ><span style=\"color:#ff3333\"><b>Note: If you do not select a tab that exists currently, the data related to the tab will be deleted permanently and cannot be retrieved back.</b></span></td>");
			out.println("</tr>");
			out.println("</table>");
		
			out.println("<br />");
		}
				
		int iCount = ETSWorkspaceDAO.checkSubWorkspaces(con,proj.getProjectId());
		
		if (iCount > 0) {
			
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" ><span style=\"color:#ff3333\"><b>Note: This workspace has sub-workspaces. The sub-workspaces will be deleted once this proposal is promoted to a project.</b></span></td>");
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<br />");
			
		}
		
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"130\"><b><label for=\"label_name\">Workspace name:</label></b></td>");
		if (sError.equals("")) {
			out.println("<td headers=\"\" align=\"left\" ><input type=\"text\" name=\"workspace_name\" class=\"iform\" id=\"label_name\" value=\"" + proj.getName() + "\" style=\"width:300px\" width=\"300px\" maxlength=\"100\" /></td>");
		} else {
			String sWorkspaceName = request.getParameter("workspace_name");
			out.println("<td headers=\"\" align=\"left\" ><input type=\"text\" name=\"workspace_name\" class=\"iform\" id=\"label_name\" value=\"" + sWorkspaceName + "\" style=\"width:300px\" width=\"300px\" maxlength=\"100\" /></td>");
		}
		
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
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Main\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>Main</b><input type=\"hidden\" name=\"tab_main\" value=\"Y\" /></td>");
				out.println("</tr>");
				// display meetings by default
				if (!bITAR) {
					out.println("<tr>");
					out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
					if (sTabMeetings.equalsIgnoreCase("Y")) {
						out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meetings\" selected=\"selected\" checked=\"checked\" /></td>");	
					} else {
						out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meetings\" /></td>");
					}
					
					out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_meetings\">Meetings</label></b></td>");
					out.println("</tr>");
				}				
			} else if (cat.getViewType() == Defines.CONTRACTS_VT && !bITAR) {
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabContracts.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_contracts\" value=\"Y\" id=\"label_contract\" selected=\"selected\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_contracts\" value=\"Y\" id=\"label_contract\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_contract\">Contracts</label></b></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.DOCUMENTS_VT) {
				bDocuments = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Documents\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Documents</b><input type=\"hidden\" name=\"tab_documents\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.ISSUES_CHANGES_VT) {
				bIssues = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabIssues.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_issues\" selected=\"selected\" checked=\"checked\" /></td>");	
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_issues\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_issues\">Issues/Changes</label></b></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.TEAM_VT) {
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Team</b><input type=\"hidden\" name=\"tab_team\" value=\"Y\" /></td>");
				out.println("</tr>");
			}
			out.println("</table>");
		}

		if (!bDocuments) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
			if (sTabDocuments.equalsIgnoreCase("Y")) {
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_documents\" selected=\"selected\" checked=\"checked\" /></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_documents\" /></td>");
			}
			
			out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_documents\">Documents</label></b></td>");
			out.println("</tr>");
			out.println("</table>");
		}
		
		if (!bIssues && !bITAR) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
			if (sTabIssues.equalsIgnoreCase("Y")) {
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_issues\" selected=\"selected\" checked=\"checked\" /></td>");	
			} else {
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_issues\" /></td>");
			}
			
			out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_issues\">Issues/Changes</label></b></td>");
			out.println("</tr>");
			out.println("</table>");
		}
	
		out.println("<br />");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" id=\"label_cont\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");		
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");
		
	}

	/**
	 * 
	 */
	private void displayRPMLink(String sError) throws SQLException, Exception {
		
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
	
		String rpm_proj_code = request.getParameter("rpm_proj_code");
		if (rpm_proj_code == null || rpm_proj_code.trim().equalsIgnoreCase("")) {
			rpm_proj_code = "";
		} else {
			rpm_proj_code = rpm_proj_code.trim();
		}
			
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Assign project code</span></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
	
		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"assign_rpm_link_confirm\" />");
			
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >");
		out.println("Enabling synchronization between Rational Portfolio Manager and this workspace is a multi-step process.<br /><br />");
		out.println("<ol><li>First contact the Rational Portfolio Manager deployment team to get the appropriate access enabled for you in Rational Portfolio Manager. They will provide you detailed information on how to set up Rational Portfolio Manager for synchronization. </li>");
		out.println("<li>After all the setup of Rational Portfolio Manager has been completed you are ready for the final step. Enter the value from the \"REFERENCE #\" field found in the Rational Portfolio Manager \"Identification Portal\". The reference number usually contains a three digit code assigned to an opportunity/project and the geography information, for example \"ETS.US.ETS\".</li>");
		out.println("</ol>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
	
		printGreyDottedLine(out);
		out.println("<br />");
		
		if (!sError.trim().equals("")) {
				
			out.println("<br />");
				
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + sError + "</span></td>");
			out.println("</tr>");
			out.println("</table>");
				
			out.println("<br />");
				
			printGreyDottedLine(out);
			
			out.println("<br />");
				
		}
		
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"130\"><b><label for=\"label_code\">Project code:</label></b></td>");
		if (sError.equals("")) {
			out.println("<td headers=\"\" align=\"left\" ><input type=\"text\" name=\"rpm_proj_code\" class=\"iform\" id=\"label_code\" value=\"\" style=\"width:300px\" width=\"300px\" maxlength=\"100\" /></td>");
		} else {
			out.println("<td headers=\"\" align=\"left\" ><input type=\"text\" name=\"rpm_proj_code\" class=\"iform\" id=\"label_code\" value=\"" + rpm_proj_code + "\" style=\"width:300px\" width=\"300px\" maxlength=\"100\" /></td>");
		}
		
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");
		
	}

	/**
	 * 
	 */
	private String validateRPMLink() throws SQLException, Exception {
		
		
		StringBuffer error = new StringBuffer("");
		
		HttpServletRequest request = this.params.getRequest();
		ETSProj proj = this.params.getETSProj();
		Connection con = this.params.getConnection();
	
		String rpm_proj_code = request.getParameter("rpm_proj_code");
		if (rpm_proj_code == null || rpm_proj_code.trim().equalsIgnoreCase("")) {
			rpm_proj_code = "";
		} else {
			rpm_proj_code = rpm_proj_code.trim();
		}
		
		if (rpm_proj_code.equals("")) {
			error.append("Project code cannot be empty. Please enter a project code.<br />");
		}
		
		if (!rpm_proj_code.equals("")) {
			Vector vCodes = ETSWorkspaceDAO.getPMOID(con,rpm_proj_code);
			if (vCodes.size() > 1) {
				error.append("There is more than one PMO ID was available for the given project code. Please contact your system administrator.<br />");
			} else if (vCodes.size() <= 0) {
				error.append("There are no PMO ID available for the given project code. Please contact your system administrator.<br />");
			} else {
				String pmo_id = (String) vCodes.elementAt(0);
				boolean assigned = ETSWorkspaceDAO.isPMOProjectIDAlreadyAssigned(con,pmo_id);
				if (assigned) {
					error.append("Project code entered is already assigned to another E&TS Connect workspace. Please enter a different Project code.<br />");
				}
			}
		}
			
		return error.toString();
		
	}

	/**
	 * 
	 */
	private void assignRPMLink() throws SQLException, Exception {
		
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
	
		String rpm_proj_code = request.getParameter("rpm_proj_code");
		if (rpm_proj_code == null || rpm_proj_code.trim().equalsIgnoreCase("")) {
			rpm_proj_code = "";
		} else {
			rpm_proj_code = rpm_proj_code.trim();
		}
			
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Assign project code</span></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		
		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"assign_rpm_link_confirm\" />");		
		
		// update lotus id into ets.ets_projects
		// get the pmo_project_id from ets.pmo_main table.
		// update pmo_project_id in ets.ets_projects
		// if all above was successful, then check if issues tab available,
		// if yes, then insert issue types..
		
		boolean invalid_pm_codes = false;
		boolean updated = false;
		boolean success = false;
		boolean issue = false;
		
		Vector vCodes = ETSWorkspaceDAO.getPMOID(con,rpm_proj_code);
		if (vCodes.size() > 1 || vCodes.size() <= 0) {
			// error.. more than one project code found.. or no project code
			invalid_pm_codes = true;
		} else {
			
			String pmo_id = (String) vCodes.elementAt(0);
			
			updated = ETSWorkspaceDAO.updateLotusID(con,proj.getProjectId(),rpm_proj_code,pmo_id);
			success = false;
			issue = false;
			
			if (updated) {

				ETSUser owner = new ETSUser();
				
				String owner_ir_id = "";
				
				Vector vOwner = ETSDatabaseManager.getUsersByProjectPriv(proj.getProjectId(),Defines.OWNER,con);

				if (vOwner != null && vOwner.size() > 0) {
					 owner = (ETSUser) vOwner.elementAt(0);
					 owner_ir_id = owner.getUserId();
				}

				String issueTopCat = ETSUtils.getTopCatId(con,proj.getProjectId(), Defines.ISSUES_CHANGES_VT); 
					
				if (!issueTopCat.equalsIgnoreCase("0") && !owner_ir_id.equals("")) {
					issue = true;
					success = ETSWorkspaceDAO.updateIssuesWithRPM(con,proj,owner.getUserId(),es.gUSERN);
				}
			}
		}
		
		printGreyDottedLine(out);
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >");
		
		if (updated) {
			if (success) {
				out.println("<b>The project code and issue types has been created and assigned successfully.</b>");
			} else { 
				if (issue) {	
					out.println("<b>The project code has been assigned successfully.</b>");	
					out.println("<br /><span style=\"color:#ff3333\"><b>There was an error in creating issue types. Please contact the your system administrator.</b></span>");	
				} else {
					out.println("<b>The project code has been assigned successfully.</b>");
				}
			}
		} else {
			if (invalid_pm_codes) {
				out.println("<span style=\"color:#ff3333\"><b>There was no PMO ID available to assign OR more than one PMO ID was available for the given project code. Please contact your system administrator.</b></span>");
			} else {
				out.println("<span style=\"color:#ff3333\"><b>There was a problem in assigning the project code. Please try again. If the problem persists, please contact your system administrator.</b></span>");
			}
		}
			
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");
				
	}

	/**
	 * 
	 */
	private String validateWorkspaceCount() throws SQLException, Exception {
		
		StringBuffer sError = new StringBuffer("");
		int tabcount = 0;
		
		HttpServletRequest request = this.params.getRequest();
	
		String sTabMain = request.getParameter("tab_main");
		if (sTabMain == null || sTabMain.trim().equalsIgnoreCase("")) {
			sTabMain = "N";
		} else {
			tabcount = tabcount + 1;
		}
		String sTabMeetings = request.getParameter("tab_meetings");
		if (sTabMeetings == null || sTabMeetings.trim().equalsIgnoreCase("")) {
			sTabMeetings = "N";
		} else {
			tabcount = tabcount + 1;
		}
		String sTabContracts = request.getParameter("tab_contracts");
		if (sTabContracts == null || sTabContracts.trim().equalsIgnoreCase("")) {
			sTabContracts = "N";
		} else {
			tabcount = tabcount + 1;
		}
		String sTabDocuments = request.getParameter("tab_documents");
		if (sTabDocuments == null || sTabDocuments.trim().equalsIgnoreCase("")) {
			sTabDocuments = "N";
		} else {
			tabcount = tabcount + 1;
		}
		String sTabIssues = request.getParameter("tab_issues");
		if (sTabIssues == null || sTabIssues.trim().equalsIgnoreCase("")) {
			sTabIssues = "N";
		} else {
			tabcount = tabcount + 1;
		}
		String sTabTeam = request.getParameter("tab_team");
		if (sTabTeam == null || sTabTeam.trim().equalsIgnoreCase("")) {
			sTabTeam = "N";
		} else {
			tabcount = tabcount + 1;
		}
		
		if (tabcount > 5 ) {
			sError.append("You can select up to a maximum of five (5) tabs only.<br />");
		}
		
		String sWorkspaceName = request.getParameter("workspace_name");
		if (sWorkspaceName == null || sWorkspaceName.trim().equalsIgnoreCase("")) {
			sWorkspaceName = "";
		} else {
			sWorkspaceName = sWorkspaceName.trim();
		}
		
		if (sWorkspaceName.trim().equalsIgnoreCase("")) {
			sError.append("Workspace name cannot be empty. Please enter workspace name.<br />");
		}
		
		return sError.toString();
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Promote this proposal to project</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" ><b>Please review your changes and click on <b>Submit</b>.</b></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"130\" ><b>Workspace name:</b></td>");
		out.println("<td headers=\"\" align=\"left\"  >" + sWorkspaceName + "</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
	
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"130\" ><b>Workspace tabs:</b></td>");
		out.println("<td headers=\"\" align=\"left\"  ><b>Main</b></td>");
		out.println("</tr>");
		out.println("</table>");
		
		if (sTabMeetings.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"\" align=\"left\"  ><b>Meetings</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		if (sTabContracts.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"\" align=\"left\"  ><b>Contracts</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		if (sTabDocuments.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"\" align=\"left\"  ><b>Documents</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}
		
		if (sTabIssues.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"\" align=\"left\"  ><b>Issues/Changes</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}
		
		if (sTabTeam.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"130\" >&nbsp;</td>");
			out.println("<td headers=\"\" align=\"left\"  ><b>Team</b></td>");
			out.println("</tr>");
			out.println("</table>");
		}
		
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" id=\"label_cont\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Promote this proposal to project</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" ><b>The proposal workspace has been promoted to project successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Archive this workspace</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" >You have selected the option to archive this workspace. Please read the details below and click on <b>Submit</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");
	
		printGreyDottedLine(out);
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" ><b>Please note that the following happens when a workspace gets archived</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >");
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
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" id=\"label_cont\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Archive this workspace</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" ><b>This workspace has been archived successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Delete this workspace</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" >You have selected the option to delete this workspace. Please read the details below and click on <b>Submit</b> to confirm this change.</td>");
		out.println("</tr>");
		out.println("</table>");
	
		printGreyDottedLine(out);
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" ><b>Please note that the following happens when a workspace gets deleted</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >");
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
			out.println("<td headers=\"\" align=\"left\" ><span style=\"color:#ff3333\"><b>Note: This workspace has sub-workspaces. The sub-workspaces will also be deleted once this workspace is deleted.</b></span></td>");
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<br />");
			
		}
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" id=\"label_cont\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Delete this workspace</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" ><b>This workspace has been deleted.</b></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" >You have selected to request to have a new workspace owner. Once this new owner is assigned, your role in the workspace will be reassigned. Please select the new workspace owner and click on <b>Submit</b> to send a request to change workspace owner for this workspace.<br /><br /></td>");
		out.println("</tr>");
		out.println("</table>");
	
		printGreyDottedLine(out);
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"130\"><b><label for=\"label_owner\">Workspace owner:</label></b></td>");
		out.println("<td headers=\"\" align=\"left\" >");
		
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
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" id=\"label_cont\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" ><b>Your request to change workspace owner has been submitted successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
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
			out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Change workspace owner for this workspace</span></td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");

			if (!sError.trim().equalsIgnoreCase("")) {
				
				out.println("<br />");
				
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
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
			out.println("<td headers=\"\" align=\"left\" >You have selected to change the workspace owner for this workspace. Once this new owner is assigned, the old workspace owner has to be reassigned to a different role. Please select the new workspace owner and click on <b>Continue</b> to send a request to change workspace owner for this workspace.<br /><br /></td>");
			out.println("</tr>");
			out.println("</table>");
		
			printGreyDottedLine(out);
			out.println("<br />");
			
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"130\"><b><label for=\"label_owner\">New workspace owner:</label></b></td>");
			out.println("<td headers=\"\" align=\"left\" >");
			
			// get the current workspace owner and dont display him in the list...
			
			Vector vOwners = ETSDatabaseManager.getUsersByProjectPriv(proj.getProjectId(),Defines.OWNER,con);
			
			Vector vMembers = ETSWorkspaceDAO.getInternalUsersInWorkspace(con,proj.getProjectId());
			
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
			out.println("<td headers=\"\" align=\"left\" >Select the role the current workspace owner gets assigned once the new workspace owner is assigned.</td>");
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
	
				if (!(ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.OWNER,con))){
					out.println("<tr>");
					int iRole = -1;
					if (!sRoleStr.trim().equalsIgnoreCase("")) {
						iRole = Integer.parseInt(sRoleStr);
					}
					if (iRole == roleid) {
						out.println("<td headers=\"\" align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_"+i+"\" type=\"radio\" name=\"roles\" value=\""+roleid+"\" checked=\"checked\" /></td>");
					} else {
						out.println("<td headers=\"\" align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_"+i+"\" type=\"radio\" name=\"roles\" value=\""+roleid+"\" /></td>");
					}
					
					out.println("<td headers=\"\" align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"role_"+i+"\">"+rolename+"</label></td>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td headers=\"\">&nbsp;</td>");
					//out.println("<td headers=\"\" align=\"left\" valign=\"top\">Privileges: "+privs+"</td>");
					out.println("<td headers=\"\" align=\"left\" valign=\"top\">Privileges: ");
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
					out.println("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
				}
			}
	
			out.println("<br />");
				
			printGreyDottedLine(out);
			
			out.println("<br />");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" id=\"label_cont\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
			out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Change workspace owner for this workspace</span></td>");
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
		out.println("<td headers=\"\" align=\"left\" ><b>Please review your changes and click on <b>Submit</b>.</b></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"130\" ><b>New workspace owner:</b></td>");
		out.println("<td headers=\"\" align=\"left\"  >" + ETSUtils.getUsersName(con,sWorkspaceOwner) + "</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
	
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"130\" ><b>Role to old workspace owner:</b></td>");
		out.println("<td headers=\"\" align=\"left\"  >" + ETSDatabaseManager.getRoleName(con,Integer.parseInt(sRoleStr)) + "</td>");
		out.println("</tr>");
		out.println("</table>");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" id=\"label_cont\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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
		int iWorkspaceOwnerRoleID = 0;
		boolean ownerFlag = true;
		try {
			
	
	
			String sWorkspaceOwner = request.getParameter("workspace_owner");
			String sRoleStr = request.getParameter("roles");
			int roleid = (new Integer(sRoleStr)).intValue();
		
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Change workspace owner for this workspace</span></td>");
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
			 * 5. check if the new workspace owner has MULTIPOC entitlement.
			 * 6. if no, then send a request to decaf...
			 * 
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
			
			//if (bAssigned) changed for Manage workspace owner use case, if existing workspace owner is valid then need to 
			// reasign the new workspace role to old workspace owner
			if (bAssigned  && ownerFlag == true) {
				// assign the old workspace owner to the newly selected role.
				boolean bSuccess  = ETSWorkspaceDAO.assignRoleToOldWorkspaceOwner(con,proj.getProjectId(),own.getUserId(),roleid);
			}
			
			con.commit();
			con.setAutoCommit(true);
			
			boolean bPOCEntRequested = false;
				
			// check if the new workspace entered has MULTIPOC entitlement, if not request for one.
				
			boolean bHasPOCEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sWorkspaceOwner,Defines.MULTIPOC);
				
			if (!bHasPOCEnt) {
					
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
			out.println("<td headers=\"\" align=\"left\" ><b>New workspace owner has been assigned successfully. An email notification also has been send to the owner regarding the change.</b></td>");
			out.println("</tr>");
			if (bPOCEntRequested) {
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" ><b>Additionally, a customer Point of Contact (POC) entitlement request has been sent to the new workspace owner's manager for approval.</b></td>");
				out.println("</tr>");
			}
			out.println("</table>");
		
			out.println("<br />");
			printGreyDottedLine(out);
			
			out.println("<br />");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
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
			out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
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
			out.println("<td headers=\"\" align=\"left\" >You can request to add a sub-workspace under this current workspace. Simply submit the name you would like for your sub-workspace and an email will be sent to an " + prop.getAppName() + " administrator alerting them of your request.<br /><br /></td>");
			out.println("</tr>");
			out.println("</table>");
		
			printGreyDottedLine(out);
			out.println("<br />");

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"150\"><b><label for=\"label_sub\">Sub-workspace name:</label></b></td>");
			out.println("<td headers=\"\" align=\"left\" ><input type=\"text\" name=\"subworkspace_name\" class=\"iform\" value=\"\" style=\"width:250px\" width=\"250px\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" >&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"150\"><b><label for=\"label_owner\">Workspace owner:</label></b></td>");
			out.println("<td headers=\"\" align=\"left\" >");
			
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
			out.println("<td headers=\"\" colspan=\"2\" >&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"150\" valign=\"top\"><b><label for=\"label_desc\">Description:</label></b></td>");
			out.println("<td headers=\"\" align=\"left\" ><textarea name=\"description\" id=\"label_desc\" cols=\"39\" rows=\"5\"  class=\"iform\" maxlength=\"1000\"></textarea></td>");
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<br />");
			
			printGreyDottedLine(out);
			out.println("<br />");
			out.println("<br />");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" id=\"label_cont\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" border=\"0\" /></td>");
			out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Manage this workspace</span></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
	
		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" id=\"label_mge\" value=\"delete-confirm\" />");
			
		printGreyDottedLine(out);
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" ><b>Your request for creation of sub-workspace has been sent successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");
		
	}

	private void printGreyDottedLine(PrintWriter out) {
		
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

	/**
	 * 
	 */
	private void showWorkspaceNameChange(String error) throws SQLException, Exception {
		
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
	
	
		String workspaceName = ETSUtils.checkNull(this.params.getRequest().getParameter("ws_name"));
		String workspaceDesc = ETSUtils.checkNull(this.params.getRequest().getParameter("ws_desc"));
		
		if (error.equalsIgnoreCase("")) {
			workspaceName = proj.getName();
			workspaceDesc = proj.getDescription();
		}
	
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Change workspace name</span></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
		
		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"change_ws_name_confirm\" />");
			
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >Please enter the new workspace name below and click on <b>Submit</b>.<br /><br /></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >Fields marked with <span style=\"color:#ff3333\">*</span> are mandatory fields.<br /></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
		printGreyDottedLine(out);
		out.println("<br />");
		

		
		if (!error.trim().equalsIgnoreCase("")) {
				
			//out.println("<br />");
				
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + error.toString() + "</span></td>");
			out.println("</tr>");
			out.println("</table>");
				
			out.println("<br />");
				
			printGreyDottedLine(out);
			
			out.println("<br />");
				
		}
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"160\"><b><label for=\"label_ws_name\"><span style=\"color:#ff3333\">*</span>&nbsp;Workspace name:</label></b></td>");
		out.println("<td headers=\"\" align=\"left\" ><input type=\"text\" class=\"iform\" name=\"ws_name\" id=\"label_ws_name\" style=\"width:250px\" maxlength=\"128\" width=\"250px\" value=\"" + workspaceName + "\" /></td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"160\"><b><label for=\"label_ws_desc\">&nbsp;Workspace description:</label></b></td>");
		out.println("<td headers=\"\" align=\"left\" ><input type=\"text\" class=\"iform\" name=\"ws_desc\" id=\"label_ws_desc\" style=\"width:250px\" maxlength=\"1024\" width=\"250px\" value=\"" + workspaceDesc + "\" /></td>");
		out.println("</tr>");

		out.println("</table>");
	
		out.println("<br />");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");
		
	}

	/**
	 * @return
	 */
	private String validateChangeWorkspaceName() throws Exception {
		
		StringBuffer error = new StringBuffer("");
		
		String workspaceName = ETSUtils.checkNull(this.params.getRequest().getParameter("ws_name"));
		 
		if (workspaceName.equalsIgnoreCase("")) {
			error.append("Workspace name cannot be empty. Please enter the new workspace name.<br />");
		}
		
		if (!workspaceName.equalsIgnoreCase(this.params.getETSProj().getName())) { 
			if (ETSWorkspaceDAO.checkIfWorkspaceNameExists(this.params.getConnection(),workspaceName)) {
				error.append("A workspace with this name already exists, please enter the new workspace name.<br />");
			}
		}
		
		return error.toString();
	}

	/**
	 * 
	 */
	private void updateWorkspaceNameChange() throws Exception {
		
		PrintWriter out = this.params.getWriter();
		ETSProj proj = this.params.getETSProj();
		
		String workspaceName = ETSUtils.checkNull(this.params.getRequest().getParameter("ws_name"));
		String workspaceDesc = ETSUtils.checkNull(this.params.getRequest().getParameter("ws_desc"));

		boolean success = ETSWorkspaceDAO.updateWorkspaceName(this.params.getConnection(),proj.getProjectId(),workspaceName, workspaceDesc);
		
		if (success) {
			ETSSendWorkspaceEvent wrkspcEvent = new ETSSendWorkspaceEvent();
			wrkspcEvent.sendWrkspaceEvent(this.params.getConnection(),proj.getProjectId(),"Update Workspace Event");
		}
		
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Change workspace name</span></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		
		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"change_ws_name_confirm\" />");		

		printGreyDottedLine(out);
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >");
		
		if (success) {
			out.println("<b>The workspace name and description has been changed successfully.</b>");
		} else { 
			out.println("<span style=\"color:#ff3333\"><b>There was a problem in changing the workspace name and description. Please try again. If the problem persists, please contact your system administrator.</b></span>");
			
		}
			
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");

		
	}

	/**
	 * 
	 */
	private void showGeographyAndOther(String error) throws SQLException, Exception {
		
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		String sDelTeam = request.getParameter("delivery");
		if (sDelTeam == null || sDelTeam.trim().equals("")) {
			if (error.equalsIgnoreCase("")) {
				sDelTeam = proj.getDeliveryTeam();
			} else {
				sDelTeam = "";
			}
		} else {
			sDelTeam = sDelTeam.trim();
		}

		String sGeoList = request.getParameter("geography");
		if (sGeoList == null || sGeoList.trim().equals("")) {
			if (error.equalsIgnoreCase("")) {
				sGeoList = proj.getGeography();
			} else {
				sGeoList = "";
			}
		} else {
			sGeoList = sGeoList.trim();
		}

		String sIndList = request.getParameter("industry");
		if (sIndList == null || sIndList.trim().equals("")) {
			if (error.equalsIgnoreCase("")) {
				sIndList = proj.getIndustry();
			} else {
				sIndList = "";
			}
		} else {
			sIndList = sIndList.trim();
		}
			
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Update geography, delivery team and industry</span></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
		
		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"change_ws_geo_confirm\" />");
			
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >Please select the geography, delivery team and industry below and click on <b>Submit</b>.<br /><br /></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >Fields marked with <span style=\"color:#ff3333\">*</span> are mandatory fields.<br /></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
		printGreyDottedLine(out);
		out.println("<br />");
		
		if (!error.trim().equalsIgnoreCase("")) {
				
			//out.println("<br />");
				
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + error.toString() + "</span></td>");
			out.println("</tr>");
			out.println("</table>");
				
			out.println("<br />");
				
			printGreyDottedLine(out);
			
			out.println("<br />");
				
		}
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"160\"><b><label for=\"label_geo\"><span style=\"color:#ff3333\">*</span>&nbsp;Geography:</label></b></td>");
		out.println("<td headers=\"\" align=\"left\" >");
		
		out.println("<select style=\"width:200px\" width=\"200px\" name=\"geography\" id=\"label_geo\" class=\"iform\">");
	
		out.println("<option value=\"\" >Select a geography</option>");
			
		Vector vGeo = getValues(con,Defines.GEOGRAPHY);
	
		if (vGeo != null && vGeo.size() > 0) {
	
			for (int i = 0; i < vGeo.size(); i++) {
	
				String sGeo = (String) vGeo.elementAt(i);
	
				if (sGeoList.equalsIgnoreCase(sGeo))  {
					out.println("<option value=\"" + sGeo + "\" selected=\"selected\">" + sGeo + "</option>");
				} else {
					out.println("<option value=\"" + sGeo + "\" >" + sGeo + "</option>");
				}
			}
		}
	
		out.println("</select>");		
		out.println("</td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
		out.println("</tr>");
		
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"160\"><b><label for=\"label_del\"><span style=\"color:#ff3333\">*</span>&nbsp;Delivery team:</label></b></td>");
		out.println("<td headers=\"\" align=\"left\" >");
		
		out.println("<select style=\"width:200px\" width=\"200px\" name=\"delivery\" id=\"label_del\" class=\"iform\">");
	
		out.println("<option value=\"\" >Select a delivery team</option>");
			
		Vector vDelivery = getValues(con,Defines.DELIVERY_TEAM);
	
		if (vDelivery != null && vDelivery.size() > 0) {
	
			for (int i = 0; i < vDelivery.size(); i++) {
	
				String sDelivery = (String) vDelivery.elementAt(i);
	
				if (sDelTeam.equalsIgnoreCase(sDelivery))  {
					out.println("<option value=\"" + sDelivery + "\" selected=\"selected\">" + sDelivery + "</option>");
				} else {
					out.println("<option value=\"" + sDelivery + "\" >" + sDelivery + "</option>");
				}
			}
		}
	
		out.println("</select>");		
		out.println("</td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"160\"><b><label for=\"label_ind\"><span style=\"color:#ff3333\">*</span>&nbsp;Industry:</label></b></td>");
		out.println("<td headers=\"\" align=\"left\" >");
		
		out.println("<select style=\"width:200px\" width=\"200px\" name=\"industry\" id=\"label_ind\" class=\"iform\">");
	
		out.println("<option value=\"\" >Select an industry</option>");
			
		Vector vIndustry = getValues(con,Defines.INDUSTRY);
	
		if (vIndustry != null && vIndustry.size() > 0) {
	
			for (int i = 0; i < vIndustry.size(); i++) {
	
				String sValue = (String) vIndustry.elementAt(i);
	
				if (sIndList.equalsIgnoreCase(sValue))  {
					out.println("<option value=\"" + sValue + "\" selected=\"selected\">" + sValue + "</option>");
				} else {
					out.println("<option value=\"" + sValue + "\" >" + sValue + "</option>");
				}
			}
		}
	
		out.println("</select>");		
		out.println("</td>");
		out.println("</tr>");

		out.println("</table>");
	
		out.println("<br />");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");
		
	}

	/**
	 * 
	 */
	private String validateGeographyAndOther() throws SQLException, Exception {
		
		StringBuffer error = new StringBuffer("");
		
		HttpServletRequest request = this.params.getRequest();

		String sGeoList = request.getParameter("geography");
		if (sGeoList == null || sGeoList.trim().equals("")) {
			error.append("Please select a value for Geography.<br />");
		}
	
		String sDelTeam = request.getParameter("delivery");
		if (sDelTeam == null || sDelTeam.trim().equals("")) {
			error.append("Please select a value for Delivery team.<br />");
		}
	
		String sIndList = request.getParameter("industry");
		if (sIndList == null || sIndList.trim().equals("")) {
			error.append("Please select a value for Industry.<br />");
		}

		return error.toString();			
	
	}

	/**
	 * 
	 */
	private void updateGeographyAndOther() throws Exception {
		
		PrintWriter out = this.params.getWriter();
		ETSProj proj = this.params.getETSProj();
		HttpServletRequest request = this.params.getRequest();
		
		
		String sGeoList = ETSUtils.checkNull(request.getParameter("geography"));
	
		String sDelTeam = ETSUtils.checkNull(request.getParameter("delivery"));
	
		String sIndList = ETSUtils.checkNull(request.getParameter("industry"));
	
		boolean success = ETSWorkspaceDAO.updateGeoAndOther(this.params.getConnection(),proj.getProjectId(),sGeoList, sDelTeam, sIndList);
		
		if (success) {
			ETSSendWorkspaceEvent wrkspcEvent = new ETSSendWorkspaceEvent();
			wrkspcEvent.sendWrkspaceEvent(this.params.getConnection(),proj.getProjectId(),"Update Workspace Event");
		}
		
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Update geography, delivery team and industry</span></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		
		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"change_ws_geo_confirm\" />");		
	
		printGreyDottedLine(out);
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" >");
		
		if (success) {
			out.println("<b>The geography, delivery team and industry has been updated successfully.</b>");
		} else { 
			out.println("<span style=\"color:#ff3333\"><b>There was a problem in changing geography, delivery team and industry. Please try again. If the problem persists, please contact your system administrator.</b></span>");
			
		}
			
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");
	
		
	}

	/**
	 * 
	 */
	private void changeWorkspaceTabs(String sError) throws SQLException, Exception {
		
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
	
		boolean bITAR = proj.isITAR();
	
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
		
		String sTabAsic = request.getParameter("tab_asic");
		if (sTabAsic == null || sTabAsic.trim().equalsIgnoreCase("")) {
			sTabAsic = "N";
		} else {
			sTabAsic = sTabAsic.trim();
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

		String sTabSelf = request.getParameter("tab_self");
		if (sTabSelf == null || sTabSelf.trim().equalsIgnoreCase("")) {
			sTabSelf = "N";
		} else {
			sTabSelf = sTabSelf.trim();
		}

		String sTabSurvey = request.getParameter("tab_survey");
		if (sTabSurvey == null || sTabSurvey.trim().equalsIgnoreCase("")) {
			sTabSurvey = "N";
		} else {
			sTabSurvey = sTabSelf.trim();
		}

		String sTabFeedback = request.getParameter("tab_feedback");
		if (sTabFeedback == null || sTabFeedback.trim().equalsIgnoreCase("")) {
			sTabFeedback = "N";
		} else {
			sTabFeedback = sTabSelf.trim();
		}


		boolean displaysubmit = false;
		
		Vector graphTabs = ETSDatabaseManager.getTopCats(proj.getProjectId());
					
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Change workspace tabs</span></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
	
		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"change_ws_tabs_confirm\" />");
			
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		if (proj.getProjectOrProposal().equalsIgnoreCase("C")) {
			if (graphTabs.size() == 5) {
				out.println("<td headers=\"\" align=\"left\" >This workspace already has all the tabs that are currently allowed for a Client Voice workspace.</td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" >Please select the tabs you would like to be included in this workspace and click on <b>Submit</b>. <br /><br /><b>NOTE:</b> Each workspace can currently support a maximum of five (5) tabs only.</td>");
			}
		} else {
			if (proj.isITAR() && graphTabs.size() == 4) {
				out.println("<td headers=\"\" align=\"left\" >This workspace already has all the tabs that are currently allowed for an ITAR workspace.</td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" >Please select the tabs you would like to be included in this workspace and click on <b>Submit</b>. <br /><br /><b>NOTE:</b> Each workspace can currently support a maximum of five (5) tabs only.</td>");
			}
		}
		
		out.println("</tr>");
		out.println("</table>");
	
		printGreyDottedLine(out);
		out.println("<br />");
		
		if (!sError.trim().equals("")) {
				
			out.println("<br />");
				
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + sError + "</span></td>");
			out.println("</tr>");
			out.println("</table>");
				
			out.println("<br />");
				
			printGreyDottedLine(out);
			
			out.println("<br />");
				
		}
		
		out.println("<br />");
		
		boolean documents = false;
		boolean issues = false;
		boolean contracts = false;
		boolean meetings = false;
		boolean asic = false;
		boolean self = false;
		boolean survey = false;
		boolean feedback = false;
		
		
		// display the graph tabs for selection along with the project related tabs.
		
		
		
		 
		String projectType = proj.getProjectOrProposal();
		//O = proposal, P = project and C = Client Voice

		for (int i = 0; i < graphTabs.size(); i++) {
		
			ETSCat cat = (ETSCat) graphTabs.elementAt(i);
		
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			if (cat.getViewType() == Defines.MAIN_VT) {
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Main\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Main</b><input type=\"hidden\" name=\"tab_main\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.MEETINGS_VT) {
				meetings = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Meetings\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Meetings</b><input type=\"hidden\" name=\"tab_meetings\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.CONTRACTS_VT) {
				contracts = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Contracts\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Contracts</b><input type=\"hidden\" name=\"tab_contracts\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.DOCUMENTS_VT) {
				documents = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Documents\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Documents</b><input type=\"hidden\" name=\"tab_documents\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.ASIC_VT) {
				asic = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"ASIC\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;ASIC</b><input type=\"hidden\" name=\"tab_asic\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.ISSUES_CHANGES_VT) {
				issues = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Issues / Changes\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Issues / Changes</b><input type=\"hidden\" name=\"tab_issues\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.SETMET_VT) {
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Set / Met Reviews\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Set / Met Reviews</b><input type=\"hidden\" name=\"tab_setmet\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.SELF_ASSESSMENT_VT) {
				self = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\"><b>&nbsp;</b></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Self Assessment\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Self Assessment</b><input type=\"hidden\" name=\"tab_self\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.SURVEY_VT) {
				survey = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\"><b>&nbsp;</b></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Survey\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Survey</b><input type=\"hidden\" name=\"tab_survey\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.FEEDBACK_VT) {
				feedback = true;
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\"><b>&nbsp;</b></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Feedback\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Feedback</b><input type=\"hidden\" name=\"tab_feedback\" value=\"Y\" /></td>");
				out.println("</tr>");
			} else if (cat.getViewType() == Defines.TEAM_VT) {
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" ><b>&nbsp;Team</b><input type=\"hidden\" name=\"tab_team\" value=\"Y\" /></td>");
				out.println("</tr>");
			}
			out.println("</table>");
		}

		if (projectType.equalsIgnoreCase("O")) {
			if (!contracts && !bITAR) {
				displaysubmit = true;
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabContracts.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_contracts\" value=\"Y\" id=\"label_contract\" selected=\"selected\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_contracts\" value=\"Y\" id=\"label_contract\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_contract\">Contracts</label></b></td>");
				out.println("</tr>");
				out.println("</table>");
			}
		}

		if (projectType.equalsIgnoreCase("P")) {
			if (!meetings) {
				displaysubmit = true;
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabContracts.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meetings\" selected=\"selected\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meetings\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_meetings\">Meetings</label></b></td>");
				out.println("</tr>");
				out.println("</table>");
			}
		}
					
		if (!projectType.equalsIgnoreCase("C")) {				
			if (!documents) {
				displaysubmit = true;
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabDocuments.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_documents\" selected=\"selected\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_documents\" /></td>");
				}
			
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_documents\">Documents</label></b></td>");
				out.println("</tr>");
				out.println("</table>");
			}

			if (!asic && !bITAR) {
				displaysubmit = true;
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabAsic.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" selected=\"selected\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_asic\">ASIC</label></b></td>");
				out.println("</tr>");
				out.println("</table>");
			}

		
			if (!issues) {
				displaysubmit = true;
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabIssues.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_issues\" selected=\"selected\" checked=\"checked\" /></td>");	
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_issues\" /></td>");
				}
			
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_issues\">Issues/Changes</label></b></td>");
				out.println("</tr>");
				out.println("</table>");
			}
		} else {
			// client voice
			if (!self) {
				displaysubmit = true;
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabSelf.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_self\" value=\"Y\" id=\"label_self\" selected=\"selected\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_self\" value=\"Y\" id=\"label_self\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_self\">Self Assessment</label></b></td>");
				out.println("</tr>");
				out.println("</table>");
			}

			if (!survey) {
				displaysubmit = true;
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabSurvey.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_survey\" value=\"Y\" id=\"label_survey\" selected=\"selected\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_survey\" value=\"Y\" id=\"label_survey\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_survey\">Survey</label></b></td>");
				out.println("</tr>");
				out.println("</table>");
			}

			if (!feedback) {
				displaysubmit = true;
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"130\">&nbsp;</td>");
				if (sTabFeedback.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_feedback\" value=\"Y\" id=\"label_feedback\" selected=\"selected\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" align=\"left\" width=\"16\"><input type=\"checkbox\" name=\"tab_feedback\" value=\"Y\" id=\"label_feedback\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_feedback\">Feedback</label></b></td>");
				out.println("</tr>");
				out.println("</table>");
			}

		}
	
		out.println("<br />");
			
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		if (displaysubmit) {
			out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" border=\"0\" /></td>");
		}
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=manage_wspace&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");
				
		out.println("</form>");
		
	}

	/**
	 * 
	 */
	private String validateChangeWorkspaceTabs() throws SQLException, Exception {

		StringBuffer error = new StringBuffer("");		
		HttpServletRequest request = this.params.getRequest();
		
		int tabCount = 0;

		String sTabMain = request.getParameter("tab_main");
		if (sTabMain == null || sTabMain.trim().equalsIgnoreCase("")) {
			sTabMain = "N";
		} else {
			tabCount = tabCount + 1;
		}
		String sTabMeetings = request.getParameter("tab_meetings");
		if (sTabMeetings == null || sTabMeetings.trim().equalsIgnoreCase("")) {
			sTabMeetings = "N";
		} else {
			tabCount = tabCount + 1;
		}
		String sTabContracts = request.getParameter("tab_contracts");
		if (sTabContracts == null || sTabContracts.trim().equalsIgnoreCase("")) {
			sTabContracts = "N";
		} else {
			tabCount = tabCount + 1;
		}
		String sTabDocuments = request.getParameter("tab_documents");
		if (sTabDocuments == null || sTabDocuments.trim().equalsIgnoreCase("")) {
			sTabDocuments = "N";
		} else {
			tabCount = tabCount + 1;
		}
		
		String sTabAsic = request.getParameter("tab_asic");
		if (sTabAsic == null || sTabAsic.trim().equalsIgnoreCase("")) {
			sTabAsic = "N";
		} else {
			tabCount = tabCount + 1;
		}
		
		String sTabIssues = request.getParameter("tab_issues");
		if (sTabIssues == null || sTabIssues.trim().equalsIgnoreCase("")) {
			sTabIssues = "N";
		} else {
			tabCount = tabCount + 1;
		}
		String sTabTeam = request.getParameter("tab_team");
		if (sTabTeam == null || sTabTeam.trim().equalsIgnoreCase("")) {
			sTabTeam = "N";
		} else {
			tabCount = tabCount + 1;
		}
	
		String sTabSelf = request.getParameter("tab_self");
		if (sTabSelf == null || sTabSelf.trim().equalsIgnoreCase("")) {
			sTabSelf = "N";
		} else {
			tabCount = tabCount + 1;
		}
	
		String sTabSurvey = request.getParameter("tab_survey");
		if (sTabSurvey == null || sTabSurvey.trim().equalsIgnoreCase("")) {
			sTabSurvey = "N";
		} else {
			tabCount = tabCount + 1;
		}
	
		String sTabFeedback = request.getParameter("tab_feedback");
		if (sTabFeedback == null || sTabFeedback.trim().equalsIgnoreCase("")) {
			sTabFeedback = "N";
		} else {
			tabCount = tabCount + 1;
		}
	
		if (tabCount > 5) {
			error.append("Please do not select more than five (5) tabs. Currently, all workspaces can only support a maximum of five (5) tabs.<br />");
		}
	
		return error.toString();
		
	}

	/**
	 * 
	 */
	private void changeWorkspaceTabsFinal() throws SQLException, Exception {
		
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
	
	
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
		
		String sTabAsic = request.getParameter("tab_asic");
		if (sTabAsic == null || sTabAsic.trim().equalsIgnoreCase("")) {
			sTabAsic = "N";
		} else {
			sTabAsic = sTabAsic.trim();
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

		String sTabSelf = request.getParameter("tab_self");
		if (sTabSelf == null || sTabSelf.trim().equalsIgnoreCase("")) {
			sTabSelf = "N";
		} else {
			sTabSelf = sTabSelf.trim();
		}

		String sTabSurvey = request.getParameter("tab_survey");
		if (sTabSurvey == null || sTabSurvey.trim().equalsIgnoreCase("")) {
			sTabSurvey = "N";
		} else {
			sTabSurvey = sTabSelf.trim();
		}

		String sTabFeedback = request.getParameter("tab_feedback");
		if (sTabFeedback == null || sTabFeedback.trim().equalsIgnoreCase("")) {
			sTabFeedback = "N";
		} else {
			sTabFeedback = sTabSelf.trim();
		}
	
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\" height=\"18\" ><span class=\"subtitle\">Change workspace tabs</span></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
	
		out.println("<form name=\"ManageWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
	
		out.println("<input type=\"hidden\" name=\"etsop\" id=\"label_etsop\" value=\"manage_wspace\" />");	
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"manage\" value=\"change_ws_tabs_confirm\" />");
		
			
		printGreyDottedLine(out);
		out.println("<br />");
		
		try {
			
			// get the graph tabs for this proposal.
			Vector graphTabs = ETSDatabaseManager.getTopCats(proj.getProjectId());
			
			con.setAutoCommit(false);
			
			int iOrderType = 20;		// 10 is for main (or setmet) which is already there.
	
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
			}
			
			if (sTabAsic.trim().equalsIgnoreCase("Y")) {
				// asic tab has been selected.
				// check if issues already exists
				boolean bExists = false;
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.ASIC_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						bExists = true;
						iOrderType = iOrderType + 10;
					}
				}
				if (!bExists) {
					// create this tab.
					ETSWorkspaceDAO.createTab(con,proj.getProjectId(),Defines.ASIC_VT,iOrderType,es.gIR_USERN);
					iOrderType = iOrderType + 10;
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
			}

			if (sTabSelf.trim().equalsIgnoreCase("Y")) {
				// issues tab has been selected.
				// check if issues already exists
				boolean bExists = false;
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.SELF_ASSESSMENT_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						bExists = true;
						iOrderType = iOrderType + 10;
					}
				}
				if (!bExists) {
					// create this tab.
					ETSWorkspaceDAO.createTab(con,proj.getProjectId(),Defines.SELF_ASSESSMENT_VT,iOrderType,es.gIR_USERN);
					iOrderType = iOrderType + 10;
				}
			}

			if (sTabSurvey.trim().equalsIgnoreCase("Y")) {
				// issues tab has been selected.
				// check if issues already exists
				boolean bExists = false;
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.SURVEY_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						bExists = true;
						iOrderType = iOrderType + 10;
					}
				}
				if (!bExists) {
					// create this tab.
					ETSWorkspaceDAO.createTab(con,proj.getProjectId(),Defines.SURVEY_VT,iOrderType,es.gIR_USERN);
					iOrderType = iOrderType + 10;
				}
			}

			if (sTabFeedback.trim().equalsIgnoreCase("Y")) {
				// issues tab has been selected.
				// check if issues already exists
				boolean bExists = false;
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.FEEDBACK_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						bExists = true;
						iOrderType = iOrderType + 10;
					}
				}
				if (!bExists) {
					// create this tab.
					ETSWorkspaceDAO.createTab(con,proj.getProjectId(),Defines.FEEDBACK_VT,iOrderType,es.gIR_USERN);
					iOrderType = iOrderType + 10;
				}
			}
			
	
			if (sTabTeam.trim().equalsIgnoreCase("Y")) {
				// team tab has been selected.
				// check if team already exists
				for (int i = 0; i < graphTabs.size(); i++) {
					ETSCat cat = (ETSCat) graphTabs.elementAt(i);
					if (cat.getViewType() == Defines.TEAM_VT) {
						// update the view order type..
						ETSWorkspaceDAO.updateTabOrderType(con,proj.getProjectId(),cat.getId(),iOrderType,es.gIR_USERN);
						iOrderType = iOrderType + 10;
					}
				}
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
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" ><b>Workspace tabs has been changed successfully.</b></td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br />");
		printGreyDottedLine(out);
		
		out.println("<br />");
		
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");
		
	}

	/**
	 * @param con
	 * @return
	 */
	private Vector getValues(Connection con, String sType) throws SQLException, Exception {
			
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
			
		Vector vValues = new Vector();
			
		try {
				
			sQuery.append("SELECT DISTINCT INFO_LINK, INFO_MODULE FROM ETS.ETS_PROJECT_INFO WHERE PROJECT_ID = '" + sType + "' AND INFO_TYPE = '" + Defines.GEO_INFO_TYPE + "' ORDER BY INFO_MODULE for READ ONLY");
				
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
				
			while (rs.next()) {
					
				String sValue = rs.getString("INFO_LINK");
					
				vValues.addElement(sValue);
					
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
			
		return vValues;
	}
}

