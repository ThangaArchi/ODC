/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.sa.initialize;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.setmet.SetMetDAO;
import oem.edge.ets.fe.workflow.util.SelectControl;

import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
/**
 * Class       : InitializeDAO
 * Package     : oem.edge.ets.fe.workflow.sa.initialize
 * Description : 
 * Date		   : Feb 2, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class InitializeDAO extends AbstractDAO {
	
	private static Log logger = WorkflowLogger.getLogger(InitializeDAO.class);
	
	private String identifyStageID = null;
	public String getIdentifyStageID(){return identifyStageID;}
	
	public boolean saveWorkflowObject(WorkflowObject workflowObject) {
		
		if(!(workflowObject instanceof InitializeVO))
			return false;
		
		InitializeVO vo = (InitializeVO)workflowObject;
		
		DBAccess db = null;
		try{
			db = new DBAccess();
			String meetingID = ETSCalendar.getNewCalendarId();
			String workflowID = ETSCalendar.getNewCalendarId();
			identifyStageID = ETSCalendar.getNewCalendarId();
			
			createMeeting(vo,meetingID,db);
			createWorkflowDef(vo,workflowID,meetingID,db);
			createIdentifyStage(vo,identifyStageID,db);
			addClientAttendees(vo,db);
			addIBMAttendees(vo,db);
			
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
		return true;
	}


	private void createMeeting(InitializeVO vo, String meetingID, DBAccess db) throws Exception{
		Timestamp ts = new Timestamp(Integer.parseInt(vo.getPlannedYear()[0])-1900,Integer.parseInt(vo.getPlannedMonth()[0])-1,Integer.parseInt(vo.getPlannedDay()[0]),9,0,0,0);
		//TODO: insert invitees
		String query = "insert into ets.ets_calendar (calendar_id," +
													  "project_id," +
													  "calendar_type," +
													  "schedule_date," +
													  "scheduled_by," +
													  "start_time," +
													  "duration," +
													  "description," +
													  "cancel_flag," +
													  "email_flag," +
													  "ibm_only," +
													  "repeat_type," +
													  "subject" +
													  ") values (" +
													  "'"+meetingID+"'," +
													  "'"+vo.getProjectID()+"'," +
													  "'M'," +
													  "current timestamp," +
													  "'"+vo.getLoggedUser()+"'," +
													  "'"+ts.toString()+"'," +
													  "180," +
													  "'Self Assessment Meeting'," +
													  "'N'," +
													  "'N'," +
													  "'N'," +
													  "'N'," +
													  "'Self Assessment Meeting')";
		db.prepareDirectQuery(query);
		db.execute();
	}

	private void createWorkflowDef(InitializeVO vo, String workflowID, String meetingID, DBAccess db) throws Exception{
		
		String strCompany = vo.getCompany().trim();
		if(strCompany.length()>14)
			strCompany = strCompany.substring(0,14);
		String strMonth = vo.getPlannedMonth()[0].trim();
		if(strMonth.length()==1)
			strMonth="0"+strMonth;
		String strDay = vo.getPlannedDay()[0].trim();
		if(strDay.length()==1)
			strDay="0"+strDay;
		
		String wf_name = "SA" + "-" + strCompany
		+ "-" + strMonth + strDay
		+ vo.getPlannedYear()[0].substring(2).trim();

		String query = "insert into ets.wf_def (project_id, " +
												"wf_id," +
												"wf_type," +
												"wf_name," +
												"requestor," +
												"acct_contact," +
												"backup_acct_contact," +
												"quarter," +
												"year," +
												"meeting_id," +
												"wf_curr_stage_name," +
												"creation_date," +
												"last_userid," +
												"last_timestamp" +
												") values (" +
												"'"+vo.getProjectID()+"'," +
												"'"+workflowID+"'," +
												"'SELF ASSESSMENT',"+
												"'"+wf_name+"',"+
												"'"+vo.getLoggedUser()+"',"+
												"'"+vo.getAccountContact()[0]+"',"+
												"'"+vo.getBackupContact()[0]+"',"+
												""+vo.getQbrQuarter()[0]+","+
												"'"+vo.getQbrYear()[0]+"',"+
												"'"+meetingID+"',"+
												"'"+WorkflowConstants.IDENTIFY+"',"+
												"current date,"+
												"'"+vo.getLoggedUser()+"',"+
												"current timestamp)";
		db.prepareDirectQuery(query);
		db.execute();
		vo.setWorkflowID(workflowID);
	}
	private void createIdentifyStage(InitializeVO vo, String identifyStageID, DBAccess db) throws Exception{

		db.prepareDirectQuery(
		"INSERT INTO ETS.WF_STAGE_IDENTIFY_SETMET(PROJECT_ID,WF_ID,WF_STAGE_ID,SCORING_LEVEL,EXEC_SPONSOR,LOCATION,BIWEEKLY_FLAG,BIWEEKLY_DATE,BIWEEKLY_STATUS,NSI_RATING,STATUS,LAST_USERID,LAST_TIMESTAMP, RATING_PERIOD_FROM, RATING_PERIOD_TO) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
		);

		db.setString(1, vo.getProjectID()); 
		db.setString(2, vo.getWorkflowID());
		db.setString(3, identifyStageID);
		db.setString(4, "C");
		//db.setString(5, vo.getExecSponsor()[0]);
		db.setNull(5,Types.VARCHAR);
		db.setString(6, vo.getMeetingLocation());
		db.setString(7, "N");
		//db.setDate(8, new java.sql.Date(Integer.parseInt(vo.getBiweeklyYear()[0])-1900,Integer.parseInt(vo.getBiweeklyMonth()[0])-1,Integer.parseInt(vo.getBiweeklyDay()[0])));
		db.setNull(8, Types.DATE);
		//db.setString(9, vo.getBiweeklyStatus()[0]);
		db.setNull(9, Types.VARCHAR);
		db.setInt(10, Integer.parseInt(vo.getNsiRating()[0]));
		db.setString(11, "Y");
		db.setString(12, vo.getLoggedUser());
		db.setObject(13, new java.sql.Timestamp(System.currentTimeMillis()));
		/*db.setDate(14, new java.sql.Date(Integer.parseInt(vo.getRatingFromYear()[0])-1900,Integer.parseInt(vo.getRatingFromMonth()[0])-1,Integer.parseInt(vo.getRatingFromDay()[0])));
		db.setDate(15, new java.sql.Date(Integer.parseInt(vo.getRatingToYear()[0])-1900,Integer.parseInt(vo.getRatingToMonth()[0])-1,Integer.parseInt(vo.getRatingToDay()[0])));*/
		db.setNull(14, Types.DATE);
		db.setNull(15, Types.DATE);
		
		db.execute();
	}

	private void addIBMAttendees(InitializeVO vo, DBAccess db) throws Exception {
		if(vo.getIbmAttendees()==null)
			return;
		ArrayList ibmlist = new ArrayList();
		ArrayList innerlist = null;
		String str = "insert_wf_setmet_attendees_ibm";
		String[] ibm = vo.getIbmAttendees();
		ibmlist = new ArrayList();
		if (ibm != null) {
			for (int x = 0; x < ibm.length; x++) {
				innerlist = new ArrayList();
				innerlist.add(vo.getProjectID().trim()); //project id
				innerlist.add(vo.getWorkflowID().trim()); // WorkFlow id
				innerlist.add(ibm[x].trim()); //Unique IBM id
				ibmlist.add(innerlist);
				innerlist = null;
			}
			boolean status = db.batchQuery(str, ibmlist);
		}		
			
	}

	private void addClientAttendees(InitializeVO vo, DBAccess db) throws Exception{
		ArrayList clientlist = new ArrayList();
		ArrayList innerlist = null;
		String str = "insert_wf_setmet_attendees_client";
		String[] clientattendees = vo.getAttendees();
		clientlist = new ArrayList();
		if (clientattendees != null) {
			for (int x = 0; x < clientattendees.length; x++) {
				innerlist = new ArrayList();
				innerlist.add(vo.getProjectID().trim()); //project id
				innerlist.add(vo.getWorkflowID().trim()); //WorkFlow id
				innerlist.add(clientattendees[x].trim()); // unique Clientid
				clientlist.add(innerlist);
				innerlist = null;
			}
			boolean status = db.batchQuery(str, clientlist);
		}
	}

	public WorkflowObject getWorkflowObject(String workflowID, String company) {
		
		InitializeVO vo = new InitializeVO();
		
		SetMetDAO setMetDAO = new SetMetDAO();
		ArrayList clientPeople = setMetDAO.getClient_Attendees(company);
		
		String q1 = "select a.acct_contact, a.backup_acct_contact, a.quarter, a.year, b.exec_sponsor, b.location, b.nsi_rating, b.biweekly_flag, b.biweekly_date, b.biweekly_status, c.start_time, b.rating_period_from, b.rating_period_to from ets.wf_def a, ets.wf_stage_identify_setmet b, ets.ets_calendar c where a.meeting_id=c.calendar_id and a.wf_id=b.wf_id and a.wf_id='"+workflowID+"' with ur";
		String q2 = "select userid from ets.wf_setmet_attendees_ibm where wf_id='"+workflowID+"' with ur ";
		String q3 = "select userid from ets.wf_setmet_attendees_client where wf_id='"+workflowID+"' with ur";

		DBAccess db = null;
		try{
			db = new DBAccess();
			
			db.prepareDirectQuery(q1);
			db.execute();
			
			vo.setAccountContact(arr(db.getString(0,0)));
			vo.setBackupContact(arr(db.getString(0,1)));
			vo.setQbrQuarter(arr(db.getString(0,2)));
			vo.setQbrYear(arr(db.getString(0,3)));
			//vo.setExecSponsor(arr(db.getString(0,4)));
			vo.setMeetingLocation(db.getString(0,5));
			vo.setNsiRating(arr(db.getString(0,6)));
			
			//vo.setBiweeklyFlag(arr(db.getString(0,7)));
			
			/*String d = db.getString(0,8);
			System.out.println("Biweekly review date is "+d);
			String[] temp = new String[1];
			temp[0] = Integer.toString(Integer.parseInt(d.substring(5,7)));
			vo.setBiweeklyMonth(temp);
			temp = new String[1];
			temp[0] = Integer.toString(Integer.parseInt(d.substring(8,10)));
			vo.setBiweeklyDay(temp);
			temp = new String[1];
			temp[0] = d.substring(0,4);
			vo.setBiweeklyYear(temp);
			if(db.getString(0,9)!=null)
				vo.setBiweeklyStatus(arr(db.getString(0,9).trim()));
			else
				vo.setBiweeklyStatus(arr("NA"));
			*/			
			java.util.Date dt = (Date) db.getObject(0,10);
			String d = dt.toString();
			String[] temp = null;
			System.out.println("Planned meeting date is "+d);
			temp = new String[1];
			temp[0] = Integer.toString(Integer.parseInt(d.substring(5,7)));
			vo.setPlannedMonth(temp);
			temp = new String[1];
			temp[0] = Integer.toString(Integer.parseInt(d.substring(8,10)));
			vo.setPlannedDay(temp);
			temp = new String[1];
			temp[0] = d.substring(0,4);
			vo.setPlannedYear(temp);
			
			/*d = db.getString(0,11);
			temp = new String[1];
			temp[0] = Integer.toString(Integer.parseInt(d.substring(5,7)));
			vo.setRatingFromMonth(temp);
			temp = new String[1];
			temp[0] = Integer.toString(Integer.parseInt(d.substring(8,10)));
			vo.setRatingFromDay(temp);
			temp = new String[1];
			temp[0] = d.substring(0,4);
			vo.setRatingFromYear(temp);
			
			d = db.getString(0,12);
			temp = new String[1];
			temp[0] = Integer.toString(Integer.parseInt(d.substring(5,7)));
			vo.setRatingToMonth(temp);
			temp = new String[1];
			temp[0] = Integer.toString(Integer.parseInt(d.substring(8,10)));
			vo.setRatingToDay(temp);
			temp = new String[1];
			temp[0] = d.substring(0,4);
			vo.setRatingToYear(temp);*/
			
			db.prepareDirectQuery(q2);
			int rows = db.execute();
			String[] ibmAttendees = new String[rows];
			for(int i=0; i<rows; i++)
				ibmAttendees[i] = db.getString(i,0);
			vo.setIbmAttendees(ibmAttendees);
			
			db.prepareDirectQuery(q3);
			rows = db.execute();
			
			ArrayList temp2 = new ArrayList();
			for(int i=0; i<rows; i++)
				temp2.add(db.getString(i,0));
			
			
			ArrayList nonAttendees=new ArrayList(); //left side box in screen
			ArrayList selectedAttendees=new ArrayList(); //right side box in screen
			for(int j=0; j<clientPeople.size(); j++)
			{
				boolean isAttendee = false;
				for(int i=0; i<rows;i++)	
					if(((SelectControl)clientPeople.get(j)).getValue().equals(temp2.get(i)))isAttendee=true;
				if(!isAttendee)
					nonAttendees.add(clientPeople.get(j));
				else
					selectedAttendees.add(clientPeople.get(j));
			}
			vo.setAllAttendees(nonAttendees);
			vo.setAllSelectedAttendees(selectedAttendees);
			
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
		
		return vo;
	}
	private String[] arr(String str)
	{
		return new String[]{str};
	}


	public void updateWorkflowObject(WorkflowObject workflowObject) {
		
		if(!(workflowObject instanceof InitializeVO))
			return;

		InitializeVO vo = (InitializeVO)workflowObject;
		
		DBAccess db = null;
		try{
			db = new DBAccess();
			
			updateMeeting(vo,db);
			updateIdentifyStage(vo,db);
			updateClientAttendees(vo,db);
			updateIBMAttendees(vo,db);
			updateWorkflowDef(vo,db);
			setIdentifyStageId(vo.getWorkflowID(),db);
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
		return;
	}
	private void updateMeeting(InitializeVO vo, DBAccess db) throws Exception{
		//TODO: update invitees
		Timestamp ts = new Timestamp(Integer.parseInt(vo.getPlannedYear()[0])-1900,Integer.parseInt(vo.getPlannedMonth()[0])-1,Integer.parseInt(vo.getPlannedDay()[0]),9,0,0,0);
		String meetingID = null;
		
		String query = "select meeting_id from ets.wf_def where wf_id='"+vo.getWorkflowID()+"' with ur";
		db.prepareDirectQuery(query);
		if(db.execute()==1)meetingID = db.getString(0,0);
		
		query = "update ets.ets_calendar set start_time=" +
												"'"+ts.toString()+"' " +
												"where calendar_id=" +
												"'"+meetingID+"'";
		db.prepareDirectQuery(query);
		db.execute();
	}

	private void updateWorkflowDef(InitializeVO vo, DBAccess db) throws Exception{
		
		String query = "update ets.wf_def set " +
								"acct_contact=" +"'"+vo.getAccountContact()[0]+"',"+
								"backup_acct_contact=" +"'"+vo.getBackupContact()[0]+"',"+
								"quarter=" +""+vo.getQbrQuarter()[0]+","+
								"year=" +"'"+vo.getQbrYear()[0]+"',"+
								"last_userid="+"'"+vo.getLoggedUser()+"',"+
								"last_timestamp=current timestamp where wf_id='"+vo.getWorkflowID()+"'" ;
		
		db.prepareDirectQuery(query);
		db.execute();
	}
	private void updateIdentifyStage(InitializeVO vo, DBAccess db) throws Exception{
		String query = "update ets.wf_stage_identify_setmet set " +
							//"exec_sponsor='" + vo.getExecSponsor()[0]+"', "+
							"location='" +vo.getMeetingLocation()+"',"+
							//"biweekly_flag='" +vo.getBiweeklyFlag()[0]+"',"+
							//"biweekly_date='"+vo.getBiweeklyYear()[0]+"-"+vo.getBiweeklyMonth()[0]+"-"+vo.getBiweeklyDay()[0]+"',"+
							//"rating_period_from='"+vo.getRatingFromYear()[0]+"-"+vo.getRatingFromMonth()[0]+"-"+vo.getRatingFromDay()[0]+"',"+
							//"rating_period_to='"+vo.getRatingToYear()[0]+"-"+vo.getRatingToMonth()[0]+"-"+vo.getRatingToDay()[0]+"',"+
							//"biweekly_status='" +vo.getBiweeklyStatus()[0]+"',"+
							"nsi_rating=" +vo.getNsiRating()[0]+
							" where wf_id='"+vo.getWorkflowID()+"'";
		db.prepareDirectQuery(query);
		db.execute();
	}

	private void updateIBMAttendees(InitializeVO vo, DBAccess db) throws Exception {
		String query = "delete from ets.wf_setmet_attendees_ibm where wf_id='"+vo.getWorkflowID()+"'";
		db.prepareDirectQuery(query);
		db.execute();
		addIBMAttendees(vo,db);
	}

	private void updateClientAttendees(InitializeVO vo, DBAccess db) throws Exception{
		String query = "delete from ets.wf_setmet_attendees_client where wf_id='"+vo.getWorkflowID()+"'";
		db.prepareDirectQuery(query);
		db.execute();
		addClientAttendees(vo,db);
	}
	
	public WorkflowObject getWorkflowObject(String ID) {
		return null;
	}
	public boolean saveWorkflowObjectList(ArrayList object) {
		return false;
	}

	public ArrayList getWorkflowObjectList(String ID) {
		return null;
	}

	private void setIdentifyStageId(String workflowID, DBAccess db) throws Exception
	{
		String query = "select wf_stage_id from ets.wf_stage_identify_setmet where wf_id='"+workflowID+"' with ur";
		db.prepareDirectQuery(query);
		if(db.execute()==1)
			identifyStageID = db.getString(0,0);
	}
	/**
	 * This is used to determine which earlier workflow will be used for copying
	 */
	public String[] getPreviousWorkflow(String projectID) {
		String[] returnValue = null;
		DBAccess db = null;
		try{
			db = new DBAccess();
			String query = "select wf_id, wf_name from ets.wf_def where project_id='"+projectID+"' and wf_type='SELF ASSESSMENT' order by wf_id desc with ur";
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

