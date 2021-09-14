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


package oem.edge.ets.fe.workflow.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.dao.DBuffer;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;


/**
 * Class       : DetailsUtils
 * Package     : oem.edge.ets.fe.workflow.util
 * Description : 
 * Date		   : Oct 23, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class DetailsUtils {
	private static Log logger = WorkflowLogger.getLogger(DetailsUtils.class);
	
	private String projectID = null;
	private String company = null;
	private String projectName = null;
	
	private String workflowID = null;
	private String issueID = null;
	private String meetingID = null;
	
	private String wwf_type = null;
	private String wwf_name = null;
	private String wws_doc_id = null;
	private String wrequestor = null;
	private String wacct_contact = null;
	private String wbackup_acct_contact = null;
	private String wquarter = null;
	private String wyear = null;
	private String wmeeting_id = null;
	private String wwf_curr_stage_name = null;
	private String wcreation_date = null;
	private String wlast_userid = null;
	private String wlast_timestamp = null;
	
	private String iissue_desc = null;
	private String iissue_title = null;
	private String iissue_id_display = null;
	private String iissue_contact= null;
	private String istatus = null;
	private String iissue_type = null;
	private String iinitial_target_date = null;
	private String itarget_date = null;
	private String isubmit_date = null;
	private String iissue_accept_date = null;
	private String iclose_date = null;
	private String ilast_userid = null;
	private String ilast_timestamp = null;
	private ArrayList iownerNames = new ArrayList();
	private ArrayList iownerIds = new ArrayList();
	private ArrayList iownerStates = new ArrayList();
	/* Added in 7.1.1 */
	private String iissue_category = null;
	
	private String mschedule_date = null;	
	
	private String ccalendarID = null;
	
	
	
	private Connection conn= null;

	private String cschedule_date;
	private String cscheduled_by;
	private String cstart_time;
	private String cduration;
	private String csubject;
	private String cdescription;
	private String cinvitees_id;
	private String ccc_list;
	private String cemail_flag;
	private String ccrepeat_type;
	private String cnotify_type;

	
	public void setWorkflowID(String workflowID)
	{
		this.workflowID = workflowID;
	}
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	

	public void extractWorkflowDetails(Connection conn)
	{
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
			ps =  conn.prepareStatement("select * from ets.wf_def where project_id = '"+projectID+"' and wf_id='"+workflowID+"' with ur");
			rs = ps.executeQuery();
			DBuffer db = new DBuffer();
			db.addDBColumns(rs);
			int rows = db.getRows();
			
			System.out.println("CONN SAYS rows="+rows);
			if(rows == 1)
			{
				wwf_type = db.getString(0,"wf_type");
				wwf_name = db.getString(0,"wf_name");
				wws_doc_id = db.getString(0,"ws_doc_id");
				wrequestor = db.getString(0,"requestor");
				wacct_contact = db.getString(0,"acct_contact");
				wbackup_acct_contact = db.getString(0,"backup_acct_contact");
				wquarter = db.getString(0,"quarter");
				wyear = db.getString(0,"year");
				wmeeting_id = db.getString(0,"meeting_id");
				wwf_curr_stage_name = db.getString(0,"wf_curr_stage_name");
				wcreation_date = db.getString(0,"creation_date");
				wlast_userid = db.getString(0,"last_userid");
				/*if(db.getObject(0,"last_timestamp")!=null)
					wlast_timestamp = db.getString(0,"last_timestamp");*/
			}
			rs.close();
			ps.close();
			//conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
				try{
				if(rs != null)
					rs.close();
				if(ps != null)
					ps.close();
				}catch(Exception x){x.printStackTrace();}
		}
		System.out.println("return from extractWorkflowDetails");
	}
	public String getWFPrevActiveStageName(String wf_id) throws Exception {
		DBAccess db = null;
		Connection c = null;
		PreparedStatement p = null;
		ResultSet r = null;
		String prev_stage = "";
		try{
			db = new DBAccess();
			c = db.getConnection();
			
			StringBuffer sb = new StringBuffer();
			sb.append("select distinct new_value, last_timestamp from ets.wf_history a, ets.wf_history_field b ");
			sb.append("where a.action = 'Set/Met Modified' and b.field_changed in ('Current Stage','Workflow Stage') ");
			sb.append("and a.wf_history_id = b.wf_history_id ");
			sb.append("and a.wf_id = '" + wf_id + "' ");
			sb.append("and new_value not in ('Closed','Cancelled', 'Complete') ");
			sb.append("order by last_timestamp desc with ur");
			logger.debug("Prev Stage From History:" + sb.toString());
			System.out.println(sb.toString());
			p = c.prepareStatement(sb.toString());
			r = p.executeQuery();
			
			if (r.next())
			{
				prev_stage = r.getString("new_value");
				if(prev_stage == null)
					prev_stage = "";
			}
		} finally {
			try {
				if(r != null)
					r.close();
				if(p != null)
					p.close();
				db.doCommit();
				db.close();
				db = null;
			} catch (Exception ex) {}
		}
		return prev_stage;
	}

	public String getWFProjectDetails() {
		DBAccess db = null;
		Connection c = null;
		PreparedStatement p = null;
		ResultSet r = null;
		String proj_id = "";
		String company = "";
		String proj_name = "";
		try{
			db = new DBAccess();
			c = db.getConnection();
			
			StringBuffer sb = new StringBuffer();
			sb.append("select company, project_name from ets.ets_projects ");
			sb.append("where project_id = '" + getProjectID() + "' with ur");
			
			logger.debug("WF Project Details:" + sb.toString());
			System.out.println(sb.toString());
			p = c.prepareStatement(sb.toString());
			r = p.executeQuery();
			
			if (r.next())
			{				
				company = r.getString("company");
				if (company == null)
					company = "";
				else
					company = company.trim();
				//
				setCompany(company);
				
				proj_name = r.getString("project_name");
				if (proj_name == null)
					proj_name = "";
				else
					proj_name = proj_name.trim();
				setProjectName(proj_name);
			}
		} catch (Exception e){
			System.out.println("Exception in getWFProjectDetails()");
			e.printStackTrace();
		}		
		finally {
			try {
				if(r != null)
					r.close();
				if(p != null)
					p.close();
				db.doCommit();
				db.close();
				db = null;
			} catch (Exception ex) {}
		}
		return proj_id;
	}
	
	public void extractWorkflowDetails()
	{
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery("select * from ets.wf_def where project_id = '"+projectID+"' and wf_id='"+workflowID+"' with ur");
			int rows = db.execute();
			
			
			System.out.println("CONN SAYS rows="+rows);
			if(rows == 1)
			{
				wwf_type = db.getString(0,"wf_type");
				wwf_name = db.getString(0,"wf_name");
				wws_doc_id = db.getString(0,"ws_doc_id");
				wrequestor = db.getString(0,"requestor");
				wacct_contact = db.getString(0,"acct_contact");
				wbackup_acct_contact = db.getString(0,"backup_acct_contact");
				wquarter = db.getString(0,"quarter");
				wyear = db.getString(0,"year");
				wmeeting_id = db.getString(0,"meeting_id");
				wwf_curr_stage_name = db.getString(0,"wf_curr_stage_name");
				wcreation_date = db.getString(0,"creation_date");
				wlast_userid = db.getString(0,"last_userid");
				/*if(db.getObject(0,"last_timestamp")!=null)
					wlast_timestamp = db.getString(0,"last_timestamp");*/
			}
			db.close();
			db = null;
		} catch (Exception e) {
			//db.doRollback();
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
	
	}
	
	public void extractCalendarDetails(DBAccess db)
	{
		try{
		db.prepareDirectQuery("select schedule_date, scheduled_by,start_time,duration,subject,description,invitees_id,cc_list,email_flag,repeat_type,notify_type from ets.ets_calendar where calendar_id ='"+ccalendarID+"'");
		int rows = db.execute();
			if(rows==1)
			{
				cschedule_date =db.getObject(0,0).toString();
				cscheduled_by = db.getString(0,1);
				cstart_time = db.getObject(0,2).toString();
				cduration = db.getString(0,3);
				csubject = db.getString(0,4);
				cdescription = db.getString(0,5);
				cinvitees_id = db.getString(0,6);
				ccc_list = db.getString(0,7);
				cemail_flag  =db.getString(0,8);
				ccrepeat_type =db.getString(0,9);
				cnotify_type=db.getString(0,10);
				//workflowID=db.getString(0,11);
			}
		}catch(Exception e)
		{
			System.out.println(e);
		}
	}
	public void extractIssueDetails()
	{
		DBAccess db = null;
		
		try{
			db = new DBAccess();
			db.prepareDirectQuery("select * from ets.wf_issue where issue_id='"+issueID+"' with ur");
			int rows = db.execute();
			
			if(rows == 1)
			{
				 iissue_desc = db.getString(0,"issue_desc");
				 iissue_title = db.getString(0,"issue_title");
				 iissue_id_display = db.getString(0,"issue_id_display");
				 iissue_contact= db.getString(0,"issue_contact");
				 istatus = db.getString(0,"status");
				 iissue_type = db.getString(0,"issue_type");
				 iissue_category = db.getString(0,"issue_category");
				 
				 Object temp = null;
				 
				 temp = db.getObject(0,"initial_target_date");
				 System.out.println("temp of type: "+temp.getClass());
				 if(temp!=null)
				 	iinitial_target_date = (String)temp;
				 
				 temp = db.getObject(0,"target_date");
				 if(temp!=null)
				 	 itarget_date = (String)temp;
				 
				 temp = db.getObject(0,"submit_date");
				 if(temp!=null)
				 	isubmit_date = (String)temp;
				 
				 temp = db.getObject(0,"issue_accept_date");
				 if(temp!=null)
				 	 iissue_accept_date = (String)temp;
				 
				 temp = db.getObject(0,"close_date");
				 if(temp!=null)
				 	iclose_date = (String)temp;
				 
				 ilast_userid = db.getString(0,"last_userid");
				 //ilast_timestamp = db.getString(0,"last_timestamp");
				 iownerNames = new ArrayList();
				db.prepareDirectQuery("select owner_id,ownership_state from ets.wf_issue_owner where issue_id = '"+issueID+"' with ur");
				rows = db.execute();
				ArrayList temp2 = new ArrayList();
				for(int i = 0; i< rows; i++)
				{
					if(db.getString(i,0)!=null)
						{temp2.add(db.getString(i,0));
						if(db.getObject(i,1)==null)
							iownerStates.add("ASSIGNED");
						else
						     iownerStates.add(db.getString(i,1));
						 }
				}
				for(int i = 0; i< temp2.size(); i++)
				{
					ETSUserDetails u = new ETSUserDetails();
					u.setWebId((String)temp2.get(i));
					u.extractUserDetails(db.getConnection());
					iownerNames.add(u.getFirstName()+" "+u.getLastName());
					iownerIds.add(temp2.get(i));
					
				}	
				
			}
			db.close();
			db = null;
		} catch (Exception e) {
			db.doRollback();
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
		
	}

	

	public void extractIssueDetails(Connection conn)
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps =  conn.prepareStatement("select * from ets.wf_issue where issue_id='"+issueID+"' with ur");
			rs = ps.executeQuery();
			DBuffer db = new DBuffer();
			db.addDBColumns(rs);
			int rows = db.getRows();
	
			if(rows == 1)
			{
				 iissue_desc = db.getString(0,"issue_desc");
				 iissue_title = db.getString(0,"issue_title");
				 iissue_id_display = db.getString(0,"issue_id_display");
				 iissue_contact= db.getString(0,"issue_contact");
				 istatus = db.getString(0,"status");
				 iissue_type = db.getString(0,"issue_type");
				 iissue_category = db.getString(0,"issue_category");
				 
				 Object temp = null;
				 
				 temp = db.getObject(0,"initial_target_date");
				 System.out.println("temp of type: "+temp.getClass());
				 if(temp!=null)
				 	iinitial_target_date = (String)temp;
				 
				 temp = db.getObject(0,"target_date");
				 if(temp!=null)
				 	 itarget_date = (String)temp;
				 
				 temp = db.getObject(0,"submit_date");
				 if(temp!=null)
				 	isubmit_date = (String)temp;
				 
				 temp = db.getObject(0,"issue_accept_date");
				 if(temp!=null)
				 	 iissue_accept_date = (String)temp;
				 
				 temp = db.getObject(0,"close_date");
				 if(temp!=null)
				 	iclose_date = (String)temp;
				 
				 ilast_userid = db.getString(0,"last_userid");
				 //ilast_timestamp = db.getString(0,"last_timestamp");
				 iownerNames = new ArrayList();
				 
				 ps =  conn.prepareStatement("select owner_id, ownership_state from ets.wf_issue_owner where issue_id = '"+issueID+"' with ur");
				 rs = ps.executeQuery();
				 db = new DBuffer();
				 db.addDBColumns(rs);
				 rows = db.getRows();
			System.out.println("Number of rows returned is "+rows);
			System.out.println("select owner_id,ownership_state from ets.wf_issue_owner where issue_id = '"+issueID+"' with ur");

			ArrayList temp2 = new ArrayList();
			for(int i = 0; i< rows; i++)
			{
				System.out.println("STATE is : "+db.getString(i,1));
				if(db.getString(i,0)!=null)
					{temp2.add(db.getString(i,0));
					System.out.println(db.getString(i,0)+":"+db.getString(i,1));
					if(db.getObject(i,1)==null)
						iownerStates.add("ASSIGNED");
					else
					     iownerStates.add(db.getString(i,1));
					}
				System.out.println("owner is----:"+db.getString(i,0));
			}
			for(int i = 0; i< temp2.size(); i++)
				{
					ETSUserDetails u = new ETSUserDetails();
					u.setWebId((String)temp2.get(i));
					u.extractUserDetails(conn);
					iownerNames.add(u.getFirstName()+" "+u.getLastName());
					iownerIds.add(temp2.get(i));
					
				}
				//conn.close();

			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			try{
			if(rs != null)
				rs.close();
			if(ps != null)
				ps.close();
			}catch(Exception x){x.printStackTrace();}
		}
	}

	
	public void extractWFIdANDProjFromIID(String issueID)
	{
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery("select * from ets.wf_issue_wf_map where issue_id='"+issueID+"' with ur");
			int rows = db.execute();
			
			if(rows == 1)
			{
				 
				 workflowID= db.getString(0,"WF_ID");
				 projectID= db.getString(0,"PROJECT_ID");
				
				
			}
			db.close();
			db = null;
		} catch (Exception e) {
			db.doRollback();
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
		
	}

	public void extractWFIdANDProjFromIID(String issueID, Connection conn)
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps =  conn.prepareStatement("select * from ets.wf_issue_wf_map where issue_id='"+issueID+"' with ur");
			rs = ps.executeQuery();
			DBuffer db = new DBuffer();
			db.addDBColumns(rs);
			int rows = db.getRows();
			
			if(rows == 1)
			{
				 
				 workflowID= db.getString(0,"WF_ID");
				 projectID= db.getString(0,"PROJECT_ID");
				
				
			}
			//conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{
			if(rs != null)
				rs.close();
			if(ps != null)
				ps.close();
			}catch(Exception x){x.printStackTrace();}
		}		
	}

	
	public void extractMeetingDetails()
	{   
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery("select * from ets.ets_calendar where calendar_id='"+meetingID+"' with ur");
			int rows = db.execute();
			
			if(rows == 1)
			{
				//mschedule_date =  ((Date)db.getObject(0,"schedule_date")).toString();
				//mschedule_date =  ((Date)db.getObject(0,"START_TIME")).toString().substring(0,10); 
				String mschedule_date1 =  ((Date)db.getObject(0,"START_TIME")).toString().substring(0,10);
				mschedule_date = mschedule_date1.substring(5,7)+"-"+mschedule_date1.substring(8,10)+"-"+mschedule_date1.substring(2,4);

				
			}
			db.close();
			db = null;
		} catch (Exception e) {
			db.doRollback();
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
		

		
	}
	
	public void extractAllDetails()
	{
		extractWorkflowDetails();
		extractIssueDetails();
		extractMeetingDetails();
	}
	private void extractAllDetails(Connection conn)
	{
		//unimplemented
	}
	
	
	/**
	 * @return Returns the iclose_date.
	 */
	public String getIclose_date() {
		return iclose_date;
	}
	/**
	 * @return Returns the iinitial_target_date.
	 */
	public String getIinitial_target_date() {
		return iinitial_target_date;
	}
	/**
	 * @return Returns the iissue_accept_date.
	 */
	public String getIissue_accept_date() {
		return iissue_accept_date;
	}
	/**
	 * @return Returns the iissue_contact.
	 */
	public String getIissue_contact() {
		return iissue_contact;
	}
	/**
	 * @return Returns the iissue_desc.
	 */
	public String getIissue_desc() {
		return iissue_desc;
	}
	/**
	 * @return Returns the iissue_title.
	 */
	public String getIissue_title() {
		return iissue_title;
	}
	/**
	 * @return Returns the iissue_id_display.
	 */
	public String getIissue_id_display() {
		return iissue_id_display;
	}
	/**
	 * @return Returns the iissue_type.
	 */
	public String getIissue_type() {
		return iissue_type;
	}
	/**
	 * @return Returns the ilast_timestamp.
	 */
	public String getIlast_timestamp() {
		return ilast_timestamp;
	}
	/**
	 * @return Returns the ilast_userid.
	 */
	public String getIlast_userid() {
		return ilast_userid;
	}
	/**
	 * @return Returns the istatus.
	 */
	public String getIstatus() {
		return istatus;
	}
	/**
	 * @return Returns the isubmit_date.
	 */
	public String getIsubmit_date() {
		return isubmit_date;
	}
	/**
	 * @return Returns the itarget_date.
	 */
	public String getItarget_date() {
		return itarget_date;
	}
	/**
	 * @return Returns the wacct_contact.
	 */
	public String getWacct_contact() {
		return wacct_contact;
	}
	/**
	 * @return Returns the wbackup_acct_contact.
	 */
	public String getWbackup_acct_contact() {
		return wbackup_acct_contact;
	}
	/**
	 * @return Returns the wcreation_date.
	 */
	public String getWcreation_date() {
		return wcreation_date;
	}
	/**
	 * @return Returns the wlast_timestamp.
	 */
	public String getWlast_timestamp() {
		return wlast_timestamp;
	}
	/**
	 * @return Returns the wlast_userid.
	 */
	public String getWlast_userid() {
		return wlast_userid;
	}
	/**
	 * @return Returns the wmeeting_id.
	 */
	public String getWmeeting_id() {
		return wmeeting_id;
	}
	/**
	 * @return Returns the wquarter.
	 */
	public String getWquarter() {
		return wquarter;
	}
	/**
	 * @return Returns the wrequestor.
	 */
	public String getWrequestor() {
		return wrequestor;
	}
	/**
	 * @return Returns the wwf_curr_stage_name.
	 */
	public String getWwf_curr_stage_name() {
		return wwf_curr_stage_name;
	}
	/**
	 * @return Returns the wwf_name.
	 */
	public String getWwf_name() {
		return wwf_name;
	}
	/**
	 * @return Returns the wwf_type.
	 */
	public String getWwf_type() {
		return wwf_type;
	}
	/**
	 * @return Returns the wws_doc_id.
	 */
	public String getWws_doc_id() {
		return wws_doc_id;
	}
	/**
	 * @return Returns the wyear.
	 */
	public String getWyear() {
		return wyear;
	}
	
	
	/**
	 * @return Returns the mschedule_date.
	 */
	public String getMschedule_date() {
		return mschedule_date;
	}
	
	/**
	 * @param meetingID The meetingID to set.
	 */
	public void setMeetingID(String meetingID) {
		this.meetingID = meetingID;
	}
	/**
	 * @return Returns the iownerNames.
	 */
	public ArrayList getIownerNames() {
		return iownerNames;
	}
	/**
	 * @return Returns the projectID.
	 */
	public String getProjectID() {
		return projectID;
	}
	/**
	 * @return Returns the workflowID.
	 */
	public String getWorkflowID() {
		return workflowID;
	}
	
	/**
	 * @return Returns the iownerIds.
	 */
	public ArrayList getIownerIds() {
		return iownerIds;
	}
	
	/**
	 * @return Returns the iownerStates.
	 */
	public ArrayList getIownerStates() {
		return iownerStates;
	}
	/**
	 * @return Returns the ccalendarID.
	 */
	public String getCcalendarID() {
		return ccalendarID;
	}
	/**
	 * @param ccalendarID The ccalendarID to set.
	 */
	public void setCcalendarID(String ccalendarID) {
		this.ccalendarID = ccalendarID;
	}
	/**
	 * @return Returns the ccc_list.
	 */
	public String getCcc_list() {
		return ccc_list;
	}
	/**
	 * @param ccc_list The ccc_list to set.
	 */
	public void setCcc_list(String ccc_list) {
		this.ccc_list = ccc_list;
	}
	/**
	 * @return Returns the ccrepeat_type.
	 */
	public String getCcrepeat_type() {
		return ccrepeat_type;
	}
	/**
	 * @param ccrepeat_type The ccrepeat_type to set.
	 */
	public void setCcrepeat_type(String ccrepeat_type) {
		this.ccrepeat_type = ccrepeat_type;
	}
	/**
	 * @return Returns the cdescription.
	 */
	public String getCdescription() {
		return cdescription;
	}
	/**
	 * @param cdescription The cdescription to set.
	 */
	public void setCdescription(String cdescription) {
		this.cdescription = cdescription;
	}
	/**
	 * @return Returns the cduration.
	 */
	public String getCduration() {
		return cduration;
	}
	/**
	 * @param cduration The cduration to set.
	 */
	public void setCduration(String cduration) {
		this.cduration = cduration;
	}
	/**
	 * @return Returns the cemail_flag.
	 */
	public String getCemail_flag() {
		return cemail_flag;
	}
	/**
	 * @param cemail_flag The cemail_flag to set.
	 */
	public void setCemail_flag(String cemail_flag) {
		this.cemail_flag = cemail_flag;
	}
	/**
	 * @return Returns the cinvitees_id.
	 */
	public String getCinvitees_id() {
		return cinvitees_id;
	}
	/**
	 * @param cinvitees_id The cinvitees_id to set.
	 */
	public void setCinvitees_id(String cinvitees_id) {
		this.cinvitees_id = cinvitees_id;
	}
	/**
	 * @return Returns the cnotify_type.
	 */
	public String getCnotify_type() {
		return cnotify_type;
	}
	/**
	 * @param cnotify_type The cnotify_type to set.
	 */
	public void setCnotify_type(String cnotify_type) {
		this.cnotify_type = cnotify_type;
	}
	/**
	 * @return Returns the cschedule_date.
	 */
	public String getCschedule_date() {
		return cschedule_date;
	}
	/**
	 * @param cschedule_date The cschedule_date to set.
	 */
	public void setCschedule_date(String cschedule_date) {
		this.cschedule_date = cschedule_date;
	}
	/**
	 * @return Returns the cscheduled_by.
	 */
	public String getCscheduled_by() {
		return cscheduled_by;
	}
	/**
	 * @param cscheduled_by The cscheduled_by to set.
	 */
	public void setCscheduled_by(String cscheduled_by) {
		this.cscheduled_by = cscheduled_by;
	}
	/**
	 * @return Returns the cstart_time.
	 */
	public String getCstart_time() {
		return cstart_time;
	}
	/**
	 * @param cstart_time The cstart_time to set.
	 */
	public void setCstart_time(String cstart_time) {
		this.cstart_time = cstart_time;
	}
	/**
	 * @return Returns the csubject.
	 */
	public String getCsubject() {
		return csubject;
	}
	/**
	 * @param csubject The csubject to set.
	 */
	public void setCsubject(String csubject) {
		this.csubject = csubject;
	}
	
	public String getIissue_category() {
		return iissue_category;
	}
	public void setIissue_category(String iissue_category) {
		this.iissue_category = iissue_category;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}

