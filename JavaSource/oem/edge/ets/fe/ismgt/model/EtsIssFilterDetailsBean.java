package oem.edge.ets.fe.ismgt.model;

import java.util.*;
import java.io.*;
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
 * This is the abstract class,to represent Issue Filter details
 */
public class EtsIssFilterDetailsBean implements Serializable {

	public static final String VERSION = "1.10";

	private String issueProjectId;

	private ArrayList issueTypeList;
	private ArrayList severityTypeList;
	private ArrayList statusTypeList;
	private ArrayList issueSubmitterList;
	private ArrayList issueOwnerList;
	private String issueDateAll;
	private String issueStartDate;
	private String issueEndDate;

	//previous selections in List
	private ArrayList prevIssueTypeList;
	private ArrayList prevSeverityTypeList;
	private ArrayList prevStatusTypeList;
	private ArrayList prevIssueSubmitterList;
	private ArrayList prevIssueOwnerList;
	private String prevIssueDateAll;
	private String prevIssueStartDate;
	private String prevIssueEndDate;

	//report objects for issue types/change types
	private ArrayList issueReportTabList;
	
	//download list object
	private ArrayList downLoadList;
	private String downLoadFileName; 

	///any validation messgs list for the form//
	private ArrayList errMsgList;
	
	///problem type////
	private String problemType;
	
	//srch on,if user click/comes by search button
	private String srchOn;
	
	//issue source str//
	private String issueAccess;
	
	//sort columns
	private String sortColumn;
	
	//sort order
	private String sortOrder;
	
	//chk save qry (save search criteria
	private String chkSaveQry;
	
	///search by num option
	private String searchByNum;

	/**
	 * Constructor for EtsIssFilterDetailsAbsBean.
	 */
	public EtsIssFilterDetailsBean() {
		super();
	}

	/**
	 * Returns the errMsgList.
	 * @return ArrayList
	 */
	public ArrayList getErrMsgList() {
		return errMsgList;
	}

	/**
	 * Returns the issueDateAll.
	 * @return String
	 */
	public String getIssueDateAll() {
		return issueDateAll;
	}

	/**
	 * Returns the issueEndDate.
	 * @return String
	 */
	public String getIssueEndDate() {
		return issueEndDate;
	}

	/**
	 * Returns the issueProjectId.
	 * @return String
	 */
	public String getIssueProjectId() {
		return issueProjectId;
	}

	/**
	 * Returns the issueReportTabList.
	 * @return ArrayList
	 */
	public ArrayList getIssueReportTabList() {
		return issueReportTabList;
	}

	/**
	 * Returns the issueStartDate.
	 * @return String
	 */
	public String getIssueStartDate() {
		return issueStartDate;
	}

	/**
	 * Returns the issueSubmitterList.
	 * @return ArrayList
	 */
	public ArrayList getIssueSubmitterList() {
		return issueSubmitterList;
	}

	/**
	 * Returns the issueTypeList.
	 * @return ArrayList
	 */
	public ArrayList getIssueTypeList() {
		return issueTypeList;
	}

	/**
	 * Returns the prevIssueDateAll.
	 * @return String
	 */
	public String getPrevIssueDateAll() {
		return prevIssueDateAll;
	}

	/**
	 * Returns the prevIssueEndDate.
	 * @return String
	 */
	public String getPrevIssueEndDate() {
		return prevIssueEndDate;
	}

	/**
	 * Returns the prevIssueStartDate.
	 * @return String
	 */
	public String getPrevIssueStartDate() {
		return prevIssueStartDate;
	}

	/**
	 * Returns the prevIssueSubmitterList.
	 * @return ArrayList
	 */
	public ArrayList getPrevIssueSubmitterList() {
		return prevIssueSubmitterList;
	}

	/**
	 * Returns the prevIssueTypeList.
	 * @return ArrayList
	 */
	public ArrayList getPrevIssueTypeList() {
		return prevIssueTypeList;
	}

	/**
	 * Returns the prevSeverityTypeList.
	 * @return ArrayList
	 */
	public ArrayList getPrevSeverityTypeList() {
		return prevSeverityTypeList;
	}

	/**
	 * Returns the prevStatusTypeList.
	 * @return ArrayList
	 */
	public ArrayList getPrevStatusTypeList() {
		return prevStatusTypeList;
	}

	/**
	 * Returns the severityTypeList.
	 * @return ArrayList
	 */
	public ArrayList getSeverityTypeList() {
		return severityTypeList;
	}

	/**
	 * Returns the statusTypeList.
	 * @return ArrayList
	 */
	public ArrayList getStatusTypeList() {
		return statusTypeList;
	}

	/**
	 * Sets the errMsgList.
	 * @param errMsgList The errMsgList to set
	 */
	public void setErrMsgList(ArrayList errMsgList) {
		this.errMsgList = errMsgList;
	}

	/**
	 * Sets the issueDateAll.
	 * @param issueDateAll The issueDateAll to set
	 */
	public void setIssueDateAll(String issueDateAll) {
		this.issueDateAll = issueDateAll;
	}

	/**
	 * Sets the issueEndDate.
	 * @param issueEndDate The issueEndDate to set
	 */
	public void setIssueEndDate(String issueEndDate) {
		this.issueEndDate = issueEndDate;
	}

	/**
	 * Sets the issueProjectId.
	 * @param issueProjectId The issueProjectId to set
	 */
	public void setIssueProjectId(String issueProjectId) {
		this.issueProjectId = issueProjectId;
	}

	/**
	 * Sets the issueReportTabList.
	 * @param issueReportTabList The issueReportTabList to set
	 */
	public void setIssueReportTabList(ArrayList issueReportTabList) {
		this.issueReportTabList = issueReportTabList;
	}

	/**
	 * Sets the issueStartDate.
	 * @param issueStartDate The issueStartDate to set
	 */
	public void setIssueStartDate(String issueStartDate) {
		this.issueStartDate = issueStartDate;
	}

	/**
	 * Sets the issueSubmitterList.
	 * @param issueSubmitterList The issueSubmitterList to set
	 */
	public void setIssueSubmitterList(ArrayList issueSubmitterList) {
		this.issueSubmitterList = issueSubmitterList;
	}

	/**
	 * Sets the issueTypeList.
	 * @param issueTypeList The issueTypeList to set
	 */
	public void setIssueTypeList(ArrayList issueTypeList) {
		this.issueTypeList = issueTypeList;
	}

	/**
	 * Sets the prevIssueDateAll.
	 * @param prevIssueDateAll The prevIssueDateAll to set
	 */
	public void setPrevIssueDateAll(String prevIssueDateAll) {
		this.prevIssueDateAll = prevIssueDateAll;
	}

	/**
	 * Sets the prevIssueEndDate.
	 * @param prevIssueEndDate The prevIssueEndDate to set
	 */
	public void setPrevIssueEndDate(String prevIssueEndDate) {
		this.prevIssueEndDate = prevIssueEndDate;
	}

	/**
	 * Sets the prevIssueStartDate.
	 * @param prevIssueStartDate The prevIssueStartDate to set
	 */
	public void setPrevIssueStartDate(String prevIssueStartDate) {
		this.prevIssueStartDate = prevIssueStartDate;
	}

	/**
	 * Sets the prevIssueSubmitterList.
	 * @param prevIssueSubmitterList The prevIssueSubmitterList to set
	 */
	public void setPrevIssueSubmitterList(ArrayList prevIssueSubmitterList) {
		this.prevIssueSubmitterList = prevIssueSubmitterList;
	}

	/**
	 * Sets the prevIssueTypeList.
	 * @param prevIssueTypeList The prevIssueTypeList to set
	 */
	public void setPrevIssueTypeList(ArrayList prevIssueTypeList) {
		this.prevIssueTypeList = prevIssueTypeList;
	}

	/**
	 * Sets the prevSeverityTypeList.
	 * @param prevSeverityTypeList The prevSeverityTypeList to set
	 */
	public void setPrevSeverityTypeList(ArrayList prevSeverityTypeList) {
		this.prevSeverityTypeList = prevSeverityTypeList;
	}

	/**
	 * Sets the prevStatusTypeList.
	 * @param prevStatusTypeList The prevStatusTypeList to set
	 */
	public void setPrevStatusTypeList(ArrayList prevStatusTypeList) {
		this.prevStatusTypeList = prevStatusTypeList;
	}

	/**
	 * Sets the severityTypeList.
	 * @param severityTypeList The severityTypeList to set
	 */
	public void setSeverityTypeList(ArrayList severityTypeList) {
		this.severityTypeList = severityTypeList;
	}

	/**
	 * Sets the statusTypeList.
	 * @param statusTypeList The statusTypeList to set
	 */
	public void setStatusTypeList(ArrayList statusTypeList) {
		this.statusTypeList = statusTypeList;
	}

	/**
	 * Returns the issueOwnerList.
	 * @return ArrayList
	 */
	public ArrayList getIssueOwnerList() {
		return issueOwnerList;
	}

	/**
	 * Returns the prevIssueOwnerList.
	 * @return ArrayList
	 */
	public ArrayList getPrevIssueOwnerList() {
		return prevIssueOwnerList;
	}

	/**
	 * Sets the issueOwnerList.
	 * @param issueOwnerList The issueOwnerList to set
	 */
	public void setIssueOwnerList(ArrayList issueOwnerList) {
		this.issueOwnerList = issueOwnerList;
	}

	/**
	 * Sets the prevIssueOwnerList.
	 * @param prevIssueOwnerList The prevIssueOwnerList to set
	 */
	public void setPrevIssueOwnerList(ArrayList prevIssueOwnerList) {
		this.prevIssueOwnerList = prevIssueOwnerList;
	}

	/**
	 * Returns the problemType.
	 * @return String
	 */
	public String getProblemType() {
		return problemType;
	}

	/**
	 * Sets the problemType.
	 * @param problemType The problemType to set
	 */
	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

	/**
	 * Returns the srchOn.
	 * @return String
	 */
	public String getSrchOn() {
		return srchOn;
	}

	/**
	 * Sets the srchOn.
	 * @param srchOn The srchOn to set
	 */
	public void setSrchOn(String srchOn) {
		this.srchOn = srchOn;
	}

	

	/**
	 * Returns the issueAccess.
	 * @return String
	 */
	public String getIssueAccess() {
		return issueAccess;
	}

	/**
	 * Sets the issueAccess.
	 * @param issueAccess The issueAccess to set
	 */
	public void setIssueAccess(String issueAccess) {
		this.issueAccess = issueAccess;
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

	/**
	 * @return
	 */
	public ArrayList getDownLoadList() {
		return downLoadList;
	}

	/**
	 * @param list
	 */
	public void setDownLoadList(ArrayList list) {
		downLoadList = list;
	}

	/**
	 * @return
	 */
	public String getDownLoadFileName() {
		return downLoadFileName;
	}

	/**
	 * @param string
	 */
	public void setDownLoadFileName(String string) {
		downLoadFileName = string;
	}

	/**
	 * @return
	 */
	public String getChkSaveQry() {
		return chkSaveQry;
	}

	/**
	 * @param string
	 */
	public void setChkSaveQry(String string) {
		chkSaveQry = string;
	}

	/**
	 * @return
	 */
	public String getSearchByNum() {
		return searchByNum;
	}

	/**
	 * @param string
	 */
	public void setSearchByNum(String string) {
		searchByNum = string;
	}

}

