 /*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
/*                  														*/
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
 * Created on Sep 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;


import oem.edge.common.DbConnect;
import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.util.DBHandler;
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.util.HistoryUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;
import oem.edge.ets.fe.workflow.util.SelectControl;
import oem.edge.ets.fe.workflow.util.UserUtils;


/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetDAO extends AbstractDAO {

	/**
	 * This method is used for generating the unique id based on the current timestamp
	 * @return currenttimestamp.
	 * @throws SQLException
	 * @throws Exception
	 */
	public String generateUniqueID() throws SQLException, Exception {
		String uniqueID = "";
		Long lDate = new Long(System.currentTimeMillis());
		uniqueID = lDate.toString();
		return uniqueID;
	}

	/** ----------------------------------------------------------------------------------------------------------
	 Code added for createing the new setmet
	 --------------------------------------------------------------------------------------------------------- */

	/**
	 *   This method returns the meetingID for a workflow
	 *    @param WorkflowObject workflowObject
	 */
	private void getMeetingID(SetMetIdentifyStageObject obj, DBAccess db)
			throws Exception {

		String meetID = ETSCalendar.getNewCalendarId();

		//db.prepareDirectQuery("INSERT INTO ETS.ETS_CALENDAR (CALENDAR_ID,PROJECT_ID,CALENDAR_TYPE,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID)VALUES (?,?,?,?,?,?,?,?,?,?)");
		db.prepareQuery("insert_etscalendar");
		String scdate = obj.getPlDate() + "/" + obj.getPlMon() + "/"
				+ obj.getPlYear();
        String subject = "SETMET-"  + obj.getPlMon() + "-" + obj.getPlDate() + "-" + obj.getPlYear();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		java.sql.Date schduledate = new java.sql.Date(
				((java.util.Date) formatter.parse(scdate)).getTime());
		String sttime = "09:00:000";
		//SimpleDateFormat formattertime = new SimpleDateFormat("h:mm a");
		//java.sql.Date schduleTime= new java.sql.Date(((java.util.Date) formattertime.parse(sttime)).getTime());

		db.setString(1, meetID); //meetingID == Calendarid generated from the timestamp
		db.setString(2, obj.getProjectID()); //project id get it from the object
		db.setString(3, "M"); // Set meeting time 'M' for Meeting
		java.sql.Timestamp currentts = new java.sql.Timestamp(System.currentTimeMillis());
		db.setObject(4, currentts); //Schduled date in timestamp
		db.setString(5, obj.getAcctContact()); //Userid the login person in Varchr2
		db.setObject(6, schduledate); //Starttime in timestamp
		db.setInt(7, 180); //Duration in minutes
		db.setString(8, subject); //Meeting Subject
		db.setString(9, "setmet meeting event"); //Meeting Description

		String[] ibm = obj.getIbmlist();
		String ibmidlist = "";
		if (ibm != null) {
			for (int x = 0; x < ibm.length; x++)
				ibmidlist = ibmidlist + ibm[x].trim() + ",";
		}
		if (ibm != null) {
			ibmidlist = ibmidlist.substring(0, (ibmidlist.length() - 1)) + ibmidlist.substring(ibmidlist.length());// Remove the last ','
		}
		db.setString(10, ibmidlist); //INVITEES_ID

		db.setString(11, "N"); //CANCEL_FLAG
		db.setString(12, "N"); //EMAIL_FLAG
		db.setString(13, "N"); //IBM_ONLY
		db.setString(14, "N"); //REPEAT_TYPE

		db.execute();
		obj.setMeetingID(meetID);
	}

	/**
	 * This method returns the WorkflowID for a workflow
	 * @param SetMetIdentifyStageObject obj
	 *
	 */
	private void getWorkflowID(SetMetIdentifyStageObject obj, DBAccess db)
			throws Exception {

		String WF_ID = ETSCalendar.getNewCalendarId();
		String WF_NAME = obj.getWorkflowType() + "-" + obj.getClientName()
				+ "-" + obj.getPlMon() + obj.getPlDate()
				+ obj.getPlYear().substring(2);

		java.sql.Timestamp ts = new java.sql.Timestamp(System.currentTimeMillis());

		db.prepareQuery("insert_wf_definition");

		db.setString(1, obj.getProjectID()); // Unique project id
		db.setString(2, WF_ID); // Unique Workflw id
		db.setString(3, obj.getWorkflowType()); // Workflow type currently it is "SETMET"
		db.setString(4, WF_NAME); // Workflow name
		db.setInt(5, 1); // Document id
		db.setString(6, obj.getRequestor()); //REQUESTOR varchar
		db.setString(7, obj.getAcctContact()); // ACCT_CONTACT varchar selected from the cratesetmet page
		db.setString(8, obj.getDelegate()); // BACKUP_ACCT_CONTACT  same asa above
		if (obj.getQuarter() == null || obj.getQuarter(). length() == 0) {
			db.setInt(9, 0);
		}else{
			db.setInt(9, Integer.parseInt(obj.getQuarter()));
		}
		db.setString(10, obj.getYear().trim()); // YEAR when meeting is going to be happen
		db.setString(11, obj.getMeetingID()); //MEETING_ID fromthe calender
		db.setString(12, (obj.getWorkflowStatus().equals("true")) ? obj
				.getNextStage() : obj.getStageName()); //WF_CURR_STAGE_NAME current stage name like .Identify,Prepare
		java.sql.Date cdate = new java.sql.Date(new java.util.Date().getTime());
		//		db.setString(13,(obj.getWorkflowStatus().equals("true"))? "Y" : "N"); 		// Workflow Status
		db.setDate(13, cdate); //CREATION_DATE  creation date it is java.sql.date
		db.setString(14, obj.getRequestor()); // IBM ID of the  profile creator/updater
		db.setObject(15, ts);
		int j = db.execute();
		obj.setWorkflowID(WF_ID);

	}

	/**
	 * This method is used for inserting the IBM attendees List
	 * @param obj
	 * @param db
	 * @throws Exception
	 */

	public void setIBM_Attendees(SetMetIdentifyStageObject obj, DBAccess db)
			throws Exception {
		ArrayList ibmlist = new ArrayList();
		ArrayList innerlist = null;
		String str = "insert_wf_setmet_attendees_ibm";
		String[] ibm = obj.getIbmlist();
		ibmlist = new ArrayList();
		if (ibm != null) {
			for (int x = 0; x < ibm.length; x++) {
				innerlist = new ArrayList();
				innerlist.add(obj.getProjectID().trim()); //project id
				innerlist.add(obj.getWorkflowID().trim()); // WorkFlow id
				innerlist.add(ibm[x].trim()); //Unique IBM id
				ibmlist.add(innerlist);
				innerlist = null;
			}
			boolean status = db.batchQuery(str, ibmlist);
		}
	}

	/**
	 * This method is used for inserting the Client attendees List
	 * @param obj
	 * @param db
	 * @throws Exception
	 */

	public void setClient_Attendees(SetMetIdentifyStageObject obj, DBAccess db)
			throws Exception {
		ArrayList clientlist = new ArrayList();
		ArrayList innerlist = null;
		String str = "insert_wf_setmet_attendees_client";
		String[] clientattendees = obj.getAttendees();
		clientlist = new ArrayList();
		if (clientattendees != null) {
			for (int x = 0; x < clientattendees.length; x++) {
				innerlist = new ArrayList();
				innerlist.add(obj.getProjectID().trim()); //project id
				innerlist.add(obj.getWorkflowID().trim()); //WorkFlow id
				innerlist.add(clientattendees[x].trim()); // unique Clientid
				clientlist.add(innerlist);
				innerlist = null;
			}
			boolean status = db.batchQuery(str, clientlist);
		}
	}

	/**
	 * This method is used to insert the data in to the Setmet identify table.
	 * @param workflowObject
	 * @return
	 */
	public boolean initializeWorkflowTables(WorkflowObject workflowObject) {
		boolean inserted = false;
		SetMetIdentifyStageObject obj = (SetMetIdentifyStageObject) workflowObject;
		DBAccess db = null;
		try {
			db = new DBAccess();
			System.out
					.println("Inside the initializartion object --------------------------------");

			getMeetingID(obj, db);
			getWorkflowID(obj, db);
			setIBM_Attendees(obj, db);
			setClient_Attendees(obj, db);

			String WF_STAGE_ID = ETSCalendar.getNewCalendarId();
			obj.setStageID(WF_STAGE_ID);

			if(obj.getWorkflowStatus().equalsIgnoreCase("true")){
				//update history for the stage
				updateSetMetHistoryforStage(obj, db,"Identify","Prepare");
			}
			java.sql.Timestamp ts = new java.sql.Timestamp(System
					.currentTimeMillis());

			String day = obj.getBiWeeklyDt();
			String month = obj.getBiWeeklyMon();
			String year = obj.getBiWeeklyYr();
			String date = (day + "/" + month + "/" + year).toString();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			java.sql.Date Biweekly_date = new java.sql.Date(
					((java.util.Date) formatter.parse(date)).getTime());


			db.prepareQuery("insert_wf_setmet");


			db.setString(1, obj.getProjectID()); //Project ID
			db.setString(2, obj.getWorkflowID()); //WorkFlow ID
			db.setString(3, WF_STAGE_ID); //WF_STAGE_ID
			String clientlevel = "A";
			if(obj.isClientScorecard()==true){
				clientlevel = "C";
			}
			db.setString(4, clientlevel); //ClientScorecard
			db.setString(5, obj.getSponsor()); //EXEC_SPONSOR

			db.setString(6, obj.getMeetingLocn());
			if (obj.isBiWeeklyflg()) {
				db.setString(7, "Y");
			} else {
				db.setString(7, "N");
			}
			db.setDate(8, Biweekly_date);
			db.setString(9, obj.getBiWeeklyStatus().toString());
			if (obj.getNsiRating() == null || obj.getNsiRating().trim().length() == 0)
				db.setInt(10, -111);
			else
				db.setInt(10, Integer.parseInt(obj.getNsiRating()));
			db.setString(11, (obj.getWorkflowStatus().equals("true")) ? "Y"	: "N");
			db.setString(12, obj.getAcctContact());//loggrd person
			db.setObject(13, ts);
			db.execute();
			db.doCommit();
			db.close();
			db = null;
			inserted = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			inserted = false;
			db.doRollback();
		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception e) {

				}
			}
		}
		return inserted;
	}

	/**This is the root method that will call the above methods during creation of the new Setmet
	 * @param workflowObject
	 * @return
	 */
	public boolean saveWorkflowObject(WorkflowObject workflowObject) {
		DBAccess db = null;
		SetMetIdentifyStageObject obj = (SetMetIdentifyStageObject) workflowObject;
		try {
			System.out.println(obj.getMeetingLocn());
			System.out.println(obj.getAcctContact());
			System.out.println(obj.getBiWeeklyDt());
			System.out.println(obj.getBiWeeklyMon());
			System.out.println(obj.getBiWeeklyYr());
			System.out.println(obj.getNsiRating());
			System.out.println(obj.getPlDate());
			System.out.println(obj.getQuarter());
			System.out.println(obj.isBiWeeklyflg());
			System.out.println(obj.getYear());
			System.out.println(obj.getBiWeeklyStatus());
			System.out.println(obj.isClientScorecard()+"---------------------");
			String[] ibm = obj.getIbmlist();
			if (ibm != null) {
				for (int x = 0; x < ibm.length; x++)
					System.out.println(ibm[x]);
			}
			initializeWorkflowTables(workflowObject);

		} catch (Exception ex) {
			db.doRollback();
		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception e) {

				}
			}
		}
		return false;
	}

	/** ----------------------------------------------------------------------------------------------------------
	 creating new Setmet code ends here
	 --------------------------------------------------------------------------------------------------------- */

	/** ----------------------------------------------------------------------------------------------------------
	 Code starts for the update the setmet
	 --------------------------------------------------------------------------------------------------------- */

	/**
	 * This method is used to update the ets calendar table
	 * @param SetMetIdentifyStageObject
	 * @return
	 */
	private void setETS_CALENDAR(SetMetIdentifyStageObject obj, DBAccess db)
			throws Exception {

		//db.prepareQuery("update_etscalendar");
		db
				.prepareDirectQuery("UPDATE ETS.ETS_CALENDAR SET CALENDAR_TYPE = ?,SCHEDULE_DATE = ?,SCHEDULED_BY = ?"
						+ ",START_TIME = ?,DURATION = ?,SUBJECT = ? ,DESCRIPTION = ?,INVITEES_ID = ?,CANCEL_FLAG = ?"
						+ ",EMAIL_FLAG = ?,IBM_ONLY = ?,REPEAT_TYPE = ? WHERE CALENDAR_ID = ?");

		String scdate = obj.getPlDate() + "/" + obj.getPlMon() + "/" + obj.getPlYear();
		String subject = "SETMET-"  + obj.getPlMon() + "-" + obj.getPlDate() + "-" + obj.getPlYear();

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		java.sql.Date schduledate = new java.sql.Date(
				((java.util.Date) formatter.parse(scdate)).getTime());

		String sttime = "09:00:000";
		//SimpleDateFormat formattertime = new SimpleDateFormat("h:mm a");
		//java.sql.Date schduleTime= new java.sql.Date(((java.util.Date) formattertime.parse(sttime)).getTime());

		db.setString(1, "M"); // Set meeting time 'M' for Meeting
		java.sql.Timestamp currentts = new java.sql.Timestamp(System.currentTimeMillis());
		db.setObject(2, currentts); //Schduled date in timestamp
		db.setString(3, obj.getAcctContact()); //Userid the login person in Varchr2
		db.setObject(4, schduledate); //Starttime in timestamp
		db.setInt(5, 160); //Duration in minutes
		db.setString(6, subject ); //Meeting Subject
		db.setString(7, "setmet meeting event"); //Meeting Description

		String[] ibm = obj.getIbmlist();
		String ibmidlist = "";
		if (ibm != null) {
			for (int x = 0; x < ibm.length; x++)
				ibmidlist = ibmidlist + ibm[x].trim() + ",";
		}
		if (ibm != null && ibm.length > 0) {
			ibmidlist = ibmidlist.substring(0, (ibmidlist.length() - 1))+ ibmidlist.substring(ibmidlist.length());// Remove the last ','
		}
		db.setString(8, ibmidlist); //INVITEES_ID
		db.setString(9, "N"); //CANCEL_FLAG
		db.setString(10, "N"); //EMAIL_FLAG
		db.setString(11, "N"); //IBM_ONLY
		db.setString(12, "N"); //REPEAT_TYPE
		db.setString(13, obj.getMeetingID()); //Calendarid = MEETINGID
		db.execute();

	}

	/**
	 * This method is used to update the wf_def table.
	 * @param obj
	 * @param db
	 * @throws Exception
	 */

		private void setWF_DEF(SetMetIdentifyStageObject obj, DBAccess db)
			throws Exception {
		String WF_NAME = obj.getWorkflowType() + "-" + obj.getClientName()
				+ "-" + obj.getPlMon() + obj.getPlDate()
				+ obj.getPlYear().substring(2);

		String stagename = getWorkflowCurrentStage(obj.getProjectID(),obj.getWorkflowID(),db);

		String wfstatename = (obj.getWorkflowStatus().equals("true")) ? obj.getNextStage() : obj.getStageName();
		java.sql.Timestamp ts = new java.sql.Timestamp(System
				.currentTimeMillis());

		//db.prepareQuery("insert_wf_definition");
		db
				.prepareDirectQuery("UPDATE ETS.WF_DEF SET WF_TYPE = ? ,WS_DOC_ID = ?,REQUESTOR = ?,"
						+ "ACCT_CONTACT = ?,BACKUP_ACCT_CONTACT = ?,QUARTER = ?,YEAR = ? ,MEETING_ID = ?,"
						+ "WF_CURR_STAGE_NAME = ?,CREATION_DATE = ?,LAST_USERID = ?,LAST_TIMESTAMP = ?"
						+ " WHERE WF_ID = ?");
		db.setString(1, obj.getWorkflowType()); // Workflow type currently it is "SETMET"
		//db.setString(2, WF_NAME); // Workflow name
		db.setInt(2, 1); // Document id
		db.setString(3, obj.getRequestor()); //REQUESTOR varchar
		db.setString(4, obj.getAcctContact()); // ACCT_CONTACT varchar selected from the cratesetmet page
		db.setString(5, obj.getDelegate()); // BACKUP_ACCT_CONTACT  same asa above
		int getQuarter = 0;
		String qtr = obj.getQuarter();
		if (qtr != null || qtr != "") {
			getQuarter = Integer.parseInt(qtr);
		}
		db.setInt(6, getQuarter);
		db.setString(7, obj.getYear().trim()); // YEAR when meeting is going to be happen
		db.setString(8, obj.getMeetingID()); //MEETING_ID fromthe calender

		db.setString(9, stagename.equalsIgnoreCase("IDENTIFY") ? wfstatename : stagename);//Stage Name
		java.sql.Date cdate = new java.sql.Date(new java.util.Date().getTime());
		db.setDate(10, cdate); //CREATION_DATE  creation date it is java.sql.date
		db.setString(11, obj.getAcctContact());// IBM ID of the  profile creator/updater
		db.setObject(12, ts);
		db.setString(13, obj.getWorkflowID()); // Unique Workflw id
		db.execute();
		}


	/**
	 * This method is used to delete the recods from the WF_SETMET_ATTENDEES_IBM table.
	 * @param obj
	 * @param db
	 * @throws Exception
	 */
	public void deleteIBM_Attendees(SetMetIdentifyStageObject obj, DBAccess db)
			throws Exception {
		db.prepareDirectQuery("DELETE FROM ETS.WF_SETMET_ATTENDEES_IBM WHERE WF_ID = ? ");
		db.setString(1, obj.getWorkflowID());
		db.execute();
	}

	/**
	 * This method is used to update th WF_SETMET_ATTENDEES_IBM table
	 * @param obj
	 * @param db
	 * @throws Exception
	 */
	//Basicall remove all the records based on the wf_id and then add the new IBM attendees on the basis of wf_id.
	public void updateIBM_Attendees(SetMetIdentifyStageObject obj, DBAccess db)
			throws Exception {
		deleteIBM_Attendees(obj, db);
		ArrayList ibmlist = new ArrayList();
		ArrayList innerlist = null;
		String str = "insert_wf_setmet_attendees_ibm";
		String[] ibm = obj.getIbmlist();
		ibmlist = new ArrayList();
		if (ibm != null) {
			for (int x = 0; x < ibm.length; x++) {
				innerlist = new ArrayList();
				innerlist.add(obj.getProjectID().trim()); //project id
				innerlist.add(obj.getWorkflowID().trim()); // WorkFlow id
				innerlist.add(ibm[x].trim()); //Unique IBM id
				ibmlist.add(innerlist);
				innerlist = null;
			}
			boolean status = db.batchQuery(str, ibmlist);
		}
	}

	/**
	 * This mehod is used to delete the WF_SETMET_ATTENDEES_CLIENT table.
	 * @param obj
	 * @param db
	 * @throws Exception
	 */

	public void deleteclient_Attendees(SetMetIdentifyStageObject obj,
			DBAccess db) throws Exception {
		db.prepareDirectQuery("DELETE FROM ETS.WF_SETMET_ATTENDEES_CLIENT  WHERE WF_ID = ? ");
		db.setString(1, obj.getWorkflowID());
		int x = db.execute();
	}

	/**
	 * This method is used to update the client attendees table.
	 * @param obj
	 * @param db
	 * @throws Exception
	 */
	//Basicall remove all the records based on the wf_id and then add the new Client attendees on the basis of wf_id.
	public void updateClient_Attendees(SetMetIdentifyStageObject obj,
			DBAccess db) throws Exception {
		deleteclient_Attendees(obj, db);
		ArrayList clientlist = new ArrayList();
		ArrayList innerlist = null;
		String str = "insert_wf_setmet_attendees_client";
		String[] clientattendees = obj.getAttendees();
		clientlist = new ArrayList();
		if (clientattendees != null) {
			for (int x = 0; x < clientattendees.length; x++) {
				innerlist = new ArrayList();
				innerlist.add(obj.getProjectID().trim()); //project id
				innerlist.add(obj.getWorkflowID().trim()); //WorkFlow id
				innerlist.add(clientattendees[x].trim()); // unique Clientid
				clientlist.add(innerlist);
				innerlist = null;
			}
			boolean status = db.batchQuery(str, clientlist);
		}
	}

	public boolean cancelWorkflow(String project_id, String wf_id, String loggedUser) throws Exception {
		boolean flag = true;
		DbConnect db = null;
		DBAccess dbAccess = null;
		try {
		dbAccess = 	new DBAccess();
		db = new DbConnect("etsds");
		db.makeConn();

		//get workflow stage
		Vector vec = DBHandler.getVQueryResult(db.conn, "select WF_CURR_STAGE_NAME, MEETING_ID from ets.wf_def where project_id = '" + project_id + "' and wf_id = '" + wf_id + "' with ur", 2);
		String curr_stage = "";
		String meeting_id = "";
		if(vec.size() > 0)
		{
			String[] s = (String[]) vec.elementAt(0);
			curr_stage = s[0];
			meeting_id = s[0];
		}

		//update workfow stage
		DBHandler.fireUpdate(db.conn, "update ets.wf_def set WF_CURR_STAGE_NAME = 'Cancelled' where project_id = '" + project_id + "' and wf_id = '" + wf_id + "' ");

		//cancel the meeting associated with this workflow
		DBHandler.fireUpdate(db.conn, "update ets.ets_calendar set CANCEL_FLAG = 'Y' where project_id = '" + project_id + "' and CALENDAR_ID = '" + meeting_id + "' ");

		//update history tables
		String historyID = HistoryUtils.enterHistory(project_id,
				wf_id, wf_id,
				HistoryUtils.ACTION_GENERIC_SETMET, loggedUser,
				"Workflow moved into Cancelled Stage", dbAccess);
		HistoryUtils.addHistoryField(historyID, project_id,
				"Workflow Stage", curr_stage, "Cancelled", dbAccess);
		dbAccess.doCommit();

		} finally {
			db.closeConn();
			dbAccess.close();
		}
		return flag;
	}

	public boolean reinstateWorkflow(String project_id, String wf_id, String loggedUser) throws Exception {
		boolean flag = true;
		DbConnect db = null;
		DBAccess dbAccess = null;
		try {
		dbAccess = new DBAccess();
		db = new DbConnect();
		db.makeConn();

		//check workflow stage before it was cancelled.
		String cnt_str = DBHandler.getValue(db.conn, "select count(*) from ets.WF_STAGE_PREPARE_SETMET where project_id = '" + project_id + "' and wf_id = '" + wf_id + "' with ur");
		int cnt = 0;
		try {
			cnt = Integer.parseInt(cnt_str);
		}
		catch (NumberFormatException pe){
			//do nothing
		}
		String stage_name = "";
		if(cnt > 0)
			stage_name = "Prepare";
		else
			stage_name = "Identify";
		//reinstate workflow to old stage
		DBHandler.fireUpdate(db.conn, "update ets.wf_def set WF_CURR_STAGE_NAME = '"+ stage_name +"' where project_id = '" + project_id + "' and wf_id = '" + wf_id + "' ");

		//reinstate the meeting associated with this workflow
		String meeting_id = DBHandler.getValue(db.conn, "select MEETING_ID from ets.wf_def where project_id = '" + project_id + "' and wf_id = '" + wf_id + "' with ur");
		DBHandler.fireUpdate(db.conn, "update ets.ets_calendar set CANCEL_FLAG = 'N' where project_id = '" + project_id + "' and CALENDAR_ID = '" + meeting_id + "' ");

		//update history tables
		String historyID = HistoryUtils.enterHistory(project_id,
				wf_id, wf_id,
				HistoryUtils.ACTION_GENERIC_SETMET, loggedUser,
				"Workflow reinstated into "+stage_name+" Stage", dbAccess);
		HistoryUtils.addHistoryField(historyID, project_id,
				"Workflow Stage", "Cancelled", stage_name, dbAccess);
		dbAccess.doCommit();

		} finally {
			db.closeConn();
			dbAccess.close();
		}
		return flag;
	}

	/**
	 * This mehod is used to Update the setmet identify table.
	 * @param workflowObject
	 * @return
	 */

	public boolean updateWorkflowTables(WorkflowObject workflowObject) {
		boolean inserted = false;

		SetMetIdentifyStageObject obj = (SetMetIdentifyStageObject) workflowObject;

		DBAccess db = null;
		try {
			db = new DBAccess();
			String stagename = getWorkflowCurrentStage(obj.getProjectID(),obj.getWorkflowID(),db);
			String clinet_level = getClientlevel(obj.getWorkflowID(),db);
			if(stagename.equalsIgnoreCase("identify") && obj.getWorkflowStatus().equalsIgnoreCase("true")){
				//update history for the stage
				updateSetMetHistoryforStage(obj, db,"Identify","Prepare");
			}
			if(!stagename.equalsIgnoreCase("identify")){
				SetMetIdentifyStageObject objold = (SetMetIdentifyStageObject)getWorkflowObjectOld(obj.getWorkflowID(),db);
				updateSetMetHistoryTable(objold,obj, db);//First Updat the History tables then into the setmettable

			}

			setETS_CALENDAR(obj, db);
			setWF_DEF(obj, db);
			updateIBM_Attendees(obj, db);
			updateClient_Attendees(obj, db);

			String WF_STAGE_ID = ETSCalendar.getNewCalendarId();
			java.sql.Timestamp ts = new java.sql.Timestamp(System
					.currentTimeMillis());

			String day = obj.getBiWeeklyDt();
			String month = obj.getBiWeeklyMon();
			String year = obj.getBiWeeklyYr();

			String date = (day + "/" + month + "/" + year).toString();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			java.sql.Date Biweekly_date = new java.sql.Date(
					((java.util.Date) formatter.parse(date)).getTime());

			obj.setStageID(WF_STAGE_ID);
			db
					.prepareDirectQuery("UPDATE ETS.WF_STAGE_IDENTIFY_SETMET SET SCORING_LEVEL= ?,EXEC_SPONSOR = ?,LOCATION = ? ,"
							+ "BIWEEKLY_FLAG = ? ,BIWEEKLY_DATE = ? ,BIWEEKLY_STATUS = ? ,NSI_RATING = ? ,"
							+ "STATUS = ? ,LAST_USERID = ? ,LAST_TIMESTAMP = ? WHERE WF_ID = ?");
			//db.prepareQuery("update_wf_setmet");

			String clientlevel = "A";
			if(obj.isClientScorecard() == true){
				clientlevel = "C";
			}
			db.setString(1, clientlevel); //SCORING_LEVEL
			db.setString(2, obj.getSponsor()); //EXEC_SPONSOR
			db.setString(3, obj.getMeetingLocn());
			if (obj.isBiWeeklyflg()) {
				db.setString(4, "Y");
			} else {
				db.setString(4, "N");
			}
			db.setDate(5, Biweekly_date);
			db.setString(6,obj.getBiWeeklyStatus().toString());
			if (obj.getNsiRating() != null || obj.getNsiRating() != "")
				db.setInt(7, Integer.parseInt(obj.getNsiRating()));
			else
				db.setInt(7, -1);

			db.setString(8, (obj.getWorkflowStatus().equals("true")) ? "Y"
					: "N");
			db.setString(9, obj.getLastUsr());
			db.setObject(10, ts);
			db.setString(11, obj.getWorkflowID()); // Unique Workflw id
			db.execute();
			db.doCommit();
			db.close();
			db = null;
			inserted = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			inserted = false;
			db.doRollback();
		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception e) {

				}
			}
		}
		return inserted;
	}

	/**
	 * This is the base class whic is responsible to call the able all method during update the setmet.
	 * @param workflowObject
	 * @return
	 */
	public boolean updateWorkflowObject(WorkflowObject workflowObject) {
		DBAccess db = null;
		SetMetIdentifyStageObject obj = (SetMetIdentifyStageObject) workflowObject;
		try {
			System.out.println(obj.getProjectID());
			System.out.println(obj.getWorkflowID());
			System.out.println(obj.getStageID());
			System.out.println(obj.getMeetingID());
			System.out.println(obj.getMeetingLocn());
			System.out.println(obj.getAcctContact());
			System.out.println(obj.getBiWeeklyDt());
			System.out.println(obj.getBiWeeklyMon());
			System.out.println(obj.getBiWeeklyYr());
			System.out.println(obj.getNsiRating());
			System.out.println(obj.getPlDate());
			System.out.println(obj.getQuarter());
			System.out.println(obj.isBiWeeklyflg());
			System.out.println(obj.getYear());
			System.out.println(obj.getBiWeeklyStatus());
			String[] ibm = obj.getIbmlist();
			if (ibm != null) {
				for (int x = 0; x < ibm.length; x++)
					System.out.println(ibm[x]);
			}
			updateWorkflowTables(workflowObject);

		} catch (Exception ex) {
			db.doRollback();

		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception e) {

				}
			}
		}
		return false;
	}
	/**
	 * This method is used for get the client level value for the scoring matrix
	 * @param wfid
	 * @param db
	 * @return
	 */
	public String getClientlevel(String wfid,DBAccess db){
		String clientlevel = "";
		try {
			db.prepareDirectQuery("SELECT SCORING_LEVEL FROM ETS.WF_STAGE_IDENTIFY_SETMET WHERE WF_ID='" + wfid + "'");
			int setmetrow = db.execute();
			for (int i = 0; i < setmetrow; i++) {
				clientlevel = (db.getString(i, "SCORING_LEVEL")); //Get SCORING_LEVEL
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return clientlevel;
	}
	/** ----------------------------------------------------------------------------------------------------------
	 Code ended for update the existing setmet
	 --------------------------------------------------------------------------------------------------------- */

	/** ----------------------------------------------------------------------------------------------------------
	 Code Started for veiw identify stage of a setmet
	 --------------------------------------------------------------------------------------------------------- */

	/**
	 * Returns the list of the workflow object to identify in which stage is our workflow is
	 * @param wflowid
	 * @return SetMetIdentifyStageObject
	 */

	public WorkflowObject getWorkflowObject(String ID) {
		System.out
				.println("Inside DAO******************************************");
		String wfid = ID;
		SetMetIdentifyStageObject sObj = new SetMetIdentifyStageObject();
		;
		DBAccess db = null;
		try {
			db = new DBAccess();
			db
					.prepareDirectQuery("SELECT A.PROJECT_ID,A.WF_ID,B.WF_STAGE_ID,B.SCORING_LEVEL,"
							+ "A.REQUESTOR,A.ACCT_CONTACT,A.BACKUP_ACCT_CONTACT,B.EXEC_SPONSOR,"
							+ "B.LOCATION,B.BIWEEKLY_FLAG,B.BIWEEKLY_DATE,B.BIWEEKLY_STATUS,"
							+ "B.NSI_RATING,A.QUARTER,A.YEAR,A.MEETING_ID,A.WF_CURR_STAGE_NAME "
							+ "FROM ETS.WF_DEF A , ETS.WF_STAGE_IDENTIFY_SETMET B WHERE A.WF_ID = '"
							+ wfid + "' AND B.WF_ID = '" + wfid + "' with ur");
			int setmetrow = db.execute();
			for (int i = 0; i < setmetrow; i++) {
				sObj.setProjectID(db.getString(i, "PROJECT_ID")); //Get project id
				sObj.setWorkflowID(db.getString(i, "WF_ID")); //workflow id
				sObj.setStageID(db.getString(i, "WF_STAGE_ID")); //workflow stage id
				sObj.setRequestor(checkNull(db.getString(i, "REQUESTOR"))); //requestor
				sObj.setAcctContact(checkNull(db.getString(i, "ACCT_CONTACT"))); //Account contact
				sObj.setDelegate(checkNull(db.getString(i,
						"BACKUP_ACCT_CONTACT")));//Backup account contact
				boolean scoringlevel = db.getString(i, "SCORING_LEVEL").equalsIgnoreCase("C") ? true : false;
				sObj.setClientScorecard(scoringlevel); //SCORING_LEVEL
				sObj.setSponsor(checkNull(db.getString(i, "EXEC_SPONSOR"))); //Executive sponcer
				sObj.setMeetingLocn(checkNull(db.getString(i, "LOCATION"))); //meeting location
				sObj.setBiWeeklyflg(db.getString(i, "BIWEEKLY_FLAG").equalsIgnoreCase("Y") ? true : false);

				String  biweeklydate =  db.getString(i,"BIWEEKLY_DATE");
				if(biweeklydate!=null)
				{
				StringTokenizer bst = new StringTokenizer(biweeklydate, "-");
				String byear = bst.nextElement().toString();
				String bmonth = bst.nextElement().toString();
				String bdd = bst.nextElement().toString();
				sObj.setBiWeeklyDt(bdd);
				sObj.setBiWeeklyMon(bmonth);
				sObj.setBiWeeklyYr(byear);
				}
				/*sObj.setBiWeeklyStatus(db.getString(i, "BIWEEKLY_STATUS")
						.equalsIgnoreCase("true") ? true : false);*/
				if(db.getString(i, "BIWEEKLY_STATUS")!=null)
				{
				sObj.setBiWeeklyStatus(db.getString(i, "BIWEEKLY_STATUS").toString().trim());
				}
				sObj.setNsiRating(db.getString(i, "NSI_RATING")); //Nsi Rating

				int getQuarter = Integer.parseInt(db.getString(i, "QUARTER"));
				String QTR = null;
				switch (getQuarter) {
				case 0:
					QTR = "00";
					break;
				case 1:
					QTR = "01";
					break;
				case 2:
					QTR = "02";
					break;
				case 3:
					QTR = "03";
					break;
				case 4:
					QTR = "04";
					break;
				}
				sObj.setQuarter(QTR); //Quarter
				sObj.setYear(checkNull(db.getString(i, "YEAR"))); //YEAR
				sObj.setMeetingID(checkNull(db.getString(i, "MEETING_ID"))); //meeting id

				sObj.setIbmlist(ibmattendees_List(wfid));
				sObj.setAttendees(clientAttendees_list(wfid));

				String plannedddate = schdule_date(wfid);
				StringTokenizer st = new StringTokenizer(plannedddate, "-");
				String year = st.nextElement().toString();
				String month = st.nextElement().toString();
				String dd = st.nextElement().toString().substring(0, 2);

				sObj.setPlDate(dd);
				sObj.setPlMon(month);
				sObj.setPlYear(year);
			}
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return sObj;
	}

	/**
	 * This method is used to select the ibm attendees list from the database.
	 * @param wfid
	 * @return
	 */
	public String[] ibmattendees_List(String wfid) {
		DBAccess db = null;
		String ibmlist[] = null;
		try {
			db = new DBAccess();
			db.prepareDirectQuery("SELECT * FROM ETS.WF_SETMET_ATTENDEES_IBM WHERE WF_ID='"	+ wfid + "'");
			int setmetrow = db.execute();
			ibmlist = new String[setmetrow];
			for (int i = 0; i < setmetrow; i++) {
				ibmlist[i] = (db.getString(i, "USERID")); //Get project id
			}
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return ibmlist;
	}
	/**
	 * This method is used to select the IBM attendees from the database by passing the db access.
	 * @param wfid
	 * @param db
	 * @return
	 */
	public String[] ibmattendees_List(String wfid,DBAccess db) {
		String ibmlist[] = null;
		try {
			db.prepareDirectQuery("SELECT * FROM ETS.WF_SETMET_ATTENDEES_IBM WHERE WF_ID='"	+ wfid + "'");
			int setmetrow = db.execute();
			ibmlist = new String[setmetrow];
			for (int i = 0; i < setmetrow; i++) {
				ibmlist[i] = (db.getString(i, "USERID")); //Get project id
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return ibmlist;
	}

	/**
	 * This method is used to select the Clent attendees list from the database.
	 * @param wfid
	 * @return
	 */
	public String[] clientAttendees_list(String wfid) {
		DBAccess db = null;
		String clientattlist[] = null;
		try {
			db = new DBAccess();
			db
					.prepareDirectQuery("SELECT * FROM ETS.WF_SETMET_ATTENDEES_CLIENT WHERE WF_ID='"
							+ wfid + "'");
			int setmetrow = db.execute();
			clientattlist = new String[setmetrow];
			for (int i = 0; i < setmetrow; i++) {
				clientattlist[i] = (db.getString(i, "USERID")); //Get project id
			}
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return clientattlist;
	}
	/**
	 * This method is used to select the client attendees from the database by passing the db access.
	 * @param wfid
	 * @param db
	 * @return
	 */
	public String[] clientAttendees_list(String wfid,DBAccess db) {
		String clientattlist[] = null;
		try {
			db
					.prepareDirectQuery("SELECT * FROM ETS.WF_SETMET_ATTENDEES_CLIENT WHERE WF_ID='"
							+ wfid + "'");
			int setmetrow = db.execute();
			clientattlist = new String[setmetrow];
			for (int i = 0; i < setmetrow; i++) {
				clientattlist[i] = (db.getString(i, "USERID")); //Get project id
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return clientattlist;
	}

	/**
	 * This mehod is used to get the schedule date from the calendat table.
	 * @param wfid
	 * @return
	 */
	public String schdule_date(String wfid) {
		DBAccess db = null;
		String scdate = null;
		try {
			db = new DBAccess();
			db
					.prepareDirectQuery("SELECT A.START_TIME FROM ETS.ETS_CALENDAR A WHERE A.CALENDAR_ID = (SELECT MEETING_ID FROM ETS.WF_DEF WHERE WF_ID='"
							+ wfid + "') with ur");
			int setmetrow = db.execute();
			for (int i = 0; i < setmetrow; i++) {
				java.util.Date dt = (Date) db.getObject(i, "START_TIME"); //Get schdule date
				scdate = dt.toString();
			}
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return scdate;
	}
	/**
	 * This mehod is used to get the schedule date from the calendat table by passin the DBAccess object.
	 * @param wfid
	 * @param db
	 * @return
	 */
	public String schdule_date(String wfid,DBAccess db) {
		String scdate = null;
		try {
			db
					.prepareDirectQuery("SELECT A.START_TIME FROM ETS.ETS_CALENDAR A WHERE A.CALENDAR_ID = (SELECT MEETING_ID FROM ETS.WF_DEF WHERE WF_ID='"
							+ wfid + "') with ur");
			int setmetrow = db.execute();
			for (int i = 0; i < setmetrow; i++) {
				java.util.Date dt = (Date) db.getObject(i, "START_TIME"); //Get schdule date
				scdate = dt.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return scdate;
	}

	/**
	 * This method is used to get the added clent attendees during creation of the perticular Setmet
	 * @param clientattendees
	 * @param wfid
	 * @return
	 */
	public ArrayList getClientList(String[] clientattendees, String wfid,String company) {
		ArrayList clientatt = new ArrayList();
		DBAccess db = null;
		SelectControl selCon = null;
		try {
			db = new DBAccess();
			db
					.prepareDirectQuery("SELECT CLIENT_ID,FNAME ,LNAME,TITLE FROM ETS.WF_CLIENT WHERE CLIENT_ID IN "
							+ "(SELECT USERID FROM ETS.WF_SETMET_ATTENDEES_CLIENT WHERE WF_ID='"
							+ wfid + "') AND COMPANY = '"+ company +"'" );
			int setmetrow = db.execute();
			for (int i = 0; i < setmetrow; i++) {

				selCon = new SelectControl();
				String CLIENT_ID = db.getString(i, "CLIENT_ID");
				String FNAME = db.getString(i, "FNAME");
				String LNAME = db.getString(i, "LNAME");
				String TITLE = checkNull(db.getString(i, "TITLE"));

				String Clientname = FNAME.concat(" " + LNAME + " ").concat("-")
						.concat(" " + (TITLE));

				selCon.setLable(Clientname);
				selCon.setValue(CLIENT_ID);

				clientatt.add(selCon);

			}
			db.close();
			db = null;

		} catch (Exception e) {
			e.getMessage();
		} finally {

		}
		return clientatt;
	}

	/**
	 * This method is used to get the  clent attendees which is not added during creation of the perticular Setmet
	 * @param clientattendees
	 * @param wfid
	 * @return
	 */
	public ArrayList getAttendeeList(String[] clientattendees, String wfid,String company) {
		ArrayList clientatt = new ArrayList();
		DBAccess db = null;
		SelectControl selCon = null;
		try {
			db = new DBAccess();
			db
					.prepareDirectQuery("SELECT CLIENT_ID,FNAME ,LNAME,TITLE FROM ETS.WF_CLIENT WHERE CLIENT_ID NOT IN "
							+ "(SELECT USERID FROM ETS.WF_SETMET_ATTENDEES_CLIENT WHERE WF_ID='"
							+ wfid + "') AND COMPANY='"+ company +"'");
			int setmetrow = db.execute();
			for (int i = 0; i < setmetrow; i++) {

				selCon = new SelectControl();
				String CLIENT_ID = db.getString(i, "CLIENT_ID");
				String FNAME = db.getString(i, "FNAME");
				String LNAME = db.getString(i, "LNAME");
				String TITLE = checkNull(db.getString(i, "TITLE"));

				String Clientname = FNAME.concat(" " + LNAME + " ").concat("-")
						.concat(" " + (TITLE));

				selCon.setLable(Clientname);
				selCon.setValue(CLIENT_ID);

				clientatt.add(selCon);

			}
			db.close();
			db = null;

		} catch (Exception e) {
			e.getMessage();
		} finally {

		}
		return clientatt;
	}

	/** ----------------------------------------------------------------------------------------------------------
	 Code Ended for veiw identify stage of a setmet
	 -------------------------------------------------------------------------------------------------------------- */

	public boolean saveWorkflowObjectList(ArrayList object) {

		return false;
	}

	public ArrayList getWorkflowObjectList(String ID) {

		return null;
	}

	/**
	 * This method returns the list of setmets available for the client
	 * Method signature modified for 7.1.1-added argument: String workflowType; KP
	 * @param projectID
	 * @return ArrayList
	 */

	public ArrayList getWorkflowList(String projectID, String workflowType) {

		ArrayList list = new ArrayList();
		DBAccess db = null;
		try {
			db = new DBAccess();
			db.prepareDirectQuery("SELECT a.PROJECT_ID,a.WF_ID,c.WF_STAGE_ID,a.WF_NAME,a.YEAR,a.QUARTER," +
								  "b.START_TIME,a.WF_CURR_STAGE_NAME, a.ACCT_CONTACT from ETS.WF_DEF a,ETS.ETS_CALENDAR b," +
								  "ETS.WF_STAGE_IDENTIFY_SETMET c where (b.CALENDAR_ID = a.MEETING_ID and " +
								  "a.WF_ID = c.WF_ID and a.PROJECT_ID='"+projectID+"') and a.WF_TYPE ='"+workflowType+"' order by a.WF_NAME with ur ");
			int setmetrow = db.execute();
			for (int i = 0; i < setmetrow; i++) {
				Workflow wfobj = new Workflow();
				wfobj.setProjectID(checkNull(db.getString(i, "PROJECT_ID")));
				wfobj.setWorkflowID(checkNull(db.getString(i, "WF_ID")));
				//wfobj.setProjectID(checkNull(db.getString(i,"WF_STAGE_ID")));
				wfobj.setWorkflowName(checkNull(db.getString(i, "WF_NAME")));
				String year = null;
				year = "Q".concat(db.getString(i, "QUARTER")).concat(db.getString(i, "YEAR").substring(2));
				if("SELF ASSESSMENT".equals(workflowType))
					year = db.getString(i, "QUARTER").concat("/").concat(db.getString(i, "YEAR").substring(2));
				wfobj.setQuarter(year);
				java.util.Date dt = (java.util.Date) (db.getObject(i,"START_TIME"));
				wfobj.setMeetingDate(getSchduledate(dt.toString()));
				String setmetstatus = checkNull(db.getString(i, "WF_CURR_STAGE_NAME"));
				wfobj.setStatus(setmetstatus);
				//wfobj.setWorkflowOwner(db.getString(i,"ACCT_CONTACT"));
				wfobj.setWorkflowOwner(getNameOnWebid(db.getString(i,"ACCT_CONTACT")));

				list.add(wfobj);
			}
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return list;
	}

	/**
	 * This method is used to client attendees based on the Company
	 * @param company
	 * @return
	 */
	/*public ArrayList getClient_Attendees(SetMetIdentifyStageObject obj,DBAccess db){*/

	public ArrayList getClient_Attendees(String company) {
		ArrayList clientlist = new ArrayList();
		DBAccess db = null;
		SelectControl selCon = new SelectControl();
		try {
			db = new DBAccess();

			//db.prepareDirectQuery("SELECT CLIENT_ID,FNAME,LNAME,TITLE FROM ETS.WF_CLIENT WHERE COMPANY='A K STAMP'");
			db.prepareQuery("client_attendees");
			db.setString(1, company);
			int rows = db.execute();

			for (int rCount = 0; rCount < rows; rCount++) {
				selCon = new SelectControl();
				String CLIENT_ID = db.getString(rCount, "CLIENT_ID");
				String FNAME = db.getString(rCount, "FNAME");
				String LNAME = db.getString(rCount, "LNAME");
				String TITLE = db.getString(rCount, "TITLE");
				String Clientname = FNAME.concat(" " + LNAME + " ").concat("-")
						.concat(" " + TITLE);
				selCon.setLable(Clientname);
				selCon.setValue(CLIENT_ID);

				clientlist.add(selCon);
			}

			db.close();
			db = null;
		} catch (Exception e) {

		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return clientlist;
	}
	/**
	 * This method is used to IBM attendees based on the Company
	 * @param projectID
	 * @return
	 */
	public ArrayList getIBM_Attendees(String projectID) {


		//New implementation by KP
		ArrayList iBMAttendees = new ArrayList();
		iBMAttendees = UserUtils.getUserByRoleName("WS_OWNER", projectID);
		iBMAttendees.addAll(UserUtils.getUserByRoleName("WS_MGR",projectID ));
		iBMAttendees.addAll(UserUtils.getUserByRoleName("MEM",projectID ));
		MiscUtils.removeDuplicates(iBMAttendees);
		DBAccess db = null;
		try {
			db = new DBAccess();

			for (int i = 0; i < iBMAttendees.size(); i++) {
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId((String) iBMAttendees.get(i));
				u.extractUserDetails(db.getConnection());

				iBMAttendees.set(i, new SelectControl(u.getFirstName()
						+ " " + u.getLastName(), (String) iBMAttendees
						.get(i)));
			}
			db.close();
			db = null;
		} catch (Exception e) {

		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return iBMAttendees;

		//Old implementation follows, commented.
		/*
		ArrayList ibmattendeeslist = new ArrayList();
		DBAccess db = null;
		SelectControl selCon = new SelectControl();
		EdgeAccessCntrl ctrl = new EdgeAccessCntrl();
		try {
			db = new DBAccess();

			String q="select distinct user_id from ets.ets_users where user_project_id='"+projectID+"' and active_flag='A'";
			System.out.println("DB:"+q);
			db.prepareDirectQuery(q);

			//
			//db.prepareDirectQuery("SELECT DISTINCT (ETS_USERS.USER_ID) FROM ETS.ETS_USERS AS ETS_USERS WHERE ETS_USERS.USER_PROJECT_ID = '"+projectID+"' AND ETS_USERS.ACTIVE_FLAG  = 'A' ");
			//

			int rows = db.execute();

			for (int i = 0; i < rows; i++) {

				ETSUserDetails u = new ETSUserDetails();
				selCon = new SelectControl();
				String webid = db.getString(i, "USER_ID");
				u.setWebId(webid);
				u.extractUserDetails(db.getConnection());
				//u.getUserType();
				selCon.setLable(u.getFirstName() + " " + u.getLastName());
				selCon.setValue(webid);
				ibmattendeeslist.add(selCon);
			}

			db.close();
			db = null;
		} catch (Exception e) {

		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return ibmattendeeslist;*/

		}
	/**
	 * This method is used to get the account contact based on the project
	 * @param project_id
	 * @return
	 */
	public ArrayList getAccount_Contact(String project_id) {
		ArrayList accountcontactlist = new ArrayList();
		DBAccess db = null;
		SelectControl selCon = new SelectControl();

		try {
			db = new DBAccess();
			db
					.prepareDirectQuery("SELECT DISTINCT(ETS_USERS.USER_ID) FROM ETS.ETS_USERS AS ETS_USERS WHERE ETS_USERS.ACTIVE_FLAG  = 'A'");
			//"WHERE ETS_USERS.USER_PROJECT_ID = '1157361555891' AND ETS_USERS.ACTIVE_FLAG  = 'A'");
			int rows = db.execute();
			accountcontactlist.add(new SelectControl("Select", null));
			for (int i = 0; i < rows; i++) {

				ETSUserDetails u = new ETSUserDetails();
				selCon = new SelectControl();
				u.setWebId(db.getString(i, "USER_ID"));
				u.extractUserDetails(db.getConnection());

				selCon.setLable(u.getFirstName() + " " + u.getLastName());
				selCon.setValue(u.getEMail());

				accountcontactlist.add(selCon);
			}

			db.close();
			db = null;
		} catch (Exception e) {

		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return accountcontactlist;

	}
    /**
     * This method is used to Executive_Sponsor(basically user) based on the project id.
     * @param proj
     * @return
     */
	public ArrayList getExecutive_Sponsor(String proj) {


		ArrayList executivesponcerlist = new ArrayList();
		DBAccess db = null;
		SelectControl selCon = new SelectControl();

		try {
			db = new DBAccess();
			String q="select distinct user_id from ets.ets_users where user_project_id='"+proj+"' and active_flag='A'";
			db.prepareDirectQuery(q);
			//db.prepareDirectQuery("SELECT DISTINCT(ETS_USERS.USER_ID) FROM ETS.ETS_USERS AS ETS_USERS  WHERE ETS_USERS.ACTIVE_FLAG  = 'A'");
			int rows = db.execute();
			executivesponcerlist.add(new SelectControl("Select", null));
			for (int i = 0; i < rows; i++) {

				ETSUserDetails u = new ETSUserDetails();
				selCon = new SelectControl();
				u.setWebId(db.getString(i, "USER_ID"));
				u.extractUserDetails(db.getConnection());

				selCon.setLable(u.getFirstName() + " " + u.getLastName());
				selCon.setValue(u.getEMail());

				executivesponcerlist.add(selCon);
			}

			db.close();
			db = null;
		} catch (Exception e) {

		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return executivesponcerlist;

	}

	/**
	 * This method is used to see if the variable is null or not if null return "" else the input value.
	 * @param sInString
	 * @return
	 */
	public static String checkNull(String sInString) {

		String sOutString = "";

		if (sInString == null || sInString.trim().equals("")) {
			sOutString = "";
		} else {
			sOutString = sInString.trim();
		}

		return sOutString;

	}
	/**
	 * This method is used to get the date in the form of mm/dd/yy
	 * @param date
	 * @return
	 */
	public String getSchduledate(String date) {
		StringTokenizer st = new StringTokenizer(date, "-");
		String year = st.nextElement().toString();
		String month = st.nextElement().toString();
		String dd = st.nextElement().toString();
		StringTokenizer st1 = new StringTokenizer(dd);
		String day = st1.nextElement().toString();
		String schduledate = month + "/" + day + "/" + year.substring(2);
		return schduledate;
	}
	/**
	 * This method is used to get the AccntcontByroles based on the projet id.
	 * @param proj
	 * @return
	 * @throws Exception
	 */
	public ArrayList getAccntcontByroles(String proj) throws Exception{

		//New implementation by KP
		System.out.println("In getAccntcontByroles, for proj="+proj);
		ArrayList acctCntCandidates = new ArrayList();
		acctCntCandidates = UserUtils.getUserByRoleName("WS_OWNER", proj);
		acctCntCandidates.addAll(UserUtils.getUserByRoleName("WS_MGR",proj ));
		MiscUtils.removeDuplicates(acctCntCandidates);
		DBAccess db = null;
		try {
			db = new DBAccess();

			for (int i = 0; i < acctCntCandidates.size(); i++) {
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId((String) acctCntCandidates.get(i));
				u.extractUserDetails(db.getConnection());

				acctCntCandidates.set(i, new SelectControl(u.getFirstName()	+ " " + u.getLastName(), (String) acctCntCandidates
						.get(i)));
			}
			db.close();
			db = null;
		} catch (Exception e) {

		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		System.out.println("Accout contact candidates are :");
		for(int i = 0; i < acctCntCandidates.size(); i++)
			System.out.println(acctCntCandidates.get(i));
		return acctCntCandidates;

		//Old implementation follows, commented.
		/*
		DBAccess db = new DBAccess();
		SelectControl selCon = null;
		Vector vMembers = ETSDatabaseManager.getProjMembers(proj,Defines.SORT_BY_USERID_STR,"asc",true,true,db.getConnection());
		ArrayList userlistwithrole = new ArrayList();
		if (vMembers != null && vMembers.size() > 0) {
			System.out.println(vMembers.size()+"-----------------------------------------------");
			for (int i = 0; i < vMembers.size(); i++) {
				selCon = new SelectControl();
				ETSUser user = (ETSUser) vMembers.elementAt(i);
				//System.out.println("User Role is :-"+user.getRoleName()+":"+ user.getUserJob());
				String userrole = ETSUtils.getUserRole(user.getUserId(),proj,db.getConnection());
				if(userrole.equals(Defines.WORKSPACE_OWNER) || userrole.equals(Defines.WORKSPACE_MANAGER))
				{
					selCon.setLable(ETSUtils.getUsersName(db.getConnection(),user.getUserId()));
				    selCon.setValue(user.getUserId());
				    System.out.println("Name :-"+ETSUtils.getUsersName(db.getConnection(),user.getUserId())+"YYYYYYYYYYYYY"+user.getUserId()+"YYYYYY");
				    userlistwithrole.add(selCon);
				}
			}
		}
		//List newList= new ArrayList(new HashSet(userlistwithrole)); // for removing the duplicate entry
		return userlistwithrole;
		*/
	}
	/**
	 * This method is used to get all the name of the employee based on the webid.
	 * @param webid
	 * @return
	 */
	public String getNameOnWebid(String webid){
		DBAccess db = null;
		System.out.println("Printing the name of the owner..................................................."+webid);
		String name = null;
		 try{
		 db = new DBAccess();
		 ETSUserDetails u = new ETSUserDetails();
		 u.setWebId(webid);
		 u.extractUserDetails(db.getConnection());
		 name = u.getFirstName()+" " +u.getLastName();
		 db.close();
		 db = null;
		 }catch(Exception e){
		 	System.out.println(e.getMessage());
		 }finally{
			 if(db!=null){
				 try{
					 db.close();
					 db=null;
				 }catch(Exception ex){
				 }
			 }
		 }
		 return name;
		 }

	/**
	 * This method is used to get the name of the sector.
	 * @param projectId
	 * @return
	 */
	public String getSector(String projectId) {
		DBAccess db = null;
		String sector = null;
		try {
			db = new DBAccess();
			db.prepareDirectQuery("SELECT SECTOR FROM ETS.ETS_PROJECTS WHERE PROJECT_ID='" + projectId + "'");
			int projectIdrow = db.execute();
			for (int i = 0; i < projectIdrow; i++) {
				sector =  db.getString(i, "SECTOR").toString(); //Get sector
			}
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
					db = null;
				} catch (Exception ex) {

				}
			}
		}
		return sector;
	}

	/**
	 * This method is used to get the current stage of the workflow.
	 * @param projectID
	 * @param workflowID
	 * @param db
	 * @return
	 */

	public String getWorkflowCurrentStage(String projectID,String workflowID,DBAccess db){
		boolean valid = false;
		String stageName = "";
		try{
			db.prepareDirectQuery("SELECT WF_CURR_STAGE_NAME CURRSTAGE FROM ETS.WF_DEF WHERE PROJECT_ID='"+projectID+"' AND WF_ID='"+workflowID+"' with ur");
			int rows= db.execute();
			if(rows>0){
				stageName = db.getString(0,"CURRSTAGE");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
		}
		return stageName;

	}
	/** ----------------------------------------------------------------------------------------------------------
	 Capturing the History and putting into the WF database code starts here
	 --------------------------------------------------------------------------------------------------------- */
	    String HistoryID = "";
	    boolean updatestatus = false;//if any field modified then put updatestatus as true
	    /**
	     * This Method is used to add the history data into the history table.
	     * @param newobj
	     * @param db
	     * @throws SQLException
	     * @throws Exception
	     */
	    public void updateSetMetHistoryforStage(WorkflowObject newobj,DBAccess db,String oldval,String newval) throws SQLException, Exception{
	    HistoryID = ETSCalendar.getNewCalendarId();

	    updateSetmetHistory(newobj,db);
	    updateSetmetHistoryFileds(newobj,db,oldval,newval);
	    }

	    /**
	     * This method is used to insert the History information in the WF_HISTORY_FIELD table for the Stage change.
		 * @param newobj
		 * @param db
		 */
		private void updateSetmetHistoryFileds(WorkflowObject newobj, DBAccess db,String oldval,String newval) throws Exception{


			db.prepareDirectQuery("INSERT INTO ETS.WF_HISTORY_FIELD (WF_HISTORY_ID,PROJECT_ID," +
									"FIELD_CHANGED,PREVIOUS_VALUE,NEW_VALUE)VALUES (?,?,?,?,?)");

			db.setString(1, HistoryID); //Unique ID to identify the WF History
			db.setString(2, newobj.getProjectID()); //Project_id of the workspace in which this workflow will be created.Parent key -> ETS_PROJECTS
			db.setString(3, "Current Stage"); //
			db.setString(4, oldval); //
			db.setString(5, newval); //

			db.execute();
		}

		/**
		 * This method is used to insert the history data into WF_HISTORY fileds for the stage change.
		 * @param newobj
		 * @param db
		 */
		private void updateSetmetHistory(WorkflowObject newobj, DBAccess db)throws Exception {




			SetMetIdentifyStageObject newobj1 = (SetMetIdentifyStageObject)newobj;



			db.prepareDirectQuery("INSERT INTO ETS.WF_HISTORY (WF_HISTORY_ID,PROJECT_ID,WF_ID," +
		               "WF_RESOURCE_ID,ACTION,ACTION_BY,ACTION_DATE,COMMENT,LAST_TIMESTAMP)VALUES " +
		               "(?,?,?,?,?,?,?,?,?)");
			db.setString(1, HistoryID); //Unique ID to identify the WF History
			db.setString(2, newobj1.getProjectID()); //Project_id of the workspace in which this workflow will be created.Parent key -> ETS_PROJECTS
			db.setString(3, newobj1.getWorkflowID()); // Unique ID to identify the Workflow.Parent key -> WF_DEF
			java.sql.Timestamp currentts = new java.sql.Timestamp(System.currentTimeMillis());
			db.setString(4, newobj1.getStageID()); //WF_RESOURCE_ID = Either ISSUE_ID or WF_STAGE_Id
			db.setString(5, "Set/Met Modified"); //Actions done; MODIFIED etc
			db.setString(6, newobj1.getLastUsr()); //IBM ID of the  profile creator/updater
			java.sql.Date actiondate = new java.sql.Date(new java.util.Date().getTime());
			java.sql.Timestamp currenttime = new java.sql.Timestamp(System.currentTimeMillis());
			db.setDate(7, actiondate); //Date on which the action has taken
			db.setString(8,"Set/Met Stage Modified"); //Comments
			db.setObject(9, currenttime); //Date on which the action has taken

			db.execute();
		}

		/**
	     *
	     * @param oldobj
	     * @param obj
	     * @param db
	     * @throws Exception
	     */
	    public void updateSetMetHistoryTable(SetMetIdentifyStageObject oldobj, SetMetIdentifyStageObject newobj,DBAccess db)
			throws Exception {
	    	HistoryID = ETSCalendar.getNewCalendarId();
	    	ArrayList fieldsToModify = new ArrayList();
		    fieldsToModify = compareObjectValues(oldobj,newobj,db);//get the Modified fiels values and updatestatus
		    if(updatestatus){
		    	setSetMetHistoryInfo(newobj,db);
		    	setSetMetHistoryFields(fieldsToModify,db);
		    }
	    }

		/**
		 *
		 * @param oldobj
		 * @param obj
		 * @param db
		 * @throws Exception
		 */
		public void setSetMetHistoryInfo(SetMetIdentifyStageObject obj,DBAccess db)
				throws Exception {
				db.prepareDirectQuery("INSERT INTO ETS.WF_HISTORY (WF_HISTORY_ID,PROJECT_ID,WF_ID," +
						               "WF_RESOURCE_ID,ACTION,ACTION_BY,ACTION_DATE,COMMENT,LAST_TIMESTAMP)VALUES " +
						               "(?,?,?,?,?,?,?,?,?)");
				db.setString(1, HistoryID); //Unique ID to identify the WF History
				db.setString(2, obj.getProjectID()); //Project_id of the workspace in which this workflow will be created.Parent key -> ETS_PROJECTS
				db.setString(3, obj.getWorkflowID()); // Unique ID to identify the Workflow.Parent key -> WF_DEF
				java.sql.Timestamp currentts = new java.sql.Timestamp(System.currentTimeMillis());
				db.setString(4, obj.getStageID()); //WF_RESOURCE_ID = Either ISSUE_ID or WF_STAGE_Id
				db.setString(5, "Set/Met Modified"); //Actions done; MODIFIED etc
				db.setString(6, obj.getLastUsr()); //IBM ID of the  profile creator/updater
				java.sql.Date actiondate = new java.sql.Date(new java.util.Date().getTime());
				java.sql.Timestamp currenttime = new java.sql.Timestamp(System.currentTimeMillis());
				db.setDate(7, actiondate); //Date on which the action has taken
				//db.setString(8, "Modified on :-"+actiondate.toString()); //Comments
				db.setString(8, "Set/Met Modified"); //Comments
				db.setObject(9, currenttime); //Date on which the action has taken
				db.execute();
		}

	/**
	 *
	 * @param oldobj
	 * @param obj
	 * @param db
	 * @throws Exception
	 */
		public void setSetMetHistoryFields(ArrayList historydata,DBAccess db)
				throws Exception {
			 String query = "insert_wf_history";
        	  System.out.println(query+"^^^^^^^^^^^%%%%%%%%%%%%%%%%%%%^^^^^^^^^");
			   boolean status = db.batchQuery(query,historydata);
			}

		/**
		 *
		 * @param objold
		 * @param objnew
		 * @return
		 * @throws Exception
		 */

		public ArrayList compareObjectValues1(SetMetIdentifyStageObject objold,SetMetIdentifyStageObject objnew)
		throws Exception {

			ArrayList modifiedFields = new ArrayList();
			Class cl = Class.forName(objold.getClassName());
			Method m[]=cl.getDeclaredMethods();
			for(int i=0;i<=m.length;i++){
				String methodName = m[i].getName();
				System.out.println("1.methodname----------"+methodName +"--------\n");
				String oldValue = (String)m[i].invoke(objold,null);
				System.out.println("\n 2.oldValue----------"+oldValue +"--------\n");
				String newValue = (String)m[i].invoke(objnew,null);
				System.out.println("\n 3.newValue----------"+newValue +"--------\n");
				if(!methodName.equalsIgnoreCase("getIbmlist") && !methodName.equalsIgnoreCase("getAttendees")){//chk for ibm attendees and Client
					if(!oldValue.equalsIgnoreCase(newValue)){
						this.updatestatus = true;
						ArrayList modifiedValues = new ArrayList();

						System.out.println("4. ----------------------------------------------");
						modifiedValues.add(HistoryID);
						modifiedValues.add(objold.getProjectID());
						//modifiedValues.add(getFieldNames(methodName));
						modifiedValues.add(oldValue);
						modifiedValues.add(newValue);

						modifiedFields.add(modifiedValues);
						modifiedValues = null;
					}
				}
			}
			return modifiedFields;
		}

		/**
		 *
		 * @param objold
		 * @param objnew
		 * @return
		 * @throws Exception
		 */
		public ArrayList compareObjectValues(SetMetIdentifyStageObject objold,SetMetIdentifyStageObject objnew,DBAccess db)
		throws Exception {
			ArrayList modifiedFields = new ArrayList();

			String oldplanneddate = objold.getPlMon() + "/"+  objold.getPlDate()+ "/" + objold.getPlYear();
			String newplanneddate = objnew.getPlMon() + "/" + objnew.getPlDate()+ "/" + objnew.getPlYear();

			String boldplanneddate = objold.getBiWeeklyMon() + "/"+ objold.getBiWeeklyDt() +"/" + objold.getBiWeeklyYr();
			String bnewplanneddate = objnew.getBiWeeklyMon() + "/"+ objnew.getBiWeeklyDt() +"/" + objnew.getBiWeeklyYr();

			System.out.println(objold.isClientScorecard()+"22========================================="+ objnew.isClientScorecard());

			if(compareArrays(objold.getAttendees(),objnew.getAttendees())){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),getClientnameOnclientID(objold.getAttendees(),db),getClientnameOnclientID(objnew.getAttendees(),db),"Client Attendees"));
			}if(compareArrays(objold.getIbmlist(),objnew.getIbmlist())){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),concateArray(objold.getIbmlist()),concateArray(objnew.getIbmlist()),"IBM Attendees"));
			}if(!objold.isClientScorecard()== objnew.isClientScorecard()){
				this.updatestatus = true;
				String oldval = "All level \"A\" ";
				String newval = "All level \"A\" ";
				if(objold.isClientScorecard() == true){
					oldval = "Clent level \"C\" ";
				}if(objnew.isClientScorecard() == true){
					newval = "Clent level \"C\" ";
				}
				modifiedFields.add(addToBatchList(objold.getProjectID(),oldval,newval,"Client Level"));
			}if(!objold.getSponsor().equalsIgnoreCase(objnew.getSponsor())){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),objold.getSponsor(),objnew.getSponsor(),"Execut Sponsor"));
			}if(!objold.getNsiRating().equalsIgnoreCase(objnew.getNsiRating())){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),objold.getNsiRating(),objnew.getNsiRating(),"Nsi Rating"));
			}if(!objold.getSponsor().equalsIgnoreCase(objnew.getSponsor())){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),objold.getSponsor(),objnew.getSponsor(),"Account Contact"));
			}if(!objold.getYear().equalsIgnoreCase(objnew.getYear())){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),objold.getYear(),objnew.getYear(),"Year"));
			}if(!objold.getDelegate().equalsIgnoreCase(objnew.getDelegate())){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),objold.getDelegate(),objnew.getDelegate(),"Backup Account Contact"));
			}if(!objold.getQuarter().trim().equalsIgnoreCase(objnew.getQuarter().trim())){
				this.updatestatus = true;
				String oldQtr = "Q".concat(objold.getQuarter().substring(1)).concat(objold.getYear().substring(2));
				String newQtr = "Q".concat(objnew.getQuarter().substring(1)).concat(objnew.getYear().substring(2));
				modifiedFields.add(addToBatchList(objold.getProjectID(),oldQtr,newQtr,"Quarter"));
			}if(!oldplanneddate.trim().equalsIgnoreCase(newplanneddate.trim())){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),oldplanneddate,newplanneddate,"Planned date"));
			}if(!objold.getMeetingLocn().equalsIgnoreCase(objnew.getMeetingLocn())){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),objold.getMeetingLocn(),objnew.getMeetingLocn(),"Meeting Location"));
			}if(!objold.isBiWeeklyflg() == objnew.isBiWeeklyflg()){
				this.updatestatus = true;
				String oldval = "false";
				String newval = "false";
				if(objold.isBiWeeklyflg() == true){
					oldval = "true";
				}if(objnew.isBiWeeklyflg() == true){
					newval = "true";
				}
				modifiedFields.add(addToBatchList(objold.getProjectID(),oldval,newval,"Biweekly flag"));
			}if(objold.isBiWeeklyflg() == true && objnew.isBiWeeklyflg() == true){
				this.updatestatus = true;
				if(!boldplanneddate.trim().equalsIgnoreCase(bnewplanneddate.trim())){
					this.updatestatus = true;
					modifiedFields.add(addToBatchList(objold.getProjectID(),boldplanneddate,bnewplanneddate,"Biweekly Planned date"));
				}if(!objold.getBiWeeklyStatus().equalsIgnoreCase(objnew.getBiWeeklyStatus())){
					this.updatestatus = true;
					modifiedFields.add(addToBatchList(objold.getProjectID(),objold.getBiWeeklyStatus(),objnew.getBiWeeklyStatus(),"Biweekly Status"));
				}
			}if(objold.isBiWeeklyflg() == true && objnew.isBiWeeklyflg() == false){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID(),boldplanneddate," - ","Biweekly Planned date"));
				modifiedFields.add(addToBatchList(objold.getProjectID(),objold.getBiWeeklyStatus()," - ","Biweekly Status"));
			}if(objold.isBiWeeklyflg() == false && objnew.isBiWeeklyflg() == true){
				this.updatestatus = true;
				modifiedFields.add(addToBatchList(objold.getProjectID()," - ",bnewplanneddate,"Biweekly Planned date"));
				modifiedFields.add(addToBatchList(objold.getProjectID()," - ",objnew.getBiWeeklyStatus(),"Biweekly Status"));
			}
			return modifiedFields;
		}
		/**
		 *
		 * @param firstfieldname
		 * @param secondfieldname
		 * @return
		 */
		 public boolean compareArrays(String[] firstfieldname,String[] secondfieldname){
	    	boolean compare = false;
	    	if(firstfieldname.length >  0 || firstfieldname !=null || secondfieldname.length > 0 || secondfieldname != null){
	    		for(int i=0 ;i< firstfieldname.length;i++){
	    			for(int j=0 ;j< secondfieldname.length;j++){
	    				if(!firstfieldname[i].trim().equalsIgnoreCase(secondfieldname[j].trim())){
	    					compare = true;
	    				}
	    			}
	    		}
	    	}
	    	return compare;
	    }
           /**
            *
            * @param ID
            * @param db
            * @return
            */
			public WorkflowObject getWorkflowObjectOld(String ID,DBAccess db) {
				String wfid = ID;
				SetMetIdentifyStageObject sObj = new SetMetIdentifyStageObject();
				try {
					db
							.prepareDirectQuery("SELECT A.PROJECT_ID,A.WF_ID,B.WF_STAGE_ID,B.SCORING_LEVEL,"
									+ "A.REQUESTOR,A.ACCT_CONTACT,A.BACKUP_ACCT_CONTACT,B.EXEC_SPONSOR,"
									+ "B.LOCATION,B.BIWEEKLY_FLAG,B.BIWEEKLY_DATE,B.BIWEEKLY_STATUS,"
									+ "B.NSI_RATING,A.QUARTER,A.YEAR,A.MEETING_ID,A.WF_CURR_STAGE_NAME "
									+ "FROM ETS.WF_DEF A , ETS.WF_STAGE_IDENTIFY_SETMET B WHERE A.WF_ID = '"
									+ wfid + "' AND B.WF_ID = '" + wfid + "' with ur");
					int setmetrow = db.execute();
					for (int i = 0; i < setmetrow; i++) {
						sObj.setProjectID(db.getString(i, "PROJECT_ID")); //Get project id
						sObj.setWorkflowID(db.getString(i, "WF_ID")); //workflow id
						sObj.setStageID(db.getString(i, "WF_STAGE_ID")); //workflow stage id
						sObj.setRequestor(checkNull(db.getString(i, "REQUESTOR"))); //requestor
						sObj.setAcctContact(checkNull(db.getString(i, "ACCT_CONTACT"))); //Account contact
						sObj.setDelegate(checkNull(db.getString(i,
								"BACKUP_ACCT_CONTACT")));//Backup account contact
						sObj.setClientScorecard(db.getString(i, "SCORING_LEVEL").equalsIgnoreCase("C") ? true : false); //SCORING_LEVEL
						sObj.setSponsor(checkNull(db.getString(i, "EXEC_SPONSOR"))); //Executive sponcer
						sObj.setMeetingLocn(checkNull(db.getString(i, "LOCATION"))); //meeting location
						sObj.setBiWeeklyflg(db.getString(i, "BIWEEKLY_FLAG").equalsIgnoreCase("Y") ? true : false);

						String  biweeklydate =  db.getString(i,"BIWEEKLY_DATE");
						StringTokenizer bst = new StringTokenizer(biweeklydate, "-");
						String byear = bst.nextElement().toString();
						String bmonth = bst.nextElement().toString();
						String bdd = bst.nextElement().toString();
						sObj.setBiWeeklyDt(bdd);
						sObj.setBiWeeklyMon(bmonth);
						sObj.setBiWeeklyYr(byear);

						sObj.setBiWeeklyStatus(db.getString(i, "BIWEEKLY_STATUS").toString().trim());
						sObj.setNsiRating(db.getString(i, "NSI_RATING")); //Nsi Rating

						int getQuarter = Integer.parseInt(db.getString(i, "QUARTER"));
						String QTR = null;
						switch (getQuarter) {
						case 0:
							QTR = "00";
							break;
						case 1:
							QTR = "01";
							break;
						case 2:
							QTR = "02";
							break;
						case 3:
							QTR = "03";
							break;
						case 4:
							QTR = "04";
							break;
						}
						sObj.setQuarter(QTR); //Quarter
						sObj.setYear(checkNull(db.getString(i, "YEAR"))); //YEAR
						sObj.setMeetingID(checkNull(db.getString(i, "MEETING_ID"))); //meeting id

						sObj.setIbmlist(ibmattendees_List(wfid,db));
						sObj.setAttendees(clientAttendees_list(wfid,db));

						String plannedddate = schdule_date(wfid,db);
						StringTokenizer st = new StringTokenizer(plannedddate, "-");
						String year = st.nextElement().toString();
						String month = st.nextElement().toString();
						String dd = st.nextElement().toString().substring(0, 2);

						sObj.setPlDate(dd);
						sObj.setPlMon(month);
						sObj.setPlYear(year);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {

				}
				return sObj;
			}
			/**
			 *
			 * @param projID
			 * @param oldval
			 * @param newval
			 * @param field
			 * @return
			 */
		public ArrayList addToBatchList(String projID,String oldval,String newval,String field){
			ArrayList modifiedValues = null;
			modifiedValues = new ArrayList();
			modifiedValues.add(HistoryID);
			modifiedValues.add(projID);
			modifiedValues.add(field);
			modifiedValues.add(oldval);
			modifiedValues.add(newval);

			return modifiedValues;
		}
		/**
		 *
		 * @param stringArray
		 * @return
		 */
		public String concateArray(String[] stringArray){
			String concatevalue="";
			if(stringArray !=null ){
				for (int x = 0; x < stringArray.length; x++)
					concatevalue = concatevalue + stringArray[x].trim() + ", ";
			}
			if (concatevalue != null) {
				concatevalue = concatevalue.trim();
				concatevalue = concatevalue.substring(0, (concatevalue.length() - 1)) + concatevalue.substring(concatevalue.length());// Remove the last ','
				}
			return concatevalue;
		}

	/** ----------------------------------------------------------------------------------------------------------
	 Capturing the History and putting into the WF database code Ends here
	 --------------------------------------------------------------------------------------------------------- */
	/** ----------------------------------------------------------------------------------------------------------
	 Show the History data from the Table Code starts here
	 --------------------------------------------------------------------------------------------------------- */
	/**
	 *
	 * @param projectID
	 * @return
	 */
	public ArrayList getHistoryList(String wid) {
			ArrayList list = new ArrayList();
			DBAccess db = null;
			boolean isOldSetmet = false;
			try {
				db = new DBAccess();
				ArrayList hlist = gethistoryidlist(wid,db);
				for  ( int i=0;i<hlist.size();i++ )   {
					Object h = 	gethistorylistAccordingToID(wid,hlist.get(i).toString(),db);
					if(h==null)
					{
						isOldSetmet = true;
						break;
					}
					list.add(h);
				}

	           	db.close();
				db = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null) {
					try {
						db.close();
						db = null;
					} catch (Exception ex) {

					}
				}
			}
			if(isOldSetmet)list.clear();
			return list;
		}
	/**
	 *
	 * @param historyId
	 * @param db
	 * @return
	 */
	public ArrayList gethistorylistAccordingToID(String wfid,String actiondate,DBAccess db){
		ArrayList list = new ArrayList();
		try{
			db.prepareDirectQuery("select A.WF_HISTORY_ID,A.PROJECT_ID,A.WF_RESOURCE_ID,A.ACTION," +
									"A.ACTION_BY,A.ACTION_DATE,A.COMMENT,A.LAST_TIMESTAMP,B.FIELD_CHANGED,B.PREVIOUS_VALUE," +
									"B.NEW_VALUE FROM ETS.WF_HISTORY A ,ETS.WF_HISTORY_FIELD B  where " +
									"(A.WF_HISTORY_ID=B.WF_HISTORY_ID AND A.WF_ID ='"+wfid+"' AND " +
									"ACTION_DATE ='"+actiondate+"' AND ACTION='Set/Met Modified') ORDER BY A.ACTION_BY DESC with ur");

			int rows= db.execute();
			for(int i = 0; i < rows ; i++){
				SetMetHistoryObject hObj = new SetMetHistoryObject();
				boolean isOldSetmet = false;
				isOldSetmet = isOldSetmet || db.getObject(i,"ACTION_DATE")==null;
				isOldSetmet = isOldSetmet || db.getObject(i,"FIELD_CHANGED")==null;
				isOldSetmet = isOldSetmet || db.getObject(i,"PREVIOUS_VALUE")==null;
				isOldSetmet = isOldSetmet || db.getObject(i,"NEW_VALUE")==null;
				isOldSetmet = isOldSetmet || db.getObject(i,"COMMENT")==null;
				isOldSetmet = isOldSetmet || db.getObject(i, "LAST_TIMESTAMP")==null;
				isOldSetmet = isOldSetmet || db.getObject(i,"ACTION_BY")==null;

				if(isOldSetmet)
					return null;

				hObj.setDateModified(formateDateOnMonth(db.getString(i,"ACTION_DATE")));
	            hObj.setModifiedField(db.getString(i,"FIELD_CHANGED"));
	            hObj.setPreviousValue(db.getString(i,"PREVIOUS_VALUE"));
	            hObj.setNewValue(db.getString(i,"NEW_VALUE"));
	            hObj.setComments(db.getString(i,"COMMENT"));
	            java.util.Date dt = (Date) db.getObject(i, "LAST_TIMESTAMP");
	            hObj.setTimeModified(checkNull(dt.toString().substring(10,19)));
	            hObj.setAuthor(db.getString(i,"ACTION_BY"));

				list.add(hObj);
				hObj = null;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
		}
		return list;
	}
	/**
	 *
	 * @param wfid
	 * @param db
	 * @return
	 */
	public ArrayList gethistoryidlist(String wfid,DBAccess db){
		ArrayList list = new ArrayList();
		try{
			db.prepareDirectQuery("SELECT DISTINCT(A.ACTION_DATE) FROM ETS.WF_HISTORY A WHERE  " +
								"A.WF_ID ='"+wfid+"'AND A.ACTION='Set/Met Modified' ORDER BY A.ACTION_DATE DESC ");
			int rows= db.execute();
			for(int i = 0; i < rows ; i++){
				list.add(db.getString(i,"ACTION_DATE"));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
		}
		return list;
	}
	/**
	 *
	 * @param dateinput
	 * @return
	 */
	public String formateDate(String dateinput){
		StringTokenizer st = new StringTokenizer(dateinput, "-");
		String year = st.nextElement().toString().substring(2,4);
		String month = st.nextElement().toString();
		String dd = st.nextElement().toString();
		return month+"-"+dd+"-"+year;

	}
	/**
	 *
	 * @param dateInput
	 * @return
	 * @throws ParseException
	 */
	// return date in the form July 24,2008
	 public String formateDateOnMonth(String dateInput) throws ParseException{
		String date1 = "";
		Format formatter;
		StringTokenizer st = new StringTokenizer(dateInput, "-");
		SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd");
		Date date=df.parse(st.nextElement().toString()+"/"+st.nextElement().toString()+"/"+st.nextElement());
		formatter = new SimpleDateFormat("MMM dd,yyyy");
		date1 = formatter.format(date);
		return date1;

	}
	 /**
	  *
	  * @param dateInput
	  * @return
	  * @throws ParseException
	  */
//	 return date in the form July 24,2008
	 public String formateDateOnTime(String dateInput) throws ParseException{
		String date1 = "";
		Format formatter;
		StringTokenizer st = new StringTokenizer(dateInput, "-");
		SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd");
		Date date=df.parse(st.nextElement().toString()+"/"+st.nextElement().toString()+"/"+st.nextElement());
		formatter = new SimpleDateFormat("HH.mm.ss");
		date1 = formatter.format(date);
		return date1;

	}
	 /**
	  *
	  * @param clientid
	  * @param db
	  * @return
	  */
	public String getClientnameOnclientID(String[] clientid,DBAccess db){
		String clientname="";
		if (clientid != null) {
			for (int x = 0; x < clientid.length; x++)
				try {
					db = new DBAccess();
					db
							.prepareDirectQuery("SELECT FNAME ,LNAME,TITLE FROM ETS.WF_CLIENT WHERE CLIENT_ID ='"+clientid[x].trim()+"'");
					int setmetrow = db.execute();
					for (int i = 0; i < setmetrow; i++) {

						String FNAME = db.getString(i, "FNAME");
						String LNAME = db.getString(i, "LNAME");
						//String TITLE = checkNull(db.getString(i, "TITLE"));
						//clientname = clientname+FNAME.concat(" " + LNAME + " ").concat("-").concat(" " + (TITLE))+",";
						clientname = clientname+FNAME.concat(" " + LNAME)+", ";
					}
				} catch (Exception e) {
					e.getMessage();
				} finally {
				}
		}
		if (clientname != null || clientname.length() > 0) {
			clientname = clientname.trim();
			clientname = clientname.substring(0, (clientname.length() - 1)) + clientname.substring(clientname.length());// Remove the last ','
		}
		return clientname;
	}
 	/**
 	 *
 	 * @param s
 	 * @param sep
 	 * @return
 	 */
	public String[] StringtoArray( String s, String sep ) {
	    StringBuffer buf = new StringBuffer(s);
	    int arraysize = 1;
	    for ( int i = 0; i < buf.length(); i++ ) {
	      if ( sep.indexOf(buf.charAt(i) ) != -1 )
	        arraysize++;
	    }
	    String [] elements  = new String [arraysize];
	    int y,z = 0;
	    if ( buf.toString().indexOf(sep) != -1 ) {
	      while (  buf.length() > 0 ) {
	        if ( buf.toString().indexOf(sep) != -1 ) {
	          y =  buf.toString().indexOf(sep);
	          if ( y != buf.toString().lastIndexOf(sep) ) {
	            elements[z] = buf.toString().substring(0, y ); z++;
	            buf.delete(0, y + 1);
	          }
	          else if ( buf.toString().lastIndexOf(sep) == y ) {
	            elements[z] = buf.toString().substring(0, buf.toString().indexOf(sep));
	            z++;
	            buf.delete(0, buf.toString().indexOf(sep) + 1);
	            elements[z] = buf.toString();z++;
	            buf.delete(0, buf.length() );
	          }
	        }
	      }
	    }
	    else {elements[0] = buf.toString(); }
	    buf = null;
	    return elements;
	  }
	/**
	 *
	 * @param s
	 * @param sep
	 * @return
	 */
	public String ArrayToString(String s[], String sep) {
	     int k;
	     String result = "";

	     k = s.length;
	     if (k > 0) {
	        result = s[0];
	          for (int i= 1 ; i < k; i++) {
	            result += sep + s[i] ;
	            }
	         }
	     return result;
	     }

	/** ----------------------------------------------------------------------------------------------------------
	 Show the History data from the Table Code ends here
	 --------------------------------------------------------------------------------------------------------- */

	/** ----------------------------------------------------------------------------------------------------------
	 Document Attachment and View Code starts here
	 --------------------------------------------------------------------------------------------------------- */

	/**
	 * This method will add the Document and its related information in the document table.
	 * @param iDocID
	 * @param strDocFileName
	 * @param iDocSize
	 * @param strFileDescription
	 * @param strFileStatus
	 * @param pdInStream
	 * @param db
	 * @return
	 * @throws SQLException
	 */
/*	public  boolean addDocFile(int iDocID,String strDocFileName,int iDocSize,String strFileDescription,	String strFileStatus,InputStream pdInStream,DBAccess db)throws SQLException {
			boolean bSuccess = false;

			try {
				//int iNewDocFileID = getNewDocFileID(iDocID);

				int iNewDocFileID = 112562;
				db.prepareDirectQuery(
						"INSERT INTO ETS.ETS_DOCFILE(DOC_ID,DOCFILE_ID,DOCFILE_NAME,DOCFILE,"
							+ "DOCFILE_SIZE,DOCFILE_UPDATE_DATE,FILE_DESCRIPTION,FILE_STATUS) "
							+ "VALUES(?,?,?,?,?,current timestamp,?,?)");

				db.setInt(1, iDocID);
				db.setInt(2, iNewDocFileID);
				db.setString(3, strDocFileName);
				if (pdInStream != null) {
					db.setBinaryStream(4, pdInStream, iDocSize);
				} else {
					db.setNull(4, Types.BLOB);
				}
				db.setInt(5, iDocSize);

				if (!StringUtil.isNullorEmpty(strFileDescription)) {
					db.setString(6, strFileDescription);
				} else {
					db.setNull(6, Types.VARCHAR);
				}

				if (!StringUtil.isNullorEmpty(strFileStatus)) {
					db.setString(7, strFileStatus);
				} else {
					db.setNull(7, Types.VARCHAR);
				}
				db.execute();
				bSuccess = true;
			} catch (SQLException e) {
				bSuccess = false;
				throw e;
			}
			return bSuccess;
		}*/


	/** ----------------------------------------------------------------------------------------------------------
	 Document Attachment and View Code Ends here
	 --------------------------------------------------------------------------------------------------------- */
	
	/**
	 * This is used to determine which earlier workflow will be used for copying
	 * @since 7.1.1
	 * @author KP
	 */
	public String[] getPreviousWorkflow(String projectID) {
		String[] returnValue = null;
		DBAccess db = null;
		try{
			db = new DBAccess();
			String query = "select wf_id, wf_name from ets.wf_def where project_id='"+projectID+"' and wf_type='SETMET' order by wf_id desc with ur";
			db.prepareDirectQuery(query);
			if(db.execute()>0)
				returnValue = new String[]{db.getString(0,0),db.getString(0,1)};
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		if(returnValue!=null)
			System.out.println("Pre-populating fields from wf_id="+returnValue[0]+", "+returnValue[1]);
		return returnValue;
	}
}
