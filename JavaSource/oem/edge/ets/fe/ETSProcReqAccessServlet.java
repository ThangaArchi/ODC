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

package oem.edge.ets.fe;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.AmtHfConstants;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.acmgt.bdlg.AddMembrProcDataPrep;
import oem.edge.ets.fe.acmgt.bdlg.InvMemberProcDataPrep;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.UserInviteStatusModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsOpModel;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

/**
 * @version     1.0
 * @author
 */
public class ETSProcReqAccessServlet extends HttpServlet implements Servlet {

	private static Log logger = EtsLogger.getLogger(ETSProcReqAccessServlet.class);

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	public static final String VERSION = "1.13.1.10";
	private static final String CLASS_VERSION = "1.10";

	private String mailhost;

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Connection conn = null;
		String Msg = null;
		Hashtable params = null;
		String sLink = "";

		StringBuffer sHeader = new StringBuffer("");
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		boolean validateparams = true;
		Hashtable hs = new Hashtable();

		try {

			///new//

			hs = ETSUtils.getServletParameters(request);

			/////////validation end

			conn = ETSDBUtils.getConnection();
			System.out.println("After DB Connection");
			if (!es.GetProfile(response, request, conn)) {
				return;
			}

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				sLink = "250000";
			}

			ETSParams parameters = new ETSParams(); // set the parameters
			parameters.setConnection(conn);
			parameters.setRequest(request);
			parameters.setResponse(response);
			parameters.setWriter(writer);
			parameters.setLinkId(sLink);
			parameters.setEdgeAccessCntrl(es);

			AmtHeaderFooter headerfooter = new AmtHeaderFooter(es, conn, hs);

			ETSProcReqAccessFunctions uaFuncs = new ETSProcReqAccessFunctions();
			uaFuncs.setETSParms(parameters);
			String goPageString = "";
			String suboption = "";

			// do all the processing in this block...starts
			String option = getParameter(request, "option"); // controller
			if (option.equals("") || option.equals("showreq")) {
				goPageString = uaFuncs.showReq();
			}
			if (option.equals("ibmreg")) {
				goPageString = "ibmreg";
			}
			if (option.equals("procreq")) {

				String ibmId = AmtCommonUtils.getTrimStr(request.getParameter("ibmid"));

				ETSUserDetails reqUserDetails = new ETSUserDetails();
				reqUserDetails.setWebId(ibmId);
				reqUserDetails.extractUserDetails(conn);

				logger.debug("REQUEST ID:procreq::PROC REQ ACCESS SERVLET==" + ibmId);
				logger.debug("USER TYPE:procreq::PROC REQ ACCESS SERVLET==" + reqUserDetails.getUserType());

				if (reqUserDetails.getUserType() == reqUserDetails.USER_TYPE_INTERNAL) {

					goPageString = uaFuncs.procReq();

				} else {

					if (checkOwnerMultiPOC(conn, writer, request, es.gIR_USERN, ibmId)) {

						goPageString = uaFuncs.procReq();

					} else {

						goPageString = "multipocerr";

					}
				} //end of external
			} //end of option
			if (option.equals("procreqz")) {
				goPageString = "summary";
			} // show the summary
			if (option.equals("procreq2")) {
				goPageString = uaFuncs.procReq2();
			}
			if (option.equals("invite")) {
				goPageString = "invite";
			}
			if (goPageString.equals("redirect")) {
				response.sendRedirect("ETSConnectServlet.wss");
			}
			if (option.equals("nouidmail")) {
				String to = getParameter(request, "toid");

				String ccEmail = getParameter(request, "ccid");

				String comments = getParameter(request, "comments");

				String from = parameters.getEdgeAccessCntrl().gEMAIL;
				String projectName = getParameter(request, "projname");

				String woemail = getParameter(request, "woemail");
				if (!woemail.equals(from.toString())) {
					ccEmail = woemail + "," + ccEmail;
				}
				String proj = getParameter(request, "proj");
				String ibmid = getParameter(request, "ibmid");
				String tc = getParameter(request, "tc");
				String cc = getParameter(request, "cc");
				String action = getParameter(request, "action");
				suboption = AmtCommonUtils.getTrimStr(getParameter(request, "suboption"));

				//

				UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(proj);

				ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
				String selfRegistrationURL = resBundle.getString("selfRegistrationURL");
				selfRegistrationURL = selfRegistrationURL.substring(0, selfRegistrationURL.indexOf("&okurl"));

				String loginURL = Global.WebRoot + "/" + "index.jsp";

				String subject = "Invitation to IBM " + unBrandedprop.getAppName() + " web portal";

				StringBuffer sBuff = new StringBuffer();

				sBuff.append("\n");

				String webRoot = "";
				if (!Global.loaded) {

					Global.Init();

					webRoot = Global.server;

				}

				Global.println("web root==" + webRoot);

				if (suboption.equals("invbywo")) {

					InvMemberProcDataPrep invDataPrep = new InvMemberProcDataPrep();
					String errMsg = invDataPrep.validateReqParams(request);
					Global.println("ERR MSG===" + errMsg);

					if (!AmtCommonUtils.isResourceDefined(errMsg)) {

						boolean ret = invDataPrep.loadInviteDataFromReqParams(request, es.gIR_USERN, es.gUSERN);
						Global.println("ret code after insert into invite status table==" + ret);

						//send mail start

						UserInviteStatusModel invStatModel = invDataPrep.transformParamsToInvModel(request, es.gIR_USERN, es.gUSERN);

						sBuff.append("I would like you to join a workspace for '" + projectName + "' on IBM " + unBrandedprop.getAppName() + " web portal. We will use this workspace  to collaborate and share information securely.").append("\n");

						sBuff.append("\n");
						sBuff.append("Please follow these two steps:").append("\n");
						sBuff.append("\n");
						sBuff.append("---------------------------------------------------------------------------------------").append("\n");
						sBuff.append("STEP #1: Create an IBM ID and password").append("\n");
						sBuff.append("---------------------------------------------------------------------------------------").append("\n");
						sBuff.append("\n");
						sBuff.append("Click and follow URL to register your ID of <" + invStatModel.getUserId() + ">. Make sure you fill your \"company address\" completely as it will be required for access to the workspace.").append("\n");
						sBuff.append("\n");
						sBuff.append("" + selfRegistrationURL + "").append("\n");
						sBuff.append("\n");
						sBuff.append("---------------------------------------------------------------------------------------").append("\n");
						sBuff.append("STEP #2: Log in to initiate access to the workspace").append("\n");
						sBuff.append("---------------------------------------------------------------------------------------").append("\n");
						sBuff.append("\n");
						sBuff.append("Click on the link below and login with your newly created IBM ID and password. You do not need to do anything further,the system will initiate the final processing based on your  login and you will be informed by an email once your access is complete. ").append("\n");
						sBuff.append("\n");
						sBuff.append("" + loginURL + "").append("\n");
						sBuff.append("\n");
						sBuff.append("If you run into any difficulty, you can contact me or our 24x7 help desk at " + AmtHfConstants.sAmtHfEdgeContPhoneNum + " for US & Canada or " + AmtHfConstants.sAmtHfEdgeContPhoneNumOtherGeos + " for international users.").append("\n");
						sBuff.append("\n");
						sBuff.append("\n");
						sBuff.append("Registration information").append("\n");
						sBuff.append("IBM ID:                               " + invStatModel.getUserId()).append("\n");
						sBuff.append("Project or proposal:                  " + projectName).append("\n");
						sBuff.append("Country:                              " + invStatModel.getUserCountryName()).append("\n");
						sBuff.append("Company:                              " + invStatModel.getUserCompany()).append("\n");
						sBuff.append("Privilege:                            " + invStatModel.getRoleName()).append("\n");
						sBuff.append("E-mail address of IBM contact:        " + woemail).append("\n");
						sBuff.append("\n");
						if (!comments.equals("")) {
							sBuff.append("Additions comments if any from the the invitee").append("\n");
							sBuff.append(comments).append("\n");
						}
						Global.println("mail messg==" + sBuff.toString());
						Global.Init();

						try {
							ETSUtils.sendEMail(from, to, ccEmail, Global.mailHost, sBuff.toString(), subject, from);
						} catch (Exception eX) {
							eX.printStackTrace();
						}

						//send mail end

					} else {

						validateparams = false;
						ETSProj updproj = ETSDatabaseManager.getProjectDetails(proj);
						Hashtable rebuildTab = invDataPrep.getPrevReqParams(request, es.gEMAIL);
						ETSProcReqAccessGoPages procGopages = new ETSProcReqAccessGoPages();
						writer.println(headerfooter.printETSBullsEyeHeader());
						writer.println(headerfooter.printBullsEyeLeftNav());
						writer.println(headerfooter.printSubHeader());

						printHeader("Add member", "", false, updproj, writer, request);
						writer.println(procGopages.inviteByWOWM(rebuildTab, errMsg));
						writer.println("<br />");
						// end right column...
						writer.println("</td>");
						writer.println("</tr>");
						writer.println("</table>");

						writer.println("<br /><br />");

						writer.println(headerfooter.printBullsEyeFooter());

					}

				}

				if (validateparams) {

					ETSProj updproj = ETSDatabaseManager.getProjectDetails(proj);
					ETSProcReqAccessGoPages procGopages = new ETSProcReqAccessGoPages();
					writer.println(headerfooter.printETSBullsEyeHeader());
					writer.println(headerfooter.printBullsEyeLeftNav());
					writer.println(headerfooter.printSubHeader());

					printHeader("Add/Invite member", "", false, updproj, writer, request);
					writer.println(procGopages.printInviteBkMsg(proj, tc, sLink));
					writer.println("<br />");
					// end right column...
					writer.println("</td>");
					writer.println("</tr>");
					writer.println("</table>");

					writer.println("<br /><br />");

					writer.println(headerfooter.printBullsEyeFooter());

					//response.sendRedirect("ETSProjectsServlet.wss?linkid=251000&proj=" + proj + "&tc=" + tc + "&cc=" + cc + "&action=" + action);
					validateparams=false;

				}
				//return;
			}
			if (option.equals("invalidmail")) {
				String to = getParameter(request, "toid");

				String ccEmail = getParameter(request, "ccid");

				String comments = getParameter(request, "comments");

				String from = parameters.getEdgeAccessCntrl().gEMAIL;
				String projectName = getParameter(request, "projname");

				String woemail = getParameter(request, "woemail");
				if (!woemail.equals(from.toString())) {
					ccEmail = woemail + "," + ccEmail;
				}
				String proj = getParameter(request, "proj");
				String ibmid = getParameter(request, "ibmid");
				String tc = getParameter(request, "tc");
				String cc = getParameter(request, "cc");
				String action = getParameter(request, "action");

				//
				UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(proj);

				String subject = "Invitation to IBM " + unBrandedprop.getAppName() + " web portal";

				StringBuffer sBuff = new StringBuffer();
				// TODO ... public page ?
				Global.Init();
				ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
				procFuncs.sendValidateEmail(from, to, ccEmail, Global.mailHost, projectName, woemail);

				response.sendRedirect("ETSProjectsServlet.wss?linkid=" + unBrandedprop.getLinkID() + "&proj=" + proj + "&tc=" + tc + "&cc=" + cc + "&action=" + action);
				//return;
			}

			if (option.equals("noaddressmail")) {

				String to = getParameter(request, "toid");
				String ccEmail = getParameter(request, "ccid");

				String comments = getParameter(request, "comments");

				ETSAccessRequest accReq = new ETSAccessRequest();
				String woemail = getParameter(request, "woemail");
				String projectName = getParameter(request, "projname");
				accReq.setMgrEMail(woemail);
				String from = parameters.getEdgeAccessCntrl().gEMAIL;

				if (!woemail.equals(from.toString())) {
					ccEmail = woemail + "," + ccEmail;
				}

				Global.Init();
				ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
				procFuncs.sendAddressEmail(from, to, ccEmail, Global.mailHost, projectName, woemail, comments);

				String proj = getParameter(request, "proj");
				String ibmid = getParameter(request, "ibmid");
				String tc = getParameter(request, "tc");
				String cc = getParameter(request, "cc");
				String action = getParameter(request, "action");

				UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(proj);

				response.sendRedirect("ETSProjectsServlet.wss?linkid=" + unBrandedprop.getLinkID() + "&proj=" + proj + "&tc=" + tc + "&cc=" + cc + "&action=" + action);
				//return;
			}

			// ...ends

			if (validateparams) {

				//check with laxman

				hs = ETSUtils.getServletParameters(request);
				AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, conn, hs);
				EdgeHeader.setPopUp("J");
				EdgeHeader.setHeader("Access approval to E&TS Connect");
				EdgeHeader.setSubHeader("Pending access request");
				sHeader.insert(0, EdgeHeader.printSubHeader());
				sHeader.insert(0, EdgeHeader.printBullsEyeLeftNav());
				sHeader.insert(0, EdgeHeader.printETSBullsEyeHeader());
				sHeader.append("<form name=\"uid\" method=\"post\" action=\"ETSProcReqAccessServlet.wss\">");
				sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				sHeader.append("<tr valign=\"top\"><td width=\"600\" valign=\"top\">");
				writer.println(sHeader.toString()); // output end with a open <td>

				// direct the user to the ...
				ETSProcReqAccessGoPages goPage = new ETSProcReqAccessGoPages();
				goPage.setETSParms(parameters);
				// set the error messages if any .. from the process .. todisplay
				goPage.setErrMsg(uaFuncs.getErrMsg());
				if (goPageString.equals("showreq")) {
					writer.println(goPage.showReq());
				}
				if (goPageString.equals("invite")) {
					writer.println(goPage.invite(null));
				}
				if (goPageString.equals("reqfwd")) {
					writer.println(goPage.reqFwd());
				}
				if (goPageString.equals("wolevel")) {
					writer.println(goPage.woLevel());
				}
				if (goPageString.equals("summary")) {
					writer.println(goPage.showSummary());
				}
				if (goPageString.equals("acceptok")) {
					writer.println(goPage.acceptOk());
				}
				if (goPageString.equals("ibmreg")) {
					writer.println(goPage.ibmReg());
				}
				if (goPageString.equals("multipocerr")) {
					writer.println(printMultiErrMsg(request, conn, es.gUSERN));
				}

				//... necessary page

				writer.println("</td>");
				writer.println("</tr>");
				writer.println("</table>");
				// default form variables
				writer.println("<input type=\"hidden\" name=\"linkid\" value=\"250000\" />");
				writer.println("</form>");

				// content ends
				writer.println(EdgeHeader.printBullsEyeFooter());

			}

		} catch (SQLException e) {
			e.printStackTrace(); // TODO remove
			SysLog.log(SysLog.ERR, this, e); // TODO remove
			ETSUtils.displayError(writer, ETSErrorCodes.getErrorCode(e), "Error occurred on E&TS Connect.");
		} catch (Exception e) {
			e.printStackTrace(); //TODO remove
			SysLog.log(SysLog.ERR, this, e); // TODO remove
			ETSUtils.displayError(writer, ETSErrorCodes.getErrorCode(e), "Error occurred on IBM E&TS Connect.");
		} finally {
			ETSDBUtils.close(conn);
			writer.flush();
			writer.close();
		}
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	private String getParameter(HttpServletRequest req, String key) {
		String value = req.getParameter(key);
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}

	private void printHeader(String msg, String subheader, boolean printBM, ETSProj Project, PrintWriter writer, HttpServletRequest request) {
		StringBuffer buf = new StringBuffer();
		try {
			//gutter between content and right column
			writer.println("<td rowspan=\"4\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			// Right column start
			writer.println("<td rowspan=\"4\" width=\"150\" valign=\"top\">");
			ETSContact contact = new ETSContact(Project.getProjectId(), request);
			contact.printContactBox(writer);
			writer.println("</td></tr>");
		} catch (Exception e) {

		}
		/*
		buf.append("<tr><td class=\"small\">");
		buf.append("<b>"+msg+"</b>");
		buf.append("</td></tr>");

		buf.append("<tr><td height=\"10\">");
		buf.append("<img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"443\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>");
		*/

		String header = ETSUtils.getBookMarkString(msg, "", printBM);

		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"top\">");
		buf.append(header + "</td></tr>");
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\" class=\"small\">");
		buf.append(subheader + "</td></tr>");
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\">");
		buf.append("<img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"443\" valign=\"bottom\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>");

		writer.println(buf.toString());
	}

	public boolean checkOwnerMultiPOC(Connection con, PrintWriter out, HttpServletRequest request, String loginUserId, String reqUserid) throws SQLException, Exception {

		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();

		boolean req = false;
		ETSUserDetails loginUserDetails = new ETSUserDetails();
		loginUserDetails.setWebId(loginUserId);
		loginUserDetails.extractUserDetails(con);

		ETSUserDetails reqUserDetails = new ETSUserDetails();
		reqUserDetails.setWebId(reqUserid);
		reqUserDetails.extractUserDetails(con);

		AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();
		AddMembrProcDataPrep addMmbrBdlg = new AddMembrProcDataPrep();

		boolean womultipoc = wrkSpcDao.userHasEntitlement(loginUserDetails.getWebId(), Defines.MULTIPOC);

		// check if the user is external or not...
		if (reqUserDetails.getUserType() == reqUserDetails.USER_TYPE_EXTERNAL) {

			logger.debug("MULTI-POC EXISTS FOR OWNER USER ID ==" + womultipoc);

			//if not multipoc request entitlement for WO and continue the process
			if (!womultipoc) {

				logger.debug(" START MULTI-POC ENT OWNER USER ID ==" + loginUserDetails.getWebId());

				actOpModel = addMmbrBdlg.requestMultiPocForWO(loginUserDetails, reqUserDetails);

				logger.debug("END MULTI-POC ENT OWNER USER ID ==" + loginUserDetails.getWebId());

				String retCode = AmtCommonUtils.getTrimStr(actOpModel.getRetCode());

				logger.debug("RET CODE FOR MULTI POC REQUEST===" + retCode);

			}

		}

		//		check if the user is external or not...
		if (reqUserDetails.getUserType() == reqUserDetails.USER_TYPE_INTERNAL) {

			womultipoc = true;
		}

		return womultipoc;

	}

	public String printMultiErrMsg(HttpServletRequest req, Connection conn, String sOwnerEdgeId) throws SQLException, Exception {

		String topCatStr = req.getParameter("tc");
		String linkid = req.getParameter("linkid");
		StringBuffer sb = new StringBuffer();

		sb.append("<br />");
		sb.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td >");
		sb.append("<span style=\"color:#ff3333\">");
		sb.append("You do not have MultiPOC entitlement to approve this request. MultiPOC entitlement has been requested for you. You should be able to approve this request, once your entitlement request has been approved.");
		sb.append("</span>");
		sb.append("</td></tr>");

		String sMgrEmail = AmtCommonUtils.getTrimStr(ETSUtils.getManagersEMailFromDecafTable(conn, sOwnerEdgeId));
		String sMgrName = AmtCommonUtils.getTrimStr(ETSUtils.getManagerNameFromDecafTable(conn, sOwnerEdgeId));

		String mgrInfoStr = sMgrName + " " + "[Email : " + sMgrEmail + "]";

		sb.append("<tr><td>&nbsp;</td></tr>");

		if (AmtCommonUtils.isResourceDefined(sMgrEmail)) {
			sb.append("<tr>");
			sb.append("<td  align=\"left\">Note : MultiPOC entitlement request has been forwarded to your manager , <b>" + mgrInfoStr + "</b> for approval. You will receive an e-mail from \"IBM Customer Connect\" when this has been processed.</td>");
			sb.append("</tr>");
		}
		sb.append("<tr><td>&nbsp;</td></tr>");
		sb.append("<tr><td>&nbsp;</td></tr>");
		sb.append("</table>");

		sb.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
		sb.append("<tr>");
		sb.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
		sb.append("<a href=\"ETSConnectServlet.wss?linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
		sb.append("</td> ");
		sb.append("<td  align=\"left\" valign=\"top\">");
		sb.append("<a href=\"ETSConnectServlet.wss?linkid=" + linkid + "\" class=\"fbox\">Back to E&TS Connect</a>");
		sb.append("</td> ");
		sb.append("<tr>");
		sb.append("</table>");

		return sb.toString();

	}

} //end of class
