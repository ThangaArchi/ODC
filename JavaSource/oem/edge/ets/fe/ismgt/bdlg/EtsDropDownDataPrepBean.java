package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.*;
import java.util.*;
import oem.edge.amt.*;
import oem.edge.common.*;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.ets.fe.ismgt.dao.*;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssueActionSessnParams;
import oem.edge.ets.fe.ismgt.actions.*;
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

/**
 * @author v2phani
 * This class prepares the data required for submission form data
 */
public class EtsDropDownDataPrepBean implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.11";
	private EtsIssObjectKey etsIssObjKey ;

	/**
	 * Constructor for EtsDropDownDataPrepBean.
	 */
	public EtsDropDownDataPrepBean(EtsIssObjectKey etsIssObjKey) {
		super();
		this.etsIssObjKey = etsIssObjKey;

	}

	/**
	 * to get Form Label Data use session/DB combination
	 */

	public HashMap getFormLabelData() throws SQLException, Exception {

		EtsIssueActionSessnParams actionSessnParams = new EtsIssueActionSessnParams(etsIssObjKey);

		EtsDropDownDataBean dropModel = new EtsDropDownDataBean();
		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		HashMap labelMap = new HashMap();

		///get params for static data//
		dropModel.setProjectId(etsIssObjKey.getProj().getProjectId());
		dropModel.setIssueClass(etsIssObjKey.getIssueClass());

		//set in session, if not defined//

		if (!actionSessnParams.isFormLabelDataDefnd()) {

			actionSessnParams.setFormLabelData(dropDao.getFormLabelData(dropModel));
		}

		//get from session

		labelMap = actionSessnParams.getFormLabelData();

		//check once again if it is defined from session properly

		if (!EtsIssFilterUtils.isHashMapDefnd(labelMap)) {

			labelMap = dropDao.getFormLabelData(dropModel);
		}

		return labelMap;
	}

	/**
	 * 
	 * this method prepares the static data for drop downs
	 */

	public EtsIssStaticSubmFormDetails prepareStaticDropDownDets(boolean firstTime) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		EtsIssStaticSubmFormDetails subFormDet = new EtsIssStaticSubmFormDetails();

		//set field names and lists//
		setStaticFieldCNames(subFormDet, firstTime);

		return subFormDet;
	}

	/**
	 * 
	 * this method prepares the dynamic data for drop downs
	 */

	public EtsIssDynSubmFormDetails prepareDynDropDownDets(String issueType, boolean firstTime) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		EtsIssDynSubmFormDetails subDynFormDet = new EtsIssDynSubmFormDetails();

		//set dyn field names and lists//
		setDynamicFieldCNames(subDynFormDet, issueType, firstTime);

		return subDynFormDet;
	}

	/**
	 * set field names for static data
	 */
	private void setStaticFieldCNames(EtsIssStaticSubmFormDetails subFormDet, boolean firstTime) throws SQLException, Exception {

		//get field map
		HashMap labelMap = getFormLabelData();
		/////
		String prevFieldC1Val = "";
		String prevFieldC2Val = "";
		String prevFieldC3Val = "";
		String prevFieldC4Val = "";
		String prevFieldC5Val = "";
		String prevFieldC6Val = "";
		String prevFieldC7Val = "";

		////
		if (firstTime) {

			ETSIssue currentIssue = etsIssObjKey.getCurrentIssue();

			prevFieldC1Val = currentIssue.field_C1;
			prevFieldC2Val = currentIssue.field_C2;
			prevFieldC3Val = currentIssue.field_C3;
			prevFieldC4Val = currentIssue.field_C4;
			prevFieldC5Val = currentIssue.field_C5;
			prevFieldC6Val = currentIssue.field_C6;
			prevFieldC7Val = currentIssue.field_C7;

		} else {
			//prev vals always user reference name
			prevFieldC1Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC1NAME));
			prevFieldC2Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC2NAME));
			prevFieldC3Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC3NAME));
			prevFieldC4Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC4NAME));
			prevFieldC5Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC5NAME));
			prevFieldC6Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC6NAME));
			prevFieldC7Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC7NAME));

		}

		//prev lists
		ArrayList prevFieldC1ValList = new ArrayList();
		ArrayList prevFieldC2ValList = new ArrayList();
		ArrayList prevFieldC3ValList = new ArrayList();
		ArrayList prevFieldC4ValList = new ArrayList();
		ArrayList prevFieldC5ValList = new ArrayList();
		ArrayList prevFieldC6ValList = new ArrayList();
		ArrayList prevFieldC7ValList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(prevFieldC1Val)) {

			prevFieldC1ValList.add(prevFieldC1Val);

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC2Val)) {

			prevFieldC2ValList.add(prevFieldC2Val);

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC3Val)) {

			prevFieldC3ValList.add(prevFieldC3Val);

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC4Val)) {

			prevFieldC4ValList.add(prevFieldC4Val);

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC5Val)) {

			prevFieldC5ValList.add(prevFieldC5Val);

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC6Val)) {

			prevFieldC6ValList.add(prevFieldC6Val);

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC7Val)) {

			prevFieldC7ValList.add(prevFieldC7Val);

		}

		//set name as class
		subFormDet.setFieldC1RefName(STDFIELDC1NAME);
		subFormDet.setFieldC1DispName((String) labelMap.get(STDFIELDC1NAME));
		subFormDet.setFieldC1ValList(getStaticFieldCNameValLists(STDFIELDC1NAME));
		subFormDet.setPrevFieldC1ValList(prevFieldC1ValList);

		subFormDet.setFieldC2RefName(STDFIELDC2NAME);
		subFormDet.setFieldC2DispName((String) labelMap.get(STDFIELDC2NAME));
		subFormDet.setFieldC2ValList(getStaticFieldCNameValLists(STDFIELDC2NAME));
		subFormDet.setPrevFieldC2ValList(prevFieldC2ValList);

		subFormDet.setFieldC3RefName(STDFIELDC3NAME);
		subFormDet.setFieldC3DispName((String) labelMap.get(STDFIELDC3NAME));
		subFormDet.setFieldC3ValList(getStaticFieldCNameValLists(STDFIELDC3NAME));
		subFormDet.setPrevFieldC3ValList(prevFieldC3ValList);

		subFormDet.setFieldC4RefName(STDFIELDC4NAME);
		subFormDet.setFieldC4DispName((String) labelMap.get(STDFIELDC4NAME));
		subFormDet.setFieldC4ValList(getStaticFieldCNameValLists(STDFIELDC4NAME));
		subFormDet.setPrevFieldC4ValList(prevFieldC4ValList);

		subFormDet.setFieldC5RefName(STDFIELDC5NAME);
		subFormDet.setFieldC5DispName((String) labelMap.get(STDFIELDC5NAME));
		//subFormDet.setFieldC5ValList(getStaticFieldCNameValLists(STDFIELDC5NAME));
		subFormDet.setFieldC5ValList(getDefaultStaticValList());
		subFormDet.setPrevFieldC5ValList(prevFieldC5ValList);

		subFormDet.setFieldC6RefName(STDFIELDC6NAME);
		subFormDet.setFieldC6DispName((String) labelMap.get(STDFIELDC6NAME));
		//subFormDet.setFieldC6ValList(getStaticFieldCNameValLists(STDFIELDC6NAME));
		subFormDet.setFieldC6ValList(getDefaultStaticValList());
		subFormDet.setPrevFieldC6ValList(prevFieldC6ValList);

		subFormDet.setFieldC7RefName(STDFIELDC7NAME);
		subFormDet.setFieldC7DispName((String) labelMap.get(STDFIELDC7NAME));
		//subFormDet.setFieldC7ValList(getStaticFieldCNameValLists(STDFIELDC7NAME));
		subFormDet.setFieldC7ValList(getDefaultStaticValList());
		subFormDet.setPrevFieldC7ValList(prevFieldC7ValList);

	}

	/**
	 * set field names for dynamic data
	 */
	private void setDynamicFieldCNames(EtsIssDynSubmFormDetails subDynFormDet, String issueType, boolean firstTime) throws SQLException, Exception {

		//get field map
		HashMap labelMap = getFormLabelData();

		//prev vals always user reference name
		String prevSubTypeAVal = "";
		String prevSubTypeBVal = "";
		String prevSubTypeCVal = "";
		String prevSubTypeDVal = "";

		if (firstTime) {

			ETSIssue currentIssue = etsIssObjKey.getCurrentIssue();

			prevSubTypeAVal = currentIssue.subTypeA;
			prevSubTypeBVal = currentIssue.subTypeB;
			prevSubTypeCVal = currentIssue.subTypeC;
			prevSubTypeDVal = currentIssue.subTypeD;

		} else {

			//prev vals always user reference name
			prevSubTypeAVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_A));
			prevSubTypeBVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_B));
			prevSubTypeCVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_C));
			prevSubTypeDVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_D));

		}

		//prev lists
		ArrayList prevSubTypeAValList = new ArrayList();
		ArrayList prevSubTypeBValList = new ArrayList();
		ArrayList prevSubTypeCValList = new ArrayList();
		ArrayList prevSubTypeDValList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(prevSubTypeAVal)) {

			prevSubTypeAValList.add(prevSubTypeAVal);

		}

		if (AmtCommonUtils.isResourceDefined(prevSubTypeBVal)) {

			prevSubTypeBValList.add(prevSubTypeBVal);

		}

		if (AmtCommonUtils.isResourceDefined(prevSubTypeCVal)) {

			prevSubTypeCValList.add(prevSubTypeCVal);

		}

		if (AmtCommonUtils.isResourceDefined(prevSubTypeDVal)) {

			prevSubTypeDValList.add(prevSubTypeDVal);

		}

		//set name as class
		subDynFormDet.setSubTypeARefName(STDSUBTYPE_A);
		subDynFormDet.setSubTypeADispName((String) labelMap.get(STDSUBTYPE_A));
		//set field name lists
		subDynFormDet.setSubTypeAValList(getDynamicFieldCNameValLists(issueType, STDSUBTYPE_A, firstTime));
		subDynFormDet.setPrevSubTypeAValList(prevSubTypeAValList);

		subDynFormDet.setSubTypeBRefName(STDSUBTYPE_B);
		subDynFormDet.setSubTypeBDispName((String) labelMap.get(STDSUBTYPE_B));
		subDynFormDet.setSubTypeBValList(getDynamicFieldCNameValLists(issueType, STDSUBTYPE_B, firstTime));
		subDynFormDet.setPrevSubTypeBValList(prevSubTypeBValList);

		subDynFormDet.setSubTypeCRefName(STDSUBTYPE_C);
		subDynFormDet.setSubTypeCDispName((String) labelMap.get(STDSUBTYPE_C));
		subDynFormDet.setSubTypeCValList(getDynamicFieldCNameValLists(issueType, STDSUBTYPE_C, firstTime));
		subDynFormDet.setPrevSubTypeCValList(prevSubTypeCValList);

		subDynFormDet.setSubTypeDRefName(STDSUBTYPE_D);
		subDynFormDet.setSubTypeDDispName((String) labelMap.get(STDSUBTYPE_D));
		subDynFormDet.setSubTypeDValList(getDynamicFieldCNameValLists(issueType, STDSUBTYPE_D, firstTime));
		subDynFormDet.setPrevSubTypeDValList(prevSubTypeDValList);

	}

	/***
	 * get static field name lists 
	 * 
	 */

	private ArrayList getStaticFieldCNameValLists(String fieldCName) throws SQLException, Exception {

		EtsDropDownDataBean dropModel = new EtsDropDownDataBean();

		///get params for static data//
		dropModel.setProjectId(etsIssObjKey.getProj().getProjectId());
		dropModel.setIssueClass(etsIssObjKey.getIssueClass());
		dropModel.setIssueType("");
		dropModel.setSubTypeA("");
		dropModel.setSubTypeB("");
		dropModel.setSubTypeC("");
		dropModel.setSubTypeD("");
		dropModel.setFieldName(fieldCName);
		dropModel.setIssueAccess(etsIssObjKey.getIssueAccess());
		dropModel.setActiveFlag("Y");

		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		return dropDao.getStaticDropDownVals(dropModel);

	}

	/***
	 * get dynamic field name lists 
	 * 
	 */

	private ArrayList getDynamicFieldCNameValLists(String issueType, String subType, boolean firstTime) throws SQLException, Exception {

		ArrayList dropValList = new ArrayList();

		String prevSubTypeAVal = "";
		String prevSubTypeBVal = "";
		String prevSubTypeCVal = "";
		String prevSubTypeDVal = "";

		if (firstTime) {

			ETSIssue currentIssue = etsIssObjKey.getCurrentIssue();

			prevSubTypeAVal = currentIssue.subTypeA;
			prevSubTypeBVal = currentIssue.subTypeB;
			prevSubTypeCVal = currentIssue.subTypeC;
			prevSubTypeDVal = currentIssue.subTypeD;

		} else {

			//prev vals always user reference name
			prevSubTypeAVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_A));
			prevSubTypeBVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_B));
			prevSubTypeCVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_C));
			prevSubTypeDVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_D));

		}

		//get go actions//
		String gosubtypea = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("go" + STDSUBTYPE_A + ".x"));
		String gosubtypeb = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("go" + STDSUBTYPE_B + ".x"));
		String gosubtypec = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("go" + STDSUBTYPE_C + ".x"));
		String gosubtyped = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("go" + STDSUBTYPE_D + ".x"));

		//

		//edit subtype actions
		String editsubtypea = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("edit" + STDSUBTYPE_A + ".x"));
		String editsubtypeb = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("edit" + STDSUBTYPE_B + ".x"));
		String editsubtypec = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("edit" + STDSUBTYPE_C + ".x"));
		String editsubtyped = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("edit" + STDSUBTYPE_D + ".x"));

		EtsDropDownDataBean dropModel = new EtsDropDownDataBean();

		///get params for static data//
		dropModel.setProjectId(etsIssObjKey.getProj().getProjectId());
		dropModel.setIssueClass(etsIssObjKey.getIssueClass());
		dropModel.setIssueType(issueType);
		dropModel.setSubTypeA(prevSubTypeAVal);
		dropModel.setSubTypeB(prevSubTypeBVal);
		dropModel.setSubTypeC(prevSubTypeCVal);
		dropModel.setSubTypeD(prevSubTypeDVal);
		dropModel.setIssueAccess(etsIssObjKey.getIssueAccess());
		dropModel.setActiveFlag("Y");

		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		if (subType.equals(STDSUBTYPE_A)) {

			dropValList = dropDao.getDynamicSubTypeAVals(dropModel);

		}

		if (subType.equals(STDSUBTYPE_B)) {

			if (AmtCommonUtils.isResourceDefined(editsubtypea)) {

				dropModel.setSubTypeA("");

			}

			dropValList = dropDao.getDynamicSubTypeBVals(dropModel);

		}

		if (subType.equals(STDSUBTYPE_C)) {

			if (AmtCommonUtils.isResourceDefined(gosubtypea) || AmtCommonUtils.isResourceDefined(editsubtypea) || AmtCommonUtils.isResourceDefined(editsubtypeb)) {

				dropModel.setSubTypeA("");
				dropModel.setSubTypeB("");

			}

			dropValList = dropDao.getDynamicSubTypeCVals(dropModel);

		}

		if (subType.equals(STDSUBTYPE_D)) {

			if (AmtCommonUtils.isResourceDefined(gosubtypea) || AmtCommonUtils.isResourceDefined(gosubtypeb) || AmtCommonUtils.isResourceDefined(editsubtypea) || AmtCommonUtils.isResourceDefined(editsubtypeb) || AmtCommonUtils.isResourceDefined(editsubtypec)) {

				dropModel.setSubTypeA("");
				dropModel.setSubTypeB("");
				dropModel.setSubTypeC("");

			}

			dropValList = dropDao.getDynamicSubTypeDVals(dropModel);

		}

		return dropValList;

	}

	/**
	 * To get Yes/No values for the 3 std fields in static system, irrespective of project
	 */

	private ArrayList getDefaultStaticValList() {

		ArrayList defStaticValList = new ArrayList();

		for (int i = 0; i < 2; i++) {

			EtsDropDownDataBean dropBean = new EtsDropDownDataBean();

			if (i == 0) {
				dropBean.setFieldValue("Yes");
			} else {

				dropBean.setFieldValue("No");
			}

			defStaticValList.add(dropBean);

		}

		return defStaticValList;

	}
	
		 

} //end of class

