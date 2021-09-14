package oem.edge.ets.fe.ismgt.bdlg;

import java.util.*;
import java.sql.*;
import oem.edge.common.*;
import oem.edge.amt.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.helpers.*;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.ets.fe.ismgt.dao.*;
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
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class FilterDetailsDataPrepAbsBean implements EtsIssFilterConstants {

	public static final String VERSION = "1.48";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private EtsIssFilterObjectKey issobjkey;
	private EtsIssFilterUserSessnParams etsIssUserSessn; //for filtering 
	private EtsIssSaveQryDataPrep etsSaveQryPrep;
	

	private ArrayList defIssueTypeList = new ArrayList();
	private ArrayList defSeverityTypeList = new ArrayList();
	private ArrayList defStatusTypeList = new ArrayList();
	private ArrayList defSubmitterList = new ArrayList();
	private ArrayList defCurOwnerList = new ArrayList();

	//blnk list//
	private ArrayList blnkList = new ArrayList();

	/**
	 * Constructor for FilterDetailsDataPrepAbsBean.
	 */
	public FilterDetailsDataPrepAbsBean(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
		super();
		this.request = request;
		this.response = response;
		this.issobjkey = issobjkey;
		this.etsIssUserSessn = new EtsIssFilterUserSessnParams(request, issobjkey);
		this.etsSaveQryPrep = new EtsIssSaveQryDataPrep(issobjkey);
		

	}

	/***
	 * This method will set default values for Issue Types, Severrity Types, Status Types
	 * and Submitter types
	 * 
	 */

	public abstract void setDefaultFilterParams();

	/**
	 * this abstract method will give the details of the final object that contains
	 * the data rep tab list,the field list and err msg list
	 */

	public abstract EtsIssFilterDetailsBean getFilterDetails() throws SQLException, Exception;

	/**
	 * This  method will implements various view rules on DB object, post process it
	 * and finally gives the formatted view list
	 */
	public abstract ArrayList postProcessDBRecs(ArrayList issueRepTabList) throws Exception;

	/***
	 * This method is the core method for issues filter which will first
	 * 1. set the DB VALUES/current values for populating list boxes
	 * 2. set the default lists to default values
	 * 2. set the previously set selected values/ if not present set to default values
	 * 3. set finally value to EtsIssFilterDetailsBean
	 */

	public abstract void setFilterParams() throws SQLException, Exception;

	/**
	 * This critical method will check the issue types, submitters, 
	 * it checks if the values are there in session, if not found, get from database, 
	 * 
	 */

	public abstract void setFilterDBParams() throws SQLException, Exception;

	/**
	 * for err msg list in process
	 * 
	 */

	public abstract ArrayList getErrMsgList();

	/**
	 * This critical method will check the selected issue types, submitters,  
	 * it checks if the values are there in session, if not found, set them to defaults, 
	 * 
	 */

	public abstract void setFilterUserSelectdParams();

	/***
	 * to get suitable DAO for the business objects
	 * 
	 */

	public FilterDAOAbs getFilterDAO() throws Exception {

		FilterDAOFactory daoFac = new FilterDAOFactory();

		FilterDAOAbs daoObj = daoFac.createFilterDAO(issobjkey);

		return daoObj;

	}

	/**
	 * Returns the request.
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Returns the response.
	 * @return HttpServletResponse
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Sets the request.
	 * @param request The request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Sets the response.
	 * @param response The response to set
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * Returns the issobjkey.
	 * @return EtsIssFilterObjectKey
	 */
	public EtsIssFilterObjectKey getIssobjkey() {
		return issobjkey;
	}

	/**
	 * Sets the issobjkey.
	 * @param issobjkey The issobjkey to set
	 */
	public void setIssobjkey(EtsIssFilterObjectKey issobjkey) {
		this.issobjkey = issobjkey;
	}

	/**
	 * Returns the etsIssUserSessn.
	 * @return EtsIssFilterUserSessnParams
	 */
	public EtsIssFilterUserSessnParams getEtsIssUserSessn() {
		return etsIssUserSessn;
	}

	/**
	 * Sets the etsIssUserSessn.
	 * @param etsIssUserSessn The etsIssUserSessn to set
	 */
	public void setEtsIssUserSessn(EtsIssFilterUserSessnParams etsIssUserSessn) {
		this.etsIssUserSessn = etsIssUserSessn;
	}

	/**
	 * Returns the defCurOwnerList.
	 * @return ArrayList
	 */
	public ArrayList getDefCurOwnerList() {
		return defCurOwnerList;
	}

	/**
	 * Returns the defIssueTypeList.
	 * @return ArrayList
	 */
	public ArrayList getDefIssueTypeList() {
		return defIssueTypeList;
	}

	/**
	 * Returns the defSeverityTypeList.
	 * @return ArrayList
	 */
	public ArrayList getDefSeverityTypeList() {
		return defSeverityTypeList;
	}

	/**
	 * Returns the defStatusTypeList.
	 * @return ArrayList
	 */
	public ArrayList getDefStatusTypeList() {
		return defStatusTypeList;
	}

	/**
	 * Returns the defSubmitterList.
	 * @return ArrayList
	 */
	public ArrayList getDefSubmitterList() {
		return defSubmitterList;
	}

	/**
	 * Sets the defCurOwnerList.
	 * @param defCurOwnerList The defCurOwnerList to set
	 */
	public void setDefCurOwnerList(ArrayList defCurOwnerList) {
		this.defCurOwnerList = defCurOwnerList;
	}

	/**
	 * Sets the defIssueTypeList.
	 * @param defIssueTypeList The defIssueTypeList to set
	 */
	public void setDefIssueTypeList(ArrayList defIssueTypeList) {
		this.defIssueTypeList = defIssueTypeList;
	}

	/**
	 * Sets the defSeverityTypeList.
	 * @param defSeverityTypeList The defSeverityTypeList to set
	 */
	public void setDefSeverityTypeList(ArrayList defSeverityTypeList) {
		this.defSeverityTypeList = defSeverityTypeList;
	}

	/**
	 * Sets the defStatusTypeList.
	 * @param defStatusTypeList The defStatusTypeList to set
	 */
	public void setDefStatusTypeList(ArrayList defStatusTypeList) {
		this.defStatusTypeList = defStatusTypeList;
	}

	/**
	 * Sets the defSubmitterList.
	 * @param defSubmitterList The defSubmitterList to set
	 */
	public void setDefSubmitterList(ArrayList defSubmitterList) {
		this.defSubmitterList = defSubmitterList;
	}

	/**
	 * to get issue source
	 * 
	 */

	public String getIssueAccess() {

		String issueAccess = "ALL";

		String userType = issobjkey.getEs().gDECAFTYPE;

		Global.println("userType==" + userType);

		if (userType.equals("I")) {

			issueAccess = "IBM";

		}

		Global.println("issue source==" + issueAccess);
		return issueAccess;

	}

	/**
	 * This method will set the error messages for the given form pages, for Issue Types
	 * 
	 */

	public ArrayList validateDateRange() {

		//compare dates properly
		String compFromdate = "";
		String compToDate = "";
		ArrayList errMsgList = new ArrayList();

		int sortstate = issobjkey.getSortState();

		String selIssueDateAll = AmtCommonUtils.getTrimStr(request.getParameter("issuedateall"));

		if (issobjkey.getState() == ETSISSRPTWALLFCGO || issobjkey.getState() == ETSISSRPTISUBFCGO || issobjkey.getState() == ETSISSRPTASGNDFCGO) { //only if user selects Go button

			if (!selIssueDateAll.equals("All")) { //only if ALL dates option is not selected

				String selIssueStartMonth = AmtCommonUtils.getTrimStr(request.getParameter("IssueStartMonth"));
				String selIssueStartDay = AmtCommonUtils.getTrimStr(request.getParameter("IssueStartDay"));
				String selIssueStartYear = AmtCommonUtils.getTrimStr(request.getParameter("IssueStartYear"));

				String selIssueStartDate = selIssueStartMonth + "-" + selIssueStartDay + "-" + selIssueStartYear;
				compFromdate = selIssueStartYear + "-" + selIssueStartMonth + "-" + selIssueStartDay;

				String selIssueEndMonth = AmtCommonUtils.getTrimStr(request.getParameter("IssueEndMonth"));
				String selIssueEndDay = AmtCommonUtils.getTrimStr(request.getParameter("IssueEndDay"));
				String selIssueEndYear = AmtCommonUtils.getTrimStr(request.getParameter("IssueEndYear"));

				String selIssueEndDate = selIssueEndMonth + "-" + selIssueEndDay + "-" + selIssueEndYear;
				compToDate = selIssueEndYear + "-" + selIssueEndMonth + "-" + selIssueEndDay;

				//if from date > to date
				if (EtsIssFilterUtils.diffDates(compFromdate, compToDate, "YYYY-MM-DD")) {
					errMsgList.add(issobjkey.getPropMap().get("filter.fromdt.grttodt.msg"));

				}

				Global.println("sel issue start date===" + selIssueStartDate);
				Global.println("sel issue end date===" + selIssueEndDate);

				if (!EtsIssFilterUtils.checkDayOfMonth(selIssueStartDate)) {

					errMsgList.add(issobjkey.getPropMap().get("filter.fromdt.mxdays.msg"));

				}

				if (!EtsIssFilterUtils.checkDayOfMonth(selIssueEndDate)) {

					errMsgList.add(issobjkey.getPropMap().get("filter.todt.mxdays.msg"));

				}

			} //only if all-dates option is not selected

		} //only if user selects Go button

		return errMsgList;

	}

	/**
		 * This method will set the error messages for the given form pages, for Issue Types
		 * 
		 */

	public ArrayList validateDateRange(String selIssueDateAll, String selIssueStartDate, String selIssueEndDate) {

		//compare dates properly
		String compFromdate = "";
		String compToDate = "";
		ArrayList errMsgList = new ArrayList();

		String selIssueStartMonth = "";
		String selIssueStartDay = "";
		String selIssueStartYear = "";

		///
		String selIssueEndMonth = "";
		String selIssueEndDay = "";
		String selIssueEndYear = "";

		if (issobjkey.getState() == ETSISSRPTWALLFCGO || issobjkey.getState() == ETSISSRPTISUBFCGO || issobjkey.getState() == ETSISSRPTASGNDFCGO) { //only if user selects Go button

			if (!selIssueDateAll.equals("All")) { //only if ALL dates option is not selected

				selIssueStartMonth = selIssueStartDate.substring(0, 2);
				selIssueStartDay = selIssueStartDate.substring(3, 5);
				selIssueStartYear = selIssueStartDate.substring(6);

				compFromdate = selIssueStartYear + "-" + selIssueStartMonth + "-" + selIssueStartDay;

				selIssueEndMonth = selIssueEndDate.substring(0, 2);
				;
				selIssueEndDay = selIssueEndDate.substring(3, 5);
				selIssueEndYear = selIssueEndDate.substring(6);

				compToDate = selIssueEndYear + "-" + selIssueEndMonth + "-" + selIssueEndDay;

				//if from date > to date
				if (EtsIssFilterUtils.diffDates(compFromdate, compToDate, "YYYY-MM-DD")) {
					errMsgList.add(issobjkey.getPropMap().get("filter.fromdt.grttodt.msg"));

				}

				Global.println("sel issue start date===" + selIssueStartDate);
				Global.println("sel issue end date===" + selIssueEndDate);

				if (!EtsIssFilterUtils.checkDayOfMonth(selIssueStartDate)) {

					errMsgList.add(issobjkey.getPropMap().get("filter.fromdt.mxdays.msg"));

				}

				if (!EtsIssFilterUtils.checkDayOfMonth(selIssueEndDate)) {

					errMsgList.add(issobjkey.getPropMap().get("filter.todt.mxdays.msg"));

				}

			} //only if all-dates option is not selected

		} //only if user selects Go button

		return errMsgList;

	}

	/**
	 * Returns the blnkList.
	 * @return ArrayList
	 */
	public ArrayList getBlnkList() {
		return blnkList;
	}

	/**
	 * Sets the blnkList.
	 * @param blnkList The blnkList to set
	 */
	public void setBlnkList(ArrayList blnkList) {
		this.blnkList = blnkList;
	}

	/**
	 * This  method will implements various view rules on DB object, post process it
	 * and finally gives the formatted view list
	 */

	public ArrayList postProcessRepRecs(ArrayList issueRepTabList) throws Exception {

		ArrayList postRepTabList = new ArrayList();

		int repsize = 0;

		String issueZone=""; //new issue or cq issue user issue
		String issueProblemId="";
		String issueCqTrkId="";
		String issueTitle="";
		String issueType="";
		String issueSeverity="";
		String issueStatus="";
		String issueClass="";
		String issueSubmitter="";
		String issueSubmitterName="";
		String issueLastTime="";
		String currentOwnerId="";
		String currentOwnerName="";
		String refId="";

		repsize = issueRepTabList.size();
		String loginUserEdgeId = AmtCommonUtils.getTrimStr(getIssobjkey().getEs().gUSERN);

		///new change///
		String localIssOpn = issobjkey.getOpn();

		for (int r = 0; r < repsize; r++) {

			EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

			issueZone = etsreptab.getIssueZone();
			issueProblemId = etsreptab.getIssueProblemId();
			issueCqTrkId = etsreptab.getIssueCqTrkId();
			refId=etsreptab.getRefId();
			
			if (refId.toUpperCase().startsWith("ETST")) {
				
				refId=refId.substring(7);
			}
			
			
			else if (refId.toUpperCase().startsWith("ETS")) {
				
				refId=refId.substring(6);
			}
			
			else {
				
				refId=refId;
			}
			
			issueTitle = etsreptab.getIssueTitle();

			//apply 21 chars rule for title // in fix changed to 63
			if (issueTitle.length() > 63) {

				issueTitle = issueTitle.substring(0, 63) + "..";

			}

			issueType = etsreptab.getIssueType();
			issueSeverity = etsreptab.getIssueSeverity();

			//remove like 1-,2- from severity 	
			int inx = issueSeverity.indexOf("-");

			if (inx != -1) {

				issueSeverity = issueSeverity.substring(inx + 1);

			}

			issueStatus = etsreptab.getIssueStatus();
			issueClass = etsreptab.getIssueClass();
			issueSubmitter = etsreptab.getIssueSubmitter();
			issueSubmitterName = etsreptab.getIssueSubmitterName();
			issueLastTime = etsreptab.getIssueLastTime();
			currentOwnerName = etsreptab.getCurrentOwnerName();
			currentOwnerId = etsreptab.getCurrentOwnerId();

			//cur owner rule for issue title//

			if (!localIssOpn.substring(0, 1).equals("4")) {

				if (loginUserEdgeId.equals(currentOwnerId)) {

					issueTitle = issueTitle + "*";

				}

			} //show * except in Issues/crs assigned to me

			else {

				issueTitle = issueTitle;

			}

			//owner name check
			if (currentOwnerId.equals("NOOWNER") || !AmtCommonUtils.isResourceDefined(currentOwnerId)) {
				currentOwnerName = "No owner";

			}

			EtsIssFilterRepTabBean etsreptabpost = new EtsIssFilterRepTabBean();
			etsreptabpost.setIssueZone(issueZone);
			etsreptabpost.setIssueProblemId(issueProblemId);
			etsreptabpost.setIssueCqTrkId(issueCqTrkId);
			etsreptabpost.setIssueTitle(issueTitle);
			etsreptabpost.setIssueType(issueType);
			etsreptabpost.setIssueSeverity(issueSeverity);
			etsreptabpost.setIssueStatus(issueStatus);
			etsreptabpost.setIssueClass(issueClass);
			etsreptabpost.setIssueSubmitter(issueSubmitter);
			etsreptabpost.setIssueSubmitterName(issueSubmitterName);
			etsreptabpost.setIssueLastTime(issueLastTime);
			etsreptabpost.setCurrentOwnerName(currentOwnerName);
			etsreptabpost.setCurrentOwnerId(currentOwnerId);
			etsreptabpost.setRefId(refId);

			postRepTabList.add(etsreptabpost);

		}

		return postRepTabList;

	}

	/**
		 * 
		 * @author V2PHANI
		 *
		 * To change the template for this generated type comment go to
		 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
		 */

	public String getUniqCsvName(EtsIssFilterObjectKey issfilterkey, String filterCriteriaName) {

		String uniqCsvName = "DefaultFile.csv";

		String dervdProjName = "";

		//since the project name will be having blank spaces in btween 

		String projName = issfilterkey.getProj().getName();

		ArrayList tokList = AmtCommonUtils.getArrayListFromStringTok(projName, " ");
		int toksize = 0;
		if (tokList != null && !tokList.isEmpty()) {

			toksize = tokList.size();
		}

		StringBuffer sb = new StringBuffer();

		String tempToken = "";
		String indToken = "";

		//for 1 or 2 elements

		if (toksize == 1 || toksize == 2) {

			for (int i = 0; i < toksize; i++) {

				tempToken = (String) tokList.get(i);

				int iIndex = tempToken.indexOf("/");

				if (iIndex != -1) {

					indToken = tempToken.substring(0, iIndex);

				} else {

					indToken = tempToken;
				}

				sb.append(indToken);
				sb.append("_");
			}

			dervdProjName = sb.toString();
		}

		//	for greater than 2 elements, restrict to 3 elements only

		if (toksize > 2) {

			for (int i = 0; i < 3; i++) {

				tempToken = (String) tokList.get(i);

				int iIndex = tempToken.indexOf("/");

				if (iIndex != -1) {

					indToken = tempToken.substring(0, iIndex);

				} else {

					indToken = tempToken;
				}

				sb.append(indToken);
				sb.append("_");
			}

			dervdProjName = sb.toString();

		}

		Global.println("DERIVED PROJECT NAME===" + dervdProjName);

		uniqCsvName = dervdProjName + filterCriteriaName + ".csv";

		Global.println("DERIVED CSV FILE NAME===" + uniqCsvName);

		return uniqCsvName;
	}

	public abstract ArrayList getDownLoadList();

	public abstract String getDownLoadFileName();

	/***
			 * This method is the core method for issues filter which will first
			 * 1. set the DB VALUES/current values for populating list boxes
			 * 2. set the default lists to default values
			 * 2. set the previously set selected values/ if not present set to default values
			 * 3. set finally value to EtsIssFilterDetailsBean
			 */

	public void setUserSaveQryParams(EtsIssFilterDetailsBean filDetBean) throws SQLException, Exception {

		//set the def issues in session///
		getEtsIssUserSessn().setPrevIssueTypeList(filDetBean.getPrevIssueTypeList());

		///set the def sever in session///
		getEtsIssUserSessn().setPrevSeverityTypeList(filDetBean.getPrevSeverityTypeList());

		//set def status types
		getEtsIssUserSessn().setPrevStatusTypeList(filDetBean.getPrevStatusTypeList());

		///set the def submitters in session///
		getEtsIssUserSessn().setPrevIssueSubmitterList(filDetBean.getPrevIssueSubmitterList());

		///set the current owners in session
		getEtsIssUserSessn().setPrevIssueCurOwnerList(filDetBean.getPrevIssueOwnerList());

		///set the def dates all in session///
		getEtsIssUserSessn().setIssueDateAll(filDetBean.getIssueDateAll());

		//set the start dates all in session///
		getEtsIssUserSessn().setIssueStartDate(filDetBean.getIssueStartDate());

		//set the start dates all in session///
		getEtsIssUserSessn().setIssueEndDate(filDetBean.getIssueEndDate());

		//click on search//
		String srchon = AmtCommonUtils.getTrimStr(getRequest().getParameter("srchon"));
		getEtsIssUserSessn().setIssueSrch(srchon);

	}

	/**
	 * 
	 * @throws SQLException
	 * @throws Exception
	 */
	public void setUserSaveSearchParams(String queryView) throws SQLException, Exception {

		//get the user saved params from db
		EtsIssFilterDetailsBean filDetBean = etsSaveQryPrep.getFiltDetFromSaveQry(queryView);

		//set the params to currentobj;
		setUserSaveQryParams(filDetBean);

	}

	/**
		 * 
		 * @return
		 */

	public boolean isDefaultUsrSaveQryExists(String queryView) throws SQLException, Exception {

		return etsSaveQryPrep.isDefaultUsrSaveQryExists(queryView);
	}

	/**
				 * To derive Save Qry Model from EtsIssFilter Model
				 * @param etsFilterBean
				 * @return
				 */

	public EtsIssSaveQryModel deriveSaveQryModel(EtsIssFilterDetailsBean etsFilterBean, String queryView) {

		return etsSaveQryPrep.deriveSaveQryModel(etsFilterBean, queryView);
	}
	
	
	
} //class
