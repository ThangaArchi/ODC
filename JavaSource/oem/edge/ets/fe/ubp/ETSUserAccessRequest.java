/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
package oem.edge.ets.fe.ubp;

/**
 * @author Ravi K. Ravipati
 * Date: Feb 19, 2004
 * File: ETSUserAccessRequest.java
 *
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import oem.edge.amt.AMTException;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.*;
import oem.edge.decaf.ws.DecafEntAccessObj;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.SQLString;
import oem.edge.ets.fe.acmgt.actions.UserEntitlementsMgrIF;
import oem.edge.ets.fe.acmgt.actions.UserProjectsMgrIF;
import oem.edge.ets.fe.acmgt.bdlg.UserEntReqUtilsImpl;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSUserAccessRequest {
	
	private static Log logger = EtsLogger.getLogger(ETSUserAccessRequest.class);
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2005";
	
	public static final String VERSION = "1.17";
	

	public final String POINT_OF_CONTACT = "PointOfContact";
	public ETSUserAccessRequest() {
		tmIrId = "";
		entl = "";
		pmIrId = "";
		userCompany = "";
		userDetails = new ETSUserDetails();
		pocDetails = new ETSUserDetails();
	}

	public void setTmIrId(String tmIrId) {
		this.tmIrId = tmIrId.toLowerCase();
	}
	public void setPmIrId(String pmIrId) {
		this.pmIrId = pmIrId.toLowerCase();
	}
	public void setDecafEntitlement(String entl) {
		this.entl = entl;
	}
	public void setIsAProject(boolean isAProj) {
		this.isAProject = isAProj;
	}
	public void setUserCompany(String userCompany) {
		this.userCompany = userCompany;
	}

	public List getUsersForAMTEmail(Connection conn, String email) {
		List found = new ArrayList();
		String query = "select IR_USERID from amt.users where USER_EMAIL='" + email.trim() + "' with ur";
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String id = rs.getString("IR_USERID");
				if (id == null)
					id = "";
				found.add(id);
			}
		} catch (Exception eX) {
			eX.printStackTrace();
			/* otherwise, ignore error */
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		return found;
	}

	/**
	 * 
	 * @param userid
	 * @param project
	 * @param contact
	 * @param requestor
	 */

	public void recordRequest(String userid, String project, String contact, String requestor,String projType) {

		Connection conn = null;
		try {

			conn = WrkSpcTeamUtils.getConnection();

			recordRequest(conn, userid, project, contact, requestor,projType);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ETSDBUtils.close(conn);
		}
	}

	/**
	 * 
	 * @param conn
	 * @param userid
	 * @param project
	 * @param contact
	 * @param requestor
	 */

	public void recordRequest(Connection conn, String userid, String project, String contact, String requestor,String projType) {
		SQLString usr_sql = new SQLString(userid);
		SQLString prj_sql = new SQLString(project);
		SQLString ctct_sql = new SQLString(contact);
		SQLString rqstr_sql = new SQLString(requestor);
		Statement stmt = null;
		try {
			int reqId = this.getNextRequestId(conn);
			stmt = conn.createStatement();
			String statement =
				"insert into  ets.ets_access_req  (REQUEST_ID, USER_ID, PROJECT_NAME, MGR_EMAIL, DATE_REQUESTED, STATUS, REQUESTED_BY, last_userid, last_timestamp, project_type) values (" + " " + reqId + ", " + "'" + usr_sql + "', " + "'" + prj_sql + "', " + "'" + ctct_sql + "', " + "CURRENT TIMESTAMP, " + "'" + Defines.ACCESS_PENDING + "', " + "'" + rqstr_sql + "', " + "'MEMREQUEST', CURRENT TIMESTAMP,'" + projType + "')";
			logger.debug("MemReuest SQL: "+statement);
			stmt.executeUpdate(statement);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ETSDBUtils.close(stmt);
		}
	}
	
	/**
	 * 
	 * @param conn
	 * @param userid
	 * @param project
	 * @param contact
	 * @param requestor
	 * @param comments
	 * @param projectType
	 */
	public void recordRequestWithComments(Connection conn, String userid, String project, String contact, String requestor, String comments,String projType) {
		SQLString usr_sql = new SQLString(userid);
		SQLString prj_sql = new SQLString(project);
		SQLString ctct_sql = new SQLString(contact);
		SQLString rqstr_sql = new SQLString(requestor);
		SQLString comments_sql = new SQLString(comments);
		Statement stmt = null;
		try {
			int reqId = this.getNextRequestId(conn);
			stmt = conn.createStatement();
			String statement =
				"insert into  ets.ets_access_req  (REQUEST_ID, USER_ID, PROJECT_NAME, MGR_EMAIL, DATE_REQUESTED, STATUS, REQUESTED_BY, last_userid, last_timestamp, project_type) values (" + " " + reqId + ", " + "'" + usr_sql + "', " + "'" + prj_sql + "', " + "'" + ctct_sql + "', " + "CURRENT TIMESTAMP, " + "'" + Defines.ACCESS_PENDING + "', " + "'" + rqstr_sql + "', " + "'MEMREQUEST', CURRENT TIMESTAMP, '" + projType + "')";

			stmt.executeUpdate(statement);
			// insert comments
			String commentQry = "insert into ets.ets_access_log (request_id, log_ts, action_by, action, last_userid, last_timestamp, status)" + " values (" + reqId + ",(current timestamp),'" + usr_sql + "','" + comments + "','" + usr_sql + "',(current timestamp),'Request')";
			stmt.executeUpdate(commentQry);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ETSDBUtils.close(stmt);
		}
	}

	public synchronized int getNextRequestId(Connection conn) throws SQLException {
		String reqQry = "select value(max(request_id)+1,1) from  ets.ets_access_req";
		Statement stmt = null;
		ResultSet rs = null;
		int retVal = 1;
		try {
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery(reqQry);
		
		if (rs.next()) {
			retVal = rs.getInt(1);
		}
		
		}
		
		finally {
			
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		return retVal;
	}

	public ETSStatus request(Connection conn) {
		ETSStatus etsStatus = new ETSStatus();

		try {
			// initialize user & poc details
			userDetails.setWebId(this.getTmIrId());
			userDetails.extractUserDetails(conn);
			pocDetails.setWebId(this.getPmIrId());
			pocDetails.extractUserDetails(conn);

			// update user company and pocid(if MPOC1000)
			if (this.isAProject()) {
				this.updateUserDetails(conn);
				// refresh pocid & updated company
				userDetails.extractUserDetails(conn);
			}

			String reqId = this.getUniqueToken();
			// insert into tracking
			String trkQry = "insert into decaf.req_approval_tracking " + " (decaf_id, req_serial_id, req_submitted_date, appr_result, " + " comments, last_user , last_timestamp" + " ) values " + " ('" + userDetails.getDecafId() + "','" + reqId + "',(current date),'P','E&TS Request','" + pocDetails.getEdgeId() + "',(current timestamp) )";
			EntitledStatic.safeInsert(conn, trkQry);

			// get the accessid, idtype, leveltype
			// levelid, levelval for role or project
			String accessId = "";
			String accessIdType = "";
			String accessLevelType = "";
			String accessLevelId = "";
			String accessLevelVal = "";

			if (this.isAProject()) {
				accessId = EntitledStatic.getValue(conn, "select project_id from decaf.project where project_name='" + this.getDecafEntitlement() + "' with ur");
				accessIdType = "PROJECT";
				accessLevelType = "DT";
			} else {
				accessId = EntitledStatic.getValue(conn, "select roles_id from decaf.roles where roles_name='" + this.getDecafEntitlement() + "' with ur");
				accessIdType = "ROLES";
				accessLevelType = "DT";
				accessLevelId = EntitledStatic.getValue(conn, "select roles_level_id from decaf.roles_levels where roles_id='" + accessId + "' and roles_level=1 with ur");
				accessLevelVal = getUserCompany();
			}

			// insert into details
			String dtlQry =
				"insert into decaf.req_approval_details " + " values ('" + userDetails.getDecafId() + "', '" + reqId + "' ,'" + accessId + "','" + accessIdType + "','" + accessLevelType + "'" + ",'" + accessLevelId + "','" + accessLevelVal + "'" + ",0,'MAN1000','PROF10001',(current date), (current date)" + ",'G',' ','I','E&TS Request','" + pocDetails.getEdgeId() + "',(current timestamp)) ";
			EntitledStatic.safeInsert(conn, dtlQry);
			
			etsStatus.setErrCode(0);
			etsStatus.setErrText("Request is success");

		} catch (Exception eX) {
			etsStatus.setErrCode(-1);
			etsStatus.setErrText("Error requesting entitlement");
			logger.error("exception in request::user request",eX);
			eX.printStackTrace();
		}
		return (etsStatus);
	}

	/**
	 * 
	 * @return
	 */

	public ETSStatus requestProject() throws SQLException, Exception {

		Connection conn = null;
		ETSStatus etsStatus = new ETSStatus();

		try {

			conn = WrkSpcTeamUtils.getConnection();
			etsStatus = requestProject(conn);

		} finally {

			ETSDBUtils.close(conn);
		}

		return etsStatus;

	}

	/**
	 * 
	 * @param conn
	 * @return
	 */

	public ETSStatus requestProject(Connection conn) {

		ETSStatus etsStatus = new ETSStatus();
		UserProjectsMgrIF projReqMgrIF = new UserEntReqUtilsImpl();
		try {
			// initialize user & poc details
			userDetails.setWebId(this.getTmIrId());
			userDetails.extractUserDetails(conn);
			pocDetails.setWebId(this.getPmIrId());
			pocDetails.extractUserDetails(conn);

			// update user company and pocid(if MPOC1000)
			if (this.isAProject() && userDetails.getUserType()==userDetails.USER_TYPE_EXTERNAL) {
				
				this.updateUserDetailsForWrkSpc(conn);
				// refresh pocid & updated company
				userDetails.extractUserDetails(conn);
			}

			Vector entProj = new Vector();
			entProj.add(getProjectName());

			boolean projReq = projReqMgrIF.requestProjectToUser(this.getTmIrId(), this.getPmIrId(), entProj);
			
			logger.debug("ent Req in requestProject::ETSUserAccessRequest=="+projReq);

			if (projReq) {

				etsStatus.setErrCode(0);
				etsStatus.setErrText("Project request is success");
			} else {

				etsStatus.setErrCode(-1);
				etsStatus.setErrText("Error requesting Project");
			}
			
		} catch (Exception eX) {
			etsStatus.setErrCode(-1);
			etsStatus.setErrText("Error requesting Project");
		}
		return (etsStatus);
	}

	/**
		 * 
		 * @return
		 */

	public ETSStatus requestEntitlement(DecafEntAccessObj decafEntAccObj) throws SQLException, Exception {

		Connection conn = null;
		ETSStatus etsStatus = new ETSStatus();

		try {

			conn = WrkSpcTeamUtils.getConnection();
			etsStatus = requestEntitlement(conn, decafEntAccObj);

		} finally {

			ETSDBUtils.close(conn);
		}

		return etsStatus;

	}

	/**
	 * 
	 * @param conn
	 * @param decafEntAccObj
	 * @return
	 */

	public ETSStatus requestEntitlement(Connection conn, DecafEntAccessObj decafEntAccObj) {

		ETSStatus etsStatus = new ETSStatus();
		UserEntitlementsMgrIF entReqMgrIF = new UserEntReqUtilsImpl();

		try {
			// initialize user & poc details
			userDetails.setWebId(this.getTmIrId());
			userDetails.extractUserDetails(conn);
			pocDetails.setWebId(this.getPmIrId());
			pocDetails.extractUserDetails(conn);

			// update user company and pocid(if MPOC1000)
			if (this.isAProject() && userDetails.getUserType()==userDetails.USER_TYPE_EXTERNAL) {
				this.updateUserDetailsForWrkSpc(conn);
				// refresh pocid & updated company
				userDetails.extractUserDetails(conn);
			}

			boolean entReq = entReqMgrIF.requestEntitlementToUser(this.getTmIrId(), this.getPmIrId(), decafEntAccObj);
			
			logger.debug("ent Req in requestEntitlement::ETSUserAccessRequest=="+entReq);

			if (entReq) {

				etsStatus.setErrCode(0);
				etsStatus.setErrText("Entitlement request is success");

			} else {

				etsStatus.setErrCode(-1);
				etsStatus.setErrText("Error requesting entitlement");
			}
			
		} catch (Exception eX) {
			etsStatus.setErrCode(-1);
			etsStatus.setErrText("Error requesting entitlement");
		}
		return (etsStatus);
	}

	// private variables
	private String tmIrId;
	private String entl;
	private String projectName;
	private boolean isAProject;
	private String pmIrId;
	private String userCompany;
	private String userCountry;
	private ETSUserDetails userDetails;
	private ETSUserDetails pocDetails;

	private String getPmIrId() {
		return pmIrId;
	}
	private String getTmIrId() {
		return tmIrId;
	}
	private String getDecafEntitlement() {
		return entl;
	}
	private boolean isAProject() {
		return this.isAProject;
	}
	private String getUserCompany() {
		return userCompany;
	}

	private void updateUserDetails(Connection conn) {
		try {

			if (this.getUserCompany() != null && !this.getUserCompany().trim().equalsIgnoreCase("")) {
				String qry = "update decaf.users set assoc_company='" + this.getUserCompany() + "',last_user='"+pocDetails.getEdgeId()+"',last_timestamp=current timestamp where userid='" + userDetails.getEdgeId() + "'";
				EntitledStatic.fireUpdate(conn, qry);
			}

			if (this.getUserCountry() != null && !this.getUserCountry().trim().equalsIgnoreCase("")) {
				String qry = "update decaf.users set country_code='" + this.getUserCountry() + "',last_user='"+pocDetails.getEdgeId()+"',last_timestamp=current timestamp  where userid='" + userDetails.getEdgeId() + "'";
				EntitledStatic.fireUpdate(conn, qry);
			}

			if (userDetails.getPocId() != null && userDetails.getPocId().equals("MPOC1000")) {
				String pocUpd = "update decaf.users set poc_id='" + pocDetails.getDecafId() + "' , poc_mail='" + pocDetails.getEMail() + "',last_user='"+pocDetails.getEdgeId()+"',last_timestamp=current timestamp  where userid = '" + userDetails.getEdgeId() + "'";
				EntitledStatic.fireUpdate(conn, pocUpd);
			}
		} catch (AMTException amtEx) {
		} catch (SQLException sqlEx) {
		}
	}
	
	private void updateUserDetailsForWrkSpc(Connection conn) {
			try {

				if (this.getUserCompany() != null && !this.getUserCompany().trim().equalsIgnoreCase("")) {
					String qry = "update decaf.users set assoc_company='" + this.getUserCompany() + "',last_user='"+pocDetails.getEdgeId()+"',last_timestamp=current timestamp where userid='" + userDetails.getEdgeId() + "'";
					EntitledStatic.fireUpdate(conn, qry);
				}

				if (this.getUserCountry() != null && !this.getUserCountry().trim().equalsIgnoreCase("")) {
					String qry = "update decaf.users set country_code='" + this.getUserCountry() + "',last_user='"+pocDetails.getEdgeId()+"',last_timestamp=current timestamp  where userid='" + userDetails.getEdgeId() + "'";
					EntitledStatic.fireUpdate(conn, qry);
				}

				if (userDetails.getUserType()==userDetails.USER_TYPE_EXTERNAL) {
					
					String pocFName=AmtCommonUtils.getTrimStr(pocDetails.getFirstName());
					String pocLName=AmtCommonUtils.getTrimStr(pocDetails.getLastName());
					
					String pocFullName=pocFName+" "+pocLName;
					
					if(AmtCommonUtils.isResourceDefined(pocFullName)) {
						
						if(pocFullName.length() >40) {
							
							pocFullName=pocFullName.substring(0,40);
						}
					}
					String pocUpd = "update decaf.users set poc_id='" + pocDetails.getDecafId() + "', poc_name='"+pocFullName+"', poc_mail='" + pocDetails.getEMail() + "',last_user='"+pocDetails.getEdgeId()+"',last_timestamp=current timestamp  where userid = '" + userDetails.getEdgeId() + "'";
					EntitledStatic.fireUpdate(conn, pocUpd);
				}
			} catch (AMTException amtEx) {
			} catch (SQLException sqlEx) {
			}
		}


	private synchronized String getUniqueToken() throws Exception {
		java.util.Date d = new java.util.Date();
		long lastID = d.getTime();
		try {
			Thread.sleep(2);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		String token = Long.toString(lastID);
		return (token);
	}

	/**
	 * @return
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param string
	 */
	public void setProjectName(String string) {
		projectName = string;
	}

	/**
	 * @return
	 */
	public String getUserCountry() {
		return userCountry;
	}

	/**
	 * @param string
	 */
	public void setUserCountry(String string) {
		userCountry = string;
	}

}
