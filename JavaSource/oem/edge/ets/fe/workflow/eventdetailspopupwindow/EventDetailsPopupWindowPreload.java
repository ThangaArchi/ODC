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
import java.util.Collection;

import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.SelectControl;

import org.apache.commons.logging.Log;



/**
 * Class       : EventDetailsPopupWindowPreload
 * Package     : oem.edge.ets.fe.workflow.eventdetailspopupwindow
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class EventDetailsPopupWindowPreload extends WorkflowObject {

	private static Log logger = WorkflowLogger.getLogger(EventDetailsPopupWindowPreload.class);
	private Collection months = new ArrayList();
	private Collection days = new ArrayList();
	private Collection years = new ArrayList();
	private Collection hours = new ArrayList();
	private Collection minutes = new ArrayList();
	private Collection repeatsForDays = new ArrayList();
	private Collection appliesToMembers = new ArrayList();
	
	
	/**
	 * 
	 */
	public EventDetailsPopupWindowPreload(String project_id, WorkflowEventDetailsVO vo, String extend) {


		Timestamp ts = new Timestamp(System.currentTimeMillis());
		 
		if(extend==null && vo!=null)
		{
			String[] temp = {""+(ts.getYear()+1900)};
			vo.setYear(temp);
			temp = new String[]{""+(ts.getMonth()+1)};
			vo.setMonth(temp);
			temp = new String[]{""+(ts.getDate())};
			vo.setDay(temp);
		}
			
		months.add(new SelectControl("Jan","1"));
		months.add(new SelectControl("Feb","2"));
		months.add(new SelectControl("Mar","3"));
		months.add(new SelectControl("Apr","4"));
		months.add(new SelectControl("May","5"));
		months.add(new SelectControl("Jun","6"));
		months.add(new SelectControl("Jul","7"));
		months.add(new SelectControl("Aug","8"));
		months.add(new SelectControl("Sep","9"));
		months.add(new SelectControl("Oct","10"));
		months.add(new SelectControl("Nov","11"));
		months.add(new SelectControl("Dec","12"));
		
		
		for(int i = 1; i<32; i++)
			days.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		
		for(int i = 2006; i<2013; i++)
			years.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		
		for(int i = 0; i<13; i++)
			hours.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		
		for(int i = 0; i<60; i++)
			minutes.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		
		for(int i = 1; i<20; i++)
			repeatsForDays.add(new SelectControl(Integer.toString(i),Integer.toString(i)));
		
		///////////////////////
		DBAccess db = null;
		try {
			db = new DBAccess();
			//db.prepareDirectQuery("SELECT * FROM ETS.WF_CLIENT");
			db.prepareDirectQuery("SELECT ETS_USERS.USER_ID "+
									"FROM ETS.ETS_USERS AS ETS_USERS "+
									"WHERE ETS_USERS.USER_PROJECT_ID = '"+project_id+"' AND ETS_USERS.ACTIVE_FLAG = 'A' with ur");
			System.out.println(".......Waiting for person list from the database");
			int rows = db.execute();
			System.out.println(".......Person list having " + rows
					+ " items recieved from database.");
			//logger.info("Person list having " + rows+ " items recieved from database.");
			
			
			
			for (int i = 0; i < rows; i++) {
				
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId(db.getString(i,"USER_ID"));
				u.extractUserDetails(db.getConnection());
				
				appliesToMembers.add(new SelectControl(u.getFirstName()+" "+u.getLastName(),u.getEMail()));
			}
			db.close();
			db = null;
		} catch (Exception e) {

			e.printStackTrace();
		}finally{
			if(db!=null)
			{
				try {
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				db=null;
			}
		}

		
		//////////////////////
		
		
		
	}
	
	/**
	 * @return Returns the appliesToMembers.
	 */
	public Collection getAppliesToMembers() {
		return appliesToMembers;
	}
	/**
	 * @param appliesToMembers The appliesToMembers to set.
	 */
	public void setAppliesToMembers(Collection appliesToMembers) {
		this.appliesToMembers = appliesToMembers;
	}
	/**
	 * @return Returns the days.
	 */
	public Collection getDays() {
		return days;
	}
	/**
	 * @param days The days to set.
	 */
	public void setDays(Collection days) {
		this.days = days;
	}
	/**
	 * @return Returns the hours.
	 */
	public Collection getHours() {
		return hours;
	}
	/**
	 * @param hours The hours to set.
	 */
	public void setHours(Collection hours) {
		this.hours = hours;
	}
	/**
	 * @return Returns the minutes.
	 */
	public Collection getMinutes() {
		return minutes;
	}
	/**
	 * @param minutes The minutes to set.
	 */
	public void setMinutes(Collection minutes) {
		this.minutes = minutes;
	}
	/**
	 * @return Returns the months.
	 */
	public Collection getMonths() {
		return months;
	}
	/**
	 * @param months The months to set.
	 */
	public void setMonths(Collection months) {
		this.months = months;
	}
	/**
	 * @return Returns the repeatsForDays.
	 */
	public Collection getRepeatsForDays() {
		return repeatsForDays;
	}
	/**
	 * @param repeatsForDays The repeatsForDays to set.
	 */
	public void setRepeatsForDays(Collection repeatsForDays) {
		this.repeatsForDays = repeatsForDays;
	}
	/**
	 * @return Returns the years.
	 */
	public Collection getYears() {
		return years;
	}
	/**
	 * @param years The years to set.
	 */
	public void setYears(Collection years) {
		this.years = years;
	}
}
