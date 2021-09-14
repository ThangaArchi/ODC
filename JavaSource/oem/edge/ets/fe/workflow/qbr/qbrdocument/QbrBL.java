//Source file: E:\\test\\aic\\JavaSource\\oem\\edge\\ets\\fe\\workflow\\qbr\\qbrdocument\\QbrBL.java

package oem.edge.ets.fe.workflow.qbr.qbrdocument;

import java.util.HashMap;

import oem.edge.ets.fe.workflow.core.WorkflowStage;
import oem.edge.ets.fe.workflow.util.OrderedMap;

public class QbrBL 
{
   
   /**
   @roseuid 45C8711403D8
    */
   public QbrBL() 
   {
    
   }
   
   public String getScoreMatrixStatus(WorkflowStage card){
    String status = "I";	
    try{
    	 QbrDAO dao = new QbrDAO();
    	 status = dao.getScoreMatrixStatus(card);
    }catch(Exception ex){
    	  
    }
	return status;
}
   
   
   public OrderedMap getQuestions(WorkflowStage obj) 
   {
   	  OrderedMap questions = new OrderedMap();
     try{
     	 QbrDAO dao = new QbrDAO();
     	 questions = dao.getTemplateQuestions(obj);
     }catch(Exception ex){
     	  
     }
      return questions;
   }
   
   /**
   @return java.util.HashMap
   @roseuid 45B734340167
    */
   public HashMap getPrevQtrValues() 
   {
    return null;
   }
   
   /**
   @param scorecard
   @return boolean
   @roseuid 45B73449031C
    */
   public boolean saveScores(QBRDocumentStage scorecard) 
   {
   	boolean saveFlg = false;
   	 try{
   	 	   QbrDAO dao = new QbrDAO();
   	 	   saveFlg=dao.saveWorkflowObject(scorecard);
   	 }catch(Exception ex){
   	 	
   	 }
    return saveFlg;
   }
   
   /**
   @param question
   @return boolean
   @roseuid 45B73490009C
    */
   public boolean addQuestion(String question) 
   {
    return true;
   }
   
   /**
   @param workflowID
   @return oem.edge.ets.fe.workflow.qbr.qbrdocument.QBRScorecard
   @roseuid 45B734AF0177
    */
   public QBRScorecard getScores(String workflowID) 
   {
    return null;
   }
}
