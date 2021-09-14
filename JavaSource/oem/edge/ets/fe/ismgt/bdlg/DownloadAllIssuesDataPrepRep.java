/*
 * Created on Mar 23, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.ismgt.bdlg;



import java.util.ArrayList;
import java.util.Hashtable;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;


import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

import oem.edge.ets.fe.ismgt.dao.EtsDownloadAllIssuesDAO;
import oem.edge.ets.fe.ismgt.dao.EtsIssueAddFieldsDAO;

import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;

import oem.edge.ets.fe.ismgt.model.EtsIssueAddFieldsBean;

/**
 * @author Shanmugam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DownloadAllIssuesDataPrepRep implements EtsIssueConstants, EtsIssFilterConstants {
	
	EtsIssFilterObjectKey issfilterkey = null;
	
	public DownloadAllIssuesDataPrepRep(EtsIssFilterObjectKey issfilterkey) {
		this.issfilterkey = issfilterkey;
	}
	
	
	public ArrayList getDownloadList() {
		
		String projectId = issfilterkey.getProjectId();
		
		
		ArrayList downLoadList = new ArrayList();	
		///first fill the header information into download list

		ArrayList headerList = new ArrayList();
		//headerList.add("ID");
		// sathish
		headerList.add("Id");
		
		headerList.add("Title");
		
		headerList.add("Issue type");
		
		headerList.add("Submitter");
		
		
		
		headerList.add("Submitter's company");
		
		headerList.add("Submitter's e-mail");
		
		headerList.add("Submitter's phone");
		
		headerList.add("Issue submission date");
		
		

		headerList.add("Current owner");

		headerList.add("Severity");

		headerList.add("Status");
		
		headerList.add("Description");
		
		headerList.add("Comments");		
		
		
		
		EtsIssueAddFieldsDAO addFieldsDAO = new EtsIssueAddFieldsDAO();
		
		EtsIssueAddFieldsBean [] addFieldsBean =  addFieldsDAO.getAllCustFields(projectId);
		ArrayList custFieldId = new ArrayList();
		for(int i=0; i<addFieldsBean.length; i++) {
			EtsIssueAddFieldsBean addFieldBean = addFieldsBean[i];
			headerList.add(addFieldBean.getFieldLabel());
			custFieldId.add(i, new Integer( addFieldBean.getFieldId() ) );		
		}
		
		//add headers to download list
		downLoadList.add(headerList);
		EtsDownloadAllIssuesDAO downloadAllIssuesDAO = new EtsDownloadAllIssuesDAO(issfilterkey);
		
		ArrayList repDataList = downloadAllIssuesDAO.getReportTabListWithPstmt(projectId);
		
		int repsize = repDataList.size();
		

		for (int r = 0; r < repsize; r++) {

			ArrayList tempList = new ArrayList();

			Hashtable etsreptab = (Hashtable) repDataList.get(r);
			                     
			String issueTitle = etsreptab.get("ISSUETITLE").toString();
			String issueType = etsreptab.get("ISSUETYPE").toString();
			String issueSeverity = etsreptab.get("SEVERITY").toString();
			String issueStatus = etsreptab.get("STATUS").toString();
			String issueSubmitterName = etsreptab.get("SUBMITTER").toString();
			
			
			String issueSubmittersCompany = etsreptab.get("SUBMITTERSCOMPANY").toString();
			String issueSubmittersEmail = etsreptab.get("SUBMITTEREMAIL").toString();
			String issueSubmittersPhone = etsreptab.get("SUBMITTERPHONE").toString();
			String issueSubmissionDate = etsreptab.get("ISSUESUBMISSIONDATE").toString();
			
			
			String issueCurOwnerName = etsreptab.get("OWNERNAME").toString();
			String refId = etsreptab.get("ID").toString();
			String description = etsreptab.get("DESCRIPTION").toString();
			String comments = etsreptab.get("COMMENTS").toString();
			
						
			tempList.add(refId);
			tempList.add(issueTitle);
			tempList.add(issueType);
			tempList.add(issueSubmitterName);
			
			tempList.add(issueSubmittersCompany);
			tempList.add(issueSubmittersEmail);
			tempList.add(issueSubmittersPhone);
			tempList.add(issueSubmissionDate);
			
			tempList.add(issueCurOwnerName);
			tempList.add(issueSeverity);
			tempList.add(issueStatus);
			tempList.add(description);
			tempList.add(comments);
			
			for(int x=0; x<custFieldId.size(); x++ ) {
				
				tempList.add(etsreptab.get( custFieldId.get(x).toString() ).toString() );
				
			}
			
			//add tempList to downLoadList
			downLoadList.add(tempList);		
		}
		
		return downLoadList;
		
	}
	
	
	public String getDescCommQryStrWithPstmt() {
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		sb.append(" u.problem_desc as probdesc, ");
		sb.append(" u.comm_from_cust as custcomm ");
		sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u ");
		sb.append(" where");
		sb.append(" u.application_id='" + ETSAPPLNID + "' ");	
		sb.append(" and u.edge_problem_id =? ");
		sb.append(" with ur");
		
		return sb.toString();
	}
	
	public String getFinalQryStrWithPstmt() {

		StringBuffer sb = new StringBuffer();

		////new -------for brand new issues, for which cq_trk_id has not been assigned-------///

			sb.append("select ");
			sb.append(" 'NEWISSUE' as issuezone, ");
			sb.append(" u.edge_problem_id as edgeproblemid, ");
			sb.append(" u.cq_trk_id as cqtrkid, ");
			sb.append(" u.problem_state as problemstate, ");
			sb.append(" u.problem_creator as problemcreator, ");

			
			sb.append(" u.cust_company as submitterscompany, ");
			
			sb.append(" u.cust_email as submitteremail, ");
			
			sb.append(" u.cust_phone as submitterphone, ");
			
			sb.append(" u.creation_date as issuesubmissiondate, ");
			
			
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
			sb.append(" u.issue_source as issuesource, ");
			sb.append(" u.field_c1, u.field_c2, u.field_c3,u.field_c4,u.field_c5,u.field_c6,u.field_c7,u.field_c8, ");
			sb.append(" u.cq_trk_id as refid ");
			sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u ");
			sb.append(" where");
			sb.append(" u.application_id='" + ETSAPPLNID + "' ");
			//sb.append(" and u.problem_class IN " + dynInClsProbTypeStr + " ");
			sb.append(" and u.problem_class IN ('Defect','Question') ");
			
			
			//sb.append(" and u.cust_project =(select project_name from ets.ets_projects where project_id= ? ) ");
			sb.append(" and u.ets_project_id =? ");//v2sagar
			
			
			sb.append(" and u.edge_problem_id NOT IN  (select distinct EDGE_PROBLEM_ID from "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 where APPLICATION_ID = '" + ETSAPPLNID + "' ) ");


			sb.append(" group by u.severity,u.problem_state,u.problem_creator, u.cust_company, u.cust_email, u.cust_phone, u.creation_date, u.edge_problem_id,u.cq_trk_id,u.title,u.problem_class,u.issue_type_id,u.last_timestamp,u.issue_source, u.field_c1, u.field_c2, u.field_c3,u.field_c4,u.field_c5,u.field_c6,u.field_c7,u.field_c8 ");

			sb.append(" union");

	//	} //only if state is not issues assigned to me

		////-------for existing issues, for which cq_trk_id has been assigned and in work flow, and waiting for user resp("+ISMGTSCHEMA+".seq_no > usrseq_no-------////

		sb.append(" select ");
		sb.append(" 'CQISSUE' as issuezone, ");
		sb.append(" c.edge_problem_id as edgeproblemid, ");
		sb.append(" c.cq_trk_id as cqtrkid, ");
		sb.append(" c.problem_state as problemstate, ");
		sb.append(" u.problem_creator as problemcreator, ");

		
		
		sb.append(" u.cust_company as submitterscompany, ");
		
		sb.append(" u.cust_email as submitteremail, ");
		
		sb.append(" u.cust_phone as submitterphone, ");
		
		sb.append(" u.creation_date as issuesubmissiondate, ");
				
		
		
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
		sb.append(" c.issue_source as issuesource, c.field_c1, c.field_c2, c.field_c3, c.field_c4, c.field_c5, c.field_c6, c.field_c7, c.field_c8, ");
		sb.append(" c.cq_trk_id as refid ");
		sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u, "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 c, "+ISMGTSCHEMA+".ETS_OWNER_CQ o ");
		sb.append(" where ");
		sb.append(" c.application_id='" + ETSAPPLNID + "' ");
		
		
		//sb.append(" and c.problem_class IN " + dynInClsProbTypeStr + " ");
		sb.append(" and c.problem_class IN ('Defect','Question') ");		
		
		//sb.append(" and u.cust_project =(select project_name from ets.ets_projects where project_id= ? ) ");
		sb.append(" and u.ets_project_id =? ");//v2sagar
		
		
		sb.append(" and c.application_id=u.application_id ");
		sb.append(" and c.edge_problem_id=u.edge_problem_id ");
		sb.append(" and u.seq_no < c.seq_no ");
		sb.append(" and c.edge_problem_id=o.edge_problem_id ");

		sb.append(" group by c.severity,c.problem_state,u.problem_creator, u.cust_company, u.cust_email, u.cust_phone, u.creation_date, o.owner_id,o.owner_email,c.edge_problem_id,c.cq_trk_id,c.title,c.problem_class,u.issue_type_id,c.last_timestamp,c.issue_source, c.field_c1, c.field_c2, c.field_c3,c.field_c4,c.field_c5,c.field_c6,c.field_c7,c.field_c8  ");

		//show In Process issues only when the state is 'All' or 'In Process'

			sb.append(" union");

			////-------for existing issues, for which cq_trk_id has been assigned and in work flow, and waiting for cq resp(usr.seq_no > "+ISMGTSCHEMA+".seq_no-------////

			sb.append(" select ");
			sb.append(" 'USRISSUE' as issuezone, ");
			sb.append(" u.edge_problem_id as edgeproblemid, ");
			sb.append(" u.cq_trk_id as cqtrkid, ");
			//sb.append(" c.problem_state as problemstate, "); //for clarity btwn action/state
			sb.append(" 'In Process' as problemstate, "); //for clarity btwn action/state
			sb.append(" u.problem_creator as problemcreator, ");
			
			
			sb.append(" u.cust_company as submitterscompany, ");
			
			sb.append(" u.cust_email as submitteremail, ");
			
			sb.append(" u.cust_phone as submitterphone, ");
			
			sb.append(" u.creation_date as issuesubmissiondate, ");
			
			
			
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
			sb.append(" u.ISSUE_SOURCE as issuesource, u.field_c1, u.field_c2, u.field_c3, u.field_c4, u.field_c5, u.field_c6, u.field_c7, u.field_c8, ");
			sb.append(" u.cq_trk_id as refid ");
			sb.append(" from "+ISMGTSCHEMA+".PROBLEM_INFO_USR1 u,  "+ISMGTSCHEMA+".PROBLEM_INFO_CQ1 c, "+ISMGTSCHEMA+".ETS_OWNER_CQ o ");
			sb.append(" where ");
			sb.append(" u.application_id='" + ETSAPPLNID + "' ");
			
			
			//sb.append(" and u.problem_class IN " + dynInClsProbTypeStr + " ");
			sb.append(" and u.problem_class IN ('Defect','Question') ");
			
			
			//sb.append(" and u.cust_project =(select project_name from ets.ets_projects where project_id= ? ) ");
			sb.append(" and u.ets_project_id =? ");//v2sagar
			
			
			sb.append(" and u.application_id=c.application_id ");
			sb.append(" and u.edge_problem_id=c.edge_problem_id ");
			sb.append(" and c.seq_no < u.seq_no ");
			sb.append(" and c.edge_problem_id=o.edge_problem_id ");


			sb.append(" group by u.severity,c.problem_state,u.problem_creator, u.cust_company, u.cust_email, u.cust_phone, u.creation_date, o.owner_id,o.owner_email, u.edge_problem_id,u.cq_trk_id,u.title,u.problem_class,u.issue_type_id,u.last_timestamp,u.issue_source, ");
			sb.append(" u.issue_source, u.field_c1, u.field_c2, u.field_c3,u.field_c4,u.field_c5,u.field_c6,u.field_c7,u.field_c8 ");

			sb.append(" ORDER BY refid ASC ");			
			
		 //include In Process only when it is 'All' or 'In Process'

		sb.append(" with ur");

		////end/////
		return sb.toString();

	}
	
	
	
	public String getUniqCsvName() {

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

		uniqCsvName = dervdProjName + "WORK_WITH_ALL_ISSUES.csv";

		Global.println("DERIVED CSV FILE NAME===" + uniqCsvName);

		return uniqCsvName;
	}
	

}
