
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
 * Created on Sep 29, 2006
 *
 * 
 */
package oem.edge.ets.fe.workflow.setmet.document;

import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.util.SelectControl;

import org.apache.struts.action.ActionMapping;

/**
 * @author Administrator
 *
 */
public class ScorecardFormBean extends WorkflowForm {
	
	private ArrayList scorers = new ArrayList();
    private ArrayList scores = new ArrayList();
    
    private ArrayList errors = new ArrayList();
    private String addQuestionDesc= null;
    private String addQuestionScore=null;
    
    private String scoreMatrixStatus = WorkflowConstants.DOCUMENT_STAGE_INCOMPLETE;
	
    private String incomplete="";
    private String complete="";
    
    private String firstQuarter = "First Quarter";
    private String secondQuarter = "Second Quarer";
    private String thirdQuarter = "ThirdQuarter";
    private String fourthQuarter = "FourthQuarter";
    
    private String currentYear="";
    private String previousYear="";
    
    private String matrixID = "";
	private String comment = null;
    
    private ArrayList ratingQuestions = new ArrayList();
    
    
    private String currentStage = "Document";
    private String accessible="";
    
    
    private ArrayList ratings = null;
    private ArrayList colors = null;
    
    
    	
    private ArrayList questions = new ArrayList();
    private ArrayList clientAttendees = new ArrayList();
    private String clientAttendee = "";
    
    private ArrayList monthList		  = new ArrayList();
    private ArrayList dateList		  = new ArrayList();
    private ArrayList year		 	  = new ArrayList();

	private String date		 	 	  = "";
	private String month		 	  = "";
	private String yr		 		  = "";

    
    private Scorecard scorecard = new Scorecard();
    private String workflowName = null;
    private String reporttitle  = null;
    private String summaryreport = null;
    private String overallcomments = null;
    private String overallrating   = null;
    private boolean bypass			= false;
   
	/**
	 * @return Returns the bypass.
	 */
	public boolean isBypass() {
		return bypass;
	}
	/**
	 * @param bypass The bypass to set.
	 */
	public void setBypass(boolean bypass) {
		System.out.println("the value of the bean is )))))))))))))))))))))))))))))))"+bypass);
		
		this.bypass = bypass;
	}
	/**
	 * @return Returns the overallcomments.
	 */
	public String getOverallcomments() {
		return overallcomments;
	}
	/**
	 * @param overallcomments The overallcomments to set.
	 */
	public void setOverallcomments(String overallcomments) {
		this.overallcomments = overallcomments;
	}
	/**
	 * @return Returns the reporttitle.
	 */
	public String getReporttitle() {
		return reporttitle;
	}
	/**
	 * @param reporttitle The reporttitle to set.
	 */
	public void setReporttitle(String reporttitle) {
		this.reporttitle = reporttitle;
	}
	/**
	 * @return Returns the summaryreport.
	 */
	public String getSummaryreport() {
		return summaryreport;
	}
	/**
	 * @param summaryreport The summaryreport to set.
	 */
	public void setSummaryreport(String summaryreport) {
		this.summaryreport = summaryreport;
	}
    
   public ArrayList getColors(){
   	  colors = new ArrayList();
   	  colors.add(new SelectControl("Select a Color","0"));
   	  colors.add(new SelectControl("Red","3"));
   	  colors.add(new SelectControl("Yellow","2"));
   	  colors.add(new SelectControl("Green","1"));
	  return colors;
   }
	

   
	public ArrayList getRatings() {
		ratings = new ArrayList();
		ratings.add(new SelectControl("Select a rating",String.valueOf(0)));
		for(int x = 1;x<11;x++)
			ratings.add(new SelectControl(String.valueOf(x),String.valueOf(x)));
		 return ratings;
	}
	/**
	 * @param ratings The ratings to set.
	 */
	public void setRatings(ArrayList ratings) {
		this.ratings = ratings;
	}
    
	public String getComment(){

		return comment;
	}

	public void setComment(String comment){

      this.comment = comment;
	}
    
	public String getAccessible() {
		return accessible;
	}
	public void setAccessible(String accessible) {
		this.accessible = accessible;
	}
	public String getCurrentStage() {
		return currentStage;
	}
	public void setCurrentStage(String currentStage) {
		this.currentStage = currentStage;
	}
	public ArrayList getRatingQuestions() {
		return ratingQuestions;
	}
	public void setRatingQuestions(ArrayList ratingQuestions) {
		this.ratingQuestions = ratingQuestions;
	}
	public String getMatrixID() {
		return matrixID;
	}
	public void setMatrixID(String matrixID) {
		this.matrixID = matrixID;
	}
    public ArrayList getErrors() {
		return errors;
	}
	public void setErrors(ArrayList errors) {
		this.errors = errors;
	}    
    
	public String getCurrentYear() {
		return currentYear;
	}
	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}
	public String getFirstQuarter() {
		return firstQuarter;
	}
	public void setFirstQuarter(String firstQuarter) {
		this.firstQuarter = firstQuarter;
	}
	public String getFourthQuarter() {
		return fourthQuarter;
	}
	public void setFourthQuarter(String fourthQuarter) {
		this.fourthQuarter = fourthQuarter;
	}
	public String getPreviousYear() {
		return previousYear;
	}
	public void setPreviousYear(String previousYear) {
		this.previousYear = previousYear;
	}
	public String getSecondQuarter() {
		return secondQuarter;
	}
	public void setSecondQuarter(String secondQuarter) {
		this.secondQuarter = secondQuarter;
	}
	public String getThirdQuarter() {
		return thirdQuarter;
	}
	public void setThirdQuarter(String thirdQuarter) {
		this.thirdQuarter = thirdQuarter;
	}
	public String getClientAttendee() {
		return clientAttendee;
	}
	public void setClientAttendee(String clientAttendee) {
		this.clientAttendee = clientAttendee;
	}
	public ArrayList getClientAttendees() {
		return clientAttendees;
	}
	public void setClientAttendees(ArrayList clientAttendees) {
		this.clientAttendees = clientAttendees;
	}
	public String getScoreMatrixStatus() {
		return scoreMatrixStatus;
	}
	public void setScoreMatrixStatus(String scoreMatrixStatus) {
		this.scoreMatrixStatus = scoreMatrixStatus;
	}
    public String getComplete() {
		return complete;
	}
	public void setComplete(String complete) {
		this.complete = complete;
	}
	public String getIncomplete() {
		return incomplete;
	}
	public void setIncomplete(String incomplete) {
		this.incomplete = incomplete;
	}
	
    public String getAddQuestionScore() {
		return addQuestionScore;
	}
	public void setAddQuestionScore(String addQuestionScore) {
		this.addQuestionScore = addQuestionScore;
	}
	public String getAddQuestionDesc() {
		return addQuestionDesc;
	}
	public void setAddQuestionDesc(String addQuestionDesc) {
		this.addQuestionDesc = addQuestionDesc;
	}
	public String getWorkflowName() {
		return workflowName;
	}
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	public Scorecard getScorecard() {
		return scorecard;
	}
	public void setScorecard(Scorecard scorecard) {
		this.scorecard = scorecard;
	}
    private String scorer = null;
	
	public String getScorer() {
		return scorer;
	}
	public void setScorer(String scorer) {
		this.scorer = scorer;
	}
	public ArrayList getQuestions() {
		return questions;
	}
	public void setQuestions(ArrayList questions) {
		this.questions = questions;
	}
	public ScorecardFormBean(){
		
		System.out.println("The form bean is instantiated ###########################################");
		scorers = new ArrayList();
		questions = new ArrayList();
		bypass = false;
	}
	

	/**
	 * @return Returns the scorers.
	 */
	public ArrayList getScorers()
	{
		
		return scorers;
	}
	/**
	 * @param scorers The scorers to set.
	 */
	public void setScorers(ArrayList scorers) 
	{
		
		this.scorers = scorers;
	}
	
	public ArrayList getScores()
	{
		ArrayList scoresArrayList = new ArrayList();
		for(int i = 0; i < 11; i++)
		{
			scoresArrayList.add(new Integer(i).toString());
		}
		return scoresArrayList;
	}
	/**
	 * @return Returns the date.
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date The date to set.
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return Returns the dateList.
	 */
	public ArrayList getDateList() {
		dateList = new ArrayList();
		dateList.add(new SelectControl(String.valueOf("01"),String.valueOf("1")));
		dateList.add(new SelectControl(String.valueOf("02"),String.valueOf("2")));
		dateList.add(new SelectControl(String.valueOf("03"),String.valueOf("3")));
		dateList.add(new SelectControl(String.valueOf("04"),String.valueOf("4")));
		dateList.add(new SelectControl(String.valueOf("05"),String.valueOf("5")));
		dateList.add(new SelectControl(String.valueOf("06"),String.valueOf("6")));
		dateList.add(new SelectControl(String.valueOf("07"),String.valueOf("7")));
		dateList.add(new SelectControl(String.valueOf("08"),String.valueOf("8")));
		dateList.add(new SelectControl(String.valueOf("09"),String.valueOf("9")));
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
	 * @return Returns the month.
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month The month to set.
	 */
	public void setMonth(String month) {
		this.month = month;
	}
	/**
	 * @return Returns the monthList.
	 */
	public ArrayList getMonthList() {
		monthList  = new ArrayList();
		monthList.add(new SelectControl("January","1"));
		monthList.add(new SelectControl("February","2"));
		monthList.add(new SelectControl("March","3"));
		monthList.add(new SelectControl("April","4"));
		monthList.add(new SelectControl("May","5"));
		monthList.add(new SelectControl("June","6"));
		monthList.add(new SelectControl("July","7"));
		monthList.add(new SelectControl("August","8"));
		monthList.add(new SelectControl("September","9"));
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
		Calendar calendar = Calendar.getInstance();
		int strYear = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR)));
		
		year  = new ArrayList();
		for(int x = (strYear-4); x< (strYear+10) ;x++){
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
	/**
	 * @return Returns the yr.
	 */
	public String getYr() {
		return yr;
	}
	/**
	 * @param yr The yr to set.
	 */
	public void setYr(String yr) {
		this.yr = yr;
	}
	/**
	 * @param scores The scores to set.
	 */
	public void setScores(ArrayList scores) {
		this.scores = scores;
	}
	/**
	 * @return Returns the overallrating.
	 */
	public String getOverallrating() {
		return overallrating;
	}
	/**
	 * @param overallrating The overallrating to set.
	 */
	public void setOverallrating(String overallrating) {
		this.overallrating = overallrating;
	}
}
