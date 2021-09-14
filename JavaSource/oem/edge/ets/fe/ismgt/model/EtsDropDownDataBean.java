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

import java.sql.Timestamp;

/**
 * @author v2phani
 * This class represents the tables cq.ets_dropdown_data
 */
public class EtsDropDownDataBean {
	
	public static final String VERSION = "1.43";
	
	///ETS_DROPDOWN_DATA///
	private String dataId;
	private String projectId;
	private String projectName;
	private String issueClass;
	private String issueType;
	private String subTypeA;
	private String subTypeB;
	private String subTypeC;
	private String subTypeD;
	private String issueSource;
	private String issueAccess;
	private String activeFlag;
	private String issueEtsR1;
	private String issueEtsR2;
	
	//ETS_INDEP_DATA//
	private String fieldName;
	private String fieldValue;
	
	
	//ETS FORM LABEL DATA//
	private String issueTypeName;
	private String issueTypeValue;
	
	//ETS OWNER DATA//
	private EtsIssOwnerInfo ownerInfo;

	//ETS BACKUP OWNER DATA //
	private EtsIssOwnerInfo backupOwnerInfo;
	
	//submitter info
	private String lastUserId;
	private Timestamp lastTimeStamp;
	
	/**
	 * Constructor for EtsDropDownDataBean.
	 */
	public EtsDropDownDataBean() {
		super();
	}

	

	/**
	 * Returns the activeFlag.
	 * @return String
	 */
	public String getActiveFlag() {
		return activeFlag;
	}

	/**
	 * Returns the dataId.
	 * @return String
	 */
	public String getDataId() {
		return dataId;
	}

	/**
	 * Returns the fieldName.
	 * @return String
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Returns the fieldValue.
	 * @return String
	 */
	public String getFieldValue() {
		return fieldValue;
	}

	/**
	 * Returns the issueAccess.
	 * @return String
	 */
	public String getIssueAccess() {
		return issueAccess;
	}

	/**
	 * Returns the issueClass.
	 * @return String
	 */
	public String getIssueClass() {
		return issueClass;
	}

	/**
	 * Returns the issueEtsR1.
	 * @return String
	 */
	public String getIssueEtsR1() {
		return issueEtsR1;
	}

	/**
	 * Returns the issueEtsR2.
	 * @return String
	 */
	public String getIssueEtsR2() {
		return issueEtsR2;
	}

	/**
	 * Returns the issueSource.
	 * @return String
	 */
	public String getIssueSource() {
		return issueSource;
	}

	/**
	 * Returns the issueType.
	 * @return String
	 */
	public String getIssueType() {
		return issueType;
	}

	/**
	 * Returns the issueTypeName.
	 * @return String
	 */
	public String getIssueTypeName() {
		return issueTypeName;
	}

	/**
	 * Returns the issueTypeValue.
	 * @return String
	 */
	public String getIssueTypeValue() {
		return issueTypeValue;
	}

	/**
	 * Returns the projectId.
	 * @return String
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Returns the projectName.
	 * @return String
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Returns the subTypeA.
	 * @return String
	 */
	public String getSubTypeA() {
		return subTypeA;
	}

	/**
	 * Returns the subTypeB.
	 * @return String
	 */
	public String getSubTypeB() {
		return subTypeB;
	}

	/**
	 * Returns the subTypeC.
	 * @return String
	 */
	public String getSubTypeC() {
		return subTypeC;
	}

	/**
	 * Returns the subTypeD.
	 * @return String
	 */
	public String getSubTypeD() {
		return subTypeD;
	}

	/**
	 * Sets the activeFlag.
	 * @param activeFlag The activeFlag to set
	 */
	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}

	/**
	 * Sets the dataId.
	 * @param dataId The dataId to set
	 */
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	/**
	 * Sets the fieldName.
	 * @param fieldName The fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Sets the fieldValue.
	 * @param fieldValue The fieldValue to set
	 */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	/**
	 * Sets the issueAccess.
	 * @param issueAccess The issueAccess to set
	 */
	public void setIssueAccess(String issueAccess) {
		this.issueAccess = issueAccess;
	}

	/**
	 * Sets the issueClass.
	 * @param issueClass The issueClass to set
	 */
	public void setIssueClass(String issueClass) {
		this.issueClass = issueClass;
	}

	/**
	 * Sets the issueEtsR1.
	 * @param issueEtsR1 The issueEtsR1 to set
	 */
	public void setIssueEtsR1(String issueEtsR1) {
		this.issueEtsR1 = issueEtsR1;
	}

	/**
	 * Sets the issueEtsR2.
	 * @param issueEtsR2 The issueEtsR2 to set
	 */
	public void setIssueEtsR2(String issueEtsR2) {
		this.issueEtsR2 = issueEtsR2;
	}

	/**
	 * Sets the issueSource.
	 * @param issueSource The issueSource to set
	 */
	public void setIssueSource(String issueSource) {
		this.issueSource = issueSource;
	}

	/**
	 * Sets the issueType.
	 * @param issueType The issueType to set
	 */
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	/**
	 * Sets the issueTypeName.
	 * @param issueTypeName The issueTypeName to set
	 */
	public void setIssueTypeName(String issueTypeName) {
		this.issueTypeName = issueTypeName;
	}

	/**
	 * Sets the issueTypeValue.
	 * @param issueTypeValue The issueTypeValue to set
	 */
	public void setIssueTypeValue(String issueTypeValue) {
		this.issueTypeValue = issueTypeValue;
	}

	/**
	 * Sets the projectId.
	 * @param projectId The projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * Sets the projectName.
	 * @param projectName The projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * Sets the subTypeA.
	 * @param subTypeA The subTypeA to set
	 */
	public void setSubTypeA(String subTypeA) {
		this.subTypeA = subTypeA;
	}

	/**
	 * Sets the subTypeB.
	 * @param subTypeB The subTypeB to set
	 */
	public void setSubTypeB(String subTypeB) {
		this.subTypeB = subTypeB;
	}

	/**
	 * Sets the subTypeC.
	 * @param subTypeC The subTypeC to set
	 */
	public void setSubTypeC(String subTypeC) {
		this.subTypeC = subTypeC;
	}

	/**
	 * Sets the subTypeD.
	 * @param subTypeD The subTypeD to set
	 */
	public void setSubTypeD(String subTypeD) {
		this.subTypeD = subTypeD;
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
	 * @return Returns the backupOwnerInfo.
	 */
	public EtsIssOwnerInfo getBackupOwnerInfo() {
		return backupOwnerInfo;
	}
	/**
	 * @param backupOwnerInfo The backupOwnerInfo to set.
	 */
	public void setBackupOwnerInfo(EtsIssOwnerInfo backupOwnerInfo) {
		this.backupOwnerInfo = backupOwnerInfo;
	}
	
	
	
	/**
	 * @return
	 */
	public Timestamp getLastTimeStamp() {
		return lastTimeStamp;
	}

	/**
	 * @return
	 */
	public String getLastUserId() {
		return lastUserId;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimeStamp(Timestamp timestamp) {
		lastTimeStamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setLastUserId(String string) {
		lastUserId = string;
	}

}

