/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.setmet.summary.setmet;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.setmet.document.Question;
import oem.edge.ets.fe.workflow.setmet.document.Scorecard;
import oem.edge.ets.fe.workflow.setmet.document.ScorecardDAO;
import oem.edge.ets.fe.workflow.setmet.document.WorkflowDefinition;

import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : ScorecardDetails
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.setmet
 * Description : 
 * Date		   : Nov 20, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class ScorecardDetails {
	private static Log logger = WorkflowLogger.getLogger(ScorecardDetails.class);
	private ArrayList qtrQYY = new ArrayList();
	private ArrayList rows = new ArrayList(); //contains ScorecardRow objects
	
	
	
	/**
	 * @param db
	 * @param company
	 */
	public ScorecardDetails(DBAccess db, String projectID, String workflowID, String company) {
		
		ScorecardDAO scorecardDAO = new ScorecardDAO();
		ArrayList workflowDefs = scorecardDAO.getWorkflowDefinitions(projectID, workflowID);
         for(int i = 0; i < workflowDefs.size(); i++)
		  {
         	WorkflowDefinition workflowDef = (WorkflowDefinition)workflowDefs.get(i);
		   	qtrQYY.add(workflowDef.getQuarter()+"Q"+workflowDef.getYear());
		  }
		  
		  ArrayList workflowIds = new ArrayList();
		  
		  for(int i = 0; i <workflowDefs.size(); i++)
		  {
		   WorkflowDefinition workflowDef = (WorkflowDefinition)workflowDefs.get(i);
		   workflowIds.add(workflowDef.getWorkflowID());
		  }
		  Scorecard scorecard = new Scorecard();
		  scorecard.setMatrixID("");
		  scorecard.setProjectID(projectID);
		  scorecard.setWorkflowId(workflowID);
		  ArrayList questionList = new ArrayList();

		  questionList = scorecardDAO.getQuestionsList(scorecard, company, workflowIds);
		  
		  for(int i =0; i< questionList.size(); i++)
		  {
		  	ScorecardRow row = null;
		  	row = new ScorecardRow();
			row.setQuestion(((Question)questionList.get(i)).getQues_desc());
			
			row.setScores(new String[]{((Question)questionList.get(i)).getFirstQuarterScore()
					,((Question)questionList.get(i)).getSecondQuarterScore()
					,((Question)questionList.get(i)).getThirdQuarterScore()
					,((Question)questionList.get(i)).getFourthQuarterScore()
					});
			
			rows.add(row);
		  }
		  
		  
		//
		/*qtrQYY.add("4Q05");
		qtrQYY.add("1Q06");
		qtrQYY.add("2Q06");
		qtrQYY.add("3Q06");
		*/
	}
	/**
	 * @return Returns the qtrQYY.
	 */
	public ArrayList getQtrQYY() {
		return qtrQYY;
	}
	/**
	 * @param qtrQYY The qtrQYY to set.
	 */
	public void setQtrQYY(ArrayList qtrQYY) {
		this.qtrQYY = qtrQYY;
	}
	/**
	 * @return Returns the rows.
	 */
	public ArrayList getRows() {
	
		return rows;
	}
	/**
	 * @param rows The rows to set.
	 */
	public void setRows(ArrayList rows) {
		this.rows = rows;
	}
	
}

