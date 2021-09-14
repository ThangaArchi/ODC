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
import oem.edge.ets.fe.ismgt.bdlg.EtsCrSubmitNewDataPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsCrSubmitNewGuiUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
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
public class EtsCrSubmitNewCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsCrActionConstants {

	public static final String VERSION = "1.37";

	/**
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public EtsCrSubmitNewCmd(EtsIssObjectKey issobjkey) {
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

			curstate = getSubmitNewCrSubActionState();

			//get next state

			if (crInfoModel != null) {

				nextstate = crInfoModel.getNextActionState();

			}

			//create not for visitir or executive

			if (getIssobjkey().getUsrRolesModel().isUsrVisitor()) {

				nextstate = FATALERROR;
				String errMsg = (String) getIssobjkey().getPropMap().get("issues.invalid.user.msg");
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

			SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest", "CURRENT state in processRequest ===" + curstate + "");

			SysLog.log(SysLog.DEBUG, "NEXT state in processRequest", "NEXT state in processRequest ===" + nextstate + "");

			if (nextstate > 0) {

				curstate = nextstate;
			}

			if (crInfoModel != null) {

				//set the bean details into request//
				getIssobjkey().getRequest().setAttribute("crInfoModel", crInfoModel);

			} else {

				curstate = FATALERROR;
				String errMsg = "crInfo Model is null in Submit issue. The session might have been idle for long time. Please try again : RC 10";
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

			//process Request success

			//processreq = 1;

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrSubmitNewCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in Submit issue : RC 11";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrSubmitNewCmd", ETSLSTUSR);

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
	public EtsCrProbInfoModel getCrProbInfoDets() throws SQLException, Exception {

		EtsCrProbInfoModel crInfoModel = new EtsCrProbInfoModel();

		int state = getSubmitNewCrSubActionState();

		Global.println("current state in getCrProbInfoDets()===" + state);

		EtsCrSubmitNewDataPrep crNewDataPrep = new EtsCrSubmitNewDataPrep(getIssobjkey(), state);

		switch (state) {

			case NEWINITIAL :

				crInfoModel = crNewDataPrep.getNewInitialDetails();

				break;

			case CONTDESCR :

				crInfoModel = crNewDataPrep.getNewContDescrDetails();

				break;

			case CANCDESCR :

				crInfoModel = crNewDataPrep.getCancelDescrDetails();

				break;

			case EDITDESCR :

				crInfoModel = crNewDataPrep.getEditIssueDescrDetails();

				break;

			case FILEATTACH :

				crInfoModel = crNewDataPrep.doFileAttach();

				break;

			case DELETEFILE :

				crInfoModel = crNewDataPrep.deleteFileAttach();

				break;

			case CANCFILEATTACH :

				crInfoModel = crNewDataPrep.getCancelFileAttachDetails();
				break;

			case EDITFILEATTACH :

				crInfoModel = crNewDataPrep.getEditFileAttachDetails();

				break;

			case ADDFILEATTACH :

				crInfoModel = crNewDataPrep.getContFileattachDetails();

				break;

			case SUBMITTODB :

				crInfoModel = crNewDataPrep.getSubmitCRDetails();

				break;

			case CANCELSUBMITTODB :

				crInfoModel = crNewDataPrep.getCancelFinalSubmitDetails();

				break;

		}

		//print details
		EtsCrSubmitNewGuiUtils crGuiUtil = new EtsCrSubmitNewGuiUtils();
		crGuiUtil.debugCrInfoModelDetails(crInfoModel);

		return crInfoModel;

	}

	/**
		 * To get the sub state of the given actions
		 */

	public int getSubmitNewCrSubActionState() {

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

		String op_20 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_20.x"));

		if (AmtCommonUtils.isResourceDefined(op_20)) {

			state = ADDFILEATTACH;

		}

		String submitvalue = AmtCommonUtils.getTrimStr((String) getIssobjkey().getSubmitValue());

		Global.println("submitvalue===" + submitvalue);

		if (AmtCommonUtils.isResourceDefined(submitvalue)) {

			if (submitvalue.equals("delete")) {

				state = DELETEFILE;

			}

		}

		String op_123 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_123.x"));

		if (AmtCommonUtils.isResourceDefined(op_123)) {

			state = CANCFILEATTACH;

		}

		String op_21 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_21.x"));

		if (AmtCommonUtils.isResourceDefined(op_21)) {

			state = EDITFILEATTACH;

		}

		String op_26 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_26.x"));

		if (AmtCommonUtils.isResourceDefined(op_26)) {

			state = SUBMITTODB;

		}

		String op_126 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_126.x"));

		if (AmtCommonUtils.isResourceDefined(op_126)) {

			state = CANCELSUBMITTODB;

		}

		Global.println("state in getSubmitNewCrSubActionState()===" + state);

		return state;
	}

} //end of class
