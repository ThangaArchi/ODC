package oem.edge.ets_pmo.datastore.resource;
import java.util.Vector;
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

/**
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Resource {
	private static String CLASS_VERSION = "4.5.1";
	private String ResourceID;
	private String element_name;
	private String security_id;
	private String security_id_Rank;
	private String logon_name;
	private String company_name;
	private String email = null;
	private String phone = null;
	
	private static Vector vResourceListForThisProject;
	
	public Resource(){
		ResourceID		 = null;
		element_name	 = null;
		security_id		 = null;
		security_id_Rank = null;
		logon_name		 = null;
		company_name	 = null;
	}
	/**
	 * Returns the company_name.
	 * @return String
	 */
	public String getCompany_name() {
		return company_name;
	}

	/**
	 * Returns the element_name.
	 * @return String
	 */
	public String getElement_name() {
		return element_name;
	}

	/**
	 * Returns the logon_name.
	 * @return String
	 */
	public String getLogon_name() {
		return logon_name;
	}

	/**
	 * Returns the resourceID.
	 * @return int
	 */
	public String getResourceID() {
		return ResourceID;
	}

	/**
	 * Returns the security_id.
	 * @return String
	 */
	public String getSecurity_id() {
		return security_id;
	}

	/**
	 * Returns the security_id_Rank.
	 * @return String
	 */
	public String getSecurity_id_Rank() {
		return security_id_Rank;
	}

	/**
	 * Sets the company_name.
	 * @param company_name The company_name to set
	 */
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	/**
	 * Sets the element_name.
	 * @param element_name The element_name to set
	 */
	public void setElement_name(String element_name) {
		this.element_name = element_name;
	}

	/**
	 * Sets the logon_name.
	 * @param logon_name The logon_name to set
	 */
	public void setLogon_name(String logon_name) {
		this.logon_name = logon_name;
	}

	/**
	 * Sets the resourceID.
	 * @param resourceID The resourceID to set
	 */
	public void setResourceID(String resourceID) {
		ResourceID = resourceID;
	}

	/**
	 * Sets the security_id.
	 * @param security_id The security_id to set
	 */
	public void setSecurity_id(String security_id) {
		this.security_id = security_id;
	}

	/**
	 * Sets the security_id_Rank.
	 * @param security_id_Rank The security_id_Rank to set
	 */
	public void setSecurity_id_Rank(String security_id_Rank) {
		this.security_id_Rank = security_id_Rank;
	}

	/**
	 * Returns the email.
	 * @return String
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 * @param email The email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the phone.
	 * @return String
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Sets the phone.
	 * @param phone The phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
public String toString(){
			String str = "ResourceID : " + ResourceID + "\n" + 
						"element_name: " + element_name +"\n" + 
						"security_id : " + security_id +"\n" + 
						"security_id_Rank : " + security_id_Rank +"\n" + 
						"logon_name : " + logon_name +"\n" + 
						"company_name :" + 	 company_name;
						
			return str;
	
}
	/**
	 * Returns the resourceListForThisProject.
	 * @return Vector
	 */
	public static Vector getResourceListForThisProject() {
		return vResourceListForThisProject;
	}
public Resource retrieveResourceFromResourceListForThisProject(int index) throws IndexOutOfBoundsException{
	Resource pro = null;
	if(this.vResourceListForThisProject != null &&
		!this.vResourceListForThisProject.isEmpty()){
			if(index >= vResourceListForThisProject.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vResourceListForThisProject");
			}
	pro = (Resource)vResourceListForThisProject.get(index);
	}
	return pro;
		
}

	public static void populateVResourceListForThisProject(Resource pro) {
		
		if(vResourceListForThisProject == null){
			vResourceListForThisProject = new Vector();
		}
		if(vResourceListForThisProject != null){
			boolean recordalreadyPresentInThisVector = false;
			for(int i = 0 ; i< vResourceListForThisProject.size(); i ++){
				if(pro.getElement_name().equalsIgnoreCase(((Resource)vResourceListForThisProject.get(i)).getElement_name())){
						recordalreadyPresentInThisVector = true;
						break;
					}
			}
		if(recordalreadyPresentInThisVector ==  false){
				vResourceListForThisProject.add(pro);
			}
		}
	}
	
public int RetrievePopulationOfResourceListForThisProject() {
		if(vResourceListForThisProject == null)
			return -1;
		return vResourceListForThisProject.size();
	}
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
