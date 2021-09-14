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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSSetMetExpectServlet extends javax.servlet.http.HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.17";

	private static Log logger = EtsLogger.getLogger(ETSSetMetExpectServlet.class);

	/**
	 * @see java.lang.Object#Object()
	 */
	public ETSSetMetExpectServlet() {
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

			String sQuestionType = request.getParameter("qt");
			if (sQuestionType == null || sQuestionType.trim().equals("")) {
				sQuestionType = "";
			} else {
				sQuestionType = sQuestionType.trim();
			}


			String sSubmitFlag = request.getParameter("image_submit.x");
			if (sSubmitFlag == null || sSubmitFlag.trim().equals("")) {
				sSubmitFlag = "";
			} else {
				sSubmitFlag = sSubmitFlag.trim();
			}


			ETSSetMetQuestion question = ETSSetMetDAO.getQuestion(conn,Integer.parseInt(sQuestionID));

			ETSProj proj = ETSUtils.getProjectDetails(conn,sProjId);
			
			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());
			
			params = ETSUtils.getServletParameters(request);

			PopupHeaderFooter header = new PopupHeaderFooter();
			//header.setHeader("");
			header.setPageTitle(prop.getAppName() + " - Details");
			out.println(header.printPopupHeader());
			if (sQuestionType.trim().equalsIgnoreCase("M")) {
				if (sOp.trim().equalsIgnoreCase("add")) {
					ETSUtils.popupHeaderLeft("Add Expectation",question.getQuestionDesc(),out);
				} else if (sOp.trim().equalsIgnoreCase("edit")) {
					ETSUtils.popupHeaderLeft("Edit Expectation",question.getQuestionDesc(),out);
				} else if (sOp.trim().equalsIgnoreCase("delete")) {
					ETSUtils.popupHeaderLeft("Delete Expectation",question.getQuestionDesc(),out);
				}
			} else {
				ETSUtils.popupHeaderLeft("Comments",question.getQuestionDesc(),out);
			}
			
			out.println("<form name=\"SetMetExpect\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss\">");

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
			out.println("<input type=\"hidden\" name=\"qt\" value=\"" + sQuestionType + "\" />");

			if (!sSubmitFlag.trim().equals("")) {
				
				String sError = validateExpectation(request);
				
				if (!sError.trim().equalsIgnoreCase("")) {
					displayExpectation(conn,es,request,out,proj,sProjId,sOp,sSetMetID,sQuestionID,sSeqNo,sQuestionType, sError);
				} else {
					String sStatus = modifyExpectation(conn,out,request,es);
				
					out.println("<br /><br />");
				
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  align=\"left\" ><b>" + sStatus + "</b></td></tr></table>");

					out.println("<br /><br />");
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\"  width=\"30\" align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" /></td><td headers=\"\"  align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\">Ok</a></a></td></tr></table>");
					out.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");
				}


			} else {
				displayExpectation(conn,es,request,out,proj,sProjId,sOp,sSetMetID,sQuestionID,sSeqNo,sQuestionType,"");
			}

			out.println("</form>");
			//out.println("<br /><br />");

			ETSUtils.popupHeaderRight(out);

			out.println(header.printPopupFooter());


		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
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
	private String modifyExpectation(Connection conn, PrintWriter out, HttpServletRequest request, EdgeAccessCntrl es) throws SQLException, Exception {
		
		String sSuccess = "<span style=\"color:#ff3333\">There was a system error when uppdating your comments. Please try again. If the problem persists, please contact your client care advocate.</span>";
		
		try {
			
			String sQuestionType = ETSUtils.checkNull(request.getParameter("qt"));
			
			if (sQuestionType.trim().equalsIgnoreCase("M")) {
			
				String sOp = ETSUtils.checkNull(request.getParameter("op"));
				String sProjectID = ETSUtils.checkNull(request.getParameter("proj"));
				String sSetMetID = ETSUtils.checkNull(request.getParameter("setmet"));
				String sQuestionNo = ETSUtils.checkNull(request.getParameter("question"));
				String sSeqNo = ETSUtils.checkNull(request.getParameter("seqno"));
				String sRating = ETSUtils.checkNull(request.getParameter("init_rating"));
				String sExpDesc = ETSUtils.checkNull(request.getParameter("expectation"));
				String sExpAction = ETSUtils.checkNull(request.getParameter("exp_action"));
				String sExpCode = ETSUtils.checkNull(request.getParameter("exp_code"));
				
				if (sOp.trim().equalsIgnoreCase("add")) {
					
					// operation is to add a new expectation..
					
					int iNextSeq = ETSSetMetDAO.getNextSeqNoForExpectation(conn,sProjectID,sSetMetID,Integer.parseInt(sQuestionNo));
					
					ETSSetMetExpectation exp = new ETSSetMetExpectation();
					
					exp.setSetMetID(sSetMetID);
					exp.setProjectID(sProjectID);
					exp.setQuestionID(Integer.parseInt(sQuestionNo));
					exp.setSeqNo(iNextSeq);
					exp.setExpectDesc(sExpDesc);
					exp.setExpectAction(sExpAction);
					exp.setExpectRating(Double.parseDouble(sRating));
					exp.setExpectID(Integer.parseInt(sExpCode));
					exp.setFinalRating(-1);
					exp.setComments("");
					exp.setLastUserID(es.gIR_USERN);
					exp.setLastTimestamp(null);
					
					ETSSetMetDAO.insertSetMetExpectation(conn,exp);
					
					sSuccess = "Your expectations/actions has been added successfully.";
					
				} else if (sOp.trim().equalsIgnoreCase("edit")) {
					
					ETSSetMetExpectation exp = new ETSSetMetExpectation();
					
					exp.setSetMetID(sSetMetID);
					exp.setProjectID(sProjectID);
					exp.setQuestionID(Integer.parseInt(sQuestionNo));
					exp.setSeqNo(Integer.parseInt(sSeqNo));
					exp.setExpectDesc(sExpDesc);
					exp.setExpectAction(sExpAction);
					exp.setExpectRating(Double.parseDouble(sRating));
					exp.setExpectID(Integer.parseInt(sExpCode));
					exp.setFinalRating(-1);
					exp.setComments("");
					exp.setLastUserID(es.gIR_USERN);
					exp.setLastTimestamp(null);
					
					ETSSetMetDAO.updateSetMetExpectation(conn,exp);
					
					sSuccess = "Your expectations/actions has been modified successfully.";
	
				} else if (sOp.trim().equalsIgnoreCase("delete")) {
					
					ETSSetMetExpectation exp = new ETSSetMetExpectation();
					
					exp.setSetMetID(sSetMetID);
					exp.setProjectID(sProjectID);
					exp.setQuestionID(Integer.parseInt(sQuestionNo));
					exp.setSeqNo(Integer.parseInt(sSeqNo));
					exp.setExpectDesc(sExpDesc);
					exp.setExpectAction(sExpAction);
					exp.setExpectRating(Double.parseDouble(sRating));
					exp.setExpectID(Integer.parseInt(sExpCode));
					exp.setFinalRating(-1);
					exp.setComments("");
					exp.setLastUserID(es.gIR_USERN);
					exp.setLastTimestamp(null);
					
					ETSSetMetDAO.deleteSetMetExpectation(conn,exp);
					
					sSuccess = "The expectations/actions comments has been deleted successfully.";
				}

			} else {
							
				String sOp = ETSUtils.checkNull(request.getParameter("op"));
				String sProjectID = ETSUtils.checkNull(request.getParameter("proj"));
				String sSetMetID = ETSUtils.checkNull(request.getParameter("setmet"));
				String sQuestionNo = ETSUtils.checkNull(request.getParameter("question"));
				String sSeqNo = ETSUtils.checkNull(request.getParameter("seqno"));
				String sRating = ETSUtils.checkNull(request.getParameter("init_rating"));
				String sExpDesc = ETSUtils.checkNull(request.getParameter("expectation"));
				String sExpAction = ETSUtils.checkNull(request.getParameter("exp_action"));
				String sExpCode = ETSUtils.checkNull(request.getParameter("exp_code"));
				String sComments = ETSUtils.checkNull(request.getParameter("comments"));
				
				
				if (sOp.trim().equalsIgnoreCase("add")) {
					
					// operation is to add a new expectation..
					
					int iNextSeq = ETSSetMetDAO.getNextSeqNoForExpectation(conn,sProjectID,sSetMetID,Integer.parseInt(sQuestionNo));
					
					ETSSetMetExpectation exp = new ETSSetMetExpectation();
					
					exp.setSetMetID(sSetMetID);
					exp.setProjectID(sProjectID);
					exp.setQuestionID(Integer.parseInt(sQuestionNo));
					exp.setSeqNo(iNextSeq);
					exp.setExpectDesc(sExpDesc);
					exp.setExpectAction(sExpAction);
					exp.setExpectRating(Double.parseDouble(sRating));
					exp.setExpectID(Integer.parseInt(sExpCode));
					exp.setFinalRating(-1);
					exp.setComments(sComments);
					exp.setLastUserID(es.gIR_USERN);
					exp.setLastTimestamp(null);
					
					ETSSetMetDAO.insertSetMetExpectation(conn,exp);
					
					sSuccess = "Your comments has been added successfully.";
					
				} else if (sOp.trim().equalsIgnoreCase("edit")) {
					
					ETSSetMetExpectation exp = new ETSSetMetExpectation();
					
					exp.setSetMetID(sSetMetID);
					exp.setProjectID(sProjectID);
					exp.setQuestionID(Integer.parseInt(sQuestionNo));
					exp.setSeqNo(Integer.parseInt(sSeqNo));
					exp.setExpectDesc(sExpDesc);
					exp.setExpectAction(sExpAction);
					exp.setExpectRating(Double.parseDouble(sRating));
					exp.setExpectID(Integer.parseInt(sExpCode));
					exp.setFinalRating(-1);
					exp.setComments(sComments);
					exp.setLastUserID(es.gIR_USERN);
					exp.setLastTimestamp(null);
					
					ETSSetMetDAO.updateSetMetExpectation(conn,exp);
					
					sSuccess = "Your comments has been modified successfully.";
				}
			}
			
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return sSuccess;
		
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
	private static void displayExpectation(Connection con, EdgeAccessCntrl es,HttpServletRequest request, PrintWriter out, ETSProj proj, String sProjectId, String sOp, String sSetMetID, String sQuestionID, String sSeqNo, String sQuestionType, String sError) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		try {
			
			NumberFormat format = new DecimalFormat("0");
			
			if (sQuestionType.trim().equalsIgnoreCase("M")) {
				
				String sExp = "";
				String sAct = "";
				double dRating = 0;
				int iExpect = 0;
				
				if (!sError.trim().equalsIgnoreCase("")) {
					String sRating = ETSUtils.checkNull(request.getParameter("init_rating"));
					String sExpDesc = ETSUtils.checkNull(request.getParameter("expectation"));
					String sExpAction = ETSUtils.checkNull(request.getParameter("exp_action"));
					String sExpCode = ETSUtils.checkNull(request.getParameter("exp_code"));
					
					sExp = sExpDesc;
					sAct = sExpAction;
					dRating = Double.parseDouble(sRating);
					if (sExpCode.trim().equalsIgnoreCase("")) {
						iExpect = -1;
					} else {
						iExpect = Integer.parseInt(sExpCode);
					}
				} else {
					if (sOp.trim().equalsIgnoreCase("edit")) {
						ETSSetMetExpectation exp = ETSSetMetDAO.getSetMetExpectation(con,sProjectId,sSetMetID,Integer.parseInt(sQuestionID),Integer.parseInt(sSeqNo));
						sExp = exp.getExpectDesc();
						sAct = exp.getExpectAction();
						dRating = exp.getExpectRating();
						iExpect = exp.getExpectID();
					} else if (sOp.trim().equalsIgnoreCase("delete")) {
						ETSSetMetExpectation exp = ETSSetMetDAO.getSetMetExpectation(con,sProjectId,sSetMetID,Integer.parseInt(sQuestionID),Integer.parseInt(sSeqNo));
						sExp = exp.getExpectDesc();
						sAct = exp.getExpectAction();
						dRating = exp.getExpectRating();
						iExpect = exp.getExpectID();
					}
				}


				if (!sError.trim().equalsIgnoreCase("")) {
	
					out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"> ");
					out.println("<tr>");
					out.println("<td headers=\"\"  colspan=\"2\" valign=\"top\" ><span style=\"color:#ff3333\">" + sError + "</span></td>");
					out.println("</tr>");
					out.println("</table>");
					
					out.println("<br />");
					
					out.println("<!-- Gray dotted line -->");
					out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
					out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
					out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<!-- End Gray dotted line -->");
					
					out.println("<br />");
				}
				
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"> ");
	
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" valign=\"top\" ><label for=\"exp\"><b>Comments:</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\"  colspan=\"2\" ><textarea name=\"expectation\" cols=\"80\" rows=\"5\"  class=\"iform\" maxlength=\"1000\" id=\"exp\">" + sExp + "</textarea></td>");
				out.println("</tr>");
	
				// divider
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\"  colspan=\"2\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\"  valign=\"top\" ><label for=\"exp_act\"><b>Expectation action (optional):</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\"  colspan=\"2\" ><textarea name=\"exp_action\" cols=\"80\" rows=\"5\" class=\"iform\" maxlength=\"1000\" id=\"exp_act\">" + sAct + "</textarea></td>");
				out.println("</tr>");
	
				// divider
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\"  colspan=\"2\" >&nbsp;</td>");
				out.println("</tr>");
	
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\"  width=\"150\" align=\"left\"><label for=\"rating\"><b>Rating:</b></label></td>");
				out.println("<td headers=\"\"  align=\"left\">");				
				out.println("<select name=\"init_rating\" id=\"rating\" style=\"width:80px\" width=\"80px\">");
				out.println("<option value=\"0\" selected=\"selected\">Rating</option>");
				
				double dCount = 0;
				
				while (dCount <= 10) {
					if (dRating == dCount) {
						out.println("<option value=\"" + String.valueOf(dCount) + "\" selected=\"selected\" >" + String.valueOf(format.format(dCount)) + "</option>");
					} else {
						out.println("<option value=\"" + String.valueOf(dCount) + "\">" + String.valueOf(format.format(dCount)) + "</option>");
					}
					dCount = dCount + 1;
				}
//				for (int iRate = 0; iRate <= 10; iRate++) {
//					if (iRating == iRate) {
//						out.println("<option value=\"" + String.valueOf(iRate) + "\" selected=\"selected\" >" + String.valueOf(iRate) + "</option>");
//					} else {
//						out.println("<option value=\"" + String.valueOf(iRate) + "\">" + String.valueOf(iRate) + "</option>");
//					}
//				}
				out.println("</select>");
				out.println("</td></tr>");
	
	
				if (!ETSUtils.checkUserRole(es,sProjectId).equalsIgnoreCase(Defines.WORKSPACE_CLIENT)) {
					
					// display this only if user is not client...
					
					Vector vExpect = ETSSetMetDAO.getExpectationCode(con);
					
					if (vExpect != null) {
						// divider
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\"  colspan=\"2\" >&nbsp;</td>");
						out.println("</tr>");
						
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\"  width=\"150\" align=\"left\"><label for=\"exp_code1\"><b>Category code:</b></label></td>");
						out.println("<td headers=\"\"  align=\"left\">");				
						out.println("<select name=\"exp_code\" id=\"exp_code1\" style=\"width:300px\" width=\"300px\">");
						out.println("<option value=\"\" selected=\"selected\">Category code</option>");
						for (int i = 0; i < vExpect.size(); i++) {
							ETSSetMetExpectCode code = (ETSSetMetExpectCode) vExpect.elementAt(i);
							if (code.getExpectID() == iExpect) {
								out.println("<option value=\"" + String.valueOf(code.getExpectID()) + "\" selected=\"selected\" >" + code.getExpectDesc() + "</option>");
							} else {
								out.println("<option value=\"" + String.valueOf(code.getExpectID()) + "\">" + code.getExpectDesc() + "</option>");
							}
						}
						out.println("</select>");
						out.println("</td></tr>");
		
					}
				} else {
					// divider
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\"  colspan=\"2\" ><input type=\"hidden\" name=\"exp_code\" value=\"" + iExpect + "\" /></td>");
					out.println("</tr>");
					
				}
	
				out.println("</table>");
				
				out.println("<br />");
				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				if (sOp.trim().equalsIgnoreCase("add")) {
					out.println("<td headers=\"\"  align=\"left\"><b>Click on \"Submit\" to add this expectation. <br /><span style=\"color: #ff3333\">Please wait for confirmation page to be displayed and click on \"Ok\" to close the window.</span></b></td>");
				} else if (sOp.trim().equalsIgnoreCase("edit")) {
					out.println("<td headers=\"\"  align=\"left\"><b>Click on \"Submit\" to edit this expectation. <br /><span style=\"color: #ff3333\">Please wait for confirmation page to be displayed and click on \"Ok\" to close the window.</span></b></td>");
				} else if (sOp.trim().equalsIgnoreCase("delete")) {
					out.println("<td headers=\"\"  align=\"left\"><b>Click on \"Submit\" to delete this expectation. <br /><span style=\"color: #ff3333\">Please wait for confirmation page to be displayed and click on \"Ok\" to close the window.</span></b></td>");
				}
				out.println("</table>");
				
			} else if (sQuestionType.trim().equalsIgnoreCase("G")) {
				
				// general questions...
				
				out.println("<input type=\"hidden\" name=\"expectation\" value=\"\" />");
				out.println("<input type=\"hidden\" name=\"exp_action\" value=\"\" />");
				out.println("<input type=\"hidden\" name=\"exp_code\" value=\"0\" />");
				
				double dRating = 0;
				int iExpect = 0;
				String sComments = "";
				
				if (sOp.trim().equalsIgnoreCase("edit")) {
					ETSSetMetExpectation exp = ETSSetMetDAO.getSetMetExpectation(con,sProjectId,sSetMetID,Integer.parseInt(sQuestionID),Integer.parseInt(sSeqNo));
					dRating = exp.getExpectRating();
					iExpect = exp.getExpectID();
					sComments = exp.getComments();
				}
				
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"> ");
	
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" valign=\"top\" ><label for=\"exp\"><b>Comments:</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\"  colspan=\"2\"><textarea name=\"comments\" cols=\"80\" rows=\"5\"  class=\"iform\" maxlength=\"1000\" id=\"exp\">" + sComments + "</textarea></td>");
				out.println("</tr>");
	
				// divider
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\"  colspan=\"2\" >&nbsp;</td>");
				out.println("</tr>");
	
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\"   width=\"80\" align=\"left\"><label for=\"exp_rate\"><b>Rating:</b></label></td>");
				out.println("<td headers=\"\"  width=\"500\" align=\"left\">");				
				out.println("<select name=\"init_rating\" id=\"exp_rate\" style=\"width:80px\" width=\"80px\">");
				out.println("<option value=\"0\" selected=\"selected\">Rating</option>");
				
				double dCount = 0;
				
				while (dCount <= 10) {
					
					if (dRating == dCount) {
						out.println("<option value=\"" + String.valueOf(dCount) + "\" selected=\"selected\" >" + String.valueOf(dCount) + "</option>");
					} else {
						out.println("<option value=\"" + String.valueOf(dCount) + "\">" + String.valueOf(dCount) + "</option>");
					}
					dCount = dCount + 0.5;
				}
				
//				for (int iRate = 0; iRate <= 10; iRate++) {
//					if (iRating == iRate) {
//						out.println("<option value=\"" + String.valueOf(iRate) + "\" selected=\"selected\" >" + String.valueOf(iRate) + "</option>");
//					} else {
//						out.println("<option value=\"" + String.valueOf(iRate) + "\">" + String.valueOf(iRate) + "</option>");
//					}
//				}
				out.println("</select>");
				out.println("</td></tr>");
				out.println("</table>");
				
				out.println("<br />");
				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\"  align=\"left\"><b>Click on \"Submit\" to update comments. <br /><span style=\"color: #ff3333\">Please wait for confirmation page to be displayed and click on \"Ok\" to close the window.</span></b></td>");
				out.println("</table>");

			} else if (sQuestionType.trim().equalsIgnoreCase("C") || sQuestionType.trim().equalsIgnoreCase("P")) {
				
				// client and principal comments ...
				
				out.println("<input type=\"hidden\" name=\"expectation\" value=\"\" />");
				out.println("<input type=\"hidden\" name=\"exp_action\" value=\"\" />");
				out.println("<input type=\"hidden\" name=\"init_rating\" value=\"0\" />");
				out.println("<input type=\"hidden\" name=\"exp_code\" value=\"0\" />");
				
				String sComments = "";
				
				if (sOp.trim().equalsIgnoreCase("edit")) {
					ETSSetMetExpectation exp = ETSSetMetDAO.getSetMetExpectation(con,sProjectId,sSetMetID,Integer.parseInt(sQuestionID),Integer.parseInt(sSeqNo));
					sComments = exp.getComments();
				}
				
				
				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"> ");
	
				out.println("<tr>");
				out.println("<td headers=\"\"  valign=\"top\" ><label for=\"exp\"><b>Comments:</b></label></td>");
				out.println("</tr>");
				
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" ><textarea name=\"comments\" cols=\"80\" rows=\"5\"  class=\"iform\" maxlength=\"1000\" id=\"exp\">" + sComments + "</textarea></td>");
				out.println("</tr>");
	
				// divider
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\"  colspan=\"2\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("</table>");
				
				out.println("<br />");
				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\"  align=\"left\"><b>Click on \"Submit\" to update comments. <br /><span style=\"color: #ff3333\">Please wait for confirmation page to be displayed and click on \"Ok\" to close the window.</span></b></td>");
				out.println("</table>");

			}
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

	/**
	 * @param conn
	 * @param out
	 * @param request
	 * @param es
	 */
	private String validateExpectation(HttpServletRequest request) throws Exception {
		
		StringBuffer sError = new StringBuffer(""); 
		
		try {
			
			String sQuestionType = ETSUtils.checkNull(request.getParameter("qt"));
			
			if (sQuestionType.trim().equalsIgnoreCase("M")) {
			
				String sOp = ETSUtils.checkNull(request.getParameter("op"));
				String sProjectID = ETSUtils.checkNull(request.getParameter("proj"));
				String sSetMetID = ETSUtils.checkNull(request.getParameter("setmet"));
				String sQuestionNo = ETSUtils.checkNull(request.getParameter("question"));
				String sSeqNo = ETSUtils.checkNull(request.getParameter("seqno"));
				String sRating = ETSUtils.checkNull(request.getParameter("init_rating"));
				String sExpDesc = ETSUtils.checkNull(request.getParameter("expectation"));
				String sExpAction = ETSUtils.checkNull(request.getParameter("exp_action"));
				String sExpCode = ETSUtils.checkNull(request.getParameter("exp_code"));
				
				if (sExpDesc.trim().equalsIgnoreCase("")) {
					sError.append("<b>Please enter the comments</b><br />");
				}
				
				if (sExpCode.trim().equalsIgnoreCase("")) {
					sError.append("<b>Please select the category code</b><br />");
				}
			}
			
		} catch (Exception e) {
			throw e;
		}
		
		return sError.toString();
		
	}

}
