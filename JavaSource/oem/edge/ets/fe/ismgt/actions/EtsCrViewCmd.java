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

package oem.edge.ets.fe.ismgt.actions;

import java.sql.SQLException;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ismgt.bdlg.EtsCrViewDataPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsCrSubmitNewGuiUtils;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsCrActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrViewCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsCrActionConstants {

	public static final String VERSION = "1.30";

	/**
	 * 
	 */
	public EtsCrViewCmd(EtsIssObjectKey issobjkey) {
		super(issobjkey);

	}

	/**
			 * key process request method
			 */

	public int processRequest() {

		int curstate = 0;
		int nextstate = 0;

		try {

			EtsCrProbInfoModel crInfoModel = getCrProbInfoDets();

			curstate = getViewCrSubActionState();

			//get next state

			if (crInfoModel != null) {

				nextstate = crInfoModel.getNextActionState();

			}

			SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest in View CR ", "CURRENT state in processRequest in View CR ===" + curstate + "");

			SysLog.log(SysLog.DEBUG, "NEXT state in processRequest in View CR", "NEXT state in processRequest in View CR ===" + nextstate + "");

			if (nextstate > 0) {

				curstate = nextstate;
			}

			if (crInfoModel != null) {

				//set the bean details into request//
				getIssobjkey().getRequest().setAttribute("crInfoModel", crInfoModel);

			} else {

				curstate = FATALERROR;
				String errMsg = "crInfo Model is null in View CR. The session might have been idle for long time. Please try again : RC 60";
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

			//process Request success

			//processreq = 1;

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrViewCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in View issue : RC 61";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrViewCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in View issue : RC 62";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		}

		return curstate;

	}

	/**
		 * This method will determine the state of the sub-action, then calls the suitable
		 * method of BDLG and gets the data
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */
	public EtsCrProbInfoModel getCrProbInfoDets() throws SQLException, Exception {

		EtsCrProbInfoModel crInfoModel = new EtsCrProbInfoModel();

		int state = getViewCrSubActionState();

		Global.println("current state in getCrProbInfoDets in View CR()===" + state);

		EtsCrViewDataPrep crViewDataPrep = new EtsCrViewDataPrep(getIssobjkey(), state);

		switch (state) {

			case VIEWCRDETS :

				crInfoModel = crViewDataPrep.getViewCrInfoDetails();

				break;

		}

		//		print details
		//print details
		EtsCrSubmitNewGuiUtils crGuiUtil = new EtsCrSubmitNewGuiUtils();
		crGuiUtil.debugCrInfoModelDetails(crInfoModel);

		return crInfoModel;

	}

	/**
		* To get the sub state of the given actions
			 */

	public int getViewCrSubActionState() {

		int state = 60;

		String op = (String) getIssobjkey().getParams().get("op");

		String userType = getIssobjkey().getEs().gDECAFTYPE;

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("60")) {

				state = VIEWCRDETS;

			}

		}

		return state;
	}

} //end of class
