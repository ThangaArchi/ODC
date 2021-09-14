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
package oem.edge.ets.fe.acmgt.model;

import java.sql.SQLException;

import oem.edge.ets.fe.ETSBoardingUtils;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.acmgt.bdlg.AddMembrProcDataPrep;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WrkSpcTeamWM extends WrkSpcTmMembrRole {

	private static Log logger = EtsLogger.getLogger(WrkSpcTeamWM.class);

	private AddMembrToWrkSpcDAO wrkSpcDao;
	private AddMembrProcDataPrep addMmbrBdlg;
	public static final String VERSION = "1.9";

	/**
	 * @param sLoginUserId
	 */
	public WrkSpcTeamWM(String sLoginUserId) {
		super(sLoginUserId);
		wrkSpcDao = new AddMembrToWrkSpcDAO();
		addMmbrBdlg = new AddMembrProcDataPrep();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.acmgt.model.WrkSpcTmMembrRole#processAddMembrRequest(oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel, oem.edge.ets.fe.acmgt.model.WrkSpcTeamObjKey)
	 */
	public WrkSpcTeamActionsOpModel processAddMembrRequest(WrkSpcTeamActionsInpModel actInpModel, WrkSpcTeamObjKey teamObjKey) throws SQLException, Exception {
		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();

		String userId = teamObjKey.getSIRUserId(); //IR USER ID

		//get the latest proj info
		ETSProj wrkSpc = teamObjKey.getWrkSpc();

		//extract user dets
		ETSUserDetails etsUserDets = teamObjKey.getEtsUserIdDets();

		//extract requestor dets
		ETSUserDetails reqUserDets = teamObjKey.getReqUserIdDets();

		//extract owner info for the given wrk spc
		ETSUserDetails ownerDets = teamObjKey.getWrkSpcOwnrDets();

		logger.debug("USER ID IN WM==" + etsUserDets.getWebId());
		logger.debug("USER ID TYPE IN WM==" + etsUserDets.getUserType());
		logger.debug("REQUESTOR USER ID IN WM==" + reqUserDets.getWebId());
		logger.debug("OWNER USER ID IN WM==" + ownerDets.getWebId());

		// check if the user is external or not...
		if (etsUserDets.getUserType() == etsUserDets.USER_TYPE_EXTERNAL) {

			//the person logged in is a workspace manager.. so create a request..

			ETSUserAccessRequest uar = new ETSUserAccessRequest();

			logger.debug(" START RECORD REQUEST FOR EXTERNAL USER BY WM TO WO");

			//record request
			uar.recordRequest(userId, wrkSpc.getName(), ownerDets.getEMail(), reqUserDets.getWebId(),wrkSpc.getProjectType());

			logger.debug("END RECORD REQUEST FOR EXTERNAL USER BY WM TO WO");
			
			logger.debug("START updating invite table");

			actOpModel = addMmbrBdlg.updateInviteStatTab(actInpModel, "R");

			logger.debug("END updating invite table");

			logger.debug("START LOGGING INTO METRICS BY WM");
			//log into metrics
			addMmbrBdlg.LogIntoMetrics(teamObjKey.getWrkSpc(), teamObjKey.getReqstrIRUserId(), "Team_Add_Request");

			logger.debug("END LOGGING INTO METRICS BY WM");

			logger.debug("START SEND MGR MAIL");

			//send mail
			boolean sent = ETSBoardingUtils.sendManagerEMail(etsUserDets, ownerDets.getEMail(), teamObjKey.getWrkSpc(), reqUserDets.getEMail());

			logger.debug("END SEND MGR MAIL");

			actOpModel.setRetCode("REQUESTED_FORWARDED_TO_WO");
			actOpModel.setRetCodeMsg("A request has been forwarded to the workspace owner because this user is new and requires additional processing.");

		}

		if (etsUserDets.getUserType() == etsUserDets.USER_TYPE_INTERNAL) {

			logger.debug("START submitEntAddMembrToWrkSpc IN WM");

			IccWrkSpcStatusModel compStatModel = addMmbrBdlg.submitEntAddMembrToWrkSpc(actInpModel, teamObjKey);

			logger.debug("START submitEntAddMembrToWrkSpc in WM");

			logger.debug("START updating invite table");

			actOpModel = addMmbrBdlg.updateInviteStatTab(compStatModel, actInpModel, teamObjKey);

			logger.debug("END updating invite table");

			logger.debug("START sendMailProcess");

			actOpModel = addMmbrBdlg.sendMailProcess(compStatModel, actInpModel, teamObjKey);

			logger.debug("END sendMailProcess");

		}

		return actOpModel;
	}

}
