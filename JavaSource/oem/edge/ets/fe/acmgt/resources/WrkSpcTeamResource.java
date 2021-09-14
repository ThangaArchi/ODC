/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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

package oem.edge.ets.fe.acmgt.resources;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WrkSpcTeamResource implements WrkSpcTeamConstantsIF {

	public static final String VERSION = "1.8";
	private static Log logger = EtsLogger.getLogger(WrkSpcTeamResource.class);

	private HashMap wrkSpcPropMap = null; //props from file
	private static WrkSpcTeamResource wrkSpcResource = null;

	/**
	 * 
	 */
	private WrkSpcTeamResource() {
		super();
	}

	/**
		 * get single instance of EtsPcrResource
		 */

	public static WrkSpcTeamResource getInstance(String wrkSpcType) {

		if (wrkSpcResource == null) {
			wrkSpcResource = new WrkSpcTeamResource();
			wrkSpcResource.readPropFile(wrkSpcType);
		}

		return wrkSpcResource;
	}

	/**
		 * this method will read the property file
		 */
	private void readPropFile(String wrkSpcType) {

		String propKey = "";
		String propVal = "";
		ResourceBundle rb = null;

		try {

			wrkSpcPropMap = new HashMap();

			wrkSpcType = AmtCommonUtils.getTrimStr(wrkSpcType);

			if (AmtCommonUtils.isResourceDefined(wrkSpcType)) {

				if (wrkSpcType.equals("wrkspc")) {

					rb = ResourceBundle.getBundle("oem.edge.ets.fe.acmgt.resources.wrkspc");
				}

				if (wrkSpcType.equals("etswrkspc")) {

					rb = ResourceBundle.getBundle("oem.edge.ets.fe.acmgt.resources.etswrkspc");
				}

				if (wrkSpcType.equals("aicwrkspc")) {

					rb = ResourceBundle.getBundle("oem.edge.ets.fe.acmgt.resources.aicwrkspc");
				}

			} else {

				rb = ResourceBundle.getBundle("oem.edge.ets.fe.acmgt.resources.wrkspc");

			}

			Enumeration em = rb.getKeys();

			if (em != null) {

				while (em.hasMoreElements()) {

					//get key
					propKey = AmtCommonUtils.getTrimStr((String) em.nextElement());

					if (AmtCommonUtils.isResourceDefined(propKey)) {

						//get val
						propVal = AmtCommonUtils.getTrimStr(rb.getString(propKey));

						wrkSpcPropMap.put(propKey, propVal);

					}

				}

			}

		} catch (MissingResourceException mrEx) {

			AmtCommonUtils.LogGenExpMsg(mrEx, "General Exception in building WrkSpcTeamResource", ETS_TEAM_LSTUSR);

			if (mrEx != null) {
				SysLog.log(SysLog.ERR, this, mrEx);
				mrEx.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building WrkSpcTeamResource", ETS_TEAM_LSTUSR);

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
	public HashMap getWrkSpcPropMap() {
		return wrkSpcPropMap;
	}

} //end of class
