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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSUtils;

/**
 * 01/24/2004
 * changed by phani to get the project contact info from session,instead of from DB, to reduce 2 qrys for
 * every page
 */

/**
 * 03/04/2004
 * changed by Navneet
 * added a method: public StringBuffer getContactBox()
 * and changed printContactBox(PrintWriter) to print the StringBuffer returned by this new method
 * also both these methods now throw Exception instead of silently failing
 */

public class ETSClientCareContact {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";

	private String projid;
	private HttpServletRequest request;
	private Connection con = null;

	// ETSContact(String p_id,ETSDatabaseManager databaseManager) {
	public ETSClientCareContact(Connection conn,String p_id, HttpServletRequest request) {
		this.con = conn;
		this.projid = p_id;
		this.request = request;
	}

	public void printContactBox(PrintWriter writer) throws Exception {
		writer.println(getContactBox());
	}

	public StringBuffer getContactBox() throws Exception {

		StringBuffer buf = new StringBuffer();

		try {
			
			String sPrimary = ETSSetMetDAO.getPrimaryContact(this.con,this.projid);
			
			boolean isAvailable = false;
			

			buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
			buf.append("<tr>");
			buf.append("<td class=\"tblue\" height=\"18\" width=\"150\"><b>&nbsp;&nbsp;Your contact</b></td>");
			buf.append("</tr>");
			buf.append("</table>");

			buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			buf.append("<tr>");
			buf.append("<td class=\"tgreen\" valign=\"top\" width=\"150\">");
			buf.append("<table cellspacing=\"1\" cellpadding=\"0\" border=\"0\" width=\"100%\"> ");
			buf.append("<tr>");
			buf.append("<td valign=\"top\" width=\"100%\">");
			
			
			if (ETSUtils.isUserPhotoAvailable(con,sPrimary)) {

				isAvailable = true;
				
				// spacer
				buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%>");			
				buf.append("<tr valign=\"middle\">");
				buf.append("<td colspan=\"4\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\" align=\"middle\">&nbsp;</td>");
				buf.append("</tr>");
				
				buf.append("<tr valign=\"middle\">");
				buf.append("<td width=\"17\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\" align=\"middle\">&nbsp;</td>");
				buf.append("<td colspan=\"2\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\" align=\"middle\"><img src=\"" + Defines.SERVLET_PATH + "ETSUserPhotoServlet.wss?userid=" + sPrimary + "\" border=\"0\" alt=\"Client care advocate\" width=\"115\" height=\"115\" /></td>");
				buf.append("<td style=\"background-color: #ffffff; color: #000000; font-weight: normal\" align=\"middle\">&nbsp;</td>");
				buf.append("</tr>");
				
				// spacer			
				buf.append("<tr valign=\"middle\">");
				buf.append("<td colspan=\"4\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\" align=\"middle\">&nbsp;</td>");
				buf.append("</tr>");
				
	
				// bar			
				buf.append("<tr valign=\"middle\">");
				buf.append("<td colspan=\"4\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\" >");
				buf.append("<!-- Begin Gray dotted line -->");
				buf.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				buf.append("<tr>");
				buf.append("<td width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				buf.append("<td background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
				buf.append("<td width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				buf.append("</tr>");
				buf.append("</table>");
				buf.append("<!-- End Gray dotted line -->");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("</table>");
			}
			
			buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			buf.append("<tr valign=\"middle\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\">");
			buf.append("<td width=\"17\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\" align=\"middle\">&nbsp;</td>");
			buf.append("<td style=\"background-color: #ffffff; color: #000000; font-weight: normal; word-wrap:break-word;width:125px\" align=\"left\"><b>" + ETSUtils.getUsersName(this.con,sPrimary) + "</b></td>");
			buf.append("</tr>");
			buf.append("</table>");

			buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			buf.append("<tr valign=\"middle\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\">");
			buf.append("<td width=\"17\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\" align=\"middle\">&nbsp;</td>");
			buf.append("<td width=\"16\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\"><img src=\"" + Defines.ICON_ROOT + "em.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
			buf.append("<td  align=\"left\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\"><a href=\"mailto:" + ETSUtils.getUserEmail(this.con, sPrimary) + "\">E-mail me</a></td>");
			buf.append("<td align=\"middle\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\">&nbsp;</td>");
			buf.append("</tr>");
			buf.append("</table>");

			buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			buf.append("<tr valign=\"middle\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\">");
			buf.append("<td width=\"17\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\" align=\"middle\" >&nbsp;</td>");
			buf.append("<td width=\"16\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\"><img src=\"" + Defines.ICON_ROOT + "ph.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
			buf.append("<td  align=\"left\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\">" + ETSUtils.getUserPhone(this.con,sPrimary) + "</td>");
			buf.append("<td align=\"middle\" style=\"background-color: #ffffff; color: #000000; font-weight: normal;\">&nbsp;</td>");
			buf.append("</tr>");
			buf.append("</table>");
			
			buf.append("</td>");
			buf.append("</tr>");
			buf.append("</table>");
			buf.append("</td>");
			buf.append("</tr>");
			buf.append("</table>");
			
		} catch (SQLException se) {
			throw se;
		} catch (Exception ex) {
			throw ex;
		}

		return buf;

	}

} // end of class
