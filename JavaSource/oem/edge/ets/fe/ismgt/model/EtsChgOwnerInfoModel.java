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

package oem.edge.ets.fe.ismgt.model;

import java.util.ArrayList;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsChgOwnerInfoModel {

	public static final String VERSION = "1.23";

	private String edgeProblemId;
	private String issueType;
	private int seqNo;
	private ArrayList ownerIdList;
	private ArrayList prevOwnerIdList;
	private EtsIssProjectMember submitterInfo;
	private EtsIssOwnerInfo ownerInfo;
	private String errMsg;

	private int previousActionState; //previous state
	private int currentActionState; //action state
	private int nextActionState; //next state
	private int cancelActionState; //cancel action state

	private String lastUserId;
	private String lastUserFirstName;
	private String lastUserLastName;

	/**
	 * 
	 */
	public EtsChgOwnerInfoModel() {
		super();

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
	public String getIssueType() {
		return issueType;
	}

	/**
	 * @return
	 */
	public ArrayList getOwnerIdList() {
		return ownerIdList;
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
	public void setIssueType(String string) {
		issueType = string;
	}

	/**
	 * @param list
	 */
	public void setOwnerIdList(ArrayList list) {
		ownerIdList = list;
	}

	/**
	 * @return
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * @param string
	 */
	public void setErrMsg(String string) {
		errMsg = string;
	}

	/**
	 * @return
	 */
	public EtsIssOwnerInfo getOwnerInfo() {
		return ownerInfo;
	}

	/**
	 * @param info
	 */
	public void setOwnerInfo(EtsIssOwnerInfo info) {
		ownerInfo = info;
	}

	/**
	 * @return
	 */
	public EtsIssProjectMember getSubmitterInfo() {
		return submitterInfo;
	}

	/**
	 * @param member
	 */
	public void setSubmitterInfo(EtsIssProjectMember member) {
		submitterInfo = member;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevOwnerIdList() {
		return prevOwnerIdList;
	}

	/**
	 * @param list
	 */
	public void setPrevOwnerIdList(ArrayList list) {
		prevOwnerIdList = list;
	}

	/**
	 * @return
	 */
	public int getCancelActionState() {
		return cancelActionState;
	}

	/**
	 * @return
	 */
	public int getCurrentActionState() {
		return currentActionState;
	}

	/**
	 * @return
	 */
	public int getNextActionState() {
		return nextActionState;
	}

	/**
	 * @return
	 */
	public int getPreviousActionState() {
		return previousActionState;
	}

	/**
	 * @param i
	 */
	public void setCancelActionState(int i) {
		cancelActionState = i;
	}

	/**
	 * @param i
	 */
	public void setCurrentActionState(int i) {
		currentActionState = i;
	}

	/**
	 * @param i
	 */
	public void setNextActionState(int i) {
		nextActionState = i;
	}

	/**
	 * @param i
	 */
	public void setPreviousActionState(int i) {
		previousActionState = i;
	}

	/**
	 * @return
	 */
	public int getSeqNo() {
		return seqNo;
	}

	/**
	 * @param i
	 */
	public void setSeqNo(int i) {
		seqNo = i;
	}

	/**
	 * @return
	 */
	public String getLastUserId() {
		return lastUserId;
	}

	/**
	 * @param string
	 */
	public void setLastUserId(String string) {
		lastUserId = string;
	}

	/**
	 * @return
	 */
	public String getLastUserFirstName() {
		return lastUserFirstName;
	}

	/**
	 * @return
	 */
	public String getLastUserLastName() {
		return lastUserLastName;
	}

	/**
	 * @param string
	 */
	public void setLastUserFirstName(String string) {
		lastUserFirstName = string;
	}

	/**
	 * @param string
	 */
	public void setLastUserLastName(String string) {
		lastUserLastName = string;
	}

} //end of class
