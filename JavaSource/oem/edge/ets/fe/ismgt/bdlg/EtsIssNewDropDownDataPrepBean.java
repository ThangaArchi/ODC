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

import java.util.*;
import java.sql.*;

import oem.edge.common.*;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.ets.fe.ismgt.helpers.*;
import oem.edge.ets.fe.ismgt.dao.*;
import oem.edge.amt.*;
import oem.edge.ets.fe.ETSDBUtils;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssNewDropDownDataPrepBean implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.11";
	private EtsIssObjectKey etsIssObjKey;
	private EtsIssCommonGuiUtils comGuiUtils;

	/**
	 * 
	 */
	public EtsIssNewDropDownDataPrepBean(EtsIssObjectKey etsIssObjKey) {
		super();
		this.etsIssObjKey = etsIssObjKey;
		this.comGuiUtils=new EtsIssCommonGuiUtils();
	}

	/**
		 * 
		 * this method prepares the static data for drop downs
		 */

	public EtsIssStaticSubmFormDetails prepareStaticDropDownDets(EtsIssProbInfoUsr1Model identModel) throws SQLException, Exception {

		
		EtsIssStaticSubmFormDetails subFormDet = getStaticFieldLists(identModel);

		return subFormDet;
	}

	/**
		 * set field names for static data
		 */
	public  EtsIssStaticSubmFormDetails getStaticFieldLists(EtsIssProbInfoUsr1Model identModel) throws SQLException, Exception {

		//
		EtsIssStaticSubmFormDetails subFormDet = new EtsIssStaticSubmFormDetails();

		//get field map
		HashMap labelMap = getFormLabelData();
		/////

		//prev lists
		ArrayList prevFieldC1ValList = identModel.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = identModel.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = identModel.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = identModel.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = identModel.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = identModel.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = identModel.getPrevFieldC7List();

		//set name as class
		subFormDet.setFieldC1RefName(STDFIELDC1NAME);
		subFormDet.setFieldC1DispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDFIELDC1NAME, DEFUALTSTDFIELDC1NAME));
		subFormDet.setFieldC1ValList(getStaticFieldCNameValLists(STDFIELDC1NAME));
		subFormDet.setPrevFieldC1ValList(prevFieldC1ValList);

		subFormDet.setFieldC2RefName(STDFIELDC2NAME);
		subFormDet.setFieldC2DispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDFIELDC2NAME, DEFUALTSTDFIELDC2NAME));
		subFormDet.setFieldC2ValList(getStaticFieldCNameValLists(STDFIELDC2NAME));
		subFormDet.setPrevFieldC2ValList(prevFieldC2ValList);

		subFormDet.setFieldC3RefName(STDFIELDC3NAME);
		subFormDet.setFieldC3DispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDFIELDC3NAME, DEFUALTSTDFIELDC3NAME));
		subFormDet.setFieldC3ValList(getStaticFieldCNameValLists(STDFIELDC3NAME));
		subFormDet.setPrevFieldC3ValList(prevFieldC3ValList);

		subFormDet.setFieldC4RefName(STDFIELDC4NAME);
		subFormDet.setFieldC4DispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDFIELDC4NAME, DEFUALTSTDFIELDC4NAME));
		subFormDet.setFieldC4ValList(getStaticFieldCNameValLists(STDFIELDC4NAME));
		subFormDet.setPrevFieldC4ValList(prevFieldC4ValList);

		subFormDet.setFieldC5RefName(STDFIELDC5NAME);
		subFormDet.setFieldC5DispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDFIELDC5NAME, DEFUALTSTDFIELDC5NAME));
		//subFormDet.setFieldC5ValList(getStaticFieldCNameValLists(STDFIELDC5NAME));
		subFormDet.setFieldC5ValList(getDefaultStaticValList());
		subFormDet.setPrevFieldC5ValList(prevFieldC5ValList);

		subFormDet.setFieldC6RefName(STDFIELDC6NAME);
		subFormDet.setFieldC6DispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDFIELDC6NAME, DEFUALTSTDFIELDC6NAME));
		//subFormDet.setFieldC6ValList(getStaticFieldCNameValLists(STDFIELDC6NAME));
		subFormDet.setFieldC6ValList(getDefaultStaticValList());
		subFormDet.setPrevFieldC6ValList(prevFieldC6ValList);

		subFormDet.setFieldC7RefName(STDFIELDC7NAME);
		subFormDet.setFieldC7DispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDFIELDC7NAME, DEFUALTSTDFIELDC7NAME));
		//subFormDet.setFieldC7ValList(getStaticFieldCNameValLists(STDFIELDC7NAME));
		subFormDet.setFieldC7ValList(getDefaultStaticValList());
		subFormDet.setPrevFieldC7ValList(prevFieldC7ValList);

		return subFormDet;
	}

	/***
		 * get dynamic field name lists 
		 * 
		 */

	private ArrayList getDynSubTypeList(EtsIssProbInfoUsr1Model identModel, String subType) throws SQLException, Exception {

		ArrayList dropValList = new ArrayList();

		String probType="";
		String issueType = "";
		String prevSubTypeAVal = "";
		String prevSubTypeBVal = "";
		String prevSubTypeCVal = "";
		String prevSubTypeDVal = "";

		ArrayList prevIssueTypeList = identModel.getPrevProbTypeList();
		ArrayList prevSubTypeAValList = identModel.getPrevSubTypeAList();
		ArrayList prevSubTypeBValList = identModel.getPrevSubTypeBList();
		ArrayList prevSubTypeCValList = identModel.getPrevSubTypeCList();
		ArrayList prevSubTypeDValList = identModel.getPrevSubTypeDList();

		//			prev vals always user reference name
		if (EtsIssFilterUtils.isArrayListDefnd(prevIssueTypeList)) {

			probType = AmtCommonUtils.getTrimStr((String) prevIssueTypeList.get(0));
			
			//parse the $ sep string
					
			
			issueType=EtsIssFilterUtils.getDelimitIssueVal(probType);

		}

		//prev vals always user reference name
		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList)) {

			prevSubTypeAVal = AmtCommonUtils.getTrimStr((String) prevSubTypeAValList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBValList)) {

			prevSubTypeBVal = AmtCommonUtils.getTrimStr((String) prevSubTypeBValList.get(0));

		}
		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCValList)) {
			prevSubTypeCVal = AmtCommonUtils.getTrimStr((String) prevSubTypeCValList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeDValList)) {
			prevSubTypeDVal = AmtCommonUtils.getTrimStr((String) prevSubTypeDValList.get(0));

		}

		//		

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

			dropValList = dropDao.getDynamicSubTypeBVals(dropModel);

		}

		if (subType.equals(STDSUBTYPE_C)) {

			dropValList = dropDao.getDynamicSubTypeCVals(dropModel);

		}

		if (subType.equals(STDSUBTYPE_D)) {

			dropValList = dropDao.getDynamicSubTypeDVals(dropModel);

		}

		return dropValList;

	}

	/**
		 * set field names for dynamic data
		 */
	public EtsIssDynFieldDataModel getDynamicFieldDataModel(EtsIssProbInfoUsr1Model identModel, String subType) throws SQLException, Exception {

		//get field map
		HashMap labelMap = getFormLabelData();

		EtsIssDynFieldDataModel dynFieldModel = new EtsIssDynFieldDataModel();

		String issueType = "";
		//prev vals always user reference name
		String prevSubTypeAVal = "";
		String prevSubTypeBVal = "";
		String prevSubTypeCVal = "";
		String prevSubTypeDVal = "";

		ArrayList prevIssueTypeList = identModel.getPrevProbTypeList();
		//prev lists
		ArrayList prevSubTypeAValList = identModel.getPrevSubTypeAList();
		ArrayList prevSubTypeBValList = identModel.getPrevSubTypeBList();
		ArrayList prevSubTypeCValList = identModel.getPrevSubTypeCList();
		ArrayList prevSubTypeDValList = identModel.getPrevSubTypeDList();

		
		if (subType.equals(STDSUBTYPE_A)) {
			//set name as class
			dynFieldModel.setFieldRefName(STDSUBTYPE_A);
			dynFieldModel.setFieldDispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDSUBTYPE_A, DEFUALTSTDSUBTYPE_A));
			//set field name lists
			dynFieldModel.setFieldValList(getDynSubTypeList(identModel, STDSUBTYPE_A));
			dynFieldModel.setPrevFieldValList(prevSubTypeAValList);
		}

		if (subType.equals(STDSUBTYPE_B)) {

			dynFieldModel.setFieldRefName(STDSUBTYPE_B);
			dynFieldModel.setFieldDispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDSUBTYPE_B, DEFUALTSTDSUBTYPE_B));
			dynFieldModel.setFieldValList(getDynSubTypeList(identModel, STDSUBTYPE_B));
			dynFieldModel.setPrevFieldValList(prevSubTypeBValList);

		}

		if (subType.equals(STDSUBTYPE_C)) {

			dynFieldModel.setFieldRefName(STDSUBTYPE_C);
			dynFieldModel.setFieldDispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDSUBTYPE_C, DEFUALTSTDSUBTYPE_C));
			dynFieldModel.setFieldValList(getDynSubTypeList(identModel, STDSUBTYPE_C));
			dynFieldModel.setPrevFieldValList(prevSubTypeCValList);

		}

		if (subType.equals(STDSUBTYPE_D)) {

			dynFieldModel.setFieldRefName(STDSUBTYPE_D);
			dynFieldModel.setFieldDispName(comGuiUtils.getDefaultSubTypeStr(labelMap, STDSUBTYPE_D, DEFUALTSTDSUBTYPE_D));
			dynFieldModel.setFieldValList(getDynSubTypeList(identModel, STDSUBTYPE_D));
			dynFieldModel.setPrevFieldValList(prevSubTypeDValList);

		}

		return dynFieldModel;

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
