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

/**
 * @author v2phani
 * This class represents the view params(the titles/subtitle) that appear
 * in report of issues/change requests
 */
public class EtsFilterRepViewParamsBean {
	
	public static final String VERSION = "1.47";
	
	
	private String repWelcomeMsg;	
	private String repHeaderName;
	private String repTitleName;
	private String repIssueTypeName;
	private String repSubName;
	private String repCownerName;
	private String repSeverityName;
	private String repStatusName;
	private String repTrkId;
	

	/**
	 * Constructor for EtsFilterRepViewParamsBean.
	 */
	public EtsFilterRepViewParamsBean() {
		super();
	}

	/**
	 * Returns the repCownerName.
	 * @return String
	 */
	public String getRepCownerName() {
		return repCownerName;
	}

	/**
	 * Returns the repHeaderName.
	 * @return String
	 */
	public String getRepHeaderName() {
		return repHeaderName;
	}

	/**
	 * Returns the repIssueTypeName.
	 * @return String
	 */
	public String getRepIssueTypeName() {
		return repIssueTypeName;
	}

	/**
	 * Returns the repSeverityName.
	 * @return String
	 */
	public String getRepSeverityName() {
		return repSeverityName;
	}

	/**
	 * Returns the repStatusName.
	 * @return String
	 */
	public String getRepStatusName() {
		return repStatusName;
	}

	/**
	 * Returns the repSubName.
	 * @return String
	 */
	public String getRepSubName() {
		return repSubName;
	}

	/**
	 * Returns the repTitleName.
	 * @return String
	 */
	public String getRepTitleName() {
		return repTitleName;
	}

	/**
	 * Sets the repCownerName.
	 * @param repCownerName The repCownerName to set
	 */
	public void setRepCownerName(String repCownerName) {
		this.repCownerName = repCownerName;
	}

	/**
	 * Sets the repHeaderName.
	 * @param repHeaderName The repHeaderName to set
	 */
	public void setRepHeaderName(String repHeaderName) {
		this.repHeaderName = repHeaderName;
	}

	/**
	 * Sets the repIssueTypeName.
	 * @param repIssueTypeName The repIssueTypeName to set
	 */
	public void setRepIssueTypeName(String repIssueTypeName) {
		this.repIssueTypeName = repIssueTypeName;
	}

	/**
	 * Sets the repSeverityName.
	 * @param repSeverityName The repSeverityName to set
	 */
	public void setRepSeverityName(String repSeverityName) {
		this.repSeverityName = repSeverityName;
	}

	/**
	 * Sets the repStatusName.
	 * @param repStatusName The repStatusName to set
	 */
	public void setRepStatusName(String repStatusName) {
		this.repStatusName = repStatusName;
	}

	/**
	 * Sets the repSubName.
	 * @param repSubName The repSubName to set
	 */
	public void setRepSubName(String repSubName) {
		this.repSubName = repSubName;
	}

	/**
	 * Sets the repTitleName.
	 * @param repTitleName The repTitleName to set
	 */
	public void setRepTitleName(String repTitleName) {
		this.repTitleName = repTitleName;
	}

	/**
	 * Returns the repWelcomeMsg.
	 * @return String
	 */
	public String getRepWelcomeMsg() {
		return repWelcomeMsg;
	}

	/**
	 * Sets the repWelcomeMsg.
	 * @param repWelcomeMsg The repWelcomeMsg to set
	 */
	public void setRepWelcomeMsg(String repWelcomeMsg) {
		this.repWelcomeMsg = repWelcomeMsg;
	}

	/**
	 * @return
	 */
	public String getRepTrkId() {
		return repTrkId;
	}

	/**
	 * @param string
	 */
	public void setRepTrkId(String string) {
		repTrkId = string;
	}

}

