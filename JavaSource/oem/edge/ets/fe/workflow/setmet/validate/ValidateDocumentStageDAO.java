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
 * Created on Nov 1, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.validate;

import java.util.ArrayList;

import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.qbr.qbrdocument.Question;
import oem.edge.ets.fe.workflow.setmet.document.ScorecardDAO;
import oem.edge.ets.fe.workflow.setmet.document.SetMetDocumentStageObject;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;
import oem.edge.ets.fe.workflow.util.OrderedMap;

import org.apache.commons.logging.Log;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ValidateDocumentStageDAO extends AbstractDAO {

		 /* (non-Javadoc)
		  * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObject(oem.edge.ets.fe.workflow.core.WorkflowObject)
		  */
		 public boolean saveWorkflowObject(WorkflowObject workflowObject) {
		 		 // TODO Auto-generated method stub
		 		 return false;
		 }

		 /* (non-Javadoc)
		  * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObject(java.lang.String)
		  */
		 public WorkflowObject getWorkflowObject(String ID) {
		 		 // TODO Auto-generated method stub
		 		 return null;
		 }

		 /* (non-Javadoc)
		  * @see oem.edge.ets.fe.workflow.core.AbstractDAO#saveWorkflowObjectList(java.util.ArrayList)
		  */
		 public boolean saveWorkflowObjectList(ArrayList object) {
		 		 // TODO Auto-generated method stub
		 		 return false;
		 }

		 /* (non-Javadoc)
		  * @see oem.edge.ets.fe.workflow.core.AbstractDAO#getWorkflowObjectList(java.lang.String)
		  */
		 public ArrayList getWorkflowObjectList(String ID) {
		 		 // TODO Auto-generated method stub
		 		 return null;
		 }
         private static int getByPassStatus(String projectID,String workflowID){
         	ValidateObject stage = new ValidateObject();
         	stage.setProjectID(projectID);
         	stage.setWorkflowID(workflowID);         	
         	String value = new ScorecardDAO().getByPassScoreStatus(stage);
         	
         	if("Y".equalsIgnoreCase(value))
         		return 1;
         	else
         		return 0;
         	
         }
		 public static int isStageCompleted(String projectID,String workflowID){
		 		 DBAccess db = null;
		 		 int completed  = 0;
		 		 int rows = 0;
		 		 DetailsUtils ud = new DetailsUtils();
	         	 ud.setWorkflowID(workflowID);
	         	 ud.setProjectID(projectID);
	         	 ud.extractWorkflowDetails();
	         	 String wf_type = ud.getWwf_type();	         		         	 			 		 
		 		 try{
		 		 		 db = new DBAccess();
		 		 		 if("SETMET".equalsIgnoreCase(wf_type)){
		 		 		 		db.prepareDirectQuery("SELECT COUNT(A.STATUS) CNT FROM ETS.WF_SCORE_MATRIX A,ETS.WF_SCORE_MATRIX B WHERE A.PROJECT_ID='"+projectID+"' AND A.WF_ID='"+workflowID+"' AND A.SCORE_MATRIX_ID=B.SCORE_MATRIX_ID AND A.STATUS ='I'  WITH UR");
		 		 		 		rows= db.execute();
		 		 		 		int scoreCnt = db.getInt(0,"CNT");
		 		 		 		if(scoreCnt==0){
		 		 		 			completed = completed+1;
		 		 		 		}
		 		 		 }else{
		 		 		 	 completed = 1;
		 		 		 }

		 		 		 db.prepareDirectQuery("SELECT COUNT(ISSUE_ID) CNT FROM ETS.WF_ISSUE_WF_MAP WHERE PROJECT_ID='"+projectID+"' AND WF_ID='"+workflowID+"' WITH UR");
		 		 		 rows = db.execute();
		 		 		 int createdIssues = db.getInt(0,"CNT");
		 		 		 if(createdIssues==0){
		 		 		 	completed = completed+4;
		 		 		 }
		 		 		 if(createdIssues != 0){
		 		 		 			db.prepareDirectQuery("SELECT COUNT(A.STATUS) CNT FROM ETS.WF_ISSUE A,ETS.WF_ISSUE_WF_MAP B WHERE B.PROJECT_ID='"+projectID+"' AND B.WF_ID= '"+workflowID+"' AND B.ISSUE_ID = A.ISSUE_ID AND UPPER(A.STATUS)='ASSIGNED'  WITH UR");
		 		 		 			rows = db.execute();
		 		 		 			int issueCnt = db.getInt(0,"CNT");
		 		 		 			if(issueCnt==0){
		 		 		 				completed=completed+2;
		 		 		 			}
		 		 		 }
		 		 		 db.close();
		 		 		 db=null;
		 		 }catch(Exception ex){
		 		 		 logger.error("the exception in validatedocumentstage getscoringmatrices",ex);
		 		 }
		 		 finally{
		 		 		 try{
		 		 		 		 if(db!=null){
		 		 		 		 		 db.close();
		 		 		 		 		 db=null;
		 		 		 		 }

		 		 		 }catch(Exception ex1){
		 		 		 		 logger.error("the exception in validatedocumentstage getscoringmatrices finally",ex1);
		 		 		 }
		 		 }
		 		 return completed;

		 }


		 /**
		     * Track the Workflow Stage change
		     * @param obj
		     * @return
		     */

			private static boolean trackWorkflowStageChange(WorkflowObject obj,DBAccess db,String oldVal,String newVal) throws Exception{
				boolean changes = false;
				ScorecardDAO dao = new ScorecardDAO();
				dao.updateSetMetHistoryforStage(obj,db,oldVal,newVal);
				changes =true;
				return changes;
			}


		 public static boolean updateWorkflowStage(String projectID,String workflowID,String stage,String loggedUser){
		 		 boolean valid = false;
		 		 DBAccess db  = null;
		 		 String stageName = "";

		 		 SetMetDocumentStageObject docObj = new SetMetDocumentStageObject();
		 		 docObj.setProjectID(projectID);
		 		 docObj.setWorkflowID(workflowID);
		 		 docObj.setLastUsr(loggedUser);

		 		 try{
		 		 		 db  = new DBAccess();
		 		 		 db.prepareDirectQuery("SELECT WF_CURR_STAGE_NAME CURRSTAGE FROM ETS.WF_DEF WHERE PROJECT_ID='"+projectID+"' AND WF_ID='"+workflowID+"' with ur");
		 		 		 int rows= db.execute();
		 		 		 if(rows>0){
		 		 		 		 stageName = db.getString(0,"CURRSTAGE");
		 		 		 }

		 		 		 if(WorkflowConstants.STAGECOMPLETED.equalsIgnoreCase(stageName))return false;


		 		 		 if(WorkflowConstants.DOCUMENT.equalsIgnoreCase(stageName)){

		 		 		 		 db.prepareDirectQuery("UPDATE ETS.WF_DEF SET WF_CURR_STAGE_NAME='"+stage+"' WHERE PROJECT_ID='"+projectID+"' AND WF_ID='"+workflowID+"'");
		 		 		 		 db.execute();
		 		 		 		 docObj.setStageID(MiscUtils.getWorkflowStageID(docObj.getProjectID(),docObj.getWorkflowID(),WorkflowConstants.DOCUMENT));
		 		 		 		 trackWorkflowStageChange(docObj,db,WorkflowConstants.DOCUMENT,WorkflowConstants.VALIDATE);
		 		 		 		 valid = true;
		 		 		 		 logger.debug("The stage is updated to validate");
		 		 		 }else{
		 		 		 		 db.prepareDirectQuery("UPDATE ETS.WF_DEF SET WF_CURR_STAGE_NAME='"+stage+"' WHERE PROJECT_ID='"+projectID+"' AND WF_ID='"+workflowID+"'");
		 		 		 		 db.execute();
		 		 		 		 docObj.setStageID(MiscUtils.getWorkflowStageID(docObj.getProjectID(),docObj.getWorkflowID(),WorkflowConstants.VALIDATE));
		 		 		 		 trackWorkflowStageChange(docObj,db,WorkflowConstants.VALIDATE,WorkflowConstants.STAGECOMPLETED);
		 		 		 		 valid = true;
		 		 		 		 logger.debug("The stage is completed");

		 		 		 }

		 		 		 db.doCommit();
		 		 		System.out.println("the db changes are committed %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

		 		 		 db.close();
		 		 		 db=null;
		 		 }catch(Exception ex){
		 		 		 logger.error("The exception in getCurrentStage of VALIDATEDOCUMENTSTAGE",ex);
		 		 }
		 		 finally{
		 		 		  try{
		 		 		  		 if(db!=null){
		 		 		  		 		 db.close();
		 		 		  		 		 db=null;
		 		 		  		 }
		 		 		  }catch(Exception ex1){
		 		 		  		 logger.error("The exception in getCurrentStage of VALIDATEDOCUMENTSTAGE finally",ex1);
		 		 		  }
		 		 }
		 		 return valid;
		 }


     /**
      * This method retieves the avergage score for each question in the scorecard
      */

	 public static boolean insertAverageScores(String projID,String workflowID,String uid){
	 	boolean result = false;
	 	if(getByPassStatus(projID,workflowID)==1)
	 		   return true;
	 	ScorecardDAO dao = new ScorecardDAO();
	 	OrderedMap avgRating = new OrderedMap();
	 	ArrayList quesList = dao.getQuestionIDs(projID,workflowID);
	 	if(quesList!=null && quesList.size() >0){

	 		for(int qCnt = 0; qCnt < quesList.size(); qCnt++){

	 			String qid = (String)quesList.get(qCnt);
	 		    String avg = String.valueOf(dao.getQuestionAverage(projID,workflowID,qid));
	 		    avgRating.put(qid,avg);
	 		}
	 		 result = insertAverageScores(avgRating,projID,workflowID,uid);
	 	}
	 	return result;
	 }
	 /**
	  * This method inserts th average for each questions in the workflow scorecard
	  * @param avgRating
	  * @return
	  */
	 private static boolean insertAverageScores(OrderedMap avgRating,String projID,String workflowID,String uid){
	 	boolean inserted = false;
	 	DBAccess db = null;
	 	String matrixID = "";
	 	try{
	 		matrixID = ETSCalendar.getNewCalendarId();
	 		db = new DBAccess();
	 		db.prepareQuery("insert_score_matrix");
	 		db.setString(1,projID);
	 		db.setString(2,workflowID);
	 		db.setString(3,matrixID);
	 		db.setString(4,"CLIENT_AVG");
	 		db.setString(5,"C");
	 		db.setString(6,uid);
	 		db.setObject(7,new java.sql.Timestamp(System.currentTimeMillis()));
	 		db.execute();
	 		db.clearListData();
	 		ArrayList keys = avgRating.keys();
	 		db.prepareQuery("insert_score");
	 		for(int qid=0;qid<keys.size();qid++){
	 			String key = (String) keys.get(qid);
	 			Float value = new Float(((String)avgRating.get(key)));
	 			int rating = value.intValue();

	 			db.setString(1,projID);
		 		db.setString(2,workflowID);
		 		db.setString(3,matrixID);
		 		db.setString(4,key);
		 		db.setInt(5,rating);
		 		db.setString(6,uid);
		 		db.setObject(7,new java.sql.Timestamp(System.currentTimeMillis()));
		 		db.execute();
		 		db.clearListData();

	 		}
	 		db.doCommit();
	 		db.close();
	 		db=null;
	 		inserted =true;
	 	}catch(Exception ex){
	 		db.doRollback();
	 		inserted = false;
	 		logger.error("The exception in insertAvgScores OrderedMap",ex);
	 	}finally{
	 		 try{
	 		 	if(db!=null){
	 		 		db.close();
	 		 		db=null;
	 		 	}

	 		 }catch(Exception ex1){
	 		 	logger.error("The exception in insertAvgScores OrderedMap finally",ex1);
	 		 }
	 	}

	 	return inserted;
	 }

/**
 * Checks wheather the stage can be moved or not
 * @param projectID
 * @param workflowID
 * @return
 */
	 public static boolean getCurrentStage(String projectID,String workflowID){
		boolean valid = false;
		DBAccess db  = null;
		String stageName = "";
		try{
			db  = new DBAccess();
			db.prepareDirectQuery("SELECT WF_CURR_STAGE_NAME CURRSTAGE FROM ETS.WF_DEF WHERE PROJECT_ID='"+projectID+"' AND WF_ID='"+workflowID+"' with ur");
			int rows= db.execute();
			if(rows>0){
				stageName = db.getString(0,"CURRSTAGE");
			}

			if("document".equalsIgnoreCase(stageName)){

				db.prepareDirectQuery("UPDATE ETS.WF_DEF SET WF_CURR_STAGE_NAME='VALIDATE' WHERE PROJECT_ID='"+projectID+"' AND WF_ID='"+workflowID+"'");
				db.execute();
				valid = true;
				logger.debug("The stage is updated to document");
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception ex){
			logger.error("The exception in getCurrentStage of ScorecardDAO",ex);
		}
		finally{
			 try{
			 	if(db!=null){
			 		db.close();
			 		db=null;
			 	}
			 }catch(Exception ex1){
			 	logger.error("The exception in getCurrentStage of scorecardDAO finally",ex1);
			 }
		}
		return valid;
	}
	 public static boolean generateStageID(String projectID,String workflowID,String loggedUser){
		boolean valid = false;
		DBAccess db  = null;

		try{
			db  = new DBAccess();
			db.prepareQuery("insert_validate_setmet");
			db.setString(1,projectID);
			db.setString(2,workflowID);
			db.setString(3,ETSCalendar.getNewCalendarId());
			db.setString(4,"C");
			db.setString(5,loggedUser);
			db.setObject(6,new java.sql.Timestamp(System.currentTimeMillis()));
			db.execute();
			db.doCommit();
			valid = true;
			db.close();
			db=null;
		}catch(Exception ex){
			logger.error("The exception in getCurrentStage of ScorecardDAO",ex);
			valid = false;
		}
		finally{
			 try{
			 	if(db!=null){
			 		db.close();
			 		db=null;
			 	}
			 }catch(Exception ex1){
			 	logger.error("The exception in getCurrentStage of scorecardDAO finally",ex1);
			 }
		}
		return valid;
	}



	 public static boolean updateNextMeetingDate(ValidateObject obj ){
	      DBAccess db = null;
			   boolean updated = true;

			   String month = null;
			   String year  = null;
			   String date = null;
			   float rating = 0.0f;
			   String nxtDate = obj.getNxtDate();
               if(obj.getRating()!=null){
               	  rating = Float.parseFloat(obj.getRating());
               }
			   if(nxtDate.indexOf("/")!=-1){
			   	  month = nxtDate.substring(0,(nxtDate.indexOf("/")));
			   }
             if(nxtDate.indexOf("/")!=-1 && nxtDate.indexOf("&")!=-1)
             	   date = nxtDate.substring(nxtDate.indexOf("/")+1,nxtDate.indexOf("&"));
			   if(nxtDate.indexOf("&")!=-1)
			   	  year = nxtDate.substring(nxtDate.indexOf("&")+1);
			   try{

			 		   db = new DBAccess();
			 		   db.prepareDirectQuery("UPDATE ETS.WF_STAGE_DOCUMENT_SETMET SET REPORT_TITLE='"+ obj.getReporttitle()+"' ,OVERAL_COMMENTS='"+obj.getComment()+"' , QBR_SUMMARY_REPORT = '"+obj.getSummary()+"', OVERAL_CLIENT_SAT_RATING="+rating+",NEXT_MEETING_DATE='"+year+"-"+month+"-"+date+"' WHERE PROJECT_ID='"+obj.getProjectID()+"' AND WF_ID='"+obj.getWorkflowID()+"'");
			 		   db.execute();
			 		   db.doCommit();
			 		   db.close();
			 		   db=null;
			   }catch(Exception ex){
			 		 		 db.doRollback();
			 		 		 updated= false;
			 		 		 logger.error("Exception occured in updating next meting date",ex);

			   }finally{

			 		 		 try{
			 		 		 		  if(db!=null){
			 		 		 		 		   db.close();
			 		 		 		 		   db = null;
			 		 		 		  }
			 		 		 }catch(Exception ex1){
			 		 		 		 logger.error("Exception occured in updating next meting date finally",ex1);
			 		 		 }

			   }

        return updated;
	 }
	 public static boolean saveAttachment(String projectID,String workflowID,int docID,String loggedUser){
	 	 DBAccess db = null;
		   boolean updated = true;
		   try{

		 		   db = new DBAccess();
		 		   db.prepareQuery("insert_attachment");// UPDATE ETS.WF_STAGE_DOCUMENT_SETMET SET NEXT_MEETING_DATE=? WHERE PROJECT_ID=? AND WORKFLOW_ID=?
		 		   db.setString(1,projectID);
		 		   db.setString(2,workflowID);
		 		   db.setInt(3,docID);
		 		   db.setString(4,loggedUser);
		 		   db.setObject(5,new java.sql.Timestamp(System.currentTimeMillis()));

		 		   db.execute();
		 		   db.doCommit();
		 		   db.close();
		 		   db=null;
		   }catch(Exception ex){
		 		 		 db.doRollback();
		 		 		 updated= false;
		 		 		 logger.error("Exception occured in saveAttachment",ex);

		   }finally{

		 		 		 try{
		 		 		 		  if(db!=null){
		 		 		 		 		   db.close();
		 		 		 		 		   db = null;
		 		 		 		  }
		 		 		 }catch(Exception ex1){
		 		 		 		 logger.error("Exception occured in saveAttachment finally",ex1);
		 		 		 }

		   }

		   return updated;

	 }
	 public static ArrayList getAttachmentIDs(String projectID,String workflowID){
	 	   DBAccess db = null;
		   ArrayList idList = new ArrayList();
		   try{

		 		   db = new DBAccess();
		 		   db.prepareQuery("get_doc_ids");// UPDATE ETS.WF_STAGE_DOCUMENT_SETMET SET NEXT_MEETING_DATE=? WHERE PROJECT_ID=? AND WORKFLOW_ID=?
		 		   db.setString(1,projectID);
		 		   db.setString(2,workflowID);
		 		   int rows = db.execute();
		 		   if(rows>0){
		 		   	  for(int cnt = 0;cnt<rows;cnt++){
		 		   	  	  idList.add(String.valueOf(db.getInt(cnt,"DID")));
		 		   	  }
		 		   }
		 		   db.close();
		 		   db=null;
		   }catch(Exception ex){
		 		 		 db.doRollback();
		 		 		 logger.error("Exception occured in getAttachmentIDs",ex);

		   }finally{

		 		 		 try{
		 		 		 		  if(db!=null){
		 		 		 		 		   db.close();
		 		 		 		 		   db = null;
		 		 		 		  }
		 		 		 }catch(Exception ex1){
		 		 		 		 logger.error("Exception occured in getAttachmentIDs finally",ex1);
		 		 		 }

		   }

		   return idList;

	 }

	 public static boolean deleteAttachment(int docID){
	 	DBAccess db = null;
	 	boolean deleted = false;

	 	try{
	 		db= new DBAccess();
	 		db.prepareDirectQuery("DELETE FROM ETS.WF_DOC_ATTACHMENTS WHERE DOC_ID="+docID);
	 		db.execute();
	 		deleted = true;
	 		db.doCommit();
	 		db.close();
	 		db = null;
	 	}catch(Exception ex){
	 		db.doRollback();
	 		deleted = false;
	 		logger.error("the exception in deleteattachment validateDocumentStageDAO",ex);
	 	}finally{
	 		  try{
	 		  	if(db!=null){
	 		  		db.close();
	 		  		db=null;
	 		  	}
	 		  }catch(Exception ex1){
	 		  	logger.error("the exception in deleteattachment finally validateDocumentStageDAO",ex1);
	 		  }
	 	}
	 	return deleted;
	 }
	 
     public static String getScorecardStatus(String workflowID){
     	  DBAccess db = null;
     	  String status="I";
     	  try{
     	  	  db = new DBAccess();
     	  	  db.prepareDirectQuery("SELECT STATUS STAT FROM ETS.WF_SCORE_MATRIX WHERE WF_ID='"+workflowID+"' AND UPPER(SCORED_BY)='CLIENT' WITH UR");
     	  	  db.execute();
     	  	int rows = db.execute();
    	 	if(rows>0){
    	 		  status = db.getString(0,"STAT");
    	 	}
    	 	db.doCommit();
    	 	db.close();
    	 	db = null;
     	  	  
     	  }catch(Exception ex){
     	  	 logger.error("exception if getscorecardstatus validatestage",ex);
     	  }finally{
     	  	  try{
     	  	         if(db!=null){
     	  	         	db.close();
     	  	         	db = null;
     	  	         }
     	  	  }catch(Exception finale){
     	  	  		logger.error("exception if getscorecardstatus finally validatestage",finale); 	
     	  	  }
     	  }
     	 return status;  
     }  
	 
  private static Log logger = WorkflowLogger.getLogger(ValidateDocumentStageDAO.class);
}
