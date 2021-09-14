package oem.edge.ets.fe.ismgt.model;

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
 * This class  the user details of the given user
 *
 */
public abstract class EtsIssUserDetailsAbsBean implements Serializable {

	public static final String VERSION = "1.44";
	private String userIrId;
	private String userEdgeId;
	private String userFullName;
	private String userEmail;
	private String userContPhone;
	private String userCustCompany;
	private String userFirstName;
	private String userLastName;
	private String userType;

	/**
	 * Constructor for EtsIssCurrentOwner.
	 */
	public EtsIssUserDetailsAbsBean() {
		super();
	}

	/**
	 * Returns the userContPhone.
	 * @return String
	 */
	public String getUserContPhone() {
		return userContPhone;
	}

	/**
	 * Returns the userEdgeId.
	 * @return String
	 */
	public String getUserEdgeId() {
		return userEdgeId;
	}

	/**
	 * Returns the userEmail.
	 * @return String
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * Returns the userFullName.
	 * @return String
	 */
	public String getUserFullName() {
		return userFullName;
	}

	/**
	 * Returns the userIrId.
	 * @return String
	 */
	public String getUserIrId() {
		return userIrId;
	}

	/**
	 * Sets the userContPhone.
	 * @param userContPhone The userContPhone to set
	 */
	public void setUserContPhone(String userContPhone) {
		this.userContPhone = userContPhone;
	}

	/**
	 * Sets the userEdgeId.
	 * @param userEdgeId The userEdgeId to set
	 */
	public void setUserEdgeId(String userEdgeId) {
		this.userEdgeId = userEdgeId;
	}

	/**
	 * Sets the userEmail.
	 * @param userEmail The userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * Sets the userFullName.
	 * @param userFullName The userFullName to set
	 */
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	/**
	 * Sets the userIrId.
	 * @param userIrId The userIrId to set
	 */
	public void setUserIrId(String userIrId) {
		this.userIrId = userIrId;
	}

	/**
	 * @return
	 */
	public String getUserCustCompany() {
		return userCustCompany;
	}

	/**
	 * @param string
	 */
	public void setUserCustCompany(String string) {
		userCustCompany = string;
	}

	/**
	 * @return
	 */
	public String getUserFirstName() {
		return userFirstName;
	}

	/**
	 * @return
	 */
	public String getUserLastName() {
		return userLastName;
	}

	/**
	 * @return
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param string
	 */
	public void setUserFirstName(String string) {
		userFirstName = string;
	}

	/**
	 * @param string
	 */
	public void setUserLastName(String string) {
		userLastName = string;
	}

	/**
	 * @param string
	 */
	public void setUserType(String string) {
		userType = string;
	}

}
