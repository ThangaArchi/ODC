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
import java.net.URLEncoder;
import java.sql.Connection;
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

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSMail;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSSyncUser;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.workflow.util.UserUtils;
import oem.edge.ets.fe.wspace.ETSSendWorkspaceEvent;
import oem.edge.ets.fe.wspace.ETSWorkspaceDAO;
import oem.edge.ets.fe.aic.AICWorkspaceDAO;
import oem.edge.ets.fe.aic.wspace.DecafUtilsImpl;


public class AICCreateWorkspaceServlet extends HttpServlet {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.5";


	//private static Log logger = EtsLogger.getLogger(AICCreateWorkspaceServlet.class);

	private static final String BUNDLE_NAME = "oem.edge.ets.fe.aic.AICResources";
	private static final ResourceBundle aic_rb = ResourceBundle.getBundle(BUNDLE_NAME);
	private static final String APPLICATION_NAME = aic_rb.getString("aic.AICApplicationName");
	private static final String LANDING_PAGE = aic_rb.getString("aic.AICLandingPage");


	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		Connection conn = null;
		String Msg = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();

		Hashtable params;

		StringBuffer sHeader = new StringBuffer("");

		String sLink;

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
			String sDate = df.format(new Date());


			conn = ETSDBUtils.getConnection();
			if (!es.GetProfile(response, request, conn)) {
				return;
			}

			Hashtable hs = ETSUtils.getServletParameters(request);

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", prop.getLinkID());
				sLink = prop.getLinkID();
			}

			// if not superadmin then display error
			//if (!es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT, "tg_member=MD")) {
			//	response.sendRedirect("ETSConnectServlet.wss");
			//	return;
			//}

			//check for external User
			boolean bIBMer = ETSUtils.isIBMer(es.gIR_USERN,conn);
			if (!bIBMer) {
				response.sendRedirect("AICConnectServlet.wss");
				return;
			}

			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, conn, hs);
			EdgeHeader.setPopUp("J");

			ETSParams parameters = new ETSParams();
			parameters.setConnection(conn);
			parameters.setEdgeAccessCntrl(es);
			parameters.setRequest(request);
			parameters.setResponse(response);
			parameters.setWriter(out);
			parameters.setLinkId(sLink);

			String sOp = request.getParameter("op");
			if (sOp == null || sOp.trim().equals("")) {
				sOp = "";
			} else {
				sOp = sOp.trim();
			}
			String sITAR = request.getParameter("itar");
			if (sITAR == null || sITAR.trim().equals("")) {
				sITAR = "";
			} else {
				sITAR = sITAR.trim();
			}



			EdgeHeader.setPageTitle(prop.getAppName() + " - Create a new workspace");
			EdgeHeader.setHeader("Create a Collaboration Center Workspace");

			if (sOp.trim().equals("")) {
				EdgeHeader.setSubHeader("Identify workspace type");
			} else if (sOp.trim().equals("second")) {
				if (validateFirstStep(parameters) == false) {
					EdgeHeader.setSubHeader("Identify workspace type");
				} else {
					EdgeHeader.setSubHeader("Enter workspace details");
				}
			} else if (sOp.trim().equals("subsector")) {
				String sError = validateFinalStep(parameters);
				if (!sError.trim().equalsIgnoreCase("")) {
					EdgeHeader.setSubHeader("Enter workspace details");
				} else {
					EdgeHeader.setSubHeader("Enter Workspace Sub-sector details");
				}
			} else if (sOp.trim().equals("confirm")) {
				String sError = validateSubSectorStep(parameters);
				if (!sError.trim().equalsIgnoreCase("")) {
					EdgeHeader.setSubHeader("Enter Workspace Sub-sector details");
				} else if (sITAR.trim().equalsIgnoreCase("Y")) {
					EdgeHeader.setSubHeader("ITAR workspace compliance");
				} else {
					EdgeHeader.setSubHeader("Workspace creation confirmation");
				}
			} else if (sOp.trim().equals("itarfinal")) {
				// if it comes here, it means it went to the itar creation page...
				String sError = validateITARStep(parameters);
				if (!sError.trim().equalsIgnoreCase("")) {
					EdgeHeader.setSubHeader("ITAR workspace compliance");
				} else {
					EdgeHeader.setSubHeader("Workspace creation confirmation");
				}
			}

			out.println(EdgeHeader.printBullsEyeHeader());
			out.println(EdgeHeader.printBullsEyeLeftNav());
			out.println(EdgeHeader.printSubHeader());

			out.println("<form name=\"CreateWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "AICCreateWorkspaceServlet.wss" + "\">");

			//script to make IBM only checkbox selected and disabled when public WS is selected.
			out.println("<script type=\"text/javascript\" language=\"javascript\">");
			out.println("function publicWSFunc(){");
			out.println("document.CreateWorkspace.chk_ibmonly.checked=true;");
			out.println("document.CreateWorkspace.chk_ibmonly.disabled=true;");
			out.println("}");

			//script to make IBM only unchecked  & disables for teamroom wrkspace
			out.println("function teamroomWSFunc(){");
			out.println("document.CreateWorkspace.chk_ibmonly.checked=false;");
			out.println("document.CreateWorkspace.chk_ibmonly.disabled=true;");
			out.println("}");

			out.println("function privateWSFunc(){");
			out.println("document.CreateWorkspace.chk_ibmonly.disabled=false;");
			out.println("document.CreateWorkspace.chk_ibmonly.checked=false;");
			out.println("}</script>");

			//top table to define the content and right sides..
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			//out.println("<tr valign=\"top\"><td headers=\"td_log\" width=\"443\" valign=\"top\" class=\"small\"><b>You are logged in as:</b> " + es.gIR_USERN + "</td>");
			out.println("<tr valign=\"top\"><td headers=\"td_user\" width=\"443\" valign=\"top\" class=\"small\"><table summary=\"\" width=\"100%\"><tr><td headers=\"td_username\" width=\"60%\">" + es.gIR_USERN + "</td><td headers=\"td_sdate\" width=\"40%\" align=\"right\">" + sDate + "</td></tr></table></td>");
			out.println("<td headers=\"td_img1\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			out.println("<td headers=\"td_img2\" class=\"small\" align=\"right\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"td_img3\" width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"td_img4\" headers=\"\" width=\"90\" align=\"right\">Secure content</td></tr></table></td></tr>");
			out.println("</table>");

			printGreyDottedLine(out);



			if (sOp.trim().equals("")) {
				displayFirstStep(parameters,false);
			} else if (sOp.trim().equals("second")) {
				if (validateFirstStep(parameters) == false) {
					displayFirstStep(parameters,true);
				} else {
					displayFinalStep(parameters,"");
				}
			} else if (sOp.trim().equals("subsector")) {
				String sError = validateFinalStep(parameters);
				if (!sError.trim().equalsIgnoreCase("")) {
					displayFinalStep(parameters,sError);
				} else {
					displaySubSectorDetails(parameters,"");
				}

			} else if (sOp.trim().equals("confirm")) {
				String sError = validateSubSectorStep(parameters);
				if (!sError.trim().equalsIgnoreCase("")) {
					displaySubSectorDetails(parameters,sError);
				} else {
					confirmWorkspaceCreate(parameters,"");
				}
			} else if (sOp.trim().equals("itarfinal")) {
				// if it comes here, it means it went to the itar creation page...
				String sError = validateITARStep(parameters);
				if (!sError.trim().equalsIgnoreCase("")) {
					confirmWorkspaceCreate(parameters,sError);
				} else {
					createWorkspace(parameters);
				}

			}

			out.println("</form>");
			out.println("<br /><br />");
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"td_icroot\" width=\"16\" align=\"left\"><img alt=\"protected content\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"td_secure\" align=\"left\"><span class=\"fnt\">A key icon displayed in a page indicates that the page is secure and password-protected.</span></td></tr></table>");

			out.println(EdgeHeader.printBullsEyeFooter());

		} catch (SQLException e) {
			e.printStackTrace();
			SysLog.log(SysLog.ERR, this, e);
			ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(e), "Error occurred on " + APPLICATION_NAME );
		} catch (Exception e) {
			e.printStackTrace();
			SysLog.log(SysLog.ERR, this, e);
			ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(e), "Error occurred on " + APPLICATION_NAME );
		} finally {
			ETSDBUtils.close(conn);
			out.flush();
			out.close();
		}
	}

	/**
	 * @param parameters
	 */
	private void displayFirstStep(ETSParams params, boolean bError) throws SQLException, Exception {
		/*
		 * This step displays the workspace name, company, type of workspace,
		 * and catetory of workspce
		 */

		PrintWriter out = params.getWriter();
		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();
		boolean bAdmin = false;
		boolean bOEMSales = false;
		boolean bWFAdmin = false;
		bWFAdmin = UserUtils.doesUserHaveAIC_Workflow_AdminCollabEntitlement(con,es.gIR_USERN);
        System.out.println("es.gIR_USERN&**********************&&&&&&&&&&&&&&&*************"+es.gIR_USERN + ":" +bWFAdmin);
		try {

			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,es.gIR_USERN);
			Vector userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				bAdmin = true;
			} else if (userents.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT) ||
					    userents.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT) ||
					    userents.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT)) {
				bOEMSales = true;
			}

			out.println("<input type=\"hidden\" name=\"op\" value=\"second\" />");
			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_mand_content\" width=\"100%\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
			out.println("</tr>");
			out.println("</table>");

			//printGreyDottedLine(out);

			out.println("<br />");


			String sWorkspaceCompany = request.getParameter("workspace_company");
			if (sWorkspaceCompany == null || sWorkspaceCompany.trim().equals("")) {
				sWorkspaceCompany = "";
			} else {
				sWorkspaceCompany = sWorkspaceCompany.trim();
			}

			String sWorkspaceCat = request.getParameter("workspace_cat");
			if (sWorkspaceCat == null || sWorkspaceCat.trim().equals("")) {
				sWorkspaceCat = "";
			} else {
				sWorkspaceCat = sWorkspaceCat.trim();
			}

			String sIbmOnly = request.getParameter("chk_ibmonly");
			if (sIbmOnly == null || sIbmOnly.trim().equals("")) {
				//Next line includes support for Workflow - modification by KP
				if(sWorkspaceCat.equals("Public-Workspace") || sWorkspaceCat.trim().equals("WorkFlow-Workspace") ){
					sIbmOnly = "Y";
				}else{
					sIbmOnly = "N";
				}
			} else {
				sIbmOnly = sIbmOnly.trim();
			}

			StringBuffer sError = new StringBuffer("");

			if (bError) {

				if (sWorkspaceCompany == null || sWorkspaceCompany.trim().equals("")) {
					sError.append("Workspace company cannot be empty. Please select workspace company from the list.<br />");
				}

				if (sWorkspaceCat == null || sWorkspaceCat.trim().equals("")) {
					sError.append("Workspace type cannot be empty. Please select the workspace type.<br />");
				}

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"td_createws\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			}

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"client\" width=\"150\" align=\"left\"><b>Client company:</b></td>");
			out.println("<td headers=\"wscompany\" align=\"left\"><b>" + sWorkspaceCompany + "</b><input type=\"hidden\" name=\"workspace_company\" value=\"" + sWorkspaceCompany + "\" /></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_etshelp1\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_etshelp2\" width=\"150\" align=\"left\"><b><span style=\"color:#cc6600\">*</span>Workspace type:</b></td>");
			out.println("<td>");
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" >");
			if (bAdmin || bOEMSales){
				out.println("<tr>");
				if (sWorkspaceCat.trim().equals("Public-Workspace")) {
					out.println("<td headers=\"td_salespublic1\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Public-Workspace\" selected=\"selected\" checked=\"checked\" id=\"w_cat\" onClick=\"javascript:publicWSFunc()\" /></td>");
				} else {
					out.println("<td headers=\"td_salespublic2\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Public-Workspace\" id=\"w_cat\" onClick=\"javascript:publicWSFunc()\" /></td>");
				}
				out.println("<td headers=\"col115\" align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"w_cat\"><b>Public workspace</b></label></td>");
				out.println("</tr>");
				out.println("<tr><td>&nbsp;</td><td headers=\"det\" align=\"left\">Workspace type which allows Internal Users who have one of the Sales Collab Entitlements to automatically participate as \"members\".</td></tr>");
				out.println("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
			}
			out.println("<tr>");
			if (sWorkspaceCat.trim().equals("Restricted-Workspace")) {
				out.println("<td headers=\"td_salres1\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Restricted-Workspace\" selected=\"selected\" checked=\"checked\" id=\"w_cat2\" onClick=\"javascript:privateWSFunc()\" /></td>");
			} else {
				out.println("<td headers=\"td_salres2\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Restricted-Workspace\" id=\"w_cat2\" onClick=\"javascript:privateWSFunc()\" /></td>");
			}
			out.println("<td headers=\"col11x\" align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"w_cat2\"><b>Restricted Workspace</b></label></td>");
			out.println("</tr>");
			out.println("<tr><td>&nbsp;</td><td headers=\"det\" align=\"left\">Workspace type where users are invited and managed through the Teamtab environment. For internal users, existence of Restricted workspaces can be viewed as an available workspace to allow users to request access to the workspace.  For external users, users will only see the workspace exists if they have access.</td></tr>");
			out.println("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");

			out.println("<tr>");
			if (sWorkspaceCat.trim().equals("Private-Workspace")) {
				out.println("<td headers=\"td_priv1\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Private-Workspace\" selected=\"selected\" checked=\"checked\" id=\"w_cat1\" onClick=\"javascript:privateWSFunc()\" /></td>");
			} else {
				out.println("<td headers=\"td_priv2\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Private-Workspace\" id=\"w_cat1\" onClick=\"javascript:privateWSFunc()\" /></td>");
			}
			out.println("<td headers=\"col115\" align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"w_cat1\"><b>Private Workspace</b></label></td>");
			out.println("</tr>");
			out.println("<tr><td>&nbsp;</td><td headers=\"det\" align=\"left\">Workspace type where users are invited and managed through the Teamtab environment. Existence of Private workspaces are not able to be viewed as an available workspace unless you are either a visitor, member, workspace owner or workspace manager within that workspace.</td></tr>");
			out.println("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
			out.println("<tr><td headers=\"col118\" colspan=\"2\">");
			printGreyDottedLine(out);
			out.println("</td></tr>");
			out.println("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
			//for Teamroom type workspace
			//printGreyDottedLine(out);
			out.println("<tr>");
			if (sWorkspaceCat.trim().equals("Restricted-Teamroom")) {
				out.println("<td headers=\"td_salres1\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Restricted-Teamroom\" selected=\"selected\" checked=\"checked\" id=\"w_cat3\" onClick=\"javascript:teamroomWSFunc()\" /></td>");
			} else {
				out.println("<td headers=\"td_salres2\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Restricted-Teamroom\" id=\"w_cat3\" onClick=\"javascript:teamroomWSFunc()\" /></td>");
			}
			out.println("<td headers=\"col115\" align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"w_cat3\"><b>Restricted Teamroom Workspace</b></label></td>");
			out.println("</tr>");
			out.println("<tr><td>&nbsp;</td><td headers=\"det\" align=\"left\">Workspace type where users are invited, deleted and whose access is managed by the user's POC (using E-DECAF or other appropriate business line applications).  User maintenance in this workspace type can be coordinated with other applications and data on IBM Customer Connect.  For internal users, existence of Restricted Teamroom workspaces can be viewed as an available workspace to allow users to request access to the workspace.  For external users, users will only see the workspace exists if they have access.</td></tr>");
			out.println("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
			
			out.println("<tr>");
			if (sWorkspaceCat.trim().equals("Private-Teamroom")) {
				out.println("<td headers=\"td_salres1\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Private-Teamroom\" selected=\"selected\" checked=\"checked\" id=\"w_cat4\" onClick=\"javascript:teamroomWSFunc()\" /></td>");
			} else {
				out.println("<td headers=\"td_salres2\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"Private-Teamroom\" id=\"w_cat4\" onClick=\"javascript:teamroomWSFunc()\" /></td>");
			}
			out.println("<td headers=\"col115\" align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"w_cat4\"><b>Private Teamroom Workspace</b></label></td>");
			out.println("</tr>");
			out.println("<tr><td>&nbsp;</td><td headers=\"det\" align=\"left\">Workspace type where users are invited, deleted and whose access is managed by the user's POC (using E-DECAF or other appropriate business line applications).  User maintenance in this workspace type can be coordinated with other applications and data on IBM Customer Connect.  Existence of Private Teamroom workspaces are not able to be viewed as an available workspace unless you are either a visitor, member, workspace owner or workspace manager within that workspace.  This is the recommended workspace type for usage with external users.</td></tr>");
			out.println("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
			
//------code added for Workflow by Ryazuddin on 11/10/06
			
			if (bAdmin || bOEMSales || bWFAdmin){
				out.println("<tr>");
				if (sWorkspaceCat.trim().equals("WorkFlow-Workspace")) {
					out.println("<td headers=\"td_salespublic1\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"WorkFlow-Workspace\" selected=\"selected\" checked=\"checked\" id=\"w_cat\" onClick=\"javascript:publicWSFunc()\" /></td>");
				} else {
					out.println("<td headers=\"td_salespublic2\" align=\"left\" width=\"3%\" class=\"lgray\"><input type=\"radio\" name=\"workspace_cat\" value=\"WorkFlow-Workspace\" id=\"w_cat5\" onClick=\"javascript:publicWSFunc()\" /></td>");
				}
				out.println("<td headers=\"col115\" align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"w_cat5\"><b>Sales Management Client Assessments</b></label></td>");
				out.println("</tr>");
				out.println("<tr><td>&nbsp;</td><td headers=\"det\" align=\"left\">This workspace type is designated as an \"internal only\" workspace for TCS for the purpose of creating a Client Assessment for an account. The workspace uses a defined workflow to route the assessment through the process.</td></tr>");
				out.println("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
			}
			
//--- Completed
			
			out.println("</table>");
			out.println("</td></tr></table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_wstypeclose\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			/* start of IBM-Only code */
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_wsaccess\" width=\"150\" align=\"left\">&nbsp;<b>Workspace Access:</b></td>");

			if (sWorkspaceCat.trim().equals("Public-Workspace")) {
				out.println("<td headers=\"td_ibmcheck\" align=\"left\"><input type=\"checkbox\" name=\"chk_ibmonly\" value=\"Y\" id=\"chk_ibmonly\" checked=\"checked\" disabled=\"true\" /><label for=\"chk_ibmonly\"><b>Permanently limits access to IBM Team Members Only</b></label></td>");
			}else{
				out.println("<td headers=\"td_ibmcheck\" align=\"left\"><input type=\"checkbox\" name=\"chk_ibmonly\" value=\"Y\" id=\"chk_ibmonly\" /><label for=\"chk_ibmonly\"><b>Permanently limits access to IBM Team Members Only</b></label></td>");
			}
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br />");
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"td_contbutton\" headers=\"\" width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
			out.println("<td headers=\"td_img1\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"td_img2\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"td_landingpage\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + LANDING_PAGE + "?linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr></table>");


		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param parameters
	 */
	private boolean validateFirstStep(ETSParams params) throws Exception {
		/*
		 * This step displays the workspace name, company, type of workspace,
		 * and catetory of workspce
		 */

		PrintWriter out = params.getWriter();
		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();

		try {


			String sWorkspaceCompany = request.getParameter("workspace_company");
			if (sWorkspaceCompany == null || sWorkspaceCompany.trim().equals("")) {
				return false;
			}

			String sWorkspaceCat = request.getParameter("workspace_cat");
			if (sWorkspaceCat == null || sWorkspaceCat.trim().equals("")) {
				return false;
			}

			return true;

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * @param parameters
	 */
	private void displayFinalStep(ETSParams params, String sError) throws SQLException, Exception {
		/*
		 * This step displays the workspace name, company, type of workspace,
		 * and catetory of workspce
		 */

		PrintWriter out = params.getWriter();
		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();
		boolean bAdmin = false;

		try {


			String sWorkspaceName = request.getParameter("workspace_name");
			if (sWorkspaceName == null || sWorkspaceName.trim().equals("")) {
				sWorkspaceName = "";
			} else {
				sWorkspaceName = sWorkspaceName.trim();
			}

			String sWorkspaceDesc = request.getParameter("workspace_desc");
			if (sWorkspaceDesc == null || sWorkspaceDesc.trim().equals("")) {
				sWorkspaceDesc = "";
			} else {
				sWorkspaceDesc = sWorkspaceDesc.trim();
			}

			String sWorkspaceCompany = request.getParameter("workspace_company");
			if (sWorkspaceCompany == null || sWorkspaceCompany.trim().equals("")) {
				sWorkspaceCompany = "";
			} else {
				sWorkspaceCompany = sWorkspaceCompany.trim();
			}

			String sWorkspaceCat = request.getParameter("workspace_cat");
			if (sWorkspaceCat == null || sWorkspaceCat.trim().equals("")) {
				sWorkspaceCat = "";
			} else {
				sWorkspaceCat = sWorkspaceCat.trim();
			}

			String sIbmOnly = request.getParameter("chk_ibmonly");
			if (sIbmOnly == null || sIbmOnly.trim().equals("")) {
				//Next line includes support for Workflow - modification by KP
				if(sWorkspaceCat.equals("Public-Workspace") || sWorkspaceCat.trim().equals("WorkFlow-Workspace")){
					sIbmOnly = "Y";
				}else{
					sIbmOnly = "N";
				}
			} else {
				sIbmOnly = sIbmOnly.trim();
			}

			String sWorkspaceType = "";
			String sWorkspaceFlag = "";

			if (sWorkspaceCat.equalsIgnoreCase("clientvoice")) {
				sWorkspaceType = "Client Voice";
				sWorkspaceFlag = "Main";
			} else {
				sWorkspaceType = sWorkspaceCat;
				sWorkspaceFlag = sWorkspaceCat;
			}

			String sMainWorkspace = request.getParameter("workspace_main");
			// Will have main workspace project id if any.
			if (sMainWorkspace == null || sMainWorkspace.trim().equals("")) {
				sMainWorkspace = "";
			} else {
				sMainWorkspace = sMainWorkspace.trim();
			}

			String sTabMeeting = request.getParameter("tab_meetings");
			if (sTabMeeting == null || sTabMeeting.trim().equals("")) {
				sTabMeeting = "N";
			} else {
				sTabMeeting = sTabMeeting.trim();
			}

			String sTabDocuments = request.getParameter("tab_documents");
			if (sTabDocuments == null || sTabDocuments.trim().equals("")) {
				sTabDocuments = "N";
			} else {
				sTabDocuments = sTabDocuments.trim();
			}

			String sTabIssues = request.getParameter("tab_issues");
			if (sTabIssues == null || sTabIssues.trim().equals("")) {
				sTabIssues = "N";
			} else {
				sTabIssues = sTabIssues.trim();
			}

			String sTabAsic = request.getParameter("tab_asic");
			if (sTabAsic == null || sTabAsic.trim().equals("")) {
				sTabAsic = "N";
			} else {
				sTabAsic = sTabAsic.trim();
			}
			
			String sPrimaryEmail = request.getParameter("primary_email");
			if (sPrimaryEmail == null || sPrimaryEmail.trim().equals("")) {
				sPrimaryEmail = "";
			} else {
				sPrimaryEmail = sPrimaryEmail.trim();
			}

			String sSectorList = request.getParameter("sector");
			if (sSectorList == null || sSectorList.trim().equals("")) {
				sSectorList = "";
			} else {
				sSectorList = sSectorList.trim();
			}

			String sSceSectorList = request.getParameter("sce_sector");
			if (sSceSectorList == null || sSceSectorList.trim().equals("")) {
				sSceSectorList = "";
			} else {
				sSceSectorList = sSceSectorList.trim();
			}

			String sProcessList = request.getParameter("process");
			if (sProcessList == null || sProcessList.trim().equals("")) {
				sProcessList = "";
			} else {
				sProcessList = sProcessList.trim();
			}

			String sBrandList = request.getParameter("brand");
			if (sBrandList == null || sBrandList.trim().equals("")) {
				sBrandList = "";
			} else {
				sBrandList = sBrandList.trim();
			}

			String sComments = request.getParameter("comments");
			if (sComments == null || sComments.trim().equals("")) {
				sComments = "";
			} else {
				sComments = sComments.trim();
			}

			String sITAR = request.getParameter("itar");
			if (sITAR == null || sITAR.trim().equals("")) {
				sITAR = "";
			} else {
				sITAR = sITAR.trim();
			}

		    // START FIX FOR CSR IBMCC00010835 - V2SRIKAU
			String strDataTypeOverride = (String) request.getAttribute("datatype_override");
			if (strDataTypeOverride == null) {
			    strDataTypeOverride = "";
			}
			out.println("<input type=\"hidden\" name=\"datatype_override\" value=\"" + strDataTypeOverride + "\" />");     /* changed confirm to subsector */
		    // END FIX FOR CSR IBMCC00010835 - V2SRIKAU

			out.println("<input type=\"hidden\" name=\"op\" value=\"subsector\" />");     /* changed confirm to subsector */
			out.println("<input type=\"hidden\" name=\"workspace_type\" value=\"" + sWorkspaceType + "\" />");
			out.println("<input type=\"hidden\" name=\"workspace_cat\" value=\"" + sWorkspaceCat + "\" />");
			out.println("<input type=\"hidden\" name=\"workspace_company\" value=\"" + sWorkspaceCompany + "\" />");
			out.println("<input type=\"hidden\" name=\"chk_ibmonly\" value=\"" + sIbmOnly + "\" />");


			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_fieldmar\" width=\"100%\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
			out.println("</tr>");
			out.println("</table>");

			//printGreyDottedLine(out);

			out.println("<br />");

			if (!sError.trim().equalsIgnoreCase("")) {

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"td_serror\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			} else {
				sTabDocuments = "Y";
			}


			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_scompany\" width=\"220\" align=\"left\"><b>Workspace company:</b></td>");

			out.println("<td headers=\"td_wscompany\" align=\"left\">" + sWorkspaceCompany + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"td_wstype1\" width=\"220\" align=\"left\"><b>Workspace type:</b></td>");

			out.println("<td headers=\"td_wstype2\" align=\"left\">" + sWorkspaceType + "</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_wsname1\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"td_wsname2\" width=\"210\" align=\"left\"><b><label for=\"w_name\">Workspace name</label></b></td>");

			out.println("<td headers=\"td_swsname\" align=\"left\"><input type=\"text\" name=\"workspace_name\" value=\"" + sWorkspaceName + "\" id=\"w_name\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"td_swsname1\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
			out.println("<td headers=\"td_swsname2\" width=\"210\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_des1\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_des2\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
			out.println("<td headers=\"td_des3\" width=\"210\" align=\"left\"><b><label for=\"w_desc\">Description:</label></b></td>");
			out.println("<td headers=\"td_des4\" align=\"left\"><input type=\"text\" name=\"workspace_desc\" value=\"" + sWorkspaceDesc + "\" id=\"w_desc\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_des5\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_des6\" width=\"10\" align=\"left\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");


			out.println("<td headers=\"td_des7\" width=\"210\" align=\"left\"><b><label for=\"primary_desc\">Sales Manager's IBM internet  &nbsp; &nbsp; &nbsp; e-mail address:</label></b></td>");

			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,es.gIR_USERN);
			Vector userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				bAdmin = true;
			}

			if (bAdmin) {
				out.println("<td headers=\"td_prim1\" align=\"left\" valign=\"top\"><input type=\"text\" name=\"primary_email\" value=\"" + sPrimaryEmail + "\" id=\"primary_desc\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			} else {
				out.println("<td headers=\"td_prim2\" align=\"left\" valign=\"top\"><b>" + es.gEMAIL.trim() + "</b><input type=\"hidden\" name=\"primary_email\" value=\"" + es.gEMAIL.trim() + "\" /></td>");
			}
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"td_prim3\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");



			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
//			----------Conditional check for the category "Workspace" added on 11/10/2006
			
			if (!sWorkspaceCat.equalsIgnoreCase("WorkFlow-Workspace")){	
				
				out.println("<tr>");
				out.println("<td headers=\"td_imgserv1\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"td_imgserv2\" width=\"210\" align=\"left\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"td_imgserv3\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Main\" border=\"0\" /></td>");
				out.println("<td headers=\"td_imgserv4\" align=\"left\"><b>Main</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"td_imgserv5\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"td_imgserv6\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabMeeting.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"td_chkbx11a\"width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meet\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"td_chbx21a\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meet\" /></td>");
				}
				out.println("<td headers=\"td_imgserv8\" align=\"left\"><label for=\"label_meet\"><b>Meeting</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"td_imgserv9\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"td_imgserv10\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabDocuments.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"td_chkbx11\"width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_doc\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"td_chbx21\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_doc\" /></td>");
				}
				out.println("<td headers=\"td_imgserv12\" align=\"left\"><label for=\"label_doc\"><b>Documents</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"td_asic1\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"td_asic2\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabAsic.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"td_asic3\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"td_asic4\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" /></td>");
				}
				 
				out.println("<td headers=\"td_asic5\" align=\"left\"><label for=\"label_asic\"><b>ASIC</b></label></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"td_asic6\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"td_asic7\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabIssues.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"td_chkbx1\"width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_iss\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"td_chbx2\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_iss\" /></td>");
				}
				
				out.println("<td headers=\"td_isschg\" align=\"left\"><label for=\"label_iss\"><b>Issues / Changes</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"imgserv1\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"imgserv2\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"imgserv3\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"imgserv4\" align=\"left\"><b>Team</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" colspan=\"2\"><b>NOTE:</b>&nbsp; A total of 5 tabs may be selected for a workspace</td>");
				out.println("</tr>");

				out.println("</table>");
			}// Conditional if Ends here
			
//			--------- added for WorkFlow 11/10/2006. If Workspace category is of  the category "WorkFlow" then display chkbox Accordingly.
			
			if(sWorkspaceCat.equalsIgnoreCase("WorkFlow-Workspace")){
				
			    out.println("<tr>");
				out.println("<td headers=\"imgserv1\" width=\"10\" align=\"left\" valign=\"middle\"></td>");
				out.println("<td headers=\"imgserv2\" width=\"210\" align=\"left\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"td_imgserv3\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Workflow Main\" border=\"0\" /></td>");
				out.println("<td headers=\"td_imgserv81w\" align=\"left\"><label for=\"label_tab_WorkFlowMain\"><b>WorkFlow Main</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"imgserv1a\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"imgserv2a\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"td_imgserv3\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Assessment\" border=\"0\" /></td>");
				out.println("<td headers=\"td_imgserv81a\" align=\"left\"><label for=\"label_Assessment\"><b>Assessment</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"imgserv1\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"imgserv2\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"imgserv3\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"imgserv4\" align=\"left\"><b>Team</b></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"imgserv1\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"imgserv2\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"imgserv3\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Meeting\" border=\"0\" /></td>");
				out.println("<td headers=\"imgserv4\" align=\"left\"><b>Meeting</b></td>");
				out.println("</tr>");
				
				out.println("</table>");
			
			}
//----------Ends here

			System.out.println("Workspace Type---->" + sWorkspaceFlag.trim());

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"bord1\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"bord2\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"bord3\" align=\"left\" width=\"210\"><label for=\"label_brand\"><b>Brand:</b></label></td>");
			out.println("<td headers=\"bord4\" align=\"left\" >");

			out.println("<select name=\"brand\" id=\"label_brand\" class=\"iform\">");

			out.println("<option value=\"\" >Select a brand</option>");

			Vector vBrand = getValues(con,Defines.BRAND);

			if (vBrand != null && vBrand.size() > 0) {

				for (int i = 0; i < vBrand.size(); i++) {

					String sValue = (String) vBrand.elementAt(i);

					if (sBrandList.equalsIgnoreCase(sValue))  {
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

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"proc1\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"proc2\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"proc3\" align=\"left\" width=\"210\"><label for=\"label_process\"><b>Process:</b></label></td>");
			out.println("<td headers=\"proc4\" align=\"left\" >");

//			--------- added for WorkFlow 02/11/2006 by Ryazuddin.
			if(sWorkspaceCat.equalsIgnoreCase("WorkFlow-Workspace")){
	            
					out.println("<select name=\"process\" id=\"label_process\" class=\"iform\">");

								out.println("<option value=\" Workflow \" selected=\"selected\">Workflow</option>");
								//out.println("<option value=\"" + sProcess + "\" >" + sProcess + "</option>");

					out.println("</select>");
				
				}else{
					out.println("<select name=\"process\" id=\"label_process\" class=\"iform\">");

					out.println("<option value=\"\" >Select a process</option>");
					
					Vector vProcess = getValues(con,Defines.PROCESS);

					if (vProcess != null && vProcess.size() > 0) {

						for (int i = 0; i < vProcess.size(); i++) {

							String sProcess = (String) vProcess.elementAt(i);

							if (sProcessList.equalsIgnoreCase(sProcess))  {
								out.println("<option value=\"" + sProcess + "\" selected=\"selected\">" + sProcess + "</option>");
							} else {
								out.println("<option value=\"" + sProcess + "\" >" + sProcess + "</option>");
							}
						}
					}

					out.println("</select>");
				}
//			Ends here
			
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"sce1\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"sce2\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"sce3\" align=\"left\" width=\"210\"><label for=\"label_scesect\"><b>SCE Sector:</b></label></td>");
			out.println("<td headers=\"sce4\" align=\"left\" >");

			out.println("<select name=\"sce_sector\" id=\"label_scesect\" class=\"iform\">");

			out.println("<option value=\"\" >Select a SCE Sector</option>");

			Vector vSceSect = getValues(con,Defines.SCE_SECTOR);

			if (vSceSect != null && vSceSect.size() > 0) {

				for (int i = 0; i < vSceSect.size(); i++) {

					String sSceSect = (String) vSceSect.elementAt(i);

					if (sSceSect.equalsIgnoreCase(sSceSectorList))  {
						out.println("<option value=\"" + sSceSect + "\" selected=\"selected\">" + sSceSect + "</option>");
					} else {
						out.println("<option value=\"" + sSceSect + "\" >" + sSceSect + "</option>");
					}
				}
			}

			out.println("</select>");

			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"sect1\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"sect2\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"sect3\" align=\"left\" width=\"210\"><label for=\"label_sector\"><b>Business Sector:</b></label></td>");
			out.println("<td headers=\"sect4\" align=\"left\" >");

			out.println("<select name=\"sector\" id=\"label_sector\" class=\"iform\">");

			out.println("<option value=\"\" >Select a Sector</option>");

			Vector vSect = getValues(con,Defines.SECTOR);

			if (vSect != null && vSect.size() > 0) {

				for (int i = 0; i < vSect.size(); i++) {

					String sSector = (String) vSect.elementAt(i);

					if (sSectorList.equalsIgnoreCase(sSector))  {
						out.println("<option value=\"" + sSector + "\" selected=\"selected\">" + sSector + "</option>");
					} else {
						out.println("<option value=\"" + sSector + "\" >" + sSector + "</option>");
					}
				}
			}

			out.println("</select>");

			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"listend1\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"listend2\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			printGreyDottedLine(out);
			String sCreateWSServlet = aic_rb.getString("aic.createWorkspaceServlet");
			out.println("<br />");
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"sbmtbut\" width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
			out.println("<td headers=\"cancbut\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"butroot1\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"crwsserv\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + sCreateWSServlet + "?linkid=" + params.getLinkId() + "&workspace_name=" + URLEncoder.encode(sWorkspaceName) + "&workspace_desc=" + URLEncoder.encode(sWorkspaceDesc) + "&workspace_company=" + URLEncoder.encode(sWorkspaceCompany) + "&workspace_cat=" + URLEncoder.encode(sWorkspaceCat) + "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr></table>");


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param parameters
	 */
	private String validateFinalStep(ETSParams params) throws Exception {
		/*
		 * This step displays the workspace name, company, type of workspace,
		 * and catetory of workspce
		 */

		PrintWriter out = params.getWriter();
		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();

		StringBuffer sError = new StringBuffer("");

		try {

			String sWorkspaceCat = request.getParameter("workspace_cat");
			if (sWorkspaceCat == null || sWorkspaceCat.trim().equals("")) {
				sWorkspaceCat = "";
			} else {
				sWorkspaceCat = sWorkspaceCat.trim();
			}

			String sWorkspaceType = "";
			String sWorkspaceFlag = "";

			if (sWorkspaceCat.equalsIgnoreCase("clientvoice")) {
				sWorkspaceType = "Client Voice";
				sWorkspaceFlag = "Main";
			} else {
				sWorkspaceType = sWorkspaceCat.substring(0,sWorkspaceCat.indexOf("-"));
				sWorkspaceFlag = sWorkspaceCat.substring(sWorkspaceCat.indexOf("-")+1);
			}


			String sMainWorkspace = request.getParameter("workspace_main");
			// Will have main workspace project id if any.
			if (sMainWorkspace == null || sMainWorkspace.trim().equals("")) {
				sMainWorkspace = "";
			} else {
				sMainWorkspace = sMainWorkspace.trim();
			}

			String sWorkspaceName = request.getParameter("workspace_name");
			if (sWorkspaceName == null || sWorkspaceName.trim().equals("")) {
				sWorkspaceName = "";
			} else {
				sWorkspaceName = sWorkspaceName.trim();
			}

			if (sWorkspaceName.trim().equals("")) {
				sError.append("<b>Workspace name</b> cannot be empty.<br />");
			} else if (sWorkspaceName.trim().indexOf(",") >= 0) {
				sError.append("<b>Workspace name</b> cannot contain comma (\",\").<br />");
			}

			String sPrimaryEmail = request.getParameter("primary_email");
			if (sPrimaryEmail == null || sPrimaryEmail.trim().equals("")) {
				sPrimaryEmail = "";
			} else {
				sPrimaryEmail = sPrimaryEmail.trim();
			}

			if (sPrimaryEmail.trim().equals("")) {
				sError.append("<b>Project manager's IBM internet e-mail address</b> cannot be empty.<br />");
			}
			if (!sPrimaryEmail.trim().equals("")) {
				if (!sPrimaryEmail.trim().toLowerCase().endsWith("ibm.com")) {
					sError.append("<b>Project manager's IBM internet e-mail address</b> has to be IBM employee.<br />");
				}
			}



			int iTabCount = 2; // by default the main and team is available.

			String sTabMeeting = request.getParameter("tab_meetings");
			if (sTabMeeting == null || sTabMeeting.trim().equals("")) {
				sTabMeeting = "N";
			} else {
				iTabCount = iTabCount + 1;
			}

			String sTabDocuments = request.getParameter("tab_documents");
			if (sTabDocuments == null || sTabDocuments.trim().equals("")) {
				sTabDocuments = "N";
			} else {
				iTabCount = iTabCount + 1;
			}

			String sTabAsic = request.getParameter("tab_asic");
			if (sTabAsic == null || sTabAsic.trim().equals("")) {
				sTabAsic = "N";
			} else {
				sTabAsic = sTabAsic.trim();
			}
			
			String sTabIssues = request.getParameter("tab_issues");
			if (sTabIssues == null || sTabIssues.trim().equals("")) {
				sTabIssues = "N";
			} else {
				iTabCount = iTabCount + 1;
			}
			
			if (iTabCount > 5) {
				// cannot allow more than five tabs..
				sError.append("<b>You cannot select more than five (5) tabs for your workspace.</b> <br />");
			}


			String sSceSector = request.getParameter("sce_sector");
			if (sSceSector == null || sSceSector.trim().equals("")) {
				sSceSector = "";
			} else {
				sSceSector = sSceSector.trim();
			}

			if (sSceSector.trim().equals("")) {
				sError.append("<b>SCE Sector</b> cannot be empty.<br />");
			}

			String sProcess = request.getParameter("process");
			if (sProcess == null || sProcess.trim().equals("")) {
				sProcess = "";
			} else {
				sProcess = sProcess.trim();
			}

			if (sProcess.trim().equals("")) {
				sError.append("<b>Process</b> cannot be empty.<br />");
			}

			String sSector = request.getParameter("sector");
			if (sSector == null || sSector.trim().equals("")) {
				sSector = "";
			} else {
				sSector = sSector.trim();
			}

			if (sSector.trim().equals("")) {
				sError.append("<b>Business Sector </b> cannot be empty.<br />");
			}


			String sBrand = request.getParameter("brand");
			if (sBrand == null || sBrand.trim().equals("")) {
				sBrand = "";
			} else {
				sBrand = sBrand.trim();
			}

			if (sBrand.trim().equals("")) {
				sError.append("<b>Brand</b> cannot be empty.<br />");
			}

			// also check to see if the primary contact entered is a valid id...

			if (!sPrimaryEmail.trim().equalsIgnoreCase("") && sPrimaryEmail.trim().toLowerCase().endsWith("ibm.com")) {

				ETSUserDetails usrDetails = new ETSUserDetails();
				usrDetails.setWebId(sPrimaryEmail);
				usrDetails.extractUserDetails(con);

				 if (usrDetails.isUserExists()){
					 if (usrDetails.getUserType() == usrDetails.USER_TYPE_INVALID){
						sError.append("<b>Manager's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
					 }
				 } else {

					// try to sync from UD and see if the id exists

					ETSSyncUser syncUser = new ETSSyncUser();
					syncUser.setWebId(sPrimaryEmail);
					ETSStatus status = syncUser.syncUser(con);

					usrDetails = new ETSUserDetails();
					usrDetails.setWebId(sPrimaryEmail);
					usrDetails.extractUserDetails(con);

					 if (usrDetails.isUserExists()){
						 if (usrDetails.getUserType() == usrDetails.USER_TYPE_INVALID){
							sError.append("<b>Manager's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
						 }
					 } else {
						sError.append("<b>Manager's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
					 }
				 }
			}

			// check to see if the workspace name enterd already exists in the system

			if (!sWorkspaceName.trim().equals("")) {
				if (AICWorkspaceDAO.checkIfWorkspaceNameExists(con,sWorkspaceName)) {
					sError.append("A workspace with this name already exists in the system. Please specify a different <b>Workspace name</b>.<br />");
				}
			}

			// START FIX FOR CSR IBMCC00010835 - V2SRIKAU
			// Don't perform this check till everything else is sorted out
			if (sError.length() == 0 ) {
			if (!sWorkspaceName.trim().equals("") && 
					(sWorkspaceCat.equalsIgnoreCase("Restricted-Teamroom") || 
					 sWorkspaceCat.equalsIgnoreCase("Private-Teamroom"))) 
			{
				String sWorkspaceCompany = request.getParameter("workspace_company");
				String profileName = sWorkspaceCompany + " BPS_AUTHOR " + sWorkspaceName;
				if (profileName.length() > 100 ) {
					int intWorkspcNameLength = 100 - (sWorkspaceCompany + " BPS_AUTHOR ").length();
					sError.append("Workspace name is too long for creating decaf project. Please select workspace name 1-" + intWorkspcNameLength + " long.");
				}
				DecafUtilsImpl decafUtils = new DecafUtilsImpl();
				if (decafUtils.checkDataTypeExists(sWorkspaceName,con)) {
					    
					    if ( (request.getParameter("datatype_override") == null) 
					            || (request.getParameter("datatype_override").equals("")) ) {
							sError.append("A workspace datatype with this name already exists in decaf. Click Submit to create workspace with existing datatype OR specify a different <b>Workspace name</b>.<br />");
							request.setAttribute("datatype_override", "true");
					    }
					    else {
					        request.setAttribute("datatype_override", request.getParameter("datatype_override"));
					    }
					}
				}
			}
		    // END FIX FOR CSR IBMCC00010835 - V2SRIKAU
			
			

			return sError.toString();

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param parameters
	 */
	private void confirmWorkspaceCreate(ETSParams params, String sError) throws SQLException, Exception {

		/*
		 * Check if ITAR has been selected, if yes, then display the ITAR certification page.
		 * If no, then call the create workspace code
		 */

		PrintWriter out = params.getWriter();
		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();
		StringBuffer sConfirm = new StringBuffer("");

		boolean bAdmin = false;

		String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,es.gIR_USERN);
		Vector userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

		if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
			bAdmin = true;
		}

		String sOp = request.getParameter("op");
		if (sOp == null || sOp.trim().equals("")) {
			sOp = "";
		} else {
			sOp = sOp.trim();
		}

		String sITAR = request.getParameter("itar");
		if (sITAR == null || sITAR.trim().equals("")) {
			sITAR = "";
		} else {
			sITAR = sITAR.trim();
		}

		String sWorkspaceName = request.getParameter("workspace_name");
		if (sWorkspaceName == null || sWorkspaceName.trim().equals("")) {
			sWorkspaceName = "";
		} else {
			sWorkspaceName = sWorkspaceName.trim();
		}

		String sWorkspaceDesc = request.getParameter("workspace_desc");
		if (sWorkspaceDesc == null || sWorkspaceDesc.trim().equals("")) {
			sWorkspaceDesc = "";
		} else {
			sWorkspaceDesc = sWorkspaceDesc.trim();
		}

		String sWorkspaceCompany = request.getParameter("workspace_company");
		if (sWorkspaceCompany == null || sWorkspaceCompany.trim().equals("")) {
			sWorkspaceCompany = "";
		} else {
			sWorkspaceCompany = sWorkspaceCompany.trim();
		}

		String sWorkspaceCat = request.getParameter("workspace_cat");
		if (sWorkspaceCat == null || sWorkspaceCat.trim().equals("")) {
			sWorkspaceCat = "";
		} else {
			sWorkspaceCat = sWorkspaceCat.trim();
		}

		String sIbmOnly = request.getParameter("chk_ibmonly");
		if (sIbmOnly == null || sIbmOnly.trim().equals("")) {
			if(sWorkspaceCat.equals("Public-Workspace")){
					sIbmOnly = "Y";
			}else{
					sIbmOnly = "N";
			}
		} else {
			sIbmOnly = sIbmOnly.trim();
		}

		String sWorkspaceType = "";
		String sWorkspaceFlag = "";
		if (sWorkspaceCat.equalsIgnoreCase("clientvoice")) {
			sWorkspaceType = "Client Voice";
			sWorkspaceFlag = "Main";
		} else {
			sWorkspaceType = sWorkspaceCat.substring(0, sWorkspaceCat.indexOf("-"));
			sWorkspaceFlag = sWorkspaceCat.substring(sWorkspaceCat.indexOf("-") + 1);
		}

		String sMainWorkspace = request.getParameter("workspace_main");
		// Will have main workspace project id if any.
		if (sMainWorkspace == null || sMainWorkspace.trim().equals("")) {
			sMainWorkspace = "";
		} else {
			sMainWorkspace = sMainWorkspace.trim();
		}

		String sTabMeeting = request.getParameter("tab_meetings");
		if (sTabMeeting == null || sTabMeeting.trim().equals("")) {
			sTabMeeting = "N";
		} else {
			sTabMeeting = sTabMeeting.trim();
		}

		String sTabDocuments = request.getParameter("tab_documents");
		if (sTabDocuments == null || sTabDocuments.trim().equals("")) {
			sTabDocuments = "N";
		} else {
			sTabDocuments = sTabDocuments.trim();
		}

		String sTabAsic = request.getParameter("tab_asic");
		if (sTabAsic == null || sTabAsic.trim().equals("")) {
			sTabAsic = "N";
		} else {
			sTabAsic = sTabAsic.trim();
		}
		
		String sTabIssues = request.getParameter("tab_issues");
		if (sTabIssues == null || sTabIssues.trim().equals("")) {
			sTabIssues = "N";
		} else {
			sTabIssues = sTabIssues.trim();
		}
		
		String sPrimaryEmail = request.getParameter("primary_email");
		if (sPrimaryEmail == null || sPrimaryEmail.trim().equals("")) {
			sPrimaryEmail = "";
		} else {
			sPrimaryEmail = sPrimaryEmail.trim();
		}

		String sSectorList = request.getParameter("sector");
		if (sSectorList == null || sSectorList.trim().equals("")) {
			sSectorList = "";
		} else {
			sSectorList = sSectorList.trim();
		}

		String sSceSectorList = request.getParameter("sce_sector");
		if (sSceSectorList == null || sSceSectorList.trim().equals("")) {
			sSceSectorList = "";
		} else {
			sSceSectorList = sSceSectorList.trim();
		}

		String sProcessList = request.getParameter("process");
		if (sProcessList == null || sProcessList.trim().equals("")) {
			sProcessList = "";
		} else {
			sProcessList = sProcessList.trim();
		}

		String sBrandList = request.getParameter("brand");
		if (sBrandList == null || sBrandList.trim().equals("")) {
			sBrandList = "";
		} else {
			sBrandList = sBrandList.trim();
		}

		String sSubSectList = request.getParameter("subsector");
		if (sSubSectList == null || sSubSectList.trim().equals("")) {
			sSubSectList = "";
		} else {
			sSubSectList = sSubSectList.trim();
		}

		String sComments = request.getParameter("comments");
		if (sComments == null || sComments.trim().equals("")) {
			sComments = "";
		} else {
			sComments = sComments.trim();
		}

		try {
			 if (sITAR.trim().equalsIgnoreCase("Y")) {

				out.println("<input type=\"hidden\" name=\"op\" value=\"itarfinal\" />");

				out.println("<input type=\"hidden\" name=\"op\" value=\"confirm\" />");
				out.println("<input type=\"hidden\" name=\"workspace_name\" value=\"" + sWorkspaceName + "\" />");
				out.println("<input type=\"hidden\" name=\"workspace_desc\" value=\"" + sWorkspaceDesc + "\" />");
				out.println("<input type=\"hidden\" name=\"workspace_type\" value=\"" + sWorkspaceType + "\" />");
				out.println("<input type=\"hidden\" name=\"workspace_cat\" value=\"" + sWorkspaceCat + "\" />");
				out.println("<input type=\"hidden\" name=\"chk_ibmonly\" value=\"" + sIbmOnly + "\" />");
				out.println("<input type=\"hidden\" name=\"workspace_company\" value=\"" + sWorkspaceCompany + "\" />");

				out.println("<input type=\"hidden\" name=\"workspace_main\" value=\"" + sMainWorkspace + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_meetings\" value=\"" + sTabMeeting + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_documents\" value=\"" + sTabDocuments + "\" />");
				
				out.println("<input type=\"hidden\" name=\"tab_issues\" value=\"" + sTabIssues + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_asic\" value=\"" + sTabAsic + "\" />");
				out.println("<input type=\"hidden\" name=\"sector\" value=\"" + sSectorList + "\" />");
				out.println("<input type=\"hidden\" name=\"subsector\" value=\"" + sSubSectList + "\" />");
				out.println("<input type=\"hidden\" name=\"sce_sector\" value=\"" + sSceSectorList + "\" />");
				out.println("<input type=\"hidden\" name=\"process\" value=\"" + sProcessList + "\" />");
				out.println("<input type=\"hidden\" name=\"brand\" value=\"" + sBrandList + "\" />");
				out.println("<input type=\"hidden\" name=\"comments\" value=\"" + sComments + "\" />");
				out.println("<input type=\"hidden\" name=\"primary_email\" value=\"" + sPrimaryEmail + "\" />");
				out.println("<input type=\"hidden\" name=\"itar\" value=\"" + sITAR + "\" />");

				// showitar screens
				out.println("<br />");
				out.println("<br />");

				if (!sError.trim().equalsIgnoreCase("")) {

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"serr1\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<br />");

					printGreyDottedLine(out);

					out.println("<br />");

				}

				String sEntity = request.getParameter("entity");
				if (sEntity == null || sEntity.trim().equals("")) {
					sEntity = "";
				} else {
					sEntity = sEntity.trim();
				}


				String sPersonal = request.getParameter("personal");
				if (sPersonal == null || sPersonal.trim().equals("")) {
					sPersonal = "";
				} else {
					sPersonal = sPersonal.trim();
				}

				String sEducated = request.getParameter("educated");
				if (sEducated == null || sEducated.trim().equals("")) {
					sEducated = "";
				} else {
					sEducated = sEducated.trim();
				}

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"warn1\" align=\"left\" class=\"subtitle\"><span style=\"color:#ff3333\"><b>ATTENTION and WARNING</b></span></td>");
				out.println("</tr></table>");

				out.println("<br />");

//				printGreyDottedLine(out);
//				out.println("<br />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"unclassitar\" align=\"left\">You are requesting creation and ownership of a workspace to store and exchange unclassified ITAR (International Traffic in Arms Regulations) data and technology with external clients.</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"authorztn\" align=\"left\">To receive authorization you MUST ENSURE (please check all boxes):</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				if (sPersonal.trim().equalsIgnoreCase("Y")) {
					out.println("<td headers=\"persitar1\" width=\"16\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"personal\" id=\"label_personal\" value=\"Y\" class=\"iform\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"persitar2\" width=\"16\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"personal\" id=\"label_personal\" value=\"Y\" class=\"iform\" /></td>");
				}

				out.println("<td headers=\"persitar3\" align=\"left\"><label for=\"label_personal\">You are PERSONALLY ITAR educated, and will re-educate anually. You must certify that you are a US person; and disclose that you have reviewed the applicable Technology Control Plan (TCP) that controls your sites work environment.</label></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				if (sEducated.trim().equalsIgnoreCase("Y")) {
					out.println("<td headers=\"persitar4\" width=\"16\" align=\"left\"  valign=\"top\"><input type=\"checkbox\" name=\"educated\" id=\"label_edu\" value=\"Y\" class=\"iform\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"persitar5\" width=\"16\" align=\"left\"  valign=\"top\"><input type=\"checkbox\" name=\"educated\" id=\"label_edu\" value=\"Y\" class=\"iform\" /></td>");
				}

				out.println("<td headers=\"persitar6\" align=\"left\"><label for=\"label_edu\">You must ensure ALL IBM persons you will allow access to this team room are ITAR educated, US person certified and disclosed as noted above.</label></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				if (sEntity.trim().equalsIgnoreCase("Y")) {
					out.println("<td headers=\"col\" width=\"16\" headers=\"\" align=\"left\"  valign=\"top\"><input type=\"checkbox\" name=\"entity\" id=\"label_entity\" value=\"Y\" class=\"iform\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"col2\" width=\"16\" headers=\"\" align=\"left\"  valign=\"top\"><input type=\"checkbox\" name=\"entity\" id=\"label_entity\" value=\"Y\" class=\"iform\" /></td>");
				}

				out.println("<td headers=\"col3\" align=\"left\"><label for=\"label_entity\">All non-IBM entities who will have access to this room MUST ensure (via document) to you that they understand ITAR regulations and will not expose your work to any US export or Department of Defense violations.</label></td>");
				out.println("</tr>");
				out.println("</table>");


				out.println("<br /><br />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"sitar3\" headers=\"\" align=\"left\">ANY violations of these ITAR regulations is to be reported immediately to your local Export Regulations Coordinator (ERC).</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br /><br />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"col4\" align=\"left\">ANY questions on this subject matter should be adressed to your site ERC or E&TS divisional ERC (Joe Morris/Endicott/IBM) before continuing.</td>");
				out.println("</tr>");
				out.println("</table>");


				out.println("<br /><br />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"col5\" align=\"left\"><b>By checking the boxes above you are confirming that YOU understand and will comply with all US ITAR regulations; the above responsibilities are understood and have been compiled with, including the documentation to support your compliance and to be audit ready at all times.</b></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);
				String sCreateWSServlet = aic_rb.getString("aic.createWorkspaceServlet");
				out.println("<br />");
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"col6\" width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "i_agree.gif\" width=\"120\" height=\"21\" alt=\"I agree\" /></td>");
				out.println("<td headers=\"col7\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col8\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col9\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + sCreateWSServlet + "?linkid=" + params.getLinkId() + "&workspace_company=" + URLEncoder.encode(sWorkspaceCompany) + "\" >Cancel</a></td></tr></table></td>");
				out.println("</tr></table>");

			} else {
				createWorkspace(params);
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param parameters
	 */
	private void displaySubSectorDetails(ETSParams params, String sError) throws SQLException, Exception {

		/*
		 * Check if ITAR has been selected, if yes, then display the ITAR certification page.
		 * If no, then call the create workspace code
		 */

		PrintWriter out = params.getWriter();
		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();
		StringBuffer sConfirm = new StringBuffer("");

		boolean bAdmin = false;

		String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,es.gIR_USERN);
		Vector userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

		if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
			bAdmin = true;
		}

		String sOp = request.getParameter("op");
		if (sOp == null || sOp.trim().equals("")) {
			sOp = "";
		} else {
			sOp = sOp.trim();
		}

		String sITAR = request.getParameter("itar");
		if (sITAR == null || sITAR.trim().equals("")) {
			sITAR = "";
		} else {
			sITAR = sITAR.trim();
		}

		String sWorkspaceName = request.getParameter("workspace_name");
		if (sWorkspaceName == null || sWorkspaceName.trim().equals("")) {
			sWorkspaceName = "";
		} else {
			sWorkspaceName = sWorkspaceName.trim();
		}

		String sWorkspaceDesc = request.getParameter("workspace_desc");
		if (sWorkspaceDesc == null || sWorkspaceDesc.trim().equals("")) {
			sWorkspaceDesc = "";
		} else {
			sWorkspaceDesc = sWorkspaceDesc.trim();
		}

		String sWorkspaceCompany = request.getParameter("workspace_company");
		if (sWorkspaceCompany == null || sWorkspaceCompany.trim().equals("")) {
			sWorkspaceCompany = "";
		} else {
			sWorkspaceCompany = sWorkspaceCompany.trim();
		}

		String sWorkspaceCat = request.getParameter("workspace_cat");
		if (sWorkspaceCat == null || sWorkspaceCat.trim().equals("")) {
			sWorkspaceCat = "";
		} else {
			sWorkspaceCat = sWorkspaceCat.trim();
		}

		String sIbmOnly = request.getParameter("chk_ibmonly");
		if (sIbmOnly == null || sIbmOnly.trim().equals("")) {
			if(sWorkspaceCat.equals("Public-Workspace")){
					sIbmOnly = "Y";
			}else{
					sIbmOnly = "N";
			}
		} else {
			sIbmOnly = sIbmOnly.trim();
		}

		String sWorkspaceType = "";
		String sWorkspaceFlag = "";
		sWorkspaceType = sWorkspaceCat;
		sWorkspaceFlag = sWorkspaceCat;

		String sMainWorkspace = request.getParameter("workspace_main");
		// Will have main workspace project id if any.
		if (sMainWorkspace == null || sMainWorkspace.trim().equals("")) {
			sMainWorkspace = "";
		} else {
			sMainWorkspace = sMainWorkspace.trim();
		}

		String sTabMeeting = request.getParameter("tab_meetings");
		if (sTabMeeting == null || sTabMeeting.trim().equals("")) {
			sTabMeeting = "N";
		} else {
			sTabMeeting = sTabMeeting.trim();
		}

		String sTabDocuments = request.getParameter("tab_documents");
		if (sTabDocuments == null || sTabDocuments.trim().equals("")) {
			sTabDocuments = "N";
		} else {
			sTabDocuments = sTabDocuments.trim();
		}

		String sTabAsic = request.getParameter("tab_asic");
		if (sTabAsic == null || sTabAsic.trim().equals("")) {
			sTabAsic = "N";
		} else {
			sTabAsic = sTabAsic.trim();
		}

		String sTabIssues = request.getParameter("tab_issues");
		if (sTabIssues == null || sTabIssues.trim().equals("")) {
			sTabIssues = "N";
		} else {
			sTabIssues = sTabIssues.trim();
		}
		
		String sPrimaryEmail = request.getParameter("primary_email");
		if (sPrimaryEmail == null || sPrimaryEmail.trim().equals("")) {
			sPrimaryEmail = "";
		} else {
			sPrimaryEmail = sPrimaryEmail.trim();
		}

		String sSectorList = request.getParameter("sector");
		if (sSectorList == null || sSectorList.trim().equals("")) {
			sSectorList = "";
		} else {
			sSectorList = sSectorList.trim();
		}

		String sSceSectorList = request.getParameter("sce_sector");
		if (sSceSectorList == null || sSceSectorList.trim().equals("")) {
			sSceSectorList = "";
		} else {
			sSceSectorList = sSceSectorList.trim();
		}

		String sProcessList = request.getParameter("process");
		if (sProcessList == null || sProcessList.trim().equals("")) {
			sProcessList = "";
		} else {
			sProcessList = sProcessList.trim();
		}

		String sBrandList = request.getParameter("brand");
		if (sBrandList == null || sBrandList.trim().equals("")) {
			sBrandList = "";
		} else {
			sBrandList = sBrandList.trim();
		}

		String sSubSectList = request.getParameter("subsector");
		if (sSubSectList == null || sSubSectList.trim().equals("")) {
			sSubSectList = "";
		} else {
			sSubSectList = sSubSectList.trim();
		}

		String sComments = request.getParameter("comments");
		if (sComments == null || sComments.trim().equals("")) {
			sComments = "";
		} else {
			sComments = sComments.trim();
		}

		try {
		    // START FIX FOR CSR IBMCC00010835 - V2SRIKAU
			String strDataTypeOverride = (String) request.getAttribute("datatype_override");
			if (strDataTypeOverride == null) {
			    strDataTypeOverride = "";
			}
			out.println("<input type=\"hidden\" name=\"datatype_override\" value=\"" + strDataTypeOverride + "\" />");     /* changed confirm to subsector */
		    // END FIX FOR CSR IBMCC00010835 - V2SRIKAU
				//out.println("<input type=\"hidden\" name=\"op\" value=\"itarfinal\" />");
			out.println("<input type=\"hidden\" name=\"op\" value=\"confirm\" />");
			out.println("<input type=\"hidden\" name=\"workspace_type\" value=\"" + sWorkspaceType + "\" />");
			out.println("<input type=\"hidden\" name=\"workspace_cat\" value=\"" + sWorkspaceCat + "\" />");
			out.println("<input type=\"hidden\" name=\"workspace_company\" value=\"" + sWorkspaceCompany + "\" />");
			out.println("<input type=\"hidden\" name=\"chk_ibmonly\" value=\"" + sIbmOnly + "\" />");


			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col10\" width=\"100%\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
			out.println("</tr>");
			out.println("</table>");

			//printGreyDottedLine(out);

			out.println("<br />");

			if (!sError.trim().equalsIgnoreCase("")) {

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"col11\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			}


			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col12\" width=\"220\" align=\"left\"><b>Workspace company:</b></td>");

			out.println("<td headers=\"col13\" align=\"left\">" + sWorkspaceCompany + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"col14\" width=\"220\" align=\"left\"><b>Workspace type:</b></td>");

			out.println("<td headers=\"col15\" align=\"left\">" + sWorkspaceType + "</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col16\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"col17\" width=\"210\" align=\"left\"><b><label for=\"w_name\">Workspace name</label></b></td>");

			out.println("<td headers=\"col18\" align=\"left\"><input type=\"text\" name=\"workspace_name\" value=\"" + sWorkspaceName + "\" id=\"w_name\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"col19\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
			out.println("<td headers=\"col20\" width=\"210\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col21\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col22\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
			out.println("<td headers=\"col23\" width=\"210\" align=\"left\"><b><label for=\"w_desc\">Description:</label></b></td>");
			out.println("<td headers=\"col24\" align=\"left\"><input type=\"text\" name=\"workspace_desc\" value=\"" + sWorkspaceDesc + "\" id=\"w_desc\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col25\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col26\" width=\"10\" align=\"left\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");


			out.println("<td headers=\"col27\" width=\"210\" align=\"left\"><b><label for=\"primary_desc\">Sales Manager's IBM internet  &nbsp; &nbsp; &nbsp; e-mail address:</label></b></td>");
			if (bAdmin) {
				out.println("<td headers=\"col28\" align=\"left\" valign=\"top\"><input type=\"text\" name=\"primary_email\" value=\"" + sPrimaryEmail + "\" id=\"primary_desc\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			} else {
				out.println("<td headers=\"col29\" align=\"left\" valign=\"top\"><b>" + es.gEMAIL.trim() + "</b><input type=\"hidden\" name=\"primary_email\" value=\"" + es.gEMAIL.trim() + "\" /></td>");
			}
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col30\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

//			Second submission************************
			
			if (!sWorkspaceCat.equalsIgnoreCase("WorkFlow-Workspace")){
				
				out.println("<tr>");
				out.println("<td headers=\"col31\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"col32\" width=\"210\" align=\"left\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"col33\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Main\" border=\"0\" /></td>");
				out.println("<td headers=\"col34\" align=\"left\"><b>Main</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"col35\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"col36\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabMeeting.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"col50a\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meet\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"col51b\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meet\" /></td>");
				}
				out.println("<td headers=\"col38\" align=\"left\"><label for=\"label_meet\"><b>Meeting</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"col39\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"col40\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabDocuments.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"col502a\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_doc\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"col513b\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_doc\" /></td>");
				}
				out.println("<td headers=\"col42\" align=\"left\"><label for=\"label_doc\"><b>Documents</b></label></td>");
				out.println("</tr>");

				out.println("<tr>");
				 out.println("<td headers=\"col43\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				 out.println("<td headers=\"col44\" width=\"210\" align=\"left\">&nbsp;</td>");
				 if (sTabAsic.equalsIgnoreCase("Y")) {
				 out.println("<td headers=\"col45\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" checked=\"checked\" /></td>");
				 } else {
				 out.println("<td headers=\"col46\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" /></td>");
				 }
				 
				 out.println("<td headers=\"col47\" align=\"left\"><label for=\"label_asic\"><b>ASIC</b></label></td>");
				 out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"col48\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"col49\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabIssues.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"col50\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_iss\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"col51\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_iss\" /></td>");
				}
				
				out.println("<td headers=\"col52\" align=\"left\"><label for=\"label_iss\"><b>Issues / Changes</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"col53\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"col54\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"col55\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"col56\" align=\"left\"><b>Team</b></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" colspan=\"2\"><b>NOTE:</b>&nbsp; A total of 5 tabs may be selected for a workspace</td>");
				out.println("</tr>");
				
			}//end here
			
//			 Code added for Workflow by Ryazuddin on 11/10/2006		
			
			if(sWorkspaceCat.equalsIgnoreCase("WorkFlow-Workspace")){	
				
				
				out.println("<tr>");
				out.println("<td headers=\"col31\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"col32\" width=\"210\" align=\"left\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"col33\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"WorkFlow Main\" border=\"0\" /></td>");
	    		out.println("<td headers=\"col521\" align=\"left\"><label for=\"label_WorkFlowMain\"><b>WorkFlow main</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"col481\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"col491\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"col33\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Assessment\" border=\"0\" /></td>");
	    		out.println("<td headers=\"col521\" align=\"left\"><label for=\"label_Assessment\"><b>Assessment</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"col53\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"col54\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"col55\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"col56\" align=\"left\"><b>Team</b></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"col53\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"col54\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"col55\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Meeting\" border=\"0\" /></td>");
				out.println("<td headers=\"col56\" align=\"left\"><b>Meeting</b></td>");
				out.println("</tr>");
				
			}
//			 Completes here for workflow
			
			out.println("</table>");
			System.out.println("Workspace Type---->" + sWorkspaceFlag.trim());

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col57\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col58\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"col59\" align=\"left\" width=\"210\"><label for=\"label_brand\"><b>Brand:</b></label></td>");
			out.println("<td headers=\"col60\" align=\"left\" >");

			out.println("<select name=\"brand\" id=\"label_brand\" class=\"iform\">");

			out.println("<option value=\"\" >Select a brand</option>");

			Vector vBrand = getValues(con,Defines.BRAND);

			if (vBrand != null && vBrand.size() > 0) {

				for (int i = 0; i < vBrand.size(); i++) {

					String sValue = (String) vBrand.elementAt(i);

					if (sBrandList.equalsIgnoreCase(sValue))  {
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

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col61\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col62\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"col63\" align=\"left\" width=\"210\"><label for=\"label_process\"><b>Process:</b></label></td>");
			out.println("<td headers=\"col64\" align=\"left\" >");

//			--------- added for WorkFlow 02/11/2006 by Ryazuddin.
			if(sWorkspaceCat.equalsIgnoreCase("WorkFlow-Workspace")){
	            
					out.println("<select name=\"process\" id=\"label_process\" class=\"iform\">");

								out.println("<option value=\" Workflow \" selected=\"selected\">Workflow</option>");
								//out.println("<option value=\"" + sProcess + "\" >" + sProcess + "</option>");

					out.println("</select>");
				
				}else{
					out.println("<select name=\"process\" id=\"label_process\" class=\"iform\">");

					out.println("<option value=\"\" >Select a process</option>");
					
					Vector vProcess = getValues(con,Defines.PROCESS);

					if (vProcess != null && vProcess.size() > 0) {

						for (int i = 0; i < vProcess.size(); i++) {

							String sProcess = (String) vProcess.elementAt(i);

							if (sProcessList.equalsIgnoreCase(sProcess))  {
								out.println("<option value=\"" + sProcess + "\" selected=\"selected\">" + sProcess + "</option>");
							} else {
								out.println("<option value=\"" + sProcess + "\" >" + sProcess + "</option>");
							}
						}
					}

					out.println("</select>");
				}
//			Ends here
			
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col65\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col66\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"col67\" align=\"left\" width=\"210\"><label for=\"label_scesect\"><b>SCE Sector:</b></label></td>");
			out.println("<td headers=\"col68\" align=\"left\" >");

			out.println("<select name=\"sce_sector\" id=\"label_scesect\" class=\"iform\">");

			out.println("<option value=\"\" >Select a SCE Sector</option>");

			Vector vSceSect = getValues(con,Defines.SCE_SECTOR);

			if (vSceSect != null && vSceSect.size() > 0) {

				for (int i = 0; i < vSceSect.size(); i++) {

					String sSceSect = (String) vSceSect.elementAt(i);

					if (sSceSect.equalsIgnoreCase(sSceSectorList))  {
						out.println("<option value=\"" + sSceSect + "\" selected=\"selected\">" + sSceSect + "</option>");
					} else {
						out.println("<option value=\"" + sSceSect + "\" >" + sSceSect + "</option>");
					}
				}
			}

			out.println("</select>");

			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col69\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col70\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"col71\" width=\"210\" align=\"left\"><b><label for=\"label_sector\">Business Sector:</label></b></td>");

			out.println("<td headers=\"col72\" align=\"left\"><input width=\"200px\" type=\"text\" name=\"sector\" value=\"" + sSectorList + "\" id=\"sector\" maxlength=\"100\" size=\"40\" class=\"iform\" READONLY/>");
			String sCreateWSServlet = aic_rb.getString("aic.createWorkspaceServlet");
			out.println("<img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" width=\"21\" height=\"21\" alt=\"Edit\" border=\"0\" /><a href=\"" + Defines.SERVLET_PATH + sCreateWSServlet + "?linkid=" + params.getLinkId() + "&workspace_name=" + URLEncoder.encode(sWorkspaceName) + "&workspace_desc=" + URLEncoder.encode(sWorkspaceDesc) + "&workspace_company=" + URLEncoder.encode(sWorkspaceCompany) + "&workspace_cat=" + URLEncoder.encode(sWorkspaceCat) + "&op=second\" >Edit</a></td>");
			out.println("</td><td></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"col73\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
			out.println("<td headers=\"col74\" width=\"210\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");


			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col75\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr><td headers=\"col76\" align=\"left\" wrap  ><b>Please select a Business Sub sector for the Business sector selected. Click on Edit to change Business Sector.</b></td>");
			//out.println("<td></td>");
			//String sCreateWSServlet = aic_rb.getString("aic.createWorkspaceServlet");
			//out.println("<img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" width=\"21\" height=\"21\" alt=\"Edit\" border=\"0\" /></td><td headers=\"cols77\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + sCreateWSServlet + "?linkid=" + params.getLinkId() + "&workspace_name=" + URLEncoder.encode(sWorkspaceName) + "&workspace_desc=" + URLEncoder.encode(sWorkspaceDesc) + "&workspace_company=" + URLEncoder.encode(sWorkspaceCompany) + "&workspace_cat=" + URLEncoder.encode(sWorkspaceCat) + "&op=second\" >Edit</a></td>");
			out.println("<td headers=\"col78\" align=\"left\">&nbsp;</td></tr>");
			out.println("<tr>");
			out.println("<td headers=\"col79\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col80\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"col81\" align=\"left\" width=\"210\"><label for=\"label_subSect\"><b>Business SubSector:</b></label></td>");
			out.println("<td headers=\"col82\" align=\"left\" >");

			out.println("<select name=\"subsector\" id=\"label_subsect\" class=\"iform\">");

			out.println("<option value=\"\" >Select a Sub Sector</option>");

			Vector vSubSector = getValuesSubSector(con,Defines.SECTOR,sSectorList);

			if (vSubSector != null && vSubSector.size() > 0) {

				for (int i = 0; i < vSubSector.size(); i++) {

					String sValue = (String) vSubSector.elementAt(i);

					if (sSubSectList.equalsIgnoreCase(sValue))  {
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

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"col83\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			printGreyDottedLine(out);
			out.println("<br />");
			//String sCreateWSServlet = aic_rb.getString("aic.createWorkspaceServlet");
			out.println("<br />");
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"cols84\" width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
			out.println("<td headers=\"col85\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col86\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col87\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + sCreateWSServlet + "?linkid=" + params.getLinkId() + "&workspace_name=" + URLEncoder.encode(sWorkspaceName) + "&workspace_desc=" + URLEncoder.encode(sWorkspaceDesc) + "&workspace_company=" + URLEncoder.encode(sWorkspaceCompany) + "&workspace_cat=" + URLEncoder.encode(sWorkspaceCat) + "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr></table>");




		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * @param parameters
	 */
	private void createWorkspace(ETSParams params) throws SQLException, Exception {

		 /*
		  * Check if ITAR has been selected, if yes, then display the ITAR certification page.
		  * If no, then create the workspace according to the logic below...
		  *
		  *
		  * 1. check to see if the logged in person is admin or not.
		  *    - if admin
		  *       - then create the workspace
		  *       - if the primary contact entered does not have sales entitlement, still create a workspace
		  *       - if the primary contact entered does not have POC, create a request for POC (MULTIPOC) (if the workspace is external )
		  *    - if not admin
		  *       - if the primary contact has ets projects entitlement
		  *          - create the workspace. (DO NOT CREATE THE WORKSPACE IF ITAR. SEND AN EMAIL TO ETS ADMIN. CHANGED BY SATHISH BASED ON ANNE'S REQUEST)
		  *          - if the primary contact does not have POC entitlement
		  *            - request POC entitlement
		  *       else if primary contact does not have ets projects entitlement
		  *         - send the requested stuff as email to ets admin
		  *
		  */


		PrintWriter out = params.getWriter();
		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();
		StringBuffer sConfirm = new StringBuffer("");

		try {

			boolean bAdmin = false;

			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,es.gIR_USERN);
			Vector userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				bAdmin = true;
			}

			String sITAR = request.getParameter("itar");
			if (sITAR == null || sITAR.trim().equals("")) {
				sITAR = "";
			} else {
				sITAR = sITAR.trim();
			}

			String sWorkspaceName = request.getParameter("workspace_name");
			if (sWorkspaceName == null || sWorkspaceName.trim().equals("")) {
				sWorkspaceName = "";
			} else {
				sWorkspaceName = sWorkspaceName.trim();
			}

			String sWorkspaceDesc = request.getParameter("workspace_desc");
			if (sWorkspaceDesc == null || sWorkspaceDesc.trim().equals("")) {
				sWorkspaceDesc = "";
			} else {
				sWorkspaceDesc = sWorkspaceDesc.trim();
			}

			String sWorkspaceCompany = request.getParameter("workspace_company");
			if (sWorkspaceCompany == null || sWorkspaceCompany.trim().equals("")) {
				sWorkspaceCompany = "";
			} else {
				sWorkspaceCompany = sWorkspaceCompany.trim();
			}

			String sWorkspaceCat = request.getParameter("workspace_cat");
			if (sWorkspaceCat == null || sWorkspaceCat.trim().equals("")) {
				sWorkspaceCat = "";
			} else {
				sWorkspaceCat = sWorkspaceCat.trim();
			}

			String sIbmOnly = request.getParameter("chk_ibmonly");
			if (sIbmOnly == null || sIbmOnly.trim().equals("")) {
				if(sWorkspaceCat.equals("Public-Workspace")){
					sIbmOnly = "Y";
				}else if (sWorkspaceCat.equals("Restricted-Teamroom") || sWorkspaceCat.equals("Private-Teamroom")){
					sIbmOnly = "N";
				} else {
					sIbmOnly = "N";
				}
			} else {
				sIbmOnly = sIbmOnly.trim();
			}

			String sWorkspaceType = "";
			String sWorkspaceFlag = "";

			sWorkspaceType = sWorkspaceCat;
			sWorkspaceFlag = sWorkspaceCat;

			String sMainWorkspace = request.getParameter("workspace_main");
			// Will have main workspace project id if any.
			if (sMainWorkspace == null || sMainWorkspace.trim().equals("")) {
				sMainWorkspace = "";
			} else {
				sMainWorkspace = sMainWorkspace.trim();
			}

			String sTabMeeting = request.getParameter("tab_meetings");
			if (sTabMeeting == null || sTabMeeting.trim().equals("")) {
				sTabMeeting = "N";
			} else {
				sTabMeeting = sTabMeeting.trim();
			}

			String sTabDocuments = request.getParameter("tab_documents");
			if (sTabDocuments == null || sTabDocuments.trim().equals("")) {
				sTabDocuments = "N";
			} else {
				sTabDocuments = sTabDocuments.trim();
			}

			String sTabAsic = request.getParameter("tab_asic");
			if (sTabAsic == null || sTabAsic.trim().equals("")) {
				sTabAsic = "N";
			} else {
				sTabAsic = sTabAsic.trim();
			}

			String sTabIssues = request.getParameter("tab_issues");
			if (sTabIssues == null || sTabIssues.trim().equals("")) {
				sTabIssues = "N";
			} else {
				sTabIssues = sTabIssues.trim();
			}

			
			String sPrimaryEmail = request.getParameter("primary_email");
			if (sPrimaryEmail == null || sPrimaryEmail.trim().equals("")) {
				sPrimaryEmail = "";
			} else {
				sPrimaryEmail = sPrimaryEmail.trim();
			}

			String sSectorList = request.getParameter("sector");
			if (sSectorList == null || sSectorList.trim().equals("")) {
				sSectorList = "";
			} else {
				sSectorList = sSectorList.trim();
			}

			String sSubSectList = request.getParameter("subsector");
			if (sSubSectList == null || sSubSectList.trim().equals("")) {
				sSubSectList = "";
			} else {
				sSubSectList = sSubSectList.trim();
			}

			String sSceSectorList = request.getParameter("sce_sector");
			if (sSceSectorList == null || sSceSectorList.trim().equals("")) {
				sSceSectorList = "";
			} else {
				sSceSectorList = sSceSectorList.trim();
			}

			String sProcessList = request.getParameter("process");
			if (sProcessList == null || sProcessList.trim().equals("")) {
				sProcessList = "";
			} else {
				sProcessList = sProcessList.trim();
			}

			String sBrandList = request.getParameter("brand");
			if (sBrandList == null || sBrandList.trim().equals("")) {
				sBrandList = "";
			} else {
				sBrandList = sBrandList.trim();
			}

			String sComments = request.getParameter("comments");
			if (sComments == null || sComments.trim().equals("")) {
				sComments = "";
			} else {
				sComments = sComments.trim();
			}

			out.println("<br />");


			String sProjectOrProposal = "P";

			if (bAdmin) {

				// create the project.
				// check if the primary contact entered has ETSProjects entitlement, if not request for one.
				// check if the primary contact entered has POC entitlement, if not request for one.


				String sProjId = create(params);

				//Send MQ event -- start
				ETSSendWorkspaceEvent sendEvent = new ETSSendWorkspaceEvent();
				boolean mqStatus = sendEvent.sendWrkspaceEvent(con,sProjId,"Create Workspace Event");
				System.out.println("STATUS of MQ EVENT for create workspace" + mqStatus);
				//Send MQ event -- end 
				
				boolean bEntRequested = false;
				boolean bPOCEntRequested = false;
				boolean bHasEnt = true;
				boolean bTeamroomWrkspc = false;
				
				if (sWorkspaceCat.equals("Restricted-Teamroom") || sWorkspaceCat.equals("Private-Teamroom")){
					bTeamroomWrkspc = true;
				}

				// check if the primary contact entered has ETSProjects entitlement, if not request for one.
				// No entitlement required for Primary contact
				/*
				boolean bHasEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sPrimaryEmail,Defines.AIC_ENTITLEMENT);

				if (!bHasEnt & !sProjId.trim().equalsIgnoreCase("")) {

					ETSUserAccessRequest uar = new ETSUserAccessRequest();
					uar.setTmIrId(sPrimaryEmail);
					uar.setPmIrId(es.gIR_USERN);
					uar.setDecafEntitlement(Defines.AIC_ENTITLEMENT);
					uar.setIsAProject(true);
					uar.setUserCompany("");
					ETSStatus status = uar.request(con);

					if (status.getErrCode() == 0) {
						bEntRequested = true;
					} else {
						bEntRequested  = false;
					}
				}
				*/


				// check if the primary contact entered has MULTIPOC entitlement, if not request for one.
				// check Multi POC for extrenal Projects

				if (sIbmOnly.equalsIgnoreCase("N")) {
					boolean bHasPOCEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sPrimaryEmail,Defines.MULTIPOC);
					if (!bHasPOCEnt & !sProjId.trim().equalsIgnoreCase("")) {

						ETSUserAccessRequest uar = new ETSUserAccessRequest();
						uar.setTmIrId(sPrimaryEmail);
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

				out.println("<br /><br />");

				if (!sProjId.trim().equalsIgnoreCase("")) {
					if (bTeamroomWrkspc) {
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"col88\" align=\"left\">A new workspace has been created. You will be able to use the workspace once you get entitlement for -<b>" + sWorkspaceCompany + " BPS OWNER " + sWorkspaceName + ".</b></td>");
						out.println("</tr>");
						out.println("<tr><td>&nbsp;</td></tr>");
						out.println("<tr><td headers=\"col88\" align=\"left\"><ul><li>You can add users to the workspace by requesting BPSOwner/BPSReader/BPSAuthor entitlement for the users from decaf.</li></ul></td>");
						out.println("</tr>");
					} else {
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"col88\" align=\"left\"><b>A new workspace has been created and is ready for use.</b></td>");
						out.println("</tr>");						
					}
					out.println("</table>");

					// have to get the users edge id...

					String sEdgeId = ETSUtils.getUserEdgeIdFromAMT(con,sPrimaryEmail);
					String sMgrEmail = "";

					if (!sEdgeId.trim().equalsIgnoreCase("")) {
						sMgrEmail = ETSUtils.getManagersEMailFromDecafTable(con,sEdgeId);
					} else {
						sMgrEmail = "";
					}

					if (!sMgrEmail.trim().equalsIgnoreCase("")) {
						sMgrEmail = "at " + sMgrEmail;
					}

					if (bEntRequested) {

						out.println("<br />");
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"col89\" align=\"left\"><ul><li>An entitlement request has been sent to user's manager " + sMgrEmail + " for approval.</li></ul></td>");
						out.println("</tr>");
						out.println("</table>");
					} else {
						if (!bHasEnt & !sProjId.trim().equalsIgnoreCase("")) {
							out.println("<br />");
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"col90\" align=\"left\"><span style=\"color:#ff3333\"><ul><li>An error occured while requesting entitlement. Please contact your system administrator.</li></ul></span></td>");
							out.println("</tr>");
							out.println("</table>");
						}
					}

					if (bPOCEntRequested) {
						out.println("<br />");
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"col91\" align=\"left\"><ul><li>A customer Point Of Contact (POC) entitlement request has been sent to user's manager " + sMgrEmail + " for approval.</li></ul></td>");
						out.println("</tr>");
						out.println("</table>");
					}
					out.println("<table>");
					out.println("<tr><td headers=\"col88a\" align=\"left\">&nbsp;</td></tr>");
					out.println("<tr><td headers=\"col88b\" align=\"left\">Please note: The workspace owner is responsible for security and integrity of the access granted to the members.</td></tr>");
					out.println("</table>");

					sendNotificationEmail(con,sWorkspaceName,sWorkspaceCompany,sPrimaryEmail,bEntRequested,bPOCEntRequested,es);
					if (bPOCEntRequested) {
						sendPOCEmail(con,es,sPrimaryEmail);
					}

				} else {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"col92\" align=\"left\"><span style=\"color:#ff3333\"><b>There was an error creating the workspace. Please try again later. If the problem persists, please contact your primary contact.</b></span></td>");
					out.println("</tr>");
					out.println("</table>");
				}

				out.println("<br /><br />");

				printGreyDottedLine(out);
				out.println("<br />");

				if (!sProjId.trim().equalsIgnoreCase("")) {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"col93\" width=\"200\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col94\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to " + sWorkspaceName + "\" border=\"0\" /></td><td headers=\"col95\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?linkid=" + params.getLinkId() + "&proj=" + sProjId + "\" >Go to '" + sWorkspaceName + "'</a></td></tr></table></td>");
					out.println("<td headers=\"col96\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col97\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to Collaboration Center home page\" border=\"0\" /></td><td headers=\"col98\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + LANDING_PAGE + "?linkid=" + params.getLinkId() + "\" >Go to Collaboration Center home page</a></td></tr></table></td>");
					out.println("</tr></table>");
				} else {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"col99\" width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + LANDING_PAGE + "?linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
					out.println("</tr></table>");
				}

			} else {

				// Find out if the primary contact has ets projects

				boolean bHasEntitlement = true;
				boolean bTeamroomWrkspc = false;
				
				if (sWorkspaceCat.equals("Restricted-Teamroom") || sWorkspaceCat.equals("Private-Teamroom")){
					bTeamroomWrkspc = true;
				}

				if (bHasEntitlement) {

					// the primary contact has entitlement..
					// create the project.
					// check if the primary contact has POC entitlement, if not request for one.

						String sProjId = create(params);

						//Send MQ event -- start
						ETSSendWorkspaceEvent sendEvent = new ETSSendWorkspaceEvent();
						boolean mqStatus = sendEvent.sendWrkspaceEvent(con,sProjId,"Create Workspace Event");
						System.out.println("STATUS of MQ EVENT for create workspace" + mqStatus);
						//Send MQ event -- end 
						
						boolean bPOCEntRequested = false;

						// check if the primary contact entered has MULTIPOC entitlement, if not request for one.

						if (sIbmOnly.equalsIgnoreCase("N")) {
							boolean bHasPOCEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sPrimaryEmail,Defines.MULTIPOC);
							if (!bHasPOCEnt & !sProjId.trim().equalsIgnoreCase("")) {

								ETSUserAccessRequest uar = new ETSUserAccessRequest();
								uar.setTmIrId(sPrimaryEmail);
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
						out.println("<br /><br />");

						if (!sProjId.trim().equalsIgnoreCase("")) {
							if (bTeamroomWrkspc) {
								out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
								out.println("<td headers=\"col88\" align=\"left\">A new workspace has been created. You will be able to use the workspace once you get entitlement for -<b>" + sWorkspaceCompany + " BPS OWNER " + sWorkspaceName + ".</b></td>");
								out.println("</tr>");
								out.println("<tr><td>&nbsp;</td></tr>");
								out.println("<tr><td headers=\"col88\" align=\"left\"><ul><li>You can add users to the workspace by requesting BPSOwner/BPSReader/BPSAuthor entitlement for the users from decaf.</li></ul></td>");
								out.println("</tr>");
							} else {

								out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
								out.println("<tr>");
								out.println("<td headers=\"col103\" align=\"left\"><b>A new workspace has been created and is ready for use.</b></td>");
								out.println("</tr>");
							}
							
							if (bPOCEntRequested) {
								String sMgrEmail = ETSUtils.getManagersEMailFromDecafTable(con,es.gUSERN);
								if (!sMgrEmail.trim().equalsIgnoreCase("")) {
									sMgrEmail = "at " + sMgrEmail;
								}
								out.println("<tr><td headers=\"col104\" align=\"left\"><ul><li></li>A customer Point of Contact (POC) entitlement request has been sent to your manager " + sMgrEmail + " and then to the system level administrator for approval. You will receive an e-mail from \"IBM Customer Connect\" when this has been processed.</ul></b></td></tr>");
								out.println("<tr><td headers=\"col105\" align=\"left\"><ul><li></li>A POC request is required before you can add your clients to this workspace but you can begin adding IBMers now.</ul></b></td></tr>");
							}
							out.println("<tr><td headers=\"col88a\" align=\"left\">&nbsp;</td></tr>");
							out.println("<tr><td headers=\"col88b\" align=\"left\">Please note: The workspace owner is responsible for security and integrity of the access granted to the members.</td></tr>");

							out.println("</table>");
						} else {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"col106\" align=\"left\"><span style=\"color:#ff3333\"><b>There was an error creating the workspace. Please try again later. If the problem persists, please contact your primary contact.</b></span></td>");
							out.println("</tr>");
							out.println("</table>");
						}

						out.println("<br /><br />");


						printGreyDottedLine(out);
						out.println("<br />");

						if (!sProjId.trim().equalsIgnoreCase("")) {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"col107\" width=\"200\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col108\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to " + sWorkspaceName + "\" border=\"0\" /></td><td headers=\"col109\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?linkid=" + params.getLinkId() + "&proj=" + sProjId + "\" >Go to '" + sWorkspaceName + "'</a></td></tr></table></td>");
							out.println("<td headers=\"col110\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col111\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to Collaboration Center home page\" border=\"0\" /></td><td headers=\"col112\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + LANDING_PAGE + "?linkid=" + params.getLinkId() + "\" >Go to Collaboration Center home page</a></td></tr></table></td>");
							out.println("</tr></table>");
						} else {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"col113\" width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + LANDING_PAGE + "?linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
							out.println("</tr></table>");
						}

						if (bPOCEntRequested) {
							sendPOCEmail(con,es,sPrimaryEmail);
						}



				} else {

					// the primary contact does not have entitlement.
					// forward this request as email to ets admin...

					UnbrandedProperties prop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

					ETSMail mail = new ETSMail();
					StringBuffer sEmailStr = new StringBuffer("");
					StringBuffer sEmailTo = new StringBuffer("");

					mail.setSubject(APPLICATION_NAME + ": Request for new workspace creation.");
					sEmailStr.append("A request to create a new workspace has been generated from " + APPLICATION_NAME +".\n\n");

					sEmailStr.append("The new request details are as follows.\n\n");

					sEmailStr.append("Requestor details:\n\n");
					sEmailStr.append("ID                      : " + es.gIR_USERN + "\n");
					sEmailStr.append("Name                    : " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + "\n");
					sEmailStr.append("E-mail                  : " + es.gEMAIL + "\n\n");


					sEmailStr.append("Details entered on request form:\n\n");
					sEmailStr.append("Client company          : " + sWorkspaceCompany + "\n");
					if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
						sEmailStr.append("Sub workspace name      : " + sWorkspaceName + "\n");
					} else {
						sEmailStr.append("Workspace name          : " + sWorkspaceName + "\n");
					}

					if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
						sEmailStr.append("Sub workspace type      : " + sWorkspaceType + "\n");
					} else {
						if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
							sEmailStr.append("Workspace type          : " + sWorkspaceType + "\n");
						} else {
							if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
								sEmailStr.append("Workspace type          : " + sWorkspaceType + " - Sub Workspace\n");
							} else {
								sEmailStr.append("Workspace type          : " + sWorkspaceType + " - Main Workspace\n");
							}
						}
					}
					sEmailStr.append("Description             : " + sWorkspaceDesc + "\n");
					sEmailStr.append("Sales manager's email   : " + sPrimaryEmail + "\n");

					sEmailStr.append("Workspace tabs          : Main\n");

					if (sTabMeeting.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : Meetings\n");
					}
					if (sTabDocuments.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : Documents\n");
					}
					if (sTabAsic.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : ASIC\n");
					}
					if (sTabIssues.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : Issues\n");
					}
					sEmailStr.append("                        : Team\n");

					sEmailStr.append("SCE Sector               : " + sSceSectorList + "\n");
					sEmailStr.append("Sector                   : " + sSectorList + "\n");
					sEmailStr.append("Brand                    : " + sBrandList + "\n");
					sEmailStr.append("Process                  : " + sProcessList + "\n");

					if (sITAR.trim().equalsIgnoreCase("Y")) {
						sEmailStr.append("ITAR                    : Yes\n");
					}
					sEmailStr.append("\n\n");
					sEmailStr.append("Additional comments: \n");
					sEmailStr.append(sComments + "\n\n");

					sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));

					mail.setMessage(sEmailStr.toString());
					mail.setTo(prop.getAdminEmailID());
					mail.setFrom(es.gEMAIL);
					mail.setBcc("");
					mail.setCc("");

					boolean bSent = ETSUtils.sendEmail(mail);

					out.println("<br />");

					if (bSent) {
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
							out.println("<td headers=\"col114\" align=\"left\"><b>Your request to create a new Sub workspace has been submitted to " + APPLICATION_NAME + " administrator for processing. It may take up to 5 business days to process your request. You can contact us at <a href=\"mailto:" + prop.getAdminEmailID() + "\">" + prop.getAdminEmailID() + "</a> if you have any questions.</b></td>");
						} else {
							out.println("<td headers=\"col115\" align=\"left\"><b>Your request to create a new workspace has been submitted to " + APPLICATION_NAME + " administrator for processing. It may take up to 5 business days to process your request. You can contact us at <a href=\"mailto:" + prop.getAdminEmailID() + "\">" + prop.getAdminEmailID() + "</a> if you have any questions.</b></td>");
						}
						out.println("</tr>");
						out.println("</table>");
					} else {
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"col116\" align=\"left\"><span style=\"color:#ff3333\"><b>There was an error when submitting your request to create workspace. Please try again later. If the problem persists, please contact your primary contact.</b></span></td>");
						out.println("</tr>");
						out.println("</table>");
					}
					out.println("<br />");

					printGreyDottedLine(out);
					out.println("<br />");

					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"col117\" width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + LANDING_PAGE + "?linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
					out.println("</tr></table>");


				}
			}



		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param parameters
	 */
	private String create(ETSParams params) throws SQLException, Exception {

		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();

		String sProjectID = "";

		try {

			String sITAR = request.getParameter("itar");
			if (sITAR == null || sITAR.trim().equals("")) {
				sITAR = "";
			} else {
				sITAR = sITAR.trim();
			}

			String sWorkspaceName = request.getParameter("workspace_name");
			if (sWorkspaceName == null || sWorkspaceName.trim().equals("")) {
				sWorkspaceName = "";
			} else {
				sWorkspaceName = sWorkspaceName.trim();
			}

			String sWorkspaceDesc = request.getParameter("workspace_desc");
			if (sWorkspaceDesc == null || sWorkspaceDesc.trim().equals("")) {
				sWorkspaceDesc = "";
			} else {
				sWorkspaceDesc = sWorkspaceDesc.trim();
			}

			String sWorkspaceCompany = request.getParameter("workspace_company");
			if (sWorkspaceCompany == null || sWorkspaceCompany.trim().equals("")) {
				sWorkspaceCompany = "";
			} else {
				sWorkspaceCompany = sWorkspaceCompany.trim();
			}

			String sWorkspaceCat = request.getParameter("workspace_cat");
			if (sWorkspaceCat == null || sWorkspaceCat.trim().equals("")) {
				sWorkspaceCat = "";
			} else {
				sWorkspaceCat = sWorkspaceCat.trim();
			}

			String sIbmOnly = request.getParameter("chk_ibmonly");
			if (sIbmOnly == null || sIbmOnly.trim().equals("")) {
				if(sWorkspaceCat.equals("Public-Workspace")){
					sIbmOnly = "Y";
				}else{
					sIbmOnly = "N";
				}
			} else {
				sIbmOnly = sIbmOnly.trim();
			}

			String sWorkspaceType = "";
			String sWorkspaceFlag = "";
			if (sWorkspaceCat.equalsIgnoreCase("clientvoice")) {
				sWorkspaceType = "Client Voice";
				sWorkspaceFlag = "Main";
			} else {
				sWorkspaceType = sWorkspaceCat.substring(0, sWorkspaceCat.indexOf("-"));
				sWorkspaceFlag = sWorkspaceCat.substring(sWorkspaceCat.indexOf("-") + 1);
			}

			String sMainWorkspace = request.getParameter("workspace_main");
			// Will have main workspace project id if any.
			if (sMainWorkspace == null || sMainWorkspace.trim().equals("")) {
				sMainWorkspace = "";
			} else {
				sMainWorkspace = sMainWorkspace.trim();
			}

			String sTabMeeting = request.getParameter("tab_meetings");
			if (sTabMeeting == null || sTabMeeting.trim().equals("")) {
				sTabMeeting = "N";
			} else {
				sTabMeeting = sTabMeeting.trim();
			}

			String sTabDocuments = request.getParameter("tab_documents");
			if (sTabDocuments == null || sTabDocuments.trim().equals("")) {
				sTabDocuments = "N";
			} else {
				sTabDocuments = sTabDocuments.trim();
			}

			String sTabAsic = request.getParameter("tab_asic");
			if (sTabAsic == null || sTabAsic.trim().equals("")) {
				sTabAsic = "N";
			} else {
				sTabAsic = sTabAsic.trim();
			}

			String sTabIssues = request.getParameter("tab_issues");
			if (sTabIssues == null || sTabIssues.trim().equals("")) {
				sTabIssues = "N";
			} else {
				sTabIssues = sTabIssues.trim();
			}

			String sPrimaryEmail = request.getParameter("primary_email");
			if (sPrimaryEmail == null || sPrimaryEmail.trim().equals("")) {
				sPrimaryEmail = "";
			} else {
				sPrimaryEmail = sPrimaryEmail.trim();
			}

			String sSectorList = request.getParameter("sector");
			if (sSectorList == null || sSectorList.trim().equals("")) {
				sSectorList = "";
			} else {
				sSectorList = sSectorList.trim();
			}

			String sSubSectList = request.getParameter("subsector");
			if (sSubSectList == null || sSubSectList.trim().equals("")) {
				sSubSectList = "";
			} else {
				sSubSectList = sSubSectList.trim();
			}

			String sSceSectorList = request.getParameter("sce_sector");
			if (sSceSectorList == null || sSceSectorList.trim().equals("")) {
				sSceSectorList = "";
			} else {
				sSceSectorList = sSceSectorList.trim();
			}

			String sProcessList = request.getParameter("process");
			if (sProcessList == null || sProcessList.trim().equals("")) {
				sProcessList = "";
			} else {
				sProcessList = sProcessList.trim();
			}

			String sBrandList = request.getParameter("brand");
			if (sBrandList == null || sBrandList.trim().equals("")) {
				sBrandList = "";
			} else {
				sBrandList = sBrandList.trim();
			}

			String sComments = request.getParameter("comments");
			if (sComments == null || sComments.trim().equals("")) {
				sComments = "";
			} else {
				sComments = sComments.trim();
			}

			String sProjectOrProposal = "P";

			con.setAutoCommit(false);
			
//			--Work space created here **********
			
			sProjectID = AICWorkspaceDAO.createAICWorkspace(con, sWorkspaceName, sWorkspaceDesc, sProjectOrProposal, sMainWorkspace, sWorkspaceCompany, sSectorList, sSceSectorList, sBrandList,sProcessList, sITAR , sSubSectList, sWorkspaceCat, sIbmOnly);
			//String sLoginUserId = aic_rb.getString("aic.decaf.insert.user");
			
			boolean bSuccess = false;
			if (sWorkspaceCat.equalsIgnoreCase("RESTRICTED-TEAMROOM") || sWorkspaceCat.equalsIgnoreCase("PRIVATE-TEAMROOM")) {
				DecafUtilsImpl decaf = new DecafUtilsImpl();
				String sLoginUserId = aic_rb.getString("aic.decaf.insert.user");
			    // START FIX FOR CSR IBMCC00010835 - V2SRIKAU
				//bSuccess = decaf.createDatatypeAndProfile(sWorkspaceName,es.gIR_USERN,sWorkspaceCompany,sLoginUserId);
				String strOverrideDataType = request.getParameter("datatype_override");
				boolean bIsOverride = "true".equalsIgnoreCase(strOverrideDataType);
				if (bIsOverride) {
					bSuccess = decaf.createProfile(sWorkspaceName,es.gIR_USERN,sWorkspaceCompany,sLoginUserId);
				}
				else {
				bSuccess = decaf.createDatatypeAndProfile(sWorkspaceName,es.gIR_USERN,sWorkspaceCompany,sLoginUserId);
				}
			    // END FIX FOR CSR IBMCC00010835 - V2SRIKAU
		
				if (!bSuccess) {
					throw new Exception();
				}
			}

			bSuccess = ETSWorkspaceDAO.createRoles(con, sProjectID, sProjectOrProposal, es.gIR_USERN);

			bSuccess = ETSWorkspaceDAO.createWorkspaceOwner(con, sProjectID, sPrimaryEmail, es.gIR_USERN);

			if (sProjectOrProposal.trim().equalsIgnoreCase("P")) {
				int iCount = 10;
				//added for workflow by Ryazuddin on 11/10/2006
				
				// add tabs for teamroom & collaboration center workspaces.
				if (!sWorkspaceCat.equalsIgnoreCase("WorkFlow-Workspace")){
				
					// main tab
					bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.MAIN_VT, iCount, es.gIR_USERN);
					iCount = iCount + 10;
					if (sTabMeeting.trim().equalsIgnoreCase("Y")) {
						bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.MEETINGS_VT, iCount, es.gIR_USERN);
						iCount = iCount + 10;
					}
					if (sTabDocuments.trim().equalsIgnoreCase("Y")) {
						bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.DOCUMENTS_VT, iCount, es.gIR_USERN);
						iCount = iCount + 10;
					}
					if (sTabAsic.trim().equalsIgnoreCase("Y")) {
						bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.ASIC_VT, iCount, es.gIR_USERN);
						iCount = iCount + 10;
					}
					if (sTabIssues.trim().equalsIgnoreCase("Y")) {
						bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.ISSUES_CHANGES_VT, iCount, es.gIR_USERN);
						iCount = iCount + 10;
					}
					bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.TEAM_VT, iCount, es.gIR_USERN);
				}//condition ends here
				
				//code added for workflow by Ryazuddin on 11/10/2006
				
				if(sWorkspaceCat.equalsIgnoreCase("WorkFlow-Workspace")){
						bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.WorkFlow_Maintab, iCount, es.gIR_USERN);
						iCount = iCount + 10;
						
						bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.MEETINGS_VT, iCount, es.gIR_USERN);
						iCount = iCount + 10;
						
						bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.WorkFlow_Assessment, iCount, es.gIR_USERN);
						iCount = iCount + 10;
						
						bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.TEAM_VT, iCount, es.gIR_USERN);
					
					
				}// for workflow Ends here
			}
				
			boolean bPrefInsert = ETSWorkspaceDAO.insertWorkspaceIntoPreference(con,sProjectID,sPrimaryEmail);

			// for adding quick links
			boolean bQuickLinks	= AICWorkspaceDAO.populateLinksFirstTime(con,sProjectID,es.gIR_USERN);
			
			// sync users roles for decaf
			if (sWorkspaceCat.equalsIgnoreCase("RESTRICTED-TEAMROOM") || 
					sWorkspaceCat.equalsIgnoreCase("PRIVATE-TEAMROOM")) {
				AICWorkspaceDAO.syncTeamRoomRoles(con,sProjectID,sWorkspaceName,sWorkspaceCompany);
				AICWorkspaceDAO.updateWrkSpcOwnerStatus(con,sProjectID,es.gIR_USERN,Defines.USER_PENDING);
			}

			con.commit();

			con.setAutoCommit(true);

		} catch (SQLException e) {
			sProjectID = "";
			con.rollback();
			con.setAutoCommit(true);
			throw e;
		} catch (Exception e) {
			sProjectID = "";
			con.rollback();
			con.setAutoCommit(true);
			throw e;
		}

		return sProjectID;
	}



	/**
	 * @param con
	 * @param sWorkspaceOwner
	 * @return
	 */
	private boolean sendNotificationEmail(Connection con, String sWorkspaceName, String sCompany,String sWorkspaceOwnerEmail, boolean bEntRequested, boolean bPOCRequested, EdgeAccessCntrl es) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {

			UnbrandedProperties unBrandprop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

			String sEmailSubject = APPLICATION_NAME + " - A new workspace has been created for your use.";

			sEmailStr.append("A new workspace has been created on " + APPLICATION_NAME + "\n");
			sEmailStr.append("and you have been assigned as the workspace owner.\n\n");

			sEmailStr.append("The details of the workspace are as follows: \n");

			sEmailStr.append("===============================================================\n");

			sEmailStr.append("  Name:           " + sWorkspaceName + "\n");
			sEmailStr.append("  Company:        " + sCompany + "\n\n");

			if (bEntRequested && bPOCRequested) {
				sEmailStr.append("An entitlement request and a customer Point Of Contact (POC) has been\n");
				sEmailStr.append("sent to your manager for approval. Use the following URL below to access \n");
				sEmailStr.append("your workspace after your manager has approved both the requests.\n");
				sEmailStr.append(Global.getUrl("ets/" + LANDING_PAGE ) + "?linkid=" + unBrandprop.getLinkID() + "\n\n");

			}

			if (bEntRequested && !bPOCRequested) {
				sEmailStr.append("An entitlement request has been sent to your manager for approval.\n");
				sEmailStr.append("Use the following URL below to access your workspace after your manager\n");
				sEmailStr.append("has approved the request.\n");
				sEmailStr.append(Global.getUrl("ets/" + LANDING_PAGE ) + "?linkid=" + unBrandprop.getLinkID() + "\n\n");
			}

			if (!bEntRequested && bPOCRequested) {
				sEmailStr.append("A customer Point Of Contact (POC) request has been sent to your manager.\n");
				sEmailStr.append("for approval. Use the following URL below to access your workspace after \n");
				sEmailStr.append("your manager has approved the request.\n");
				sEmailStr.append(Global.getUrl("ets/" + LANDING_PAGE ) + "?linkid=" + unBrandprop.getLinkID() + "\n\n");
			}

			if (!bEntRequested && !bPOCRequested) {
				sEmailStr.append("To work with the workspace and setup members, click on the following URL:\n");
				sEmailStr.append(Global.getUrl("ets/" + LANDING_PAGE ) + "?linkid=" + unBrandprop.getLinkID() + "\n\n");
			}

			
			sEmailStr.append(CommonEmailHelper.getEmailFooter(unBrandprop.getAppName()));

			String sToList = sWorkspaceOwnerEmail;

			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
			ETSUtils.insertEmailLog(con,"WORKSPACE","CREATE","CREATE",es.gEMAIL,"",sEmailSubject,sToList,"");

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
	private boolean sendPOCEmail(Connection con, EdgeAccessCntrl es, String sIRId) throws Exception {

		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {
			UnbrandedProperties prop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

			String sName = ETSUtils.getUsersName(con,sIRId);

			String sEmailSubject = APPLICATION_NAME + " - MultiPOC entitlement request for ( " + sName + " )";

			sEmailStr.append(APPLICATION_NAME + " has requested MultiPOC for the following user \n");
			sEmailStr.append("who is going to be managing a workspace and will need to give\n");
			sEmailStr.append("external users access to it.\n\n");

			sEmailStr.append("The details of the request are as follows: \n");
			sEmailStr.append("ID                 : " + sIRId + "\n");
			sEmailStr.append("Name               : " + sName + "\n\n");
			sEmailStr.append("Requested by id    : " + es.gIR_USERN + "\n");
			sEmailStr.append("Requested by       : " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + "\n\n\n");

			
			sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));

			String sToList = prop.getPOCEmail();

			bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;
	}

	private static void printGreyDottedLine(PrintWriter out) {

		out.println("<!-- Gray dotted line -->");
		out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col118\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("<td headers=\"col119\" background=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		out.println("<td headers=\"col120\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<!-- End Gray dotted line -->");


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
	 * @param con
	 * @return
	 */
	private static Vector getValues(Connection con, String sType) throws SQLException, Exception {

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

	/**
	 * @param con
	 * @return
	 */
	private static Vector getValuesSubSector(Connection con, String sMasterType, String sType ) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		Vector vValues = new Vector();

		try {

			sQuery.append("SELECT DISTINCT SECTOR_VALUE, SECTOR_TYPE FROM ETS.AIC_SECTOR_DETAILS WHERE SECTOR_TYPE = '" + sType + "' AND INFO_TYPE = '" + sMasterType + "' ORDER BY SECTOR_VALUE for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sValue = rs.getString("SECTOR_VALUE");

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
	/**
	 * @param parameters
	 */
	private String validateITARStep(ETSParams params) throws Exception {
		/*
		 * This step displays the workspace name, company, type of workspace,
		 * and catetory of workspce
		 */

		PrintWriter out = params.getWriter();
		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();

		StringBuffer sError = new StringBuffer("");

		try {

			String sEntity = request.getParameter("entity");
			if (sEntity == null || sEntity.trim().equals("")) {
				sEntity = "";
			} else {
				sEntity = sEntity.trim();
			}


			String sPersonal = request.getParameter("personal");
			if (sPersonal == null || sPersonal.trim().equals("")) {
				sPersonal = "";
			} else {
				sPersonal = sPersonal.trim();
			}

			String sEducated = request.getParameter("educated");
			if (sEducated == null || sEducated.trim().equals("")) {
				sEducated = "";
			} else {
				sEducated = sEducated.trim();
			}

			if (sEntity.trim().equals("") || sEducated.trim().equals("") || sPersonal.trim().equals("")) {
				sError.append("You have to select all three check boxes before you click I agree.<br />");
			}

			return sError.toString();

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param parameters
	 */
	private String validateSubSectorStep(ETSParams params) throws Exception {
		/*
		 * This step validates the sub sector details for the workspace

		 */

		PrintWriter out = params.getWriter();
		Connection con = params.getConnection();
		HttpServletRequest request = params.getRequest();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();

		StringBuffer sError = new StringBuffer("");

		try {

			String sValidateFinalStepError = validateFinalStep(params);
			if 	(!sValidateFinalStepError.trim().equals("")){
				sError.append(sValidateFinalStepError);
			}

			String sSubSector = request.getParameter("subsector");
			if (sSubSector == null || sSubSector.trim().equals("")) {
				sSubSector = "";
			} else {
				sSubSector = sSubSector.trim();
			}

			if (sSubSector.trim().equals("")) {
				sError.append("You have to select Sub Sector for the Workspace.<br />");
			}

			return sError.toString();

		} catch (Exception e) {
			throw e;
		}
	}

}



