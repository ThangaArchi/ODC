//Source file: E:\\test\\aic\\JavaSource\\oem\\edge\\ets\\fe\\workflow\\qbr\\qbrdocument\\QBRDocumentAction.java

package oem.edge.ets.fe.workflow.qbr.qbrdocument;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.util.OrderedMap;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class QBRDocumentAction extends WorkflowAction 
{
   
   /**
   @roseuid 45C87114034B
    */
   public QBRDocumentAction() 
   {
    
   }
   public  ActionForward executeWorkflow(ActionMapping mapping,
		WorkflowForm form,
		HttpServletRequest request,
		HttpServletResponse response)
throws IOException, ServletException{
   	
   	/**
   	 * 1) Get the workflow type and get all the questions as question objects 
   	 */
   	 
   	
   	
   	  QbrBL bObject  = new QbrBL();
   	  QBRDocumentStage obj = new QBRDocumentStage();
   	  OrderedMap questions = bObject.getQuestions(obj);
   	  
   	
   	
   	  return null;
   }
   
}
