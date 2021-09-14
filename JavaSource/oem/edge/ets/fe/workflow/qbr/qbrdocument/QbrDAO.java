

package oem.edge.ets.fe.workflow.qbr.qbrdocument;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import oem.edge.ets.fe.workflow.core.AbstractDAO;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.core.WorkflowStage;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.setmet.document.Scorecard;
import oem.edge.ets.fe.workflow.setmet.document.ScorecardDAO;
import oem.edge.ets.fe.workflow.util.OrderedMap;

public class QbrDAO extends AbstractDAO{ 

      
    public String getScoreMatrixStatus(WorkflowStage stage){
    	DBAccess db = null;
    	String status = "I";
    	QBRDocumentStage docStage = (QBRDocumentStage)stage;
    	QBRScorecard card = docStage.getScorecard();
    	try{
				
				if(!"QBR".equalsIgnoreCase(stage.getWorkflowType())){
    				if(getScorecardStatus(card))
    					  return "C";
    				else
    					  return "I";
    			}

    		db = new DBAccess();
    		db.prepareDirectQuery("SELECT RATING_PERIOD_TO TODATE FROM ETS.WF_STAGE_IDENTIFY_SETMET WHERE PROJECT_ID='"+stage.getProjectID()+"' AND WF_ID='"+stage.getWorkflowID()+"' WITH UR");
    		int rows = db.execute();
    		if(rows >0){
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    			String str = db.getString(0,"TODATE");
    			System.out.println("the dates are ********************************"+str+"^^^^^^^^^^^^^^^^^^^^^^^^");
    			Date dt = sdf.parse(str);
    			Calendar cal = Calendar.getInstance();
    			cal.setTime(dt);
    			cal.add(Calendar.MONTH,1);
    			Date postDate = new Date(cal.getTimeInMillis());
    			Date today = new Date(System.currentTimeMillis()); 
    			
    			System.out.println("the dates are ********************************"+postDate+"^^^^^^^^^^^^^^^^^^^^^^^^66"+today);
    			
    			if(postDate.compareTo(today)== 0 || postDate.compareTo(today)==-1){
    				System.out.println("the dates are ********************************"+postDate+"^^^^^^^^^^^^^^^^");
					  
					  Scorecard scorecard = new Scorecard();
					  scorecard.setProjectID(docStage.getProjectID());
					  scorecard.setWorkflowId(docStage.getWorkflowID());
					  scorecard.setMatrixID(card.getMatrixID());
					  ScorecardDAO dao = new ScorecardDAO();
					  if(dao.updateScorecard(scorecard))
					   	  status = "C";
					  else 
					  	  status = "I";
				}
    			else
    				System.out.println("the dates are ******************************** else");
    				  if(getScorecardStatus(card))
    				  	  return "C";
    				  else 
    				  	  return "I";
    			
    		}
    		db.doCommit();
    		db.close();
    		db = null;
    	}catch(Exception ex){
    		db.doRollback();
    		System.out.println("the dates are ********************************^^^^^^^^^^^^^^^exception^^^^^^^^^66"+ex);
    	}finally{
    		 try{
    		 	 if(db==null){
    		 	 	db.close();
    		 	 	db = null;
    		 	 }
    		 }catch(Exception ex1){
    		 	
    		 }
    	}
    	
    	return status;
    }
    
    private boolean getScorecardStatus(QBRScorecard card){
    	
    	 DBAccess db = null;
    	 boolean count = false;
    	 
    	 try{
    	 	db = new DBAccess(); 
    	 	db.prepareDirectQuery("SELECT STATUS STAT FROM ETS.WF_SCORE_MATRIX WHERE SCORE_MATRIX_ID='"+card.getMatrixID()+"' WITH UR");
    	 	int rows = db.execute();
    	 	if(rows>0){
    	 		  if("C".equalsIgnoreCase(db.getString(0,"STAT"))){
    	 		  	 count = true;
    	 		  }else{
    	 		  	count = false;
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
    	 	 		db=null;
    	 	 	}
    	 	 }catch(Exception finale){
    	 	 	
    	 	 }
    	 }
    	 
    	 
    	 return count;
    	
    }
    
    public  boolean saveWorkflowObject(WorkflowObject workflowObject){
    	OrderedMap questions  = new OrderedMap();
    	Question qObj = null;
    	QBRDocumentStage stage = (QBRDocumentStage)workflowObject;
    	QBRScorecard card = stage.getScorecard();
    	OrderedMap quesMap = card.getQuesMap();
    	ArrayList qList = quesMap.values();
    	boolean saveFlg = false;
    	if(qList == null)
    		qList = new ArrayList();
    	
    	DBAccess db = null;
    	
    	try{
    		db = new DBAccess();
    		//db.prepareQuery("wf_score_qbr_update");
    		
    		for(int x = 0; x < qList.size();x++){
    			qObj = (Question)qList.get(x);
    			db.prepareDirectQuery("UPDATE ETS.WF_SCORE SET RATING_COMMENTS='"+qObj.getComment()+"' , RATING="+Integer.parseInt(qObj.getRating())+" WHERE QUESTION_ID='"+qObj.getQuesID()+"' AND SCORE_MATRIX_ID='"+card.getMatrixID()+"'");
    			//db.setString(1,qObj.getComment());
    			//db.setString(2,qObj.getRating());
    			db.execute();
    			db.clearListData();
    		}
    		
    		db.doCommit();
    		db.close();
    		db = null;
    		saveFlg = true;
    	}catch(Exception ex){
    	    db.doRollback();
    	    saveFlg= false;
    	}finally{
    		try{
    		     if(db!=null){
    		     	db.close();
    		     	db = null;
    		     }
    		}catch(Exception finall){
    			
    		}
    	}    	    	
    	return saveFlg;
    	
    	
    }

    
    public  WorkflowObject getWorkflowObject(String ID){
		return null;
	}
	
    
    public  boolean saveWorkflowObjectList(ArrayList object){
		 return true;
	}   
	
    
    public  ArrayList getWorkflowObjectList(String ID){
	  
		 return null;
		
	}
   
    public OrderedMap getTemplateQuestions(WorkflowStage obj){
    	OrderedMap questions  = new OrderedMap();
    	Question qObj = null;
    	QBRDocumentStage stage = (QBRDocumentStage)obj;
    	QBRScorecard card = stage.getScorecard();
    	DBAccess db = null;
    	
    	String status = getScoreMatrixStatus(obj);
    	try{
    		db = new DBAccess();
    		//db.prepareQuery("get_self_qbr_template");

    		//db.setString(1,stage.getWorkflowID());
    		//db.setString(2,stage.g());
    		//db.setString(3,stage.getWorkflowType());
    		db.prepareDirectQuery("select a.question_id QID,a.ques_desc QDESC,b.rating RTNG,b.rating_comments COMMENTS from ets.wf_score b,ets.wf_score_question_template a where b.score_matrix_id='"+card.getMatrixID()+"' and a.question_id = b.question_id order by a.question_type,a.question_id, a.ques_desc WITH UR");
    		
    	    int rows = db.execute();
    		for(int x = 0; x < rows ;x++){
    			qObj = new Question();
    			qObj.setQuesID(db.getString(x,"QID"));
    			qObj.setQuesDesc(db.getString(x,"QDESC"));
    			if(WorkflowConstants.STOPLIGHT_COLOR.equalsIgnoreCase(db.getString(x,"QDESC")) && "C".equalsIgnoreCase(status)){
    				qObj.setRating(getColor(db.getString(x,"RTNG")));				
    			}else{
    				qObj.setRating(db.getString(x,"RTNG"));
    			}
    			qObj.setQType(db.getString(x,"QTYPE"));
    			qObj.setComment(db.getString(x,"COMMENTS"));
    			questions.put(new String(db.getString(x,"QID")),qObj);
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
    		}catch(Exception finall){
    			
    		}
    	}    	    	
    	return questions;
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
}
