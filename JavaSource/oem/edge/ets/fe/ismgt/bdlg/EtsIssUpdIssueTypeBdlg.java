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
public class EtsIssUpdIssueTypeBdlg extends EtsIssActionDataPrepAbsBean implements EtsIssueActionConstants, EtsIssueConstants {

	public static final String VERSION = "1.7";

	private EtsIssTypeParseParams parseParams;
	private int currentstate = 0;

	/**
	 * 
	 */
	public EtsIssUpdIssueTypeBdlg(EtsIssObjectKey etsIssObjKey, int currentstate) {

		super(etsIssObjKey);
		this.parseParams = new EtsIssTypeParseParams(etsIssObjKey);
		this.currentstate = currentstate;

		// TODO Auto-generated constructor stub
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
		issTypeModel.setIssueTypeList(getUpdIssTypeListDetails(issTypeModel));

		///set the vals into session
		setIssueTypeInfoIntoSessn(issTypeModel, UPDISSUETYPEUNIQID);

		issTypeModel.setErrMsg("");
		issTypeModel.setCurrentActionState(currentstate);
		issTypeModel.setCancelActionState(0);
		issTypeModel.setNextActionState(0);

		return issTypeModel;

		///

	}

	/**
			  * 
								 * @return
								 * @throws SQLException
								 * @throws Exception
								 */

	public ArrayList getUpdIssTypeListDetails(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		//		get proj member DAO and get details
		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		ArrayList issTypeList = dropDao.getUpdateIssueTypesList(issTypeModel);
		ArrayList delList = new ArrayList();

		int size = 0;
		String dataId = "";
		String issType = "";

		if (EtsIssFilterUtils.isArrayListDefndWithObj(issTypeList)) {

			size = issTypeList.size();

			for (int i = 0; i < size; i++) {

				EtsDropDownDataBean dropBean = (EtsDropDownDataBean) issTypeList.get(i);

				dataId = AmtCommonUtils.getTrimStr(dropBean.getDataId());
				issType = AmtCommonUtils.getTrimStr(dropBean.getIssueType());

				//
				delList.add(dataId);
				delList.add(issType);

			} //end of for

		} //if projMemelist is defined

		return delList;
	}

	/**
								 * This method will load step1 details into session and get the step 2 details
								 * 
								 * @return
								 * @throws SQLException
								 * @throws Exception
								 */
	public EtsIssTypeInfoModel getContPg1Details() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr1UpdIssTypeModel();

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
			issTypeSessnModel.setNextActionState(UPDISSUETYPE1STPAGE);

		} else {

			ArrayList prevDelIssTypeList = issTypeParams.getPrevIssueTypeList();

			String dataId = AmtCommonUtils.getTrimStr((String) prevDelIssTypeList.get(0));

			EtsDropDownDAO dropDao = new EtsDropDownDAO();
			EtsIssTypeInfoModel dbIssTypeModel = dropDao.getEtsIssueTypeInfoDetails(dataId);

			EtsIssueTypeGuiUtils guiUtils = new EtsIssueTypeGuiUtils();
			String guiIssueAccess = guiUtils.getIssueAccess(dbIssTypeModel.getIssueAccess());
			String issueOwnerShip = guiUtils.getIssueOwnerShipIbmOnly(dbIssTypeModel.getIssueAccess());

			Global.println("ACT ISSUE ACCESS IN UPD BDLG===" + dbIssTypeModel.getIssueAccess());
			Global.println("GUI ISSUE ACCESS IN UPD BDLG===" + guiIssueAccess);

			Global.println("issueOwnerShip IN UPD BDLG===" + issueOwnerShip);

			if (issueOwnerShip.equals("Yes")) {

				issueOwnerShip = "Y";
			}

			if (issueOwnerShip.equals("No")) {

				issueOwnerShip = "N";
			}

			//set all selected params
			issTypeSessnModel.setDataId(dataId);
			issTypeSessnModel.setIssueType(dbIssTypeModel.getIssueType());
			issTypeSessnModel.setIssueAccess(dbIssTypeModel.getIssueAccess());
			issTypeSessnModel.setIssueSource(dbIssTypeModel.getIssueSource());
			issTypeSessnModel.setGuiIssueAccess(guiIssueAccess);
			issTypeSessnModel.setOwnerShipInternal(issueOwnerShip);
			issTypeSessnModel.setOwnerProfile(dbIssTypeModel.getOwnerProfile());
			issTypeSessnModel.setBackupOwnerProfile(dbIssTypeModel.getBackupOwnerProfile());
			
			if(AmtCommonUtils.isResourceDefined(dbIssTypeModel.getBackupOwnershipInternal()))				
				issTypeSessnModel.setBackupOwnershipInternal(issueOwnerShip);
			else 
				issTypeSessnModel.setBackupOwnershipInternal("");

			//set prev owner list
			EtsIssProjectMember ownerProfile = dbIssTypeModel.getOwnerProfile();
			EtsIssProjectMember backupOwnerProfile = dbIssTypeModel.getBackupOwnerProfile();

			String edgeUserId = ownerProfile.getUserEdgeId();
			String edgeUserEmail = ownerProfile.getUserEmail();
			
			String bkEdgeUserId = backupOwnerProfile.getUserEdgeId();
			String bkEdgeUserEmail = backupOwnerProfile.getUserEmail();
			
			ArrayList prevOwnerList = new ArrayList();
			ArrayList prevBackupOwnerList = new ArrayList();
			
			prevOwnerList.add(edgeUserId);
			prevBackupOwnerList.add(bkEdgeUserId);
			issTypeSessnModel.setPrevOwnerList(prevOwnerList);
			issTypeSessnModel.setPrevBackupOwnerList(prevBackupOwnerList);
			

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, UPDISSUETYPEUNIQID);

			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(4); //make cancel state = 4 edit main page
			issTypeSessnModel.setNextActionState(0);

		} //end of no err msg

		EtsIssueTypeGuiUtils issTypeUtils = new EtsIssueTypeGuiUtils();
		issTypeUtils.debugIssTypeModelDetails(issTypeSessnModel);

		return issTypeSessnModel;

	}

	/**
				 * To get the Issue from sessn
				 */

	public EtsIssTypeInfoModel getIssTypeInfoFromSessn() {

		EtsIssTypeInfoModel issTypeModel = getIssueTypeSessnParams().getSessnIssTypeInfoModel(UPDISSUETYPEUNIQID);

		return issTypeModel;

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

		//get from session
		return errsb.toString();

	}

	public EtsIssTypeInfoModel getEditIssueTypeNameDets() {

		//		//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		int cancelstate = getCancelActionState();

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(cancelstate); //make cancel state
		issTypeSessnModel.setNextActionState(0);

		return issTypeSessnModel;

	}

	public int getCancelActionState() {

		String strcancelstate = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("cancelstate"));
		int cancelstate = 0;

		if (AmtCommonUtils.isResourceDefined(strcancelstate)) {

			cancelstate = Integer.parseInt(strcancelstate);
		}
		return cancelstate;

	}

	/**
					  * 
					  * @return
					  */

	public EtsIssTypeInfoModel getContEditIssueTypeNameDets() throws SQLException, Exception {

		//		//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		int cancelstate = getCancelActionState();

		//			get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr1AddIssTypeModel();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateIssTypName(issTypeParams);

		//print error msg
		Global.println("err msg getContDelIssTypeDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			issTypeSessnModel.setIssueType(issTypeParams.getIssueType()); //add comments
			issTypeSessnModel.setErrMsg(errMsg);
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(UPDEDITISSTYPNAME);

		} else {

			issTypeSessnModel.setIssueType(issTypeParams.getIssueType()); //add new issue type name

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, UPDISSUETYPEUNIQID);

			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(4); //make cancel state = 4 edit main page
			issTypeSessnModel.setNextActionState(UPDISSUETYPEMAINPAGE);

		}

		return issTypeSessnModel;

	}

	/**
				  * 
				  * @return
				  */

	public EtsIssTypeInfoModel getCancelEditIssueTypeNameDets() {

		//		//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		int cancelstate = getCancelActionState();

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(cancelstate); //always to state of modify main page==5
		issTypeSessnModel.setNextActionState(UPDISSUETYPEMAINPAGE);

		return issTypeSessnModel;

	}

	/**
				 * This method will load step1 details and check for validations
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */
	public String validateIssTypName(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

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

		//get from session
		return errsb.toString();

	}

	public EtsIssTypeInfoModel getEditIssueTypeAccess() {

		//		//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		EtsIssueTypeGuiUtils issGuiUtils = new EtsIssueTypeGuiUtils();

		String guiIssueAccess = issGuiUtils.getIssueAccess(issTypeSessnModel.getIssueAccess());

		//set the issue access
		issTypeSessnModel.setGuiIssueAccess(guiIssueAccess);

		int cancelstate = getCancelActionState();

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(cancelstate); //make cancel state
		issTypeSessnModel.setNextActionState(0);

		return issTypeSessnModel;

	}

	/**
					  * 
					  * @return
					  */

	public EtsIssTypeInfoModel getCancelEditIssueTypeAccess() {

		//		//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		int cancelstate = getCancelActionState();

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(cancelstate); //always to state of modify main page==5
		issTypeSessnModel.setNextActionState(UPDISSUETYPEMAINPAGE);

		return issTypeSessnModel;

	}

	/**
							 * This method will load step1 details into session and get the step 2 details
							 * 
							 * @return
							 * @throws SQLException
							 * @throws Exception
							 */
	public EtsIssTypeInfoModel getContEditIssueTypeAccess() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr1AddIssTypeModel();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateIssueAccess(issTypeParams);

		//print error msg
		Global.println("err msg getContAddIssTypeDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			issTypeSessnModel.setGuiIssueAccess(issTypeParams.getIssueAccess()); //add comments
			issTypeSessnModel.setErrMsg(errMsg);
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(UPDEDITISSTYPACCESS);

		} else {

			//add the comments
			//when there are no err msgs

			String issueAccess = AmtCommonUtils.getTrimStr(issTypeParams.getIssueAccess());

			Global.println("PRINTING ISSUE TYPE ACCESS===" + issueAccess);

			issTypeSessnModel.setIssueAccess(issueAccess); //add comments
			issTypeSessnModel.setGuiIssueAccess(issTypeParams.getIssueAccess()); //add comments

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, UPDISSUETYPEUNIQID);

			//			set owner list details
			issTypeSessnModel.setOwnerList(getOwnerListDetails(issTypeParams.getIssueAccess()));
			issTypeSessnModel.setPrevOwnerList(issTypeSessnModel.getPrevOwnerList());

			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state

			if (issueAccess.equals("External") && !getEtsIssObjKey().isProjBladeType()) {

				issTypeSessnModel.setNextActionState(UPDEDITISSTYPACCESSCONT);

			} else {

				issTypeSessnModel.setNextActionState(0);

			}

		} //end of no err msg

		return issTypeSessnModel;

	}

	/**
					 * This method will load step1 details and check for validations
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */
	public String validateIssueAccess(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//cehck for issue access

		if (!AmtCommonUtils.isResourceDefined(issTypeModel.getIssueAccess())) {

			errsb.append("Please provide Security classification.");
			errsb.append("<br />");

		}

		//get from session
		return errsb.toString();

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

		Global.println("ISSUE ACCESS IN OWNER LIST DETAILS====" + issueAccess);

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
								 * This method will load step1 details into session and get the step 2 details
								 * 
								 * @return
								 * @throws SQLException
								 * @throws Exception
								 */
	public EtsIssTypeInfoModel getContEditOwnerDets() throws SQLException, Exception {
		
		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr2AddIssTypeModel();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateIssueOwner(issTypeParams);
		
		String issueAccess = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueAccess());

		//print error msg
		Global.println("err msg getSubmitReqDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			issTypeSessnModel.setPrevOwnerList(issTypeParams.getPrevOwnerList());
			issTypeSessnModel.setErrMsg(errMsg);
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state

			if (issueAccess.equals("External") && !getEtsIssObjKey().isProjBladeType()) {

				issTypeSessnModel.setNextActionState(UPDEDITISSTYPACCESSCONTEXTCONT);

			} else {

				issTypeSessnModel.setNextActionState(UPDEDITISSTYPACCESSCONT);

			}

		} else {

			String prevOwnerStr = "";
			ArrayList prevOwnerList = issTypeParams.getPrevOwnerList();

			if (EtsIssFilterUtils.isArrayListDefnd(prevOwnerList)) {

				prevOwnerStr = (String) prevOwnerList.get(0);
			}

			//get owner profile
			CommonInfoDAO comDao = new CommonInfoDAO();
			issTypeSessnModel.setOwnerProfile(comDao.getUserDetailsInfo(prevOwnerStr));
			issTypeSessnModel.setPrevOwnerList(prevOwnerList);
			issTypeSessnModel.setActiveFlag("Y");

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, UPDISSUETYPEUNIQID);

			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(UPDISSUETYPEMAINPAGE);

		} //end of no err msg

		return issTypeSessnModel;

	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public EtsIssTypeInfoModel getContEditBackupOwnerDets() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr2AddIssTypeModel();

		//check for any error msgs from the form model for scrn1		
		String errMsg = validateIssueBackupOwner(issTypeParams, issTypeSessnModel.getOwnerProfile().getUserEdgeId());

		String issueAccess = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueAccess());

		//print error msg
		Global.println("err msg getContEditBackupOwnerDets()=====" + errMsg);

		//on err msg, the actions
		if (AmtCommonUtils.isResourceDefined(errMsg)) {
			//issTypeSessnModel.setPrevBackupOwnerList(issTypeParams.getPrevBackupOwnerList());
			issTypeSessnModel.setErrMsg(errMsg);
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(UPDEDITISSTYPBKOWNER);  // UPDEDITISSTYPBKOWNER UPDEDITISSTYPBKOWNERCONT

		} else {

			String prevBackupOwnerStr = "";
			ArrayList prevBackupOwnerList = issTypeParams.getPrevBackupOwnerList();

			if (EtsIssFilterUtils.isArrayListDefnd(prevBackupOwnerList)) {

				prevBackupOwnerStr = (String) prevBackupOwnerList.get(0);
			}
			

			//get backup owner profile
			CommonInfoDAO comDao = new CommonInfoDAO();
			if (AmtCommonUtils.isResourceDefined(prevBackupOwnerStr)) {
				issTypeSessnModel.setBackupOwnerProfile(comDao.getUserDetailsInfo(prevBackupOwnerStr));
				issTypeSessnModel.setPrevBackupOwnerList(prevBackupOwnerList);
				issTypeSessnModel.setActiveFlag("Y");	
			}

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, UPDISSUETYPEUNIQID);

			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(UPDISSUETYPEMAINPAGE);

		} //end of no err msg

		return issTypeSessnModel;

	}	


	/**
	 * 
	 * @param issTypeModel
	 * @param OwnerEdgeId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String validateIssueBackupOwner(EtsIssTypeInfoModel issTypeModel, String OwnerEdgeId) throws SQLException, Exception {
		
		StringBuffer errsb = new StringBuffer();
		String backupOwnerEdgeId = "";
		
		if (EtsIssFilterUtils.isArrayListDefnd(issTypeModel.getPrevBackupOwnerList())) {
			
			backupOwnerEdgeId = AmtCommonUtils.getTrimStr((String) issTypeModel.getPrevBackupOwnerList().get(0));
			
			if(OwnerEdgeId.trim().equals(backupOwnerEdgeId.trim()) ) {
				errsb.append("Issue owner and issue backup owner cannot be same. Please select new issue backup owner.");
				errsb.append("<br />");				
			}
			
		} 	
		
		return errsb.toString();
		
	}
	
	
	/**
	 * This method will load step1 details and check for validations
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String validateIssueOwner(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();
		String ownerEdgeId = "";
		String ownshipinternal = "";
		String decafUserType = "";

		//cehck for issue type name

		//			check for issue owner

		if (EtsIssFilterUtils.isArrayListDefnd(issTypeModel.getPrevOwnerList())) {

			if (issTypeModel.getPrevOwnerList().contains("NONE")) {

				errsb.append("Please select issue owner.");
				errsb.append("<br />");

			} else { //none

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

			}
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
	public EtsIssTypeInfoModel getContEditOwnerShipDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr2AddIssTypeModel();

		//add the comments
		//when there are no err msgs
		String ownerShipInt = AmtCommonUtils.getTrimStr(issTypeParams.getOwnerShipInternal());

		String issueAccess = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueAccess());

		//check for any error msgs from the form model for scrn1
		String errMsg = validateIssueOwner(issTypeParams);

		//print error msg
		Global.println("err msg getSubmitReqDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			issTypeSessnModel.setOwnerShipInternal(ownerShipInt);
			issTypeSessnModel.setPrevOwnerList(issTypeParams.getPrevOwnerList());
			issTypeSessnModel.setErrMsg(errMsg);
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state

			if (issueAccess.equals("External") && !getEtsIssObjKey().isProjBladeType()) {

				//issTypeSessnModel.setNextActionState(UPDEDITISSTYPACCESSCONTEXTCONT);
				issTypeSessnModel.setNextActionState(UPDEDITISSTYPACCESSCONTEXT);

			} else {

				issTypeSessnModel.setNextActionState(UPDEDITISSTYPACCESSCONT);

			}

		} else {

			//		///////end

			issTypeSessnModel.setOwnerShipInternal(ownerShipInt);

			String prevOwnerStr = "";
			ArrayList prevOwnerList = issTypeParams.getPrevOwnerList();

			if (EtsIssFilterUtils.isArrayListDefnd(prevOwnerList)) {

				prevOwnerStr = (String) prevOwnerList.get(0);
			}

			//get owner profile
			CommonInfoDAO comDao = new CommonInfoDAO();
			issTypeSessnModel.setOwnerProfile(comDao.getUserDetailsInfo(prevOwnerStr));
			issTypeSessnModel.setPrevOwnerList(prevOwnerList);
			issTypeSessnModel.setActiveFlag("Y");

			//upload the updated object into session//
			setIssueTypeInfoIntoSessn(issTypeSessnModel, UPDISSUETYPEUNIQID);

			issTypeSessnModel.setErrMsg("");
			issTypeSessnModel.setCurrentActionState(currentstate);
			issTypeSessnModel.setCancelActionState(0); //make cancel state
			issTypeSessnModel.setNextActionState(UPDISSUETYPEMAINPAGE);

		} //end of no err msg

		/////////end

		return issTypeSessnModel;

	}

	/**
							 * This method will load step1 details into session and get the step 2 details
							 * 
							 * @return
							 * @throws SQLException
							 * @throws Exception
							 */
	public EtsIssTypeInfoModel getSubmitUpdIssTypeDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(0); //make cancel state

		EtsDropDownDAO dropDao = new EtsDropDownDAO();
		EtsDropDownDataBean dropModel = deriveDropDownData(issTypeSessnModel);

		//if drop model modified successfully
		if (dropDao.updIssueType(dropModel)) {

			issTypeSessnModel.setNextActionState(0);

		} else {

			issTypeSessnModel.setNextActionState(ERRINACTION);
		}

		return issTypeSessnModel;

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

		String dataId = AmtCommonUtils.getTrimStr(issTypeSessnModel.getDataId());
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
		String guiIssueAccess = AmtCommonUtils.getTrimStr(issTypeSessnModel.getGuiIssueAccess());
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
		
		////////// Backup Owner Info ///////////
		ArrayList prevBackupOwnerList = issTypeSessnModel.getPrevBackupOwnerList();

		EtsIssOwnerInfo backupOwnerInfoObj = new EtsIssOwnerInfo();
		//EtsProjMemberDAO projDao = new EtsProjMemberDAO();
		ArrayList backupOwnerInfoList = projDao.getUserIdInfoList(prevBackupOwnerList);
		if (backupOwnerInfoList != null && !backupOwnerInfoList.isEmpty()) 
			backupOwnerInfoObj = (EtsIssOwnerInfo) backupOwnerInfoList.get(0);

		//////////Backup Owner Info ///////////
		
		//update to DB value of issue access, based on interal/external rules
		EtsIssueTypeGuiUtils issGuiUtils = new EtsIssueTypeGuiUtils();

		String updIssueAccess = issGuiUtils.getIssueAccessMatrix(guiIssueAccess, ownShipIntOnly, getEtsIssObjKey().isProjBladeType());

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
		dropModel.setBackupOwnerInfo(backupOwnerInfoObj);

		return dropModel;

	}

	/**
									 * This method will load step1 details into session and get the step 2 details
									 * 
									 * @return
									 * @throws SQLException
									 * @throws Exception
									 */
	public EtsIssTypeInfoModel getEditOwnerDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//add the comments
		//when there are no err msgs
		String ownerShipInt = AmtCommonUtils.getTrimStr(issTypeSessnModel.getOwnerShipInternal());

		String issueAccess = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueAccess());

		Global.println("OWNERSHIP INT====" + ownerShipInt);

		//upload the updated object into session//
		setIssueTypeInfoIntoSessn(issTypeSessnModel, UPDISSUETYPEUNIQID);

		if (issueAccess.equals("Internal") || issueAccess.equals("IBM:IBM")) {

			issTypeSessnModel.setOwnerList(getOwnerListDetails("Internal"));
			
		} else {

			if (ownerShipInt.equals("Y")) {

				issTypeSessnModel.setOwnerList(getOwnerListDetails("Internal"));

			} else {

				issTypeSessnModel.setOwnerList(getOwnerListDetails("External"));

			}

		}

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(0); //make cancel state
		issTypeSessnModel.setNextActionState(0);

		return issTypeSessnModel;

	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public EtsIssTypeInfoModel getEditBackupOwnerDetails() throws SQLException, Exception {

		//			//get from the session the latest model
		EtsIssTypeInfoModel issTypeSessnModel = getIssTypeInfoFromSessn();

		//add the comments
		//when there are no err msgs
		String ownerShipInt = AmtCommonUtils.getTrimStr(issTypeSessnModel.getBackupOwnershipInternal());

		String issueAccess = AmtCommonUtils.getTrimStr(issTypeSessnModel.getIssueAccess());

		Global.println("OWNERSHIP INT====" + ownerShipInt);

		//upload the updated object into session//
		setIssueTypeInfoIntoSessn(issTypeSessnModel, UPDISSUETYPEUNIQID);

		if (issueAccess.equals("Internal") || issueAccess.equals("IBM:IBM")) {

			issTypeSessnModel.setBackupOwnerList(getOwnerListDetails("Internal"));
			
		} else {

			if (ownerShipInt.equals("Y")) {

				issTypeSessnModel.setBackupOwnerList(getOwnerListDetails("Internal"));

			} else {

				issTypeSessnModel.setBackupOwnerList(getOwnerListDetails("External"));

			}

		}

		issTypeSessnModel.setErrMsg("");
		issTypeSessnModel.setCurrentActionState(currentstate);
		issTypeSessnModel.setCancelActionState(0); //make cancel state
		issTypeSessnModel.setNextActionState(0);

		return issTypeSessnModel;

	}	

} //end of class
