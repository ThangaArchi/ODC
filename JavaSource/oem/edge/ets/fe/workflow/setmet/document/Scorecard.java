
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
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.document;


import java.util.ArrayList;

import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.util.OrderedMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Scorecard extends WorkflowObject {
	
	private String matrixID		 = "";
	private OrderedMap qestRating 	 = null;
	private String scoredBy  	 = null;
	private String scorerID	 	 = null;
	private String rating = null;
	private char scorecardStatus = 'I';
	private ArrayList questions = null;
	private String workflowId = null;
	private String last_userId = null;
	private String updateStatus = "";
	private String questionID = null;
	private String meetingDate = null;
	private String overallcomment = null;
	private String reporttitle = null;
	private String summaryreport=null;
	private String overallrating = null;
	
	
	
	
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
	/**
	 * @return Returns the overallcomment.
	 */
	public String getOverallcomment() {
		return overallcomment;
	}
	/**
	 * @param overallcomment The overallcomment to set.
	 */
	public void setOverallcomment(String overallcomment) {
		this.overallcomment = overallcomment;
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
	/**
	 * @return Returns the meetingDate.
	 */
	public String getMeetingDate() {
		return meetingDate;
	}
	/**
	 * @param meetingDate The meetingDate to set.
	 */
	public void setMeetingDate(String meetingDate) {
		this.meetingDate = meetingDate;
	}
	public String getQuestionID() {
		return questionID;
	}
	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getUpdateStatus() {
		return updateStatus;
	}
	public void setUpdateStatus(String updateStatus) {
		this.updateStatus = updateStatus;
	}
	public String getLast_userId() {
		return last_userId;
	}
	public void setLast_userId(String last_userId) {
		this.last_userId = last_userId;
	}
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public ArrayList getQuestions() {
		return questions;
	}
	public void setQuestions(ArrayList questions) {
		this.questions = questions;
	}
	/**
	 * @return Returns the scorerID.
	 */
	public String getScorerID() {
		return scorerID;
	}
	/**
	 * @param scorerID The scorerID to set.
	 */
	public void setScorerID(String scorerID) {
		this.scorerID = scorerID;
	}
	
	/**
	 * @return Returns the matrixID.
	 */
	public String getMatrixID() {
		return matrixID;
	}
	/**
	 * @param matrixID The matrixID to set.
	 */
	public void setMatrixID(String matrixID) {
		this.matrixID = matrixID;
	}
	
	/**
	 * @return Returns the scorecardStatus.
	 */
	public char getScorecardStatus() {
		return scorecardStatus;
	}
	/**
	 * @param scorecardStatus The scorecardStatus to set.
	 */
	public void setScorecardStatus(char scorecardStatus) {
		this.scorecardStatus = scorecardStatus;
	}
	/**
	 * @return Returns the scoredBy.
	 */
	public String getScoredBy() {
		return scoredBy;
	}
	/**
	 * @param scoredBy The scoredBy to set.
	 */
	public void setScoredBy(String scoredBy) {
		this.scoredBy = scoredBy;
	}
	/**
	 * @return Returns the qestRating.
	 */
	public OrderedMap getQestRating() {
		return qestRating;
	}
	/**
	 * @param qestRating The qestRating to set.
	 */
	public void setQestRating(OrderedMap qestRating) {
		this.qestRating = qestRating;
	}
}
