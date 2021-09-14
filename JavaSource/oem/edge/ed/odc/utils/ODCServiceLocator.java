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

package oem.edge.ed.odc.utils;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;


/**
 * @author tkandhas@in.ibm.com
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ODCServiceLocator {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger = ODCLogger.getLogger(ODCServiceLocator.class);
	private static ODCServiceLocator objODCServiceLocator = null;
	private static UserTransaction userTransactionCache = null;

	/*Enumerating the different services available from the Service Locator*/
	public static final int USER_TRANSACTION = 0;

	/*The JNDI Names used to lookup a service*/
	private static final String USER_TRANSACTION_JNDINAME = "java:comp/UserTransaction";

	/*Private Constructor for the ODCServiceLocator */
	private ODCServiceLocator() {

	}

	/*
	 * The ODCServiceLocator is implemented as a Singleton.  The getInstance()
	 * method will return the static reference to the ODCServiceLocator stored
	 * inside of the ODCServiceLocator Class.
	 */
	public static synchronized ODCServiceLocator getInstance() {
		if (objODCServiceLocator == null) {
			objODCServiceLocator = new ODCServiceLocator();
		}
		return objODCServiceLocator;
	}

	static private String getServiceName(int pServiceId) throws Exception 
	{
		String serviceName = null;

		switch (pServiceId) {
			case USER_TRANSACTION :
									serviceName = USER_TRANSACTION_JNDINAME;
									break;
			/* default :
				throw new ServiceLocatorException(
					"Unable to locate the service requested in "
						+ "ServiceLocator.getServiceName() method.  "); */
		}
		return serviceName;
	}

	public UserTransaction getUserTransaction(int pServiceId) throws Exception 
	{
		if (logger.isInfoEnabled()) {
					logger.info("-> getUserTransaction");
		}
		/*Getting the JNDI Service Name*/
		String serviceName = getServiceName(pServiceId);
		if(logger.isDebugEnabled())
		{
			logger.debug("Service Name is ="+serviceName);
			logger.info("===> Service Name is ="+serviceName);
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

		} catch (Exception e) {

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