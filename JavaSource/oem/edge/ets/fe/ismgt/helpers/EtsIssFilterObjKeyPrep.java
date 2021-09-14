package oem.edge.ets.fe.ismgt.helpers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssUserRoleFilter;
import oem.edge.ets.fe.ismgt.model.EtsFilterCondsViewParamsBean;
import oem.edge.ets.fe.ismgt.model.EtsFilterRepViewParamsBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterResource;
import oem.edge.ets.fe.ismgt.resources.EtsPcrResource;
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
 * This class prepares the Key EtsIssFilterObjectKey object and returns to Servlet
 */
public class EtsIssFilterObjKeyPrep implements EtsIssFilterConstants {

	public static final String VERSION = "1.49";
	private HttpServletRequest request;
	private EdgeAccessCntrl es;

	/**
	 * Constructor for EtsIssFilterObjKeyPrep.
	 */
	public EtsIssFilterObjKeyPrep(HttpServletRequest request, EdgeAccessCntrl es) {
		super();
		this.request = request;
		this.es = es;
	}

	/***
	 * to return the state of the servlet
	 * 
	 */

	private int getServletState(HttpServletRequest request) {

		//get opn
		String opn = AmtCommonUtils.getTrimStr(request.getParameter("opn"));
		int state = 10;

		//get the  opn in int
		if (opn != null && !opn.equals("")) {
			state = Integer.parseInt(opn); //get the  state of the Servlet, i.e state=operation
		}

		if (opn.equals("")) {
			state = 10;
		}

		if (!AmtCommonUtils.isResourceDefined(opn)) {

			opn = "10";
		}

		return state;
	} //end of method

	/**
	 * get opn desc ,for bread crumb puprose, etcc...
	 */

	private String getStateDesc(HttpServletRequest request, HashMap issPropMap) {

		String stateDesc = "Issues/changes";
		int state = getServletState(request);
		String isType = AmtCommonUtils.getTrimStr(request.getParameter("istyp"));

		if (isType.equals("iss")) {

			switch (state) {

				////ISSUES////

				case ETSISSRPTWALL :

					stateDesc = (String) issPropMap.get("filter.issue.wrkall.linktext");

					break;

				case ETSISSRPTWALLFC :

					stateDesc = (String) issPropMap.get("filter.issue.wrkall.linktext");

					break;

				case ETSISSRPTWALLFCGO :

					stateDesc = (String) issPropMap.get("filter.issue.wrkall.linktext");

					break;

				case ETSISSRPTISUB :

					stateDesc = (String) issPropMap.get("filter.issue.isub.linktext");

					break;

				case ETSISSRPTISUBFC :

					stateDesc = (String) issPropMap.get("filter.issue.isub.linktext");

					break;

				case ETSISSRPTISUBFCGO :

					stateDesc = (String) issPropMap.get("filter.issue.isub.linktext");

					break;

				case ETSISSRPTASGND :

					stateDesc = (String) issPropMap.get("filter.issue.assignme.linktext");

					break;

				case ETSISSRPTASGNDFC :

					stateDesc = (String) issPropMap.get("filter.issue.assignme.linktext");

					break;

				case ETSISSRPTASGNDFCGO :

					stateDesc = (String) issPropMap.get("filter.issue.assignme.linktext");

					break;

				default :

					stateDesc = "Issues/changes";

					break;

			}

		} // end of only if issues

		if (isType.equals("chg")) {

			switch (state) {
				//CHANGE REQUESTS

				case ETSISSRPTWALL :

					stateDesc = (String) issPropMap.get("filter.chgreq.wrkall.linktext");

					break;

				case ETSISSRPTWALLFC :

					stateDesc = (String) issPropMap.get("filter.chgreq.wrkall.linktext");

					break;

				case ETSISSRPTWALLFCGO :

					stateDesc = (String) issPropMap.get("filter.chgreq.wrkall.linktext");

					break;

				case ETSISSRPTISUB :

					stateDesc = (String) issPropMap.get("filter.chgreq.isub.linktext");

					break;

				case ETSISSRPTISUBFC :

					stateDesc = (String) issPropMap.get("filter.chgreq.isub.linktext");

					break;

				case ETSISSRPTISUBFCGO :

					stateDesc = (String) issPropMap.get("filter.chgreq.isub.linktext");

					break;

				case ETSISSRPTASGND :

					stateDesc = (String) issPropMap.get("filter.chgreq.assignme.linktext");

					break;

				case ETSISSRPTASGNDFC :

					stateDesc = (String) issPropMap.get("filter.chgreq.assignme.linktext");

					break;

				case ETSISSRPTASGNDFCGO :

					stateDesc = (String) issPropMap.get("filter.chgreq.assignme.linktext");

					break;

				default :

					stateDesc = "Issues/changes";

					break;

			}

		} // end of only if changes

		return stateDesc;
	} //end of method

	/***
	 * to set FC view params
	 */

	private EtsFilterCondsViewParamsBean getFilterCondsViewParams(String isType, HashMap issPropMap) {

		EtsFilterCondsViewParamsBean fcView = new EtsFilterCondsViewParamsBean();

		if (isType.equals("iss")) {

			fcView.setFcHeaderName((String) issPropMap.get("filter.iss.fchead.name"));
			fcView.setFcIssTypeName((String) issPropMap.get("filter.iss.fcistype.name"));
			fcView.setFcSeverityName((String) issPropMap.get("filter.iss.fcsev.name"));
			fcView.setFcStatusName((String) issPropMap.get("filter.iss.fcstatus.name"));
			fcView.setFcSubName((String) issPropMap.get("filter.iss.fcsub.name"));
			fcView.setFcCownName((String) issPropMap.get("filter.iss.fccown.name"));
			fcView.setFcDateSubName((String) issPropMap.get("filter.iss.datesub.name"));

		}

		if (isType.equals("chg")) {

			fcView.setFcHeaderName((String) issPropMap.get("filter.chg.fchead.name"));
			fcView.setFcIssTypeName((String) issPropMap.get("filter.chg.fcistype.name"));
			fcView.setFcSeverityName((String) issPropMap.get("filter.chg.fcsev.name"));
			fcView.setFcStatusName((String) issPropMap.get("filter.chg.fcstatus.name"));
			fcView.setFcSubName((String) issPropMap.get("filter.chg.fcsub.name"));
			fcView.setFcCownName((String) issPropMap.get("filter.chg.fccown.name"));
			fcView.setFcDateSubName((String) issPropMap.get("filter.chg.datesub.name"));

		}

		return fcView;
	}

	/***
	 * to set Filter Rep view params
	 */

	private EtsFilterRepViewParamsBean getRepTabViewParams(EtsIssFilterObjectKey issfilterkey, String isType, HashMap issPropMap) {

		EtsFilterRepViewParamsBean repView = new EtsFilterRepViewParamsBean();

		if (isType.equals("iss")) {

			repView.setRepWelcomeMsg((String) issPropMap.get("filter.iss.repwel.msg") + getModifyFilterLink(issfilterkey)+(String) issPropMap.get("filter.iss.repwel.multiprint.msg"));
			repView.setRepHeaderName((String) issPropMap.get("filter.iss.rephead.name"));
			repView.setRepTitleName((String) issPropMap.get("filter.iss.reptitle.name"));
			repView.setRepIssueTypeName((String) issPropMap.get("filter.iss.repisstype.name"));
			repView.setRepSubName((String) issPropMap.get("filter.iss.repsub.name"));
			repView.setRepCownerName((String) issPropMap.get("filter.iss.repcown.name"));
			repView.setRepSeverityName((String) issPropMap.get("filter.iss.repsev.name"));
			repView.setRepStatusName((String) issPropMap.get("filter.iss.repstatus.name"));
			repView.setRepTrkId((String) issPropMap.get("filter.iss.reptrkid.name"));

		}

		if (isType.equals("chg")) {

			repView.setRepWelcomeMsg((String) issPropMap.get("filter.chg.repwel.msg") + getModifyFilterLink(issfilterkey));
			repView.setRepHeaderName((String) issPropMap.get("filter.chg.rephead.name"));
			repView.setRepTitleName((String) issPropMap.get("filter.chg.reptitle.name"));
			repView.setRepIssueTypeName((String) issPropMap.get("filter.chg.repisstype.name"));
			repView.setRepSubName((String) issPropMap.get("filter.chg.repsub.name"));
			repView.setRepCownerName((String) issPropMap.get("filter.chg.repcown.name"));
			repView.setRepSeverityName((String) issPropMap.get("filter.chg.repsev.name"));
			repView.setRepStatusName((String) issPropMap.get("filter.chg.repstatus.name"));
			repView.setRepTrkId((String) issPropMap.get("filter.chg.reptrkid.name"));

		}

		return repView;
	}

	/**
	 * get Issue Filter Object Key
	 */

	public EtsIssFilterObjectKey getEtsIssFilterObjKey(HttpServletRequest request, EdgeAccessCntrl es, ETSProj proj) throws SQLException, Exception {

		EtsIssFilterObjectKey issobjkey = new EtsIssFilterObjectKey();

		try {

			String istyp = AmtCommonUtils.getTrimStr(request.getParameter("istyp"));

			String projectidStr = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

			//get all the form params
			Hashtable params = AmtCommonUtils.getServletParameters(request);
			
			//set request and response
			issobjkey.setRequest(request);
			
			//add params to object key
			issobjkey.setParams(params);

			///set es,opn,issue type,project id
			issobjkey.setEs(es);
			issobjkey.setOpn(AmtCommonUtils.getTrimStr(request.getParameter("opn")));
			issobjkey.setProblemType(istyp);
			issobjkey.setProjectId(AmtCommonUtils.getTrimStr(request.getParameter("proj")));
			issobjkey.setTc(AmtCommonUtils.getTrimStr(request.getParameter("tc")));
			issobjkey.setCc(AmtCommonUtils.getTrimStr(request.getParameter("cc")));
			issobjkey.setLinkid(AmtCommonUtils.getTrimStr(request.getParameter("linkid")));
			issobjkey.setState(getServletState(request));
			issobjkey.setProj(proj);

			//set the properties in key obj in form of HaspMap//
			HashMap issPropMap = EtsIssFilterResource.getInstance().getFilterPropMap();
			issobjkey.setPropMap(issPropMap);

			//set the PCR properties in key obj in form of HaspMap//
			HashMap pcrPropMap = EtsPcrResource.getInstance().getPcrPropMap();
			issobjkey.setPcrPropMap(pcrPropMap);

			//state desc
			String stateDesc = getStateDesc(request, issPropMap);
			issobjkey.setStateDesc(stateDesc);

			if (istyp.equals("iss")) {

				issobjkey.setIssueSubType("Defect");
				issobjkey.setRepViewBean(getRepTabViewParams(issobjkey, istyp, issPropMap));
				issobjkey.setFcViewBean(getFilterCondsViewParams(istyp, issPropMap));

			}

			if (istyp.equals("chg")) {

				issobjkey.setIssueSubType("Change");
				issobjkey.setRepViewBean(getRepTabViewParams(issobjkey, istyp, issPropMap));
				issobjkey.setFcViewBean(getFilterCondsViewParams(istyp, issPropMap));

			}

			//set sort state
			int sortstate = getSortState(params);
			issobjkey.setSortState(sortstate);

			//set back state 
			// it is very imp parameter, as if its value equals to opn value or state value like for Work with all issues, say 20>21>22
			//22 is state after modiy search criteria and get the results
			// so if flop value is set to 22, the program takes the prev. selected value from session of modiy sreach criteria
			int flopstate = getFlopState(params);
			issobjkey.setFlopstate(flopstate);

			//set issue owner
			issobjkey.setShowIssueOwner(EtsIssFilterUtils.isShowIssueOwner(proj));

			issobjkey.setProjBladeType(proj.isProjBladeType());

			//get user/roles matrix
			EtsIssUserRoleFilter userFilter = new EtsIssUserRoleFilter();
			EtsIssUserRolesModel usrRolesModel = userFilter.getUserRoleMatrix(issobjkey);

			/////////////////
			issobjkey.setUsrRolesModel(usrRolesModel);

			//set pmo project param
			issobjkey.setProjPmoEnabled(isProjectPmoEnabled(issobjkey.getProj().getPmo_project_id()));

		} finally {

		}

		return issobjkey;

	}

	public int getSortState(Hashtable params) {

		int state = 0;

		//		sort by issue title  
		String issue_sort_trkid_A = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_trkid_A.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_trkid_A)) {

			state = SORTTRKID_A;

		}

		String issue_sort_trkid_D = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_trkid_D.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_trkid_D)) {

			state = SORTTRKID_D;

		}

		//		sort by issue title  
		String issue_sort_title_A = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_title_A.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_title_A)) {

			state = SORTISSUETITLE_A;

		}

		String issue_sort_title_D = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_title_D.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_title_D)) {

			state = SORTISSUETITLE_D;

		}

		//		sort by issue type
		String issue_sort_isstype_A = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_isstype_A.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_isstype_A)) {

			state = SORTISSUETYPE_A;

		}

		String issue_sort_isstype_D = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_isstype_D.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_isstype_D)) {

			state = SORTISSUETYPE_D;

		}

		//sort by submitter

		String issue_sort_submitter_A = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_submitter_A.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_submitter_A)) {

			state = SORTSUBMITTER_A;

		}

		String issue_sort_submitter_D = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_submitter_D.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_submitter_D)) {

			state = SORTSUBMITTER_D;

		}

		//sort by current owner

		String issue_sort_owner_A = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_owner_A.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_owner_A)) {

			state = SORTOWNER_A;

		}

		String issue_sort_owner_D = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_owner_D.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_owner_D)) {

			state = SORTOWNER_D;

		}

		//sort by severity

		String issue_sort_severity_A = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_severity_A.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_severity_A)) {

			state = SORTSEVERITY_A;

		}

		String issue_sort_severity_D = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_severity_D.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_severity_D)) {

			state = SORTSEVERITY_D;

		}

		//sort by status

		String issue_sort_status_A = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_status_A.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_status_A)) {

			state = SORTSTATUS_A;

		}

		String issue_sort_status_D = AmtCommonUtils.getTrimStr((String) params.get("issue_sort_status_D.x"));

		if (AmtCommonUtils.isResourceDefined(issue_sort_status_D)) {

			state = SORTSTATUS_D;

		}

		/////////////sort states for issue type list info

		String issuetype_sort_name_A = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_name_A.x"));

		if (AmtCommonUtils.isResourceDefined(issuetype_sort_name_A)) {

			state = SORTLISTISSTYPENAME_A;

		}

		String issuetype_sort_name_D = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_name_D.x"));

		if (AmtCommonUtils.isResourceDefined(issuetype_sort_name_D)) {

			state = SORTLISTISSTYPENAME_D;

		}

		String issuetype_sort_access_A = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_access_A.x"));

		if (AmtCommonUtils.isResourceDefined(issuetype_sort_access_A)) {

			state = SORTLISTISSTYPEACCESS_A;

		}

		String issuetype_sort_access_D = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_access_D.x"));

		if (AmtCommonUtils.isResourceDefined(issuetype_sort_access_D)) {

			state = SORTLISTISSTYPEACCESS_D;

		}

		String issuetype_sort_owner_A = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_owner_A.x"));

		if (AmtCommonUtils.isResourceDefined(issuetype_sort_owner_A)) {

			state = SORTLISTISSTYPEOWNER_A;

		}
		String issuetype_sort_owner_D = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_owner_D.x"));

		if (AmtCommonUtils.isResourceDefined(issuetype_sort_owner_D)) {

			state = SORTLISTISSTYPEOWNER_D;

		}

		String issuetype_sort_ownremail_A = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_ownremail_A.x"));

		if (AmtCommonUtils.isResourceDefined(issuetype_sort_ownremail_A)) {

			state = SORTLISTISSTYPEOWNEREMAIL_A;

		}

		String issuetype_sort_ownremail_D = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_ownremail_D.x"));

		if (AmtCommonUtils.isResourceDefined(issuetype_sort_ownremail_D)) {

			state = SORTLISTISSTYPEOWNEREMAIL_D;

		}
		
		String issuetype_sort_backupowner_A = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_backupowner_A.x"));
		if (AmtCommonUtils.isResourceDefined(issuetype_sort_backupowner_A)) {

			state = SORTLISTISSTYPEBACKUPOWNER_A;

		}
		String issuetype_sort_backupowner_D = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_backupowner_D.x"));
		if (AmtCommonUtils.isResourceDefined(issuetype_sort_backupowner_D)) {

			state = SORTLISTISSTYPEBACKUPOWNER_D;

		}

		String issuetype_sort_backupownremail_A = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_backupownremail_A.x"));
		
		if (AmtCommonUtils.isResourceDefined(issuetype_sort_backupownremail_A)) {

			state = SORTLISTISSTYPEBACKUPOWNEREMAIL_A;

		}

		String issuetype_sort_backupownremail_D = AmtCommonUtils.getTrimStr((String) params.get("issuetype_sort_backupownremail_D.x"));

		if (AmtCommonUtils.isResourceDefined(issuetype_sort_backupownremail_D)) {

			state = SORTLISTISSTYPEBACKUPOWNEREMAIL_D;

		}
		
		//get sort state from url, if it is defined and this happens when the user click Return to Issues/changes
		//in either view/modify/resolve/close/reject/withdraw/change owner actions
		
		String srt=AmtCommonUtils.getTrimStr((String) params.get("srt"));
		
		if(AmtCommonUtils.isResourceDefined(srt)) {
			
			state=Integer.parseInt(srt);
		}

		Global.println("GET SORT SORT STATE IN ISSUE FILTER OBJ KEY====" + state);

		return state;

	}

	public int getFlopState(Hashtable params) {

		int flopstate = 0;

		//		sort by issue title  
		String flop_Str = AmtCommonUtils.getTrimStr((String) params.get("flop"));

		Global.println("flop str==" + flop_Str);

		if (AmtCommonUtils.isResourceDefined(flop_Str)) {

			flopstate = Integer.parseInt(flop_Str);

		}

		return flopstate;

	}

	/**
	 * This method will print 'Modify filter link' on the welcome page of report page
	 * @param issfilterkey
	 * @return
	 */

	public String getModifyFilterLink(EtsIssFilterObjectKey issfilterkey) {

		EtsIssFilterGuiUtils filtGuiUtils = new EtsIssFilterGuiUtils();

		StringBuffer sb = new StringBuffer();

		String issopn = issfilterkey.getOpn();

		if (AmtCommonUtils.isResourceDefined(issopn)) {

			String opnqual = issopn.substring(0, 1);
			HashMap propMap = issfilterkey.getPropMap();

			sb.append(filtGuiUtils.printModifySearchLink(issfilterkey, (String) propMap.get("filter.reppg.fclnk.txt"), opnqual + "1"));
			sb.append(" to change search criteria.");

		}

		return sb.toString();

	}

	/**
					 * 
					 * @param pmoProjectId
					 * @return
					 */

	public boolean isProjectPmoEnabled(String pmoProjectId) {

		if (AmtCommonUtils.isResourceDefined(pmoProjectId)) {

			return true;
		}

		return false;
	}

} //end of class
