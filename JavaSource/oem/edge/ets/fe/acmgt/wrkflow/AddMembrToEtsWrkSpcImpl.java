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
package oem.edge.ets.fe.acmgt.wrkflow;

import java.sql.SQLException;

import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.actions.AddMemberToWrkSpcIF;
import oem.edge.ets.fe.acmgt.bdlg.AddMembrProcDataPrep;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.UserIccStatusModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsOpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamMembrRoleFactoryIF;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamMembrRoleFactoryImpl;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamObjKey;
import oem.edge.ets.fe.acmgt.resources.WrkSpcTeamConstantsIF;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AddMembrToEtsWrkSpcImpl extends ActionMembrToWrkSpcAbsImpl implements AddMemberToWrkSpcIF, WrkSpcTeamConstantsIF {

	public static final String VERSION = "1.8";
	private static Log logger = EtsLogger.getLogger(AddMembrToEtsWrkSpcImpl.class);
	private AddMembrToWrkSpcDAO wrkSpcDao;
	private AddMembrProcDataPrep addMmbrBdlg;

	/**
	 * 
	 */
	public AddMembrToEtsWrkSpcImpl() {
		super();
		wrkSpcDao = new AddMembrToWrkSpcDAO();
		addMmbrBdlg = new AddMembrProcDataPrep();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
			 * @see oem.edge.ets.fe.acmgt.actions.AddMemberToWrkSpc#addMemberToWrkSpc(oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel)
			 */
	public WrkSpcTeamActionsOpModel addMemberToWrkSpc(WrkSpcTeamActionsInpModel actInpModel) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addMemberWrkFlow(WrkSpcTeamActionsInpModel actInpModel) throws SQLException, Exception {

		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();

		String userId = actInpModel.getUserId();
		String projectId = actInpModel.getWrkSpcId();

		//check if the user is defined in WORKSPACE

		if (isUserDefndInWrkSpc(projectId, userId)) {

			actOpModel.setRetCode("USERDEFNDINWRKSPC");
			actOpModel.setRetCodeMsg("IBM ID entered is either already a member of this workspace or the request to add this ID to workspace is pending. Please enter the IBM ID of user who is not a member of this workspace or pending.");

		} else { //if user id is not defined in ETS, then check in AMT-ICC to check if the user has an entry

			//check if the user is defined in AMT
			if (isUserDefndinICC(userId)) {

				UserIccStatusModel iccStatModel = wrkSpcDao.getUserIdStatusInICC(userId);

				int uidcount = iccStatModel.getAmtuidcount();
				int emailcount = iccStatModel.getAmtemailcount();

			} else { //when the user is not defined in AMT

				//if id exists in IR

				if (isUserIdDefndInUD(userId)) {

				} else { //if userid not defined in UD

					//invite the person
					//log the entry into tables for daemon processing

				}

			} //if user is not defined in ICC

		} //IF user not defined in ETS

	} //end of workflow

	/**
		* Method submitAddMember.
		* @param conn
		* @param out
		* @param sProjId
		* @param sIRId
		* @param req
		* @throws SQLException
		* @throws Exception
		*/
	public WrkSpcTeamActionsOpModel processAddMemberToWrkSpc(WrkSpcTeamActionsInpModel actInpModel) throws SQLException, Exception {

		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();

		try {

			String userId = actInpModel.getUserId();
			String projectId = actInpModel.getWrkSpcId();
			String requestorId = actInpModel.getRequestorId(); //IWO /WM R USERID
			
			logger.debug("RECORD PROCESSING START FOR :: USERID::PROJECTID::REQUESTORID::"+userId+"::"+projectId+"::"+requestorId);

			//get the team object key first
			WrkSpcTeamObjKey teamObjKey = getWrkSpcTeamObjKey(actInpModel);

			String userRole = WrkSpcTeamUtils.getUserRole(requestorId, projectId);
			
			//print the processing rec
			WrkSpcTeamUtils.printCompInvRecord(actInpModel,teamObjKey);
			

			//get the suitable role object
			WrkSpcTeamMembrRoleFactoryIF usrRoleFac = new WrkSpcTeamMembrRoleFactoryImpl();
			actOpModel = usrRoleFac.createWrkSpcTeamRole(userId, userRole).processAddMembrRequest(actInpModel, teamObjKey);
			
			logger.debug("ACT OP MODEL RET CODE in processAddMemberToWrkSpc:::"+actOpModel.getRetCode());
			logger.debug("ACT OP MODEL RET CODE MSG in processAddMemberToWrkSpc:::"+actOpModel.getRetCodeMsg());
			
			logger.debug("RECORD PROCESSING END FOR :: USERID::PROJECTID::REQUESTORID::"+userId+"::"+projectId+"::"+requestorId);

		} catch (Exception e) {
			throw e;
		}

		return actOpModel;

	}

} //end of class
