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



package oem.edge.ets.fe.setmet;

/**
 * @author: Sathish
 */

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSSetMetCommentsServlet extends javax.servlet.http.HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.8";

	private static Log logger = EtsLogger.getLogger(ETSSetMetCommentsServlet.class);

	/**
	 * @see java.lang.Object#Object()
	 */
	public ETSSetMetCommentsServlet() {
		super();
	}


	/**
	 * Method getClassVersion.
	 * @return String
	 */
	public static String getClassVersion() {
		return CLASS_VERSION;
	}


	/**
	 * Method service.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	    response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		Connection conn = null;
		String Msg = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		Hashtable params;

		int topCatId = 0;
		ETSCat topCat;
		ETSCat subCat;
		ETSCat currentCat;
		ETSUser user;
		String sLink;

		try {

			conn = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, conn)) {
				return;
			}

			String sProjId = request.getParameter("proj");
			if (sProjId == null || sProjId.trim().equals("")) {
				sProjId = "";
			} else {
				sProjId = sProjId.trim();
			}

			String sOp = request.getParameter("op");
			if (sOp == null || sOp.trim().equals("")) {
				sOp = "";
			} else {
				sOp = sOp.trim();
			}

			String sSetMetID = request.getParameter("setmet");
			if (sSetMetID == null || sSetMetID.trim().equals("")) {
				sSetMetID = "";
			} else {
				sSetMetID = sSetMetID.trim();
			}

			String sQuestionID = request.getParameter("question");
			if (sQuestionID == null || sQuestionID.trim().equals("")) {
				sQuestionID = "";
			} else {
				sQuestionID = sQuestionID.trim();
			}

			String sSeqNo = request.getParameter("seqno");
			if (sSeqNo == null || sSeqNo.trim().equals("")) {
				sSeqNo = "";
			} else {
				sSeqNo = sSeqNo.trim();
			}

			String sSubmitFlag = request.getParameter("image_submit.x");
			if (sSubmitFlag == null || sSubmitFlag.trim().equals("")) {
				sSubmitFlag = "";
			} else {
				sSubmitFlag = sSubmitFlag.trim();
			}


			ETSProj proj = ETSUtils.getProjectDetails(conn,sProjId);
			
			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

			params = ETSUtils.getServletParameters(request);

			PopupHeaderFooter header = new PopupHeaderFooter();
			header.setPageTitle(prop.getAppName() + " - Details");
			out.println(header.printPopupHeader());
			
			ETSUtils.popupHeaderLeft("Comments","Add/Edit comments",out);
			
			out.println("<form name=\"SetMetExpectComments\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSSetMetCommentsServlet.wss\">");

			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">");
			out.println(" function ok_close(){");
			out.println("   URL = unescape(window.opener.location.href);");
			out.println("   var iPos = URL.indexOf('#');");
			out.println("   if (iPos == '-1') { ");
			out.println("       URL = URL;");
			out.println("   } else { ");
			out.println("       URL = URL.substr(0,iPos);");
			out.println("   } ");
			out.println("   window.opener.location.reload();");
			out.println("   window.opener.location.href = URL + '#question_" + sQuestionID + "';");
			out.println("   self.close();");
			out.println(" }</script>");
			
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"op\" value=\"" + sOp + "\" />");
			out.println("<input type=\"hidden\" name=\"setmet\" value=\"" + sSetMetID + "\" />");
			out.println("<input type=\"hidden\" name=\"question\" value=\"" + sQuestionID + "\" />");
			out.println("<input type=\"hidden\" name=\"seqno\" value=\"" + sSeqNo + "\" />");

			if (!sSubmitFlag.trim().equals("")) {

				boolean bSuccess = updateComments(conn,out,request,es);

				out.println("<br /><br />");
				
				if (bSuccess) {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" align=\"left\" ><b>Your comments has been updated successfully.</b></td></tr></table>");
				} else {
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" align=\"left\" ><b><span style=\"color:#ff3333\">There was a system error when uppdating your comments. Please try again. If the problem persists, please contact your client care advocate.</span></b></td></tr></table>");
				}

			    out.println("<br /><br />");
			    out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" width=\"30\" align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" /></td><td headers=\"\" align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\">Ok</a></a></td></tr></table>");
			    out.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");

			} else {
				displayExpectationComments(conn,es,out,proj,sProjId,sOp,sSetMetID,sQuestionID,sSeqNo);
			}

			out.println("</form>");
			//out.println("<br /><br />");

			ETSUtils.popupHeaderRight(out);

			out.println(header.printPopupFooter());


		} catch (SQLException e) {
			logger.error(this,e);
		} catch (Exception e) {
			logger.error(this,e);
		} finally {
			ETSDBUtils.close(conn);
			out.flush();
			out.close();
		}
	}

	/**
	 * @param conn
	 * @param out
	 * @param request
	 * @param es
	 */
	private boolean updateComments(Connection conn, PrintWriter out, HttpServletRequest request, EdgeAccessCntrl es) throws SQLException, Exception {
		
		boolean bSuccess = false;
		
		try {
			
							
			String sProjectID = ETSUtils.checkNull(request.getParameter("proj"));
			String sSetMetID = ETSUtils.checkNull(request.getParameter("setmet"));
			String sQuestionNo = ETSUtils.checkNull(request.getParameter("question"));
			String sSeqNo = ETSUtils.checkNull(request.getParameter("seqno"));
			String sComments = ETSUtils.checkNull(request.getParameter("comments"));
			
			ETSSetMetExpectation exp = new ETSSetMetExpectation();
			
			exp.setSetMetID(sSetMetID);
			exp.setProjectID(sProjectID);
			exp.setQuestionID(Integer.parseInt(sQuestionNo));
			exp.setSeqNo(Integer.parseInt(sSeqNo));
			exp.setComments(sComments);
			exp.setLastUserID(es.gIR_USERN);
			exp.setLastTimestamp(null);
			
			ETSSetMetDAO.updateExpectationComments(conn,exp);
			
			bSuccess = true;
			
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return bSuccess;
		
	}


	/**
	 * Method displayCalendarEntry.
	 * @param con
	 * @param out
	 * @param sProjectId
	 * @param sCalendarType
	 * @param sCalendarId
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayExpectationComments(Connection con, EdgeAccessCntrl es,PrintWriter out, ETSProj proj, String sProjectId, String sOp, String sSetMetID, String sQuestionID, String sSeqNo) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		try {
			
			String sComments = "";
			
			ETSSetMetExpectation exp = ETSSetMetDAO.getSetMetExpectation(con,sProjectId,sSetMetID,Integer.parseInt(sQuestionID),Integer.parseInt(sSeqNo));
			sComments = exp.getComments();
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"> ");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" valign=\"top\" ><b>Client comments:</b> " + exp.getExpectDesc() + "</td>");
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<br /><br />");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"> ");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" valign=\"top\" ><label for=\"exp\"><b>Additional comments:</b></label></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><textarea name=\"comments\" cols=\"80\" rows=\"5\"  class=\"iform\" maxlength=\"1000\" id=\"exp\">" + sComments + "</textarea></td>");
			out.println("</tr>");
			
			out.println("</table>");
			
			out.println("<br /><br />");
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Click on \"Submit\" to update additional comments. <br /><span style=\"color: #ff3333\">Please wait for confirmation page to be displayed and click on \"Ok\" to close the window.</span></b></td>");
			out.println("</table>");

			out.println("<br />");
			
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\" width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
			out.println("<td headers=\"\" align=\"left\">");
			out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\">&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
			out.println("  <noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
			out.println("</td></tr></table>");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

	}

}
