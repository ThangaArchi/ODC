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
 * This class represents the names(title/subtitles) to be given for filter conditions
 * for issues/change requests
 */
public class EtsFilterCondsViewParamsBean {
	
	public static final String VERSION = "1.10";
	
	private String fcHeaderName;
	
	private String fcIssTypeName;
	private String fcSeverityName;
	private String fcStatusName;
	private String fcSubName;
	private String fcCownName;
	private String fcDateSubName;

	/**
	 * Constructor for EtsFilterCondsViewParamsBean.
	 */
	public EtsFilterCondsViewParamsBean() {
		super();
	}

	/**
	 * Returns the fcCownName.
	 * @return String
	 */
	public String getFcCownName() {
		return fcCownName;
	}

	/**
	 * Returns the fcDateSubName.
	 * @return String
	 */
	public String getFcDateSubName() {
		return fcDateSubName;
	}

	/**
	 * Returns the fcHeaderName.
	 * @return String
	 */
	public String getFcHeaderName() {
		return fcHeaderName;
	}

	/**
	 * Returns the fcIssTypeName.
	 * @return String
	 */
	public String getFcIssTypeName() {
		return fcIssTypeName;
	}

	/**
	 * Returns the fcSeverityName.
	 * @return String
	 */
	public String getFcSeverityName() {
		return fcSeverityName;
	}

	/**
	 * Returns the fcStatusName.
	 * @return String
	 */
	public String getFcStatusName() {
		return fcStatusName;
	}

	/**
	 * Returns the fcSubName.
	 * @return String
	 */
	public String getFcSubName() {
		return fcSubName;
	}

	/**
	 * Sets the fcCownName.
	 * @param fcCownName The fcCownName to set
	 */
	public void setFcCownName(String fcCownName) {
		this.fcCownName = fcCownName;
	}

	/**
	 * Sets the fcDateSubName.
	 * @param fcDateSubName The fcDateSubName to set
	 */
	public void setFcDateSubName(String fcDateSubName) {
		this.fcDateSubName = fcDateSubName;
	}

	/**
	 * Sets the fcHeaderName.
	 * @param fcHeaderName The fcHeaderName to set
	 */
	public void setFcHeaderName(String fcHeaderName) {
		this.fcHeaderName = fcHeaderName;
	}

	/**
	 * Sets the fcIssTypeName.
	 * @param fcIssTypeName The fcIssTypeName to set
	 */
	public void setFcIssTypeName(String fcIssTypeName) {
		this.fcIssTypeName = fcIssTypeName;
	}

	/**
	 * Sets the fcSeverityName.
	 * @param fcSeverityName The fcSeverityName to set
	 */
	public void setFcSeverityName(String fcSeverityName) {
		this.fcSeverityName = fcSeverityName;
	}

	/**
	 * Sets the fcStatusName.
	 * @param fcStatusName The fcStatusName to set
	 */
	public void setFcStatusName(String fcStatusName) {
		this.fcStatusName = fcStatusName;
	}

	/**
	 * Sets the fcSubName.
	 * @param fcSubName The fcSubName to set
	 */
	public void setFcSubName(String fcSubName) {
		this.fcSubName = fcSubName;
	}

}

