package oem.edge.ets.fe.ismgt.model;

import java.util.ArrayList;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
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
 * This class takes the selected values of the user, of various filter conditions
 * in issue fileters and applies any rules on them,amd make query strings ready for
 * use
 */
public class EtsIssFilterQryPrepBean implements EtsIssFilterConstants,EtsIssueConstants {

	public static final String VERSION = "1.33.1.21";

	private String problemTypeQryStr;
	private String issueTypeQryStr;
	private String severityTypeQryStr;
	private String statusTypeQryStr;
	private String issueSubmitterTypeQryStr;
	private String issueCurOwnerQryStr;
	private String issueProjIdStr;
	private String issueDateAllStr;
	private String issueStartDateStr;
	private String issueEndDateStr;

	private String finalQryStr;
	private String finalQryStrWithPstmt;

	private EtsIssFilterDetailsBean etsDet;
	private EtsIssFilterObjectKey issobjkey;

	//////NEW LISTS PREV///
	private ArrayList prevIssueTypeQryList;
	private ArrayList prevSeverityTypeQryList;
	private ArrayList prevStatusTypeQryList;
	private ArrayList prevIssueSubmitterQryList;
	private ArrayList prevCurOwnerQryList;

	private String issOpn;

	//issue source
	private String issueAccessQryStr;

	//current owner all
	boolean isCurOwnerAll = false;

	/////
	private ArrayList prevProbTypeQryList;

	///
	private String finalQryStrForCr;
	private String finalQryStrWithPstmtForCr;

	//
	private String sortColumn;
	private String sortOrder;
	
	//
	private String searchByNum;

	/**
	 * Constructor for EtsIssFilterQryPrepBean.
	 */
	public EtsIssFilterQryPrepBean(EtsIssFilterDetailsBean etsDet, EtsIssFilterObjectKey issobjkey) {
		super();
		this.etsDet = etsDet;
		this.issobjkey = issobjkey;
		this.issOpn = issobjkey.getOpn();
		this.sortColumn = etsDet.getSortColumn();
		this.sortOrder = etsDet.getSortOrder();
		setIssFilterQryStr();
		setIssFilterQryWithPstmtStr();

	}

	/**
	 * Returns the issueSubmitterTypeQryStr.
	 * @return String
	 */
	public String getIssueSubmitterTypeQryStr() {
		return issueSubmitterTypeQryStr;
	}

	/**
	 * Returns the issueTypeQryStr.
	 * @return String
	 */
	public String getIssueTypeQryStr() {
		return issueTypeQryStr;
	}

	/**
	 * Returns the severityTypeQryStr.
	 * @return String
	 */
	public String getSeverityTypeQryStr() {
		return severityTypeQryStr;
	}

	/**
	 * Returns the statusTypeQryStr.
	 * @return String
	 */
	public String getStatusTypeQryStr() {
		return statusTypeQryStr;
	}

	/**
	 * Sets the issueSubmitterTypeQryStr.
	 * @param issueSubmitterTypeQryStr The issueSubmitterTypeQryStr to set
	 */
	public void setIssueSubmitterTypeQryStr(String issueSubmitterTypeQryStr) {
		this.issueSubmitterTypeQryStr = issueSubmitterTypeQryStr;
	}

	/**
	 * Sets the issueTypeQryStr.
	 * @param issueTypeQryStr The issueTypeQryStr to set
	 */
	public void setIssueTypeQryStr(String issueTypeQryStr) {
		this.issueTypeQryStr = issueTypeQryStr;
	}

	/**
	 * Sets the severityTypeQryStr.
	 * @param severityTypeQryStr The severityTypeQryStr to set
	 */
	public void setSeverityTypeQryStr(String severityTypeQryStr) {
		this.severityTypeQryStr = severityTypeQryStr;
	}

	/**
	 * Sets the statusTypeQryStr.
	 * @param statusTypeQryStr The statusTypeQryStr to set
	 */
	public void setStatusTypeQryStr(String statusTypeQryStr) {
		this.statusTypeQryStr = statusTypeQryStr;
	}

	/**
	 * 
	 * This method will apply any rules applicable while
	 * making sql strings, in filter conditions
	 */

	private void setIssFilterQryStr() {

		ArrayList prevIssueTypeList = etsDet.getPrevIssueTypeList();

		ArrayList prevSeverityTypeList = etsDet.getPrevSeverityTypeList();

		ArrayList prevStatusTypeList = etsDet.getPrevStatusTypeList();

		ArrayList prevIssueSubmitterList = etsDet.getPrevIssueSubmitterList();

		ArrayList prevCurOwnerList = etsDet.getPrevIssueOwnerList();

		issueDateAllStr = etsDet.getPrevIssueDateAll();

		issueStartDateStr = etsDet.getPrevIssueStartDate();

		issueEndDateStr = etsDet.getPrevIssueEndDate();

		//issueStart

		//previous issues//

		if (prevIssueTypeList.contains("All")) {

			setIssueTypeQryStr("All");

		} else {

			setIssueTypeQryStr(AmtCommonUtils.getQryStr(prevIssueTypeList));

		}

		//previous severity//

		if (prevSeverityTypeList.contains("All")) {

			setSeverityTypeQryStr("All");

		} else {

			setSeverityTypeQryStr(AmtCommonUtils.getQryStr(prevSeverityTypeList));

		}

		//previous status//

		if (prevStatusTypeList.contains("All")) {

			setStatusTypeQryStr("All");

		} else {

			setStatusTypeQryStr(AmtCommonUtils.getQryStr(prevStatusTypeList));

		}

		//previous submiiters//

		if (prevIssueSubmitterList.contains("All")) {

			setIssueSubmitterTypeQryStr("All");

		} else {

			setIssueSubmitterTypeQryStr(AmtCommonUtils.getQryStr(prevIssueSubmitterList));

		}

		///previous current owners//
		if (prevCurOwnerList.contains("All")) {

			setIssueCurOwnerQryStr("All");

		} else {

			setIssueCurOwnerQryStr(AmtCommonUtils.getQryStr(prevCurOwnerList));

		}

		setIssueProjIdStr(etsDet.getIssueProjectId());

		//setProblemTypeQryStr(etsDet.getProblemType());

		setProblemTypeQryStr(AmtCommonUtils.getQryStr(getStdIssueProbTypeList(etsDet.getProblemType())));

		setIssueAccessQryStr(etsDet.getIssueAccess());

		if (AmtCommonUtils.getTrimStr(issueCurOwnerQryStr).equals("All")) {

			isCurOwnerAll = true;

		}

		setFinalQryStr();

		//for cr also
		setFinalQryStrForCr();

	}

	/**
	 * Returns the issueProjIdStr.
	 * @return String
	 */
	public String getIssueProjIdStr() {
		return issueProjIdStr;
	}

	/**
	 * Sets the issueProjIdStr.
	 * @param issueProjIdStr The issueProjIdStr to set
	 */
	public void setIssueProjIdStr(String issueProjIdStr) {
		this.issueProjIdStr = issueProjIdStr;
	}

	/**
	 * Returns the issueDateAll.
	 * @return String
	 */
	public String getIssueDateAllStr() {
		return issueDateAllStr;
	}

	/**
	 * Returns the issueEndDate.
	 * @return String
	 */
	public String getIssueEndDateStr() {
		return issueEndDateStr;
	}

	/**
	 * Returns the issueStartDate.
	 * @return String
	 */
	public String getIssueStartDateStr() {
		return issueStartDateStr;
	}

	/**
	 * Sets the issueDateAll.
	 * @param issueDateAll The issueDateAll to set
	 */
	public void setIssueDateAllStr(String issueDateAllStr) {
		this.issueDateAllStr = issueDateAllStr;
	}

	/**
	 * Sets the issueEndDate.
	 * @param issueEndDate The issueEndDate to set
	 */
	public void setIssueEndDateStr(String issueEndDateStr) {
		this.issueEndDateStr = issueEndDateStr;
	}

	/**
	 * Sets the issueStartDate.
	 * @param issueStartDate The issueStartDate to set
	 */
	public void setIssueStartDateStr(String issueStartDateStr) {
		this.issueStartDateStr = issueStartDateStr;
	}

	/**
	 * Returns the issueCurOwnerQryStr.
	 * @return String
	 */
	public String getIssueCurOwnerQryStr() {
		return issueCurOwnerQryStr;
	}

	/**
	 * Sets the issueCurOwnerQryStr.
	 * @param issueCurOwnerQryStr The issueCurOwnerQryStr to set
	 */
	public void setIssueCurOwnerQryStr(String issueCurOwnerQryStr) {
		this.issueCurOwnerQryStr = issueCurOwnerQryStr;
	}

	/**
	 * Returns the finalQryStr.
	 * @return String
	 */
	private void setFinalQryStr() {

		StringBuffer sb = new StringBuffer();

		Global.println("issue type qry str=" + issueTypeQryStr);
		Global.println("severity type qry str=" + severityTypeQryStr);
		Global.println("status type qry str=" + statusTypeQryStr);
		Global.println("problem creator qry str=" + issueSubmitterTypeQryStr);
		Global.println("current owner str=" + issueCurOwnerQryStr);
		Global.println("issue access in qry str=" + issueAccessQryStr);

		////new -------for brand new issues, for which cq_trk_id has not been assigned-------///
		if (!issOpn.substring(0, 1).equals("4") && isCurOwnerAll) { //only if not issues  assigned to me

			sb.append("select ");
			sb.append(" 'NEWISSUE' as issuezone, ");
			sb.append(" u.edge_problem_id as edgeproblemid, ");
			sb.append(" u.cq_trk_id as cqtrkid, ");
			sb.append(" u.problem_state as problemstate, ");
			sb.append(" u.problem_creator as problemcreator, ");
			sb.append(" u.title as title, ");
			sb.append(" u.problem_class as problemclass, ");
			sb.append(" u.severity as severity, ");
			sb.append(" u.PROBLEM_TYPE as problemtype, ");
			sb.append(" 'NOOWNER' as currentowner, ");
			sb.append(" 'NOOWNERNAME' as ownername,");
			sb.append(" 'NOOWNEREMAIL' as owneremail,");
			sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=u.problem_creator) as submittername,");
			sb.append(" u.last_timestamp as lasttime,  ");
			sb.append(" 'USERLASTACTION' as userlastaction, ");
			sb.append(" u.issue_source as issuesource  ");
			sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u ");
			sb.append(" where");
			sb.append(" u.application_id='" + ETSAPPLNID + "' ");
			sb.append(" and u.problem_class IN (" + problemTypeQryStr + ") ");
			//sb.append(" and u.cust_project =(select project_name from ets.ets_projects where project_id='" + issueProjIdStr + "' ) ");
			sb.append(" and u.ets_project_id ='" + issueProjIdStr + "'");//v2sagar			
			sb.append(" and u.edge_problem_id NOT IN  (select distinct EDGE_PROBLEM_ID from "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 where APPLICATION_ID = '" + ETSAPPLNID + "' ) ");

			if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {

				sb.append(" and u.problem_type IN (" + issueTypeQryStr + ") ");

			}

			if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

				sb.append(" and u.severity IN (" + severityTypeQryStr + ") ");

			}

			if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

				sb.append(" and u.problem_state IN (" + statusTypeQryStr + ") ");

			}

			if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

				sb.append(" and u.problem_creator IN (" + issueSubmitterTypeQryStr + ") ");

			}

			if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

				sb.append(" and date(u.creation_date) >= '" + issueStartDateStr + "' AND  date(u.creation_date) <= '" + issueEndDateStr + "' ");

			}

			if (!AmtCommonUtils.getTrimStr(issueAccessQryStr).equals("IBM")) {

				sb.append(" and u.issue_access='ALL' ");

			}

			sb.append(" group by u.severity,u.problem_type,u.problem_state,u.problem_creator,u.edge_problem_id,u.cq_trk_id,u.title,u.problem_class,u.last_timestamp,u.issue_source ");

			sb.append(" union");

		} //only if state is not issues assigned to me

		////-------for existing issues, for which cq_trk_id has been assigned and in work flow, and waiting for user resp("+ISMGTSCHEMA+".seq_no > usrseq_no-------////

		sb.append(" select ");
		sb.append(" 'CQISSUE' as issuezone, ");
		sb.append(" c.edge_problem_id as edgeproblemid, ");
		sb.append(" c.cq_trk_id as cqtrkid, ");
		sb.append(" c.problem_state as problemstate, ");
		sb.append(" u.problem_creator as problemcreator, ");
		sb.append(" c.title as title, ");
		sb.append(" c.problem_class as problemclass, ");
		sb.append(" c.severity as severity, ");
		sb.append(" c.problem_type as problemtype, ");
		sb.append(" o.owner_id as currentowner, ");
		sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=o.owner_id) as ownername,");
		sb.append(" o.owner_email as owneremail,");
		sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=u.problem_creator) as submittername, ");
		sb.append(" c.last_timestamp as lasttime, ");
		sb.append(" 'USERLASTACTION' as userlastaction, ");
		sb.append(" c.issue_source as issuesource  ");
		sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u, "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 c, "+ISMGTSCHEMA+".ETS_OWNER_CQ o ");
		sb.append(" where ");
		sb.append(" c.application_id='" + ETSAPPLNID + "' ");
		sb.append(" and c.problem_class IN (" + problemTypeQryStr + ") ");
		//sb.append(" and u.cust_project =(select project_name from ets.ets_projects where project_id='" + issueProjIdStr + "' ) ");
		sb.append(" and u.ets_project_id ='" + issueProjIdStr + "'");//v2sagar
		sb.append(" and c.application_id=u.application_id ");
		sb.append(" and c.edge_problem_id=u.edge_problem_id ");
		sb.append(" and u.seq_no < c.seq_no ");
		sb.append(" and c.edge_problem_id=o.edge_problem_id ");

		if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {

			sb.append(" and c.problem_type IN (" + issueTypeQryStr + ") ");

		}

		if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

			sb.append(" and c.severity IN (" + severityTypeQryStr + ") ");

		}

		if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

			sb.append(" and c.problem_state IN (" + statusTypeQryStr + ") ");

		}

		if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

			sb.append(" and u.problem_creator IN (" + issueSubmitterTypeQryStr + ") ");

		}

		if (!AmtCommonUtils.getTrimStr(issueCurOwnerQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueCurOwnerQryStr)) {

			sb.append(" and o.owner_id IN (" + issueCurOwnerQryStr + ") ");

		}

		if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

			sb.append(" and date(u.creation_date) >= '" + issueStartDateStr + "' AND  date(u.creation_date) <= '" + issueEndDateStr + "' ");

		}

		if (!AmtCommonUtils.getTrimStr(issueAccessQryStr).equals("IBM")) {

			sb.append(" and u.issue_access='ALL' ");

		}

		sb.append(" group by c.severity,c.problem_type,c.problem_state,u.problem_creator,o.owner_id,o.owner_email,c.edge_problem_id,c.cq_trk_id,c.title,c.problem_class,c.last_timestamp,c.issue_source ");

		//	show In Process issues only when the state is 'All' or 'In Process'

		ArrayList tmpPrevStatusLst = getPrevStatusTypeQryList();
		boolean prvStList = false;

		if (tmpPrevStatusLst != null && !tmpPrevStatusLst.isEmpty()) {

			if (tmpPrevStatusLst.contains("In Process")) {

				prvStList = true;

			}

		}

		if (AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr) || prvStList) {

			sb.append(" union");

			////-------for existing issues, for which cq_trk_id has been assigned and in work flow, and waiting for cq resp(usr.seq_no > "+ISMGTSCHEMA+".seq_no-------////

			sb.append(" select ");
			sb.append(" 'USRISSUE' as issuezone, ");
			sb.append(" u.edge_problem_id as edgeproblemid, ");
			sb.append(" u.cq_trk_id as cqtrkid, ");
			//sb.append(" c.problem_state as problemstate, "); //for clarity btwn action/state
			sb.append(" 'In Process' as problemstate, "); //for clarity btwn action/state
			sb.append(" u.problem_creator as problemcreator, ");
			sb.append(" u.title as title, ");
			sb.append(" u.problem_class as problemclass, ");
			sb.append(" u.severity as severity, ");
			sb.append(" u.problem_type as problemtype, ");
			sb.append(" o.owner_id as currentowner, ");
			sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=o.owner_id) as ownername,");
			sb.append(" o.owner_email as owneremail,");
			sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=u.problem_creator) as submittername, ");
			sb.append(" u.last_timestamp as lasttime, ");
			sb.append(" 'USERLASTACTION' as userlastaction, ");
			sb.append(" u.issue_source as issuesource  ");
			sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u,  "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 c, "+ISMGTSCHEMA+".ETS_OWNER_CQ o ");
			sb.append(" where ");
			sb.append(" u.application_id='" + ETSAPPLNID + "' ");
			sb.append(" and u.problem_class IN (" + problemTypeQryStr + ") ");
			//sb.append(" and u.cust_project =(select project_name from ets.ets_projects where project_id='" + issueProjIdStr + "' ) ");
			sb.append(" and u.ets_project_id ='" + issueProjIdStr + "'");//v2sagar
			sb.append(" and u.application_id=c.application_id ");
			sb.append(" and u.edge_problem_id=c.edge_problem_id ");
			sb.append(" and c.seq_no < u.seq_no ");
			sb.append(" and c.edge_problem_id=o.edge_problem_id ");

			if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {

				sb.append(" and u.problem_type IN (" + issueTypeQryStr + ") ");

			}

			if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

				sb.append(" and u.severity IN (" + severityTypeQryStr + ") ");

			}

			//		if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {
			//
			//			sb.append(" and c.problem_state IN (" + statusTypeQryStr + ") ");
			//
			//		}

			if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

				sb.append(" and u.problem_creator IN (" + issueSubmitterTypeQryStr + ") ");

			}

			if (!AmtCommonUtils.getTrimStr(issueCurOwnerQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueCurOwnerQryStr)) {

				sb.append(" and o.owner_id IN (" + issueCurOwnerQryStr + ") ");

			}

			if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

				sb.append(" and date(u.creation_date) >= '" + issueStartDateStr + "' AND  date(u.creation_date) <= '" + issueEndDateStr + "' ");

			}

			if (!AmtCommonUtils.getTrimStr(issueAccessQryStr).equals("IBM")) {

				sb.append(" and u.issue_access='ALL' ");

			}

			sb.append(" group by u.severity,u.problem_type,c.problem_state,u.problem_creator,o.owner_id,o.owner_email,u.edge_problem_id,u.cq_trk_id,u.title,u.problem_class,u.last_timestamp,u.issue_source ");

		} //show In Process when All/In Process is there

		sb.append(" union");

		sb.append(" SELECT ");
		sb.append(" 'CRETS' as ISSUEZONE,");
		sb.append(" u.ETS_ID as EDGEPROBLEMID, ");
		sb.append(" u.PMO_ID as CQTRKID, ");
		sb.append(" u.PROBLEM_STATE as PROBLEMSTATE,");
		sb.append(" u.SUBMITTER_IR_ID as PROBLEMCREATOR,");
		sb.append(" u.TITLE as TITLE,");
		sb.append(" u.CLASS as PROBLEMCLASS,");
		sb.append(" u.SEVERITY as SEVERITY,");
		sb.append(" u.TYPE as PROBLEMTYPE, ");
		sb.append(" u.OWNER_IR_ID as CURRENTOWNER,");
		sb.append(" u.OWNER_NAME as OWNERNAME,");
		sb.append(" (select user_email from amt.users where ir_userid=u.OWNER_IR_ID) as owneremail,");
		sb.append(" u.SUBMITTER_NAME as SUBMITTERNAME, ");
		sb.append(" u.LAST_TIMESTAMP as LASTTIME, ");
		sb.append(" u.STATE_ACTION as USERLASTACTION,");
		sb.append(" u.issue_source as issuesource  ");
		sb.append(" from ");
		sb.append(" ets.pmo_issue_info u ");
		sb.append(" where");
		sb.append(" u.class ='" + ETSPMOISSUESUBTYPE + "' ");
		sb.append(" and u.pmo_project_id =(select PMO_PROJECT_ID from ets.ets_projects where project_id='" + issueProjIdStr + "' ) ");

		if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

			sb.append(" and u.severity IN (" + severityTypeQryStr + ") ");

		}

		if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

			sb.append(" and u.problem_state IN (" + statusTypeQryStr + ") ");

		}

		if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

			sb.append(" and u.submitter_ir_id IN  (" + issueSubmitterTypeQryStr + ")  ");

		}

		if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

			sb.append(" and date(u.submission_date) >= '" + issueStartDateStr + "' AND  date(u.submission_date) <= '" + issueEndDateStr + "' ");

		}

		if (!AmtCommonUtils.getTrimStr(issueAccessQryStr).equals("IBM")) {

			sb.append(" and u.issue_access='ALL' ");

		}

		sb.append(" group by u.severity,u.state_action,u.problem_state,u.submitter_ir_id,u.submitter_name,u.owner_ir_id,u.owner_name,u.ets_id,u.pmo_id,u.title,u.class,u.type,u.last_timestamp,u.issue_source ");

		sb.append(" order by ");

		//	sort column

		if (AmtCommonUtils.isResourceDefined(sortColumn)) {

			sb.append(" " + sortColumn + " ");

		}

		//sort order

		if (AmtCommonUtils.isResourceDefined(sortOrder)) {

			sb.append(" " + sortOrder + " ");

		}

		//		CSR UR
		//sb.append(" FOR READ ONLY");
		sb.append(" with ur");

		////end/////

		this.finalQryStr = sb.toString();

	}

	/**
	 * Returns the problemTypeQryStr.
	 * @return String
	 */
	public String getProblemTypeQryStr() {
		return problemTypeQryStr;
	}

	/**
	 * Sets the problemTypeQryStr.
	 * @param problemTypeQryStr The problemTypeQryStr to set
	 */
	public void setProblemTypeQryStr(String problemTypeQryStr) {
		this.problemTypeQryStr = problemTypeQryStr;
	}

	/**
	 * Returns the finalQryStr.
	 * @return String
	 */
	public String getFinalQryStr() {
		return finalQryStr;
	}

	/**
	 * Returns the finalQryStr.
	 * @return String
	 */
	public String getFinalQryStrWithPstmt() {
		return finalQryStrWithPstmt;
	}

	/**
	 * 
	 * This method will apply any rules applicable while
	 * making sql strings, in filter conditions
	 */

	private void setIssFilterQryWithPstmtStr() {

		ArrayList prevIssueTypeList = etsDet.getPrevIssueTypeList();

		ArrayList prevSeverityTypeList = etsDet.getPrevSeverityTypeList();

		ArrayList prevStatusTypeList = etsDet.getPrevStatusTypeList();

		ArrayList prevIssueSubmitterList = etsDet.getPrevIssueSubmitterList();

		ArrayList prevCurOwnerList = etsDet.getPrevIssueOwnerList();

		issueDateAllStr = etsDet.getPrevIssueDateAll();

		issueStartDateStr = etsDet.getPrevIssueStartDate();

		issueEndDateStr = etsDet.getPrevIssueEndDate();
		
		searchByNum=etsDet.getSearchByNum();

		//issueStart

		//previous issues//

		if (prevIssueTypeList.contains("All")) {

			setIssueTypeQryStr("All");

		} else {

			setPrevIssueTypeQryList(prevIssueTypeList);
			setIssueTypeQryStr(AmtCommonUtils.getQryStr(prevIssueTypeList));

		}

		//previous severity//

		if (prevSeverityTypeList.contains("All")) {

			setSeverityTypeQryStr("All");

		} else {

			setPrevSeverityTypeQryList(prevSeverityTypeList);
			setSeverityTypeQryStr(AmtCommonUtils.getQryStr(prevSeverityTypeList));

		}

		//previous status//

		if (prevStatusTypeList.contains("All")) {

			setStatusTypeQryStr("All");

		} else {

			setPrevStatusTypeQryList(prevStatusTypeList);
			setStatusTypeQryStr(AmtCommonUtils.getQryStr(prevStatusTypeList));

		}

		//previous submiiters//

		if (prevIssueSubmitterList.contains("All")) {

			setIssueSubmitterTypeQryStr("All");

		} else {

			setPrevIssueSubmitterQryList(prevIssueSubmitterList);
			setIssueSubmitterTypeQryStr(AmtCommonUtils.getQryStr(prevIssueSubmitterList));

		}

		///previous current owners//
		if (prevCurOwnerList.contains("All")) {

			setIssueCurOwnerQryStr("All");

		} else {

			setPrevCurOwnerQryList(prevCurOwnerList);
			setIssueCurOwnerQryStr(AmtCommonUtils.getQryStr(prevCurOwnerList));

		}

		setIssueProjIdStr(etsDet.getIssueProjectId());

		//setProblemTypeQryStr(etsDet.getProblemType());

		setProblemTypeQryStr(AmtCommonUtils.getQryStr(getStdIssueProbTypeList(etsDet.getProblemType())));

		setPrevProbTypeQryList(getStdIssueProbTypeList(etsDet.getProblemType()));

		Global.println("issue source str in qry prpe==" + etsDet.getIssueAccess());

		setIssueAccessQryStr(etsDet.getIssueAccess());

		if (AmtCommonUtils.getTrimStr(issueCurOwnerQryStr).equals("All")) {

			isCurOwnerAll = true;

		}

		setFinalQryStrWithPstmt();

		//for CR

		setFinalQryStrWithPstmtForCr();

	}

	/**
	 * Returns the prevCurOwnerQryList.
	 * @return ArrayList
	 */
	public ArrayList getPrevCurOwnerQryList() {
		return prevCurOwnerQryList;
	}

	/**
	 * Returns the prevIssueSubmitterQryList.
	 * @return ArrayList
	 */
	public ArrayList getPrevIssueSubmitterQryList() {
		return prevIssueSubmitterQryList;
	}

	/**
	 * Returns the prevIssueTypeQryList.
	 * @return ArrayList
	 */
	public ArrayList getPrevIssueTypeQryList() {
		return prevIssueTypeQryList;
	}

	/**
	 * Returns the prevSeverityTypeQryList.
	 * @return ArrayList
	 */
	public ArrayList getPrevSeverityTypeQryList() {
		return prevSeverityTypeQryList;
	}

	/**
	 * Returns the prevStatusTypeQryList.
	 * @return ArrayList
	 */
	public ArrayList getPrevStatusTypeQryList() {
		return prevStatusTypeQryList;
	}

	/**
	 * Sets the prevCurOwnerQryList.
	 * @param prevCurOwnerQryList The prevCurOwnerQryList to set
	 */
	public void setPrevCurOwnerQryList(ArrayList prevCurOwnerQryList) {
		this.prevCurOwnerQryList = prevCurOwnerQryList;
	}

	/**
	 * Sets the prevIssueSubmitterQryList.
	 * @param prevIssueSubmitterQryList The prevIssueSubmitterQryList to set
	 */
	public void setPrevIssueSubmitterQryList(ArrayList prevIssueSubmitterQryList) {
		this.prevIssueSubmitterQryList = prevIssueSubmitterQryList;
	}

	/**
	 * Sets the prevIssueTypeQryList.
	 * @param prevIssueTypeQryList The prevIssueTypeQryList to set
	 */
	public void setPrevIssueTypeQryList(ArrayList prevIssueTypeQryList) {
		this.prevIssueTypeQryList = prevIssueTypeQryList;
	}

	/**
	 * Sets the prevSeverityTypeQryList.
	 * @param prevSeverityTypeQryList The prevSeverityTypeQryList to set
	 */
	public void setPrevSeverityTypeQryList(ArrayList prevSeverityTypeQryList) {
		this.prevSeverityTypeQryList = prevSeverityTypeQryList;
	}

	/**
	 * Sets the prevStatusTypeQryList.
	 * @param prevStatusTypeQryList The prevStatusTypeQryList to set
	 */
	public void setPrevStatusTypeQryList(ArrayList prevStatusTypeQryList) {
		this.prevStatusTypeQryList = prevStatusTypeQryList;
	}

	/**
	 * Returns the finalQryStr.
	 * @return String
	 */
	private void setFinalQryStrWithPstmt() {

		StringBuffer sb = new StringBuffer();

		Global.println("issue type qry str=" + issueTypeQryStr);
		Global.println("severity type qry str=" + severityTypeQryStr);
		Global.println("status type qry str=" + statusTypeQryStr);
		Global.println("problem creator qry str=" + issueSubmitterTypeQryStr);
		Global.println("current owner str=" + issueCurOwnerQryStr);
		Global.println("issue access in qry str=" + issueAccessQryStr);
		Global.println("SORT COLUMN===" + sortColumn);
		Global.println("SORT ORDER===" + sortOrder);
		Global.println("SEARCH BY NUM===" + searchByNum);

		String dynInClsProbTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevProbTypeQryList());
		String dynInClsIssTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevIssueTypeQryList());
		String dynInClsSevTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevSeverityTypeQryList());
		String dynInClsStatTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevStatusTypeQryList());
		String dynInClsIssSubTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevIssueSubmitterQryList());
		String dynInClsIssCurOwnStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevCurOwnerQryList());

		////new -------for brand new issues, for which cq_trk_id has not been assigned-------///
		if (!issOpn.substring(0, 1).equals("4") && isCurOwnerAll) { //only if not issues  assigned to me

			sb.append("select ");
			sb.append(" 'NEWISSUE' as issuezone, ");
			sb.append(" u.edge_problem_id as edgeproblemid, ");
			sb.append(" u.cq_trk_id as cqtrkid, ");
			sb.append(" u.problem_state as problemstate, ");
			sb.append(" u.problem_creator as problemcreator, ");
			sb.append(" u.title as title, ");
			sb.append(" u.problem_class as problemclass, ");
			sb.append(" u.severity as severity, ");
			sb.append(" (select x.issuetype from ets.ets_dropdown_data x where x.data_id=u.issue_type_id) as problemtype,");
			//sb.append(" u.PROBLEM_TYPE as problemtype, ");
			sb.append(" 'NOOWNER' as currentowner, ");
			sb.append(" 'NOOWNERNAME' as ownername,");
			sb.append(" 'NOOWNEREMAIL' as owneremail,");
			sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=u.problem_creator) as submittername,");
			sb.append(" u.last_timestamp as lasttime,  ");
			sb.append(" 'USERLASTACTION' as userlastaction, ");
			sb.append(" u.issue_source as issuesource,  ");
			sb.append(" u.cq_trk_id as refid ");
			sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u ");
			sb.append(" where");
			sb.append(" u.application_id='" + ETSAPPLNID + "' ");
			sb.append(" and u.problem_class IN " + dynInClsProbTypeStr + " ");
			//sb.append(" and u.cust_project =(select project_name from ets.ets_projects where project_id= ? ) ");
			sb.append(" and u.ets_project_id =? ");//v2sagar
			sb.append(" and u.edge_problem_id NOT IN  (select distinct EDGE_PROBLEM_ID from "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 where APPLICATION_ID = '" + ETSAPPLNID + "' ) ");

			if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {

				//sb.append(" and u.problem_type IN " + dynInClsIssTypeStr + " ");
				sb.append(" and u.issue_type_id IN ");
				sb.append("    (select data_id from ets.ets_dropdown_data");
				sb.append("           where");
				sb.append("           project_id = ? ");
				sb.append("          and  issue_class IN " + dynInClsProbTypeStr + " ");
				sb.append("          and  issuetype IN " + dynInClsIssTypeStr + " ");
				sb.append("    ) ");
				
			}

			if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

				sb.append(" and u.severity IN " + dynInClsSevTypeStr + " ");

			}

			if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

				sb.append(" and u.problem_state IN " + dynInClsStatTypeStr + " ");

			}

			if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

				sb.append(" and u.problem_creator IN " + dynInClsIssSubTypeStr + " ");

			}

			if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

				sb.append(" and u.creation_date >= ? AND  u.creation_date <= ? ");

			}

			if (!AmtCommonUtils.getTrimStr(issueAccessQryStr).equals("IBM")) {

				sb.append(" and u.issue_access='ALL' ");

			}
			
			if(AmtCommonUtils.isResourceDefined(searchByNum)) {
			
				sb.append(" and u.cq_trk_id like '%" + searchByNum + "%' ");
				
			}


			sb.append(" group by u.severity,u.problem_state,u.problem_creator,u.edge_problem_id,u.cq_trk_id,u.title,u.problem_class,u.issue_type_id,u.last_timestamp,u.issue_source ");

			sb.append(" union");

		} //only if state is not issues assigned to me

		////-------for existing issues, for which cq_trk_id has been assigned and in work flow, and waiting for user resp("+ISMGTSCHEMA+".seq_no > usrseq_no-------////

		sb.append(" select ");
		sb.append(" 'CQISSUE' as issuezone, ");
		sb.append(" c.edge_problem_id as edgeproblemid, ");
		sb.append(" c.cq_trk_id as cqtrkid, ");
		sb.append(" c.problem_state as problemstate, ");
		sb.append(" u.problem_creator as problemcreator, ");
		sb.append(" c.title as title, ");
		sb.append(" c.problem_class as problemclass, ");
		sb.append(" c.severity as severity, ");
		sb.append(" (select x.issuetype from ets.ets_dropdown_data x where x.data_id=u.issue_type_id) as problemtype,");
		//sb.append(" c.problem_type as problemtype, ");
		sb.append(" o.owner_id as currentowner, ");
		sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=o.owner_id) as ownername,");
		sb.append(" o.owner_email as owneremail,");
		sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=u.problem_creator) as submittername, ");
		sb.append(" c.last_timestamp as lasttime, ");
		sb.append(" 'USERLASTACTION' as userlastaction, ");
		sb.append(" c.issue_source as issuesource, ");
		sb.append(" c.cq_trk_id as refid ");
		sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u, "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 c, "+ISMGTSCHEMA+".ETS_OWNER_CQ o ");
		sb.append(" where ");
		sb.append(" c.application_id='" + ETSAPPLNID + "' ");
		sb.append(" and c.problem_class IN " + dynInClsProbTypeStr + "  ");
		//sb.append(" and u.cust_project =(select project_name from ets.ets_projects where project_id= ? ) ");
		sb.append(" and u.ets_project_id =? ");//v2sagar
		sb.append(" and c.application_id=u.application_id ");
		sb.append(" and c.edge_problem_id=u.edge_problem_id ");
		sb.append(" and u.seq_no < c.seq_no ");
		sb.append(" and c.edge_problem_id=o.edge_problem_id ");

		if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {

			//sb.append(" and c.problem_type IN " + dynInClsIssTypeStr + " ");
			sb.append(" and u.issue_type_id IN ");
			sb.append("    (select data_id from ets.ets_dropdown_data");
			sb.append("           where");
			sb.append("           project_id = ? ");
			sb.append("          and  issue_class IN " + dynInClsProbTypeStr + " ");
			sb.append("          and  issuetype IN " + dynInClsIssTypeStr + " ");
			sb.append("    ) ");

		}

		if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

			sb.append(" and c.severity IN " + dynInClsSevTypeStr + " ");

		}

		if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

			sb.append(" and c.problem_state IN " + dynInClsStatTypeStr + " ");

		}

		if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

			sb.append(" and u.problem_creator IN " + dynInClsIssSubTypeStr + " ");

		}

		if (!AmtCommonUtils.getTrimStr(issueCurOwnerQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueCurOwnerQryStr)) {

			sb.append(" and o.owner_id IN " + dynInClsIssCurOwnStr + " ");

		}

		if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

			sb.append(" and u.creation_date >= ? AND  u.creation_date <= ? ");

		}

		if (!AmtCommonUtils.getTrimStr(issueAccessQryStr).equals("IBM")) {

			sb.append(" and u.issue_access='ALL' ");

		}
		
		if(AmtCommonUtils.isResourceDefined(searchByNum)) {
			
			sb.append(" and c.cq_trk_id like '%" + searchByNum + "%' ");
		}

		sb.append(" group by c.severity,c.problem_state,u.problem_creator,o.owner_id,o.owner_email,c.edge_problem_id,c.cq_trk_id,c.title,c.problem_class,u.issue_type_id,c.last_timestamp,c.issue_source ");

		//show In Process issues only when the state is 'All' or 'In Process'

		ArrayList tmpPrevStatusLstPstmt = getPrevStatusTypeQryList();
		boolean prvStListPstmt = false;

		if (tmpPrevStatusLstPstmt != null && !tmpPrevStatusLstPstmt.isEmpty()) {

			if (tmpPrevStatusLstPstmt.contains("In Process")) {

				prvStListPstmt = true;

			}

		}

		if (AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr) || prvStListPstmt) {

			sb.append(" union");

			////-------for existing issues, for which cq_trk_id has been assigned and in work flow, and waiting for cq resp(usr.seq_no > "+ISMGTSCHEMA+".seq_no-------////

			sb.append(" select ");
			sb.append(" 'USRISSUE' as issuezone, ");
			sb.append(" u.edge_problem_id as edgeproblemid, ");
			sb.append(" u.cq_trk_id as cqtrkid, ");
			//sb.append(" c.problem_state as problemstate, "); //for clarity btwn action/state
			sb.append(" 'In Process' as problemstate, "); //for clarity btwn action/state
			sb.append(" u.problem_creator as problemcreator, ");
			sb.append(" u.title as title, ");
			sb.append(" u.problem_class as problemclass, ");
			sb.append(" u.severity as severity, ");
			sb.append(" (select x.issuetype from ets.ets_dropdown_data x where x.data_id=u.issue_type_id) as problemtype,");
			//sb.append(" u.problem_type as problemtype, ");
			sb.append(" o.owner_id as currentowner, ");
			sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=o.owner_id) as ownername,");
			sb.append(" o.owner_email as owneremail,");
			sb.append(" (select x.user_fullname from amt.users x where x.edge_userid=u.problem_creator) as submittername, ");
			sb.append(" u.last_timestamp as lasttime, ");
			sb.append(" 'USERLASTACTION' as userlastaction, ");
			sb.append(" u.ISSUE_SOURCE as issuesource, ");
			sb.append(" u.cq_trk_id as refid ");
			sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u,  "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 c, "+ISMGTSCHEMA+".ETS_OWNER_CQ o ");
			sb.append(" where ");
			sb.append(" u.application_id='" + ETSAPPLNID + "' ");
			sb.append(" and u.problem_class IN " + dynInClsProbTypeStr + " ");
			//sb.append(" and u.cust_project =(select project_name from ets.ets_projects where project_id= ? ) ");
			sb.append(" and u.ets_project_id =? ");//v2sagar
			sb.append(" and u.application_id=c.application_id ");
			sb.append(" and u.edge_problem_id=c.edge_problem_id ");
			sb.append(" and c.seq_no < u.seq_no ");
			sb.append(" and c.edge_problem_id=o.edge_problem_id ");

			if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {

				//sb.append(" and u.problem_type IN " + dynInClsIssTypeStr + " ");
				sb.append(" and u.issue_type_id IN ");
				sb.append("    (select data_id from ets.ets_dropdown_data");
				sb.append("           where");
				sb.append("           project_id = ? ");
				sb.append("          and  issue_class IN " + dynInClsProbTypeStr + " ");
				sb.append("          and  issuetype IN " + dynInClsIssTypeStr + " ");
				sb.append("    ) ");

			}

			if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

				sb.append(" and u.severity IN " + dynInClsSevTypeStr + " ");

			}

			//		if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {
			//
			//			sb.append(" and c.problem_state IN " + dynInClsStatTypeStr + " ");
			//
			//		}

			if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

				sb.append(" and u.problem_creator IN " + dynInClsIssSubTypeStr + " ");

			}

			if (!AmtCommonUtils.getTrimStr(issueCurOwnerQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueCurOwnerQryStr)) {

				sb.append(" and o.owner_id IN " + dynInClsIssCurOwnStr + " ");
			}

			if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

				sb.append(" and u.creation_date >= ? AND  u.creation_date <= ? ");

			}

			if (!AmtCommonUtils.getTrimStr(issueAccessQryStr).equals("IBM")) {

				sb.append(" and u.issue_access='ALL' ");

			}
			
			if(AmtCommonUtils.isResourceDefined(searchByNum)) {
			
				sb.append(" and u.cq_trk_id like '%" + searchByNum + "%' ");
			}

			sb.append(" group by u.severity,c.problem_state,u.problem_creator,o.owner_id,o.owner_email,u.edge_problem_id,u.cq_trk_id,u.title,u.problem_class,u.issue_type_id,u.last_timestamp,u.issue_source ");

		} //include In Process only when it is 'All' or 'In Process'

		//new PMO STR
		sb.append(" UNION");

		///for issues from PMO///
		sb.append(" SELECT ");
		sb.append(" 'CRETS' as ISSUEZONE,");
		sb.append(" u.ETS_ID as EDGEPROBLEMID, ");
		sb.append(" u.PMO_ID as CQTRKID, ");
		sb.append(" u.PROBLEM_STATE as PROBLEMSTATE,");
		sb.append(" u.SUBMITTER_IR_ID as PROBLEMCREATOR,");
		sb.append(" u.TITLE as TITLE,");
		sb.append(" u.CLASS as PROBLEMCLASS, ");
		sb.append(" u.SEVERITY as SEVERITY, ");
		sb.append(" (select x.issuetype from ets.ets_dropdown_data x where x.project_id=? and x.issue_class='Defect' and issue_source='ETSPMO') as problemtype,");
		//sb.append(" u.TYPE as PROBLEMTYPE, ");
		sb.append(" u.OWNER_IR_ID as CURRENTOWNER, ");
		sb.append(" u.OWNER_NAME as OWNERNAME, ");
		sb.append(" (select user_email from amt.users where ir_userid=u.OWNER_IR_ID) as owneremail, ");
		sb.append(" u.SUBMITTER_NAME as SUBMITTERNAME, ");
		sb.append(" u.LAST_TIMESTAMP as LASTTIME, ");
		//sb.append(" u.ISSUE_SOURCE as issuesource ");
		sb.append(" u.STATE_ACTION as USERLASTACTION, ");
		sb.append(" 'ETSPMO' as issuesource, ");
		sb.append(" CHAR(u.REF_NO) as REFID ");
		
		sb.append(" from ");
		sb.append(" ets.pmo_issue_info u");
		sb.append(" where");
		sb.append(" u.class ='" + ETSPMOISSUESUBTYPE + "' ");
		sb.append(" and u.pmo_project_id =(select PMO_PROJECT_ID from ets.ets_projects where project_id=? ) ");
		
//		if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {
//
//			//sb.append(" and u.type IN " + dynInClsIssTypeStr + " ");
//			sb.append(" and u.issue_type_id IN ");
//			sb.append("    (select data_id from ets.ets_dropdown_data");
//			sb.append("           where");
//			sb.append("           project_id = ? ");
//			sb.append("          and  issue_class IN " + dynInClsProbTypeStr + " ");
//			sb.append("          and  issuetype IN " + dynInClsIssTypeStr + " ");
//			sb.append("    ) ");
//
//		}

		if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

			sb.append(" and u.severity IN " + dynInClsSevTypeStr + " ");

		}

		if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

			sb.append(" and u.problem_state IN " + dynInClsStatTypeStr + " ");

		}

		if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

			sb.append(" and u.submitter_ir_id IN   " + dynInClsIssSubTypeStr + "  ");

		}

		if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

			sb.append(" and u.submission_date >= ? AND  u.submission_date <= ? ");

		}

		if (!AmtCommonUtils.getTrimStr(issueAccessQryStr).equals("IBM")) {

			sb.append(" and u.issue_access='ALL' ");

		}
		
		if(AmtCommonUtils.isResourceDefined(searchByNum)) {
			
			sb.append(" and char(u.ref_no) like '%" + searchByNum + "%' ");
		}

		sb.append(" group by u.severity,u.state_action,u.problem_state,u.submitter_ir_id,u.submitter_name,u.owner_ir_id,u.owner_name,u.ets_id,u.ref_no,u.pmo_id,u.title,u.class,u.issue_type_id,u.last_timestamp,u.issue_source ");

		///

		//NEW PMO END

		if (prvStListPstmt) {

			//				new PMO IN PROCESS START
			sb.append(" UNION");

			///for issues from PMO///
			sb.append(" SELECT ");
			sb.append(" 'CRETSINPROCESS' as ISSUEZONE,");
			sb.append(" u.ETS_ID as EDGEPROBLEMID, ");
			sb.append(" u.PMO_ID as CQTRKID, ");
			sb.append(" u.PROBLEM_STATE as PROBLEMSTATE,");
			sb.append(" u.SUBMITTER_IR_ID as PROBLEMCREATOR,");
			sb.append(" u.TITLE as TITLE,");
			sb.append(" u.CLASS as PROBLEMCLASS, ");
			sb.append(" u.SEVERITY as SEVERITY, ");
			sb.append(" (select x.issuetype from ets.ets_dropdown_data x where x.project_id=? and x.issue_class='Defect' and issue_source='ETSPMO') as problemtype,");
			//sb.append(" u.TYPE as PROBLEMTYPE, ");
			sb.append(" u.OWNER_IR_ID as CURRENTOWNER, ");
			sb.append(" u.OWNER_NAME as OWNERNAME, ");
			sb.append(" (select user_email from amt.users where ir_userid=u.OWNER_IR_ID) as owneremail, ");
			sb.append(" u.SUBMITTER_NAME as SUBMITTERNAME, ");
			sb.append(" u.LAST_TIMESTAMP as LASTTIME, ");
			//sb.append(" u.ISSUE_SOURCE as issuesource ");
			sb.append(" u.STATE_ACTION as USERLASTACTION, ");
			sb.append(" 'ETSPMO' as issuesource, ");
			sb.append(" CHAR(u.REF_NO) as REFID ");
			sb.append(" from ");
			sb.append(" ets.pmo_issue_info u");
			sb.append(" where");
			sb.append(" u.class ='" + ETSPMOISSUESUBTYPE + "' ");
			sb.append(" and u.pmo_project_id =(select PMO_PROJECT_ID from ets.ets_projects where project_id=? ) ");
			
//			if (!AmtCommonUtils.getTrimStr(issueTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueTypeQryStr)) {
//
//				//sb.append(" and u.type IN " + dynInClsIssTypeStr + " ");
//				sb.append(" and u.issue_type_id IN ");
//				sb.append("    (select data_id from ets.ets_dropdown_data");
//				sb.append("           where");
//				sb.append("           project_id = ? ");
//				sb.append("          and  issue_class IN " + dynInClsProbTypeStr + " ");
//				sb.append("          and  issuetype IN " + dynInClsIssTypeStr + " ");
//				sb.append("    ) ");
//
//			}

			if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

				sb.append(" and u.severity IN " + dynInClsSevTypeStr + " ");

			}

			if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

				sb.append(" and u.submitter_ir_id IN   " + dynInClsIssSubTypeStr + "  ");

			}

			if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

				sb.append(" and u.submission_date >= ? AND  u.submission_date <= ? ");

			}

			if (!AmtCommonUtils.getTrimStr(issueAccessQryStr).equals("IBM")) {

				sb.append(" and u.issue_access='ALL' ");

			}
			
			if(AmtCommonUtils.isResourceDefined(searchByNum)) {
			
				sb.append(" and char(u.ref_no) like '%" + searchByNum + "%' ");
			}

			sb.append(" group by u.severity,u.state_action,u.problem_state,u.submitter_ir_id,u.submitter_name,u.owner_ir_id,u.owner_name,u.ets_id,u.ref_no, u.pmo_id,u.title,u.class,u.issue_type_id,u.last_timestamp,u.issue_source ");

		}

		///

		//PMO IN PROCESS end

		sb.append(" ORDER BY");

		//sort column

		if (AmtCommonUtils.isResourceDefined(sortColumn)) {

			sb.append(" " + sortColumn + " ");

		}

		//sort order

		if (AmtCommonUtils.isResourceDefined(sortOrder)) {

			sb.append(" " + sortOrder + " ");

		}

		//sb.append(" for read only");
		//CSR UR
		sb.append(" with ur");

		////end/////

		this.finalQryStrWithPstmt = sb.toString();

	}

	/**
	 * Returns the issOpn.
	 * @return String
	 */
	public String getIssOpn() {
		return issOpn;
	}

	/**
	 * Returns the issueAccessQryStr.
	 * @return String
	 */
	public String getIssueAccessQryStr() {
		return issueAccessQryStr;
	}

	/**
	 * Sets the issueAccessQryStr.
	 * @param issueAccessQryStr The issueAccessQryStr to set
	 */
	public void setIssueAccessQryStr(String issueAccessQryStr) {
		this.issueAccessQryStr = issueAccessQryStr;
	}

	/**
	 * Returns the isCurOwnerAll.
	 * @return boolean
	 */
	public boolean isCurOwnerAll() {
		return isCurOwnerAll;
	}

	/**
	 * Sets the isCurOwnerAll.
	 * @param isCurOwnerAll The isCurOwnerAll to set
	 */
	public void setIsCurOwnerAll(boolean isCurOwnerAll) {
		this.isCurOwnerAll = isCurOwnerAll;
	}

	/**
	 * get standard prob types for issue_type=Defect/change
	 */

	private ArrayList getStdIssueProbTypeList(String issueSubType) {

		ArrayList stdProbTypeList = new ArrayList();

		if (issueSubType.equals(ETSISSUESUBTYPE)) {

			stdProbTypeList.add("Defect");
			stdProbTypeList.add("Question");

		} else if (issueSubType.equals(ETSCHANGESUBTYPE)) {

			stdProbTypeList.add("Change");

		} else {

			stdProbTypeList.add("Change");

		}

		return stdProbTypeList;

	}

	/**
	 * Returns the prevProbTypeQryList.
	 * @return ArrayList
	 */
	public ArrayList getPrevProbTypeQryList() {
		return prevProbTypeQryList;
	}

	/**
	 * Sets the prevProbTypeQryList.
	 * @param prevProbTypeQryList The prevProbTypeQryList to set
	 */
	public void setPrevProbTypeQryList(ArrayList prevProbTypeQryList) {
		this.prevProbTypeQryList = prevProbTypeQryList;
	}

	/**
		 * Returns the finalQryStr.
		 * @return String
		 */
	private void setFinalQryStrForCr() {

		StringBuffer sb = new StringBuffer();

		Global.println("issue type qry str=" + issueTypeQryStr);
		Global.println("severity type qry str=" + severityTypeQryStr);
		Global.println("status type qry str=" + statusTypeQryStr);
		Global.println("problem creator qry str=" + issueSubmitterTypeQryStr);
		Global.println("current owner str=" + issueCurOwnerQryStr);
		Global.println("issue access in qry str=" + issueAccessQryStr);

		sb.append(" SELECT ");
		sb.append(" 'CRETS' as ISSUEZONE,");
		sb.append(" u.ETS_ID as EDGEPROBLEMID, ");
		sb.append(" u.PMO_ID as CQTRKID, ");
		sb.append(" u.INFO_SRC_FLAG as PROBLEMTYPE, ");
		sb.append(" u.STATE_ACTION as USERLASTACTION,");
		sb.append(" u.PROBLEM_STATE as PROBLEMSTATE,");
		sb.append(" u.SUBMITTER_IR_ID as PROBLEMCREATOR,");
		sb.append(" u.SUBMITTER_NAME as SUBMITTERNAME, ");
		sb.append(" u.CLASS as PROBLEMCLASS,");
		sb.append(" u.TITLE as TITLE,");
		sb.append(" u.SEVERITY as SEVERITY,");
		sb.append(" u.OWNER_IR_ID as CURRENTOWNER,");
		sb.append(" u.OWNER_NAME as OWNERNAME,");
		sb.append(" u.LAST_TIMESTAMP as LASTTIME");
		sb.append(" from ");
		sb.append(" ets.pmo_issue_info u ");
		sb.append(" where");
		sb.append(" u.class ='" + ETSPMOCHANGESUBTYPE + "' ");
		sb.append(" and u.pmo_project_id =(select PMO_PROJECT_ID from ets.ets_projects where project_id='" + issueProjIdStr + "' ) ");

		if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

			sb.append(" and u.severity IN (" + severityTypeQryStr + ") ");

		}

		if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

			sb.append(" and u.problem_state IN (" + statusTypeQryStr + ") ");

		}

		if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

			sb.append(" and u.submitter_ir_id IN  (" + issueSubmitterTypeQryStr + ")  ");

		}

		if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

			sb.append(" and date(u.submission_date) >= '" + issueStartDateStr + "' AND  date(u.submission_date) <= '" + issueEndDateStr + "' ");

		}

		sb.append(" group by u.severity,u.problem_state,u.state_action,u.submitter_ir_id,u.submitter_name,u.owner_ir_id,u.owner_name,u.ets_id,u.pmo_id,u.title,u.class,u.info_src_flag,u.last_timestamp ");

		sb.append(" ORDER BY");

		//sort column

		if (AmtCommonUtils.isResourceDefined(sortColumn)) {

			sb.append(" " + sortColumn + " ");

		}

		//sort order

		if (AmtCommonUtils.isResourceDefined(sortOrder)) {

			sb.append(" " + sortOrder + " ");

		}

		sb.append(" with ur");

		////end/////

		this.finalQryStrForCr = sb.toString();

	}

	/**
		 * Returns the finalQryStr.
		 * @return String
		 */
	private void setFinalQryStrWithPstmtForCr() {

		StringBuffer sb = new StringBuffer();

		Global.println("issue type qry str=" + issueTypeQryStr);
		Global.println("severity type qry str=" + severityTypeQryStr);
		Global.println("status type qry str=" + statusTypeQryStr);
		Global.println("problem creator qry str=" + issueSubmitterTypeQryStr);
		Global.println("current owner str=" + issueCurOwnerQryStr);
		Global.println("issue access in qry str=" + issueAccessQryStr);

		String dynInClsProbTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevProbTypeQryList());
		String dynInClsIssTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevIssueTypeQryList());
		String dynInClsSevTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevSeverityTypeQryList());
		String dynInClsStatTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevStatusTypeQryList());
		String dynInClsIssSubTypeStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevIssueSubmitterQryList());
		String dynInClsIssCurOwnStr = AmtCommonUtils.getPrepStmtINCluaseStr(getPrevCurOwnerQryList());

		sb.append(" SELECT ");
		sb.append(" 'CRETS' as ISSUEZONE,");
		sb.append(" u.ETS_ID as EDGEPROBLEMID, ");
		sb.append(" u.PMO_ID as CQTRKID, ");
		sb.append(" u.INFO_SRC_FLAG as PROBLEMTYPE, ");
		sb.append(" u.STATE_ACTION as USERLASTACTION,");
		sb.append(" u.PROBLEM_STATE as PROBLEMSTATE,");
		sb.append(" u.SUBMITTER_IR_ID as PROBLEMCREATOR,");
		sb.append(" u.SUBMITTER_NAME as SUBMITTERNAME, ");
		sb.append(" u.CLASS as PROBLEMCLASS,");
		sb.append(" u.TITLE as TITLE,");
		sb.append(" u.SEVERITY as SEVERITY,");
		sb.append(" u.OWNER_IR_ID as CURRENTOWNER,");
		sb.append(" u.OWNER_NAME as OWNERNAME,");
		sb.append(" u.LAST_TIMESTAMP as LASTTIME,");
		sb.append(" CHAR(u.REF_NO) as REFID ");
		sb.append(" from ");
		sb.append(" ets.pmo_issue_info u ");
		sb.append(" where");
		sb.append(" u.class ='" + ETSPMOCHANGESUBTYPE + "' ");
		sb.append(" and u.pmo_project_id =(select PMO_PROJECT_ID from ets.ets_projects where project_id=? ) ");

		if (!AmtCommonUtils.getTrimStr(severityTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(severityTypeQryStr)) {

			sb.append(" and u.severity IN " + dynInClsSevTypeStr + " ");

		}

		if (!AmtCommonUtils.getTrimStr(statusTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(statusTypeQryStr)) {

			sb.append(" and u.problem_state IN " + dynInClsStatTypeStr + " ");

		}

		if (!AmtCommonUtils.getTrimStr(issueSubmitterTypeQryStr).equals("All") || !AmtCommonUtils.isResourceDefined(issueSubmitterTypeQryStr)) {

			sb.append(" and u.submitter_ir_id IN   " + dynInClsIssSubTypeStr + "  ");

		}

		if (!AmtCommonUtils.getTrimStr(issueDateAllStr).equals("All")) {

			sb.append(" and u.submission_date >= ? AND  u.submission_date <= ? ");

		}

		sb.append(" group by u.severity,u.problem_state,u.state_action,u.submitter_ir_id,u.submitter_name,u.owner_ir_id,u.owner_name,u.ets_id,u.ref_no,u.pmo_id,u.title,u.class,u.info_src_flag,u.last_timestamp ");

		sb.append(" ORDER BY");

		//sort column

		if (AmtCommonUtils.isResourceDefined(sortColumn)) {

			sb.append(" " + sortColumn + " ");

		}

		//sort order

		if (AmtCommonUtils.isResourceDefined(sortOrder)) {

			sb.append(" " + sortOrder + " ");

		}

		sb.append(" with ur");

		this.finalQryStrWithPstmtForCr = sb.toString();

	}

	/**
	 * @return
	 */
	public String getFinalQryStrForCr() {
		return finalQryStrForCr;
	}

	/**
	 * @return
	 */
	public String getFinalQryStrWithPstmtForCr() {
		return finalQryStrWithPstmtForCr;
	}

	/**
	 * @return
	 */
	public String getSortColumn() {
		return sortColumn;
	}

	/**
	 * @return
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param string
	 */
	public void setSortColumn(String string) {
		sortColumn = string;
	}

	/**
	 * @param string
	 */
	public void setSortOrder(String string) {
		sortOrder = string;
	}

} //end of class
