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

package oem.edge.ets.fe.acmgt.bdlg;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.acmgt.actions.UserProjectsMgrIF;
import oem.edge.ets.fe.acmgt.dao.WrkSpcInfoDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SubWrkSpcProcBdlg {

	private static Log logger = EtsLogger.getLogger(SubWrkSpcProcBdlg.class);
	public static final String VERSION = "1.3";

	/**
	 * 
	 */
	public SubWrkSpcProcBdlg() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param projectId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public ArrayList getActiveMemList(String parentProjId, String projectId) throws SQLException, Exception {

		ArrayList memList = new ArrayList();
		ArrayList curMemList = new ArrayList();
		int vsize = 0;

		String sortby = Defines.SORT_BY_USERNAME_STR;

		String ad = Defines.SORT_ASC_STR;

		Vector members = ETSDatabaseManager.getAllProjMembers(parentProjId, sortby, ad, true, false);

		Vector curMembrs = ETSDatabaseManager.getAllProjMembers(projectId, sortby, ad, true, false);

		//
		memList = getParentList(members);

		//
		curMemList = getCurrentList(curMembrs);

		return transformIntoUserList(memList, curMemList);
	}

	/**
	 * 
	 * @param memList
	 * @return
	 */

	public ArrayList transformIntoUserList(ArrayList memList, ArrayList curMemList) {

		ArrayList userList = new ArrayList();

		int msize = 0;
		String memName = "";
		String memUserId = "";

		if (memList != null && !memList.isEmpty()) {

			msize = memList.size();

			for (int i = 0; i < msize; i++) {

				ETSUser memb = (ETSUser) memList.get(i);
				memName = memb.getUserName();
				memUserId = memb.getUserId();

				//only if 
				if (!curMemList.contains(memUserId)) {

					userList.add(memUserId);
					userList.add(memName + "[ " + memUserId + "]");

				}

			}

		}

		return userList;

	}

	/**
		 * 
		 * @param projectId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public ArrayList getSelectedMemList(String projectId, HttpServletRequest request) throws SQLException, Exception {

		ArrayList selectMemList = new ArrayList();
		int vsize = 0;

		String sortby = Defines.SORT_BY_USERNAME_STR;

		String ad = Defines.SORT_ASC_STR;

		String selecMemValues[] = request.getParameterValues("userlist");

		String userQryStr = AmtCommonUtils.getQryStr(selecMemValues);

		return getSelectedMemList(projectId, userQryStr);

	}

	/**
			 * 
			 * @param projectId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public ArrayList getSelectedMemList(String projectId, String userQryStr) throws SQLException, Exception {

		ArrayList selectMemList = new ArrayList();
		int vsize = 0;

		String sortby = Defines.SORT_BY_USERNAME_STR;

		String ad = Defines.SORT_ASC_STR;

		Vector members = ETSDatabaseManager.getSelectedProjMembers(projectId, sortby, ad, true, true, userQryStr);

		if (members != null && !members.isEmpty()) {

			vsize = members.size();

			for (int i = 0; i < vsize; i++) {

				//get amt information
				ETSUser memb = (ETSUser) members.elementAt(i);

				selectMemList.add(members.get(i));

			}
		}

		return selectMemList;
	}

	/**
					 * This method will load step1 details and check for validations
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */
	public String validateScrn1FormFields(HttpServletRequest request) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//cehck for issue type name

		String selecMemValues[] = request.getParameterValues("userlist");

		if (selecMemValues != null) {

		} else {

			errsb.append("Please select atleast one user from the list.");
			errsb.append("<br />");
		}

		//get from session
		return errsb.toString();

	}

	public ArrayList validateScrn2FormFields(HttpServletRequest request) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		ArrayList userInfoList = new ArrayList();
		int uidcount = 0;
		int rolecount = 0;

		String userId = "";
		int rolesId = 0;
		String strRolesId = "";

		String strUidCount = AmtCommonUtils.getTrimStr(request.getParameter("uidcount"));

		String projectId = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

		String mgrRoleId = WrkSpcInfoDAO.getMgrRolesId(projectId);

		ArrayList mgrRoleList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(strUidCount)) {

			uidcount = Integer.parseInt(strUidCount);
		}

		for (int i = 0; i < uidcount; i++) {

			ETSUser new_user = new ETSUser();

			userId = AmtCommonUtils.getTrimStr(request.getParameter("swuid" + i));
			strRolesId = AmtCommonUtils.getTrimStr(request.getParameter("roles" + i));

			if (AmtCommonUtils.isResourceDefined(strRolesId)) {

				rolesId = Integer.parseInt(strRolesId);
			}

			if (strRolesId.equals(mgrRoleId)) {

				mgrRoleList.add(strRolesId);

			}

			if (!AmtCommonUtils.isResourceDefined(strRolesId)) {

				rolecount++;
			}

		} //end of for

		//		if (mgrRoleList != null && !mgrRoleList.isEmpty()) {
		//
		//			if (mgrRoleList.size() > 1) {
		//
		//				errsb.append("Please assign Workspace Manager privilege for only one userid.");
		//				errsb.append("<br />");
		//
		//			}
		//
		//		}

		if (rolecount > 0) {

			errsb.append("Please assign privilege(s) for all the users and click 'Submit'.");
			errsb.append("<br />");
		}

		ArrayList selInfoList = transformInpIntoEtsUser(request, "");

		userInfoList.add(errsb.toString());
		userInfoList.add(selInfoList);

		//		get from session
		return userInfoList;

	}
	/**
	 * 
	 * @param request
	 * @param lastUserId
	 */

	public ArrayList transformInpIntoEtsUser(HttpServletRequest request, String lastUserId) throws SQLException, Exception {

		ArrayList userInfoList = new ArrayList();
		int uidcount = 0;

		String userId = "";
		int rolesId = 0;
		String strRolesId = "";
		String strRolesName = "";
		String sJobResp = "";
		String userStatus = "A";

		String strUidCount = AmtCommonUtils.getTrimStr(request.getParameter("uidcount"));

		String projectId = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

		if (AmtCommonUtils.isResourceDefined(strUidCount)) {

			uidcount = Integer.parseInt(strUidCount);
		}

		for (int i = 0; i < uidcount; i++) {

			ETSUser new_user = new ETSUser();

			userId = AmtCommonUtils.getTrimStr(request.getParameter("swuid" + i));
			strRolesId = AmtCommonUtils.getTrimStr(request.getParameter("roles" + i));

			if (AmtCommonUtils.isResourceDefined(strRolesId)) {

				rolesId = Integer.parseInt(strRolesId);
			}

			strRolesName = ETSDatabaseManager.getRoleName(rolesId);

			sJobResp = AmtCommonUtils.getTrimStr(request.getParameter("jobresp" + i));

			new_user.setUserId(userId);
			new_user.setRoleId(rolesId);
			new_user.setProjectId(projectId);
			new_user.setUserJob(sJobResp);
			new_user.setPrimaryContact(Defines.NO);
			new_user.setLastUserId(lastUserId);
			new_user.setActiveFlag(userStatus);
			new_user.setRoleName(strRolesName);

			userInfoList.add(new_user);

		} //end of for

		return userInfoList;

	} //end of transform

	/**
		 * 
		 * @param request
		 * @param lastUserId
		 */

	public boolean submitUserInfoToDB(ArrayList userList) throws SQLException, Exception {

		int count = 0;

		int succount = 0;

		if (userList != null && !userList.isEmpty()) {

			count = userList.size();
		}

		for (int i = 0; i < count; i++) {

			ETSUser new_user = (ETSUser) userList.get(i);

			String[] res = WrkSpcInfoDAO.addProjectMemberWithStatus(new_user);

			String success = AmtCommonUtils.getTrimStr(res[0]);
			
			//in case of itar workspaces, request itar entitlement also
			requestITAREntitlement(new_user);

			if (success.equals("0")) {

				succount++;
			}

		} //end of for

		System.out.println("succount==" + succount);
		System.out.println("count==" + count);

		if (succount == count) {

			return true;
		}

		return false;
	} //end of submit

	public boolean submitSubWrkSpcUsers(HttpServletRequest request, String lastUserId) throws SQLException, Exception {

		return submitUserInfoToDB(transformInpIntoEtsUser(request, lastUserId));

	}

	/**
	 * 
	 * @param members
	 * @return
	 */

	public ArrayList getParentList(Vector members) {

		int vsize = 0;
		ArrayList memList = new ArrayList();

		//		for parent proj members
		if (members != null && !members.isEmpty()) {

			vsize = members.size();

			for (int i = 0; i < vsize; i++) {

				//get amt information
				ETSUser memb = (ETSUser) members.elementAt(i);

				if (AmtCommonUtils.getTrimStr(memb.getActiveFlag()).equals("A")) {

					memList.add(members.get(i));

				}
			}
		}

		return memList;
	}

	/**
		 * 
		 * @param members
		 * @return
		 */

	public ArrayList getCurrentList(Vector curMembers) {

		int vsize = 0;
		ArrayList memList = new ArrayList();

		//			for parent proj members
		if (curMembers != null && !curMembers.isEmpty()) {

			vsize = curMembers.size();

			for (int i = 0; i < vsize; i++) {

				//get amt information
				ETSUser memb = (ETSUser) curMembers.elementAt(i);

				if (AmtCommonUtils.getTrimStr(memb.getActiveFlag()).equals("A") || AmtCommonUtils.getTrimStr(memb.getActiveFlag()).equals("P")) {

					memList.add(curMembers.get(i));

				}
			}
		}

		//

		ArrayList userList = new ArrayList();

		int msize = 0;
		String memName = "";
		String memUserId = "";

		if (memList != null && !memList.isEmpty()) {

			msize = memList.size();

			for (int i = 0; i < msize; i++) {

				ETSUser memb = (ETSUser) memList.get(i);
				memName = memb.getUserName();
				memUserId = memb.getUserId();

				userList.add(memUserId);

			}

		}

		return userList;
	}

	public ArrayList getMemDetsFromSelectdUserList(String parentProjId, ArrayList userList) throws SQLException, Exception {

		ArrayList userInfoList = new ArrayList();

		ArrayList tempList = new ArrayList();

		String projectId = "";

		int count = 0;

		if (userList != null && !userList.isEmpty()) {

			count = userList.size();
		}

		for (int i = 0; i < count; i++) {

			ETSUser new_user = (ETSUser) userList.get(i);

			tempList.add(new_user.getUserId());

		}

		String userQryStr = AmtCommonUtils.getQryStr(tempList);

		return getSelectedMemList(parentProjId, userQryStr);

	}

	/**
		 * 
		 * @param request
		 * @param lastUserId
		 */

	public ArrayList getPrevSelectdUserList(HttpServletRequest request) {

		ArrayList userInfoList = new ArrayList();
		int uidcount = 0;

		String userId = "";

		String strUidCount = AmtCommonUtils.getTrimStr(request.getParameter("uidcount"));

		String projectId = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

		if (AmtCommonUtils.isResourceDefined(strUidCount)) {

			uidcount = Integer.parseInt(strUidCount);
		}

		for (int i = 0; i < uidcount; i++) {

			ETSUser new_user = new ETSUser();

			userId = AmtCommonUtils.getTrimStr(request.getParameter("swuid" + i));

			userInfoList.add(userId);

		} //end of for

		return userInfoList;

	} //end of transform

	public boolean isWrkSpcMgrDefnd(String projectId) throws SQLException, Exception {

		return WrkSpcInfoDAO.isWrkSpcMgrDefnd(projectId);

	}

	/**
	 * 
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */

	public boolean requestITAREntitlement(ETSUser etsUser) throws SQLException, Exception {

		//get projectid
		String projectId = etsUser.getProjectId();
		
		//get the userid whos is requesting
		String reqsIrUserId=etsUser.getLastUserId();

		//for whom it is requested
		String irUserId = etsUser.getUserId();

		//get the proj info
		ETSProj wrkSpc = ETSDatabaseManager.getProjectDetails(projectId);
		
		//if ITAR

		if (wrkSpc.isITAR()) {

			AddMembrProcDataPrep addDataPrep = new AddMembrProcDataPrep();

			boolean bEntitled = addDataPrep.isUserHasReqdEntForWrkSpc(irUserId, wrkSpc);

			boolean bHasPendEtitlement = addDataPrep.isUserHasPendEntForWrkSpc(irUserId, wrkSpc);

			String reqstdProject = WrkSpcTeamUtils.getWrkSpcReqProject(wrkSpc);

			logger.debug("reqstdProject in isUserHasReqdEntForWrkSpc ==" + reqstdProject);

			Vector entProj = new Vector();
			entProj.add(reqstdProject);

			UserProjectsMgrIF projReqMgrIF = new UserEntReqUtilsImpl();

			//not having entitlement && not pending request itar project

			if (!bEntitled && !bHasPendEtitlement) {

				boolean projReq = projReqMgrIF.requestProjectToUser(irUserId, reqsIrUserId, entProj);

			}

		} //only in case of ITAR projects, request itar entitlement

		return true;
	}

} //end of class
