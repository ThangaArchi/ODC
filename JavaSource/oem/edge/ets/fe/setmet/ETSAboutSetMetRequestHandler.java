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


package oem.edge.ets.fe.setmet;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSAboutSetMetRequestHandler {


	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.3";

	private ETSParams params;

	private EdgeAccessCntrl es = null;
	

	public ETSAboutSetMetRequestHandler(ETSParams parameters, EdgeAccessCntrl es1) {
		
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
			 
			displayAboutSetMet();

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
	private void displayAboutSetMet() throws SQLException, Exception {


		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" >" + ETSUtils.getBookMarkString("<span class=\"subtitle\">Set/Met Process</span>","",true) + "</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br /><br />");
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"middle\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ABOUTSETMET&mod=0\" width=\"100\" height=\"100\" alt=\"Set/Met\" border=\"0\" /></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br /><br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"middle\" class=\"subtitle\"><b>Settting customer expectations then ensuring they are Met</b></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br /><br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"left\" class=\"subtitle\"><b>What is Set/Met?</b></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"left\" >Set/Met is a simple customer dialogue technique that improves customer relationships and is a key tool for building customer confidence in E&TS. Set/Met provides an opportunity to listen to the customer's expectations, understand how the customer sees IBM compared to our competitors and identify specific things that we can do to ensure that IBM is the customer's vendor of choice. The real value is acting on these identified specific items to improve our project delivery and performance, and to deliver highest value and satisfaction to our customers.</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"left\" >The Set/Met can be part of any regular customer contact call, it doesn't need to be a separate 'stand-alone' activity. Set/Met can be done in a face to face meeting with the customer or over the telephone. </td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"left\" >Because every customer has different needs, we conduct Set/Met interviews atleast twice a year to SET individual customer expectations and ensure they are MET. </td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"left\" >This gives our customers a regular forum in which to raise issues, keeps Client Teams focussed on customer satisfaction targets and enables us to deliver a tailored level of service to meet individual customers’ needs.</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"left\" ><b>How Set/Met works</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"left\" >Set/Met is a rolling process of continual improvement: </td>");
		out.println("</tr>");
		out.println("</table>");
	
		out.println("<br /><br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ABOUTSETMET&mod=1\" width=\"258\" height=\"375\" alt=\"About Set/Met\" border=\"0\" /></td>");
		out.println("</tr>");
		out.println("</table>");
	
	}

	
}
