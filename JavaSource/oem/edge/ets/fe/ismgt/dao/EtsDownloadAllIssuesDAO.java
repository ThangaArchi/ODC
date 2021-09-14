/*
 * Created on Mar 23, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.ismgt.dao;

import java.sql.*;
import java.util.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import oem.edge.ets.fe.ismgt.bdlg.DownloadAllIssuesDataPrepRep;
import oem.edge.ets.fe.ismgt.helpers.*;
import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ETSProj;

/**
 * @author Shanmugam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EtsDownloadAllIssuesDAO  implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.0";
	private EtsIssFilterObjectKey issobjkey;
	private String projectId;

	private EtsIssObjectKey etsIssObjKey;

	/**
	 * Constructor for EtsDownloadAllIssuesDAO.
	 */
	public EtsDownloadAllIssuesDAO(EtsIssFilterObjectKey issobjkey) {
		super();
		this.issobjkey = issobjkey;
		this.projectId = issobjkey.getProjectId();
	}

	
	
	/**
	 * This method will prepare an ArrayList of Report Table Objects for the list
	 * of Issues/changes, to be displayed based on selection
	 */

	public ArrayList getReportTabListWithPstmt(String projectId) {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
			
		
		ArrayList etsRepList = new ArrayList();
		DownloadAllIssuesDataPrepRep downloadAllIssDataPrepRep = new DownloadAllIssuesDataPrepRep(issobjkey);
		String query = downloadAllIssDataPrepRep.getFinalQryStrWithPstmt();

		try {

			SysLog.log(SysLog.DEBUG, "getEtsIssuReportTabList pstmt qry", "getEtsIssuReportTabList pstmt qry=" + query + ":");
			Global.println("getEtsIssuReportTabList pstmt qry=" + query + ":");

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.prepareStatement(query);

			stmt.clearParameters();
			
			stmt.setString(1, projectId);
			stmt.setString(2, projectId);
			stmt.setString(3, projectId);

			rs = stmt.executeQuery();

			//new flags
			String issueZone = "";
			String txnFlag = "";
			String edgeProblemId = "";
			String cqTrkId = "";
			String probState = "";
			String finalResState = "";
			String issueSource = "";
			String userLastAction = "";
			String refId="";
			String comments = "";
			String description = "";

			EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
			EtsIssActionGuiUtils actGuiUtils = new EtsIssActionGuiUtils();
			
			Hashtable htRowKeyVal = null;
			if (rs != null) {
				while (rs.next()) {
					htRowKeyVal = new Hashtable();
					EtsIssFilterRepTabBean issueRepTab = new EtsIssFilterRepTabBean();

					issueZone = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEZONE"));
					edgeProblemId = EtsIssFilterUtils.getTrimStr(rs.getString("EDGEPROBLEMID"));
					cqTrkId = EtsIssFilterUtils.getTrimStr(rs.getString("CQTRKID"));
					
					probState = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMSTATE"));
					userLastAction = EtsIssFilterUtils.getTrimStr(rs.getString("USERLASTACTION"));
					issueSource = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUESOURCE"));
					refId=EtsIssFilterUtils.getTrimStr(rs.getString("REFID"));
					

					
					ArrayList descCommList = getDescCommListWithPstmt(edgeProblemId);
					if(descCommList.size() > 1) {
						description = descCommList.get(0).toString();
						comments = descCommList.get(1).toString();
					} 
						
						
					if (issueSource.equals(ETSPMOSOURCE)) {

						txnFlag = crPmoDao.getPmoCrTxnFlag(edgeProblemId);

						finalResState = actGuiUtils.getUpdatedStateAction(cqTrkId, userLastAction, probState, txnFlag);

					} else {

						finalResState = probState;
					}
									
					htRowKeyVal.put("ID", refId);
					htRowKeyVal.put("ISSUETITLE", EtsIssFilterUtils.getTrimStr(rs.getString("TITLE")) ); 
					htRowKeyVal.put("ISSUETYPE", EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMTYPE")));							
					htRowKeyVal.put("SUBMITTER", EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTERNAME")));
					
					htRowKeyVal.put("SUBMITTERSCOMPANY", EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTERSCOMPANY")));
					
					htRowKeyVal.put("SUBMITTEREMAIL", EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTEREMAIL")));
					
					htRowKeyVal.put("SUBMITTERPHONE", EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTERPHONE")));
					
					htRowKeyVal.put("ISSUESUBMISSIONDATE", EtsIssFilterUtils.getTrimStr(rs.getTimestamp("ISSUESUBMISSIONDATE").toString()) );
					
					
					
                    htRowKeyVal.put("OWNERNAME", EtsIssFilterUtils.getTrimStr(rs.getString("OWNERNAME")));
                    htRowKeyVal.put("SEVERITY", EtsIssFilterUtils.getTrimStr(rs.getString("SEVERITY")));
                    htRowKeyVal.put("STATUS", finalResState);
                    htRowKeyVal.put("DESCRIPTION", description);
                    htRowKeyVal.put("COMMENTS", comments);
                    htRowKeyVal.put("1", EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C1")));
                    htRowKeyVal.put("2", EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C2")));
                    htRowKeyVal.put("3", EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C3")));
                    htRowKeyVal.put("4", EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C4")));
                    htRowKeyVal.put("5", EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C5")));
                    htRowKeyVal.put("6", EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C6")));
                    htRowKeyVal.put("7", EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C7")));
                    htRowKeyVal.put("8", EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C8")));

                                        
					etsRepList.add(htRowKeyVal);

				}

			}

		} catch (SQLException se) {
			
			se.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}

		return etsRepList;

	}
	
	
	
	public ArrayList getDescCommListWithPstmt(String problemId) {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
			
		
		ArrayList descCommList = new ArrayList();
		DownloadAllIssuesDataPrepRep downloadAllIssDataPrepRep = new DownloadAllIssuesDataPrepRep(issobjkey);
		String query = downloadAllIssDataPrepRep.getDescCommQryStrWithPstmt();

		try {

			SysLog.log(SysLog.DEBUG, "getEtsIssuReportTabList pstmt qry", "getEtsIssuReportTabList pstmt qry=" + query + ":  problemId " + problemId);
			Global.println("getEtsIssuReportTabList pstmt qry=" + query + ":  problemId " + problemId);

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.prepareStatement(query);

			stmt.clearParameters();
			
			
			stmt.setString(1, problemId);

			rs = stmt.executeQuery();

			//new flags
			String probDesc = "";
			String custComm = "";
			
			Hashtable htRowKeyVal = null;
			if (rs.next()) {			
				if(rs.getString("PROBDESC") != null)
					descCommList.add(0, EtsIssFilterUtils.getTrimStr(rs.getString("PROBDESC")));
				else
					descCommList.add(0, "");
				
				if(rs.getString("CUSTCOMM") != null)
					descCommList.add(1, EtsIssFilterUtils.getTrimStr(rs.getString("CUSTCOMM")));
				else
					descCommList.add(1, "");
				
			
			} else {
				descCommList.add(0, "");
				descCommList.add(1, "");
			}
			
		} catch (SQLException se) {
			
			se.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}
		return descCommList;	
	}
}

