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
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.CommonInfoDAO;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssueTypeGuiUtils;
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
public class EtsIssAddIssueTypeBdlg extends EtsIssActionDataPrepAbsBean implements EtsIssueActionConstants, EtsIssueConstants {

	public static final String VERSION = "1.11";

	private EtsIssTypeParseParams parseParams;
	private int currentstate = 0;

	/**
	 * 
	 */
	public EtsIssAddIssueTypeBdlg(EtsIssObjectKey etsIssObjKey, int currentstate) {
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

		///set the vals into session
		setIssueTypeInfoIntoSessn(issTypeModel, ADDISSUETYPEUNIQID);

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
	public ArrayList getOwnerListDetails(String issueAccess) throws SQLException, Exception {

		//		get proj member DAO and get details
		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		ArrayList projMemList = projDao.getProjMemberListWithUserTypeWthoutVisitors(getEtsIssObjKey().getProj().getProjectId(), getEtsIssObjKey().isProjBladeType());
		ArrayList userTypeList = new ArrayList();

		int projsize = 0;
		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			projsize = projMemList.size();

			if (!getEtsIssObjKey().isProjBladeType()) {

				if (!issueAccess.equals("Internal")) {

					for (int i = 0; i < projsize; i = i + 4) {

						etsUserEdgeId = (String) projMemList.get(i);
						etsUserNameWithIrId = (String) projMemList.get(i + 1);
						etsUserType = (String) projMemList.get(i + 2);

						if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I") && etsUserType.equals("I")) {

							etsUserNameWithIrId = etsUserNameWithIrId + " *";

						} //if user type is IBM

						userTypeList.add(etsUserEdgeId);
						userTypeList.add(etsUserNameWithIrId);

					} //end of for

				} // end of for iss type=IBM ONLY !=Y

				else {

					for (int i = 0; i < projsize; i = i + 4) {

						etsUserEdgeId = (String) projMemList.get(i);
						etsUserNameWithIrId = (String) projMemList.get(i + 1);
						etsUserType = (String) projMemList.get(i + 2);

						if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I") && etsUserType.equals("I")) {

							userTypeList.add(etsUserEdgeId);
							userTypeList.add(etsUserNameWithIrId);

						} //if user type is IBM

					} //end of for

				} //end of check iss ibm type=Y

			} else { //blade project

				for (int i = 0; i < projsize; i = i + 4) {

					etsUserEdgeId = (String) projMemList.get(i);
					etsUserNameWithIrId = (String) projMemList.get(i + 1);
					etsUserType = (String) projMemList.get(i + 2);

					if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I") && etsUserType.equals("I")) {

						userTypeList.add(etsUserEdgeId);
						userTypeList.add(etsUserNameWithIrId);

					} //if user type is IBM

				} //end of for
			}

		} //if projMemelist is defined

		return userTypeList;
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

		if (!AmtCommonUtils.isResourceDefined(issTypeModel.getIssueType())) {

			errsb.append("Please provide issue type name.");
			errsb.append("<br />");

		} else {

			String tempIssueType = AmtCommonUtils.getTrimStr(issTypeModel.getIssueType());

			if (tempIssueType.length() > 100) {

				errsb.append("Please provide maximum of 100 characters for issue type name.");
				errsb.append("<br />");

			} else {

				EtsDropDownDAO dropDao = new EtsDropDownDAO();

				if (dropDao.isIssueTypeExistsForProj(issTypeModel)) {

					errsb.append("An issue type, with the given name already exists in the workspace. Please provide different");
					errsb.append(" name for issue type.");
					errsb.append("<br />");

				} //check for 
			}
		}

		//cehck for issue access

		if (!AmtCommonUtils.isResourceDefined(issTypeModel.getIssueAccess())) {

			errsb.append("Please provide Security classification.");
			errsb.append("<br />");

		}

		//get from session
		return errsb.toString();

	}

	/**
	 * This method will load step1 details and check for validations
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String validateScrn2FormFields(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();
		String ownerEdgeId = "";
		String backupOwnerEdgeId = "";
		String ownshipinternal = "";
		String backupownshipinternal = "";
		String decafUserType = "";

		//cehck for issue type name

		//			check for issue owner

		if (EtsIssFilterUtils.isArrayListDefnd(issTypeModel.getPrevOwnerList())) {

			if (issTypeModel.getPrevOwnerList().contains("NONE")) {

				errsb.append("Please select issue owner.");
				errsb.append("<br />");

			} else { //if it contains other than NONE

				ownerEdgeId = AmtCommonUtils.getTrimStr((String) issTypeModel.getPrevOwnerList().get(0));

				ownshipinternal = AmtCommonUtils.getTrimStr(issTypeModel.getOwnerShipInternal());

				if (ownshipinternal.equals("Y")) {

					EtsProjMemberDAO projDao = new EtsProjMemberDAO();
					decafUserType = projDao.getDecafUserType(ownerEdgeId);

					if (!decafUserType.equals("I")) {
						errsb.append("The owner cannot be external type, when the 'Restrict ownership to IBM team members only' is clicked. Please select internal team member as owner for this issue type.");
						errsb.append("<br />");
					}
				} //if ownership
				
				if (EtsIssFilterUtils.isArrayListDefnd(issTypeModel.getPrevBackupOwnerList())) {	
					backupOwnerEdgeId = AmtCommonUtils.getTrimStr((String) issTypeModel.getPrevBackupOwnerList().get(0));
					if(ownerEdgeId.equals(backupOwnerEdgeId)) {
						errsb.append("The issue type owner and issue type backup owner cannot be same. Please select different member as backup owner for this issue type.");
						errsb.append("<br />");						
					}
					backupownshipinternal = AmtCommonUtils.getTrimStr(issTypeModel.getBackupOwnershipInternal());
					if (backupownshipinternal.equals("Y")) {
						EtsProjMemberDAO projDao = new EtsProjMemberDAO();
						decafUserType = projDao.getDecafUserType(backupOwnerEdgeId);
						if (!decafUserType.equals("I")) {
							errsb.append("The backup owner cannot be external type, when the 'Restrict ownership to IBM team members only' is clicked. Please select internal team member as backup owner for this issue type.");
							errsb.append("<br />");
						}

					} //if backupownershipinternal 
					
				}				
				

			} // other than none

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
	public EtsIssTypeInfoModel getContAddIssTypeDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr1AddIssTypeModel();

		String issueAccess = AmtCommonUtils.getTrimStr(issTypeParams.getIssueAccess());

		EtsIssueTypeGuiUtils guiUtils = new EtsIssueTypeGuiUtils();
		String guiIssueAccess = guiUtils.getSecurityClassification(issueAccess);

		//check for any error msgs from the form model for scrn1
		String errMsg = validateScrn1FormFields(issTypeParams);

		//print error msg
		Global.println("err msg getContAddIssTypeDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			issTypeSessnModel.setIssueType(issTypeParams.getIssueType()); //add comments
			issTypeSessnModel.setIssueAccess(issTypeParams.getIssueAccess()); //add comments
			issTypeSessnModel.setIssueSource(issTypeParams.getIssueSource()); //add comments
			issTypeSessnModel.setErrMsg(errMsg);
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(ADDISSUETYPE1STPAGE);

		} else {

			//add the comments
			//when there are no err msgs

			issTypeSessnModel.setIssueType(issTypeParams.getIssueType()); //add comments
			issTypeSessnModel.setIssueAccess(issTypeParams.getIssueAccess()); //add comments
			issTypeSessnModel.setGuiIssueAccess(guiIssueAccess); //add comments
			issTypeSessnModel.setIssueSource(issTypeParams.getIssueSource()); //add comments
			issTypeSessnModel.setOwnerShipInternal("");

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, ADDISSUETYPEUNIQID);

			//			set owner list details
			issTypeSessnModel.setOwnerList(getOwnerListDetails(issTypeParams.getIssueAccess()));			
			issTypeSessnModel.setPrevOwnerList(issTypeSessnModel.getPrevOwnerList());
			
			// set backup owner list details
			issTypeSessnModel.setBackupOwnerList(getOwnerListDetails(issTypeParams.getIssueAccess()));						
			issTypeSessnModel.setPrevBackupOwnerList(issTypeSessnModel.getPrevBackupOwnerList());
			
			
			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state

			if (issueAccess.equals("External") && !getEtsIssObjKey().isProjBladeType()) {

				issTypeSessnModel.setNextActionState(ADDISSUETYPEEXTCONTINUE);

			} else {

				issTypeSessnModel.setNextActionState(0);

			}

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
	public EtsIssTypeInfoModel getSubmitAddIssTypeDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(0); //make cancel state

		EtsDropDownDAO dropDao = new EtsDropDownDAO();
		EtsDropDownDataBean dropModel = deriveDropDownData(issTypeSessnModel);

		//if drop model addedd successfully
		if (dropDao.addIssueType(dropModel)) {

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

		EtsIssTypeInfoModel issTypeModel = getIssueTypeSessnParams().getSessnIssTypeInfoModel(ADDISSUETYPEUNIQID);

		return issTypeModel;

	}

	/**
	 * 
	 * @param issTypeSessnModel
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	private EtsDropDownDataBean deriveDropDownData(EtsIssTypeInfoModel issTypeSessnModel) throws SQLException, Exception {

		EtsDropDownDataBean dropModel = new EtsDropDownDataBean();

		String dataId = EtsIssFilterUtils.getUniqRefNoStr("DT");

		String projectId = AmtCommonUtils.getTrimStr(issTypeSessnModel.getProjectId());
		String projectName = AmtCommonUtils.getTrimStr(issTypeSessnModel.getProjectName());
		String issueClass = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueClass());
		String issueType = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueType());
		String subTypeA = AmtCommonUtils.getTrimStr(issTypeSessnModel.getSubTypeA());
		String subTypeB = AmtCommonUtils.getTrimStr(issTypeSessnModel.getSubTypeB());
		String subTypeC = AmtCommonUtils.getTrimStr(issTypeSessnModel.getSubTypeC());
		String subTypeD = AmtCommonUtils.getTrimStr(issTypeSessnModel.getSubTypeD());
		String issueSource = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueSource());
		String issueAccess = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueAccess());
		String activeFlag = AmtCommonUtils.getTrimStr(issTypeSessnModel.getActiveFlag());
		String issueEtsR1 = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueEtsR1());
		String issueEtsR2 = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueEtsR2());
		String lastUserId = AmtCommonUtils.getTrimStr(issTypeSessnModel.getLastUserId());
		String ownShipIntOnly = AmtCommonUtils.getTrimStr(issTypeSessnModel.getOwnerShipInternal());
		
		ArrayList prevOwnerList = issTypeSessnModel.getPrevOwnerList();

		EtsIssOwnerInfo ownerInfoObj = new EtsIssOwnerInfo();
		String ownerEdgeId = "";
		String ownerEmail = "";

		EtsProjMemberDAO projDao = new EtsProjMemberDAO();
		ArrayList ownerInfoList = projDao.getUserIdInfoList(prevOwnerList);

		if (ownerInfoList != null && !ownerInfoList.isEmpty()) {

			ownerInfoObj = (EtsIssOwnerInfo) ownerInfoList.get(0);

			//get the vals

			ownerEdgeId = ownerInfoObj.getUserEdgeId();
			ownerEmail = ownerInfoObj.getUserEmail();
		}
		
		ArrayList prevBackupOwnerList = issTypeSessnModel.getPrevBackupOwnerList();

		EtsIssOwnerInfo backupOwnerInfoObj = null;
		String backupOwnerEdgeId = "";
		String backupOwnerEmail = "";

		EtsProjMemberDAO projDao2 = new EtsProjMemberDAO();
		ArrayList backupOwnerInfoList = projDao2.getUserIdInfoList(prevBackupOwnerList);

		if (backupOwnerInfoList != null && !backupOwnerInfoList.isEmpty()) {
			
			backupOwnerInfoObj = (EtsIssOwnerInfo) backupOwnerInfoList.get(0);
			//get the vals
			backupOwnerEdgeId = backupOwnerInfoObj.getUserEdgeId();
			backupOwnerEmail = backupOwnerInfoObj.getUserEmail();

		}
		
		//update to DB value of issue access, based on interal/external rules
		EtsIssueTypeGuiUtils issGuiUtils = new EtsIssueTypeGuiUtils();

		String updIssueAccess = issGuiUtils.getIssueAccessMatrix(issueAccess, ownShipIntOnly, getEtsIssObjKey().isProjBladeType());

		//set the drop down params
		dropModel.setDataId(dataId);
		dropModel.setProjectId(projectId);
		dropModel.setProjectName(projectName);
		dropModel.setIssueClass(issueClass);
		dropModel.setIssueType(ETSUtils.escapeString(issueType));
		dropModel.setSubTypeA(subTypeA);
		dropModel.setSubTypeB(subTypeB);
		dropModel.setSubTypeC(subTypeC);
		dropModel.setSubTypeD(subTypeD);
		dropModel.setIssueSource(issueSource);
		dropModel.setIssueAccess(updIssueAccess);
		dropModel.setIssueEtsR1(issueEtsR1);
		dropModel.setIssueEtsR2(issueEtsR2);
		dropModel.setActiveFlag(activeFlag);
		dropModel.setLastUserId(lastUserId);
		dropModel.setOwnerInfo(ownerInfoObj);
        if(backupOwnerInfoObj != null)
        	dropModel.setBackupOwnerInfo(backupOwnerInfoObj);
        
		return dropModel;

	}

	/**
				  * 
				  * @return
				  */

	public EtsIssTypeInfoModel getCancAddIssTypeDetails() {

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

	public EtsIssTypeInfoModel getEditAddIssTypeDetails() {

		//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr2AddIssTypeModel();
		
		if (EtsIssFilterUtils.isArrayListDefnd(issTypeParams.getPrevOwnerList() ) ) 		
			issTypeSessnModel.setPrevOwnerList(issTypeParams.getPrevOwnerList());
		if(EtsIssFilterUtils.isArrayListDefnd(issTypeParams.getPrevBackupOwnerList() ) )
			issTypeSessnModel.setPrevBackupOwnerList( issTypeParams.getPrevBackupOwnerList() );
		
		//upload the updated object into session//
		setIssueTypeInfoIntoSessn(issTypeSessnModel, ADDISSUETYPEUNIQID);

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(0); //always to state of modify main page==5
		issTypeSessnModel.setNextActionState(0);

		return issTypeSessnModel;

	}

	/**
	 * This method will load step1 details into session and get the step 2 details
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public EtsIssTypeInfoModel getContOwnerShipDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr2AddIssTypeModel();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateScrn2FormFields(issTypeParams);

		//print error msg
		Global.println("err msg getSubmitReqDetails ()=====" + errMsg);
		
		String ownerShipInt = AmtCommonUtils.getTrimStr(issTypeParams.getOwnerShipInternal());
		String backupOwnerShipInt = AmtCommonUtils.getTrimStr(issTypeParams.getBackupOwnershipInternal());
		//on err msg, the actions
		if (AmtCommonUtils.isResourceDefined(errMsg)) {
			issTypeSessnModel.setPrevOwnerList(issTypeParams.getPrevOwnerList());
			issTypeSessnModel.setPrevBackupOwnerList(issTypeParams.getPrevBackupOwnerList());
			issTypeSessnModel.setOwnerShipInternal(ownerShipInt);
			issTypeSessnModel.setBackupOwnershipInternal(backupOwnerShipInt);
			issTypeSessnModel.setErrMsg(errMsg);
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(ADDISSUETYPEEXTCONTINUE);

		} else {

			//add the comments
			//when there are no err msgs
			issTypeSessnModel.setOwnerShipInternal(ownerShipInt);
			issTypeSessnModel.setBackupOwnershipInternal(backupOwnerShipInt);
					
			
			//new 
			//add the comments
			//when there are no err msgs

			String issueSource = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueSource());

			//rule >> if issue source is defined and equals to ETSPMOSOURCE, then PMO SOURCE
			//else equals to ETSOLD

			if (AmtCommonUtils.isResourceDefined(issueSource)) {

				if (issueSource.equals(ETSPMOSOURCE)) {

					issueSource = ETSPMOSOURCE;
				}
			} else {

				issueSource = STDETSOLD;
			}

			String prevOwnerStr = "";
			ArrayList prevOwnerList = issTypeParams.getPrevOwnerList();

			if (EtsIssFilterUtils.isArrayListDefnd(prevOwnerList)) {

				prevOwnerStr = (String) prevOwnerList.get(0);
			}

			
			String prevBackupOwnerStr = "";
			ArrayList prevBackupOwnerList = issTypeParams.getPrevBackupOwnerList();

			if (EtsIssFilterUtils.isArrayListDefnd(prevBackupOwnerList)) {

				prevBackupOwnerStr = (String) prevBackupOwnerList.get(0);
			}
						
			
			
			//get owner profile
			CommonInfoDAO comDao = new CommonInfoDAO();
			issTypeSessnModel.setOwnerProfile(comDao.getUserDetailsInfo(prevOwnerStr));
			issTypeSessnModel.setBackupOwnerProfile(comDao.getUserDetailsInfo(prevBackupOwnerStr));
			issTypeSessnModel.setIssueSource(issueSource);
			issTypeSessnModel.setPrevOwnerList(prevOwnerList);
			issTypeSessnModel.setPrevBackupOwnerList(prevBackupOwnerList);
			issTypeSessnModel.setActiveFlag("Y");

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, ADDISSUETYPEUNIQID);

			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(0);

			
			///

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
	public EtsIssTypeInfoModel getContOwnerDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr2AddIssTypeModel();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateScrn2FormFields(issTypeParams);

		String issueAccess = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueAccess());

		//print error msg
		Global.println("err msg getSubmitReqDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			issTypeSessnModel.setPrevOwnerList(issTypeParams.getPrevOwnerList());
			issTypeSessnModel.setPrevBackupOwnerList(issTypeParams.getPrevBackupOwnerList());
			issTypeSessnModel.setErrMsg(errMsg);
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state

			if (issueAccess.equals("External") && !getEtsIssObjKey().isProjBladeType()) {

				issTypeSessnModel.setNextActionState(ADDISSTYPEOWNSHIPCONT);

			} else {

				issTypeSessnModel.setNextActionState(ADDISSUETYPECONTINUE);

			}

		} else {

			//add the comments
			//when there are no err msgs

			String issueSource = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueSource());

			//rule >> if issue source is defined and equals to ETSPMOSOURCE, then PMO SOURCE
			//else equals to ETSOLD

			if (AmtCommonUtils.isResourceDefined(issueSource)) {

				if (issueSource.equals(ETSPMOSOURCE)) {

					issueSource = ETSPMOSOURCE;
				}
			} else {

				issueSource = STDETSOLD;
			}

			String prevOwnerStr = "";
			ArrayList prevOwnerList = issTypeParams.getPrevOwnerList();

			if (EtsIssFilterUtils.isArrayListDefnd(prevOwnerList)) {

				prevOwnerStr = (String) prevOwnerList.get(0);
			}

			String prevBackupOwnerStr = "";
			ArrayList prevBackupOwnerList = issTypeParams.getPrevBackupOwnerList();

			if (EtsIssFilterUtils.isArrayListDefnd(prevBackupOwnerList)) {

				prevBackupOwnerStr = (String) prevBackupOwnerList.get(0);
			}
			
			
			//get owner profile
			CommonInfoDAO comDao = new CommonInfoDAO();
			issTypeSessnModel.setOwnerProfile(comDao.getUserDetailsInfo(prevOwnerStr));
			
			issTypeSessnModel.setBackupOwnerProfile(comDao.getUserDetailsInfo(prevBackupOwnerStr));
			issTypeSessnModel.setIssueSource(issueSource);
			issTypeSessnModel.setPrevOwnerList(prevOwnerList);
			issTypeSessnModel.setPrevBackupOwnerList(prevBackupOwnerList);
			issTypeSessnModel.setActiveFlag("Y");

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, ADDISSUETYPEUNIQID);

			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(0);

		} //end of no err msg

		return issTypeSessnModel;

	}

	/**
						  * 
						  * @return
						  */

	public EtsIssTypeInfoModel getEditOwnerDetails() {

		//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		String issueAccess = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueAccess());

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(0); //always to state of modify main page==5

		if (issueAccess.equals("External") && !getEtsIssObjKey().isProjBladeType()) {

			issTypeSessnModel.setNextActionState(ADDISSUETYPEEXTCONTINUE);

		} else {

			issTypeSessnModel.setNextActionState(ADDISSUETYPECONTINUE);

		}

		return issTypeSessnModel;

	}

} //end of class
