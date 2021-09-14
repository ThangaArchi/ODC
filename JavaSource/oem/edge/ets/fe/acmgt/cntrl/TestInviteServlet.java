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

package oem.edge.ets.fe.acmgt.cntrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.acmgt.helpers.*;
import oem.edge.ets.fe.acmgt.dao.*;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel;
import oem.edge.ets.fe.acmgt.wrkflow.AddMembrToEtsWrkSpcImpl;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @version 	1.0
 * @author
 */
public class TestInviteServlet extends HttpServlet {
	
	private static Log logger = EtsLogger.getLogger(TestInviteServlet.class);
	public static final String VERSION = "1.8";

	/**
	 * This service method is the core method which will do the following actions in ETS Issues/Changes Filter
	 * 1.get the state of the process
	 * 2.routes to resp. Command object
	 * 3. prepares the key object that contains various key values required across issues filtereing
	 * 3.creates es.GetProfile
	 * 4.creates AmtHeaderFooter
	 */

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		AddMembrToEtsWrkSpcImpl addMmbrIF = new AddMembrToEtsWrkSpcImpl();
		PrintWriter out=response.getWriter();
		Connection conn=null;

		try {
			
			if(logger.isDebugEnabled()) {
				
							logger.debug("SYSTEM LOG TEST FRM SERVLET");
						}

			WrkSpcTeamActionsInpModel actInpModel = new WrkSpcTeamActionsInpModel();

			actInpModel.setUserId("toolsale@us.ibm.com");
			actInpModel.setRequestorId("yli@us.ibm.com");
			actInpModel.setLastUserId("yli@us.i");
			actInpModel.setRoleId(147);
			actInpModel.setUserAssignCompany("CISCO");
			actInpModel.setUserAssignCountry("897");
			actInpModel.setWrkSpcId("1117123752521");
			actInpModel.setWrkSpcType("ETS");

			//WrkSpcTeamActionsOpModel opModel = addMmbrIF.processAddMemberToWrkSpc(actInpModel);
			//out.println(opModel.getRetCode());
			//out.println(opModel.getRetCodeMsg());
			conn=WrkSpcTeamUtils.getConnection();
			
			if(conn!=null) {
				
				InvMembrToWrkSpcDAO invDao=new InvMembrToWrkSpcDAO();
				logger.debug("print boolean=="+invDao.isRequestExistsInInviteStatus(conn,"v2sathis@us.ibm.com","1"));
			}

		} catch (SQLException se) {

			se.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		
		finally{
			
			ETSDBUtils.close(conn);
		}

	}

}
