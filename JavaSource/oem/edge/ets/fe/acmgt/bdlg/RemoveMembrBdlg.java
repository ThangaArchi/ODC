/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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
package oem.edge.ets.fe.acmgt.bdlg;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.UserObject;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.acmgt.dao.WrkSpcInfoDAO;
import oem.edge.ets.fe.acmgt.model.RemoveMembrModel;
import oem.edge.ets.fe.clientvoice.CVReplaceMemberDAO;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.TasksHelper;
import oem.edge.ets.fe.ismgt.bdlg.IssueRemoveMembrBdlg;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.dao.IssueInfoDAO;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemoveMembrBdlg {

	private static Log logger = EtsLogger.getLogger(RemoveMembrBdlg.class);

	public static final String VERSION = "1.4";

	private IssueRemoveMembrBdlg issueBdlg;
	private CVReplaceMemberDAO replDao;

	/**
	 * 
	 */
	public RemoveMembrBdlg() {
		super();
		issueBdlg = new IssueRemoveMembrBdlg();
		replDao = new CVReplaceMemberDAO();

	}

	/**
	 * 
	 * @param projectId
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public List getIssuesRecsForRemoveMember(String projectId, String userId) throws SQLException, Exception {

		return issueBdlg.getIssuesRecsForRemoveMember(projectId, userId);

	}

	/**
	 * 
	 * @param projectId
	 * @param isProjBladeType
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public List getProjMembrListWithoutVisitors(String projectId, boolean isProjBladeType, String remUserIrId) throws SQLException, Exception {

		EtsProjMemberDAO projDAO = new EtsProjMemberDAO();

		List projMemList = projDAO.getProjMemberListWithIrId(projectId, isProjBladeType);

		String remEdgeUserId = projDAO.getUserObjectIrId(remUserIrId).gUSERN;

		String decafUserType = projDAO.getDecafUserType(remEdgeUserId);

		logger.debug("remove user type===" + decafUserType);

		int projsize = 0;
		String etsUserIrId = "";
		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";

		List userTypeList = new ArrayList();

		if (projMemList != null && !projMemList.isEmpty()) {

			projsize = projMemList.size();

		}

		for (int i = 0; i < projsize; i = i + 5) {

			etsUserEdgeId = (String) projMemList.get(i);
			etsUserNameWithIrId = (String) projMemList.get(i + 1);
			etsUserType = (String) projMemList.get(i + 2);
			etsUserIrId = (String) projMemList.get(i + 4);
			
			if(!remEdgeUserId.equals(etsUserEdgeId)) {//exclude the current userid to be removed
			

			if (decafUserType.equals("I")) {

				if (etsUserType.equals("I")) {

					userTypeList.add(etsUserIrId);
					userTypeList.add(etsUserNameWithIrId);

				}

			} else if (decafUserType.equals("E")) {

				userTypeList.add(etsUserIrId);
				userTypeList.add(etsUserNameWithIrId);

			} else {

				userTypeList.add(etsUserIrId);
				userTypeList.add(etsUserNameWithIrId);

			}
			
			}//exclude the current userid to be removed

		} //end of for

		return userTypeList;

	}

	/**
		 * 
		 * @param projectId
		 * @param userId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public List getTaskRecsForRemoveMember(String projectId, String userId) throws SQLException, Exception {

		return TasksHelper.getOpenTasksForUser(userId, projectId);

	}

	/**
	 * 
	 * @param projectId
	 * @param userId
	 * @return
	 * @throws Exception
	 */

	public List getClientVoiceForRemoveMembr(String projectId, String userId) throws Exception {

		return replDao.getMemberAssignmentDetails(projectId, userId);

	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public UserObject getRemoveUserDets(String userId) throws SQLException, Exception {

		EtsProjMemberDAO projDAO = new EtsProjMemberDAO();

		return projDAO.getUserObjectIrId(userId);

	}

	/**
	 * 
	 * @param params
	 * @param projectId
	 * @param oldUserId
	 * @param lastUserId
	 * @return
	 */

	public RemoveMembrModel replaceUserId(Hashtable params, ETSProj Project, String oldUserId, String lastUserId, String lastIrUserId) throws Exception {

		RemoveMembrModel remModel = new RemoveMembrModel();

		String projectId = Project.getProjectId();

		remModel.setChangePrimCntct(true);
		remModel.setChangeIssues(true);
		remModel.setChangeTasks(true);
		remModel.setChangeClients(true);
		remModel.setRemovemem(false);

		String primuser = AmtCommonUtils.getTrimStr((String) params.get("primuser"));
		String issueuser = AmtCommonUtils.getTrimStr((String) params.get("issueuser"));
		String taskuser = AmtCommonUtils.getTrimStr((String) params.get("taskuser"));
		String clientuser = AmtCommonUtils.getTrimStr((String) params.get("clientuser"));

		//update primary

		if (AmtCommonUtils.isResourceDefined(primuser)) {

			EtsProjMemberDAO projDao = new EtsProjMemberDAO();
			remModel.setChangePrimCntct(projDao.updatePrimaryContact(primuser, projectId));

		}

		//update issues//

		if (AmtCommonUtils.isResourceDefined(issueuser)) {

			IssueInfoDAO issueDao = new IssueInfoDAO();
			remModel.setChangeIssues(issueDao.updateIssuesOnRemMembr(projectId, oldUserId, issueuser, lastUserId));

		}

		//update tasks//

		if (AmtCommonUtils.isResourceDefined(taskuser)) {

			remModel.setChangeTasks(TasksHelper.transferTasks(projectId, oldUserId, taskuser));

		}

		//update client

		if (AmtCommonUtils.isResourceDefined(clientuser)) {

			remModel.setChangeClients(replDao.replaceMember(projectId, oldUserId, clientuser));

		}

		//get msgs

		if (!remModel.isChangePrimCntct()) {

			remModel.setPrimCntctErrStr("There is some error in updating the primary contact with new user id. Please try removing the member later.");
		}

		if (!remModel.isChangeIssues()) {

			remModel.setIssueErrMsgStr("There is some error in updating the issue records with new user id. Please try removing the member later.");
		}

		if (!remModel.isChangeTasks()) {

			remModel.setIssueErrMsgStr("There is some error in updating the task records with new user id. Please try removing the member later.");
		}

		if (!remModel.isChangeClients()) {

			remModel.setIssueErrMsgStr("There is some error in updating the client voice records with new user id. Please try removing the member later.");
		}

		//if all true delete member
		if (remModel.isChangePrimCntct() && remModel.isChangeIssues() && remModel.isChangeTasks() && remModel.isChangeClients()) {

			remModel.setRemovemem(deleteMember(oldUserId, Project, lastIrUserId));
		}

		if (!remModel.isRemovemem()) {

			remModel.setRemoveMembrMsg("Error occurred while removing member " + oldUserId + ".  Please try again.");

		} else {

			remModel.setRemoveMembrMsg("Userid: <b>" + oldUserId + "</b> has been removed from this workspace.");
		}

		return remModel;
	}

	/**
	 * 
	 * @param userid
	 * @param Project
	 * @param lastIrUserId
	 * @return
	 */

	public boolean deleteMember(String userid, ETSProj Project, String lastIrUserId) {

		WrkSpcInfoDAO wrkDao = new WrkSpcInfoDAO();

		return wrkDao.deleteMember(userid, Project, lastIrUserId);

	}
	
	/**
	 * 
	 * @param projId
	 * @param userId
	 * @return 
	 */
	
	public Vector getSubWrkSpcsForUser(String projId,String userId){
		
		WrkSpcInfoDAO wrkDao = new WrkSpcInfoDAO();
		return wrkDao.getSubWrkSpcsForUser(projId,userId);
	}
	
	/**
	 * 
	 * @param projId
	 * @return 
	 */
	
	public String getSubWsName(String projId)throws SQLException, Exception {
		
		WrkSpcInfoDAO wrkDao = new WrkSpcInfoDAO();
		return wrkDao.getSubWsName(projId);
		
	}


} //end of class
