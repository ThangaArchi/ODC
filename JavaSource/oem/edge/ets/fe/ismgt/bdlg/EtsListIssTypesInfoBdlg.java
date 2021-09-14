/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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

package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUserSessnParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterDetailsBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssSortObjModel;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsListIssTypesInfoBdlg extends FilterDetailsDataPrepAbsBean {
	
	

	/**
	 * 
	 */
	public EtsListIssTypesInfoBdlg(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issFilterObjkey) {
		super(request, response, issFilterObjkey);
		

		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param projectId
	 * @return
	 */

	public ArrayList getIssueTypeOwnerInfoList() throws SQLException, Exception {

		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		String projectId = getIssobjkey().getProjectId();

		ArrayList issTypeInfoList = new ArrayList();

		EtsIssSortDataPrep sortPrep = new EtsIssSortDataPrep(getIssobjkey());

		EtsIssSortObjModel sortObjModel = sortPrep.getIssTypeListSortInfoBean();

		///
		issTypeInfoList = dropDao.getIssueTypeOwnerInfoList(projectId, sortObjModel.getSortColumn(), sortObjModel.getSortOrder());

		//set the rep tab list into session, from filter bean
		getEtsIssUserSessn().setIssTypeInfoTabListIntoSessn(issTypeInfoList);

		return issTypeInfoList;
	}

	/**
			 * to get download file name
			 */

	public String getDownLoadFileName() {

		//set the csv name
		return getUniqCsvName(getIssobjkey(), "LIST_ALL_ISSUE_TYPES");

	}

	/**
		 * to get download list
		 */

	public ArrayList getDownLoadList() {

		ArrayList downLoadList = new ArrayList();
		ArrayList issTypeInfoList = new ArrayList();

		int repsize = 0;

		String issueType = "";
		String issueAccess = "";
		String issueSource = "";
		String ownerEmail = "";
		String ownerName = "";

		try {

			
			EtsIssFilterUserSessnParams filterSessnParams = new EtsIssFilterUserSessnParams(getRequest(), getIssobjkey());
			issTypeInfoList = filterSessnParams.getIssTypInfoTabListFromSessn();

			if (issTypeInfoList != null && !issTypeInfoList.isEmpty()) {

				repsize = issTypeInfoList.size();

			}

			///first fill the header information into download list

			ArrayList headerList = new ArrayList();
			headerList.add("Issue type");
			headerList.add("Issue type access");
			headerList.add("Owner name");
			headerList.add("Owner email");

			//submitter header

			//add headers to download list
			downLoadList.add(headerList);

			for (int r = 0; r < repsize; r++) {

				ArrayList tempList = new ArrayList();

				EtsDropDownDataBean dropBean = (EtsDropDownDataBean) issTypeInfoList.get(r);

				issueType = AmtCommonUtils.getTrimStr(dropBean.getIssueType());
				issueSource = AmtCommonUtils.getTrimStr(dropBean.getIssueSource());
				issueAccess = AmtCommonUtils.getTrimStr(dropBean.getIssueAccess());

				EtsIssOwnerInfo ownerInfo = dropBean.getOwnerInfo();

				ownerEmail = AmtCommonUtils.getTrimStr(ownerInfo.getUserEmail());
				ownerName = AmtCommonUtils.getTrimStr(ownerInfo.getUserFullName());

				tempList.add(issueType);
				tempList.add(issueAccess);
				tempList.add(ownerName);
				tempList.add(ownerEmail);

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

	/**
		 * error msg list(right now only dates
		 */
	public ArrayList getErrMsgList() {

		return getBlnkList();

	}

	/***
		 * This method will finally set the attributes for the EtsIssFilterDetailsBean, either from session/or from
		 * static lists/or from DB
		 * 
		 */

	public EtsIssFilterDetailsBean getFilterDetails() throws SQLException, Exception {
		return null;
	}

	/**
		 * This  method will implements various view rules on DB object, post process it
		 * and finally gives the formatted view list
		 */
	public ArrayList postProcessDBRecs(ArrayList dropDownList) throws Exception {

		ArrayList filterDropList = new ArrayList();
		int dropsize = 0;
		String issueAccess = "All Team members";

		if (EtsIssFilterUtils.isArrayListDefndWithObj(dropDownList)) {

			dropsize = dropDownList.size();
		}

		for (int i = 0; i < dropsize; i++) {

			EtsDropDownDataBean dropBean = (EtsDropDownDataBean) dropDownList.get(i);
			issueAccess = AmtCommonUtils.getTrimStr(dropBean.getIssueAccess());

			if (issueAccess.equals("ALL:IBM") || issueAccess.equals("ALL:EXT")) {

				issueAccess = "All Team members";
			}

			if (issueAccess.equals("IBM:IBM")) {

				issueAccess = "All IBM Team members only";
			}

			dropBean.setIssueAccess(issueAccess);

			filterDropList.add(dropBean);

		}

		return filterDropList;

	}

	/***
		 * This method will set default values for Issue Types, Severrity Types, Status Types
		 * and Submitter types
		 * 
		 */

	public void setDefaultFilterParams() {

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

	}

	/***
	 * This method is the core method for issues filter which will first
	 * 1. set the DB VALUES/current values for populating list boxes
	 * 2. set the default lists to default values
	 * 2. set the previously set selected values/ if not present set to default values
	 * 3. set finally value to EtsIssFilterDetailsBean
	 */

	public void setFilterParams() throws SQLException, Exception {

	}

} //end of class
