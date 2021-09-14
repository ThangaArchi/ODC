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

package oem.edge.ets.fe.aic.common.helper;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;

import oem.edge.ets.fe.aic.common.exception.AICServiceLocatorException;
import oem.edge.ets.fe.common.EtsLogger;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICServiceLocator {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger = EtsLogger.getLogger(AICServiceLocator.class);
	private static AICServiceLocator objAICServiceLocator = null;
	private static UserTransaction userTransactionCache = null;

	/*Enumerating the different services available from the Service Locator*/
	public static final int USER_TRANSACTION = 0;

	/*The JNDI Names used to lookup a service*/
	private static final String USER_TRANSACTION_JDINAME =
		"java:comp/UserTransaction";

	/*Private Constructor for the AICServiceLocator*/
	private AICServiceLocator() {

	}

	/*
	 * The AICServiceLocator is implemented as a Singleton.  The getInstance()
	 * method will return the static reference to the AICServiceLocator stored
	 * inside of the AICServiceLocator Class.
	 */
	public static synchronized AICServiceLocator getInstance() {
		if (objAICServiceLocator == null) {
			objAICServiceLocator = new AICServiceLocator();
		}
		return objAICServiceLocator;
	}

	static private String getServiceName(int pServiceId)
		throws AICServiceLocatorException {
		String serviceName = null;

		switch (pServiceId) {
			case USER_TRANSACTION :
				serviceName = USER_TRANSACTION_JDINAME;
				break;
			default :
				throw new AICServiceLocatorException(
					"Unable to locate the service requested in "
						+ "AICServiceLocator.getServiceName() method.  ");
		}
		return serviceName;
	}

	public UserTransaction getUserTransaction(int pServiceId)
		throws AICServiceLocatorException {
		if (logger.isInfoEnabled()) {
					logger.info("-> getUserTransaction");
		}
		/*Getting the JNDI Service Name*/
		String serviceName = getServiceName(pServiceId);
		if(logger.isDebugEnabled())
		{
			logger.debug("Service Name is ="+serviceName);
		}
		
		try {

			if (userTransactionCache == null) {
				InitialContext ctx = new InitialContext();
				Object txObj = ctx.lookup(serviceName);
				if(logger.isDebugEnabled())
				{
					logger.debug("txObj ="+txObj);
				}
				userTransactionCache = (UserTransaction) txObj;
				if(logger.isDebugEnabled())
				{
					logger.debug("userTransactionCache ="+userTransactionCache);
				}
			}
		} catch (NamingException e) {
			throw new AICServiceLocatorException(
				"A JNDI Naming exception has occurred in AICServiceLocator.getUserTransaction()"
					+ e);
		} catch (Exception e) {
			throw new AICServiceLocatorException(
				"An exception has occurred in AICServiceLocator.getUserTransaction()"
					+ e);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Before <- getUserTransaction ="+userTransactionCache);
		}
				
		if (logger.isInfoEnabled()) {
			logger.info("<- getUserTransaction");
		}
		return userTransactionCache;
	}
}
