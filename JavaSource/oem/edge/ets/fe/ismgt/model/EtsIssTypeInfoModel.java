/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
public class EtsIssTypeInfoModel extends EtsDropDownDataBean {

	public static final String VERSION = "1.10";

	//submitter profiles
	private EtsIssProjectMember submitterProfile;

	//owner list
	private ArrayList ownerList;
	private ArrayList prevOwnerList;
	private EtsIssProjectMember ownerProfile;
	
	//backup owner list
	private ArrayList backupOwnerList;
	private ArrayList prevBackupOwnerList;
	private EtsIssProjectMember backupOwnerProfile;
	private String backupOwnershipInternal;
	
	//iss type list
	private ArrayList issueTypeList;
	private ArrayList prevIssueTypeList;
	
	//
	private String guiIssueAccess;
	private String ownerShipInternal;
	
	//gui state actions
	private int currentActionState; //action state
	private int nextActionState; //next state
	private int cancelActionState; //cancel action state
	private String errMsg;
	
	
	//for subscriptions add/del
	private ArrayList subsAddIssTypList;
	private ArrayList prevSubsAddIssTypList;
	private ArrayList subsDelIssTypList;
	private ArrayList prevSubsDelIssTypList;
	/**
	 * 
	 */
	public EtsIssTypeInfoModel() {
		super();
		// TODO Auto-generated constructor stub
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
	public String getErrMsg() {
		return errMsg;
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
	public ArrayList getOwnerList() {
		return ownerList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevOwnerList() {
		return prevOwnerList;
	}

	/**
	 * @return
	 */
	public EtsIssProjectMember getSubmitterProfile() {
		return submitterProfile;
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
	 * @param string
	 */
	public void setErrMsg(String string) {
		errMsg = string;
	}

	/**
	 * @param i
	 */
	public void setNextActionState(int i) {
		nextActionState = i;
	}

	/**
	 * @param list
	 */
	public void setOwnerList(ArrayList list) {
		ownerList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevOwnerList(ArrayList list) {
		prevOwnerList = list;
	}

	/**
	 * @param member
	 */
	public void setSubmitterProfile(EtsIssProjectMember member) {
		submitterProfile = member;
	}

	/**
	 * @return
	 */
	public String getOwnerShipInternal() {
		return ownerShipInternal;
	}

	/**
	 * @param string
	 */
	public void setOwnerShipInternal(String string) {
		ownerShipInternal = string;
	}

	
	/**
	 * @return
	 */
	public ArrayList getIssueTypeList() {
		return issueTypeList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevIssueTypeList() {
		return prevIssueTypeList;
	}

	/**
	 * @param list
	 */
	public void setIssueTypeList(ArrayList list) {
		issueTypeList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevIssueTypeList(ArrayList list) {
		prevIssueTypeList = list;
	}

	
	/**
	 * @return
	 */
	public EtsIssProjectMember getOwnerProfile() {
		return ownerProfile;
	}

	/**
	 * @param member
	 */
	public void setOwnerProfile(EtsIssProjectMember member) {
		ownerProfile = member;
	}

	/**
	 * @return
	 */
	public String getGuiIssueAccess() {
		return guiIssueAccess;
	}

	/**
	 * @param string
	 */
	public void setGuiIssueAccess(String string) {
		guiIssueAccess = string;
	}

	/**
	 * @return
	 */
	public ArrayList getSubsAddIssTypList() {
		return subsAddIssTypList;
	}

	/**
	 * @return
	 */
	public ArrayList getSubsDelIssTypList() {
		return subsDelIssTypList;
	}

	/**
	 * @param list
	 */
	public void setSubsAddIssTypList(ArrayList list) {
		subsAddIssTypList = list;
	}

	/**
	 * @param list
	 */
	public void setSubsDelIssTypList(ArrayList list) {
		subsDelIssTypList = list;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevSubsAddIssTypList() {
		return prevSubsAddIssTypList;
	}

	/**
	 * @return
	 */
	public ArrayList getPrevSubsDelIssTypList() {
		return prevSubsDelIssTypList;
	}

	/**
	 * @param list
	 */
	public void setPrevSubsAddIssTypList(ArrayList list) {
		prevSubsAddIssTypList = list;
	}

	/**
	 * @param list
	 */
	public void setPrevSubsDelIssTypList(ArrayList list) {
		prevSubsDelIssTypList = list;
	}

	
	
	/**
	 * @return Returns the backupOwnerList.
	 */
	public ArrayList getBackupOwnerList() {
		return backupOwnerList;
	}
	/**
	 * @param backupOwnerList The backupOwnerList to set.
	 */
	public void setBackupOwnerList(ArrayList backupOwnerList) {
		this.backupOwnerList = backupOwnerList;
	}
	/**
	 * @return Returns the backupOwnerProfile.
	 */
	public EtsIssProjectMember getBackupOwnerProfile() {
		return backupOwnerProfile;
	}
	/**
	 * @param backupOwnerProfile The backupOwnerProfile to set.
	 */
	public void setBackupOwnerProfile(EtsIssProjectMember backupOwnerProfile) {
		this.backupOwnerProfile = backupOwnerProfile;
	}
	/**
	 * @return Returns the prevBackupOwnerList.
	 */
	public ArrayList getPrevBackupOwnerList() {
		return prevBackupOwnerList;
	}
	/**
	 * @param prevBackupOwnerList The prevBackupOwnerList to set.
	 */
	public void setPrevBackupOwnerList(ArrayList prevBackupOwnerList) {
		this.prevBackupOwnerList = prevBackupOwnerList;
	}
	

	/**
	 * @return Returns the backupOwnershipInternal.
	 */
	public String getBackupOwnershipInternal() {
		return backupOwnershipInternal;
	}
	/**
	 * @param backupOwnershipInternal The backupOwnershipInternal to set.
	 */
	public void setBackupOwnershipInternal(String backupOwnershipInternal) {
		this.backupOwnershipInternal = backupOwnershipInternal;
	}
}
