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


package oem.edge.ets.fe.workflow.util;

import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;

import oem.edge.common.Global;
import oem.edge.ets.fe.SMTPMail;
import oem.edge.ets.fe.documents.NotificationMsgHelper;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;

/**
 * Class       : Notifier
 * Package     : oem.edge.ets.fe.workflow.util
 * Description : 
 * Date		   : Oct 23, 2006
 * 
 * @author     : Pradyumna Achar
 * 
 * @deprecated Replaced by @link{ #oem.edge.ets.fe.workflow.notification}
 * 
 * Use tools in oem.edge.ets.fe.workflow.notification instead of this.
 */
public class Notifier {
	
	/*
	 * This utility class is not to be used anymore.
	 * Use tools under oem.edge.ets.fe.workflow.notification package instead.
	 */
	
	private static Log logger = WorkflowLogger.getLogger(Notifier.class);
	
	
	/**
	 * 
	 * @deprecated
	 */
	public static boolean editIssueNewOwnerOWNER(String issueID, String projectID, String workflowID, String tc, String fromEmail, ArrayList toEmails)
	{
		return newIssueOWNER(issueID,projectID,workflowID,tc,fromEmail,toEmails);
	}

	
	/**
	 *@deprecated 
	 */
	public static boolean editIssueDeletedOwnerOWNER(String issueID, String projectID, String workflowID, String tc, String fromEmail, ArrayList toEmails)
	{
		DetailsUtils d = new DetailsUtils();
		d.setIssueID(issueID);
		d.setWorkflowID(workflowID);
		d.setProjectID(projectID);
		d.extractAllDetails();
		System.out.println(workflowID);
		System.out.println(projectID);
		String body = "";
		body += "* * AIC Workflow Notification Mail * * ";
		body += "\n";
		body += "\n Your are no longer an owner for the Issue ";
		body += "\n having the following details:";
		body += "\n Issue ID    : "+issueID;
		body += "\n Issue Title : "+d.getIissue_title();
		body += "\n Workflow    : "+d.getWwf_name();
		body += "\n";
		body += "\n Description:";
		body += "\n "+d.getIissue_desc();
		body += "\n";
		body += "\n You can use the following link to visit the issue on AIC:";
		body += "\n";
		body += "\n(You may need to copy the URL and paste it in the address bar of a logged-in session)";
		body += "\n";
		body += "\n "+ Global.getUrl("ets/issueEdit.wss?proj="+projectID+"&workflowID="+workflowID+"&tc="+tc+"&id="+issueID);
		body += "\n";
		body += "\n .This is a system generated mail.";
		
		
		String subject = "IBM Customer Connect: AIC Workflow Notification";
		String to = "";
		for(int i = 0; i<toEmails.size(); i++)
			if(i!=0)
				to += ","+(String)toEmails.get(i);
			else
				to = (String)toEmails.get(i);
		
		return doMail(body, subject, fromEmail, to);

	}

	/**
	 *@deprecated 
	 */
	public static boolean editIssueCancelled(String issueID, String projectID, String workflowID, String tc, String fromEmail, ArrayList toEmails)
	{
		DetailsUtils d = new DetailsUtils();
		d.setIssueID(issueID);
		d.setWorkflowID(workflowID);
		d.setProjectID(projectID);
		d.extractAllDetails();
		System.out.println(workflowID);
		System.out.println(projectID);
		String body = "";
		body += "* * AIC Workflow Notification Mail * * ";
		body += "\n";
		body += "\n The issue having the following details ";
		body += "\n has been cancelled";
		body += "\n Issue ID    : "+issueID;
		body += "\n Issue Title : "+d.getIissue_title();
		body += "\n Workflow    : "+d.getWwf_name();
		body += "\n";
		body += "\n Description:";
		body += "\n "+d.getIissue_desc();
		body += "\n";
		body += "\n You can use the following link to visit the issue on AIC:";
		body += "\n";
		body += "\n(You may need to copy the URL and paste it in the address bar of a logged-in session)";
		body += "\n";
		body += "\n "+ Global.getUrl("ets/issueEdit.wss?proj="+projectID+"&workflowID="+workflowID+"&tc="+tc+"&id="+issueID);
		body += "\n";
		body += "\n .This is a system generated mail.";
		
		
		String subject = "IBM Customer Connect: AIC Workflow Notification";
		String to = "";
		for(int i = 0; i<toEmails.size(); i++)
			if(i!=0)
				to += ","+(String)toEmails.get(i);
			else
				to = (String)toEmails.get(i);
		
		return doMail(body, subject, fromEmail, to);

	}

	/**
	 *@deprecated 
	 */
	public static boolean editIssueCompleted(String issueID, String projectID, String workflowID, String tc, String fromEmail, ArrayList toEmails)
	{
		DetailsUtils d = new DetailsUtils();
		d.setIssueID(issueID);
		d.setWorkflowID(workflowID);
		d.setProjectID(projectID);
		d.extractAllDetails();
		System.out.println(workflowID);
		System.out.println(projectID);
		String body = "";
		body += "* * AIC Workflow Notification Mail * * ";
		body += "\n";
		body += "\n The issue having the following details ";
		body += "\n has been completed";
		body += "\n Issue ID    : "+issueID;
		body += "\n Issue Title : "+d.getIissue_title();
		body += "\n Workflow    : "+d.getWwf_name();
		body += "\n";
		body += "\n Description:";
		body += "\n "+d.getIissue_desc();
		body += "\n";
		body += "\n You can use the following link to visit the issue on AIC:";
		body += "\n";
		body += "\n(You may need to copy the URL and paste it in the address bar of a logged-in session)";
		body += "\n";
		body += "\n "+ Global.getUrl("ets/issueEdit.wss?proj="+projectID+"&workflowID="+workflowID+"&tc="+tc+"&id="+issueID);
		body += "\n";
		body += "\n .This is a system generated mail.";
		
		
		String subject = "IBM Customer Connect: AIC Workflow Notification";
		String to = "";
		for(int i = 0; i<toEmails.size(); i++)
			if(i!=0)
				to += ","+(String)toEmails.get(i);
			else
				to = (String)toEmails.get(i);
		
		return doMail(body, subject, fromEmail, to);

	}

	/**
	 *@deprecated 
	 */
	public static boolean newIssueOWNER(String issueID, String projectID, String workflowID, String tc, String fromEmail, ArrayList toEmails)
	{
		DetailsUtils d = new DetailsUtils();
		d.setIssueID(issueID);
		d.setWorkflowID(workflowID);
		d.setProjectID(projectID);
		d.extractAllDetails();
		System.out.println(workflowID);
		System.out.println(projectID);
		String body = "";
		Date tempd = new Date();
		
		 body += "A new SET/MET Issue has been assigned to you:\n"; 
		 body += "\n";
		 body += "The details of the Issue are as follows:\n"; 
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Name:           	" + d.getIissue_title() +"\n";
		 body += "Description:    	" + d.getIissue_desc() +"\n";
		 body += "Issue Target Date:   	"+ d.getItarget_date() + "\n";
		 body += "Issue Status:		" + d.getIstatus() + "\n";
		 body += "Account Contact:	" + d.getWacct_contact()+ "\n";
		 body += "Date Assigned:       	"+ (tempd.getMonth()+1)+"/"+tempd.getDate()+"/"+tempd.getYear()+"(mm/dd/yyyy)\n";
		 body += "\n";
		 body += "\n";
		 body += "This document is marked IBM Only\n";
		 body += "\n";
		 body += "To view this Issue, and accept or reject, click on the following  URL:\n";
		 body += Global.getUrl("ets/issueEdit.wss?proj="+projectID+"&workflowID="+workflowID+"&tc="+tc+"&id="+issueID)+"\n";
		 body += "\n";
		 body += "If you do not respond within 5 days, the issue will automatically be assigned to you.\n";
		 body += "\n";
		 body += "==============================================================\n";
		 body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		 body += "This is a system generated email.\n"; 
		 body += "==============================================================\n";
		
		String subject = "Collaboration Center - Issue Assigned: "+d.getIissue_title();
		String to = "";
		for(int i = 0; i<toEmails.size(); i++)
			if(i!=0)
				to += ","+(String)toEmails.get(i);
			else
				to = (String)toEmails.get(i);
		
		return doMail(body, subject, fromEmail, to);

	}

	/**
	 *@deprecated 
	 */
	public static boolean newIssueCONTACT(String issueID, String projectID, String workflowID, String tc, String fromEmail, ArrayList toEmails)
	{
		DetailsUtils d = new DetailsUtils();
		d.setIssueID(issueID);
		d.setWorkflowID(workflowID);
		d.setProjectID(projectID);
		d.extractAllDetails();
		
		System.out.println(workflowID);
		System.out.println(projectID);
		String body = "";
		

		body += "A new SET/MET Issue has been assigned to the following issue owners, and you have been deemed the Issue Contact\n";
		body += "\n";
		for(int i =0; i<d.getIownerNames().size(); i++)
		body += "("+(i+1)+") "+d.getIownerNames().get(i)+"\n";
		body += "\n";
		body += "The details of the Issue are as follows:\n"; 
		body += "\n";
		body += "==============================================================\n";
  		body += "Name:           		"+d.getIissue_title()+"\n";
  		body += "Description:    		"+d.getIissue_desc()+ "\n";
  		body += "Issue Type: 			"+d.getIissue_type()+"\n";
  		body += "Issue Target Date:  	"+d.getItarget_date()+"\n";
		body += "\n";
		body += "\n";
  		body += "This document is marked IBM Only\n";
		body += "\n";
		body += "To view this Issue, click on the following  URL:\n";  
		body += Global.getUrl("ets/issueEdit.wss?proj="+projectID+"&workflowID="+workflowID+"&tc="+tc+"&id="+issueID)+"\n";
		body += "\n";
		body += "==============================================================\n";
		body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		body += "This is a system generated email. \n";
		body += "==============================================================\n";

		
		String subject = "Collaboration Center - Issue: "+d.getIissue_title();
		String to = "";
		for(int i = 0; i<toEmails.size(); i++)
			if(i!=0)
				to += ","+(String)toEmails.get(i);
			else
				to = (String)toEmails.get(i);
		
		return doMail(body, subject, fromEmail, to);

	}

	/**
	 *@deprecated 
	 */
	public static boolean issueAcceptedCONTACT(String issueID, String projectID, String workflowID, String tc, String fromEmail, ArrayList toEmails)
	{
		DetailsUtils d = new DetailsUtils();
		d.setIssueID(issueID);
		d.setWorkflowID(workflowID);
		d.setProjectID(projectID);
		d.extractAllDetails();
		
		Date tempd = new Date();
		
		System.out.println(workflowID);
		System.out.println(projectID);
		String body = "";
		

		body += "The SET/MET Issue you assigned has been \"accepted\"\n"; 
		body += "\n";
		body += "The details of the Issue are as follows:\n"; 
		body += "\n";
		body += "==============================================================\n";
  		body += "Name:           		"+d.getIissue_title()+"\n";
  		body += "Description:    		"+d.getIissue_desc()+"\n";
  		body += "Issue Status:			"+d.getIstatus()+"\n";
  		body += "Issue Target Date:   	"+d.getItarget_date()+"\n";
  		body += "Issue Target revised date:"+d.getItarget_date()+"\n";
		body += "Issue Owners:			\n";
		for(int i =0; i<d.getIownerNames().size(); i++)
			body += "\t"+d.getIownerNames().get(i)+"\n";
  		body += "Date Accepted:       	"+ (tempd.getMonth()+1)+"/"+tempd.getDate()+"/"+tempd.getYear()	+"(mm/dd/yyyy)\n";
  		body += "Comments:\n";
		body += "\n";
  		body += "This document is marked IBM Only\n";
		body += "\n";
		body += "To view this Issue in its entirety, click on the following  URL:  \n";
		body += Global.getUrl("ets/issueEdit.wss?proj="+projectID+"&workflowID="+workflowID+"&tc="+tc+"&id="+issueID)+"\n";
		body += "\n";
		body += "==============================================================\n";
		body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		body += "This is a system generated email. \n";
		body += "==============================================================\n";

		
		String subject = "Collaboration Center - Issue: "+d.getIissue_title();
		String to = "";
		for(int i = 0; i<toEmails.size(); i++)
			if(i!=0)
				to += ","+(String)toEmails.get(i);
			else
				to = (String)toEmails.get(i);
		
		return doMail(body, subject, fromEmail, to);

	}

	/**
	 *@deprecated 
	 */
	public static boolean issueRejectedCONTACT(String issueID, String projectID, String workflowID, String tc, String fromEmail, ArrayList toEmails)
	{
		DetailsUtils d = new DetailsUtils();
		d.setIssueID(issueID);
		d.setWorkflowID(workflowID);
		d.setProjectID(projectID);
		d.extractAllDetails();
		
		Date tempd = new Date();
		
		System.out.println(workflowID);
		System.out.println(projectID);
		String body = "";
		

		body += "The SET/MET Issue you assigned has been \"Rejected\"\n"; 
		body += "\n";
		body += "The details of the Issue are as follows:\n"; 
		body += "\n";
		body += "==============================================================\n";
  		body += "Name:           		"+d.getIissue_title()+"\n";
  		body += "Description:    		"+d.getIissue_desc()+"\n";
  		body += "Issue Status:			"+d.getIstatus()+"\n";
  		body += "Issue Target Date:   	"+d.getItarget_date()+"\n";
  		body += "Issue Target revised date:"+d.getItarget_date()+"\n";
		body += "Issue Owners:			\n";
		for(int i =0; i<d.getIownerNames().size(); i++)
			body += "\t"+d.getIownerNames().get(i)+"\n";
  		body += "Date Accepted:       	"+ (tempd.getMonth()+1)+"/"+tempd.getDate()+"/"+tempd.getYear()	+"(mm/dd/yyyy)\n";
  		body += "Comments:\n";
		body += "\n";
  		body += "This document is marked IBM Only\n";
		body += "\n";
		body += "To view this Issue in its entirety, click on the following  URL:  \n";
		body += Global.getUrl("ets/issueEdit.wss?proj="+projectID+"&workflowID="+workflowID+"&tc="+tc+"&id="+issueID)+"\n";
		body += "\n";
		body += "==============================================================\n";
		body += "Delivered by SET/MET Workflow Collaboration Center.\n";
		body += "This is a system generated email. \n";
		body += "==============================================================\n";

		
		String subject = "Collaboration Center - Issue: "+d.getIissue_title();
		String to = "";
		for(int i = 0; i<toEmails.size(); i++)
			if(i!=0)
				to += ","+(String)toEmails.get(i);
			else
				to = (String)toEmails.get(i);
		
		return doMail(body, subject, fromEmail, to);

	}

	/**
	 *@deprecated 
	 */
	private static boolean doMail(String body, String subject, String from, String to)
	{
		/*SMTPMail mailer = new SMTPMail();
		mailer.setSubject(subject);
		mailer.setMailText(body);
    	mailer.setOriginator(from);
		mailer.setRecipients(to);
		*/
		
			
		System.out.println("from: "+from);
		System.out.println("to: "+to);
		if(to.trim().length()==0 || from.trim().length()==0 || subject.trim().length()==0 || body.trim().length()==0)return true;
		try{
			//mailer.send();
			NotificationMsgHelper.sendEMail(from,to,StringUtil.EMPTY_STRING,StringUtil.EMPTY_STRING,Global.mailHost,body,subject,from);
		}catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Message sending failed. "+e);
			e.printStackTrace();
			return false;
		}
		System.out.println("Mail sent to "+to);
		System.out.println("Sub:"+subject);
		System.out.println(body);
		return true;
	}
	
}

