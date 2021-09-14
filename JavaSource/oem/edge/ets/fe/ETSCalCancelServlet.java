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
/*     US Government Users Restricted Rights                                 */
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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.common.Global;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSCalCancelServlet extends javax.servlet.http.HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.27";
	
	private static Log logger = EtsLogger.getLogger(ETSCalCancelServlet.class);


	public ETSCalCancelServlet() {
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

			String sSubmitFlag = request.getParameter("image_submit.x");
			if (sSubmitFlag == null || sSubmitFlag.trim().equals("")) {
				sSubmitFlag = "";
			} else {
				sSubmitFlag = sSubmitFlag.trim();
			}

			ETSProj proj = ETSUtils.getProjectDetails(conn,sProjId);
			
			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());

			params = ETSUtils.getServletParameters(request);

			PopupHeaderFooter header = new PopupHeaderFooter();
			//header.setHeader("");
			header.setPageTitle(unBrandedprop.getAppName() + " - Details");
			out.println(header.printPopupHeader());

			if (sCalType.equalsIgnoreCase("A")) {
				if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
					ETSUtils.popupHeaderLeft("Message details","Cancel Message",out);
				} else {
					ETSUtils.popupHeaderLeft("Message details","Cancel message",out);
				}
			} else if (sCalType.equalsIgnoreCase("E")) {
				ETSUtils.popupHeaderLeft("Events details","Cancel event",out);
			} else if (sCalType.equalsIgnoreCase("M")) {
				ETSUtils.popupHeaderLeft("Meeting details","Cancel meeting",out);
			}


			out.println("<form name=\"CalendarCancel\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSCalCancelServlet.wss\">");

			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");
			
			out.println("<script type=\"text/javascript\" language=\"javascript\">function checkDoc1(){");
						
			out.println("x=document.getElementById(\"radio_this\");");
			out.println("y=document.getElementById(\"radio_all\");");
			out.println("if(document.CalendarCancel.del_mtg_docs.checked){");
			out.println("if(x.disabled) { x.disabled=false; }");
			out.println("if(y.disabled) { y.disabled=false; }");
			out.println("document.CalendarCancel.del_mtg_docs.value=\"Y\";");
			out.println("}else{");
			out.println("if(!x.disabled) { x.disabled=true; }");
			out.println("if(!y.disabled) { y.disabled=true; }");
			out.println("document.CalendarCancel.del_mtg_docs.value=\"N\";");
			out.println("}");
			out.println("}");
			
			out.println("function checkDoc2(){");
			out.println("if(document.CalendarCancel.del_mtg_docs.checked){");
			out.println("document.CalendarCancel.del_mtg_docs.value=\"Y\";");
			out.println("}else{");
			out.println("document.CalendarCancel.del_mtg_docs.value=\"N\";");
			out.println("}");
			
			out.println("} </script>");
			
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"caltype\" value=\"" + sCalType + "\" />");
			out.println("<input type=\"hidden\" name=\"calid\" value=\"" + sCalId + "\" />");

			if (!sSubmitFlag.trim().equals("")) {

				cancelCalendarEntry(conn,out,request,es);

			    out.println("<br /><br />");
			    out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"30\" align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" /></td><td headers=\"\"  align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\">Ok</a></a></td></tr></table>");
			    out.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");

			} else {
				displayCalendarEntry(conn,out,proj,sProjId,sCalType,sCalId);
			}

			out.println("</form>");
			out.println("<br /><br />");

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

			sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = '" + sCalendarType + "' AND PROJECT_ID = '" + sProjectId + "' AND ");
			sQuery.append("CALENDAR_ID = '" + sCalendarId + "' for READ ONLY ");

			logger.debug("ETSCalCancelServlet::displayCalendarEntry()::QUERY : " + sQuery.toString());

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
                String sCallIn = ETSUtils.checkNull(rs.getString("CALLIN_NUMBER"));
                String sPass = ETSUtils.checkNull(rs.getString("PASS_CODE"));
				String sRepeatType = ETSUtils.checkNull(rs.getString("REPEAT_TYPE"));
				String sRepeatID = ETSUtils.checkNull(rs.getString("REPEAT_ID"));
				Timestamp tRepeatEnd = rs.getTimestamp("REPEAT_END");

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
                cal.setSCallIn(sCallIn);
                cal.setSPass(sPass);
                cal.setSRepeatType(sRepeatType);
                cal.setSRepeatID(sRepeatID);
                cal.setSRepeatEnd(tRepeatEnd);

				String sTempDate = cal.getSStartTime().toString();
	
				String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
				String sTime = sTempDate.substring(11, 16);
	
				String sHour = sTempDate.substring(11, 13);
				String sMin = sTempDate.substring(14, 16);
				String sAMPM = "AM";
				
				String sRepeatValue = "";
				if (sRepeatID.trim().equalsIgnoreCase("")) {
					sRepeatValue = "THIS-IS-NOT-A-REPEAT";
				} else {
					sRepeatValue = sRepeatID;
				}
				
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

				out.println("<br />");

				out.println("<input type=\"hidden\" name=\"cal_notify\" value=\"" + sEmailFlag + "\" />");

				if (sCalendarType.equalsIgnoreCase("A")) {

					// alerts

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr><td headers=\"\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
					out.println("<tr>");
						if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
							out.println("<td headers=\"\" class=\"tblue\" height=\"18\"><b>&nbsp;&nbsp;Message details</b></td>");
						} else {
							out.println("<td headers=\"\" class=\"tblue\" height=\"18\"><b>&nbsp;&nbsp;Message details</b></td>");
						}
					out.println("</tr>");
					out.println("</table>");

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" class=\"tblue\" valign=\"top\">");
					out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");
					out.println("<tr><td headers=\"\"  style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
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

					out.println("</table>");
					out.println("</td></tr></table>");
					out.println("</td></tr></table>");

					out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
						out.println("<td headers=\"\"  align=\"left\"><b>If you would like to cancel this Message, please click submit.</b>");
					} else {
						out.println("<td headers=\"\"  align=\"left\"><b>If you would like to cancel this message, please click submit.</b>");
					}
					out.println("</td></tr></table>");


				} else if (sCalendarType.equalsIgnoreCase("E")) {

					// Events



					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr><td headers=\"\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
					out.println("<tr>");
					out.println("<td headers=\"\" class=\"tblue\" height=\"18\"><b>&nbsp;&nbsp;Event details</b></td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" class=\"tblue\" valign=\"top\">");
					out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");
					out.println("<tr><td headers=\"\"  style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
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
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color:#666666\"><b>" + sDate + "</b>&nbsp;&nbsp;<span class=\"small\">" + sHour + ":" + sMin + " " + sAMPM.toLowerCase() + "</span></span></td>");
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

					out.println("</table>");
					out.println("</td></tr></table>");
					out.println("</td></tr></table>");

					out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"\"  align=\"left\"><b>If you would like to cancel this event, please click submit.</b>");
					out.println("</td></tr></table>");

				} else if (sCalendarType.equalsIgnoreCase("M")) {

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

					String sTempDate1 = cal.getSScheduleDate().toString();

					String sDate1 = sTempDate1.substring(5, 7) + "/" + sTempDate1.substring(8, 10) + "/" + sTempDate1.substring(0, 4);

					long lTime = cal.getSStartTime().getTime() + (cal.getIDuration() * 60 * 1000);

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
					out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><span class=\"subtitle\">" + cal.getSSubject() + "</span><br /><br /></td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Meeting date</b>:</td>");
					out.println("<td headers=\"\" width=\"132\" class=\"small\" align=\"left\">" + sDate + "</td>");
					out.println("<td headers=\"\" width=\"100\" class=\"small\" align=\"left\"><b>Organized by</b>:</td>");
					out.println("<td headers=\"\" width=\"119\" class=\"small\" align=\"left\">" + ETSUtils.getUsersName(con,cal.getSScheduleBy()) + "</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Start time</b>:</td>");
					out.println("<td headers=\"\" width=\"132\" class=\"small\" align=\"left\">" + sHour + ":" + sMin + sAMPM.toLowerCase() + " Eastern US</td>");
					out.println("<td headers=\"\" width=\"100\" class=\"small\" align=\"left\"><b>Post date</b>:</td>");
					out.println("<td headers=\"\" width=\"119\" class=\"small\" align=\"left\">" + sDate1 + "</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>End time</b>:</td>");
					out.println("<td headers=\"\" width=\"132\" class=\"small\" align=\"left\">" + sHour1 + ":" + sMin1 + sAMPM1.toLowerCase() + " Eastern US</td>");
					out.println("<td headers=\"\" width=\"100\" class=\"small\" align=\"left\">&nbsp;</td>");
					out.println("<td headers=\"\" width=\"119\" class=\"small\" align=\"left\">&nbsp;</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><br /></td>");
					out.println("</tr>");


					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Invitees</b>:</td>");
					out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">");

					StringTokenizer st = new StringTokenizer(cal.getSInviteesID(),",");
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
					out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">" + cal.getSCCList() + "</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><br /></td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Description</b>:</td>");
					out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">" + cal.getSDescription() + "</td>");
					out.println("</tr>");

                    out.println("<tr valign=\"top\">");
                    out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><br /></td>");
                    out.println("</tr>");

                    out.println("<tr valign=\"top\">");
                    out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Call-in number</b>:</td>");
                    out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">" + cal.getSCallIn() + "</td>");
                    out.println("</tr>");

                    out.println("<tr valign=\"top\">");
                    out.println("<td headers=\"\" colspan=\"4\" align=\"left\"><br /></td>");
                    out.println("</tr>");

                    out.println("<tr valign=\"top\">");
                    out.println("<td headers=\"\" width=\"92\" class=\"small\" align=\"left\"><b>Pass code</b>:</td>");
                    out.println("<td headers=\"\" colspan=\"3\" class=\"small\" align=\"left\">" + cal.getSPass() + "</td>");
                    out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"4\" height=\"18\" >&nbsp;</td>");
					out.println("</tr>");

					if (sRepeatType.trim().equalsIgnoreCase("D") || sRepeatType.trim().equalsIgnoreCase("W") || sRepeatType.trim().equalsIgnoreCase("M")) {

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
						out.println("<td headers=\"\"  class=\"small\"  width=\"92\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Repeats:</b></td>");
						if (sRepeatType.trim().equalsIgnoreCase("D")) {
							out.println("<td headers=\"\"  class=\"small\"  colspan=\"3\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">Daily</td>");
						} else if (sRepeatType.trim().equalsIgnoreCase("W")) {
							out.println("<td headers=\"\"  class=\"small\"  colspan=\"3\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">Weekly</td>");
						} else if (sRepeatType.trim().equalsIgnoreCase("M")) {
							out.println("<td headers=\"\"  class=\"small\"  colspan=\"3\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">Monthly</td>");
						}
						out.println("</tr>");

						// divider
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  class=\"small\"  width=\"92\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Repeat end date:</b></td>");

						String sTemp1 = null;
						if ("".equals(sRepeatID)) {
							sTemp1 = sEndDate.substring(5, 7) + "/" + sEndDate.substring(8, 10) + "/" + sEndDate.substring(0, 4);
						}
						else {
						String sTemp = cal.getSRepeatEnd().toString();
							sTemp1 = sTemp.substring(5, 7) + "/" + sTemp.substring(8, 10) + "/" + sTemp.substring(0, 4);
						}

						out.println("<td headers=\"\"  class=\"small\"  colspan=\"3\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">" + sTemp1 + "</td>");
						out.println("</tr>");

						// divider
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  class=\"small\"  width=\"92\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Cancel applies to:</b></td>");
						out.println("<td headers=\"\" align=\"left\" class=\"small\"  colspan=\"3\" valign=\"top\"><input type=\"radio\" name=\"repeat_none\" value=\"N\" selected=\"selected\" checked=\"checked\" id=\"label_this\" /><label for=\"label_this\">This instance only</label><br /><input type=\"radio\" name=\"repeat_none\" value=\"Y\" id=\"label_all\" /><label for=\"label_all\">All future instances</label></td>");
						out.println("</tr>");

					}
					
					// Delete Meeting Docs...
					
					Vector vDocs = ETSDatabaseManager.getDocsForMeetings(con,proj.getProjectId(),sCalID, sRepeatValue);
					
					if (vDocs != null && vDocs.size() > 0) {
							//if (sRepeatType.trim().equalsIgnoreCase("D") || sRepeatType.trim().equalsIgnoreCase("W") || sRepeatType.trim().equalsIgnoreCase("M")) {
																					
								// divider - line
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
								out.println("<td headers=\"\" align=\"left\" class=\"small\"  colspan=\"3\" valign=\"top\">");
								
								// Del Mtg Doc Check Box
								//out.println("<input type=\"checkbox\" id=\"del_mtg_docs\" name=\"del_mtg_docs\" value=\"N\" onclick=\"checkDoc1()\" />");
								out.println("<label for=\"del_mtg_docs\"><b>To delete documents as well, select the documents to be deleted.</b></label>");  
								out.println("</td></tr>");
								out.println("<tr><td headers=\"\" align=\"left\" class=\"small\" colspan=\"3\">&nbsp;</td></tr>");
								boolean bHasAllInstanceDoc = false;
								StringBuffer strDocBuffer = new StringBuffer("");
								for(int i=0; i < vDocs.size(); i++) {
								    ETSDoc udDoc = (ETSDoc) vDocs.get(i);
								    strDocBuffer.append("<tr>");
								    strDocBuffer.append("<td headers=\"\" align=\"left\" class=\"small\"  colspan=\"3\" valign=\"middle\">");
								    strDocBuffer.append("<table><tr><td><input type=\"checkbox\" name=\"deldoc" + udDoc.getId() + "\" />&nbsp;</td><td>");
								    if (udDoc.getMeetingId().equalsIgnoreCase(cal.getSRepeatID())) {
								        bHasAllInstanceDoc = true;
								        strDocBuffer.append("<span class=\"ast\">*</span>");
								    }
								    strDocBuffer.append(udDoc.getName());
								    strDocBuffer.append("</td></tr></table></td>");
								    strDocBuffer.append("</tr>");
								}
								if (bHasAllInstanceDoc) {
									out.println("<tr><td headers=\"\" align=\"left\" class=\"small\" colspan=\"3\">(Documents marked with <span class=\"ast\">*</span> applies to all instances of this meeting)</td></tr>");
									out.println("<tr><td headers=\"\" align=\"left\" class=\"small\" colspan=\"3\">&nbsp;</td></tr>");
								}
								out.println(strDocBuffer.toString());
								out.println("<tr><td headers=\"\" align=\"left\" class=\"small\" colspan=\"3\">&nbsp;</td></tr>");
								// Del Mtg Doc Radio Buttons
//								out.println("<tr>");
//								out.println("<td headers=\"\" >&nbsp;</td>");
//								out.println("<td headers=\"\" align=\"left\" class=\"small\"  colspan=\"3\" valign=\"top\">");
//								out.println("<input type=\"radio\" id=\"radio_this\" name=\"del_doc_this\" value=\"this\" selected=\"selected\" checked=\"checked\" disabled=\"true\" />");
//								out.println("<label for=\"radio_this\">Documents from this meeting only.</label>");
//								
//								out.println("<br /><input type=\"radio\" id=\"radio_all\" name=\"del_doc_this\" value=\"all\" disabled=\"true\" />");
//								out.println("<label for=\"radio_all\">Documents from all instances of the meeting including past instances.</label>");
//								out.println("</td></tr>");
						
//						} else {
//														
//							// divider - line
//							out.println("<tr valign=\"top\">");
//							out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
//							out.println("</tr>");
//							out.println("<tr valign=\"top\">");
//							out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
//							out.println("</tr>");
//							out.println("<tr valign=\"top\">");
//							out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
//							out.println("</tr>");
//
//							out.println("<tr>");
//							out.println("<td headers=\"\" align=\"left\" class=\"small\"  colspan=\"3\" valign=\"top\">");
//							
//							// Del Mtg Doc Check Box
//							out.println("<input type=\"checkbox\" id=\"del_mtg_docs\" name=\"del_mtg_docs\" value=\"N\" onclick=\"checkDoc2()\" />");
//							out.println("<label for=\"del_mtg_docs\"><b>Delete Documents from the meeting.</b></label>"); 
//							out.println("</tr>");
//											
//						}
								
					} // if Docs >0			

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
					out.println("<br />");

					out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"\"  align=\"left\"><b>If you would like to cancel this meeting, please click submit.</b>");
					out.println("</td></tr></table>");

					out.println("<br />");

				}


				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
				out.println("<td headers=\"\"  align=\"left\">");
				out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\" >&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
				out.println("  <noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
				out.println("</td></tr></table>");

			} else {
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td headers=\"\" ><b>Calendar entry not found.</b></td></tr></table>");
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

	/**
	 * Method updateCalendarEntry.
	 * @param conn
	 * @param out
	 * @param request
	 */
	private void cancelCalendarEntry(Connection conn, PrintWriter out, HttpServletRequest request, EdgeAccessCntrl es) throws SQLException, Exception {

		Statement stmt1 = null;
		ResultSet rs = null;

		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		StringBuffer sQuery = new StringBuffer("");

		try {

			String sCalendarId = ETSUtils.checkNull(request.getParameter("calid"));
			String sCalendarEntry = ETSUtils.checkNull(request.getParameter("caltype"));
			String sProjectId = ETSUtils.checkNull(request.getParameter("proj"));

			ETSCal cal = new ETSCal();

			int iResult = 0;

			sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,NOTIFY_TYPE,REPEAT_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = '" + sCalendarEntry + "' AND PROJECT_ID = '" + sProjectId + "' AND ");
			sQuery.append("CALENDAR_ID = '" + sCalendarId + "' for READ ONLY ");

			logger.debug("ETSCalCancelServlet::cancelCalendarEntry()::QUERY : " + sQuery.toString());

			stmt1 = conn.createStatement();
			rs = stmt1.executeQuery(sQuery.toString());

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
				String sNotifyType = ETSUtils.checkNull(rs.getString("NOTIFY_TYPE"));
				String sRepeatID = ETSUtils.checkNull(rs.getString("REPEAT_ID"));


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
				cal.setNotifyType(sNotifyType);


				sQuery.setLength(0);

				if (sCalendarEntry.trim().equalsIgnoreCase("M")) {

					String sRepeatNone = request.getParameter("repeat_none");
					if (sRepeatNone == null || sRepeatNone.trim().equalsIgnoreCase("")) {
						sRepeatNone = "N";
					} else {
						sRepeatNone = sRepeatNone.trim();
					}

					String sRepeatValue = "";
					if (sRepeatID.trim().equalsIgnoreCase("")) {
						sRepeatValue = "THIS-IS-NOT-A-REPEAT";
					} else {
						sRepeatValue = sRepeatID;
					}
					
					Enumeration enum = request.getParameterNames();
					Vector vtDocIds = new Vector();
					while (enum.hasMoreElements()) {
					    String strParamName = (String) enum.nextElement();
					    if (strParamName.startsWith("deldoc")) {
					        vtDocIds.add(strParamName.substring(6));
					    }
					}
					if (vtDocIds.size() > 0 ) {
					    // means document needs to be deleted.
					    for(int i=0; i < vtDocIds.size(); i++) {
						    String strEachDocID = (String) vtDocIds.get(i);
						    int iDocID = Integer.parseInt(strEachDocID);
						    ETSDoc udDoc = new ETSDoc();
						    udDoc.setId(iDocID);
						    ETSDatabaseManager.delDoc(udDoc, es.gIR_USERN, true, conn);
					    }
					}
//					String sDelMtgDoc = request.getParameter("del_mtg_docs");
//					if (sDelMtgDoc == null || sDelMtgDoc.trim().equalsIgnoreCase("")) {
//						sDelMtgDoc = "N";
//					} else {
//						sDelMtgDoc = sDelMtgDoc.trim();
//					}
//					
//					logger.debug("THE VALUE OF del_mtg_docs === "+sDelMtgDoc);
					
//					String sDelDocThis = request.getParameter("del_doc_this");
//					if (sDelDocThis == null || sDelDocThis.trim().equalsIgnoreCase("")) {
//						sDelDocThis = "N";
//					} else {
//						sDelDocThis = sDelDocThis.trim();
//					}
										
//					if (sDelMtgDoc.equalsIgnoreCase("Y")){
//						
//						logger.debug("Entering mtgChbox selected...");
//						
//						Vector vDocs = ETSDatabaseManager.getDocsForMeetings(conn,sProjectId,sCalendarId,sRepeatValue);
//
//						if (vDocs != null && vDocs.size() > 0) {
//							
//							for (int j = 0; j < vDocs.size(); j++) {
//
//								ETSDoc doc = (ETSDoc) vDocs.elementAt(j);
//								
//								logger.debug("DOC Name ==  "+doc.getName());		
//												
//						if(sDelDocThis.equalsIgnoreCase("this")){
//							
//							logger.debug("Entering radio - THIS block ...");
//							
//						     	if(!doc.getMeetingId().equalsIgnoreCase(sRepeatID)){
//						   
//						     		logger.debug("radio - THIS block delDoc if not Repeat Mtg...");
//						     		ETSDatabaseManager.delDoc(doc,es.gIR_USERN,true,conn);
//						     	}
//												
//						}else if(sDelDocThis.equalsIgnoreCase("all")){
//							
//							logger.debug("Entering radio - ALL block...");
//						
//							ETSDatabaseManager.delDoc(doc,es.gIR_USERN,true,conn);
//							
//						}else{
//							
//							logger.debug("Entering default mtgChkd block ...");
//							
//							ETSDatabaseManager.delDoc(doc,es.gIR_USERN,true,conn);
//							
//						}
//					  }
//					}		
//											
//					}
					
					logger.debug("CANCELLING THE MEETING ...");
					
					sQuery.append("UPDATE ETS.ETS_CALENDAR SET CANCEL_FLAG='Y' WHERE ");
					sQuery.append("PROJECT_ID = ? AND CALENDAR_TYPE= ? AND CALENDAR_ID = ?");

					logger.debug("ETSCalCalcelServlet::cancelCalendarEntry()::QUERY : " + sQuery.toString());

					stmt = conn.prepareStatement(sQuery.toString());

					stmt.setString(1,sProjectId);
					stmt.setString(2,sCalendarEntry.toUpperCase());
					stmt.setString(3,sCalendarId);

					iResult = stmt.executeUpdate();

					if (sRepeatNone.trim().equalsIgnoreCase("Y")) {

						// cancel all future instances of this meeting...

						Vector vMeetings = ETSCalendar.getUpdatableRepeatMeetings(conn,sProjectId,sCalendarId);

						for (int i = 0; i < vMeetings.size(); i++) {

							ETSCal calEntry = (ETSCal) vMeetings.elementAt(i);

							if (!calEntry.getSCancelFlag().equalsIgnoreCase("Y")) {
								stmt.clearParameters();

								stmt.setString(1,sProjectId);
								stmt.setString(2,sCalendarEntry.toUpperCase());
								stmt.setString(3,calEntry.getSCalendarId());

								iResult = stmt.executeUpdate();
							}

						}
					}

				} else {

					
					sQuery.append("DELETE FROM ETS.ETS_CALENDAR WHERE ");
					sQuery.append("PROJECT_ID = ? AND CALENDAR_TYPE= ? AND CALENDAR_ID = ?");

					logger.debug("ETSCalCalcelServlet::updateCalendarEntry()::QUERY : " + sQuery.toString());

					stmt = conn.prepareStatement(sQuery.toString());

					stmt.setString(1,sProjectId);
					stmt.setString(2,sCalendarEntry.toUpperCase());
					stmt.setString(3,sCalendarId);

					iResult = stmt.executeUpdate();
				}

                ETSProperties prop = new ETSProperties();
                String sMess = "";

				if (iResult > 0) {
					
					if (sCalendarEntry.trim().equalsIgnoreCase("M")) {
						// call send email routine
						sendCancelEmailNotification(conn,es,cal);
					} else {
						if (sEmailFlag.trim().equalsIgnoreCase("Y")) {
							// call send email routine
							sendCancelEmailNotification(conn,es,cal);
						}
					}

		    		if (sCalType.trim().equals("A")) {

                        sMess = prop.getSMessageCancel();

		    			ETSProj proj = ETSUtils.getProjectDetails(conn,sProjectId);

						if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
							// project..
							Metrics.appLog(conn, es.gIR_USERN, "ETS_Project_Alert_Delete");
						} else {
							// proposal
							Metrics.appLog(conn, es.gIR_USERN, "ETS_Proposal_Message_Delete");
						}
		    		} else if (sCalType.trim().equals("E")) {
                        sMess = prop.getSEventCancel();
		    			Metrics.appLog(conn, es.gIR_USERN, "ETS_Project_Event_Delete");
		    		} else if (sCalType.trim().equals("M")) {
                        sMess = prop.getSMeetingCancel();
		    			Metrics.appLog(conn, es.gIR_USERN, "ETS_Project_Meeting_Cancel");
					}


				} else {

                    if (sCalType.trim().equals("A")) {
                        sMess = prop.getSMessageFailure();
                    } else if (sCalType.trim().equals("E")) {
                        sMess = prop.getSEventFailure();
                    } else if (sCalType.trim().equals("M")) {
                        sMess = prop.getSMeetingFailure();
                    }
                }

				if (iResult > 0) {
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td headers=\"\" ><b>" + sMess + "</b><br /></td></tr></table>");
				} else {
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td headers=\"\" ><span style=\"color:#cc6600\"><b>" + sMess + "</b></span><br /></td></tr></table>");
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(stmt1);
			ETSDBUtils.close(stmt2);
		}

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
	private void sendCancelEmailNotification(Connection conn, EdgeAccessCntrl es, ETSCal cal) throws SQLException, Exception {

		StringBuffer sEmailStr = new StringBuffer("");

		try {

			String sMailType = "";

			String sTempDate = cal.getSStartTime().toString();

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

			ETSProj proj = ETSUtils.getProjectDetails(conn,cal.getSProjectId());
			
			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());
			String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }

			String sProjProposal = proj.getProjectOrProposal();
			if (sProjProposal.trim().equalsIgnoreCase("P")) {
				sProjProposal = "project";
			} else {
				sProjProposal = "proposal";
			}

			String sType = "";

			if (cal.getSCalType().trim().equalsIgnoreCase("E")) {
				sType = "event has been cancelled";
				sMailType = "EVENT";
			} else if (cal.getSCalType().trim().equalsIgnoreCase("A")) {
				if (sProjProposal.trim().equalsIgnoreCase("proposal")) {
					sType = "message has been cancelled";
					sMailType = "MESSAGE";
				} else {
					sType = "Message has been cancelled";
					sMailType = "ALERT";
				}
			} else {
				sType = "meeting has been cancelled";
				sMailType = "MEETING";
			}

			String sEmailSubject = "";

			if (cal.getSCalType().trim().equals("E")) {
				sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Event Cancelled: " + cal.getSSubject());
			} else if (cal.getSCalType().trim().equals("A")) {
				if (sProjProposal.trim().equalsIgnoreCase("proposal")) {
					sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Message Update: " + cal.getSSubject());
				} else {
					sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Message Update: " + cal.getSSubject());
				}
			} else {
				sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Meeting Cancelled: " + cal.getSSubject());
			}


		    if (cal.getSCalType().trim().equals("E")) {
		    	sEmailStr.append("An " + sType + " on IBM " +strCustConnect + unBrandedprop.getAppName() + " for \n" + sProjProposal + ": " + proj.getName() + " \n\n");
		    } else if (cal.getSCalType().trim().equals("A")) {
		    	if (proj.getProjectOrProposal().trim().equalsIgnoreCase("P")) {
			    	sEmailStr.append("A " + sType + " on IBM " +strCustConnect + unBrandedprop.getAppName() + " for \n" + sProjProposal + ": " + proj.getName() + " \n\n");
		    	} else {
		    		sEmailStr.append("A " + sType + " on IBM " +strCustConnect + unBrandedprop.getAppName() + " for \n" + sProjProposal + ": " + proj.getName() + " \n\n");
		    	}
		    } else {
		    	sEmailStr.append("A " + sType + " on IBM " +strCustConnect + unBrandedprop.getAppName() + " for \n" + sProjProposal + ": " + proj.getName() + " \n\n");
		    }

		    sEmailStr.append("The details of the cancellation are as follows: \n\n");

			sEmailStr.append("===============================================================\n");

			if (cal.getSCalType().trim().equals("E") || cal.getSCalType().trim().equals("A")) {
				sEmailStr.append("  Title:          " + ETSUtils.formatEmailStr(cal.getSSubject()) + "\n");
				sEmailStr.append("  Description:    " + ETSUtils.formatEmailStr(cal.getSDescription()) + " \n");
				sEmailStr.append("  Date:           " + sDate + " (mm/dd/yyyy) \n");
				sEmailStr.append("  By:             " + ETSUtils.getUsersName(conn,cal.getSScheduleBy()) + " \n\n");
			} else {
				
				long lTime = cal.getSStartTime().getTime() + (cal.getIDuration() * 60 * 1000);

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
				
				sEmailStr.append("  Subject:        " + ETSUtils.formatEmailStr(cal.getSSubject()) + "\n");
				sEmailStr.append("  Start time:     " + sDate + " " + sHour + ":" + sMin + sAMPM.toLowerCase() + " Eastern US \n");
				sEmailStr.append("  End time:       " + sHour1 + ":" + sMin1 + sAMPM1.toLowerCase() + " Eastern US \n");
				sEmailStr.append("  Organized by:   " + ETSUtils.getUsersName(conn,cal.getSScheduleBy()) + " \n\n");
			}

			sEmailStr.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("Delivered by E&TS Connect. \n");
//			sEmailStr.append("This is a system generated email. \n");
//			sEmailStr.append("===============================================================\n\n");


			String sToList = "";

			if (cal.getSCalType().trim().equals("E") || cal.getSCalType().trim().equals("A")) {

				if (cal.getSInviteesID().indexOf("ALL") >= 0) {
					Vector vMembers = ETSDatabaseManager.getProjMembers(cal.getSProjectId(),conn);
					if (vMembers != null && vMembers.size() > 0) {
						for (int i = 0; i < vMembers.size(); i++) {
							ETSUser user = (ETSUser) vMembers.elementAt(i);
							if (sToList.trim().equals("")) {
								sToList = ETSUtils.getUserEmail(conn,user.getUserId());
							} else {
								sToList = sToList + "," + ETSUtils.getUserEmail(conn,user.getUserId());
							}
						}
					}
				} else {
					StringTokenizer st = new StringTokenizer(cal.getSInviteesID(),",");
					while (st.hasMoreTokens()) {
						if (sToList.trim().equals("")) {
							sToList = ETSUtils.getUserEmail(conn,st.nextToken());
						} else {
							sToList = sToList + "," + ETSUtils.getUserEmail(conn,st.nextToken());
						}
					}
					sToList = sToList + "," + es.gEMAIL;					
				}
			} else {
				// since meeting, get the to list from the invitees.
				StringTokenizer st = new StringTokenizer(cal.getSInviteesID(),",");
				while (st.hasMoreTokens()) {
					if (sToList.trim().equals("")) {
						sToList = ETSUtils.getUserEmail(conn,st.nextToken());
					} else {
						sToList = sToList + "," + ETSUtils.getUserEmail(conn,st.nextToken());
					}
				}
				sToList = sToList + "," + es.gEMAIL;
			}

			boolean bSent = false;

			if (!sToList.trim().equals("")) {
				
				if (cal.getNotifyType().equalsIgnoreCase("T")) {
					bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,cal.getSCCList(),"",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
				} else {
					bSent = ETSUtils.sendEMail(es.gEMAIL,"",cal.getSCCList(),sToList,Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
				}

				ETSUtils.insertEmailLog(conn,sMailType,cal.getSCalendarId(),"CANCEL",es.gEMAIL,cal.getSProjectId(),sEmailSubject,sToList,cal.getSCCList());

			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}
}
