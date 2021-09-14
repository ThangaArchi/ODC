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
package oem.edge.ets_pmo.domain;

/**
 * @author shingte
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResObject extends PmoObject {
	private String security_id;
	private String logon_name;
	private String company_name;
	/**
	 * @return Returns the logon_name.
	 */
	public String getLogon_name() {
		return logon_name;
	}
	/**
	 * @param logon_name The logon_name to set.
	 */
	public void setLogon_name(String logon_name) {
		this.logon_name = logon_name;
	}
	/**
	 * @return Returns the security_id.
	 */
	public String getSecurity_id() {
		return security_id;
	}
	/**
	 * @param security_id The security_id to set.
	 */
	public void setSecurity_id(String security_id) {
		this.security_id = security_id;
	}
	
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
}
