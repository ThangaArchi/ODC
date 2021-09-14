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

package oem.edge.ets.fe.workflow.issue.edit;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;


import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.dao.DBHelperException;
import oem.edge.ets.fe.workflow.dao.WorkflowDBException;

import oem.edge.ets.fe.workflow.notification.IssueNotificationParams;
import oem.edge.ets.fe.workflow.notification.NotificationConstants;
import oem.edge.ets.fe.workflow.notification.NotifierFactory;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import oem.edge.ets.fe.workflow.util.HistoryUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;
import oem.edge.ets.fe.workflow.notification.Notifier;



/**
 * Class : EditIssueDAO Package : oem.edge.ets.fe.workflow.issue.edit
 * Description : Date : Oct 11, 2006
 * 
 * @author : Pradyumna Achar
 */
public class EditIssueDAO extends AbstractDAO {

	private ArrayList newOwners = null;

	private ArrayList delOwners = null;

	public String loggedUser = null;

	String q = null;

	private int DBCOUNT = 0; //TODO:REMOVE

	public ArrayList getDelOwners() {
		return delOwners;
	}

	public ArrayList getNewOwners() {
		return newOwners;
	}

	//private static Log logger = WorkflowLogger.getLogger(EditIssueDAO.class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObject(oem.edge.ets.fe.workflow.core.WorkflowObject)
	 */
	public boolean saveWorkflowObject(WorkflowObject workflowObject) {
		EditIssueVO vo = (EditIssueVO) workflowObject;
		//code to write the issue
		//Runtime.getRuntime().gc();

		DBAccess db = null;
		DetailsUtils issDets = new DetailsUtils();
		issDets.setIssueID(vo.getIssueID());

		try {
			db = vo.getDB();
			DBCOUNT++;

			issDets.extractIssueDetails(vo.getDB().getConnection());
			ArrayList oldOwners = issDets.getIownerIds();
			System.out.println("Old owners are");
			for (int i = 0; i < oldOwners.size(); i++)
				System.out.println(oldOwners.get(i));
			newOwners = new ArrayList();
			delOwners = new ArrayList();

			ArrayList temp = new ArrayList();
			for (int i = 0; i < vo.getOwners().length; i++) {
				temp.add(vo.getOwners()[i]);
			}
			MiscUtils.removeDuplicates(temp);
			String[] temp2 = new String[temp.size()];
			for (int i = 0; i < temp.size(); i++) {
				temp2[i] = (String) temp.get(i);
			}
			vo.setOwners(temp2);

			System.out.println("User selected owner list is:");
			for (int i = 0; i < vo.getOwners().length; i++)
				System.out.println(vo.getOwners()[i]);
			//Deleting all owners from DB
			db
					.prepareDirectQuery("delete from ets.wf_issue_owner where issue_id='"
							+ vo.getIssueID() + "'");
			System.out.println("DBCOUNT=" + DBCOUNT);
			db.execute();

			//Inserting owners into DB
			for (int i = 0; i < vo.getOwners().length; i++) {
				boolean isOldOwner = false;
				int oldIndex = 0;
				for (int j = 0; j < oldOwners.size(); j++) {
					if (vo.getOwners()[i].equals(oldOwners.get(j))) {
						isOldOwner = true;
						oldIndex = j;
						continue;
					}
				}
				if (!isOldOwner)
					newOwners.add(vo.getOwners()[i]);

				String q2 = "INSERT INTO ETS.WF_ISSUE_OWNER(ISSUE_ID, "
						+ "OWNER_ID, ASSIGNed_DATE) " + " VALUES(" + "'" + vo.getIssueID()
						+ "','" + vo.getOwners()[i] + "', current date)";
				if (isOldOwner
						&& !((String) issDets.getIownerStates().get(oldIndex))
								.equals("ASSIGNED"))
					q2 = "INSERT INTO ETS.WF_ISSUE_OWNER(ISSUE_ID, "
							+ "OWNER_ID, OWNERSHIP_STATE) " + " VALUES(" + "'"
							+ vo.getIssueID() + "','" + vo.getOwners()[i]
							+ "', '"
							+ (String) issDets.getIownerStates().get(oldIndex)
							+ "')";
				db.prepareDirectQuery(q2);
				System.out.println("DB: " + q2);
				System.out.println("DBCOUNT=" + DBCOUNT);
				db.execute();
				System.out.println("DB: Done");

			}

			for (int j = 0; j < oldOwners.size(); j++) {
				boolean isDeletedOwner = true;
				for (int i = 0; i < vo.getOwners().length; i++) {
					if (oldOwners.get(j).equals(vo.getOwners()[i])) {
						isDeletedOwner = false;
						continue;
					}
				}
				if (isDeletedOwner)
					delOwners.add(oldOwners.get(j));
			}

			System.out.println("New Owners are:");
			for (int i = 0; i < newOwners.size(); i++) {
				System.out.println(newOwners.get(i));
			}
			System.out.println("Deleted Owners are:");
			for (int i = 0; i < delOwners.size(); i++) {
				System.out.println(delOwners.get(i));
			}

			String q1 = "UPDATE ETS.WF_ISSUE SET " + "ISSUE_TITLE= '"
					+ vo.getTitle() + "', ISSUE_DESC= '" + vo.getDesc() + "' ";
			if (vo.getFocalPt() != null && vo.getFocalPt()[0] != null
					&& vo.getFocalPt()[0].trim().length() != 0)
				q1 += ",ISSUE_CONTACT='" + vo.getFocalPt()[0] + "' ";
			if (vo.getType() != null && vo.getType()[0] != null
					&& vo.getType()[0].trim().length() != 0)
				q1 += ",ISSUE_TYPE= '" + vo.getType()[0] + "'";
			if (vo.getCategory() != null && vo.getCategory()[0] != null
					&& vo.getCategory()[0].trim().length() != 0)
				q1 += ",ISSUE_CATEGORY= '" + vo.getCategory()[0] + "'";

			String day = null;
			String month = null;
			String year = null;
			if (vo.getDay() != null && vo.getDay()[0] != null
					&& vo.getDay()[0].trim().length() != 0)
				day = vo.getDay()[0];
			if (vo.getMonth() != null && vo.getMonth()[0] != null
					&& vo.getMonth()[0].trim().length() != 0)
				month = vo.getMonth()[0];
			if (vo.getYear() != null && vo.getYear()[0] != null
					&& vo.getYear()[0].trim().length() != 0)
				year = vo.getYear()[0];
			if (day != null && month != null && year != null) {
				q1 += ", TARGET_DATE= '" + year + "-" + month + "-" + day
						+ "' ";

				if (issDets.getIinitial_target_date() == null)
					q1 += ", INITIAL_TARGET_DATE= '" + year + "-" + month + "-"
							+ day + "' ";
				System.out.println(year);
				System.out.println(month);
				System.out.println(day);
				//String
				// newStatus=updateStatus(vo.getIssueID(),""+year+"-"+month+"-"+day,db);
				//q1+=", STATUS='"+newStatus+"' ";
			}

			q1 += " WHERE ISSUE_ID='" + vo.getIssueID() + "'";
			db.prepareDirectQuery(q1);
			System.out.println("DB:" + q1);
			System.out.println("DBCOUNT=" + DBCOUNT);
			db.execute();
			//db.doCommit();
			updateStatus(vo.getIssueID(), "" + year + "-" + month + "-" + day,
					db, vo.getWorkflowID());
			String histID = HistoryUtils.enterHistory(vo.getProjectID(), vo.getWorkflowID(), vo
					.getIssueID(), HistoryUtils.ACTION_MODIFIED, loggedUser, vo
					.getComment(), db);
			HistoryUtils.setSecondaryHistory(histID, issDets,vo,db);
			
			System.out.println("configuring notifier...");
			IssueNotificationParams params = new IssueNotificationParams();
			params.setProjectID(vo.getProjectID());
			params.setWorkflowID(vo.getWorkflowID());
			params.setTc("0000");
			params.setIssueID(vo.getIssueID());
			params.setLoggedUser(loggedUser);
			params.setNotificationType(NotificationConstants.NT_ISSUE);
			params.setEventType(NotificationConstants.EVT_ISSUE_REASSIGN);
			Notifier n = NotifierFactory.getNotificationSender(params);
			n.init(params, db);
			System.out.println("Config done.");
			
			System.out.println("Mailing new owners..");
			n.send(NotificationConstants.RECIPIENT_ISSUE_OWNER,getNewOwners(),false,db);
			
			System.out.println("Mailing deleted owners..");
			n.send(NotificationConstants.RECIPIENT_ISSUE_OLD_OWNER,getDelOwners(),false,db);

			System.out.println("Mailing account contact..");
			db.prepareDirectQuery("select acct_contact from ets.wf_def where wf_id='"+vo.getWorkflowID()+"' with ur");
			if(db.execute()==1)
				n.send(NotificationConstants.RECIPIENT_WORKFLOW_ACCOUNT_CONTACT,db.getString(0,0),false,db);
			
			System.out.println("Mailing all participants of the issue...");
			ArrayList participants = new ArrayList();
			for(int i=0; i< vo.getOwners().length; i++)
			{
				participants.add(vo.getOwners()[i]);
			}
			participants.add(vo.getFocalPt()[0]);
			n.send(NotificationConstants.RECIPIENT_ISSUE_GENERAL,participants,false,db);
			
			db.doCommit();
			System.out.println("DB:done");
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}
		//end of code to write the issue

		//modifying issue status
		//updateStatus(vo.getIssueID());
		//issue status modified.
		System.out.println("exiting DAO");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObject(java.lang.String)
	 */
	public WorkflowObject getWorkflowObject(String ID) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObjectList(java.util.ArrayList)
	 */
	public boolean saveWorkflowObjectList(ArrayList object) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObjectList(java.lang.String)
	 */
	public ArrayList getWorkflowObjectList(String ID) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param uid
	 * @param iid
	 * @param string
	 */
	public void modifyOwnership(EditIssueVO vo, String uid, String iid,
			String newStatus, DBAccess db) {

		try {

			db
					.prepareDirectQuery("update ets.wf_issue_owner set ownership_state='"
							+ newStatus
							+ "' where issue_id='"
							+ iid
							+ "' and owner_id='" + uid + "'");
			System.out.println("DBCOUNT=" + DBCOUNT);
			db.execute();
			updateStatus(iid, db, vo.getWorkflowID());
			if (newStatus.equalsIgnoreCase("ACCEPTED"))
				HistoryUtils.enterHistory(vo.getProjectID(),
						vo.getWorkflowID(), iid, HistoryUtils.ACTION_ACCEPTED,
						loggedUser, vo.getComment(), db);
			else
				HistoryUtils.enterHistory(vo.getProjectID(),
						vo.getWorkflowID(), iid, HistoryUtils.ACTION_REJECTED,
						loggedUser, vo.getComment(), db);
			
			System.out.println("configuring notifier...");
			IssueNotificationParams params = new IssueNotificationParams();
			params.setProjectID(vo.getProjectID());
			params.setWorkflowID(vo.getWorkflowID());
			params.setTc("0000");
			params.setIssueID(iid);
			params.setLoggedUser(loggedUser);
			params.setNotificationType(NotificationConstants.NT_ISSUE);
			if(vo.getComment()!=null)
				params.setComments(vo.getComment().replaceAll("''","'"));
			if(newStatus.equalsIgnoreCase("ACCEPTED"))
				params.setEventType(NotificationConstants.EVT_OWNER_ACCEPTS);
			else
				params.setEventType(NotificationConstants.EVT_OWNER_REJECTS);
			
			ETSUserDetails u = new ETSUserDetails();
			u.setWebId(loggedUser);
			u.extractUserDetails(db.getConnection());
			
			params.setIssue_owner(u.getFirstName()+" "+u.getLastName());
			Notifier n = NotifierFactory.getNotificationSender(params);
			n.init(params, db);
			System.out.println("Config done.");
			
			db.prepareDirectQuery("select issue_contact from ets.wf_issue where issue_id='"+iid+"' with ur");
			if(db.execute()==1)
			{
				n.send(NotificationConstants.RECIPIENT_ISSUE_CONTACT,db.getString(0,0),false,db);
			}
			
			System.out.println("Mailing account contact..");
			db.prepareDirectQuery("select acct_contact from ets.wf_def where wf_id='"+vo.getWorkflowID()+"' with ur");
			if(db.execute()==1)
				n.send(NotificationConstants.RECIPIENT_WORKFLOW_ACCOUNT_CONTACT,db.getString(0,0),false,db);
			
			db.doCommit();

		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}

	}

	public void modifyIssueStatus(EditIssueVO vo, String iid, String newStatus,
			DBAccess db) {
		//CLOSE_DATE not yet done.

		try {

			String q = "update ets.wf_issue set statUS='" + newStatus
					+ "' where issue_id='" + iid + "'";
			System.out.println("DB:" + q);
			db.prepareDirectQuery(q);
			System.out.println("Calling db.execute()");
			System.out.println("DBCOUNT=" + DBCOUNT);
			db.execute();
			System.out.println("update query done - modifyIssueStatus");
			db.doCommit();
			if (newStatus.equalsIgnoreCase("COMPLETED"))
				HistoryUtils.enterHistory(vo.getProjectID(),
						vo.getWorkflowID(), iid, HistoryUtils.ACTION_COMPLETED,
						loggedUser, vo.getComment(), db);
			else
				HistoryUtils.enterHistory(vo.getProjectID(),
						vo.getWorkflowID(), iid, HistoryUtils.ACTION_CANCELLED,
						loggedUser, vo.getComment(), db);

		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}
	}
	
	/**
	 * @since 7.1.1
	 */
	public void updateStatus(String iid, DBAccess db, String workflowID) throws SQLException,
			DBHelperException, WorkflowDBException {
		updateStatus(iid,db,workflowID,false);
		
	}
	
	/**
	 * Method signature changed in 7.1.1
	 * Change: An additional argument workflowID (String)
	 * Reason: One issue can appear in multiple workflows from 7.1.1
	 */
	public void updateStatus(String iid, DBAccess db, String workflowID, boolean isDaemon) throws SQLException,
			DBHelperException, WorkflowDBException {
		DetailsUtils d = new DetailsUtils();
		d.setIssueID(iid);
		d.extractIssueDetails(db.getConnection());
		String newStatus = updateStatus(iid, d.getItarget_date(), db, workflowID);
	}

	/**
	 * @since 7.1.1
	 */
	private String updateStatus(String iid, String tdate, DBAccess db, String workflowID)
	throws SQLException, DBHelperException, WorkflowDBException {
		return updateStatus(iid,tdate,db,workflowID,false);
	}
	/**
	 * Method signature changed in 7.1.1
	 * Change: An additional argument workflowID (String)
	 * Reason: One issue can appear in multiple workflows from 7.1.1
	 */
	private String updateStatus(String iid, String tdate, DBAccess db, String workflowID, boolean isDaemon)
			throws SQLException, DBHelperException, WorkflowDBException {
		//TODO : tc and loggedUser are hardcoded .. Change them soon
		DetailsUtils d = new DetailsUtils();
		d.setIssueID(iid);
		d.extractWFIdANDProjFromIID(iid, db.getConnection());
		d.setWorkflowID(workflowID); //added in 7.1.1
		d.extractWorkflowDetails(db.getConnection());
		d.extractIssueDetails(db.getConnection());

		//DBAccess db = null;
		String newState = d.getIstatus();

		//try {

		//	db = new DBAccess();
		//	DBCOUNT++;

		String state = d.getIstatus();
		System.out.println("current status is :" + state);
		String year = null;
		String month = null;
		String day = null;
		if (tdate != null) {
			String[] temp2 = tdate.split("-");

			year = temp2[0];
			month = Integer.toString(Integer.parseInt(temp2[1]));
			day = temp2[2];

			System.out.println("target year" + year);
			System.out.println("target month" + month);
			System.out.println("targe day" + day);
		}
		db
				.prepareDirectQuery("select OWNERSHIP_STATE from ets.wf_issue_owner where issue_id='"
						+ iid + "' with ur");
		System.out.println("DBCOUNT=" + DBCOUNT);
		int n = db.execute();
		boolean isTouchedByOwners = false;
		boolean isTotallyRejected = true;
		boolean isAcceptedBySomeone = false;
		boolean isRejectedBySomeone = false;
		for (int i = 0; i < n; i++)
			if (db.getString(i, 0) != null)
				isTouchedByOwners = true;
		for (int i = 0; i < n; i++)
			if (db.getString(i, 0) == null
					|| !db.getString(i, 0).equalsIgnoreCase("REJECTED"))
				isTotallyRejected = false;
		for (int i = 0; i < n; i++)
			if (db.getString(i, 0) != null
					&& db.getString(i, 0).equalsIgnoreCase("ACCEPTED"))
				isAcceptedBySomeone = true;
		for (int i = 0; i < n; i++)
			if (db.getString(i, 0) != null
					&& db.getString(i, 0).equalsIgnoreCase("REJECTED"))
				isRejectedBySomeone = true;

		System.out.println("UpdateStatus: isTotallyRejected is "
				+ isTotallyRejected);
		System.out.println("UpdateStatus: isTouchedByOwners is "
				+ isTouchedByOwners);

		if (tdate == null) {
			newState = "ASSIGNED";
		} else {
			if (isTotallyRejected && !isAcceptedBySomeone
					&& !state.equalsIgnoreCase("COMPLETED")
					&& !state.equalsIgnoreCase("CANCELLED")) {
				newState = "REJECTED";
				System.out.println("isTotallyRejected && !isAcceptedBySomeone");
			}
			if (!isTouchedByOwners && !state.equalsIgnoreCase("COMPLETED")
					&& !state.equalsIgnoreCase("CANCELLED")) {
				newState = "ASSIGNED";
				System.out.println("!isTouchedByOwners");
			}

			if (isAcceptedBySomeone && !state.equalsIgnoreCase("COMPLETED")
					&& !state.equalsIgnoreCase("CANCELLED")) {
				System.out.println("isAcceptedBySomeone");
				Timestamp ts = new Timestamp(Integer.parseInt(year) - 1900,
						Integer.parseInt(month) - 1, Integer.parseInt(day), 0,
						0, 0, 0);
				Timestamp cts = new Timestamp(System.currentTimeMillis());
				boolean isPastDate = true;
				if (ts.getYear() > cts.getYear())
					isPastDate = false;
				if (ts.getYear() == cts.getYear()
						&& ts.getMonth() > cts.getMonth())
					isPastDate = false;
				if (ts.getYear() == cts.getYear()
						&& ts.getMonth() == cts.getMonth()
						&& ts.getDate() >= cts.getDate())
					isPastDate = false;
				if (!isPastDate)
					newState = "NOT PAST DUE";
				else
					newState = "PAST DUE";
			}
		}

		db.prepareDirectQuery("update ets.wf_issue set status='" + newState
				+ "' where issue_id='" + iid + "'");
		db.execute();
		db.doCommit();
		

		//Sending E-mails
		
		try{
		IssueNotificationParams params = new IssueNotificationParams();
		params.setProjectID(d.getProjectID());
		params.setWorkflowID(d.getWorkflowID());
		params.setTc("0000");
		params.setIssueID(iid);
		params.setLoggedUser(loggedUser);
		params.setNotificationType(NotificationConstants.NT_ISSUE);
		Notifier ntf = NotifierFactory.getNotificationSender(params);
		
		if (!state.equalsIgnoreCase("REJECTED")
				&& newState.equalsIgnoreCase("REJECTED")) {
			ArrayList temp = new ArrayList();
			temp.add(d.getIissue_contact());
			params.setEventType(NotificationConstants.EVT_ISSUE_REJECTED);
			ntf.init(params, db);
			
			ntf.send(NotificationConstants.RECIPIENT_ISSUE_CONTACT,temp,false,db);
			db.prepareDirectQuery("select acct_contact from ets.wf_def where wf_id='"+workflowID+"' with ur");
			if(db.execute()==1)
				ntf.send(NotificationConstants.RECIPIENT_WORKFLOW_ACCOUNT_CONTACT,db.getString(0,0),false,db);
		
		}

		if ((state.equalsIgnoreCase("REJECTED")
				&& !newState.equalsIgnoreCase("REJECTED") && !newState
				.equalsIgnoreCase("CANCELLED"))
				|| (state.equalsIgnoreCase("ASSIGNED")
						&& !newState.equalsIgnoreCase("ASSIGNED")
						&& !newState.equalsIgnoreCase("REJECTED") && !newState
						.equalsIgnoreCase("CANCELLED"))) {
			System.out.println("****0* EMAIL CHECK *****");
			System.out.println(state);
			System.out.println(newState);

			ArrayList temp = new ArrayList();
			temp.add(d.getIissue_contact());
			if(isDaemon)
				params.setEventType(NotificationConstants.EVT_ISSUE_ACCEPTED_DAEMON);	
			else
				params.setEventType(NotificationConstants.EVT_ISSUE_ACCEPTED);
			ntf.init(params, db);
			ntf.send(NotificationConstants.RECIPIENT_ISSUE_CONTACT,temp,false,db);
			db.prepareDirectQuery("select acct_contact from ets.wf_def where wf_id='"+workflowID+"' with ur");
			if(db.execute()==1)
				ntf.send(NotificationConstants.RECIPIENT_WORKFLOW_ACCOUNT_CONTACT,db.getString(0,0),false,db);
		}
}catch(Exception e)
{
	System.out.println(e);
}

		/*
		 * db.close(); DBCOUNT--; db=null; } catch (Exception e) {
		 * db.doRollback(); e.printStackTrace(); }finally{ if(db!=null){ try{
		 * db.close(); DBCOUNT--; db=null;
		 * 
		 * }catch(Exception ex){
		 *  } } }
		 */return newState;

	}

}
