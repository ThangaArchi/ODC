/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                      */
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


package oem.edge.ets.fe.workflow.sa.initialize;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.brand.ETSUnbrandedProperties;
import oem.edge.ets.fe.workflow.common.Validator;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.CharUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Class       : InitializeAction
 * Package     : oem.edge.ets.fe.workflow.sa.initialize
 * Description : 
 * Date		   : Feb 2, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class InitializeAction extends WorkflowAction {

	private static Log logger = WorkflowLogger.getLogger(InitializeAction.class);
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
										WorkflowForm form,
										HttpServletRequest request,
										HttpServletResponse response)
										throws IOException, ServletException{
		
		if(gatekeeper(mapping, form, request, response)==false)
			return mapping.findForward("badURL");
		request.removeAttribute("WORKFLOW_TITLE");
		InitializeVO vo = null;
		vo = (InitializeVO)form.getWorkflowObject();

		String mode = MiscUtils.getPA(request,"mode");
		
		vo.setMode(mode);
		vo.setProjectID(projectID);
		vo.setCompany(company);
		vo.setLoggedUser(loggedUser);
		
		request.setAttribute("loggedUser",loggedUser);
		request.setAttribute("mode",mode);
		request.setAttribute("proj",projectID);
		request.setAttribute("tc",tc);
		
		if(vo.getMode().equals("new"))
		{
			request.setAttribute("WF_TYPE_TEXT","Self&nbsp;Assessment");
			request.setAttribute("WF_TYPE","SELF ASSESSMENT");
			
			if(!canAccess(request,"SETMET","CREATE"))
			{
				ETSUnbrandedProperties udBranding =  new ETSUnbrandedProperties();
				response.sendRedirect(udBranding.getUnauthorizedURL());
				return null;
			}
			InitializeBL bl = new InitializeBL();
			if(action==null || action.length()==0 || ("submit".equals(action) && "true".equals(MiscUtils.getPA(request,"noreset"))))
			{
				String prepop=request.getParameter("prepop");
				if(prepop!=null)
				{
					String[] previousWF = bl.getPreviousWorkflow(vo.getProjectID());
					if(previousWF!=null && previousWF.length==2)
					{
						vo.reset();
						bl.fillVO(vo, previousWF[0]);
						request.setAttribute("prevWF",previousWF[1]);
					}
				}else
				{
					if("true".equals(MiscUtils.getPA(request,"noreset")))
					{
						bl.fillVOOptions(vo);
						bl.retainClientAttendeeSelection(vo);
					}else
					{
						vo.reset();
						bl.fillVO(vo);	
					}
				}
				return mapping.findForward("initialize");
			}
			if(action.equals("submit"))
			{
				if(inputValidation(vo, request)==false)
				{
					bl.fillVOOptions(vo);
					bl.retainClientAttendeeSelection(vo);
					return mapping.findForward("initialize");
				}

				bl.createNewQBR(vo);
				request.setAttribute("isNewWorkflow","true");
				request.setAttribute("workflowID",vo.getWorkflowID());
				setCanProceed(request);
				setNecessaryFields(request,vo);
				vo.reset();
				return mapping.findForward("initializeCnf");
			}
		}
		
		if(vo.getMode().equals("edit"))
		{
			if(!canAccess(request,"SETMET","EDIT"))
			{
				ETSUnbrandedProperties udBranding =  new ETSUnbrandedProperties();
				response.sendRedirect(udBranding.getUnauthorizedURL());
				return null;
			}
			InitializeBL bl = new InitializeBL();
			if(action==null || action.length()==0 || ("submit".equals(action) && "true".equals(MiscUtils.getPA(request,"noreset"))))
			{
				if("true".equals(MiscUtils.getPA(request,"noreset")))
				{
					bl.fillVOOptions(vo);
					bl.retainClientAttendeeSelection(vo);
				}else
				{
					vo.reset();
					bl.fillVO(vo,vo.getWorkflowID());
				}
				return mapping.findForward("initialize");
			}
			if(action.equals("submit"))
			{
				if(inputValidation(vo, request)==false)
				{
					bl.fillVOOptions(vo);
					bl.retainClientAttendeeSelection(vo);
					return mapping.findForward("initialize");
				}
				bl.updateQBR(vo);
				request.setAttribute("isOldWorkflow","true");
				request.setAttribute("workflowID",vo.getWorkflowID());
				setCanProceed(request);
				setNecessaryFields(request,vo);
				vo.reset();
				return mapping.findForward("initializeCnf");
			}
		}
		return mapping.findForward("badURL"); //it isn't supposed to come here if things are fine
	}

	private boolean gatekeeper(ActionMapping mapping, WorkflowForm form, HttpServletRequest request, HttpServletResponse response) {
		
		InitializeVO vo = null;
		if(form==null || mapping==null || request==null || response==null)
		{
			request.setAttribute("WFerr", "Form is null");
			return false;
		}
		
		vo = (InitializeVO)form.getWorkflowObject();
		if(vo==null)
		{
			request.setAttribute("WFerr", "vo is null");
			return false;
		}
		
		String workflowID = MiscUtils.getPA(request,"workflowID");
		vo.setWorkflowID(workflowID);
		if(projectID==null)
		{
			request.setAttribute("WFerr", "No project ID");
			return false;
		}
		if(action!=null && !action.equals("submit") && action.length()!=0)
		{
			request.setAttribute("WFerr", "Invalid action");
			return false;
		}
		
		String mode = MiscUtils.getPA(request,"mode");
		if(mode==null || !(mode.equals("new") || mode.equals("edit")))
		{
			request.setAttribute("WFerr", "Bad mode");
			return false;
		}
		if(mode.equals("edit") && workflowID==null && vo.getWorkflowID()==null)
		{
			request.setAttribute("WFerr", "No workflow ID");
			return false;
		}
		if(vo.getWorkflowID()==null)vo.setWorkflowID(workflowID); //workflowID as parameter comes when user clicks on link; subsequently from VO
		
		if(mode.equals("edit"))
		{
			//TODO: Checks for whether this is a valid SA workflow & entitlement checks
		}
		if(mode.equals("new"))
		{
			//TODO: Checks for whether this is a valid SA workflow & entitlement checks
			if(!MiscUtils.canCreateSA(projectID))
			{
				request.setAttribute("WFerr", "There can be only one SA open at a time in a project");
				return false;
			}
		}
		if(company==null)
		{
			request.setAttribute("WFerr","No company");
			return false;
		}
		return true;

	}
	private boolean inputValidation(InitializeVO vo, HttpServletRequest request)
	{
		ArrayList errs = new ArrayList();
		if(vo.getAccountContact()==null || vo.getAccountContact().length==0)
			errs.add("Please select an Account contact");
		if(vo.getAttendees()==null || vo.getAttendees().length==0)
			errs.add("Please select client attendees");
		if(vo.getBackupContact()==null || vo.getBackupContact().length==0)
			errs.add("Please select a Backup contact");
		/*if(vo.getExecSponsor()==null || vo.getExecSponsor().length==0)
			errs.add("Please select an Executive sponsor");*/
		if(vo.getNsiRating()==null || vo.getNsiRating().length==0)
			errs.add("Please select an NSI rating");
		/*if(vo.getPlannedDay()==null || vo.getPlannedDay().length==0)
			errs.add("Please select a day for the planned meeting date");
		if(vo.getPlannedMonth()==null || vo.getPlannedMonth().length==0)
			errs.add("Please select a month for the planned meeting date");
		if(vo.getPlannedYear()==null || vo.getPlannedYear().length==0)
			errs.add("Please select a year for the planned meeting date");*/
		if(vo.getQbrQuarter()==null || vo.getQbrQuarter().length==0)
			errs.add("Please select a quarter for the Self Assessment");
		if(vo.getQbrYear()==null || vo.getQbrYear().length==0)
			errs.add("Please select a year for the Self Assessment");
		/*if(!MiscUtils.isValidDate(vo.getPlannedYear()[0],vo.getPlannedMonth()[0],vo.getPlannedDay()[0]))
			errs.add("Please select a valid planned meeting date.");*/
		if(vo.getMeetingLocation()!=null)
			errs.addAll(CharUtils.getStringErrors(vo.getMeetingLocation(),20,false,"Meeting location"));
		String mode = MiscUtils.getPA(request,"mode");
		if("edit".equals(mode) && !(request.getAttribute("CURRENT_STAGE")==null
				||((String)request.getAttribute("CURRENT_STAGE")).trim().length()==0
				||((String)request.getAttribute("CURRENT_STAGE")).equalsIgnoreCase(WorkflowConstants.IDENTIFY)))
		{
			if(vo.getIbmAttendees()==null || vo.getIbmAttendees().length==0)
				errs.add("Please select IBM attendees");
			if(vo.getMeetingLocation()==null || vo.getMeetingLocation().trim().length()==0)
				errs.add("Please provide the meeting location");
		}
		//TODO: check if the ibm attendees, account contact, backup and exec sponsor provided are valid and eligible
		//TODO: check if the client attendees provided are valid
		//TODO: check if the NSI rating is valid
		//TODO: check if the year and quarter are valid
		boolean proceedParsingBiweekly = false;
		/*if(vo.getBiweeklyFlag()==null||vo.getBiweeklyFlag().length==0||!(vo.getBiweeklyFlag()[0].equals("Y")||vo.getBiweeklyFlag()[0].equals("N")) )
		{
			errs.add("Please select a biweekly flag.");
			proceedParsingBiweekly = false;
		}
		if(proceedParsingBiweekly && vo.getBiweeklyFlag()[0].equals("Y"))
		{
			if(!checkAgainstNullDate(vo.getBiweeklyYear(),vo.getBiweeklyMonth(),vo.getBiweeklyDay(),"biweekly review date",errs)){
				proceedParsingBiweekly = false;
			}
			if(proceedParsingBiweekly)
			{	
				String currentBiweeklyFlag = "N";
				String currentBiweeklyDate = null;
				if("edit".equals(mode))
				{
					String[] currentBiweeklyDateAndFlag = getBiweeklyDateAndFlag(vo.getWorkflowID());
					currentBiweeklyDate = currentBiweeklyDateAndFlag[0];
					currentBiweeklyFlag = currentBiweeklyDateAndFlag[1];
				}
				if("edit".equals(mode) && currentBiweeklyFlag.equals("Y"))
				{
					guardAgainstPastDateEntry(currentBiweeklyDate,vo.getBiweeklyYear()[0],vo.getBiweeklyMonth()[0],vo.getBiweeklyDay()[0],errs,"Bi-weekly review date");
				}else
				{
					if(!MiscUtils.isValidDate(vo.getBiweeklyYear()[0],vo.getBiweeklyMonth()[0],vo.getBiweeklyDay()[0])){
						errs.add("The bi-weekly review date specified is invalid");
					}
					else
					{
						guardAgainstPastDateEntry(null,vo.getBiweeklyYear()[0],vo.getBiweeklyMonth()[0],vo.getBiweeklyDay()[0],errs,"Bi-weekly review date",false);
					}
					
				}
			}
			if(vo.getBiweeklyStatus()==null || vo.getBiweeklyStatus().length==0
			|| !( vo.getBiweeklyStatus()[0].equals("Complete")
				||vo.getBiweeklyStatus()[0].equals("Cancelled")
				||vo.getBiweeklyStatus()[0].equals("Reviewed")
				||vo.getBiweeklyStatus()[0].equals("Skipped")
				)
			)
				errs.add("Please select a valid bi-weekly status");
		}*/
		if(checkAgainstNullDate(vo.getPlannedYear(),vo.getPlannedMonth(),vo.getPlannedYear(),"Planned meeting date",errs))
		{
			if("edit".equals(mode))
			{
				guardAgainstPastDateEntry(getPlannedMeetingDate(vo.getWorkflowID()),vo.getPlannedYear()[0],vo.getPlannedMonth()[0],vo.getPlannedDay()[0],errs,"planned meeting date");
			}
			else{
				guardAgainstPastDateEntry(null,vo.getPlannedYear()[0],vo.getPlannedMonth()[0],vo.getPlannedDay()[0],errs,"planned meeting date",false);
			}
		}
		
		/*String currentRatingFromDate = null;
		String currentRatingToDate = null;
		boolean isRatingPeriodSpecified = true;
		if(checkAgainstNullDate(vo.getRatingFromYear(),vo.getRatingFromMonth(),vo.getRatingFromYear(),"Rating Period \"From\" date",errs))
		{
			if("edit".equals(mode))
			{
				String[] ratingPeriod = getRatingPeriod(vo.getWorkflowID());
				currentRatingFromDate = ratingPeriod[0];
				currentRatingToDate = ratingPeriod[1];
				int errsLength = errs.size(); 
				guardAgainstPastDateEntry(currentRatingFromDate,vo.getRatingFromYear()[0],vo.getRatingFromMonth()[0],vo.getRatingFromDay()[0],errs,"Rating Period \"From\" date");
				if(errs.size() > errsLength)
					isRatingPeriodSpecified = false;
			}
			else{
				int errsLength = errs.size(); 
				guardAgainstPastDateEntry(null,vo.getRatingFromYear()[0],vo.getRatingFromMonth()[0],vo.getRatingFromDay()[0],errs,"Rating Period \"From\" date",false);
				if(errs.size() > errsLength)
					isRatingPeriodSpecified = false;
			}
		}
		else
		{
			isRatingPeriodSpecified = false;
		}
		if(checkAgainstNullDate(vo.getRatingToYear(),vo.getRatingToMonth(),vo.getRatingToYear(),"Rating Period \"To\" date",errs))
		{
			if("edit".equals(mode))
			{
				int errsLength = errs.size();
				guardAgainstPastDateEntry(currentRatingToDate,vo.getRatingToYear()[0],vo.getRatingToMonth()[0],vo.getRatingToDay()[0],errs,"Rating Period \"To\" date");
				if(errs.size() > errsLength)
					isRatingPeriodSpecified = false;
			}
			else{
				int errsLength = errs.size();
				guardAgainstPastDateEntry(null,vo.getRatingToYear()[0],vo.getRatingToMonth()[0],vo.getRatingToDay()[0],errs,"Rating Period \"To\" date", false);
				if(errs.size() > errsLength)
					isRatingPeriodSpecified = false;
			}
		}
		else
		{
			isRatingPeriodSpecified = false;
		}
		if(isRatingPeriodSpecified)
		{
			Timestamp fromTS = new Timestamp(Integer.parseInt(vo.getRatingFromYear()[0]) - 1900, Integer
					.parseInt(vo.getRatingFromMonth()[0]) - 1, Integer.parseInt(vo.getRatingFromDay()[0]), 0, 0, 0, 0);
			Timestamp toTS = new Timestamp(Integer.parseInt(vo.getRatingToYear()[0]) - 1900, Integer
					.parseInt(vo.getRatingToMonth()[0]) - 1, Integer.parseInt(vo.getRatingToDay()[0]), 0, 0, 0, 0);
			if(fromTS.after(toTS))
				errs.add("The Rating Period \"To\" date must be in the future of the \"From\" date");
		}*/
		/*
		 * Timestamp ts = new Timestamp(Integer.parseInt(newYear) - 1900, Integer
				.parseInt(newMonth) - 1, Integer.parseInt(newDay), 0, 0, 0, 0);
		
		 */
		if(errs.size()!=0)
		{
			request.setAttribute("WFerror",errs);
			return false;
		}
		return true;
	}

	private String[] getBiweeklyDateAndFlag(String workflowID) {
		String[] biweeklyDateAndFlag = new String[]{"2004-01-01","N"};
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery("select biweekly_date, biweekly_flag from ets.wf_stage_identify_setmet where wf_id='"+workflowID+"' with ur");
			if(db.execute()==1)
			{
				String temp = db.getString(0,0);
				if(temp!=null && temp.trim().length()!=0)
					biweeklyDateAndFlag[0] =  temp;
				temp = db.getString(0,1);
				if(temp!=null && temp.trim().length()!=0)
					biweeklyDateAndFlag[1] =  temp;
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return biweeklyDateAndFlag;
	}
	private String getPlannedMeetingDate(String workflowID) {
		String plannedMeetingDate = "2004-01-01";
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery("select b.start_time from ets.wf_def a, ets.ets_calendar b where a.meeting_id=b.calendar_id and a.wf_id='"+workflowID+"' with ur");

			if(db.execute()==1)
			{
				java.util.Date dt = (Date) db.getObject(0,0);
				if(dt!=null)
				{
					String temp = dt.toString();
					if(temp!=null && temp.trim().length()>=10)
						plannedMeetingDate =  temp.substring(0,10);
				}
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return plannedMeetingDate;
	}
	private String[] getRatingPeriod(String workflowID) {
		String[] ratingPeriod = {"2004-01-01","2004-01-01"};
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery("select rating_period_from, rating_period_to from ets.wf_stage_identify_setmet where wf_id='"+workflowID+"' with ur");

			if(db.execute()==1)
			{
				String temp = db.getString(0,0);
				if(temp!=null && temp.trim().length()!=0)
					ratingPeriod[0] =  temp;
				temp = db.getString(0,1);
				if(temp!=null && temp.trim().length()!=0)
					ratingPeriod[1] =  temp;
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return ratingPeriod;
	}
	private void setNecessaryFields(HttpServletRequest request, InitializeVO vo) {
		System.out.println("Enter setNecessaryFields");
		ArrayList necessaryFieldList = new ArrayList();
		Validator validator = Validator.getInstance();

		//validator.validateStage(vo,necessaryFieldList);
		tempValidate(vo,necessaryFieldList); //this is used in lieu of validator.validateStage as a temp patch 
		
		String necessaryFields = "";
		if(necessaryFieldList.size()==1)
			necessaryFields = (String)necessaryFieldList.get(0);
		else
			if(necessaryFieldList.size()>1)
			{
				necessaryFields = (String)necessaryFieldList.get(0);
				for(int i=1; i<necessaryFieldList.size()-2;i++)
					necessaryFields += ", "+(String)necessaryFieldList.get(i);
				necessaryFields += " and "+(String)necessaryFieldList.get(necessaryFieldList.size()-1);
			}
		if(necessaryFieldList.size()!=0)
			request.setAttribute("necessaryFields", necessaryFields);
		else
			System.out.println("Validator passed.");
	}

	/**
	 * This is a temporary patch for a broken Validator.
	 */
	private void tempValidate(InitializeVO vo, ArrayList necessaryFieldList) {
		
		if(vo.getMeetingLocation()==null || vo.getMeetingLocation().trim().length()==0)
			necessaryFieldList.add("Meeting location");
		if(vo.getIbmAttendees()==null || vo.getIbmAttendees().length ==0)
			necessaryFieldList.add("IBM attendees");
	}

	private void setCanProceed(HttpServletRequest request) {
		System.out.println("Enter setCanProceed");
		if(request.getAttribute("CURRENT_STAGE")==null
				||((String)request.getAttribute("CURRENT_STAGE")).trim().length()==0
			||((String)request.getAttribute("CURRENT_STAGE")).equalsIgnoreCase(WorkflowConstants.IDENTIFY))
			request.setAttribute("canProceed", "true");
		else
			System.out.println("Disable Continue to Prepare button. Current stage is "+request.getAttribute("CURRENT_STAGE"));
	}
	private void guardAgainstPastDateEntry(String oldDate, String newYear, String newMonth, String newDay, ArrayList errs, String fieldName)
	{
		guardAgainstPastDateEntry(oldDate, newYear, newMonth, newDay, errs, fieldName, true);
	}
	private void guardAgainstPastDateEntry(String oldDate, String newYear, String newMonth, String newDay, ArrayList errs, String fieldName, boolean checkForDateChange)
	{
		if(!checkForDateChange) oldDate = "1984-11-14";
		String[] temp2= oldDate.split("-");
		String oldYear =temp2[0];
		String oldMonth =Integer.toString(Integer.parseInt(temp2[1]));
		String oldDay = temp2[2];
		if(checkForDateChange && Integer.parseInt(oldYear)==Integer.parseInt(newYear) && Integer.parseInt(oldMonth)==Integer.parseInt(newMonth) && Integer.parseInt(oldDay)==Integer.parseInt(newDay))
		{
		}
		else{
		Timestamp ts = new Timestamp(Integer.parseInt(newYear) - 1900, Integer
				.parseInt(newMonth) - 1, Integer.parseInt(newDay), 0, 0, 0, 0);
		Timestamp cts = new Timestamp(System.currentTimeMillis());
		if(!MiscUtils.isValidDate(newYear,newMonth,newDay))
		{
			errs.add(fieldName+" is invalid.");
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
				errs.add(fieldName+" must be after current date.");
		}
		}
	}
	private boolean checkAgainstNullDate(String[] year, String[] month, String[] day, String fieldName,ArrayList errs)
	{
		if(year==null||year.length==0
				|| month==null||month.length==0
				|| day==null||day.length==0
			){
				errs.add("Please select a "+fieldName);
				return false;
		}
		return true;
	}
}

