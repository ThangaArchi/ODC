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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;


/*
 *  The SQLCode class is a helper class that loads all of the SQL
 *  code used within the persistence framework into a private properties
 *  object stored within the SQLCode class.
 *
 */
public class SQLCode {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger = ODCLogger.getLogger(SQLCode.class);
	private static SQLCode sqlCode = null;
	private static Properties sqlCache = new Properties();
	private static final String BUNDLE_NAME = "oem.edge.ed.odc.ODCSQL";
	
	/*Calling the SQLCode's constructor*/
	/*static {
		sqlCode = new SQLCode();
	}
	*/

	/*Retrieves the Singleton for the SQLCode class*/
	public static synchronized SQLCode getInstance() //throws DataAccessException
	{
		
		if(sqlCode == null)
		{
			sqlCode = new SQLCode();
		}
		return sqlCode;
	}

	/*
	 * The SQLCode constructor loads all of the SQL code used in the
	 * persistence framework from the sql.properties class into the sqlCache
	 * properties object
	 *
	 */
	private SQLCode() //throws DataAccessException
	{
	
		try {
			sqlCache = PropertyLoader.loadProperties(BUNDLE_NAME);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//sqlCache.load(new FileInputStream(sqlFileName));

	}

	/* 
	 * The getSQLStatement() method will try to retrieve a SQL statement
	 * from the SQLCache properties object based on the key passed into 
	 * the method.  If it can not find an the SQL statement, a DataAccess
	 * Exception will be raised.
	 *
	 */
	public String getSQLStatement(String pSQLKeyName)//	throws AICDataAccessException 
	{
		if (logger.isInfoEnabled()) {
				logger.info("-> getSQLStatement");
		}
		/*Checking to see if the requested SQL statement is in the sqlCache*/
		
		if (sqlCache.containsKey(pSQLKeyName)) {
			if (logger.isInfoEnabled()) {
					logger.info("<- getSQLStatement");
			}
			return (String) sqlCache.get(pSQLKeyName);
			
		} else {
			if (logger.isInfoEnabled()) {
					logger.info("<- getSQLStatement");
			}
		}		
		return pSQLKeyName;
	}
}
