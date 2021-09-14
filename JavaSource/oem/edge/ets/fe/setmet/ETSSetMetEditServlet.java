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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
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
import oem.edge.ets.fe.wspace.ETSWorkspaceDAO;

import org.apache.commons.logging.Log;

public class ETSSetMetEditServlet extends javax.servlet.http.HttpServlet {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.13";
	
	private static Log logger = EtsLogger.getLogger(ETSSetMetEditServlet.class);


	/**
	 * @see java.lang.Object#Object()
	 */
	public ETSSetMetEditServlet() {
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

			String sSetMetID = request.getParameter("setmet");
			if (sSetMetID == null || sSetMetID.trim().equals("")) {
				sSetMetID = "";
			} else {
				sSetMetID = sSetMetID.trim();
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
			
			ETSUtils.popupHeaderLeft("Edit demographics","",out);
			
			out.println("<form name=\"SetMetEditDemo\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss\">");

			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");

			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"setmet\" value=\"" + sSetMetID + "\" />");

			if (!sSubmitFlag.trim().equals("")) {
				
				String sError = validateCreateSetMetInterview(request);
				 
				if (!sError.trim().equalsIgnoreCase("")) {
					displaySetMetEdit(conn,request,es,out,proj,sSetMetID,sError);
				} else { 
					updateSetMetAndMembers(conn,out,request,es);
					out.println("<br /><br />");
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"30\" align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" /></td><td headers=\"\"  align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\">Ok</a></a></td></tr></table>");
					out.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");
				}
			} else {
				displaySetMetEdit(conn,request,es,out,proj,sSetMetID,"");
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
	private void updateSetMetAndMembers(Connection conn, PrintWriter out, HttpServletRequest request, EdgeAccessCntrl es) throws SQLException, Exception {
		
		try {
			
			String sProjectID = ETSUtils.checkNull(request.getParameter("proj"));
			String sSetMetID = ETSUtils.checkNull(request.getParameter("setmet"));
			String sPrincipal = ETSUtils.checkNull(request.getParameter("setmet_principal"));
			String sClient = ETSUtils.checkNull(request.getParameter("setmet_client"));
			String sClientName = ETSUtils.checkNull(request.getParameter("clientname"));
			String sPM = ETSUtils.checkNull(request.getParameter("setmet_pm"));
			
			String sPrincipalMonthO = ETSUtils.checkNull(request.getParameter("original_principal_month"));
			String sPrincipalDayO = ETSUtils.checkNull(request.getParameter("original_principal_day"));
			String sPrincipalYearO = ETSUtils.checkNull(request.getParameter("original_principal_year"));
				
			String sActionMonthO = ETSUtils.checkNull(request.getParameter("original_action_month"));
			String sActionDayO = ETSUtils.checkNull(request.getParameter("original_action_day"));
			String sActionYearO = ETSUtils.checkNull(request.getParameter("original_action_year"));

			String sActionImplementMonthO = ETSUtils.checkNull(request.getParameter("original_implement_month"));			
			String sActionImplementDayO = ETSUtils.checkNull(request.getParameter("original_implement_day"));
			String sActionImplementYearO = ETSUtils.checkNull(request.getParameter("original_implement_year"));
				
//			String sAfterActionMonthO = ETSUtils.checkNull(request.getParameter("original_after_month"));
//			String sAfterActionDayO = ETSUtils.checkNull(request.getParameter("original_after_day"));
//			String sAfterActionYearO = ETSUtils.checkNull(request.getParameter("original_after_year"));
			
			String SupressClient1 = ETSUtils.checkNull(request.getParameter("supress_client1"));
			String SupressClient2 = ETSUtils.checkNull(request.getParameter("supress_client2"));

			String sSupress1 = "0";
			String sSupress2 = "0";
			String sSupress3 = "0";
			String sSupress4 = "0";
			String sSupress5 = "0";
			String sSupress6 = "0";
			
			if (SupressClient1.equalsIgnoreCase("Y")) {
				sSupress2 = "1";
			}

			if (SupressClient2.equalsIgnoreCase("Y")) {
				sSupress6 = "1";
			}
			
			ETSSetMet setmet = new ETSSetMet();

			setmet.setClientIRID(sClient);
			setmet.setClientName(sClientName);
			
			setmet.setProjectID(sProjectID);
			setmet.setSetMetBSE(sPrincipal); // store the principal here.
			setmet.setSetMetID(sSetMetID);
			setmet.setSetMetPractice(sPM); // store the program manager here.
			setmet.setSupressFlags(sSupress1 + sSupress2 + sSupress3 + sSupress4 + sSupress5 + sSupress6);
			
			ETSSetMetDAO.updateSetMetDemographics(conn,setmet);
			
			String sPrincipalDay = ETSUtils.checkNull(request.getParameter("principal_day"));
			String sPrincipalMonth = ETSUtils.checkNull(request.getParameter("principal_month"));
			String sPrincipalYear = ETSUtils.checkNull(request.getParameter("principal_year"));

			String sActionDay = ETSUtils.checkNull(request.getParameter("action_day"));
			String sActionMonth = ETSUtils.checkNull(request.getParameter("action_month"));
			String sActionYear = ETSUtils.checkNull(request.getParameter("action_year"));

			String sActionImplementDay = ETSUtils.checkNull(request.getParameter("action_implement_day"));
			String sActionImplementMonth = ETSUtils.checkNull(request.getParameter("action_implement_month"));
			String sActionImplementYear = ETSUtils.checkNull(request.getParameter("action_implement_year"));

//			String sAfterActionDay = ETSUtils.checkNull(request.getParameter("after_day"));
//			String sAfterActionMonth = ETSUtils.checkNull(request.getParameter("after_month"));
//			String sAfterActionYear = ETSUtils.checkNull(request.getParameter("after_year"));
			


			// steps for the setmet
			// blank						Interview not completed
			//SETMET_CLIENT_INTERVIEW		Interview completed, waiting for client approval
			//SETMET_CLIENT_APPROVED		client approved interview
			//SETMET_PRINCIPAL_APPROVED		principal approved
			//SETMET_ACTION_PLAN			action plan created
			//SETMET_ACTION_PLAN_APPROVED	action plan implemented
			//SETMET_CLOSE					after action review
			//SETMET_FINAL_RATING			set met completed.
			
			
			String sPrincipalFlag = ETSUtils.checkNull(request.getParameter("principal_flag"));
			String sActionFlag = ETSUtils.checkNull(request.getParameter("action_flag"));
			String sActionImplementFlag = ETSUtils.checkNull(request.getParameter("action_implement_flag"));
			String sAfterActionFlag = ETSUtils.checkNull(request.getParameter("after_flag"));
			
			int iCount = 0;
			
			if (sPrincipalFlag.equalsIgnoreCase("Y")) {
				if (!sPrincipalYear.equalsIgnoreCase(sPrincipalYearO) || !sPrincipalMonth.equalsIgnoreCase(sPrincipalMonthO) || !sPrincipalDay.equalsIgnoreCase(sPrincipalDayO)) {
					Timestamp timePrincipal = Timestamp.valueOf(sPrincipalYear + "-" + sPrincipalMonth + "-" + sPrincipalDay + " 00:00:00.000000000");
					iCount = ETSSetMetDAO.updateSetMetNofification(conn,sSetMetID,sProjectID,Defines.SETMET_PRINCIPAL_APPROVED,timePrincipal);
				}
			}
			
			if (sActionFlag.equalsIgnoreCase("Y")) {
				if (!sActionDay.equalsIgnoreCase(sActionDayO) || !sActionMonth.equalsIgnoreCase(sActionMonthO) || !sActionYear.equalsIgnoreCase(sActionYearO)) {
					Timestamp timeAction = Timestamp.valueOf(sActionYear + "-" + sActionMonth + "-" + sActionDay + " 00:00:00.000000000");
					iCount = ETSSetMetDAO.updateSetMetNofification(conn,sSetMetID,sProjectID,Defines.SETMET_ACTION_PLAN,timeAction);
				}
			}
			
//			if (sActionImplementFlag.equalsIgnoreCase("Y")) {	
//				if (!sActionImplementDay.equalsIgnoreCase(sActionImplementDayO) || !sActionImplementMonth.equalsIgnoreCase(sActionImplementMonthO) || !sActionImplementYear.equalsIgnoreCase(sActionImplementYearO)) {		
//					Timestamp timeActionImplement = Timestamp.valueOf(sActionImplementYear + "-" + sActionImplementMonth + "-" + sActionImplementDay + " 00:00:00.000000000");
//					iCount = ETSSetMetDAO.updateSetMetNofification(conn,sSetMetID,sProjectID,Defines.SETMET_ACTION_PLAN_APPROVED,timeActionImplement);
//				}
//			}
//			if (sAfterActionFlag.equalsIgnoreCase("Y")) {
//				if (!sAfterActionDay.equalsIgnoreCase(sAfterActionDayO) || !sAfterActionMonth.equalsIgnoreCase(sAfterActionMonthO) || !sAfterActionYear.equalsIgnoreCase(sAfterActionYearO)) {			
//					Timestamp timeAfterAction = Timestamp.valueOf(sAfterActionYear + "-" + sAfterActionMonth + "-" + sAfterActionDay + " 00:00:00.000000000");
//					iCount = ETSSetMetDAO.updateSetMetNofification(conn,sSetMetID,sProjectID,Defines.SETMET_CLOSE,timeAfterAction);
//				}
//			}
						
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\"  align=\"left\"><b>The Set/Met demographics has been updated successfully.</b></td>");
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
	private static void displaySetMetEdit(Connection con, HttpServletRequest request, EdgeAccessCntrl es,PrintWriter out, ETSProj proj, String sSetMetID, String sError) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;

		try {
			
			String sPrincipal = "";
			String sClient = "";
			String sPM = "";
			String sTitle = "";	
			String sTeam = "";
			String sInterviewDate = "";
			
			String sPrincipalFlag = "";
			String sPrincipalDay = "";
			String sPrincipalMonth = "";
			String sPrincipalYear = "";

			String sActionFlag = "";
			String sActionDay = "";
			String sActionMonth = "";
			String sActionYear = "";
			
//			String sActionImplementFlag = "";
//			String sActionImplementDay = "";
//			String sActionImplementMonth = "";
//			String sActionImplementYear = "";

			String sAfterActionFlag = "";
			String sAfterActionDay = "";
			String sAfterActionMonth = "";
			String sAfterActionYear = "";

			String sPrincipalDayO = "";
			String sPrincipalMonthO = "";
			String sPrincipalYearO = "";

			String sActionDayO = "";
			String sActionMonthO = "";
			String sActionYearO = "";
			
//			String sActionImplementDayO = "";
//			String sActionImplementMonthO = "";
//			String sActionImplementYearO = "";

			String sAfterActionDayO = "";
			String sAfterActionMonthO = "";
			String sAfterActionYearO = "";
			
			String sCurrentStep = "";
			
			String sPrincipalActualComplete = "";
			String sActionActualComplete = "";
			String sActionActualImplement = "";
			String sAfterActionActual = "";
			
			String SupressClient1 = "";
			String SupressClient2 = "";
			
			String sClientName = "";
			
			if (!sError.trim().equalsIgnoreCase("")) {
				
				sTitle = ETSUtils.checkNull(request.getParameter("title"));
				sPrincipal = ETSUtils.checkNull(request.getParameter("setmet_principal"));
				sClient = ETSUtils.checkNull(request.getParameter("setmet_client"));
				sPM = ETSUtils.checkNull(request.getParameter("setmet_pm"));
				sInterviewDate = ETSUtils.checkNull(request.getParameter("interview_date"));
				
				sPrincipalDay = ETSUtils.checkNull(request.getParameter("principal_day"));
				sPrincipalMonth = ETSUtils.checkNull(request.getParameter("principal_month"));
				sPrincipalYear = ETSUtils.checkNull(request.getParameter("principal_year"));

				sActionDay = ETSUtils.checkNull(request.getParameter("action_day"));
				sActionMonth = ETSUtils.checkNull(request.getParameter("action_month"));
				sActionYear = ETSUtils.checkNull(request.getParameter("action_year"));

//				sActionImplementDay = ETSUtils.checkNull(request.getParameter("action_implement_day"));
//				sActionImplementMonth = ETSUtils.checkNull(request.getParameter("action_implement_month"));
//				sActionImplementYear = ETSUtils.checkNull(request.getParameter("action_implement_year"));

//				sAfterActionDay = ETSUtils.checkNull(request.getParameter("after_day"));
//				sAfterActionMonth = ETSUtils.checkNull(request.getParameter("after_month"));
//				sAfterActionYear = ETSUtils.checkNull(request.getParameter("after_year"));
				
				sPrincipalFlag = ETSUtils.checkNull(request.getParameter("principal_flag"));
				sActionFlag = ETSUtils.checkNull(request.getParameter("action_flag"));
				//sActionImplementFlag = ETSUtils.checkNull(request.getParameter("action_implement_flag"));
				sAfterActionFlag = ETSUtils.checkNull(request.getParameter("after_flag"));
				
				sCurrentStep = ETSUtils.checkNull(request.getParameter("current_step"));
				
				sPrincipalMonthO = ETSUtils.checkNull(request.getParameter("original_principal_month"));
				sPrincipalDayO = ETSUtils.checkNull(request.getParameter("original_principal_day"));
				sPrincipalYearO = ETSUtils.checkNull(request.getParameter("original_principal_year"));
				
				sActionMonthO = ETSUtils.checkNull(request.getParameter("original_action_month"));
				sActionDayO = ETSUtils.checkNull(request.getParameter("original_action_day"));
				sActionYearO = ETSUtils.checkNull(request.getParameter("original_action_year"));

//				sActionImplementMonthO = ETSUtils.checkNull(request.getParameter("original_implement_month"));			
//				sActionImplementDayO = ETSUtils.checkNull(request.getParameter("original_implement_day"));
//				sActionImplementYearO = ETSUtils.checkNull(request.getParameter("original_implement_year"));
				
//				sAfterActionMonthO = ETSUtils.checkNull(request.getParameter("original_after_month"));
//				sAfterActionDayO = ETSUtils.checkNull(request.getParameter("original_after_day"));
//				sAfterActionYearO = ETSUtils.checkNull(request.getParameter("original_after_year"));
				
				sPrincipalActualComplete = ETSUtils.checkNull(request.getParameter("actual_principal"));
				sActionActualComplete = ETSUtils.checkNull(request.getParameter("actual_action"));
				sActionActualImplement = ETSUtils.checkNull(request.getParameter("actual_implement"));
//				sAfterActionActual = ETSUtils.checkNull(request.getParameter("actual_after"));
				
				SupressClient1 = ETSUtils.checkNull(request.getParameter("supress_client1"));
				SupressClient2 = ETSUtils.checkNull(request.getParameter("supress_client2"));
				
				sClientName = ETSUtils.checkNull(request.getParameter("clientname"));

			} else {
				
				ETSSetMet setmet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
				// removed set met team members in 5.2.1
				//Vector vTeamMembers = ETSSetMetDAO.getSetMetTeamMembers(con,proj.getProjectId(),sSetMetID);
				
				String SupressFlags = setmet.getSupressFlags();
				
				if (SupressFlags == null || SupressFlags.equalsIgnoreCase("")) {
					SupressFlags = "000000";
				}
				
				if (SupressFlags.substring(1,2).equalsIgnoreCase("1")) {
					SupressClient1 = "Y";
				}

				if (SupressFlags.substring(5,6).equalsIgnoreCase("1")) {
					SupressClient2 = "Y";
				}
				
				Vector vNotifications = ETSSetMetDAO.getSetMetNotifications(con,proj.getProjectId(),sSetMetID);
				
				Vector vStates = setmet.getSetMetStates();

				if (vStates != null) {
					// get the last to get the current state the set met is in...
					if (vStates.size() > 0) {
						ETSSetMetActionState state = (ETSSetMetActionState) vStates.elementAt(vStates.size()-1);
						sCurrentStep = state.getStep();
					}
				}
				
				if (vStates != null) {

					boolean bAvailable = false;

					for (int i = 0; i < vStates.size(); i++) {

						ETSSetMetActionState state = (ETSSetMetActionState) vStates.elementAt(i);

						String sState = state.getStep();
						Timestamp tDate = state.getActionDate();
						String sBy = state.getActionBy();

						if (sState.equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
							sPrincipalActualComplete = ETSUtils.formatDate(state.getActionDate());
						} else if (sState.equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
							sActionActualComplete = ETSUtils.formatDate(state.getActionDate());
//						} else if (sState.equalsIgnoreCase(Defines.SETMET_ACTION_PLAN_APPROVED)) {
//							sActionActualImplement = ETSUtils.formatDate(state.getActionDate());
//						} else if (sState.equalsIgnoreCase(Defines.SETMET_CLOSE)) {
//							sAfterActionActual = ETSUtils.formatDate(state.getActionDate());
						} 
					}
				}				
				
				sPrincipal = setmet.getSetMetBSE();
				sPM = setmet.getSetMetPractice();
				sClient = setmet.getClientIRID();
				sClientName = setmet.getClientName();
				sTitle = setmet.getSetMetName();
				sInterviewDate = setmet.getMeetingDate().toString().substring(5, 7) + "/" + setmet.getMeetingDate().toString().substring(8, 10) + "/" + setmet.getMeetingDate().toString().substring(0, 4) ;

				if (vNotifications != null && vNotifications.size() > 0) {
					
					for (int i = 0; i < vNotifications.size(); i++) {
						ETSSetMetNotify notify = (ETSSetMetNotify) vNotifications.elementAt(i);
						
						if (notify.getStep().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
							if (sCurrentStep.equalsIgnoreCase("") || sCurrentStep.equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW) || sCurrentStep.equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED)) {
								sPrincipalFlag = "Y";
							} else {
								sPrincipalFlag = "N";
							}
							sPrincipalDay = notify.getDueDate().toString().substring(8, 10);
							sPrincipalMonth = notify.getDueDate().toString().substring(5, 7);
							sPrincipalYear = notify.getDueDate().toString().substring(0, 4);
							break;
						}
					}
					
					for (int i = 0; i < vNotifications.size(); i++) {
						
						ETSSetMetNotify notify = (ETSSetMetNotify) vNotifications.elementAt(i);
						
						if (notify.getStep().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
							if (sCurrentStep.equalsIgnoreCase("") || sCurrentStep.equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW) || sCurrentStep.equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED) || sCurrentStep.equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
								sActionFlag = "Y";
							} else {
								sActionFlag = "N";
							}
							sActionDay = notify.getDueDate().toString().substring(8, 10);
							sActionMonth = notify.getDueDate().toString().substring(5, 7);
							sActionYear = notify.getDueDate().toString().substring(0, 4);
							break;
						}
					}
				}
			}
			
			if (!sError.trim().equalsIgnoreCase("")) {
				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\"  align=\"left\"><span style=\"color: #ff3333\">" + sError + "</span></td>");
				out.println("</table>");
			}
			
			out.println("<input type=\"hidden\" name=\"principal_flag\" value=\"" + sPrincipalFlag + "\" />");
			out.println("<input type=\"hidden\" name=\"action_flag\" value=\"" + sActionFlag + "\" />");
			//out.println("<input type=\"hidden\" name=\"action_implement_flag\" value=\"" + sActionImplementFlag + "\" />");
//			out.println("<input type=\"hidden\" name=\"after_flag\" value=\"" + sAfterActionFlag + "\" />");
			out.println("<input type=\"hidden\" name=\"current_step\" value=\"" + sCurrentStep + "\" />");
			
			out.println("<input type=\"hidden\" name=\"original_principal_month\" value=\"" + sPrincipalMonthO + "\" />");
			out.println("<input type=\"hidden\" name=\"original_principal_day\" value=\"" + sPrincipalDayO + "\" />");
			out.println("<input type=\"hidden\" name=\"original_principal_year\" value=\"" + sPrincipalYearO + "\" />");

			out.println("<input type=\"hidden\" name=\"original_action_month\" value=\"" + sActionMonthO + "\" />");
			out.println("<input type=\"hidden\" name=\"original_action_day\" value=\"" + sActionDayO + "\" />");
			out.println("<input type=\"hidden\" name=\"original_action_year\" value=\"" + sActionYearO + "\" />");
			
//			out.println("<input type=\"hidden\" name=\"original_implement_month\" value=\"" + sActionImplementMonthO + "\" />");
//			out.println("<input type=\"hidden\" name=\"original_implement_day\" value=\"" + sActionImplementDayO + "\" />");
//			out.println("<input type=\"hidden\" name=\"original_implement_year\" value=\"" + sActionImplementYearO + "\" />");

//			out.println("<input type=\"hidden\" name=\"original_after_month\" value=\"" + sAfterActionMonthO + "\" />");
//			out.println("<input type=\"hidden\" name=\"original_after_day\" value=\"" + sAfterActionDayO + "\" />");
//			out.println("<input type=\"hidden\" name=\"original_after_year\" value=\"" + sAfterActionYearO + "\" />");

			out.println("<input type=\"hidden\" name=\"actual_principal\" value=\"" + sPrincipalActualComplete + "\" />");
			out.println("<input type=\"hidden\" name=\"actual_action\" value=\"" + sActionActualComplete + "\" />");
			out.println("<input type=\"hidden\" name=\"actual_implement\" value=\"" + sActionActualImplement + "\" />");
//			out.println("<input type=\"hidden\" name=\"actual_after\" value=\"" + sAfterActionActual + "\" />");
			
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
			out.println("<tr><td headers=\"\"  class=\"tdblue\">");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Interview information</td>");
			out.println("</tr>");
			out.println("<tr><td headers=\"\"  width=\"100%\">");

			out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
			out.println("<tr valign=\"middle\">");
			out.println("<td headers=\"\"  style=\"background-color: #ffffff;color: #000000;\" align=\"center\" >");
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\">&nbsp;</td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b>Company:</b></td>");
			out.println("<td headers=\"\"  align=\"left\"><b>" + proj.getCompany() + "</b></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\">&nbsp;</td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b>Set/Met title:</b></td>");
			out.println("<td headers=\"\"  align=\"left\"><b>" + sTitle + "</b><input type=\"hidden\" name=\"title\" value=\"" + sTitle + "\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\">&nbsp;</td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b>Interview date:</b></td>");
			out.println("<td headers=\"\"  align=\"left\"><b>" + sInterviewDate + "</b><input type=\"hidden\" name=\"interview_date\" value=\"" + sInterviewDate + "\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\">&nbsp;</td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b>Client care advocate:</b></td>");
			out.println("<td headers=\"\"  align=\"left\">" + ETSUtils.getUsersName(con,ETSSetMetDAO.getPrimaryContact(con,proj.getProjectId())) + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b><label for=\"client\">Client name:</label></b></td>");
			out.println("<td headers=\"\"  align=\"left\">" + displayClientAsSelect(con,"setmet_client","client",proj.getProjectId(),sClient,es.gIR_USERN) + " <b>or</b></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\">&nbsp;</td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\">&nbsp;</td>");
			out.println("<td headers=\"\"  align=\"left\">enter a client name</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"></td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\"></td>");
			out.println("<td headers=\"\"  align=\"left\"><input type=\"text\" name=\"clientname\" class=\"iform\" id=\"client\" maxlength=\"80\" size=\"45\" value=\"" + sClientName + "\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");


			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"></td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b>Client e-mail notification:</label></b></td>");
			if (SupressClient1.trim().equalsIgnoreCase("")) {
				out.println("<td headers=\"\"  align=\"left\"><table><tr><td headers=\"\" width=\"16\" valign=\"top\"><input type=\"checkbox\" class=\"iform\" name=\"supress_client1\" id=\"client_supress1\" value=\"Y\" /></td><td headers=\"\"><label for=\"client_supress1\">Supress client e-mail notification for <b>Client review/approve interview</b> step.</label></td></tr></table></td>");
			} else {
				out.println("<td headers=\"\"  align=\"left\"><table><tr><td headers=\"\" width=\"16\" valign=\"top\"><input type=\"checkbox\" class=\"iform\" name=\"supress_client1\" id=\"client_supress1\" value=\"Y\" checked=\"checked\" selected=\"selected\" /></td><td headers=\"\"><label for=\"client_supress1\">Supress client e-mail notification for <b>Client review/approve interview</b> step.</label></td></tr></table></td>");
			}
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"></td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\">&nbsp;</td>");
			if (SupressClient2.trim().equalsIgnoreCase("")) {
				out.println("<td headers=\"\"  align=\"left\"><table><tr><td headers=\"\" width=\"16\" valign=\"top\"><input type=\"checkbox\" class=\"iform\" name=\"supress_client2\" id=\"client_supress2\" value=\"Y\" /></td><td headers=\"\"><label for=\"client_supress2\">Supress client e-mail notification for <b>Set/Met final rating</b> step.</label></td></tr></table></td>");
			} else {
				out.println("<td headers=\"\"  align=\"left\"><table><tr><td headers=\"\" width=\"16\" valign=\"top\"><input type=\"checkbox\" class=\"iform\" name=\"supress_client2\" id=\"client_supress2\" value=\"Y\" checked=\"checked\" selected=\"selected\" /></td><td headers=\"\"><label for=\"client_supress2\">Supress client e-mail notification for <b>Set/Met final rating</b> step.</label></td></tr></table></td>");
			}
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");

			
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b><label for=\"principal\">Principal:</label></b></td>");
			out.println("<td headers=\"\"  align=\"left\">" + displayInviteesAsSelect(con,"setmet_principal","principal",proj.getProjectId(),sPrincipal,es.gIR_USERN) + "</td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			
			
			if (sPrincipalFlag.equalsIgnoreCase("Y")) {
				out.println("<tr>");
				out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
				out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b><label for=\"label_pdate\">Principal review date:</label></b></td>");
				out.println("<td headers=\"\"  align=\"left\">");
				showDate(out,sPrincipalMonth,sPrincipalDay,sPrincipalYear,"principal_month","principal_day","principal_year","label_pdate");
				out.println("</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
				out.println("</tr>");
			} else {
				out.println("<tr>");
				out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
				out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b><label for=\"label_pdate\">Principal review date:</label></b></td>");
				out.println("<td headers=\"\"  align=\"left\">" + sPrincipalActualComplete + " (mm/dd/yyyy)");
				out.println("</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
				out.println("</tr>");
			}
			
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
			out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b><label for=\"pm\">Program manager:</label></b></td>");
			out.println("<td headers=\"\"  align=\"left\">" + displayInviteesAsSelect(con,"setmet_pm","pm",proj.getProjectId(),sPM,es.gIR_USERN) + "</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");

			if (sActionFlag.equalsIgnoreCase("Y")) {
				out.println("<tr>");
				out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
				out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b><label for=\"label_adate\">Action plan date:</label></b></td>");
				out.println("<td headers=\"\"  align=\"left\">");
				showDate(out,sActionMonth,sActionDay,sActionYear,"action_month","action_day","action_year","label_adate");
				out.println("</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
				out.println("</tr>");
			} else {
				out.println("<tr>");
				out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
				out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b><label for=\"label_adate\">Action plan date:</label></b></td>");
				out.println("<td headers=\"\"  align=\"left\">" + sActionActualComplete + " (mm/dd/yyyy)");
				out.println("</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
				out.println("</tr>");
			}

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
			out.println("<td headers=\"\"  align=\"left\"><b>Click on \"Submit\" to update Set/Met demographics. <br /><span style=\"color: #ff3333\">Please wait for confirmation page to be displayed and click on \"Ok\" to close the window.</span></b></td>");
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
	
			//Vector vMembers = ETSDatabaseManager.getProjMembers(sProjectId,con);
			
			Vector vMembers = ETSWorkspaceDAO.getInternalUsersInWorkspace(con,sProjectId);
	
			if (vMembers != null && vMembers.size() > 0) {
	
				for (int i = 0; i < vMembers.size(); i++) {
	
					ETSUser user = (ETSUser) vMembers.elementAt(i);
					
					if (!ETSDatabaseManager.hasProjectPriv(user.getUserId(),sProjectId,Defines.CLIENT,con) && !ETSDatabaseManager.hasProjectPriv(user.getUserId(),sProjectId,Defines.VISITOR,con)) {
						
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

	private static String displayClientAsSelect(Connection con, String sSelectName, String sLabelId, String sProjectId, String sInviteesList, String sLoggedInID) throws SQLException, Exception {
	
		StringBuffer out = new StringBuffer("");
	
		try {
	
			String sAvailable = "," + sInviteesList + ",";
			out.append("<select style=\"width:250px\" width=\"250px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");
			out.append("<option value=\"\" selected=\"selected\">Please select a client</option>");
	
			Vector vMembers = ETSDatabaseManager.getUsersByProjectPriv(sProjectId, Defines.CLIENT, con);
	
			if (vMembers != null && vMembers.size() > 0) {
	
				for (int i = 0; i < vMembers.size(); i++) {
	
					ETSUser user = (ETSUser) vMembers.elementAt(i);
					if (sAvailable.indexOf("," + user.getUserId() + ",") >= 0)  {
						out.append("<option value=\"" + user.getUserId() + "\" selected=\"selected\">" + ETSUtils.getUsersName(con,user.getUserId()) + "</option>");
					} else {
						out.append("<option value=\"" + user.getUserId() + "\" >" + ETSUtils.getUsersName(con,user.getUserId()) + "</option>");
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

	private String validateCreateSetMetInterview(HttpServletRequest request) throws Exception {
	
		StringBuffer sError = new StringBuffer("");
	
		try {
	
			String sClient = ETSUtils.checkNull(request.getParameter("setmet_client"));
			String sClientName = ETSUtils.checkNull(request.getParameter("clientname"));
			if (sClient.equals("") && sClientName.equals("")) {
				sError.append("<b>Client name</b> is not set. Please choose the appropriate client name or key in the client name.<br />");
			}
			
			String sPrincipal = ETSUtils.checkNull(request.getParameter("setmet_principal"));
			if (sPrincipal.equals("")) {
				sError.append("<b>Principal</b> is not set. Please choose the appropriate principal.<br />");
			}
			
			String sPrincipalFlag = ETSUtils.checkNull(request.getParameter("principal_flag"));
			String sActionFlag = ETSUtils.checkNull(request.getParameter("action_flag"));
			String sActionImplementFlag = ETSUtils.checkNull(request.getParameter("action_implement_flag"));
			//String sAfterActionFlag = ETSUtils.checkNull(request.getParameter("after_flag"));

			if (sPrincipalFlag.equalsIgnoreCase("Y")) {			
				String sCalMonth = ETSUtils.checkNull(request.getParameter("principal_month"));
				String sCalDay = ETSUtils.checkNull(request.getParameter("principal_day"));
				String sCalYear = ETSUtils.checkNull(request.getParameter("principal_year"));
	
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
					sError.append("<b>Principal review date</b> is not valid. Please set the principal review date correctly.<br />");
				}
			}
						
			
			String sPM = ETSUtils.checkNull(request.getParameter("setmet_pm"));
			if (sPM.equals("")) {
				sError.append("<b>Program manager</b> is not set. Please choose the appropriate program manager.<br />");
			}

			if (sActionFlag.equalsIgnoreCase("Y")) {
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
					sError.append("<b>Action plan creation date</b> is not valid. Please set the action plan creation date correctly.<br />");
				}

			}
			
//			if (sActionImplementFlag.equalsIgnoreCase("Y")) {
//				String sCalMonth = ETSUtils.checkNull(request.getParameter("action_implement_month"));
//				String sCalDay = ETSUtils.checkNull(request.getParameter("action_implement_day"));
//				String sCalYear = ETSUtils.checkNull(request.getParameter("action_implement_year"));
//	
//				int month = Integer.parseInt(sCalMonth.trim());
//				int day = Integer.parseInt(sCalDay.trim());
//				int year = Integer.parseInt(sCalYear.trim());
//	
//				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.YEAR,year);
//				cal.set(Calendar.MONTH,month -1);
//				int iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//				int iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
//	
//				if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
//					cal.set(Calendar.DAY_OF_MONTH,day);
//				} else{
//					sError.append("<b>Action plan implementation date</b> is not valid. Please set the Action plan implementation date correctly.<br />");
//				}
//			}
			
			
			
			
			
//			if (sAfterActionFlag.equalsIgnoreCase("Y")) {
//				String sCalMonth = ETSUtils.checkNull(request.getParameter("after_month"));
//				String sCalDay = ETSUtils.checkNull(request.getParameter("after_day"));
//				String sCalYear = ETSUtils.checkNull(request.getParameter("after_year"));
//	
//				int month = Integer.parseInt(sCalMonth.trim());
//				int day = Integer.parseInt(sCalDay.trim());
//				int year = Integer.parseInt(sCalYear.trim());
//	
//				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.YEAR,year);
//				cal.set(Calendar.MONTH,month -1);
//				int iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//				int iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
//	
//				if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
//					cal.set(Calendar.DAY_OF_MONTH,day);
//				} else{
//					sError.append("<b>After action review date</b> is not valid. Please set the after action review date correctly.<br />");
//				}
//			}
			
			
			if (sPrincipalFlag.equalsIgnoreCase("Y")) {

				// is of the format mm/dd/yyyy
				String sInterviewDate = ETSUtils.checkNull(request.getParameter("interview_date"));
				
				String sMonth1 = sInterviewDate.substring(0,2);
				String sDay1 = sInterviewDate.substring(3,5);
				String sYear1 = sInterviewDate.substring(6,10);
				
				String sMonth2 = ETSUtils.checkNull(request.getParameter("principal_month"));
				String sDay2 = ETSUtils.checkNull(request.getParameter("principal_day"));
				String sYear2 = ETSUtils.checkNull(request.getParameter("principal_year"));

				long date1 = Timestamp.valueOf(sYear1 + "-" + sMonth1 + "-" + sDay1 + " 00:00:00.000000000").getTime();
				long date2 = Timestamp.valueOf(sYear2 + "-" + sMonth2 + "-" + sDay2 + " 00:00:00.000000000").getTime();
		
				if (date1 > date2) {
					sError.append("Interview date cannot be greater than Principal review date. <br />");
				}
				
			}
			
			if (sPrincipalFlag.equalsIgnoreCase("Y") && sActionFlag.equalsIgnoreCase("Y")) {
							
				String sMonth1 = ETSUtils.checkNull(request.getParameter("principal_month"));
				String sDay1 = ETSUtils.checkNull(request.getParameter("principal_day"));
				String sYear1 = ETSUtils.checkNull(request.getParameter("principal_year"));

				String sMonth2 = ETSUtils.checkNull(request.getParameter("action_month"));
				String sDay2 = ETSUtils.checkNull(request.getParameter("action_day"));
				String sYear2 = ETSUtils.checkNull(request.getParameter("action_year"));

				long date1 = Timestamp.valueOf(sYear1 + "-" + sMonth1 + "-" + sDay1 + " 00:00:00.000000000").getTime();
				long date2 = Timestamp.valueOf(sYear2 + "-" + sMonth2 + "-" + sDay2 + " 00:00:00.000000000").getTime();
		
				if (date1 > date2) {
					sError.append("Principal review date cannot be greater than Action plan date. <br />");
				}
		
			}
	
//			if (sActionFlag.equalsIgnoreCase("Y") && sAfterActionFlag.equalsIgnoreCase("Y")) {
//				
//				String sMonth1 = ETSUtils.checkNull(request.getParameter("action_month"));
//				String sDay1 = ETSUtils.checkNull(request.getParameter("action_day"));
//				String sYear1 = ETSUtils.checkNull(request.getParameter("action_year"));
//	
//				String sMonth2 = ETSUtils.checkNull(request.getParameter("after_month"));
//				String sDay2 = ETSUtils.checkNull(request.getParameter("after_day"));
//				String sYear2 = ETSUtils.checkNull(request.getParameter("after_year"));
//			
//				long date1 = Timestamp.valueOf(sYear1 + "-" + sMonth1 + "-" + sDay1 + " 00:00:00.000000000").getTime();
//				long date2 = Timestamp.valueOf(sYear2 + "-" + sMonth2 + "-" + sDay2 + " 00:00:00.000000000").getTime();
//		
//				if (date1 > date2) {
//					sError.append("Action plan date cannot be greater than After action review date. <br />");
//				}
//				
//			}
			
	
						
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
