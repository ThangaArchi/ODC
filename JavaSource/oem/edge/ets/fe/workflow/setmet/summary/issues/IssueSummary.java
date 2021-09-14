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


package oem.edge.ets.fe.workflow.setmet.summary.issues;

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.issue.IssueHistoryBean;
import oem.edge.ets.fe.workflow.issue.IssueHistoryItem;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import oem.edge.ets.fe.workflow.util.HistoryUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC

/**
 * Class       : IssueSummary
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.issues
 * Description : 
 * Date		   : Dec 7, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssueSummary {
	private static Log logger = WorkflowLogger.getLogger(IssueSummary.class);
	private IssueTemplateTable table1 = null;
	private IssueTemplateTable  table2 = null;
	public IssueSummary(String projectID, String workflowID) {
		table1 = new IssueTemplateTable();
		table2 = new IssueTemplateTable();
		Table1Row r = null;
		Table2Row t = null;

		String q ="select issue_id from ets.wf_issue_wf_map where wf_id='"+workflowID+"' and project_id='"+projectID+"' with ur";
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(q);
			int rows = db.execute();
			for(int i=0; i< rows; i++)
			{
				r = new Table1Row();
				r.setItemNumber(db.getString(i,0));
				table1.getRows().add(r);
			}
			for(int i=0; i<table1.getRows().size(); i++)
			{
				Table1Row temp = (Table1Row)(table1.getRows().get(i));
				/*IssueHistoryBean h = HistoryUtils.getHistory(projectID, workflowID, (temp).getItemNumber(),db);
				for(int l = 0; l <h.getItems().size(); l++)
				{
				IssueHistoryItem hItem = ((IssueHistoryItem)(h.getItems().get(l)));
				if(l==0)
					temp.setActionDetails(hItem.getAction_taken()+" on "+hItem.getAction_date()+" by "+ hItem.getModified_by());
				else
					temp.setActionDetails(temp.getActionDetails()+"<br />"+hItem.getAction_taken()+" on "+hItem.getAction_date()+" by "+ hItem.getModified_by());
				}*/
				
				DetailsUtils d = new DetailsUtils();
				d.setIssueID(temp.getItemNumber());
				d.extractIssueDetails(db.getConnection());
				
				temp.setActionDetails(d.getIissue_desc()); //This will contain Issue Description
				temp.setItemNumber(d.getIissue_title()); //This will contain Issue Title
				
				t = new Table2Row();
				
				t.setFocalPt(d.getIissue_contact());
				t.setStatus(d.getIstatus());
				t.setActualDate(MiscUtils.reformatDate(d.getItarget_date()));
				t.setTargetDate(MiscUtils.reformatDate(d.getIinitial_target_date()));
				t.setType(d.getIissue_type());
				for(int j = 0; j < d.getIownerNames().size(); j++)
				{
					if(j==0)
						t.setOwners((String)d.getIownerNames().get(j));
					else
						t.setOwners(t.getOwners()+",<br />"+d.getIownerNames().get(j));
				}
				table2.getRows().add(t);	
			}
			
			db.close();
			db = null;
		}catch(Exception e)
		{
			db=null;
		}
		finally{
			if(db!=null)
			{
				try{
				db.close();
				}catch(Exception e){}
				db=null;
			}
		}
	}
	 
	
	/**
	 * @return Returns the table1.
	 */
	public IssueTemplateTable getTable1() {
		return table1;
	}
	/**
	 * @param table1 The table1 to set.
	 */
	public void setTable1(IssueTemplateTable table1) {
		this.table1 = table1;
	}
	/**
	 * @return Returns the table2.
	 */
	public IssueTemplateTable getTable2() {
		return table2;
	}
	/**
	 * @param table2 The table2 to set.
	 */
	public void setTable2(IssueTemplateTable table2) {
		this.table2 = table2;
	}
}

