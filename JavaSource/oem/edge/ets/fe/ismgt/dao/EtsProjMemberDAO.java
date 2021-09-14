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

package oem.edge.ets.fe.ismgt.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.UserObject;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsProjMemberDAO implements EtsIssFilterConstants {
	public static final String VERSION = "1.44";

	private static Log logger = EtsLogger.getLogger(EtsProjMemberDAO.class);

	/**
	 * 
	 */
	public EtsProjMemberDAO() {
		super();

	}

	/**
		 * This method will give list of ETS users in a given project
		 * i.e the List of Users of ETS Project, who have submitted issues
		 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
		 * ets.ets_users >> userid === ir_userid in amt.users
		 * inlcude the ETS_ADMIN name in the list alsoe
		 */

	public ArrayList getProjMemberList(String projectId,String adminId) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";

		sb.append("select rtrim(a.user_fullname)||' '||'['||rtrim(a.ir_userid)||']' as etsusernamewithirid, ");
		sb.append(" rtrim(a.user_fullname) as etsusername, ");
		sb.append(" rtrim(a.edge_userid) as etsuseredgeid ");
		sb.append(" from  amt.users a, ets.ets_users  b");
		sb.append(" where");
		sb.append(" a.ir_userid = b.user_id");
		sb.append(" and b.user_project_id='" + projectId + "' ");
		sb.append(" and b.active_flag='" + ETSACTIVEUSERFLAG + "' "); //show only active users
		sb.append(" and b.user_role_id NOT IN (select distinct role_id from ets.ets_roles where priv_id=9 and priv_value=1 and project_id='" + projectId + "') "); //donot show visitors
		sb.append(" UNION"); //inlcude the list of super-admins also
		sb.append(" select rtrim(a.user_fullname)||' '||'['||rtrim(a.ir_userid)||']' as etsusernamewithirid, ");
		sb.append(" rtrim(a.user_fullname) as etsusername, ");
		sb.append(" rtrim(a.edge_userid) as etsuseredgeid ");
		sb.append(" from  amt.users a, amt.s_user_access_view  b");
		sb.append(" where");
		sb.append(" a.edge_userid = b.userid");
		sb.append(" and b.entitlement='" + adminId + "' ");
		sb.append(" order by 2,3");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getProjMemberList qry ", "getProjMemberList qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			submitterList.add("All");
			submitterList.add("All");

			if (rs != null) {
				while (rs.next()) {

					etsUserNameWithIrId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAMEWITHIRID"));
					etsUserName = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAME"));
					etsUserEdgeId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREDGEID"));

					//get project mem object
					/*EtsIssProjectMember etsProjMem = new EtsIssProjectMember();
					etsProjMem.setOwnerIrId(etsUserIrId);
					etsProjMem.setOwnerNameWithIrId(etsUserNameWithIrId);
					etsProjMem.setOwnerName(etsUserName);
					submitterList.add(etsProjMem);*/

					submitterList.add(etsUserEdgeId);
					submitterList.add(etsUserNameWithIrId);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
		 * This method will give list of ETS users in a given project
		 * i.e the List of Users of ETS Project, who have submitted issues
		 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
		 * ets.ets_users >> userid === ir_userid in amt.users
		 */

	public ArrayList getProjMemberListWithUserType(String projectId) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";
		String etsUserEmail = "";

		sb.append("select rtrim(a.user_fullname)||' '||'['||rtrim(a.ir_userid)||']' as etsusernamewithirid, ");
		sb.append(" rtrim(a.user_fullname) as etsusername, ");
		sb.append(" rtrim(a.edge_userid) as etsuseredgeid, ");
		sb.append(" rtrim(a.user_email) as etsuseremail, ");
		sb.append(" rtrim(c.user_type) as etsusertype ");
		sb.append(" from  amt.users a, ets.ets_users  b,decaf.users c ");
		sb.append(" where");
		sb.append(" a.ir_userid = b.user_id");
		sb.append(" and b.user_project_id='" + projectId + "' ");
		sb.append(" and b.active_flag='" + ETSACTIVEUSERFLAG + "' "); //show only active users
		sb.append(" and a.edge_userid=c.userid");
		sb.append(" order by 2,3");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getProjMemberListWithUserType qry ", "getProjMemberListWithUserType qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			//submitterList.add("All");
			//submitterList.add("All");

			if (rs != null) {
				while (rs.next()) {

					etsUserNameWithIrId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAMEWITHIRID"));
					etsUserName = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAME"));
					etsUserEdgeId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREDGEID"));
					etsUserType = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERTYPE"));
					etsUserEmail = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREMAIL"));

					//get project mem object
					/*EtsIssProjectMember etsProjMem = new EtsIssProjectMember();
					etsProjMem.setOwnerIrId(etsUserIrId);
					etsProjMem.setOwnerNameWithIrId(etsUserNameWithIrId);
					etsProjMem.setOwnerName(etsUserName);
					submitterList.add(etsProjMem);*/

					submitterList.add(etsUserEdgeId);
					submitterList.add(etsUserNameWithIrId);
					submitterList.add(etsUserType);
					submitterList.add(etsUserEmail);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
				 * This method will give list of ETS users in a given project
				 * i.e the List of Users of ETS Project, who have submitted issues
				 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
				 * ets.ets_users >> userid === ir_userid in amt.users
				 */

	public ArrayList getProjMemberListFromEdgeId(ArrayList projMemEdgeIdList) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserNameWithIrId = "";
		String qryStr = "' '";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemEdgeIdList)) {

			qryStr = AmtCommonUtils.getQryStr(projMemEdgeIdList);
		}

		sb.append("select rtrim(a.user_fullname)||' '||'['||rtrim(a.ir_userid)||']' as etsusernamewithirid ");
		sb.append(" from  amt.users a ");
		sb.append(" where");
		sb.append(" a.edge_userid");
		sb.append(" IN ");
		sb.append(" ( ");
		sb.append(qryStr);
		sb.append("  )");
		sb.append(" order by 1");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getProjMemberListFromEdgeId qry ", "getProjMemberListFromEdgeId qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					etsUserNameWithIrId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAMEWITHIRID"));

					submitterList.add(etsUserNameWithIrId);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
					 * This method will give list of ETS users in a given project
					 * i.e the List of Users of ETS Project, who have submitted issues
					 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
					 * ets.ets_users >> userid === ir_userid in amt.users
					 */

	public ArrayList getEmailListFromEdgeId(ArrayList projMemEdgeIdList) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserEmail = "";
		String qryStr = "' '";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemEdgeIdList)) {

			qryStr = AmtCommonUtils.getQryStr(projMemEdgeIdList);
		}

		sb.append("select distinct rtrim(a.user_email) as etsuseremail ");
		sb.append(" from  amt.users a ");
		sb.append(" where");
		sb.append(" a.edge_userid");
		sb.append(" IN ");
		sb.append(" ( ");
		sb.append(qryStr);
		sb.append("  )");
		sb.append(" order by 1");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getEmailListFromEdgeId qry ", "getEmailListFromEdgeId qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					etsUserEmail = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREMAIL"));

					submitterList.add(etsUserEmail);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
					 * This method will give list of ETS users in a given project
					 * i.e the List of Users of ETS Project, who have submitted issues
					 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
					 * ets.ets_users >> userid === ir_userid in amt.users
					 */

	public ArrayList getProjMemberListFromEmailId(ArrayList projMemEmailIdList, String projId) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserNameWithEmail = "";
		String userEmail = "";
		String edgeUserId = "";
		String qryStr = "' '";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemEmailIdList)) {

			qryStr = AmtCommonUtils.getQryStr(projMemEmailIdList);
		}

		sb.append("select rtrim(a.user_fullname)||' '||'['||rtrim(a.user_email)||']' as etsusernamewithemail, ");
		sb.append(" rtrim(a.user_email) as useremail ");
		sb.append(" from  amt.users a , ets.ets_users b ");
		sb.append(" where");
		sb.append(" a.user_email");
		sb.append(" IN ");
		sb.append(" ( ");
		sb.append(qryStr);
		sb.append("  )");
		sb.append(" and b.user_id=a.ir_userid ");
		sb.append(" and b.user_project_id = '" + projId + "' ");
		sb.append(" and b.active_flag='" + ETSACTIVEUSERFLAG + "' "); //show only active users
		sb.append(" group by a.user_email,a.user_fullname ");
		sb.append(" order by 2,1 ");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getProjMemberListFromEmailId qry ", "getProjMemberListFromEmailId qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					etsUserNameWithEmail = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAMEWITHEMAIL"));
					userEmail = AmtCommonUtils.getTrimStr(rs.getString("USEREMAIL"));

					if (!submitterList.contains(userEmail)) {

						submitterList.add(userEmail);
						submitterList.add(etsUserNameWithEmail);

					}

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
					 * This method will give list of ETS users in a given project
					 * i.e the List of Users of ETS Project, who have submitted issues
					 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
					 * ets.ets_users >> userid === ir_userid in amt.users
					 */

	public ArrayList getProjMemberListNamesFromEdgeId(ArrayList projMemEdgeIdList) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserName = "";
		String qryStr = "' '";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemEdgeIdList)) {

			qryStr = AmtCommonUtils.getQryStr(projMemEdgeIdList);
		}

		sb.append("select rtrim(a.user_fullname) as etsusername ");
		sb.append(" from  amt.users a ");
		sb.append(" where");
		sb.append(" a.edge_userid");
		sb.append(" IN ");
		sb.append(" ( ");
		sb.append(qryStr);
		sb.append("  )");
		sb.append(" order by 1");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getProjMemberListNamesFromEdgeId qry ", "getProjMemberListNamesFromEdgeId qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					etsUserName = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAME"));

					submitterList.add(etsUserName);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
						 * This method will give list of ETS users in a given project
						 * i.e the List of Users of ETS Project, who have submitted issues
						 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
						 * ets.ets_users >> userid === ir_userid in amt.users
						 */

	public ArrayList getUserIdInfoList(ArrayList projMemEdgeIdList) throws SQLException, Exception {

		ArrayList userInfoList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String irUserId = "";
		String edgeUserId = "";
		String userFullName = "";
		String userEmail = "";
		String userType = "";

		String qryStr = "' '";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemEdgeIdList)) {

			qryStr = AmtCommonUtils.getQryStr(projMemEdgeIdList);
		}

		sb.append("select ");
		sb.append(" rtrim(a.ir_userid) as iruserid, ");
		sb.append(" rtrim(a.edge_userid) as edgeuserid, ");
		sb.append(" rtrim(a.user_fullname) as userfullname, ");
		sb.append(" rtrim(a.user_email) as useremail ");
		sb.append(" from  amt.users a ");
		sb.append(" where");
		sb.append(" a.edge_userid");
		sb.append(" IN ");
		sb.append(" ( ");
		sb.append(qryStr);
		sb.append("  )");
		sb.append(" order by 1");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getEmailListFromEdgeId qry ", "getEmailListFromEdgeId qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					irUserId = AmtCommonUtils.getTrimStr(rs.getString("IRUSERID"));
					edgeUserId = AmtCommonUtils.getTrimStr(rs.getString("EDGEUSERID"));
					userFullName = AmtCommonUtils.getTrimStr(rs.getString("USERFULLNAME"));
					userEmail = AmtCommonUtils.getTrimStr(rs.getString("USEREMAIL"));

					EtsIssOwnerInfo ownerInfo = new EtsIssOwnerInfo();
					ownerInfo.setUserIrId(irUserId);
					ownerInfo.setUserEdgeId(edgeUserId);
					ownerInfo.setUserFullName(userFullName);
					ownerInfo.setUserEmail(userEmail);

					userInfoList.add(ownerInfo);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return userInfoList;
	}

	/**
			 * This method will give list of ETS users having WORKSPACE OWNER OR WORKSPACE MANAGER ROLES in a given project
			 * takes a join btwn ets.ets_users/ets.ets_priv/ets.ets_roles/amt.users (change to decaf.users if performace is an issue)
			 * ets.ets_users >> userid === ir_userid in amt.users
			 */

	public ArrayList getWrkSpcOwnerMgrListForProject(String projectId) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";
		String etsUserEmail = "";

		sb.append("select rtrim(a.user_fullname)||' '||'['||rtrim(a.ir_userid)||']' as etsusernamewithirid, ");
		sb.append(" rtrim(a.user_fullname) as etsusername, ");
		sb.append(" rtrim(a.edge_userid) as etsuseredgeid, ");
		sb.append(" rtrim(a.user_email) as etsuseremail, ");
		sb.append(" rtrim(c.user_type) as etsusertype ");
		sb.append(" from  amt.users a, ets.ets_users  b,decaf.users c ");
		sb.append(" where");
		sb.append(" a.ir_userid = b.user_id");
		sb.append(" and b.user_project_id='" + projectId + "' ");
		sb.append(" and b.active_flag='" + ETSACTIVEUSERFLAG + "' "); //show only active users
		sb.append(" and a.edge_userid=c.userid");
		sb.append(" and b.user_role_id");
		sb.append("       in");
		sb.append("        (select distinct role_id from ets.ets_roles where priv_id in (5,8) and priv_value=1 and project_id='" + projectId + "')");
		sb.append(" order by 2,3");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getWrkSpcOwnerMgrListForProject qry ", "getWrkSpcOwnerMgrListForProject qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			//submitterList.add("All");
			//submitterList.add("All");

			if (rs != null) {
				while (rs.next()) {

					etsUserNameWithIrId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAMEWITHIRID"));
					etsUserName = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAME"));
					etsUserEdgeId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREDGEID"));
					etsUserType = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERTYPE"));
					etsUserEmail = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREMAIL"));

					//get project mem object
					/*EtsIssProjectMember etsProjMem = new EtsIssProjectMember();
					etsProjMem.setOwnerIrId(etsUserIrId);
					etsProjMem.setOwnerNameWithIrId(etsUserNameWithIrId);
					etsProjMem.setOwnerName(etsUserName);
					submitterList.add(etsProjMem);*/

					submitterList.add(etsUserEdgeId);
					submitterList.add(etsUserNameWithIrId);
					submitterList.add(etsUserType);
					submitterList.add(etsUserEmail);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
				 * This method will give list of ETS users having CLIENT ROLES in a given project
				 * takes a join btwn ets.ets_users/ets.ets_priv/ets.ets_roles/amt.users (change to decaf.users if performace is an issue)
				 * ets.ets_users >> userid === ir_userid in amt.users
				 */

	public ArrayList getClientRoleExtListForProject(String projectId, boolean isProjBladeType) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";
		String etsUserEmail = "";

		sb.append("select rtrim(a.user_fullname)||' '||'['||rtrim(a.ir_userid)||']' as etsusernamewithirid, ");
		sb.append(" rtrim(a.user_fullname) as etsusername, ");
		sb.append(" rtrim(a.edge_userid) as etsuseredgeid, ");
		sb.append(" rtrim(a.user_email) as etsuseremail, ");
		sb.append(" rtrim(c.user_type) as etsusertype ");
		sb.append(" from  amt.users a, ets.ets_users  b,decaf.users c ");
		sb.append(" where");
		sb.append(" a.ir_userid = b.user_id");
		sb.append(" and b.user_project_id='" + projectId + "' ");
		sb.append(" and b.active_flag='" + ETSACTIVEUSERFLAG + "' "); //show only active users
		sb.append(" and a.edge_userid=c.userid");
		sb.append(" and c.user_type='E'");

		///if project is not blade type, then only consider visitor role
		if (!isProjBladeType) {

			sb.append(" and b.user_role_id NOT IN (select distinct role_id from ets.ets_roles where priv_id=9 and priv_value=1 and project_id='" + projectId + "') ");

		}

		sb.append(" order by 2,3");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getClientRoleExtListForProject qry ", "getClientRoleExtListForProject qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			//submitterList.add("All");
			//submitterList.add("All");

			if (rs != null) {
				while (rs.next()) {

					etsUserNameWithIrId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAMEWITHIRID"));
					etsUserName = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAME"));
					etsUserEdgeId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREDGEID"));
					etsUserType = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERTYPE"));
					etsUserEmail = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREMAIL"));

					//get project mem object
					/*EtsIssProjectMember etsProjMem = new EtsIssProjectMember();
					etsProjMem.setOwnerIrId(etsUserIrId);
					etsProjMem.setOwnerNameWithIrId(etsUserNameWithIrId);
					etsProjMem.setOwnerName(etsUserName);
					submitterList.add(etsProjMem);*/

					submitterList.add(etsUserEdgeId);
					submitterList.add(etsUserNameWithIrId);
					submitterList.add(etsUserType);
					submitterList.add(etsUserEmail);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
	 *	to get user object from amt.users				
	 */

	public UserObject getUserObject(String edgeUserId) throws SQLException, Exception {

		Connection conn = null;

		UserObject userObj = new UserObject();

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			userObj = getUserObject(conn, edgeUserId);

		} finally {

			ETSDBUtils.close(conn);

		}

		return userObj;
	}
	
	/**
		 *	to get user object from amt.users				
		 */

		public UserObject getUserObject(Connection conn, String edgeUserId) throws SQLException, Exception {
		

			UserObject userObj = AccessCntrlFuncs.getUserObject(conn, edgeUserId, false, false);
		

			return userObj;
		}

	/**
		 *	to get user object from amt.users				
		 */

	public UserObject getUserObjectIrId(String irUserId) throws SQLException, Exception {

		Connection conn = null;

		UserObject userObj = new UserObject();

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			userObj = getUserObjectIrId(conn, irUserId);

		} finally {

			ETSDBUtils.close(conn);

		}

		return userObj;
	}
	
	/**
			 *	to get user object from amt.users				
			 */

		public UserObject getUserObjectIrId(Connection conn, String irUserId) throws SQLException, Exception {
							
			UserObject userObj = AccessCntrlFuncs.getUserObject(conn, irUserId, true, false);

			return userObj;
		}

	/**
		 *	to get user object from amt.users				
		 */

	public String getAssocCompany(String edgeUserId) throws SQLException, Exception {

		Connection conn = null;

		String assocComp = "";

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			assocComp = AccessCntrlFuncs.assocComp(edgeUserId, conn);

		} finally {

			ETSDBUtils.close(conn);

		}

		return assocComp;
	}

	/**
			 *	to get user object from amt.users				
			 */

	public String getDecafUserType(String edgeUserId) throws SQLException, Exception {

		Connection conn = null;

		String userType = "";

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			userType = AccessCntrlFuncs.decafType(edgeUserId, conn);

		} finally {

			ETSDBUtils.close(conn);

		}

		return userType;
	}

	/**
			 * This method will give list of ETS users in a given project
			 * i.e the List of Users of ETS Project, who have submitted issues
			 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
			 * ets.ets_users >> userid === ir_userid in amt.users
			 */

	public ArrayList getProjMemberListWithUserTypeWthoutVisitors(String projectId, boolean isProjBladeType) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";
		String etsUserEmail = "";

		sb.append("select rtrim(a.user_fullname)||' '||'['||rtrim(a.ir_userid)||']' as etsusernamewithirid, ");
		sb.append(" rtrim(a.user_fullname) as etsusername, ");
		sb.append(" rtrim(a.edge_userid) as etsuseredgeid, ");
		sb.append(" rtrim(a.user_email) as etsuseremail, ");
		sb.append(" rtrim(c.user_type) as etsusertype ");
		sb.append(" from  amt.users a, ets.ets_users  b,decaf.users c ");
		sb.append(" where");
		sb.append(" a.ir_userid = b.user_id");
		sb.append(" and b.user_project_id='" + projectId + "' ");
		sb.append(" and b.active_flag='" + ETSACTIVEUSERFLAG + "' "); //show only active users
		sb.append(" and a.edge_userid=c.userid");
		//			/if project is not blade type, then only consider visitor role
		if (!isProjBladeType) {

			sb.append(" and b.user_role_id NOT IN (select distinct role_id from ets.ets_roles where priv_id=9 and priv_value=1 and project_id='" + projectId + "') ");

		}
		sb.append(" order by 2,3");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getProjMemberListWithUserType qry ", "getProjMemberListWithUserType qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			//submitterList.add("All");
			//submitterList.add("All");

			if (rs != null) {
				while (rs.next()) {

					etsUserNameWithIrId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAMEWITHIRID"));
					etsUserName = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAME"));
					etsUserEdgeId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREDGEID"));
					etsUserType = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERTYPE"));
					etsUserEmail = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREMAIL"));

					//get project mem object
					/*EtsIssProjectMember etsProjMem = new EtsIssProjectMember();
					etsProjMem.setOwnerIrId(etsUserIrId);
					etsProjMem.setOwnerNameWithIrId(etsUserNameWithIrId);
					etsProjMem.setOwnerName(etsUserName);
					submitterList.add(etsProjMem);*/

					submitterList.add(etsUserEdgeId);
					submitterList.add(etsUserNameWithIrId);
					submitterList.add(etsUserType);
					submitterList.add(etsUserEmail);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
						 * This method will give list of ETS users in a given project
						 * i.e the List of Users of ETS Project, who have submitted issues
						 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
						 * ets.ets_users >> userid === ir_userid in amt.users
						 */

	public ArrayList getEmailListFromEdgeIdWithAccess(ArrayList projMemEdgeIdList, String issueAccess) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserEmail = "";
		String qryStr = "' '";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemEdgeIdList)) {

			qryStr = AmtCommonUtils.getQryStr(projMemEdgeIdList);
		}

		if (issueAccess.equals("IBM")) {

			sb.append("select distinct rtrim(a.user_email) as etsuseremail ");
			sb.append(" from  amt.users a,decaf.users b ");
			sb.append(" where");
			sb.append(" a.edge_userid");
			sb.append(" IN ");
			sb.append(" ( ");
			sb.append(qryStr);
			sb.append("  )");
			sb.append(" and a.edge_userid=b.userid");
			sb.append(" and b.user_type='I' ");
			sb.append(" order by 1");
			sb.append(" with ur");

		} else {

			sb.append("select distinct rtrim(a.user_email) as etsuseremail ");
			sb.append(" from  amt.users a ");
			sb.append(" where");
			sb.append(" a.edge_userid");
			sb.append(" IN ");
			sb.append(" ( ");
			sb.append(qryStr);
			sb.append("  )");
			sb.append(" order by 1");
			sb.append(" with ur");

		}

		SysLog.log(SysLog.DEBUG, "getEmailListFromEdgeId qry ", "getEmailListFromEdgeId qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					etsUserEmail = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREMAIL"));

					submitterList.add(etsUserEmail);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
				 * This method will give list of ETS users having WORKSPACE OWNER ROLES in a given project
				 * takes a join btwn ets.ets_users/ets.ets_priv/ets.ets_roles/amt.users (change to decaf.users if performace is an issue)
				 * ets.ets_users >> userid === ir_userid in amt.users
				 */

	public EtsIssProjectMember getWrkSpcOwnerDetsForProject(Connection conn, String projectId) throws SQLException, Exception {

		EtsIssProjectMember etsProjMem = new EtsIssProjectMember();
		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		ResultSet rs = null;

		String etsUserIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";
		String etsUserEmail = "";

		sb.append("select ");
		sb.append(" rtrim(a.ir_userid) as etsuseririd, ");
		sb.append(" rtrim(a.user_fullname) as etsusername, ");
		sb.append(" rtrim(a.edge_userid) as etsuseredgeid, ");
		sb.append(" rtrim(a.user_email) as etsuseremail, ");
		sb.append(" rtrim(c.user_type) as etsusertype ");
		sb.append(" from  amt.users a, ets.ets_users  b, decaf.users c ");
		sb.append(" where");
		sb.append(" a.ir_userid = b.user_id");
		sb.append(" and b.user_project_id='" + projectId + "' ");
		sb.append(" and b.active_flag='" + ETSACTIVEUSERFLAG + "' "); //show only active users
		sb.append(" and a.edge_userid=c.userid");
		sb.append(" and b.user_role_id");
		sb.append("       in");
		sb.append("        (select distinct role_id from ets.ets_roles where priv_id = 8 and priv_value=1 and project_id='" + projectId + "')");
		sb.append(" order by 2,3");
		sb.append(" with ur");

		logger.debug("getWrkSpcOwnerForProject qry =" + sb.toString() + ":");

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					etsUserIrId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERIRID"));
					etsUserName = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAME"));
					etsUserEdgeId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREDGEID"));
					etsUserType = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERTYPE"));
					etsUserEmail = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREMAIL"));

					//get project mem object

					etsProjMem.setUserIrId(etsUserIrId);
					etsProjMem.setUserEdgeId(etsUserEdgeId);
					etsProjMem.setUserFullName(etsUserName);
					etsProjMem.setUserType(etsUserType);
					etsProjMem.setUserEmail(etsUserEmail);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return etsProjMem;
	}

	/**
				 * This method will give list of ETS users in a given project
				 * i.e the List of Users of ETS Project, who have submitted issues
				 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
				 * ets.ets_users >> userid === ir_userid in amt.users
				 */

	public ArrayList getProjMemberListWithIrId(String projectId, boolean isProjBladeType) throws SQLException, Exception {

		ArrayList submitterList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";
		String etsUserEmail = "";
		String etsUserIrId = "";

		sb.append("select rtrim(a.user_fullname)||' '||'['||rtrim(a.ir_userid)||']' as etsusernamewithirid, ");
		sb.append(" rtrim(a.ir_userid) as etsuseririd, ");
		sb.append(" rtrim(a.user_fullname) as etsusername, ");
		sb.append(" rtrim(a.edge_userid) as etsuseredgeid, ");
		sb.append(" rtrim(a.user_email) as etsuseremail, ");
		sb.append(" rtrim(c.user_type) as etsusertype ");
		sb.append(" from  amt.users a, ets.ets_users  b,decaf.users c ");
		sb.append(" where");
		sb.append(" a.ir_userid = b.user_id");
		sb.append(" and b.user_project_id='" + projectId + "' ");
		sb.append(" and b.active_flag='" + ETSACTIVEUSERFLAG + "' "); //show only active users
		sb.append(" and a.edge_userid=c.userid");
		//			/if project is not blade type, then only consider visitor role
		if (!isProjBladeType) {

			sb.append(" and b.user_role_id NOT IN (select distinct role_id from ets.ets_roles where priv_id=9 and priv_value=1 and project_id='" + projectId + "') ");

		}
		sb.append(" order by 2,3");
		sb.append(" with ur");

		SysLog.log(SysLog.DEBUG, "getProjMemberListWithUserType qry ", "getProjMemberListWithUserType qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			//submitterList.add("All");
			//submitterList.add("All");

			if (rs != null) {
				while (rs.next()) {

					etsUserNameWithIrId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAMEWITHIRID"));
					etsUserName = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERNAME"));
					etsUserEdgeId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREDGEID"));
					etsUserType = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERTYPE"));
					etsUserEmail = AmtCommonUtils.getTrimStr(rs.getString("ETSUSEREMAIL"));
					etsUserIrId = AmtCommonUtils.getTrimStr(rs.getString("ETSUSERIRID"));

					//get project mem object
					/*EtsIssProjectMember etsProjMem = new EtsIssProjectMember();
					etsProjMem.setOwnerIrId(etsUserIrId);
					etsProjMem.setOwnerNameWithIrId(etsUserNameWithIrId);
					etsProjMem.setOwnerName(etsUserName);
					submitterList.add(etsProjMem);*/

					submitterList.add(etsUserEdgeId);
					submitterList.add(etsUserNameWithIrId);
					submitterList.add(etsUserType);
					submitterList.add(etsUserEmail);
					submitterList.add(etsUserIrId);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return submitterList;
	}

	/**
			 *	to get user object from amt.users				
			 */

	public boolean isUserPrimaryContact(String irUserId, String projectId) throws SQLException, Exception {

		Connection conn = null;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			flag = isUserPrimaryContact(conn, irUserId, projectId);

		} finally {

			ETSDBUtils.close(conn);

		}

		return flag;
	}

	/**
				 *	to get user object from amt.users				
				 */

	public boolean isUserPrimaryContact(Connection conn, String irUserId, String projectId) throws SQLException, Exception {

		int count = 0;
		String primContact = "";
		boolean flag = false;

		primContact = AmtCommonUtils.getTrimStr(AmtCommonUtils.getValue(conn, "select primary_contact from ets.ets_users where user_id='" + irUserId + "' and user_project_id='" + projectId + "' with ur"));

		if (primContact.equals("Y"))
			flag = true;

		return flag;

	}
	
	/**
					 *	to get user object from amt.users				
					 */

		public String getProjPrimaryContact(Connection conn, String irUserId, String projectId) throws SQLException, Exception {
		

			String primContact = AmtCommonUtils.getTrimStr(AmtCommonUtils.getValue(conn, "select primary_contact from ets.ets_users where user_id='" + irUserId + "' and user_project_id='" + projectId + "' with ur"));

			
			return primContact;

		}
		
		
	/**
				 *	to get user object from amt.users				
				 */

		public String  getProjPrimaryContact(String irUserId, String projectId) throws SQLException, Exception {

			Connection conn = null;
			String primContact ="";

			try {

				conn = ETSDBUtils.getConnection(ETSDATASRC);

				primContact = getProjPrimaryContact(conn, irUserId, projectId);

			} finally {

				ETSDBUtils.close(conn);

			}

			return primContact;
		}

	public boolean updatePrimaryContact(String newUserId, String projectId) {

		Connection conn = null;
		boolean flag = false;
		StringBuffer sb = new StringBuffer();
		Statement stmt = null;
		int count = 0;

		try {

			sb.append("update ets.ets_users");
			sb.append(" set");
			sb.append(" primary_contact='Y' ");
			sb.append(" where");
			sb.append(" user_id='" + newUserId + "' ");
			sb.append(" and user_project_id='" + projectId + "'");

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			count += stmt.executeUpdate(sb.toString());

		} catch (SQLException sqlEx) {

			logger.error("SQL Exception in update primary cont", sqlEx);
			sqlEx.printStackTrace();
			
		} catch (Exception Ex) {

			logger.error("Exception in update primary cont", Ex);
			Ex.printStackTrace();
			
		} finally {

			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		if (count > 0) {

			flag = true;
		}

		return flag;

	}

} //end of class
