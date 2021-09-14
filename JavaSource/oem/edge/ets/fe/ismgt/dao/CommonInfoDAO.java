package oem.edge.ets.fe.ismgt.dao;

import java.util.*;
import java.sql.*;

import oem.edge.common.*;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.amt.*;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;;
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

/**
 * @author v2phani
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CommonInfoDAO implements EtsIssFilterConstants {

	
	public static final String VERSION = "1.46";
	
	

	/**
	 * Constructor for CommonInfoDAO.
	 */
	public CommonInfoDAO() {
		super();
		
		
	}
	
	/**
	 * This method will return an object, holding Primary Contact Info for a given Project Id
	 * and returns ArrayList
	 * ets.ets_users >> userid === ir_userid in amt.users
	 */

	public EtsPrimaryContactInfo getProjContactInfo(Connection conn,String projectId) throws SQLException {

		ArrayList contList = new ArrayList();
		StringBuffer sb = new StringBuffer();
		EtsPrimaryContactInfo etsContInfo = new EtsPrimaryContactInfo();

		Statement stmt = null;
		ResultSet rs = null;

		sb.append("select a.ir_userid as iruserid,a.user_fullname as userfullname,a.user_email as useremail,a.user_phone as userphone");
		sb.append("   from amt.users a, ets.ets_users b ");
		sb.append("   where ");
		sb.append("   a.ir_userid=b.user_id");
		sb.append("   and   b.primary_contact='Y' ");
		sb.append("   and   b.user_project_id='" + projectId + "'");
		sb.append("  with ur");

		SysLog.log(SysLog.DEBUG, "getProjContactInfo qry", "getProjContactInfo qry=" + sb.toString() + ":");

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					etsContInfo.setUserIrId(AmtCommonUtils.getTrimStr(rs.getString("IRUSERID")));
					etsContInfo.setUserFullName(AmtCommonUtils.getTrimStr(rs.getString("USERFULLNAME")));
					etsContInfo.setUserEmail(AmtCommonUtils.getTrimStr(rs.getString("USEREMAIL")));
					etsContInfo.setUserContPhone(AmtCommonUtils.getTrimStr(rs.getString("USERPHONE")));

				}

			}

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {

				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException se) {

				}

			}

		}

		return etsContInfo;

	}

	/**
	 * This method will return an object, holding Primary Contact Info for a given Project Id
	 * and returns ArrayList
	 * ets.ets_users >> userid === ir_userid in amt.users
	 */

	public  EtsPrimaryContactInfo getProjContactInfo(String projectId) throws SQLException {

		DbConnect db = null;
		EtsPrimaryContactInfo etsContInfo = new EtsPrimaryContactInfo();

		try {

			db = new DbConnect();
			db.makeConn(ETSDATASRC);

			etsContInfo = getProjContactInfo(db.conn,projectId);
			
		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ETS GUI UTILS", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				SysLog.log(SysLog.ERR, "SQL Exception in ETS GUI UTILS ", se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in ETS GUI UTILS", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, "Exception in ETS GUI UTILS & ", ex);
				ex.printStackTrace();

			}

		} finally {

			if (db != null)
				db.closeConn();
			db = null;
		}

		return etsContInfo;

	}
	
	
	/**
	 * Method getProjectDetails.
	 * @param projectidStr
	 * @return ETSProj
	 */
	public ETSProj getProjectDetails(Connection conn, String projectidStr) throws SQLException, Exception {
		
		
	return ETSDatabaseManager.getProjectDetails(conn,projectidStr);
	
	}
	
	
	/**
	 * This method will return an object, holding Primary Contact Info for a given Project Id
	 * and returns ArrayList
	 * ets.ets_users >> userid === ir_userid in amt.users
	 * ask deepak, which is good (sub-query /join on amt.users on info??)
	 * ask deepak, which is good (sub-query /join on amt.users on info??) discuss 9735 recs in decaf.users with subquery
	 * and 9736 record with join btwn decaf.users/amt.users ??
	 */

	public ETSProj getProjectDetails(String projectidStr) throws SQLException, Exception {

		DbConnect db = null;
		ETSProj etsProj=new ETSProj();

		try {

			db = new DbConnect();
			db.makeConn(ETSDATASRC);

			etsProj=getProjectDetails(db.conn, projectidStr);
			
		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ETS GUI UTILS", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				SysLog.log(SysLog.ERR, "SQL Exception in ETS GUI UTILS ", se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in ETS GUI UTILS", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, "Exception in ETS GUI UTILS & ", ex);
				ex.printStackTrace();

			}

		} finally {

			if (db != null)
				db.closeConn();
			db = null;
		}

		return etsProj;

	}
	
	/**
	 * This method will return an object, holding Primary Contact Info for a given Project Id
	 * and returns ArrayList
	 * ets.ets_users >> userid === ir_userid in amt.users
	 */

	public  EtsIssProjectMember getUserDetailsInfo(String userEdgeId) throws SQLException {

		DbConnect db = null;
		EtsIssProjectMember etsProjMem = new EtsIssProjectMember();

		try {

			db = new DbConnect();
			db.makeConn(ETSDATASRC);

			etsProjMem = getUserDetailsInfo(db.conn,userEdgeId);
			
		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ETS GUI UTILS", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				SysLog.log(SysLog.ERR, "SQL Exception in ETS GUI UTILS ", se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in ETS GUI UTILS", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, "Exception in ETS GUI UTILS & ", ex);
				ex.printStackTrace();

			}

		} finally {

			if (db != null)
				db.closeConn();
			db = null;
		}

		return etsProjMem;

	}
	
	
	/**
	 * This method will return an object, holding Primary Contact Info for a given Project Id
	 * and returns ArrayList
	 * ets.ets_users >> userid === ir_userid in amt.users
	 */

	public EtsIssProjectMember getUserDetailsInfo(Connection conn,String userEdgeId) throws SQLException {

		ArrayList contList = new ArrayList();
		StringBuffer sb = new StringBuffer();
		EtsIssProjectMember etsProjMem = new EtsIssProjectMember();

		PreparedStatement stmt = null;
		ResultSet rs = null;

		sb.append("select a.ir_userid as iruserid,a.edge_userid as edgeuserid,a.user_fullname as userfullname,a.user_email as useremail,a.user_phone as userphone");
		sb.append("   from amt.users a");
		sb.append("   where ");
		sb.append("   a.edge_userid=?");
		sb.append("  with ur");

		SysLog.log(SysLog.DEBUG, "getUserDetailsInfo qry", "getUserDetailsInfo qry=" + sb.toString() + ":");

		try {

			stmt = conn.prepareStatement(sb.toString());
			
			stmt.clearParameters();
			stmt.setString(1,userEdgeId);
			
			
			rs = stmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {

					etsProjMem.setUserIrId(AmtCommonUtils.getTrimStr(rs.getString("IRUSERID")));
					etsProjMem.setUserEdgeId(AmtCommonUtils.getTrimStr(rs.getString("EDGEUSERID")));
					etsProjMem.setUserFullName(AmtCommonUtils.getTrimStr(rs.getString("USERFULLNAME")));
					etsProjMem.setUserEmail(AmtCommonUtils.getTrimStr(rs.getString("USEREMAIL")));
					etsProjMem.setUserContPhone(AmtCommonUtils.getTrimStr(rs.getString("USERPHONE")));

				}

			}

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {

				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException se) {

				}

			}

		}

		return etsProjMem;

	}
	
	
	/**
		 * This method will return external user count for a given project
		 * and returns ArrayList
		 * ets.ets_users >> userid === ir_userid in amt.users
		 *
		 */

		public int getExtUserCountInProj(String projectidStr) throws SQLException, Exception {

			DbConnect db = null;
			int count=0;

			try {

				db = new DbConnect();
				db.makeConn(ETSDATASRC);

				count=getExtUserCountInProj(db.conn, projectidStr);
			
			} catch (SQLException se) {

				AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in getExtUserCountInProj", ETSLSTUSR);

				if (db != null) {
					db.removeConn(se);
				}
				if (se != null) {
					SysLog.log(SysLog.ERR, "SQL Exception in getExtUserCountInProj ", se);
					se.printStackTrace();

				}

			} catch (Exception ex) {

				AmtCommonUtils.LogGenExpMsg(ex, "General Exception in getExtUserCountInProj", ETSLSTUSR);

				if (ex != null) {
					SysLog.log(SysLog.ERR, "Exception in getExtUserCountInProjs & ", ex);
					ex.printStackTrace();

				}

			} finally {

				if (db != null)
					db.closeConn();
				db = null;
			}

			return count;

		}
		
	/**
		 * This method will return an object, holding Primary Contact Info for a given Project Id
		 * and returns ArrayList
		 * ets.ets_users >> userid === ir_userid in amt.users
		 */

		public int getExtUserCountInProj(Connection conn,String projectId) throws SQLException {

			
			StringBuffer sb = new StringBuffer();
			int count =0;
			
			sb.append("select count(user_type) from decaf.users");
			sb.append(" where");
			sb.append("  userid in (select edge_userid from amt.users ");
			sb.append("                         where ir_userid in ");
			sb.append("                          (select user_id from ets.ets_users where user_project_id='"+projectId+"' and user_role_id NOT IN (select distinct role_id from ets.ets_roles where priv_id=9 and priv_value=1))");
			sb.append("             ) ");
			sb.append(" and user_type = 'E' ");


			SysLog.log(SysLog.DEBUG, "getExtUserCountInProj qry", "getExtUserCountInProj qry=" + sb.toString() + ":");

			
			count =AmtCommonUtils.getRecCount(conn,sb.toString());
			
			return count;

		}

		public String isProjectIBMOnly(String strProjectId)
									throws SQLException, Exception {

			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
	
			StringBuffer sQuery = new StringBuffer("");
		
			String isProjIBMonly = "";
			try {
				
				con = ETSDBUtils.getConnection();
				sQuery.append("SELECT IBM_ONLY FROM ETS.ETS_PROJECTS WHERE PROJECT_ID = ? for READ ONLY");
			
				stmt = con.prepareStatement(sQuery.toString());
				stmt.setString(1,strProjectId);
			
				rs = stmt.executeQuery();
			
				if (rs.next()) {
					isProjIBMonly = rs.getString(1).trim();
				}
		
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
				if (con != null) {
					ETSDBUtils.close(con);
				}
			}
		
			return isProjIBMonly;

		}

}//end of class

