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
package oem.edge.ets.fe.acmgt.helpers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.AmtProfileDAObject;
import oem.edge.amt.AmtUserProfile;
import oem.edge.amt.AmtUserProfileFactory;
import oem.edge.amt.UserObject;
import oem.edge.decaf.ws.DecafEntAccessObj;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.acmgt.bdlg.AddMembrProcDataPrep;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.dao.WrkSpcInfoDAO;
import oem.edge.ets.fe.acmgt.model.UserIccStatusModel;
import oem.edge.ets.fe.acmgt.model.UserInviteStatusModel;
import oem.edge.ets.fe.acmgt.model.UserWrkSpcStatusModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsOpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamObjKey;
import oem.edge.ets.fe.acmgt.resources.WrkSpcTeamConstantsIF;
import oem.edge.ets.fe.acmgt.resources.WrkSpcTeamResource;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

import com.ibm.bluepages.BPResults;
import com.ibm.bluepages.BluePages;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WrkSpcTeamUtils implements WrkSpcTeamConstantsIF {

	private AddMembrToWrkSpcDAO wrkSpcDao;
	private static Log logger = EtsLogger.getLogger(WrkSpcTeamUtils.class);
	public static final String VERSION = "1.12";

	/**
	 *
	 */
	public WrkSpcTeamUtils() {
		super();
		wrkSpcDao = new AddMembrToWrkSpcDAO();
		// TODO Auto-generated constructor stub
	}

	/**
		 *
		 * @param projectId
		 * @param userId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public boolean isUserDefndInWrkSpc(String projectId, String userId) throws SQLException, Exception {

		UserWrkSpcStatusModel wrkSpcStat = wrkSpcDao.getUserIdStatusInWrkSpc(projectId, userId);

		int uidcount = wrkSpcStat.getWrkspcuidcount();
		int emailcount = wrkSpcStat.getWrkspcemailcount();

		if (uidcount == 0 && emailcount == 0) {

			return false;

		} else if (uidcount > 0 || emailcount > 0) {

			return true;
		}

		return false;

	}

	/**
	 *
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean isUserDefndinICC(String userId) throws SQLException, Exception {

		UserIccStatusModel iccStatModel = wrkSpcDao.getUserIdStatusInICC(userId);

		int uidcount = iccStatModel.getAmtuidcount();
		int emailcount = iccStatModel.getAmtemailcount();

		if (uidcount == 0 && emailcount == 0) {

			return false;

		} else if (uidcount > 0 || emailcount > 0) {

			return true;
		}

		return false;

	}

	/**
		 *
		 * @param userId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public boolean isUserIdDefndInUD(String userId) throws SQLException, Exception {

		return wrkSpcDao.isUserIdDefndInUD(userId);

	}

	/**
	 *
	 * @param wrkSpcType
	 * @return
	 */

	public HashMap getWrkSpcPropMap(String wrkSpcType) {

		//get prop map based on wrk spc type
		return WrkSpcTeamResource.getInstance(wrkSpcType).getWrkSpcPropMap();
	}

	/**
	 *
	 * @param sIRUserId
	 * @param reqstrIRUserId
	 * @param projectId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public WrkSpcTeamObjKey getWrkSpcTeamObjDets(WrkSpcTeamActionsInpModel actInpModel) throws SQLException, Exception {

		AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();

		WrkSpcTeamObjKey teamObjKey = new WrkSpcTeamObjKey();

		//get the details based on object and return the full one
		String sIRUserId = actInpModel.getUserId();
		String reqstrIRUserId = actInpModel.getRequestorId();
		String wrkSpcId = actInpModel.getWrkSpcId();

		//get the project details
		ETSProj wrkSpcDets = WrkSpcInfoDAO.getProjectDetails(wrkSpcId);

		//assign to wrk spc ownder id

		String wrkSpcOwnrId = getOwnerIdForProject(wrkSpcId);

		logger.debug("owner id for workspace in WrkSpcTeamObjKey==" + wrkSpcOwnrId);

		//get the ets user dets, for which team action is reqd
		ETSUserDetails etsUserDets = wrkSpcDao.getUserDetails(sIRUserId);

		//get the requestor details, who has started the reqst
		ETSUserDetails reqstUserDets = wrkSpcDao.getUserDetails(reqstrIRUserId);

		//get the wrkspc owner details
		ETSUserDetails wrkSpcOwnerDets = wrkSpcDao.getUserDetails(wrkSpcOwnrId);

		//set all values
		teamObjKey.setSIRUserId(sIRUserId);
		teamObjKey.setReqstrIRUserId(reqstrIRUserId);
		teamObjKey.setWrkSpcOwnrId(wrkSpcOwnrId);
		teamObjKey.setEtsUserIdDets(etsUserDets);
		teamObjKey.setReqUserIdDets(reqstUserDets);
		teamObjKey.setWrkSpcOwnrDets(wrkSpcOwnerDets);
		teamObjKey.setWrkSpcId(wrkSpcId);
		teamObjKey.setWrkSpc(wrkSpcDets);

		return teamObjKey;

	}

	/**
	 *
	 * @param wrkSpcType
	 * @param isITAR
	 * @return
	 */

	public static String getWrkSpcReqEntitlement(String wrkSpcType, boolean isITAR) {

		String entitlement = "";

		if (wrkSpcType.equals(Defines.ETS_WORKSPACE_TYPE)) {

			if (!isITAR) {

				entitlement = Defines.ETS_ENTITLEMENT;

			} else {

				entitlement = Defines.ITAR_ENTITLEMENT;

			}

		}

		if (wrkSpcType.equals(Defines.AIC_WORKSPACE_TYPE)) {

			//change the ETS project to AIC project
			entitlement = Defines.AIC_ENTITLEMENT;

		}

		return entitlement;
	}

	/**
	 *
	 * @param wrkSpc
	 * @return
	 */

	public static String getWrkSpcReqEntitlement(ETSProj wrkSpc) {

		return getWrkSpcReqEntitlement(wrkSpc.getProjectType(), wrkSpc.isITAR());
	}

	/**
	 *
	 * @param wrkSpcType
	 * @param isITAR
	 * @return
	 */

	public static String getWrkSpcReqProject(String wrkSpcType, boolean isITAR) {

		String reqProject = "";

		if (wrkSpcType.equals(Defines.ETS_WORKSPACE_TYPE)) {

			if (!isITAR) {

				reqProject = Defines.REQUEST_PROJECT;

			} else {

				reqProject = Defines.ITAR_PROJECT;

			}

		}

		if (wrkSpcType.equals(Defines.AIC_WORKSPACE_TYPE)) {

			//return AIC entitlement, as there is no project concept for AIC
			reqProject = Defines.AIC_ENTITLEMENT;

		}

		return reqProject;
	}

	/**
		 *
		 * @param wrkSpc
		 * @return
		 */

	public static String getWrkSpcReqProject(ETSProj wrkSpc) {

		return getWrkSpcReqProject(wrkSpc.getProjectType(), wrkSpc.isITAR());
	}

	/**
		 *
		 * @param wrkSpcType
		 * @param isITAR
		 * @return
		 */

	public static DecafEntAccessObj getWrkSpcReqEntitlementObj(String wrkSpcType, boolean isITAR) {

		String entitlement = getWrkSpcReqEntitlement(wrkSpcType, isITAR);

		DecafEntAccessObj decafEntAccObj = new DecafEntAccessObj();

		decafEntAccObj.setEntName(entitlement);

		if (wrkSpcType.equals(Defines.ETS_WORKSPACE_TYPE)) {

			decafEntAccObj.setLevel(1);
			Vector dtVec = new Vector();
			dtVec.add(Defines.ETS_PROJ_DATATYPE);
			decafEntAccObj.setDataTypeVal(dtVec);
		}

		if (wrkSpcType.equals(Defines.AIC_WORKSPACE_TYPE)) {

			decafEntAccObj.setLevel(1);
			Vector dtVec = new Vector();
			dtVec.add(Defines.AIC_PROJ_DATATYPE);
			decafEntAccObj.setDataTypeVal(dtVec);

		}

		return decafEntAccObj;
	}

	/**
		 *
		 * @param wrkSpcType
		 * @param isITAR
		 * @return
		 */

	public static DecafEntAccessObj getWrkSpcReqEntitlementObj(ETSProj wrkSpc) {

		return getWrkSpcReqEntitlementObj(wrkSpc.getProjectType(), wrkSpc.isITAR());
	}

	/**
	 *
	 * @param wrkSpc
	 * @param sLogMsg
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public static String getMetricsLogMsg(ETSProj wrkSpc, String sLogMsg) throws SQLException, Exception {

		String wrkSpcLogMsg = "";
		String wrkSpcType = wrkSpc.getProjectType();
		boolean isITAR = wrkSpc.isITAR();
		String projectProposal = wrkSpc.getProjectOrProposal();

		return getMetricsLogMsg(wrkSpcType, isITAR, projectProposal, sLogMsg);

	}

	/**
		 *
		 * @param wrkSpc
		 * @param sLogMsg
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public static String getMetricsLogMsg(String wrkSpcType, boolean isITAR, String projectProposal, String sLogMsg) throws SQLException, Exception {

		String wrkSpcLogMsg = "";

		if (wrkSpcType.equals(Defines.ETS_WORKSPACE_TYPE)) {

			if (!isITAR) {

				wrkSpcLogMsg = Defines.ETS_WORKSPACE_TYPE + "_";

			} else {

				wrkSpcLogMsg = Defines.ETS_WORKSPACE_TYPE + "_ITAR_";

			}

		}

		if (wrkSpcType.equals(Defines.AIC_WORKSPACE_TYPE)) {

			//change the ETS project to AIC project
			wrkSpcLogMsg = Defines.AIC_WORKSPACE_TYPE + "_";

		}

		if (projectProposal.equals("P")) {

			wrkSpcLogMsg += "Project_";

		} else {

			wrkSpcLogMsg += "Proposal_";
		}

		wrkSpcLogMsg += sLogMsg;

		return wrkSpcLogMsg;
	}

	/**
	 *
	 * @param actInpModel
	 * @param teamObjKey
	 */

	public static void printCompInvRecord(WrkSpcTeamActionsInpModel actInpModel, WrkSpcTeamObjKey teamObjKey) {

		if (logger.isDebugEnabled()) {

			logger.debug("PRINTING COMPLETE INVITE PROCESS RECORD");
			logger.debug("USERID:::" + AmtCommonUtils.getTrimStr(actInpModel.getUserId()));
			logger.debug("REQUESTOR_ID:::" + AmtCommonUtils.getTrimStr(actInpModel.getRequestorId()));
			logger.debug("USER COMPANY:::" + AmtCommonUtils.getTrimStr(actInpModel.getUserAssignCompany()));
			logger.debug("USER COUNTRY:::" + AmtCommonUtils.getTrimStr(actInpModel.getUserAssignCountry()));
			logger.debug("LAST USERID:::" + AmtCommonUtils.getTrimStr(actInpModel.getLastUserId()));
			logger.debug("USER ROLE ID:::" + actInpModel.getRoleId());
			logger.debug("USER STATUS:::" + AmtCommonUtils.getTrimStr(actInpModel.getUserStatus()));
			logger.debug("WRK SPC ID:::" + AmtCommonUtils.getTrimStr(actInpModel.getWrkSpcId()));
			logger.debug("WRK SPC TYPE:::" + AmtCommonUtils.getTrimStr(actInpModel.getWrkSpcType()));
			logger.debug("######################################");
			logger.debug("PRINTING TEAM OBJ KEY INFO");
			logger.debug("USER ID - WEB ID==" + AmtCommonUtils.getTrimStr(teamObjKey.getEtsUserIdDets().getWebId()));
			logger.debug("USER ID - EDGE ID==" + AmtCommonUtils.getTrimStr(teamObjKey.getEtsUserIdDets().getEdgeId()));
			logger.debug("USER ID - EMAIL ID==" + AmtCommonUtils.getTrimStr(teamObjKey.getEtsUserIdDets().getEMail()));
			logger.debug("REQ USER ID - WEB ID==" + AmtCommonUtils.getTrimStr(teamObjKey.getReqUserIdDets().getWebId()));
			logger.debug("REQ USER ID - EDGE ID==" + AmtCommonUtils.getTrimStr(teamObjKey.getReqUserIdDets().getEdgeId()));
			logger.debug("REQ USER ID - EMAIL ID==" + AmtCommonUtils.getTrimStr(teamObjKey.getReqUserIdDets().getEMail()));
			logger.debug("OWNER USER ID - WEB ID==" + AmtCommonUtils.getTrimStr(teamObjKey.getWrkSpcOwnrDets().getWebId()));
			logger.debug("OWNER USER ID - EDGE ID==" + AmtCommonUtils.getTrimStr(teamObjKey.getWrkSpcOwnrDets().getEdgeId()));
			logger.debug("OWNER USER ID - EMAIL ID==" + AmtCommonUtils.getTrimStr(teamObjKey.getWrkSpcOwnrDets().getEMail()));
			logger.debug("WRK SPC ID=" + AmtCommonUtils.getTrimStr(teamObjKey.getWrkSpc().getProjectId()));
			logger.debug("WRK SPC NAME==" + AmtCommonUtils.getTrimStr(teamObjKey.getWrkSpc().getName()));
			logger.debug("WRK SPC TYPE==" + AmtCommonUtils.getTrimStr(teamObjKey.getWrkSpc().getProjectType()));
			logger.debug("WRK SPC ITAR==" + teamObjKey.getWrkSpc().isITAR());

		}

	}

	/**
	 *
	 * @param actInpModel
	 */

	public static void printActInpRecord(WrkSpcTeamActionsInpModel actInpModel) {

		if (logger.isDebugEnabled()) {

			logger.debug("PRINTING ACTION INPUT MODEL  RECORD");
			logger.debug("USERID:::" + AmtCommonUtils.getTrimStr(actInpModel.getUserId()));
			logger.debug("REQUESTOR_ID:::" + AmtCommonUtils.getTrimStr(actInpModel.getRequestorId()));
			logger.debug("USER COMPANY:::" + AmtCommonUtils.getTrimStr(actInpModel.getUserAssignCompany()));
			logger.debug("USER COUNTRY:::" + AmtCommonUtils.getTrimStr(actInpModel.getUserAssignCountry()));
			logger.debug("LAST USERID:::" + AmtCommonUtils.getTrimStr(actInpModel.getLastUserId()));
			logger.debug("USER ROLE ID:::" + actInpModel.getRoleId());
			logger.debug("USER STATUS:::" + AmtCommonUtils.getTrimStr(actInpModel.getUserStatus()));
			logger.debug("WRK SPC ID:::" + AmtCommonUtils.getTrimStr(actInpModel.getWrkSpcId()));
			logger.debug("WRK SPC TYPE:::" + AmtCommonUtils.getTrimStr(actInpModel.getWrkSpcType()));
			logger.debug("######################################");

		}

	}

	/**
		 *
		 * @param actInpModel
		 */

	public static void printInvStatRecord(UserInviteStatusModel invStatModel) {

		if (logger.isDebugEnabled()) {

			logger.debug("PRINTING INVITE STATUS MODEL  RECORD");
			logger.debug("USERID:::" + AmtCommonUtils.getTrimStr(invStatModel.getUserId()));
			logger.debug("REQUESTOR_ID:::" + AmtCommonUtils.getTrimStr(invStatModel.getRequestorId()));
			logger.debug("USER COMPANY:::" + AmtCommonUtils.getTrimStr(invStatModel.getUserCompany()));
			logger.debug("USER COUNTRY:::" + AmtCommonUtils.getTrimStr(invStatModel.getUserCountryCode()));
			logger.debug("LAST USERID:::" + AmtCommonUtils.getTrimStr(invStatModel.getLastUserId()));
			logger.debug("USER ROLE ID:::" + invStatModel.getRoleId());
			logger.debug("USER STATUS:::" + AmtCommonUtils.getTrimStr(invStatModel.getInviteStatus()));
			logger.debug("WRK SPC ID:::" + AmtCommonUtils.getTrimStr(invStatModel.getWrkSpcId()));
			logger.debug("WRK SPC TYPE:::" + AmtCommonUtils.getTrimStr(invStatModel.getWrkSpcType()));
			logger.debug("######################################");

		}

	}

	/**
	 *
	 * @param invStatModel
	 * @return
	 */

	public static WrkSpcTeamActionsInpModel transFormToInpModel(UserInviteStatusModel invStatModel) {

		WrkSpcTeamActionsInpModel actInpModel = new WrkSpcTeamActionsInpModel();

		actInpModel.setUserId(invStatModel.getUserId());
		actInpModel.setRequestorId(invStatModel.getRequestorId());
		actInpModel.setWrkSpcId(invStatModel.getWrkSpcId());
		actInpModel.setWrkSpcType(invStatModel.getWrkSpcType());
		actInpModel.setUserAssignCompany(invStatModel.getUserCompany());
		actInpModel.setUserAssignCountry(invStatModel.getUserCountryCode());
		actInpModel.setRoleId(invStatModel.getRoleId());
		actInpModel.setLastUserId(invStatModel.getLastUserId());

		return actInpModel;
	}

	/**
		 *
		 * @param invStatModel
		 * @return
		 */

	public static UserInviteStatusModel transFormToInvStatModel(WrkSpcTeamActionsInpModel actInpModel) {

		UserInviteStatusModel invStatModel = new UserInviteStatusModel();

		invStatModel.setUserId(actInpModel.getUserId());
		invStatModel.setRequestorId(actInpModel.getRequestorId());
		invStatModel.setWrkSpcId(actInpModel.getWrkSpcId());
		invStatModel.setWrkSpcType(actInpModel.getWrkSpcType());
		invStatModel.setUserCompany(actInpModel.getUserAssignCompany());
		invStatModel.setUserCountryCode(actInpModel.getUserAssignCountry());
		invStatModel.setRoleId(actInpModel.getRoleId());
		invStatModel.setLastUserId(actInpModel.getLastUserId());

		return invStatModel;
	}

	/**
	 *
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public static Connection getConnection() throws SQLException, Exception {

		return ETSDBUtils.getConnection(ETS_BE_DATASRC);
	}

	/**
	 *
	 * @param wrkSpcId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public static String getOwnerIdForProject(String wrkSpcId) throws SQLException, Exception {

		//	get the project details
		ETSProj wrkSpcDets = WrkSpcInfoDAO.getProjectDetails(wrkSpcId);

		//get the owner details,
		Vector owners = WrkSpcInfoDAO.getUsersByProjectPriv(wrkSpcId, Defines.OWNER);
		ETSUser owner = new ETSUser();

		if (owners.size() > 0) {

			owner = (ETSUser) owners.elementAt(0); // take the first

		}

		//assign to wrk spc ownder id
		String wrkSpcOwnrId = owner.getUserId(); //get the info frm proj itself

		return wrkSpcOwnrId;

	}

	/**
	 *
	 * @param userId
	 * @param projectId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public static String getUserRole(String userId, String projectId) throws SQLException, Exception {

		return WrkSpcInfoDAO.getUserRole(userId, projectId);
	}

	public boolean isIRIdExistsinUD(String userId) {

		Connection conn = null;
		boolean flag = false;
		try {

			conn = ETSDBUtils.getConnection();

			flag = AccessCntrlFuncs.isIbmIdExistsInIR(conn, userId);
		} catch (SQLException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		} finally {

			ETSDBUtils.close(conn);

		}

		return flag;
	}

	public UserObject refreshUserDetailsBasedOnId(String userId) {

		Connection conn = null;
		boolean flag = false;
		UserObject userDet = new UserObject();
		try {

			conn = ETSDBUtils.getConnection();

			userDet = AccessCntrlFuncs.refreshUserDetailsBasedOnId(conn, userId, true, false);
		} catch (SQLException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		} finally {

			ETSDBUtils.close(conn);

		}

		return userDet;
	}

	public AmtUserProfile getUserProfile(String userid) {

		Connection conn = null;

		int retVal = 0;
		AmtUserProfileFactory prffactory = new AmtUserProfileFactory();
		Hashtable params = new Hashtable();
		AmtUserProfile usrprofile = null;
		AmtProfileDAObject dbprofile = null;

		try {

			conn = ETSDBUtils.getConnection();

			usrprofile = new AmtUserProfile();
			dbprofile = new AmtProfileDAObject();

			params.put("userid", userid);

			///

			if ((userid.toLowerCase()).endsWith("ibm.com")) {

				System.out.println("Profile reading  from BLUE PAGES START");
				usrprofile = prffactory.initiateUserProfile("bluepages").getUserProfile(conn, userid);
				System.out.println("Profile reading  from BLUE PAGES END");

			} else {

				System.out.println("Profile reading  from WEB-ID START");
				usrprofile = prffactory.initiateUserProfile("webid").getUserProfile(conn, userid);
				System.out.println("Profile reading  from WEB-ID END");
			}

			//usrprofile.

			//						System.out.println("LAODING PROFILE INTO DB START");
			//						dbprofile.setAmtprofile(usrprofile);
			//						retVal=usrprofile.getPullprofile();
			//						//if(retVal==0)
			//						 //retVal=dbprofile.saveProfile(conn,userid);
			//						System.out.println("LAODING PROFILE INTO DB END");

		} catch (Exception e) {

			e.printStackTrace();
		} finally {

			ETSDBUtils.close(conn);

		}
		return usrprofile;

	}

	/**
	 *
	 * @param projType
	 * @return
	 */

	public static boolean isReqAccessProject(String projType) {

		if (projType.equals(Defines.ETS_WORKSPACE_TYPE)) {

			return true;
		}

		if (projType.equals(Defines.AIC_WORKSPACE_TYPE)) {

			return false;
		}

		return true;

	}

	/**
		 *
		 * @param projType
		 * @return
		 */

	public static boolean isReqAccessProject(ETSProj etsProj) {

		return isReqAccessProject(etsProj.getProjectType());

	}

	/**
	 * get brand props
	 * @param projectType
	 * @return
	 */

	public static UnbrandedProperties getBrandProps(String projectType) {

		UnbrandedProperties unBrandedprop = null;

		String projType = AmtCommonUtils.getTrimStr(projectType);

		if (!AmtCommonUtils.isResourceDefined(projType)) {

			projType = Defines.ETS_WORKSPACE_TYPE;
		}

		unBrandedprop = PropertyFactory.getProperty(projType);

		return unBrandedprop;

	}

	/**
	 *
	 * @param projectName
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public static UnbrandedProperties getBrandPropsFromProjName(String projectName) throws SQLException, Exception {

		ETSProj etsProj = ETSDatabaseManager.getProjectDetsFromProjName(projectName);

		String projType = "";

		if (etsProj != null) {

			projType = AmtCommonUtils.getTrimStr(etsProj.getProjectType());
		}

		return getBrandProps(projType);

	}

	/**
		 *
		 * @param projectName
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public static UnbrandedProperties getBrandPropsFromProjId(String projectId) throws SQLException, Exception {

		ETSProj etsProj = ETSDatabaseManager.getProjectDetails(projectId);

		String projType = "";

		if (etsProj != null) {

			projType = AmtCommonUtils.getTrimStr(etsProj.getProjectType());
		}

		return getBrandProps(projType);

	}

	/**
	 *
	 * @param parentId
	 * @return
	 */

	public static boolean isWrkSpcSubType(String parentId) {

		if (!AmtCommonUtils.getTrimStr(parentId).equals("0")) {

			return true;
		}

		return false;

	}


	public static boolean isWrkSpcSubType(ETSProj etsProj) {

		return isWrkSpcSubType(etsProj.getParent_id());
	}


	public ArrayList getCompList()throws SQLException, Exception{

		return wrkSpcDao.getCompanyList();
	}

	public Vector getCntryList()throws SQLException, Exception{

		return wrkSpcDao.getCntryList();
	}

	public Vector getIntUserPrvlgs(String projId) throws SQLException , Exception{

		return wrkSpcDao.getIntUserPrvlgs(projId);
	}

	public Vector getExtUserPrvlgs(String projId) throws SQLException , Exception{

		return wrkSpcDao.getExtUserPrvlgs(projId);
	}


	public static boolean checkOwnerMultiPOC(Connection conn, String sProjId, String sIRUserId) throws SQLException, Exception {

		AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();
		AddMembrProcDataPrep addMmbrBdlg = new AddMembrProcDataPrep();
		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();

		// assign to wrk spc ownder id

		String wrkSpcOwnrId = getOwnerIdForProject(sProjId);

		ETSUserDetails ownerDets = wrkSpcDao.getUserDetails(conn,wrkSpcOwnrId);

		boolean womultipoc = wrkSpcDao.userHasEntitlement(ownerDets.getWebId(), Defines.MULTIPOC);

        logger.debug("MULTI-POC EXISTS FOR OWNER USER ID ==" + womultipoc);

			//if not multipoc request entitlement for WO and continue the process
					if (!womultipoc) {

						logger.debug(" START  REQ MULTI-POC ENT OWNER USER ID ==" + ownerDets.getWebId());

						actOpModel = addMmbrBdlg.requestMultiPocForWO(ownerDets.getWebId(), sIRUserId );

						logger.debug(" REQ MULTI-POC ENT OWNER :: STATUS ==" + actOpModel.getRetCodeMsg());
					}

		return womultipoc;

	}

	/**
	 *
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public String getIRUserIdFromEmail(String userId) throws SQLException, Exception {

		AddMembrToWrkSpcDAO addWrkSpcDao = new AddMembrToWrkSpcDAO();
		ArrayList idList = addWrkSpcDao.getMultiIDDetails(userId);
		UserObject uo = (UserObject) idList.get(0);
		String mailIRUserId = uo.gIR_USERN;

		return mailIRUserId;
	}


	public static String ExtractInfoFromBluePages(String NotesEmail){

		BPResults results;
		Hashtable row	= null;
		boolean notokens	= false;

		StringBuffer strRslt = new StringBuffer();
		boolean added = false;
        StringTokenizer st = null;
        if(NotesEmail.indexOf(";") != -1){
            st = new StringTokenizer(NotesEmail, ";") ; /* parsing when
                                                    /* the parsing
                                                    * string is a
                                                    * semicolon */
        }
        else if(NotesEmail.indexOf(",") != -1){
            st = new StringTokenizer(NotesEmail, ",");/* parsing when
                                                * the parsing
                                                * string is a
                                                * comma */
        }
        else{
         	NotesEmail	= NotesEmail.trim();
         	notokens 	= true;
        }

		if(notokens == false){
			while( st.hasMoreTokens()){
            	String string = st.nextToken();
	        	string = string.trim();
	        	boolean noconversion = false;
	        	string = correctTheString(string);

	        	logger.debug("String is--->>>"+string);
                		results = BluePages.getPersonsByNotesID(string);
						if (results.rows() == 0) {
							  	BPResults AltResults;
								AltResults	= BluePages.getPersonsByInternet(string);
								if(AltResults.rows()	!= 0){
								//	System.out.println("\nAlready a converted Intranet ID");
									noconversion = true;
								}
								else{
									logger.debug("\nNo IBM Intranet id found for : " + string + " \n Inserting null in ets.ets_opportunity");
    		    		    		continue;
								}
							}
						if(!noconversion){
				      		row = results.getRow(0);
  							if(added == true)
								strRslt.append(", ");
							strRslt.append((String) row.get("INTERNET"));
						}
						else{
							if(added == true)
								strRslt.append(", ");
								strRslt.append(string);

						}
						added = true;
    	       		}
				} else{
						NotesEmail = correctTheString(NotesEmail);
						boolean noconversion	= false;
						results = BluePages.getPersonsByNotesID(NotesEmail);
						if (results.rows() == 0) {
								BPResults AltResults;
								AltResults	= BluePages.getPersonsByInternet(NotesEmail);
								if(AltResults.rows()	!= 0){
						//			System.out.println("\nAlready a converted Intranet ID");
									noconversion	= true;
								}
								else{
									logger.debug("No IBM Intranet id found for : " + NotesEmail);
    			    	    		return null;
								}
							}
						if(!noconversion){
				      		row = results.getRow(0);
	  						strRslt.append((String) row.get("INTERNET"));
  						}
						else{
							if(added == true)
								strRslt.append(", ");
								strRslt.append(NotesEmail);
						}

						added = true;
				}
			return new String(strRslt);
	}


	private static String correctTheString(String str){
		String retstr = null;
		if(str != null){
			StringTokenizer oldst	=	new StringTokenizer(str,"/");
			StringBuffer	newst	=	new StringBuffer("");

			logger.debug("OLD STR == " + oldst);

            int noTokens = oldst.countTokens();

			if(   noTokens < 3 || noTokens > 4)
                  return str;

            String cnStr = (String)oldst.nextElement();
			String ouStr = (String)oldst.nextElement();
			String contrStr = null;

            if(noTokens == 4)
                 contrStr = (String)oldst.nextElement();
                 String oStr = (String)oldst.nextElement();

			if(cnStr.indexOf("=") == -1){
					newst.append("cn=");newst.append(cnStr);
			}else{
					newst.append(cnStr);
			}
			newst.append("/");

			logger.debug("NEW STR == " + newst);

			if(ouStr.indexOf("=") == -1){newst.append("ou=");newst.append(ouStr);}
			else						{newst.append(ouStr);}
			newst.append("/");

            if(contrStr != null){
            	newst.append("ou=");
            	newst.append(contrStr);
            	newst.append("/");
             }


			if(oStr.indexOf( "=") == -1){newst.append("o=");newst.append("IBM");}
			else {newst.append("IBM");}
			retstr =  new String(newst);
		}
             logger.debug(" Corrected String: [ " + retstr + " ]");

       return retstr;

	}

} //end of class
