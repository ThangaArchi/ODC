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

import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.core.WorkflowStage;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScorecardBL {
		 
		 private static Log logger = WorkflowLogger.getLogger(ScorecardBL.class);
		 
		 /**
		  * This method retrieves the list of client attendees with their scorematrix ID
		  * @param ID
		  * @return
		  */
		 public SetMetDocumentStageObject getWorkflowObject(WorkflowObject obj,boolean createMatrix){
		 		 SetMetDocumentStageObject object = null;
		 		 ScorecardDAO dao = null;
		 		 try{
		 		 		 dao = new ScorecardDAO();
		 		 		 if(createMatrix)
		 		 		 		 object=(SetMetDocumentStageObject)dao.getWorkflowObject(obj);
		 		 		 else
		 		 		 		 object=(SetMetDocumentStageObject)dao.getWorkflowObjectView(obj);
		 		 }catch(Exception ex){
		 		 		 logger.debug("The exception is scorecardBL getWorkflowObject",ex);
		 		 }
		 		 return object;
		 }
		 
		 public boolean trackWorkflowStageChange(WorkflowStage obj){
		 	 boolean changes = false;
		 	 try{
		 	 ScorecardDAO dao = new ScorecardDAO();
		 	 dao.trackWorkflowStageChange(obj,WorkflowConstants.PREPARE,WorkflowConstants.DOCUMENT);
		 	 changes= true;
		 	 }catch(Exception ex){
		 	 	logger.debug("The exception is scorecardBL",ex);
		 	 }
		 	 return changes;
		 }
		 
		 public boolean isValidStage(String projectID,String workflowID){
		 		  boolean valid = false;
		 		  ScorecardDAO dao = null;
		 		  try{
		 		  		 dao = new ScorecardDAO();
		 		  		 valid = dao.getCurrentStage(projectID,workflowID);
		 		  		 
		 		  }catch(Exception ex){
		 		  		 logger.debug ("The exception is scorecardBL getCuurentStage",ex);
		 		  }
		 		  
		 		  return valid;
		 }
		 public String getWorkflowName(String projID,String workflowID){
		 	String wName = "";
		 	try{
		 		ScorecardDAO dao = new ScorecardDAO();
		 		wName= dao.getWorkflowName(projID,workflowID);
		 	}catch(Exception ex){
		 		logger.debug ("The exception is scorecardBL getWorkflowName",ex);
		 	}
		 	return wName;
		 }
}
