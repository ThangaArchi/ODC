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

package oem.edge.ets.fe.ubp;

import oem.edge.decaf.ws.DecafEntAccessObj;
import oem.edge.decaf.ws.DecafWsRepObj;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.acmgt.actions.UserEntitlementsMgrIF;
import oem.edge.ets.fe.acmgt.bdlg.UserEntReqUtilsImpl;

/**
 * @author v2ravik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSRequestApprovalFunctions {
	
	public static final String VERSION = "1.2";
	
	private ETSParams etsParams;
	
	public ETSRequestApprovalFunctions() {
		this.etsParams = null;
	}
	public String getRequestStatus(String requestId) {
		return "";
	}
	public void setEtsParms(ETSParams etsParms) {
		this.etsParams = etsParms;
	}

/**
 * 
 * @param entReqIrUserId
 * @param loginIrUserId
 * @param decafEntAccObj
 * @return
 */
	
	public DecafWsRepObj requestEntitlement(String entReqIrUserId, String loginIrUserId, DecafEntAccessObj decafEntAccObj) {
		
		UserEntitlementsMgrIF usrEntMgrIF = new UserEntReqUtilsImpl();
				
		return usrEntMgrIF.requestEntitlementObjToUser(entReqIrUserId,loginIrUserId,decafEntAccObj);
}

}//end of class
