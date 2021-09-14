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

import java.util.*;
import java.sql.*;

import oem.edge.common.SysLog;
import oem.edge.common.DbConnect;
import oem.edge.ets.fe.ubp.*;

public class ETSAccessRequestHome {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.7";

	private java.sql.Connection conn = null;

	private final String attributes_sql =
		"select " +
		"REQUEST_ID, USER_ID, " +
		"PROJECT_NAME, MGR_EMAIL, " +
		"DATE_REQUESTED, REQUESTED_BY, " +
		"STATUS " +
		"from ETS.ETS_ACCESS_REQ ";

	private final String standard_order = "order by DATE_REQUESTED desc for read only";

	ETSAccessRequestHome (java.sql.Connection conn) {
		super();
		this.conn = conn;
	}

	ETSAccessRequest getAccessRequestByRequestId(int request_id) {
		String sql = attributes_sql +"where REQUEST_ID="
						+Integer.toString(request_id)+" "
						+standard_order;
		ETSAccessRequestList list = runQuery(sql);
		if (list.size() == 0) {
			SysLog.log(SysLog.DEBUG,this,"No ETS_ACCESS_REQ found for request_id="+request_id);
			return new ETSAccessRequest();
		}
		if (list.size() > 1) {
			SysLog.log(SysLog.ERR,this,"More than one ETS_ACCESS_REQ for request_id="+request_id);
		}
		return list.get(0);   // must be singleton list
	}


	ETSAccessRequestList getAllAccessRequests() {
		String sql = attributes_sql + standard_order;
		SysLog.log(SysLog.DEBUG,this,"All Requests sql: "+sql);
		return runQuery(sql);
	}

	int countPendingByOwnerEMail(String owner_email,String wrkSpcType) {
		String count = "select count(*) as COUNT from ETS.ETS_ACCESS_REQ ";
		String sql = count +"where status='"+Defines.ACCESS_PENDING+"' "+
					"and MGR_EMAIL='"+owner_email+"'";
		SysLog.log(SysLog.DEBUG,this,"Pend sql: "+sql);
		return runCount(sql);
	}

	ETSAccessRequestList getPendingByOwnerEMail(String owner_email,String wrkSpcType) {
		String sql = attributes_sql +"where status='"+Defines.ACCESS_PENDING+"' "+
					"and MGR_EMAIL='"+owner_email+"' "+ standard_order;
		SysLog.log(SysLog.DEBUG,this,"Pend sql: "+sql);
		return runQuery(sql);
	}

	ETSAccessRequestList getAccessRequestsByRequestorID(String requestorId) {
		String sql = "";
		return runQuery(sql);
	}

	ETSAccessRequestList getAccessRequestsByMgrEMail(String aMgrEMail) {
		String sql = "";
		return runQuery(sql);
	}

	ETSAccessRequestList getAccessRequestsByProjName(String projName) {
		String sql = "select REQUEST_ID, , ";
		return runQuery(sql);
	}

	ETSAccessRequestList runQuery(String sql) {
		ResultSet rs = null;
		ETSAccessRequestList found = new ETSAccessRequestList(new ArrayList());
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			while(rs.next()){
				int key = rs.getInt("REQUEST_ID");
				Timestamp tsdate = rs.getTimestamp("DATE_REQUESTED");
				String userid = fixStr(rs.getString("USER_ID"));
				String project = fixStr(rs.getString("PROJECT_NAME"));
				String mgr_email = fixStr(rs.getString("MGR_EMAIL"));
				String requestor = fixStr(rs.getString("REQUESTED_BY"));
				String status = fixStr(rs.getString("STATUS"));
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId(userid);   u.extractUserDetails(conn);
				ETSAccessRequest ar = new ETSAccessRequest(key,tsdate,userid,project,
												mgr_email, requestor, status, u);
				found.add(ar);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			found = new ETSAccessRequestList(new ArrayList());
		}
		finally {
			ETSDBUtils.close(rs);
		}


		return found;
	}

	int runCount(String sql) {
		ResultSet rs = null;
		int count = 0;
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			rs.next();
			count = rs.getInt("COUNT");

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			ETSDBUtils.close(rs);
		}
		return count;
	}

	public void Accept(int id, String actionby, String reply) {
		updStatus(id, Defines.ACCESS_APPROVED, actionby,  reply);
	}
	public void Reject(int id, String actionby, String reply) {
		updStatus(id, Defines.ACCESS_REJECTED, actionby, reply);
	}

	private void updStatus(int id, String status, String actionby, String reply) {
		try {
			Statement stmt = conn.createStatement();
			int k = stmt.executeUpdate("update ETS.ETS_ACCESS_REQ set "
					+"STATUS='"+status+"', "
					+"ACTION_BY='"+actionby+"', "
					+"REPLY_TEXT='"+reply+"' "
					+"where REQUEST_ID="+Integer.toString(id));
			if (k!=1) {
				SysLog.log(SysLog.WARNING,this,"REQUEST Update Status has an unexpected update count = "+k);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void close() {
		ETSDBUtils.close(this.conn);
	}

	private String fixStr(String s) {
		if (s==null) return "";
		return s;
	}
}

