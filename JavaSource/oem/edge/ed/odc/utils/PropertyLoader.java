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

import java.io.InputStream;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;


import org.apache.commons.logging.Log;


/**
 * @author thanga
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class PropertyLoader {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger = ODCLogger.getLogger(PropertyLoader.class);
	
	public static Properties loadProperties(String name) throws Exception {
		if (logger.isInfoEnabled()) {
				logger.info("==> Start to loadProperties");
			}
		if (name == null) throw new Exception("==> Resource Bundle Name cannot be null");

		Properties result = null;
		InputStream in = null;

		try {

			// Throws MissingResourceException on lookup failures:
			final ResourceBundle rb = ResourceBundle.getBundle(name);

			result = new Properties();
			for (Enumeration keys = rb.getKeys(); keys.hasMoreElements();) {
				final String key = (String) keys.nextElement();
				final String value = rb.getString(key);

				result.put(key, value);
			}

		}
		catch(MissingResourceException mre)
		{
			throw new Exception("==> Resource Bundle "+ name + " not found");
		}
		catch (Exception e) {
			result = null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Throwable ignore) {
					System.out.println(ignore);
				}
		}
		if (logger.isInfoEnabled()) {
			logger.info("End to loadProperties");
		}
		return result;
	}
}