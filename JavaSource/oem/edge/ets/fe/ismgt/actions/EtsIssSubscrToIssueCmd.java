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
import oem.edge.ets.fe.ismgt.bdlg.EtsIssSubscrIssueBdlg;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionGuiUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSubscrToIssueCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.5";

	/**
	 * 
	 */
	public EtsIssSubscrToIssueCmd(EtsIssObjectKey issobjkey) {
		super(issobjkey);
		// TODO Auto-generated constructor stub
	}

	/**
				 * key process request method
				 */

	public int processRequest() {

		int curstate = 0;
		int nextstate = 0;
		EtsIssProbInfoUsr1Model usr1InfoModel = new EtsIssProbInfoUsr1Model();

		try {

			//check for state and authz
			String notAuthMsg = "";
			String edgeProblemId = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("edge_problem_id"));
			usr1InfoModel.setEdgeProblemId(edgeProblemId);
			getIssobjkey().getRequest().setAttribute("edgeProblemId", edgeProblemId);

			if (getUserActionModel().isActionavailable()) {

				int actionKey = getIssobjkey().getActionkey();

				if (!getUserActionModel().isUsrSubscribe()) {

					nextstate = ACTION_NOTAUTHORIZED;
					notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
					getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

				} else {

					usr1InfoModel = getProbInfoUsr1Dets();

					curstate = getSubscrSubActionState();

					//get next state

					if (usr1InfoModel != null) {

						nextstate = usr1InfoModel.getNextActionState();

					}

				}
				
			} else {

				nextstate = ACTION_INPROCESS;
				notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.inprocess.msg");
				getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

			}

			SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest for Resolve issue", "CURRENT state in processRequest for Resolve issue ===" + curstate + "");

			SysLog.log(SysLog.DEBUG, "NEXT state in processRequest for Resolve issue", "NEXT state in processRequest for Resolve issue ===" + nextstate + "");

			if (nextstate > 0) {

				curstate = nextstate;
			}

			if (usr1InfoModel != null) {

				//set the bean details into request//
				getIssobjkey().getRequest().setAttribute("usr1InfoModel", usr1InfoModel);

			} else {

				curstate = FATALERROR;
				String errMsg = "Usr1Info Model is null in Resolve issue. The session might have been idle for long time. Please try again : RC 10";
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssResolveCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in Resolve issue : RC 511";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssResolveCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in Resolve issue : RC 512";
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
	public EtsIssProbInfoUsr1Model getProbInfoUsr1Dets() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usr1Dets = new EtsIssProbInfoUsr1Model();

		int state = getSubscrSubActionState();

		Global.println("current state in resolve/getProbInfoUsr1Dets()===" + state);

		EtsIssSubscrIssueBdlg subsDataPrep = new EtsIssSubscrIssueBdlg(getIssobjkey(), state);

		switch (state) {

			case SUBSCRISSUE :

				usr1Dets = subsDataPrep.subscribeIssue();

				break;

			case UNSUBSCRISSUE :

				usr1Dets = subsDataPrep.unSubscribeIssue();

				break;

		}

		//		print details
		EtsIssActionGuiUtils guiUtil = new EtsIssActionGuiUtils();
		guiUtil.debugUsr1ModelDetails(usr1Dets);

		return usr1Dets;
	}

	/**
			 * To get the sub state of the given actions
			 */

	public int getSubscrSubActionState() {

		int state = 1300;

		String op = (String) getIssobjkey().getParams().get("op");

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("1300")) {

				state = SUBSCRISSUE;

			}

			if (op.equals("1301")) {

				state = UNSUBSCRISSUE;

			}

		}

		Global.println("state in getSubscrSubActionState()===" + state);

		return state;

	}

} //end of class
