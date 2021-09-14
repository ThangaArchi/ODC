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
import oem.edge.ets.fe.ismgt.bdlg.EtsCrUpdateDataPrep;
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
public class EtsCrUpdateCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsCrActionConstants {

	public static final String VERSION = "1.30";

	/**
	 * 
	 */
	public EtsCrUpdateCmd(EtsIssObjectKey issobjkey) {
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

			curstate = getUpdCrSubActionState();

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

			SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest in Comment CR ", "CURRENT state in processRequest in Comment CR ===" + curstate + "");

			SysLog.log(SysLog.DEBUG, "NEXT state in processRequest in Comment CR", "NEXT state in processRequest in Comment CR ===" + nextstate + "");

			if (nextstate > 0) {

				curstate = nextstate;
			}

			if (crInfoModel != null) {

				//set the bean details into request//
				getIssobjkey().getRequest().setAttribute("crInfoModel", crInfoModel);

			} else {

				curstate = FATALERROR;
				String errMsg = "crInfo Model is null in Comment CR. The session might have been idle for long time. Please try again : RC 60";
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

			//process Request success

			//processreq = 1;

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrUpdateCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in View issue : RC 61";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrUpdateCmd", ETSLSTUSR);

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

		int state = getUpdCrSubActionState();

		Global.println("current state in getCrProbInfoDets in EtsCrUpdateCmd CR()===" + state);

		EtsCrUpdateDataPrep crUpdDataPrep = new EtsCrUpdateDataPrep(getIssobjkey(), state);

		switch (state) {

			case COMMENTSCRFIRSTPAGE :

			case COMMENTSCREDITFILEATTACH :

				crInfoModel = crUpdDataPrep.getViewCrInfoDetails();

				break;

			case COMMENTSCRSUBMITTODB :

				crInfoModel = crUpdDataPrep.getSubmitCommentsDetails();

				break;

			case COMMENTSCRCANCELDB :

				crInfoModel = crUpdDataPrep.getCancelCommentsDetails();

				break;

			case COMMENTSCRFILEATTACH :

				crInfoModel = crUpdDataPrep.doFileAttach();

				break;

			case COMMENTSCRDELETEFILE :

				crInfoModel = crUpdDataPrep.deleteFileAttach();

				break;

			case COMMENTSCRSUBTFILEATTACH :

				crInfoModel = crUpdDataPrep.getSubmitFileattachDetails();

				break;

			case COMMENTSCRCANCFILEATTACH :

				crInfoModel = crUpdDataPrep.getCancelFileAttachDetails();

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

	public int getUpdCrSubActionState() {

		int state = COMMENTSCRFIRSTPAGE;

		String op = (String) getIssobjkey().getParams().get("op");

		String userType = getIssobjkey().getEs().gDECAFTYPE;

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("500")) {

				state = COMMENTSCRFIRSTPAGE;

			}

		}

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("521")) {

				state = COMMENTSCREDITFILEATTACH;

			}

		}

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("2")) {

				state = COMMENTSCRFILEATTACH;

			}

		}

		String op_526 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_526.x"));

		if (AmtCommonUtils.isResourceDefined(op_526)) {

			state = COMMENTSCRSUBMITTODB;

		}

		String op_527 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_527.x"));

		if (AmtCommonUtils.isResourceDefined(op_527)) {

			state = COMMENTSCRCANCELDB;

		}

		String submitvalue = AmtCommonUtils.getTrimStr((String) getIssobjkey().getSubmitValue());

		Global.println("submitvalue===" + submitvalue);

		if (AmtCommonUtils.isResourceDefined(submitvalue)) {

			if (submitvalue.equals("delete")) {

				state = COMMENTSCRDELETEFILE;

			}

		}

		String op_520 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_520.x"));

		if (AmtCommonUtils.isResourceDefined(op_520)) {

			state = COMMENTSCRSUBTFILEATTACH;

		}

		String op_5123 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_5123.x"));

		if (AmtCommonUtils.isResourceDefined(op_5123)) {

			state = COMMENTSCRCANCFILEATTACH;

		}

		return state;
	}

} //end of class
