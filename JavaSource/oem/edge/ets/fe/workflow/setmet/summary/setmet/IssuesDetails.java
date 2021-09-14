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

import java.sql.SQLException;
import java.util.ArrayList;

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.dao.WorkflowDBException;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : IssuesDetails
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.setmet
 * Description : 
 * Date		   : Nov 20, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssuesDetails{
	private static Log logger = WorkflowLogger.getLogger(IssuesDetails.class);
	private ArrayList rows = new ArrayList(); //contains IssuesRow objects.
	
	/**
	 * @param db
	 */
	public IssuesDetails(DBAccess db, String projectID, String workflowID) {
		
		IssuesRow row = null;
		
		try {
			db.prepareDirectQuery("SELECT a.ISSUE_ID, a.ISSUE_TITLE, a.STATUS FROM ETS.WF_ISSUE a, ets.wf_issue_wf_map b WHERE b.PROJECT_ID = '"+projectID+"' AND b.WF_ID ='"+workflowID+"' and a.issue_id=b.issue_id with ur");
			int r = db.execute();
			for(int i = 0; i < r; i++)
			{
				row = new IssuesRow();
				row.setIssueNum(db.getString(i,0));
				row.setTitle(db.getString(i,1));
				row.setAction(db.getString(i,2));

				rows.add(row);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	/**
	 * @return Returns the rows.
	 */
	public ArrayList getRows() {
		return rows;
	}
	/**
	 * @param rows The rows to set.
	 */
	public void setRows(ArrayList rows) {
		this.rows = rows;
	}
}

