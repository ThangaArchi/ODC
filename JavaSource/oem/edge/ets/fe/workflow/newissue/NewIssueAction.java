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

package oem.edge.ets.fe.workflow.newissue;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.dao.DBAccess;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.CharUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;

/**
 * Class : NewIssueAction
 * Package : oem.edge.ets.fe.workflow.newissue
 * Description :
 * 
 * @author Pradyumna Achar
 */
public class NewIssueAction extends WorkflowAction {

	private static Log logger = WorkflowLogger.getLogger(NewIssueAction.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping,
	 *      oem.edge.ets.fe.workflow.core.WorkflowForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,	WorkflowForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		ActionForward forward = null;
		NewIssueFormBean formBean = null;
		NewIssueVO vo = null;
		
		if(form==null)
			System.out.println("PANIC: FORM NULL");
		
		formBean = (NewIssueFormBean) form;
		
		NewIssueBL bl = new NewIssueBL();
		
		

		String proj = request.getParameter("proj");
		String tc = request.getParameter("tc");
		String workflowID = request.getParameter("workflowID");
		request.setAttribute("proj", proj);
		request.setAttribute("tc", tc);
		request.setAttribute("workflowID", workflowID);

		MiscUtils.setDB(request);
		DBAccess db = (DBAccess)request.getAttribute("WFdb");

		
		///////////////////////////////////////////////////////////////////////////////////////
		boolean badFlag = false;
		if(projectID == null || !MiscUtils.isValidProject(projectID,db))
		{
			request.setAttribute("WFerr","Bad project ID");
			forward = mapping.findForward("badURL");
			badFlag = true;
			
		}
		else
		{
			if(workflowID == null || !MiscUtils.isValidWorkflow(projectID, workflowID,db))
			{
				request.setAttribute("WFerr","Bad workflow ID");
				forward = mapping.findForward("badURL");
				badFlag = true;
				
			}
			
		}
		if(tc == null)
		{
			request.setAttribute("WFerr","Bad top category");
			forward = mapping.findForward("badURL");
			badFlag = true;
			
		}
		if(badFlag == true)
		{
			try{db.close();}catch(Exception e){System.err.println(e);}
			request.removeAttribute("WFdb");
			return forward;
		}
		/////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		vo = (NewIssueVO) formBean.getWorkflowObject();
		if(vo==null)
			System.out.println("PANIC: VO NULL");
		vo.setProjectID(projectID);
		vo.setWorkflowID(request.getParameter("workflowID"));
		vo.setDB(db);
		
		
		
		if (formBean.getAction() == null || formBean.getAction().length() == 0) {

			if (bl.isIssueQuotaExhausted(wf_type,vo)) {
				forward = new ActionForward("/issuelist.wss" + "?proj=" + proj
						+ "&workflowID=" + workflowID + "&tc=" + tc+"&extend=true", true);
				ArrayList errs = new ArrayList();
				errs.add("A new issue cannot be created because a Set/Met can have a maximum of five issues.");
				request.getSession().setAttribute("errorMessages", errs);
				
				try{db.close();}catch(Exception e){System.err.println(e);}
				request.removeAttribute("WFdb");
				
				return forward;
			}
			
			forward = mapping.findForward("newIssue");
			request.setAttribute("action", "submit");
			request.setAttribute("preloadBean",new NewIssuePreload(projectID));

			return forward;
		}
		
		if (formBean.getAction() != null && formBean.getAction().equalsIgnoreCase("finish")) {
			forward = new ActionForward("/issuelist.wss" + "?proj=" + proj
					+ "&workflowID=" + workflowID + "&tc=" + tc, true);
		}
		
		if (formBean.getAction() != null && formBean.getAction().equalsIgnoreCase("submit")) {
			if (bl.isIssueQuotaExhausted(wf_type,vo)) {
				forward = new ActionForward("/issuelist.wss" + "?proj=" + proj
						+ "&workflowID=" + workflowID + "&tc=" + tc+"&extend=true", true);
				ArrayList errs = new ArrayList();
				errs.add("A new issue cannot be created because a Set/Met can have a maximum of five issues.");
				request.getSession().setAttribute("errorMessages", errs);
			}
			else {
				ArrayList errs = getValidationErrors(vo);
				if(!canAccess(request,"ISSUES","CREATE"))
					errs.add("You are not authorized to create new Issues in this Set/Met");
				if (errs != null && errs.size() != 0) {
					request.setAttribute("errorMessages", errs);
					forward = mapping.findForward("newIssue");
					
				}
				else {
					if (!bl.saveNewIssue(vo, loggedUser, tc)) {
						System.out.println("Failure not handled but BL returned false.");
						forward = mapping.findForward("failure");
					} else {
						forward = mapping.findForward("newIssueCnf");
						request.setAttribute("action", null);

						ArrayList peopleNotified = new ArrayList();
						if (vo.getNotifyOption() != null) {
							for (int i = 0; i < vo.getNotificationList().length; i++)
								peopleNotified.add(bl.getName(vo.getNotificationList()[i]));
						}
						request.setAttribute("notifiedPeople", peopleNotified);
						request.setAttribute("AccountContactPlusOwner",bl.acOwner);
						vo.reset();
					}
				}
			}
		}
		try{db.close();}catch(Exception e){System.err.println(e);}
		request.removeAttribute("WFdb");
		return forward;
	}

	/**
	 * @param vo
	 * @return
	 */
	private ArrayList getValidationErrors(NewIssueVO vo) {
		ArrayList errs = new ArrayList();
		if(vo.getFocalPointID()==null || vo.getFocalPointID().trim().length()==0)
			errs.add("A focal point must be selected");
		errs.addAll(CharUtils.getStringErrors(vo.getTitle(),80,true,"Title"));
		errs.addAll(CharUtils.getStringErrors(vo.getDesc(),1000,true,"Description"));
		if(vo.getNotifyOption()!=null && (vo.getNotificationList()==null || vo.getNotificationList().length==0 ||vo.getNotificationList()[0].trim().length()==0))
			errs.add("If you choose the notification option, you must select some people to be notified as well.");
		if(vo.getOwnerID()==null || vo.getOwnerID().length==0 ||vo.getOwnerID()[0].trim().length()==0)
			errs.add("Atleast one owner must be assigned.");
		if(vo.getIssueTypeID()==null || vo.getIssueTypeID().trim().length()==0)
			errs.add("Please select an Issue Type.");
		if(vo.getIssueCategory()==null || vo.getIssueCategory().trim().length()==0)
			errs.add("Please select an Issue Category");
		
		boolean y = (vo.getYear()==null || vo.getYear().length==0 || vo.getYear()[0].trim().length()==0);
		boolean m = (vo.getMonth()==null|| vo.getMonth().length==0 || vo.getMonth()[0].trim().length()==0);
		boolean d = (vo.getDay() ==null || vo.getDay().length==0 || vo.getDay()[0].trim().length()==0);
		if(y||m||d)
		{	
			if(y)errs.add("Target year was NOT specified.");
			if(m)errs.add("Target month was NOT specified");
			if(d)errs.add("Target day was NOT specified.");
		}
		if (!y && !m && !d) {

			String year = vo.getYear()[0];
			String month = vo.getMonth()[0];
			String day = vo.getDay()[0];
			System.out.println(year);
			System.out.println(month);
			System.out.println(day);
			Timestamp ts = new Timestamp(Integer.parseInt(year) - 1900, Integer
					.parseInt(month) - 1, Integer.parseInt(day), 0, 0, 0, 0);
			Timestamp cts = new Timestamp(System.currentTimeMillis());
			if(!MiscUtils.isValidDate(year,month,day))
				errs.add("Target date is invalid.");
			else{
				boolean isPastDate = true;
				if(ts.getYear() > cts.getYear())
					isPastDate = false;
				if(ts.getYear() == cts.getYear() && ts.getMonth() > cts.getMonth())
					isPastDate = false;
				if(ts.getYear() == cts.getYear() && ts.getMonth() == cts.getMonth() && ts.getDate() >= cts.getDate())
					isPastDate = false;
				if(isPastDate)
					errs.add("Target date must be after current date.");
			}
					
		}
		if(errs.size()==0)
		{
			vo.setTitle(CharUtils.SQLize(vo.getTitle()));
			vo.setDesc(CharUtils.SQLize(vo.getDesc()));
			System.out.println("Title: "+vo.getTitle());
			System.out.println("Desc: "+vo.getDesc());
			
		}
		return errs;
	}

}
