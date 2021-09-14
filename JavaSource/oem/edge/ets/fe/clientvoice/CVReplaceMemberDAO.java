/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
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
 * Created on Mar 9, 2006
 * Created by v2sathis
 * 
 * @author v2sathis
 */
package oem.edge.ets.fe.clientvoice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oem.edge.ets.fe.ETSDBUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author v2sathis
 *
 */
public class CVReplaceMemberDAO {
	
	public static final String VERSION = "1.2";
	
	Connection con = null;
	static Log log = LogFactory.getLog(CVReplaceMemberDAO.class); 
	public static final String SETMET = "Set/Met";
	public static final String SELFASSESSMENT = "Self assessment";

	/**
	 * @return
	 * @throws Exception
	 */
	private Connection getConnection() throws Exception {
		try { 
			return ETSDBUtils.getConnection();
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(this,e);
			}
			
			throw new Exception("Unable to create a connection to database.",e);
		}
	}
	
	
	/**
	 * 
	 */
	private void cleanUp() {
		if (con != null) {
			ETSDBUtils.close(con);
		}
	}
	
	/**
	 * @param projectId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List getMemberAssignmentDetails(String projectId, String userId) throws Exception {
		
		List listMembers = new ArrayList();
		StringBuffer querySetMet = new StringBuffer("SELECT QBR_NAME, CLIENT_IR_ID, ETS_PRACTICE, ETS_BSE, INTERVIEW_BY FROM ETS.ETS_QBR_MAIN WHERE PROJECT_ID = '" + projectId + "' AND (CLIENT_IR_ID = '" + userId + "' OR ETS_PRACTICE = '" + userId + "' OR ETS_BSE = '" + userId + "' OR INTERVIEW_BY = '" + userId + "') for READ ONLY");
		StringBuffer querySelf = new StringBuffer("SELECT SELF_NAME, SELF_PM, SELF_PLAN_OWNER, (SELECT COUNT(MEMBER_IR_ID) FROM ETS.ETS_SELF_MEMBERS WHERE PROJECT_ID = '" + projectId + "' AND MEMBER_IR_ID = '" + userId + "') AS MEMBER_COUNT FROM ETS.ETS_SELF_MAIN WHERE PROJECT_ID = '" + projectId + "' AND (SELF_PM = '" + userId + "' OR SELF_PLAN_OWNER= '" + userId + "' OR '" + userId + "' IN (SELECT MEMBER_IR_ID FROM ETS.ETS_SELF_MEMBERS WHERE MEMBER_IR_ID = '" + userId + "' AND PROJECT_ID = '" + projectId + "')) for READ ONLY");
		
		Statement stmt = null;
		ResultSet rs = null;
		
		Statement stmtSelf = null;
		ResultSet rsSelf = null;

		try {
			
			con = getConnection();
			stmt = con.createStatement();
			
			if (log.isDebugEnabled()) {
				log.debug(querySetMet);
			}
			
			rs = stmt.executeQuery(querySetMet.toString());
			
			while (rs.next()) {
				
				String name = rs.getString("QBR_NAME");
				String type = SETMET;
				String step = "";

				if (rs.getString("INTERVIEW_BY").trim().equalsIgnoreCase(userId)) {
						step = "Initial interview";	
				}

				if (rs.getString("CLIENT_IR_ID").trim().equalsIgnoreCase(userId)) {
					if (step.equals("")) {
						step = "Client review/approve interview, Set/Met final rating";
					} else {
						step = step + ", Client review/approve interview, Set/Met final rating";
					}
				}
				
				if (rs.getString("ETS_BSE").trim().equalsIgnoreCase(userId)) {
					if (step.equals("")) {
						step = "Principal review and comments";	
					} else {
						step = step + ", Principal review and comments";
					}
				}
				
				if (rs.getString("ETS_PRACTICE").trim().equalsIgnoreCase(userId)) {
					if (step.equals("")) {
						step = "Action plan";	
					} else {
						step = step + ", Action plan";
					}
				}
				
				CVMemberDetail detail = new CVMemberDetail(name,type,step);
				listMembers.add(detail);
				
			}
			
			
			stmtSelf = con.createStatement();
			rsSelf = stmtSelf.executeQuery(querySelf.toString());
			
			if (log.isDebugEnabled()) {
				log.debug(querySelf);
			}			
			
			while (rsSelf.next()) {
				
				String name = rsSelf.getString("SELF_NAME");
				String type = SELFASSESSMENT;
				String step = "";

				if (rsSelf.getInt("MEMBER_COUNT") > 0) {
					step = "Assessment team Member";
				}

				if (rsSelf.getString("SELF_PM").equalsIgnoreCase(userId)) {
					if (step.equals("")) {
						step = "Compile assessment owner";
					} else {
						step = step + ", Compile assessment owner";
					}
				}
				if (rsSelf.getString("SELF_PLAN_OWNER").equalsIgnoreCase(userId)) {
					if (step.equals("")) {
						step = "Action plan owner";
					} else {
						step = step + ", Action plan owner";
					}
				}
				
				
				CVMemberDetail detail = new CVMemberDetail(name,type,step);
				listMembers.add(detail);
				
			}
		
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(rsSelf);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(stmtSelf);
			cleanUp();
		}
		return listMembers;

	}


	/**
	 * @param projectId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private void handleSelfAssessmentDeletes(String projectId, String oldUserId, String newUserId) throws Exception {
		
		List listSelf = new ArrayList();
		StringBuffer querySetMet = new StringBuffer("SELECT SELF_ID FROM ETS.ETS_SELF_MEMBERS WHERE PROJECT_ID = '" + projectId + "' AND MEMBER_IR_ID = '" + newUserId + "' AND SELF_ID IN (SELECT SELF_ID FROM ETS.ETS_SELF_MEMBERS WHERE PROJECT_ID = '" + projectId + "' AND MEMBER_IR_ID = '" + oldUserId + "') for READ ONLY");
		
		Statement stmt = null;
		ResultSet rs = null;
		
		Statement deleteStmt = null;
		
 
		try {
			
			con = getConnection();
			stmt = con.createStatement();
			
			if (log.isDebugEnabled()) {
				log.debug(querySetMet);
			}
			
			rs = stmt.executeQuery(querySetMet.toString());
			
			while (rs.next()) {
				// self assessments which has both old id and new id as members
				String selfId = rs.getString("SELF_ID");
				listSelf.add(selfId);
			}
			
			// for these self assessments, since the new member is already a member, assigning old member
			// will cause a primary key problem..so remove the old member in these self assessments
			
			if (listSelf != null && listSelf.size() > 0) {
				// remove the old user id
				// ets_self_exp
				// ets_self_members
				// ets_self_section
				
				for (Iterator iter = listSelf.iterator(); iter.hasNext(); ) {
					
					String selfId = (String) iter.next();
					
					deleteStmt = con.createStatement();
					
					deleteStmt.addBatch("DELETE FROM ETS.ETS_SELF_EXP WHERE SELF_ID = '" + selfId + "' AND PROJECT_ID = '" + projectId + "' AND MEMBER_IR_ID = '" + oldUserId + "'");
					deleteStmt.addBatch("DELETE FROM ETS.ETS_SELF_MEMBERS WHERE SELF_ID = '" + selfId + "' AND PROJECT_ID = '" + projectId + "' AND MEMBER_IR_ID = '" + oldUserId + "'");
					deleteStmt.addBatch("DELETE FROM ETS.ETS_SELF_SECTION WHERE SELF_ID = '" + selfId + "' AND PROJECT_ID = '" + projectId + "' AND MEMBER_IR_ID = '" + oldUserId + "'");
					
					deleteStmt.executeBatch();

					ETSDBUtils.close(deleteStmt);
				}
				
			}
		
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(deleteStmt);
			cleanUp();
		}
	}

	
	/**
	 * @param projectId
	 * @param oldUserId
	 * @param newUserId
	 * @return 
	 * @throws Exception
	 */
	public boolean replaceMember(String projectId, String oldUserId, String newUserId) throws Exception {
		
		boolean status = false;
		
		StringBuffer updateQuery = new StringBuffer("UPDATE ETS.ETS_QBR_MAIN SET CLIENT_IR_ID = '" + newUserId + "' WHERE CLIENT_IR_ID = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");
		StringBuffer updateQuery1 = new StringBuffer("UPDATE ETS.ETS_QBR_MAIN SET ETS_PRACTICE = '" + newUserId + "' WHERE ETS_PRACTICE = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");
		StringBuffer updateQuery2 = new StringBuffer("UPDATE ETS.ETS_QBR_MAIN SET ETS_BSE = '" + newUserId + "' WHERE ETS_BSE = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");
		StringBuffer updateQuery3 = new StringBuffer("UPDATE ETS.ETS_QBR_MAIN SET INTERVIEW_BY  = '" + newUserId + "' WHERE INTERVIEW_BY = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");
		StringBuffer updateQuery4 = new StringBuffer("UPDATE ETS.ETS_QBR_PLAN SET ACTION_BY_ID  = '" + newUserId + "' WHERE ACTION_BY_ID = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");

		StringBuffer updateSelfQuery = new StringBuffer("UPDATE ETS.ETS_SELF_MAIN SET SELF_PM  = '" + newUserId + "' WHERE SELF_PM = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");
		StringBuffer updateSelfQuery1 = new StringBuffer("UPDATE ETS.ETS_SELF_MAIN SET SELF_PLAN_OWNER = '" + newUserId + "' WHERE SELF_PLAN_OWNER = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");
		StringBuffer updateSelfQuery2 = new StringBuffer("UPDATE ETS.ETS_SELF_MEMBERS SET MEMBER_IR_ID  = '" + newUserId + "' WHERE MEMBER_IR_ID = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");
		StringBuffer updateSelfQuery3 = new StringBuffer("UPDATE ETS.ETS_SELF_EXP SET MEMBER_IR_ID  = '" + newUserId + "' WHERE MEMBER_IR_ID = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");
		StringBuffer updateSelfQuery4 = new StringBuffer("UPDATE ETS.ETS_SELF_PLAN SET ACTION_BY_ID  = '" + newUserId + "' WHERE ACTION_BY_ID = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");
		StringBuffer updateSelfQuery5 = new StringBuffer("UPDATE ETS.ETS_SELF_SECTION SET MEMBER_IR_ID  = '" + newUserId + "' WHERE MEMBER_IR_ID = '" + oldUserId + "' AND PROJECT_ID  = '" + projectId + "' ");

		Statement stmt = null;
		
		
		try {
			
			handleSelfAssessmentDeletes(projectId,oldUserId,newUserId);
			
			con = getConnection();
			
			stmt = con.createStatement();

			// set/met updates
			stmt.addBatch(updateQuery.toString());
			stmt.addBatch(updateQuery1.toString());
			stmt.addBatch(updateQuery2.toString());
			stmt.addBatch(updateQuery3.toString());
			stmt.addBatch(updateQuery4.toString());

			// self assessment updates
			stmt.addBatch(updateSelfQuery.toString());
			stmt.addBatch(updateSelfQuery1.toString());
			stmt.addBatch(updateSelfQuery2.toString());
			stmt.addBatch(updateSelfQuery3.toString());
			stmt.addBatch(updateSelfQuery4.toString());
			stmt.addBatch(updateSelfQuery5.toString());

			stmt.executeBatch();
			
			status = true;
			
		} finally {
			ETSDBUtils.close(stmt);
			cleanUp();
		}
		
		return status;
	}
	
}
