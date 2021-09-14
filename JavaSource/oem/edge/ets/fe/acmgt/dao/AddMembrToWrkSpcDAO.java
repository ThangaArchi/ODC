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
import java.util.Vector;
import java.util.StringTokenizer;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.Metrics;
import oem.edge.amt.UserObject;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.decaf.DecafCreateEnt;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.acmgt.actions.UserCountries;
import oem.edge.ets.fe.acmgt.actions.UserPrivileges;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.UserIccStatusModel;
import oem.edge.ets.fe.acmgt.model.UserWrkSpcStatusModel;
import oem.edge.ets.fe.acmgt.resources.WrkSpcTeamConstantsIF;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSSyncUser;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProjectInfoBean;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AddMembrToWrkSpcDAO implements WrkSpcTeamConstantsIF {

	public static final String VERSION = "1.9";

	private static Log logger = EtsLogger.getLogger(AddMembrToWrkSpcDAO.class);

	/**
	 *
	 */
	public AddMembrToWrkSpcDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 *
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public UserIccStatusModel getUserIdStatusInICC(String userId) throws SQLException, Exception {

		Connection conn = null;
		int uidcount = 0;
		int emailcount = 0;
		int decafidcount=0;
		UserIccStatusModel iccStatModel = new UserIccStatusModel();

		try {

			conn = WrkSpcTeamUtils.getConnection();

			uidcount = getUserIdCountInAMT(conn, userId);
			emailcount = getUserEmailCountInAMT(conn, userId);
			decafidcount=getUserIdCountInDECAF(conn,userId);

			iccStatModel.setAmtuidcount(uidcount);
			iccStatModel.setAmtemailcount(emailcount);
			iccStatModel.setDecafidcount(decafidcount);

		} finally {

			ETSDBUtils.close(conn);
		}

		return iccStatModel;

	}

	/**
	 *
	 * @param conn
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	private int getUserIdCountInAMT(Connection conn, String userId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(a.ir_userid) from amt.users a where a.ir_userid = '" + userId + "' and a.status='A' with ur");

		if (logger.isDebugEnabled()) {

			logger.debug("getUserIdCountInAMT qry=" + sb.toString() + ":");

		}

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		return count;

	}

	/**
		 *
		 * @param conn
		 * @param userId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

		private int getUserIdCountInDECAF(Connection conn, String userId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();
			int count = 0;

			sb.append("select count(a.userid) from decaf.users a where a.userid = (select edge_userid from amt.users where ir_userid= '" + userId + "')  with ur");

			if (logger.isDebugEnabled()) {

				logger.debug("getUserIdCountInDECAF qry=" + sb.toString() + ":");

			}

			count = AmtCommonUtils.getRecCount(conn, sb.toString());

			return count;

		}


	/**
		 *
		 * @param conn
		 * @param userId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	private int getUserEmailCountInAMT(Connection conn, String userId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(a.ir_userid) from amt.users a where a.user_email = '" + userId + "' and a.status='A' with ur");

		if (logger.isDebugEnabled()) {

			logger.debug("getUserEmailCountInAMT qry=" + sb.toString() + ":");

		}

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		return count;

	}

	/**
	 *
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public UserWrkSpcStatusModel getUserIdStatusInWrkSpc(String projectId, String userId) throws SQLException, Exception {

		Connection conn = null;

		int uidcount = 0;
		int emailcount = 0;

		UserWrkSpcStatusModel wrkSpcStat = new UserWrkSpcStatusModel();
		try {

			conn = WrkSpcTeamUtils.getConnection();

			uidcount = getUserIdCountInWrkSpc(conn, projectId, userId);
			emailcount = getUserEmailCountInWrkSpc(conn, projectId, userId);

			wrkSpcStat.setWrkspcuidcount(uidcount);
			wrkSpcStat.setWrkspcemailcount(emailcount);

		} finally {

			ETSDBUtils.close(conn);
		}

		return wrkSpcStat;

	}

	/**
	 *
	 * @param conn
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	private int getUserIdCountInWrkSpc(Connection conn, String projectId, String userId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(a.user_id) from ets.ets_users a");
		sb.append(" where");
		sb.append(" a.user_project_id='" + projectId + "' ");
		sb.append(" and a.user_id='" + userId + "' ");
		sb.append(" with ur");

		if (logger.isDebugEnabled()) {

			logger.debug("getUserIdCountInWrkSpc qry=" + sb.toString() + ":");

		}

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		return count;

	}

	/**
			 *
			 * @param conn
			 * @param userId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	private int getUserEmailCountInWrkSpc(Connection conn, String projectId, String userId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(a.user_id) from ets.ets_users a");
		sb.append("  where");
		sb.append(" a.user_project_id='" + projectId + "' ");
		sb.append(" and a.user_id IN (select b.ir_userid from amt.users b where b.user_email='" + userId + "' and  b.status='A') ");
		sb.append(" with ur");

		if (logger.isDebugEnabled()) {

			logger.debug("getUserEmailCountInWrkSpc qry=" + sb.toString() + ":");

		}

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		return count;

	}

	/**
	 *
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean isUserIdDefndInUD(String userId) throws SQLException, Exception {

		Connection conn = null;
		boolean flag = false;

		try {

			conn = WrkSpcTeamUtils.getConnection();
			flag = AccessCntrlFuncs.isIbmIdExistsInIR(conn, userId);

		} finally {

			ETSDBUtils.close(conn);
		}

		return flag;

	}

	/**
		 *
		 * @param userId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public boolean userHasEntitlement(String sIRUserId, String sEntitlement) throws SQLException, Exception {

		Connection conn = null;
		boolean flag = false;

		try {

			conn = WrkSpcTeamUtils.getConnection();
			flag = userHasEntitlement(conn, sIRUserId, sEntitlement);

		} finally {

			ETSDBUtils.close(conn);
		}

		return flag;

	}

	/**
			* Method userHasEntitlement.
			* @param con
			* @param sIRId
			* @param sEntitlement
			* @return boolean
			* @throws SQLException
			* @throws Exception
			*/
	public boolean userHasEntitlement(Connection conn, String sIRUserId, String sEntitlement) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		boolean bEntitled = false;
		int count = 0;

		String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, sIRUserId);
		String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);

		// 6.1 Internal users are not required to have Sales_collab entitlement
		if (decaftype.equalsIgnoreCase("I") && sEntitlement.equalsIgnoreCase(Defines.COLLAB_CENTER_ENTITLEMENT)) {
			bEntitled = true;
		} else {

			sb.append("SELECT COUNT(ENTITLEMENT) FROM AMT.S_USER_ACCESS_VIEW WHERE ENTITLEMENT = '" + sEntitlement + "' AND USERID = (SELECT EDGE_USERID FROM AMT.USERS WHERE IR_USERID='" + sIRUserId + "') with ur");

			if (logger.isDebugEnabled()) {

				logger.debug("AddWrkSpcDao::userHasEntitlement()==" + sb.toString());

			}

			count = AmtCommonUtils.getRecCount(conn, sb.toString());

			if (count > 0) {

				bEntitled = true;
			}
		}

		return bEntitled;
	}

	/**
			 *
			 * @param userId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public boolean userHasPendEntitlement(String sIRUserId, String sEntitlement) throws SQLException, Exception {

		Connection conn = null;
		boolean flag = false;

		try {

			conn = WrkSpcTeamUtils.getConnection();
			flag = userHasPendEntitlement(conn, sIRUserId, sEntitlement);

		} finally {

			ETSDBUtils.close(conn);
		}

		return flag;

	}

	/**
	 *
	 * @param conn
	 * @param edgeid
	 * @param entitlement
	 * @return
	 * @throws SQLException
	 */

	public boolean userHasPendEntitlement(Connection conn, String sIRUserId, String sEntitlement) throws SQLException {

		StringBuffer sb = new StringBuffer();
		int count = 0;
		boolean bEntitled = false;

		sb.append(" SELECT count(REQ_SERIAL_ID) ");
		sb.append(" from decaf.req_approval_tracking");
		sb.append(" where req_serial_id in");
		sb.append(" (select req_serial_id from decaf.req_approval_details");
		sb.append(" where");
		sb.append(" decaf_id = (select decaf_id from decaf.users");
		sb.append(" where userid = ");
		sb.append(" (SELECT EDGE_USERID FROM AMT.USERS WHERE IR_USERID='" + sIRUserId + "'))");
		sb.append(" and access_id = (select project_id from decaf.project where project_name = '" + sEntitlement + "'))");
		sb.append(" and appr_result in ('P','O') with ur");

		if (logger.isDebugEnabled()) {

			logger.debug("AddWrkSpcDao::userHasPendEntitlement()==" + sb.toString());

		}

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count > 0) {

			bEntitled = true;
		}

		return bEntitled;
	}

	/**
	 *
	 * @param sIRuserId
	 * @return
	 */

	public ETSUserDetails getUserDetails(String sIRUserId) throws SQLException, Exception {

		Connection conn = null;
		ETSUserDetails userDets = new ETSUserDetails();

		try {

			conn = WrkSpcTeamUtils.getConnection();
			userDets = getUserDetails(conn, sIRUserId);

		} finally {

			ETSDBUtils.close(conn);
		}

		return userDets;

	}

	/**
	 *
	 * @param conn
	 * @param sIRuserId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public ETSUserDetails getUserDetails(Connection conn, String sIRuserId) throws SQLException, Exception {

		//extract user dets

		ETSUserDetails userDets = new ETSUserDetails();
		userDets.setWebId(sIRuserId);
		userDets.extractUserDetails(conn);

		return userDets;
	}

	/**
	 *
	 * @param userId
	 * @param sAssignCntryCode
	 * @param sAssignCompany
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean updateUserCompCountry(String userId, String sAssignCntryCode, String sAssignCompany, String lastUserId) throws SQLException, Exception {

		Connection conn = null;
		boolean flag = false;

		try {

			conn = WrkSpcTeamUtils.getConnection();
			flag = updateUserCompCountry(conn, userId, sAssignCntryCode, sAssignCompany, lastUserId);

		} finally {

			ETSDBUtils.close(conn);
		}

		return flag;
	}

	/**
	 *
	 * @param conn
	 * @param userId
	 * @param sAssignCntryCode
	 * @param sAssignCompany
	 * @return
	 */

	public boolean updateUserCompCountry(Connection conn, String userId, String sAssignCntryCode, String sAssignCompany, String lastUserId) throws SQLException, Exception {

		boolean flag = false;

		int count = EntitledStatic.fireUpdate(conn, "update decaf.users set assoc_company = '" + sAssignCompany + "', country_code = '" + sAssignCntryCode + "',last_userid='" + lastUserId + "', last_timestamp=current timestamp  where userid = '" + userId + "'");

		if (count > 0) {

			flag = true;
		}

		return flag;

	}

	/**
		 *
		 * @param sIRuserId
		 * @return
		 */

	public boolean LogIntoMetrics(String sIRUserId, String sLogMsg) throws SQLException, Exception {

		Connection conn = null;
		ETSUserDetails userDets = new ETSUserDetails();
		boolean flag = false;

		try {

			conn = WrkSpcTeamUtils.getConnection();
			Metrics.appLog(conn, sIRUserId, sLogMsg);

		} finally {

			ETSDBUtils.close(conn);
		}

		return flag;

	}

	/**
	 *
	 * @param userId
	 * @param conn
	 * @return
	 */

	public boolean syncUser(String userId, Connection conn) {

		ETSSyncUser syncUser = new ETSSyncUser();
		syncUser.setWebId(userId);
		ETSStatus status = syncUser.syncUser(conn);

		if (status.getErrCode() == 0) {

			return true;
		}

		return false;
	}

	/**
			 *
			 * @param sIRuserId
			 * @return
			 */

	public boolean syncUser(String userId) throws SQLException, Exception {

		Connection conn = null;

		boolean flag = false;

		try {

			conn = WrkSpcTeamUtils.getConnection();
			flag = syncUser(userId, conn);

		} finally {

			ETSDBUtils.close(conn);
		}

		return flag;

	}

	/**
				 *
				 * @param sIRuserId
				 * @return
				 */

	public ArrayList getMultiIDDetails(String userId) throws SQLException, Exception {

		Connection conn = null;

		ArrayList userList = new ArrayList();

		try {

			conn = WrkSpcTeamUtils.getConnection();
			userList = getMultiIDDetails(conn, userId);

		} finally {

			ETSDBUtils.close(conn);
		}

		return userList;

	}

	/**
			 *
			 * @param conn
			 * @param userId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public ArrayList getMultiIDDetails(Connection conn, String userId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;
		ArrayList userList = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		UserObject userDet = new UserObject();

		sb.append(" select ir_userid,edge_userid,user_email,user_fname,user_lname");
		sb.append(" from amt.users a ");
		sb.append(" where");
		sb.append(" a.user_email = '" + userId + "' and a.status='A' with ur");

		logger.debug("getUserEmailCountInAMT qry=" + sb.toString() + ":");

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					userDet.gIR_USERN = AmtCommonUtils.getTrimStr(rs.getString("IR_USERID"));
					userDet.gUSERN = AmtCommonUtils.getTrimStr(rs.getString("EDGE_USERID"));
					userDet.gEMAIL = AmtCommonUtils.getTrimStr(rs.getString("USER_EMAIL"));
					userDet.gFIRST_NAME = AmtCommonUtils.getTrimStr(rs.getString("USER_FNAME"));
					userDet.gLAST_NAME = AmtCommonUtils.getTrimStr(rs.getString("USER_LNAME"));

					userList.add(userDet);
					
				}
			}
			
		} catch (SQLException ex) {

			logger.error("SQL Exception in getMultiIDDetails", ex);
			ex.printStackTrace();
		}finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		
		return userList;

	}

	public ArrayList getCompanyList(Connection conn) {

		StringBuffer sb = new StringBuffer();
		int count = 0;
		ArrayList compList = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;

		sb.append("select distinct parent from decafobj.company_view with ur");

		logger.debug("getCompanyList qry=" + sb.toString() + ":");

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					compList.add(AmtCommonUtils.getTrimStr(rs.getString("PARENT")));

				}
			}
		
		} catch (SQLException ex) {

			logger.error("SQL Exception in getCompanyList", ex);
			ex.printStackTrace();

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		
		return compList;

	}

	/**
					 *
					 * @param sIRuserId
					 * @return
					 */

	public ArrayList getCompanyList() throws SQLException, Exception {

		Connection conn = null;

		ArrayList compList = new ArrayList();

		try {

			conn = ETSDBUtils.getConnection();
			compList = getCompanyList(conn);

		} finally {

			ETSDBUtils.close(conn);
		}

		return compList;

	}

	/**
						 *
						 * @param sIRuserId
						 * @return
						 */

	public ArrayList getCountryList() throws SQLException, Exception {

		Connection conn = null;

		ArrayList compList = new ArrayList();

		try {

			conn = ETSDBUtils.getConnection();
			compList = getCountryList(conn);

		} finally {

			ETSDBUtils.close(conn);
		}

		return compList;

	}

	public ArrayList getCountryList(Connection conn) throws SQLException {

		//		country list only for external users
		StringBuffer sb = new StringBuffer();
		sb.append("select rtrim(country_code) as countrycode,rtrim(country_name) as countryname from decaf.country order by 2 with ur");

		Statement stmt = null;
		ResultSet rs = null;
		String countryCode = "";
		String countryName = "";
		ArrayList countryList = new ArrayList();

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					countryCode = AmtCommonUtils.getTrimStr(rs.getString("COUNTRYCODE"));
					countryName = AmtCommonUtils.getTrimStr(rs.getString("COUNTRYNAME"));

					countryList.add(countryCode);
					countryList.add(countryName);

				}
			}
			
		} catch (SQLException ex) {

			logger.error("SQL Exception in getCompanyList", ex);
			ex.printStackTrace();

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return countryList;

	}


	/**
	 *
	 * @param sIRuserId
	 * @return
	 */

	public Vector getCntryList() throws SQLException, Exception {

		Connection conn = null;

		Vector compList = new Vector();

		try {

			conn = ETSDBUtils.getConnection();
			compList = getCntryList(conn);

		} finally {

			ETSDBUtils.close(conn);
		}

		return compList;

	}

	public Vector getCntryList(Connection conn) throws SQLException {

		//		country list only for external users
		StringBuffer sb = new StringBuffer();
		sb.append("select rtrim(country_code) as countrycode,rtrim(country_name) as countryname from decaf.country order by 2 with ur");

		Statement stmt = null;
		ResultSet rs = null;
		String countryCode = "";
		String countryName = "";
		Vector countryList = new Vector();

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					countryCode = AmtCommonUtils.getTrimStr(rs.getString("COUNTRYCODE"));
					countryName = AmtCommonUtils.getTrimStr(rs.getString("COUNTRYNAME"));

					UserCountries uCountries = new UserCountries();
					uCountries.setCountryCode(countryCode);
					uCountries.setCountryName(countryName);

					countryList.addElement(uCountries);
				}
			}
		} catch (SQLException ex) {

			logger.error("SQL Exception in getCompanyList", ex);
			ex.printStackTrace();

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return countryList;

	}

	public String getCountryName(Connection conn,String countryCode) throws SQLException {

		//		country list only for external users
		StringBuffer sb = new StringBuffer();
		sb.append("select rtrim(country_name) as countryname from decaf.country where country_code='" + countryCode + "' with ur");

		Statement stmt = null;
		ResultSet rs = null;
		String countryName = "";

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					countryName = AmtCommonUtils.getTrimStr(rs.getString("COUNTRYNAME"));

				}
			}
		} catch (SQLException ex) {

			logger.error("SQL Exception in getCountryName", ex);
			ex.printStackTrace();

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return countryName;

	}



	public String getWSCompany(Connection conn,String sProjId) throws SQLException {

		//		country list only for external users
		StringBuffer sb = new StringBuffer();
		sb.append("select company from ets.ets_projects where project_id = '" + sProjId + "' with ur");

		Statement stmt = null;
		ResultSet rs = null;
		String wkspcCmp = "";

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					wkspcCmp = AmtCommonUtils.getTrimStr(rs.getString("company"));

				}
			}
		} catch (SQLException ex) {

			logger.error("SQL Exception in getWSCompany", ex);
			ex.printStackTrace();

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return wkspcCmp;

	}

	/**
	 * @param strProjectId
	 * @param iViewType
	 * @return
	 * @throws SQLException
	 */
	public int getTopCatId(String strProjectId, int iViewType)
		throws SQLException,Exception {

        Connection conn = null;
		int iTopCatID = 0;

		String query =
			"select cat_id from ets.ets_cat"
				+ " where view_type = "
				+ iViewType
				+ " and project_id = '"
				+ strProjectId
				+ "'"
				+ " and parent_id=0 with ur";

		try{
			conn = ETSDBUtils.getConnection();
			Statement stmtTopCat = conn.createStatement();
			ResultSet rsTopCat = stmtTopCat.executeQuery(query);

			if (rsTopCat.next()) {
				iTopCatID = rsTopCat.getInt("cat_id");
			}
			
			rsTopCat.close();
			stmtTopCat.close();
		} finally {
			ETSDBUtils.close(conn);
		}
		return iTopCatID;
	}

	/**
	 * @param projId
	 * @throws SQLException,Exception
	 */

	public Vector getIntUserPrvlgs(String projId) throws SQLException , Exception{

		  Vector userPrevProps = new Vector();
		  Connection conn = ETSDBUtils.getConnection();
		  ETSProjectInfoBean projBean = ETSUtils.getProjInfoBean(conn);
		  Vector r = ETSDatabaseManager.getRolesPrivs(projId, conn);

		  for (int i = 0; i < r.size(); i++) {
		   String[] rp = (String[]) r.elementAt(i);
		   int roleid = (new Integer(rp[0])).intValue();
		   String rolename = rp[1];
		   //String privs = rp[2];
		   String privids = rp[3];
		   if (!(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.OWNER, conn))) {
		     String priv_desc = "";
		     StringTokenizer st = new StringTokenizer(privids, ",");
		     Vector privs = new Vector();
		     while (st.hasMoreTokens()) {
		      String priv = st.nextToken();
		      privs.addElement(priv);
		     }
		     for (int j = 0; j < privs.size(); j++) {
		      String s = (String) privs.elementAt(j);
		      String desc = projBean.getInfoDescription("PRIV_" + s, 0);
		      if (!desc.equals("")) {
		       if (!priv_desc.equals("")) {
		        priv_desc = priv_desc + "; " + desc;
		       } else {
		        priv_desc = desc;
		       }
		      }
		     }
		     userPrevProps.add(new UserPrivileges(String.valueOf(roleid),rolename,priv_desc));
		    }
		   }
		ETSDBUtils.close(conn);
	    return userPrevProps;
	}

	/**
	 * @param projId
	 * @throws SQLException,Exception
	 */

	public Vector getExtUserPrvlgs(String projId) throws SQLException , Exception{

		Vector userPrevProps = new Vector();
		  Connection conn = ETSDBUtils.getConnection();
		  ETSProjectInfoBean projBean = ETSUtils.getProjInfoBean(conn);
		  Vector r = ETSDatabaseManager.getRolesPrivs(projId, conn);

		  for (int i = 0; i < r.size(); i++) {
		   String[] rp = (String[]) r.elementAt(i);
		   int roleid = (new Integer(rp[0])).intValue();
		   String rolename = rp[1];
		   //String privs = rp[2];
		   String privids = rp[3];
		   if (!(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.OWNER, conn)) && !(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.ADMIN, conn))) {
		     String priv_desc = "";
		     StringTokenizer st = new StringTokenizer(privids, ",");
		     Vector privs = new Vector();
		     while (st.hasMoreTokens()) {
		      String priv = st.nextToken();
		      privs.addElement(priv);
		     }
		     for (int j = 0; j < privs.size(); j++) {
		      String s = (String) privs.elementAt(j);
		      String desc = projBean.getInfoDescription("PRIV_" + s, 0);
		      if (!desc.equals("")) {
		       if (!priv_desc.equals("")) {
		        priv_desc = priv_desc + "; " + desc;
		       } else {
		        priv_desc = desc;
		      }
		     }
		    }
		     userPrevProps.add(new UserPrivileges(String.valueOf(roleid),rolename,priv_desc));
		  }
		}
		ETSDBUtils.close(conn);
		return userPrevProps;
	}
	
	/**
	 * @param projectId
	 * @return
	 * @throws SQLException
	 */
	public EtsPrimaryContactInfo getProjContactInfo(String projectId)
	throws SQLException , Exception {


		ArrayList contList = new ArrayList();
		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		EtsPrimaryContactInfo etsContInfo = new EtsPrimaryContactInfo();

		Statement stmt = null;
		ResultSet rs = null;

		sb.append("select a.ir_userid as iruserid,");
		sb.append("a.user_fullname as userfullname,");
		sb.append("a.user_email as useremail,a.user_phone as userphone");
		sb.append("   from amt.users a, ets.ets_users b ");
		sb.append("   where ");
		sb.append("   a.ir_userid=b.user_id");
		sb.append("   and   b.primary_contact='Y' ");
		sb.append("   and   b.user_project_id='" + projectId + "'");
		sb.append("  with ur");

		try {
			conn = ETSDBUtils.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					etsContInfo.setUserIrId(AmtCommonUtils.getTrimStr(rs
							.getString("IRUSERID")));
					etsContInfo.setUserFullName(AmtCommonUtils.getTrimStr(rs
							.getString("USERFULLNAME")));
					etsContInfo.setUserEmail(AmtCommonUtils.getTrimStr(rs
							.getString("USEREMAIL")));
					etsContInfo.setUserContPhone(AmtCommonUtils.getTrimStr(rs
							.getString("USERPHONE")));

				}

			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}
		return etsContInfo;

	}


	public static boolean setDefaultAICDecafEntitlementExtUsers(Connection conn, String sEdgeId){

		boolean bInsertEnt = false;
		ArrayList  entProjList = new ArrayList();

		try{
			String role_id=EntitledStatic.getValue(conn,"select roles_id from decaf.roles "
					+ "where roles_name='" + Defines.AIC_ENTITLEMENT + "' for read only");

			entProjList.add(role_id);

			DecafCreateEnt crtent= new DecafCreateEnt();
			crtent.setUserid(sEdgeId);
			crtent.setIsEnt(true);
			crtent.setEntprojList(entProjList);
			int i = crtent.insertEntProj(conn);
			System.out.println("i===="+i);
			if (i == 0) {
				bInsertEnt = true;
			}
		}
		catch(Exception e){
			  e.printStackTrace();

		}


		return bInsertEnt;
	}

	/*
	 * This method will send mail to the POC for extrenal users if the user has been granted access
	 * to AIC by decaf API directly
	 * @author vishal
	 */

	public boolean sendPOCInformationEmail(Connection conn, EdgeAccessCntrl ez, String sProjId, String sProjName, String sNewUser) throws Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {
			ETSUserDetails new_user = new ETSUserDetails();
			new_user.setWebId(sNewUser);
			new_user.extractUserDetails(conn);

			ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, sProjId);
			UnbrandedProperties unBrandedprop =
				PropertyFactory.getProperty(proj.getProjectType());
			
			String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }

			// country for external users
			String qry = "select rtrim(country_code),rtrim(country_name) from decaf.country where country_code='" + new_user.getCountryCode() + "'  with ur";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(qry);
			String countryName = "";
			while (rs.next()) {
				countryName = rs.getString(2);
			}

			String appName = unBrandedprop.getAppName();
			String sEmailSubject = "IBM " + unBrandedprop.getAppName() + " - Access granted to " + new_user.getFirstName() + " " + new_user.getLastName() + " for " + sProjName;

			sEmailStr.append("This is an informational email since you are the 	POC\n");
			sEmailStr.append("for the below user. This user will now be able to access the following " + unBrandedprop.getAppName() + " workspace: " + sProjName + ".\n\n");
			sEmailStr.append("The details of the user are as follows: \n");
			sEmailStr.append("User name               : " + new_user.getFirstName() + " " + new_user.getLastName() + "\n");
			sEmailStr.append("User email id           : " + new_user.getEMail()+ "\n");
			sEmailStr.append("User company            : " + new_user.getCompany() + "\n");
			sEmailStr.append("User country            : " + countryName + "\n\n");
			sEmailStr.append("Access granted by  : " + ez.gFIRST_NAME.trim() + " " + ez.gLAST_NAME.trim() + "\n\n");
			sEmailStr.append("If you have questions or comments, please contact " + ez.gEMAIL +  "\n\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));

			System.out.println(sEmailStr);
			bSent = ETSUtils.sendEMail(ez.gEMAIL,new_user.getPocEmail(),"",Global.mailHost,sEmailStr.toString(),sEmailSubject,ez.gEMAIL);

		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return bSent;

	}


	public static synchronized String[] addProjectMember(ETSUser user, Connection conn) throws SQLException {

		if (!existsRoleID(user.getRoleId(), conn))
				return new String[] { "-1", "role_id does not exist" };

		if (!existsProjectID(user.getProjectId(), conn))
				return new String[] { "-2", "project_id does not exist" };

		else if (isUserInProject(user.getUserId(), user.getProjectId(), conn))
				return new String[] { "-3", "user_id is already in project" };

		String update = "insert into ETS.ETS_USERS (USER_ID, USER_ROLE_ID,USER_PROJECT_ID,USER_JOB,PRIMARY_CONTACT,LAST_USERID,LAST_TIMESTAMP) values (" + "'"
				+ user.getUserId() + "'," + user.getRoleId() + "," + "'" + user.getProjectId() + "'," + "'" + user.getUserJob() + "'," + "'"
				+ user.getPrimaryContact() + "'," + "'" + user.getLastUserId() + "'," + " current timestamp )";

		Statement statement = conn.createStatement();
		int rowCount = statement.executeUpdate(update);

		try {
			String insert = "insert into ETS.ETS_USER_WS (USER_ID, PROJECT_ID) values (" + "'" + user.getUserId() + "','" + user.getProjectId() + "')";
			statement.executeUpdate(insert);
		} catch (Exception e) {
  	   }
		statement.close();

		if (rowCount == 1)
				return new String[] { "0", "" };
		else if (rowCount == 0)
				return new String[] { "1", "unknown db2 problem" };
		else
				return new String[] { String.valueOf(rowCount), "unknown db2 problem" };

	}

	public static boolean hasPendingRequest(Connection con, String userid, String sProjId) throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		boolean bExists = false;

		try {

			sQuery.append("SELECT REQUEST_ID FROM ETS.ETS_ACCESS_REQ WHERE USER_ID = ? AND PROJECT_NAME = (select project_name from ets.ets_projects where project_id = ? ) AND STATUS = ? with ur");

			SysLog.log(SysLog.DEBUG, "ETSAdminServlet::getPendingRequest()", "QUERY : " + sQuery.toString());

			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1, userid);
			stmt.setString(2, sProjId);
			stmt.setString(3, Defines.ACCESS_PENDING);

			rs = stmt.executeQuery();

			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					bExists = true;
				} else {
					bExists = false;
				}
			} else {
				bExists = false;
			}

		} catch (SQLException e) {
			logger.error("execption===", e);
			throw e;
		} catch (Exception e) {
			logger.error("execption===", e);
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return bExists;
	}

	static boolean existsRoleID(int roleid, Connection conn) throws SQLException {

		boolean exists = false;
		String query = "select distinct ROLE_NAME from ETS.ETS_ROLES where ROLE_ID = " + roleid + " with ur";
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.next())
			exists = true;
		rs.close();
		statement.close();

		return exists;

	}

	public static boolean isUserInProject(String userid, String projectid, Connection conn) throws SQLException {

		boolean exists = false;
		String query = "select USER_ROLE_ID from ETS.ETS_USERS where USER_ID = '" +	userid + "' and ACTIVE_FLAG='A' and USER_PROJECT_ID = '" + projectid + "' with ur";
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.next())
			exists = true;
		rs.close();
		statement.close();

		return exists;
	}

	static boolean existsProjectID(String projectid, Connection conn) throws SQLException {

		boolean exists = false;
		String query = "select PROJECT_NAME from ETS.ETS_PROJECTS where PROJECT_ID 	= '" + projectid + "' with ur";
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.next())
			exists = true;
		rs.close();
		statement.close();

		return exists;
	}

	public static int insertReasonToAdminLog(String userid, String projectid,String reason, Connection conn) throws SQLException,Exception {
        	   
       String action = "CPNY_DIFF_REASON";
       int updt = 0;
       
       try{
       	        
		PreparedStatement ps;
		
			ps = conn.prepareStatement("insert into ets.ets_admin_log(last_timestamp,user_id,PROJECT_ID,ACTION_TYPE,KEY1) values(current timestamp,?,?,?,?)");
			ps.setString(1, userid);
			ps.setString(2, projectid);
			ps.setString(3, action);
			ps.setString(4, reason);
		
		updt = ps.executeUpdate();
		ps.close();
		
		}catch (SQLException e) {
			logger.error("execption===", e);
			throw e;
		} catch (Exception e) {
			logger.error("execption===", e);
			throw e;
		} 
		
		return updt;
	}


} //end of class
