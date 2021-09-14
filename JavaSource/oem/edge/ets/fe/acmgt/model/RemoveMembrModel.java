/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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
package oem.edge.ets.fe.acmgt.model;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemoveMembrModel {

	public static final String VERSION = "1.1";
	private boolean changePrimCntct;
	private String primCntctErrStr;
	private boolean changeIssues;
	private String issueErrMsgStr;
	private boolean changeTasks;
	private String taskErrMsgStr;
	private boolean changeClients;
	private String clientErrMsgStr;
	private boolean removemem;
	private String removeMembrMsg;
	///
	private String edgeProblemId;
	private String etsCcListStr;
	private String etsProjectId;

	/**
	 * 
	 */
	public RemoveMembrModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public boolean isChangeClients() {
		return changeClients;
	}

	/**
	 * @return
	 */
	public boolean isChangeIssues() {
		return changeIssues;
	}

	/**
	 * @return
	 */
	public boolean isChangePrimCntct() {
		return changePrimCntct;
	}

	/**
	 * @return
	 */
	public boolean isChangeTasks() {
		return changeTasks;
	}

	/**
	 * @param b
	 */
	public void setChangeClients(boolean b) {
		changeClients = b;
	}

	/**
	 * @param b
	 */
	public void setChangeIssues(boolean b) {
		changeIssues = b;
	}

	/**
	 * @param b
	 */
	public void setChangePrimCntct(boolean b) {
		changePrimCntct = b;
	}

	/**
	 * @param b
	 */
	public void setChangeTasks(boolean b) {
		changeTasks = b;
	}

	/**
	 * @return
	 */
	public String getClientErrMsgStr() {
		return clientErrMsgStr;
	}

	/**
	 * @return
	 */
	public String getIssueErrMsgStr() {
		return issueErrMsgStr;
	}

	/**
	 * @return
	 */
	public String getPrimCntctErrStr() {
		return primCntctErrStr;
	}

	/**
	 * @return
	 */
	public String getTaskErrMsgStr() {
		return taskErrMsgStr;
	}

	/**
	 * @param string
	 */
	public void setClientErrMsgStr(String string) {
		clientErrMsgStr = string;
	}

	/**
	 * @param string
	 */
	public void setIssueErrMsgStr(String string) {
		issueErrMsgStr = string;
	}

	/**
	 * @param string
	 */
	public void setPrimCntctErrStr(String string) {
		primCntctErrStr = string;
	}

	/**
	 * @param string
	 */
	public void setTaskErrMsgStr(String string) {
		taskErrMsgStr = string;
	}

	/**
	 * @return
	 */
	public boolean isRemovemem() {
		return removemem;
	}

	/**
	 * @return
	 */
	public String getRemoveMembrMsg() {
		return removeMembrMsg;
	}

	/**
	 * @param b
	 */
	public void setRemovemem(boolean b) {
		removemem = b;
	}

	/**
	 * @param string
	 */
	public void setRemoveMembrMsg(String string) {
		removeMembrMsg = string;
	}

	/**
	 * @return
	 */
	public String getEdgeProblemId() {
		return edgeProblemId;
	}

	/**
	 * @return
	 */
	public String getEtsCcListStr() {
		return etsCcListStr;
	}

	/**
	 * @param string
	 */
	public void setEdgeProblemId(String string) {
		edgeProblemId = string;
	}

	/**
	 * @param string
	 */
	public void setEtsCcListStr(String string) {
		etsCcListStr = string;
	}

	/**
	 * @return
	 */
	public String getEtsProjectId() {
		return etsProjectId;
	}

	/**
	 * @param string
	 */
	public void setEtsProjectId(String string) {
		etsProjectId = string;
	}

} //class
