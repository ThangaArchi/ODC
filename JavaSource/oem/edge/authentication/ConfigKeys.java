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

import java.text.*;

/**
 * @author bjr
 */
public class ConfigKeys {
	public final static String VERSION = "1.4";
	public static final String ResourceBundle = "LDAPAuthenticator";
	public static final String PropertiesFile = ResourceBundle+".properties";
	public static final String SiteFormat = "site.{0}";
	public static final String UserBaseFormat = "{0}.userBase";
	public static final String UserFilterFormat = "{0}.userFilter";
	public static final String UserGroupFilterFormat = "{0}.userGroupFilter";
	public static final String SearchTimeLimitFilterFormat = "{0}.searchTimeLimit";

	public static String getSiteKey(int i) {
		return MessageFormat.format(SiteFormat, new Object[] { new Integer(i) });
	}
	
	public static String getUserFilterKey(String site) {
		return MessageFormat.format(UserFilterFormat, new Object[] { site });
	}	
	
	public static String getUserGroupFilterKey(String site) {
		return MessageFormat.format(UserGroupFilterFormat, new Object[] { site });
	}
	
	public static String getUserBaseKey(String site) {
		return MessageFormat.format(UserBaseFormat, new Object[] { site });
	}
	
	public static String getSearchTimeLimitKey(String site) {
		return MessageFormat.format(SearchTimeLimitFilterFormat, new Object[] { site });
	}
}
