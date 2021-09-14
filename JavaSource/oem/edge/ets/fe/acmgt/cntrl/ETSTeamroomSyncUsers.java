/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
package oem.edge.ets.fe.acmgt.cntrl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.AICUserDecafRole;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.acmgt.dao.TeamroomDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcMailHandler;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.documents.common.StringUtil;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ETSTeamroomSyncUsers {
	
	private static Log logger = LogFactory.getLog(ETSTeamroomSyncUsers.class);
	
	public void syncTeamRoomUsers () {
		TeamroomDAO teamroomDAO = new TeamroomDAO();
		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
		try {
			Connection conn = WrkSpcTeamUtils.getConnection();
			
			Vector projList = teamroomDAO.getAllTeamRoomProjects(conn);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Total no of AIC teamrooms::" + projList.size());
			}
			
			for (int iCount=0; iCount < projList.size(); iCount ++) {
				ETSProj proj = new ETSProj();
				proj = (ETSProj)projList.elementAt(iCount);
				String projectId = proj.getProjectId();
				if (logger.isDebugEnabled()) {
					logger.debug("SYNCING AIC TEAMROOM ::" + projectId);
				}
				syncUsersForProject(conn,proj);
			}
			ETSDBUtils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug("END of AIC TEAMROOM SYNC USERS");
			}
			
		
		}catch (Exception e){
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN TEAMROOM SYNC JOB", "Exception in teamroom sync JOB");
			e.printStackTrace();
		}
		
	}
	
	public void syncUsersForProject(Connection conn, ETSProj proj) {
		
		Vector vtActiveUsersFrmDecaf = null;
		Vector vtExisitingUsersInWrkspc = null;
		String strLastUserName = "ibmuser@us.ibm.com";
		TeamroomDAO teamroomDAO = new TeamroomDAO();
		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
		try {
			vtActiveUsersFrmDecaf = teamroomDAO.getAllUsersWithTeamroomEntData(conn,proj.getProjectId());
			
			vtExisitingUsersInWrkspc = teamroomDAO.getALLActiveUsersForWrkspc(conn,proj.getProjectId());
			
			Vector vtOldUsrList = new Vector();
			for (int i=0;i< vtExisitingUsersInWrkspc.size(); i++) {
				ETSUser usr = (ETSUser)vtExisitingUsersInWrkspc.elementAt(i);
				vtOldUsrList.add(usr.getUserId());
			}
			
			String strAddUser = StringUtil.EMPTY_STRING;
			String strRemoveUser = StringUtil.EMPTY_STRING;
			for (int iCountr = 0; iCountr < vtActiveUsersFrmDecaf.size(); iCountr++) {
				AICUserDecafRole usrDts = (AICUserDecafRole)vtActiveUsersFrmDecaf.elementAt(iCountr);
				if (vtOldUsrList.contains(usrDts.getUserId())) {
					//update the existing user
					teamroomDAO.updateUserRolesInWrkspc(conn,usrDts.getProjectId(),usrDts.getUserId(),usrDts.getRoleId());
				} else {
					//add this new user
					if (!strAddUser
							.equals(StringUtil.EMPTY_STRING))
							strAddUser =
									strAddUser
									+ ",('"
									+ usrDts.getUserId()
									+ "',"
									+ usrDts.getRoleId()
									+ ",'"
									+ usrDts.getProjectId()
									+ "','"
									+ StringUtil.EMPTY_STRING
									+ "','"
									+ "N"    // primary contact
									+ "','"
									+ StringUtil.EMPTY_STRING
									+ "','"
									+ strLastUserName
									+ "',current timestamp,'A')";
						else
							strAddUser =
									 "('"
									+ usrDts.getUserId()
									+ "',"
									+ usrDts.getRoleId()
									+ ",'"
									+ usrDts.getProjectId()
									+ "','"
									+ StringUtil.EMPTY_STRING
									+ "','"
									+ "N"    // primary contact
									+ "','"
									+ StringUtil.EMPTY_STRING
									+ "','"
									+ strLastUserName
									+ "',current timestamp,'A')";
				}
			}
			
			Vector vtDecafList = new Vector();
			for(int i=0; i< vtActiveUsersFrmDecaf.size(); i ++) {
				vtDecafList.add(
						((AICUserDecafRole)vtActiveUsersFrmDecaf.elementAt(i))
						.getUserId());
			}
			//create Remove String
			for (int i=0; i<vtOldUsrList.size();i++) {
				if(!vtDecafList.contains(vtOldUsrList.elementAt(i))) {
					if (!strRemoveUser.equals(StringUtil.EMPTY_STRING))
							strRemoveUser = strRemoveUser + ",'" + vtOldUsrList.elementAt(i) + "'";
						else
							strRemoveUser = "'" + vtOldUsrList.elementAt(i) + "'";
				}
			}
			if (!strRemoveUser.equals(StringUtil.EMPTY_STRING)){
				teamroomDAO.deleteUsersFrmWrkspc(conn,proj.getProjectId(),strRemoveUser);
			}
			if (!strAddUser.equals(StringUtil.EMPTY_STRING)) {
				teamroomDAO.addUsersToWrkspc(conn,strAddUser);
			}
			// update Workspace Owner Status in Workspace
			// Workspace Owner has to be made in Pending if he doesn't have entitlement
			
			Vector vtWrkspcOwner = teamroomDAO.getExistingWrkspcOwners(conn,proj.getProjectId());
			for (int i=0;i < vtWrkspcOwner.size(); i++) {
				String strUserID = (String)vtWrkspcOwner.elementAt(i);
				if (vtDecafList.contains(strUserID)) {
					teamroomDAO.updateWrkspcOwner(conn,proj.getProjectId(),strUserID,Defines.USER_ENTITLED);
				} else {
					teamroomDAO.updateWrkspcOwner(conn,proj.getProjectId(),strUserID,Defines.USER_PENDING);
				}
			}
			
		} catch (SQLException ex) {
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN TEAMROOM SYNC JOB", "SQL Exception while syncing wrkspc -" + proj.getProjectId() );
			ex.printStackTrace();
		}
		
		catch (Exception e) {
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN TEAMROOM SYNC JOB", "Exception while syncing wrkspc -" + proj.getProjectId() );
			e.printStackTrace();
		}
	}

}
