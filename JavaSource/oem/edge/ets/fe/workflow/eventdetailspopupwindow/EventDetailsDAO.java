/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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

package oem.edge.ets.fe.workflow.eventdetailspopupwindow;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.DetailsUtils;

import org.apache.commons.logging.Log;

/**
 * Class       : EventDetailsDAO
 * Package     : oem.edge.ets.fe.workflow.eventdetailspopupwindow
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class EventDetailsDAO extends AbstractDAO {

	private static Log logger = WorkflowLogger.getLogger(EventDetailsDAO.class);
	
	public boolean saveWorkflowObject(WorkflowObject workflowObject){return false;}
	public boolean saveWorkflowObject(WorkflowObject workflowObject, String loggedUser) {
		WorkflowEventDetailsVO vo = (WorkflowEventDetailsVO)workflowObject;
		
		//////////////////
			
		String title = vo.getTitle();
		String description = vo.getDesc();
		String month = vo.getMonth()[0];
		String day = vo.getDay()[0];
		String year = vo.getYear()[0];
		String minute = vo.getMin()[0];
		String hour = vo.getHour()[0];
		String ampm = vo.getAmpm()[0];
		String repeatsFor = vo.getRepeatsFor()[0];
		String rlist = "";
		if (vo.getTeamMembers() == null) {
			System.out.println("Nobody was selected.");

		} else {
			for (int i = 0; i < vo.getTeamMembers().length; i++) {
				if (rlist == "") {
					rlist = vo.getTeamMembers()[i];
				} else {
					rlist += "," + vo.getTeamMembers()[i];
				}
				System.out.println("........." + vo.getTeamMembers()[i]);
			}
		}
		boolean sendEmail = false;
		if (vo.getNotifyEmail() != null) {
			if (vo.getNotifyEmail().trim().equals("on")) {
				sendEmail = true;
			} else {
				System.out.println("no emails to be sent");
			}
		} else {
			System.out.println("no emails to be sent");
		}
		boolean bccEmail = true;
		if (vo.getEmailOption() == null) {
			System.out
					.println(".....None of the email notification options were selected.");
		} else {
			System.out.println(".....Email notification option = "
					+ vo.getEmailOption());
			if (vo.getEmailOption().equals("to"))
				bccEmail = false;
		}

	
		/////////////////////////////////DATABASE THINGS////////////////////////

		DBAccess db = null;
		try {
			System.out.println("..........Creating new DBAccess object");
			db=new DBAccess();
			System.out.println("..........Created DBAccess object.");
			String project_id = "'"+vo.getProjectID()+"'";
			calendarID = ETSCalendar.getNewCalendarId();
			String calendar_id = "'" + calendarID + "'";
			
			String calendar_type = "'"+"E"+"'" ;
			Timestamp ts = new Timestamp(System.currentTimeMillis());

			String schedule_date = "'" + ts.toString()+ "'";
			String scheduled_by ="'"+ loggedUser + "'";
			
			int hour24 = 0;
			if(ampm.equalsIgnoreCase("am"))
				hour24=Integer.parseInt(hour);
			else
				hour24 = Integer.parseInt(hour)+12;
			if(hour24==24)hour24=0;
			
			ts = new Timestamp(Integer.parseInt(year)-1900,Integer.parseInt(month)-1,Integer.parseInt(day),hour24,Integer.parseInt(minute),0,0);
			String start_time = "'"+ts.toString()+"'" ;
			String subject="'" + title+  "'";
			String invitees_id="'"+rlist+"'";
			String email_flag=null;
			if(sendEmail)
				email_flag ="'"+ "Y"+"'";
			else
				email_flag ="'"+ "N"+"'";
			String repeat_type = "'"+"D"+"'";
			String repeat_id = "'"+"0"+"'";;
			//String repeat_duration = "'1'";
			String repeat_start = "'"+ts.toString()+"'" ;

			Calendar c = Calendar.getInstance();
			c.set(Integer.parseInt(year),Integer.parseInt(month)-1,Integer.parseInt(day),hour24,Integer.parseInt(minute));
			c.add(Calendar.DATE,Integer.parseInt(repeatsFor));
			
			ts = new Timestamp(c.getTimeInMillis());
			String repeat_end = "'"+ts.toString()+"'" ;;
			String notify_type=null;
			
			if(bccEmail)
				notify_type = "'" + "B"+"'";
			else
				notify_type = "'" + "T"+"'";

			String q = "INSERT INTO ETS.ETS_CALENDAR (" +
					"CALENDAR_ID," +
					"PROJECT_ID," +
					"CALENDAR_TYPE," +
					"SCHEDULE_DATE," +
					"SCHEDULED_BY," +
					"START_TIME," +
					"DURATION," +
					"SUBJECT," +
					"DESCRIPTION," +
					"INVITEES_ID," +
					"EMAIL_FLAG," +
					"REPEAT_TYPE," +
					"REPEAT_ID," +
					"REPEAT_START," +
					"REPEAT_END," +
					"NOTIFY_TYPE," +
					"WF_ID)" +
					" " +
					"VALUES (" +
					calendar_id +
					"," +
					project_id +
					"," +
					calendar_type +
					"," +
					schedule_date +
					"," +
					scheduled_by +
					"," +
					start_time +
					"," +
					Integer.parseInt(repeatsFor) +
					//"1" +
					"," +
					 subject+
					"," +
					"'"+description+"'"+
					"," +
					invitees_id +
					"," +
					 email_flag+
					"," +
					 repeat_type+
					"," +
					repeat_id +
					"," +
					 repeat_start+
					"," +
					 repeat_end+
					"," +
					notify_type +
					",'" +
					vo.getWorkflowID() +
					"')";
			db.prepareDirectQuery(q);
			System.out.println(q);
			System.out.println(".....Waiting for database to insert the new event.");
			db.execute();
			db.doCommit();
			System.out.println("....Database finished inserting.");
			db.close();
			db=null;
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
					
				}catch(Exception ex){
					
				}
			}
		}
		System.out.println(".....Reurning from DAO");

		///////////////////////////////DATABASE THINGS END//////////////////////
		
		return true;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObject(java.lang.String)
	 */
	public WorkflowObject getWorkflowObject(String ID) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObjectList(java.util.ArrayList)
	 */
	public boolean saveWorkflowObjectList(ArrayList object) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObjectList(java.lang.String)
	 */
	public ArrayList getWorkflowObjectList(String ID) {
		// TODO Auto-generated method stub
		return null;
	}
	String calendarID = null;
	/**
	 * @return
	 */
	public String getCalendarID() {
		return calendarID;
	}

}
