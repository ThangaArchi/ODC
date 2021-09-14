/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2006                                          */
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

package oem.edge.ets.fe.ismgt.helpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.model.ETSIssue;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EmailFormatUtils {

	public static final String VERSION = "1.4";

	private static Log logger = EtsLogger.getLogger(EmailFormatUtils.class);

	/**
	 * 
	 */
	public EmailFormatUtils() {
		super();

		if (!Global.loaded) {

			Global.Init();
		}

	}

	/**
	 * 
	 * @return
	 */

//this is for Remind Job Email Content v2sagar need to modify From email id ,link id

	
	
	public String generateOpeningStatementForRemind() {

		StringBuffer sEmailStr = new StringBuffer();
		sEmailStr.append("Reminder:\n");

		sEmailStr.append("You are receiving this message because an action on this issue is overdue. The details of this issue are given below. You may use the link to update the issue with appropriate status\n");

		return sEmailStr.toString();
	}

	//	Thid method generates the issue details that need to be incorporated in the email
	public String generateIssueDetailsForRemind(ETSIssue currentRecord) {
		StringBuffer sEmailStr = new StringBuffer();

		sEmailStr.append("======================= D E T A I L S ========================\n\n");

		try {

			sEmailStr.append("  ID:            " + ETSUtils.formatEmailStr(currentRecord.cq_trk_id) + "\n");
			sEmailStr.append("  Workspace:     " + ETSUtils.formatEmailStr(currentRecord.cust_project) + "\n");
			sEmailStr.append("  Title:         " + ETSUtils.formatEmailStr(currentRecord.title) + "\n");
			sEmailStr.append("  Severity:      " + ETSUtils.formatEmailStr(currentRecord.severity) + "\n");
			sEmailStr.append("  Submitted by:  " + currentRecord.cust_name + " \n\n");
			//sEmailStr.append("  Description:   " + ETSUtils.formatEmailStr(currentRecord.problem_desc) + " \n");
			//sEmailStr.append("  Description:   " + currentRecord.problem_desc + " \n");
			//sEmailStr.append("  Description:   \n");
			//sEmailStr.append("  " + currentRecord.problem_desc + " \n");
		} catch (Exception e) {

			logger.error("Error in issue details email msg for reminder===", e);
		}

		return sEmailStr.toString();

	}

	//	This method creates the action link to access the issue directly from the email
	public String createLinkForRemind(ETSIssue currentRecord) {
		
		StringBuffer sEmailStr=new StringBuffer();

		String urlString = "";

		String sTC = AmtCommonUtils.getTrimStr(String.valueOf(currentRecord.field_C12));

		//modify the link v2sagar for AIC Remind Job
		System.out.println("CHECK 1#####"+currentRecord.ets_project_id.trim()+"####");
		System.out.println("CHECK 2#####"+getProjectType(currentRecord.ets_project_id.trim())+"####");
		
		if(getProjectType(currentRecord.ets_project_id.trim()).equalsIgnoreCase("ETS"))		
			urlString = (String) Global.getUrl("ets") + "/ETSProjectsServlet.wss" + "?linkid=251000&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC + "&sc=0&actionType=resolveIssue&edge_problem_id=" + currentRecord.edge_problem_id;
		else if(getProjectType(currentRecord.ets_project_id.trim()).equalsIgnoreCase("AIC"))
		{
			urlString = (String) Global.getUrl("ets") + "/ETSProjectsServlet.wss" + "?linkid=320000&proj=" + currentRecord.ets_project_id.trim() + "&tc=" + sTC + "&sc=0&actionType=resolveIssue&edge_problem_id=" + currentRecord.edge_problem_id;
			
		}
		//till here v2sagar
		sEmailStr.append(urlString);
		//sEmailStr.append("\n");

		return sEmailStr.toString();

	}

	//	This method generates the subject line of the email
	public String generateEmailSubjectForRemind(ETSIssue currentRecord) {

		StringBuffer sEmailSubjectbuf = new StringBuffer();
		if(getProjectType(currentRecord.ets_project_id.trim()).equalsIgnoreCase("ETS"))		
			sEmailSubjectbuf.append("E&TS Connect: Issue action reminder for issue: " + currentRecord.cq_trk_id + " in workspace '" + currentRecord.cust_project + "' ");
		else 
			sEmailSubjectbuf.append("Collaboration Center: Issue action reminder for issue: " + currentRecord.cq_trk_id + " in workspace '" + currentRecord.cust_project + "' ");

		return sEmailSubjectbuf.toString();

	}
	
//	This method is used to generate the content of the email as described above in the template
	public String generateEmailContent(ETSIssue currentRecord) {

		StringBuffer sEmailStr = new StringBuffer();
		
		sEmailStr.append(generateOpeningStatementForRemind());
		sEmailStr.append("\n\n");
		sEmailStr.append(generateIssueDetailsForRemind(currentRecord));
		sEmailStr.append("\n");
		sEmailStr.append(createLinkForRemind(currentRecord));
		sEmailStr.append("\n");
		//modified by v2sagar
		if(getProjectType(currentRecord.ets_project_id.trim()).equalsIgnoreCase("ETS"))	            
            sEmailStr.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));
		else if(getProjectType(currentRecord.ets_project_id.trim()).equalsIgnoreCase("AIC"))
			sEmailStr.append(CommonEmailHelper.getEmailFooter("Collaboration Center"));
		//till here

		return sEmailStr.toString();
	}
//this method recognizes wheather the project type is AIC or ETS v2sagar	 
	public static String getProjectType(String project_id)
	{	
		String sqlQry = "SELECT PROJECT_TYPE FROM ETS.ETS_PROJECTS WHERE PROJECT_ID = '" + project_id + "' with ur";		

		Statement  stmt		= null;
		ResultSet  rset		= null;
		Connection conn		= null;
		String projectType	= "";
		System.out.println("Query to Retrieve ProjectType"+sqlQry);

		try
		{
			conn = WrkSpcTeamUtils.getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlQry);

			while (rset.next())
			{
				projectType = rset.getString("PROJECT_TYPE");
	
			}
		}
		catch (Exception sqlEx)
		{
			sqlEx.printStackTrace();
		}
		finally
		{
			ETSDBUtils.close(rset);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);			
		}
		System.out.println("Project Type From DB--->>>"+projectType);
		return projectType;
	}
} //end of class
