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
package oem.edge.ets.fe;

/**
 * @author Ravi K. Ravipati
 * File: ETSQuickSwitch.java
 *
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oem.edge.amt.AMTException;
import oem.edge.amt.EntitledStatic;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSQuickSwitch {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.12";
	public  static final String VERSION = "1.12";
	private static Log logger = EtsLogger.getLogger(ETSQuickSwitch.class);
	
	private boolean bAdmin = false;
	private String irId;
	private String curPrjId;
	private String projType;
	
	
	private String getIrId() {
		return irId;
	}
	
	private void setIrId(String irId) {
		this.irId = irId;
	}
	
	private String getCurPrjId() {
		return curPrjId;
	}
	
	private void setCurPrjId(String curPrjId) {
		this.curPrjId = curPrjId;
	}
	
	public ETSQuickSwitch(String irId, String currProjId, boolean bAdm, String sProjectType) {
		this.irId = irId;
		curPrjId = currProjId;
		this.bAdmin = bAdm;
		this.projType = sProjectType;
	}
	
	
	public String getQuickSwitchBox(Connection conn) {
		
		StringBuffer buffBox = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSet rset = null;
		String pstmtQuery = "";
		
		if (this.hasMoreProjects(conn)) {
			
			UnbrandedProperties prop = PropertyFactory.getProperty(projType);
			
			if (bAdmin) {
				pstmtQuery = "SELECT PROJECT_ID,PROJECT_NAME FROM ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND PROJECT_STATUS != ? AND PROJECT_TYPE = ? for READ ONLY";
			} else {
				pstmtQuery = "SELECT PROJECT_ID,PROJECT_NAME FROM ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND PROJECT_STATUS != ? AND PROJECT_TYPE = ? AND PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = ?) for READ ONLY";
			}
			
			// changed to display only customize workspaces in 5.2.1
			
			String qryPrj = "";
			
			if (bAdmin) {
				qryPrj = "select a.project_name, a.project_id "
						+ " from ets.ets_projects a where "
						+ "a.parent_id='0' and a.project_or_proposal in ('P','O') and a.project_status != '"
						+ Defines.WORKSPACE_DELETE
						+ "' "
						+ " and a.project_id in (select project_id from ets.ets_user_ws where user_id = '"
						+ this.getIrId()
						+ "') and a.project_type = '" + this.projType + "'"
						+ " order by a.project_name for read only";
			} else {
				qryPrj = "select a.project_name, a.project_id "
						+ " from ets.ets_projects a, ets.ets_users b"
						+ " where a.project_id = b.user_project_id "
						+ " and b.user_id = '"
						+ this.getIrId()
						+ "' and a.parent_id='0' and a.project_or_proposal in ('P','O') and a.project_status != '"
						+ Defines.WORKSPACE_DELETE
						+ "' "
						+ " and a.project_id in (select project_id from ets.ets_user_ws where user_id = '"
						+ this.getIrId()
						+ "') and a.project_type = '" + this.projType + "'"
						+ " order by a.project_name for read only";
			}
					
			buffBox.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"150\">");
			buffBox.append("<tr><td headers=\"\"  class=\"tgreen\">");
			buffBox.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"150\">");
			buffBox.append("<tr>");
			buffBox.append("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Your other workspace(s)</td>");
			buffBox.append("</tr>");
			buffBox.append("<tr><td headers=\"\"  width=\"150\">");
			buffBox.append("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
			buffBox.append("<tr valign=\"middle\">");
			buffBox.append("<td headers=\"\"  style=\"background-color: #ffffff;\" align=\"center\">");
			buffBox.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			
			try {
				
				Vector prjVect = EntitledStatic.getVQueryResult(conn, qryPrj, 2);
				
				if (prjVect.size() != 0) {
					
					buffBox.append("<tr><td headers=\"\">");
					buffBox.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					String[] prj = new String[2];
					
					for (int i = 0; i < prjVect.size(); i++) {
						prj = (String[]) prjVect.get(i);
						buffBox.append("<tr><td headers=\"\" width=\"16\" align=\"center\" valign=\"top\"> <img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + prj[1] + "&linkid=" + prop.getLinkID()+"\" class=\"fbox\">" + prj[0] + "</a></td></tr>");
						
						// prepare statement to get the sub workspaces.
						pstmt = conn.prepareStatement(pstmtQuery);
						// check for sub workspaces here...
						
						pstmt.setString(1, prj[1]);
						pstmt.setString(2, Defines.WORKSPACE_DELETE);
						pstmt.setString(3, this.projType);
						if (!bAdmin) {
							pstmt.setString(4, this.irId);
						}
						
						rset = pstmt.executeQuery();
						// print the sub workspaces here if available...
						while (rset.next()) {
							
							String sSubWorkspaceProjectID = rset.getString(1); // cannot be null
							String sSubWorkspaceProjectName = rset.getString(2); // cannot be null
							
							buffBox.append("<tr><td headers=\"\" width=\"16\">&nbsp;</td><td headers=\"\" align=\"left\" valign=\"top\">");
							buffBox.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\"><tr><td headers=\"\"  width=\"10\"  valign=\"top\"><span style=\"color: #006699;font-size:15\">&#183;</span></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + sSubWorkspaceProjectID + "&linkid=" + prop.getLinkID() + "\" class=\"fbox\"><b>" + sSubWorkspaceProjectName + "</b></a></td></tr></table>");
							
							buffBox.append("</td></tr>");
						}
					}
					buffBox.append("</table>");
					buffBox.append("</td></tr>");
				}
			} catch (SQLException sqlEx) {
			}
			
			finally {
				
				ETSDBUtils.close(rs);
				ETSDBUtils.close(rset);
				ETSDBUtils.close(pstmt);
			}
			buffBox.append("</table>");
			buffBox.append("</td></tr></table>");
			buffBox.append("</td></tr></table>");
			buffBox.append("</td></tr></table>");
		} else {
			buffBox.append(" ");
		}
		return (buffBox.toString());
	}
	public boolean hasMoreProjects(Connection conn) {
		
		// if user has more than one project/proposal - then show this box
		// else no need
		boolean hasMore = false;
		
		String qry = "select count(*) from ets.ets_users where user_id='" + this.getIrId() + "' and user_project_id!='" + this.getCurPrjId() + "'";
		
		try {
			
			int count = Integer.parseInt(EntitledStatic.getValue(conn, qry));
			hasMore = (count > 0) ? true : false;
			
		} catch (AMTException amtEx) {
		} catch (SQLException sqlEx) {
		}
		return (hasMore);
	}
	
}
