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
import java.text.*;
import javax.naming.*;
import javax.naming.directory.*;
import org.apache.commons.logging.*;

/**
 * @author bjr
 */
public class LDAPAuthenticator {
	public final static String VERSION = "1.4";
	private static Log log = LogFactory.getLog(LDAPAuthenticator.class);
	private LDAPAuthenticatorConfiguration config = null;
	
	public LDAPAuthenticator(LDAPAuthenticatorConfiguration config) {
		this.config = config;
	}
	
	public boolean authenticate(DirContext dirContext, String userId, String password) throws NamingException {
		if (log.isDebugEnabled()) log.debug("authenticate("+dirContext+", "+userId+")");
		boolean rc = false;
		String dn = getDN(dirContext, userId);
		rc = (dn == null) ? false : bindAsUser(dirContext, dn, password);
		return rc;
	}
	
	protected boolean bindAsUser(DirContext ctx, String dn, String password) throws NamingException {
		if (log.isDebugEnabled()) log.debug("bindAsUser("+ctx+", "+dn+")");
		ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
		ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
		boolean rc = false;
		try {
			ctx.getAttributes("", null);
			rc = true;
		} catch (AuthenticationException x) {
			log.info(x);
		}
		return rc;
	}

	public String getDN(DirContext dirContext, String userId) throws NamingException {
		if (log.isDebugEnabled()) log.debug("getDN("+userId+")");
		SearchResult searchResult = search(dirContext, userId);
		String dn = (searchResult == null) ? null : getDN(dirContext, searchResult);
		return dn;
	}
	
	protected String getDN(DirContext ctx, SearchResult searchResult) throws NamingException {
		if (log.isDebugEnabled()) log.debug("getDN("+ctx+", "+searchResult+")");
		NameParser parser = ctx.getNameParser("");
		Name ctxName = parser.parse(ctx.getNameInNamespace());
		Name baseName = parser.parse(config.getUserBase());
		Name entryName = parser.parse(searchResult.getName());
		Name name = ctxName.addAll(baseName);
		name.addAll(entryName);
		String dn = name.toString();
		return dn;
	}

	public Object getAttribute(DirContext dirContext, String userId, String attributeId) throws NamingException {
		if (log.isDebugEnabled()) log.debug("getAttribute("+dirContext+", "+userId+", "+attributeId+")");
		SearchResult searchResult = search(dirContext, userId);
		Attribute attr = (searchResult == null) ? null : searchResult.getAttributes().get(attributeId);
		return (attr == null) ? null : attr.get();
	}

	private SearchResult search(DirContext dirContext, String userId) throws NamingException {
		if (log.isDebugEnabled()) log.debug("search("+userId+")");
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		ctls.setTimeLimit(config.getSearchTimeLimit());
		ctls.setCountLimit(1L);
		ctls.setReturningAttributes(new String[] {});
		if (log.isDebugEnabled()) log.debug("starting search with a time limit of "+config.getSearchTimeLimit());
		NamingEnumeration enum = dirContext.search(config.getUserBase(), config.getUserFilter(), new String[] {userId}, ctls);
		if (log.isDebugEnabled()) log.debug("search complete");
		if (enum == null || !enum.hasMore()) 
			return null;
		SearchResult searchResult = (SearchResult) enum.next();
		return searchResult;
	}
		
}
