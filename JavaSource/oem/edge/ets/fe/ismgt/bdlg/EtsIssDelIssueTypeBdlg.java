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

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.EtsIssTypeInfoModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssDelIssueTypeBdlg extends EtsIssActionDataPrepAbsBean implements EtsIssueActionConstants, EtsIssueConstants {

	public static final String VERSION = "1.8";

	private EtsIssTypeParseParams parseParams;
	private int currentstate = 0;

	/**
		 * 
		 */
	public EtsIssDelIssueTypeBdlg(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.parseParams = new EtsIssTypeParseParams(etsIssObjKey);
		this.currentstate = currentstate;

	}

	/**
				 * This method will model the issue description data 
				 */

	public EtsIssTypeInfoModel getFirstPageDets() throws SQLException, Exception {

		EtsIssTypeInfoModel issTypeModel = new EtsIssTypeInfoModel();

		//get proj mem info
		EtsIssProjectMember projMem = getSubmitterProfileFromEs(getEtsIssObjKey().getEs());

		////////////get ets obj key
		String projectId = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getProj().getProjectId());
		String projectName = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getProj().getName());
		String issueClass = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getIssueClass());
		String lastUserId = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getEs().gUSERN);

		//set proj mem info into obj
		issTypeModel.setSubmitterProfile(projMem);

		//set project info
		issTypeModel.setProjectId(projectId);
		issTypeModel.setProjectName(projectName);
		issTypeModel.setIssueClass(issueClass);
		issTypeModel.setLastUserId(lastUserId);
		issTypeModel.setIssueTypeList(getDelIssTypeListDetails(issTypeModel));

		///set the vals into session
		setIssueTypeInfoIntoSessn(issTypeModel, DELISSUETYPEUNIQID);

		issTypeModel.setErrMsg("");
		issTypeModel.setCurrentActionState(currentstate);
		issTypeModel.setCancelActionState(0);
		issTypeModel.setNextActionState(0);

		return issTypeModel;

		///

	}

	/**
	 * 
	 * @param usr1Model
	 * @param uniqObjId
	 */

	public void setIssueTypeInfoIntoSessn(EtsIssTypeInfoModel issTypeModel, String uniqObjId) {

		getIssueTypeSessnParams().setSessnIssTypeInfoModel(issTypeModel, uniqObjId);
	}

	/**
		  * 
							 * @return
							 * @throws SQLException
							 * @throws Exception
							 */

	public ArrayList getDelIssTypeListDetails(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		//		get proj member DAO and get details
		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		ArrayList issTypeList = dropDao.getDeleteIssueTypesList(issTypeModel);
		ArrayList delList = new ArrayList();

		int size = 0;
		String dataId = "";
		String issType = "";

		if (EtsIssFilterUtils.isArrayListDefndWithObj(issTypeList)) {

			size = issTypeList.size();

			for (int i = 0; i < size; i++) {

				EtsDropDownDataBean dropBean = (EtsDropDownDataBean) issTypeList.get(i);

				//dataId = AmtCommonUtils.getTrimStr(dropBean.getDataId());
				issType = AmtCommonUtils.getTrimStr(dropBean.getIssueType());

				//
				//delList.add(dataId);
				delList.add(issType);

			} //end of for

		} //if projMemelist is defined

		return delList;
	}

	/**
					 * This method will load step1 details and check for validations
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */
	public String validateScrn1FormFields(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//cehck for issue type name

		//			check for issue owner

		if (EtsIssFilterUtils.isArrayListDefnd(issTypeModel.getPrevIssueTypeList())) {

			if (issTypeModel.getPrevIssueTypeList().contains("NONE")) {

				errsb.append("Please select issue type.");
				errsb.append("<br />");

			}
		}

		//cehck if the issue type is in active state, before deleting issue 
		EtsDropDownDAO dropDao = new EtsDropDownDAO();
		ArrayList probStateList = dropDao.getProbStateList(issTypeModel);

		if (probStateList.contains("Submit") || probStateList.contains("In Process") || probStateList.contains("Assigned") || probStateList.contains("Modified") || probStateList.contains("Open") || probStateList.contains("Rejected") || probStateList.contains("Resolved")) {

			errsb.append("The selected issue type is in active state. An issue type can be deactivated only if all the issues having this particular issue type are in 'Withdrawn' or 'Closed' state. Please select different issue type.");
			errsb.append("<br />");
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
	public EtsIssTypeInfoModel getContDelIssTypeDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr1DelIssTypeModel();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateScrn1FormFields(issTypeParams);

		//print error msg
		Global.println("err msg getContDelIssTypeDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			issTypeSessnModel.setPrevIssueTypeList(issTypeParams.getPrevIssueTypeList()); //add comments
			issTypeSessnModel.setErrMsg(errMsg);
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(DELISSUETYPE1STPAGE);

		} else {

			//add the comments
			//when there are no err msgs

			ArrayList prevDelIssTypeList = issTypeParams.getPrevIssueTypeList();

			issTypeSessnModel.setPrevIssueTypeList(prevDelIssTypeList); //add comments

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, DELISSUETYPEUNIQID);

			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(0);

		} //end of no err msg

		return issTypeSessnModel;

	}

	/**
						 * This method will load step1 details into session and get the step 2 details
						 * 
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */
	public EtsIssTypeInfoModel getSubmitDelIssTypeDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(0); //make cancel state

		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		//if drop model addedd successfully
		if (dropDao.deleteIssueType(issTypeSessnModel)) {

			issTypeSessnModel.setNextActionState(0);

		} else {

			issTypeSessnModel.setNextActionState(ERRINACTION);
		}

		return issTypeSessnModel;

	}

	/**
			 * To get the Issue from sessn
			 */

	public EtsIssTypeInfoModel getIssTypeInfoFromSessn() {

		EtsIssTypeInfoModel issTypeModel = getIssueTypeSessnParams().getSessnIssTypeInfoModel(DELISSUETYPEUNIQID);

		return issTypeModel;

	}

	/**
				  * 
				  * @return
				  */

	public EtsIssTypeInfoModel getCancDelIssTypeDetails() {

		//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(0); //always to state of modify main page==5
		issTypeSessnModel.setNextActionState(MAINPAGE);

		return issTypeSessnModel;

	}

	/**
					  * 
					  * @return
					  */

	public EtsIssTypeInfoModel getEditDelIssTypeDetails() {

		//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(0); //always to state of modify main page==5
		issTypeSessnModel.setNextActionState(0);

		return issTypeSessnModel;

	}

} //end of class
