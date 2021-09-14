/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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


/*
 * Created on Feb 21, 2006
 * @author v2sathis@us.ibm.com
 */
 
package oem.edge.ets.fe.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oem.edge.ets.fe.AICUserDecafRole;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author v2sathis
 *
 */
public class UserRoleDAO {
	
	public static final String VERSION = "1.2";
	private static Log logger = LogFactory.getLog(UserRoleDAO.class);	
	
	public boolean hasProjectPriv(String userid, String projid, int priv, Connection conn) throws Exception {
		
		int priv_value = 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		
		try {
			
			String query = new String("SELECT PRIV_VALUE FROM ETS.ETS_ROLES WHERE PRIV_ID = ? AND ROLE_ID = (SELECT USER_ROLE_ID FROM ETS.ETS_USERS WHERE USER_ID = ? AND ACTIVE_FLAG = ? AND USER_PROJECT_ID = ?) with ur");
			
			if (logger.isDebugEnabled()) {
				logger.debug(query + " with values: priv: " + priv + " userid: " + userid + " projid: " + projid); 
			}
			
			statement = conn.prepareStatement(query);
			
			statement.setInt(1,priv);
			statement.setString(2, userid);
			statement.setString(3, "A");
			statement.setString(4, projid);
			
			rs = statement.executeQuery();
	
			if (rs.next()) {
				priv_value = rs.getInt("PRIV_VALUE");
			}
		
		} catch (Exception e){
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			throw e;
		} finally {
			rs.close();
			statement.close();
		}
	
		return (priv_value == 1);
	}	
	
	/**
	 *  Method to return the user role for AIC teamroom wrkspces
	 * @param conn
	 * @param projId
	 * @param irUserId
	 * @return
	 * @throws Exception
	 */
	
	public AICUserDecafRole extractUserDetails(Connection conn, String projId, String irUserId) 
	throws Exception{
		AICUserDecafRole userDets = new AICUserDecafRole();
		PreparedStatement getUserDataSt = null;
		ResultSet rs = null;
		
		try {
			getUserDataSt = conn.prepareStatement("select map.project_id, "
				+ " map.datatype_name, map.entitlement_name, map.role_id," 
				+ " map.profile_name , map.profile_id, users.user_id " 
				+ " from ets.ws_decaf_mapping map, ets.ets_users users "
				+ " where map.project_id = ? "
				+ " and map.project_id = users.user_project_id "
				+ " and map.role_id = users.user_role_id "
				+ " and users.user_id = ?  " 
				+ " and users.active_flag = 'A' " );
		
			getUserDataSt.setString(1, projId);
			getUserDataSt.setString(2, irUserId);
			rs = getUserDataSt.executeQuery();

			if (rs.next()) {
				String sProjId = rs.getString("PROJECT_ID");
				String sDatatypeName = rs.getString("DATATYPE_NAME");
				String sEntitlementName = rs.getString("ENTITLEMENT_NAME");
				int iRoleId = rs.getInt("ROLE_ID");
				String sProfileName = rs.getString("PROFILE_NAME");
				String sProfileId = rs.getString("PROFILE_ID");
				String sUserId = rs.getString("USER_ID");

				userDets.setProjectId(sProjId);
				userDets.setDatatypeName(sDatatypeName);
				userDets.setEntitlementName(sEntitlementName);
				userDets.setRoleId(iRoleId);
				userDets.setDecafProfileName(sProfileName);
				userDets.setDecafProfileId(sProfileId);
				userDets.setUserId(sUserId);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			rs.close();
			getUserDataSt.close();
		}

	return userDets;

	}
	
	
	public Vector getUserPrivs(int roleid, Connection conn) throws SQLException {
		PreparedStatement getPrivSt = conn.prepareStatement("select priv_id from ETS.ETS_ROLES r where r.role_id = ? and r.priv_value=1 with ur");

		getPrivSt.setInt(1, roleid);
		ResultSet rs = getPrivSt.executeQuery();

		Vector v = new Vector();
		while (rs.next()) {
			v.addElement(new Integer(rs.getInt("priv_id")));
		}
		
		rs.close();
		getPrivSt.close();
		return v;
	}

}
