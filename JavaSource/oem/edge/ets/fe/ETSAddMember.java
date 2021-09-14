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

/*****************************************************************************/
/*                                                                           */
/*  File Name   :   ETSAddMember.java                                        */
/*  Class Path  :   ets/fe                                 */
/*  Release     :   4.2.1                                                    */
/*  Description :   Displays Add member functions.                           */
/*  Created By  :   v2sathis@us.ibm.com                                      */
/*  Date        :   03/01/2004                                               */
/*****************************************************************************/
/*  Change Log  :   Please Enter Changed on, Changed by and Desc             */
/*****************************************************************************/

/**
 * @author: Sathish
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.AmtErrorHandler;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;
import oem.edge.decaf.DecafCreateEnt;
import oem.edge.amt.Metrics;
import oem.edge.amt.UserObject;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.acmgt.bdlg.AddMembrProcDataPrep;
import oem.edge.ets.fe.acmgt.bdlg.SubWrkSpcProcBdlg;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.helpers.SubWrkSpcGuiUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamGuiUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsOpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamObjKey;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSSyncUser;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;
import oem.edge.ets.fe.aic.AICWorkspaceDAO;
import oem.edge.ets.fe.ETSGroup;


import org.apache.commons.logging.Log;

public class ETSAddMember {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.69";
	public static final String VERSION = "1.69";
	private EdgeAccessCntrl ez = null;
	private static Log logger = EtsLogger.getLogger(ETSAddMember.class);
	private UnbrandedProperties unBrandedprop;

	public ETSAddMember(UnbrandedProperties unBrandedprop) {
		super();
		this.unBrandedprop = unBrandedprop;
		// TODO Auto-generated constructor stub
	}
	/**
	 * Method getClassVersion.
	 * @return String
	 */
	public static String getClassVersion() {
		return CLASS_VERSION;
	}

	/**
	 * Method processRequest.
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void processRequest(Connection con, HttpServletRequest request, HttpServletResponse response) throws Exception {

		PrintWriter out = response.getWriter();
			
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		ez = es;
		Hashtable params;
		String msg="";
		
		try {

			if (!es.GetProfile(response, request, con)) {
				return;
			}

			String sProjId = request.getParameter("proj");
			if (sProjId == null || sProjId.trim().equals("")) {
				sProjId = "";
			} else {
				sProjId = sProjId.trim();
			}

			String sSubmitFlag = request.getParameter("image_submit.x");
			if (sSubmitFlag == null || sSubmitFlag.trim().equals("")) {
				sSubmitFlag = "";
			} else {
				sSubmitFlag = sSubmitFlag.trim();
			}

			ETSProj proj = ETSDatabaseManager.getProjectDetails(con, sProjId);

			params = ETSUtils.getServletParameters(request);

			String zInvite = request.getParameter("invusr");
			if (zInvite == null || zInvite.trim().equals("")) {
				zInvite = "";
			} else {
				zInvite = zInvite.trim();
			}

			if (zInvite.equals("z1")) {
				String zUserId = AmtCommonUtils.getTrimStr(request.getParameter("add_id").toLowerCase());
				String invUserType = "E";

				if (zUserId.toLowerCase().endsWith("ibm.com")) {

					invUserType = "I";
				}

				// email and not a memeber of this workspace ?
				int userEmail = Integer.parseInt(EntitledStatic.getValue(con, "select count(*) from amt.users where user_email = '" + zUserId + "' and status='A' with ur"));
				Hashtable hashInvite = new Hashtable();

				hashInvite.put("proj", sProjId);
				String tc = request.getParameter("tc");
				hashInvite.put("tc", tc);
				String cc = request.getParameter("cc");
				hashInvite.put("cc", cc);
				String link = request.getParameter("linkid");
				hashInvite.put("link", link);
				String action = request.getParameter("action");
				hashInvite.put("action", action);
				String adminop = request.getParameter("admin_op");
				hashInvite.put("adminop", adminop);
				String aug1 = request.getParameter("aug1");
				if (aug1 != null) {
					hashInvite.put("aug1", aug1.trim());
				}

				switch (userEmail) {
					case 0 :
						// no email address
						// show invite screen for email
						ETSProcReqAccessGoPages procGo = new ETSProcReqAccessGoPages();
						Hashtable hash = new Hashtable();
						if (zUserId.indexOf("@") > 0) {
							hash.put("toid", zUserId);
						} else {
							hash.put("toid", "");
						}
						hash.put("reqemail", "" + es.gEMAIL);
						hash.put("ibmid", zUserId);
						hash.put("projid", sProjId);
						String topCatStr = request.getParameter("tc");
						hash.put("tc", topCatStr);
						//hash.put("cc",request.getParameter("cc"));
						hash.put("cc", "");
						hash.put("projname", EntitledStatic.getValue(con, "select project_name from ets.ets_projects where project_id = '" + sProjId + "' with ur"));
						if (ETSUtils.checkUserRole(es, sProjId) == Defines.WORKSPACE_OWNER) {
							hash.put("woemail", es.gEMAIL);
							hash.put("woemailcc", "");
						} else {
							ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
							hash.put("woemailcc", ETSUtils.getUserEmail(con, procFuncs.woWebId(sProjId, con)));
							hash.put("woemail", ETSUtils.getUserEmail(con, procFuncs.woWebId(sProjId, con)));
						}
						ETSProcReqAccessGoPages procGopages = new ETSProcReqAccessGoPages();
						hash.put("action", "usr_invite");
						hash.put("invUsrType", invUserType);

						out.println(procGo.inviteEmail(hash));
						break;
					case 1 :
						// only one 1 exists
						String usrId = AmtCommonUtils.getTrimStr(EntitledStatic.getValue(con, "select a.ir_userid from amt.users a, ets.ets_users b where a.user_email = '" + zUserId + "'and a.status='A'  and b.user_id = a.ir_userid and b.user_project_id = '" + sProjId + "' with ur"));

						if (!usrId.equals("")) {
							// and the part of workspace - show error - and back to invite
							hashInvite.put("inwkspc", "1");
							out.println(inviteResult(hashInvite));
						} else {
							// if not give add member link
							// not a part of the workspace - just get the id

							String xUsrId = EntitledStatic.getValue(con, "select a.ir_userid from amt.users a where a.user_email = '" + zUserId + "' and a.status='A' with ur");
							hashInvite.put("inwkspc", "0");
							hashInvite.put("add_id", xUsrId);

							out.println(inviteResult(hashInvite));
						}
						break;
					default :
						// more than 1 id with this email address ???
						// give add member link
						String count = EntitledStatic.getValue(con, "select count(*) from amt.users a, ets.ets_users b where a.user_email = '" + zUserId + "' and a.status='A' and a.ir_userid = b.user_id and b.user_project_id = '" + sProjId + "' with ur");

						if (Integer.parseInt(count) > 1) {

							hashInvite.put("inwkspc", "9");

						} else if (Integer.parseInt(count) == 1) {

							hashInvite.put("inwkspc", "1");
						} else {

							hashInvite.put("inwkspc", "0");

						}
						hashInvite.put("add_id", zUserId);
						out.println(inviteResult(hashInvite));
						break;
				}
				return;
			}

			if (!sSubmitFlag.trim().equals("")) {
				String sOp = request.getParameter("admin_op");
				sOp = sOp.trim();

				String sUserId = (String) request.getAttribute("add_id");
				if(!StringUtil.isNullorEmpty(sUserId)) sUserId = sUserId.trim();
				
				String invUserType = "E";

				if (sOp.trim().equals("verify_addmember")) {
					int iErr = validateAddMember(con, sProjId, request);
					if (iErr == 0) {
						// get to see if it is user id or email id
						// if id proceed to add - else show userid selection

						if (sUserId.toLowerCase().endsWith("ibm.com")) {

							invUserType = "I";
						}
						int byUidCount = Integer.parseInt(EntitledStatic.getValue(con, "select count(*) from amt.users where ir_userid = '" + sUserId + "' with ur"));
						if (byUidCount == 0) {
							// there is no id, check for email address
							int byECount = Integer.parseInt(EntitledStatic.getValue(con, "select count(*) from amt.users where user_email = '" + sUserId + "' with ur"));
							switch (byECount) {
								case 0 : // no user with this email found

									//	if id exists in IR
									if (AccessCntrlFuncs.isIbmIdExistsInIR(con, sUserId)) {

										processAddMember(con, out, sProjId,msg,request, sUserId, es, sUserId);

									} else if (proj.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)
											&& AICWorkspaceDAO.isProjectIBMOnly(sProjId,con).equalsIgnoreCase("Y")) {
												// workspace access  is available for internal users only
												showAddMemberScreen(con, out, sProjId, request, -88);
									} else {

										// show invitation screen.
										Hashtable hash = new Hashtable();
										if (sUserId.indexOf("@") > 0) {
											hash.put("toid", sUserId);
										} else {
											hash.put("toid", "");
										}
										hash.put("reqemail", "" + es.gEMAIL);
										hash.put("ibmid", sUserId);
										hash.put("projid", sProjId);
										String topCatStr = request.getParameter("tc");
										hash.put("tc", topCatStr);
										//hash.put("cc",request.getParameter("cc"));
										hash.put("cc", "");
										hash.put("projname", EntitledStatic.getValue(con, "select project_name from ets.ets_projects where project_id = '" + sProjId + "' with ur"));
										if (ETSUtils.checkUserRole(es, sProjId) == Defines.WORKSPACE_OWNER) {
											hash.put("woemail", es.gEMAIL);
											hash.put("woemailcc", "");
										} else {
											ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
											hash.put("woemailcc", ETSUtils.getUserEmail(con, procFuncs.woWebId(sProjId, con)));
											hash.put("woemail", ETSUtils.getUserEmail(con, procFuncs.woWebId(sProjId, con)));
										}
										ETSProcReqAccessGoPages procGopages = new ETSProcReqAccessGoPages();
										hash.put("action", "admin_add");
										hash.put("linkid", unBrandedprop.getLinkID());
										hash.put("invUserType", invUserType);
										//out.println(procGopages.invite(hash));

										out.println(procGopages.inviteByWOWM(hash, ""));

									} //end of invitation
									break;
								case 1 : // one id
									String zUserId = EntitledStatic.getValue(con, "select a.ir_userid from amt.users a where a.user_email = '" + sUserId + "' with ur");
									ETSUserDetails uDet = new ETSUserDetails();
									uDet.setWebId(zUserId);
									uDet.extractUserDetails(con);
									if ((uDet.getUserType() == uDet.USER_TYPE_EXTERNAL)
											&& proj.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)
											&& AICWorkspaceDAO.isProjectIBMOnly(sProjId,con).equalsIgnoreCase("Y")) {
										// workspace access  is available for internal users only
										showAddMemberScreen(con, out, sProjId, request, -88);
									}
									else if (uDet.getUserType() == uDet.USER_TYPE_EXTERNAL && ETSUtils.checkUserRole(es, sProjId).equals(Defines.WORKSPACE_MANAGER)) {
										// create a request and fwd it to WO
										ETSProj cProj = ETSDatabaseManager.getProjectDetails(con, sProjId);
										ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
										String owner_email = ETSUtils.getUserEmail(con, procFuncs.woWebId(sProjId, con));
										//check to see if there is already a pending request for this user.
										// fix for CSR IBMCC00010010
										if (hasPendingRequest(con,sUserId,sProjId)) {
											showAddMemberScreen(con, out, sProjId, request, 3);
										} else {
											ETSUserAccessRequest uar = new ETSUserAccessRequest();
											uar.recordRequest(con, sUserId, cProj.getName(), owner_email, es.gIR_USERN, cProj.getProjectType()); //??
											boolean sent = ETSBoardingUtils.sendManagerEMail(uDet, owner_email, cProj, es.gEMAIL);
											showAddMemberScreen(con, out, sProjId, request, -77);
										}
									} else {
										// process the request accordingly
										processAddMember(con, out, sProjId,msg,request, zUserId, es,sUserId);
									}

									break;
								default : // show multiple option - more than one found
									out.println("<form name=\"AddMember\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

									out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
									out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");

									out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");

									showMultiple(con, out, sProjId, request, sUserId);
									out.println("</form>");
									out.println("<br /><br />");
									break;
							}
						} else { // when 1
							ETSUserDetails uD = new ETSUserDetails();
							uD.setWebId(sUserId);
							uD.extractUserDetails(con);
							if ((uD.getUserType() == uD.USER_TYPE_EXTERNAL)
									&& proj.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)
									&& AICWorkspaceDAO.isProjectIBMOnly(sProjId,con).equalsIgnoreCase("Y")) {
								// workspace access  is available for internal users only
								showAddMemberScreen(con, out, sProjId, request, -88);
							} else if (uD.getUserType() == uD.USER_TYPE_EXTERNAL && ETSUtils.checkUserRole(es, sProjId).equals(Defines.WORKSPACE_MANAGER)) {
								// create a request and fwd it to WO
								ETSProj cProj = ETSDatabaseManager.getProjectDetails(con, sProjId);
								ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
								String owner_email = ETSUtils.getUserEmail(con, procFuncs.woWebId(sProjId, con));
								//check to see if there is already a pending request for this user.
								// fix for CSR IBMCC00010010
								if (hasPendingRequest(con,sUserId,sProjId)) {
									showAddMemberScreen(con,out,sProjId,request,3);
								} else {
									ETSUserAccessRequest uar = new ETSUserAccessRequest();
									uar.recordRequest(con, sUserId, cProj.getName(), owner_email, es.gIR_USERN, cProj.getProjectType()); //??
									boolean sent = ETSBoardingUtils.sendManagerEMail(uD, owner_email, cProj, es.gEMAIL);
									showAddMemberScreen(con, out, sProjId, request, -77);
								}
							} else {
								// process the request accordingly
								processAddMember(con, out, sProjId,msg,request, "Z", es,sUserId);
							}
						}
					} else {
						showAddMemberScreen(con, out, sProjId, request, iErr);
					}
				} else if (sOp.trim().equals("process_addmemberz")) {
					processAddMemberSummary(con, out, sProjId, request, "Z");
				} else if (sOp.trim().equals("process_addmember")) {

					submitAddMember(con, out, sProjId, es.gIR_USERN, request, es.gEMAIL);

				}
			} else {
				showAddMemberScreen(con, out, sProjId, request, 0);
			}
		} catch (SQLException e) {
			SysLog.log(SysLog.ERR, this, e);
			logger.error("execption", e);
			throw e;
		} catch (Exception e) {
			SysLog.log(SysLog.ERR, this, e);
			logger.error("execption", e);
			throw e;
		}
	}

	public String inviteResult(Hashtable hash) {
		String inWkspc = (String) hash.get("inwkspc");
		String zUserId = (String) hash.get("add_id");
		String proj = (String) hash.get("proj");
		String tc = (String) hash.get("tc");
		String cc = (String) hash.get("cc");
		String linkid = (String) hash.get("link");
		String action = (String) hash.get("action");
		String adminop = (String) hash.get("adminop");
		String aug1 = (String) hash.get("aug1");

		Global.println("invite work space===" + inWkspc);

		String msg = "";
		String url1 = "";
		String url2 = "";
		if (inWkspc.equals("1")) {
			// user in wkspc
			msg = "The e-mail address you entered indicates that the user already has an IBM ID and has access to this workspace.";
			url1 = "Team listing";
			url2 = "Return to invite user";
		} else {
			if (inWkspc.equals("9")) {
				msg = "The e-mail address you entered indicates that the user more than one IBM ID and one or more of them has access to this workspace.";
			} else {
				msg = "The e-mail address you entered indicates that the user already has an IBM ID but does not have access to this workspace.";
			}
			if (aug1 != null) {
				url1 = "Proceed with request add member";
			} else {
				url1 = "Proceed with add member";
			}
			url2 = "Return to invite user";
		}

		StringBuffer sBuff = new StringBuffer();
		//ets/ETSProjectsServlet.wss?action=usr_invite&proj=1&tc=5&cc=5&linkid=251000

		sBuff.append("<table  cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
		sBuff.append("<tr><td class=\"small\">");
		sBuff.append("<b>Invite new user</b>");
		sBuff.append("</td></tr>");
		sBuff.append("</table>");

		sBuff.append("<br />");
		//    sBuff.append("<input type=\"hidden\" name=\"action\" value=\"admin_add\" />");
		//sBuff.append("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
		//    sBuff.append("<input type=\"hidden\" name=\"admin_op\" value=\"verify_addmember\" />");
		//sBuff.append("<input type=\"hidden\" name=\"tc\" value=\"" + topCatStr + "\" />");
		//sBuff.append("<input type=\"hidden\" name=\"linkid\" value=\"" + linkid + "\" />");
		//sBuff.append("<input type=\"hidden\" name=\"invusr\" value=\"z1\" />");

		sBuff.append("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"443\"> ");

		sBuff.append("<tr>");
		sBuff.append("<td  colspan=\"2\" valign=\"top\" >" + msg + "</td>");
		sBuff.append("</tr>");

		// divider
		sBuff.append("<tr valign=\"top\">");
		sBuff.append("<td colspan=\"2\">&nbsp;</td>");
		sBuff.append("</tr>");
		if (inWkspc.equals("1")) {
			sBuff.append("<tr valign=\"top\">");
			sBuff.append("<td colspan=\"2\"><a class=\"fbox\" href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj + "&tc=" + tc + "&cc=" + tc + "&linkid=" + linkid + "\" class=\"fbox\">" + url1 + "</a></td>");
			sBuff.append("</tr>");
		} else {
			if (aug1 != null) {
				sBuff.append("<tr valign=\"top\">");
				sBuff.append("<td colspan=\"2\"><a class=\"fbox\" href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?uid=" + zUserId + "&action=addreqmember&admin_op=" + adminop + "&proj=" + proj + "&tc=" + tc + "&cc=" + cc + "&linkid=" + linkid + "&image_submit.x=z\">" + url1 + "</a></td>");
				sBuff.append("</tr>");
			} else {
				sBuff.append("<tr valign=\"top\">");
				sBuff.append("<td colspan=\"2\"><a class=\"fbox\" href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?add_id=" + zUserId + "&action=" + action + "&admin_op=" + adminop + "&proj=" + proj + "&tc=" + tc + "&cc=" + cc + "&linkid=" + linkid + "&image_submit.x=z\">" + url1 + "</a></td>");
				sBuff.append("</tr>");
			}
		}

		// divider
		sBuff.append("<tr valign=\"top\">");
		sBuff.append("<td colspan=\"2\">&nbsp;</td>");
		sBuff.append("</tr>");

		sBuff.append("<tr valign=\"top\">");
		sBuff.append("<td colspan=\"2\"><a class=\"fbox\" href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?action=usr_invite&proj=" + proj + "&tc=" + tc + "&cc=" + cc + "&linkid=" + linkid + "\">" + url2 + "</a></td>");
		sBuff.append("</tr>");

		sBuff.append("</table>");

		sBuff.append("<br /><br />");
		sBuff.append("<br /><br />");

		return sBuff.toString();
	}
	public void Invite(Connection conn, HttpServletRequest req, HttpServletResponse res) throws IOException {

		PrintWriter out = res.getWriter();
		String sProjId = req.getParameter("proj");
		if (sProjId == null || sProjId.trim().equals("")) {
			sProjId = "";
		} else {
			sProjId = sProjId.trim();
		}

		String aug1 = req.getParameter("aug1");
		if (aug1 == null || aug1.trim().equals("")) {
			aug1 = "";
		} else {
			aug1 = aug1.trim();
		}

		String topCatStr = req.getParameter("tc");
		String cCatStr = req.getParameter("cc");
		String linkid = req.getParameter("linkid");

		out.println("<form name=\"AddMember\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
		out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
		out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");

		//        out.println("<table  cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
		//        out.println("<tr><td class=\"small\">");
		//        out.println("<b>Invite new user</b>");
		//        out.println("</td></tr>");
		//        out.println("</table>");

		//        out.println("<br />");
		out.println("<input type=\"hidden\" name=\"action\" value=\"admin_add\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
		out.println("<input type=\"hidden\" name=\"admin_op\" value=\"verify_addmember\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + topCatStr + "\" />");
		out.println("<input type=\"hidden\" name=\"cc\" value=\"" + cCatStr + "\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + linkid + "\" />");
		out.println("<input type=\"hidden\" name=\"invusr\" value=\"z1\" />");
		if (aug1.equals("aug1")) {
			out.println("<input type=\"hidden\" name=\"aug1\" value=\"aug1\" />");
		}

		out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"443\"> ");

		out.println("<tr>");
		out.println("<td  colspan=\"2\" valign=\"top\" >Enter the e-mail address of the person to whom you wish to send an invitation to join this " + unBrandedprop.getAppName() + " workspace.</td>");
		out.println("</tr>");

		out.println("<tr valign=\"top\">");
		out.println("<td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td  valign=\"top\" ><b><label for=\"label_id\">E-mail address:</label></b></td>");
		out.println("<td   valign=\"top\" ><input type=\"text\" size=\"30\" id=\"label_id\" name=\"add_id\" value=\"\" /></td>");
		out.println("</tr>");

		// divider
		out.println("<tr valign=\"top\">");
		out.println("<td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"443\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("<tr valign=\"top\">");
		out.println("<td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
		out.println("<td  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
		out.println("<td  align=\"left\">");
		out.println(
			" <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td  width=\"25\" align=\"left\"><a href=\""
				+ Defines.SERVLET_PATH
				+ "ETSProjectsServlet.wss?proj="
				+ sProjId
				+ "&tc="
				+ topCatStr
				+ "&linkid="
				+ linkid
				+ "\"><img src=\""
				+ Defines.BUTTON_ROOT
				+ "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td >&nbsp;&nbsp;<a href=\""
				+ Defines.SERVLET_PATH
				+ "ETSProjectsServlet.wss?proj="
				+ sProjId
				+ "&tc="
				+ topCatStr
				+ "&linkid="
				+ linkid
				+ "\">Cancel</a></td></tr></table>");
		out.println("</td></tr></table>");

		out.println("<br /><br />");

		out.println("</form>");
		out.println("<br /><br />");
	}

	public void showMultiple(Connection conn, PrintWriter out, String projectId, HttpServletRequest request, String userId) throws SQLException {

		String topCatStr = request.getParameter("tc");
		String linkid = request.getParameter("linkid");
		out.println("<input type=\"hidden\" name=\"action\" value=\"admin_add\" />");
		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + projectId + "\" />");
		out.println("<input type=\"hidden\" name=\"admin_op\" value=\"verify_addmember\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + topCatStr + "\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + linkid + "\" />");

		out.println("<table cellspacing=\"1\" cellpadding=\"3\" border=\"0\"> ");

		out.println("<tr>");
		out.println("<td  colspan=\"4\" valign=\"top\" >This email address, " + userId + " is shared by the following userids:</td>");
		out.println("</tr>");

		out.println("<tr><td colspan=\"4\">&nbsp;</td></tr>");
		Statement st = conn.createStatement();
		out.println("<tr><td  colspan=\"4\" valign=\"top\" ><b>The following userids have access to this workspace.</b></tr>");
		out.println("<tr class=\"tblue\">");
		out.println("<td>&nbsp;</td><td>IBM ID</td><td>User name</td><td>Last logon<br />mm/dd/yyyy</td>");
		out.println("</tr>");
		String bgColor = "#eeeeee";
		String qry1 = "select a.ir_userid, a.user_fname||' '||a.user_lname, char(date(a.last_logon),usa) from amt.users a, ets.ets_users b where a.user_email = '" + userId + "' and a.status='A' and a.ir_userid = b.user_id and b.user_project_id = '" + projectId + "' order by 3 with ur";
		ResultSet rs1 = st.executeQuery(qry1);
		while (rs1.next()) {
			out.println("<tr bgcolor=\"" + (bgColor = bgColor.equals("#eeeeee") ? "#ffffff" : "#eeeeee") + "\">"); //add_id
			out.println("<td>&nbsp;</td><td>" + rs1.getString(1).trim() + "</td><td>" + rs1.getString(2).trim() + "</td><td>" + rs1.getString(3).trim() + "</td>");
			out.println("</tr>");
		}

		// divider
		out.println("<tr><td colspan=\"4\">&nbsp;</td></tr>");
		rs1.close();

		out.println("<tr><td  colspan=\"4\" valign=\"top\" ><b>The following userids do not have access to this workspace. Please choose the right userid and submit the request.</b></td></tr>");
		out.println("<tr class=\"tblue\">");
		out.println("<td>Select</td><td>IBM ID</td><td>User name</td><td>Last logon<br />mm/dd/yyyy</td>");
		out.println("</tr>");
		String qry =
			"select a.ir_userid, a.user_fname||' '||a.user_lname, char(date(a.last_logon),usa) from amt.users a where a.user_email = '" + userId + "' and a.status='A' and a.ir_userid not in (select a.ir_userid from amt.users a, ets.ets_users b where a.user_email = '" + userId + "' and a.status='A' and a.ir_userid = b.user_id and b.user_project_id = '" + projectId + "') order by 3 with ur";
		ResultSet rs = st.executeQuery(qry);
		bgColor = "#eeeeee";
		while (rs.next()) {
			out.println("<tr bgcolor=\"" + (bgColor = bgColor.equals("#eeeeee") ? "#ffffff" : "#eeeeee") + "\">"); //add_id
			out.println("<td>" + AmtErrorHandler.printImgLabel("sLblAddId12", "Add Id") + "<input type=\"radio\" name=\"add_id\" value=\"" + rs.getString(1).trim() + "\" id=\"sLblAddId12\" /></td><td>" + rs.getString(1).trim() + "</td><td>" + rs.getString(2).trim() + "</td><td>" + rs.getString(3).trim() + "</td>");
			out.println("</tr>");
		}

		// divider
		out.println("<tr><td colspan=\"4\">&nbsp;</td></tr>");

		out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
		out.println("<td  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
		out.println("<td >&nbsp;</td>");
		out.println("<td >&nbsp;</td>");
		out.println("<td  align=\"left\">");
		out.println(
			" <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td  width=\"25\" align=\"left\"><a href=\""
				+ Defines.SERVLET_PATH
				+ "ETSProjectsServlet.wss?proj="
				+ projectId
				+ "&tc="
				+ topCatStr
				+ "&linkid="
				+ linkid
				+ "\"><img src=\""
				+ Defines.BUTTON_ROOT
				+ "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td ><a class =\"fbox\" href=\""
				+ Defines.SERVLET_PATH
				+ "ETSProjectsServlet.wss?proj="
				+ projectId
				+ "&tc="
				+ topCatStr
				+ "&linkid="
				+ linkid
				+ "\">Cancel</a></td></tr></table>");
		out.println("</td></tr></table>");

		out.println("<br /><br />");

	}
	/**
	 * Method showAddMemberScreen.
	 * @param conn
	 * @param out
	 * @param sProjId
	 * @param request
	 * @param iErr
	 * @throws SQLException
	 * @throws Exception
	 */
	private void showAddMemberScreen(Connection conn, PrintWriter out, String sProjId, HttpServletRequest request, int iErr) throws SQLException, Exception {

		try {

			ETSProj etsProj = ETSDatabaseManager.getProjectDetails(sProjId);

			// alerts
			out.println("<form name=\"AddMember\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			String setmet = request.getParameter("backto");
			if (setmet != null) {
				out.println("<input type=\"hidden\" name=\"backto\" value=\"" + setmet + "\" />");
			}
			//            out.println("<table  cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
			//            out.println("<tr><td class=\"small\">");
			//            if(iErr!=-9){
			//out.println("<b>Add member</b>");

			//            }
			//            out.println("</td></tr>");
			//            out.println("</table>");

			//            out.println("<br />");

			String topCatStr = request.getParameter("tc");
			String linkid = request.getParameter("linkid");
			out.println("<input type=\"hidden\" name=\"action\" value=\"admin_add\" />");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"admin_op\" value=\"verify_addmember\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + topCatStr + "\" />");
			out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + linkid + "\" />");

			String sUser = "";

			String sbwspc_add_cont1 = AmtCommonUtils.getTrimStr(request.getParameter("sbwspc_add_cont1.x"));

			//new start

			if (!AmtCommonUtils.isResourceDefined(sbwspc_add_cont1)) {

			} else {

				SubWrkSpcProcBdlg subWrkSpcBdlg = new SubWrkSpcProcBdlg();

				if (AmtCommonUtils.isResourceDefined(subWrkSpcBdlg.validateScrn1FormFields(request))) {

					iErr=-888;

				} else {

				}

			}

			//new end

			String msg = "";
			switch (iErr) {
				case 1 :
					msg = "IBM ID cannot be blank. Please enter an IBM ID and click Continue.";
					break;
				case 2 :
					msg = "IBM ID entered is already a member of this workspace. Please enter the IBM ID of user who is not a member of this workspace.";
					break;
				case 3 :
					msg = "There is already a pending request for this ID.";
					break;
				case -9 :
					msg = "The address is incomplete for this user. An email has been sent to the user to update the address";
					break;
				case -10 :
					msg = "The IBM ID entered was not found on IBM registration site. Please verify the IBM ID entered and try again.";
					break;
				case -11 :
					msg = "The IBM ID entered is not yet validated. An email has been sent to the user, requesting to validate the userid and submit the request.";
					break;
				case -12 :
					ETSProj cProj = ETSDatabaseManager.getProjectDetails(conn, sProjId);
					ETSProj pProj = ETSDatabaseManager.getProjectDetails(conn, cProj.getParent_id());
					msg = "The IBM ID entered is not an approved user of the parent workspace, " + pProj.getName() + ". Please add the user to the parent workspace and then add the user to this workspace. ";
					break;
				case -77 :
					msg = "Since the user you worked on is not an IBM internal user, and you have been identified as the workspace manager of this workspace, so this request was forwarded to the owner of this workspace.";
					break;
				case -88 :
					msg = "Since the user entered is external user, and this workspace is available for IBM internal users only. Please enter IBM ID of user.";
					break;

				case -888 :

					msg = "Please select atleast one user from the list";

					break;

			}

			if (iErr != 0) {
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
				out.println("<tr>");
				out.println("<td  colspan=\"2\" valign=\"top\" ><span style=\"color:#ff3333\"><b><ul><li>" + msg + "</li></ul></b></span></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"443\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("</table>");
				
				// In case of error, page should ask to go to add Member page to add users again
				
				out.println("<table summary=\"\" border=\"0\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
				out.println("<td  align=\"left\" valign=\"top\" width=\"16\">");
				out.println("<a href=\"displayAddMembrInput.wss?action=admin_add&proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Add/Invite member(s) \" /></a>&nbsp;");
				out.println("</td> ");
				out.println("<td  align=\"left\" valign=\"top\" >");
				out.println("<a href=\"displayAddMembrInput.wss?action=admin_add&proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\" class=\"fbox\">Add/Invite member(s)</a>");
				out.println("</td> ");
				out.println("<td  align=\"left\" width=\"70\">&nbsp;</td>");
				out.println("<td  align=\"left\">&nbsp;</td>");
				out.println("<td  align=\"right\" valign=\"top\" width=\"16\">");
				out.println("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk_c.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
				out.println("</td> ");
				out.println("<td  align=\"left\" valign=\"top\" >");
				out.println("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
				out.println("</td> ");
				out.println("</td></tr></table>");
				
				out.println("<br /><br />");

				out.println("</form>");
				out.println("<br /><br />");
				// return the control	
				return;
			}

			WrkSpcTeamGuiUtils guiUtils = new WrkSpcTeamGuiUtils();

			String sbwspc_add_back = AmtCommonUtils.getTrimStr(request.getParameter("sbwspc_add_back.x"));
			ArrayList prevUserList = new ArrayList();

			//get the
			if(AmtCommonUtils.isResourceDefined(sbwspc_add_back)) {

				SubWrkSpcProcBdlg procBdlg = new SubWrkSpcProcBdlg();
				prevUserList=procBdlg.getPrevSelectdUserList(request);


			}

			if (!WrkSpcTeamUtils.isWrkSpcSubType(etsProj.getParent_id())) {

				out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"600\"> ");

				out.println("<tr>");
				out.println("<td  colspan=\"3\" valign=\"top\">Please enter the IBM ID or e-mail address of the user you wish to add to the team and click on submit.</td>");
				out.println("</tr>");

				// divider
				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td nowrap=\"nowrap\"  valign=\"top\" width=\"200\" ><b><label for=\"label_id\">IBM ID (or) e-mail address:</label></b></td>");
				out.println("<td   valign=\"top\" width=\"200\"><input class=\"iform\" type=\"text\" size=\"30\" id=\"label_id\" maxlength=\"132\" name=\"add_id\" value=\"" + sUser + "\" /></td>");
				out.println("<td nowrap=\"nowrap\"  valign=\"top\" width=\"200\" >" + guiUtils.printIbmLookUp() + "</td>");
				out.println("</tr>");

				// divider
				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"3\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<table summary=\"\" border=\"0\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
				out.println("<td  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
				out.println("<td  align=\"left\" width=\"70\">&nbsp;</td>");
				out.println("<td  align=\"left\">&nbsp;</td>");
				out.println("<td  align=\"right\" valign=\"top\" width=\"16\">");
				out.println("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk_c.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
				out.println("</td> ");
				out.println("<td  align=\"left\" valign=\"top\" >");
				out.println("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
				out.println("</td> ");
				out.println("</td></tr></table>");

			} else {

				SubWrkSpcGuiUtils subWrkUtils = new SubWrkSpcGuiUtils();
				out.println(subWrkUtils.printUserList(prevUserList,sProjId, etsProj.getParent_id(), topCatStr, linkid));
			}

			out.println("<br /><br />");

			out.println("</form>");
			out.println("<br /><br />");

		} catch (Exception e) {
			logger.error("execption", e);
			throw e;
		}

	}

	private void processAddMemberSummary(Connection conn, PrintWriter out, String sProjId, HttpServletRequest req, String zVal) throws SQLException, Exception {
		try {
			String sUserId = req.getParameter("add_id");
			sUserId = sUserId.trim();

			String topCatStr = req.getParameter("tc");
			String cCatStr = req.getParameter("cc");
			String linkid = req.getParameter("linkid");
			String sJob = req.getParameter("job");
			String grpExists = req.getParameter("grpExists");
			if (grpExists == null || grpExists.equals("")) {
				grpExists = "";
			} else {
				grpExists = grpExists.trim();
			}
			String partOfGrp = req.getParameter("partOfGrp");
			if (partOfGrp == null || partOfGrp.equals("")) {
				partOfGrp = "";
			} else {
				partOfGrp = partOfGrp.trim();
			}

			String selGrp = req.getParameter("selGrp");
			if (selGrp == null || selGrp.equals("")) {
				selGrp = "";
			} else {
				selGrp = selGrp.trim();
			}
						
			String sAssignCompany = "";
			String sAssignCty = "";

			EdgeAccessCntrl es = new EdgeAccessCntrl();

			logger.debug("partOfGrp----->"+partOfGrp);

			if( !(partOfGrp.equals("YES")) && !(partOfGrp.equals("NO")) && (grpExists.equals("exists")) ){
				processAddMember(conn,out,sProjId,"Please select a value for 'Make member part of a Group?' option !",req,sUserId,es,sUserId);

			}else if((partOfGrp.equals("YES")) && (selGrp.equals(""))){
				processAddMember(conn,out,sProjId,"Please select a Group !",req,sUserId,es,sUserId);
			}else{

			ETSUserDetails details = new ETSUserDetails();
			details.setWebId(sUserId);
			details.extractUserDetails(conn);

			if (details.getUserType() == details.USER_TYPE_EXTERNAL) {

				sAssignCompany = AmtCommonUtils.getTrimStr(req.getParameter("assign_company"));
				sAssignCty = AmtCommonUtils.getTrimStr(req.getParameter("assign_country"));
				
				if((sAssignCompany.equals("")) && (sAssignCty.equals(""))){
					processAddMember(conn,out,sProjId,"Please select a value for 'Company and Country' options !",req,sUserId,es,sUserId);

				}else if(sAssignCompany.equals("")){
					processAddMember(conn,out,sProjId,"Please select a value for 'Company' option !",req,sUserId,es,sUserId);

				}else if(sAssignCty.equals("")){
					processAddMember(conn,out,sProjId,"Please select a value for 'Country' option !",req,sUserId,es,sUserId);

				}


			} else if (details.getUserType() == details.USER_TYPE_INTERNAL) {

				sAssignCompany = details.getCompany();
				sAssignCty = "";
			}

			out.println("<form onsubmit=\"return Validate()\" name=\"AddMember\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
			
			out.println("<script type=\"text/javascript\" language=\"javascript\">");

			out.println("function Validate() {");
			out.println("Message = \"\"");
			out.println("Message = Message + CheckReason()");
			out.println("if (Message == \"\") {");
			out.println("return true");
			out.println("}");
			out.println("else {");
			out.println("alert(Message)");
			out.println("return false");
			out.println("}");
			out.println("}");
			
			out.println("function trim(sString){");
			out.println("while (sString.substring(0,1) == ' '){");
			out.println("sString = sString.substring(1, sString.length);");
			out.println("}");
				
			out.println("while (sString.substring(sString.length-1, sString.length) == ' '){");
			out.println("sString = sString.substring(0,sString.length-1);");
			out.println("}");
			out.println("return sString;");
			out.println("}");

			out.println("function CheckReason() {");
			out.println("Message = \"\"");    
			out.println("maxlength=127;"); 
			out.println("id=document.getElementById(\"cmpRsn\").value;");
			out.println("if((trim(id) == \"\") || (trim(id) == \" \")){");
			out.println("Message = Message + \"Please enter a reason for the checked users\";");
			out.println("}else if(trim(id).length > maxlength){");
			out.println("Message = Message + \"Reason must be 128 characters or less\";");
			out.println("}");       	                                 
			out.println("return Message;");
			out.println("}");
			 
			out.println("</script>");
			
			out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"443\"> ");
			//out.println("<tr><td  colspan=\"2\" valign=\"top\">This confirmation page summarizes the access you are granting access to this user:</td></tr>");
			out.println("<tr><td  colspan=\"2\" valign=\"top\">Please review the following user details and click 'Submit' to grant access to the user.</td></tr>");
			out.println("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");
			out.println("<tr valign=\"top\"><td colspan=\"2\"><b>User information:</b></td></tr>");
			out.println("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");

			out.println("<tr><td  width=\"150\" valign=\"top\" ><b>User name:</b></td><td   valign=\"top\" >" + details.getFirstName().trim() + " " + details.getLastName().trim() + "</td></tr>");
			out.println("<tr><td  width=\"150\" valign=\"top\" ><b>IBM ID:</b></td><td   valign=\"top\" >" + details.getWebId() + "</td></tr>");
			out.println("<tr><td  width=\"150\" valign=\"top\" ><b>E-mail:</b></td><td   valign=\"top\" >" + details.getEMail() + "</td></tr>");
			out.println("<tr><td  width=\"150\" valign=\"top\" ><b>Company:</b></td><td   valign=\"top\" >" + details.getCompany() + "</td></tr>");
			out.println("<tr><td  width=\"150\" valign=\"top\" ><b>Address:</b></td><td   valign=\"top\" >" + details.getStreetAddress() + "</td></tr>");

			out.println("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");

			if (details.getUserType() == details.USER_TYPE_EXTERNAL) {
				out.println("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
				out.println("<tr>");
				out.println("<td><b><label for=\"label_company\">TG SAP company name:</label></b></td><td>" + sAssignCompany + "</td></tr>");
				String usrCnty = EntitledStatic.getValue(conn, "select country_name from decaf.country where country_code = '" + sAssignCty + "' with ur");
				out.println("<tr><td><b><label for=\"wkspc\">Country:</label></b></td><td>" + usrCnty + "</td></tr>");
			}
			out.println("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			out.println("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
			String wkspcName = EntitledStatic.getValue(conn, "select project_name from ets.ets_projects where project_id = '" + sProjId + "' with ur");
			String wkspcCmp = AmtCommonUtils.getTrimStr(EntitledStatic.getValue(conn, "select company from ets.ets_projects where project_id = '" + sProjId + "' with ur"));
			out.println("<tr><td><b>Workspace name:</b></td><td>" + wkspcName + "</td></tr>");
			out.println("<tr><td><b>Workspace company:</b></td><td>" + wkspcCmp + "</td></tr>");

			String roleStr = req.getParameter("roles").trim();
			int roleid = (new Integer(roleStr)).intValue();
			String rolename = ETSDatabaseManager.getRoleName(conn, roleid);

			out.println("<tr><td><b>Privileges:</b></td><td>" + rolename + "</td></tr>");

			Global.println("WK SPACE COMPANY from ADD =====" + wkspcCmp);
			Global.println("ASSIGNED COMPANY from ADD=====" + sAssignCompany);

			if (details.getUserType() == details.USER_TYPE_EXTERNAL) {

				if (!sAssignCompany.equals(wkspcCmp)) {
					ETSProj etsProj = ETSDatabaseManager.getProjectDetails(sProjId);
					out.println("<tr><td colspan=\"2\">&nbsp;</td></tr>");
					out.println("<tr><td colspan=\"2\"><b><span style=\"color:rgb(255,0,0)\">Warning: The external user you are adding to this workspace is not profiled for this company, and requires a reason for entering and participating in this workspace.</span></b></td></tr>");
					out.println("<tr><td colspan=\"2\">&nbsp;</td></tr>");
					out.println("<tr><td><b><label for=\"label_reason\">Reason:</label></b></td><td><textarea id=\"cmpRsn\" name=\"cmpRsn\" rows=\"3\" cols=\"30\" style=\"border-style: solid;\"></textarea></td></tr>");
					if (etsProj.getProjectType().equalsIgnoreCase(Defines.AIC_WORKSPACE_TYPE)){
						out.println("<tr><td headers=\"col37a\" colspan=\"2\">Adding an external user who&#39;s ICC" 
									+ " company&#39;s profile does not match the WS company profile requires the WS Owner"
									+ " to log the external user&#39;s name, date of invite and reason for adding this "
									+ "person to the workspace.  Please create a document for this purpose and file in"
									+ " the documents section of this workspace.</td></tr>");
					}					
					out.println("<tr><td colspan=\"2\">&nbsp;</td></tr>");
				}

			}

			out.println("<tr><td><b><label for=\"label_job\">Job responsibility:</label></b></td><td>" + sJob + "</td></tr>");

			if((partOfGrp.equals("YES")) && (! selGrp.equals(""))){
			TeamGroupDAO grpDAO = new TeamGroupDAO();
			grpDAO.prepare();
			ETSGroup group = grpDAO.getGroupByIDAndProject(sProjId,selGrp);
			grpDAO.cleanup();
			out.println("<tr><td><b><label for=\"label_group\">Group:</label></b></td><td>" + group.group_name + "</td></tr>");
			}
			out.println("<tr><td colspan=\"2\">&nbsp;</td></tr>");

			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
			out.println("<td  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
			out.println("<td  align=\"left\">");
			String backto = req.getParameter("backto");
			String url = "";
			if (backto != null) {
				url = "" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?image_submit.x=y&backto=backtp&admin_op=verify_addmember&action=admin_add&add_id=" + sUserId + "&proj=" + sProjId + "&tc=" + topCatStr + "&linkid=" + linkid;
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td  width=\"25\" align=\"left\"><a href=\"" + url + "\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Back\" /></a></td><td ><a class=\"fbox\" href=\"" + url + "\">Back</a></td></tr></table>");
			} else {
				url = "" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?image_submit.x=y&admin_op=verify_addmember&action=admin_add&add_id=" + sUserId + "&proj=" + sProjId + "&tc=" + topCatStr + "&linkid=" + linkid;
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td  width=\"25\" align=\"left\"><a href=\"" + url + "\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Back\" /></a></td><td ><a class=\"fbox\" href=\"" + url + "\">Back</a></td></tr></table>");
			}
			out.println("</td></tr></table>");

			out.println("<br /><br />");

			if (backto != null) {
				out.println("<input type=\"hidden\" name=\"backto\" value=\"" + backto + "\" />");
			}
			out.println("<input type=\"hidden\" name=\"action\" value=\"admin_add\" />");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + topCatStr + "\" />");
			out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + linkid + "\" />");
			out.println("<input type=\"hidden\" name=\"admin_op\" value=\"process_addmember\" />");
			out.println("<input type=\"hidden\" name=\"add_id\" value=\"" + sUserId + "\" />");
			out.println("<input type=\"hidden\" name=\"assign_company\" value=\"" + sAssignCompany + "\" />");
			out.println("<input type=\"hidden\" name=\"assign_country\" value=\"" + sAssignCty + "\" />");
			out.println("<input type=\"hidden\" name=\"job\" value=\"" + sJob + "\" />");
			out.println("<input type=\"hidden\" name=\"roles\" value=\"" + roleStr + "\" />");
			out.println("<input type=\"hidden\" name=\"partOfGrp\" value=\"" + partOfGrp + "\" />");
			out.println("<input type=\"hidden\" name=\"selGrp\" value=\"" + selGrp + "\" />");
			out.println("</form>");
			out.println("<br /><br />");
		  }
		} catch (Exception e) {
			logger.error("execption", e);
			throw e;
		}
	 }
	private void processAddMember(Connection conn, PrintWriter out, String sProjId,String msg, HttpServletRequest req, String zVal, EdgeAccessCntrl es,String add_id) throws SQLException, Exception {
		try {

			/**
			 * 1. Find out if the entered user id is internal.
			 *    If internal, then find out if the user in amt and decaf and pull into and find out if user validated.
			 *      If not validated, then dont proceed.
			 *      If validated, then assign company as IBM and display all privilates to assign (no address validation required).
			 *    If external, then find out if the user is in amt and decaf, if not pull into amt and decaf.
			 *      Display company select box if company not assigned yet. Display address for validation.
			 *      Display only the member role as the external cannot be assigned as workspace manager.
			 *
			 */

			//out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td  height=\"18\" width=\"443\" class=\"subtitle\">Add member</td></tr></table>");
			String requesterIRUserId = es.gIR_USERN;
			String sUserId = add_id;
			if (zVal.equals("Z")) {
				sUserId = sUserId.trim();
			} else {
				sUserId = zVal.trim();
			}

			String topCatStr = req.getParameter("tc");
			String cCatStr = req.getParameter("cc");
			String linkid = req.getParameter("linkid");
			ETSProcReqAccessFunctions procFunctions = new ETSProcReqAccessFunctions();

			ETSSyncUser syncUser = new ETSSyncUser();
			syncUser.setWebId(sUserId);
			ETSStatus status = syncUser.syncUser(conn);

			if (status.getErrCode() == 0) {

				// check here itself - if the project is a sub project and
				// if it is, user is the member of parent
				ETSProj thisProj = ETSDatabaseManager.getProjectDetails(conn, sProjId);
				if (!thisProj.getParent_id().equals("0")) {
					//ETSUser etsUsr = ETSDatabaseManager.getETSUserAll(sUserId,thisProj.getParent_id(),conn);
					String cnt = EntitledStatic.getValue(conn, "select count(*) from ets.ets_users where active_flag='A' and user_id = '" + sUserId + "' and user_project_id = '" + thisProj.getParent_id() + "' with ur");
					int count = Integer.parseInt(cnt);
					if (count < 1) {
						showAddMemberScreen(conn, out, sProjId, req, -12);
						return;
					}
				}

				// things are OK. proceed.
				// check if user is internal or not..

				ETSUserDetails details = new ETSUserDetails();
				details.setWebId(sUserId);
				details.extractUserDetails(conn);

				boolean womultipoc = true;

				if (details.getUserType() == details.USER_TYPE_EXTERNAL) { //start of multi poc check for externals

					if (ETSUtils.checkUserRole(es, sProjId) == Defines.WORKSPACE_OWNER) {

						womultipoc = checkOwnerMultiPOC(conn, out, sProjId, es.gIR_USERN, req, es.gEMAIL, sUserId);

					}

				}

				if (womultipoc) {

					String projName = EntitledStatic.getValue(conn, "select project_name from ets.ets_projects where project_id = '" + sProjId + "' with ur");
					Hashtable valHash = new Hashtable();
					valHash.put("toid", details.getEMail());
					valHash.put("reqemail", ez.gEMAIL);
					if (!procFunctions.isWO(ez, sProjId)) {
						valHash.put("woemailcc", ez.gEMAIL);
					} else {
						valHash.put("woemailcc", "");
					}

					valHash.put("ibmid", details.getWebId());
					valHash.put("projname", projName);
					valHash.put("woemail", ETSUtils.getUserEmail(conn, procFunctions.woWebId(sProjId, conn)));
					valHash.put("projid", sProjId);
					valHash.put("tc", topCatStr);
					valHash.put("cc", "");
					valHash.put("action", "admin_add");
					ETSProcReqAccessGoPages goPages = new ETSProcReqAccessGoPages();
					if (details.getUserType() == details.USER_TYPE_INTERNAL_PENDING_VALIDATION) {
						System.out.println("User is in Pending Validation State" + sUserId);
						printPendingValidationErrMesg(out, sProjId, req, es, sUserId);
						return;
						
// Commented for CSR - IBMCC00010010 -- IBM internal member being asked to validate through email
//						// user internal and pending validation..
//						// cannot proceed as the user is not validated yet..
//						out.println("<input type=\"hidden\" name=\"admin_op\" value=\"verify_addmember\" />");
//						Global.Init();
//						// take the user error
//						valHash.put("option", "invalidmail");
//						out.println(goPages.errPage(2, valHash, details));
//						return;

					}
					if (details.getStreetAddress().equals("") && details.getUserType() == details.USER_TYPE_EXTERNAL) {
						out.println("<input type=\"hidden\" name=\"admin_op\" value=\"verify_addmember\" />");
						Global.Init();
						valHash.put("option", "noaddressmail");
						// take the user error
						out.println(goPages.errPage(1, valHash, details));
						return;
					}

					out.println("<form name=\"AddMember\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
					
					
					if (!((msg.trim()).equals(""))){
						out.println("<table><tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr></table>");
					}
					
					out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
					out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");
					out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
					String backto = req.getParameter("backto");
					if (backto != null) {
						out.println("<input type=\"hidden\" name=\"backto\" value=\"" + backto + "\" />");
					}
					//               out.println("<table  cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
					//               out.println("<tr><td class=\"small\">");
					//               //out.println("<b>Add member</b>");
					//               out.println("</td></tr>");
					//               out.println("</table>");
					//               out.println("<br />");

					out.println("<input type=\"hidden\" name=\"action\" value=\"admin_add\" />");
					out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
					out.println("<input type=\"hidden\" name=\"tc\" value=\"" + topCatStr + "\" />");
					out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + linkid + "\" />");

					if (details.getUserType() == details.USER_TYPE_INTERNAL || details.getUserType() == details.USER_TYPE_EXTERNAL) {
						out.println("<input type=\"hidden\" name=\"admin_op\" value=\"process_addmemberz\" />");
						out.println("<input type=\"hidden\" name=\"add_id\" value=\"" + sUserId + "\" />");

						// user internal and validated
						// display the roles and address for validation and proceed.

						out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"443\"> ");
						out.println("<tr><td  colspan=\"2\" valign=\"top\">The following are the details of the user. Please verify and validate the information before you click on Continue.</td></tr>");
						out.println("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");
						out.println("<tr valign=\"top\"><td colspan=\"2\"><b>User information:</b></td></tr>");
						out.println("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");

						out.println("<tr><td  width=\"150\" valign=\"top\" ><b>User name:</b></td><td   valign=\"top\" >" + details.getFirstName().trim() + " " + details.getLastName().trim() + "</td></tr>");
						out.println("<tr><td  width=\"150\" valign=\"top\" ><b>IBM ID:</b></td><td   valign=\"top\" >" + details.getWebId() + "</td></tr>");
						out.println("<tr><td  width=\"150\" valign=\"top\" ><b>E-mail:</b></td><td   valign=\"top\" >" + details.getEMail() + "</td></tr>");
						out.println("<tr><td  width=\"150\" valign=\"top\" ><b>Company:</b></td><td   valign=\"top\" >" + details.getCompany() + "</td></tr>");
						out.println("<tr><td  width=\"150\" valign=\"top\" ><b>Address:</b></td><td   valign=\"top\" >" + details.getStreetAddress() + "</td></tr>");

						out.println("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");

						out.println("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");

						ETSProcReqAccessGoPages GoPages = new ETSProcReqAccessGoPages();
						out.println("<tr><td colspan=\"2\"><b>IBM Export Control Regulations:</b><span class=\"small\">[</span>" + GoPages.ibmReg() + "<span class=\"small\">]</span>" + "</td></tr>");

						if (details.getUserType() == details.USER_TYPE_EXTERNAL) {
							Vector vCompanies = EntitledStatic.getValues(conn, "select distinct parent from decafobj.company_view with ur");

							out.println("<tr>");
														
							if(StringUtil.isNullorEmpty(details.getCompany())){
								out.println("<td  width=\"150\" valign=\"top\"><b><label for=\"label_company\"><span class=\"ast\"><b>*</b></span>TG SAP company name:</label></b></td>");
									out.println("<td   valign=\"top\" ><select name=\"assign_company\" id=\"assign_company\">");
									out.println("<option value=\"\">Select company</option>");
									for (int i = 0; i < vCompanies.size(); i++) {
										String sTemp = (String) vCompanies.elementAt(i);
											out.println("<option value=\"" + sTemp + "\">" + sTemp + "</option>");
										}
									out.println("</select></td>");	
		
						   }else{
						   	
						   	 out.println("<td  width=\"150\" valign=\"top\"><b><label for=\"label_company\">TG SAP company name:</label></b></td>");
						   	 out.println("<td   valign=\"top\" >" + details.getCompany() + "</td>");
						   	 out.println("<input type=\"hidden\" name=\"assign_company\" value=\"" + details.getCompany() + "\" />");
							 						   	
						   }
													
							out.println("</tr>");

							// country list only for external users
							String qry = "select rtrim(country_code),rtrim(country_name) from decaf.country order by 2 with ur";
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(qry);
							out.println("<tr>");
							
							if(StringUtil.isNullorEmpty(details.getCountryCode())){
									out.println("<td><b><label for=\"wkspc\"><span class=\"ast\"><b>*</b></span>Country:</label></b></td>");
									out.println("<td><select id=\"assign_country\" name=\"assign_country\"><option name=\"compval\" value=\"select\">Select country</option>");
									while (rs.next()) {
										String countryCode = rs.getString(1);
										out.println("<option name=\"compval\" value=\"" + countryCode + "\">" + rs.getString(2) + "</option>");
									}
										out.println("</select></td>");
							}else{
															
								String cntryName = EntitledStatic.getValue(conn, "select country_name from decaf.country where country_code = '" + details.getCountryCode() + "' with ur");
								out.println("<td><b><label for=\"wkspc\">Country:</label></b></td>");
								out.println("<td   valign=\"top\" >" + cntryName + "</td>");
								out.println("<input type=\"hidden\" name=\"assign_country\" value=\"" + details.getCountryCode() + "\" />");
								
							}
							
							out.println("</tr>");
							out.println("<tr><td colspan=\"2\">&nbsp;</td></tr>");
						} else {
							out.println("<tr><td colspan=\"2\">&nbsp;</td></tr>");
							out.println("<tr><td colspan=\"2\">Export regulation check is not required because this is an IBM internal user</td></tr>");
							out.println("<tr><td colspan=\"2\">&nbsp;</td></tr>");
						}
						out.println("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");

						out.println("<tr>");
						out.println("<td  width=\"150\" valign=\"top\" ><b>Privileges:</b></td>");
						out.println("<td   valign=\"top\" >");

						Vector r = ETSDatabaseManager.getRolesPrivs(sProjId, conn);

						ETSProjectInfoBean projBean = ETSUtils.getProjInfoBean(conn);

						out.println("<table cellpadding=\"0\" cellspacing=\"0\" width=\"293\" border=\"0\">");

						for (int i = 0; i < r.size(); i++) {
							String[] rp = (String[]) r.elementAt(i);
							int roleid = (new Integer(rp[0])).intValue();
							String rolename = rp[1];
							//String privs = rp[2];
							String privids = rp[3];

							if (details.getUserType() == details.USER_TYPE_INTERNAL) {
								if (!(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.OWNER, conn))) {
									out.println("<tr>");
									out.println("<td align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_" + i + "\" type=\"radio\" name=\"roles\" value=\"" + roleid + "\" checked=\"checked\" /></td>");
									out.println("<td align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"role_" + i + "\">" + rolename + "</label></td>");
									out.println("</tr>");
									out.println("<tr>");
									out.println("<td>&nbsp;</td>");
									out.println("<td align=\"left\" valign=\"top\">Privileges: ");
									String priv_desc = "";
									StringTokenizer st = new StringTokenizer(privids, ",");
									Vector privs = new Vector();
									while (st.hasMoreTokens()) {
										String priv = st.nextToken();
										privs.addElement(priv);
									}
									for (int j = 0; j < privs.size(); j++) {
										String s = (String) privs.elementAt(j);
										String desc = projBean.getInfoDescription("PRIV_" + s, 0);
										if (!desc.equals("")) {
											if (!priv_desc.equals("")) {
												priv_desc = priv_desc + "; " + desc;
											} else {
												priv_desc = desc;
											}
										}
									}
									out.println(priv_desc);
									out.println("</td>");
									out.println("</tr>");
									out.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
								}
							}
							if (details.getUserType() == details.USER_TYPE_EXTERNAL) {
								if (!(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.OWNER, conn)) && !(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.ADMIN, conn))) {
									out.println("<tr>");
									out.println("<td align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_" + i + "\" type=\"radio\" name=\"roles\" value=\"" + roleid + "\" checked=\"checked\" /></td>");
									out.println("<td align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"role_" + i + "\">" + rolename + "</label></td>");
									out.println("</tr>");
									out.println("<tr>");
									out.println("<td>&nbsp;</td>");
									out.println("<td align=\"left\" valign=\"top\">Privileges: ");
									String priv_desc = "";
									StringTokenizer st = new StringTokenizer(privids, ",");
									Vector privs = new Vector();
									while (st.hasMoreTokens()) {
										String priv = st.nextToken();
										privs.addElement(priv);
									}
									for (int j = 0; j < privs.size(); j++) {
										String s = (String) privs.elementAt(j);
										String desc = projBean.getInfoDescription("PRIV_" + s, 0);
										if (!desc.equals("")) {
											if (!priv_desc.equals("")) {
												priv_desc = priv_desc + "; " + desc;
											} else {
												priv_desc = desc;
											}
										}
									}
									out.println(priv_desc);
									out.println("</td>");
									out.println("</tr>");
									out.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
								}
							}
						}
						out.println("</table>");
						out.println("</td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td  width=\"150\" valign=\"top\" ><b><label for=\"label_job\">Job responsibility:</label></b></td>");
						out.println("<td   valign=\"top\" ><input type=\"text\" id=\"label_job\" name=\"job\" value=\"\" /></td>");
						out.println("</tr>");

						TeamGroupDAO grpDAO = new TeamGroupDAO();
						grpDAO.prepare();
						Vector allGrpVect = grpDAO.getAllGroupsForEdit(sProjId,"","ASC",false,requesterIRUserId);
						Vector userGrp = new Vector();
						grpDAO.cleanup();
						String grpExists = "";

						if(!allGrpVect.isEmpty()){
							// If Groups are present for the current project
							grpExists="exists";
						if (details.getUserType() == details.USER_TYPE_INTERNAL) {
							for (int i=0; i < allGrpVect.size(); i++) {
								ETSGroup group = (ETSGroup) allGrpVect.elementAt(i);
								userGrp.addElement(group);
						  }
						}else if(details.getUserType() == details.USER_TYPE_EXTERNAL) {
						   for (int i=0; i < allGrpVect.size(); i++) {
							   ETSGroup group = (ETSGroup) allGrpVect.elementAt(i);
							   if(group.getGroupSecurityClassification().equals("0"))
									userGrp.addElement(group);
						   }
					   }

					   	if(!userGrp.isEmpty()){
						grpExists="exists";
						out.println("<tr>");
						out.println("<td colspan=\"2\" valign=\"top\" ><b><label for=\"label_partOfGrp\"><span class=\"ast\"><b>*</b></span>Make member part of a Group?</label></b>");
						out.println("<input type=\"radio\" name=\"partOfGrp\" id=\"partOfGrp\" value=\"YES\" />Yes &nbsp;&nbsp;");
						out.println("<input type=\"radio\" name=\"partOfGrp\" id=\"partOfGrp\" value=\"NO\" />No</td>");
					    out.println("</tr>");

						out.println("<tr>");
						out.println("<td valign=\"top\" nowrap=\"nowrap\"><b>Select a Group:</b></td>");
						out.println("<td valign=\"top\"><select name=\"selGrp\" id=\"selGrp\" >");
						out.println("<option value=\"\" selected=\"selected\">Select a Group</option>");

						for (int i=0; i < userGrp.size(); i++) {
							ETSGroup group = (ETSGroup) userGrp.elementAt(i);
							out.println("<option value=\""+group.group_id+"\">" +group.group_name+ "</option>");
							}
						out.println("</select></td>");
						out.println("</tr>");
					   	} // if(!userGrp.isEmpty())
					   	else{
							grpExists="empty";
							out.println("<tr><td colspan=\"2><span style=\"color:#000000\">Presently no groups exist for this external user.</span></td></tr>");
					   	}
						} // if(!allGrpVect.isEmpty())
						else{
							grpExists="empty";
							out.println("<tr><td colspan=\"2><span style=\"color:#000000\">Presently no groups exist for the workspace.</span></td></tr>");
						}
						out.println("<input type=\"hidden\" name=\"grpExists\" value=\"" + grpExists + "\" />");
						out.println("<tr><td colspan=\"2\">&nbsp;</td></tr>");

						out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
						out.println("<td  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
						out.println("<td  align=\"left\">");
						out.println(
							"  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td  width=\"25\" align=\"left\"><a href=\""
								+ Defines.SERVLET_PATH
								+ "ETSProjectsServlet.wss?proj="
								+ sProjId
								+ "&tc="
								+ topCatStr
								+ "&linkid="
								+ linkid
								+ "\"><img src=\""
								+ Defines.BUTTON_ROOT
								+ "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td ><a class=\"fbox\" href=\""
								+ Defines.SERVLET_PATH
								+ "ETSProjectsServlet.wss?proj="
								+ sProjId
								+ "&tc="
								+ topCatStr
								+ "&linkid="
								+ linkid
								+ "\">Cancel</a></td></tr></table>");
						out.println("</td></tr></table>");

						out.println("<br /><br />");
					}

					if (details.getUserType() == details.USER_TYPE_INVALID) {
						// user invalid (does not exist in web id or exists did not agree to EAI).
						out.println("<input type=\"hidden\" name=\"add_id\" value=\"" + sUserId + "\" />");
						out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"443\"> ");

						// divider
						out.println("<tr valign=\"top\">");
						out.println("<td colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
						out.println("</tr>");

						out.println("<tr>");
						out.println("<td  colspan=\"2\" valign=\"top\" ><span style=\"color:#ff3333\"><b>The IBM ID entered is not a valid Id. The reason for not valid is either the ID is not registered or the user has not accepted the IBM Customer Connect agreement.</b></span><br /><br />");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td colspan=\"2\"><hr noshade=\"noshade\" size=\"1\" /></td>");
						out.println("</tr>");

						out.println("</table>");
						out.println("</td></tr></table>");
						out.println("</td></tr></table>");

						out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
						out.println("<td  width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&linkid=" + linkid + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\" width=\"120\" height=\"21\" alt=\"continue\" /></a></td>");
						out.println("<td  align=\"left\">");
						out.println(
							" <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td  width=\"25\" align=\"left\"><a href=\""
								+ Defines.SERVLET_PATH
								+ "displayAddMembrInput.wss?action=admin_add&proj="
								+ sProjId
								+ "&tc="
								+ topCatStr
								+ "&linkid="
								+ linkid
								+ "\"><img src=\""
								+ Defines.BUTTON_ROOT
								+ "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td >&nbsp;&nbsp;<a href=\""
								+ Defines.SERVLET_PATH
								+ "displayAddMembrInput.wss?action=admin_add&proj="
								+ sProjId
								+ "&tc="
								+ topCatStr
								+ "&linkid="
								+ linkid
								+ "\">Cancel</a></td></tr></table>");
						out.println("</tr></table>");

						out.println("<br /><br />");
					}

				} //end of multi-poc check for externals

			} else {
				// not found or some other error

				showAddMemberScreen(conn, out, sProjId, req, -10);
				// ? show invitation
			}
			out.println("</form>");
			out.println("<br /><br />");

		} catch (Exception e) {
			logger.error("execption", e);
			throw e;
		}
	}

	/**
	 * Method submitAddMember.
	 * @param conn
	 * @param out
	 * @param sProjId
	 * @param sIRId
	 * @param req
	 * @throws SQLException
	 * @throws Exception
	 */
	private void submitAddMember(Connection conn, PrintWriter out, String sProjId, String sIRId, HttpServletRequest req, String sEmail) throws SQLException, Exception {

		try {

			/**
			 * Check to see if this user has ETS_PROJECTS entitlement.
			 *    If yes, then add him into this workspace
			 *    If no, then request the project and add him into workspace.
			 *
			 */
			out.println("<form name=\"AddMember\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");

			//            out.println("<table  cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
			//            out.println("<tr><td class=\"small\">");
			//            //out.println("<b>Add member</b>");
			//            out.println("</td></tr>");
			//            out.println("</table>");

			//out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td  height=\"18\" width=\"443\" class=\"subtitle\">Add member</td></tr></table>");
			out.println("<br />");

			String topCatStr = req.getParameter("tc");
			String linkid = req.getParameter("linkid");

			out.println("<input type=\"hidden\" name=\"action\" value=\"admin_add\" />");
			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"admin_op\" value=\"process_addmember\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + topCatStr + "\" />");

			String sUserId = req.getParameter("add_id");
			sUserId = sUserId.trim();

			String sCmpRsn = req.getParameter("cmpRsn");
			if (sCmpRsn == null || sCmpRsn.trim().equals("")) {
				sCmpRsn = "";
			} else {
				sCmpRsn = sCmpRsn.trim();
			}
			
			String sAssignCompany = req.getParameter("assign_company");
			if (sAssignCompany == null || sAssignCompany.trim().equals("")) {
				sAssignCompany = "";
			} else {
				sAssignCompany = sAssignCompany.trim();
			}

			String sAssignCty = req.getParameter("assign_country");
			if (sAssignCty == null || sAssignCty.trim().equals("")) {
				sAssignCty = "";
			} else {
				sAssignCty = sAssignCty.trim();
			}

			String sJob = req.getParameter("job");
			if (sJob == null || sJob.trim().equals("")) {
				sJob = "";
			} else {
				sJob = sJob.trim();
			}

			String partOfGrp = req.getParameter("partOfGrp");
			if (partOfGrp == null || partOfGrp.equals("")) {
				partOfGrp = "";
			} else {
				partOfGrp = partOfGrp.trim();
			}

			String selGrp = req.getParameter("selGrp");
			if (selGrp == null || selGrp.equals("")) {
				selGrp = "";
			} else {
				selGrp = selGrp.trim();
			}

			if((partOfGrp.equals("YES")) && (! selGrp.equals(""))){
				boolean add = false;
				int iResult = 0;
				PreparedStatement stmt = null;
				conn = ETSDBUtils.getConnection();
				TeamGroupDAO grpDAO = new TeamGroupDAO();
				grpDAO.prepare();
				ETSGroup group = grpDAO.getGroupByIDAndProject(sProjId,selGrp);
				EdgeAccessCntrl es = new EdgeAccessCntrl();
				String strLastUserName = es.gIR_USERN;
				String strAddUser ="('"+ group.getGroupId() + "','"+ sUserId + "','" + strLastUserName + "',current timestamp)";

				StringBuffer sQuery = new StringBuffer("");
				sQuery.append("DELETE FROM ETS.USER_GROUPS WHERE ");
				sQuery.append("GROUP_ID IN (SELECT GROUP_ID FROM ETS.GROUPS WHERE PROJECT_ID=?) AND USER_ID=?");
				logger.debug("ETSAddMember::SubmitAddMember()::QUERY : " + sQuery.toString());
				stmt = conn.prepareStatement(sQuery.toString());
				stmt.setString(1,sProjId);
				stmt.setString(2,sUserId);
				iResult = stmt.executeUpdate();
				logger.debug("DELETE ACTION ---->"+iResult);
				conn.commit();
				conn.setAutoCommit(true);
				add = grpDAO.updateGroupMembersList(strAddUser,"",group);
				logger.debug("ADD ACTION ---->"+add);
				grpDAO.cleanup();
			}

			ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, sProjId);

			ETSUserDetails details = new ETSUserDetails();
			details.setWebId(sUserId);
			details.extractUserDetails(conn);

			//541 new//
			String reqstdEntitlement = WrkSpcTeamUtils.getWrkSpcReqEntitlement(proj);
			logger.debug("reqstdEntitlement entitlement in ADD MEMBER=== " + reqstdEntitlement);

			String reqstdProject = WrkSpcTeamUtils.getWrkSpcReqProject(proj);
			logger.debug("reqstdProject in ADD MEMBER ==" + reqstdProject);

			boolean bEntitled = userHasEntitlement(conn, sUserId, reqstdEntitlement);
			ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
			boolean bHasPendEtitlement = procFuncs.userHasPendEntitlement(conn, details.getEdgeId(), reqstdProject);
			boolean bStatus = false;
			boolean bSuccess = false;
			boolean bReqEnt = false;
			boolean bCreateRequest = false;
			// changed the following in a CSR (CSR IBMCC00006191) as follows..
			// if the person logged in is a workspace manager and the person
			// being added is an external user without entitlement, then
			// just create a request to workspace owner...

			// check if the user is external or not...
			if (details.getUserType() == details.USER_TYPE_EXTERNAL) {

				// the user is external and does not have the entitlement..
				// just create it as a request to the workspace owner...

				// find out if the logged in person is a manager or an owner...

				boolean workspaceowner = ETSDatabaseManager.hasProjectPriv(sIRId, proj.getProjectId(), Defines.OWNER, conn);

				if (workspaceowner) {

					// user not entitled, request project for him..
					//TODO: CSR IBMCC00007115
					if (!bEntitled && !bHasPendEtitlement) {
						if (proj.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)) {
							bStatus = setDefaultAICDecafEntitlementExtUsers(conn,details.getEdgeId());
							logger.debug("Status of Create default Entitlement for AIC user ++ " + bStatus);
							if ( bStatus ) {
								bEntitled = true;
								//send inforrmational email to the POC owner for ext user
								if (!details.getPocEmail().equals(ez.gEMAIL) 
									&& (details.getPocEmail() !=null) 
									&& !details.getPocEmail().equals("")) {
									sendPOCInformationEmail(conn,proj.getProjectId(),proj.getName(), sUserId);
								}
							}
						} else {

							ETSUserAccessRequest reqAccess = new ETSUserAccessRequest();
							reqAccess.setTmIrId(sUserId);
							reqAccess.setPmIrId(sIRId);
							reqAccess.setIsAProject(WrkSpcTeamUtils.isReqAccessProject(proj.getProjectType()));
							if (!sAssignCompany.trim().equals("")) {
								reqAccess.setUserCompany(sAssignCompany);
							} else {
								reqAccess.setUserCompany(details.getCompany());
							}
							if (!sAssignCty.trim().equals("")) { // set the country
								EntitledStatic.fireUpdate(conn, "update decaf.users set country_code = '" + sAssignCty + "'  where userid = '" + details.getEdgeId() + "'");
							}

							reqAccess.setDecafEntitlement(reqstdProject);
							ETSStatus status = reqAccess.request(conn);

							if (status.getErrCode() == 0) {
								bStatus = true;
								bReqEnt = true;
							} else {
								bStatus = false;
							}
						}
					} else {
						// since the user has an entitlement, just add the member
						// to the workspace CSR: IBMCC00007177
						bStatus = true;
					}

					logger.debug("bStatus flag value in add member Internal==" + bStatus);
					logger.debug("bReqEnt flag value in add member Internal==" + bReqEnt);

				} else {
					// the person logged in is a workspace manager.. so create a request..

					ETSUserAccessRequest uar = new ETSUserAccessRequest();

					Vector owners = ETSDatabaseManager.getUsersByProjectPriv(proj.getProjectId(), Defines.OWNER, conn);

					if (owners.size() > 0) {

						ETSUser owner = (ETSUser) owners.elementAt(0); // take the first

						UserObject uo = AccessCntrlFuncs.getUserObject(conn, owner.getUserId(), true, false);
						String owner_email = uo.gEMAIL;

						uar.recordRequest(conn, sUserId, proj.getName(), owner_email, sIRId, proj.getProjectType());

						Metrics.appLog(conn, sIRId, WrkSpcTeamUtils.getMetricsLogMsg(proj, "Team_Add_Request"));

						ETSUserDetails u = new ETSUserDetails();
						u.setWebId(sUserId);
						u.extractUserDetails(conn);

						boolean sent = ETSBoardingUtils.sendManagerEMail(u, owner_email, proj, sEmail);

						bCreateRequest = true;
					}
				}
			} else {
				// internal user
				// user not entitled, request project for him..
				//TODO: CSR IBMCC00007115
				if (!bEntitled && !bHasPendEtitlement) {
					ETSUserAccessRequest reqAccess = new ETSUserAccessRequest();
					reqAccess.setTmIrId(sUserId);
					reqAccess.setPmIrId(sIRId);
					reqAccess.setIsAProject(WrkSpcTeamUtils.isReqAccessProject(proj.getProjectType()));
					reqAccess.setUserCompany(details.getCompany());
					reqAccess.setDecafEntitlement(reqstdProject);
					ETSStatus status = reqAccess.request(conn);

					if (status.getErrCode() == 0) {
						bStatus = true;
						bReqEnt = true;
					} else {
						bStatus = false;
					}
				} else {
					// since the user has an entitlement, just add the member
					// to the workspace CSR: IBMCC00007177
					bStatus = true;
				}
			}

			logger.debug("bStatus flag value in add member Internal==" + bStatus);
			logger.debug("bReqEnt flag value in add member Internal==" + bReqEnt);

			if (bStatus && !bCreateRequest) {

				String roleStr = req.getParameter("roles").trim();
				int roleid = (new Integer(roleStr)).intValue();

				ETSUser new_user = new ETSUser();
				new_user.setUserId(sUserId);
				new_user.setRoleId(roleid);
				new_user.setProjectId(sProjId);
				new_user.setUserJob(sJob);
				new_user.setPrimaryContact(Defines.NO);
				new_user.setLastUserId(sIRId);

				String[] res = ETSDatabaseManager.addProjectMember(new_user, conn);
				if (details.getUserType() == details.USER_TYPE_EXTERNAL) {
					
					if(! StringUtil.isNullorEmpty(sCmpRsn)){
	                	int updt = 0;
	                	String rsn = sCmpRsn.trim();
	                	updt = AddMembrToWrkSpcDAO.insertReasonToAdminLog(sUserId,sProjId,rsn,conn);
	                	if(updt == 1) logger.debug("This Reason to ets_admin_log is updated  == " +rsn);
	                }
					
					if (!sAssignCompany.trim().equals("")) {
						EntitledStatic.fireUpdate(conn, "update decaf.users set assoc_company = '" + sAssignCompany + "'  where userid = '" + details.getEdgeId() + "'");
					} else {
						EntitledStatic.fireUpdate(conn, "update decaf.users set assoc_company = '" + details.getCompany() + "'  where userid = '" + details.getEdgeId() + "'");
					}
					if (!sAssignCty.trim().equals("")) { // set the country
						EntitledStatic.fireUpdate(conn, "update decaf.users set country_code = '" + sAssignCty + "'  where userid = '" + details.getEdgeId() + "'");
					} else {
						EntitledStatic.fireUpdate(conn, "update decaf.users set country_code = '" + details.getCountryCode() + "'  where userid = '" + details.getEdgeId() + "'");
					}
				}

				String success = res[0];

				logger.debug("success flag value in add member==" + success);

				if (success.equals("0")) {

					if(bEntitled) {

						EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'A'  where user_id = '" + sUserId + "' and user_project_id = '" + sProjId + "'");

					}

					else {  //not entitled

					if (bReqEnt) { // entitlement requested - so the active flag is "P"
						EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'P'  where user_id = '" + sUserId + "' and user_project_id = '" + sProjId + "'");
					} else { // "A"
						if (bHasPendEtitlement) {
							EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'P'  where user_id = '" + sUserId + "' and user_project_id = '" + sProjId + "'");
						}
					}

					}

					Metrics.appLog(conn, sIRId, WrkSpcTeamUtils.getMetricsLogMsg(proj, "Team_Add"));

					bSuccess = true;
				} else {
					bSuccess = false;
				}

			}

			out.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"443\"> ");

			out.println("<tr valign=\"top\">");
			out.println("<td ><hr noshade=\"noshade\" size=\"1\" /></td>");
			out.println("</tr>");

			out.println("<tr>");

			if (bCreateRequest) {
				out.println("<td  valign=\"top\" >A request has been forwarded to the workspace owner because this user is new and requires additional processing.</td>");
			} else {
				ETSAccessRequest ar = new ETSAccessRequest();
				ar.setProjName(proj.getName());
				ar.setMgrEMail(this.ez.gEMAIL);

				if (bSuccess) {
					if (bReqEnt) {
						ETSBoardingUtils.sendAcceptEMailPendEnt(details, ar, ar.getProjName(), "", "", this.ez.gEMAIL);
						if (details.getUserType() == details.USER_TYPE_INTERNAL) {
							out.println("<td  valign=\"top\" >The user has been added to the workspace successfully. The corresponding entitlement to access " + unBrandedprop.getAppName() + " has been requested for the user and is pending with their manager.</td>");
						} else {
							ETSUserDetails u = new ETSUserDetails();
							u.setWebId(sUserId);
							u.extractUserDetails(conn);

							if (u.getPocEmail().equals(ez.gEMAIL)) {
								out.println("<td  valign=\"top\" >The user has been added to the workspace successfully. The corresponding entitlement to access " + unBrandedprop.getAppName() + " has been requested for the user.</td>");
							} else {
								out.println("<td  valign=\"top\" >The user has been added to the workspace successfully. The corresponding entitlement to access " + unBrandedprop.getAppName() + " has been requested for the user and is pending their IBM contact.</td>");
							}
						}
					} else {
						ETSBoardingUtils.sendAcceptEMail(details, ar, ar.getProjName(), "", "", this.ez.gEMAIL);
						out.println("<td  valign=\"top\" >The user has been added to the workspace successfully.</td>");
					}
				} else {
					out.println("<td  valign=\"top\" ><span style=\"color:#ff3333\">An error occured when adding the user to workspace. Please try again later.</span></td>");
				}
			}
			out.println("</tr>");

			out.println("<tr valign=\"top\">");
			out.println("<td ><hr noshade=\"noshade\" size=\"1\" /></td>");
			out.println("</tr>");

			out.println("</table>");

			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
			out.println("<td  align=\"left\">");
			out.println(
				" <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td  width=\"25\" align=\"left\"><a href=\""
					+ Defines.SERVLET_PATH
					+ "ETSProjectsServlet.wss?proj="
					+ sProjId
					+ "&tc="
					+ topCatStr
					+ "&linkid="
					+ linkid
					+ "\"><img src=\""
					+ Defines.ICON_ROOT
					+ "fw.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Team listing\" /></a></td><td >&nbsp;&nbsp;<a href=\""
					+ Defines.SERVLET_PATH
					+ "ETSProjectsServlet.wss?proj="
					+ sProjId
					+ "&tc="
					+ topCatStr
					+ "&linkid="
					+ linkid
					+ "\">Team listing</a></td></tr></table></td>");
			String backTo = req.getParameter("backto");
			if (backTo != null) {
				String sSetMetTopCat = ETSUtils.getTopCatId(conn, proj.getProjectId(), Defines.SETMET_VT);
				String url = "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + sSetMetTopCat + "&etsop=fromteam&linkid=" + unBrandedprop.getLinkID() + "";
				out.println("<td  align=\"left\">");
				out.println(
					" <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td  width=\"25\" align=\"left\"><a href=\""
						+ Defines.SERVLET_PATH
						+ url
						+ "\"><img src=\""
						+ Defines.ICON_ROOT
						+ "fw.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Back to Set/Met\" /></a></td><td >&nbsp;&nbsp;<a href=\""
						+ Defines.SERVLET_PATH
						+ url
						+ "\">Back to Set/Met</a></td></tr></table></td>");
			}
			out.println("<td  align=\"left\">");
			out.println(
				" <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td  width=\"25\" align=\"left\"><a href=\""
					+ Defines.SERVLET_PATH
					+ "displayAddMembrInput.wss?action=admin_add&proj="
					+ sProjId
					+ "&tc="
					+ topCatStr
					+ "&linkid="
					+ linkid
					+ "\"><img src=\""
					+ Defines.ICON_ROOT
					+ "fw.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Add member\" /></a></td><td >&nbsp;&nbsp;<a href=\""
					+ Defines.SERVLET_PATH
					+ "displayAddMembrInput.wss?action=admin_add&proj="
					+ sProjId
					+ "&tc="
					+ topCatStr
					+ "&linkid="
					+ linkid
					+ "\">Add member</a></td></tr></table></td>");

			out.println("</tr></table>");

			out.println("<br /><br />");
			out.println("</form>");
			out.println("<br /><br />");

		} catch (Exception e) {
			logger.error("execption", e);
			throw e;
		}

	}

	/**
	 * Method userHasEntitlement.
	 * @param con
	 * @param sIRId
	 * @param sEntitlement
	 * @return boolean
	 * @throws SQLException
	 * @throws Exception
	 */
	private static boolean userHasEntitlement(Connection con, String sIRId, String sEntitlement) throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		boolean bEntitled = false;

		try {
			boolean isIBMer = ETSUtils.isIBMer(sIRId, con);
			// 6.1 Internal users are not required to have Sales_collab entitlement
			if (isIBMer && sEntitlement.equalsIgnoreCase(Defines.COLLAB_CENTER_ENTITLEMENT)) {
				bEntitled = true;
			} else {

				sQuery.append("SELECT ENTITLEMENT FROM AMT.S_USER_ACCESS_VIEW WHERE ENTITLEMENT = ? AND USERID = (SELECT EDGE_USERID FROM AMT.USERS WHERE IR_USERID=?) with ur");

				SysLog.log(SysLog.DEBUG, "ETSUtils::userHasEntitlement()", "QUERY : " + sQuery.toString());

				stmt = con.prepareStatement(sQuery.toString());
				stmt.setString(1, sEntitlement);
				stmt.setString(2, sIRId);

				rs = stmt.executeQuery();

				if (rs.next()) {
					if (rs.getString(1).trim().equalsIgnoreCase(sEntitlement)) {
						bEntitled = true;
					} else {
						bEntitled = false;
					}
				} else {
					bEntitled = false;
				}
			}
		} catch (SQLException e) {
			logger.error("execption", e);
			throw e;
		} catch (Exception e) {
			logger.error("execption", e);
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return bEntitled;
	}

	/**
	 * Method validateAddMember.
	 * @param con
	 * @param sProjId
	 * @param req
	 * @return int
	 * @throws Exception
	 */
	private static int validateAddMember(Connection con, String sProjId, HttpServletRequest req) throws Exception {

		String userid = (String) req.getAttribute("add_id");
		if(!StringUtil.isNullorEmpty(userid))  userid = userid.trim();

		if (userid == null || userid.trim().equals("")) {
			return 1;
		}

		boolean exists = ETSDatabaseManager.isUserInProjectForDel(userid, sProjId, con); // Check for any type of user
		if (exists) {
			return 2;
		}

		//boolean hasPending = getPendingRequest(con,userid,sProjId);
		//if (hasPending) {
		//return 3;
		//}

		return 0;

	}

	private static boolean getPendingRequest(Connection con, String userid, String sProjId) throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		boolean bExists = false;

		try {

			sQuery.append("SELECT REQUEST_ID FROM ETS.ETS_ACCESS_REQ WHERE USER_ID = ? AND PROJECT_NAME = (SELECT PROJECT_NAME FROM ETS.ETS_PROJECTS WHERE PROJECT_ID = ?) AND STATUS = ? with ur");

			SysLog.log(SysLog.DEBUG, "ETSAddMember::getPendingRequest()", "QUERY : " + sQuery.toString());

			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1, userid);
			stmt.setString(2, sProjId);
			stmt.setString(3, Defines.ACCESS_PENDING);

			rs = stmt.executeQuery();

			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					bExists = true;
				} else {
					bExists = false;
				}
			} else {
				bExists = false;
			}

		} catch (SQLException e) {
			logger.error("execption", e);
			throw e;
		} catch (Exception e) {
			logger.error("execption", e);
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return bExists;
	}

	public boolean checkOwnerMultiPOC(Connection con, PrintWriter out, String sProjId, String sIRUserId, HttpServletRequest request, String sIREmailId, String sUserId) throws SQLException, Exception {

		WrkSpcTeamActionsInpModel actInpModel = new WrkSpcTeamActionsInpModel();
		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();
		actInpModel.setUserId(sUserId);
		actInpModel.setRequestorId(sIRUserId);
		actInpModel.setWrkSpcId(sProjId);
		boolean req = false;

		WrkSpcTeamUtils wrkSpcTmUtils = new WrkSpcTeamUtils();
		WrkSpcTeamObjKey teamObjKey = wrkSpcTmUtils.getWrkSpcTeamObjDets(actInpModel);

		//		extract user dets
		ETSUserDetails etsUserDets = teamObjKey.getEtsUserIdDets();

		//extract requestor dets
		ETSUserDetails reqUserDets = teamObjKey.getReqUserIdDets();

		//extract owner info for the given wrk spc
		ETSUserDetails ownerDets = teamObjKey.getWrkSpcOwnrDets();

		AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();
		AddMembrProcDataPrep addMmbrBdlg = new AddMembrProcDataPrep();

		boolean womultipoc = wrkSpcDao.userHasEntitlement(ownerDets.getWebId(), Defines.MULTIPOC);

		// check if the user is external or not...
		if (etsUserDets.getUserType() == etsUserDets.USER_TYPE_EXTERNAL) {

			logger.debug("MULTI-POC EXISTS FOR OWNER USER ID ==" + womultipoc);

			//if not multipoc request entitlement for WO and continue the process
			if (!womultipoc) {

				logger.debug(" START MULTI-POC ENT OWNER USER ID ==" + ownerDets.getWebId());

				actOpModel = addMmbrBdlg.requestMultiPocForWO(ownerDets, reqUserDets);

				logger.debug("END MULTI-POC ENT OWNER USER ID ==" + ownerDets.getWebId());

				printMultiErrMsg(out, sProjId, request, con, ownerDets.getEdgeId());

			}

		}

		String retCode = "";

		if (!womultipoc) {

			retCode = AmtCommonUtils.getTrimStr(actOpModel.getRetCode());

			logger.debug("RET CODE FOR MULTI POC REQUEST===" + retCode);

		}

		return womultipoc;

	}

	public void printMultiErrMsg(PrintWriter out, String sProjId, HttpServletRequest req, Connection conn, String sOwnerEdgeId) throws SQLException, Exception {

		String topCatStr = req.getParameter("tc");
		String linkid = req.getParameter("linkid");

		out.println("<br />");
		out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td >");
		out.println("<span style=\"color:#ff3333\">");
		out.println("You do not have MultiPOC entitlement to perform the Add member task. MultiPOC entitlement has been requested for you. You should be able to add the member, once your entitlement request has been approved.");
		out.println("</span>");
		out.println("</td></tr>");

		String sMgrEmail = AmtCommonUtils.getTrimStr(ETSUtils.getManagersEMailFromDecafTable(conn, sOwnerEdgeId));
		String sMgrName = AmtCommonUtils.getTrimStr(ETSUtils.getManagerNameFromDecafTable(conn, sOwnerEdgeId));

		String mgrInfoStr = sMgrName + " " + "[Email : " + sMgrEmail + "]";

		out.println("<tr><td>&nbsp;</td></tr>");

		if (AmtCommonUtils.isResourceDefined(sMgrEmail)) {
			out.println("<tr>");
			out.println("<td  align=\"left\">Note : MultiPOC entitlement request has been forwarded to your manager , <b>" + mgrInfoStr + "</b> for approval. You will receive an e-mail from \"IBM Customer Connect\" when this has been processed.</td>");
			out.println("</tr>");
		}
		out.println("<tr><td>&nbsp;</td></tr>");
		out.println("<tr><td>&nbsp;</td></tr>");
		out.println("</table>");

		out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td  align=\"right\" valign=\"top\" width=\"16\">");
		out.println("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
		out.println("</td> ");
		out.println("<td  align=\"left\" valign=\"top\">");
		out.println("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
		out.println("</td> ");
		out.println("<tr>");
		out.println("</table>");

	}
	
	public void printPendingValidationErrMesg(PrintWriter out, String sProjId, HttpServletRequest req, EdgeAccessCntrl es, String sUserId) throws SQLException, Exception {

		String topCatStr = req.getParameter("tc");
		String linkid = req.getParameter("linkid");

		out.println("<br />");
		out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td >");
		out.println("<li>This ID <b>" + sUserId + "</b> is in an obsolete state. Please contact the " +
					"help desk <a href=\"mailto:econnect@us.ibm.com\" >econnect@us.ibm.com</a> to have them update the ID to a valid internal user. " +
					"Once this has been updated you can add them to the workspace.</li>");
		out.println("</td></tr>");


		out.println("<tr><td>&nbsp;</td></tr>");

		out.println("<tr><td>&nbsp;</td></tr>");
		out.println("<tr><td>&nbsp;</td></tr>");
		out.println("</table>");

		out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
		out.println("<tr>");
		out.println("<td  align=\"right\" valign=\"top\" width=\"16\">");
		out.println("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
		out.println("</td> ");
		out.println("<td  align=\"left\" valign=\"top\">");
		out.println("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
		out.println("</td> ");
		out.println("<tr>");
		out.println("</table>");
		
		
	}
	
	public static boolean setDefaultAICDecafEntitlementExtUsers(Connection conn, String sEdgeId)
	{
		boolean bInsertEnt = false;
		ArrayList  entProjList = new ArrayList();

		try{
			String role_id=EntitledStatic.getValue(conn,"select roles_id from decaf.roles " +
				  "where roles_name='" + Defines.AIC_ENTITLEMENT + "' for read only");

			entProjList.add(role_id);

			DecafCreateEnt crtent= new DecafCreateEnt();
			crtent.setUserid(sEdgeId);
			crtent.setIsEnt(true);
			crtent.setEntprojList(entProjList);
			int i = crtent.insertEntProj(conn);
			System.out.println("i===="+i);
			if (i == 0) {
				bInsertEnt = true;
			}
		}
		catch(Exception e){
			  e.printStackTrace();

		}


		return bInsertEnt;
	}
	/*
	 * This method will send mail to the POC for extrenal users if the user has been granted access
	 * to AIC by decaf API directly 
	 * @author vishal
	 */
	private boolean sendPOCInformationEmail(Connection conn, String sProjId, String sProjName, String sNewUser) 
	throws Exception {
		StringBuffer sEmailStr = new StringBuffer("");
		boolean bSent = false;

		try {
			
			ETSUserDetails new_user = new ETSUserDetails();
			new_user.setWebId(sNewUser);
			new_user.extractUserDetails(conn);
			
			// country for external users
			String qry = "select rtrim(country_code),rtrim(country_name) from decaf.country where country_code='" + new_user.getCountryCode() + "'  with ur";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(qry);
			String countryName = "";
			while (rs.next()) {
				countryName = rs.getString(2);
			}
			
			String appName = unBrandedprop.getAppName();
			String sEmailSubject = appName + " - Access granted to  " + sNewUser ;

			sEmailStr.append("This is an informational email since you are the POC\n");
			sEmailStr.append("for the below user. This user will now be able to access the following Collaboration center: " + sProjName + ".\n\n");

			sEmailStr.append("The details of the user are as follows: \n");
			sEmailStr.append("User name               : " + new_user.getFirstName() + " " + new_user.getLastName() + "\n");
			sEmailStr.append("User email id           : " + new_user.getEMail() + "\n");
			sEmailStr.append("User company            : " + new_user.getCompany() + "\n");
			sEmailStr.append("User country            : " + countryName + "\n\n");
			sEmailStr.append("Access granted by  : " + ez.gFIRST_NAME.trim() + " " + ez.gLAST_NAME.trim() + "\n\n");
			
			sEmailStr.append("If you have any issues Please contact " + ez.gEMAIL +  "\n\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));

			System.out.println(sEmailStr);
			bSent = ETSUtils.sendEMail(ez.gEMAIL,new_user.getPocEmail(),"",Global.mailHost,sEmailStr.toString(),sEmailSubject,ez.gEMAIL);

		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bSent;

	}


	private static boolean hasPendingRequest(Connection con, String userid, String sProjId) throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		boolean bExists = false;

		try {

			sQuery.append("SELECT REQUEST_ID FROM ETS.ETS_ACCESS_REQ WHERE USER_ID = ? AND PROJECT_NAME = (select project_name from ets.ets_projects where project_id = ? ) AND STATUS = ? with ur");

			SysLog.log(SysLog.DEBUG, "ETSAdminServlet::getPendingRequest()", "QUERY : " + sQuery.toString());

			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1, userid);
			stmt.setString(2, sProjId);
			stmt.setString(3, Defines.ACCESS_PENDING);

			rs = stmt.executeQuery();

			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					bExists = true;
				} else {
					bExists = false;
				}
			} else {
				bExists = false;
			}

		} catch (SQLException e) {
			logger.error("execption===", e);
			throw e;
		} catch (Exception e) {
			logger.error("execption===", e);
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return bExists;
	}


} //end of class
