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
package oem.edge.authentication;

/**
 * @author bjr
 */
public class AuthenticationResult {
	private final static String VERSION = "1.3";
	private boolean authenticated = false;
	private String site = null;
	private Throwable exception = null;
	
	
	/**
	 * Returns the authenticated.
	 * @return boolean
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * Returns the exception.
	 * @return Throwable
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Returns the site.
	 * @return String
	 */
	public String getSite() {
		return site;
	}

	/**
	 * Sets the authenticated.
	 * @param authenticated The authenticated to set
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	/**
	 * Sets the exception.
	 * @param exception The exception to set
	 */
	public void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * Sets the site.
	 * @param site The site to set
	 */
	public void setSite(String site) {
		this.site = site;
	}

}
