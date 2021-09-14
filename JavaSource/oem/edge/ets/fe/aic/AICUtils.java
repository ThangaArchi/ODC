/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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


package oem.edge.ets.fe.aic;

import java.util.Vector;
import java.sql.Connection;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;

public class AICUtils {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.5";

	//private static Log logger = EtsLogger.getLogger(AICUtils.class);


	public static String checkUserRole(EdgeAccessCntrl es) throws Exception {

		String sRole = Defines.INVALID_USER;
		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection();
			String ir_userid = es.gIR_USERN;


			Vector userents = new Vector();

			// non ets workspace.. AIC
			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(conn,ir_userid);
			userents = AccessCntrlFuncs.getUserEntitlements(conn,edgeuserid,true, true);

			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				sRole = Defines.ETS_ADMIN;
			}else if (userents.contains(Defines.COLLAB_CENTER_ENTITLEMENT)) {
				if  (userents.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT) || 
				userents.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT) || 
				userents.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT)) {
					sRole = Defines.WORKSPACE_MEMBER;
				} else {
					sRole = Defines.WORKSPACE_VISITOR;
				}						
			} else {
				sRole = Defines.INVALID_USER;				
			}
			System.out.println(ir_userid +"---Role in AIC----" + sRole);

		} catch (Exception e) {
		   	throw e;
		} finally {
			ETSDBUtils.close(conn);
		}

		return sRole;
	 }
}
