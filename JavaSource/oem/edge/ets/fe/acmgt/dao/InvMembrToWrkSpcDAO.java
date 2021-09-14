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
package oem.edge.ets.fe.acmgt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.UserInviteStatusModel;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InvMembrToWrkSpcDAO {

	private static Log logger = EtsLogger.getLogger(InvMembrToWrkSpcDAO.class);
	public static final String VERSION = "1.10";

	/**
	 * 
	 */
	public InvMembrToWrkSpcDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized boolean addMemberToInviteStatus(UserInviteStatusModel invStatModel) throws SQLException, Exception {

		boolean flg = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		int rowCount = 0;

		try {

			conn = WrkSpcTeamUtils.getConnection();

			sb.append("INSERT INTO ETS.ETS_INVITE_STATUS (INVITE_ID, PROJECT_ID, STATUS, ROLE_ID, REQUESTOR_ID, COMPANY, COUNTRY_CODE,LAST_USERID, LAST_TIMESTAMP) ");

			sb.append(" VALUES (?, ?, ?, ?,?,?, ?, ?, current timestamp)");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, invStatModel.getUserId());
			pstmt.setString(2, invStatModel.getWrkSpcId());
			pstmt.setString(3, invStatModel.getInviteStatus());
			pstmt.setInt(4, invStatModel.getRoleId());
			pstmt.setString(5, invStatModel.getRequestorId());
			pstmt.setString(6, invStatModel.getUserCompany());
			pstmt.setString(7, invStatModel.getUserCountryCode());
			pstmt.setString(8, invStatModel.getLastUserId());

			if (!isRequestExistsInInviteStatus(conn, invStatModel.getUserId(), invStatModel.getWrkSpcId())) {

				rowCount = pstmt.executeUpdate();

			} else {

				//delete and insert
				deleteReqFromInviteStatus(conn, invStatModel.getUserId(), invStatModel.getWrkSpcId());

				rowCount = pstmt.executeUpdate();

			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);
		}

		if (rowCount > 0) {

			flg = true;

		}
		return flg;

	}

	/**
	 * 
	 * @param conn
	 * @param inviteId
	 * @param wrkSpcId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean isRequestExistsInInviteStatus(Connection conn, String inviteId, String wrkSpcId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("SELECT COUNT(INVITE_ID) FROM ETS.ETS_INVITE_STATUS");
		sb.append(" WHERE");
		sb.append(" INVITE_ID = '" + inviteId + "' ");
		sb.append(" AND PROJECT_ID='" + wrkSpcId + "' ");
		sb.append(" with ur");

		logger.debug("isRequestExistsInInviteStatus qry==" + sb.toString());

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count > 0) {

			return true;

		}

		return false;

	}

	public synchronized int deleteReqFromInviteStatus(Connection conn, String inviteId, String wrkSpcId) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM ETS.ETS_INVITE_STATUS WHERE  INVITE_ID =? and PROJECT_ID=? ");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, inviteId);
			pstmt.setString(2, wrkSpcId);

			deleteCount = pstmt.executeUpdate();

			logger.debug("deleteReqFromInviteStatus::deleteCount=" + inviteId + ":wrkSpcId : " + wrkSpcId + "+:" + deleteCount);

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return deleteCount;
	}


	/**
			 * 
			 * @param etsId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public ArrayList getAllInviteMembersList() throws SQLException, Exception {

		ArrayList inviteList = new ArrayList();
		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {

			sb.append(" select ");
			sb.append(" a.invite_id as INVITEID,  ");
			sb.append(" a.project_id as WRKSPCID,");
			sb.append(" b.project_type as WRKSPCTYPE,");
			sb.append(" a.status as INVSTATUS,");
			sb.append(" a.role_id as ROLEID,");
			sb.append(" a.REQUESTOR_ID as REQUESTORID,");
			sb.append(" a.company as COMPANY,");
			sb.append(" a.country_code as COUNTRYCODE,");
			sb.append(" a.last_userid as LASTUSERID ");
			sb.append(" from ");
			sb.append(" ets.ets_invite_status a, ets.ets_projects b ");
			sb.append(" where ");
			sb.append(" b.project_id=a.project_id ");
			sb.append(" and a.status in ( 'R','I') ");
			sb.append(" and b.project_status !='D' ");
			sb.append(" order by wrkspctype");
			sb.append(" with ur");

			logger.debug("getAllInviteMembersList  QRY====" + sb.toString());

			conn = WrkSpcTeamUtils.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					UserInviteStatusModel invStatModel = new UserInviteStatusModel();
					invStatModel.setUserId(AmtCommonUtils.getTrimStr(rs.getString("INVITEID")));
					invStatModel.setWrkSpcId(AmtCommonUtils.getTrimStr(rs.getString("WRKSPCID")));
					invStatModel.setWrkSpcType(AmtCommonUtils.getTrimStr(rs.getString("WRKSPCTYPE")));
					invStatModel.setInviteStatus(AmtCommonUtils.getTrimStr(rs.getString("INVSTATUS")));
					invStatModel.setRoleId(rs.getInt("ROLEID"));
					invStatModel.setRequestorId(AmtCommonUtils.getTrimStr(rs.getString("REQUESTORID")));
					invStatModel.setUserCompany(AmtCommonUtils.getTrimStr(rs.getString("COMPANY")));
					invStatModel.setUserCountryCode(AmtCommonUtils.getTrimStr(rs.getString("COUNTRYCODE")));
					invStatModel.setLastUserId(AmtCommonUtils.getTrimStr(rs.getString("LASTUSERID")));

					//
					inviteList.add(invStatModel);

				}

			} //if r ! null

			///set information to end/////

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return inviteList;

	}

	
	public synchronized boolean updInviteStatusTab(UserInviteStatusModel invStatModel) throws SQLException, Exception {

		boolean flg = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		int rowCount = 0;

		try {

			conn = WrkSpcTeamUtils.getConnection();

			sb.append("UPDATE ETS.ETS_INVITE_STATUS");
			sb.append(" SET STATUS=?, LAST_TIMESTAMP=current timestamp ");
			sb.append(" WHERE");
			sb.append(" INVITE_ID=?");
			sb.append(" AND PROJECT_ID=?");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, invStatModel.getInviteStatus());
			pstmt.setString(2, invStatModel.getUserId());
			pstmt.setString(3, invStatModel.getWrkSpcId());

			rowCount = pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);
		}

		if (rowCount > 0) {

			flg = true;

		}
		return flg;

	}

	/**
				 * 
				 * @param etsId
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public ArrayList getInvitationsRepList(String sortBy, String sortOrder, String projectId) throws SQLException, Exception {

		ArrayList inviteList = new ArrayList();
		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {

			sb.append(" select ");
			sb.append(" a.invite_id as INVITEID,  ");
			sb.append(" 'INVITED' as STATUS,");
			sb.append(" (select b.role_name from ets.ets_roles b where b.role_id=a.role_id) as rolename,");
			sb.append(" a.requestor_id as requestorid, ");
			sb.append(" (select x.user_fullname from amt.users x where x.ir_userid=a.requestor_id) as requestorname,");
			sb.append(" a.company as company,");
			sb.append(" (select y.country_name from decaf.country y where y.country_code=a.country_code) as countryname");
			sb.append(" from ");
			sb.append(" ets.ets_invite_status a ");
			sb.append(" where ");
			sb.append(" a.status = 'I' ");
			sb.append(" and a.project_id='" + projectId + "' ");

			if (AmtCommonUtils.isResourceDefined(sortBy)) {

				sb.append(" order by " + sortBy + " ");
			} else {

				sb.append(" order by INVITEID ");
			}

			if (AmtCommonUtils.isResourceDefined(sortOrder)) {

				sb.append(" " + sortOrder + " ");

			} else {

				sb.append(" asc ");
			}

			sb.append(" with ur");

			logger.debug("getInvitationsRepList  QRY====" + sb.toString());

			conn = WrkSpcTeamUtils.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					UserInviteStatusModel invStatModel = new UserInviteStatusModel();
					invStatModel.setUserId(AmtCommonUtils.getTrimStr(rs.getString("INVITEID")));
					invStatModel.setInviteStatus(AmtCommonUtils.getTrimStr(rs.getString("STATUS")));
					invStatModel.setRoleName(AmtCommonUtils.getTrimStr(rs.getString("ROLENAME")));
					invStatModel.setRequestorId(AmtCommonUtils.getTrimStr(rs.getString("REQUESTORID")));
					invStatModel.setRequestorName(AmtCommonUtils.getTrimStr(rs.getString("REQUESTORNAME")));
					invStatModel.setUserCompany(AmtCommonUtils.getTrimStr(rs.getString("COMPANY")));
					invStatModel.setUserCountryName(AmtCommonUtils.getTrimStr(rs.getString("COUNTRYNAME")));

					//
					inviteList.add(invStatModel);

				}

			} //if r ! null

			///set information to end/////

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return inviteList;

	}

	/**
	 * 
	 * @param roleId
	 * @return
	 */

	public String getRolesName(int roleId) throws SQLException, Exception {

		Connection conn = null;
		String rolesName = "";

		try {

			conn = WrkSpcTeamUtils.getConnection();
			rolesName = AmtCommonUtils.getTrimStr(ETSDatabaseManager.getRoleName(conn, roleId));

		} finally {

			ETSDBUtils.close(conn);

		}

		return rolesName;

	}

	public String getCountryName(Connection conn, String countryCode) throws SQLException {

		//		country list only for external users
		StringBuffer sb = new StringBuffer();
		sb.append("select rtrim(country_name) as countryname from decaf.country where country_code='" + countryCode + "' with ur");

		return AmtCommonUtils.getValue(conn, sb.toString());

	}

	/**
							 * 
							 * @param sIRuserId
							 * @return
							 */

	public String getCountryName(String countryCode) throws SQLException, Exception {

		Connection conn = null;

		String counryName = "";

		try {

			conn = ETSDBUtils.getConnection();
			counryName = getCountryName(conn, countryCode);

		} finally {

			ETSDBUtils.close(conn);
		}

		return counryName;

	}

} //end of class
