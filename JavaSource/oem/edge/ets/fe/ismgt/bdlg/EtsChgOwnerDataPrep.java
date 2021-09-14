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
import oem.edge.common.*;
import oem.edge.amt.*;
import oem.edge.ets.fe.ismgt.dao.*;
import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.helpers.*;
import oem.edge.ets.fe.ismgt.model.EtsChgOwnerInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsChgOwnerDataPrep extends EtsIssActionDataPrepAbsBean implements EtsIssueActionConstants {

	public static final String VERSION = "1.24";
	private EtsIssParseFormParams parseParams;
	private int currentstate = 0;

	/**
	 * 
	 */

	public EtsChgOwnerDataPrep(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.parseParams = new EtsIssParseFormParams(etsIssObjKey);
		this.currentstate = currentstate;

	}

	/**
			 * 
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsChgOwnerInfoModel getChgOwnerInitDetails() throws SQLException, Exception {

		//get the edge problem id
		String edgeProblemId = (String) getEtsIssObjKey().getParams().get("edge_problem_id");
		String projectId = (String) getEtsIssObjKey().getParams().get("proj");

		//get current problem id info
		//get the issue details from DB	
		ETSIssue currentIssue = ETSIssuesManager.getIssue(edgeProblemId);
		String issueAccess = AmtCommonUtils.getTrimStr(currentIssue.issue_access);
		String issueTypeId = AmtCommonUtils.getTrimStr(currentIssue.issueTypeId);

		//get the current owner info//
		IssueInfoDAO issInfoDao = new IssueInfoDAO();
		ArrayList probOwnerList = issInfoDao.getProbOwnerList(edgeProblemId);

		//get owner edge id info, based on problem id
		EtsIssOwnerInfo etsOwnerInfo = new EtsIssOwnerInfo();
		etsOwnerInfo = (EtsIssOwnerInfo) probOwnerList.get(0);

		//get owner edge id
		String ownerEdgeId = "";

		if (etsOwnerInfo != null) {

			ownerEdgeId = AmtCommonUtils.getTrimStr(etsOwnerInfo.getUserEdgeId());

		}

		//add edge id to array list
		ArrayList ownerEdgeIdList = new ArrayList();
		ownerEdgeIdList.add(ownerEdgeId);

		//get the project member list for the current project id
		EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

		//get the owner info details based on edgeId
		ArrayList ownerInfoList = projMemDao.getUserIdInfoList(ownerEdgeIdList);

		//get the owner info
		EtsIssOwnerInfo ownerInfo = new EtsIssOwnerInfo();

		ownerInfo = (EtsIssOwnerInfo) ownerInfoList.get(0);

		//get the submitter profile info
		EtsIssProjectMember submitterInfo = new EtsIssProjectMember();

		submitterInfo.setUserFullName(getEtsIssObjKey().getEs().gFIRST_NAME + " " + getEtsIssObjKey().getEs().gLAST_NAME);
		submitterInfo.setUserEmail(getEtsIssObjKey().getEs().gEMAIL);
		submitterInfo.setUserContPhone(getEtsIssObjKey().getEs().gPHONE);
		submitterInfo.setUserCustCompany(getEtsIssObjKey().getEs().gASSOC_COMP);

		//get the proj mem list
		ArrayList projMemList = new ArrayList();

		if (!getEtsIssObjKey().isProjBladeType()) {

			projMemList = projMemDao.getProjMemberListWithUserTypeWthoutVisitors(projectId, getEtsIssObjKey().isProjBladeType());

		} else {

			projMemList = projMemDao.getWrkSpcOwnerMgrListForProject(projectId);
		}

		//get the issue type access and then filter owner details based on that, not on issue access
		EtsDropDownDAO dropDao = new EtsDropDownDAO();
		EtsIssTypeInfoModel issTypeModel = dropDao.getEtsIssueTypeInfoDetails(issueTypeId);
		String issueTypeSecAccess = AmtCommonUtils.getTrimStr(issTypeModel.getIssueAccess());
		Global.println("ISSUE TYPE ACCESS=="+issueTypeSecAccess);
		
		//for non-blade project, the owner list depeneds on upon issue type access
		//for blade project, the owner list are always IBM

		if (!getEtsIssObjKey().isProjBladeType()) {

			if (issueTypeSecAccess.equals("IBM:IBM")) {

				issueAccess = "IBM";
			}

			if (issueTypeSecAccess.equals("ALL:IBM")) {

				issueAccess = "IBM";
			}

			if (issueTypeSecAccess.equals("ALL:EXT")) {

				issueAccess = "ALL";
			}

		} else {

			issueAccess = "IBM";
		}

		//set the owner info model for screen
		EtsChgOwnerInfoModel ownerInfoModel = new EtsChgOwnerInfoModel();
		ownerInfoModel.setEdgeProblemId(edgeProblemId);
		ownerInfoModel.setOwnerIdList(filterOwnerListDetails(projMemList, issueAccess)); //add avail owner list
		ownerInfoModel.setPrevOwnerIdList(ownerEdgeIdList); //add prev owner list
		ownerInfoModel.setSubmitterInfo(submitterInfo); //add current submitter info
		ownerInfoModel.setOwnerInfo(ownerInfo); //add current owner info

		return ownerInfoModel;
	}

	/**
		  * check with JV for logic
		  * check the issue_access of the issue type, instead of issue_access of issue
							 * @return
							 * @throws SQLException
							 * @throws Exception
							 */

	public ArrayList filterOwnerListDetails(ArrayList projMemList, String issueAccess) throws SQLException, Exception {

		ArrayList filtMemList = new ArrayList();

		int projsize = 0;
		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";

		Global.println("Issue access in filterOwnerList details===" + issueAccess);

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			projsize = projMemList.size();

			for (int i = 0; i < projsize; i = i + 4) {

				etsUserEdgeId = (String) projMemList.get(i);
				etsUserNameWithIrId = (String) projMemList.get(i + 1);
				etsUserType = (String) projMemList.get(i + 2);

				if (issueAccess.equals("IBM")) {

					if (etsUserType.equals("I")) {

						filtMemList.add(etsUserEdgeId);
						filtMemList.add(etsUserNameWithIrId);

					}

				} else {

					filtMemList.add(etsUserEdgeId);
					filtMemList.add(etsUserNameWithIrId);

				}

			} //end of for

		} //if projMemelist is defined

		return filtMemList;
	}

	/**
						 * This method will load step1 details and check for validations
						 * 
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */
	public String validateScrn1FormFields(EtsChgOwnerInfoModel ownerInfoModel) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//			check for issue owner

		if (EtsIssFilterUtils.isArrayListDefnd(ownerInfoModel.getPrevOwnerIdList())) {

			if (ownerInfoModel.getPrevOwnerIdList().contains("NONE")) {

				errsb.append("Please select issue owner.");
				errsb.append("<br />");

			}
		}

		//get from session
		return errsb.toString();

	}

	public EtsChgOwnerInfoModel submitOwnerDetails() throws SQLException, Exception {

		//		get the edge problem id
		String edgeProblemId = (String) getEtsIssObjKey().getParams().get("edge_problem_id");

		//		get the params model frm the form
		EtsChgOwnerInfoModel ownerValidInfoModel = parseParams.loadScr1ChgOwnerDetails();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateScrn1FormFields(ownerValidInfoModel);

		//print error msg
		Global.println("err msg submitOwnerDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			EtsChgOwnerInfoModel ownerInfoOldModel = getChgOwnerInitDetails();

			ownerInfoOldModel.setErrMsg(errMsg);
			ownerInfoOldModel.setCurrentActionState(currentstate);
			ownerInfoOldModel.setCancelActionState(0); //make cancel state
			ownerInfoOldModel.setNextActionState(CHGOWNER1STPAGE);

			return ownerInfoOldModel;

		} else {

			EtsChgOwnerInfoModel ownerInfoNewModel = new EtsChgOwnerInfoModel();

			//get seq no from cq table
			int cq_seq_no = ETSIssuesManager.getCq1Seq_no(edgeProblemId);

			//get the current owner from UI
			ArrayList prevOwnerEdgeIdList = ownerValidInfoModel.getPrevOwnerIdList();

			//	get the project member list for the current project id
			EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

			//get the owner info details based on edgeId
			ArrayList ownerInfoList = projMemDao.getUserIdInfoList(prevOwnerEdgeIdList);

			//get the owner info
			EtsIssOwnerInfo ownerInfo = new EtsIssOwnerInfo();

			ownerInfo = (EtsIssOwnerInfo) ownerInfoList.get(0);

			//build owner info model for submission to DB
			ownerInfoNewModel.setEdgeProblemId(edgeProblemId);
			ownerInfoNewModel.setSeqNo(cq_seq_no + 4); //add 4 to the seq no
			ownerInfoNewModel.setPrevOwnerIdList(prevOwnerEdgeIdList);
			ownerInfoNewModel.setOwnerInfo(ownerInfo);
			ownerInfoNewModel.setLastUserId((String) getEtsIssObjKey().getEs().gUSERN);
			ownerInfoNewModel.setErrMsg("");
			ownerInfoNewModel.setLastUserFirstName((String) getEtsIssObjKey().getEs().gFIRST_NAME);
			ownerInfoNewModel.setLastUserLastName((String) getEtsIssObjKey().getEs().gLAST_NAME);

			ChgOwnerDAO ownerDao = new ChgOwnerDAO();

			if (ownerDao.insertCqOwnerUsr(ownerInfoNewModel, getEtsIssObjKey())) {

				ownerInfoNewModel.setNextActionState(0);

			} else {

				ownerInfoNewModel.setNextActionState(ERRINACTION);

			}

			return ownerInfoNewModel;

		} //end of no err msg

	}

} //end of class
