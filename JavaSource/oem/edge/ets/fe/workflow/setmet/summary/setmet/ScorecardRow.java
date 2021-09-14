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

import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : ScorecardRow
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.setmet
 * Description : 
 * Date		   : Nov 20, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class ScorecardRow {
	private static Log logger = WorkflowLogger.getLogger(ScorecardRow.class);
	private String question = null;
	private String[] scores = null;
	
	/**
	 * @return Returns the question.
	 */
	public String getQuestion() {
		return question;
	}
	/**
	 * @param question The question to set.
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
	/**
	 * @return Returns the scores.
	 */
	public String[] getScores() {
		return scores;
	}
	/**
	 * @param scores The scores to set.
	 */
	public void setScores(String[] scores) {
		this.scores = scores;
	}
}

