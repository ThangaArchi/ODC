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

import oem.edge.ets.fe.workflow.stage.PrepareStageObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : ListOfExistingIssuesVO
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class OneIssueVO extends PrepareStageObject {

	
	private static Log logger = WorkflowLogger.getLogger(OneIssueVO.class);
	private String selectedIssue = null;
	
	private String issueID = null;
	
	
	/**
	 * Get selectedIssue
	 * 
	 * @return String[]
	 */
	public String getSelectedIssue() {
		return selectedIssue;
		
	}

	/**
	 * Set selectedIssue
	 * 
	 * @param <code>String</code>
	 */
	public void setSelectedIssue(String s) {
		this.selectedIssue = s;
	}

	public void reset()
	{
		
		selectedIssue = null;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowStage#getWorkflowType()
	 */
	public String getWorkflowType() {
		
		return "SETMET";
	}
	public String getIssueID() {
		return issueID;
	}
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}

	
}
