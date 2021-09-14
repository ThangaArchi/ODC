/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.qbr.summary;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.qbr.initialize.InitializeDAO;
import oem.edge.ets.fe.workflow.util.pdfutils.QBRCommonData;

/**
 * Class       : ScoringSummaryBL
 * Package     : oem.edge.ets.fe.workflow.qbr.summary
 * Description : 
 * Date		   : Mar 5, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class ScoringSummaryBL {
	public QBRCommonData getCommonData(String projectID,String workflowID)
	{
		String prevWF = (new ScoringSummaryDAO()).getPreviousWF(workflowID);
		
		if(prevWF!=null)
			return  (new ScoringSummaryDAO()).getCommonData(workflowID, prevWF);
		else
			return  (new ScoringSummaryDAO()).getCommonData(workflowID,null);
		
	}
	public ArrayList getScore(String projectID, String workflowID)
	{
		String prevWF = (new ScoringSummaryDAO()).getPreviousWF(workflowID);
		
		if(prevWF!=null)
			return  (new ScoringSummaryDAO()).getScore(workflowID, prevWF);
		else
			return  (new ScoringSummaryDAO()).getScore(workflowID,null);
	}
}

