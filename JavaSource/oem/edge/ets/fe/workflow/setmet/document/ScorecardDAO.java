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


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.core.WorkflowStage;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.qbr.qbrdocument.QBRDocumentStage;
import oem.edge.ets.fe.workflow.util.OrderedMap;

import org.apache.commons.logging.Log;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScorecardDAO extends AbstractDAO {


	private static Log logger = WorkflowLogger.getLogger(ScorecardDAO.class);
	/* (non-Javadoc)
	 *
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
		return null;
	}
	public ArrayList getQBRWorkflowDefinitions(WorkflowStage object)
	{

		ArrayList workflowDefinitionsList = new ArrayList();
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		String project_id = object.getProjectID();
		String wf_id= 		object.getWorkflowID();
		
String query=	 "select a.wf_id wf_id, a.creation_date creation_date, b.schedule_date meeting_date,  "+
							 "a.quarter quarter1, a.year year1 from ets.wf_def a, ets.ets_calendar b   "+
							 "where a.project_id='"+project_id+"' and lower(a.wf_type)=lower('"+object.getWorkflowType()+"') and a.meeting_id=b.calendar_id and  "+
							 " (( (select quarter from ets.wf_def where wf_id='"+wf_id+"')-a.quarter+(("+
							 "cast((select year from ets.wf_def where wf_id='"+wf_id+"') as int)-cast("+
							 "a.year as int))*4) BETWEEN 0 and 4 ) and   a.creation_date <= (select creation_date "+
							 "from ets.wf_def where project_id='"+project_id+"' and lower(wf_type)=lower('"+object.getWorkflowType()+"') and wf_id='"+wf_id+
							 "')) and a.project_id=b.project_id and  a.wf_curr_stage_name='Complete' "+
							 "order by a.creation_date desc ,b.schedule_date desc with ur";

//			db.prepareDirectQuery("SELECT A.PROJECT_ID PROJ, A.WF_ID WID FROM ETS.WF_DEF A WHERE LOWER(A.WF_TYPE)=LOWER('"+object.getWorkflowType()+"') AND a.wf_curr_stage_name ='Complete' and A.PROJECT_ID='"+object.getProjectID()+"' AND  A.CREATION_DATE <=(SELECT X.CREATION_DATE FROM ETS.WF_DEF X WHERE X.WF_ID='"+object.getWorkflowID()+"') ORDER BY A.CREATION_DATE DESC with ur");

		/*
		 * 
		 String selectQuery = "select wf_id, meeting_date from ets.wf_def a, ets.ets_calendar where project_id='"+
							 project_id+"' and WF_CURR_STAGE_NAME='Complete' order by creation_date desc";

		/*
		String selectQuery = "select wf_id from ets.wf_def where project_id='"+
		 					  project_id+"' order by creation_date desc";
		*/
		logger.debug(query);
		try
		{
			int i = 0;
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(query);
		    logger.debug("********************************************************") ;
		    while(resultSet.next())
		    {
		    	WorkflowDefinition workflowDef = new WorkflowDefinition();
		    	workflowDef.setWorkflowID(resultSet.getString("wf_id"));
		    	Timestamp timestamp = resultSet.getTimestamp("meeting_date");
		    	long longDate = timestamp.getTime();
		    	Format formatter = new SimpleDateFormat("MM/dd/yyyy");
		    	String meetingDate = formatter.format(new Date(longDate));
		    	workflowDef.setMeetingDate(meetingDate);
		    	workflowDef.setQuarter(new Integer(resultSet.getInt("quarter1")).toString());
		    	workflowDef.setYear(resultSet.getString("year1"));
		    	workflowDef.setCreationDate(resultSet.getDate("creation_date"));
		    	workflowDefinitionsList.add(workflowDef);
		    	i++;
		    	if(i > 3)break;
		    	logger.debug("********************************************************");

		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getWorkflowDefinitions() method is completed ");


		return workflowDefinitionsList;

	}
   /**
    * 
    * @param byPass
    */
	
	public void updateByPassScore(WorkflowStage obj){
		DBAccess db = null;
		char score = 'N';
		QBRDocumentStage byPass = (QBRDocumentStage)obj;
		try{
			
			db= new DBAccess();
		    if(byPass.isBypass())
		    	score='Y';
	 		db.prepareDirectQuery("UPDATE ETS.WF_STAGE_DOCUMENT_SETMET SET BYPASS_SCORE='"+score+"' WHERE PROJECT_ID='"+byPass.getProjectID()+"' AND WF_ID='"+byPass.getWorkflowID()+"'");
	 		int rows = db.execute();
	 		db.doCommit();
	 		db.close();
	 		db=null;
		}catch(Exception ex){
	 		db.doRollback();
	 	}finally{
	 		  try{
	 		  	  if(db!=null){
	 		  	  	 db.close();
	 		  	  	 db = null;
	 		  	  }
	 		  }catch(Exception finale){
	 		  	
	 		  }
	 	}
		
	}
	
	public String getByPassScoreStatus(WorkflowStage stage){
		DBAccess db = null;
		String byPass = "N";
		
		try{
			
			db= new DBAccess();
	 		db.prepareDirectQuery("SELECT BYPASS_SCORE PASS FROM ETS.WF_STAGE_DOCUMENT_SETMET WHERE PROJECT_ID='"+stage.getProjectID()+"' AND WF_ID='"+stage.getWorkflowID()+"'");
	 		int rows = db.execute();
	 		if(rows>0){
	 			  byPass = db.getString(0,"PASS");
	 		}
	 		db.doCommit();
	 		db.close();
	 		db=null;
		}catch(Exception ex){
	 		db.doRollback();
	 	}finally{
	 		  try{
	 		  	  if(db!=null){
	 		  	  	 db.close();
	 		  	  	 db = null;
	 		  	  }
	 		  }catch(Exception finale){
	 		  	
	 		  }
	 	}
		logger.debug("the value of byPass score is ######################************"+byPass);
		if(byPass==null || byPass.trim().length()==0)
			return "N";
		else
			return byPass;
	
	}
	/**
	 * This method returns the color for the given color code
	 * @param code
	 * @return Color
	 */
	private String getColor(String code){
		String color = "0";
		  if("1".equalsIgnoreCase(code))
		  	   color =  WorkflowConstants.GREEN;
		  if("2".equalsIgnoreCase(code))
		  	   color = WorkflowConstants.YELLOW;
		  if("3".equalsIgnoreCase(code))
		  	   color =  WorkflowConstants.RED;
		  return color;
	}
	
	/**
	 * This method gets the average of ratings for a given matrixID
	 * @param matrixID
	 * @return int
	 */
	 private float getAverageOnMatrixID(String matrixID,OrderedMap ratingsPerQuestion){
	 	DBAccess db = null;
	 	float average = 0.0f;
	 	int count = 0;
	 	int sum = 0;
	 	try{
	 		db= new DBAccess();
	 		db.prepareDirectQuery("SELECT A.QUESTION_ID QID,B.QUES_DESC DESC,A.RATING RATE FROM ETS.WF_SCORE A,ETS.WF_SCORE_MATRIX C,ETS.WF_SCORE_QUESTION_TEMPLATE B  WHERE B.QUESTION_ID=A.QUESTION_ID AND A.SCORE_MATRIX_ID='"+matrixID+"' AND C.SCORE_MATRIX_ID=A.SCORE_MATRIX_ID AND C.SCORED_BY!='CLIENT_AVG' ");
	 		int rows = db.execute();
	 		for(int x = 0;x <rows ;x++){
	 			if(!WorkflowConstants.STOPLIGHT_COLOR.equalsIgnoreCase(db.getString(x,"DESC"))){
	 			
	 			  System.out.println("the value of the Question is %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+db.getString(x,"QID")+"%%%%%%%%%%%%%%%%%%%%%"+db.getString(x,"RATE"));
	 			  
	 			  if(!"0".equalsIgnoreCase(db.getString(x,"RATE"))){
	 			  			  ratingsPerQuestion.put(db.getString(x,"QID"),db.getString(x,"RATE"));
	 			  }else{
	 			  			  ratingsPerQuestion.put(db.getString(x,"QID"),"-");
	 			  }
	 			}else{
	 				System.out.println("the value of the Question is %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+db.getString(x,"QID") + "%%%%%%%%%%%%%%%%%%%%%"+db.getString(x,"RATE"));
	 				String color = getColor(db.getString(x,"RATE"));
	 				if(!"0".equalsIgnoreCase(color)){
	 					ratingsPerQuestion.put(db.getString(x,"QID"),color);
	 				}else{
	 					ratingsPerQuestion.put(db.getString(x,"QID"),"-");
	 				}
	 			}
	 			  if(!WorkflowConstants.STOPLIGHT_COLOR.equalsIgnoreCase(db.getString(x,"DESC")) && !WorkflowConstants.OVERALL_RATING.equalsIgnoreCase(db.getString(x,"DESC"))){
	 			  		if(db.getInt(x,"RATE")!=0){
	 			  			count++;
	 			  			sum = sum+db.getInt(x,"RATE");
	 			  		}
	 			  }
	 		}
	 		average = ((float)sum/count);
	 		db.doCommit();
	 		db.close();
	 		db=null;
	 	}catch(Exception ex){
	 		db.doRollback();
	 	}finally{
	 		  try{
	 		  	  if(db!=null){
	 		  	  	 db.close();
	 		  	  	 db = null;
	 		  	  }
	 		  }catch(Exception finale){
	 		  	
	 		  }
	 	}
	 	
	 	 return average;
	 }
	
	/**
	 * This method finds the overall scorematrix average client level of qbr
	 * @param object
	 * @return int
	 */
	public float getAverage(WorkflowStage object){
		float average = 0.0f;
		OrderedMap ratingsPerQuestion = new OrderedMap();
		ArrayList matrixList = getAllScoringMatrices(object.getProjectID(),object.getWorkflowID());
		for(int cnt = 0 ;cnt <matrixList.size();cnt++){
			ScoringMatrix sm = (ScoringMatrix)matrixList.get(cnt);
			average = average + getAverageOnMatrixID(sm.getScoreMatrixID(),ratingsPerQuestion);
		}		
		return average;
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	 /**
	  * This method sums up the rating value of a question across scoring matrices
	  * @param quesRating
	  * @param matrix
	  */
	
	private void getRatingSummationPerQuestion(OrderedMap matrix,OrderedMap quesRating){
		
		   if(quesRating!=null){
		   	       ArrayList keys = quesRating.keys();
		   	       for(int x = 0; x< keys.size(); x++){
		   	       	        String key = (String)keys.get(x);
		   	       	        if(matrix.containsKey(key)){
		   	       	        	   String val1= (String)quesRating.get(key);
		   	       	        	   String val2 = (String)matrix.get(key);
		   	       	        	   matrix.put(key,String.valueOf(Integer.parseInt(val1)+Integer.parseInt(val2)));
		   	       	        	   
		   	       	        }else{
		   	       	        	  matrix.put(key,(String)quesRating.get(key));
		   	       	        }
		   	       }
		   }
		
		
	}
	
	/**
	 * Get OverAll History at attendee  level
	 * @param object
	 * @return
	 */
	public OrderedMap getQBRSelfHistory(WorkflowStage object){
		
		 
		ArrayList matrixList = getAllScoringMatrices(object.getProjectID(),object.getWorkflowID());
		for(int cnt = 0 ;cnt <matrixList.size();cnt++){
			ScoringMatrix sm = (ScoringMatrix)matrixList.get(cnt);
		}
		 return null;
	}
	
	/**
	 * This method sums the rating values for a given question across the scorematrixIDs
	 * @param object
	 *
	 */
	private OrderedMap getAverageOnAttendees(ArrayList matrixID){
		 DBAccess db = null;
		 OrderedMap matrix = new OrderedMap();
		 try{
		 	if(matrixID!=null && matrixID.size()>0){
		 		for(int x = 0;x<matrixID.size() ; x++){
		 			    OrderedMap quesRating =new OrderedMap();
		 			    String matID = (String)matrixID.get(x);
		 			    getAverageOnMatrixID(matID,quesRating);
		 			    getRatingSummationPerQuestion(matrix,quesRating);
		 		}
		 	}
		 	
		 }catch(Exception ex){
		 	
		 }finally{
		 	   try{
		 	   	
		 	   }catch(Exception finale){
		 	   	
		 	   }
		 }
		
		
		 return matrix;
	}
	/**
	 * This method returns the questions description of questionIDs in a scoring matrix 
	 * @param object
	 * @return
	 */
	private OrderedMap getMatrixQuestions(OrderedMap workflowID){
		DBAccess db = null;
		OrderedMap questions = new OrderedMap();
		OrderedMap tempQues = new OrderedMap();
		try{   
			    db= new DBAccess();
				ArrayList key = workflowID.keys();
				for(int x = 0;x<key.size();x++){
		  	       ArrayList matrix = (ArrayList)workflowID.get((String)key.get(x)); //Get the list of matrix for a given workflowID
		  	     		for(int j = 0;j<matrix.size();j++){
		  	       			ScoringMatrix sm = (ScoringMatrix)matrix.get(j);
		  	       			String matrixID = sm.getScoreMatrixID();
		  	       			db.prepareDirectQuery("SELECT B.QUESTION_ID QID,A.QUES_DESC DESC FROM ETS.WF_SCORE B,ETS.WF_SCORE_QUESTION_TEMPLATE A WHERE B.SCORE_MATRIX_ID='"+matrixID+"' AND A.QUESTION_ID=B.QUESTION_ID ORDER BY A.QUESTION_TYPE,A.QUESTION_ID,A.QUES_DESC");
		  	       			int rows = db.execute();
		  	       			for(int cnt = 0;cnt <rows ; cnt++){
		  	       				 String qid = db.getString(cnt,"QID");
		  	       				 String qDesc = db.getString(cnt,"DESC");
		  	       				 if(!WorkflowConstants.STOPLIGHT_COLOR.equalsIgnoreCase(qDesc) && !WorkflowConstants.OVERALL_RATING.equalsIgnoreCase(qDesc) ){
		  	       				    if(!questions.containsKey(qid)){
		  	       				 	    questions.put(qid,qDesc);
		  	       				    }
		  	       				 }else{
		  	       				if(!tempQues.containsKey(qid))
		  	       				 	 tempQues.put(qid,qDesc);
		  	       				 }
		  	       			}
		  	     		}
				}
				
				
				
				
				
				ArrayList keys = tempQues.keys();
				System.out.println(":the value of the keys is &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+keys);
				if(keys!=null){
					 for(int x = 0;x<keys.size(); x++){
					 	   String q = (String)tempQues.get((String)keys.get(x));
					 	   questions.put((String)keys.get(x),q);
					 	   
					 }
				}
				
				
				db.doCommit();
				db.close();
				db = null;
		}catch(Exception ex){
			
		}finally{
			  try{
			  	  if(db!=null){
			  	  	db.close();
			  	  	db = null;
			  	  }
			  }catch(Exception finale){
			  	
			  }
		}
		  
		  
		return questions;
		}
	
	/**
	 * This method get the ratings of the previous 4 QBR
	 * @param object
	 * @ return OrderedMap
	 */
	 
	public OrderedMap getHistory(WorkflowStage object){
		OrderedMap prevWorkflow = new OrderedMap();
	    OrderedMap questions    = new OrderedMap();
	    OrderedMap history      = new OrderedMap();
		OrderedMap names        = new OrderedMap();
		
			  prevWorkflow = getPrevRatings(object); // Workflows and their ScoreMatrices
			  
			  logger.debug("the value of Keys is &&&&&&&&&&&&&&&&&&&&&&&&"+prevWorkflow);
			  
			  
			  //names = (OrderedMap)prevWorkflow.get("NAMES");
			  //questions = getMatrixQuestions((OrderedMap)prevWorkflow.get("ID"));
			  questions = getMatrixQuestions(prevWorkflow);
			  //prevWorkflow = (OrderedMap)prevWorkflow.get("ID");
			  ArrayList key = prevWorkflow.keys();
			  
			  
			  for(int x = 0;x<key.size();x++){
			  	       ArrayList matrix = (ArrayList)prevWorkflow.get((String)key.get(x)); //Get the list of matrix for a given workflowID
			  	     		for(int j = 0;j<matrix.size();j++){
			  	       			OrderedMap QuesRating	= new OrderedMap();
			  	       			OrderedMap quest        = new OrderedMap();
			  	       			ScoringMatrix sm = (ScoringMatrix)matrix.get(j);			  	       			
			  	       			getAverageOnMatrixID(sm.getScoreMatrixID(),QuesRating); //QuesRating is populated with Question and Rating of all the Questions of a given matrix ID
			  	       			if(QuesRating.size()>0)
			  	       				prevWorkflow.put((String)key.get(x),QuesRating);// Workflows and their Questions with Ratings
			  	       			
			  	     		}
			  	       /*else{
			  	       	       OrderedMap QuesRating = new OrderedMap();
			  	       	       QuesRating = getAverageOnAttendees(matrix);
			  	       	       prevWorkflow.put((String)key.get(x),QuesRating);
			  	       	      
			  	       }*/
			  }
			  
			  
	    history.put("WORKFLOWID",questions);
	    history.put("RATINGS",prevWorkflow);
	    //history.put("NAMES",names);
		return history;
	}
	/**
	 * 
	 * @param company
	 * @return QuestionList
	 */
	public ArrayList getLocalQBRQuestions(WorkflowStage company){
		QBRDocumentStage stage = (QBRDocumentStage)company;
		ArrayList qList = new ArrayList();
		DBAccess db  =null;
		
		try{
			db = new DBAccess();
			db.prepareDirectQuery("SELECT QUESTION_ID QID FROM ETS.WF_SCORE_QUESTION_TEMPLATE WHERE LOWER(WF_TYPE)=LOWER('"+stage.getWorkflowType()+"') AND COMPANY='"+stage.getClientName()+"' AND QUESTION_TYPE='L' ");
			int rows = db.execute();
			for(int x = 0 ;x<rows;x++){
				  qList.add(db.getString(x,"QID"));
			}
			db.doCommit();
			db.close();
		}catch(Exception ex){
			db.doRollback();
		}finally{
			 try{
			 	 if(db!=null){
			 	 	  db.close();
			 	 	  db=null;
			 	 }
			 }catch(Exception finale){
			 	
			 }
		}
		
		return qList;
	}
	
	/**
	 * This method retrieves the last 4 workflow's scorematrix id
	 * @param object
	 */
	private OrderedMap getPrevRatings(WorkflowStage object){
		
		DBAccess db = null;
		OrderedMap workflowDetail = new OrderedMap();
		OrderedMap workflowMatrix = new OrderedMap();
		OrderedMap workflowNames = new OrderedMap();
		String project_id = object.getProjectID();
		String wf_id= object.getWorkflowID();
		try{
			db = new DBAccess();
			String query=	 "select a.wf_id wf_id, a.creation_date creation_date, b.schedule_date meeting_date,  "+
			 "a.quarter quarter1, a.year year1 from ets.wf_def a, ets.ets_calendar b   "+
			 "where a.project_id='"+project_id+"' and lower(a.wf_type)=lower('"+object.getWorkflowType()+"') and a.meeting_id=b.calendar_id and  "+
			 " (( (select quarter from ets.wf_def where wf_id='"+wf_id+"')-a.quarter+(("+
			 "cast((select year from ets.wf_def where wf_id='"+wf_id+"') as int)-cast("+
			 "a.year as int))*4) BETWEEN 0 and 4 ) and   a.creation_date <= (select creation_date "+
			 "from ets.wf_def where project_id='"+project_id+"' and lower(wf_type)=lower('"+object.getWorkflowType()+"') and wf_id='"+wf_id+
			 "')) and a.project_id=b.project_id and  a.wf_curr_stage_name='Complete' "+
			 "order by a.creation_date desc ,b.schedule_date desc with ur";


//			db.prepareDirectQuery("SELECT A.PROJECT_ID PROJ, A.WF_ID WID FROM ETS.WF_DEF A WHERE LOWER(A.WF_TYPE)=LOWER('"+object.getWorkflowType()+"') AND a.wf_curr_stage_name ='Complete' and A.PROJECT_ID='"+object.getProjectID()+"' AND  A.CREATION_DATE <=(SELECT X.CREATION_DATE FROM ETS.WF_DEF X WHERE X.WF_ID='"+object.getWorkflowID()+"') ORDER BY A.CREATION_DATE DESC with ur");
            db.prepareDirectQuery(query);
			int rows = db.execute();
			logger.debug("the total no of rows Returned !!!!!!!!!!!!!!!!!!!!!!!!!"+rows);
			if(rows>4)
				 rows =4;
			for(int x = 0;x < rows ; x++){
				logger.debug("the total no of rows Returned !!!!!!!!!!!!!!!!!!!!!!!!!"+x);
				   String workflow_id= db.getString(x,"wf_id");
				   String proj = project_id;
		         		   
				   workflowMatrix.put(workflow_id,getAllScoringMatrices(proj,workflow_id));
				   //workflowNames.put(wf_id,time);
			}
			//workflowDetail.put("NAMES",workflowNames);
			//workflowDetail.put("ID",workflowMatrix);
			db.doCommit();
			db.close();
			db = null;
		}catch(Exception ex){
			db.doRollback();
		}finally{
			  try{
			  	  if(db!=null){
			  	  	db.close();
			  	  	db=null;
			  	  }
			  }catch(Exception finale){
			  	
			  }
		}
		 return workflowMatrix;
	}
    /**
     * This Method is used to add the history data into the history table.
     * @param newobj
     * @param db
     * @throws SQLException
     * @throws Exception
     */
    public void updateSetMetHistoryforStage(WorkflowObject newobj,DBAccess db,String oldval,String newval) throws SQLException, Exception{
    String HistoryID = ETSCalendar.getNewCalendarId();
    
    updateSetmetHistory(newobj,db,HistoryID);
    updateSetmetHistoryFileds(newobj,db,oldval,newval,HistoryID);
    }
    
    
    /**
     * This method is used to insert the History information in the WF_HISTORY_FIELD table for the Stage change.
	 * @param newobj
	 * @param db
	 */
	private void updateSetmetHistoryFileds(WorkflowObject newobj, DBAccess db,String oldval,String newval,String HistoryID) throws Exception{
		
		
		db.prepareDirectQuery("INSERT INTO ETS.WF_HISTORY_FIELD (WF_HISTORY_ID,PROJECT_ID," +
								"FIELD_CHANGED,PREVIOUS_VALUE,NEW_VALUE)VALUES (?,?,?,?,?)");
		
		db.setString(1, HistoryID); //Unique ID to identify the WF History
		db.setString(2, newobj.getProjectID()); //Project_id of the workspace in which this workflow will be created.Parent key -> ETS_PROJECTS
		db.setString(3, "Current Stage"); // 
		db.setString(4, oldval); //
		db.setString(5, newval); //
		
		db.execute();
	}
	/**
	 * This method is used to insert the history data into WF_HISTORY fileds for the stage change.
	 * @param newobj
	 * @param db
	 */
	private void updateSetmetHistory(WorkflowObject newobj, DBAccess db,String HistoryID)throws Exception {
	
		
		
		
		SetMetDocumentStageObject newobj1 = (SetMetDocumentStageObject)newobj;
	     	
		
		
		db.prepareDirectQuery("INSERT INTO ETS.WF_HISTORY (WF_HISTORY_ID,PROJECT_ID,WF_ID," +
	               "WF_RESOURCE_ID,ACTION,ACTION_BY,ACTION_DATE,COMMENT,LAST_TIMESTAMP)VALUES " +
	               "(?,?,?,?,?,?,?,?,?)");
		db.setString(1, HistoryID); //Unique ID to identify the WF History
		db.setString(2, newobj1.getProjectID()); //Project_id of the workspace in which this workflow will be created.Parent key -> ETS_PROJECTS
		db.setString(3, newobj1.getWorkflowID()); // Unique ID to identify the Workflow.Parent key -> WF_DEF
		java.sql.Timestamp currentts = new java.sql.Timestamp(System.currentTimeMillis());
		db.setString(4, newobj1.getStageID()); //WF_RESOURCE_ID = Either ISSUE_ID or WF_STAGE_Id
		db.setString(5, "Set/Met Modified"); //Actions done; MODIFIED etc
		db.setString(6, newobj1.getLastUsr()); //IBM ID of the  profile creator/updater
		java.sql.Date actiondate = new java.sql.Date(new java.util.Date().getTime());
		db.setDate(7, actiondate); //Date on which the action has taken
		db.setString(8,"Set/Met Stage Modified"); //Comments
		db.setObject(9,currentts);
		
		db.execute();
	}

    
    
    
    /**
     * Track the Workflow Stage change
     * @param obj
     * @return
     */
	
	public boolean trackWorkflowStageChange(WorkflowStage obj,String oldVal,String newVal){
		boolean changes = false;
		DBAccess db = null;
		try{
			db = new DBAccess();			
			updateSetMetHistoryforStage(obj,db,oldVal,newVal);
			db.doCommit();
			db.close();
			db=null;
			changes =true;
		}catch(Exception ex){
			db.doRollback();
			changes= false;
			logger.debug("The exception in recording the stage change history",ex);
		}finally{
			
			try{
				if(db!=null){
					db.close();
					db=null;
				}
			}catch(Exception ex1){
				logger.debug("The exception in recording the stage change history finally",ex1);
			}
			
		}
		
		return changes;
	}
	
	public WorkflowObject getWorkflowObjectView(WorkflowObject obj){
			  DBAccess db = null;
			  Scorecard card = null;
			  ArrayList matrices = new ArrayList();
			  SetMetDocumentStageObject docObj = (SetMetDocumentStageObject)obj;
			   String selectQuery = "select a.score_matrix_id matrixid, a.status status, CONCAT(concat(b.fname,' '), b.lname) name,C.next_meeting_date dt,C.overal_comments comment,C.report_title tlt,C.qbr_summary_report rep,C.OVERAL_CLIENT_SAT_RATING rtng from ETS.WF_STAGE_DOCUMENT_SETMET C, ETS.WF_score_matrix a, ets.wf_client b"+
		         " where a.project_id='"+docObj.getProjectID()+"' and a.wf_id='"+docObj.getWorkflowID()+"' and a.wf_id = c.wf_id and a.scored_by=b.client_id and upper(a.scored_by)!='CLIENT_AVG'";

			 

			   logger.debug("the select query is %%%%%%%%%%%%%"+selectQuery);
			  try{
			   db  = new DBAccess();
			   db.prepareDirectQuery(selectQuery);
			   int rows = db.execute();
			   for(int cnt = 0;cnt < rows ; cnt++){
			    card = new Scorecard();
			    card.setMatrixID(db.getString(cnt,"matrixid"));
			    card.setMeetingDate(db.getString(cnt,"dt"));
			    card.setOverallcomment(db.getString(cnt,"comment"));
			    card.setReporttitle(db.getString(cnt,"tlt"));
			    card.setSummaryreport(db.getString(cnt,"rep"));
			    card.setOverallrating(db.getString(cnt,"rtng"));
				
			    if("I".equalsIgnoreCase(db.getString(cnt,"status")))
			     card.setScorecardStatus('I');
			    else
			     card.setScorecardStatus('C');
			                String clientName= db.getString(cnt,"name");
			                if(clientName==null || "".equals(clientName.trim()))
			                 card.setScoredBy(docObj.getClientName());
			                else
			                 card.setScoredBy(clientName);
			    matrices.add(card);
			 
			   }
			   
			   /*db.clearListData();
			   db.prepareDirectQuery("select a.score_matrix_id matrixid, a.status status from ETS.WF_score_matrix a where a.project_id='"+docObj.getProjectID()+"' and a.wf_id='"+docObj.getWorkflowID()+"' and a.scored_by='CLIENT'" );
			   int row = db.execute();
			   if(row>0){
			    card = new Scorecard();
			    card.setMatrixID(db.getString(0,"matrixid"));
			    if("I".equalsIgnoreCase(db.getString(0,"status")))
			     card.setScorecardStatus('I');
			    else
			     card.setScorecardStatus('C');
			    String clientName= db.getString(0,"name");
			    card.setScoredBy(docObj.getClientName());
			    matrices.add(card);
			   }*/
			            
			            
			   docObj.setScorecard(matrices);
			   db.close();
			   db = null;
			  }catch(Exception ex){
			   logger.error("the exception in getWorkflowObjectView is",ex);
			  }
			  finally{
			   try{
			    if(db!=null){
			     db.close();
			     db=null;
			    }
			   }catch(Exception ex1){
			    logger.error("the exception in getWorkflowObjectView finally is",ex1);
			   }
			  }
			 
			  return docObj;
			 
}
	public WorkflowObject getWorkflowObject(WorkflowObject obj){
		DBAccess db = null;
		Scorecard card = null;
		SetMetDocumentStageObject docObj = (SetMetDocumentStageObject)obj;

		try{
			db = new DBAccess();
			db.prepareQuery("insert_setmet_document");
			docObj.setStageID(ETSCalendar.getNewCalendarId());
			db.setString(1,docObj.getProjectID());
			db.setString(2,docObj.getWorkflowID());
			db.setString(3,docObj.getStageID());
			db.setString(4,docObj.getLastUsr());
			db.setObject(5,new java.sql.Timestamp(System.currentTimeMillis()));
			db.execute();
			db.clearListData();
			ArrayList scorecardList = populateScoreCard(db,docObj);
			logger.debug("the size of the scorecard list is ^^^^^^^^^^^^^^^^^^^"+scorecardList.size());


			db.prepareQuery("insert_score_matrix");
			for(int x =0;x<scorecardList.size();x++){
				 card = (Scorecard)scorecardList.remove(x);
				 card.setMatrixID(ETSCalendar.getNewCalendarId());

				 logger.debug("the value os scorematrix id is******************************"+card.getMatrixID());


				 db.setString(1,docObj.getProjectID());
				 db.setString(2,docObj.getWorkflowID());
				 db.setString(3,card.getMatrixID());
				 db.setString(4,card.getScorerID());
				 db.setString(5,String.valueOf(card.getScorecardStatus()));
				 db.setString(6,docObj.getLastUsr());
				 db.setObject(7,new java.sql.Timestamp(System.currentTimeMillis()));
				 db.execute();
				 scorecardList.add(x,card);
				 db.clearListData();
			}

			logger.debug("the insert score is going to get executed ######################################");
			db.prepareQuery("insert_score");
			for(int j = 0;j<scorecardList.size();j++){
				 card = (Scorecard)scorecardList.get(j);
				 OrderedMap om = card.getQestRating();
				 ArrayList key = om.keys();

				 logger.debug("the xise of the question ids is @@@@@@@@@@@@@@@@@@@@@@2"+key.size());

				 for(int pk=0;pk<key.size();pk++){
				 	 db.setString(1,docObj.getProjectID());
				 	 db.setString(2,docObj.getWorkflowID());
				 	 db.setString(3,card.getMatrixID());
				 	 db.setString(4,(String)key.get(pk));
				 	 db.setString(5,String.valueOf(0));
				 	 db.setString(6,docObj.getLastUsr());
				 	 db.setObject(7,new java.sql.Timestamp(System.currentTimeMillis()));
				 	 db.execute();
				 	 db.clearListData();
				 }
			}
			docObj.setScorecard(scorecardList);
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception ex){
			db.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(db!=null){
				try{
					db.close();
					db=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}
		return docObj;
	}
	/**
	    *  This method returns the list of client attendees
	    *  @param workflowID string,projectId string
	    *  @return clientList ArrayList
	    */
		public ArrayList populateScoreCard(DBAccess db,SetMetDocumentStageObject obj)throws Exception{
			ArrayList scorecard = new ArrayList();
			Scorecard score = null;
	        OrderedMap hm = null;
	        
	        db.prepareQuery("check_scoring_type");
	        db.setString(1,obj.getProjectID());
			db.setString(2,obj.getWorkflowID());
			int resultset = db.execute();
			String scoringType="";
			if(resultset>0){
			    scoringType = db.getString(0,"LEVEL");
			}
			db.clearListData();
			if("A".equalsIgnoreCase(scoringType)){
				
					db.prepareQuery("get_scorers");
					db.setString(1,obj.getProjectID());
					db.setString(2,obj.getWorkflowID());

					logger.debug("the query is @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2"+db.getQuery());
					int rows=db.execute();

					logger.debug("The total rows rtned is asasasasasassa"+rows);
					for(int x=0;x<rows;x++){
						score = new Scorecard();
						score.setScoredBy(db.getString(x,"NAME"));
						score.setScorerID(db.getString(x,"USRID"));
						score.setMeetingDate(db.getString(x,"DT"));
						scorecard.add(score);
					}
			}else{
				  score =new Scorecard();
				  score.setScoredBy("CLIENTLEVEL");
				  score.setScorerID("CLIENT");
				  scorecard.add(score);
			}
			
			
			db.clearListData();
			logger.debug("Inside populat escore card size of scorecard is"+scorecard.size());
			db.prepareDirectQuery("SELECT TEMPL.QUESTION_ID QID FROM ETS.WF_SCORE_QUESTION_TEMPLATE TEMPL WHERE (TEMPL.COMPANY = '"+obj.getClientName()+"' OR UPPER(TEMPL.QUESTION_TYPE)='B') AND LOWER(WF_TYPE)=LOWER('"+obj.getWorkflowType()+"')");
			//db.setString(1,obj.getClientName());
			//db.setString(2,obj.getWorkflowType());

			int ques=db.execute();
			for(int list=0;list<scorecard.size();list++){
				score = (Scorecard)scorecard.remove(list);
				hm = new OrderedMap();
				for(int j=0;j<ques;j++){
					hm.put(db.getString(j,"QID"),String.valueOf(0));
				}
				score.setQestRating(hm);
				scorecard.add(list,score);
			}
			db.clearListData();
			logger.debug("Method is completed ssasasasasassssssssssssssssssssssssss");
			return scorecard;
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

	public ArrayList getBaseQuestions(Scorecard scorecard) throws SQLException
	{
		ArrayList questionsList = new ArrayList();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		String projectID = scorecard.getProjectID();
		logger.debug(projectID);
		String workflowID= scorecard.getWorkflowId();
		logger.debug(workflowID);
		String matrixID = scorecard.getMatrixID();
		logger.debug(matrixID);

		try
		{
		    String selectCardQuery =
		    					" select distinct (a.question_id), a.ques_desc, a.version, " +
								" a.company, a.question_type, a.last_scored_quarter, " +
								" a.last_scored_year, a.last_userid, a.last_timestamp, " +
								" b.rating " +
								" from 	ets.wf_score_question_template a , " +
								" ets.wf_score b " +
								" where  lower(a.wf_type)='setmet' and 	b.question_id=a.question_id " +
								" and 	b.wf_id=? " +
								" and 	b.project_id=?" +
								" and 	b.score_matrix_id=? " +
								" and 	a.question_type='B'" ;
			String selectQuery =" select distinct(question_id), ques_desc, version, " +
								" company, question_type, last_scored_quarter, " +
								" last_scored_year, last_userid, last_timestamp " +
								" from 	ets.wf_score_question_template  " +
								" where	lower(wf_type)='setmet' and question_type='B'" ;

			con = dbAccess.getConnection();
			if(matrixID.equals(""))
				{
				stmt = con.prepareStatement(selectQuery);
				}
			else
				{
				stmt = con.prepareStatement(selectCardQuery);
				stmt.setString(1, workflowID);
			    stmt.setString(2, projectID);
			    stmt.setString(3,matrixID);
				}

		    resultSet = stmt.executeQuery();
		    while(resultSet.next())
		    {
		    	questionsList.add(getQuestion(resultSet, matrixID));
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getBaseQuestions() method is completed ");

		return questionsList;

	}
	private Question getQuestion(ResultSet resultSet, String matrixID)
	{
		Question question = new Question();
		try
		{
		question.setQuestion_id(resultSet.getString("QUESTION_ID").trim());
		question.setQues_desc(resultSet.getString("QUES_DESC").trim());
		question.setVersion(resultSet.getInt("VERSION"));
		//question.setCompany((resultSet.getString("COMPANY")).trim());
		question.setQuestion_type(resultSet.getString("QUESTION_TYPE").trim());
		question.setLast_scored_quarter(resultSet.getInt("LAST_SCORED_QUARTER"));
		question.setLast_scored_year(resultSet.getInt("LAST_SCORED_YEAR"));
		//question.setLast_userID(resultSet.getString("LAST_USERID").trim());
		question.setLast_timestamp(resultSet.getTimestamp("LAST_TIMESTAMP"));
		if(!matrixID.equals(""))
			{
				question.setScore(new Integer(resultSet.getInt("RATING")).toString());
			}
		else
			{
				question.setScore("0");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return question;
	}
	public String getWorkflowName(String projId, String wfId) throws SQLException
	{
		String workflowName = null;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();


		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement("select WF_NAME from ets.wf_def where project_id='"+projId+"' and wf_id='"+wfId+"' with ur");
		    resultSet = stmt.executeQuery();
		    while(resultSet.next())
		    {
		    	workflowName = resultSet.getString("WF_NAME");
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getWorkflowName() method is completed ");

		return workflowName;

	}
	public String addWFScore(Scorecard scorecard) throws SQLException
	{
		String workflowName = null;
		Connection con = null;
		PreparedStatement stmt = null;
		DBAccess dbAccess = new DBAccess();
		String status = null;
		ArrayList questions = scorecard.getQuestions();

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement("update ets.wf_score set rating=?, last_userid=? , last_timestamp=current timestamp" +
		    							"	where project_id=? and wf_id=? and score_matrix_id=? and question_id=?");
		    for(int i = 0; i < questions.size(); i++)
		    {
		    	Question question = (Question)questions.get(i);

		    	stmt.setInt(1,new Integer(question.getScore().trim()).intValue());
		    	stmt.setString(2,scorecard.getLast_userId());
		    	stmt.setString(3, scorecard.getProjectID());
		    	stmt.setString(4, scorecard.getWorkflowId());
		    	stmt.setString(5, scorecard.getMatrixID());
		    	stmt.setString(6, question.getQuestion_id());

		    	int rowsAffected = stmt.executeUpdate();
		    	if(i > 0)
		    		status = "success";
		    	else
		    		status="failure";

		    }



		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("addWFScore() method is completed ");

		return status;

	}

	public boolean updateScorecard(Scorecard scorecard)
	{
		boolean updateScorecard = false;


		String workflowName = null;
		Connection con = null;
		Statement stmt = null;
		DBAccess dbAccess = new DBAccess();
		String projectID = scorecard.getProjectID();
		String workflowID = scorecard.getWorkflowId();
		String matrixID = scorecard.getMatrixID();



		try
		{
			logger.debug("scorecard.getProjectID()"+projectID);
			logger.debug("scorecard.getWorkflowID()"+workflowID);
			logger.debug("scorecard.getMatrixID()"+matrixID);

			String updateQuery = "update ets.wf_score_matrix "+
								 "set status='C' where project_id='"+projectID+"' and wf_id='"+workflowID+
								 "' and score_matrix_id='"+matrixID+"'";
			logger.debug(updateQuery);
			con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    logger.debug("After the create statement");
		    int rowsAffected = stmt.executeUpdate(updateQuery);
		    logger.debug("After the execute statement==>"+rowsAffected);
	    	if(rowsAffected > 0)
	    		updateScorecard = true;
	    	else
	    		updateScorecard=false;
	    	logger.debug("After the everything is done");
		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("updateScorecard() method is completed ");

		return updateScorecard;
	}

	public String getScorecardStatus(Scorecard scorecard)
	{
		String scorecardStatus = "";


		String workflowName = null;
		Connection con = null;
		Statement stmt = null;
		DBAccess dbAccess = new DBAccess();
		ResultSet resultSet = null;

		try
		{
			String projectID = scorecard.getProjectID();
	    	String workflowID = scorecard.getWorkflowId();
	    	String matrixID = scorecard.getMatrixID();
	    	String selectQuery = "select status from ets.wf_score_matrix where project_id='"+projectID+
								 "' and wf_id='"+workflowID+"' and score_matrix_id='"+
								 matrixID+"'";
			con = dbAccess.getConnection();
		    stmt = con.createStatement();

		    resultSet = stmt.executeQuery(selectQuery);


		    while(resultSet.next())
		    {
		    	scorecardStatus = resultSet.getString("status");
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getScorecardStatus() method is completed ");

		return scorecardStatus;
	}

	public boolean addQuestion(Question question)
	{
		boolean addQuestionStatus = false;

		String questionID = question.getQuestion_id();
		String questionDesc = question.getQues_desc();
		char questionType = 'L';
		String company=question.getCompany();
		int last_scored_quarter = question.getLast_scored_quarter();
		int last_scored_year = question.getLast_scored_year();
		String userid = question.getLast_userID();
		Timestamp last_timestamp = question.getLast_timestamp();
		String comment = question.getComment();
		String workflowName = null;
		Connection con = null;
		Statement stmt = null;
		DBAccess dbAccess = new DBAccess();
		ResultSet resultSet = null;

		try
		{
			String insertQuery = "INSERT INTO ETS.WF_SCORE_QUESTION_TEMPLATE(QUESTION_ID, QUES_DESC, COMPANY, QUESTION_TYPE, LAST_SCORED_QUARTER, LAST_SCORED_YEAR, LAST_USERID, LAST_TIMESTAMP,WF_TYPE) "+
								 "VALUES ('"+questionID+"','"+questionDesc+"','"+company+"','"+questionType+"',"+last_scored_quarter+","+last_scored_year+",'"+userid+"','"+last_timestamp+"','"+question.getWorkflowType()+"')";
			logger.debug(insertQuery);
			con = dbAccess.getConnection();
		    stmt = con.createStatement();

		    int rowsAffected = stmt.executeUpdate(insertQuery);
		    if(rowsAffected > 0)
		    {
		    	addQuestionStatus = true;
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("addQuestion() method is completed ");

		return addQuestionStatus;

	}
	public boolean addScorecardQuestion(Scorecard scorecard)
	{
		boolean addScorecardQuestionStatus = false;
		int intRating = 0;
		String projectID = scorecard.getProjectID();
		String workflowID = scorecard.getWorkflowId();
		String matrixID = scorecard.getMatrixID();
		String questionID = scorecard.getQuestionID();
		String rating = scorecard.getRating();
		if(rating.equals("Select a rating"))
		{
		intRating = 0;
		}
		else
		{
			intRating = Integer.parseInt(rating);	
		}

		String workflowName = null;
		Connection con = null;
		Statement stmt = null;
		DBAccess dbAccess = new DBAccess();
		ResultSet resultSet = null;

		try
		{
			String insertQuery = "INSERT INTO ETS.WF_SCORE(project_id, wf_id, score_matrix_id, question_id, rating,rating_comments) "+
								 "VALUES ('"+projectID+"','"+workflowID+"','"+matrixID+"','"+questionID+"',"+intRating+" ,'"+scorecard.getOverallcomment()+"')";
			logger.debug(insertQuery);
			con = dbAccess.getConnection();
		    stmt = con.createStatement();

		    int rowsAffected = stmt.executeUpdate(insertQuery);
		    if(rowsAffected > 0)
		    {
		    	addScorecardQuestionStatus = true;
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("addScorecardQuestion() method is completed ");

		return addScorecardQuestionStatus;

	}
	public String getMaxQuestionID()
	{
		int questionID = 0;

		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		String selectQuery = "select question_id from ets.WF_SCORE_QUESTION_TEMPLATE";
		ArrayList numbersList = new ArrayList();
		int maxNumber = 0;

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);

		    while(resultSet.next())
		    {
		    	String str = resultSet.getString("question_id");
		    	if(str!=null && str.length()>0){
		    	    logger.debug("the value of str is $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+str.trim());
		    	   	questionID = Integer.parseInt(str.trim());
		    	   	numbersList.add(new Integer(questionID));
		    	}
		    }

		    	Iterator iter = numbersList.iterator();

		    	while(iter.hasNext())
		    	{
		    		int tempNumber = 0;
		    		Integer objMaxNumber = (Integer)iter.next();
		    		tempNumber = objMaxNumber.intValue();
		    		if(tempNumber > maxNumber)
		    		{
		    			maxNumber = tempNumber;
		    		}

		    	}

		    	logger.debug("maxNumber====>"+maxNumber);
		    	
		    	logger.debug("maxNumber======================================= ====>"+maxNumber);
		    	maxNumber++;


		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getMaxQuestionID() method is completed ");


		return new Integer(maxNumber).toString();

	}

	private ArrayList getQuestionIDs(Scorecard scorecard)
	{
		ArrayList questionIDList = new ArrayList();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		String projectID = scorecard.getProjectID();
		logger.debug(projectID);
		String workflowID= scorecard.getWorkflowId();
		logger.debug(workflowID);
		String matrixID = scorecard.getMatrixID();
		logger.debug(matrixID);


		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement
							(" select b.question_id qid"+
								" from 	ets.wf_score_question_template a , " +
								" ets.wf_score b " +
								" where 	b.question_id=a.question_id " +
								" and 	b.score_matrix_id=? " +
								" and 	b.wf_id=? " +
								" and 	b.project_id=?" +
								" and 	a.question_type='L'" );

		    stmt.setString(1, matrixID);
		    stmt.setString(2, workflowID);
		    stmt.setString(3, projectID);

		    resultSet = stmt.executeQuery();
		    while(resultSet.next())
		    {
		    	questionIDList.add(resultSet.getString("qid"));
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getQuestionIDs() method is completed ");


		return questionIDList;
	}
	public ArrayList getLocalQuestions(Scorecard scorecard, String company) throws SQLException
	{
		ArrayList questionsList = new ArrayList();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		String projectID = scorecard.getProjectID();
		logger.debug(projectID);
		String workflowID= scorecard.getWorkflowId();
		logger.debug(workflowID);
		String matrixID = scorecard.getMatrixID();
		logger.debug(matrixID);

		String selectQuery =" select distinct a.question_id, a.ques_desc, a.version, " +
							" a.company, a.question_type, a.last_scored_quarter, " +
							" a.last_scored_year, a.last_userid, a.last_timestamp, " +
							" b.rating " +
							" from 	ets.wf_score_question_template a , " +
							" ets.wf_score b " +
							" where lower(a.wf_type)='setmet' and b.question_id=a.question_id " +
							" and 	a.company=?  "+
							" and   b.score_matrix_id=? "+
							" and 	a.question_type='L'" ;
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement(selectQuery);
		    logger.debug("getting local questions"+selectQuery);
		    stmt.setString(1, company);
		    stmt.setString(2, matrixID);
		    resultSet = stmt.executeQuery();
		    while(resultSet.next())
		    {
		    	Question tempQuestion = getQuestion(resultSet, matrixID);
		    	logger.debug("isAnswered(tempQuestion)==>"+isAnswered(tempQuestion));
		    	if(isAnswered(tempQuestion))
		    		questionsList.add(tempQuestion);
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getLocalQuestions() method is completed ");

		return questionsList;

	}
	public ArrayList getLocalQuestions(String company) throws SQLException
	{
		ArrayList questionsList = new ArrayList();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		String selectQuery =" select question_id , ques_desc, version, " +
							" company, question_type, last_scored_quarter, " +
							" last_scored_year, last_userid, last_timestamp " +
							" from 	ets.wf_score_question_template " +
							" where company='"+company+"' " +
							" and question_type='L'" ;
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement(selectQuery);
		    logger.debug("getting local questions"+selectQuery);
		    resultSet = stmt.executeQuery();
		    while(resultSet.next())
		    {
		    	Question tempQuestion = getQuestion(resultSet,"");
		    	logger.debug("isAnswered(tempQuestion)==>"+isAnswered(tempQuestion));
		    	if(isAnswered(tempQuestion))
		    		questionsList.add(tempQuestion);
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getLocalQuestions() method is completed ");

		return questionsList;

	}

	public ArrayList getClientAttendees(String companyName)
	{
		ArrayList clientAttendeesList = new ArrayList();
		int questionID = 0;

		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		String selectQuery = "select CONCAT(concat(fname,' '), lname) name  from ets.WF_client where company='"+companyName+"'";
		ArrayList numbersList = new ArrayList();
		int maxNumber = 0;

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);

		    while(resultSet.next())
		    {
		    	clientAttendeesList.add(resultSet.getString("name"));
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getClientAttendees() method is completed ");


		return clientAttendeesList;

	}
	public ArrayList getClientAttendees(Scorecard scorecard)
	{
		ArrayList projectClientAttendeesList = new ArrayList();
		int questionID = 0;

		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		String projectId = scorecard.getProjectID();
		String workflowId = scorecard.getWorkflowId();

		String selectQuery = "select b.scored_by client_id  from ets.WF_client a, ets.WF_SCORE_MATRIX b  "+
							 "where b.project_id ='"+projectId+"' "+" and  b.wf_id='"+workflowId+"' and b.scored_by=a.client_id";
		ArrayList numbersList = new ArrayList();
		int maxNumber = 0;

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);

		    while(resultSet.next())
		    {
		    	projectClientAttendeesList.add(getClientAttendeeName(resultSet.getString("client_id")));
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}
		logger.debug("getClientAttendees() method is completed ");
		return projectClientAttendeesList;
	}
	public ArrayList getQuestionIDs(String project_id, String wf_id)
	{
		ArrayList questionIDList = new ArrayList();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		logger.debug("project_id====>"+project_id);
		logger.debug("workflow id====>"+wf_id);

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement
							(   " select b.question_id qid, a.ques_desc desc"+
								" from 	ets.wf_score_question_template a , " +
								" ets.wf_score b " +
								" where 	b.question_id=a.question_id " +
								" and 	b.wf_id=? " +
								" and 	b.project_id=?" );

		    stmt.setString(1, wf_id);
		    stmt.setString(2, project_id);

		    resultSet = stmt.executeQuery();
		    while(resultSet.next())
		    {
		    	String qDesc = resultSet.getString("desc");
		    	if(!WorkflowConstants.STOPLIGHT_COLOR.equalsIgnoreCase(qDesc) && !WorkflowConstants.OVERALL_RATING.equalsIgnoreCase(qDesc))
		    			questionIDList.add(resultSet.getString("qid"));
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getQuestionIDs() method is completed ");


		return questionIDList;
	}
	public ArrayList getQuestions(String company)
	{
		ArrayList questionsList = new ArrayList();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement
							(   " select question_id, ques_desc, version, question_type, last_scored_quarter,  "+
								" last_scored_year "+
								" from 	ets.wf_score_question_template  " +
								" where lower(wf_type)='setmet' and (question_type='B' " +
								" or 	COMPANY=? )" );

		    stmt.setString(1, company);

		    resultSet = stmt.executeQuery();
		    while(resultSet.next())
		    {
		    	Question question = new Question();
		    	question.setQues_desc(resultSet.getString("ques_desc"));
		    	question.setQuestion_id(resultSet.getString("question_id"));
		    	question.setCompany(company);
		    	question.setQuestion_type(resultSet.getString("question_type"));
		    	question.setLast_scored_quarter(resultSet.getInt("last_scored_quarter"));
		    	question.setLast_scored_year(resultSet.getInt("last_scored_year"));

		    	if(question.getQuestion_type().equals("B"))
		    		questionsList.add(question);
		    	else if(question.getQuestion_type().equals("L") && isAnswered(question))
		    		questionsList.add(question);

		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getQuestionIDs() method is completed ");


		return questionsList;
	}
	public String getQuestionDesc(String question_id)
	{
		String questionDesc = "";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();


		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement
							(   " select ques_desc "+
								" from 	ets.wf_score_question_template  " +
								" where question_id='"+question_id+"'" );

		    resultSet = stmt.executeQuery();

		    while(resultSet.next())
		    {
		    	questionDesc = resultSet.getString("ques_desc");
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getQuestionDesc() method is completed ");


		return questionDesc;
	}
	public float getQuestionAverage(String project_id, String wf_id, String question_id)
	{
		float questionAverage = 0.00f;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		//logger.debug("project_id====>"+project_id);
		//logger.debug("wf_id===>"+wf_id);
		//logger.debug("question_id===>"+question_id);
		String selectQuery = "select rating  from ets.wf_score where project_id='"+
							 project_id+"' and wf_id='"+wf_id+"' and "+
							 "question_id='"+question_id+"'";
		logger.debug("ratingQuery===>"+selectQuery);
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement(selectQuery);
		    int i = 0;
		    int j = 0;
		    resultSet = stmt.executeQuery();
		    while(resultSet.next())
		    {
		    	int tempRating = resultSet.getInt("rating");
		    	if(tempRating != 0)
		    	{
		    		logger.debug(tempRating+"<br />");
		    		i = i+ tempRating;
		    		j++;
		    	}
		    }
		    
		    if(j != 0)
		    	{	
			    	logger.debug(new Integer(i).toString());
			    	logger.debug(new Integer(j).toString());
			    	
			    	float if1 = i;
			    	
			    	questionAverage = if1/j;
			    	
			    	logger.debug("questionAverage"+questionAverage+"<br />");
			    	
			    	questionAverage = getPreciseFloat(questionAverage);
			    	
			    	logger.debug(new Float(questionAverage).toString());
		    	}
		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getQuestionIDs() method is completed ");

		return questionAverage;
	}
	public float getClientQuestionAverage(String project_id, String wf_id, String question_id)
	{
		float questionClientAverage = 0.00f;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		//logger.debug("project_id====>"+project_id);
		//logger.debug("wf_id===>"+wf_id);
		//logger.debug("question_id===>"+question_id);
		String selectQuery = "select b.rating rating from ets.wf_score_matrix a, ets.wf_score b where b.project_id='"+
							 project_id+"' and b.wf_id='"+wf_id+"' and "+
							 "b.question_id='"+question_id+"' and a.scored_by='CLIENT_AVG' and a.project_id=b.project_id "+
							 " and b.wf_id=a.wf_id";
		logger.debug(selectQuery);
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement(selectQuery);
		    int i = 0;
		    resultSet = stmt.executeQuery();
		    while(resultSet.next())
		    {
		    	questionClientAverage = resultSet.getInt("rating");
		    }
		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getCleintQuestionAverage cacthblock",ex);
			logger.debug("the execption is getCleintQuestionAverage is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getCleintQuestionAverage finally block",e);
				}
			}
		}

		logger.debug("getClientQuestionAverage() method is completed ");

		return questionClientAverage;
	}
	public Scorecard addScoringMatrix(Scorecard scorecard)
	{
		boolean addingScoringMatrix = false;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		String insertQuery = "INSERT INTO ETS.WF_SCORE_MATRIX " +
							 "(PROJECT_ID,WF_ID,SCORE_MATRIX_ID,SCORED_BY,STATUS,LAST_USERID,LAST_TIMESTAMP)"+
							 " VALUES "+
							 "(?,?,?,?,?,?,?)";


		logger.debug("The value new scorematrix id is*********"+scorecard.getMatrixID());
		int rowsAffected = 0;

		try
			{
			con = dbAccess.getConnection();
		    stmt = con.prepareStatement(insertQuery);
			logger.debug(insertQuery);
		    stmt.setString(1,scorecard.getProjectID());
			stmt.setString(2,scorecard.getWorkflowId());
			stmt.setString(3,scorecard.getMatrixID());
			stmt.setString(4,scorecard.getScoredBy());
			stmt.setString(5,String.valueOf(scorecard.getScorecardStatus()));
			stmt.setString(6,scorecard.getLast_userId());
			stmt.setTimestamp(7,new java.sql.Timestamp(System.currentTimeMillis()));


			    rowsAffected = stmt.executeUpdate();
			    if(rowsAffected>0)
			    	addingScoringMatrix = true;
			    dbAccess.doCommit();
				dbAccess.close();
				dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
		logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getClientAttendees() method is completed ");

		if(addingScoringMatrix)
			return scorecard;
		else return null;

	}
	public ArrayList getScoringMatrices(Scorecard scorecard)
	{
		ArrayList scoringMatricesList = new ArrayList();

		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;

		DBAccess dbAccess = new DBAccess();
		String projectId = scorecard.getProjectID();
		String workflowId = scorecard.getWorkflowId();

		String selectQuery = "select score_matrix_id from ETS.WF_SCORe_matrix  "+
							 "where project_id ='"+projectId+"' "+" and  wf_id='"+workflowId+"'";
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);

		    while(resultSet.next())
		    {
		    	scoringMatricesList.add(resultSet.getString("score_matrix_id"));
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getScoringMatrices() method is completed ");


		return scoringMatricesList;

	}
	public boolean addScorecardAttendee(String project_id, String wf_id, String user_id)
	{
		boolean addingScoringMatrix = false;
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		
		String insertQuery = "insert into ets.WF_SETMET_ATTENDEES_CLIENT (project_id, wf_id, userid) values(" +
							 "'"+project_id+"','"+wf_id+"','"+user_id+"')";
		logger.debug(insertQuery);
		int rowsAffected = 0;

		try
		{
		  con = dbAccess.getConnection();
		  stmt = con.createStatement();
		  boolean attendeeExists = false;
		  rowsAffected = stmt.executeUpdate(insertQuery);
		  if(rowsAffected > 0) addingScoringMatrix = true;

		  dbAccess.doCommit();
		  dbAccess.close();
		  dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
		logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("addScorecardAttendee() method is completed ");


		return addingScoringMatrix;

	}
	public String getClientAttendeeID(String clientAttendeeName)
	{
		String user_id = null;

		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;

		DBAccess dbAccess = new DBAccess();

		String selectQuery = "select client_id from ETS.WF_client  "+
							 "where CONCAT(concat(fname,' '), lname) ='"+clientAttendeeName+"'";
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);

		    while(resultSet.next())
		    {
		    	user_id = resultSet.getString("client_id");
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getScoringMatrices() method is completed ");


		return user_id;

	}
	public String getClientAttendeeName(String clientAttendeeID)
	{
		String userName = null;

		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;

		DBAccess dbAccess = new DBAccess();

		String selectQuery = "select CONCAT(concat(fname,' '), lname) name from ETS.WF_client  "+
							 "where  client_id='"+clientAttendeeID+"'";
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);

		    while(resultSet.next())
		    {
		    	userName = resultSet.getString("name");
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getScoringMatrices() method is completed ");


		return userName;

	}
	public ArrayList getAllScoringMatrices(String project_id, String wf_id)
	{   logger.debug("********************************************************CCCCCCCCCCCCCCCCCCCCCCCCc") ;

		ArrayList scoringMatrices = new ArrayList();
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;

		DBAccess dbAccess = new DBAccess();

		String selectQuery = "select distinct(scored_by), score_matrix_id , status  from ets.wf_score_matrix where project_id='"+project_id+"' and wf_id='"+wf_id+"'";

	logger.debug("the query is is $$$$$$$$$$$$$$$$$$$$$$$"+selectQuery);
	
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);
		    logger.debug("********************************************************") ;
		    while(resultSet.next())
		    {
		    	ScoringMatrix scoringMatrix = new ScoringMatrix();

		    	scoringMatrix.setName(getClientAttendeeName(resultSet.getString("scored_by")));
		    	scoringMatrix.setScoreMatrixID(resultSet.getString("score_matrix_id"));
		    	scoringMatrix.setStatus(resultSet.getString("status"));


		    	logger.debug("scorematrix ID ===> "+scoringMatrix.getScoreMatrixID() );
		    	logger.debug("status         ===> "+scoringMatrix.getStatus() );
		    	logger.debug("name           ===> "+scoringMatrix.getName() );
		    	logger.debug("********************************************************");
		    	scoringMatrices.add(scoringMatrix);
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

	logger.debug("getAllScoringMatrices() method is completed ");


		return scoringMatrices;

	}
	public ArrayList getWorkflowDefinitions(String project_id)
	{

		ArrayList workflowDefinitionsList = new ArrayList();
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;

		DBAccess dbAccess = new DBAccess();
		
		String selectQuery = "select a.wf_id wf_id, a.creation_date creationDate, b.schedule_date  meeting_date, a.quarter quarter1, a.year year1 from ets.wf_def a, ets.ets_calendar b "+
							 " where a.project_id='"+project_id+"' and a.meeting_id=b.calendar_id and "+
							 "  a.project_id=b.project_id and WF_CURR_STAGE_NAME='Complete' order by creation_date desc with ur";

		/*
		 * 
		 String selectQuery = "select wf_id, meeting_date from ets.wf_def a, ets.ets_calendar where project_id='"+
							 project_id+"' and WF_CURR_STAGE_NAME='Complete' order by creation_date desc";

		/*
		String selectQuery = "select wf_id from ets.wf_def where project_id='"+
		 					  project_id+"' order by creation_date desc";
		*/
		logger.debug(selectQuery);
		try
		{
			int i = 0;
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);
		    logger.debug("********************************************************") ;
		    while(resultSet.next())
		    {
		    	WorkflowDefinition workflowDef = new WorkflowDefinition();
		    	workflowDef.setWorkflowID(resultSet.getString("wf_id"));
		    	Timestamp timestamp = resultSet.getTimestamp("meeting_date");
		    	long longDate = timestamp.getTime();
		    	Format formatter = new SimpleDateFormat("MM/dd/yyyy");
		    	String meetingDate = formatter.format(new Date(longDate));
		    	workflowDef.setMeetingDate(meetingDate);
		    	workflowDef.setQuarter(new Integer(resultSet.getInt("quarter1")).toString());
		    	workflowDef.setYear(resultSet.getString("year1"));
		    	workflowDef.setCreationDate(resultSet.getDate("creationDate"));
		    	workflowDefinitionsList.add(workflowDef);
		    	i++;
		    	if(i > 3)break;
		    	logger.debug("********************************************************");

		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getWorkflowDefinitions() method is completed ");


		return workflowDefinitionsList;

	}
	public ArrayList getWorkflowDefinitions(String project_id, String wf_id)
	{

		ArrayList workflowDefinitionsList = new ArrayList();
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		
		int currentYear = getYear();
		int currentQuarter = getQuarter(); 
		String selectQuery = "select a.wf_id wf_id, a.creation_date creation_date, b.schedule_date meeting_date,  "+
							 "a.quarter quarter1, a.year year1 from ets.wf_def a, ets.ets_calendar b   "+
							 "where a.project_id='"+project_id+"' and lower(a.wf_type)='setmet' and a.meeting_id=b.calendar_id and  "+
							 " (( (select quarter from ets.wf_def where wf_id='"+wf_id+"')-a.quarter+(("+
							 "cast((select year from ets.wf_def where wf_id='"+wf_id+"') as int)-cast("+
							 "a.year as int))*4) BETWEEN 0 and 4 ) and   a.creation_date <= (select creation_date "+
							 "from ets.wf_def where  lower(wf_type)='setmet' and  project_id='"+project_id+"' and wf_id='"+wf_id+
							 "')) and a.project_id=b.project_id and  a.wf_curr_stage_name='Complete' "+
							 "order by a.creation_date desc with ur";
		
		
		/*
		String selectQuery = "select a.wf_id wf_id, a.creation_date creation_date, b.schedule_date meeting_date, "+
							 " a.quarter quarter1, a.year year1 from ets.wf_def a, ets.ets_calendar b  "+ 
							 " where a.project_id='"+project_id+"' and a.meeting_id=b.calendar_id and  "+ 
            				 "( ((select quarter from ets.wf_def where wf_id='"+wf_id+"')"+"-a.quarter+("+"(cast(select year from ets.wf_def where wf_id='"+wf_id+"') as int)"+"-cast(a.year as int))*4) < 4  and  "+ 
							 " a.creation_date <= (select creation_date from ets.wf_def where project_id='"+project_id +
							 "' and wf_id='"+wf_id+"')) and a.project_id=b.project_id and "+
							 " a.wf_curr_stage_name='Complete' order by a.creation_date desc";
		
		/*
		String selectQuery = "select a.wf_id wf_id, a.creation_date creationDate, b.schedule_date  meeting_date, a.quarter quarter1, a.year year1 from ets.wf_def a, ets.ets_calendar b "+
							 " where a.project_id='"+project_id+"' and a.meeting_id=b.calendar_id and "+
							 "  a.creation_date <=(select creation_date from ets.wf_def where project_id='"+project_id+"' and wf_id='"+wf_id+"') and "+
							 "  a.project_id=b.project_id and WF_CURR_STAGE_NAME='Complete' order by creation_date desc";

		
		 * 
		 String selectQuery = "select wf_id, meeting_date from ets.wf_def a, ets.ets_calendar where project_id='"+
							 project_id+"' and WF_CURR_STAGE_NAME='Complete' order by creation_date desc";

		/*
		String selectQuery = "select wf_id from ets.wf_def where project_id='"+
		 					  project_id+"' order by creation_date desc";
		*/
		logger.debug(selectQuery);
		try
		{
			int i = 0;
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);
		    logger.debug("********************************************************") ;
		    while(resultSet.next())
		    {
		    	WorkflowDefinition workflowDef = new WorkflowDefinition();
		    	workflowDef.setWorkflowID(resultSet.getString("wf_id"));
		    	Timestamp timestamp = resultSet.getTimestamp("meeting_date");
		    	long longDate = timestamp.getTime();
		    	Format formatter = new SimpleDateFormat("MM/dd/yyyy");
		    	String meetingDate = formatter.format(new Date(longDate));
		    	workflowDef.setMeetingDate(meetingDate);
		    	workflowDef.setQuarter(new Integer(resultSet.getInt("quarter1")).toString());
		    	workflowDef.setYear(resultSet.getString("year1"));
		    	workflowDef.setCreationDate(resultSet.getDate("creation_date"));
		    	workflowDefinitionsList.add(workflowDef);
		    	i++;
		    	if(i > 3)break;
		    	logger.debug("********************************************************");

		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getWorkflowDefinitions() method is completed ");


		return workflowDefinitionsList;

	}
	public String addScore(Scorecard scorecard) throws SQLException
	{
		String workflowName = null;
		Connection con = null;
		PreparedStatement stmt = null;
		DBAccess dbAccess = new DBAccess();
		String status = null;
		ArrayList questions = scorecard.getQuestions();

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement
				("insert into ets.wf_score (project_id, wf_id, score_matrix_id, question_id, rating, last_userid , last_timestamp)" +
		    							" values (?,?,?,?,?,?,?)");
		    for(int i = 0; i < questions.size(); i++)
		    {
		    	Question question = (Question)questions.get(i);

		    	stmt.setString(1, scorecard.getProjectID());
		    	stmt.setString(2, scorecard.getWorkflowId());
		    	stmt.setString(3, scorecard.getMatrixID());
		    	stmt.setString(4, question.getQuestion_id());
		    	stmt.setInt(5, Integer.parseInt(question.getScore()));
		    	stmt.setString(6,scorecard.getScoredBy());
		    	stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

		    	int rowsAffected = stmt.executeUpdate();
		    	if(i > 0)
		    		status = "success";
		    	else
		    		status="failure";

		    }



		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("addWFScore() method is completed ");

		return status;

	}

	public int getYear()
	{
		int year = 0;

		Calendar cal = new GregorianCalendar();

		year = cal.get(Calendar.YEAR);

		return year;
	}
	public int getQuarter()
	{
		int quarter = 0;

		Calendar cal = new GregorianCalendar();

		int month = cal.get(Calendar.MONTH);

		logger.debug("month==>"+month);

		switch (month) {
        case 0:  quarter=1; break;
        case 1:  quarter=1; break;
        case 2:  quarter=1; break;
        case 3:  quarter=2; break;
        case 4:  quarter=2; break;
        case 5:  quarter=2; break;
        case 6:  quarter=3; break;
        case 7:  quarter=3; break;
        case 8:  quarter=3; break;
        case 9:  quarter=4; break;
        case 10: quarter=4; break;
        case 11: quarter=4; break;
        default: quarter=0;break;
    }

		return quarter;
	}
	public boolean isAnswered(Question question)
	{
		boolean isAnswered = true;
		int currentYear = new Timestamp(System.currentTimeMillis()).getYear();
		int currentQuarter = getQuarter();
		int questionQuarter = question.getLast_scored_quarter();
		int questionYear = question.getLast_scored_year();

		int diffYears = currentYear - (question.getLast_scored_year());

		if((currentQuarter - questionQuarter + (diffYears * 4)) <= 3 )
		{
			isAnswered = true;
		}
		else
			isAnswered = false;
		/*
		q = cQ - pQ + n(4)
		if((currentQuarter > 0) && (currentYear > question.getLast_scored_year()) )
			{
			  int diffYears = currentYear - (question.getLast_scored_year()+1);
			  if(diffYears > 1)
			  {
			  	isAnswered = false;
			  }
			  else if(diffYears == 0)
			  {
			  	int diffQuestionQuarter = 4 - questionQuarter;
			  	diffQuestionQuarter = currentQuarter + diffQuestionQuarter;
				  	if(diffQuestionQuarter > 4)
				  	{
				  		isAnswered = false;
				  	}
				  	else
				  	{
				  		isAnswered = true;
				  	}
			  	}
			}

		*/
		return isAnswered;
	}

	public boolean isClientAttendeeExists(String user_id, String project_id, String wf_id){
			boolean attendeeStatus = false;
			DBAccess db  = null;
			Connection con = null;
			Statement stmt = null;
			ResultSet resultSet = null;
			String selectQuery = "SELECT userid from ets.wf_setmet_attendees_client where userid='"+user_id+"' and project_id='"+project_id+"' and wf_id='"+wf_id+"'";

			try{
				db  = new DBAccess();
				con = db.getConnection();
				stmt = con.createStatement();
				resultSet = stmt.executeQuery(selectQuery);

				while(resultSet.next())
				{
					String tempUserid = resultSet.getString("userid").trim();
					if(tempUserid.equals(user_id))
						{
						attendeeStatus = true;
						break;
						}

				}
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
			return attendeeStatus;
	}
	public boolean isQuestionExists(String question_desc, String company){
		boolean questionStatus = false;
		DBAccess db  = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;

		try{
			db  = new DBAccess();
			con = db.getConnection();
			stmt = con.createStatement();
			resultSet = stmt.executeQuery("SELECT QUES_DESC from ets.wf_score_question_template where company='"+company+"'");
			while(resultSet.next())
			{
				String tempQues_desc = resultSet.getString("QUES_DESC");
				if(tempQues_desc.equals(question_desc))
					{
					questionStatus = true;
					break;
					}

			}
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
		return questionStatus;
}
	public boolean getCurrentStage(String projectID,String workflowID){
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

			if("prepare".equalsIgnoreCase(stageName)){

				db.prepareDirectQuery("UPDATE ETS.WF_DEF SET WF_CURR_STAGE_NAME='Document' WHERE PROJECT_ID='"+projectID+"' AND WF_ID='"+workflowID+"'");
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

	public static String getWorkflowCurrentStage(String projectID,String workflowID){
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
		return stageName;

	}
	public ArrayList getQuestionsList(Scorecard scorecard, String company, ArrayList workflowIds)
	{
		ArrayList questionList = new ArrayList();
		ArrayList questionsList = getQuestions(company);

		//ArrayList scoringMatricesList = scorecardDAO.getAllScoringMatrices(scorecard.getProjectID(), scorecard.getWorkflowId());
		Iterator questionIdsIterator = questionsList.iterator();

		logger.debug("questionIDList====>"+questionsList);

		ArrayList questionRatingList = new ArrayList();


		while(questionIdsIterator.hasNext())
		{
			Question question = (Question)questionIdsIterator.next();
			String question_id = question.getQuestion_id();


			for(int i=0; i < workflowIds.size();i++)
			{
				String workflowId = (String)workflowIds.get(i);
				float questionAvgRating = getClientQuestionAverage(scorecard.getProjectID(), workflowId, question_id);
				logger.debug("question_id===>"+question_id);
				logger.debug("questionAvgRating===>"+questionAvgRating);
				if(i==0)
					question.setFirstQuarterScore(((new Float(questionAvgRating).intValue()==0)?"-":(new Float(questionAvgRating).toString())));
				if(i==1)
					question.setSecondQuarterScore(((new Float(questionAvgRating).intValue()==0)?"-":(new Float(questionAvgRating).toString())));
				if(i==2)
					question.setThirdQuarterScore(((new Float(questionAvgRating).intValue()==0)?"-":(new Float(questionAvgRating).toString())));
				if(i==3)
					question.setFourthQuarterScore(((new Float(questionAvgRating).intValue()==0)?"-":(new Float(questionAvgRating).toString())));

			}
				questionRatingList.add(question);
		}

		/*try {
				questionList = scorecardDAO.getBaseQuestions(new Scorecard());
				questionList.addAll(scorecardDAO.getLocalQuestions(company));

			} catch (SQLException e) {

				e.printStackTrace();
			}

			for(int i = 0; i < questionList.size(); i++)
			{
				Question question = (Question)questionList.get(i);
				logger.debug("question_id====>"+question.getQuestion_id());
				question = getQuestionAverageRating(question, questionRatingList);
				logger.debug("question.getScore()===>"+question.getScore());

			}
		*/
		return questionRatingList;
	}
	public ArrayList getAverageQuestionsList(Scorecard scorecard, String company)
	{
		ArrayList questionList = new ArrayList();
		ScorecardDAO scorecardDAO = new ScorecardDAO();
		ArrayList questionsList = scorecardDAO.getQuestions(company);

		//ArrayList scoringMatricesList = scorecardDAO.getAllScoringMatrices(scorecard.getProjectID(), scorecard.getWorkflowId());
		Iterator questionIdsIterator = questionsList.iterator();

		logger.debug("questionIDList====>"+questionsList);

		ArrayList questionRatingList = new ArrayList();


		while(questionIdsIterator.hasNext())
		{
			Question question = (Question)questionIdsIterator.next();
			String question_id = question.getQuestion_id();


				
				float questionAvgRating = scorecardDAO.getClientQuestionAverage(scorecard.getProjectID(), scorecard.getWorkflowId(), question_id);
				logger.debug("question_id===>"+question_id);
				logger.debug("questionAvgRating===>"+questionAvgRating);
				question.setScore(new Float(questionAvgRating).toString());
			
				questionRatingList.add(question);
		}

		/*try {
				questionList = scorecardDAO.getBaseQuestions(new Scorecard());
				questionList.addAll(scorecardDAO.getLocalQuestions(company));

			} catch (SQLException e) {

				e.printStackTrace();
			}

			for(int i = 0; i < questionList.size(); i++)
			{
				Question question = (Question)questionList.get(i);
				logger.debug("question_id====>"+question.getQuestion_id());
				question = getQuestionAverageRating(question, questionRatingList);
				logger.debug("question.getScore()===>"+question.getScore());

			}
		*/
		return questionRatingList;
	}
	
	public float getOverallClientAverage(String project_id, String workflow_id, String company)
	{
		float overallAverage = 0.00f;
		int i = 0;
		
		Scorecard scorecard = new Scorecard();
		scorecard.setProjectID(project_id);
		scorecard.setWorkflowId(workflow_id);
		ArrayList  questionRatingList = getAverageQuestionsList(scorecard, company);
		Iterator questionRatingsIterator = questionRatingList.iterator();
		while(questionRatingsIterator.hasNext())
		{
			Question question = (Question)questionRatingsIterator.next();
			
			if(Float.parseFloat(question.getScore()) != 0.00f)
			{
				overallAverage += Float.parseFloat(question.getScore());
				i++;
			}
		}
		
		if(i != 0)
	    	overallAverage = overallAverage/i;
		
		overallAverage = getPreciseFloat(overallAverage);
		
		return overallAverage;
		
	}
	public float getRespondentScore(String project_id, String wf_id, String scored_by, String question_id)
	{
		float respondentScore = 0.00f;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		//logger.debug("project_id====>"+project_id);
		//logger.debug("wf_id===>"+wf_id);
		//logger.debug("question_id===>"+question_id);
		String selectQuery = "select b.rating  from ets.wf_score_matrix a, ets.wf_score b where b.project_id='"+
							 project_id+"' and b.wf_id='"+wf_id+"' and "+
							 "b.question_id='"+question_id+"' and a.scored_by='"+scored_by+"' and a.project_id=b.project_id "+
							 " and b.wf_id=a.wf_id and a.score_matrix_id=b.score_matrix_id";
		
		logger.debug(selectQuery);
		
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement(selectQuery);
		    int i = 0;
		    resultSet = stmt.executeQuery();
		    
		    while(resultSet.next())
		    {
		    	respondentScore = resultSet.getInt("rating");
		    }
		   
		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getRespondentScore cacthblock",ex);
			logger.debug("the execption is getRespondentScore is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getRespondentScore finally block",e);
				}
			}
		}

		logger.debug("getRespondentScore() method is completed ");

		return respondentScore;
	}
	public ArrayList getRespondentQuestionsList(String project_id, String company, ArrayList workflowIds, String scored_by)
	{
		ArrayList questionList = new ArrayList();
		ArrayList questionsList = getQuestions(company);

		//ArrayList scoringMatricesList = scorecardDAO.getAllScoringMatrices(scorecard.getProjectID(), scorecard.getWorkflowId());
		Iterator questionIdsIterator = questionsList.iterator();

		logger.debug("questionIDList====>"+questionsList);

		ArrayList questionRatingList = new ArrayList();


		while(questionIdsIterator.hasNext())
		{
			Question question = (Question)questionIdsIterator.next();
			String question_id = question.getQuestion_id();
			

			for(int i=0; i < workflowIds.size();i++)
			{
				String workflowId = (String)workflowIds.get(i);
				float questionAvgRating = getRespondentScore(project_id, workflowId, scored_by, question_id);
				logger.debug("question_id===>"+question_id);
				logger.debug("questionAvgRating===>"+questionAvgRating);
				if(i==0)
					question.setFirstQuarterScore(((new Float(questionAvgRating).intValue()==0)?"-":(new Float(questionAvgRating).toString())));
				if(i==1)
					question.setSecondQuarterScore(((new Float(questionAvgRating).intValue()==0)?"-":(new Float(questionAvgRating).toString())));
				if(i==2)
					question.setThirdQuarterScore(((new Float(questionAvgRating).intValue()==0)?"-":(new Float(questionAvgRating).toString())));
				if(i==3)
					question.setFourthQuarterScore(((new Float(questionAvgRating).intValue()==0)?"-":(new Float(questionAvgRating).toString())));
			}
				questionRatingList.add(question);
		}

		/*try {
				questionList = scorecardDAO.getBaseQuestions(new Scorecard());
				questionList.addAll(scorecardDAO.getLocalQuestions(company));

			} catch (SQLException e) {

				e.printStackTrace();
			}

			for(int i = 0; i < questionList.size(); i++)
			{
				Question question = (Question)questionList.get(i);
				logger.debug("question_id====>"+question.getQuestion_id());
				question = getQuestionAverageRating(question, questionRatingList);
				logger.debug("question.getScore()===>"+question.getScore());

			}
		*/
		return questionRatingList;
	}
	
	public String getScoredBy(String score_matrix_id)
	{
		String scoredBy = "";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();


		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement
							(" select CONCAT(concat(fname,' '), lname) name  from ets.WF_client where client_id= "+
						     "(select scored_by  from ets.wf_score_matrix where score_matrix_id='"+score_matrix_id+
							 "') " );

		    resultSet = stmt.executeQuery();

		    while(resultSet.next())
		    {
		    	scoredBy = resultSet.getString("name");
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getScoredBy() method is completed ");


		return scoredBy;
	}
	public boolean completeScorecardAverage(String project_id, String workflow_id, String company, String loggedUser)
	{
		boolean isCompleted = false;
		try
		{
		Scorecard scorecard = new Scorecard();

		scorecard.setProjectID(project_id);
		scorecard.setWorkflowId(workflow_id);
		
		ArrayList questionList = getAverageQuestionsList(scorecard, company);

		//		Load the scorecard with required values
		scorecard.setQuestions(questionList);
		scorecard.setMatrixID(ETSCalendar.getNewCalendarId());
		scorecard.setScoredBy("CLIENT_AVG");
		scorecard.setScorecardStatus('C');
		scorecard.setLast_userId(loggedUser);
		
		if(null != addScoringMatrix(scorecard))
		{
		scorecard.setQuestions(questionList);
		String status = addScore(scorecard);
		if(status.equals("success"))
			{
			isCompleted = true;
			}
		}
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return isCompleted;
	}
	public float getPreciseFloat(float tempFloat)
	{

		java.math.BigDecimal bigDecimal = new java.math.BigDecimal(tempFloat);

		bigDecimal = bigDecimal.multiply(new java.math.BigDecimal(100.0));
		tempFloat = bigDecimal.floatValue();
		int i = Math.round(tempFloat);

		bigDecimal = new java.math.BigDecimal(i);

		return bigDecimal.floatValue()/100;
	}
	public String getScoredBy(String project_id, String workflow_id, String score_matrix_id)
	{
		String scoredBy = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		String selectQuery="select scored_by  from ets.wf_score_matrix where project_id='"+project_id+
						   "'  and wf_id='"+workflow_id+
						   "'  and score_matrix_id='"+score_matrix_id+"' " ;
		
		logger.debug(selectQuery);

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement(selectQuery);
		    resultSet = stmt.executeQuery();

		    while(resultSet.next())
		    {
		    	scoredBy = resultSet.getString("scored_by");
		    }
		    logger.debug(scoredBy);
		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getScoredBy() method is completed ");


		return scoredBy;
	}
	public int getNumberOfClientAttendees(String project_id, String wf_id)
	{
		int noOfClientAttendees = 0;
		
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		String selectQuery="select count(*) count from ets.WF_SETMET_ATTENDEES_CLIENT where project_id='"+project_id+
						   "' and wf_id='"+wf_id+"'" ;
		
		logger.debug(selectQuery);

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.prepareStatement(selectQuery);
		    resultSet = stmt.executeQuery();

		    while(resultSet.next())
		    {
		    	noOfClientAttendees = resultSet.getInt("count");
		    }
		    logger.debug(new Integer(noOfClientAttendees).toString());
		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getWorkflowObject cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getWorkflowObject finally block",e);
				}
			}
		}

		logger.debug("getNumberOfClientAttendees() method is completed ");


		return noOfClientAttendees;
		
	}
	public int getScoringMatrixRating(String project_id, String workflow_id, String scored_by)
	{
		int rating = 0;

		return rating;
	}
	public ArrayList getClientRespondents(String companyName)
	{
		ArrayList clientAttendeesList = new ArrayList();
		int questionID = 0;

		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		String selectQuery = "select client_id, CONCAT(concat(fname,' '), lname) name from ets.WF_client where company='"+companyName+"'";
		ArrayList numbersList = new ArrayList();
		int maxNumber = 0;
		logger.debug("selectQuery===>"+selectQuery);
		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);

		    while(resultSet.next())
		    {
		    	ClientAttendee clientAttendee = new ClientAttendee();
		    	clientAttendee.setClientId(resultSet.getString("client_id"));
		    	clientAttendee.setName(resultSet.getString("name"));
		    	clientAttendeesList.add(clientAttendee);
		    	
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getClientRespondents cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getClientRespondents finally block",e);
				}
			}
		}

		logger.debug("getClientRespondents() method is completed ");


		return clientAttendeesList;

	}
	public ArrayList getClientRespondents(Scorecard scorecard)
	{
		ArrayList projectRespondentsList = new ArrayList();
		int questionID = 0;

		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		String projectId = scorecard.getProjectID();
		String workflowId = scorecard.getWorkflowId();

		String selectQuery = "select CONCAT(concat(a.fname,' '), a.lname) name, a.client_id client_id  from ets.WF_client a, ets.WF_Score_matrix b  "+
							 "where b.project_id ='"+projectId+"' "+" and  b.wf_id='"+workflowId+"' and b.scored_by=a.client_id";
		ArrayList numbersList = new ArrayList();
		int maxNumber = 0;

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);

		    while(resultSet.next())
		    {
		    	ClientAttendee clientAttendee = new ClientAttendee();
		    	clientAttendee.setClientId(resultSet.getString("client_id"));
		    	clientAttendee.setName(resultSet.getString("name"));
		    	projectRespondentsList.add(clientAttendee);
		    }

		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getClientRespondents cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getClientRespondents finally block",e);
				}
			}
		}

		logger.debug("getClientRespondents() method is completed ");


		return projectRespondentsList;

	}
	// Method for updating the overall score in wf_stage_document_setmet
	public boolean updateOverallScore(String project_id, String wf_id, String wf_stage_id, float overallScore)
	{
		boolean status = false;
		int questionID = 0;

		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		

		String updateQuery = "update ets.WF_STAGE_DOCUMENT_SETMET set overall_score="+overallScore+
							 "where project_id='"+project_id+"'"+ " and wf_id='"+wf_id+"' and wf_stage_id='"+
							 wf_stage_id+"'";
		
		logger.debug(updateQuery);
		
		int rowsAffected = 0;

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    rowsAffected = stmt.executeUpdate(updateQuery);
		    
		    if(rowsAffected > 0 )
		    {
		    	status = true;
		    }
		    dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in updateOverallScore cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in updateOverallScore finally block",e);
				}
			}
		}

		logger.debug("updateOverallScore() method is completed ");
		return status;

	}
	
	
	//	 Method for getting the overall score from wf_stage_document_setmet
	public float getOverallScore(String project_id, String wf_id, String wf_stage_id, float overallScore)
	{
		float clientOverallScore = 0.0f;
		
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();
		

		String selectQuery = "select overall_score from  ets.WF_STAGE_DOCUMENT_SETMET "+
							 "where project_id='"+project_id+"'"+ " and wf_id='"+wf_id+"' and wf_stage_id='"+
							 wf_stage_id+"'";
		
		logger.debug(selectQuery);
		
		int rowsAffected = 0;

		try
		{
		    con = dbAccess.getConnection();
		    stmt = con.createStatement();
		    resultSet = stmt.executeQuery(selectQuery);
		    if(resultSet.next())
		    {
		    	clientOverallScore = resultSet.getFloat("overall_score");
		    }
		   
		    //dbAccess.doCommit();
			dbAccess.close();
			dbAccess=null;
		}catch(Exception ex){
			dbAccess.doRollback();
			logger.error("Exception in getOverallScore cacthblock",ex);
			logger.debug("the execption is workflow onbject scorecard is"+ex);
		}finally{
			if(dbAccess!=null){
				try{
					dbAccess.close();
					dbAccess=null;
				}catch(Exception e){
					logger.error("Exception in getOverallScore finally block",e);
				}
			}
		}

		logger.debug("getOverallScore() method is completed ");
		return clientOverallScore;

	}
}

