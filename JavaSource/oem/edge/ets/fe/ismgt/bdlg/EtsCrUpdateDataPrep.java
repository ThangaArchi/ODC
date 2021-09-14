/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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

import java.sql.SQLException;
import java.util.Vector;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoDAO;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoIssueDocDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFileAttachUtils;
import oem.edge.ets.fe.ismgt.model.EtsCrAttach;
import oem.edge.ets.fe.ismgt.model.EtsCrDbModel;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsCrActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrUpdateDataPrep extends EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants, EtsCrActionConstants {

	public static final String VERSION = "1.34";
	private int currentstate = 0;
	private EtsIssParseFormParams parseParams;

	/**
	 * 
	 */

	public EtsCrUpdateDataPrep(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.currentstate = currentstate;
		this.parseParams = new EtsIssParseFormParams(etsIssObjKey);

	}

	/**
			 * 
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsCrProbInfoModel getViewCrInfoDetails() throws SQLException, Exception {

		EtsCrViewDataPrep crViewDataPrep = new EtsCrViewDataPrep(getEtsIssObjKey(), currentstate);

		EtsCrProbInfoModel crInfoModel = crViewDataPrep.getViewCrInfoDetails();

		//			get edge_problem_id from href
		String etsId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("etsId"));

		Global.println("ets id in getViewCrInfoDetails===" + etsId);

		//	set prev one to null
		setCrInfoIntoSessn(crInfoModel, etsId);

		return crInfoModel;

	}

	/**
						 * This method will load step1 details into session and get the step 2 details
						 * 
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */
	public EtsCrProbInfoModel getSubmitCommentsDetails() throws SQLException, Exception {

		//		get edge_problem_id from href
		String etsId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("etsId"));

		//		get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//get the params model frm the form
		EtsCrProbInfoModel crInfoModel = parseParams.loadCommentsIntoCrInfoModel();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateCommentsFormFields(crInfoModel);

		//print error msg
		Global.println("err msg getSubmitCommentsDetails ()=====" + errMsg);

		//get cancel state or scrn state, 
		int cancelstate = getCancelActionState();

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			crSessnModel.setCommFromCust(crInfoModel.getCommFromCust()); //add comments
			crSessnModel.setErrMsg(errMsg);
			crSessnModel.setCurrentActionState(currentstate);
			crSessnModel.setCancelActionState(cancelstate); //make cancel state
			crSessnModel.setNextActionState(COMMENTSCRFIRSTPAGE);

			return crSessnModel;

		} else {

			//add the comments
			//when there are no err msgs

			///
			crSessnModel.setCommFromCust(crInfoModel.getCommFromCust()); //add comments

			//upload the updated object into session//
			setCrInfoIntoSessn(crSessnModel, etsId);

			return getUpdateCrDetails();

		} //end of no err msg

	}

	/**
							 * This method will load step1 details and check for validations
							 * 
							 * @return
							 * @throws SQLException
							 * @throws Exception
							 */
	public String validateCommentsFormFields(EtsCrProbInfoModel crInfoModel) throws Exception {

		StringBuffer errsb = new StringBuffer();

		//cehck for comments

		if (!AmtCommonUtils.isResourceDefined(crInfoModel.getCommFromCust())) {

			errsb.append("Please provide comments.");
			errsb.append("<br />");

		} else {

			String tempDesc = AmtCommonUtils.getTrimStr(crInfoModel.getCommFromCust());

			if (tempDesc.length() > 1000) {

				errsb.append("Please provide maximum of 1000 characters for comments.");
				errsb.append("<br />");

			}

		}

		//get from session
		return errsb.toString();

	}

	public int getCancelActionState() {

		String strcancelstate = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("cancelstate"));
		int cancelstate = 0;

		if (AmtCommonUtils.isResourceDefined(strcancelstate)) {

			cancelstate = Integer.parseInt(strcancelstate);
		}
		return cancelstate;

	}

	/**
			 * 
			 * To set the usr1 info into session
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public void setCrInfoIntoSessn(EtsCrProbInfoModel crInfoModel, String uniqObjId) {

		getActSessnParams().setSessnCrProbInfoModel(crInfoModel, uniqObjId);
	}

	/**
			 * To get the Issue from sessn
			 */

	public EtsCrProbInfoModel getCrInfoFromSessn() {

		//	get edge_problem_id from href
		String etsId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("etsId"));

		EtsCrProbInfoModel crInfoModel = getActSessnParams().getSessnCrProbInfoModel(etsId);

		return crInfoModel;

	}

	/**
						 * 
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */

	public EtsCrProbInfoModel getUpdateCrDetails() throws SQLException, Exception {

		//		get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//		get cancelstate
		int cancelstate = getCancelActionState();

		boolean successsubmit = submitCrInfoToDb(crSessnModel);

		if (!successsubmit) {

			crSessnModel.setNextActionState(ERRINACTION);

			StringBuffer sberr = new StringBuffer();
			sberr.append("An error has occured while processing the change request. Please try after sometime or contact");
			sberr.append(" Customer Connect Help Desk for more help.");

			//set error msg
			getEtsIssObjKey().getRequest().setAttribute("actionerrmsg", sberr.toString());

		} else {

			crSessnModel.setNextActionState(COMMENTSCRCONFIRMPAGE);

		}

		//		set all states
		crSessnModel.setErrMsg("");
		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate);

		return crSessnModel;
		//return getViewCrInfoDetails();

	}

	public boolean submitCrInfoToDb(EtsCrProbInfoModel crSessnModel) throws SQLException, Exception {

		EtsCrPmoDAO crDao = new EtsCrPmoDAO();

		//		NAME AND VAL

		EtsCrDbModel crDbModel = new EtsCrDbModel();

		//key details
		crDbModel.etsId = crSessnModel.getEtsId();
		crDbModel.pmoId = crSessnModel.getPmoId();
		crDbModel.pmoProjectId = crSessnModel.getPmoProjectId();
		crDbModel.parentPmoId = crSessnModel.getParentPmoId();
		crDbModel.refNo = crSessnModel.getRefNo();
		crDbModel.infoSrcFlag = crSessnModel.getInfoSrcFlag();
		crDbModel.CRType = crSessnModel.getCrType();

		//last user id

		crDbModel.lastUserId = crSessnModel.getLastUserId();

		//txn flag
		crDbModel.statusFlag = "U";
		
		//add comments eneterd by whom on what time??
		StringBuffer comsb = new StringBuffer();
		String userName=getEtsIssObjKey().getEs().gFIRST_NAME+" " +getEtsIssObjKey().getEs().gLAST_NAME;
		String lastUserIrId=getEtsIssObjKey().getEs().gIR_USERN;
		String dateString = AmtCommonUtils.getDateString("MMM d, yyyy");
			
		comsb.append("--- Comment by "+userName+" [" + lastUserIrId + "] on " + dateString + "\n");
		
		String commFromCust = comsb.toString() + crSessnModel.getCommFromCust()+"\n";
		crSessnModel.setCommFromCust(commFromCust);

		//update pmo_issue_info
		int updinfocount = crDao.updateCrComments(crSessnModel);

		//	update pmo_rtf
		int updrtfcount = crDao.updateCrCommentsRtf(crSessnModel, getEtsIssObjKey().getPcrPropMap());

		//update txn table
		int txncount = crDao.deletePrevandInsertNewCrTxn(crDbModel);
		
		Global.println("comments count=="+updinfocount);
		Global.println("comments RTF count=="+updrtfcount);
		Global.println("tnx count count=="+txncount);

		if (updinfocount > 0 && updrtfcount > 0 && txncount > 0) {

			return true;
		}

		return false;

	}

	/**
							 * This method will load step1 details into session and get the step 2 details
							 * 
							 * @return
							 * @throws SQLException
							 * @throws Exception
							 */
	public EtsCrProbInfoModel getCancelCommentsDetails() throws SQLException, Exception {

		//	get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//	get cancelstate
		int cancelstate = getCancelActionState();

		//	set all states
		crSessnModel.setErrMsg("");
		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate);
		crSessnModel.setNextActionState(0);

		return crSessnModel;

	}

	/**
			 * 
			 * @return
			 */

	public EtsCrProbInfoModel deleteFileAttach() {

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//String edge_problem_id = (String) getEtsIssObjKey().getParams().get("edge_problem_id");
		String etsId = crSessnModel.getEtsId();
		int fileNum = getEtsIssObjKey().getFilenum();
		Global.println("etsId===" + etsId);
		Global.println("file num===" + fileNum);

		boolean success = true;

		try {

			EtsCrPmoIssueDocDAO crDocdao = new EtsCrPmoIssueDocDAO();

			crDocdao.deleteAttach(etsId, fileNum);

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
			success = false;
		}

		int cancelstate = getCancelActionState();

		crSessnModel.setErrMsg("");
		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate); //make cancel state		
		crSessnModel.setNextActionState(0);

		return crSessnModel;

	}

	/**
			 * 
			 * @return
			 */

	public EtsCrProbInfoModel doFileAttach() {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		Global.println("file desc===" + (String) getEtsIssObjKey().getParams().get("file_desc"));
		Global.println("file name===" + (String) getEtsIssObjKey().getParams().get("upload_file"));
		String etsId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("etsId"));
		Global.println("etsId===" + etsId);

		String fileErrMsg = "";

		if (!AmtCommonUtils.isResourceDefined(fileErrMsg)) {

			String errFile[] = fileUtils.doAttachForCr(getEtsIssObjKey().getRequest(), crSessnModel);
			fileErrMsg = errFile[1];

		}

		int cancelstate = getCancelActionState();

		crSessnModel.setErrMsg(fileErrMsg);
		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate); //make cancel state		
		crSessnModel.setNextActionState(0);

		return crSessnModel;

	}

	/**
			  * 
			  * @return
			  */

	public EtsCrProbInfoModel getSubmitFileattachDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		String etsId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("etsId"));

		//		update files flag form T >> Y >>E
		EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();
		int updcount = crIssueDao.updateAttachFilesWithNewFlg(etsId, "T", "E");

		boolean successsubmit = submitFileCrInfoToDb(crSessnModel);

		int cancelstate = getCancelActionState();

		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate); //increment by 1 to screen -3
		crSessnModel.setNextActionState(FILEATTACHSCRCONFIRMPAGE);

		crSessnModel.setErrMsg("");

		return crSessnModel;
		
		//return getViewCrInfoDetails();

	}

	/**
					 * This method will retrieve the Model from session
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public EtsCrProbInfoModel getCancelFileAttachDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//delete all the files currently attached
		EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();
		int deletecount = crIssueDao.deleteAttachWithFileTmpFlg(crSessnModel.getEtsId());

		//get cancelstate
		int cancelstate = getCancelActionState();

		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate); //increment by 1 to screen -3
		crSessnModel.setNextActionState(0);

		crSessnModel.setErrMsg("");

		return crSessnModel;

	}

	public boolean submitFileCrInfoToDb(EtsCrProbInfoModel crSessnModel) throws SQLException, Exception {

		EtsCrPmoDAO crDao = new EtsCrPmoDAO();

		//current files
		crSessnModel.setCommFromCust(getCommentsStrForAttachedFiles(crSessnModel.getEtsId()));

		//update pmo_issue_info
		int updinfocount = crDao.updateCrComments(crSessnModel);

		//	update pmo_rtf
		int updrtfcount = crDao.updateCrCommentsRtf(crSessnModel, getEtsIssObjKey().getPcrPropMap());

		//		NAME AND VAL

		EtsCrDbModel crDbModel = new EtsCrDbModel();

		//key details
		crDbModel.etsId = crSessnModel.getEtsId();
		crDbModel.pmoId = crSessnModel.getPmoId();
		crDbModel.pmoProjectId = crSessnModel.getPmoProjectId();
		crDbModel.parentPmoId = crSessnModel.getParentPmoId();
		crDbModel.refNo = crSessnModel.getRefNo();
		crDbModel.infoSrcFlag = crSessnModel.getInfoSrcFlag();
		crDbModel.CRType = crSessnModel.getCrType();

		//last user id

		crDbModel.lastUserId = crSessnModel.getLastUserId();

		//txn flag
		crDbModel.statusFlag = "U";

		//update txn table
		int txncount = crDao.deletePrevandInsertNewCrTxn(crDbModel);

		if (updinfocount > 0 && updrtfcount > 0 && txncount > 0) {

			return true;
		}

		return false;

	}
	
	
	public String getCommentsStrForAttachedFiles(String etsId) {

		StringBuffer sb = new StringBuffer();

		EtsCrPmoIssueDocDAO docDao = new EtsCrPmoIssueDocDAO();

		Vector fileList = docDao.getAttachedFilesWithSrcFlgForComments(etsId,"E");

		int filesize = 0;

		String docName = "";
		String userName = "";
		String lastUserIrId = "";
		String lastTimeStampStr = "";

		if (fileList != null && !fileList.isEmpty()) {

			filesize = fileList.size();
		}

		for (int i = 0; i < filesize; i++) {

			EtsCrAttach attach = (EtsCrAttach) fileList.get(i);

			//

			if (attach != null) {

				docName = attach.getDocName();
				userName = attach.getLastUserName();
				lastUserIrId = attach.getLastUserIrId();
				lastTimeStampStr = attach.getTimeStampString();

			}

			if (i == 0) {

				sb.append("---File attachment(s) in ICC\n");
			}

			//sb.append("Document&nbsp;:&nbsp; " + docName + " has been attached by " + userName + " [IBM ID: " + lastUserIrId + "] on " + lastTimeStampStr + " ");
			if(AmtCommonUtils.isResourceDefined(lastUserIrId)) {
				
				sb.append("Document  " + docName + " has been attached by " + userName + " [" + lastUserIrId + "] on " + lastTimeStampStr + " ");
			}
			
			else {
				
				sb.append("Document  " + docName + " has been attached on " + lastTimeStampStr + " ");
			}
			
			sb.append("\n");

    }

		return sb.toString();
	}

} //end of class
