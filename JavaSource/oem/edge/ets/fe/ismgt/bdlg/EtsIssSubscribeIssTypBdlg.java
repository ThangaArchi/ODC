/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
import oem.edge.ets.fe.ismgt.dao.EtsIssTypSubscribeDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.EtsIssTypeInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssSubscribeIssTypModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSubscribeIssTypBdlg extends EtsIssActionDataPrepAbsBean implements EtsIssueActionConstants, EtsIssueConstants {

	public static final String VERSION = "1.9";

	private EtsIssTypeParseParams parseParams;
	private int currentstate = 0;

	/**
	 * 
	 */
	public EtsIssSubscribeIssTypBdlg(EtsIssObjectKey etsIssObjKey, int currentstate) {
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

		String issueAccess = "ALL";

		if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I")) {

			issueAccess = "IBM";
		}

		//set proj mem info into obj
		issTypeModel.setSubmitterProfile(projMem);

		//set project info
		issTypeModel.setProjectId(projectId);
		issTypeModel.setProjectName(projectName);
		issTypeModel.setIssueClass(issueClass);
		issTypeModel.setLastUserId(lastUserId);
		issTypeModel.setIssueAccess(issueAccess);
		issTypeModel.setSubsAddIssTypList(getSubsAddIssTypeListDetails(issTypeModel));
		issTypeModel.setSubsDelIssTypList(getSubsDelIssTypeListDetails(issTypeModel));

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

	public ArrayList getSubsAddIssTypeListDetails(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		//		get proj member DAO and get details
		EtsIssTypSubscribeDAO subsDao = new EtsIssTypSubscribeDAO();

		ArrayList issTypeList = subsDao.getSubsAddIssueTypesList(issTypeModel);

		return getDerivedListFromDropModel(issTypeList);
	}

	/**
					  * 
										 * @return
										 * @throws SQLException
										 * @throws Exception
										 */

	public ArrayList getSubsDelIssTypeListDetails(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		//		get proj member DAO and get details
		EtsIssTypSubscribeDAO subsDao = new EtsIssTypSubscribeDAO();

		ArrayList issTypeList = subsDao.getSubsDelIssueTypesList(issTypeModel);

		return getDerivedListFromDropModel(issTypeList);
	}

	/**s
	 * 
	 */
	public ArrayList getDerivedListFromDropModel(ArrayList issTypeList) {

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
							 * This method will load step1 details and check for validations
							 * 
							 * @return
							 * @throws SQLException
							 * @throws Exception
							 */
	public String validateSubsIssueType(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//cehck for issue type name

		//			check for issue owner

		if (EtsIssFilterUtils.isArrayListDefnd(issTypeModel.getPrevSubsAddIssTypList())) {

			int size = issTypeModel.getPrevSubsAddIssTypList().size();

			if (size == 1) {

				if (issTypeModel.getPrevSubsAddIssTypList().contains("NONE")) {

					errsb.append("Please select issue type to be subscribed.");
					errsb.append("<br />");

				}

			}
		}
		
		else {
			
			errsb.append("Please select issue type to be subscribed.");
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
	public String validateUnSubsIssueType(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//cehck for issue type name

		//			check for issue owner

		if (EtsIssFilterUtils.isArrayListDefnd(issTypeModel.getPrevSubsDelIssTypList())) {

			if (issTypeModel.getPrevSubsDelIssTypList().contains("NONE")) {

				int size = issTypeModel.getPrevSubsDelIssTypList().size();

				if (size == 1) {

					errsb.append("Please select issue type to be unsubscribed.");
					errsb.append("<br />");

				}

			}
		}
		
		else {
			
			errsb.append("Please select issue type to be unsubscribed.");
			errsb.append("<br />");
		}

		//get from session
		return errsb.toString();

	}

	/**
							 * This method will model the issue description data 
							 */

	public EtsIssTypeInfoModel subscribeIssueType() throws SQLException, Exception {

		EtsIssTypeInfoModel issTypeModel = getFirstPageDets();

		//			get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr1SubsIssTypeModel();

		//		check for any error msgs from the form model for scrn1
		String errMsg = validateSubsIssueType(issTypeParams);

		//print error msg
		Global.println("err msg getContDelIssTypeDetails ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			issTypeModel.setErrMsg(errMsg);
			issTypeModel.setNextActionState(SUBSISSTYP1STPAGE);

		} else {

			issTypeModel.setPrevSubsAddIssTypList(issTypeParams.getPrevSubsAddIssTypList()); //add new issue type name

			ArrayList subsList = deriveSubsModelData(issTypeModel);

			EtsIssTypSubscribeDAO subsDAO = new EtsIssTypSubscribeDAO();

			if (subsDAO.subsIssueType(subsList)) {

				issTypeModel = getFirstPageDets();

				issTypeModel.setPrevSubsDelIssTypList(issTypeParams.getPrevSubsAddIssTypList());

				issTypeModel.setNextActionState(0);

			} else {

				issTypeModel.setNextActionState(ERRINACTION);
			}

		}

		return issTypeModel;
	}

	/**
								 * This method will model the issue description data 
								 */

	public EtsIssTypeInfoModel unSubscribeIssueType() throws SQLException, Exception {

		EtsIssTypeInfoModel issTypeModel = getFirstPageDets();

		//			get the params model frm the form
		EtsIssTypeInfoModel issTypeParams = parseParams.loadScr1SubsIssTypeModel();

		//		check for any error msgs from the form model for scrn1
		String errMsg = validateUnSubsIssueType(issTypeParams);

		//print error msg
		Global.println("err msg unSubscribeIssueType ()=====" + errMsg);

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			issTypeModel.setErrMsg(errMsg);
			issTypeModel.setNextActionState(SUBSISSTYP1STPAGE);

		} else {

			issTypeModel.setPrevSubsDelIssTypList(issTypeParams.getPrevSubsDelIssTypList()); //add new issue type name

			ArrayList unSubsList = deriveUnSubsModelData(issTypeModel);

			EtsIssTypSubscribeDAO subsDAO = new EtsIssTypSubscribeDAO();

			if (subsDAO.unSubsIssueType(unSubsList)) {

				issTypeModel = getFirstPageDets();

				//show unsubscribed one as selected in subscribed list
				issTypeModel.setPrevSubsAddIssTypList(issTypeParams.getPrevSubsDelIssTypList());

				issTypeModel.setNextActionState(0);

			} else {

				issTypeModel.setNextActionState(ERRINACTION);
			}

		}

		return issTypeModel;
	}

	/**
			 * 
			 * @param issTypeSessnModel
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	private ArrayList deriveSubsModelData(EtsIssTypeInfoModel issTypeSessnModel) throws SQLException, Exception {

		//get subs model list
		ArrayList subsTypList = new ArrayList();

		//get selected subs iss type list
		ArrayList prevSubsIssTypeList = issTypeSessnModel.getPrevSubsAddIssTypList();

		//
		String subsIssueTypeId = "";
		int subssize = 0;

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubsIssTypeList)) {

			subssize = prevSubsIssTypeList.size();

		}

		//remove if the list contains a value of NONE(default start value

		if (subssize > 0) {

			if (prevSubsIssTypeList.contains("NONE")) {

				prevSubsIssTypeList.remove("NONE");
			}
		}

		EtsIssProjectMember submProfile = issTypeSessnModel.getSubmitterProfile();
		String edgeUserId = submProfile.getUserEdgeId();

		String projectId = issTypeSessnModel.getProjectId();
		String lastUserId = issTypeSessnModel.getLastUserId();

		//for subs issue type
		for (int i = 0; i < subssize; i++) {

			subsIssueTypeId = AmtCommonUtils.getTrimStr((String) prevSubsIssTypeList.get(i));

			EtsIssSubscribeIssTypModel subsModel = new EtsIssSubscribeIssTypModel();

			//set all params
			subsModel.setEdgeUserId(edgeUserId);
			subsModel.setIssueTypeId(subsIssueTypeId);
			subsModel.setProjectId(projectId);
			subsModel.setLastUserId(lastUserId);

			//add model to list
			subsTypList.add(subsModel);

		}

		return subsTypList;

	}

	/**
			 * 
			 * @param issTypeSessnModel
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	private ArrayList deriveUnSubsModelData(EtsIssTypeInfoModel issTypeSessnModel) throws SQLException, Exception {

		//get subs model list
		ArrayList unSubsTypList = new ArrayList();

		//get selected un subs iss type list
		ArrayList prevUnSubsIssTypeList = issTypeSessnModel.getPrevSubsDelIssTypList();

		//
		String unSubsIssueTypeId = "";
		int unsubssize = 0;

		if (EtsIssFilterUtils.isArrayListDefnd(prevUnSubsIssTypeList)) {

			unsubssize = prevUnSubsIssTypeList.size();

		}
		
		//remove first element

		if (unsubssize > 0) {

			if (prevUnSubsIssTypeList.contains("NONE")) {

				prevUnSubsIssTypeList.remove("NONE");
			}
		}

		EtsIssProjectMember submProfile = issTypeSessnModel.getSubmitterProfile();
		String edgeUserId = submProfile.getUserEdgeId();

		String projectId = issTypeSessnModel.getProjectId();
		String lastUserId = issTypeSessnModel.getLastUserId();

		//for subs issue type
		for (int i = 0; i < unsubssize; i++) {

			unSubsIssueTypeId = AmtCommonUtils.getTrimStr((String) prevUnSubsIssTypeList.get(i));

			EtsIssSubscribeIssTypModel subsModel = new EtsIssSubscribeIssTypModel();

			//set all params
			subsModel.setEdgeUserId(edgeUserId);
			subsModel.setIssueTypeId(unSubsIssueTypeId);
			subsModel.setProjectId(projectId);
			subsModel.setLastUserId(lastUserId);

			//add model to list
			unSubsTypList.add(subsModel);

		}

		return unSubsTypList;

	}

} //end of class
