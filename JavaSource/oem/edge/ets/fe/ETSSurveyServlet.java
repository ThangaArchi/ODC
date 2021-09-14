package oem.edge.ets.fe;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSSurveyServlet extends HttpServlet {
	private static Log logger = EtsLogger.getLogger(ETSSurveyServlet.class);
	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("text/html");
		PrintWriter writer = response.getWriter();

		Connection conn = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();

		Hashtable params;
		try {

			conn = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, req, conn)) {
				return;
			}

			if (!es.qualifyEntitlement(Defines.ETS_ADMIN_ENTITLEMENT)) {
				response.sendRedirect("ETSErrorServlet.wss?ecode=ETS-ACCESS-DENIED&sname=Access+Denied");
				return;
			}

			Hashtable hs = ETSUtils.getServletParameters(req);

			String sLink = req.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", Defines.LINKID);
				sLink = Defines.LINKID;
			}
			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, conn, hs);
			EdgeHeader.setPopUp("J");
			String assocCompany = es.gASSOC_COMP;
			String header = "E&TS Connect";

			String action = req.getParameter("action");
			if (logger.isDebugEnabled()) {
				logger.debug("ETSSurveyUpload::action : " + action);
			}
			EdgeHeader.setPageTitle(header);
			EdgeHeader.setHeader(header);

			EdgeHeader.setSubHeader("Upload Survey " + (action.equals("addSurveyData") ? "Data" : "Questions"));
			writer.println(ETSSearchCommon.getMasthead(EdgeHeader));
			writer.println(EdgeHeader.printBullsEyeLeftNav());
			writer.println(EdgeHeader.printSubHeader());

			// display the home page for the user

			Metrics.appLog(conn, es.gIR_USERN, "ETS_Upload");
			int behind = 5;
			int years[] = ETSSupportFunctions.getYears(behind, 1);
			if (action.equals("addSurveyData")) {

				writer.println("<form action=\"ETSSurveyUploadServlet.wss\" method=\"post\" enctype=\"multipart/form-data\" name=\"adddocForm\" >");
				writer.println("<input type=\"hidden\" name=\"action\" value=\"addSurveyData\" />");
				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println("<td align=\"left\" width=\"99%\"><label for=\"docfile\"><b>Survey Data File:</b></label></td></tr>");
				writer.println("<tr><td colspan=\"2\"><input type=\"file\" id=\"docfile\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"docfile\" value=\"\" /></td></tr>");
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println("<td align=\"left\" width=\"99%\"><label for=\"year\"><b>Year:</b></label></td></tr>");
				writer.println("<tr><td colspan=\"2\"><select name=\"year\" id=\"year\" />");
				for (int i = 0; i < years.length; i++)
					if (i == behind)
						writer.println("<option value=\"" + years[i] + "\" selected=\"selected\">" + years[i] + "</option>");
					else
						writer.println("<option value=\"" + years[i] + "\">" + years[i] + "</option>");
				writer.println("</select></td></tr>");

				writer.println("</table><br />");
				writer.println("<table><tr><td headers=\"\" valign=\"middle\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT +"submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"add document\" /> &nbsp; &nbsp;</td>");
				writer.println("<td headers=\"\" valign=\"middle\"><a href=\"ETSConnectServlet.wss?linkid=251000&pghead=E&TS+Connect&pgtitle=E&TS+Connect\"><img border=\"0\" name=\"back\" src=\"" + Defines.BUTTON_ROOT +"arrow_lt.gif\" width=\"21\" height=\"21\" align=\"bottom\" alt=\"Back\" /></a></td>");
				writer.println("<td headers=\"\" valign=\"middle\"><a href=\"ETSConnectServlet.wss?linkid=251000&pghead=E&TS+Connect&pgtitle=E&TS+Connect\">Back</a></td></tr></table>");
				writer.println("</form>");

			} else if (action.equals("addSurveyQuestion")) {

				writer.println("<form action=\"ETSSurveyUploadServlet.wss\" method=\"post\" enctype=\"multipart/form-data\" name=\"adddocForm\" >");
				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println("<td align=\"left\" width=\"99%\"><label for=\"filetype\"><b>Survey Data to be uploaded :</b></label></td></tr>");
				writer.println("<tr><td colspan=\"2\"><input type=\"radio\" id=\"filetype\" name=\"action\" value=\"questionData\" checked=\"checked\" />&nbsp;Survery questions data</td></tr>");
				writer.println("<tr><td colspan=\"2\"><input type=\"radio\" id=\"filetype\" name=\"action\" value=\"referenceData\" />&nbsp;Survery reference data</td></tr>");
				writer.println("<tr><td colspan=\"2\"><input type=\"radio\" id=\"filetype\" name=\"action\" value=\"mappingData\" />&nbsp;Survery mapping data</td></tr>");

				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println("<td align=\"left\" width=\"99%\"><label for=\"docfile\"><b>Survey Question File:</b></label></td></tr>");
				writer.println("<tr><td colspan=\"2\"><input type=\"file\" id=\"docfile\" size=\"50\" name=\"docfile\" value=\"\" /></td></tr>");
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println("<td align=\"left\" width=\"99%\"><label for=\"year\"><b>Year:</b></label></td></tr>");
				writer.println("<tr><td colspan=\"2\"><select name=\"year\" id=\"year\" />");
				for (int i = 0; i < years.length; i++)
					if (i == behind)
						writer.println("<option value=\"" + years[i] + "\" selected=\"selected\">" + years[i] + "</option>");
					else
						writer.println("<option value=\"" + years[i] + "\">" + years[i] + "</option>");
				writer.println("</select></td></tr>");
				writer.println("</table><br />");
				writer.println("<table><tr><td headers=\"\" valign=\"middle\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT +"submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"add document\" /> &nbsp; &nbsp;</td>");
				writer.println("<td headers=\"\" valign=\"middle\"><a href=\"ETSConnectServlet.wss?linkid=251000&pghead=E&TS+Connect&pgtitle=E&TS+Connect\"><img border=\"0\" name=\"back\" src=\"" + Defines.BUTTON_ROOT +"arrow_lt.gif\" width=\"21\" height=\"21\" align=\"bottom\" alt=\"Back\" /></a></td>");
				writer.println("<td headers=\"\" valign=\"middle\"><a href=\"ETSConnectServlet.wss?linkid=251000&pghead=E&TS+Connect&pgtitle=E&TS+Connect\">Back</a></td></tr></table>");
				writer.println("</form>");
				writer.println("<br /><br /><b>Important: The data will be replaced in table</b>");
			}

			writer.println(EdgeHeader.printBullsEyeFooter());

		} catch (SQLException e) {
			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
		} catch (Exception e) {
			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(conn);
			writer.flush();
			writer.close();
		}

	}

}
