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

package oem.edge.ets.fe.workflow.newissue;

import java.util.ArrayList;

import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.issue.edit.EditIssueDAO;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.notification.IssueNotificationParams;
import oem.edge.ets.fe.workflow.notification.NotificationConstants;
import oem.edge.ets.fe.workflow.notification.Notifier;
import oem.edge.ets.fe.workflow.notification.NotifierFactory;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import oem.edge.ets.fe.workflow.util.HistoryUtils;

import org.apache.commons.logging.Log;


/**
 * Class       : NewIssueDAO
 * Package     : oem.edge.ets.fe.workflow.newissue
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewIssueDAO extends AbstractDAO {

	private String issueID = null;
	
	
	/**
	 * @return Returns the issueID.
	 */
	public String getIssueID() {
		return issueID;
	}
	
	private static Log logger = WorkflowLogger.getLogger(NewIssueDAO.class);
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObject(oem.edge.ets.fe.workflow.core.WorkflowObject)
	 */
	String loggedUser = null;
	String acOwner = null;
	public boolean saveWorkflowObject(WorkflowObject workflowObject) {
		NewIssueVO vo = (NewIssueVO)workflowObject;
	
		DetailsUtils d = new DetailsUtils();
		d.setProjectID(vo.getProjectID());
		d.setWorkflowID(vo.getWorkflowID());
		d.extractWorkflowDetails(vo.getDB().getConnection());
		
		
		System.out.println("In saveWorkflowObject");
		String day = null;
		String month = null;
		String year = null;
		if(vo.getDay()!=null && vo.getDay()[0]!=null && vo.getDay()[0].trim().length()!=0)
			day  = vo.getDay()[0];
		if(vo.getMonth()!=null && vo.getMonth()[0]!=null && vo.getMonth()[0].trim().length()!=0)
			month  = vo.getMonth()[0];
		if(vo.getYear()!=null && vo.getYear()[0]!=null && vo.getYear()[0].trim().length()!=0)
			year  = vo.getYear()[0];
		
		/** Code to persist data into the database	 */
		
		DBAccess db = null;
		
		try {
			db=vo.getDB();
			
			
			String issue_id = ETSCalendar.getNewCalendarId();
			String issue_id_display = getIssueID(vo.getProjectID(),vo.getWorkflowID(), db);
			issueID = issue_id;
			

			
			String q1 ="INSERT INTO ETS.WF_ISSUE (ISSUE_ID, " +
												 "ISSUE_TITLE, " +
												 "ISSUE_DESC, " +
												 "ISSUE_CONTACT, " +
												 "STATUS, " +
												 "ISSUE_TYPE, " +
												 "ISSUE_CATEGORY,"+
												 "TARGET_DATE, " +
												 "INITIAL_TARGET_DATE," +
												 "ISSUE_ID_DISPLAY) " +
												 
												 " VALUES(" +
												 "'"+issue_id+
												 "','"+vo.getTitle()+
												 "','"+vo.getDesc()+
												 "','"+vo.getFocalPointID()+
												 "','"+"ASSIGNED"+
												 "','"+vo.getIssueTypeID()+
												 "','"+vo.getIssueCategory()+
												 "', DATE('"+year+"-"+month+"-"+day+
												 "'), DATE('"+year+"-"+month+"-"+day+
												 "'),'" + issue_id_display+"' "+
												 ")";
			db.prepareDirectQuery(q1);
			System.out.println(q1);
			System.out.println(".....Waiting for database to insert the new attendee.");
			db.execute();
			System.out.println("....Database finished inserting.");
			
			for(int i=0; i<vo.getOwnerID().length; i++)
			{
			
				
			String q2 = "INSERT INTO ETS.WF_ISSUE_OWNER(ISSUE_ID, "
						+ "OWNER_ID, ASSIGNed_DATE) " + " VALUES(" + "'"
						+ issue_id + "','" + vo.getOwnerID()[i]
						+ "', current date)";
			//if(vo.getOwnerID()[i].equals(d.getWacct_contact()))
			if(vo.getOwnerID()[i].equals(loggedUser))
			{
				 q2 = "INSERT INTO ETS.WF_ISSUE_OWNER(ISSUE_ID, "
					+ "OWNER_ID, OWNERSHIP_STATE, ASSIGNed_DATE) " + " VALUES(" + "'"
					+ issue_id + "','" + vo.getOwnerID()[i]
					+ "', 'ACCEPTED', current date)";
				 acOwner = loggedUser;
			}
			
			db.prepareDirectQuery(q2);
			System.out.println("DB: "+q2);
			db.execute();
			
			System.out.println("DB: Done");

			if(i==0)
			{
				String project_id = vo.getProjectID();
				String wf_id = vo.getWorkflowID();
				q2 = "INSERT INTO ETS.WF_ISSUE_WF_MAP (project_id, wf_id, issue_id) values ('"+project_id+"', '"+wf_id+"', '"+issue_id+"')";
				db.prepareDirectQuery(q2);
				db.execute();
				System.out.println("DB: Done");
			}
			
			}
			
			System.out.println("Calling updateStatus");
			EditIssueDAO issueEditor = new EditIssueDAO();
			issueEditor.loggedUser = loggedUser;
			issueEditor.updateStatus(issueID,db,vo.getWorkflowID());
			String histID = HistoryUtils.enterHistory(vo.getProjectID(),vo.getWorkflowID(),issue_id,HistoryUtils.ACTION_CREATE,loggedUser,"New issue created",db);
			HistoryUtils.setSecondaryHistory(histID,null,vo,db);
			if(acOwner!=null)HistoryUtils.enterHistory(vo.getProjectID(),vo.getWorkflowID(),issue_id,HistoryUtils.ACTION_ACCEPTED,loggedUser,"Ownership status for "+ acOwner+" set to ACCEPTED automatically",db);
			ArrayList owners = new ArrayList();
			ArrayList focalPoint = new ArrayList(); //this will be ArrayList of size one.
			for(int i=0; i < vo.getOwnerID().length; i++)
			{
				owners.add(vo.getOwnerID()[i]);
				if(vo.getOwnerID()[i].equals(loggedUser))
					owners.remove(i);
			}
			focalPoint.add(vo.getFocalPointID());
			
			System.out.println("configuring notifier...");
			IssueNotificationParams params = new IssueNotificationParams();
			params.setProjectID(vo.getProjectID());
			params.setWorkflowID(vo.getWorkflowID());
			params.setTc("0000");
			params.setIssueID(getIssueID());
			params.setLoggedUser(loggedUser);
			params.setNotificationType(NotificationConstants.NT_ISSUE);
			params.setEventType(NotificationConstants.EVT_ISSUE_NEW_ISSUE);
			Notifier n = NotifierFactory.getNotificationSender(params);
			n.init(params, db);
			System.out.println("Config done.");
		
			System.out.println("Mailing owners..");
			n.send(NotificationConstants.RECIPIENT_ISSUE_OWNER,owners,false,db);
		
			System.out.println("Mailing focal point..");
			n.send(NotificationConstants.RECIPIENT_ISSUE_CONTACT,focalPoint,false,db);
		
			System.out.println("Mailing account contact..");
			n.send(NotificationConstants.RECIPIENT_WORKFLOW_ACCOUNT_CONTACT,d.getWacct_contact(),false,db);
			
			System.out.println("Mailing others..");
			ArrayList others = new ArrayList();
			if(vo.getNotifyOption()!=null && vo.getNotificationList()!=null)
				for(int i = 0; i<vo.getNotificationList().length; i++)
					others.add(vo.getNotificationList()[i]);
			n.send(NotificationConstants.RECIPIENT_ISSUE_GENERAL,others,false,db);

			
			db.doCommit();
			
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}
		System.out.println("returning from new issue DAO");
		return true;
	}
	
	private String getIssueID(String projectID, String workflowID, DBAccess db) throws Exception {
		//String q="select concat(a.company, concat('-', right(concat('00000', rtrim(cast(count(b.issue_id)+1 as char(5)))),5))) from ets.ets_projects a, ets.wf_issue_wf_map b where a.project_id='"+projectID+"' and a.project_id = b.project_id group by a.company";
		String prefix = "NONE";
		String q = "select wf_type from ets.wf_def where wf_id = '"+workflowID+"' with ur";
		db.prepareDirectQuery(q);
		if(db.execute()==1)prefix=db.getString(0,0);
		if("QBR".equalsIgnoreCase(prefix.trim()))prefix="qb";
		if("SETMET".equalsIgnoreCase(prefix.trim()))prefix="sm";
		if("SELF ASSESSMENT".equalsIgnoreCase(prefix.trim()))prefix="sa";
		q = "select right(concat('0000', rtrim(cast(count(a.issue_id) as char(4)))),4) from ets.wf_issue_wf_map a, ets.wf_issue b where a.issue_id=b.issue_id and b.issue_id_display like '"+prefix+"%' and a.project_id='"+projectID+"' with ur";
		db.prepareDirectQuery(q);
		if(db.execute()==1)return prefix+db.getString(0,0);
		return ETSCalendar.getNewCalendarId();
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

	
	public int getIssueCount(NewIssueVO vo) {
		String project_id = vo.getProjectID();
		String wf_id = vo.getWorkflowID();
		String q="SELECT count(issue_id) FROM ETS.WF_ISSUE_WF_MAP where project_id='"+project_id+"' and wf_id='"+wf_id+"' with ur";
		DBAccess db = null;
		try {
			db=vo.getDB();
			db.prepareDirectQuery(q);
			System.out.println("DB: "+q);
			db.execute();
			return db.getInt(0,0);
		}
		catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
		}
		
		return 9999; //This value may require review
	}
	
}
