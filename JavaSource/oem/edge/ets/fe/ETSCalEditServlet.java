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

/*****************************************************************************/
/*  Change Log 	: 	Please Enter Changed on, Changed by and Desc			 */
/*****************************************************************************/
/*	Changed On  : 	05/01/2007												 */
/*	Changed By  : 	Suresh													 */
/*	Change Desc : 	Added End Time field for Events and added the value 	 */
/*					to the Repeat_End column of ETS.CALENDAR table.          */
/*****************************************************************************/

/**
 * @author: Sathish
 */

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.common.DatesArithmatic;
import oem.edge.common.Global;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSCalEditServlet extends javax.servlet.http.HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.29";
	
	private static Log logger = EtsLogger.getLogger(ETSCalEditServlet.class);


	private static void showDuration(PrintWriter out, String sInDuration, String sDurationName, String sId) {

		out.println("<select id=\"" + sId + "\" class=\"iform\" name=" + sDurationName + ">");

		for (int k = 1; k <= 30; k++) {
			String qq = "" + k;
			if (k < 10) {
				qq = "0" + qq;
			}
			out.println("<option value=\"" + qq + "\"");
			if (Integer.parseInt(sInDuration) == k) {
				out.println(" selected=\"selected\" ");
			}
			out.println(">" + qq + "</option>");
		}

		out.println("</select>");
	}



	private static void showDate(PrintWriter out, String m, String d, String yy, String mname, String dname, String yname, String sId) {

		String mon[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);

		out.println("<table summary=\"\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td headers=\"\"  align=\"left\" width=\"40\"><label for=\"month\">Month</label><br />");
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

		out.println("</select></td><td headers=\"\"  width=\"30\" align=\"left\"><label for=\"day\">Day</label><br /><select id=\"" + sId + " day\" class=\"iform\" name=" + dname + ">");
		for (int k = 1; k < 32; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (d.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select></td><td headers=\"\"   align=\"left\"><label for=\"year\">Year</label><br /><select id=\"" + sId + " year\" class=\"iform\" name=" + yname + ">");
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




	/**
	 * Method ETSCalDisplayServlet.
	 */
	public ETSCalEditServlet() {
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

			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(conn,sProjId);

			params = ETSUtils.getServletParameters(request);

			ETSProj proj = ETSUtils.getProjectDetails(conn,sProjId);

			PopupHeaderFooter header = new PopupHeaderFooter();
			header.setPageTitle(unBrandedprop.getAppName() + " - Details");
			out.println(header.printPopupHeader());
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


			out.println("<form name=\"CalendarAdd\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss\">");

			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");

			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"caltype\" value=\"" + sCalType + "\" />");
			out.println("<input type=\"hidden\" name=\"calid\" value=\"" + sCalId + "\" />");


			if (!sSubmitFlag.trim().equals("")) {
				updateCalendarEntry(conn,out,request,es,proj);
			} else {
				displayCalendarEntry(conn,out,sProjId,sCalType,sCalId,proj,es.gIR_USERN);
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
	 * Method updateCalendarEntry.
	 * @param conn
	 * @param out
	 * @param request
	 */
	private void updateCalendarEntry(Connection conn, PrintWriter out, HttpServletRequest request, EdgeAccessCntrl es, ETSProj proj) throws SQLException, Exception {

		PreparedStatement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		String sOp = "";

		try {

            ETSProperties prop = new ETSProperties();
            String sMess = "";



			if (validateCalendarEntry(conn,out,request,es.gIR_USERN)) {

				String sCalendarId = ETSUtils.checkNull(request.getParameter("calid"));
				String sCalendarEntry = ETSUtils.checkNull(request.getParameter("caltype"));
				String sProjectId = ETSUtils.checkNull(request.getParameter("proj"));

				String sCalSubject = ETSUtils.checkNull(request.getParameter("cal_subject"));
				String sCalDuration = ETSUtils.checkNull(request.getParameter("cal_duration"));
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
				
				Timestamp timeStart = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000");
				Timestamp timeEnd = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalEndHour + ":" + sCalEndMinutes + ":00.000000000");

				/*long lDur = 0;

				if (timeStart.before(timeEnd)) {
					lDur = timeEnd.getTime() - timeStart.getTime();
					sCalDuration = String.valueOf((lDur / 1000) / 60);		// to convert to minutes...
				}*/

				String sAllMembers = request.getParameter("all_members");
				if (sAllMembers == null || sAllMembers.trim().equals("")) {
					sAllMembers = "";
				} else {
					sAllMembers = sAllMembers.trim();
				}

				String sInviteesId = "";
				if (sAllMembers.trim().equals("")) {
					String sCalInvitees[] = request.getParameterValues("cal_invitees");
					if (sCalInvitees != null && sCalInvitees.length > 0) {
						for (int i = 0; i < sCalInvitees.length; i++) {
							if (sInviteesId.trim().equals("")) {
								sInviteesId = sCalInvitees[i];
							} else {
								sInviteesId = sInviteesId + "," + sCalInvitees[i];
							}
						}
					}
				} else {
					sInviteesId = "ALL";
				}

				String sCalNotify = ETSUtils.checkNull(request.getParameter("cal_notify"));
				String sNotifyType = ETSUtils.checkNull(request.getParameter("notifyOption"));

				int iResult = 0;

				if (!sCalendarId.trim().equals("")) {

					sQuery.append("UPDATE ETS.ETS_CALENDAR SET SUBJECT = ?, DESCRIPTION = ?, DURATION = ?, START_TIME = ?, EMAIL_FLAG = ?, SCHEDULE_DATE=?, INVITEES_ID=?, NOTIFY_TYPE=?, REPEAT_END = ? WHERE ");
					sQuery.append("PROJECT_ID = ? AND CALENDAR_TYPE= ? AND CALENDAR_ID = ?");

					logger.debug("ETSCalEditServlet::updateCalendarEntry()::QUERY : " + sQuery.toString());

					stmt = conn.prepareStatement(sQuery.toString());

					stmt.setString(1,ETSUtils.escapeString(sCalSubject));
					stmt.setString(2,ETSUtils.escapeString(sCalDesc));
					stmt.setInt(3,Integer.parseInt(sCalDuration));
					stmt.setTimestamp(4,Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000")); 		// check this to see if it works correctly. Sathish
					stmt.setString(5,sCalNotify);
					stmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));
					stmt.setString(7,sInviteesId);
					stmt.setString(8,sNotifyType);
					stmt.setTimestamp(9,timeEnd);
					stmt.setString(10,sProjectId);
					stmt.setString(11,sCalendarEntry.toUpperCase());
					stmt.setString(12,sCalendarId);

					iResult = stmt.executeUpdate();

					sOp = " updated.";

                    if (sCalendarEntry.toUpperCase().equals("A")) {
                        sMess = prop.getSMessageUpdate();
                    } else {
                        sMess = prop.getSEventUpdate();
                    }


				} else {

					ETSCal cal = new ETSCal();
					sCalendarId = ETSCalendar.getNewCalendarId(); 	// generate a  new id
					cal.setSCalendarId(sCalendarId);
					cal.setSProjectId(sProjectId);
					cal.setSCalType(sCalendarEntry.toUpperCase());
					cal.setSScheduleDate(new Timestamp(System.currentTimeMillis()));
					cal.setSScheduleBy(es.gIR_USERN);
					cal.setSStartTime(Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000"));
					cal.setIDuration(Integer.parseInt(sCalDuration));
					cal.setSSubject(ETSUtils.escapeString(sCalSubject));
					cal.setSDescription(ETSUtils.escapeString(sCalDesc));
					cal.setSInviteesID(sInviteesId);
					cal.setSCCList("");
					cal.setSWebFlag("N");
					cal.setSCancelFlag("N");
					cal.setSEmailFlag(sCalNotify);
					cal.setSIBMOnly("N");
					cal.setNotifyType(sNotifyType);
					cal.setSRepeatEnd(timeEnd);

					ETSDBUtils.close(stmt);
					sQuery.setLength(0);

					sQuery.append("INSERT INTO ETS.ETS_CALENDAR (CALENDAR_ID,PROJECT_ID,CALENDAR_TYPE,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,NOTIFY_TYPE,REPEAT_END) VALUES ");
					sQuery.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

					stmt = conn.prepareStatement(sQuery.toString());

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
					stmt.setString(16,cal.getNotifyType());				// NOTIFY TYPE
					stmt.setTimestamp(17,cal.getSRepeatEnd());          // End_Time  

					iResult = stmt.executeUpdate();


					if (iResult > 0) {
						sOp = " inserted.";

						if (sCalendarEntry.toUpperCase().equals("A")) {
                            sMess = prop.getSMessageSuccess();
							// alert
							if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
								// project..
								Metrics.appLog(conn, es.gIR_USERN, "ETS_Project_Alert_Add");
							} else {
								// proposal
								Metrics.appLog(conn, es.gIR_USERN, "ETS_Proposal_Message_Add");
							}
						} else {
                            sMess = prop.getSEventSuccess();
							// event
							Metrics.appLog(conn, es.gIR_USERN, "ETS_Project_Event_Add");
						}

					} else {
						iResult = 0;
                        if (sCalendarEntry.toUpperCase().equals("A")) {
                            sMess = prop.getSMessageFailure();
                        } else {
                            sMess = prop.getSEventFailure();
                        }
					}


				}

				if (iResult > 0) {

				    if (sCalNotify.trim().equalsIgnoreCase("Y")) {
				    	// call send email routine
				    	if (sOp.trim().equalsIgnoreCase("inserted.")) {
				    		sendUpdateEmailNotification(conn,es,"Y",sProjectId,sCalendarId,sCalendarEntry);
				    	} else {
					    	sendUpdateEmailNotification(conn,es,"N",sProjectId,sCalendarId,sCalendarEntry);
				    	}
				    }
				}

				if (iResult > 0) {
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td headers=\"\" ><b>" + sMess + "</b><br /></td></tr></table>");
				} else {
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td headers=\"\" ><span style=\"color:#cc6600\"><b>" + sMess + "</b></span><br /></td></tr></table>");
				}

			    out.println("<br /><br />");
			    out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"30\" align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" /></td><td headers=\"\"  align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\">Ok</a></a></td></tr></table>");
			    out.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");
			    out.println("<br /><br />");


			} else {

				StringBuffer sError = new StringBuffer("");

				String sCalendarId = ETSUtils.checkNull(request.getParameter("calid"));
				String sCalendarEntry = ETSUtils.checkNull(request.getParameter("caltype"));
				String sProjectId = ETSUtils.checkNull(request.getParameter("proj"));

				String sCalSubject = ETSUtils.checkNull(request.getParameter("cal_subject"));
				if (sCalSubject.trim().equals("")) {
					sError.append("<b>Title</b> cannot not be empty. Please enter the title.<br />");
				}
				String sCalDuration = ETSUtils.checkNull(request.getParameter("cal_duration"));
				String sCalDesc = ETSUtils.checkNull(request.getParameter("cal_desc"));
				if (sCalDesc.trim().equals("")) {
					sError.append("<b>Description</b> cannot not be empty. Please enter the description.<br />");
				} else {
					if (sCalDesc.trim().length() > 1000) {
						sError.append("<b>Description</b> cannot exceed 1000 characters.<br />");
					}
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

				long lDur = 0;

				if( (!sCalendarEntry.equalsIgnoreCase("A") ) ) {
					if (timeStart.before(timeEnd)) {
					} else {
						sError.append("<b>End time</b> cannot be before meeting <b>Start time</b>. Please select meeting times appropriately.<br />");
					}
				}
				
				String sAllMembers = request.getParameter("all_members");
				if (sAllMembers == null || sAllMembers.trim().equals("")) {
					sAllMembers = "";
				} else {
					sAllMembers = sAllMembers.trim();
				}

				String invitees = "";
				
				String sCalInvitees[] = request.getParameterValues("cal_invitees");
				if (sAllMembers.trim().equals("")) {
					String sInviteesId = "";
					if (sCalInvitees == null || sCalInvitees.length <= 0) {
						sError.append("<b>Applies to</b> has to be selected. Please select \"All members\" or select members from the list.<br />");
					}
				}
				
				if (sCalInvitees == null || sCalInvitees.length <= 0) {
					
				} else {
					for (int i = 0; i < sCalInvitees.length; i++) {
						if (invitees.trim().equalsIgnoreCase("")) {
							invitees = sCalInvitees[i];
						} else {
							invitees = invitees + "," + sCalInvitees[i];
						}
					}
				}
				

				if (sCalendarEntry.equalsIgnoreCase("A")) {
					// check to make sure the alert are not posted for a date before the current date.
					String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");

					String sCurrentMonth = sTodaysDate.substring(0, 2);
					String sCurrentDay = sTodaysDate.substring(3, 5);
					String sCurrentYear = sTodaysDate.substring(6, 10);

					long timeEvent = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 01:00:00.000000000").getTime();
					long timeNow = Timestamp.valueOf(sCurrentYear + "-" + sCurrentMonth + "-" + sCurrentDay + " 00:00:00.000000000").getTime();

					if (timeEvent < timeNow && sError.toString().trim().equals("")) {
						sError.append("<b>This entry is being created in the past. Click submit to create anyway.</b><br />");
						out.println("<input type=\"hidden\" name=\"submit_anyway\" value=\"Y\" />");
					}

				} else {

					// check to make sure the event are not posted for a time before the current time.
					long timeEvent = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000").getTime();
					long timeNow = new Timestamp(System.currentTimeMillis()).getTime();

					if (timeEvent < timeNow && sError.toString().trim().equals("")) {
						sError.append("<b>This entry is being created in the past. Click submit to create anyway.</b><br />");
						out.println("<input type=\"hidden\" name=\"submit_anyway\" value=\"Y\" />");
					}
				}

				// the code above makes the 12 to 0 if the time set is 12.** AM
				// so set it back to it displays OK
				if (Integer.parseInt(sCalHour) == 0) {
					sCalHour = "12";
				}

				String sCalNotify = ETSUtils.checkNull(request.getParameter("cal_notify"));
				
				String sNotifyOpt = ETSUtils.checkNull(request.getParameter("notifyOption"));

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

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"100%\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
				out.println("</tr>");
				out.println("</table>");


				if (sCalendarEntry.equalsIgnoreCase("A")) {

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
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_title\">Title:</label></b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"text\"  class=\"iform\" name=\"cal_subject\" id=\"label_title\" maxlength=\"100\" size=\"40\" value=\"" + sCalSubject + "\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_desc\">Description:</label></b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">[ Maximum 1000 chars. ]</span><br /><textarea cols=\"40\"  class=\"iform\" rows=\"5\" name=\"cal_desc\" id=\"label_desc\" maxlength=\"1000\">" + sCalDesc + "</textarea></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_date\">Date:</label></b></td>");
					out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\" align=\"left\">");
					showDate(out,sCalMonth,sCalDay,sCalYear,"cal_month","cal_day","cal_year","label_date");
					out.println("</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">Your message will be visible to users after you click Submit and until it expires. Select a date to indicate when the user should attend to your message.</span></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_duration\">Expires in:</label></b></td>");
					out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
					showDuration(out,String.valueOf(sCalDuration),"cal_duration","label_duration");
					out.println(" days.</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"150\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_invitees\">Applies to</label></b>:</td>");
					if (sAllMembers.equalsIgnoreCase("all")) {
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\" checked=\"checked\" selected=\"selected\" /><label for=\"label_members\">All members</label></td>");
					} else {
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\" /><label for=\"label_members\">All members</label></td>");
					}
					
					out.println("</tr>");
		
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"150\" align=\"left\">&nbsp;</td>");
					out.println("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</span><br />" + displayInviteesAsSelect(conn,"cal_invitees","label_invitees",proj.getProjectId(),invitees,es.gIR_USERN) + "</td>");
					out.println("</tr>");


					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Notification:</b></td>");

					if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
						if (sCalNotify.trim().equalsIgnoreCase("Y")) {
							out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" checked=\"checked\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify all team members through e-mail about this Message.</span></td>");
						} else {
							out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify all team members through e-mail about this Message.</span></td>");
						}
					} else {
						if (sCalNotify.trim().equalsIgnoreCase("Y")) {
							out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" checked=\"checked\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify all team members through e-mail about this message.</span></td>");
						} else {
							out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify all team members through e-mail about this message.</span></td>");
						}
					}
					out.println("</tr>");


					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>E-mail notification option:</b></td>");
					if (sNotifyOpt.equalsIgnoreCase("t")) {
						out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\" checked=\"checked\" /><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" /><label for=\"notifyOption2\">Bcc</label></td>");
					} else {
						out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\"/><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" checked=\"checked\"/><label for=\"notifyOption2\">Bcc</label></td>");
					}
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
					out.println("<td headers=\"\" valign=\"top\"><span class=\"small\">[Notifications are sent via internet e-mail, with the e-mail addresses listed in the \"To\" field of the e-mail header. If you choose \"Bcc\" as the e-mail notification option, then all e-mail addresses are listed in the blind copy (Bcc) field of the e-mail header, thus, hiding the list of notification recipients.]</span></td>");
					out.println("</tr>");

					out.println("</table>");
					out.println("</td></tr></table>");
					out.println("</td></tr></table>");

					out.println("<br />");
					out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
					out.println("<td headers=\"\"  align=\"left\">");
					out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\" >&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
					out.println("  <noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
					out.println("</td></tr></table>");

				} else {

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
					out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_title\">Title:</label></b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"text\"  class=\"iform\" name=\"cal_subject\" id=\"label_title\" maxlength=\"100\" size=\"40\" value=\"" + sCalSubject + "\" /></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_desc\">Description:</label></b></td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">[ Maximum 1000 chars. ]</span><br /><textarea cols=\"40\"  class=\"iform\" rows=\"5\" name=\"cal_desc\" id=\"label_desc\" maxlength=\"1000\">" + sCalDesc + "</textarea></td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_date\">Date:</label></b></td>");
					out.println("<td headers=\"\"  width=\"350\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
					showDateTime(out,sCalMonth,sCalDay,sCalYear,sCalHour,sCalMinutes,sCalAMPM,"cal_month","cal_day","cal_year","cal_hour","cal_min","cal_ampm","label_date","label_date");
					out.println("</td>");
					out.println("</tr>");
					
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_edate\">End Time:</label></b></td>");
					out.println("<td headers=\"\" width=\"343\" align=\"left\">");

					//showDate(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10), sTempDate.substring(0, 4),"cal_month","cal_day","cal_year","label_date");
					showTime(out,sCalEndHour,sCalEndMinutes,sCalEndAMPM,"cal_ehour","cal_emin","cal_eampm","label_edate");

					out.println("</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
					out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">Your event will be visible to users after you click Submit and until it expires. Select a date to indicate when the user should attend to your event.</span></td>");
					out.println("</tr>");


					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_duration\">Repeats for</label>:</b></td>");
					out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
					showDuration(out,String.valueOf(sCalDuration),"cal_duration","label_duration");
					out.println(" days.</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_invitees\">Applies to</label></b>:</td>");
					if (sAllMembers.equalsIgnoreCase("all")) {
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\" checked=\"checked\" selected=\"selected\" /><label for=\"label_members\">All members</label></td>");
					} else {
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\"/><label for=\"label_members\">All members</label></td>");
					}

					out.println("</tr>");
		
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"100\" align=\"left\">&nbsp;</td>");
					out.println("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</span><br />" + displayInviteesAsSelect(conn,"cal_invitees","label_invitees",proj.getProjectId(),invitees,es.gIR_USERN) + "</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Notification:</b></td>");

					if (sCalNotify.trim().equalsIgnoreCase("Y")) {
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" checked=\"checked\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify all team members through e-mail about this event.</span></td>");
					} else {
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify all team members through e-mail about this event.</span></td>");
					}
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>E-mail notification option:</b></td>");
					if (sNotifyOpt.equalsIgnoreCase("t")) {
						out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\" checked=\"checked\" /><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" /><label for=\"notifyOption2\">Bcc</label></td>");
					} else {
						out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\"/><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" checked=\"checked\"/><label for=\"notifyOption2\">Bcc</label></td>");
					}
					out.println("</tr>");
					
					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
					out.println("<td headers=\"\" valign=\"top\"><span class=\"small\">[Notifications are sent via internet e-mail, with the e-mail addresses listed in the \"To\" field of the e-mail header. If you choose \"Bcc\" as the e-mail notification option, then all e-mail addresses are listed in the blind copy (Bcc) field of the e-mail header, thus, hiding the list of notification recipients.]</span></td>");
					out.println("</tr>");
					

					out.println("</table>");
					out.println("</td></tr></table>");
					out.println("</td></tr></table>");

					out.println("<br />");
					out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
					out.println("<td headers=\"\"  align=\"left\">");
					out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\" >&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
					out.println("  <noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
					out.println("</td></tr></table>");
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
	private void sendUpdateEmailNotification(Connection conn, EdgeAccessCntrl es, String sInsFlag, String sProjectId, String sCalendarId, String sCalendarEntry) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		StringBuffer sEmailStr = new StringBuffer("");

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(conn,sProjectId);

			String sMailType = "";

			sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,NOTIFY_TYPE,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = '" + sCalendarEntry + "' AND PROJECT_ID = '" + sProjectId + "' AND CALENDAR_ID = '" + sCalendarId + "' ");
			sQuery.append("for READ ONLY ");

			logger.debug("ETSCalEditServlet::sendUpdateEmailNotification()::QUERY : " + sQuery.toString());

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
				String sNotifyType = ETSUtils.checkNull(rs.getString("NOTIFY_TYPE"));
				Timestamp tEndDate = rs.getTimestamp("REPEAT_END");

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
				
				System.out.println("\n\n PEROJKLAJSKLJ :" + sProjectId);
				
				System.out.println("\n\n PEROJKLAJSKLJ :" + proj.getProjectType());
				
				UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());
				String strCustConnect = "";
			    if ("Collaboration Center".equalsIgnoreCase(prop.getAppName())) {
			        strCustConnect = "Customer Connect ";
			    }

				String sProjProposal = proj.getProjectOrProposal();
				if (sProjProposal.trim().equalsIgnoreCase("P")) {
					sProjProposal = "project";
				} else {
					sProjProposal = "proposal";
				}

				String sType = "";

				if (sCalendarEntry.trim().equals("E")) {
					sType = "event";
				} else {
					if (sProjProposal.trim().equalsIgnoreCase("proposal")) {
						sType = "message";
					} else {
						//sType = "alert";
                        sType = "message";
					}
				}

				String sEmailSubject = "";

				if (sInsFlag.trim().equals("Y")) {
					if (sCalendarEntry.trim().equals("E")) {
						sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Event Notice: " + sSubject);
					} else {
						if (sProjProposal.trim().equalsIgnoreCase("proposal")) {
							sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Message: " + sSubject);
						} else {
							sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Message: " + sSubject);
						}
					}
				} else {
					if (sCalendarEntry.trim().equals("E")) {
						sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Event Update: " + sSubject);
					} else {
						if (sProjProposal.trim().equalsIgnoreCase("proposal")) {
							sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Message Update: " + sSubject);
						} else {
							sEmailSubject = ETSUtils.formatEmailSubject("IBM "+unBrandedprop.getAppName() + " - Message Update: " + sSubject);
						}
					}
				}

			    if (sInsFlag.trim().equals("Y")) {
		    		sEmailStr.append("A new " + sType + " has been posted for you in an IBM " +strCustConnect + unBrandedprop.getAppName() + " for \n" + sProjProposal + ": " + proj.getName() + " \n\n");
			    } else {
                    if (sCalendarEntry.trim().equals("E")) {
                        sEmailStr.append("An " + sType + " has been updated for you in an IBM " +strCustConnect + unBrandedprop.getAppName() + " for \n" + sProjProposal + ": " + proj.getName() + " \n\n");
                    } else {
			    		sEmailStr.append("A " + sType + " has been updated for you in an IBM " +strCustConnect + unBrandedprop.getAppName() + " for \n" + sProjProposal + ": " + proj.getName() + " \n\n");
                    }
			    }
                
                int iTCForMain = ETSDatabaseManager.getTopCatId(sProjectId,Defines.MAIN_VT);
                
			    sEmailStr.append("The details of the " + sType + " are as follows: \n\n");

				sEmailStr.append("===============================================================\n");

				sEmailStr.append("  Title:          " + ETSUtils.formatEmailStr(sSubject) + "\n");
				sEmailStr.append("  Date:           " + sDate + " (mm/dd/yyyy) \n");
				sEmailStr.append("  Description:    " + ETSUtils.formatEmailStr(sDescription) + " \n");
				sEmailStr.append("  By:             " + ETSUtils.getUsersName(conn,sScheduleBy) + " \n\n");

				sEmailStr.append("To view the " + sType + ", click on the following URL and log-in:\n");
                if (sCalendarEntry.trim().equals("E")) {
                    sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + sProjectId + "&tc=" + iTCForMain + "&etsop=moreevents&linkid=" + prop.getLinkID() + "\n\n");
                } else {
    				sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + sProjectId + "&tc=" + iTCForMain + "&etsop=morealerts&linkid=" + prop.getLinkID() + "\n\n");
                }
                
                sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));
                
//				sEmailStr.append("===============================================================\n");
//				sEmailStr.append("Delivered by E&TS Connect. \n");
//				sEmailStr.append("This is a system generated email. \n");
//				sEmailStr.append("===============================================================\n\n");

				String sToList = "";

				if (sInvitees.indexOf("ALL") >= 0) {
					Vector vMembers = ETSDatabaseManager.getProjMembers(sProjectId,conn);
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
					StringTokenizer st = new StringTokenizer(sInvitees,",");
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
					
					if (sNotifyType.equalsIgnoreCase("T")) {
						bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,"","",Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL); 
					} else {
						bSent = ETSUtils.sendEMail(es.gEMAIL,"","",sToList,Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);
					}
					

					if (sInsFlag.trim().equals("Y")) {
						ETSUtils.insertEmailLog(conn,sType.toUpperCase(),sCalendarId,"ADD",es.gEMAIL,sProjectId,sEmailSubject,sToList,"");
					} else {
						ETSUtils.insertEmailLog(conn,sType.toUpperCase(),sCalendarId,"UPDATE",es.gEMAIL,sProjectId,sEmailSubject,sToList,"");
					}
				}
			}

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
	 * Method validateCalendarEntry.
	 * @param conn
	 * @param out
	 * @param request
	 * @param sUserId
	 * @return boolean
	 */
	private boolean validateCalendarEntry(Connection conn, PrintWriter out, HttpServletRequest request, String sUserId) throws Exception {

		try {

			String sCalSubject = ETSUtils.checkNull(request.getParameter("cal_subject"));
			if (sCalSubject.trim().equals("")) {
				return false;
			}
			String sCalDesc = ETSUtils.checkNull(request.getParameter("cal_desc"));
			if (sCalDesc.trim().equals("")) {
				return false;
			} else {
				if (sCalDesc.trim().length() > 1000) {
					return false;
				}
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

			String sCalType = request.getParameter("caltype");
			if( ( !sCalType.equalsIgnoreCase("A") ) ) {
				if (timeStart.before(timeEnd)) {
	
				} else {
					return false;
				}
			}

			if (sCalType == null || sCalType.trim().equals("")) {
				sCalType = "";
			} else {
				sCalType = sCalType.trim();
			}

			String sAllMembers = request.getParameter("all_members");
			if (sAllMembers == null || sAllMembers.trim().equals("")) {
				sAllMembers = "";
			} else {
				sAllMembers = sAllMembers.trim();
			}

			if (sAllMembers.trim().equals("")) {
				String sCalInvitees[] = request.getParameterValues("cal_invitees");
				String sInviteesId = "";
				if (sCalInvitees == null || sCalInvitees.length <= 0) {
					return false;
				}
			}
			
			String sSubmitAnyway = ETSUtils.checkNull(request.getParameter("submit_anyway"));

			if (sCalType.equalsIgnoreCase("A")) {
				if (!sSubmitAnyway.trim().equalsIgnoreCase("Y")) {
					// check to make sure the alert are not posted for a date before the current date.
					String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");

					String sCurrentMonth = sTodaysDate.substring(0, 2);
					String sCurrentDay = sTodaysDate.substring(3, 5);
					String sCurrentYear = sTodaysDate.substring(6, 10);

					long timeEvent = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 01:00:00.000000000").getTime();
					long timeNow = Timestamp.valueOf(sCurrentYear + "-" + sCurrentMonth + "-" + sCurrentDay + " 00:00:00.000000000").getTime();

					if (timeEvent < timeNow) {
						return false;
					}
				}
			} else {

				if (!sSubmitAnyway.trim().equalsIgnoreCase("Y")) {
					// check to make sure the event are not posted for a time before the current time.
					long timeEvent = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " " + sCalHour + ":" + sCalMinutes + ":00.000000000").getTime();
					long timeNow = new Timestamp(System.currentTimeMillis()).getTime();

					if (timeEvent < timeNow) {
						return false;
					}
				}
			}


		} catch (Exception e) {
			throw e;
		}

		return true;
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
	private static void displayCalendarEntry(Connection con, PrintWriter out, String sProjectId, String sCalendarType, String sCalendarId,ETSProj proj, String sIRUserID) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		try {

			if (!sCalendarId.trim().equals("")) {

				// this is an edit operation

				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,NOTIFY_TYPE,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = '" + sCalendarType + "' AND PROJECT_ID = '" + sProjectId + "' AND ");
				sQuery.append("CALENDAR_ID = '" + sCalendarId + "' for READ ONLY ");


				logger.debug("ETSCalEditServlet::displayCalendarEntry()::QUERY : " + sQuery.toString());

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
					String sNotifyType = ETSUtils.checkNull(rs.getString("NOTIFY_TYPE"));
					Timestamp tEndDate = rs.getTimestamp("REPEAT_END");
					
					if ((tEndDate == null)||(tEndDate.equals(""))){
						tEndDate = tStartTime;
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
					cal.setNotifyType(sNotifyType);
					cal.setSRepeatEnd(tEndDate);

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
					
					String sEndDate = tEndDate.toString();
					
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

					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" width=\"100%\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
					out.println("</tr>");
					out.println("</table>");

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
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_title\">Title:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"text\"  class=\"iform\" name=\"cal_subject\" id=\"label_title\" maxlength=\"100\" size=\"40\" value=\"" + cal.getSSubject() + "\" /></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_desc\">Description:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">[ Maximum 1000 chars. ]</span><br /><textarea cols=\"40\"  class=\"iform\" rows=\"5\" name=\"cal_desc\" id=\"label_desc\" maxlength=\"1000\">" + cal.getSDescription() + "</textarea></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_date\">Date:</label></b></td>");
						out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\" align=\"left\">");
						showDate(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10),sTempDate.substring(0, 4),"cal_month","cal_day","cal_year","label_date");
						out.println("</td>");
						out.println("</tr>");
						
						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">Your message will be visible to users after you click Submit and until it expires. Select a date to indicate when the user should attend to your message.</span></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_duration\">Expires in:</label></b></td>");
						out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
						showDuration(out,String.valueOf(cal.getIDuration()),"cal_duration","label_duration");
						out.println(" days.</td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"150\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_invitees\">Applies to</label></b>:</td>");
						if (sInvitees.indexOf("ALL") >= 0) {
							out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\" selected=\"selected\" checked=\"checked\" /><label for=\"label_members\">All members</label></td>");
						} else {
							out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\" /><label for=\"label_members\">All members</label></td>");
						}
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"150\" align=\"left\">&nbsp;</td>");
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</span><br />" + displayInviteesAsSelect(con,"cal_invitees","label_invitees",proj.getProjectId(),sInvitees,sScheduleBy) + "</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Notification:</b></td>");
						if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
							if (sEmailFlag.trim().equalsIgnoreCase("Y")) {
								out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" checked=\"checked\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify selected team members through e-mail about this Message.</span></td>");
							} else {
								out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify selected team members through e-mail about this Message.</span></td>");
							}
						} else {
							if (sEmailFlag.trim().equalsIgnoreCase("Y")) {
								out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" checked=\"checked\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify selected team members through e-mail about this message.</span></td>");
							} else {
								out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify selected team members through e-mail about this message.</span></td>");
							}
						}
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>E-mail notification option:</b></td>");
						if (sNotifyType.equalsIgnoreCase("T")) {
							out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\" checked=\"checked\" /><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" /><label for=\"notifyOption2\">Bcc</label></td>");
						} else {
							out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\"/><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" checked=\"checked\"/><label for=\"notifyOption2\">Bcc</label></td>");
						}
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
						out.println("<td headers=\"\" valign=\"top\"><span class=\"small\">[Notifications are sent via internet e-mail, with the e-mail addresses listed in the \"To\" field of the e-mail header. If you choose \"Bcc\" as the e-mail notification option, then all e-mail addresses are listed in the blind copy (Bcc) field of the e-mail header, thus, hiding the list of notification recipients.]</span></td>");
						out.println("</tr>");

						out.println("</table>");
						out.println("</td></tr></table>");
						out.println("</td></tr></table>");

						out.println("<br />");
						out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
						out.println("<td headers=\"\"  align=\"left\">");
						out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\" >&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
						out.println("  <noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
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
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_title\">Title:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"text\"  class=\"iform\" name=\"cal_subject\" id=\"label_title\" maxlength=\"100\" size=\"40\" value=\"" + cal.getSSubject() + "\" /></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_desc\">Description:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">[ Maximum 1000 chars. ]</span><br /><textarea cols=\"40\"  class=\"iform\" rows=\"5\" name=\"cal_desc\" id=\"label_desc\" maxlength=\"1000\">" + cal.getSDescription() + "</textarea></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_date\">Date:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
						showDateTime(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10),sTempDate.substring(0, 4),sHour,sMin,sAMPM,"cal_month","cal_day","cal_year","cal_hour","cal_min","cal_ampm","label_date","label_date");
						out.println("</td>");
						out.println("</tr>");
						
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_edate\">End Time:</label></b></td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\">");

						//showDate(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10), sTempDate.substring(0, 4),"cal_month","cal_day","cal_year","label_date");
						showTime(out,sHour1,sMin1,sAMPM,"cal_ehour","cal_emin","cal_eampm","label_edate");

						out.println("</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">Your event will be visible to users after you click Submit and until it expires. Select a date to indicate when the user should attend to your event.</span></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_duration\">Repeats for</label>:</b></td>");
						out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
						showDuration(out,String.valueOf(cal.getIDuration()),"cal_duration","label_duration");
						out.println(" days.</td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_invitees\">Applies to</label></b>:</td>");
						if (sInvitees.indexOf("ALL") >= 0) {
							out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\" selected=\"selected\" checked=\"checked\" /><label for=\"label_members\">All members</label></td>");
						} else {
							out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\" /><label for=\"label_members\">All members</label></td>");
						}
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\">&nbsp;</td>");
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</span><br />" + displayInviteesAsSelect(con,"cal_invitees","label_invitees",proj.getProjectId(),sInvitees,sScheduleBy) + "</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Notification:</b></td>");
						if (sEmailFlag.trim().equalsIgnoreCase("Y")) {
							out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" checked=\"checked\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify selected team members through e-mail about this event.</span></td>");
						} else {
							out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify selected team members through e-mail about this event.</span></td>");
						}
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>E-mail notification option:</b></td>");
						if (sNotifyType.equalsIgnoreCase("T")) {
							out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\" checked=\"checked\" /><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" /><label for=\"notifyOption2\">Bcc</label></td>");
						} else {
							out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\"/><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" checked=\"checked\" /><label for=\"notifyOption2\">Bcc</label></td>");
						}
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
						out.println("<td headers=\"\" valign=\"top\"><span class=\"small\">[Notifications are sent via internet e-mail, with the e-mail addresses listed in the \"To\" field of the e-mail header. If you choose \"Bcc\" as the e-mail notification option, then all e-mail addresses are listed in the blind copy (Bcc) field of the e-mail header, thus, hiding the list of notification recipients.]</span></td>");
						out.println("</tr>");

						out.println("</table>");
						out.println("</td></tr></table>");
						out.println("</td></tr></table>");

						out.println("<br />");
						out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
						out.println("<td headers=\"\"  align=\"left\">");
						out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\" >&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
						out.println("  <noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
						out.println("</td></tr></table>");
					}

				} else {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td headers=\"\" ><b>Calendar entry not found.</b></td></tr></table>");
				}

			} else {

				// this is an add new operation.

				String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");

				String sStartMonth = sTodaysDate.substring(0, 2);
				String sStartDate = sTodaysDate.substring(3, 5);
				String sStartYear = sTodaysDate.substring(6, 10);

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"100%\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
				out.println("</tr>");
				out.println("</table>");


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
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_title\">Title:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"text\" name=\"cal_subject\" id=\"label_title\" maxlength=\"100\" size=\"40\" value=\"\" /></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_desc\">Description:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">[ Maximum 1000 chars. ]</span><br /><textarea cols=\"40\" rows=\"5\" name=\"cal_desc\" id=\"label_desc\" maxlength=\"1000\"></textarea></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_date\">Date:</label></b></td>");
						out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\" align=\"left\">");
						showDate(out,sStartMonth,sStartDate,sStartYear,"cal_month","cal_day","cal_year","label_date");
						out.println("</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">Your message will be visible to users after you click Submit and until it expires. Select a date to indicate when the user should attend to your message.</span></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_duration\">Expires in:</label></b></td>");
						out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
						showDuration(out,"14","cal_duration","label_duration");
						out.println(" days.</td>");
						out.println("</tr>");
						
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"150\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_invitees\">Applies to</label></b>:</td>");
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\" /><label for=\"label_members\">All members</label></td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"150\" align=\"left\">&nbsp;</td>");
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</span><br />" + displayInviteesAsSelect(con,"cal_invitees","label_invitees",proj.getProjectId(),"",sIRUserID) + "</td>");
						out.println("</tr>");
						

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Notification:</b></td>");
						if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
							out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify selected team members through e-mail about this Message.</span></td>");
						} else {
							out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify selected team members through e-mail about this message.</span></td>");
						}
						out.println("</tr>");
						
						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>E-mail notification option:</b></td>");
						out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\" checked=\"checked\" /><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" /><label for=\"notifyOption2\">Bcc</label></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"150\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
						out.println("<td headers=\"\" valign=\"top\"><span class=\"small\">[Notifications are sent via internet e-mail, with the e-mail addresses listed in the \"To\" field of the e-mail header. If you choose \"Bcc\" as the e-mail notification option, then all e-mail addresses are listed in the blind copy (Bcc) field of the e-mail header, thus, hiding the list of notification recipients.]</span></td>");
						out.println("</tr>");

						out.println("</table>");
						out.println("</td></tr></table>");
						out.println("</td></tr></table>");

						out.println("<br />");
						out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
						out.println("<td headers=\"\"  align=\"left\">");
						out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\" >&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
						out.println("  <noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
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
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_title\">Title:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"text\" name=\"cal_subject\" id=\"label_title\" maxlength=\"100\" size=\"40\" value=\"\" /></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_desc\">Description:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">[ Maximum 1000 chars. ]</span><br /><textarea cols=\"40\" rows=\"5\" name=\"cal_desc\" id=\"label_desc\" maxlength=\"1000\"></textarea></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_date\">Date:</label></b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
						showDateTime(out,sStartMonth,sStartDate,sStartYear,"08","00","AM","cal_month","cal_day","cal_year","cal_hour","cal_min","cal_ampm","label_date","label_date");
						out.println("</td>");
						out.println("</tr>");
						
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_edate\">End Time:</label></b></td>");
						out.println("<td headers=\"\" width=\"343\" align=\"left\">");
						//showDate(out,sTempDate.substring(5, 7),sTempDate.substring(8, 10), sTempDate.substring(0, 4),"cal_month","cal_day","cal_year","label_date");
						showTime(out,"09","00","AM","cal_ehour","cal_emin","cal_eampm","label_edate");
						out.println("</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span class=\"small\">Your event will be visible to users after you click Submit and until it expires. Select a date to indicate when the user should attend to your event.</span></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><span style=\"color: #cc6600;\">*</span><b><label for=\"label_duration\">Repeats for</label>:</b></td>");
						out.println("<td headers=\"\"  valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">");
						showDuration(out,"1","cal_duration","label_duration");
						out.println(" days.</td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_invitees\">Applies to</label></b>:</td>");
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"checkbox\" name=\"all_members\" id=\"label_members\" value=\"ALL\" /><label for=\"label_members\">All members</label></td>");
						out.println("</tr>");
		
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"100\" align=\"left\">&nbsp;</td>");
						out.println("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</span><br />" + displayInviteesAsSelect(con,"cal_invitees","label_invitees",proj.getProjectId(),"",sIRUserID) + "</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>Notification:</b></td>");
						out.println("<td headers=\"\"   valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><input type=\"checkbox\" class=\"iform\" name=\"cal_notify\" id=\"label_notify\" value=\"Y\" />&nbsp;&nbsp;<label for=\"label_notify\">Notify selected team members through e-mail about this event.</span></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\"><b>E-mail notification option:</b></td>");
						out.println("<td headers=\"\" valign=\"top\"><input type=\"radio\" id=\"notifyOption1\" name=\"notifyOption\" value=\"t\" checked=\"checked\" /><label for=\"notifyOption1\">To</label>&nbsp;&nbsp;&nbsp;<input type=\"radio\" id=\"notifyOption2\" name=\"notifyOption\" value=\"b\" /><label for=\"notifyOption2\">Bcc</label></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"100\" valign=\"top\" style=\"background-color: #ffffff; color: #000000; font-weight: normal\">&nbsp;</td>");
						out.println("<td headers=\"\" valign=\"top\"><span class=\"small\">[Notifications are sent via internet e-mail, with the e-mail addresses listed in the \"To\" field of the e-mail header. If you choose \"Bcc\" as the e-mail notification option, then all e-mail addresses are listed in the blind copy (Bcc) field of the e-mail header, thus, hiding the list of notification recipients.]</span></td>");
						out.println("</tr>");

						out.println("</table>");
						out.println("</td></tr></table>");
						out.println("</td></tr></table>");

						out.println("<br />");
						out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
						out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
						out.println("<td headers=\"\"  align=\"left\">");
						out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\" >&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
						out.println("  <noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
						out.println("</td></tr></table>");
					}


			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
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

		out.println("</select>Eastern US");
		out.println("</td></tr></table>");

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

		out.println("</select>Eastern US");
		out.println("</td></tr></table>");

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


}
