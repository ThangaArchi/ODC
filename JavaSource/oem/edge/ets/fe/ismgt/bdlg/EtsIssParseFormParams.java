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
import java.sql.*;
import java.util.*;
import java.io.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.ets.fe.ismgt.helpers.*;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssParseFormParams implements EtsIssueConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.41";
	private EtsIssObjectKey etsIssObjKey;

	/**
	 * Constructor for EtsIssParseFormParams
	 */
	public EtsIssParseFormParams(EtsIssObjectKey etsIssObjKey) {
		super();
		this.etsIssObjKey = etsIssObjKey;

	}

	/**
	 * This method will parse the form params and load the EtsIssUserInfo Object model
	 * 
	 */

	public EtsIssProbInfoUsr1Model loadScr1ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		////params from screen1//
		String severity = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("severity"));
		String title = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("title"));
		String description = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("description"));
		String problem_type = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("problem_type"));

		//load severity
		ArrayList prevProbSevList = new ArrayList();
		prevProbSevList.add(severity);
		///////////////////////
		///load prob type
		ArrayList prevProbTypeList = new ArrayList();
		prevProbTypeList.add(problem_type);
		///////////////////
		uniqUsr1Model.setPrevProbSevList(prevProbSevList); //add severity
		uniqUsr1Model.setProbTitle(title); //add title
		uniqUsr1Model.setProbDesc(description); //add descr
		uniqUsr1Model.setPrevProbTypeList(prevProbTypeList); //add prob type

		return uniqUsr1Model;

	}

	/**
		 * This method will parse the form params and load the EtsIssUserInfo Object model
		 * 
		 */

	public EtsIssProbInfoUsr1Model loadNewScr1ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		////params from screen1//
		String severity = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("severity"));
		String title = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("title"));
		String description = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("description"));

		//load severity
		ArrayList prevProbSevList = new ArrayList();
		prevProbSevList.add(severity);
		///////////////////////

		///////////////////

		uniqUsr1Model.setPrevProbSevList(prevProbSevList); //add severity
		uniqUsr1Model.setProbTitle(title); //add title
		uniqUsr1Model.setProbDesc(description); //add descr

		return uniqUsr1Model;

	}

	/**
		 * This method will parse the form params and load the EtsIssUserInfo Object model
		 * 
		 */

	public EtsIssProbInfoUsr1Model loadScr11ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		String problem_type = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("problem_type"));

		///load prob type
		ArrayList prevProbTypeList = new ArrayList();
		prevProbTypeList.add(problem_type);
		///////////////////

		uniqUsr1Model.setPrevProbTypeList(prevProbTypeList); //add prob type

		return uniqUsr1Model;

	}

	/**
		 * This method will parse the form params and load the EtsIssUserInfo Object model
		 * 
		 */

	public EtsIssProbInfoUsr1Model loadScr2ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		//	to check if they are defined atleast
		String defSubTypeAVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDSUBTYPE_A));

		//params from screen2//
		String subTypeAVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_A));

		//
		ArrayList prevSubTypeAList = new ArrayList();
		///
		prevSubTypeAList.add(subTypeAVal);

		///////////

		if (AmtCommonUtils.isResourceDefined(defSubTypeAVal)) {

			uniqUsr1Model.setSubTypeADefnd(defSubTypeAVal);
			uniqUsr1Model.setPrevSubTypeAList(prevSubTypeAList);

		}

		return uniqUsr1Model;

	}

	/**
			 * This method will parse the form params and load the EtsIssUserInfo Object model
			 * 
			 */

	public EtsIssProbInfoUsr1Model loadScr3ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		//to check if they are defined atleast
		String defSubTypeBVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDSUBTYPE_B));

		//params from screen2//
		String subTypeBVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_B));

		//
		ArrayList prevSubTypeBList = new ArrayList();
		prevSubTypeBList.add(subTypeBVal);

		if (AmtCommonUtils.isResourceDefined(defSubTypeBVal)) {

			uniqUsr1Model.setSubTypeBDefnd(defSubTypeBVal);
			uniqUsr1Model.setPrevSubTypeBList(prevSubTypeBList);

		}

		return uniqUsr1Model;

	}

	/**
				 * This method will parse the form params and load the EtsIssUserInfo Object model
				 * 
				 */

	public EtsIssProbInfoUsr1Model loadScr4ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		//	to check if they are defined atleast
		String defSubTypeCVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDSUBTYPE_C));

		//params from screen2//
		String subTypeCVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_C));

		//
		ArrayList prevSubTypeCList = new ArrayList();
		prevSubTypeCList.add(subTypeCVal);

		if (AmtCommonUtils.isResourceDefined(defSubTypeCVal)) {

			uniqUsr1Model.setSubTypeCDefnd(defSubTypeCVal);
			uniqUsr1Model.setPrevSubTypeCList(prevSubTypeCList);

		}

		return uniqUsr1Model;

	}

	/**
					 * This method will parse the form params and load the EtsIssUserInfo Object model
					 * 
					 */

	public EtsIssProbInfoUsr1Model loadScr5ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		//sub type a

		//to check if they are defined atleast
		String defSubTypeAVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDSUBTYPE_A));

		//params from screen2//
		String subTypeAVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_A));

		//
		ArrayList prevSubTypeAList = new ArrayList();
		///
		prevSubTypeAList.add(subTypeAVal);

		///////////

		if (AmtCommonUtils.isResourceDefined(defSubTypeAVal)) {

			uniqUsr1Model.setSubTypeADefnd(defSubTypeAVal);
			uniqUsr1Model.setPrevSubTypeAList(prevSubTypeAList);

		}

		//sub type b//
		//		to check if they are defined atleast
		String defSubTypeBVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDSUBTYPE_B));

		//params from screen2//
		String subTypeBVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_B));

		//
		ArrayList prevSubTypeBList = new ArrayList();
		prevSubTypeBList.add(subTypeBVal);

		if (AmtCommonUtils.isResourceDefined(defSubTypeBVal)) {

			uniqUsr1Model.setSubTypeBDefnd(defSubTypeBVal);
			uniqUsr1Model.setPrevSubTypeBList(prevSubTypeBList);

		}

		//sub tyep c//

		//		to check if they are defined atleast
		String defSubTypeCVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDSUBTYPE_C));

		//params from screen2//
		String subTypeCVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_C));

		//
		ArrayList prevSubTypeCList = new ArrayList();
		prevSubTypeCList.add(subTypeCVal);

		if (AmtCommonUtils.isResourceDefined(defSubTypeCVal)) {

			uniqUsr1Model.setSubTypeCDefnd(defSubTypeCVal);
			uniqUsr1Model.setPrevSubTypeCList(prevSubTypeCList);

		}

		//to check if they are defined atleast

		String defSubTypeDVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDSUBTYPE_D));

		//params from screen2//

		String subTypeDVal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDSUBTYPE_D));

		//

		ArrayList prevSubTypeDList = new ArrayList();
		prevSubTypeDList.add(subTypeDVal);

		if (AmtCommonUtils.isResourceDefined(defSubTypeDVal)) {

			uniqUsr1Model.setSubTypeDDefnd(defSubTypeDVal);
			uniqUsr1Model.setPrevSubTypeDList(prevSubTypeDList);

		}

		return loadStaticParamsIntoUsr1Model(uniqUsr1Model);

	}

	/**
	 * To validate static fields if decalred if any
	 */

	public EtsIssProbInfoUsr1Model loadStaticParamsIntoUsr1Model(EtsIssProbInfoUsr1Model uniqUsr1Model) {

		///to check if they are defined atleast
		String defFieldC1 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDFIELDC1NAME));
		String defFieldC2 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDFIELDC2NAME));
		String defFieldC3 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDFIELDC3NAME));
		String defFieldC4 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDFIELDC4NAME));
		String defFieldC5 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDFIELDC5NAME));
		String defFieldC6 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDFIELDC6NAME));
		String defFieldC7 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("def" + STDFIELDC7NAME));
		String deftestcase = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("deftestcase"));

		//	   params from screen 2 fields//
		String fieldC1Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC1NAME));
		String fieldC2Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC2NAME));
		String fieldC3Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC3NAME));
		String fieldC4Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC4NAME));
		String fieldC5Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC5NAME));
		String fieldC6Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC6NAME));
		String fieldC7Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get(STDFIELDC7NAME));
		String testcase = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("testcase"));

		ArrayList prevFieldC1ValList = new ArrayList();
		ArrayList prevFieldC2ValList = new ArrayList();
		ArrayList prevFieldC3ValList = new ArrayList();
		ArrayList prevFieldC4ValList = new ArrayList();
		ArrayList prevFieldC5ValList = new ArrayList();
		ArrayList prevFieldC6ValList = new ArrayList();
		ArrayList prevFieldC7ValList = new ArrayList();

		//add field c1
		prevFieldC1ValList.add(fieldC1Val);
		prevFieldC2ValList.add(fieldC2Val);
		prevFieldC3ValList.add(fieldC3Val);
		prevFieldC4ValList.add(fieldC4Val);
		prevFieldC5ValList.add(fieldC5Val);
		prevFieldC6ValList.add(fieldC6Val);
		prevFieldC7ValList.add(fieldC7Val);

		if (AmtCommonUtils.isResourceDefined(defFieldC1)) {

			uniqUsr1Model.setFieldC1Defnd(defFieldC1);
			uniqUsr1Model.setPrevFieldC1List(prevFieldC1ValList);
		}

		if (AmtCommonUtils.isResourceDefined(defFieldC2)) {

			uniqUsr1Model.setFieldC2Defnd(defFieldC2);
			uniqUsr1Model.setPrevFieldC2List(prevFieldC2ValList);
		}

		if (AmtCommonUtils.isResourceDefined(defFieldC3)) {

			uniqUsr1Model.setFieldC3Defnd(defFieldC3);
			uniqUsr1Model.setPrevFieldC3List(prevFieldC3ValList);
		}

		if (AmtCommonUtils.isResourceDefined(defFieldC4)) {

			uniqUsr1Model.setFieldC4Defnd(defFieldC4);
			uniqUsr1Model.setPrevFieldC4List(prevFieldC4ValList);
		}

		if (AmtCommonUtils.isResourceDefined(defFieldC5)) {

			uniqUsr1Model.setFieldC5Defnd(defFieldC5);
			uniqUsr1Model.setPrevFieldC5List(prevFieldC5ValList);
		}

		if (AmtCommonUtils.isResourceDefined(defFieldC6)) {

			uniqUsr1Model.setFieldC6Defnd(defFieldC6);
			uniqUsr1Model.setPrevFieldC6List(prevFieldC6ValList);
		}
		if (AmtCommonUtils.isResourceDefined(defFieldC7)) {

			uniqUsr1Model.setFieldC7Defnd(defFieldC7);
			uniqUsr1Model.setPrevFieldC7List(prevFieldC7ValList);
		}

		if (AmtCommonUtils.isResourceDefined(deftestcase)) {

			uniqUsr1Model.setTestCaseDefnd(deftestcase);
			uniqUsr1Model.setTestCase(testcase);

		}

		return uniqUsr1Model;
	}

	/**
						 * This method will parse the form params and load the EtsIssUserInfo Object model
						 * 
						 */

	public EtsIssProbInfoUsr1Model loadScr6ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		String chkissibmonly = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("chkissibmonly"));

		Global.println("chkissibmonly in loadScr6 Params===" + chkissibmonly);

		if (AmtCommonUtils.isResourceDefined(chkissibmonly)) {

			uniqUsr1Model.setChkIssTypeIbmOnly(chkissibmonly);
		}

		return uniqUsr1Model;

	}

	/**
	  * This method will parse the form params and load the EtsIssUserInfo Object model
	  * 
	  */

	public EtsIssProbInfoUsr1Model loadScr7ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		ArrayList prevNotifyList = new ArrayList();

		String notifyArry[] = etsIssObjKey.getRequest().getParameterValues("notifylist");

		if (notifyArry != null) {

			prevNotifyList = AmtCommonUtils.getArrayListFromArray(notifyArry);
		}

		uniqUsr1Model.setPrevNotifyList(prevNotifyList);
		return uniqUsr1Model;

	}

	/**
	 * This method is only for comments
	 * 
	 */

	public EtsIssProbInfoUsr1Model loadCommentsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		String comments = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("comments"));

		uniqUsr1Model.setCommFromCust(comments);
		return uniqUsr1Model;

	}

	/**
		 * This method is only for comments
		 * 
		 */

	public EtsCrProbInfoModel loadCommentsIntoCrInfoModel() {

		EtsCrProbInfoModel crInfoModel = new EtsCrProbInfoModel();

		String comments = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("comments"));

		crInfoModel.setCommFromCust(comments);

		return crInfoModel;

	}

	/**
		 * This method is only for comments
		 * 
		 */

	public EtsIssProbInfoUsr1Model loadScr1CreateNewIssTypeIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		String issuetypename = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issuetypename"));

		String issueaccess = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issueaccess"));

		uniqUsr1Model.setIssueType(issuetypename);
		uniqUsr1Model.setIssueAccess(issueaccess);

		return uniqUsr1Model;

	}

	/**
			 * This method is only for comments
			 * 
			 */

	public EtsIssProbInfoUsr1Model loadScr2CreateNewIssTypeIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		String issueowner = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issueowner"));

		ArrayList prevNotifyList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(issueowner)) {

			prevNotifyList.add(issueowner);
		}

		uniqUsr1Model.setPrevNotifyList(prevNotifyList);

		return uniqUsr1Model;

	}

	/**
		 * This method will parse the form params and load the EtsIssUserInfo Object model
		 * 
		 */

	public EtsCrProbInfoModel loadNewScr1ParamsIntoCrModel() {

		EtsCrProbInfoModel uniqCrModel = new EtsCrProbInfoModel();

		////params from screen1//
		String severity = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("severity"));
		String title = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("title"));
		String description = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("description"));

		//load severity
		ArrayList prevProbSevList = new ArrayList();
		prevProbSevList.add(severity);
		///////////////////////

		uniqCrModel.setPrevProbSevList(prevProbSevList); //add severity
		uniqCrModel.setProbTitle(title); //add title
		uniqCrModel.setProbDesc(description); //add descr

		return uniqCrModel;

	}

	/**
				 * This method is only for comments
				 * 
				 */

	public EtsChgOwnerInfoModel loadScr1ChgOwnerDetails() {

		EtsChgOwnerInfoModel ownerInfoModel = new EtsChgOwnerInfoModel();

		String issueowner = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issueowner"));

		ArrayList prevOwnerIdList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(issueowner)) {

			prevOwnerIdList.add(issueowner);
		}

		ownerInfoModel.setPrevOwnerIdList(prevOwnerIdList);

		return ownerInfoModel;

	}

	/**
			 * This method will parse the form params and load the EtsIssUserInfo Object model
			 * 
			 */

	public EtsIssProbInfoUsr1Model loadScr00ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		String extUser = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("subbhfextuser"));

		///load prob type
		ArrayList prevExtUserList = new ArrayList();
		prevExtUserList.add(extUser);
		///////////////////

		uniqUsr1Model.setPrevExtUserList(prevExtUserList); //add prob type

		return uniqUsr1Model;

	}

	///611 st

	/**
		 * This method will parse the form params and load the EtsIssUserInfo Object model
		 * 
		 */

	public EtsIssProbInfoUsr1Model loadFastScr1ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		////params from screen1//
		String severity = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("severity"));
		String title = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("title"));
		String description = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("description"));
		String problem_type = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("problem_type"));

		//load severity
		ArrayList prevProbSevList = new ArrayList();
		prevProbSevList.add(severity);
		///////////////////////

		///load prob type
		ArrayList prevProbTypeList = new ArrayList();
		prevProbTypeList.add(problem_type);
		///////////////////

		uniqUsr1Model.setPrevProbSevList(prevProbSevList); //add severity
		uniqUsr1Model.setProbTitle(title); //add title
		uniqUsr1Model.setProbDesc(description); //add descr
		uniqUsr1Model.setPrevProbTypeList(prevProbTypeList); //add prob type

		return uniqUsr1Model;

	}

	/**
			 * This method will parse the form params and load the EtsIssUserInfo Object model
			 * 
			 */

	public EtsIssProbInfoUsr1Model loadFastScr2ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		String chkissibmonly = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("chkissibmonly"));

		Global.println("chkissibmonly in loadScr6 Params===" + chkissibmonly);

		if (AmtCommonUtils.isResourceDefined(chkissibmonly)) {

			uniqUsr1Model.setChkIssTypeIbmOnly(chkissibmonly);
		}

		return uniqUsr1Model;

	}

	/**
	 * This method will parse the form params and load the EtsIssUserInfo Object model
	 * 
	 */
	public EtsIssProbInfoUsr1Model loadFastScr3ParamsIntoUsr1Model() {

		EtsIssProbInfoUsr1Model uniqUsr1Model = new EtsIssProbInfoUsr1Model();

		String chkissibmonly = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("chkissibmonly"));

		Global.println("chkissibmonly in loadScr6 Params===" + chkissibmonly);

		if (AmtCommonUtils.isResourceDefined(chkissibmonly)) {

			uniqUsr1Model.setChkIssTypeIbmOnly(chkissibmonly);
		}

		
		if(etsIssObjKey.getParams().get("custFieldIds") != null ) {
		  String custIds = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("custFieldIds") );
		  
		  ArrayList arrLstCustIds = AmtCommonUtils.getArrayListFromStringTok(custIds, ",");
		  
		  String val = "";
		  ArrayList arrLst = null;
		  for(int i=0; i< arrLstCustIds.size(); i++) {
			  String fid = arrLstCustIds.get(i).toString().trim();
			  
			  String txtName = "custFieldVal"+ fid;
			  
			  if(etsIssObjKey.getParams().get(txtName) != null ) {
			  	if(AmtCommonUtils.isResourceDefined( etsIssObjKey.getParams().get(txtName).toString() ) ){
			  		val = etsIssObjKey.getParams().get(txtName).toString();
			  		
				  	arrLst = new ArrayList();
				  	arrLst.add(val);
				  	switch(Integer.parseInt(fid)) {
				  	
					  	case 1 :
					  		uniqUsr1Model.setPrevFieldC1List(arrLst);	
					  	break;
		
					  	case 2 :
					  		uniqUsr1Model.setPrevFieldC2List(arrLst);	
					  	break;
					  	
					  	case 3 :
					  		uniqUsr1Model.setPrevFieldC3List(arrLst);	
					  	break;
					  	
					  	case 4 :
					  		uniqUsr1Model.setPrevFieldC4List(arrLst);	
					  	break;
					  	
					  	case 5 :
					  		uniqUsr1Model.setPrevFieldC5List(arrLst);	
					  	break;
					  	
					  	case 6 :
					  		uniqUsr1Model.setPrevFieldC6List(arrLst);	
					  	break;
					  	
					  	case 7 :
					  		uniqUsr1Model.setPrevFieldC7List(arrLst);	
					  	break;
					  	
					  	case 8 :
					  		uniqUsr1Model.setPrevFieldC8List(arrLst);	
					  	break;			  	
					  	
				  	}		  		
			  	 }
			  }		  	
		   }		  
		}
		AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("chkissibmonly"));
		
		
		ArrayList prevNotifyList = new ArrayList();

		String notifyArry[] = etsIssObjKey.getRequest().getParameterValues("notifylist");

		if (notifyArry != null) {

			prevNotifyList = AmtCommonUtils.getArrayListFromArray(notifyArry);
		}

		uniqUsr1Model.setPrevNotifyList(prevNotifyList);

		return uniqUsr1Model;

	}

	
///EtsIssProbInfoUsr1Model usrSessnModel
	public EtsIssProbInfoUsr1Model loadModifyEditCustFieldsIntoUsr1Model() {
		EtsIssProbInfoUsr1Model usrSessnModel = new EtsIssProbInfoUsr1Model();		
			
		ArrayList prevFieldCxList = null;
		
		if(etsIssObjKey.getParams().get("FieldC1Val")!= null) {
			prevFieldCxList = new ArrayList();
			String FieldC1Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("FieldC1Val"));
			prevFieldCxList.add(0,FieldC1Val);
			usrSessnModel.setPrevFieldC1List(prevFieldCxList);			
		}
		
		if(etsIssObjKey.getParams().get("FieldC2Val")!= null) {
			prevFieldCxList = new ArrayList();
			String FieldC2Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("FieldC2Val"));
			prevFieldCxList.add(0,FieldC2Val);
			usrSessnModel.setPrevFieldC2List(prevFieldCxList);			
		}
		
		if(etsIssObjKey.getParams().get("FieldC3Val")!= null) {
			prevFieldCxList = new ArrayList();
			String FieldC3Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("FieldC3Val"));
			prevFieldCxList.add(0,FieldC3Val);
			usrSessnModel.setPrevFieldC3List(prevFieldCxList);			
		}
		
		if(etsIssObjKey.getParams().get("FieldC4Val")!= null) {
			prevFieldCxList = new ArrayList();
			String FieldC4Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("FieldC4Val"));
			prevFieldCxList.add(0,FieldC4Val);
			usrSessnModel.setPrevFieldC4List(prevFieldCxList);			
		}
		
		if(etsIssObjKey.getParams().get("FieldC5Val")!= null) {
			prevFieldCxList = new ArrayList();
			String FieldC5Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("FieldC5Val"));
			prevFieldCxList.add(0,FieldC5Val);
			usrSessnModel.setPrevFieldC5List(prevFieldCxList);			
		}
		
		if(etsIssObjKey.getParams().get("FieldC6Val")!= null) {
			prevFieldCxList = new ArrayList();
			String FieldC6Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("FieldC6Val"));
			prevFieldCxList.add(0,FieldC6Val);
			usrSessnModel.setPrevFieldC6List(prevFieldCxList);			
		}
		
		if(etsIssObjKey.getParams().get("FieldC7Val")!= null) {
			prevFieldCxList = new ArrayList();
			String FieldC7Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("FieldC7Val"));
			prevFieldCxList.add(0,FieldC7Val);
			usrSessnModel.setPrevFieldC7List(prevFieldCxList);			
		}

		if(etsIssObjKey.getParams().get("FieldC8Val")!= null) {
			prevFieldCxList = new ArrayList();
			String FieldC8Val = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("FieldC8Val"));
			prevFieldCxList.add(0,FieldC8Val);
			usrSessnModel.setPrevFieldC8List(prevFieldCxList);			
		}
				
		return usrSessnModel;
	}
	
	//611 end

} //end of class
