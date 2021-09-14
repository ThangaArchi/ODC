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

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.UserObject;
import oem.edge.ets.fe.aic.AICManageWorkspaceHandler;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.wspace.ETSManageWorkspaceHandler;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSProposalHome {


	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.42";
	
	private static Log logger = EtsLogger.getLogger(ETSProposalHome.class);


	/**
	 * Method getContactURL.
	 * @param con
	 * @return String
	 */
	private static String getInfoURL(Connection con, String sProjId, int iMod) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sContactURL = "";

		try {

			sQuery.append("SELECT INFO_LINK FROM ETS.ETS_PROJECT_INFO WHERE PROJECT_ID='" + sProjId + "' AND INFO_MODULE=" + iMod + " with ur");

			logger.debug("ETSProposalHome::getContactURL::Query : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			if (rs.next()) {
				sContactURL = rs.getString(1);
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return sContactURL;

	}


	private static void displayAlerts(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			int iTopCat = params.getTopCat();
			ETSProj proj = params.getETSProj();
			String sLinkId = params.getLinkId();
			UnbrandedProperties prop = params.getUnbrandedProperties();

			Vector vAlerts = getUpcomingAlerts(params);

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" class=\"tblue\">&nbsp;Messages</td>");
			out.println("</tr>");

			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);


					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Alert\" border=\"0\" /></td>");
					out.println("<td headers=\"\" colspan=\"3\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">" + calEntry.getSSubject() + "</a>");
					out.println("<br />");
					out.println("<span class=\"small\"><b>" + sDate + "</b>&nbsp;&nbsp;<span style=\"color:#666666\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</span></span>");
					out.println("</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					// divider

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					if (i >= 1) {
						break;
					}

				}

			} else {

                String sMess = prop.getMessagesText();
                
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_EXECUTIVE)) {
					sMess = prop.getVisitorMessagesText();
				} else {
					sMess = prop.getMessagesText();
				}

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"4\">&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"4\">" + sMess + "</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"4\">&nbsp;</td>");
				out.println("</tr>");
			}

			// the post a message should not be displayed to executive and visitor.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MANAGER)
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKFLOW_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MEMBER)) {
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Post a message\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"127\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Post a message</a></td>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=morealerts&linkid=" + sLinkId + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"More messages and options\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"284\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=morealerts&linkid=" + sLinkId + "\" class=\"fbox\">More messages and options</a></td>");
				out.println("</tr>");
				out.println("</table>");
			} else {
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=morealerts&linkid=" + sLinkId + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"More messages and options\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"284\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=morealerts&linkid=" + sLinkId + "\" class=\"fbox\">More messages and options</a></td>");
				out.println("</tr>");
				out.println("</table>");

			}


		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	public static void designedToHelpYouModule(ETSParams params) throws SQLException, Exception {


		PrintWriter out = params.getWriter();
		ETSProjectInfoBean projBean = params.getProjBeanInfo();
		Connection con = params.getConnection();

		// designed to help you module...

//		out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
//		out.println("<tr>");
//		out.println("<td headers=\"\"  style=\"background-color:#cccccc;\" height=\"18\" width=\"150\"><b style=\"color: #ffffff\">&nbsp;Designed to help you</b></td>");
//		out.println("</tr>");
//		out.println("</table>");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  style=\"background-color:#cccccc;\" valign=\"top\" width=\"150\">");
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr valign=\"middle\" >");
		out.println("<td headers=\"\"  style=\"background-color: #ffffff;\" align=\"center\"><a href=\"" + getInfoURL(con,"ETS_PROPOSAL_1",1) + "\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ETS_PROPOSAL_1&mod=1\" alt=\"" + projBean.getImageAltText("ETS_PROPOSAL_1",1) + "\" width=\"150\" height=\"149\" border=\"0\" /></a></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		//out.println("<br />");


	}



	private static void displayAllAlerts(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			UnbrandedProperties prop = params.getUnbrandedProperties();

			Vector vAlerts = getUpcomingAlerts(params);

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><span class=\"subtitle\">&nbsp;Upcoming messages</span></td>");
			out.println("</tr>");

			// divider

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					int iDuration = calEntry.getIDuration();

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Alert\" border=\"0\" /></td>");
					out.println("<td headers=\"\" width=\"427\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">" + calEntry.getSSubject() + "</a>");
					out.println("<br />");
					out.println("<span class=\"small\"><b>" + sDate + "</b>&nbsp;&nbsp;&nbsp;<span style=\"color:#666666\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</span></span>");
					out.println("</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					// the edit and cancel a message should be displayed to the user who created it and admin.
					// changed for 4.4.1
					if (calEntry.getSScheduleBy().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN)) {

						out.println("<tr>");
						out.println("<td headers=\"\" width=\"16\" align=\"center\">&nbsp;</td>");
						out.println("<td headers=\"\"  width=\"427\">[&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Edit this message</a>&nbsp;]&nbsp;&nbsp;&nbsp;[&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Cancel this message</a>&nbsp;]</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
						out.println("</tr>");

					}

					// divider

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");
				}

			} else {
				
                String sMess = prop.getMessagesText();

				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_EXECUTIVE)) {
					sMess = prop.getVisitorMessagesText();
				} else {
					sMess = prop.getMessagesText();
					
				}
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >" + sMess + "</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

                out.println("<tr valign=\"top\">");
                out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
                out.println("</tr>");
                out.println("<tr valign=\"top\">");
                out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
                out.println("</tr>");
                out.println("<tr valign=\"top\">");
                out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
                out.println("</tr>");

			}

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\">");

			// the post a message should not be displayed to executive and visitor.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MANAGER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKFLOW_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MEMBER)) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Post a message\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" width=\"100\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Post a message</a></td>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastalerts&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"View past messages\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastalerts&linkid=" + params.getLinkId() + "\" class=\"fbox\">View past messages</a></td>");
				out.println("</tr>");
				out.println("</table>");
			} else {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastalerts&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"View past messages\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastalerts&linkid=" + params.getLinkId() + "\" class=\"fbox\">View past messages</a></td>");
				out.println("</tr>");
				out.println("</table>");
			}

			out.println("</td></tr>");
			out.println("</table>");

			out.println("<br /><br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
			out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" class=\"fbox\" >Back to '" + params.getCurrentTabName() + "'</a></td>");
			out.println("</tr>");
			out.println("</table>");


		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	private static void displayPastAlerts(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			UnbrandedProperties prop = params.getUnbrandedProperties();

			Vector vAlerts = getPastAlerts(params);

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><span class=\"subtitle\">&nbsp;Past messages</span></td>");
			out.println("</tr>");

			// divider

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					int iDuration = calEntry.getIDuration();

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Alert\" border=\"0\" /></td>");
					out.println("<td headers=\"\" width=\"427\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">" + calEntry.getSSubject() + "</a>");
					out.println("<br />");
					out.println("<span class=\"small\"><b>" + sDate + "</b>&nbsp;&nbsp;&nbsp;<span style=\"color:#666666\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</span></span>");
					out.println("</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					// divider

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");
				}

			} else {


                String sMess = prop.getPrevMessagesText();
                
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_EXECUTIVE)) {
					sMess = prop.getVisitorPrevMessagesText();
				} else {
					sMess = prop.getPrevMessagesText();
				}

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><b>" + sMess + "</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
			}

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\">");

			// the post a message should not be displayed to executive and visitor.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MANAGER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKFLOW_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MEMBER)) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Post a message\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Post a message</a></td>");
				out.println("</tr>");
				out.println("</table>");
			}

			out.println("</td></tr>");
			out.println("</table>");

			out.println("<br /><br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
			out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"  class=\"fbox\">Back to '" + params.getCurrentTabName() + "'</a></td>");
			out.println("</tr>");
			out.println("</table>");


		} catch (Exception e) {
			throw e;
		} finally {
		}

	}



	private static void displayIWantToModule(ETSParams params) throws Exception {

        ETSProjectHome.displayIWantToModule(params);


	}

	public static void displayProposalHome(ETSParams params) throws SQLException, Exception {

		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			HttpServletRequest request = params.getRequest();

			ETSProjectInfoBean projBean = (ETSProjectInfoBean) request.getSession(false).getAttribute("ETSProjInfo");

			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());
			
			if (projBean == null || !projBean.isLoaded()) {
				projBean = ETSUtils.getProjInfoBean(con);
				request.getSession(false).setAttribute("ETSProjInfo",projBean);
			}
			
			String sETSOp = request.getParameter("etsop");

			if (sETSOp == null || sETSOp.trim().equals("")) {
				sETSOp = "";
			} else {
				sETSOp = sETSOp.trim();
			}

			if (sETSOp.trim().equals("")) {
				
				//out.println(ETSUtils.getBookMarkString("","",true));
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER)) {
					out.println(ETSUtils.getBookMarkString("<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&etsop=manage_wspace\" >Manage this workspace</a>","",true));
				} else {
					out.println(ETSUtils.getBookMarkString("","",true));
				}


				ETSUser user = ETSDatabaseManager.getPrimaryContact(proj.getProjectId());
				UserObject uo = AccessCntrlFuncs.getUserObject(con,user.getUserId(),true,false);
				String email = uo.gEMAIL;

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\"><tr valign=\"top\">");

				if (projBean.isImageAvailable(proj.getProjectId(),0)) {
					out.println("   <td headers=\"\"  valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=" + proj.getProjectId() + "&mod=0\" width=\"443\" height=\"60\" alt=\"" + projBean.getImageAltText(proj.getProjectId(),0) + "\" /><br /><br />");
					//out.println(projBean.getInfoDescription(proj.getProjectId(),0));
					out.println(prop.getAppName() + " allows us to share documents and collaborate successfully on completing this proposal. You can use the \"Documents\" tab to access related documents or use the \"Team\" tab to locate members of the proposal team. If you have any questions, please contact me at <a href=\"mailto:"+email+"\" class=\"fbox\">" + email + "</a>.");
					out.println("<br /><br />");
				} else {
					out.println("   <td headers=\"\"  valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ETS_PROPOSAL_1&mod=0\" width=\"443\" height=\"60\" alt=\"" + projBean.getImageAltText("ETS_PROPOSAL_1",0) + "\" /><br /><br />");
					//out.println(projBean.getInfoDescription("ETS_PROPOSAL_1",0));
					out.println(prop.getAppName() + " allows us to share documents and collaborate successfully on completing this proposal. You can use the \"Documents\" tab to access related documents or use the \"Team\" tab to locate members of the proposal team. If you have any questions, please contact me at <a href=\"mailto:"+email+"\" class=\"fbox\">" + email + "</a>.");
					out.println("<br /><br />");
				}
				out.println("</td></tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  width=\"443\" valign=\"top\">");

                displayAlerts(params);

                out.println("<br />");

                displayRecentDocuments(params);

                out.println("<br />");

                displayIWantToModule(params);

				out.println("</td>");
				out.println("</tr></table>");

				out.println("<br />");

			} else if (sETSOp.trim().equalsIgnoreCase("morealerts")) {

				displayAllAlerts(params);

				out.println("<br />");

			} else if (sETSOp.trim().equalsIgnoreCase("pastalerts")) {

				displayPastAlerts(params);

				out.println("<br />");
			} else if (sETSOp.trim().equalsIgnoreCase("manage_wspace")) {
				if (proj.getProjectType().equalsIgnoreCase(Defines.ETS_WORKSPACE_TYPE)) {
					ETSManageWorkspaceHandler workspace = new ETSManageWorkspaceHandler(params,es);
					workspace.handleRequest();
				} else {
					AICManageWorkspaceHandler handler = new AICManageWorkspaceHandler(params,es);
					handler.handleRequest();
				}
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}


	private static Vector getUpcomingAlerts(ETSParams params) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vEvents = new Vector();

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			boolean bAdmin = params.isSuperAdmin();
			boolean bExecutive = params.isExecutive();

			// changing the following query because of invitees_id becoming long varchar...
			if (bAdmin || bExecutive) {
				//sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'A' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				//sQuery.append("DATE(CURRENT TIMESTAMP) BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS) UNION ");
				//sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'A' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				//sQuery.append("DATE(START_TIME) > DATE(CURRENT TIMESTAMP) ORDER BY START_TIME ASC, SCHEDULE_DATE DESC with ur ");
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'A' AND PROJECT_ID = '" + proj.getProjectId() + "' ");
				sQuery.append("AND ((DATE(CURRENT TIMESTAMP) BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS)) OR DATE(START_TIME) > DATE(CURRENT TIMESTAMP)) ");
				sQuery.append("ORDER BY START_TIME ASC, SCHEDULE_DATE DESC with ur ");
				
			} else {
				//sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'A' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				//sQuery.append("DATE(CURRENT TIMESTAMP) BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS) UNION ");
				//sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'A' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				//sQuery.append("DATE(START_TIME) > DATE(CURRENT TIMESTAMP) ORDER BY START_TIME ASC, SCHEDULE_DATE DESC with ur ");
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'A' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "') ");
				sQuery.append("AND ((DATE(CURRENT TIMESTAMP) BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS)) OR DATE(START_TIME) > DATE(CURRENT TIMESTAMP)) ");
				sQuery.append("ORDER BY START_TIME ASC, SCHEDULE_DATE DESC with ur ");
				
			}


			logger.debug("ETSProposalHome::getUpcomingAlerts()::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sProjectID = proj.getProjectId();
				String sCalType = "A";
				Timestamp tScheduleDate = rs.getTimestamp("SCHEDULE_DATE");
				String sScheduleBy = ETSUtils.checkNull(rs.getString("SCHEDULED_BY"));
				Timestamp tStartTime = rs.getTimestamp("START_TIME");
				int iDuration = rs.getInt("DURATION");
				String sSubject = ETSUtils.checkNull(rs.getString("SUBJECT"));
				String sDescription = ETSUtils.checkNull(rs.getString("DESCRIPTION"));
				String sInvitees = ETSUtils.checkNull(rs.getString("INVITEES_ID"));
				String sCCList = ETSUtils.checkNull(rs.getString("CC_LIST"));
				String sWebFlag = ETSUtils.checkNull(rs.getString("WEB_CONF_FLAG"));
				String sCancelFlag = ETSUtils.checkNull(rs.getString("CANCEL_FLAG"));
				String sEmailFlag = ETSUtils.checkNull(rs.getString("EMAIL_FLAG"));
				String sIBMOnly = ETSUtils.checkNull(rs.getString("IBM_ONLY"));

				ETSCal cal = new ETSCal();
				cal.setSCalendarId(sCalID);
				cal.setSProjectId(sProjectID);
				cal.setSCalType(sCalType);
				cal.setSScheduleDate(tScheduleDate);
				cal.setSScheduleBy(sScheduleBy);
				cal.setSStartTime(tStartTime);
				cal.setIDuration(iDuration);
				cal.setSSubject(sSubject);
				cal.setSDescription(sDescription);
				cal.setSInviteesID(sInvitees);
				cal.setSCCList(sCCList);
				cal.setSWebFlag(sWebFlag);
				cal.setSCancelFlag(sCancelFlag);
				cal.setSEmailFlag(sEmailFlag);
				cal.setSIBMOnly(sIBMOnly);

				vEvents.addElement(cal);

			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vEvents;

	}

	private static Vector getPastAlerts(ETSParams params) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vEvents = new Vector();

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			boolean bAdmin = params.isSuperAdmin();
			boolean bExecutive = params.isExecutive();
			
			if (bAdmin || bExecutive) {
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'A' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				sQuery.append("DATE(START_TIME) < DATE(CURRENT TIMESTAMP) ORDER BY START_TIME DESC with ur ");
			} else {
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'A' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				sQuery.append("DATE(START_TIME) < DATE(CURRENT TIMESTAMP) ORDER BY START_TIME DESC with ur ");
			}


			logger.debug("ETSProposalHome::getUpcomingAlerts()::QUERY : " + sQuery.toString());
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sProjectID = proj.getProjectId();
				String sCalType = "A";
				Timestamp tScheduleDate = rs.getTimestamp("SCHEDULE_DATE");
				String sScheduleBy = ETSUtils.checkNull(rs.getString("SCHEDULED_BY"));
				Timestamp tStartTime = rs.getTimestamp("START_TIME");
				int iDuration = rs.getInt("DURATION");
				String sSubject = ETSUtils.checkNull(rs.getString("SUBJECT"));
				String sDescription = ETSUtils.checkNull(rs.getString("DESCRIPTION"));
				String sInvitees = ETSUtils.checkNull(rs.getString("INVITEES_ID"));
				String sCCList = ETSUtils.checkNull(rs.getString("CC_LIST"));
				String sWebFlag = ETSUtils.checkNull(rs.getString("WEB_CONF_FLAG"));
				String sCancelFlag = ETSUtils.checkNull(rs.getString("CANCEL_FLAG"));
				String sEmailFlag = ETSUtils.checkNull(rs.getString("EMAIL_FLAG"));
				String sIBMOnly = ETSUtils.checkNull(rs.getString("IBM_ONLY"));

				ETSCal cal = new ETSCal();
				cal.setSCalendarId(sCalID);
				cal.setSProjectId(sProjectID);
				cal.setSCalType(sCalType);
				cal.setSScheduleDate(tScheduleDate);
				cal.setSScheduleBy(sScheduleBy);
				cal.setSStartTime(tStartTime);
				cal.setIDuration(iDuration);
				cal.setSSubject(sSubject);
				cal.setSDescription(sDescription);
				cal.setSInviteesID(sInvitees);
				cal.setSCCList(sCCList);
				cal.setSWebFlag(sWebFlag);
				cal.setSCancelFlag(sCancelFlag);
				cal.setSEmailFlag(sEmailFlag);
				cal.setSIBMOnly(sIBMOnly);

				vEvents.addElement(cal);

			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vEvents;

	}





	public static void displaySiteHelp(ETSParams params) throws Exception {

		try {

			PrintWriter writer = params.getWriter();


	        writer.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
	        writer.println("<tr style=\"background-color: #eeeeee; color: #000000;\">");
	        writer.println("<td headers=\"\" >");

	        writer.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");

	        writer.println("<tr>");
	        writer.println("<td headers=\"\" >");
	        writer.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"150\">");
	        writer.println("<tr>");
	        writer.println("<td headers=\"\" width=\"150\" class=\"tblue\" height=\"18\"><b>&nbsp;Site support</b></td>");
	        writer.println("</tr>");
	        writer.println("</table>");
	        writer.println("</td>");
	        writer.println("</tr>");

	        writer.println("<tr><td headers=\"\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

	        writer.println("<tr>");
	        writer.println("<td headers=\"\" >");
	        writer.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"150\">");
	        writer.println("<tr>");
	        writer.println("<td headers=\"\" width=\"150\"><b>Help Desk</b></td>");
	        writer.println("</tr>");
	        writer.println("</table>");
	        writer.println("</td>");
	        writer.println("</tr>");

	        writer.println("<tr><td headers=\"\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

	        writer.println("<tr>");
	        writer.println("<td headers=\"\">");
	        writer.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"150\">");
	        writer.println("<tr>");
	        writer.println("<td headers=\"\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" border=\"0\" alt=\"phone\" /></td>");
	        writer.println("<td headers=\"\" width=\"134\">US/Canada: <br />1 888 220 3343</td>");
	        writer.println("</tr>");
	        writer.println("</table>");
	        writer.println("</td>");


	        writer.println("<tr><td headers=\"\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

	        writer.println("<tr>");
	        writer.println("<td headers=\"\">");
	        writer.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"150\">");
	        writer.println("<tr>");
	        writer.println("<td headers=\"\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" border=\"0\" alt=\"phone\" /></td>");
	        writer.println("<td headers=\"\" width=\"134\">International: <br />1 802 769 3353</td>");
	        writer.println("</tr>");
	        writer.println("</table>");
	        writer.println("</td>");
	        writer.println("</tr>");

	        writer.println("<tr><td headers=\"\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

	        writer.println("<tr>");
	        writer.println("<td headers=\"\">");
	        writer.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"150\">");
	        writer.println("<tr>");
	        writer.println("<td headers=\"\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" border=\"0\" alt=\"email\" /></td>");
	        writer.println("<td headers=\"\" width=\"134\"><b>E-mail:</b><br /><a href=\"mailto:econnect@us.ibm.com\" class=\"fbox\">eConnect@us.ibm.com</a></td>");
	        writer.println("</tr>");
	        writer.println("</table>");
	        writer.println("</td>");
	        writer.println("</tr>");

			writer.println("<tr><td headers=\"\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

	        writer.println("</table>");

			writer.println("</td>");
	        writer.println("</tr>");
	        writer.println("</table>");



		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

    private static void displayRecentDocuments(ETSParams params) throws SQLException, Exception {


        try {
        	
        	boolean bDisplayed = false;

            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

            Connection con = params.getConnection();
            PrintWriter out = params.getWriter();
            EdgeAccessCntrl es = params.getEdgeAccessCntrl();
            ETSProj proj = params.getETSProj();

            ETSDatabaseManager dbManager = new ETSDatabaseManager();

            //Vector vDocs = ETSDatabaseManager.getLatestDocs(con,proj.getProjectId(),5);
            
			boolean internal = false;
			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				internal = true;
			}
			            
			Vector vDocs = new Vector();

			boolean showExp = false;
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN)){
				showExp = true;	
			}
			
			boolean bExpDoc = false;
			boolean ibmconfFlag = false;
			boolean ibmonlyFlag = false;
			boolean resFlag = false;
			
			DocumentDAO udDAO = new DocumentDAO();
			udDAO.setConnection(con);
			if (internal) {
				if (showExp) {
					vDocs = udDAO.getLatestDocs(proj.getProjectId(), 5);
				} else {
					vDocs = udDAO.getLatestDocs(proj.getProjectId(), es.gIR_USERN,params.isExecutive(),5);
				}
			} else {
				/*if (showExp) {
					vDocs = ETSDatabaseManager.getLatestDocs(con, proj.getProjectId(), 5, internal);
				} else {*/
					vDocs = udDAO.getLatestDocs(proj.getProjectId(), es.gIR_USERN,5, internal,params.isExecutive());
				//}
			}

			boolean bIBMConfFlag = false;
            if (vDocs != null) {

                for (int i = 0; i < vDocs.size(); i++) {
                	
                	bDisplayed = true;
                	
                	if (i == 0) {
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" class=\"tblue\">&nbsp;Recent documents</td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<th id=\"head_name\" colspan=\"2\" align=\"left\">");
						out.println("<span class=\"small\"><b>Name</b></span>");
						out.println("</th>");
						out.println("<th id=\"head_modified\" align=\"left\" >");
						out.println("<span class=\"small\"><b>Modified</b></span>");
						out.println("</th>");
						out.println("<th id=\"head_type\" align=\"left\" >");
						out.println("<span class=\"small\">&nbsp;</span>");
						out.println("</th>");
						out.println("<th id=\"head_author\" align=\"left\" >");
						out.println("<span class=\"small\"><b>Author</b></span>");
						out.println("</th>");
						out.println("</tr>");
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"7\" alt=\"\" /></td>");
						out.println("</tr>");
                		
                	}

                    ETSDoc doc = (ETSDoc) vDocs.elementAt(i);

                    java.util.Date date = new java.util.Date(doc.getUpdateDate());
                    String sDocDate = df.format(date);
					String exStr = "";
					String ibmStr =  "";
					String resStr =  "";
					String IBMConfStr = "";
					if(doc.hasExpired()){
						exStr = "<span class=\"small\"><span class=\"ast\"><b>&#8224;</b></span></span>";
						bExpDoc = true;
					}
					if(doc.getIbmOnly() == Defines.ETS_IBM_CONF){
						ibmconfFlag = true;
						ibmStr = "<span class=\"ast\">**</span>"; 
					}
					else if(doc.getIbmOnly() == Defines.ETS_IBM_ONLY){
						ibmonlyFlag = true;
						ibmStr = "<span class=\"ast\">*</span>";
					}

					if(doc.IsDPrivate()){
						resFlag = true;
						resStr = "<span class=\"ast\">#</span>";
					}
					if(DocConstants.IND_YES.equals(doc.getIBMConfidential())) {
						bIBMConfFlag = true;
						IBMConfStr = "<span class=\"ast\">!</span>";
					}
					
                    if ((i % 2) == 0) {
                        out.println("<tr style=\"background-color: #eeeeee\">");
                    } else {
                        out.println("<tr >");
                    }
                    
                    String encFileName = URLEncoder.encode(doc.getFileName());
                    out.println("<td headers=\"\" headers=\"head_name\" width=\"16\" height=\"21\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"\" border=\"0\" /></td>");
					out.println("<td headers=\"\" headers=\"head_name\" width=\"200\" height=\"21\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "displayDocumentDetails.wss?proj=" + proj.getProjectId() + "&hitrequest=true&docid=" + doc.getId() + "&tc=" + ETSUtils.getTopCatId(con,proj.getProjectId(),Defines.DOCUMENTS_VT) + "&cc=" + doc.getCatId() + "&linkid=" + params.linkId + "\" class=\"fbox\">" + doc.getName() + ibmStr + exStr + resStr + IBMConfStr + "</a></td>");
                    out.println("<td headers=\"\" headers=\"head_name\" width=\"70\" height=\"21\" align=\"left\" valign=\"top\">" + sDocDate + "</td>");
                    out.println("<td headers=\"\" headers=\"head_name\" width=\"35\" align=\"left\" height=\"21\" valign=\"top\">&nbsp;</td>");
                    out.println("<td headers=\"\" headers=\"head_name\" align=\"left\" valign=\"top\"><span class=\"small\"><span style=\"color:#666666\">" + ETSUtils.getUsersName(con,doc.getUserId()) + "</span></span></td>");
                    out.println("</tr>");

                }

            }

			if (bDisplayed) {
				out.println("<tr >");
				out.println("<td headers=\"\" headers=\"head_name\" colspan=\"5\" align=\"left\" valign=\"top\">To view complete list of recent documents click <a href=\"" + Defines.SERVLET_PATH + "displayDocumentList.wss?docAction=showalldocs&proj=" + proj.getProjectId() + "&tc=" + ETSUtils.getTopCatId(con,proj.getProjectId(),Defines.DOCUMENTS_VT) + "&cc=" + ETSUtils.getTopCatId(con,proj.getProjectId(),Defines.DOCUMENTS_VT) + "&linkid=" + params.getLinkId() + "&sortBy="+Defines.SORT_BY_CUSTOM_STR+"\">here</a>.</td>");
				out.println("</tr>");
				if (ibmonlyFlag){
					out.println("<tr><td headers=\"\" colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
					out.println("<tr><td headers=\"\" colspan=\"5\" class=\"small\" valign=\"bottom\"><span class=\"ast\">*</span>Denotes IBM Only folder/document</td></tr>");
				}
				if (ibmconfFlag){
					out.println("<tr><td headers=\"\" colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
					out.println("<tr><td headers=\"\" colspan=\"5\" class=\"small\" valign=\"bottom\"><span class=\"ast\">**</span>Denotes permanent IBM Only folder/document</td></tr>");
				}
				if (bExpDoc){
					out.println("<tr><td headers=\"\" colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
					out.println("<tr><td headers=\"\" colspan=\"5\" class=\"small\" valign=\"bottom\"><span class=\"ast\"><b>&#8224;</b></span>Denotes expired document</td></tr>");
				}				
				if (resFlag){
					out.println("<tr><td headers=\"\" colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
					out.println("<tr><td headers=\"\" colspan=\"5\" class=\"small\" valign=\"bottom\"><span class=\"ast\">#</span>Access to this document is restricted to selected team members</td></tr>");
				}	
				if (bIBMConfFlag){
					out.println("<tr><td headers=\"\" colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
					out.println("<tr><td headers=\"\" colspan=\"5\" class=\"small\" valign=\"bottom\"><span class=\"ast\">!</span>Denotes IBM Confidential document</td></tr>");
				}				
	            out.println("</table>");
			}

        } catch (Exception e) {
            throw e;
        } finally {
        }

    }

}
