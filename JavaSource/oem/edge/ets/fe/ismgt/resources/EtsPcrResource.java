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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.SysLog;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsPcrResource implements Serializable, EtsIssFilterConstants {

	public static final String VERSION = "1.29";

	private HashMap pcrPropMap = null; //filter props from file
	private static EtsPcrResource pcrResource = null;

	/**
	 * 
	 */
	private EtsPcrResource() {
		super();
	}

	/**
		 * get single instance of EtsPcrResource
		 */

	public static EtsPcrResource getInstance() {

		if (pcrResource == null) {
			pcrResource = new EtsPcrResource();
			pcrResource.readPropFile();
		}

		return pcrResource;
	}

	/**
		 * this method will read the property file
		 */
	private void readPropFile() {

		String propKey = "";
		String propVal = "";

		try {

			pcrPropMap = new HashMap();

			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ismgt.resources.pcr");

			Enumeration em = rb.getKeys();

			if (em != null) {

				while (em.hasMoreElements()) {

					//get key
					propKey = AmtCommonUtils.getTrimStr((String) em.nextElement());

					if (AmtCommonUtils.isResourceDefined(propKey)) {

						//get val
						propVal = AmtCommonUtils.getTrimStr(rb.getString(propKey));

						pcrPropMap.put(propKey, propVal);

					}

				}

			}

		} catch (MissingResourceException mrEx) {

			AmtCommonUtils.LogGenExpMsg(mrEx, "General Exception in building EtsPcrResource", ETSLSTUSR);

			if (mrEx != null) {
				SysLog.log(SysLog.ERR, this, mrEx);
				mrEx.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building EtsPcrResource", ETSLSTUSR);

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
	public HashMap getPcrPropMap() {
		return pcrPropMap;
	}

} //end of class
