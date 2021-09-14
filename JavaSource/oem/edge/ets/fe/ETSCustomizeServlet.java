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
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;


public class ETSCustomizeServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.10";
	
	private static Log logger = EtsLogger.getLogger(ETSCustomizeServlet.class);

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

			UnbrandedProperties prop = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);
			
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

			EdgeHeader.setPageTitle("E&TS Connect - Customize your E&TS main page");
			EdgeHeader.setHeader("Customize your E&TS main page");
			//EdgeHeader.setSubHeader("Customize");

			out.println(EdgeHeader.printBullsEyeHeader());
			out.println(EdgeHeader.printBullsEyeLeftNav());
			out.println(EdgeHeader.printSubHeader());
			
			// top table to define the content and right sides..
			out.println("<form name=\"CustomizeWorkspace\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSCustomizeServlet.wss\">");
			
		    out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			//out.println("<tr valign=\"top\"><td headers=\"\" width=\"443\" valign=\"top\" class=\"small\"><b>You are logged in as:</b> " + es.gIR_USERN + "</td>");
			out.println("<tr valign=\"top\"><td headers=\"\" width=\"443\" valign=\"top\" class=\"small\"><table summary=\"\" width=\"100%\"><tr><td headers=\"\" width=\"60%\">" + es.gIR_USERN + "</td><td headers=\"\" width=\"40%\" align=\"right\">" + sDate + "</td></tr></table></td>");
			out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			out.println("<td headers=\"\" class=\"small\" align=\"right\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\"  width=\"90\" align=\"right\">Secure content</td></tr></table></td></tr>");
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
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" align=\"left\"><img alt=\"protected content\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\"  align=\"left\"><span class=\"fnt\">A key icon displayed in a page indicates that the page is secure and password-protected.</span></td></tr></table>");

			out.println(EdgeHeader.printBullsEyeFooter());

		} catch (SQLException e) {
            e.printStackTrace();
            if (logger.isErrorEnabled()) {
            	logger.error(this,e);
            }
			ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(e), "Error occurred on E&TS Connect.");
		} catch (Exception e) {
            e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(e), "Error occurred on IBM E&TS Connect.");
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
			boolean bITAR = false;
			
			String sType = ETSUtils.checkNull(request.getParameter("type"));
			String sGeo[] = request.getParameterValues("geo");
			String sDel[] = request.getParameterValues("delivery");
			String sInd[] = request.getParameterValues("industry");
			String sSort = ETSUtils.checkNull(request.getParameter("sort"));
			
			String sFirst = ETSUtils.checkNull(request.getParameter("flag"));
			
			if (sType != null && !sType.trim().equalsIgnoreCase("")) {
				
				String sInsert = "";
				
				if (sType.trim().equalsIgnoreCase("O")) {
					sInsert = "<b>My E&TS proposals</b>";	
				} else if (sType.trim().equalsIgnoreCase("P")) {
					sInsert = "<b>My E&TS projects</b>";
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
				
				String sGeoValues = "";
				String sDeliveryValues = "";
				String sIndustryValues = "";
				
				String sSortOrder = "COMPANY";
				
				if (sSort == null || sSort.trim().equalsIgnoreCase("")) {
					sSort = "CLIENT";
					sSortOrder = "COMPANY";
				} else {
					sSort = sSort.trim();
				}
				
				if (sSort.trim().equalsIgnoreCase("CLIENT")) {
					sSortOrder = "COMPANY";
				} else if (sSort.trim().equalsIgnoreCase("DELIVERY TEAM")) {
					sSortOrder = "DELIVERY_TEAM";
				} else if (sSort.trim().equalsIgnoreCase("GEOGRAPHY")) {
					sSortOrder = "GEOGRAPHY";
				} else if (sSort.trim().equalsIgnoreCase("INDUSTRY")) {
					sSortOrder = "INDUSTRY";
				} else if (sSort.trim().equalsIgnoreCase("WORKSPACE NAME")) {
					sSortOrder = "PROJECT_NAME";
				}
				
				if (sGeo == null) {
					sGeoValues = "";
				} else {
					for (int i = 0; i < sGeo.length; i++) {
						if (sGeoValues.trim().equalsIgnoreCase("")) {
							sGeoValues = sGeo[i].trim();
						} else {
							sGeoValues = sGeoValues + "," + sGeo[i].trim();
						}
					}
				}

				if (sDel == null) {
					sDeliveryValues = "";
				} else {
					for (int i = 0; i < sDel.length; i++) {
						if (sDeliveryValues.trim().equalsIgnoreCase("")) {
							sDeliveryValues = sDel[i].trim();
						} else {
							sDeliveryValues = sDeliveryValues + "," + sDel[i].trim();
						}
					}
				}
				
				if (sInd == null) {
					sIndustryValues = "";
				} else {
					for (int i = 0; i < sInd.length; i++) {
						if (sIndustryValues.trim().equalsIgnoreCase("")) {
							sIndustryValues = sInd[i].trim();
						} else {
							sIndustryValues = sIndustryValues + "," + sInd[i].trim();
						}
					}
				}				
			
				if (es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT, "tg_member=MD")) {
					bAdmin = true;
				} else if (es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD")) {
					bAdmin = true;
				}
	
				if (es.Qualify(Defines.ITAR_ENTITLEMENT, "tg_member=MD")) {
					bITAR = true;
				}
				
				if (bAdmin) {
					sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY,GEOGRAPHY,DELIVERY_TEAM,INDUSTRY, IS_ITAR, (SELECT COUNT(1) AS CUST_FLAG FROM ETS.ETS_USER_WS WHERE PROJECT_ID = P.PROJECT_ID AND USER_ID = '" + sUserId + "' GROUP BY PROJECT_ID) FROM ETS.ETS_PROJECTS P WHERE PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' AND PROJECT_OR_PROPOSAL = '" + sType + "' AND PARENT_ID = '0' AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "' ORDER BY " + sSortOrder + " for READ ONLY");
				} else {
					sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY,GEOGRAPHY,DELIVERY_TEAM,INDUSTRY, IS_ITAR, (SELECT COUNT(1) AS CUST_FLAG FROM ETS.ETS_USER_WS WHERE PROJECT_ID = P.PROJECT_ID AND USER_ID = '" + sUserId + "' GROUP BY PROJECT_ID) FROM ETS.ETS_PROJECTS P WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "' AND ACTIVE_FLAG = '" + Defines.USER_ENTITLED + "') AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' AND PROJECT_OR_PROPOSAL = '" + sType + "'  AND PARENT_ID = '0' AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "' ORDER BY " + sSortOrder + " for READ ONLY");
				}
	
				if (logger.isDebugEnabled()) {
					logger.debug("ETSConnectServlet::displayUserProjects::Query : " + sQuery.toString());
				}
			
				out.println("<input type=\"hidden\" name=\"type\" value=\"" + sType + "\" />");

				if (bError) {
					out.println("<br />");
					out.println("<br />");
					
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"\" align=\"left\" ><span style=\"color:#ff3333\"><b>You have to select at least one workspace before you click Submit.</b><span></td>");
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
					out.println("<td headers=\"\" align=\"left\" >Please select the workspaces you would like to see in " + sInsert + " in your E&TS main page and click on submit. To automatically select workspaces based on Geography or Industry or Delivery team, use the appropriate filters provided below and click on <b>Go</b> button. Alternately, you can cherry-pick your workspaces from the list of workspaces below.</td>");
					out.println("</tr>");
					out.println("</table>");
 				} else {
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"\" align=\"left\" >Please select the workspaces you would like to see in " + sInsert + " in your E&TS main page and click on submit.</td>");
					out.println("</tr>");
					out.println("</table>");
 				}
	
	
				if (bInternal) {
					out.println("<br />");
		
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"\" align=\"left\" width=\"190\"><b><label for=\"label_geo\">Geography</label></b></td>");
					out.println("<td headers=\"\" align=\"left\" width=\"200\"><b><label for=\"label_delivery\">Delivery team</label></b></td>");
					out.println("<td headers=\"\" align=\"left\" ><b><label for=\"label_industry\">Industry</label></b></td>");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"\" align=\"left\" width=\"190\">" + displayGeo(con,"geo","label_geo",sUserId,sGeoValues,bAdmin, sType) + "</td>");
					out.println("<td headers=\"\" align=\"left\" width=\"200\">" + displayDeliveryTeam(con,"delivery","label_delivery",sUserId,sDeliveryValues,bAdmin, sType) + "</td>");
					out.println("<td headers=\"\" align=\"left\" >" + displayIndustry(con,"industry","label_industry",sUserId,sIndustryValues,bAdmin, sType) + "</td>");
					out.println("</tr>");
					out.println("</table>");
		
					out.println("<br />");
					
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\" >");
					out.println("<td headers=\"\" align=\"left\" width=\"80\"><b><label for=\"label_sort\">Sort by</label></b></td>");
					out.println("<td headers=\"\" align=\"left\" width=\"180\"  >" + displaySort("sort","label_sort",sSort) + "</td>");
					out.println("<td headers=\"\" align=\"left\" ><input type=\"image\" name=\"filter\" src=\"" + Defines.BUTTON_ROOT + "go.gif\" height=\"21\" width=\"21\" alt=\"Go\" /></td>");
					out.println("</tr>");
					out.println("</table>");
					
					out.println("<br />");
					out.println("<br />");
					
					printGreyDottedLine(out);
					
				}
				
				out.println("<br />");
				
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  align=\"left\">The following are the workspaces for which you have access to. Workspaces marked with <span style=\"color:#cc6600\">*</span> are the workspaces in your current customized list.</td></tr></table>");
				
				out.println("<br />");
				out.println("<br />");
				
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
				out.println("<tr valign=\"top\" class=\"tdblue\">");
				out.println("<td headers=\"\" colspan=\"6\" align=\"left\" >Your workspaces</td>");
				out.println("</tr>");
	
				if (bInternal) {
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\"  colspan=\"2\" align=\"left\" valign=\"top\" ><b>Client</b></td>");
					out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\" ><b>Workspace name</b></td>");
					out.println("<td headers=\"\"  width=\"100\" align=\"left\" valign=\"top\" ><b>Geography</b></td>");
					out.println("<td headers=\"\"  width=\"110\" align=\"left\" valign=\"top\" ><b>Delivery team</b></td>");
					out.println("<td headers=\"\"  align=\"left\" valign=\"top\" ><b>Industry</b></td>");
					out.println("</tr>");
				} else {
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\"  colspan=\"2\" align=\"left\" valign=\"top\" ><b>Client</b></td>");
					out.println("<td headers=\"\"  align=\"left\" valign=\"top\" ><b>Workspace name</b></td>");
					out.println("</tr>");
				}
						
	
				int iCount = 0;
	
				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());
	
				while (rs.next()) {
	
					String sProjID = rs.getString(1);
					String sProjName = rs.getString(2);
					String sCompany = rs.getString(3);
					String sGeography = rs.getString(4);
					String sDeliveryTeam = rs.getString(5);
					String sIndustry = rs.getString(6);
					String isITAR = ETSUtils.checkNull(rs.getString(7));
					String sCustomize = rs.getString(8);

					boolean bDisplay = false;
						
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
	
					
						String sCustFlag = "N";
	
						if (sFirst.trim().equalsIgnoreCase("y")) {
							if (sCustomize == null || sCustomize.trim().equalsIgnoreCase("")) {
								sCustFlag = "N";
							} else {
								sCustFlag = "Y";
							}
						} else {
							
							boolean bGeoFlag = false;
							boolean bDeliveryFlag = false;
							boolean bIndustryFlag = false;
						
							
							if (sGeo == null) {
								sGeoValues = "";
								bGeoFlag = true;
							} else {
								for (int i = 0; i < sGeo.length; i++) {
									if (sGeo[i].trim().equalsIgnoreCase(sGeography)) {
										bGeoFlag = true;
										break;
									}
								}
							}						
							
							if (sDel == null) {
								sDeliveryValues = "";
								bDeliveryFlag = true;
							} else {
								for (int i = 0; i < sDel.length; i++) {
									if (sDel[i].trim().equalsIgnoreCase(sDeliveryTeam)) {
										bDeliveryFlag = true;
										break;
									}
								}
							}
					
	
							if (sInd == null) {
								sIndustryValues = "";
								bIndustryFlag = true;
							} else {
								for (int i = 0; i < sInd.length; i++) {
									if (sInd[i].trim().equalsIgnoreCase(sIndustry)) {
										bIndustryFlag = true;
										break;
									}
								}
							}
							
							if (bGeoFlag && bIndustryFlag && bDeliveryFlag) {
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
							out.println("<td headers=\"\"  width=\"10\" align=\"middle\" valign=\"top\" ><input type=\"checkbox\" id=\"label_proj" + iCount + "\" class=\"iform\" name=\"proj_selection\" value=\"" + sProjID + "\" checked=\"checked\" selected=\"selected\" /></td>");
						} else {
							out.println("<td headers=\"\"  width=\"10\" align=\"middle\" valign=\"top\" ><input type=\"checkbox\" id=\"label_proj" + iCount + "\" class=\"iform\" name=\"proj_selection\" value=\"" + sProjID + "\" /></td>");
						}
							
						out.println("<td headers=\"\"  width=\"100\" align=\"left\" valign=\"top\" ><label for=\"label_proj" + iCount + "\">" + sCompany + sCustomize + "</label></td>");
						if (bInternal) {
							out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\" >" +  sProjName  + "</td>");
							out.println("<td headers=\"\"  width=\"100\" align=\"left\" valign=\"top\"  >" +  sGeography + "</td>");
							out.println("<td headers=\"\"  width=\"110\" align=\"left\" valign=\"top\"  >" + sDeliveryTeam + "</td>");
							out.println("<td headers=\"\"  align=\"left\" valign=\"top\" >" + sIndustry + "</td>");
						} else {
							out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\" >" +  sProjName  + "</td>");
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
				out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
				out.println("</tr>");
				out.println("</table>");
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


	private static String displayGeo(Connection con, String sSelectName, String sLabelId, String sUserId, String sGeoList, boolean bAdmin, String sType) throws SQLException, Exception {
	
			StringBuffer out = new StringBuffer("");
	
			try {
	
				String sAvailable = "," + sGeoList + ",";
				out.append("<select size=\"5\" style=\"width:190px\" multiple=\"multiple\" width=\"190px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");
	
				Vector vGeo = getValues(con,Defines.GEOGRAPHY);

				if (vGeo != null && vGeo.size() > 0) {

					for (int i = 0; i < vGeo.size(); i++) {

						String sGeo = (String) vGeo.elementAt(i);

						if (sAvailable.indexOf("," + sGeo + ",") >= 0)  {
							out.append("<option value=\"" + sGeo + "\" selected=\"selected\">" + sGeo + "</option>");
						} else {
							out.append("<option value=\"" + sGeo + "\" >" + sGeo + "</option>");
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

	private static String displayDeliveryTeam(Connection con, String sSelectName, String sLabelId, String sUserId, String sDeliveryTeamList, boolean bAdmin,String sType) throws SQLException, Exception {
	
			StringBuffer out = new StringBuffer("");
	
			try {
	
				String sAvailable = "," + sDeliveryTeamList + ",";
				out.append("<select size=\"5\" style=\"width:200px\" multiple=\"multiple\" width=\"200px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");
	
				Vector vDelivery = getValues(con,Defines.DELIVERY_TEAM);
	
				if (vDelivery != null && vDelivery.size() > 0) {
	
					for (int i = 0; i < vDelivery.size(); i++) {
	
						String sDelivery = (String) vDelivery.elementAt(i);
	
						if (sAvailable.indexOf("," + sDelivery + ",") >= 0)  {
							out.append("<option value=\"" + sDelivery + "\" selected=\"selected\">" + sDelivery + "</option>");
						} else {
							out.append("<option value=\"" + sDelivery + "\" >" + sDelivery + "</option>");
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

	private static String displayIndustry(Connection con, String sSelectName, String sLabelId, String sUserId, String sIndustryList, boolean bAdmin, String sType) throws SQLException, Exception {
	
			StringBuffer out = new StringBuffer("");
	
			try {
	
				String sAvailable = "," + sIndustryList + ",";
				out.append("<select size=\"5\" style=\"width:200px\" multiple=\"multiple\" width=\"200px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");
	
				Vector vIndustry = getValues(con,Defines.INDUSTRY);
	
				if (vIndustry != null && vIndustry.size() > 0) {
	
					for (int i = 0; i < vIndustry.size(); i++) {
	
						String sIndustry = (String) vIndustry.elementAt(i);
	
						if (sAvailable.indexOf("," + sIndustry + ",") >= 0)  {
							out.append("<option value=\"" + sIndustry + "\" selected=\"selected\">" + sIndustry + "</option>");
						} else {
							out.append("<option value=\"" + sIndustry + "\" >" + sIndustry + "</option>");
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
		
		vSort.addElement("CLIENT");
		vSort.addElement("DELIVERY TEAM");
		vSort.addElement("GEOGRAPHY");
		vSort.addElement("INDUSTRY");
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
				 
				sQuery.append("DELETE FROM ETS.ETS_USER_WS WHERE PROJECT_ID IN (SELECT PROJECT_ID FROM ETS.ETS_PROJECTS WHERE PROJECT_OR_PROPOSAL = '" + sType + "' AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "') AND USER_ID = '" + sUserId + "'");
				
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
			sInsert = "<b>My E&TS projects</b>";
		} else if (sType.trim().equalsIgnoreCase("C")) {
			sInsert = "<b>Client voice</b>";
		}
		 
		
		out.println("<br />");
	
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\">You have successfully customized " + sInsert + " in your E&TS main page.</td>");
		out.println("</tr>");
		out.println("</table>");		

		out.println("<br />");
		//out.println("<br />");
		
		printGreyDottedLine(out);

		out.println("<br />");
		out.println("<br />");
	
		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"130\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"Continue\" border=\"0\" /></a></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" width=\"21\" height=\"21\" alt=\"Back to customize\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCustomizeServlet.wss?type=" + sType + "&flag=Y&linkid=" + params.getLinkId() + "\" >Back to customize page</a></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");
	
		
		out.println("<br />");
		out.println("<br />");

				
		
	}
	

}
