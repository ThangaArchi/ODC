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
import javax.naming.*;
import javax.naming.directory.*;
import org.apache.commons.logging.*;

/**
 * @author bjr
 */
public class MultisiteAuthenticator {
	public final static String VERSION = "1.3.1.1";
	private static Log log = LogFactory.getLog(MultisiteAuthenticator.class);
	private static HashMap authenticators = null;
	
	public static LDAPAuthenticator getAuthenticator(String site) throws MultisiteAuthenticationException {
		if (authenticators == null) {
			authenticators = new HashMap();
			Iterator sites = null;
			try {
				sites = Configuration.getSites();
			} catch (ConfigurationException x) {
				throw new MultisiteAuthenticationException("", x);
			}
			while (sites.hasNext()) {
				String key = sites.next().toString();
				LDAPAuthenticator authenticator = null;
				try {
					authenticator = new LDAPAuthenticator(Configuration.getConfiguration(key));
				} catch (ConfigurationException x) {
					throw new MultisiteAuthenticationException("", x);
				}	
				authenticators.put(key, authenticator);
			}
		}
		return (LDAPAuthenticator) authenticators.get(site);
	}
	
	public static void reconfigure() throws MultisiteAuthenticationException {
		try {
			DirContextPool.getInstance().close();
			Configuration.forceReload();
		} catch (Exception x) {
			MultisiteAuthenticationException mx = new MultisiteAuthenticationException("Error reloading configuration");
			mx.addSourceException(x);
			throw mx;
		}
		Configuration.forceReload();
	}
	
	public static AuthenticationResult[] authenticateToAllSites(String userId, String password) throws MultisiteAuthenticationException {
		if (log.isDebugEnabled()) log.debug("authenticateToAllSites("+userId+")");
		DirContext ctx = null;
		AuthenticationResult results[] = null;
		MultisiteAuthenticationException multisiteAuthenticationException = null;
		Iterator sites = null;
		try {
			results = new AuthenticationResult[Configuration.getNumberOfSites()];
			sites = Configuration.getSites();
		} catch (ConfigurationException x) {
			throw new MultisiteAuthenticationException("", x);
		}
		int i = 0;
		while (sites.hasNext()) {
			String site = sites.next().toString();
			if (log.isDebugEnabled()) log.debug("authenticating to "+site);
			results[i] = new AuthenticationResult();
			results[i].setSite(site);
			try {
				ctx = DirContextPool.getInstance().borrowDirContext(site);
				boolean authenticated = getAuthenticator(site).authenticate(ctx, userId, password);
				results[i].setAuthenticated(authenticated);
			} catch (Exception x) {
				results[i].setAuthenticated(false);
				results[i].setException(x);
				if (multisiteAuthenticationException == null) {
					multisiteAuthenticationException = new MultisiteAuthenticationException("");
				}
				multisiteAuthenticationException.addSourceException(x);
				log.info("Exception while authenticating "+userId+" to "+site, x);
			} finally {
				if (ctx != null) {
					try {
						DirContextPool.getInstance().returnDirContext(site, ctx);
					} catch (Exception dontcare) {
						log.info(dontcare);
					}
				}
			}
			i++;
		}
		return results;
	}
	
	public static boolean authenticate(String userId, String password) throws MultisiteAuthenticationException {
		if (log.isDebugEnabled()) log.debug("authenticate("+userId+")");
		DirContext ctx = null;
		boolean authenticated = false;
		boolean done = false;
		MultisiteAuthenticationException multisiteAuthenticationException = null;
		Iterator sites = null;
		try {
			sites = Configuration.getSites();
		} catch (ConfigurationException x) {
			throw new MultisiteAuthenticationException("", x);
		}
		while (!done && sites.hasNext()) {
			String site = sites.next().toString();
			if (log.isDebugEnabled()) log.debug("authenticating to "+site);
			try {
				ctx = DirContextPool.getInstance().borrowDirContext(site);
				authenticated = getAuthenticator(site).authenticate(ctx, userId, password);
				done = true;				
			} catch (Exception x) {
				if (multisiteAuthenticationException == null) {
					multisiteAuthenticationException = new MultisiteAuthenticationException("");
				}
				multisiteAuthenticationException.addSourceException(x);
				log.info("Exception while authenticating "+userId+" to "+site, x);
			} finally {
				if (ctx != null) {
					try {
						DirContextPool.getInstance().returnDirContext(site, ctx);
					} catch (Exception dontcare) {
						log.info(dontcare);
					}
				}
			}
		}
		// throw an exception if we got exceptions from each site
		if (multisiteAuthenticationException != null && !done) 
			throw multisiteAuthenticationException;
		return authenticated;
	}
	
}
