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
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;


public class AICCustomizeServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";

	private static Log logger = EtsLogger.getLogger(AICCustomizeServlet.class);

	private static final String BUNDLE_NAME =
			"oem.edge.ets.fe.aic.AICResources";
	private static final ResourceBundle aic_rb =
			ResourceBundle.getBundle(BUNDLE_NAME);

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

			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
			String sDate = df.format(new Date());

			conn = ETSDBUtils.getConnection();
     	    if (!es.GetProfile(response, request, conn)) {
				return;
			}

			UnbrandedProperties prop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

			Hashtable hs = ETSUtils.getServletParameters(request);

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", prop.getLandingPageURL());
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

			EdgeHeader.setPageTitle("AIC Connect - Customize your Collaboration Center main page");
			EdgeHeader.setHeader("Customize your Collaboration Center ");
			//EdgeHeader.setSubHeader("Customize");

			out.println(EdgeHeader.printBullsEyeHeader());
			out.println(EdgeHeader.printBullsEyeLeftNav());
			out.println(EdgeHeader.printSubHeader());

			// top table to define the content and right sides..
			out.println("<form name=\"CustomizeWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "AICCustomizeServlet.wss\">");

		    out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			//out.println("<tr valign=\"top\"><td headers=\"col1\" width=\"443\" valign=\"top\" class=\"small\"><b>You are logged in as:</b> " + es.gIR_USERN + "</td>");
			out.println("<tr valign=\"top\"><td headers=\"col2\" width=\"443\" valign=\"top\" class=\"small\"><table summary=\"\" width=\"100%\"><tr><td headers=\"col3\" width=\"60%\">" + es.gIR_USERN + "</td><td headers=\"col4\" width=\"40%\" align=\"right\">" + sDate + "</td></tr></table></td>");
			out.println("<td headers=\"col5\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			out.println("<td headers=\"col6\" class=\"small\" align=\"right\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col7\" width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"col8\"  width=\"90\" align=\"right\">Secure content</td></tr></table></td></tr>");
			out.println("</table>");

			printGreyDottedLine(out);


			String sSubmitFlag = request.getParameter("image_submit.x");
			if (sSubmitFlag == null || sSubmitFlag.trim().equals("")) {
				sSubmitFlag = "";
			} else {
				sSubmitFlag = sSubmitFlag.trim();
			}

			if (!sSubmitFlag.trim().equalsIgnoreCase("")) {
				if (customizeConfirm(parameters)) {
					displayConfirmation(parameters);
				} else {
					displayUserProjects(parameters,true);
				}
			} else {
				displayUserProjects(parameters,false);
			}

			out.println("</form>");
			out.println("<br /><br />");
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col9\" width=\"16\" align=\"left\"><img alt=\"protected content\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"col10\"  align=\"left\"><span class=\"fnt\">A key icon displayed in a page indicates that the page is secure and password-protected.</span></td></tr></table>");

			out.println(EdgeHeader.printBullsEyeFooter());

		} catch (SQLException e) {
            e.printStackTrace();
            if (logger.isErrorEnabled()) {
            	logger.error(this,e);
            }
			ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(e), "Error occurred on AIC Connect.");
		} catch (Exception e) {
            e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(e), "Error occurred on IBM AIC Connect.");
		} finally {
			ETSDBUtils.close(conn);
			out.flush();
			out.close();
		}
	}

	private static void printGreyDottedLine(PrintWriter out) {

		out.println("<!-- Gray dotted line -->");
		out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col11\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("<td headers=\"col12\" background=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		out.println("<td headers=\"col13\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
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


	private static void displayUserProjects(ETSParams params, boolean bError) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		try {

			PrintWriter out = params.getWriter();
			Connection con = params.getConnection();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			HttpServletRequest request = params.getRequest();

			String sUserId = es.gIR_USERN;
			boolean bAdmin = false;
			boolean bWorkflowAdmin = false;
			boolean bITAR = false;

			String sType = ETSUtils.checkNull(request.getParameter("type"));
			String sSect[] = request.getParameterValues("sector");
			String sSceS[] = request.getParameterValues("sceSector");
			String sProc[] = request.getParameterValues("process");
			String sBran[] = request.getParameterValues("brand");
			String sSort = ETSUtils.checkNull(request.getParameter("sort"));

			String sFirst = ETSUtils.checkNull(request.getParameter("flag"));

			if (sFirst == null || sFirst.equals("")) {
				sFirst="Y";
			}

			if (sType != null && !sType.trim().equalsIgnoreCase("")) {

				String sInsert = "";

				if (sType.trim().equalsIgnoreCase("O")) {
					sInsert = "<b>My AIC proposals</b>";
				} else if (sType.trim().equalsIgnoreCase("P")) {
					sInsert = "<b>My Collaboration Center Workspaces</b>";
				} else if (sType.trim().equalsIgnoreCase("C")) {
					sInsert = "<b>Client voice</b>";
				}

				String sInternal = es.gDECAFTYPE;

				if (sInternal == null || sInternal.trim().equalsIgnoreCase("")) {
					sInternal = "";
				} else {
					sInternal = sInternal.trim().toUpperCase();
				}

				boolean bInternal = false;

				if (sInternal.equalsIgnoreCase("I")) {
					bInternal = true;
				}

				String sSectorValues = "";
				String sSceSectorValues = "";
				String sProcessValues = "";
				String sBrandValues = "";

				String sSortOrder = "PROJECT_NAME";

				if (sSort == null || sSort.trim().equalsIgnoreCase("")) {
					sSort = "WORKSPACE NAME";
					sSortOrder = "PROJECT_NAME";
				} else {
					sSort = sSort.trim();
				}

				if (sSort.trim().equalsIgnoreCase("BUSINESS SECTOR")) {
					sSortOrder = "SECTOR";
				} else if (sSort.trim().equalsIgnoreCase("BRAND")) {
					sSortOrder = "BRAND";
				} else if (sSort.trim().equalsIgnoreCase("PROCESS")) {
					sSortOrder = "PROCESS";
				} else if (sSort.trim().equalsIgnoreCase("SCE SECTOR")) {
					sSortOrder = "SCE_SECTOR";
				} else if (sSort.trim().equalsIgnoreCase("WORKSPACE NAME")) {
					sSortOrder = "PROJECT_NAME";
				}

				if (sSect == null) {
					sSectorValues = "";
				} else {
					for (int i = 0; i < sSect.length; i++) {
						if (sSectorValues.trim().equalsIgnoreCase("")) {
							sSectorValues = sSect[i].trim();
						} else {
							sSectorValues = sSectorValues + "," + sSect[i].trim();
						}
					}
				}

				if (sSceS == null) {
					sSceSectorValues = "";
				} else {
					for (int i = 0; i < sSceS.length; i++) {
						if (sSceSectorValues.trim().equalsIgnoreCase("")) {
							sSceSectorValues = sSceS[i].trim();
						} else {
							sSceSectorValues = sSceSectorValues + "," + sSceS[i].trim();
						}
					}
				}

				if (sProc == null) {
					sProcessValues = "";
				} else {
					for (int i = 0; i < sProc.length; i++) {
						if (sProcessValues.trim().equalsIgnoreCase("")) {
							sProcessValues = sProc[i].trim();
						} else {
							sProcessValues = sProcessValues + "," + sProc[i].trim();
						}
					}
				}

				if (sBran == null) {
					sBrandValues = "";
				} else {
					for (int i = 0; i < sBran.length; i++) {
						if (sBrandValues.trim().equalsIgnoreCase("")) {
							sBrandValues = sBran[i].trim();
						} else {
							sBrandValues = sBrandValues + "," + sBran[i].trim();
						}
					}
				}

				String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,es.gIR_USERN);
				Vector userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

				if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
					bAdmin = true;
				} else if (userents.contains(Defines.WORKFLOW_ADMIN)) {
					bWorkflowAdmin = true;
				}

				//if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT, "tg_member=MD")) {
				//	bAdmin = true;
				//} else if (es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD")) {
				//	bAdmin = true;
				//}

				if (es.Qualify(Defines.ITAR_ENTITLEMENT, "tg_member=MD")) {
					bITAR = true;
				}

				if (bAdmin) {
					sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY,BRAND,PROCESS,SCE_SECTOR,SECTOR, IS_ITAR, (SELECT COUNT(1) AS CUST_FLAG FROM ETS.ETS_USER_WS WHERE PROJECT_ID = P.PROJECT_ID AND USER_ID = '" + sUserId + "' GROUP BY PROJECT_ID) FROM ETS.ETS_PROJECTS P WHERE PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' AND PROJECT_OR_PROPOSAL = '" + sType + "' AND PARENT_ID = '0' AND PROJECT_TYPE = '"+ Defines.AIC_WORKSPACE_TYPE +"'");
				} else if (bWorkflowAdmin) {
					sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY,BRAND,PROCESS,SCE_SECTOR,SECTOR, IS_ITAR, (SELECT COUNT(1) AS CUST_FLAG FROM ETS.ETS_USER_WS WHERE PROJECT_ID = P.PROJECT_ID AND USER_ID = '" + sUserId + "' GROUP BY PROJECT_ID) FROM ETS.ETS_PROJECTS P WHERE PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' AND PROJECT_OR_PROPOSAL = '" + sType + "' AND PARENT_ID = '0' AND PROJECT_TYPE = '"+ Defines.AIC_WORKSPACE_TYPE +"' " +
							" and ((is_private='A' and process='Workflow' ) " +
							" or ( PROJECT_ID IN (SELECT USER_PROJECT_ID FROM " +
							" ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "' " +
							" and active_flag='A'))) AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "'   ");
				} else {
					sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY,BRAND,PROCESS,SCE_SECTOR,SECTOR, IS_ITAR, (SELECT COUNT(1) AS CUST_FLAG FROM ETS.ETS_USER_WS WHERE PROJECT_ID = P.PROJECT_ID AND USER_ID = '" + sUserId + "' GROUP BY PROJECT_ID) FROM ETS.ETS_PROJECTS P WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "') AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' AND PROJECT_OR_PROPOSAL = '" + sType + "'  AND PARENT_ID = '0' AND PROJECT_TYPE = '"+ Defines.AIC_WORKSPACE_TYPE +"'");
				}

				if (sBran != null) {
				  if ((sBran[0].equalsIgnoreCase("All")) == false) {
					if (sBran.length > 1) {
							sQuery.append(" AND (BRAND='" + sBran[0] + "' ");
						} else {
							sQuery.append(" AND BRAND='" + sBran[0] + "' ");
							}

						for (int i = 1; i < sBran.length; i++) {
								if (i == ((sBran.length) - 1)) {
									sQuery.append(
										" OR BRAND='" + sBran[i] + "')");
							} else {
								sQuery.append(
									" OR BRAND='" + sBran[i] + "' ");
									}

								}
							}
						}

				if (sProc != null) {
				  if ((sProc[0].equalsIgnoreCase("All")) == false) {
					if (sProc.length > 1) {
							sQuery.append(" AND (PROCESS='" + sProc[0] + "' ");
						} else {
							sQuery.append(" AND PROCESS='" + sProc[0] + "' ");
							}

						for (int i = 1; i < sProc.length; i++) {
								if (i == ((sProc.length) - 1)) {
									sQuery.append(
										" OR PROCESS='" + sProc[i] + "')");
							} else {
								sQuery.append(
									" OR PROCESS='" + sProc[i] + "' ");
									}

								}
							}
						}

				if (sSceS != null) {
				  if ((sSceS[0].equalsIgnoreCase("All")) == false) {
					if (sSceS.length > 1) {
							sQuery.append(" AND (SCE_SECTOR='" + sSceS[0] + "' ");
						} else {
							sQuery.append(" AND SCE_SECTOR='" + sSceS[0] + "' ");
							}

						for (int i = 1; i < sSceS.length; i++) {
								if (i == ((sSceS.length) - 1)) {
									sQuery.append(
										" OR SCE_SECTOR='" + sSceS[i] + "')");
							} else {
								sQuery.append(
									" OR SCE_SECTOR='" + sSceS[i] + "' ");
									}

								}
							}
						}

				if (sSect != null) {
				  if ((sSect[0].equalsIgnoreCase("All")) == false) {
					if (sSect.length > 1) {
							sQuery.append(" AND (SECTOR='" + sSect[0] + "' ");
						} else {
							sQuery.append(" AND SECTOR='" + sSect[0] + "' ");
							}

						for (int i = 1; i < sSect.length; i++) {
								if (i == ((sSect.length) - 1)) {
									sQuery.append(
										" OR SECTOR='" + sSect[i] + "')");
							} else {
								sQuery.append(
									" OR SECTOR='" + sSect[i] + "' ");
									}

								}
							}
						}

				sQuery.append(" ORDER BY " + sSortOrder + " for READ ONLY");

				if (logger.isDebugEnabled()) {
					logger.debug("AICConnectServlet::displayUserProjects::Query : " + sQuery.toString());
				}

				out.println("<input type=\"hidden\" name=\"type\" value=\"" + sType + "\" />");

				if (bError) {
					out.println("<br />");
					out.println("<br />");

					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"col14\" align=\"left\" ><span style=\"color:#ff3333\"><b>You have to select at least one workspace before you click Submit.</b><span></td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<br />");
					//out.println("<br />");
				}

				out.println("<br />");
//				out.println("<br />");

 				if (bInternal) {
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"col15\" align=\"left\" >Please select the workspaces you would like to see in " + sInsert + " in your Collaboration Center Main Page and click on submit. To automatically select workspaces based on Brand or Process or SCE Sector or Business Sector, use the appropriate filters provided below and click on <b>Go</b> button. Alternately, you can cherry-pick your workspaces from the list of workspaces below.</td>");
					out.println("</tr>");
					out.println("</table>");
 				} else {
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"col16\" align=\"left\" >Please select the workspaces you would like to see in " + sInsert + " in your Collaboration Center Main Page and click on submit.</td>");
					out.println("</tr>");
					out.println("</table>");
 				}


				if (bInternal) {
					out.println("<br />");

					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"col17\" align=\"left\" width=\"190\"><b><label for=\"label_brand\">Brand</label></b></td>");
					out.println("<td headers=\"col18\" align=\"left\" width=\"200\"><b><label for=\"label_process\">Process</label></b></td>");
					out.println("<td headers=\"col19\" align=\"left\" ><b><label for=\"label_scesector\">SCE Sector</label></b></td>");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"col20\" align=\"left\" width=\"190\">" + displayBrand(con,"brand","label_brand",sUserId,sBrandValues,bAdmin, sType) + "</td>");
					out.println("<td headers=\"col21\" align=\"left\" width=\"200\">" + displayProcess(con,"process","label_process",sUserId,sProcessValues,bAdmin, sType) + "</td>");
					out.println("<td headers=\"col22\" align=\"left\" width=\"190\">" + displaySceSector(con,"sceSector","label_sceSector",sUserId,sSceSectorValues,bAdmin, sType) + "</td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"col23\" align=\"left\">&nbsp;</td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"col24\" align=\"left\" width=\"190\"><b><label for=\"label_sector\">Business Sector</label></b></td>");
					out.println("<td headers=\"col25\" align=\"left\">&nbsp;</td>");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"col26\" align=\"left\" >" + displaySector(con,"sector","label_sector",sUserId,sSectorValues,bAdmin, sType) + "</td>");
					out.println("<td headers=\"col27\" align=\"left\">&nbsp;</td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<br />");

					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"col28\" align=\"left\" width=\"80\"><b><label for=\"label_sort\">Sort by</label></b></td>");
					out.println("<td headers=\"col29\" align=\"left\" width=\"180\"  >" + displaySort("sort","label_sort",sSort) + "</td>");
					out.println("<td headers=\"col30\" align=\"left\" ><input type=\"image\" name=\"filter\" src=\"" + Defines.BUTTON_ROOT + "go.gif\" height=\"21\" width=\"21\" alt=\"Go\" /></td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<br />");
					out.println("<br />");

					printGreyDottedLine(out);

				}

				int iCount = 0;

				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());

			  if(rs.next()){

				out.println("<br />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col31\"  align=\"left\">The following are the Collaboration Center for which you have access to. Collaboration Center marked with <span style=\"color:#cc6600\">*</span> are the workspaces in your current customized list.</td></tr></table>");

				out.println("<br />");
				out.println("<br />");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
				out.println("<tr valign=\"top\" class=\"tdblue\">");
				out.println("<td headers=\"col32\" colspan=\"6\" align=\"left\" >Your collaboration center</td>");
				out.println("</tr>");

				if (bInternal) {
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"col33\"  colspan=\"2\" width=\"150\" align=\"left\" valign=\"top\" ><b>Workspace name</b></td>");
					out.println("<td headers=\"col34\"  width=\"100\" align=\"left\" valign=\"top\" ><b>Brand</b></td>");
					out.println("<td headers=\"col35\"  width=\"110\" align=\"left\" valign=\"top\" ><b>Process</b></td>");
					out.println("<td headers=\"col36\"  width=\"110\" align=\"left\" valign=\"top\" ><b>SCE Sector</b></td>");
					out.println("<td headers=\"col37\"  align=\"left\" valign=\"top\" ><b>Business Sector</b></td>");
					out.println("</tr>");
				} else {
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"col38\"  colspan=\"2\" align=\"left\" valign=\"top\" ><b>Client</b></td>");
					out.println("<td headers=\"col39\"  align=\"left\" valign=\"top\" ><b>Workspace name</b></td>");
					out.println("</tr>");
				}
				String sProjID = rs.getString(1);
				String sProjName = rs.getString(2);
				String sCompany = rs.getString(3);
				String sBrand = rs.getString(4);
				String sProcess = rs.getString(5);
				String sSceSector = rs.getString(6);
				String sSector = rs.getString(7);
				String isITAR = ETSUtils.checkNull(rs.getString(8));
				String sCustomize = rs.getString(9);

				boolean bDisplay = false;

				if (isITAR.equalsIgnoreCase("Y")) {
					if (bITAR) {
						bDisplay = true;
					}
				} else {
					bDisplay = true;
				}

				String sCustFlag = "N";

				if (bDisplay) {

					if (sCustomize == null || sCustomize.trim().equalsIgnoreCase("")) {
						sCustomize ="";
					} else {
						sCustomize ="&nbsp;<span style=\"color:#cc6600\">*</span>&nbsp;";
					}

					if (sFirst.trim().equalsIgnoreCase("y")) {
						if (sCustomize == null || sCustomize.trim().equalsIgnoreCase("")) {
							sCustFlag = "N";
						} else {
							sCustFlag = "Y";
						}
					} else {

						boolean bBrandFlag = false;
						boolean bProcessFlag = false;
						boolean bSectorFlag = false;
						boolean bSceSectorFlag = false;


						if (sBran == null) {
							sBrandValues = "";
							bBrandFlag = true;
						} else {
							for (int i = 0; i < sBran.length; i++) {
								if (sBran[i].trim().equalsIgnoreCase(sBrand)) {
									bBrandFlag = true;
									break;
								}
							}
						}

						if (sProc == null) {
							sProcessValues = "";
							bProcessFlag = true;
						} else {
							for (int i = 0; i < sProc.length; i++) {
								if (sProc[i].trim().equalsIgnoreCase(sProcess)) {
									bProcessFlag = true;
									break;
								}
							}
						}


						if (sSceS == null) {
							sSceSectorValues = "";
							bSceSectorFlag = true;
						} else {
							for (int i = 0; i < sSceS.length; i++) {
								if (sSceS[i].trim().equalsIgnoreCase(sSceSector)) {
									bSceSectorFlag = true;
									break;
								}
							}
						}

						if (sSect == null) {
							sSectorValues = "";
							bSectorFlag = true;
						} else {
							for (int i = 0; i < sSect.length; i++) {
								if (sSect[i].trim().equalsIgnoreCase(sSceSector)) {
									bSectorFlag = true;
									break;
								}
							}
						}

						if (bBrandFlag && bProcessFlag && bSceSectorFlag && bSectorFlag) {
							sCustFlag = "Y";
						}

					}

					iCount = iCount + 1;

					if ((iCount % 2) == 0) {
						out.println("<tr valign=\"top\">");
					} else {
						out.println("<tr style=\"background-color:#eeeeee\">");
					}

					if (sCustFlag.equalsIgnoreCase("Y")) {
						out.println("<td headers=\"col40\"  width=\"10\" align=\"middle\" valign=\"top\" ><input type=\"checkbox\" id=\"label_proj" + iCount + "\" class=\"iform\" name=\"proj_selection\" value=\"" + sProjID + "\" checked=\"checked\" selected=\"selected\" /></td>");
					} else {
						out.println("<td headers=\"col41\"  width=\"10\" align=\"middle\" valign=\"top\" ><input type=\"checkbox\" id=\"label_proj" + iCount + "\" class=\"iform\" name=\"proj_selection\" value=\"" + sProjID + "\" /></td>");
					}

					out.println("<td headers=\"col42\"  width=\"150\" align=\"left\" valign=\"top\" ><label for=\"label_proj" + iCount + "\">" + sProjName + sCustomize + "</label></td>");
					if (bInternal) {
						//out.println("<td headers=\"col43\"  width=\"150\" align=\"left\" valign=\"top\" >" +  sProjName  + "</td>");
						out.println("<td headers=\"col44\"  width=\"100\" align=\"left\" valign=\"top\"  >" +  sBrand + "</td>");
						out.println("<td headers=\"col45\"  width=\"110\" align=\"left\" valign=\"top\"  >" + sProcess + "</td>");
						out.println("<td headers=\"col46\"  width=\"110\" align=\"left\" valign=\"top\"  >" + sSceSector + "</td>");
						out.println("<td headers=\"col47\"  align=\"left\" valign=\"top\" >" + sSector + "</td>");
					} else {
						//out.println("<td headers=\"col48\"  width=\"150\" align=\"left\" valign=\"top\" >" +  sProjName  + "</td>");
					}

					out.println("</tr>");
				}
				while (rs.next()) {

					sProjID = rs.getString(1);
					sProjName = rs.getString(2);
					sCompany = rs.getString(3);
					sBrand = rs.getString(4);
					sProcess = rs.getString(5);
					sSceSector = rs.getString(6);
					sSector = rs.getString(7);
					isITAR = ETSUtils.checkNull(rs.getString(8));
					sCustomize = rs.getString(9);

					bDisplay = false;

					if (isITAR.equalsIgnoreCase("Y")) {
						if (bITAR) {
							bDisplay = true;
						}
					} else {
						bDisplay = true;
					}

					if (bDisplay) {

						if (sCustomize == null || sCustomize.trim().equalsIgnoreCase("")) {
							sCustomize ="";
						} else {
							sCustomize ="&nbsp;<span style=\"color:#cc6600\">*</span>&nbsp;";
						}

						sCustFlag = "N";

						if (sFirst.trim().equalsIgnoreCase("y")) {
							if (sCustomize == null || sCustomize.trim().equalsIgnoreCase("")) {
								sCustFlag = "N";
							} else {
								sCustFlag = "Y";
							}
						} else {

							boolean bBrandFlag = false;
							boolean bProcessFlag = false;
							boolean bSectorFlag = false;
							boolean bSceSectorFlag = false;


							if (sBran == null) {
								sBrandValues = "";
								bBrandFlag = true;
							} else {
								for (int i = 0; i < sBran.length; i++) {
									if (sBran[i].trim().equalsIgnoreCase(sBrand)) {
										bBrandFlag = true;
										break;
									}
								}
							}

							if (sProc == null) {
								sProcessValues = "";
								bProcessFlag = true;
							} else {
								for (int i = 0; i < sProc.length; i++) {
									if (sProc[i].trim().equalsIgnoreCase(sProcess)) {
										bProcessFlag = true;
										break;
									}
								}
							}


							if (sSceS == null) {
								sSceSectorValues = "";
								bSceSectorFlag = true;
							} else {
								for (int i = 0; i < sSceS.length; i++) {
									if (sSceS[i].trim().equalsIgnoreCase(sSceSector)) {
										bSceSectorFlag = true;
										break;
									}
								}
							}

							if (sSect == null) {
								sSectorValues = "";
								bSectorFlag = true;
							} else {
								for (int i = 0; i < sSect.length; i++) {
									if (sSect[i].trim().equalsIgnoreCase(sSceSector)) {
										bSectorFlag = true;
										break;
									}
								}
							}

							if (bBrandFlag && bProcessFlag && bSceSectorFlag && bSectorFlag) {
								sCustFlag = "Y";
							}

						}

						iCount = iCount + 1;

						if ((iCount % 2) == 0) {
							out.println("<tr valign=\"top\">");
						} else {
							out.println("<tr style=\"background-color:#eeeeee\">");
						}

						if (sCustFlag.equalsIgnoreCase("Y")) {
							out.println("<td headers=\"col49\"  width=\"10\" align=\"middle\" valign=\"top\" ><input type=\"checkbox\" id=\"label_proj" + iCount + "\" class=\"iform\" name=\"proj_selection\" value=\"" + sProjID + "\" checked=\"checked\" selected=\"selected\" /></td>");
						} else {
							out.println("<td headers=\"col50\"  width=\"10\" align=\"middle\" valign=\"top\" ><input type=\"checkbox\" id=\"label_proj" + iCount + "\" class=\"iform\" name=\"proj_selection\" value=\"" + sProjID + "\" /></td>");
						}

						out.println("<td headers=\"col51\"  width=\"150\" align=\"left\" valign=\"top\" ><label for=\"label_proj" + iCount + "\">" + sProjName + sCustomize + "</label></td>");
						if (bInternal) {
							//out.println("<td headers=\"col52\"  width=\"150\" align=\"left\" valign=\"top\" >" +  sProjName  + "</td>");
							out.println("<td headers=\"col53\"  width=\"100\" align=\"left\" valign=\"top\"  >" +  sBrand + "</td>");
							out.println("<td headers=\"col54\"  width=\"110\" align=\"left\" valign=\"top\"  >" + sProcess + "</td>");
							out.println("<td headers=\"col55\"  width=\"110\" align=\"left\" valign=\"top\"  >" + sSceSector + "</td>");
							out.println("<td headers=\"col56\"  align=\"left\" valign=\"top\" >" + sSector + "</td>");
						} else {
							//out.println("<td headers=\"col57\"  width=\"150\" align=\"left\" valign=\"top\" >" +  sProjName  + "</td>");
						}

						out.println("</tr>");
					}
				}

				out.println("</table>");

				out.println("<br />");
				printGreyDottedLine(out);

				out.println("<br />");
				out.println("<br />");

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"col58\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" border=\"0\" /></td>");
				out.println("<td headers=\"col59\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col60\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col61\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "AICConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
				out.println("</tr>");
				out.println("</table>");
				}else {
					logger.debug("NO Results.");
					out.println(
						"<table><tr valign=\"top\"><td headers=\"col62\" " +
					" align=\"left\" valign=\"top\" ><span style=\"color:#ff3333\">"
						+ aic_rb.getString("aic.wslist.noResMsg").trim()
						+ "</span></td></tr></table>");
						out.println("<br />");
					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"col63\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col64\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col65\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "AICConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
					out.println("</tr>");
					out.println("</table>");
					}
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}


	}


	private static String displayBrand(Connection con, String sSelectName, String sLabelId, String sUserId, String sBrandList, boolean bAdmin, String sType) throws SQLException, Exception {

			StringBuffer out = new StringBuffer("");

			try {

				String sAvailable = "," + sBrandList + ",";
				out.append("<select size=\"5\" style=\"width:190px\" multiple=\"multiple\" width=\"190px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");

				if(sBrandList.equals("")){
					out.append("<option value=\"All\" selected=\"selected\">All</option>");
				}else{
					out.append("<option value=\"All\">All</option>");
				}

				Vector vBran = getValues(con,Defines.BRAND);

				if (vBran != null && vBran.size() > 0) {

					for (int i = 0; i < vBran.size(); i++) {

						String sBrand = (String) vBran.elementAt(i);

						if (sAvailable.indexOf("," + sBrand + ",") >= 0)  {
							out.append("<option value=\"" + sBrand + "\" selected=\"selected\">" + sBrand + "</option>");
						} else {
							out.append("<option value=\"" + sBrand + "\" >" + sBrand + "</option>");
						}
					}
				}

				out.append("</select>");

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return out.toString();

		}

	private static String displayProcess(Connection con, String sSelectName, String sLabelId, String sUserId, String sProcessList, boolean bAdmin,String sType) throws SQLException, Exception {

			StringBuffer out = new StringBuffer("");

			try {

				String sAvailable = "," + sProcessList + ",";
				out.append("<select size=\"5\" style=\"width:200px\" multiple=\"multiple\" width=\"200px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");

				if(sProcessList.equals("")){
					out.append("<option value=\"All\" selected=\"selected\">All</option>");
				}else{
					out.append("<option value=\"All\">All</option>");
				}

				Vector vProcess = getValues(con,Defines.PROCESS);

				if (vProcess != null && vProcess.size() > 0) {

					for (int i = 0; i < vProcess.size(); i++) {

						String sProcess = (String) vProcess.elementAt(i);

						if (sAvailable.indexOf("," + sProcess + ",") >= 0)  {
							out.append("<option value=\"" + sProcess + "\" selected=\"selected\">" + sProcess + "</option>");
						} else {
							out.append("<option value=\"" + sProcess + "\" >" + sProcess + "</option>");
						}
					}
				}

				out.append("</select>");

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return out.toString();

		}

	private static String displaySceSector(Connection con, String sSelectName, String sLabelId, String sUserId, String sSceSectorList, boolean bAdmin, String sType) throws SQLException, Exception {

			StringBuffer out = new StringBuffer("");

			try {

				String sAvailable = "," + sSceSectorList + ",";
				out.append("<select size=\"5\" style=\"width:200px\" multiple=\"multiple\" width=\"200px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");

				if(sSceSectorList.equals("")){
					out.append("<option value=\"All\" selected=\"selected\">All</option>");
				}else{
					out.append("<option value=\"All\">All</option>");
				}

				Vector vSceSector = getValues(con,"SCE_SECTOR");

				if (vSceSector != null && vSceSector.size() > 0) {

					for (int i = 0; i < vSceSector.size(); i++) {

						String sSceSector = (String) vSceSector.elementAt(i);

						if (sAvailable.indexOf("," + sSceSector + ",") >= 0)  {
							out.append("<option value=\"" + sSceSector + "\" selected=\"selected\">" + sSceSector + "</option>");
						} else {
							out.append("<option value=\"" + sSceSector + "\" >" + sSceSector + "</option>");
						}
					}
				}

				out.append("</select>");

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return out.toString();

		}

	private static String displaySector(Connection con, String sSelectName, String sLabelId, String sUserId, String sSectorList, boolean bAdmin, String sType) throws SQLException, Exception {

			StringBuffer out = new StringBuffer("");

			try {

				String sAvailable = "," + sSectorList + ",";
				out.append("<select size=\"5\" style=\"width:200px\" multiple=\"multiple\" width=\"200px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");

                if(sSectorList.equals("")){
					out.append("<option value=\"All\" selected=\"selected\">All</option>");
                }else{
					out.append("<option value=\"All\">All</option>");
                }

				Vector vSector = getValues(con,Defines.SECTOR);

				if (vSector != null && vSector.size() > 0) {

					for (int i = 0; i < vSector.size(); i++) {

						String sSector = (String) vSector.elementAt(i);

						if (sAvailable.indexOf("," + sSector + ",") >= 0)  {
							out.append("<option value=\"" + sSector + "\" selected=\"selected\">" + sSector + "</option>");
						} else {
							out.append("<option value=\"" + sSector + "\" >" + sSector + "</option>");
						}
					}
				}

				out.append("</select>");

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return out.toString();

		}

	private static String displaySort(String sSelectName, String sLabelId, String sSortOrder) throws SQLException, Exception {

			StringBuffer out = new StringBuffer("");

			try {

				String sAvailable = "," + sSortOrder + ",";
				out.append("<select style=\"width:160px\" width=\"160px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");

				Vector vSort = getSortList();

				if (vSort != null && vSort.size() > 0) {

					for (int i = 0; i < vSort.size(); i++) {

						String sSort = (String) vSort.elementAt(i);

						if (sSortOrder.equalsIgnoreCase(sSort))  {
							out.append("<option value=\"" + sSort + "\" selected=\"selected\">" + sSort + "</option>");
						} else {
							out.append("<option value=\"" + sSort + "\" >" + sSort + "</option>");
						}
					}
				}

				out.append("</select>");

			} catch (Exception e) {
				throw e;
			}

			return out.toString();

		}

	/**
	 * @return
	 */
	private static Vector getSortList() throws Exception {

		Vector vSort = new Vector();

		vSort.addElement("BRAND");
		vSort.addElement("PROCESS");
		vSort.addElement("SCE SECTOR");
		vSort.addElement("BUSINESS SECTOR");
		vSort.addElement("WORKSPACE NAME");

		return vSort;
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

	private static boolean customizeConfirm(ETSParams params) throws SQLException, Exception {

		boolean bSuccess = false;
		PreparedStatement stmt = null;
		Statement stmt1 = null;

		StringBuffer sQuery = new StringBuffer("");

		Connection con = params.getConnection();

		try {

			HttpServletRequest request = params.getRequest();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();


			String sProjects[] = request.getParameterValues("proj_selection");
			String sType = request.getParameter("type");

			String sUserId = es.gIR_USERN;

			if (sProjects == null || sProjects.length <= 0) {
				bSuccess = false;
			} else {

				/*
				 * 1. first delete off the customized projects for the type...
				 * 2. insert the newly selected projects...
				 */

				con.setAutoCommit(false);

				sQuery.append("DELETE FROM ETS.ETS_USER_WS WHERE PROJECT_ID IN (SELECT PROJECT_ID FROM ETS.ETS_PROJECTS WHERE PROJECT_OR_PROPOSAL = '" + sType + "' AND PROJECT_TYPE = '" + Defines.AIC_WORKSPACE_TYPE + "') AND USER_ID = '" + sUserId + "'");

				stmt1 = con.createStatement();
				stmt1.executeUpdate(sQuery.toString());

				sQuery.setLength(0);
				sQuery.append("INSERT INTO ETS.ETS_USER_WS (USER_ID,PROJECT_ID) VALUES (?,?)");

				stmt = con.prepareStatement(sQuery.toString());

				for (int i = 0; i < sProjects.length; i++) {

					stmt.clearParameters();

					stmt.setString(1,sUserId);
					stmt.setString(2,sProjects[i].trim());

					stmt.executeUpdate();
				}
				bSuccess = true;
				con.commit();
				con.setAutoCommit(true);
			}



		} catch (SQLException e) {
			con.rollback();
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			ETSDBUtils.close(stmt1);
			ETSDBUtils.close(stmt);
			con.setAutoCommit(true);
		}

		return bSuccess;

	}

	private static void displayConfirmation(ETSParams params) throws Exception {



		PrintWriter out = params.getWriter();
		HttpServletRequest request = params.getRequest();

		String sType = ETSUtils.checkNull(request.getParameter("type"));

		String sInsert = "";

		if (sType.trim().equalsIgnoreCase("O")) {
			sInsert = "<b>My E&TS proposals</b>";
		} else if (sType.trim().equalsIgnoreCase("P")) {
			sInsert = "<b>My Collaboration Center </b>";
		} else if (sType.trim().equalsIgnoreCase("C")) {
			sInsert = "<b>Client voice</b>";
		}


		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col66\" align=\"left\">You have successfully customized " + sInsert + " in your Collaboration Center main page.</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");
		//out.println("<br />");

		printGreyDottedLine(out);

		out.println("<br />");
		out.println("<br />");

		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"col67\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "AICConnectServlet.wss?linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"Continue\" border=\"0\" /></a></td>");
		out.println("<td headers=\"col68\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col69\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" width=\"21\" height=\"21\" alt=\"Back to customize\" border=\"0\" /></td><td headers=\"col70\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "AICCustomizeServlet.wss?type=" + sType + "&flag=Y&linkid=" + params.getLinkId() + "\" >Back to customize page</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");


		out.println("<br />");
		out.println("<br />");



	}


}
