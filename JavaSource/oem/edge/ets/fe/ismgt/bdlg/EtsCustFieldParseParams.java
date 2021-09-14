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

import java.util.ArrayList;
import java.util.Hashtable;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.dao.EtsIssueAddFieldsDAO;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsCustFieldInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssueAddFieldsBean;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;


public class EtsCustFieldParseParams  implements EtsIssueActionConstants {

	public static final String VERSION = "1.10";
	private EtsIssObjectKey etsIssObjKey;

	/**
	 * 
	 */
	public EtsCustFieldParseParams(EtsIssObjectKey etsIssObjKey) {
		super();
		this.etsIssObjKey = etsIssObjKey;

	}

	/**
	 * 
	 * @return
	 */
	public EtsCustFieldInfoModel loadScr1AddCustFieldModel() {

		EtsCustFieldInfoModel custFieldModel = new EtsCustFieldInfoModel();

		String custFieldReq = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("selCustFields"));
	

		//issue source
		String issuesource = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issuesource"));

		String projectId = AmtCommonUtils.getTrimStr(etsIssObjKey.getProj().getProjectId());
		String lastUserName = AmtCommonUtils.getTrimStr(etsIssObjKey.getEs().gUSERN );
		
		Global.println("issue source===" + issuesource);

		custFieldModel.setNewFieldsReq(Integer.parseInt(custFieldReq));
		custFieldModel.setProjectId(projectId);
		custFieldModel.setCurrentActionState(2410);
		custFieldModel.setNextActionState(2411);
		custFieldModel.setLastUserId(lastUserName);

		return custFieldModel;
	}
	
	/**
	 * 
	 * @return
	 */
	public EtsCustFieldInfoModel loadScr2AddCustFieldModel() {		
		ArrayList arrLstFieldId = new ArrayList();
		ArrayList arrLstFieldLabel = new ArrayList();
		boolean isValidData = true;
		EtsCustFieldInfoModel custFieldModel;
		
		String custFieldReq = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("numCustFields"));
		String custFieldId = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("hidCustFieldId"));
		
		int intCustFields = 0;
		int intCustFieldId = 0;
		if (AmtCommonUtils.isResourceDefined(custFieldReq) ) {
			intCustFields = Integer.parseInt(custFieldReq);			
		}

		//if (AmtCommonUtils.isResourceDefined(custFieldId) ) {
		//	intCustFieldId = Integer.parseInt(custFieldId);			
		//}
		
		//issue source
		String issuesource = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issuesource"));
		String projectId = AmtCommonUtils.getTrimStr(etsIssObjKey.getProj().getProjectId());
		String lastUserName = AmtCommonUtils.getTrimStr(etsIssObjKey.getEs().gUSERN );
		
		Global.println("issue source===" + issuesource);
		
		custFieldModel = new EtsCustFieldInfoModel();
		custFieldModel.setProjectId(projectId);
		custFieldModel.setLastUserId(lastUserName);

		EtsIssueAddFieldsDAO issAddFieldsDAO = new EtsIssueAddFieldsDAO();
		ArrayList arrLstCustFieldIDs = issAddFieldsDAO.getCurrentCustFieldId(projectId);
		ArrayList arrLstAvailableCustFieldIDs = new ArrayList();
		
		for(int i = 1; i <= EtsIssueConstants.MAXCUSTFIELDS; i++ ) {
		  if(! arrLstCustFieldIDs.contains(new Integer(i) ) ) {
		  	arrLstAvailableCustFieldIDs.add( (new Integer( i ) ) );
		  }
		}
							
		for(int i=1; i <= intCustFields; i++ ) {
			//arrLstFieldId.add((i-1), new Integer(i + intCustFieldId) );						
			if( AmtCommonUtils.isResourceDefined( (etsIssObjKey.getParams().get("custField"+i) ).toString() ) ) {
				arrLstFieldId.add((i-1), new Integer( arrLstAvailableCustFieldIDs.get(i-1).toString() ) );
				arrLstFieldLabel.add((i-1), AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("custField"+i)) );	
			} else {
				isValidData = false;
				break;
			}
						
		}
		if(isValidData) {
			custFieldModel.setFieldId(arrLstFieldId);
			custFieldModel.setFieldLabel(arrLstFieldLabel);
			custFieldModel.setCurrentActionState(2411);
			custFieldModel.setNextActionState(2413);		
			custFieldModel.setErrMsg("");
		} else {
			custFieldModel.setErrMsg("Please provide valid custom field label. Blank custom field label are not allowed.<br />");
			custFieldModel.setNewFieldsReq(intCustFields);
			custFieldModel.setCurrentActionState(2411);
			custFieldModel.setNextActionState(2411);
			custFieldModel.setCancelActionState(0);
		}
		
		return custFieldModel;
	}
	
	/**
	 * 
	 * @param custFieldSessnModel
	 * @return
	 */
	public EtsCustFieldInfoModel loadScr1UpdateCustFieldModel(EtsCustFieldInfoModel custFieldSessnModel, String updateUserId) {
		int custFieldNum = custFieldSessnModel.getFieldLabel().size();
		ArrayList arrLstSelectedCustFieldId = new ArrayList();
		ArrayList arrLstSelectedCustFieldLabel = new ArrayList();	
		
		String custFieldId = "";
		String custFieldLabel = "";
		Hashtable htFieldIdLabelMap = deriveCustFieldIdLabelMap(custFieldSessnModel);
		for(int i=1; i<=custFieldNum; i++) {
			custFieldId = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("custFieldId"+i));
			custFieldLabel = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("custFieldLabel"+i));
			
			if(AmtCommonUtils.isResourceDefined(custFieldId) && AmtCommonUtils.isResourceDefined(custFieldLabel)  ) {
								
				if(! htFieldIdLabelMap.get(custFieldId).toString().equals(custFieldLabel) ) {
					arrLstSelectedCustFieldId.add(custFieldId);
					arrLstSelectedCustFieldLabel.add(custFieldLabel);
				}
			}			
		}
		custFieldSessnModel.setFieldId(arrLstSelectedCustFieldId);
		custFieldSessnModel.setFieldLabel(arrLstSelectedCustFieldLabel);
		custFieldSessnModel.setLastUserId(updateUserId);
		custFieldSessnModel.setCurrentActionState(2431);
		custFieldSessnModel.setNextActionState(2432);		
		return custFieldSessnModel;
	}
	
    /**
     * 
     * @param custFieldModel
     * @return
     */	
	public Hashtable deriveCustFieldIdLabelMap(EtsCustFieldInfoModel custFieldModel) {		
		int num = custFieldModel.getFieldLabel().size();
		Hashtable htFieldIdLabel = new Hashtable();		 
		for(int i=0; i<num; i++) {
			htFieldIdLabel.put(custFieldModel.getFieldId().get(i).toString(), custFieldModel.getFieldLabel().get(i).toString());
		}
		return htFieldIdLabel;
	}
	
	
	/**
	 * 
	 * @param custFieldSessnModel
	 * @return
	 */
	public EtsCustFieldInfoModel loadScr1RemCustFieldModel(EtsCustFieldInfoModel custFieldSessnModel) {
		
		int custFieldNum = custFieldSessnModel.getFieldLabel().size();
		ArrayList arrLstSelectedCustFieldId = new ArrayList();
		ArrayList arrLstSelectedCustFieldLabel = new ArrayList();		
		String custFieldId = "";
		for(int i=1; i<=custFieldNum; i++) {
			custFieldId = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("custField"+i));
			if(AmtCommonUtils.isResourceDefined(custFieldId) ) {
				arrLstSelectedCustFieldId.add(custFieldId);
				int index = custFieldSessnModel.getFieldId().indexOf(new Integer(custFieldId));
				arrLstSelectedCustFieldLabel.add(custFieldSessnModel.getFieldLabel().get(index).toString()); 
			}			
		}
		if(arrLstSelectedCustFieldId.size() > 0  ) {
			custFieldSessnModel.setErrMsg("");
			custFieldSessnModel.setFieldId(arrLstSelectedCustFieldId);
			custFieldSessnModel.setFieldLabel(arrLstSelectedCustFieldLabel);			
		} else {
			custFieldSessnModel.setErrMsg("Please select atleast one custom field which you want to remove and click 'Continue' to proceed.<br />");
		}
		return custFieldSessnModel;
	}
	
} //end of class
