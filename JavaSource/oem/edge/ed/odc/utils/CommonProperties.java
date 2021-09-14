/*
 * Created on Feb 8, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;


import org.apache.commons.logging.Log;

/**
 * @author thanga
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CommonProperties
{

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger = ODCLogger.getLogger(CommonProperties.class);

	private static CommonProperties propTest = null;
	private static CommonProperties commomPropTest = null;
	
	private static Properties propCache = new Properties();
	private static final String BUNDLE_NAME = "ODCCommon";
	

	/*
	 * The PropertyTester constructor loads all of the SQL code used in the
	 * persistence framework from the sql.properties class into the propCache
	 * properties object
	 *
	 */
	private CommonProperties() throws Exception
	{
		logger.info("Bundle Name :: ===>"+BUNDLE_NAME);
		propCache = PropertyLoader.loadProperties(BUNDLE_NAME);
	}

	

	/*
	 * The PropertyTester constructor loads all of the SQL code used in the
	 * persistence framework from the COMMON properties class into the propCache
	 * properties object
	 *
	 */
	private CommonProperties(String bundleName) throws Exception
	{
		logger.info("Bundle Name :: ===>"+bundleName);
		propCache = PropertyLoader.loadProperties(bundleName);
	}

	/*Retrieves the Singleton for the PropertyTester class*/
	public static synchronized CommonProperties getInstance() throws Exception
	{
		if(propTest == null)
		{
			propTest = new CommonProperties();
		}
		return propTest;
	}


	/*Retrieves the Singleton for the PropertyTester class*/
	public static synchronized CommonProperties getInstance(String bundleName) throws Exception
	{
		if(commomPropTest == null)
		{
			commomPropTest = new CommonProperties(bundleName);
		}
		return commomPropTest;
	}

	/* 
	 * The getSQLStatement() method will try to retrieve a SQL statement
	 * from the SQLCache properties object based on the key passed into 
	 * the method.  If it can not find an the SQL statement, a DataAccess
	 * Exception will be raised.
	 *
	 */
	public String getCommonProperty(String pSQLKeyName)
	{
		if (logger.isInfoEnabled()) {
				logger.info("start to getSQLStatement");
		}
		/*Checking to see if the requested SQL statement is in the propCache*/
		
		if (propCache.containsKey(pSQLKeyName))
		{
			if (logger.isInfoEnabled())
			{
					logger.info("End to getSQLStatement");
			}
			return (String) propCache.get(pSQLKeyName);
		}
		else
		{
			if (logger.isInfoEnabled())
			{
					logger.info("End to getSQLStatement");
			}
			logger.info("Unable to locate the SQL statement requested in PropertyTester.getSQLCode() "+ pSQLKeyName);
		}
		return propCache.getProperty(pSQLKeyName);
				
	}
}
