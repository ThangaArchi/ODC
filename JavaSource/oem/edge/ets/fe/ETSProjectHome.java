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
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.aic.AICManageWorkspaceHandler;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocReaderDAO;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.pmo.ETSPMODao;
import oem.edge.ets.fe.pmo.ETSPMOffice;
import oem.edge.ets.fe.pmo.ETSPMOfficeDisplay;
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
public class ETSProjectHome {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.37.1.22";
	
	private static Log logger = EtsLogger.getLogger(ETSProjectHome.class);



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

			logger.debug("ETSConnectServlet::getContactURL::Query : " + sQuery.toString());

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
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Message\" border=\"0\" /></td>");
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
                
				String sMess = "";
				
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_EXECUTIVE)) {
					sMess = prop.getVisitorPrevMessagesText();
				} else {
					sMess = prop.getPrevMessagesText();
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

			logger.debug("ETSProjectHome::getUpcomingAlerts()::QUERY : " + sQuery.toString());

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
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Message\" border=\"0\" /></td>");
					out.println("<td headers=\"\" width=\"427\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">" + calEntry.getSSubject() + "</a>");
					out.println("<br />");
					out.println("<span class=\"small\"><b>" + sDate + "</b>&nbsp;&nbsp;&nbsp;<span style=\"color:#666666\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</span></span>");
					out.println("</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					// the editing of the message can be done by the creator of the message and the super admin... 
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
                
				String dfltMsg = prop.getMessagesText();
				
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_EXECUTIVE)) {
					dfltMsg = prop.getVisitorMessagesText();
				} else {                
					dfltMsg = prop.getMessagesText();
				}
                
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\">"+dfltMsg+"</td>");
//				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><b>There are no alerts present at this time.</b></td>");
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
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastalerts&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"View past message\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastalerts&linkid=" + params.getLinkId() + "\"  class=\"fbox\">View past messages</a></td>");
				out.println("</tr>");
				out.println("</table>");
				
			} else {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastalerts&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"View past message\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastalerts&linkid=" + params.getLinkId() + "\"  class=\"fbox\">View past messages</a></td>");
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

	private static void displayQuickLinks (ETSParams params) throws Exception {
	// display the list of quicklinks

	}
	public static void displayIWantToModule(ETSParams params) throws Exception {

		PrintWriter out = params.getWriter();
		ETSQuickLinks etsQuickLinks = new ETSQuickLinks();
		etsQuickLinks.setUserId(params.getEdgeAccessCntrl().gIR_USERN);
		out.println(etsQuickLinks.showQuickLinks(params));

	}


	private static void displayAllEvents(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			UnbrandedProperties prop = params.getUnbrandedProperties();

			Vector vAlerts = ETSCalendar.getUpcomingEvents(params);

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><span class=\"subtitle\">&nbsp;Upcoming events</span></td>");
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
					String sTmpEndDate = calEntry.getSRepeatEnd().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					String sHour = sTempDate.substring(11, 13);
					String sMin = sTempDate.substring(14, 16);
					String sAMPM = "AM";

					if (Integer.parseInt(sHour) > 12) {
						sHour = String.valueOf(Integer.parseInt(sHour) - 12);
						sAMPM = "PM";
					}
					
					String sEndDate = sTmpEndDate.substring(5, 7) + "/" + sTmpEndDate.substring(8, 10) + "/" + sTmpEndDate.substring(0, 4);
					String sEndTime = sTmpEndDate.substring(11, 16);

					String sEndHour = sTmpEndDate.substring(11, 13);
					String sEndMin = sTmpEndDate.substring(14, 16);
					String sEndAMPM = "AM";

					if (Integer.parseInt(sEndHour) == 0) {
						sEndHour = "12";
						sEndAMPM = "AM";
					} else if (Integer.parseInt(sEndHour) == 12) {
						sEndHour = String.valueOf(Integer.parseInt(sEndHour));
						if (Integer.parseInt(sEndHour) < 10) sEndHour = "0" + sEndHour;
						sEndAMPM = "PM";
					} else if (Integer.parseInt(sEndHour) > 12) {
						sEndHour = String.valueOf(Integer.parseInt(sEndHour) - 12);
						if (Integer.parseInt(sEndHour) < 10) sEndHour = "0" + sEndHour;
						sEndAMPM = "PM";
					}
					
					int iDuration = calEntry.getIDuration();

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Event\" border=\"0\" /></td>");
					out.println("<td headers=\"\" width=\"427\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">" + calEntry.getSSubject() + "</a>");
					out.println("<br />");
					out.println("<span style=\"color:#666666\"><b>" + sDate + "</b>&nbsp;&nbsp;<span class=\"small\">" + sHour + ":" + sMin +  sAMPM.toLowerCase() + "&nbsp;Eastern US&nbsp;to&nbsp;" + sEndHour + ":" + sEndMin +  sEndAMPM.toLowerCase() + "&nbsp;Eastern US</span></span>");
					out.println("<br />");
					out.println("This event repeats for " + String.valueOf(iDuration) + " days");
					out.println("</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					// the edit and cancel should be displayed to the user who created it and super admin
					// changed for 4.4.1
					if (calEntry.getSScheduleBy().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN)) {

						out.println("<tr>");
						out.println("<td headers=\"\" width=\"16\" align=\"center\">&nbsp;</td>");
						out.println("<td headers=\"\"  width=\"427\">[&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Edit this event</a>&nbsp;]&nbsp;&nbsp;&nbsp;[&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Cancel this event</a>&nbsp;]</td>");
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

				String sMess = prop.getEventText();
                
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_EXECUTIVE)) {
					sMess = prop.getVisitorEventText();
				} else {                
					sMess = prop.getEventText();
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

			// the add an event should not be displayed to executive and visitor.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MEMBER)) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Add an event\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" width=\"140\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\">Add an event</a></td>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastevents&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"View past events\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastevents&linkid=" + params.getLinkId() + "\"  class=\"fbox\">View past events</a></td>");
				out.println("</tr>");
				out.println("</table>");
			} else {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastevents&linkid=" + params.getLinkId() + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"View past events\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + params.getTopCat() + "&proj=" + proj.getProjectId() + "&etsop=pastevents&linkid=" + params.getLinkId() + "\"  class=\"fbox\">View past events</a></td>");
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
			out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" class=\"tblue\">&nbsp;Messages</td>");
			out.println("</tr>");

			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);


					//out.println("<tr>");
					//out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					//out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Message\" border=\"0\" /></td>");
					out.println("<td headers=\"\" width=\"427\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=A&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">" + calEntry.getSSubject() + "</a>");
					out.println("<br />");
					out.println("<span class=\"small\"><b>" + sDate + "</b>&nbsp;&nbsp;<span style=\"color:#666666\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</span></span>");
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

					//out.println("<tr>");
					//out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					//out.println("</tr>");

					if (i >= 1) {
						break;
					}

				}

			} else {

				String dfltMsg = prop.getMessagesText();
                
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_EXECUTIVE)) {
					dfltMsg = prop.getVisitorMessagesText();
				} else {
					dfltMsg = prop.getMessagesText();
				}

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\">"+dfltMsg+"</td>");
//				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><b>There are no alerts present at this time.</b></td>");
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
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Post a message\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" width=\"127\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=A','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Post a message</a></td>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=morealerts&linkid=" + sLinkId + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"More messages and options\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" width=\"284\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=morealerts&linkid=" + sLinkId + "\" class=\"fbox\">More messages and options</a></td>");
				out.println("</tr>");
				out.println("</table>");
				
			} else {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=morealerts&linkid=" + sLinkId + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"More messages and options\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" width=\"427\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=morealerts&linkid=" + sLinkId + "\" class=\"fbox\">More messages and options</a></td>");
				out.println("</tr>");
				out.println("</table>");

			}

			out.println("</td></tr>");
			out.println("</table>");

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	public static void displayPastEvents(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			UnbrandedProperties prop = params.getUnbrandedProperties();

			Vector vPastEvents = getPastEvents(params);

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><span class=\"subtitle\">&nbsp;Past events</span></td>");
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


			if (vPastEvents != null && vPastEvents.size() > 0) {

				for (int i = 0; i < vPastEvents.size(); i++) {

					ETSCal calEntry = (ETSCal) vPastEvents.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					int iDuration = calEntry.getIDuration();

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Message\" border=\"0\" /></td>");
					out.println("<td headers=\"\" width=\"427\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">" + calEntry.getSSubject() + "</a>");
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
				
				String sMess = prop.getPrevEventText();
                
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_EXECUTIVE)) {
					sMess = prop.getVisitorPrevEventText();
				} else {
					sMess = prop.getPrevEventText();
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

			// the add an event should not be displayed to executive and visitor.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MEMBER)) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Add an event\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\">Add an event</a></td>");
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

	/**
	 * @param params
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayProjStatus(ETSParams params) throws SQLException, Exception {
		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			ETSProj proj = params.getETSProj();
			
			String strProjectId = proj.getProjectId();
			
			DocReaderDAO udReader = new DocReaderDAO();
			udReader.setConnection(con);
			String strSourceId = "ASIC";
			String strDestId = "MAIN";
			ETSProjectStatus udProjStatus = udReader.getProjectStatus(strProjectId, strSourceId, strDestId);
			
			if (udProjStatus != null) {

			String strLink = "<a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss?projid=" + proj.getProjectId() + "&projstatus=true&source=" + udProjStatus.getSourceId() + "&dest=" + udProjStatus.getDestId() + "\" target=\"new\">Project details</a>";

			    String strDate = StringUtil.formatDate(udProjStatus.getLastTimestamp());
			

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"6\" height=\"18\" class=\"tdblue\">&nbsp;Project status</td>");
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"6\">The project status document is available (modified " + strDate + ").&nbsp;" + strLink + "</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("</table>");
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
		}
	}

	private static void displayProjPlan(ETSParams params, boolean bPMO) throws SQLException, Exception {


		StringBuffer sQuery = new StringBuffer("");
		Hashtable hSSM = new Hashtable();
		SimpleDateFormat dtformat = new SimpleDateFormat("MM/dd/yyyy");
		
		boolean bDisplayed = false;

		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();

			Hashtable hDocs = new Hashtable();
			long lPostDate = 0;
			String sPostDate = "";
			String sPostBy = "";

			boolean bAdmin = false;

			// display the upload only if primary contact or workspace owner or workspace manager or super workspace admin
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MANAGER) || (ETSDatabaseManager.getPrimaryContact(proj.getProjectId(),con).getUserId().trim().equalsIgnoreCase(es.gIR_USERN) && !proj.getProject_status().equalsIgnoreCase("A"))) {
				bAdmin = true;
			}


			Vector vDocs = ETSDatabaseManager.getDocs(con,Defines.PROJECT_PLAN_CATEGORY,proj.getProjectId(),Defines.PROJECT_PLAN);

			if (vDocs != null && vDocs.size() != 0) {

				bDisplayed = true;
				
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"6\" height=\"18\" class=\"tblue\">&nbsp;Project plan</td>");
				out.println("</tr>");
				
				out.println("<tr valign=\"top\">");
				out.println("<th id=\"head_name\" align=\"left\">");
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
				out.println("<th id=\"head_download\" colspan=\"2\" align=\"left\" >");
				//out.println("<span class=\"small\"><b>Download</b></span>");
				out.println("<span class=\"small\">&nbsp;</span>");
				out.println("</th>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"6\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");

				ETSDoc doc = (ETSDoc) vDocs.elementAt(0);
				hDocs.put(doc.getFileType(),doc);
				java.util.Date date = new java.util.Date(doc.getUploadDate());
				sPostDate = dtformat.format(date);
				sPostBy = doc.getUserId();

				String encFileName = URLEncoder.encode(doc.getFileName());
				out.println("<tr>");
				
				// TO PRINT DOC NAME INSTEAD OF JUST Project Plan
				String strPPlan = "Project Plan";
				if (doc != null && doc.getName() != null ) {
				    strPPlan = doc.getName();
				}
				out.println("<td headers=\"\" headers=\"head_name\" width=\"100\" height=\"21\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + encFileName +"?projid=" + proj.getProjectId() + "&docid=" + doc.getId() + "&linkid=120000\" class=\"fbox\" target=\"new\">"+strPPlan+"</a></td>");
				out.println("<td headers=\"\" headers=\"head_modified\" width=\"70\" height=\"21\" align=\"left\" valign=\"top\">" + sPostDate + "</td>");
				out.println("<td headers=\"\" headers=\"head_type\" width=\"35\" align=\"left\" height=\"21\" valign=\"top\">&nbsp;</td>");
				out.println("<td headers=\"\" headers=\"head_author\" width=\"150\" align=\"left\" valign=\"top\"><span class=\"small\"><span style=\"color:#666666\">" + ETSUtils.getUsersName(con,sPostBy) + "</span></span></td>");
				out.println("<td headers=\"\" headers=\"head_download\" width=\"16\" align=\"center\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + encFileName +"?projid=" + proj.getProjectId() + "&docid=" + doc.getId() + "&linkid=120000&download=Y\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "dn.gif\" width=\"16\" height=\"16\" alt=\"Download project plan\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" headers=\"head_download\" width=\"72\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + encFileName +"?projid=" + proj.getProjectId() + "&docid=" + doc.getId() + "&linkid=120000&download=Y\" class=\"fbox\">Download</a></td>");
				out.println("</tr>");
				out.println("</table>");

			} else {
            	
				if (bAdmin || !bPMO) {
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
	
					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"6\" height=\"18\" class=\"tblue\">&nbsp;Project plan</td>");
					out.println("</tr>");
	            	
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\"  colspan=\"6\" align=\"left\">Project plan document currently not available.</td>");
					out.println("</tr>");
					out.println("</table>");
				}
			}

            
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_MANAGER) || (ETSDatabaseManager.getPrimaryContact(proj.getProjectId(),con).getUserId().trim().equalsIgnoreCase(es.gIR_USERN) && !proj.getProject_status().equalsIgnoreCase("A"))) {
				
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
				
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"6\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\"><td headers=\"\" colspan=\"6\">");
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\"><tr>");
				if (bDisplayed) {
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=addprojectplan&action=addprojectplan&cc="+params.getTopCat()+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Upload new plan\" border=\"0\" /></a></td>");
					out.println("<td headers=\"\" width=\"160\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=addprojectplan&action=addprojectplan&cc="+params.getTopCat()+"\" class=\"fbox\">Upload new project plan</a></td>");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=addprojectplan&action=addprojectplan&cc="+params.getTopCat()+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Delete project plan\" border=\"0\" /></a></td>");
					out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=delprojectplan&action=delprojectplan&cc="+params.getTopCat()+"\" class=\"fbox\">Delete project plan</a></td>");
				} else {
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=addprojectplan&action=addprojectplan&cc="+params.getTopCat()+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Upload new plan\" border=\"0\" /></a></td>");
					out.println("<td headers=\"\" align=\"left\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=addprojectplan&action=addprojectplan&cc="+params.getTopCat()+"\" class=\"fbox\">Upload new project plan</a></td>");
				}
				out.println("</tr></table>");
				out.println("</td>");
				out.println("</tr>");

				out.println("</table>");                
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

		private static boolean displayPMOPlan(ETSParams params) throws SQLException, Exception {
	
	
			StringBuffer sQuery = new StringBuffer("");
			Hashtable hSSM = new Hashtable();
			SimpleDateFormat dtformat = new SimpleDateFormat("MM/dd/yyyy");
			
			boolean bDisplayed = false;
	
			try {
	
				Connection con = params.getConnection();
				PrintWriter out = params.getWriter();
				ETSProj proj = params.getETSProj();
				EdgeAccessCntrl es = params.getEdgeAccessCntrl();
	
				// check to see if the project has a pm office related to it.
				// if yes, then get the project from pm office and display the status band with
				// details link.
				
				String sPMOProjectID = proj.getPmo_project_id();
				if (sPMOProjectID != null && !sPMOProjectID.trim().equals("")) {
					
					bDisplayed = true;
					 					
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
					
					out.println("<tr>");
					out.println("<td headers=\"\"  height=\"18\" class=\"tdblue\">&nbsp;Project status</td>");
					out.println("</tr>");
					
					// there exists a pm office link to this ets project...
					// get the pmo project details and display the quick status...
					ETSPMODao pmoDAO = new ETSPMODao();
					ETSPMOffice pmoProject = pmoDAO.getPMOfficeProjectDetails(con,sPMOProjectID);
					
					String sBaseFinish = "N/A";
							
					if (pmoProject.getBaseFinish() == null || pmoProject.getBaseFinish().toString().trim().equals("")) {
						sBaseFinish = "N/A";
					} else {
						sBaseFinish = ETSUtils.formatDate(pmoProject.getBaseFinish());
					}
							
					String sFinishType = pmoProject.getCurrFinishType();
					if (sFinishType == null || sFinishType.trim().equals("")) {
						sFinishType = "";
					} else {
						sFinishType = sFinishType.trim();
					}
							
					String sCurrDate = "N/A";
					if (pmoProject.getCurrFinish() == null || pmoProject.getCurrFinish().toString().trim().equals("")) {
						sCurrDate = "N/A";
					} else {
						sCurrDate = ETSUtils.formatDate(pmoProject.getCurrFinish());
					}
					
					String sRPMCode = ETSUtils.getsRPMProjectCode(con,sPMOProjectID);
					
					if (!sRPMCode.equalsIgnoreCase("")) {
						if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
							out.println("<tr>");
							out.println("<td headers=\"\" ><b>Project code:</b> " + ETSUtils.getsRPMProjectCode(con,sPMOProjectID) + "</td>");
							out.println("</tr>");
						}					
					}
					
					out.println("<tr>");
					if (sFinishType.trim().equals("")) {
						out.println("<td headers=\"\" >The completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("Plan")) {
						out.println("<td headers=\"\" >The <b>planned</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("Expected")) {
						out.println("<td headers=\"\" >The <b>expected</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("Schedule")) {
						out.println("<td headers=\"\" >The <b>scheduled</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("Forecast")) {
						out.println("<td headers=\"\" >The <b>forecasted</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("Actual")) {
						out.println("<td headers=\"\" >The <b>actual</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("User Actual")) {
						out.println("<td headers=\"\" >The <b>user actual</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("User ForeCast")) {
						out.println("<td headers=\"\" >The <b>user forcasted</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("User Planned")) {
						out.println("<td headers=\"\" >The <b>user planned</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("Incompleted")) {
						out.println("<td headers=\"\" >The <b>incompletd</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else if (sFinishType.trim().equalsIgnoreCase("Outlook")) {
						out.println("<td headers=\"\" >The <b>outlook</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					} else {
						out.println("<td headers=\"\" >The <b>" + sFinishType.trim() + "</b> completion date for this project is <b>" + sCurrDate + "</b>&nbsp;&nbsp;&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=pmo\" class=\"fbox\">More details</a></td>");
					}
					
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td headers=\"\" >&nbsp;</td>");
					out.println("</tr>");
					
					out.println("</table>");
					
				}
	     	
				return bDisplayed;       
	
	
			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			} finally {
			}
	
		}




	public static void displaySiteHelp(ETSParams params) throws Exception {

		try {
			PrintWriter writer = params.getWriter();
			writer.println("<table summary=\"Right navigation for need more help\" cellspacing=\"0\" cellpadding=\"0\" width=\"150\" border=\"0\">");
			writer.println("<tr><td colspan=\"2\" class=\"tblue\" width=\"150\" height=\"18\" align=\"left\">&nbsp;&nbsp;Web site support</td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer.println("<td headers=\"\" colspan=\"2\" valign=\"top\"><img height=\"4\" width=\"1\" alt=\"\" src=\""+Defines.V11_IMAGE_ROOT+" c.gif\" /></td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer.println("<td headers=\"\" valign=\"top\" width=\"16\"><a href=\"/technologyconnect/cchelp.html\" onclick=\"window.open('/technologyconnect/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\"  onkeypress=\"window.open('/technologyconnect/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\" target=\"new\" class=\"fbox\"><img src=\""+Defines.ICON_ROOT+"popup.gif\" height=\"16\" width=\"16\" border=\"0\" alt=\"\" /></a></td>");
			writer.println("<td headers=\"\" width=\"134\"><a href=\"/technologyconnect/cchelp.html\" onclick=\"window.open('/technologyconnect/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\" onkeypress=\"window.open('/technologyconnect/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\" target=\"new\" class=\"fbox\">Contact help desk</a></td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer.println("<td headers=\"\" valign=\"top\" width=\"16\"><a href=\"" + Global.getUrl("EdCQMDServlet.wss?linkid=251000") + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" border=\"0\" alt=\"\" /></a></td>");
            writer.println("<td headers=\"\" width=\"134\"><a href=\"" + Global.getUrl("EdCQMDServlet.wss?linkid=" + params.getLinkId()) + "\" class=\"fbox\">Report a problem online</a></td>");
			writer.println("</tr>");
			writer.println("</table>");
		} catch (Exception eX) {
			throw eX;
		}

	}

	public static void displayProjectHome(ETSParams params) throws SQLException, Exception {

		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			HttpServletRequest request = params.getRequest();
			UnbrandedProperties prop = params.getUnbrandedProperties();

			String sETSOp = request.getParameter("etsop");

			if (sETSOp == null || sETSOp.trim().equals("")) {
				sETSOp = "";
			} else {
				sETSOp = sETSOp.trim();
			}

			if (sETSOp.trim().equals("")) {
				// to print the bookmark link
				//out.println(ETSUtils.getBookMarkString("","",true));
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) 
						|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER)
						|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKFLOW_ADMIN) ) {
					out.println(ETSUtils.getBookMarkString("<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&etsop=manage_wspace\" >Manage this workspace</a>","",true));
				} else {
					out.println(ETSUtils.getBookMarkString("","",true));
				}

				boolean bDisplayed = false;
				if (proj.getProjectType().equalsIgnoreCase(Defines.ETS_WORKSPACE_TYPE)) {
					bDisplayed = displayPMOPlan(params); // pm office status band
					out.println("<br />");
				}

				displayProjStatus(params); // project status
				if (!proj.isITAR() && !proj.getProjectType().equalsIgnoreCase(Defines.AIC_WORKSPACE_TYPE)) {
					displayProjPlan(params, bDisplayed); // project plan
					out.println("<br />");
				}

				displayAlerts(params); // project alerts
				out.println("<br />");

				displayRecentDocuments(params); //recent documents
				out.println("<br />");

				displayIWantToModule(params); // quick links
				out.println("<br />");

				out.println("<br />");
			} else if (sETSOp.trim().equalsIgnoreCase("moreevents")) {

				displayAllEvents(params);

				out.println("<br />");
			} else if (sETSOp.trim().equalsIgnoreCase("morealerts")) {

				displayAllAlerts(params);

				out.println("<br />");
			} else if (sETSOp.trim().equalsIgnoreCase("caldetail")) {

				ETSMeetings.displayTodaysMeetings(params);

				out.println("<br />");

				ETSMeetings.displayTodaysEvents(params);

				out.println("<br />");
			} else if (sETSOp.trim().equalsIgnoreCase("newmeeting") || sETSOp.trim().equalsIgnoreCase("editmeeting")) {
				ETSMeetings.editMeetingDetails(params);
			} else if (sETSOp.trim().equalsIgnoreCase("insertmeeting") || sETSOp.trim().equalsIgnoreCase("updatemeeting")) {
				// update the meeting details and display the meeting details screen.

				ETSMeetings.insertUpdateMeeting(params);

			} else if (sETSOp.trim().equalsIgnoreCase("viewmeeting")) {
				ETSMeetings.displayMeetingDetails(params);

			} else if (sETSOp.trim().equalsIgnoreCase("pastalerts")) {
				displayPastAlerts(params);

			} else if (sETSOp.trim().equalsIgnoreCase("pastevents")) {
				displayPastEvents(params);

			} else if (sETSOp.trim().equalsIgnoreCase("addprojectplan")) {
				ETSDocumentManager dm = new ETSDocumentManager(params);
				dm.ETSDocumentHandler();
			} else if (sETSOp.trim().equalsIgnoreCase("addmeetingdoc")) {
				ETSDocumentManager dm = new ETSDocumentManager(params);
				dm.ETSDocumentHandler();
			} else if (sETSOp.trim().equalsIgnoreCase("pmo")) {
				ETSPMOfficeDisplay pmDisplay = new ETSPMOfficeDisplay();
				pmDisplay.handleRequest(params);
			} else if (sETSOp.trim().equalsIgnoreCase("delprojectplan")) {
				displayProjectPlanDelete(params);
			} else if (sETSOp.trim().equalsIgnoreCase("delplan")) {
				boolean success = ETSDatabaseManager.deleteProjectPlan(proj.getProjectId(),es.gIR_USERN,con);
				
				// after deleting print the default page...
				// to print the bookmark link

				// to print the bookmark link
				//out.println(ETSUtils.getBookMarkString("","",true));
				if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.ETS_ADMIN) 
						|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKFLOW_ADMIN) 
						|| ETSUtils.checkUserRole(es,proj.getProjectId(),con).equals(Defines.WORKSPACE_OWNER)) {
					out.println(ETSUtils.getBookMarkString("<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&etsop=manage_wspace\" >Manage this workspace</a>","",true));
				} else {
					out.println(ETSUtils.getBookMarkString("","",true));
				}

				boolean bDisplayed = displayPMOPlan(params); // pm office status band
				out.println("<br />");
				
				displayProjPlan(params,bDisplayed); // project plan
				out.println("<br />");

				displayAlerts(params); // project alerts
				out.println("<br />");

				displayRecentDocuments(params); //recent documents
				out.println("<br />");

				displayIWantToModule(params); // quick links
				out.println("<br />");

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


	/**
	 * @param params
	 */
	private static void displayProjectPlanDelete(ETSParams params) throws Exception {

		Connection con = params.getConnection();
		PrintWriter out = params.getWriter();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();
		ETSProj proj = params.getETSProj();
		
		out.println("<form name=\"DelProjPlan\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
		
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"etsop\" value=\"delplan\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  align=\"left\" height=\"18\" class=\"subtitle\">Delete project plan confirmation</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\"  align=\"left\" height=\"18\" >&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\"  align=\"left\" height=\"18\" ><b>Please click on \"Submit\" to delete the project plan.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<!-- Gray dotted line -->");
		out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<!-- End Gray dotted line -->");

		out.println("<br />");
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\"  align=\"left\"><b><a class=\"fbox\" href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" >Cancel</a></b></td></tr></table></td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("</form>");

	}



	private static void displayRecentDocuments(ETSParams params) throws SQLException, Exception {

		try {
			
			boolean bDisplayed = false;

			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();

			boolean internal = false;
			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				internal = true;
			}

			Vector vDocs = new Vector();

//			if (internal) {
//				vDocs = ETSDatabaseManager.getLatestDocs(con, proj.getProjectId(), 5);
//			} else {
//				vDocs = ETSDatabaseManager.getLatestDocs(con, proj.getProjectId(), 5, internal);
//			}

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
				if (showExp) {  //ws owner or superadmin
					vDocs = udDAO.getLatestDocs(proj.getProjectId(), 5);
				} else {  //all internal members
					vDocs = udDAO.getLatestDocs(proj.getProjectId(), es.gIR_USERN,params.isExecutive(),5);
				}
			} else {  //all external members
				/*if (showExp) {  //should never have to use this
					vDocs = ETSDatabaseManager.getLatestDocs(con, proj.getProjectId(), 5, internal);
				} else {*/
					vDocs = udDAO.getLatestDocs(proj.getProjectId(), es.gIR_USERN,5, internal,params.isExecutive());
				//}
			}

			//Vector vDocs = dbManager.getLatestDocs(con,proj.getProjectId(),5);

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
					out.println("<td headers=\"\" headers=\"head_name\" align=\"left\" valign=\"top\"><span class=\"small\"><span style=\"color:#666666\">" + ETSUtils.getUsersName(con, doc.getUserId()) + "</span></span></td>");
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

			logger.debug("ETSProjectHome::getUpcomingAlerts()::QUERY : " + sQuery.toString());

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

	private static Vector getPastEvents(ETSParams params) throws SQLException, Exception {

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
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				sQuery.append("DATE(START_TIME) < DATE(CURRENT TIMESTAMP) ORDER BY START_TIME DESC with ur ");
			} else {
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				sQuery.append("DATE(START_TIME) < DATE(CURRENT TIMESTAMP) ORDER BY START_TIME DESC with ur ");
			}


			logger.debug("ETSProjectHome::getUpcomingAlerts()::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sProjectID = proj.getProjectId();
				String sCalType = "E";
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
				Timestamp sEndTime = rs.getTimestamp("REPEAT_END");

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
				cal.setSRepeatEnd(sEndTime);

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
	
}

