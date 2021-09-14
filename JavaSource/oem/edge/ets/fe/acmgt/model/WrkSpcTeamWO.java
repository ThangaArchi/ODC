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

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.acmgt.bdlg.AddMembrProcDataPrep;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WrkSpcTeamWO extends WrkSpcTmMembrRole {

	private static Log logger = EtsLogger.getLogger(WrkSpcTeamWO.class);

	private AddMembrToWrkSpcDAO wrkSpcDao;
	private AddMembrProcDataPrep addMmbrBdlg;
	public static final String VERSION = "1.8";

	/**
	 * @param sLoginUserId
	 */
	public WrkSpcTeamWO(String sLoginUserId) {
		super(sLoginUserId);
		wrkSpcDao = new AddMembrToWrkSpcDAO();
		addMmbrBdlg = new AddMembrProcDataPrep();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
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

		UserWrkSpcStatusModel wrkSpcStatModel = new UserWrkSpcStatusModel();

		logger.debug("USER ID IN WO==" + etsUserDets.getWebId());
		logger.debug("USER ID TYPE IN WO==" + etsUserDets.getUserType());
		logger.debug("REQUESTOR USER ID IN WO==" + reqUserDets.getWebId());
		logger.debug("OWNER USER ID IN WO==" + ownerDets.getWebId());		

		// check if the user is external or not...
		if (etsUserDets.getUserType() == etsUserDets.USER_TYPE_EXTERNAL) {

			boolean womultipoc = wrkSpcDao.userHasEntitlement(ownerDets.getWebId(), Defines.MULTIPOC);
			
			logger.debug("MULTI-POC EXISTS FOR OWNER USER ID =="+womultipoc);

			//if not multipoc request entitlement for WO and continue the process
			if (!womultipoc) {
				
				logger.debug(" START MULTI-POC ENT OWNER USER ID =="+ownerDets.getWebId());

				actOpModel = addMmbrBdlg.requestMultiPocForWO(ownerDets, reqUserDets);
												
				logger.debug("END MULTI-POC ENT OWNER USER ID =="+ownerDets.getWebId());

			}

		}

		//internal or exteranl, if user doesnot have request/add otw, if the user has reqd
		//ent, then simply add user to workdpace
		
		if (etsUserDets.getUserType() == etsUserDets.USER_TYPE_EXTERNAL || etsUserDets.getUserType() == etsUserDets.USER_TYPE_INTERNAL) {
		
		logger.debug("START submitEntAddMembrToWrkSpc");

		IccWrkSpcStatusModel compStatModel = addMmbrBdlg.submitEntAddMembrToWrkSpc(actInpModel, teamObjKey);
		
		logger.debug("END submitEntAddMembrToWrkSpc");
		
		logger.debug("START updating invite table");
		
		actOpModel=addMmbrBdlg.updateInviteStatTab(compStatModel, actInpModel, teamObjKey);
		
		logger.debug("END updating invite table");
		
		logger.debug("START sendMailProcess");

		actOpModel = addMmbrBdlg.sendMailProcess(compStatModel, actInpModel, teamObjKey);
		
		logger.debug("END sendMailProcess");
		
		}

		return actOpModel;

	}

}
