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


package oem.edge.ets.fe.acmgt.model;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UserInviteStatusModel {
	
	public static final String VERSION = "1.8";
	
	private String userId;
	private String wrkSpcId;
	private String wrkSpcType;
	private String inviteStatus;
	private int roleId;
	private String roleName;
	private String requestorId;
	private String requestorName;
	private String userCompany;
	private String userCountryCode;
	private String userCountryName;
	private String lastUserId;
	
	

	/**
	 * 
	 */
	public UserInviteStatusModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @return
	 */
	public String getInviteStatus() {
		return inviteStatus;
	}

	/**
	 * @return
	 */
	public String getLastUserId() {
		return lastUserId;
	}

	/**
	 * @return
	 */
	public String getWrkSpcId() {
		return wrkSpcId;
	}

	

	/**
	 * @param string
	 */
	public void setInviteStatus(String string) {
		inviteStatus = string;
	}

	/**
	 * @param string
	 */
	public void setLastUserId(String string) {
		lastUserId = string;
	}

	/**
	 * @param string
	 */
	public void setWrkSpcId(String string) {
		wrkSpcId = string;
	}

	/**
	 * @return
	 */
	public String getWrkSpcType() {
		return wrkSpcType;
	}

	/**
	 * @param string
	 */
	public void setWrkSpcType(String string) {
		wrkSpcType = string;
	}

	/**
	 * @return
	 */
	public String getRequestorId() {
		return requestorId;
	}

	/**
	 * @param string
	 */
	public void setRequestorId(String string) {
		requestorId = string;
	}

	

	/**
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param string
	 */
	public void setUserId(String string) {
		userId = string;
	}

	/**
	 * @return
	 */
	public String getUserCompany() {
		return userCompany;
	}

	/**
	 * @return
	 */
	public String getUserCountryCode() {
		return userCountryCode;
	}

	/**
	 * @param string
	 */
	public void setUserCompany(String string) {
		userCompany = string;
	}

	/**
	 * @param string
	 */
	public void setUserCountryCode(String string) {
		userCountryCode = string;
	}

	/**
	 * @return
	 */
	public int getRoleId() {
		return roleId;
	}

	/**
	 * @param i
	 */
	public void setRoleId(int i) {
		roleId = i;
	}

	/**
	 * @return
	 */
	public String getRequestorName() {
		return requestorName;
	}

	/**
	 * @return
	 */
	public String getRoleName() {
		return roleName;
	}

	

	/**
	 * @param string
	 */
	public void setRequestorName(String string) {
		requestorName = string;
	}

	/**
	 * @param string
	 */
	public void setRoleName(String string) {
		roleName = string;
	}

	

	/**
	 * @return
	 */
	public String getUserCountryName() {
		return userCountryName;
	}

	/**
	 * @param string
	 */
	public void setUserCountryName(String string) {
		userCountryName = string;
	}

}
