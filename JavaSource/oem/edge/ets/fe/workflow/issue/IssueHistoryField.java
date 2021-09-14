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

import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;
//TODO: 00 Not yet uploaded in CMVC

/**
 * Class       : IssueHistoryField
 * Package     : oem.edge.ets.fe.workflow.issue
 * Description : 
 * Date		   : Nov 14, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssueHistoryField {
	private static Log logger = WorkflowLogger.getLogger(IssueHistoryField.class);
	private String fieldName = null;
	private String oldValue = null;
	private String newValue = null;
	
	/**
	 * @return Returns the fieldName.
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName The fieldName to set.
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return Returns the newValue.
	 */
	public String getNewValue() {
		return newValue;
	}
	/**
	 * @param newValue The newValue to set.
	 */
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	/**
	 * @return Returns the oldValue.
	 */
	public String getOldValue() {
		return oldValue;
	}
	/**
	 * @param oldValue The oldValue to set.
	 */
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
}

