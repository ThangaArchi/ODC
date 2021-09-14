//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\ActionStep_Notify.java

package oem.edge.ets.fe.ismgt.middleware;

import java.io.StringWriter;
import java.util.Vector;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;



/**
 * @author jetendra
 * @decription This class encapsulates the beahviour of the notification action.
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 *
 *	BELOW IS THE FORMAT OF THE EMAIL GENERATED BY THIS CLASS
 *

Hello ,
You are receiving this message because you or a team member has submitted a new issue on E&TS Connect. An owner has been assigned to address the issue.
This e-mail provides details about the issue and describes the actions that you can take.

The details of the issue are as follows:

======================= D E T A I L S ========================
ID:            ETS00000369
Project:       Indy
Title:         IDAC from Dragon chip not appropriate
Severity:      2-Major
Description:   The IDAC from the Dragon chip, named ICOBANCAL_D11,
is a 5 bit IDAC, with 7 bits
of calibration.  It does not appear to be
able to meet the 7 bit IDAC specifications
that have been posted without major
modification

======================= A C T I O N S ========================
You can perform the following actions on this issue:

Team member actions:
Comment:       Add comments to the commentary log.

Submitter actions:
Modify:        Modify the issue attributes.
Comment:       Add comments to the commentary log.
Withdraw:      Withdraw the issue.



To perform an action or check the status of this issue, click the following URL:
https://www-306.ibm.com/servlet/oem/edge/ets/ETSProjectsServlet.wss?linkid=251000&proj=1115133202118&tc=2201&sc=0&actionType=viewIssue&edge_problem_id=keford@u-1117130128358

===============================================================
Delivered by E&TS Connect.
This is a system generated email.
===============================================================


 *
 *
 */



public class ActionStep_Notify implements ActionStep {
	public static final String VERSION = "1.5.1.1";
	
	private ETS_Workflow workflow = null;
	/**
	 * @roseuid 42753BE50158
	 */
	public ActionStep_Notify() {

	}

	public void setWorkflow(Workflow _workflow) {
		workflow = (ETS_Workflow) _workflow;

	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.middleware.ActionStep#executeActionStep(oem.edge.ets.fe.ismgt.model.ETSIssue, oem.edge.ets.fe.ismgt.model.EtsIssObjectKey)
	 */
	public boolean executeActionStep(ETSIssue usr1issue, EtsIssObjectKey issobjkey) {
		
		boolean success = false;
		ETSMWIssue currentRecord = (ETSMWIssue) usr1issue;
		
		ETSProj proj = issobjkey.getProj();
		UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

		//String	ETS_FromEmail = prop.getAdminEmailID();
		
		// Modified by Prasad
		// Changed to use submiter email id to send mails..
		String ETS_FromEmail = currentRecord.cust_email;
		
		if (!Global.loaded)
			Global.Init();
			
				
		try {
			
			String filteredNotifyListStr=usr1issue.ets_cclist;		
			String subscribeListStr=ETSMW_IssueDAO.getSubscriberEmailList(currentRecord.issueTypeId);
			//filter notify list
			filteredNotifyListStr=EmailRuleFilter.filterNotifyListStr(usr1issue.cust_email,geteOwnerEmailList(currentRecord),usr1issue.ets_cclist);					
			//filter subscribe list
			subscribeListStr=EmailRuleFilter.filterSubscriptionList(filteredNotifyListStr,subscribeListStr,usr1issue.cust_email,geteOwnerEmailList(currentRecord));
			
			// if the workspace is not of type SUPPORT?BLADE
			if( ! usr1issue.etsIssuesType.equalsIgnoreCase("SUPPORT") ){
				 //	Email to submitter
				if (!StringUtil.isNullorEmpty(usr1issue.cust_email))
				 ETSUtils.sendEMail(ETS_FromEmail, usr1issue.cust_email,"","", Global.mailHost, generateEmailContent(currentRecord, "ISSUE_SUBMITTER", issobjkey), generateEmailSubject(currentRecord, false, currentRecord.problem_state), null);
				 // Email to Copy list
				 if (!StringUtil.isNullorEmpty(filteredNotifyListStr))
				 	ETSUtils.sendEMail(ETS_FromEmail, filteredNotifyListStr, "","", Global.mailHost, generateEmailContent(currentRecord, "ISSUE_ONCOPY", issobjkey), generateEmailSubject(currentRecord, false, currentRecord.problem_state), null);
				 // Email to owner
				 if (!StringUtil.isNullorEmpty(geteOwnerEmailList(currentRecord)))
				 	ETSUtils.sendEMail(ETS_FromEmail, geteOwnerEmailList(currentRecord),"","", Global.mailHost, generateEmailContent(currentRecord, "ISSUE_OWNER", issobjkey), generateEmailSubject(currentRecord, true, currentRecord.problem_state), null);
				 if (!StringUtil.isNullorEmpty(subscribeListStr))
				 	ETSUtils.sendEMail(ETS_FromEmail, subscribeListStr,"","", Global.mailHost, generateEmailContent(currentRecord, "ISSUE_SUBSCRIBER", issobjkey), generateEmailSubject(currentRecord, false, currentRecord.problem_state), null);
				 }
			else {
				
			
				// Email to submitter
				// Incase of blade we do ot want the external customer to get to know about the Changeowner
				// which is more of an internal action
				// Note ; All emails are sent as bcc
				if(!  usr1issue.problem_state.equalsIgnoreCase("changeowner")){
					if (!StringUtil.isNullorEmpty(usr1issue.cust_email))
						ETSUtils.sendEMail(ETS_FromEmail, usr1issue.cust_email, "","", Global.mailHost, generateEmailContent(currentRecord, "ISSUE_SUBMITTER", issobjkey), generateEmailSubject(currentRecord, false, currentRecord.problem_state), null);
					// Email to Copy list
					if (!StringUtil.isNullorEmpty(filteredNotifyListStr))
						ETSUtils.sendEMail(ETS_FromEmail, "","",filteredNotifyListStr.concat(","), Global.mailHost, generateEmailContent(currentRecord, "ISSUE_ONCOPY", issobjkey), generateEmailSubject(currentRecord, false, currentRecord.problem_state), null);
				}
			// Email to owner
				if (!StringUtil.isNullorEmpty(geteOwnerEmailList(currentRecord)))
					ETSUtils.sendEMail(ETS_FromEmail, geteOwnerEmailList(currentRecord),"", "", Global.mailHost, generateEmailContent(currentRecord, "ISSUE_OWNER", issobjkey), generateEmailSubject(currentRecord, true, currentRecord.problem_state), null);
				if (!StringUtil.isNullorEmpty(subscribeListStr))
					ETSUtils.sendEMail(ETS_FromEmail, "","",subscribeListStr.concat(","), Global.mailHost, generateEmailContent(currentRecord, "ISSUE_SUBSCRIBER", issobjkey), generateEmailSubject(currentRecord, false, currentRecord.problem_state), null);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}
	
	
//	This method is used to generate the content of the email as described above in the template
	private String generateEmailContent(ETSMWIssue currentRecord, String role, EtsIssObjectKey issobjkey) {

		StringBuffer sEmailStr = new StringBuffer();
		String ETS_ISSUES_TYPE = "";
		if (currentRecord.etsIssuesType != null)
			ETS_ISSUES_TYPE = currentRecord.etsIssuesType;
		
		String action = currentRecord.problem_state;
		// role determination based on role on issue
		boolean isOwner = false;
		if (role.equalsIgnoreCase("ISSUE_OWNER"))
			isOwner = true;
		boolean isOnCopy = false;
		if (role.equalsIgnoreCase("ISSUE_ONCOPY"))
			isOnCopy = true;
		boolean isSubscriber = false;
		if (role.equalsIgnoreCase("ISSUE_SUBSCRIBER"))
			isSubscriber = true;

		generateOpeningStatement(currentRecord, role, issobjkey, sEmailStr);
		generateIssueDetails(currentRecord, role, issobjkey, sEmailStr);

		String nextAction = generateNextAction(sEmailStr, issobjkey, role, currentRecord);

		if (nextAction == null)
			createLink(currentRecord, sEmailStr, "view", issobjkey);
		else {
			createLink(currentRecord, sEmailStr, nextAction, issobjkey);			
			createLink(currentRecord, sEmailStr, "view", issobjkey);
		}
		generateEmailReason(currentRecord,role,issobjkey,sEmailStr);
		//generateEmailSignature(currentRecord, role, issobjkey, sEmailStr);
		//v2sagar
		ETSProj proj = issobjkey.getProj();
		UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());
		sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));

		return sEmailStr.toString();

	}
	// This creates the opening statement of the email starting with "Hello"
	private void generateOpeningStatement(ETSMWIssue currentRecord, String role, EtsIssObjectKey issobjkey, StringBuffer sEmailStr) {
		String action = currentRecord.problem_state;
		
		ETSProj proj = issobjkey.getProj();
		UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());
		
		boolean isOwner = false;
		if (role.equalsIgnoreCase("ISSUE_OWNER"))
			isOwner = true;
		boolean isOnCopy = false;
		if (role.equalsIgnoreCase("ISSUE_ONCOPY"))
			isOnCopy = true;
		boolean isSubscriber = false;
		if (role.equalsIgnoreCase("ISSUE_SUBSCRIBER"))
			isSubscriber = true;
		String issueclass = "issue";
		if (currentRecord.problem_class.equals("Change"))
			issueclass = "change request";
		if (currentRecord.problem_class.equals("Feedback"))
			issueclass = "Feedback";
		sEmailStr.append("Hello ,\n");
		//changed the content v2sagar
		if (action.equals("New") || action.equals("Create") || action.equals("Submit")) {
			if (isOwner)
				sEmailStr.append("An "+ prop.getAppName() +" team member submitted a new " + issueclass + ", and it has been assigned to you. " + " \n");
			else if(isSubscriber)
				sEmailStr.append("You are receiving this email because an issue has been submitted against one of the issuetypes to which you have subscribed to be notified" +".\n");
			else if(isOnCopy)
				sEmailStr.append("You are receiving this message because a team member submitted a new issue on " + prop.getAppName() + " and you are on its notification list. An owner has been assigned to address the " + issueclass + ".\n");
			else // isSubmitter
				sEmailStr.append("You are receiving this message because you or a team member submitted a new issue on " + prop.getAppName() + ". An owner has been assigned to address the " + issueclass + ".\n");
			
			sEmailStr.append("This e-mail provides details about the " + issueclass + " and describes the actions that you can take. " + "\n\n");
			

		} else if (action.equals("Modify")) {
			if (isOwner)
				sEmailStr.append("The " + issueclass + " " + currentRecord.cq_trk_id + " assigned to you has been modified." + " \n\n");
			else
				sEmailStr.append("The " + issueclass + " was modified successfully on " + prop.getAppName() + " and the owner has been notified. \n\n");
		} else if (action.equals("Withdraw")) {
			if (isOwner)
				sEmailStr.append("The " + issueclass + " " + currentRecord.cq_trk_id + " assigned to you has been withdrawn." + " \n\n");
			else
				sEmailStr.append("The " + issueclass + " was withdrawn successfully on " + prop.getAppName() + " and the owner has been notified. \n\n");
		} else if (action.equals("Accept")) {
			if (isOwner)
				sEmailStr.append("The " + issueclass + " " + currentRecord.cq_trk_id + " has been accepted by you." + " \n\n");
			else
				sEmailStr.append("The " + issueclass + " has been accepted. \n\n");
		} else if (action.equals("Reject")) {
			if (issueclass.equals("change request")) {
				if (isOwner)
					sEmailStr.append("The " + issueclass + " " + currentRecord.cq_trk_id + " has been rejected by you." + " \n\n");
				else
					sEmailStr.append("The " + issueclass + " has been rejected. \n\n");
			} else {
				if (isOwner)
					sEmailStr.append("The resolution to the " + issueclass + " " + currentRecord.cq_trk_id + " has been rejected." + " \n\n");
				else
					sEmailStr.append("The resolution to the " + issueclass + " has been rejected. \n\n");
			}
		} else if (action.equals("Resolve")) {
			if (isOwner)
				sEmailStr.append("The " + issueclass + " " + currentRecord.cq_trk_id + " has been resolved by you." + " \n\n");
			else
				sEmailStr.append("The " + issueclass + " has been resolved. \n\n");
			//	content modified for comment v2sagar
		} else if (action.equals("Comment")) {			
			sEmailStr.append("A comment has been added to the following " + issueclass + ". \n\n");
		} else if (action.equals("Close")) {
			sEmailStr.append("The " + issueclass + " has been closed. \n\n");
		} else if (action.equals("Changeowner")) {
			if (isOwner)
				sEmailStr.append("The " + issueclass + " has been reassigned to you in response to a changeowner action. \n\n");
			else
				sEmailStr.append("The " + issueclass + " has been reassigned to a new owner in response to a changeowner action. \n\n");
		}
		sEmailStr.append("The details of the " + issueclass + " are as follows: \n\n");

	}
	
//	This creates the opening statement of the email starting with "Hello"
	 private void generateEmailReason(ETSMWIssue currentRecord, String role, EtsIssObjectKey issobjkey, StringBuffer sEmailStr) {
		 String action = currentRecord.problem_state;
		 boolean isOwner = false;
		 if (role.equalsIgnoreCase("ISSUE_OWNER"))
			 isOwner = true;
		 boolean isOnCopy = false;
		 if (role.equalsIgnoreCase("ISSUE_ONCOPY"))
			 isOnCopy = true;
		 boolean isSubscriber = false;
		 if (role.equalsIgnoreCase("ISSUE_SUBSCRIBER"))
			 isSubscriber = true;
		 String issueclass = "issue";
		 if (currentRecord.problem_class.equals("Change"))
			 issueclass = "change request";
		 if (currentRecord.problem_class.equals("Feedback"))
			 issueclass = "Feedback";
		 

		 
			 if(isSubscriber){
				 sEmailStr.append("\nYou are receiving this email because you have subscribed to be notified for any updates on issues belonging to this issuetype.\nTo unsubscribe click on the link below" +".\n");
				 createUnSubscribeLink(currentRecord,sEmailStr,"subsIssType",issobjkey);
			 }else if (isOnCopy){
			 	sEmailStr.append("\nYou are receiving this email because you are on the notification list of this issue.\nTo unsubscribe click on the link below" +".\n");
				createUnSubscribeLink(currentRecord,sEmailStr,"unSubscrIssue",issobjkey);
				
			 }		
		
	 }

// Thid method generates the issue details that need to be incorporated in the email
	private void generateIssueDetails(ETSMWIssue currentRecord, String role, EtsIssObjectKey issobjkey, StringBuffer sEmailStr) {
		String action = currentRecord.problem_state;
		String ETS_ISSUES_TYPE = "";
		if (currentRecord.etsIssuesType != null)
			ETS_ISSUES_TYPE = currentRecord.etsIssuesType;
		boolean isOwner = false;
		if (role.equalsIgnoreCase("ISSUE_OWNER"))
			isOwner = true;
		boolean isOnCopy = false;
		if (role.equalsIgnoreCase("ISSUE_ONCOPY"))
			isOnCopy = true;
		boolean isSubscriber = false;
		if (role.equalsIgnoreCase("ISSUE_SUBSCRIBER"))
			isSubscriber = true;
		String issueclass = "issue";
		if (currentRecord.problem_class.equals("Change"))
			issueclass = "change request";
		if (currentRecord.problem_class.equals("Feedback"))
			issueclass = "Feedback";

		sEmailStr.append("======================= D E T A I L S ========================\n");

		String project = null;
		String problem_desc = null;
		String actionBy = null;
		String comment = null;

		if (currentRecord.issue_source.equals("CQROC")) {
			project = issobjkey.getProj().getName();
			problem_desc = currentRecord.problem_desc;
			actionBy = currentRecord.field_C14 + " " + currentRecord.field_C15;
			comment = currentRecord.comm_from_cust;
		} else {

			project = currentRecord.cust_project;
			problem_desc = currentRecord.problem_desc;
			actionBy = currentRecord.field_C14 + " " + currentRecord.field_C15;
			comment = currentRecord.comm_from_cust;
		}

		try {
			sEmailStr.append("  ID:            " + ETSUtils.formatEmailStr(currentRecord.cq_trk_id) + "\n");
			sEmailStr.append("  Project:       " + ETSUtils.formatEmailStr(project) + "\n");
			sEmailStr.append("  Title:         " + ETSUtils.formatEmailStr(currentRecord.title) + "\n");
			sEmailStr.append("  Severity:      " + ETSUtils.formatEmailStr(currentRecord.severity) + "\n");

			if (action.equalsIgnoreCase("New") || action.equalsIgnoreCase("Submit")) {
				if (isOwner)
					if (!ETS_ISSUES_TYPE.equals("SUPPORT")) // CHANGE FOR BLADE TYPE PROJECT
						sEmailStr.append("  Submitted by:  " + currentRecord.cust_name + " \n");
				//below line is commented by v2sagar to remove description in the emails for security concerns
				//sEmailStr.append("  Description:   " + ETSUtils.formatEmailStr(problem_desc) + " \n");
			} else {
				if (!ETS_ISSUES_TYPE.equals("SUPPORT")) // Dont send action by for Bladetype project
					sEmailStr.append("  Action by:     " + ETSUtils.formatEmailStr(actionBy) + " \n");
				//below line is commented by v2sagar to remove comments in the emails for security concerns
				//sEmailStr.append("  Comment:       " + ETSUtils.formatEmailStr(comment) + " \n");
			}

		} catch (Exception e) {
		}

	}

// This method creates the action link to access the issue directly from the email
	private String createLink(ETSMWIssue currentRecord, StringBuffer sEmailStr, String action, EtsIssObjectKey issobjkey) {

		ETSProj proj = issobjkey.getProj();
		UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

		String issueclass = "issue";
		String urlString = null;
		if (action.equals("view"))
			sEmailStr.append("\nTo perform an action or check the status of this " + issueclass + ", click the following URL:" + " \n");
		else
			sEmailStr.append("\nTypically, you would perform the '" + action + "' action on the issue. To do this, click the following URL:\n");

		// new code done by sathish
		// The tc in the url was hardcoded which will not work for all projects.
		// have changed it to pick up from field_c12.

		String sTC = String.valueOf(issobjkey.getTopCatId());

		if (!Global.loaded)
			Global.Init();

		if (currentRecord.issue_source.equals("ETSOLD")) {
			if (issueclass.equals("change request")) {
				urlString = (String) Global.getUrl("ets") + "/ETSProjectsServlet.wss" + "?linkid=" + prop.getLinkID() + "&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC.trim() + "&sc=0&actionType=viewChange&edge_problem_id=" + currentRecord.edge_problem_id;
			} else {
				String URL = (String) Global.getUrl("ets");
				urlString = (String) Global.getUrl("ets") + "/ETSProjectsServlet.wss" + "?linkid=" + prop.getLinkID() + "&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC.trim() + "&sc=0&actionType=" + action.toLowerCase() + "Issue&edge_problem_id=" + currentRecord.edge_problem_id;
			}
		} else {
			if (issueclass.equals("change request")) {
				String URL = (String) Global.getUrl("ets");
				urlString = (String) Global.getUrl("ets") + "/ETSProjectsServlet.wss" + "?linkid=" + prop.getLinkID() + "&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC.trim() + "&sc=0&actionType=viewChange&edge_problem_id=" + currentRecord.edge_problem_id;
			} else {
				urlString = (String) Global.getUrl("ets") + "/ETSProjectsServlet.wss" + "?linkid=" + prop.getLinkID() + "&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC.trim() + "&sc=0&actionType=" + action.toLowerCase() + "Issue&edge_problem_id=" + currentRecord.edge_problem_id;
			}
		}

		sEmailStr.append(urlString);
		sEmailStr.append("\n");

		return urlString;

	}
	
//	This method creates the action link to access the issue directly from the email
	 private String createUnSubscribeLink(ETSMWIssue currentRecord, StringBuffer sEmailStr, String action, EtsIssObjectKey issobjkey) {

		 ETSProj proj = issobjkey.getProj();
		 UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

		 String issueclass = "issue";
		 String urlString = null;
		 
		 // new code done by sathish
		 // The tc in the url was hardcoded which will not work for all projects.
		 // have changed it to pick up from field_c12.

		 String sTC = String.valueOf(issobjkey.getTopCatId());

		 if (!Global.loaded)
			 Global.Init();

		 if (action.equalsIgnoreCase("unSubscrIssue")) {
			 if (issueclass.equals("change request")) {
				 urlString = (String) Global.getUrl("ets") + "/ETSProjectsServlet.wss" + "?linkid=" + prop.getLinkID() + "&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC.trim() + "&sc=0&actionType=viewChange&edge_problem_id=" + currentRecord.edge_problem_id + "&op=1300";
			 } else {
				 urlString = (String) Global.getUrl("ets") + "/ETSProjectsServlet.wss" + "?linkid=" + prop.getLinkID() + "&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC.trim() + "&sc=0&actionType=" + action + "&edge_problem_id=" + currentRecord.edge_problem_id + "&op=1300";
			 }
		 } else {
			 if (action.equalsIgnoreCase("subsIssType")) {
				 String URL = (String) Global.getUrl("ets");
				 urlString = (String) Global.getUrl("ets") + "/subsIssType.wss" + "?linkid=" + prop.getLinkID() + "&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC.trim() + "&sc=0&actionType=" + action + "&istyp=iss&opn=1200";
			 } else {
				 urlString = (String) Global.getUrl("ets") + "/subsIssType.wss" + "?linkid=" + prop.getLinkID() + "&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC.trim() + "&sc=0&actionType=" + action + "&istyp=iss&opn=1200";
			 }
		 }

		 sEmailStr.append(urlString);
		 sEmailStr.append("\n\n");

		 return urlString;

	 }
// This method generates the Actions sections of the email depending on user role and the allowable
// actions in the Workflow
	private String generateNextAction(StringBuffer sEmailStr, EtsIssObjectKey issobjkey, String role, ETSMWIssue currentIssue) {

		String issueclass = "issue";
		if (role.equalsIgnoreCase("ISSUE_ONCOPY") || role.equalsIgnoreCase("ISSUE_SUBSCRIBER"))
			role = Defines.WORKSPACE_MEMBER;
		String nextaction = workflow.generateTypicalNextAction(currentIssue.nextState, role);
		ActionMatrix currentActions = new ActionMatrix();
		currentActions = currentActions.Add(ActionStateRoleModel.getActionMatrixforRole(role));
		currentActions = currentActions.Filter(ActionStateRoleModel.getActionMatrixforState(currentIssue.nextState));
		sEmailStr.append("\n======================= A C T I O N S ========================\n");
		sEmailStr.append("You can perform the following actions on this issue:\n\n");

		try {
			if (currentActions.Modify)
				sEmailStr.append("  Modify:        " + ETSUtils.formatEmailStr("Modify the issue attributes.") + "\n");
			if (currentActions.Comment)
				sEmailStr.append("  Comment:       " + ETSUtils.formatEmailStr("Add comments to the commentary log.") + "\n");
			if (currentActions.Resolve)
				sEmailStr.append("  Resolve:       " + ETSUtils.formatEmailStr("Submit a resolution for this issue.") + "\n");
			if (currentActions.Changeowner)
				sEmailStr.append("  Changeowner:   " + ETSUtils.formatEmailStr("Reassign the issue to a different owner.") + "\n");

			if (currentActions.Withdraw)
				sEmailStr.append("  Withdraw:      " + ETSUtils.formatEmailStr("Withdraw the issue.") + "\n");
			if (currentActions.Close)
				sEmailStr.append("  Close:         " + ETSUtils.formatEmailStr("Close the issue, accepting the resolution.") + "\n");
			if (currentActions.Reject)
				sEmailStr.append("  Reject:        " + ETSUtils.formatEmailStr("Reject the resolution.") + "\n");

		} catch (Exception e) {
		}
		return nextaction;

	}
// This method generates the subject line of the email
	public String generateEmailSubject(ETSMWIssue currentRecord, boolean isOwner, String action) {

		StringBuffer sEmailSubjectbuf = new StringBuffer();

		sEmailSubjectbuf.append("");

		if (currentRecord.problem_class.equals("Change"))
			sEmailSubjectbuf.append(" Change request ");
		else
			sEmailSubjectbuf.append(" Issue ");

		String title = currentRecord.title;
		if (title.length() > 20)
			title = title.substring(0, 19) + "...";
		sEmailSubjectbuf.append("'" + title + "'");

		sEmailSubjectbuf.append(" has been ");

		if (action.equals("Submit")) {
			if (isOwner)
				sEmailSubjectbuf.append("assigned.");
			else
				sEmailSubjectbuf.append("submitted.");
		} else if (action.equals("Modify")) {
			sEmailSubjectbuf.append("modified.");
		} else if (action.equals("Withdraw")) {
			sEmailSubjectbuf.append("withdrawn.");
		} else if (action.equals("Accept")) {
			sEmailSubjectbuf.append("accepted.");
		} else if (action.equals("Reject")) {
			sEmailSubjectbuf.append("rejected.");
		} else if (action.equals("Resolve")) {
			sEmailSubjectbuf.append("resolved.");
		} else if (action.equals("Close")) {
			sEmailSubjectbuf.append("closed.");
		} else if (action.equals("Comment")) {
			sEmailSubjectbuf.append("commented on.");
		} else if (action.equals("Changeowner")) {
			sEmailSubjectbuf.append("reassigned.");
		}

		return sEmailSubjectbuf.toString();

	}
// This method gets the list of owner email addresses to be notified
	public String geteOwnerEmailList(ETSMWIssue currentRecord) {

		StringWriter emaillist = new StringWriter();
		Vector owners = currentRecord.ownerRecords;
		

		if (owners == null)
			try {
				owners = ETSMW_IssueDAO.getOwnerRecord(currentRecord.edge_problem_id);
			} catch (Exception e) {
				e.printStackTrace();
			}

		for (int j = 0; j < owners.size(); j++) {
			ETSMWOwnerRecord cqrec = (ETSMWOwnerRecord) owners.elementAt(j);
			if (j != 0)
				emaillist.write(",");
			emaillist.write(cqrec.getOwner_email());
			
			if(AmtCommonUtils.isResourceDefined(cqrec.getBackupOwner_email())) {
				emaillist.write(",");
				emaillist.write(cqrec.getBackupOwner_email());
			}
				
		}
		// For change owner actio only
		if(currentRecord.ownerInfo != null) {
		
			emaillist.write(",");
			emaillist.write(currentRecord.ownerInfo.getUserEmail());
			
		}
			
			
		return emaillist.getBuffer().toString();

	}
}
