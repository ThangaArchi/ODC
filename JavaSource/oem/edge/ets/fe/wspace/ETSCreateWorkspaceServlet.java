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
import oem.edge.ets.fe.ETSConnectServlet;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSMail;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProperties;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSSyncUser;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;


public class ETSCreateWorkspaceServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.40";

	private static Log logger = EtsLogger.getLogger(ETSCreateWorkspaceServlet.class);
	
	
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
			
			UnbrandedProperties prop = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);
			
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
			EdgeHeader.setHeader("Create a new workspace");
			
			if (sOp.trim().equals("")) {
				EdgeHeader.setSubHeader("Identify workspace type");
			} else if (sOp.trim().equals("second")) {
				if (validateFirstStep(parameters) == false) {
					EdgeHeader.setSubHeader("Identify workspace type");
				} else {
					EdgeHeader.setSubHeader("Enter workspace details");
				}
			} else if (sOp.trim().equals("confirm")) {
				String sError = validateFinalStep(parameters);
				if (!sError.trim().equalsIgnoreCase("")) {
					EdgeHeader.setSubHeader("Enter workspace details");
				} else {				
					if (sITAR.trim().equalsIgnoreCase("Y")) {
						EdgeHeader.setSubHeader("ITAR workspace compliance");
					} else {
						EdgeHeader.setSubHeader("Workspace creation confirmation");
					}
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
			
			// top table to define the content and right sides..
			out.println("<form name=\"CreateWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSCreateWorkspaceServlet.wss\">");
			
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			//out.println("<tr valign=\"top\"><td headers=\"\" width=\"443\" valign=\"top\" class=\"small\"><b>You are logged in as:</b> " + es.gIR_USERN + "</td>");
			out.println("<tr valign=\"top\"><td headers=\"\" width=\"443\" valign=\"top\" class=\"small\"><table summary=\"\" width=\"100%\"><tr><td headers=\"\" width=\"60%\">" + es.gIR_USERN + "</td><td headers=\"\" width=\"40%\" align=\"right\">" + sDate + "</td></tr></table></td>");
			out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			out.println("<td headers=\"\" class=\"small\" align=\"right\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\" width=\"90\" align=\"right\">Secure content</td></tr></table></td></tr>");
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
			} else if (sOp.trim().equals("confirm")) {
				String sError = validateFinalStep(parameters);
				if (!sError.trim().equalsIgnoreCase("")) {
					displayFinalStep(parameters,sError);
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
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" align=\"left\"><img alt=\"protected content\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\" align=\"left\"><span class=\"fnt\">A key icon displayed in a page indicates that the page is secure and password-protected.</span></td></tr></table>");

			out.println(EdgeHeader.printBullsEyeFooter());

		} catch (SQLException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(e), "Error occurred in ETSCreateWorkspaceServlet.");
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(e), "Error occurred in ETSCreateWorkspaceServlet.");
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

		try {
			
			out.println("<input type=\"hidden\" name=\"op\" id=\"label_op\" value=\"second\" />");		 
			out.println("<br />");
			 
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"100%\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
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
				out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");
				
				out.println("<br />");
				
				printGreyDottedLine(out);
			
				out.println("<br />");
				
			}
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\" align=\"left\"><b>Client company:</b></td>");
			out.println("<td headers=\"\" align=\"left\"><b>" + sWorkspaceCompany + "</b><input type=\"hidden\" name=\"workspace_company\" id=\"label_wsComp\" value=\"" + sWorkspaceCompany + "\" /></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");
			 
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\" align=\"left\"><b><span style=\"color:#cc6600\">*</span><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=projtype&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=projtype&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=projtype&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Workspace type</a>:</b></td>");
			if (sWorkspaceCat.trim().equals("Project-Main")) {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"Project-Main\" selected=\"selected\" checked=\"checked\" id=\"w_cat\" /><label for=\"w_cat\"><b>Project - Main workspace</b></label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"Project-Main\" id=\"w_cat\" /><label for=\"w_cat\"><b>Project - Main workspace</b></label></td>");
			}
			
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\" align=\"left\">&nbsp;</td>");
			if (sWorkspaceCat.trim().equals("Project-Sub")) {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"Project-Sub\" selected=\"selected\" checked=\"checked\" id=\"w_cat1\" /><label for=\"w_cat1\"><b>Project - Sub workspace</b></label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"Project-Sub\" id=\"w_cat1\" /><label for=\"w_cat1\"><b>Project - Sub workspace</b></label></td>");
			}
			
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\" align=\"left\">&nbsp;</td>");
			if (sWorkspaceCat.trim().equals("Proposal-Main")) {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"Proposal-Main\" selected=\"selected\" checked=\"checked\" id=\"w_cat2\" /><label for=\"w_cat2\"><b>Proposal - Main workspace</b></label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"Proposal-Main\" id=\"w_cat2\" /><label for=\"w_cat2\"><b>Proposal - Main workspace</b></label></td>");
			}
			
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\" align=\"left\">&nbsp;</td>");
			if (sWorkspaceCat.trim().equals("Proposal-Sub")) {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"Proposal-Sub\" selected=\"selected\" checked=\"checked\" id=\"w_cat3\" /><label for=\"w_cat3\"><b>Proposal - Sub workspace</b></label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"Proposal-Sub\" id=\"w_cat3\" /><label for=\"w_cat3\"><b>Proposal - Sub workspace</b></label></td>");
			}
			
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\" align=\"left\">&nbsp;</td>");
			if (sWorkspaceCat.trim().equals("ClientVoice")) {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"ClientVoice\" selected=\"selected\" checked=\"checked\" id=\"w_cat4\" /><label for=\"w_cat4\"><b>Client Voice</b></label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\"><input type=\"radio\" name=\"workspace_cat\" value=\"ClientVoice\" id=\"w_cat4\" /><label for=\"w_cat4\"><b>Client Voice</b></label></td>");
			}
			
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<br />");
			
			printGreyDottedLine(out);
			
			out.println("<br />");
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\" width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
			out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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
	
			String sTabContracts = request.getParameter("tab_contracts");
			if (sTabContracts == null || sTabContracts.trim().equals("")) {
				sTabContracts = "N";
			} else {
				sTabContracts = sTabContracts.trim();
			}
	
			String sTabAboutSetMet = request.getParameter("tab_aboutsetmet");
			if (sTabAboutSetMet == null || sTabAboutSetMet.trim().equals("")) {
				sTabAboutSetMet = "N";
			} else {
				sTabAboutSetMet = sTabAboutSetMet.trim();
			}

			String sTabFeedback = request.getParameter("tab_survey");
			if (sTabFeedback == null || sTabFeedback.trim().equals("")) {
				sTabFeedback = "N";
			} else {
				sTabFeedback = sTabFeedback.trim();
			}
	
			String sTabSurvey = request.getParameter("tab_feedback");
			if (sTabSurvey == null || sTabSurvey.trim().equals("")) {
				sTabSurvey = "N";
			} else {
				sTabSurvey = sTabSurvey.trim();
			}
			
			String sTabSelf = request.getParameter("tab_self");
			if (sTabSelf == null || sTabSelf.trim().equals("")) {
				sTabSelf = "N";
			} else {
				sTabSelf = sTabSelf.trim();
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

			String sDelTeam = request.getParameter("delivery");
			if (sDelTeam == null || sDelTeam.trim().equals("")) {
				sDelTeam = "";
			} else {
				sDelTeam = sDelTeam.trim();
			}

			String sGeoList = request.getParameter("geography");
			if (sGeoList == null || sGeoList.trim().equals("")) {
				sGeoList = "";
			} else {
				sGeoList = sGeoList.trim();
			}

			String sIndList = request.getParameter("industry");
			if (sIndList == null || sIndList.trim().equals("")) {
				sIndList = "";
			} else {
				sIndList = sIndList.trim();
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

			
			out.println("<input type=\"hidden\" name=\"op\" id=\"label_proj\" value=\"confirm\" />");
			out.println("<input type=\"hidden\" name=\"workspace_type\" value=\"" + sWorkspaceType + "\" />");
			out.println("<input type=\"hidden\" name=\"workspace_cat\" value=\"" + sWorkspaceCat + "\" />");
			out.println("<input type=\"hidden\" name=\"workspace_company\" value=\"" + sWorkspaceCompany + "\" />");
			
			
			out.println("<br />");
			 
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"100%\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
			out.println("</tr>");
			out.println("</table>");
				
			//printGreyDottedLine(out);
			
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
			
			
			if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
				
				String sTemp = "P";
				if (sWorkspaceType.trim().equalsIgnoreCase("project")) {
					sTemp = "P";
				} else {
					sTemp = "O";
				}
				
				Vector vWorkspaces = new Vector();
				
				if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
					vWorkspaces = ETSWorkspaceDAO.getMainWorkspaces(con,sTemp,es.gIR_USERN,true); 
				} else {
					vWorkspaces = ETSWorkspaceDAO.getMainWorkspaces(con,sTemp,es.gIR_USERN,false);
				}
		
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"w_main\">Main workspace:</label></b></td>");
				out.println("<td headers=\"\" align=\"left\"><select name=\"workspace_main\" id=\"w_main\" class=\"iform\" width=\"350px\" style=\"width:350px\">");
				
				out.println("<option value=\"\" selected=\"selected\">Select Main Workspace</option>");
				for (int i = 0; i < vWorkspaces.size(); i++) {
					ETSProj proj  = (ETSProj) vWorkspaces.elementAt(i);
					if (sMainWorkspace.equalsIgnoreCase(proj.getProjectId())) {
						out.println("<option value=\"" + proj.getProjectId() + "\" selected=\"selected\">" + proj.getName() + " [ " + proj.getCompany() + " ] </option>");
					} else {
						out.println("<option value=\"" + proj.getProjectId() + "\">" + proj.getName() + " [ " + proj.getCompany() + " ] </option>");
					}
					
				}
				out.println("</select>");
				out.println("</td>");
				out.println("</tr>");
				out.println("</table>");
		
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");
				out.println("</table>");
				
			}
			
	
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
				out.println("<td headers=\"\" width=\"220\" align=\"left\"><b>Sub workspace company:</b></td>");
			} else {
				out.println("<td headers=\"\" width=\"220\" align=\"left\"><b>Workspace company:</b></td>");
			}
			out.println("<td headers=\"\" align=\"left\">" + sWorkspaceCompany + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
				out.println("<td headers=\"\" width=\"220\" align=\"left\"><b>Sub workspace type:</b></td>");
			} else {
				out.println("<td headers=\"\" width=\"220\" align=\"left\"><b>Workspace type:</b></td>");
			}
			
			if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
				out.println("<td headers=\"\" align=\"left\">" + sWorkspaceType + "</td>");
			} else {
				if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
					out.println("<td headers=\"\" align=\"left\">" + sWorkspaceType + " - Sub Workspace</td>");
				} else {
					out.println("<td headers=\"\" align=\"left\">" + sWorkspaceType + " - Main Workspace</td>");
				}
			}
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");
			
			printGreyDottedLine(out);
			
			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
				out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"w_name\">Sub workspace name:</label></b></td>");
			} else {
				if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
					out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"w_name\">Proposal or opportunity name:</label></b></td>");
				} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
					out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"w_name\">Project name</label></b></td>");
				} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
					out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"w_name\">Workspace name:</label></b></td>");
				}
			}
			
			out.println("<td headers=\"\" align=\"left\"><input type=\"text\" name=\"workspace_name\" value=\"" + sWorkspaceName + "\" id=\"w_name\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			out.println("</tr>");
			if (!sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
					out.println("<td headers=\"\" align=\"left\"><span class=\"small\">[Please match name from E&TS New Business Opportunity Tool]</span></td>");
				} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
					out.println("<td headers=\"\" align=\"left\"><span class=\"small\">[Please match name from Rational Portfolio Manager]</span></td>");
				} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
					out.println("<td headers=\"\" align=\"left\"><span class=\"small\">[Workspace name that will be displayed on E&TS main page.]</span></td>");
				}
				
				out.println("</tr>");
			}
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");
				 
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"w_desc\">Description:</label></b></td>");
			out.println("<td headers=\"\" align=\"left\"><input type=\"text\" name=\"workspace_desc\" value=\"" + sWorkspaceDesc + "\" id=\"w_desc\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");


			if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
				if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
					out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"primary_desc\">Principal's IBM ID:</label></b></td>");
				} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
					out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"primary_desc\">Project manager's IBM ID:</label></b></td>");
				} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
					out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"primary_desc\">Client advocate's IBM ID:</label></b></td>");
				}			
			} else {
				if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
					out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"primary_desc\">Principal's IBM internet e-mail address:</label></b></td>");
				} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
					out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"primary_desc\">Project manager's IBM internet e-mail address:</label></b></td>");
				} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
					out.println("<td headers=\"\" width=\"210\" align=\"left\"><b><label for=\"primary_desc\">Client advocate's IBM internet e-mail address:</label></b></td>");
				}			
			}
						
			if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
				out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"text\" name=\"primary_email\" value=\"" + sPrimaryEmail + "\" id=\"primary_desc\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");				
			} else {
				out.println("<td headers=\"\" align=\"left\" valign=\"top\"><b>" + es.gEMAIL.trim() + "</b><input type=\"hidden\" name=\"primary_email\" value=\"" + es.gEMAIL.trim() + "\" /></td>");
			}
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<script>");
			out.println("var helpwindow = null;");
			out.println("var IE = document.all?true:false;");
			out.println("var tempX = 0;");
			out.println("var tempY = 0;");
			out.println("function getMouseXY(e) {");
			out.println("	if (IE) { // grab the x-y pos.s if browser is IE");
			out.println("		tempX = event.clientX + 50;");
			out.println("		tempY = event.clientY + 150;");
			out.println("	}");
			out.println("	else {  // grab the x-y pos.s if browser is NS");
			out.println("		tempX = e.pageX + 50;");
			out.println("		tempY = e.pageY;");
			out.println("	}  ");
			out.println("	if (tempX < 0){tempX = 0;}");
			out.println("	if (tempY < 0){tempY = 0;}  ");
			out.println("	return true;");
			out.println("}");
			out.println("if (!IE) document.captureEvents(Event.MOUSEMOVE)");
			out.println("document.onmousemove = getMouseXY;");
			out.println("function openHelpWindow(txt) {");
			out.println("    var dims = 'top=' + tempY + ',left=' + tempX + ',height=150,width=200,status=no,toolbar=no,menubar=no,location=no';");
			out.println("    helpwindow = window.open('', 'Function', dims);");
			out.println("    helpwindow.document.write(txt);");
			out.println("}");
			out.println("function closeHelpWindow() {");
			out.println("    if (helpwindow != null) {");
			out.println("        helpwindow.close();");
			out.println("    }");
			out.println("}");
			out.println("</script>");

			
			

			if (sWorkspaceType.trim().equalsIgnoreCase("project")) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Main\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Landing page with project overview information');\" onmouseout=\"closeHelpWindow();\"><b>Main</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabMeeting.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meet\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_meetings\" value=\"Y\" id=\"label_meet\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Project calendar, ability to post events and schedule meetings in workspace');\" onmouseout=\"closeHelpWindow();\"><b>Meeting</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabDocuments.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_doc\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_doc\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Controlled access document repository organized by folder');\" onmouseout=\"closeHelpWindow();\"><b>Documents</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabAsic.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Action item management and links to ASIC Connect design kits (additional entitlements required)');\" onmouseout=\"closeHelpWindow();\"><b>ASIC</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabIssues.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_iss\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_iss\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Tracking and managing issues and changes where issue types are defined by workspace owner');\" onmouseout=\"closeHelpWindow();\"><b>Issues / Changes</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Access management of the workspace');\" onmouseout=\"closeHelpWindow();\"><b>Team</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" colspan=\"2\"><b>NOTE:</b>&nbsp; A total of 5 tabs may be selected for a workspace</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" colspan=\"2\">For more detailed information on the functionality available, go to the on-line training available from the \"Using our tools\" link on the E&TS Connect landing page.</td>");
				out.println("</tr>");
				out.println("</table>");
			} else if (sWorkspaceType.trim().equalsIgnoreCase("proposal")) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Main\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Landing page with project overview information');\" onmouseout=\"closeHelpWindow();\"><b>Main</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabContracts.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_contracts\" value=\"Y\" id=\"label_contract\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_contracts\" value=\"Y\" id=\"label_contract\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Action item management designed to be used during negotiations of a contract.');\" onmouseout=\"closeHelpWindow();\"><b>Contracts</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabDocuments.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_doc\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_documents\" value=\"Y\" id=\"label_doc\" /></td>");
				}
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Controlled access document repository organized by folders');\" onmouseout=\"closeHelpWindow();\"><b>Documents</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabAsic.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_asic\" value=\"Y\" id=\"label_asic\" /></td>");
				}
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Action item management and links to ASIC Connect design kits (additional entitlements required)');\" onmouseout=\"closeHelpWindow();\"><b>ASIC</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");				
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabIssues.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_iss\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_issues\" value=\"Y\" id=\"label_iss\" /></td>");
				}
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Tracking and managing issues and changes where issue types are defined by workspace owner');\" onmouseout=\"closeHelpWindow();\"><b>Issues / Changes</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Access management of the workspace');\" onmouseout=\"closeHelpWindow();\"><b>Team</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" colspan=\"2\"><b>NOTE:</b>&nbsp; A total of 5 tabs may be selected for a workspace</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" colspan=\"2\">For more detailed information on the functionality available, go to the on-line training available from the \"Using our tools\" link on the E&TS Connect landing page.</td>");
				out.println("</tr>");
				out.println("</table>");
			} else if (sWorkspaceType.trim().equalsIgnoreCase("Client Voice")) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\"><b>Workspace tabs:</b></td>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Set/Met Reviews\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Template to work with clients on setting expectations and meeting them.');\" onmouseout=\"closeHelpWindow();\"><b>Set/Met Reviews</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabSelf.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_self\" value=\"Y\" id=\"label_self\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_self\" value=\"Y\" id=\"label_self\" /></td>");
				}
				
				out.println("<td headers=\"\" style=\"cursor: help;\" align=\"left\" onclick=\"openHelpWindow('IBM team members rate perceived client satisfaction');\" onmouseout=\"closeHelpWindow();\"><b>Self Assessment</b></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabSurvey.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_survey\" value=\"Y\" id=\"label_survey\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_survey\" value=\"Y\" id=\"label_survey\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Annual corporate survey results are uploaded when available');\" onmouseout=\"closeHelpWindow();\"><b>Survey</b></td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				if (sTabSelf.equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_feedback\" value=\"Y\" id=\"label_doc\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"left\"><input type=\"checkbox\" name=\"tab_feedback\" value=\"Y\" id=\"label_doc\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Collect and manage feedback from team members');\" onmouseout=\"closeHelpWindow();\"><b>Feedback</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Team\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" style=\"cursor: help;\" onclick=\"openHelpWindow('Access management of the workspace');\" onmouseout=\"closeHelpWindow();\"><b>Team</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" colspan=\"2\"><b>NOTE:</b>&nbsp; A total of 5 tabs may be selected for a workspace</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"210\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" colspan=\"2\">For more detailed information on the functionality available, go to the on-line training available from the \"Using our tools\" link on the E&TS Connect landing page.</td>");
				out.println("</tr>");
				out.println("</table>");
			}

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"210\"><label for=\"label_geo\"><b>Geography:</b></label></td>");
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
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"210\"><label for=\"label_delivery\"><b>Delivery team:</b></label></td>");
			out.println("<td headers=\"\" align=\"left\" >");
			
			out.println("<select style=\"width:200px\" width=\"200px\" name=\"delivery\" id=\"label_delivery\" class=\"iform\">");
	
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
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"210\"><label for=\"label_ind\"><b>Industry:</b></label></td>");
			out.println("<td headers=\"\" align=\"left\" >");
			
			out.println("<select style=\"width:200px\" width=\"200px\" name=\"industry\" id=\"label_ind\" class=\"iform\">");
	
			out.println("<option value=\"\" >Select a industry</option>");
			
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
	

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");


			if (!sWorkspaceType.equalsIgnoreCase("Client Voice")) {
				// dont display itar for client voice workspace..
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"210\"><b>ITAR:</b></td>");
				if (sITAR.trim().equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" align=\"left\" ><input type=\"checkbox\" name=\"itar\" value=\"Y\" id=\"label_itar\" class=\"iform\" checked=\"checked\" /><b><label for=\"label_itar\">&nbsp;This workspace will store ITAR unclassified data.</label></b></td>");
				} else {
					out.println("<td headers=\"\" align=\"left\" ><input type=\"checkbox\" name=\"itar\" value=\"Y\" id=\"label_itar\" class=\"iform\" /><b><label for=\"label_itar\">&nbsp;This workspace will store ITAR unclassified data.</label></b></td>");
				}
				
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" width=\"210\">&nbsp;</td>");
				out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Learn more about ITAR unclassified data requirements.</a></td>");
				out.println("</tr>");
				out.println("</table>");
	
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");
				out.println("</table>");
			}
	
//			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
//			out.println("<tr>");
//			out.println("<td headers=\"\" width=\"10\" align=\"left\" valign=\"middle\">&nbsp;</td>");
//			out.println("<td headers=\"\" width=\"210\" align=\"left\" valign=\"top\"><b><label for=\"label_comments\">Additional comments:</label></b></td>");
//			out.println("<td headers=\"\" align=\"left\"><textarea name=\"comments\" id=\"label_comments\" rows=\"5\" cols=\"40\" class=\"iform\">" + sComments + "</textarea></td>");
//			out.println("</tr>");
//			out.println("</table>");
//	
//			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
//			out.println("<tr>");
//			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
//			out.println("</tr>");
//			out.println("</table>");

	
			out.println("<br />");
			
			printGreyDottedLine(out);
			
			out.println("<br />");
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\" width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
			out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCreateWorkspaceServlet.wss?linkid=" + params.getLinkId() + "&workspace_name=" + URLEncoder.encode(sWorkspaceName) + "&workspace_desc=" + URLEncoder.encode(sWorkspaceDesc) + "&workspace_company=" + URLEncoder.encode(sWorkspaceCompany) + "&workspace_cat=" + URLEncoder.encode(sWorkspaceCat) + "\" >Cancel</a></td></tr></table></td>");
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
			
			if (sWorkspaceType.trim().equalsIgnoreCase("Project") || sWorkspaceType.trim().equalsIgnoreCase("Proposal")) {
				if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
					if (sMainWorkspace.trim().equals("")) {
						sError.append("You must select a <b>Main workspace</b>. <br />");
					}
				}
			}
			
						
			String sWorkspaceName = request.getParameter("workspace_name");
			if (sWorkspaceName == null || sWorkspaceName.trim().equals("")) {
				sWorkspaceName = "";
			} else {
				sWorkspaceName = sWorkspaceName.trim();
			}
	
			if (sWorkspaceName.trim().equals("")) {
				if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
					sError.append("You must enter a <b>Sub workspace name</b>.<br />");
				} else {
					if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
						sError.append("You must enter a <b>Proposal or opportunity name</b>.<br />");
					} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
						sError.append("You must enter a <b>Project name</b>.<br />");
					} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
						sError.append("You must enter a <b>Workspace name</b>.<br />");
					}
				}
			} else {
				if (sWorkspaceName.trim().indexOf(",") >= 0) {
					if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
						sError.append("<b>Sub workspace name</b> cannot contain comma (\",\").<br />");
					} else {
						if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
							sError.append("<b>Proposal or opportunity name</b> cannot contain comma (\",\").<br />");
						} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
							sError.append("<b>Project name</b> cannot contain comma (\",\").<br />");
						} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
							sError.append("<b>Workspace name</b> cannot contain comma (\",\").<br />");
						}
					}
				}
			}

			String sPrimaryEmail = request.getParameter("primary_email");
			if (sPrimaryEmail == null || sPrimaryEmail.trim().equals("")) {
				sPrimaryEmail = "";
			} else {
				sPrimaryEmail = sPrimaryEmail.trim();
			}
	
			if (sPrimaryEmail.trim().equals("")) {
				if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
					sError.append("You must enter a <b>Principal's IBM internet e-mail address</b>.<br />");
				} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
					sError.append("You must enter a <b>Project manager's IBM internet e-mail address</b>.<br />");
				} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
					sError.append("You must enter a <b>Client advocate's IBM internet e-mail address</b>.<br />");
				}
			}	
			
			if (!sPrimaryEmail.trim().equals("")) {
				if (!sPrimaryEmail.trim().toLowerCase().endsWith("ibm.com")) {
					if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
						sError.append("<b>Principal's IBM internet e-mail address</b> has to be IBM employee.<br />");
					} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
						sError.append("<b>Project manager's IBM internet e-mail address</b> has to be IBM employee.<br />");
					} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
						sError.append("<b>Client advocate's IBM internet e-mail address</b> has to be IBM employee.<br />");
					}
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
	
			String sTabIssues = request.getParameter("tab_issues");
			if (sTabIssues == null || sTabIssues.trim().equals("")) {
				sTabIssues = "N";
			} else {
				iTabCount = iTabCount + 1;
			}
	
			String sTabContracts = request.getParameter("tab_contracts");
			if (sTabContracts == null || sTabContracts.trim().equals("")) {
				sTabContracts = "N";
			} else {
				iTabCount = iTabCount + 1;
			}
	
			String sTabAboutSetMet = request.getParameter("tab_aboutsetmet");
			if (sTabAboutSetMet == null || sTabAboutSetMet.trim().equals("")) {
				sTabAboutSetMet = "N";
			} else {
				iTabCount = iTabCount + 1;
			}

			String sTabSurvey = request.getParameter("tab_survey");
			if (sTabSurvey == null || sTabSurvey.trim().equals("")) {
				sTabSurvey = "N";
			} else {
				iTabCount = iTabCount + 1;
			}
	
			String sTabFeedback = request.getParameter("tab_feedback");
			if (sTabFeedback == null || sTabFeedback.trim().equals("")) {
				sTabFeedback = "N";
			} else {
				iTabCount = iTabCount + 1;
			}

			String sTabSelf = request.getParameter("tab_self");
			if (sTabSelf == null || sTabSelf.trim().equals("")) {
				sTabSelf = "N";
			} else {
				iTabCount = iTabCount + 1;
			}

			String sTabAsic = request.getParameter("tab_asic");
			if (sTabAsic == null || sTabAsic.trim().equals("")) {
				sTabAsic = "N";
			} else {
				iTabCount = iTabCount + 1;
			}

			
			if (iTabCount > 5) {
				// cannot allow more than five tabs..
				sError.append("You cannot select more than five (5) tabs for your workspace. <br />");
			}


			String sGeography = request.getParameter("geography");
			if (sGeography == null || sGeography.trim().equals("")) {
				sGeography = "";
			} else {
				sGeography = sGeography.trim();
			}
	
			if (sGeography.trim().equals("")) {
				sError.append("You must select a <b>Geography</b>.<br />");
			}


			String sDelivery = request.getParameter("delivery");
			if (sDelivery == null || sDelivery.trim().equals("")) {
				sDelivery = "";
			} else {
				sDelivery = sDelivery.trim();
			}
	
			if (sDelivery.trim().equals("")) {
				sError.append("You must select a <b>Delivery team</b>.<br />");
			}
	

			String sIndustry = request.getParameter("industry");
			if (sIndustry == null || sIndustry.trim().equals("")) {
				sIndustry = "";
			} else {
				sIndustry = sIndustry.trim();
			}
	
			if (sIndustry.trim().equals("")) {
				sError.append("You must select a <b>Industry</b>.<br />");
			}
			
			// also check to see if the primary contact entered is a valid id...

			if (!sPrimaryEmail.trim().equalsIgnoreCase("") && sPrimaryEmail.trim().toLowerCase().endsWith("ibm.com")) {
				
				ETSUserDetails usrDetails = new ETSUserDetails();
				if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
					usrDetails.setWebId(sPrimaryEmail);
				} else {
					usrDetails.setWebId(es.gIR_USERN);
				}
				
				usrDetails.extractUserDetails(con);
	
				 if (usrDetails.isUserExists()){
					 if (usrDetails.getUserType() == usrDetails.USER_TYPE_INVALID){
						if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
							if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
								sError.append("<b>Principal's IBM ID</b> entered is not a valid IBM ID.<br />");
							} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
								sError.append("<b>Project manager's IBM ID </b> entered is not a valid IBM ID.<br />");
							} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
								sError.append("<b>Client advocate's IBM ID</b> entered is not a valid IBM ID.<br />");
							}
						} else {
							if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
								sError.append("<b>Principal's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
							} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
								sError.append("<b>Project manager's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
							} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
								sError.append("<b>Client advocate's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
							}
						}
					 }
				 } else {
				 	
					// try to sync from UD and see if the id exists
				 	
					ETSSyncUser syncUser = new ETSSyncUser();
					if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
						syncUser.setWebId(sPrimaryEmail);
					} else {
						syncUser.setWebId(es.gIR_USERN);
					}
					
					ETSStatus status = syncUser.syncUser(con);
	
					usrDetails = new ETSUserDetails();
					if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
						usrDetails.setWebId(sPrimaryEmail);
					} else {
						usrDetails.setWebId(es.gIR_USERN);
					}
					
					usrDetails.extractUserDetails(con);
	
					 if (usrDetails.isUserExists()){
						 if (usrDetails.getUserType() == usrDetails.USER_TYPE_INVALID){
							if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
								if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
									sError.append("<b>Principal's IBM ID</b> entered is not a valid IBM ID.<br />");
								} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
									sError.append("<b>Project manager's IBM ID</b> entered is not a valid IBM ID.<br />");
								} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
									sError.append("<b>Client advocate's IBM ID</b> entered is not a valid IBM ID.<br />");
								}
							} else {
								if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
									sError.append("<b>Principal's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
								} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
									sError.append("<b>Project manager's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
								} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
									sError.append("<b>Client advocate's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
								}
							}
						 } else {
							if (sWorkspaceType.trim().equalsIgnoreCase("Project") || sWorkspaceType.trim().equalsIgnoreCase("Proposal")) {
								if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
									if (sMainWorkspace.trim().equals("")) {
										//check to see if the person entered as owner belongs to the main workspace.. 
										//if not, dont allow to proceed...
										if (!ETSWorkspaceDAO.isMemberOfWorkspace(con,sMainWorkspace,sPrimaryEmail)) {
											if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
												if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
													sError.append("<b>Principal's IBM ID entered</b> has to member of main workspace selected.<br />");
												} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
													sError.append("<b>Project manager's IBM ID entered</b> has to be member of main workspace selected.<br />");
												} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
													sError.append("<b>Client advocate's IBM ID entered</b> has to be member of main workspace selected.<br />");
												}
											} else {
												if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
													sError.append("<b>Principal's IBM internet e-mail address</b> has to member of main workspace selected.<br />");
												} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
													sError.append("<b>Project manager's IBM internet e-mail address</b> has to be member of main workspace selected.<br />");
												} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
													sError.append("<b>Client advocate's IBM internet e-mail address</b> has to be member of main workspace selected.<br />");
												}
											}
										}
									}
								}
							}
						 }
					 } else {
						if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD")) {
							if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
								sError.append("<b>Principal's IBM ID</b> entered is not a valid e-mail address.<br />");
							} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
								sError.append("<b>Project manager's IBM ID</b> entered is not a valid e-mail address.<br />");
							} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
								sError.append("<b>Client advocate's IBM ID</b> entered is not a valid e-mail address.<br />");
							}
						} else {
							if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
								sError.append("<b>Principal's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
							} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
								sError.append("<b>Project manager's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
							} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
								sError.append("<b>Client advocate's IBM internet e-mail address</b> entered is not a valid e-mail address.<br />");
							}
						}
					 }
				 }
			}
			
			// check to see if the workspace name enterd already exists in the system
	
			if (!sWorkspaceName.trim().equals("")) {
				if (ETSWorkspaceDAO.checkIfWorkspaceNameExists(con,sWorkspaceName)) {
					if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
						sError.append("A workspace with this name already exists in the system. Please specify a different <b>Sub workspace name</b>.<br />");
					} else {
						if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
							sError.append("A workspace with this name already exists in the system. Please specify a different <b>Proposal or opportunity name</b>.<br />");
						} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
							sError.append("A workspace with this name already exists in the system. Please specify a different <b>Project name</b>.<br />");
						} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
							sError.append("A workspace with this name already exists in the system. Please specify a different <b>Workspace name</b>.<br />");
						}
					}
				}
			}
			
			String sITAR = request.getParameter("itar");
			if (sITAR == null || sITAR.trim().equals("")) {
				sITAR = "";
			} else {
				sITAR = sITAR.trim();
			}
			
			
			if (sITAR.trim().equalsIgnoreCase("Y")) {

// *****************************************************************
				//if (sTabMeeting.equalsIgnoreCase("Y") || sTabAsic.equalsIgnoreCase("Y") || sTabIssues.equalsIgnoreCase("Y") || sTabContracts.equalsIgnoreCase("Y")) {
				if (sTabAsic.equalsIgnoreCase("Y") || sTabContracts.equalsIgnoreCase("Y")) {
					// these tabs cannot exist in itar workspace..									
					sError.append("ITAR workspace must have <b>Main</b>, <b>Meetings</b>, <b>Documents</b>, <b>Issues</b> and <b>Team</b> tabs only. All other tabs are not supported.<br />");		
				}
//***************************************************************** */
				if (sTabDocuments == null || sTabDocuments.equalsIgnoreCase("") || sTabDocuments.equalsIgnoreCase("N")) {
					// documents tab is mandatory								
					//sError.append("Please select <b>Documents</b> tab. ITAR workspace must have <b>Main</b>, <b>Documents</b> and <b>Team</b> tabs. <br />");		
					sError.append("Please select <b>Documents</b> tab. All ITAR workspace must have <b>Documents</b> tab. <br />");
				}
			}
			
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
		
		boolean bAdmin = es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD");
		
		
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
		
		String sTabIssues = request.getParameter("tab_issues");
		if (sTabIssues == null || sTabIssues.trim().equals("")) {
			sTabIssues = "N";
		} else {
			sTabIssues = sTabIssues.trim();
		}
		
		String sTabContracts = request.getParameter("tab_contracts");
		if (sTabContracts == null || sTabContracts.trim().equals("")) {
			sTabContracts = "N";
		} else {
			sTabContracts = sTabContracts.trim();
		}
		
		String sTabAboutSetMet = request.getParameter("tab_aboutsetmet");
		if (sTabAboutSetMet == null || sTabAboutSetMet.trim().equals("")) {
			sTabAboutSetMet = "N";
		} else {
			sTabAboutSetMet = sTabAboutSetMet.trim();
		}

		String sTabSurvey = request.getParameter("tab_survey");
		if (sTabSurvey == null || sTabSurvey.trim().equals("")) {
			sTabSurvey = "N";
		} else {
			sTabSurvey = sTabSurvey.trim();
		}
		
		String sTabFeedback = request.getParameter("tab_feedback");
		if (sTabFeedback == null || sTabFeedback.trim().equals("")) {
			sTabFeedback = "N";
		} else {
			sTabFeedback = sTabFeedback.trim();
		}
		
		String sTabSelf = request.getParameter("tab_self");
		if (sTabSelf == null || sTabSelf.trim().equals("")) {
			sTabSelf = "N";
		} else {
			sTabSelf = sTabSelf.trim();
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
		
		String sDelTeam = request.getParameter("delivery");
		if (sDelTeam == null || sDelTeam.trim().equals("")) {
			sDelTeam = "";
		} else {
			sDelTeam = sDelTeam.trim();
		}
		
		String sGeoList = request.getParameter("geography");
		if (sGeoList == null || sGeoList.trim().equals("")) {
			sGeoList = "";
		} else {
			sGeoList = sGeoList.trim();
		}
		
		String sIndList = request.getParameter("industry");
		if (sIndList == null || sIndList.trim().equals("")) {
			sIndList = "";
		} else {
			sIndList = sIndList.trim();
		}
		
		String sComments = request.getParameter("comments");
		if (sComments == null || sComments.trim().equals("")) {
			sComments = "";
		} else {
			sComments = sComments.trim();
		}
		
		try {
			
			if (sITAR.trim().equalsIgnoreCase("Y")) {
				
				out.println("<input type=\"hidden\" name=\"op\" id=\"label_proj\" value=\"itarfinal\" />");
				
				out.println("<input type=\"hidden\" name=\"op\" id=\"label_proj\" value=\"confirm\" />");
				out.println("<input type=\"hidden\" name=\"workspace_name\" value=\"" + sWorkspaceName + "\" />");
				out.println("<input type=\"hidden\" name=\"workspace_desc\" value=\"" + sWorkspaceDesc + "\" />");
				out.println("<input type=\"hidden\" name=\"workspace_type\" value=\"" + sWorkspaceType + "\" />");
				out.println("<input type=\"hidden\" name=\"workspace_cat\" value=\"" + sWorkspaceCat + "\" />");
				out.println("<input type=\"hidden\" name=\"workspace_company\" value=\"" + sWorkspaceCompany + "\" />");
			
				out.println("<input type=\"hidden\" name=\"workspace_main\" value=\"" + sMainWorkspace + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_meetings\" value=\"" + sTabMeeting + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_documents\" value=\"" + sTabDocuments + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_issues\" value=\"" + sTabIssues + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_contracts\" value=\"" + sTabContracts + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_aboutsetmet\" value=\"" + sTabAboutSetMet + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_survey\" value=\"" + sTabSurvey + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_feedback\" value=\"" + sTabFeedback + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_self\" value=\"" + sTabSelf + "\" />");
				out.println("<input type=\"hidden\" name=\"tab_asic\" value=\"" + sTabAsic + "\" />");
				out.println("<input type=\"hidden\" name=\"delivery\" value=\"" + sDelTeam + "\" />");
				out.println("<input type=\"hidden\" name=\"geography\" value=\"" + sGeoList + "\" />");
				out.println("<input type=\"hidden\" name=\"industry\" value=\"" + sIndList + "\" />");
				out.println("<input type=\"hidden\" name=\"comments\" value=\"" + sComments + "\" />");
				out.println("<input type=\"hidden\" name=\"primary_email\" value=\"" + sPrimaryEmail + "\" />");
				out.println("<input type=\"hidden\" name=\"itar\" value=\"" + sITAR + "\" />");
				
				// showitar screens
				out.println("<br />");
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
				out.println("<td headers=\"\" align=\"left\" class=\"subtitle\"><span style=\"color:#ff3333\"><b>ATTENTION and WARNING</b></span></td>");
				out.println("</tr></table>");
				
				out.println("<br />");
				
//				printGreyDottedLine(out);
//				out.println("<br />");
				
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\" align=\"left\">You are requesting creation and ownership of a workspace to store and exchange unclassified ITAR (International Traffic in Arms Regulations) data and technology with external clients.</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");
				
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\" align=\"left\">To receive authorization you MUST ENSURE (please check all boxes):</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");
				
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				if (sPersonal.trim().equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"personal\" id=\"label_personal\" value=\"Y\" class=\"iform\" checked=\"checked\" /></td>");  
				} else {
					out.println("<td headers=\"\" width=\"16\" headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"personal\" id=\"label_personal\" value=\"Y\" class=\"iform\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\"><label for=\"label_personal\">You are PERSONALLY ITAR educated, and will re-educate anually. You must certify that you are a US person; and disclose that you have reviewed the applicable Technology Control Plan (TCP) that controls your sites work environment.</label></td>");
				out.println("</tr>");
				out.println("</table>");
				
				out.println("<br />");
				
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				if (sEducated.trim().equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" headers=\"\" align=\"left\"  valign=\"top\"><input type=\"checkbox\" name=\"educated\" id=\"label_edu\" value=\"Y\" class=\"iform\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" headers=\"\" align=\"left\"  valign=\"top\"><input type=\"checkbox\" name=\"educated\" id=\"label_edu\" value=\"Y\" class=\"iform\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\"><label for=\"label_edu\">You must ensure ALL IBM persons you will allow access to this team room are ITAR educated, US person certified and disclosed as noted above.</label></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
				
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				if (sEntity.trim().equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"16\" headers=\"\" align=\"left\"  valign=\"top\"><input type=\"checkbox\" name=\"entity\" id=\"label_entity\" value=\"Y\" class=\"iform\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" headers=\"\" align=\"left\"  valign=\"top\"><input type=\"checkbox\" name=\"entity\" id=\"label_entity\" value=\"Y\" class=\"iform\" /></td>");
				}
				
				out.println("<td headers=\"\" align=\"left\"><label for=\"label_entity\">All non-IBM entities who will have access to this room MUST ensure (via document) to you that they understand ITAR regulations and will not expose your work to any US export or Department of Defense violations.</label></td>");
				out.println("</tr>");
				out.println("</table>");


				out.println("<br /><br />");
				
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\" align=\"left\">ANY violations of these ITAR regulations is to be reported immediately to your local Export Regulations Coordinator (ERC).</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br /><br />");
				
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\" align=\"left\">ANY questions on this subject matter should be adressed to your site ERC or E&TS divisional ERC (Joe Morris/Endicott/IBM) before continuing.</td>");
				out.println("</tr>");
				out.println("</table>");


				out.println("<br /><br />");
				
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\" align=\"left\"><b>By checking the boxes above you are confirming that YOU understand and will comply with all US ITAR regulations; the above responsibilities are understood and have been compiled with, including the documentation to support your compliance and to be audit ready at all times.</b></td>");
				out.println("</tr>");
				out.println("</table>");
				
				out.println("<br />");
			
				printGreyDottedLine(out);
			
				out.println("<br />");
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\" width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "i_agree.gif\" width=\"120\" height=\"21\" alt=\"I agree\" /></td>");
				out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCreateWorkspaceServlet.wss?linkid=" + params.getLinkId() + "&workspace_company=" + URLEncoder.encode(sWorkspaceCompany) + "\" >Cancel</a></td></tr></table></td>");
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
	private void createWorkspace(ETSParams params) throws SQLException, Exception {
		
		 /*
		  * Check if ITAR has been selected, if yes, then display the ITAR certification page.
		  * If no, then create the workspace according to the logic below...
		  * 
		  * 
		  * 1. check to see if the logged in person is admin or not.
		  *    - if admin
		  *       - then create the workspace
		  *       - if the primary contact entered does not have ets projects, create a request
		  *       - if the primary contact entered does not have POC, create a request for POC (MULTIPOC)
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
			
			
			UnbrandedProperties unBrandprop = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);
			
			boolean bAdmin = es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD");


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
	
			String sTabContracts = request.getParameter("tab_contracts");
			if (sTabContracts == null || sTabContracts.trim().equals("")) {
				sTabContracts = "N";
			} else {
				sTabContracts = sTabContracts.trim();
			}
	
			String sTabAboutSetMet = request.getParameter("tab_aboutsetmet");
			if (sTabAboutSetMet == null || sTabAboutSetMet.trim().equals("")) {
				sTabAboutSetMet = "N";
			} else {
				sTabAboutSetMet = sTabAboutSetMet.trim();
			}

			String sTabSurvey = request.getParameter("tab_survey");
			if (sTabSurvey == null || sTabSurvey.trim().equals("")) {
				sTabSurvey = "N";
			} else {
				sTabSurvey = sTabSurvey.trim();
			}
	
			String sTabFeedback = request.getParameter("tab_feedback");
			if (sTabFeedback == null || sTabFeedback.trim().equals("")) {
				sTabFeedback = "N";
			} else {
				sTabFeedback = sTabFeedback.trim();
			}
			
			String sTabSelf = request.getParameter("tab_self");
			if (sTabSelf == null || sTabSelf.trim().equals("")) {
				sTabSelf = "N";
			} else {
				sTabSelf = sTabSelf.trim();
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
	
			String sDelTeam = request.getParameter("delivery");
			if (sDelTeam == null || sDelTeam.trim().equals("")) {
				sDelTeam = "";
			} else {
				sDelTeam = sDelTeam.trim();
			}
	
			String sGeoList = request.getParameter("geography");
			if (sGeoList == null || sGeoList.trim().equals("")) {
				sGeoList = "";
			} else {
				sGeoList = sGeoList.trim();
			}
	
			String sIndList = request.getParameter("industry");
			if (sIndList == null || sIndList.trim().equals("")) {
				sIndList = "";
			} else {
				sIndList = sIndList.trim();
			}
	
			String sComments = request.getParameter("comments");
			if (sComments == null || sComments.trim().equals("")) {
				sComments = "";
			} else {
				sComments = sComments.trim();
			}
			
			out.println("<br />");
			
			 
			String sProjectOrProposal = "P";
			 
			if (sWorkspaceType.trim().equalsIgnoreCase("Client Voice")) {
				sProjectOrProposal = "C";
			} else if (sWorkspaceType.trim().equalsIgnoreCase("Project")) {
				sProjectOrProposal = "P";
			} else if (sWorkspaceType.trim().equalsIgnoreCase("Proposal")) {
				sProjectOrProposal = "O";
			}
			
			
			if (bAdmin) {
				
				// create the project.
				// check if the primary contact entered has ETSProjects entitlement, if not request for one.
				// check if the primary contact entered has POC entitlement, if not request for one.
				
				
				String sProjId = create(params);
				
				boolean bEntRequested = false;
				boolean bPOCEntRequested = false;
				
				
				// added for 5.4.1 - sathish
				boolean bITARRequested = false;
				boolean bHasITAREnt = false;
				
				if (sITAR.trim().equalsIgnoreCase("Y")) {
					
					bHasITAREnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sPrimaryEmail,Defines.ITAR_ENTITLEMENT);
					if (!bHasITAREnt & !sProjId.trim().equalsIgnoreCase("")) {
						
						// user does not have itar entitlement.. so request for itar project
						ETSUserAccessRequest uar = new ETSUserAccessRequest();
						uar.setTmIrId(sPrimaryEmail);
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
				
				// end of addition - sathish
				
				// check if the primary contact entered has ETSProjects entitlement, if not request for one.
				
				boolean bHasEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sPrimaryEmail,Defines.ETS_ENTITLEMENT);
				
				if (!bHasEnt & !sProjId.trim().equalsIgnoreCase("")) {

					ETSUserAccessRequest uar = new ETSUserAccessRequest();
					uar.setTmIrId(sPrimaryEmail);
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
				
				// check if the primary contact entered has MULTIPOC entitlement, if not request for one.
				
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

				
				out.println("<br /><br />");
				
				if (!sProjId.trim().equalsIgnoreCase("")) {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
						out.println("<td headers=\"\" align=\"left\"><b>A new Sub workspace has been created and is ready for use.</b></td>");
					} else {
						out.println("<td headers=\"\" align=\"left\"><b>A new workspace has been created and is ready for use.</b></td>");
					}
					
					out.println("</tr>");
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
						out.println("<td headers=\"\" align=\"left\"><ul><li>An entitlement request has been sent to user's manager " + sMgrEmail + " for approval.</li></ul></td>");
						out.println("</tr>");
						out.println("</table>");
					} else {
						if (!bHasEnt & !sProjId.trim().equalsIgnoreCase("")) {
							out.println("<br />");
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><ul><li>An error occured while requesting entitlement. Please contact your system administrator.</li></ul></span></td>");
							out.println("</tr>");
							out.println("</table>");
						}
					}
					

					if (bPOCEntRequested) {					
						out.println("<br />");
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"\" align=\"left\"><ul><li>A customer Point Of Contact (POC) entitlement request has been sent to user's manager " + sMgrEmail + " for approval.</li></ul></td>");
						out.println("</tr>");
						out.println("</table>");
					} else {
						if (!bHasPOCEnt & !sProjId.trim().equalsIgnoreCase("")) {
							out.println("<br />");
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><ul><li>An error occured while requesting customer Point of Contact (POC) entitlement. Please contact your system administrator.</li></ul></span></td>");
							out.println("</tr>");
							out.println("</table>");
						}
					}
					
//					added for 5.4.1 - sathish
					if (sITAR.trim().equalsIgnoreCase("Y")) {
						if (bITARRequested) {
							out.println("<br />");
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"\" align=\"left\"><ul><li>An ITAR entitlement request has been sent to the user's manager " + sMgrEmail + " for approval.</li></ul></td>");
							out.println("</tr>");
							out.println("</table>");
						} else {
							if (!bHasITAREnt & !sProjId.trim().equalsIgnoreCase("")) {
								out.println("<br />");
								out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
								out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><ul><li>An error occured while requesting ITAR entitlement. Please contact your system administrator.</li></ul></span></td>");
								out.println("</tr>");
								out.println("</table>");
							}
							
						}
					}
					// end of addition - sathish
					
					sendNotificationEmail(con,sWorkspaceName,sWorkspaceCompany,sPrimaryEmail,bEntRequested,bPOCEntRequested,es);
					if (bPOCEntRequested) {
						sendPOCEmail(con,es,sPrimaryEmail);
					}
					
				} else {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><b>There was an error creating the workspace. Please try again later. If the problem persists, please contact your primary contact.</b></span></td>");
					out.println("</tr>");
					out.println("</table>");
				}
				
				out.println("<br /><br />");
				
				printGreyDottedLine(out);
				out.println("<br />");
		
				if (!sProjId.trim().equalsIgnoreCase("")) {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"\" width=\"200\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to " + sWorkspaceName + "\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?linkid=" + params.getLinkId() + "&proj=" + sProjId + "\" >Go to '" + sWorkspaceName + "'</a></td></tr></table></td>");
					out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to E&TS Connect home page\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Go to E&TS Connect home page</a></td></tr></table></td>");
					out.println("</tr></table>");
				} else {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"\" width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + unBrandprop.getLinkID() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
					out.println("</tr></table>");
				}
													
			} else {
				 
				// Find out if the primary contact has ets projects
				// changing sPrimaryEmail as logged in user id after verifying with Anne - CSR9527 - sathish
				
				//boolean bHasEntitlement = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sPrimaryEmail,Defines.ETS_ENTITLEMENT);
				boolean bHasEntitlement = ETSWorkspaceDAO.doesUserHaveEntitlement(con,es.gIR_USERN.trim(),Defines.ETS_ENTITLEMENT);
				
				if (bHasEntitlement) {
					
					// the primary contact has entitlement.. 
					// create the project.
					// check if the primary contact has POC entitlement, if not request for one.
					
					if (sITAR.trim().equalsIgnoreCase("Y")) {
						
						// IF ITAR WORKSPACE THEN DONT CREATE IT. BASED ON ANNE'S REQUEST
						
						ETSProperties prop = new ETSProperties();
		
						ETSMail mail = new ETSMail();
						StringBuffer sEmailStr = new StringBuffer("");
						StringBuffer sEmailTo = new StringBuffer("");
	
						if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
							mail.setSubject("E&TS Connect: Request for new Sub workspace creation.");
							sEmailStr.append("A request to create a new Sub workspace has been generated from E&TS Connect.\n\n");
						} else {
							mail.setSubject("E&TS Connect: Request for new workspace creation.");
							sEmailStr.append("A request to create a new workspace has been generated from E&TS Connect.\n\n");
						}

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
						if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
							sEmailStr.append("Principal's email    : " + es.gEMAIL + "\n");
						} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
							sEmailStr.append("Project manager's email : " + es.gEMAIL + "\n");
						} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
							sEmailStr.append("Project manager's email : " + es.gEMAIL + "\n");
						}			
					
						if (!sMainWorkspace.trim().equalsIgnoreCase("")) {
							sEmailStr.append("Main Workspace name     : " + ETSWorkspaceDAO.getWorkspaceName(con,sMainWorkspace) + "\n");
						}
					
						if (sWorkspaceType.equalsIgnoreCase("Proposal") || sWorkspaceType.equalsIgnoreCase("Project")) {
							sEmailStr.append("Workspace tabs          : Main\n");
						} else {
							sEmailStr.append("Workspace tabs          : Set/Met reviews\n");
						}
						if (sTabMeeting.equalsIgnoreCase("Y")) {
							sEmailStr.append("                        : Meetings\n");
						}
						if (sTabDocuments.equalsIgnoreCase("Y")) {
							sEmailStr.append("                        : Documents\n");
						}
						if (sTabIssues.equalsIgnoreCase("Y")) {
							sEmailStr.append("                        : Issues\n");
						}
						if (sTabContracts.equalsIgnoreCase("Y")) {
							sEmailStr.append("                        : Contracts\n");
						}
						if (sTabAsic.equalsIgnoreCase("Y")) {
							sEmailStr.append("                        : ASIC\n");
						}
						if (sTabSelf.equalsIgnoreCase("Y")) {
							sEmailStr.append("                        : Self assessment\n");
						}
						if (sTabFeedback.equalsIgnoreCase("Y")) {
							sEmailStr.append("                        : Feedback\n");
						}
						sEmailStr.append("                        : Team\n");
					
						sEmailStr.append("Geography               : " + sGeoList + "\n");
						sEmailStr.append("Delivery team           : " + sDelTeam + "\n");
						sEmailStr.append("Industry                : " + sIndList + "\n");
					
						if (sITAR.trim().equalsIgnoreCase("Y")) {
							sEmailStr.append("ITAR                    : Yes\n");
						}
						sEmailStr.append("\n\n");
						sEmailStr.append("Additional comments: \n");
						sEmailStr.append(sComments + "\n\n");

						sEmailStr.append("===============================================================\n");
						sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
						sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
						sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
						sEmailStr.append("of on demand tools that is available online 24/7.\n");
						sEmailStr.append("===============================================================\n");
						sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
						sEmailStr.append("===============================================================\n\n");
	
						mail.setMessage(sEmailStr.toString());
						mail.setTo(prop.getAdminEmail());
						mail.setFrom(es.gEMAIL);
						mail.setBcc("");
						mail.setCc("");
					
						boolean bSent = ETSUtils.sendEmail(mail);
						
						
						// added for 5.4.1 - sathish
						boolean bITARRequested = false;
						boolean bHasITAREnt = false;
						
					
						//bHasITAREnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sPrimaryEmail,Defines.ITAR_ENTITLEMENT);
						bHasITAREnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,es.gIR_USERN,Defines.ITAR_ENTITLEMENT);
						if (!bHasITAREnt & bSent) {
					
							// user does not have itar entitlement.. so request for itar project
							ETSUserAccessRequest uar = new ETSUserAccessRequest();
							//uar.setTmIrId(sPrimaryEmail);
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
				
						// end of addition - sathish
						
						boolean bPOCEntRequested = false;
						//boolean bHasPOCEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,sPrimaryEmail,Defines.MULTIPOC);
						boolean bHasPOCEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,es.gIR_USERN.trim(),Defines.MULTIPOC);
				
						if (!bHasPOCEnt & bSent) {
					
							ETSUserAccessRequest uar = new ETSUserAccessRequest();
							//uar.setTmIrId(sPrimaryEmail);
							uar.setTmIrId(es.gIR_USERN);
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
					
						out.println("<br />");
					
						if (bSent) {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
								out.println("<td headers=\"\" align=\"left\"><b>Your request to create a new Sub workspace has been submitted to E&TS Connect administrator for processing. It may take up to 5 business days to process your request. You can contact us at <a href=\"mailto:etsadmin@us.ibm.com\">etsadmin@us.ibm.com</a> if you have any questions.</b></td>");
							} else {
								out.println("<td headers=\"\" align=\"left\"><b>Your request to create a new workspace has been submitted to E&TS Connect administrator for processing. It may take up to 5 business days to process your request. You can contact us at <a href=\"mailto:etsadmin@us.ibm.com\">etsadmin@us.ibm.com</a> if you have any questions.</b></td>");
							}
							out.println("</tr>");
							out.println("</table>");
					
							// have to get the users edge id...
					
							//String sEdgeId = ETSUtils.getUserEdgeIdFromAMT(con,sPrimaryEmail);
							String sEdgeId = ETSUtils.getUserEdgeIdFromAMT(con,es.gIR_USERN);
							String sMgrEmail = "";
				
							if (!sEdgeId.trim().equalsIgnoreCase("")) {
								sMgrEmail = ETSUtils.getManagersEMailFromDecafTable(con,sEdgeId);
							} else {
								sMgrEmail = "";
							}
					
							if (!sMgrEmail.trim().equalsIgnoreCase("")) {
								sMgrEmail = "at " + sMgrEmail; 	
							}

							if (bITARRequested) {
								out.println("<br />");
								out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
								out.println("<td headers=\"\" align=\"left\"><ul><li>An ITAR entitlement request has been sent to the user's manager " + sMgrEmail + " for approval.</li></ul></td>");
								out.println("</tr>");
								out.println("</table>");
							} else {
								if (!bHasITAREnt & bSent) {
									out.println("<br />");
									out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
									out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><ul><li>An error occured while requesting ITAR entitlement. Please contact your system administrator.</li></ul></span></td>");
									out.println("</tr>");
									out.println("</table>");
								}
						
							}
							
							if (bPOCEntRequested) {					
								out.println("<br />");
								out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
								out.println("<td headers=\"\" align=\"left\"><ul><li>A customer Point Of Contact (POC) entitlement request has been sent to user's manager " + sMgrEmail + " for approval.</li></ul></td>");
								out.println("</tr>");
								out.println("</table>");
							} else {
								if (!bHasPOCEnt & bSent) {
									out.println("<br />");
									out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
									out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><ul><li>An error occured while requesting customer Point of Contact (POC) entitlement. Please contact your system administrator.</li></ul></span></td>");
									out.println("</tr>");
									out.println("</table>");
								}
							}
					
							
						} else {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><b>There was an error when submitting your request to create workspace. Please try again later. If the problem persists, please contact your primary contact.</b></span></td>");
							out.println("</tr>");
							out.println("</table>");
						}
						out.println("<br />");
					
						printGreyDottedLine(out);
						out.println("<br />");
		
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"\" width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + unBrandprop.getLinkID() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
						out.println("</tr></table>");
						
					} else {
						
						String sProjId = create(params);
						
						boolean bPOCEntRequested = false;
				
						// check if the primary contact entered has MULTIPOC entitlement, if not request for one.
				
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
					
						out.println("<br /><br />");
				
						if (!sProjId.trim().equalsIgnoreCase("")) {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
							out.println("<tr>");
							if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
								out.println("<td headers=\"\" align=\"left\"><b>A new Sub workspace has been created and is ready for use.</b></td>");
							} else {
								out.println("<td headers=\"\" align=\"left\"><b>A new workspace has been created and is ready for use.</b></td>");
							}
						
							out.println("</tr>");

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
							
							if (bPOCEntRequested) {					
								out.println("<br />");
								out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
								out.println("<td headers=\"\" align=\"left\"><ul><li>A customer Point Of Contact (POC) entitlement request has been sent to user's manager " + sMgrEmail + " for approval.</li></ul></td>");
								out.println("</tr>");
								out.println("</table>");
							} else {
								if (!bHasPOCEnt & !sProjId.trim().equalsIgnoreCase("")) {
									out.println("<br />");
									out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
									out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><ul><li>An error occured while requesting customer Point of Contact (POC) entitlement. Please contact your system administrator.</li></ul></span></td>");
									out.println("</tr>");
									out.println("</table>");
								}
							}
							out.println("</table>");
						} else {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><b>There was an error creating the workspace. Please try again later. If the problem persists, please contact your primary contact.</b></span></td>");
							out.println("</tr>");
							out.println("</table>");
						}
				
						out.println("<br /><br />");
					
					
						printGreyDottedLine(out);
						out.println("<br />");
		
						if (!sProjId.trim().equalsIgnoreCase("")) {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"\" width=\"200\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to " + sWorkspaceName + "\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?linkid=" + params.getLinkId() + "&proj=" + sProjId + "\" >Go to '" + sWorkspaceName + "'</a></td></tr></table></td>");
							out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to E&TS Connect home page\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Go to E&TS Connect home page</a></td></tr></table></td>");
							out.println("</tr></table>");
						} else {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
							out.println("<td headers=\"\" width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + unBrandprop.getLinkID() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
							out.println("</tr></table>");
						}
					
						if (bPOCEntRequested) {
							sendPOCEmail(con,es,sPrimaryEmail);
						}
						
					}
					
				} else {
					
					// the primary contact does not have entitlement. 
					// forward this request as email to ets admin...
					
					ETSProperties prop = new ETSProperties();
		
					ETSMail mail = new ETSMail();
					StringBuffer sEmailStr = new StringBuffer("");
					StringBuffer sEmailTo = new StringBuffer("");
	
					if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
						mail.setSubject("E&TS Connect: Request for new Sub workspace creation.");
						sEmailStr.append("A request to create a new Sub workspace has been generated from E&TS Connect.\n\n");
					} else {
						mail.setSubject("E&TS Connect: Request for new workspace creation.");
						sEmailStr.append("A request to create a new workspace has been generated from E&TS Connect.\n\n");
					}

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
					if (sWorkspaceType.equalsIgnoreCase("Proposal")) {
						sEmailStr.append("Principal's email    : " + sPrimaryEmail + "\n");
					} else if (sWorkspaceType.equalsIgnoreCase("Project")) {
						sEmailStr.append("Project manager's email : " + sPrimaryEmail + "\n");
					} else if (sWorkspaceType.equalsIgnoreCase("Client Voice")) {
						sEmailStr.append("Project manager's email : " + sPrimaryEmail + "\n");
					}			
					
					if (!sMainWorkspace.trim().equalsIgnoreCase("")) {
						sEmailStr.append("Main Workspace name     : " + ETSWorkspaceDAO.getWorkspaceName(con,sMainWorkspace) + "\n");
					}
					
					if (sWorkspaceType.equalsIgnoreCase("Proposal") || sWorkspaceType.equalsIgnoreCase("Project")) {
						sEmailStr.append("Workspace tabs          : Main\n");
					} else {
						sEmailStr.append("Workspace tabs          : Set/Met reviews\n");
					}
					if (sTabMeeting.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : Meetings\n");
					}
					if (sTabDocuments.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : Documents\n");
					}
					if (sTabIssues.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : Issues\n");
					}
					if (sTabContracts.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : Contracts\n");
					}
					if (sTabAsic.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : ASIC\n");
					}
					if (sTabSelf.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : Self assessment\n");
					}
					if (sTabFeedback.equalsIgnoreCase("Y")) {
						sEmailStr.append("                        : Feedback\n");
					}
					sEmailStr.append("                        : Team\n");
					
					sEmailStr.append("Geography               : " + sGeoList + "\n");
					sEmailStr.append("Delivery team           : " + sDelTeam + "\n");
					sEmailStr.append("Industry                : " + sIndList + "\n");
					
					if (sITAR.trim().equalsIgnoreCase("Y")) {
						sEmailStr.append("ITAR                    : Yes\n");
					}
					sEmailStr.append("\n\n");
					sEmailStr.append("Additional comments: \n");
					sEmailStr.append(sComments + "\n\n");

					sEmailStr.append("===============================================================\n");
					sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
					sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
					sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
					sEmailStr.append("of on demand tools that is available online 24/7.\n");
					sEmailStr.append("===============================================================\n");
					sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
					sEmailStr.append("===============================================================\n\n");
	
					mail.setMessage(sEmailStr.toString());
					mail.setTo(prop.getAdminEmail());
					mail.setFrom(es.gEMAIL);
					mail.setBcc("");
					mail.setCc("");
					
					boolean bSent = ETSUtils.sendEmail(mail);
					
					out.println("<br />");
					
					if (bSent) {
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						if (sWorkspaceFlag.trim().equalsIgnoreCase("Sub")) {
							out.println("<td headers=\"\" align=\"left\"><b>Your request to create a new Sub workspace has been submitted to E&TS Connect administrator for processing. It may take up to 5 business days to process your request. You can contact us at <a href=\"mailto:etsadmin@us.ibm.com\">etsadmin@us.ibm.com</a> if you have any questions.</b></td>");
						} else {
							out.println("<td headers=\"\" align=\"left\"><b>Your request to create a new workspace has been submitted to E&TS Connect administrator for processing. It may take up to 5 business days to process your request. You can contact us at <a href=\"mailto:etsadmin@us.ibm.com\">etsadmin@us.ibm.com</a> if you have any questions.</b></td>");
						}
						out.println("</tr>");
						out.println("</table>");
					} else {
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\"><b>There was an error when submitting your request to create workspace. Please try again later. If the problem persists, please contact your primary contact.</b></span></td>");
						out.println("</tr>");
						out.println("</table>");
					}
					out.println("<br />");
					
					printGreyDottedLine(out);
					out.println("<br />");
		
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"\" width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + unBrandprop.getLinkID() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
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
			
			boolean bAdmin = es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT,"tg_member=MD");
			
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
			
			String sTabIssues = request.getParameter("tab_issues");
			if (sTabIssues == null || sTabIssues.trim().equals("")) {
				sTabIssues = "N";
			} else {
				sTabIssues = sTabIssues.trim();
			}
			
			String sTabContracts = request.getParameter("tab_contracts");
			if (sTabContracts == null || sTabContracts.trim().equals("")) {
				sTabContracts = "N";
			} else {
				sTabContracts = sTabContracts.trim();
			}
			
			String sTabAboutSetMet = request.getParameter("tab_aboutsetmet");
			if (sTabAboutSetMet == null || sTabAboutSetMet.trim().equals("")) {
				sTabAboutSetMet = "N";
			} else {
				sTabAboutSetMet = sTabAboutSetMet.trim();
			}

			String sTabSurvey = request.getParameter("tab_survey");
			if (sTabSurvey == null || sTabSurvey.trim().equals("")) {
				sTabSurvey = "N";
			} else {
				sTabSurvey = sTabSurvey.trim();
			}
			
			String sTabFeedback = request.getParameter("tab_feedback");
			if (sTabFeedback == null || sTabFeedback.trim().equals("")) {
				sTabFeedback = "N";
			} else {
				sTabFeedback = sTabFeedback.trim();
			}
			
			String sTabSelf = request.getParameter("tab_self");
			if (sTabSelf == null || sTabSelf.trim().equals("")) {
				sTabSelf = "N";
			} else {
				sTabSelf = sTabSelf.trim();
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
			
			String sDelTeam = request.getParameter("delivery");
			if (sDelTeam == null || sDelTeam.trim().equals("")) {
				sDelTeam = "";
			} else {
				sDelTeam = sDelTeam.trim();
			}
			
			String sGeoList = request.getParameter("geography");
			if (sGeoList == null || sGeoList.trim().equals("")) {
				sGeoList = "";
			} else {
				sGeoList = sGeoList.trim();
			}
			
			String sIndList = request.getParameter("industry");
			if (sIndList == null || sIndList.trim().equals("")) {
				sIndList = "";
			} else {
				sIndList = sIndList.trim();
			}
			
			String sComments = request.getParameter("comments");
			if (sComments == null || sComments.trim().equals("")) {
				sComments = "";
			} else {
				sComments = sComments.trim();
			}
			
			String sProjectOrProposal = "P";
			 
			if (sWorkspaceType.trim().equalsIgnoreCase("Client Voice")) {
				sProjectOrProposal = "C";
			} else if (sWorkspaceType.trim().equalsIgnoreCase("Project")) {
				sProjectOrProposal = "P";
			} else if (sWorkspaceType.trim().equalsIgnoreCase("Proposal")) {
				sProjectOrProposal = "O";
			}			
			
			
			if (!bAdmin) {
				// if logged in person is not admin, use his id - CSR 9527 - sathish
				sPrimaryEmail = es.gIR_USERN.trim();
			}
			
			con.setAutoCommit(false);
			
			sProjectID = ETSWorkspaceDAO.createWorkspace(con, sWorkspaceName, sWorkspaceDesc, sProjectOrProposal, sMainWorkspace, sWorkspaceCompany, sDelTeam, sGeoList, sIndList, sITAR, Defines.ETS_WORKSPACE_TYPE);
			
			boolean bSuccess = false;
			
			bSuccess = ETSWorkspaceDAO.createRoles(con, sProjectID, sProjectOrProposal, es.gIR_USERN);
			
			bSuccess = ETSWorkspaceDAO.createWorkspaceOwner(con, sProjectID, sPrimaryEmail, es.gIR_USERN);
			
			if (sProjectOrProposal.trim().equalsIgnoreCase("P")) {
				int iCount = 10;
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
				iCount = iCount + 10;
			} else if (sProjectOrProposal.trim().equalsIgnoreCase("O")) {
				int iCount = 10;
				// main tab
				bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.MAIN_VT, iCount, es.gIR_USERN);
				iCount = iCount + 10;
				if (sTabContracts.trim().equalsIgnoreCase("Y")) {
					bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.CONTRACTS_VT, iCount, es.gIR_USERN);
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
				iCount = iCount + 10;
			} else {
				int iCount = 10;
				// Set/Met reviews tab
				bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.SETMET_VT, iCount, es.gIR_USERN);
				iCount = iCount + 10;
				if (sTabSelf.trim().equalsIgnoreCase("Y")) {
					bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.SELF_ASSESSMENT_VT, iCount, es.gIR_USERN);
					iCount = iCount + 10;
				}
//				if (sTabAboutSetMet.trim().equalsIgnoreCase("Y")) {
//					bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.ABOUT_SETMET_VT, iCount, es.gIR_USERN);
//					iCount = iCount + 10;
//				}
				if (sTabSurvey.trim().equalsIgnoreCase("Y")) {
					bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.SURVEY_VT, iCount, es.gIR_USERN);
					iCount = iCount + 10;
				}
				if (sTabFeedback.trim().equalsIgnoreCase("Y")) {
					bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.FEEDBACK_VT, iCount, es.gIR_USERN);
					iCount = iCount + 10;
				}
				bSuccess = ETSWorkspaceDAO.createTab(con, sProjectID, Defines.TEAM_VT, iCount, es.gIR_USERN);
				iCount = iCount + 10;
			}
				
				
			boolean bPrefInsert = ETSWorkspaceDAO.insertWorkspaceIntoPreference(con,sProjectID,sPrimaryEmail);
							
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
			
			UnbrandedProperties unBrandprop = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);

			String sEmailSubject = "E&TS Connect - A new workspace has been created for your use.";

			sEmailStr.append("A new workspace has been created on E&TS Connect\n");
			sEmailStr.append("and you have been assigned as the workspace owner.\n\n");
			
			sEmailStr.append("The details of the workspace are as follows: \n");

			sEmailStr.append("===============================================================\n");

			sEmailStr.append("  Name:           " + sWorkspaceName + "\n");
			sEmailStr.append("  Company:        " + sCompany + "\n\n");
			
			if (bEntRequested && bPOCRequested) {
				sEmailStr.append("An entitlement request and a customer Point Of Contact (POC) has been\n");
				sEmailStr.append("sent to your manager for approval. Use the following URL below to access \n");
				sEmailStr.append("your workspace after your manager has approved both the requests.\n");
				sEmailStr.append(Global.getUrl("ets/ETSConnectServlet.wss") + "?linkid=" + unBrandprop.getLinkID() + "\n\n");
				
			}

			if (bEntRequested && !bPOCRequested) {
				sEmailStr.append("An entitlement request has been sent to your manager for approval.\n");
				sEmailStr.append("Use the following URL below to access your workspace after your manager\n");
				sEmailStr.append("has approved the request.\n");
				sEmailStr.append(Global.getUrl("ets/ETSConnectServlet.wss") + "?linkid=" + unBrandprop.getLinkID() + "\n\n");
			}

			if (!bEntRequested && bPOCRequested) {
				sEmailStr.append("A customer Point Of Contact (POC) request has been sent to your manager.\n");
				sEmailStr.append("for approval. Use the following URL below to access your workspace after \n");
				sEmailStr.append("your manager has approved the request.\n");
				sEmailStr.append(Global.getUrl("ets/ETSConnectServlet.wss") + "?linkid=" + unBrandprop.getLinkID() + "\n\n");
			}

			if (!bEntRequested && !bPOCRequested) {
				sEmailStr.append("To work with the workspace and setup members, click on the following URL:\n");
				sEmailStr.append(Global.getUrl("ets/ETSConnectServlet.wss") + "?linkid=" + unBrandprop.getLinkID() + "\n\n");
			}


			sEmailStr.append("===============================================================\n");
			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
			sEmailStr.append("of on demand tools that is available online 24/7.\n");
			sEmailStr.append("===============================================================\n");
			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
			sEmailStr.append("===============================================================\n\n");

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
			
			String sName = ETSUtils.getUsersName(con,sIRId);
	
			String sEmailSubject = "E&TS Connect - MultiPOC entitlement request for ( " + sName + " )";
	
			sEmailStr.append("E&TS Connect has requested MultiPOC for the following user \n");
			sEmailStr.append("who is going to be managing a workspace and will need to give\n");
			sEmailStr.append("external users access to it.\n\n");
			
			sEmailStr.append("The details of the request are as follows: \n");
			sEmailStr.append("ID                 : " + sIRId + "\n");
			sEmailStr.append("Name               : " + sName + "\n\n");
			sEmailStr.append("Requested by id    : " + es.gIR_USERN + "\n");
			sEmailStr.append("Requested by       : " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + "\n\n\n");
	
			sEmailStr.append("===============================================================\n");
			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
			sEmailStr.append("of on demand tools that is available online 24/7.\n");
			sEmailStr.append("===============================================================\n");
			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
			sEmailStr.append("===============================================================\n\n");
			
			ETSProperties prop = new ETSProperties();
	
			String sToList = prop.getPOCContactEmail();
	
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
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
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

}



