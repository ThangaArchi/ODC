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

package oem.edge.ets.fe.workflow.eventdetailspopupwindow;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import oem.edge.ets.fe.workflow.util.CharUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;

/**
 * Class       : WorkflowEventDetailsAction
 * Package     : oem.edge.ets.fe.workflow.eventdetailspopupwindow
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class WorkflowEventDetailsAction extends WorkflowAction

{

	private static Log logger = WorkflowLogger.getLogger(WorkflowEventDetailsAction.class);
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping, WorkflowForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    
       ActionForward forward = null;
       WorkflowEventDetailsBean workflowEventDetailsBean = null;
       WorkflowEventDetailsVO vo = null;
       EventDetailsPopupWindowPreload preloadBean = null;
       
       if(form==null)
		System.out.println("PANIC: FORM NULL");
       
       String workflowID = MiscUtils.getPA(request,"workflowID");
       if(form.getAction()==null && !MiscUtils.isValidWorkflow(projectID,workflowID))
       		return mapping.findForward("badURL");
       
       request.setAttribute("workflowID",workflowID);
       
       workflowEventDetailsBean = (WorkflowEventDetailsBean) form;
       vo=(WorkflowEventDetailsVO)form.getWorkflowObject();
       
       if (form.getAction() == null) {
       	workflowEventDetailsBean.reset();
       	preloadBean = new EventDetailsPopupWindowPreload(projectID,vo,null);
		request.setAttribute("preloadBean",preloadBean);
       	forward = mapping.findForward("/eventDetailsPopup");
       }
       else{
		if(vo==null)
			System.out.println("PANIC: VO NULL");
		
		vo.setProjectID(projectID);
		//vo.setWorkflowID(workflowID);
		if(!MiscUtils.isValidWorkflow(projectID,vo.getWorkflowID()))
       		return mapping.findForward("badURL");
       
		ArrayList errs = getValidationErrors(vo);
		if (errs != null && errs.size() != 0) {
			request.setAttribute("errorMessages", errs);
			forward = mapping.findForward("/eventDetailsPopup");
		}
		else {
			EventDetailsBL bl = new EventDetailsBL();
			if (!bl.setEventDetails(vo,loggedUser)) {
				System.out.println("Failure not handled but BL returned false.");
				forward = mapping.findForward("failure");
			} else {
				forward = mapping.findForward("/eventDetailsPopup");
				System.out.println("Set forward to " + forward);
				request.setAttribute("close_signal", " ");
			}
		}
       }
       return (forward);
	}
	
	private ArrayList getValidationErrors(WorkflowEventDetailsVO vo) {
		ArrayList errs = new ArrayList();
		if(vo.getTitle()==null || vo.getTitle().trim().length()==0)
			errs.add("Title must not be empty");
		if(vo.getTitle()==null || CharUtils.isAlNum(vo.getTitle())==false)
			errs.add("Title can only contain alphabets, digits and space characters");
		if(vo.getDesc()!=null && vo.getDesc().length()>1000)
			errs.add("Description must not exceed 1000 characters");
		if(vo.getDesc()==null || vo.getDesc().trim().length()==0)
			errs.add("Description must not be empty");
		if(vo.getDesc()!=null && CharUtils.isAlNum(vo.getDesc())==false)
			errs.add("Description can only contain alphabets, digits and space characters");
		if(vo.getAmpm()==null || vo.getAmpm().length==0 ||vo.getAmpm()[0].trim().length()==0)
			errs.add("You must specify one of AM/PM for the Event time");
		if(vo.getMonth()==null || vo.getMonth().length==0 ||vo.getMonth()[0].trim().length()==0)
			errs.add("Month must be specified for the Event date");
		if(vo.getYear()==null || vo.getYear().length==0 ||vo.getYear()[0].trim().length()==0)
			errs.add("Year must be specified for the Event date");
		if(vo.getDay()==null || vo.getDay().length==0 ||vo.getDay()[0].trim().length()==0)
			errs.add("Day must be specified for the Event date");
		if(vo.getHour()==null || vo.getHour().length==0 ||vo.getHour()[0].trim().length()==0)
			errs.add("Hour must be specified for the Event time");
		if(vo.getMin()==null || vo.getMin().length==0 ||vo.getMin()[0].trim().length()==0)
			errs.add("Minute must be specified for the Event time");
		if(vo.getRepeatsFor()==null || vo.getRepeatsFor().length==0 ||vo.getRepeatsFor()[0].trim().length()==0)
			errs.add("\"Repeats For\" is required");
		if(vo.getTeamMembers()==null || vo.getTeamMembers().length==0 ||vo.getTeamMembers()[0].trim().length()==0)
			errs.add("Atleast one member must be selected");
		//
		boolean sendEmail = false;
		if (vo.getNotifyEmail() != null && vo.getNotifyEmail().trim().equals("on")) {
			if(vo.getEmailOption() == null)
				errs.add("If you wish to send a notification mail, you must specify either the TO or BCC option");
			if(vo.getTeamMembers().length == 0)
				errs.add("If you wish to send a notification mail, you must specify the person(s) to whom it is to be sent");
		}
	
		String year = vo.getYear()[0];
		String month = vo.getMonth()[0];
		String day = vo.getDay()[0];
		String minute = vo.getMin()[0];
		String hour = vo.getHour()[0];
		String ampm = vo.getAmpm()[0];
		int hour24 = 0;
		if(ampm.equals("am"))
			hour24=Integer.parseInt(hour);
		else
			hour24 = Integer.parseInt(hour)+12;
		if(hour24==24)hour24=0;
		Timestamp ts = new Timestamp(Integer.parseInt(year)-1900,Integer.parseInt(month)-1,Integer.parseInt(day),hour24,Integer.parseInt(minute),0,0);
		Timestamp cts = new Timestamp(System.currentTimeMillis());
		if(!MiscUtils.isValidDate(year,month,day))
		{
			errs.add("Event date is invalid.");
		}else{
		boolean isPastDate = true;
		if(ts.getYear() > cts.getYear())
			isPastDate = false;
		if(ts.getYear() == cts.getYear() && ts.getMonth() > cts.getMonth())
			isPastDate = false;
		if(ts.getYear() == cts.getYear() && ts.getMonth() == cts.getMonth() && ts.getDate() >= cts.getDate())
			isPastDate = false;
		if(isPastDate)
			errs.add("Event date should not be a date in the past.");
		}
		

		if(errs.size()==0)
		{
			vo.setTitle(CharUtils.SQLize(vo.getTitle()));
			vo.setDesc(CharUtils.SQLize(vo.getDesc()));
		}
		
		return errs;
	}
	
	
	private String lookupEmail(String memberID)
	{
		int imemberID = Integer.parseInt(memberID);
		switch(imemberID)
		{
		
		case 1:
			return "k.p.achar@in.ibm.com";
		case 2:
			return "kesavankutty@in.ibm.com";
		case 3:
			return "sgovindaraj@in.ibm.com";
		case 4:
			return "ryazshai@in.ibm.com";
		default:
			return "";
			
		}
	}
}
