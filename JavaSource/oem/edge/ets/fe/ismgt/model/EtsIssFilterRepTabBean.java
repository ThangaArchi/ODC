package oem.edge.ets.fe.ismgt.model;

import java.util.*;
import java.io.*;
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

/**
 * @author v2phani
 * This  class represents the columns in the report table of filtered issues
 * and and object representation of the report
 *
 */
public  class EtsIssFilterRepTabBean implements Serializable{
	
	public static final String VERSION = "1.30.1.20";
	
	private String issueZone; //new issue or cq issue user issue
	private String issueProblemId;
	private String issueCqTrkId;
	private String issueTitle;
	private String issueType;
	private String issueSeverity;
	private String issueStatus;
	private String issueClass;
	private String issueSubmitter;
	private String issueSubmitterName;
	private String issueLastTime;
	private String currentOwnerId;
	private String currentOwnerName;
	private String currentBackupOwnerId;
	private String currentBackupOwnerName;
	private String issueSource;
	private String refId;

	/**
	 * Constructor for EtsIssFilterIssueRepTab.
	 */
	public EtsIssFilterRepTabBean() {
		super();
	}

	
	/**
	 * Returns the issueSeverity.
	 * @return String
	 */
	public String getIssueSeverity() {
		return issueSeverity;
	}

	/**
	 * Returns the issueStatus.
	 * @return String
	 */
	public String getIssueStatus() {
		return issueStatus;
	}

	/**
	 * Returns the issueTitle.
	 * @return String
	 */
	public String getIssueTitle() {
		return issueTitle;
	}

	/**
	 * Returns the issueType.
	 * @return String
	 */
	public String getIssueType() {
		return issueType;
	}

	

	/**
	 * Sets the issueSeverity.
	 * @param issueSeverity The issueSeverity to set
	 */
	public void setIssueSeverity(String issueSeverity) {
		this.issueSeverity = issueSeverity;
	}

	/**
	 * Sets the issueStatus.
	 * @param issueStatus The issueStatus to set
	 */
	public void setIssueStatus(String issueStatus) {
		this.issueStatus = issueStatus;
	}

	/**
	 * Sets the issueTitle.
	 * @param issueTitle The issueTitle to set
	 */
	public void setIssueTitle(String issueTitle) {
		this.issueTitle = issueTitle;
	}

	/**
	 * Sets the issueType.
	 * @param issueType The issueType to set
	 */
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	/**
	 * Returns the issueCqTrkId.
	 * @return String
	 */
	public String getIssueCqTrkId() {
		return issueCqTrkId;
	}

	/**
	 * Returns the issueProblemId.
	 * @return String
	 */
	public String getIssueProblemId() {
		return issueProblemId;
	}

	/**
	 * Sets the issueCqTrkId.
	 * @param issueCqTrkId The issueCqTrkId to set
	 */
	public void setIssueCqTrkId(String issueCqTrkId) {
		this.issueCqTrkId = issueCqTrkId;
	}

	/**
	 * Sets the issueProblemId.
	 * @param issueProblemId The issueProblemId to set
	 */
	public void setIssueProblemId(String issueProblemId) {
		this.issueProblemId = issueProblemId;
	}

	/**
	 * Returns the issueSubmitter.
	 * @return String
	 */
	public String getIssueSubmitter() {
		return issueSubmitter;
	}

	/**
	 * Sets the issueSubmitter.
	 * @param issueSubmitter The issueSubmitter to set
	 */
	public void setIssueSubmitter(String issueSubmitter) {
		this.issueSubmitter = issueSubmitter;
	}

	/**
	 * Returns the issueLastTime.
	 * @return String
	 */
	public String getIssueLastTime() {
		return issueLastTime;
	}

	/**
	 * Sets the issueLastTime.
	 * @param issueLastTime The issueLastTime to set
	 */
	public void setIssueLastTime(String issueLastTime) {
		this.issueLastTime = issueLastTime;
	}

	/**
	 * Returns the issueSubmitterName.
	 * @return String
	 */
	public String getIssueSubmitterName() {
		return issueSubmitterName;
	}

	/**
	 * Sets the issueSubmitterName.
	 * @param issueSubmitterName The issueSubmitterName to set
	 */
	public void setIssueSubmitterName(String issueSubmitterName) {
		this.issueSubmitterName = issueSubmitterName;
	}

	

	/**
	 * Returns the currentOwnerId.
	 * @return String
	 */
	public String getCurrentOwnerId() {
		return currentOwnerId;
	}

	/**
	 * Returns the currentOwnerName.
	 * @return String
	 */
	public String getCurrentOwnerName() {
		return currentOwnerName;
	}

	/**
	 * Sets the currentOwnerId.
	 * @param currentOwnerId The currentOwnerId to set
	 */
	public void setCurrentOwnerId(String currentOwnerId) {
		this.currentOwnerId = currentOwnerId;
	}

	/**
	 * Sets the currentOwnerName.
	 * @param currentOwnerName The currentOwnerName to set
	 */
	public void setCurrentOwnerName(String currentOwnerName) {
		this.currentOwnerName = currentOwnerName;
	}

	
	
	/**
	 * @return Returns the currentBackupOwnerId.
	 */
	public String getCurrentBackupOwnerId() {
		return currentBackupOwnerId;
	}
	/**
	 * @param currentBackupOwnerId The currentBackupOwnerId to set.
	 */
	public void setCurrentBackupOwnerId(String currentBackupOwnerId) {
		this.currentBackupOwnerId = currentBackupOwnerId;
	}
	/**
	 * @return Returns the currentBackupOwnerName.
	 */
	public String getCurrentBackupOwnerName() {
		return currentBackupOwnerName;
	}
	/**
	 * @param currentBackupOwnerName The currentBackupOwnerName to set.
	 */
	public void setCurrentBackupOwnerName(String currentBackupOwnerName) {
		this.currentBackupOwnerName = currentBackupOwnerName;
	}
	/**
	 * Returns the issueZone.
	 * @return String
	 */
	public String getIssueZone() {
		return issueZone;
	}

	/**
	 * Sets the issueZone.
	 * @param issueZone The issueZone to set
	 */
	public void setIssueZone(String issueZone) {
		this.issueZone = issueZone;
	}

	/**
	 * Returns the issueClass.
	 * @return String
	 */
	public String getIssueClass() {
		return issueClass;
	}

	/**
	 * Sets the issueClass.
	 * @param issueClass The issueClass to set
	 */
	public void setIssueClass(String issueClass) {
		this.issueClass = issueClass;
	}

	/**
	 * @return
	 */
	public String getIssueSource() {
		return issueSource;
	}

	/**
	 * @param string
	 */
	public void setIssueSource(String string) {
		issueSource = string;
	}

	/**
	 * @return
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @param string
	 */
	public void setRefId(String string) {
		refId = string;
	}

}

