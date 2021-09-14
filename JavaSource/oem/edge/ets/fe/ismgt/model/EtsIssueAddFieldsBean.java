/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2008                                     */
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

package oem.edge.ets.fe.ismgt.model;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @author Dharanendra Prasad
 *
 * This class represents the tables ets.ets_issue_add_fields
 */
public class EtsIssueAddFieldsBean {
		
	public static final String VERSION = "1.0";
		
	///ETS_ISSUE_ADD_FIELDS///
	private String projectId;
	private int fieldId;
	private String fieldLabel;
	private String lastUserId;
	private Timestamp lastTimeStamp;
	
	public EtsIssueAddFieldsBean() {
		super();
	}
	
	/**
	 * @return Returns the fieldId.
	 */
	public int getFieldId() {
		return fieldId;
	}
	/**
	 * @param fieldId The fieldId to set.
	 */
	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
	/**
	 * @return Returns the fieldLabel.
	 */
	public String getFieldLabel() {
		return fieldLabel;
	}
	/**
	 * @param fieldLabel The fieldLabel to set.
	 */
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	/**
	 * @return Returns the lastTimeStamp.
	 */
	public Timestamp getLastTimeStamp() {
		return lastTimeStamp;
	}
	/**
	 * @param lastTimeStamp The lastTimeStamp to set.
	 */
	public void setLastTimeStamp(Timestamp lastTimeStamp) {
		this.lastTimeStamp = lastTimeStamp;
	}
	/**
	 * @return Returns the lastUserId.
	 */
	public String getLastUserId() {
		return lastUserId;
	}
	/**
	 * @param lastUserId The lastUserId to set.
	 */
	public void setLastUserId(String lastUserId) {
		this.lastUserId = lastUserId;
	}
	/**
	 * @return Returns the projectId.
	 */
	public String getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId The projectId to set.
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
