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

import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : ListOfExistingIssuesBL
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class ListOfExistingIssuesBL {

	private static Log logger = WorkflowLogger.getLogger(ListOfExistingIssuesBL.class);
	private boolean hasCreatedNewPrepareObj = false;
	private String loggedUser = null;
	public void setLoggedUser(String lu)
	{
		loggedUser = lu;
	}
	public boolean bringOldIssues(ListOfExistingIssuesVO vo)
	{
		ListOfExistingIssuesDAO dao = new ListOfExistingIssuesDAO();
		dao.setLoggedUser(loggedUser);
		dao.bringIssues(vo);
		hasCreatedNewPrepareObj = dao.getHasCreatedNewPrepareObj();
		return true;
	}
	
	public boolean getHasCreatedNewPrepareObj() {
		return hasCreatedNewPrepareObj;
	}
}
