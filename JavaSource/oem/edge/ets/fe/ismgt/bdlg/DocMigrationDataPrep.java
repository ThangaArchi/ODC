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

package oem.edge.ets.fe.ismgt.bdlg;

import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFileAttachUtils;
import oem.edge.ets.fe.ismgt.model.ETSIssueAttach;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DocMigrationDataPrep {

	public static final String VERSION = "1.1";
	private static Log logger = EtsLogger.getLogger(DocMigrationDataPrep.class);

	/**
	 * 
	 */
	public DocMigrationDataPrep() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
	}

	public void migrIssueFiles(HttpServletRequest request,HttpServletResponse response)  {
		
		PrintWriter out = null;

		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;

		//file vars
		String edgeProblemId;
		String projectId;
		int fileno = 0;
		String fileDesc = null;
		String fileName = null;
		int filesize = 0;
		String statusFlag = "";
		String lastUserId = "";
		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();
		int iDocId = -1;
		int count = 0;
		InputStream inStream =null;
		
		String probid="";

		try {
			
			out = response.getWriter();
			
			
			probid=AmtCommonUtils.getTrimStr(request.getParameter("probid"));

			sb.append("select ");
			sb.append(" a.EDGE_PROBLEM_ID as problemid, ");
			sb.append(" b.ETS_PROJECT_ID as projectid, ");
			sb.append(" a.FILE_NO as fileno, ");
			sb.append(" a.FILE_NAME as filename, ");
			sb.append(" a.FILE_DESC as filedesc, ");
			sb.append(" a.FILE_SIZE as filesize, ");
			sb.append(" a.FILE_NEW_FLG as statusflag, ");
			sb.append(" a.FILE_DATA as filedata, ");
			sb.append(" a.LAST_USERID as lastuser, ");
			sb.append(" a.LAST_TIMESTAMP ");
			sb.append(" from ");
			sb.append(" ets.problem_info_usr2 a, ets.problem_info_usr1 b ");
			sb.append(" where ");
			sb.append(" a.edge_problem_id=b.edge_problem_id ");
			sb.append(" and b.problem_class in ('Defect') ");
			
			if(AmtCommonUtils.isResourceDefined(probid)) {
				
				sb.append(" and a.edge_problem_id='"+probid+"' ");
			}
			sb.append(" order by problemid,fileno ");
			sb.append(" with ur ");

			System.out.println("GET ISSUE DOCS QRY===" + sb.toString());
			logger.debug("GET ISSUE DOCS QRY===" + sb.toString());

			conn = WrkSpcTeamUtils.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					edgeProblemId = AmtCommonUtils.getTrimStr(rs.getString("problemid"));
					projectId = AmtCommonUtils.getTrimStr(rs.getString("projectid"));
					fileno = rs.getInt("fileno");
					fileName = AmtCommonUtils.getTrimStr(rs.getString("filename"));
					fileDesc = AmtCommonUtils.getTrimStr(rs.getString("filedesc"));
					statusFlag = AmtCommonUtils.getTrimStr(rs.getString("statusflag"));
					lastUserId = AmtCommonUtils.getTrimStr(rs.getString("lastuser"));
					inStream = rs.getBinaryStream("filedata");
					filesize = inStream.available();
					
					////browser o/p
					out.println("/////////////////////////////File Counter ::: "+count +"/////////////////////////////");
					out.println("<br />");
					out.println(" Edge problem Id :::  "+edgeProblemId);
					out.println("<br />");
					out.println(" Project Id :::  "+projectId);
					out.println("<br />");
					out.println(" fileno :::  "+fileno);
					out.println("<br />");
					out.println(" fileName :::  "+fileName);
					out.println("<br />");
					out.println(" fileDesc :::  "+fileDesc);
					out.println("<br />");
					out.println(" filesize :::  "+filesize);
					out.println("<br />");
					out.println(" statusFlag :::  "+statusFlag);
					out.println("<br />");
					out.println(" lastUserId :::  "+lastUserId);
					out.println("<br />");
					
					/////log o/p
					logger.debug("File Num ::: "+count);
					logger.debug(" Edge problem Id :::  "+edgeProblemId);
					logger.debug(" Project Id :::  "+projectId);
					logger.debug(" fileno :::  "+fileno);
					logger.debug(" fileName :::  "+fileName);
					logger.debug(" fileDesc :::  "+fileDesc);
					logger.debug(" filesize :::  "+filesize);
					logger.debug(" statusFlag :::  "+statusFlag);
					logger.debug(" lastUserId :::  "+lastUserId);
					
					

					iDocId = fileUtils.getIssuesDoc(projectId, edgeProblemId,"ISMGTFILES");
					out.println(" iDocId :::  "+iDocId);
					out.println("<br />");
					out.println("<br />");
					logger.debug(" iDocId :::  "+iDocId);

					ETSIssueAttach attach = new ETSIssueAttach();
					attach.setEdgeProblemId(edgeProblemId);
					attach.setFileDesc(fileDesc);
					attach.setFileName(fileName);
					attach.setFileNewFlag(statusFlag);
					attach.setFileSize(filesize);
					attach.setFileData(getFileData(inStream, filesize));
					attach.setUser(lastUserId);

					boolean success = false;
					
					try {
					

					success = fileUtils.attachIssueFile(projectId, statusFlag, iDocId, attach);
					
					}
					
					catch(Exception ex) {
						
						out.println("Exception file loading file data");
						logger.error("Exception file loading file data",ex);
						ex.printStackTrace();
					}
					
					out.println(" file migrn success :::  "+success);
					out.println("<br />");
					out.println("<br />");
					logger.debug(" FILE MIGRN SUCCESS :::  <b>"+success+"</b>");

					count++;

				} //end of while
			}

		} catch (SQLException se) {

			logger.error("SQL exception while attach migrn",se);
			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in DocMigrtnDAO", "DOCMIGRN");

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			logger.error("Exception while attach migrn",ex);
			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building DocMigrtnDAO", "DOCMIGRN");

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
			
			out.flush();
			out.close();

		}

	}

	public byte[] getFileData(InputStream inStream, int inStreamAvail) {

		byte[] temp = new byte[inStreamAvail];
		int amountread = 0;
		int offset = 0;
		
		while (amountread < inStreamAvail) {
			try {
				int readnow = inStream.read(temp, offset, inStreamAvail - amountread);
				
				if (readnow > 0) {
					amountread += readnow;
					offset += readnow;
				}

			} catch (Exception e) {
				logger.error("error while reading the file data",e);
				e.printStackTrace();
			}

		}

		return temp;
	}
} //end of class
