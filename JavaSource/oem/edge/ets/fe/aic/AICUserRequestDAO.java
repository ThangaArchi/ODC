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


/*
 * Created on May 12, 2005
 */
 
package oem.edge.ets.fe.aic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author v2sathis
 *
 */
public class AICUserRequestDAO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";

	private static Log logger = EtsLogger.getLogger(AICUserRequestDAO.class);
	
	public static Vector getActiveWorkspacesForCompany(Connection con, String sCompany, String sUserId) throws SQLException, Exception {
		
		Vector vWorkspaces = new Vector();
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		

		try {

			sQuery.append("SELECT PROJECT_ID,PROJECT_DESCRIPTION,PROJECT_NAME,PROJECT_START," +
				              "PROJECT_END,DECAF_PROJECT_NAME,PROJECT_OR_PROPOSAL,LOTUS_PROJECT_ID,RELATED_ID," +
				              "PARENT_ID,COMPANY,PMO_PROJECT_ID,SHOW_ISSUE_OWNER,PROJECT_STATUS,DELIVERY_TEAM," +
				              "GEOGRAPHY,INDUSTRY,IS_ITAR " +
				              "FROM ETS.ETS_PROJECTS " +
				              "WHERE COMPANY = '" + sCompany + "' " +
				              "AND PROJECT_OR_PROPOSAL IN ('O','P','C') " +
				              "AND PROJECT_STATUS NOT IN ('D','A') AND PROJECT_TYPE = 'AIC' " +				              "AND (IS_PRIVATE not in ('" + Defines.AIC_IS_PRIVATE_PRIVATE + "','"+ Defines.AIC_IS_PRIVATE_TEAMROOM + "') or " +
				              "(project_id in (select user_project_id from ets.ets_users where user_id='"+ sUserId + "'))) " +
							  
							  "ORDER BY PROJECT_NAME with ur");

			
			if (logger.isDebugEnabled()) {
				logger.debug("AICUserRequestDAO::getActiveWorkspacesForCompany::QUERY : " + sQuery.toString());
			}			

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				
				ETSProj proj = new ETSProj();

				String sProjId = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
				String sProjDesc = ETSUtils.checkNull(rs.getString("PROJECT_DESCRIPTION"));

				String sProjName = ETSUtils.checkNull(rs.getString("PROJECT_NAME"));
				Timestamp tProjStart = rs.getTimestamp("PROJECT_START");
				Timestamp tProjEnd = rs.getTimestamp("PROJECT_END");
				String sDecafProjName = ETSUtils.checkNull(rs.getString("DECAF_PROJECT_NAME"));
				String sProjOrProposal = ETSUtils.checkNull(rs.getString("PROJECT_OR_PROPOSAL"));
				String sLotusProjID = ETSUtils.checkNull(rs.getString("LOTUS_PROJECT_ID"));
				String sRelatedId = ETSUtils.checkNull(rs.getString("RELATED_ID"));
				String sParentId = ETSUtils.checkNull(rs.getString("PARENT_ID"));
				String sCompany1 = ETSUtils.checkNull(rs.getString("COMPANY"));
				String sPmoProjectId = ETSUtils.checkNull(rs.getString("PMO_PROJECT_ID"));
				String sShowIssueOwner = ETSUtils.checkNull(rs.getString("SHOW_ISSUE_OWNER"));
				String sProjectStatus = ETSUtils.checkNull(rs.getString("PROJECT_STATUS"));
				String sDelivery = ETSUtils.checkNull(rs.getString("DELIVERY_TEAM"));
				String sGeo = ETSUtils.checkNull(rs.getString("GEOGRAPHY"));
				String sIndustry = ETSUtils.checkNull(rs.getString("INDUSTRY"));
				String sIsITAR = ETSUtils.checkNull(rs.getString("IS_ITAR"));

				proj.setProjectId(sProjId);
				proj.setDescription(sProjDesc);
				proj.setName(sProjName);
				proj.setStartDate(tProjStart);
				proj.setEndDate(tProjEnd);
				proj.setDecafProject(sDecafProjName);
				proj.setProjectOrProposal(sProjOrProposal);
				proj.setLotusProject(sLotusProjID);
				proj.setRelatedProjectId(sRelatedId);
				proj.setParent_id(sParentId);
				proj.setCompany(sCompany);
				proj.setPmo_project_id(sPmoProjectId);
				proj.setShow_issue_owner(sShowIssueOwner);
				proj.setProject_status(sProjectStatus);
				proj.setProjBladeType(ETSUtils.isEtsProjBladeProject(sProjId));
				proj.setDeliveryTeam(sDelivery);
				proj.setGeography(sGeo);
				proj.setIndustry(sIndustry);
				
				if (sIsITAR.equalsIgnoreCase("Y")) {
					proj.setITAR(true);
				} else {
					proj.setITAR(false);
				}

				vWorkspaces.add(proj);

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vWorkspaces;
		 
	}

}


