//Source file: E:\\test\\aic\\JavaSource\\oem\\edge\\ets\\fe\\workflow\\qbr\\qbrdocument\\QBRDocumentStage.java

package oem.edge.ets.fe.workflow.qbr.qbrdocument;

import oem.edge.ets.fe.workflow.stage.DocumentStageObject;
import java.util.ArrayList;

public class QBRDocumentStage extends DocumentStageObject 
{
   private String overallRating = null;
   private String nextMeetingYear = null;
   private String nextMeetingMonth = null;
   private String nextMeetingDate = null;
   private ArrayList dateList = null;
   private ArrayList monthList = null;
   private ArrayList yearList = null;
   private String workflowType = null;
   private String clientName = null;
   private String lastUsr = null;
   private QBRScorecard scorecard = null;
   private boolean bypass = false;
   
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
	this.bypass = bypass;
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
   @roseuid 45C871150148
    */
   public QBRDocumentStage() 
   {
    
   }
   public void setWorkflowType(String workflowType){
	   this.workflowType  = workflowType;
   }
   public String getWorkflowType(){
   	 return workflowType;
   }
/**
 * @return Returns the dateList.
 */
public ArrayList getDateList() {
	return dateList;
}
/**
 * @param dateList The dateList to set.
 */
public void setDateList(ArrayList dateList) {
	this.dateList = dateList;
}
/**
 * @return Returns the monthList.
 */
public ArrayList getMonthList() {
	return monthList;
}
/**
 * @param monthList The monthList to set.
 */
public void setMonthList(ArrayList monthList) {
	this.monthList = monthList;
}
/**
 * @return Returns the nextMeetingDate.
 */
public String getNextMeetingDate() {
	return nextMeetingDate;
}
/**
 * @param nextMeetingDate The nextMeetingDate to set.
 */
public void setNextMeetingDate(String nextMeetingDate) {
	this.nextMeetingDate = nextMeetingDate;
}
/**
 * @return Returns the nextMeetingMonth.
 */
public String getNextMeetingMonth() {
	return nextMeetingMonth;
}
/**
 * @param nextMeetingMonth The nextMeetingMonth to set.
 */
public void setNextMeetingMonth(String nextMeetingMonth) {
	this.nextMeetingMonth = nextMeetingMonth;
}
/**
 * @return Returns the nextMeetingYear.
 */
public String getNextMeetingYear() {
	return nextMeetingYear;
}
/**
 * @param nextMeetingYear The nextMeetingYear to set.
 */
public void setNextMeetingYear(String nextMeetingYear) {
	this.nextMeetingYear = nextMeetingYear;
}
/**
 * @return Returns the overallRating.
 */
public String getOverallRating() {
	return overallRating;
}
/**
 * @param overallRating The overallRating to set.
 */
public void setOverallRating(String overallRating) {
	this.overallRating = overallRating;
}
/**
 * @return Returns the yearList.
 */
public ArrayList getYearList() {
	return yearList;
}
/**
 * @param yearList The yearList to set.
 */
public void setYearList(ArrayList yearList) {
	this.yearList = yearList;
}
/**
 * @return Returns the scorecard.
 */
public QBRScorecard getScorecard() {
	return scorecard;
}
/**
 * @param scorecard The scorecard to set.
 */
public void setScorecard(QBRScorecard scorecard) {
	this.scorecard = scorecard;
}
}
