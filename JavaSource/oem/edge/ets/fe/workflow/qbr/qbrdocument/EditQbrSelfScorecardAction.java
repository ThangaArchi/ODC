
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
 * Created on Feb 7, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.qbr.qbrdocument;

import java.io.IOException;
import oem.edge.ets.fe.workflow.setmet.document.WorkflowDefinition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.setmet.document.ScorecardBL;
import oem.edge.ets.fe.workflow.setmet.document.ScorecardDAO;
import oem.edge.ets.fe.workflow.setmet.document.ScorecardFormBean;
import oem.edge.ets.fe.workflow.util.OrderedMap;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditQbrSelfScorecardAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		String mID = request.getParameter("mid");
        String strForward = "success";		
		
		//logger.debug("mid==>"+mID);

		ArrayList scorecard = new ArrayList();
		ArrayList questionList = new ArrayList();
		
		ScorecardFormBean 			 fBean = (ScorecardFormBean)form;
		ScorecardBL		  			 object=  new ScorecardBL();
		ScorecardDAO           scorecardDAO= new ScorecardDAO();
		HttpSession pdSession = request.getSession(true);
		
		String workflowType = request.getParameter("wf_type");
		
		QbrBL bl = new QbrBL();
		
		QBRDocumentStage document =  new QBRDocumentStage();
		
		String workflowID = request.getParameter("workflowID");
		String scoredBy = scorecardDAO.getScoredBy(mID);
		String byPassScore = (String) request.getParameter("byPass");
		
		
		//logger.debug("ScoredBy===>"+scoredBy);
		
		if("false".equalsIgnoreCase(byPassScore))
	         fBean.setBypass(false);
		else
			fBean.setBypass(true);
		
		fBean.setScorer(scoredBy);

		fBean.setCurrentStage(ScorecardDAO.getWorkflowCurrentStage(fBean.getProj(), workflowID));

		document.setProjectID(fBean.getProj());
		document.setWorkflowID(fBean.getWorkflowID());
	    document.setClientName(company);
		document.setLastUsr(loggedUser);
		document.setWorkflowType(workflowType);
		
		QBRScorecard objScorecard = new QBRScorecard();
		objScorecard.setScoredBy(scoredBy);
		
	
		
		
		
		//logger.debug("the value of the requestor is"+loggedUser);
		//logger.debug("The company name is"+company);
		
		
		boolean canAccess = canAccess(request, "SCORECARD", "EDIT");
		String accessible = new Boolean(canAccess).toString();
		fBean.setAccessible(accessible);
		//fBean.setAccessible("false");
		
		objScorecard.setMatrixID(mID);
		document.setScorecard(objScorecard);
		
	
			try
			{
				
				
				fBean.setWorkflowName(scorecardDAO.getWorkflowName(projectID,workflowID));									
				
				if(fBean.getAction()==null){
					OrderedMap questions = bl.getQuestions(document);
					ArrayList questionObject = questions.values();				
					fBean.setQuestions(questionObject);
				}if("update".equalsIgnoreCase(fBean.getAction())){
					ArrayList questionObject = fBean.getQuestions();
					boolean filled = isClientRatingFilled(questionObject);
					objScorecard.setQuesMap(ArrayList2Map(questionObject));
					document.setScorecard(objScorecard);
					if(fBean.isBypass())
						document.setBypass(true);
					else
						document.setBypass(false);
					
					
					boolean saveFlg=false;
							
						scorecardDAO.updateByPassScore(document);
						saveFlg = bl.saveScores(document);
					
					if(!saveFlg){
						request.setAttribute("UPDATE_ERROR","Scorecard update failed");
					}
					request.setAttribute("CLIENTRATING",new Boolean(filled));
				}if("addNew".equalsIgnoreCase(fBean.getAction())){
					  	  pdSession.setAttribute("FORMBEAN",fBean.getQuestions());					  	  
					  	  return new ActionForward("/addNewQuestionsDisplayAction.wss?wf_type="+workflowType+"&workflowID="+workflowID+"&proj="+fBean.getProj()+"&mid="+mID+"&tc="+fBean.getTc());
				}				  					
				
				OrderedMap map = scorecardDAO.getHistory(document);
		        ArrayList wDefn =scorecardDAO.getQBRWorkflowDefinitions(document);
		        ArrayList localQuestions = scorecardDAO.getLocalQBRQuestions(document); 
		        ArrayList dropQuesIDs = dropLocalQuestions(localQuestions,map);		        
			    
		        
		        fBean.setScoreMatrixStatus(bl.getScoreMatrixStatus(document));

				if(scorecardDAO.getByPassScoreStatus(document).equalsIgnoreCase("N")){
					OrderedMap questions = bl.getQuestions(document);
					
					//code to remove the unanswered questions from the scoring template
					
					for(int x = 0 ;x< dropQuesIDs.size();x++){
						questions.remove( (String)dropQuesIDs.get(x));
					}
					
					sortQuestions(questions);
					ArrayList questionObject = questions.values();
					
					fBean.setQuestions(questionObject);
					fBean.setBypass(false);
				}else{		
					fBean.setBypass(true);
				}

				// fBean.setScoreMatrixStatus("I");
		        
		        OrderedMap names = getMeetingDate(wDefn,workflowType);
		        map.put("NAMES",names);
		        request.setAttribute("HISTORY",map);
		        if("quesAdded".equalsIgnoreCase(fBean.getAction())){
		      	  if(pdSession.getAttribute("FORMBEAN")!=null){
				  	 ArrayList al = (ArrayList)pdSession.getAttribute("FORMBEAN");
				  	 OrderedMap addQ = bl.getQuestions(document);
				  	 addNewQuestionsToSession(al,addQ.values());
				  	 fBean.setQuestions(al);
				  	 pdSession.removeAttribute("FORMBEAN");
		      	  }
		        }
			}
		        
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			if("update".equalsIgnoreCase(fBean.getAction()))
                 return mapping.findForward("confirm");
            else
				 return mapping.findForward("success");
	}

	/**
	 * 
	 * @param questionList
	 * @return arrayList
	 */
	
	private void sortQuestions(OrderedMap questionMap){
		ArrayList questionList = questionMap.keys();
		OrderedMap keys = new OrderedMap();
		if(questionList!=null && questionList.size()>0){
		  for(int x = 0;x< questionList.size();x++){
		  	 Question q1 = (Question) questionMap.get((String)questionList.get(x));
		  	 if(WorkflowConstants.STOPLIGHT_COLOR.equalsIgnoreCase(q1.getQuesDesc())){
		  	 	  keys.put((String)questionList.get(x),q1);
		  	 }	
		  	if(WorkflowConstants.OVERALL_RATING.equalsIgnoreCase(q1.getQuesDesc())){
		  		 keys.put((String)questionList.get(x),q1);
	  	    }	
			
	     }
	  }		
		
		ArrayList qid = keys.keys();
		
		for(int x = 0;x< qid.size();x++){
			 questionMap.remove((String)qid.get(x));			 			 
		}
		
		for(int x =0;x<qid.size();x++){
			 Question q1 = (Question) keys.get((String)qid.get(x));
		     questionMap.put((String)qid.get(x),q1);		 
		}
		
	}
	
	/**
	 * 
	 * @param al
	 * @return
	 */
	private OrderedMap getMeetingDate(ArrayList defn,String workflowType){
		
		
		OrderedMap names = new OrderedMap();
		if(defn!=null && defn.size()>0){
			for(int x= 0;x<defn.size();x++){
			   WorkflowDefinition def = (WorkflowDefinition) defn.get(x);
			   if("QBR".equalsIgnoreCase(workflowType)){
					   names.put(def.getWorkflowID(),"Q"+def.getQuarter()+",\n"+def.getMeetingDate());
			   }else{
			   		   names.put(def.getWorkflowID(),getMonth(def.getQuarter())+",\n"+def.getMeetingDate());
			   }
			}
		}
		
		
		return names;
		
	}
   /**
    * This method forms a Map of question objects from an ArrayList
    * @param al
    * @return
    */
	private OrderedMap ArrayList2Map(ArrayList al){
		OrderedMap qMap = new OrderedMap();
		
		if(al!=null){
			
			System.out.println("the size of arraylist is $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+al.size());
			 for(int x = 0 ;x <al.size();x++){
			 	 Question q = (Question)al.get(x);
			 	 qMap.put(q.getQuesID(),q);
			 }
		}
		return qMap;
	}
	private ArrayList dropLocalQuestions(ArrayList local, OrderedMap map ){
		
		ArrayList list = new ArrayList();
		OrderedMap question =(OrderedMap) map.get("WORKFLOWID");
		OrderedMap ratings = (OrderedMap) map.get("RATINGS");
		ArrayList qID = question.keys();
	    ArrayList wID = ratings.keys();
	    
	    if(wID.size()==4){
	    		for(int ques=0;ques<qID.size(); ques++){
	    			boolean  rating = false; 
	    				if(local.contains((String)qID.get(ques)))	{
	    					System.out.println("The value of the question id is ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+(String)qID.get(ques));
	    						for(int x = (wID.size()-1); x>=0; x-- ){
	    								OrderedMap values = (OrderedMap)ratings.get((String)wID.get(x)); 
	    								String value = (String)values.get((String)qID.get(ques));      
	    								
	    								System.out.println("The value of the question id is ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+value);
	    								
	    								if(value!=null && !"-".equals(value.trim())){
	    									System.out.println("The value of the question id is !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1"+(String)qID.get(ques));
	    									rating = true;
	    									break;
	    								}
	    						}
	    						if(!rating){
	    							list.add((String)qID.get(ques));
	    							question.remove((String)qID.get(ques));
	    						}
	    				}
	    		}
	    		map.put("WORKFLOWID",question);
	    }
	    System.out.println("The  value of list is $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$44"+list);
	    
        return list;	    
	}
	
	private boolean isClientRatingFilled(ArrayList qList){
		  boolean filled=false;
		  
		  if(qList!=null && qList.size()>0){
		  	 for(int x =0;x< qList.size();x++){
		  	 	  Question q1 = (Question)qList.get(x);
		  	 	  if(WorkflowConstants.OVERALL_RATING.equalsIgnoreCase(q1.getQuesDesc())){
		  	 	  	    if(!"0".equalsIgnoreCase(q1.getRating())){
		  	 	  	    	  filled=true;
		  	 	  	    }
		  	 	  }
		  	 	  
		  	 }
		  }
		  
		  return filled;
	}
	
	private ArrayList addNewQuestionsToSession(ArrayList sess,ArrayList db){
		  ArrayList dummy = new ArrayList();
		  OrderedMap tempMap = new OrderedMap();
		  
		  
		   if(db!=null && sess!=null){
		   	       for(int x = 0;x<db.size();x++){
		   	       	    Question outer = (Question)db.get(x);
		   	         
		   	       	    boolean found = false;
		   	       	    for(int j =0 ;j<sess.size(); j++){
		   	       	   	     Question inner = (Question)sess.get(j);
		   	       	   	     
		   	       	   	     if((WorkflowConstants.STOPLIGHT_COLOR.equalsIgnoreCase(inner.getQuesDesc()) || WorkflowConstants.OVERALL_RATING.equalsIgnoreCase(inner.getQuesDesc()))){
		   	       	   	     	if(!tempMap.containsKey(inner.getQuesID())){
		   	       	   	     		  tempMap.put(inner.getQuesID(),inner);
		   	       	   	     	}
		   	       	   	     	
		   	       	   	     }
		   	       	   	     
		   	       	   	     if(outer.getQuesID().equals(inner.getQuesID())){
		   	       	   	     	  found = true;
		   	       	   	     	  break;
		   	       	   	     }
		   	       	   }
		   	       	   if(!found){
		   	       	   	   sess.add(outer);
		   	       	   }
		   	       }
		   }
		   
		   dummy = tempMap.values(); 
		   for(int cnt = 0;cnt <dummy.size();cnt++){
		   	   Question qObj = (Question)dummy.get(cnt);		   	   		   	   
		   	 for(int j =0 ;j<sess.size(); j++){
	       	   	     Question Qobjinner = (Question)sess.get(j);
	       	   	     
	       	   	     if(qObj.getQuesID().equals(Qobjinner.getQuesID())){
	       	   	     	
	       	   	     	System.out.println("the reved value is %%%%%%%%%%%%%%%%%%%%"+Qobjinner.getQuesID());
	       	   	     		sess.remove(j);
	       	   	     }
	       	   }
		   }
		   for(int cnt = 0;cnt <dummy.size();cnt++){
		      sess.add((Question)dummy.get(cnt));
		   }
		   System.out.println("The sess value is ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+sess);
		   
		   return sess;
	}
	
	public String getMonth(String mon)
	{
		String quarter = "";


		int month = Integer.parseInt(mon);


		switch (month) {
        case 1:  quarter="Jan"; break;
        case 2:  quarter="Feb"; break;
        case 3:  quarter="Mar"; break;
        case 4:  quarter="Apr"; break;
        case 5:  quarter="May"; break;
        case 6:  quarter="Jun"; break;
        case 7:  quarter="Jul"; break;
        case 8:  quarter="Aug"; break;
        case 9:  quarter="Sept"; break;
        case 10: quarter="Oct"; break;
        case 11: quarter="Nov"; break;
        default: quarter="Dec";break;
    }

		return quarter;
	}
	
}
