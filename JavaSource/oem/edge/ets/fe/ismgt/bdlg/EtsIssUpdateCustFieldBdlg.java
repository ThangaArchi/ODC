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

/**
 * @author  Dharanendra Prasad
 *
 */

import java.sql.SQLException;
import java.util.ArrayList;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.dao.EtsIssueAddFieldsDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsCustFieldSessionParams;
import oem.edge.ets.fe.ismgt.model.EtsCustFieldInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssueAddFieldsBean;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

public class EtsIssUpdateCustFieldBdlg  implements EtsIssueActionConstants, EtsIssueConstants {

	public static final String VERSION = "1.00";

	private EtsCustFieldParseParams parseParams;
	private int currentState = 0;
	private EtsIssObjectKey etsIssObjKey;
	
	private EtsCustFieldSessionParams custFieldSessnParams;

	/**
	 * 
	 */
	public EtsIssUpdateCustFieldBdlg(EtsIssObjectKey etsIssObjKey, int currentState) {
		//super(etsIssObjKey);
		this.custFieldSessnParams = new EtsCustFieldSessionParams(etsIssObjKey);
		this.parseParams = new EtsCustFieldParseParams(etsIssObjKey);
		this.currentState = currentState;
		this.etsIssObjKey = etsIssObjKey;		
	}

	/**
	 * This method will model the custom fields data
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */ 
	public EtsCustFieldInfoModel getFirstPageDets() throws SQLException, Exception {
		
		String projectId = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getProj().getProjectId() );
		String lastUserId = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getEs().gUSERN);

		EtsIssueAddFieldsDAO  issueAddFieldsDao = new EtsIssueAddFieldsDAO();
		EtsIssueAddFieldsBean []issueAddFields = issueAddFieldsDao.getAllCustFields(projectId) ;
		EtsCustFieldInfoModel custFieldModel = deriveCustFieldInfoModel(issueAddFields);
		
		custFieldModel.setProjectId(projectId);
		custFieldModel.setLastUserId(lastUserId);
		custFieldModel.setCurrentActionState(currentState);
		custFieldModel.setCancelActionState(0);
		custFieldModel.setNextActionState(0);
		
		///set the vals into session
		setCustFieldInfoIntoSessn(custFieldModel, UPDATECUSTFIELDUNIQID);
       		
		return custFieldModel;
	}


	public EtsCustFieldInfoModel getContUpdateCustFieldDetails() throws SQLException, Exception {

		////get from the session the latest model
		EtsCustFieldInfoModel custFieldSessnModel = getCustFieldInfoFromSessn();

		//get the params model frm the form
		EtsCustFieldInfoModel custFieldsParams = parseParams.loadScr1UpdateCustFieldModel(custFieldSessnModel, etsIssObjKey.getEs().gUSERN );

		//upload the updated object into session//
		setCustFieldInfoIntoSessn(custFieldSessnModel, UPDATECUSTFIELDUNIQID);

		return custFieldSessnModel;

	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public EtsCustFieldInfoModel getSubmitUpdateCustFieldDetails() throws SQLException, Exception {		
		////get from the session the latest model
		EtsCustFieldInfoModel custFieldSessnModel = getCustFieldInfoFromSessn();
		//custFieldSessnModel.setCurrentActionState(currentState);
		//custFieldSessnModel.setCancelActionState(0);
		String projectId = custFieldSessnModel.getProjectId();
		String lastUserId = custFieldSessnModel.getLastUserId();
				
		EtsIssueAddFieldsDAO  issueAddFieldsDao = new EtsIssueAddFieldsDAO();
		
		EtsIssueAddFieldsBean []issueAddFields = deriveIssueAddFieldsBean(custFieldSessnModel);
		
		EtsIssueAddFieldsBean [] updateIssueAddFields = null;
		EtsCustFieldInfoModel custFieldInfoModel = null;
		if(issueAddFieldsDao.updateCustField(issueAddFields)) {
			updateIssueAddFields = issueAddFieldsDao.getAllCustFields(projectId);
			custFieldInfoModel = deriveCustFieldInfoModel(updateIssueAddFields);
			custFieldInfoModel.setCurrentActionState(2430);
			custFieldInfoModel.setNextActionState(0);
			custFieldInfoModel.setCancelActionState(0);
		}
		
		return custFieldInfoModel;
	}
	
	/**
	 * 
	 * @param custFieldModel
	 * @return
	 */
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
	
	public EtsCustFieldInfoModel  deriveCustFieldInfoModel(EtsIssueAddFieldsBean [] arrIssueAddFieldsBean) {
		ArrayList arrLstFieldIds = new ArrayList();
		ArrayList arrLstFieldLabels = new ArrayList();
		int num = arrIssueAddFieldsBean.length;
		EtsCustFieldInfoModel custFieldModel = new EtsCustFieldInfoModel();
		
		EtsIssueAddFieldsBean  issueAddFieldsBean; 
		for(int i=0; i<num; i++) {
			issueAddFieldsBean = arrIssueAddFieldsBean[i];
			arrLstFieldIds.add(i, new Integer(issueAddFieldsBean.getFieldId()));
			arrLstFieldLabels.add(i, issueAddFieldsBean.getFieldLabel());

		}
		custFieldModel.setFieldId(arrLstFieldIds);
		custFieldModel.setFieldLabel(arrLstFieldLabels);		
		return custFieldModel;				
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
		EtsCustFieldInfoModel custFieldModel = custFieldSessnParams.getSessnCustFieldInfoModel(UPDATECUSTFIELDUNIQID);		
		return custFieldModel;
	}


}
