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

import java.lang.reflect.*;
import javax.naming.*;
import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.commons.logging.*;
import junit.framework.*;

/**
 * @author bjr
 */
public class MultisiteAuthenticatorTest extends TestCase {
	public static Log log = LogFactory.getLog(MultisiteAuthenticatorTest.class);
	public static String userId = null;
	public static String password = null;
	public static int iterations = 3;
	
	public MultisiteAuthenticatorTest(String name) {
		super(name);
	}
	
	protected void tearDown() {
		log.debug("tearDown()");
	}
	
	public void testConfiguration() {
		log.debug("\n\ntestConfiguration()");
		int n = -1;
		try {
			n = Configuration.getNumberOfSites();
			for (Iterator iter=Configuration.getSites(); iter.hasNext(); ) {
				String site = (String) iter.next();
				Configuration.getConfiguration(site);
			}
		} catch (Exception x) {
			log.error(x);
			fail(x.toString());
		}
		assertEquals("Number of LDAP sites configured", 2, n);
	}
	
	public void testConnectivity() {
		log.debug("\n\ntestConnectivity()");
		ArrayList failedResults = new ArrayList();
		try {
			AuthenticationResult results[] = MultisiteAuthenticator.authenticateToAllSites(userId, password);
			for (int i=0; i<results.length; i++) {
				if (results[i].getException() != null)
					failedResults.add(results[i]);
			}
		} catch (Exception x) {
			log.error(x);
			fail(x.toString());
		}
		if (failedResults.size() > 0) {
			String mesg = "";
			for (Iterator iter = failedResults.iterator(); iter.hasNext(); ) {
				AuthenticationResult r = (AuthenticationResult) iter.next();
				mesg = mesg+"failed to connect to site "+r.getSite()+": "+r.getException();
				r.getException().printStackTrace();
			}
		}
		assertTrue(true);
	}
	
	public void testAuthentication() {
		log.debug("\n\ntestAuthentication()");
		for (int i=0; i<iterations; i++) {
			try {
				log.debug("authentication iteration "+i);
				boolean rc = MultisiteAuthenticator.authenticate(userId, password);
				assertTrue("authenticate("+userId+", "+password+")", rc);
			} catch (Exception x) {
				log.error(x);
				fail("authenticate("+userId+"): "+x);
			}
		}		
	}
	
	public void testCacheTimeout() {
		log.debug("\n\ntestCacheTimeout()");
		try {
			log.debug(DirContextPool.getInstance().toString());
		} catch (Exception x) {
			log.error(x);
		}
		try {
			boolean rc = MultisiteAuthenticator.authenticate(userId, password);
		} catch (Exception x) {
			log.error(x);
			fail("cache timeout");
		}
		try {
			Thread.sleep(180000+60000+30000);
		} catch (Exception donecare) {
		}
		try {
			boolean rc = MultisiteAuthenticator.authenticate(userId, password);
		} catch (Exception x) {
			log.error(x);
			fail("cache timeout");
		}
		assertTrue("cache timeout", true);
	}
	
	public void testConfigurationChange() {
		log.debug("\n\ntestConfigurationChange()");
		boolean rc = true;
		try {
			log.info("1. authenticate against "+ConfigKeys.PropertiesFile);
			rc = MultisiteAuthenticator.authenticate(userId, password);
			log.info("1. authentication rc="+rc);
			Configuration.addConfigurationChangeListener(new ConfigurationChangeListener() {
				public void configurationChange(ConfigurationChangeEvent event) {
					try {
						MultisiteAuthenticator.reconfigure();
					} catch (Exception x) {
						log.error(x);
					}
				}
			});
			Configuration.setPollingPeriod(5*1000);
			// poll the file once
			try {
				Thread.sleep(6*1000);
			} catch (Exception dontcare) {
			}
			// replace the configuration file with a bad one
			File originalFile = Configuration.getConfigurationFile();
			String originalFilename = originalFile.getCanonicalPath();
			originalFile.renameTo(new File(originalFilename+"-bak"));
			String path = originalFilename.substring(0, originalFilename.indexOf(ConfigKeys.PropertiesFile));
			File replacementFile = new File(path+"BadLDAPAuthenticator.properties");
			replacementFile.renameTo(new File(originalFilename));
			// System.out.println("name->["+originalFile.getName()+"]");
			// System.out.println("absolute->["+originalFile.getAbsolutePath()+"]");
			// System.out.println("Canonical->["+originalFile.getCanonicalPath()+"]");
			// make sure a 2nd poll occurs
			try {
				Thread.sleep(6*1000);
			} catch (Exception dontcare) {
			}
			try {
				// this should fail
				log.info("2. authenticate against "+replacementFile);
				log.info("wiTest config "+Configuration.getConfiguration("wiTest"));
				rc = MultisiteAuthenticator.authenticate(userId, password);
				log.info("2. authenticate against "+replacementFile+": "+rc);
			} catch (Exception x) {
				rc = false;
				log.info("2. authenticate against "+replacementFile, x);
			}
			// restore the original files
			(new File(originalFilename)).renameTo(new File(path+"BadLDAPAuthenticator.properties"));
			(new File(originalFilename+"-bak")).renameTo(new File(originalFilename));
		} catch (Exception x) {
			log.error(x);
		}
		assertFalse(rc);
	}
	
	public static Test suite() {
		log.debug("\n\nsuite()");
		//return new TestSuite(Test.class);
		TestSuite suite= new TestSuite("MultisiteAuthentictor Tests");
		//suite.addTest(new MultisiteAuthenticatorTest("testConfiguration"));
		//suite.addTest(new MultisiteAuthenticatorTest("testConnectivity"));
		suite.addTest(new MultisiteAuthenticatorTest("testAuthentication"));
		//suite.addTest(new MultisiteAuthenticatorTest("testCacheTimeout"));
		//suite.addTest(new MultisiteAuthenticatorTest("testConfigurationChange"));
	    return suite;
	}
	
	public static void main(String[] args) {
		userId = args[0];
		password = args[1];
		junit.textui.TestRunner.run(suite());
		System.exit(0);
	}
}
