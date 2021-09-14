/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2007                                     */
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



/*
 * Created on Sep 11, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.workflow.common.Validator;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author ryazuddin
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SetMetIdentifyCreateAction extends WorkflowAction {
	
	private static Log logger	=		WorkflowLogger.getLogger(SetMetIdentifyCreateAction.class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping,
	 *      oem.edge.ets.fe.workflow.core.WorkflowForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		
		HttpSession sessionobj = request.getSession(true);
		SetMetIdentifyStageForm iForm = (SetMetIdentifyStageForm) form;
		SetMetIdentifyStageObject object = 	new SetMetIdentifyStageObject();

		SetMetBL businessLogic = new SetMetBL();
        		
		object.setProjectID(projectID);
		object.setRequestor(requestor);
		object.setAcctContact(loggedUser);
		object.setLastUsr(loggedUser);
		object.setClientName(company);
		System.out.println("action:"+iForm.getAction());
		   if (iForm.getAction().equalsIgnoreCase("newsetmet")) {
		   	
		   	request.setAttribute("SECTOR",SetMetBL.getSector(projectID));
		   	
			if(canAccess(request,"SETMET","CREATE"))
		   		 request.setAttribute("ACCESS_DENIED","false");
		   	else
		   	     request.setAttribute("ACCESS_DENIED","true");
            
		   	ArrayList getAccntByroles = new ArrayList();
			ArrayList attendees = new ArrayList();
			ArrayList actContact = new ArrayList();
			ArrayList bkAccountcontact = new ArrayList();
			ArrayList ibmlist = new ArrayList();
			ArrayList sponsor = new ArrayList();
			ArrayList selectedclientlist = new ArrayList();
			//Populating the data into the setmet page

			getAccntByroles = businessLogic.getAccntcontByroles(projectID);
			attendees = businessLogic.getClient_Attendees(company);
			actContact = businessLogic.getIBM_Attendees(projectID);
			//ibmlist = businessLogic.getIBM_Attendees("2");
			//sponsor = businessLogic.getExecutive_Sponsor("2");
			//bkAccountcontact = businessLogic.getAccount_Contact("2");

			iForm.setAttendees(attendees);
			iForm.setAcctList(getAccntByroles);
			iForm.setIbmerList(actContact);
			iForm.setSponsor(actContact);
			iForm.setDelegateList(getAccntByroles);
			iForm.setMeetingAttendees(selectedclientlist);
			object.setPlDate(getToday('D'));
			object.setPlMon(getToday('M'));
			object.setPlYear(getToday('Y'));
			object.setBiWeeklyDt(getToday('D'));
			object.setBiWeeklyMon(getToday('M'));
			object.setBiWeeklyYr(getToday('Y'));
			object.setYear(getToday('Y'));
			request.setAttribute("loggedUser",loggedUser);
			iForm.setWorkflowObject(object);
		}

		if (iForm.getAction().equalsIgnoreCase("editsetmet")) {
			String workflowID = request.getParameter("workflowID");
			System.out.println("enter editsetmet");
			/* Added for 7.1.1 pre-populate button by KP */
			if(request.getParameter("prepop")!=null)
			{
				String[] temp = (new SetMetDAO()).getPreviousWorkflow(projectID);
				iForm.setAction("newsetmet");
				if(temp!=null)
				{
				workflowID = temp[0];
				request.setAttribute("prevWF",temp[1]);
				}
				else{
					System.out.println("getting into recursion; action="+iForm.getAction());
					return executeWorkflow(mapping,form,request,response);
				}
			} //Addition for 7.1.1 ends
			request.setAttribute("SECTOR",SetMetBL.getSector(projectID));
			if(canAccess(request,"SETMET","EDIT"))
				 request.setAttribute("ACCESS_DENIED","false");
			else
				 request.setAttribute("ACCESS_DENIED","true");
			
			/*HashMap setmet = new HashMap();
			setmet = (HashMap) sessionobj.getAttribute("SETMETSTAGE-OBJECT");
			System.out.println(setmet.size()+"---------------------------------------");
			SetMetIdentifyStageObject identifyobj = (SetMetIdentifyStageObject) setmet.get("IDENTIFY");
			System.out.println(identifyobj+"----------------------------*********************");*/
			//code added for removing the session
			SetMetBL bl = new SetMetBL();
	        SetMetIdentifyStageObject identifyobj = (SetMetIdentifyStageObject) bl.getWorkflowObject(workflowID);
			request.setAttribute("identifyobj",identifyobj);
			//----Code ends here
			ArrayList getAccntByroles = new ArrayList();
			ArrayList attendees = new ArrayList();
			ArrayList actContact = new ArrayList();
			ArrayList bkAccountcontact = new ArrayList();
			ArrayList ibmlist = new ArrayList();
			ArrayList sponsor = new ArrayList();
			ArrayList selectedclientlist = new ArrayList();
			getAccntByroles = businessLogic.getAccntcontByroles(projectID);

			attendees = businessLogic.getAttendeeList(identifyobj.getClientele(), identifyobj.getWorkflowID(),company);
			//Next line is a modification by KP
			actContact = businessLogic.getIBM_Attendees(projectID);
			
			selectedclientlist = businessLogic.getClientList(identifyobj.getClientele(), identifyobj.getWorkflowID(),company);

			iForm.setAttendees(attendees);

			iForm.setDelegateList(getAccntByroles);
			iForm.setAcctList(getAccntByroles);
			iForm.setIbmerList(actContact);
			iForm.setSponsor(actContact);
			System.out.println("@@@@@@~~~~~~~~~!!!!!!!!!!@@@@@@@@@@@@@@#@###########@"+identifyobj.getBiWeeklyStatus());
			//ArrayList temp = new ArrayList();temp.add(identifyobj.getBiWeeklyStatus());	iForm.setStatus(temp);
			
			iForm.setMeetingAttendees(selectedclientlist);
			/*System.out.println("((SetMetIdentifyStageObject)(iForm.getWorkflowObject())).getBiWeeklyStatus() = "+((SetMetIdentifyStageObject)(iForm.getWorkflowObject())).getBiWeeklyStatus());
			((SetMetIdentifyStageObject)(iForm.getWorkflowObject())).setBiWeeklyStatus(identifyobj.getBiWeeklyStatus());
		*/
			iForm.setWorkflowObject(identifyobj);
			
			}

		if (iForm.getAction().equalsIgnoreCase("submit")) {
			
						object = (SetMetIdentifyStageObject)iForm.getWorkflowObject();
						object.setLastUsr(loggedUser);
						object.setClientName(company);
						// added by jv to fix build error
						ArrayList methodList = new ArrayList();
						try { 
							if(canAccess(request,"SETMET","CREATE")){
								Validator validator = Validator.getInstance();
								validator.validateStage(object,methodList);
			  
								logger.debug("The Status of the workflow%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% is"+object.getWorkflowStatus());
								System.out.println("the length iof nsirating is $$$$$$$$$"+object.getNsiRating().length());
								boolean x = businessLogic.saveWorkflowObject(object);
								request.setAttribute("ACCESS_DENIED","false");
							}else{
								request.setAttribute("ACCESS_DENIED","true");
							}
						} catch(Exception e) {
							e.printStackTrace(); 
						}
						String status = object.getWorkflowStatus();
						request.setAttribute("IDENTIFY_STATUS",status);
						request.setAttribute("METHODS",methodList);
						request.setAttribute("UPDATESTATUS","true");
			  //added by KP to make the prepare stage link work
						request.setAttribute("workflowID", object.getWorkflowID());
			  //end of addition by KP			 			  
						return  mapping.findForward("submitPage");		
						
		}//End of Submit chk

		if (iForm.getAction().equalsIgnoreCase("update")) {
             
			  
			  object = (SetMetIdentifyStageObject)iForm.getWorkflowObject();
			  System.out.println("/**//*/*/*/*/*/**/*/**/*/*/*/*/**/*/*/*/**/*/*/*/*/*/*/*/*/*/***"+object.isClientScorecard());
			  object.setLastUsr(loggedUser);
			  object.setClientName(company);
			  String status = "";
			  String updateStatus = "false";
			  // added by JV to correct build error
			  ArrayList methodList = new ArrayList();
			  try { 
			  	Validator validator = Validator.getInstance();
			  	validator.validateStage(object,methodList);
			  	logger.debug("The Status of the workflow%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% is"+object.getWorkflowStatus());
			  	boolean canUpdate = validator.getWorkflowCurrentStage(object,"update");
			  	 status = object.getWorkflowStatus();
			  	boolean x  = false;
			  	if(canAccess(request,"SETMET","UPDATE")){
			  		request.setAttribute("ACCESS_DENIED","false");
			  		if(canUpdate){
			  			x = businessLogic.updateWorkflowObject(object);
			  			updateStatus = "true";
			  		}else{
			  			if("true".equalsIgnoreCase(status)){
			  				x = businessLogic.updateWorkflowObject(object);
			  				updateStatus = "false";
			  			}
			  		}
			  	}else{
			  		request.setAttribute("ACCESS_DENIED","true");
			  	}
			  	} catch	  (Exception e) { 
			  		e.printStackTrace(); 
			  		} 
			  	
			  	
 			  	request.setAttribute("METHODS",methodList);
 			  	request.setAttribute("IDENTIFY_STATUS",status);
 			  	request.setAttribute("UPDATESTATUS",updateStatus);
			  	 //added by KP to make the prepare stage link work
				request.setAttribute("workflowID", object.getWorkflowID());
				  //end of addition by KP
				  
			  	return  mapping.findForward("submitPage");
			 
		} //End of Update chk
		
		/* Added for 7.1.1 
		 * XXX Is a patch for things like SETMET-IBM-040207Set/Met -> Identify
		 */
		request.removeAttribute("WORKFLOW_TITLE");
		
		return mapping.findForward("showPage");
	}

	private String getToday(char type) {
		String value = "";
		Calendar cal = Calendar.getInstance();
		int mon = cal.get(Calendar.MONTH) + 1;
		int dat = cal.get(Calendar.DATE);
		switch (type) {
		case 'D':
			if(cal.get(Calendar.DATE)< 10){
				value = "0"+String.valueOf(cal.get(Calendar.DATE));
			}else{
			value = String.valueOf(cal.get(Calendar.DATE));
			}
			break;
		case 'M':
			if (mon < 10) {
				value = "0" + mon;
			} else {
				value = String.valueOf(mon);
			}
			break;
		case 'Y':
			value = String.valueOf(cal.get(Calendar.YEAR));
			break;
		}

		return value;
	}
}
