/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2008                                     */
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

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.dao.EtsIssueAddFieldsDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsCustFieldSessionParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssTypeSessionParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssueTypeGuiUtils;
import oem.edge.ets.fe.ismgt.model.EtsCustFieldInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.EtsIssTypeInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssueAddFieldsBean;

import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author  Dharanendra Prasad
 *
 */
public class EtsIssAddCustFieldBdlg implements EtsIssueActionConstants, EtsIssueConstants {

	public static final String VERSION = "1.11";

	private EtsCustFieldParseParams parseParams;
	private int currentState = 0;
	private EtsIssObjectKey etsIssObjKey;
	
	private EtsCustFieldSessionParams custFieldSessnParams;

	/**
	 * 
	 */
	public EtsIssAddCustFieldBdlg(EtsIssObjectKey etsIssObjKey, int currentState) {
		//super(etsIssObjKey);
		this.custFieldSessnParams = new EtsCustFieldSessionParams(etsIssObjKey);
		this.parseParams = new EtsCustFieldParseParams(etsIssObjKey);
		this.currentState = currentState;
		this.etsIssObjKey = etsIssObjKey;
		
		
	}

	/**
			 * This method will model the issue description data 
			 */

	public EtsCustFieldInfoModel getFirstPageDets() throws SQLException, Exception {

		EtsCustFieldInfoModel custFieldModel = new EtsCustFieldInfoModel();
		
		String projectId = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getProj().getProjectId() );
		String lastUserId = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getEs().gUSERN);

		custFieldModel.setProjectId(projectId);
		custFieldModel.setLastUserId(lastUserId);

		custFieldModel.setCurrentActionState(currentState);
		custFieldModel.setCancelActionState(0);
		custFieldModel.setNextActionState(0);
		
		///set the vals into session
		setCustFieldInfoIntoSessn(custFieldModel, ADDCUSTFIELDUNIQID);
       		
		return custFieldModel;
	}


	public EtsCustFieldInfoModel getContAddCustFieldDetails() throws SQLException, Exception {

		////get from the session the latest model
		EtsCustFieldInfoModel custFieldSessnModel = getCustFieldInfoFromSessn();

		//get the params model frm the form
		EtsCustFieldInfoModel custFieldsParams = parseParams.loadScr1AddCustFieldModel();


		custFieldSessnModel.setNewFieldsReq(custFieldsParams.getNewFieldsReq() );
		custFieldSessnModel.setLastUserId(custFieldsParams.getLastUserId());
		custFieldSessnModel.setProjectId(custFieldsParams.getProjectId());
		custFieldSessnModel.setLastUserId(custFieldsParams.getLastUserId());
		custFieldSessnModel.setNextActionState(custFieldsParams.getNextActionState());
		custFieldSessnModel.setCurrentActionState(custFieldsParams.getCurrentActionState());
		custFieldSessnModel.setCancelActionState(custFieldsParams.getCancelActionState());
		
		//upload the updated object into session//
		setCustFieldInfoIntoSessn(custFieldSessnModel, ADDCUSTFIELDUNIQID);

		return custFieldSessnModel;

	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public EtsCustFieldInfoModel getExtContAddCustFieldDetails() throws SQLException, Exception {		

		EtsCustFieldInfoModel custFieldsParams = parseParams.loadScr2AddCustFieldModel();

		//upload the updated object into session//
		setCustFieldInfoIntoSessn(custFieldsParams, ADDCUSTFIELDUNIQID);
		
		return custFieldsParams;
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public EtsCustFieldInfoModel getSubmitAddCustFieldDetails() throws SQLException, Exception {		

		////get from the session the latest model
		EtsCustFieldInfoModel custFieldSessnModel = getCustFieldInfoFromSessn();
		custFieldSessnModel.setCurrentActionState(currentState);
		custFieldSessnModel.setNextActionState(ADDCUSTFIELDSUBMIT);
		custFieldSessnModel.setCancelActionState(0);
		
		EtsIssueAddFieldsDAO  issueAddFieldsDao = new EtsIssueAddFieldsDAO();
		
		EtsIssueAddFieldsBean []issueAddFields = deriveIssueAddFieldsBean(custFieldSessnModel);
		
		
		issueAddFieldsDao.addCustField(issueAddFields);
		
		return custFieldSessnModel;
	}
	
	public EtsIssueAddFieldsBean [] deriveIssueAddFieldsBean(EtsCustFieldInfoModel custFieldModel) {
		
		int num = custFieldModel.getFieldLabel().size();
		EtsIssueAddFieldsBean [] arrIssueAddFieldsBean = new EtsIssueAddFieldsBean [num];
		
		EtsIssueAddFieldsBean  issueAddFieldsBean; 
		for(int i=0; i<num; i++) {

			issueAddFieldsBean = new EtsIssueAddFieldsBean();
			
			issueAddFieldsBean.setProjectId(custFieldModel.getProjectId());
			issueAddFieldsBean.setFieldId(Integer.parseInt( custFieldModel.getFieldId().get(i).toString() ) );
			issueAddFieldsBean.setFieldLabel(custFieldModel.getFieldLabel().get(i).toString());
			issueAddFieldsBean.setLastUserId(custFieldModel.getLastUserId());
			arrIssueAddFieldsBean[i] = issueAddFieldsBean;
			
		}
		
		return arrIssueAddFieldsBean;
	}
	
	public EtsIssObjectKey getEtsIssObjKey() {
		return etsIssObjKey;
	}

	
	/**
	 * 
	 * @param usr1Model
	 * @param uniqObjId
	 */

	public void setCustFieldInfoIntoSessn(EtsCustFieldInfoModel custFieldModel, String uniqObjId) {
		custFieldSessnParams.setSessnCustFieldInfoModel(custFieldModel, uniqObjId);		
	}
	
	/**
	 * To get the Issue from sessn
	 */

	public EtsCustFieldInfoModel getCustFieldInfoFromSessn() {
		EtsCustFieldInfoModel custFieldModel = custFieldSessnParams.getSessnCustFieldInfoModel(ADDCUSTFIELDUNIQID);		
		return custFieldModel;
	}
}
