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
 * Created on Sep 11, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.stage.IdentifyStageObject;

import org.apache.commons.logging.Log;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetIdentifyStageObject extends IdentifyStageObject {
	

	 private String requestor 			= null;
	 private String acctContact 		= null;
	 private String delegate  			= null;
	 private String sponsor    			= null;
	 private String quarter				= null;
	 private String meetingID			= null;
	  	 
	 private String year       		 	= null;
	 private String plYear				= null;
	 private String plMon				= null;
	 private String plDate				= null;
	 
	 private String[] clientele     	= null;
	 private String[] ibmlist			= null;
	 private String[] attendees			= null;
	 
	 private boolean clientScorecard     = false;
	 
	 private boolean biWeeklyflg 		= false;
	 private String biWeeklyDt	 		= null;
	 private String biWeeklyMon			= null;
	 private String biWeeklyYr			= null;
	 
	 private String biWeeklyStatus 		= null;
	 private String nsiRating			= null;
	 private String lastUsr				= null;
	 private Timestamp lastTS			= null;
	 private String meetingLocn			= null;
	 private String workflowType    	= "SETMET";
	 private String clientName		    = "IBM";		
	 
	 
	 
	 /**
	  * This method returns a string stating any IBMer has been added are not
	  * @return
	  */
	 public String hasIBMERS(){
	 	if(ibmlist!=null && ibmlist.length>0)
	 		return "contains";
	 	else
	 		return null;
	 }
	 /**
	  * This method returns a string stating any attendee has been added are not
	  * @return
	  */
	 public String hasAttendees(){
	   if(attendees!=null && attendees.length>0)
	   	  return "contains";
	   else
	   	 return null;
	 }
	/**
	 * @return Returns the clientName.
	 */
	public String getClientName() {
		
		return clientName;
	}
	/**
	 * @param clientName The clientName to set.
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
		/**
		 * @return Returns the meetingID.
		 */
	
		public String getMeetingID() {
			return meetingID;
		}
		/**
		 * @param meetingID The meetingID to set.
		 */
		public void setMeetingID(String meetingID) {
			this.meetingID = meetingID;
		}
	/**
	 * @return Returns the workflowType.
	 */
	public String getWorkflowType() {
		return workflowType;
	}
	/**
	 * @return Returns the plDate.
	 */
	public String getPlDate() {
		
		return plDate;
	}
	/**
	 * @param plDate The plDate to set.
	 */
	public void setPlDate(String plDate) {
		this.plDate = plDate;
	}
	/**
	 * @return Returns the plMon.
	 */
	public String getPlMon() {
		return plMon;
	}
	/**
	 * @param plMon The plMon to set.
	 */
	public void setPlMon(String plMon) {
		this.plMon = plMon;
	}
	/**
	 * @return Returns the plYear.
	 */
	public String getPlYear() {
		return plYear;
	}
	/**
	 * @param plYear The plYear to set.
	 */
	public void setPlYear(String plYear) {
		this.plYear = plYear;
	}
	/**
	 * @param quarter The quarter to set.
	 */
	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}
	 
	/**
	 * @return Returns the biWeeklyDt.
	 */
	public String getBiWeeklyDt() {
		return biWeeklyDt;
	}
	/**
	 * @param biWeeklyDt The biWeeklyDt to set.
	 */
	public void setBiWeeklyDt(String biWeeklyDt) {
		this.biWeeklyDt = biWeeklyDt;
	}
	/**
	 * @return Returns the biWeeklyMon.
	 */
	public String getBiWeeklyMon() {
		return biWeeklyMon;
	}
	/**
	 * @param biWeeklyMon The biWeeklyMon to set.
	 */
	public void setBiWeeklyMon(String biWeeklyMon) {
		this.biWeeklyMon = biWeeklyMon;
	}
	/**
	 * @return Returns the biWeeklyYr.
	 */
	public String getBiWeeklyYr() {
		return biWeeklyYr;
	}
	/**
	 * @param biWeeklyYr The biWeeklyYr to set.
	 */
	public void setBiWeeklyYr(String biWeeklyYr) {
		this.biWeeklyYr = biWeeklyYr;
	}
	/**
	 * @return Returns the meetingLocn.
	 */
	public String getMeetingLocn() {
		return meetingLocn;
	}
	/**
	 * @param meetingLocn The meetingLocn to set.
	 */
	public void setMeetingLocn(String meetingLocn) {
		this.meetingLocn = meetingLocn;
	}
	/**
	 * @return Returns the acctContact.
	 */
	public String getAcctContact() {
		return acctContact;
	}
	/**
	 * @param acctContact The acctContact to set.
	 */
	public void setAcctContact(String acctContact) {
		this.acctContact = acctContact;
	}
	
	/**
	 * @return Returns the biWeeklyflg.
	 */
	public boolean isBiWeeklyflg() {
		return biWeeklyflg;
	}
	/**
	 * @param biWeeklyflg The biWeeklyflg to set.
	 */
	public void setBiWeeklyflg(boolean biWeeklyflg) {
		
		this.biWeeklyflg = biWeeklyflg;
	}
	
	/**
	 * @return Returns the delegate.
	 */
	public String getDelegate() {
		return delegate;
	}
	/**
	 * @param delegate The delegate to set.
	 */
	public void setDelegate(String delegate) {
		this.delegate = delegate;
	}
	/**
	 * @return Returns the lastTS.
	 */
	public Timestamp getLastTS() {
		return lastTS;
	}
	/**
	 * @param lastTS The lastTS to set.
	 */
	public void setLastTS(Timestamp lastTS) {
		this.lastTS = lastTS;
	}
	/**
	 * @return Returns the lastUsr.
	 */
	public String getLastUsr() {
		return lastUsr;
	}
	/**
	 * @param lastUsr The lastUsr to set.
	 */
	public void setLastUsr(String lastUsr) {
		this.lastUsr = lastUsr;
	}
	/**
	 * @return Returns the nsiRating.
	 */
	public String getNsiRating() {
		return nsiRating;
	}
	/**
	 * @param nsiRating The nsiRating to set.
	 */
	public void setNsiRating(String nsiRating) {
		this.nsiRating = nsiRating;
	}
	/**
	 * @return Returns the quarter.
	 */
	public String getQuarter() {
		return quarter;
	}

	/**
	 * @return Returns the requestor.
	 */
	public String getRequestor() {
		return requestor;
	}
	/**
	 * @param requestor The requestor to set.
	 */
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	/**
	 * @return Returns the sponsor.
	 */
	public String getSponsor() {
		return sponsor;
	}
	/**
	 * @param sponsor The sponsor to set.
	 */
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}
	/**
	 * @return Returns the year.
	 */
	public String getYear() {
		return year;
	}
	/**
	 * @param year The year to set.
	 */
	public void setYear(String year) {
		this.year = year;
	}
	
	
	/**
	 * @return Returns the attendees.
	 */
	public String[] getAttendees() {
		return attendees;
	}
	/**
	 * @param attendees The attendees to set.
	 */
	public void setAttendees(String[] attendees) {
		this.attendees = attendees;
	}
	/**
	 * @return Returns the clientele.
	 */
	public String[] getClientele() {
		return clientele;
	}
	/**
	 * @param clientele The clientele to set.
	 */
	public void setClientele(String[] clientele) {
		this.clientele = clientele;
	}
	/**
	 * @return Returns the ibmlist.
	 */
	public String[] getIbmlist() {
		return ibmlist;
	}
	/**
	 * @param ibmlist The ibmlist to set.
	 */
	public void setIbmlist(String[] ibmlist) {
		this.ibmlist = ibmlist;
	}
	
	/**
	 * @return Returns the biWeeklyStatus.
	 */
	public String getBiWeeklyStatus() {
		return biWeeklyStatus;
	}
	/**
	 * @param biWeeklyStatus The biWeeklyStatus to set.
	 */
	public void setBiWeeklyStatus(String biWeeklyStatus) {
		this.biWeeklyStatus = biWeeklyStatus;
	}
	public String getClassName(){
		  return "oem.edge.ets.fe.workflow.setmet.SetMetIdentifyStageObject";
	}
	/*public String getValidPLDate(){
		java.util.Date dtTmp    = null;
		java.util.Date currDate = null;
		String flg				= null;
		try{
			 dtTmp = new SimpleDateFormat("MM/dd/yy").parse(getPlMon()+"/"+getPlDate()+"/"+getPlYear());
			 
             currDate = new java.util.Date(System.currentTimeMillis());
             
             if(dtTmp.compareTo(currDate)==1 ){
             	if(setcheckDate(Integer.parseInt(getPlDate()),Integer.parseInt(getPlMon()),Integer.parseInt(getPlYear())))
             	  flg ="true";
             }
		}catch(Exception ex){
			logger.error("Date format exception in setmetidentifystageobject",ex);
		}
		return flg;
	}
    public boolean setcheckDate(int dt,int mon,int yr){
    	boolean flg = false;
    	if(mon==4 || mon==6|| mon==9|| mon==11){
    		 if(dt<=30)
    		   flg = true;
    	}else{
    		if(mon==2){
    			if(isLeapYear(yr)){
    				if(dt<=29)
    					flg=true;
    			}else{
    				if(dt<=28)
    					flg=true;
    			}
    			
    		}
    	}
    	if(mon==1 || mon==3 || mon==5 || mon == 7 || mon==10 || mon==12){
    		if(dt<=31)
    			flg = true;
    	}
    	
    	return flg;
    	
    }
    
    public boolean isLeapYear(int yr){
    	if(yr%400==0 ||(yr%4==0&&yr%100!=0)){
    		System.out.println("Its  a leap year");
    		return true;
    	}else{
    		return false;
    	}    	
    }*/
	private static Log logger		  	  = WorkflowLogger.getLogger(SetMetIdentifyStageObject.class);

	/**
	 * @return Returns the clientScorecard.
	 */
	public boolean isClientScorecard() {
		return clientScorecard;
	}
	/**
	 * @param clientScorecard The clientScorecard to set.
	 */
	public void setClientScorecard(boolean clientScorecard) {
		this.clientScorecard = clientScorecard;
	}
}
