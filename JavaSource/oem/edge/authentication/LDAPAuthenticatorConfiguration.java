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

import java.util.*;
import org.apache.commons.logging.*;

/**
 * @author bjr
 */
public class LDAPAuthenticatorConfiguration {
	public final static String VERSION = "1.4";
	private Hashtable env = new Hashtable();
	private String userBase = "";
	private String userFilter = null; // "(&(uid={0})(objectclass=irperson))";
	private String groupFilter = null; // "(&(cn={0})(|(objectclass=groupOfNames)(objectclass=irperson)))";
	private int searchTimeLimit = 60*1000; // 60 sec

	public int getSearchTimeLimit() {
	  return searchTimeLimit;
	}

	public void setSearchTimeLimit(int timeLimit) {
	  searchTimeLimit = timeLimit;
	}
	
	/**
	 * Returns the userFilter.
	 * @return String
	 */
	public String getUserFilter() {
		return userFilter;
	}

	/**
	 * Sets the userFilter.
	 * @param userFilter The userFilter to set
	 */
	public void setUserFilter(String userFilter) {
		this.userFilter = userFilter;
	}

	/**
	 * Returns the groupFilter.
	 * @return String
	 */
	public String getGroupFilter() {
		return groupFilter;
	}

	/**
	 * Sets the groupFilter.
	 * @param groupFilter The groupFilter to set
	 */
	public void setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
	}
	
	/**
	 * Returns the userBase.
	 * @return String
	 */
	public String getUserBase() {
		return userBase;
	}

	/**
	 * Sets the userBase.
	 * @param userBase The userBase to set
	 */
	public void setUserBase(String userBase) {
		this.userBase = userBase;
	}
	
	public Hashtable getEnv() {
		return env;
	}
	
	public void setEnv(Hashtable env) {
		this.env = env;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("LDAPAuthenticatorConfiguration{ jndi env {");
		for (Enumeration enum=env.keys(); enum.hasMoreElements(); ) {
			Object key = enum.nextElement();
			Object val = env.get(key);
			buf.append(key+"="+val);
		}
		buf.append("}, ")
			.append(userBase)
			.append(", ")
			.append(userFilter)
			.append(", ")
			.append(groupFilter)
			.append(", ")
			.append(searchTimeLimit)
			.append("}");
		return buf.toString();
	}
}
