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

package oem.edge.mailpref;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import oem.edge.amt.AMTException;
import oem.edge.common.Global;
import oem.edge.ets.fe.acmgt.actions.InviteMembrToWrkSpcIF;
import oem.edge.ets.fe.acmgt.bdlg.WrkSpcActiveFlagImpl;
import oem.edge.ets.fe.acmgt.dao.InvMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcMailHandler;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.UserInviteStatusModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsOpModel;
import oem.edge.ets.fe.acmgt.resources.WrkSpcTeamConstantsIF;
import oem.edge.ets.fe.acmgt.wrkflow.InviteMembrToEtsWrkSpcImpl;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * This class will take care of ETS INVITE USER PROC JOB .
 *
 */

public class EtsTeamInviteJob implements JobInterface, WrkSpcTeamConstantsIF {

	public static final String VERSION = "1.4";

	private static Log logger = EtsLogger.getLogger(EtsTeamInviteJob.class);

	/**
	 * Constructor.
	 */

	public EtsTeamInviteJob() {

		super();
	}

	public String JobImplementation(Connection conn, String user, String func, String division, String start_date, String start_time, String end_date, String end_time) {

		//run invite job
		runTeamInviteJob();

		///active flag job//
		runActiveFlagJob();

		return null;

	}

	public String mailFormat(String str, Connection conn, String user, String function, String division, String tag) throws SQLException {
		return null;
	}

	public void runTeamInviteJob() {

		InviteMembrToWrkSpcIF invMmbrIF = new InviteMembrToEtsWrkSpcImpl();

		WrkSpcTeamActionsOpModel opModel = new WrkSpcTeamActionsOpModel();
		WrkSpcTeamActionsInpModel actInpModel = new WrkSpcTeamActionsInpModel();
		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();

		try {

			logger.debug("START  run time for invite job:::" + Global.getCurrentDate());

			logger.debug("getting all invited recs");
			//get the recs to be processed
			InvMembrToWrkSpcDAO invDao = new InvMembrToWrkSpcDAO();
			ArrayList invList = invDao.getAllInviteMembersList();
			int invsize = 0;

			if (invList != null && !invList.isEmpty()) {

				invsize = invList.size();
			}

			logger.debug("INVITE RECS LIST SIZE===" + invsize);

			mn : for (int i = 0; i < invsize; i++) {

				try {

					logger.debug("START  run time for invite rec " + i + ":::" + Global.getCurrentDate());

					actInpModel = WrkSpcTeamUtils.transFormToInpModel((UserInviteStatusModel) invList.get(i));

					opModel = invMmbrIF.inviteMemberToWrkSpc(actInpModel);

					logger.debug("END  run time for invite rec " + i + ":::" + Global.getCurrentDate());

				} catch (SQLException sqlEx) {

					logger.error("SQLException in while processing each record in Invite Job", sqlEx);
					String mailMsg = "SQLException in processing the rec ::" + actInpModel.getUserId() + "::" + actInpModel.getWrkSpcId();
					mailHandler.sendMailOnErrorToSupp("ERROR IN INVITE JOB", mailMsg);
					sqlEx.printStackTrace();

				} catch (Exception ex) {

					logger.error("SQLException in while processing each record in Invite Job", ex);
					String mailMsg = "Exception in processing the rec ::" + actInpModel.getUserId() + "::" + actInpModel.getWrkSpcId();
					mailHandler.sendMailOnErrorToSupp("ERROR IN INVITE JOB", mailMsg);
					ex.printStackTrace();
				}

			} //end of mn

			logger.debug("END run time for invite job:::" + Global.getCurrentDate());

		} catch (SQLException sqlEx) {

			logger.fatal("SQLException in EtsTeamtInviteJob", sqlEx);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN INVITE JOB", "FATAL SQL Exception in start of INVITE JOB");
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.fatal("SQLException in EtsTeamtInviteJob", ex);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN INVITE JOB", "Exception in start of INVITE JOB");
			ex.printStackTrace();
		}

	}

	/**
	 * 
	 *
	 */

	public void runActiveFlagJob() {

		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
		WrkSpcActiveFlagImpl actFlagImpl = new WrkSpcActiveFlagImpl();

		try {

			logger.debug("START  run time for active flag job:::" + Global.getCurrentDate());

			actFlagImpl.updateWrkSpcUsers();

			logger.debug("END run time for active flag job:::" + Global.getCurrentDate());

		} catch (AMTException amtEx) {

			logger.fatal("AMTException in EtsActiveFlagJob", amtEx);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN ACTIVE FLAG JOB", "FATAL AMT Exception in start of ACTIVE FLAG JOB");
			amtEx.printStackTrace();

		} catch (SQLException sqlEx) {

			logger.fatal("SQLException in EtsActiveFlagJob", sqlEx);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN ACTIVE FLAG JOB", "FATAL SQL Exception in start of ACTIVE FLAG JOB");
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.fatal("SQLException in EtsActiveFlagJob", ex);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN ACTIVE FLAG JOB", "Exception in start of ACTIVE FLAG JOB");
			ex.printStackTrace();
		}
	} //end of active flag job

} //end of class