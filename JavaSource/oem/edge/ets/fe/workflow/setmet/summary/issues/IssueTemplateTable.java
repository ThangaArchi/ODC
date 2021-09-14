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


package oem.edge.ets.fe.workflow.setmet.summary.issues;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : IssueTemplateTable
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.issues
 * Description : 
 * Date		   : Dec 7, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssueTemplateTable {
	private static Log logger = WorkflowLogger.getLogger(IssueTemplateTable.class);
	
	private ArrayList rows = new ArrayList();
	
	public ArrayList getRows() {
		return rows;
	}
	public void setRows(ArrayList tableRows) {
		rows = tableRows;
	}
}

