
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

import oem.edge.ets.fe.workflow.core.WorkflowForm;

import oem.edge.ets.fe.workflow.util.SelectControl;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetIdentifyStageForm extends WorkflowForm {
   
	  private ArrayList attendees 		 = null;
	  private ArrayList meetingAttendees = null;
	  private ArrayList ibmerList		 = null;
	  private ArrayList	monthList		 = null;
	  private ArrayList dateList		 = null;
	  private ArrayList year			 = null;
	  private ArrayList qtr				 = null;
	  private ArrayList rating			 = null;
	  private ArrayList acctList		 = null;
	  private ArrayList delegateList     = null;
	  private ArrayList sponsor			 = null;
	  private ArrayList status			 = null;
	 
	/**
	 * @return Returns the acctList.
	 */
	public ArrayList getAcctList() {
		return acctList;
	}
	/**
	 * @param acctList The acctList to set.
	 */
	public void setAcctList(ArrayList acctList) {
		this.acctList = acctList;
	}
	/**
	 * @return Returns the delegateList.
	 */
	public ArrayList getDelegateList() {
		return delegateList;
	}
	/**
	 * @param delegateList The delegateList to set.
	 */
	public void setDelegateList(ArrayList delegateList) {
		this.delegateList = delegateList;
	}
	/**
	 * @return Returns the sponsor.
	 */
	public ArrayList getSponsor() {
		return sponsor;
	}
	/**
	 * @param sponsor The sponsor to set.
	 */
	public void setSponsor(ArrayList sponsor) {
		this.sponsor = sponsor;
	}
	  public SetMetIdentifyStageForm(){
	  	  attendees 		 = new ArrayList();
	  	  qtr				 = new ArrayList();
		  meetingAttendees   = new ArrayList();
		  ibmerList		 	 = new ArrayList();
		  monthList			 = new ArrayList();
		  dateList			 = new ArrayList();
		  year				 = new ArrayList();
		  rating			 = new ArrayList();
	  }
	  
	  
	  
	/**
	 * @return Returns the rating.
	 */
	public ArrayList getRating() {
		rating = new ArrayList();
		
		for(int x = 0; x<101;x++)
		   rating.add(new SelectControl(String.valueOf(x),String.valueOf(x)));
		return rating;
	}
	/**
	 * @param rating The rating to set.
	 */
	public void setRating(ArrayList rating) {
		this.rating = rating;
	}
	/**
	 * @return Returns the attendees.
	 */
	public ArrayList getAttendees() {
		return attendees;
	}
	/**
	 * @param attendees The attendees to set.
	 */
	public void setAttendees(ArrayList attendees) {
		this.attendees = attendees;
	}
	/**
	 * @return Returns the dateList.
	 */
	public ArrayList getDateList() {
		dateList = new ArrayList();
		dateList.add(new SelectControl(String.valueOf("01"),String.valueOf("01")));
		dateList.add(new SelectControl(String.valueOf("02"),String.valueOf("02")));
		dateList.add(new SelectControl(String.valueOf("03"),String.valueOf("03")));
		dateList.add(new SelectControl(String.valueOf("04"),String.valueOf("04")));
		dateList.add(new SelectControl(String.valueOf("05"),String.valueOf("05")));
		dateList.add(new SelectControl(String.valueOf("06"),String.valueOf("06")));
		dateList.add(new SelectControl(String.valueOf("07"),String.valueOf("07")));
		dateList.add(new SelectControl(String.valueOf("08"),String.valueOf("08")));
		dateList.add(new SelectControl(String.valueOf("09"),String.valueOf("09")));
		for(int x = 10;x< 32;x++)
			dateList.add(new SelectControl(String.valueOf(x),String.valueOf(x)));	
		
		return dateList;
	}
	/**
	 * @param dateList The dateList to set.
	 */
	public void setDateList(ArrayList dateList) {
		this.dateList = dateList;
	}
	/**
	 * @return Returns the ibmerList.
	 */
	public ArrayList getIbmerList() {
		return ibmerList;
	}
	/**
	 * @param ibmerList The ibmerList to set.
	 */
	public void setIbmerList(ArrayList ibmerList) {
		this.ibmerList = ibmerList;
	}
	/**
	 * @return Returns the meetingAttendees.
	 */
	public ArrayList getMeetingAttendees() {
		return meetingAttendees;
	}
	/**
	 * @param meetingAttendees The meetingAttendees to set.
	 */
	public void setMeetingAttendees(ArrayList meetingAttendees) {
		this.meetingAttendees = meetingAttendees;
	}
	/**
	 * @return Returns the monthList.
	 */
	public ArrayList getMonthList() {
		monthList = new ArrayList();
		monthList.add(new SelectControl("January","01"));
		monthList.add(new SelectControl("February","02"));
		monthList.add(new SelectControl("March","03"));
		monthList.add(new SelectControl("April","04"));
		monthList.add(new SelectControl("May","05"));
		monthList.add(new SelectControl("June","06"));
		monthList.add(new SelectControl("July","07"));
		monthList.add(new SelectControl("August","08"));
		monthList.add(new SelectControl("September","09"));
		monthList.add(new SelectControl("October","10"));
		monthList.add(new SelectControl("November","11"));
		monthList.add(new SelectControl("December","12"));		
		
		return monthList;
	}
	/**
	 * @param monthList The monthList to set.
	 */
	public void setMonthList(ArrayList monthList) {
		this.monthList = monthList;
	}
	/**
	 * @return Returns the year.
	 */
	public ArrayList getYear() {
		//Modified 7.1.1: Hard-code years from 2006 to 2012
		
		//Calendar calendar = Calendar.getInstance();
		//int strYear = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR)));
		
		year  = new ArrayList();
		//for(int x = strYear; x< (strYear+10) ;x++){
		for(int x = 2006; x < 2013; x++){
			year.add(new SelectControl(String.valueOf(x),String.valueOf(x)));
		}
		return year;
	}
	/**
	 * @param year The year to set.
	 */
	public void setYear(ArrayList year) {
		this.year = year;
	}
	  public void reset(){
	  	  super.reset();
	  	  attendees 		 = new ArrayList();
		  meetingAttendees   = new ArrayList();
		  ibmerList		 	 = new ArrayList();
		  monthList			 = new ArrayList();
		  dateList			 = new ArrayList();
		  year				 = new ArrayList();
	  }
	
	
	/**
	 * @return Returns the qtr.
	 */
	public ArrayList getQtr() {
	    qtr = new ArrayList();
	    qtr.add(new SelectControl("First Quarter","01"));
	    qtr.add(new SelectControl("Second Quarter","02"));
	    qtr.add(new SelectControl("Third Quarter","03"));
	    qtr.add(new SelectControl("Fourth Quarter","04"));
	    
		return qtr;
	}
	/**
	 * @param qtr The qtr to set.
	 */
	public void setQtr(ArrayList qtr) {
		this.qtr = qtr;
	}
	/**
	 * @return Returns the status.
	 */
	public ArrayList getStatus() {
	    status = new ArrayList();
	    status.add(new SelectControl("Complete","Complete"));
	    status.add(new SelectControl("Cancelled","Cancelled"));
	    status.add(new SelectControl("Reviewed","Reviewed"));
	    status.add(new SelectControl("Skipped","Skipped"));
	    
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(ArrayList status) {
		this.status = status;
	}
}
