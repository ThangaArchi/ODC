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

package oem.edge.ets.fe.workflow.newissue;

import java.util.ArrayList;

import javax.mail.MessagingException;

import oem.edge.ets.fe.SMTPMail;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.notification.IssueNotificationParams;
import oem.edge.ets.fe.workflow.notification.NotificationConstants;
import oem.edge.ets.fe.workflow.notification.NotifierFactory;
import oem.edge.ets.fe.workflow.notification.Notifier;

import org.apache.commons.logging.Log;


/**
 * Class       : NewIssueBL
 * Package     : oem.edge.ets.fe.workflow.newissue
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewIssueBL {

	private static Log logger = WorkflowLogger.getLogger(NewIssueBL.class);
	public String acOwner = null;
	
	/**
	 * @param vo
	 * @param loggedUser
	 * @param tc
	 * @return
	 */
	public boolean saveNewIssue(NewIssueVO vo, String loggedUser, String tc) {
		NewIssueDAO dao = new NewIssueDAO();
		dao.loggedUser = loggedUser;
		dao.saveWorkflowObject(vo);
		acOwner = dao.acOwner;
		return true;
	}

	/**
	 * @param vo
	 * @return
	 */
	public boolean isIssueQuotaExhausted(NewIssueVO vo)	{

		if ((new NewIssueDAO()).getIssueCount(vo) < 5)
			return false;
		else
			return true;
	}

	//Added by Rajesh, in 7.1.1
	public boolean isIssueQuotaExhausted(String wf_type,NewIssueVO vo)
	{
	if(wf_type.equals("QBR")||wf_type.equals("SELF ASSESSMENT"))
		return false;
	else
		return isIssueQuotaExhausted(vo);
	}
	

	/**
	 * @param string
	 * @return
	 */
	public String getName(String id) {
		String name = null;
		
		DBAccess db = null;
		try {
			db=new DBAccess();
			ETSUserDetails u = new ETSUserDetails();
			u.setWebId(id);
			u.extractUserDetails(db.getConnection());
			name = u.getFirstName() + " "+ u.getLastName();
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
					
				}catch(Exception ex){
					
				}
				return null;
			}
		}
		return name;
	}
}
