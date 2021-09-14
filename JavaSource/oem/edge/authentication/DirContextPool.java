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
import java.lang.reflect.*;
import javax.naming.*;
import javax.naming.directory.*;
import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;
import org.apache.commons.logging.*;
import oem.edge.util.*;

/**
 * @author bjr
 */
public class DirContextPool {
	public final static String VERSION = "1.6.1.1";
	private static Log log = LogFactory.getLog(DirContextPool.class);
	private static DirContextPool instance = null;
	private GenericKeyedObjectPool pool = null;
	
	protected DirContextPool() {
		if (log.isDebugEnabled()) log.debug("DirContextPool");
		pool = new GenericKeyedObjectPool(new PoolableDirContextFactory(this));
	}

	public static LDAPAuthenticatorConfiguration getConfig(Object handle) throws ConfigurationException {
		return Configuration.getConfiguration(handle.toString());
	}
	
	private static HashMap loadConfiguration() throws ConfigurationException {
		if (log.isDebugEnabled()) log.debug("loadConfiguration()");
		ArrayList sites = new ArrayList();
		HashMap cfgs = new HashMap();
		for (Iterator i=Configuration.getSites(); i.hasNext(); ) {
			String site = (String) i.next();
			LDAPAuthenticatorConfiguration cfg = getConfig(site); // new LDAPAuthenticatorConfiguration();
			cfgs.put(site, cfg);
		}
		return cfgs;
	}
	
	protected GenericKeyedObjectPool getPool() {
		return pool;
	}
	
	public static DirContextPool getInstance() throws DirContextPoolException {
		if (log.isDebugEnabled()) log.debug("getInstance()");
		if (instance == null) {
			try {
				HashMap configs = loadConfiguration();
				instance = new DirContextPool();
				try {
					GenericKeyedObjectPool.Config poolConfig = new GenericKeyedObjectPool.Config();
					ResourceBundle rb = ResourceBundle.getBundle(ConfigKeys.ResourceBundle);
					PropertiesLoader.loadConfigurationBean(rb, "pool", poolConfig);
					instance.getPool().setConfig(poolConfig);				
				} catch (MissingResourceException mre) {
				}
			} catch (ConfigurationException x) {
				throw new DirContextPoolException("Error loading configuration", x);
			}
		}
		return instance;
	}
	
	private String getHandle(int i) {
		return "";
	}
	
	public DirContext borrowDirContext(int handle) throws DirContextPoolException {
		if (log.isDebugEnabled()) log.debug("borrowDirContext("+handle+")");
		return borrowDirContext(getHandle(handle));
	}
	
	public DirContext borrowDirContext(Object handle) throws DirContextPoolException {
		if (log.isDebugEnabled()) log.debug("borrowDirContext("+handle+")");
		Object o = null;
		try {
			o = pool.borrowObject(handle);
		} catch (Exception x) {
			throw new DirContextPoolException("Exception borrowing context("+handle+")", x);
		}
		return (DirContext) o;
	}
	
	public void returnDirContext(int handle, DirContext ctx)  throws DirContextPoolException {
		if (log.isDebugEnabled()) log.debug("returnDirContext("+handle+", "+ctx+")");
		returnDirContext(getHandle(handle), ctx);
	}
	
	public void returnDirContext(Object handle, DirContext ctx) throws DirContextPoolException {
		if (log.isDebugEnabled()) log.debug("returnDirContext("+handle+", "+ctx+")");
		try {
			if (ctx == null) {
				log.warn("error: trying to return null object to "+handle+" pool");
			} else {
				LDAPAuthenticatorConfiguration cfg = getConfig(handle);
				ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, cfg.getEnv().get(Context.SECURITY_PRINCIPAL)); // cfg.getPrincipal());
				ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, cfg.getEnv().get(Context.SECURITY_CREDENTIALS)); // cfg.getPassword());
				pool.returnObject(handle, ctx);
			}
		} catch (Exception x) {
			throw new DirContextPoolException("Could not return DirContext ("+ctx+") to pool "+handle, x);
		}
	}
	
	public void close() throws DirContextPoolException {
		if (log.isDebugEnabled()) log.debug("close()");
		if (pool != null) {
			try {
				pool.clear();
				pool.close();
				instance = null;
			} catch (Exception x) {
				throw new DirContextPoolException("Error while closing pool", x);
			}
		}
	}
	
	public String toString() {
		String s = "DirContextPool{"+
			"maxActive="+pool.getMaxActive()+", "+
			"maxIdle="+pool.getMaxIdle()+", "+
			"maxWait="+pool.getMaxWait()+", "+
			"minEvictableIdleTimeMillis="+pool.getMinEvictableIdleTimeMillis()+", "+
			"numActive="+pool.getNumActive()+", "+
			"numIdle="+pool.getNumIdle()+", "+
			"timeBetweenEvictionRunMillis="+pool.getTimeBetweenEvictionRunsMillis()+" "+
			"}";
		return s;
	}
}
