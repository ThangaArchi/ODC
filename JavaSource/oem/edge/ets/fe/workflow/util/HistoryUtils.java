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

import java.util.ArrayList;
import java.util.Date;

import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.issue.IssueHistoryBean;
import oem.edge.ets.fe.workflow.issue.IssueHistoryField;
import oem.edge.ets.fe.workflow.issue.IssueHistoryItem;
import oem.edge.ets.fe.workflow.issue.edit.EditIssueVO;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.newissue.NewIssueVO;
import oem.edge.ets.fe.workflow.qbr.initialize.InitializeVO;
import oem.edge.ets.fe.workflow.util.idToName.ClientNameFromID;
import oem.edge.ets.fe.workflow.util.idToName.IDToName;
import oem.edge.ets.fe.workflow.util.idToName.IdentityTransformer;
import oem.edge.ets.fe.workflow.util.idToName.UserNameFromID;
import oem.edge.ets.fe.workflow.util.idToName.MonthNameFromID;
import org.apache.commons.logging.Log;

/**
 * Class       : HistoryUtils
 * Package     : oem.edge.ets.fe.workflow.util
 * Description : 
 * Date		   : Nov 1, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class HistoryUtils {
	private static Log logger = WorkflowLogger.getLogger(HistoryUtils.class);
	
	public static final int ACTION_MODIFIED=0;
	public static final int ACTION_CREATE=1;
	public static final int ACTION_COMPLETED=2;
	public static final int ACTION_CANCELLED=3;
	public static final int ACTION_ACCEPTED=4;
	public static final int ACTION_REJECTED=5;
	public static final int ACTION_COMMENTS=6;
	public static final int ACTION_NEW_PREPARE_STAGE=7;
	public static final int ACTION_EDIT_PREPARE_STAGE=8;
	public static final int ACTION_GENERIC_SETMET=9;
	public static String enterHistory(String projectID, String workflowID, String resourceID,int action,String actionBy,String comment, DBAccess db){
		
		String strAction = "";
		if(action==ACTION_MODIFIED)
			strAction = "EDIT";
		if(action==ACTION_CREATE)
			strAction = "CREATE";
		if(action==ACTION_COMPLETED)
			strAction = "COMPLETE";
		if(action==ACTION_CANCELLED)
			strAction = "CANCEL";
		if(action==ACTION_ACCEPTED)
			strAction = "ACCEPT";
		if(action==ACTION_REJECTED)
			strAction = "REJECT";
		if(action==ACTION_COMMENTS)
			strAction = "COMMENT";
		if(action==ACTION_NEW_PREPARE_STAGE)
			strAction = "Set/Met Modified";
		if(action==ACTION_EDIT_PREPARE_STAGE)
			strAction = "Set/Met Modified";
		if(action==ACTION_GENERIC_SETMET)
			strAction = "Set/Met Modified";
		
		Date d = new Date();
		String actionDate = ""+(d.getYear()+1900)+"-"+(d.getMonth()+1)+"-"+d.getDate();
		
		try {
			String historyID = ETSCalendar.getNewCalendarId();
			String q="insert into ets.wf_history (wf_history_id, project_id, wf_id, wf_resource_id, action, action_by, action_date, comment, last_timestamp) values ('"+
			historyID+"','"+
			projectID+"','"+
			workflowID+"','"+
			resourceID+"','"+
			strAction+"','"+
			actionBy+"',DATE('"+
			actionDate+"'),'"+
			comment+"',current timestamp)";
			db.prepareDirectQuery(q);db.execute();
			System.out.println("INSERTING HISTORY: "+q);
			db.doCommit();
			return historyID;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}
		return null;
	}
	public static IssueHistoryBean getHistory(String projectID, String workflowID, String resourceID)
	{
		DBAccess db = null;
		IssueHistoryBean temp = null;
		try{
			db = new DBAccess();
			temp = getHistory(projectID, workflowID, resourceID, db);
			db.close();
			db=null;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(db!=null)
			{
				try{
				db.close();
				}catch(Exception e)
				{
					;
				}
				db=null;
			}
		}
		return temp;
		
	}
	
	public static IssueHistoryBean getHistory(String projectID, String workflowID, String resourceID, DBAccess db)
	{
				
		IssueHistoryBean history = new IssueHistoryBean();
		
		String q = "select action, action_by, action_date, comment, wf_history_id from ets.wf_history where wf_resource_id='"+resourceID+"' and project_id='"+projectID+"' order by wf_history_id desc with ur";
		try{
		db.prepareDirectQuery(q);
		System.out.println(q);
		int rows =db.execute();
		System.out.println("There are "+rows+" history items.");
		for(int i = 0; i< rows; i++)
		{
			IssueHistoryItem hItem = new IssueHistoryItem();
			hItem.setAction_taken(db.getString(i,0));
			hItem.setModified_by(db.getString(i,1));
			hItem.setAction_date(MiscUtils.reformatDate(db.getString(i,2)));
			hItem.setComments(db.getString(i,3));
			hItem.setHistory_id(db.getString(i,4));
			history.getItems().add(hItem);
		}
		for(int i = 0; i< rows; i++)
		{
			String histID = ((IssueHistoryItem)history.getItems().get(i)).getHistory_id();
			String action_by = ((IssueHistoryItem)history.getItems().get(i)).getModified_by();
			
			ETSUserDetails u = new ETSUserDetails();
			u.setWebId(action_by);
			u.extractUserDetails(db.getConnection());
			
			((IssueHistoryItem)history.getItems().get(i)).setModified_by(u.getFirstName()+" "+u.getLastName());
			
			getHistoryFields(histID,((IssueHistoryItem)history.getItems().get(i)).getFields(),db);
		}
		}catch(Exception e)
		{
			System.out.println(e);
		}
		
		
		return history;
	}
	private static void getHistoryFields(String history_id, ArrayList fields, DBAccess db) {
		IssueHistoryField f = null;
		String q = "select field_changed, previous_value, new_value from ets.wf_history_field where wf_history_id='"+history_id+"' with ur";
		try{
		db.prepareDirectQuery(q);
		System.out.println("Execing "+q);
		int rows = db.execute();
		for(int i =0; i< rows; i++)
		{
			f = new  IssueHistoryField();
			f.setFieldName(db.getString(i,0));
			f.setOldValue(db.getString(i,1));
			f.setNewValue(db.getString(i,2));
			fields.add(f);
		}
		}catch(Exception e)
		{
			System.out.println(e);
		}
		
	}
	public static void  setSecondaryHistory(String history_id, DetailsUtils d, NewIssueVO  vo, DBAccess db )
	{
		addHistoryField(history_id, vo.getProjectID(), "TITLE", null, vo.getTitle(),db);
		addHistoryField(history_id, vo.getProjectID(), "DESCRIPTION", null, vo.getDesc(),db);
		addHistoryField(history_id, vo.getProjectID(), "FOCAL POINT", null, vo.getFocalPointID(),db);
		addHistoryField(history_id, vo.getProjectID(), "TYPE", null, vo.getIssueTypeID(),db);
		addHistoryField(history_id, vo.getProjectID(), "CATEGORY", null, vo.getIssueCategory(),db);
		addHistoryField(history_id, vo.getProjectID(), "TARGET DATE", null, vo.getMonth()[0]+"/"+vo.getDay()[0]+"/"+vo.getYear()[0],db);
		for(int i = 0; i < vo.getOwnerID().length; i++)
		{
			addHistoryField(history_id, vo.getProjectID(), "NEW OWNER", null, vo.getOwnerID()[i],db);
		}
		db.doCommit();
		
	}
	public static void  setSecondaryHistory(String history_id, DetailsUtils d, EditIssueVO  vo, DBAccess db )
	{
			if(!CharUtils.SQLize(d.getIissue_title()).equals(vo.getTitle()))
				addHistoryField(history_id, vo.getProjectID(), "TITLE", CharUtils.SQLize(d.getIissue_title()), vo.getTitle(),db);
			if(!CharUtils.SQLize(d.getIissue_desc()).equals(vo.getDesc()))
				addHistoryField(history_id, vo.getProjectID(), "DESCRIPTION", CharUtils.SQLize(d.getIissue_desc()), vo.getDesc(),db);
			if(!CharUtils.SQLize(d.getIissue_contact()).equals(vo.getFocalPt()[0]))
				addHistoryField(history_id, vo.getProjectID(), "FOCAL POINT", CharUtils.SQLize(d.getIissue_contact()), vo.getFocalPt()[0],db);
			if(!CharUtils.SQLize(d.getIissue_type()).equals(vo.getType()[0]))
				addHistoryField(history_id, vo.getProjectID(), "TYPE", CharUtils.SQLize(d.getIissue_type()), vo.getType()[0],db);
			if(!CharUtils.SQLize(d.getIissue_category()).equals(vo.getCategory()[0]))
				addHistoryField(history_id, vo.getProjectID(), "CATEGORY", CharUtils.SQLize(d.getIissue_category()), vo.getCategory()[0],db);
			if(vo.getMonth()[0]!=null)if(vo.getMonth()[0].length()!=2)vo.setMonth(new String[]{"0"+vo.getMonth()[0]});
			if(vo.getDay()[0]!=null)if(vo.getDay()[0].length()!=2)vo.setDay(new String[]{"0"+vo.getDay()[0]});
			if(!d.getItarget_date().equals(vo.getYear()[0]+"-"+vo.getMonth()[0]+"-"+vo.getDay()[0]))
				addHistoryField(history_id, vo.getProjectID(), "TARGET_DATE", d.getItarget_date(), vo.getYear()[0]+"-"+vo.getMonth()[0]+"-"+vo.getDay()[0],db);
			for(int i = 0; i < vo.getOwners().length; i++)
			{
				boolean isNewOwner = true;
				for(int j =0; j < d.getIownerIds().size(); j ++)
				{
					if(((String)d.getIownerIds().get(j)).equals(vo.getOwners()[i]))
						{isNewOwner= false;continue;}
				}
				if(isNewOwner)
					addHistoryField(history_id, vo.getProjectID(), "NEW OWNER",null , vo.getOwners()[i],db);
			}
			for(int j =0; j < d.getIownerIds().size(); j ++)
			{
				boolean isDeletedOwner = true;
				for(int i = 0; i < vo.getOwners().length; i++)
				{
					
					if(((String)d.getIownerIds().get(j)).equals(vo.getOwners()[i]))
					{
						isDeletedOwner = false;continue;
					}
				}
				if(isDeletedOwner)
					addHistoryField(history_id, vo.getProjectID(), "DELETED OWNER",null , (String)d.getIownerIds().get(j),db);
				
			}
			db.doCommit();
		
	}
	public static void  setSecondaryHistory(String history_id, String projectID, ArrayList oldIssues,ArrayList newIssues, DBAccess db )
	{
		for(int i=0; i<newIssues.size();i++)
			addHistoryField(history_id,projectID,"ATTACHED ISSUE","",(String) newIssues.get(i),db);
		for(int i=0; i<oldIssues.size();i++)
			addHistoryField(history_id,projectID,"DETACHED ISSUE","",(String) oldIssues.get(i),db);
	}
	public static void addHistoryField(String history_id, String projectID, String field_changed, String previous_value, String new_value, DBAccess db) { 
		String q = "insert into ets.wf_history_field (wf_history_id, project_id, field_changed,";
		if(previous_value!=null)		q+=" previous_value,";
		q+=" new_value) values ("+
					" '"+history_id+"',"+
					" '"+projectID+"',"+
					" '"+field_changed+"',";
		if(previous_value!=null)q+=	" '"+previous_value+"',";
					q+=" '"+new_value+"')";
		try{
			System.out.println("Hist Field:"+q);
		db.prepareDirectQuery(q);
		db.execute();
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
		}
	}
	public static void setHistory(InitializeVO oldVO, InitializeVO vo, String identifyStageId) {
		DBAccess db = null;
		try{
			db = new DBAccess();
			String historyID = enterHistory(vo.getProjectID(),vo.getWorkflowID(),identifyStageId,ACTION_GENERIC_SETMET,vo.getLoggedUser(),"QBR Stage Modified",db);
			setBiweeklyReviewHistory(oldVO,vo,identifyStageId,historyID, db);
			setPlannedMeetingHistory(oldVO,vo,historyID,db);
			enterDateHistory(oldVO.getRatingFromYear()[0],oldVO.getRatingFromMonth()[0],oldVO.getRatingFromDay()[0],vo.getRatingFromYear()[0],vo.getRatingFromMonth()[0],vo.getRatingFromDay()[0],"Rating period \"From\" date",historyID,vo.getProjectID(),db);
			enterDateHistory(oldVO.getRatingToYear()[0],oldVO.getRatingToMonth()[0],oldVO.getRatingToDay()[0],vo.getRatingToYear()[0],vo.getRatingToMonth()[0],vo.getRatingToDay()[0],"Rating period \"To\" date",historyID,vo.getProjectID(),db);
			
			IdentityTransformer cnv_i = new IdentityTransformer();
			ClientNameFromID cnv_c = new ClientNameFromID();
			UserNameFromID cnv_u = new UserNameFromID();

			enterDropDownHistory(oldVO.getQbrQuarter(),vo.getQbrQuarter(),"Quarter",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getQbrYear(),vo.getQbrYear(),"Year",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getNsiRating(),vo.getNsiRating(),"NSI Rating",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getBackupContact(),vo.getBackupContact(),"Backup Account contact",historyID,vo.getProjectID(),cnv_u,db);
			enterDropDownHistory(oldVO.getAccountContact(),vo.getAccountContact(),"Account contact",historyID,vo.getProjectID(),cnv_u,db);
			enterDropDownHistory(oldVO.getExecSponsor(),vo.getExecSponsor(),"Executive Sponsor",historyID,vo.getProjectID(),cnv_u,db);
			enterComboBoxHistory(oldVO.getIbmAttendees(),vo.getIbmAttendees(),"IBM Attendees",historyID,vo.getProjectID(),cnv_u,db);
			enterComboBoxHistory(oldVO.getAttendees(),vo.getAttendees(),"Client Attendees",historyID,vo.getProjectID(),cnv_c,db);
			
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
	}
	public static void setHistory(oem.edge.ets.fe.workflow.sa.initialize.InitializeVO oldVO, oem.edge.ets.fe.workflow.sa.initialize.InitializeVO vo, String identifyStageId) {
		DBAccess db = null;
		try{
			db = new DBAccess();
			String historyID = enterHistory(vo.getProjectID(),vo.getWorkflowID(),identifyStageId,ACTION_GENERIC_SETMET,vo.getLoggedUser(),"Self Assessment Stage Modified",db);
			//setBiweeklyReviewHistory(oldVO,vo,identifyStageId,historyID, db);
			setPlannedMeetingHistory(oldVO,vo,historyID,db);
			/*enterDateHistory(oldVO.getRatingFromYear()[0],oldVO.getRatingFromMonth()[0],oldVO.getRatingFromDay()[0],vo.getRatingFromYear()[0],vo.getRatingFromMonth()[0],vo.getRatingFromDay()[0],"Rating period \"From\" date",historyID,vo.getProjectID(),db);
			enterDateHistory(oldVO.getRatingToYear()[0],oldVO.getRatingToMonth()[0],oldVO.getRatingToDay()[0],vo.getRatingToYear()[0],vo.getRatingToMonth()[0],vo.getRatingToDay()[0],"Rating period \"To\" date",historyID,vo.getProjectID(),db);*/
			
			IdentityTransformer cnv_i = new IdentityTransformer();
			ClientNameFromID cnv_c = new ClientNameFromID();
			UserNameFromID cnv_u = new UserNameFromID();
			MonthNameFromID cnv_m = new MonthNameFromID();
			
			enterDropDownHistory(oldVO.getQbrQuarter(),vo.getQbrQuarter(),"Month",historyID,vo.getProjectID(),cnv_m,db);
			enterDropDownHistory(oldVO.getQbrYear(),vo.getQbrYear(),"Year",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getNsiRating(),vo.getNsiRating(),"NSI Rating",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getBackupContact(),vo.getBackupContact(),"Backup Account contact",historyID,vo.getProjectID(),cnv_u,db);
			enterDropDownHistory(oldVO.getAccountContact(),vo.getAccountContact(),"Account contact",historyID,vo.getProjectID(),cnv_u,db);
			//enterDropDownHistory(oldVO.getExecSponsor(),vo.getExecSponsor(),"Executive Sponsor",historyID,vo.getProjectID(),cnv_u,db);
			enterComboBoxHistory(oldVO.getIbmAttendees(),vo.getIbmAttendees(),"IBM Attendees",historyID,vo.getProjectID(),cnv_u,db);
			enterComboBoxHistory(oldVO.getAttendees(),vo.getAttendees(),"Client Attendees",historyID,vo.getProjectID(),cnv_c,db);
			
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
	}
	/**
	 * This sets the history for the biweekly review fields.
	 * If provided with a non-null historyID, it uses the same. Otherwise it creates & uses a historyID and returns it.
	 */
	private static String setBiweeklyReviewHistory(InitializeVO oldVO, InitializeVO vo,String identifyStageId, String historyID, DBAccess db)
	{
		if(oldVO.getBiweeklyFlag()[0].equals("N") && vo.getBiweeklyFlag()[0].equals("N"))
		{
			; //nothing changed
		}
		if(oldVO.getBiweeklyFlag()[0].equals("N") && vo.getBiweeklyFlag()[0].equals("Y"))
		{
			if(historyID==null)
				historyID = enterHistory(vo.getProjectID(),vo.getWorkflowID(),identifyStageId,ACTION_GENERIC_SETMET,vo.getLoggedUser(),"Set/Met Stage Modified",db);
			
			addHistoryField(historyID,vo.getProjectID(),"Biweekly Review Flag","N","Y",db);
			addHistoryField(historyID,vo.getProjectID(),"Biweekly Review Date","NONE",vo.getBiweeklyYear()[0]+"/"+vo.getBiweeklyMonth()[0]+"/"+vo.getBiweeklyDay()[0],db);
			addHistoryField(historyID,vo.getProjectID(),"Biweekly review status","NONE",vo.getBiweeklyStatus()[0],db);
		}
		if(oldVO.getBiweeklyFlag()[0].equals("Y") && vo.getBiweeklyFlag()[0].equals("N"))
		{
			if(historyID==null)
				historyID = enterHistory(vo.getProjectID(),vo.getWorkflowID(),identifyStageId,ACTION_GENERIC_SETMET,vo.getLoggedUser(),"Set/Met Stage Modified",db);
			
			addHistoryField(historyID,vo.getProjectID(),"Biweekly Review Flag","Y","N",db);
			addHistoryField(historyID,vo.getProjectID(),"Biweekly Review Date",oldVO.getBiweeklyYear()[0]+"/"+oldVO.getBiweeklyMonth()[0]+"/"+oldVO.getBiweeklyDay()[0],"NONE",db);
			addHistoryField(historyID,vo.getProjectID(),"Biweekly review status",oldVO.getBiweeklyStatus()[0],"NONE",db);
		}
		if(oldVO.getBiweeklyFlag()[0].equals("Y") && vo.getBiweeklyFlag()[0].equals("Y"))
		{
			String oldYear = oldVO.getBiweeklyYear()[0];
			String oldMonth = oldVO.getBiweeklyMonth()[0];
			String oldDay = oldVO.getBiweeklyDay()[0];
			String newYear = vo.getBiweeklyYear()[0];
			String newMonth = vo.getBiweeklyMonth()[0];
			String newDay = vo.getBiweeklyDay()[0];
			historyID = null;
			
			boolean reviewDateChanged = true;
			boolean reviewStatusChanged =true;
			
			if(Integer.parseInt(oldYear)==Integer.parseInt(newYear) && Integer.parseInt(oldMonth)==Integer.parseInt(newMonth) && Integer.parseInt(oldDay)==Integer.parseInt(newDay))				
				reviewDateChanged = false;
			
			if(oldVO.getBiweeklyStatus()[0].equals(vo.getBiweeklyStatus()[0]))
				reviewStatusChanged = false;

			if(reviewDateChanged || reviewStatusChanged)
				if(historyID==null)
					historyID = enterHistory(vo.getProjectID(),vo.getWorkflowID(),identifyStageId,ACTION_GENERIC_SETMET,vo.getLoggedUser(),"Set/Met Stage Modified",db);
			if(reviewDateChanged)
				addHistoryField(historyID,vo.getProjectID(),"Biweekly Review Date",oldVO.getBiweeklyYear()[0]+"/"+oldVO.getBiweeklyMonth()[0]+"/"+oldVO.getBiweeklyDay()[0],vo.getBiweeklyYear()[0]+"/"+vo.getBiweeklyMonth()[0]+"/"+vo.getBiweeklyDay()[0],db);
			if(reviewStatusChanged)
				addHistoryField(historyID,vo.getProjectID(),"Biweekly review status",oldVO.getBiweeklyStatus()[0],vo.getBiweeklyStatus()[0],db);
		}
		return historyID;
	}
	private static void setPlannedMeetingHistory(InitializeVO oldVO, InitializeVO vo,String historyID, DBAccess db)
	{
		enterDateHistory(oldVO.getPlannedYear()[0],oldVO.getPlannedMonth()[0],oldVO.getPlannedDay()[0], vo.getPlannedYear()[0],vo.getPlannedMonth()[0],vo.getPlannedDay()[0],"Planned meeting date",historyID,vo.getProjectID(),db);
		boolean isOldLocationEmpty = false;
		boolean isNewLocationEmpty = false;
		if(oldVO.getMeetingLocation()==null || oldVO.getMeetingLocation().trim().length()==0)
			isOldLocationEmpty = true;
		if(vo.getMeetingLocation()==null || vo.getMeetingLocation().trim().length()==0)
			isNewLocationEmpty = true;
		if(!isOldLocationEmpty && !isNewLocationEmpty && !vo.getMeetingLocation().trim().equals(oldVO.getMeetingLocation().trim()))
			addHistoryField(historyID,vo.getProjectID(),"Meeting location",oldVO.getMeetingLocation(),vo.getMeetingLocation(),db);
		if(isOldLocationEmpty && !isNewLocationEmpty)
			addHistoryField(historyID,vo.getProjectID(),"Meeting location","NONE",vo.getMeetingLocation(),db);
		if(!isOldLocationEmpty && isNewLocationEmpty)
			addHistoryField(historyID,vo.getProjectID(),"Meeting location",oldVO.getMeetingLocation(),"NONE",db);
	}
	private static void setPlannedMeetingHistory(oem.edge.ets.fe.workflow.sa.initialize.InitializeVO oldVO, oem.edge.ets.fe.workflow.sa.initialize.InitializeVO vo,String historyID, DBAccess db)
	{
		enterDateHistory(oldVO.getPlannedYear()[0],oldVO.getPlannedMonth()[0],oldVO.getPlannedDay()[0], vo.getPlannedYear()[0],vo.getPlannedMonth()[0],vo.getPlannedDay()[0],"Planned meeting date",historyID,vo.getProjectID(),db);
		boolean isOldLocationEmpty = false;
		boolean isNewLocationEmpty = false;
		if(oldVO.getMeetingLocation()==null || oldVO.getMeetingLocation().trim().length()==0)
			isOldLocationEmpty = true;
		if(vo.getMeetingLocation()==null || vo.getMeetingLocation().trim().length()==0)
			isNewLocationEmpty = true;
		if(!isOldLocationEmpty && !isNewLocationEmpty && !vo.getMeetingLocation().trim().equals(oldVO.getMeetingLocation().trim()))
			addHistoryField(historyID,vo.getProjectID(),"Meeting location",oldVO.getMeetingLocation(),vo.getMeetingLocation(),db);
		if(isOldLocationEmpty && !isNewLocationEmpty)
			addHistoryField(historyID,vo.getProjectID(),"Meeting location","NONE",vo.getMeetingLocation(),db);
		if(!isOldLocationEmpty && isNewLocationEmpty)
			addHistoryField(historyID,vo.getProjectID(),"Meeting location",oldVO.getMeetingLocation(),"NONE",db);
	}
	public static void setHistory(InitializeVO vo, String identifyStageId) {
		DBAccess db = null;
		try{
			db = new DBAccess();
			String historyID = enterHistory(vo.getProjectID(),vo.getWorkflowID(),identifyStageId,ACTION_GENERIC_SETMET,vo.getLoggedUser(),"Set/Met Stage Modified",db);
			
			/* Biweekly review */
			addHistoryField(historyID,vo.getProjectID(),"Biweekly Review Flag","NONE",vo.getBiweeklyFlag()[0],db);
			if(vo.getBiweeklyFlag()[0].equals("Y"))
			{
				addHistoryField(historyID,vo.getProjectID(),"Biweekly Review Date","NONE",vo.getBiweeklyYear()[0]+"/"+vo.getBiweeklyMonth()[0]+"/"+vo.getBiweeklyDay()[0],db);
				addHistoryField(historyID,vo.getProjectID(),"Biweekly review status","NONE",vo.getBiweeklyStatus()[0],db);
			}
			
			/* A dummy oldVO */
			InitializeVO oldVO = new InitializeVO();
			oldVO.setPlannedDay(new String[]{null});
			oldVO.setPlannedMonth(new String[]{null});
			oldVO.setPlannedYear(new String[]{null});
			oldVO.setRatingFromDay(new String[]{null});
			oldVO.setRatingFromMonth(new String[]{null});
			oldVO.setRatingFromYear(new String[]{null});
			oldVO.setRatingToDay(new String[]{null});
			oldVO.setRatingToMonth(new String[]{null});
			oldVO.setRatingToYear(new String[]{null});
			
			setPlannedMeetingHistory(oldVO,vo,historyID,db);
			
			enterDateHistory(oldVO.getRatingFromYear()[0],oldVO.getRatingFromMonth()[0],oldVO.getRatingFromDay()[0],vo.getRatingFromYear()[0],vo.getRatingFromMonth()[0],vo.getRatingFromDay()[0],"Rating period \"From\" date",historyID,vo.getProjectID(),db);
			enterDateHistory(oldVO.getRatingToYear()[0],oldVO.getRatingToMonth()[0],oldVO.getRatingToDay()[0],vo.getRatingToYear()[0],vo.getRatingToMonth()[0],vo.getRatingToDay()[0],"Rating period \"To\" date",historyID,vo.getProjectID(),db);
			
			IdentityTransformer cnv_i = new IdentityTransformer();
			ClientNameFromID cnv_c = new ClientNameFromID();
			UserNameFromID cnv_u = new UserNameFromID();
			
			enterDropDownHistory(oldVO.getQbrQuarter(),vo.getQbrQuarter(),"Quarter",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getQbrYear(),vo.getQbrYear(),"Year",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getNsiRating(),vo.getNsiRating(),"NSI Rating",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getBackupContact(),vo.getBackupContact(),"Backup Account contact",historyID,vo.getProjectID(),cnv_u,db);
			enterDropDownHistory(oldVO.getAccountContact(),vo.getAccountContact(),"Account contact",historyID,vo.getProjectID(),cnv_u,db);
			enterDropDownHistory(oldVO.getExecSponsor(),vo.getExecSponsor(),"Executive Sponsor",historyID,vo.getProjectID(),cnv_u,db);
			enterComboBoxHistory(oldVO.getIbmAttendees(),vo.getIbmAttendees(),"IBM Attendees",historyID,vo.getProjectID(),cnv_u,db);
			enterComboBoxHistory(oldVO.getAttendees(),vo.getAttendees(),"Client Attendees",historyID,vo.getProjectID(),cnv_c,db);
			
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
	}
	public static void setHistory(oem.edge.ets.fe.workflow.sa.initialize.InitializeVO vo, String identifyStageId) {
		DBAccess db = null;
		try{
			db = new DBAccess();
			String historyID = enterHistory(vo.getProjectID(),vo.getWorkflowID(),identifyStageId,ACTION_GENERIC_SETMET,vo.getLoggedUser(),"Set/Met Stage Modified",db);
			
			/* Biweekly review */
			/*addHistoryField(historyID,vo.getProjectID(),"Biweekly Review Flag","NONE",vo.getBiweeklyFlag()[0],db);
			if(vo.getBiweeklyFlag()[0].equals("Y"))
			{
				addHistoryField(historyID,vo.getProjectID(),"Biweekly Review Date","NONE",vo.getBiweeklyYear()[0]+"/"+vo.getBiweeklyMonth()[0]+"/"+vo.getBiweeklyDay()[0],db);
				addHistoryField(historyID,vo.getProjectID(),"Biweekly review status","NONE",vo.getBiweeklyStatus()[0],db);
			}*/
			
			/* A dummy oldVO */
			oem.edge.ets.fe.workflow.sa.initialize.InitializeVO oldVO = new oem.edge.ets.fe.workflow.sa.initialize.InitializeVO();
			oldVO.setPlannedDay(new String[]{null});
			oldVO.setPlannedMonth(new String[]{null});
			oldVO.setPlannedYear(new String[]{null});
			/*oldVO.setRatingFromDay(new String[]{null});
			oldVO.setRatingFromMonth(new String[]{null});
			oldVO.setRatingFromYear(new String[]{null});
			oldVO.setRatingToDay(new String[]{null});
			oldVO.setRatingToMonth(new String[]{null});
			oldVO.setRatingToYear(new String[]{null});*/
			
			setPlannedMeetingHistory(oldVO,vo,historyID,db);
			
			/*enterDateHistory(oldVO.getRatingFromYear()[0],oldVO.getRatingFromMonth()[0],oldVO.getRatingFromDay()[0],vo.getRatingFromYear()[0],vo.getRatingFromMonth()[0],vo.getRatingFromDay()[0],"Rating period \"From\" date",historyID,vo.getProjectID(),db);
			enterDateHistory(oldVO.getRatingToYear()[0],oldVO.getRatingToMonth()[0],oldVO.getRatingToDay()[0],vo.getRatingToYear()[0],vo.getRatingToMonth()[0],vo.getRatingToDay()[0],"Rating period \"To\" date",historyID,vo.getProjectID(),db);*/
			
			IdentityTransformer cnv_i = new IdentityTransformer();
			ClientNameFromID cnv_c = new ClientNameFromID();
			UserNameFromID cnv_u = new UserNameFromID();
			MonthNameFromID cnv_m = new MonthNameFromID();
			
			enterDropDownHistory(oldVO.getQbrQuarter(),vo.getQbrQuarter(),"Month",historyID,vo.getProjectID(),cnv_m,db);
			enterDropDownHistory(oldVO.getQbrYear(),vo.getQbrYear(),"Year",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getNsiRating(),vo.getNsiRating(),"NSI Rating",historyID,vo.getProjectID(),cnv_i,db);
			enterDropDownHistory(oldVO.getBackupContact(),vo.getBackupContact(),"Backup Account contact",historyID,vo.getProjectID(),cnv_u,db);
			enterDropDownHistory(oldVO.getAccountContact(),vo.getAccountContact(),"Account contact",historyID,vo.getProjectID(),cnv_u,db);
			//enterDropDownHistory(oldVO.getExecSponsor(),vo.getExecSponsor(),"Executive Sponsor",historyID,vo.getProjectID(),cnv_u,db);
			enterComboBoxHistory(oldVO.getIbmAttendees(),vo.getIbmAttendees(),"IBM Attendees",historyID,vo.getProjectID(),cnv_u,db);
			enterComboBoxHistory(oldVO.getAttendees(),vo.getAttendees(),"Client Attendees",historyID,vo.getProjectID(),cnv_c,db);
			
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
	}
	private static boolean areDatesDifferent(String oldYear, String newYear, String oldMonth, String newMonth, String oldDay, String newDay)
	{
		return (Integer.parseInt(oldYear)==Integer.parseInt(newYear) && Integer.parseInt(oldMonth)==Integer.parseInt(newMonth) && Integer.parseInt(oldDay)==Integer.parseInt(newDay));
	}
	private static void enterDateHistory(String oldYear, String newYear, String oldMonth, String newMonth, String oldDay, String newDay, String fieldName, String historyID, String projectID, DBAccess db)
	{
		boolean isOldEmpty = false;
		boolean isNewEmpty = false;
		if(oldYear==null || oldYear.trim().length()==0 ||
		   oldMonth==null || oldMonth.trim().length()==0 ||
		   oldDay==null || oldDay.trim().length()==0
		)
			isOldEmpty = true;
		if(newYear==null || newYear.trim().length()==0 ||
				   newMonth==null || newMonth.trim().length()==0 ||
				   newDay==null || newDay.trim().length()==0
		)
			isNewEmpty = true;
		if(!isOldEmpty && !isNewEmpty)
			if(areDatesDifferent(oldYear, newYear, oldMonth, newMonth, oldDay, newDay))
				addHistoryField(historyID,projectID,fieldName,oldYear+"/"+oldMonth+"/"+oldDay,newYear+"/"+newMonth+"/"+newDay,db);
		if(!isOldEmpty && isNewEmpty)
			addHistoryField(historyID,projectID,fieldName,oldYear+"/"+oldMonth+"/"+oldDay,"NONE",db);
		if(isOldEmpty && !isNewEmpty)
			addHistoryField(historyID,projectID,fieldName,"NONE",newYear+"/"+newMonth+"/"+newDay,db);
	}
	private static void enterDropDownHistory(String[] oldDropDown, String[] newDropDown, String fieldName, String historyID, String projectID, IDToName cnv, DBAccess db) throws Exception
	{
		boolean isOldDropDownEmpty = false;
		boolean isNewDropDownEmpty = false;
		if(oldDropDown==null || oldDropDown.length==0 || oldDropDown[0]==null || oldDropDown[0].trim().length()==0)
			isOldDropDownEmpty = true;
		if(newDropDown==null || newDropDown.length==0 || newDropDown[0]==null || newDropDown[0].trim().length()==0)
			isNewDropDownEmpty = true;
		if(!isOldDropDownEmpty && !isNewDropDownEmpty)
			if(!oldDropDown[0].trim().equals(newDropDown[0].trim()))
				addHistoryField(historyID,projectID,fieldName,cnv.convert(oldDropDown[0],db),cnv.convert(newDropDown[0],db),db);
		if(isOldDropDownEmpty && !isNewDropDownEmpty)
			addHistoryField(historyID,projectID,fieldName,"NONE",cnv.convert(newDropDown[0],db),db);
		if(!isOldDropDownEmpty && isNewDropDownEmpty)
			addHistoryField(historyID,projectID,fieldName,cnv.convert(oldDropDown[0],db),"NONE",db);	
	}
	private static void enterComboBoxHistory(String[] oldCombo, String[] newCombo, String fieldName, String historyID, String projectID,IDToName cnv, DBAccess db) throws Exception
	{
		boolean isOldComboEmpty = false;
		boolean isNewComboEmpty = false;
		if(oldCombo==null || oldCombo.length==0)
			isOldComboEmpty = true;
		if(newCombo==null || newCombo.length==0)
			isNewComboEmpty = true;
				
		if(!isOldComboEmpty && !isNewComboEmpty)
		{
			boolean isDifferent = false;
			if(oldCombo.length!=newCombo.length)isDifferent=true;
			for(int i=0; i<oldCombo.length; i++)
			{
				boolean isPresentInNewCombo = false;
				for(int j=0; j<newCombo.length; j++)
				{
					if(oldCombo[i].equals(newCombo[j]))
					{
						isPresentInNewCombo = true;
						continue;
					}
				}
				if(!isPresentInNewCombo)
				{
					isDifferent = true;
					continue;
				}
			}
			System.out.println("old combo="+makeCommaSeparated(oldCombo,cnv,db));
			System.out.println("new combo="+makeCommaSeparated(newCombo,cnv,db));
			System.out.println("isDifferent = "+isDifferent);
			if(isDifferent)
				addHistoryField(historyID,projectID,fieldName,makeCommaSeparated(oldCombo,cnv,db),makeCommaSeparated(newCombo,cnv,db),db);
		}
		if(isOldComboEmpty && !isNewComboEmpty)
			addHistoryField(historyID,projectID,fieldName,"NONE",makeCommaSeparated(newCombo,cnv,db),db);
		if(!isOldComboEmpty && isNewComboEmpty)
			addHistoryField(historyID,projectID,fieldName,makeCommaSeparated(oldCombo,cnv,db),"NONE",db);
	}
	private static String makeCommaSeparated(String[] stringArray, IDToName cnv, DBAccess db) throws Exception
	{
		StringBuffer b = new StringBuffer("");
		for(int i=0;i<stringArray.length-1;i++)
			b.append(cnv.convert(stringArray[i],db)+", ");
		b.append(cnv.convert(stringArray[stringArray.length-1],db));
		return b.toString();
	}
}

