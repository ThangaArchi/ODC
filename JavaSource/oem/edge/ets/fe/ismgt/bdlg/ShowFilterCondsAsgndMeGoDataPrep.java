package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ismgt.dao.EtsIssSaveQryDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUserSessnParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterDetailsBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterQryPrepBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterRepTabBean;
import oem.edge.ets.fe.ismgt.model.EtsIssSaveQryModel;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
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
 * This bean prepares the data required,when the user clicks SEARCH button, on Filter Conditions, from Issues assigned tome
 */

public class ShowFilterCondsAsgndMeGoDataPrep extends FilterDetailsDataPrepAbsBean {

	public static final String VERSION = "1.11";

	/**
	 * Constructor for ShowFilterCondsAsgndMeGoDataPrep.
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public ShowFilterCondsAsgndMeGoDataPrep(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
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

		//set issues ( from session)
		etsFilterBean.setIssueTypeList(getEtsIssUserSessn().getIssueTypeList());

		//set severity(static)
		etsFilterBean.setSeverityTypeList(getFilterDAO().getSeverityTypes());

		//set status(static)
		etsFilterBean.setStatusTypeList(getFilterDAO().getStatusTypes());

		//set issue submitters(from session)
		etsFilterBean.setIssueSubmitterList(getEtsIssUserSessn().getIssueSubmitterList());

		//set issue cur owners(from session)
		etsFilterBean.setIssueOwnerList(getEtsIssUserSessn().getIssueCurOwnerList());

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

		//set the issue start date
		etsFilterBean.setPrevIssueStartDate(getEtsIssUserSessn().getIssueStartDate());

		//set the isseu end date
		etsFilterBean.setPrevIssueEndDate(getEtsIssUserSessn().getIssueEndDate());

		//set issue source
		etsFilterBean.setIssueAccess(getIssueAccess());

		//set any error messages
		etsFilterBean.setErrMsgList(getErrMsgList(getEtsIssUserSessn().getIssueDateAll(), getEtsIssUserSessn().getIssueStartDate(), getEtsIssUserSessn().getIssueEndDate()));

		//get recs only when no validation error msgs//

		if (etsFilterBean.getErrMsgList().size() == 0) {

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

			//set report tab list
			etsFilterBean.setIssueReportTabList(postProcessDBRecs(repTabList));

		}

		//set srch on flag on bean,where it has come from from default /from search
		etsFilterBean.setSrchOn(getEtsIssUserSessn().getIssueSrch());

		//		set the rep tab list into session, from filter bean
		getEtsIssUserSessn().setIssReportTabListIntoSessn(etsFilterBean.getIssueReportTabList());
		
		//set issue save qry
		etsFilterBean.setChkSaveQry(getEtsIssUserSessn().getIssueSaveQry());

		//		if user has saved some search criteria, save it
		if (AmtCommonUtils.getTrimStr(etsFilterBean.getChkSaveQry()).equals("Y")) {

			EtsIssSaveQryModel saveQryModel = deriveSaveQryModel(etsFilterBean, "ASSGNTOME");

			EtsIssSaveQryDAO saveQryDao = new EtsIssSaveQryDAO();
			saveQryDao.insertUserSaveQry(saveQryModel);

		}

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

		//set default issue status types as Open
		getDefStatusTypeList().add("Open");

		//set default issue submitters as All
		getDefSubmitterList().add("All");

		//set default cur owners as All
		getDefCurOwnerList().add(getIssobjkey().getEs().gUSERN);

	}

	/**
	 * This critical method will check the issue types, submitters, 
	 * it checks if the values are there in session, if not found, get from database, 
	 * 
	 */

	public void setFilterDBParams() throws SQLException, Exception {

		//set the issue type from db,if not from sessn
		if (!getEtsIssUserSessn().isIssueTypeListDefnd()) {

			///set the issues in session///
			getEtsIssUserSessn().setIssueTypeList(getFilterDAO().getIssueTypes(getIssobjkey().getIssueSubType()));

		}

		//set the issue submtrs from db,if not from sessn
		if (!getEtsIssUserSessn().isIssueSubmitterListDefnd()) {

			getEtsIssUserSessn().setIssueSubmitterList(getFilterDAO().getSubmitterList());

		}

		//set the cur owners  from db,if not from sessn	
		if (!getEtsIssUserSessn().isIssueCurOwnerListDefnd()) {

			getEtsIssUserSessn().setIssueCurOwnerList(getFilterDAO().getOwnersList());

		}

	} //end of method

	/**
	 * This critical method will check the selected issue types, submitters,  
	 * it checks if the values are there in session, if not found, set them to defaults, 
	 * 
	 */

	public void setFilterUserSelectdParams() {

		//prev sel issues types

		String selIssueArr[] = getRequest().getParameterValues("issuetype");

		if (selIssueArr != null) {

			getEtsIssUserSessn().setPrevIssueTypeList(AmtCommonUtils.getArrayListFromArray(selIssueArr));

		} else {

			getEtsIssUserSessn().setPrevIssueTypeList(getDefIssueTypeList());

		}

		//prev sel severity types

		String selIssueSeverityArr[] = getRequest().getParameterValues("issueseverity");

		if (selIssueSeverityArr != null) {

			getEtsIssUserSessn().setPrevSeverityTypeList(AmtCommonUtils.getArrayListFromArray(selIssueSeverityArr));

		} else {

			getEtsIssUserSessn().setPrevSeverityTypeList(getDefSeverityTypeList());
		}

		//prev sel status types

		String selIssueStatusArr[] = getRequest().getParameterValues("issuestatus");

		if (selIssueStatusArr != null) {

			getEtsIssUserSessn().setPrevStatusTypeList(AmtCommonUtils.getArrayListFromArray(selIssueStatusArr));

		} else {

			getEtsIssUserSessn().setPrevStatusTypeList(getDefStatusTypeList());

		}

		//prev issue sel submiiters

		String selIssueSubmitterArr[] = getRequest().getParameterValues("issuesubmitter");

		if (selIssueSubmitterArr != null) {

			getEtsIssUserSessn().setPrevIssueSubmitterList(AmtCommonUtils.getArrayListFromArray(selIssueSubmitterArr));

		} else {

			getEtsIssUserSessn().setPrevIssueSubmitterList(getDefSubmitterList());

		}

		//prev current owner 

		String selIssueCurOwnerArr[] = getRequest().getParameterValues("issuecurowner");

		if (selIssueCurOwnerArr != null) {

			getEtsIssUserSessn().setPrevIssueCurOwnerList(AmtCommonUtils.getArrayListFromArray(selIssueCurOwnerArr));

		} else {

			getEtsIssUserSessn().setPrevIssueCurOwnerList(getDefCurOwnerList());

		}

		//all flag

		String selIssueDateAll = AmtCommonUtils.getTrimStr(getRequest().getParameter("issuedateall"));
		System.out.println("date all==========" + selIssueDateAll);
		getEtsIssUserSessn().setIssueDateAll(selIssueDateAll);

		//prev issue start date

		String selIssueStartMonth = AmtCommonUtils.getTrimStr(getRequest().getParameter("IssueStartMonth"));
		String selIssueStartDay = AmtCommonUtils.getTrimStr(getRequest().getParameter("IssueStartDay"));
		String selIssueStartYear = AmtCommonUtils.getTrimStr(getRequest().getParameter("IssueStartYear"));

		if (!AmtCommonUtils.isResourceDefined(selIssueStartMonth)) {

			getEtsIssUserSessn().setIssueStartDate(EtsIssFilterUtils.getCurDate());

		} else {

			String selIssueStartDate = selIssueStartMonth + "-" + selIssueStartDay + "-" + selIssueStartYear;
			getEtsIssUserSessn().setIssueStartDate(selIssueStartDate);

		}

		//prev issue end date

		String selIssueEndMonth = AmtCommonUtils.getTrimStr(getRequest().getParameter("IssueEndMonth"));
		String selIssueEndDay = AmtCommonUtils.getTrimStr(getRequest().getParameter("IssueEndDay"));
		String selIssueEndYear = AmtCommonUtils.getTrimStr(getRequest().getParameter("IssueEndYear"));

		if (!AmtCommonUtils.isResourceDefined(selIssueEndMonth)) {

			getEtsIssUserSessn().setIssueEndDate(EtsIssFilterUtils.getCurDate());

		} else {

			String selIssueEndDate = selIssueEndMonth + "-" + selIssueEndDay + "-" + selIssueEndYear;
			getEtsIssUserSessn().setIssueEndDate(selIssueEndDate);

		}

		//click on search//
		String srchon = AmtCommonUtils.getTrimStr(getRequest().getParameter("srchon"));
		getEtsIssUserSessn().setIssueSrch(srchon);
		
		//click on save qry//
		String saveQry = AmtCommonUtils.getTrimStr(getRequest().getParameter("chk_save_qry"));
		getEtsIssUserSessn().setIssueSaveQry(saveQry);

	}

	/***
	 * This method is the core method for issues filter which will first
	 * 1. set the DB VALUES/current values for populating list boxes
	 * 2. set the default lists to default values
	 * 2. set the previously set selected values/ if not present set to default values
	 * 3. set finally value to EtsIssFilterDetailsBean
	 */

	public void setFilterParams() throws SQLException, Exception {

		int sortstate = getIssobjkey().getSortState();
		int flopstate = getIssobjkey().getFlopstate();

		//1. set the DB PARAMS first

		setFilterDBParams();

		//2. set default selections for issues

		setDefaultFilterParams();

		if (sortstate == 0) {

			if (flopstate == 0) {

				//3.set the actuals selections for issues

				setFilterUserSelectdParams();

			}

		}

	}

	/**
	 * error msg list(right now only dates
	 */
	public ArrayList getErrMsgList() {

		return validateDateRange();

	}

	/**
	 * This  method will implements various view rules on DB object, post process it
	 * and finally gives the formatted view list
	 */
	public ArrayList postProcessDBRecs(ArrayList issueRepTabList) throws Exception {

		return postProcessRepRecs(issueRepTabList);

	}

	/**
			 * error msg list(right now only dates
			 */
	public ArrayList getErrMsgList(String selIssueDateAll, String selIssueStartDate, String selIssueEndDate) {

		return validateDateRange(selIssueDateAll, selIssueStartDate, selIssueEndDate);

	}

	/**
		 * This method will get the report table list and transforms into Arraylist, which is reqd
		 * for Download capability
		 */

	public ArrayList getDownLoadList() {

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

			//submitter header

			if (!getIssobjkey().isProjBladeType()) {

				headerList.add("Submitter");

			} else { //in case of blade project, show submitter details only in case of internals

				if (usrRolesModel.isBladeUsrInt()) {

					headerList.add("Submitter");

				}

			}

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

				//rules for submitter
				//show submitter to externals only in case of non-blade projects
				if (!getIssobjkey().isProjBladeType()) {

					tempList.add(issueSubmitterName);

				} else { //in case of blade project, show submitter details only in case of internals

					if (usrRolesModel.isBladeUsrInt()) {

						tempList.add(issueSubmitterName);

					}

				}

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

	public String getDownLoadFileName() {

		//set the csv name
		return getUniqCsvName(getIssobjkey(), "ISSUES_ASSIGNED_ME");

	}

}
