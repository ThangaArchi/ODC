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



package oem.edge.ets.fe.survey;

/**
 * @author: Sathish
 */

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.common.DatesArithmatic;
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

public class ETSSurveyActionPlanServlet extends javax.servlet.http.HttpServlet {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.5";
	
	private static Log logger = EtsLogger.getLogger(ETSSurveyActionPlanServlet.class);


	/**
	 * @see java.lang.Object#Object()
	 */
	public ETSSurveyActionPlanServlet() {
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

			String sYear = request.getParameter("survey_year");
			if (sYear == null || sYear.trim().equals("")) {
				sYear = "";
			} else {
				sYear = sYear.trim();
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
			//header.setHeader("");
			header.setPageTitle(prop.getAppName() + " - Details");
			out.println(header.printPopupHeader());
			
			ETSUtils.popupHeaderLeft("Action plan","",out);
			
			out.println("<form name=\"SurveyActionPlan\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSSurveyActionPlanServlet.wss\">");

			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");

			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"survey_year\" value=\"" + sYear + "\" />");

			if (!sSubmitFlag.trim().equals("")) {
				
				String sError = validateActionPlan(request);
				 
				if (!sError.trim().equalsIgnoreCase("")) {
					displayActionPlan(conn,request,es,out,proj,sYear,sError);
				} else { 
					updateActionPlanDetails(conn,proj,out,request,es);
					out.println("<br /><br />");
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"30\" align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" /></td><td headers=\"\"  align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\">Ok</a></a></td></tr></table>");
					out.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");
				}
			} else {
				displayActionPlan(conn,request,es,out,proj,sYear,"");
			}

			out.println("</form>");
			//out.println("<br /><br />");

			ETSUtils.popupHeaderRight(out);

			out.println(header.printPopupFooter());


		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			e.printStackTrace();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			e.printStackTrace();
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
	private void updateActionPlanDetails(Connection conn, ETSProj proj, PrintWriter out, HttpServletRequest request, EdgeAccessCntrl es) throws SQLException, Exception {
		
		try {
			
			String sProjectID = ETSUtils.checkNull(request.getParameter("proj"));
			String sYear = ETSUtils.checkNull(request.getParameter("survey_year"));
			String sPlanOwner = ETSUtils.checkNull(request.getParameter("plan_owner"));
			
			String sActionDay = ETSUtils.checkNull(request.getParameter("action_day"));
			String sActionMonth = ETSUtils.checkNull(request.getParameter("action_month"));
			String sActionYear = ETSUtils.checkNull(request.getParameter("action_year"));

			Timestamp timeAction = Timestamp.valueOf(sActionYear + "-" + sActionMonth + "-" + sActionDay + " 00:00:00.000000000");
			
			ETSSurveyActionPlan plan = new ETSSurveyActionPlan();
			plan.setCompany(proj.getCompany());
			plan.setLastTimestamp(new Timestamp(System.currentTimeMillis()));
			plan.setLastUserId(es.gIR_USERN);
			plan.setPlanDueDate(timeAction);
			plan.setPlanOwnerId(sPlanOwner);
			plan.setYear(sYear);
			
			ETSSurveyDAO.updateActionPlanOwner(conn,plan);
						
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\"  align=\"left\"><b>The action plan details has been updated successfully.</b></td>");
			out.println("</table>");
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
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
	private static void displayActionPlan(Connection con, HttpServletRequest request, EdgeAccessCntrl es,PrintWriter out, ETSProj proj, String sYear, String sError) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		try {

			String sActionPlanOwner = "";
			String sActionDay = "";
			String sActionMonth = "";
			String sActionYear = "";
			
			if (!sError.trim().equalsIgnoreCase("")) {
				

				sActionDay = ETSUtils.checkNull(request.getParameter("action_day"));
				sActionMonth = ETSUtils.checkNull(request.getParameter("action_month"));
				sActionYear = ETSUtils.checkNull(request.getParameter("action_year"));
				
				sActionPlanOwner = ETSUtils.checkNull(request.getParameter("plan_owner"));
				
			} else {
				
				ETSSurveyActionPlan plan = ETSSurveyDAO.getSurveyActionPlan(con,sYear,proj.getCompany());
				
				sActionPlanOwner = plan.getPlanOwnerId();
				
				if (plan.getPlanDueDate() != null) {
					sActionDay = plan.getPlanDueDate().toString().substring(8, 10);
					sActionMonth = plan.getPlanDueDate().toString().substring(5, 7);
					sActionYear = plan.getPlanDueDate().toString().substring(0, 4);
				} else {
					String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");
					sActionMonth = sTodaysDate.substring(0, 2);
					sActionDay = sTodaysDate.substring(3, 5);
					sActionYear = sTodaysDate.substring(6, 10);
				}
			}
			
			if (!sError.trim().equalsIgnoreCase("")) {
				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\"  align=\"left\"><span style=\"color: #ff3333\">" + sError + "</span></td>");
				out.println("</table>");
			}
			
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Action plan details</td>");
			out.println("</tr>");
			out.println("<tr><td headers=\"\"  width=\"100%\">");

			out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
			out.println("<tr valign=\"middle\">");
			out.println("<td headers=\"\"  style=\"background-color: #ffffff;color: #000000;\" align=\"center\" >");
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b><label for=\"client\">Action plan owner:</label></b></td>");
			out.println("<td headers=\"\"  align=\"left\">" + displayInviteesAsSelect(con,"plan_owner","client",proj.getProjectId(),sActionPlanOwner,es.gIR_USERN) + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b><label for=\"label_adate\">Action plan due date:</label></b></td>");
			out.println("<td headers=\"\"  align=\"left\">");
			showDate(out,sActionMonth,sActionDay,sActionYear,"action_month","action_day","action_year","label_adate");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");

			out.println("</table>");

			out.println("</td></tr>");
			out.println("</table>");

			out.println("</td></tr>");
			out.println("</table>");

			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\"  align=\"left\"><b>Click on \"Submit\" to update action plan details. <br /><span style=\"color: #ff3333\">Please wait for confirmation page to be displayed and click on \"Ok\" to close the window.</span></b></td>");
			out.println("</table>");

			out.println("<br />");			
			
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
			out.println("<td headers=\"\"  align=\"left\">");
			out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\" >&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
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

	private static String displayInviteesAsSelect(Connection con, String sSelectName, String sLabelId, String sProjectId, String sInviteesList, String sLoggedInID) throws SQLException, Exception {
	
		StringBuffer out = new StringBuffer("");
	
		try {
	
			String sAvailable = "," + sInviteesList + ",";
			
			out.append("<select style=\"width:250px\" width=\"250px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");
			out.append("<option value=\"\" selected=\"selected\">Please select a user</option>");
	
			Vector vMembers = ETSDatabaseManager.getProjMembers(sProjectId,con);
	
			if (vMembers != null && vMembers.size() > 0) {
	
				for (int i = 0; i < vMembers.size(); i++) {
	
					ETSUser user = (ETSUser) vMembers.elementAt(i);
					
					if (!ETSDatabaseManager.hasProjectPriv(user.getUserId(),sProjectId,Defines.VISITOR,con) && !ETSDatabaseManager.hasProjectPriv(user.getUserId(),sProjectId,Defines.CLIENT,con)) {
						
						if (sAvailable.indexOf("," + user.getUserId().trim() + ",") >= 0)  {
							out.append("<option value=\"" + user.getUserId().trim() + "\" selected=\"selected\">" + ETSUtils.getUsersName(con,user.getUserId().trim()) + "</option>");
						} else {
							out.append("<option value=\"" + user.getUserId().trim() + "\" >" + ETSUtils.getUsersName(con,user.getUserId().trim()) + "</option>");
						}
	
					}
				}
			}
	
			out.append("</select>");
	
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	
		return out.toString();
	
	}

	private String validateActionPlan(HttpServletRequest request) throws Exception {
	
		StringBuffer sError = new StringBuffer("");
	
		try {
	
			String sPlanOwner = ETSUtils.checkNull(request.getParameter("plan_owner"));
			if (sPlanOwner.equals("") && sPlanOwner.equals("")) {
				sError.append("<b>Action plan owner</b> is not set. Please choose the action plan owner.<br />");
			}
			
			String sCalMonth = ETSUtils.checkNull(request.getParameter("action_month"));
			String sCalDay = ETSUtils.checkNull(request.getParameter("action_day"));
			String sCalYear = ETSUtils.checkNull(request.getParameter("action_year"));

			int month = Integer.parseInt(sCalMonth.trim());
			int day = Integer.parseInt(sCalDay.trim());
			int year = Integer.parseInt(sCalYear.trim());

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR,year);
			cal.set(Calendar.MONTH,month -1);
			int iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			int iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);


			if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
				cal.set(Calendar.DAY_OF_MONTH,day);
			} else{
				sError.append("<b>Action plan due date</b> is not valid. Please set the action plan due date date correctly.<br />");
			}
						
		} catch (Exception e) {
			throw e;
		}
	
		return sError.toString();
	
	}

	private static void showDate(PrintWriter out, String m, String d, String yy, String mname, String dname, String yname, String sId) {
	
		String mon[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);
	
		out.println("<table summary=\"\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td headers=\"\"  align=\"left\" width=\"40\"><label for=\"month\">Month</label><br />");
		out.println("<select id=\"" + sId + " month\" class=\"iform\" name=\"" + mname + "\">");
	
		for (int k = 1; k < 13; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (m.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + mon[Integer.parseInt(qq) - 1] + "</option>");
		}
	
		out.println("</select></td><td headers=\"\"  width=\"30\" align=\"left\">&nbsp;&nbsp;<label for=\"day\">Day</label><br /><select id=\"" + sId + " day\" class=\"iform\" name=\"" + dname + "\">");
		for (int k = 1; k < 32; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (d.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}
	
		out.println("</select></td><td headers=\"\"   align=\"left\">&nbsp;&nbsp;<label for=\"year\">Year</label><br /><select id=\"" + sId + " year\" class=\"iform\" name=\"" + yname + "\">");
		for (int k = 0; k < 20; k++) {
			String qq = "" + (y + k - 2);
			out.println("<option value=\"" + qq + "\"");
			if (yy.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}
	
		out.println("</select>");
		out.println("</td></tr></table>");
	
	}

}
