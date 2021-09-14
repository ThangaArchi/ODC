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
 * This bean prepares the data for Filter Conditions when the user clicks Work all issues
 */
public class ShowFilterCondsWrkAllDataPrep extends FilterDetailsDataPrepAbsBean implements EtsIssFilterConstants {

	public static final String VERSION = "1.11";

	/**
	 * Constructor for ShowFilterCondsWrkAllDataPrep.
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public ShowFilterCondsWrkAllDataPrep(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
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

		//set srch on flag on bean,where it has come from from default /from search
		etsFilterBean.setSrchOn(getEtsIssUserSessn().getIssueSrch());

		//set any error messages
		etsFilterBean.setErrMsgList(getErrMsgList());

		//set issue source
		etsFilterBean.setIssueAccess(getIssueAccess());
		
		
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
			getDefStatusTypeList().add("Under Review");

		} else {

			//set default issue status types as Open
			getDefStatusTypeList().add("Open");

		}

		//set default issue submitters as All
		getDefSubmitterList().add("All");

		//set default cur owners as All
		getDefCurOwnerList().add("All");

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

		if (!getEtsIssUserSessn().isPrevIssueTypeListDefnd()) {

			getEtsIssUserSessn().setPrevIssueTypeList(getDefIssueTypeList());

		}

		if (!getEtsIssUserSessn().isPrevSeverityTypeListDefnd()) {

			getEtsIssUserSessn().setPrevSeverityTypeList(getDefSeverityTypeList());
		}

		//prev sel status types

		if (!getEtsIssUserSessn().isPrevStatusTypeListDefnd()) {

			getEtsIssUserSessn().setPrevStatusTypeList(getDefStatusTypeList());

		}

		//prev issue sel submiiters

		if (!getEtsIssUserSessn().isPrevIssueSubmitterListDefnd()) {

			getEtsIssUserSessn().setPrevIssueSubmitterList(getDefSubmitterList());

		}

		//prev current owner 

		if (!getEtsIssUserSessn().isPrevIssueCurOwnerListDefnd()) {

			getEtsIssUserSessn().setPrevIssueCurOwnerList(getDefCurOwnerList());

		}

		//all flag only if not coming from click on search button
		if (!getEtsIssUserSessn().isIssueSrchDefnd()) {

			if (!getEtsIssUserSessn().isIssueDateAllDefnd()) {

				getEtsIssUserSessn().setIssueDateAll("All");

			}

		}

		//prev issue start date

		if (!getEtsIssUserSessn().isIssueStartDateDefnd()) {

			getEtsIssUserSessn().setIssueStartDate(EtsIssFilterUtils.getCurDate());

		}

		//prev issue end date
		if (!getEtsIssUserSessn().isIssueEndDateDefnd()) {

			getEtsIssUserSessn().setIssueEndDate(EtsIssFilterUtils.getCurDate());

		}

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

		//1. set the DB PARAMS first

		setFilterDBParams();

		//2. set default selections for issues

		setDefaultFilterParams();

		//3.set the actuals selections for issues

		setFilterUserSelectdParams();

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

		return getBlnkList();

	} //end of class
	
	
	/**
			 * This method will get the report table list and transforms into Arraylist, which is reqd
			 * for Download capability
			 */

		public ArrayList getDownLoadList() {

			ArrayList downLoadList = new ArrayList();

			return downLoadList;

		}

		public String getDownLoadFileName() {

			//set the csv name
			return "";

		}


}
