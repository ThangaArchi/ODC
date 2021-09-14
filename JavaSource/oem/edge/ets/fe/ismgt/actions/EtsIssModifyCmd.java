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
import oem.edge.ets.fe.ismgt.bdlg.EtsIssModifyDataPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionGuiUtils;
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
public class EtsIssModifyCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.36";

	/**
	 * 
	 */
	public EtsIssModifyCmd(EtsIssObjectKey issobjkey) {
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
			String edgeProblemId=AmtCommonUtils.getTrimStr((String)getIssobjkey().getParams().get("edge_problem_id"));
			usr1InfoModel.setEdgeProblemId(edgeProblemId);
			getIssobjkey().getRequest().setAttribute("edgeProblemId", edgeProblemId);


			if (getUserActionModel().isActionavailable()) {

				int actionKey = getIssobjkey().getActionkey();

				//for resolve issue	
				if (actionKey == 2) {

					if (!getUserActionModel().isUsrModifyIssue()) {

						nextstate = ACTION_NOTAUTHORIZED;
						notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
						getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);

					} else {

						usr1InfoModel = getProbInfoUsr1Dets();

						curstate = getModifySubActionState();

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

			SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest for Modify issue", "CURRENT state in processRequest for Modify issue ===" + curstate + "");

			SysLog.log(SysLog.DEBUG, "NEXT state in processRequest for Modify issue", "NEXT state in processRequest for Modify issue ===" + nextstate + "");

			if (nextstate > 0) {

				curstate = nextstate;
			}

			if (usr1InfoModel != null) {

				//set the bean details into request//
				getIssobjkey().getRequest().setAttribute("usr1InfoModel", usr1InfoModel);

			} else {

				curstate = FATALERROR;
				String errMsg = "Usr1Info Model is null in Modify issue. The session might have been idle for long time. Please try again : RC 10";
				getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssModifyCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in Modify issue : RC 311";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssModifyCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in Modify issue : RC 312";
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

		int state = getModifySubActionState();

		Global.println("current state in modify/getProbInfoUsr1Dets()===" + state);

		EtsIssModifyDataPrep modDataPrep = new EtsIssModifyDataPrep(getIssobjkey(), state);

		switch (state) {

			case MODIFYISSUEFIRSTPAGE :

				usr1Dets = modDataPrep.getFirstPageDets();

				break;

			case MODIFYEDITDESCR :

				usr1Dets = modDataPrep.getEditIssueDescrDetails();

				break;

			case MODIFYCONTDESCR :

				usr1Dets = modDataPrep.getNewContDescrDetails();

				break;

			case MODIFYCANCDESCR :

				usr1Dets = modDataPrep.getCancelDescrDetails();

				break;

			case MODIFYEDITFILEATTACH :

				usr1Dets = modDataPrep.getEditFileAttachDetails();

				break;

			case FILEATTACH :

				usr1Dets = modDataPrep.doFileAttach();

				break;

			case DELETEFILE :

				usr1Dets = modDataPrep.deleteFileAttach();

				break;

			case MODIFYCONTFILEATTACH :

				usr1Dets = modDataPrep.getContFileattachDetails();

				break;

			case MODIFYCANCFILEATTACH :

				usr1Dets = modDataPrep.getCancFileattachDetails();

				break;

			case MODIFYEDITCUSTOMFIELDS :
				
				usr1Dets = modDataPrep.getModEditCustFields();

				break;

			case MODIFYEDITCUSTOMFIELDSCONT :
				
				usr1Dets = modDataPrep.getModEditCustFieldsCont();

				break;
				
				
			case MODIFYEDITNOTIFYLIST :

				usr1Dets = modDataPrep.getEditNotifyListDetails();

				break;

			case MODIFYCONTNOTIFYLIST :

				usr1Dets = modDataPrep.getContNotifyListDetails();

				break;

			case MODIFYCANCNOTIFYLIST :

				usr1Dets = modDataPrep.getCancNotifyListDetails();

				break;

			case MODIFYEDITISSUEIDENTF :

				usr1Dets = modDataPrep.getModEditIssueIdentfDetails();

				break;

			case MODIFYEDITISSUETYPE :

				usr1Dets = modDataPrep.getModEditIssueTypeDetails();

				break;

			case MODIFYADDISSUETYPE :

				usr1Dets = modDataPrep.getModAddIssueTypeDetails();

				break;

			case MODIFYCANCADDISSUETYPE :

				usr1Dets = modDataPrep.getModCancAddIssueTypeDetails();
				break;

			case MODIFYGOSUBTYPEA :

				usr1Dets = modDataPrep.getModGoSubTypeADetails();

				break;

			case MODIFYGOSUBTYPEB :

				usr1Dets = modDataPrep.getModGoSubTypeBDetails();

				break;

			case MODIFYGOSUBTYPEC :

				usr1Dets = modDataPrep.getModGoSubTypeCDetails();

				break;

			case MODIFYEDITSUBTYPEA :

				usr1Dets = modDataPrep.getModEditSubTypeADetails();

				break;

			case MODIFYEDITSUBTYPEB :

				usr1Dets = modDataPrep.getModEditSubTypeBDetails();

				break;

			case MODIFYEDITSUBTYPEC :

				usr1Dets = modDataPrep.getModEditSubTypeCDetails();

				break;

			case MODIFYEDITSUBTYPED :

				usr1Dets = modDataPrep.getModEditSubTypeDDetails();

				break;

			case MODIFYCANCSUBTYPE :

				usr1Dets = modDataPrep.getModCancelSubTypeDetails();
				break;

			case MODIFYCONTIDENTF :

				usr1Dets = modDataPrep.goModContIssueIdentDetails();

				break;

			case MODIFYCANCIDENTF :

				usr1Dets = modDataPrep.getModCancelIssueIdentDetails();

				break;

			case MODIFYUNIFIEDCANCEL :

				usr1Dets = modDataPrep.getModUnifiedCancel();

				break;

			case MODIFYSUBMITTODB :

				usr1Dets = modDataPrep.getContCommentsDetails();

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

	public int getModifySubActionState() {

		int state = 300;

		String op = (String) getIssobjkey().getParams().get("op");

		String userType = getIssobjkey().getEs().gDECAFTYPE;

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("300")) {

				state = MODIFYISSUEFIRSTPAGE;

			}

			if (op.equals("2")) {

				state = FILEATTACH;

			}

		}

		String op_313 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_313.x"));

		if (AmtCommonUtils.isResourceDefined(op_313)) {

			state = MODIFYEDITDESCR;

		}

		String op_311 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_311.x"));

		if (AmtCommonUtils.isResourceDefined(op_311)) {

			state = MODIFYCONTDESCR;

		}

		String op_312 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_312.x"));

		if (AmtCommonUtils.isResourceDefined(op_312)) {

			state = MODIFYCANCDESCR;

		}

		String op_321 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_321.x"));

		if (AmtCommonUtils.isResourceDefined(op_321)) {

			state = MODIFYEDITFILEATTACH;

		}

		String op_322 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_322.x"));

		if (AmtCommonUtils.isResourceDefined(op_322)) {

			state = MODIFYEDITCUSTOMFIELDS;
			
		}		
		
		//contnName = "op_3221";

		String op_3221 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3221.x"));

		if (AmtCommonUtils.isResourceDefined(op_3221)) {

			state = MODIFYEDITCUSTOMFIELDSCONT;
			
		}				
		
		String submitvalue = AmtCommonUtils.getTrimStr((String) getIssobjkey().getSubmitValue());

		Global.println("submitvalue===" + submitvalue);

		if (AmtCommonUtils.isResourceDefined(submitvalue)) {

			if (submitvalue.equals("delete")) {

				state = DELETEFILE;

			}

		}

		String op_320 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_320.x"));

		if (AmtCommonUtils.isResourceDefined(op_320)) {

			state = MODIFYCONTFILEATTACH;

		}

		String op_3123 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3123.x"));

		if (AmtCommonUtils.isResourceDefined(op_3123)) {

			state = MODIFYCANCFILEATTACH;

		}

		String op_325 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_325.x"));

		if (AmtCommonUtils.isResourceDefined(op_325)) {

			state = MODIFYEDITNOTIFYLIST;

		}

		String op_324 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_324.x"));

		if (AmtCommonUtils.isResourceDefined(op_324)) {

			state = MODIFYCONTNOTIFYLIST;

		}

		String op_3125 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3125.x"));

		if (AmtCommonUtils.isResourceDefined(op_3125)) {

			state = MODIFYCANCNOTIFYLIST;

		}

		String op_317 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_317.x"));

		if (AmtCommonUtils.isResourceDefined(op_317)) {

			state = MODIFYEDITISSUEIDENTF;

		}

		String op_314 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_314.x"));

		if (AmtCommonUtils.isResourceDefined(op_314)) {

			state = MODIFYEDITISSUETYPE;

		}

		String op_328 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_328.x"));

		if (AmtCommonUtils.isResourceDefined(op_328)) {

			state = MODIFYADDISSUETYPE;

		}

		String op_3128 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3128.x"));

		if (AmtCommonUtils.isResourceDefined(op_3128)) {

			state = MODIFYCANCADDISSUETYPE;

		}

		String op_3141 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3141.x"));

		if (AmtCommonUtils.isResourceDefined(op_3141)) {

			state = MODIFYGOSUBTYPEA;

		}

		String op_3142 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3142.x"));

		if (AmtCommonUtils.isResourceDefined(op_3142)) {

			state = MODIFYGOSUBTYPEB;

		}

		String op_3143 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3143.x"));

		if (AmtCommonUtils.isResourceDefined(op_3143)) {

			state = MODIFYGOSUBTYPEC;

		}

		String op_3151 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3151.x"));

		if (AmtCommonUtils.isResourceDefined(op_3151)) {

			state = MODIFYEDITSUBTYPEA;

		}

		String op_3152 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3152.x"));

		if (AmtCommonUtils.isResourceDefined(op_3152)) {

			state = MODIFYEDITSUBTYPEB;

		}

		String op_3153 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3153.x"));

		if (AmtCommonUtils.isResourceDefined(op_3153)) {

			state = MODIFYEDITSUBTYPEC;

		}

		String op_3154 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3154.x"));

		if (AmtCommonUtils.isResourceDefined(op_3154)) {

			state = MODIFYEDITSUBTYPED;

		}

		String op_3121 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3121.x"));

		if (AmtCommonUtils.isResourceDefined(op_3121)) {

			state = MODIFYCANCSUBTYPE;

		}

		String op_3122 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_3122.x"));

		if (AmtCommonUtils.isResourceDefined(op_3122)) {

			state = MODIFYCANCIDENTF;

		}

		String op_316 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_316.x"));

		if (AmtCommonUtils.isResourceDefined(op_316)) {

			state = MODIFYCONTIDENTF;

		}

		String op_unifcancel = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_unifcancel.x"));

		if (AmtCommonUtils.isResourceDefined(op_unifcancel)) {

			state = MODIFYUNIFIEDCANCEL;

		}

		String op_326 = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("op_326.x"));

		if (AmtCommonUtils.isResourceDefined(op_326)) {

			state = MODIFYSUBMITTODB;

		}

		Global.println("state in getModifySubActionState()===" + state);

		return state;

	}

} //end of class
