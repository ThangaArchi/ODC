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

/**
 * @author: Sathish
 */

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSCalDisplayServlet extends javax.servlet.http.HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.15";
	
	private static Log logger = EtsLogger.getLogger(ETSProjectsServlet.class);

	/**
	 * @see java.lang.Object#Object()
	 */
	public ETSCalDisplayServlet() {
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
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
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

			String sCalType = request.getParameter("caltype");
			if (sCalType == null || sCalType.trim().equals("")) {
				sCalType = "";
			} else {
				sCalType = sCalType.trim();
			}

			String sCalId = request.getParameter("calid");
			if (sCalId == null || sCalId.trim().equals("")) {
				sCalId = "";
			} else {
				sCalId = sCalId.trim();
			}

			params = ETSUtils.getServletParameters(request);

			ETSProj proj = ETSUtils.getProjectDetails(conn,sProjId);
			
			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());

			PopupHeaderFooter header = new PopupHeaderFooter();
			header.setPageTitle(unBrandedprop.getAppName() + " - Details");
			//header.setHeader("");
			out.println(header.printPopupHeader());
			//out.println(header.printSubHeader());
			if (sCalType.equalsIgnoreCase("A")) {
				if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
					ETSUtils.popupHeaderLeft("Message details","",out);
				} else {
					ETSUtils.popupHeaderLeft("Message details","",out);
				}
			} else if (sCalType.equalsIgnoreCase("E")) {
				ETSUtils.popupHeaderLeft("Event details","",out);
			} else if (sCalType.equalsIgnoreCase("M")) {
				ETSUtils.popupHeaderLeft("Meeting details","",out);
			}

			displayCalendarEntry(conn,out,proj,sProjId,sCalType,sCalId);

			ETSUtils.popupHeaderRight(out);

			out.println(header.printPopupFooter());


		} catch (SQLException e) {
			logger.error(this,e);
		} catch (Exception e) {
			logger.error(this,e);
		} finally {
			ETSDBUtils.close(conn);
			out.flush();
			out.close();
		}
	}

	/**
	 * Method displayCalendarEntry.
	 * @param con
	 * @param out
	 * @param sProjectId
	 * @param sCalendarType
	 * @param sCalendarId
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayCalendarEntry(Connection con, PrintWriter out, ETSProj proj, String sProjectId, String sCalendarType, String sCalendarId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		try {

			sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = '" + sCalendarType + "' AND PROJECT_ID = '" + sProjectId + "' AND ");
			sQuery.append("CALENDAR_ID = '" + sCalendarId + "' for READ ONLY ");

			logger.debug("ETSCalDisplayServlet::getUpcomingEvents()::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sProjectID = sProjectId;
				String sCalType = sCalendarType;
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
				Timestamp sEndDateTime = rs.getTimestamp("REPEAT_END");

				//This field is required for the duration of the event
				if ((sEndDateTime == null)||(sEndDateTime.equals(""))){
					sEndDateTime = tStartTime;
				}
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
				cal.setSRepeatEnd(sEndDateTime);

				String sTempDate = cal.getSStartTime().toString();
				String sTmpEndDate = cal.getSRepeatEnd().toString();

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

				String sScheduleDate = cal.getSScheduleDate().toString();
//
				String sSDate = sScheduleDate.substring(5, 7) + "/" + sScheduleDate.substring(8, 10) + "/" + sScheduleDate.substring(0, 4);
//				String sSTime = sScheduleDate.substring(11, 16);
//
//				String sSHour = sScheduleDate.substring(11, 13);
//				String sSMin = sScheduleDate.substring(14, 16);
//				String sSAMPM = "AM";
//
//				if (Integer.parseInt(sSHour) > 12) {
//					sSHour = String.valueOf(Integer.parseInt(sSHour) - 12);
//                    if (Integer.parseInt(sHour) < 10) sHour = "0" + sHour;
//					sSAMPM = "PM";
//				}

				out.println("<br />");

				if (sCalendarType.equalsIgnoreCase("A")) {

					// alerts

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr><td headers=\"\" width=\"100%\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
					out.println("<tr>");
						if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
							out.println("<td headers=\"\" class=\"tblue\" height=\"18\" width=\"100%\"><b>&nbsp;&nbsp;Message details</b></td>");
						} else {
							out.println("<td headers=\"\" class=\"tblue\" height=\"18\" width=\"100%\"><b>&nbsp;&nbsp;Message details</b></td>");
						}

					out.println("</tr>");
					out.println("</table>");

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" class=\"tblue\" valign=\"top\" width=\"100%\">");
					out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");
					out.println("<tr><td headers=\"\"  style=\"background-color: #ffffff; color: #000000; font-weight: normal\" width=\"100%\">");
					out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Title:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + cal.getSSubject() + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Description:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + cal.getSDescription() + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Date:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + sDate + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Expires in:</b></td>");
					out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + String.valueOf(cal.getIDuration()) + " days.</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>By:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + ETSUtils.getUsersName(con,cal.getSScheduleBy()) + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Last updated on:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + sSDate + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("</table>");
					out.println("</td></tr></table>");
					out.println("</td></tr></table>");

				} else if (sCalendarType.equalsIgnoreCase("E")) {

					// Events

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr><td headers=\"\" width=\"100%\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
					out.println("<tr>");
					out.println("<td headers=\"\" class=\"tblue\" height=\"18\" width=\"100%\"><b>&nbsp;&nbsp;Event details</b></td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" class=\"tblue\" valign=\"top\" width=\"100%\">");
					out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");
					out.println("<tr><td headers=\"\"  style=\"background-color: #ffffff; color: #000000; font-weight: normal\" width=\"100%\">");
					out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");


					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Title:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + cal.getSSubject() + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Description:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + cal.getSDescription() + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Date:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color:#666666\"><b>" + sDate + "</b>&nbsp;&nbsp;<span class=\"small\">" + sHour + ":" + sMin + sAMPM.toLowerCase() + "&nbsp;Eastern US&nbsp;to&nbsp;" + sEndHour + ":" + sEndMin +  sEndAMPM.toLowerCase() + "&nbsp;Eastern US</span></span></td>");
					out.println("</tr>");

					// divider					
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Repeats for:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + String.valueOf(cal.getIDuration()) + " days.</td>");
					out.println("</tr>");					

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>By:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + ETSUtils.getUsersName(con,cal.getSScheduleBy()) + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Last updated on:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + sSDate + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("</table>");
					out.println("</td></tr></table>");
					out.println("</td></tr></table>");

				} else if (sCalendarType.equalsIgnoreCase("M")) {
					// meeting entry

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr><td headers=\"\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
					out.println("<tr>");
					out.println("<td headers=\"\" class=\"tblue\" height=\"18\"><b>&nbsp;&nbsp;Meeting details</b></td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" class=\"tblue\" valign=\"top\">");
					out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");
					out.println("<tr><td headers=\"\"  style=\"background-color: #ffffff; color: #000000; font-weight: normal\" width=\"100%\">");
					out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");


					if (cal.getSCancelFlag().trim().equalsIgnoreCase("Y")) {

						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"2\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #ff3333\"><b>Please note that this meeting has been cancelled.</b></span></td>");
						out.println("</tr>");

						// divider
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
						out.println("</tr>");
					}

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Subject:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + cal.getSSubject() + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Description:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + cal.getSDescription() + "</td>");
					out.println("</tr>");

					/*
                    // divider
                    out.println("<tr valign=\"top\">");
                    out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
                    out.println("</tr>");

                    out.println("<tr>");
                    out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Call-in number:</b></td>");
                    out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + cal.getSCallIn() + "</td>");
                    out.println("</tr>");

                    // divider
                    out.println("<tr valign=\"top\">");
                    out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
                    out.println("</tr>");

                    out.println("<tr>");
                    out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Pass code:</b></td>");
                    out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + cal.getSPass() + "</td>");
                    out.println("</tr>");
                    */

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");
					
					//for meeting this is duration of meeting in minutes					
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(cal.getSStartTime().getTime());
					c.add(Calendar.MINUTE, iDuration);
					SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Date & Time:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color:#666666\"><b>" + sDate + "</b>&nbsp;&nbsp;<span class=\"small\">" + sHour + ":" + sMin + "&nbsp;" + sAMPM.toLowerCase() + "&nbsp;to&nbsp;" + sdf.format(c.getTime())+ "&nbsp;Eastern US&nbsp;</span></span></td>");
					out.println("</tr>");

					// divider
					/*
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Duration:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + String.valueOf(cal.getIDuration()) + " minutes.</td>");
					out.println("</tr>"); */

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Invitees:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + displayInvitees(con, cal.getSInviteesID()) + "</span></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"></td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>CC list:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + displayInvitees(con, cal.getSCCList()) + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Web conference:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + cal.getSWebFlag() + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>By:</b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + ETSUtils.getUsersName(con,cal.getSScheduleBy()) + "</td>");
					out.println("</tr>");

					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
					out.println("</tr>");

					out.println("</table>");
					out.println("</td></tr></table>");
					out.println("</td></tr></table>");

				}


			} else {
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"100%\"><b>Calendar entry not found.</b></td></tr></table>");
			}

			out.println("<br /><br />");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

	}
	static public String displayInvitees(Connection con, String invitee_list) throws Exception
	{
		String in_list = "";
		StringTokenizer st = new StringTokenizer(invitee_list,",");
		
		while(st.hasMoreElements())
		{
			String temp = (String) st.nextElement();
			String temp1 = ETSUtils.getUsersName(con, temp);
			in_list =  in_list + "<br />" + temp1;
		}
		return in_list;
	}
}