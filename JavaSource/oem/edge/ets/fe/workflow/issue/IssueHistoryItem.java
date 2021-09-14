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
 * Class       : IssueHistoryItem
 * Package     : oem.edge.ets.fe.workflow.issue
 * Description : 
 * Date		   : Nov 14, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssueHistoryItem {
	private static Log logger = WorkflowLogger.getLogger(IssueHistoryItem.class);
	private ArrayList fields = new ArrayList();
	private String modified_by = null;
	private String action_date = null;
	private String action_taken = null;
	private String comments = null;
	private String history_id = null;
	/**
	 * @return Returns the action_date.
	 */
	public String getAction_date() {
		return action_date;
	}
	/**
	 * @param action_date The action_date to set.
	 */
	public void setAction_date(String action_date) {
		this.action_date = action_date;
	}
	/**
	 * @return Returns the action_taken.
	 */
	public String getAction_taken() {
		return action_taken;
	}
	/**
	 * @param action_taken The action_taken to set.
	 */
	public void setAction_taken(String action_taken) {
		this.action_taken = action_taken;
	}
	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return Returns the fields.
	 */
	public ArrayList getFields() {
		return fields;
	}
	/**
	 * @param fields The fields to set.
	 */
	public void setFields(ArrayList fields) {
		this.fields = fields;
	}
	/**
	 * @return Returns the modified_by.
	 */
	public String getModified_by() {
		return modified_by;
	}
	/**
	 * @param modified_by The modified_by to set.
	 */
	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}
	/**
	 * @return Returns the history_id.
	 */
	public String getHistory_id() {
		return history_id;
	}
	/**
	 * @param history_id The history_id to set.
	 */
	public void setHistory_id(String history_id) {
		this.history_id = history_id;
	}
}

