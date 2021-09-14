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
public class WrkSpcInviteJobProps implements WrkSpcTeamConstantsIF {

	public static final String VERSION = "1.8";
	private static Log logger = EtsLogger.getLogger(WrkSpcInviteJobProps.class);

	private HashMap invJobPropMap = null; //props from file
	private static WrkSpcInviteJobProps invJobProps = null;

	/**
	 * 
	 */
	private WrkSpcInviteJobProps() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
			 * get single instance of EtsPcrResource
			 */

	public static WrkSpcInviteJobProps getInstance() {

		if (invJobProps == null) {
			invJobProps = new WrkSpcInviteJobProps();
			invJobProps.readPropFile();
		}

		return invJobProps;
	}

	/**
		 * this method will read the property file
		 */
	private void readPropFile() {

		String propKey = "";
		String propVal = "";
		ResourceBundle rb = null;

		try {

			invJobPropMap = new HashMap();

			rb = ResourceBundle.getBundle("oem.edge.ets.fe.acmgt.resources.invjob");

			Enumeration em = rb.getKeys();

			if (em != null) {

				while (em.hasMoreElements()) {

					//get key
					propKey = AmtCommonUtils.getTrimStr((String) em.nextElement());

					if (AmtCommonUtils.isResourceDefined(propKey)) {

						//get val
						propVal = AmtCommonUtils.getTrimStr(rb.getString(propKey));

						invJobPropMap.put(propKey, propVal);

					}

				}

			}

		} catch (MissingResourceException mrEx) {

			AmtCommonUtils.LogGenExpMsg(mrEx, "General Exception in building WrkSpcInviteJobProps", ETS_TEAM_LSTUSR);

			if (mrEx != null) {
				SysLog.log(SysLog.ERR, this, mrEx);
				mrEx.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building WrkSpcInviteJobProps", ETS_TEAM_LSTUSR);

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
	public HashMap getInvJobPropMap() {
		return invJobPropMap;
	}

} //end of class
