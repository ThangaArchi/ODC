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


package oem.edge.ets.fe.workflow.notification;

//TODO: 00 Not yet uploaded in CMVC
/**
 * Class       : EventNotificationParams
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 27, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class EventNotificationParams extends NotificationParams {

	/**
	 * @return Returns the company.
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * @param company The company to set.
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	/**
	 * @return Returns the wf_curr_stage_name.
	 */
	public String getWf_curr_stage_name() {
		return wf_curr_stage_name;
	}
	/**
	 * @param wf_curr_stage_name The wf_curr_stage_name to set.
	 */
	public void setWf_curr_stage_name(String wf_curr_stage_name) {
		this.wf_curr_stage_name = wf_curr_stage_name;
	}
	/**
	 * @return Returns the wf_id.
	 */
	public String getWf_id() {
		return wf_id;
	}
	/**
	 * @param wf_id The wf_id to set.
	 */
	public void setWf_id(String wf_id) {
		this.wf_id = wf_id;
	}
	private String company = "";
	private String wf_id = "";
	private String wf_curr_stage_name = "";
	private String calendarID = null;
	private String msa_next_duedate = "";
	
	/**
	 * @return Returns the calendarID.
	 */
	public String getCalendarID() {
		return calendarID;
	}
	/**
	 * @param calendarID The calendarID to set.
	 */
	public void setCalendarID(String calendarID) {
		this.calendarID = calendarID;
	}
	
	/**
	 * @return Returns the msa_next_duedate.
	 */
	public String getMsa_next_duedate() {
		return msa_next_duedate;
	}
	/**
	 * @param msa_next_duedate The msa_next_duedate to set.
	 */
	public void setMsa_next_duedate(String msa_next_duedate) {
		this.msa_next_duedate = msa_next_duedate;
	}
}

