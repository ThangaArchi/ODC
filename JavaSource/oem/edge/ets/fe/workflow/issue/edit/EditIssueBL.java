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


package oem.edge.ets.fe.workflow.issue.edit;

import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;

import oem.edge.ets.fe.workflow.util.HistoryUtils;
import oem.edge.ets.fe.workflow.util.Notifier;
import java.util.ArrayList;

/**
 * Class       : EditIssueBL
 * Package     : oem.edge.ets.fe.workflow.issue.edit
 * Description : 
 * Date		   : Oct 11, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class EditIssueBL {
	private static Log logger = WorkflowLogger.getLogger(EditIssueBL.class);
	public void modifyIssue(EditIssueVO vo, String loggedUser, String tc)
	{
		//if(this person is allowed to modify)
		EditIssueDAO dao = new EditIssueDAO();
		dao.loggedUser = loggedUser;
		System.out.println("WorkflowID is "+vo.getWorkflowID());
		
		dao.saveWorkflowObject(vo);
		System.out.println("WorkflowID is "+vo.getWorkflowID());
		String fromEmail = null;
		ArrayList toEmail = dao.getNewOwners();
		ArrayList deletedOwnersToEmail = dao.getDelOwners();
		DBAccess db = null;
		try {
			db = new DBAccess();
			
			ETSUserDetails u = new ETSUserDetails();
			u.setWebId(loggedUser);
			u.extractUserDetails(db.getConnection());
			fromEmail = u.getEMail();
			
			for(int i = 0; i < toEmail.size(); i++)
			{
				u = new ETSUserDetails();
				u.setWebId((String)toEmail.get(i));
				u.extractUserDetails(db.getConnection());
				toEmail.set(i, u.getEMail());
			}
			for(int i = 0; i < deletedOwnersToEmail.size(); i++)
			{
				u = new ETSUserDetails();
				u.setWebId((String)deletedOwnersToEmail.get(i));
				u.extractUserDetails(db.getConnection());
				deletedOwnersToEmail.set(i, u.getEMail());
			}
			db.close();
			db = null;
		} catch (Exception e) {

			e.printStackTrace();
		}finally{
			if(db!=null)
			{
				try {
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				db=null;
			}
		}
		
		/*Notifier.editIssueNewOwnerOWNER(vo.getIssueID(),vo.getProjectID(), vo.getWorkflowID(),tc,fromEmail,toEmail);
		Notifier.editIssueDeletedOwnerOWNER(vo.getIssueID(),vo.getProjectID(), vo.getWorkflowID(),tc,fromEmail,deletedOwnersToEmail);*/
	}
	public void acceptIssue(EditIssueVO vo, String loggedUser)
	{
		String uid =vo.getUserid();
		String iid = vo.getIssueID();
		EditIssueDAO dao =new EditIssueDAO();
		dao.loggedUser = loggedUser;
		dao.modifyOwnership(vo,uid,iid,"ACCEPTED",vo.getDB());
	}
	public void rejectIssue(EditIssueVO vo, String loggedUser)
	{
		String uid =vo.getUserid();
		String iid = vo.getIssueID();
		EditIssueDAO dao =new EditIssueDAO();
		dao.loggedUser = loggedUser;
		dao.modifyOwnership(vo,uid,iid,"REJECTED",vo.getDB());
		
	}
	/**
	 * @param issueVO
	 * @param string
	 */
	public void modifyState(EditIssueVO vo, String string, String loggedUser) {
		EditIssueDAO dao =new EditIssueDAO();
		dao.loggedUser = loggedUser;
		(dao).modifyIssueStatus(vo,vo.getIssueID(),string,vo.getDB());
	}
	/**
	 * @param vo
	 * @param loggedUser
	 */
	public void addComment(EditIssueVO vo, String loggedUser) {
		HistoryUtils.enterHistory(vo.getProjectID(),vo.getWorkflowID(),vo.getIssueID(),HistoryUtils.ACTION_COMMENTS,loggedUser,vo.getComment(),vo.getDB());
	}
}

