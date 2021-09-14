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

package oem.edge.ets.fe.acmgt.actions;

/**
 * @author Suresh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddMembrUserDetails {
	
	private String enteredId;
	private String enteredId2;
	private String webId;
	private String emailId;
	private String previlage;
	private String company;
	private String address;
	private String countryCode;
	private String country;
	private String userName;
	private String userType;
	private String status="";
	private String accessLevel;
	private String job;
	private String msgrID;
	private String verifyId;
	private String selectedUser;
	private String cmpDifRsn;
	private String ctryCmpEmpty = "";
	
		
	
	/**
	 * @return Returns the ctryCmpEmpty.
	 */
	public String getCtryCmpEmpty() {
		return ctryCmpEmpty;
	}
	/**
	 * @param ctryCmpEmpty The ctryCmpEmpty to set.
	 */
	public void setCtryCmpEmpty(String ctryCmpEmpty) {
		this.ctryCmpEmpty = ctryCmpEmpty;
	}
	
	/**
	 * @return Returns the cmpDifRsn.
	 */
	public String getCmpDifRsn() {
		return cmpDifRsn;
	}
	/**
	 * @param cmpDifRsn The cmpDifRsn to set.
	 */
	public void setCmpDifRsn(String cmpDifRsn) {
		this.cmpDifRsn = cmpDifRsn;
	}
	/**
	 * @return Returns the selectedUser.
	 */
	public String getSelectedUser() {
		return selectedUser;
	}
	/**
	 * @param selectedUser The selectedUser to set.
	 */
	public void setSelectedUser(String selectedUser) {
		this.selectedUser = selectedUser;
	}
	/**
	 * @return Returns the enteredId2.
	 */
	public String getEnteredId2() {
		return enteredId2;
	}
	/**
	 * @param enteredId2 The enteredId2 to set.
	 */
	public void setEnteredId2(String enteredId2) {
		this.enteredId2 = enteredId2;
	}
	/**
	 * @return Returns the verifyId.
	 */
	public String getVerifyId() {
		return verifyId;
	}
	/**
	 * @param verifyId The verifyId to set.
	 */
	public void setVerifyId(String verifyId) {
		this.verifyId = verifyId;
	}
	private UserPrivileges m_udUserPrivilages = new UserPrivileges();
	private UserCountries m_udUserCountries = new UserCountries();

	public AddMembrUserDetails(){
		super();
	}
	
	
	/**
	 * @return Returns the accessLevel.
	 */
	public String getAccessLevel() {
		return accessLevel;
	}
	/**
	 * @param accessLevel The accessLevel to set.
	 */
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	/**
	 * @return Returns the job.
	 */
	public String getJob() {
		return job;
	}
	/**
	 * @param job The job to set.
	 */
	public void setJob(String job) {
		this.job = job;
	}
	/**
	 * @return Returns the msgrID.
	 */
	public String getMsgrID() {
		return msgrID;
	}
	/**
	 * @param msgrID The msgrID to set.
	 */
	public void setMsgrID(String msgrID) {
		this.msgrID = msgrID;
	}
	/**
	 * @return Returns the userType.
	 */
	public String getUserType() {
		return userType;
	}
	/**
	 * @param userType The userType to set.
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}
	/**
	 * @return Returns the enteredId.
	 */
	public String getEnteredId() {
		return enteredId;
	}
	/**
	 * @param enteredId The enteredId to set.
	 */
	public void setEnteredId(String enteredId) {
		this.enteredId = enteredId;
	}
	/**
	 * @return Returns the m_udUsrCountries.
	 */
	public UserCountries getUserCountries() {
		return m_udUserCountries;
	}
	/**
	 * @param usrCountries The m_udUsrCountries to set.
	 */
	public void setUserCountries(UserCountries userCountries) {
		m_udUserCountries = userCountries;
	}
	/**
	 * @return Returns the m_udUsrPrivilages.
	 */
	public UserPrivileges getUserPrivilages() {
		return m_udUserPrivilages;
	}
	/**
	 * @param usrPrivilages The m_udUsrPrivilages to set.
	 */
	public void setUserPrivilages(UserPrivileges userPrivilages) {
		m_udUserPrivilages = userPrivilages;
	}
	/**
	 * @return Returns the countryCode.
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode The countryCode to set.
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return Returns the address.
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address The address to set.
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * @return Returns the company.
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * @param company The company to set.
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	/**
	 * @return Returns the emailId.
	 */
	public String getEmailId() {
		return emailId;
	}
	/**
	 * @param emailId The emailId to set.
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	/**
	 * @return Returns the previlage.
	 */
	public String getPrevilage() {
		return previlage;
	}
	/**
	 * @param previlage The previlage to set.
	 */
	public void setPrevilage(String previlage) {
		this.previlage = previlage;
	}
	
	/**
	 * @return Returns the webId.
	 */
	public String getWebId() {
		return webId;
	}
	/**
	 * @param webId The webId to set.
	 */
	public void setWebId(String webId) {
		this.webId = webId;
	}
	
	/**
	 * @return Returns the country.
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country The country to set.
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
}
