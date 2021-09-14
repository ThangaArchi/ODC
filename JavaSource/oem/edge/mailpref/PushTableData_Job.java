/****
 * 	Before checking the file in make sure :-
 * 1.sendMailtoprincipal's TO field is changed to appropraite
 * 2. sendmailtobus_solution_mgr '' '' '  '  '' 
 * 3. sendmailtoclient_service mgr '''''''''
 * 4.sendfinalmail ''''''''
 * 5. property file's representaion is changed from rb = ResourceBundle.getBundle("property/oem/edge/ets/fe/ets");
 * to	rb = ResourceBundle.getBundle("oem.edge.ets.fe.ets");
 * 
 ****/

package oem.edge.mailpref;

/**
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.Vector;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Connection;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.mail.internet.InternetAddress;

import java.net.InetAddress;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;

import oem.edge.common.Global;
import oem.edge.common.SysLog;
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

public class PushTableData_Job implements JobInterface{
	
	private ResourceBundle rb;
	
	private static boolean DebugVal = false;
    private static int EMAIL_SLEEP_TIME = 5000;		// milliseconds
    private static int EMAIL_NO_TIMES = 5;
	
	private int roleID							=	-1;
	
	private static String email_sleep_time;	
	private static String email_number_times;
	private static String mailFm;
	private static String mailHost;
	private static String mailTO;
	private static String problem_mailTO;
	private static int shouldIGetEmail;
	private static String mailcopy;
		
	private static String usr_job_bus_solution_mgr;
	private static String usr_job_client_service_mgr;
	private static String usr_job_principal;
	private static String last_userid;
	
	private static String wmprivname;
	private static String mprivname;
	private static int no_of_roles;
	private static int priv_available;
	private static int priv_not_available;
	private static int member_priv_id;
	
	private static String project_or_proposal;
	
	/* ETS_CAT related data [START] */
	private static int no_proposal_cat;
	private static int no_proposal_subcat;
	private static int show_project_desc;
	/* ETS_CAT related data [END] */
	
	private Connection amt_Connection;
	
	private java.util.StringTokenizer ClientServiceTokens		= null;
	private java.util.StringTokenizer IBMTeamMembersTokens		= null;
	private String baseURL		= null;
	private String	registrationURL		= null;
	private String	entitlementURL		= null;
	private String connectURL			= null;
	private boolean Init() {
		boolean success = true;

		try {
		//	rb = ResourceBundle.getBundle("property.oem.edge.ets.fe.ets");
			rb = ResourceBundle.getBundle("oem.edge.ets.fe.ets");
			
			//baseURL						= getParam(rb, "ets.baseURL");
			baseURL						= Global.getUrl("") ;
			registrationURL				= getParam(rb, "ets.registrationURL");
			entitlementURL				= getParam(rb, "ets.entitlementURL");
			connectURL					= getParam(rb, "ets.connectURL");
			

			
			mailFm						= getParam(rb, "ets.mailFm");
			mailHost					= getParam(rb, "ets.mailHost");
			email_sleep_time			= getParam(rb, "ets.email_sleep_time");			
			email_number_times			= getParam(rb, "ets.email_number_times");
			mailTO						= getParam(rb, "ets.mailto");
			problem_mailTO				= getParam(rb, "ets.problem_mailto");
	
			shouldIGetEmail				= Integer.parseInt(getParam(rb, "ets.shouldIGetEmail"));
			mailcopy					= getParam(rb, "ets.mailcopy");
			
			usr_job_bus_solution_mgr	= getParam(rb, "ets.usr_job_bus_solution_mgr");
			usr_job_principal			= getParam(rb, "ets.usr_job_principal");
			usr_job_client_service_mgr	= getParam(rb, "ets.usr_job_client_service_mgr");
			last_userid					= getParam(rb, "ets.last_userid");
			
			wmprivname					= getParam(rb, "ets.wmprivname");
			mprivname					= getParam(rb, "ets.mprivname");
			no_of_roles					= Integer.parseInt(getParam(rb, "ets.no_of_roles").trim());
			priv_available				= Integer.parseInt(getParam(rb, "ets.priv_available").trim());
			priv_not_available			= Integer.parseInt(getParam(rb, "ets.priv_not_available").trim());
			member_priv_id				= Integer.parseInt(getParam(rb, "ets.member_priv_id").trim());
			
			project_or_proposal			= getParam(rb, "ets.project_or_proposal");
			
			/* ETS_CAT related data [START] */
			no_proposal_cat				= Integer.parseInt(getParam(rb, "ets.no_proposal_cat"));
			no_proposal_subcat			= Integer.parseInt(getParam(rb, "ets.no_proposal_subcat"));
			show_project_desc			= Integer.parseInt(getParam(rb, "ets.show_project_desc"));
			/* ETS_CAT related data [END] */
						
		}
		catch(Exception x){
			DebugPrint(	"PushTableData_Job", "init",
							"INIT FAILED");
			success = false;
							
		}
		return success;
	
	}
/**
 * Save Bundle reader (if parametr not found result is empty string
 * Added by Valentin Korotky-Adamenko.
 * Creation date: (3.29.01 7:14:13 PM)
 * @return java.lang.String
 * @param rb java.util.ResourceBundle
 * @param param java.lang.String
 */
private  String getParam(ResourceBundle rb, String param) {
	String result = "";
	try {
		result = rb.getString(param);
		if (result != null) {
			result = result.trim();
		}
		else {
			DebugPrint(	"PushTableData_Job", "JobImplementation",
							"ets.property: param=[" + param + "] init failed");
			return "";
		}
		if (result.length() == 0) {
			DebugPrint(	"PushTableData_Job", "JobImplementation",
							"ets.property: param=[" + param + "] have zero length");
		}
		return result;
	}
	catch (MissingResourceException mre) {
		if(DebugVal){
			DebugPrint(	"ets.property",
						"getParam",
						"Global: MissingResourceException param=[" + param + "] " + mre.getMessage());
		}
		
		result="";
	}
	return result;
}
	
	
	/////////    The function specific implementation of preference ////////
    ///
    ///	This function takes a string variable <str> and returns an actual 
    /// value corresponding to that string. Example can be DATE with return 
    /// value 04/23/200 .
    ///
    ////////////////////////////////////////////////////////////////////////

    public String mailFormat (String str,Connection conn, String user,
		String division,String function,String tag)
		throws SQLException{
			amt_Connection = conn;
			return null;}

	public String JobImplementation(Connection conn, String user, String func, String division, String start_date, String start_time, String end_date, String end_time) throws SQLException {
			
			Global.Init();
			
			if(!this.Init()){
				DebugPrint(	"PushTableData_Job", "JobImplementation",
							"Program Exiting...Property file " +
									" couldn't get initialized");
				return null;
			} 
			//if(func.equalsIgnoreCase("debug")){
				DebugVal	= true;
			//	}
			Vector v	=	new Vector();
			StringBuffer strmsg	= new StringBuffer();
			try{
				v	=	readOpportunityID(conn, "ets.ETS_OPPORTUNITY");
				
		/*		sendMailToPrincipal(conn,(Vector)v.get(1));
				sendMailToBus_Solution_Mgr(conn, (Vector)v.get(1));	
		*/
			if(!v.isEmpty()){
				strmsg.append("Records in ETS_OPPORTUNITY since last execution: [ " + v.size() + " ]" + "\n");
				v	=	(Vector)SearchOPPIDnotinETS_PROJECTS(conn, "ets.ETS_PROJECTS", v);
				strmsg.append("new records in ETS_OPPORTUNITY but not in ETS_PROJECTS : [ " + v.size() + " ]");
			}
			else{
				strmsg.append(" No new records available since last execution" + "\n");
				DebugPrint(	"PushTableData_Job", "JobImplementation",
								"No new records available since last time the script ran. Script was initiated previously at : [" + Yesterdays_Time().toString()+ "]");
				}
			if(!v.isEmpty())
					{
							DebugPrint(	"PushTableData_Job", "JobImplementation",
										"No. of elements in the vector: " + v.size());
						
					   for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
					   {
					   			
					   				
					   				Vector vec	=	(Vector)e.nextElement();
					   				DebugPrint(	"PushTableData_Job", "JobImplementation",
												"Processing..." + DisplayRecord(vec));
					   				if (FindDuplicatesOrNullsInRecords(vec)){
					   					DebugPrint(	"PushTableData_Job", "JobImplementation",
					   								"found duplicates/null values in the Record \n Will not update this record in any of the tables.");
					   						continue;
					   				}
					   				
					   				
									insertRecordsForETS_PROJECTS(conn,null,vec);
						
									insertRecordsForETS_ROLES(conn, null, vec);
						
									boolean rslt = insertRecordsForETS_USERS(conn, null, vec, "principal");
						
									if(!rslt) {
										DebugPrint(	"PushTableData_Job", "JobImplementation",
													"Problem with insertRecordsForETS_USERS. CONTINUING WITH THE NEXT ID");
											continue;
									}
									rslt = insertRecordsForETS_CAT(conn, null, vec);
						
										if(rslt){
											sendMailToPrincipal(conn, vec);
										}
						
									rslt = insertRecordsForETS_USERS(conn, null, vec, "bus_solution_mgr");	
						
										if(rslt) {
											sendMailToBus_Solution_Mgr(conn, vec);
										}
					
					
									rslt = insertRecordsForETS_USERS(conn, null, vec, "client_service_mgr");
					
										if(rslt) {
											sendMailToClient_Service_Mgr(conn, vec);
										}
					
									rslt = insertRecordsForETS_USERS(conn, null, vec, "ibm_team_members");		
										if(rslt){
										//email to anne bierce
											Vector vector	= (Vector)SearchOPPIDinETS_OPP_CONTACTS(conn, null, (String)vec.get(0));
											if(vector != null){
												sendFinalMail(vector);
											}
										}
				
					   }		
				}
				else{
						
							DebugPrint(this.getClass().toString(),
										"JobImplementation",
										" VECTOR EMPTY...No update required for ETS_PROJECTS");	
				}
			}
			catch(Exception e){
				if(this.getDebugVal())
					DebugPrint(this.getClass().toString(),"JobImplementation"," Exception caught");
				e.printStackTrace();
			}
			try{
			sendSuccessMailToSubu(strmsg);	
			}
			catch(Exception e){
				DebugPrint(this.getClass().toString(),"JobImplementation"," Error sending success mail to subu");
				e.printStackTrace();
			}
		return null;
	}

private Vector readOpportunityID(Connection conn, String tablename) throws SQLException, Exception{
	
	if(tablename == null || !tablename.startsWith("ets."))
		tablename = "ets.ETS_OPPORTUNITY";
	
		Statement stmt = null;
		ResultSet rsETS_OPPORTUNITY = null;
		Vector vel = new Vector();
		StringBuffer selectQueryETS_OPPORTUNITY = new StringBuffer();
		StringBuffer updateQuery = new StringBuffer();
		try{
			
			Timestamp tLastTouchDown = null;
			if((tLastTouchDown = Yesterdays_Time()) == null){
				DebugPrint("PushTableData_Job", "readOpportunityID","\nError retrieving info from the file...\n" + 
									"1. No such file\n" +
									"2. No timestamp stored in the file\n" +
									"3. File Corrupt");
				tLastTouchDown = (Timestamp)getCurrentTime();
			}
		
			stmt = conn.createStatement();
			String str = "select OPPORTUNITY_ID, OPPORTUNITY_NAME, OPPORTUNITY_DESC, EXPIRY_DATE, PRINCIPAL, BUS_SOLUTION_MGR, CLIENT_SERVICE_MGR, IBM_TEAM_MEMBERS from " + tablename +" where TIMESTAMP > '" + tLastTouchDown + "'";
			DebugPrint(	"PushTableData_Job","readOpportunityID",
						"Executing: [ " + str + " ] ");
			selectQueryETS_OPPORTUNITY.append(str);
			rsETS_OPPORTUNITY = stmt.executeQuery(selectQueryETS_OPPORTUNITY.toString());
			while (rsETS_OPPORTUNITY.next()) {
				String sOppID			= rsETS_OPPORTUNITY.getString(1);
				String sOppName			= rsETS_OPPORTUNITY.getString(2);
				String sOppDesc			= rsETS_OPPORTUNITY.getString(3);
				Date sExpiryDate		= rsETS_OPPORTUNITY.getDate(4);
				String sPrincipal		= rsETS_OPPORTUNITY.getString(5);
				String sBus_Sol_Mgr		= rsETS_OPPORTUNITY.getString(6);
				String sClient_Ser_Mgr	= rsETS_OPPORTUNITY.getString(7);
				String sIBM_Team_Mem	= rsETS_OPPORTUNITY.getString(8);
				
				DebugPrint(	"PushTableData_Job","readOpportunityID",
								"\tOPPORTUNITY ID: " + sOppID + "\n\t" +
								"OPPORTUNITY NAME: " + sOppName + "\n\t" +
								"OPPORTUNITY DESC: " + sOppDesc + "\n\t" +
								"EXPIRY DATE: " + sExpiryDate  + "\n\t" +
								"PRINCIPAL: " + sPrincipal);
								
				
				
				Vector v = new Vector();
				v.add(sOppID)	;	v.add(sOppName)		;
				v.add(sOppDesc)	;	v.add(sExpiryDate)	;
				v.add(sPrincipal);	v.add(sBus_Sol_Mgr)	;
				v.add(sClient_Ser_Mgr);
				v.add(sIBM_Team_Mem);
				
				vel.add(v);

			}
		}
		catch(SQLException e) {
		
			e.printStackTrace();
		} catch (Exception e) {
		
			e.printStackTrace();
		} finally {
			if (rsETS_OPPORTUNITY != null) {
				rsETS_OPPORTUNITY.close();
				rsETS_OPPORTUNITY = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			selectQueryETS_OPPORTUNITY = null;
		}
		return vel;
}
private Vector SearchOPPIDnotinETS_PROJECTS(Connection conn, String tablename, Vector vec) 
throws SQLException, Exception{
	
	if(tablename == null || !tablename.startsWith("ets."))
		tablename = "ets.ETS_PROJECTS";
		
			Statement stmt	= null;
			ResultSet rslt	= null;
			
			try{
					stmt		= conn.createStatement();
					int size	= vec.size();

						DebugPrint("PushTableData_Job","SearchOPPIDnotinETS_PROJECTS", "The vector size: " + size);

						
					for(int i = 0 ; i < size; i++ )
						{
							String sb	=	"select * from " + tablename 
											+ " where PROJECT_ID = '" 
											+ (String)((Vector)vec.get(i)).get(0) 
											+ "'";
							rslt		= stmt.executeQuery(sb);
							if(rslt.next()){
								
									DebugPrint(	"PushTableData_Job", "SearchOPPIDnotinETS_PROJECTS", 
												"Record found in " + tablename + "\nRemoving from vector: " +  DisplayRecord((Vector)vec.get(i) ));
								
								vec.remove(i);
								size	= size - 1;
								i		= i - 1;
								
							}
						}
			}
			
			catch(SQLException sqle){
				
				sqle.printStackTrace();
			}
			catch(Exception e){
			
				e.printStackTrace();
			}
			finally{
				if(stmt != null){
					stmt.close();
					stmt	= null;	
				}
				
			}
			return vec;
	}

private String DisplayRecord(Vector v){
	if(v == null) return null;
	StringBuffer sb = new StringBuffer();
	//for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
			Enumeration e = v.elements();
		 	String sOppID		= (String)e.nextElement();
		 	String sOppName		= (String)e.nextElement();
			String sOppDesc		= (String)e.nextElement();
			Date sExpiryDate	= (Date)e.nextElement();
			String sPrincipal	= (String)e.nextElement();	
			String sBusSolMgr	= (String)e.nextElement();
			String sClientSerMgr= (String)e.nextElement();
			String sIBMTeamMem	= (String)e.nextElement();
			
		 	sb.append(	"\nRECORD: \n\t\tsOppID: "
		 				+	sOppID
		 				+	" \n\t\tsOppName: "
		 				+	sOppName
		 				+	" \n\t\tsOppDesc: "
		 				+	sOppDesc
		 				+	" \n\t\tsExpiryDate: "
 		 				+	sExpiryDate
 		 				+	" \n\t\tPrincipal: "
 		 				+   sPrincipal
 		 				+	" \n\t\tBusiness Solution Manager: "
 		 				+	sBusSolMgr
 		 				+	" \n\t\tClient Service Manager: " 
 		 				+	sClientSerMgr
 		 				+	" \n\t\tIBM Team Members: " 
 		 				+ 	sIBMTeamMem
 		 				);
 	

	//	 }
		 return sb.toString();
}

private boolean insertRecordsForETS_PROJECTS(Connection conn, String tablename, Vector vec)throws SQLException, Exception{
		if(tablename == null || !tablename.startsWith("ets."))
		tablename = "ets.ETS_PROJECTS";
		
			Statement stmt	= null;

			try{
					stmt		= conn.createStatement();
					int size	= vec.size();
					if(DebugVal){
						DebugPrint("PushTableData_Job","insertRecordsForETS_PROJECTS", "VECTOR SIZE: " + size);
					}
							String projID	= (String)vec.get(0);
							String projName	= (String)vec.get(1);
							String projDesc	= (String)vec.get(2);
							Date projEnd	= (Date)vec.get(3);
							if(projEnd == null ){
								if(DebugVal){
									DebugPrint("PushTableData_Job", "insertRecordsForETS_PROJECTS()", "PROJECT ENDDATE: NULL");
								}
						
								throw new Exception();
							}
							if(projID == null ){
								if(DebugVal){
									DebugPrint("PushTableData_Job", "insertRecordsForETS_PROJECTS()", "PROJECT ID: NULL");
								}
								
								throw new Exception();
							}
							
							String sts		= projEnd.toString();
							sts =	sts + " 00:00:00";
							Timestamp tstamp	=	Timestamp.valueOf(sts);
							
							StringBuffer sb = new StringBuffer();
							sb.append(	"INSERT INTO " + tablename 
										+ " (PROJECT_ID, PROJECT_NAME, PROJECT_DESCRIPTION,"
										+ " PROJECT_START,PROJECT_END, PROJECT_OR_PROPOSAL,"
									  	+ " LOTUS_PROJECT_ID) VALUES ('" 
										+ projID + "', '" + projName + "', '"       
										+ projDesc + "', " + "current timestamp" + ", '"
										+ tstamp + "', '" + project_or_proposal + "', '"	
										+ projID + "')") ; 
										
							if(DebugVal){
								DebugPrint( "Class:PushTableData_Job", "Method:insertRecordsForETS_PROJECTS()",
											sb.toString());
							}
							int rslt = stmt.executeUpdate(sb.toString());
							if(rslt < 1){
								if(DebugVal){
									DebugPrint(	"PushTableData_Job",
												"insertRecordsForETS_PROJECTS()",
												"rslt	<	1");
								}
						 throw new Exception();
							}
			
			}
			catch(SQLException sqle){
				DebugPrint("Class:PushTableData_Job","Method:insertRecordsForETS_PROJECTS()","SQLException caught...");			
				sqle.printStackTrace();
				return false;//"finally" gets executed before "return"
			}
			catch(Exception e){
				DebugPrint("Class:PushTableData_Job","Method:insertRecordsForETS_PROJECTS()","Exception caught...");			
				e.printStackTrace();
				return false;//"finally" gets executed before "return"
			}
			finally{
				if(stmt != null){
					stmt.close();
					stmt	= null;	
				}
				
			}
			return true;
}

private boolean insertRecordsForETS_ROLES(Connection conn, String tablename, Vector v) throws SQLException, Exception{
		if(tablename == null || !tablename.startsWith("ets."))
			tablename = "ets.ETS_ROLES";
		
			Statement stmt	= null;
			int maxRoleID;

			try{
					stmt		= conn.createStatement();
					int size	= v.size();
					if(DebugVal){
						DebugPrint(	"C:PushTableData_Job",
									"M:insertRecordsForETS_ROLES",
									"VECTOR SIZE: " + size);
					}
					String ProjID	= (String)v.get(0);
					String LastUID	= (String)v.get(4);
					DebugPrint("C:PushTableData_Job",
									   "M:insertRecordsForETS_ROLES()",
										" ProjID : " + ProjID + "LastUID : " + LastUID); 
					if(ProjID == null ){
								if(DebugVal){
									DebugPrint(	"C:PushTableData_Job",
												"M:insertRecordsForETS_ROLES()",
												"PROJECT ID: NULL");
								}
								
								throw new Exception();
							}
					LastUID	=	convertToShortName(LastUID);
					maxRoleID		= RetrieveMaxRoleIDfromETS_ROLES(conn, tablename);
					if(maxRoleID == -1){
						if(DebugVal){
									DebugPrint(	"C:PushTableData_Job",
												"M:insertRecordsForETS_ROLES()",
												"maxRoleID == -1.Failure in method:RetrieveMaxRoleIDfromETS_ROLES()");
								}
						 throw new Exception();
					}
					
					StringBuffer insertStr	=	new StringBuffer();
					insertStr.append("INSERT INTO " + tablename 
									+ " (ROLE_ID, PRIV_ID, ROLE_NAME,"
									+ " PRIV_VALUE,PROJECT_ID,") ;
					if(LastUID != null)
						insertStr.append("LAST_USERID, "); 
					insertStr.append(" LAST_TIMESTAMP)");
					
					int privID			=	1;
					String WMprivName	=	wmprivname;
					String MprivName	=	mprivname;
					int priv_Value		=	priv_available; 
					StringBuffer sb[]	= 	new StringBuffer[no_of_roles];
					
					
					for(int i = 0; i < sb.length; i++){
						String privName = WMprivName;

						sb[i]			=	new StringBuffer(insertStr.toString());
						privID = i + 1;
						if(i == no_of_roles - 1){
								privName	= MprivName;
								privID		= member_priv_id;
								maxRoleID ++;
						}
						sb[i].append(	" VALUES (" 
								+ maxRoleID + ", " + privID + ", '"       
								+ privName + "', " + priv_Value + ", '"
								+ ProjID + "',");
								
						if(LastUID != null)
							sb[i].append( "'" + LastUID + "',");
							
						sb[i].append( " current timestamp" + ")") ; 
						
						if(DebugVal){
							DebugPrint(	"C:PushTableData_Job",
										"M:insertRecordsForETS_ROLES()",
										"sql : " + sb[i]);
						}
						int rslt = stmt.executeUpdate(sb[i].toString());
						if(rslt < 1){
								if(DebugVal){
									DebugPrint(	"PushTableData_Job",
												"insertRecordsForETS_ROLES()",
												"rslt	<	1");
								}
						 throw new Exception();
							}
					}	
					
							
			}
			catch(SQLException sqle){
				DebugPrint("Class:PushTableData_Job","Method:insertRecordsForETS_PROJECTS()","SQLException caught...");			
				sqle.printStackTrace();
				return false;//"finally" gets executed before "return"
			}
			catch(Exception e){
				DebugPrint("Class:PushTableData_Job","Method:insertRecordsForETS_PROJECTS()","Exception caught...");			
				e.printStackTrace();
				return false;//"finally" gets executed before "return"
			}
			finally{
				if(stmt != null){
					stmt.close();
					stmt	= null;	
				}
				
			}

			setRoleIDforETS_USERS(maxRoleID);
			
			return true;
}

private void setRoleIDforETS_USERS(int roleID){
	if(roleID != -1)
		this.roleID	=	roleID -1;	
	else this.roleID	=	-1;
}
private int getRoleIDforETS_USERS(){
	return this.roleID;
}
private int RetrieveMaxRoleIDfromETS_ROLES(Connection conn, String tablename) throws SQLException, Exception{
			Statement stmt	=	null;
			ResultSet rslt	=	null;
			int maxn		=	-1;
			try{
				String str	=	"SELECT MAX(ROLE_ID) FROM " + tablename; 
				stmt		=	conn.createStatement();
				rslt		=	stmt.executeQuery(str);
				
				while(rslt.next())
					maxn		=	rslt.getInt(1) + 1;
					if(DebugVal){
							DebugPrint(	"Class:PushTableData_Job","Method:RetrieveMaxRoleIDfromETS_ROLES",
							"Max(RoleID) retrieved : " + maxn); 	
				}
				
			}
			
			catch(SQLException sqle){
				
				sqle.printStackTrace();
			}
			catch(Exception e){
			
				e.printStackTrace();
			}
			finally{
				if(stmt != null){
					stmt.close();
					stmt	= null;	
				}
				
			}
			return maxn;
			
}

private boolean insertRecordsForETS_USERS(Connection conn, String tablename, Vector v, String type) throws SQLException, Exception{
			if(tablename == null || !tablename.startsWith("ets."))
			tablename = "ets.ETS_USERS";
		
			boolean rtrn = true;
			Statement stmt	= null;
			String userRtrn	= null;

			try{
					stmt		= conn.createStatement();
					int size	= v.size();
					if(DebugVal){
						DebugPrint(	"C:PushTableData_Job",
									"M:insertRecordsForETS_USERS",
									"VECTOR SIZE: " + size);
					}
					String UsrProjID	= (String)v.get(0);
					String UID			= null;
					if(type.equalsIgnoreCase("principal")){
							UID			= (String)v.get(4);
					}
					 if(type.equalsIgnoreCase("bus_solution_mgr")){
							UID			= (String)v.get(5);
					}
					else if(type.equalsIgnoreCase("client_service_mgr")){
							UID			= (String)v.get(6);
					}
					else if(type.equalsIgnoreCase("ibm_team_members")){
							UID			= (String)v.get(7);
					}
					int UsrRoleID		= getRoleIDforETS_USERS();
					if(UsrProjID == null ){
								if(DebugVal){
									DebugPrint(	"C:PushTableData_Job",
												"M:insertRecordsForETS_USERS()",
												"USER PROJECT ID: NULL");
								}
								
								throw new Exception();
					}
					if(UID == null ){
								if(DebugVal){
									DebugPrint(	"C:PushTableData_Job",
												"M:insertRecordsForETS_USERS()",
												"USER ID: NULL");
								}
								
								throw new Exception();
					}
					if(UsrRoleID == -1 ){
								if(DebugVal){
									DebugPrint(	"C:PushTableData_Job",
												"M:insertRecordsForETS_USERS()",
												"USER ROLE ID: NULL");
								}
					
								throw new Exception();
					}
					String usrJob			= null;
					String primaryContact	=	"";
					String lastUID			= last_userid;
					StringTokenizer tokens	= null;
					
					if(DebugVal){
						DebugPrint(	this.getClass().toString(),
									"M:insertRecordsForETS_USERS()",
									" Inserting in ETS_USERS: " + type);
										
						}
					if(type.equalsIgnoreCase("principal")){
						usrJob			= usr_job_principal;	
						primaryContact	= "Y";
						UID				= convertToShortName(UID);
					}
					else if(type.equalsIgnoreCase("bus_solution_mgr")){
						usrJob		= usr_job_bus_solution_mgr;
						UID			= convertToShortName(UID);	
					}
					else if(type.equalsIgnoreCase("client_service_mgr")){
						usrJob		= usr_job_client_service_mgr;
						
						
						if(UID.indexOf(";") != -1){
        			            tokens = new StringTokenizer(UID, ";") ; /* parsing when
                                                  					  * the parsing
                                                  					  * string is a
                                                   					  * semicolon */
                                ClientServiceTokens = new StringTokenizer(UID, ";");
        				}
        				else if(UID.indexOf(",") != -1){
            					tokens = new StringTokenizer(UID, ",");	/* parsing when
                                                								* the parsing
                                                								* string is a
                                                								* comma */
                                ClientServiceTokens = new StringTokenizer(UID, ",");                                                								
        				}
        				else{
         						UID	= UID.trim();
         						ClientServiceTokens = new StringTokenizer(UID, ",");          
        				}	
        				
						
					}
					else if(type.equalsIgnoreCase("ibm_team_members")){
						usrJob		= "Team Member";
						
						if(UID.indexOf(";") != -1){
        			            tokens = new StringTokenizer(UID, ";") ; /* parsing when
                                                  					  * the parsing
                                                  					  * string is a
                                                   					  * semicolon */
                                IBMTeamMembersTokens = new StringTokenizer(UID, ";");                                                   					  
        				}
        				else if(UID.indexOf(",") != -1){
            					tokens = new StringTokenizer(UID, ",");	/* parsing when
                                                								* the parsing
                                                								* string is a
                                                								* comma */
                                IBMTeamMembersTokens = new StringTokenizer(UID, ",");                                                								
        				}
        				else{
         						UID	= UID.trim();
         						IBMTeamMembersTokens = new StringTokenizer(UID, ",");                                                								
        				}	

        				
					}
					if(tokens == null){
						if(DebugVal){
						DebugPrint(	this.getClass().toString(),
									"M:insertRecordsForETS_USERS()",
									" tokens == null. No tokenizing");
										
						}
					StringBuffer sb = new StringBuffer();
					sb.append(	"INSERT INTO " + tablename 
								+ " (USER_ID, USER_ROLE_ID, USER_PROJECT_ID,"
								+ " USER_JOB, PRIMARY_CONTACT, LAST_USERID,"
							  	+ " LAST_TIMESTAMP) VALUES ('" 
								+ UID + "', " + UsrRoleID + ", '"       
								+ UsrProjID + "', '" + usrJob + "', '"
								+ primaryContact + "', '" + lastUID + "', "	
								+ "current timestamp" + ")") ; 
										
							if(DebugVal){
								DebugPrint( "Class:PushTableData_Job", "Method:insertRecordsForETS_USERS()",
											sb.toString());
							}
							int rslt = stmt.executeUpdate(sb.toString());
							if(rslt < 1){
								if(DebugVal){
									DebugPrint(	"PushTableData_Job",
												"insertRecordsForETS_USERS()",
												"rslt	<	1");
								}
								String message = concatMsg(	"PushTableData_Job",
												"insertRecordsForETS_USERS()",
												"rslt	<	1. \n SQL error:" + sb);
								sendMailHtml(mailFm, problem_mailTO, mailHost, message, "Error", "");
						 throw new Exception();
							}
					}//end if tokens == null
					else if(tokens != null){
						if(DebugVal){
						DebugPrint(	this.getClass().toString(),
									"M:insertRecordsForETS_USERS()",
									" tokens != null. Tokenizing");
										
						}
						while( tokens.hasMoreTokens()){
    	              		String string	= (String)tokens.nextToken();
        	        		string			= string.trim();
        	        		string			= convertToShortName(string);
        	        		StringBuffer sb = new StringBuffer();
							sb.append(	"INSERT INTO " + tablename 
								+ " (USER_ID, USER_ROLE_ID, USER_PROJECT_ID,"
								+ " USER_JOB, PRIMARY_CONTACT, LAST_USERID,"
							  	+ " LAST_TIMESTAMP) VALUES ('" 
								+ string + "', " + UsrRoleID + ", '"       
								+ UsrProjID + "', '" + usrJob + "', '"
								+ primaryContact + "', '" + lastUID + "', "	
								+ "current timestamp" + ")") ; 
										
							if(DebugVal){
								DebugPrint( "Class:PushTableData_Job", "Method:insertRecordsForETS_PROJECTS()",
											sb.toString());
							}
							int rslt = stmt.executeUpdate(sb.toString());
							if(rslt < 1){
								if(DebugVal){
									DebugPrint(	"PushTableData_Job",
												"insertRecordsForETS_PROJECTS()",
												"rslt	<	1");
								}
								String message = concatMsg(	"PushTableData_Job",
												"insertRecordsForETS_PROJECTS()",
												"rslt	<	1. \n SQL error:" + sb);
								sendMailHtml(mailFm, problem_mailTO, mailHost, message, "Error", "");
						 throw new Exception();
							}
        	        	
						}
					}		
			}
			catch(SQLException sqle){
				DebugPrint("Class:PushTableData_Job","Method:insertRecordsForETS_USERS()","SQLException caught...");			
				sqle.printStackTrace();
				rtrn = false;//"finally" gets executed before "return"
			}
			catch(Exception e){
				DebugPrint("Class:PushTableData_Job","Method:insertRecordsForETS_USERS()","Exception caught...");			
				e.printStackTrace();
				rtrn = false;//"finally" gets executed before "return"
			}
			finally{
				if(stmt != null){
					stmt.close();
					stmt	= null;	
				}
				
			}
			return rtrn;

}

private  void DebugPrint(String classN, String methodN, String val){
		System.out.println(concatMsg(classN, methodN, val));
		try{
			logDebugMessage(concatMsg(classN, methodN, val));
		}
		catch(Exception e){
			System.out.println("Error writing to the log file");
			e.printStackTrace();
		}
	}
private String concatMsg(String classN, String methodN, String val){
		String 	rtr 	=	"\n" 
						+	classN	+	" : "
						+	methodN	+	" : \n"
						+	val;
		return rtr;
}

private void setDebugVal(boolean b){
		DebugVal	=	b;
	}
private boolean getDebugVal(){
		return DebugVal;
	}
private String convertToShortName(String UID) throws Exception{
	
	if((UID == null) || UID.equalsIgnoreCase("") || UID.equalsIgnoreCase("null"))
		return "null";
	String newUID ;
				newUID = UID.substring(0, UID.indexOf("@"));
				if(DebugVal){
				DebugPrint(	this.getClass().toString(),
							"convertToShortName",
							"name converted to : " + newUID);
				}
	return newUID;
}
/**
 * sends an javax mail without cc list
 */

private static boolean sendMailHtml(String from, String to, String host, String sMessage, String Subject, String reply) throws Exception {

	boolean debug = true;
	long sleepTime = 1000 * 1000; // sleep for ...sleepTime
	boolean mailSent = false;


	InternetAddress[] tolist = InternetAddress.parse(to, false);

	// create some properties and get the default Session
	Properties props = new Properties();
	props.put("mail.smtp.host", host);
	javax.mail.Session session = javax.mail.Session.getInstance(props, null);
	if(DebugVal){
	session.setDebug(debug);
	}

	try {

		// create a message

		javax.mail.Message msg = new javax.mail.internet.MimeMessage(session);
		msg.setContent(sMessage, "text/html");
	//	msg.addHeader("X-Priority", "1");
	//	msg.addHeader("Importance", "High");
		
		msg.setFrom(new javax.mail.internet.InternetAddress(from));

		// Create the address

	 	msg.setRecipients(javax.mail.Message.RecipientType.TO, tolist); // takes the TO array

		if(! (reply == null || reply.equals(""))) {
			javax.mail.internet.InternetAddress[] replyto = { new javax.mail.internet.InternetAddress(reply) };
			msg.setReplyTo(replyto);
		}

		msg.setSubject(Subject);

		InetAddress addr = InetAddress.getLocalHost();
		String hostName = addr.getHostName();

		/**
		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setContent(sMessage, "text/html");
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);
		msg.setContent(mp);
		**/
		
		msg.setDataHandler(new DataHandler(new ByteArrayDataSource(sMessage, "text/html")));// where msg is MimeMessage objectTransport.send(msg);		


		for (int i = 0; i <= 5; i++) {
			try {
				javax.mail.Transport.send(msg);
				mailSent = true;
				break;
			} catch (Exception ex) {
				String str = "Thrown while trying to send e-mail (attempt# " + (i + 1) + ") as follows:\n" + sMessage + "\n\n" + "StackTrace:\n" + ex.getMessage() + "\n\n" + "Will Re-try " + (5 - i) + " times\n\n" + "This error was thrown at: " + new java.util.Date();
				System.err.println(str);
			}

			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				String str = "Thrown while WAITING to re-send e-mail\n" + "StackTrace:\n" + e.getMessage() + "\n\n" + "This error was thrown at: " + new java.util.Date();
				System.err.println(str);
			}
		}

		if (!mailSent) {
			String str = "***ERROR***: The following e-mail could NOT be sent despite " + (5 + 1) + " attempts:\n" + sMessage;
			System.err.println(str);
			
		}


	} catch (Exception ex) {
		throw ex;
	}

	return mailSent;

}	



private static Timestamp getCurrentTime()
   { 
    // Instantiates a Date object and calls
    // the getTime method, and creates and
    // returns the Timestamp object with the
    // current time. In one line!
    return new Timestamp(new Date().getTime());
   }
   
private static Timestamp Yesterdays_Time(){
	
	long ms 		= new Date().getTime();
	ms				= ms - (24*60*60*1000);
	
	Timestamp	ts =	new Timestamp(ms);

	return ts;
}

private boolean sendMailToPrincipal(Connection conn, Vector v){
		boolean rtrn	= true;
		String msg ="";
		String probMsg	= "Couldn't send mail to Principal. SelectMessage returned null";
		String sbj	=""	;
		String TO		= (String)v.get(4);
	//	System.out.println("Should be sending mail to principal: " + TO);
		
		try{
		int state		= getUSERIDState(conn, convertToShortName(TO.trim()).trim());

		String ID = "";
		if(state == 1 || state == 2)
			ID	= (String)getUSERID(conn, convertToShortName(TO.trim()).trim());
			
		if(ID == null || ID.equalsIgnoreCase(""))
			ID	= new String(convertToShortName(TO.trim()));
		

		
		
			msg			= selectMessage(state, wmprivname, v, ID);
			sbj			= selectSubject(wmprivname);
			if(msg != null && sbj != null){
				rtrn		= sendMailHtml(mailFm,  TO /*problem_mailTO*/, mailHost, msg, sbj, "");
				if(shouldIGetEmail == 1){
					sendMailHtml(mailFm,  mailcopy, mailHost, msg, "CC : [ " + sbj + " ]", "");
				}
			}
			else
				sendMailHtml(mailFm, problem_mailTO, mailHost, probMsg, sbj, "");
		}
		catch(Exception e){
			rtrn = false;
			if(DebugVal){
			DebugPrint(	"PushTableData_Job","sendMailPrincipal",
						"Exception caused while sending mail to principal\n"
									+ "\n\t mailFm: " + mailFm
									+ "\n\t problem_mailTO: " + problem_mailTO
									+ "\n\t mailHost: " + mailHost
									+ "\n\t msg: " + msg
									+ "\n\t sbj: " + sbj);
			}
		}
		return rtrn;
		
	}
private boolean sendMailToBus_Solution_Mgr(Connection conn, Vector v){
		boolean rtrn	= true;
		String msg		= "This is the message to the BSM \n" + DisplayRecord(v);
		String probMsg	= "Couldn't send mail to Principal. SelectMessage returned null";
		String sbj		= "Business Solution manager ROCKS";
		String TO		= (String)v.get(5);
	//	System.out.println("Should be sending mail to Business Solution Manager: " + TO);
		
		try{
		int state		= getUSERIDState(conn, TO.trim());

		String ID = "";
		if(state == 1 || state == 2)
			ID	= new String((String)getUSERID(conn, convertToShortName(TO.trim()).trim()));
		if(ID == null || ID.equalsIgnoreCase(""))
			ID	= new String(convertToShortName(TO.trim()));
		
		
		
			msg			= selectMessage(state, mprivname, v, ID);
			sbj			= selectSubject(mprivname);
			if(msg != null && sbj != null){
				rtrn		= sendMailHtml(mailFm,  TO/*problem_mailTO*/, mailHost, msg, sbj, "");
				if(shouldIGetEmail == 1){
					sendMailHtml(mailFm,  mailcopy, mailHost, msg, "CC : [ " + sbj + " ]", "");
				}
			}
				
			else
				sendMailHtml(mailFm, problem_mailTO, mailHost, probMsg, sbj, "");
		}
		catch(Exception e){
			rtrn = false;
			if(DebugVal){
			DebugPrint(	"PushTableData_Job","sendMailToBus_Solution_Mgr",
						"Exception caused while sending mail to business solution manager\n"
									+ "\n\t mailFm: " + mailFm
									+ "\n\t problem_mailTO: " + problem_mailTO
									+ "\n\t mailHost: " + mailHost
									+ "\n\t msg: " + msg
									+ "\n\t sbj: " + sbj);
			}
		}
		return rtrn;
		
	}
private boolean sendMailToClient_Service_Mgr(Connection conn, Vector v){
		boolean rtrn	= false;
		String msg		= "This is the message to the CSM \n" + DisplayRecord(v);
		String probMsg	= "Couldn't send mail to Principal. SelectMessage returned null";
	
		if(ClientServiceTokens != null){
		while(ClientServiceTokens.hasMoreTokens()){
           	String string	= (String)ClientServiceTokens.nextToken();
        	String sbj		= "Client Solution manager ROCKS..Should be sending this mail to:"
        					  + string ;
			String TO		= string;
		
			try{
				int state		= getUSERIDState(conn, TO.trim());
				String ID = "";
				if(state == 1 || state == 2)
					ID	= new String((String)getUSERID(conn, convertToShortName(TO.trim()).trim()));
				if(ID == null || ID.equalsIgnoreCase("") )
					ID	= new String(convertToShortName(TO.trim()));
		
		
					msg			= selectMessage(state, mprivname, v, ID);
				sbj			= selectSubject(mprivname);
				if(msg != null && sbj != null){
					rtrn		= sendMailHtml(mailFm,  TO/* problem_mailTO*/, mailHost, msg, sbj, "");
					if(shouldIGetEmail == 1){
					sendMailHtml(mailFm,  mailcopy, mailHost, msg, "CC : [ " + sbj + " ]", "");
				}
				}
				else{
					sendMailHtml(mailFm, problem_mailTO, mailHost, probMsg, sbj, "");
					rtrn = false;
					break;
				}
			}
			catch(Exception e){
				rtrn = false;
				DebugPrint(	"PushTableData_Job","sendMailToClient_Service_Mgr",
							"Exception caused while sending mail to client service manager\n"
									+ "\n\t mailFm: " + mailFm
									+ "\n\t mailTO: " + string
									+ "\n\t mailHost: " + mailHost
									+ "\n\t msg: " + msg
									+ "\n\t sbj: " + sbj);	
				break;
			}
		}
		}	
		return rtrn;
		
	}
		
private boolean sendFinalMail(Vector v){
			boolean rtrn	= true;
		String msg		= selectMessage(-1, null, v, null);
		String sbj		= "E&TS Connect - New Opportunity: " + v.get(0);
		try{
			rtrn		= sendMailHtml(mailFm, /*"subu@us.ibm.com"*/mailTO, mailHost, msg, sbj, "");
			if(shouldIGetEmail == 1){
					sendMailHtml(mailFm,  mailcopy, mailHost, msg, "CC : [ " + sbj + " ]", "");
				}
		}
		catch(Exception e){
			rtrn = false;
			DebugPrint(	"PushTableData_Job","sendFinalMail",
						"Exception caused while sending ETS_OPP_CONTACTS info\n"
									+ "\n\t mailFm: " + mailFm
									+ "\n\t problem_mailTO: " + mailTO
									+ "\n\t mailHost: " + mailHost
									+ "\n\t msg: " + msg
									+ "\n\t sbj: " + sbj);
		}
		return rtrn;
		
	}
private boolean insertRecordsForETS_CAT(Connection conn, String tablename, Vector v) throws SQLException, Exception{
/**************insertRecordsForETS_CAT starts here **********/
			if(tablename == null || !tablename.startsWith("ets."))
			tablename = "ets.ETS_CAT";
		
			boolean rtrn = true;
			Statement stmt	= null;

			try{
					stmt		= conn.createStatement();
					int size	= v.size();
					if(DebugVal){
						DebugPrint(	"C:PushTableData_Job",
									"M:insertRecordsForETS_CAT",
									"VECTOR SIZE: " + size);
					}
					int catID		= RetrieveMaxCatIDfromETS_CAT(conn, tablename);
					String ProjID	= (String)v.get(0);
					String UID		= (String)v.get(4);
					
					if(ProjID == null ){
								if(DebugVal){
									DebugPrint(	"C:PushTableData_Job",
												"M:insertRecordsForETS_CAT()",
												"PROJECT ID: NULL");
								}
								
								throw new Exception();
					}
					if(UID == null ){
								if(DebugVal){
									DebugPrint(	"C:PushTableData_Job",
												"M:insertRecordsForETS_CAT()",
												"USER ID: NULL");
								}
					
								throw new Exception();
					}
			
			String	[]proposal_cat_name			= new String[no_proposal_cat];
			int	[]proposal_cat_type			= new int[no_proposal_cat];
			int	[]proposal_cat_parent_id	= new int[no_proposal_cat];
			int	[]proposal_cat_order		= new int[no_proposal_cat];
			int	[]proposal_cat_view_type	= new int[no_proposal_cat];
			int	[]proposal_cat_id			= new int[no_proposal_cat];

			for(int i = 0; i < no_proposal_cat; i++, catID++){
				int index = i + 1;
				
				proposal_cat_id[i]						= catID;
				
				String proposal_cat_name_Value			= "ets.proposal_cat_name" + index;
				proposal_cat_name[i]					= getParam(rb, proposal_cat_name_Value); 
				
				String proposal_cat_type_Value			= "ets.proposal_cat_type" + index;
				proposal_cat_type[i]					= Integer.parseInt(getParam(rb, proposal_cat_type_Value).trim());
				
				String proposal_cat_parent_id_Value		= "ets.proposal_cat_parent_id" + index;
				proposal_cat_parent_id[i]				= Integer.parseInt(getParam(rb, proposal_cat_parent_id_Value).trim());
				 
				String proposal_cat_order_Value			= "ets.proposal_cat_order" + index;
				proposal_cat_order[i]					= Integer.parseInt(getParam(rb, proposal_cat_order_Value).trim());
				
				String proposal_cat_view_type_Value		= "ets.proposal_cat_view_type" + index;
				proposal_cat_view_type[i]				= Integer.parseInt(getParam(rb, proposal_cat_view_type_Value).trim());
				
				StringBuffer sb = new StringBuffer();
				sb.append(	"INSERT INTO " + tablename 
							+ " (CAT_ID, CAT_NAME, CAT_TYPE,CAT_DESCRIPTION,"
							+ " PARENT_ID, PROJECT_ID, PROJ_DESC, ORDER, VIEW_TYPE,"
							+ " USER_ID, PRIVS, LAST_TIMESTAMP) VALUES (" 
							+ catID + ", '" + proposal_cat_name[i] + "', "       
							+ proposal_cat_type[i] + ", '" + "" + "', "
							+ proposal_cat_parent_id[i] + ", '" + ProjID + "', "	
							+ show_project_desc + ", " + proposal_cat_order[i] + ", " 
							+ proposal_cat_view_type[i] + ", '" + UID + "', '"
							+ "" + "', " + "current timestamp" + ")") ; 
										
				if(DebugVal){
								DebugPrint( "Class:PushTableData_Job", "Method:insertRecordsForETS_CAT()",
											sb.toString());
							}
							int rslt = stmt.executeUpdate(sb.toString());
							if(rslt < 1){
								if(DebugVal){
									DebugPrint(	"PushTableData_Job",
												"insertRecordsForETS_CAT()",
												"rslt	<	1.");
								}
								String message = concatMsg(	"PushTableData_Job",
												"insertRecordsForETS_CAT()",
												"rslt	<	1. \n SQL error:" + sb);
								sendMailHtml(mailFm, problem_mailTO, mailHost, message, "Error", "");
						 		throw new Exception();
							}
				
			}
			String	[]proposal_subcat_name			= new String[no_proposal_subcat];
			int	[]proposal_subcat_type			= new int[no_proposal_subcat];
			String	[]proposal_subcat_parent		= new String[no_proposal_subcat];
			int	[]proposal_subcat_order			= new int[no_proposal_subcat];
			int	[]proposal_subcat_view_type		= new int[no_proposal_subcat];
			for(int i = 0; i < no_proposal_subcat; i++, catID++){
				int index = i + 1;
				String proposal_subcat_name_Value			= "ets.proposal_subcat_name" + index;
				proposal_subcat_name[i]						= getParam(rb, proposal_subcat_name_Value); 
				
				String proposal_subcat_type_Value			= "ets.proposal_subcat_type" + index;
				proposal_subcat_type[i]						= Integer.parseInt(getParam(rb, proposal_subcat_type_Value).trim());
				
				String proposal_subcat_parent_Value			= "ets.proposal_subcat_parent" + index;
				proposal_subcat_parent[i]					= getParam(rb, proposal_subcat_parent_Value);
								
				String proposal_subcat_order_Value			= "ets.proposal_subcat_order" + index;
				proposal_subcat_order[i]					= Integer.parseInt(getParam(rb, proposal_subcat_order_Value).trim());
				
				String proposal_subcat_view_type_Value		= "ets.proposal_subcat_view_type" + index;
				proposal_subcat_view_type[i]				= Integer.parseInt(getParam(rb, proposal_subcat_view_type_Value).trim());
				
				int parentID = 0;
				for(int j = 0; j < no_proposal_cat; j++){
					if(proposal_subcat_parent[i].equals(proposal_cat_name[j])){
						parentID	= proposal_cat_id[j];
						if(DebugVal){
							DebugPrint("Class:PushTableData_Job", "Method:insertRecordsForETS_CAT()",
										"Processing Parent ID: " + parentID + " and Parent Name: " + proposal_cat_name[j]);	
						}
						break;
					}
				}
				if(parentID == 0){
											DebugPrint("Class:PushTableData_Job", "Method:insertRecordsForETS_CAT()",
														"No parent found for " 
									+ proposal_subcat_name[i]);
					
				}
				StringBuffer sb = new StringBuffer();
				sb.append(	"INSERT INTO " + tablename 
							+ " (CAT_ID, CAT_NAME, CAT_TYPE,CAT_DESCRIPTION,"
							+ " PARENT_ID, PROJECT_ID, PROJ_DESC, ORDER, VIEW_TYPE,"
							+ " USER_ID, PRIVS, LAST_TIMESTAMP) VALUES (" 
							+ catID + ", '" + proposal_subcat_name[i] + "', "       
							+ proposal_subcat_type[i] + ", '" + "" + "', "
							+ parentID + ", '" + ProjID + "', "	
							+ show_project_desc + ", " + proposal_subcat_order[i] + ", " 
							+ proposal_subcat_view_type[i] + ", '" + UID + "', '"
							+ "" + "', " + "current timestamp" + ")") ; 
										
				if(DebugVal){
								DebugPrint( "Class:PushTableData_Job", "Method:insertRecordsForETS_CAT()",
											sb.toString());
							}
							int rslt = stmt.executeUpdate(sb.toString());
							if(rslt < 1){
								if(DebugVal){
									DebugPrint(	"PushTableData_Job",
												"insertRecordsForETS_CAT()",
												"rslt	<	1");
								}
								String message = concatMsg(	"PushTableData_Job",
												"insertRecordsForETS_CAT()",
												"rslt	<	1. \n SQL error:" + sb);
								sendMailHtml(mailFm, problem_mailTO, mailHost, message, "Error", "");
						 		throw new Exception();
							}
			}
					

			}
			catch(SQLException sqle){
				DebugPrint("Class:PushTableData_Job","Method:insertRecordsForETS_CAT()","SQLException caught...");			
				sqle.printStackTrace();
				rtrn = false;//"finally" gets executed before "return"
			}
			catch(Exception e){
				DebugPrint("Class:PushTableData_Job","Method:insertRecordsForETS_CAT()","Exception caught...");			
				e.printStackTrace();
				rtrn = false;//"finally" gets executed before "return"
			}
			finally{
				if(stmt != null){
					stmt.close();
					stmt	= null;	
				}
				
			}
			return rtrn;
/*********insertRecordsForETS_CAT ends here *********/
}
private int RetrieveMaxCatIDfromETS_CAT(Connection conn, String tablename) throws SQLException, Exception{
			Statement stmt	=	null;
			ResultSet rslt	=	null;
			int maxn		=	-1;
			try{
				String str	=	"SELECT MAX(CAT_ID) FROM " + tablename; 
				stmt		=	conn.createStatement();
				rslt		=	stmt.executeQuery(str);
				
				while(rslt.next())
					maxn		=	rslt.getInt(1) + 1;
					if(DebugVal){
							DebugPrint(	"Class:PushTableData_Job","Method:RetrieveMaxCatIDfromETS_CAT",
							"Max(CAT_ID) retrieved : " + maxn); 	
				}
				
			}
			
			catch(SQLException sqle){
				
				sqle.printStackTrace();
			}
			catch(Exception e){
			
				e.printStackTrace();
			}
			finally{
				if(stmt != null){
					stmt.close();
					stmt	= null;	
				}
				
			}
			return maxn;
		
}
private String selectMessage(int number, String type, Vector v, String ID){
	
	StringBuffer str	= new StringBuffer();
		str.append("<!doctype html	system \"http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd\" >");
		str.append("<html lang=\"en-us\">");
		str.append("<head>");
		str.append("<meta name=\"GENERATOR\" content=\"IBM WebSphere Studio\" />");
		str.append("<title>\"Approved: Request for creating a Workspace\"</title>");
		str.append("<style type=\"text/css\">");
		str.append("<!--");
		str.append("P {");
		str.append(" font-family: \"Courier New\" font-size: 10pt");
		str.append("}");
if(type == null){
		str.append("TD { font-family: \"Courier New\" font-size: 10pt}");
		
	}
		str.append("-->");
		str.append("</style>");
		str.append("</head>");
		
if(type != null){
	
	if(type.equalsIgnoreCase(wmprivname))
	{
		str.append(	"<body alink=\"#0000ff\" bgcolor=\"#ffffff\" leftmargin=\"2\" topmargin=\"2\" marginwidth=\"2\" marginheight=\"2\"><br />Your request for creating a Workspace"
					+ " on E&TS connect for the following proposal"
					+ " has been approved:<br />");
		str.append(v.get(1) + "<br />");
		str.append("<br />");
		str.append("You have been assigned as a Workspace Manager.<br />");
		str.append("<br />");
		str.append(	"The details of the proposal  "
					+ "\"" + v.get(1) + "\" "
					+ "are as follows: <br />");
		str.append("<br />");
		str.append("==============================================================<br />");
		str.append("<br />");
		str.append("Description");
		str.append("&nbsp;&nbsp; &nbsp;:&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp; &nbsp;&nbsp; &nbsp;");
		str.append(v.get(2) + "<br />");
		str.append("Expiry Date");
		str.append("&nbsp;&nbsp; &nbsp;:&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp; &nbsp;&nbsp; &nbsp;");
		str.append(v.get(3) + " (yyyy/mm/dd)<br />");
		str.append("<br />");
		str.append("==============================================================<br />");
		switch (number){
				
		
			case 0: 
				str.append("Our records show that you are not registered with E&amp;TS Connect. Please follow the following instructions in order to access the WorkSpace:-<br />");
				str.append("Step 1:	Click on the following URL to proceed with the registration.<br />");
				str.append("<a href=\"" + registrationURL +" \">" + registrationURL + "</a>" + "<br />");
				str.append("Step 2:	Once registered, click on the following URL to request ETS_PROJECT entitlement.<br />");
				str.append("<a href=\"" + baseURL + entitlementURL +" \">" + baseURL + entitlementURL + "</a>" + "<br />");
				str.append("Step 3:	Click on the following URL to access the Workspace.<br />");
				str.append("<a href=\"" + baseURL + connectURL +" \">" + baseURL + connectURL + "</a>" + "<br />");
				str.append("<br />");
				break;
			case 1:
				str.append("Our records indicate that you are registered as " 
							+ "\"" +  ID  + "\"" + " with E&TS Connect but do not have the entitlement - ETS_PROJECT."); 
				str.append("<br />Please follow the instructions in order ");
				str.append("to access the workspace:-<br />");
				str.append("Step 1: Click on the following URL to request ETS_PROJECT entitlement.<br />");
				str.append("<a href=\"" + baseURL + entitlementURL +" \">" + baseURL + entitlementURL + "</a>" + "<br />");
				str.append("Step 2: Click on the following URL to access the Workspace.<br />");
				str.append("<a href=\"" + baseURL + connectURL +" \">" + baseURL + connectURL + "</a>" + "<br />");
				str.append("<br />");
				break;
			case 2:
				str.append(	"Our records indicate that you are registered as "  
				 			+  ID + " with E&TS Connect.<br />");
				str.append("Please click on the following URL to access the Workspace.<br />");
				str.append("<a href=\"" + baseURL + connectURL +" \">" + baseURL + connectURL + "</a>" + "<br />");
				str.append("<br />");
				break;
				
			default:
				return null;
		}

	}
	else if(type.equalsIgnoreCase(mprivname)){
				str.append("<br />You have been assigned as     ");
				str.append(" Member for the following proposal created on E&TS connect:<br />");
				str.append(v.get(1) + "<br />");
				str.append("<br />");
				str.append(	"The details of the proposal  "
							+ "\"" + v.get(1) + "\" "
							+ "are as follows: <br />");
				str.append("<br />");
				str.append("==============================================================<br />");
				str.append("<br />");
				str.append("Description:    	 " + v.get(2) + "<br />");
				str.append("Expiry Date:           " + v.get(3) + " (mm/dd/yyyy)<br />");
				str.append("<br />");
				str.append("==============================================================<br />");
		switch (number){
		
			case 0: 	
				str.append("Our records show that you are not registered with E&amp;TS Connect. Please follow the following instructions in order to access the WorkSpace:-<br />");
				str.append("Step 1:	Click on the following URL to proceed with the registration.<br />");
				str.append("<a href=\"" + registrationURL +" \">" + registrationURL + "</a>" + "<br />");
				str.append("Step 2:	Once registered, click on the following URL to request ETS_PROJECT entitlement.<br />");
				str.append("<a href=\"" + baseURL + entitlementURL +" \">" + baseURL + entitlementURL + "</a>" + "<br />");
				str.append("Step 3:	Click on the following URL to access the Workspace.<br />");
				str.append("<a href=\"" + baseURL + connectURL +" \">" + baseURL + connectURL + "</a>" + "<br />");
				str.append("<br />");
				break;
			case 1:
				str.append("Our records indicate that you are registered as " 
							+  ID  + " with E&TS Connect but do not have the entitlement - ETS_PROJECT."); 
				str.append("<br />Please follow the instructions in order ");
				str.append("to access the workspace:-<br />");
				str.append("Step 1: Click on the following URL to request ETS_PROJECT entitlement.<br />");
				str.append("<a href=\"" + baseURL + entitlementURL +" \">" + baseURL + entitlementURL + "</a>" + "<br />");
				str.append("Step 2: Click on the following URL to access the Workspace.<br />");
				str.append("<a href=\"" + baseURL + connectURL +" \">" + baseURL + connectURL + "</a>" + "<br />");
				str.append("<br />");
				break;
			case 2:
				str.append(	"Our records indicate that you are registered as "  
				 			+  ID + " with E&TS Connect.<br />");
				str.append("Please click on the following URL to access the Workspace.<br />");
				str.append("<a href=\"" + baseURL + connectURL +" \">" + baseURL + connectURL + "</a>" + "<br />");
				str.append("<br />");
				break;
			default:
				return null;
		}
		
	}
	else{ return null;}
	
}
else{//Final mail that has to be sent to anne bierce("TO" field changeable)
				str.append("<body alink=\"#0000ff\" bgcolor=\"#ffffff\" leftmargin=\"2\" topmargin=\"2\" marginwidth=\"2\" marginheight=\"2\">");
				str.append("<br />A new Opportunity has been created on E&amp;TS Connect.<br />");
				str.append("The contact information of the opportunity: ");
				str.append("\"" + (String)v.get(0) + "\"");
				str.append(" are as follows: <br />");
				str.append("<br />");
				str.append("==============================================================<br />");
				str.append("<table border=\"0\">");
				str.append("<tbody><tr><td>Opportunity Id</td><td>:</td>");
				str.append("<td>");
				str.append((String)v.get(0));
				str.append("</td>");
				str.append("</tr><tr><td>Contact No.</td><td>:</td>");
				str.append("<td>");
				str.append(((Integer)v.get(1)).toString());
				str.append("</td>");
				str.append("</tr>");
				str.append("<tr>");
				str.append("<td>Name</td>");
				str.append("<td>:</td><td>");
				str.append((String)v.get(2));
				str.append("</td></tr><tr><td>Title</td><td>:</td><td>");
				str.append((String)v.get(3));
				str.append("</td></tr><tr><td>Phone</td><td>:</td><td>");
				str.append((String)v.get(4));
				str.append("</td></tr><tr><td>E-Mail</td><td>:</td><td>");
				str.append((String)v.get(5));
				str.append("</td></tr><tr><td>TimeStamp</td><td>:</td><td>");
				str.append(((Timestamp)v.get(6)).toString());
				str.append("</td></tr></tbody></table>");
	}
	str.append("==============================================================<br />");
	str.append("Delivered by E&amp;TS Connect.<br />");
	str.append("This is a system generated email. <br />");
	str.append("==============================================================<br />");
	str.append("</body>");
	str.append("</html>");
	return str.toString();
}

private int getUSERIDState(Connection conn, String UID){
		int state = 0;
try{
		 String edgeID = AccessCntrlFuncs.getEdgeUserId(conn , UID ) ;
		 if(edgeID != null){
			edgeID	= edgeID.trim();
				if(!edgeID.equalsIgnoreCase(""))
					state = 1;
		 		
		 		EdgeAccessCntrl es= new EdgeAccessCntrl();
				es.getEdgeUserProfile(conn , edgeID );
				//System.out.println("edgeuid: " + es.gCOUNTRY); 
				if(es.qualifyEntitlement("ETS_PROJECTS")){
					//System.out.println("The user has entitlement") ;
					state = 2;
				}
		 	}

		}
		catch(Exception e){}
return state;	
}
private String getUSERID(Connection conn, String UID){
String str = null;
try{
	String edgeID = AccessCntrlFuncs.getEdgeUserId(conn , UID ) ;
		 if(edgeID != null){
			edgeID	= edgeID.trim();
				if(!edgeID.equalsIgnoreCase("")){
					str = edgeID;
				}
		}
	}
	catch(Exception e){}
return str;	
}
private String selectSubject(String type){
	String str	= null;
	if(type != null){
	if(type.equalsIgnoreCase(wmprivname)){
		str	= new String("E&TS Connect - [Approved : Request for creating a Workspace]" );
	}
	else if(type.equalsIgnoreCase(mprivname)){
		str	= new String("E&TS Connect - New Proposal : [Assigned: Member]");
	}
	}
		
	return str;	
}
private Vector SearchOPPIDinETS_OPP_CONTACTS(Connection conn, String tablename, String searchStr) throws SQLException, Exception{
	Vector v = null;
		if(tablename == null || !tablename.startsWith("ets."))
		tablename = "ets.ETS_OPP_CONTACTS";
		
			Statement stmt	= null;
			ResultSet rslt	= null;
			
			try{
					stmt		= conn.createStatement();
					String sb	=	"select * from " + tablename 
									+ " where OPPORTUNITY_ID = '" 
									+ searchStr
									+ "'";
					rslt		= stmt.executeQuery(sb);
					if(rslt.next()){
						if(DebugVal){
						DebugPrint("PushTableData_Job","SearchOPPIDinETS_OPP_CONTACTS",	
										"Record found in " + tablename );
								}
								
						String oppid	=	rslt.getString(1);
						int contact_no	=	rslt.getInt(2);
						String name		=	rslt.getString(3);
						String title	=	rslt.getString(4);
						String phone	=	rslt.getString(5);
						String email	=	rslt.getString(6);
						Timestamp t		= 	rslt.getTimestamp(7);
						v = new Vector();
						v.add(oppid);	v.add(new Integer(contact_no));
						v.add(name);	v.add(title);
						v.add(phone);	v.add(email);
						v.add(t);
								
							
						}
			}
			
			catch(SQLException sqle){
				
				sqle.printStackTrace();
			}
			catch(Exception e){
				
				e.printStackTrace();
			}
			finally{
				if(stmt != null){
					stmt.close();
					stmt	= null;	
				}
				
			}
	return v;
}
private boolean FindDuplicatesOrNullsInRecords(Vector v){
	Vector newVec =new Vector();
	for(int i = 4; i< v.size(); i++){
		String nRec	= (String)v.get(i);
		nRec		= nRec.trim();
				
		if((nRec == null) 					||
			nRec.equalsIgnoreCase("null") 	||
			nRec.equalsIgnoreCase("")){
				return true;
			}
		if(nRec.indexOf(",") != -1){

			StringTokenizer str = new StringTokenizer(nRec, ",");
			while(str.hasMoreTokens()){
				String st = str.nextToken().toString().trim();
				if( (st == null)					||
					st.equalsIgnoreCase("null") 	||
					st.equalsIgnoreCase("")){
							return true;
					}
								
				newVec.add(st);
			}
		}
		else if(nRec.indexOf(";") != -1){
			StringTokenizer str = new StringTokenizer(nRec, ",");
			while(str.hasMoreTokens()){
				String st = str.nextToken().toString().trim();
				if( (st == null)					||
					st.equalsIgnoreCase("null") 	||
					st.equalsIgnoreCase("")){
							return true;
					}
				newVec.add(st);
			}
		}
		else{
			newVec.add(nRec);	
		}
			
		
	}
	DebugPrint("Class:PushTableData_Job", "Method:FindDuplicatesOrNullsInTheRecords",
				"Tokenizing the elements to find the duplicates");
	for(int i =0 ; i < newVec.size(); i++)
		System.out.println( newVec.get(i));
	for(int i = 0; i< newVec.size(); i++){
			for(int j = i+1; j < newVec.size(); j++){
				if(((String)newVec.get(i)).equalsIgnoreCase((String)newVec.get(j)))
					 return true;	
				
			}
			
	}
	return false;
	
}
private static void logDebugMessage(String sMessage) throws IOException, Exception {

		if (Global.comments) {
			Global.println("[DEBUG] " + sMessage);
		}

	}

private static void sendSuccessMailToSubu(StringBuffer strm) throws SQLException, Exception{
	String message = " PushTableData_Job script completed. [ " + getCurrentTime() + " ]" + "\n" + strm;
	sendMailHtml(mailFm, problem_mailTO, mailHost, message, "success" + message, "");
	
}

}
