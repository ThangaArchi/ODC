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
import oem.edge.ets.fe.ismgt.bdlg.EtsIssResolveDataPrep;
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
public class EtsIssResolveCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.21.1.17";

	/**
	 * 
	 */
	public EtsIssResolveCmd(EtsIssObjectKey issobjkey) {
		super(issobjkey);

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

				//for resolve issue	
				if (actionKey == 3) {

					if (!getUserActionModel().isUsrResolveIssue()) {

						nextstate = ACTION_NOTAUTHORIZED;
						notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
						getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

					} else {

						usr1InfoModel = getProbInfoUsr1Dets();

						curstate = getResolveSubActionState();

						//get next state

						if (usr1InfoModel != null) {

							nextstate = usr1InfoModel.getNextActionState();

						}
					}

				}

				//for reject issue

				else if (actionKey == 5) {

					if (!getUserActionModel().isUsrRejectIssue()) {

						nextstate = ACTION_NOTAUTHORIZED;
						notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
						getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

					} else {

						usr1InfoModel = getProbInfoUsr1Dets();

						curstate = getResolveSubActionState();

						//get next state

						if (usr1InfoModel != null) {

							nextstate = usr1InfoModel.getNextActionState();

						}
					}

				}

				//	for close issue

				else if (actionKey == 6) {

					if (!getUserActionModel().isUsrCloseIssue()) {

						nextstate = ACTION_NOTAUTHORIZED;
						notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
						getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

					} else {

						usr1InfoModel = getProbInfoUsr1Dets();

						curstate = getResolveSubActionState();

						//get next state

						if (usr1InfoModel != null) {

							nextstate = usr1InfoModel.getNextActionState();

						}
					}

				}

				//	for comment issue

				else if (actionKey == 19) {

					if (!getUserActionModel().isUsrCommentIssue()) {

						nextstate = ACTION_NOTAUTHORIZED;
						notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
						getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

					} else {

						usr1InfoModel = getProbInfoUsr1Dets();

						curstate = getResolveSubActionState();

						//get next state

						if (usr1InfoModel != null) {

							nextstate = usr1InfoModel.getNextActionState();

						}
					}

				}

				//	for withdraw

				else if (actionKey == 23) {

					if (!getUserActionModel().isUsrWithDraw()) {

						nextstate = ACTION_NOTAUTHORIZED;
						notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
						getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

					} else {

						usr1InfoModel = getProbInfoUsr1Dets();

						curstate = getResolveSubActionState();

						//get next state

						if (usr1InfoModel != null) {

							nextstate = usr1InfoModel.getNextActionState();

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

		int state = getResolveSubActionState();

		Global.println("current state in resolve/getProbInfoUsr1Dets()===" + state);

		EtsIssResolveDataPrep resDataPrep = new EtsIssResolveDataPrep(getIssobjkey(), state);

		switch (state) {

			case RESOLVEISSUEFIRSTPAGE :

				usr1Dets = resDataPrep.getFirstPageDets();

				break;

			case RESOLVEEDITFILEATTACH :

				usr1Dets = resDataPrep.getEditFileAttachDetails();

				break;

			case FILEATTACH :

				usr1Dets = resDataPrep.doFileAttach();

				break;

			case DELETEFILE :

				usr1Dets = resDataPrep.deleteFileAttach();

				break;

			case RESOLVECONTFILEATTACH :

				usr1Dets = resDataPrep.getContFileattachDetails();

				break;

			case RESOLVECANCFILEATTACH :

				usr1Dets = resDataPrep.getCancFileattachDetails();

				break;

			case RESOLVESUBMITTODB :

				usr1Dets = resDataPrep.getContCommentsDetails();

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

	public int getResolveSubActionState() {

		int state = 500;

		String op = (String) getIssobjkey().getParams().get("op");

		String userType = getIssobjkey().getEs().gDECAFTYPE;

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("500")) {

				state = RESOLVEISSUEFIRSTPAGE;

			}

			if (op.equals("2")) {

				state = FILEATTACH;

			}

		}

		String op_521 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_521.x"));

		if (AmtCommonUtils.isResourceDefined(op_521)) {

			state = RESOLVEEDITFILEATTACH;

		}

		String submitvalue = AmtCommonUtils.getTrimStr((String) getIssobjkey().getSubmitValue());

		Global.println("submitvalue===" + submitvalue);

		if (AmtCommonUtils.isResourceDefined(submitvalue)) {

			if (submitvalue.equals("delete")) {

				state = DELETEFILE;

			}

		}

		String op_520 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_520.x"));

		if (AmtCommonUtils.isResourceDefined(op_520)) {

			state = RESOLVECONTFILEATTACH;

		}

		String op_5123 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_5123.x"));

		if (AmtCommonUtils.isResourceDefined(op_5123)) {

			state = RESOLVECANCFILEATTACH;

		}

		String op_526 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_526.x"));

		if (AmtCommonUtils.isResourceDefined(op_526)) {

			state = RESOLVESUBMITTODB;

		}

		Global.println("state in getResolveSubActionState()===" + state);

		return state;

	}

}
