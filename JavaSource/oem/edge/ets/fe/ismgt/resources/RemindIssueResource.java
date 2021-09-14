/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.ismgt.resources;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemindIssueResource {

	public static final String VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(RemindIssueResource.class);

	private HashMap remindPropMap = null; //filter props from file
	private static RemindIssueResource remindResource = null;

	/**
	 * 
	 */
	private RemindIssueResource() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
			 * get single instance of EtsPcrResource
			 */

	public static RemindIssueResource getInstance() {

		if (remindResource == null) {
			remindResource = new RemindIssueResource();
			remindResource.readPropFile();
		}

		return remindResource;
	}

	/**
		 * this method will read the property file
		 */
	private void readPropFile() {

		String propKey = "";
		String propVal = "";

		try {

			remindPropMap = new HashMap();

			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ismgt.resources.remindjob");

			Enumeration em = rb.getKeys();

			if (em != null) {

				while (em.hasMoreElements()) {

					//get key
					propKey = AmtCommonUtils.getTrimStr((String) em.nextElement());

					if (AmtCommonUtils.isResourceDefined(propKey)) {

						//get val
						propVal = AmtCommonUtils.getTrimStr(rb.getString(propKey));

						remindPropMap.put(propKey, propVal);

					}

				}

			}

		} catch (MissingResourceException mrEx) {

			logger.error("Missing Resource Exception@@RemindIssueResource", mrEx);
			mrEx.printStackTrace();

		} catch (Exception ex) {

			logger.error("Gen Exception@@RemindIssueResource", ex);
			ex.printStackTrace();

		}

	} //end of method

	/**
	 * Returns the filterPropMap.
	 * @return HashMap
	 */
	public HashMap getRemindPropMap() {
		return remindPropMap;
	}

} //end of class
