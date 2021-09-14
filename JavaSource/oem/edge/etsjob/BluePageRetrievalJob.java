package oem.edge.etsjob;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: IBM Customer Connect                                          */
/* (C) Copyright IBM Corp. 2000 - 2001                                       */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** EOF : HEADER *************************************/
/*									 */
/*	File Name 	: 	CMSubscription_Job.java			 */
/*	Class Path	:	oem.edge.mailpref			 */
/*	Release		:	03.02.1					 */
/*	Description	:	BluePageRetrievalJob job		 */
/*	Created By	: 	Subu Sundaram				 */
/*	Date		:	10/14/2003				 */
/*****************************************************************************/
/*  Change Log 	: 	Please Enter Changed on, Changed by and Desc	 */
/*  				in getChangeLog	method 		  	 */
/*****************************************************************************/


/**
 * @author: Subu Sundaram
 */

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.sql.Timestamp;
import java.util.Date;

import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Calendar;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.sql.SQLException;
import java.io.IOException;
import java.io.PrintStream;

import java.util.MissingResourceException;

import com.ibm.bluepages.*;
import java.sql.DriverManager;

import oem.edge.ed.util.EDCMafsFile;
import oem.edge.ed.util.PasswordUtils;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

public class BluePageRetrievalJob  {

public static EDCMafsFile authenticator = new EDCMafsFile();
   private static long AUTH_DURATION = 4 * 60 * 60 * 1000  ;
   private static long nextAuthenticate = 0;
   private static String proxyPath="";
   private static String afsCell;
   private static String afsUid ;
   private static String afsPwdPath;

   private static String outFilePath;

   private static String driver_class;
   private static String connect_string;
   private static String db2usr;
   private static String db2pw;
   private static String mailHost;
   private static String mailFm;
   private static String mailto;
   private static String problem_mailto;


   private static java.util.ResourceBundle prop  = null;
   private final static java.lang.String propName = "ts";
   private static String timestampLocation;
   private static boolean DebugVal = false;

   FileOutputStream out = null;

   public BluePageRetrievalJob(){
      super();
   }

   private static synchronized boolean authenticate() {
      System.out.println("Entering authenticate()...");
      boolean authOK = false;
//	if(System.currentTimeMillis() > nextAuthenticate) {
      if(authenticator.afsAuthenticate(afsCell, afsUid, PasswordUtils.getPassword(afsPwdPath))) {
         System.out.println("Authentication OK");
         nextAuthenticate = System.currentTimeMillis() + AUTH_DURATION;
         authOK = true;
      }
      else
         authOK = false;
     //       }
     //      else
     //         authOK = true;  

      if( ! authOK ) {
         System.out.println("Authentication Failure");
      }
  System.out.println("leaving authenticate()...");
  return authOK;
   }
   private void sendOutputToFile(String outFName){
      try{
         Calendar tempdate = Calendar.getInstance();
         tempdate.setTime(new Date(System.currentTimeMillis()));
         System.out.println("writing to btv cell...");
         String outFilename =  outFName + new String("-" + tempdate.get(Calendar.YEAR)+"-"+(tempdate.get(Calendar.MONTH)+1) + "-" + tempdate.get(Calendar.DAY_OF_MONTH));

         out = new FileOutputStream(outFilename, true);
         PrintStream temp = new PrintStream(out);
         System.setOut(temp);
      }
      catch(Exception e){
         System.out.println(e.getMessage());
      }



   }
   private boolean Init() {
      boolean success = true;

      try {
         prop = ResourceBundle.getBundle("properties/oem/edge/etsjob/conversion");
         driver_class   = getParam(prop, "conversion.driver_class");
         connect_string = getParam(prop, "conversion.connect_string");
         db2usr         = getParam(prop, "conversion.db2usr");
         db2pw          = getParam(prop, "conversion.db2pw");
         afsCell        = getParam(prop, "conversion.AFSCell");
         afsUid         = getParam(prop, "conversion.AFSUid");
         afsPwdPath     = getParam(prop, "conversion.AFSPwdDir");
         outFilePath    = getParam(prop, "conversion.outputLog");
		 mailHost		= getParam(prop, "conversion.mailHost");
		 mailFm			= getParam(prop, "conversion.mailFm");
		 mailto			= getParam(prop, "conversion.mailto");
		 problem_mailto = getParam(prop, "conversion.problem_mailto");

         System.out.println("afsCell : [ " + afsCell + " ]");
         System.out.println("afsUid : [ " + afsUid + " ]");
         System.out.println("afsPwdPath : [ " + afsPwdPath + " ]");
         System.out.println("outFilePath : [ " + outFilePath + " ]");
         System.out.println("db2usr : [ " + db2usr + " ]");
         System.out.println("db2pw : [ " + db2pw + " ]");
         System.out.println("driver_class : [ " + driver_class + " ]");
         System.out.println("connect_string : [ " + connect_string + " ]");
         System.out.println("mailHost : [ " + mailHost + " ]");
         System.out.println("mailFm : [ " + mailFm + " ]");
         System.out.println("mailto : [ " + mailto + " ]");
         System.out.println("problem_mailto : [ " + problem_mailto + " ]");

      }
      catch(Exception x){
         System.out.println("INIT FAILED: [ " + x.getMessage() + " ]");
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
			 System.out.println("Global: param=[" + param + "] init failed");
			return "";
		}
		if (result.length() == 0) {
			 System.out.println("Global: param=[" + param + "] have zero length");
		}
		return result;
	}
		catch (MissingResourceException mre) {
		
			System.out.println(	"ets PROPERTY" +
						"getParam" +
						"Globsl: MissingResourceException param=[" + param + "] " + mre.getMessage());
		

		result="";
	}
	return result;
}
public String JobImplementation(Connection conn, String user, String func, String division, String start_date, String start_time, String end_date, String end_time) throws SQLException {

	try{
	/*	if(!this.Init()){
				System.out.println(	"Program Exiting...Property file " +
									" couldn't get initialized");
				return null;
			} */
  	/*	if(true){
			callDummyInsert(conn);
			return null;
  		}
	*/

	 Vector v = readOpportunityID(conn,"ets.ETS_OPPORTUNITY");
	 if(!v.isEmpty()){
	 	System.out.println("The vector size from readOpportunityID is : " + v.size());
		 for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
		 	Vector vel = (Vector)e.nextElement();
		 	System.out.println("\nCALLING UPDATE ON...");
		 	    String sOppID				= "";
				String bus_lead_mgr			= "";
				String principal			= "";
				String bus_sol_mgr			= "";
				String client_service_mgr	= "";
				String ibm_team_members		= "";
				if(vel.elementAt(0) != null)
				 	 sOppID				= (String)vel.elementAt(0);
				if(vel.elementAt(1) != null)
				 	 bus_lead_mgr			= (String)vel.elementAt(1);
				if(vel.elementAt(2) != null)
				 	 principal			= (String)vel.elementAt(2);
				if(vel.elementAt(3) != null)
				 	 bus_sol_mgr			= (String)vel.elementAt(3);
				if(vel.elementAt(4) != null)
				 	 client_service_mgr	= (String)vel.elementAt(4);
				if(vel.elementAt(5) != null)
				 	 ibm_team_members		= (String)vel.elementAt(5);

				System.out.println("The opportunity id is : " + sOppID);
				System.out.println("The Business Solution Manager : " + bus_sol_mgr);
				System.out.println("Principal : " + principal );
				System.out.println("The Business lead Manager : " + bus_lead_mgr );
				System.out.println("The client service Manager : " + client_service_mgr );
				System.out.println("The IBM team members : " + ibm_team_members + "\n");

		 	callUpdate(	conn, sOppID, bus_lead_mgr, principal,
		 				bus_sol_mgr, client_service_mgr, ibm_team_members);

//		 	for(Enumeration enum = vel.elements(); enum.hasMoreElements();){
//		 			System.out.println("\n"+ enum.nextElement()+ " row " + v.get(0) + "2nd element " + vel.get(1));
//					callUpdate(	conn, (String)enum.nextElement(), (String)enum.nextElement(), (String)enum.nextElement(),
//								(String)enum.nextElement(), (String)enum.nextElement(), (String)enum.nextElement());
//		 	}
		 }

	 }



	}

	catch(SQLException sqle){
		System.out.println(sqle.getMessage());
	}
	catch(IOException ioe){
		System.out.println(ioe.getMessage());
		ioe.printStackTrace();
	}
	catch(Exception e){
		System.out.println(e.getMessage());
	}

		return null;
	}

public String mailFormat (String str,Connection conn, String user, String function,String division,String tag) throws SQLException {
		return null;
	}
public static void callDummyInsert(Connection conn) throws SQLException, Exception{
		Statement stmt	= null;
		ResultSet rs	= null;
		StringBuffer sQuery = new StringBuffer();
		StringBuffer sQuery1 = new StringBuffer();
		StringBuffer sQuery2 = new StringBuffer();
		StringBuffer sQuery3 = new StringBuffer();
		StringBuffer sQuery4 = new StringBuffer();
		try{
		sQuery.append("INSERT INTO ets.ETS_OPPORTUNITY (OPPORTUNITY_ID,OPPORTUNITY_NAME,OPPORTUNITY_DESC,CODE_NAME,CLIENT_NAME, CDA_NDA, BUS_SOLUTION_MGR,PRINCIPAL, BUS_LEAD_MGR, SSM, IBM_TEAM_MEMBERS, CLIENT_ADDRESS,CLIENT_PHONE, HQ_LOCATION, CLIENT_BRANCH, TIMESTAMP, EXPIRY_DATE) VALUES ('r1','Bugati','bugati','codename1','clientname1', 'y','CN=Jeetendra Rao/OU=Fishkill/O=IBM@IBMUS','CN=Thomas Stranko/OU=Fishkill/O=IBM@IBMUS','Colleen Hayase/OU=Fishkill/O=IBM@IBMUS','ssm1','CN=Colleen Hayase/OU=Fishkill/O=IBM@IBMUS,cn=Gary Coryer/ou=Burlington/o=IBM@IBMUS,cn=Jim Baker/ou=Fishkill/o=IBM@IBMUS','clientaddress1', '845-454-7827','hqlocation1' , 'clientbranch1',	'2003-11-03 18:08:10.347', '04-09-2004')");
		sQuery1.append("INSERT INTO ets.ETS_OPPORTUNITY (OPPORTUNITY_ID,OPPORTUNITY_NAME,OPPORTUNITY_DESC,CODE_NAME,CLIENT_NAME, CDA_NDA, BUS_SOLUTION_MGR,PRINCIPAL, BUS_LEAD_MGR,  SSM, IBM_TEAM_MEMBERS, CLIENT_ADDRESS,CLIENT_PHONE, HQ_LOCATION, CLIENT_BRANCH, TIMESTAMP, EXPIRY_DATE) VALUES ('rcdefghijk3456','Opp6','seco Opportunity','codename2','clientname2', 'y','satkins@us.ibm.com','subu@us.ibm.com','jeetrao@us.ibm.com','ssm1','subu@us.ibm.com  ,waiki@us.ibm.com, crichton@us.ibm.com, staten@us.ibm.com','clientaddress1', '845-454-7827','hqlocation1' , 'clientbranch1',	'2003-10-14 16:08:10.347', '04-09-2003')");
		sQuery2.append("INSERT INTO ets.ETS_OPPORTUNITY (OPPORTUNITY_ID,OPPORTUNITY_NAME,OPPORTUNITY_DESC,CODE_NAME,CLIENT_NAME, CDA_NDA, BUS_SOLUTION_MGR,PRINCIPAL, BUS_LEAD_MGR, CLIENT_SERVICE_MGR, SSM, IBM_TEAM_MEMBERS, CLIENT_ADDRESS,CLIENT_PHONE, HQ_LOCATION, CLIENT_BRANCH, TIMESTAMP, EXPIRY_DATE) VALUES ('rdefghijkl4567','Opp7','third Opportunity','codename3','clientname3', 'n','CN=Debbie Ottmar/OU=Rochester/O=IBM@IBMUS','CN=Debbie Ottmar/OU=Rochester/O=IBM@IBMUS','cn=Lou Biskup/ou=Fishkill/o=IBM@IBMUS','Colleen Hayase/Fishkill/IBM@IBMUS','ssm1','Steven Murray/Fishkill/IBM@IBMUS;Gary Nagelhout/Fishkill/IBM@IBMUS;Jay Hammer/Burlington/IBM@IBMUS','clientaddress1', '845-454-7827','hqlocation1' , 'clientbranch1',	'2003-10-15 16:08:10.347', '12-02-2003')");
		sQuery3.append("INSERT INTO ets.ETS_OPPORTUNITY (OPPORTUNITY_ID,OPPORTUNITY_NAME,OPPORTUNITY_DESC,CODE_NAME,CLIENT_NAME, CDA_NDA, BUS_SOLUTION_MGR,PRINCIPAL, BUS_LEAD_MGR, CLIENT_SERVICE_MGR, SSM, IBM_TEAM_MEMBERS, CLIENT_ADDRESS,CLIENT_PHONE, HQ_LOCATION, CLIENT_BRANCH, TIMESTAMP) VALUES ('refghijklm5678','Opp8','Fourth Opportunity','codename4','clientname4', 'y','CN=William Liu/OU=Fishkill/O=IBM@IBMUS','CN=Debbie Ottmar/OU=Rochester/O=IBM@IBMUS','Lou Biskup/Fishkill/IBM@IBMUS','CN=Colleen Hayase/OU=Fishkill/O=IBM@IBMUS','ssm1','Steven Murray/Fishkill/IBM@IBMUS,Michael Quaranta/Rochester/IBM@IBMUS,Kenneth Mann/Fishkill/IBM@IBMUS','clientaddress1', '845-454-7827','hqlocation1' , 'clientbranch1',	'2003-10-16 16:08:10.347')");
		sQuery4.append("INSERT INTO ets.ETS_OPPORTUNITY (OPPORTUNITY_ID,OPPORTUNITY_NAME,OPPORTUNITY_DESC,CODE_NAME,CLIENT_NAME, CDA_NDA, BUS_SOLUTION_MGR,PRINCIPAL, BUS_LEAD_MGR, CLIENT_SERVICE_MGR, SSM, IBM_TEAM_MEMBERS, CLIENT_ADDRESS,CLIENT_PHONE, HQ_LOCATION, CLIENT_BRANCH, TIMESTAMP) VALUES ('rfghijklmn6789','Opp9','Fifth Opportunity','codename5','clientname5', 'n','CN=Thomas Stranko/OU=Fishkill/O=IBM@IBMUS','CN=Debbie Ottmar/OU=Rochester/IBM@IBMUS','cn=Lou Biskup/Fishkill/o=IBM@IBMUS','CN=Colleen Hayase/OU=Fishkill/O=IBM@IBMUS','ssm1','Gary Nagelhout/Fishkill/IBM@IBMUS','clientaddress1', '845-454-7827','hqlocation1' , 'clientbranch1',	'2003-10-20 16:08:10.347')");

		//sQuery.append("insert into ets.ETS_OPPORTUNITY VALUES ('abcdefghi1234','Opportunity1','First Opportunity','codename1','clientname1', 'y', 02-02-2003, 'bus_solution_manager1','principal1', 'bus_lead_mgr1', 'client_service_mgr1', 'ssm1','subu@us.ibm.com,subs_me@yahoo.com,subs_me@hotmail.com', 'clientaddress1', 'clientphone1', 'hqlocation1' , 'clientbranch1',current timestamp)");
		stmt = conn.createStatement();
		System.out.println(sQuery);
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
public void callDummySelect(Connection conn) throws SQLException, Exception{
		/************/
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {
			sQuery.append("select * from ets.ETS_	USERS");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				String sUserID = rs.getString(1);
				if (!sUserID.trim().equals("")) {
					int iUser_Role_Id = rs.getInt(2);
					String sUser_Project_Id = rs.getString(3);
					String sUser_Job = rs.getString(4);

				}
			}
		} catch (SQLException e) {
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



public static Timestamp getCurrentTime()
   {
    // Instantiates a Date object and calls
    // the getTime method, and creates and
    // returns the Timestamp object with the
    // current time. In one line!
    return new Timestamp(new Date().getTime());
   }


private Vector readOpportunityID(Connection conn, String tablename) throws SQLException, Exception{

		Statement stmt = null;
		ResultSet rs = null;
		Vector vel = new Vector();
		StringBuffer selectQuery = new StringBuffer();
		StringBuffer updateQuery = new StringBuffer();
		try{
			Timestamp tLastTouchDown = null;
			if((tLastTouchDown = Yesterdays_Time()) == null){
				System.out.println("\nError retrieving info from the file...\n" +
									"1. No such file\n" +
									"2. No timestamp stored in the file\n" +
									"3. File Corrupt");
				return null;
			}
			stmt = conn.createStatement();
                       	selectQuery.append("select OPPORTUNITY_ID, PRINCIPAL, BUS_LEAD_MGR,BUS_SOLUTION_MGR, CLIENT_SERVICE_MGR, IBM_TEAM_MEMBERS from " + tablename +" where TIMESTAMP > '" + tLastTouchDown + "'");
                       //	selectQuery.append("select OPPORTUNITY_ID, PRINCIPAL, BUS_LEAD_MGR,BUS_SOLUTION_MGR, IBM_TEAM_MEMBERS from " + tablename +" where TIMESTAMP > '" + tLastTouchDown + "'");

			System.out.println("Select String: [ " + selectQuery.toString() + " ]");
                        
                        System.out.println("Processing..." + selectQuery.toString());
                        rs = stmt.executeQuery(selectQuery.toString());
			while (rs.next()) {
                           String sOppID				= rs.getString(1);
				String principal			= rs.getString(2);
				String bus_lead_mgr			= rs.getString(3);
				String bus_sol_mgr			= rs.getString(4);
				String client_service_mgr	= rs.getString(5);//yahoo;to test on comserv508
				String ibm_team_members		= rs.getString(6);//rs.getString(5);to test on comserv508

				if( sOppID == null
				|| (sOppID.trim()).equalsIgnoreCase("")
				|| (sOppID.trim()).equalsIgnoreCase("null")){
					sOppID="no sOppID assigned@";
				}
				if( principal == null
				|| (principal.trim()).equalsIgnoreCase("")
				|| (principal.trim()).equalsIgnoreCase("null")){
					principal="no principal assigned@";
				}
				if( bus_lead_mgr == null
				|| (bus_lead_mgr.trim()).equalsIgnoreCase("")
				|| (bus_lead_mgr.trim()).equalsIgnoreCase("null")){
					bus_lead_mgr="no bus_lead_mgr assigned@";
				}
				if( bus_sol_mgr == null
				|| (bus_sol_mgr.trim()).equalsIgnoreCase("")
				|| (bus_sol_mgr.trim()).equalsIgnoreCase("null")){
					bus_sol_mgr="no bus_sol_mgr assigned@";
				}
				if( client_service_mgr == null
				|| (client_service_mgr.trim()).equalsIgnoreCase("")
				|| (client_service_mgr.trim()).equalsIgnoreCase("null")){
					client_service_mgr="no client_service_mgr assigned@";
				}
				if( ibm_team_members == null
				|| (ibm_team_members.trim()).equalsIgnoreCase("")
				|| (ibm_team_members.trim()).equalsIgnoreCase("null")){
					ibm_team_members="no ibm_team_members assigned@";
				}

				System.out.println("\nBEFORE CONVERSION...\n");
				System.out.println("The opportunity id is : " + sOppID);
				System.out.println("The Business Solution Manager : " + bus_sol_mgr);
				System.out.println("Principal : " + principal );
				System.out.println("The Business lead Manager : " + bus_lead_mgr );
				System.out.println("The client service Manager : " + client_service_mgr );
				System.out.println("The IBM team members : " + ibm_team_members );

				Vector v = new Vector();
				System.out.println("AFTER CONVERSION TO INTRANET EMAIL...\n");
				v.addElement(sOppID);
				System.out.println("The opportunity id is : " + v.elementAt(0));
				v.addElement(ConvertToIntranetEmail(bus_sol_mgr));
				System.out.println("The Business Solution Manager : " + v.elementAt(1));
				v.addElement(ConvertToIntranetEmail(principal));
				System.out.println("Principal : "  + v.elementAt(2));
				v.addElement(ConvertToIntranetEmail(bus_lead_mgr));
				System.out.println("The Business lead Manager : " + v.elementAt(3));
				v.addElement(ConvertToIntranetEmail(client_service_mgr));
				System.out.println("The client service Manager : " + v.elementAt(4));
				v.addElement(ConvertToIntranetEmail(ibm_team_members));
				System.out.println("The IBM team members : " + v.elementAt(5));

				System.out.println("\n");


				vel.addElement(v);

			}
		}
		catch(SQLException e) {
			System.out.println("SQLException caught..........." + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception caught..........." + e.getMessage());
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			selectQuery = null;
		}
		return vel;
}

private String ConvertToIntranetEmail(String NotesEmail){
    return ExtractInfoFromBluePages(NotesEmail);
}
private String ExtractInfoFromBluePages(String NotesEmail){
		BPResults results;
		Hashtable row	= null;
		boolean notokens	= false;

		StringBuffer strRslt = new StringBuffer();
		boolean added = false;

        StringTokenizer st = null;

        if(NotesEmail.indexOf(";") != -1){

            st = new StringTokenizer(NotesEmail, ";") ; /* parsing when
                                                    /* the parsing
                                                    * string is a
                                                    * semicolon */
        }
        else if(NotesEmail.indexOf(",") != -1){
            st = new StringTokenizer(NotesEmail, ",");/* parsing when
                                                * the parsing
                                                * string is a
                                                * comma */
        }
        else{
         	NotesEmail	= NotesEmail.trim();
         	notokens 	= true;
        }

		if(notokens == false){
			while( st.hasMoreTokens()){
    	              	String string = st.nextToken();
        	        	string = string.trim();
        	        	boolean noconversion = false;
        	        	string = correctTheString(string);

		                		results = BluePages.getPersonsByNotesID(string);
								if (results.rows() == 0) {
									  	BPResults AltResults;
										AltResults	= BluePages.getPersonsByInternet(string);
										if(AltResults.rows()	!= 0){
										//	System.out.println("\nAlready a converted Intranet ID");
											noconversion	= true;
										}
										else{
    	    							    System.out.println("\nNo IBM Intranet id found for : " + string + " \n Inserting null in ets.ets_opportunity");
		    		    		    		continue;
										}
									}
								if(!noconversion){
						      		row = results.getRow(0);
    	  							if(added == true)
      									strRslt.append(", ");
      								strRslt.append((String) row.get("INTERNET"));
								}
								else{
									if(added == true)
      									strRslt.append(", ");
      									strRslt.append(string);

								}


      					added = true;
            	       }
			}
		else{
						NotesEmail = correctTheString(NotesEmail);
 								boolean noconversion	= false;
								results = BluePages.getPersonsByNotesID(NotesEmail);
								if (results.rows() == 0) {
										BPResults AltResults;
										AltResults	= BluePages.getPersonsByInternet(NotesEmail);
										if(AltResults.rows()	!= 0){
								//			System.out.println("\nAlready a converted Intranet ID");
											noconversion	= true;
										}
										else{
    	    					//	    	System.out.println("No IBM Intranet id found for : " + NotesEmail);
		    			    	    		return null;
										}
									}
								if(!noconversion){
						      		row = results.getRow(0);
        	  						strRslt.append((String) row.get("INTERNET"));
    	  						}
								else{
									if(added == true)
      									strRslt.append(", ");
      									strRslt.append(NotesEmail);
								}

      					added = true;
			}
			return new String(strRslt);

	}

private String correctTheString(String str){
		String retstr = null;
		if(str != null){
			StringTokenizer oldst	=	new StringTokenizer(str,"/");
			StringBuffer	newst	=	new StringBuffer("");
/*
			if(oldst.countTokens() != 3 && str.indexOf("@") != -1)
				return str;
			else if(oldst.countTokens() != 3)
				return retstr;
				*/
                        int noTokens = oldst.countTokens();
                        
			if(   noTokens < 3
                           || noTokens > 4)
                           return str;
                        
			String cnStr	=	(String)oldst.nextElement();
			String ouStr	=	(String)oldst.nextElement();
			
                        String contrStr =       null;
             
                        if(noTokens == 4)                       
                           contrStr     =        (String)oldst.nextElement();

                        String oStr	=	(String)oldst.nextElement();
                        
                        
			if(cnStr.indexOf("=") == -1){newst.append("cn=");newst.append(cnStr);}
			else						{newst.append(cnStr);}

			newst.append("/");

			if(ouStr.indexOf("=") == -1){newst.append("ou=");newst.append(ouStr);}
			else						{newst.append(ouStr);}

			newst.append("/");
                        
                        if(contrStr != null){
                           newst.append("ou=");
                           newst.append(contrStr);
                           newst.append("/");
                        }
                        

			if(oStr.indexOf( "=") == -1){newst.append("o=");newst.append("IBM@IBMUS");}
			else						{newst.append("IBM@IBMUS");}

			retstr =  new String(newst);
		}
                System.out.println(" Corrected String: [ " + retstr + " ]");
	return retstr;
	}

private boolean callUpdate(Connection conn, String sOppID, String bus_sol_mgr, String principal,
							 String bus_lead_mgr, String client_service_mgr, String ibm_team_members) throws SQLException, Exception{
		Statement stmt	= null;
		ResultSet rs	= null;
		StringBuffer sQuery = new StringBuffer();
		try{
                   sQuery.append("UPDATE ets.ETS_OPPORTUNITY SET BUS_SOLUTION_MGR='" + bus_sol_mgr + "',PRINCIPAL='" + principal + "', BUS_LEAD_MGR='" + bus_lead_mgr + "', CLIENT_SERVICE_MGR='" + client_service_mgr + "', IBM_TEAM_MEMBERS='" + ibm_team_members + "' WHERE OPPORTUNITY_ID='" + sOppID + "'");
                   


		//sQuery.append("insert into ets.ETS_OPPORTUNITY VALUES ('abcdefghi1234','Opportunity1','First Opportunity','codename1','clientname1', 'y', 02-02-2003, 'bus_solution_manager1','principal1', 'bus_lead_mgr1', 'client_service_mgr1', 'ssm1','subu@us.ibm.com,subs_me@yahoo.com,subs_me@hotmail.com', 'clientaddress1', 'clientphone1', 'hqlocation1' , 'clientbranch1',current timestamp)");
		stmt = conn.createStatement();
		stmt.executeUpdate(sQuery.toString());
		} catch (SQLException e) {
			System.out.println("sqlException caught " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("exception caught " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			sQuery = null;
		}
	return true;
	}

private static void callDummyUpdate(Connection conn) throws SQLException, Exception{
	Statement stmt	= null;
		ResultSet rs	= null;
		StringBuffer sQuery = new StringBuffer();
		StringBuffer sQuery1 = new StringBuffer();
		StringBuffer sQuery2 = new StringBuffer();
		StringBuffer sQuery3 = new StringBuffer();
		StringBuffer sQuery4 = new StringBuffer();
		try{
		sQuery.append("UPDATE ets.ETS_OPPORTUNITY SET EXPIRY_DATE='04-05-2004' WHERE OPPORTUNITY_ID='gdefghij2345'");
		stmt = conn.createStatement();
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

private  void DebugPrint(String classN, String methodN, String val){
		System.out.println(concatMsg(classN, methodN, val));
	}
private String concatMsg(String classN, String methodN, String val){
		String 	rtr 	=	"\n"
						+	classN	+	" : "
						+	methodN	+	" : \n"
						+	val;
		return rtr;
}

private static Timestamp Yesterdays_Time(){

	long ms 		= new Date().getTime();
	ms				= ms - (24*60*60*1000);

	Timestamp	ts =	new Timestamp(ms);

	return ts;
}

public static void main(String[] args) {
   BluePageRetrievalJob job = new BluePageRetrievalJob();
   if(!job.Init()){
      System.out.println("Couldnt initiate the property file");
      return;
   }
   try {
      job.authenticate();
      job.sendOutputToFile(outFilePath);
      Class.forName(driver_class).newInstance();
      Connection dbConnection = DriverManager.getConnection(connect_string, db2usr, db2pw);

     // BluePageRetrievalJob.callDummyInsert(dbConnection);
     //BluePageRetrievalJob.callDummyUpdate(dbConnection);

      job.JobImplementation(dbConnection,"","","","","","","");

   } catch (Exception e) {
      e.printStackTrace();
   }
   try{
      sendMail(mailHost, mailFm, problem_mailto, problem_mailto, "", "","BluePageRetrievalJOB Finished","Job Finished");
   }
   catch(MessagingException me){
      System.out.println(me.getMessage());
   }
}
   public static void sendMail(String mailhost, String from, String to, String cc, String bcc, String replyTo, String subject, String body)throws MessagingException {
      if (DebugVal) {
	  System.out.println("SENDER IS " + from);
	}



        try {

             java.util.Properties props = new java.util.Properties();

	    if (mailhost != null)
                props.put("mail.smtp.host", mailhost);
        else
                props.put("mail.smtp.host", "us.ibm.com");

        Session session = Session.getDefaultInstance(props, null);

	    MimeMessage msg = new MimeMessage(session);

	    if(from != null && from.trim().length() != 0){
		    if (DebugVal) {
      		  System.out.println("SENDER IN IF BLOCK " + from.trim());
		    }
		    msg.setFrom(new InternetAddress(from.trim()));
	    }
	    else{
		    if (DebugVal) {
		      System.out.println("SENDER IN ELSE " + from.trim());
		    }
            msg.setFrom();
	    }


	    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));


	    if(cc != null && cc.trim().length() != 0)
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));

	    if(bcc != null && bcc.trim().length() != 0)
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));

            if(replyTo != null && replyTo.trim().length() != 0)
                msg.setReplyTo(InternetAddress.parse(replyTo, false));


	    msg.setSubject(subject);
            msg.setText(body);

            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.send(msg);
            transport.close();

        }
        catch(MessagingException e) {
            try {
                Thread.sleep(5000);
            }
            catch(InterruptedException ie) { }

            throw e;
        }
	}

}
