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
public class WrkSpcTeamActionsInpModel {
	
	public static final String VERSION = "1.8";
	
	private String wrkSpcId;
	private String wrkSpcType;
	private String userId;
	private int roleId;
	private String requestorId;
	private String lastUserId;
	private String userAssignCompany;
	private String userAssignCountry;
	
	///
	private String userStatus;

	/**
	 * 
	 */
	public WrkSpcTeamActionsInpModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public String getUserId() {
		return userId;
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
	public void setUserId(String string) {
		userId = string;
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
	public String getUserAssignCompany() {
		return userAssignCompany;
	}

	/**
	 * @return
	 */
	public String getUserAssignCountry() {
		return userAssignCountry;
	}

	/**
	 * @param string
	 */
	public void setUserAssignCompany(String string) {
		userAssignCompany = string;
	}

	/**
	 * @param string
	 */
	public void setUserAssignCountry(String string) {
		userAssignCountry = string;
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
	public String getUserStatus() {
		return userStatus;
	}

	/**
	 * @param string
	 */
	public void setUserStatus(String string) {
		userStatus = string;
	}

}
