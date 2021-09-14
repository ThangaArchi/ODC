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

import java.sql.SQLException;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class WrkSpcTmMembrRole {
	
	private String sLoginUserId;
	public static final String VERSION = "1.8";

	/**
	 * 
	 */
	public WrkSpcTmMembrRole(String sLoginUserId) {
		super();
		this.sLoginUserId=sLoginUserId;
		// TODO Auto-generated constructor stub
	}
	
	public abstract WrkSpcTeamActionsOpModel processAddMembrRequest(WrkSpcTeamActionsInpModel actInpModel, WrkSpcTeamObjKey teamObjKey) throws SQLException, Exception;
	
	

}//end of class
