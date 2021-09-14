//Source file: E:\\test\\aic\\JavaSource\\oem\\edge\\ets\\fe\\workflow\\qbr\\qbrdocument\\QBRScorecard.java

package oem.edge.ets.fe.workflow.qbr.qbrdocument;

import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.util.OrderedMap;

public class QBRScorecard extends WorkflowObject 
{
   private String overallComments = null;
   private String scoredBy = null;
   private String scoreLevel = "client";
   private OrderedMap quesMap;
   private String status  = "C";
   private String matrixID = null;
   
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
 * @return Returns the status.
 */
public String getStatus() {
	return status;
}
/**
 * @param status The status to set.
 */
public void setStatus(String status) {
	this.status = status;
}
   //private Question theQuestion;
   
   /**
   @roseuid 45C8711501E4
    */
   public QBRScorecard() 
   {
    
   }
/**
 * @return Returns the overallComments.
 */
public String getOverallComments() {
	return overallComments;
}
/**
 * @param overallComments The overallComments to set.
 */
public void setOverallComments(String overallComments) {
	this.overallComments = overallComments;
}
/**
 * @return Returns the quesMap.
 */
public OrderedMap getQuesMap() {
	return quesMap;
}
/**
 * @param quesMap The quesMap to set.
 */
public void setQuesMap(OrderedMap quesMap) {
	this.quesMap = quesMap;
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
 * @return Returns the scoreLevel.
 */
public String getScoreLevel() {
	return scoreLevel;
}
/**
 * @param scoreLevel The scoreLevel to set.
 */
public void setScoreLevel(String scoreLevel) {
	this.scoreLevel = scoreLevel;
}
/**
 * @return Returns the theQuestion.

public Question getTheQuestion() {
	return theQuestion;
}

 * @param theQuestion The theQuestion to set.

public void setTheQuestion(Question theQuestion) {
	this.theQuestion = theQuestion;
}*/
 
 

}
