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


package oem.edge.ets.fe.workflow.qbr.initialize;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.core.WorkflowStage;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;

/**
 * Class       : InitializeFormBean
 * Package     : oem.edge.ets.fe.workflow.qbr.initialize
 * Description : vo for the initialize phase of qbr
 * Date	       : Feb 2, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class InitializeVO extends WorkflowStage{
	
	private static Log logger = WorkflowLogger.getLogger(InitializeVO.class);
	
	private String[] notAttendees = null;
	private ArrayList allAttendees = null;
	private String[] attendees = null;
	private String[] ibmAttendees = null;
	private ArrayList allIBMers = null;
	private String[] execSponsor = null;
	private ArrayList allSponsors = null;
	private String[] nsiRating = null;
	private ArrayList allNsiRatings = null;
	private String[] accountContact = null;
	private ArrayList allContacts = null;
	private String[] qbrYear = null;
	private ArrayList allQbrYears = null;
	private String[] backupContact = null;
	private ArrayList allBackups = null;
	private String[] qbrQuarter = null;
	private ArrayList allQuarters = null;
	private String[] plannedMonth = null;
	private ArrayList allMonths = null;
	private String[] plannedDay = null;
	private ArrayList allDays = null;
	private String[] plannedYear = null;
	private ArrayList allYears = null;
	private String meetingLocation = null;
	private ArrayList allSelectedAttendees = null;
	private String[] biweeklyMonth = null;
	private String[] biweeklyDay = null;
	private String[] biweeklyYear = null;
	private String[] biweeklyStatus = null;
	private String[] biweeklyFlag = null;
	private ArrayList allBiweeklyStatuses = null;
	
	private String[] ratingFromYear = null;
	private String[] ratingFromMonth = null;
	private String[] ratingFromDay = null;
	
	private String[] ratingToYear = null;
	private String[] ratingToMonth = null;
	private String[] ratingToDay = null;
	
	private String workflowID = null;
	private String company = null;
	private String mode = null;
	private String loggedUser = null;
	
	public String[] getAccountContact() {
		return accountContact;
	}
	public void setAccountContact(String[] accountContact) {
		this.accountContact = accountContact;
	}
	public ArrayList getAllAttendees() {
		return allAttendees;
	}
	public void setAllAttendees(ArrayList allAttendees) {
		this.allAttendees = allAttendees;
	}
	public ArrayList getAllBackups() {
		return allBackups;
	}
	public void setAllBackups(ArrayList allBackups) {
		this.allBackups = allBackups;
	}
	public ArrayList getAllContacts() {
		return allContacts;
	}
	public void setAllContacts(ArrayList allContacts) {
		this.allContacts = allContacts;
	}
	public ArrayList getAllDays() {
		return allDays;
	}
	public void setAllDays(ArrayList allDays) {
		this.allDays = allDays;
	}
	public ArrayList getAllIBMers() {
		return allIBMers;
	}
	public void setAllIBMers(ArrayList allIBMers) {
		this.allIBMers = allIBMers;
	}
	public ArrayList getAllMonths() {
		return allMonths;
	}
	public void setAllMonths(ArrayList allMonths) {
		this.allMonths = allMonths;
	}
	public ArrayList getAllNsiRatings() {
		return allNsiRatings;
	}
	public void setAllNsiRatings(ArrayList allNsiRatings) {
		this.allNsiRatings = allNsiRatings;
	}
	public ArrayList getAllQbrYears() {
		return allQbrYears;
	}
	public void setAllQbrYears(ArrayList allQbrYears) {
		this.allQbrYears = allQbrYears;
	}
	public ArrayList getAllQuarters() {
		return allQuarters;
	}
	public void setAllQuarters(ArrayList allQuarters) {
		this.allQuarters = allQuarters;
	}
	public ArrayList getAllSponsors() {
		return allSponsors;
	}
	public void setAllSponsors(ArrayList allSponsors) {
		this.allSponsors = allSponsors;
	}
	public ArrayList getAllYears() {
		return allYears;
	}
	public void setAllYears(ArrayList allYears) {
		this.allYears = allYears;
	}
	public String[] getAttendees() {
		return attendees;
	}
	public void setAttendees(String[] attendees) {
		this.attendees = attendees;
	}
	public String[] getBackupContact() {
		return backupContact;
	}
	public void setBackupContact(String[] backupContact) {
		this.backupContact = backupContact;
	}
	public String[] getExecSponsor() {
		return execSponsor;
	}
	public void setExecSponsor(String[] execSponsor) {
		this.execSponsor = execSponsor;
	}
	public String[] getIbmAttendees() {
		return ibmAttendees;
	}
	public void setIbmAttendees(String[] ibmAttendees) {
		this.ibmAttendees = ibmAttendees;
	}
	public String getMeetingLocation() {
		return meetingLocation;
	}
	public void setMeetingLocation(String meetingLocation) {
		this.meetingLocation = meetingLocation;
	}
	public String[] getNotAttendees() {
		return notAttendees;
	}
	public void setNotAttendees(String[] notAttendees) {
		this.notAttendees = notAttendees;
	}
	public String[] getNsiRating() {
		return nsiRating;
	}
	public void setNsiRating(String[] nsiRating) {
		this.nsiRating = nsiRating;
	}
	public String[] getPlannedDay() {
		return plannedDay;
	}
	public void setPlannedDay(String[] plannedDay) {
		this.plannedDay = plannedDay;
	}
	public String[] getPlannedMonth() {
		return plannedMonth;
	}
	public void setPlannedMonth(String[] plannedMonth) {
		this.plannedMonth = plannedMonth;
	}
	public String[] getPlannedYear() {
		return plannedYear;
	}
	public void setPlannedYear(String[] plannedYear) {
		this.plannedYear = plannedYear;
	}
	public String[] getQbrQuarter() {
		return qbrQuarter;
	}
	public void setQbrQuarter(String[] qbrQuarter) {
		this.qbrQuarter = qbrQuarter;
	}
	public String[] getQbrYear() {
		return qbrYear;
	}
	public void setQbrYear(String[] qbrYear) {
		this.qbrYear = qbrYear;
	}
	public String getWorkflowID() {
		return workflowID;
	}
	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getLoggedUser() {
		return loggedUser;
	}
	public void setLoggedUser(String loggedUser) {
		this.loggedUser = loggedUser;
	}
	
	public ArrayList getAllSelectedAttendees() {
		return allSelectedAttendees;
	}
	public void setAllSelectedAttendees(ArrayList allSelectedAttendees) {
		this.allSelectedAttendees = allSelectedAttendees;
	}
	
	public String[] getBiweeklyDay() {
		return biweeklyDay;
	}
	public void setBiweeklyDay(String[] biweeklyDay) {
		this.biweeklyDay = biweeklyDay;
	}
	public String[] getBiweeklyFlag() {
		return biweeklyFlag;
	}
	public void setBiweeklyFlag(String[] biweeklyFlag) {
		this.biweeklyFlag = biweeklyFlag;
	}
	public String[] getBiweeklyMonth() {
		return biweeklyMonth;
	}
	public void setBiweeklyMonth(String[] biweeklyMonth) {
		this.biweeklyMonth = biweeklyMonth;
	}
	public String[] getBiweeklyStatus() {
		return biweeklyStatus;
	}
	public void setBiweeklyStatus(String[] biweeklyStatus) {
		this.biweeklyStatus = biweeklyStatus;
	}
	public String[] getBiweeklyYear() {
		return biweeklyYear;
	}
	public void setBiweeklyYear(String[] biweeklyYear) {
		this.biweeklyYear = biweeklyYear;
	}
	public ArrayList getAllBiweeklyStatuses() {
		return allBiweeklyStatuses;
	}
	public void setAllBiweeklyStatuses(ArrayList allBiweeklyStatuses) {
		this.allBiweeklyStatuses = allBiweeklyStatuses;
	}
	public String[] getRatingFromDay() {
		return ratingFromDay;
	}
	public void setRatingFromDay(String[] ratingFromDay) {
		this.ratingFromDay = ratingFromDay;
	}
	public String[] getRatingFromMonth() {
		return ratingFromMonth;
	}
	public void setRatingFromMonth(String[] ratingFromMonth) {
		this.ratingFromMonth = ratingFromMonth;
	}
	public String[] getRatingFromYear() {
		return ratingFromYear;
	}
	public void setRatingFromYear(String[] ratingFromYear) {
		this.ratingFromYear = ratingFromYear;
	}
	public String[] getRatingToDay() {
		return ratingToDay;
	}
	public void setRatingToDay(String[] ratingToDay) {
		this.ratingToDay = ratingToDay;
	}
	public String[] getRatingToMonth() {
		return ratingToMonth;
	}
	public void setRatingToMonth(String[] ratingToMonth) {
		this.ratingToMonth = ratingToMonth;
	}
	public String[] getRatingToYear() {
		return ratingToYear;
	}
	public void setRatingToYear(String[] ratingToYear) {
		this.ratingToYear = ratingToYear;
	}
	public void reset() {
		
		notAttendees = null;
		allAttendees = null;
		attendees = null;
		ibmAttendees = null;
		allIBMers = null;
		execSponsor = null;
		allSponsors = null;
		nsiRating = null;
		allNsiRatings = null;
		accountContact = null;
		allContacts = null;
		qbrYear = null;
		allQbrYears = null;
	    backupContact = null;
		allBackups = null;
		qbrQuarter = null;
		allQuarters = null;
		plannedMonth = null;
		allMonths = null;
		plannedDay = null;
		allDays = null;
		plannedYear = null;
		allYears = null;
		meetingLocation = null;		
		allSelectedAttendees = null;
		biweeklyMonth = null;
		biweeklyDay = null;
		biweeklyYear = null;
		biweeklyStatus = null;
		biweeklyFlag = null;
		allBiweeklyStatuses = null;
		ratingFromYear = null;
		ratingFromMonth = null;
		ratingFromDay = null;
		ratingToYear = null;
		ratingToMonth = null;
		ratingToDay = null;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowStage#getStageName()
	 */
	public String getStageName() {
		return WorkflowConstants.IDENTIFY;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowStage#getNextStage()
	 */
	public String getNextStage() {
		return WorkflowConstants.PREPARE;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowStage#getWorkflowType()
	 */
	public String getWorkflowType() {
		return "QBR";
	}
}

