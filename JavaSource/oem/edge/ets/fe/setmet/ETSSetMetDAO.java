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

package oem.edge.ets.fe.setmet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSetMetDAO {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.12";
	
	private static Log logger = EtsLogger.getLogger(ETSSetMetDAO.class);

	
	public static ETSSetMet getSetMet(Connection con, String sProjectId, String sSetMet) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");
		ETSSetMet setmet = new ETSSetMet();
		
		try {
			
			if (sSetMet.trim().equals("")) {
				// display all setmets to all members.... changed in 5.2.1
				sQuery.append("SELECT QBR_ID,PROJECT_ID,QBR_NAME,CLIENT_IR_ID,ETS_PRACTICE,ETS_BSE,MEETING_DATE,NEXT_MEETING_DATE,STATE,INTERVIEW_BY,LAST_TIMESTAMP,EMAIL_SUPPR_FLAG,CLIENT_NAME FROM ETS.ETS_QBR_MAIN WHERE ");
				sQuery.append("PROJECT_ID = '" + sProjectId + "' ORDER BY MEETING_DATE DESC for READ ONLY");
			} else {
				sQuery.append("SELECT QBR_ID,PROJECT_ID,QBR_NAME,CLIENT_IR_ID,ETS_PRACTICE,ETS_BSE,MEETING_DATE,NEXT_MEETING_DATE,STATE,INTERVIEW_BY,LAST_TIMESTAMP,EMAIL_SUPPR_FLAG,CLIENT_NAME FROM ETS.ETS_QBR_MAIN WHERE QBR_ID = '" + sSetMet + "' AND PROJECT_ID = '" + sProjectId + "' for READ ONLY");
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			if (rs.next()) {
				
				setmet.setSetMetID(rs.getString("QBR_ID"));
				setmet.setProjectID(rs.getString("PROJECT_ID"));
				setmet.setSetMetName(rs.getString("QBR_NAME"));
				setmet.setClientIRID(rs.getString("CLIENT_IR_ID"));
				setmet.setSetMetPractice(rs.getString("ETS_PRACTICE"));
				setmet.setSetMetBSE(rs.getString("ETS_BSE"));
				setmet.setMeetingDate(rs.getTimestamp("MEETING_DATE"));
				setmet.setNextMeetingDate(rs.getTimestamp("NEXT_MEETING_DATE"));
				setmet.setState(rs.getString("STATE"));
				setmet.setInterviewByIRID(rs.getString("INTERVIEW_BY"));
				setmet.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
				setmet.setSupressFlags(rs.getString("EMAIL_SUPPR_FLAG"));
				setmet.setClientName(rs.getString("CLIENT_NAME"));
				
				Vector vStates = getSetMetActionStates(con,setmet.getProjectID(),setmet.getSetMetID());
				setmet.setSetMetStates(vStates);
				
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
		
		return setmet;
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

	public static Vector getOpenSetMets(Connection con, String sProjectId) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vSetMets = new Vector();
		
		try {
			
			sQuery.append("SELECT QBR_ID,PROJECT_ID,QBR_NAME,CLIENT_IR_ID,ETS_PRACTICE,ETS_BSE,MEETING_DATE,NEXT_MEETING_DATE,STATE,INTERVIEW_BY,LAST_TIMESTAMP,EMAIL_SUPPR_FLAG, CLIENT_NAME FROM ETS.ETS_QBR_MAIN WHERE PROJECT_ID = '" + sProjectId + "' AND STATE = '" + Defines.SETMET_OPEN + "' for READ ONLY");
			
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				ETSSetMet setmet = new ETSSetMet();
				
				setmet.setSetMetID(rs.getString("QBR_ID"));
				setmet.setProjectID(rs.getString("PROJECT_ID"));
				setmet.setSetMetName(rs.getString("QBR_NAME"));
				setmet.setClientIRID(rs.getString("CLIENT_IR_ID"));
				setmet.setSetMetPractice(rs.getString("ETS_PRACTICE"));
				setmet.setSetMetBSE(rs.getString("ETS_BSE"));
				setmet.setMeetingDate(rs.getTimestamp("MEETING_DATE"));
				setmet.setNextMeetingDate(rs.getTimestamp("NEXT_MEETING_DATE"));
				setmet.setState(rs.getString("STATE"));
				setmet.setInterviewByIRID(rs.getString("INTERVIEW_BY"));
				setmet.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
				setmet.setSupressFlags(rs.getString("EMAIL_SUPPR_FLAG"));
				setmet.setClientName(rs.getString("CLIENT_NAME"));
				
				if (setmet.getState().trim().equals(Defines.SETMET_OPEN)) {
					vSetMets.addElement(setmet);
				}
				
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
		
		return vSetMets;
	}

	public static Vector getClosedSetMets(Connection con, String sProjectId) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vSetMets = new Vector();
		
		try {

			sQuery.append("SELECT QBR_ID,PROJECT_ID,QBR_NAME,CLIENT_IR_ID,ETS_PRACTICE,ETS_BSE,MEETING_DATE,NEXT_MEETING_DATE,STATE,INTERVIEW_BY,LAST_TIMESTAMP,EMAIL_SUPPR_FLAG,CLIENT_NAME FROM ETS.ETS_QBR_MAIN WHERE PROJECT_ID = '" + sProjectId + "' AND STATE = '" + Defines.SETMET_CLOSED + "' for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				ETSSetMet setmet = new ETSSetMet();
				
				setmet.setSetMetID(rs.getString("QBR_ID"));
				setmet.setProjectID(rs.getString("PROJECT_ID"));
				setmet.setSetMetName(rs.getString("QBR_NAME"));
				setmet.setClientIRID(rs.getString("CLIENT_IR_ID"));
				setmet.setSetMetPractice(rs.getString("ETS_PRACTICE"));
				setmet.setSetMetBSE(rs.getString("ETS_BSE"));
				setmet.setMeetingDate(rs.getTimestamp("MEETING_DATE"));
				setmet.setNextMeetingDate(rs.getTimestamp("NEXT_MEETING_DATE"));
				setmet.setState(rs.getString("STATE"));
				setmet.setInterviewByIRID(rs.getString("INTERVIEW_BY"));
				setmet.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
				setmet.setSupressFlags(rs.getString("EMAIL_SUPPR_FLAG"));
				setmet.setClientName(rs.getString("CLIENT_NAME"));
				
				if (setmet.getState().trim().equals(Defines.SETMET_CLOSED)) {
					vSetMets.addElement(setmet);
				}
				
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
		
		return vSetMets;
	}
	

	/**
	 * @return
	 */
	public static Vector getQuestions(Connection con) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vQuestions = new Vector();
		
		try {
			
			sQuery.append("SELECT QUESTION_ID,QUESTION_TYPE,DESCRIPTION FROM ETS.ETS_QUESTION_DATA ORDER BY QUESTION_ID for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				ETSSetMetQuestion question = new ETSSetMetQuestion();
				
				question.setQuestionID(rs.getInt("QUESTION_ID"));
				question.setQuestionType(rs.getString("QUESTION_TYPE"));
				question.setQuestionDesc(rs.getString("DESCRIPTION"));
				
				vQuestions.addElement(question);
				
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
		
		return vQuestions;
		
	}



	/**
	 * @return
	 */
	public static Vector getExpectationCode(Connection con) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vExpect = new Vector();
		
		try {
			
			sQuery.append("SELECT EXPECT_ID,EXPECT_CODE,DESCRIPTION FROM ETS.ETS_EXP_DATA ORDER BY EXPECT_ID for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				ETSSetMetExpectCode code = new ETSSetMetExpectCode();
				
				code.setExpectID(rs.getInt("EXPECT_ID"));
				code.setExpectCode(rs.getString("EXPECT_CODE"));
				code.setExpectDesc(rs.getString("DESCRIPTION"));
				
				vExpect.addElement(code);
				
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
		
		return vExpect;
		
	}

	/**
	 * @return
	 */
	public static Vector getSetMetActionStates(Connection con, String sProjectID, String sSetMetID) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vStates = new Vector();
		
		try {
			
			sQuery.append("SELECT QBR_ID,PROJECT_ID,STEP,ACTION_DATE,ACTION_BY_ID,LAST_TIMESTAMP FROM ETS.ETS_QBR_PLAN WHERE QBR_ID = '" + sSetMetID + "' AND PROJECT_ID = '" + sProjectID + "' ORDER BY ACTION_DATE for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				ETSSetMetActionState state = new ETSSetMetActionState();
				
				state.setSetMetID(rs.getString("QBR_ID"));
				state.setProjectID(rs.getString("PROJECT_ID"));
				state.setStep(rs.getString("STEP"));
				state.setActionDate(rs.getTimestamp("ACTION_DATE"));
				state.setActionBy(rs.getString("ACTION_BY_ID"));
				state.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
				
				
				vStates.addElement(state);
				
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
		
		return vStates;
		
	}

	/**
	 * @return
	 */
	public static Vector getSetMetExpectations(Connection con, String sProjectID, String sSetMetID) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vExpectations = new Vector();
		
		try {
			
			sQuery.append("SELECT QBR_ID,PROJECT_ID,QUESTION_ID,SEQ_NO,EXPECT_DESCRIPTION,EXPECT_RATING,EXPECT_ID,EXP_ACTION,FINAL_RATING,COMMENTS,LAST_USERID,LAST_TIMESTAMP FROM ETS.ETS_QBR_EXP WHERE PROJECT_ID = '" + sProjectID + "' AND QBR_ID = '" + sSetMetID + "' ORDER BY QUESTION_ID, SEQ_NO for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				ETSSetMetExpectation exp = new ETSSetMetExpectation();
				
				exp.setSetMetID(rs.getString("QBR_ID"));
				exp.setProjectID(rs.getString("PROJECT_ID"));
				exp.setQuestionID(rs.getInt("QUESTION_ID"));
				exp.setSeqNo(rs.getInt("SEQ_NO"));
				exp.setExpectDesc(rs.getString("EXPECT_DESCRIPTION"));
				exp.setExpectRating(rs.getDouble("EXPECT_RATING"));
				exp.setExpectID(rs.getInt("EXPECT_ID"));
				exp.setExpectAction(rs.getString("EXP_ACTION"));
				exp.setFinalRating(rs.getDouble("FINAL_RATING"));
				exp.setComments(rs.getString("COMMENTS"));
				exp.setLastUserID(rs.getString("LAST_USERID"));
				exp.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
				
				vExpectations.addElement(exp);
				
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
		
		return vExpectations;
		
	}

	/**
	 * @return
	 */
	public static ETSSetMetExpectation getSetMetExpectation(Connection con, String sProjectID, String sSetMetID, int QuestionID, int SeqNo) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		ETSSetMetExpectation exp = new ETSSetMetExpectation();
		
		try {
			
			sQuery.append("SELECT QBR_ID,PROJECT_ID,QUESTION_ID,SEQ_NO,EXPECT_DESCRIPTION,EXPECT_RATING,EXPECT_ID,EXP_ACTION,FINAL_RATING,COMMENTS,LAST_USERID,LAST_TIMESTAMP FROM ETS.ETS_QBR_EXP WHERE PROJECT_ID = '" + sProjectID + "' AND QBR_ID = '" + sSetMetID + "' AND QUESTION_ID = " + QuestionID + " AND SEQ_NO = " + SeqNo + " ORDER BY QUESTION_ID, SEQ_NO for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			if (rs.next()) {
				
				exp.setSetMetID(rs.getString("QBR_ID"));
				exp.setProjectID(rs.getString("PROJECT_ID"));
				exp.setQuestionID(rs.getInt("QUESTION_ID"));
				exp.setSeqNo(rs.getInt("SEQ_NO"));
				exp.setExpectDesc(rs.getString("EXPECT_DESCRIPTION"));
				exp.setExpectRating(rs.getDouble("EXPECT_RATING"));
				exp.setExpectID(rs.getInt("EXPECT_ID"));
				exp.setExpectAction(rs.getString("EXP_ACTION"));
				exp.setFinalRating(rs.getDouble("FINAL_RATING"));
				exp.setComments(rs.getString("COMMENTS"));
				exp.setLastUserID(rs.getString("LAST_USERID"));
				exp.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
				
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
		
		return exp;
		
	}

	/**
	 * @return
	 */
	public static int insertSetMetExpectation(Connection con, ETSSetMetExpectation exp) throws SQLException, Exception {
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {

			sQuery.append("INSERT INTO ETS.ETS_QBR_EXP (QBR_ID,PROJECT_ID,QUESTION_ID,SEQ_NO,EXPECT_DESCRIPTION,EXPECT_RATING,EXPECT_ID,EXP_ACTION,FINAL_RATING,COMMENTS,LAST_USERID,LAST_TIMESTAMP) VALUES (");
			sQuery.append("?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.setString(1,exp.getSetMetID());
			pstmt.setString(2,exp.getProjectID());
			pstmt.setInt(3,exp.getQuestionID());
			pstmt.setInt(4,exp.getSeqNo());
			pstmt.setString(5,ETSUtils.escapeString(exp.getExpectDesc()));
			pstmt.setDouble(6,exp.getExpectRating());
			pstmt.setInt(7,exp.getExpectID());
			pstmt.setString(8,ETSUtils.escapeString(exp.getExpectAction()));
			pstmt.setDouble(9,exp.getFinalRating());
			pstmt.setString(10,ETSUtils.escapeString(exp.getComments()));
			pstmt.setString(11,exp.getLastUserID());
			pstmt.setTimestamp(12,new Timestamp(System.currentTimeMillis()));
			
			
			iCount = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
		
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static int insertApproval(Connection con, ETSSetMetActionState state) throws SQLException, Exception {
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
	
			sQuery.append("INSERT INTO ETS.ETS_QBR_PLAN (QBR_ID,PROJECT_ID,STEP,ACTION_DATE,ACTION_BY_ID,LAST_TIMESTAMP) VALUES (");
			sQuery.append("?,?,?,?,?,?)");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.setString(1,state.getSetMetID());
			pstmt.setString(2,state.getProjectID());
			pstmt.setString(3,state.getStep());
			pstmt.setTimestamp(4,state.getActionDate());
			pstmt.setString(5,state.getActionBy());
			pstmt.setTimestamp(6,state.getLastTimestamp());
			
			iCount = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
		
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static int closeSetMet(Connection con, String sProjectID, String sSetMetID) throws SQLException, Exception {
		
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
	
			sQuery.append("UPDATE ETS.ETS_QBR_MAIN SET STATE = '" + Defines.SETMET_CLOSED + "',LAST_TIMESTAMP = current timestamp WHERE PROJECT_ID = '" + sProjectID + "' AND QBR_ID = '" + sSetMetID + "'");

			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());			
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static int updateSetMetExpectation(Connection con, ETSSetMetExpectation exp) throws SQLException, Exception {
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {

			sQuery.append("UPDATE ETS.ETS_QBR_EXP SET ");
			sQuery.append("EXPECT_DESCRIPTION = ?, ");
			sQuery.append("EXPECT_RATING = ?, ");
			sQuery.append("EXPECT_ID = ?, ");
			sQuery.append("COMMENTS = ?, ");
			sQuery.append("EXP_ACTION = ?, ");
			sQuery.append("FINAL_RATING = ?, ");
			sQuery.append("LAST_USERID = ?, ");
			sQuery.append("LAST_TIMESTAMP = ? ");
			sQuery.append("WHERE ");
			sQuery.append("QBR_ID = ? AND ");
			sQuery.append("PROJECT_ID = ? AND ");
			sQuery.append("QUESTION_ID = ? AND ");
			sQuery.append("SEQ_NO = ?");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.setString(1,ETSUtils.escapeString(exp.getExpectDesc()));
			pstmt.setDouble(2,exp.getExpectRating());
			pstmt.setInt(3,exp.getExpectID());
			pstmt.setString(4,ETSUtils.escapeString(exp.getComments()));
			pstmt.setString(5,ETSUtils.escapeString(exp.getExpectAction()));
			pstmt.setDouble(6,exp.getFinalRating());
			pstmt.setString(7,exp.getLastUserID());
			pstmt.setTimestamp(8,new Timestamp(System.currentTimeMillis()));
			pstmt.setString(9,exp.getSetMetID());
			pstmt.setString(10,exp.getProjectID());
			pstmt.setInt(11,exp.getQuestionID());
			pstmt.setInt(12,exp.getSeqNo());
					
			
			iCount = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
		
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static int updateSetMetInitialRating(Connection con, String sSetMetID, String sProjectID, int iQuestionNo, int iSeqNo, double dRating, String sLastUserID) throws SQLException, Exception {
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
	
			sQuery.append("UPDATE ETS.ETS_QBR_EXP SET ");
			sQuery.append("EXPECT_RATING = ?, ");
			sQuery.append("LAST_USERID = ?, ");
			sQuery.append("LAST_TIMESTAMP = ? ");
			sQuery.append("WHERE ");
			sQuery.append("QBR_ID = ? AND ");
			sQuery.append("PROJECT_ID = ? AND ");
			sQuery.append("QUESTION_ID = ? AND ");
			sQuery.append("SEQ_NO = ?");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.setDouble(1,dRating);
			pstmt.setString(2,sLastUserID);
			pstmt.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
			pstmt.setString(4,sSetMetID);
			pstmt.setString(5,sProjectID);
			pstmt.setInt(6,iQuestionNo);
			pstmt.setInt(7,iSeqNo);
					
			iCount = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
		
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static int updateSetMetFinalRating(Connection con, String sSetMetID, String sProjectID, int iQuestionNo, int iSeqNo, double dRating, String sLastUserID) throws SQLException, Exception {
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
	
			sQuery.append("UPDATE ETS.ETS_QBR_EXP SET ");
			sQuery.append("FINAL_RATING = ?, ");
			sQuery.append("LAST_USERID = ?, ");
			sQuery.append("LAST_TIMESTAMP = ? ");
			sQuery.append("WHERE ");
			sQuery.append("QBR_ID = ? AND ");
			sQuery.append("PROJECT_ID = ? AND ");
			sQuery.append("QUESTION_ID = ? AND ");
			sQuery.append("SEQ_NO = ?");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.setDouble(1,dRating);
			pstmt.setString(2,sLastUserID);
			pstmt.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
			pstmt.setString(4,sSetMetID);
			pstmt.setString(5,sProjectID);
			pstmt.setInt(6,iQuestionNo);
			pstmt.setInt(7,iSeqNo);
					
			iCount = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
		
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static int deleteSetMetExpectation(Connection con, ETSSetMetExpectation exp) throws SQLException, Exception {
		
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
			
			sQuery.append("DELETE FROM ETS.ETS_QBR_EXP ");
			sQuery.append("WHERE ");
			sQuery.append("QBR_ID = '" + exp.getSetMetID() + "' AND ");
			sQuery.append("PROJECT_ID = '" + exp.getProjectID() + "' AND ");
			sQuery.append("QUESTION_ID = " + exp.getQuestionID() + " AND ");
			sQuery.append("SEQ_NO = " + exp.getSeqNo());
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static int getNextSeqNoForExpectation(Connection con, String sProjectID, String sSetMetID, int iQuestionID) throws SQLException, Exception {
		
		ResultSet rs = null;
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 1;
		
		try {
			
			sQuery.append("SELECT MAX(SEQ_NO) + 1 FROM ETS.ETS_QBR_EXP WHERE QBR_ID = '" + sSetMetID + "' AND PROJECT_ID = '" + sProjectID + "' AND QUESTION_ID = " + iQuestionID + " for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			if (rs.next()) {
				iCount = rs.getInt(1);
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
		
		return iCount;
		
	}
	
	public static ETSSetMet getSecondSetMet(Connection con, String sProjectId) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sQuery = new StringBuffer("");
		ETSSetMet setmet = new ETSSetMet();
		
		try {
			
			// display all setmets for all members... changed in 5.2.1
			sQuery.append("SELECT QBR_ID,PROJECT_ID,QBR_NAME,CLIENT_IR_ID,ETS_PRACTICE,ETS_BSE,MEETING_DATE,NEXT_MEETING_DATE,STATE,INTERVIEW_BY,LAST_TIMESTAMP,EMAIL_SUPPR_FLAG, CLIENT_NAME FROM ETS.ETS_QBR_MAIN WHERE ");
			sQuery.append("PROJECT_ID = '" + sProjectId + "' ORDER BY MEETING_DATE DESC for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			int iCount = 1;
			
			while (rs.next()) {
				
				if (iCount == 2) {
					
					// ignore the first one as we need the second one.
					
					setmet.setSetMetID(rs.getString("QBR_ID"));
					setmet.setProjectID(rs.getString("PROJECT_ID"));
					setmet.setSetMetName(rs.getString("QBR_NAME"));
					setmet.setClientIRID(rs.getString("CLIENT_IR_ID"));
					setmet.setSetMetPractice(rs.getString("ETS_PRACTICE"));
					setmet.setSetMetBSE(rs.getString("ETS_BSE"));
					setmet.setMeetingDate(rs.getTimestamp("MEETING_DATE"));
					setmet.setNextMeetingDate(rs.getTimestamp("NEXT_MEETING_DATE"));
					setmet.setState(rs.getString("STATE"));
					setmet.setInterviewByIRID(rs.getString("INTERVIEW_BY"));
					setmet.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
					setmet.setSupressFlags(rs.getString("EMAIL_SUPPR_FLAG"));
					setmet.setClientName(rs.getString("CLIENT_NAME"));
					
					Vector vStates = getSetMetActionStates(con,setmet.getProjectID(),setmet.getSetMetID());
					setmet.setSetMetStates(vStates);
					
					break;
				}
				
				iCount = iCount + 1;				
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
		
		return setmet;
	}
	
	/**
	 * @return
	 */
	public static int insertNewSetMetDemographics(Connection con, ETSSetMet setmet) throws SQLException, Exception {
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
	
			sQuery.append("INSERT INTO ETS.ETS_QBR_MAIN (QBR_ID,PROJECT_ID,QBR_NAME,CLIENT_IR_ID,ETS_PRACTICE,ETS_BSE,MEETING_DATE,NEXT_MEETING_DATE,STATE,INTERVIEW_BY,LAST_TIMESTAMP,EMAIL_SUPPR_FLAG,CLIENT_NAME) VALUES (");
			sQuery.append("?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.setString(1,setmet.getSetMetID());  
			pstmt.setString(2,setmet.getProjectID());
			pstmt.setString(3,setmet.getSetMetName());
			pstmt.setString(4,setmet.getClientIRID());
			pstmt.setString(5,setmet.getSetMetPractice());
			pstmt.setString(6,setmet.getSetMetBSE());
			pstmt.setTimestamp(7,setmet.getMeetingDate());
			pstmt.setTimestamp(8,setmet.getNextMeetingDate());
			pstmt.setString(9,setmet.getState());
			pstmt.setString(10,setmet.getInterviewByIRID());
			pstmt.setTimestamp(11,setmet.getLastTimestamp());
			pstmt.setString(12,setmet.getSupressFlags());
			pstmt.setString(13,setmet.getClientName());
			
			iCount = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
		
		return iCount;
		
	}

	/**
	 * @param con
	 * @param sSetMetID
	 * @param sProjectID
	 * @param sStep
	 * @param tDueDate
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public static int insertSetMetNofification(Connection con,String sSetMetID, String sProjectID, String sStep, Timestamp tDueDate) throws SQLException, Exception{
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
		
			sQuery.append("INSERT INTO ETS.ETS_QBR_NOTIFY (QBR_ID,PROJECT_ID,STEP,DUE_DATE,IS_NOTIFIED,LAST_TIMESTAMP) VALUES ");
			sQuery.append("(?,?,?,?,?,?)");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.clearParameters();
			
			pstmt.setString(1,sSetMetID);  
			pstmt.setString(2,sProjectID);
			pstmt.setString(3,sStep);
			pstmt.setTimestamp(4,tDueDate);
			pstmt.setString(5,"N");
			pstmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));
		
			iCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
			
		return iCount;
		
	}

	/**
	 * @param con
	 * @param sSetMetID
	 * @param sProjectID
	 * @param sStep
	 * @param tDueDate
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	
	public static int updateSetMetNofification(Connection con,String sSetMetID, String sProjectID, String sStep, Timestamp tDueDate) throws SQLException, Exception{
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
		
			sQuery.append("UPDATE ETS.ETS_QBR_NOTIFY SET DUE_DATE = ?, IS_NOTIFIED = ?,LAST_TIMESTAMP = ? WHERE ");
			sQuery.append("QBR_ID = ? AND PROJECT_ID = ? AND STEP = ?");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.clearParameters();
			
			pstmt.setTimestamp(1,tDueDate);
			pstmt.setString(2,"N");
			pstmt.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
			pstmt.setString(4,sSetMetID);  
			pstmt.setString(5,sProjectID);
			pstmt.setString(6,sStep);
			
			iCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
			
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static int updateExpectationComments(Connection con, ETSSetMetExpectation exp) throws SQLException, Exception {
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
	
			sQuery.append("UPDATE ETS.ETS_QBR_EXP SET ");
			sQuery.append("COMMENTS = ? ");
			sQuery.append("WHERE ");
			sQuery.append("QBR_ID = ? AND ");
			sQuery.append("PROJECT_ID = ? AND ");
			sQuery.append("QUESTION_ID = ? AND ");
			sQuery.append("SEQ_NO = ?");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.setString(1,ETSUtils.escapeString(exp.getComments()));
			pstmt.setString(2,exp.getSetMetID());
			pstmt.setString(3,exp.getProjectID());
			pstmt.setInt(4,exp.getQuestionID());
			pstmt.setInt(5,exp.getSeqNo());
					
			
			iCount = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
		
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static int updateSetMetDemographics(Connection con, ETSSetMet setmet) throws SQLException, Exception {
		
		PreparedStatement pstmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;
		
		try {
	
			sQuery.append("UPDATE ETS.ETS_QBR_MAIN ");
			sQuery.append("SET CLIENT_IR_ID = ?, ");
			sQuery.append("CLIENT_NAME = ?, ");
			sQuery.append("ETS_PRACTICE = ?, ");
			sQuery.append("ETS_BSE = ?, ");
			sQuery.append("EMAIL_SUPPR_FLAG = ? WHERE ");
			sQuery.append("QBR_ID = ? AND ");
			sQuery.append("PROJECT_ID = ?");
			
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.setString(1,setmet.getClientIRID());
			pstmt.setString(2,setmet.getClientName());
			pstmt.setString(3,setmet.getSetMetPractice());
			pstmt.setString(4,setmet.getSetMetBSE());
			pstmt.setString(5,setmet.getSupressFlags());
			pstmt.setString(6,setmet.getSetMetID());
			pstmt.setString(7,setmet.getProjectID());
			
			iCount = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
		
		return iCount;
		
	}

	/**
	 * @return
	 */
	public static Vector getSetMetNotifications(Connection con, String sProjectId, String sSetMetID) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vNotifications = new Vector();
		
		try {
			
			sQuery.append("SELECT QBR_ID,PROJECT_ID,STEP,DUE_DATE,IS_NOTIFIED,LAST_TIMESTAMP FROM ETS.ETS_QBR_NOTIFY WHERE QBR_ID = '" + sSetMetID + "' AND PROJECT_ID = '" + sProjectId + "' for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				ETSSetMetNotify notify = new ETSSetMetNotify();
				
				notify.setSetMetID(rs.getString("QBR_ID"));
				notify.setProjectID(rs.getString("PROJECT_ID"));
				notify.setStep(rs.getString("STEP"));
				notify.setDueDate(rs.getTimestamp("DUE_DATE"));
				notify.setNotified(rs.getString("IS_NOTIFIED"));
				notify.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
				
				vNotifications.addElement(notify);
				
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
		
		return vNotifications;
		
	}

	/**
	 * @return
	 */
	public static ETSSetMetQuestion getQuestion(Connection con, int iID) throws SQLException, Exception {
		
		ETSSetMetQuestion question = new ETSSetMetQuestion();
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vQuestions = new Vector();
		
		try {
			
			sQuery.append("SELECT QUESTION_ID,QUESTION_TYPE,DESCRIPTION FROM ETS.ETS_QUESTION_DATA WHERE QUESTION_ID = " + iID + " for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				question.setQuestionID(rs.getInt("QUESTION_ID"));
				question.setQuestionType(rs.getString("QUESTION_TYPE"));
				question.setQuestionDesc(rs.getString("DESCRIPTION"));
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
		
		return question;
		
	}
	
}
