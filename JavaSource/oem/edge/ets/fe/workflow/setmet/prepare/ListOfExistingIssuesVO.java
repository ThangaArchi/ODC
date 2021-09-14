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

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.stage.PrepareStageObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : ListOfExistingIssuesVO
 * Package     : oem.edge.ets.fe.workflow.setmet.prepare
 * Description : 
 *
 * @author       Pradyumna Achar
 */
public class ListOfExistingIssuesVO extends PrepareStageObject {

	private static Log logger = WorkflowLogger.getLogger(ListOfExistingIssuesVO.class);
	private ArrayList issues = null;

public ArrayList getIssues() {
	return issues;
}
public void setIssues(ArrayList issues) {
	this.issues = issues;
}
public OneIssueVO getIssuesIndexed(int index)
{
	return (OneIssueVO)issues.get(index);
}
public void setIssuesIndexed(int index, OneIssueVO vo)
{
	issues.set(index,vo);
}

/* (non-Javadoc)
 * @see oem.edge.ets.fe.workflow.core.WorkflowStage#getWorkflowType()
 */
public String getWorkflowType() {
	
	return "SETMET";
}
public void reset()
{
	if(issues!=null)
	{
		for (int i = 0; i < issues.size(); i++) {
				if (issues.get(i) != null)
					((OneIssueVO) issues.get(i)).reset();
		}
	}
}
}
