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

import java.lang.reflect.Array;
import java.util.ArrayList;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssTypeInfoModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssTypeParseParams implements EtsIssueActionConstants {

	public static final String VERSION = "1.10";
	private EtsIssObjectKey etsIssObjKey;

	/**
	 * 
	 */
	public EtsIssTypeParseParams(EtsIssObjectKey etsIssObjKey) {
		super();
		this.etsIssObjKey = etsIssObjKey;

	}

	/**
			 * This method is only for comments
			 * 
			 */

	public EtsIssTypeInfoModel loadScr1AddIssTypeModel() {

		EtsIssTypeInfoModel issTypeModel = new EtsIssTypeInfoModel();

		String issuetypename = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issuetypename"));

		String issueaccess = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issueaccess"));

		//issue source
		String issuesource = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issuesource"));

		String projectId = AmtCommonUtils.getTrimStr(etsIssObjKey.getProj().getProjectId());
		String issueClass = AmtCommonUtils.getTrimStr(etsIssObjKey.getIssueClass());

		Global.println("issue source===" + issuesource);

		issTypeModel.setIssueType(issuetypename);
		issTypeModel.setIssueAccess(issueaccess);
		issTypeModel.setIssueSource(issuesource);
		issTypeModel.setProjectId(projectId);
		issTypeModel.setIssueClass(issueClass);

		return issTypeModel;

	}

	/**
	 * 
	 * @return
	 */
	public EtsIssTypeInfoModel loadScr2AddIssTypeModel() {

		EtsIssTypeInfoModel issTypeModel = new EtsIssTypeInfoModel();

		String issueowner = "";
		String issuebackupowner = "";
		
		if(etsIssObjKey.getParams().get("issueowner") != null)
			issueowner = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issueowner"));
		
		if(etsIssObjKey.getParams().get("issuebackupowner") != null)
			issuebackupowner = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issuebackupowner"));
		
		String ownshipinternal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("ownshipinternal"));
		//String backupownshipinternal = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("backupownshipinternal"));
		
		Global.println("issue owner===" + issueowner);
		Global.println("issue ownership internal only ===" + ownshipinternal);

		Global.println("issue backup owner===" + issuebackupowner);
		//Global.println("issue backup ownership internal only ===" + backupownshipinternal);

		
		ArrayList prevOwnerList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(issueowner)) {

			prevOwnerList.add(issueowner);
		}
						
		ArrayList prevBackupOwnerList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(issuebackupowner)) {

			prevBackupOwnerList.add(issuebackupowner);
		}		

		if(AmtCommonUtils.isResourceDefined(issueowner) )		
			issTypeModel.setPrevOwnerList(prevOwnerList);
		
		if(AmtCommonUtils.isResourceDefined(ownshipinternal) ) {
			issTypeModel.setOwnerShipInternal(ownshipinternal);
			issTypeModel.setBackupOwnershipInternal(ownshipinternal);
		}
		
		if(AmtCommonUtils.isResourceDefined(issuebackupowner) )
			issTypeModel.setPrevBackupOwnerList(prevBackupOwnerList);
				
		
		return issTypeModel;
	}

	/**
	 * This method is only for comments
	 * 
	 */

	public EtsIssTypeInfoModel loadScr1DelIssTypeModel() {

		EtsIssTypeInfoModel issTypeModel = new EtsIssTypeInfoModel();
		ArrayList prevIssueTypeList = new ArrayList();

		String issuetype = AmtCommonUtils.getTrimStr( ((String) etsIssObjKey.getParams().get("issuetype")).replaceAll("\"","&quot;") );
		String projectId = AmtCommonUtils.getTrimStr(etsIssObjKey.getProj().getProjectId());
		String issueClass = AmtCommonUtils.getTrimStr(etsIssObjKey.getIssueClass());

		Global.println("issue type===" + issuetype);

		if (AmtCommonUtils.isResourceDefined(issuetype)) {

			prevIssueTypeList.add(issuetype);
		}

		issTypeModel.setPrevIssueTypeList(prevIssueTypeList);
		issTypeModel.setIssueType(issuetype);
		issTypeModel.setProjectId(projectId);
		issTypeModel.setIssueClass(issueClass);

		return issTypeModel;

	}

	/**
				 * This method is only for comments
				 * 
				 */

	public EtsIssTypeInfoModel loadScr1UpdIssTypeModel() {

		EtsIssTypeInfoModel issTypeModel = new EtsIssTypeInfoModel();
		ArrayList prevIssueTypeList = new ArrayList();

		String issuetype = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("issuetype"));

		Global.println("issue type===" + issuetype);

		if (AmtCommonUtils.isResourceDefined(issuetype)) {

			prevIssueTypeList.add(issuetype);
		}

		issTypeModel.setPrevIssueTypeList(prevIssueTypeList);

		return issTypeModel;

	}

	/**
				 * This method is only for comments
				 * 
				 */

	public EtsIssTypeInfoModel loadScr1SubsIssTypeModel() {

		EtsIssTypeInfoModel issTypeModel = new EtsIssTypeInfoModel();
		
		ArrayList prevSubsIssTypeList = new ArrayList();
		ArrayList prevUnSubsIssTypeList = new ArrayList();
		
		//////////////
		String subIssTypeArry[]=etsIssObjKey.getRequest().getParameterValues("subsissuetype");
		String unSubIssTypeArry[]=etsIssObjKey.getRequest().getParameterValues("unsubsissuetype");
		
		prevSubsIssTypeList=AmtCommonUtils.getArrayListFromArray(subIssTypeArry);
		prevUnSubsIssTypeList=AmtCommonUtils.getArrayListFromArray(unSubIssTypeArry);
		
		issTypeModel.setPrevSubsAddIssTypList(prevSubsIssTypeList);
		issTypeModel.setPrevSubsDelIssTypList(prevUnSubsIssTypeList);

		return issTypeModel;

	}

} //end of class
