package oem.edge.ets.fe.ismgt.helpers;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
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
 * This class provides the critical methods for set/get/checking the objects/Strings into session
 * Each Object set/get from session is provided separate method, to control the values put into session
 * and get them back
 * These are the following objects set into session and get them back
 * 
 * ISSUETYPELIST 		- 	ArrayList of issuetypes(DB)
 * ISSUESUBMITTERLIST	- 	ArrayList of issue submitters(DB)
 * ISSUECUROWNERLIST    -   ArrayList of current owners(DB)
 * PREVISSUETYPELIST		- 	ArrayList of selected issue types
 * PREVSEVERITYTYPELIST		- 	ArrayList of selected severity types
 * PREVSTATUSTYPELIST		- 	ArrayList of selected status types
 * PREVISSUESUBMITTERLIST		- 	ArrayList of selected issue submitters
 * PREVISSUECUROWNERLIST		- ArrayList of selected current owners
 * ISSUEPROJECTID		-  String of selected project id
 * ISSUEALLDATES   - check box value, of to select check-box all dates(All)
 * ISSUESTARTDATE  - start date for filter of issues(MM-DD-YYYY)
 * ISSUEENDDATE  - end date for filter of issues(MM-DD-YYYY)
 * ISSUESRCH - flag to know that search button has been clicked
 * etsamthf - AmtHeaderFooter object
 * ISSUESREPORTTABLIST - Issue report tab list
 * ISSUESAVEQRY - to save search criteria
 * 
 */

public class EtsIssFilterUserSessnParams {

	////////////////version of the class////
	public static final String VERSION = "1.48";
	private EtsIssFilterSessnHandler etsIssSessn;
	private EtsIssFilterObjectKey issObjKey;
	private String uniqSessnKey;

	/**
	 * Constructor for EtsIssFilterUserSessnParams.
	 */
	public EtsIssFilterUserSessnParams(HttpServletRequest request, EtsIssFilterObjectKey issObjKey) {
		super();

		this.etsIssSessn = new EtsIssFilterSessnHandler(request.getSession(true));
		this.issObjKey = issObjKey;

		String tempOpn = issObjKey.getOpn();

		if (AmtCommonUtils.isResourceDefined(tempOpn)) {

			this.uniqSessnKey = "ETSISSUES" + issObjKey.getProjectId() + issObjKey.getProblemType() + (issObjKey.getOpn().substring(0, 1)); //to make a unique combination
		}
		
		else {
			
			this.uniqSessnKey = "ETSISSUES" + issObjKey.getProjectId() + issObjKey.getProblemType(); //to make a unique combination
		}

	}

	/**
	 * This method will get basic issue types from session
	 */
	public ArrayList getIssueTypeList() {

		return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "ISSUETYPELIST");

	}

	/**
	 * This method will set basic issue types into session
	 */
	public void setIssueTypeList(ArrayList issueTypeList) {

		etsIssSessn.setSessionObjValue(uniqSessnKey + "ISSUETYPELIST", issueTypeList);

	}

	/**
	 * This method will return boolean, taking basic issue types  from session
	 */
	public boolean isIssueTypeListDefnd() {

		return EtsIssFilterUtils.isArrayListDefnd(getIssueTypeList());

	}

	/**
	 * This method will get issue submitters  from session
	 */
	public ArrayList getIssueSubmitterList() {

		return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "ISSUESUBMITTERLIST");

	}

	/**
	 * This method will set  issue submitters into session
	 */
	public void setIssueSubmitterList(ArrayList issueSubmitterList) {

		etsIssSessn.setSessionObjValue(uniqSessnKey + "ISSUESUBMITTERLIST", issueSubmitterList);

	}

	/**
	 * This method will return boolean, taking basic submiiters   from session
	 */
	public boolean isIssueSubmitterListDefnd() {

		return EtsIssFilterUtils.isArrayListDefnd(getIssueSubmitterList());

	}

	/**
	 * This method will get issue submitters  from session
	 */
	public ArrayList getIssueCurOwnerList() {

		return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "ISSUECUROWNERLIST");

	}

	/**
	 * This method will set  issue submitters into session
	 */
	public void setIssueCurOwnerList(ArrayList curOwnerList) {

		etsIssSessn.setSessionObjValue(uniqSessnKey + "ISSUECUROWNERLIST", curOwnerList);

	}

	/**
	 * This method will return boolean, taking basic submiiters   from session
	 */
	public boolean isIssueCurOwnerListDefnd() {

		return EtsIssFilterUtils.isArrayListDefnd(getIssueCurOwnerList());

	}

	/**
	 * This method will get selected issue types from session
	 */
	public ArrayList getPrevIssueTypeList() {

		return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "PREVISSUETYPELIST");

	}

	/**
	 * This method will set selected issue types into session
	 */
	public void setPrevIssueTypeList(ArrayList prevIssueTypeList) {

		etsIssSessn.setSessionObjValue(uniqSessnKey + "PREVISSUETYPELIST", prevIssueTypeList);

	}

	/**
	 * This method will return boolean, taking selected issue types  from session
	 */
	public boolean isPrevIssueTypeListDefnd() {

		return EtsIssFilterUtils.isArrayListDefnd(getPrevIssueTypeList());

	}

	/**
	 * This method will get selected severity types from session
	 */
	public ArrayList getPrevSeverityTypeList() {

		return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "PREVSEVERITYTYPELIST");

	}

	/**
	 * This method will set selected severity types into session
	 */
	public void setPrevSeverityTypeList(ArrayList prevSeverityTypeList) {

		etsIssSessn.setSessionObjValue(uniqSessnKey + "PREVSEVERITYTYPELIST", prevSeverityTypeList);

	}

	/**
	 * This method will return boolean, taking selected severity types  from session
	 */
	public boolean isPrevSeverityTypeListDefnd() {

		return EtsIssFilterUtils.isArrayListDefnd(getPrevSeverityTypeList());

	}

	/**
	 * This method will get selected status types from session
	 */
	public ArrayList getPrevStatusTypeList() {

		return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "PREVSTATUSTYPELIST");

	}

	/**
	 * This method will set selected status types into session
	 */
	public void setPrevStatusTypeList(ArrayList prevStatusTypeList) {

		etsIssSessn.setSessionObjValue(uniqSessnKey + "PREVSTATUSTYPELIST", prevStatusTypeList);

	}

	/**
	 * This method will return boolean, taking selected status types  from session
	 */
	public boolean isPrevStatusTypeListDefnd() {

		return EtsIssFilterUtils.isArrayListDefnd(getPrevStatusTypeList());

	}

	/**
	 * This method will get selected issue submitters  from session
	 */
	public ArrayList getPrevIssueSubmitterList() {

		return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "PREVISSUESUBMITTERLIST");

	}

	/**
	 * This method will set selected issue submitters into session
	 */
	public void setPrevIssueSubmitterList(ArrayList prevIssueSubmitterList) {

		etsIssSessn.setSessionObjValue(uniqSessnKey + "PREVISSUESUBMITTERLIST", prevIssueSubmitterList);

	}

	/**
	 * This method will return boolean, taking selected issue submitters  from session
	 */
	public boolean isPrevIssueSubmitterListDefnd() {

		return EtsIssFilterUtils.isArrayListDefnd(getPrevIssueSubmitterList());

	}

	/**
	 * This method will get selected issue current owners  from session
	 */
	public ArrayList getPrevIssueCurOwnerList() {

		return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "PREVISSUECUROWNERLIST");

	}

	/**
	 * This method will set selected issue current owners into session
	 */
	public void setPrevIssueCurOwnerList(ArrayList prevIssueCurOwnerList) {

		etsIssSessn.setSessionObjValue(uniqSessnKey + "PREVISSUECUROWNERLIST", prevIssueCurOwnerList);

	}

	/**
	 * This method will return boolean, taking selected issue current owners  from session
	 */
	public boolean isPrevIssueCurOwnerListDefnd() {

		return EtsIssFilterUtils.isArrayListDefnd(getPrevIssueCurOwnerList());

	}

	/**
	 * This method will get the check-all date param from session
	 */

	public String getIssueDateAll() {

		return (String) etsIssSessn.getSessionStrValue(uniqSessnKey + "ISSUEALLDATES");

	}

	/**
	 * This method will set all dates param into session
	 */

	public void setIssueDateAll(String dateAll) {

		etsIssSessn.setSessionStrValue(uniqSessnKey + "ISSUEALLDATES", dateAll);
	}

	/**
	 * This method will return boolean, taking date all param  from session
	 */
	public boolean isIssueDateAllDefnd() {

		return EtsIssFilterUtils.isStringDefnd(getIssueDateAll());

	}

	/**
	 * This method will get the start date for filter
	 */

	public String getIssueStartDate() {

		return (String) etsIssSessn.getSessionStrValue(uniqSessnKey + "ISSUESTARTDATE");

	}

	/**
	 * This method will set start date into session
	 */

	public void setIssueStartDate(String issueStartDate) {

		etsIssSessn.setSessionStrValue(uniqSessnKey + "ISSUESTARTDATE", issueStartDate);
	}

	/**
	 * This method will return boolean, taking issue start date  from session
	 */
	public boolean isIssueStartDateDefnd() {

		return EtsIssFilterUtils.isStringDefnd(getIssueStartDate());

	}

	/**
	 * This method will get the end date for filter
	 */

	public String getIssueEndDate() {

		return (String) etsIssSessn.getSessionStrValue(uniqSessnKey + "ISSUEENDDATE");

	}

	/**
	 * This method will set end date into session
	 */

	public void setIssueEndDate(String issueEndDate) {

		etsIssSessn.setSessionStrValue(uniqSessnKey + "ISSUEENDDATE", issueEndDate);
	}

	/**
	 * This method will return boolean, taking issue end date  from session
	 */
	public boolean isIssueEndDateDefnd() {

		return EtsIssFilterUtils.isStringDefnd(getIssueEndDate());

	}

	/**
	 * This method will get the end date for filter
	 */

	public String getIssueSrch() {

		return (String) etsIssSessn.getSessionStrValue(uniqSessnKey + "ISSUESRCH");

	}

	/**
	 * This method will set end date into session
	 */

	public void setIssueSrch(String issueSrch) {

		etsIssSessn.setSessionStrValue(uniqSessnKey + "ISSUESRCH", issueSrch);
	}

	/**
	 * This method will return boolean, taking issue end date  from session
	 */
	public boolean isIssueSrchDefnd() {

		return EtsIssFilterUtils.isStringDefnd(getIssueSrch());

	}

	/**
	 * 
	 * @author V2PHANI
	 * set the report tab list into session
	 */

	public void setIssReportTabListIntoSessn(ArrayList issRepTabList) {

		etsIssSessn.setSessionObjValue(uniqSessnKey + "ISSUESREPORTTABLIST", issRepTabList);
	}

	

	/**
		 * This method will get report tab list from session
		 */
	public ArrayList getIssReportTabListFromSessn() {

		return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "ISSUESREPORTTABLIST");

	}

	

	/**
		 * This method will set save qry into session
		 */

	public void setIssueSaveQry(String issueSaveQry) {

		etsIssSessn.setSessionStrValue(uniqSessnKey + "ISSUESAVEQRY", issueSaveQry);
	}

	/**
		 * This method will get the save qry for filter
		 */

	public String getIssueSaveQry() {

		return (String) etsIssSessn.getSessionStrValue(uniqSessnKey + "ISSUESAVEQRY");

	}

	/**
		 * This method will return boolean, taking issue save qry from session
		 */
	public boolean isIssueSaveQryDefnd() {

		return EtsIssFilterUtils.isStringDefnd(getIssueSaveQry());

	}
	
	/**
			 * 
			 * @author V2PHANI
			 * set the report tab list into session
			 */

		public void setIssTypeInfoTabListIntoSessn(ArrayList issTypInfoList) {
		
			etsIssSessn.setSessionObjValue(uniqSessnKey + "ISSTYPEINFOTABLIST", issTypInfoList);
		}

		
		/**
				 * This method will get report tab list from session
				 */
		public ArrayList getIssTypInfoTabListFromSessn() {
			

			return (ArrayList) etsIssSessn.getSessionObjValue(uniqSessnKey + "ISSTYPEINFOTABLIST");

		}


} //end of class
