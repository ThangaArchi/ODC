	
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoDAO;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoIssueDocDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsCrDbModel;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsCrRtfModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsCrActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrViewDataPrep extends EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants, EtsCrActionConstants {

	public static final String VERSION = "1.13.1.23";
	private int currentstate = 0;

	/**
	 * 
	 */
	public EtsCrViewDataPrep(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.currentstate = currentstate;

	}

	/**
			 * 
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsCrProbInfoModel getViewCrInfoDetails(String etsId) throws SQLException, Exception {

		//	get a new model
		EtsCrProbInfoModel crInfoModel = new EtsCrProbInfoModel();

		Global.println("ETS ID in getViewCrInfoDetails===" + etsId);

		String op = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("op"));

		if (op.equals("60")) {

			//update all old files with O flag

			//		update files flag form T >> Y >>E
			EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();
			int updcount = crIssueDao.updateAttachFilesWithFlg(etsId, "O");

		}

		//get the issue details from DB
		EtsCrPmoDAO pmoDao = new EtsCrPmoDAO();

		EtsCrDbModel crDbModel = pmoDao.getPCRInfoModel(etsId);

		//key info
		String pmoId = AmtCommonUtils.getTrimStr(crDbModel.pmoId);
		String pmoProjId = AmtCommonUtils.getTrimStr(crDbModel.pmoProjectId);
		String parentPmoId = AmtCommonUtils.getTrimStr(crDbModel.parentPmoId);
		String infoSrcFlag = AmtCommonUtils.getTrimStr(crDbModel.infoSrcFlag);
		String CRType = AmtCommonUtils.getTrimStr(crDbModel.CRType);
		int refNo=crDbModel.refNo;

		//state info
		String statusFlag = AmtCommonUtils.getTrimStr(crDbModel.statusFlag);
		String stateAction = AmtCommonUtils.getTrimStr(crDbModel.stateAction);
		
				
		String probState = AmtCommonUtils.getTrimStr(crDbModel.probState);
		
		///get txn flag
		String txnStatusFlag = AmtCommonUtils.getTrimStr(crDbModel.statusFlag);

		

		//submitter info
		String probCreator = AmtCommonUtils.getTrimStr(crDbModel.probCreator);
		String custName = AmtCommonUtils.getTrimStr(crDbModel.custName);
		String custEmail = AmtCommonUtils.getTrimStr(crDbModel.custEmail);
		String custPhone = AmtCommonUtils.getTrimStr(crDbModel.custPhone);
		String custCompany = AmtCommonUtils.getTrimStr(crDbModel.custCompany);
		String submiDateStr = AmtCommonUtils.getTrimStr(crDbModel.creationDateStr);
		String dateString = EtsIssFilterUtils.formatDate(submiDateStr, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy");

		//description info
		String probClass = AmtCommonUtils.getTrimStr(crDbModel.probClass);
		String probTitle = AmtCommonUtils.getTrimStr(crDbModel.probTitle);
		String probSeverity = AmtCommonUtils.getTrimStr(crDbModel.probSeverity);
		String probDesc = AmtCommonUtils.getTrimStr(crDbModel.probDesc);

		//comments info
		String comments = AmtCommonUtils.getTrimStr(crDbModel.commFromCust);

		//owner info
		String ownerId = AmtCommonUtils.getTrimStr(crDbModel.ownerIrId);
		String ownerName = AmtCommonUtils.getTrimStr(crDbModel.ownerName);

		//RTF List
		ArrayList rtfList = crDbModel.rtfList;

		//get RTF MAP
		HashMap rtfMap = getRtfMap(rtfList);

		//any err msg
		String errMsg = getErrMsg(statusFlag);
		
		
//		only for PCR from ETS, not from PMO 
			if (!infoSrcFlag.equals("P")) { //for ISSUE from ETS

				//get updated state action if txn flag=N/T
				probState = getUpdatedStateActionForCR(pmoId, stateAction,probState, txnStatusFlag);

			} else { //for issues from PMO

				if (AmtCommonUtils.isResourceDefined(pmoId) && !AmtCommonUtils.isResourceDefined(txnStatusFlag)) {

				} else if (AmtCommonUtils.isResourceDefined(pmoId) && AmtCommonUtils.isResourceDefined(txnStatusFlag)) {

					//	get updated state action if txn flag=N/T
					probState = getUpdatedStateActionForCR(pmoId, stateAction,probState, txnStatusFlag);

				}
			}

		/////assign to info model

		//set key info
		crInfoModel.setEtsId(etsId);
		crInfoModel.setPmoId(pmoId);
		crInfoModel.setPmoProjectId(pmoProjId);
		crInfoModel.setParentPmoId(parentPmoId);
		crInfoModel.setInfoSrcFlag(infoSrcFlag);
		crInfoModel.setCrType(CRType);
		crInfoModel.setRefNo(refNo);

		//status info
		crInfoModel.setStateAction(stateAction);
		crInfoModel.setStatusFlag(statusFlag);

		//submitter info
		crInfoModel.setProbCreator(probCreator);
		crInfoModel.setCustCompany(custCompany);
		crInfoModel.setCustEmail(custEmail);
		crInfoModel.setCustPhone(custPhone);
		crInfoModel.setCustName(custName);
		crInfoModel.setCreationDateStr(dateString);

		//description info

		ArrayList prevProbSevList = new ArrayList();
		prevProbSevList.add(probSeverity);

		crInfoModel.setProbClass(probClass);
		crInfoModel.setPrevProbSevList(prevProbSevList);
		crInfoModel.setProbTitle(probTitle);
		crInfoModel.setProbDesc(probDesc);
		crInfoModel.setProbState(probState);

		//comments
		crInfoModel.setCommFromCust(comments);

		//owner info
		crInfoModel.setOwnerIrId(ownerId);
		crInfoModel.setOwnerName(ownerName);

		//RTF list
		crInfoModel.setRtfList(rtfList);

		//RTF Map
		crInfoModel.setRtfMap(rtfMap);

		//error msg
		crInfoModel.setErrMsg(errMsg);

		//last user id
		crInfoModel.setLastUserId(getEtsIssObjKey().getEs().gUSERN);

		return crInfoModel;

	}

	/**
		 * 
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */
	public EtsCrProbInfoModel getViewCrInfoDetails() throws SQLException, Exception {

		//	get a new model
		EtsCrProbInfoModel crInfoModel = new EtsCrProbInfoModel();

		//	get edge_problem_id from href

		String etsId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("etsId"));

		Global.println("ETS ID in getViewCrInfoDetails===" + etsId);

		return getViewCrInfoDetails(etsId);
	}

	public String getErrMsg(String txnFlag) {

		HashMap pcrPropMap = getEtsIssObjKey().getPcrPropMap();

		String errMsg = "";

		if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE"))) {

			errMsg = (String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE.msg");
		}

		if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE"))) {

			errMsg = (String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE.msg");
		}

		Global.println("ERror Msg in updated Error Msg===" + errMsg);

		return errMsg;
	}

	public String getUpdatedStateAction(String pmoId, String stateAction, String txnFlag) {

		HashMap pcrPropMap = getEtsIssObjKey().getPcrPropMap();

		/*if (!AmtCommonUtils.isResourceDefined(pmoId)) {
		
			stateAction = "New";
			
		} else */

		if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE"))) {

			stateAction = (String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE.staction");

		}

		if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE"))) {

			stateAction = (String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE.staction");

		}

		/*else if (AmtCommonUtils.isResourceDefined(pmoId) && !txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE")) && !txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE")) && !txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_ACKED_STATE"))) {
		
			stateAction = "In Process";
		}*/

		Global.println("stateAction in updated getUpdatedStateAction===" + stateAction);

		return stateAction;
	}

	public HashMap getRtfMap(ArrayList rtfList) {

		HashMap rtfMap = new HashMap();
		int rtfsize = 0;

		String rtfId="";
		String rtfAliasName = "";
		String rtfBlobStr = "";

		if (EtsIssFilterUtils.isArrayListDefndWithObj(rtfList)) {

			rtfsize = rtfList.size();
		}

		for (int i = 0; i < rtfsize; i++) {

			EtsCrRtfModel crRtfModel = (EtsCrRtfModel) rtfList.get(i);

			rtfAliasName = crRtfModel.getRtfAliasName();
			rtfBlobStr = crRtfModel.getRtfBlobStr();
			rtfId=AmtCommonUtils.getTrimStr(""+crRtfModel.getRtfId());
			
			rtfMap.put(rtfId, crRtfModel);

		}

		return rtfMap;

	}

} //end of class
