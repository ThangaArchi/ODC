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

package oem.edge.ets.fe.workflow.setmet.prepare;

import java.sql.Timestamp;
import java.util.ArrayList;

import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import oem.edge.ets.fe.workflow.util.HistoryUtils;

import org.apache.commons.logging.Log;


/**
 * Class       : ListOfExistingIssuesDAO
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */

public class ListOfExistingIssuesDAO extends AbstractDAO {

	private static Log logger = WorkflowLogger.getLogger(ListOfExistingIssuesDAO.class);
	private boolean hasCreatedNewPrepareObj = false;	
	
	private boolean hist_isFirstTime = false;
	private ArrayList hist_deletedIssues = new ArrayList();
	private ArrayList hist_newIssues = new ArrayList();
	private String loggedUser = null;
	public void setLoggedUser(String lu)
	{
		loggedUser = lu;
	}
	public boolean bringIssues(ListOfExistingIssuesVO voList) {
		/*
		 * Hardcoded values are:
		 * status
		 * userid
		 */
		System.out.println("In prepare stage DAO");
		int nSelected = 0;
		System.out.println("voList="+voList);
		System.out.println("voList.getIssues()="+voList.getIssues());
		System.out.println("voList.getIssues().size()="+voList.getIssues().size());
		for(int loopVar = 0; loopVar < voList.getIssues().size(); loopVar ++)
		{
			System.out.println("loopVar = "+loopVar);
			OneIssueVO vo = (OneIssueVO)voList.getIssues().get(loopVar);
			System.out.println("vo = "+vo);
				if(vo.getSelectedIssue()!=null)nSelected++;
		}
		System.out.println("Outside the first loop");
		System.out.println("nSelected = "+nSelected);
		String[] issue_ids = new String[nSelected];
		int thisIssueIndex = 0;
		for(int loopVar = 0; loopVar < voList.getIssues().size(); loopVar ++)
		{
			OneIssueVO vo = (OneIssueVO)voList.getIssues().get(loopVar);
			if(vo.getSelectedIssue()!=null)
				issue_ids[thisIssueIndex++] = vo.getSelectedIssue();
		}
		
		System.out.println(".......Issues to be brought in are :");
		for(int i = 0 ; i< issue_ids.length; i++)
			System.out.println(issue_ids[i]);
		
		String project_id = voList.getProjectID();
		String wf_id = voList.getWorkflowID();
		String status = "N";
		String userid = "v2srikau@us.ibm.com";
		
		System.out.println("project_id=" + project_id);
		System.out.println("wf_id=" + wf_id);
		
	

		/////////////////////////////////////////
		DBAccess db = null;
		try {
			System.out.println("..........Creating new DBAccess object");
			db = new DBAccess();
			System.out.println("..........Created DBAccess object.");

			String wf_stage_id=null;
			String timestamp = new Timestamp(System.currentTimeMillis()).toString();
			String q =null;
			
			db.prepareDirectQuery("SELECT WF_STAGE_ID FROM ETS.WF_STAGE_PREPARE_SETMET WHERE WF_ID='"
					+ wf_id + "' with ur");

			System.out.println(".......prepareDirectQuery done.");
			System.out.println(".....Waiting for response from database.");
			int rows = db.execute();
			System.out.println(".....Recieved response from database\n"
								+ "Number of rows returned = " + rows);
			wf_stage_id = db.getString(0, 0);
			if(wf_stage_id==null)
			{
				wf_stage_id = ETSCalendar.getNewCalendarId();
				q = "INSERT INTO ETS.WF_STAGE_PREPARE_SETMET (PROJECT_ID,"
						+ "WF_ID," + "WF_STAGE_ID," + "STATUS,"
						+ "LAST_USERID," + "LAST_TIMESTAMP) " + "VALUES(" + "'"
						+ project_id + "'," + "'" + wf_id + "'," + "'"
						+ wf_stage_id + "'," + "'" + status + "'," + "'"
						+ userid + "'," + "'" + timestamp + "')";
				db.prepareDirectQuery(q);
				System.out.println(".......prepareDirectQuery done.");
				System.out.println("DB:" + q);
				rows = db.execute();
				System.out.println("DB: done inserting");
				db.doCommit();
				hasCreatedNewPrepareObj = true;
				hist_isFirstTime = true;
			}
			else
			{
				q="SELECT issue_id FROM ETS.WF_PREPARE_PREVIOUS_ISSUES where wf_id='"+wf_id+"' with ur";
				db.prepareDirectQuery(q);
				rows=db.execute();
				for(int g=0;g<rows;g++)
				{
					String thisIssue = db.getString(g,0);
					boolean isUnchanged = false;
					for(int gg = 0 ; gg< issue_ids.length; gg++)
							if(thisIssue.equals(issue_ids[gg]))
								isUnchanged =true;
					if(!isUnchanged)hist_deletedIssues.add(thisIssue);
				}
				for(int g = 0 ; g< issue_ids.length; g++)
				{
					boolean isPresent = false;
					for(int gg=0; gg<rows;gg++)
					{
						if(db.getString(gg,0).equals(issue_ids[g]))
							isPresent=true;
					}
					if(!isPresent)hist_newIssues.add(issue_ids[g]);
				}
				q="DELETE FROM ETS.WF_PREPARE_PREVIOUS_ISSUES where wf_id='"+wf_id+"'";
				System.out.println("DB:"+q);
				db.prepareDirectQuery(q);
				rows=db.execute();
				db.doCommit();
				hist_isFirstTime = false;
				System.out.println("DB: done");
			}
			
			
			for (int i = 0; i < issue_ids.length; i++) {
				System.out.println("Bringing issue with ISSUE_ID="
						+ issue_ids[i]);
 
				q = "INSERT INTO ETS.WF_PREPARE_PREVIOUS_ISSUES (PROJECT_ID, WF_ID, WF_STAGE_ID, ISSUE_ID) VALUES('"
						+ project_id
						+"','"
						+ wf_id
						+ "','"
						+ wf_stage_id
						+ "','"
						+ issue_ids[i]
						+ "')";
				db.prepareDirectQuery(q);

				System.out.println(q);
				System.out.println("....insert query ready");
				System.out
						.println(".....Waiting for database to insert the issue.");
				db.execute();
				db.doCommit();
				System.out.println("....Database finished inserting.");
			}
			String histID = null;
			if(hist_isFirstTime)
				histID=HistoryUtils.enterHistory(project_id,wf_id,wf_stage_id,HistoryUtils.ACTION_NEW_PREPARE_STAGE,loggedUser,"Set/Met Modified",db);
			else
				histID=HistoryUtils.enterHistory(project_id,wf_id,wf_stage_id,HistoryUtils.ACTION_EDIT_PREPARE_STAGE,loggedUser,"Set/Met Modified",db);
			HistoryUtils.setSecondaryHistory(histID,project_id,hist_deletedIssues,hist_newIssues,db);
			db.doCommit();
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
	//TODO: 01 Hardcoded document here..
	/*DetailsUtils d = new DetailsUtils();
	d.setProjectID(project_id);
	d.setWorkflowID(wf_id);
	d.extractWorkflowDetails();
	if(d.getWwf_curr_stage_name()!=null && d.getWwf_curr_stage_name().equalsIgnoreCase("prepare"))
	{
	System.out.println("Updating stage name to document");
	try{
		db = new DBAccess();
		db.prepareDirectQuery("update ets.wf_def set WF_CURR_STAGE_NAME='document' where project_id='"+project_id+"' and wf_id='"+wf_id+"'");
		db.execute();
		db.doCommit();
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
	}*/
	
		System.out.println(".....Reurning from DAO");
		/////////////////////////////////////////
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObject(oem.edge.ets.fe.workflow.core.WorkflowObject)
	 */
	public boolean saveWorkflowObject(WorkflowObject workflowObject) {
		// TODO Auto-generated method stub
		return false;
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
	public boolean getHasCreatedNewPrepareObj() {
		return hasCreatedNewPrepareObj;
	}

}
