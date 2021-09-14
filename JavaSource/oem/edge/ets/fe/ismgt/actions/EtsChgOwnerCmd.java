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
import oem.edge.ets.fe.ismgt.bdlg.EtsChgOwnerDataPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsChgOwnerInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsChgOwnerCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.24";

	/**
	 * 
	 */
	public EtsChgOwnerCmd(EtsIssObjectKey issobjkey) {
		super(issobjkey);

	}

	/**
		* key process request method
		*/

	public int processRequest() {

		int curstate = 0;
		int nextstate = 0;
		EtsChgOwnerInfoModel chgOwnerModel = new EtsChgOwnerInfoModel();

		try {

			//new st//

			//			check for state and authz
			String notAuthMsg = "";
			String edgeProblemId=AmtCommonUtils.getTrimStr((String)getIssobjkey().getParams().get("edge_problem_id"));
			chgOwnerModel.setEdgeProblemId(edgeProblemId);
			getIssobjkey().getRequest().setAttribute("edgeProblemId", edgeProblemId);


			if (getUserActionModel().isActionavailable()) {

				int actionKey = getIssobjkey().getActionkey();

				//for resolve issue	
				if (actionKey == 22) {

					if (!getUserActionModel().isUsrChangeOwner()) {

						nextstate = ACTION_NOTAUTHORIZED;
						notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
						getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

					} else {

						chgOwnerModel = getChgOwnerInfoDets();

						curstate = getChgOwnerActionState();

						//get next state

						if (chgOwnerModel != null) {

							nextstate = chgOwnerModel.getNextActionState();

						}

					}

				} else {

					nextstate = ACTION_NOTAUTHORIZED;
					notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
					getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

				}

			} else {

				nextstate = ACTION_INPROCESS;
				notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.inprocess.msg");
				getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

			}

			//new end//
			SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest", "CURRENT state in processRequest ===" + curstate + "");

			SysLog.log(SysLog.DEBUG, "NEXT state in processRequest", "NEXT state in processRequest ===" + nextstate + "");

			if (nextstate > 0) {

				curstate = nextstate;
			}

			if (chgOwnerModel != null) {

				//set the bean details into request//
				getIssobjkey().getRequest().setAttribute("chgOwnerModel", chgOwnerModel);

			} else {

				curstate = FATALERROR;
				String errMsg = "crInfo Model is null in Submit issue. The session might have been idle for long time. Please try again : RC 810";
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

			//process Request success

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsChgOwnerCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in Submit issue : RC 811";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsChgOwnerCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in Submit issue : RC 812";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		}

		return curstate;

	}

	/**
			 * To get the sub state of the given actions
			 */

	public int getChgOwnerActionState() {

		int state = 800;

		String coop = (String) getIssobjkey().getParams().get("coop");

		if (AmtCommonUtils.isResourceDefined(coop)) {

			if (coop.equals("800")) {

				state = CHGOWNER1STPAGE;

			}

		}

		///submit 
		String op_801 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_801.x"));

		if (AmtCommonUtils.isResourceDefined(op_801)) {

			state = CHGOWNERSUBMIT;

		}

		Global.println("print 801.x===" + op_801);
		Global.println("state in getChgOwnerActionState()===" + state);

		return state;
	}

	/**
		 * This method will determine the state of the sub-action, then calls the suitable
		 * method of BDLG and gets the data
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */
	public EtsChgOwnerInfoModel getChgOwnerInfoDets() throws SQLException, Exception {

		EtsChgOwnerInfoModel chgOwnerModel = new EtsChgOwnerInfoModel();

		int state = getChgOwnerActionState();

		Global.println("current state in getChgOwnerInfoDets()===" + state);

		EtsChgOwnerDataPrep chgOwnerDataPrep = new EtsChgOwnerDataPrep(getIssobjkey(), state);

		switch (state) {

			case CHGOWNER1STPAGE :

				chgOwnerModel = chgOwnerDataPrep.getChgOwnerInitDetails();

				break;

			case CHGOWNERSUBMIT :

				chgOwnerModel = chgOwnerDataPrep.submitOwnerDetails();

				break;

		}

		return chgOwnerModel;

	}

} //end of class
