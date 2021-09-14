/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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


/*
 * Created on Sep 27, 2005
 * @author v2sathis@us.ibm.com
 */
 
package oem.edge.ets.fe.survey;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 */
public class ETSSurveyPrinterFriendlyServlet extends HttpServlet {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.5";
	
	private static Log logger = EtsLogger.getLogger(ETSSurveyPrinterFriendlyServlet.class);


	/**
	 * @see java.lang.Object#Object()
	 */
	public ETSSurveyPrinterFriendlyServlet() {
		super();
	}


	/**
	 * Method getClassVersion.
	 * @return String
	 */
	public static String getClassVersion() {
		return CLASS_VERSION;
	}


	/**
	 * Method service.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	    response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		Connection conn = null;
		String Msg = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		Hashtable params;

		int topCatId = 0;
		ETSCat topCat;
		ETSCat subCat;
		ETSCat currentCat;
		ETSUser user;
		String sLink;

		try {

			conn = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, conn)) {
				return;
			}

			String sProjId = request.getParameter("proj");
			if (sProjId == null || sProjId.trim().equals("")) {
				sProjId = "";
			} else {
				sProjId = sProjId.trim();
			}

			String sYear = request.getParameter("survey_year");
			if (sYear == null || sYear.trim().equals("")) {
				sYear = "";
			} else {
				sYear = sYear.trim();
			}

			String sResponseId = request.getParameter("response_id");
			if (sResponseId == null || sResponseId.trim().equals("")) {
				sResponseId = "";
			} else {
				sResponseId = sResponseId.trim();
			}


			ETSProj proj = ETSUtils.getProjectDetails(conn,sProjId);
			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

			params = ETSUtils.getServletParameters(request);

			PopupHeaderFooter header = new PopupHeaderFooter();
			//header.setHeader("");
			header.setPageTitle(prop.getAppName() + " - Survey - Printer Friendly");
			out.println(header.printPopupHeader());
			
			ETSUtils.popupHeaderLeft("Survey","",out);
			
			out.println("<form name=\"SurveyActionPlan\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSSurveyActionPlanServlet.wss\">");

			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");

			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"survey_year\" value=\"" + sYear + "\" />");

			displaySurvey(conn,out,proj,sYear,sResponseId);

			out.println("</form>");
			//out.println("<br /><br />");

			ETSUtils.popupHeaderRight(out);

			out.println(header.printPopupFooter());


		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			e.printStackTrace();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(conn);
			out.flush();
			out.close();
		}
	}


	/**
	 * @param conn
	 * @param out
	 * @param proj
	 * @param sYear
	 * @param sResponseId
	 */
	private void displaySurvey(Connection conn, PrintWriter out, ETSProj proj, String sYear, String sResponseId) throws SQLException, Exception {
		
		try {
		
			ETSSurvey survey = ETSSurveyDAO.getSurveyData(conn,sYear,sResponseId);
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\"><b>Respondent name:</b></td>");
			out.println("<td headers=\"\" align=\"left\">" + survey.getFirstName() + " " + survey.getLastName() + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\"><b>Respondent title:</b></td>");
			out.println("<td headers=\"\" align=\"left\">" + survey.getTitle() + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\"><b>Respondent country:</b></td>");
			out.println("<td headers=\"\" align=\"left\">" + survey.getCountry() + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\"><b>Survey date:</b></td>");
			out.println("<td headers=\"\" align=\"left\">" + survey.getSurveyDate() + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
						
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" ><b>Note:</b> Rating of <b>" + ETSSurveyConstants.RATING_NA_REPLACE + "</b> means Don't know / No answer</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
			ETSSurveyDetails details = ETSSurveyFuncs.getSurveyDetails(conn,sYear,sResponseId);
		
			Vector vSurveyData = details.getData();
		
			if (vSurveyData != null && vSurveyData.size() >=0) {

				out.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> <tr> <td class=\"tblue\" height=\"18\">Survey rating</td> </tr> <tr> <td>");
			
			
				out.println("<table summary=\"\" width=\"600\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\" class=\"v14-gray-table-border\">");
				out.println("<tr style=\"background-color: #eeeeee\">");
				out.println("<td headers=\"\" width=\"40\" align=\"left\" valign=\"top\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"240\" align=\"left\" valign=\"top\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + details.getDivision() + "</b></td>");
				out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + details.getProvider1() + "</b></td>");
				out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + details.getProvider2() + "</b></td>");
				out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + details.getProvider3() + "</b></td>");
				out.println("</tr>");
			
//				out.println("<tr >");
//				out.println("<td headers=\"\" colspan=\"6\" align=\"left\" valign=\"top\">");
//				printGreyDottedLine(out);
//				out.println("</td>");
//				out.println("</tr>");
			
				int iCount = 1;
				for (int i = 0; i < vSurveyData.size(); i++) {
					ETSSurveyQAData data = (ETSSurveyQAData) vSurveyData.elementAt(i);
					if (data.getAnswerType().equalsIgnoreCase(ETSSurveyConstants.DISPLAY_RATING)) {
						if ((iCount %2) == 0) {
							out.println("<tr style=\"background-color: #eeeeee\">");	
						} else {
							out.println("<tr >");
						}
						iCount = iCount + 1;
						out.println("<td headers=\"\" width=\"40\" align=\"left\" valign=\"top\"><b>" + data.getQuestionNo() + ".</b></td>");
						out.println("<td headers=\"\" width=\"240\" align=\"left\" valign=\"top\">" + data.getQuestionText() + "</td>");
						out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + data.getAnswer1() + "</b></td>");
						out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + data.getAnswer2() + "</b></td>");
						out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + data.getAnswer3() + "</b></td>");
						out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + data.getAnswer4() + "</b></td>");
						out.println("</tr>");
//						out.println("<tr >");
//						out.println("<td headers=\"\" colspan=\"6\" align=\"middle\" valign=\"top\">");
//						printGreyDottedLine(out);
//						out.println("</td>");
//						out.println("</tr>");
					}
				}
				out.println("</table>");
				out.println("</td> </tr> </table>"); 


				out.println("<br /><br />");

				out.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> <tr> <td class=\"tblue\" height=\"18\">Survey details</td> </tr> <tr> <td>");
			
				out.println("<table summary=\"\" width=\"600\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
			
				for (int i = 0; i < vSurveyData.size(); i++) {
					ETSSurveyQAData data = (ETSSurveyQAData) vSurveyData.elementAt(i);
					if (data.getAnswerType().equalsIgnoreCase(ETSSurveyConstants.DISPLAY_TEXT) && !data.getAnswer1().equalsIgnoreCase("")) {
						out.println("<tr >");
						out.println("<td headers=\"\" width=\"40\" align=\"left\" valign=\"top\"><b>" + data.getQuestionNo() + ".</b></td>");
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><b>" + data.getQuestionText() + "</b></td>");
						out.println("</tr>");
						out.println("<tr >");
						out.println("<td headers=\"\" width=\"40\" align=\"left\" valign=\"top\">&nbsp;</td>");
						out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + data.getAnswer1() + "</td>");
						out.println("</tr>");
						out.println("<tr >");
						out.println("<td headers=\"\" colspan=\"2\" align=\"left\" valign=\"top\">&nbsp;</td>");
						out.println("</tr>");
					}
				}
				out.println("</table>");
				out.println("</td> </tr> </table>"); 
			}
				
			out.println("<br /><br />");				
			
			
			out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td align=\"left\" width=\"20\"><img src=\"" + Defines.ICON_ROOT + "printer.gif\" width=\"16\" height=\"16\" alt=\"Print\" border=\"0\" /></td>");
			out.println("<td align=\"left\" ><a href=\"javascript:window.print()\">Print this page</a>");
			out.println("<noscript><br />Javascript is not enabled. To print this page, please right click your mouse and select the print option.</noscript>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			
			
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
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

}
