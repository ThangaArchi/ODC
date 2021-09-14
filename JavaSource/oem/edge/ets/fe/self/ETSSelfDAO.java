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


/*
 * Created on Jan 19, 2005
 */

package oem.edge.ets.fe.self;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSelfDAO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.5";

	public static Vector getOpenSelfAssessments(Connection con, String sProjectId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		Vector vSelf = new Vector();

		try {
	
			sQuery.append("SELECT SELF_ID,PROJECT_ID,SELF_NAME,SELF_PM,SELF_PLAN_OWNER,SELF_DATE,STATE,LAST_TIMESTAMP FROM ETS.ETS_SELF_MAIN WHERE PROJECT_ID = '" + sProjectId + "' AND STATE = '" + ETSSelfConstants.SELF_OPEN + "' for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				
				ETSSelfAssessment self = new ETSSelfAssessment();

				self.setTitle(rs.getString("SELF_NAME"));
				self.setProjectId(rs.getString("PROJECT_ID"));
				self.setSelfId(rs.getString("SELF_ID"));
				self.setAssessmentOwner(rs.getString("SELF_PM"));
				self.setPlanOwner(rs.getString("SELF_PLAN_OWNER"));
				self.setStartDate(rs.getTimestamp("SELF_DATE"));
				self.setState(rs.getString("STATE"));
				self.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
				self.setExpectations(getSelfExpectations(con,self.getSelfId(),sProjectId));
				self.setMembers(getSelfMembers(con,self.getSelfId(),sProjectId));
				self.setStep(getSelfAssessmentSteps(con,self.getSelfId(),sProjectId));
				self.setDueDates(getSelfAssessmentDueDates(con,self.getSelfId(),sProjectId));

				if (self.getState().trim().equals(ETSSelfConstants.SELF_OPEN)) {
					vSelf.addElement(self);
				}

			}

			
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return vSelf;
	}
	public static Vector getClosedSelfAssessments(Connection con, String sProjectId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		Vector vSelf = new Vector();

		try {

			sQuery.append("SELECT SELF_ID,PROJECT_ID,SELF_NAME,SELF_PM,SELF_PLAN_OWNER,SELF_DATE,STATE,LAST_TIMESTAMP FROM ETS.ETS_SELF_MAIN WHERE PROJECT_ID = '" + sProjectId + "' AND STATE = '" + ETSSelfConstants.SELF_CLOSED + "' for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {


				ETSSelfAssessment self = new ETSSelfAssessment();

				self.setTitle(rs.getString("SELF_NAME"));
				self.setProjectId(rs.getString("PROJECT_ID"));
				self.setSelfId(rs.getString("SELF_ID"));
				self.setAssessmentOwner(rs.getString("SELF_PM"));
				self.setPlanOwner(rs.getString("SELF_PLAN_OWNER"));
				self.setStartDate(rs.getTimestamp("SELF_DATE"));
				self.setState(rs.getString("STATE"));
				self.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));

				self.setExpectations(getSelfExpectations(con,self.getSelfId(),sProjectId));
				self.setMembers(getSelfMembers(con,self.getSelfId(),sProjectId));
				self.setStep(getSelfAssessmentSteps(con,self.getSelfId(),sProjectId));
				self.setDueDates(getSelfAssessmentDueDates(con,self.getSelfId(),sProjectId));

				if (self.getState().trim().equals(ETSSelfConstants.SELF_CLOSED)) {
					vSelf.addElement(self);
				}
				

			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return vSelf;
	}
	/**
	 * @return
	 */
	public static ArrayList getInternalNotClientUsersInWorkspace(Connection con, String sProjectID) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		ArrayList userlist = new ArrayList();

		try {

			sQuery.append("SELECT LTRIM(RTRIM(A.IR_USERID)),LTRIM(RTRIM(A.USER_LNAME)) || ', ' || LTRIM(RTRIM(A.USER_FNAME)) FROM AMT.USERS A, DECAF.USERS B WHERE A.IR_USERID IN (SELECT P.USER_ID FROM ETS.ETS_USERS P WHERE P.USER_PROJECT_ID = '" + sProjectID + "' AND P.ACTIVE_FLAG = '" + Defines.USER_ENTITLED + "' AND USER_ROLE_ID NOT IN (SELECT DISTINCT ROLE_ID FROM ETS.ETS_ROLES WHERE PRIV_ID IN (" + Defines.CLIENT + "," + Defines.VISITOR + ") AND PROJECT_ID = '" + sProjectID + "')) AND A.EDGE_USERID = B.USERID AND B.USER_TYPE = 'I' ORDER BY 2 for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sID = rs.getString(1);
				String sName = rs.getString(2);

				ETSUser user = new ETSUser();

				user.setUserName(sName);
				user.setUserId(sID);

				userlist.add(user);

			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return userlist;

	}
	/**
	 *
	 */
	public ETSSelfDAO() {
		super();
	}


	public static boolean createSelfAssessment(Connection con, String sSelfID, String sProjectID, ETSSelfAssessmentForm self) throws SQLException, Exception {

		boolean created = false;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;

		try {

		sQuery.append("INSERT INTO ETS.ETS_SELF_MAIN (SELF_ID,PROJECT_ID,SELF_NAME,SELF_PM,SELF_PLAN_OWNER,SELF_DATE,STATE,LAST_TIMESTAMP) ");
		sQuery.append("VALUES (?,?,?,?,?,?,?,?)");

		stmt = con.prepareStatement(sQuery.toString());
		stmt.clearParameters();

		stmt.setString(1,sSelfID);
		stmt.setString(2,sProjectID);
		stmt.setString(3,self.getTitle());
		stmt.setString(4,self.getAssessmentOwner());
		stmt.setString(5,self.getPlanOwner());
		stmt.setTimestamp(6,Timestamp.valueOf(self.getStartYear() + "-" + self.getStartMonth() + "-" + self.getStartDay() + " 00:00:00.000000000"));
		stmt.setString(7,"OPEN");
		stmt.setTimestamp(8,new Timestamp(System.currentTimeMillis()));

		int iCount = stmt.executeUpdate();

		if (iCount > 0) {
			created = true;
		}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}


		return created;

	}

	public static boolean createSelfAssessmentMembers(Connection con, String sSelfID, String sProjectID, ETSSelfAssessmentForm self, String sLastUserId) throws SQLException, Exception {

		boolean created = false;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;

		try {

		sQuery.append("INSERT INTO ETS.ETS_SELF_MEMBERS (SELF_ID,PROJECT_ID,MEMBER_IR_ID,COMPLETED,DUE_DATE,LAST_USERID,LAST_TIMESTAMP) ");
		sQuery.append("VALUES (?,?,?,?,?,?,?)");

		stmt = con.prepareStatement(sQuery.toString());

		String sMembers[] = self.getMembers();

		for (int i = 0; i < sMembers.length; i++) {

			String sMemberID = sMembers[i].trim();

			stmt.clearParameters();

			stmt.setString(1,sSelfID);
			stmt.setString(2,sProjectID);
			stmt.setString(3,sMemberID);
			stmt.setString(4,"N");
			stmt.setTimestamp(5,Timestamp.valueOf(self.getMemberDueYear() + "-" + self.getMemberDueMonth() + "-" + self.getMemberDueDay() + " 00:00:00.000000000"));
			stmt.setString(6,sLastUserId);
			stmt.setTimestamp(7,new Timestamp(System.currentTimeMillis()));

			int iCount = stmt.executeUpdate();

			if (iCount > 0 && !created) {
				created = true;
			}
		}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return created;

	}

	public static boolean createSelfAssessmentDueDates(Connection con, String sSelfID, String sProjectID, ETSSelfAssessmentForm self) throws SQLException, Exception {

		boolean created = false;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;

		try {

		sQuery.append("INSERT INTO ETS.ETS_SELF_NOTIFY (SELF_ID, PROJECT_ID, STEP, DUE_DATE, IS_NOTIFIED, LAST_TIMESTAMP) ");
		sQuery.append("VALUES (?,?,?,?,?,?)");

		stmt = con.prepareStatement(sQuery.toString());

		stmt.clearParameters();

		stmt.setString(1,sSelfID);
		stmt.setString(2,sProjectID);
		stmt.setString(3,ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT);
		stmt.setTimestamp(4,Timestamp.valueOf(self.getMemberDueYear() + "-" + self.getMemberDueMonth() + "-" + self.getMemberDueDay() + " 00:00:00.000000000"));
		stmt.setString(5,"N");
		stmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));

		int iCount = stmt.executeUpdate();

		stmt.clearParameters();

		stmt.setString(1,sSelfID);
		stmt.setString(2,sProjectID);
		stmt.setString(3,ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT);
		stmt.setTimestamp(4,Timestamp.valueOf(self.getAssessDueYear() + "-" + self.getAssessDueMonth() + "-" + self.getAssessDueDay() + " 00:00:00.000000000"));
		stmt.setString(5,"N");
		stmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));

		iCount = stmt.executeUpdate();

		stmt.clearParameters();

		stmt.setString(1,sSelfID);
		stmt.setString(2,sProjectID);
		stmt.setString(3,ETSSelfConstants.SELF_STEP_ACTION_PLAN);
		stmt.setTimestamp(4,Timestamp.valueOf(self.getPlanDueYear() + "-" + self.getPlanDueMonth() + "-" + self.getPlanDueDay() + " 00:00:00.000000000"));
		stmt.setString(5,"N");
		stmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));

		iCount = stmt.executeUpdate();

		if (iCount > 0) {
			created = true;
		}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return created;

	}

	public static boolean createSelfAssessmentStep(Connection con, String sSelfID, String sProjectID, String sStep, String sActionBy) throws SQLException, Exception {

		boolean created = false;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;

		try {

		sQuery.append("INSERT INTO ETS.ETS_SELF_PLAN (SELF_ID, PROJECT_ID, STEP, ACTION_DATE, ACTION_BY_ID, LAST_TIMESTAMP) ");
		sQuery.append("VALUES (?,?,?,?,?,?)");

		stmt = con.prepareStatement(sQuery.toString());

		stmt.clearParameters();

		stmt.setString(1,sSelfID);
		stmt.setString(2,sProjectID);
		stmt.setString(3,sStep);
		stmt.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
		stmt.setString(5,sActionBy);
		stmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));

		int iCount = stmt.executeUpdate();

		if (iCount > 0) {
			created = true;
		}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return created;

	}

	public static boolean createSelfAssessmentExpectation(Connection con, ETSAttributeForm keyform, String sLastUserId) throws SQLException, Exception {

		boolean created = false;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;

		try {

		sQuery.append("INSERT INTO ETS.ETS_SELF_EXP (SELF_ID, PROJECT_ID, SECTION_ID, SUB_SECTION_ID, SEQ_NO, MEMBER_IR_ID, COMMENTS, RATING, EXPECT_ID, LAST_USERID,LAST_TIMESTAMP) ");
		sQuery.append("VALUES (?,?,?,?,?,?,?,?,?,?,?)");

		stmt = con.prepareStatement(sQuery.toString());

		stmt.clearParameters();

		stmt.setString(1, keyform.getSelfId());
		stmt.setString(2,keyform.getProjectId());
		stmt.setInt(3,Integer.parseInt(keyform.getSectionId()));
		stmt.setInt(4,Integer.parseInt(keyform.getSubSectionId()));
		stmt.setInt(5,Integer.parseInt(keyform.getSequenceNo()));
		stmt.setString(6,sLastUserId);
		stmt.setString(7,keyform.getComments());
		stmt.setInt(8,Integer.parseInt(keyform.getRating()));
		stmt.setInt(9,Integer.parseInt(keyform.getExpectId()));
		stmt.setString(10,sLastUserId);
		stmt.setTimestamp(11,new Timestamp(System.currentTimeMillis()));

		int iCount = stmt.executeUpdate();

		if (iCount > 0 && !created) {
			created = true;
		}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return created;

	}

	public static boolean createSelfAssessmentExpectation(Connection con, ETSOverallAttributeForm keyform, String sLastUserId) throws SQLException, Exception {
	
		boolean created = false;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;
	
		try {
	
		sQuery.append("INSERT INTO ETS.ETS_SELF_EXP (SELF_ID, PROJECT_ID, SECTION_ID, SUB_SECTION_ID, SEQ_NO, MEMBER_IR_ID, COMMENTS, RATING, EXPECT_ID, LAST_USERID,LAST_TIMESTAMP) ");
		sQuery.append("VALUES (?,?,?,?,?,?,?,?,?,?,?)");
	
		stmt = con.prepareStatement(sQuery.toString());
	
		stmt.clearParameters();
	
		stmt.setString(1, keyform.getSelfId());
		stmt.setString(2,keyform.getProjectId());
		stmt.setInt(3,Integer.parseInt(keyform.getSectionId()));
		stmt.setInt(4,Integer.parseInt(keyform.getSubSectionId()));
		stmt.setInt(5,Integer.parseInt(keyform.getSequenceNo()));
		stmt.setString(6,sLastUserId);
		stmt.setString(7,keyform.getComments());
		stmt.setInt(8,Integer.parseInt(keyform.getRating()));
		stmt.setInt(9,Integer.parseInt(keyform.getExpectId()));
		stmt.setString(10,sLastUserId);
		stmt.setTimestamp(11,new Timestamp(System.currentTimeMillis()));
	
		int iCount = stmt.executeUpdate();
	
		if (iCount > 0 && !created) {
			created = true;
		}
	
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	
		return created;
	
	}

	public static boolean updateSelfAssessmentExpectation(Connection con, ETSAttributeForm keyform, String sLastUserId) throws SQLException, Exception {

		boolean created = false;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;

		try {

		sQuery.append("UPDATE ETS.ETS_SELF_EXP SET RATING = ?, COMMENTS = ?, LAST_USERID = ?, EXPECT_ID =?, LAST_TIMESTAMP = ? WHERE SELF_ID = ? AND PROJECT_ID = ? AND SECTION_ID = ? AND SUB_SECTION_ID = ? AND SEQ_NO = ?  ");

		stmt = con.prepareStatement(sQuery.toString());

		stmt.clearParameters();

		stmt.setInt(1, Integer.parseInt(keyform.getRating()));
		stmt.setString(2, keyform.getComments());
		stmt.setString(3,sLastUserId);
		stmt.setInt(4,Integer.parseInt(keyform.getExpectId()));
		stmt.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
		stmt.setString(6,keyform.getSelfId());
		stmt.setString(7,keyform.getProjectId());
		stmt.setInt(8,Integer.parseInt(keyform.getSectionId()));
		stmt.setInt(9,Integer.parseInt(keyform.getSubSectionId()));
		stmt.setInt(10,Integer.parseInt(keyform.getSequenceNo()));

		int iCount = stmt.executeUpdate();

		if (iCount > 0) {

			created = true;
		}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return created;

	}

	public static boolean updateSelfAssessmentExpectation(Connection con, ETSOverallAttributeForm keyform, String sLastUserId) throws SQLException, Exception {
	
		boolean created = false;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;
	
		try {
	
		sQuery.append("UPDATE ETS.ETS_SELF_EXP SET RATING = ?, COMMENTS = ?, LAST_USERID = ?, LAST_TIMESTAMP = ? WHERE SELF_ID = ? AND PROJECT_ID = ? AND SECTION_ID = ? AND SUB_SECTION_ID = ? AND SEQ_NO = ?  ");
	
		stmt = con.prepareStatement(sQuery.toString());
	
		stmt.clearParameters();
	
		stmt.setInt(1, Integer.parseInt(keyform.getRating()));
		stmt.setString(2, keyform.getComments());
		stmt.setString(3,sLastUserId);
		stmt.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
		stmt.setString(5,keyform.getSelfId());
		stmt.setString(6,keyform.getProjectId());
		stmt.setInt(7,Integer.parseInt(keyform.getSectionId()));
		stmt.setInt(8,Integer.parseInt(keyform.getSubSectionId()));
		stmt.setInt(9,Integer.parseInt(keyform.getSequenceNo()));
	
		int iCount = stmt.executeUpdate();
	
		if (iCount > 0) {
	
			created = true;
		}
	
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	
		return created;
	
	}

	public static boolean deleteSelfAssessmentExpectation(Connection con, ETSAttributeForm keyform, String sLastUserId) throws SQLException, Exception {

		boolean created = false;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;

		try {

		sQuery.append("DELETE FROM ETS.ETS_SELF_EXP WHERE SELF_ID = ? AND PROJECT_ID = ? AND SECTION_ID = ? AND SUB_SECTION_ID = ? AND SEQ_NO = ? ");

		stmt = con.prepareStatement(sQuery.toString());

		stmt.clearParameters();

		stmt.setString(1,keyform.getSelfId());
		stmt.setString(2,keyform.getProjectId());
		stmt.setInt(3,Integer.parseInt(keyform.getSectionId()));
		stmt.setInt(4,Integer.parseInt(keyform.getSubSectionId()));
		stmt.setInt(5,Integer.parseInt(keyform.getSequenceNo()));

		int iCount = stmt.executeUpdate();

		if (iCount > 0) {
			created = true;
		}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return created;

	}

	private static ArrayList getSelfExpectations(Connection con, String sSelfId, String sProjectId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		ArrayList explist = new ArrayList();

		try {

			sQuery.append("SELECT SELF_ID,PROJECT_ID,SECTION_ID,SUB_SECTION_ID,SEQ_NO,MEMBER_IR_ID,COMMENTS,RATING,EXPECT_ID,LAST_USERID,LAST_TIMESTAMP FROM ETS.ETS_SELF_EXP WHERE SELF_ID = '" + sSelfId + "' AND PROJECT_ID = '" + sProjectId + "' for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				ETSSelfAssessmentExpectation exp = new ETSSelfAssessmentExpectation();

				exp.setSelfId(rs.getString("SELF_ID"));
				exp.setProjectId(rs.getString("PROJECT_ID"));
				exp.setSectionId(rs.getInt("SECTION_ID"));
				exp.setSubSectionId(rs.getInt("SUB_SECTION_ID"));
				exp.setSequenceNo(rs.getInt("SEQ_NO"));
				exp.setMemberId(rs.getString("MEMBER_IR_ID"));
				exp.setMemberName(ETSUtils.getUsersName(con,exp.getMemberId()));
				exp.setComments(rs.getString("COMMENTS"));
				exp.setRating(rs.getInt("RATING"));
				exp.setExpectId(rs.getInt("EXPECT_ID"));
				exp.setExpectName(getExpectName(con,exp.getExpectId()));
				exp.setLastUserId(rs.getString("LAST_USERID"));
				exp.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));

				explist.add(exp);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return explist;
	}

	private static ArrayList getSelfMembers(Connection con, String sSelfId, String sProjectId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		ArrayList memberlist = new ArrayList();

		try {

			sQuery.append("SELECT SELF_ID,PROJECT_ID,MEMBER_IR_ID, COMPLETED,DUE_DATE,LAST_USERID,LAST_TIMESTAMP FROM ETS.ETS_SELF_MEMBERS WHERE SELF_ID = '" + sSelfId + "' AND PROJECT_ID = '" + sProjectId + "' for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				ETSSelfAssessmentMember member = new ETSSelfAssessmentMember();

				member.setSelfId(rs.getString("SELF_ID"));
				member.setProjectId(rs.getString("PROJECT_ID"));
				member.setMemberId(rs.getString("MEMBER_IR_ID"));
				member.setCompleted(rs.getString("COMPLETED"));
				member.setDueDate(rs.getTimestamp("DUE_DATE"));
				member.setLastUserId(rs.getString("LAST_USERID"));
				member.setLastUserName(ETSUtils.getUsersName(con,member.getLastUserId()));
				member.setMemberName(ETSUtils.getUsersName(con,member.getMemberId()));
				member.setMemberEmail(ETSUtils.getUserEmail(con,member.getMemberId()));

				memberlist.add(member);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return memberlist;
	}

	private static ArrayList getSelfAssessmentDueDates(Connection con, String sSelfId, String sProjectId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		ArrayList duedates = new ArrayList();

		try {

			sQuery.append("SELECT SELF_ID, PROJECT_ID, STEP, DUE_DATE, IS_NOTIFIED, LAST_TIMESTAMP FROM ETS.ETS_SELF_NOTIFY WHERE SELF_ID = '" + sSelfId + "' AND PROJECT_ID = '" + sProjectId + "' for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				ETSSelfAssessmentDueDate due = new ETSSelfAssessmentDueDate();

				due.setSelfId(rs.getString("SELF_ID"));
				due.setProjectId(rs.getString("PROJECT_ID"));
				due.setStep(rs.getString("STEP"));
				due.setDueDate(rs.getTimestamp("DUE_DATE"));
				due.setNotified(rs.getString("IS_NOTIFIED"));
				due.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));

				duedates.add(due);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return duedates;
	}

	private static ArrayList getSelfAssessmentSteps(Connection con, String sSelfId, String sProjectId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		ArrayList memberlist = new ArrayList();

		try {

			sQuery.append("SELECT SELF_ID, PROJECT_ID, STEP, ACTION_DATE, ACTION_BY_ID, LAST_TIMESTAMP, ACTION_DUE_DATE FROM ETS.ETS_SELF_PLAN WHERE SELF_ID = '" + sSelfId + "' AND PROJECT_ID = '" + sProjectId + "' ORDER BY LAST_TIMESTAMP for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				ETSSelfAssessmentStep step = new ETSSelfAssessmentStep();

				step.setSelfId(rs.getString("SELF_ID"));
				step.setProjectId(rs.getString("PROJECT_ID"));
				step.setStep(rs.getString("STEP"));
				step.setActionDate(rs.getTimestamp("ACTION_DATE"));
				step.setActioById(rs.getString("ACTION_BY_ID"));
				step.setActionByName(ETSUtils.getUsersName(con,step.getActioById()));

				memberlist.add(step);

			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return memberlist;
	}

	public static synchronized String getNewSetMetID() throws SQLException, Exception {

		Random rand = new Random();

		String sUniqueId = "";

		Long lDate = new Long(System.currentTimeMillis());
		sUniqueId = lDate + "-" + rand.nextInt(1000) + "";

		return sUniqueId;
	}

	public static ETSSelfAssessment getSelfAssessment(Connection con, String sProjectId, String sSelfId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");
		ETSSelfAssessment self = new ETSSelfAssessment();

		try {

			if (sSelfId.trim().equals("")) {
				sQuery.append("SELECT SELF_ID,PROJECT_ID,SELF_NAME,SELF_PM,SELF_PLAN_OWNER,SELF_DATE,STATE,LAST_TIMESTAMP FROM ETS.ETS_SELF_MAIN WHERE ");
				sQuery.append("PROJECT_ID = '" + sProjectId + "' ORDER BY SELF_DATE DESC for READ ONLY");
			} else {
				sQuery.append("SELECT SELF_ID,PROJECT_ID,SELF_NAME,SELF_PM,SELF_PLAN_OWNER,SELF_DATE,STATE,LAST_TIMESTAMP FROM ETS.ETS_SELF_MAIN WHERE SELF_ID = '" + sSelfId + "' AND PROJECT_ID = '" + sProjectId + "' for READ ONLY");
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				String sSelf = rs.getString("SELF_ID");

				self.setSelfId(sSelf);
				self.setProjectId(rs.getString("PROJECT_ID"));
				self.setTitle(rs.getString("SELF_NAME"));
				self.setAssessmentOwner(rs.getString("SELF_PM"));
				self.setPlanOwner(rs.getString("SELF_PLAN_OWNER"));
				self.setStartDate(rs.getTimestamp("SELF_DATE"));
				self.setState(rs.getString("STATE"));
				self.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));

				self.setExpectations(getSelfExpectations(con,sSelf,sProjectId));
				self.setMembers(getSelfMembers(con,sSelf,sProjectId));
				self.setStep(getSelfAssessmentSteps(con,sSelf,sProjectId));
				self.setDueDates(getSelfAssessmentDueDates(con,sSelf,sProjectId));


			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return self;
	}

	public static ArrayList getMemberSectionStatus(Connection con, String sSelfId, String sProjectId, String sMemberIrId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		ArrayList memberstatus = new ArrayList();

		try {

			sQuery.append("SELECT SELF_ID, PROJECT_ID, SECTION_ID, MEMBER_IR_ID, STATUS, LAST_TIMESTAMP FROM ETS.ETS_SELF_SECTION WHERE SELF_ID = '" + sSelfId + "' AND PROJECT_ID = '" + sProjectId + "' AND MEMBER_IR_ID = '" + sMemberIrId + "' ORDER BY SECTION_ID for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				ETSSelfMemberSectionStatus status = new ETSSelfMemberSectionStatus();

				status.setSelfId(rs.getString("SELF_ID"));
				status.setProjectId(rs.getString("PROJECT_ID"));
				status.setSectionId(rs.getInt("SECTION_ID"));
				status.setMemberId(rs.getString("MEMBER_IR_ID"));
				status.setMemberName(ETSUtils.getUsersName(con,status.getMemberId()));
				status.setStatus(rs.getString("STATUS"));
				status.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));

				memberstatus.add(status);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return memberstatus;
	}

	private static String getExpectName(Connection con, int iExpectId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");

		String sExpectName = "";

		try {

			sQuery.append("SELECT DESCRIPTION FROM ETS.ETS_SELF_DATA WHERE EXPECT_ID = " + iExpectId + " for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				sExpectName = rs.getString("DESCRIPTION");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return sExpectName;
	}

		public static ArrayList getExpectations(Connection con) throws SQLException, Exception {

			Statement stmt = null;
			ResultSet rs = null;

			StringBuffer sQuery = new StringBuffer("");

			ArrayList list = new ArrayList();

			try {

				sQuery.append("SELECT EXPECT_ID, EXPECT_CODE, DESCRIPTION FROM ETS.ETS_SELF_DATA ORDER BY EXPECT_ID for READ ONLY");

				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());

				while (rs.next()) {

					ETSSelfExpectation exp = new ETSSelfExpectation();
					exp.setExpectId(rs.getInt("EXPECT_ID"));
					exp.setExpectCode(rs.getString("EXPECT_CODE"));
					exp.setExpectDesc(rs.getString("DESCRIPTION"));

					list.add(exp);
				}

			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			return list;
		}

		public static synchronized int getNextSeqNo(Connection con, String sSelfId, String sProjectId, int iSectionId, int iSubSectionId) throws SQLException, Exception {

			Statement stmt = null;
			ResultSet rs = null;

			StringBuffer sQuery = new StringBuffer("");

			int iNext = 0;


			try {


				sQuery.append("SELECT MAX(SEQ_NO) + 1 FROM ETS.ETS_SELF_EXP WHERE SELF_ID = '" + sSelfId + "' AND PROJECT_ID = '" + sProjectId + "' AND SECTION_ID = " + iSectionId + " AND SUB_SECTION_ID = " + iSubSectionId + " for READ ONLY");

				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());

				if (rs.next()) {
					iNext = rs.getInt(1);
				}

				if (iNext == 0) {
					iNext = 1;
				}

			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			return iNext;
		}

		public static boolean completeMemberSection(Connection con, String sSelfId, String sProjectId, int iSectionId, String sMemberId) throws SQLException, Exception {

			boolean created = false;
			StringBuffer sQuery = new StringBuffer("");
			PreparedStatement stmt = null;

			try {

			sQuery.append("INSERT INTO ETS.ETS_SELF_SECTION (SELF_ID, PROJECT_ID, SECTION_ID, MEMBER_IR_ID, STATUS, LAST_TIMESTAMP) VALUES ");
			sQuery.append("(?,?,?,?,?,?)");

			stmt = con.prepareStatement(sQuery.toString());

			stmt.clearParameters();

			stmt.setString(1, sSelfId);
			stmt.setString(2, sProjectId);
			stmt.setInt(3,iSectionId);
			stmt.setString(4, sMemberId);
			stmt.setString(5,ETSSelfConstants.MEMBER_COMPLETED);
			stmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));

			int iCount = stmt.executeUpdate();

			if (iCount > 0) {
				created = true;
			}

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return created;

		}

		public static boolean updateMemberStatus(Connection con, String sSelfId, String sProjectId, String sMemberId) throws SQLException, Exception {

			boolean created = false;
			StringBuffer sQuery = new StringBuffer("");
			PreparedStatement stmt = null;

			try {

			sQuery.append("UPDATE ETS.ETS_SELF_MEMBERS SET COMPLETED = ?, LAST_TIMESTAMP = ? WHERE SELF_ID = ? AND PROJECT_ID = ? AND MEMBER_IR_ID = ? ");

			stmt = con.prepareStatement(sQuery.toString());

			stmt.clearParameters();

			stmt.setString(1, ETSSelfConstants.MEMBER_COMPLETED);
			stmt.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
			stmt.setString(3,sSelfId);
			stmt.setString(4,sProjectId);
			stmt.setString(5,sMemberId);

			int iCount = stmt.executeUpdate();


			if (iCount > 0) {

				created = true;
			}

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return created;

		}

		public static boolean closeSelfAssessment(Connection con, String sSelfId, String sProjectId, String sLastUserId) throws SQLException, Exception {

			boolean created = false;
			StringBuffer sQuery = new StringBuffer("");
			PreparedStatement stmt = null;

			try {

			sQuery.append("UPDATE ETS.ETS_SELF_MAIN SET STATE = ?, LAST_TIMESTAMP = ? WHERE SELF_ID = ? AND PROJECT_ID = ? ");

			stmt = con.prepareStatement(sQuery.toString());

			stmt.clearParameters();

			stmt.setString(1, ETSSelfConstants.SELF_CLOSED);
			stmt.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
			stmt.setString(3,sSelfId);
			stmt.setString(4,sProjectId);

			int iCount = stmt.executeUpdate();

			if (iCount > 0) {

				created = true;
			}

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return created;

		}

		public static boolean updateAllMemberAsCompleted(Connection con, String sSelfId, String sProjectId) throws SQLException, Exception {

			boolean created = false;
			StringBuffer sQuery = new StringBuffer("");
			PreparedStatement stmt = null;

			try {

			sQuery.append("UPDATE ETS.ETS_SELF_MEMBERS SET COMPLETED = ?, LAST_TIMESTAMP = ? WHERE SELF_ID = ? AND PROJECT_ID = ? ");

			stmt = con.prepareStatement(sQuery.toString());

			stmt.clearParameters();

			stmt.setString(1, ETSSelfConstants.MEMBER_COMPLETED);
			stmt.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
			stmt.setString(3,sSelfId);
			stmt.setString(4,sProjectId);

			int iCount = stmt.executeUpdate();


			if (iCount > 0) {

				created = true;
			}

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return created;

		}

		public static boolean updateSelfAssessmentDueDates(Connection con, String sSelfID, String sProjectID, ETSSelfAssessmentForm self) throws SQLException, Exception {

			boolean created = false;
			StringBuffer sQuery = new StringBuffer("");
			PreparedStatement stmt = null;

			try {

			sQuery.append("UPDATE ETS.ETS_SELF_NOTIFY SET DUE_DATE = ?, LAST_TIMESTAMP = ? WHERE SELF_ID = ? AND PROJECT_ID = ? AND STEP = ? ");

			stmt = con.prepareStatement(sQuery.toString());

			stmt.clearParameters();

			stmt.setTimestamp(1,Timestamp.valueOf(self.getMemberDueYear() + "-" + self.getMemberDueMonth() + "-" + self.getMemberDueDay() + " 00:00:00.000000000"));
			stmt.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
			stmt.setString(3,sSelfID);
			stmt.setString(4,sProjectID);
			stmt.setString(5,ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT);

			int iCount = stmt.executeUpdate();

			stmt.clearParameters();

			stmt.setTimestamp(1,Timestamp.valueOf(self.getAssessDueYear() + "-" + self.getAssessDueMonth() + "-" + self.getAssessDueDay() + " 00:00:00.000000000"));
			stmt.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
			stmt.setString(3,sSelfID);
			stmt.setString(4,sProjectID);
			stmt.setString(5,ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT);

			iCount = stmt.executeUpdate();

			stmt.clearParameters();

			stmt.setTimestamp(1,Timestamp.valueOf(self.getPlanDueYear() + "-" + self.getPlanDueMonth() + "-" + self.getPlanDueDay() + " 00:00:00.000000000"));
			stmt.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
			stmt.setString(3,sSelfID);
			stmt.setString(4,sProjectID);
			stmt.setString(5,ETSSelfConstants.SELF_STEP_ACTION_PLAN);

			iCount = stmt.executeUpdate();

			if (iCount > 0) {
				created = true;
			}

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return created;

		}

		public static boolean updateSelfAssessment(Connection con, String sSelfID, String sProjectID, ETSSelfAssessmentForm self) throws SQLException, Exception {

			boolean created = false;
			StringBuffer sQuery = new StringBuffer("");
			PreparedStatement stmt = null;

			try {

			sQuery.append("UPDATE ETS.ETS_SELF_MAIN SET SELF_NAME = ?, SELF_PM = ?, SELF_PLAN_OWNER = ?, SELF_DATE = ?, LAST_TIMESTAMP = ? WHERE SELF_ID = ? AND PROJECT_ID = ? ");

			stmt = con.prepareStatement(sQuery.toString());
			stmt.clearParameters();

			stmt.setString(1,self.getTitle());
			stmt.setString(2,self.getAssessmentOwner());
			stmt.setString(3,self.getPlanOwner());
			stmt.setTimestamp(4,Timestamp.valueOf(self.getStartYear() + "-" + self.getStartMonth() + "-" + self.getStartDay() + " 00:00:00.000000000"));
			stmt.setTimestamp(5,new Timestamp(System.currentTimeMillis()));

			stmt.setString(6,sSelfID);
			stmt.setString(7,sProjectID);

			int iCount = stmt.executeUpdate();


			if (iCount > 0) {
				created = true;
			}


			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}


			return created;

		}

		public static String getPrimaryContact(Connection con, String sProjectId) throws SQLException, Exception {
			
			Statement stmt = null;
			ResultSet rs = null;
		
			StringBuffer sQuery = new StringBuffer("");
			String sPrimaryID = "";
			
			try {
				
				sQuery.append("SELECT USER_ID FROM ETS.ETS_USERS WHERE USER_PROJECT_ID = '" + sProjectId + "' AND PRIMARY_CONTACT = 'Y' for READ ONLY");
		
				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());
				
				if (rs.next()) {
					
					sPrimaryID = rs.getString("USER_ID");
					
				}
			
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
			}
			
			return sPrimaryID;
		}

		public static ETSSelfAssessment getLatestSelfAssessment(Connection con, String sProjectId) throws SQLException, Exception {
		
			Statement stmt = null;
			ResultSet rs = null;
		
			StringBuffer sQuery = new StringBuffer("");
		
			ETSSelfAssessment selfAssessment = null; 
			
			try {
		
				sQuery.append("SELECT SELF_ID,PROJECT_ID,SELF_NAME,SELF_PM,SELF_PLAN_OWNER,SELF_DATE,STATE,LAST_TIMESTAMP FROM ETS.ETS_SELF_MAIN WHERE PROJECT_ID = '" + sProjectId + "' ORDER BY SELF_DATE DESC for READ ONLY");
		
				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());
		
				if (rs.next()) {
					
					selfAssessment = new ETSSelfAssessment();
		
					selfAssessment.setTitle(rs.getString("SELF_NAME"));
					selfAssessment.setProjectId(rs.getString("PROJECT_ID"));
					selfAssessment.setSelfId(rs.getString("SELF_ID"));
					selfAssessment.setAssessmentOwner(rs.getString("SELF_PM"));
					selfAssessment.setPlanOwner(rs.getString("SELF_PLAN_OWNER"));
					selfAssessment.setStartDate(rs.getTimestamp("SELF_DATE"));
					selfAssessment.setState(rs.getString("STATE"));
					selfAssessment.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
					selfAssessment.setExpectations(getSelfExpectations(con,selfAssessment.getSelfId(),sProjectId));
					selfAssessment.setMembers(getSelfMembers(con,selfAssessment.getSelfId(),sProjectId));
					selfAssessment.setStep(getSelfAssessmentSteps(con,selfAssessment.getSelfId(),sProjectId));
					selfAssessment.setDueDates(getSelfAssessmentDueDates(con,selfAssessment.getSelfId(),sProjectId));
		
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		
			return selfAssessment;
		}

}
