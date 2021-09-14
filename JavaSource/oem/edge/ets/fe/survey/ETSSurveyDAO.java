/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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
 * Created on Sep 23, 2005
 * @author v2sathis@us.ibm.com
 */
 
package oem.edge.ets.fe.survey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Vector;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 */
public class ETSSurveyDAO {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2005";
	private static final String CLASS_VERSION = "1.2";

	private static Log logger = EtsLogger.getLogger(ETSSurvey.class);	
	
	public static Vector getSurveysForCompany(Connection con, String sCompany, String sOpenClosedType) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs1 = null;
	
		StringBuffer sQuery = new StringBuffer("");
		StringBuffer sQuery1 = new StringBuffer("");
		
		Vector vAllSurveys = new Vector();
		
		
		try {
			
			sQuery1.append("SELECT SURVEY_YEAR,RESPONSE_ID,SURVEY_KEY,SURVEY_VALUE,LAST_TIMESTAMP FROM ETS.ETS_SURVEY_DATA A ");
			sQuery1.append("WHERE SURVEY_YEAR = ? AND RESPONSE_ID = ? FOR READ ONLY");
			
			sQuery.append("SELECT A.SURVEY_YEAR,A.RESPONSE_ID,B.STATUS ");
			sQuery.append("FROM ETS.ETS_SURVEY_DATA A LEFT OUTER JOIN ETS.ETS_SURVEY_STATUS B ");
			sQuery.append("ON A.SURVEY_YEAR = B.SURVEY_YEAR AND A.SURVEY_VALUE = B.COMPANY ");
			sQuery.append("WHERE SURVEY_KEY = '" + ETSSurveyConstants.COMPANY_FIELD + "' AND ");
			sQuery.append("LTRIM(RTRIM(SURVEY_VALUE)) = '" + sCompany + "'");
			
			logger.debug("ETSSurveyDAO::getSurveysForCompany()::Query :" + sQuery.toString());
			
			pstmt = con.prepareStatement(sQuery1.toString());
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				String sYear = rs.getString("SURVEY_YEAR");
				String sResponseId = rs.getString("RESPONSE_ID");
				String sStatus = ETSUtils.checkNull(rs.getString("STATUS"));
				
				boolean bToAdd = false;
				
				if (sOpenClosedType.equalsIgnoreCase("C")) {
					if (sStatus.equalsIgnoreCase(ETSSurveyConstants.SURVEY_CLOSED)) {
						bToAdd = true;
					}
				} else {
					if (sStatus.equalsIgnoreCase(ETSSurveyConstants.SURVEY_OPEN) || sStatus.equalsIgnoreCase("")) {
						bToAdd = true;
					}
				}
				
				if (bToAdd) {
					
					pstmt.clearParameters();
					pstmt.setString(1,sYear);
					pstmt.setString(2,sResponseId);
					
					rs1 = pstmt.executeQuery();
					
					Vector vSurvey = new Vector();
					while (rs1.next()) {
						
						ETSSurveyData data = new ETSSurveyData();
						data.setYear(sYear);
						data.setResponseId(sResponseId);
						data.setSurveyKey(rs1.getString("SURVEY_KEY"));
						data.setSurveyValue(rs1.getString("SURVEY_VALUE"));
						data.setLastTimestamp(rs1.getTimestamp("LAST_TIMESTAMP"));
						
						vSurvey.addElement(data);
					}
					
					if (vSurvey != null) {
						ETSSurvey survey = new ETSSurvey();
						survey.setSurveyData(vSurvey);
						survey.setStatus(sOpenClosedType);
						survey.setResponseID(sResponseId);
						survey.setYear(sYear);
						vAllSurveys.addElement(survey);
					}
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
			ETSDBUtils.close(rs1);
			ETSDBUtils.close(pstmt);
			
		}
		
		return vAllSurveys;
		
		
	}

	public static ETSSurvey getSurveyData(Connection con, String sYear,String sResponseId) throws SQLException, Exception {
	
		Statement stmt = null;
		ResultSet rs = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs1 = null;
	
		StringBuffer sQuery = new StringBuffer("");
		StringBuffer sQuery1 = new StringBuffer("");
		
		ETSSurvey survey = new ETSSurvey();

		try {
			
			sQuery1.append("SELECT SURVEY_YEAR,RESPONSE_ID,SURVEY_KEY,SURVEY_VALUE,LAST_TIMESTAMP FROM ETS.ETS_SURVEY_DATA ");
			sQuery1.append("WHERE SURVEY_YEAR = ? AND RESPONSE_ID = ? FOR READ ONLY");
			
			pstmt = con.prepareStatement(sQuery1.toString());
			
			pstmt.clearParameters();
			pstmt.setString(1,sYear);
			pstmt.setString(2,sResponseId);
				
			rs1 = pstmt.executeQuery();
			
			Vector vSurvey = new Vector();
				
			while (rs1.next()) {
				
				ETSSurveyData data = new ETSSurveyData();
				data.setYear(sYear);
				data.setResponseId(sResponseId);
				data.setSurveyKey(rs1.getString("SURVEY_KEY"));
				data.setSurveyValue(rs1.getString("SURVEY_VALUE"));
				data.setLastTimestamp(rs1.getTimestamp("LAST_TIMESTAMP"));
				
				vSurvey.addElement(data);
			}
				
			if (vSurvey != null) {
				survey.setSurveyData(vSurvey);
				survey.setYear(sYear);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(rs1);
			ETSDBUtils.close(pstmt);
			
		}
		
		return survey;
		
		
	}
	/**
	 * @param con
	 * @param sYear
	 * @param sResponseId
	 * @return
	 */
	public static ETSSurveyActionPlan getSurveyActionPlan(Connection con, String sYear, String sCompany) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		
		ETSSurveyActionPlan plan = new ETSSurveyActionPlan();
		
		try {
			
			sQuery.append("SELECT SURVEY_YEAR,COMPANY,SURVEY_ID,STATUS,PLAN_OWNER,PLAN_DUE_DATE,LAST_USERID,LAST_TIMESTAMP FROM ETS.ETS_SURVEY_STATUS ");
			sQuery.append("WHERE SURVEY_YEAR = '" + sYear + "' AND COMPANY = '" + sCompany + "' for READ ONLY ");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			if (rs.next()) {
				plan.setYear(sYear);
				plan.setCompany(sCompany);
				plan.setSurveyId(ETSUtils.checkNull(rs.getString("SURVEY_ID")));
				plan.setStatus(ETSUtils.checkNull(rs.getString("STATUS")));
				plan.setPlanOwnerId(ETSUtils.checkNull(rs.getString("PLAN_OWNER")));
				plan.setPlanDueDate(rs.getTimestamp("PLAN_DUE_DATE"));
				plan.setLastUserId(ETSUtils.checkNull(rs.getString("LAST_USERID")));
				plan.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
			} else {
				plan.setYear(sYear);
				plan.setCompany(sCompany);
				plan.setSurveyId(getNewSurveyId());
				plan.setStatus(ETSSurveyConstants.SURVEY_OPEN);
				plan.setPlanOwnerId("");
				plan.setPlanDueDate(null);
				plan.setLastUserId("");
				plan.setLastTimestamp(null);
				
				createActionPlan(con,plan);
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
		
		return plan;
	}

	/**
	 * @param con
	 * @param sYear
	 * @param sResponseId
	 * @return
	 */
	public static ETSSurveyActionPlan getSurveyActionPlanFromSurveyID(Connection con, String sSurveyID) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		
		ETSSurveyActionPlan plan = new ETSSurveyActionPlan();
		
		try {
			
			sQuery.append("SELECT SURVEY_YEAR,COMPANY,SURVEY_ID,STATUS,PLAN_OWNER,PLAN_DUE_DATE,LAST_USERID,LAST_TIMESTAMP FROM ETS.ETS_SURVEY_STATUS ");
			sQuery.append("WHERE SURVEY_ID = '" + sSurveyID + "' for READ ONLY ");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			if (rs.next()) {
				plan.setYear(ETSUtils.checkNull(rs.getString("SURVEY_YEAR")));
				plan.setCompany(ETSUtils.checkNull(rs.getString("COMPANY")));
				plan.setSurveyId(ETSUtils.checkNull(rs.getString("SURVEY_ID")));
				plan.setStatus(ETSUtils.checkNull(rs.getString("STATUS")));
				plan.setPlanOwnerId(ETSUtils.checkNull(rs.getString("PLAN_OWNER")));
				plan.setPlanDueDate(rs.getTimestamp("PLAN_DUE_DATE"));
				plan.setLastUserId(ETSUtils.checkNull(rs.getString("LAST_USERID")));
				plan.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
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
		
		return plan;
	}
	/**
	 * @param plan
	 */
	public static boolean createActionPlan(Connection con, ETSSurveyActionPlan plan) throws SQLException, Exception {
		
		boolean bCreated = false;
		
		PreparedStatement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		
		try {
			
			sQuery.append("INSERT INTO ETS.ETS_SURVEY_STATUS (SURVEY_YEAR,COMPANY,SURVEY_ID,STATUS,PLAN_OWNER,PLAN_DUE_DATE,LAST_USERID,LAST_TIMESTAMP) VALUES ");
			sQuery.append("(?,?,?,?,?,?,?,?)");
			
			stmt = con.prepareStatement(sQuery.toString());
			
			stmt.clearParameters();
			stmt.setString(1,plan.getYear());
			stmt.setString(2,plan.getCompany());
			stmt.setString(3,plan.getSurveyId());
			stmt.setString(4,plan.getStatus());
			stmt.setString(5,plan.getPlanOwnerId());
			stmt.setTimestamp(6,plan.getPlanDueDate());
			stmt.setString(7,plan.getLastUserId());
			stmt.setTimestamp(8,new Timestamp(System.currentTimeMillis()));
			
			int iCount =  stmt.executeUpdate();
			
			if (iCount > 0) {
				bCreated = true;
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
		
		
		
		return bCreated;
	}

	/**
	 * @param con
	 * @param sYear
	 * @param sResponseId
	 * @return
	 */
	public static Vector getSurveyQuestions(Connection con, String sYear) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vQuestions = new Vector();
		
		try {
			
			sQuery.append("SELECT YEAR, QUESTION_ID, DESCRIPTION FROM ETS.ETS_SURVEY_QUES ");
			sQuery.append("WHERE YEAR = '" + sYear + "' for READ ONLY ");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				String Year = ETSUtils.checkNull(rs.getString("YEAR"));
				String QuestionId = ETSUtils.checkNull(rs.getString("QUESTION_ID"));
				String QuestionText = ETSUtils.checkNull(rs.getString("DESCRIPTION"));
				
				ETSSurveyQuestion question = new ETSSurveyQuestion();
				question.setYear(Year);
				question.setQuestionId(QuestionId);
				question.setQuestionText(QuestionText);
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
	 * @param con
	 * @param sYear
	 * @return
	 */
	public static Vector getSurveyMappingData(Connection con, String sYear) throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vMapping = new Vector();
		
		try {
			
			sQuery.append("SELECT YEAR, SEQ_NO, QUESTION_ID, QUESTION_GROUP, RESPONSE_TYPE, RESPONSE_OTHER, MAPPING_QUERY FROM ETS.ETS_SURVEY_MAPPING ");
			sQuery.append("WHERE YEAR = '" + sYear + "' ORDER BY SEQ_NO for READ ONLY ");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				ETSSurveyMapping mapping = new ETSSurveyMapping();
				mapping.setYear(ETSUtils.checkNull(rs.getString("YEAR")));
				mapping.setSeqNo(rs.getInt("SEQ_NO"));
				mapping.setQuestionId(ETSUtils.checkNull(rs.getString("QUESTION_ID")));
				mapping.setQuestionGroup(ETSUtils.checkNull(rs.getString("QUESTION_GROUP")));
				mapping.setResponseType(ETSUtils.checkNull(rs.getString("RESPONSE_TYPE")));
				mapping.setResponseOther(ETSUtils.checkNull(rs.getString("RESPONSE_OTHER")));
				mapping.setMappingQuery(ETSUtils.checkNull(rs.getString("MAPPING_QUERY")));
				
				vMapping.addElement(mapping);
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
		
		return vMapping;
	}

	/**
	 * @param con
	 * @param sYear
	 * @return
	 */
	public static Vector getSurveyReferenceData(Connection con, String sYear) throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vReference = new Vector();
		
		try {
			
			sQuery.append("SELECT YEAR, REFERENCE, KEY, DESCRIPTION FROM ETS.ETS_SURVEY_REF ");
			sQuery.append("WHERE YEAR = '" + sYear + "' for READ ONLY ");
			
			stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				ETSSurveyReference ref = new ETSSurveyReference();
				ref.setYear(ETSUtils.checkNull(rs.getString("YEAR")));
				ref.setReference(ETSUtils.checkNull(rs.getString("REFERENCE")));
				ref.setKey(ETSUtils.checkNull(rs.getString("KEY")));
				ref.setDescription(ETSUtils.checkNull(rs.getString("DESCRIPTION")));
				
				vReference.addElement(ref);
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
		
		return vReference;

	}

	/**
	 * @param con
	 * @param sMappingSQL
	 * @return
	 */
	public static String executeSQL(Connection con, String sMappingSQL) throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		
		String sValue = "";
		
		try {
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sMappingSQL);
			
			if (rs.next()) {
				sValue = rs.getString(1);
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
		
		return sValue;
	}

	private static synchronized String getNewSurveyId() throws SQLException, Exception {

		Random rand = new Random();

		String sUniqueId = "";

		Long lDate = new Long(System.currentTimeMillis());
		sUniqueId = lDate + "-" + rand.nextInt(1000) + "";

		return sUniqueId;
	}
	
	/**
	 * @param plan
	 */
	public static boolean updateActionPlanOwner(Connection con, ETSSurveyActionPlan plan) throws SQLException, Exception {
		
		boolean bCreated = false;
		
		PreparedStatement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_SURVEY_STATUS SET ");
			sQuery.append("PLAN_OWNER = ?,");
			sQuery.append("PLAN_DUE_DATE = ?,");
			sQuery.append("LAST_USERID = ?,");
			sQuery.append("LAST_TIMESTAMP = ? WHERE ");
			sQuery.append("SURVEY_YEAR = ? AND ");
			sQuery.append("COMPANY = ?");
			
			stmt = con.prepareStatement(sQuery.toString());
			
			stmt.clearParameters();
			stmt.setString(1,plan.getPlanOwnerId());
			stmt.setTimestamp(2,plan.getPlanDueDate());
			stmt.setString(3,plan.getLastUserId());
			stmt.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
			stmt.setString(5,plan.getYear());
			stmt.setString(6,plan.getCompany());
			
			int iCount =  stmt.executeUpdate();
			
			if (iCount > 0) {
				bCreated = true;
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
		
		return bCreated;
	}
	
	/**
	 * @param plan
	 */
	public static boolean updateActionPlanStatusBySurveyID(Connection con, ETSSurveyActionPlan plan) throws SQLException, Exception {
		
		boolean bCreated = false;
		
		PreparedStatement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_SURVEY_STATUS SET ");
			sQuery.append("STATUS = ?,");
			sQuery.append("LAST_USERID = ?,");
			sQuery.append("LAST_TIMESTAMP = ? WHERE ");
			sQuery.append("SURVEY_ID = ? ");
			
			stmt = con.prepareStatement(sQuery.toString());
			stmt.clearParameters();
			
			stmt.setString(1,plan.getStatus());
			stmt.setString(2,plan.getLastUserId());
			stmt.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
			stmt.setString(4,plan.getSurveyId());
			
			int iCount =  stmt.executeUpdate();
			
			if (iCount > 0) {
				bCreated = true;
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
		
		
		
		return bCreated;
	}

	public static Vector getLatestSurveysForCompany(Connection con, String sCompany) throws SQLException, Exception {
	
		Statement stmt = null;
		ResultSet rs = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs1 = null;
	
		StringBuffer sQuery = new StringBuffer("");
		StringBuffer sQuery1 = new StringBuffer("");
		
		Vector vAllSurveys = new Vector();
		
		
		try {
			
			sQuery1.append("SELECT SURVEY_YEAR,RESPONSE_ID,SURVEY_KEY,SURVEY_VALUE,LAST_TIMESTAMP FROM ETS.ETS_SURVEY_DATA A ");
			sQuery1.append("WHERE SURVEY_YEAR = ? AND RESPONSE_ID = ? FOR READ ONLY");
			
			sQuery.append("SELECT SURVEY_YEAR,RESPONSE_ID ");
			sQuery.append("FROM ETS.ETS_SURVEY_DATA ");
			sQuery.append("WHERE SURVEY_KEY = '" + ETSSurveyConstants.COMPANY_FIELD + "' AND ");
			sQuery.append("LTRIM(RTRIM(SURVEY_VALUE)) = '" + sCompany + "' ");
			sQuery.append("AND SURVEY_YEAR IN (");
			sQuery.append("SELECT SURVEY_YEAR  ");
			sQuery.append("FROM ETS.ETS_SURVEY_DATA ");
			sQuery.append("WHERE SURVEY_KEY = '" + ETSSurveyConstants.COMPANY_FIELD + "' AND ");
			sQuery.append("LTRIM(RTRIM(SURVEY_VALUE)) = '" + sCompany + "' ORDER BY SURVEY_YEAR DESC FETCH FIRST 1 ROWS ONLY) for READ ONLY");			
			
			logger.debug("ETSSurveyDAO::getLatestSurveysForCompany()::Query :" + sQuery.toString());
			
			pstmt = con.prepareStatement(sQuery1.toString());
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			String sLatestYear = "";
			
			while (rs.next()) {
				
				String sYear = rs.getString("SURVEY_YEAR");
				String sResponseId = rs.getString("RESPONSE_ID");
				
				boolean bToAdd = false;
				
				pstmt.clearParameters();
				pstmt.setString(1,sYear);
				pstmt.setString(2,sResponseId);
				
				rs1 = pstmt.executeQuery();
				
				Vector vSurvey = new Vector();
				while (rs1.next()) {
					
					ETSSurveyData data = new ETSSurveyData();
					data.setYear(sYear);
					data.setResponseId(sResponseId);
					data.setSurveyKey(rs1.getString("SURVEY_KEY"));
					data.setSurveyValue(rs1.getString("SURVEY_VALUE"));
					data.setLastTimestamp(rs1.getTimestamp("LAST_TIMESTAMP"));
					
					vSurvey.addElement(data);
				}
				
				if (vSurvey != null) {
					ETSSurvey survey = new ETSSurvey();
					survey.setSurveyData(vSurvey);
					// making status as open.. wont harm as status is not being used...
					survey.setStatus(ETSSurveyConstants.SURVEY_OPEN);		
					survey.setResponseID(sResponseId);
					survey.setYear(sYear);
					vAllSurveys.addElement(survey);
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
			ETSDBUtils.close(rs1);
			ETSDBUtils.close(pstmt);
			
		}
		
		return vAllSurveys;
		
		
	}
	
}
