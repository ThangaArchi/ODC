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


package oem.edge.ets.fe;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DatesArithmatic;
import oem.edge.common.Global;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.DocumentsHelper;
import oem.edge.ets.fe.documents.common.DocMessages;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSMeetings {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.51";
	
	private static Log logger = EtsLogger.getLogger(ETSMeetings.class);


	private static void showDate(PrintWriter out, String m, String d, String yy, String mname, String dname, String yname, String sId) {

		String mon[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);

		out.println("<table summary=\"\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td headers=\"\"  align=\"left\" width=\"60\"><label for=\"month\">Month</label><br />");
		out.println("<select id=\"" + sId + " month\" class=\"iform\" name=" + mname + ">");

		for (int k = 1; k < 13; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (m.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + mon[Integer.parseInt(qq) - 1] + "</option>");
		}

		out.println("</select></td><td headers=\"\" width=\"50\" align=\"left\"><label for=\"day\">Day</label><br /><select id=\"" + sId + " day\" class=\"iform\" name=" + dname + ">");
		for (int k = 1; k < 32; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (d.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select></td><td headers=\"\" align=\"left\"><label for=\"year\">Year</label><br /><select id=\"" + sId + " year\" class=\"iform\" name=" + yname + ">");
		for (int k = 0; k < 20; k++) {
			String qq = "" + (y + k - 2);
			out.println("<option value=\"" + qq + "\"");
			if (yy.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select>");
		out.println("</td></tr></table>");

	}



	public static void displayMeetingConfirmation(ETSParams params, String sOp, String sProjectId, String sCalId, Vector vAffectedEntries) throws SQLException, Exception {


		try {

			SimpleDateFormat simpleFormat = new SimpleDateFormat("EEE, MMM d,");

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			HttpServletRequest request = params.getRequest();

			String sLinkId = params.getLinkId();
			int iTopCat = params.getTopCat();

			String sMeetingOwner = "";

			//String sMeetingId = ETSUtils.checkNull(request.getParameter("meetid"));

			//Vector vMeetings = ETSCalendar.getMeetingsDetails(params,proj.getProjectId(),sMeetingId);

			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">Meeting confirmation</td></tr></table>");
			out.println("<br />");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

            ETSProperties prop = new ETSProperties();


			if (sCalId.trim().equals("")) {
                String sMess = prop.getSMeetingFailure();
				out.println("<tr>");
				out.println("<td headers=\"\"   height=\"18\" ><span style=\"color:#cc6600\">" + sMess + "</span></td>");
				out.println("</tr>");
			} else {
                String sMess = prop.getSMeetingSuccess();
				out.println("<tr>");
				out.println("<td headers=\"\"   height=\"18\" ><b>" + sMess + "</b></td>");
				out.println("</tr>");


				if (vAffectedEntries != null && vAffectedEntries.size() > 1) {
					// part of repeat meeting and more than one entry has been affected.

					out.println("<tr>");
					out.println("<td headers=\"\"   height=\"18\" >&nbsp;</td>");
					out.println("</tr>");


					if (sOp.trim().equalsIgnoreCase("I")) {
						out.println("<tr>");
						out.println("<td headers=\"\"   height=\"18\" ><b>Meetings on the following dates were also created:</b></td>");
						out.println("</tr>");

					} else {
						out.println("<tr>");
						out.println("<td headers=\"\"   height=\"18\" ><b>Meetings on the following dates were also updated:</b></td>");
						out.println("</tr>");
					}

					out.println("<tr>");
					out.println("<td headers=\"\"   height=\"18\" >&nbsp;</td>");
					out.println("</tr>");

					Vector vAffectedMeetings = ETSCalendar.getAffectedMeetingDetails(con,sProjectId,vAffectedEntries);

					if (vAffectedMeetings != null && vAffectedMeetings.size() > 0) {

						for (int i = 0; i < vAffectedMeetings.size(); i++) {

							ETSCal calEntry = (ETSCal) vAffectedMeetings.elementAt(i);

							String sRepeatDay = simpleFormat.format(calEntry.getSStartTime());
							String sTempDay = "";

							if (sRepeatDay.substring(0,3).equalsIgnoreCase("Mon")) {
								sTempDay = "Monday";
							} else if (sRepeatDay.substring(0,3).equalsIgnoreCase("Tue")) {
								sTempDay = "Tuesday";
							} else if (sRepeatDay.substring(0,3).equalsIgnoreCase("Wed")) {
								sTempDay = "Wednesday";
							} else if (sRepeatDay.substring(0,3).equalsIgnoreCase("Thu")) {
								sTempDay = "Thursday";
							} else if (sRepeatDay.substring(0,3).equalsIgnoreCase("Fri")) {
								sTempDay = "Friday";
							} else if (sRepeatDay.substring(0,3).equalsIgnoreCase("Sat")) {
								sTempDay = "Saturday";
							} else if (sRepeatDay.substring(0,3).equalsIgnoreCase("Sun")) {
								sTempDay = "Sunday";
							}
							String sStartDate = calEntry.getSStartTime().toString();
							String sFormattedStartDate = sStartDate.substring(5, 7) + "/" + sStartDate.substring(8, 10) + "/" + sStartDate.substring(0, 4);

							out.println("<tr>");
							out.println("<td headers=\"\"  align=\"left\" valign=\"top\">");
								out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
								out.println("<tr valign=\"top\">");
								out.println("<td headers=\"\" width=\"92\" align=\"left\"><b>Meeting date:</b></td>");
								if (sTempDay.trim().equalsIgnoreCase("Saturday") || sTempDay.trim().equalsIgnoreCase("Sunday")) {
									out.println("<td headers=\"\"  height=\"18\" align=\"left\">" + sFormattedStartDate + " [<span style=\"color:#cc6600\">" + sTempDay + "</span>]</td>");
								} else {
									out.println("<td headers=\"\"  height=\"18\" align=\"left\">" + sFormattedStartDate + " [" + sTempDay + "]</td>");
								}
								out.println("</tr>");
								out.println("</table>");
							out.println("</td></tr>");

						}
					}

				}

			}

			out.println("<tr>");
			out.println("<td headers=\"\"   height=\"18\" >&nbsp;</td>");
			out.println("</tr>");

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" >&nbsp;</td>");
			out.println("</tr>");

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=viewmeeting&meetid=" + sCalId + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
			out.println("</tr>");

			out.println("</table>");
			out.println("<br />");

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}



	private static void showDateTime(PrintWriter out, String m, String d, String yy, String hh, String mm, String ampm, String mname, String dname, String yname, String hhname, String mmname, String ampmname, String sDateId, String sTimeId) {

		String mon[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);

		out.println("<table summary=\"\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td headers=\"\"  align=\"left\" width=\"40\"><label for=\"month\">Month</label><br />");
		out.println("<select id=\"" + sDateId + " month\" class=\"iform\" name=" + mname + ">");

		for (int k = 1; k < 13; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (m.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + mon[Integer.parseInt(qq) - 1] + "</option>");
		}

		out.println("</select>&nbsp;&nbsp;</td><td headers=\"\"  width=\"30\" align=\"left\"><label for=\"day\">Day</label><br /><select id=\"" + sDateId + " day\" class=\"iform\" name=" + dname + ">");
		for (int k = 1; k < 32; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (d.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select>&nbsp;&nbsp;</td><td headers=\"\"  width=\"35\" align=\"left\"><label for=\"year\">Year</label><br /><select id=\"" + sDateId + " year\" class=\"iform\" name=" + yname + ">");
		for (int k = 0; k < 20; k++) {
			String qq = "" + (y + k - 2);
			out.println("<option value=\"" + qq + "\"");
			if (yy.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select>&nbsp;&nbsp;<td headers=\"\"  width=\"35\" align=\"left\"><label for=\"hour\">Hour</label><br />");
		out.println("<select id=\"" + sTimeId + " hour\" class=\"iform\" name=" + hhname + ">");

		for (int k = 1; k <= 12; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (hh.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select>&nbsp;&nbsp;</td><td headers=\"\"  width=\"35\" align=\"left\"><label for=\"min\">Min</label><br /><select id=\"" + sTimeId + " min\" class=\"iform\" name=" + mmname + ">");
		for (int k = 0; k <= 59; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (mm.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select>&nbsp;&nbsp;</td><td headers=\"\"  align=\"left\"><label for=\"label_tim\">AM/PM</label><br /><select id=\"" + sTimeId + " label_tim\" class=\"iform\" name=" + ampmname + ">");
		out.println("<option value=\"AM\"");
		if (ampm.equalsIgnoreCase("AM"))
			out.println(" selected=\"selected\" ");
		out.println(">AM</option>");
		out.println("<option value=\"PM\"");
		if (ampm.equalsIgnoreCase("PM"))
			out.println(" selected=\"selected\" ");
		out.println(">PM</option>");

		out.println("</select>&nbsp;&nbsp;Eastern US");
		out.println("</td></tr></table>");

	}

	public static void displayMeetingDetails(ETSParams params) throws SQLException, Exception {


		try {

			boolean bRepeatFlag = false;
			String sRepeatID = "";

			boolean bDocsDisplayed = false;
			boolean bDisplayEdit = false;
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			HttpServletRequest request = params.getRequest();

		    String strError = request.getParameter("error");
		    if (strError != null && !strError.equals("")) {
		        strError = DocMessages.getMessage(strError);
		    }
			String sLinkId = params.getLinkId();
			int iTopCat = params.getTopCat();

			String sMeetingOwner = "";
			String sCancelFlag = "N";

			String sMeetingId = ETSUtils.checkNull(request.getParameter("meetid"));
			
			int iFolderId = 0;

			Vector vMeetings = ETSCalendar.getMeetingsDetails(con,proj.getProjectId(),sMeetingId);

			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\"><b>Meeting details</b></td></tr></table>");
			out.println("<br />");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			if (strError != null && !strError.equals("")) {
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"4\"><span style=\"color:#ff3333\">" + strError.toString() + "<span></td>");
				out.println("</tr>");
			}
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");
			
	
			if (vMeetings != null && vMeetings.size() > 0) {

				for (int i = 0; i < vMeetings.size(); i++) {
					
					ETSCal calEntry = (ETSCal) vMeetings.elementAt(i);
					
					iFolderId = calEntry.getFolderId(); 
					
					boolean bInvitee = false;
					
					String sTemp = "," + calEntry.getSInviteesID() + ",";
			
					if (sTemp.indexOf("," + es.gIR_USERN.trim() + ",") >= 0)  {
						bInvitee = true;
					}						
					
					if (calEntry.getSScheduleBy().trim().equalsIgnoreCase(es.gIR_USERN) 
							|| bInvitee 
							|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_OWNER) 
							|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_MANAGER)
							|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.ETS_ADMIN) 
							|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKFLOW_ADMIN) 
							|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.ETS_EXECUTIVE)) {

						sMeetingOwner = calEntry.getSScheduleBy();
	
						String sTempDate = calEntry.getSStartTime().toString();
	
						String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
						String sTime = sTempDate.substring(11, 16);
	
						String sHour = sTempDate.substring(11, 13);
						String sMin = sTempDate.substring(14, 16);
						String sAMPM = "AM";
	
						if (Integer.parseInt(sHour) == 0) {
							sHour = "12";
							sAMPM = "AM";
						} else if (Integer.parseInt(sHour) == 12) {
							sHour = String.valueOf(Integer.parseInt(sHour));
	                        if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
							sAMPM = "PM";
						} else if (Integer.parseInt(sHour) > 12) {
							sHour = String.valueOf(Integer.parseInt(sHour) - 12);
	                        if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
							sAMPM = "PM";
						}
	
						String sTempDate1 = calEntry.getSScheduleDate().toString();
	
						String sDate1 = sTempDate1.substring(5, 7) + "/" + sTempDate1.substring(8, 10) + "/" + sTempDate1.substring(0, 4);
	
						long lTime = calEntry.getSStartTime().getTime() + (calEntry.getIDuration() * 60 * 1000);
	
						Timestamp timeEnd = new Timestamp(lTime);
						String sEndDate = timeEnd.toString();
	
						String sHour1 = sEndDate.substring(11, 13);
						String sMin1 = sEndDate.substring(14, 16);
						String sAMPM1 = "AM";
	
						if (Integer.parseInt(sHour1) == 0) {
							sHour1 = "12";
							sAMPM1 = "AM";
						} else if (Integer.parseInt(sHour1) == 12) {
							sHour1 = String.valueOf(Integer.parseInt(sHour1));
	                        if (Integer.parseInt(sHour1) < 10) sHour1 = "0" + sHour1;
							sAMPM1 = "PM";
						} else if (Integer.parseInt(sHour1) > 12) {
							sHour1 = String.valueOf(Integer.parseInt(sHour1) - 12);
	                        if (Integer.parseInt(sHour1) < 10) sHour1 = "0" + sHour1;
							sAMPM1 = "PM";
						}
	
						long lMeetingDate = Timestamp.valueOf(sTempDate.substring(0, 4) + "-" + sTempDate.substring(5, 7) + "-" + sTempDate.substring(8, 10) + " 23:59:00.000000000").getTime();
	
						String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");
						String sStartMonth = sTodaysDate.substring(0, 2);
						String sStartDate = sTodaysDate.substring(3, 5);
						String sStartYear = sTodaysDate.substring(6, 10);
	
						long lCurrentDate = Timestamp.valueOf(sStartYear + "-" + sStartMonth + "-" + sStartDate + " 00:00:00.000000000").getTime();
	
						if (lMeetingDate >= lCurrentDate) {
							bDisplayEdit = true;
						} else {
							bDisplayEdit = false;
						}
	
	
						if (calEntry.getSCancelFlag().trim().equalsIgnoreCase("Y")) {
							out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><span style=\"color:#ff3333\"><b>Please note that this meeting has been cancelled.</b></span></td>");
							out.println("</tr>");
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
							out.println("</tr>");
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
							out.println("</tr>");
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /><br /></td>");
							out.println("</tr>");
							out.println("</table>");
	
							sCancelFlag = "Y";
						}
	
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
						out.println("<tr valign=\"top\">");
						if (calEntry.getSWebFlag().trim().equalsIgnoreCase("Y") && !calEntry.getSCancelFlag().trim().equalsIgnoreCase("Y")) {
							out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><span class=\"subtitle\">" + calEntry.getSSubject() + "</span>&nbsp;&nbsp;[&nbsp;<a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Attend web conference</a>&nbsp;]<br /><br /></td>");
						} else {
							out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><span class=\"subtitle\">" + calEntry.getSSubject() + "</span><br /><br /></td>");
						}
	
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Meeting date</b>:</td>");
						out.println("<td headers=\"\" width=\"132\" class=\"small\" align=\"left\">" + sDate + "</td>");
						out.println("<td headers=\"\" width=\"94\" class=\"small\" align=\"left\"><b>Organized by</b>:</td>");
						out.println("<td headers=\"\" width=\"125\" class=\"small\" align=\"left\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Start time</b>:</td>");
						out.println("<td headers=\"\" width=\"132\" class=\"small\" align=\"left\">" + sHour + ":" + sMin + sAMPM.toLowerCase() + " Eastern US</td>");
						out.println("<td headers=\"\" width=\"94\" class=\"small\" align=\"left\"><b>Post date</b>:</td>");
						out.println("<td headers=\"\" width=\"125\" class=\"small\" align=\"left\">" + sDate1 + "</td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>End time</b>:</td>");
						out.println("<td headers=\"\" width=\"132\" class=\"small\" align=\"left\">" + sHour1 + ":" + sMin1 + sAMPM1.toLowerCase() + " Eastern US</td>");
						out.println("<td headers=\"\" width=\"94\" class=\"small\" align=\"left\">&nbsp;</td>");
						out.println("<td headers=\"\" width=\"125\" class=\"small\" align=\"left\">&nbsp;</td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><br /></td>");
						out.println("</tr>");
	
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Invitees</b>:</td>");
						out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">");
	
						StringTokenizer st = new StringTokenizer(calEntry.getSInviteesID(),",");
						int iCount = 0;
						while (st.hasMoreTokens()) {
							if (iCount == 0) {
								out.println(ETSUtils.getUsersName(con,st.nextToken()).trim());
							} else {
								out.println(",&nbsp;&nbsp;" + ETSUtils.getUsersName(con,st.nextToken()).trim());
							}
							iCount++;
						}
	
						out.println("</td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><br /></td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Copy to</b>:</td>");
						out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">" + insertSpacesForLongString(calEntry.getSCCList()) + "</td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><br /></td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Description</b>:</td>");
						out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">" + calEntry.getSDescription() + "</td>");
						out.println("</tr>");
	
	                    out.println("<tr valign=\"top\">");
	                    out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><br /></td>");
	                    out.println("</tr>");
	
	                    out.println("<tr valign=\"top\">");
	                    out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Call-in number</b>:</td>");
	                    out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">" + calEntry.getSCallIn() + "</td>");
	                    out.println("</tr>");
	
	//                    out.println("<tr valign=\"top\">");
	//                    out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><br /></td>");
	//                    out.println("</tr>");
	
	                    out.println("<tr valign=\"top\">");
	                    out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Pass code</b>:</td>");
	                    out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">" + calEntry.getSPass() + "</td>");
	                    out.println("</tr>");
	
						sRepeatID = calEntry.getSRepeatID();
	
						if (calEntry.getSRepeatType() != null && !calEntry.getSRepeatType().equalsIgnoreCase("N")) {
	
							// this is a repeat meetind.. just display the entries.
	
							bRepeatFlag = true;
	
	//						out.println("<tr>");
	//						out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" >&nbsp;</td>");
	//						out.println("</tr>");
	
	
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
							out.println("</tr>");
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
							out.println("</tr>");
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
							out.println("</tr>");
	
	//						out.println("<tr>");
	//						out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" >&nbsp;</td>");
	//						out.println("</tr>");
	
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" width=\"92\" class=\"small\"  align=\"left\"><b>Repeats:</b></td>");
							if (calEntry.getSRepeatType().equalsIgnoreCase("D")) {
								out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">Daily</td>");
							} else if (calEntry.getSRepeatType().equalsIgnoreCase("W")) {
								out.println("<td headers=\"\" colspan=\"3\" class=\"small\"  align=\"left\">Weekly</td>");
							} else if (calEntry.getSRepeatType().equalsIgnoreCase("M")) {
								out.println("<td headers=\"\" colspan=\"3\" class=\"small\"  align=\"left\">Monthly</td>");
							}
	
							out.println("</tr>");
	
	//						out.println("<tr>");
	//						out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" >&nbsp;</td>");
	//						out.println("</tr>");
	
							String sRepeatEndDate = df.format(calEntry.getSRepeatEnd());
	
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" width=\"92\" class=\"small\"  align=\"left\"><b>Repeat end date:</b></td>");
							out.println("<td headers=\"\" colspan=\"3\" class=\"small\"  align=\"left\">" + sRepeatEndDate + "</td>");
							out.println("</tr>");
	
						}
	
						out.println("</table>");
	
						out.println("<br />");
	
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
						out.println("</tr>");
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
						out.println("</tr>");
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
						out.println("</tr>");
						out.println("</table>");
							
						// display the edit only if owner of meeting or super admin
						// changed for 4.4.1
						if ((es.gIR_USERN.trim().equalsIgnoreCase(sMeetingOwner) 
								|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) && !sCancelFlag.trim().equalsIgnoreCase("Y")) {
	
							if (bDisplayEdit) {
	
								out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
								out.println("<tr valign=\"top\">");
								out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=editmeeting&meetid=" + sMeetingId + "&linkid=" + sLinkId + "\" ><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Edit meeting\" border=\"0\" /></a></td>");
								out.println("<td headers=\"\" width=\"80\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=editmeeting&meetid=" + sMeetingId + "&linkid=" + sLinkId + "\"  class=\"fbox\">Edit meeting</a></td>");
								out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=M&calid=" + sMeetingId + "\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=M&calid=" + sMeetingId + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=580,height=700,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=M&calid=" + sMeetingId + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=580,height=600,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Cancel meeting\" border=\"0\" /></a></td>");
								out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=M&calid=" + sMeetingId + "\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=M&calid=" + sMeetingId + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=580,height=700,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss?proj=" + proj.getProjectId() + "&caltype=M&calid=" + sMeetingId + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=580,height=600,left=150,top=120'); return false;\">Cancel meeting</a></td>");
								out.println("</tr>");
								out.println("</table>");
	
							}
	
						}
	
						out.println("<br /><br />");
						
						String sRepeatValue = "";
						if (sRepeatID.trim().equalsIgnoreCase("")) {
							sRepeatValue = "THIS-IS-NOT-A-REPEAT";
						} else {
							sRepeatValue = sRepeatID;
						}
						Vector vDocs = ETSDatabaseManager.getDocsForMeetings(con,proj.getProjectId(),sMeetingId, sRepeatValue);
	
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" class=\"tblue\">&nbsp;Meeting documents</td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<th id=\"head_name\" colspan=\"2\"  align=\"left\">");
						out.println("<span class=\"small\"><b>Name</b></span>");
						out.println("</th>");
						out.println("<th id=\"head_modified\"  align=\"left\">");
						out.println("<span class=\"small\"><b>Modified</b></span>");
						out.println("</th>");
						out.println("<th id=\"head_author\"  align=\"left\">");
						out.println("<span class=\"small\"><b>Author</b></span>");
						out.println("</th>");
						out.println("</tr>");
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"7\" alt=\"\" /></td>");
						out.println("</tr>");
	
						if (vDocs != null && vDocs.size() > 0) {
	
							bDocsDisplayed = true;
	
							for (int j = 0; j < vDocs.size(); j++) {
	
								ETSDoc doc = (ETSDoc) vDocs.elementAt(j);
	
								java.util.Date date = new java.util.Date(doc.getUpdateDate());
								String sDocDate = df.format(date);
	
								if ((j % 2) == 0) {
									out.println("<tr style=\"background-color: #eeeeee\">");
								} else {
									out.println("<tr >");
								}
								out.println("<td headers=\"\" headers=\"head_name\" width=\"16\" height=\"21\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"\" border=\"0\" /></td>");
								out.println("<td headers=\"\" headers=\"head_name\" height=\"21\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + doc.getFileName() +"?projid=" + proj.getProjectId() + "&docid=" + doc.getId() + "&linkid=120000\" class=\"fbox\" target=\"new\">" + doc.getName() + "</a></td>");
								out.println("<td headers=\"\" headers=\"head_modified\" height=\"21\" align=\"left\" valign=\"top\">" + sDocDate + "</td>");
								out.println("<td headers=\"\" headers=\"head_author\" height=\"21\" align=\"left\" valign=\"top\"><span class=\"small\"><span style=\"color:#666666\">" + ETSUtils.getUsersName(con,doc.getUserId()) + "</span></span></td>");
								out.println("</tr>");
	
							}
	
						} else {
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"5\">No documents has been added to this meeting yet.</td>");
							out.println("</tr>");
						}
	
						out.println("</table>");
					} else {
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" >&nbsp;</td>");
						out.println("</tr>");
						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" ><span style=\"color:#ff3333\"><b>The meeting details could not be displayed because you are not part of the invitees.</b></span></td>");
						out.println("</tr>");
						out.println("</table>");
					}
				}


			} else {
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" ><b>There meeting entry could not be found.</b></td>");
				out.println("</tr>");
				out.println("</table>");
			}

			out.println("<br />");
		
			String sCC = "";
			String sParentCC = "";
		
			if (iFolderId == 0) {
				sCC = "";
				sParentCC = "";
			} else {
				sCC = String.valueOf(iFolderId);
				int iParentFolderId = DocumentsHelper.getParentCatID(iFolderId);
				sParentCC = String.valueOf(iParentFolderId);
			}

			// display the edit only if owner of meeting or super admin
			// changed for 4.4.1
			if ((es.gIR_USERN.trim().equalsIgnoreCase(sMeetingOwner) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) && !sCancelFlag.trim().equalsIgnoreCase("Y")) {

				if (bDisplayEdit) {
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
	
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("</table>");
	
	
					if (bRepeatFlag) {
						out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">");
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"2\">");
						out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\" width=\"16\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&cc="+sCC+"&meetingid=" + sMeetingId + "&action=addmeetingdoc&etsop=addmeetingdoc\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Add document to this meeting\" border=\"0\" /></a></td>");
						out.println("<td headers=\"\" width=\"200\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&cc="+sCC+"&meetingid=" + sMeetingId + "&action=addmeetingdoc&etsop=addmeetingdoc\"  class=\"fbox\">Add document to this meeting</a></td>");
						out.println("<td headers=\"\" width=\"16\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&cc="+sParentCC+"&meetingid=" + sMeetingId + "&repeatid=" + sRepeatID + "&action=addmeetingdoc&etsop=addmeetingdoc\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Add documens to all instances\" border=\"0\" /></a></td>");
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&cc="+sParentCC+"&meetingid=" + sMeetingId + "&repeatid=" + sRepeatID + "&action=addmeetingdoc&etsop=addmeetingdoc\"  class=\"fbox\">Add document to all instances</a></td>");
						out.println("</tr>");
						out.println("</table>");
						out.println("</td>");
						out.println("</tr>");
						out.println("</table>");
	
						if (bDocsDisplayed) {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sCC +"','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Delete documents\" border=\"0\" /></a></td>");
							out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sCC +"','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Delete documents</a></td>");
							out.println("</tr>");
							out.println("</table>");
						}
	
					} else {
						if (bDocsDisplayed) {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">");
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" width=\"16\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&cc="+sCC+"&meetingid=" + sMeetingId + "&action=addmeetingdoc&etsop=addmeetingdoc\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Add document\" border=\"0\" /></a></td>");
							out.println("<td headers=\"\" width=\"150\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&cc="+sCC+"&meetingid=" + sMeetingId + "&action=addmeetingdoc&etsop=addmeetingdoc\"  class=\"fbox\">Add document</a></td>");
							out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sCC +"','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Delete documents\" border=\"0\" /></a></td>");
							out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sCC +"','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Delete documents</a></td>");
							out.println("</table>");
						} else {
							out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">");
							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" width=\"16\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&cc="+sCC+"&meetingid=" + sMeetingId + "&action=addmeetingdoc&etsop=addmeetingdoc\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Add document\" border=\"0\" /></a></td>");
							out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&cc="+sCC+"&meetingid=" + sMeetingId + "&action=addmeetingdoc&etsop=addmeetingdoc\"  class=\"fbox\">Add document</a></td>");
							out.println("</table>");
						}
					}
	
				}
				
			}else if ((es.gIR_USERN.trim().equalsIgnoreCase(sMeetingOwner) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) && sCancelFlag.trim().equalsIgnoreCase("Y")) {
				
				if (bDocsDisplayed) {
					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
	
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("</table>");
										
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sCC +"','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Delete documents\" border=\"0\" /></a></td>");
					out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sCC +"','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss?proj=" + proj.getProjectId() + "&calid=" + sMeetingId + "&tc="+params.getTopCat()+"&cc="+sParentCC + "','Delete','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Delete documents</a></td>");
					out.println("</tr>");
					out.println("</table>");
					
			   }	
			}

			out.println("<br /><br />");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("</table>");
				

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

	
	
	public static void displayTodaysMeetings(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			HttpServletRequest request = params.getRequest();

			int iTopCat = params.getTopCat();
			String sLinkId = params.getLinkId();

			String sInDate = ETSUtils.checkNull(request.getParameter("showdate"));

			Vector vAlerts = ETSCalendar.getTodaysMeetings(params,sInDate);

			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">Meetings and events for: " + sInDate +"</td></tr></table>");
			out.println("<br />");

			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"tdblue\"><b>&nbsp;Meetings</b></td></tr></table>");
			out.println("<br />");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\"><span class=\"small\">Meetings with <span style=\"color:#cc6600\">*</span> denotes a web conference meeting.</span></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");


			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					String sHour = sTempDate.substring(11, 13);
					String sMin = sTempDate.substring(14, 16);
					String sAMPM = "AM";

					if (Integer.parseInt(sHour) == 0) {
						sHour = "12";
						sAMPM = "AM";
					} else if (Integer.parseInt(sHour) == 12) {
						sHour = String.valueOf(Integer.parseInt(sHour));
                        if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
						sAMPM = "PM";
					} else if (Integer.parseInt(sHour) > 12) {
						sHour = String.valueOf(Integer.parseInt(sHour) - 12);
                        if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
						sAMPM = "PM";
					}

					int iDuration = calEntry.getIDuration();

					if (i == 0) {
						out.println("<tr>");
						out.println("<th id=\"meet_date\" height=\"18\" width=\"80\" align=\"left\"><span class=\"small\">Date</span></th>");
						out.println("<th id=\"meet_time\" height=\"18\" width=\"63\" align=\"left\"><span class=\"small\">Start time</span></th>");
						out.println("<th id=\"meet_web\" height=\"18\" width=\"2\" align=\"left\">&nbsp;</th>");
						out.println("<th id=\"meet_sub\" height=\"18\" width=\"180\" align=\"left\"><span class=\"small\">Subject</span></th>");
						out.println("<th id=\"meet_by\" height=\"18\" align=\"left\"><span class=\"small\">Organized by</span></th>");
						out.println("</tr>");
					}

					if ((i % 2) == 0) {
						out.println("<tr style=\"background-color: #eeeeee\">");
					} else {
						out.println("<tr >");
					}

					out.println("<td headers=\"\" headers=\"meet_date\" height=\"18\" width=\"80\" align=\"left\" valign=\"top\">" + sDate + "</td>");
					out.println("<td headers=\"\" headers=\"meet_time\" height=\"18\" width=\"63\" align=\"left\" valign=\"top\">" + sHour + ":" + sMin + sAMPM.toLowerCase() + "</td>");
					if (calEntry.getSWebFlag().trim().equalsIgnoreCase("Y")) {
						out.println("<td headers=\"\" headers=\"meet_web\" height=\"18\" width=\"2\" align=\"left\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
					} else {
						out.println("<td headers=\"\" headers=\"meet_web\" height=\"18\" width=\"2\" align=\"left\" valign=\"top\">&nbsp;</td>");
					}
					if (calEntry.getSCancelFlag().trim().equalsIgnoreCase("Y")) {
						out.println("<td headers=\"\" headers=\"meet_sub\" height=\"18\" width=\"180\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=viewmeeting&meetid=" + calEntry.getSCalendarId() + "&linkid=" + sLinkId + "\">" + calEntry.getSSubject() + "</a>&nbsp;[&nbsp;<span class=\"small\" style=\"color:#cc6600\">Cancelled</span>&nbsp;]</td>");
					} else {
						out.println("<td headers=\"\" headers=\"meet_sub\" height=\"18\" width=\"180\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=viewmeeting&meetid=" + calEntry.getSCalendarId() + "&linkid=" + sLinkId + "\">" + calEntry.getSSubject() + "</a></td>");
					}
					out.println("<td headers=\"\" headers=\"meet_by\" height=\"18\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</td>");
					out.println("</tr>");

				}

			} else {
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" ><b>There are no meetings defined for " + sInDate + "</b></td>");
				out.println("</tr>");
			}

			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" >&nbsp;</td>");
			out.println("</tr>");

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");


			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\">");

			// the schedule a meeting should not be displayed to executive and visitor.
			// changed for 4.4.1

			// schedule meeting should also allow aic workflow admin for 7.1.1 fix -  thanga
			if ( 
					ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MEMBER)
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKFLOW_ADMIN) )		{
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=newmeeting&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Schedule meeting\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"150\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=newmeeting&linkid=" + sLinkId + "\"  class=\"fbox\">Schedule a meeting</a></td>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Attent web conference\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Attend web conference</a></td>");
				out.println("</tr>");
				out.println("</table><br />");
			} else {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Attent web conference\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Attend web conference</a></td>");
				out.println("</tr>");
				out.println("</table><br />");
			}

			out.println("</td></tr>");
			out.println("</table>");

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}




	private static void displayUpComingMeetings(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			UnbrandedProperties prop = params.getUnbrandedProperties();

			int iTopCat = params.getTopCat();
			String sLinkId = params.getLinkId();

			Vector vAlerts = ETSCalendar.getUpcomingMeetings(params);

			//out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">Upcoming meetings</td></tr></table>");
			//out.println("<br />");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  headers=\"\" colspan=\"5\"><span class=\"small\">Meetings with <span style=\"color:#cc6600\">*</span> denotes a web conference meeting.</span></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  headers=\"\" colspan=\"5\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");


			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					String sHour = sTempDate.substring(11, 13);
					String sMin = sTempDate.substring(14, 16);
					String sAMPM = "AM";

					if (Integer.parseInt(sHour) == 0) {
						sHour = "12";
						sAMPM = "AM";
					} else if (Integer.parseInt(sHour) == 12) {
						sHour = String.valueOf(Integer.parseInt(sHour));
                        if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
						sAMPM = "PM";
					} else if (Integer.parseInt(sHour) > 12) {
						sHour = String.valueOf(Integer.parseInt(sHour) - 12);
                        if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
						sAMPM = "PM";
					}

					int iDuration = calEntry.getIDuration();

					if (i == 0) {
						out.println("<tr>");
						out.println("<th id=\"meet_date\" height=\"18\" width=\"80\" align=\"left\"><span class=\"small\">Date</span></th>");
						out.println("<th id=\"meet_time\" height=\"18\" width=\"63\" align=\"left\"><span class=\"small\">Start time</span></th>");
						out.println("<th id=\"meet_web\" height=\"18\" width=\"2\" align=\"left\">&nbsp;</th>");
						out.println("<th id=\"meet_sub\" height=\"18\" width=\"180\" align=\"left\"><span class=\"small\">Subject</span></th>");
						out.println("<th id=\"meet_by\" height=\"18\" align=\"left\"><span class=\"small\">Organized by</span></th>");
						out.println("</tr>");
					}

					if ((i % 2) == 0) {
						out.println("<tr style=\"background-color: #eeeeee\">");
					} else {
						out.println("<tr >");
					}

					out.println("<td headers=\"\" headers=\"meet_date\" height=\"18\" width=\"80\" align=\"left\" valign=\"top\">" + sDate + "</td>");
					out.println("<td headers=\"\" headers=\"meet_time\" height=\"18\" width=\"63\" align=\"left\" valign=\"top\">" + sHour + ":" + sMin + sAMPM.toLowerCase() + "</td>");
					if (calEntry.getSWebFlag().trim().equalsIgnoreCase("Y")) {
						out.println("<td headers=\"\" headers=\"meet_web\" height=\"18\" width=\"2\" align=\"left\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
					} else {
						out.println("<td headers=\"\" headers=\"meet_web\" height=\"18\" width=\"2\" align=\"left\" valign=\"top\">&nbsp;</td>");
					}
					if (calEntry.getSCancelFlag().trim().equalsIgnoreCase("Y")) {
						out.println("<td headers=\"\" headers=\"meet_sub\" height=\"18\" width=\"180\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=viewmeeting&meetid=" + calEntry.getSCalendarId() + "&linkid=" + sLinkId + "\">" + calEntry.getSSubject() + "</a>&nbsp;[&nbsp;<span class=\"small\" style=\"color:#cc6600\">Cancelled</span>&nbsp;]</td>");
					} else {
						out.println("<td headers=\"\" headers=\"meet_sub\" height=\"18\" width=\"180\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=viewmeeting&meetid=" + calEntry.getSCalendarId() + "&linkid=" + sLinkId + "\">" + calEntry.getSSubject() + "</a></td>");
					}

					out.println("<td headers=\"\" headers=\"meet_by\" height=\"18\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</td>");
					out.println("</tr>");

				}

			} else {

                String sMess = prop.getMeetingText();
				if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_VISITOR) 
						|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
					sMess = prop.getVisitorMeetingText();
				} else {
					sMess = prop.getMeetingText();
				}
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" ><b>" + sMess + "</b></td>");
				out.println("</tr>");
			}

			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" >&nbsp;</td>");
			out.println("</tr>");

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  headers=\"\" colspan=\"5\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");


			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\"><br />");

			// the schedule a meeting should not be displayed to executive and visitor.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MEMBER)
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKFLOW_ADMIN)	) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=newmeeting&linkid=" + sLinkId + "\"  class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Schedule a meeting\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\"  headers=\"\" align=\"left\" width=\"120\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=newmeeting&linkid=" + sLinkId + "\"  class=\"fbox\">Schedule a meeting</a></td>");
				out.println("<td headers=\"\"  headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Attend web conference\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\"  headers=\"\" align=\"left\" width=\"150\"><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Attend web conference</a></td>");
				out.println("<td headers=\"\"  headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=prevmeeting&linkid=" + sLinkId + "\"  class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"View past meetings\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\"  headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=prevmeeting&linkid=" + sLinkId + "\"  class=\"fbox\">View past meetings</a></td>");
				out.println("</tr>");
				out.println("</table><br />");
			} else {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Attend web conference\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\"  headers=\"\" align=\"left\" width=\"150\"><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Attend web conference</a></td>");
				out.println("<td headers=\"\"  headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=prevmeeting&linkid=" + sLinkId + "\"  class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"View past meetings\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\"  headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=prevmeeting&linkid=" + sLinkId + "\"  class=\"fbox\">View past meetings</a></td>");
				out.println("</tr>");
				out.println("</table><br />");
			}


			out.println("</td></tr>");
			out.println("</table>");

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}


	public static void displayTodaysEvents(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			HttpServletRequest request = params.getRequest();

			String sInDate = request.getParameter("showdate");

			String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");
			String sStartMonth = sTodaysDate.substring(0, 2);
			String sStartDate = sTodaysDate.substring(3, 5);
			String sStartYear = sTodaysDate.substring(6, 10);



			Vector vAlerts = ETSCalendar.getTodaysEvents(params, sInDate);

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" class=\"tblue\">&nbsp;Events</td>");
			out.println("</tr>");

			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();
					String sTmpEndDate = calEntry.getSRepeatEnd().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					String sHour = sTempDate.substring(11, 13);
					String sHour1 = sTempDate.substring(11, 13);
					String sMin = sTempDate.substring(14, 16);
					String sAMPM = "AM";

					if (Integer.parseInt(sHour) == 0) {
						sHour = "12";
						sAMPM = "AM";
					} else if (Integer.parseInt(sHour) == 12) {
						sHour = String.valueOf(Integer.parseInt(sHour));
						if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
						sAMPM = "PM";
					} else if (Integer.parseInt(sHour) > 12) {
						sHour = String.valueOf(Integer.parseInt(sHour) - 12);
						if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
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

					boolean bDisplayEdit = true;

					long lCurrentDate = new Timestamp(System.currentTimeMillis()).getTime();
					long lMeetingDate = Timestamp.valueOf(sTempDate.substring(0, 4) + "-" + sTempDate.substring(5, 7) + "-" + sTempDate.substring(8, 10) + " 23:59:00.000000000").getTime();

					if (lCurrentDate > lMeetingDate) {
						bDisplayEdit = false;
					}

					// the edit and cancel an  event should be displayed to user who created it and super admin.
					// changed for 4.4.1
					if ((calEntry.getSScheduleBy().trim().equalsIgnoreCase(es.gIR_USERN) 
							|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) && bDisplayEdit) {

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
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\">&nbsp;</td>");
				out.println("</tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><b>There are no events defined for " + sInDate + "</b></td>");
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

			// the add an event should not be displayed to executive and visitor.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MEMBER)
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKFLOW_ADMIN) ) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\">  <a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Add an event\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\">Add an event</a></td>");
				out.println("</tr>");
				out.println("</table>");
			}

			out.println("</td></tr>");
			out.println("</table>");

			out.println("<br /><br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"  class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
			out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"  class=\"fbox\">Back to '" + params.getCurrentTabName() + "'</a></td>");
			out.println("</tr>");
			out.println("</table>");
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}



	private static void displayUpComingEvents(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			UnbrandedProperties prop = params.getUnbrandedProperties();

			Vector vAlerts = ETSCalendar.getUpcomingEvents(params);

			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  class=\"tblue\"height=\"18\" width=\"443\"><b>&nbsp;Upcoming events</b></td></tr></table>");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  class=\"tblue\" valign=\"top\" width=\"100%\">");
			out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
			out.println("<tr valign=\"middle\">");
			out.println("<td headers=\"\"  style=\"background-color: #ffffff; color: #000000; font-weight: normal\" align=\"center\" valign=\"top\">");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			out.println("<tr>");
			out.println("<th id=\"event_date\" height=\"18\" width=\"80\" align=\"left\"><span class=\"small\">Date</span></th>");
			out.println("<th id=\"event_time\" height=\"18\" width=\"63\" align=\"left\"><span class=\"small\">Start time</span></th>");
			out.println("<th id=\"event_sub\" height=\"18\" width=\"180\" align=\"left\"><span class=\"small\">Subject</span></th>");
			out.println("<th id=\"event_by\" height=\"18\" align=\"left\"><span class=\"small\">Organized by</span></th>");
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

					if (Integer.parseInt(sHour) == 0) {
						sHour = "12";
						sAMPM = "AM";
					} else if (Integer.parseInt(sHour) == 12) {
						sHour = String.valueOf(Integer.parseInt(sHour));
						if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
						sAMPM = "PM";
					} else if (Integer.parseInt(sHour) > 12) {
						sHour = String.valueOf(Integer.parseInt(sHour) - 12);
						if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
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

					if ((i % 2) == 0) {
						out.println("<tr style=\"background-color: #eeeeee\">");
					} else {
						out.println("<tr >");
					}

					out.println("<td headers=\"\" headers=\"event_date\" height=\"18\" width=\"80\" valign=\"top\" align=\"left\">" + sDate + "</td>");
					out.println("<td headers=\"\" headers=\"event_time\" height=\"18\" width=\"63\" valign=\"top\" align=\"left\">" + sHour + ":" + sMin + sAMPM + "</td>");
					out.println("<td headers=\"\" headers=\"event_sub\" height=\"18\" width=\"180\" valign=\"top\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEntry.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">" + calEntry.getSSubject() + "</a></td>");
					out.println("<td headers=\"\" headers=\"event_by\" height=\"18\" valign=\"top\" align=\"left\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</td>");
					out.println("</tr>");

				}

			} else {

                String sMess = prop.getEventText();
				if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
					sMess = prop.getVisitorEventText();
				} else {
					sMess = prop.getEventText();
				}
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" ><b>" + sMess + "</b></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
			}

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"4\"><br />");

			// the add an event should not be displayed to executive and visitor.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MEMBER)
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKFLOW_ADMIN) ) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\">  <a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Add an event\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\">Add an event</a></td>");
				out.println("</tr>");
				out.println("</table><br />");
			}

			out.println("</td></tr>");
			out.println("</table>");
			out.println("</td></tr>");
			out.println("</table>");
			out.println("</td></tr>");
			out.println("</table>");

		} catch (Exception e) {
			throw e;
		} finally {
		}

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
			out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" class=\"tblue\">&nbsp;Upcoming events</td>");
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

					if (Integer.parseInt(sHour) == 0) {
						sHour = "12";
						sAMPM = "AM";
					} else if (Integer.parseInt(sHour) == 12) {
						sHour = String.valueOf(Integer.parseInt(sHour));
						if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
						sAMPM = "PM";
					} else if (Integer.parseInt(sHour) > 12) {
						sHour = String.valueOf(Integer.parseInt(sHour) - 12);
						if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
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

					// the edit and cancel should only be displayed to creator and super admin
					// changed for 4.4.1
					if (calEntry.getSScheduleBy().trim().equalsIgnoreCase(es.gIR_USERN) 
							|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)
							|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKFLOW_ADMIN) ) {

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

				if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
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
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MEMBER)
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKFLOW_ADMIN) ) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\">  <a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Add an event\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=600,height=500,left=150,top=120'); return false;\">Add an event</a></td>");
				out.println("</tr>");
				out.println("</table>");
			}

			out.println("</td></tr>");
			out.println("</table>");

			out.println("<br /><br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"  class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
			out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"  class=\"fbox\">Back to '" + params.getCurrentTabName() + "'</a></td>");
			out.println("</tr>");
			out.println("</table>");
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}











	public static void editMeetingDetails(ETSParams params) throws SQLException, Exception {


		try {

			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			HttpServletRequest request = params.getRequest();

			String sMeetingId = ETSUtils.checkNull(request.getParameter("meetid"));
			
			String sHelpUrl = Global.WebRoot + "/ets_meeting_copyto.html";

			out.println("<form name=\"cal_edit\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");

			out.println("<input type=\"hidden\" name=\"meetid\" value=\"" + sMeetingId + "\" />");

			if (sMeetingId.trim().equals("")) {

				// new meeting entry..

				out.println("<input type=\"hidden\" name=\"etsop\" value=\"insertmeeting\" />");

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">Schedule a new meeting</td></tr></table>");
				out.println("<br />");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
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

				String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");

				String sStartMonth = sTodaysDate.substring(0, 2);
				String sStartDate = sTodaysDate.substring(3, 5);
				String sStartYear = sTodaysDate.substring(6, 10);

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_subject\">Subject</label></b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_subject\" id=\"label_sub\" class=\"iform\" maxlength=\"100\" size=\"45\" value=\"\" /></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_desc\">Description</label></b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><textarea name=\"cal_desc\" cols=\"47\" rows=\"5\"  class=\"iform\" maxlength=\"1000\" id=\"label_desc\"></textarea></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

                // Callin number & passcode...
                    // Callin number...
                out.println("<tr valign=\"top\">");
                out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_subject\">Call-in number:</label></b></td>");
                out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_callin\" id=\"label_sub\" class=\"iform\" maxlength=\"100\" size=\"45\" value=\"\" /></td>");
                out.println("</tr>");
                    // Passcode...
                out.println("<tr>");
                out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
                out.println("</tr>");
                out.println("<tr valign=\"top\">");
                out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_subject\">Passcode:</label></b></td>");
                out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_passcode\" id=\"label_sub\" class=\"iform\" maxlength=\"100\" size=\"45\" value=\"\" /></td>");
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
                out.println("</tr>");

                //...for 4.2.1 relase

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"92\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_date\">Meeting date:</label></b></td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">");
				showDate(out,sStartMonth,sStartDate,sStartYear,"cal_month","cal_day","cal_year","label_date");
				out.println("</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_date\">Starts:</label></b></td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">");
				//showDate(out,sStartMonth,sStartDate,sStartYear,"cal_month","cal_day","cal_year","label_date");
				showTime(out,"08","00","AM","cal_hour","cal_min","cal_ampm","label_date");

				out.println("</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_edate\">Ends:</label></b></td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">");

				//showDate(out,sStartMonth,sStartDate,sStartYear,"cal_month","cal_day","cal_year","label_date");
				showTime(out,"09","00","AM","cal_ehour","cal_emin","cal_eampm","label_edate");

				out.println("</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");


				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_invitees\">Invitees</label></b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><span class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</span><br />" + displayInviteesAsSelect(con,"cal_invitees","label_invitees",proj.getProjectId(),"",es.gIR_USERN) + "</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_cc\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=copyto&proj_type=" + proj.getProjectType() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=copyto&proj_type=" + proj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=copyto&proj_type=" + proj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Copy to</a></label></b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_cc\"  class=\"iform\" id=\"label_cc\" maxlength=\"1000\" size=\"45\" value=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">(Add multiple e-mail addresses by separating them with a comma. Example: user1@domain.com, user2@domain.com)</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_web\">Web conference</label></b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"checkbox\" name=\"cal_web\"  class=\"iform\" id=\"label_web\" value=\"Y\" /></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  width=\"100\"><b><a href=\"ETSContentManagerServlet.wss?OP=Notification%20help&BlurbField=E-mail%20notification%20option&proj=" + proj.getProjectId() + "\" onclick=\"window.open('ETSContentManagerServlet.wss?OP=Notification%20help&BlurbField=E-mail%20notification%20option&proj=" + proj.getProjectId() + "','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\" onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Notification%20help&BlurbField=E-mail%20notification%20option&proj=" + proj.getProjectId() + "','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >E-mail notification option</a>:</b></td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\" checked=\"checked\" /><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" /><label for=\"notifyOption2\">Bcc</label></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");


				// documents
				
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><b>Documents</b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">You must click <b>Submit</b> before you can attach documents to this meeting.</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				
				// end of documents

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\" align=\"left\">If you would like to schedule this meeting as a repeat meeting, please complete the section below.</td>");
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

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><label for=\"label_repeat\"><b>Repeats:</b></label></td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><select id=\"label_repeat\" class=\"iform\" name=\"repeat\"><option value=\"NONE\" selected=\"selected\">None</option><option value=\"DAILY\">Daily</option><option value=\"WEEKLY\">Weekly</option><option value=\"MONTHLY\">Monthly</option></select></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_repeat_end_date\">Repeat end date:</label></b></td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">");
				showDate(out,sStartMonth,sStartDate,sStartYear,"end_month","end_day","end_year","label_repeat_end_date");
				out.println("</td>");
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


				out.println("<tr valign=\"top\"><td headers=\"\" colspan=\"4\" align=\"left\"><br />");
					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
					out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"  >Cancel</a></td></tr></table></td>");
					out.println("</tr>");
					out.println("</table>");
				out.println("</td></tr>");

				out.println("</table>");
				out.println("<br />");



			} else {

				out.println("<input type=\"hidden\" name=\"etsop\" value=\"updatemeeting\" />");

				Vector vMeetings = ETSCalendar.getMeetingsDetails(con,proj.getProjectId(),sMeetingId);

				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">Edit meeting details</td></tr></table>");
				out.println("<br />");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
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


				if (vMeetings != null && vMeetings.size() > 0) {

					for (int i = 0; i < vMeetings.size(); i++) {

						ETSCal calEntry = (ETSCal) vMeetings.elementAt(i);

						String sTempDate = calEntry.getSStartTime().toString();

						String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
						String sTime = sTempDate.substring(11, 16);

						String sHour = sTempDate.substring(11, 13);
						String sMin = sTempDate.substring(14, 16);
						String sAMPM = "AM";

						if (Integer.parseInt(sHour) == 0) {
							sHour = "12";
							sAMPM = "AM";
						} else if (Integer.parseInt(sHour) == 12) {
							sHour = String.valueOf(Integer.parseInt(sHour));
							if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
							sAMPM = "PM";
						} else if (Integer.parseInt(sHour) > 12) {
							sHour = String.valueOf(Integer.parseInt(sHour) - 12);
							if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
							sAMPM = "PM";
						}

//						String sTempDate1 = calEntry.getSScheduleDate().toString();
//
//						String sDate1 = sTempDate1.substring(5, 7) + "/" + sTempDate1.substring(8, 10) + "/" + sTempDate1.substring(0, 4);

						long lTime = calEntry.getSStartTime().getTime() + (calEntry.getIDuration() * 60 * 1000);

						Timestamp timeEnd = new Timestamp(lTime);
						String sEndDate = timeEnd.toString();

						String sHour1 = sEndDate.substring(11, 13);
						String sMin1 = sEndDate.substring(14, 16);
						String sAMPM1 = "AM";

						if (Integer.parseInt(sHour1) == 0) {
							sHour1 = "12";
							sAMPM1 = "AM";
						} else if (Integer.parseInt(sHour1) == 12) {
							sHour1 = String.valueOf(Integer.parseInt(sHour1));
                            if (Integer.parseInt(sHour1) < 10) sHour1 = "0" + sHour1;
							sAMPM1 = "PM";
						} else if (Integer.parseInt(sHour1) > 12) {
							sHour1 = String.valueOf(Integer.parseInt(sHour1) - 12);
                            if (Integer.parseInt(sHour1) < 10) sHour1 = "0" + sHour1;
							sAMPM1 = "PM";
						}

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_subject\">Subject</label></b>:</td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_subject\" id=\"label_sub\" class=\"iform\" maxlength=\"100\" size=\"45\" value=\"" + calEntry.getSSubject() + "\" /></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_desc\">Description</label></b>:</td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\"><textarea name=\"cal_desc\" cols=\"47\" rows=\"5\"  class=\"iform\" maxlength=\"1000\" id=\"label_desc\">" + calEntry.getSDescription() + "</textarea></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
						out.println("</tr>");

                        // Callin number & passcode...
                            // Callin number...
                        out.println("<tr valign=\"top\">");
                        out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_subject\">Call-in number:</label></b></td>");
                        out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_callin\" id=\"label_sub\" class=\"iform\" maxlength=\"100\" size=\"45\" value=\"" + calEntry.getSCallIn() + "\" /></td>");
                        out.println("</tr>");
                            // Passcode...
                        out.println("<tr>");
                        out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
                        out.println("</tr>");
                        out.println("<tr valign=\"top\">");
                        out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_subject\">Passcode:</label></b></td>");
                        out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_passcode\" id=\"label_sub\" class=\"iform\" maxlength=\"100\" size=\"45\" value=\"" + calEntry.getSPass() + "\" /></td>");
                        out.println("</tr>");

                        out.println("<tr>");
                        out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
                        out.println("</tr>");

                        //...for 4.2.1 relase

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"92\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_date\">Meeting date:</label></b></td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\">");

						showDate(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10), sTempDate.substring(0, 4),"cal_month","cal_day","cal_year","label_date");
						//showTime(out,sHour,sMin,sAMPM,"cal_hour","cal_min","cal_ampm","label_date");

						out.println("</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_date\">Starts:</label></b></td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\">");

						//showDate(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10), sTempDate.substring(0, 4),"cal_month","cal_day","cal_year","label_date");
						showTime(out,sHour,sMin,sAMPM,"cal_hour","cal_min","cal_ampm","label_date");

						out.println("</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_edate\">Ends:</label></b></td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\">");

						//showDate(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10), sTempDate.substring(0, 4),"cal_month","cal_day","cal_year","label_date");
						showTime(out,sHour1,sMin1,sAMPM1,"cal_ehour","cal_emin","cal_eampm","label_edate");

						out.println("</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_invitees\">Invitees</label></b>:</td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\"><span class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</span><br />" + displayInviteesAsSelect(con,"cal_invitees","label_invitees",proj.getProjectId(),calEntry.getSInviteesID(),es.gIR_USERN) + "</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_cc\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=copyto&proj_type=" + proj.getProjectType() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=copyto&proj_type=" + proj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=copyto&proj_type=" + proj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Copy to</a></label></b>:</td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_cc\"  class=\"iform\" id=\"label_cc\" maxlength=\"1000\" size=\"45\" value=\"" + calEntry.getSCCList() + "\" /></td>");
						out.println("</tr>");
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\">&nbsp;</td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\">(Add multiple e-mail addresses by separating them with a comma. Example: user1@domain.com, user2@domain.com)</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
						out.println("</tr>");


						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_web\">Web conference</label></b>:</td>");
						if (calEntry.getSWebFlag().trim().equalsIgnoreCase("Y")) {
							out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"checkbox\" name=\"cal_web\"  class=\"iform\" id=\"label_web\" value=\"Y\" checked=\"checked\" /></td>");
						} else {
							out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"checkbox\" name=\"cal_web\"  class=\"iform\" id=\"label_web\" value=\"Y\" /></td>");
						}
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\"><b><a href=\"ETSContentManagerServlet.wss?OP=Notification%20help&BlurbField=E-mail%20notification%20option&proj=" + proj.getProjectId() + "\" onclick=\"window.open('ETSContentManagerServlet.wss?OP=Notification%20help&BlurbField=E-mail%20notification%20option&proj=" + proj.getProjectId() + "','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\" onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Notification%20help&BlurbField=E-mail%20notification%20option&proj=" + proj.getProjectId() + "','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >E-mail notification option</a>:</b></td>");
						if (calEntry.getNotifyType().equalsIgnoreCase("T")) {
							out.println("<td headers=\"\" width=\"343\" align=\"left\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\" checked=\"checked\" /><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" /><label for=\"notifyOption2\">Bcc</label></td>");
						} else {
							out.println("<td headers=\"\" width=\"343\" align=\"left\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\"/><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" checked=\"checked\" /><label for=\"notifyOption2\">Bcc</label></td>");
						}
						
						out.println("</tr>");


						if (calEntry.getSRepeatType() != null && !calEntry.getSRepeatType().equalsIgnoreCase("N")) {

							// this is a repeat meetind.. just display the entries.


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

							out.println("<tr>");
							out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" width=\"100\" align=\"left\"><b>Apply changes to:</b></td>");
							out.println("<td headers=\"\" width=\"343\" align=\"left\" valign=\"top\"><input type=\"radio\" name=\"repeat_none\" value=\"N\" selected=\"selected\" checked=\"checked\" id=\"label_this\" /><label for=\"label_this\">This meeting only</label><br /><input type=\"radio\" name=\"repeat_none\" value=\"Y\" id=\"label_all\" /><label for=\"label_all\">All future instances from this meeting</label></td>");
							out.println("</tr>");

							out.println("<tr>");
							out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" width=\"100\" align=\"left\"><b>Repeats:</b></td>");
							if (calEntry.getSRepeatType().equalsIgnoreCase("D")) {
								out.println("<td headers=\"\" width=\"343\" align=\"left\">Daily</td>");
							} else if (calEntry.getSRepeatType().equalsIgnoreCase("W")) {
								out.println("<td headers=\"\" width=\"343\" align=\"left\">Weekly</td>");
							} else if (calEntry.getSRepeatType().equalsIgnoreCase("D")) {
								out.println("<td headers=\"\" width=\"343\" align=\"left\">Monthly</td>");
							}

							out.println("</tr>");

							out.println("<tr>");
							out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
							out.println("</tr>");

							String sRepeatEndDate = df.format(calEntry.getSRepeatEnd());

							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" width=\"100\" align=\"left\"><b>Repeat end date:</b></td>");
							out.println("<td headers=\"\" width=\"343\" align=\"left\">" + sRepeatEndDate + "</td>");
							out.println("</tr>");

							out.println("<tr>");
							out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
							out.println("</tr>");

						}


					}


				} else {
					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><b>This meeting entry could not be found.</b></td>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
					out.println("</tr>");
				}

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");


				out.println("<tr valign=\"top\"><td headers=\"\" colspan=\"4\" align=\"left\"><br />");
					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
					out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
					out.println("</tr>");
					out.println("</table>");
				out.println("</td></tr>");

				out.println("</table>");
				out.println("<br />");
			}

			out.println("</form>");

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}










	public static void displayMeetingsTab(ETSParams params) throws SQLException, Exception {

		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			HttpServletRequest request = params.getRequest();

			String sETSOp = request.getParameter("etsop");

			if (sETSOp == null || sETSOp.trim().equals("")) {
				sETSOp = "";
			} else {
				sETSOp = sETSOp.trim();
			}

			if (sETSOp.trim().equals("")) {

				out.println(ETSUtils.getBookMarkString("Upcoming meetings",""));

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\"><tr valign=\"top\">");
				out.println("<td headers=\"\"  width=\"443\">");

				displayUpComingMeetings(params);

				out.println("<br />");

				//displayUpComingEvents(params);

				out.println("</td>");
				out.println("</tr></table>");

				out.println("<br />");

			} else if (sETSOp.trim().equalsIgnoreCase("newmeeting") || sETSOp.trim().equalsIgnoreCase("editmeeting")) {
				editMeetingDetails(params);
			} else if (sETSOp.trim().equalsIgnoreCase("insertmeeting") || sETSOp.trim().equalsIgnoreCase("updatemeeting")) {
				// update the meeting details and display the meeting details screen.

				insertUpdateMeeting(params);

			} else if (sETSOp.trim().equalsIgnoreCase("prevmeeting")) {

				displayPreviousMeetings(params);

				out.println("<br />");
			} else if (sETSOp.trim().equalsIgnoreCase("viewmeeting")) {
				displayMeetingDetails(params);
			} else if (sETSOp.trim().equalsIgnoreCase("moreevents")) {

				displayAllEvents(params);

				out.println("<br />");
			} else if (sETSOp.trim().equalsIgnoreCase("caldetail")) {

				displayTodaysMeetings(params);

				out.println("<br />");

				displayTodaysEvents(params);

				out.println("<br />");

			} else if (sETSOp.trim().equalsIgnoreCase("pastevents")) {
				ETSProjectHome.displayPastEvents(params);

			} else if (sETSOp.trim().equalsIgnoreCase("addmeetingdoc")) {
                ETSDocumentManager dm = new ETSDocumentManager(params);
                dm.ETSDocumentHandler();
            }





		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}




	/**
	 * Method displayPreviousMeetings.
	 * @param params
	 */
	private static void displayPreviousMeetings(ETSParams params) throws SQLException, Exception {


		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			UnbrandedProperties prop = params.getUnbrandedProperties();

			int iTopCat = params.getTopCat();
			String sLinkId = params.getLinkId();

			Vector vAlerts = ETSCalendar.getPreviousMeetings(params);

			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">Past meetings</td></tr></table>");
			out.println("<br />");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\"><span class=\"small\">Meetings with <span style=\"color:#cc6600\">*</span> denotes a web conference meeting.</span></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");


			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					String sHour = sTempDate.substring(11, 13);
					String sMin = sTempDate.substring(14, 16);
					String sAMPM = "AM";

					if (Integer.parseInt(sHour) == 0) {
						sHour = "12";
						sAMPM = "AM";
					} else if (Integer.parseInt(sHour) == 12) {
						sHour = String.valueOf(Integer.parseInt(sHour));
						if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
						sAMPM = "PM";
					} else if (Integer.parseInt(sHour) > 12) {
						sHour = String.valueOf(Integer.parseInt(sHour) - 12);
						if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
						sAMPM = "PM";
					}

					int iDuration = calEntry.getIDuration();

					if (i == 0) {
						out.println("<tr>");
						out.println("<th id=\"meet_date\" height=\"18\" width=\"80\" align=\"left\"><span class=\"small\">Date</span></th>");
						out.println("<th id=\"meet_time\" height=\"18\" width=\"63\" align=\"left\"><span class=\"small\">Start time</span></th>");
						out.println("<th id=\"meet_web\" height=\"18\" width=\"2\" align=\"left\">&nbsp;</th>");
						out.println("<th id=\"meet_sub\" height=\"18\" width=\"180\" align=\"left\"><span class=\"small\">Subject</span></th>");
						out.println("<th id=\"meet_by\" height=\"18\" align=\"left\"><span class=\"small\">Organized by</span></th>");
						out.println("</tr>");
					}

					if ((i % 2) == 0) {
						out.println("<tr style=\"background-color: #eeeeee\">");
					} else {
						out.println("<tr >");
					}

					out.println("<td headers=\"\" headers=\"meet_date\" height=\"18\" width=\"80\" align=\"left\" valign=\"top\">" + sDate + "</td>");
					out.println("<td headers=\"\" headers=\"meet_time\" height=\"18\" width=\"63\" align=\"left\" valign=\"top\">" + sHour + ":" + sMin + sAMPM.toLowerCase() + "</td>");
					if (calEntry.getSWebFlag().trim().equalsIgnoreCase("Y")) {
						out.println("<td headers=\"\" headers=\"meet_web\" height=\"18\" width=\"2\" align=\"left\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
					} else {
						out.println("<td headers=\"\" headers=\"meet_web\" height=\"18\" width=\"2\" align=\"left\" valign=\"top\">&nbsp;</td>");
					}
					if (calEntry.getSCancelFlag().trim().equalsIgnoreCase("Y")) {
						out.println("<td headers=\"\" headers=\"meet_sub\" height=\"18\" width=\"180\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=viewmeeting&meetid=" + calEntry.getSCalendarId() + "&linkid=" + sLinkId + "\">" + calEntry.getSSubject() + "</a>&nbsp;[&nbsp;<span class=\"small\" style=\"color:#cc6600\">Cancelled</span>&nbsp;]</td>");
					} else {
						out.println("<td headers=\"\" headers=\"meet_sub\" height=\"18\" width=\"180\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=viewmeeting&meetid=" + calEntry.getSCalendarId() + "&linkid=" + sLinkId + "\">" + calEntry.getSSubject() + "</a></td>");
					}

					out.println("<td headers=\"\" headers=\"meet_by\" height=\"18\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,calEntry.getSScheduleBy()) + "</td>");
					out.println("</tr>");

				}

			} else {
                String sMess = prop.getPrevMeetingText();

				if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_VISITOR) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
					sMess = prop.getVisitorPrevMeetingText();
				} else {
					sMess = prop.getPrevMeetingText();
				}
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" ><b>" + sMess + "</b></td>");
				out.println("</tr>");
			}

			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"5\" height=\"18\" >&nbsp;</td>");
			out.println("</tr>");

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");


			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\"  colspan=\"5\"><br />");

			// the schedule a meeting should not be displayed to executive and visitor.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) 
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MEMBER)
					|| ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKFLOW_ADMIN) ) {
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Attend web conference\" border=\"0\" /></a></td>");
				out.println("<td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5\" target=\"new\"  class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSServicesServlet.wss?servicesop=5','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Attend web conference</a></td>");
				out.println("</tr>");
				out.println("</table><br />");
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


	private static String displayInviteesAsSelect(Connection con, String sSelectName, String sLabelId, String sProjectId, String sInviteesList, String sLoggedInID) throws SQLException, Exception {

		StringBuffer out = new StringBuffer("");

		try {

			String sAvailable = "," + sInviteesList + ",";
			out.append("<select multiple=\"multiple\" size=\"5\" style=\"width:250px\" width=\"250px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");

			Vector vMembers = ETSDatabaseManager.getProjMembers(sProjectId,con);

			if (vMembers != null && vMembers.size() > 0) {

				for (int i = 0; i < vMembers.size(); i++) {

					ETSUser user = (ETSUser) vMembers.elementAt(i);

                    if (!user.getUserId().equalsIgnoreCase(sLoggedInID)) {

    					if (sAvailable.indexOf("," + user.getUserId() + ",") >= 0)  {
    						out.append("<option value=\"" + user.getUserId() + "\" selected=\"selected\">" + ETSUtils.getUsersName(con,user.getUserId()) + "</option>");
    					} else {
    						out.append("<option value=\"" + user.getUserId() + "\" >" + ETSUtils.getUsersName(con,user.getUserId()) + "</option>");
    					}

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

	/**
	 * Method updateCalendarEntry.
	 * @param conn
	 * @param out
	 * @param request
	 */
	public static String insertUpdateMeeting(ETSParams params) throws SQLException, Exception {

		PreparedStatement stmt = null;
		StringBuffer sQuery = new StringBuffer("");

		String sCalendarId = "";
		String sOp = "I";

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
		String sHelpUrl = Global.WebRoot + "/ets_meeting_copyto.html";

		try {

			if (validateMeeting(params)) {
				
				String sCalID = "";
				Vector vAffectedEntries = new Vector();

				HttpServletRequest request = params.getRequest();
				Connection conn = params.getConnection();
				EdgeAccessCntrl es = params.getEdgeAccessCntrl();
				
				sCalendarId = ETSUtils.checkNull(request.getParameter("meetid"));
				String sCalendarEntry = "M";
				String sProjectId = ETSUtils.checkNull(request.getParameter("proj"));
				
				String docCatID = ETSUtils.getTopCatId(conn,sProjectId, Defines.DOCUMENTS_VT);

				String sCalSubject = ETSUtils.checkNull(request.getParameter("cal_subject"));
				String sCalDuration = "";		//ETSUtils.checkNull(request.getParameter("cal_duration"));
				String sCalDesc = ETSUtils.checkNull(request.getParameter("cal_desc"));
				String sCalMonth = ETSUtils.checkNull(request.getParameter("cal_month"));
				String sCalDay = ETSUtils.checkNull(request.getParameter("cal_day"));
				String sCalYear = ETSUtils.checkNull(request.getParameter("cal_year"));

				String sCalHour = ETSUtils.checkNull(request.getParameter("cal_hour"));
				if (sCalHour.trim().equals("")) sCalHour = "00";
				String sCalMinutes = ETSUtils.checkNull(request.getParameter("cal_min"));
				if (sCalMinutes.trim().equals("")) sCalMinutes = "00";

				String sCalAMPM = ETSUtils.checkNull(request.getParameter("cal_ampm"));
				if (sCalAMPM.trim().equals("")) {
					sCalAMPM = "AM";
					if (Integer.parseInt(sCalHour) == 12) {
						sCalHour = "00";
					}
				} else if (sCalAMPM.trim().equalsIgnoreCase("PM")) {
					if (Integer.parseInt(sCalHour) < 12) {
						sCalHour = String.valueOf(Integer.parseInt(sCalHour) + 12);
					}
				} else if (sCalAMPM.trim().equalsIgnoreCase("AM")) {
					if (Integer.parseInt(sCalHour) == 12) {
						sCalHour = "00";
					}
				}


				String sCalEndHour = ETSUtils.checkNull(request.getParameter("cal_ehour"));
				if (sCalEndHour.trim().equals("")) sCalEndHour = "00";
				String sCalEndMinutes = ETSUtils.checkNull(request.getParameter("cal_emin"));
				if (sCalEndMinutes.trim().equals("")) sCalEndMinutes = "00";

				String sCalEndAMPM = ETSUtils.checkNull(request.getParameter("cal_eampm"));
				if (sCalEndAMPM.trim().equals("")) {
					sCalEndAMPM = "AM";
					if (Integer.parseInt(sCalEndHour) == 12) {
						sCalEndHour = "00";
					}
				} else if (sCalEndAMPM.trim().equalsIgnoreCase("PM")) {
					if (Integer.parseInt(sCalEndHour) < 12) {
						sCalEndHour = String.valueOf(Integer.parseInt(sCalEndHour) + 12);
					}
				} else if (sCalEndAMPM.trim().equalsIgnoreCase("AM")) {
					if (Integer.parseInt(sCalEndHour) == 12) {
						sCalEndHour = "00";
					}
				}

				String sCalInvitees[] = request.getParameterValues("cal_invitees");
				String sInviteesId = "";
				if (sCalInvitees != null && sCalInvitees.length > 0) {
					for (int i = 0; i < sCalInvitees.length; i++) {
						if (sInviteesId.trim().equals("")) {
							sInviteesId = sCalInvitees[i];
						} else {
							sInviteesId = sInviteesId + "," + sCalInvitees[i];
						}
					}
				}

				Timestamp timeStart = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000");
				Timestamp timeEnd = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalEndHour + ":" + sCalEndMinutes + ":00.000000000");

				long lDur = 0;

				if (timeStart.before(timeEnd)) {
					lDur = timeEnd.getTime() - timeStart.getTime();
					sCalDuration = String.valueOf((lDur / 1000) / 60);		// to convert to minutes...
				}

				String sCalWeb = ETSUtils.checkNull(request.getParameter("cal_web"));

				String sCalCC = ETSUtils.checkNull(request.getParameter("cal_cc"));
                String sCallIn = ETSUtils.checkNull(request.getParameter("cal_callin"));
                String sPass = ETSUtils.checkNull(request.getParameter("cal_passcode"));
                
				String sNotifyType = ETSUtils.checkNull(request.getParameter("notifyOption"));

				int iResult = 0;

				if (!sCalendarId.trim().equals("")) {

					sCalID = sCalendarId;

					// get this information before cos once it is updated, the date changes.

					Vector vRepeatMeetings = ETSCalendar.getUpdatableRepeatMeetings(conn,sProjectId,sCalendarId);

					/*
					 * 1. check to see if all future instances has been selected.
					 * if yes, then update all the future instances of the meeting..
					 */

					sQuery.append("UPDATE ETS.ETS_CALENDAR SET SUBJECT = ?, DESCRIPTION = ?, DURATION = ?, START_TIME = ?, INVITEES_ID = ?,WEB_CONF_FLAG = ?, CC_LIST = ?, CALLIN_NUMBER = ?, PASS_CODE = ?, NOTIFY_TYPE = ? WHERE ");
					sQuery.append("PROJECT_ID = ? AND CALENDAR_TYPE= ? AND CALENDAR_ID = ?");

					logger.debug("ETSCalendar::updateCalendarEntry()::QUERY : " + sQuery.toString());

					stmt = conn.prepareStatement(sQuery.toString());

					stmt.setString(1,ETSUtils.escapeString(sCalSubject));
					stmt.setString(2,ETSUtils.escapeString(sCalDesc));
					stmt.setInt(3,Integer.parseInt(sCalDuration));
					stmt.setTimestamp(4,Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000")); 		// check this to see if it works correctly. Sathish
					stmt.setString(5,sInviteesId);
					stmt.setString(6,sCalWeb);
					stmt.setString(7,sCalCC);
					stmt.setString(8,sCallIn);
					stmt.setString(9,sPass);
					stmt.setString(10,sNotifyType);
					stmt.setString(11,sProjectId);
					stmt.setString(12,sCalendarEntry.toUpperCase());
					stmt.setString(13,sCalendarId);

					vAffectedEntries.addElement(sCalendarId);

					iResult = stmt.executeUpdate();
					
					Vector vMeetings = ETSCalendar.getMeetingsDetails(conn,sProjectId,sCalendarId);

					if (vMeetings != null && vMeetings.size() > 0) {
						for (int i = 0; i < vMeetings.size(); i++) {
							ETSCal calEntry = (ETSCal) vMeetings.elementAt(i);
							if (calEntry.getFolderId() != 0) {
								// documents tab exists.. so update folder.
								
								if (calEntry.getSRepeatType().equalsIgnoreCase("N")) {
									// update the folder information...
									DocumentsHelper.updateMeetingFolder(calEntry.getFolderId(),sCalSubject,sProjectId,es.gIR_USERN);	
								} else {
									// update the folder information with the data as this is part of repeat meeting...
									String sTYear = calEntry.getSStartTime().toString().substring(0,4);
									String sTMonth = calEntry.getSStartTime().toString().substring(5,7);
									String sTDay = calEntry.getSStartTime().toString().substring(8,10);
									DocumentsHelper.updateMeetingFolder(calEntry.getFolderId(),sTMonth + "/" + sTDay + "/" + sTYear,sProjectId,es.gIR_USERN);
								}
							}
						}
					}
					

					String sRepeatNone = request.getParameter("repeat_none");
					if (sRepeatNone == null || sRepeatNone.trim().equalsIgnoreCase("")) {
						sRepeatNone = "N";
					}


					if (sRepeatNone.trim().equalsIgnoreCase("Y")) {

						// update the future instances of the meeting also.
						// this is a repeat meeting and changes apply to all future instances.

						boolean bUpdate = false;

						ETSDBUtils.close(stmt);

						sQuery.setLength(0);
						sQuery.append("UPDATE ETS.ETS_CALENDAR SET SUBJECT = ?, DESCRIPTION = ?, DURATION = ?, START_TIME = ?, INVITEES_ID = ?,WEB_CONF_FLAG = ?, CC_LIST = ?, CALLIN_NUMBER = ?, PASS_CODE = ?, NOTIFY_TYPE = ? WHERE ");
						sQuery.append("PROJECT_ID = ? AND CALENDAR_TYPE= ? AND CALENDAR_ID = ?");

						logger.debug("ETSCalendar::updateCalendarEntry()::QUERY : " + sQuery.toString());

						stmt = conn.prepareStatement(sQuery.toString());

						for (int i = 0; i < vRepeatMeetings.size(); i++) {

							ETSCal calEntry = (ETSCal) vRepeatMeetings.elementAt(i);

							stmt.clearParameters();

							sCalYear = calEntry.getSStartTime().toString().substring(0,4);
							sCalMonth = calEntry.getSStartTime().toString().substring(5,7);
							sCalDay = calEntry.getSStartTime().toString().substring(8,10);

							stmt.setString(1,ETSUtils.escapeString(sCalSubject));
							stmt.setString(2,ETSUtils.escapeString(sCalDesc));
							stmt.setInt(3,Integer.parseInt(sCalDuration));
							stmt.setTimestamp(4,Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000")); 		// check this to see if it works correctly. Sathish
							stmt.setString(5,sInviteesId);
							stmt.setString(6,sCalWeb);
							stmt.setString(7,sCalCC);
							stmt.setString(8,sCallIn);
							stmt.setString(9,sPass);
							stmt.setString(10,sNotifyType);
							stmt.setString(11,sProjectId);
							stmt.setString(12,sCalendarEntry.toUpperCase());
							stmt.setString(13,calEntry.getSCalendarId());

							vAffectedEntries.addElement(calEntry.getSCalendarId());

							iResult = stmt.executeUpdate();

							Vector vGetMeetings = ETSCalendar.getMeetingsDetails(conn,sProjectId,calEntry.getSCalendarId());

							if (vGetMeetings != null && vGetMeetings.size() > 0) {
								for (int j = 0; j < vGetMeetings.size(); j++) {
									ETSCal calentry = (ETSCal) vGetMeetings.elementAt(j);
									if (calEntry.getFolderId() != 0) {
										// update the folder information...
										String sTYear = calentry.getSStartTime().toString().substring(0,4);
										String sTMonth = calentry.getSStartTime().toString().substring(5,7);
										String sTDay = calentry.getSStartTime().toString().substring(8,10);
										
										DocumentsHelper.updateMeetingFolder(calentry.getFolderId(),sTMonth + "/" + sTDay + "/" + sTYear,sProjectId,es.gIR_USERN);
									}
								}
							}					
						}
						
						// Edit meeting update Restr. users for Docs...
						for (int i = 0; i < vRepeatMeetings.size(); i++) {
							
							ETSCal calEntry = (ETSCal) vRepeatMeetings.elementAt(i);
							Vector vtMtgDocs = ETSDatabaseManager.getDocsForMeetings(conn,sProjectId,calEntry.getSCalendarId(), calEntry.getSRepeatID());
								
								for (int j=0; j < vtMtgDocs.size(); j++) {
								
									ETSDoc doc = (ETSDoc) vtMtgDocs.elementAt(j); 
									String docId = ""+ doc.getId()+"";
									Vector existgRestDocUsrs = ETSDatabaseManager.getAllDocRestrictedUserIds(docId,sProjectId,conn);
									String strAddUser = "";
															
									StringTokenizer strHeaderTokens = new StringTokenizer(sInviteesId,",");
								    
								     while (strHeaderTokens.hasMoreTokens()) {
								         String strToken = strHeaderTokens.nextToken();
								          boolean usrExsts = false;
								    
								          for(int k=0; k < existgRestDocUsrs.size(); k++){
								          		String exstgUsr = (String) existgRestDocUsrs.elementAt(k); 
								               if (strToken.equals(exstgUsr)) { usrExsts = true; break; }
								           }
								           
								           if(!usrExsts){
								           	
								           	if (!strAddUser.equals(""))
								    				strAddUser =
								    						strAddUser
								    						+ ",("
								    						+ docId
								    						+ ",'"
								    						+ strToken
								    						+ "','"
								    						+ sProjectId +"')";
								    						
								    		else
								    				strAddUser =
								    						strAddUser
								    						+ "("
								    						+ docId
								    						+ ",'"
								    						+ strToken
								    						+ "','"
								    						+ sProjectId +"')";
								    						
								         	
								           }
								            
									} // For each user chk ExistgRestUsersList
								     
								     logger.debug("The updated Restr users for this Doc == "+strAddUser);
								     
								     if (!strAddUser.equals("")){
								     	
								     	boolean addRestUsr = false;
								     	addRestUsr = ETSDatabaseManager.updateDocResUsers(doc,strAddUser,"",conn);
								     	
								     	if(addRestUsr){
								     		logger.debug("The Restricted users updated for Doc == "+doc.getName());
								     		logger.debug("The updated Restr users for this Doc == "+strAddUser); 
								     	}
								     }
								     
									
								} // For each meeting Doc
															
											
						} // For each meeting
					}else{
						
						Vector vtMtgDocs = ETSDatabaseManager.getDocsForMeetings(conn,sProjectId,sCalendarId, "THIS-IS-NOT-A-REPEAT");
						
						for (int j=0; j < vtMtgDocs.size(); j++) {
						
							ETSDoc doc = (ETSDoc) vtMtgDocs.elementAt(j); 
							String docId = ""+ doc.getId()+"";
							Vector existgRestDocUsrs = ETSDatabaseManager.getAllDocRestrictedUserIds(docId,sProjectId,conn);
							String strAddUser = "";
													
							StringTokenizer strHeaderTokens = new StringTokenizer(sInviteesId,",");
						    
						     while (strHeaderTokens.hasMoreTokens()) {
						         String strToken = strHeaderTokens.nextToken();
						          boolean usrExsts = false;
						    
						          for(int k=0; k < existgRestDocUsrs.size(); k++){
						          		String exstgUsr = (String) existgRestDocUsrs.elementAt(k); 
						               if (strToken.equals(exstgUsr)) { usrExsts = true; break; }
						           }
						           
						           if(!usrExsts){
						           	
						           	if (!strAddUser.equals(""))
						    				strAddUser =
						    						strAddUser
						    						+ ",("
						    						+ docId
						    						+ ",'"
						    						+ strToken
						    						+ "','"
						    						+ sProjectId +"')";
						    						
						    		else
						    				strAddUser =
						    						strAddUser
						    						+ "("
						    						+ docId
						    						+ ",'"
						    						+ strToken
						    						+ "','"
						    						+ sProjectId +"')";
						    						
						         	
						           }
						            
							} // For each user chk ExistgRestUsersList
						     
						     if (!strAddUser.equals("")){
						     	
						     	boolean addRestUsr = false;
						     	addRestUsr = ETSDatabaseManager.updateDocResUsers(doc,strAddUser,"",conn);
						     	
						     	if(addRestUsr){
						     		logger.debug("The Restricted users updated for Doc == "+doc.getName());
						     		logger.debug("The updated Restr users for this Doc == "+strAddUser); 
						     	}
						     }
						
							
						} // For each meeting Doc
						
					}

					sOp = "U";

				} else {

					String sRepeat = request.getParameter("repeat");
					if (sRepeat == null || sRepeat.trim().equalsIgnoreCase("")) {
						sRepeat = "";
					} else {
						sRepeat = sRepeat.trim();
					}

					String sRepEndMonth = ETSUtils.checkNull(request.getParameter("end_month"));
					String sRepEndDay = ETSUtils.checkNull(request.getParameter("end_day"));
					String sRepEndYear = ETSUtils.checkNull(request.getParameter("end_year"));

					String sRepeatID = "";

					if (sRepeat.trim().equalsIgnoreCase("Daily") || sRepeat.trim().equalsIgnoreCase("Weekly") || sRepeat.trim().equalsIgnoreCase("Monthly")) {
						// generate a new repeat id..
						sRepeatID = ETSCalendar.getNewCalendarId();
					}


					ETSDBUtils.close(stmt);
					sQuery.setLength(0);

					sQuery.append("INSERT INTO ETS.ETS_CALENDAR (CALENDAR_ID,PROJECT_ID,CALENDAR_TYPE,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_START,REPEAT_END,NOTIFY_TYPE,FOLDER_ID) VALUES ");
					sQuery.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

					stmt = conn.prepareStatement(sQuery.toString());

					if (sRepeat.trim().equalsIgnoreCase("Daily") || sRepeat.trim().equalsIgnoreCase("Weekly") || sRepeat.trim().equalsIgnoreCase("Monthly")) {
						
						// create the top level folder for repeat meeting
						int iParentFolderId = 0;
						
						if (!docCatID.equalsIgnoreCase("0")) {
							iParentFolderId = DocumentsHelper.addMeetingsFolder(Defines.MEETINGS_PARENT_FOLDER_ID,ETSUtils.escapeString(sCalSubject),sProjectId,es.gIR_USERN);	
						}
						

						boolean bCompleted = false;

						String sMeetDay = sCalDay;

						boolean bFirst = true;

						sCalID = ETSCalendar.getNewCalendarId();

						while (!bCompleted) {


							stmt.clearParameters();

							ETSCal cal = new ETSCal();
							if (bFirst) {
								sCalendarId = sCalID;
								bFirst = false;
							} else {
								sCalendarId = ETSCalendar.getNewCalendarId();
							}
							cal.setSCalendarId(sCalendarId);		// generate a  new id
							cal.setSProjectId(sProjectId);
							cal.setSCalType(sCalendarEntry.toUpperCase());
							cal.setSScheduleDate(new Timestamp(System.currentTimeMillis()));
							cal.setSScheduleBy(es.gIR_USERN);
							cal.setSStartTime(Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000"));

							String sTYear = cal.getSStartTime().toString().substring(0,4);
							String sTMonth = cal.getSStartTime().toString().substring(5,7);
							String sTDay = cal.getSStartTime().toString().substring(8,10);

							// create the top level folder for repeat meeting
							int iFolderId = 0;
							if (!docCatID.equalsIgnoreCase("0")) {
								iFolderId = DocumentsHelper.addMeetingsFolder(iParentFolderId,sTMonth + "/" + sTDay + "/" + sTYear,sProjectId,es.gIR_USERN);
							}
							
							cal.setIDuration(Integer.parseInt(sCalDuration));
							cal.setSSubject(ETSUtils.escapeString(sCalSubject));
							cal.setSDescription(ETSUtils.escapeString(sCalDesc));
							cal.setSInviteesID(sInviteesId);
							cal.setSCCList(sCalCC);
							cal.setSWebFlag(sCalWeb);
							cal.setSCancelFlag("N");
							cal.setSEmailFlag("N");
							cal.setSIBMOnly("N");
							cal.setSCallIn(sCallIn);
							cal.setSPass(sPass);
							cal.setSRepeatID(sRepeatID);
							cal.setSRepeatType(sRepeat.substring(0,1).toUpperCase());
							cal.setSRepeatStart(Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000"));
							cal.setSRepeatEnd(Timestamp.valueOf(sRepEndYear + "-" + sRepEndMonth + "-" + sRepEndDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000"));
							cal.setNotifyType(sNotifyType);
							cal.setFolderId(iFolderId);

							stmt.setString(1,cal.getSCalendarId());				// calendar id
							stmt.setString(2,cal.getSProjectId());				// project id
							stmt.setString(3,cal.getSCalType());				// calendar type
							stmt.setTimestamp(4,cal.getSScheduleDate());		// schedule date
							stmt.setString(5,cal.getSScheduleBy());				// schedule by
							stmt.setTimestamp(6,cal.getSStartTime());			// start time
							stmt.setInt(7,cal.getIDuration());					// duration
							stmt.setString(8,cal.getSSubject());				// subject
							stmt.setString(9,cal.getSDescription());			// description
							stmt.setString(10,cal.getSInviteesID());			// Invitees id
							stmt.setString(11,cal.getSCCList());				// CC list
							stmt.setString(12,cal.getSWebFlag());				// web conference flag
							stmt.setString(13,cal.getSCancelFlag());			// cancel flag
							stmt.setString(14,cal.getSEmailFlag());				// email flag
							stmt.setString(15,cal.getSIBMOnly());				// ibm only flag
							stmt.setString(16,cal.getSCallIn());                // callin number
							stmt.setString(17,cal.getSPass());                  // passcode
							stmt.setString(18,cal.getSRepeatType());            // repeat type
							stmt.setString(19,cal.getSRepeatID());              // repeat id
							stmt.setTimestamp(20,cal.getSRepeatStart());        // repeat start
							stmt.setTimestamp(21,cal.getSRepeatEnd());          // repeat end
							stmt.setString(22,cal.getNotifyType()); 	        // NOTIFY TYPE
							stmt.setInt(23,cal.getFolderId()); 	        		// Folder ID

							iResult = stmt.executeUpdate();


							vAffectedEntries.addElement(sCalendarId);


							if (sRepeat.trim().equalsIgnoreCase("Daily")) {
								// add a day
								sCalDay = String.valueOf(Integer.parseInt(sCalDay) + 1);

								Timestamp timeMeeting = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 00:00:00.000000000");
								Timestamp repEnd = Timestamp.valueOf(sRepEndYear + "-" + sRepEndMonth + "-" + sRepEndDay + " 01:00:00.000000000");

								if (timeMeeting.before(repEnd)) {

								} else {
									bCompleted = true;
								}

							} else if (sRepeat.trim().equalsIgnoreCase("Weekly")) {
								// add a 7 days
								sCalDay = String.valueOf(Integer.parseInt(sCalDay) + 7);

								Timestamp timeMeeting = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 00:00:00.000000000");
								Timestamp repEnd = Timestamp.valueOf(sRepEndYear + "-" + sRepEndMonth + "-" + sRepEndDay + " 01:00:00.000000000");

								if (timeMeeting.before(repEnd)) {

								} else {
									bCompleted = true;
								}

							} else if (sRepeat.trim().equalsIgnoreCase("Monthly")) {

								// add a month
								sCalMonth = String.valueOf(Integer.parseInt(sCalMonth) + 1);

								if (Integer.parseInt(sCalMonth) > 12) {
									sCalMonth = String.valueOf(1);
									sCalYear = String.valueOf(Integer.parseInt(sCalYear) + 1);
								}

								Calendar cal1 = Calendar.getInstance();
								cal1.set(Calendar.YEAR,Integer.parseInt(sCalYear));
								cal1.set(Calendar.MONTH,Integer.parseInt(sCalMonth) -1);

								int iMaxDaysInMonth =  cal1.getActualMaximum(Calendar.DAY_OF_MONTH);
								int iMinDaysInMonth = cal1.getActualMinimum(Calendar.DAY_OF_MONTH);


								boolean bCorrectDay = false;

								sCalDay = sMeetDay;

								while (bCorrectDay == false) {
									if(Integer.parseInt(sCalDay) > iMaxDaysInMonth){
										sCalDay = String.valueOf(Integer.parseInt(sCalDay) -1);
									} else {
										bCorrectDay = true;
									}
								}

								Timestamp timeMeeting = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 00:00:00.000000000");
								Timestamp repEnd = Timestamp.valueOf(sRepEndYear + "-" + sRepEndMonth + "-" + sRepEndDay + " 01:00:00.000000000");

								if (timeMeeting.before(repEnd)) {

								} else {
									bCompleted = true;
								}

							}

						}


					} else {
						
						// not a repeat meeting.. just create one entry.

						sCalID = ETSCalendar.getNewCalendarId();

						stmt.clearParameters();

						ETSCal cal = new ETSCal();
						sCalendarId = sCalID;
						cal.setSCalendarId(sCalendarId);		// generate a  new id
						cal.setSProjectId(sProjectId);
						cal.setSCalType(sCalendarEntry.toUpperCase()); 
						cal.setSScheduleDate(new Timestamp(System.currentTimeMillis()));
						cal.setSScheduleBy(es.gIR_USERN);
						cal.setSStartTime(Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000"));
						
						String sTYear = cal.getSStartTime().toString().substring(0,4);
						String sTMonth = cal.getSStartTime().toString().substring(5,7);
						String sTDay = cal.getSStartTime().toString().substring(8,10);

						int iFolderId = 0;
						if (!docCatID.equalsIgnoreCase("0")) {
							// create the top level folder for repeat meeting
							iFolderId = DocumentsHelper.addMeetingsFolder(Defines.MEETINGS_PARENT_FOLDER_ID,sCalSubject,sProjectId,es.gIR_USERN);	
						}
						
						cal.setIDuration(Integer.parseInt(sCalDuration));
						cal.setSSubject(ETSUtils.escapeString(sCalSubject));
						cal.setSDescription(ETSUtils.escapeString(sCalDesc));
						cal.setSInviteesID(sInviteesId);
						cal.setSCCList(sCalCC);
						cal.setSWebFlag(sCalWeb);
						cal.setSCancelFlag("N");
						cal.setSEmailFlag("N");
						cal.setSIBMOnly("N");
						cal.setSCallIn(sCallIn);
						cal.setSPass(sPass);
						cal.setSRepeatID(sRepeatID);
						cal.setSRepeatType(sRepeat.substring(0,1).toUpperCase());
						cal.setSRepeatStart(Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000"));
						cal.setSRepeatEnd(Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000"));
						cal.setNotifyType(sNotifyType);
						cal.setFolderId(iFolderId);

						stmt.setString(1,cal.getSCalendarId());				// calendar id
						stmt.setString(2,cal.getSProjectId());				// project id
						stmt.setString(3,cal.getSCalType());				// calendar type
						stmt.setTimestamp(4,cal.getSScheduleDate());		// schedule date
						stmt.setString(5,cal.getSScheduleBy());				// schedule by
						stmt.setTimestamp(6,cal.getSStartTime());			// start time
						stmt.setInt(7,cal.getIDuration());					// duration
						stmt.setString(8,cal.getSSubject());				// subject
						stmt.setString(9,cal.getSDescription());			// description
						stmt.setString(10,cal.getSInviteesID());			// Invitees id
						stmt.setString(11,cal.getSCCList());				// CC list
						stmt.setString(12,cal.getSWebFlag());				// web conference flag
						stmt.setString(13,cal.getSCancelFlag());			// cancel flag
						stmt.setString(14,cal.getSEmailFlag());				// email flag
						stmt.setString(15,cal.getSIBMOnly());				// ibm only flag
						stmt.setString(16,cal.getSCallIn());                // callin number
						stmt.setString(17,cal.getSPass());                  // passcode
						stmt.setString(18,cal.getSRepeatType());            // repeat type
						stmt.setString(19,cal.getSRepeatID());              // repeat id
						stmt.setTimestamp(20,cal.getSRepeatStart());        // repeat start
						stmt.setTimestamp(21,cal.getSRepeatEnd());          // repeat end
						stmt.setString(22,cal.getNotifyType()); 	        // NOTIFY TYPE
						stmt.setInt(23,cal.getFolderId()); 	        // folder id

						vAffectedEntries.addElement(sCalendarId);

						iResult = stmt.executeUpdate();

					}


					if (iResult <= 0) {
						sCalendarId = "";
					}

					sOp = "I";
				}

				if (!sCalendarId.trim().equals("")) {
			    	// call send email routine to notify the people in cc list and notify list.
			    	sendUpdateEmailNotification(conn,es,sOp,sProjectId,sCalID,"M",vAffectedEntries,params.getLinkId());
			    }

				displayMeetingConfirmation(params,sOp,sProjectId,sCalID,vAffectedEntries);

			} else {

				StringBuffer sError = new StringBuffer("");

				Connection con = params.getConnection();
				PrintWriter out = params.getWriter();
				EdgeAccessCntrl es = params.getEdgeAccessCntrl();
				ETSProj proj = params.getETSProj();
				HttpServletRequest request = params.getRequest();

				sCalendarId = ETSUtils.checkNull(request.getParameter("meetid"));
				String sCalendarEntry = "M";
				String sProjectId = ETSUtils.checkNull(request.getParameter("proj"));

				String sCalSubject = ETSUtils.checkNull(request.getParameter("cal_subject"));
				if (sCalSubject.trim().equals("")) {
					sError.append("<b>Subject</b> cannot be empty. Please enter the subject for the meeting.<br />");
				}

				String sCalDuration = "";		//ETSUtils.checkNull(request.getParameter("cal_duration"));
				String sCalDesc = ETSUtils.checkNull(request.getParameter("cal_desc"));
				if (sCalDesc.trim().equals("")) {
					sError.append("<b>Description</b> cannot be empty. Please enter the description for the meeting.<br />");
				}
				String sCalMonth = ETSUtils.checkNull(request.getParameter("cal_month"));
				String sCalDay = ETSUtils.checkNull(request.getParameter("cal_day"));
				String sCalYear = ETSUtils.checkNull(request.getParameter("cal_year"));

				String sCalHour = ETSUtils.checkNull(request.getParameter("cal_hour"));
				if (sCalHour.trim().equals("")) sCalHour = "00";
				String sCalMinutes = ETSUtils.checkNull(request.getParameter("cal_min"));
				if (sCalMinutes.trim().equals("")) sCalMinutes = "00";

				String sCalAMPM = ETSUtils.checkNull(request.getParameter("cal_ampm"));
				if (sCalAMPM.trim().equals("")) {
					sCalAMPM = "AM";
					if (Integer.parseInt(sCalHour) == 12) {
						sCalHour = "00";
					}
				} else if (sCalAMPM.trim().equalsIgnoreCase("PM")) {
					if (Integer.parseInt(sCalHour) < 12) {
						sCalHour = String.valueOf(Integer.parseInt(sCalHour) + 12);
					}
				} else if (sCalAMPM.trim().equalsIgnoreCase("AM")) {
					if (Integer.parseInt(sCalHour) == 12) {
						sCalHour = "00";
					}
				}


				String sCalEndHour = ETSUtils.checkNull(request.getParameter("cal_ehour"));
				if (sCalEndHour.trim().equals("")) sCalEndHour = "00";
				String sCalEndMinutes = ETSUtils.checkNull(request.getParameter("cal_emin"));
				if (sCalEndMinutes.trim().equals("")) sCalEndMinutes = "00";

				String sCalEndAMPM = ETSUtils.checkNull(request.getParameter("cal_eampm"));
				if (sCalEndAMPM.trim().equals("")) {
					sCalEndAMPM = "AM";
					if (Integer.parseInt(sCalEndHour) == 12) {
						sCalEndHour = "00";
					}					
				} else if (sCalEndAMPM.trim().equalsIgnoreCase("PM")) {
					if (Integer.parseInt(sCalEndHour) < 12) {
						sCalEndHour = String.valueOf(Integer.parseInt(sCalEndHour) + 12);
					}
				} else if (sCalEndAMPM.trim().equalsIgnoreCase("AM")) {
					if (Integer.parseInt(sCalEndHour) == 12) {
						sCalEndHour = "00";
					}
				}

                String sCallIn = ETSUtils.checkNull(request.getParameter("cal_callin"));
                String sPass = ETSUtils.checkNull(request.getParameter("cal_passcode"));

				Timestamp timeStart = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000");
				Timestamp timeEnd = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalEndHour + ":" + sCalEndMinutes + ":00.000000000");

				long lDur = 0;

				if (timeStart.before(timeEnd)) {
				} else {
					sError.append("<b>End time</b> cannot be before meeting <b>Start time</b>. Please select meeting times appropriately.<br />");
				}

//				// to make it display correctly if it is set to 0.
//				// it is set to 0 when the meeting time is set to 12.** AM
//				if (Integer.parseInt(sCalEndHour) == 0) {
//					sCalEndHour = "12";
//				}
//
//				// to make it display correctly if it is set to 0.
//				// it is set to 0 when the meeting time is set to 12.** AM
//				if (Integer.parseInt(sCalHour) == 0) {
//					sCalHour = "12";
//				}
				

				// to make it display correctly if as the hour might has been changed to validate time..
				sCalHour = ETSUtils.checkNull(request.getParameter("cal_hour"));
				sCalEndHour = ETSUtils.checkNull(request.getParameter("cal_ehour"));
				

				String sCalInvitees[] = request.getParameterValues("cal_invitees");
				String sInviteesId = "";
				if (sCalInvitees != null && sCalInvitees.length > 0) {
					for (int i = 0; i < sCalInvitees.length; i++) {
						if (sInviteesId.trim().equals("")) {
							sInviteesId = sCalInvitees[i];
						} else {
							sInviteesId = sInviteesId + "," + sCalInvitees[i];
						}
					}
				} else {
					sError.append("<b>Invitees</b> cannot be empty. Please select invitees for the meeting.<br />");
				}

				long timeEvent = timeStart.getTime();
				long timeNow = new Timestamp(System.currentTimeMillis()).getTime();

				out.println("<form name=\"cal_edit\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\" method=\"post\">");
				out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
				out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
				out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");


				String sRepeat = request.getParameter("repeat");
				String sRepEndMonth = ETSUtils.checkNull(request.getParameter("end_month"));
				String sRepEndDay = ETSUtils.checkNull(request.getParameter("end_day"));
				String sRepEndYear = ETSUtils.checkNull(request.getParameter("end_year"));
				if (sRepeat == null || sRepeat.trim().equalsIgnoreCase("")) {
					sRepeat = "";
				} else {
					sRepeat = sRepeat.trim();

					// checks valid end date for repeat meeting - thanga
					if (sRepeat.trim().equalsIgnoreCase("Daily") || sRepeat.trim().equalsIgnoreCase("Weekly") || sRepeat.trim().equalsIgnoreCase("Monthly")) {
						if( checkForRepeatMeetingCounts(params) == 0) {
							sError.append("This meeting can not be set as a repeat meeting due to incorrect end date.");
						}

						Timestamp repStart = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000");
						Timestamp repEnd = Timestamp.valueOf(sRepEndYear + "-" + sRepEndMonth + "-" + sRepEndDay + " " + sCalEndHour + ":" + sCalEndMinutes + ":00.000000000");

						if (repStart.before(repEnd)) {

						} else {
							sError.append("Meeting date is greater then repeat end date. Please set repeat end appropriately.<br />");
						}						
					}
				}

				if (timeEvent < timeNow && sError.toString().trim().equals("")) {
					sError.append("<b>This meeting entry is being created in the past. Click submit to create anyway.</b><br />");
					out.println("<input type=\"hidden\" name=\"submit_anyway\" value=\"Y\" />");
				}


				String sCalWeb = ETSUtils.checkNull(request.getParameter("cal_web"));
				String sCalCC = ETSUtils.checkNull(request.getParameter("cal_cc"));
				
				String sNotifyType = ETSUtils.checkNull(request.getParameter("notifyOption"));

				String sMeetingId = ETSUtils.checkNull(request.getParameter("meetid"));


				out.println("<input type=\"hidden\" name=\"meetid\" value=\"" + sMeetingId + "\" />");

				if (sMeetingId.trim().equals("")) {
					// new meeting entry..
					out.println("<input type=\"hidden\" name=\"etsop\" value=\"insertmeeting\" />");
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">Schedule a new meeting</td></tr></table>");
				} else {
					out.println("<input type=\"hidden\" name=\"etsop\" value=\"updatemeeting\" />");
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">Edit meeting details</td></tr></table>");
				}

				out.println("<br />");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><span style=\"color:#ff3333\">" + sError.toString() + "<span></td>");
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

				out.println("</table>");
				out.println("<br />");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
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

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_subject\">Subject</label></b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_subject\" id=\"label_sub\" class=\"iform\" maxlength=\"100\" size=\"45\" value=\"" + sCalSubject + "\" /></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_desc\">Description</label></b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><textarea name=\"cal_desc\" cols=\"47\" rows=\"5\"  class=\"iform\" maxlength=\"1000\" id=\"label_desc\">" + sCalDesc + "</textarea></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

                // Callin number & passcode...
                    // Callin number...
                out.println("<tr valign=\"top\">");
                out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_subject\">Call-in number:</label></b></td>");
                out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_callin\" id=\"label_sub\" class=\"iform\" maxlength=\"100\" size=\"45\" value=\"" + sCallIn + "\" /></td>");
                out.println("</tr>");
                    // Passcode...
                out.println("<tr>");
                out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
                out.println("</tr>");
                out.println("<tr valign=\"top\">");
                out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_subject\">Passcode:</label></b></td>");
                out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_passcode\" id=\"label_sub\" class=\"iform\" maxlength=\"100\" size=\"45\" value=\"" + sPass + "\" /></td>");
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
                out.println("</tr>");

                //...for 4.2.1 relase

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"92\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_date\">Meeting date:</label></b></td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">");

				showDate(out,sCalMonth,sCalDay,sCalYear,"cal_month","cal_day","cal_year","label_date");

				out.println("</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_date\">Starts:</label></b></td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">");

				//showDate(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10), sTempDate.substring(0, 4),"cal_month","cal_day","cal_year","label_date");
				showTime(out,sCalHour,sCalMinutes,sCalAMPM,"cal_hour","cal_min","cal_ampm","label_date");

				out.println("</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_edate\">Ends:</label></b></td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">");

				//showDate(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10), sTempDate.substring(0, 4),"cal_month","cal_day","cal_year","label_date");
				showTime(out,sCalEndHour,sCalEndMinutes,sCalEndAMPM,"cal_ehour","cal_emin","cal_eampm","label_edate");

				out.println("</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_invitees\">Invitees</label></b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><span class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</span><br />" + displayInviteesAsSelect(con,"cal_invitees","label_invitees",proj.getProjectId(),sInviteesId, es.gIR_USERN) + "</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_cc\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=copyto&proj_type=" + proj.getProjectType() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=copyto&proj_type=" + proj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=copyto&proj_type=" + proj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Copy to</a></label></b>:</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"text\" name=\"cal_cc\"  class=\"iform\" id=\"label_cc\" maxlength=\"1000\" size=\"45\" value=\"" + sCalCC + "\" /></td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\">&nbsp;</td>");
				out.println("<td headers=\"\" width=\"343\" align=\"left\">(Add multiple e-mail addresses by separating them with a comma. Example: user1@domain.com, user2@domain.com)</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_web\">Web conference</label></b>:</td>");
				if (sCalWeb.trim().equalsIgnoreCase("Y")) {
					out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"checkbox\" name=\"cal_web\"  class=\"iform\" id=\"label_web\" value=\"Y\" checked=\"checked\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"343\" align=\"left\"><input type=\"checkbox\" name=\"cal_web\"  class=\"iform\" id=\"label_web\" value=\"Y\" /></td>");
				}
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");


				out.println("<tr>");
				out.println("<td headers=\"\"  width=\"100\"><b><a href=\"ETSContentManagerServlet.wss?OP=Notification%20help&BlurbField=E-mail%20notification%20option&proj=" + proj.getProjectId() + "\" onclick=\"window.open('ETSContentManagerServlet.wss?OP=Notification%20help&BlurbField=E-mail%20notification%20option&proj=" + proj.getProjectId() + "','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\" onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Notification%20help&BlurbField=E-mail%20notification%20option&proj=" + proj.getProjectId() + "','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >E-mail notification option</a>:</b></td>");
				if (sNotifyType.equalsIgnoreCase("T")) {
					out.println("<td headers=\"\" width=\"343\" align=\"left\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\" checked=\"checked\" /><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" /><label for=\"notifyOption2\">Bcc</label></td>");
				} else {
					out.println("<td headers=\"\" width=\"343\" align=\"left\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\"/><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" checked=\"checked\" /><label for=\"notifyOption2\">Bcc</label></td>");
				}
				out.println("</tr>");
				
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");
				

				if (sMeetingId.trim().equals("")) {
					
					// display documents information only if a new meeting...

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"100\" align=\"left\"><b>Documents</b>:</td>");
					out.println("<td headers=\"\" width=\"343\" align=\"left\">You must click <b>Submit</b> before you can attach documents to this meeting.</td>");
					out.println("</tr>");
	
					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
					out.println("</tr>");
				}

				if (sMeetingId.trim().equals("")) {

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\" align=\"left\">If you would like to schedule this meeting as a repeat meeting, please complete the section below.</td>");
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

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"100\" align=\"left\"><label for=\"label_repeat\"><b>Repeats:</b></label></td>");
					out.println("<td headers=\"\" width=\"343\" align=\"left\"><select id=\"label_repeat\" class=\"iform\" name=\"repeat\">");
					out.println("<option value=\"NONE\" selected=\"selected\">None</option>");
					if (sRepeat.trim().equalsIgnoreCase("Daily")) {
						out.println("<option value=\"DAILY\" selected=\"selected\">Daily</option>");
					} else {
						out.println("<option value=\"DAILY\">Daily</option>");
					}
					if (sRepeat.trim().equalsIgnoreCase("Weekly")) {
						out.println("<option value=\"WEEKLY\" selected=\"selected\">Weekly</option>");
					} else {
						out.println("<option value=\"WEEKLY\">Weekly</option>");
					}
					if (sRepeat.trim().equalsIgnoreCase("Monthly")) {
						out.println("<option value=\"MONTHLY\" selected=\"selected\">Monthly</option>");
					} else {
						out.println("<option value=\"MONTHLY\">Monthly</option>");
					}

					out.println("</select></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"100\" align=\"left\"><b><label for=\"label_repeat_end_date\">Repeat end date:</label></b></td>");
					out.println("<td headers=\"\" width=\"343\" align=\"left\">");
					showDate(out,sRepEndMonth,sRepEndDay,sRepEndYear,"end_month","end_day","end_year","label_repeat_end_date");
					out.println("</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
					out.println("</tr>");

				} else {
					// display the repeat information here.. not editable.

					Vector vMeetings = ETSCalendar.getMeetingsDetails(con,proj.getProjectId(),sMeetingId);

					if (vMeetings != null && vMeetings.size() > 0) {

						for (int i = 0; i < vMeetings.size(); i++) {

							ETSCal calEntry = (ETSCal) vMeetings.elementAt(i);

							if (calEntry.getSRepeatType() != null && !calEntry.getSRepeatType().equalsIgnoreCase("N")) {

								// this is a repeat meetind.. just display the entries.

//								out.println("<tr>");
//								out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
//								out.println("</tr>");

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
								out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
								out.println("</tr>");

								String sRepeatNone = request.getParameter("repeat_none");
								if (sRepeatNone == null || sRepeatNone.trim().equalsIgnoreCase("")) {
									sRepeatNone = "N";
								}

								out.println("<tr valign=\"top\">");
								out.println("<td headers=\"\" width=\"100\" align=\"left\"><b>Apply changes to:</b></td>");
								out.println("<td headers=\"\" width=\"343\" align=\"left\">");
								if (sRepeatNone.equalsIgnoreCase("N")) {
									out.println("<input type=\"radio\" name=\"repeat_none\" value=\"N\" selected=\"selected\" checked=\"checked\" id=\"label_this\" /><label for=\"label_this\">This meeting only</label><br /><input type=\"radio\" name=\"repeat_none\" value=\"Y\" id=\"label_all\" /><label for=\"label_all\">All future instances from this meeting</label></td>");
								} else {
									out.println("<input type=\"radio\" name=\"repeat_none\" value=\"N\" id=\"label_this\" /><label for=\"label_this\">This meeting only</label><br /><input type=\"radio\" name=\"repeat_none\" value=\"Y\" selected=\"selected\" checked=\"checked\" id=\"label_all\" /><label for=\"label_all\">All future instances from this meeting</label></td>");
								}

								out.println("</tr>");

								out.println("<tr>");
								out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
								out.println("</tr>");

								out.println("<tr valign=\"top\">");
								out.println("<td headers=\"\" width=\"100\" align=\"left\"><b>Repeats:</b></td>");
								if (calEntry.getSRepeatType().equalsIgnoreCase("D")) {
									out.println("<td headers=\"\" width=\"343\" align=\"left\">Daily</td>");
								} else if (calEntry.getSRepeatType().equalsIgnoreCase("W")) {
									out.println("<td headers=\"\" width=\"343\" align=\"left\">Weekly</td>");
								} else if (calEntry.getSRepeatType().equalsIgnoreCase("D")) {
									out.println("<td headers=\"\" width=\"343\" align=\"left\">Monthly</td>");
								}

								out.println("</tr>");

								out.println("<tr>");
								out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
								out.println("</tr>");

								String sRepeatEndDate = df.format(calEntry.getSRepeatEnd());

								out.println("<tr valign=\"top\">");
								out.println("<td headers=\"\" width=\"100\" align=\"left\"><b>Repeat end date:</b></td>");
								out.println("<td headers=\"\" width=\"343\" align=\"left\">" + sRepeatEndDate + "</td>");
								out.println("</tr>");

								out.println("<tr>");
								out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
								out.println("</tr>");

							}

						}
					}

				}

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");


				out.println("<tr valign=\"top\"><td headers=\"\" colspan=\"4\" align=\"left\"><br />");
					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
					out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
					out.println("</tr>");
					out.println("</table>");
				out.println("</td></tr>");

				out.println("</table>");
				out.println("<br />");

				out.println("</form>");

			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}

		return sCalendarId;

	}

	/**
	 * Method validateMeeting.
	 * @param params
	 * @return boolean
	 */
	private static boolean validateMeeting(ETSParams params) throws Exception {

		try {

			HttpServletRequest request = params.getRequest();
			Connection conn = params.getConnection();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();

			String sCalSubject = ETSUtils.checkNull(request.getParameter("cal_subject"));
			if (sCalSubject.trim().equals("")) {
				return false;
			}

			String sCalDesc = ETSUtils.checkNull(request.getParameter("cal_desc"));
			if (sCalDesc.trim().equals("")) {
				return false;
			}

			String sCalMonth = ETSUtils.checkNull(request.getParameter("cal_month"));
			String sCalDay = ETSUtils.checkNull(request.getParameter("cal_day"));
			String sCalYear = ETSUtils.checkNull(request.getParameter("cal_year"));

			String sCalHour = ETSUtils.checkNull(request.getParameter("cal_hour"));
			if (sCalHour.trim().equals("")) sCalHour = "00";
			String sCalMinutes = ETSUtils.checkNull(request.getParameter("cal_min"));
			if (sCalMinutes.trim().equals("")) sCalMinutes = "00";

			String sCalAMPM = ETSUtils.checkNull(request.getParameter("cal_ampm"));
			if (sCalAMPM.trim().equals("")) {
				sCalAMPM = "AM";
				if (Integer.parseInt(sCalHour) == 12) {
					sCalHour = "00";
				}
			} else if (sCalAMPM.trim().equalsIgnoreCase("PM")) {
				if (Integer.parseInt(sCalHour) < 12) {
					sCalHour = String.valueOf(Integer.parseInt(sCalHour) + 12);
				}
			} else if (sCalAMPM.trim().equalsIgnoreCase("AM")) {
				if (Integer.parseInt(sCalHour) == 12) {
					sCalHour = "00";
				}
			}

			
			String sCalEndHour = ETSUtils.checkNull(request.getParameter("cal_ehour"));
			if (sCalEndHour.trim().equals("")) sCalEndHour = "00";
			String sCalEndMinutes = ETSUtils.checkNull(request.getParameter("cal_emin"));
			if (sCalEndMinutes.trim().equals("")) sCalEndMinutes = "00";

			String sCalEndAMPM = ETSUtils.checkNull(request.getParameter("cal_eampm"));
			if (sCalEndAMPM.trim().equals("")) {
				sCalEndAMPM = "AM";
				if (Integer.parseInt(sCalEndHour) == 12) {
					sCalEndHour = "00";
				}
			} else if (sCalEndAMPM.trim().equalsIgnoreCase("PM")) {
				if (Integer.parseInt(sCalEndHour) < 12) {
					sCalEndHour = String.valueOf(Integer.parseInt(sCalEndHour) + 12);
				}
			} else if (sCalEndAMPM.trim().equalsIgnoreCase("AM")) {
				if (Integer.parseInt(sCalEndHour) == 12) {
					sCalEndHour = "00";
				}
			}

			Timestamp timeStart = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000");
			Timestamp timeEnd = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalEndHour + ":" + sCalEndMinutes + ":00.000000000");

			if (timeStart.before(timeEnd)) {

			} else {
				return false;
			}


			String sCalInvitees[] = request.getParameterValues("cal_invitees");
			String sInviteesId = "";
			if (sCalInvitees == null || sCalInvitees.length <= 0) {
				return false;
			}


			String sRepeat = request.getParameter("repeat");
			if (sRepeat == null || sRepeat.trim().equalsIgnoreCase("")) {
				sRepeat = "";
			} else {
				sRepeat = sRepeat.trim();

				// checks valid end date for repeat meeting - thanga
				if (sRepeat.trim().equalsIgnoreCase("Daily") || sRepeat.trim().equalsIgnoreCase("Weekly") || sRepeat.trim().equalsIgnoreCase("Monthly")) {
					if(checkForRepeatMeetingCounts(params)==0) {
						return false;
					}
	
					String sRepEndMonth = ETSUtils.checkNull(request.getParameter("end_month"));
					String sRepEndDay = ETSUtils.checkNull(request.getParameter("end_day"));
					String sRepEndYear = ETSUtils.checkNull(request.getParameter("end_year"));
	
					Timestamp repStart = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000");
					Timestamp repEnd = Timestamp.valueOf(sRepEndYear + "-" + sRepEndMonth + "-" + sRepEndDay + " " + sCalEndHour + ":" + sCalEndMinutes + ":00.000000000");
	
					if (repStart.before(repEnd)) {
	
					} else {
						return false;
					}
				}
			}


			String sSubmitAnyway = ETSUtils.checkNull(request.getParameter("submit_anyway"));

			if (!sSubmitAnyway.trim().equalsIgnoreCase("Y")) {
				// check to make sure the alert and event are not posted for a time before the current date.
				long timeEvent = timeStart.getTime();
				long timeNow = new Timestamp(System.currentTimeMillis()).getTime();

				if (timeEvent < timeNow) {
					return false;
				}
			}



		} catch (Exception e) {
			throw e;
		}

		return true;
	}


	private static void showTime(PrintWriter out, String hh, String mm, String ampm, String hhname, String mmname, String ampmname,String sTimeId) {

		out.println("<table summary=\"\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td headers=\"\"  align=\"left\" width=\"50\"><label for=\"hour\">Hour</label><br />");
		out.println("<select id=\"" + sTimeId + " hour\" class=\"iform\" name=" + hhname + ">");

		for (int k = 1; k <= 12; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (hh.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select></td><td headers=\"\" width=\"50\" align=\"left\"><label for=\"min\">Min</label><br /><select id=\"" + sTimeId + " min\" class=\"iform\" name=" + mmname + ">");
		for (int k = 0; k <= 59; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (mm.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select></td><td headers=\"\"  align=\"left\"><label for=\"label_tim\">AM/PM</label><br /><select id=\"" + sTimeId + " label_tim\" class=\"iform\" name=" + ampmname + ">");
		out.println("<option value=\"AM\"");
		if (ampm.equalsIgnoreCase("AM"))
			out.println(" selected=\"selected\" ");
		out.println(">AM</option>");
		out.println("<option value=\"PM\"");
		if (ampm.equalsIgnoreCase("PM"))
			out.println(" selected=\"selected\" ");
		out.println(">PM</option>");

		out.println("</select>&nbsp;&nbsp;Eastern US");
		out.println("</td></tr></table>");

	}

	/**
	 * Method sendEmailNotification.
	 * @param conn
	 * @param sInsFlag
	 * @param sProjectId
	 * @param sCalendarId
	 * @param sCalendarEntry
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void sendUpdateEmailNotification(Connection conn, EdgeAccessCntrl es, String sInsFlag, String sProjectId, String sCalendarId, String sCalendarEntry, Vector vAffectedEntries, String sLinkId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		StringBuffer sEmailStr = new StringBuffer("");
	    char newline = '\n';
	    char replacechar = ' ';


		try {

			sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,NOTIFY_TYPE FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = '" + sCalendarEntry + "' AND PROJECT_ID = '" + sProjectId + "' AND CALENDAR_ID = '" + sCalendarId + "' ");
			sQuery.append("for READ ONLY ");

			logger.debug("ETSCalendar::getMeetingsDetails()::QUERY : " + sQuery.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sProjectID = sProjectId;
				String sCalType = sCalendarEntry;
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
                String sCallIn = ETSUtils.checkNull(rs.getString("CALLIN_NUMBER"));
                String sPass = ETSUtils.checkNull(rs.getString("PASS_CODE"));
				String sRepeatType = ETSUtils.checkNull(rs.getString("REPEAT_TYPE"));
				String sRepeatID = ETSUtils.checkNull(rs.getString("REPEAT_ID"));
				Timestamp tRepeatEnd = rs.getTimestamp("REPEAT_END");
				String sNotifyType = ETSUtils.checkNull(rs.getString("NOTIFY_TYPE"));


				String sTempDate = tStartTime.toString();

				String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
				String sTime = sTempDate.substring(11, 16);

				String sHour = sTempDate.substring(11, 13);
				String sMin = sTempDate.substring(14, 16);
				String sAMPM = "AM";

				if (Integer.parseInt(sHour) == 0) {
					sHour = "12";
					sAMPM = "AM";
				} else if (Integer.parseInt(sHour) == 12) {
					sHour = String.valueOf(Integer.parseInt(sHour));
					if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
					sAMPM = "PM";
				} else if (Integer.parseInt(sHour) > 12) {
					sHour = String.valueOf(Integer.parseInt(sHour) - 12);
					if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
					sAMPM = "PM";
				}

				long lTime = tStartTime.getTime() + (iDuration * 60 * 1000);

				Timestamp timeEnd = new Timestamp(lTime);
				String sEndDate = timeEnd.toString();

				String sHour1 = sEndDate.substring(11, 13);
				String sMin1 = sEndDate.substring(14, 16);
				String sAMPM1 = "AM";

				if (Integer.parseInt(sHour1) == 0) {
					sHour1 = "12";
					sAMPM1 = "AM";
				} else if (Integer.parseInt(sHour1) == 12) {
					sHour1 = String.valueOf(Integer.parseInt(sHour1));
					if (Integer.parseInt(sHour1) < 10) sHour1 = "0" + sHour1;
					sAMPM1 = "PM";
				} else if (Integer.parseInt(sHour1) > 12) {
					sHour1 = String.valueOf(Integer.parseInt(sHour1) - 12);
					if (Integer.parseInt(sHour1) < 10) sHour1 = "0" + sHour1;
					sAMPM1 = "PM";
				}

				ETSProj proj = ETSUtils.getProjectDetails(conn,sProjectId);
				
				UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());
				String strCustConnect = "";
			    if ("Collaboration Center".equalsIgnoreCase(prop.getAppName())) {
			        strCustConnect = "Customer Connect ";
			    }

				String sEmailSubject = "";

				if (sInsFlag.trim().equals("I")) {
					sEmailSubject = ETSUtils.formatEmailSubject("IBM "+prop.getAppName() + " - Meeting Invitation: " + sSubject);
				} else {
					sEmailSubject = ETSUtils.formatEmailSubject("IBM "+prop.getAppName() + " - Meeting Update: " + sSubject);
				}

                int iTCForMeeting = ETSDatabaseManager.getTopCatId(sProjectId,Defines.MEETINGS_VT);

			    if (sInsFlag.trim().equals("I")) {
		    		sEmailStr.append("You are invited to a new meeting that has been scheduled in an IBM " +strCustConnect + prop.getAppName() + " for \nproject: " + proj.getName() + " \n\n");
			    } else {
		    		sEmailStr.append("An update has been made to the following meeting which you are a part of in an IBM " +strCustConnect + prop.getAppName() + " for \nproject: " + proj.getName() + " \n\n");
			    }
			    sEmailStr.append("The details of the meeting are as follows: \n\n");

				sEmailStr.append("===============================================================\n");

				sEmailStr.append("  Subject:        " + ETSUtils.formatEmailStr(sSubject) + "\n");
				sEmailStr.append("  Start date:     " + sDate + "\n");
				sEmailStr.append("  Start time:     " + sHour + ":" + sMin + sAMPM.toLowerCase() + " Eastern US \n");
				sEmailStr.append("  End time:       " + sHour1 + ":" + sMin1 + sAMPM1.toLowerCase() + " Eastern US \n");
				sEmailStr.append("  Organized by:   " + ETSUtils.getUsersName(conn,sScheduleBy) + " \n\n");

				if (sRepeatType != null && !sRepeatType.trim().equalsIgnoreCase("N")) {
					if (sRepeatType.trim().equalsIgnoreCase("D")) {
						sEmailStr.append("  Repeats:        Daily \n");
					} else if (sRepeatType.trim().equalsIgnoreCase("W")) {
						sEmailStr.append("  Repeats:        Weekly \n");
					} else if (sRepeatType.trim().equalsIgnoreCase("M")) {
						sEmailStr.append("  Repeats:        Monthly \n");
					}
					sEmailStr.append("  Repeat end date:" + ETSUtils.formatDate(tRepeatEnd) + " \n\n");
				}


				if (sInsFlag.trim().equals("U")) {

					Vector vAffectedMeetings = ETSCalendar.getAffectedMeetingDetails(conn,sProjectId,vAffectedEntries);

					if (vAffectedMeetings != null && vAffectedMeetings.size() > 1) {

						// means this is a repeat entry and more than one meeting has been affected.
						sEmailStr.append("===============================================================\n");
						sEmailStr.append("The update is applicable to all future instances of this meeting. \n");
						sEmailStr.append("===============================================================\n\n");

					} else {
						sEmailStr.append("===============================================================\n");
						sEmailStr.append("The update is applicable to this instance only. \n");
						sEmailStr.append("===============================================================\n\n");
					}
				}

				sEmailStr.append("To view the meeting details, click on the following URL and log-in:\n");
				sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + sProjectId + "&tc=" + iTCForMeeting + "&etsop=viewmeeting&meetid=" + sCalID + "&linkid=" + sLinkId + "\n\n");

				sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));
				
//				sEmailStr.append("===============================================================\n");
//				sEmailStr.append("Delivered by E&TS Connect. \n");
//				sEmailStr.append("This is a system generated email. \n");
//				sEmailStr.append("===============================================================\n\n");

				String sToList = "";

                StringTokenizer st = new StringTokenizer(sInvitees,",");
                while (st.hasMoreTokens()) {
                    if (sToList.trim().equals("")) {
                        sToList = ETSUtils.getUserEmail(conn,st.nextToken());
                    } else {
                        sToList = sToList + "," + ETSUtils.getUserEmail(conn,st.nextToken());
                    }
                }

                sToList = sToList + "," + es.gEMAIL;

				boolean bSent = false;

				if (!sToList.trim().equals("")) {
					
					if (sNotifyType.equalsIgnoreCase("T")) {
						bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,sCCList,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
					} else {
						bSent = ETSUtils.sendEMail(es.gEMAIL,"",sCCList,sToList,Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
					}
					
					
					
					
					if (sInsFlag.trim().equals("I")) {
						ETSUtils.insertEmailLog(conn,"MEETING",sCalendarId,"ADD",es.gEMAIL,sProjectId,sEmailSubject,sToList,sCCList);
					} else {
						ETSUtils.insertEmailLog(conn,"MEETING",sCalendarId,"UPDATE",es.gEMAIL,sProjectId,sEmailSubject,sToList,sCCList);
					}

				}
			}

		} catch (SQLException e) {
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

	}

	private static String insertSpacesForLongString(String sInString) {

		StringBuffer sOutString = new StringBuffer("");

		try {

			for (int i =0; i < sInString.length(); i++) {
				if ((sInString.substring(i,i+1)).trim().equalsIgnoreCase(",")){
					sOutString.append(", ");
				} else {
					sOutString.append(sInString.substring(i,i+1));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			sOutString.append(sInString);
		}

		return sOutString.toString();


	}



	/**
	 * @param params
	 * @return iRepeatCount
	 */
	public static int checkForRepeatMeetingCounts(ETSParams params) {
	// validation start date & end date for repeat meeting - thanga

		StringBuffer sError = new StringBuffer("");
		HttpServletRequest request = params.getRequest();
		String sRepeat = request.getParameter("repeat");
		if (sRepeat == null || sRepeat.trim().equalsIgnoreCase("")) {
			sRepeat = "";
		} else {
			sRepeat = sRepeat.trim();
		}

		String sCalMonth = ETSUtils.checkNull(request.getParameter("cal_month"));
		String sCalDay = ETSUtils.checkNull(request.getParameter("cal_day"));
		String sCalYear = ETSUtils.checkNull(request.getParameter("cal_year"));

		String sRepEndMonth = ETSUtils.checkNull(request.getParameter("end_month"));
		String sRepEndDay = ETSUtils.checkNull(request.getParameter("end_day"));
		String sRepEndYear = ETSUtils.checkNull(request.getParameter("end_year"));
		int iRepeatCount=0;

		if (sRepeat.trim().equalsIgnoreCase("Daily") || sRepeat.trim().equalsIgnoreCase("Weekly") || sRepeat.trim().equalsIgnoreCase("Monthly")) {

			boolean bCompleted = false;
			String sMeetDay = sCalDay;

			while (!bCompleted) {

				if (sRepeat.trim().equalsIgnoreCase("Daily")) {
					// add a day
					sCalDay = String.valueOf(Integer.parseInt(sCalDay) + 1);

					Timestamp timeMeeting = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 00:00:00.000000000");
					Timestamp repEnd = Timestamp.valueOf(sRepEndYear + "-" + sRepEndMonth + "-" + sRepEndDay + " 01:00:00.000000000");

					if (timeMeeting.before(repEnd)) {
						iRepeatCount++;
					} else {
						bCompleted = true;
					}

				} else if (sRepeat.trim().equalsIgnoreCase("Weekly")) {
					// add a 7 days
					sCalDay = String.valueOf(Integer.parseInt(sCalDay) + 7);

					Timestamp timeMeeting = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 00:00:00.000000000");
					Timestamp repEnd = Timestamp.valueOf(sRepEndYear + "-" + sRepEndMonth + "-" + sRepEndDay + " 01:00:00.000000000");

					if (timeMeeting.before(repEnd)) {
						iRepeatCount++;
					} else {
						bCompleted = true;
					}

				} else if (sRepeat.trim().equalsIgnoreCase("Monthly")) {

					// add a month
					sCalMonth = String.valueOf(Integer.parseInt(sCalMonth) + 1);

					if (Integer.parseInt(sCalMonth) > 12) {
						sCalMonth = String.valueOf(1);
						sCalYear = String.valueOf(Integer.parseInt(sCalYear) + 1);
					}

					Calendar cal1 = Calendar.getInstance();
					cal1.set(Calendar.YEAR,Integer.parseInt(sCalYear));
					cal1.set(Calendar.MONTH,Integer.parseInt(sCalMonth) -1);

					int iMaxDaysInMonth =  cal1.getActualMaximum(Calendar.DAY_OF_MONTH);
					int iMinDaysInMonth = cal1.getActualMinimum(Calendar.DAY_OF_MONTH);


					boolean bCorrectDay = false;

					sCalDay = sMeetDay;

					while (bCorrectDay == false) {
						if(Integer.parseInt(sCalDay) > iMaxDaysInMonth){
							sCalDay = String.valueOf(Integer.parseInt(sCalDay) -1);
						} else {
							bCorrectDay = true;
						}
					}

					Timestamp timeMeeting = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 00:00:00.000000000");
					Timestamp repEnd = Timestamp.valueOf(sRepEndYear + "-" + sRepEndMonth + "-" + sRepEndDay + " 01:00:00.000000000");

					if (timeMeeting.before(repEnd)) {
						iRepeatCount++;
					} else {
						bCompleted = true;
					}

				}

			}
		}
		return iRepeatCount;
		}
}