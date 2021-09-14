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
import oem.edge.ets.fe.ismgt.bdlg.EtsIssReqCreateNewIssTypeDataPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
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
public class EtsIssReqCreateNewIssTypeCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.36";

	/**
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public EtsIssReqCreateNewIssTypeCmd(EtsIssObjectKey issobjkey) {
		super(issobjkey);

	}

	/**
	 * key process request method
	 */

	public int processRequest() {

		int curstate = 0;
		int nextstate = 0;

		try {

			EtsIssProbInfoUsr1Model usr1InfoModel = getProbInfoUsr1Dets();

			curstate = getCreatIssTypeSubActionState();

			//get next state

			if (usr1InfoModel != null) {

				nextstate = usr1InfoModel.getNextActionState();

			}

			//			create not for visitir or executive

			if (EtsIssFilterUtils.isUserIssViewOnly(getIssobjkey().getEs(), getIssobjkey().getProj().getProjectId())) {

				nextstate = FATALERROR;
				String errMsg = (String) getIssobjkey().getPropMap().get("issues.invalid.user.msg");
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

			SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest", "CURRENT state in processRequest ===" + curstate + "");

			SysLog.log(SysLog.DEBUG, "NEXT state in processRequest", "NEXT state in processRequest ===" + nextstate + "");

			if (nextstate > 0) {

				curstate = nextstate;
			}

			if (curstate == CREATEISSUETYPEMAILERR) {

				curstate = FATALERROR;
				String errMsg = "An internal error has occured while processing your request. Please try again or contact IBM Customer Connect  Help Desk: RC : MAILFAILURE";
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

			}

			if (usr1InfoModel != null) {

				//set the bean details into request//
				getIssobjkey().getRequest().setAttribute("usr1InfoModel", usr1InfoModel);

			} else {

				curstate = FATALERROR;
				String errMsg = "Usr1Info Model is null to request create new issue type. The session might have been idle for long time. Please try again : RC 710";
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

			//process Request success

			//processreq = 1;

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssReqCreateNewIssTypeCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in Submit issue : RC 711";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssReqCreateNewIssTypeCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in Submit issue : RC 712";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		}

		return curstate;

	}

	/**
			 * To get the sub state of the given actions
			 */

	public int getCreatIssTypeSubActionState() {

		int state = 700;

		String op = (String) getIssobjkey().getParams().get("op");

		String userType = getIssobjkey().getEs().gDECAFTYPE;

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("700")) {

				state = CREATEISSUETYPE1STPAGE;

			}
		}

		String op_701 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_701.x"));

		if (AmtCommonUtils.isResourceDefined(op_701)) {

			state = CREATEISSUETYPECONTINUE;

		}

		String op_702 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_702.x"));

		if (AmtCommonUtils.isResourceDefined(op_702)) {

			state = CREATEISSUETYPECANCEL;

		}

		String op_703 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_703.x"));

		if (AmtCommonUtils.isResourceDefined(op_703)) {

			state = EDITCREATEISSUETYPE;

		}

		String op_704 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_704.x"));

		if (AmtCommonUtils.isResourceDefined(op_704)) {

			state = CREATEISSUETYPESUBMIT;

		}

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
	public EtsIssProbInfoUsr1Model getProbInfoUsr1Dets() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usr1Dets = new EtsIssProbInfoUsr1Model();

		int state = getCreatIssTypeSubActionState();

		Global.println("current state in getProbInfoUsr1Dets()===" + state);

		EtsIssReqCreateNewIssTypeDataPrep createDataPrep = new EtsIssReqCreateNewIssTypeDataPrep(getIssobjkey(), state);

		switch (state) {

			case CREATEISSUETYPE1STPAGE :

				usr1Dets = createDataPrep.getFirstPageDets();

				break;

			case CREATEISSUETYPECONTINUE :

				usr1Dets = createDataPrep.getContinueReqDetails();

				break;

			case CREATEISSUETYPECANCEL :

				usr1Dets = createDataPrep.getCancReqNewIssTypeDetails();

				break;

			case EDITCREATEISSUETYPE :

				usr1Dets = createDataPrep.getEditReqNewIssTypeDetails();

				break;

			case CREATEISSUETYPESUBMIT :

				usr1Dets = createDataPrep.getSubmitReqDetails();

				break;

		}

		return usr1Dets;

	}

} //end of class
