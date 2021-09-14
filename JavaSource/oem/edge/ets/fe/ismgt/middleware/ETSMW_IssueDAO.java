/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\ETSMW_IssueDAO.java

package oem.edge.ets.fe.ismgt.middleware;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.Vector;


import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSStringUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.CommonInfoDAO;
import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.model.EtsChgOwnerInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;


public class ETSMW_IssueDAO implements IssueDAO
{
	public static final String VERSION = "1.10";
	private static String ETSCQFormat = "ETS";
	// initializer gets the ETSCQ Format
	static{
		try {
			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ismgt.resources.filterresource");
			ETSCQFormat = rb.getString("ets.ismgt.cqtrkid.format");

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void printOut(String str) {
		// System.out.println(str);
	}
	private static void printErr(String str) {
		System.err.println(str);
	}
   IssueDAO theIssueDAO;

   /**
    * @param issue
    * @roseuid 427B9CA0023F
    */
   public static synchronized boolean  updateIssueCQ1(ETSMWIssue issue, Connection conn ) throws SQLException, Exception
   {

			//DbConnect dbConnect = null;
			boolean flg = false;
			PreparedStatement pstmt = null;

			try {

				if (conn == null) {
					/* dbConnect = new DbConnect();
					   dbConnect.makeConn();
					   conn = dbConnect.conn;*/
					throw new Exception("No connection for executong Work unit");
				}

				String sql =
				"update ETS.PROBLEM_INFO_CQ1 set"
					+ " CQ_TRK_ID =?,"
					+ " PROBLEM_STATE =?,"
					+ " PROBLEM_TYPE =?,"
					+ " PROBLEM_DESC =?,"
					+ " PROBLEM_CLASS =?,"
					+ " SEVERITY =?,"
					+ " TITLE =?,"
					+" SUBTYPE_A =?,"
					+ " SUBTYPE_B =?,"
					+ " SUBTYPE_C =?,"
					+ " SUBTYPE_D =?,"
					+ " FIELD_C1 =?,"
					+ " FIELD_C2 =?,"
					+ " FIELD_C3 =?,"
					+ " FIELD_C4 =?,"
					+ " FIELD_C5 =?,"
					+ " FIELD_C6 =?,"
					+ " FIELD_C7 =?,"
					+ " FIELD_C8 =?,"
					+ " TEST_CASE = ?,"
					+ " ISSUE_SOURCE = ?,"
					+ " ISSUE_TYPE_ID = ?,"
				+"last_timestamp=current timestamp," + " LAST_USERID= '" + AmtCommonUtils.getTrimStr(issue.last_userid) + "'," + " SEQ_NO = " + issue.seq_no + " + 8, "
				+ " FIELD_C14=?,"
				+ " FIELD_C15=?"
				+ " where EDGE_PROBLEM_ID = '" + issue.edge_problem_id + "'" + " and APPLICATION_ID = 'ETS'";

			Global.println("modify issue qry===" + sql);
			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();


			pstmt.setString(1,AmtCommonUtils.getTrimStr(issue.cq_trk_id));
			pstmt.setString(2,AmtCommonUtils.getTrimStr(issue.nextState));
			pstmt.setString(3,AmtCommonUtils.getTrimStr(issue.problem_type));
			pstmt.setString(4,AmtCommonUtils.getTrimStr(issue.problem_desc));
			pstmt.setString(5,AmtCommonUtils.getTrimStr(issue.problem_class));
			pstmt.setString(6,AmtCommonUtils.getTrimStr(issue.severity));
			pstmt.setString(7,AmtCommonUtils.getTrimStr(issue.title));
			pstmt.setString(8,AmtCommonUtils.getTrimStr(issue.subTypeA));
			pstmt.setString(9,AmtCommonUtils.getTrimStr(issue.subTypeB));
			pstmt.setString(10,AmtCommonUtils.getTrimStr(issue.subTypeC));
			pstmt.setString(11,AmtCommonUtils.getTrimStr(issue.subTypeD));
			pstmt.setString(12,AmtCommonUtils.getTrimStr(issue.field_C1));
			pstmt.setString(13,AmtCommonUtils.getTrimStr(issue.field_C2));
			pstmt.setString(14,AmtCommonUtils.getTrimStr(issue.field_C3));
			pstmt.setString(15,AmtCommonUtils.getTrimStr(issue.field_C4));
			pstmt.setString(16,AmtCommonUtils.getTrimStr(issue.field_C5));
			pstmt.setString(17,AmtCommonUtils.getTrimStr(issue.field_C6));
			pstmt.setString(18,AmtCommonUtils.getTrimStr(issue.field_C7));
			pstmt.setString(19,AmtCommonUtils.getTrimStr(issue.field_C8));
			pstmt.setString(20,AmtCommonUtils.getTrimStr(issue.test_case));
			pstmt.setString(21,AmtCommonUtils.getTrimStr(issue.issue_source));
			pstmt.setString(22,AmtCommonUtils.getTrimStr(issue.issueTypeId));
			pstmt.setString(23,AmtCommonUtils.getTrimStr(issue.field_C14));
			pstmt.setString(24,AmtCommonUtils.getTrimStr(issue.field_C15));


				int rowCount = 0;
				rowCount =  pstmt.executeUpdate();



			} catch (SQLException e) {
				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} catch (Exception e) {

				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} finally {
				/*if (dbConnect != null) {
					dbConnect.closeConn();
				}*/

				ETSDBUtils.close(pstmt);
				//ETSDBUtils.close(conn);  DONT CLOSE THE CONNECTION
			}

			flg = true;
			return flg;


   }

   /**
    * @roseuid 427B9EFA019A
    */
   public ETSMW_IssueDAO()
   {

   }

   public static void test (){
	ETSMWIssue issue = new ETSMWIssue();
	issue.application_id = "ETS";
				issue.cq_trk_id = "-";
				issue.edge_problem_id = "edge249";

				//submitter profile
				issue.problem_creator = "problemCreator";
				issue.cust_company = "custCompany";
				issue.cust_email = "custEmail";
				issue.cust_name = "custName";
				issue.cust_phone = "custPhone";
				issue.cust_project = "custProjectName";
				issue.ets_project_id = "etsProjectId";

				//issue desc

				issue.problem_class = "probClass";
;				issue.severity = "prevProbSeverity";
				issue.title = "probTitle";
				issue.problem_desc = "(probDesc)";
				issue.problem_state = "probState";

				//issue ident
				issue.problem_type = "issueType";

				///dyn vals//
				issue.subTypeA = "prevSubTypeAVal";
				issue.subTypeB = "prevSubTypeBVal";
				issue.subTypeC = "prevSubTypeCVal";
				issue.subTypeD = "prevSubTypeDVal";

				////static vals
				issue.field_C1 = "prevFieldC1Val";
				issue.field_C2 = "prevFieldC2Val";
				issue.field_C3 = "prevFieldC3Val";
				issue.field_C4 = "prevFieldC4Val";
				issue.field_C5 = "prevFieldC5Val";
				issue.field_C6 = "prevFieldC6Val";
				issue.field_C7 = "prevFieldC7Val";
				issue.test_case = "ETSUtils.escapeString(testCase)";

				issue.field_C12 = "fieldC12Val";
				issue.field_C14 = "fieldC14Val";
				issue.field_C15 = "fieldC15Val";
				///////////issue access
				issue.issue_access = "issueAccess";
				issue.issue_source = "issueSource";

				//commenst
				issue.comm_from_cust = "";

				//notification
				issue.ets_cclist = "ETSUtils.escapeString(sbnotify.toString())";
				//last user id

				issue.last_userid = "getEtsIssObjKey().getEs().gUSERN";

				//for ets issues type

				issue.nextState = "next state";
				issue.comm_log="comm_log";

					issue.etsIssuesType = "SUPPORT";
					try {
						ETSMW_IssueDAO.insertIssueCQ1(issue,null);
					}
					catch (Exception e) {
					}






}
   /**
    * @param issue
    * @roseuid 427B9CA0023F
    */
   public static synchronized boolean  updateActiononIssueCQ1(ETSMWIssue issue, Connection conn ) throws SQLException, Exception
   {

			//DbConnect dbConnect = null;
			boolean flg = false;
			PreparedStatement pstmt = null;

			try {

				if (conn == null) {
					/* dbConnect = new DbConnect();
					   dbConnect.makeConn();
					   conn = dbConnect.conn;*/
					throw new Exception("No connection for executong Work unit");
				}

				String sql =
				"update ETS.PROBLEM_INFO_CQ1 set"
					+ " PROBLEM_STATE = '"
					+ AmtCommonUtils.getTrimStr(issue.nextState)
					+ "',"



				////
	+"last_timestamp=current timestamp," + " LAST_USERID= '" + AmtCommonUtils.getTrimStr(issue.last_userid) + "'," + " SEQ_NO = " + issue.seq_no + " + 8, " + " FIELD_C14=?,"  + " FIELD_C15=?"  + " where EDGE_PROBLEM_ID = '" + issue.edge_problem_id + "'" + " and APPLICATION_ID = 'ETS'";

			Global.println("modify issue qry===" + sql);	pstmt = conn.prepareStatement(sql);

				pstmt.clearParameters();
				pstmt.setString(1,AmtCommonUtils.getTrimStr(issue.field_C14));
				pstmt.setString(2,AmtCommonUtils.getTrimStr(issue.field_C15));


				int rowCount = 0;
				rowCount =  pstmt.executeUpdate();



			} catch (SQLException e) {
				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} catch (Exception e) {

				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} finally {
				/*if (dbConnect != null) {
					dbConnect.closeConn();
				}*/

				ETSDBUtils.close(pstmt);
				//ETSDBUtils.close(conn);  DONT CLOSE THE CONNECTION
			}

			flg = true;
			return flg;


   }

   /**
    * @param issue
    * @roseuid 427B9CA0023F
    */
   public static synchronized boolean  updateIssueCQ2(ETSMWIssue issue, Connection conn ) throws SQLException, Exception
   {

			//DbConnect dbConnect = null;
			boolean flg = false;
			PreparedStatement pstmt = null;

			try {

				if (conn == null) {
					/* dbConnect = new DbConnect();
					   dbConnect.makeConn();
					   conn = dbConnect.conn;*/
					throw new Exception("No connection for executong Work unit");
				}

				String sql =
				"update ETS.PROBLEM_INFO_CQ2 set"
					+ " CQ_TRK_ID = '"
					+ AmtCommonUtils.getTrimStr(issue.cq_trk_id)
					+ "',"


		+ " COMM_LOG="
		+ "CONCAT( ?,(SELECT COMM_LOG FROM ETS.problem_info_cq2 WHERE APPLICATION_ID =" + "'" + issue.application_id + "' AND EDGE_PROBLEM_ID = '" + issue.edge_problem_id + "' AND CQ_TRK_ID= '" + issue.cq_trk_id + "'))"
		+ ", "
	/*	+ " ISSUE_TYPE_ID = '"
				+ AmtCommonUtils.getTrimStr(issue.ets_type_id)
				+ "', "*/


				////
	+"last_timestamp=current timestamp," + " LAST_USERID= '" + AmtCommonUtils.getTrimStr(issue.last_userid) + "'," + " SEQ_NO = " + issue.seq_no + " + 8  "  + " where EDGE_PROBLEM_ID = '" + issue.edge_problem_id + "'" + " and APPLICATION_ID = 'ETS'";

			Global.println("modify issue qry===" + sql);


			pstmt = conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setString(1,("\n\n" + ETSUtils.escapeString(issue.comm_log)+ "\n\n"));



				int rowCount = 0;
				rowCount =  pstmt.executeUpdate();



			} catch (SQLException e) {
				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} catch (Exception e) {

				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} finally {
				/*if (dbConnect != null) {
					dbConnect.closeConn();
				}*/

				ETSDBUtils.close(pstmt);
				//ETSDBUtils.close(conn);  DONT CLOSE THE CONNECTION
			}

			flg = true;
			return flg;


   }

public static synchronized boolean  updateActiononIssueRecord(ETSMWIssue mwIssue) throws SQLException, Exception
	 {

			  //DbConnect dbConnect = null;

			  boolean flg = false;
			  Connection conn = null;

		  try {



					// Begin work unit to update the various tables
					conn = ETSDBUtils.getConnection();
					conn.setAutoCommit(false);


					if( ETSIssuesManager. updateCommentsWithPtmt(mwIssue,conn)
						&& ETSMW_IssueDAO.updateActiononIssueCQ1(mwIssue, conn)
						&& ETSMW_IssueDAO.updateIssueCQ2(mwIssue, conn)
						&& ETSMW_IssueDAO.insertHistoryRecord(mwIssue, conn)){

							conn.commit();
							flg = true;
					}
					else
							conn.rollback();



				}
				catch (SQLException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
							
					ETSDBUtils.close(conn);
				}

			  return flg;


	 }

   /**
    * @param issue
    * @roseuid 427B9CA0023F
    */
   public static synchronized boolean  updateOwnerRecord(ETSMWIssue issue, Connection conn ) throws SQLException, Exception
   {

			//DbConnect dbConnect = null;
			boolean flg = false;
			Vector ownerRecords = issue.ownerRecords;
				 if( ownerRecords == null)
				   return flg;
				 int numOwnerRecs= ownerRecords.size();
				 if(numOwnerRecs< 1)
				   return flg;
			PreparedStatement pstmt = null;

			try {

				if (conn == null) {
					/* dbConnect = new DbConnect();
					   dbConnect.makeConn();
					   conn = dbConnect.conn;*/
					throw new Exception("No connection for executong Work unit");
				}

				for( int i=0; i<numOwnerRecs;i++ ){
									ETSMWOwnerRecord ownerRec = (ETSMWOwnerRecord)ownerRecords.elementAt(i);
				String sql =
				"update ETS.ETS_OWNER_CQ set"
					+ " EDGE_PROBLEM_ID = '"
					+ AmtCommonUtils.getTrimStr(issue.edge_problem_id)
					+ "',"
					+ " OWNER_ID = '"
					+ AmtCommonUtils.getTrimStr(ownerRec.getOwner_id())
					+ "',"
					+ " OWNER_EMAIL = '"
					+ AmtCommonUtils.getTrimStr(ownerRec.getOwner_email())
					+ "',"
					+ " ACTIVE_FLAG = '"
					+ AmtCommonUtils.getTrimStr("Y")
					+ "',"
					+"last_timestamp=current timestamp," + " LAST_USERID= '" + AmtCommonUtils.getTrimStr(issue.last_userid) + "'," + " SEQ_NO = " + issue.seq_no + " + 8  "  + " where EDGE_PROBLEM_ID = '" + issue.edge_problem_id + "'" + " and APPLICATION_ID = 'ETS'";

					Global.println("modify issue qry===" + sql);


				pstmt = conn.prepareStatement(sql);



				int rowCount = 0;
				rowCount =  pstmt.executeUpdate();

				}

			} catch (SQLException e) {
				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} catch (Exception e) {

				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} finally {
				/*if (dbConnect != null) {
					dbConnect.closeConn();
				}*/

				ETSDBUtils.close(pstmt);
				//ETSDBUtils.close(conn);  DONT CLOSE THE CONNECTION
			}

			flg = true;
			return flg;


   }
   public static synchronized boolean  changeOwnerRecord(ETSMWIssue issue, Connection conn ) throws SQLException, Exception
	  {

			   //DbConnect dbConnect = null;
			   boolean flg = false;
			   ETSMWOwnerRecord newownerRec = new ETSMWOwnerRecord();
			   EtsIssOwnerInfo ownerInfo=  issue.ownerInfo;
			   newownerRec.setOwner_id(ownerInfo.getUserEdgeId());
			   newownerRec.setOwner_email(ownerInfo.getUserEmail());
			   Vector ownerRecords = new Vector();
			   ownerRecords.add(newownerRec);
					if( ownerRecords == null)
					  return flg;
					int numOwnerRecs= ownerRecords.size();
					if(numOwnerRecs< 1)
					  return flg;
			   PreparedStatement pstmt = null;

			   try {

				   if (conn == null) {
					   /* dbConnect = new DbConnect();
						  dbConnect.makeConn();
						  conn = dbConnect.conn;*/
					   throw new Exception("No connection for executong Work unit");
				   }

				   for( int i=0; i<numOwnerRecs;i++ ){
									   ETSMWOwnerRecord ownerRec = (ETSMWOwnerRecord)ownerRecords.elementAt(i);
				   String sql =
				   "update ETS.ETS_OWNER_CQ set"
					   + " EDGE_PROBLEM_ID = '"
					   + AmtCommonUtils.getTrimStr(issue.edge_problem_id)
					   + "',"
					   + " OWNER_ID = '"
					   + AmtCommonUtils.getTrimStr(ownerRec.getOwner_id())
					   + "',"
					   + " OWNER_EMAIL = '"
					   + AmtCommonUtils.getTrimStr(ownerRec.getOwner_email())
					   + "',"
					   + " ACTIVE_FLAG = '"
					   + AmtCommonUtils.getTrimStr("Y")
					   + "',"
					   +"last_timestamp=current timestamp," + " LAST_USERID= '" + AmtCommonUtils.getTrimStr(issue.last_userid) + "'," + " SEQ_NO = " + issue.seq_no + " + 8  "  + " where EDGE_PROBLEM_ID = '" + issue.edge_problem_id + "'";

					   Global.println("modify issue qry===" + sql);


				   pstmt = conn.prepareStatement(sql);



				   int rowCount = 0;
				   rowCount =  pstmt.executeUpdate();

				   }

			   } catch (SQLException e) {
				   flg = false;
				   printErr(getStackTrace(e));
				   throw e;
			   } catch (Exception e) {

				   flg = false;
				   printErr(getStackTrace(e));
				   throw e;
			   } finally {
				   /*if (dbConnect != null) {
					   dbConnect.closeConn();
				   }*/

				   ETSDBUtils.close(pstmt);
				   //ETSDBUtils.close(conn);  DONT CLOSE THE CONNECTION
			   }

			   flg = true;
			   return flg;


	  }

   /**
    * @param issue
    * @roseuid 427B9CA0023F
    */
   public static synchronized boolean  insertIssueCQ1(ETSMWIssue issue, Connection conn ) throws SQLException, Exception
   {

			//DbConnect dbConnect = null;
			boolean flg = false;
			PreparedStatement pstmt = null;

			try {

				if (conn == null) {
					/* dbConnect = new DbConnect();
					   dbConnect.makeConn();
					   conn = dbConnect.conn;*/
					throw new Exception("No connection for executong Work unit");
				}

				String sql =
					"insert into ETS.PROBLEM_INFO_CQ1"
						+ " (APPLICATION_ID, EDGE_PROBLEM_ID, CQ_TRK_ID, PROBLEM_STATE, SEQ_NO, PROBLEM_CLASS, TITLE, SEVERITY, PROBLEM_TYPE, PROBLEM_DESC, LAST_USERID, LAST_TIMESTAMP, "
						+ " FIELD_C1,FIELD_C2,FIELD_C3,FIELD_C4,FIELD_C5,FIELD_C6,FIELD_C7,PROJECT_ID,ISSUE_ACCESS,ISSUE_SOURCE,SUBTYPE_A,SUBTYPE_B,SUBTYPE_C,SUBTYPE_D,TEST_CASE,FIELD_C14,FIELD_C15,FIELD_C12,ISSUE_TYPE_ID ) "
						+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				pstmt = conn.prepareStatement(sql);

				String userid = issue.problem_creator;

				pstmt.setString(1, "ETS");
				pstmt.setString(2, AmtCommonUtils.getTrimStr(issue.edge_problem_id));
				pstmt.setString(3, AmtCommonUtils.getTrimStr(issue.cq_trk_id));
				pstmt.setString(4, AmtCommonUtils.getTrimStr(issue.nextState));
				pstmt.setInt(5, issue.seq_no+2);
				pstmt.setString(6, AmtCommonUtils.getTrimStr(issue.problem_class));
				pstmt.setString(7, AmtCommonUtils.getTrimStr(issue.title));
				pstmt.setString(8, AmtCommonUtils.getTrimStr(issue.severity));
				pstmt.setString(9, AmtCommonUtils.getTrimStr(issue.problem_type));
				pstmt.setString(10, AmtCommonUtils.getTrimStr(issue.problem_desc));
				pstmt.setString(11, AmtCommonUtils.getTrimStr(issue.last_userid));
	//			pstmt.setString(12, AmtCommonUtils.getTrimStr());
				pstmt.setString(12, AmtCommonUtils.getTrimStr(issue.field_C1));
				pstmt.setString(13, AmtCommonUtils.getTrimStr(issue.field_C2));
				pstmt.setString(14, AmtCommonUtils.getTrimStr(issue.field_C3));
				pstmt.setString(15, AmtCommonUtils.getTrimStr(issue.field_C4));
				pstmt.setString(16, AmtCommonUtils.getTrimStr(issue.field_C5));
				pstmt.setString(17, AmtCommonUtils.getTrimStr(issue.field_C6));
				pstmt.setString(18, AmtCommonUtils.getTrimStr(issue.field_C7));
				pstmt.setString(19, AmtCommonUtils.getTrimStr(issue.ets_project_id));
				pstmt.setString(20, AmtCommonUtils.getTrimStr(issue.issue_access));
				pstmt.setString(21, AmtCommonUtils.getTrimStr(issue.issue_source));
				pstmt.setString(22, AmtCommonUtils.getTrimStr(issue.subTypeA));
				pstmt.setString(23, AmtCommonUtils.getTrimStr(issue.subTypeB));
				pstmt.setString(24, AmtCommonUtils.getTrimStr(issue.subTypeC));
				pstmt.setString(25, AmtCommonUtils.getTrimStr(issue.subTypeD));
				pstmt.setString(26, AmtCommonUtils.getTrimStr(issue.test_case));
				///////////fxpk1////////////
				pstmt.setString(27, AmtCommonUtils.getTrimStr(issue.field_C14)); //first name
				pstmt.setString(28, AmtCommonUtils.getTrimStr(issue.field_C15)); //last name
				pstmt.setString(29, AmtCommonUtils.getTrimStr(issue.field_C12)); //tc
				pstmt.setString(30, AmtCommonUtils.getTrimStr(issue.issueTypeId)); //issue_type_id


				int rowCount = 0;
				rowCount =  pstmt.executeUpdate();



			} catch (SQLException e) {
				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} catch (Exception e) {

				flg = false;
				printErr(getStackTrace(e));
				throw e;
			} finally {
				/*if (dbConnect != null) {
					dbConnect.closeConn();
				}*/

				ETSDBUtils.close(pstmt);
				//ETSDBUtils.close(conn);  DONT CLOSE THE CONNECTION
			}

			flg = true;
			return flg;


   }

   public static boolean get_and_blockFeedbackTrackingID(ETSMWIssue issue, Connection conn) throws SQLException, Exception {

			   //DbConnect dbConnect = null;
			boolean flg = false;
			String CQ_TRK_ID = "00000";

			   Statement stmt = null;
			   //ResultSet rs = null;
			   ResultSet rs1 = null;
		   //	TODO  get cqtablename from a file
			   String outtable1_name="ets.problem_info_cq1";
			   try {
				   if (conn == null) {
					   //dbConnect = new DbConnect();
					   //dbConnect.makeConn();
					   //conn = dbConnect.conn;
					throw new Exception("No connection for executong Work unit");
				   }

					// GET OVERALL MAX CQ_TRK_ID

				   String sql = "select CQ_TRK_ID from " +  outtable1_name + " where application_ID='ETS'  ORDER by CQ_TRK_ID DESC";

				   stmt = conn.createStatement();
				   rs1 = stmt.executeQuery(sql);

				   boolean getCQ2 = false;
				   int value=0;
				   if (rs1.next()){
					   CQ_TRK_ID=rs1.getString("CQ_TRK_ID");
					   System.out.println("MAX CQ_TRK_ID " + CQ_TRK_ID);
					}

				   rs1.close();
				   stmt.close();

				//	GET Project MAX CQ_TRK_ID
				 sql = "select cq.CQ_TRK_ID from " +  outtable1_name + " cq , ets.problem_info_usr1 u where u.application_ID='ETS' and u.edge_problem_id=cq.edge_problem_id and u.ets_project_id='" +  issue.ets_project_id + "' ORDER by CQ_TRK_ID DESC";
				 stmt = conn.createStatement();
				 rs1 = stmt.executeQuery(sql);
				 String project_CQ_TRK_ID= "00000";
				if (rs1.next()){
				   project_CQ_TRK_ID=rs1.getString("CQ_TRK_ID");
				   System.out.println("MAX CQ_TRK_ID " + CQ_TRK_ID);
				}
				rs1.close();
				stmt.close();

				// IF THERE EXISTIN	ISSUES HAVING CQ_TRK_IDS IN THE OLD FORMAT
				if(project_CQ_TRK_ID.indexOf(ETSCQFormat) == -1){

					CQ_TRK_ID = project_CQ_TRK_ID;
					value= Integer.valueOf(CQ_TRK_ID).intValue();
					System.out.println("MAX CQ_TRK_ID " + CQ_TRK_ID);
					System.out.println("value is " + value);
					value=value+1;
					int valuesize = String.valueOf(value).length();
					String newCQTRKID = "";
					valuesize = 5-valuesize;
					for( int j=0; j < valuesize ; j++)
						newCQTRKID = newCQTRKID.concat("0");
					newCQTRKID=newCQTRKID.concat(String.valueOf(value));
					CQ_TRK_ID=newCQTRKID;


			   }
			   else{ // NEW FORMAT

					value= Integer.valueOf(CQ_TRK_ID.substring(ETSCQFormat.length(),CQ_TRK_ID.length())).intValue();
					   System.out.println("MAX CQ_TRK_ID " + CQ_TRK_ID);
					   System.out.println("value is " + value);
					   value=value+1;
					   int valuesize = String.valueOf(value).length();
					   String newCQTRKID = ETSCQFormat;
					   valuesize = 8-valuesize;
					   for( int j=0; j < valuesize ; j++)
							   newCQTRKID = newCQTRKID.concat("0");
					   newCQTRKID=newCQTRKID.concat(String.valueOf(value));
					CQ_TRK_ID=newCQTRKID;

			   }

			   flg=true;




			   } catch (SQLException e) {
				   /*if (dbConnect != null) {
					   dbConnect.removeConn(e);
					   dbConnect = null;
				   }*/
				   printErr(getStackTrace(e));
				   throw e;
			   } catch (Exception e) {
				   printErr(getStackTrace(e));
				   throw e;
			   } finally {
				   /*if (dbConnect != null) {
					   dbConnect.closeConn();
				   }*/
				   ETSDBUtils.close(rs1);
				   ETSDBUtils.close(stmt);
				//   ETSDBUtils.close(conn);
			   }
		   	    issue.cq_trk_id= CQ_TRK_ID;
		   	    return flg;
		   }

   public static boolean get_and_blockTrackingID(ETSMWIssue issue, Connection conn) throws SQLException, Exception {

			   //DbConnect dbConnect = null;
			boolean flg = false;
			String CQ_TRK_ID = "00000";

			   Statement stmt = null;
			   //ResultSet rs = null;
			   ResultSet rs1 = null;
		   //	TODO  get cqtablename from a file
			   String outtable1_name="ets.problem_info_cq1";
			   try {
				   if (conn == null) {
					   //dbConnect = new DbConnect();
					   //dbConnect.makeConn();
					   //conn = dbConnect.conn;
					throw new Exception("No connection for executong Work unit");
				   }

					// GET OVERALL MAX CQ_TRK_ID

				   String sql = "select CQ_TRK_ID from " +  outtable1_name + " where application_ID='ETS' ORDER by CQ_TRK_ID DESC";

				   stmt = conn.createStatement();
				   rs1 = stmt.executeQuery(sql);

				   boolean getCQ2 = false;
				   int value=0;
				   if (rs1.next()){
					   CQ_TRK_ID=rs1.getString("CQ_TRK_ID");
					   System.out.println("MAX CQ_TRK_ID " + CQ_TRK_ID);
					}

				   rs1.close();
				   stmt.close();

				//	GET Project MAX CQ_TRK_ID
				 sql = "select cq.CQ_TRK_ID from " +  outtable1_name + " cq , ets.problem_info_usr1 u where u.application_ID='ETS' and u.edge_problem_id=cq.edge_problem_id and u.ets_project_id='" +  issue.ets_project_id + "' ORDER by CQ_TRK_ID DESC";
				 stmt = conn.createStatement();
				 rs1 = stmt.executeQuery(sql);
				 String project_CQ_TRK_ID= "00000";
				if (rs1.next()){
				   project_CQ_TRK_ID=rs1.getString("CQ_TRK_ID");
				   System.out.println("MAX CQ_TRK_ID " + CQ_TRK_ID);
				}
				rs1.close();
				stmt.close();

				// IF THERE EXISTIN	ISSUES HAVING NO CQ_TRK_IDS IN THE OLD FORMAT
				if(project_CQ_TRK_ID.indexOf(ETSCQFormat) == -1){

					CQ_TRK_ID = project_CQ_TRK_ID;
					value= Integer.valueOf(CQ_TRK_ID).intValue();
					System.out.println("MAX CQ_TRK_ID " + CQ_TRK_ID);
					System.out.println("value is " + value);
					value=value+1;
					int valuesize = String.valueOf(value).length();
					String newCQTRKID = "";
					valuesize = 5-valuesize;
					for( int j=0; j < valuesize ; j++)
						newCQTRKID = newCQTRKID.concat("0");
					newCQTRKID=newCQTRKID.concat(String.valueOf(value));
					CQ_TRK_ID=newCQTRKID;


			   }
			   else{ // OLD FORMAT

					value= Integer.valueOf(CQ_TRK_ID.substring(ETSCQFormat.length(),CQ_TRK_ID.length())).intValue();
					   System.out.println("MAX CQ_TRK_ID " + CQ_TRK_ID);
					   System.out.println("value is " + value);
					   value=value+1;
					   int valuesize = String.valueOf(value).length();
					   String newCQTRKID = ETSCQFormat;
					   valuesize = 8-valuesize;
					   for( int j=0; j < valuesize ; j++)
							   newCQTRKID = newCQTRKID.concat("0");
					   newCQTRKID=newCQTRKID.concat(String.valueOf(value));
					CQ_TRK_ID=newCQTRKID;

			   }

			   flg=true;




			   } catch (SQLException e) {
				   /*if (dbConnect != null) {
					   dbConnect.removeConn(e);
					   dbConnect = null;
				   }*/
				   printErr(getStackTrace(e));
				   throw e;
			   } catch (Exception e) {
				   printErr(getStackTrace(e));
				   throw e;
			   } finally {
				   /*if (dbConnect != null) {
					   dbConnect.closeConn();
				   }*/
				   ETSDBUtils.close(rs1);
				   ETSDBUtils.close(stmt);
				//   ETSDBUtils.close(conn);
			   }
		   	    issue.cq_trk_id= CQ_TRK_ID;
		   	    return flg;
		   }

	public static String getCurrentState(ETSMWIssue issue) throws SQLException, Exception {

				//DbConnect dbConnect = null;
			 boolean flg = false;
			 String currentState = null;
			   	Connection conn=null;
			   	Statement stmt = null;
				ResultSet rs = null;
				String outtable1_name="ets.problem_info_cq1";
				try {
					conn= ETSDBUtils.getConnection();

					String sql = "select problem_state from " +  outtable1_name + " where application_ID='ETS' and edge_problem_id='" + issue.edge_problem_id + "'";

					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);

					if (rs.next()){
						currentState=rs.getString("PROBLEM_STATE");
						System.out.println("NEW PROBLEM_STATE " + currentState);
					}


					stmt.close();

				} catch (SQLException e) {
					/*if (dbConnect != null) {
						dbConnect.removeConn(e);
						dbConnect = null;
					}*/
					printErr(getStackTrace(e));
					throw e;
				} catch (Exception e) {
					printErr(getStackTrace(e));
					throw e;
				} finally {
					/*if (dbConnect != null) {
						dbConnect.closeConn();
					}*/
					ETSDBUtils.close(rs);
					ETSDBUtils.close(stmt);
				    ETSDBUtils.close(conn);
				}

				 return currentState;
			}
   public static synchronized boolean  insertOwnerRecord(ETSMWIssue issue, Connection conn ) throws SQLException, Exception
	 {

			  //DbConnect dbConnect = null;

			  boolean flg = false;
			  Vector ownerRecords = issue.ownerRecords;
			  if( ownerRecords == null)
			  	return flg;
			  int numOwnerRecs= ownerRecords.size();
			  if(numOwnerRecs< 1)
			  	return flg;

			  PreparedStatement pstmt = null;

			  try {

				  if (conn == null) {
					  /* dbConnect = new DbConnect();
						 dbConnect.makeConn();
						 conn = dbConnect.conn;*/
					  throw new Exception("No connection available to complete work unit");
				  }


				  for( int i=0; i<numOwnerRecs;i++ ){
				  	ETSMWOwnerRecord ownerRec = (ETSMWOwnerRecord)ownerRecords.elementAt(i);
				  	/*
					String sql =
										  "insert into ETS.ETS_OWNER_CQ"
											  + " (EDGE_PROBLEM_ID, OWNER_ID,OWNER_EMAIL,LAST_USERID, LAST_TIMESTAMP,ACTIVE_FLAG) "
											  + " VALUES(?, ?, ?, ?,current timestamp,?)";
					*/
					String sql =  "insert into ETS.ETS_OWNER_CQ"
							  + " (EDGE_PROBLEM_ID, OWNER_ID,OWNER_EMAIL,LAST_USERID, LAST_TIMESTAMP,ACTIVE_FLAG,BK_OWNER_ID,BK_OWNER_EMAIL) "
							  + " VALUES(?, ?, ?, ?,current timestamp,?,?,?)";
	
					
					

									  pstmt = conn.prepareStatement(sql);

									  String userid = issue.problem_creator;

									  pstmt.setString(1, AmtCommonUtils.getTrimStr(issue.edge_problem_id));
									  pstmt.setString(2, AmtCommonUtils.getTrimStr(ownerRec.getOwner_id()));
									  pstmt.setString(3, AmtCommonUtils.getTrimStr(ownerRec.getOwner_email()));
									  pstmt.setString(4, "JVRAO");
									  pstmt.setString(5, "Y");
									  pstmt.setString(6, AmtCommonUtils.getTrimStr(ownerRec.getBackupOwner_id()));
									  pstmt.setString(7, AmtCommonUtils.getTrimStr(ownerRec.getBackupOwner_email()));									  


									//  pstmt.setString(6, AmtCommonUtils.getTrimStr(issue.comm_log));


									  int rowCount = 0;
									  rowCount=pstmt.executeUpdate();

				  }



			  } catch (SQLException e) {
				  flg = false;
				  printErr(getStackTrace(e));
				  throw e;
			  } catch (Exception e) {

				  flg = false;
				  printErr(getStackTrace(e));
				  throw e;
			  } finally {
				  /*if (dbConnect != null) {
					  dbConnect.closeConn();
				  }*/

				  ETSDBUtils.close(pstmt);
				  //ETSDBUtils.close(conn);  DONT CLOSE THE CONNECTION
			  }

			  flg = true;
			  return flg;


	 }

   public static synchronized boolean  insertHistoryRecord(ETSMWIssue issue, Connection conn ) throws SQLException, Exception
	 {

			  //DbConnect dbConnect = null;

			  boolean flg = false;


			  PreparedStatement pstmt = null;

			  try {

				  if (conn == null) {
					  /* dbConnect = new DbConnect();
						 dbConnect.makeConn();
						 conn = dbConnect.conn;*/
					 throw new Exception("No connection for executong Work unit");
				  }


				  String sql =
										  "insert into ETS.ISSUE_HISTORY"
											  + " (EDGE_PROBLEM_ID, USER_NAME,ACTION_NAME,ISSUE_STATE, ACTION_TS) "
											  + " VALUES(?, ?, ?, ?,current timestamp)";

									  pstmt = conn.prepareStatement(sql);

									  String userid = issue.problem_creator;

									  pstmt.setString(1, AmtCommonUtils.getTrimStr(issue.edge_problem_id));
									  pstmt.setString(2, AmtCommonUtils.getTrimStr(issue.last_userid));
									  pstmt.setString(3, AmtCommonUtils.getTrimStr(issue.problem_state));
									  pstmt.setString(4, AmtCommonUtils.getTrimStr(issue.nextState));



									//  pstmt.setString(6, AmtCommonUtils.getTrimStr(issue.comm_log));


									  int rowCount = 0;
									  rowCount=pstmt.executeUpdate();




			  } catch (SQLException e) {
				  flg = false;
				  printErr(getStackTrace(e));
				  throw e;
			  } catch (Exception e) {

				  flg = false;
				  printErr(getStackTrace(e));
				  throw e;
			  } finally {
				  /*if (dbConnect != null) {
					  dbConnect.closeConn();
				  }*/

				  ETSDBUtils.close(pstmt);
				  //ETSDBUtils.close(conn);  DONT CLOSE THE CONNECTION
			  }

			  flg = true;
			  return flg;


	 }

	public static synchronized boolean  insertIssueRecord(ETSMWIssue mwIssue) throws SQLException, Exception
		 {

				  //DbConnect dbConnect = null;

				  boolean flg = false;
				  Connection conn = null;

			  try {

						// get owner records
						mwIssue.ownerRecords=
										ETSMW_IssueDAO.getOwnerdata(mwIssue);


						// Begin work unit to update the various tables
						conn = ETSDBUtils.getConnection();
						conn.setAutoCommit(false);


						if( ETSIssuesManager. addNewIssue(mwIssue,conn)
							&& ETSMW_IssueDAO.get_and_blockTrackingID(mwIssue,conn)
						    && ETSMW_IssueDAO.insertIssueCQ1(mwIssue, conn)
							&& ETSMW_IssueDAO.insertIssueCQ2(mwIssue, conn)
							&& ETSMW_IssueDAO.insertOwnerRecord(mwIssue, conn)
							&& ETSMW_IssueDAO.insertHistoryRecord(mwIssue, conn)){

								conn.commit();
								flg = true;
						}
						else
								conn.rollback();



					}
					catch (SQLException e) {
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
								
						ETSDBUtils.close(conn);
					}

				  return flg;


		 }

	public static synchronized boolean  insertFeedbackRecord(ETSMWIssue mwIssue) throws SQLException, Exception
			 {

					  //DbConnect dbConnect = null;

					  boolean flg = false;
					Connection conn = null;

				  try {


							// Begin work unit to update the various tables
							conn = ETSDBUtils.getConnection();
							conn.setAutoCommit(false);


							if( ETSIssuesManager. addNewIssue(mwIssue,conn)
								&& ETSMW_IssueDAO.get_and_blockTrackingID(mwIssue,conn)
								&& ETSMW_IssueDAO.insertIssueCQ1(mwIssue, conn)
								&& ETSMW_IssueDAO.insertIssueCQ2(mwIssue, conn)
							//	&& ETSMW_IssueDAO.insertOwnerRecord(mwIssue, conn)
							//	&& ETSMW_IssueDAO.insertHistoryRecord(mwIssue, conn))
							){

									conn.commit();
									flg = true;
							}
							else
									conn.rollback();



						}
						catch (SQLException e) {
							e.printStackTrace();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						
						finally {
											
							ETSDBUtils.close(conn);
						}

					  return flg;


			 }


	public static synchronized boolean  updateIssueRecord(ETSMWIssue mwIssue) throws SQLException, Exception
		 {

				  //DbConnect dbConnect = null;

				  boolean flg = false;
				 Connection conn = null;

			  try {



						// Begin work unit to update the various tables
						conn = ETSDBUtils.getConnection();
						conn.setAutoCommit(false);


						if( ETSIssuesManager. modifyIssueWithPstmt(mwIssue,conn)
							&& ETSMW_IssueDAO.updateIssueCQ1(mwIssue, conn)
							&& ETSMW_IssueDAO.updateIssueCQ2(mwIssue, conn)
							&& ETSMW_IssueDAO.insertHistoryRecord(mwIssue, conn)){

								conn.commit();
								flg = true;
						}
						else
								conn.rollback();



					}
					catch (SQLException e) {
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					}

			        finally {
							
						ETSDBUtils.close(conn);
					}


				  return flg;


		 }

	public static synchronized boolean  updateActiononFeedbackRecord(ETSMWIssue mwIssue) throws SQLException, Exception
		 {

				  //DbConnect dbConnect = null;

				  boolean flg = false;
				  Connection conn = null;

			  try {



						// Begin work unit to update the various tables
						conn = ETSDBUtils.getConnection();
						conn.setAutoCommit(false);


						if( ETSIssuesManager. updateCommentsWithPtmt(mwIssue,conn)
							&& ETSMW_IssueDAO.updateActiononIssueCQ1(mwIssue, conn)
							&& ETSMW_IssueDAO.updateIssueCQ2(mwIssue, conn)
						//	&& ETSMW_IssueDAO.insertHistoryRecord(mwIssue, conn)
						){

								conn.commit();
								flg = true;
						}
						else
								conn.rollback();



					}
					catch (SQLException e) {
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
								
						ETSDBUtils.close(conn);
					}

				  return flg;


		 }

	public static synchronized boolean  changeOwnerofIssueRecord(ETSMWIssue mwIssue) throws SQLException, Exception
			 {

					  //DbConnect dbConnect = null;
				Connection conn = null;

					  boolean flg = false;

				  try {

				//			get owner records
							mwIssue.ownerRecords = ETSMW_IssueDAO.getOwnerdata_OLD(mwIssue);



							// Begin work unit to update the various tables
							conn = ETSDBUtils.getConnection();
							conn.setAutoCommit(false);


							if( ETSIssuesManager. updateCommentsWithPtmt(mwIssue,conn)
								&& ETSMW_IssueDAO.updateActiononIssueCQ1(mwIssue, conn)
								&& ETSMW_IssueDAO.updateIssueCQ2(mwIssue, conn)
								&& ETSMW_IssueDAO.insertHistoryRecord(mwIssue, conn)
								&& ETSMW_IssueDAO.changeOwnerRecord(mwIssue, conn)){

									conn.commit();
									flg = true;
							}
							else
									conn.rollback();



						}
						catch (SQLException e) {
							e.printStackTrace();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						
						finally {
								
							ETSDBUtils.close(conn);
						
					    }

					  return flg;


			 }
   public static synchronized boolean  insertIssueCQ2(ETSMWIssue issue, Connection conn ) throws SQLException, Exception
	 {

			  //DbConnect dbConnect = null;
			  boolean flg = false;
			  PreparedStatement pstmt = null;

			  try {

				  if (conn == null) {
					  /* dbConnect = new DbConnect();
						 dbConnect.makeConn();
						 conn = dbConnect.conn;*/
					throw new Exception("No connection for executong Work unit");
				  }

				  String sql =
					  "insert into ETS.PROBLEM_INFO_CQ2"
						  + " (APPLICATION_ID, EDGE_PROBLEM_ID, CQ_TRK_ID,SEQ_NO,LAST_USERID, LAST_TIMESTAMP,COMM_LOG )"
						  + " VALUES (?, ?, ?, ?,?,current timestamp,?)";

				  pstmt = conn.prepareStatement(sql);

				  String userid = issue.problem_creator;

				  pstmt.setString(1, AmtCommonUtils.getTrimStr(issue.application_id));
				  pstmt.setString(2, AmtCommonUtils.getTrimStr(issue.edge_problem_id));
				  pstmt.setString(3, AmtCommonUtils.getTrimStr(issue.cq_trk_id));
				  pstmt.setInt(4, issue.seq_no+2);
				  pstmt.setString(5, AmtCommonUtils.getTrimStr(issue.last_userid));


				  pstmt.setString(6, AmtCommonUtils.getTrimStr(issue.comm_log));


				  int rowCount = 0;
				  rowCount=pstmt.executeUpdate();



			  } catch (SQLException e) {
				  flg = false;
				  printErr(getStackTrace(e));
				  throw e;
			  } catch (Exception e) {

				  flg = false;
				  printErr(getStackTrace(e));
				  throw e;
			  } finally {
				  /*if (dbConnect != null) {
					  dbConnect.closeConn();
				  }*/

				  ETSDBUtils.close(pstmt);
				  //ETSDBUtils.close(conn);  DONT CLOSE THE CONNECTION
			  }

			  flg = true;
			  return flg;


	 }
   private static String getStackTrace(Throwable t) {
		   StringWriter sw = new StringWriter();
		   PrintWriter pw = new PrintWriter(sw);
		   t.printStackTrace(pw);
		   String stackTrace = sw.toString();
		   pw.close();
		   return stackTrace;
	   }

   /**
    * @param issue
    * @roseuid 427B9CAB00A0
    */
   public void updateIssue(int issue)
   {

   }

   /**
    * @param owner
    * @roseuid 427B9CBE02E2
    */
   public void insertOwner(int owner)
   {

   }

   /**
    * @param owner
    * @roseuid 427B9CC70032
    */
   public void updateOwner(int owner)
   {

   }

   /**
    * @param issuetype
    * @roseuid 427B9CD00017
    */
   public void insertIssuetype(int issuetype)
   {

   }
   public void createIssue(ETSMWIssue test)
	  {

	  }

public static synchronized boolean  updateIssueandOwnerRecord(ETSMWIssue mwIssue) throws SQLException, Exception
	 {

			  //DbConnect dbConnect = null;

			  boolean flg = false;
			Connection conn = null;

		  try {



					// Begin work unit to update the various tables
					conn = ETSDBUtils.getConnection();
					conn.setAutoCommit(false);


					if( ETSIssuesManager. modifyIssueWithPstmt(mwIssue,conn)
						&& ETSMW_IssueDAO.updateIssueCQ1(mwIssue, conn)
						&& ETSMW_IssueDAO.updateIssueCQ2(mwIssue, conn)
						&& ETSMW_IssueDAO.updateOwnerRecord(mwIssue,conn)
						&& ETSMW_IssueDAO.insertHistoryRecord(mwIssue, conn)){

							conn.commit();
							flg = true;
					}
					else
							conn.rollback();



				}
				catch (SQLException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}

				finally {
							
					ETSDBUtils.close(conn);
				}


			  return flg;


	 }


   /**
    * @param issuetype
    * @roseuid 427B9CE102D8
    */
   public void updateIssuetype(int issuetype)
   {

   }

   /**
 * @param edge_problem_id
 * @param conn
 * @return
 * @throws SQLException
 * @throws Exception
 */
/**
    * @roseuid 427B9CF102BD
    * This method gets the default owner records which is assigned when an issue
    * is originally submitted
    */

	public static Vector getOwnerdata_OLD(ETSMWIssue currentRecord) throws SQLException, Exception {
			Vector ownerdataRecords=new Vector();
			//DbConnect dbConnect = null;
			Statement stmt = null;
			ResultSet rs = null;
			Connection conn =null;

			try {
				
				conn = ETSDBUtils.getConnection();
				

				StringBuffer sqlbuffer = new StringBuffer();
				sqlbuffer.append("select * from ETS.ets_owner_data u WHERE u.data_id=(select c.data_id FROM ETS.ets_dropdown_data c where c.issuetype='" );
				sqlbuffer.append(currentRecord.problem_type+ "'" + " and ");
				if(currentRecord.problem_class.equals("Question") || currentRecord.problem_class.equals("Defect"))
					sqlbuffer.append("c.issue_class='Defect'");
				else
					sqlbuffer.append("c.issue_class='Change'");
				sqlbuffer.append(" and ");
				sqlbuffer.append("c.PROJECT_ID='" + currentRecord.ets_project_id.trim() + "'");
				sqlbuffer.append(" and ");
				sqlbuffer.append("c.subtype_a='" + currentRecord.subTypeA.trim() + "'");
				sqlbuffer.append(" and ");
				sqlbuffer.append("c.subtype_b='" + currentRecord.subTypeB.trim() + "'");
				sqlbuffer.append(" and ");
				sqlbuffer.append("c.subtype_c='" + currentRecord.subTypeC.trim() + "'");
				sqlbuffer.append(" and ");
				sqlbuffer.append("c.subtype_d='" + currentRecord.subTypeD.trim() + "'");
				sqlbuffer.append(")");


				stmt = conn.createStatement();

				rs = stmt.executeQuery(sqlbuffer.toString());
				while(rs.next()){
					ETSMWOwnerRecord cqrec = new ETSMWOwnerRecord();
					cqrec.setOwner_id(rs.getString("ISSUE_OWNER_ID"));
					cqrec.setOwner_email(rs.getString("ISSUE_OWNER_EMAIL"));
					ownerdataRecords.addElement(cqrec);
				}

			} catch (SQLException e) {
				/*if (dbConnect != null) {
					dbConnect.removeConn(e);
					dbConnect = null;
				}*/
				printErr(getStackTrace(e));
				throw e;
			} catch (Exception e) {
				printErr(getStackTrace(e));
				throw e;
			} finally {
				/*if (dbConnect != null) {
					dbConnect.closeConn();
				}*/

				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
				ETSDBUtils.close(conn);
			}
			return ownerdataRecords;
		}
	public static Vector getOwnerRecord(String edge_problem_id) throws SQLException, Exception {
				Vector ownerdataRecords=new Vector();
				//DbConnect dbConnect = null;
				Statement stmt = null;
				ResultSet rs = null;
				Connection conn = null;

				try {
					
					conn = ETSDBUtils.getConnection();
					

					StringBuffer sqlbuffer = new StringBuffer();
					sqlbuffer.append("select * from ETS.ets_owner_cq WHERE edge_problem_id='" + edge_problem_id + "'" );


					stmt = conn.createStatement();

					rs = stmt.executeQuery(sqlbuffer.toString());
					while(rs.next()){
						ETSMWOwnerRecord cqrec = new ETSMWOwnerRecord();
						cqrec.setOwner_id(rs.getString("OWNER_ID"));
						cqrec.setOwner_email(rs.getString("OWNER_EMAIL"));
						cqrec.setBackupOwner_id(rs.getString("BK_OWNER_ID"));
						cqrec.setBackupOwner_email(rs.getString("BK_OWNER_EMAIL"));
						ownerdataRecords.addElement(cqrec);
					}

				} catch (SQLException e) {
					/*if (dbConnect != null) {
						dbConnect.removeConn(e);
						dbConnect = null;
					}*/
					printErr(getStackTrace(e));
					throw e;
				} catch (Exception e) {
					printErr(getStackTrace(e));
					throw e;
				} finally {
					/*if (dbConnect != null) {
						dbConnect.closeConn();
					}*/

					ETSDBUtils.close(rs);
					ETSDBUtils.close(stmt);
					ETSDBUtils.close(conn);
				}
				return ownerdataRecords;
			}

	   /**
	 * @param edge_problem_id
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	/**
	    * @roseuid 427B9CF102BD
	    * This method gets the default owner records which is assigned when an issue
	    * is originally submitted
	    */

		public static boolean isIssueTypeActiveForCurrentRecord(String edge_problem_id) throws SQLException, Exception {
				//DbConnect dbConnect = null;
				Statement stmt = null;
				ResultSet rs = null;
				Connection conn =null;
				boolean active = false;
				try {
					
					conn = ETSDBUtils.getConnection();
					

					StringBuffer sqlbuffer = new StringBuffer();
					sqlbuffer.append("select ACTIVE_FLAG from ETS.ets_dropdown_data WHERE data_id=(Select issue_type_id from ets.problem_info_usr1 where edge_problem_id='"  + edge_problem_id + "')");

					stmt = conn.createStatement();

					rs = stmt.executeQuery(sqlbuffer.toString());
					while(rs.next()){
						if(rs.getString("ACTIVE_FLAG").equalsIgnoreCase("Y"))
							active=true;
					}

				} catch (SQLException e) {
					/*if (dbConnect != null) {
						dbConnect.removeConn(e);
						dbConnect = null;
					}*/
					printErr(getStackTrace(e));
					throw e;
				} catch (Exception e) {
					printErr(getStackTrace(e));
					throw e;
				} finally {
					/*if (dbConnect != null) {
						dbConnect.closeConn();
					}*/

					ETSDBUtils.close(rs);
					ETSDBUtils.close(stmt);
					ETSDBUtils.close(conn);
				}
				return active;
			}


	public static String getSubscriberEmailList(String issue_type_id) throws SQLException, Exception {
					StringWriter emailList = new StringWriter();
					//DbConnect dbConnect = null;
					Statement stmt = null;
					ResultSet rs = null;
					Connection conn = null;

					try {
						
						conn = ETSDBUtils.getConnection();
					

						StringBuffer sqlbuffer = new StringBuffer();
						sqlbuffer.append("select edge_user_id from ETS.SUBSCRIBE_ISSUETYPE WHERE issue_type_id='" + issue_type_id + "'" );


						stmt = conn.createStatement();

						rs = stmt.executeQuery(sqlbuffer.toString());
						CommonInfoDAO dao = new CommonInfoDAO();
						while(rs.next()){

							emailList.write(dao.getUserDetailsInfo(rs.getString("EDGE_USER_ID")).getUserEmail());
							emailList.write(",");
						}


					} catch (SQLException e) {
						/*if (dbConnect != null) {
							dbConnect.removeConn(e);
							dbConnect = null;
						}*/
						printErr(getStackTrace(e));
						throw e;
					} catch (Exception e) {
						printErr(getStackTrace(e));
						throw e;
					} finally {
						/*if (dbConnect != null) {
							dbConnect.closeConn();
						}*/

						ETSDBUtils.close(rs);
						ETSDBUtils.close(stmt);
						ETSDBUtils.close(conn);
					}
					String emails = emailList.getBuffer().toString();
					int strlen = emails.length();
					if(strlen >0)
						emails= emails.substring(0, strlen -1);


					return emails;
				}

   /**
 * @param edge_problem_id
 * @param conn
 * @return
 * @throws SQLException
 * @throws Exception
 */
/**
    * @roseuid 427B9CF102BD
    * This method gets the default owner records which is assigned when an issue
    * is originally submitted
    */

	public static Vector getOwnerdata(ETSMWIssue currentRecord) throws SQLException, Exception {
			Vector ownerdataRecords= new Vector();
			//DbConnect dbConnect = null;
			Connection conn=null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				
				conn = ETSDBUtils.getConnection();
				
				StringBuffer sqlbuffer = new StringBuffer();
				sqlbuffer.append("select * from ETS.ets_owner_data  WHERE data_id=");
				sqlbuffer.append("'" + currentRecord.issueTypeId + "'");

				String temp = sqlbuffer.toString();

				stmt = conn.createStatement();

				rs = stmt.executeQuery(sqlbuffer.toString());
				while(rs.next()){
					ETSMWOwnerRecord cqrec = new ETSMWOwnerRecord();
					cqrec.setOwner_id(rs.getString("ISSUE_OWNER_ID"));
					cqrec.setOwner_email(rs.getString("ISSUE_OWNER_EMAIL"));
					cqrec.setBackupOwner_id(rs.getString("ISSUE_BK_OWNER_ID"));
					cqrec.setBackupOwner_email(rs.getString("ISSUE_BK_OWNER_EMAIL"));					
					ownerdataRecords.addElement(cqrec);
				}

			} catch (SQLException e) {
				/*if (dbConnect != null) {
					dbConnect.removeConn(e);
					dbConnect = null;
				}*/
				printErr(getStackTrace(e));
				throw e;
			} catch (Exception e) {
				printErr(getStackTrace(e));
				throw e;
			} finally {
				/*if (dbConnect != null) {
					dbConnect.closeConn();
				}*/

				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
				ETSDBUtils.close(conn);
			}
			return ownerdataRecords;
		}

   /**
    * @roseuid 427B9CFC004C
    */
   public Vector getOwnerRecords(ETSMWIssue issue) throws SQLException, Exception
   {
	Vector ownerdataRecords=null;
			//DbConnect dbConnect = null;
			Statement stmt = null;
			ResultSet rs = null;
			Connection conn=null;

			try {
				
				conn = ETSDBUtils.getConnection();
				

				StringBuffer sqlbuffer = new StringBuffer();
				sqlbuffer.append("select * from ETS.ets_owner_cq  WHERE edge_problem_id=");
				sqlbuffer.append("'" + issue.edge_problem_id + "'");
				sqlbuffer.append(")");


				stmt = conn.createStatement();

				rs = stmt.executeQuery(sqlbuffer.toString());
				while(rs.next()){
					ETSMWOwnerRecord cqrec = new ETSMWOwnerRecord();
					cqrec.setOwner_id(rs.getString("ISSUE_OWNER_ID"));
					cqrec.setOwner_email(rs.getString("ISSUE_OWNER_EMAIL"));
					ownerdataRecords.addElement(cqrec);
				}

			} catch (SQLException e) {
				/*if (dbConnect != null) {
					dbConnect.removeConn(e);
					dbConnect = null;
				}*/
				printErr(getStackTrace(e));
				throw e;
			} catch (Exception e) {
				printErr(getStackTrace(e));
				throw e;
			} finally {
				/*if (dbConnect != null) {
					dbConnect.closeConn();
				}*/

				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
				ETSDBUtils.close(conn);
			}
			return ownerdataRecords;
   }

   /**
    * @param command
    * @param object
    * @return boolean
    * @roseuid 427B9EFA01CC
    */
   public boolean update(String command, Object object)
   {
    return true;
   }
/**
 * @param string
 * @return
 */

}
