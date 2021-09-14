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


/*
 * Created on Oct 9, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.document;

import java.sql.Timestamp;

/**
 * @author Amar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Question
{
	private String question_id = null;
	private String ques_desc=null;
	private int version=0;
	private String company=null;
	private String question_type=null;
	private int last_scored_quarter=0;
	private int last_scored_year=0;
	private String last_userID = null;
	private Timestamp last_timestamp=null;
	private String score = null;
	
	private String firstQuarterScore="-";
	private String secondQuarterScore="-";
	private String thirdQuarterScore="-";
	private String fourthQuarterString="-";
	
	private String comment = null;
	
	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment The comment to set.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	private String workflowType = "";
	
	public String getFirstQuarterScore() {
		return firstQuarterScore;
	}
	public void setFirstQuarterScore(String firstQuarterScore) {
		this.firstQuarterScore = firstQuarterScore;
	}
	public String getFourthQuarterScore() {
		return fourthQuarterString;
	}
	public void setFourthQuarterScore(String fourthQuarterString) {
		this.fourthQuarterString = fourthQuarterString;
	}
	public String getSecondQuarterScore() {
		return secondQuarterScore;
	}
	public void setSecondQuarterScore(String secondQuarterScore) {
		this.secondQuarterScore = secondQuarterScore;
	}
	public String getThirdQuarterScore() {
		return thirdQuarterScore;
	}
	public void setThirdQuarterScore(String thirdQuarterScore) {
		this.thirdQuarterScore = thirdQuarterScore;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public int getLast_scored_year() {
		return last_scored_year;
	}
	public void setLast_scored_year(int last_scored_year) {
		this.last_scored_year = last_scored_year;
	}
	

	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public int getLast_scored_quarter() {
		return last_scored_quarter;
	}
	public void setLast_scored_quarter(int last_scored_quarter) {
		this.last_scored_quarter = last_scored_quarter;
	}
	public Timestamp getLast_timestamp() {
		return last_timestamp;
	}
	public void setLast_timestamp(Timestamp last_timestamp) {
		this.last_timestamp = last_timestamp;
	}
	public String getLast_userID() {
		return last_userID;
	}
	public void setLast_userID(String last_userID) {
		this.last_userID = last_userID;
	}
	public String getQues_desc() {
		return ques_desc;
	}
	public void setQues_desc(String ques_desc) {
		this.ques_desc = ques_desc;
	}
	public String getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(String question_id) {
		this.question_id = question_id;
	}
	public String getQuestion_type() {
		return question_type;
	}
	public void setQuestion_type(String question_type) {
		this.question_type = question_type;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	/**
	 * @return Returns the workflowType.
	 */
	public String getWorkflowType() {
		return workflowType;
	}
	/**
	 * @param workflowType The workflowType to set.
	 */
	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

}
