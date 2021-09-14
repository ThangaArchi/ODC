package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.*;
import java.util.*;
import java.io.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.ets.fe.ismgt.helpers.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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
 * This bean makes all the data reqd, for the  Issues I have submitted report
 */
public class IssuesISubDataPrepRep extends FilterDetailsDataPrepAbsBean {

	public static final String VERSION = "1.49";

	/**
	 * Constructor for IssuesISubDataPrepRep.
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public IssuesISubDataPrepRep(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
		super(request, response, issobjkey);
	}

	/***
	 * This method will finally set the attributes for the EtsIssFilterDetailsBean, either from session/or from
	 * static lists/or from DB
	 * 
	 */

	public EtsIssFilterDetailsBean getFilterDetails() throws SQLException, Exception {

		//set all reqd params//

		setFilterParams();

		///

		EtsIssFilterDetailsBean etsFilterBean = new EtsIssFilterDetailsBean();

		//previous values of issues types selected params	
		etsFilterBean.setPrevIssueTypeList(getEtsIssUserSessn().getPrevIssueTypeList());

		//previous values of issues severity types selected params	
		etsFilterBean.setPrevSeverityTypeList(getEtsIssUserSessn().getPrevSeverityTypeList());

		//previous values of issues status types selected params	
		etsFilterBean.setPrevStatusTypeList(getEtsIssUserSessn().getPrevStatusTypeList());

		//previous values of submiiter types selected params	
		etsFilterBean.setPrevIssueSubmitterList(getEtsIssUserSessn().getPrevIssueSubmitterList());

		//previous values of owner list types selected params	
		etsFilterBean.setPrevIssueOwnerList(getEtsIssUserSessn().getPrevIssueCurOwnerList());

		//set the project id info
		etsFilterBean.setIssueProjectId(getIssobjkey().getProjectId());

		//set the problem type
		etsFilterBean.setProblemType(getIssobjkey().getIssueSubType());

		//set the issue date all
		etsFilterBean.setPrevIssueDateAll(getEtsIssUserSessn().getIssueDateAll());

		if (isDefaultUsrSaveQryExists("ISUB")) { //if user save does not exists

			//set the issue start date
			etsFilterBean.setPrevIssueStartDate(getEtsIssUserSessn().getIssueStartDate());

			//set the isseu end date
			etsFilterBean.setPrevIssueEndDate(getEtsIssUserSessn().getIssueEndDate());

		}

		//set issue source
		etsFilterBean.setIssueAccess(getIssueAccess());

		//set any error messages
		etsFilterBean.setErrMsgList(getErrMsgList());

		//delegate qry prep to another bean
		//EtsIssFilterQryPrepBean qryPrepBean = new EtsIssFilterQryPrepBean(etsFilterBean, getIssobjkey());
		EtsIssSortDataPrep sortDataPrep = new EtsIssSortDataPrep(getIssobjkey());
		EtsIssFilterQryPrepBean qryPrepBean = sortDataPrep.getSortInfoBean(etsFilterBean);

		ArrayList repTabList = new ArrayList();

		if (getIssobjkey().getIssueSubType().equals(ETSCHANGESUBTYPE)) {

			repTabList = getFilterDAO().getReportTabListWithPstmtForCr(qryPrepBean);

		} else {

			repTabList = getFilterDAO().getReportTabListWithPstmt(qryPrepBean);

		}

		//ArrayList repTabList = getFilterDAO().getReportTabListWithPstmt(qryPrepBean);
		//ArrayList repTabList = getFilterDAO().getReportTabList(qryPrepBean);

		//set report tab list
		etsFilterBean.setIssueReportTabList(postProcessDBRecs(repTabList));

		//set srch on flag on bean,where it has come from from default /from search
		etsFilterBean.setSrchOn(getEtsIssUserSessn().getIssueSrch());

		//		set the rep tab list into session, from filter bean
		getEtsIssUserSessn().setIssReportTabListIntoSessn(etsFilterBean.getIssueReportTabList());

		return etsFilterBean;

	}

	/***
	 * This method will set default values for Issue Types, Severrity Types, Status Types
	 * and Submitter types
	 * 
	 */

	public void setDefaultFilterParams() {

		//set default issue types as All
		getDefIssueTypeList().add("All");

		//set default issue severity types as All
		getDefSeverityTypeList().add("All");

		if (getIssobjkey().getIssueSubType().equals(ETSCHANGESUBTYPE)) {

			//	set default issue status types as Open
			//getDefStatusTypeList().add("Under Review");
			getDefStatusTypeList().add("All");

		} else {

			//set default issue status types as Open
			//			New to Submit
			//			Submitted > Assigned
			//			getDefStatusTypeList().add("Submit"); //new to submit
			//			getDefStatusTypeList().add("Open");
			//			getDefStatusTypeList().add("Assigned");
			//			getDefStatusTypeList().add("Resolved");
			getDefStatusTypeList().add("All");

		}

		//set default issue submitters as current login edge_userid
		getDefSubmitterList().add(getIssobjkey().getEs().gUSERN);

		//set default cur owners as All
		getDefCurOwnerList().add("All");

	}

	/**
	 * This critical method will check the issue types, submitters, 
	 * it checks if the values are there in session, if not found, get from database, 
	 * 
	 */

	public void setFilterDBParams() throws SQLException, Exception {

	} //end of method

	/**
	 * This critical method will check the selected issue types, submitters,  
	 * it checks if the values are there in session, if not found, set them to defaults, 
	 * 
	 */

	public void setFilterUserSelectdParams() {

		///set the def issues in session///
		getEtsIssUserSessn().setPrevIssueTypeList(getDefIssueTypeList());

		///set the def sever in session///
		getEtsIssUserSessn().setPrevSeverityTypeList(getDefSeverityTypeList());

		//set def status types
		getEtsIssUserSessn().setPrevStatusTypeList(getDefStatusTypeList());

		///set the def submitters in session///
		getEtsIssUserSessn().setPrevIssueSubmitterList(getDefSubmitterList());

		///set the current owners in session
		getEtsIssUserSessn().setPrevIssueCurOwnerList(getDefCurOwnerList());

		///set the def dates all in session///
		getEtsIssUserSessn().setIssueDateAll("All");

		//click on search//
		String srchon = AmtCommonUtils.getTrimStr(getRequest().getParameter("srchon"));
		getEtsIssUserSessn().setIssueSrch(srchon);

	}

	/***
	 * This method is the core method for issues filter which will first
	 * 1. set the DB VALUES/current values for populating list boxes
	 * 2. set the default lists to default values
	 * 2. set the previously set selected values/ if not present set to default values
	 * 3. set finally value to EtsIssFilterDetailsBean
	 */

	public void setFilterParams() throws SQLException, Exception {
		
		if (!isDefaultUsrSaveQryExists("ISUB")) { //if user save does not exists

		//1. set the DB PARAMS first

		setFilterDBParams();

		//2. set default selections for issues

		setDefaultFilterParams();

		//3.set the actuals selections for issues

		setFilterUserSelectdParams();
		
		}
		
		else {
			
			setUserSaveSearchParams("ISUB");
		}

	}

	/**
	 * error msg list(right now only dates
	 */
	public ArrayList getErrMsgList() {

		return getBlnkList();

	}

	/**
	 * This  method will implements various view rules on DB object, post process it
	 * and finally gives the formatted view list
	 */
	public ArrayList postProcessDBRecs(ArrayList issueRepTabList) throws Exception {

		return postProcessRepRecs(issueRepTabList);

	}

	/**
		 * This method will get the report table list and transforms into Arraylist, which is reqd
		 * for Download capability
		 */

	public ArrayList getDownLoadListForIssues() {

		ArrayList downLoadList = new ArrayList();

		int repsize = 0;

		String userType = "E";

		try {

			EtsIssFilterUserSessnParams filterSessnParams = new EtsIssFilterUserSessnParams(getRequest(), getIssobjkey());
			ArrayList issueRepTabList = filterSessnParams.getIssReportTabListFromSessn();

			EtsIssUserRolesModel usrRolesModel = getIssobjkey().getUsrRolesModel();

			if (issueRepTabList != null && !issueRepTabList.isEmpty()) {

				repsize = issueRepTabList.size();

			}

			///first fill the header information into download list

			ArrayList headerList = new ArrayList();
			headerList.add("Id");
			headerList.add("Title");
			headerList.add("Issue type");

			if (usrRolesModel.isShowOwnerName()) { //table driven

				if (usrRolesModel.isUsrInternal()) { //if user is internal

					headerList.add("Current owner");

				} //usr is internal

			} //table param

			headerList.add("Severity");
			headerList.add("Status");

			//add headers to download list
			downLoadList.add(headerList);

			String issueTitle = "";
			String issueType = "";
			String issueSeverity = "";
			String issueStatus = "";
			String issueSubmitterName = "";
			String issueCurOwnerName = "";
			String refId="";

			for (int r = 0; r < repsize; r++) {

				ArrayList tempList = new ArrayList();

				EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

				issueTitle = etsreptab.getIssueTitle();
				issueType = etsreptab.getIssueType();
				issueSeverity = etsreptab.getIssueSeverity();
				issueStatus = etsreptab.getIssueStatus();
				issueSubmitterName = etsreptab.getIssueSubmitterName();
				issueCurOwnerName = etsreptab.getCurrentOwnerName();
				refId=etsreptab.getRefId();

				tempList.add(refId);
				tempList.add(issueTitle);
				tempList.add(issueType);

				//rules for owner
				//show owner details to internals only

				if (usrRolesModel.isShowOwnerName()) { //table driven

					if (usrRolesModel.isUsrInternal()) { //if user is internal

						if (issueCurOwnerName.equals("No owner")) {

							tempList.add(issueCurOwnerName);

						} else {

							tempList.add(issueCurOwnerName);

						}

					} //usr is internal

				} //table param

				tempList.add(issueSeverity);
				tempList.add(issueStatus);

				//add tempList to downLoadList
				downLoadList.add(tempList);
			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsChgOwnerCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

		}

		return downLoadList;

	}
	public String getDownLoadFileNameForIssues() {

		//set the csv name
		return getUniqCsvName(getIssobjkey(), "ISSUES_I_SUBMITTED");

	}

	/**
				 * This method will get the report table list and transforms into Arraylist, which is reqd
				 * for Download capability
				 */

	public ArrayList getDownLoadListForPCRs() {

		ArrayList downLoadList = new ArrayList();

		int repsize = 0;

		String userType = "E";

		try {

			EtsIssFilterUserSessnParams filterSessnParams = new EtsIssFilterUserSessnParams(getRequest(), getIssobjkey());
			ArrayList issueRepTabList = filterSessnParams.getIssReportTabListFromSessn();

			EtsIssUserRolesModel usrRolesModel = getIssobjkey().getUsrRolesModel();

			if (issueRepTabList != null && !issueRepTabList.isEmpty()) {

				repsize = issueRepTabList.size();

			}

			///first fill the header information into download list

			ArrayList headerList = new ArrayList();
			headerList.add("Id");
			headerList.add("Title");
			headerList.add("Priority");
			headerList.add("Status");

			//add headers to download list
			downLoadList.add(headerList);

			String issueTitle = "";
			String issueSeverity = "";
			String issueStatus = "";
			String issueSubmitterName = "";
			String refId="";

			for (int r = 0; r < repsize; r++) {

				ArrayList tempList = new ArrayList();

				EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

				issueTitle = etsreptab.getIssueTitle();
				issueSeverity = etsreptab.getIssueSeverity();
				issueStatus = etsreptab.getIssueStatus();
				issueSubmitterName = etsreptab.getIssueSubmitterName();
				refId=etsreptab.getRefId();

				tempList.add(refId);
				tempList.add(issueTitle);
				tempList.add(issueSeverity);
				tempList.add(issueStatus);

				//add tempList to downLoadList
				downLoadList.add(tempList);
			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsChgOwnerCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

		}

		return downLoadList;

	}

	public String getDownLoadFileNameForPCRs() {

		//set the csv name
		return getUniqCsvName(getIssobjkey(), "CHANGE_REQUESTS_I_SUBMITTED");

	}

	/**
			 * to get download file name
			 */

	public String getDownLoadFileName() {

		if (getIssobjkey().getIssueSubType().equals(ETSCHANGESUBTYPE)) {

			return getDownLoadFileNameForPCRs();

		} else {

			return getDownLoadFileNameForIssues();
		}

	}

	/**
		 * to get download list
		 */

	public ArrayList getDownLoadList() {

		if (getIssobjkey().getIssueSubType().equals(ETSCHANGESUBTYPE)) {

			return getDownLoadListForPCRs();

		} else {

			return getDownLoadListForIssues();
		}

	}

} //end of class
