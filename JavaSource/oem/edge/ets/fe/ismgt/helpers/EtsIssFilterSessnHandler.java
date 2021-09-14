package oem.edge.ets.fe.ismgt.helpers;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
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
 * This class provide the basic session utilities required for ETS Issue Filter
 * and other classes involved for ETS Issue Filter
 * provides get/set methods for session attributes
 * 
 */

public class EtsIssFilterSessnHandler  {

	public static final String VERSION = "1.48";
	private HttpSession session = null;

	/**
	 * AmtHfSessnHandler constructor comment.
	 */

	public EtsIssFilterSessnHandler(HttpSession session) {
		super();
		this.session = session;

	}

	/**
	 * 
	 * set the session obj name/value
	 */

	public void setSessionObjValue(String name, Object value) {

		if (session != null) {
			session.setAttribute(name, value);
		}

	}

	/**
	 * 
	 * set the session string name/value
	 */

	public void setSessionStrValue(String name, String value) {

		if (session != null) {
			session.setAttribute(name, value);
		}

	}

	/**
	 * 
	 * get the session object value for the name
	 */

	public Object getSessionObjValue(String name) {

		if (session != null) {
			return session.getAttribute(name);
		}
		return null;
	}

	/**
	 * 
	 * get the session String value for the name
	 */

	public String getSessionStrValue(String name) {

		if (session != null) {
			return (String) session.getAttribute(name);
		}

		return null;
	}

}

