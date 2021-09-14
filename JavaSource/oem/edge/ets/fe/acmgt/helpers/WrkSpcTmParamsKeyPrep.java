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

package oem.edge.ets.fe.acmgt.helpers;

import java.sql.SQLException;

import oem.edge.ets.fe.acmgt.model.WrkSpcTmParamsKey;
import oem.edge.ets.fe.acmgt.resources.WrkSpcTeamConstantsIF;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2phani
 * This class prepares the Key EtsIssFilterObjectKey object and returns to Servlet
 */
public class WrkSpcTmParamsKeyPrep implements WrkSpcTeamConstantsIF {

	public static final String VERSION = "1.8";
	private static Log logger = EtsLogger.getLogger(WrkSpcTmParamsKeyPrep.class);
	
	

	/**
	 * Constructor for EtsIssFilterObjKeyPrep.
	 */
	public WrkSpcTmParamsKeyPrep() {
		super();
		

	}

	
	/**
	 * get Issue Filter Object Key
	 */

	public WrkSpcTmParamsKey getWrkSpcTeamParamsKey(String wrkSpcType) throws SQLException, Exception {

		WrkSpcTmParamsKey wrkSpcPrmKey = new WrkSpcTmParamsKey();

		try {
			
			WrkSpcTeamUtils teamUtils = new WrkSpcTeamUtils();
			
			//set prop map
			wrkSpcPrmKey.setPropMap(teamUtils.getWrkSpcPropMap(wrkSpcType));
			

		} finally {

		}

		return wrkSpcPrmKey;

	}

} //end of class
