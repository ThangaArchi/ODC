/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import oem.edge.common.Global;
import oem.edge.ets.fe.documents.NotificationMsgHelper;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.CharUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;

//
/**
 * Class       : Notifier
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 15, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class Notifier implements NotificationSender, NotificationConstants{

	private static Log logger = WorkflowLogger.getLogger(Notifier.class);
	
	protected final int TEMPLATE_NEW_ISSUE__OWNER = 1;
	protected final int TEMPLATE_NEW_ISSUE__CONTACT = 2;
	protected final int TEMPLATE_NEW_ISSUE__GENERAL = 3;
	protected final int TEMPLATE_ISSUE_ACCEPTED__CONTACT = 4;
	protected final int TEMPLATE_ISSUE_ACCEPTED__OWNER = 5;
	protected final int TEMPLATE_ISSUE_REJECTED__CONTACT = 6;
	protected final int TEMPLATE_ISSUE_REJECTED__OWNER = 7;
	protected final int TEMPLATE_ISSUE_REASSIGNED__OWNER = 8;
	protected final int TEMPLATE_ISSUE_REASSIGNED__OLDOWNER = 9;
	protected final int TEMPLATE_ISSUE_EDITED = 10;
	protected final int TEMPLATE_OWNER_ACCEPTS__CONTACT = 11;
	protected final int TEMPLATE_OWNER_REJECTS__CONTACT = 12;
	
	protected final int TEMPLATE_NEW_ISSUE__ACCOUNT_CONTACT = TEMPLATE_NEW_ISSUE__GENERAL;
	protected final int TEMPLATE_ISSUE_ACCEPTED__ACCOUNT_CONTACT = 14;
	protected final int TEMPLATE_ISSUE_REJECTED__ACCOUNT_CONTACT = 15;
	protected final int TEMPLATE_OWNER_ACCEPTS__ACCOUNT_CONTACT = 16;
	protected final int TEMPLATE_OWNER_REJECTS__ACCOUNT_CONTACT = 17;
	
	protected final int TEMPLATE_ISSUE_ACCEPTED__CONTACT_DAEMON = 101;
	protected final int TEMPLATE_ISSUE_ACCEPTED__OWNER_DAEMON = 102;
	protected final int TEMPLATE_OWNER_ACCEPTS__CONTACT_DAEMON = 103;
	protected final int TEMPLATE_ISSUE_ACCEPTED__ACCOUNT_CONTACT_DAEMON = 104;
	protected final int TEMPLATE_OWNER_ACCEPTS__ACCOUNT_CONTACT_DAEMON = 105;
	
	protected final int TEMPLATE_WORKFLOW_GENERAL = 20;
	
	protected final int TEMPLATE_REMINDER = 30;
	protected final int TEMPLATE_REMINDER_CREATION = 31;
	protected final int TEMPLATE_MSA_NEXT_DUEDATE_REMINDER = 32;
	
	protected String from_email = null;
	protected String to_email = null;
	private String loggedUserName = null;
	
	private String tc = null;
	
	private boolean inited = false;
	
	protected String workspace_name = null;
	protected String projectID = null;
	protected NotificationParams params = null;
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.notification.NotificationSender#init(oem.edge.ets.fe.workflow.notification.NotificationParams, oem.edge.ets.fe.workflow.dao.DBAccess)
	 */
	public boolean init(NotificationParams params, DBAccess db) {
		from_email = getEmail(params.getLoggedUser(), db);
		loggedUserName  = getUserName(params.getLoggedUser(),db);
		try {
			db.prepareDirectQuery("SELECT PROJECT_NAME FROM ETS.ETS_PROJECTS WHERE PROJECT_ID='"+params.getProjectID()+"' with ur");
			int r = db.execute();
			System.out.println(db.getQuery());
			System.out.println("Rows = "+r);
			if(r==1)
				workspace_name = db.getString(0,0);
			if(params instanceof WorkflowNotificationParams)
			{
				WorkflowNotificationParams p = (WorkflowNotificationParams)params;
				db.prepareDirectQuery("Select wf_type from ets.wf_def where wf_id='"+p.getWorkflowID()+"'");
				if(db.execute()==1)
				{
					p.setWf_type(db.getString(0,0));
					if("QBR".equalsIgnoreCase(p.getWf_type()))
						p.setWf_type_text("QBR");
					if("SETMET".equalsIgnoreCase(p.getWf_type()))
						p.setWf_type_text("Set/Met");
					if("SELF ASSESSMENT".equalsIgnoreCase(p.getWf_type()))
						p.setWf_type_text("Self Assessment");
					
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		projectID = params.getProjectID();
		tc = params.getTc();
		this.params = params;
		inited = true;
		return true;
	}
	
	public boolean isInited(){return inited;}
	
	private String getEmail(String userid, DBAccess db)
	{
		ETSUserDetails u = new ETSUserDetails();
		u.setWebId(userid);
		u.extractUserDetails(db.getConnection());
		return u.getEMail();
		
	}
	protected String getUserName(String userid, DBAccess db)
	{
		ETSUserDetails u = new ETSUserDetails();
		u.setWebId(userid);
		u.extractUserDetails(db.getConnection());
		return u.getFirstName()+" "+u.getLastName();
		
	}
	
	
	public boolean send(int recipient_type, String recipient_userid, boolean bccMail, DBAccess db) {
		ArrayList temp = new ArrayList();
		String[] temp2 = recipient_userid.split(recipient_userid,',');
		for(int i=0; i<temp2.length; i++)
			temp.add(temp2[i]);
		return send(recipient_type,temp,bccMail,db);
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.notification.NotificationSender#send(int, java.util.Collection, boolean)
	 */
	public boolean send(int recipient_type, Collection recipient_userid, boolean bccMail, DBAccess db) {
		System.out.println("In send() of base class");
		Object[] toArray = recipient_userid.toArray();
		to_email = "";
		for(int i = 0; i<recipient_userid.size(); i++)
			if(i==0)
				to_email = getEmail((String)toArray[i], db);
			else
				to_email+=", "+ getEmail((String)toArray[i], db);

		return true;
	}
	protected boolean doMail(String body, String subject, String from, String to,boolean bccMail)
	{
		System.out.println("Subject passed in:"+subject);
		System.out.println("from: "+from);
		System.out.println("to: "+to);
		if(to.trim().length()==0 || from.trim().length()==0 || subject.trim().length()==0 || body.trim().length()==0)return true;
		try{
			if(!bccMail)
				NotificationMsgHelper.sendEMail(from,to,StringUtil.EMPTY_STRING,StringUtil.EMPTY_STRING,Global.mailHost,body,subject,from);
			else
				NotificationMsgHelper.sendEMail(from,StringUtil.EMPTY_STRING,StringUtil.EMPTY_STRING,to,Global.mailHost,body,subject,from);
				
		}catch(Exception e)
		{
			
			System.err.println("Message sending failed. "+e);
			System.out.println(e);
			return false;
		}
		System.out.println("[DO MAIL] Mail sent to "+to);
		System.out.println("Sub:"+subject);
		System.out.println(body);
		System.out.flush();
		return true;
	}
	protected String[] getTemplate(int template_id, DBAccess db)
	{
		try{
		db.prepareDirectQuery("Select subject, body from ets.wf_email_template where template_id="+template_id+" with ur");
		int r = db.execute();
		if(r==1)
		{
			String subject = db.getString(0,0);
			String body = db.getString(0,1);
			String[] template = new String[2];
			template[0] = subject;
			template[1] = body;
			return template;
		}
		}catch(Exception e){System.out.println(e);}
		
		if(template_id==TEMPLATE_NEW_ISSUE__OWNER)
		{
			 
			 String subject = "Collaboration Center - Issue Assigned: <ISSUE_TITLE>";
			
			 String body = "";
			 body += "A new SET/MET Issue has been assigned to you:\n"; 
			 body += "\n";
			 body += "The details of the Issue are as follows:\n"; 
			 body += "\n";
			 body += "==============================================================\n";
			 body += "Name:           	<ISSUE_ID> \n";
			 body += "Description:    	<ISSUE_TITLE>\n";
			 body += "Issue Target Date:   <ISSUE_TARGET_DATE>\n";
			 body += "Issue Status:		<ISSUE_STATUS>\n";
			 body += "Account Contact:	<WORKFLOW_ACCOUNT_CONTACT>\n";
			 body += "Date Assigned:       	<DATE_ASSIGNED>\n";
			 body += "\n";
			 body += "\n";
			 body += "This document is marked IBM Only\n";
			 body += "\n";
			 body += "To view this Issue, and accept or reject, click on the following  URL:\n";
			 body += "<GLOBAL_URL_BASE>ets/issueEdit.wss?proj=<PROJECT_ID>&workflowID=<WORKFLOW_ID>&tc=<TC>&id=<ISSUE_ID>\n";
			 body += "\n";
			 body += "If you do not respond within 5 days, the issue will automatically be assigned to you.\n";
			 body += "\n";
			 body += "==============================================================\n";
			 body += "Delivered by SET/MET Workflow Collaboration Center.\n";
			 body += "This is a system generated email.\n"; 
			 body += "==============================================================\n";
			 
			 String[] template = new String[2];
			 template[0] = subject;
			 template[1] = body;
			 return template;
		}

		if(template_id==TEMPLATE_NEW_ISSUE__CONTACT)
		{
			 
			 String subject = "Collaboration Center - Issue Contact: <ISSUE_TITLE>";
			
			 String body = "";

			body += "A new SET/MET Issue has been assigned to the following issue owners, and you have been deemed the Issue Contact\n";
			body += "\n";
			body += "<ISSUE_OWNERS>";
			body += "\n";
			body += "The details of the Issue are as follows:\n";
			body += "\n";
			body += "==============================================================\n";
			body += "Name:           		<ISSUE_TITLE>\n";
			body += "Description:    		<ISSUE_DESC>\n";
			body += "Issue Type: 			<ISSUE_TYPE>\n";
			body += "Issue Target Date:  	<ISSUE_TARGET_DATE>\n";
			body += "\n";
			body += "\n";
			body += "This document is marked IBM Only\n";
			body += "\n";
			body += "To view this Issue, click on the following  URL:\n";
			body += "<GLOBAL_URL_BASE>ets/issueEdit.wss?proj=<PROJECT_ID>&workflowID=<WORKFLOW_ID>&tc=<TC>&id=<ISSUE_ID>\n";
			body += "\n";
			body += "==============================================================\n";
			body += "Delivered by SET/MET Workflow Collaboration Center.\n";
			body += "This is a system generated email. \n";
			body += "==============================================================\n";
		 
			String[] template = new String[2];
			template[0] = subject;
			template[1] = body;
			return template;
	}
	if(template_id==TEMPLATE_NEW_ISSUE__GENERAL)
		{
			 
			 String subject = "Collaboration Center Sales Management Workflow-SET/MET \"<WORKSPACE_NAME>\" Update";
			
			 String body = "";
			 body += "An update has been made to the \"<WORKFLOW_NAME>\"\n";  
			 body += "\n";
			 body += "The details of the SET/MET are as follows: \n";
			 body += "\n";
			 body += "===============================================================\n";
			 body += "  Title:          <WORKFLOW_NAME>\n";
			 body += "  By:             <LOGGED_USERNAME>\n";
			 body += "  Comments:		<COMMENTS>\n";
			 body += "\n";
			 body += "To view the SET/MET, click on the following URL to be directed to the SET/MET Status Page:\n";
 			 body += "<GLOBAL_URL_BASE>ets/showstage.wss?action=viewsetmet&workflowID=<WORKFLOW_ID>&proj=<PROJECT_ID>&tc=<TC>\n";
    		 body += "\n";
			 body += "==================================================================\n";
		     body += "IBM Customer Connect Collaboration Center enables clients of IBM\n";
		     body += "and IBM team members to collaborate in a secure workspace. The IBM\n";
		     body += "Customer Connect Collaboration Center allows users to access a\n";
		     body += "comprehensive suite of on demand tools that is available on-line 24/7.\n";
		     body += "==================================================================\n";
		     body += "This is a system-generated e-mail delivered by Collaboration Center.\n";
		     body += "==================================================================\n";

			 String[] template = new String[2];
			template[0] = subject;
			template[1] = body;
			return template;
	}

	if(template_id==TEMPLATE_ISSUE_ACCEPTED__CONTACT)
	{
		 
		 String subject = "Collaboration Center - Issue Accepted:  \"<ISSUE_TITLE>\"";
		
		 String body = "";
		 
		 body += "The SET/MET Issue you assigned has been \"accepted\" \n";
		 body += "\n";
		 body += "The details of the Issue are as follows:\n"; 
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Name:           		<ISSUE_TITLE>\n";
		 body += "Description:    		<ISSUE_DESC>\n";
		 body += "Issue Status:			<ISSUE_STATUS>\n";
		 body += "Issue Target Date:   		<ISSUE_INITIAL_TARGET_DATE>\n";
		 body += "Issue Target revised date:	<ISSUE_TARGET_DATE>\n";
		 body += "Issue Owner:			<ISSUE_OWNERS>\n";
		 body += "Date Accepted:       		<TODAY> (mm/dd/yyyy)\n";
		 body += "Comments: <COMMENTS>\n";
		 body += "\n";
		 body += "This document is marked IBM Only\n";
		 body += "\n";
		 body += "To view this Issue in its entirety, click on the following  URL:\n";  
		 body += "<GLOBAL_URL_BASE>ets/issueEdit.wss?proj=<PROJECT_ID>&workflowID=<WORKFLOW_ID>&tc=<TC>&id=<ISSUE_ID>\n";
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		 body += "This is a system generated email. \n";
		 body += "==============================================================\n";
		 
		 String[] template = new String[2];
		template[0] = subject;
		template[1] = body;
		return template;
		}

	if(template_id==TEMPLATE_ISSUE_ACCEPTED__OWNER)
	{
		 
		 String subject = "Collaboration Center - Issue Accepted:  \"<ISSUE_TITLE>\"";
		
		 String body = "";
		 
		 body += "The SET/MET Issue you assigned has been \"accepted\" \n";
		 body += "\n";
		 body += "The details of the Issue are as follows:\n"; 
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Name:           		<ISSUE_TITLE>\n";
		 body += "Description:    		<ISSUE_DESC>\n";
		 body += "Issue Status:			<ISSUE_STATUS>\n";
		 body += "Issue Target Date:   		<ISSUE_INITIAL_TARGET_DATE>\n";
		 body += "Issue Target revised date:	<ISSUE_TARGET_DATE>\n";
		 body += "Issue Owner:			<ISSUE_OWNERS>\n";
		 body += "Date Accepted:       		<TODAY> (mm/dd/yyyy)\n";
		 body += "Comments: <COMMENTS>\n";
		 body += "\n";
		 body += "This document is marked IBM Only\n";
		 body += "\n";
		 body += "To view this Issue in its entirety, click on the following  URL:\n";  
		 body += "<GLOBAL_URL_BASE>ets/issueEdit.wss?proj=<PROJECT_ID>&workflowID=<WORKFLOW_ID>&tc=<TC>&id=<ISSUE_ID>\n";
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		 body += "This is a system generated email. \n";
		 body += "==============================================================\n";
		 
		 String[] template = new String[2];
		template[0] = subject;
		template[1] = body;
		return template;
	}
	
	if(template_id==TEMPLATE_ISSUE_REJECTED__CONTACT)
	{
		 
		 String subject = "Collaboration Center - Issue Rejected:  \"<ISSUE_TITLE>\"";
		
		 String body = "";
		 
		 body += "The SET/MET Issue you assigned has been \"rejected\" \n";
		 body += "\n";
		 body += "The details of the Issue are as follows:\n"; 
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Name:           		<ISSUE_TITLE>\n";
		 body += "Description:    		<ISSUE_DESC>\n";
		 body += "Issue Status:			<ISSUE_STATUS>\n";
		 body += "Issue Target Date:   		<ISSUE_TARGET_DATE>\n";
		 body += "Account Contact:			<WORKFLOW_ACCOUNT_CONTACT>\n";
		 body += "Date Rejected:       		<TODAY> (mm/dd/yyyy)\n";
		 body += "Comments: <COMMENTS>\n";
		 body += "\n";
		 body += "This document is marked IBM Only\n";
		 body += "\n";
		 body += "To view this Issue in its entirety, click on the following  URL:\n";  
		 body += "<GLOBAL_URL_BASE>ets/issueEdit.wss?proj=<PROJECT_ID>&workflowID=<WORKFLOW_ID>&tc=<TC>&id=<ISSUE_ID>\n";
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		 body += "This is a system generated email. \n";
		 body += "==============================================================\n";
		 
		 String[] template = new String[2];
		template[0] = subject;
		template[1] = body;
		return template;
		}
	
	if(template_id==TEMPLATE_ISSUE_REJECTED__OWNER)
	{
		
		 String subject = "Collaboration Center - Issue Rejected:  \"<ISSUE_TITLE>\"";
		
		 String body = "";
		 
		 body += "The SET/MET Issue you assigned has been \"rejected\" \n";
		 body += "\n";
		 body += "The details of the Issue are as follows:\n"; 
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Name:           		<ISSUE_TITLE>\n";
		 body += "Description:    		<ISSUE_DESC>\n";
		 body += "Issue Status:			<ISSUE_STATUS>\n";
		 body += "Issue Target Date:   		<ISSUE_TARGET_DATE>\n";
		 body += "Account Contact:			<WORKFLOW_ACCOUNT_CONTACT>\n";
		 body += "Date Rejected:       		<TODAY> (mm/dd/yyyy)\n";
		 body += "Comments: <COMMENTS>\n";
		 body += "\n";
		 body += "This document is marked IBM Only\n";
		 body += "\n";
		 body += "To view this Issue in its entirety, click on the following  URL:\n";  
		 body += "<GLOBAL_URL_BASE>ets/issueEdit.wss?proj=<PROJECT_ID>&workflowID=<WORKFLOW_ID>&tc=<TC>&id=<ISSUE_ID>\n";
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		 body += "This is a system generated email. \n";
		 body += "==============================================================\n";
		 
		 String[] template = new String[2];
		template[0] = subject;
		template[1] = body;
		return template;
		}
		
	
	if(template_id==TEMPLATE_WORKFLOW_GENERAL)
	{
		System.out.println("Fetching General Template...");
		 String subject = "Collaboration Center Sales Management Workflow-SET/MET \"<WORKSPACE_NAME>\" Update";
		
		 String body = "";
		 body += "This is a general notification\n";  
		 body += "\n";
		 body += "\n";
		 body += "===============================================================\n";
		 body += "  Comments:		<COMMENTS>\n";
		 body += "\n";
		 body += "To view the SET/MET, click on the following URL to be directed to the SET/MET Status Page:\n";
		 body += "<GLOBAL_URL_BASE>ets/showstage.wss?action=viewsetmet&workflowID=<WORKFLOW_ID>&proj=<PROJECT_ID>&tc=<TC>\n";
		 body += "\n";
		 body += "==================================================================\n";
	     body += "IBM Customer Connect Collaboration Center enables clients of IBM\n";
	     body += "and IBM team members to collaborate in a secure workspace. The IBM\n";
	     body += "Customer Connect Collaboration Center allows users to access a\n";
	     body += "comprehensive suite of on demand tools that is available on-line 24/7.\n";
	     body += "==================================================================\n";
	     body += "This is a system-generated e-mail delivered by Collaboration Center.\n";
	     body += "==================================================================\n";

		 String[] template = new String[2];
		template[0] = subject;
		template[1] = body;
		return template;
		}
	
	if(template_id==TEMPLATE_REMINDER)
	{
		 System.out.println("Fetching Reminder Template...");
		 String subject = "Collaboration Center Sales Management Workflow-SET/MET \"<WORKSPACE_NAME>\" Reminder";
		
		 String body = "";
		 body += "This mail is to remind you of an event in Workflow \"<WORKFLOW_NAME>\"\n";  
		 body += "\n";
		 body += "The details of the SET/MET are as follows: \n";
		 body += "\n";
		 body += "===============================================================\n";
		 body += "  Title:          <WORKFLOW_NAME>\n";
		 body += "  By:             <LOGGED_USERNAME>\n";
		 body += "\n";
		 body += "To view the SET/MET, click on the following URL to be directed to the SET/MET Status Page:\n";
			 body += "<GLOBAL_URL_BASE>ets/ETSProjectsServlet.wss?proj=<PROJECT_ID>&tc=<TC>&linkid=1k0000\n";
		 body += "\n";
		 body += "==================================================================\n";
	     body += "IBM Customer Connect Collaboration Center enables clients of IBM\n";
	     body += "and IBM team members to collaborate in a secure workspace. The IBM\n";
	     body += "Customer Connect Collaboration Center allows users to access a\n";
	     body += "comprehensive suite of on demand tools that is available on-line 24/7.\n";
	     body += "==================================================================\n";
	     body += "This is a system-generated e-mail delivered by Collaboration Center.\n";
	     body += "==================================================================\n";

		 String[] template = new String[2];
		template[0] = subject;
		template[1] = body;
		return template;
	}

	if(template_id==TEMPLATE_REMINDER_CREATION)
	{
		 System.out.println("Fetching Reminder Creation Template...");
		 String subject = "Collaboration Center Sales Management Workflow-SET/MET \"<WORKSPACE_NAME>\" Reminder";
		
		 String body = "";
		 body += "A new event has been added\n";  
		 body += "\n";
		 body += "The details of the event are as follows: \n";
		 body += "\n";
		 body += "===============================================================\n";
		 body += "  Scheduled on:   <EVT_SCHEDULE_DATE>\n";
		 body += "  Scheduled by:   <EVT_SCHEDULED_BY>\n";
		 body += "  Start time:     <EVT_START_TIME>\n";
		 body += "  Duration:       <EVT_DURATION>\n";
		 body += "  Subject:        <EVT_SUBJECT>\n";
		 body += "  Description:    <EVT_DESCRIPTION>\n";
		 body += "\n";
		 body += "To view the SET/MET, click on the following URL to be directed to the SET/MET Status Page:\n";
		 body += "<GLOBAL_URL_BASE>ets/ETSProjectsServlet.wss?proj=<PROJECT_ID>&tc=<TC>&linkid=1k0000\n";
		 body += "\n";
		 body += "==================================================================\n";
	     body += "IBM Customer Connect Collaboration Center enables clients of IBM\n";
	     body += "and IBM team members to collaborate in a secure workspace. The IBM\n";
	     body += "Customer Connect Collaboration Center allows users to access a\n";
	     body += "comprehensive suite of on demand tools that is available on-line 24/7.\n";
	     body += "==================================================================\n";
	     body += "This is a system-generated e-mail delivered by Collaboration Center.\n";
	     body += "==================================================================\n";

		 String[] template = new String[2];
		template[0] = subject;
		template[1] = body;
		return template;
	}

	if(template_id==TEMPLATE_ISSUE_REASSIGNED__OWNER)
	{
		 
		 String subject = "Collaboration Center - Issue Assigned: <ISSUE_TITLE>";
		
		 String body = "";
		 body += "A SET/MET Issue has been assigned to you:\n"; 
		 body += "\n";
		 body += "The details of the Issue are as follows:\n"; 
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Name:           	<ISSUE_ID> \n";
		 body += "Description:    	<ISSUE_TITLE>\n";
		 body += "Issue Target Date:   <ISSUE_TARGET_DATE>\n";
		 body += "Issue Status:		<ISSUE_STATUS>\n";
		 body += "Account Contact:	<WORKFLOW_ACCOUNT_CONTACT>\n";
		 body += "Date Assigned:       	<DATE_ASSIGNED>\n";
		 body += "\n";
		 body += "\n";
		 body += "This document is marked IBM Only\n";
		 body += "\n";
		 body += "To view this Issue, and accept or reject, click on the following  URL:\n";
		 body += "<GLOBAL_URL_BASE>ets/issueEdit.wss?proj=<PROJECT_ID>&workflowID=<WORKFLOW_ID>&tc=<TC>&id=<ISSUE_ID>\n";
		 body += "\n";
		 body += "If you do not respond within 5 days, the issue will automatically be assigned to you.\n";
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		 body += "This is a system generated email.\n"; 
		 body += "==============================================================\n";
		 
		 String[] template = new String[2];
		 template[0] = subject;
		 template[1] = body;
		 return template;
	}
	if(template_id==TEMPLATE_ISSUE_REASSIGNED__OLDOWNER)
	{
		 
		 String subject = "Collaboration Center - Issue: <ISSUE_TITLE>";
		
		 String body = "";
		 body += "You are no longer the owner of a SET/MET issue.\n"; 
		 body += "\n";
		 body += "The details of the Issue are as follows:\n"; 
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Name:           	<ISSUE_ID> \n";
		 body += "Description:    	<ISSUE_TITLE>\n";
		 body += "Account Contact:	<WORKFLOW_ACCOUNT_CONTACT>\n";
		 body += "\n";
		 body += "This document is marked IBM Only\n";
		 body += "\n";
		 body += "To view this Issue, click on the following  URL:\n";
		 body += "<GLOBAL_URL_BASE>ets/issueEdit.wss?proj=<PROJECT_ID>&workflowID=<WORKFLOW_ID>&tc=<TC>&id=<ISSUE_ID>\n";
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		 body += "This is a system generated email.\n"; 
		 body += "==============================================================\n";
		 
		 String[] template = new String[2];
		 template[0] = subject;
		 template[1] = body;
		 return template;
	}

	if(template_id==TEMPLATE_ISSUE_EDITED)
	{
		 
		 String subject = "Collaboration Center - Issue: <ISSUE_TITLE> EDITED";
		
		 String body = "";
		 body += "** THIS TEMPLATE IS INCOMPLETE ** FILL UP ISSUE EDIT TEMPLATE\n\n";
		 body += "The details of the Issue are as follows:\n"; 
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Name:           	<ISSUE_ID> \n";
		 body += "Description:    	<ISSUE_TITLE>\n";
		 body += "Account Contact:	<WORKFLOW_ACCOUNT_CONTACT>\n";
		 body += "\n";
		 body += "This document is marked IBM Only\n";
		 body += "\n";
		 body += "To view this Issue, click on the following  URL:\n";
		 body += "<GLOBAL_URL_BASE>ets/issueEdit.wss?proj=<PROJECT_ID>&workflowID=<WORKFLOW_ID>&tc=<TC>&id=<ISSUE_ID>\n";
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		 body += "This is a system generated email.\n"; 
		 body += "==============================================================\n";
		 
		 String[] template = new String[2];
		 template[0] = subject;
		 template[1] = body;
		 return template;
	}

		
		return null;
	}


	protected String fillPlaceholders(String s) {
		s= s.replaceAll("<TODAY>",CharUtils.HTMLizeNoNull(er((new Date()).toString())));
		s= s.replaceAll("<WORKSPACE_NAME>",CharUtils.HTMLizeNoNull(er(workspace_name)));
		s= s.replaceAll("<LOGGED_USERNAME>",CharUtils.HTMLizeNoNull(er(loggedUserName)));
		s= s.replaceAll("<GLOBAL_URL_BASE>",CharUtils.HTMLizeNoNull(er(Global.getUrl(""))));
		s= s.replaceAll("<PROJECT_ID>",CharUtils.HTMLizeNoNull(er(projectID)));
		s= s.replaceAll("<TC>",CharUtils.HTMLizeNoNull(er(tc)));
		s= s.replaceAll("<TC_MAIN>",CharUtils.HTMLizeNoNull(er(MiscUtils.getTc(projectID,MiscUtils.TC_MAIN))));
		s= s.replaceAll("<TC_ASSESSMENT>",CharUtils.HTMLizeNoNull(er(MiscUtils.getTc(projectID,MiscUtils.TC_ASSESSMENT))));
		s= s.replaceAll("<TC_MEETINGS>",CharUtils.HTMLizeNoNull(er(MiscUtils.getTc(projectID,MiscUtils.TC_MEETINGS))));
		s= s.replaceAll("<TC_TEAM>",CharUtils.HTMLizeNoNull(er(MiscUtils.getTc(projectID,MiscUtils.TC_TEAM))));
		if(params instanceof WorkflowNotificationParams)
		{
			WorkflowNotificationParams p = (WorkflowNotificationParams)params;
			s= s.replaceAll("<WF_TYPE_TEXT>",CharUtils.HTMLizeNoNull(er(p.getWf_type_text())));
			s= s.replaceAll("<WF_TYPE>",CharUtils.HTMLizeNoNull(er(p.getWf_type())));
		}
		return s;
	}
	/**
	 * er stands for Escape Regex. Since this func will be called in many places, the name has been shortened.
	 * @param s
	 * @return replaces \ with \\ and $ with \$
	 */
	protected String er(String s)
	{
		if(s==null)return null;
		String retval= s.replaceAll("\\\\","\\\\\\\\").replaceAll("\\$","\\\\\\$");
		System.out.println(s);
		System.out.println(retval);
		return retval;
	}
	public static void main(String[] a)
	{
		Notifier n = new Notifier();
		String[]t =n.getTemplate(3,null); 
		System.out.println(t[0]);
		System.out.println(t[1]);
			
	}
}

