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

import java.io.*;
import java.util.*;
import java.security.*;
import java.lang.reflect.*;
import java.net.*;
import javax.naming.*;
import org.apache.commons.logging.*;

/**
 * @author bjr
 * Example properties file:
site.0=wiTest
site.1=irTest

wiTest.java.naming.factory.initial=com.sun.jndi.ldap.LdapCtxFactory
wiTest.java.naming.provider.url=ldap://v25ldrs107.mkm.can.ibm.com:389/l=world
wiTest.java.naming.authentication=simple
wiTest.java.naming.security.principal=uid=MDEDGE,cn=applications
wiTest.java.naming.security.credentials=d1sney
wiTest.java.naming.security.protocol=ssl
wiTest.userBase=
wiTest.userFilter=(&(uid={0})(objectclass=irperson))
wiTest.userGroupFilter=(&(cn={0})(objectclass=accessrole))

irTest.java.naming.factory.initial=com.sun.jndi.ldap.LdapCtxFactory
irTest.java.naming.provider.url=ldap://solma.sanjose.ibm.com:389/l=world
irTest.java.naming.authentication=simple
irTest.java.naming.security.principal=uid=MDEDGE,cn=applications
irTest.java.naming.security.credentials=d1sney
irTest.java.naming.security.protocol=ssl
irTest.userBase=
irTest.userFilter=(&(uid={0})(objectclass=irperson))
irTest.userGroupFilter=(&(cn={0})(objectclass=accessrole))
 */
public class Configuration {
	public final static String VERSION = "1.5";
	private final static int PUBLIC_STATIC_FINAL = Modifier.FINAL|Modifier.PUBLIC|Modifier.STATIC;
	private final static long DEFAULT_POLLING_PERIOD = 3*60*1000; // 3 mins
	private static Log log = LogFactory.getLog(Configuration.class);
	private static Collection sites = null;
	private static HashMap cfgs = null;
	private static ConfigurationFileWatchdog watchdog = null;
	private static Thread watchdogThread = null;
	
	public static Collection getContextKeys() throws IllegalAccessException {
		ArrayList keys = new ArrayList();
		Field fields[] = Context.class.getFields();
		for (int i=0; i<fields.length; i++) {
			int m = fields[i].getModifiers();
			if ((m & PUBLIC_STATIC_FINAL) == PUBLIC_STATIC_FINAL) {
				keys.add(fields[i].get(null));
			}
		}
		return keys;
	}

	private static ConfigurationFileWatchdog getWatchdog() throws IOException {
		if (watchdog == null) {
			ClassLoader contextClassLoader = (ClassLoader) AccessController.doPrivileged(
				new PrivilegedAction() {
					public Object run() {
						return getContextClassLoader();
                	}
            	});
			URL url = getResource(contextClassLoader, ConfigKeys.PropertiesFile);
			File configFile = new File(URLDecoder.decode(url.getFile()));			
			boolean b = configFile.exists();
			watchdog = new ConfigurationFileWatchdog(configFile, DEFAULT_POLLING_PERIOD);
			watchdogThread = new Thread(watchdog, "ConfigurationFileWatchdog");
			watchdogThread.start();
		}
		return watchdog;
	}
	
	public static void setPollingPeriod(long t) throws ConfigurationException {
		try {
			getWatchdog().setPollingPeriod(t);
		} catch (Exception x) {
			throw new ConfigurationException("", x);
		}
	}
	
	public static long getPollingPeriod() throws ConfigurationException {
		long t = -1L;
		try {
			t = getWatchdog().getPollingPeriod();
		} catch (Exception x) {
			throw new ConfigurationException("", x);
		}
		return t;
	}
	
	public static void addConfigurationChangeListener(ConfigurationChangeListener listener) throws ConfigurationException {
		try {
			getWatchdog().addConfigurationChangeListener(listener);
		} catch (Exception x) {
			throw new ConfigurationException("", x);
		}
	}
	
	public static void removeConfigurationChangeListener(ConfigurationChangeListener listener) {
		try {
			getWatchdog().removeConfigurationChangeListener(listener);
		} catch (Exception x) {
			log.info(x);
		}
	}
	
	public static void forceReload() {
		if (log.isDebugEnabled()) log.debug("forceReload()");
		sites = null;
		cfgs = null;
	}

	// this exists to support unit testing	
	public static File getConfigurationFile() {
		ClassLoader contextClassLoader = (ClassLoader) AccessController.doPrivileged(
				new PrivilegedAction() {
					public Object run() {
						return getContextClassLoader();
                	}
            	});
		URL url = getResource(contextClassLoader, ConfigKeys.PropertiesFile);
		File configFile = new File(URLDecoder.decode(url.getFile()));			
		return configFile;
	}
	
	private static URL getResource(final ClassLoader loader, final String name) {
		if (log.isDebugEnabled()) log.debug("getResourceAsStream("+loader+", "+name+")");
        return (URL) AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    if (loader != null) {
                        return loader.getResource(name);
                    } else {
                        return ClassLoader.getSystemResource(name);
                    }
                }
            });
	}
	
	private static InputStream getResourceAsStream(final ClassLoader loader, final String name) {
		if (log.isDebugEnabled()) log.debug("getResourceAsStream("+loader+", "+name+")");
        return (InputStream)AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    if (loader != null) {
                        return loader.getResourceAsStream(name);
                    } else {
                        return ClassLoader.getSystemResourceAsStream(name);
                    }
                }
            });
    }

	protected static ClassLoader getContextClassLoader() throws SecurityException {
		if (log.isDebugEnabled()) log.debug("getContextClassLoader()");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader;
    }

	private static Properties loadProperties() throws ConfigurationException {
		if (log.isDebugEnabled()) log.debug("loadProperties()");
		// Identify the class loader we will be using
        ClassLoader contextClassLoader = (ClassLoader) AccessController.doPrivileged(
			new PrivilegedAction() {
				public Object run() {
					return getContextClassLoader();
                }
            });
        Properties props = null;
		InputStream stream = null;
		try {
			stream = getResourceAsStream(contextClassLoader, ConfigKeys.PropertiesFile);
        	if (stream != null) {
        		props = new Properties();
                props.load(stream);
            } else {
            	log.error("Error loading "+ConfigKeys.PropertiesFile+" stream is null");
        	}
		} catch (IOException x) {
			log.error("Error loading "+ConfigKeys.PropertiesFile, x);
			throw new ConfigurationException("", x);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (Exception dontcare) {
			}
		}
		return props;
	}
	
	private static void loadConfiguration() throws ConfigurationException {
		if (sites != null && cfgs != null) 
			return;
		if (log.isDebugEnabled()) log.debug("loadConfiguration()");
		sites = new ArrayList();
		cfgs = new HashMap();
		String site = "";
		Object key = null;
		try { 
			Properties rb = loadProperties();
			for (int i=0; site != null; i++) {
				try {
					site = (String) rb.get("site."+i);
					if (site != null)
						sites.add(site);
				} catch (MissingResourceException x) {
					site = null;
				}
			}
			if (sites.size() == 0) {
				log.error("Error: no sites specified in "+ConfigKeys.ResourceBundle);
			}
			for (Iterator i=sites.iterator(); i.hasNext(); ) {
				site = (String) i.next();
				LDAPAuthenticatorConfiguration cfg = new LDAPAuthenticatorConfiguration();
				try {
					cfg.setUserBase((String) rb.get(ConfigKeys.getUserBaseKey(site)));
				} catch (MissingResourceException x) {
					log.warn("Resource "+ConfigKeys.getUserBaseKey(site)+" could not be loaded, using the default.");
				}
				cfg.setUserFilter((String) rb.get(ConfigKeys.getUserFilterKey(site)));
				cfg.setGroupFilter((String) rb.get(ConfigKeys.getUserGroupFilterKey(site)));
				String t = null;
				try {
				  t = (String) rb.get(ConfigKeys.getSearchTimeLimitKey(site));
				  int timeLimit = Integer.parseInt(t);
				  cfg.setSearchTimeLimit(timeLimit);
				} catch (MissingResourceException x) {
				  log.warn("Resource "+ConfigKeys.getSearchTimeLimitKey(site)+" could not be loaded, using thre default: "+cfg.getSearchTimeLimit());
				} catch (NumberFormatException x) {
				  log.warn("Resource "+ConfigKeys.getSearchTimeLimitKey(site)+" could not be loaded - invalid number format ("+t+")");
				}
				// load the properties that match the fields of Context
				// i.e. java.naming.factory.initial will be of the format
				// SITE.java.naming.factory.initial
				Hashtable ctxEnv = new Hashtable();
				Collection contextKeys = getContextKeys();
				for (Iterator iter=contextKeys.iterator(); iter.hasNext(); ) {
					key = iter.next();
					try {
						String val = (String) rb.get(site+"."+key);
						if (key != null && val != null)
							ctxEnv.put(key, val);
					} catch (MissingResourceException dontcare) {
						log.warn("Missing property: "+site+"."+key);
					}					
				}
				// search for any ldap specific properties
				for (Iterator iter=rb.keySet().iterator(); iter.hasNext(); ) {
					String cfgKey = iter.next().toString();
					String val = rb.getProperty(cfgKey);
					if (cfgKey.startsWith(site+".java.naming.ldap.")) {
						String jndiKey = cfgKey.substring((site+".").length());
						ctxEnv.put(jndiKey, val);
					}
				}
				cfg.setEnv(ctxEnv);
				//
				cfgs.put(site, cfg);
			}
		} catch (IllegalAccessException x) {
			log.warn("Exception while loading configuration: site="+site+", key="+key, x);
			throw new ConfigurationException("Error loading configuration", x);
		} 
	}
	
	public static Iterator getSites() throws ConfigurationException {
		loadConfiguration();
		Iterator iter = null;
		if (sites != null) {
			iter = sites.iterator();
		} else {
			iter = new Iterator() {
				public boolean hasNext() { return false; }
				public Object next() { return null; }
				public void remove() { }
			};
		}
		return iter;
	}
	
	public static int getNumberOfSites() throws ConfigurationException {
		loadConfiguration();
		int n = 0;
		if (sites != null) 
			n = sites.size();
		return n;
	}
	
	public static LDAPAuthenticatorConfiguration getConfiguration(String site) throws ConfigurationException {
		loadConfiguration();
		return (LDAPAuthenticatorConfiguration) cfgs.get(site);
	}
}
