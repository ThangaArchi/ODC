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


/**
 * @author 		: v2sathis
 * Created on 		: Feb 19, 2004
 */
package oem.edge.ets.fe.ismgt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.actions.ETSFeedbackBean;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

public class ETSFeedbackDAO implements EtsIssueConstants {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.53";

	public static ETSFeedbackBean getFeedback(Connection con, String sApplicationId, String sEdgeProblemId) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        ETSFeedbackBean feedBack = new ETSFeedbackBean();;
        StringBuffer sQuery = new StringBuffer("");

        try {

            sQuery.append("SELECT ");
            sQuery.append("APPLICATION_ID,EDGE_PROBLEM_ID,CQ_TRK_ID,PROBLEM_STATE,");
            sQuery.append("SEQ_NO,PROBLEM_CREATOR,CREATION_DATE,CUST_NAME,CUST_EMAIL,");
            sQuery.append("CUST_PHONE,CUST_COMPANY,CUST_PROJECT,PROBLEM_CLASS,TITLE,");
            sQuery.append("SEVERITY,PROBLEM_TYPE,COMM_FROM_CUST,LAST_USERID,LAST_TIMESTAMP,");
            sQuery.append("ETS_PROJECT_ID FROM "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 WHERE APPLICATION_ID = '" + sApplicationId + "' AND EDGE_PROBLEM_ID = '" + sEdgeProblemId + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {

                feedBack.setApplicationId(rs.getString("APPLICATION_ID"));
                feedBack.setEdgeProblemId(rs.getString("EDGE_PROBLEM_ID"));
                feedBack.setCQTrackId(rs.getString("CQ_TRK_ID"));
                feedBack.setProblemState(rs.getString("PROBLEM_STATE"));
                feedBack.setSeqNo(rs.getInt("SEQ_NO"));
                feedBack.setProblemCreator(rs.getString("PROBLEM_CREATOR"));
                feedBack.setCustName(rs.getString("CUST_NAME"));
                feedBack.setCustEmail(rs.getString("CUST_EMAIL"));
                feedBack.setCustPhone(rs.getString("CUST_PHONE"));
                feedBack.setCustCompany(rs.getString("CUST_COMPANY"));
                feedBack.setCustProjectName(rs.getString("CUST_PROJECT"));
                feedBack.setProblemClass(rs.getString("PROBLEM_CLASS"));
                feedBack.setTitle(rs.getString("TITLE"));
                feedBack.setSeverity(rs.getString("SEVERITY"));
                feedBack.setProblemType(rs.getString("PROBLEM_TYPE"));
                feedBack.setComments(rs.getString("COMM_FROM_CUST"));
                feedBack.setLastUserId(rs.getString("LAST_USERID"));
                feedBack.setLastTime(rs.getTimestamp("LAST_TIMESTAMP"));
                feedBack.setETSProjectId(rs.getString("ETS_PROJECT_ID"));

            }


        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(rs);
            ETSDBUtils.close(stmt);
        }

        return feedBack;


    }


	public static Vector getAllMyFeedbacks(Connection con,String sAppId, String sProjId, String sIRId) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        Vector vFeedBacks = new Vector();

        try {

            sQuery.append("SELECT ");
            sQuery.append("APPLICATION_ID,EDGE_PROBLEM_ID,CQ_TRK_ID,PROBLEM_STATE,");
            sQuery.append("SEQ_NO,PROBLEM_CREATOR,CREATION_DATE,CUST_NAME,CUST_EMAIL,");
            sQuery.append("CUST_PHONE,CUST_COMPANY,CUST_PROJECT,PROBLEM_CLASS,TITLE,");
            sQuery.append("SEVERITY,PROBLEM_TYPE,COMM_FROM_CUST,LAST_USERID,LAST_TIMESTAMP,");
            sQuery.append("ETS_PROJECT_ID FROM "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 WHERE APPLICATION_ID = '" + sAppId + "' AND ETS_PROJECT_ID = '" + sProjId + "' AND PROBLEM_CREATOR = '" + sIRId + "' AND UCASE(PROBLEM_CLASS) = 'FEEDBACK' ORDER BY CREATION_DATE for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            while (rs.next()) {

                ETSFeedbackBean feedBack = new ETSFeedbackBean();

                feedBack.setApplicationId(rs.getString("APPLICATION_ID"));
                feedBack.setEdgeProblemId(rs.getString("EDGE_PROBLEM_ID"));
                feedBack.setCQTrackId(rs.getString("CQ_TRK_ID"));
                feedBack.setProblemState(rs.getString("PROBLEM_STATE"));
                feedBack.setSeqNo(rs.getInt("SEQ_NO"));
                feedBack.setProblemCreator(rs.getString("PROBLEM_CREATOR"));
                feedBack.setCustName(rs.getString("CUST_NAME"));
                feedBack.setCustEmail(rs.getString("CUST_EMAIL"));
                feedBack.setCustPhone(rs.getString("CUST_PHONE"));
                feedBack.setCustCompany(rs.getString("CUST_COMPANY"));
                feedBack.setCustProjectName(rs.getString("CUST_PROJECT"));
                feedBack.setProblemClass(rs.getString("PROBLEM_CLASS"));
                feedBack.setTitle(rs.getString("TITLE"));
                feedBack.setSeverity(rs.getString("SEVERITY"));
                feedBack.setProblemType(rs.getString("PROBLEM_TYPE"));
                feedBack.setComments(rs.getString("COMM_FROM_CUST"));
                feedBack.setLastUserId(rs.getString("LAST_USERID"));
                feedBack.setLastTime(rs.getTimestamp("LAST_TIMESTAMP"));
                feedBack.setETSProjectId(rs.getString("ETS_PROJECT_ID"));

                vFeedBacks.addElement(feedBack);

            }


        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(rs);
            ETSDBUtils.close(stmt);
        }

        return vFeedBacks;
	}



    public static final String VERSION = "1.2";

    public static boolean insertFeedback(Connection con,ETSFeedbackBean feedBack, String sFirstName, String sLastName) throws SQLException,Exception {

        PreparedStatement stmt = null;
        StringBuffer sQuery = new StringBuffer("");

        try {

           String sWorkspaceOwnerEmail = "";

           Vector vOwner = ETSDatabaseManager.getUsersByProjectPriv(feedBack.getETSProjectId(),Defines.OWNER,con);

           if (vOwner != null && vOwner.size() > 0) {
                ETSUser user = (ETSUser) vOwner.elementAt(0);
                sWorkspaceOwnerEmail = ETSUtils.getUserEmail(con,user.getUserId());
           }


           sQuery.append("INSERT INTO "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 (");
           sQuery.append("APPLICATION_ID,EDGE_PROBLEM_ID,CQ_TRK_ID,PROBLEM_STATE,");
           sQuery.append("SEQ_NO,PROBLEM_CREATOR,CREATION_DATE,CUST_NAME,CUST_EMAIL,");
           sQuery.append("CUST_PHONE,CUST_COMPANY,CUST_PROJECT,PROBLEM_CLASS,TITLE,");
           sQuery.append("SEVERITY,PROBLEM_TYPE,COMM_FROM_CUST,LAST_USERID,LAST_TIMESTAMP,");
           sQuery.append("ETS_PROJECT_ID,ETS_CCLIST,FIELD_C14, FIELD_C15) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");



           SysLog.log(SysLog.DEBUG,"Feedback Insert",sQuery.toString());

           stmt = con.prepareStatement(sQuery.toString());

           stmt.setString(1,feedBack.getApplicationId());
           stmt.setString(2,feedBack.getEdgeProblemId());
           stmt.setString(3,feedBack.getCQTrackId());
           stmt.setString(4,feedBack.getProblemState());
           stmt.setInt(5,feedBack.getSeqNo());
           stmt.setString(6,feedBack.getLastUserId());
           stmt.setTimestamp(7,feedBack.getLastTime());
           stmt.setString(8,feedBack.getCustName());
           stmt.setString(9,feedBack.getCustEmail());
           stmt.setString(10,feedBack.getCustPhone());
           stmt.setString(11,feedBack.getCustCompany());
           stmt.setString(12,feedBack.getCustProjectName());
           stmt.setString(13,feedBack.getProblemClass());
           stmt.setString(14,ETSUtils.escapeString(feedBack.getTitle()));
           stmt.setString(15,feedBack.getSeverity());
           stmt.setString(16,feedBack.getProblemType());
           stmt.setString(17,ETSUtils.escapeString(feedBack.getComments()));
           stmt.setString(18,feedBack.getLastUserId());
           stmt.setTimestamp(19,feedBack.getLastTime());
           stmt.setString(20,feedBack.getETSProjectId());
           stmt.setString(21,sWorkspaceOwnerEmail);

           stmt.setString(22,sFirstName);
           stmt.setString(23,sLastName);

           int iCount = stmt.executeUpdate();

           if (iCount > 0) {
                return true;
           } else {
                return false;
           }

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(stmt);
        }

    }
    public static String getFeedbackCCList(Connection con, String sApplicationId, String sEdgeProblemId) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String sCCList = "";

        try {

            sQuery.append("SELECT ETS_CCLIST FROM "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 WHERE APPLICATION_ID = '" + sApplicationId + "' AND EDGE_PROBLEM_ID = '" + sEdgeProblemId + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sCCList = ETSUtils.checkNull(rs.getString("ETS_CCLIST"));
            }


        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(rs);
            ETSDBUtils.close(stmt);
        }

        return sCCList;


    }

	public static boolean updateFeedback(Connection con,ETSFeedbackBean feedbackBean, String sFirstName, String sLastName) throws SQLException, Exception {

        PreparedStatement stmt = null;
        StringBuffer sQuery = new StringBuffer("");

        try {

           String sWorkspaceOwnerEmail = "";

           Vector vOwner = ETSDatabaseManager.getUsersByProjectPriv(feedbackBean.getETSProjectId(),Defines.OWNER,con);

           if (vOwner != null && vOwner.size() > 0) {
                ETSUser user = (ETSUser) vOwner.elementAt(0);
                sWorkspaceOwnerEmail = ETSUtils.getUserEmail(con,user.getUserId());
           }

           String sUpdateEmail = ETSUtils.getUserEmail(con,feedbackBean.getLastUserId());
           String sCurrentCCList = getFeedbackCCList(con,feedbackBean.getApplicationId(),feedbackBean.getEdgeProblemId());

           if (sCurrentCCList.indexOf(sUpdateEmail) < 0) {
                sCurrentCCList = sCurrentCCList + "," + sUpdateEmail;
           }

           if (sCurrentCCList.indexOf(sWorkspaceOwnerEmail) < 0) {
                sCurrentCCList = sCurrentCCList + "," + sWorkspaceOwnerEmail;
           }

           sQuery.append("UPDATE "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 SET ");
           sQuery.append("SEQ_NO = ?,");
           sQuery.append("CQ_TRK_ID = ?,");
           sQuery.append("PROBLEM_STATE = ?,");
           sQuery.append("TITLE = ?,");
           sQuery.append("COMM_FROM_CUST = ?,");
           sQuery.append("ETS_CCLIST = ?,");
           sQuery.append("LAST_USERID = ?,");
           sQuery.append("FIELD_C14 = ?,");
           sQuery.append("FIELD_C15 = ?,");
           sQuery.append("LAST_TIMESTAMP = ? ");
           sQuery.append("WHERE APPLICATION_ID = ? AND ");
           sQuery.append("EDGE_PROBLEM_ID = ? " );

           SysLog.log(SysLog.DEBUG,"Feedback update",sQuery.toString());

           stmt = con.prepareStatement(sQuery.toString());

           stmt.setInt(1,feedbackBean.getSeqNo());
           stmt.setString(2,feedbackBean.getCQTrackId());
           stmt.setString(3,feedbackBean.getProblemState());
           stmt.setString(4,ETSUtils.escapeString(feedbackBean.getTitle()));
           stmt.setString(5, ETSUtils.escapeString(feedbackBean.getComments()));
           stmt.setString(6, sCurrentCCList);
           stmt.setString(7, feedbackBean.getLastUserId());
           stmt.setString(8, sFirstName);
           stmt.setString(9, sLastName);
           stmt.setTimestamp(10, feedbackBean.getLastTime());
           stmt.setString(11, feedbackBean.getApplicationId());
           stmt.setString(12, feedbackBean.getEdgeProblemId());

           int iCount = stmt.executeUpdate();

           if (iCount > 0) {
                return true;
           } else {
                return false;
           }

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(stmt);
        }
	}

	public static Vector getAllProjectFeedbacks(Connection con,String sAppId, String sProjId) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;

        StringBuffer sQuery = new StringBuffer("");
        Vector vFeedBacks = new Vector();

        try {

            sQuery.append("SELECT ");
            sQuery.append("APPLICATION_ID,EDGE_PROBLEM_ID,CQ_TRK_ID,PROBLEM_STATE,");
            sQuery.append("SEQ_NO,PROBLEM_CREATOR,CREATION_DATE,CUST_NAME,CUST_EMAIL,");
            sQuery.append("CUST_PHONE,CUST_COMPANY,CUST_PROJECT,PROBLEM_CLASS,TITLE,");
            sQuery.append("SEVERITY,PROBLEM_TYPE,COMM_FROM_CUST,LAST_USERID,LAST_TIMESTAMP, ");
            sQuery.append("ETS_PROJECT_ID FROM "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 WHERE APPLICATION_ID = '" + sAppId + "' AND ETS_PROJECT_ID = '" + sProjId + "' AND UCASE(PROBLEM_CLASS) = 'FEEDBACK' ORDER BY CREATION_DATE for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            while (rs.next()) {

                ETSFeedbackBean feedBack = new ETSFeedbackBean();

                feedBack.setApplicationId(rs.getString("APPLICATION_ID"));
                feedBack.setEdgeProblemId(rs.getString("EDGE_PROBLEM_ID"));
                feedBack.setCQTrackId(rs.getString("CQ_TRK_ID"));
                feedBack.setProblemState(rs.getString("PROBLEM_STATE"));
                feedBack.setSeqNo(rs.getInt("SEQ_NO"));
                feedBack.setProblemCreator(rs.getString("PROBLEM_CREATOR"));
                feedBack.setCustName(rs.getString("CUST_NAME"));
                feedBack.setCustEmail(rs.getString("CUST_EMAIL"));
                feedBack.setCustPhone(rs.getString("CUST_PHONE"));
                feedBack.setCustCompany(rs.getString("CUST_COMPANY"));
                feedBack.setCustProjectName(rs.getString("CUST_PROJECT"));
                feedBack.setProblemClass(rs.getString("PROBLEM_CLASS"));
                feedBack.setTitle(rs.getString("TITLE"));
                feedBack.setSeverity(rs.getString("SEVERITY"));
                feedBack.setProblemType(rs.getString("PROBLEM_TYPE"));
                feedBack.setComments(rs.getString("COMM_FROM_CUST"));
                feedBack.setLastUserId(rs.getString("LAST_USERID"));
                feedBack.setLastTime(rs.getTimestamp("LAST_TIMESTAMP"));
                feedBack.setETSProjectId(rs.getString("ETS_PROJECT_ID"));

                vFeedBacks.addElement(feedBack);

            }


        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(rs);
            ETSDBUtils.close(stmt);
        }

        return vFeedBacks;
	}

	public static int getSeqNoFromCQ1(Connection con, String sEdgeProblemId) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        int iSeq = 0;

        try {

            sQuery.append("SELECT SEQ_NO FROM "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 WHERE EDGE_PROBLEM_ID = '" + sEdgeProblemId + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                iSeq = rs.getInt("SEQ_NO");
            } else {
                iSeq = 0;
            }


        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(rs);
            ETSDBUtils.close(stmt);
        }

        return iSeq;

	}

	public static String getCommentsLog(Connection con,String sEdgeProblemId) throws SQLException, Exception {
       Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String sCommLog = "";

        try {

            sQuery.append("SELECT COMM_LOG FROM "+ISMGTSCHEMA+".PROBLEM_INFO_CQ2 WHERE EDGE_PROBLEM_ID = '" + sEdgeProblemId + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sCommLog = rs.getString("COMM_LOG");
            } else {
                sCommLog = "";
            }


        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(rs);
            ETSDBUtils.close(stmt);
        }

        return sCommLog;

	}

	public static String getCQTrkId(Connection con, String sEdgeProblemId) throws SQLException, Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sQuery = new StringBuffer("");
        String sCQTrkID = "-";

        try {

            sQuery.append("SELECT CQ_TRK_ID FROM "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 WHERE EDGE_PROBLEM_ID = '" + sEdgeProblemId + "' for READ ONLY");

            stmt = con.createStatement();
            rs = stmt.executeQuery(sQuery.toString());

            if (rs.next()) {
                sCQTrkID = rs.getString("CQ_TRK_ID");
            } else {
                sCQTrkID = "-";
            }


        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(rs);
            ETSDBUtils.close(stmt);
        }

        return sCQTrkID;

	}

}
