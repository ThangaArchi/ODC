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
import oem.edge.ets.fe.ismgt.bdlg.EtsIssSubmitNewDataPrep;
import oem.edge.ets.fe.ismgt.bdlg.SubmitIssueBdlg;
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
public class EtsIssSubmitNewCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.43";

	/**
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public EtsIssSubmitNewCmd(EtsIssObjectKey issobjkey) {
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

			//create not for visitir or executive

			if (!getIssobjkey().getUsrRolesModel().isUsrSubmitIssue()) {

				nextstate = FATALERROR;
				String errMsg = (String) getIssobjkey().getPropMap().get("issues.invalid.user.msg");
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

			} else {

				usr1InfoModel = getProbInfoUsr1Dets();

				curstate = getSubmitNewSubActionState();

				//get next state

				if (usr1InfoModel != null) {

					nextstate = usr1InfoModel.getNextActionState();

				}

			}

			SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest", "CURRENT state in processRequest ===" + curstate + "");

			SysLog.log(SysLog.DEBUG, "NEXT state in processRequest", "NEXT state in processRequest ===" + nextstate + "");

			if (nextstate > 0) {

				curstate = nextstate;
			}

			if (usr1InfoModel != null) {

				//set the bean details into request//
				getIssobjkey().getRequest().setAttribute("usr1InfoModel", usr1InfoModel);

			} else {

				curstate = FATALERROR;
				String errMsg = "Usr1Info Model is null in Submit issue. The session might have been idle for long time. Please try again : RC 10";
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

			//process Request success

			//processreq = 1;

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssSubmitNewCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in Submit issue : RC 11";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssSubmitNewCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in Submit issue : RC 12";
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

		int state = getSubmitNewSubActionState();

		Global.println("current state in getProbInfoUsr1Dets()===" + state);

		SubmitIssueBdlg newDataPrep = new SubmitIssueBdlg(getIssobjkey(), state);

		switch (state) {

			case SUBMIT_ONBEHALF_EXT :

				usr1Dets = newDataPrep.getSubmitOnBehalfExtDets();

				break;

			case CONT_SUBMIT_ONBEHALF_EXT :

				usr1Dets = newDataPrep.getContBehalfExtDets();

				break;

			case NEWINITIAL :

				usr1Dets = newDataPrep.getNewInitialDetails();

				break;

			case CONTDESCR :

				usr1Dets = newDataPrep.getNewContDescrDetails();

				break;

			case CANCDESCR :

				usr1Dets = newDataPrep.getCancelDescrDetails();

				break;

			case EDITDESCR :

				usr1Dets = newDataPrep.getEditIssueDescrDetails();

				break;

			
			case FILEATTACH :

				usr1Dets = newDataPrep.doFileAttach();

				break;

			case DELETEFILE :

				usr1Dets = newDataPrep.deleteFileAttach();

				break;

		
			case ISSUEFINALSUBMIT :

				usr1Dets = newDataPrep.getNotifyDetsSubmit();

				break;

			case EDITNOTIFYLIST :

				usr1Dets = newDataPrep.getEditNotifyListDeatils();

				break;

			case SUBMITTODB :

				usr1Dets = newDataPrep.getSubmitIssueDetails();

				break;

			case ISSUEFINALCANCEL :

				usr1Dets = newDataPrep.getCancelSubmitDetails();

				break;

		}

		//set the action state
		//usr1Dets.setCurrentActionState(state);

		//print details
		EtsIssActionGuiUtils guiUtil = new EtsIssActionGuiUtils();
		guiUtil.debugUsr1ModelDetails(usr1Dets);

		return usr1Dets;

	}

	/**
		 * To get the sub state of the given actions
		 */

	public int getSubmitNewSubActionState() {

		int state = 1;

		String op = (String) getIssobjkey().getParams().get("op");

		String userType = getIssobjkey().getEs().gDECAFTYPE;

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("1")) {

				state = NEWINITIAL;

			}

			if (op.equals("2")) {

				state = FILEATTACH;

			}

			if (op.equals("3")) {

				state = SUBMIT_ONBEHALF_EXT;

			}

		}

		String op_00 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_00.x"));

		if (AmtCommonUtils.isResourceDefined(op_00)) {

			state = CONT_SUBMIT_ONBEHALF_EXT;

		}

		String op_01 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_01.x"));

		if (AmtCommonUtils.isResourceDefined(op_01)) {

			state = MAINPAGE;

		}

		String op_11 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_11.x"));

		if (AmtCommonUtils.isResourceDefined(op_11)) {

			state = CONTDESCR;

		}

		String op_12 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_12.x"));

		if (AmtCommonUtils.isResourceDefined(op_12)) {

			state = CANCDESCR;

		}

		String op_13 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_13.x"));

		if (AmtCommonUtils.isResourceDefined(op_13)) {

			state = EDITDESCR;

		}

		String op_16 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_16.x"));

		if (AmtCommonUtils.isResourceDefined(op_16)) {

			state = ADDFILEATTACH;

		}

		String submitvalue = AmtCommonUtils.getTrimStr((String) getIssobjkey().getSubmitValue());

		Global.println("submitvalue===" + submitvalue);

		if (AmtCommonUtils.isResourceDefined(submitvalue)) {

			if (submitvalue.equals("delete")) {

				state = DELETEFILE;

			}

		}

		String op_20 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_20.x"));

		//chk if it is on ext behalf
		String extBhf = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("extbhf"));

		if (AmtCommonUtils.isResourceDefined(op_20)) {

			if (!extBhf.equals("1")) {

				if (userType.equals("I")) {

					state = ADDACCESSCNTRL;

				} else {

					if (!getIssobjkey().isProjBladeType()) {

						state = ADDNOTIFYLISTFOREXT;

					} else {

						state = ISSUEFINALSUBMIT;

						//state = ADDNOTIFYLISTFOREXT;

					}

					//state = ADDNOTIFYLISTFOREXT;

				}

			} //end of not ext bhf

			else {

				Global.println("extering ext bhf final");

				if (!getIssobjkey().isProjBladeType()) {

					state = ADDNOTIFYLISTFOREXT;

				} else {

					state = ISSUEFINALSUBMIT;

					//state = ADDNOTIFYLISTFOREXT;

				}

			}
		} //end of op=20

		String op_22 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_22.x"));

		if (AmtCommonUtils.isResourceDefined(op_22)) {

			state = ADDNOTIFYLIST;

		}

		String op_24 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_24.x"));

		if (AmtCommonUtils.isResourceDefined(op_24)) {

			state = ISSUEFINALSUBMIT;

		}

		String op_25 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_25"));

		if (AmtCommonUtils.isResourceDefined(op_25)) {

			state = EDITNOTIFYLIST;

		}

		String op_26 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_26.x"));

		if (AmtCommonUtils.isResourceDefined(op_26)) {

			state = SUBMITTODB;

		}

		String op_28 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_28.x"));

		if (AmtCommonUtils.isResourceDefined(op_28)) {

			state = ISSUEFINALSUBMIT;

		}

		String op_126 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_126.x"));

		if (AmtCommonUtils.isResourceDefined(op_126)) {

			state = ISSUEFINALCANCEL;

		}

		Global.println("state in getSubmitNewSubActionState()===" + state);

		return state;
	}

} //end of class
