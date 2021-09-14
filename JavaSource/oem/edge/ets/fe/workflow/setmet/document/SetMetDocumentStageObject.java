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

import java.sql.Date;
import java.util.ArrayList;

import oem.edge.ets.fe.workflow.stage.DocumentStageObject;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetDocumentStageObject extends DocumentStageObject {

	 private ArrayList scorecard = null;
	 private ArrayList issues	 = null;
	 private String scoringLevel = "A";
	 private String overallScore = null;
	 private Date   nextMeeting  = null;
	 private String comments	 = null;
	 private String clientName	 = null;
	 private String lastUsr		 = null;
		 
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
	 public SetMetDocumentStageObject(){
	 	 scorecard = new ArrayList();
		 issues	 = new ArrayList();
	 }	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return Returns the issues.
	 */
	public ArrayList getIssues() {
		return issues;
	}
	/**
	 * @param issues The issues to set.
	 */
	public void setIssues(ArrayList issues) {
		this.issues = issues;
	}
	/**
	 * @return Returns the nextMeeting.
	 */
	public Date getNextMeeting() {
		return nextMeeting;
	}
	/**
	 * @param nextMeeting The nextMeeting to set.
	 */
	public void setNextMeeting(Date nextMeeting) {
		this.nextMeeting = nextMeeting;
	}
	/**
	 * @return Returns the overallScore.
	 */
	public String getOverallScore() {
		return overallScore;
	}
	/**
	 * @param overallScore The overallScore to set.
	 */
	public void setOverallScore(String overallScore) {
		this.overallScore = overallScore;
	}
	/**
	 * @return Returns the scorecard.
	 */
	public ArrayList getScorecard() {
		return scorecard;
	}
	/**
	 * @param scorecard The scorecard to set.
	 */
	public void setScorecard(ArrayList scorecard) {
		this.scorecard = scorecard;
	}
	/**
	 * @return Returns the scoringLevel.
	 */
	public String getScoringLevel() {
		return scoringLevel;
	}
	/**
	 * @param scoringLevel The scoringLevel to set.
	 */
	public void setScoringLevel(String scoringLevel) {
		this.scoringLevel = scoringLevel;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowStage#getWorkflowType()
	 */
	public String getWorkflowType() {		
		return workflowType;
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
}
