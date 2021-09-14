/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
package oem.edge.ets.fe.aic.metrics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSParams;


import oem.edge.common.DatesArithmatic;

public interface AICMetricsFunctionsInt {


    public final String metURL = Defines.SERVLET_PATH+"AICMetricsServlet.wss?linkid=1k0000&project_id=metrics&option=search";


	public void setETSParams(ETSParams params);
	public AICMetricsResultObj getReportList();
	public AICMetricsResultObj getReportPageInfo();

}

