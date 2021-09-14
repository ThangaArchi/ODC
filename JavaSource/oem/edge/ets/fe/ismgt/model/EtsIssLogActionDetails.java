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
 * This class will give the details of the fields
 * that are going to be represented in Log actions report
 *
 */
public class EtsIssLogActionDetails {

	public static final String VERSION = "1.10";

	private String issueCommentsLog;

	/**
	 * Constructor for EtsIssLogActionDetails.
	 */
	public EtsIssLogActionDetails() {
		super();
	}

	/**
	 * Returns the issueCommentsLog.
	 * @return String
	 */
	public String getIssueCommentsLog() {
		return issueCommentsLog;
	}

	/**
	 * Sets the issueCommentsLog.
	 * @param issueCommentsLog The issueCommentsLog to set
	 */
	public void setIssueCommentsLog(String issueCommentsLog) {
		this.issueCommentsLog = issueCommentsLog;
	}

}

