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


package oem.edge.ets.fe.workflow.issue;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : IssueHistoryBean
 * Package     : oem.edge.ets.fe.workflow.issue
 * Description : 
 * Date		   : Nov 14, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssueHistoryBean {
	private static Log logger = WorkflowLogger.getLogger(IssueHistoryBean.class);
	private ArrayList items = new ArrayList(); 
	
	/**
	 * @return Returns the items.
	 */
	public ArrayList getItems() {
		return items;
	}
	/**
	 * @param items The items to set.
	 */
	public void setItems(ArrayList items) {
		this.items = items;
	}
}

