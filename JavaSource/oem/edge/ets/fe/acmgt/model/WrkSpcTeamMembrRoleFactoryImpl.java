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
package oem.edge.ets.fe.acmgt.model;

import oem.edge.ets.fe.Defines;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WrkSpcTeamMembrRoleFactoryImpl implements WrkSpcTeamMembrRoleFactoryIF {
	
	public static final String VERSION = "1.8";

	/**
	 * 
	 */
	public WrkSpcTeamMembrRoleFactoryImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.acmgt.model.TeamMembrRoleFactoryIF#createWrkSpcTeamRole(java.lang.String)
	 */
	public WrkSpcTmMembrRole createWrkSpcTeamRole(String sLoginUserId, String userRole) throws Exception {

		if (userRole.equals(Defines.WORKSPACE_OWNER)) {

			return new WrkSpcTeamWO(sLoginUserId);
		}

		if (userRole.equals(Defines.WORKSPACE_MANAGER)) {

			return new WrkSpcTeamWM(sLoginUserId);
		}

		return null;

	}

} //end of class
