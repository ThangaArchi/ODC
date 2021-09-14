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


package oem.edge.ets.fe.workflow.setmet.summary.setmet;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : PrepareDetails
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.setmet
 * Description : 
 * Date		   : Nov 20, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class PrepareDetails{
	private static Log logger = WorkflowLogger.getLogger(PrepareDetails.class);
	private ArrayList issueID = new ArrayList();
	private ArrayList issueTitle = new ArrayList();
	
	/**
	 * @param db
	 */
	public PrepareDetails(DBAccess db, String projectID, String workflowID) {
		
		
		try {
			db.prepareDirectQuery("select a.issue_id, b.issue_title from ets.wf_prepare_previous_issues a, ets.wf_issue b where a.issue_id = b.issue_id and a.project_id='"+projectID+"' and a.wf_id='"+workflowID+"' with ur");
			int r = db.execute();
			for(int i = 0; i < r; i++)
			{
			
				issueID.add(db.getString(i,0));
				issueTitle.add(db.getString(i,1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * @return Returns the issueID.
	 */
	public ArrayList getIssueID() {
		return issueID;
	}
	/**
	 * @param issueID The issueID to set.
	 */
	public void setIssueID(ArrayList issueID) {
		this.issueID = issueID;
	}
	/**
	 * @return Returns the issueTitle.
	 */
	public ArrayList getIssueTitle() {
		return issueTitle;
	}
	/**
	 * @param issueTitle The issueTitle to set.
	 */
	public void setIssueTitle(ArrayList issueTitle) {
		this.issueTitle = issueTitle;
	}
}

