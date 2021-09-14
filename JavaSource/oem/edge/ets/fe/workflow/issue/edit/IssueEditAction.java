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

package oem.edge.ets.fe.workflow.issue.edit;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.CharUtils;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;
import oem.edge.ets.fe.workflow.util.RoleUtils;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Class : IssueEditAction
 * Package : oem.edge.ets.fe.workflow.issue.edit
 * Description : Date : Oct 10, 2006
 * 
 * @author : Pradyumna Achar
 */
public class IssueEditAction extends WorkflowAction

{
	private static Log logger = WorkflowLogger.getLogger(IssueEditAction.class);

	public ActionForward executeWorkflow(ActionMapping mapping, WorkflowForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		ActionForward forward = null;
		EditIssueFormBean formBean = null;
		EditIssueVO vo = null;
		IssueEditPreload preloadBean = null;
		
		String proj = request.getParameter("proj");
		String tc = request.getParameter("tc");
		String workflowID = request.getParameter("workflowID");
		String id = request.getParameter("id");
		
		if(form==null)
			System.out.println("PANIC: FORM NULL");
		
		formBean = (EditIssueFormBean) form;

		if(id==null && (formBean!=null && (EditIssueVO)formBean.getWorkflowObject()!=null))
			{id=((EditIssueVO)formBean.getWorkflowObject()).getIssueID();System.out.println("Got issueID from vo");}
			
		MiscUtils.setDB(request);
		DBAccess db = (DBAccess)request.getAttribute("WFdb");

		//////////////////////////////////////////////////////////////////////////////////////
		boolean badFlag = false;
		if(projectID == null || !MiscUtils.isValidProject(projectID,db))
		{
			request.setAttribute("WFerr","Bad project ID :"+projectID);
			forward = mapping.findForward("badURL");
			badFlag = true;
		}
		else
		{
			if(workflowID == null || !MiscUtils.isValidWorkflow(projectID, workflowID,db))
			{
				request.setAttribute("WFerr","Bad workflow ID :"+workflowID);
				forward = mapping.findForward("badURL");
				badFlag = true;
			}
		}
		//TODO: need to check for isThisAnIssueInThisWorkflow also.
		if(tc == null)
		{
			request.setAttribute("WFerr","Bad top category");
			forward = mapping.findForward("badURL");
			badFlag = true;
		}
		if(badFlag == true){
			try{db.close();}catch(Exception e){System.err.println(e);}
			request.removeAttribute("WFdb");
			return forward;
		}
/////////////////////////////////////////////////////////////////////////////////////////

		request.setAttribute("proj", proj);
		request.setAttribute("tc", tc);
		request.setAttribute("workflowID", workflowID);
		System.out.println("ID="+id);
		request.setAttribute("id", id);

		
		if(id ==null || !MiscUtils.isValidIssue(id,db))
		{
			request.setAttribute("WFerr","Bad issue ID :"+id);
			forward = mapping.findForward("badURL");
			try{db.close();}catch(Exception e){System.err.println(e);}
			request.removeAttribute("WFdb");
			return forward;
		}
	
		
		if (formBean.getAction() == null || formBean.getAction().length() == 0) {
			forward = mapping.findForward("issueEdit");
			setPerms(loggedUser, request,id,workflowID,projectID);
			try{db.close();}catch(Exception e){System.err.println(e);}
			request.removeAttribute("WFdb");
			return forward;
		}

		
		
		vo = (EditIssueVO) formBean.getWorkflowObject();
		
		if(vo==null)
			System.out.println("PANIC: VO NULL");
	
		vo.setProjectID(projectID);
		
		if(vo.getWorkflowID()==null)
			vo.setWorkflowID(workflowID);
		
		
		//Actions coming from issueEdit main page:
		
		if (formBean.getAction().equalsIgnoreCase("close_confirm")) {
				request.setAttribute("actionstring", "complete");
				forward = mapping.findForward("issueClose");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
		}
		
		if (formBean.getAction().equalsIgnoreCase("cancel_confirm")) {
				request.setAttribute("actionstring", "cancel");
				forward = mapping.findForward("issueClose");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
		}

		if (formBean.getAction().equalsIgnoreCase("do_accept")) {
				request.setAttribute("actionstring", "accept");
				forward = mapping.findForward("issueAcceptReject");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
		}

		if (formBean.getAction().equalsIgnoreCase("do_reject")) {
				request.setAttribute("actionstring", "reject");
				forward = mapping.findForward("issueAcceptReject");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
		}

		if (formBean.getAction().equalsIgnoreCase("do_modify")) {
			try{db.close();}catch(Exception e){System.err.println(e);}		
			request.removeAttribute("WFdb");
			preloadBean = new IssueEditPreload(id, projectID,formBean, request);
			request.setAttribute("preloadBean",preloadBean);
			forward = mapping.findForward("issueModify");
			return forward;
		}

		if (formBean.getAction().equalsIgnoreCase("do_comments")) {
				forward = mapping.findForward("issueComments");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
		}

		HttpServletRequest tempRequest = request;
		
		if (formBean.getAction().equalsIgnoreCase("submit")) {
			if(id==null || !MiscUtils.isValidIssue(id,db))
			{
				request.setAttribute("WFerr","Bad issue ID :"+id);
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			setPerms(loggedUser, tempRequest,id,workflowID,projectID);
			if(tempRequest.getAttribute("perm_details_view_or_edit")==null)
			{
				request.setAttribute("WFerr","You are not authorized to perform that operation.");
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			vo.setDB(db);
			
			ArrayList errs = getValidationErrors(vo);
			if (errs!=null && errs.size()!=0) {
				request.setAttribute("errorMessages",errs);
				id = ((EditIssueVO) formBean.getWorkflowObject()).getIssueID();
				forward = mapping.findForward("issueModify");
				preloadBean = new IssueEditPreload(id, projectID,formBean, request);
				request.setAttribute("preloadBean",preloadBean);
			}
			else {
				EditIssueBL bl = new EditIssueBL();
				vo.setUserid(loggedUser);
				vo.setWorkflowID(workflowID);
				vo.setProjectID(proj);
				bl.modifyIssue(vo, loggedUser, tc);
				forward = new ActionForward("/issueEdit.wss"
						+ "?proj=" + proj + "&workflowID=" + workflowID
						+ "&tc=" + tc+"&id="+id, true);
				vo.reset();
			}
		}

		if (formBean.getAction().equalsIgnoreCase("comment")) {
			if(id==null || !MiscUtils.isValidIssue(id,db))
			{
				request.setAttribute("WFerr","Bad issue ID :"+id);
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			setPerms(loggedUser, tempRequest,id,workflowID,projectID);
			if(tempRequest.getAttribute("perm_comments")==null)
			{
				request.setAttribute("WFerr","You are not authorized to perform that operation.");
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			boolean flag = false;
			if(vo.getComment()==null ||	vo.getComment().trim().length()==0)
			{
				request.setAttribute("WFerror","Please fill in some comments");
				flag=true;
			}
			if(vo.getComment()!=null && !CharUtils.isAlNum(vo.getComment()))
			{
				request.setAttribute("WFerror","Comments can only contain alphabets, digits and space characters");
				flag = true;
			}
			if(vo.getComment()!=null &&	CharUtils.SQLize(vo.getComment()).length() > 1000)
			{
				request.setAttribute("WFerror","Comments must not exceed 1000 characters");
				flag = true;
			}

			if(!flag)
			{
				vo.setComment(CharUtils.SQLize(vo.getComment()));
				EditIssueBL bl = new EditIssueBL();
				vo.setUserid(loggedUser);
				vo.setDB(db);
				bl.addComment(vo,loggedUser);
				forward = new ActionForward("/issueEdit.wss"+"?proj="+proj+"&workflowID="+workflowID+"&tc="+tc+"&id="+id,true);
				vo.reset();
			}
			else
			{
				request.setAttribute("actionstring", "accept");
				forward = mapping.findForward("issueComments");
			}
		}

		if (formBean.getAction().equalsIgnoreCase("accept")) {
			if(id==null || !MiscUtils.isValidIssue(id,db))
			{
				request.setAttribute("WFerr","Bad issue ID :"+id);
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			setPerms(loggedUser, tempRequest,id,workflowID,projectID);
			if(tempRequest.getAttribute("perm_accept_reject")==null)
			{
				request.setAttribute("WFerr","You are not authorized to perform that operation.");
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			boolean flag = false;
			
			if(vo.getComment()==null ||	vo.getComment().trim().length()==0)
			{
				//request.setAttribute("WFerror","Comments are mandatory.");
				//flag=true; disabled for 7.1.1
				vo.setComment("-No Comments Provided-");
			}
			if(vo.getComment()!=null &&	!CharUtils.isAlNum(vo.getComment()))
			{
				request.setAttribute("WFerror","Comments can only contain alphabets, digits and space characters");
				flag = true;
			}
			if(vo.getComment()!=null && CharUtils.SQLize(vo.getComment()).length() > 1000)
			{
				request.setAttribute("WFerror","Comments must not exceed 1000 characters");
				flag = true;
			}

			if(!flag)
			{
				vo.setComment(CharUtils.SQLize(vo.getComment()));
				EditIssueBL bl = new EditIssueBL();
				vo.setUserid(loggedUser);
				vo.setDB(db);
				bl.acceptIssue(vo,loggedUser);
				forward = new ActionForward("/issueEdit.wss"+"?proj="+proj+"&workflowID="+workflowID+"&tc="+tc+"&id="+id,true);
				vo.reset();
			}
			else
			{
				request.setAttribute("actionstring", "accept");
				forward = mapping.findForward("issueAcceptReject");
			}
		}

		if (formBean.getAction().equalsIgnoreCase("reject")) {
			if(id==null || !MiscUtils.isValidIssue(id,db))
			{
				request.setAttribute("WFerr","Bad issue ID :"+id);
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			setPerms(loggedUser, tempRequest,id,workflowID,projectID);
			if(tempRequest.getAttribute("perm_accept_reject")==null)
			{
				request.setAttribute("WFerr","You are not authorized to perform that operation.");
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			boolean flag = false;
			if(vo.getComment()==null || vo.getComment().trim().length()==0)
			{
				request.setAttribute("WFerror","Comments are mandatory.");
				flag=true;
				//vo.setComment("-No Comments Provided-");
			}
			if(vo.getComment()!=null && !CharUtils.isAlNum(vo.getComment()))
			{
				request.setAttribute("WFerror","Comments can only contain alphabets, digits and space characters");
				flag = true;
			}
			if(vo.getComment()!=null && CharUtils.SQLize(vo.getComment()).length() > 1000)
			{
				request.setAttribute("WFerror","Comments must not exceed 1000 characters");
				flag = true;
			}

			if(!flag)
			{
				vo.setComment(CharUtils.SQLize(vo.getComment()));
				EditIssueBL bl = new EditIssueBL();
				vo.setUserid(loggedUser);
				vo.setDB(db);
				bl.rejectIssue(vo,loggedUser);
				forward = new ActionForward("/issueEdit.wss"+"?proj="+proj+"&workflowID="+workflowID+"&tc="+tc+"&id="+id,true);
				vo.reset();
			}
			else
			{
				request.setAttribute("actionstring", "reject");
				forward = mapping.findForward("issueAcceptReject");
			}
					
		}

		if (formBean.getAction().equalsIgnoreCase("complete")) {
			if(id==null || !MiscUtils.isValidIssue(id,db))
			{
				request.setAttribute("WFerr","Bad issue ID :"+id);
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			setPerms(loggedUser, tempRequest,id,workflowID,projectID);
			if(tempRequest.getAttribute("perm_close")==null)
			{
				request.setAttribute("WFerr","You are not authorized to perform that operation.");
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			boolean flag = false;
			if(vo.getComment()==null ||	vo.getComment().trim().length()==0)
			{
				//request.setAttribute("WFerror","Comments are mandatory.");
				//flag=true; disabled for 7.1.1
				vo.setComment("-No Comments Provided-");
			}
			if(vo.getComment()!=null && !CharUtils.isAlNum(vo.getComment()))
			{
				request.setAttribute("WFerror","Comments can only contain alphabets, digits and space characters");
				flag = true;
			}
			if(vo.getComment()!=null &&	CharUtils.SQLize(vo.getComment()).length() > 1000)
			{
				request.setAttribute("WFerror","Comments must not exceed 1000 characters");
				flag = true;
			}

			if(!flag)
			{
				vo.setComment(CharUtils.SQLize(vo.getComment()));
				EditIssueBL bl = new EditIssueBL();
				vo.setUserid(loggedUser);
				vo.setDB(db);
				bl.modifyState(vo, "COMPLETED",loggedUser);
				forward = new ActionForward("/issueEdit.wss"+"?proj="+proj+"&workflowID="+workflowID+"&tc="+tc+"&id="+id,true);
				vo.reset();
			}
			else
			{
				request.setAttribute("actionstring", "complete");
				forward = mapping.findForward("issueClose");
			}
		}
		if (formBean.getAction().equalsIgnoreCase("cancel")) {
			if(id==null || !MiscUtils.isValidIssue(id,db))
			{
				request.setAttribute("WFerr","Bad issue ID :"+id);
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			setPerms(loggedUser, tempRequest,id,workflowID,projectID);
			if(tempRequest.getAttribute("perm_cancel")==null)
			{
				request.setAttribute("WFerr","You are not authorized to perform that operation.");
				forward = mapping.findForward("badURL");
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				return forward;
			}
			boolean flag = false;
			
			if(((EditIssueVO) formBean.getWorkflowObject()).getComment()==null || ((EditIssueVO) formBean.getWorkflowObject()).getComment().trim().length()==0)
			{
				//request.setAttribute("WFerror","Comments are mandatory.");
				//flag =true; disabled for 7.1.1
				vo.setComment("-No Comments Provided-");
			}
			
			if(((EditIssueVO) formBean.getWorkflowObject()).getComment()!=null && !CharUtils.isAlNum(((EditIssueVO) formBean.getWorkflowObject()).getComment()))
			{
				request.setAttribute("WFerror","Comments can only contain alphabets, digits and space characters");
				flag = true;
			}
			if(vo.getComment()!=null && CharUtils.SQLize(vo.getComment()).length() > 1000)
			{
				request.setAttribute("WFerror","Comments must not exceed 1000 characters");
				flag = true;
			}
			if(!flag)
			{
				vo.setComment(CharUtils.SQLize(vo.getComment()));
				EditIssueBL bl = new EditIssueBL();
				vo.setUserid(loggedUser);
				vo.setDB(db);
				bl.modifyState(vo, "CANCELLED",loggedUser);

				forward = new ActionForward("/issueEdit.wss"+"?proj="+proj+"&workflowID="+workflowID+"&tc="+tc+"&id="+id,true);
				
				((EditIssueVO) formBean.getWorkflowObject()).reset();
			}
			else
			{
				request.setAttribute("actionstring", "cancel");
				forward = mapping.findForward("issueClose");
			}
		}

		try{db.close();}catch(Exception e){System.err.println(e);}
		request.removeAttribute("WFdb");
		return forward;
	}

	/**
	 * @param loggedUser
	 * @param request
	 */
	private void setPerms(String u, HttpServletRequest request,String issueID,String workflowID, String projectID) {

		request.removeAttribute("perm_details_view");
		request.removeAttribute("perm_details_view_or_edit");
		request.removeAttribute("perm_close");
		request.removeAttribute("perm_cancel");
		request.removeAttribute("perm_accept_reject");
		request.removeAttribute("perm_comments");
		
		request.setAttribute("perm_details_view"," ");
		request.setAttribute("perm_details_view_or_edit"," ");
		DBAccess db = (DBAccess)request.getAttribute("WFdb");
		if(getIssueStatus(issueID,db).equalsIgnoreCase("COMPLETED"))
			return;
		
		if(getIssueStatus(issueID,db).equalsIgnoreCase("CANCELLED"))
			return;

		if(
				   isSuperAdmin(request) ||
				   isWorkflowAdmin(request) ||
				   isWspaceOwner(request) ||
				   isWspaceMgr(request) ||
				   RoleUtils.isAccoutContact(u,projectID,workflowID) ||
				   RoleUtils.isBkupContact(u,projectID,workflowID) ||
				   RoleUtils.isIssueContact(u,issueID)
		)
		{
			if(getIssueStatus(issueID,db).equalsIgnoreCase("NOT PAST DUE")
			|| getIssueStatus(issueID,db).equalsIgnoreCase("PAST DUE")
			)
			request.setAttribute("perm_close"," ");
				
		}
		if(
				   isSuperAdmin(request) ||
				   isWorkflowAdmin(request) ||
				   isWspaceOwner(request) ||
				   isWspaceMgr(request) ||
				   RoleUtils.isAccoutContact(u,projectID,workflowID) ||
				   RoleUtils.isBkupContact(u,projectID,workflowID) ||
				   RoleUtils.isIssueContact(u,issueID)
		)
		{
			request.setAttribute("perm_cancel"," ");
		}
				
		
		
		if(
		   isSuperAdmin(request) ||
		   isWorkflowAdmin(request) ||
		   isWspaceOwner(request) ||
		   isWspaceMgr(request) ||
		   RoleUtils.isAccoutContact(u,projectID,workflowID) ||
		   RoleUtils.isBkupContact(u,projectID,workflowID) ||
		   RoleUtils.isIssueContact(u,issueID)
		)
		{
			request.removeAttribute("perm_details_view");
			request.setAttribute("perm_details_edit"," ");
			System.out.println("Giving EDIT permission on issue view/edit page to "+u);
			request.setAttribute("perm_details_view_or_edit"," ");
		}
		if(
		   RoleUtils.isIssueOwner(u,issueID) && !RoleUtils.isTouchedOwner(u,issueID)
		  )
		{
			request.setAttribute("perm_accept_reject"," ");
			System.out.println("Giving ACCEPT/REJECT permission on issue view/edit page to "+u);
		}
		if(	isSuperAdmin(request) ||
			isWorkflowAdmin(request) ||
			isWspaceOwner(request) ||
			isWspaceMgr(request) ||
			RoleUtils.isAccoutContact(u,projectID,workflowID) ||
			RoleUtils.isBkupContact(u,projectID,workflowID) ||
			RoleUtils.isIssueContact(u,issueID) ||
			RoleUtils.isAcceptedOwner(u,issueID))
		{
			request.setAttribute("perm_comments"," ");
			System.out.println("Giving Comments permission");
		}
	}

	/**
	 * @param issueVO
	 * @return
	 */
	private ArrayList getValidationErrors(EditIssueVO vo) {
		ArrayList errs = new ArrayList();
		
		System.out.println(vo.getOwners());
		System.out.println(vo.getTitle());
		System.out.println(vo.getDesc());
		
		if(vo.getOwners()==null || vo.getOwners().length==0)
			errs.add("The issue was NOT modified, because you deleted all issue owners.");

		errs.addAll(CharUtils.getStringErrors(vo.getTitle(),80,true,"Title"));
		errs.addAll(CharUtils.getStringErrors(vo.getDesc(),1000,true,"Description"));
		if(vo.getComment()!=null && vo.getComment().trim().length()!=0)
			errs.addAll(CharUtils.getStringErrors(vo.getComment(),1000,true,"Comments"));
		else
			vo.setComment("-No Comments Provided-");
		
		System.out.println("Going for target date checking...");
		boolean y = (vo.getYear()==null || vo.getYear().length==0 || vo.getYear()[0].trim().length()==0);
		boolean m = (vo.getMonth()==null|| vo.getMonth().length==0 || vo.getMonth()[0].trim().length()==0);
		boolean d = (vo.getDay() ==null || vo.getDay().length==0 || vo.getDay()[0].trim().length()==0);
		
		
		
		if((y||m||d)&&!(y && m && d)&&(!y && !m && !d))
		{	
			String e = null;
			e = "Either specify all of Target Year,Month and Day or leave all of them blank.";
			if(y)e += "<br />Target year was NOT specified.";
			if(m)e += "<br />Target month was NOT specified.";
			if(d)e += "<br />Target day was NOT specified.";
			errs.add(e);
		}
		
		if (!y && !m && !d) {

			String year = vo.getYear()[0];
			String month = vo.getMonth()[0];
			String day = vo.getDay()[0];
			
			System.out.println(year);
			System.out.println(month);
			System.out.println(day);
			
			DetailsUtils issDets = new DetailsUtils();
			issDets.setIssueID(vo.getIssueID());
			issDets.extractIssueDetails(vo.getDB().getConnection());
			String cur_tar_date = issDets.getItarget_date();
			String[] temp2= cur_tar_date.split("-");
			String cur_tar_year =temp2[0];
			String cur_tar_month =Integer.toString(Integer.parseInt(temp2[1]));
			String cur_tar_day = temp2[2];
			if(Integer.parseInt(cur_tar_year)==Integer.parseInt(year) && Integer.parseInt(cur_tar_month)==Integer.parseInt(month) && Integer.parseInt(cur_tar_day)==Integer.parseInt(day))
			{
			}
			else{
			Timestamp ts = new Timestamp(Integer.parseInt(year) - 1900, Integer
					.parseInt(month) - 1, Integer.parseInt(day), 0, 0, 0, 0);
			Timestamp cts = new Timestamp(System.currentTimeMillis());
			if(!MiscUtils.isValidDate(year,month,day))
			{
				errs.add("Revised target date is invalid.");
			}else
			{
				boolean isPastDate = true;
				if(ts.getYear() > cts.getYear())
					isPastDate = false;
				if(ts.getYear() == cts.getYear() && ts.getMonth() > cts.getMonth())
					isPastDate = false;
				if(ts.getYear() == cts.getYear() && ts.getMonth() == cts.getMonth() && ts.getDate() >= cts.getDate())
					isPastDate = false;
				if(isPastDate)
					errs.add("Revised target date must be after current date.");
			}
			}
		}
		
		if(errs.size()==0)
		{
			vo.setTitle(CharUtils.SQLize(vo.getTitle()));
			vo.setDesc(CharUtils.SQLize(vo.getDesc()));
			vo.setComment(CharUtils.SQLize(vo.getComment()));
		}
		System.out.println("Validation errors are:");
		for(int i = 0; i < errs.size(); i++) System.out.println(errs.get(i));
		return errs;
	}
	static String getIssueStatus(String iid,DBAccess db)
	{
	
		String state = " ";
		try {
	
		db.prepareDirectQuery("select status from ets.wf_issue where issue_id='"+iid+"' with ur");
		int n = db.execute();
		if(n==0)
		{
			db.close();
			db=null;
			return state;
		}
		state =db.getString(0,0).trim();
	} catch (Exception e) {
		db.doRollback();
		e.printStackTrace();
	}
	return state;	
	}
}
