/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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
package oem.edge.ets.fe;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Vector;

import oem.edge.amt.AmtProfileDAObject;
import oem.edge.amt.AmtUserProfile;
import oem.edge.amt.AmtUserProfileFactory;
import oem.edge.common.Global;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;
public class ETSMetricsDAO {
	
	private static Log logger = EtsLogger.getLogger(ETSMetricsDAO.class);
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.15";
	static {
		if (!Global.loaded)
			Global.Init();
	}
	static boolean isAnOwner(String ir_id, String project_type) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return isAnOwner(ir_id, conn, project_type);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return false;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return false;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static boolean isAnOwner(String ir_id, Connection conn, String project_type) throws SQLException {
		boolean isOwner = false;
		Statement statement = null;
		ResultSet rs = null;
		try {
			String query = "select distinct p.company from ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r" + " where p.project_status != 'A'" + " and p.project_status != 'D' " + " and '" + ir_id + "'=u.user_id" + " and u.active_flag='A'" + " and u.user_project_id=p.project_id" + " and u.user_role_id = r.role_id" + " and r.priv_id = 8 and p.project_type='" + project_type + "' with ur";
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			if (rs.next()) {
				isOwner = true;
			}
		} finally {
			close(statement, rs);
		}
		return isOwner;
	}
	static boolean isBladeOwner(String ir_id, String project_type) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return isBladeOwner(ir_id, conn, project_type);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return false;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return false;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static boolean isBladeOwner(String ir_id, Connection conn, String project_type) throws SQLException {
		boolean isBladeOwner = false;
		String query =
			"select distinct p.company from ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r,blade.blade_projects b"
				+ " where p.project_status != 'A'"
				+ " and p.project_status != 'D' "
				+ " and p.project_id=b.ets_project_id "
				+ " and '"
				+ ir_id
				+ "'=u.user_id"
				+ " and u.active_flag='A'"
				+ " and u.user_project_id=p.project_id"
				+ " and u.user_role_id = r.role_id"
				+ " and r.priv_id = 8 and p.project_type='"
				+ project_type
				+ "' with ur";
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			if (rs.next()) {
				isBladeOwner = true;
			}
		} finally {
			close(statement, rs);
		}
		return isBladeOwner;
	}
	static Vector getAllCompanyList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getAllCompanyList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getAllCompanyList(ETSParams params, Connection conn) throws SQLException {
		Vector comps = new Vector();
		String s = new String();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query = "select distinct company from ETS.ETS_Projects" + " where project_status != 'A'" + " and project_status != 'D' and project_type='" + project_type + "' with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query = "select distinct p.company from ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r" + " where p.project_status != 'A'" + " and p.project_status != 'D' " + " and '" + params.es.gIR_USERN + "'=u.user_id" + " and u.active_flag='A'" + " and u.user_project_id=p.project_id" + " and u.user_role_id = r.role_id" + " and r.priv_id = 8 and p.project_type='" + project_type + "' with ur";
		}
		System.out.println("****************");
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			comps.addElement("All values");
			while (rs.next()) {
				comps.addElement(rs.getString("company"));
			}
		} finally {
			close(statement, rs);
		}
		return comps;
	}
	static Vector getAllWorkspaceList(ETSParams p) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getAllWorkspaceList(conn, p);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getAllWorkspaceList(Connection conn, ETSParams params) throws SQLException {
		Vector v = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query = "select project_id,project_name from ETS.ETS_Projects" + " where project_status != 'A'" + " and project_status != 'D' " + " and project_or_proposal!='M'" + " and project_type='" + project_type + "' order by project_name,project_id with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select p.project_id,p.project_name "
					+ " from ETS.ETS_Projects p,ets.ets_users u, ets.ets_roles r"
					+ " where project_status != 'A'"
					+ " and project_status != 'D' "
					+ " and project_or_proposal!='M'"
					+ " and p.project_id=u.user_project_id"
					+ " and u.user_role_id = r.role_id"
					+ " and r.priv_id="
					+ Defines.OWNER
					+ " and u.user_id='"
					+ params.es.gIR_USERN
					+ "'"
					+ " and u.active_flag='A'"
					+ " and p.project_type='"
					+ project_type
					+ "' order by p.project_name,project_id with ur";
		}
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			v.addElement(new String[] { "All values", "0All values0" });
			while (rs.next()) {
				v.addElement(new String[] { rs.getString("project_name"), rs.getString("project_id")});
			}
		} finally {
			close(statement, rs);
		}
		return v;
	}
	static Vector getAllUsersList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getAllUsersList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getAllUsersList(ETSParams params, Connection conn) throws SQLException {
		Vector v = new Vector();
		String s = new String();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query =
			"select distinct u.user_id,a.user_lname,a.user_fname"
				+ " from ets.ets_users u, ets.ets_projects p, amt.users a"
				+ " where p.project_id=u.user_project_id"
				+ " and u.user_id=a.ir_userid"
				+ " and u.active_flag='A'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and p.project_or_proposal!='M'"
				+ " and p.project_type='"
				+ project_type
				+ "' order by a.user_lname,a.user_fname,u.user_id with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select distinct u.user_id,a.user_lname,a.user_fname"
					+ " from ets.ets_users u, ets.ets_projects p, amt.users a"
					+ " where p.project_id=u.user_project_id"
					+ " and u.user_id=a.ir_userid"
					+ " and u.active_flag='A'"
					+ " and p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_or_proposal!='M'"
					+ " and p.project_id in (select pp.project_id from ets.ets_projects pp, ets.ets_users uu,ets.ets_roles rr "
					+ " where pp.project_id=uu.user_project_id"
					+ " and uu.user_id='"
					+ params.es.gIR_USERN
					+ "'"
					+ " and uu.user_role_id=rr.role_id"
					+ " and rr.priv_id="
					+ Defines.OWNER
					+ ")"
					+ " and p.project_type='"
					+ project_type
					+ "' order by a.user_lname,a.user_fname,u.user_id with ur";
		}
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			v.addElement(new String[] { "All values", "0All values0" });
			while (rs.next()) {
				v.addElement(new String[] { rs.getString("user_lname") + ", " + rs.getString("user_fname"), rs.getString("user_id")});
			}
		} finally {
			close(statement, rs);
		}
		return v;
	}
	static Vector getDeliveryTeamList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getDeliveryTeamList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getDeliveryTeamList(ETSParams params, Connection conn) throws SQLException {
		Vector teams = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query = "select distinct p.delivery_team " + " from ETS.ETS_Projects p" + " where p.project_status!='A'" + " and p.project_status!='D'" + " and p.project_type='" + project_type + "' with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select distinct p.delivery_team "
					+ " from ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r"
					+ " where p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_id=u.user_project_id"
					+ " and u.user_role_id=r.role_id"
					+ " and r.priv_id="
					+ Defines.OWNER
					+ " and u.active_flag='A'"
					+ " and u.user_id='"
					+ params.es.gIR_USERN
					+ "'"
					+ " and p.project_type='"
					+ project_type
					+ "' with ur";
		}
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			teams.addElement("All values");
			while (rs.next()) {
				teams.addElement(rs.getString("delivery_team"));
			}
		} finally {
			close(statement, rs);
		}
		return teams;
	}
	static Vector getExpCatList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getExpCatList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getExpCatList(ETSParams params, Connection conn) throws SQLException {
		Vector v = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query = "select distinct description from ETS.ETS_exp_data " + " union select distinct description from ETS.ETS_self_data with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select distinct e.description "
					+ " from ETS.ETS_exp_data e,ETS.ETS_qbr_exp q, ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r"
					+ " where e.expect_id=q.expect_id"
					+ " and q.project_id=p.project_id"
					+ " and p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_type='"
					+ project_type
					+ "' and p.company in (select pp.company from ETS.ETS_Projects pp, ets.ets_users u, ets.ets_roles r"
					+ " where u.user_project_id=pp.project_id"
					+ " and u.user_role_id=r.role_id"
					+ " and r.priv_id="
					+ Defines.OWNER
					+ " and u.active_flag='A'"
					+ " and u.user_id='"
					+ params.es.gIR_USERN
					+ "')"
					+ " union select distinct s.description "
					+ " from ETS.ETS_self_data s,ETS.ETS_self_exp q, ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r"
					+ " where s.expect_id=q.expect_id"
					+ " and q.project_id=p.project_id"
					+ " and p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_type='"
					+ project_type
					+ "' and p.company in (select pp.company from ETS.ETS_Projects pp, ets.ets_users u, ets.ets_roles r"
					+ " where u.user_project_id=pp.project_id"
					+ " and u.user_role_id=r.role_id"
					+ " and r.priv_id="
					+ Defines.OWNER
					+ " and u.active_flag='A'"
					+ " and u.user_id='"
					+ params.es.gIR_USERN
					+ "')"
					+ " with ur";
		}
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			v.addElement("All values");
			while (rs.next()) {
				System.out.println("desc = " + rs.getString("description"));
				v.addElement(rs.getString("description"));
			}
		} finally {
			close(statement, rs);
		}
		return v;
	}
	static Vector getAdvocateList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getAdvocateList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getAdvocateList(ETSParams params, Connection conn) throws SQLException {
		Vector v = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query =
			"select distinct u.user_id,a.user_lname,a.user_fname"
				+ " from ets.ets_users u, ets.ets_projects p, amt.users a"
				+ " where p.project_id=u.user_project_id"
				+ " and u.user_id=a.ir_userid"
				+ " and u.active_flag='A'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and p.project_or_proposal='C'"
				+ " and u.primary_contact='Y'"
				+ " and p.project_type='"
				+ project_type
				+ "' order by a.user_lname,a.user_fname,u.user_id with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select distinct u.user_id,a.user_lname,a.user_fname"
					+ " from ets.ets_users u, ets.ets_projects p, amt.users a"
					+ " where p.project_id=u.user_project_id"
					+ " and u.user_id=a.ir_userid"
					+ " and u.active_flag='A'"
					+ " and p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_or_proposal='C'"
					+ " and u.primary_contact='Y'"
					+ " and p.project_type='"
					+ project_type
					+ "' and p.company in (select pp.company from ets.ets_projects pp, ets.ets_users uu,ets.ets_roles rr "
					+ " where pp.project_id=uu.user_project_id"
					+ " and uu.user_id='"
					+ params.es.gIR_USERN
					+ "'"
					+ " and uu.user_role_id=rr.role_id"
					+ " and rr.priv_id="
					+ Defines.OWNER
					+ ")"
					+ " order by a.user_lname,a.user_fname,u.user_id with ur";
		}
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			v.addElement(new String[] { "All values", "0All values0" });
			while (rs.next()) {
				String id = rs.getString("user_id");
				String first = rs.getString("user_fname");
				String last = rs.getString("user_lname");
				v.addElement(new String[] { last + ", " + first, id });
			}
		} finally {
			close(statement, rs);
		}
		return v;
	}
	static Vector getCVWSOwnerList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getCVWSOwnerList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getCVWSOwnerList(ETSParams params, Connection conn) throws SQLException {
		Vector v = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query =
			"select distinct u.user_id,a.user_lname,a.user_fname"
				+ " from ets.ets_users u,ets.ets_roles r, ets.ets_projects p, amt.users a"
				+ " where p.project_id=u.user_project_id"
				+ " and u.user_id=a.ir_userid"
				+ " and u.active_flag='A'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and p.project_or_proposal='C'"
				+ " and u.user_role_id=r.role_id"
				+ " and r.priv_id="
				+ Defines.OWNER
				+ " and p.project_type='"
				+ project_type
				+ "' order by a.user_lname,a.user_fname,u.user_id with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select distinct u.user_id,a.user_lname,a.user_fname"
					+ " from ets.ets_users u, ets.ets_projects p, amt.users a,ets.ets_roles r"
					+ " where p.project_id=u.user_project_id"
					+ " and u.user_id=a.ir_userid"
					+ " and u.active_flag='A'"
					+ " and p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_or_proposal='C'"
					+ " and u.user_role_id=r.role_id"
					+ " and r.priv_id="
					+ Defines.OWNER
					+ " and p.project_type='"
					+ project_type
					+ "' and p.company in (select pp.company from ets.ets_projects pp, ets.ets_users uu,ets.ets_roles rr "
					+ " where pp.project_id=uu.user_project_id"
					+ " and uu.user_id='"
					+ params.es.gIR_USERN
					+ "'"
					+ " and uu.user_role_id=rr.role_id"
					+ " and rr.priv_id="
					+ Defines.OWNER
					+ ")"
					+ " order by a.user_lname,a.user_fname,u.user_id with ur";
		}
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			v.addElement(new String[] { "All values", "0All values0" });
			while (rs.next()) {
				String id = rs.getString("user_id");
				String first = rs.getString("user_fname");
				String last = rs.getString("user_lname");
				v.addElement(new String[] { last + ", " + first, id });
			}
		} finally {
			close(statement, rs);
		}
		return v;
	}
	/*
		static Vector getSAExpCatList(ETSParams params) {
			Connection conn = null;
	
			try{
				conn = ETSDBUtils.getConnection();
				return getSAExpCatList(params, conn);
			}
			catch (SQLException e) {
				System.out.println("SQL ERROR="+e);
				e.printStackTrace();
				return null;
			}
			catch (Exception ex) {
				System.out.println("EX ERROR="+ex);
				ex.printStackTrace();
				return null;
			}
			finally{
				ETSDBUtils.close(conn);
			}
	
		}
		static Vector getSAExpCatList(ETSParams params,Connection conn) throws SQLException {
			Vector v = new Vector();
			String query = "select distinct description from ETS.ETS_self_data with ur";
			
			if (!(params.isExecutive || params.isSuperAdmin)){
				query = "select distinct e.description " +
					" from ETS.ETS_self_data e,ETS.ETS_self_exp q, ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r" +
					" where e.expect_id=q.expect_id" +
					" and q.project_id=p.project_id"+
					" and p.project_status!='A'" +
					" and p.project_status!='D'" +
					" and p.company in (select pp.company from ETS.ETS_Projects pp, ets.ets_users u, ets.ets_roles r" +
						" where u.user_project_id=pp.project_id" +
						" and u.user_role_id=r.role_id" +
						" and r.priv_id="+Defines.OWNER+
						" and u.active_flag='A'"+
						" and u.user_id='"+params.es.gIR_USERN+"')"+
					" with ur";
			}
			
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(query);
	
			v.addElement("All values");
	
			while (rs.next()){
				v.addElement(rs.getString("description"));	
			}
	
			rs.close();
			statement.close();
	
			return v;
	
		}
	*/
	static Vector getTabNameList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getTabNameList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getTabNameList(ETSParams params, Connection conn) throws SQLException {
		Vector v = new Vector();
		String query = "select distinct cat_name from ETS.ETS_cat where cat_type=0 with ur";
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select distinct cat_name  "
					+ " from ETS.ETS_cat c, ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r"
					+ " where c.cat_type=0"
					+ " and c.project_id=p.project_id"
					+ " and p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_id=u.user_project_id"
					+ " and u.user_role_id=r.role_id"
					+ " and r.priv_id="
					+ Defines.OWNER
					+ " and u.active_flag='A'"
					+ " and u.user_id='"
					+ params.es.gIR_USERN
					+ "'"
					+ " and p.project_type='"
					+ project_type
					+ "' with ur";
		}
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			v.addElement("All values");
			while (rs.next()) {
				v.addElement(rs.getString("cat_name"));
			}
		} finally {
			close(statement, rs);
		}
		return v;
	}
	static Vector getIndsList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getIndsList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getIndsList(ETSParams params, Connection conn) throws SQLException {
		Vector v = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query = "select distinct industry from ETS.ETS_projects where project_type='" + project_type + "' with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select distinct p.industry  "
					+ " from ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r"
					+ " where p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_id=u.user_project_id"
					+ " and u.user_role_id=r.role_id"
					+ " and r.priv_id="
					+ Defines.OWNER
					+ " and u.active_flag='A'"
					+ " and u.user_id='"
					+ params.es.gIR_USERN
					+ "'"
					+ " and p.project_type='"
					+ project_type
					+ "' with ur";
		}
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			v.addElement("All values");
			while (rs.next()) {
				String i = rs.getString("industry");
				System.out.println("industry=" + i);
				if (i != null)
					v.addElement(i);
			}
		} finally {
			close(statement, rs);
		}
		return v;
	}
	static Vector getGeosList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getGeosList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getGeosList(ETSParams params, Connection conn) throws SQLException {
		Vector v = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query = "select distinct geography from ETS.ETS_projects where project_type='" + project_type + "' with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select distinct p.geography  "
					+ " from ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r"
					+ " where p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_id=u.user_project_id"
					+ " and u.user_role_id=r.role_id"
					+ " and r.priv_id="
					+ Defines.OWNER
					+ " and u.active_flag='A'"
					+ " and u.user_id='"
					+ params.es.gIR_USERN
					+ "'"
					+ " and p.project_type='"
					+ project_type
					+ "' with ur";
		}
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			v.addElement("All values");
			while (rs.next()) {
				String g = rs.getString("geography");
				if (g != null)
					v.addElement(g);
			}
		} finally {
			close(statement, rs);
		}
		return v;
	}
	static Vector getRolesList(ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return getRolesList(params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector getRolesList(ETSParams params, Connection conn) throws SQLException {
		Vector v = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query = "select distinct role_name from ETS.ETS_roles with ur";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			query =
				"select distinct r.role_name  "
					+ " from ets.ets_roles r"
					+ " where r.project_id in ("
					+ " select p.project_id "
					+ " from ETS.ETS_Projects p, ets.ets_users u, ets.ets_roles r2"
					+ " where p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and p.project_id=u.user_project_id"
					+ " and u.user_role_id=r2.role_id"
					+ " and r2.priv_id="
					+ Defines.OWNER
					+ " and u.active_flag='A'"
					+ " and u.user_id='"
					+ params.es.gIR_USERN
					+ "')"
					+ " and p.project_type='"
					+ project_type
					+ "' with ur";
		}
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			v.addElement("All values");
			while (rs.next()) {
				String g = rs.getString("role_name");
				if (g != null)
					v.addElement(g);
			}
		} finally {
			close(statement, rs);
		}
		return v;
	}
	static Vector filterOverallSat(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterOverallSat(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterOverallSat(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String comp_filter = new String();
		String smdate_filter = new String();
		String cldes_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String sadate_filter = new String();
		String surveydate_filter1 = new String();
		String surveydate_filter2 = new String();
		String notAdminStr = getNotAdminStr(params);
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedClientDes().contains("All values")) {
			cldes_filter = " and c.designation in (" + resobj.getSelectedClientDesDBStr() + ")";
		}
		if (!resobj.getAllDates()) {
			smdate_filter = " and date(e.last_timestamp)>=date('" + resobj.getFromDateTS() + "') and date(e.last_timestamp) <= date('" + resobj.getToDateTS() + "')";
			sadate_filter = " and date(m.self_date)>=date('" + resobj.getFromDateTS() + "') and date(m.self_date) <= date('" + resobj.getToDateTS() + "')";
			surveydate_filter1 = " and d.survey_value!='' and date(d.survey_value)>=date('" + resobj.getFromDateTS() + "') and date(d.survey_value) <= date('" + resobj.getToDateTS() + "')";
			surveydate_filter2 = " and date(b.LAST_TIMESTAMP)>=date('" + resobj.getFromDateTS() + "') and date(b.LAST_TIMESTAMP) <= date('" + resobj.getToDateTS() + "')";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
			String query =
				"(select 'SM' as cvtype,c.designation,p.company as company,p.industry,p.geography,m.state,m.qbr_id as id,max(e.last_timestamp) as ts,"
					+ "(select er.final_rating from ets.ets_qbr_exp er"
					+ "  where er.qbr_id=m.qbr_id and er.project_id=m.project_id"
					+ "  and m.state='CLOSED' and er.question_id=4) as rating, 0 as cnt"
					+ " from ets.ets_qbr_main m, ets.ets_qbr_exp e,ets.ets_projects p"
					+ " left outer join ets.ets_client_desgn c"
					+ " on c.company=p.company"
					+ " where p.project_id=m.project_id"
					+ smdate_filter
					+ " and e.last_timestamp in ("
					+ " select e1.last_timestamp from ets.ets_qbr_exp e1"
					+ " where e.qbr_id=m.qbr_id and e.project_id=m.project_id"
					+ " and (m.state='OPEN' or (m.state='CLOSED' and e.question_id=4)))"
					+ " and p.project_or_proposal='C'"
					+ " and p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ comp_filter
					+ cldes_filter
					+ notAdminStr
					+ ind_filter
					+ geo_filter
					+ " and p.project_type='"
					+ project_type
					+ "' group by p.company,m.state,m.qbr_id,m.project_id,p.industry,p.geography,c.designation)"
					+ " union "
					+ " (select 'SA' as cvtype,c.designation,p.company as company,p.industry,p.geography,m.state,m.self_id as id,m.self_date as ts,"
					+ " (select sum(e2.rating) from ets.ets_self_exp e2"
					+ "  where e2.self_id=m.self_id and e2.project_id=m.project_id"
					+ "  and e2.section_id=0) as rating,"
					+ " (select count(e3.rating) from ets.ets_self_exp e3"
					+ "  where e3.self_id=m.self_id and e3.project_id=m.project_id"
					+ "  and e3.section_id=0) as cnt"
					+ " from ets.ets_self_main m, ets.ets_self_exp e,ets.ets_projects p"
					+ " left outer join ets.ets_client_desgn c"
					+ " on c.company=p.company"
					+ " where p.project_id=m.project_id"
					+ " and e.project_id=m.project_id"
					+ " and e.self_id=m.self_id"
					+ " and e.section_id=0"
		+ //ask anne about this
	" and p.project_or_proposal='C'" + " and p.project_status!='A'" + " and p.project_status!='D'" + sadate_filter + comp_filter + cldes_filter + notAdminStr + ind_filter + geo_filter + " and p.project_type='" + project_type + "' group by p.company,m.state,m.self_id,m.project_id,m.self_date,p.industry,p.geography,c.designation)" + " order by company with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				if (rs.getString("cvtype").equals("SM")) {
					obj.setChangeStr(true);
					obj.setWorkspaceType(Defines.METRICS_SM);
				} else {
					obj.setChangeStr(false);
					obj.setWorkspaceType(Defines.METRICS_SA);
				}
				obj.setClientDesignation(rs.getString("designation"));
				obj.setProjectCompany(rs.getString("company"));
				if (rs.getString("cvtype").equals("SA"))
					obj.setState("CLOSED");
				else
					obj.setState(rs.getString("state"));
				obj.setRatingDate(rs.getTimestamp("ts"));
				if (rs.getString("cvtype").equals("SA")) {
					double rating = ETSSupportFunctions.parseDouble(rs.getString("rating"), 0);
					int cnt = rs.getInt("cnt");
					if (cnt > 0) {
						double f = rating / cnt;
						obj.setRating((float) f);
					} else
						obj.setRating(0);
				} else
					obj.setRating((float) ETSSupportFunctions.parseDouble(rs.getString("rating"), 0));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				result.addElement(obj);
			}
		} finally {
			ETSSupportFunctions.close(statement, rs);
		}
		try {
			String qry =
				"select c.designation,p.company,p.industry,p.geography,d.SURVEY_VALUE as ts,b.survey_value as rating from ets.ETS_SURVEY_data a,ets.ETS_SURVEY_DATA b ,ets.ETS_SURVEY_DATA d , ets.ets_projects p "
					+ " left outer join ets.ets_client_desgn c"
					+ " on c.company=p.company"
					+ " where p.company=a.survey_value and a.response_id=b.response_id and a.response_id=d.response_id and a.SURVEY_KEY='CONAME' and b.survey_key='OSAT - IBM' and d.survey_key='INTERVIEW_END' "
					+ comp_filter
					+ cldes_filter
					+ notAdminStr
					+ ind_filter
					+ geo_filter
					+ surveydate_filter1
					+ " and p.project_type='"
					+ project_type
					+ "' and p.PROJECT_OR_PROPOSAL='C' with ur";
			System.out.println(qry);
			statement = conn.createStatement();
			rs = statement.executeQuery(qry);
			while (rs.next()) {
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				obj.setRating((float) ETSSupportFunctions.parseDouble(rs.getString("rating"), 0));
				obj.setProjectCompany(rs.getString("company"));
				obj.setClientDesignation(rs.getString("designation"));
				obj.setWorkspaceType(Defines.METRICS_SU);
				obj.setRatingDate(rs.getString("ts"));
				result.addElement(obj);
			}
		} finally {
			ETSSupportFunctions.close(statement, rs);
		}
		return result;
	}
	static Vector filterExpRatingByClient(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterExpRatingByClient(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterExpRatingByClient(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String comp_filter = new String();
		String smdate_filter = new String();
		String sadate_filter = new String();
		String expcat_filter = new String();
		String cldes_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String notAdminStr = getNotAdminStr(params);
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedClientDes().contains("All values")) {
			cldes_filter = " and c.designation in (" + resobj.getSelectedClientDesDBStr() + ")";
		}
		if (!resobj.getSelectedExpCats().contains("All values")) {
			expcat_filter = " and ex.description in (" + resobj.getSelectedExpCatsDBStr() + ")";
		}
		if (!resobj.getAllDates()) {
			smdate_filter = " and date(e.last_timestamp)>=date('" + resobj.getFromDateTS() + "') and date(e.last_timestamp) <= date('" + resobj.getToDateTS() + "')";
			sadate_filter = " and date(m.self_date)>=date('" + resobj.getFromDateTS() + "') and date(m.self_date) <= date('" + resobj.getToDateTS() + "')";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		//CHG5.4//
			String query =
				"(select 'SM' as cvtype,c.designation,p.company,p.industry,p.geography,m.state,m.qbr_id as id,e.last_timestamp as last_timestamp,ex.description,e.expect_rating as rat,e.final_rating as frat"
					+ " from ets.ets_qbr_main m, ets.ets_qbr_exp e,ets.ets_exp_data ex,ets.ets_projects p"
					+ " left outer join ets.ets_client_desgn c"
					+ " on c.company=p.company"
					+ " where p.project_id=m.project_id"
					+ " and m.qbr_id=e.qbr_id"
					+ " and m.project_id=e.project_id"
					+ " and ex.expect_id=e.expect_id"
					+ " and e.question_id!=5"
					+ " and e.question_id!=6"
					+ " and p.project_or_proposal='C'"
					+ " and p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ comp_filter
					+ cldes_filter
					+ expcat_filter
					+ smdate_filter
					+ notAdminStr
					+ ind_filter
					+ geo_filter
		+ //" order by p.company,date(e.last_timestamp),ex.description)"+
		" and p.project_type='"
			+ project_type
			+ "' ) union "
			+ "(select 'SA' as cvtype,c.designation,p.company,p.industry,p.geography,m.state,m.self_id as id,m.last_timestamp as last_timestamp,ex.description,"
			+ "(select sum(e2.rating) from ets.ets_self_exp e2"
			+ "  where e2.self_id=m.self_id and e2.project_id=m.project_id"
			+ "  and e2.section_id=0) as rat,"
			+ " (select count(e3.rating) from ets.ets_self_exp e3"
			+ "  where e3.self_id=m.self_id and e3.project_id=m.project_id"
			+ "  and e3.section_id=0) as frat"
			+ " from ets.ets_self_main m, ets.ets_self_exp e,ets.ets_self_data ex,ets.ets_projects p"
			+ " left outer join ets.ets_client_desgn c"
			+ " on c.company=p.company"
			+ " where p.project_id=m.project_id"
			+ " and m.self_id=e.self_id"
			+ " and m.project_id=e.project_id"
			+ " and ex.expect_id=e.expect_id"
			+ " and p.project_or_proposal='C'"
			+ " and p.project_status!='A'"
			+ " and p.project_status!='D'"
			+ " and e.section_id=0"
			+ comp_filter
			+ cldes_filter
			+ expcat_filter
			+ sadate_filter
			+ notAdminStr
			+ ind_filter
			+ geo_filter
			+ " and p.project_type='"
			+ project_type
			+ "' )"
		+ //" order by p.company,date(m.last_timestamp) desc,ex.description)";
	" order by 3,8,9 with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				String type = "";
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				String cvtype = rs.getString("cvtype");
				if (cvtype.equals("SA")) {
					type = Defines.METRICS_SA;
				} else if (cvtype.equals("SM")) {
					type = Defines.METRICS_SM;
				}
				System.out.println("type = " + type);
				obj.setWorkspaceType(type);
				obj.setClientDesignation(rs.getString("designation"));
				obj.setProjectCompany(rs.getString("company"));
				obj.setState(rs.getString("state"));
				obj.setRatingDate(rs.getTimestamp("last_timestamp"));
				if (type.equals(Defines.METRICS_SM)) {
					if (rs.getString("state").equals("OPEN"))
						obj.setRating(ETSSupportFunctions.parseFloat(rs.getString("rat")));
					else
						obj.setRating(ETSSupportFunctions.parseFloat(rs.getString("frat")));
				} else {
					float rating = ETSSupportFunctions.parseFloat(rs.getString("rat"));
					float cnt = ETSSupportFunctions.parseFloat(rs.getString("frat"));
					if (cnt > 0) {
						float f = rating / cnt;
						System.out.println(rating + "/" + cnt + "=" + f);
						obj.setRating(f);
					} else
						obj.setRating(0);
				}
				obj.setExpCat(rs.getString("description"));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterExpRatingFreqDistribution(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterExpRatingFreqDistribution(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterExpRatingFreqDistribution(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String comp_filter = new String();
		String date_filter = new String();
		String expcat_filter = new String();
		String cldes_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String notAdminStr = getNotAdminStr(params);
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedClientDes().contains("All values")) {
			cldes_filter = " and c.designation in (" + resobj.getSelectedClientDesDBStr() + ")";
		}
		if (!resobj.getSelectedExpCats().contains("All values")) {
			expcat_filter = " and ex.description in (" + resobj.getSelectedExpCatsDBStr() + ")";
		}
		if (!resobj.getAllDates()) {
			date_filter = " and date(e.last_timestamp)>=date('" + resobj.getFromDateTS() + "') and date(e.last_timestamp) <= date('" + resobj.getToDateTS() + "')";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query =
			"select distinct ex.description, "
				+ " sum(case when (e.final_rating>=0 and e.final_rating<=3) then 1 else 0 end) as low,"
				+ " sum(case when (e.final_rating>=4 and e.final_rating<=5) then 1 else 0 end) as some,"
				+ " sum(case when (e.final_rating>=6 and e.final_rating<=8) then 1 else 0 end) as met,"
				+ " sum(case when (e.final_rating>=9 and e.final_rating<=10) then 1 else 0 end) as exceed"
				+ " from ets.ets_exp_data ex, ets.ets_qbr_main m, ets.ets_qbr_exp e,ets.ets_projects p"
				+ " left outer join ets.ets_client_desgn c"
				+ " on c.company=p.company"
				+ " where e.qbr_id=m.qbr_id"
				+ " and e.project_id=m.project_id"
				+ " and p.project_id=m.project_id"
				+ " and e.expect_id=ex.expect_id"
				+ " and m.state='CLOSED'"
				+ " and p.project_or_proposal='C'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and e.question_id!=5"
				+ " and e.question_id!=6"
				+ comp_filter
				+ cldes_filter
				+ expcat_filter
				+ date_filter
				+ notAdminStr
				+ ind_filter
				+ geo_filter
				+ " and p.project_type='"
				+ project_type
				+ "' group by ex.description"
				+ " order by ex.description with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				obj.setState("CLOSED");
				obj.setExpCat(rs.getString("description"));
				obj.setLowRating(rs.getInt("low"));
				obj.setSomeRating(rs.getInt("some"));
				obj.setMetRating(rs.getInt("met"));
				obj.setExceedRating(rs.getInt("exceed"));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	/*
		static Vector filterOverallSASat(ETSMetricsResultObj resobj,ETSParams params) {
			Connection conn = null;
	
			try{
				conn = ETSDBUtils.getConnection();
				return filterOverallSASat(resobj,params,conn);
			}
			catch (SQLException e) {
				System.out.println("SQL ERROR="+e);
				e.printStackTrace();
				return null;
			}
			catch (Exception ex) {
				System.out.println("EX ERROR="+ex);
				ex.printStackTrace();
				return null;
			}
			finally{
				ETSDBUtils.close(conn);
			}
	
		}
		static Vector filterOverallSASat(ETSMetricsResultObj resobj,ETSParams params,Connection conn) throws SQLException {
			Vector result = new Vector();
			
			String comp_filter = new String();
			String date_filter = new String();
			String cldes_filter = new String();
			String ind_filter = new String();
			String geo_filter = new String();
			
			String notAdminStr = getNotAdminStr(params);
	
			if (!resobj.getSelectedComps().contains("All values")){
				comp_filter = " and p.company in ("+resobj.getSelectedCompsDBStr()+")";
			}
			if (!resobj.getSelectedClientDes().contains("All values")){
				cldes_filter = " and c.designation in ("+resobj.getSelectedClientDesDBStr()+")";
			}
			if (!resobj.getAllDates()){
				date_filter = " and date(m.self_date)>=date('"+resobj.getFromDateTS()+"') and date(m.self_date) <= date('"+resobj.getToDateTS()+"')"; 	
			}
			if (!resobj.getSelectedInds().contains("All values")){
				ind_filter = " and p.industry in ("+resobj.getSelectedIndsDBStr()+")";
			}
			if (!resobj.getSelectedGeos().contains("All values")){
				geo_filter = " and p.geography in ("+resobj.getSelectedGeosDBStr()+")";
			}
			
			String query = "select c.designation,p.company,p.industry,p.geography,m.state,m.self_id,m.self_date," +
				" (select sum(e2.rating) from ets.ets_self_exp e2" +
				"  where e2.self_id=m.self_id and e2.project_id=m.project_id" +
				"  and e2.section_id=0) as rating," +
				" (select count(e3.rating) from ets.ets_self_exp e3" +
				"  where e3.self_id=m.self_id and e3.project_id=m.project_id" +
				"  and e3.section_id=0) as cnt" +
				" from ets.ets_self_main m, ets.ets_self_exp e,ets.ets_projects p" +
				" left outer join ets.ets_client_desgn c" +
				" on c.company=p.company" +
				" where p.project_id=m.project_id" +
				" and e.project_id=m.project_id" +
				" and e.self_id=m.self_id" +
				" and e.section_id=0"+  //ask anne about this
				" and p.project_or_proposal='C'"+
				" and p.project_status!='A'"+
				" and p.project_status!='D'"+
				date_filter +
				comp_filter+
				cldes_filter+
				notAdminStr+
				ind_filter+
				geo_filter+
				" group by p.company,m.state,m.self_id,m.project_id,m.self_date,p.industry,p.geography,c.designation"+
				" order by p.company,m.self_date";
				
				System.out.println(query);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(query);
	
	
			while (rs.next()){
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				obj.setChangeStr(true);
				obj.setClientDesignation(rs.getString("designation"));
				obj.setProjectCompany(rs.getString("company"));	
				obj.setState(rs.getString("state"));
				obj.setCreateDate(rs.getTimestamp("self_date"));
				int rating = rs.getInt("rating");
				System.out.println("rating sum = "+rating);
				int cnt = rs.getInt("cnt");
				System.out.println("cnt = "+cnt);
				if (cnt>0)
					obj.setAvgRating(rating/cnt);
				else
					obj.setAvgRating(0);
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				result.addElement(obj);	
			}
	
			rs.close();
			statement.close();
	
			return result;
	
		}
	*/
	/*
		static Vector filterSAExpRatingByClient(ETSMetricsResultObj resobj,ETSParams params) {
			Connection conn = null;
	
			try{
				conn = ETSDBUtils.getConnection();
				return filterSAExpRatingByClient(resobj,params,conn);
			}
			catch (SQLException e) {
				System.out.println("SQL ERROR="+e);
				e.printStackTrace();
				return null;
			}
			catch (Exception ex) {
				System.out.println("EX ERROR="+ex);
				ex.printStackTrace();
				return null;
			}
			finally{
				ETSDBUtils.close(conn);
			}
	
		}
		static Vector filterSAExpRatingByClient(ETSMetricsResultObj resobj,ETSParams params,Connection conn) throws SQLException {
			Vector result = new Vector();
			
			String comp_filter = new String();
			String date_filter = new String();
			String expcat_filter = new String();
			String cldes_filter = new String();
			String ind_filter = new String();
			String geo_filter = new String();
			String notAdminStr = getNotAdminStr(params);
			
			if (!resobj.getSelectedComps().contains("All values")){
				comp_filter = " and p.company in ("+resobj.getSelectedCompsDBStr()+")";
			}
			if (!resobj.getSelectedClientDes().contains("All values")){
				cldes_filter = " and c.designation in ("+resobj.getSelectedClientDesDBStr()+")";
			}
			if (!resobj.getSelectedExpCats().contains("All values")){
				expcat_filter = " and ex.description in ("+resobj.getSelectedExpCatsDBStr()+")";
			}
			if (!resobj.getAllDates()){
				date_filter = " and date(e.last_timestamp)>=date('"+resobj.getFromDateTS()+"') and date(e.last_timestamp) <= date('"+resobj.getToDateTS()+"')"; 	
			}
			if (!resobj.getSelectedInds().contains("All values")){
				ind_filter = " and p.industry in ("+resobj.getSelectedIndsDBStr()+")";
			}
			if (!resobj.getSelectedGeos().contains("All values")){
				geo_filter = " and p.geography in ("+resobj.getSelectedGeosDBStr()+")";
			}
			
			String query = "select c.designation,p.company,p.industry,p.geography,m.state,m.self_id,e.last_timestamp,ex.description,e.rating" +
				" from ets.ets_self_main m, ets.ets_self_exp e,ets.ets_self_data ex,ets.ets_projects p" +
				" left outer join ets.ets_client_desgn c" +
				" on c.company=p.company" +
				" where p.project_id=m.project_id" +
				" and m.self_id=e.self_id"+
				" and m.project_id=e.project_id"+
				" and ex.expect_id=e.expect_id" +
				" and p.project_or_proposal='C'"+
				" and p.project_status!='A'"+
				" and p.project_status!='D'"+
				" and e.section_id=0"+
				comp_filter+
				cldes_filter+
				expcat_filter+
				date_filter+
				notAdminStr+
				ind_filter+
				geo_filter+
				" order by p.company,date(e.last_timestamp) desc,ex.description";
				
				System.out.println(query);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(query);
	
	
			while (rs.next()){
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				obj.setClientDesignation(rs.getString("designation"));
				obj.setProjectCompany(rs.getString("company"));	
				obj.setState(rs.getString("state"));
				obj.setRatingDate(rs.getTimestamp("last_timestamp"));
				obj.setRating(rs.getInt("rating"));
				obj.setExpCat(rs.getString("description"));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				result.addElement(obj);	
			}
	
			rs.close();
			statement.close();
	
			return result;
	
		}
	
	*/
	static Vector filterProjectActivity(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterProjectActivity(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterProjectActivity(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String comp_filter = new String();
		String delTeam_filter = new String();
		String wstype_filter = new String();
		String docdate_filter = new String();
		String metricsdate_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr = " and u.user_id='" + params.es.gIR_USERN + "'";
		}
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedTeams().contains("All values")) {
			delTeam_filter = " and p.delivery_team in (" + resobj.getSelectedTeamsDBStr() + ")";
		}
		if (!resobj.getSelectedWsTypes().contains("All values")) {
			wstype_filter = " and p.project_or_proposal in (" + resobj.getSelectedWsTypesDBStr() + ")";
		}
		if (!resobj.getAllDates()) {
			docdate_filter = " and date(d.doc_upload_date)>=date('" + resobj.getFromDateTS() + "') and date(d.doc_upload_date) <= date('" + resobj.getToDateTS() + "')";
			metricsdate_filter = " and date(m.last_timestamp)>=date('" + resobj.getFromDateTS() + "') and date(m.last_timestamp) <= date('" + resobj.getToDateTS() + "')";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		System.out.println(comp_filter);
		String query =
			"select p.project_or_proposal,p.delivery_team,p.company,p.project_name,p.industry,p.geography,p.project_start,u.user_id,"
				+ " (select count(u1.user_id) from ets.ets_users u1 where u1.user_project_id=p.project_id) as mems,"
				+ " (select count(doc_id) from ets.ets_doc d where d.project_id=p.project_id and doc_type=0 "
				+ docdate_filter
				+ ") as newdocs,"
				+ " (select count(user_id) from ets.ets_metrics m where m.project_id=p.project_id "
				+ metricsdate_filter
				+ ") as hits,"
				+ " (select count(*) from ets.problem_info_cq1 c, ets.problem_info_usr1 u1 where c.edge_problem_id=u1.edge_problem_id and u1.ets_project_id=p.project_id and u1.application_id='ETS' and u1.problem_class='Defect' and c.problem_state in ('Assigned','Rejected')) as issues"
				+ " from ets.ets_projects p, ets.ets_users u, ets.ets_roles r"
				+ " where p.project_id=u.user_project_id"
				+ " and u.user_role_id = r.role_id"
				+ " and r.priv_id = "
				+ Defines.OWNER
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and p.project_or_proposal!='M'"
				+ comp_filter
				+ delTeam_filter
				+ wstype_filter
				+ notAdminStr
				+ ind_filter
				+ geo_filter
				+ " and p.project_type='"
				+ project_type
				+ "' order by p.company,p.project_or_proposal,p.project_name with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setProjectCompany(rs.getString("company"));
				obj.setProjectName(rs.getString("project_name"));
				obj.setProjectOwnerId(rs.getString("user_id"));
				obj.setProjectOwnerName(getLFName(rs.getString("user_id"), conn));
				obj.setWorkspaceType(rs.getString("project_or_proposal"));
				obj.setDeliveryTeam(rs.getString("delivery_team"));
				obj.setMemberCount((new Integer(rs.getInt("mems")).intValue()));
				obj.setNewDocCount((new Integer(rs.getInt("newdocs")).intValue()));
				obj.setHitCount((new Integer(rs.getInt("hits")).intValue()));
				obj.setOpenIssuesCount((new Integer(rs.getInt("issues")).intValue()));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				obj.setCreateDate(rs.getTimestamp("project_start"));
				/*String comp = rs.getString("company");	
				String p_name = rs.getString("project_name");
				String p_oid= rs.getString("user_id");
				String memCnt = (new Integer(rs.getInt("mems"))).toString();
				String docCnt = (new Integer(rs.getInt("newdocs"))).toString();
				obj.setOutputResults(new String[]{comp,p_name,p_oid,memCnt,docCnt});*/
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterDocumentActivity(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterDocumentActivity(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterDocumentActivity(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String comp_filter = new String();
		String delTeam_filter = new String();
		String wstype_filter = new String();
		String docdate_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr = " and u.user_id='" + params.es.gIR_USERN + "'";
		}
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedTeams().contains("All values")) {
			delTeam_filter = " and p.delivery_team in (" + resobj.getSelectedTeamsDBStr() + ")";
		}
		if (!resobj.getSelectedWsTypes().contains("All values")) {
			wstype_filter = " and p.project_or_proposal in (" + resobj.getSelectedWsTypesDBStr() + ")";
		}
		if (!resobj.getAllDates()) {
			docdate_filter = " and date(d.doc_upload_date)>=date('" + resobj.getFromDateTS() + "') and date(d.doc_upload_date) <= date('" + resobj.getToDateTS() + "')";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query =
			"select p.project_or_proposal,p.delivery_team,p.company,p.project_name,p.industry,p.geography,u.user_id,"
				+ " (select count(d.doc_id) from ets.ets_doc d where d.project_id=p.project_id and d.doc_type=0 "
				+ docdate_filter
				+ ") as newdocs,"
				+ " (select sum(f.docfile_size) from ets.ets_doc d,ets.ets_docfile f where d.project_id=p.project_id and d.doc_type=0 and d.doc_id=f.doc_id "
				+ docdate_filter
				+ ") as docsize,"
				+ " (select count(d.doc_id) from ets.ets_doc d,amt.users a,decaf.users de where d.project_id=p.project_id and d.doc_type=0 and d.user_id=a.ir_userid and a.edge_userid=de.userid and de.user_type='I' "
				+ docdate_filter
				+ ") as intpost,"
				+ " (select count(d.doc_id) from ets.ets_doc d,amt.users a,decaf.users de where d.project_id=p.project_id and d.doc_type=0 and d.user_id=a.ir_userid and a.edge_userid=de.userid and de.user_type!='I' "
				+ docdate_filter
				+ ") as extpost,"
				+ " (select count(d.doc_id) from ets.ets_doc d where d.project_id=p.project_id  and d.doc_type=0 and (d.ibm_only='1' or d.ibm_only='2') "
				+ docdate_filter
				+ ") as ibmonlypost"
				+ " from ets.ets_projects p, ets.ets_users u, ets.ets_roles r"
				+ " where p.project_id=u.user_project_id"
				+ " and u.user_role_id = r.role_id"
				+ " and r.priv_id = "
				+ Defines.OWNER
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and p.project_or_proposal!='M'"
				+ notAdminStr
				+ comp_filter
				+ delTeam_filter
				+ wstype_filter
				+ ind_filter
				+ geo_filter
				+ " and p.project_type='"
				+ project_type
				+ "' order by p.company,p.project_or_proposal,p.project_name with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setProjectCompany(rs.getString("company"));
				obj.setProjectName(rs.getString("project_name"));
				obj.setProjectOwnerId(rs.getString("user_id"));
				obj.setProjectOwnerName(getLFName(rs.getString("user_id"), conn));
				obj.setWorkspaceType(rs.getString("project_or_proposal"));
				obj.setDeliveryTeam(rs.getString("delivery_team"));
				obj.setNewDocCount((new Integer(rs.getInt("newdocs")).intValue()));
				obj.setDocSizeSum((new Integer(rs.getInt("docsize")).intValue()));
				obj.setIntPostCount((new Integer(rs.getInt("intpost")).intValue()));
				obj.setExtPostCount((new Integer(rs.getInt("extpost")).intValue()));
				obj.setIBMOnlyPostCount((new Integer(rs.getInt("ibmonlypost")).intValue()));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				/*Vector v = resobj.getColumnsToShow();
				
				for (int i = 0;i<v.size();i++){
						
				}*/
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterIssueActivity(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterIssueActivity(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterIssueActivity(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String comp_filter = new String();
		String delTeam_filter = new String();
		String cldes_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr = " and u.user_id='" + params.es.gIR_USERN + "'";
		}
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedClientDes().contains("All values")) {
			cldes_filter = " and c.designation in (" + resobj.getSelectedClientDesDBStr() + ")";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		/*String query = "select p.delivery_team,p.project_or_proposal,p.company,p.project_name,u.user_id," +
			" count(*) as totalissues," +
			" sum(case when (c1.problem_state in ('Submit','Assigned','Rejected')) then 1 else 0 end) as openissues," +
			" sum(case when (c1.problem_state in ('Submit','Assigned','Rejected') and (u1.severity like '1-%')) then 1 else 0 end) as opensev1issues," +
			" sum(case when (u1.problem_creator=d.userid and d.user_type='I') then 1 else 0 end) as intissues,"+
			" sum(case when (u1.problem_creator=d.userid and d.user_type!='I') then 1 else 0 end) as extissues," +
			" sum(case when (u1.severity like '1-%') then 1 else 0 end) as sev1issues," +
			" sum(case when (c1.problem_state in ('Submit','Assigned','Rejected') and (u1.severity like '2-%')) then 1 else 0 end) as opensev2issues," +
			" sum(case when (u1.severity like '2-%') then 1 else 0 end) as sev2issues," +
			" sum(case when (c1.problem_state in ('Submit','Assigned','Rejected') and (u1.severity not like '1-%' and u1.severity not like '2-%')) then 1 else 0 end) as opensev35issues," +
			" sum(case when (u1.severity not like '1-%' and u1.severity not like '2-%') then 1 else 0 end) as sev35issues" +
			" from ets.ets_users u, ets.ets_roles r,cq.problem_info_cq1 c1, cq.problem_info_usr1 u1,decaf.users d,ets.ets_projects p" +
			" where p.project_id=u.user_project_id" +
			" and c1.edge_problem_id = u1.edge_problem_id" +
			" and u1.ets_project_id=p.project_id" +
			" and u.user_role_id = r.role_id" +
			" and r.priv_id = 8" +
			comp_filter+
			delTeam_filter+
			" group by p.delivery_team,p.project_or_proposal,p.company,p.project_name,u.user_id";*/
		String query =
			"with"
				+ " issues(proj_id,totalissues,openissues,intissues,extissues,opensev1issues,sev1issues,opensev2issues,sev2issues,opensev35issues,sev35issues) as"
				+ " (select p.project_id,"
				+ " count(*) as totalissues,"
				+ " sum(case when (c1.problem_state in ('Submit','Assigned','Rejected')) then 1 else 0 end) as openissues,"
				+ " sum(case when (u1.problem_creator=d.userid and d.user_type='I') then 1 else 0 end) as intissues,"
				+ " sum(case when (u1.problem_creator=d.userid and d.user_type!='I') then 1 else 0 end) as exissues,"
				+ " sum(case when (c1.problem_state in ('Submit','Assigned','Rejected') and (u1.severity like '1-%')) then 1 else 0 end) as opensev1issues,"
				+ " sum(case when (u1.severity like '1-%') then 1 else 0 end) as sev1issues,"
				+ " sum(case when (c1.problem_state in ('Submit','Assigned','Rejected') and (u1.severity like '2-%')) then 1 else 0 end) as opensev2issues, "
				+ " sum(case when (u1.severity like '2-%') then 1 else 0 end) as sev2issues,"
				+ " sum(case when (c1.problem_state in ('Submit','Assigned','Rejected') and (u1.severity not like '1-%' and u1.severity not like '2-%')) then 1 else 0 end) as opensev35issues,"
				+ " sum(case when (u1.severity not like '1-%' and u1.severity not like '2-%') then 1 else 0 end) as sev35issues"
				+ " from ets.problem_info_usr1 u1,ets.problem_info_cq1 c1, decaf.users d, ets.ets_projects p"
				+ " where u1.application_id='ETS'"
				+ " and u1.edge_problem_id=c1.edge_problem_id"
				+ " and u1.ets_project_id=p.project_id"
				+ " and u1.problem_creator=d.userid"
				+ " and u1.problem_class='Defect'"
				+ " and p.project_type='"
				+ project_type
				+ "' group by p.project_id)"
				+ " select p.delivery_team,p.project_or_proposal,p.company,p.project_name,p.industry,p.geography,u.user_id,"
				+ " value((select totalissues from issues where proj_id=p.project_id),0) as totalissues,"
				+ " value((select openissues from issues where proj_id=p.project_id),0) as openissues,"
				+ " value((select intissues from issues where proj_id=p.project_id),0) as intissues,"
				+ " value((select extissues from issues where proj_id=p.project_id),0) as extissues,"
				+ " value((select opensev1issues from issues where proj_id=p.project_id),0) as opensev1issues,"
				+ " value((select sev1issues from issues where proj_id=p.project_id),0) as sev1issues,"
				+ " value((select opensev2issues from issues where proj_id=p.project_id),0) as opensev2issues,"
				+ " value((select sev2issues from issues where proj_id=p.project_id),0) as sev2issues,"
				+ " value((select opensev35issues from issues where proj_id=p.project_id),0) as opensev35issues,"
				+ " value((select sev35issues from issues where proj_id=p.project_id),0) as sev35issues"
				+ " from issues,ets.ets_users u, ets.ets_roles r,ets.ets_projects p"
				+ " left outer join ets.ets_client_desgn c"
				+ " on c.company=p.company"
				+ " where p.project_id=u.user_project_id"
				+ " and issues.proj_id=p.project_id"
				+ " and u.user_role_id = r.role_id"
				+ " and r.priv_id = "
				+ Defines.OWNER
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and p.project_or_proposal!='M'"
				+ comp_filter
				+ cldes_filter
				+ notAdminStr
				+ ind_filter
				+ geo_filter
				+ " and p.project_type='"
				+ project_type
				+ "' group by p.delivery_team,p.project_or_proposal,p.company,p.project_name,p.project_id,p.industry,p.geography,u.user_id"
				+ " having value((select totalissues from issues where proj_id=p.project_id),0) >0"
				+ " order by p.company,p.project_name with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setDeliveryTeam(rs.getString("delivery_team"));
				obj.setProjectCompany(rs.getString("company"));
				obj.setProjectName(rs.getString("project_name"));
				obj.setProjectOwnerId(rs.getString("user_id"));
				obj.setProjectOwnerName(getLFName(rs.getString("user_id"), conn));
				obj.setOpenIssuesCount(rs.getInt("openissues"));
				obj.setIssuesCount(rs.getInt("totalissues"));
				obj.setIntPostCount(rs.getInt("intissues"));
				obj.setExtPostCount(rs.getInt("extissues"));
				obj.setOpenSev1IssuesCount(rs.getInt("opensev1issues"));
				obj.setSev1IssuesCount(rs.getInt("sev1issues"));
				obj.setOpenSev2IssuesCount(rs.getInt("opensev2issues"));
				obj.setSev2IssuesCount(rs.getInt("sev2issues"));
				obj.setOpenSev35IssuesCount(rs.getInt("opensev35issues"));
				obj.setSev35IssuesCount(rs.getInt("sev35issues"));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	public static Vector filterIssueActivityDetails(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterIssueActivityDetails(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	public static Vector filterIssueActivityDetails(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String comp_filter = new String();
		String cldes_filter = new String();
		String status_filter = new String();
		String date_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String intext_filter = new String();
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			System.out.println("not admin");
			notAdminStr = " and u.user_id='" + params.es.gIR_USERN + "'";
		} else {
			System.out.println("Admin");
		}
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedIssueStatus().contains("All values")) {
			status_filter = " and c1.problem_state in (" + resobj.getSelectedIssueStatusDBStr() + ")";
		}
		if (!resobj.getSelectedClientDes().contains("All values")) {
			cldes_filter = " and c.designation in (" + resobj.getSelectedClientDesDBStr() + ")";
		}
		if (!resobj.getAllDates()) {
			date_filter = " and date(u1.creation_date)>=date('" + resobj.getFromDateTS() + "') and date(u1.creation_date) <= date('" + resobj.getToDateTS() + "')";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		if (!resobj.getSelectedIntExt().equals("0")) {
			if (resobj.getSelectedIntExt().equals("1"))
				intext_filter = " and d.user_type='I'";
			else
				intext_filter = " and d.user_type!='I'";
		}
		/*String query = "select p.company,p.project_name,u.user_id,c1.problem_state,u1.severity," +
			" (date(current timestamp)-date(u1.creation_date)) as age," +
			" u1.creation_date,u1.last_timestamp,u1.problem_type,o.owner_id,c1.title," +
			" d.user_type" +
			" from ets.ets_users u, ets.ets_roles r,cq.problem_info_cq1 c1, cq.problem_info_usr1 u1," +
			" cq.ets_owner_cq o,decaf.users d,ets.ets_projects p" +
			" left outer join ets.ets_client_desgn c" +
			" on c.company=p.company"+
			" where p.project_id=u.user_project_id" +
			" and c1.edge_problem_id = u1.edge_problem_id" +
			" and c1.edge_problem_id=o.edge_problem_id" +
			" and u1.ets_project_id=p.project_id" +
			" and d.userid=u1.problem_creator" +
			" and u.user_role_id = r.role_id" +
			" and r.priv_id = "+Defines.OWNER+
			" and p.project_or_proposal!='M'" +
			" and p.project_status!='A'" +
			" and p.project_status!='D'" +
			comp_filter+
			cldes_filter+
			date_filter+
			status_filter+
			notAdminStr+
			" order by p.company,p.project_name";*/
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query =
			"select p.company,p.project_name,p.industry,p.geography,u.user_id,c1.problem_state,u1.severity,c1.cq_trk_id, "
				+ " case when c1.problem_state='Closed' or c1.problem_state='Resolved' then (timestampdiff(16,char(u1.last_timestamp-u1.creation_date))) else (timestampdiff(16,char(current timestamp-u1.creation_date))) end as age,"
				+ " u1.creation_date,u1.last_timestamp,it.issuetype,o.owner_id,c1.title,"
				+ " d.user_type"
				+ " from ets.ets_users u, ets.ets_roles r,ets.ets_dropdown_data it, ets.problem_info_cq1 c1,"
				+ " decaf.users d,ets.ets_projects p"
				+ " left outer join ets.ets_client_desgn c"
				+ " on c.company=p.company,"
				+ " ets.problem_info_usr1 u1"
				+ " left outer join ets.ets_owner_cq o"
				+ " on u1.edge_problem_id = o.edge_problem_id"
				+ " where p.project_id=u.user_project_id"
				+ " and c1.edge_problem_id = u1.edge_problem_id"
				+ " and u1.ets_project_id=p.project_id"
				+ " and d.userid=u1.problem_creator"
				+ " and u.user_role_id = r.role_id"
				+ " and r.priv_id = "
				+ Defines.OWNER
				+ " and p.project_or_proposal!='M'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and u1.problem_class='Defect'"
				+ " and u1.issue_type_id = it.data_id"
				+ comp_filter
				+ cldes_filter
				+ date_filter
				+ status_filter
				+ notAdminStr
				+ ind_filter
				+ geo_filter
				+ intext_filter
				+ " and p.project_type='"
				+ project_type
				+ "' order by p.company,p.project_name with ur";
		
		if (logger.isDebugEnabled()) {
			logger.debug(query);
		} 
		
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setProjectCompany(rs.getString("company"));
				obj.setProjectName(rs.getString("project_name"));
				obj.setProjectOwnerId(rs.getString("user_id"));
				obj.setProjectOwnerName(getLFName(rs.getString("user_id"), conn));
				obj.setIssueStatus(rs.getString("problem_state"));
				obj.setIssueSeverity(rs.getString("severity"));
				obj.setIssueAge(rs.getInt("age"));
				obj.setIssueCreateDate(rs.getTimestamp("creation_date"));
				obj.setLastActivity(rs.getTimestamp("last_timestamp"));
				//obj.setIssueProblemtype(rs.getString("problem_type"));
				obj.setIssueProblemtype(rs.getString("issuetype"));
				String own = rs.getString("owner_id");
				if (own == null) {
					own = "NEW";
				}
				obj.setOwnerId(own);
				if (!own.equals("NEW"))
					obj.setOwnerName(getLFNameForEdgeId(rs.getString("owner_id"), conn));
				else
					obj.setOwnerName("NEW");
				obj.setIssueTitle(rs.getString("title"));
				obj.setUserType(rs.getString("user_type"));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				obj.setIssueNumber(rs.getString("cq_trk_id"));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterMembershipDistribution(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterMembershipDistribution(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterMembershipDistribution(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String comp_filter = new String();
		String delTeam_filter = new String();
		String wstype_filter = new String();
		String docdate_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr = " and u.user_id='" + params.es.gIR_USERN + "'";
		}
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedTeams().contains("All values")) {
			delTeam_filter = " and p.delivery_team in (" + resobj.getSelectedTeamsDBStr() + ")";
		}
		if (!resobj.getSelectedWsTypes().contains("All values")) {
			wstype_filter = " and p.project_or_proposal in (" + resobj.getSelectedWsTypesDBStr() + ")";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		System.out.println(comp_filter);
		String query =
			"select p.project_or_proposal,p.delivery_team,p.company,p.project_name,p.industry,p.geography,u.user_id,"
				+ " (select count(u1.user_id) from ets.ets_users u1 where u1.user_project_id=p.project_id and u1.active_flag='A') as mems,"
				+ " (select count(u2.user_id) from ets.ets_users u2,decaf.users d1,amt.users a1 where u2.user_project_id=p.project_id"
				+ " and u2.user_id=a1.ir_userid and a1.edge_userid=d1.userid and d1.user_type='I' and u2.active_flag='A') as intmems,"
				+ " (select count(u3.user_id) from ets.ets_users u3,decaf.users d2,amt.users a2 where u3.user_project_id=p.project_id"
				+ " and u3.user_id=a2.ir_userid and a2.edge_userid=d2.userid and d2.user_type!='I' and u3.active_flag='A') as extmems"
				+ " from ets.ets_projects p, ets.ets_users u,ets.ets_roles r"
				+ " where p.project_id=u.user_project_id"
				+ " and u.user_role_id = r.role_id"
				+ " and r.priv_id = "
				+ Defines.OWNER
				+ " and p.project_or_proposal!='M'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and u.active_flag='A'"
				+ comp_filter
				+ delTeam_filter
				+ wstype_filter
				+ notAdminStr
				+ ind_filter
				+ geo_filter
				+ " and p.project_type='"
				+ project_type
				+ "' order by p.company,p.project_or_proposal,p.project_name with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setProjectCompany(rs.getString("company"));
				obj.setProjectName(rs.getString("project_name"));
				obj.setProjectOwnerId(rs.getString("user_id"));
				obj.setProjectOwnerName(getLFName(rs.getString("user_id"), conn));
				obj.setWorkspaceType(rs.getString("project_or_proposal"));
				obj.setDeliveryTeam(rs.getString("delivery_team"));
				obj.setMemberCount((new Integer(rs.getInt("mems")).intValue()));
				obj.setIntMemberCount((new Integer(rs.getInt("intmems")).intValue()));
				obj.setExtMemberCount((new Integer(rs.getInt("extmems")).intValue()));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterUsageSummary(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterUsageSummary(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterUsageSummary(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String comp_filter = new String();
		String delTeam_filter = new String();
		String wstype_filter = new String();
		String metricsdate_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr = " and u.user_id='" + params.es.gIR_USERN + "'";
		}
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedTeams().contains("All values")) {
			delTeam_filter = " and p.delivery_team in (" + resobj.getSelectedTeamsDBStr() + ")";
		}
		if (!resobj.getSelectedWsTypes().contains("All values")) {
			wstype_filter = " and p.project_or_proposal in (" + resobj.getSelectedWsTypesDBStr() + ")";
		}
		if (!resobj.getAllDates()) {
			metricsdate_filter = " and date(m.last_timestamp)>=date('" + resobj.getFromDateTS() + "') and date(m.last_timestamp) <= date('" + resobj.getToDateTS() + "')";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query =
			"select p.project_or_proposal,p.delivery_team,p.company,p.project_name,p.industry,p.geography,u.user_id,"
				+ " (select count(m.user_id) from ets.ets_metrics m where m.project_id=p.project_id "
				+ metricsdate_filter
				+ "  ) as total,"
				+ " (select count(m.user_id) from ets.ets_metrics m,amt.users a,decaf.users d where m.project_id=p.project_id and m.user_id=a.ir_userid and a.edge_userid=d.userid and d.user_type='I' "
				+ metricsdate_filter
				+ " ) as inttotal,"
				+ " (select count(m.user_id) from ets.ets_metrics m,amt.users a,decaf.users d where m.project_id=p.project_id and m.user_id=a.ir_userid and a.edge_userid=d.userid and d.user_type!='I' "
				+ metricsdate_filter
				+ " ) as exttotal"
				+ " from ets.ets_projects p, ets.ets_users u, ets.ets_roles r"
				+ " where p.project_id=u.user_project_id"
				+ " and u.user_role_id = r.role_id"
				+ " and r.priv_id = "
				+ Defines.OWNER
				+ " and p.project_or_proposal!='M'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ comp_filter
				+ delTeam_filter
				+ wstype_filter
				+ notAdminStr
				+ ind_filter
				+ geo_filter
				+ " and p.project_type='"
				+ project_type
				+ "' order by p.company,p.project_or_proposal,p.project_name with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setProjectCompany(rs.getString("company"));
				obj.setProjectName(rs.getString("project_name"));
				obj.setProjectOwnerId(rs.getString("user_id"));
				obj.setProjectOwnerName(getLFName(rs.getString("user_id"), conn));
				obj.setWorkspaceType(rs.getString("project_or_proposal"));
				obj.setDeliveryTeam(rs.getString("delivery_team"));
				obj.setHitCount((new Integer(rs.getInt("total")).intValue()));
				obj.setIntHitCount((new Integer(rs.getInt("inttotal")).intValue()));
				obj.setExtHitCount((new Integer(rs.getInt("exttotal")).intValue()));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterUsageDetails(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterUsageDetails(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterUsageDetails(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String comp_filter = new String();
		String delTeam_filter = new String();
		String wstype_filter = new String();
		String tabname_filter = new String();
		String metricsdate_filter = new String();
		String ind_filter = new String();
		String geo_filter = new String();
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr = " and o.user_id='" + params.es.gIR_USERN + "'";
		}
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedTeams().contains("All values")) {
			delTeam_filter = " and p.delivery_team in (" + resobj.getSelectedTeamsDBStr() + ")";
		}
		if (!resobj.getSelectedWsTypes().contains("All values")) {
			wstype_filter = " and p.project_or_proposal in (" + resobj.getSelectedWsTypesDBStr() + ")";
		}
		if (!resobj.getSelectedTabNames().contains("All values")) {
			tabname_filter = " and c.cat_name in (" + resobj.getSelectedTabNamesDBStr() + ")";
		}
		if (!resobj.getAllDates()) {
			metricsdate_filter = " and date(m.last_timestamp)>=date('" + resobj.getFromDateTS() + "') and date(m.last_timestamp) <= date('" + resobj.getToDateTS() + "')";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		/*
		String query = "select p.project_or_proposal, p.delivery_team, p.company, p.project_name, p.industry, p.geography,u.user_id, c.cat_name,"+
		" (select count(*) from ets.ets_metrics m where m.project_id=p.project_id and m.tab_name=c.cat_name"+metricsdate_filter+") as total," +
		" (select count(*) from ets.ets_metrics m,amt.users a,decaf.users d where m.project_id=p.project_id and m.tab_name=c.cat_name and m.user_id=a.ir_userid and a.edge_userid=d.userid and d.user_type='I'"+metricsdate_filter+") as inttotal," +
		" (select count(*) from ets.ets_metrics m,amt.users a,decaf.users d where m.project_id=p.project_id and m.tab_name=c.cat_name and m.user_id=a.ir_userid and a.edge_userid=d.userid and d.user_type!='I'"+metricsdate_filter+") as exttotal" +
		" from ets.ets_projects p, ets.ets_users u, ets.ets_users u2, ets.ets_cat c,ets.ets_metrics m,ets.ets_roles r" +
		" where p.project_id=u.user_project_id" +
		" and u.user_project_id=c.project_id" +
		" and c.cat_type=0" +
		" and m.project_id=c.project_id" +
		" and u.user_role_id = r.role_id" +
		" and r.priv_id = "+Defines.OWNER +
		" and p.project_or_proposal!='M'" +
		" and p.project_status!='A'" +
		" and p.project_status!='D'" +
		comp_filter +
		delTeam_filter +
		wstype_filter +
		tabname_filter +
		notAdminStr+
		ind_filter+
		geo_filter+
		" group by p.project_or_proposal,p.delivery_team,p.company,p.project_name,u.user_id,c.cat_name,p.project_id,p.industry,p.geography" +
		" order by p.company,p.project_or_proposal,p.project_name,c.cat_name";
		*/
		String query =
			"with p as"
				+ " ("
				+ " select p.project_id, p.project_or_proposal, p.delivery_team, p.company, p.project_name, p.industry, p.geography"
				+ " from   ets.ets_projects p"
				+ " where  p.project_or_proposal!='M'"
				+ " and    p.project_status!='A'"
				+ " and    p.project_status!='D'"
				+ comp_filter
				+ delTeam_filter
				+ wstype_filter
				+ ind_filter
				+ geo_filter
				+ " and p.project_type='"
				+ project_type
				+ "' ),"
				+ " c as"
				+ " ("
				+ " select c.cat_name, c.project_id"
				+ " from   ets.ets_cat c"
				+ " where  c.cat_type=0"
				+ tabname_filter
				+ " ),"
				+ " o as"
				+ " (select o.user_id,o.user_project_id,o.user_role_id"
				+ " from ets.ets_users o"
				+ " where o.user_role_id in (select role_id from ets.ets_roles where role_id=o.user_role_id and priv_id="
				+ Defines.OWNER
				+ ")"
				+ notAdminStr
				+ ")"
				+ " select SUM(CASE WHEN du.user_type  = 'I' THEN 1 ELSE 0 END) inttotal,"
				+ " SUM(CASE WHEN du.user_type != 'I' THEN 1 ELSE 0 END) exttotal,"
				+ " SUM(CASE WHEN du.userid != '' THEN 1 ELSE 0 END) total,"
				+ " p.project_or_proposal, p.delivery_team, p.company, p.project_name, p.industry, p.geography, o.user_id, c.cat_name"
				+ " from   ets.ets_metrics m,p,amt.users au,decaf.users du,c,ets.ets_users u,o"
				+ " where  m.project_id = p.project_id"
				+ " and u.user_project_id=p.project_id"
				+ " and m.user_id = au.ir_userid"
				+ " and m.user_id=u.user_id"
				+ " and p.project_id=u.user_project_id"
				+ " and u.user_id = au.ir_userid"
				+ " and au.edge_userid = du.userid"
				+ " and u.user_project_id=c.project_id"
				+ " and m.tab_name = c.cat_name"
				+ " and o.user_project_id=m.project_id"
				+ metricsdate_filter
				+ " group by p.project_or_proposal,p.delivery_team,p.company,p.project_name,o.user_id,c.cat_name,p.project_id,p.industry,p.geography"
				+ " order by p.company,p.project_or_proposal,p.project_name,c.cat_name"
				+ " with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setProjectCompany(rs.getString("company"));
				obj.setProjectName(rs.getString("project_name"));
				obj.setProjectOwnerId(rs.getString("user_id"));
				obj.setProjectOwnerName(getLFName(rs.getString("user_id"), conn));
				obj.setWorkspaceType(rs.getString("project_or_proposal"));
				obj.setDeliveryTeam(rs.getString("delivery_team"));
				obj.setTabName(rs.getString("cat_name"));
				obj.setHitCount((new Integer(rs.getInt("total")).intValue()));
				obj.setIntHitCount((new Integer(rs.getInt("inttotal")).intValue()));
				obj.setExtHitCount((new Integer(rs.getInt("exttotal")).intValue()));
				obj.setInds(rs.getString("industry"));
				obj.setGeos(rs.getString("geography"));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterIssueTabMetrics(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterIssueTabMetrics(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterIssueTabMetrics(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String wkspc_filter = new String();
		String date_filter = new String();
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr = " and u.user_id='" + params.es.gIR_USERN + "'";
		}
		if (!resobj.getSelectedWorkspaces().contains("0All values0")) {
			wkspc_filter = " and p.project_id in (" + resobj.getSelectedWorkspacesDBStr() + ")";
		}
		if (!resobj.getAllDates()) {
			date_filter = " and date(u1.creation_date)>=date('" + resobj.getFromDateTS() + "') and date(u1.creation_date) <= date('" + resobj.getToDateTS() + "')";
		}
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query =
			"select p.project_name,"
				+ " sum(case when (c1.problem_class='Defect') then 1 else 0 end) as issues,"
				+ " sum(case when (c1.problem_class='Change') then 1 else 0 end) as changes,"
				+ " sum(case when (c1.problem_class='Feedback') then 1 else 0 end) as feedbacks"
				+ " from ets.problem_info_usr1 u1,ets.ets_projects p,ets.problem_info_cq1 c1,ets.ets_users u, ets.ets_roles r"
				+ " where c1.edge_problem_id = u1.edge_problem_id"
				+ " and u1.ets_project_id=p.project_id"
				+ " and u1.application_id='ETS'"
				+ " and u1.ets_project_id=p.project_id"
				+ " and p.project_or_proposal!='M'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and p.project_id=u.user_project_id"
				+ " and u.user_role_id = r.role_id"
				+ " and r.priv_id = "
				+ Defines.OWNER
				+ wkspc_filter
				+ date_filter
				+ notAdminStr
				+ " and p.project_type='"
				+ project_type
				+ "' group by p.project_name"
				+ " order by p.project_name with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setProjectName(rs.getString("project_name"));
				obj.setIssuesCount((new Integer(rs.getInt("issues")).intValue()));
				obj.setChangesCount((new Integer(rs.getInt("changes")).intValue()));
				obj.setFeedbacksCount((new Integer(rs.getInt("feedbacks")).intValue()));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterTeamListing(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterTeamListing(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterTeamListing(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String wkspc_filter = new String();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr =
				" and p.project_id in (select pp.project_id   "
					+ " from ETS.ETS_Projects pp, ets.ets_users uu, ets.ets_roles rr"
					+ " where pp.project_status!='A'"
					+ " and pp.project_status!='D'"
					+ " and pp.project_id=uu.user_project_id"
					+ " and uu.user_role_id=rr.role_id"
					+ " and rr.priv_id="
					+ Defines.OWNER
					+ " and uu.active_flag='A'"
					+ " and uu.user_id='"
					+ params.es.gIR_USERN
					+ "' and pp.project_type='"
					+ project_type
					+ "' )";
		}
		if (!resobj.getSelectedWorkspaces().contains("0All values0")) {
			wkspc_filter = " and p.project_id in (" + resobj.getSelectedWorkspacesDBStr() + ")";
		}
		String query =
			"select distinct p.project_name,a.user_lname,a.user_fname,u.user_id,a.user_email,"
				+ " a.last_logon,a.no_of_logons,r.role_name"
				+ " from ets.ets_projects p,ets.ets_users u,ets.ets_roles r, amt.users a"
				+ " where u.user_role_id=r.role_id"
				+ " and u.user_project_id=r.project_id"
				+ " and u.user_project_id=p.project_id"
				+ " and u.user_id=a.ir_userid"
				+ " and p.project_or_proposal!='M'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and u.active_flag='A'"
				+ wkspc_filter
				+ notAdminStr
				+ " and p.project_type='"
				+ project_type
				+ "' order by p.project_name,a.user_lname,a.user_fname,u.user_id with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setProjectName(rs.getString("project_name"));
				obj.setUserName(rs.getString("user_lname"), rs.getString("user_fname"));
				obj.setUserId(rs.getString("user_id"));
				obj.setUserEmail(rs.getString("user_email"));
				obj.setLastLogon(rs.getTimestamp("last_logon"));
				obj.setLogonCount(rs.getInt("no_of_logons"));
				obj.setRoleName(rs.getString("role_name"));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterTeamMembership(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterTeamMembership(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterTeamMembership(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String user_filter = new String();
		String roles_filter = new String();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr =
				" and p.project_id in (select pp.project_id   "
					+ " from ETS.ETS_Projects pp, ets.ets_users uu, ets.ets_roles rr"
					+ " where pp.project_status!='A'"
					+ " and pp.project_status!='D'"
					+ " and pp.project_id=uu.user_project_id"
					+ " and uu.user_role_id=rr.role_id"
					+ " and rr.priv_id="
					+ Defines.OWNER
					+ " and uu.active_flag='A'"
					+ " and uu.user_id='"
					+ params.es.gIR_USERN
					+ "' and pp.project_type='"
					+ project_type
					+ "' )";
		}
		if (!resobj.getSelectedUsers().contains("0All values0")) {
			user_filter = " and u.user_id in (" + resobj.getSelectedUsersDBStr() + ")";
		}
		if (!resobj.getSelectedRoles().contains("All values")) {
			roles_filter = " and r.role_name in (" + resobj.getSelectedRolesDBStr() + ")";
		}
		String query =
			"select distinct p.project_name,a.user_lname,a.user_fname,u.user_id,a.user_email,"
				+ " r.role_name"
				+ " from ets.ets_projects p,ets.ets_users u,ets.ets_roles r, amt.users a"
				+ " where u.user_role_id=r.role_id"
				+ " and u.user_project_id=r.project_id"
				+ " and u.user_project_id=p.project_id"
				+ " and u.user_id=a.ir_userid"
				+ " and p.project_or_proposal!='M'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and u.active_flag='A'"
				+ notAdminStr
				+ user_filter
				+ roles_filter
				+ " and p.project_type='"
				+ project_type
				+ "' order by a.user_lname,a.user_fname,u.user_id,p.project_name with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setProjectName(rs.getString("project_name"));
				obj.setUserName(rs.getString("user_lname"), rs.getString("user_fname"));
				obj.setUserId(rs.getString("user_id"));
				obj.setUserEmail(rs.getString("user_email"));
				obj.setRoleName(rs.getString("role_name"));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterBladeDocHits(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterBladeDocHits(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterBladeDocHits(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String date_filter = new String();
		String wdate_filter = new String();
		if (!resobj.getAllDates()) {
			date_filter = " and date(timestamp)>=date('" + resobj.getFromDateTS() + "') and date(timestamp) <= date('" + resobj.getToDateTS() + "')";
			//wdate_filter = " where date(timestamp)>=date('"+resobj.getFromDateTS()+"') and date(timestamp) <= date('"+resobj.getToDateTS()+"')"; 	
		}
		/*String query = "select title,count(ir_userid) as cnt from tgcm.cm_usage"+
			" where title like '%:BaseS%' and node_type='f'"+
			date_filter+
			" group by title";
		*/
		String query =
			"select c.title,count(*) as cnt,"
				+ " (select count(*) from tgcm.cm_usage c2,amt.users a,decaf.users d"
				+ " where c2.ir_userid=a.ir_userid"
				+ " and c2.title=c.title"
				+ " and a.edge_userid=d.userid"
				+ " and d.user_type='I'"
				+ date_filter
				+ ") as inttotal,"
				+ " (select count(*) from tgcm.cm_usage c2,amt.users a,decaf.users d"
				+ " where c2.ir_userid=a.ir_userid"
				+ " and c2.title=c.title"
				+ " and a.edge_userid=d.userid"
				+ " and d.user_type!='I'"
				+ date_filter
				+ ") as exttotal"
				+ " from tgcm.cm_usage c"
				+ " where c.title like '%:BaseS%' and node_type='f'"
				+ date_filter
				+ " group by c.title with ur";
		/*String query = "select c.title,count(*) as cnt," +
			" (select count(*) from tgcm.cm_usage c2,amt.users a,decaf.users d" +
			" where c2.ir_userid=a.ir_userid" +
			" and c2.title=c.title" +
			" and a.edge_userid=d.userid" +
			" and d.user_type='I'" +
			date_filter+") as inttotal," +
			" (select count(*) from tgcm.cm_usage c2,amt.users a,decaf.users d" +
			" where c2.ir_userid=a.ir_userid" +
			" and c2.title=c.title" +
			" and a.edge_userid=d.userid" +
			" and d.user_type!='I'" +
			date_filter+") as exttotal" +
			" from tgcm.cm_usage c" +
			wdate_filter+
			" group by c.title";
		*/
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsWSObj obj = new ETSMetricsWSObj();
				obj.setTitle(rs.getString("title"));
				obj.setHitCount((new Integer(rs.getInt("cnt")).intValue()));
				obj.setIntHitCount((new Integer(rs.getInt("inttotal")).intValue()));
				obj.setExtHitCount((new Integer(rs.getInt("exttotal")).intValue()));
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterBladeLicenseActivity(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterBladeLicenseActivity(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterBladeLicenseActivity(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String date_filter = new String();
		if (!resobj.getAllDates()) {
			date_filter = " where date(b.last_timestamp)>=date('" + resobj.getFromDateTS() + "') and date(b.last_timestamp) <= date('" + resobj.getToDateTS() + "')";
		}
		String query = "select b.ir_userid as bid,b.last_timestamp as blt,a.*, " + "(select count(a.ir_userid) from amt.users a where a.ir_userid=b.ir_userid) as cnt " + "from blade.blade_clickaccp_license b " + "left outer join amt.users a on b.ir_userid=a.ir_userid " + date_filter + "order by b.last_timestamp with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsBladeObj obj = new ETSMetricsBladeObj();
				String id = getResultString(rs.getString("bid"));
				obj.setUserid(id);
				obj.setSal(getResultString(rs.getString("user_sal")));
				obj.setFname(getResultString(rs.getString("user_fname")));
				obj.setLname(getResultString(rs.getString("user_lname")));
				obj.setSuffix(getResultString(rs.getString("user_generation")));
				obj.setEmail(getResultString(rs.getString("user_email")));
				obj.setDayPhone(getResultString(rs.getString("user_phone")));
				obj.setFax(getResultString(rs.getString("user_fax")));
				obj.setEvenPhone(getResultString(rs.getString("user_homephone")));
				obj.setPagmobPhone(getResultString(rs.getString("user_pager")));
				obj.setJobTitle(getResultString(rs.getString("user_jobtitle")));
				obj.setStAddr1(getResultString(rs.getString("company_addr1")));
				obj.setStAddr2(getResultString(rs.getString("company_addr2")));
				obj.setCity(getResultString(rs.getString("company_city")));
				obj.setStprov(getResultString(rs.getString("company_statecode")));
				obj.setPostcode(getResultString(rs.getString("company_postcode")));
				obj.setCountry(getResultString(rs.getString("user_cntry")));
				obj.setCompany(getResultString(rs.getString("user_company")));
				obj.setLicenseDate(rs.getTimestamp("blt"));
				int i = rs.getInt("cnt");
				if (i < 1) {
					obj.setRevoked(true);
					obj = getAmtProfile(id, obj, conn);
				}
				result.addElement(obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	private static ETSMetricsBladeObj getAmtProfile(String id, ETSMetricsBladeObj obj, Connection conn) {
		AmtUserProfile usrprofile = null;
		AmtProfileDAObject dbprofile = null;
		usrprofile = new AmtUserProfile();
		dbprofile = new AmtProfileDAObject();
		AmtUserProfileFactory prffactory = new AmtUserProfileFactory();
		//id = "ibmuser@icc.com";
		usrprofile = prffactory.initiateUserProfile("webid").getUserProfile(conn, id);
		if (usrprofile.getPullprofile() != 1) {
			obj.setSal(usrprofile.getSalTn());
			obj.setFname(usrprofile.getFirstName());
			obj.setLname(usrprofile.getLastName());
			obj.setSuffix(usrprofile.getSubTitle());
			obj.setEmail(usrprofile.getEmailId());
			obj.setDayPhone(usrprofile.getOffPhNum());
			obj.setFax(usrprofile.getFaxPhNum());
			obj.setEvenPhone(usrprofile.getEvePhNum());
			obj.setPagmobPhone(usrprofile.getPagPhNum());
			obj.setJobTitle(usrprofile.getJobTitle());
			obj.setStAddr1(usrprofile.getOffAddr_street());
			obj.setStAddr2(usrprofile.getOffAddr_street2());
			obj.setCity(usrprofile.getOffAddr_city());
			obj.setStprov(usrprofile.getOffAddr_state());
			obj.setPostcode(usrprofile.getOffAddr_code());
			obj.setCountry(usrprofile.getOffAddr_cntry());
			obj.setCompany(usrprofile.getCompName());
		}
		return obj;
	}
	static Vector filterAvgSatData(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterAvgSatData(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterAvgSatData(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String geo_filter = new String();
		String ind_filter = new String();
		String delteam_filter = new String();
		String user_table = new String();
		String adv_filter = new String();
		String owner_filter = new String();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr =
				" and p.company in (select pp.company   "
					+ " from ETS.ETS_Projects pp, ets.ets_users uu, ets.ets_roles rr"
					+ " where pp.project_status!='A'"
					+ " and pp.project_status!='D'"
					+ " and pp.project_id=uu.user_project_id"
					+ " and uu.user_role_id=rr.role_id"
					+ " and rr.priv_id="
					+ Defines.OWNER
					+ " and uu.active_flag='A'"
					+ " and uu.user_id='"
					+ params.es.gIR_USERN
					+ "' and pp.project_type='"
					+ project_type
					+ "' )";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		if (!resobj.getSelectedTeams().contains("All values")) {
			delteam_filter = " and p.delivery_team in (" + resobj.getSelectedTeamsDBStr() + ")";
		}
		if (!resobj.getSelectedAdvocate().contains("0All values0")) {
			user_table = ",ets.ets_users u";
			adv_filter = " and p.project_id = u.user_project_id" + " and u.primary_contact='Y'" + " and u.user_id in (" + resobj.getSelectedAdvocateDBStr() + ")";
		}
		if (!resobj.getSelectedWSOwner().contains("0All values0")) {
			user_table = ",ets.ets_users u,ets.ets_roles r";
			owner_filter = " and p.project_id = u.user_project_id" + " and u.user_role_id=r.role_id" + " and r.priv_id=" + Defines.OWNER + " and u.user_id in (" + resobj.getSelectedWSOwnerDBStr() + ")";
		}
		String[][] qtr = getQuarterString(9);
		String query =
			"select"
				+ " avg(case when (date(s.last_timestamp) >= date('"
				+ qtr[0][0]
				+ "')) then cast(rating as double) end) as selfcurrent,"
				+ " avg(case when (date(q.last_timestamp) >= date('"
				+ qtr[0][0]
				+ "')) then cast(q.final_rating as double) end) as setcurrent,"
				+ " avg(case when (date(s.last_timestamp)>= date('"
				+ qtr[1][0]
				+ "') and date(s.last_timestamp)<= date('"
				+ qtr[1][1]
				+ "')) then cast(s.rating as double) end) as selfprev,"
				+ " avg(case when (date(q.last_timestamp)>= date('"
				+ qtr[1][0]
				+ "') and date(q.last_timestamp)<= date('"
				+ qtr[1][1]
				+ "')) then cast(q.final_rating as double) end) as setprev,"
				+ " avg(case when (date(s.last_timestamp)>= date('"
				+ qtr[2][0]
				+ "') and date(s.last_timestamp)<= date('"
				+ qtr[2][1]
				+ "')) then cast(s.rating as double) end) as self2q,"
				+ " avg(case when (date(q.last_timestamp)>= date('"
				+ qtr[2][0]
				+ "') and date(q.last_timestamp)<= date('"
				+ qtr[2][1]
				+ "')) then cast(q.final_rating as double) end) as set2q,"
				+ " avg(case when (date(s.last_timestamp)>= date('"
				+ qtr[3][0]
				+ "') and date(s.last_timestamp)<= date('"
				+ qtr[3][1]
				+ "')) then cast(s.rating as double) end) as self3q,"
				+ " avg(case when (date(q.last_timestamp)>= date('"
				+ qtr[3][0]
				+ "') and date(q.last_timestamp)<= date('"
				+ qtr[3][1]
				+ "')) then cast(q.final_rating as double) end) as set3q,"
				+ " avg(case when (date(s.last_timestamp)>= date('"
				+ qtr[4][0]
				+ "') and date(s.last_timestamp)<= date('"
				+ qtr[4][1]
				+ "')) then cast(s.rating as double) end) as self4q,"
				+ " avg(case when (date(q.last_timestamp)>= date('"
				+ qtr[4][0]
				+ "') and date(q.last_timestamp)<= date('"
				+ qtr[4][1]
				+ "')) then cast(q.final_rating as double) end) as set4q,"
				+ " avg(case when (date(s.last_timestamp)>= date('"
				+ qtr[5][0]
				+ "') and date(s.last_timestamp)<= date('"
				+ qtr[5][1]
				+ "')) then cast(s.rating as double) end) as self5q,"
				+ " avg(case when (date(q.last_timestamp)>= date('"
				+ qtr[5][0]
				+ "') and date(q.last_timestamp)<= date('"
				+ qtr[5][1]
				+ "')) then cast(q.final_rating as double) end) as set5q,"
				+ " avg(case when (date(s.last_timestamp)>= date('"
				+ qtr[6][0]
				+ "') and date(s.last_timestamp)<= date('"
				+ qtr[6][1]
				+ "')) then cast(s.rating as double) end) as self6q,"
				+ " avg(case when (date(q.last_timestamp)>= date('"
				+ qtr[6][0]
				+ "') and date(q.last_timestamp)<= date('"
				+ qtr[6][1]
				+ "')) then cast(q.final_rating as double) end) as set6q,"
				+ " avg(case when (date(s.last_timestamp)>= date('"
				+ qtr[7][0]
				+ "') and date(s.last_timestamp)<= date('"
				+ qtr[7][1]
				+ "')) then cast(s.rating as double) end) as self7q,"
				+ " avg(case when (date(q.last_timestamp)>= date('"
				+ qtr[7][0]
				+ "') and date(q.last_timestamp)<= date('"
				+ qtr[7][1]
				+ "')) then cast(q.final_rating as double) end) as set7q,"
				+ " avg(case when (date(s.last_timestamp)>= date('"
				+ qtr[8][0]
				+ "') and date(s.last_timestamp)<= date('"
				+ qtr[8][1]
				+ "')) then cast(s.rating as double) end) as self8q,"
				+ " avg(case when (date(q.last_timestamp)>= date('"
				+ qtr[8][0]
				+ "') and date(q.last_timestamp)<= date('"
				+ qtr[8][1]
				+ "')) then cast(q.final_rating as double) end) as set8q"
				+ " from ets.ets_self_exp s,ets.ets_qbr_exp q,ets.ets_self_main sm, ets.ets_qbr_main qm,ets.ets_projects p"
				+ user_table
				+ " where s.project_id=q.project_id"
				+ " and qm.state='CLOSED'"
				+ " and qm.qbr_id=q.qbr_id"
				+ " and qm.project_id=q.project_id"
				+ " and sm.state='CLOSED'"
				+ " and sm.self_id=s.self_id"
				+ " and sm.project_id=s.project_id"
				+ " and s.project_id=p.project_id"
				+ " and q.question_id!=5"
				+ " and q.question_id!=6"
				+ geo_filter
				+ ind_filter
				+ adv_filter
				+ owner_filter
				+ notAdminStr
				+ " and p.project_type='"
				+ project_type
				+ "' with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		ETSMetricsCCObj obj = new ETSMetricsCCObj();
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			if (rs.next()) {
				double d = rs.getDouble("selfcurrent");
				obj.setSelfCurrRating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("setcurrent");
				obj.setSetCurrRating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("selfprev");
				obj.setSelfPrevRating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("setprev");
				System.out.println("dao=" + d);
				obj.setSetPrevRating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("self2q");
				obj.setSelf2Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("set2q");
				obj.setSet2Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("self3q");
				obj.setSelf3Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("set3q");
				obj.setSet3Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("self4q");
				obj.setSelf4Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("set4q");
				obj.setSet4Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("self5q");
				obj.setSelf5Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("set5q");
				obj.setSet5Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("self6q");
				obj.setSelf6Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("set6q");
				obj.setSet6Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("self7q");
				obj.setSelf7Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("set7q");
				obj.setSet7Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("self8q");
				obj.setSelf8Rating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("set8q");
				obj.setSet8Rating((rs.wasNull()) ? -1 : d);
			}
		} finally {
			close(statement, rs);
		}
		int[] whichYear = getSurveyYears(9);
		for (int i = 0; i < whichYear.length; i++) {
			if (whichYear[i] == 0)
				obj.setSurveyRating(i, -1);
			else {
				String qry =
					"select b.survey_value from ets.ETS_SURVEY_data a,ets.ETS_SURVEY_DATA b , ets.ets_projects p"
						+ user_table
						+ " where company=a.survey_value and a.response_id=b.response_id and a.SURVEY_KEY='CONAME' and b.survey_key='OSAT - IBM' and b.SURVEY_YEAR='"
						+ whichYear[i]
						+ "' and a.SURVEY_YEAR='"
						+ whichYear[i]
						+ "' "
						+ geo_filter
						+ ind_filter
						+ adv_filter
						+ owner_filter
						+ notAdminStr
						+ " and p.project_type='"
						+ project_type
						+ "' and p.PROJECT_OR_PROPOSAL='C' with ur";
				Vector v = ETSSupportFunctions.getValues(conn, qry);
				double d = ETSSupportFunctions.getStringVectorToAvgFloat(v);
				obj.setSurveyRating(i, d);
			}
		}
		obj.setRowName("Overall average");
		obj.setRowDesc("Overall");
		result.addElement(obj);
		ETSMetricsCCObj obj2 = new ETSMetricsCCObj(obj);
		obj2.setRowName("Avg set/met");
		obj2.setRowDesc("Set");
		result.addElement(obj2);
		ETSMetricsCCObj obj3 = new ETSMetricsCCObj(obj);
		obj3.setRowName("Avg self assessment");
		obj3.setRowDesc("Self");
		result.addElement(obj3);
		ETSMetricsCCObj obj4 = new ETSMetricsCCObj(obj);
		obj4.setRowName("Avg Survey data");
		obj4.setRowDesc("Survey");
		result.addElement(obj4);
		return result;
	}
	static Vector filterAvgRatByCode(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterAvgRatByCode(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterAvgRatByCode(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String geo_filter = new String();
		String ind_filter = new String();
		String delteam_filter = new String();
		String user_table = new String();
		String adv_filter = new String();
		String owner_filter = new String();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr =
				" and p.company in (select pp.company   "
					+ " from ETS.ETS_Projects pp, ets.ets_users uu, ets.ets_roles rr"
					+ " where pp.project_status!='A'"
					+ " and pp.project_status!='D'"
					+ " and pp.project_id=uu.user_project_id"
					+ " and uu.user_role_id=rr.role_id"
					+ " and rr.priv_id="
					+ Defines.OWNER
					+ " and uu.active_flag='A'"
					+ " and uu.user_id='"
					+ params.es.gIR_USERN
					+ "' and pp.project_type='"
					+ project_type
					+ "' )";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		if (!resobj.getSelectedTeams().contains("All values")) {
			delteam_filter = " and p.delivery_team in (" + resobj.getSelectedTeamsDBStr() + ")";
		}
		if (!resobj.getSelectedAdvocate().contains("0All values0")) {
			user_table = ",ets.ets_users u";
			adv_filter = " and p.project_id = u.user_project_id" + " and u.primary_contact='Y'" + " and u.user_id in (" + resobj.getSelectedAdvocateDBStr() + ")";
		}
		if (!resobj.getSelectedWSOwner().contains("0All values0")) {
			user_table = ",ets.ets_users u,ets.ets_roles r";
			owner_filter = " and p.project_id = u.user_project_id" + " and u.user_role_id=r.role_id" + " and r.priv_id=" + Defines.OWNER + " and u.user_id in (" + resobj.getSelectedWSOwnerDBStr() + ")";
		}
		String[][] qtr = getQuarterString(2);
		String query =
			"select ex.description,"
				+ " avg(case when (date(e.last_timestamp) >= date('"
				+ qtr[0][0]
				+ "')) then cast(e.final_rating as double) end) as current,"
				+ " avg(case when (date(e.last_timestamp)>= date('"
				+ qtr[1][0]
				+ "') and date(e.last_timestamp)<= date('"
				+ qtr[1][1]
				+ "')) then cast(e.final_rating as double) end) as prev"
				+ " from ets.ets_exp_data ex, ets.ets_qbr_main m, ets.ets_qbr_exp e,ets.ets_projects p"
				+ user_table
				+ " where e.qbr_id=m.qbr_id"
				+ " and e.project_id=m.project_id"
				+ " and p.project_id=m.project_id"
				+ " and e.expect_id=ex.expect_id"
				+ " and m.state='CLOSED'"
				+ " and p.project_or_proposal='C'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and e.question_id!=5"
				+ " and e.question_id!=6"
				+ ind_filter
				+ geo_filter
				+ delteam_filter
				+ adv_filter
				+ owner_filter
				+ notAdminStr
				+ " and p.project_type='"
				+ project_type
				+ "' group by ex.description"
				+ " with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		Statement statement1 = null;
		ResultSet rs1 = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			double currenttotal = 0;
			int currentcnt = 0;
			double prevtotal = 0;
			int prevcnt = 0;
			while (rs.next()) {
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				double d = rs.getDouble("current");
				obj.setSetCurrRating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					System.out.println(d);
					currenttotal = currenttotal + d;
					currentcnt++;
				}
				d = rs.getDouble("prev");
				obj.setSetPrevRating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					prevtotal = prevtotal + d;
					prevcnt++;
				}
				obj.setRowDesc("Set");
				obj.setRowName(rs.getString("description"));
				result.addElement(obj);
			}
			ETSMetricsCCObj obj = new ETSMetricsCCObj();
			if (currentcnt > 0) {
				obj.setSetCurrRating(currenttotal / currentcnt);
			}
			if (prevcnt > 0) {
				obj.setSetPrevRating(prevtotal / prevcnt);
			}
			obj.setRowDesc("Set");
			obj.setRowName("Avg rating for all codes");
			result.add(0, obj);
			query =
				"select "
					+ " avg(case when (date(e.last_timestamp) >= date('"
					+ qtr[0][0]
					+ "')) then cast(e.final_rating as double) end) as current,"
					+ " avg(case when (date(e.last_timestamp)>= date('"
					+ qtr[1][0]
					+ "') and date(e.last_timestamp)<= date('"
					+ qtr[1][1]
					+ "')) then cast(e.final_rating as double) end) as prev"
					+ " from ets.ets_qbr_main m, ets.ets_qbr_exp e,ets.ets_projects p"
					+ user_table
					+ " where e.qbr_id=m.qbr_id"
					+ " and e.project_id=m.project_id"
					+ " and p.project_id=m.project_id"
					+ " and m.state='CLOSED'"
					+ " and p.project_or_proposal='C'"
					+ " and p.project_status!='A'"
					+ " and p.project_status!='D'"
					+ " and e.question_id=4"
					+ ind_filter
					+ geo_filter
					+ delteam_filter
					+ adv_filter
					+ owner_filter
					+ notAdminStr
					+ " and p.project_type='"
					+ project_type
					+ "' with ur";
			System.out.println(query);
			statement1 = conn.createStatement();
			rs1 = statement.executeQuery(query);
			if (rs.next()) {
				ETSMetricsCCObj obj1 = new ETSMetricsCCObj();
				double d = rs.getDouble("current");
				obj1.setSetCurrRating((rs.wasNull()) ? -1 : d);
				d = rs.getDouble("prev");
				obj1.setSetPrevRating((rs.wasNull()) ? -1 : d);
				obj1.setRowDesc("Set");
				obj1.setRowName("Overall sat rating");
				result.add(0, obj1);
			} else {
				ETSMetricsCCObj obj1 = new ETSMetricsCCObj();
				obj1.setSetCurrRating(-1);
				obj1.setSetPrevRating(-1);
				obj1.setRowDesc("Set");
				obj1.setRowName("Overall sat rating");
				result.add(0, obj1);
			}
		} finally {
			close(statement, rs);
			close(statement1, rs1);
		}
		return result;
	}
	static Vector filterClientsWithNoInput(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterClientsWithNoInput(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterClientsWithNoInput(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String query =
			"select p.project_id"
				+ " from ets.ets_exp_data ex, ets.ets_qbr_main m, ets.ets_qbr_exp e,ets.ets_projects p"
				+ " where e.qbr_id=m.qbr_id"
				+ " and e.project_id=m.project_id"
				+ " and p.project_id=m.project_id"
				+ " and e.expect_id=ex.expect_id"
				+ " and m.state='CLOSED'"
				+ " and p.project_or_proposal='C'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and p.project_type='"
				+ project_type
				+ "' group by p.company"
				+ " with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterAvgClientSatByClient(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterAvgClientSatByClient(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterAvgClientSatByClient(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String comp_filter = new String();
		String geo_filter = new String();
		String ind_filter = new String();
		String delteam_filter = new String();
		String user_table = new String();
		String adv_filter = new String();
		String owner_filter = new String();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr =
				" and p.company in (select pp.company   "
					+ " from ETS.ETS_Projects pp, ets.ets_users uu, ets.ets_roles rr"
					+ " where pp.project_status!='A'"
					+ " and pp.project_status!='D'"
					+ " and pp.project_id=uu.user_project_id"
					+ " and uu.user_role_id=rr.role_id"
					+ " and rr.priv_id="
					+ Defines.OWNER
					+ " and uu.active_flag='A'"
					+ " and uu.user_id='"
					+ params.es.gIR_USERN
					+ "' and pp.project_type='"
					+ project_type
					+ "' )";
		}
		if (!resobj.getSelectedComps().contains("All values")) {
			comp_filter = " and p.company in (" + resobj.getSelectedCompsDBStr() + ")";
		}
		if (!resobj.getSelectedInds().contains("All values")) {
			ind_filter = " and p.industry in (" + resobj.getSelectedIndsDBStr() + ")";
		}
		if (!resobj.getSelectedGeos().contains("All values")) {
			geo_filter = " and p.geography in (" + resobj.getSelectedGeosDBStr() + ")";
		}
		if (!resobj.getSelectedTeams().contains("All values")) {
			delteam_filter = " and p.delivery_team in (" + resobj.getSelectedTeamsDBStr() + ")";
		}
		if (!resobj.getSelectedAdvocate().contains("0All values0")) {
			user_table = ",ets.ets_users u";
			adv_filter = " and p.project_id = u.user_project_id" + " and u.primary_contact='Y'" + " and u.user_id in (" + resobj.getSelectedAdvocateDBStr() + ")";
		}
		if (!resobj.getSelectedWSOwner().contains("0All values0")) {
			user_table = ",ets.ets_users u,ets.ets_roles r";
			owner_filter = " and p.project_id = u.user_project_id" + " and u.user_role_id=r.role_id" + " and r.priv_id=" + Defines.OWNER + " and u.user_id in (" + resobj.getSelectedWSOwnerDBStr() + ")";
		}
		String[][] qtr = getQuarterString(9);
		String query =
			"select p.company,"
				+ " avg(case when (date(e.last_timestamp) >= date('"
				+ qtr[0][0]
				+ "')) then cast(e.final_rating as double) end) as setcurrent,"
				+ " avg(case when (date(e.last_timestamp)>= date('"
				+ qtr[1][0]
				+ "') and date(e.last_timestamp)<= date('"
				+ qtr[1][1]
				+ "')) then cast(e.final_rating as double) end) as setprev,"
				+ " avg(case when (date(e.last_timestamp)>= date('"
				+ qtr[2][0]
				+ "') and date(e.last_timestamp)<= date('"
				+ qtr[2][1]
				+ "')) then cast(e.final_rating as double) end) as set2q,"
				+ " avg(case when (date(e.last_timestamp)>= date('"
				+ qtr[3][0]
				+ "') and date(e.last_timestamp)<= date('"
				+ qtr[3][1]
				+ "')) then cast(e.final_rating as double) end) as set3q,"
				+ " avg(case when (date(e.last_timestamp)>= date('"
				+ qtr[4][0]
				+ "') and date(e.last_timestamp)<= date('"
				+ qtr[4][1]
				+ "')) then cast(e.final_rating as double) end) as set4q,"
				+ " avg(case when (date(e.last_timestamp)>= date('"
				+ qtr[5][0]
				+ "') and date(e.last_timestamp)<= date('"
				+ qtr[5][1]
				+ "')) then cast(e.final_rating as double) end) as set5q,"
				+ " avg(case when (date(e.last_timestamp)>= date('"
				+ qtr[6][0]
				+ "') and date(e.last_timestamp)<= date('"
				+ qtr[6][1]
				+ "')) then cast(e.final_rating as double) end) as set6q,"
				+ " avg(case when (date(e.last_timestamp)>= date('"
				+ qtr[7][0]
				+ "') and date(e.last_timestamp)<= date('"
				+ qtr[7][1]
				+ "')) then cast(e.final_rating as double) end) as set7q,"
				+ " avg(case when (date(e.last_timestamp)>= date('"
				+ qtr[8][0]
				+ "') and date(e.last_timestamp)<= date('"
				+ qtr[8][1]
				+ "')) then cast(e.final_rating as double) end) as set8q"
				+ " from ets.ets_qbr_main m, ets.ets_qbr_exp e,ets.ets_projects p"
				+ user_table
				+ " where e.qbr_id=m.qbr_id"
				+ " and e.project_id=m.project_id"
				+ " and p.project_id=m.project_id"
				+ " and m.state='CLOSED'"
				+ " and p.project_or_proposal='C'"
				+ " and p.project_status!='A'"
				+ " and p.project_status!='D'"
				+ " and e.question_id=4"
				+ comp_filter
				+ ind_filter
				+ geo_filter
				+ delteam_filter
				+ adv_filter
				+ owner_filter
				+ notAdminStr
				+ " and p.project_type='"
				+ project_type
				+ "' group by p.company"
				+ " with ur";
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			double currenttotal = 0;
			double prevtotal = 0;
			double set2qtotal = 0;
			double set3qtotal = 0;
			double set4qtotal = 0;
			double set5qtotal = 0;
			double set6qtotal = 0;
			double set7qtotal = 0;
			double set8qtotal = 0;
			int currentcnt = 0;
			int prevcnt = 0;
			int set2qcnt = 0;
			int set3qcnt = 0;
			int set4qcnt = 0;
			int set5qcnt = 0;
			int set6qcnt = 0;
			int set7qcnt = 0;
			int set8qcnt = 0;
			while (rs.next()) {
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				double d = rs.getDouble("setcurrent");
				obj.setSetCurrRating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					System.out.println(d);
					currenttotal = currenttotal + d;
					currentcnt++;
				}
				d = rs.getDouble("setprev");
				obj.setSetPrevRating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					prevtotal = prevtotal + d;
					prevcnt++;
				}
				d = rs.getDouble("set2q");
				obj.setSet2Rating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					set2qtotal = set2qtotal + d;
					set2qcnt++;
				}
				d = rs.getDouble("set3q");
				obj.setSet3Rating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					set3qtotal = set3qtotal + d;
					set3qcnt++;
				}
				d = rs.getDouble("set4q");
				obj.setSet4Rating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					set4qtotal = set4qtotal + d;
					set4qcnt++;
				}
				d = rs.getDouble("set5q");
				obj.setSet5Rating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					set5qtotal = set5qtotal + d;
					set5qcnt++;
				}
				d = rs.getDouble("set6q");
				obj.setSet6Rating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					set6qtotal = set6qtotal + d;
					set6qcnt++;
				}
				d = rs.getDouble("set7q");
				obj.setSet7Rating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					set7qtotal = set7qtotal + d;
					set7qcnt++;
				}
				d = rs.getDouble("set8q");
				obj.setSet8Rating((rs.wasNull()) ? -1 : d);
				if (!rs.wasNull()) {
					set8qtotal = set8qtotal + d;
					set8qcnt++;
				}
				obj.setRowDesc("Set");
				obj.setProjectCompany(rs.getString("company"));
				result.addElement(obj);
			}
			ETSMetricsCCObj obj = new ETSMetricsCCObj();
			if (currentcnt > 0) {
				obj.setSetCurrRating(currenttotal / currentcnt);
			}
			if (prevcnt > 0) {
				obj.setSetPrevRating(prevtotal / prevcnt);
			}
			if (set2qcnt > 0) {
				obj.setSet2Rating(set2qtotal / set2qcnt);
			}
			if (set3qcnt > 0) {
				obj.setSet3Rating(set3qtotal / set3qcnt);
			}
			if (set4qcnt > 0) {
				obj.setSet4Rating(set4qtotal / set4qcnt);
			}
			if (set5qcnt > 0) {
				obj.setSet5Rating(set5qtotal / set5qcnt);
			}
			if (set6qcnt > 0) {
				obj.setSet6Rating(set6qtotal / set6qcnt);
			}
			if (set7qcnt > 0) {
				obj.setSet7Rating(set7qtotal / set7qcnt);
			}
			if (set8qcnt > 0) {
				obj.setSet8Rating(set8qtotal / set8qcnt);
			}
			obj.setRowDesc("Set");
			obj.setProjectCompany("Average");
			result.addElement(obj);
		} finally {
			close(statement, rs);
		}
		return result;
	}
	static Vector filterCurrentSetMetRating(ETSMetricsResultObj resobj, ETSParams params) {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return filterCurrentSetMetRating(resobj, params, conn);
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
			return null;
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	static Vector filterCurrentSetMetRating(ETSMetricsResultObj resobj, ETSParams params, Connection conn) throws SQLException {
		Vector result = new Vector();
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr =
				" and p.company in (select pp.company   "
					+ " from ETS.ETS_Projects pp, ets.ets_users uu, ets.ets_roles rr"
					+ " where pp.project_status!='A'"
					+ " and pp.project_status!='D'"
					+ " and pp.project_id=uu.user_project_id"
					+ " and uu.user_role_id=rr.role_id"
					+ " and rr.priv_id="
					+ Defines.OWNER
					+ " and uu.active_flag='A'"
					+ " and uu.user_id='"
					+ params.es.gIR_USERN
					+ "' and pp.project_type='"
					+ project_type
					+ "' )";
		}
		String query =
			"select p.company, e.final_rating, m.last_timestamp"
				+ " from ets.ets_projects p, ets.ets_qbr_exp e, ets.ets_qbr_main m"
				+ " where e.qbr_id=m.qbr_id"
				+ " and   m.state='CLOSED'"
				+ " and   p.project_id=e.project_id"
				+ " and   e.question_id=4"
				+ notAdminStr
				+ " and   (p.company||char(m.last_timestamp))"
				+ " in"
				+ " (select ps.company||char(max(ms.last_timestamp))"
				+ " from ets.ets_projects ps, ets.ets_qbr_exp es, ets.ets_qbr_main ms"
				+ " where es.qbr_id=ms.qbr_id"
				+ " and   ms.state='CLOSED'"
				+ " and   ps.project_id=es.project_id"
				+ " and   es.question_id=4"
				+ " group by ps.company)"
				+ " and p.project_type='"
				+ project_type
				+ "' order by e.final_rating desc,p.company,m.last_timestamp"
				+ " with ur";
		double rattotal = 0;
		int ratcnt = 0;
		System.out.println(query);
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				obj.setProjectCompany(rs.getString("company"));
				double d = rs.getDouble("final_rating");
				obj.setSetCurrRating(rs.wasNull() ? -1 : d);
				if (!rs.wasNull()) {
					rattotal = rattotal + d;
					ratcnt++;
				}
				obj.setRatingDate(rs.getTimestamp(("last_timestamp")));
				obj.setRowDesc("Set");
				result.addElement(obj);
			}
			if (ratcnt > 0) {
				ETSMetricsCCObj obj = new ETSMetricsCCObj();
				obj.setProjectCompany("Average");
				obj.setSetCurrRating(rattotal / ratcnt);
				obj.setRatingDate(new java.util.Date());
				obj.setRowDesc("Set");
				result.add(0, obj);
			}
		} finally {
			close(statement, rs);
		}
		return result;
	}
	private static String getNotAdminStr(ETSParams params) {
		String project_type = params.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		String notAdminStr = new String();
		notAdminStr = "";
		if (!(params.isExecutive || params.isSuperAdmin)) {
			notAdminStr =
				" and p.company in (select pp.company   "
					+ " from ETS.ETS_Projects pp, ets.ets_users u, ets.ets_roles r"
					+ " where pp.project_status!='A'"
					+ " and pp.project_status!='D'"
					+ " and pp.project_id=u.user_project_id"
					+ " and u.user_role_id=r.role_id"
					+ " and r.priv_id="
					+ Defines.OWNER
					+ " and u.active_flag='A'"
					+ " and u.user_id='"
					+ params.es.gIR_USERN
					+ "' and pp.project_type='"
					+ project_type
					+ "' )";
		}
		return notAdminStr;
	}
	private static String getLFName(String user_id, Connection conn) {
		String s = user_id;
		Statement statement = null;
		ResultSet rs = null;
		String query = "select a.user_lname,a.user_fname" + " from amt.users a" + " where a.ir_userid='" + user_id + "' with ur";
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			if (rs.next()) {
				s = rs.getString("user_lname") + ", " + rs.getString("user_fname");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
		} finally {
			close(statement, rs);
		}
		return s;
	}

	private static String getLFNameForEdgeId(String edge_user_id, Connection conn) {
		String s = edge_user_id;
		Statement statement = null;
		ResultSet rs = null;
		String query = "select a.user_lname,a.user_fname" + " from amt.users a" + " where a.edge_userid='" + edge_user_id + "' with ur";
		try {
			
			if (logger.isDebugEnabled()) {
				logger.debug(query);
			} 
						
			statement = conn.createStatement();
			rs = statement.executeQuery(query);
			
			if (rs.next()) {
				s = rs.getString("user_lname") + ", " + rs.getString("user_fname");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERROR=" + e);
			e.printStackTrace();
		} catch (Exception ex) {
			System.out.println("EX ERROR=" + ex);
			ex.printStackTrace();
		} finally {
			close(statement, rs);
		}
		return s;
	}
	private static String getResultString(String s) {
		if (s == null) {
			s = "";
		}
		s = s.replace(',', '-');
		//System.out.println("s = "+s);
		return s;
	}
	private static String[][] getQuarterString(int qtrs) {
		String[][] s = new String[qtrs][2];
		Calendar c = Calendar.getInstance();
		int thisMonth = c.get(Calendar.MONTH);
		int thisYear = c.get(Calendar.YEAR);
		int thisQuarter = 1;
		if (thisMonth >= 1 && thisMonth <= 3) {
			thisQuarter = 1;
		} else if (thisMonth >= 4 && thisMonth <= 6) {
			thisQuarter = 2;
		} else if (thisMonth >= 7 && thisMonth <= 9) {
			thisQuarter = 3;
		} else if (thisMonth >= 10 && thisMonth <= 12) {
			thisQuarter = 4;
		}
		int j = 0;
		for (int i = qtrs; i > 0; i--) {
			String s_temp1 = "";
			String s_temp2 = "";
			if (thisQuarter == 4) {
				s_temp1 = thisYear + "-10-01";
				s_temp2 = thisYear + "-12-31";
			} else if (thisQuarter == 3) {
				s_temp1 = thisYear + "-07-01";
				s_temp2 = thisYear + "-09-30";
			} else if (thisQuarter == 2) {
				s_temp1 = thisYear + "-04-01";
				s_temp2 = thisYear + "-06-30";
			} else { //if (thisQuarter==1){
				s_temp1 = thisYear + "-01-01";
				s_temp2 = thisYear + "-03-31";
				thisYear--;
				thisQuarter = 5;
			}
			s[j] = (new String[] { s_temp1, s_temp2 });
			j++;
			thisQuarter--;
		}
		return s;
	}
	public static String[] getQuarterAbrString(int qtrs) {
		String[] s = new String[qtrs];
		Calendar c = Calendar.getInstance();
		int thisMonth = c.get(Calendar.MONTH);
		int thisYear = c.get(Calendar.YEAR);
		int thisQuarter = 1;
		if (thisMonth >= 1 && thisMonth <= 3) {
			thisQuarter = 1;
		} else if (thisMonth >= 4 && thisMonth <= 6) {
			thisQuarter = 2;
		} else if (thisMonth >= 7 && thisMonth <= 9) {
			thisQuarter = 3;
		} else if (thisMonth >= 10 && thisMonth <= 12) {
			thisQuarter = 4;
		}
		int j = 0;
		for (int i = qtrs; i > 0; i--) {
			String s_temp = "";
			String sYear = String.valueOf(thisYear).substring(2);
			if (thisQuarter == 4) {
				s_temp = "4Q" + sYear;
			} else if (thisQuarter == 3) {
				s_temp = "3Q" + sYear;
			} else if (thisQuarter == 2) {
				s_temp = "2Q" + sYear;
			} else { //if (thisQuarter==1){
				s_temp = "1Q" + sYear;
				thisYear--;
				thisQuarter = 5;
			}
			s[j] = s_temp;
			j++;
			thisQuarter--;
		}
		return s;
	}
	public static int[] getSurveyYears(int qtrs) {
		int[] s = new int[qtrs];
		Calendar c = Calendar.getInstance();
		int thisMonth = c.get(Calendar.MONTH);
		int thisYear = c.get(Calendar.YEAR);
		int thisQuarter = 1;
		if (thisMonth >= 1 && thisMonth <= 3) {
			thisQuarter = 1;
		} else if (thisMonth >= 4 && thisMonth <= 6) {
			thisQuarter = 2;
		} else if (thisMonth >= 7 && thisMonth <= 9) {
			thisQuarter = 3;
		} else if (thisMonth >= 10 && thisMonth <= 12) {
			thisQuarter = 4;
		}
		int j = 0;
		for (int i = qtrs; i > 0; i--) {
			if (thisQuarter == 4) {
				s[j] = thisYear;
			} else if (thisQuarter == 3) {
				s[j] = 0;
			} else if (thisQuarter == 2) {
				s[j] = 0;
			} else { //if (thisQuarter==1){
				s[j] = 0;
				thisYear--;
				thisQuarter = 5;
			}
			j++;
			thisQuarter--;
		}
		return s;
	}
	public static void close(Statement s, ResultSet r) {
		try {
			if (r != null) {
				r.close();
				r = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (s != null) {
				s.close();
				s = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
