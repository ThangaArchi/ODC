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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

public class ETSCalendar {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.28";

	private static Log logger = EtsLogger.getLogger(ETSCalendar.class);
	
	/* Constants below added for 7.1.1 MSA dummy meetings, KP*/
	private static final int UPCOMING_EXEC = 1;
	private static final int UPCOMING_GENERAL = 2;
	private static final int TODAYS_EXEC = 3;
	private static final int TODAYS_GENERAL = 4;
	private static final int PREV_EXEC = 5;
	private static final int PREV_GENERAL = 6;
	private static final int MEETING_EVENT_DATE__EXEC = 7;
	private static final int MEETING_EVENT_DATE__GENERAL = 8;
	private static final int REPEAT_MEETINGS = 9;
	private static final int AFFECTED_MEETINGS = 10;
	
	public static Vector getUpcomingMeetings(ETSParams params) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vMeetings = new Vector();

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
				// display all the meetings if admin or executive...
				// changed for 4.4.1
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				sQuery.append("DATE(START_TIME) >= DATE(CURRENT TIMESTAMP) "+escapeMSA(UPCOMING_EXEC)+" ORDER BY START_TIME for READ ONLY ");
			} else {
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				sQuery.append("DATE(START_TIME) >= DATE(CURRENT TIMESTAMP) "+escapeMSA(UPCOMING_GENERAL)+" ORDER BY START_TIME for READ ONLY ");
			}


			logger.debug("ETSCalendar::getUpcomingEvents()::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sProjectID = proj.getProjectId();
				String sCalType = "m";
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
                String sPassCode = ETSUtils.checkNull(rs.getString("PASS_CODE"));
				String sRepeatType = ETSUtils.checkNull(rs.getString("REPEAT_TYPE"));
				String sRepeatID = ETSUtils.checkNull(rs.getString("REPEAT_ID"));
				Timestamp tRepeatEnd = rs.getTimestamp("REPEAT_END");
				int iFolderId = rs.getInt("FOLDER_ID");
                

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
                cal.setSPass(sPassCode);
                cal.setSRepeatType(sRepeatType);
                cal.setSRepeatID(sRepeatID);
                cal.setSRepeatEnd(tRepeatEnd);
                cal.setFolderId(iFolderId);

				vMeetings.addElement(cal);

			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vMeetings;

	}




	public static Vector getTodaysEvents(ETSParams params, String sDate) throws SQLException, Exception {

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
			
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				sQuery.append("'" + sDate  + "' BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS) for READ ONLY");
			} else {
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				sQuery.append("'" + sDate  + "' BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS) for READ ONLY");
			}

			logger.debug("ETSCalendar::getUpcomingEvents()::QUERY : " + sQuery.toString());

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
                String sCallIn = ETSUtils.checkNull(rs.getString("CALLIN_NUMBER"));
                String sPass = ETSUtils.checkNull(rs.getString("PASS_CODE"));
                Timestamp sEndTime = rs.getTimestamp("REPEAT_END");

                if ((sEndTime == null)|| (sEndTime.equals(""))){
                	sEndTime = tStartTime;
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
                cal.setSCallIn(sCallIn);
                cal.setSPass(sPass);
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


	public static Vector getPreviousMeetings(ETSParams params) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vMeetings = new Vector();

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();

			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
				// display all the meetings if admin or executive...
				// changed for 4.4.1
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				sQuery.append("DATE(START_TIME) < DATE(CURRENT TIMESTAMP) "+escapeMSA(PREV_EXEC)+" ORDER BY START_TIME DESC for READ ONLY ");
			} else {
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				sQuery.append("DATE(START_TIME) < DATE(CURRENT TIMESTAMP) "+escapeMSA(PREV_GENERAL)+" ORDER BY START_TIME DESC for READ ONLY ");
			}

			logger.debug("ETSCalendar::getUpcomingEvents()::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sProjectID = proj.getProjectId();
				String sCalType = "m";
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
				int iFolderId = rs.getInt("FOLDER_ID");

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
                cal.setFolderId(iFolderId);

				vMeetings.addElement(cal);

			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vMeetings;

	}


	public static Vector getUpcomingEvents(ETSParams params) throws SQLException, Exception {

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
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
				//sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				//sQuery.append("DATE(CURRENT TIMESTAMP) BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS) UNION ");
				//sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				//sQuery.append("DATE(START_TIME) > DATE(CURRENT TIMESTAMP) ORDER BY START_TIME for READ ONLY ");
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' ");
				sQuery.append("AND ((DATE(CURRENT TIMESTAMP) BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS)) OR DATE(START_TIME) > DATE(CURRENT TIMESTAMP)) ");
				sQuery.append("ORDER BY START_TIME for READ ONLY ");
				
			} else {
				//sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				//sQuery.append("DATE(CURRENT TIMESTAMP) BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS) UNION ");
				//sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				//sQuery.append("DATE(START_TIME) > DATE(CURRENT TIMESTAMP) ORDER BY START_TIME for READ ONLY ");
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "') ");
				sQuery.append("AND ((DATE(CURRENT TIMESTAMP) BETWEEN DATE(START_TIME) AND DATE(START_TIME + (DURATION -1) DAYS)) OR DATE(START_TIME) > DATE(CURRENT TIMESTAMP)) ");
				sQuery.append("ORDER BY START_TIME for READ ONLY ");
			}

			logger.debug("ETSCalendar::getUpcomingEvents()::QUERY : " + sQuery.toString());

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
                String sCallIn = ETSUtils.checkNull(rs.getString("CALLIN_NUMBER"));
                String sPass = ETSUtils.checkNull(rs.getString("PASS_CODE"));
                Timestamp sEndTime = rs.getTimestamp("REPEAT_END");
                
                if ((sEndTime == null)|| (sEndTime.equals(""))){
                	sEndTime = tStartTime;
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
                cal.setSCallIn(sCallIn);
                cal.setSPass(sPass);
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



	/**
	 * Method displayCalendar.
	 * @param con
	 * @param request
	 * @param out
	 * @param sProjectId
	 * @param iTopCat
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean displayCalendar(ETSParams params) throws SQLException, Exception {

		boolean bDisplayed = false;
		
		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			HttpServletRequest request = params.getRequest();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			String sLinkId = params.getLinkId();
			int iTopCat = params.getTopCat();
			ETSProjectInfoBean projBean = params.getProjBeanInfo();

			Hashtable hMeetingDates = getMeetingEventDates(params);
			
			int iMeetingsTopCat = 0;
			iMeetingsTopCat = ETSDatabaseManager.getTopCatId(proj.getProjectId(), Defines.MEETINGS_VT);
			
			// display calendar if meetings tab is displayed.
			if (iMeetingsTopCat > 0) {
				
				bDisplayed = true;
				
				String sInDate = ETSUtils.checkNull(request.getParameter("showdate"));
				String sMeetId = ETSUtils.checkNull(request.getParameter("meetid"));
	
			    Calendar cal = Calendar.getInstance();
	
	
			   	String sInMonth = projBean.getSMonth();
			   	if (sInMonth == null) {
			   		sInMonth = "";
			   	} else {
			   		sInMonth = sInMonth.trim();
			   	}
	
			   	String sInYear = projBean.getSYear();
			   	if (sInYear == null) {
			   		sInYear = "";
			   	} else {
			   		sInYear = sInYear.trim();
			   	}
	
			   	String sETSOp = request.getParameter("etsop");
			   	if (sETSOp == null) {
			   		sETSOp = "";
			   	} else {
			   		sETSOp = sETSOp.trim();
			   	}
	
			   	boolean bDisplayLinks = true;
	
			   	if (sETSOp.trim().equalsIgnoreCase("newmeeting") || sETSOp.trim().equalsIgnoreCase("editmeeting") || sETSOp.trim().equalsIgnoreCase("insertmeeting") || sETSOp.trim().equalsIgnoreCase("updatemeeting")) {
			   		bDisplayLinks = false;
			   	}
	
			   	if (!sInMonth.trim().equals("") && !sInYear.trim().equals("")) {
			   		logger.debug("ETS Calendar : Calendar Month : " + sInMonth);
			   		logger.debug("ETS Calendar : Calendar Year  : " + sInYear);
				    cal.set(Integer.parseInt(sInYear),Integer.parseInt(sInMonth),01);
			   	} else {
			   		cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),01);
			   	}
	
			    String todayDate = "";
			    int month = cal.get(Calendar.MONTH) + 1;
	
			    if (month > 9) {
			        todayDate = String.valueOf(month);
			    } else {
			        todayDate = "0" + String.valueOf(month);
			    }
	
			    int day = cal.get(Calendar.DAY_OF_MONTH);
			    todayDate = todayDate + "/";
			    if (day > 9) {
			        todayDate = todayDate + String.valueOf(day);
			    } else {
			        todayDate = todayDate + "0" + String.valueOf(day);
			    }
	
			    int year = cal.get(Calendar.YEAR);
			    todayDate = todayDate + "/" + String.valueOf(year);
	
			    int iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	
			    int iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
	
			    int iDay = cal.get(Calendar.DAY_OF_WEEK);
	
			    int iMonth = cal.get(Calendar.MONTH);
	
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
				out.println("<tr><td headers=\"\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" class=\"tblue\" height=\"18\" width=\"150\"><b>&nbsp;&nbsp;Project calendar</b></td>");
				out.println("</tr>");
				out.println("</table>");
	
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" class=\"tblue\" valign=\"top\" width=\"150\">");
	
				out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\">");
				out.println("<tr style=\"background-color: #ffffff; color: #000000;\">");
				out.println("<td headers=\"\" valign=\"top\" width=\"150\">");
	
				out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");
				out.println("<tr valign=\"middle\">");
	
				if (bDisplayLinks) {
	
				    switch (iMonth) {
	
				    	case (0) :
				    		out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">January " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=11&year=" + (year - 1) + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
				    		break;
				    	case (1) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">February " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (2) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">March " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (3) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">April " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"   align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (4) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">May " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (5) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">June " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"   align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (6) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">July " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (7) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">August " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (8) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">September " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (9) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">October " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (10) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">November " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth + 1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    	case (11) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">December " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\" align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=" + (iMonth -1) + "&year=" + year + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&month=0&year=" + (year + 1) + "&meetid=" + sMeetId + "&showdate=" + sInDate + "&etsop=" + sETSOp + "&linkid=" + sLinkId + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					    	break;
				    }
				} else {
				    switch (iMonth) {
	
				    	case (0) :
				    		out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">January " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
				    		break;
				    	case (1) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">February " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (2) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">March " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (3) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">April " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"   align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (4) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">May " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (5) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">June " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"   align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (6) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">July " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (7) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">August " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (8) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">September " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (9) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">October " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (10) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">November " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    	case (11) :
					    	out.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">December " + year + "</td><td headers=\"\"  width=\"21\" valign=\"middle\" align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"middle\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					    	break;
				    }
				}
				out.println("<tr valign=\"middle\">");
			    out.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>S</b></td>");
			    out.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>M</b></td>");
			    out.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>T</b></td>");
			    out.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>W</b></td>");
			    out.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>T</b></td>");
			    out.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>F</b></td>");
			    out.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>S</b></td>");
			    out.println("</tr>");
	
				int iCount = 1;
				int iDayCount = 1;
	
				for (int i = 1; i <= 42; i++) {
	
					if (iCount == 1) {
					    out.println("<tr>");
					}
	
					if (i < iDay || iDayCount > iMaxDaysInMonth ) {
					    out.println("<td headers=\"\"  width=\"21\" align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					} else {
	
						String sDisplayDay = "";
						String sDisplayMonth = "";
						String sDisplayYear = "";
	
						if (iDayCount <= 9) {
							sDisplayDay = "0" + String.valueOf(iDayCount);
						} else {
							sDisplayDay = String.valueOf(iDayCount);
						}
	
						if (iMonth + 1 <= 9) {
							sDisplayMonth = "0" + String.valueOf(iMonth + 1);
						} else {
							sDisplayMonth = String.valueOf(iMonth + 1);
						}
	
						String sAvailable = (String) hMeetingDates.get(sDisplayMonth + "/" + sDisplayDay + "/" + String.valueOf(year));
						if (sAvailable == null || sAvailable.trim().equals("")) {
							sAvailable = "N";
						} else {
							sAvailable = sAvailable.trim();
						}
	
						if (sAvailable.equalsIgnoreCase("Y")) {
							if (bDisplayLinks) {
								out.println("<td headers=\"\"  width=\"21\" align=\"center\" style=\"background-color: #eeeeee; font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=caldetail&linkid=" + sLinkId + "&showdate=" + sDisplayMonth + "/" + sDisplayDay + "/" + year +"\">" + iDayCount + "</a></td>");
							} else {
								out.println("<td headers=\"\"  width=\"21\" align=\"center\" style=\"background-color: #cccccc; color: #000000;  font-weight: normal;\">" + iDayCount + "</td>");
							}
						} else {
							out.println("<td headers=\"\"  width=\"21\" align=\"center\" style=\"background-color: #cccccc; color: #000000;  font-weight: normal;\">" + iDayCount + "</td>");
						}
	
						iDayCount++;
					}
	
				    iCount++;
	
					if (iCount == 8) {
					    out.println("</tr>");
					    iCount = 1;
					}
	
	
				}
	
				out.println("</td></tr></table>");
	
				if (bDisplayLinks) {

					// dont display add an event link for eecutive and visitor..
					// changed for 4.4.1
					if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MEMBER)) {
	
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Add an event\" border=\"0\" /></a></td>");
						out.println("<td headers=\"\" style=\"font-weight: normal\">");
						out.println("<a href=\"" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalEditServlet.wss?proj=" + proj.getProjectId() + "&caltype=E','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Add an event</a>");
						out.println("</td>");
						out.println("</tr>");
						out.println("</table>");
					}
	
					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\" style=\"font-weight: normal\">");
	
					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					out.println("<tr>");
					out.println("<td headers=\"\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"7\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\"><b>Upcoming events</b></td>");
	
					boolean bDisplayMoreAlerts = false;
	
					Vector vUpcomingEvents = getUpcomingEvents(params);
	
					if (vUpcomingEvents != null && vUpcomingEvents.size() != 0) {
	
						bDisplayMoreAlerts = true;
	
						for (int i = 0; i < vUpcomingEvents.size(); i++) {
	
							ETSCal calEvent = (ETSCal) vUpcomingEvents.elementAt(i);
							String sTempDate = calEvent.getSStartTime().toString();
							String sTmpEndDate = calEvent.getSRepeatEnd().toString();
	
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
	
							int iDuration = calEvent.getIDuration();
	
							// printing separator
							out.println("</tr>");
							out.println("<tr>");
							out.println("<td headers=\"\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"7\" alt=\"\" /></td>");
							out.println("</tr>");
							out.println("<tr>");
							out.println("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
							out.println("</tr>");
							out.println("<tr>");
							out.println("<td headers=\"\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
							out.println("</tr>");
							// end separator
	
							out.println("<tr>");
							out.println("<td headers=\"\">");
							out.println("<span style=\"color:#666666\"><span class=\"small\"><b>" + sDate + "</b>&nbsp;" + sHour + ":" + sMin +  sAMPM.toLowerCase() + "<br />Eastern&nbsp;US&nbsp;to&nbsp;" + sEndHour + ":" + sEndMin +  sEndAMPM.toLowerCase() + "<br />Eastern&nbsp;US</span></span>");

							//out.println("<br />");
							if (iDuration > 1) {
								out.println("<br />");
								out.println("<span class=\"small\">(Repeats for <b>" + String.valueOf(iDuration) + "</b> days)</span>");
								out.println("<br />");
							}
							out.println("<a href=\"" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEvent.getSCalendarId() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEvent.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss?proj=" + proj.getProjectId() + "&caltype=E&calid=" + calEvent.getSCalendarId() + "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">" + calEvent.getSSubject() + "</a>");
							out.println("</td>");
							out.println("</tr>");
	
							if (i >= 1) {
								break;
							}
						}
					} else {
	
						// printing separator
						out.println("</tr>");
						out.println("<tr>");
						out.println("<td headers=\"\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"7\" alt=\"\" /></td>");
						out.println("</tr>");
						out.println("<tr>");
						out.println("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
						out.println("</tr>");
						out.println("<tr>");
						out.println("<td headers=\"\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
						out.println("</tr>");
						// end separator
	
						out.println("<tr>");
						out.println("<td headers=\"\">");
						out.println("<span style=\"color:#666666\"><b>There are no upcoming events at this time.</span>");
						out.println("</td>");
						out.println("</tr>");
	
					}
					out.println("</table>");
	
					if (bDisplayMoreAlerts) {
						out.println("<br />");
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=moreevents&linkid=" + sLinkId + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"More events\" border=\"0\" /></a></td>");
						out.println("<td headers=\"\">");
						out.println("<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + iTopCat + "&proj=" + proj.getProjectId() + "&etsop=moreevents&linkid=" + sLinkId + "\" class=\"fbox\">More events</a>");
						out.println("</td>");
						out.println("</tr>");
						out.println("</table>");
					}
	
	                //out.println("</td></tr></table>");
	                out.println("</td></tr></table>");
	
	
				}
	
				out.println("</td></tr></table>");
				out.println("</td></tr></table>");

			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
		}
		
		return bDisplayed;

	}

	/**
	 * Method getMeetingEventDates.
	 * @param con
	 * @param sProjectId
	 * @param sIRId
	 * @return Hashtable
	 * @throws SQLException
	 * @throws Exception
	 */
	private static Hashtable getMeetingEventDates(ETSParams params) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Hashtable hDates = new Hashtable();

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
				// display all the meetings if admin or executive...
				// changed for 4.4.1
				sQuery.append("SELECT START_TIME,CALENDAR_TYPE,DURATION FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE IN ('M','E') AND PROJECT_ID = '" + proj.getProjectId() + "' "+escapeMSA(MEETING_EVENT_DATE__EXEC)+" ORDER BY START_TIME,CALENDAR_TYPE for READ ONLY");
			} else {			
				sQuery.append("SELECT START_TIME,CALENDAR_TYPE,DURATION FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR SCHEDULED_BY = '" + es.gIR_USERN + "') UNION ");
				sQuery.append("SELECT START_TIME,CALENDAR_TYPE,DURATION FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'E' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '" + es.gIR_USERN + "')  "+escapeMSA(MEETING_EVENT_DATE__GENERAL)+" ORDER BY START_TIME,CALENDAR_TYPE for READ ONLY");
			}


			logger.debug("ETSCalendar::getMeetingEventDates()::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sDate = rs.getString("START_TIME");
				String sCalendarType = ETSUtils.checkNull(rs.getString("CALENDAR_TYPE"));
				int iDuration = rs.getInt("DURATION");

				if (sCalendarType.trim().equalsIgnoreCase("M")) {
					// meeting entry...
					//formatting in mm/dd/yyyy format and store as key...
					hDates.put(sDate.substring(5, 7) + "/" + sDate.substring(8, 10) + "/" + sDate.substring(0, 4),"Y");

				} else {
					// events entry...

					for (int i = 0; i < iDuration; i++) {

						Calendar cal = Calendar.getInstance();
						cal.clear();

						String sDay = sDate.substring(8,10);
						int iDay = Integer.parseInt(sDay) + i;

						cal.set(Integer.parseInt(sDate.substring(0, 4)),Integer.parseInt(sDate.substring(5, 7)) -1,iDay);

						String sMonth = "";
						int month = cal.get(Calendar.MONTH) + 1;
					    if (month > 9) {
					        sMonth = String.valueOf(month);
					    } else {
					        sMonth= "0" + String.valueOf(month);
					    }

						String sCurrentDay = "";
					    int day = cal.get(Calendar.DAY_OF_MONTH);
					    if (day > 9) {
					        sCurrentDay = String.valueOf(day);
					    } else {
					        sCurrentDay = "0" + String.valueOf(day);
					    }

						String sYear = "";
					    int year = cal.get(Calendar.YEAR);
					    sYear =  String.valueOf(year);

						hDates.put(sMonth + "/" + sCurrentDay + "/" + sYear,"Y");

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

		return hDates;
	}


	public static Vector getTodaysMeetings(ETSParams params, String sDate) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vEvents = new Vector();

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();

			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
				// display all the meetings if admin or executive...
				// changed for 4.4.1
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + proj.getProjectId() + "' AND ");
				sQuery.append("DATE(START_TIME) >= '" + sDate + "' AND DATE(START_TIME) <= '" + sDate + "' "+escapeMSA(TODAYS_EXEC)+" ORDER BY START_TIME for READ ONLY ");
			} else {
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ',' LIKE '%," + es.gIR_USERN + ",%' OR SCHEDULED_BY = '" + es.gIR_USERN + "') AND ");
				sQuery.append("DATE(START_TIME) >= '" + sDate + "' AND DATE(START_TIME) <= '" + sDate + "' "+escapeMSA(TODAYS_GENERAL)+" ORDER BY START_TIME for READ ONLY ");
			}

			logger.debug("ETSCalendar::getUpcomingEvents()::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sProjectID = proj.getProjectId();
				String sCalType = "m";
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
                String sPassCode = ETSUtils.checkNull(rs.getString("PASS_CODE"));
				String sRepeatType = ETSUtils.checkNull(rs.getString("REPEAT_TYPE"));
				String sRepeatID = ETSUtils.checkNull(rs.getString("REPEAT_ID"));
				Timestamp tRepeatEnd = rs.getTimestamp("REPEAT_END");
				int iFolderId = rs.getInt("FOLDER_ID");

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
                cal.setSPass(sPassCode);
                cal.setSRepeatType(sRepeatType);
                cal.setSRepeatID(sRepeatID);
                cal.setSRepeatEnd(tRepeatEnd);
                cal.setFolderId(iFolderId);

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

	public static synchronized String getNewCalendarId() throws SQLException, Exception {

		String sUniqueId = "";

		Long lDate = new Long(System.currentTimeMillis());

		sUniqueId = lDate + "-" + generateRandomNumber(9999);

		return sUniqueId;
	}

	/**
	 * Generates random numbers between 0 and the max value passed.
	 */
	private static String generateRandomNumber(int iMaxValue) {

		Random rand = new Random();

		String sRandomValue = rand.nextInt(iMaxValue) + "";

		return sRandomValue;

	}




	/**
	 * @param conn
	 * @param sCalendarId
	 * @return
	 */
	public static Vector getUpdatableRepeatMeetings(Connection conn, String sProjectID,String sMeetingID) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vMeetings = new Vector();

		try {

			sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + sProjectID + "' ");
			sQuery.append("AND REPEAT_ID IN (SELECT REPEAT_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + sProjectID + "' AND CALENDAR_ID = '" + sMeetingID + "')");
			sQuery.append("AND DATE(START_TIME) > (SELECT DATE(START_TIME) FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + sProjectID + "' AND CALENDAR_ID = '" + sMeetingID + "')");
			sQuery.append(" "+escapeMSA(REPEAT_MEETINGS)+" ORDER BY START_TIME for READ ONLY ");

			logger.debug("ETSCalendar::getAllRepeatMeetings()::QUERY : " + sQuery.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sCalType = "M";
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
				int iFolderId = rs.getInt("FOLDER_ID");

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
				cal.setFolderId(iFolderId);

				vMeetings.addElement(cal);

			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vMeetings;
	}




	/**
	 * @param conn
	 * @param vAffectedEntries
	 * @return
	 */
	public static Vector getAffectedMeetingDetails(Connection conn, String sProjectID, Vector vAffectedEntries) throws SQLException, Exception {
		

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vMeetings = new Vector();

		try {
			
			if (vAffectedEntries != null && vAffectedEntries.size() > 0) {
				
				// make it as 'id','id' format..
				
				String sIDs = "";
				
				for (int i = 0; i < vAffectedEntries.size(); i++) {
					
					if (sIDs.trim().equalsIgnoreCase("")) {
						sIDs = "'" + vAffectedEntries.elementAt(i) + "'";
					} else {
						sIDs = sIDs + ",'" + vAffectedEntries.elementAt(i) + "'";
					}
				}
				
								
				sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + sProjectID + "' ");
				sQuery.append("AND CALENDAR_ID IN (" + sIDs + ")");
				sQuery.append(" "+escapeMSA(AFFECTED_MEETINGS)+" ORDER BY START_TIME for READ ONLY ");

				logger.debug("ETSCalendar::getAffectedMeetingDetails()::QUERY : " + sQuery.toString());
			
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sQuery.toString());

				while (rs.next()) {

					String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
					String sCalType = "M";
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
					int iFolderId = rs.getInt("FOLDER_ID");

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
					cal.setFolderId(iFolderId);
				
					vMeetings.addElement(cal);

				}
				
			} else {
				vMeetings = new Vector();
			}



		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vMeetings;
		
	}

	public static Vector getMeetingsDetails(Connection con, String sProjectId, String sMeetingId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vEvents = new Vector();

		try {

			sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,NOTIFY_TYPE,FOLDER_ID FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + sProjectId + "' AND CALENDAR_ID = '" + sMeetingId + "' ");
			sQuery.append("for READ ONLY ");

			logger.debug("ETSCalendar::getMeetingsDetails()::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sCalType = "M";
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
				int iFolderId = rs.getInt("FOLDER_ID");

				ETSCal cal = new ETSCal();
				cal.setSCalendarId(sCalID);
				cal.setSProjectId(sProjectId);
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
				cal.setNotifyType(sNotifyType);
				cal.setFolderId(iFolderId);

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
	
	/**
	 * @param key
	 * @return AND clause to ignore MSA dummy meetings
	 * @author KP
	 * @since 7.1.1
	 */
	private static String escapeMSA(int key){
		if(key==UPCOMING_EXEC || key==UPCOMING_GENERAL ||
				key==TODAYS_EXEC || key==TODAYS_GENERAL ||
				key==PREV_EXEC || key==PREV_GENERAL ||
				key==MEETING_EVENT_DATE__EXEC || key==MEETING_EVENT_DATE__GENERAL ||
				key==REPEAT_MEETINGS ||
				key==AFFECTED_MEETINGS
		){
			return " and calendar_id not in (select distinct meeting_id from ets.wf_def where wf_type = 'SELF ASSESSMENT' ) ";
			
		}
		return " AND 1=1 "; //This is a harmless AND clause.
	}

}
