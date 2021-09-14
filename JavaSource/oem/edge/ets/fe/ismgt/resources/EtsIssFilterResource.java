package oem.edge.ets.fe.ismgt.resources;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.SysLog;
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

/**
 * @author v2phani
 * This class implements singleton,to get single instance of properties
 * loaded into at a time
 *
 */
public class EtsIssFilterResource implements Serializable, EtsIssFilterConstants {

	public static final String VERSION = "1.45";

	private HashMap filterPropMap = null; //filter props from file
	private static EtsIssFilterResource filterResource = null;

	/**
	 * Constructor for FilterResource.
	 */
	private EtsIssFilterResource() {
		super();
	}

	/**
	 * get single instance of EtsIssFilterResource
	 */

	public static EtsIssFilterResource getInstance() {
		if (filterResource == null) {
			filterResource = new EtsIssFilterResource();
			filterResource.readPropFile();
		}

		return filterResource;
	}

	/**
	 * this method will read the property file
	 */
	private void readPropFile() {

		String propKey = "";
		String propVal = "";

		try {

			filterPropMap = new HashMap();

			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ismgt.resources.filterresource");

			Enumeration em = rb.getKeys();

			if (em != null) {

				while (em.hasMoreElements()) {

					//get key
					propKey = AmtCommonUtils.getTrimStr((String) em.nextElement());

					if (AmtCommonUtils.isResourceDefined(propKey)) {

						//get val
						propVal = AmtCommonUtils.getTrimStr(rb.getString(propKey));

						filterPropMap.put(propKey, propVal);

					}

				}

			}

		} catch (MissingResourceException mrEx) {

			AmtCommonUtils.LogGenExpMsg(mrEx, "General Exception in building EtsIssFilterResource", ETSLSTUSR);

			if (mrEx != null) {
				SysLog.log(SysLog.ERR, this, mrEx);
				mrEx.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building EtsIssFilterResource", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

	} //end of method

	/**
	 * Returns the filterPropMap.
	 * @return HashMap
	 */
	public HashMap getFilterPropMap() {
		return filterPropMap;
	}

} //end of class
