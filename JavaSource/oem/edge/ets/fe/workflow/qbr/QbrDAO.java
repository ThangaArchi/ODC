/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.qbr;

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.HistoryUtils;

import org.apache.commons.logging.Log;

/**
 * Class       : QbrDAO
 * Package     : oem.edge.ets.fe.workflow.qbr
 * Description : 
 * Date		   : Feb 13, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class QbrDAO {
	private static Log logger = WorkflowLogger.getLogger(QbrDAO.class);
	public static void moveStage(String projectID, String workflowID, String oldStage, String newStage, String loggedUser)
	{
		DBAccess db = null;
		try{
			db = new DBAccess();
			String q = "update ets.wf_def set WF_CURR_STAGE_NAME='"+newStage+"' where project_id='"+projectID+"' and wf_id='"+workflowID+"'";
			db.prepareDirectQuery(q);
			db.execute();
			String historyID = HistoryUtils.enterHistory(projectID,workflowID,workflowID,HistoryUtils.ACTION_GENERIC_SETMET,loggedUser,"Workflow moved into Prepare Stage",db);
			HistoryUtils.addHistoryField(historyID,projectID,"Workflow Stage","Identify","Prepare",db);
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
	}
}

