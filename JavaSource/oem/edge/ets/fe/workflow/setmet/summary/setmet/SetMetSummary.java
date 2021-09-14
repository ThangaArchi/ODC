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

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : SetMetSummary
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.setmet
 * Description : 
 * Date		   : Nov 20, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class SetMetSummary {
	private static Log logger = WorkflowLogger.getLogger(SetMetSummary.class);
	
	
	private String loggedUser = null;
	private IdentifyDetails identify = null;
	private PrepareDetails prepare = null;
	private ScorecardDetails scorecard = null;
	private IssuesDetails issues = null;
	
	private String projectID = null;
	private String workflowID = null;
	
	public SetMetSummary(String projectID, String workflowID, String company) throws Exception {
	
		DBAccess db = null;
		db = new DBAccess();
		
		if(!MiscUtils.isValidProject(projectID,db) || !MiscUtils.isValidWorkflow(projectID, workflowID, db) )
		{
			db.close();
			db = null;
			throw new Exception("Invalid projectID or workflowID");
		}
		identify = new IdentifyDetails(db,projectID, workflowID);
		prepare = new PrepareDetails(db,projectID, workflowID);
		scorecard =new ScorecardDetails(db,projectID, workflowID, company);
		issues = new IssuesDetails(db,projectID, workflowID);
		db.close();
		db = null;
	}
	
	
	/**
	 * @return Returns the identify.
	 */
	public IdentifyDetails getIdentify() {
		return identify;
	}
	/**
	 * @param identify The identify to set.
	 */
	public void setIdentify(IdentifyDetails identify) {
		this.identify = identify;
	}
	/**
	 * @return Returns the issues.
	 */
	public IssuesDetails getIssues() {
		return issues;
	}
	/**
	 * @param issues The issues to set.
	 */
	public void setIssues(IssuesDetails issues) {
		this.issues = issues;
	}
	/**
	 * @return Returns the loggedUser.
	 */
	public String getLoggedUser() {
		return loggedUser;
	}
	/**
	 * @param loggedUser The loggedUser to set.
	 */
	public void setLoggedUser(String loggedUser) {
		this.loggedUser = loggedUser;
	}
	/**
	 * @return Returns the prepare.
	 */
	public PrepareDetails getPrepare() {
		return prepare;
	}
	/**
	 * @param prepare The prepare to set.
	 */
	public void setPrepare(PrepareDetails prepare) {
		this.prepare = prepare;
	}
	/**
	 * @return Returns the projectID.
	 */
	public String getProjectID() {
		return projectID;
	}
	/**
	 * @param projectID The projectID to set.
	 */
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	/**
	 * @return Returns the scorecard.
	 */
	public ScorecardDetails getScorecard() {
		return scorecard;
	}
	/**
	 * @param scorecard The scorecard to set.
	 */
	public void setScorecard(ScorecardDetails scorecard) {
		this.scorecard = scorecard;
	}
	/**
	 * @return Returns the workflowID.
	 */
	public String getWorkflowID() {
		return workflowID;
	}
	/**
	 * @param workflowID The workflowID to set.
	 */
	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}
}

