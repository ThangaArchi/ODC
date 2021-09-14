/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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
 * Created on Sep 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.dao.QueryCaching;
import oem.edge.ets.fe.workflow.core.WorkflowException;

/**
 * @author ryazuddin
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SetMetBL {

	public boolean saveWorkflowObject(WorkflowObject object) {
		boolean status = false;
		SetMetDAO dao = new SetMetDAO();
		try {
			status = dao.saveWorkflowObject(object);
		} catch (Exception ex) {
			System.out.println("The exception in Bl is" + ex);
		}
		return status;
	}

	public boolean updateWorkflowObject(WorkflowObject object) {
		boolean status = false;
		SetMetDAO dao = new SetMetDAO();

		try {
			status = dao.updateWorkflowObject(object);
		} catch (Exception ex) {
			System.out.println("The exception in Bl is" + ex);
		}
		return status;
	}
/**
 * 
 * Method signature for this was modified for 7.1.1; added argument:String workflowType; KP
 */
	public ArrayList getWorkflowList(String projectID,String workflowType) {
		ArrayList workflowlist = null;
		try {
			SetMetDAO dao = new SetMetDAO();
			workflowlist = dao.getWorkflowList(projectID, workflowType);
			if (workflowlist == null)
				throw new WorkflowException("Workflow List is empty");
		} catch (WorkflowException we) {

		} catch (Exception e) {
		}
		return workflowlist;
	}
	
	public ArrayList getHistoryList(String wid) {
		ArrayList workflowlist = null;
		try {
			SetMetDAO dao = new SetMetDAO();
			workflowlist = dao.getHistoryList(wid);
			if (workflowlist == null)
				throw new WorkflowException("History List is empty");
		} catch (WorkflowException we) {

		} catch (Exception e) {
		}
		return workflowlist;
	}
	public ArrayList getClient_Attendees(String company) {
		ArrayList clientlist = null;
		try {
			SetMetDAO dao = new SetMetDAO();
			clientlist = dao.getClient_Attendees(company);
			if (clientlist == null)
				throw new WorkflowException("Client List is empty");
		} catch (WorkflowException we) {

		} catch (Exception e) {

		}
		return clientlist;
	}

	public ArrayList getIBM_Attendees(String companyID) {
		ArrayList ibmattendeeslist = null;
		try {
			SetMetDAO dao = new SetMetDAO();
			ibmattendeeslist = dao.getIBM_Attendees(companyID);
			if (ibmattendeeslist == null)
				throw new WorkflowException("IBMAttendees List is empty");
		} catch (WorkflowException we) {
		} catch (Exception e) {
		}
		return ibmattendeeslist;
	}

	public ArrayList getAccount_Contact(String companyID) {
		ArrayList accountcontactlist = null;
		try {
			SetMetDAO dao = new SetMetDAO();
			accountcontactlist = dao.getAccount_Contact(companyID);
			if (accountcontactlist == null)
				throw new WorkflowException("Client List is empty");
		} catch (WorkflowException we) {
		} catch (Exception e) {
		}
		return accountcontactlist;
	}

	public ArrayList getExecutive_Sponsor(String companyID) {
		ArrayList executivesponcerlist = null;
		try {
			SetMetDAO dao = new SetMetDAO();
			executivesponcerlist = dao.getExecutive_Sponsor(companyID);
			if (executivesponcerlist == null)
				throw new WorkflowException("Client List is empty");
		} catch (WorkflowException we) {
		} catch (Exception e) {
		}
		return executivesponcerlist;
	}

	public static String getCurrentQTR() {
		String QTR = null;
		Calendar cal = new GregorianCalendar();
		int currentMonth = cal.get(Calendar.MONTH);
		switch (currentMonth) {
		case 0: case 1: case 2: QTR = "01"; break;
		case 3: case 4: case 5: QTR = "02"; break;
		case 6: case 7: case 8: QTR = "03"; break;
		case 9: case 10: case 11: QTR = "04"; break;
		}
		return QTR;
	}

	public boolean createIdentifySetMet(SetMetIdentifyStageObject obj)
			throws Exception {
		SetMetDAO dao = new SetMetDAO();
		boolean status = false;
		try {
			status = dao.initializeWorkflowTables(obj);
		} catch (Exception e) {
			status = false;
			e.getMessage();
		}
		return status;
	}

	public WorkflowObject getWorkflowObject(String ID) {
		String id = ID.trim();
		SetMetDAO dao = new SetMetDAO();
		SetMetIdentifyStageObject sObj = (SetMetIdentifyStageObject) dao.getWorkflowObject(id);
		System.out.println("Inside bl***********************************************");
		System.out.println(sObj.getProjectID());
		System.out.println(sObj.getWorkflowID());
		System.out.println(sObj.getStageID());
		
		System.out.println(sObj.getRequestor());
		System.out.println(sObj.getAcctContact());
		System.out.println(sObj.getDelegate());  // backup account contact
		System.out.println(sObj.getSponsor());
		
		System.out.println(sObj.getMeetingLocn());
		System.out.println(sObj.isBiWeeklyflg());
		System.out.println(sObj.getBiWeeklyDt());
		System.out.println(sObj.getBiWeeklyStatus());
		System.out.println(sObj.getNsiRating());
		System.out.println(sObj.getQuarter());
		System.out.println(sObj.getYear());
		System.out.println(sObj.getMeetingID());
		System.out.println(sObj.getPlDate()); 
		System.out.println(sObj.getPlMon());
		System.out.println(sObj.getPlYear());
		System.out.println("Hi;;;;;;;" + sObj.getIbmlist());
		System.out.println(sObj.getAttendees());
		return sObj;
	}

	public ArrayList getClientList(String[] clientattendees, String wfid,String company) {
		ArrayList clientatt = new ArrayList();
		try {
			SetMetDAO dao = new SetMetDAO();
			clientatt = dao.getClientList(clientattendees, wfid , company);
			if (clientatt == null)
				throw new WorkflowException("Client List is empty");
		} catch (Exception e) {
			e.getMessage();
		} finally {

		}
		return clientatt;
	}

	public ArrayList getAttendeeList(String[] clientattendees, String wfid,String company) {
		ArrayList attendee = new ArrayList();
		try {
			SetMetDAO dao = new SetMetDAO();
			attendee = dao.getAttendeeList(clientattendees, wfid,company);
			if (attendee == null)
				throw new WorkflowException("Client List is empty");
		} catch (Exception e) {
			e.getMessage();
		} finally {
		}
		return attendee;
	}
	public ArrayList getAccntcontByroles(String proj) {
		ArrayList userlistwithrole = new ArrayList();
		try {
			SetMetDAO setmetdo = new SetMetDAO();
			userlistwithrole = setmetdo.getAccntcontByroles(proj);
			if (userlistwithrole == null)
				throw new WorkflowException("User List is empty");
		} catch (Exception e) {
			e.getMessage();
		} finally {
		}
		return userlistwithrole;
	}
	public static String getSector(String projectId) {
		String sector = null;
		try {
			SetMetDAO setmetdo = new SetMetDAO();
			sector = setmetdo.getSector(projectId);
			if (sector == null)
				throw new WorkflowException("sector is empty");
		} catch (Exception e) {
			e.getMessage();
		} finally {
		}
		return sector;
	}
}
