/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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
package oem.edge.ets.fe;

import oem.edge.ets.fe.ETSDetailedObj;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AICUserDecafRole extends ETSDetailedObj{

	private int roleId;
	private String projectId;
	private String entitlementName;
	private String datatype;
	private String datatypeName;
	private String decafProfileName;
	private String decafProfileId;
	private String userid;
	
	
	/**
	 * @return Returns the datatypeName.
	 */
	public String getDatatypeName() {
		return datatypeName;
	}
	/**
	 * @param datatypeName The datatypeName to set.
	 */
	public void setDatatypeName(String datatypeName) {
		this.datatypeName = datatypeName;
	}
	/**
	 * @return Returns the decafProfile.
	 */
	public String getDecafProfileName() {
		return decafProfileName;
	}
	/**
	 * @param decafProfile The decafProfile to set.
	 */
	public void setDecafProfileName(String decafProfileName) {
		this.decafProfileName = decafProfileName;
	}
	/**
	 * @return Returns the entitlementName.
	 */
	public String getEntitlementName() {
		return entitlementName;
	}
	/**
	 * @param entitlementName The entitlementName to set.
	 */
	public void setEntitlementName(String entitlementName) {
		this.entitlementName = entitlementName;
	}
	/**
	 * @return Returns the projectId.
	 */
	public String getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId The projectId to set.
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return Returns the roleId.
	 */
	public int getRoleId() {
		return roleId;
	}
	/**
	 * @param roleId The roleId to set.
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
    
	public void setUserId(String id){
    	this.userid = id;
    }
    
    public String getUserId(){
    	return userid;
    }

	/**
	 * @return Returns the decafProfileId.
	 */
	public String getDecafProfileId() {
		return decafProfileId;
	}
	/**
	 * @param decafProfileId The decafProfileId to set.
	 */
	public void setDecafProfileId(String decafProfileId) {
		this.decafProfileId = decafProfileId;
	}
}
