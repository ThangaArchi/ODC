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
package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.amt.AmtGenericMail;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssReqCreateNewIssTypeDataPrep extends EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.34";
	private EtsIssParseFormParams parseParams;
	private int currentstate = 0;

	/**
		 * @param etsIssObjKey
		 */
	public EtsIssReqCreateNewIssTypeDataPrep(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.parseParams = new EtsIssParseFormParams(etsIssObjKey);
		this.currentstate = currentstate;

	}

	/**
		 * This method will model the issue description data 
		 */

	public EtsIssProbInfoUsr1Model getFirstPageDets() throws SQLException, Exception {

		//current one
		EtsIssProbInfoUsr1Model usr1Model = new EtsIssProbInfoUsr1Model();

		///submitter details
		usr1Model.setCustName(getEtsIssObjKey().getEs().gFIRST_NAME + " " + getEtsIssObjKey().getEs().gLAST_NAME);
		usr1Model.setCustEmail(getEtsIssObjKey().getEs().gEMAIL);
		usr1Model.setCustPhone(getEtsIssObjKey().getEs().gPHONE);

		if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I")) {

			usr1Model.setCustCompany("IBM");

		} else {

			usr1Model.setCustCompany(getEtsIssObjKey().getEs().gASSOC_COMP);
		}

		usr1Model.setCustProject(getEtsIssObjKey().getProj().getName());

		///set the vals
		setUsr1InfoIntoSessn(usr1Model, REQCREATEISSTYPEUNIQID);

		usr1Model.setErrMsg("");
		usr1Model.setCurrentActionState(currentstate);
		usr1Model.setCancelActionState(0); //make cancel state
		usr1Model.setNextActionState(0);

		return usr1Model;

	}

	/**
	  * 
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */

	public ArrayList getOwnerListDetails(String issueAccess) throws SQLException, Exception {

		//		get proj member DAO and get details
		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		ArrayList projMemList = projDao.getProjMemberListWithUserType(getEtsIssObjKey().getProj().getProjectId());
		ArrayList userTypeList = new ArrayList();

		int projsize = 0;
		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			projsize = projMemList.size();

			if (!issueAccess.equals("Internal")) {

				for (int i = 0; i < projsize; i = i + 4) {

					etsUserEdgeId = (String) projMemList.get(i);
					etsUserNameWithIrId = (String) projMemList.get(i + 1);
					etsUserType = (String) projMemList.get(i + 2);

					if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I") && etsUserType.equals("I")) {

						etsUserNameWithIrId = etsUserNameWithIrId + " *";

					} //if user type is IBM

					userTypeList.add(etsUserEdgeId);
					userTypeList.add(etsUserNameWithIrId);

				} //end of for

			} // end of for iss type=IBM ONLY !=Y

			else {

				for (int i = 0; i < projsize; i = i + 4) {

					etsUserEdgeId = (String) projMemList.get(i);
					etsUserNameWithIrId = (String) projMemList.get(i + 1);
					etsUserType = (String) projMemList.get(i + 2);

					if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I") && etsUserType.equals("I")) {

						userTypeList.add(etsUserEdgeId);
						userTypeList.add(etsUserNameWithIrId);

					} //if user type is IBM

				} //end of for

			} //end of check iss ibm type=Y

		} //if projMemelist is defined

		return userTypeList;
	}

	/**
				 * This method will load step1 details and check for validations
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */
	public String validateScrn1FormFields(EtsIssProbInfoUsr1Model usrParamsInfo1) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//cehck for issue type name

		if (!AmtCommonUtils.isResourceDefined(usrParamsInfo1.getIssueType())) {

			errsb.append("Please provide issue type name.");
			errsb.append("<br />");

		} else {

			String tempIssueType = AmtCommonUtils.getTrimStr(usrParamsInfo1.getIssueType());

			if (tempIssueType.length() > 50) {

				errsb.append("Please provide maximum of 50 characters for issue type name.");
				errsb.append("<br />");

			}
		}

		//cehck for issue access

		if (!AmtCommonUtils.isResourceDefined(usrParamsInfo1.getIssueAccess())) {

			errsb.append("Please provide issue access.");
			errsb.append("<br />");

		}

		//get from session
		return errsb.toString();

	}

	/**
					 * This method will load step1 details and check for validations
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */
	public String validateScrn2FormFields(EtsIssProbInfoUsr1Model usrParamsInfo1) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//cehck for issue type name

		//			check for issue owner

		if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevNotifyList())) {

			if (usrParamsInfo1.getPrevNotifyList().contains("NONE")) {

				errsb.append("Please select issue owner.");
				errsb.append("<br />");

			}
		}

		//get from session
		return errsb.toString();

	}

	/**
							 * This method will load step1 details into session and get the step 2 details
							 * 
							 * @return
							 * @throws SQLException
							 * @throws Exception
							 */
	public EtsIssProbInfoUsr1Model getContinueReqDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr1CreateNewIssTypeIntoUsr1Model();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateScrn1FormFields(usrParamsInfo1);

		//print error msg
		Global.println("err msg getContinueReqDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			usrSessnModel.setIssueType(usrParamsInfo1.getIssueType()); //add comments
			usrSessnModel.setIssueAccess(usrParamsInfo1.getIssueAccess()); //add comments

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(0); //make cancel state
			usrSessnModel.setNextActionState(CREATEISSUETYPE1STPAGE);

		} else {

			//add the comments
			//when there are no err msgs

			usrSessnModel.setIssueType(usrParamsInfo1.getIssueType()); //add comments
			usrSessnModel.setIssueAccess(usrParamsInfo1.getIssueAccess()); //add comments

			//upload the updated object into session//
			setUsr1InfoIntoSessn(usrSessnModel, REQCREATEISSTYPEUNIQID);

			//			set owner list details
			usrSessnModel.setNotifyList(getOwnerListDetails(usrParamsInfo1.getIssueAccess()));
			usrSessnModel.setPrevNotifyList(usrSessnModel.getPrevNotifyList());

			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(0); //make cancel state
			usrSessnModel.setNextActionState(0);

		} //end of no err msg

		return usrSessnModel;

	}

	/**
						 * This method will load step1 details into session and get the step 2 details
						 * 
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */
	public EtsIssProbInfoUsr1Model getSubmitReqDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr2CreateNewIssTypeIntoUsr1Model();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateScrn2FormFields(usrParamsInfo1);

		//print error msg
		Global.println("err msg getSubmitReqDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			usrSessnModel.setPrevNotifyList(usrParamsInfo1.getPrevNotifyList());

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(0); //make cancel state
			usrSessnModel.setNextActionState(CREATEISSUETYPECONTINUE);

		} else {

			//add the comments
			//when there are no err msgs

			usrSessnModel.setPrevNotifyList(usrParamsInfo1.getPrevNotifyList());

			//upload the updated object into session//
			setUsr1InfoIntoSessn(usrSessnModel, REQCREATEISSTYPEUNIQID);

			//send mails to etsadmin@us.ibm.com
			usrSessnModel = sendEmailForCreateNewIssType(getEtsIssObjKey());

			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(0); //make cancel state

		} //end of no err msg

		return usrSessnModel;

	}

	/**
			 * To get the Issue from sessn
			 */

	public EtsIssProbInfoUsr1Model getUsr1InfoFromSessn() {

		EtsIssProbInfoUsr1Model usr1Model = getActSessnParams().getSessnProbUsr1InfoModel(REQCREATEISSTYPEUNIQID);

		return usr1Model;

	}

	/**
				 * 
				 * To set the usr1 info into session
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public void setUsr1InfoIntoSessn(EtsIssProbInfoUsr1Model usr1Model, String uniqObjId) {

		getActSessnParams().setSessnProbUsr1InfoModel(usr1Model, uniqObjId);
	}

	/**
					 * 
					 * @return
					 */

	public EtsIssProbInfoUsr1Model sendEmailForCreateNewIssType(EtsIssObjectKey etsIssObjKey) {

		boolean flag = false;
		EtsIssProbInfoUsr1Model usrSessnModel = new EtsIssProbInfoUsr1Model();

		try {

			//get the final issue details
			usrSessnModel = getUsr1InfoFromSessn();

			HashMap propMap = etsIssObjKey.getPropMap();

			String mailTo = (String) propMap.get("req.crt.isstype.mailto");

			Hashtable hMail = getMailHash(usrSessnModel);

			flag = sendReqMail(mailTo, "sendEtsIssCrtNewIssType", hMail);

			if (!flag) {

				usrSessnModel.setNextActionState(CREATEISSUETYPEMAILERR);

			} else {

				usrSessnModel.setNextActionState(0);

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in sendEmailForCreateNewIssType", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			flag = false;

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in sendEmailForCreateNewIssType", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			flag = false;

		}

		return usrSessnModel;
	}

	public boolean sendReqMail(String mailTo, String sMailFunc, Hashtable hMail) {

		Connection conn = null;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			AmtGenericMail.sendEdgeMail(conn, mailTo, sMailFunc, hMail);

			Global.println("db conn successully opened in sendReqMail@Create new issue type");

			flag = true;

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssReqCreateNewIssTypeDataPrep", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			flag = false;

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssReqCreateNewIssTypeDataPrep", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			flag = false;

		}
		
		finally {
			
			ETSDBUtils.close(conn);
		}

		return flag;

	} //end of sendDiwMail

	//	////getMsgHashTable///

	Hashtable getMailHash(EtsIssProbInfoUsr1Model usr1Model) throws SQLException, Exception {

		Hashtable hMail = new Hashtable();

		////	  	
		String projName = AmtCommonUtils.getTrimStr(usr1Model.getCustProject());
		String custName = AmtCommonUtils.getTrimStr(usr1Model.getCustName());
		String custEmail = AmtCommonUtils.getTrimStr(usr1Model.getCustEmail());
		String custPhone = AmtCommonUtils.getTrimStr(usr1Model.getCustPhone());
		String custCompany = AmtCommonUtils.getTrimStr(usr1Model.getCustCompany());

		///issue type details
		String issueTypeName = AmtCommonUtils.getTrimStr(usr1Model.getIssueType());
		String issueTypeAccess = AmtCommonUtils.getTrimStr(usr1Model.getIssueAccess());
		ArrayList prevNotifyList = usr1Model.getPrevNotifyList();

		String ownerIrId = "";
		String ownerEdgeId = "";
		String ownerFullName = "";
		String ownerEmail = "";

		/*if(EtsIssFilterUtils.isArrayListDefnd(prevNotifyList)){
			
			ownerIdDets=AmtCommonUtils.getTrimStr((String)prevNotifyList.get(0));
		}*/

		EtsProjMemberDAO projDao = new EtsProjMemberDAO();
		ArrayList ownerInfoList = projDao.getUserIdInfoList(prevNotifyList);

		if (ownerInfoList != null && !ownerInfoList.isEmpty()) {

			EtsIssOwnerInfo ownerInfoObj = new EtsIssOwnerInfo();

			ownerInfoObj = (EtsIssOwnerInfo) ownerInfoList.get(0);

			//get the vals
			ownerIrId = ownerInfoObj.getUserIrId();
			ownerEdgeId = ownerInfoObj.getUserEdgeId();
			ownerFullName = ownerInfoObj.getUserFullName();
			ownerEmail = ownerInfoObj.getUserEmail();
		}

		hMail.put("QID", projName);
		hMail.put("QID1", custName);
		hMail.put("QID2", custCompany);
		hMail.put("QID3", custEmail);
		hMail.put("QID4", custPhone);
		hMail.put("QID5", issueTypeName);
		hMail.put("QID6", issueTypeAccess);
		hMail.put("QID7", ownerIrId);
		hMail.put("QID8", ownerEdgeId);
		hMail.put("QID9", ownerFullName);
		hMail.put("QID10", ownerEmail);

		return hMail;

	}

	/**
				  * 
				  * @return
				  */

	public EtsIssProbInfoUsr1Model getCancReqNewIssTypeDetails() {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(0); //always to state of modify main page==5
		usrSessnModel.setNextActionState(MAINPAGE);

		return usrSessnModel;

	}

	/**
					  * 
					  * @return
					  */

	public EtsIssProbInfoUsr1Model getEditReqNewIssTypeDetails() {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		// get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr2CreateNewIssTypeIntoUsr1Model();

		usrSessnModel.setPrevNotifyList(usrParamsInfo1.getPrevNotifyList());

		//		upload the updated object into session//
		setUsr1InfoIntoSessn(usrSessnModel, REQCREATEISSTYPEUNIQID);

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(0); //always to state of modify main page==5
		usrSessnModel.setNextActionState(0);

		return usrSessnModel;

	}

} //end of class
