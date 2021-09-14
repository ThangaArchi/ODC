/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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


/*
 * Created on Aug 1, 2005
 * @author v2sathis@us.ibm.com
 */
 
package oem.edge.ets.fe.brand;

import java.sql.Connection;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PropertyFactory {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	public static UnbrandedProperties getProperty(Connection con, String sProjectId) throws Exception {
		
		ETSProj proj = ETSUtils.getProjectDetails(con,sProjectId);
		
		if (proj.getProjectType().equalsIgnoreCase(Defines.ETS_WORKSPACE_TYPE)) {
			return new ETSUnbrandedProperties();
		} else {
			return new AICUnbrandedProperties();
		}
		
	}

	public static UnbrandedProperties getProperty(String sProjectType) {
		if (sProjectType.equalsIgnoreCase(Defines.ETS_WORKSPACE_TYPE)) {
			return new ETSUnbrandedProperties();
		} else {
			return new AICUnbrandedProperties();
		}
	}

	
}
