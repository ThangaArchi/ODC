package oem.edge.ets.fe.ismgt.dao;

import java.sql.*;
import java.util.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import oem.edge.ets.fe.ismgt.helpers.*;
import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ETSProj;


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
 * An abstract class for getting the DB details for issues/changes
 * 
 */
public abstract class FilterDAOAbs implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.33.1.20";
	private EtsIssFilterObjectKey issobjkey;
	private String projectId;

	private EtsIssObjectKey etsIssObjKey;

	/**
	 * Constructor for FilterDAO.
	 */
	public FilterDAOAbs(EtsIssFilterObjectKey issobjkey) {
		super();
		this.issobjkey = issobjkey;
		this.projectId = issobjkey.getProjectId();
	}

	/**
		 * Constructor for FilterDAO from issue actions stuffs.
		 */
	public FilterDAOAbs(EtsIssObjectKey etsIssObjKey) {
		super();
		this.etsIssObjKey = etsIssObjKey;
		this.projectId = etsIssObjKey.getProj().getProjectId();
	}

	/**
	 * This method will give list of ETS users in a given project
	 * i.e the List of Users of ETS Project, who have submitted issues
	 * takes a join btwn ets.ets_users/amt.users(change to decaf.users if performace is an issue)
	 * ets.ets_users >> userid === ir_userid in amt.users
	 */

	public ArrayList getProjMemberList() throws SQLException, Exception {

		String pType = null;
		String adminId = null;
		Connection con = null;
		EtsProjMemberDAO projdao = new EtsProjMemberDAO();
		
		try{
			// To get the Admin Id in the ProjMemberList based on the projType (ETS/AIC)
			con = ETSDBUtils.getConnection();
			ETSProj proj = ETSUtils.getProjectDetails(con,projectId);
			pType = proj.getProjectType();
						
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
				if (con != null) {
				ETSDBUtils.close(con);
			}
		}
		
        if(pType.equals(Defines.AIC_WORKSPACE_TYPE)){
			adminId = Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT;
        }else{
        	adminId = Defines.ETS_ADMIN_ENTITLEMENT;	
        }
        
		return projdao.getProjMemberList(projectId,adminId);
	}

	/**
	 * This method will give an ArrayList of IssueType(s) and uses getIssueType() of ETSIssuesManager
	 * and returns ArrayList
	 * boundary condition>> that problem_type in CQ.PROBLEM_ID_DATA has length=varchar 50 whereas project_name
	 * in ets.ets_projects is project_name is  varchar 128 ( ?? for jeetrao)
	 * 
	 */

	public ArrayList getIssueTypes(String subTypeA) throws SQLException, Exception {

		ArrayList issueTypeList = new ArrayList();

		issueTypeList.add("All");
		StringBuffer sb = new StringBuffer();
		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		//get the drop list
		EtsDropDownDataBean dropModel = new EtsDropDownDataBean();

		//
		String qryIssueSource = "";

		///get params
		dropModel.setProjectId(issobjkey.getProjectId());
		dropModel.setIssueClass(issobjkey.getIssueSubType());
		dropModel.setActiveFlag("Y");
		dropModel.setIssueSource(qryIssueSource);

		String userType = issobjkey.getEs().gDECAFTYPE;

		if (userType.equals("I")) {

			dropModel.setIssueAccess("IBM");
		} else {

			dropModel.setIssueAccess("ALL");

		}

		ArrayList dropList = dropDao.getChangeTypes(dropModel);

		///

		String issueType = "";

		int size = 0;

		if (dropList != null && !dropList.isEmpty()) {

			size = dropList.size();

		}

		for (int i = 0; i < size; i++) {

			EtsDropDownDataBean dropBean = (EtsDropDownDataBean) dropList.get(i);

			issueType = dropBean.getIssueType();

			issueTypeList.add(issueType);

		}

		return issueTypeList;

	}

	/**
	 * This method will give an ArrayList of Severity Types
	 * All values are now hard-coded
	 */

	public abstract ArrayList getSeverityTypes();

	/**
	 * This method will give an ArrayList of Status of Issue Types
	 * All values are now hard-coded
	 */

	public abstract ArrayList getStatusTypes();

	/***
	 * To return the ArrayList of Owners 
	 */

	public abstract ArrayList getOwnersList() throws SQLException, Exception;

	/***
	 * To return the ArrayList of Submitters
	 */

	public abstract ArrayList getSubmitterList() throws SQLException, Exception;

	/**
	 * This method will prepare an ArrayList of Report Table Objects for the list
	 * of Issues/changes, to be displayed based on selection
	 */

	public ArrayList getReportTabList(EtsIssFilterQryPrepBean etsQryPrep) throws SQLException, Exception {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer();
		ArrayList etsRepList = new ArrayList();
		String issueCurrentOwner = "";
		ArrayList curOwnerList = new ArrayList();
		ArrayList blnkList = new ArrayList();
		String curOwnQryStr = "";

		///
		String edgeProblemId = "";
		String cqTrkId = "";
		String probState = "";
		String userLastAction = "";
		String issueSource = "";
		String finalResState = "";
		String txnFlag = "";

		EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
		EtsIssActionGuiUtils actGuiUtils = new EtsIssActionGuiUtils();

		try {

			//get the final qry str

			sb.append(etsQryPrep.getFinalQryStr());

			SysLog.log(SysLog.DEBUG, "getEtsIssuReportTabList PLAIN qry", "getEtsIssuReportTabList PLAIN qry =" + sb.toString() + ":");
			//Global.println("getEtsIssuReportTabList PLAIN qry =" + sb.toString() + ":");

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {
				while (rs.next()) {

					EtsIssFilterRepTabBean issueRepTab = new EtsIssFilterRepTabBean();

					////////////////st

					edgeProblemId = EtsIssFilterUtils.getTrimStr(rs.getString("EDGEPROBLEMID"));
					cqTrkId = EtsIssFilterUtils.getTrimStr(rs.getString("CQTRKID"));
					probState = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMSTATE"));
					userLastAction = EtsIssFilterUtils.getTrimStr(rs.getString("USERLASTACTION"));
					issueSource = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUESOURCE"));

					if (issueSource.equals(ETSPMOSOURCE)) {

						txnFlag = crPmoDao.getPmoCrTxnFlag(edgeProblemId);

						finalResState = actGuiUtils.getUpdatedStateAction(cqTrkId, userLastAction, probState, txnFlag);

					} else {

						finalResState = probState;
					}

					issueRepTab.setIssueZone(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEZONE")));
					issueRepTab.setIssueProblemId(edgeProblemId);
					issueRepTab.setIssueCqTrkId(cqTrkId);
					issueRepTab.setIssueStatus(finalResState);
					issueRepTab.setIssueSubmitter(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMCREATOR")));
					issueRepTab.setIssueTitle(EtsIssFilterUtils.getTrimStr(rs.getString("TITLE")));
					issueRepTab.setIssueClass(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMCLASS")));
					issueRepTab.setIssueSeverity(EtsIssFilterUtils.getTrimStr(rs.getString("SEVERITY")));
					issueRepTab.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMTYPE")));
					issueRepTab.setCurrentOwnerId(EtsIssFilterUtils.getTrimStr(rs.getString("CURRENTOWNER")));
					issueRepTab.setCurrentOwnerName(EtsIssFilterUtils.getTrimStr(rs.getString("OWNERNAME")));
					issueRepTab.setIssueSubmitterName(EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTERNAME")));
					issueRepTab.setIssueLastTime(EtsIssFilterUtils.getTrimStr(rs.getString("LASTTIME")));
					issueRepTab.setIssueSource(issueSource);

					///////////////////end

					etsRepList.add(issueRepTab);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}

		return etsRepList;

	}

	/**
		 * This method will prepare an ArrayList of Report Table Objects for the list
		 * of Issues/changes, to be displayed based on selection
		 */

	public ArrayList getReportTabListForCr(EtsIssFilterQryPrepBean etsQryPrep) throws SQLException, Exception {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer();
		ArrayList etsRepList = new ArrayList();
		String issueCurrentOwner = "";
		ArrayList curOwnerList = new ArrayList();
		ArrayList blnkList = new ArrayList();
		String curOwnQryStr = "";

		//
		String probTypeQryStr = "";

		try {

			probTypeQryStr = etsQryPrep.getProblemTypeQryStr();

			Global.println("prob type qry str==" + probTypeQryStr);

			sb.append(etsQryPrep.getFinalQryStrForCr());

			SysLog.log(SysLog.DEBUG, "getEtsIssuReportTabList qry", "getEtsIssuReportTabList qry=" + sb.toString() + ":");

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			///
			String etsId = "";
			String pmoId = "";
			String stateAction = "";
			String txnStatusFlag = "";
			String infoSrcFlag = "";
			String probState = "";
			String updProbState = "";

			EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();

			if (rs != null) {
				while (rs.next()) {

					EtsIssFilterRepTabBean issueRepTab = new EtsIssFilterRepTabBean();

					etsId = EtsIssFilterUtils.getTrimStr(rs.getString("EDGEPROBLEMID"));
					pmoId = EtsIssFilterUtils.getTrimStr(rs.getString("CQTRKID"));
					stateAction = EtsIssFilterUtils.getTrimStr(rs.getString("USERLASTACTION"));
					probState = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMSTATE"));
					infoSrcFlag = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMTYPE"));

					txnStatusFlag = crPmoDao.getPmoCrTxnFlag(etsId);

					/////

					//get the updated prob state
					updProbState = getUpdatedStateActionForCR(infoSrcFlag, pmoId, stateAction, probState, txnStatusFlag);

					//////

					issueRepTab.setIssueZone(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEZONE")));
					issueRepTab.setIssueProblemId(etsId);
					issueRepTab.setIssueCqTrkId(pmoId);
					issueRepTab.setIssueStatus(updProbState);
					issueRepTab.setIssueSubmitter(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMCREATOR")));
					issueRepTab.setIssueTitle(EtsIssFilterUtils.getTrimStr(rs.getString("TITLE")));
					issueRepTab.setIssueClass(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMCLASS")));
					issueRepTab.setIssueSeverity(EtsIssFilterUtils.getTrimStr(rs.getString("SEVERITY")));
					issueRepTab.setIssueType(infoSrcFlag);
					issueRepTab.setCurrentOwnerId(EtsIssFilterUtils.getTrimStr(rs.getString("CURRENTOWNER")));
					issueRepTab.setCurrentOwnerName(EtsIssFilterUtils.getTrimStr(rs.getString("OWNERNAME")));
					issueRepTab.setIssueSubmitterName(EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTERNAME")));
					issueRepTab.setIssueLastTime(EtsIssFilterUtils.getTrimStr(rs.getString("LASTTIME")));

					etsRepList.add(issueRepTab);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}

		return etsRepList;

	}

	/**
	 * This method will prepare an ArrayList of Report Table Objects for the list
	 * of Issues/changes, to be displayed based on selection
	 */

	public ArrayList getReportTabListWithPstmt(EtsIssFilterQryPrepBean etsQryPrep) throws SQLException, Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer();
		ArrayList etsRepList = new ArrayList();
		String issueCurrentOwner = "";
		ArrayList curOwnerList = new ArrayList();
		ArrayList blnkList = new ArrayList();
		String curOwnQryStr = "";

		/////strings////
		String issueProjIdStr = "";
		String problemTypeQryStr = "";
		String issueTypeQryStr = "";
		String severityTypeQryStr = "";
		String statusTypeQryStr = "";
		String issueSubmitterTypeQryStr = "";
		String issueCurOwnerQryStr = "";
		String issueDateAllStr = "";
		String issueStartDateStr = "";
		String issueEndDateStr = "";
		String issOpn = "";
		int index = 1;
		boolean isCurOwnerAll = false;

		///lists
		ArrayList prevIssueTypeQryList = new ArrayList();
		ArrayList prevSeverityTypeQryList = new ArrayList();
		ArrayList prevStatusTypeQryList = new ArrayList();
		ArrayList prevIssueSubmitterQryList = new ArrayList();
		ArrayList prevCurOwnerQryList = new ArrayList();
		ArrayList prevProbTypeQryList = new ArrayList();

		//		fxp 1s///
		String issueStartDtMM = "";
		String issueStartDtDD = "";
		String issueStartDtYYYY = "";

		String issueEndDtMM = "";
		String issueEndDtDD = "";
		String issueEndDtYYYY = "";

		Timestamp startTimeSp = null;
		Timestamp endTimeSp = null;
		//fxp 1e///

		try {

			issueProjIdStr = etsQryPrep.getIssueProjIdStr();
			problemTypeQryStr = etsQryPrep.getProblemTypeQryStr();
			issueTypeQryStr = etsQryPrep.getIssueTypeQryStr();
			severityTypeQryStr = etsQryPrep.getSeverityTypeQryStr();
			statusTypeQryStr = etsQryPrep.getStatusTypeQryStr();
			issueSubmitterTypeQryStr = etsQryPrep.getIssueSubmitterTypeQryStr();
			issueCurOwnerQryStr = etsQryPrep.getIssueCurOwnerQryStr();
			issueDateAllStr = etsQryPrep.getIssueDateAllStr();
			issueStartDateStr = etsQryPrep.getIssueStartDateStr();
			issueEndDateStr = etsQryPrep.getIssueEndDateStr();
			issOpn = etsQryPrep.getIssOpn();

			prevIssueTypeQryList = etsQryPrep.getPrevIssueTypeQryList();
			prevSeverityTypeQryList = etsQryPrep.getPrevSeverityTypeQryList();
			prevStatusTypeQryList = etsQryPrep.getPrevStatusTypeQryList();
			prevIssueSubmitterQryList = etsQryPrep.getPrevIssueSubmitterQryList();
			prevCurOwnerQryList = etsQryPrep.getPrevCurOwnerQryList();
			prevProbTypeQryList = etsQryPrep.getPrevProbTypeQryList();

			///
			isCurOwnerAll = etsQryPrep.isCurOwnerAll();

			//get the final qry str
			sb.append(etsQryPrep.getFinalQryStrWithPstmt());

			if (AmtCommonUtils.isResourceDefined(issueStartDateStr)) {

				Global.println("issueStartDateStr==@@@" + issueStartDateStr);

				issueStartDtMM = issueStartDateStr.substring(0, 2);
				issueStartDtDD = issueStartDateStr.substring(3, 5);
				issueStartDtYYYY = issueStartDateStr.substring(6);

				startTimeSp = EtsIssFilterUtils.getSelDtSqlTimeStamp(issueStartDtYYYY, issueStartDtMM, issueStartDtDD);

			}

			if (AmtCommonUtils.isResourceDefined(issueEndDateStr)) {

				Global.println("issueEndDateStr==@@@" + issueEndDateStr);

				issueEndDtMM = issueEndDateStr.substring(0, 2);
				issueEndDtDD = issueEndDateStr.substring(3, 5);
				issueEndDtYYYY = issueEndDateStr.substring(6);

				endTimeSp = EtsIssFilterUtils.getSelDtSqlTimeStamp(issueEndDtYYYY, issueEndDtMM, issueEndDtDD, "23", "59", "59");

			}

			SysLog.log(SysLog.DEBUG, "getEtsIssuReportTabList pstmt qry", "getEtsIssuReportTabList pstmt qry=" + sb.toString() + ":");
			Global.println("getEtsIssuReportTabList pstmt qry=" + sb.toString() + ":");

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.prepareStatement(sb.toString());

			stmt.clearParameters();
			////
			////new -------for brand new issues, for which cq_trk_id has not been assigned-------///
			if (!issOpn.substring(0, 1).equals("4") && isCurOwnerAll) { //only if not issues  assigned to me

				//stmt.setString(index++,problemTypeQryStr);
				index = setInClauseValues(stmt, index, prevProbTypeQryList);
				stmt.setString(index++, issueProjIdStr);

				if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {

					stmt.setString(index++, issueProjIdStr);
					index = setInClauseValues(stmt, index, prevProbTypeQryList);
					index = setInClauseValues(stmt, index, prevIssueTypeQryList);

				}

				if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

					index = setInClauseValues(stmt, index, prevSeverityTypeQryList);

				}

				if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

					index = setInClauseValues(stmt, index, prevStatusTypeQryList);

				}

				if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

					index = setInClauseValues(stmt, index, prevIssueSubmitterQryList);

				}

				if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

					stmt.setTimestamp(index++, startTimeSp);
					stmt.setTimestamp(index++, endTimeSp);

				}

			}

			////-------for existing issues, for which cq_trk_id has been assigned and in work flow, and waiting for user resp(cq.seq_no > usrseq_no-------////

			//stmt.setString(index++,problemTypeQryStr);
			index = setInClauseValues(stmt, index, prevProbTypeQryList);
			stmt.setString(index++, issueProjIdStr);

			if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {

				stmt.setString(index++, issueProjIdStr);
				index = setInClauseValues(stmt, index, prevProbTypeQryList);
				index = setInClauseValues(stmt, index, prevIssueTypeQryList);

			}

			if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

				index = setInClauseValues(stmt, index, prevSeverityTypeQryList);

			}

			if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

				index = setInClauseValues(stmt, index, prevStatusTypeQryList);

			}

			if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

				index = setInClauseValues(stmt, index, prevIssueSubmitterQryList);

			}

			if (!AmtCommonUtils.getTrimStr(issueCurOwnerQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueCurOwnerQryStr)) {

				index = setInClauseValues(stmt, index, prevCurOwnerQryList);

			}

			if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

				stmt.setTimestamp(index++, startTimeSp);
				stmt.setTimestamp(index++, endTimeSp);

			}

			////-------for existing issues, for which cq_trk_id has been assigned and in work flow, and waiting for cq resp(usr.seq_no > cq.seq_no-------////
			//show only when it is All or 'In Process'

			boolean prvStListPstmt = false;

			if (prevStatusTypeQryList != null && !prevStatusTypeQryList.isEmpty()) {

				if (prevStatusTypeQryList.contains("In Process")) {

					prvStListPstmt = true;

				}

			}

			if (AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr) || prvStListPstmt) {

				//stmt.setString(index++,problemTypeQryStr);
				index = setInClauseValues(stmt, index, prevProbTypeQryList);
				stmt.setString(index++, issueProjIdStr);

				if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {

					stmt.setString(index++, issueProjIdStr);
					index = setInClauseValues(stmt, index, prevProbTypeQryList);
					index = setInClauseValues(stmt, index, prevIssueTypeQryList);

				}

				if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

					index = setInClauseValues(stmt, index, prevSeverityTypeQryList);

				}

				//			if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {
				//
				//				index = setInClauseValues(stmt, index, prevStatusTypeQryList);
				//
				//			}

				if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

					index = setInClauseValues(stmt, index, prevIssueSubmitterQryList);

				}

				if (!AmtCommonUtils.getTrimStr(issueCurOwnerQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueCurOwnerQryStr)) {

					index = setInClauseValues(stmt, index, prevCurOwnerQryList);

				}

				if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

					stmt.setTimestamp(index++, startTimeSp);
					stmt.setTimestamp(index++, endTimeSp);

				}

			} //show only when it is All or 'In Process'

			/////pmo st

			//			//

			stmt.setString(index++, issueProjIdStr);

			stmt.setString(index++, issueProjIdStr);

			//			if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {
			//				
			//				
			//				stmt.setString(index++, issueProjIdStr);
			//				index = setInClauseValues(stmt, index, prevProbTypeQryList);
			//				index = setInClauseValues(stmt, index, prevIssueTypeQryList);
			//
			//			}

			if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

				index = setInClauseValues(stmt, index, prevSeverityTypeQryList);

			}

			if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

				index = setInClauseValues(stmt, index, prevStatusTypeQryList);

			}

			if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

				index = setInClauseValues(stmt, index, prevIssueSubmitterQryList);

			}

			if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

				stmt.setTimestamp(index++, startTimeSp);
				stmt.setTimestamp(index++, endTimeSp);

			}

			//////pmo end

			//			///pmo in process st

			if (prvStListPstmt) {

				stmt.setString(index++, issueProjIdStr);

				stmt.setString(index++, issueProjIdStr);

				//				if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {
				//					
				//					stmt.setString(index++, issueProjIdStr);
				//					index = setInClauseValues(stmt, index, prevProbTypeQryList);
				//					index = setInClauseValues(stmt, index, prevIssueTypeQryList);
				//
				//				}

				if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

					index = setInClauseValues(stmt, index, prevSeverityTypeQryList);

				}

				if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

					index = setInClauseValues(stmt, index, prevIssueSubmitterQryList);

				}

				if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

					stmt.setTimestamp(index++, startTimeSp);
					stmt.setTimestamp(index++, endTimeSp);

				}

			}

			//pmo in process end

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

			EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
			EtsIssActionGuiUtils actGuiUtils = new EtsIssActionGuiUtils();

			if (rs != null) {
				while (rs.next()) {

					EtsIssFilterRepTabBean issueRepTab = new EtsIssFilterRepTabBean();

					issueZone = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEZONE"));
					edgeProblemId = EtsIssFilterUtils.getTrimStr(rs.getString("EDGEPROBLEMID"));
					cqTrkId = EtsIssFilterUtils.getTrimStr(rs.getString("CQTRKID"));
					probState = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMSTATE"));
					userLastAction = EtsIssFilterUtils.getTrimStr(rs.getString("USERLASTACTION"));
					issueSource = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUESOURCE"));
					refId=EtsIssFilterUtils.getTrimStr(rs.getString("REFID"));

					if (issueSource.equals(ETSPMOSOURCE)) {

						txnFlag = crPmoDao.getPmoCrTxnFlag(edgeProblemId);

						finalResState = actGuiUtils.getUpdatedStateAction(cqTrkId, userLastAction, probState, txnFlag);

					} else {

						finalResState = probState;
					}

					issueRepTab.setIssueZone(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEZONE")));
					issueRepTab.setIssueProblemId(edgeProblemId);
					issueRepTab.setIssueCqTrkId(cqTrkId);
					issueRepTab.setIssueStatus(finalResState);
					issueRepTab.setIssueSubmitter(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMCREATOR")));
					issueRepTab.setIssueTitle(EtsIssFilterUtils.getTrimStr(rs.getString("TITLE")));
					issueRepTab.setIssueClass(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMCLASS")));
					issueRepTab.setIssueSeverity(EtsIssFilterUtils.getTrimStr(rs.getString("SEVERITY")));
					issueRepTab.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMTYPE")));
					issueRepTab.setCurrentOwnerId(EtsIssFilterUtils.getTrimStr(rs.getString("CURRENTOWNER")));
					issueRepTab.setCurrentOwnerName(EtsIssFilterUtils.getTrimStr(rs.getString("OWNERNAME")));
					issueRepTab.setIssueSubmitterName(EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTERNAME")));
					issueRepTab.setIssueLastTime(EtsIssFilterUtils.getTrimStr(rs.getString("LASTTIME")));
					issueRepTab.setIssueSource(issueSource);
					issueRepTab.setRefId(refId);

					//					For PMO ISSUES, SHOW In Process records only when the selected option is All/Default state is All/or atleast the qry list
					//contains 'In Process', 
					//otw just follow original flow
					if (issueSource.equals(ETSPMOSOURCE)) {

						if (issueZone.equals("CRETSINPROCESS")) {

							if (finalResState.equals("In Process")) {

								if (prvStListPstmt) {

									etsRepList.add(issueRepTab);

								}

							}

						} else { //if issue zone != CRETSINPROCESS

							if (finalResState.equals("In Process")) {

								if (AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr) || prvStListPstmt) {

									etsRepList.add(issueRepTab);

								}

							} else { //ordinary flow

								etsRepList.add(issueRepTab);

							} //end of In Process logic

						}

					} else { //for non-pmo issues

						etsRepList.add(issueRepTab);
					}

					//etsRepList.add(issueRepTab);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}

		return etsRepList;

	}

	/**
		 * This method will prepare an ArrayList of Report Table Objects for the list
		 * of Issues/changes, to be displayed based on selection
		 */

	public ArrayList getReportTabListWithPstmtForCr(EtsIssFilterQryPrepBean etsQryPrep) throws SQLException, Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer();
		ArrayList etsRepList = new ArrayList();
		String issueCurrentOwner = "";
		ArrayList curOwnerList = new ArrayList();
		ArrayList blnkList = new ArrayList();
		String curOwnQryStr = "";

		/////strings////
		String issueProjIdStr = "";
		String problemTypeQryStr = "";
		String issueTypeQryStr = "";
		String severityTypeQryStr = "";
		String statusTypeQryStr = "";
		String issueSubmitterTypeQryStr = "";
		String issueCurOwnerQryStr = "";
		String issueDateAllStr = "";
		String issueStartDateStr = "";
		String issueEndDateStr = "";
		String issOpn = "";
		int index = 1;
		boolean isCurOwnerAll = false;

		///lists
		ArrayList prevIssueTypeQryList = new ArrayList();
		ArrayList prevSeverityTypeQryList = new ArrayList();
		ArrayList prevStatusTypeQryList = new ArrayList();
		ArrayList prevIssueSubmitterQryList = new ArrayList();
		ArrayList prevCurOwnerQryList = new ArrayList();
		ArrayList prevProbTypeQryList = new ArrayList();

		//		fxp 1s///
		String issueStartDtMM = "";
		String issueStartDtDD = "";
		String issueStartDtYYYY = "";

		String issueEndDtMM = "";
		String issueEndDtDD = "";
		String issueEndDtYYYY = "";

		Timestamp startTimeSp = null;
		Timestamp endTimeSp = null;
		//fxp 1e///

		try {

			issueProjIdStr = etsQryPrep.getIssueProjIdStr();
			problemTypeQryStr = etsQryPrep.getProblemTypeQryStr();
			issueTypeQryStr = etsQryPrep.getIssueTypeQryStr();
			severityTypeQryStr = etsQryPrep.getSeverityTypeQryStr();
			statusTypeQryStr = etsQryPrep.getStatusTypeQryStr();
			issueSubmitterTypeQryStr = etsQryPrep.getIssueSubmitterTypeQryStr();
			issueCurOwnerQryStr = etsQryPrep.getIssueCurOwnerQryStr();
			issueDateAllStr = etsQryPrep.getIssueDateAllStr();
			issueStartDateStr = etsQryPrep.getIssueStartDateStr();
			issueEndDateStr = etsQryPrep.getIssueEndDateStr();
			issOpn = etsQryPrep.getIssOpn();

			prevIssueTypeQryList = etsQryPrep.getPrevIssueTypeQryList();
			prevSeverityTypeQryList = etsQryPrep.getPrevSeverityTypeQryList();
			prevStatusTypeQryList = etsQryPrep.getPrevStatusTypeQryList();
			prevIssueSubmitterQryList = etsQryPrep.getPrevIssueSubmitterQryList();
			prevCurOwnerQryList = etsQryPrep.getPrevCurOwnerQryList();
			prevProbTypeQryList = etsQryPrep.getPrevProbTypeQryList();

			///
			isCurOwnerAll = etsQryPrep.isCurOwnerAll();

			//get the final qry str
			sb.append(etsQryPrep.getFinalQryStrWithPstmtForCr());

			if (AmtCommonUtils.isResourceDefined(issueStartDateStr)) {

				Global.println("issueStartDateStr==@@@" + issueStartDateStr);

				issueStartDtMM = issueStartDateStr.substring(0, 2);
				issueStartDtDD = issueStartDateStr.substring(3, 5);
				issueStartDtYYYY = issueStartDateStr.substring(6);

				startTimeSp = EtsIssFilterUtils.getSelDtSqlTimeStamp(issueStartDtYYYY, issueStartDtMM, issueStartDtDD);

			}

			if (AmtCommonUtils.isResourceDefined(issueEndDateStr)) {

				Global.println("issueEndDateStr==@@@" + issueEndDateStr);

				issueEndDtMM = issueEndDateStr.substring(0, 2);
				issueEndDtDD = issueEndDateStr.substring(3, 5);
				issueEndDtYYYY = issueEndDateStr.substring(6);

				endTimeSp = EtsIssFilterUtils.getSelDtSqlTimeStamp(issueEndDtYYYY, issueEndDtMM, issueEndDtDD, "23", "59", "59");

			}

			SysLog.log(SysLog.DEBUG, "getEtsIssuReportTabList pstmt qry", "getEtsIssuReportTabList pstmt qry=" + sb.toString() + ":");

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.prepareStatement(sb.toString());

			stmt.clearParameters();
			////

			stmt.setString(index++, issueProjIdStr);

			if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

				index = setInClauseValues(stmt, index, prevSeverityTypeQryList);

			}

			if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

				index = setInClauseValues(stmt, index, prevStatusTypeQryList);

			}

			if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

				index = setInClauseValues(stmt, index, prevIssueSubmitterQryList);

			}

			if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

				stmt.setTimestamp(index++, startTimeSp);
				stmt.setTimestamp(index++, endTimeSp);

			}

			/////

			rs = stmt.executeQuery();

			String etsId = "";
			String pmoId = "";
			String stateAction = "";
			String probState = "";
			String updProbState = "";
			String txnStatusFlag = "";
			String infoSrcFlag = "";
			String refId="";

			EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();

			if (rs != null) {
				while (rs.next()) {

					EtsIssFilterRepTabBean issueRepTab = new EtsIssFilterRepTabBean();

					etsId = EtsIssFilterUtils.getTrimStr(rs.getString("EDGEPROBLEMID"));
					pmoId = EtsIssFilterUtils.getTrimStr(rs.getString("CQTRKID"));
					stateAction = EtsIssFilterUtils.getTrimStr(rs.getString("USERLASTACTION"));
					probState = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMSTATE"));
					infoSrcFlag = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMTYPE"));
					refId=EtsIssFilterUtils.getTrimStr(rs.getString("REFID"));

					txnStatusFlag = crPmoDao.getPmoCrTxnFlag(etsId);

					//get the updated prob state
					updProbState = getUpdatedStateActionForCR(infoSrcFlag, pmoId, stateAction, probState, txnStatusFlag);

					issueRepTab.setIssueZone(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEZONE")));
					issueRepTab.setIssueProblemId(etsId);
					issueRepTab.setIssueCqTrkId(pmoId);
					issueRepTab.setIssueStatus(updProbState);
					issueRepTab.setIssueSubmitter(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMCREATOR")));
					issueRepTab.setIssueTitle(EtsIssFilterUtils.getTrimStr(rs.getString("TITLE")));
					issueRepTab.setIssueClass(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMCLASS")));
					issueRepTab.setIssueSeverity(EtsIssFilterUtils.getTrimStr(rs.getString("SEVERITY")));
					issueRepTab.setIssueType(infoSrcFlag);
					issueRepTab.setCurrentOwnerId(EtsIssFilterUtils.getTrimStr(rs.getString("CURRENTOWNER")));
					issueRepTab.setCurrentOwnerName(EtsIssFilterUtils.getTrimStr(rs.getString("OWNERNAME")));
					issueRepTab.setIssueSubmitterName(EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTERNAME")));
					issueRepTab.setIssueLastTime(EtsIssFilterUtils.getTrimStr(rs.getString("LASTTIME")));
					issueRepTab.setRefId(refId);

					etsRepList.add(issueRepTab);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}

		return etsRepList;

	}

	/**
	 * to dynamically set In Cluase values
	 */
	private int setInClauseValues(PreparedStatement stmt, int startIndex, ArrayList list) throws SQLException, Exception {

		int index = startIndex;

		for (int i = 0; i < list.size(); i++) {

			stmt.setString(index++, (String) list.get(i));

		}

		return index;

	}

	public String getUpdatedStateActionForCR(String infoSrcFlag, String pmoId, String stateAction, String probState, String txnStatusFlag) {

		String localProbState = probState;

		///new fix st
		//						only for PCR from ETS, not from PMO 
		if (!infoSrcFlag.equals("P")) { //for ISSUE from ETS

			//get updated state action if txn flag=N/T
			localProbState = getUpdatedStateActionForCR(pmoId, stateAction, probState, txnStatusFlag);

		} else { //for issues from PMO

			if (AmtCommonUtils.isResourceDefined(pmoId) && !AmtCommonUtils.isResourceDefined(txnStatusFlag)) {

			} else if (AmtCommonUtils.isResourceDefined(pmoId) && AmtCommonUtils.isResourceDefined(txnStatusFlag)) {

				//	get updated state action if txn flag=N/T
				localProbState = getUpdatedStateActionForCR(pmoId, stateAction, probState, txnStatusFlag);

			}
		}

		///new fix end

		return localProbState;

	}

	/**
				 * 
				 * @param stateAction
				 * @param txnFlag
				 * @return
				 */

	public String getUpdatedStateActionForCR(String cqTrkId, String stateAction, String problemState, String txnFlag) {

		HashMap pcrPropMap = issobjkey.getPcrPropMap();

		EtsIssActionGuiUtils issActGuiUtils = new EtsIssActionGuiUtils();

		return issActGuiUtils.getUpdatedStateActionForCR(pcrPropMap, cqTrkId, stateAction, problemState, txnFlag);

	}

} //end of class
