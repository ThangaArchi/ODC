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

public class ETSUnAuthorizedServlet extends HttpServlet {
	private static Log logger = EtsLogger.getLogger(ETSUnAuthorizedServlet.class);
	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("text/html");
	    PrintWriter writer = null;
	    try {
	        writer = response.getWriter();
	    }
	    catch(IOException e) {
	        e.printStackTrace(System.err);
	    }

	    if (writer == null) {
	        return;
	    }
		Connection conn = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();

		Hashtable params;
		try {

			conn = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, req, conn)) {
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
			String header = "E&TS Connect";

			EdgeHeader.setPageTitle(header);
			EdgeHeader.setHeader(header);

			EdgeHeader.setSubHeader("Unauthorized access");

			writer.println(ETSSearchCommon.getMasthead(EdgeHeader));
			writer.println(EdgeHeader.printBullsEyeLeftNav());
			writer.println(EdgeHeader.printSubHeader());

			printGreyDottedLine(writer);
			
		    printMsg(writer);

			writer.println(EdgeHeader.printBullsEyeFooter());

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(conn);
			writer.flush();
			writer.close();
		}

	}

	public static void printMsg(PrintWriter writer) {
	    
			writer.println("<br />");
			writer.println("<table sumary=\"\" width=\"100%\"><tr><td headers=\"\">");
			writer.println("<b>You are not authorized to access this workspace or function. <br /><br />Please contact your IBM systems administrator for more information.</b>");
			writer.println("</td><tr></table>");
			writer.println("<br />");
			
			printGreyDottedLine(writer);
			
			writer.println("<br />");
			
			writer.println("<table summary=\"\"><tr>");
			writer.println("<td headers=\"\" width=\"16\"><a href=\"javascript:window.history.back()\" ><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
			writer.println("<td headers=\"\" align=\"left\"><a href=\"javascript:window.history.back()\"  class=\"fbox\"><b>Back</b></a></td>");
			writer.println("</tr></table>");
			writer.println("<noscript>");
			writer.println("<table summary=\"\"><tr>");
			writer.println("<td headers=\"\" align=\"left\">Please use your browser back button to go to previous page.</td>");
			writer.println("</tr></table>");
			writer.println("</noscript>");
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
