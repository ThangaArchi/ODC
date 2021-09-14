/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

/************************** BOF : HEADER *************************************/
/*																			 */
/*	File Name 	: 	EdesignMyFSEInfoServlet.java							 */
/*	Release		:	2.7, 2.8, 2.9											 */
/*	Description	:	Displays My Account Section Menu.						 */
/*	Created By	: 	unknown													 */
/*	Date		:	unknown													 */
/*****************************************************************************/
/*  Change Log 	: 	Please Enter Changed on, Changed by and Desc			 */
/*****************************************************************************/
/*	Changed On  : 	06/20/2001												 */
/*	Changed By  : 	Sathish													 */
/*	Change Desc : 	Added Log4j logging									 	 */
/*					Changed getting details of FSE Details to get 			 */
/*					from FSE Function 										 */
/* 					Displays one MASTER FSE if no FSE found for user		 */
/*****************************************************************************/

/**
 * @author: Sathish
 */

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.wspace.ETSWorkspaceDAO;

public class ETSAboutSetMetServlet extends javax.servlet.http.HttpServlet {
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";


	/**
	 * @see java.lang.Object#Object()
	 */
	public ETSAboutSetMetServlet() {
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

			params = ETSUtils.getServletParameters(request);

			PopupHeaderFooter header = new PopupHeaderFooter();
			//header.setHeader("");
			header.setPageTitle("E&TS Connect - About Set/Met");
			out.println(header.printPopupHeader());
			
			ETSUtils.popupHeaderLeft("About Set/Met","",out);
			
			out.println("<form name=\"SetMetEditDemo\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss\">");

			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" >" + ETSUtils.getBookMarkString("<span class=\"subtitle\">Set/Met Process</span>","",false) + "</td>");
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

			out.println("<br /><br />");

			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" width=\"30\" align=\"left\" valign=\"middle\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Close\" /></td><td headers=\"\" align=\"left\" valign=\"middle\"><a href=\"javascript:cancel()\">Close</a></a></td></tr></table>");
			out.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");

			out.println("</form>");
			//out.println("<br /><br />");

			ETSUtils.popupHeaderRight(out);

			out.println(header.printPopupFooter());


		} catch (SQLException e) {
			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
		} catch (Exception e) {
			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(conn);
			out.flush();
			out.close();
		}
	}

}
