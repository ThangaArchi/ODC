package oem.edge.ets_pmo.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.sql.Timestamp;
import java.io.*;
import java.sql.*;

import oem.edge.ets_pmo.datastore.resource.Resource;
import oem.edge.ets_pmo.datastore.util.RTFData;
import  oem.edge.ets_pmo.mq.XMLProcessor;
import oem.edge.ets_pmo.common.ETSPMOGlobalInitialize;
import oem.edge.ets_pmo.common.mail.PostMan;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import oem.edge.ets_pmo.xml.GenerateIssueCRXML;
import oem.edge.ets_pmo.util.GenerationSructOfCRIssue;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*        forward                                                                   */
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

/**
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

public class populateETS_PMO {
	private static String CLASS_VERSION = "4.5.1";
	private Connection dbConnection = null;
	static Logger logger = Logger.getLogger(populateETS_PMO.class);

	public populateETS_PMO() throws Exception {
		 String driver_class = ETSPMOGlobalInitialize.getDriver();
			String dbm = ETSPMOGlobalInitialize.getDbName();
			String usr = ETSPMOGlobalInitialize.getDbUser();
			String pwd = ETSPMOGlobalInitialize.getDbPwd();
			
			Class.forName(driver_class).newInstance();
			dbConnection = DriverManager.getConnection(dbm, usr, pwd);
	}
	
	public boolean makeConnection() {
	    String driver_class = ETSPMOGlobalInitialize.getDriver();
		String dbm = ETSPMOGlobalInitialize.getDbName();
		String usr = ETSPMOGlobalInitialize.getDbUser();
		String pwd = ETSPMOGlobalInitialize.getDbPwd();
		boolean success=true;
		try {
			Class.forName(driver_class).newInstance();
			dbConnection = DriverManager.getConnection(dbm, usr, pwd);
		} catch (Exception e) {
            success=false;
		}
		return success;
	}
	
	public void cleanUpConnection()  {
	    if (dbConnection!=null)
	    {
	        try {
                dbConnection.close();
            } catch (SQLException e) {
            }
	        dbConnection = null;
	    }
	}

	public static void callDummyInsert(Connection conn)
		throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer();

		try {
			// sQuery.append("INSERT INTO ets.ETS_PMO_MAIN (PMO_ID,PROJECT_ID,PARENT_ID,NAME,TYPE) VALUES ('r1','Bugati','bugati','codename1','clientname1')");
			// sQuery.append("INSERT INTO ets.ETS_PMO_RESOURCE PMO_ID, PROJECT_ID, PARENT_ID, USER_ID, ");
			stmt = conn.createStatement();
			logger.debug(sQuery);
			stmt.executeUpdate(sQuery.toString());
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			sQuery = null;
		}
	}

	public static void main(String[] args) {
  
		try {
			PropertyConfigurator.configure(args[0]);
			/* if(args.length < 1){
							System.out.println(" Usage : populateETS_PMO <PropertyFileLocation> " );
							System.exit(0);
						}*/

			ETSPMOGlobalInitialize glob = new ETSPMOGlobalInitialize();
			populateETS_PMO tdb = new populateETS_PMO();
			PostMan postman = new PostMan();
			
			tdb.sendIssueCRInfoInMailToProjMgr("v2sat1-1121326456233", "shingte@us.ibm.com", null, false);

			// System.out.println(tdb.RetrieveProjectManagerIdForThisTransaction("ETSCRID123-1087573324535", true));
			/*	Vector v = tdb.selectNewlyCreatedRecords();
			  if(v != null){
				for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
			         System.out.println(e.nextElement());
			
			     }
			     tdb.callCreateIssueCRInGenerateIssueCRXML(v);
			     }
			     
			     */
			/*    Vector v = tdb.selectNewlyUpdatedRecords();
			    if(v != null){
							for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
						         System.out.println(e.nextElement());
			
						     }
						     tdb.callUpdateIssueCRInGenerateIssueCRXML(v);
			    }
			    else System.out.println("The vector is null");
			    */
			/*    Vector v =  new Vector();
			    tdb.GenerationofFamily("00612A73D63E47D8AFAFC99D9591975D", "2B76BBD577684946A332D6E116125EE3", v);
			    for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
			    	GenerationSructOfCRIssue gen = (GenerationSructOfCRIssue)e.nextElement();
			        System.out.println("ID :" + gen.getGenerationParentID() + " Type : " + gen.getGenerationParentType());
			
			    }
			   Vector v1 = tdb.RetrieveParentsForThisEts_id("00612A73D63E47D8AFAFC99D9591975D", "2B76BBD577684946A332D6E116125EE3", false);
				for (Enumeration e = v1.elements() ; e.hasMoreElements() ;) {
			        GenerationSructOfCRIssue gen = (GenerationSructOfCRIssue)e.nextElement();
			        System.out.println("rID :" + gen.getGenerationParentID() + " Type : " + gen.getGenerationParentType());
			
			    }
			    System.out.println(" " );
			   Vector v2 = tdb.RetrieveParentsForThisEts_id("00612A73D63E47D8AFAFC99D9591975D", "2B76BBD577684946A332D6E116125EE3", true);
				for (Enumeration e = v2.elements() ; e.hasMoreElements() ;) {
			        GenerationSructOfCRIssue gen = (GenerationSructOfCRIssue)e.nextElement();
			        System.out.println("rID :" + gen.getGenerationParentID() + " Type : " + gen.getGenerationParentType());
			    }*/
			//GenerationSructOfCRIssue gen = tdb.findParent("00612A73D63E47D8AFAFC99D9591975D", "2B76BBD577684946A332D6E116125EE3");
			//System.out.println("PArent id : " + gen.getGenerationParentID() + " ,parent type: " + gen.getGenerationParentType());

			// tdb.callDummySelect(dbConnection);
			/* tdb.populateProject(dbConnection, "pmoid","projid","parentid","name","type",1, 2, 3, "assign_type","calendar","cal_rank",
			 					"currency","currency_rank","published",null, null,
			 				 	"state","cb",null, null,"duration","work",
			 					"pc","rem_work","eetc","es","constraint",null);
			*/

			//Testing rtf
			/* if(tdb.dbConnection != null){
			 			//	tdb.populateResource("pmoid", "projid", "parentid", "userid", "user_name",
			 			//						"company_name", "security_level", "security_rank");
			 			
				   			File file = new File("C:\\temp\\yahoo.txt");
							 FileInputStream fin = new FileInputStream(file);
							
							 tdb.populateRTF("sfd", "11", "43",7, "typo", fin, (int)file.length());
							 tdb.callDummySelect(tdb.dbConnection);
								
			 }
			*/
			// Testing txn table's timeout update.
			//  tdb.checkTxnStatusforCandU(glob.getCR_TIMEOUTWINDOW());
			//Testing the nested loop to update pmo_id from txn_id
			//	tdb.updatePMO_IDfromACK("ETSISSUEIDSUBS1085317051752", "olagaalagi");
			//Testing if this txn record exists in the txn table. will be used
			// to find if i get a duplicate ack and the record got deleted bec the user who created the record accessed the 
			// record which had obtained an ack earlier.
			// 	System.out.println(" Does The test transaction id exists in the table? : " + tdb.DoesThisTxnIdExistInTxnTable("ETSISSUEIDSUBS1085317051752"));

			/*	Testing				 this method will handle following cases:-
			 *					 case 1)if this issue is in under this criflolder...then update
								//..this will return "nochange"
								//case 2)if i dont find this issue in thie crifolder..then insert 
								//..i have to generate a new ets_id
								//case 3)if i find this issue but not under this crifolder..then get the ets_id and update the parent_id to be this crifodler id
								//...here i get the ets_id as the return value
								// i am handling all the above commented cases here
			*/

			//System.out.println("rslt: " + tdb.IsExceptionInSameFormORDifferentFormBecauseOfThisSync( "olagaalagi", "C75C8094300244298768BD53FB850C2C", "2B76BBD577684946A332D6E116125EE3"));

			//Testing updating CRI info from PMO
/*
			tdb.updateCRIInfoFromPMO(
				"43ffbb4c033b11d98000d9bcfb5f805a",
				"e1c57fced69f11d880009ef646645a92",
				"e3565c78d69f11d880009ef646645a92",
				9888,
				//"Under Review",
				"Change",
				"yongmei's",
				"Normal",
				"CHANGEREQUEST","Open");
*/
			/*	Timestamp ts = new Timestamp(System.currentTimeMillis())	;				 
				tdb.populateCRIInfo("olagaalagi", "2B76BBD577684946A332D6E116125EE3", "C75C8094300244298768BD53FB850C2C","sdf",  777 , 'P',
									 "subram", " notibm", "subs_me@yah", "324-3-23",
									 "stage_idchanged", "suu", ts, "class", 
									 "jungle book", "severity dont know", "changerequest" ,
									 "yahoo", "yahoomunipalli", "subs");*/

			/*String ets_Id, String pmo_Id, String pmo_Project_Id, String pmo_Parent_Id,  int Ref_No, char info_Src_Flag,
				String SubmitterName, String SubmitterCompany, String SubmitterEmail, String SubmitterPhone,
				String State_Action, String Submitter_IR_id, Timestamp Submission_Date, String Class,
				String title, String severity, String type, 
				String owner_IR_Id, String owner_Name, String Last_UserID) throws SQLException, Exception{*/

		} catch (Exception e) {
			logger.error("main(String[])", e);
		}

	}
	
	public String populateProject(
		String Pmo_Id,
		String proj_Id,
		String parent_Id,
		String Name,
		String Type,
		int Reference,
		int Rank,
		int Priority,
		String assign_Type,
		String Calendar,
		String Calendar_Rank,
		String currency,
		String currency_Rank,
		String published,
		Timestamp Est_Start,
		Timestamp Est_Finish,
		String State,
		String changeBrief,
		Timestamp start,
		Timestamp finish,
		String duration,
		String work,
		String percent_complete,
		String rem_Work,
		String EETC,
		String Effort_Spent,
		String constraint,
		Timestamp const_Date,
		char isReportable,
		String current_finish_type,
		Timestamp current_finish,
		Timestamp baseline_finish,
		String ref_code)
		throws SQLException, Exception {

		/*	if(Pmo_Id == null){// As Pmo_Id is null, I am adding proj_Id to make the primary field not null
					Pmo_Id = "null:-:" + proj_Id;	
			} */
		StringBuffer sQuery = new StringBuffer();

		String est_fin = null;
		if (Est_Finish != null) {
			est_fin = "'" + Est_Finish + "'";
		}
		String est_start = null;
		if (Est_Start != null) {
			est_start = "'" + Est_Start + "'";
		}
		String fin = null;
		if (finish != null) {
			fin = "'" + finish + "'";
		}
		String sta = null;
		if (start != null) {
			sta = "'" + start + "'";
		}
		String curr_fin = null;
		if (current_finish != null) {
			curr_fin = "'" + current_finish + "'";
		}
		String base_fin = null;
		if (baseline_finish != null) {
			base_fin = "'" + baseline_finish + "'";
		}
		sQuery.append(
			"INSERT INTO ets.ETS_PMO_MAIN (PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, NAME,"
				+ " TYPE, REFERENCE, RANK, PRIORITY, ASSIGN_TYPE, CALENDAR, CALENDAR_RANK,"
				+ " CURRENCY, CURRENCY_RANK, PUBLISHED,EST_START, EST_FINISH, STATE, CHANGE_BRIEF, START, "
				+ " FINISH, DURATION, WORK,"
				+ " PERCENT_COMPLETE, REM_WORK, EETC, EFFORT_SPENT, CONST, IS_REPORTABLE, LAST_TIMESTAMP, "
				+ "CURR_FINISH_TYPE, CURR_FINISH, BASE_FINISH, RPM_PROJECT_CODE ) VALUES ('"
				+ Pmo_Id
				+ "','"
				+ proj_Id
				+ "','"
				+ parent_Id
				+ "','"
				+ Name
				+ "','"
				+ Type
				+ "',"
				+ Reference
				+ ","
				+ Rank
				+ ","
				+ Priority
				+ ",'"
				+ assign_Type
				+ "','"
				+ Calendar
				+ "','"
				+ Calendar_Rank
				+ "','"
				+ currency
				+ "','"
				+ currency_Rank
				+ "','"
				+ published
				+ "',"
				+ est_start
				+ ","
				+ est_fin
				+ ",'"
				+ State
				+ "','"
				+ changeBrief
				+ "',"
				+ sta
				+ ","
				+ fin
				+ ",'"
				+ duration
				+ "','"
				+ work
				+ "','"
				+ percent_complete
				+ "','"
				+ rem_Work
				+ "','"
				+ EETC
				+ "','"
				+ Effort_Spent
				+ "','"
				+ constraint
				+ "','"
				+ isReportable
				+ "',"
				+ "current timestamp"
				+ ",'"
				+ current_finish_type
				+ "',"
				+ curr_fin
				+ ","
				+ base_fin
				+ ",'"
				+ ref_code
				+ "'"
				+ ")");

		//I AM LEAVING EST_START AND EST_FINSH...ETC FOR NOW SUBU
		try {
			executeUpdateStmt(sQuery.toString());
		} catch (SQLException e) {
			logger.error(
				"SQLException in populateProject by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		}

		return sQuery.toString();
	}

	public void populateResource(
		String Pmo_Id,
		String proj_Id,
		String parent_Id,
		String userID,
		String userName,
		String company_name,
		String security_level,
		String security_Rank)
		throws SQLException, Exception {
		if (Pmo_Id == null) {
			// As Pmo_Id is null, I am adding proj_Id to make the primary field not null
			Pmo_Id = "null:-:" + proj_Id;
		}

		StringBuffer sQuery = new StringBuffer();
		sQuery.append(
			"INSERT INTO ets.ETS_PMO_RESOURCE (PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, USER_ID,"
				+ " USER_NAME, COMPANY_NAME, SECURITY_LEVEL, SECURITY_RANK,LAST_TIMESTAMP ) VALUES ('"
				+ Pmo_Id
				+ "','"
				+ proj_Id
				+ "','"
				+ parent_Id
				+ "','"
				+ userID
				+ "','"
				+ userName
				+ "','"
				+ company_name
				+ "','"
				+ security_level
				+ "','"
				+ security_Rank
				+ "',"
				+ "current timestamp"
				+ ")");
		//I AM LEAVING current timestamp...ETC FOR NOW SUBU
		try {
			executeUpdateStmt(sQuery.toString());
		} catch (SQLException e) {
			logger.error(
				"SQLException in populateResource by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		}

	}
	public void populateRTF(
		String Pmo_Id,
		String Pmo_Project_Id,
		String Parent_Pmo_Id,
		int RTF_Id,
		String RTF_Name,
		String RTF_AliasName,
		InputStream instr /*StringReader strReader*/
	, int length, boolean updateContent) throws SQLException, Exception {
		
		logger.debug("The data used for populating populateRTF : ");

		logger.debug("PmoId : " + Pmo_Id);
		logger.debug("Pmo_Project_Id : " + Pmo_Project_Id);
		logger.debug("Parent_Pmo_Id : " + Parent_Pmo_Id);
		logger.debug("RTF_Id : " + RTF_Id);
		logger.debug("RTF_Name : " + RTF_Name);
		logger.debug("RTF_AliasName : " + RTF_AliasName);
		
		
		boolean isExist = hasRTFData(Parent_Pmo_Id, Pmo_Project_Id, RTF_Id);

		if (RTF_Name != null) {
			if (RTF_Name.length() > 40) {
				RTF_Name = RTF_Name.substring(0, 40);
				logger.debug(
					"RTF_Name to be truncated to 40 chars : " + RTF_Name);
			}
		}
		if (RTF_AliasName != null) {
			if (RTF_AliasName.length() > 40) {
				RTF_AliasName = RTF_AliasName.substring(0, 40);
				logger.debug(
					"RTF_AliasName to be truncated to 40 chars : "
						+ RTF_AliasName);
			}
		}
		
		String sQuery=null;
		try {
			
			if (isExist)
			{
			   if (updateContent){
				 sQuery = "UPDATE ets.ETS_PMO_RTF set RTF_BLOB=? where PARENT_PMO_ID=? and PMO_PROJECT_ID=? and RTF_ID=?";
				 PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
				 pstmt.setBinaryStream(1, instr, length);
				 pstmt.setString(2, Parent_Pmo_Id);
				 pstmt.setString(3, Pmo_Project_Id);
				 pstmt.setInt(4, RTF_Id);
				 pstmt.executeUpdate();
				 pstmt.close();
			   }
			}
			else
			{
			  sQuery =
				"INSERT INTO ets.ETS_PMO_RTF (PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, RTF_ID, RTF_NAME, "
					+ "RTF_BLOB, LAST_TIMESTAMP, RTF_ALIAS_NAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			  if (instr == null) {
				sQuery =
					"INSERT INTO ets.ETS_PMO_RTF (PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, RTF_ID, RTF_NAME, "
						+ "LAST_TIMESTAMP, RTF_ALIAS_NAME) VALUES (?, ?, ?, ?, ?, ?, ?)";
			   }
		  	   PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
			  //  provide values for prepared statement and execute update 

			  pstmt.setString(1, Pmo_Id);
			  pstmt.setString(2, Pmo_Project_Id);
			  pstmt.setString(3, Parent_Pmo_Id);
			  pstmt.setInt(4, RTF_Id);
			  pstmt.setString(5, RTF_Name);
			  Timestamp ts = new Timestamp(System.currentTimeMillis());
			  if (instr != null) {
				pstmt.setBinaryStream(6, instr, length);
				pstmt.setTimestamp(7, ts);
				pstmt.setString(8, RTF_AliasName);
			  } else {
				pstmt.setTimestamp(6, ts);
				pstmt.setString(7, RTF_AliasName);
			  }
			  pstmt.executeUpdate();
			  pstmt.close();
			}
		} catch (Exception e) {
			logger.error(
				"Prepared Statement Exception in populateRTF: "
					+ e.getMessage());
			throw e;
		}
		logger.debug(
			"Finished inserting PMO_ID : "
				+ Pmo_Id
				+ " , RTF Name : "
				+ RTF_Name);
	}
	public void populateScoreCard(
		String Pmo_Id,
		String Pmo_Project_Id,
		String Parent_Pmo_Id,
		String Parent_Type,
		String Name,
		String Value)
		throws SQLException, Exception {
		if (Pmo_Id == null) {
			// As Pmo_Id is null, I am adding Pmo_Project_Id to make the primary field not null
			Pmo_Id = "null:-:" + Pmo_Project_Id;
		}
		StringBuffer sQuery = new StringBuffer();
		sQuery.append(
			"INSERT INTO ets.ETS_PMO_SCORE (PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, PARENT_TYPE,"
				+ "NAME, VALUE, LAST_TIMESTAMP) VALUES('"
				+ Pmo_Id
				+ "','"
				+ Pmo_Project_Id
				+ "','"
				+ Parent_Pmo_Id
				+ "','"
				+ Parent_Type
				+ "','"
				+ Name
				+ "','"
				+ Value
				+ "',"
				+ "current timestamp"
				+ ")");
		try {
			executeUpdateStmt(sQuery.toString());
		} catch (SQLException e) {
			logger.error(
				"SQLException in populateScoreCard by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		}

	}
	/* The method checks if a DOC is eligible for an update or not.
	 * @param	Doc_Id 				The DOCUMENT ID which is used to retrieve the doc information from the table
	 * 		 	Pmo_Id 				pmo id for this doc							
	 * 			Pmo_Project_Id		pmo project id
	 * 			Parent_Pmo_Id		parent pmo id
	 * 			Parent_Type			parent type
	 * 			Doc_Name			doc name
	 * 			Doc_Type			doc type
	 * 			Doc_Desc			doc description
	 * 			Owner_Id			owner of the doc
	 * 			Security_Level		Security level of the owner
	 * 			Version_Info		Version of the doc
	 * 			Publish_Date		The date doc got published
	 * 			Update_Date			The date doc got updated
	 * @return	Integer 
	 * 			-1	In this case, we have to update the 
	 * 					doc blob, update date, publish date, version info
	 * 			-2	In this case, we have to update the following doc elements :
	 * 					Pmo_Id 				pmo id for this doc							
	 * 					Pmo_Project_Id		pmo project id
	 * 					Parent_Pmo_Id		parent pmo id
	 * 					Parent_Type			parent type
	 * 					Doc_Name			doc name
	 * 					Doc_Type			doc type
	 * 					Doc_Desc			doc description
	 * 					Owner_Id			owner of the doc
	 * 					Security_Level
	 * 			-3	We have to update all the values related to the doc : blob and the other doc elements.		
	 * 			 0	Good News ! Doc hasnt changed
	 * 			 1	There is no record found for the Doc_Id 
	 */
	public int checkIfDocChangedSinceLastUpdateToETSTables(
		String Doc_Id,
		String Pmo_Id,
		String Pmo_Project_Id,
		String Parent_Pmo_Id,
		String Parent_Type,
		String Doc_Name,
		int Doc_Type,
		String Doc_Desc,
		String Owner_Id,
		String Security_Level,
		String Version_Info,
		Timestamp Publish_Date,
		Timestamp Update_Date
		)
		throws SQLException, Exception {
			
			int rtrn	= 0;	
			
			if (logger.isDebugEnabled()) {
				logger.debug("checkIfDocChangedSinceLastUpdate - step a");
				logger.debug("checkIfDocChangedSinceLastUpdate - PMO_ID"+ Pmo_Id);
				logger.debug("checkIfDocChangedSinceLastUpdate - PMO_PROJ_ID"+ Pmo_Project_Id);
				logger.debug("checkIfDocChangedSinceLastUpdate - DOC_ID"+ Doc_Id);
				logger.debug("checkIfDocChangedSinceLastUpdate - PARENT_PMO_ID"+ Parent_Pmo_Id);
				logger.debug("checkIfDocChangedSinceLastUpdate - PARENT_TYPE"+ Parent_Type);
				logger.debug("checkIfDocChangedSinceLastUpdate - DOC_NAME"	+ Doc_Name);
			}
			
			//trimming the String parameters
			if(Pmo_Id != null)
				Pmo_Id					= Pmo_Id.trim();
			if(Pmo_Project_Id != null)
				Pmo_Project_Id			= Pmo_Project_Id.trim();
			if(Doc_Id != null) 
				Doc_Id					= Doc_Id.trim();
			if(Parent_Pmo_Id != null)
				Parent_Pmo_Id			= Parent_Pmo_Id.trim();
			if(Parent_Type != null)
				Parent_Type				= Parent_Type.trim();
			if(Doc_Name != null)
				Doc_Name				= Doc_Name.trim();
			//trimming done
					
			
					Statement stmt = null;
					ResultSet rs = null;
					StringBuffer sQuery = new StringBuffer("");
					try {

						sQuery.append(
						"select PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, PARENT_TYPE, "
						+ "DOC_NAME, DOC_TYPE, "
						+ "PUBLISH_DATE, UPDATE_DATE from ets.ets_pmo_doc "
						+ "where DOC_ID='" + Doc_Id + "' with ur");
						stmt = dbConnection.createStatement();
						rs = stmt.executeQuery(sQuery.toString());
						
						String sqlPMO_ID			= null;
						String sqlPMO_PROJECT_ID	= null;
						String sqlPARENT_PMO_ID		= null;
						String sqlPARENT_TYPE		= null;
						String sqlDOC_NAME			= null;
						int sqlDOC_TYPE				= -1;
						Timestamp sqlPUBLISH_DATE	= null;
						Timestamp sqlUPDATE_DATE	= null;
						
						int i = 0;
						while (rs.next()) {
							sqlPMO_ID			= rs.getString("PMO_ID");
							sqlPMO_PROJECT_ID	= rs.getString("PMO_PROJECT_ID");
							sqlPARENT_PMO_ID	= rs.getString("PARENT_PMO_ID");
							sqlPARENT_TYPE		= rs.getString("PARENT_TYPE");
							sqlDOC_NAME			= rs.getString("DOC_NAME");
							sqlDOC_TYPE			= rs.getInt("DOC_TYPE");
							sqlPUBLISH_DATE		= rs.getTimestamp("PUBLISH_DATE");
							sqlUPDATE_DATE		= rs.getTimestamp("UPDATE_DATE");
							i ++;
						}
						if (i > 1) {
							logger.error("The following sql statement must fetch us only one record. Here we are fetching more" +
								"than 1 record. The SQL causing problems : " + sQuery.toString());
							throw new Exception() ;
						}
						// comparing the data fed in the function as parameters and the data retrieved 
						// by the select query
						
						if( i == 1){
						
						//trimming the String resulsets
						if(sqlPMO_ID != null)
							sqlPMO_ID 			= 	sqlPMO_ID.trim();
						if(sqlPMO_PROJECT_ID != null)
							sqlPMO_PROJECT_ID	=	sqlPMO_PROJECT_ID.trim();
						if(sqlPARENT_PMO_ID != null)
							sqlPARENT_PMO_ID	=	sqlPARENT_PMO_ID.trim();
						if(sqlPARENT_TYPE != null)
							sqlPARENT_TYPE		=	sqlPARENT_TYPE.trim();
						if(sqlDOC_NAME != null)
							sqlDOC_NAME			=	sqlDOC_NAME.trim();
						//trimming done
						logger.debug(		"The values for comparison for DOC_ID :" + Doc_Id
										+	" in the following format : \nValue From XML\tVs\tValue From Database");
										
						logger.debug(		"PMO_ID :\t"					+ Pmo_Id 			+ "\tVs\t" + sqlPMO_ID
										+	"\nPMO_PROJECT_ID :\t"			+ Pmo_Project_Id 	+ "\tVs\t" + sqlPMO_PROJECT_ID
										+	"\nPARENT_PMO_ID :\t"			+ Parent_Pmo_Id		+ "\tVs\t" + sqlPARENT_PMO_ID 
										+	"\nPARENT_TYPE :\t"				+ Parent_Type 		+ "\tVs\t" + sqlPARENT_PMO_ID
										+	"\nDOC_NAME :\t"				+ Doc_Name 			+ "\tVs\t" + sqlDOC_NAME		
										+	"\nPUBLISH_DATE :\t"			+ Publish_Date		+ "\tVs\t" + sqlPUBLISH_DATE
										+	"\nLAST_CHECKIN/UPDATE DATE :\t"+ Update_Date		+ "\tVs\t" + sqlUPDATE_DATE
									);
									
						// In this case, we need to update the new document blob
						if(	!sqlUPDATE_DATE.equals(Update_Date)					||
							!sqlPUBLISH_DATE.equals(Publish_Date)				)
							{
								rtrn = -1;
							}
						// This case arises when we have the blob as the same, but the doc itself is moved to some 
						// other folder. Lets just update all the below attributes with new values.
						if( !sqlPMO_ID.equalsIgnoreCase(Pmo_Id) 				|| 
							!sqlPMO_PROJECT_ID.equalsIgnoreCase(Pmo_Project_Id) ||
							!sqlPARENT_PMO_ID.equalsIgnoreCase(Parent_Pmo_Id)	||
							!sqlPARENT_TYPE.equalsIgnoreCase(Parent_Type)		||	
							!sqlDOC_NAME.equalsIgnoreCase(Doc_Name)			
	

	
							){
								if(rtrn == -1){	//The code executed the previous if loop. So we need to update 
												//the blob and all other attributes
									rtrn = -3;
								}
								else{// The code didnt execute the previous if loop. We just need to update the attributes and not the blob. 
									rtrn = -2; 
								}
										
							}
						}// if i == 1
						else if(i == 0){// here is the case when there is no record available for the doc_id in the 
										//database.
							logger.debug("No record found for the DOC ID : " + Doc_Id + "\t DOC NAME : " + Doc_Name);
							rtrn = 1;	
						}
					} catch (SQLException e) {
						logger.error(
							"SQLException in checkIfDocChangedSinceLastUpdateToETSTables by executing this command : "
								+ sQuery.toString()
								+ "and the exception is : "
								+ getStackTrace(e));
						throw e;
					} catch (Exception e) {
						throw e;
					} finally {
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
					
					return rtrn;
	}
/* The method checks if a DOC is eligible for an update or not.
	 * @param	Doc_Id 				The DOCUMENT ID which is used to retrieve the doc information from the table
	 * 		 	Pmo_Id 				pmo id for this doc							
	 * 			Pmo_Project_Id		pmo project id
	 * 			Parent_Pmo_Id		parent pmo id
	 * 			Parent_Type			parent type
	 * 			Doc_Name			doc name
	 * 			Doc_Type			doc type
	 * 			Doc_Desc			doc description
	 * 			Owner_Id			owner of the doc
	 * 			Security_Level		Security level of the owner
	 * 			Version_Info		Version of the doc
	 * 			Publish_Date		The date doc got published
	 * 			Update_Date			The date doc got updated
	 * @return	Integer 
	 * 			-1	In this case, we have to update the 
	 * 					doc blob, update date, publish date, version info
	 * 			-2	In this case, we have to update the following doc elements :
	 * 					Pmo_Id 				pmo id for this doc							
	 * 					Pmo_Project_Id		pmo project id
	 * 					Parent_Pmo_Id		parent pmo id
	 * 					Parent_Type			parent type
	 * 					Doc_Name			doc name
	 * 					Doc_Type			doc type
	 * 					Doc_Desc			doc description
	 * 					Owner_Id			owner of the doc
	 * 					Security_Level
	 * 			-3	We have to update all the values related to the doc : blob and the other doc elements.		
	 * 			 0	Good News ! Doc hasnt changed
	 * 			 1	There is no record found for the Doc_Id 
	 */

	public int checkIfCRIDocChangedSinceLastUpdateToETSTables(
		String Doc_Id,
		String Pmo_Id,
		String Pmo_Project_Id,
		String Parent_Pmo_Id,
		
		String Doc_Name,
		int Doc_Type,
		String Doc_Desc,
		String Owner_Id,
		Timestamp lastUpdateTimestamp
		
		
		)
		throws SQLException, Exception {
			
			int rtrn	= 0;	
			
			
			//trimming the String parameters
			Pmo_Id					= Pmo_Id.trim();
			Pmo_Project_Id			= Pmo_Project_Id.trim();
			 
			Doc_Id					= Doc_Id.trim();
			Parent_Pmo_Id			= Parent_Pmo_Id.trim();
			
			Doc_Name				= Doc_Name.trim();
			Doc_Desc				= Doc_Desc.trim();
			
			
			//trimming done
					
			
					Statement stmt = null;
					ResultSet rs = null;
					StringBuffer sQuery = new StringBuffer("");
					try {

						/*sQuery.append(
						"select PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, "
						+ "DOC_NAME, DOC_DESC "
						+ "from ETS.PMO_ISSUE_DOC "
						+ "where PMO_ID='" + Doc_Id + "'");*/
						sQuery.append("select PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, DOC_NAME, DOC_DESC, LAST_TIMESTAMP from ets.pmo_issue_doc where pmo_id='" + Doc_Id + "' with ur");
						stmt = dbConnection.createStatement();
						rs = stmt.executeQuery(sQuery.toString());
						
						String sqlPMO_ID			= null;
						String sqlPMO_PROJECT_ID	= null;
						String sqlPARENT_PMO_ID		= null;
						
						String sqlDOC_NAME			= null;
						
						String sqlDOC_DESC			= null;
			
						Timestamp sqllastUpdateTimestamp = null;
						int i = 0;
						while (rs.next()) {
							sqlPMO_ID			= rs.getString("PMO_ID");
							sqlPMO_PROJECT_ID	= rs.getString("PMO_PROJECT_ID");
							sqlPARENT_PMO_ID	= rs.getString("PARENT_PMO_ID");
							sqlDOC_NAME			= rs.getString("DOC_NAME");
							sqlDOC_DESC			= rs.getString("DOC_DESC");
							
							sqllastUpdateTimestamp = rs.getTimestamp("LAST_TIMESTAMP");
							i ++;
							if (logger.isDebugEnabled()) {
								logger.debug("checkIfCRIDocChangedSinceLastUpdate - step 4");
							}
						}
						if (i > 1) {
							logger.error("The following sql statement must fetch us only one record. Here we are fetching more" +
								"than 1 record. The SQL causing problems : " + sQuery.toString());
							throw new Exception() ;
						}
						// comparing the data fed in the function as parameters and the data retrieved 
						// by the select query
						
						if( i == 1){
						
						//trimming the String resulsets
						sqlPMO_ID 			= 	sqlPMO_ID.trim();
						sqlPMO_PROJECT_ID	=	sqlPMO_PROJECT_ID.trim();
						sqlPARENT_PMO_ID	=	sqlPARENT_PMO_ID.trim();
						sqlDOC_NAME			=	sqlDOC_NAME.trim();
						sqlDOC_DESC			=	sqlDOC_DESC.trim();
						
						//trimming done
						logger.debug(		"The values for comparison for CRIDOC_ID :" + Doc_Id
										+	" in the following format : \nValue From XML\tVs\tValue From Database");
										
						logger.debug(		"PMO_ID :\t"					+ Pmo_Id 			+ "\tVs\t" + sqlPMO_ID
										+	"\nPMO_PROJECT_ID :\t"			+ Pmo_Project_Id 	+ "\tVs\t" + sqlPMO_PROJECT_ID
										+	"\nPARENT_PMO_ID :\t"			+ Parent_Pmo_Id		+ "\tVs\t" + sqlPARENT_PMO_ID 
										+	"\nDOC_NAME :\t"				+ Doc_Name 			+ "\tVs\t" + sqlDOC_NAME		
										+	"\nDOC_DESC :\t"				+ Doc_Desc 			+ "\tVs\t" + sqlDOC_DESC
						
										+   "\nLASTUPDATETIMESTAMP :\t"		+ lastUpdateTimestamp+"\tVs\t" + sqllastUpdateTimestamp);
									
									
						// In this case, we need to update the new document blob
						if(	!sqllastUpdateTimestamp.equals(lastUpdateTimestamp)){
								rtrn = -1;
							}
						// This case arises when we have the blob as the same, but the doc itself is moved to some 
						// other folder. Lets just update all the below attributes with new values.
						if( !sqlPMO_ID.equalsIgnoreCase(Pmo_Id) 				|| 
							!sqlPMO_PROJECT_ID.equalsIgnoreCase(Pmo_Project_Id) ||
							!sqlPARENT_PMO_ID.equalsIgnoreCase(Parent_Pmo_Id)	||
							!sqlDOC_NAME.equalsIgnoreCase(Doc_Name)				||
							!sqlDOC_DESC.equalsIgnoreCase(Doc_Desc)					
										
							
							){
								if(rtrn == -1){	//The code executed the previous if loop. So we need to update 
												//the blob and all other attributes
									rtrn = -3;
								}
								else{// The code didnt execute the previous if loop. We just need to update the attributes and not the blob. 
									rtrn = -2; 
								}
										
							}
						}// if i == 1
						else if(i == 0){// here is the case when there is no record available for the doc_id in the 
										//database.
							logger.debug("No record found for the CRIDOC ID : " + Doc_Id + "\t DOC NAME : " + Doc_Name);
							rtrn = 1;	
							
						}
					} catch (SQLException e) {
						logger.error(
							"SQLException in checkIfCRIDocChangedSinceLastUpdateToETSTables by executing this command : "
								+ sQuery.toString()
								+ "and the exception is : "
								+ getStackTrace(e));
						throw e;
					} catch (Exception e) {
						throw e;
					} finally {
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
					
					return rtrn;
	}
	public void updatePMODoc(String Doc_Id, InputStream Doc_BlobStream, Timestamp Update_Date,
		Timestamp Publish_Date,String version_Info, int compressedSize, int UncompressedSize) throws SQLException, Exception{
		try {
			String sQuery =
				"UPDATE  ETS.ETS_PMO_DOC  SET DOC_BLOB=?"
					+ ",UPDATE_DATE=?, PUBLISH_DATE=?, VERSION_INFO=?, UPLOAD_DATE=?, " 
					+ " LAST_TIMESTAMP=?, COMP_SIZE=?, UNCOMP_SIZE=?  where DOC_ID=?";

			PreparedStatement pstmt =
				dbConnection.prepareStatement(sQuery.toString());
			pstmt.setBinaryStream(1, Doc_BlobStream, UncompressedSize);
			pstmt.setTimestamp(2, Update_Date);
			pstmt.setTimestamp(3, Publish_Date);
			pstmt.setString(4, version_Info);
			pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			pstmt.setInt(7, compressedSize);
			pstmt.setInt(8, UncompressedSize);
			pstmt.setString(9, Doc_Id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException sqle) {
			logger.error(
				"Prepared Statement exception in updatePMODoc: "
					+ sqle.getMessage());
			throw sqle;
		} catch (Exception e) {
			throw e;
		}
	}
	
public void updatePMODoc(String Doc_Id, String Pmo_Id, String Pmo_Project_Id, String Parent_Pmo_Id, 
						String Parent_Type, String Doc_Name, int Doc_Type, String Doc_Desc,
						String Owner_Id, String Security_Level
					) throws SQLException, Exception{
//						For project level docs, I get doc_desc = "" , bec the xml doesnt have any tag called ATTACHMENT in project level unlike WBS level
						if(Doc_Name != null){
									Doc_Name = Doc_Name.trim();
								}
								if(Doc_Desc != null){
									Doc_Desc = Doc_Desc.trim();
								}
								if(Doc_Name == null || Doc_Name.equalsIgnoreCase("")){
									Doc_Name = Doc_Desc;
								}
								if(Doc_Desc == null || Doc_Desc.equalsIgnoreCase("")){
									Doc_Desc = Doc_Name;
								}
	try {
		String sQuery =
			"UPDATE  ETS.ETS_PMO_DOC  SET PMO_ID=?, PMO_PROJECT_ID=?, PARENT_PMO_ID=?, PARENT_TYPE=?, DOC_NAME=?, "
				+ "DOC_TYPE=?, DOC_DESC=?, OWNER_ID=?, SECURITY_LEVEL=?, UPLOAD_DATE=?, LAST_TIMESTAMP=? where DOC_ID=?";

		PreparedStatement pstmt =
			dbConnection.prepareStatement(sQuery.toString());
		pstmt.setString(1, Pmo_Id);
		pstmt.setString(2, Pmo_Project_Id);
		pstmt.setString(3, Parent_Pmo_Id);
		pstmt.setString(4, Parent_Type);
		pstmt.setString(5, Doc_Name);
		pstmt.setInt(6, Doc_Type);
		pstmt.setString(7, Doc_Desc);
		pstmt.setString(8, Owner_Id);
		pstmt.setString(9, Security_Level);
		pstmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
		pstmt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
		pstmt.setString(12, Doc_Id);
		pstmt.executeUpdate();
		pstmt.close();
	} catch (SQLException sqle) {
		logger.error(
			"Prepared Statement exception in updatePMODoc: "
				+ sqle.getMessage());
		throw sqle;
	} catch (Exception e) {
		throw e;
	}
}

public void updatePMODoc(String Doc_Id, String Pmo_Id, String Pmo_Project_Id, String Parent_Pmo_Id, 
						String Parent_Type, String Doc_Name, int Doc_Type, String Doc_Desc,
						String Owner_Id, String Security_Level,InputStream Doc_BlobStream, Timestamp Update_Date,
						Timestamp Publish_Date,String version_Info, int compressedSize, int uncompressedSize 
					) throws SQLException, Exception{
//						For project level docs, I get doc_desc = "" , bec the xml doesnt have any tag called ATTACHMENT in project level unlike WBS level
						if(Doc_Name != null){
									Doc_Name = Doc_Name.trim();
								}
								if(Doc_Desc != null){
									Doc_Desc = Doc_Desc.trim();
								}
								if(Doc_Name == null || Doc_Name.equalsIgnoreCase("")){
									Doc_Name = Doc_Desc;
								}
								if(Doc_Desc == null || Doc_Desc.equalsIgnoreCase("")){
									Doc_Desc = Doc_Name;
								}
	try {
		String sQuery =
			"UPDATE  ETS.ETS_PMO_DOC  SET PMO_ID=?, PMO_PROJECT_ID=?, PARENT_PMO_ID=?, PARENT_TYPE=?, DOC_NAME=?, "
				+ "DOC_TYPE=?, DOC_DESC=?, OWNER_ID=?, SECURITY_LEVEL=?, UPLOAD_DATE=?, " 				+ "DOC_BLOB=?"
				+ ",UPDATE_DATE=?, PUBLISH_DATE=?, VERSION_INFO=?, COMP_SIZE=?, UNCOMP_SIZE=?," + 
				"LAST_TIMESTAMP=? where DOC_ID=?";

		PreparedStatement pstmt =
			dbConnection.prepareStatement(sQuery.toString());
		pstmt.setString(1, Pmo_Id);
		pstmt.setString(2, Pmo_Project_Id);
		pstmt.setString(3, Parent_Pmo_Id);
		pstmt.setString(4, Parent_Type);
		pstmt.setString(5, Doc_Name);
		pstmt.setInt(6, Doc_Type);
		pstmt.setString(7, Doc_Desc);
		pstmt.setString(8, Owner_Id);
		pstmt.setString(9, Security_Level);
		pstmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
		pstmt.setBinaryStream(11, Doc_BlobStream, uncompressedSize);
		pstmt.setTimestamp(12, Update_Date);
		pstmt.setTimestamp(13, Publish_Date);
		pstmt.setString(14, version_Info);
		pstmt.setInt(15, compressedSize);
		pstmt.setInt(16, uncompressedSize);
		pstmt.setTimestamp(17, new Timestamp(System.currentTimeMillis()));
		pstmt.setString(18, Doc_Id);
		pstmt.executeUpdate();
		pstmt.close();
	} catch (SQLException sqle) {
		logger.error(
			"Prepared Statement exception in updatePMODoc: "
				+ sqle.getMessage());
		throw sqle;
	} catch (Exception e) {
		throw e;
	}
}

public void populatePMODoc(
		String Pmo_Id,
		String Pmo_Project_Id,
		String Doc_Id,
		String Parent_Pmo_Id,
		String Parent_Type,
		String Doc_Name,
		int Doc_Type,
		char Is_Compressed,
		String Doc_Desc,
		InputStream Doc_BlobStream,
		String Owner_Id,
		String security_level,
		String version_Info,
		Timestamp Publish_Date,
		Timestamp Upload_Date,
		Timestamp Update_Date,
		int comp_Size,
		int uncomp_Size)
		throws SQLException, Exception {
			
			// For project level docs, I get doc_desc = "" , bec the xml doesnt have any tag called ATTACHMENT in project level unlike WBS level
		if(Doc_Name != null){
			Doc_Name = Doc_Name.trim();
		}
		if(Doc_Desc != null){
			Doc_Desc = Doc_Desc.trim();
		}
		if(Doc_Name == null || Doc_Name.equalsIgnoreCase("")){
			Doc_Name = Doc_Desc;
		}
		if(Doc_Desc == null || Doc_Desc.equalsIgnoreCase("")){
			Doc_Desc = Doc_Name;
		}

			try{
				String sQuery = "INSERT INTO ets.ETS_PMO_DOC (PMO_ID, PMO_PROJECT_ID, DOC_ID, PARENT_PMO_ID," + 
								" PARENT_TYPE, DOC_NAME, DOC_TYPE, IS_COMPRESSED, DOC_DESC, DOC_BLOB, OWNER_ID, " +
								"SECURITY_LEVEL, VERSION_INFO, PUBLISH_DATE, UPLOAD_DATE, UPDATE_DATE, COMP_SIZE," +
								"UNCOMP_SIZE, LAST_TIMESTAMP) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
								" ?, ?, ?, ?)";
				PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
				pstmt.setString(1, Pmo_Id);
				pstmt.setString(2, Pmo_Project_Id);
				pstmt.setString(3, Doc_Id);
				pstmt.setString(4, Parent_Pmo_Id);
				pstmt.setString(5, Parent_Type);
				pstmt.setString(6, Doc_Name);
				pstmt.setInt(7, Doc_Type);
				pstmt.setString(8, new Character(Is_Compressed).toString());
				pstmt.setString(9, Doc_Desc);
				pstmt.setBinaryStream(10, Doc_BlobStream,uncomp_Size);
				pstmt.setString(11, Owner_Id);
				pstmt.setString(12, security_level);
				pstmt.setString(13, version_Info);
				pstmt.setTimestamp(14, Publish_Date);
				pstmt.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
				pstmt.setTimestamp(16, Update_Date);
				pstmt.setInt(17, comp_Size);
				pstmt.setInt(18, uncomp_Size);
				pstmt.setTimestamp(19, new Timestamp(System.currentTimeMillis()));
				
				
				pstmt.executeUpdate();
				pstmt.close();
			}
			catch(SQLException sqle){
				logger.error("Prepared Statement exception in populatePMODoc :" +
					sqle.getMessage());
			throw sqle;
			}
			catch(Exception e){
				logger.error("Exception in populatePMODoc :" + 
					e.getMessage());
					throw e;
			}
		}
/*	
	public void populatePMODoc(
		String Pmo_Id,
		String Pmo_Project_Id,
		String Doc_Id,
		String Parent_Pmo_Id,
		String Parent_Type,
		String Doc_Name,
		int Doc_Type,
		char Is_Compressed,
		String Doc_Desc,
		String Doc_Blob,
		String Owner_Id,
		String security_level,
		String version_Info,
		Timestamp Publish_Date,
		Timestamp Upload_Date,
		Timestamp Update_Date,
		int comp_Size,
		int uncomp_Size)
		throws SQLException, Exception {
		String pub_date = null;
		if (Publish_Date != null) {
			pub_date = "'" + Publish_Date + "'";
		}
		String upl_date = null;
		if (Upload_Date != null) {
			upl_date = "'" + Upload_Date + "'";
		}
		String upd_date = null;
		if (Update_Date != null) {
			upd_date = "'" + Update_Date + "'";
		}

		StringBuffer sQuery = new StringBuffer();
		sQuery.append(
			"INSERT INTO ets.ETS_PMO_DOC (PMO_ID, PMO_PROJECT_ID, DOC_ID, PARENT_PMO_ID, PARENT_TYPE, "
				+ "DOC_NAME, DOC_TYPE, IS_COMPRESSED, DOC_DESC, DOC_BLOB, OWNER_ID,"
				+ "SECURITY_LEVEL, VERSION_INFO, PUBLISH_DATE, UPLOAD_DATE, UPDATE_DATE, "
				+ "COMP_SIZE, UNCOMP_SIZE, LAST_TIMESTAMP) VALUES ('"
				+ Pmo_Id
				+ "','"
				+ Pmo_Project_Id
				+ "','"
				+ Doc_Id
				+ "','"
				+ Parent_Pmo_Id
				+ "','"
				+ Parent_Type
				+ "','"
				+ Doc_Name
				+ "',"
				+ Doc_Type
				+ ",'"
				+ Is_Compressed
				+ "','"
				+ Doc_Desc
				+ "', blob('"
				+ Doc_Blob
				+ "') ,'"
				+ Owner_Id
				+ "','"
				+ security_level
				+ "','"
				+ version_Info
				+ "',"
				+ pub_date
				+ ","
				+ "current timestamp"
				+ ","
				+ upd_date
				+ ","
				+ comp_Size
				+ ","
				+ uncomp_Size
				+ ","
				+ "current timestamp"
				+ ")");

		try {
			executeUpdateStmt(sQuery.toString());
		} catch (SQLException e) {
			logger.error(
				"SQLException in populatePMODoc by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		}
	}
*/

    public String getProblemState(String stateAction, String type )
    {
    	String problemState = null;
		Hashtable htETStoPMOStates = null;
		
		if (stateAction==null)
			return null;
		
		if(type.equalsIgnoreCase("ISSUE")){
					htETStoPMOStates = ETSPMOGlobalInitialize.getHtFrontEndETStoDaemonIssueStates();
			}
			else{
					htETStoPMOStates = ETSPMOGlobalInitialize.getHtETStoPMOChangeRequestStates();
			}
		 problemState = (String)htETStoPMOStates.get(stateAction);
		 if (problemState==null)
		 	problemState = stateAction;
		return problemState;
    	
    }

	public void populateCRIInfo(
		String ets_Id,
		String pmo_Id,
		String pmo_Project_Id,
		String pmo_Parent_Id,
		int Ref_No,
		char info_Src_Flag,
		String SubmitterName,
		String SubmitterCompany,
		String SubmitterEmail,
		String SubmitterPhone,
		String State_Action,
		String Submitter_IR_id,
		Timestamp Submission_Date,
		String Class,
		String title,
		String severity,
		String type,
		String owner_IR_Id,
		String owner_Name,
		String Last_UserID, String IssueAccess)
		throws SQLException, Exception {

		try {
			logger.debug("Populating the new PCR  : " + title);
			/* fixin a bug */
			 String STATE_ACTION = State_Action;
			 String PROBLEM_STATE = null;
			 
			 
			//if (Class.equalsIgnoreCase("ISSUE")){
			//			}
			 if (STATE_ACTION==null)
			 {
			 	STATE_ACTION = "Submit" ;
				PROBLEM_STATE = "Open";
			 }
			 else
			 	PROBLEM_STATE = State_Action;
			 
			
			String sQuery =
				"INSERT INTO ets.PMO_ISSUE_INFO (ETS_ID, PMO_ID, PMO_PROJECT_ID,PARENT_PMO_ID, REF_NO, INFO_SRC_FLAG,  "
					+ "SUBMITTER_NAME, SUBMITTER_COMPANY, SUBMITTER_EMAIL, SUBMITTER_PHONE,"
					+ "STATE_ACTION, SUBMITTER_IR_ID, SUBMISSION_DATE, CLASS, TITLE, "
					+ "SEVERITY, TYPE, OWNER_IR_ID, OWNER_NAME, LAST_USERID, "
					+ "LAST_TIMESTAMP, PROBLEM_STATE, ISSUE_ACCESS ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
			PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);

			pstmt.setString(1, ets_Id);
			pstmt.setString(2, pmo_Id);
			pstmt.setString(3, pmo_Project_Id);
			pstmt.setString(4, pmo_Parent_Id);
			pstmt.setInt(5, Ref_No);
			pstmt.setString(6, new Character(info_Src_Flag).toString());
			pstmt.setString(7, SubmitterName);
			pstmt.setString(8, SubmitterCompany);
			pstmt.setString(9, SubmitterEmail);
			pstmt.setString(10, SubmitterPhone);
			pstmt.setString(11, STATE_ACTION);
			pstmt.setString(12, Submitter_IR_id);
			pstmt.setTimestamp(13, Submission_Date);
			pstmt.setString(14, Class);
			pstmt.setString(15, title);
			pstmt.setString(16, severity);
			pstmt.setString(17, type);
			pstmt.setString(18, owner_IR_Id);
			pstmt.setString(19, owner_Name);
			pstmt.setString(20, Last_UserID);
			pstmt.setTimestamp(21, new Timestamp(System.currentTimeMillis()));
			pstmt.setString(22, PROBLEM_STATE);
			pstmt.setString(23, IssueAccess);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			logger.error(
				"Prepared Statement exception in populateCRIInfo: "
					+ e.getMessage());
			throw e;
		}

	}
	/*
	 * I would be able to update only the ISSUE_STATE(and not the ACTION_STATE) for issues from pmo to ets . 
	 * For the other kinds of issues(from ets to pmo), i will be able to update the ACTION_NAME as well.
	 */
	public void populateCRIHistory(boolean ETStoPMO, String ets_Id, String userName, String Action_Name, 
	String Issue_State, String Future1)throws SQLException,  Exception{
		try{
		
		String sQuery =
									"INSERT INTO ets.PMO_ISSUE_HISTORY (EDGE_PROBLEM_ID," + 
									"ACTION_TS, USER_NAME, ACTION_NAME," +
									"ISSUE_STATE, FUTURE1) VALUES(?, ?, ?, ?, ?, ?)";
						PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
						pstmt.setString(1, ets_Id);
						pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
						pstmt.setString(3, userName);
						pstmt.setString(4, Action_Name);
						pstmt.setString(5, Issue_State);
						pstmt.setString(6, Future1);
						pstmt.executeUpdate();
						pstmt.close();
						
					} catch (Exception e) {
						logger.error(
							"Prepared Statement exception in populateCRIHistory: "
								+ e.getMessage());
						throw e;
					}
		}
	/*
	 * This function checks the last actionState in the ets.pmo_issue_history table and decides a new record in  
	 * pmo_issue_ history table. If it finds the record already in the table, it checks if the last Action taken 
	 * on the record is same as Action_Name. If no, it inserts a new record.
	 * If it doesnt find the record in the table, it inserts a new record.
	 * 
	 *
	 * @param 	ETStoPMO the direction of the Issue flow
	 * 			ets_Id	
	 * 			Action_Name
	 * 			Username
	 * 			Issue_state
	 * 			Future1
	 * @throws	SQLException, Exception
	 * 
	 * 
	 */	
	public void AddNewRecordInPMOIssueHistory(boolean ETStoPMO, String ets_Id, String Action_Name, String UserName,
										 String Issue_State, String Future1)
	throws SQLException, Exception{
		/*
		 *  check if the Action_name is old. 
		 */
		
		if(checkLastActionState(ets_Id, Issue_State) == true){
			logger.debug("Updating the record in ets.pmo_issue_history.\n" 
						+ "\t ets_ID :\t" + ets_Id
						+ "\t isETStoPMO :\t" + ETStoPMO 
						+ "\t Action_Name :\t" + Action_Name
						+ "\t UserName :\t" +UserName
						+ "\tIssue State:\t" +Issue_State 						+ "\tFuture1:\t" + Future1);							 
			updateAction(ets_Id, Action_Name, UserName);
		}
		
		else{// if the Action_name is new
			logger.debug("Creating a new record in ets.pmo_issue_history.\n" 
									+ "\t ets_ID :\t" + ets_Id
									+ "\t isETStoPMO :\t" + ETStoPMO 
									+ "\t Action_Name :\t" + Action_Name
									+ "\t UserName :\t" +UserName
									+ "\tIssue State:\t" +Issue_State 
									+ "\tFuture1:\t" + Future1);
			populateCRIHistory(ETStoPMO, ets_Id, UserName, Action_Name, Issue_State, Future1);
		}
		
	}
	/*
	 * 	Postcondition: Compares the ACTION_NAME with the SQL ResultSet value. If ISSUE_STATE is same as ACTION_NAME,
	 *	the function returns a true. Else false.
	 *	The fucntion generates an Exception if more than 1 resultset values are obtained.
	 * @param : ets_ID 		
	 * 			Issue_state the Issue state against which we will be comparing
	 * @return
	 * 			false : 	cond 1: if there is no record available for EDGE_PROBLEM_ID supplied in
	 * 						cond 2: if there is 1 record but the ACTION_NAME and ISSUE_STATE obtained
	 * 					 			in the SQL call are not the same.
	 * 			true  : 	if there is 1 record available for EDGE_PROBLEM_D supplied in and the Issue_state
	 * 						same as ISSUE_STATE obtained in the SQL call.
	 * 
	 * @throws 
	 * 			SQLException, Exception.
	 * 		   	Exception is thrown when the ResultSet > 1
	 *
	 */
	
	public boolean checkLastActionState(String ets_Id, String Issue_State)
	throws SQLException, Exception{
			 
		boolean rslt = false;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"SELECT ISSUE_STATE from ETS.PMO_ISSUE_HISTORY where ACTION_TS=" + 
				"(SELECT MAX(ACTION_TS) from ets.PMO_ISSUE_HISTORY where EDGE_PROBLEM_ID='" + ets_Id +"')");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			int i = 0;
			while (rs.next()) {
				String IssueStateFromDB = rs.getString(1);
				IssueStateFromDB = IssueStateFromDB.trim();
				if(IssueStateFromDB.equalsIgnoreCase(Issue_State)){
					rslt = true;
				}
				i++;
			}
			if (i > 1) {
				logger.error("The following sql statement must fetch us only one record. Here we are fetching more" +					"than 1 record. The SQL :" + sQuery.toString());
				rslt = false;
				throw new Exception() ;
			}

		} catch (SQLException e) {
			logger.error(
				"SQLException in checkLastActionState by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/
		return rslt;
			
	}
	/* Updates the ACTION_TS and USER_NAME in the ETS.PMO_ISSUE_HISTORY 
	 * using EDGE_PROBLEM_ID and ISSUE_STATE. This is a private method. 
	 * 
	 *
	 * @param 	ets_ID(EDGE_PROBLEM_ID)
	 * 			Action_Name
	 * @return SQLException, Exception
	 * 
	 */
	private void updateAction(
		String ets_Id,
		String Action_Name,
		String UserName)
		throws SQLException, Exception {

		try {
			String sQuery =
				"UPDATE  ETS.PMO_ISSUE_HISTORY  SET ACTION_TS=?"
					+ ",USER_NAME=? where ISSUE_STATE = ? and EDGE_PROBLEM_ID=?";

			PreparedStatement pstmt =
				dbConnection.prepareStatement(sQuery.toString());
			pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			pstmt.setString(2, UserName);
			pstmt.setString(3, Action_Name);
			pstmt.setString(4, ets_Id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException sqle) {
			logger.error(
				"Prepared Statement exception in checkAndUpdateLastActionState: "
					+ sqle.getMessage());
			throw sqle;
		} catch (Exception e) {
			throw e;
		}
		
	}
/*	public void populateCRIInfo(
		String ets_Id,
		String pmo_Id,
		String pmo_Project_Id,
		String pmo_Parent_Id,
		int Ref_No,
		char info_Src_Flag,
		String SubmitterName,
		String SubmitterCompany,
		String SubmitterEmail,
		String SubmitterPhone,
		String State_Action,
		String Submitter_IR_id,
		Timestamp Submission_Date,
		String Class,
		String title,
		String severity,
		String type,
		String description,
		String Comm_From_Cust)
		throws SQLException, Exception {

		try {
			logger.debug("Populating the PCR  : " + title);
			String sQuery =
				"INSERT INTO ets.PMO_ISSUE_INFO (ETS_ID, PMO_ID, PMO_PROJECT_ID,PARENT_PMO_ID, REF_NO, INFO_SRC_FLAG,  "
					+ "SUBMITTER_NAME, SUBMITTER_COMPANY, SUBMITTER_EMAIL, SUBMITTER_PHONE,"
					+ "STATE_ACTION, SUBMITTER_IR_ID, SUBMISSION_DATE, CLASS, TITLE, "
					+ "SEVERITY, TYPE, DESCRIPTION, COMM_FROM_CUST, "
					+ "LAST_TIMESTAMP) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
			pstmt.setString(1, ets_Id);
			pstmt.setString(2, pmo_Id);
			pstmt.setString(3, pmo_Project_Id);
			pstmt.setString(4, pmo_Parent_Id);
			pstmt.setInt(5, Ref_No);
			pstmt.setString(6, new Character(info_Src_Flag).toString());
			pstmt.setString(7, SubmitterName);
			pstmt.setString(8, SubmitterCompany);
			pstmt.setString(9, SubmitterEmail);
			pstmt.setString(10, SubmitterPhone);
			pstmt.setString(11, State_Action);
			pstmt.setString(12, Submitter_IR_id);
			pstmt.setTimestamp(13, Submission_Date);
			pstmt.setString(14, Class);
			pstmt.setString(15, title);
			pstmt.setString(16, severity);
			pstmt.setString(17, type);
			pstmt.setString(18, description);
			pstmt.setString(19, Comm_From_Cust);
			pstmt.setTimestamp(20, new Timestamp(System.currentTimeMillis()));

			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			logger.error(
				"Prepared Statement exception in populateCRIInfo ( with CommFromCust): "
					+ e.getMessage());
			throw e;
		}

	}
*/
	//with customer info							
	/*
	 * public void populateCRIInfo( String ets_Id, String pmo_Id, String pmo_Project_Id, String pmo_Parent_Id,  int Ref_No, char info_Src_Flag,
									String SubmitterName, String SubmitterCompany, String SubmitterEmail, String SubmitterPhone,
									String State_Action, String Submitter_IR_id, Timestamp Submission_Date, String Class,
									String title, String severity, String type, String description, String Comm_From_Cust) throws SQLException, Exception{
									
									
										String sub_date = null;
										if(Submission_Date != null)
											sub_date = "'" + Submission_Date + "'";
											
										StringBuffer sQuery = new StringBuffer();
										
										sQuery.append(	"INSERT INTO ets.PMO_ISSUE_INFO (ETS_ID, PMO_ID, PMO_PROJECT_ID,PARENT_PMO_ID,  REF_NO, INFO_SRC_FLAG, " +
										 				"SUBMITTER_NAME, SUBMITTER_COMPANY, SUBMITTER_EMAIL, SUBMITTER_PHONE," + 
										 				"STATE_ACTION, SUBMITTER_IR_ID, SUBMISSION_DATE, CLASS, TITLE, " + 
										 				"SEVERITY, TYPE, DESCRIPTION, COMM_FROM_CUST, " + 
										 				"LAST_TIMESTAMP) VALUES('" +
										 				ets_Id  + "','" + pmo_Id + "','" + pmo_Project_Id + "','"  + pmo_Parent_Id + "'," + Ref_No + ",'" + info_Src_Flag + "','" +  
										 				SubmitterName + "','" +  SubmitterCompany + "','" +  SubmitterEmail + "','" +  SubmitterPhone + "','" +  State_Action + "','" + 
										 				Submitter_IR_id + "'," +  sub_date + ",'" +  Class + "','" +  title + "','" +  severity + "','" +  type + "','" + description + "','" +  Comm_From_Cust + "','" +
										 				 "'," +  "current timestamp"+ ")");
									try{
	  										executeUpdateStmt(sQuery.toString());
	  									}
	  									catch(SQLException e){ 
	  												logger.error("SQLException in populateCRIInfo by executing this command : "  + sQuery.toString() + "and the exception is : "  + getStackTrace(e));
	  												throw e;
	  											}
								}
	*/

	public void updateCRIInfoFromPMO(
		String pmo_Id,
		String pmo_Project_Id,
		String pmo_Parent_Id,
		int Ref_No,
		//String State_Action,
		String Class,
		String title,
		String severity,
		String type, String problem_State,
		String owner_Id,  String owner_Name, 
		Resource res)
		throws SQLException, Exception {
		StringBuffer sQuery = new StringBuffer();

		sQuery.append(
			"UPDATE  ets.PMO_ISSUE_INFO  SET PMO_PROJECT_ID=?"
				+ ",PARENT_PMO_ID=?, REF_NO=? , "
				//+ "STATE_ACTION=?,"
				+ " CLASS=?, TITLE=?, "
				+ "SEVERITY=?, TYPE=?, PROBLEM_STATE=?, " 
				+ "OWNER_IR_ID=?, OWNER_NAME=? "
				//+ ", SUBMITTER_IR_ID=?, SUBMITTER_NAME=?, "
				//+ "SUBMITTER_COMPANY=?, SUBMITTER_EMAIL=?, SUBMITTER_PHONE=? "
				+ "where PMO_ID = ?");

		try {
			PreparedStatement pstmt =
				dbConnection.prepareStatement(sQuery.toString());
			pstmt.setString(1, pmo_Project_Id);
			pstmt.setString(2, pmo_Parent_Id);
			pstmt.setInt(3, Ref_No);
			//pstmt.setString(4, State_Action);
			pstmt.setString(4, Class);
			pstmt.setString(5, title);
			pstmt.setString(6, severity);
			pstmt.setString(7, type);
			pstmt.setString(8, problem_State);
			pstmt.setString(9, owner_Id); 
			pstmt.setString(10, owner_Name); 
			//pstmt.setString(11, res.getLogon_name());
			//pstmt.setString(12, res.getElement_name());
			//pstmt.setString(13, res.getCompany_name());
			//pstmt.setString(14, res.getEmail());
			//pstmt.setString(15, res.getPhone());
			pstmt.setString(11, pmo_Id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			logger.error(
				"SQLException in updateCRIInfoFromPMO by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		}

	}

	public int updateCRIInfoFromETS(String ets_Id, String comm_from_cust)
		throws SQLException, Exception {

		StringBuffer sQuery = new StringBuffer();
		sQuery.append(
			"UPDATE ets.PMO_ISSUE_INFO SET COMM_FROM_CUST='"
				+ comm_from_cust
				+ "', LAST_TIMESTAMP="
				+ "current timestamp"
				+ " WHERE ETS_ID='"
				+ ets_Id
				+ "'");

		int aa = -1;
		try {
			aa = executeUpdateStmt(sQuery.toString());
		} catch (SQLException e) {
			logger.error(
				"SQLException in populateScoreCard by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		}

		return aa;

	}
	// I should have only one record for the txnid in the transction table. If i have 0 or more that 1, say 2- i will return false
	public boolean DoesThisTxnIdExistInTxnTable(String TXNid)
		throws SQLException, Exception {
		boolean rslt = false;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select txn_id from ets.ets_pmo_txn where txn_id='" + TXNid + "' with ur");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			int i = 0;
			while (rs.next()) {
				rslt = true;
				i++;
			}
			if (i > 1) {
				rslt = false;
			}

		} catch (SQLException e) {
			logger.error(
				"SQLException in DoesThisTxnIdExistInTxnTable by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/
		return rslt;

	}

	public void updateFlagInETS_PMO_TXN(String TXNid, char flag)
		throws SQLException, Exception {

		StringBuffer sQuery = new StringBuffer();
		String str =
			"UPDATE ets.ETS_PMO_TXN SET FLAG='"
				+ flag
				+ "', LAST_TIMESTAMP="
				+ "current timestamp"
				+ " WHERE TXN_ID='"
				+ TXNid
				+ "'";

		sQuery.append(str);
		try {
			logger.debug("update sql="+sQuery.toString());
			executeUpdateStmt(sQuery.toString());
		} catch (SQLException e) {
			logger.error(
				"SQLException in updateFlagInETS_PMO_TXN by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		}

	}
	public void updatePMO_IDfromACK(String TXNid, String pmo_id, int ref_num)
		throws SQLException, Exception {
		StringBuffer sQuery = new StringBuffer();
		String str =
			"UPDATE ets.pmo_issue_info SET PMO_ID = '"
				+ pmo_id
				+ "', REF_NO = " + ref_num
				+ " where ETS_ID = ( select id from ets.ets_pmo_txn where txn_id = '"
				+ TXNid
				+ "')";
		logger.debug(
			"updating pmo_id from ack. Basically retrieving the ets_id from ets.ets_pmo_txn for the txn _id obtained in ack"
				+ " and setting the pmo_id with ets_id.\n");
		sQuery.append(str);

		try {
			executeUpdateStmt(sQuery.toString());
		} catch (SQLException e) {
			logger.error(
				"SQLException in updatePMO_IDfromACK by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		}

	}
	/*
	 * subu@us.ibm.com	Sandie, Which table should i go to look upo for Project Manager email id with PMO ProjectID in hand?
	sandieps@us.ib...	well, pmo id will give you the ets project id
	sandieps@us.ib...	you will then have to get the role id for that project id that has the priv for workspace owner
	sandieps@us.ib...	then you can look at the users to see who has that role id
	sandieps@us.ib...	then that will give you the ir id of the workspace owner
	sandieps@us.ib...	you can then get the email id from amt
	subu@us.ibm.com	All the role id and ir id are in which table?
	sandieps@us.ib...	the role id are in ets.ets_roles
	sandieps@us.ib...	the priv are in ets.ets_priv
	sandieps@us.ib...	the users are in ets.ets_users
	
	
	 */
	/* This method retrieves the proj manager if the flag for this transaction is 
	 * ETSPMOGlobalInitialize.getCR_CREATED_STATE_SENT()
	 * this means that the flag was CREATE and the info was Sent which converted th flag value from 
	 * 'C' to 'S'. Check the property file for this. 
	 * This method retruns null if the transactation id provided 
	 * was for UPDATE.
	 */
	public String RetrieveProjectManagerIdForThisTransaction(String pmo_proj_id)
		//boolean CheckCreateFlag)
		throws SQLException, Exception {
		String pmoprojmgr = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sQuery = "select USER_EMAIL from amt.users where IR_USERID = (select user_id from ets.ets_users where user_role_id= (select DISTINCT role_id from ets.ets_roles where role_name = 'Workspace Owner' and project_id = (select project_id from ets.ets_projects where pmo_project_id='"
			+ pmo_proj_id + "')))";
		try {
			logger.debug(sQuery);
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				pmoprojmgr = rs.getString(1);
				if (pmoprojmgr != null) {
					break;
				}

			}

		} catch (SQLException e) {
			logger.error(
				"SQLException in RetrieveProjectManagerIdIfFlagIsCreateForThisTransaction(String TXNid) by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/
		logger.info("PM ID="+pmoprojmgr);
		return pmoprojmgr;
	}
	public String retrievePMO_ID(String etsID) throws SQLException, Exception {
		String pmoID = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select pmo_id from ets.PMO_ISSUE_INFO where ets_id='"
					+ etsID
					+ "' with ur");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				pmoID = rs.getString(1);
				if (pmoID != null) {
					break;
				}

			}

		} catch (SQLException e) {
			logger.error(
				"SQLException in retrievePMO_ID by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/
		return pmoID;
	}
public String retrieveETS_ID(String pmoID) throws SQLException, Exception {
	String etsID = null;
	Statement stmt = null;
	ResultSet rs = null;
	StringBuffer sQuery = new StringBuffer("");
	try {

		sQuery.append(
			"select ets_id from ets.PMO_ISSUE_INFO where pmo_id='"
				+ pmoID
				+ "' with ur");
		stmt = dbConnection.createStatement();
		rs = stmt.executeQuery(sQuery.toString());

		while (rs.next()) {
			etsID = rs.getString(1);
			if (etsID != null) {
				break;
			}

		}

	} catch (SQLException e) {
		logger.error(
			"SQLException in retrievePMO_ID by executing this command : "
				+ sQuery.toString()
				+ "and the exception is : "
				+ getStackTrace(e));
		throw e;
	} catch (Exception e) {
		throw e;
	} finally {
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
	/***********/
	return etsID;
}
	/* I need this method when i am sending a issue or cr . This will help me find the 
	 * ResourceID for the projID . The resource ID needs to be used in the CR create/update message
	 * I will get the project level resource id if i try to have the were clause 
	 * with pmo_project_id and parent_pmo_id.
	 * 
	 * I should not be using this table for this. Ask Mujib to create a new Table for this.
	 * 
	 * @param projID PMO Project ID
	 * @return Vector Vector contains source,destination, repositoryApp, Version, pmoResourceID in this order
	 * @throws SQLException, Exception
	 * 				
	 */
	public Vector RetrieveXMLHeaderInfoforProjID(String projID)
		throws SQLException, Exception {
		/************/
		String userID = null;
		String source = null;
		String destination = null;
		String repositoryApp = null;
		String Version = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector v = new Vector();
		try {
			String str =
				"select user_id, user_name, company_name, security_level, security_rank from ets.ETS_PMO_RESOURCE where pmo_project_id='"
					+ projID
					+ "' and parent_pmo_id='"
					+ "projectuserid"
					+ "' with ur";
			logger.debug(
				"SQL Query used to Retrieve XML info(source, destination, app, version, resourceid) :"
					+ str);
			sQuery.append(str);
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {
				userID = rs.getString(1);
				destination = rs.getString(2);
				source = rs.getString(3);
				repositoryApp = rs.getString(4);
				Version = rs.getString(5);
				logger.debug("userID="+userID+",dest="+destination+",source="+source+",app="+repositoryApp+"version="+Version);
				v.add(source);
				v.add(destination);
				v.add(repositoryApp);
				v.add(Version);
				v.add(userID);
			}
			else
				logger.debug("nothing came out from retrieve XML header");

			

		} catch (SQLException e) {
			logger.error(
				"SQLException in RetrieveXMLHeaderInfoforProjID by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/

		return v;

	}
	
	public boolean hasRTFData(
			String ParentPmoId,
			String PmoProjectId,
			int rtf_id)
			throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select RTF_NAME from ets.ETS_PMO_RTF where PARENT_PMO_ID='"
					+ ParentPmoId
					+ "' and PMO_PROJECT_ID='"
					+ PmoProjectId
					+ "' and RTF_ID="
					+ rtf_id
					+ " with ur");

			logger.debug(sQuery.toString());
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			if (rs.next()) 
				return true;
			else
				return false;
		} catch (SQLException e) {
			logger.error(
				"SQLException in retrieveRTFData by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
	public Vector retrieveRTFData(
		String ParentPmoId,
		String PmoProjectId,
		int rtf_id)
		throws SQLException, Exception {
		Vector v = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select RTF_ID, RTF_NAME, RTF_BLOB from ets.ETS_PMO_RTF where PARENT_PMO_ID='"
					+ ParentPmoId
					+ "' and PMO_PROJECT_ID='"
					+ PmoProjectId
					+ "' and RTF_ID="
					+ rtf_id
					+ " with ur");

			logger.debug(sQuery.toString());
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			while (rs.next()) {
				if (v == null) {
					v = new Vector();
				}
				RTFData rtfD = new RTFData();
				rtfD.setId(rs.getString(1));
				rtfD.setName(rs.getString(2));
				Blob b = (Blob) rs.getBlob(3);
				if (b != null) {
					logger.debug("blob.size() :" + b.length());
					if (b.length() != 0) {
						byte by[] = b.getBytes((long) 1, (int) b.length());
						logger.debug("by[] is : " + new String(by));
						String value = new String(by);
						rtfD.setValue(value); //
						rtfD.setBlobData(value);
					}
				}
				v.add(rtfD);

			}
		} catch (SQLException e) {
			logger.error(
				"SQLException in retrieveRTFData by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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

		return v;
		/***********/
	}
	/* selecting all newly created and updated records from txn table. This information is used to 
	 * determine the records for which mq message needs to be sent to PMO
	 */
	/*
	* The vector returned has id and txn id together one after other.
	* So if there are 3 records with Flag  ='U'
	* I will have 6 records in the vector:-
	* ID1
	* TXNid1
	*  ID2
	* TXNid2
	*  ID3
	* TXNid3
	*/
	public Vector selectNewlyCreatedRecords(String cflag) throws SQLException, Exception {
		Vector v = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select ID, TXN_ID from ets.ETS_PMO_TXN where FLAG='"+cflag+"' with ur");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			while (rs.next()) {
				if (v == null) {
					v = new Vector();
				}

				v.add(rs.getString(1));
				v.add(rs.getString(2));
			}
		} catch (SQLException e) {
			logger.error(
				"SQLException in selectNewlyCreatedRecords() by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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

		return v;
		/***********/
	}

	/*
	 * The vector returned has id and txn id together one after other.
	 * So if there are 3 records with Flag  ='U'
	 * I will have 6 records in the vector:-
	 * ID1
	 * TXNid1
	 *  ID2
	 * TXNid2
	 *  ID3
	 * TXNid3
	 */
	public Vector selectNewlyUpdatedRecords(String uflag) throws SQLException, Exception {
		Vector v = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select ID, TXN_ID from ets.ETS_PMO_TXN where FLAG='"+uflag+"' with ur");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			while (rs.next()) {
				if (v == null) {
					v = new Vector();
				}
				v.add(rs.getString(1));
				v.add(rs.getString(2));
			}
		} catch (SQLException e) {
			logger.error(
				"SQLException in selectNewlyUpdatedRecords by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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

		return v;
		/***********/
	}
	/*
	 * This method will find the parents of the the etsCRid and put the structure in a vector 
	 */
	public Vector RetrieveParentsForThisEts_id(
		String pmoCR_id,
		String projectID,
		boolean IsCreate)
		throws SQLException, Exception {
		Vector CRIFolderIds = new Vector();
		//If this is update, i add this value in the vector too.
		//	if(IsCreate){
		GenerationSructOfCRIssue gen = new GenerationSructOfCRIssue();
		gen.setGenerationParentID(pmoCR_id);
		gen.setGenerationParentType("CRIFOLDER");
		CRIFolderIds.add(gen);
		//	}
		GenerationofFamily(pmoCR_id, projectID, CRIFolderIds);
		/*CRIFolderIds.add("7BEFBDFD8F284AF4B53886DB9022EBDD");
		CRIFolderIds.add("B915C4347D764E23A6F753A45F03A656");
		CRIFolderIds.add("374A743BCE324FF7B9DF705040992160");*/
		return CRIFolderIds;
	}
/* Selects all the Issues / Change Requests( with Update requests only ) from the table ets.PMO_ISSUE_INFO.
 * Following info are extracted from ets.PMO_ISSSUE_INFO :ETS_ID, PMO_PROJECT_ID, PMO_ID, PARENT_PMO_ID, CLASS
 * Also comments from Cust RTF(RTF 9) is retrieved.
 * @param Vector v Contains ets_id and txn_id of the records that need to be updated.
 * 					The ets_id and txn_id are in sequestial order in the following sequence :
 * 					ets_id1
 * 					txn_id1
 * 					ets_id2
 * 					txn_id2
 * 					ets_id3
 * 					txn_id3
 * 					ets_id4
 * 					txn_id4 etc.
 * 
 */
	public void callUpdateIssueCRInGenerateIssueCRXML(Vector v)
		throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = null;
		boolean b = false;

		try {
			for (Enumeration e = v.elements(); e.hasMoreElements();) {
				String etscrid = (String) e.nextElement();
				String txnid = (String) e.nextElement();
				GenerateIssueCRXML gxml = new GenerateIssueCRXML();

				sQuery = new StringBuffer("");
				sQuery.append(
					"select ETS_ID, PMO_PROJECT_ID, PMO_ID, PARENT_PMO_ID, CLASS, STATE_ACTION, SUBMITTER_NAME"
						+ " from ets.PMO_ISSUE_INFO  where ETS_ID = '"
						+ etscrid
						+ "' with ur");
				stmt = dbConnection.createStatement();
				rs = stmt.executeQuery(sQuery.toString());
				if (rs.next()) {
					String etsCR_Id = rs.getString(1);
					String project_id = rs.getString(2);
					String pmo_id = rs.getString(3);
					String parent_pmo_id = rs.getString(4);
					String ClassInfo = rs.getString(5);
					String stateAction = rs.getString(6);
					String submitterName = rs.getString(7);
					Vector vUpdateCR_RTF = null;
					if(ClassInfo.equalsIgnoreCase("CHANGEREQUEST")){
					vUpdateCR_RTF =
						(Vector) retrieveRTFData(pmo_id, project_id, 9);
					}
					else{
					vUpdateCR_RTF =
						(Vector) retrieveRTFData(pmo_id, project_id, 7);
					}
					String comm_from_cust = null;
					if (vUpdateCR_RTF != null) {
						Enumeration enum = vUpdateCR_RTF.elements();
						RTFData rtfD = (RTFData) enum.nextElement();
						comm_from_cust = (String) rtfD.getBlobData();
					} else {
						logger.error(
							"No RTFs available for this combination in ets_pmo_rtf table : PMO_ID ="
								+ pmo_id
								+ ", PARENT_PMO_ID = "
								+ parent_pmo_id
								+ " and PMO_PROJECT_ID = "
								+ project_id
								+ " ");
						continue;
					}

					Vector parents = null;
					if (pmo_id != null && project_id != null) {
						if (!(pmo_id.equalsIgnoreCase(""))
							&& !(project_id.equalsIgnoreCase(""))) {
							logger.debug("Retrieving parents for \t" + 
								"pmoid : " + pmo_id + "\tproj : " + project_id);
							//parents = RetrieveParentsForThisEts_id(pmo_id, project_id, false);
							parents =
								RetrieveParentsForThisEts_id(
									parent_pmo_id,
									project_id,
									false);
						}
					}
					if (parents != null) {
						logger.debug("Yahoo! Successful in retrieving the parent information.");
						for (Enumeration e1 = parents.elements();
							e1.hasMoreElements();
							) {
								GenerationSructOfCRIssue gen =
								(GenerationSructOfCRIssue) e1.nextElement();
								logger.debug(
									"ParentID :"
									+ gen.getGenerationParentID()
									+ " \tParentType : "
									+ gen.getGenerationParentType());
								
						}
					} else {
						logger.error(
							"	No parents for : "
								+ etscrid
								+ " . This is against the SystemCorp business rule. We need to send the CR/Issue in a CRIFolder\n "
								+ "	Not sending this CR/Issue to PMOffice. Instead updating the flag from 'C' to 'N' ");
						updateFlagInETS_PMO_TXN(txnid, 'N');
						continue;
					}

					logger.debug(
						"etsCR_Id : "
							+ etsCR_Id
							+ "\n projectid : "
							+ project_id
							+ "\n comm_from_customer : "
							+ comm_from_cust);
					Vector vHead = RetrieveXMLHeaderInfoforProjID(project_id);
					String PMOStateValue = null;
					if (ClassInfo.equalsIgnoreCase("ISSUE")) {
									Hashtable htETStoPMOIssueStates =
												ETSPMOGlobalInitialize.getHtETStoPMOIssueStates();
								
								

											
								PMOStateValue = (String) htETStoPMOIssueStates.get(stateAction);
								if (PMOStateValue != null) {
												PMOStateValue.trim();
									}
											
							}
					
						logger.debug("Sending an Update "+ ClassInfo + " to PMOffice.\nDetails: "
												+ ClassInfo + " ID \t\t: "
												+ etsCR_Id
												+ "\n PMO ID \t\t: "
												+ pmo_id
												+ "\n PROJECT ID \t\t: "
												+ project_id
												+ "\n PARENT PMO ID \t\t: "
												+ parent_pmo_id 
												+ "\n STATE_ACTION \t\t" 
												+ stateAction
												+ "\n PROBLEM STATE \t\t"
												+ PMOStateValue
												+ "\n COMMENTS FROM CUSTOMER \t\t: "
												+ comm_from_cust
												);
					
					b =
						gxml.UpdateIssueCR(
							ClassInfo.equalsIgnoreCase("CHANGEREQUEST"),// this param is going to determine if it is issue or change request. The param passes a true/false
							project_id,
							parents,
							etsCR_Id,
							ClassInfo,
							PMOStateValue,
							comm_from_cust,
							txnid,
							(String) retrievePMO_ID(etsCR_Id),
							(String) vHead.elementAt(4),
							(String) vHead.elementAt(0),
							(String) vHead.elementAt(1),
							(String) vHead.elementAt(2),
							(String) vHead.elementAt(3));
					logger.debug("back from gxml.UpdateIssueCR(param1, param2,...) ...");
				} // end of if loop
				if (b == true) {
					updateFlagInETS_PMO_TXN(
						txnid,
						ETSPMOGlobalInitialize
							.getCR_UPDATED_STATE_SENT()
							.charAt(
							0));
				} else {
					logger.warn(
						"UpdateIssueCR returned False. Possibility of MQ down");
				}
			}
		} catch (SQLException e) {
			logger.error(
				"SQLException in callUpdateIssueCRInGenerateIssueCRXML by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			logger.error("Exception in callUpdateIssueCRInGenerateIssueCRXML");
			throw e;
		} finally {
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
	public void callCreateIssueCRInGenerateIssueCRXML(Vector v)
		throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = null;
		boolean b = false;

		try {
			for (Enumeration e = v.elements(); e.hasMoreElements();) {
				String etscrid = (String) e.nextElement();
				String txnid = (String) e.nextElement();
				GenerateIssueCRXML gxml = new GenerateIssueCRXML();

				sQuery = new StringBuffer("");
				sQuery.append(
					"select ETS_ID, PMO_ID, PMO_PROJECT_ID,PARENT_PMO_ID,  REF_NO, INFO_SRC_FLAG,"
						+ "SUBMITTER_NAME, SUBMITTER_COMPANY, SUBMITTER_EMAIL, SUBMITTER_PHONE,"
						+ "STATE_ACTION,PROBLEM_STATE, SUBMITTER_IR_ID, SUBMISSION_DATE, CLASS, TITLE, "
						+ "SEVERITY, TYPE, DESCRIPTION, COMM_FROM_CUST, OWNER_IR_ID, OWNER_NAME, LAST_USERID, "
						+ "LAST_TIMESTAMP from ets.PMO_ISSUE_INFO  where ETS_ID = '"
						+ etscrid
						+ "'");
				logger.debug("createXML sql="+sQuery.toString());
				stmt = dbConnection.createStatement();
				rs = stmt.executeQuery(sQuery.toString());
				if (rs.next()) {
					String etsCR_Id = rs.getString(1);

					String pmo_id = rs.getString(2);
					String project_id = rs.getString(3);
					String parent_pmo_id = rs.getString(4);

					Vector parents = null;
					if (parent_pmo_id != null && project_id != null) {
						if (!(parent_pmo_id.equalsIgnoreCase(""))
							&& !(project_id.equalsIgnoreCase(""))) {
							logger.debug(
								"pmoid : "
									+ parent_pmo_id
									+ " proj : "
									+ project_id);
							parents =
								RetrieveParentsForThisEts_id(
									parent_pmo_id,
									project_id,
									true);
						}
					}
					if (parents != null) {

						for (Enumeration e1 = parents.elements();
							e1.hasMoreElements();
							) {

							GenerationSructOfCRIssue gen =
								(GenerationSructOfCRIssue) e1.nextElement();
							logger.debug(
								"rID :"
									+ gen.getGenerationParentID()
									+ " Type : "
									+ gen.getGenerationParentType());

						}
					} else {
						logger.warn(
							"No parents for : "
								+ etscrid
								+ " . This is against the SystemCorp business rule. We need to send the CR/Issue in a CRIFolder");
						logger.warn(
							"Not sending this CR/Issue to PMOffice. Instead updating the flag from 'C' to 'N' ");
						updateFlagInETS_PMO_TXN(txnid, 'N');
						continue;
					}

					String ref_no = rs.getString(5);
					String info_src_flag = rs.getString(6);
					String submitterName = rs.getString(7);
					String submitter_Company = rs.getString(8);
					String submitterMail = rs.getString(9);
					String submitterPhone = rs.getString(10);
					String stateAction = rs.getString(11);
					String problemState = rs.getString(12);
					String submitterIRID = rs.getString(13);
					Timestamp submissionDate = rs.getTimestamp(14);
					String classInfo = rs.getString(15);
					String title = rs.getString(16);
					String severity = rs.getString(17);
					logger.debug("severity : " + severity);
					//subu 4.5.1 fix: moved the code to a method called getMappedSeverity. 
					Hashtable htETStoPMOStates = null;
					//subu 4.5.1 fix : Recoded the procedure to get htETStoPMOStates depending on
					// ISSUE or CHANGEREQUEST
					if (classInfo.equalsIgnoreCase("CHANGEREQUEST")) {
						severity =
							getMappedSeverity(
								severity,
								ETSPMOGlobalInitialize
									.getHtChangeRequestRankRange());
						htETStoPMOStates =
							ETSPMOGlobalInitialize
								.getHtETStoPMOChangeRequestStates();
					} else if (classInfo.equalsIgnoreCase("ISSUE")) {
						severity =
							getMappedSeverity(
								severity,
								ETSPMOGlobalInitialize.getHtIssueRankRange());
						htETStoPMOStates =
							ETSPMOGlobalInitialize.getHtETStoPMOIssueStates();
					} else { //defaulted to CHANGEREQUEST
						severity =
							getMappedSeverity(
								severity,
								ETSPMOGlobalInitialize
									.getHtChangeRequestRankRange());
						htETStoPMOStates =
													ETSPMOGlobalInitialize
														.getHtETStoPMOChangeRequestStates();
					}
					logger.debug("severity's mapped value :" + severity);
					String type = rs.getString(18);
					String description = rs.getString(19);
					String comm_from_cust = rs.getString(20);
					String owner_IR_id = rs.getString(21);
					String ownerName = rs.getString(22);
					String lastUser = rs.getString(23);
					Timestamp lastTimeStamp = rs.getTimestamp(24);
					
				
					logger.debug("stateAction : " + stateAction);
					
					String ETStoPMOStateValue = null;
					if (stateAction != null && classInfo.equalsIgnoreCase("CHANGEREQUEST")) {

						
						ETStoPMOStateValue = (String) htETStoPMOStates.get(stateAction.trim());
						if (ETStoPMOStateValue != null) {
							stateAction = ETStoPMOStateValue.trim();
						}
					}
					/* subu 4.5.1 fix */
					/* Vulnerablity point for Change Request. Need to test with Phani when he gets back
					 * 
					 */
					if(stateAction != null && classInfo.equalsIgnoreCase("ISSUE")){
						ETStoPMOStateValue = (String) htETStoPMOStates.get(stateAction.trim());
						if (ETStoPMOStateValue != null) {
							ETStoPMOStateValue = ETStoPMOStateValue.trim();
							}
						}
					
					logger.debug("stateAction's mapped value: " + stateAction);
					logger.debug("Sending Creation of a "+ classInfo + " to PMOffice.\nThe values of the " + classInfo +"\nare\n"
							+ "etsCR_Id \t\t: "
							+ etsCR_Id
							+ "\n pmo_id \t\t: "
							+ pmo_id
							+ "\n projectid \t\t: "
							+ project_id
							+ "\n parent_pmo_id \t\t: "
							+ parent_pmo_id
							+ "\n ref_no \t\t: "
							+ ref_no
							+ "\n info_src_flag \t\t: "
							+ info_src_flag
							+ "\n submitterName \t\t: "
							+ submitterName
							+ " \nsubmitter_Company \t\t: "
							+ submitter_Company
							+ "\n , submitterMail \t\t: "
							+ submitterMail
							+ "\n submitterPhone \t\t: "
							+ submitterPhone
							+ "\n stateAction \t\t: "
							+ ETStoPMOStateValue
							+ "\n problemState \t\t:" 
							+ problemState
							+ "\n submitterIRID \t\t: "
							+ submitterIRID
							+ "\n , submissionDate \t\t: "
							+ submissionDate
							+ "\n ClassInfo \t\t: "
							+ classInfo
							+ "\n title \t\t: "
							+ title
							+ "\n severity \t\t: "
							+ severity
							+ "\n , type \t\t: "
							+ type
							+ "\n description \t\t: "
							+ description
							+ "\n comm_from_cust \t\t: "
							+ comm_from_cust
							+ "\n owner_IR_id \t\t: "
							+ owner_IR_id
							+ "\n  ownername \t\t:"
							+ ownerName
							+ "\n lastUser \t\t:"
							+ lastUser
							+ "\n lastTimeStamp \t\t: "
							+ lastTimeStamp.toString());
							
					/*
					 * If Issue, I send the problemState. Else I send the stateAction
					 * 
					 */
					stateAction = classInfo.equalsIgnoreCase("ISSUE")?ETStoPMOStateValue:stateAction;
					
					Vector vHead = RetrieveXMLHeaderInfoforProjID(project_id);
					//logger.debug("now enter createIssueCR");
					int iref=(ref_no==null)? 0 : Integer.parseInt(ref_no);
					b =
						gxml.CreateIssueCR(
							classInfo.equalsIgnoreCase("CHANGEREQUEST"), //this param may return true or false. true: then change request.false: ten issue
							project_id,
							iref, // Integer.parseInt(ref_no),
							parents,
							etsCR_Id,
							title,
							severity,
							stateAction,
							null, // stage_ID Rank ..we dont send any.
							submitterName,
							submissionDate,
							description,
							submitter_Company,
							submitterMail,
							submitterPhone,
							submitterIRID,
							classInfo,
							owner_IR_id,
							ownerName,
							lastUser,
							comm_from_cust,
							txnid,
							(String) vHead.elementAt(4),
							(String) vHead.elementAt(0),
							(String) vHead.elementAt(1),
							(String) vHead.elementAt(2),
							(String) vHead.elementAt(3));

					/*
					 * Here i am populating the comments from cust from the pmo_issue_info table into
					 * ets_pmo_rtf table..
					 */
					// populateRTF(pmo_id, project_id, parent_pmo_id, 9, (String)ETSPMOGlobalInitialize.getHtCRIRTF().get(new Integer(9)), (String)ETSPMOGlobalInitialize.getHtCRIRTF().get(new Integer(9)), null, -1);
					
					if (b == true) {
						logger.debug("CreateIssueCR sent to MQ successfully, now update thx table");
										updateFlagInETS_PMO_TXN(
											txnid,
											ETSPMOGlobalInitialize
												.getCR_CREATED_STATE_SENT()
												.charAt(
												0));
									} else {
										logger.warn(
											"CreateIssueCR returned False. Possibility of MQ down");
					}
				} // end of if loop
				else {
					logger.info("cannot find entry in PMO_ISSUE_INFO table with ets_id="+etscrid);
				}

			}
		} catch (SQLException e) {
			logger.error(
				"SQLException in callCreateIssueCRInGenerateIssueCRXML by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			;
		} finally {
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

	public void sendIssueCRInfoInMailToProjMgr(
		String pmo_txnid,
		String projMgrMailId,
		String otherUserIds,
		boolean isCreate)
		throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		String cc_users = null;
		boolean b = false;
		logger.debug("Sending a mail to Proj Mgr: " + projMgrMailId);
		try {

			//String txnid	= (String)e.nextElement();
            /*
			query="select m.NAME, p.PROJECT_NAME, i.REF_NO, i.SUBMITTER_NAME, i.SUBMITTER_EMAIL, i.SUBMISSION_DATE, "
				+"i.CLASS, i.TITLE, i.SEVERITY, i.DESCRIPTION, i.COMM_FROM_CUST, i.OWNER_NAME, i.OWNER_IR_ID, "
				+"i.LAST_USERID, i.LAST_TIMESTAMP "
				+"from ets.ETS_PMO_MAIN as m, ets.ETS_PROJECTS as p, ets.PMO_ISSUE_INFO as i "
				+"where i.ETS_ID=(select ID from ets.ETS_PMO_TXN where TXN_ID='"+pmo_txnid+"') and "
				+"i.PMO_PROJECT_ID=(select PMO_PROJECT_ID from ets.ETS_PMO_TXN where TXN_ID='"+pmo_txnid+"') and "
				+"m.PMO_ID=i.PMO_PROJECT_ID and p.PMO_PROJECT_ID=i.PMO_PROJECT_ID";
				*/
			query="select m.NAME, p.PROJECT_NAME, i.REF_NO, i.SUBMITTER_NAME, i.SUBMITTER_EMAIL, i.SUBMISSION_DATE, "
				+"i.CLASS, i.TITLE, i.SEVERITY, i.DESCRIPTION, i.COMM_FROM_CUST, i.OWNER_NAME, i.OWNER_IR_ID, "
				+"i.LAST_USERID, i.LAST_TIMESTAMP, a.IR_USERID "
				+"from ets.ETS_PMO_MAIN as m, ets.ETS_PROJECTS as p, ets.PMO_ISSUE_INFO as i, amt.USERS as a "
				+"where i.ETS_ID=(select ID from ets.ETS_PMO_TXN where TXN_ID='"+pmo_txnid+"') and "
				+"i.PMO_PROJECT_ID=(select PMO_PROJECT_ID from ets.ETS_PMO_TXN where TXN_ID='"+pmo_txnid+"') and "
				+"m.PMO_ID=i.PMO_PROJECT_ID and p.PMO_PROJECT_ID=i.PMO_PROJECT_ID and a.EDGE_USERID=i.LAST_USERID";
			/*
			sQuery = new StringBuffer("");
			sQuery.append(
				"select ETS_ID, PMO_ID, PMO_PROJECT_ID,PARENT_PMO_ID,  REF_NO, INFO_SRC_FLAG,"
					+ "SUBMITTER_NAME, SUBMITTER_COMPANY, SUBMITTER_EMAIL, SUBMITTER_PHONE,"
					+ "STATE_ACTION, SUBMITTER_IR_ID, SUBMISSION_DATE, CLASS, TITLE, "
					+ "SEVERITY, TYPE, DESCRIPTION, COMM_FROM_CUST, OWNER_IR_ID, OWNER_NAME, LAST_USERID, "
					+ "LAST_TIMESTAMP from ets.PMO_ISSUE_INFO  where ETS_ID = (select id from ets.ets_pmo_txn where txn_id = '"
					+ pmo_txnid
					+ "')");
					*/
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				String pmo_project_name = rs.getString(1);
				String project_name = rs.getString(2);
				String ref_no = rs.getString(3);
				String submitterName = rs.getString(4);
				String submitterEmail = rs.getString(5);
				Timestamp submissionDate = rs.getTimestamp(6);
				String type = rs.getString(7);
				String title = rs.getString(8);
				String severity = rs.getString(9);
				String description = rs.getString(10);
				String comm_from_cust = rs.getString(11);
				String ownerName = rs.getString(12);
				String owner_IR_id = rs.getString(13);
				String lastUser = rs.getString(16);
				Timestamp lastTimeStamp = rs.getTimestamp(15);
						
				
				StringBuffer sb = new StringBuffer();
				sb.append("\nE&TS Workspace name : " + project_name);
				sb.append("\nRational PM Project name : " + pmo_project_name);
				sb.append("\nType : " + type);
				sb.append("\nTitle : " + title);
				if (ref_no != null)
					sb.append("\nReference Number : " + ref_no);
				sb.append("\nSeverity : " + severity);
				sb.append("\nSubmitter Name : " + submitterName);
				if (submitterEmail != null)
					sb.append("\nSubmitter Email : " + submitterEmail);
				sb.append("\nSubmission Date : " + submissionDate);
				
				if ("ISSUE".equalsIgnoreCase(type) && ownerName!=null)
					sb.append("\nOwner Name : " + ownerName);
				if ("ISSUE".equalsIgnoreCase(type) && owner_IR_id!=null)
				{
					sb.append("\nOwner Email : " + owner_IR_id);
					
					if (projMgrMailId.equalsIgnoreCase(owner_IR_id)==false)
					{
						if (otherUserIds==null)
							cc_users = owner_IR_id;
						else
							if (otherUserIds.indexOf(owner_IR_id)<0)
								cc_users = owner_IR_id+", "+otherUserIds;
					}
						
					
				}
				
				if (lastUser !=null)
					sb.append("\nLast modified by "+lastUser.trim()+ " on "+lastTimeStamp.toString());
				
				if (description != null)
					sb.append("\nDescription : " + description);
				if (comm_from_cust != null)
					sb.append("\nComments : \n" + comm_from_cust);

				
				logger.debug(
					"Sending mail to the project mangager : " + projMgrMailId);
				logger.debug("The content of the mail : " + sb.toString());
				
				
				if (isCreate == true) {
					PostMan.sendCreatedCRInfo(projMgrMailId, otherUserIds, sb.toString(), type); //mail to the ProjectManager
					
				} else {
					PostMan.sendUpdatedCRInfo(projMgrMailId, otherUserIds, sb.toString(), type);
				}

			} // end of if loop

		} catch (SQLException e) {
			logger.error(
				"SQLException in sendCreateIssueCRInfoInMailToProjMgr by executing this command : "
					+ query
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			;
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			
		}
	}

	/* NOTE userID is in TYPE in ets.ets_pmo_txn
	 *      etsCRID is in ID. hopefully the CRIDs dont conflict each other among different projects.
	 * 
	 */
	public String whatDoesTheStatusFlagSayForThisUserIDandCRID(
		String userId,
		String etsCRID)
		throws SQLException, Exception {
		/************/
		String rslt = "NOOPERATION";
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select TXN_ID, FLAG from ets.ETS_PMO_TXN where TYPE='"
					+ userId
					+ "' and ID='"
					+ etsCRID
					+ "'");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				String etsID = rs.getString(1);
				String flag = rs.getString(2);
				if (flag.equalsIgnoreCase("C") || flag.equalsIgnoreCase("U")) {
					rslt = "NONEDITABLE";
				} else if (
					flag.equalsIgnoreCase("N") || flag.equalsIgnoreCase("T")) {
					rslt = "FAILURE";
					this.deleteCRIwithUserID(userId);
				} else if (flag.equalsIgnoreCase("A")) {
					rslt = "SUCCESS";
					this.deleteCRIwithUserID(userId);
				}

			}

		} catch (SQLException e) {
			logger.error(
				"SQLException in whatDoesTheStatusFlagSayForThisUserIDandCRID by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/
		return rslt;

	}
	public void checkTxnStatusforCandU(int TIMEOUT)
		throws SQLException, Exception {
		// in hours
		TIMEOUT = TIMEOUT * 60 * 60 * 1000;
		
		/************/
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select ID,TXN_ID, LAST_TIMESTAMP from ets.ETS_PMO_TXN where FLAG='"
					+ ETSPMOGlobalInitialize.getCR_CREATED_STATE_SENT()
					+ "' or FLAG='"
					+ ETSPMOGlobalInitialize.getCR_UPDATED_STATE_SENT()
					+ "'");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				String etsID = rs.getString(1);
				logger.debug("current time: " + System.currentTimeMillis());
				logger.debug(
					" last timestamp : " + rs.getTimestamp(3).getTime());

				logger.debug(
					" The timeout interval is for: "
						+ TIMEOUT / 1000
						+ " secs and \n"
						+ " The difference between. currenttime and last timestamp is : "
						+ (System.currentTimeMillis()
							- rs.getTimestamp(3).getTime())
							/ 1000
						+ " secs");
				if (System.currentTimeMillis() - rs.getTimestamp(3).getTime()
					> TIMEOUT) { 
					logger.warn(
						"The ets_ID: "
							+ etsID
							+ "exceeded the timeout interval. Marking this as an Ack flag as I should let people work on it.");
					int returnVal = 0;
					if(XMLProcessor.MQManagerIsDown != 1){
						returnVal = GenerateIssueCRXML.ReSendIssueCRXML(rs.getString(2));
					}
					else{
						logger.warn("MQManager is down. Not resending the ISSUE/CR : " + rs.getString(2));
					}
					
					if(returnVal == 1){// Have reached 10 resend trials. Dont want to resend any more. The messages must 
										// be in queue. Changing the state to 'A' . The message is lost. Worst possible case.
						logger.error("Have reached 10 resend trials. Dont want to resend any more. The messages must\n" +  
						 			"be in queue. Changing the state to 'A' . The message is lost. Worst possible case." );
						updateFlagInETS_PMO_TXN(rs.getString(2), 'A');
					}
					
				} else {
					logger.debug(
						"\nThe ets_ID: "
							+ etsID
							+ " has not exceeded the timeout interval as the timeout is"
							+ " still greater than the difference calculated");
					logger.debug(
						"Hence, the ets_ID : "
							+ etsID
							+ " will not be updated from C/U to T");
				}
			}
		} catch (SQLException e) {
			logger.error(
				"SQLException in checkTxnStatusforCandU by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/

	}
	/*					 this method will handle following cases:-
	 *					 case 1)if this issue is in under this criflolder...then update
						//..this will return "nochange"
						//case 2)if i dont find this issue in thie crifolder..then insert 
						//..i have to generate a new ets_id
						//case 3)if i find this issue but not under this crifolder..then get the ets_id and update the parent_id to be this crifodler id
						//...here i get the ets_id as the return value
						// i am handling all the above commented cases here
	*/
	public String IsExceptionInSameFormORDifferentFormBecauseOfThisSync(
		String CRIid,
		String CRIFolderID,
		String project_ID)
		throws SQLException, Exception {
		String str = ":findnewETSid";
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select ets_id, parent_pmo_id from ets.PMO_ISSUE_INFO where pmo_id='"
					+ CRIid
					+ "' and pmo_project_id = '"
					+ project_ID
					+ "' with ur");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			int i = 0;
			while (rs.next()) {
				i++;
				String ets_Id = rs.getString(1);
				String parent_pmo_id = rs.getString(2);
				if (parent_pmo_id.equalsIgnoreCase(CRIFolderID))
					str = ets_Id + ":" + "nochange";
				else
					str = ets_Id + ":" + parent_pmo_id;
				// I am returning ets_id and parentid together

			}

			if (i > 1)
				return ":morethan1recordavailable";

		} catch (SQLException e) {
			logger.error(
				"SQLException in IsExceptionInSameFormORDiferentFormBecauseOfThisSync by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/
		return str;

	}
	public void populateCRIDoc(
		String ets_Id,
		String pmo_Id,
		String pmo_Proj_Id,
		String parent_Pmo_Id,
		String Doc_Desc,
		int Doc_Type,
		String Doc_Name,
		InputStream Doc_Blob,
		char info_src_flag,
		String last_Userid,
		int compressedSize,
		int uncompressedSize,
		Timestamp lastUpdateTimestamp)
		throws SQLException, Exception {
		String docMimeType = null;
		if(Doc_Name != null){
						if(docMimeType == null){
							docMimeType = "";
						}
						docMimeType = Doc_Name.substring(Doc_Name.lastIndexOf(".") + 1);
						logger.debug("Getting mime type for " + docMimeType);
						docMimeType = oem.edge.ets.fe.ETSMimeDataList.getMimeTypeByExtension(docMimeType);

						if (logger.isDebugEnabled()) {
							logger.debug("populateCRIDoc() - The doc mime type for PCR is "+ docMimeType);
						}
						if (Doc_Name.length()>128) {
							Doc_Name = Doc_Name.substring(0,127);
							logger.warn("DocName="+Doc_Name+" is truncated at max length 128");
						}
							
		}
		if(Doc_Desc != null)
		{
			if (Doc_Desc.length()>50) {
				Doc_Desc = Doc_Desc.substring(0,49);
				logger.warn("DocDesc="+Doc_Desc+" is truncated at max length 50");
			}
		}
		try {
			int docNo = getMaxDocNoForThisIssue(ets_Id);
			
			String sQuery = "INSERT INTO ets.PMO_ISSUE_DOC (ETS_ID, PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID,"  +
							"DOC_NAME, DOC_TYPE, DOC_NO, DOC_DESC, DOC_MIME, INFO_SRC_FLAG, LAST_USERID, LAST_TIMESTAMP)" +
							"VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			/*
			String sQuery = "INSERT INTO ets.PMO_ISSUE_DOC (ETS_ID, PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID,"  +
			"DOC_NAME, DOC_TYPE, DOC_NO, DOC_DESC, DOC_MIME, INFO_SRC_FLAG, LAST_USERID, LAST_TIMESTAMP)" +
			"VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			*/
			PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
			pstmt.setString(1, ets_Id);
			pstmt.setString(2, pmo_Id);
			pstmt.setString(3, pmo_Proj_Id);
			pstmt.setString(4, parent_Pmo_Id);
			pstmt.setString(5, Doc_Name);
			pstmt.setInt(6, Doc_Type);
			pstmt.setInt(7, docNo);
			pstmt.setString(8, Doc_Desc);
			/*
			pstmt.setBinaryStream(9, Doc_Blob, uncompressedSize);
			pstmt.setString(10, new Character(info_src_flag).toString());
			pstmt.setString(11, last_Userid);
			pstmt.setTimestamp(12, lastUpdateTimestamp);
			pstmt.setString(13, docMimeType);
			*/
			pstmt.setString(9, docMimeType);
			pstmt.setString(10, new Character(info_src_flag).toString());
			pstmt.setString(11, last_Userid);
			pstmt.setTimestamp(12, lastUpdateTimestamp);
			/*
			if (uncompressedSize>32*1024)
			 pstmt.setBinaryStream(13, Doc_Blob, uncompressedSize);
			else
			{
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream(512*1024);
			 	   int byteRead = readData(Doc_Blob,baos);
			 	    logger.info("docsize="+uncompressedSize+"bytes,readbytes="+byteRead+"bytes");
			 	    pstmt.setBytes(13,baos.toByteArray());
			 	    baos.flush();
			}
			*/
			//String str = new Character(info_src_flag).toString();
			//logger.info("info_src_flag="+str+",size="+str.length());
			logger.info("size="+uncompressedSize+",avail="+Doc_Blob.available());
			
			pstmt.executeUpdate();
			pstmt.close();
			
			logger.info("size1="+uncompressedSize+",avail1="+Doc_Blob.available());
			String str= "UPDATE ets.PMO_ISSUE_DOC SET DOC_BLOB=? where PMO_ID=?";
			pstmt = dbConnection.prepareStatement(str);
			pstmt.setBinaryStream(1, Doc_Blob, uncompressedSize);
			pstmt.setString(2, pmo_Id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException sqle) {
			logger.error(
				"PreparedStatementException in populateCRIDoc : "
					+ "and the exception is : "
					+ getStackTrace(sqle));
			throw sqle;
		}
		catch(Exception e){
			logger.error("Exception in populateCRIDoc : " + getStackTrace(e));
			throw e;
		}

	}
	
protected int readData(InputStream in, OutputStream out)
		throws IOException
	{
	    byte[] buffer = new byte[1024];
	    int totalRead = 0;
	    int bytesRead = 0;
	    while ((bytesRead = in.read(buffer)) != -1)
		{
		    out.write(buffer, 0, bytesRead);
		    //		    out.flush();
		    if (bytesRead > 0)
			{
			    totalRead += bytesRead;
			}
		}
	    return totalRead;
	}

public void updateCRIDoc(
	String ets_Id,
	String pmo_Proj_Id,
	String parent_Pmo_Id,
	String Doc_Desc,
	int Doc_Type,
	String Doc_Name,
	String last_Userid
	)throws SQLException, Exception {
		String docMimeType = null;
		if(Doc_Name != null){
				if(docMimeType == null){
						docMimeType = "";
					}
				docMimeType = Doc_Name.substring(Doc_Name.lastIndexOf(".") + 1);
				logger.debug("Getting mime type for  : " + docMimeType);
				docMimeType = oem.edge.ets.fe.ETSMimeDataList.getMimeTypeByExtension(docMimeType);
				
				if (Doc_Name.length()>128) {
					Doc_Name = Doc_Name.substring(0,127);
					logger.warn("DocName="+Doc_Name+" is truncated at max length 128");
				}
			}
		if(Doc_Desc != null)
		{
			if (Doc_Desc.length()>50) {
				Doc_Desc = Doc_Desc.substring(0,49);
				logger.warn("DocDesc="+Doc_Desc+" is truncated at max length 50");
			}
		}
	try {
		String sQuery = "UPDATE ETS.PMO_ISSUE_DOC SET PMO_PROJECT_ID=?, PARENT_PMO_ID=?,"  +
						"DOC_NAME=?, DOC_TYPE=?, DOC_DESC=?, LAST_USERID=?, LAST_TIMESTAMP=?, DOC_MIME=? WHERE ETS_ID=?";
						
		PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
		
		pstmt.setString(1, pmo_Proj_Id);
		pstmt.setString(2, parent_Pmo_Id);
		pstmt.setString(3, Doc_Name);
		pstmt.setInt(4, Doc_Type);
		pstmt.setString(5, Doc_Desc);
		pstmt.setString(6, last_Userid);
		pstmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
		pstmt.setString(8, docMimeType);
		pstmt.setString(9, ets_Id);
			
		pstmt.executeUpdate();
		pstmt.close();
	} catch (SQLException sqle) {
		logger.error(
			"PreparedStatementException in populateCRIDoc : "
				+ "and the exception is : "
				+ getStackTrace(sqle));
		throw sqle;
	}
	catch(Exception e){
		logger.error("Exceptin in populateCRIDoc : " + getStackTrace(e));
		throw e;
	}

}
	

public void updateCRIDoc(
		String ets_Id,
		InputStream Doc_Blob,
		int compressedSize,
		int uncompressedSize,
		Timestamp lastUpdateTimestamp)
		throws SQLException, Exception {
		try {
			String sQuery = "UPDATE ETS.PMO_ISSUE_DOC SET DOC_BLOB=?, LAST_TIMESTAMP=? WHERE ETS_ID=?" ;
			PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
			
			pstmt.setBinaryStream(1, Doc_Blob, uncompressedSize);
			pstmt.setTimestamp(2, lastUpdateTimestamp);
			pstmt.setString(3, ets_Id);
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException sqle) {
			logger.error(
				"PreparedStatementException in populateCRIDoc : "
					+ "and the exception is : "
					+ getStackTrace(sqle));
			throw sqle;
		}
		catch(Exception e){
			logger.error("Exceptin in populateCRIDoc : " + getStackTrace(e));
			throw e;
		}

	}
	public void populatePMOTransaction(
		String ets_Id,
		String Txn_Id,
		String Pmo_Project_Id,
		String userid,
		char flag)
		throws SQLException, Exception {
		/*	if(Pmo_Id == null){// As Pmo_Id is null, I am adding Pmo_Project_Id to make the primary field not null
					Pmo_Id = "null:-:" + Pmo_Project_Id;	
			}
			*/

		//Id in txn am facing some problems..cant insert in this field. I am adding the pmo_id in pmo_project_id now.
		StringBuffer sQuery = new StringBuffer();
		sQuery.append(
			"INSERT INTO ets.ETS_PMO_TXN ( TXN_ID, PMO_PROJECT_ID, ID, TYPE, FLAG, LAST_TIMESTAMP) VALUES('"
				+ Txn_Id
				+ "','"
				+ Pmo_Project_Id
				+ "','"
				+ ets_Id
				+ "','"
				+ userid
				+ "','"
				+ flag
				+ "',"
				+ "current timestamp"
				+ ")");
		try {
			executeUpdateStmt(sQuery.toString());
		} catch (SQLException e) {
			logger.error(
				"SQLException in populatePMOTransaction by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		}
	}
	private int executeUpdateStmt(String sQuery)
		throws SQLException, Exception {
		Statement stmt = null;
		int noOfRows = 0;
		//ResultSet rs = null;
		try {
			stmt = dbConnection.createStatement();
			logger.debug(sQuery);
			noOfRows = stmt.executeUpdate(sQuery);

		} catch (SQLException e) {

			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			sQuery = null;
		}
		return noOfRows;
	}
	public void callDummySelect(Connection conn)
		throws SQLException, Exception {
		/************/
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {
			sQuery.append(
				"select RTF_BLOB from ets.ets_pmo_rtf where PMO_ID='sfd'");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				Blob sUserID = rs.getBlob(1);
				//logger.debug(sUserID.getBinaryStream());
				String ls_str;
				BufferedReader ls_in =
					new BufferedReader(
						new InputStreamReader(sUserID.getBinaryStream()));

				try {
					while ((ls_str = ls_in.readLine()) != null) {
						logger.debug(ls_str);
						//strbuf.append(ls_str + "\n");

					}
				} catch (IOException e) {
					//	LogMessage.print("IOException in RTF2TextConverter",LogMessage.DEBUG_LEVEL1);
				}
			}
		} catch (SQLException e) {
			logger.error(
				"SQLException in callDummySelect by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/
	}
	public void deleteCRIwithFlag(String etsid, char Flag)
		throws SQLException, Exception {
		int noOfRows = 0;
		String sQuery = new String();

		// Working on ets.ETS_PMO_TXN				
		logger.debug(
			"Starting to Delete the records in ets.ETS_PMO_TXN for records with ID : "
				+ etsid);

		sQuery =
			"DELETE FROM ets.ETS_PMO_TXN WHERE ID ='"
				+ etsid
				+ "' and FLAG='"
				+ Flag
				+ "'";
		try {
			noOfRows = executeUpdateStmt(sQuery.toString());
		} catch (SQLException q) {
			logger.error(
				"SQLException in deleteCRIwithFlag by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(q));

			logger.error("deleteCRIwithFlag(String, char)", q);
		}
		if (noOfRows > 0) {
			logger.debug(
				"Finished executing delete on ets.ETS_PMO_TXN for records with ID : "
					+ etsid
					+ " Number of rows deleted: "
					+ noOfRows);
		} else {
			logger.debug("No rows to be deleted. A new Entry: " + etsid);
		}
	}
	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}
	public void deleteCRIwithUserID(String userid)
		throws SQLException, Exception {
		int noOfRows = 0;
		String sQuery = new String();

		// Working on ets.ETS_PMO_TXN				
		logger.debug(
			"Starting to Delete the record in ets.ETS_PMO_TXN  with userID : "
				+ userid);

		sQuery = "DELETE FROM ets.ETS_PMO_TXN WHERE TYPE ='" + userid + "'";
		try {
			noOfRows = executeUpdateStmt(sQuery.toString());
		} catch (SQLException q) {
			logger.error(
				"SQLException in deleteCRIwithUserID by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(q));

		}
		if (noOfRows > 0) {
			logger.debug(
				"Finished executing delete on ets.ETS_PMO_TXN for user_ID : "
					+ userid
					+ " Number of rows deleted: "
					+ noOfRows);
		} else {
			logger.debug("No rows to be deleted for userid:" + userid);
		}
	}
	public void deleteCRIwithETSCRIDAndUserID(String etsCRid, String userid)
		throws SQLException, Exception {
		int noOfRows = 0;
		String sQuery = new String();

		// Working on ets.ETS_PMO_TXN				
		logger.debug(
			"Starting to Delete the record in ets.ETS_PMO_TXN  with etsCRid "
				+ etsCRid
				+ " and  userID : "
				+ userid);

		sQuery =
			"DELETE FROM ets.ETS_PMO_TXN WHERE TYPE ='"
				+ userid
				+ "' and ID ='"
				+ etsCRid
				+ "'";
		try {
			noOfRows = executeUpdateStmt(sQuery.toString());
		} catch (SQLException q) {

			logger.error(
				"SQLException in deleteCRIwithETSCRIDAndUserID by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(q));
		}
		if (noOfRows > 0) {
			logger.debug(
				"Finished executing delete on ets.ETS_PMO_TXN for user_ID : "
					+ userid
					+ " Number of rows deleted: "
					+ noOfRows);
		} else {
			logger.debug("No rows to be deleted for userid:" + userid);
		}
	}
	public void deleteFromPMO_ISSUE_INFOwithETSID(String etsid)
		throws SQLException, Exception {
		int noOfRows = 0;
		String sQuery = new String();

		// Working on ets.ETS_PMO_TXN				
		logger.debug(
			"Starting to Delete the record in ets.PMO_ISSUE_INFO  with etsID : "
				+ etsid);

		sQuery = "DELETE FROM ets.PMO_ISSUE_INFO WHERE ETS_ID ='" + etsid + "'";
		try {
			noOfRows = executeUpdateStmt(sQuery.toString());
		} catch (SQLException q) {

			logger.error(
				"SQLException in deleteFromPMO_ISSUE_INFOwithETSID by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(q));
		}
		if (noOfRows > 0) {
			logger.debug(
				"Finished executing delete on ets.PMO_ISSUE_INFO for user_ID : "
					+ etsid
					+ " Number of rows deleted: "
					+ noOfRows);
		} else {
			logger.debug("No rows to be deleted for userid:" + etsid);
		}
	}
	/*
	 * I am returning no. of Project Rows
	 */
	public int deleteProjectandItsAssociates(String PmoProjID)
		throws SQLException, Exception {

		int noOfRows = 0;
		int noOfProjectRows = 0;
		String sQuery = new String();

		// Working on ets.ETS_PMO_MAIN				
		logger.debug(
			"\nStarting to Delete the records in ets.ETS_PMO_MAIN for records with PMO_PROJECT_ID : "
				+ PmoProjID);

		sQuery =
			"DELETE FROM ets.ETS_PMO_MAIN WHERE PMO_PROJECT_ID ='"
				+ PmoProjID
				+ "'";
		try {
			noOfProjectRows = executeUpdateStmt(sQuery.toString());
		} catch (SQLException q) {

			logger.error(
				"SQLException caused trying to deleteProjectandItsAssociates and the exception is :"
					+ getStackTrace(q));
		}
		if (noOfProjectRows > 0) {
			logger.debug(
				"Finished executing delete on ets.ETS_PMO_MAIN for records with PMO_PROJECT_ID : "
					+ PmoProjID
					+ " Number of rows deleted: "
					+ noOfRows);
		} else {
			logger.debug("No rows to be deleted. A new Entry: " + PmoProjID);
		}

		// Working on ets.ETS_PMO_RTF				
		//	logger.debug("\nStarting to Delete the records on ets.ETS_PMO_RTF for records with PMO_PROJECT_ID : " +
		//				 	PmoProjID  );
		//	sQuery = "DELETE FROM ets.ETS_PMO_RTF WHERE PMO_PROJECT_ID ='" + PmoProjID + "'";

		//	noOfRows = executeUpdateStmt(sQuery.toString());
		//	if(noOfRows > 0){
		//			logger.debug("Finished executing delete on ets.ETS_PMO_RTF for records with PMO_PROJECT_ID : " 
		//							+ PmoProjID + " Number of rows deleted: " + noOfRows );
		//	}
		//	else {
		//			logger.debug("No rows to be deleted. A new Entry: " + PmoProjID );
		//	}

		// Working on ets.ETS_PMO_SCORE				
		logger.debug(
			"\nStarting to Delete the records on ets.ETS_PMO_SCORE for records with PMO_PROJECT_ID : "
				+ PmoProjID);
		sQuery =
			"DELETE FROM ets.ETS_PMO_SCORE WHERE PMO_PROJECT_ID ='"
				+ PmoProjID
				+ "'";
		noOfRows = executeUpdateStmt(sQuery.toString());
		if (noOfRows > 0) {
			logger.debug(
				"Finished executing delete on ets.ETS_PMO_SCORE for records with PMO_PROJECT_ID : "
					+ PmoProjID
					+ " Number of rows deleted: "
					+ noOfRows);
		} else {
			logger.debug("No rows to be deleted. A new Entry: " + PmoProjID);
		}

		// Working on ets.ETS_PMO_RESOURCE				
		logger.debug(
			"\nStarting to Delete the records on ets.ETS_PMO_RESOURCE for records with PMO_PROJECT_ID : "
				+ PmoProjID);
		sQuery =
			"DELETE FROM ets.ETS_PMO_RESOURCE WHERE PMO_PROJECT_ID ='"
				+ PmoProjID
				+ "'";
		noOfRows = executeUpdateStmt(sQuery.toString());
		if (noOfRows > 0) {
			logger.debug(
				"Finished executing delete on ets.ETS_PMO_RESOURCE for records with PMO_PROJECT_ID : "
					+ PmoProjID
					+ " Number of rows deleted: "
					+ noOfRows);
		} else {
			logger.debug("No rows to be deleted. A new Entry: " + PmoProjID);
		}
		// Working on ets.ETS_PMO_DOC
		/*	Lets not delete the docs as they are added from the front end for PCRs. If we need to delete
			and populate with new one, lets delete that specific record which needs to be inserted.
		*/
		// boo DOC 10/13
		/*logger.debug(
			"\nStarting to Delete the records on ets.ETS_PMO_DOC for records with PMO_PROJECT_ID : "
				+ PmoProjID);
		sQuery =
			"DELETE FROM ets.ETS_PMO_DOC WHERE PMO_PROJECT_ID ='"
				+ PmoProjID
				+ "'";
		noOfRows = executeUpdateStmt(sQuery.toString());
		if (noOfRows > 0) {
			logger.debug(
				"Finished executing delete on ets.ETS_PMO_DOC for records with PMO_PROJECT_ID : "
					+ PmoProjID
					+ " Number of rows deleted: "
					+ noOfRows);
		} else {
			logger.debug(
				"No rows to be deleted in ets.ETS_PMO_DOC. A new Entry: "
					+ PmoProjID);
		}
		*/
		// Working on ets.PMO_ISSUE_INFO ( just delete the CRIFOLDERS)				
		logger.debug(
			"\nStarting to Delete  CRIFOLDERS in ets.pmo_ISSUE_INFO with PMO_PROJECT_ID : "
				+ PmoProjID);
		sQuery =
			"DELETE FROM ets.PMO_ISSUE_INFO WHERE PMO_PROJECT_ID ='"
				+ PmoProjID
				+ "' and TYPE ='"
				+ "CRIFOLDER'";
		noOfRows = executeUpdateStmt(sQuery.toString());
		if (noOfRows > 0) {
			logger.debug(
				"Finished executing delete on ets.PMO_ISSUE_INFO with PMO_PROJECT_ID : "
					+ PmoProjID
					+ " Number of rows deleted: "
					+ noOfRows);
		} else {
			logger.debug(
				"No rows to be deleted in ets.PMO_ISSUE_INFO. A new Entry: "
					+ PmoProjID);
		}
		/*	// Working on PMO_ISSUE_INFO			
			logger.debug("Starting to Delete the records on PMO_ISSUE_INFO for records with PMO_ID : " +
						 	PmoProjID );
			sQuery.append(	"DELETE FROM ets.PMO_ISSUE_INFO WHERE PMO_ID ='" + PmoProjID + "'");
			noOfRows = executeUpdateStmt(sQuery.toString());
			if(noOfRows > 0){
					logger.debug("Finished executing delete on PMO_ISSUE_INFO for records with PMO_ID : " 
									+ PmoProjID + " Number of rows deleted: " + noOfRows );
			}
			else {
					logger.debug("No rows to be deleted in PMO_ISSUE_INFO. A new Entry: " + PmoProjID );
			}
			*/ /*	// Working on PMO_ISSUE_DOC			
					logger.debug("\nStarting to Delete the records on PMO_ISSUE_DOC for records with PMO_ID : " +
								 	PmoProjID );
					sQuery =	"DELETE FROM ets.PMO_ISSUE_DOC WHERE PMO_PROJECT_ID ='" + PmoProjID + "'";
					noOfRows = executeUpdateStmt(sQuery.toString());
					if(noOfRows > 0){
							logger.debug("Finished executing delete on PMO_ISSUE_DOC for records with PMO_ID : " 
											+ PmoProjID + " Number of rows deleted: " + noOfRows );
					}
					else {
							logger.debug("No rows to be deleted in PMO_ISSUE_DOC. A new Entry: " + PmoProjID + "\n" );
					}
					*/
		return noOfProjectRows;
	}
	/*
	 * This creates the complete family of the issue/cr
	 * i.e it creates the parents of cr/issue
	 * and puts in the vector. This will be needed
	 * for sending the CRIFolder structure while
	 * sending an update/create cr/issue xml to pmo.
	 */
	/*
	* GenerationofFamilyForUpdate and findParentForUpdate  will be used when updating issues/crs.
	* They help in storing ids and type of parents in the form of
	* great great grand father id, great great grandfather type
	* great grand father id, great grandfather type
	* grand father id, grand father type
	* father id, father type
	* son id , son type
	*  unlike GenerationofFamilyForCreate and findParentForUCreate where there is no son id , son type stored.
	* 
	*/
	public void GenerationofFamily(String child, String projectID, Vector v)
		throws SQLException, Exception {
		GenerationSructOfCRIssue gen = findParent(child, projectID);
		if (gen != null
			&& !(gen.getGenerationParentID().equalsIgnoreCase(projectID))) {
			v.add(0, gen);

			//System.out.println("ID :" + gen.getGenerationParentID() + " Type : " + gen.getGenerationParentType());
			GenerationofFamily(gen.getGenerationParentID(), projectID, v);
		}
	}
	public GenerationSructOfCRIssue findParent(
		String childstr,
		String projectID)
		throws SQLException, Exception {
		String parentpmoID = null;
		String type = null;
		Statement stmt = null;
		ResultSet rs = null;
		GenerationSructOfCRIssue family = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select pmo_id, type from ets.ETS_PMO_MAIN where pmo_project_id = '"
					+ projectID
					+ "'"
					+ "and pmo_id = ( select parent_pmo_id from ets.ETS_PMO_MAIN where pmo_id='"
					+ childstr
					+ "' and pmo_project_id='"
					+ projectID
					+ "') ");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				parentpmoID = rs.getString(1);
				type = rs.getString(2);
				if (parentpmoID != null && type != null) {
					family = new GenerationSructOfCRIssue();
					family.setGenerationParentID(parentpmoID.trim());
					family.setGenerationParentType(type.trim());
					break;
				}

			}

		} catch (SQLException e) {
			logger.error(
				"SQLException in findParent() by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/
		return family;
	}
	public String populateRTFTableforCreatePCRs(String txnid, String CR_pmo_id, String classType)
		throws SQLException, Exception {
		/************/
		String rslt = null;
		Statement stmt = null;
		ResultSet rs = null;

		String comm_from_custValue = null;
		String pmo_project_idValue = null;

		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select  i.comm_from_cust, t.pmo_project_id"
					+ " from ets.pmo_issue_info i, "
					+ "ets.ets_pmo_txn t "
					+ "where t.txn_id = '"
					+ txnid
					+ "' and t.id=i.ets_id with ur");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				comm_from_custValue = rs.getString(1);
				pmo_project_idValue = rs.getString(2);

				/*
				logger.debug(	" comm_from_custValue : " + comm_from_custValue + 
							 	" pmo_project_idValue : " + pmo_project_idValue);*/
				if (logger.isInfoEnabled()) {
					logger
							.info("populateRTFTableforCreatePCRs(String, String, String) -  comm_from_custValue"
									+ comm_from_custValue
									+ "n pmo_project_idValue"
									+ pmo_project_idValue);
				}

			}

		} catch (SQLException e) {
			logger.error(
				"SQLException in populateRTFTableforCreatePCRs by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		
		/***********/
		if (comm_from_custValue != null
			&& pmo_project_idValue != null
			&& CR_pmo_id != null) {
				
			int rtf_id = 9;
			String rtf_name = null;
			if(classType.equalsIgnoreCase("CHANGEREQUEST")){
			rtf_name =
				(String) ETSPMOGlobalInitialize.getHtCRIRTF().get(
					new Integer(rtf_id));
			}
			else{
				rtf_id = 7;
				rtf_name =
								(String) ETSPMOGlobalInitialize.getHtISSUERTF().get(
									new Integer(rtf_id));
			}
			String rtf_alias_name = rtf_name;
			String value_pmo_id = CR_pmo_id + ":" + rtf_id;

			StringBuffer sIns = new StringBuffer();
			sIns.append(
				"INSERT INTO ets.ets_pmo_rtf (pmo_id, pmo_project_id, parent_pmo_id,"
					+ "rtf_id, rtf_name, rtf_alias_name, rtf_blob, last_timestamp)"
					+ "VALUES ('"
					+ value_pmo_id
					+ "' ,'"
					+ pmo_project_idValue
					+ "', '"
					+ CR_pmo_id
					+ "', "
					+ rtf_id
					+ ", '"
					+ rtf_name
					+ "', '"
					+ rtf_alias_name
					+ "', blob('"
					+ comm_from_custValue
					+ "'), current timestamp)");
			//I AM LEAVING current timestamp...ETC FOR NOW SUBU
			try {
				logger.info("SQL to insert RTF comments from cust : "  + sIns.toString());
				executeUpdateStmt(sIns.toString());
			} catch (SQLException e) {
				logger.error(
					"SQLException in populateResource by executing this command : "
						+ sIns.toString()
						+ "and the exception is : "
						+ getStackTrace(e));
				throw e;
			}

		}
		return rslt;

	}
	public String RetrieveFlagStatusofPCR(String txnID)
		throws SQLException, Exception {
		String parentpmoID = null;
		String type = null;
		Statement stmt = null;
		ResultSet rs = null;
		String flag = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select flag from ets.ets_pmo_txn where txn_id = '"
					+ txnID
					+ "' with ur");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				flag = rs.getString(1);
			}

		} catch (SQLException e) {
			logger.error(
				"SQLException in RetrieveFlagStatusofPCR(String txnID) by executing this command : "
					+ sQuery.toString()
					+ "and the exception is : "
					+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		/***********/
		return flag;
	}
	public String getMappedSeverity(String Severity, Hashtable ht) {

		if (Severity != null) {
			for (Enumeration ee = ht.keys(); ee.hasMoreElements();) {
				String key = (String) ee.nextElement();
				logger.debug("key" + key);
				String Value = ((String) ht.get(key)).trim();
				if (Value.equalsIgnoreCase(Severity.trim())) {
					int min =
						Integer.parseInt(
							key.substring(0, key.indexOf("-")).trim());
					int max =
						Integer.parseInt(
							key.substring(key.indexOf("-") + 1).trim());
					Severity = "" + min;
					logger.debug("ChangeRequest Rank value: " + Value);
					break;
				}

			}
		}
		return Severity;
	}
	//subu 4.5.1 fix 
	public String getIssueOwnerId(String IssueSource, String IssueClass, String ProjectName)
	throws SQLException, Exception {
		
		
		Statement stmt = null;
		ResultSet rs = null;
		String IssueOwnerId = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select ISSUE_OWNER_EMAIL from ets.ets_owner_data where DATA_ID=" + 
				"(select DATA_ID from ets.ets_dropdown_data where ISSUE_SOURCE='" + 
				IssueSource +
				"' and ISSUE_CLASS='" + 
				IssueClass + 
				"' and PROJECT_ID=" + 
				"(select PROJECT_ID from ets.ets_projects where PMO_PROJECT_ID='" + 
				ProjectName + 
				"'))");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				IssueOwnerId = rs.getString(1);
			}

		} catch (SQLException e) {
			logger.error(
				"SQLException in Method : getIssueOwnerId(String IssueSource, String IssueClass, String ProjectName)" + 
				"by executing this command : "
				+ sQuery.toString()
				+ "and the exception is : "
				+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		return IssueOwnerId;
	}
// subu 4.5.1 fix
public String getIssueOwnerName(String IssueOwnerId)
	throws SQLException, Exception {
		
		
		Statement stmt = null;
		ResultSet rs = null;
		String IssueOwnerName = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append(
				"select user_fullname from amt.users where ir_userid='" + 
				IssueOwnerId + 
				"' with ur");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				IssueOwnerName = rs.getString(1);
			}

			if (IssueOwnerName==null)
				IssueOwnerName = IssueOwnerId;
		} catch (SQLException e) {
			logger.error(
				"SQLException in Method : getIssueOwnerName(String IssueOwnerId)" + 
				"by executing this command : "
				+ sQuery.toString()
				+ "and the exception is : "
				+ getStackTrace(e));
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
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
		return IssueOwnerName;
	}

//======
//
//
public Resource getResourceInfo(String submitter, boolean isUsername)
throws SQLException, Exception {
	
	
	Statement stmt = null;
	ResultSet rs = null;
	Resource res = null;
	StringBuffer sQuery = new StringBuffer("");
	try {

		sQuery.append("select user_fullname, ir_userid, user_company, user_email, user_phone from amt.users");
		if (isUsername)
			sQuery.append(" where user_fullname='" +submitter+ "' with ur");
		else
			sQuery.append(" where ir_userid='" +submitter+ "' with ur");
		
		stmt = dbConnection.createStatement();
		rs = stmt.executeQuery(sQuery.toString());

		res = new Resource();
		while (rs.next()) {
			res = new Resource();
			res.setElement_name(rs.getString(1));
			res.setLogon_name(rs.getString(2));
			res.setCompany_name(rs.getString(3));
			res.setEmail(rs.getString(4));
			res.setPhone(rs.getString(5));
			logger.info("Resource Info: logon_id="+res.getLogon_name()+",fullname="+res.getElement_name()+",company="+res.getCompany_name());
		}

	} catch (SQLException e) {
		logger.error(
			"SQLException in Method : getIssueOwnerName(String IssueOwnerId)" + 
			"by executing this command : "
			+ sQuery.toString()
			+ "and the exception is : "
			+ getStackTrace(e));
		throw e;
	} catch (Exception e) {
		throw e;
	} finally {
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
	return res;
}

//
	public boolean populateProblem_StatefromState_Action(String stateAction, boolean IsISSUE, String PMO_ID)
	 throws SQLException, Exception{
		Hashtable htETStoPMOStates = null;
		boolean rslt = true;
		if(IsISSUE == true){
				htETStoPMOStates = ETSPMOGlobalInitialize.getHtFrontEndETStoDaemonIssueStates();
		}
		else{
				htETStoPMOStates = ETSPMOGlobalInitialize.getHtETStoPMOChangeRequestStates();
		}
		
		String problemState = null;
		String Class = IsISSUE? "ISSUE" : "CHANGEREQUEST";
		problemState = (String) htETStoPMOStates.get(stateAction.trim());
		if (logger.isInfoEnabled()) {
			logger.info("populateProblem_StatefromState_Action - PMO ID"+ PMO_ID);
			logger.info("populateProblem_StatefromState_Action - stateAction: "+ stateAction);
			logger.info("populateProblem_StatefromState_Action - Class: "+ Class);
			logger.info("populateProblem_StatefromState_Action - problemstate: "+ problemState);
		}
		
		if (problemState != null) {
					problemState = problemState.trim();
			}
		else {
			String critype = IsISSUE ? "ISSUE" : "CHANGEREQUEST";
			logger.error("While creating a " + critype +
				 "with PMO_ID: " + PMO_ID + ", no mapping was obtained for " + problemState);
			return false;
			} 
		try{
			String sQuery =
						"UPDATE  ETS.PMO_ISSUE_INFO  SET PROBLEM_STATE=?"
								+ "where CLASS = ? and STATE_ACTION=? and PMO_ID=?";
			PreparedStatement pstmt =
							dbConnection.prepareStatement(sQuery);
			pstmt.setString(1, problemState);
			pstmt.setString(2, Class);
			pstmt.setString(3, stateAction);
			pstmt.setString(4, PMO_ID);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException sqle){
			logger.error(
							"Prepared Statement exception in populateState_ActionfromProblem_State while"
								+ "populating State Action in the ets.pmo_issue_info table."
								+ sqle.getMessage());
			rslt = false;
			throw sqle;
		}
		catch(Exception e){
			logger.error(
							"Exception in populateState_ActionfromProblem_State while"
							+ "populating State Action in the ets.pmo_issue_info table."
							+ e.getMessage());
			rslt = false;
			throw e;
		}
		return rslt;
		
	}
	/*
public void SendMailtoCCList(String pmo_txnid, boolean isCreate)
			throws SQLException, Exception{
			Statement stmt = null;
			ResultSet rs = null;
			String MailList = null;
			String Action = null;
			StringBuffer sQuery = new StringBuffer("");
			StringBuffer sb =  null;
			try {

				sQuery.append(
					"select ETS_ID, PMO_ID, PMO_PROJECT_ID,PARENT_PMO_ID,  REF_NO, INFO_SRC_FLAG,"
					+ "SUBMITTER_NAME, SUBMITTER_COMPANY, SUBMITTER_EMAIL, SUBMITTER_PHONE,"
					+ "STATE_ACTION, SUBMITTER_IR_ID, SUBMISSION_DATE, CLASS, TITLE, "
					+ "SEVERITY, TYPE, DESCRIPTION, COMM_FROM_CUST, OWNER_IR_ID, OWNER_NAME, LAST_USERID, "
					+ "LAST_TIMESTAMP, ETS_CCLIST, PROBLEM_STATE from ets.pmo_issue_info where ETS_ID =" + 
					"(select id from ets.ets_pmo_txn where txn_id = '" + 
					pmo_txnid +
					"')");
				"select y.PMO_ID, y.NAME, y.PMO_PROJECT_ID, x.ETS_ID from ets.ets_pmo_main y, ets.pmo_issue_info x " +				"where y.pmo_project_id='e1c57fced69f11d880009ef646645a92' and y.parent_pmo_id='null' and x.pmo_project_id='e1c57fced69f11d880009ef646645a92'
				and ets_id = (select id from ets.ets_pmo_txn where txn_id = 'v2sathis-1100815201374')" 
				stmt = dbConnection.createStatement();
				rs = stmt.executeQuery(sQuery.toString());

				while (rs.next()) {
					String etsCR_Id = rs.getString(1);

									String pmo_id = rs.getString(2);
									String project_id = rs.getString(3);
									String parent_pmo_id = rs.getString(4);
									String ref_no = rs.getString(5);
									String info_src_flag = rs.getString(6);
									String submitterName = rs.getString(7);
									String submitter_Company = rs.getString(8);
									String submitterMail = rs.getString(9);
									String submitterPhone = rs.getString(10);
									String stateAction = rs.getString(11);
									String submitterIRID = rs.getString(12);
									Timestamp submissionDate = rs.getTimestamp(13);
									String classs = rs.getString(14);
									String title = rs.getString(15);
									String severity = rs.getString(16);
									String type = rs.getString(17);
									String description = rs.getString(18);
									String comm_from_cust = rs.getString(19);
									String owner_IR_id = rs.getString(20);
									String ownerName = rs.getString(21);
									String lastUser = rs.getString(22);
									Timestamp lastTimeStamp = rs.getTimestamp(23);
									MailList = rs.getString(24);
									Action	=	rs.getString(25);
									
									sb = new StringBuffer();
									sb.append("\n\tID : " + etsCR_Id);
									sb.append("\n\tTitle : " + title);
									sb.append("\n\tPMO_ID : " + pmo_id);
									sb.append("\n\tCLASS : " + classs);
									sb.append("\n\tProjectID : " + project_id);
									
									sb.append("\n\tSeverity : " + severity);
									sb.append(
										"\n\tDescription :" + description);
									sb.append(
										"\n\tComments : " + comm_from_cust);
									sb.append(
										"\n\tSubmitterName :" + submitterName);
									sb.append(
										"\n\tSubmitterCompany : "
											+ submitter_Company);
									sb.append(
										"\n\tSubmissionDate : "
											+ submissionDate);

								}

								} catch (SQLException e) {
										logger.error(
											"SQLException in Method : RetrieveCRIMailList(String  pmo_txnid"
												+ "by executing this command : "
												+ sQuery.toString()
												+ "and the exception is : "
												+ getStackTrace(e));
										throw e;
									} catch (Exception e) {
										throw e;
									} finally {
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
									logger.debug(
										"Sending mail to the ETS CC list : "
											+ MailList);
									if (sb != null) {
										logger.debug(
											"The content of the mail : "
												+ sb.toString());
									}
								PostMan p = new PostMan();

								if (isCreate == true) {
									p.sendCreatedCRInfo(MailList, sb.toString()); //mail to the mailing list
					
								} else {
									p.sendUpdatedCRInfo(MailList, sb.toString());
								}
}
*/
public boolean updateOwnerInfoForTheIssue(String exc_pmo_id, String Owner_Id, String Owner_Name) 
	throws SQLException, Exception{
		boolean b = true;
		try{
		String sQuery = "UPDATE ETS.PMO_ISSUE_INFO SET OWNER_IR_ID=?, OWNER_NAME=? WHERE PMO_ID=?";
						
			PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
		
			pstmt.setString(1, Owner_Id);
			pstmt.setString(2, Owner_Name);
			pstmt.setString(3, exc_pmo_id);
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException sqle) {
			logger.error(
				"PreparedStatementException in updateOwnerInfoForTheIssue : "
					+ "and the exception is : "
					+ getStackTrace(sqle));
			b = false;
			throw sqle;
		}
		catch(Exception e){
			b = false;
			logger.error("Exception in updateOwnerInfoForTheIssue : " + getStackTrace(e));
			throw e;
		}
	return b;
}
public String RetrieveETS_CCListForIssues(String exc_pmo_id)
	throws SQLException, Exception{
		Statement stmt = null;
		ResultSet rs = null;
		String rslt = null;
		try{
		
				StringBuffer sQuery = new StringBuffer();
				sQuery.append(
						"SELECT ETS_CCLIST from ETS.PMO_ISSUE_INFO where PMO_ID='" + exc_pmo_id +"' with ur");
					stmt = dbConnection.createStatement();
					rs = stmt.executeQuery(sQuery.toString());

					int i = 0;
					while (rs.next()) {
						rslt = rs.getString(1);
						if(rslt != null){
							rslt = rslt.trim();
						}
						i++;
					}
					if (i > 1) {
						logger.error("The following sql statement must fetch us only one record. Here we are fetching more" +
							"than 1 record. The SQL :" + sQuery.toString());
						rslt = null;
						throw new Exception() ;
					}
		}
		catch(SQLException sqlE){
			logger.error("SQLException caught while accessing RetrieveETS_CCListForIssues: " + getStackTrace(sqlE));
			throw sqlE;
		}
		catch(Exception e){
			logger.error("Exception caught while accessing RetrieveETS_CCListForIssues: " + getStackTrace(e));
		}
		return rslt;
}

public String retrieveSTATE_ACTIONforException(String exc_pmo_id)
	throws SQLException, Exception{
		Statement stmt = null;
		ResultSet rs = null;
		String rslt = null;
		try{
		
				StringBuffer sQuery = new StringBuffer();
				sQuery.append(
						"SELECT STATE_ACTION from ETS.PMO_ISSUE_INFO where PMO_ID='" + exc_pmo_id +"' with ur");
					stmt = dbConnection.createStatement();
					rs = stmt.executeQuery(sQuery.toString());

					int i = 0;
					while (rs.next()) {
						rslt = rs.getString(1);
						rslt = rslt.trim();
						i++;
					}
					if (i > 1) {
						logger.error("The following sql statement must fetch us only one record. Here we are fetching more" +
							"than 1 record. The SQL :" + sQuery.toString());
						rslt = null;
						throw new Exception() ;
					}
		}
		catch(SQLException sqlE){
			logger.error("SQLException caught while accessing RetrieveETS_CCListForIssues: " + getStackTrace(sqlE));
			throw sqlE;
		}
		catch(Exception e){
			logger.error("Exception caught while accessing RetrieveETS_CCListForIssues: " + getStackTrace(e));
		}
		return rslt;
}
public String retrieveSUBMITTER_NAMEforException(String exc_pmo_id)
	throws SQLException, Exception{
		Statement stmt = null;
		ResultSet rs = null;
		String rslt = null;
		try{
		
				StringBuffer sQuery = new StringBuffer();
				sQuery.append(
						"SELECT SUBMITTER_NAME from ETS.PMO_ISSUE_INFO where PMO_ID='" + exc_pmo_id +"' with ur");
					stmt = dbConnection.createStatement();
					rs = stmt.executeQuery(sQuery.toString());

					int i = 0;
					while (rs.next()) {
						rslt = rs.getString(1);
						rslt = rslt.trim();
						i++;
					}
					/*
					if (i > 1) {
						logger.error("The following sql statement must fetch us only one record. Here we are fetching more" +
							"than 1 record. The SQL :" + sQuery.toString());
						rslt = null;
						throw new Exception() ;
					}
					*/
		}
		catch(SQLException sqlE){
			logger.error("SQLException caught while accessing retrieveSUBMITTER_NAMEforException: " + getStackTrace(sqlE));
			throw sqlE;
		}
		catch(Exception e){
			logger.error("Exception caught while accessing retrieveSUBMITTER_NAMEforException: " + getStackTrace(e));
		}
		return rslt;
}
private int getMaxDocNoForThisIssue(String ETS_ID)
throws SQLException, Exception{
	logger.debug("Trying to retrieve total number of docs for the issue with ETS_ID : " + ETS_ID);
	Statement stmt = null;
	ResultSet rs = null;
	StringBuffer sQuery = new StringBuffer("");
	int docCount = -1;
	try {
		
		sQuery.append(
			"SELECT count(*) from ets.PMO_ISSUE_DOC where ETS_ID='" + ETS_ID +"' with ur");
		stmt = dbConnection.createStatement();
		rs = stmt.executeQuery(sQuery.toString());

		int i = 0;
		while (rs.next()) {
			docCount = rs.getInt(1);
			
		}
		if (i > 1) {
			logger.error("The following sql statement must fetch us only one record. Here we are fetching more" +
				"than 1 record. The SQL :" + sQuery.toString());
		throw new Exception() ;
		}

	} catch (SQLException e) {
		logger.error(
			"SQLException in getMaxDocNoForThisIssue by executing this command : "
				+ sQuery.toString()
				+ "and the exception is : "
				+ getStackTrace(e));
		throw e;
	} catch (Exception e) {
		throw e;
	} finally {
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
return docCount;
}
public ArrayList getDocIdsExistingForThisProject(String Pmo_Project_Id)	throws SQLException, Exception {
			
		if(Pmo_Project_Id == null || Pmo_Project_Id.equalsIgnoreCase(""))
				return null;
				
		ArrayList vRtrn = null;	
			
		Pmo_Project_Id			= Pmo_Project_Id.trim();
		if (logger.isDebugEnabled()) {
			logger
					.debug("getDocIdsExistingForThisProject(String) - proj id asdfasfd*****"
							+ Pmo_Project_Id);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer();
		try {
					sQuery.append("SELECT DOC_ID FROM ETS.ETS_PMO_DOC WHERE PMO_PROJECT_ID=? with ur");
			
					pstmt = dbConnection.prepareStatement(sQuery.toString());
					pstmt.setString(1, Pmo_Project_Id);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						String docId			= rs.getString("DOC_ID");
						if(vRtrn == null){
							vRtrn = new ArrayList();
						}
						if (logger.isDebugEnabled()) {
							logger
									.debug("getDocIdsExistingForThisProject(String) - Adding doc id"
											+ docId);
						}
						vRtrn.add(docId);
					
					}
				} catch (SQLException e) {
					logger.error(
						"SQLException in getDocIdsExistingForThisProject by executing this command : "
							+ sQuery.toString()
							+ "and the exception is : "
							+ getStackTrace(e));
					throw e;
				} catch (Exception e) {
					throw e;
				} finally {
					if (rs != null) {
						rs.close();
						rs = null;
					}
					if (pstmt != null) {
						pstmt.close();
						pstmt = null;
					}
					sQuery = null;
				}
					
				return vRtrn;
}
public void deleteDocsThatWerentInXML(String docId) throws SQLException, Exception{
			
	if(docId == null || docId.equalsIgnoreCase(""))
			return ;
				
	ArrayList vRtrn = null;	
			
	docId			= docId.trim();
	
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	StringBuffer sQuery = new StringBuffer();
	try {
				sQuery.append("DELETE FROM ETS.ETS_PMO_DOC WHERE DOC_ID=?");
			
				pstmt = dbConnection.prepareStatement(sQuery.toString());
				pstmt.setString(1, docId);
				pstmt.executeUpdate();
				
			} catch (SQLException e) {
				logger.error(
					"SQLException in deleteDocsThatWerentInXML(String docId) by executing this command : "
						+ sQuery.toString()
						+ "and the exception is : "
						+ getStackTrace(e));
				throw e;
			} catch (Exception e) {
				throw e;
			} finally {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				sQuery = null;
			}
					
			return;
	
}


	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

	public String matchProjectCode(String ref_code) throws SQLException {
		// checks if there?s LOTUS_PROJECT_ID in the ETS.ets_projects that matches the 
		// RPM_PROJECT_CODE for this RPM projects
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sqlPROJECT_ID = null;
		String sqlPROJECT_NAME=null;
		String sqlPMO_PROJECT_ID = null;
		int i = 0;
		try {
			sQuery.append(
			"select PROJECT_ID, PROJECT_NAME, PMO_PROJECT_ID from ets.ets_projects "
			+ "where LOTUS_PROJECT_ID='" + ref_code + "' with ur");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			while (rs.next()) {
				sqlPROJECT_ID = rs.getString("PROJECT_ID");
				sqlPROJECT_NAME		= rs.getString("PROJECT_NAME");
				sqlPMO_PROJECT_ID	= rs.getString("PMO_PROJECT_ID");
				i ++;
			}
			if (i > 1) {
				logger.warn("The following sql statement must fetch us only one record. Here we are fetching more" +
					"than 1 record. The SQL causing problems : " + sQuery.toString());
				
			}
		} catch (SQLException e) {
			logger.error(
					"SQLException in matchProjectId by executing this command : "
						+ sQuery.toString()
						+ "and the exception is : "
						+ getStackTrace(e));
			} catch (Exception e) {
				logger.error(
						"Exception in matchProjectId by executing this command : "
							+ sQuery.toString()
							+ "and the exception is : "
							+ getStackTrace(e));
			} finally {
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
				
		boolean result=false;
		if (i==1)
			result = updatePmoProjectId(sqlPROJECT_ID,sqlPMO_PROJECT_ID);
		if (result==true)
			return sqlPROJECT_NAME;
		else 
			return null;
	}

	public boolean updatePmoProjectId(String project_id, String pmo_project_id) {
		// insert the value of PMO_PROJECT_ID for this project to the PMO_PROJECT_ID 
		// field of ETS.ets_projects table 
		// return the name of the workspace 
		boolean b = false;
		try{
		String sQuery = "UPDATE ETS.ETS_PROJECTS SET PMO_PROJECT_ID=? WHERE PROJECT_ID=?";
						
			PreparedStatement pstmt = dbConnection.prepareStatement(sQuery);
		
			pstmt.setString(1, pmo_project_id);
			pstmt.setString(2, project_id);
			
			pstmt.executeUpdate();
			pstmt.close();
			b = true;
		} catch (SQLException sqle) {
			logger.error(
				"PreparedStatementException in updatePmoProjectId : "
					+ "and the exception is : "
					+ getStackTrace(sqle));
		}
		catch(Exception e){
			logger.error("Exception in updatePmoProjectId : " + getStackTrace(e));
		}
	return b;
	}

}
