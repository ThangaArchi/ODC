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
package oem.edge.ets.fe.acmgt.actions;

import java.sql.SQLException;

import oem.edge.decaf.ws.DecafEntAccessObj;
import oem.edge.decaf.ws.DecafWsRepObj;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface UserEntitlementsMgrIF {
	
	public boolean requestEntitlementToUser(String entReqIrUserId, String loginIrUserId, DecafEntAccessObj decafEntAccObj);
	public DecafWsRepObj requestEntitlementObjToUser(String entReqIrUserId, String loginIrUserId, DecafEntAccessObj decafEntAccObj);
	public boolean isUserHasEntitlement(String userId,String entitlement) throws SQLException,Exception;
	public boolean isUserHasPendingEntitlement(String userId,String entitlement) throws SQLException,Exception;
}
