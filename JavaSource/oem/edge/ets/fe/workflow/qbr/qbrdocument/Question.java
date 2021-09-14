//Source file: E:\\test\\aic\\JavaSource\\oem\\edge\\ets\\fe\\workflow\\qbr\\qbrdocument\\Question.java

package oem.edge.ets.fe.workflow.qbr.qbrdocument;

import oem.edge.ets.fe.workflow.core.WorkflowObject;


public class Question extends WorkflowObject
{
   private String quesID = null;
   private String quesDesc = null;
   private String rating = null;
   private String qType = null;
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
/**
 * @return Returns the qType.
 */
public String getQType() {
	return qType;
}
/**
 * @param type The qType to set.
 */
public void setQType(String type) {
	qType = type;
}
/**
 * @return Returns the quesDesc.
 */
public String getQuesDesc() {
	return quesDesc;
}
/**
 * @param quesDesc The quesDesc to set.
 */
public void setQuesDesc(String quesDesc) {
	this.quesDesc = quesDesc;
}
/**
 * @return Returns the quesID.
 */
public String getQuesID() {
	return quesID;
}
/**
 * @param quesID The quesID to set.
 */
public void setQuesID(String quesID) {
	this.quesID = quesID;
}
/**
 * @return Returns the rating.
 */
public String getRating() {
	return rating;
}
/**
 * @param rating The rating to set.
 */
public void setRating(String rating) {
	this.rating = rating;
}
   /**
   @roseuid 45C871150261
    */
   public Question() 
   {
    
   }
}
