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

import oem.edge.amt.AmtCommonUtils;

/**
 * @author Dharanendra Prasad
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EtsCustFieldInfoModel {
	public static final String VERSION = "1.0";

	private String projectId;
	private ArrayList fieldId;
	private ArrayList fieldLabel;
	private String lastUserId;
	private Timestamp lastTimeStamp;
	private int newFieldsReq;
	//gui state actions
	private int currentActionState; //action state
	private int nextActionState; //next state
	private int cancelActionState; //cancel action state
	private String errMsg;
	
	public EtsCustFieldInfoModel() {
		super();
	}

	
	/**
	 * @return Returns the fieldId.
	 */
	public ArrayList getFieldId() {
		return fieldId;
	}
	/**
	 * @param fieldId The fieldId to set.
	 */
	public void setFieldId(ArrayList fieldId) {
		this.fieldId = fieldId;
	}
	/**
	 * @return Returns the fieldLabel.
	 */
	public ArrayList getFieldLabel() {
		return fieldLabel;
	}
	/**
	 * @param fieldLabel The fieldLabel to set.
	 */
	public void setFieldLabel(ArrayList fieldLabel) {
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

	
	
	/**
	 * @return Returns the cancelActionState.
	 */
	public int getCancelActionState() {
		return cancelActionState;
	}
	/**
	 * @param cancelActionState The cancelActionState to set.
	 */
	public void setCancelActionState(int cancelActionState) {
		this.cancelActionState = cancelActionState;
	}
	/**
	 * @return Returns the currentActionState.
	 */
	public int getCurrentActionState() {
		return currentActionState;
	}
	/**
	 * @param currentActionState The currentActionState to set.
	 */
	public void setCurrentActionState(int currentActionState) {
		this.currentActionState = currentActionState;
	}
	/**
	 * @return Returns the newFieldsReq.
	 */
	public int getNewFieldsReq() {
		return newFieldsReq;
	}
	/**
	 * @param newFieldsReq The newFieldsReq to set.
	 */
	public void setNewFieldsReq(int newFieldsReq) {
		this.newFieldsReq = newFieldsReq;
	}
	/**
	 * @return Returns the nextActionState.
	 */
	public int getNextActionState() {
		return nextActionState;
	}
	/**
	 * @param nextActionState The nextActionState to set.
	 */
	public void setNextActionState(int nextActionState) {
		this.nextActionState = nextActionState;
	}
	
	public String toString() {
		StringBuffer sbInfo = new StringBuffer();
		if(AmtCommonUtils.isResourceDefined(this.projectId))
			sbInfo.append("PROJECT_ID : " + this.projectId + " \n");
		
		int inx =  0;
		if(this.fieldId != null) {
			inx =  this.fieldId.size();
			for(int i=0; i<inx; i++) {
				sbInfo.append("FIELD_ID : " + this.fieldId.get(i).toString() + " \n");
			}
		}
		if(this.fieldLabel != null) {
			inx = this.fieldLabel.size();
			for(int i=0; i<inx; i++) {
				sbInfo.append("FIELD_LABEL : " + this.fieldLabel.get(i).toString() + " \n");
			}
		}
		if(AmtCommonUtils.isResourceDefined(this.lastUserId))
			sbInfo.append("LastUserId : " + this.lastUserId + " \n");
	
		if(this.lastTimeStamp != null)
			sbInfo.append("LastTimeStamp : " + this.lastTimeStamp.toString() + " \n");
		
		if(this.currentActionState > 0)
			sbInfo.append("CurrentActionState : " + this.currentActionState + " \n");
		
		if(this.nextActionState > 0)
			sbInfo.append("NextActionState : " + this.nextActionState + " \n");
					
		return sbInfo.toString();
	}

	
	
	/**
	 * @return Returns the errMsg.
	 */
	public String getErrMsg() {
		return errMsg;
	}
	/**
	 * @param errMsg The errMsg to set.
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
