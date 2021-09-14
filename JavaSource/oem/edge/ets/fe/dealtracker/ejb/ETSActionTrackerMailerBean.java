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

package oem.edge.ets.fe.dealtracker.ejb;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.ejb.TimedObject;
import javax.mail.internet.InternetAddress;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.dao.TaskHandlerDAO;

import org.apache.commons.logging.Log;

/**
 * Bean implementation class for Enterprise Bean: ETSTaskHandler
 */
public class ETSActionTrackerMailerBean extends BaseSessionBean implements TimedObject {
    
    private static final Log m_pdLog = EtsLogger
            .getLogger(ETSActionTrackerMailerBean.class);

    private static int EMAIL_SLEEP_TIME = 5000; // milliseconds

    private static int EMAIL_NO_TIMES = 5;

    private static int MAX_FILE_SIZE = 10000000;

    private static final String FALSE_FLAG = "0";

    private static final String TRUE_FLAG = "1";

    private static final String NOT_SET_FLAG = "x";

    private static SimpleDateFormat MyDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss");
    
    /**
     * @param status
     */
    public void process() {
        if (m_pdLog.isErrorEnabled()) { 
	        m_pdLog.error("*************************************************");
	        m_pdLog.error("* INSIDE PROCESS FOR ETSActionTrackerMailerBean *");
	        m_pdLog.error("*************************************************");
        }
        TaskHandlerDAO udDAO = new TaskHandlerDAO();
        try {
            udDAO.prepare();
            JobImplementation(udDAO.getConnection());
    }
        catch(SQLException e) {
            m_pdLog.error(e);
        }
        finally {
            udDAO.cleanup();
        }
    }

    public String JobImplementation(Connection conn) throws SQLException {

        if (!Global.loaded) {
            Global.Init();
        }

        try {

            m_pdLog.debug("in deal tracker job implemation");
            sendAlerts(conn);
            m_pdLog.debug("out of deal tracker  job implemation");

            // check to see if the person identified for a step is still valid.
            // if not, then assign the workspace owner for that step.

            m_pdLog.debug("Starting Client Voice validation");
            checkAndAssignOwner(conn);
            m_pdLog.debug("Finished Client Voice validation");

            m_pdLog.debug("Starting Client Voice Reminder notifications");
            sendClientCareAlerts(conn);
            m_pdLog.debug("Finished Client Voice Reminder notifications");

            m_pdLog.debug("Starting Client Voice Reminder notifications");
            sendSelfAssessmentAlerts(conn);
            m_pdLog.debug("Finished Client Voice Reminder notifications");

        }
        catch (Exception e) {
            handleError(e);
            m_pdLog.debug(e);
            m_pdLog.error(e);
        }

        return null;
    }

    public String mailFormat(String str, Connection conn, String user,
            String function, String division, String tag) throws SQLException {
        return null;
    }

    /**
     * Method that sends alerts to the users...
     */
    private static void sendAlerts(Connection con) throws Exception {

        try {
            //sendNotSentEmails(con);
            sendAlertsForUsers(con);

        }
        catch (SQLException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }

    }

    /**
     * Method that sends the alerts for the users.
     */
    private static void sendAlertsForUsers(Connection conn)
            throws SQLException, Exception {
        boolean success = false;

        try {
            String sSubject = "E&TS Connect: Task Due";
            logDebugMessage("etsDTAlerts::sendAlertsForUsers() [Begin sps]");

            // ******************************
            // get users that have created or own tasks that are due
            // ******************************
            Vector vSendUsers = getUsersToSend(conn);

            m_pdLog.debug("vSendUsers.size=" + vSendUsers.size());

            //for each user get tasks
            for (int iUser = 0; iUser < vSendUsers.size(); iUser++) {
                int ocnt = 0;
                int ccnt = 0;

                String ir_userid = (String) vSendUsers.elementAt(iUser);
                Vector tasks = checkIbmEnt(
                        getUserTasks(conn, ir_userid),
                        ir_userid,
                        conn);
                //[0]task_id,[1]project_id,[2]creator_id,[3]createddate,[4]section,[5]title,
                //[6]desc,[7]status,[8]duedate,[9]ownerid,[10]wreq,[11]areq,[12]ibm_only,[13]parentid
                //[14]selfid [15] trackertype

                m_pdLog.debug("tasks = " + tasks.size());

                StringBuffer sMessage = new StringBuffer("");
                StringBuffer scm = new StringBuffer("");
                StringBuffer som = new StringBuffer("");

                boolean bMailAvailable = false;
                // ******************************
                //for each task add to message
                // ******************************
                for (int iTask = 0; iTask < tasks.size(); iTask++) {
                    String sTask[] = (String[]) tasks.elementAt(iTask);

                    String taskid = sTask[0];
                    String project_id = sTask[1];
                    String creator_id = sTask[2];
                    String createddate = sTask[3];
                    String section = sTask[4];
                    String title = sTask[5];
                    String desc = sTask[6];
                    String status = sTask[7];
                    String duedate = sTask[8];
                    String owner_id = sTask[9];
                    String wreq = sTask[10];
                    String areq = sTask[11];
                    String ibm_only = sTask[12];
                    String parent_id = sTask[13];
                    String self_id = sTask[14];
                    String trackerType = sTask[15];

                    boolean cv = false;
                    String cvStr = "&self=";
                    int view_type = 4; //for deal tracker
                    if (!(self_id.trim()).equals("")) {
                        cv = true;
                        if (trackerType.equals("M")) {
                            cvStr = "&set=";
                            view_type = 6; // for set/met
                        }
                        else { //trackerType.equals("A")
                            view_type = 11; // for self assessment
                        }
                    }

                    if (ir_userid.equals(owner_id)) {
                        ocnt++;
                        if (cv && trackerType.equals("M"))
                            som.append(" Set/Met:         "
                                    + convertStr(getSetMetName(
                                            conn,
                                            project_id,
                                            self_id))
                                    + "\n");
                        else if (cv && trackerType.equals("A"))
                            som.append(" Self assessment: "
                                    + convertStr(getSelfName(
                                            conn,
                                            project_id,
                                            self_id))
                                    + "\n");

                        som.append(" Task ID:         "
                                + convertStr(taskid)
                                + "\n");
                        if (!cv)
                            som.append(" Project:         "
                                    + getProjectName(conn, project_id)
                                    + "\n");
                        else
                            som.append(" Project:         "
                                    + getProjectCompany(conn, project_id)
                                    + "\n");
                        som.append(" Created by:      "
                                + getUsersName(conn, creator_id)
                                + " \n");
                        som.append(" Created on:      " + createddate + " \n");
                        if (!cv)
                            som.append(" Section:         "
                                    + convertStr(section)
                                    + " \n");
                        else
                            som.append(" Rel Attribute:   "
                                    + convertStr(section)
                                    + " \n");
                        som.append(" Title:           "
                                + convertStr(title)
                                + " \n");
                        som.append(" Description:     "
                                + convertStr(desc)
                                + " \n");
                        som.append(" Status:          "
                                + convertStr(status)
                                + " \n");
                        som.append(" Due Date:        " + duedate + " \n");
                        som.append(" Owner:           "
                                + getUsersName(conn, owner_id)
                                + " \n");
                        if (!cv)
                            som.append(" Work Required:   "
                                    + convertStr(wreq)
                                    + " \n");
                        som.append(" Action Required: "
                                + convertStr(areq)
                                + " \n");

                        int tc = getTopCat(conn, project_id, view_type);

                        som.append("  URL:             \n");
                        String url = Global
                                .getUrl("ets/ETSProjectsServlet.wss")
                                + "?action=details&taskid="
                                + taskid
                                + "&proj="
                                + project_id
                                + "&tc="
                                + tc
                                + "&cc="
                                + tc
                                + "&linkid=251000";
                        if (cv) {
                            url = Global.getUrl("ets/ETSProjectsServlet.wss")
                                    + "?action=details&taskid="
                                    + taskid
                                    + "&proj="
                                    + project_id
                                    + cvStr
                                    + self_id
                                    + "&etsop=action&tc="
                                    + tc
                                    + "&cc="
                                    + tc
                                    + "&linkid=251000";
                        }
                        som.append("  " + url + "\n\n");
                        som
                                .append("--------------------------------------------------------------\n\n");

                    }
                    else if (ir_userid.equals(creator_id)) {
                        ccnt++;
                        if (cv && trackerType.equals("M"))
                            scm.append(" Set/Met:         "
                                    + convertStr(getSetMetName(
                                            conn,
                                            project_id,
                                            self_id))
                                    + "\n");
                        else if (cv && trackerType.equals("A"))
                            scm.append(" Self assessment: "
                                    + convertStr(getSelfName(
                                            conn,
                                            project_id,
                                            self_id))
                                    + "\n");

                        scm.append(" Task ID:         "
                                + convertStr(taskid)
                                + "\n");
                        if (!cv)
                            scm.append(" Project:         "
                                    + getProjectName(conn, project_id)
                                    + "\n");
                        else
                            scm.append(" Project:         "
                                    + getProjectCompany(conn, project_id)
                                    + "\n");
                        scm.append(" Created by:      "
                                + getUsersName(conn, creator_id)
                                + " \n");
                        scm.append(" Created on:      " + createddate + " \n");
                        if (!cv)
                            scm.append(" Section:         "
                                    + convertStr(section)
                                    + " \n");
                        else
                            scm.append(" Rel Attribute:   "
                                    + convertStr(section)
                                    + " \n");
                        scm.append(" Title:           "
                                + convertStr(title)
                                + " \n");
                        scm.append(" Description:     "
                                + convertStr(desc)
                                + " \n");
                        scm.append(" Status:          "
                                + convertStr(status)
                                + " \n");
                        scm.append(" Due Date:        " + duedate + " \n");
                        scm.append(" Owner:           "
                                + getUsersName(conn, owner_id)
                                + " \n");
                        if (!cv)
                            scm.append(" Work Required:   "
                                    + convertStr(wreq)
                                    + " \n");
                        scm.append(" Action Required: "
                                + convertStr(areq)
                                + " \n");

                        int tc = getTopCat(conn, project_id, view_type);

                        scm.append("  URL:             \n");
                        String url = Global
                                .getUrl("ets/ETSProjectsServlet.wss")
                                + "?action=details&taskid="
                                + taskid
                                + "&proj="
                                + project_id
                                + "&tc="
                                + tc
                                + "&cc="
                                + tc
                                + "&linkid=251000";
                        if (cv) {
                            url = Global.getUrl("ets/ETSProjectsServlet.wss")
                                    + "?action=details&taskid="
                                    + taskid
                                    + "&proj="
                                    + project_id
                                    + cvStr
                                    + self_id
                                    + "&etsop=action&tc="
                                    + tc
                                    + "&cc="
                                    + tc
                                    + "&linkid=251000";
                        }
                        scm.append("  " + url + "\n\n");
                        scm
                                .append("--------------------------------------------------------------\n\n");

                    }

                }

                if ((ccnt + ocnt) > 0) {
                    bMailAvailable = true;
                    if (ocnt > 0) {
                        if (ocnt == 1) {
                            sMessage
                                    .append("The following task you own is due today:\n\n");
                        }
                        else if (ocnt > 1) {
                            sMessage
                                    .append("The following tasks you own are due today:\n\n");
                        }

                        sMessage.append(som);
                        if (ccnt > 0) {
                            sMessage
                                    .append("==============================================================\n\n\n");
                        }
                    }

                    if (ccnt > 0) {
                        if (ccnt == 1) {
                            sMessage
                                    .append("The following task you created is due today:\n\n");
                        }
                        else if (ccnt > 1) {
                            sMessage
                                    .append("The following tasks you created are due today:\n\n");
                        }

                        sMessage.append(scm);
                    }

                    sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));
                }

                //send mail
                if (bMailAvailable) {
                    m_pdLog.debug(sMessage);
                    // get the users email id from amt.
                    //String sEmailId = "sandieps@us.ibm.com";
                    String sEmailId = getUsersEmail(conn, ir_userid);

                    logDebugMessage("etsDTAlerts::sendAlertsForUsers() [Email ID : "
                            + sEmailId
                            + "]");
                    logDebugMessage("etsDTAlerts::sendAlertsForUsers() [Message  : "
                            + sMessage
                            + "]");

                    // send the mail to the user..
                    boolean bSuccess = false;
                    if (!sEmailId.equals("")) {
                        bSuccess = sendEMail(
                                "etsadmin@us.ibm.com",
                                sEmailId,
                                Global.mailHost,
                                sMessage.toString(),
                                sSubject,
                                "");
                    }
                    else {
                        boolean bTemp = sendEMail(
                                "etsadmin@us.ibm.com",
                                "sandieps@us.ibm.com",
                                Global.mailHost,
                                ir_userid,
                                "ERROR ETS DT",
                                "");
                    }

                    if (bSuccess) {
                        logDebugMessage("etsDTAlerts::sendAlertsForUsers() [EMAIL SENT]");
                    }
                    else {
                        logDebugMessage("etsDTAlerts::sendAlertsForUsers() [EMAIL NOT SENT]");
                    }
                }

            } //end of for each user

            logDebugMessage("etsDTAlerts::sendAlertsForUser() [End]");
        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }

    }

    /**
     * Method that gets the users
     */
    private static Vector getUsersToSend(Connection conn) throws SQLException,
            Exception {

        Vector vUser = new Vector();

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");

        try {
            logDebugMessage("etsDTAlerts::getUsersToSend() [Begin]");

            java.util.Date today = new java.util.Date();
            java.sql.Date sqlToday = new java.sql.Date(today.getTime());

            sQuery
                    .append("SELECT DISTINCT u.USER_ID FROM ets.ets_users u"
                            + " where u.USER_ID in (select t1.creator_id from ets.ets_task_main t1 where u.user_id=t1.creator_id"
                            + " and date(t1.due_date)=date('"
                            + sqlToday
                            + "') and date(t1.created_date)!=date('"
                            + sqlToday
                            + "'))"
                            + " or u.user_id in (select t2.owner_id from ets.ets_task_main t2 where u.user_id=t2.owner_id"
                            + " and date(t2.due_date)=date('"
                            + sqlToday
                            + "') and date(t2.created_date)!=date('"
                            + sqlToday
                            + "'))"
                            + " for READ ONLY");

            logDebugMessage("etsDTAlerts::getUsersToSend() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            while (rs.next()) {
                String sId = rs.getString("USER_ID");
                vUser.addElement(sId);
            }

            logDebugMessage("etsDTAlerts::getUsersToSend() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }
        return vUser;
    }

    /**
     * Method that gets the task details
     */
    private static Vector getUserTasks(Connection conn, String irid)
            throws SQLException, Exception {
        Vector vTasks = new Vector();

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");

        try {
            logDebugMessage("etsDTAlerts::getUsersTasks() [Begin]");

            java.util.Date today = new java.util.Date();
            java.sql.Date sqlToday = new java.sql.Date(today.getTime());

            sQuery.append("SELECT t.* FROM ets.ets_task_main t"
                    + " where (t.creator_id='"
                    + irid
                    + "'"
                    + " or t.owner_id='"
                    + irid
                    + "')"
                    + " and date(due_date)=date('"
                    + sqlToday
                    + "') and date(created_date)!=date('"
                    + sqlToday
                    + "')"
                    + " for READ ONLY");

            logDebugMessage("etsDTAlerts::getUsersTasks() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            while (rs.next()) {
                String task_id = rs.getString("task_id");
                String project_id = rs.getString("project_id");
                String creator_id = rs.getString("creator_id");
                Timestamp tcTimestamp = rs.getTimestamp("created_date");
                String createddate = tcTimestamp.toString();
                String section = rs.getString("section");
                String title = rs.getString("title");
                String desc = rs.getString("description");
                String status_temp = rs.getString("status");
                String self_id = rs.getString("self_id");
                if (self_id == null)
                    self_id = "";

                String status = "Green";
                if (self_id.equals("")) {
                    if (status_temp.equals("2")) {
                        status = "Yellow";
                    }
                    else if (status_temp.equals("3")) {
                        status = "Red";
                    }
                }
                else {
                    if (status_temp.equals("1")) {
                        status = "Complete";
                    }
                    else if (status_temp.equals("2")) {
                        status = "In progress";
                    }
                    else if (status_temp.equals("3")) {
                        status = "Not started";
                    }
                }
                Timestamp tTimestamp = rs.getTimestamp("due_date");
                String duedate = tTimestamp.toString();
                String ownerid = rs.getString("owner_id");
                String wreq = rs.getString("work_required");
                String areq = rs.getString("action_required");
                String ibm_only = rs.getString("ibm_only");
                String parentid = rs.getString("parent_task_id");
                String trackerType = rs.getString("tracker_type");

                boolean add = true;
                if (!self_id.equals("")) {
                    if (status_temp.equals("1")) {
                        add = false;
                    }
                    else if (isSelfAssessmentClosed(conn, self_id, project_id)) {
                        add = false;
                    }
                }

                if (add) {
                    String[] s = new String[] { task_id, project_id,
                            creator_id, createddate, section, title, desc,
                            status, duedate, ownerid, wreq, areq, ibm_only,
                            parentid, self_id, trackerType };
                    vTasks.addElement(s);
                }
            }

            logDebugMessage("etsDTAlerts::getUsersToSend() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }
        return vTasks;
    }

    /**
     * Method that gets the task details
     */
    private static Vector checkIbmEnt(Vector tasks, String irid, Connection conn)
            throws SQLException, Exception {
        Vector vTasks = new Vector();
        //[0]task_id,[1]project_id,[2]creator_id,[3]createddate,[4]section,[5]title,
        //[6]desc,[7]status,[8]duedate,[9]ownerid,[10]wreq,[11]areq,[12]ibm_only,[13]parentid

        try {
            logDebugMessage("etsDTAlerts::checkIbmEnt() [Begin]");

            if (checkEnt(irid, conn)) {
                for (int i = 0; i < tasks.size(); i++) {
                    String[] s = (String[]) tasks.elementAt(i);
                    String projectid = s[1];
                    String ibmonly = s[12];

                    if (checkProjStatus(projectid, conn)
                            && checkUserFlag(irid, projectid, conn)) {
                        if (!ibmonly.equals(TRUE_FLAG) || isIBMer(irid, conn)) {
                            vTasks.addElement(s);
                        }
                    }
                }
            }
            logDebugMessage("etsDTAlerts::checkIbmEnt() [End]");
        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {

        }
        return vTasks;
    }

    private static boolean checkEnt(String irid, Connection conn) {
        boolean ent = false;
        Vector userents = new Vector();

        try {
            String edgeuserid = AccessCntrlFuncs.getEdgeUserId(conn, irid);
            userents = AccessCntrlFuncs.getUserEntitlements(
                    conn,
                    edgeuserid,
                    true,
                    true);

            if (userents == null)
                return false;

            if (userents.contains("ETS_ADMIN")
                    || userents.contains("ETS_EXECUTIVE")
                    || userents.contains("ETS_PROJECTS")) {
                return true;
            }
        }
        catch (AMTException e) {
            m_pdLog.error("AMT ERROR in ETS_JOB::checkEnt()");
            e.printStackTrace();
            return false;
        }
        catch (SQLException e) {
            m_pdLog.error("SQL ERROR in ETS_JOB::checkEnt()");
            e.printStackTrace();
            return false;
        }

        return ent;
    }

    private static boolean checkUserFlag(String irid, String projectid,
            Connection conn) throws SQLException, Exception {
        boolean active = false;

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");

        try {
            logDebugMessage("etsDTAlerts::checkUserFlag() [Begin]");

            sQuery.append("SELECT u.active_flag FROM ets.ets_users u"
                    + " where u.user_id='"
                    + irid
                    + "'"
                    + " and u.user_project_id='"
                    + projectid
                    + "'"
                    + " for READ ONLY");

            logDebugMessage("etsDTAlerts::checkUserFlag() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            String active_flag = "";
            if (rs.next()) {
                active_flag = rs.getString("active_flag");
            }

            if (active_flag.equals("A")) {
                active = true;
                return true;
            }
            logDebugMessage("etsDTAlerts::checkUserFlag() [End]");
        }
        catch (SQLException e) {
            e.printStackTrace();
            active = false;
        }
        catch (Exception e) {
            e.printStackTrace();
            active = false;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }
        return active;
    }

    private static boolean checkProjStatus(String projectid, Connection conn)
            throws SQLException, Exception {
        boolean active = false;

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");

        try {
            logDebugMessage("etsDTAlerts::checkProjStatus() [Begin]");

            sQuery.append("SELECT p.project_status FROM ets.ets_projects p"
                    + " where p.project_id='"
                    + projectid
                    + "'"
                    + " for READ ONLY");

            logDebugMessage("etsDTAlerts::checkProjStatus() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            String proj_status = "";
            if (rs.next()) {
                proj_status = rs.getString("project_status");
            }

            if (proj_status.equals("D") || (proj_status.equals("A"))) {
                active = false;
            }
            else {
                active = true;
            }

            logDebugMessage("etsDTAlerts::checkProjStatus() [End]");
        }
        catch (SQLException e) {
            e.printStackTrace();
            active = false;
        }
        catch (Exception e) {
            e.printStackTrace();
            active = false;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }
        return active;
    }

    private static boolean isIBMer(String irid, Connection conn)
            throws SQLException, Exception {
        boolean ibmer = false;
        boolean isibmer = true;

        try {
            String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, irid);
            String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
            if (!decaftype.equalsIgnoreCase("I")) {
                isibmer = false;
            }
        }
        catch (Exception e) {
            isibmer = false;
            e.printStackTrace();
            //throw e;
        }
        finally {
        }

        return isibmer;
    }

    private static boolean isSelfAssessmentClosed(Connection con,
            String self_id, String project_id) throws SQLException, Exception {
        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String state = "";

        try {

            sQuery
                    .append("SELECT STATE FROM ETS.ETS_SELF_MAIN WHERE SELF_ID = '"
                            + self_id
                            + "' AND PROJECT_ID = '"
                            + project_id
                            + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                state = rs.getString("STATE");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            state = "";
        }
        catch (Exception e) {
            e.printStackTrace();
            state = "";
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        m_pdLog.debug("STATE =" + state);
        return (state.equals("CLOSE"));

    }

    //     ==================================================================================

    /**
     * This method wraps the String and pads that string with spaces.
     */
    private static String convertStr(String sInString)
            throws java.lang.Exception {
        StringBuffer sOut = new StringBuffer();
        boolean bBreakFlag = false;
        try {
            if (sInString.length() > 48) {
                for (int i = 0; i < sInString.length(); i++) {
                    if (i % 40 == 0) {
                        if (i > 39) {
                            bBreakFlag = true;
                        }
                    }
                    if (bBreakFlag) {
                        if (sInString.substring(i, i + 1).equals(",")
                                || sInString.substring(i, i + 1).equals(" ")
                                || sInString.substring(i, i + 1).equals(";")) {
                            sOut.append(sInString.substring(i, i + 1));
                            sOut.append("\n                  ");
                            bBreakFlag = false;
                        }
                        else {
                            sOut.append(sInString.substring(i, i + 1));
                        }

                    }
                    else {
                        sOut.append(sInString.substring(i, i + 1));
                    }
                }
            }
            else {
                sOut.append(sInString);
            }

        }
        catch (Exception e) {
            sOut.setLength(0);
            sOut.append(sInString);
        }
        return sOut.toString();

    }

    /**
     * Checks to see if the variable is null or not.
     */
    public static String checkNull(String sInString) {

        String sOutString = "";

        if (sInString == null || sInString.trim().equals("")) {
            sOutString = "";
        }
        else {
            sOutString = sInString.trim();
        }

        return sOutString;

    }

    public static String getUsersName(Connection con, String sIRId)
            throws SQLException, Exception {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String sUserName = "";

        try {

            sQuery
                    .append("SELECT LTRIM(RTRIM(USER_FNAME)) || ' ' || LTRIM(RTRIM(USER_LNAME)) FROM AMT.USERS WHERE IR_USERID = ? for READ ONLY");

            SysLog.log(SysLog.DEBUG, "ETSUtils::getUsersName()", "QUERY : "
                    + sQuery.toString());

            stmt = con.prepareStatement(sQuery.toString());
            stmt.setString(1, sIRId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                sUserName = checkNull(rs.getString(1));
            }
            else {
                sUserName = sIRId;
            }

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }
        return sUserName;

    }

    /**
     * Method to get the users email address.
     */
    private static String getUsersEmail(Connection con, String sIRId)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String sEmail = "";

        try {

            logDebugMessage("::getUsersEmail() [Begin]");

            sQuery.append("SELECT USER_EMAIL FROM AMT.USERS WHERE IR_USERID='"
                    + sIRId
                    + "' for READ ONLY");

            logDebugMessage("::getUsersEmail() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                String sTemp = rs.getString(1);
                if (sTemp == null || sTemp.trim().equals("")) {
                    sTemp = "";
                }
                sEmail = sTemp;
            }

            logDebugMessage("::getUsersEmail() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }

        return sEmail;
    }

    /**
     * Method to get the project name
     */
    private static String getProjectName(Connection con, String projid)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String projname = "";

        try {

            logDebugMessage("::getProjectName() [Begin]");

            sQuery
                    .append("SELECT project_name from ets.ets_projects WHERE project_id='"
                            + projid
                            + "' for READ ONLY");

            logDebugMessage("::getProjectName() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                String sTemp = rs.getString(1);
                if (sTemp == null || sTemp.trim().equals("")) {
                    sTemp = "";
                }
                projname = sTemp;
            }

            logDebugMessage("::getProjectName() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }

        return projname;
    }

    /**
     * Method to get the project company
     */
    private static String getProjectCompany(Connection con, String projid)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String projc = "";

        try {

            logDebugMessage("::getProjectCompany() [Begin]");

            sQuery
                    .append("SELECT company from ets.ets_projects WHERE project_id='"
                            + projid
                            + "' for READ ONLY");

            logDebugMessage("::getProjectCompany() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                String sTemp = rs.getString(1);
                if (sTemp == null || sTemp.trim().equals("")) {
                    sTemp = "";
                }
                projc = sTemp;
            }

            logDebugMessage("::getProjectName() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }

        return projc;
    }

    private static int getTopCat(Connection con, String projid, int viewType)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        int catid = 0;

        try {

            logDebugMessage("::getTopCat() [Begin]");

            String query = "select cat_id from ets.ets_cat"
                    + " where view_type = "
                    + viewType
                    + " and project_id = '"
                    + projid
                    + "'"
                    + " and parent_id=0 for read only";

            sQuery.append(query);

            logDebugMessage("::getTopCat() [Query : " + sQuery.toString() + "]");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                catid = rs.getInt(1);

            }

            logDebugMessage("::getTopCat() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }

        return catid;
    }

    /**
     * Send mail...
     */
    private static boolean sendEMail(String from, String to, String host,
            String sMessage, String Subject, String reply) throws Exception {

        boolean debug = false;
        long sleepTime = 1000 * EMAIL_SLEEP_TIME; // sleep for ...sleepTime

        boolean mailSent = false;

        InternetAddress[] tolist = InternetAddress.parse(to, false);

        // create some properties and get the default Session
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        javax.mail.Session session = javax.mail.Session
                .getInstance(props, null);
        session.setDebug(debug);

        try {

            logDebugMessage("::sendEmail() [Begin]");

            // create a message
            javax.mail.Message msg = new javax.mail.internet.MimeMessage(
                    session);
            msg.addHeader("X-Priority", "1"); //spn change from 1
            msg.addHeader("Importance", "Normal"); //spn change from High
            msg.setFrom(new javax.mail.internet.InternetAddress(from));

            javax.mail.internet.InternetAddress[] address = { new javax.mail.internet.InternetAddress(
                    to) };
            msg.setRecipients(javax.mail.Message.RecipientType.TO, tolist);

            if (reply != null && !reply.trim().equals("")) {
                javax.mail.internet.InternetAddress[] replyto = { new javax.mail.internet.InternetAddress(
                        reply) };
                msg.setReplyTo(replyto);
            }
            msg.setSubject(Subject);

            InetAddress addr = InetAddress.getLocalHost();
            String hostName = addr.getHostName();
            msg.setText(sMessage);

            for (int i = 0; i <= EMAIL_NO_TIMES; i++) {
                try {
                    javax.mail.Transport.send(msg);
                    mailSent = true;
                    break;
                }
                catch (Exception ex) {
                    String str = "Thrown while trying to send e-mail (attempt# "
                            + (i + 1)
                            + ") as follows:\n"
                            + sMessage
                            + "\n\n"
                            + "StackTrace:\n"
                            + ex.getMessage()
                            + "\n\n"
                            + "Will Re-try "
                            + (EMAIL_NO_TIMES - i)
                            + " times\n\n"
                            + "This error was thrown at: "
                            + new java.util.Date();
                    m_pdLog.error(str);
                }

                try {
                    Thread.sleep(sleepTime);
                }
                catch (Exception e) {
                    String str = "Thrown while WAITING to re-send e-mail\n"
                            + "StackTrace:\n"
                            + e.getMessage()
                            + "\n\n"
                            + "This error was thrown at: "
                            + new java.util.Date();
                    m_pdLog.error(str);
                }
            }

            if (!mailSent) {
                String str = "***ERROR***: The following e-mail could NOT be sent despite "
                        + (EMAIL_NO_TIMES + 1)
                        + " attempts:\n"
                        + sMessage;
                m_pdLog.error(str);
                Global.println(str);
            }

            logDebugMessage("::sendEmail() [End]");

        }
        catch (Exception ex) {
            throw ex;
        }

        return mailSent;
    }

    /**
     * Send mail...
     */
    private static boolean sendEMail(String from, String to, String cc,
            String host, String sMessage, String Subject, String reply)
            throws Exception {

        boolean debug = false;
        long sleepTime = 1000 * EMAIL_SLEEP_TIME; // sleep for ...sleepTime

        boolean mailSent = false;

        InternetAddress[] tolist = InternetAddress.parse(to, false);
        InternetAddress[] cclist = InternetAddress.parse(cc, false);

        // create some properties and get the default Session
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        javax.mail.Session session = javax.mail.Session
                .getInstance(props, null);
        session.setDebug(debug);

        try {

            logDebugMessage("::sendEmail() [Begin]");

            // create a message
            javax.mail.Message msg = new javax.mail.internet.MimeMessage(
                    session);
            msg.addHeader("X-Priority", "1"); //spn change from 1
            msg.addHeader("Importance", "Normal"); //spn change from High
            msg.setFrom(new javax.mail.internet.InternetAddress(from));

            //javax.mail.internet.InternetAddress[] address = {new
            // javax.mail.internet.InternetAddress(to)};
            msg.setRecipients(javax.mail.Message.RecipientType.TO, tolist);

            //javax.mail.internet.InternetAddress[] cclist = {new
            // javax.mail.internet.InternetAddress(cc)};
            msg.setRecipients(javax.mail.Message.RecipientType.CC, cclist);

            if (reply != null && !reply.trim().equals("")) {
                javax.mail.internet.InternetAddress[] replyto = { new javax.mail.internet.InternetAddress(
                        reply) };
                msg.setReplyTo(replyto);
            }
            msg.setSubject(Subject);

            InetAddress addr = InetAddress.getLocalHost();
            String hostName = addr.getHostName();
            msg.setText(sMessage);

            for (int i = 0; i <= EMAIL_NO_TIMES; i++) {
                try {
                    javax.mail.Transport.send(msg);
                    mailSent = true;
                    break;
                }
                catch (Exception ex) {
                    String str = "Thrown while trying to send e-mail (attempt# "
                            + (i + 1)
                            + ") as follows:\n"
                            + sMessage
                            + "\n\n"
                            + "StackTrace:\n"
                            + ex.getMessage()
                            + "\n\n"
                            + "Will Re-try "
                            + (EMAIL_NO_TIMES - i)
                            + " times\n\n"
                            + "This error was thrown at: "
                            + new java.util.Date();
                    m_pdLog.error(str);
                }

                try {
                    Thread.sleep(sleepTime);
                }
                catch (Exception e) {
                    String str = "Thrown while WAITING to re-send e-mail\n"
                            + "StackTrace:\n"
                            + e.getMessage()
                            + "\n\n"
                            + "This error was thrown at: "
                            + new java.util.Date();
                    m_pdLog.error(str);
                }
            }

            if (!mailSent) {
                String str = "***ERROR***: The following e-mail could NOT be sent despite "
                        + (EMAIL_NO_TIMES + 1)
                        + " attempts:\n"
                        + sMessage;
                m_pdLog.error(str);
                Global.println(str);
            }

            logDebugMessage("::sendEmail() [End]");

        }
        catch (Exception ex) {
            throw ex;
        }

        return mailSent;
    }

    /**
     * Gets stack trace as string
     */
    private static String getStackTrace(Exception e) throws IOException {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        sw.close();
        return stackTrace;
    }

    /**
     * Handles exception...
     */
    private static void handleError(Exception e) {

        String sMessage = "";

        try {

            sMessage = getStackTrace(e);

            System.err.print("[ERROR] " + sMessage);

            String logMessage = MyDateFormat.format(new java.util.Date()) + " ";
            String sTempMessage = "";
            sTempMessage = "*************************************************************\n";
            sTempMessage = sTempMessage
                    + "An error occured at : "
                    + logMessage
                    + "\n";
            sTempMessage = sTempMessage
                    + "*************************************************************\n\n\n";
            sTempMessage = sTempMessage + "Error Message : \n\n";
            sMessage = sTempMessage + sMessage + "\n\n";
            sMessage = sMessage
                    + "\n\n*************************************************************\n";
            sMessage = sMessage + "\nFrom E&TS Deal Tracker Deamon.\n";
            sMessage = sMessage
                    + "\n\n*************************************************************\n";

            sendEMail(
                    Global.mailFm,
                    Global.mailto[0],
                    Global.mailHost,
                    sMessage,
                    "Error Occured in E&TS Deal Tracker deamon !",
                    "");

        }
        catch (Exception e1) {
            m_pdLog.error(sMessage);
        }

    }

    /**
     * Writes the debug message if debug flag is set to on...
     */
    private static void logDebugMessage(String sMessage) throws IOException,
            Exception {
        /**
         * if (Global.comments) { Global.println("[DEBUG] " + sMessage); }
         */
        m_pdLog.debug("[DEBUG] " + sMessage);
    }

    public static String encodeUrl(String path, String dlim) {
        StringTokenizer line = new StringTokenizer(path, dlim, true);
        StringBuffer buf = new StringBuffer();
        String token;
        while (line.hasMoreTokens()) {
            token = line.nextToken();
            if (token.equals(dlim))
                buf.append(token);
            else
                buf.append(URLEncoder.encode(token));
        }
        return buf.toString();
    }

    /**
     * In Integration and test env, there will be a record with FS0002 and
     * FS0003 which will have an URL to be appended to the email that is sent
     * for the alerts.
     */
    private static String getStaticURL(Connection con, String sID)
            throws SQLException, Exception {

        String sDesc = "";
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();

        try {

            sb.append("SELECT LINK_URL FROM AMT.LEFTNAV WHERE LINK_ID='"
                    + sID
                    + "' for READ ONLY");

            SysLog.log(SysLog.DEBUG, ":getStaticURL()", "Query : "
                    + sb.toString());

            stmt = con.createStatement();
            rs = stmt.executeQuery(sb.toString());

            while (rs.next()) {
                sDesc = rs.getString("LINK_URL");
                if (sDesc == null || sDesc.trim().equals("")) {
                    sDesc = "";
                }
                else {
                    sDesc = sDesc.trim();
                }
            }

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sb = null;
        }

        return sDesc;

    }

    private static String stripHtmlComments(String str) {
        if (str == null)
            return null;

        StringBuffer buffer = new StringBuffer();
        int pos = 0;

        while (pos < str.length()) {
            int openTag = str.indexOf("<!--", pos);
            int closeTag = str.indexOf("-->", pos);

            if (openTag >= 0 && closeTag > openTag + 3) {
                buffer.append(str.substring(pos, openTag));
                pos = closeTag + 3;
            }
            else {
                break;
            }
        }

        if (pos < str.length()) {
            buffer.append(str.substring(pos));
        }

        return buffer.toString();
    }

    /**
     * Method that sends alerts to the users...
     */
    private static void sendClientCareAlerts(Connection con) throws Exception {

        try {

            sendClientNotifications(con);

        }
        catch (SQLException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }

    }

    /**
     * Method that gets the users
     */
    private static void sendClientNotifications(Connection conn)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");

        Vector vAlerts = new Vector();

        try {

            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

            logDebugMessage("ETSDealTracker_Job::sendClientNotifications() [Begin]");

            java.util.Date today = new java.util.Date();
            java.sql.Date sqlToday = new java.sql.Date(today.getTime());

            sQuery
                    .append("SELECT A.QBR_ID,A.PROJECT_ID,A.STEP,A.DUE_DATE,A.IS_NOTIFIED FROM ETS.ETS_QBR_NOTIFY A, ETS.ETS_PROJECTS B ");
            sQuery
                    .append("WHERE DATE(A.DUE_DATE) <= DATE('"
                            + sqlToday
                            + "') AND (A.IS_NOTIFIED IS NULL OR A.IS_NOTIFIED='N') AND A.PROJECT_ID = B.PROJECT_ID AND B.PROJECT_STATUS NOT IN ('A','D') ");
            sQuery.append("for READ ONLY");

            logDebugMessage("ETSDealTracker_Job::sendClientNotifications() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            while (rs.next()) {

                logDebugMessage("Has Client voice notifications to process.");

                String sSetMetId = rs.getString("QBR_ID");
                String sProjectId = rs.getString("PROJECT_ID");
                String sStep = rs.getString("STEP");
                Timestamp tDueDate = rs.getTimestamp("DUE_DATE");

                String sDueDate = df.format(tDueDate);

                String Notify[] = new String[] { sSetMetId, sProjectId, sStep,
                        sDueDate };

                vAlerts.addElement(Notify);
            }

            if (vAlerts != null && vAlerts.size() > 0) {

                logDebugMessage("To Process Records Count: "
                        + String.valueOf(vAlerts.size()));

                for (int i = 0; i < vAlerts.size(); i++) {

                    String sToNotify[] = (String[]) vAlerts.elementAt(i);
                    /**
                     * Fields are as follows 1. Set Met Id 2. Project Id 3. Step
                     * 4. Due Date
                     */

                    String sSetMetID = sToNotify[0];
                    String sProjectID = sToNotify[1];
                    String sStep = sToNotify[2];
                    String sDueDate = sToNotify[3];

                    logDebugMessage("Processing: SetMetID: "
                            + sSetMetID
                            + ",ProjectID: "
                            + sProjectID
                            + ", Step: "
                            + sStep
                            + ", Due Date: "
                            + sDueDate);

                    /**
                     * 1. Get the current state of the setmet. 2. Get the step
                     * and find out the owner of the step. 3. Send the email to
                     * the owner and the primary contact (client care advocate)
                     * as cc 4. Update the table to make it as sent. 5. Update
                     * the email log table.
                     */

                    String sPrimaryContact = getPrimaryContact(conn, sProjectID);

                    String sSubject = "E&TS Connect - Set/Met Notification";
                    StringBuffer sMessage = new StringBuffer("");

                    String sCCList = getUsersEmail(conn, sPrimaryContact);

                    String sCurrentStep = getSetMetCurrentState(
                            conn,
                            sProjectID,
                            sSetMetID);

                    if (sStep.equalsIgnoreCase("PRINCIPAL_APPROVED")) {
                        if (sCurrentStep.equalsIgnoreCase("")
                                || sCurrentStep
                                        .equalsIgnoreCase("CLIENT_INTERVIEW")
                                || sCurrentStep
                                        .equalsIgnoreCase("CLIENT_APPROVED")) {

                            logDebugMessage("Processing: Principal Approved Step");
                            // this notification can go out..

                            // now get the step owner which is the principal of
                            // this setmet.

                            String sPrincipal = getSetMetPrincipal(
                                    conn,
                                    sProjectID,
                                    sSetMetID);

                            /**
                             * 1. Check if the project is active, if not, then
                             * do not send the email 2. Check if the user is
                             * active, if not, then do not send the email. 3.
                             * Check if the user has the entitlement, if not,
                             * then do not send the email.
                             */

                            if (checkEnt(sPrincipal, conn)
                                    && checkProjStatus(sProjectID, conn)
                                    && checkUserFlag(
                                            sPrincipal,
                                            sProjectID,
                                            conn)) {

                                String sToList = getUsersEmail(conn, sPrincipal);

                                sMessage
                                        .append("An action for the following Set/Met on E&TS Connect is due. \n");
                                sMessage
                                        .append("The details are as follows: \n\n");

                                sMessage
                                        .append("===============================================================\n");

                                sMessage.append("  Set/Met:        "
                                        + getSetMetName(
                                                conn,
                                                sProjectID,
                                                sSetMetID)
                                        + "\n");
                                sMessage
                                        .append("  Step:           Principal review and comments \n");
                                sMessage.append("  Due Date:       "
                                        + sDueDate
                                        + " (mm/dd/yyyy) \n");

                                sMessage
                                        .append("===============================================================\n\n");

                                int tc = getTopCat(conn, sProjectID, 6);

                                String url = Global
                                        .getUrl("ets/ETSProjectsServlet.wss")
                                        + "?proj="
                                        + sProjectID
                                        + "&tc="
                                        + tc
                                        + "&set="
                                        + sSetMetID
                                        + "&linkid=251000";

                                sMessage
                                        .append("To view this Set/Met please click on the following URL: \n");
                                sMessage.append("  " + url + "\n\n");

                                sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                                logDebugMessage("Sending Mail to: " + sToList);

                                // send email..
                                sendEMail(
                                        sCCList,
                                        sToList,
                                        sCCList,
                                        Global.mailHost,
                                        sMessage.toString(),
                                        sSubject,
                                        sCCList);

                                // update is notified for the step.
                                updateNotifyFlag(
                                        conn,
                                        sProjectID,
                                        sSetMetID,
                                        "PRINCIPAL_APPROVED",
                                        "Y");

                                // insert into email log.
                                insertEmailLog(
                                        conn,
                                        "SETMET-NOTIFY",
                                        sProjectID,
                                        sSetMetID,
                                        "PRINCIPAL_APPROVED",
                                        sProjectID,
                                        sSubject,
                                        sToList,
                                        sCCList);

                            }
                            else {
                                // update is notified for the step.
                                updateNotifyFlag(
                                        conn,
                                        sProjectID,
                                        sSetMetID,
                                        "PRINCIPAL_APPROVED",
                                        "Y");
                            }

                        }
                        else {
                            // the step has been crossed.
                            // update the is notified field.
                            updateNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSetMetID,
                                    "PRINCIPAL_APPROVED",
                                    "Y");
                        }
                    }
                    else if (sStep.equalsIgnoreCase("ACTION_PLAN")) {
                        if (sCurrentStep.equalsIgnoreCase("")
                                || sCurrentStep
                                        .equalsIgnoreCase("CLIENT_INTERVIEW")
                                || sCurrentStep
                                        .equalsIgnoreCase("CLIENT_APPROVED")
                                || sCurrentStep
                                        .equalsIgnoreCase("PRINCIPAL_APPROVED")) {
                            // this notification can go out..

                            logDebugMessage("Processing: Action Plan Creation Step");

                            // now get the step owner which is the program
                            // manager of this setmet.
                            String sPM = getSetMetProgramManager(
                                    conn,
                                    sProjectID,
                                    sSetMetID);

                            /**
                             * 1. Check if the project is active, if not, then
                             * do not send the email 2. Check if the user is
                             * active, if not, then do not send the email. 3.
                             * Check if the user has the entitlement, if not,
                             * then do not send the email.
                             */

                            if (checkEnt(sPM, conn)
                                    && checkProjStatus(sProjectID, conn)
                                    && checkUserFlag(sPM, sProjectID, conn)) {

                                String sToList = getUsersEmail(conn, sPM);

                                sMessage
                                        .append("An action for the following Set/Met on E&TS Connect is due. \n");
                                sMessage
                                        .append("The details are as follows: \n\n");

                                sMessage
                                        .append("===============================================================\n");

                                sMessage.append("  Set/Met:        "
                                        + getSetMetName(
                                                conn,
                                                sProjectID,
                                                sSetMetID)
                                        + "\n");
                                sMessage
                                        .append("  Step:           Action plan creation \n");
                                sMessage.append("  Due Date:       "
                                        + sDueDate
                                        + " (mm/dd/yyyy) \n");

                                sMessage
                                        .append("===============================================================\n\n");

                                int tc = getTopCat(conn, sProjectID, 6);

                                String url = Global
                                        .getUrl("ets/ETSProjectsServlet.wss")
                                        + "?proj="
                                        + sProjectID
                                        + "&tc="
                                        + tc
                                        + "&set="
                                        + sSetMetID
                                        + "&linkid=251000";

                                sMessage
                                        .append("To view this Set/Met please click on the following URL: \n");
                                sMessage.append("  " + url + "\n\n");

                                sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                                logDebugMessage("Sending Mail to: " + sToList);

                                // send email..
                                sendEMail(
                                        sCCList,
                                        sToList,
                                        sCCList,
                                        Global.mailHost,
                                        sMessage.toString(),
                                        sSubject,
                                        sCCList);

                                // update is notified for the step.
                                updateNotifyFlag(
                                        conn,
                                        sProjectID,
                                        sSetMetID,
                                        "ACTION_PLAN",
                                        "Y");

                                // insert into email log.
                                insertEmailLog(
                                        conn,
                                        "SETMET-NOTIFY",
                                        sProjectID,
                                        sSetMetID,
                                        "ACTION_PLAN",
                                        sProjectID,
                                        sSubject,
                                        sToList,
                                        sCCList);

                            }
                            else {
                                // update is notified for the step.
                                updateNotifyFlag(
                                        conn,
                                        sProjectID,
                                        sSetMetID,
                                        "ACTION_PLAN",
                                        "Y");
                            }

                        }
                        else {
                            // the step has been crossed.
                            // update the is notified field.
                            updateNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSetMetID,
                                    "ACTION_PLAN",
                                    "Y");
                        }
                    }
                    else if (sStep.equalsIgnoreCase("ACTION_PLAN_APPROVED")) {
                        if (sCurrentStep.equalsIgnoreCase("")
                                || sCurrentStep
                                        .equalsIgnoreCase("CLIENT_INTERVIEW")
                                || sCurrentStep
                                        .equalsIgnoreCase("CLIENT_APPROVED")
                                || sCurrentStep
                                        .equalsIgnoreCase("PRINCIPAL_APPROVED")
                                || sCurrentStep.equalsIgnoreCase("ACTION_PLAN")) {
                            // this notification can go out..

                            logDebugMessage("Processing: Principal Implementation Step");

                            // now get the step owner which is the program
                            // manager of this setmet.
                            String sPM = getSetMetProgramManager(
                                    conn,
                                    sProjectID,
                                    sSetMetID);

                            /**
                             * 1. Check if the project is active, if not, then
                             * do not send the email 2. Check if the user is
                             * active, if not, then do not send the email. 3.
                             * Check if the user has the entitlement, if not,
                             * then do not send the email.
                             */

                            if (checkEnt(sPM, conn)
                                    && checkProjStatus(sProjectID, conn)
                                    && checkUserFlag(sPM, sProjectID, conn)) {

                                String sToList = getUsersEmail(conn, sPM);

                                sMessage
                                        .append("An action for the following Set/Met on E&TS Connect is due. \n");
                                sMessage
                                        .append("The details are as follows: \n\n");

                                sMessage
                                        .append("===============================================================\n");

                                sMessage.append("  Set/Met:        "
                                        + getSetMetName(
                                                conn,
                                                sProjectID,
                                                sSetMetID)
                                        + "\n");
                                sMessage
                                        .append("  Step:           Action plan implementation \n");
                                sMessage.append("  Due Date:       "
                                        + sDueDate
                                        + " (mm/dd/yyyy) \n");

                                sMessage
                                        .append("===============================================================\n\n");

                                int tc = getTopCat(conn, sProjectID, 6);

                                String url = Global
                                        .getUrl("ets/ETSProjectsServlet.wss")
                                        + "?proj="
                                        + sProjectID
                                        + "&tc="
                                        + tc
                                        + "&set="
                                        + sSetMetID
                                        + "&linkid=251000";

                                sMessage
                                        .append("To view this Set/Met please click on the following URL: \n");
                                sMessage.append("  " + url + "\n\n");

                                sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                                logDebugMessage("Sending Mail to: " + sToList);

                                // send email..
                                sendEMail(
                                        sCCList,
                                        sToList,
                                        sCCList,
                                        Global.mailHost,
                                        sMessage.toString(),
                                        sSubject,
                                        sCCList);

                                // update is notified for the step.
                                updateNotifyFlag(
                                        conn,
                                        sProjectID,
                                        sSetMetID,
                                        "ACTION_PLAN_APPROVED",
                                        "Y");

                                // insert into email log.
                                insertEmailLog(
                                        conn,
                                        "SETMET-NOTIFY",
                                        sProjectID,
                                        sSetMetID,
                                        "ACTION_PLAN_APPROVED",
                                        sProjectID,
                                        sSubject,
                                        sToList,
                                        sCCList);

                            }
                            else {
                                // update is notified for the step.
                                updateNotifyFlag(
                                        conn,
                                        sProjectID,
                                        sSetMetID,
                                        "ACTION_PLAN_APPROVED",
                                        "Y");
                            }

                        }
                        else {
                            // the step has been crossed.
                            // update the is notified field.
                            updateNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSetMetID,
                                    "ACTION_PLAN_APPROVED",
                                    "Y");
                        }
                    }
                    else if (sStep.equalsIgnoreCase("SETMET_CLOSE")) {
                        if (sCurrentStep.equalsIgnoreCase("")
                                || sCurrentStep
                                        .equalsIgnoreCase("CLIENT_INTERVIEW")
                                || sCurrentStep
                                        .equalsIgnoreCase("CLIENT_APPROVED")
                                || sCurrentStep
                                        .equalsIgnoreCase("PRINCIPAL_APPROVED")
                                || sCurrentStep.equalsIgnoreCase("ACTION_PLAN")
                                || sCurrentStep
                                        .equalsIgnoreCase("ACTION_PLAN_APPROVED")) {
                            // this notification can go out..

                            logDebugMessage("Processing: After Action Revivew Step");

                            // now get the step owner which is the principal of
                            // this setmet.
                            String sInterviewBy = getSetMetInterviewBy(
                                    conn,
                                    sProjectID,
                                    sSetMetID);

                            /**
                             * 1. Check if the project is active, if not, then
                             * do not send the email 2. Check if the user is
                             * active, if not, then do not send the email. 3.
                             * Check if the user has the entitlement, if not,
                             * then do not send the email.
                             */

                            if (checkEnt(sInterviewBy, conn)
                                    && checkProjStatus(sProjectID, conn)
                                    && checkUserFlag(
                                            sInterviewBy,
                                            sProjectID,
                                            conn)) {

                                String sToList = getUsersEmail(
                                        conn,
                                        sInterviewBy);

                                sMessage
                                        .append("An action for the following Set/Met on E&TS Connect is due. \n");
                                sMessage
                                        .append("The details are as follows: \n\n");

                                sMessage
                                        .append("===============================================================\n");

                                sMessage.append("  Set/Met:        "
                                        + getSetMetName(
                                                conn,
                                                sProjectID,
                                                sSetMetID)
                                        + "\n");
                                sMessage
                                        .append("  Step:           After action review \n");
                                sMessage.append("  Due Date:       "
                                        + sDueDate
                                        + " (mm/dd/yyyy) \n");

                                sMessage
                                        .append("===============================================================\n\n");

                                int tc = getTopCat(conn, sProjectID, 6);

                                String url = Global
                                        .getUrl("ets/ETSProjectsServlet.wss")
                                        + "?proj="
                                        + sProjectID
                                        + "&tc="
                                        + tc
                                        + "&set="
                                        + sSetMetID
                                        + "&linkid=251000";

                                sMessage
                                        .append("To view this Set/Met please click on the following URL: \n");
                                sMessage.append("  " + url + "\n\n");

                                sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                                logDebugMessage("Sending Mail to: " + sToList);

                                // send email..
                                sendEMail(
                                        sCCList,
                                        sToList,
                                        sCCList,
                                        Global.mailHost,
                                        sMessage.toString(),
                                        sSubject,
                                        sCCList);

                                // update is notified for the step.
                                updateNotifyFlag(
                                        conn,
                                        sProjectID,
                                        sSetMetID,
                                        "SETMET_CLOSE",
                                        "Y");

                                // insert into email log.
                                insertEmailLog(
                                        conn,
                                        "SETMET-NOTIFY",
                                        sProjectID,
                                        sSetMetID,
                                        "SETMET_CLOSE",
                                        sProjectID,
                                        sSubject,
                                        sToList,
                                        sCCList);

                            }
                            else {
                                // update is notified for the step.
                                updateNotifyFlag(
                                        conn,
                                        sProjectID,
                                        sSetMetID,
                                        "SETMET_CLOSE",
                                        "Y");
                            }

                        }
                        else {
                            // the step has been crossed.
                            // update the is notified field.
                            updateNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSetMetID,
                                    "SETMET_CLOSE",
                                    "Y");
                        }
                    }
                }
            }

            logDebugMessage("etsDTAlerts::getUsersToSend() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }
    }

    /**
     * @return
     */
    private static String getSetMetCurrentState(Connection con,
            String sProjectID, String sSetMetID) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String sStep = "";

        try {

            sQuery
                    .append("SELECT QBR_ID,PROJECT_ID,STEP,ACTION_DATE,ACTION_BY_ID,LAST_TIMESTAMP FROM ETS.ETS_QBR_PLAN WHERE QBR_ID = '"
                            + sSetMetID
                            + "' AND PROJECT_ID = '"
                            + sProjectID
                            + "' ORDER BY ACTION_DATE DESC for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sStep = rs.getString("STEP");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return sStep;

    }

    /**
     * @return
     */
    private static String getSetMetPrincipal(Connection con, String sProjectID,
            String sSetMetID) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String sPrincipal = "";

        try {

            sQuery
                    .append("SELECT ETS_BSE FROM ETS.ETS_QBR_MAIN WHERE QBR_ID = '"
                            + sSetMetID
                            + "' AND PROJECT_ID = '"
                            + sProjectID
                            + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sPrincipal = rs.getString("ETS_BSE");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return sPrincipal;

    }

    /**
     * @return
     */
    private static String getSetMetProgramManager(Connection con,
            String sProjectID, String sSetMetID) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String sProgramManager = "";

        try {

            sQuery
                    .append("SELECT ETS_PRACTICE FROM ETS.ETS_QBR_MAIN WHERE QBR_ID = '"
                            + sSetMetID
                            + "' AND PROJECT_ID = '"
                            + sProjectID
                            + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sProgramManager = rs.getString("ETS_PRACTICE");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return sProgramManager;

    }

    /**
     * @return
     */
    private static String getSetMetInterviewBy(Connection con,
            String sProjectID, String sSetMetID) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String sInterviewBy = "";

        try {

            sQuery
                    .append("SELECT INTERVIEW_BY FROM ETS.ETS_QBR_MAIN WHERE QBR_ID = '"
                            + sSetMetID
                            + "' AND PROJECT_ID = '"
                            + sProjectID
                            + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sInterviewBy = rs.getString("INTERVIEW_BY");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return sInterviewBy;

    }

    private static String getPrimaryContact(Connection con, String sProjectId)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String sPrimaryID = "";

        try {

            sQuery
                    .append("SELECT USER_ID FROM ETS.ETS_USERS WHERE USER_PROJECT_ID = '"
                            + sProjectId
                            + "' AND PRIMARY_CONTACT = 'Y' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sPrimaryID = rs.getString("USER_ID");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return sPrimaryID;
    }

    private static boolean updateNotifyFlag(Connection con, String sProjectId,
            String sSetMetID, String sStep, String isNotified)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        boolean bSuccess = false;

        try {

            sQuery.append("UPDATE ETS.ETS_QBR_NOTIFY SET IS_NOTIFIED = '"
                    + isNotified
                    + "' ");
            sQuery.append("WHERE PROJECT_ID = '" + sProjectId + "' AND ");
            sQuery.append("QBR_ID = '" + sSetMetID + "' AND ");
            sQuery.append("STEP = '" + sStep + "'");

            stmt = con.createStatement();
            int iCount = stmt.executeUpdate(sQuery.toString());

            if (iCount > 0) {
                bSuccess = true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return bSuccess;
    }

    /**
     * Method insertEmailLot.
     * 
     * @param conn
     * @param sMailType
     * @param sKey1
     * @param sKey2
     * @param sKey3
     * @param sProjectId
     * @param sEmailSubject
     * @param sToList
     * @param sCC
     */
    private static int insertEmailLog(Connection conn, String sMailType,
            String sKey1, String sKey2, String sKey3, String sProjectId,
            String sEmailSubject, String sToList, String sCC)
            throws SQLException, Exception {

        PreparedStatement pstmt = null;
        StringBuffer sQuery = new StringBuffer("");
        int iCount = 0;

        try {

            sQuery
                    .append("INSERT INTO ETS.ETS_EMAIL_LOG (TIMESTAMP,MAIL_TYPE,KEY1,KEY2,KEY3,PROJECT_ID,SUBJECT,TO,CC) VALUES (");
            sQuery.append("?,?,?,?,?,?,?,?,?) ");

            pstmt = conn.prepareStatement(sQuery.toString());

            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(2, sMailType);
            pstmt.setString(3, sKey1);
            pstmt.setString(4, sKey2);
            pstmt.setString(5, sKey3);
            pstmt.setString(6, sProjectId);
            pstmt.setString(7, sEmailSubject);
            pstmt.setString(8, sToList);
            pstmt.setString(9, sCC);

            iCount = pstmt.executeUpdate();

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (pstmt != null) {
                pstmt.close();
                pstmt = null;
            }

        }

        return iCount;
    }

    /**
     * Method to get the users email address.
     */
    private static String getSetMetName(Connection con, String projid,
            String sSetMetID) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String setmetname = "";

        try {

            logDebugMessage("::getSetMetName() [Begin]");

            sQuery
                    .append("SELECT QBR_NAME FROM ETS.ETS_QBR_MAIN WHERE QBR_ID = '"
                            + sSetMetID
                            + "' AND PROJECT_ID ='"
                            + projid
                            + "' for READ ONLY");

            logDebugMessage("::getSetMetName() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {

                String sTemp = rs.getString(1);

                if (sTemp == null || sTemp.trim().equals("")) {
                    sTemp = "";
                }

                setmetname = sTemp;
            }

            logDebugMessage("::getSetMetName() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }

        return setmetname;
    }

    /**
     * @param conn
     */
    private static void checkAndAssignOwner(Connection conn)
            throws SQLException, Exception {

        /**
         * 1. Get all Set/Mets and get the interview by fields. 2. Check to see
         * if they are all valid users. 3. If not, then assign the step to
         * workspace owner.
         */

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");

        Vector vSetMets = new Vector();

        try {

            logDebugMessage("ETSDealTracker_Job::checkAndAssignOwner() [Begin]");

            sQuery
                    .append("SELECT A.QBR_ID,A.PROJECT_ID,A.QBR_NAME,A.CLIENT_IR_ID,A.ETS_PRACTICE,A.ETS_BSE,A.INTERVIEW_BY FROM ETS.ETS_QBR_MAIN A, ETS.ETS_PROJECTS B ");
            sQuery
                    .append("WHERE A.STATE = 'OPEN' AND A.PROJECT_ID = B.PROJECT_ID AND B.PROJECT_STATUS NOT IN ('A','D') ");
            sQuery.append("ORDER BY A.PROJECT_ID for READ ONLY");

            logDebugMessage("ETSDealTracker_Job::checkAndAssignOwner() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            while (rs.next()) {

                logDebugMessage("Has some Set/Mets to process.");

                String sSetMetId = rs.getString("QBR_ID");
                String sProjectId = rs.getString("PROJECT_ID");
                String sSetMetName = rs.getString("QBR_NAME");
                String sClientIRID = rs.getString("CLIENT_IR_ID");
                String sPM = rs.getString("ETS_PRACTICE");
                String sPrincipal = rs.getString("ETS_BSE");
                String sInterviewBy = rs.getString("INTERVIEW_BY");

                String SetMet[] = new String[] { sSetMetId, sProjectId,
                        sSetMetName, sClientIRID, sPM, sPrincipal, sInterviewBy };

                vSetMets.addElement(SetMet);
            }

            if (vSetMets != null && vSetMets.size() > 0) {

                logDebugMessage("To Process Records Count: "
                        + String.valueOf(vSetMets.size()));

                for (int i = 0; i < vSetMets.size(); i++) {

                    String sToNotify[] = (String[]) vSetMets.elementAt(i);
                    /**
                     * Fields are as follows 1. Set Met Id 2. Project Id 3. Set
                     * Met Name 4. Client IR ID 5. Program Manager ID 6.
                     * Principal ID 7. Interview By ID
                     */

                    String sSetMetID = sToNotify[0];
                    String sProjectID = sToNotify[1];
                    String sSetMetName = sToNotify[2];
                    String sClientIRID = sToNotify[3];
                    String sPM = sToNotify[4];
                    String sPrincipal = sToNotify[5];
                    String sInterviewBy = sToNotify[6];

                    logDebugMessage("Processing: SetMetID: "
                            + sSetMetID
                            + ",ProjectID: "
                            + sProjectID);

                    String sWorkspaceOwner = getWorkspaceOwner(conn, sProjectID);
                    String sPrimaryContact = getPrimaryContact(conn, sProjectID);
                    int tc = getTopCat(conn, sProjectID, 6);

                    logDebugMessage("Workspace Owner ID for this Workspace : "
                            + sWorkspaceOwner);
                    logDebugMessage("Primary Contact ID for this Workspace : "
                            + sWorkspaceOwner);

                    boolean bSendEmail = false;

                    if (checkEnt(sInterviewBy, conn)
                            && checkUserFlag(sInterviewBy, sProjectID, conn)) {

                        logDebugMessage("Interview By ID ["
                                + sInterviewBy
                                + "] has entitlement. Checking for roles.");

                        // user has entitlement.
                        // now check to see if he is the workspace owner or
                        // manager or primary contact.
                        // if yes, then everything ok else assign the workspace
                        // owner for the step.

                        // Priv 8 = owner
                        // priv 1 = manager

                        if (sInterviewBy.equalsIgnoreCase(sPrimaryContact)
                                || hasUserPrivilage(
                                        conn,
                                        sProjectID,
                                        sInterviewBy,
                                        8)
                                || hasUserPrivilage(
                                        conn,
                                        sProjectID,
                                        sInterviewBy,
                                        1)) {
                            logDebugMessage("Interview By ID ["
                                    + sInterviewBy
                                    + "] for this Set Met is OK. It is either workspace owner or workspace manager or primary contact.");
                        }
                        else {
                            logDebugMessage("Interview By ID ["
                                    + sInterviewBy
                                    + "] is NOT OK for this Set Met. Assigning workspace owner to interview field for this setmet.");
                            boolean bSuccess = updateSetMetInterviewBy(
                                    conn,
                                    sProjectID,
                                    sSetMetID,
                                    sWorkspaceOwner);
                            bSendEmail = true;
                        }

                    }
                    else {
                        logDebugMessage("Interview By ID ["
                                + sInterviewBy
                                + "] does NOT have entitlement. Assigning workspace owner to interview field for this setmet.");
                        boolean bSuccess = updateSetMetInterviewBy(
                                conn,
                                sProjectID,
                                sSetMetID,
                                sWorkspaceOwner);
                        bSendEmail = true;
                    }

                    if (bSendEmail) {

                        String sSubject = "E&TS Connect - Set/Met: Change in step owner.";
                        StringBuffer sMessage = new StringBuffer("");

                        String sCCList = getUsersEmail(conn, sPrimaryContact);
                        String sToList = getUsersEmail(conn, sWorkspaceOwner);

                        sMessage
                                .append("You have been assigned as the owner for a step in the following Set/Met. \n\n");
                        sMessage
                                .append("This change in owner has occured because of one of the following reasons.\n\n");
                        sMessage
                                .append("1. The previous owner has been revoked of his entitlement.\n");
                        sMessage
                                .append("2. The previous owner has been revoked of his role in this workspace. \n\n");

                        sMessage
                                .append("The details of the change are as follows: \n\n");

                        sMessage
                                .append("===============================================================\n");
                        sMessage.append("  Set/Met:        "
                                + sSetMetName
                                + "\n");
                        sMessage.append("  Previous owner: "
                                + getUsersName(conn, sInterviewBy)
                                + " [ID: "
                                + sInterviewBy
                                + "]\n");
                        sMessage
                                .append("===============================================================\n\n");

                        String url = Global
                                .getUrl("ets/ETSProjectsServlet.wss")
                                + "?proj="
                                + sProjectID
                                + "&tc="
                                + tc
                                + "&set="
                                + sSetMetID
                                + "&linkid=251000";

                        sMessage
                                .append("To view this Set/Met please click on the following URL: \n");
                        sMessage.append("  " + url + "\n\n");

                        sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                        logDebugMessage("Sending Mail to: " + sToList);

                        // send email..
                        sendEMail(
                                "etsadmin@us.ibm.com",
                                sToList,
                                sCCList,
                                Global.mailHost,
                                sMessage.toString(),
                                sSubject,
                                "");
                    }

                    bSendEmail = false;

                    if (checkEnt(sClientIRID, conn)
                            && checkUserFlag(sClientIRID, sProjectID, conn)) {
                        logDebugMessage("Client ID ["
                                + sClientIRID
                                + "] for this Set Met is OK.");
                    }
                    else {
                        logDebugMessage("Client ID ["
                                + sClientIRID
                                + "]does NOT have entitlement. Assigning workspace owner to client ir id field for this setmet.");
                        boolean bSuccess = updateSetMetClient(
                                conn,
                                sProjectID,
                                sSetMetID,
                                sWorkspaceOwner);
                        bSendEmail = true;
                    }

                    if (bSendEmail) {

                        String sSubject = "E&TS Connect - Set/Met: Change in step owner.";
                        StringBuffer sMessage = new StringBuffer("");

                        String sCCList = getUsersEmail(conn, sPrimaryContact);
                        String sToList = getUsersEmail(conn, sWorkspaceOwner);

                        sMessage
                                .append("You have been assigned as the owner for a step in the following Set/Met. \n\n");
                        sMessage
                                .append("This change in owner has occured because of one of the following reasons.\n\n");
                        sMessage
                                .append("1. The previous owner has been revoked of his entitlement.\n");
                        sMessage
                                .append("2. The previous owner has been revoked of his role in this workspace. \n\n");

                        sMessage
                                .append("The details of the change are as follows: \n\n");

                        sMessage
                                .append("===============================================================\n");
                        sMessage.append("  Set/Met:        "
                                + sSetMetName
                                + "\n");
                        sMessage.append("  Previous owner: "
                                + getUsersName(conn, sClientIRID)
                                + " [ID: "
                                + sClientIRID
                                + "]\n");
                        sMessage
                                .append("===============================================================\n\n");

                        String url = Global
                                .getUrl("ets/ETSProjectsServlet.wss")
                                + "?proj="
                                + sProjectID
                                + "&tc="
                                + tc
                                + "&set="
                                + sSetMetID
                                + "&linkid=251000";

                        sMessage
                                .append("To view this Set/Met please click on the following URL: \n");
                        sMessage.append("  " + url + "\n\n");

                        sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                        logDebugMessage("Sending Mail to: " + sToList);

                        // send email..
                        sendEMail(
                                "etsadmin@us.ibm.com",
                                sToList,
                                sCCList,
                                Global.mailHost,
                                sMessage.toString(),
                                sSubject,
                                "");
                    }

                    bSendEmail = false;

                    if (checkEnt(sPM, conn)
                            && checkUserFlag(sPM, sProjectID, conn)) {
                        logDebugMessage("Program manager ID ["
                                + sPM
                                + "] for this Set Met is OK.");
                    }
                    else {
                        logDebugMessage("Program manager ID ["
                                + sPM
                                + "] does NOT have entitlement. Assigning workspace owner to program manger field id field for this setmet.");
                        boolean bSuccess = updateSetMetProgramManager(
                                conn,
                                sProjectID,
                                sSetMetID,
                                sWorkspaceOwner);
                        bSendEmail = true;
                    }

                    if (bSendEmail) {

                        String sSubject = "E&TS Connect - Set/Met: Change in step owner.";
                        StringBuffer sMessage = new StringBuffer("");

                        String sCCList = getUsersEmail(conn, sPrimaryContact);
                        String sToList = getUsersEmail(conn, sWorkspaceOwner);

                        sMessage
                                .append("You have been assigned as the owner for a step in the following Set/Met. \n\n");
                        sMessage
                                .append("This change in owner has occured because of one of the following reasons.\n\n");
                        sMessage
                                .append("1. The previous owner has been revoked of his entitlement.\n");
                        sMessage
                                .append("2. The previous owner has been revoked of his role in this workspace. \n\n");

                        sMessage
                                .append("The details of the change are as follows: \n\n");

                        sMessage
                                .append("===============================================================\n");
                        sMessage.append("  Set/Met:        "
                                + sSetMetName
                                + "\n");
                        sMessage.append("  Previous owner: "
                                + getUsersName(conn, sPM)
                                + " [ID: "
                                + sPM
                                + "]\n");
                        sMessage
                                .append("===============================================================\n\n");

                        String url = Global
                                .getUrl("ets/ETSProjectsServlet.wss")
                                + "?proj="
                                + sProjectID
                                + "&tc="
                                + tc
                                + "&set="
                                + sSetMetID
                                + "&linkid=251000";

                        sMessage
                                .append("To view this Set/Met please click on the following URL: \n");
                        sMessage.append("  " + url + "\n\n");

                        sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                        logDebugMessage("Sending Mail to: " + sToList);

                        // send email..
                        sendEMail(
                                "etsadmin@us.ibm.com",
                                sToList,
                                sCCList,
                                Global.mailHost,
                                sMessage.toString(),
                                sSubject,
                                "");
                    }

                    bSendEmail = false;

                    if (checkEnt(sPrincipal, conn)
                            && checkUserFlag(sPrincipal, sProjectID, conn)) {
                        logDebugMessage("Principal ID ["
                                + sPrincipal
                                + "] for this Set Met is OK.");
                    }
                    else {
                        logDebugMessage("Principal ID ["
                                + sPrincipal
                                + "] does NOT have entitlement. Assigning workspace owner to program manger field id field for this setmet.");
                        boolean bSuccess = updateSetMetPrincipal(
                                conn,
                                sProjectID,
                                sSetMetID,
                                sWorkspaceOwner);
                        bSendEmail = true;
                    }

                    if (bSendEmail) {

                        String sSubject = "E&TS Connect - Set/Met: Change in step owner.";
                        StringBuffer sMessage = new StringBuffer("");

                        String sCCList = getUsersEmail(conn, sPrimaryContact);
                        String sToList = getUsersEmail(conn, sWorkspaceOwner);

                        sMessage
                                .append("You have been assigned as the owner for a step in the following Set/Met. \n\n");
                        sMessage
                                .append("This change in owner has occured because of one of the following reasons.\n\n");
                        sMessage
                                .append("1. The previous owner has been revoked of his entitlement.\n");
                        sMessage
                                .append("2. The previous owner has been revoked of his role in this workspace. \n\n");

                        sMessage
                                .append("The details of the change are as follows: \n\n");

                        sMessage
                                .append("===============================================================\n");
                        sMessage.append("  Set/Met:        "
                                + sSetMetName
                                + "\n");
                        sMessage.append("  Previous owner: "
                                + getUsersName(conn, sPrincipal)
                                + " [ID: "
                                + sPrincipal
                                + "]\n");
                        sMessage
                                .append("===============================================================\n\n");

                        String url = Global
                                .getUrl("ets/ETSProjectsServlet.wss")
                                + "?proj="
                                + sProjectID
                                + "&tc="
                                + tc
                                + "&set="
                                + sSetMetID
                                + "&linkid=251000";

                        sMessage
                                .append("To view this Set/Met please click on the following URL: \n");
                        sMessage.append("  " + url + "\n\n");

                        sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                        logDebugMessage("Sending Mail to: " + sToList);

                        // send email..
                        sendEMail(
                                "etsadmin@us.ibm.com",
                                sToList,
                                sCCList,
                                Global.mailHost,
                                sMessage.toString(),
                                sSubject,
                                "");
                    }

                }
            }

            logDebugMessage("etsDTAlerts::checkAndAssignOwner() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }

    }

    private static String getWorkspaceOwner(Connection con, String sProjectId)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String sWorkspaceOwner = "";

        try {

            sQuery
                    .append("select u.* from ets.ets_roles r, ets.ets_users u where r.priv_id = 8 and r.priv_value = 1 and u.user_project_id = '"
                            + sProjectId
                            + "' and u.user_role_id = r.role_id and u.user_project_id = r.project_id and u.active_flag='A' for read only");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sWorkspaceOwner = rs.getString("USER_ID");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return sWorkspaceOwner;
    }

    private static boolean hasUserPrivilage(Connection con, String sProjectId,
            String sUserID, int iPriv) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        boolean bHasPriv = false;

        try {

            sQuery
                    .append("select u.* from ets.ets_roles r, ets.ets_users u where r.priv_id = "
                            + iPriv
                            + " and r.priv_value = 1 and u.user_project_id = '"
                            + sProjectId
                            + "' and u.user_role_id = r.role_id and u.user_project_id = r.project_id and u.active_flag='A' and u.user_id = '"
                            + sUserID
                            + "' for read only");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                bHasPriv = true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return bHasPriv;
    }

    private static boolean updateSetMetInterviewBy(Connection con,
            String sProjectId, String sSetMetID, String sWorkspaceOwner)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        boolean bSuccess = false;

        try {

            sQuery.append("UPDATE ETS.ETS_QBR_MAIN SET INTERVIEW_BY = '"
                    + sWorkspaceOwner
                    + "' ");
            sQuery.append("WHERE PROJECT_ID = '" + sProjectId + "' AND ");
            sQuery.append("QBR_ID = '" + sSetMetID + "'");

            stmt = con.createStatement();
            int iCount = stmt.executeUpdate(sQuery.toString());

            if (iCount > 0) {
                bSuccess = true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return bSuccess;
    }

    private static boolean updateSetMetProgramManager(Connection con,
            String sProjectId, String sSetMetID, String sWorkspaceOwner)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        boolean bSuccess = false;

        try {

            sQuery.append("UPDATE ETS.ETS_QBR_MAIN SET ETS_PRACTICE = '"
                    + sWorkspaceOwner
                    + "' ");
            sQuery.append("WHERE PROJECT_ID = '" + sProjectId + "' AND ");
            sQuery.append("QBR_ID = '" + sSetMetID + "'");

            stmt = con.createStatement();
            int iCount = stmt.executeUpdate(sQuery.toString());

            if (iCount > 0) {
                bSuccess = true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return bSuccess;
    }

    private static boolean updateSetMetPrincipal(Connection con,
            String sProjectId, String sSetMetID, String sWorkspaceOwner)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        boolean bSuccess = false;

        try {

            sQuery.append("UPDATE ETS.ETS_QBR_MAIN SET ETS_BSE = '"
                    + sWorkspaceOwner
                    + "' ");
            sQuery.append("WHERE PROJECT_ID = '" + sProjectId + "' AND ");
            sQuery.append("QBR_ID = '" + sSetMetID + "'");

            stmt = con.createStatement();
            int iCount = stmt.executeUpdate(sQuery.toString());

            if (iCount > 0) {
                bSuccess = true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return bSuccess;
    }

    private static boolean updateSetMetClient(Connection con,
            String sProjectId, String sSetMetID, String sWorkspaceOwner)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        boolean bSuccess = false;

        try {

            sQuery.append("UPDATE ETS.ETS_QBR_MAIN SET CLIENT_IR_ID = '"
                    + sWorkspaceOwner
                    + "' ");
            sQuery.append("WHERE PROJECT_ID = '" + sProjectId + "' AND ");
            sQuery.append("QBR_ID = '" + sSetMetID + "'");

            stmt = con.createStatement();
            int iCount = stmt.executeUpdate(sQuery.toString());

            if (iCount > 0) {
                bSuccess = true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return bSuccess;
    }

    /**
     * Method that sends alerts to the users...
     */
    private static void sendSelfAssessmentAlerts(Connection con)
            throws Exception {

        try {

            sendSelfAssessmentNotifications(con);

        }
        catch (SQLException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }

    }

    /**
     * Method that gets the users
     */
    private static void sendSelfAssessmentNotifications(Connection conn)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");

        Vector vAlerts = new Vector();

        try {

            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

            logDebugMessage("ETSDealTracker_Job::sendSelfAssessmentNotifications() [Begin]");

            java.util.Date today = new java.util.Date();
            java.sql.Date sqlToday = new java.sql.Date(today.getTime());

            sQuery
                    .append("SELECT A.SELF_ID,A.PROJECT_ID,A.STEP,A.DUE_DATE,A.IS_NOTIFIED FROM ETS.ETS_SELF_NOTIFY A, ETS.ETS_PROJECTS B ");
            sQuery
                    .append("WHERE DATE(A.DUE_DATE) <= DATE('"
                            + sqlToday
                            + "') AND (A.IS_NOTIFIED IS NULL OR A.IS_NOTIFIED='N') AND A.PROJECT_ID = B.PROJECT_ID AND B.PROJECT_STATUS NOT IN ('A','D') ");
            sQuery.append("for READ ONLY");

            logDebugMessage("ETSDealTracker_Job::sendClientNotifications() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            while (rs.next()) {

                logDebugMessage("Has self assessment notifications to process.");

                String sSelfId = rs.getString("SELF_ID");
                String sProjectId = rs.getString("PROJECT_ID");
                String sStep = rs.getString("STEP");
                Timestamp tDueDate = rs.getTimestamp("DUE_DATE");

                String sDueDate = df.format(tDueDate);

                String Notify[] = new String[] { sSelfId, sProjectId, sStep,
                        sDueDate };

                vAlerts.addElement(Notify);
            }

            if (vAlerts != null && vAlerts.size() > 0) {

                logDebugMessage("To Process Records Count: "
                        + String.valueOf(vAlerts.size()));

                for (int i = 0; i < vAlerts.size(); i++) {

                    String sToNotify[] = (String[]) vAlerts.elementAt(i);
                    /**
                     * Fields are as follows 1. Self Id 2. Project Id 3. Step 4.
                     * Due Date
                     */

                    String sSelfId = sToNotify[0];
                    String sProjectID = sToNotify[1];
                    String sStep = sToNotify[2];
                    String sDueDate = sToNotify[3];

                    logDebugMessage("Processing: Self Id: "
                            + sSelfId
                            + ",ProjectID: "
                            + sProjectID
                            + ", Step: "
                            + sStep
                            + ", Due Date: "
                            + sDueDate);

                    /**
                     * 1. Get the current state of the setmet. 2. Get the step
                     * and find out the owner of the step. 3. Send the email to
                     * the owner and the primary contact (client care advocate)
                     * as cc 4. Update the table to make it as sent. 5. Update
                     * the email log table.
                     */

                    String sPrimaryContact = getPrimaryContact(conn, sProjectID);

                    String sSubject = "E&TS Self Assessment Notification : Self Assessment action due.";
                    StringBuffer sMessage = new StringBuffer("");

                    String sCCList = getUsersEmail(conn, sPrimaryContact);

                    String sCurrentStep = getSelfAssessmentCurrentState(
                            conn,
                            sProjectID,
                            sSelfId);

                    if (sStep.equalsIgnoreCase("MEMBER_ASSESSMENT")) {

                        if (sCurrentStep
                                .equalsIgnoreCase("COMPILED_ASSESSMENT")
                                || sCurrentStep.equalsIgnoreCase("ACTION_PLAN")
                                || sCurrentStep.equalsIgnoreCase("CLOSED")) {

                            // update is notified for the step.
                            updateSelfNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSelfId,
                                    "MEMBER_ASSESSMENT",
                                    "Y");
                        }
                        else {

                            logDebugMessage("Processing: Team Member Assessment Step");
                            // this notification can go out..

                            // now get the step owner which is the principal of
                            // this setmet.

                            ArrayList members = getSelfAssessmentMembers(
                                    conn,
                                    sProjectID,
                                    sSelfId);

                            /**
                             * 1. Check if the project is active, if not, then
                             * do not send the email 2. Check if the user is
                             * active, if not, then do not send the email. 3.
                             * Check if the user has the entitlement, if not,
                             * then do not send the email.
                             */

                            for (int memcount = 0; memcount < members.size(); memcount++) {

                                String sMember = (String) members.get(memcount);

                                if (checkEnt(sMember, conn)
                                        && checkProjStatus(sProjectID, conn)
                                        && checkUserFlag(
                                                sMember,
                                                sProjectID,
                                                conn)) {

                                    String sToList = getUsersEmail(
                                            conn,
                                            sMember);

                                    sMessage.append("Self assessment:  "
                                            + getSelfName(
                                                    conn,
                                                    sProjectID,
                                                    sSelfId)
                                            + "\n\n");

                                    sMessage
                                            .append("An action for the following self assessment on IBM E&TS Connect is due. \n");
                                    sMessage
                                            .append("The details of the action required are as follows: \n\n");

                                    sMessage
                                            .append("===============================================================\n");

                                    sMessage.append("  Title:          "
                                            + getSelfName(
                                                    conn,
                                                    sProjectID,
                                                    sSelfId)
                                            + "\n");
                                    sMessage
                                            .append("  Step:           Team member assessment \n");
                                    sMessage.append("  Due Date:       "
                                            + sDueDate
                                            + " (mm/dd/yyyy) \n");

                                    sMessage
                                            .append("===============================================================\n\n");

                                    int tc = getTopCat(conn, sProjectID, 11);

                                    String url = Global
                                            .getUrl("ets/ETSProjectsServlet.wss")
                                            + "?proj="
                                            + sProjectID
                                            + "&tc="
                                            + tc
                                            + "&self="
                                            + sSelfId
                                            + "&linkid=251000";

                                    sMessage
                                            .append("To view this self assessment please click on the following URL: \n");
                                    sMessage.append("  " + url + "\n\n");

                                    sMessage
                                            .append("==============================================================\n");
                                    sMessage
                                            .append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services\n");
                                    sMessage
                                            .append("(E&TS) and IBM team members to collaborate on proposals and project\n");
                                    sMessage
                                            .append("information. E&TS Connect provides a highly secure workspace and a\n");
                                    sMessage
                                            .append("comprehensive suite of on demand tools that is available online 24/7.\n");
                                    sMessage
                                            .append("==============================================================\n\n");
                                    sMessage
                                            .append("This is a system-generated e-mail delivered by E&TS Connect.\n");
                                    sMessage.append("Please do not reply.\n");
                                    sMessage
                                            .append("==============================================================\n\n");

                                    logDebugMessage("Sending Mail to: "
                                            + sToList);

                                    // send email..
                                    sendEMail(
                                            sCCList,
                                            sToList,
                                            sCCList,
                                            Global.mailHost,
                                            sMessage.toString(),
                                            sSubject,
                                            sCCList);

                                    // insert into email log.
                                    insertEmailLog(
                                            conn,
                                            "SELF-NOTIFY",
                                            sProjectID,
                                            sSelfId,
                                            "MEMBER_ASSESSMENT",
                                            sProjectID,
                                            sSubject,
                                            sToList,
                                            sCCList);
                                }
                            }

                            // update is notified for the step.
                            updateSelfNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSelfId,
                                    "MEMBER_ASSESSMENT",
                                    "Y");

                        }

                    }
                    else if (sStep.equalsIgnoreCase("COMPILED_ASSESSMENT")) {

                        if (sCurrentStep.equalsIgnoreCase("ACTION_PLAN")
                                || sCurrentStep.equalsIgnoreCase("CLOSED")) {

                            // update is notified for the step.
                            updateSelfNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSelfId,
                                    "COMPILED_ASSESSMENT",
                                    "Y");

                        }
                        else {

                            logDebugMessage("Processing: Compiled Assessment Step");
                            // this notification can go out..

                            // now get the step owner which is the principal of
                            // this setmet.

                            String sOwner = getSelfOwner(
                                    conn,
                                    sProjectID,
                                    sSelfId);

                            /**
                             * 1. Check if the project is active, if not, then
                             * do not send the email 2. Check if the user is
                             * active, if not, then do not send the email. 3.
                             * Check if the user has the entitlement, if not,
                             * then do not send the email.
                             */

                            if (checkEnt(sOwner, conn)
                                    && checkProjStatus(sProjectID, conn)
                                    && checkUserFlag(sOwner, sProjectID, conn)) {

                                String sToList = getUsersEmail(conn, sOwner);

                                sMessage
                                        .append("An action for the following self assessment on E&TS Connect is due. \n");
                                sMessage
                                        .append("The details are as follows: \n\n");

                                sMessage
                                        .append("===============================================================\n");

                                sMessage
                                        .append("  Title:          "
                                                + getSelfName(
                                                        conn,
                                                        sProjectID,
                                                        sSelfId)
                                                + "\n");
                                sMessage
                                        .append("  Step:           Compiled assessment \n");
                                sMessage.append("  Due Date:       "
                                        + sDueDate
                                        + " (mm/dd/yyyy) \n");

                                sMessage
                                        .append("===============================================================\n\n");

                                int tc = getTopCat(conn, sProjectID, 11);

                                String url = Global
                                        .getUrl("ets/ETSProjectsServlet.wss")
                                        + "?proj="
                                        + sProjectID
                                        + "&tc="
                                        + tc
                                        + "&self="
                                        + sSelfId
                                        + "&linkid=251000";

                                sMessage
                                        .append("To view this self assessment please click on the following URL: \n");
                                sMessage.append("  " + url + "\n\n");

                                sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                                logDebugMessage("Sending Mail to: " + sToList);

                                // send email..
                                sendEMail(
                                        sCCList,
                                        sToList,
                                        sCCList,
                                        Global.mailHost,
                                        sMessage.toString(),
                                        sSubject,
                                        sCCList);

                                // insert into email log.
                                insertEmailLog(
                                        conn,
                                        "SELF-NOTIFY",
                                        sProjectID,
                                        sSelfId,
                                        "COMPILED_ASSESSMENT",
                                        sProjectID,
                                        sSubject,
                                        sToList,
                                        sCCList);
                            }

                            // update is notified for the step.
                            updateSelfNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSelfId,
                                    "COMPILED_ASSESSMENT",
                                    "Y");

                        }
                    }
                    else if (sStep.equalsIgnoreCase("ACTION_PLAN")) {

                        if (sCurrentStep.equalsIgnoreCase("CLOSED")) {

                            // update is notified for the step.
                            updateSelfNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSelfId,
                                    "ACTION_PLAN",
                                    "Y");

                        }
                        else {

                            logDebugMessage("Processing: Action Plan Step");
                            // this notification can go out..

                            // now get the step owner which is the principal of
                            // this setmet.

                            String sOwner = getPlanOwner(
                                    conn,
                                    sProjectID,
                                    sSelfId);

                            /**
                             * 1. Check if the project is active, if not, then
                             * do not send the email 2. Check if the user is
                             * active, if not, then do not send the email. 3.
                             * Check if the user has the entitlement, if not,
                             * then do not send the email.
                             */

                            if (checkEnt(sOwner, conn)
                                    && checkProjStatus(sProjectID, conn)
                                    && checkUserFlag(sOwner, sProjectID, conn)) {

                                String sToList = getUsersEmail(conn, sOwner);

                                sMessage
                                        .append("An action for the following self assessment on E&TS Connect is due. \n");
                                sMessage
                                        .append("The details are as follows: \n\n");

                                sMessage
                                        .append("===============================================================\n");

                                sMessage
                                        .append("  Title:          "
                                                + getSelfName(
                                                        conn,
                                                        sProjectID,
                                                        sSelfId)
                                                + "\n");
                                sMessage
                                        .append("  Step:           Action plan \n");
                                sMessage.append("  Due Date:       "
                                        + sDueDate
                                        + " (mm/dd/yyyy) \n");

                                sMessage
                                        .append("===============================================================\n\n");

                                int tc = getTopCat(conn, sProjectID, 11);

                                String url = Global
                                        .getUrl("ets/ETSProjectsServlet.wss")
                                        + "?proj="
                                        + sProjectID
                                        + "&tc="
                                        + tc
                                        + "&self="
                                        + sSelfId
                                        + "&linkid=251000";

                                sMessage
                                        .append("To view this self assessment please click on the following URL: \n");
                                sMessage.append("  " + url + "\n\n");

                                sMessage.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));

                                logDebugMessage("Sending Mail to: " + sToList);

                                // send email..
                                sendEMail(
                                        sCCList,
                                        sToList,
                                        sCCList,
                                        Global.mailHost,
                                        sMessage.toString(),
                                        sSubject,
                                        sCCList);

                                // insert into email log.
                                insertEmailLog(
                                        conn,
                                        "SELF-NOTIFY",
                                        sProjectID,
                                        sSelfId,
                                        "ACTION_PLAN",
                                        sProjectID,
                                        sSubject,
                                        sToList,
                                        sCCList);
                            }

                            // update is notified for the step.
                            updateSelfNotifyFlag(
                                    conn,
                                    sProjectID,
                                    sSelfId,
                                    "ACTION_PLAN",
                                    "Y");

                        }

                    }
                }
            }

            logDebugMessage("ETSDealTracker_Job::sendSelfAssessmentNotifications() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }
    }

    /**
     * @return
     */
    private static String getSelfAssessmentCurrentState(Connection con,
            String sProjectID, String sSelfID) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String sStep = "";

        try {

            sQuery
                    .append("SELECT SELF_ID,PROJECT_ID,STEP FROM ETS.ETS_SELF_PLAN WHERE SELF_ID = '"
                            + sSelfID
                            + "' AND PROJECT_ID = '"
                            + sProjectID
                            + "' ORDER BY ACTION_DATE DESC for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sStep = rs.getString("STEP");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return sStep;

    }

    private static boolean updateSelfNotifyFlag(Connection con,
            String sProjectId, String sSelfId, String sStep, String isNotified)
            throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        boolean bSuccess = false;

        try {

            sQuery.append("UPDATE ETS.ETS_SELF_NOTIFY SET IS_NOTIFIED = '"
                    + isNotified
                    + "' ");
            sQuery.append("WHERE PROJECT_ID = '" + sProjectId + "' AND ");
            sQuery.append("SELF_ID = '" + sSelfId + "' AND ");
            sQuery.append("STEP = '" + sStep + "'");

            stmt = con.createStatement();
            int iCount = stmt.executeUpdate(sQuery.toString());

            if (iCount > 0) {
                bSuccess = true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return bSuccess;
    }

    /**
     * @return
     */
    private static ArrayList getSelfAssessmentMembers(Connection con,
            String sProjectID, String sSelfId) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");

        ArrayList memberlist = new ArrayList();

        try {

            sQuery
                    .append("SELECT MEMBER_IR_ID FROM ETS.ETS_SELF_MEMBERS WHERE SELF_ID = '"
                            + sSelfId
                            + "' AND PROJECT_ID = '"
                            + sProjectID
                            + "' AND COMPLETED NOT IN ('Y') for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            while (rs.next()) {
                memberlist.add(rs.getString("MEMBER_IR_ID"));
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return memberlist;
    }

    /**
     * Method to get the users email address.
     */
    private static String getSelfName(Connection con, String projid,
            String sSelfID) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String selfname = "";

        try {

            logDebugMessage("::getSelfName() [Begin]");

            sQuery
                    .append("SELECT SELF_NAME FROM ETS.ETS_SELF_MAIN WHERE SELF_ID = '"
                            + sSelfID
                            + "' AND PROJECT_ID ='"
                            + projid
                            + "' for READ ONLY");

            logDebugMessage("::getSelfName() [Query : "
                    + sQuery.toString()
                    + "]");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {

                String sTemp = rs.getString(1);

                if (sTemp == null || sTemp.trim().equals("")) {
                    sTemp = "";
                }

                selfname = sTemp;
            }

            logDebugMessage("::getSelfName() [End]");

        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            sQuery = null;
        }

        return selfname;
    }

    /**
     * @return
     */
    private static String getSelfOwner(Connection con, String sProjectID,
            String sSelfID) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String sOwner = "";

        try {

            sQuery
                    .append("SELECT SELF_PM FROM ETS.ETS_SELF_MAIN WHERE SELF_ID = '"
                            + sSelfID
                            + "' AND PROJECT_ID = '"
                            + sProjectID
                            + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sOwner = rs.getString("SELF_PM");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return sOwner;

    }

    /**
     * @return
     */
    private static String getPlanOwner(Connection con, String sProjectID,
            String sSelfID) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        String sOwner = "";

        try {

            sQuery
                    .append("SELECT SELF_PLAN_OWNER FROM ETS.ETS_SELF_MAIN WHERE SELF_ID = '"
                            + sSelfID
                            + "' AND PROJECT_ID = '"
                            + sProjectID
                            + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sOwner = rs.getString("SELF_PLAN_OWNER");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        return sOwner;

    }

}
