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

import oem.edge.ets.fe.acmgt.bdlg.*;
import oem.edge.ets.fe.acmgt.helpers.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.Metrics;
import oem.edge.amt.UserObject;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.dao.InvMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamGuiUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.UserInviteStatusModel;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;
import oem.edge.ets.fe.aic.AICProcReqAccessFunctions;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSSyncUser;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

public class ETSAdminServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.122";
	public static final String VERSION = "1.122";
	private static Log logger = EtsLogger.getLogger(ETSAdminServlet.class);

	private String mailhost;
	protected ETSProj Project;
	protected int TopCatId;
	protected String linkid;
	protected Connection conn;
	protected EdgeAccessCntrl es;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected PrintWriter writer;
	protected ETSProperties properties;
	protected boolean isSuperAdmin;
	protected boolean isExecutive;
	private boolean isSubWrkSpc;

	protected ETSDatabaseManager databaseManager;
	protected int CurrentCatId;
	protected String userRole;
	//protected int SubCatId;
	private UnbrandedProperties unBrandedprop;
	private String userId;

	protected static String spacer = "<img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"5\" alt=\"\" />";

	public ETSAdminServlet(ETSParams parameters) {
		this.Project = parameters.getETSProj();
		this.TopCatId = parameters.getTopCat();
		this.linkid = parameters.getLinkId();
		this.conn = parameters.getConnection();
		this.es = parameters.getEdgeAccessCntrl();
		this.request = parameters.getRequest();
		this.response = parameters.getResponse();
		this.writer = parameters.getWriter();
		this.properties = parameters.getProperties();
		this.isSuperAdmin = parameters.isSuperAdmin();
		this.isExecutive = parameters.isExecutive();
		this.databaseManager = new ETSDatabaseManager();
		this.unBrandedprop = WrkSpcTeamUtils.getBrandProps(Project.getProjectType());
		String currentCatIdStr = getParameter(request, "cc");
		if (!currentCatIdStr.equals("")) {
			this.CurrentCatId = (new Integer(currentCatIdStr)).intValue();
		} else {
			this.CurrentCatId = TopCatId;
		}

		this.mailhost = Global.mailHost;
		System.out.println("Using " + this.mailhost);
		if (this.mailhost == null) {
			this.mailhost = "us.ibm.com";
		}
	}

	public void ETSAdminHandler() {
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		String action = getParameter(request, "action");

		try {
			this.userRole = ETSUtils.checkUserRole(es, Project.getProjectId());
		} catch (Exception e) {
			logger.error("execption==", e);
			this.userRole = Defines.WORKSPACE_VISITOR;
		}

		if (!action.equals("")) {
			try {

				if (action.equals("viewaccreqs")) {
					doViewAccessRequests();
				} else if (action.equals("addreqmember")) {
					doAddRequestMember(acf);
				} else if (action.equals("addmember")) {
					if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
						printHeader("Unauthorized action", "", false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}
					doAddMember(acf);
				} else if (action.equals("delmember")) {
					if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
						printHeader("Unauthorized action", "", false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}
					doDelMember(acf);
				} else if (action.equals("managemember")) {
					if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
						printHeader("Unauthorized action", "", false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}
					membersManage(acf);
				} else if (action.equals("primarycontact")) {
					if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
						printHeader("Unauthorized action", "", false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}
					doEditPrimaryContact(acf);
				} else if (action.equals("memberdetails")) {
					doMemberDetails(acf);
				} else if (action.equals("memberdocs")) {
					doMemberDocuments(acf);
				} else if (action.equals("admin_add")) {
					if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
						printHeader("Unauthorized action", "", false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}

					String sbwspc_add_cont1 = AmtCommonUtils.getTrimStr(request.getParameter("sbwspc_add_cont1.x"));


					if (AmtCommonUtils.isResourceDefined(sbwspc_add_cont1)) {

						SubWrkSpcProcBdlg subWrkSpcBdlg = new SubWrkSpcProcBdlg();

						if (AmtCommonUtils.isResourceDefined(subWrkSpcBdlg.validateScrn1FormFields(request))) {

							ETSAddMember memberFunctions = new ETSAddMember(unBrandedprop);
							printHeader("Add member", "", false);
							memberFunctions.processRequest(this.conn, request, response);

						} else {

							printHeader("Add member", "", false);
							ArrayList selectdUserList = subWrkSpcBdlg.getSelectedMemList(Project.getParent_id(), request);
							SubWrkSpcGuiUtils subWrkSpcUtils = new SubWrkSpcGuiUtils();
							ArrayList prevUserInfoList = new ArrayList();
							writer.println(subWrkSpcUtils.printUserDetails(selectdUserList, Project.getProjectId(), TopCatId, linkid, prevUserInfoList, ""));

						}

					} else {

						ETSAddMember memberFunctions = new ETSAddMember(unBrandedprop);
						printHeader("Add member", "", false);
						memberFunctions.processRequest(this.conn, request, response);

					}

				} else if (action.equals("usr_invite")) {
					if (userRole.equals(Defines.WORKSPACE_VISITOR)) {
						printHeader("Unauthorized action", "", false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}
					ETSAddMember memberFunctions = new ETSAddMember(unBrandedprop);
					printHeader("Invite new user", "", false);
					memberFunctions.Invite(this.conn, request, response);
				} else if (action.equals("addmemph")) {
					if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
						printHeader("Unauthorized action", "", false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}
					doAddPhoto(acf, getParameter(request,"msg"));
				} else if (action.equals("editmeminfo")) {
					if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
						printHeader("Unauthorized action", "", false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}
					doEditUserInfo(acf, getParameter(request,"msg"));
				} else if (action.equals("subwrkspc_add")) {

					String sbwspc_add_submit = AmtCommonUtils.getTrimStr(request.getParameter("sbwspc_add_submit.x"));

					String sbwspc_add_back = AmtCommonUtils.getTrimStr(request.getParameter("sbwspc_add_back.x"));

						if (AmtCommonUtils.isResourceDefined(sbwspc_add_back)) {
								
								ETSAddMember memberFunctions = new ETSAddMember(unBrandedprop);
								printHeader("Add member", "", false);
								memberFunctions.processRequest(this.conn, request, response);

						}


					if (AmtCommonUtils.isResourceDefined(sbwspc_add_submit)) {

						SubWrkSpcProcBdlg subWrkSpcBdlg = new SubWrkSpcProcBdlg();
						SubWrkSpcGuiUtils subWrkSpcUtils = new SubWrkSpcGuiUtils();

						ArrayList selInfoList = subWrkSpcBdlg.validateScrn2FormFields(request);

						if (AmtCommonUtils.isResourceDefined((String) selInfoList.get(0))) {

							printHeader("Add member", "", false);

							ArrayList prevUserInfoList = (ArrayList) selInfoList.get(1);
							ArrayList selectdDetsUserList = subWrkSpcBdlg.getMemDetsFromSelectdUserList(Project.getParent_id(), prevUserInfoList);

							writer.println(subWrkSpcUtils.printUserDetails(selectdDetsUserList, Project.getProjectId(), TopCatId, linkid, prevUserInfoList, (String) selInfoList.get(0)));

						} else {

							boolean ret = subWrkSpcBdlg.submitSubWrkSpcUsers(request, es.gIR_USERN);
							String userMsg = "";

							if (ret) {

								userMsg = "The users have been addedd successfully to the workspace.";
							} else {

								userMsg = "There is some problem while processing the request. Please contact IBM Help Desk for more help.";

							}

							printHeader("Add member", "", false);

							writer.println(subWrkSpcUtils.printConfirmPage(Project.getProjectId(), TopCatId, linkid, userMsg, ret));

						}

					}

				}else if(action.equals("delInvUser")){
					
					try {
						
						int invUsrDeltd = 0;
						String invUsrId = "";
						
						ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
						if(procFuncs.isWM(this.es, Project.getProjectId()) || procFuncs.isWO(this.es, 

                            Project.getProjectId()) || procFuncs.isSuperAdmin(this.es, Project.getProjectId())) {
							invUsrId = getParameter(request,"invId");
							
							if(!StringUtil.isNullorEmpty(invUsrId)){
								InvMembrToWrkSpcDAO invWrkSpcDao = new InvMembrToWrkSpcDAO();
								invUsrDeltd = invWrkSpcDao.deleteReqFromInviteStatus(this.conn,invUsrId,Project.getProjectId());
							}		
						}
						
						printHeader("Team", "", true);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");

						membersList(acf);
						printAdminActionButtons();
						
					} catch (SQLException se) {
						logger.error("sql error in admin servlet list = ", se);
						se.printStackTrace();
					} catch (IOException ioe) {
						logger.error("IO error in admin servlet list = ", ioe);
						ioe.printStackTrace();
					} catch (Exception e) {
						logger.error("ex error in admin servlet list = ", e);
						e.printStackTrace();
					}
				 }


				/*else if (action.equals("reqsws")){
					if(!userRole.equals(Defines.WORKSPACE_OWNER)){
						printHeader("Unauthorized action","",false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}
					doRequestSubWorkspace(acf,"","","","");
				}
				else if (action.equals("reqsws2")){
					if(!userRole.equals(Defines.WORKSPACE_OWNER)){
						printHeader("Unauthorized action","",false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}
					String swsname = getParameter(request,"swsname");
					String ownid = getParameter(request,"uid");
					String desc = getParameter(request,"desc");

					if (swsname.equals("") || swsname.length()>128){
						doRequestSubWorkspace(acf,swsname,ownid,desc,"Subworkspace name must be 1-128 characters long");
					}
					else if (ownid.equals("")){
						doRequestSubWorkspace(acf,swsname,ownid,desc,"Subworkspace owner must be selected");
					}
					else{
						doRequestSubWorkspace2(acf,swsname,ownid,desc);
					}
				}*/
				else {
					writer.println("error action not valid");
				}

			} catch (Exception e) {
				logger.error("ex error in admin servlet action = ", e);
				e.printStackTrace();
			}
		} else { //if (action.equals("") || listflag) { //list
			try {
				printHeader("Team", "", true);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				membersList(acf);
				printAdminActionButtons();
			} catch (SQLException se) {
				logger.error("sql error in admin servlet list = ", se);
				se.printStackTrace();
			} catch (IOException ioe) {
				logger.error("IO error in admin servlet list = ", ioe);
				ioe.printStackTrace();
			} catch (Exception e) {
				logger.error("ex error in admin servlet list = ", e);
				e.printStackTrace();
			}
		}
	}

	private void printHeader(String msg, String subheader, boolean printBM) {
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

			logger.error("execption==", e);

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

	private void printAdminActionButtons() {
		StringBuffer buf = new StringBuffer();
		boolean allowed = false;
		boolean owner = false;
		boolean bTeamroomWrkspc = false;

		try {
			allowed = (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.ADMIN, conn) || ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.MANAGE_USERS, conn) || isSuperAdmin);
			owner = (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.OWNER, conn));
			if (Project.getIsPrivate().equals(Defines.AIC_IS_PRIVATE_TEAMROOM) || Project.getIsPrivate().equals(Defines.AIC_IS_RESTRICTED_TEAMROOM)) {
				bTeamroomWrkspc = true;
			}
		} catch (SQLException se) {
			logger.error("sql error in admin servlet action buttons = ", se);
			se.printStackTrace();
		} catch (Exception e) {
			logger.error("ex error in admin servlet action buttons = ", e);
			e.printStackTrace();
		}

		if (!(userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR) || bTeamroomWrkspc)) {

			buf.append("<table  cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			buf.append("<tr>");

			//view requests
			if (owner && hasRequests(Project.getProjectType())) { //admin or privileged
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\"><a href=\"" + unBrandedprop.getLandingPageURL() + "?linkid=" + linkid + "#pending_list\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"view requests\" /></a>&nbsp;</td>");
				buf.append("<td valign=\"top\" align=\"left\"><a href=\"" + unBrandedprop.getLandingPageURL() + "?linkid=" + linkid + "#pending_list\" class=\"fbox\">View access requests</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
			} /*  NOT YET ALLOWED
			else{  //only request add
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\" class=\"small\"><a href=\"ETSProjectsServlet.wss?action=addmember&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"request to add member\" /></a>&nbsp;</td>");
				buf.append("<td valign=\"top\" align=\"left\" class=\"small\"><a href=\"ETSProjectsServlet.wss?action=addmember&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Request to add member</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
			}  */

			//add member   TURNED OFF CRUDELY,   (perhaps Admin should be allowed to add directly)
			if (allowed) { //admin or privileged
								
				boolean isSubWS = true;
				String parentId = Project.getParent_id();
				if(parentId.equals("0")) isSubWS = false; 
				
 			 if(isSubWS){
 			 	
 			 	buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=admin_add&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"add member\" /></a>&nbsp;</td>");
				buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=admin_add&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">Add/Invite member</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
 			 	
 			 }else{
				if (Project.getProjectType().equals(Defines.ETS_WORKSPACE_TYPE)) {

					buf.append("<td>");

					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\"><a href=\"displayAddMembrInput.wss?action=admin_add&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"add member\" /></a>&nbsp;</td>");
					buf.append("<td valign=\"top\" align=\"left\"><a href=\"displayAddMembrInput.wss?action=admin_add&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">Add/Invite member</a></td>");
					buf.append("</tr></table>");

					buf.append("</td>");

				} else {

					buf.append("<td>");

					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\"><a href=\"displayAddMembrInput.wss?action=admin_add&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"add member\" /></a>&nbsp;</td>");
					buf.append("<td valign=\"top\" align=\"left\"><a href=\"displayAddMembrInput.wss?action=admin_add&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">Add member</a></td>");
					buf.append("</tr></table>");

					buf.append("</td>");

				}
 			 }	

			} else { //only request add
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=addreqmember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"request to add member\" /></a>&nbsp;</td>");
				buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=addreqmember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">Request to add member</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
				buf.append("<td>");
				//buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				//buf.append("<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=usr_invite&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&aug1=aug1&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"invite member\" /></a>&nbsp;</td>");
				//buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=usr_invite&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&aug1=aug1&linkid=" + linkid + "\" class=\"fbox\">Invite member</a></td>");
				//buf.append("</tr></table>");
				buf.append("</td>");

			}

			//del member
			if (allowed) { //admin or privileged
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=delmember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"remove member\" /></a>&nbsp;</td>");
				buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=delmember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">Remove member</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
			} else { //only request del
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=delmember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"request to remove member\" /></a>&nbsp;</td>");
				buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=delmember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">Request to remove member</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
			}

			//manage member
			if (allowed) { //admin or privileged
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"manage members\" /></a>&nbsp;</td>");
				buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">Manage members</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
			}

			//primary contact
			if (allowed) { //admin or privileged
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=primarycontact&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"edit primary contact\" /></a>&nbsp;</td>");
				buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=primarycontact&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">Change primary contact</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
			}

			//request sub workspace
			/*if (owner){  //owner
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=reqsws&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"edit primary contact\" /></a>&nbsp;</td>");
				buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=reqsws&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\" class=\"fbox\">Request subworkspace</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
			}*/

			//download Member list
			buf.append("<td>");
			buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
			buf.append("<td valign=\"top\" align=\"right\"><a href=\"" + "downLoadMembersCsv.wss" + "?linkid=" + linkid + "&proj=" + Project.getProjectId() + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Export members list\" /></a>&nbsp;</td>");
			buf.append("<td valign=\"top\" align=\"left\"><a href=\"" + "downLoadMembersCsv.wss" + "?linkid=" + linkid + "&proj=" + Project.getProjectId() + "\" class=\"fbox\">Export members list</a></td>");
			buf.append("</tr></table>");
			buf.append("</td>");

			// View/create Groups
			buf.append("<td>");
			buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
			buf.append("<td valign=\"top\" align=\"right\"><a href=\"displayGroupList.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"view/create groups\" /></a>&nbsp;</td>");
			buf.append("<td valign=\"top\" align=\"left\"><a href=\"displayGroupList.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">View/Create groups</a></td>");
			buf.append("</tr></table>");
			buf.append("</td>");
			
			buf.append("</tr></table>");
			writer.println(buf.toString());

		} else {
			// Visitor/Executives are also allowed to see the group members
			buf.append("<table  cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			buf.append("<tr>");

			//download Member list
			buf.append("<td>");
			buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
			buf.append("<td valign=\"top\" align=\"right\"><a href=\"" + "downLoadMembersCsv.wss" + "?linkid=" + linkid + "&proj=" + Project.getProjectId() + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Export members list\" /></a>&nbsp;</td>");
			buf.append("<td valign=\"top\" align=\"left\"><a href=\"" + "downLoadMembersCsv.wss" + "?linkid=" + linkid + "&proj=" + Project.getProjectId() + "\" class=\"fbox\">Export members list</a></td>");
			buf.append("</tr></table>");
			buf.append("</td>");

			// View/create Groups
			buf.append("<td>");
			buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
			buf.append("<td valign=\"top\" align=\"right\"><a href=\"displayGroupList.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"view groups\" /></a>&nbsp;</td>");
			buf.append("<td valign=\"top\" align=\"left\"><a href=\"displayGroupList.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\">View groups</a></td>");
			buf.append("</tr></table>");
			buf.append("</td>");

			buf.append("</tr></table>");
			writer.println(buf.toString());

		}

	}

	boolean hasRequests(String wrkSpcType) {
		ETSAccessRequestHome arh = new ETSAccessRequestHome(conn);
		if (arh.countPendingByOwnerEMail(es.gEMAIL, wrkSpcType) < 1)
			return false;
		return true;

	}

	public void membersList(AccessCntrlFuncs acf) throws SQLException, IOException, Exception {
		StringBuffer buf = new StringBuffer();
		StringBuffer buf2 = new StringBuffer();
		Vector members = new Vector();
		boolean gray_flag = true;
		boolean wsm_flag = false;
		boolean wso_flag = false;
		/*int width_name = 123;
		int width_id = 105;
		int width_job = 125;
		int width_company = 90;*/

		int width_name = 200;
		int width_id = 175;
		int width_job = 125;
		int width_company = 100;

		String sortby = request.getParameter("sort_by");
		String ad = request.getParameter("sort");

		String sortby_inv = AmtCommonUtils.getTrimStr(request.getParameter("sort_by_inv"));
		String ad_inv = AmtCommonUtils.getTrimStr(request.getParameter("sort_inv"));

		if (sortby == null) {
			sortby = Defines.SORT_BY_USERNAME_STR;
		}
		if (ad == null) {
			ad = Defines.SORT_ASC_STR;
		}

		if (!AmtCommonUtils.isResourceDefined(sortby_inv)) {

			sortby_inv = Defines.SORT_BY_INVITEID_STR;
		}

		if (!AmtCommonUtils.isResourceDefined(ad_inv)) {

			ad_inv = Defines.SORT_ASC_STR;
		}

		try {
			members = ETSDatabaseManager.getAllProjMembers(Project.getProjectId(), sortby, ad, true, false, conn);
		} catch (Exception e) {
			logger.error("ETSAdminServlet:memebersList() error =", e);
			e.printStackTrace();
		}

		String indexReplaceStr = "=!@%%@@@@@@@@%%@!=";

		if (members == null) {
			buf.append("There are no project members at this time - null");
		} else {
			if (members.size() <= 0) {
				buf.append("There are no project members at this time");
			} else {

				if (sortby.equals(Defines.SORT_BY_COMP_STR)) {
					byte sortOrder = ETSComparator.getSortOrder(sortby);
					byte sortAD = ETSComparator.getSortBy(ad);
					Collections.sort(members, new ETSComparator(sortOrder, sortAD));
				}

				String po = "Project role";
				if (!Project.getProjectOrProposal().equals("P")) {
					po = "Proposal role";
				}

				buf.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\"><tr>");
				buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

				//buf.append("<tr><td colspan=\"4\" class=\"small\">Click on the column heading to sort</td></tr>\n");
				buf.append("<tr><td colspan=\"2\" class=\"small\">Click on the column heading to sort</td>");
				buf.append("<td colspan=\"2\" align=\"right\">Total members: " + indexReplaceStr + "</td></tr>\n");
				buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");

				buf.append("<tr><th id=\"list_name\" align=\"left\" valign=\"bottom\" height=\"16\">");
				//sort by name
				if (sortby.equals(Defines.SORT_BY_USERNAME_STR)) {
					if (ad.equals(Defines.SORT_ASC_STR)) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_USERNAME_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
						buf.append("Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
						buf.append("</table></th>");
					} else {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_USERNAME_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
						buf.append("Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
						buf.append("</table></th>");
					}
				} else {
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_USERNAME_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
					buf.append("Name</a></th>");
				}

				buf.append("<th id=\"list_id\" align=\"left\" valign=\"bottom\" height=\"16\">");
				if (sortby.equals(Defines.SORT_BY_USERID_STR)) {
					if (ad.equals(Defines.SORT_ASC_STR)) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_USERID_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
						buf.append("Userid</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
						buf.append("</table></th>");
					} else {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_USERID_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
						buf.append("Userid</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
						buf.append("</table></th>");
					}
				} else {
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_USERID_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
					buf.append("Userid</a></th>");
				}

				buf.append("<th id=\"list_job\" align=\"left\" valign=\"bottom\" height=\"16\">");
				if (sortby.equals(Defines.SORT_BY_PROJROLE_STR)) {
					if (ad.equals(Defines.SORT_ASC_STR)) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_PROJROLE_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
						buf.append(po + "</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
						buf.append("</table></th>");
					} else {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_PROJROLE_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
						buf.append(po + "</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
						buf.append("</table></th>");
					}
				} else {
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_PROJROLE_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
					buf.append(po + "</a></th>");
				}

				buf.append("<th id=\"list_comp\" align=\"left\" valign=\"bottom\" height=\"16\">");
				if (sortby.equals(Defines.SORT_BY_COMP_STR)) {
					if (ad.equals(Defines.SORT_ASC_STR)) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_COMP_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
						buf.append("Company</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
						buf.append("</table></th>");
					} else {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_COMP_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
						buf.append("Company</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
						buf.append("</table></th>");
					}
				} else {
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_COMP_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
					buf.append("Company</a></th>");
				}
				buf.append("</tr>");

				int activeMemberCnt = 0;
				for (int i = 0; i < members.size(); i++) {
					//get amt information
					ETSUser memb = (ETSUser) members.elementAt(i);
					if (memb.getActiveFlag().equals("A")) {

						try {
							//UserObject uo = AccessCntrlFuncs.getUserObject(conn, memb.getUserId(), true, false);
							if (gray_flag) {
								buf.append("<tr style=\"background-color:#eeeeee\">");
								gray_flag = false;
							} else {
								buf.append("<tr>");
								gray_flag = true;
							}

							String ast = "";

							//if (memb.getPrimaryContact()==Defines.YES){
							try {
								if (ETSDatabaseManager.hasProjectPriv(memb.getUserId(), Project.getProjectId(), Defines.OWNER, conn)) {
									wso_flag = true;
									ast = "<span class=\"ast\"><b>**</b></span>";
								} else if (ETSDatabaseManager.hasProjectPriv(memb.getUserId(), Project.getProjectId(), Defines.ADMIN, conn)) {
									wsm_flag = true;
									ast = "<span class=\"ast\"><b>*</b></span>";
								}
							} catch (Exception e) {
								logger.error("ETSAdminServlet:memebersList() hasProjectPriv error =", e);
								e.printStackTrace();
							}

							//String username = uo.gFIRST_NAME + "&nbsp;" + uo.gLAST_NAME;
							activeMemberCnt++;
							String username = memb.getUserName();

							buf.append("<td headers=\"list_name\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_name + "\">");
							buf.append("<a class=\"fbox\" href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + memb.getUserId() + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><span class=\"small\">" + username + "</span></a>" + ast);
							buf.append("</td>");

							buf.append("<td headers=\"list_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_id + "\">");
							buf.append(memb.getUserId());
							buf.append("</td>");

							buf.append("<td headers=\"list_job\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_job + "\">");
							buf.append(memb.getUserJob());
							buf.append("</td>");

							buf.append("<td headers=\"list_comp\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_company + "\">");
							buf.append(memb.getCompany());
							buf.append("</td>");
							buf.append("</tr>");
							/**/

						} catch (Exception e) {
							logger.error("exception==", e);
							e.printStackTrace();

						}
					}
				}

				int indexReplace = buf.indexOf(indexReplaceStr);
				buf.replace(indexReplace, indexReplace + (indexReplaceStr.length()), String.valueOf(activeMemberCnt));

				if (wso_flag || wsm_flag) {
					buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
					buf.append("<tr><td colspan=\"4\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
					if (wso_flag) {
						buf.append("<tr><td  width=\"1%\" height=\"21\" class=\"small\" align=\"right\" valign=\"bottom\"><span class=\"ast\"><b>**</b></span></td>");
						buf.append("<td  width=\"99%\" height=\"21\" class=\"small\" align=\"left\" valign=\"bottom\">Denotes workspace owner</td></tr>");
					}

					if (wsm_flag) {
						buf.append("<tr><td width=\"1%\" height=\"21\" class=\"small\" align=\"right\" valign=\"bottom\"><span class=\"ast\"><b>*</b></span></td>");
						buf.append("<td  width=\"99%\" height=\"21\" class=\"small\" align=\"left\" valign=\"bottom\">Denotes workspace manager</td></tr>");
					}
					buf.append("</table></td></tr>");
				}

				if (ETSDatabaseManager.getProjMembers(Project.getProjectId(), "P").size() > 0) {

					buf.append("<tr><td colspan=\"4\" height=\"21\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"600\" height=\"1\" alt=\"\" /></td></tr>");
					buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
					buf.append("<tr><td colspan=\"4\"><span class=\"small\"><b>Team members with pending requests</b></span></td></tr>");
					buf.append("<tr><td colspan=\"4\">These requests for access have been approved by the workspace owner but require further approvals.</td></tr>");
					buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

					gray_flag = true;
					for (int i = 0; i < members.size(); i++) {
						//get amt information
						ETSUser memb = (ETSUser) members.elementAt(i);
						if (memb.getActiveFlag().equals("P")) {

							try {
								if (gray_flag) {
									buf.append("<tr style=\"background-color:#eeeeee\">");
									gray_flag = false;
								} else {
									buf.append("<tr>");
									gray_flag = true;
								}

								String ast = "";

								try {
									if (ETSDatabaseManager.hasProjectPriv(memb.getUserId(), Project.getProjectId(), Defines.OWNER, conn)) {
										wso_flag = true;
										ast = "<span class=\"ast\"><b>**</b></span>";
									} else if (ETSDatabaseManager.hasProjectPriv(memb.getUserId(), Project.getProjectId(), Defines.ADMIN, conn)) {
										wsm_flag = true;
										ast = "<span class=\"ast\"><b>*</b></span>";
									}
								} catch (Exception e) {
									logger.error("ETSAdminServlet:memebersList() hasProjectPriv error =", e);
									e.printStackTrace();
								}

								//String username = uo.gFIRST_NAME + "&nbsp;" + uo.gLAST_NAME;
								String username = memb.getUserName();

								buf.append("<td headers=\"list_name\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_name + "\">");
								buf.append("<a class=\"fbox\" href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + memb.getUserId() + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><span class=\"small\">" + username + "</span></a>" + ast);
								buf.append("</td>");

								buf.append("<td headers=\"list_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_id + "\">");
								buf.append(memb.getUserId());
								buf.append("</td>");

								buf.append("<td headers=\"list_job\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_job + "\">");
								buf.append(memb.getUserJob());
								buf.append("</td>");

								buf.append("<td headers=\"list_comp\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_company + "\">");
								buf.append(memb.getCompany());
								buf.append("</td>");
								buf.append("</tr>");
								/**/

							} catch (Exception e) {
								logger.error("exception==", e);
								e.printStackTrace();

							}
						}
					}
				}
				if (ETSDatabaseManager.getProjMembers(Project.getProjectId(), "R").size() > 0) {

					ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
					if (procFuncs.isWO(this.es, Project.getProjectId()) || procFuncs.isSuperAdmin(this.es, Project.getProjectId())) {

						buf.append("<tr><td colspan=\"4\" height=\"21\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"600\" height=\"1\" alt=\"\" /></td></tr>");
						buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
						buf.append("<tr><td colspan=\"4\"><span class=\"small\"><b>Team members without access</b></span></td></tr>");
						buf.append(
							"<tr><td colspan=\"4\">These members require action by the workspace owner to reinstate or remove.<br /><span class=\"small\">[<a class=\"fbox\" name=\"BLBMISSING_HELP\" href=\"#BLBMISSING_HELP\" onclick=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=MISSING_HELP','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450'); return false\"  onkeypress=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=MISSING_HELP','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450')\">more information on action required</a>]</span></td></tr>");
						buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

						gray_flag = true;
						for (int i = 0; i < members.size(); i++) {
							//get amt information
							ETSUser memb = (ETSUser) members.elementAt(i);
							if (memb.getActiveFlag().equals("R")) {

								try {
									if (gray_flag) {
										buf.append("<tr style=\"background-color:#eeeeee\">");
										gray_flag = false;
									} else {
										buf.append("<tr>");
										gray_flag = true;
									}

									String ast = "";

									try {
										if (ETSDatabaseManager.hasProjectPriv(memb.getUserId(), Project.getProjectId(), Defines.OWNER, conn)) {
											wso_flag = true;
											ast = "<span class=\"ast\"><b>**</b></span>";
										} else if (ETSDatabaseManager.hasProjectPriv(memb.getUserId(), Project.getProjectId(), Defines.ADMIN, conn)) {
											wsm_flag = true;
											ast = "<span class=\"ast\"><b>*</b></span>";
										}
									} catch (Exception e) {
										logger.error("ETSAdminServlet:memebersList() hasProjectPriv error =", e);
										e.printStackTrace();
									}

									//String username = uo.gFIRST_NAME + "&nbsp;" + uo.gLAST_NAME;
									String username = memb.getUserName();

									buf.append("<td headers=\"list_name\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_name + "\">");
									buf.append("<a class=\"fbox\" href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + memb.getUserId() + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><span class=\"small\">" + username + "</span></a>" + ast);
									buf.append("</td>");

									buf.append("<td headers=\"list_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_id + "\">");
									buf.append(memb.getUserId());
									buf.append("</td>");

									buf.append("<td headers=\"list_job\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_job + "\">");
									buf.append(memb.getUserJob());
									buf.append("</td>");

									buf.append("<td headers=\"list_comp\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" width=\"" + width_company + "\">");
									buf.append(memb.getCompany());
									buf.append("</td>");
									buf.append("</tr>");
									/**/

								} catch (Exception e) {

									logger.error("exception===", e);
									e.printStackTrace();

								}
							}
						}

					}
				} //end of R requests

				//buf.append("<tr><td colspan=\"4\" height=\"21\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"600\" height=\"1\" alt=\"\" /></td></tr>");
				buf.append("</table>");

				//start of invitations report
				InvMembrToWrkSpcDAO invMmbrDao = new InvMembrToWrkSpcDAO();
				ArrayList invMmbrList = invMmbrDao.getInvitationsRepList(sortby_inv, ad_inv, Project.getProjectId());
				int invsize = 0;

				if (invMmbrList != null && !invMmbrList.isEmpty()) {

					invsize = invMmbrList.size();
				}

				if (invsize > 0) {

					String inviteId = "";
					String inviteStatus = "";
					String roleName = "";
					String requestorId = "";
					String requestorName = "";
					String userCompany = "";
					String userCountryName = "";

					ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
					if (procFuncs.isWM(this.es, Project.getProjectId()) || procFuncs.isWO(this.es, Project.getProjectId()) || procFuncs.isSuperAdmin(this.es, Project.getProjectId())) {

						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"600\">");
						buf.append("<tr><td colspan=\"4\" height=\"21\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"600\" height=\"1\" alt=\"\" /></td></tr>");
						buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
						buf.append("<tr><td colspan=\"4\"><span class=\"small\"><b>Outstanding invitations</b></span></td></tr>");
						buf.append("<tr><td colspan=\"4\">Invitations have been sent to the following members, who have not registered/logged in yet.<br /></td></tr>");
						buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
						buf.append("</table>");

						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"600\">");
						buf.append("<tr><th colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></th></tr>");

						//start 541
						buf.append("<tr><th id=\"inv_id\" align=\"left\" valign=\"bottom\" height=\"16\">");
						//sort by name
						if (sortby_inv.equals(Defines.SORT_BY_INVITEID_STR)) {
							if (ad_inv.equals(Defines.SORT_ASC_STR)) {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INVITEID_STR + "&sort_inv=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
								buf.append("Invite ID</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
								buf.append("</table></th>");
							} else {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INVITEID_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
								buf.append("Invite ID</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
								buf.append("</table></th>");
							}
						} else {
							buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INVITEID_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
							buf.append("Invite ID</a></th>");
						}

						buf.append("<th id=\"priv_name\" align=\"left\" valign=\"bottom\" height=\"16\">");
						if (sortby_inv.equals(Defines.SORT_BY_INV_PRIV_STR)) {
							if (ad_inv.equals(Defines.SORT_ASC_STR)) {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_PRIV_STR + "&sort_inv=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
								buf.append("Privilege</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
								buf.append("</table></th>");
							} else {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_PRIV_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
								buf.append("Privilege</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
								buf.append("</table></th>");
							}
						} else {
							buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_PRIV_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
							buf.append("Privilege</a></th>");
						}

						buf.append("<th id=\"req_id\" align=\"left\" valign=\"bottom\" height=\"16\">");
						if (sortby_inv.equals(Defines.SORT_BY_INV_REQST_STR)) {
							if (ad_inv.equals(Defines.SORT_ASC_STR)) {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_REQST_STR + "&sort_inv=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
								buf.append("Requestor</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
								buf.append("</table></th>");
							} else {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_REQST_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
								buf.append("Requestor</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
								buf.append("</table></th>");
							}
						} else {
							buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_REQST_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
							buf.append("Requestor</a></th>");
						}

						buf.append("<th id=\"inv_comp\" align=\"left\" valign=\"bottom\" height=\"16\">");
						if (sortby_inv.equals(Defines.SORT_BY_INV_COMP_STR)) {
							if (ad_inv.equals(Defines.SORT_ASC_STR)) {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_COMP_STR + "&sort_inv=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
								buf.append("Company</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
								buf.append("</table></th>");
							} else {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_COMP_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
								buf.append("Company</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
								buf.append("</table></th>");
							}
						} else {
							buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_COMP_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
							buf.append("Company</a></th>");
						}

						buf.append("<th id=\"inv_country\" align=\"left\" valign=\"bottom\" height=\"16\">");
						if (sortby_inv.equals(Defines.SORT_BY_INV_COUNTRY_STR)) {
							if (ad_inv.equals(Defines.SORT_ASC_STR)) {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_COUNTRY_STR + "&sort_inv=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
								buf.append("Country</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
								buf.append("</table></th>");
							} else {
								buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_COUNTRY_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
								buf.append("Country</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
								buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
								buf.append("</table></th>");
							}
						} else {
							buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by_inv=" + Defines.SORT_BY_INV_COUNTRY_STR + "&sort_inv=" + Defines.SORT_ASC_STR + "\">");
							buf.append("Country</a></th>");
						}
						buf.append("</tr>");

						//end 541

						try {

							gray_flag = true;
							for (int i = 0; i < invsize; i++) {
								//get invite information
								UserInviteStatusModel invStatModel = (UserInviteStatusModel) invMmbrList.get(i);
								inviteId = invStatModel.getUserId();
								inviteStatus = invStatModel.getInviteStatus();
								roleName = invStatModel.getRoleName();
								requestorId = invStatModel.getRequestorId();
								requestorName = invStatModel.getRequestorName();
								userCompany = invStatModel.getUserCompany();
								userCountryName = invStatModel.getUserCountryName();

								if (gray_flag) {
									buf.append("<tr style=\"background-color:#eeeeee\">");
									gray_flag = false;
								} else {
									buf.append("<tr>");
									gray_flag = true;
								}

								buf.append("<td headers=\"inv_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
								buf.append(inviteId);
								buf.append("</td>");

								//buf.append("<td headers=\"inv_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >&nbsp;</td>");

								buf.append("<td headers=\"priv_name\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
								buf.append(roleName);
								buf.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>");

								//buf.append("<td headers=\"inv_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >&nbsp;</td>");

								buf.append("<td headers=\"req_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
								buf.append("<a class=\"fbox\" href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + requestorId + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><span class=\"small\">" + requestorName + "</span></a>");
								buf.append("&nbsp;&nbsp;</td>");

								//buf.append("<td headers=\"inv_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >&nbsp;</td>");

								buf.append("<td headers=\"inv_comp\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
								buf.append(userCompany);
								buf.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>");

								//buf.append("<td headers=\"inv_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >&nbsp;</td>");

								buf.append("<td headers=\"inv_country\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
								buf.append(userCountryName);
								buf.append("&nbsp;&nbsp;</td>");
								
								if (procFuncs.isWO(this.es, Project.getProjectId())) {
									buf.append("<td headers=\"inv_delete\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
									buf.append("<a href=\"ETSProjectsServlet.wss?action=delInvUser&invId="+ inviteId +"&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\" onClick=\"javascript:return confirm('Are you sure, you want to delete this invitation for the user : " + inviteId + " ?')\">Delete</a>");
									buf.append("</td></tr>");
								}else if (procFuncs.isWM(this.es, Project.getProjectId()) ||  procFuncs.isSuperAdmin(this.es, Project.getProjectId())) {
									if(this.es.gIR_USERN.equals(requestorId)){
										buf.append("<td headers=\"inv_country\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
										buf.append("<a href=\"ETSProjectsServlet.wss?action=delInvUser&invId="+ inviteId +"&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\" class=\"fbox\" onClick=\"javascript:return confirm('Are you sure, you want to delete this invitation for the user : " + inviteId + " ?')\">Delete</a>");
										buf.append("</td></tr>");
									}
								}

								
								buf.append("</tr>");

							}

						} catch (Exception e) {

							logger.error("exception while printing invitation list==", e);
							e.printStackTrace();

						}

					}

					buf.append("<tr><td colspan=\"9\" height=\"21\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"600\" height=\"1\" alt=\"\" /></td></tr>");
					buf.append("</table>");

				} //end of size

				//end of invitations report

				//buf.append("<tr><td colspan=\"4\" height=\"21\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"600\" height=\"1\" alt=\"\" /></td></tr>");
				//buf.append("</table>");
			}
		}
		writer.println(buf.toString());
	}

	/**
	 * Method doViewAccessRequests.
	 * @throws Exception
	 */
	private void doViewAccessRequests() throws Exception {

		StringBuffer added = new StringBuffer();
		ETSAccessRequestHome arh = null;
		ETSUserDetails u = new ETSUserDetails();

		StringBuffer sErrMsg = new StringBuffer("");
		StringBuffer sOkMsg = new StringBuffer("");

		try {

			arh = new ETSAccessRequestHome(this.conn);

			for (int i = 0; true; i++) { // get all submitted

				String index = Integer.toString(i).trim();

				String sCmpRsn = request.getParameter("cmpRsn");
				if (sCmpRsn == null || sCmpRsn.trim().equals("")) {
					sCmpRsn = "";
				} else {
					sCmpRsn = sCmpRsn.trim();
				}
				
				String request_id = getParameter(request, "requestid_" + index).trim();
				if (request_id == null || request_id.length() < 1)
					break;

				int id = Integer.parseInt(request_id);

				ETSAccessRequest ar = arh.getAccessRequestByRequestId(id);

				String webid = ar.getUserId();

				SysLog.log(SysLog.DEBUG, this, "Processing Access request for webid=" + webid);

				u.setWebId(webid);
				u.extractUserDetails(conn);

				String reply = getParameter(request, "reply_" + index).trim();
				if (reply == null) {
					reply = "";
				}

				String action = getParameter(request, "action_" + index).trim();

				String sMgrEmail = getParameter(request, "contactemail_" + index).trim();

				if (action == null || action.trim().equals("")) {

					SysLog.log(SysLog.ERR, this, " action_" + index + " is null. Leaving the request on hold.");

				} else if (action.equalsIgnoreCase("Accept")) {

					// check if all the details has been set correctly.
					// NOTE, if address checked as Not Valid, leave the request on hold.

					String addrchk = getParameter(request, "addrchk_" + index).trim();

					if (addrchk == null || addrchk.trim().equals("")) {

						// since address has not set, leave the request on hold.

						SysLog.log(SysLog.ERR, this, " addrchk_" + index + " is null. Leaving the request on hold.");

						sErrMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> could not be processed because the address validation option was not selected.</li>");

					} else if (addrchk.equalsIgnoreCase("NotValid")) {

						// since address has not valid, leave the request on hold and send an email to user.

						SysLog.log(SysLog.DEBUG, this, "Address is NOT Valid. Leaving the request on hold");

						boolean sentOK = ETSBoardingUtils.sendAddrChkEMail(u, ar, Project.getName(), es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim(), reply, sMgrEmail);

						sErrMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> was not approved yet because the address is not valid. Automatic e-mail has been sent to user to update their address. You can process the request after the address is updated.</li>");

					} else if (addrchk.equalsIgnoreCase("Valid")) {

						// address is valid. proceed with updating the user...
						// check if this user is in this project already. If yes, ignore this user.

						SysLog.log(SysLog.DEBUG, this, "Address is Valid");

						if (!ETSDatabaseManager.isUserInProject(webid, Project.getProjectId(), conn)) {

							int roleid = -1;

							String role = getParameter(request, "roleid_" + index).trim();

							if (role != null || role.length() > 0) {

								roleid = Integer.parseInt(role);

								String company = getParameter(request, "company_" + index).trim();
								if (company == null || company.trim().equals("")) {
									company = "";
								} else {
									company = company.trim();
								}

								//541 new//
								String reqstdEntitlement = WrkSpcTeamUtils.getWrkSpcReqEntitlement(Project);
								logger.debug("reqstdEntitlement entitlement in ADD MEMBER=== " + reqstdEntitlement);

								String reqstdProject = WrkSpcTeamUtils.getWrkSpcReqProject(Project);
								logger.debug("reqstdProject in ADD MEMBER ==" + reqstdProject);

								String userid = u.getEdgeId();
								boolean bHasEntitlement = userHasEntitlement(conn, userid, reqstdEntitlement);
								boolean bRequested = false;
								boolean bRequestedEnt = false;

								if (!bHasEntitlement) {

									// user does not have entitlement. So request the project for this user.

									ETSUserAccessRequest uar = new ETSUserAccessRequest();
									uar.setTmIrId(webid);
									uar.setPmIrId(es.gIR_USERN);
									uar.setDecafEntitlement(reqstdProject);
									uar.setIsAProject(WrkSpcTeamUtils.isReqAccessProject(Project.getProjectType()));
									uar.setUserCompany(company);
									ETSStatus status = uar.request(conn);

									if (status.getErrCode() == 0) {
										bRequested = true;
										bRequestedEnt = true;
									} else {
										bRequested = false;
									}
								} else {
									bRequested = true;
								}

								if (bRequested) {

									ETSUser user = new ETSUser();
									user.setUserId(webid);
									user.setProjectId(Project.getProjectId());
									user.setUserJob("");
									user.setPrimaryContact(Defines.NO);
									user.setLastUserId(es.gIR_USERN);
									user.setRoleId(roleid);
									String[] res = ETSDatabaseManager.addProjectMember(user, conn);
									String success = res[0];

									if (success.equals("0")) {
										
										if(! StringUtil.isNullorEmpty(sCmpRsn)){
						                	int updt = 0;
						                	String rsn = sCmpRsn.trim();
						                	updt = AddMembrToWrkSpcDAO.insertReasonToAdminLog(webid,Project.getProjectId(),rsn,conn);
						                	if(updt == 1) logger.debug("This Reason to ets_admin_log is updated  == " +rsn);
						                }
										
										if (bRequestedEnt) { // entitlement requested - so the active flag is "P"
											EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'P'  where user_id = '" + webid + "' and user_project_id = '" + Project.getProjectId() + "'");
										} else { // "A"
											ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
											boolean bHasPendEtitlement = procFuncs.userHasPendEntitlement(conn, u.getEdgeId(), reqstdProject);
											if (bHasPendEtitlement) {
												EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'P'  where user_id = '" + webid + "' and user_project_id = '" + Project.getProjectId() + "'");
											} else {
												EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'A'  where user_id = '" + webid + "' and user_project_id = '" + Project.getProjectId() + "'");
											}
										}

										Metrics.appLog(conn, es.gIR_USERN, WrkSpcTeamUtils.getMetricsLogMsg(Project, "Team_Add"));

										arh.Accept(id, es.gIR_USERN, reply);

										if (added.length() == 0) {
											added.append(webid);
										} else {
											added.append(", " + webid);
										}

										// send email

										if (bRequestedEnt) {
											boolean sentOK = ETSBoardingUtils.sendAcceptEMailPendEnt(u, ar, Project.getName(), es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim(), reply, sMgrEmail);
											ETSUserDetails ud = new ETSUserDetails();
											ud.setWebId(es.gIR_USERN);
											ud.extractUserDetails(conn);
											if (u.getUserType() == u.USER_TYPE_INTERNAL) {
												sOkMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> has been approved successfully. The corresponding entitlement to access " + unBrandedprop.getAppName() + " has been requested for the user and is pending with their manager.</li>");
											} else {
												if (u.getPocId().equals(ud.getDecafId())) {
													// user is the POC of the requestor
													sOkMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> has been approved successfully. The corresponding entitlement to access " + unBrandedprop.getAppName() + " has been requested for the user.</li>");
												} else {
													sOkMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> has been approved successfully. The corresponding entitlement to access " + unBrandedprop.getAppName() + " has been requested for the user and is pending with their IBM contact.</li>");
												}
											}
										} else {
											boolean sentOK = ETSBoardingUtils.sendAcceptEMail(u, ar, Project.getName(), es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim(), reply, sMgrEmail);
											sOkMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> has been approved successfully.</li>");
										}

									} else {
										SysLog.log(SysLog.ERR, this, "FAILED:  User " + webid + " could not be added to Project=" + Project.getName());
										sErrMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> could not be processed. There was an error processing this request.</li>");
									}
								} else {
									SysLog.log(SysLog.ERR, this, "FAILED:  User " + webid + " could not be added to Project=" + Project.getName());
									sErrMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> could not be processed. There was an error processing this request.</li>");
								}

							}
						} else {
							arh.Accept(id, es.gIR_USERN, reply);
							boolean sentOK = ETSBoardingUtils.sendAcceptEMail(u, ar, Project.getName(), es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim(), reply, sMgrEmail);
							sErrMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> has not been processed because user is already a member of this workspace.</li>");
						}

					}

				} else if (action.equalsIgnoreCase("Reject")) {

					// make the request as rejected, send the email and continue.

					arh.Reject(id, es.gIR_USERN, reply);
					if (u.isUserExists() && u.getEMail() != null && u.getEMail().length() > 0) {
						boolean sentOK = ETSBoardingUtils.sendRejectEMail(u, ar, Project.getName(), es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim(), reply, sMgrEmail);
					}

					sOkMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> has been rejected.</li>");

				} else if (action.equalsIgnoreCase("Hold")) {
					// leave the request as it is and continue.
					// dont display the message that it has been left on hold.. it takes too much space for now.
					//sOkMsg.append("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + webid + " ]</b> has been left on hold.</li>");
				}

			}

			StringBuffer sMsg = new StringBuffer("");
			if (sErrMsg.length() > 0) {
				sMsg.append("<span style=\"color:#ff3333\"><ul>" + sErrMsg + "</ul></span>");
			}
			if (sOkMsg.length() > 0) {
				sMsg.append("<ul>" + sOkMsg + "</ul><br />");
			}

			accreqsView(sMsg.toString());

		} catch (Exception e) {

			logger.error("execption==", e);
			e.printStackTrace();
			throw e;
		} finally { /* used the common connection */
		}
	}

	/**
	 * Method doAddRequestMember.
	 * @param acf
	 */
	private void doAddRequestMember(AccessCntrlFuncs acf) {
		try {

			String submitted = getParameter(request, "addmember");
			String subreq = getParameter(request, "addmemberreq");

			StringBuffer sErrMsg = new StringBuffer("");
			StringBuffer sOkMsg = new StringBuffer("");
			boolean invite = false;

			// this user may not be any where - give a invite screen
			ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
			Vector owners = ETSDatabaseManager.getUsersByProjectPriv(Project.getProjectId(), Defines.OWNER, conn);
			Global.Init();

			if (owners.size() < 0) {
				SysLog.log(SysLog.ERR, this, "NO OWNER for Project=" + Project.getName() + "  request to Admin");
				//owner_email = properties.getAdminEmail();
				sErrMsg.append("<li>Workspace owner not defined for this workspace. Unable to process request to add member.</li>");
			} else {
				String owner_email = ETSUtils.getUserEmail(conn, procFuncs.woWebId(Project.getProjectId(), conn));
				String webId = getParameter(request, "uid").trim().toLowerCase(); // userid is not case sensitive

				if (webId.equals("")) {
					membersRequestAdd("");
					return;
				}
				SysLog.log(SysLog.DEBUG, this, "Add Requested on: " + webId);
				// check if this is a valid userid
				ETSSyncUser syncUser = new ETSSyncUser(); // sync the user
				syncUser.setWebId(webId);
				syncUser.syncUser(conn);

				String count = EntitledStatic.getValue(conn, "select count(*) from amt.users where ir_userid='" + webId + "' with ur");
				if (Integer.parseInt(count) == 1) {
					// webId remains webId
					// see if it is validated
				} else {
					// there is no webId
					// see if this is an email and see the count
					String emailCount = EntitledStatic.getValue(conn, "select count(*) from amt.users where user_email='" + webId + "' with ur");
					if (Integer.parseInt(emailCount) == 1) {
						// only one found - get the webId
						webId = EntitledStatic.getValue(conn, "select ir_userid from amt.users where user_email='" + webId + "' with ur");
					}
					if (Integer.parseInt(emailCount) > 1) {
						// more than one found - choose one - only E&I
						ETSProcReqAccessGoPages goPages = new ETSProcReqAccessGoPages();
						Hashtable valhash = new Hashtable();
						valhash.put("projid", this.Project.getProjectId());
						valhash.put("tc", "" + this.TopCatId);
						valhash.put("cc", "" + this.CurrentCatId);
						valhash.put("email", webId); // send email to
						invite = true;
						printHeader("Request adding a team member", "", false);
						writer.println(goPages.useridChoice(valhash, conn));
					}
					if (Integer.parseInt(emailCount) < 1) {
						// none found - give email
						// throw a inviatation email page
						ETSProcReqAccessGoPages goPages = new ETSProcReqAccessGoPages();
						Hashtable valhash = new Hashtable();
						ETSUserDetails usrDt = new ETSUserDetails();
						usrDt.setWebId(es.gIR_USERN);
						usrDt.extractUserDetails(conn);
						if (webId.indexOf("@") > 0) {
							valhash.put("toid", webId);
						} else {
							valhash.put("toid", " ");
						}
						valhash.put("ibmid", webId); // send email to
						valhash.put("reqid", usrDt.getEMail()); // from
						valhash.put("reqemail", this.es.gEMAIL); // id keyed in
						valhash.put("projname", this.Project.getName()); // project for
						valhash.put("tc", "" + this.TopCatId);
						valhash.put("cc", "" + this.CurrentCatId);
						valhash.put("projid", this.Project.getProjectId());
						valhash.put("woemail", ETSUtils.getUserEmail(conn, procFuncs.woWebId(this.Project.getProjectId(), conn)));
						if (procFuncs.isWO(es, this.Project.getProjectId())) {
							valhash.put("woemailcc", " "); // cc eamil
						} else {
							valhash.put("woemailcc", ETSUtils.getUserEmail(conn, procFuncs.woWebId(this.Project.getProjectId(), conn)));
						}
						invite = true;
						printHeader("Request adding a team member", "", false);
						writer.println(goPages.invite(valhash));
					}
				}

				// process the request here....
				ETSUserDetails uDet = new ETSUserDetails();
				uDet.setWebId(webId);
				uDet.extractUserDetails(conn);
				// check to see if this user is already a member of this workspace
				if (uDet.getUserType() != uDet.USER_TYPE_INTERNAL_PENDING_VALIDATION) {
					if (!ETSDatabaseManager.isUserInProject(webId, Project.getProjectId(), conn)) {
						// check to see if there is already a pending request for this user.
						if (!hasPendingRequest(conn, webId, Project.getProjectId())) {
							// check if address is ok - if not take the user to address confirmation
							// page to send email - and email and bring the user back to this page.
							if (uDet.getStreetAddress().equals("") && uDet.getUserType() == uDet.USER_TYPE_EXTERNAL) {
								// IBMer - has no need for email
								Hashtable valHash = new Hashtable();
								valHash.put("toid", uDet.getEMail());
								valHash.put("reqemail", es.gEMAIL);
								ETSProcReqAccessFunctions procFunctions = new ETSProcReqAccessFunctions();

								if (!procFunctions.isWO(es, this.Project.getProjectId())) {
									valHash.put("woemailcc", es.gEMAIL);
								} else {
									valHash.put("woemailcc", "");
								}

								valHash.put("ibmid", uDet.getWebId());
								valHash.put("projname", this.Project.getName());
								valHash.put("woemail", ETSUtils.getUserEmail(conn, procFunctions.woWebId(this.Project.getProjectId(), conn)));
								valHash.put("projid", this.Project.getProjectId());
								valHash.put("tc", "" + this.TopCatId);
								valHash.put("cc", "");
								//valHash.put("action","admin_add");

								// internal user not yet validated exists in this workspace.
								ETSProcReqAccessGoPages goPages = new ETSProcReqAccessGoPages();
								writer.println("<input type=\"hidden\" name=\"admin_op\" id=\"label_adOp\" value=\"verify_addmember\" />");
								Global.Init();
								// take the user error
								// turned on the invite option, becauz it was showing 2 screens
								invite = true;
								valHash.put("option", "noaddressmail");
								writer.println(goPages.errPage(1, valHash, uDet));
							} else {
								SysLog.log(SysLog.DEBUG, this, "Request for " + Project.getProjectId() + "  created for " + webId + "   Email to " + owner_email);
								ETSUserAccessRequest uar = new ETSUserAccessRequest();
								uar.recordRequest(conn, webId, Project.getName(), owner_email, es.gIR_USERN, Project.getProjectType()); //??

								Metrics.appLog(conn, es.gIR_USERN, WrkSpcTeamUtils.getMetricsLogMsg(Project, "Team_Add_Request"));

								boolean sent = ETSBoardingUtils.sendManagerEMail(uDet, owner_email, Project, es.gEMAIL);
								//sOkMsg.append("<li>Access request for IBM Id : <b>"+ webId+ "</b> has been created successfully.</li>");
								sOkMsg.append("<li>Your request to add " + uDet.getFirstName() + " " + uDet.getLastName() + " [" + webId + "] has been forwarded to the Workspace Owner for approval. If you are unsure of who the workspace owner is select on the team tab in the workspace.</li>");
							}
						} else {
							// A pending request already exists for this user for this project. Do not process this
							sErrMsg.append("<li>Requested IBM ID : <b>" + webId + "</b> already has a pending request for access to this workspace.</li>");
						}
					} else {
						// user already exists in this workspace.
						sErrMsg.append("<li>Requested IBM ID : <b>" + webId + "</b> is already a member of this workspace.</li>");
					}
				} else {

					sOkMsg.append("<li>This ID <b>" + webId + "</b> is in an obsolete state. Please contact the " +
						"help desk <a href=\"mailto:econnect@us.ibm.com\" >econnect@us.ibm.com</a> to have them update the ID to a valid internal user. " +
						"Once this has been updated you can add them to the workspace.</li>");

// Commented for CSR - IBMCC00010010 -- IBM internal member being asked to validate through email
//					Hashtable valHash = new Hashtable();
//					valHash.put("toid", uDet.getEMail());
//					valHash.put("reqemail", es.gEMAIL);
//					ETSProcReqAccessFunctions procFunctions = new ETSProcReqAccessFunctions();
//
//					if (!procFunctions.isWO(es, this.Project.getProjectId())) {
//						valHash.put("woemailcc", es.gEMAIL);
//					} else {
//						valHash.put("woemailcc", "");
//					}
//
//					valHash.put("ibmid", uDet.getWebId());
//					valHash.put("projname", this.Project.getName());
//					valHash.put("woemail", ETSUtils.getUserEmail(conn, procFunctions.woWebId(this.Project.getProjectId(), conn)));
//					valHash.put("projid", this.Project.getProjectId());
//					valHash.put("tc", "" + this.TopCatId);
//					valHash.put("cc", "");
//					//valHash.put("action","admin_add");
//
//					// internal user not yet validated exists in this workspace.
//					ETSProcReqAccessGoPages goPages = new ETSProcReqAccessGoPages();
//					writer.println("<input type=\"hidden\" name=\"admin_op\" id=\"label_adOp\" value=\"verify_addmember\" />");
//					Global.Init();
//					// take the user error
//					valHash.put("option", "invalidmail");
//					writer.println(goPages.errPage(2, valHash, uDet));
//					return;
//					//                    procFuncs.sendValidateEmail( es.gEMAIL, uDet.getEMail(),"",Global.mailHost,this.Project.getName(),owner_email);
//					//                    sErrMsg.append( "<li>Requested IBM ID : <b>"+ webId + "</b> is not yet validated. An email has been sent to the user to validate and place a request</li>");
				}
				StringBuffer sMsg = new StringBuffer("");
				if (sErrMsg.length() > 0) {
					sMsg.append("<span style=\"color:#ff3333\"><ul>" + sErrMsg + "</ul></span>");
				}
				if (sOkMsg.length() > 0) {
					sMsg.append("<ul>" + sOkMsg + "</ul><br />");
				}
				if (!invite) {
					membersRequestAdd(sMsg.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error= ", e);
		}
		return;
	}

	private void doAddMember(AccessCntrlFuncs acf) {

		try {
			String submitted = getParameter(request, "addmember");
			String subreq = getParameter(request, "addmemberreq");
			
			String sCmpRsn = request.getParameter("cmpRsn");
			if (sCmpRsn == null || sCmpRsn.trim().equals("")) {
				sCmpRsn = "";
			} else {
				sCmpRsn = sCmpRsn.trim();
			}

			if (submitted.equals("") && subreq.equals("")) {
				membersAdd("");
			} else if (!subreq.equals("")) { //user add member request
				String userid = (getParameter(request, "uid")).trim();
				String job = (getParameter(request, "job")).trim();
				String addinfo = (getParameter(request, "addinfo")).trim();
				if (userid.equals("")) {
					membersAdd("User id must be between 1- 32 chars long.");
				} else {
					//check to see if user id is valid
					UserObject uo = AccessCntrlFuncs.getUserObject(conn, userid, true, false);

					if (uo.gIR_USERN != null) { // valid userid
						boolean exists = ETSDatabaseManager.isUserInProject(userid, Project.getProjectId(), conn);
						if (!exists) {
							String[] res = sendMemberRequest(uo, addinfo, job, "add", Project, TopCatId, CurrentCatId, es, acf, conn);
							System.err.println("back from sendMemberRequest");
							if (res[0].equals("0")) {

								Metrics.appLog(conn, es.gIR_USERN, WrkSpcTeamUtils.getMetricsLogMsg(Project, "Team_Add_Request"));

								membersAdd(res[1]);
							} else {
								membersAdd(res[1]);
							}
						} else {
							membersAdd("Userid: " + userid + " already exists in this project.");
						}
					} else { //not valid id
						membersAdd("Userid:" + userid + " is not a valid Customer Connect id.");
					}
				}
			} else { //admin add member
				//check for bad inputs
				String userid = (getParameter(request, "uid")).trim();
				String job = (getParameter(request, "job")).trim();
				String roleStr = (getParameter(request, "roles")).trim();

				if (userid.equals("")) {
					System.out.println("userid=blank");
					membersAdd("User id must be between 1- 32 chars long.");
				} else if (roleStr.equals("")) {
					System.out.println("access level not set");
					membersAdd("Access level must be selected for the user.");
				} else if (job.equals("")) {
					System.out.println("job resp not set");
					if (Project.getProjectOrProposal().equals("P")) {
						membersAdd("Project role must be selected for the user.");
					} else {
						membersAdd("Proposal role must be selected for the user.");
					}
				} else {
					System.out.println("in else");
					int roleid = (new Integer(roleStr)).intValue();
					//check to see if user id is valid
					//boolean isValidId = true;
					UserObject uo = AccessCntrlFuncs.getUserObject(conn, userid, true, false);
					//check to see if role is for ibm only and user is external

					if (uo.gIR_USERN != null) { // valid userid
						boolean external = true;
						String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, userid);
						String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
						if (decaftype.equals("I")) {
							external = false;
						}

						if (ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.IBM_ONLY, conn) && external) {
							System.out.println("user external and role only for internals");
							membersAdd("The role selected can only be assigned to IBM internals.");
						}
						if (ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.OWNER, conn)) {
							System.out.println("can not add workspace owner");
							membersAdd("The role selected can only be assigned.");
						} else {
							ETSUser new_user = new ETSUser();
							new_user.setUserId(userid);
							new_user.setRoleId(roleid);
							new_user.setProjectId(Project.getProjectId());
							new_user.setUserJob(job);
							new_user.setPrimaryContact(Defines.NO);
							new_user.setLastUserId(es.gIR_USERN);

							//String[] res = databaseManager.addProjectMember(userid,roleid,Project.getProjectId());
							String[] res = ETSDatabaseManager.addProjectMember(new_user, conn);
							String success = res[0];

							if (success.equals("0")) {
								
								if(! StringUtil.isNullorEmpty(sCmpRsn)){
				                	int updt = 0;
				                	String rsn = sCmpRsn.trim();
				                	updt = AddMembrToWrkSpcDAO.insertReasonToAdminLog(userid,Project.getProjectId(),rsn,conn);
				                	if(updt == 1) logger.debug("This Reason to ets_admin_log is updated  == " +rsn);
				                }
								
								ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
								boolean bHasPendEtitlement = procFuncs.userHasPendEntitlement(conn, edge_userid, Defines.REQUEST_PROJECT);
								if (bHasPendEtitlement) {
									EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'P'  where user_id = '" + userid + "' and user_project_id = '" + Project.getProjectId() + "'");
								} else {
									EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'A'  where user_id = '" + userid + "' and user_project_id = '" + Project.getProjectId() + "'");
								}

								Metrics.appLog(conn, es.gIR_USERN, WrkSpcTeamUtils.getMetricsLogMsg(Project, "Team_Add"));

								membersAdd("Userid: " + userid + " has been added.");
							} else {
								membersAdd("Error occurred while adding member " + userid + ". Error=" + res[1] + ".");
								//admin_serv.membersAdd("Error occurred while adding member "+userid+".  Please try again.",es.gIR_USERN, writer,conn);
							}
						}
					} else { //not valid id
						System.out.println("invalid id");
						membersAdd("Userid:" + userid + " is not a valid Customer Connect id.");
					}
				}
			}
		} catch (Exception e) {
			logger.error("error= ", e);
		}

		return;
	}

	private void doDelMember(AccessCntrlFuncs acf) {
		try {
			String submitted = getParameter(request, "delmember");
			String subreq = getParameter(request, "delmemberreq");

			if (submitted.equals("") && subreq.equals("")) {
				membersDel("");
			} else if (!subreq.equals("")) { //user add member request
				String userid = (getParameter(request, "uid")).trim();
				String addinfo = (getParameter(request, "addinfo")).trim();
				if (userid.equals("")) {
					//membersDel("User id must be between 1- 32 chars long.");
					membersDel("You must select a valid user id.");
				} else {
					//check to see if user id is valid
					UserObject uo = AccessCntrlFuncs.getUserObject(conn, userid, true, false);

					if (uo.gIR_USERN != null) { // valid userid
						boolean exists = ETSDatabaseManager.isUserInProjectForDel(userid, Project.getProjectId(), conn);
						//boolean exists = false;
						if (exists) {
							String[] res = sendMemberRequest(uo, addinfo, "", "del", Project, TopCatId, CurrentCatId, es, acf, conn);
							if (res[0].equals("0")) {

								Metrics.appLog(conn, es.gIR_USERN, WrkSpcTeamUtils.getMetricsLogMsg(Project, "Team_Delete_Request"));

								membersDel(res[1]);
							} else {
								membersDel(res[1]);
							}
						} else {
							membersDel("Userid: " + userid + " does not exist in this team.");
						}
					} else { //not valid id
						membersDel("Userid:" + userid + " is not a valid Customer Connect id.");
					}
				}
			} else { //admin del member
				//check for bad inputs
				String userid = (getParameter(request, "uid")).trim();

				if (userid.equals("")) {
					membersDel("You must select a valid user id.");
				} else {
					//check to see if member is primary contact
					ETSUser ets_user = ETSDatabaseManager.getETSUserAll(userid, Project.getProjectId(), conn);
					if (ets_user.getPrimaryContact() == Defines.YES) {
						membersDel("The member you are trying to remove (" + ets_user.getUserId() + ") is the primary contact and can not be removed.  If you wish to remove this member, you must first change the primary contact to another member.");
					} else {
						//check to see if user id is valid
						UserObject uo = AccessCntrlFuncs.getUserObject(conn, userid, true, false);

						if (uo.gIR_USERN != null) { // valid userid
							String[] res = ETSDatabaseManager.delProjectMember(userid, Project.getProjectId(), conn);
							String success = res[0];

							if (success.equals("0")) {

								Metrics.appLog(conn, es.gIR_USERN, WrkSpcTeamUtils.getMetricsLogMsg(Project, "Team_Delete"));

								membersDel("Userid: " + userid + " has been removed from this workspace.");
							} else {
								membersDel("Error occurred while removing member " + userid + ".  Please try again.");
							}
						} else { //not valid id
							membersDel("Userid:" + userid + " is not a valid Customer Connect id.");
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("error= ", e);
			e.printStackTrace();
		}
	}

	private void doEditPrimaryContact(AccessCntrlFuncs acf) {

		try {
			String submitted = getParameter(request, "sub");

			if (!(ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.ADMIN, conn) || ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.MANAGE_USERS, conn) || isSuperAdmin)) {
				editPrimaryContact("", acf);
			}

			if (submitted.equals("")) {
				editPrimaryContact("", acf);
			} else { //admin add member
				//check for bad inputs
				String userid = (getParameter(request, "uid")).trim();

				if (userid.equals("")) {
					System.out.println("userid=blank");
					editPrimaryContact("Please choose a valid primary contact", acf);
				} else {
					System.out.println("in else");
					//check to see if user id is valid
					UserObject uo = AccessCntrlFuncs.getUserObject(conn, userid, true, false);
					if (uo.gIR_USERN != null) { // valid userid
						//String[] res = {"0","fake"}; //databaseManager.editPrimaryContact(Project.getProjectId(),userid);
						String[] res = ETSDatabaseManager.editPrimaryContact(Project.getProjectId(), userid, es.gIR_USERN, conn);
						String success = res[0];

						if (success.equals("0")) {

							//set the updated contact info into session also
							//initialize the common params bean//
							HttpSession session = request.getSession(true);
							//				EtsIssCommonSessnParams etsCommonParams = new EtsIssCommonSessnParams(session);
							//etsCommonParams.setEtsContInfo(Project.getProjectId());//
							editPrimaryContact("", acf);
						} else {
							editPrimaryContact("Error occurred while changing primary contact. Error=" + res[1] + ".", acf);
						}
					} else { //not valid id
						editPrimaryContact("Userid:" + userid + " is not a valid Customer Connect id.", acf);
					}
				}
			}
		} catch (Exception e) {
			logger.error("error= ", e);
			e.printStackTrace();
		}

		return;
	}

	/**
	 * Method accreqsView.
	 * @param msg
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 */
	public void accreqsView(String msg) throws SQLException, IOException, Exception {

		StringBuffer buf = new StringBuffer();
		String userid = es.gIR_USERN;
		String email = es.gEMAIL;

		//check if user has admin priviliges
		boolean admin = ETSDatabaseManager.isProjectAdmin(userid, Project.getProjectId(), conn);

		if (true || admin) { // CRUDE

			buf.append("<form method=\"post\" action=\"ETSProjectsServlet.wss\" name=\"viewaccreqsForm\">");
			buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
			buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"action\" value=\"viewaccreqs\" />");
			//?buf.append("<input type=\"hidden\" name=\"addmember\" value=\"sub\" />");

			printHeader("Access requests", "", false);
			Statement stmt = null;
			ResultSet rs = null;
			StringBuffer sQuery = new StringBuffer("");

			try {

				sQuery.append("SELECT USER_ID,PROJECT_NAME,DATE_REQUESTED,REQUEST_ID FROM ETS.ETS_ACCESS_REQ WHERE MGR_EMAIL = '" + es.gEMAIL + "' AND STATUS in ('" + Defines.ACCESS_PENDING + "','" + Defines.ACCESS_APPROVED + "') and PROJECT_TYPE='" + Project.getProjectType() + "' order by date_requested desc with ur");

				SysLog.log(SysLog.DEBUG, "ETSConnectServlet::displayPendingRequests", "Query : " + sQuery.toString());

				stmt = conn.createStatement();
				rs = stmt.executeQuery(sQuery.toString());

				Vector vDetails = new Vector();

				while (rs.next()) {

					String sUserFor = rs.getString(1);
					String sProjName = rs.getString(2);

					String sTempDate = rs.getTimestamp("DATE_REQUESTED").toString();
					String sDateRequested = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
					String sRequestId = rs.getString(4);
					vDetails.addElement(new String[] { sUserFor, sProjName, sDateRequested, sRequestId });

				}

				if (vDetails != null && vDetails.size() > 0) {

					buf.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
					buf.append("<tr><td  class=\"tdblue\">");

					buf.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
					buf.append("<tr>");
					buf.append("<td  height=\"18\" class=\"tblue\">&nbsp;Pending access requests</td>");
					buf.append("</tr>");
					buf.append("<tr><td  width=\"443\">");
					buf.append("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
					buf.append("<tr valign=\"middle\">");
					buf.append("<td  style=\"background-color: #ffffff; color: #000000; font-weight: normal;\" align=\"center\">");
					buf.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

					String bgColor = "#ffffff";

					for (int i = 0; i < vDetails.size(); i++) {

						String sTemp[] = (String[]) vDetails.elementAt(i);

						String sUserFor = sTemp[0];
						String sProjName = sTemp[1];
						String sDateRequested = sTemp[2];
						String sRequestId = sTemp[3];

						ETSProperties prop = new ETSProperties();
						String sMsg = prop.getSApprovalDefault();

						if (i == 0) {
							buf.append("<tr valign=\"top\">");
							buf.append("<td colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td>");
							buf.append("</tr>");

							buf.append("<tr valign=\"top\">");
							buf.append("<td colspan=\"4\"  align=\"left\">" + sMsg + "</td>");
							buf.append("</tr>");

							buf.append("<tr valign=\"top\">");
							buf.append("<td colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
							buf.append("</tr>");

							buf.append("<tr valign=\"top\">");
							buf.append("<td colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
							buf.append("</tr>");

							buf.append("<tr valign=\"top\">");
							buf.append("<td colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
							buf.append("</tr>");

							buf.append("<tr valign=\"top\">");
							buf.append("<td colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td>");
							buf.append("</tr>");

							buf.append("<tr>");
							buf.append("<th id=\"req_for\" width=\"100\" align=\"left\" valign=\"top\" class=\"small\"><b>User<br />[IBM ID]</b></td>");
							buf.append("<th id=\"req_proj\" width=\"143\" align=\"left\" valign=\"top\"  class=\"small\"><b>Workspace<br /> Requested&#42;</b></td>");
							buf.append("<th id=\"req_date\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><b>Date<br />requested</b></td>");
							buf.append("<th id=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><b>Status</b></td>");
							buf.append("</tr>");
						}

						buf.append("<tr valign=\"top\">");
						buf.append("<td colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td>");
						buf.append("</tr>");

						buf.append("<tr valign=\"top\">");
						buf.append("<td colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
						buf.append("</tr>");

						buf.append("<tr valign=\"top\">");
						buf.append("<td colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td>");
						buf.append("</tr>");

						ETSUserDetails userDtls = new ETSUserDetails();
						userDtls.setWebId(sUserFor);
						userDtls.extractUserDetails(conn);

						//ETSProcReqAccessFunctions procFunc = new ETSProcReqAccessFunctions();
						//String status = procFunc.getStatus(sRequestId, conn);
						String status = getAccessStatus(conn, sRequestId, Project.getProjectType());
						if (userDtls.getUserType() == userDtls.USER_TYPE_INTERNAL || userDtls.getUserType() == userDtls.USER_TYPE_EXTERNAL) {
							if (!status.equals("hasEntitlement")) {
								buf.append("<tr style=\"background-color:" + (bgColor = (bgColor.equals("#eeeeee") ? "#ffffff" : "#eeeeee")) + "\" valign=\"top\">");
								if (status.equals("New Request") || status.startsWith("Forwarded by")) {
									buf.append(
										"<td headers=\"req_for\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><a class=\"fbox\" href=\""
											+ Defines.SERVLET_PATH
											+ unBrandedprop.getProcReqstServletURL()
											+ "?linkid="
											+ unBrandedprop.getLinkID()
											+ "&option=showreq&requestid="
											+ sRequestId
											+ "&ibmid="
											+ sUserFor
											+ "\">"
											+ ETSUtils.getUsersName(conn, sUserFor)
											+ "<br />["
											+ sUserFor
											+ "]</a></td>");
								} else {
									buf.append("<td headers=\"req_for\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + ETSUtils.getUsersName(conn, sUserFor) + "<br />[" + sUserFor + "]</td>");
								}

								buf.append("<td headers=\"req_proj\" width=\"143\" align=\"left\" valign=\"top\"  class=\"small\">" + sProjName + "</td>");
								buf.append("<td headers=\"req_date\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + sDateRequested + "</td>");
								buf.append("<td headers=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + status + "</td>");
								buf.append("</tr>");
							}
						}
					}

					buf.append("<tr valign=\"top\">");
					buf.append("<td colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					buf.append("</tr>");
					buf.append("<tr valign=\"top\">");
					buf.append("<td colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td>");
					buf.append("</tr>");
					buf.append("<tr valign=\"top\">");
					buf.append("<td colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
					buf.append("</tr>");
					buf.append("<tr valign=\"top\">");
					buf.append("<td colspan=\"4\" align=\"left\">&#42;Workspace Requested as user entered on the request access form</td>");
					buf.append("</tr>");

					buf.append("</table>");
					buf.append("</td>");
					buf.append("</tr>");
					buf.append("</table>");
					buf.append("</td>");
					buf.append("</tr>");
					buf.append("</table>");

					buf.append("</td></tr></table>");

					buf.append("<br />");

				}

			} catch (Exception eX) {
				logger.error("execption==", eX);
				eX.printStackTrace();
			}

			finally {
						ETSDBUtils.close(rs);
						ETSDBUtils.close(stmt);
					}
		}
		writer.println(buf.toString());

	}

	/**
	 * Method membersRequestAdd.
	 * @param msg
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 */
	public void membersRequestAdd(String msg) throws SQLException, IOException, Exception {
		StringBuffer buf = new StringBuffer();
		String userid = es.gIR_USERN;

		//check if user has admin priviliges
		boolean admin = false; //ETSDatabaseManager.isProjectAdmin(userid,Project.getProjectId());
		if (admin) { // CRUDELY SHUT OFF
			buf.append("<form method=\"get\" action=\"ETSProjectsServlet.wss\" name=\"membersReqAddForm\">");
			buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
			buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"action\" value=\"addreqmember\" />");
			buf.append("<input type=\"hidden\" name=\"addmember\" value=\"sub\" />");

			printHeader("Request adding a team member", "", false);

			if (!msg.equals("")) {
				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">");
				buf.append("<tr>");
				buf.append("<td  valign=\"top\" align=\"left\" >");
				buf.append("<span style=\"color:#ff3333\">" + msg + "</span>");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("</table>");
			}

			buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"443\" border=\"0\">");
			buf.append("<tr><td colspan=\"2\" class=\"small\"><span class=\"ast\"></span>Comma-separated values.</td></tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");

			buf.append("<tr>");
			buf.append("<td nowrap=\"nowrap\"><label for=\"uid\"><b>User id:</b></label></td>");
			buf.append("<td><input id=\"uid\" class=\"iform\" maxlength=\"132\" name=\"uid\" size=\"50\" type=\"text\" value=\"\" /></td>");
			buf.append("</tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");
			/*
						if (Project.getProjectOrProposal().equals("P")){
							buf.append("<td nowrap=\"nowrap\"><label for=\"job\"><b>Project role:</b></label></td>");
						}
						else{
							buf.append("<td nowrap=\"nowrap\"><label for=\"job\"><b>Proposal role:</b></label></td>");
						}

						buf.append("<td valign=\"bottom\"><input id=\"job\" class=\"iform\" maxlength=\"64\" name=\"job\" size=\"35\" type=\"text\" value=\"\" /></td>");
						buf.append("</tr>");
						buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");
			*/
			/* Get rid of access level as well
						buf.append("<tr>");
						buf.append("<td valign=\"top\" nowrap=\"nowrap\"><b>Access level:</b></td>");
						buf.append("<td>");
						Vector r = databaseManager.getRolesPrivs(Project.getProjectId());
						ETSProjectInfoBean projBean = new ETSProjectInfoBean(conn);
						buf.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">");


						for (int i = 0; i<r.size(); i++){
							String[] rp = (String[])r.elementAt(i);
							int roleid = (new Integer(rp[0])).intValue();
							String rolename = rp[1];
							//String privs = rp[2];
							String privids = rp[3];

							if (!(databaseManager.doesRoleHavePriv(roleid,Defines.OWNER))){
								buf.append("<tr>");
								buf.append("<td align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_"+i+"\" type=\"radio\" name=\"roles\" value=\""+roleid+"\" /></td>");
								buf.append("<td align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"role_"+i+"\">"+rolename+"</label></td>");
								buf.append("</tr>");
								buf.append("<tr>");
								buf.append("<td>&nbsp;</td>");
								//buf.append("<td align=\"left\" valign=\"top\">Privileges: "+privs+"</td>");
								buf.append("<td align=\"left\" valign=\"top\">Privileges: ");
								String priv_desc = "";
								StringTokenizer st = new StringTokenizer(privids, ",");
								Vector privs = new Vector();
								while (st.hasMoreTokens()){
									String priv = st.nextToken();
									privs.addElement(priv);
								}
								for (int j = 0; j < privs.size(); j++){
									String s = (String)privs.elementAt(j);
									String  desc = projBean.getInfoDescription("PRIV_"+s,0);
									if (!desc.equals("")){
										if(!priv_desc.equals("")){
											priv_desc = priv_desc+"; "+desc;
										}
										else{
											priv_desc = desc;
										}
									}
								}
								buf.append(priv_desc);
								buf.append("</td>");
								buf.append("</tr>");
								buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
							}
						}
			END of old access level code  */
			buf.append("</table>");

			buf.append("</td>");
			buf.append("</tr>");
			//buf.append("<tr><td colspan=\"2\" class=\"small\"><span class=\"ast\"><b>*</b></span>All fields are required</td></tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");

			buf.append("</table>");

			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
			buf.append("<tr>");
			buf.append("<td  align=\"left\">");
			buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Submit\" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\">&nbsp;</td>");
			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk_c.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
			buf.append("</td> ");

			buf.append("</tr>");
			buf.append("</table> ");
			buf.append("</form> ");

		} //end of if admin
		else {

			WrkSpcTeamGuiUtils guiUtils = new WrkSpcTeamGuiUtils();
			//buf.append("not admin code here");

			buf.append("<form method=\"post\" action=\"ETSProjectsServlet.wss\" name=\"membersaddReqForm\">");
			buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
			buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"linkid\" value=\"" + linkid + "\" />");
			buf.append("<input type=\"hidden\" name=\"action\" value=\"addreqmember\" />");
			buf.append("<input type=\"hidden\" name=\"addmemberreq\" value=\"sub\" />");

			printHeader("Request adding a team member", "", false);

			if (!msg.equals("")) {
				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
				buf.append("<tr>");
				buf.append("<td  valign=\"top\" align=\"left\" >" + msg + "</td>");
				buf.append("</tr>");
				buf.append("<tr><td height=\"10\">");
				buf.append("<img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"443\" alt=\"\" />");
				buf.append("</td></tr>");
				buf.append("</table>");
			}

			buf.append("<table cellpadding=\"2\" cellspacing=\"1\" width=\"600\" border=\"0\">");
			buf.append("<tr><td colspan=\"2\">Please enter the IBM ID or e-mail address of the user you wish to add to the team and click on submit.");
			buf.append("</td></tr>");

			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td></tr>");

			buf.append("<tr>");
			buf.append("<td nowrap=\"nowrap\"><label for=\"uid\"><span class=\"ast\"></span><b>IBM ID (or) e-mail address:</b></label></td>");
			buf.append("<td><input id=\"uid\" class=\"iform\" maxlength=\"132\" name=\"uid\" size=\"30\" type=\"text\" value=\"\" /></td>");
			buf.append("</tr>");

			buf.append("<tr valign=\"top\">");
			buf.append("<td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td>");
			buf.append("</tr>");

			buf.append("<tr valign=\"top\">");
			buf.append("<td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			buf.append("</tr>");
			buf.append("</table>");
			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
			buf.append("<tr>");
			buf.append("<td  align=\"left\">");
			buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Submit\" />&nbsp;");
			buf.append("</td> ");

			buf.append("<td  align=\"left\">&nbsp;</td>");
			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\" >");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
			buf.append("</td> ");
			buf.append("</tr>");
			buf.append("</table> ");
			buf.append("</form> ");

		} //end of else not admin
		writer.println(buf.toString());
	}

	public void membersAdd(String msg) throws SQLException, IOException, Exception {
		StringBuffer buf = new StringBuffer();
		String userid = es.gIR_USERN;
		System.out.println("membersAdd -- SHOULD NOT BE HERE");
		//check if user has admin priviliges
		boolean admin = ETSDatabaseManager.isProjectAdmin(userid, Project.getProjectId(), conn);
		if (admin || isSuperAdmin) {
			buf.append("<form method=\"get\" action=\"ETSProjectsServlet.wss\" name=\"membersaddForm\">");
			buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
			buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"action\" value=\"addmember\" />");
			buf.append("<input type=\"hidden\" name=\"addmember\" value=\"sub\" />");

			printHeader("Add team member", "", false);

			if (!msg.equals("")) {
				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">");
				buf.append("<tr>");
				buf.append("<td  valign=\"top\" align=\"left\" >");
				buf.append("<span style=\"color:#ff3333\">" + msg + "</span>");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("</table>");
			}

			buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"443\" border=\"0\">");
			buf.append("<tr><td colspan=\"2\" class=\"small\"><span class=\"ast\"><b>*</b></span>All fields are required</td></tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");

			buf.append("<tr>");
			buf.append("<td nowrap=\"nowrap\"><label for=\"uid\"><b>User id:</b></label></td>");
			buf.append("<td><input id=\"uid\" class=\"iform\" maxlength=\"32\" name=\"uid\" size=\"35\" type=\"text\" value=\"\" /></td>");
			buf.append("</tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");

			if (Project.getProjectOrProposal().equals("P")) {
				buf.append("<td nowrap=\"nowrap\"><label for=\"job\"><b>Project role:</b></label></td>");
			} else {
				buf.append("<td nowrap=\"nowrap\"><label for=\"job\"><b>Proposal role:</b></label></td>");
			}

			buf.append("<td valign=\"bottom\"><input id=\"job\" class=\"iform\" maxlength=\"64\" name=\"job\" size=\"35\" type=\"text\" value=\"\" /></td>");
			buf.append("</tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");

			buf.append("<tr>");
			buf.append("<td valign=\"top\" nowrap=\"nowrap\"><b>Access level:</b></td>");
			buf.append("<td>");
			Vector r = ETSDatabaseManager.getRolesPrivs(Project.getProjectId(), conn);
			ETSProjectInfoBean projBean = ETSUtils.getProjInfoBean(conn);
			buf.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">");

			for (int i = 0; i < r.size(); i++) {
				String[] rp = (String[]) r.elementAt(i);
				int roleid = (new Integer(rp[0])).intValue();
				String rolename = rp[1];
				//String privs = rp[2];
				String privids = rp[3];

				if (!(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.OWNER, conn))) {
					buf.append("<tr>");
					buf.append("<td align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_" + i + "\" type=\"radio\" name=\"roles\" value=\"" + roleid + "\" /></td>");
					buf.append("<td align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"role_" + i + "\">" + rolename + "</label></td>");
					buf.append("</tr>");
					buf.append("<tr>");
					buf.append("<td>&nbsp;</td>");
					//buf.append("<td align=\"left\" valign=\"top\">Privileges: "+privs+"</td>");
					buf.append("<td align=\"left\" valign=\"top\">Privileges: ");
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
					buf.append(priv_desc);
					buf.append("</td>");
					buf.append("</tr>");
					buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
				}
			}
			buf.append("</table>");

			buf.append("</td>");
			buf.append("</tr>");
			//buf.append("<tr><td colspan=\"2\" class=\"small\"><span class=\"ast\"><b>*</b></span>All fields are required</td></tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");

			buf.append("</table>");

			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
			buf.append("<tr>");
			buf.append("<td  align=\"left\">");
			buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Submit\" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\">&nbsp;</td>");
			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
			buf.append("</td> ");

			buf.append("</tr>");
			buf.append("</table> ");
			buf.append("</form> ");

		} //end of if admin
		else {
			//buf.append("not admin code here");

			buf.append("<form method=\"get\" action=\"ETSProjectsServlet.wss\" name=\"membersaddReqForm\">");
			buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
			buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"action\" value=\"addmember\" />");
			buf.append("<input type=\"hidden\" name=\"addmemberreq\" value=\"sub\" />");

			printHeader("Request add team member", "", false);

			if (!msg.equals("")) {
				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">");
				buf.append("<tr>");
				buf.append("<td  valign=\"top\" align=\"left\" >");
				buf.append("<span style=\"color:#ff3333\">" + msg + "</span>");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr><td>&nbsp;</td></tr>");
				buf.append("</table>");
			}

			buf.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\"><tr><td>");
			buf.append("Use the form below to request a user be added to this workspace. You must enter a valid user id for the requested user.  You may also include a proposed role and any additional information that might be useful to the workspace manager.  Once submitted, this information will be sent to the workspace manager.");
			buf.append("</td></tr></table><br />");

			buf.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
			buf.append("<td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
			buf.append("</tr></table><br />");

			buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"443\" border=\"0\">");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td></tr>");

			buf.append("<tr>");
			buf.append("<td nowrap=\"nowrap\"><label for=\"uid\"><span class=\"ast\"><b>*</b></span><b>Requested user id:</b></label></td>");
			buf.append("<td><input id=\"uid\" class=\"iform\" maxlength=\"32\" name=\"uid\" size=\"35\" type=\"text\" value=\"\" /></td>");
			buf.append("</tr>");

			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			buf.append("<tr>");
			if (Project.getProjectOrProposal().equals("P")) {
				buf.append("<td nowrap=\"nowrap\"><label for=\"job\"><b>Project role:</b></label></td>");
			} else {
				buf.append("<td nowrap=\"nowrap\"><label for=\"job\"><b>Proposal role:</b></label></td>");
			}
			buf.append("<td><input id=\"job\" class=\"iform\" maxlength=\"32\" name=\"job\" size=\"35\" maxsize=\"64\" type=\"text\" value=\"\" /></td>");
			buf.append("</tr>");

			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			buf.append("<tr>");
			buf.append("<td colspan=\"2\" valign=\"top\" nowrap=\"nowrap\"><label for=\"addinfo\"><b>Additional information:</b></label></td>");
			buf.append("</tr><tr><td colspan=\"2\">");
			buf.append("<textarea id=\"addinfo\" name=\"addinfo\" size=\"1000\" rows=\"9\" cols=\"35\"  class=\"iform\"  onkeyup=\"if (this.value.length>1000) { alert('Please enter less than 1000 characters'); this.value=this.value.substring(0,1000) }\" >");
			buf.append("</textarea>");
			buf.append("</td>");
			buf.append("</tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			//buf.append("<tr><td colspan=\"2\" class=\"small\"><span class=\"ast\"><b>*</b></span>Denotes required fields</td></tr>");
			//buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			buf.append("</table>");

			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
			buf.append("<tr>");
			buf.append("<td  align=\"left\">");
			buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Submit\" />&nbsp;");
			buf.append("</td> ");

			buf.append("<td  align=\"left\">&nbsp;</td>");
			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\" >");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
			buf.append("</td> ");
			buf.append("</tr>");
			buf.append("</table> ");
			buf.append("</form> ");

		} //end of else not admin
		writer.println(buf.toString());
	}

	public void membersDel(String msg) throws SQLException, IOException, Exception {
		StringBuffer buf = new StringBuffer();
		String userid = es.gIR_USERN;

		//check if user has admin priviliges
		boolean noMembr = false;
		boolean admin = ETSDatabaseManager.isProjectAdmin(userid, Project.getProjectId(), conn) || isSuperAdmin;
		if (admin) {
			printHeader("Remove team member", "", false);
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
			buf.append("<tr><td>");
	
			buf.append("<form onsubmit=\"return Validate()\" method=\"post\" action=\"removeMembr.wss\" name=\"membersdelForm\">");
			
			buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
			buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"action\" value=\"delmember\" />");
			buf.append("<input type=\"hidden\" name=\"delmember\" value=\"sub\" />");
			buf.append("<input type=\"hidden\" name=\"nextpg\" value=\"2\" />");
			buf.append("<input type=\"hidden\" name=\"linkid\" value=\"" + linkid + "\" />");
			
			buf.append("<script type=\"text/javascript\" language=\"javascript\">");
			
			buf.append("function Validate() {");
			buf.append("Message = \"\";");
			buf.append("Message = Message + CheckRmvMemb();");
			buf.append("if (Message == \"\") {");
			buf.append("return true;");
			buf.append(" } else { ");
			buf.append("alert(Message);");
			buf.append("return false; } }");
			
			buf.append("function CheckRmvMemb() {");
			buf.append("Message = \"\";");
			buf.append("if((document.getElementById(\"uid\").value) == \"\"){");
			buf.append("Message = Message + \"Please select the member you wish to remove from this workspace.\";");
			buf.append("}");
			buf.append("return Message; }");
			
			buf.append("</script>");

			if (!msg.equals("")) {
				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">");
				buf.append("<tr>");
				buf.append("<td  valign=\"top\" align=\"left\" >");
				buf.append("<span style=\"color:#ff3333\"><b><ul><li>" + msg + "</li></ul></b></span>");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr valign=\"top\">");
				buf.append("<td><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"443\" alt=\"\" /></td>");
				buf.append("</tr>");
				buf.append("</table>");
			}

			buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"443\" border=\"0\">");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			buf.append("<tr><td colspan=\"2\">Select the member you wish to remove from this workspace.</td></tr>");
			buf.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			buf.append("<tr>");
			buf.append("<td nowrap=\"nowrap\" width=\"40%\"><label for=\"uid\"><b>Remove User Id:</b></label></td>");
			buf.append("<td align=\"left\">");
			Vector members = ETSDatabaseManager.getProjMembersAll(Project.getProjectId(), true, conn);
			if (members != null) {
				StringBuffer buf2 = new StringBuffer();
				boolean size_flag = false;

				if (members.size() > 0) {
					buf2.append("<select id=\"uid\" name=\"uid\" align=\"left\">");
					buf2.append("<option value=\"\"> </option>");
					for (int i = 0; i < members.size(); i++) {
						ETSUser user = (ETSUser) members.elementAt(i);
						if (!ETSDatabaseManager.hasProjectPriv(user.getUserId(), Project.getProjectId(), Defines.OWNER, conn)) {
							size_flag = true;
							UserObject uo = AccessCntrlFuncs.getUserObject(conn, user.getUserId(), true, false);
							String username = uo.gFIRST_NAME + "&nbsp;" + uo.gLAST_NAME;
							buf2.append("<option value=\"" + user.getUserId() + "\">" + username + " [" + user.getUserId() + "]</option>");
						}
					}
					buf2.append("</select>");
					if (size_flag) {
						buf.append(buf2);
					} else {
						noMembr = true;
						buf.append("There are no memebers to remove");
					}
				} else {
					noMembr = true;
					buf.append("There are no memebers to delete");
				}
			} else {
				noMembr = true;
				buf.append("There are no memebers to remove");
			}

			buf.append("</td>");
			buf.append("</tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");

			buf.append("</table>");

			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
			buf.append("<tr>");
			if(!noMembr){
				buf.append("<td  align=\"left\">");
				buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Continue\"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				buf.append("</td> ");
				buf.append("<td  align=\"left\">&nbsp;</td>");
			}
			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
			buf.append("</td> ");
			buf.append("</tr>");
			buf.append("</table> ");
			buf.append("</form> ");

		} //end of if admin
		else {
			//buf.append("not admin code here");
			noMembr = false;
			printHeader("Request remove team member", "", false);
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			buf.append("<tr><td>");

			buf.append("<form method=\"get\" action=\"ETSProjectsServlet.wss\" name=\"membersdelReqForm\">");
			buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
			buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"action\" value=\"delmember\" />");
			buf.append("<input type=\"hidden\" name=\"delmemberreq\" value=\"sub\" />");

			if (!msg.equals("")) {
				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\">");
				buf.append("<tr>");
				buf.append("<td  valign=\"top\" align=\"left\" >");
				buf.append("<span style=\"color:#ff3333\">" + msg + "</span>");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("<tr><td>&nbsp;</td></tr>");
				buf.append("</table>");
			}

			buf.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\"><tr><td>");
			buf.append("Select the member you wish to request be removed from this workspace. Once submitted, this information will be sent to the workspace owner. Please include any additional comments that might be useful to the workspace owner.");
			buf.append("</td></tr></table><br />");

			buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"443\" border=\"0\">");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td></tr>");

			buf.append("<tr>");
			buf.append("<td nowrap=\"nowrap\" width=\"200\"><label for=\"uid\"><b>Requested removed User Id:</b></label></td>");
			buf.append("<td  width=\"400\" align=\"left\">");
			Vector members = ETSDatabaseManager.getProjMembers(Project.getProjectId(), true, conn);
			if (members != null) {
				StringBuffer buf2 = new StringBuffer();
				boolean size_flag = false;

				if (members.size() > 0) {
					buf2.append("<select id=\"uid\" name=\"uid\" size=\"\" align=\"left\">");
					buf2.append("<option value=\"\"> </option>");
					for (int i = 0; i < members.size(); i++) {
						ETSUser user = (ETSUser) members.elementAt(i);
						if (!ETSDatabaseManager.hasProjectPriv(user.getUserId(), Project.getProjectId(), Defines.OWNER, conn)) {
							size_flag = true;
							UserObject uo = AccessCntrlFuncs.getUserObject(conn, user.getUserId(), true, false);
							buf2.append("<option value=\"" + user.getUserId() + "\">" + uo.gUSER_FULLNAME + " [" + user.getUserId() + "]</option>");
						}
					}
					buf2.append("</select>");

					if (size_flag) {
						buf.append(buf2);
					} else {
						noMembr = true;
						buf.append("There are no memebers to remove");
					}
				} else {
					noMembr = true;
					buf.append("There are no memebers to remove");
				}
			} else {
				noMembr = true;
				buf.append("There are no memebers to remove");
			}

			buf.append("</td>");
			buf.append("</tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
			buf.append("<tr>");
			buf.append("<td colspan=\"2\" valign=\"top\" nowrap=\"nowrap\"><label for=\"addinfo\"><b>Additional Information:</b></label></td>");
			buf.append("</tr><tr><td colspan=\"2\">");
			buf.append("<textarea id=\"addinfo\" name=\"addinfo\" size=\"1000\" rows=\"5\" cols=\"50\"  onkeyup=\"if (this.value.length>1000) { alert('Please enter less than 1000 characters'); this.value=this.value.substring(0,1000) }\" >");
			buf.append("</textarea>");
			buf.append("</td>");
			buf.append("</tr>");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
			buf.append("</table>");

			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
			buf.append("<tr>");
			if(!noMembr){
				buf.append("<td  align=\"left\">");
				buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Submit\" />&nbsp;");
				buf.append("</td> ");
				buf.append("<td  align=\"left\">&nbsp;</td>");
			}
			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
			buf.append("</td> ");

			buf.append("</tr>");
			buf.append("</table> ");
			buf.append("</form> ");

		} //end of else not admin

		writer.println(buf.toString());
	}

	private void membersManage(AccessCntrlFuncs acf) throws Exception {
		Vector members = new Vector();
		StringBuffer buf = new StringBuffer();
		boolean gray_flag = true;
		boolean wso_flag = false;
		boolean wsm_flag = false;
		/*
		int width_name =153;
		int width_job =143;
		int width_role =124;
		int width_edit =23;
		*/

		int width_name = 225;
		int width_job = 200;
		int width_role = 150;
		int width_edit = 25;

		String sortby = request.getParameter("sort_by");
		String ad = request.getParameter("sort");

		if (sortby == null) {
			sortby = Defines.SORT_BY_USERNAME_STR;
		}
		if (ad == null) {
			ad = Defines.SORT_ASC_STR;
		}

		printHeader("Manage members", "", false);
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		buf.append("<tr><td>");

		if (!(ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.ADMIN, conn) || ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.MANAGE_USERS, conn) || isSuperAdmin)) {
			buf.append("You are not authorized to perform this action");
			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
			buf.append("<tr>");
			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
			buf.append("</td> ");
			buf.append("</tr>");
			buf.append("</table> ");
			writer.println(buf.toString());
			return;
		}

		try {
			members = ETSDatabaseManager.getProjMembers(Project.getProjectId(), sortby, ad, true, true, conn);
		} catch (Exception e) {
			logger.error("ETSAdminServlet:managemembers() error =", e);
			e.printStackTrace();
		}

		if (members == null) {

			buf.append("There are no project members at this time - null");
		} else {
			if (members.size() <= 0) {
				buf.append("There are no project members at this time");
			} else {
				/*
				if (sortby.equals(Defines.SORT_BY_ACCLEV_STR)){
					byte sortOrder = ETSComparator.getSortOrder(sortby);
					byte sortAD = ETSComparator.getSortBy(ad);
					Collections.sort(members,new ETSComparator(sortOrder,sortAD));
				}
				*/

				String po = "Project role";
				if (!Project.getProjectOrProposal().equals("P")) {
					po = "Proposal role";
				}

				buf.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
				/*
				buf.append("<tr>");
				buf.append("<th id=\"man_name\" class=\"small\" height=\"17\" align=\"left\" valign=\"bottom\">Name</th>");

				if (Project.getProjectOrProposal().equals("P")){
					buf.append("<th id=\"man_job\" class=\"small\" height=\"17\" align=\"left\" valign=\"bottom\">Project role</th>");
				}
				else{
					buf.append("<th id=\"man_job\" class=\"small\" height=\"17\" align=\"left\" valign=\"bottom\">Proposal role</th>");
				}

				buf.append("<th id=\"man_role\" class=\"small\" height=\"17\" align=\"left\" valign=\"bottom\">Access level</th>");

				buf.append("<th id=\"man_edit\" class=\"small\" height=\"17\" align=\"left\" valign=\"bottom\">&nbsp;</th>");

				buf.append("</tr>");
				*/

				buf.append("<tr><td colspan=\"4\" class=\"small\">Click on the column heading to sort</td></tr>\n");
				buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");

				buf.append("<tr><th id=\"man_name\" align=\"left\" valign=\"bottom\" height=\"16\">");
				//sort by name
				if (sortby.equals(Defines.SORT_BY_USERNAME_STR)) {
					if (ad.equals(Defines.SORT_ASC_STR)) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_USERNAME_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
						buf.append("Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
						buf.append("</table></th>");
					} else {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_USERNAME_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
						buf.append("Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
						buf.append("</table></th>");
					}
				} else {
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_USERNAME_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
					buf.append("Name</a></th>");
				}

				buf.append("<th id=\"man_job\" align=\"left\" valign=\"bottom\" height=\"16\">");
				if (sortby.equals(Defines.SORT_BY_PROJROLE_STR)) {
					if (ad.equals(Defines.SORT_ASC_STR)) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_PROJROLE_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
						buf.append(po + "</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
						buf.append("</table></th>");
					} else {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_PROJROLE_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
						buf.append(po + "</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
						buf.append("</table></th>");
					}
				} else {
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_PROJROLE_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
					buf.append(po + "</a></th>");
				}

				buf.append("<th id=\"man_role\" align=\"left\" valign=\"bottom\" height=\"16\">");
				if (sortby.equals(Defines.SORT_BY_ACCLEV_STR)) {
					if (ad.equals(Defines.SORT_ASC_STR)) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_ACCLEV_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
						buf.append("Access level</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
						buf.append("</table></th>");
					} else {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_ACCLEV_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
						buf.append("Access level</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
						buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
						buf.append("</table></th>");
					}
				} else {
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=managemember&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_ACCLEV_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
					buf.append("Access level</a></th>");
				}

				buf.append("<th id=\"man_edit\" class=\"small\" height=\"16\" align=\"left\" valign=\"bottom\">&nbsp;</th>");

				buf.append("</tr>");

				//buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td></tr>");

				for (int i = 0; i < members.size(); i++) {
					//get amt information
					ETSUser memb = (ETSUser) members.elementAt(i);

					try {
						//UserObject uo = AccessCntrlFuncs.getUserObject(conn,memb.getUserId(),true,false);
						if (gray_flag) {
							buf.append("<tr style=\"background-color:#eeeeee\">");
							gray_flag = false;
						} else {
							buf.append("<tr>");
							gray_flag = true;
						}
						String ast = "";
						//System.out.println("pc="+memb.getPrimaryContact());

						try {
							//if (memb.getPrimaryContact()==Defines.YES){
							if (ETSDatabaseManager.hasProjectPriv(memb.getUserId(), Project.getProjectId(), Defines.OWNER, conn)) {
								wso_flag = true;
								ast = "<span class=\"ast\"><b>**</b></span>";
							} else if (ETSDatabaseManager.hasProjectPriv(memb.getUserId(), Project.getProjectId(), Defines.ADMIN, conn)) {
								wsm_flag = true;
								ast = "<span class=\"ast\"><b>*</b></span>";
							}
						} catch (Exception e) {
							logger.error("ETSAdminServlet:memebersList() hasProjectPriv error =", e);
							e.printStackTrace();
						}

						buf.append("<td headers=\"man_name\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\"" + width_name + "\">");
						String username = memb.getUserName(); //uo.gFIRST_NAME +"&nbsp;"+uo.gLAST_NAME;
						buf.append(username + ast);
						buf.append("</td>");

						buf.append("<td headers=\"man_job\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\"" + width_job + "\">");
						buf.append(memb.getUserJob());
						buf.append("</td>");
						/*
									String[] r = ETSDatabaseManager.getUserRole(memb.getUserId(),Project.getProjectId(),conn);
									//int roleid = (new Integer(r[0])).intValue();
									String rolename = r[1];
									//String privs = r[2];
						*/
						String rolename = memb.getRoleName();

						buf.append("<td headers=\"man_role\" nowrap=\"nowrap\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\"" + width_role + "\">");
						buf.append(rolename);
						buf.append("</td>");

						buf.append("<td headers=\"man_edit\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\"" + width_edit + "\">");
						buf.append(
							"<a href=\"ETSContentManagerServlet.wss?action=editMemberRole&proj="
								+ Project.getProjectId()
								+ "&tc="
								+ TopCatId
								+ "&cc="
								+ TopCatId
								+ "&edit_userid="
								+ memb.getUserId()
								+ "\" target=\"new\" onclick=\"window.open('ETSContentManagerServlet.wss?action=editMemberRole&proj="
								+ Project.getProjectId()
								+ "&tc="
								+ TopCatId
								+ "&cc="
								+ TopCatId
								+ "&edit_userid="
								+ memb.getUserId()
								+ "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=512,height=512,top=200');return false;\" onkeypress=\"window.open('ETSContentManagerServlet.wss?action=editMemberRole&proj="
								+ Project.getProjectId()
								+ "&tc="
								+ TopCatId
								+ "&cc="
								+ TopCatId
								+ "&edit_userid="
								+ memb.getUserId()
								+ "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=512,height=512,top=200');return false;\">Edit</a>");

						buf.append("</tr>");

					} /*
																												catch(AMTException ae){
																													buf.append("<tr>");
																													buf.append("<td headers=\"man_name\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\""+width_name+"\"> name not found </td>");  //full name
																													buf.append("<td headers=\"man_job\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\""+width_job+"\"> job not found </td>"); //job resp
																													buf.append("<td headers=\"man_role\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\""+width_role+"\"> access level not found </td>"); //role/priv
																													buf.append("<td headers=\"man_edit\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\""+width_edit+"\"> can not edit </td>");  //edit
																													buf.append("</tr>");
																												}*/
					catch (Exception se) {
						buf.append("<tr>");
						buf.append("<td headers=\"man_name\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\"" + width_name + "\"> name not found </td>"); //full name
						buf.append("<td headers=\"man_job\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\"" + width_job + "\"> job not found </td>"); //job resp
						buf.append("<td headers=\"man_role\" height=\"17\"class=\"small\" align=\"left\" valign=\"top\" width=\"" + width_role + "\"> access level not found </td>"); //role/priv
						buf.append("<td headers=\"man_edit\" height=\"17\" class=\"small\" align=\"left\" valign=\"top\" width=\"" + width_edit + "\"> can not edit </td>"); //edit
						buf.append("</tr>");
					}
					//buf.append("<tr><td colspan=\"3\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"443\" height=\"1\" alt=\"\" /></td></tr>");
				}

				if (wsm_flag || wso_flag) {
					buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
					buf.append("<tr><td colspan=\"4\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
					if (wso_flag) {
						buf.append("<tr><td  width=\"1%\" height=\"21\" class=\"small\" align=\"right\" valign=\"bottom\"><span class=\"ast\"><b>**</b></span></td>");
						buf.append("<td  width=\"99%\" height=\"21\" class=\"small\" align=\"left\" valign=\"bottom\">Denotes workspace owner</td></tr>");
					}

					if (wsm_flag) {
						buf.append("<tr><td width=\"1%\" height=\"21\" class=\"small\" align=\"right\" valign=\"bottom\"><span class=\"ast\"><b>*</b></span></td>");
						buf.append("<td  width=\"99%\" height=\"21\" class=\"small\" align=\"left\" valign=\"bottom\">Denotes workspace manager</td></tr>");
					}
					buf.append("</table></td></tr>");
				}

				buf.append("<tr><td colspan=\"4\" height=\"17\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"600\" height=\"1\" alt=\"\" /></td></tr>");
				buf.append("</table>");

				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
				buf.append("<tr>");
				buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
				buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
				buf.append("</td> ");
				buf.append("<td  align=\"left\" valign=\"top\" width=\"427\">");
				buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
				buf.append("</td> ");
				buf.append("</tr>");
				buf.append("</table> ");

				writer.println(buf.toString());
			}
		}

	}

	private void doMemberDetails(AccessCntrlFuncs acf) throws Exception {
		StringBuffer buf = new StringBuffer();
		String userid = getParameter(request, "uid");
		ETSUser member;
		String mID = "";
		boolean bTeamroomWrkspc = false;
		//printHeader("Team member details",false);

		try {
			//gutter between content and right column
			writer.println("<td rowspan=\"2\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			// Right column start
			writer.println("<td rowspan=\"2\" width=\"150\" valign=\"top\">");
			ETSContact contact = new ETSContact(Project.getProjectId(), request);
			contact.printContactBox(writer);
			writer.println("</td></tr>");
		} catch (Exception e) {

			logger.error("execption", e);
			e.printStackTrace();

		}
		if (Project.getIsPrivate().equals(Defines.AIC_IS_PRIVATE_TEAMROOM) || Project.getIsPrivate().equals(Defines.AIC_IS_RESTRICTED_TEAMROOM)) {
			bTeamroomWrkspc = true;
		}
		StringBuffer b = new StringBuffer();

		member = ETSDatabaseManager.getETSUserAll(userid, Project.getProjectId(), conn);

		try {
			b.append("<tr><td valign=\"top\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"><tr>");

			if (ETSUtils.isUserPhotoAvailable(conn, member.getUserId())) {
				b.append("<td valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSUserPhotoServlet.wss?userid=" + member.getUserId() + "\" width=\"115\" height=\"115\" alt=\"photo\" /></td>\n");
				b.append("<td valign=\"top\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"10\" alt=\"\" /></td>\n");
				b.append("<td valign=\"top\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"318\"><tr>");
			} else {
				b.append("<td valign=\"top\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"><tr>");
			}

			UserObject uo = AccessCntrlFuncs.getUserObject(conn, member.getUserId(), true, false);
			if (uo != null) {
				b.append("<td align=\"left\"><span class=\"title\">" + uo.gUSER_FULLNAME + "</span></td></tr>");
				b.append("<tr><td><span class=\"subtitle\">" + member.getUserId() + "</span></td></tr>");
			} else {
				b.append("<td class=\"title\">" + member.getUserId() + "</td>");
				b.append("<tr><td>&nbsp;</td></tr>");
			}
			if (es.gIR_USERN.equals(member.getUserId())) {
				if (ETSUtils.isUserPhotoAvailable(conn, member.getUserId())) {
					b.append("<tr><td class=\"small\"><a href=\"ETSProjectsServlet.wss?action=addmemph&uid=" + member.getUserId() + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\">Update your photo</a>");
					b.append("&nbsp;&nbsp;<a href=\"ETSContentManagerServlet.wss?action=delmemph&uid=" + member.getUserId() + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\">Delete your photo</a></td></tr>\n");
				} else {
					b.append("<tr><td class=\"small\"><a href=\"ETSProjectsServlet.wss?action=addmemph&uid=" + member.getUserId() + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\">Add your photo</a></td></tr>\n");
				}

			} else {
				b.append("<tr><td class=\"small\">&nbsp;</td></tr>");
			}
			b.append("</table>");
			b.append("</tr></td></table>");

			b.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>\n");
			b.append("</table>\n");
			writer.println(b.toString());
			// end of header ///////////////////////////////////////////////////////////////

			int width1 = 135;
			int width2 = 465;
			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"><tr><td>\n");

			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n");
			buf.append("<tr><td class=\"subtitle\" nowrap=\"nowrap\" colspan=\"2\"><span style=\"color:#ff3333\"><b>IBM Confidential</b></span></td></tr>");

			//if(Project.getProjectOrProposal().equals("P")){
			buf.append("<tr><td class=\"subtitle\" nowrap=\"nowrap\">Workspace profile</td>");
			//}
			//else{
			//buf.append("<tr><td class=\"subtitle\" nowrap=\"nowrap\">Proposal profile</td>");
			//}
			if (!bTeamroomWrkspc) {
				if (es.gIR_USERN.equals(member.getUserId()) || ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.ADMIN, conn) || ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.MANAGE_USERS, conn) || isSuperAdmin) {
					buf.append("<td align=\"right\">");
					buf.append(
						"<span class=\"small\"><a href=\"ETSContentManagerServlet.wss?action=editMemberRole&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ TopCatId
						+ "&edit_userid="
						+ member.getUserId()
						+ "\" target=\"new\" onclick=\"window.open('ETSContentManagerServlet.wss?action=editMemberRole&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ TopCatId
						+ "&edit_userid="
						+ member.getUserId()
						+ "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=512,height=512,top=200');return false;\" onkeypress=\"window.open('ETSContentManagerServlet.wss?action=editMemberRole&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ TopCatId
						+ "&edit_userid="
						+ member.getUserId()
						+ "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=512,height=512,top=200');return false;\">");
					if (es.gIR_USERN.equals(member.getUserId())) {
						//if(Project.getProjectOrProposal().equals("P")){
						buf.append("Manage your workspace profile</a></span>");
					//}
					//else{
					//    buf.append("Manage your proposal profile</a></span>");
					//}
					} else {
					//if(Project.getProjectOrProposal().equals("P")){
						buf.append("Manage this workspace profile</a></span>");
					//}
					//else{
					//    buf.append("Manage this proposal profile</a></span>");
					//}
					}
					buf.append("</td>");
				}
				else {
					buf.append("<td>&nbsp;</td>");
				}
			} else {
				buf.append("<td>&nbsp;</td>");
			}
			buf.append("</tr>\n");
			buf.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>\n");

			//Role
			String[] r = ETSDatabaseManager.getUserRole(member.getUserId(), Project.getProjectId(), conn);
			//int roleid = (new Integer(r[0])).intValue();
			String rolename = r[1];
			//String privs = r[2];
			buf.append("<tr>");
			buf.append("<td width=\"" + width1 + "\"><b>Access level: </b></td>");

			buf.append("<td width=\"" + width2 + "\" align=\"left\">" + rolename + "</td>");
			buf.append("</tr>\n");

			//job responsibility
			buf.append("<tr>");
			buf.append("<td width=\"" + width1 + "\" valign=\"bottom\"><b>Workspace role: </b></td>");
			buf.append("<td width=\"" + width2 + "\" align=\"left\" valign=\"bottom\">" + member.getUserJob() + "</td>");
			buf.append("</tr>\n");

			//Add Member of the groups
			TeamGroupDAO teamgrpDao = new TeamGroupDAO();
			teamgrpDao.prepare();
			String strGrp = teamgrpDao.getGrpListStringForUser(Project.getProjectId(), member.getUserId(), conn);
			teamgrpDao.cleanup();
			if (strGrp.equals("")) {
				strGrp = "None";
			}

			buf.append("<tr>");
			buf.append("<td width=\"" + width1 + "\" valign=\"bottom\"><b>Member of group(s): </b></td>");

			buf.append("<td width=\"" + width2 + "\" align=\"left\" valign=\"bottom\">" + strGrp.trim() + "</td>");
			buf.append("</tr>\n");

			mID = ETSDatabaseManager.getUserMessengerID(userid);

			buf.append("<tr>");
			buf.append("<td width=\"" + width1 + "\" valign=\"bottom\"><b>Instant message ID: </b></td>");

			buf.append("<td width=\"" + width2 + "\" align=\"left\" valign=\"bottom\">" + mID.trim() + "</td>");
			buf.append("</tr>\n");

			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"25\" width=\"1\" alt=\"\" /></td></tr>\n");

			//user information
			buf.append("<tr><td class=\"subtitle\">User information</td>");
			if (es.gIR_USERN.equals(member.getUserId())) {
				buf.append("<td align=\"right\">&nbsp;<span class=\"small\"><a href=\"ETSProjectsServlet.wss?action=editmeminfo&uid=" + member.getUserId() + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "\">Manage your user information</a></span>");
			} else {
				buf.append("<td>&nbsp;</td>");
			}
			buf.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td width=\"" + width1 + "\" align=\"left\" valign=\"top\"><b>Skills:<b></td>");
			buf.append("<td width=\"" + width2 + "\" align=\"left\" valign=\"top\">" + ETSDatabaseManager.getUserSkills(member.getUserId()) + "</td></tr>");
			buf.append("<tr><td width=\"" + width1 + "\" align=\"left\" valign=\"top\"><b>CV:<b></td>");
			if (ETSUtils.isUserCVAvailable(conn, member.getUserId())) {

				buf.append("<td width=\"" + width2 + "\" align=\"left\" valign=\"top\"><a href=\"ETSCVDeliveryServlet.wss?projid=" + Project.getProjectId() + "&uid=" + member.getUserId() + "&linkid=" + linkid + "\" target=\"new\" class=\"fbox\">View user CV</a></td></tr>");
			} else {
				buf.append("<td width=\"" + width2 + "\" align=\"left\" valign=\"top\">Not available</td></tr>");
			}

			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"25\" width=\"1\" alt=\"\" /></td></tr>\n");

			//user profile
			String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, member.getUserId());
			String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
			buf.append("<tr><td class=\"subtitle\" />User profile</td>");
			if (decaftype.equals("E") && es.gIR_USERN.equals(member.getUserId())) {
				String ir_url = ETSUtils.getIRProfileURL(conn);
				buf.append(
					"<td align=\"right\">&nbsp;<span class=\"small\"><a href=\""
						+ ir_url
						+ "\" target=\"new\" onclick=\"window.open('"
						+ ir_url
						+ "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=800,height=600,top=200');return false;\" onkeypress=\"window.open('"
						+ ir_url
						+ "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=800,height=600,top=200');return false;\">Manage your user profile</a></span>");
			} else {
				buf.append("<td>&nbsp;</td>");
			}
			buf.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>\n");

			//if (decaftype.equals("I") && es.gIR_USERN.equals(member.getUserId())){
			//	buf.append("<tr><td colspan=\"2\" class=\"small\">*Access IBM Bluepages to edit your user profile.</td></tr>\n");
			//}

			//company
			buf.append("<tr>\n");
			if (uo != null) {
				//String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,member.getUserId());
				//String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
				if (decaftype.equals("I")) {
					buf.append("<td width=\"" + width1 + "\"><b>Company: </b></td>");
					buf.append("<td width=\"" + width2 + "\" align=\"left\">IBM</td>");
				} else {
					if (uo.gUSER_COMPANY != null) {
						buf.append("<td width=\"" + width1 + "\"><b>Company: </b></td>");
						buf.append("<td width=\"" + width2 + "\" align=\"left\">" + uo.gUSER_COMPANY + "</td>");
					} else {
						buf.append("<td width=\"" + width1 + "\"><b>Company: </b></td>");
						buf.append("<td width=\"" + width2 + "\" align=\"left\">not found </td>");
					}
				}
			} else {
				buf.append("<td width=\"" + width1 + "\"><b>Company: </b> </td>");
				buf.append("<td width=\"" + width2 + "\" align=\"left\">not found </td>");
			}
			buf.append("</tr>\n");

			if (uo != null) {
				//buf.append("<tr>");
				//buf.append("<td colspan=\"2\" class=\"subtitle\">Company address</td>");
				//buf.append("</tr>\n");

				buf.append("<tr>");
				buf.append("<td width=\"" + width1 + "\"><b>Street and no: </b></td>");
				if (uo.gCOMPANY_ADDR1.equals("")) {
					buf.append("<td width=\"" + width2 + "\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
				} else {
					buf.append("<td width=\"" + width2 + "\" colspan=\"2\">" + uo.gCOMPANY_ADDR1 + "</td>");
				}
				buf.append("</tr>\n");

				buf.append("<tr>");
				buf.append("<td width=\"" + width1 + "\"><b>City:</b></td>");
				buf.append("<td width=\"" + width2 + "\">");
				if (uo.gCOMPANY_CITY.equals("")) {
					buf.append("<img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" />");
				} else {
					buf.append("" + uo.gCOMPANY_CITY);
				}
				buf.append("</td></tr>\n");

				buf.append("<tr>");
				buf.append("<td width=\"" + width1 + "\"><b>State:</b></td>");
				buf.append("<td width=\"" + width2 + "\">");
				if (uo.gCOMPANY_STATECODE.equals("")) {
					buf.append("<img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" />");
				} else {
					buf.append(uo.gCOMPANY_STATECODE);
				}
				buf.append("</td></tr>\n");

				buf.append("<tr>");
				buf.append("<td width=\"" + width1 + "\"><b>Zip:</b></td>");
				buf.append("<td width=\"" + width2 + "\">");
				if (uo.gCOMPANY_POSTCODE.equals("")) {
					buf.append("<img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" />");
				} else {
					buf.append(uo.gCOMPANY_POSTCODE);
				}
				buf.append("</td></tr>\n");

				buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");

				buf.append("<tr>");
				buf.append("<td width=\"" + width1 + "\"><b>Email:</b></td>");
				if (uo.gEMAIL.equals("")) {
					buf.append("<td width=\"" + width2 + "\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
				} else {
					buf.append("<td width=\"" + width2 + "\" align=\"left\">" + uo.gEMAIL + "</td>");
				}
				buf.append("</tr>\n");

				buf.append("<tr>");
				buf.append("<td width=\"" + width1 + "\"><b>Phone:</b></td>");
				if (uo.gPHONE.equals("")) {
					buf.append("<td width=\"" + width2 + "\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
				} else {
					buf.append("<td width=\"" + width2 + "\" align=\"left\">" + uo.gPHONE + "</td>");
				}
				buf.append("</tr>\n");

				buf.append("<tr>");
				buf.append("<td width=\"" + width1 + "\"><b>Mobile:</b></td>");
				if (uo.gCELL_PHONE.equals("")) {
					buf.append("<td width=\"" + width2 + "\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
				} else {
					buf.append("<td width=\"" + width2 + "\" align=\"left\">" + uo.gCELL_PHONE + "</td>");
				}
				buf.append("</tr>\n");

				buf.append("<td width=\"" + width1 + "\"><b>Fax:</b></td>");
				if (uo.gFAX.equals("")) {
					buf.append("<td width=\"" + width2 + "\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
				} else {
					buf.append("<td width=\"" + width2 + "\" align=\"left\">" + uo.gFAX + "</td>");
				}
				buf.append("</tr>\n");

				if (decaftype.equals("I") && es.gIR_USERN.equals(member.getUserId())) {
					buf.append("<tr><td colspan=\"2\" class=\"small\">*Access IBM Bluepages to edit your user profile.</td></tr>\n");
				}
				buf.append("</table>\n");

				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n");
				buf.append("<tr><td class=\"small\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
				//buf.append("<tr><td class=\"small\"><span class=\"ast\"><b>*</b></span>Denotes attributes from Customer Connect profile</td></tr>\n");
				buf.append("</table>\n");

			} else { //uo == null
				//buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">\n");
				buf.append("<tr>");
				buf.append("<td colspan=\"2\">User infomation not found</td>");
				buf.append("</tr>\n");
				buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
				buf.append("</table>\n");
			}
		} catch (Exception e) {
			logger.error("ETSAdminServlet:memberDetails getUser error =", e);
			e.printStackTrace();
			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">\n");
			buf.append("<tr>");
			buf.append("<td align=\"left\">Unable to locate user " + userid + " in this project.</td>");
			buf.append("</tr>\n");
			//buf.append("<tr><td class=\"small\"><span class=\"ast\"><b>*</b></span>Denotes attributes from Customer Connect profile</td></tr>\n");
			buf.append("</table>\n");
		}

		buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n");
		//buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>\n");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("</table>");

		buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">\n");

		buf.append("<tr>");
		buf.append("<td align=\"right\" valign=\"top\" width=\"16\">");
		buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
		buf.append("</td>");
		buf.append("<td align=\"left\" valign=\"top\">");
		buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
		buf.append("</td>\n");

		buf.append("<td align=\"right\" valign=\"top\" width=\"16\">");
		if (member.getActiveFlag().equals("A")) {
			buf.append("<a href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + userid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + userid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">View member created documents</a>");
		} else {
			buf.append("&nbsp;");
		}
		buf.append("</td></tr>\n");

		buf.append("</table>\n");

		writer.println(buf.toString());

	}

	private void doMemberDocuments(AccessCntrlFuncs acf) throws Exception {
		StringBuffer buf = new StringBuffer();
		String userid = getParameter(request, "uid");
		ETSUser member;
		boolean gray_flag = true;
		boolean childFlag = false;
		boolean internal = false;

		boolean ibmonlyFlag = false;
		boolean docExpired = false;
		boolean ibmconfFlag = false;
		boolean resFlag = false;

		int width_name = 384;
		int width_mod = 150;
		int width_type = 50;

		try {
			String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, es.gIR_USERN);
			String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
			if (decaftype.equalsIgnoreCase("I")) {
				internal = true;
			}
			int iTopCatID = ETSDatabaseManager.getTopCatId(Project.getProjectId(), Defines.DOCUMENTS_VT, conn);
			member = ETSDatabaseManager.getETSUser(userid, Project.getProjectId(), conn);
			String sortby = request.getParameter("sort_by");
			String ad = request.getParameter("sort");

			if (sortby == null) {
				sortby = Defines.SORT_BY_DATE_STR;
			}
			if (ad == null) {
				ad = Defines.SORT_DES_STR;
			}

			UserObject uo = AccessCntrlFuncs.getUserObject(conn, member.getUserId(), true, false);
			String titlestr = "";
			if (uo != null) {
				titlestr = uo.gUSER_FULLNAME + "  [" + member.getUserId() + "]";
			} else {
				titlestr = member.getUserId();
			}

			printHeader("Team member documents", titlestr, false);
			writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			writer.println("<tr><td>");

			buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			boolean adminauth = false;
			if (isSuperAdmin || isExecutive || userRole.equals(Defines.WORKSPACE_OWNER) || (member.getUserId().equals(es.gIR_USERN)))
				adminauth = true;

			Vector docs = ETSDatabaseManager.getDocsByAuthor(member.getUserId(), es.gIR_USERN, Project.getProjectId(), sortby, ad, adminauth, conn);

			if (docs.size() > 0) {
				if (sortby.equals(Defines.SORT_BY_TYPE_STR)) {
					byte sortOrder = ETSComparator.getSortOrder(sortby);
					byte sortAD = ETSComparator.getSortBy(ad);
					Collections.sort(docs, new ETSComparator(sortOrder, sortAD));
				}
				buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");

				for (int i = 0; i < docs.size(); i++) {
					ETSDoc doc = (ETSDoc) docs.elementAt(i);
					if ((doc.isIbmOnlyOrConf() && internal) || (!doc.isIbmOnlyOrConf())) {
						if ((!doc.hasExpired()) || (userRole == Defines.WORKSPACE_OWNER) || doc.getUserId().equals(es.gIR_USERN) || isSuperAdmin) {
							if (!childFlag) {
								buf.append("<tr><td colspan=\"4\" class=\"small\">Click on the column heading to sort</td></tr>\n");
								buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");

								buf.append(printDocTableHeader(member.getUserId(), sortby, ad));
								buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
								childFlag = true;
							}

							if (gray_flag) {
								buf.append("<tr style=\"background-color:#eeeeee\">");
								gray_flag = false;
							} else {
								buf.append("<tr>");
								gray_flag = true;
							}

							buf.append("<td width=\"16\" height=\"21\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>"); //img

							String expStr = "";
							String ibmStr = "";
							String resStr = "";

							if (doc.getIbmOnly() == Defines.ETS_IBM_ONLY) {
								ibmonlyFlag = true;
								ibmStr = "<span class=\"ast\">*</span>";
							} else if (doc.getIbmOnly() == Defines.ETS_IBM_CONF) {
								ibmconfFlag = true;
								ibmStr = "<span class=\"ast\">**</span>";
							}

							if (doc.hasExpired()) {
								docExpired = true;
								expStr = "<span class=\"small\"><span class=\"ast\"><b>&#8224;</b></span></span>";
							}

							if (doc.IsDPrivate()) {
								resFlag = true;
								resStr = "<span class=\"ast\">#</span>";
							}
							buf.append("<td headers=\"memb_doc_name\" height=\"21\" align=\"left\" valign=\"top\" width=\"" + width_name + "\"><a href=\"displayDocumentDetails.wss?proj=" + Project.getProjectId() + "&tc=" + iTopCatID + "&cc=" + doc.getCatId() + "&docid=" + doc.getId() + "&linkid=" + linkid + "\" class=\"fbox\">" + doc.getName() + "</a>" + ibmStr + resStr + expStr + "</td>");
							//filename
							SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
							java.util.Date date = new java.util.Date(doc.getUploadDate());
							buf.append("<td headers=\"memb_doc_date\" height=\"21\" align=\"left\" valign=\"top\" width=\"" + width_mod + "\">" + df.format(date) + "</td>"); //date
							buf.append("<td headers=\"memb_doc_type\" height=\"21\" class=\"small\" align=\"left\" valign=\"top\" width=\"" + width_type + "\">"/* + doc.getFileType() */+ "&nbsp;</td>"); //format
							buf.append("</tr>");
						}
					}
				}
			}

			if (!childFlag) {
				buf.append("<tr><td colspan=\"4\" align=\"left\">Member has not created any documents.</td></tr>");
			}

			if (ibmonlyFlag) {
				buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append("<tr><td colspan=\"4\" class=\"small\" valign=\"bottom\"><span class=\"ast\">*</span>Denotes IBM Only document</td></tr>");
			}
			if (ibmconfFlag) {
				buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append("<tr><td colspan=\"4\" class=\"small\" valign=\"bottom\"><span class=\"ast\">**</span>Denotes permanent IBM Only document</td></tr>");
			}
			if (resFlag) {
				buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append("<tr><td colspan=\"4\" class=\"small\" valign=\"bottom\"><span class=\"ast\">#</span>Access to this document is restricted to selected team members</td></tr>");
			}
			if (docExpired) {
				buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append("<tr><td colspan=\"4\" class=\"small\" valign=\"bottom\"><span class=\"ast\"><b>&#8224;</b></span>Denotes expired document</td></tr>");
			}

			buf.append("</table>");
		} catch (Exception e) {
			logger.error("ETSAdminServlet:memberDetails getUser error =", e);
			e.printStackTrace();
			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
			buf.append("<tr>");
			buf.append("<td align=\"left\">Unable to locate user " + userid + " in this project.</td>");
			buf.append("</tr>");
			buf.append("</table>");
		}

		buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("</table>");

		buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		buf.append("<tr>");
		buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
		buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back team listing\" /></a>&nbsp;");
		buf.append("</td> ");
		buf.append("<td  align=\"left\" valign=\"top\">");
		buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
		buf.append("</td>");

		buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
		buf.append("<a href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + userid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back to member details\" /></a>&nbsp;");
		buf.append("</td> ");
		buf.append("<td  align=\"left\" valign=\"top\">");
		buf.append("<a href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + userid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team member details</a>");
		buf.append("</td></tr>");

		buf.append("</table>");

		writer.println(buf.toString());

	}
	private String printDocTableHeader(String uid, String sortby, String ad) {
		StringBuffer buf = new StringBuffer();

		buf.append("<tr><th id=\"mem_doc_name\" colspan=\"2\" align=\"left\" valign=\"middle\" height=\"16\">");
		//sort by name
		if (sortby.equals(Defines.SORT_BY_NAME_STR)) {
			if (ad.equals(Defines.SORT_ASC_STR)) {
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + uid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_NAME_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
				buf.append("Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
				buf.append("</table></th>");
			} else {
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + uid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_NAME_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
				buf.append("Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
				buf.append("</table></th>");
			}
		} else {
			buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + uid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_NAME_STR + "&sort=" + Defines.SORT_ASC_STR + "\">");
			buf.append("Name</a></th>");
		}

		//sort by date
		buf.append("<th id=\"mem_doc_date\" align=\"left\" valign=\"middle\">");
		if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
			if (ad.equals(Defines.SORT_ASC_STR)) {
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + uid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_DATE_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
				buf.append("Modified</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
				buf.append("</table></th>");
			} else {
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + uid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_DATE_STR + "&sort=" + Defines.SORT_ASC_STR + "\" class=\"fbox\">");
				buf.append("Modified</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
				buf.append("</table></th>");
			}
		} else {
			buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + uid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_DATE_STR + "&sort=" + Defines.SORT_ASC_STR + "\" class=\"fbox\">");
			buf.append("Modified</a></th>");
		}

		//sort by type
		buf.append("<th id=\"mem_doc_type\" align=\"left\" valign=\"middle\">");
		if (sortby.equals(Defines.SORT_BY_TYPE_STR)) {
			if (ad.equals(Defines.SORT_ASC_STR)) {
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
//				buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + uid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_TYPE_STR + "&sort=" + Defines.SORT_DES_STR + "\" class=\"fbox\">");
//				buf.append("Type</a>");
				buf.append("</th><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
				buf.append("</table></th>");
			} else {
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
//				buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + uid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_TYPE_STR + "&sort=" + Defines.SORT_ASC_STR + "\" class=\"fbox\">");
//				buf.append("Type</a>");
				buf.append("</th><th align=\"left\" valign=\"middle\" height=\"16\">");
				buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
				buf.append("</table></th>");
			}
		} else {
//			buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=memberdocs&uid=" + uid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&linkid=" + linkid + "&sort_by=" + Defines.SORT_BY_TYPE_STR + "&sort=" + Defines.SORT_ASC_STR + "\" class=\"fbox\">");
//			buf.append("Type</a>");
			buf.append("</th>");
		}

		buf.append("</tr>");
		return (buf.toString());
	}

	private void editPrimaryContact(String msg, AccessCntrlFuncs acf) throws Exception {
		ETSUser pc = null;
		Vector members = new Vector();
		StringBuffer buf = new StringBuffer();
		boolean gray_flag = true;
		boolean pc_flag = false;
		UserObject uo = null;

		printHeader("Edit primary contact", "", false);
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		buf.append("<tr><td>");

		if (!msg.equals("")) {
			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
			buf.append("<tr>");
			buf.append("<td  valign=\"top\" align=\"left\" >");
			buf.append("<span style=\"color:#ff3333\">" + msg + "</span>");
			buf.append("</td>");
			buf.append("</tr>");
			buf.append("</table>");
		}

		if (!(ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.ADMIN, conn) || ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, Project.getProjectId(), Defines.MANAGE_USERS, conn) || isSuperAdmin)) {
			buf.append("You are not authorized to perform this action");
			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
			buf.append("<tr>");
			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
			buf.append("</td> ");
			buf.append("</tr>");
			buf.append("</table> ");
			writer.println(buf.toString());
			return;
		}

		try {
			members = ETSDatabaseManager.getProjMembersWithOutPriv(Project.getProjectId(), Defines.VISITOR, true, conn);
			pc = ETSDatabaseManager.getPrimaryContact(Project.getProjectId(), conn);
		} catch (Exception e) {
			logger.error("ETSAdminServlet:editPrimaryContact,getProjmemebers() error =", e);
			e.printStackTrace();
		}

		if (members == null) {
			buf.append("There are no team members at this time - null");
			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
			buf.append("<tr>");
			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
			buf.append("</td> ");

			buf.append("</tr>");
			buf.append("</table> ");
		} else {
			if (members.size() <= 0) {
				buf.append("There are no team  members at this time");
				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
				buf.append("<tr>");
				buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
				buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
				buf.append("</td> ");
				buf.append("<td  align=\"left\" valign=\"top\">");
				buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
				buf.append("</td> ");

				buf.append("</tr>");
				buf.append("</table> ");
			} else {
				Vector ibm_members = new Vector();
				for (int i = 0; i < members.size(); i++) {
					ETSUser memb = (ETSUser) members.elementAt(i);
									
					// Added by Shanmugam for manage primary contact use case
					// if workspace does not have any primary contact, the primary contact will be as null.
					// start here 
						if(pc == null){
								String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, memb.getUserId());
								String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
								if (decaftype.equals("I")) {
									ibm_members.addElement(memb);
								} else {
									System.out.println(memb.getUserId() + " is external");
								}
							}else{ // end here
								if (!memb.getUserId().equals(pc.getUserId())) {
									String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, memb.getUserId());
									String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
									if (decaftype.equals("I")) {
										ibm_members.addElement(memb);
									} else {
										System.out.println(memb.getUserId() + " is external");
									}
								}
							}
				
				}

				if (ibm_members.size() != 0) {
					buf.append("<form method=\"get\" action=\"ETSProjectsServlet.wss\" name=\"membersmanageForm\">");
					buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
					buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
					buf.append("<input type=\"hidden\" name=\"action\" value=\"primarycontact\" />");
					buf.append("<input type=\"hidden\" name=\"sub\" value=\"sub\" />");

					buf.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
					
					
					buf.append("<tr><td height=\"21\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");
					// if block is checking the primary contact empty 
					if( pc != null){
						uo = AccessCntrlFuncs.getUserObject(conn, pc.getUserId(), true, false);
						if (uo.gUSER_FULLNAME.equals("")) {
							buf.append("<tr><td height=\"21\">The current primary contact is <b>" + pc.getUserId() + "</b></td></tr>");
						} else {
							buf.append("<tr><td height=\"21\">The current primary contact is <b>" + uo.gUSER_FULLNAME + "</b> [" + pc.getUserId() + "]</td></tr>");
						}
					}else{
						buf.append("<tr><td height=\"21\"><b>The Current Primary contact is an invalid ID</b></td></tr>");
					}
					buf.append("<tr><td height=\"21\"><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>");
					buf.append("</table>");

					buf.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					buf.append("<tr><td height=\"21\"><label for=\"uid\">Change primary contact to: </label> &nbsp;</td>");
					buf.append("<td height=\"21\" align=\"left\">");
					buf.append("<select id=\"uid\" name=\"uid\" size=\"\" align=\"left\" class=\"iform\">");
					buf.append("<option value=\"\"> </option>");
					for (int j = 0; j < ibm_members.size(); j++) {
						ETSUser user = (ETSUser) ibm_members.elementAt(j);
						//UserObject uo2 = acf.getUserObject(conn,user.getUserId(),true,false);
						String username = ETSUtils.getUsersName(conn, user.getUserId());
						if (username.equals("")) {
							buf.append("<option value=\"" + user.getUserId() + "\">" + user.getUserId() + "</option>");
						} else {
							buf.append("<option value=\"" + user.getUserId() + "\">" + username + " [" + user.getUserId() + "]</option>");
						}
					}
					buf.append("</select>");
					buf.append("</td>");
					buf.append("</tr></table>");

					buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
					buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");

					buf.append("<tr>");
					buf.append("<td  align=\"left\">");
					buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Submit\" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					buf.append("</td> ");
					buf.append("<td  align=\"left\">&nbsp;</td>");
					buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
					buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
					buf.append("</td> ");
					buf.append("<td  align=\"left\" valign=\"top\">");
					buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
					buf.append("</td> ");

					buf.append("</tr>");
					buf.append("</table> ");
					buf.append("</form>");
				} else {
					buf.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
					// if block is checking the primary contact empty	
					if(pc != null){
					 uo = AccessCntrlFuncs.getUserObject(conn, pc.getUserId(), true, false);
					if (uo.gUSER_FULLNAME.equals("")) {
						buf.append("<tr><td height=\"21\">The current primary contact is <b>" + pc.getUserId() + "</b></td></tr>");
					} else {
						buf.append("<tr><td height=\"21\">The current primary contact is <b>" + uo.gUSER_FULLNAME + "</b>[" + pc.getUserId() + "]</td></tr>");
					}
					}else{
						buf.append("<tr><td height=\"21\"><b>The Current Primary contact is an invalid ID</b></td></tr>");
					}

					buf.append("<tr><td height=\"21\">Currently, there are no eligible members to switch to primary contact.</td></tr>");
					buf.append("</table>");

					buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
					buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");
					buf.append("<tr>");
					buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
					buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
					buf.append("</td> ");
					buf.append("<td  align=\"left\" valign=\"top\">");
					buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
					buf.append("</td> ");

					buf.append("</tr>");
					buf.append("</table> ");
				}

			}
		}
		writer.println(buf.toString());
	}

	private String getParameter(HttpServletRequest req, String key) {
		String value = req.getParameter(key);

		if (value == null) {
			return "";
		} else {
			return value;
		}
	}

	public String[] sendMemberRequest(UserObject newuo, String addinfo, String job, String type, ETSProj project, int topCatId, int currentCatId, EdgeAccessCntrl es, AccessCntrlFuncs acf, Connection conn) {
		Vector vProjAdmins;
		try {
			System.err.println("before getallprojadmins");
			vProjAdmins = ETSDatabaseManager.getAllProjectAdmins(project.getProjectId(), conn);
			System.err.println("after getallprojadmins");
		} catch (SQLException se) {
			logger.error("ETSProjectsServlet::sendMemberRequest::SQLEx=", se);
			String[] ret = { "1", "No workspace managers were found for this project" };
			return ret;
		} catch (Exception e) {
			logger.error("ETSProjectsServlet::sendMemberRequest::Ex=", e);
			String[] ret = { "1", "No workspace managers were found" };
			return ret;
		}

		if (vProjAdmins == null) {
			String[] ret = { "1", "No workspace managers were found" };
			return ret;
		} else if (vProjAdmins.size() <= 0) {
			String[] ret = { "1", "No workspace managers were found" };
			return ret;
		} else {
			try {
				String message = "";
				String subject = "";
				String logAction = "";
				if (type.equals("add")) {
					System.err.println("before sendadd");
					message = sendAddMemberRequest(newuo, addinfo, job, project, topCatId, currentCatId, es, acf);
					System.err.println("after sendadd");
					subject = "" + unBrandedprop.getAppName() + " - Request to add new team member";
					logAction = "Add request";
				} else if (type.equals("del")) {
					message = sendDelMemberRequest(newuo, addinfo, project, topCatId, currentCatId, es, acf);
					subject = "" + unBrandedprop.getAppName() + " - Request to remove team member";
					logAction = "Delete request";
				} else {
					String[] ret = { "1", "Error occurred, request type not found.  Please try again." };
					return ret;
				}
				String emailids = "";

				for (int i = 0; i < vProjAdmins.size(); i++) {
					//get amt information
					ETSUser memb = (ETSUser) vProjAdmins.elementAt(i);
					try {
						UserObject uo = AccessCntrlFuncs.getUserObject(conn, memb.getUserId(), true, false);
						emailids = emailids + uo.gEMAIL + ",";
					} catch (AMTException ae) {
						logger.error("ETSProjectsServlet::sendMemberRequest::amt exception caught. e= ", ae);
					} catch (SQLException se) {
						logger.error("ETSProjectsServlet::sendMemberRequest::sql exception caught. e= ", se);
					}
				}

				if (emailids.equals("")) {
					String[] ret = { "1", "Error occurred: Email id for the workspace owner not found." };
					return ret;
				} else {
					String toList = "";
					toList = emailids;
					//toList = "sandieps@us.ibm.com";
					boolean bSent = false;
					subject = ETSUtils.formatEmailSubject(subject);

					if (!toList.trim().equals("")) {
						//sendEMail(String from, String to, String sCC, String host, String sMessage, String Subject, String reply)
						bSent = ETSUtils.sendEMail(es.gEMAIL, toList, "", Global.mailHost, message, subject, es.gEMAIL);
					}

					if (bSent) {
						//addEmailLog(String mail_type, String key1, String key2, String key3, String project_id, String subject, String to, String cc)
						ETSDatabaseManager.addEmailLog("Team", newuo.gIR_USERN, logAction, es.gIR_USERN, project.getProjectId(), subject, toList, "", conn);
						String[] ret = { "0", "Workspace owner has been notified." };
						return ret;
					} else {
						String[] ret = { "1", "An error was encountered while trying to notify the workspace owner." };
						return ret;
					}

				}
			} catch (Exception e) {
				logger.error("Exception===", e);
				String[] ret = { "1", "An error was encountered while trying to notify the workspace owner." };
				return ret;
			}
		}
	}

	public String sendAddMemberRequest(UserObject newuo, String addinfo, String job, ETSProj project, int topCatId, int currentCatId, EdgeAccessCntrl es, AccessCntrlFuncs acf) throws Exception {
		StringBuffer message = new StringBuffer();

		//message.append("Dear IBM Customer Connect Workspace Manager, \n\n");
		//message.append("Dear E&TS Connect workspace manager, \n\n");

		//message.append("Dear IBM E&TS Client, \n\n");
		message.append("\n\n");

		if (Project.getProjectOrProposal().equals("P")) {
			message.append(es.gFIRST_NAME + " " + es.gLAST_NAME + " has requested a member \nbe added to the project " + project.getName() + ". \n\n");
		} else {
			message.append(es.gFIRST_NAME + " " + es.gLAST_NAME + " has requested a member \nbe added to the proposal " + project.getName() + ". \n\n");
		}
		message.append("Details of the requested member are as follows: \n\n");
		message.append("==============================================================\n");
		message.append("  Name:           " + ETSUtils.formatEmailStr(newuo.gUSER_FULLNAME) + "\n");
		message.append("  User ID:        " + ETSUtils.formatEmailStr(newuo.gIR_USERN) + "\n");

		String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, newuo.gIR_USERN);
		String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);

		if (decaftype.equals("I")) {
			message.append("  Company:        IBM \n");
		} else {
			if (newuo.gUSER_COMPANY != null) {
				message.append("  Company:        " + ETSUtils.formatEmailStr(newuo.gUSER_COMPANY) + " \n");
			} else {
				message.append("  Company:  \n");
			}
		}

		/*
		  if (newuo.gIBM_SPN_INTERNAL == 1){
		  message.append("  Company:        IBM \n");
		  }
		  else if(newuo.gIBM_SPN_INTERNAL == 0){
		  message.append("  Company:        " + ETSUtils.formatEmailStr(newuo.gUSER_COMPANY) + " \n");
		  }
		  else{
		  if (newuo.gUSER_COMPANY != null){
		  message.append("  Company:        " + ETSUtils.formatEmailStr(newuo.gUSER_COMPANY) + " \n");
		  }
		  else{
		  message.append("  Company:  \n");
		  }
		  }
		*/

		message.append("  Email:          " + ETSUtils.formatEmailStr(newuo.gEMAIL) + " \n");
		message.append("  Phone:          " + ETSUtils.formatEmailStr(newuo.gPHONE) + " \n\n");

		if (Project.getProjectOrProposal().equals("P")) {
			message.append("Requested project role:  " + job + "\n\n");
		} else {
			message.append("Requested proposal role:  " + job + "\n\n");
		}
		message.append("Additional Information from requester:  " + addinfo + "\n\n");
		//message.append("    "+addinfo+"\n\n");

		message.append("To add this user, go to the following URL:\n");
		//SPN url change
		String url = Global.getUrl("ets/displayAddMembrInput.wss") + "?action=admin_add&proj=" + project.getProjectId() + "&tc=" + topCatId + "&cc=" + currentCatId;
		//String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?proj="+project.getProjectId()+"&tc="+topCatId+"&cc="+currentCatId+"&linkid="+linkid;

		message.append(url + "\n\n");

		message.append("==============================================================\n");
		message.append("Delivered by " + unBrandedprop.getAppName() + ". \n");
		message.append("This is a system generated email.\n");
		message.append("==============================================================\n\n");

		return message.toString();
	}

	public String sendDelMemberRequest(UserObject newuo, String addinfo, ETSProj project, int topCatId, int currentCatId, EdgeAccessCntrl es, AccessCntrlFuncs acf) throws Exception {
		StringBuffer message = new StringBuffer();

		//message.append("Dear IBM Customer Connect Workspace Manager, \n\n");
		//message.append("Dear E&TS Connect workspace manager, \n\n");
		//message.append("Dear IBM E&TS Client, \n\n");
		message.append("\n\n");
		if (Project.getProjectOrProposal().equals("P")) {
			message.append(es.gFIRST_NAME + " " + es.gLAST_NAME + " has requested a member \nbe removed from the project " + project.getName() + ". \n\n");
		} else {
			message.append(es.gFIRST_NAME + " " + es.gLAST_NAME + " has requested a member \nbe removed from the proposal " + project.getName() + ". \n\n");
		}
		message.append("Details of the requested member are as follows: \n\n");
		message.append("==============================================================\n");
		message.append("  Name:           " + ETSUtils.formatEmailStr(newuo.gUSER_FULLNAME) + "\n");
		message.append("  User ID:        " + ETSUtils.formatEmailStr(newuo.gIR_USERN) + "\n");

		String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, newuo.gIR_USERN);
		String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);

		if (decaftype.equals("I")) {
			message.append("  Company:        IBM \n");
		} else {
			if (newuo.gUSER_COMPANY != null) {
				message.append("  Company:        " + ETSUtils.formatEmailStr(newuo.gUSER_COMPANY) + " \n");
			} else {
				message.append("  Company:  \n");
			}
		}

		/*
		  if(newuo.gIBM_SPN_INTERNAL == 1){
		  message.append("  Company:        IBM \n");
		  }
		  else if(newuo.gIBM_SPN_INTERNAL == 0){
		  message.append("  Company:        " + ETSUtils.formatEmailStr(newuo.gUSER_COMPANY) + " \n");
		  }
		  else{
		  if (newuo.gUSER_COMPANY != null){
		  message.append("  Company:        " + ETSUtils.formatEmailStr(newuo.gUSER_COMPANY) + " \n");
		  }
		  else{
		  message.append("  Company:  \n");
		  }
		  }
		*/

		message.append("  Email:          " + ETSUtils.formatEmailStr(newuo.gEMAIL) + " \n");
		message.append("  Phone:          " + ETSUtils.formatEmailStr(newuo.gPHONE) + " \n\n");

		message.append("Additional Information from requester:  " + addinfo + "\n\n");
		//message.append("    "+addinfo+"\n\n");

		message.append("To remove this user go to the following URL:\n");
		//SPN url change
		String url = Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + project.getProjectId() + "&action=delmember&tc=" + topCatId + "&cc=" + currentCatId + "&linkid=" + unBrandedprop.getLinkID();
		//String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?proj="+project.getProjectId()+"&tc="+topCatId+"&cc="+currentCatId+"&linkid="+linkid;

		message.append(url + "\n\n");

		message.append("==============================================================\n");
		message.append("Delivered by " + unBrandedprop.getAppName() + ".\n");
		message.append("This is a system generated email.\n");
		message.append("==============================================================\n\n");

		return message.toString();
	}

	/*
		private Vector sortByName(Vector v){
			Vector names = new Vector();
			if (v == null) return v;


			for (int i = 0; i < v.size(); i++){
				ETSUser memb = (ETSUser)v.elementAt(i);
				UserObject uo = AccessCntrlFuncs.getUserObject(conn,memb.getUserId(),true,false);
				String username = (String)uo.gUSER_FULLNAME;
				names.addElement(username);
			}

		}
	*/

	/**
	 * Method userHasEntitlement.
	 * @param con
	 * @param sEdgeId
	 * @param sEntitlement
	 * @return boolean
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean userHasEntitlement(Connection con, String sEdgeId, String sEntitlement) throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		boolean bEntitled = false;

		try {

			String decafType = AccessCntrlFuncs.decafType(sEdgeId,con);
			// 6.1 Internal users are not required to have Sales_collab entitlement
			if (decafType.equalsIgnoreCase("I") && sEntitlement.equalsIgnoreCase(Defines.COLLAB_CENTER_ENTITLEMENT)) {
				bEntitled = true;
			} else {
				sQuery.append("SELECT ENTITLEMENT FROM AMT.S_USER_ACCESS_VIEW WHERE ENTITLEMENT = ? AND USERID = ? with ur");

				SysLog.log(SysLog.DEBUG, "ETSAdminServlet::userHasEntitlement()", "QUERY : " + sQuery.toString());

				stmt = con.prepareStatement(sQuery.toString());
				stmt.setString(1, sEntitlement);
				stmt.setString(2, sEdgeId);

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
			logger.error("execption===", e);
			throw e;
		} catch (Exception e) {
			logger.error("execption===", e);
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return bEntitled;
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

	private void doAddPhoto(AccessCntrlFuncs acf, String msg) {
		StringBuffer buf = new StringBuffer();

		String userid = getParameter(request, "uid");
		if (userid.equals(es.gIR_USERN)) {
			String titlestr = "";
			try {
				UserObject uo = AccessCntrlFuncs.getUserObject(conn, userid, true, false);
				if (uo != null) {
					titlestr = uo.gUSER_FULLNAME + "[" + userid + "]";
				} else {
					titlestr = userid;
				}
			} catch (Exception e) {
				logger.error("execption===", e);
				e.printStackTrace();
			}

			printHeader("Add member photo", titlestr, false);
			writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			writer.println("<tr><td>");

			buf.append("<form method=\"post\" action=\"ETSUserInfoServlet.wss\" enctype=\"multipart/form-data\" name=\"photoaddForm\">");
			buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
			buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"cc\" value=\"" + CurrentCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"action\" value=\"addmemph2\" />");

			if (!msg.equals("")) {
				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">");
				buf.append("<tr>");
				buf.append("<td  valign=\"top\" align=\"left\" >");
				buf.append("<span style=\"color:#ff3333\">");

				if (msg.equalsIgnoreCase("1") || msg.equalsIgnoreCase("2")) {
					buf.append("Please select a photo file to upload.");
				} else if (msg.equalsIgnoreCase("3")) {
					buf.append("Photo file size is greater than 1 MB. Please upload a photo file that is less than 1 MB.");
				} else if (msg.equalsIgnoreCase("4")) {
					buf.append("A system error occured when uploading photo file. Please try again. If the problem persists, please contact your system administrator");
				} else {
					buf.append("A system error occured when uploading photo file. Please try again. If the problem persists, please contact your system administrator");
				}

				buf.append("</span>");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("</table>");

				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
				buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\">");
				buf.append("<img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"443\" valign=\"bottom\" alt=\"\" />");
				buf.append("</td></tr>");
				buf.append("</table>");
			}

			buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"443\" border=\"0\">");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");

			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			buf.append("<tr><td align=\"left\" width=\"99%\"><label for=\"photofile\"><b>Photo:</b></label></td></tr>");
			buf.append("<tr><td colspan=\"2\"><input type=\"file\" id=\"photofile\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"photofile\" value=\"\" /></td></tr>");

			buf.append("</table> ");

			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			buf.append("<tr>");
			buf.append("<td  align=\"left\">");
			buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Submit\" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			buf.append("</td> ");

			buf.append("<td  align=\"left\">&nbsp;</td>");

			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + userid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + userid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to member details</a>");
			buf.append("</td> ");
			buf.append("</tr>");
			buf.append("</table> ");

			buf.append("</form> ");

		} //end of if user
		else {
			printHeader("Authorization error", "", false);
			writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			writer.println("<tr><td>");
			buf.append("You are not authorized to perform this action");
		}
		writer.println(buf.toString());

	}

	private void doEditUserInfo(AccessCntrlFuncs acf, String msg) {
		StringBuffer buf = new StringBuffer();
		String skills = "";

		String userid = getParameter(request, "uid");
		if (userid.equals(es.gIR_USERN)) {

			try {
				skills = ETSDatabaseManager.getUserSkills(userid);
			} catch (Exception e) {
				logger.error("execption===", e);
				e.printStackTrace();
				skills = "";
			}

			String titlestr = "";
			try {
				UserObject uo = AccessCntrlFuncs.getUserObject(conn, userid, true, false);
				if (uo != null) {
					titlestr = uo.gUSER_FULLNAME + "[" + userid + "]";
				} else {
					titlestr = userid;
				}
			} catch (Exception e) {
				logger.error("execption===", e);
				e.printStackTrace();
			}

			printHeader("Edit your user information", titlestr, false);
			writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			writer.println("<tr><td>");

			buf.append("<form method=\"post\" action=\"ETSUserInfoServlet.wss\" enctype=\"multipart/form-data\" name=\"editinfoForm\">");
			buf.append("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\"" + Project.getProjectId() + "\" />");
			buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"cc\" value=\"" + CurrentCatId + "\" />");
			buf.append("<input type=\"hidden\" name=\"action\" value=\"editmeminfo2\" />");

			if (!msg.equals("")) {
				buf.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">");
				buf.append("<tr>");
				buf.append("<td  valign=\"top\" align=\"left\" >");
				buf.append("<span style=\"color:#ff3333\">");

				if (msg.equalsIgnoreCase("1") || msg.equalsIgnoreCase("2")) {
					buf.append("Please select a CV file to upload.");
				} else if (msg.equalsIgnoreCase("3")) {
					buf.append("CV file size is greater than 1 MB. Please upload a CV file that is less than 1 MB.");
				} else if (msg.equalsIgnoreCase("4")) {
					buf.append("A system error occured when updating your skills and CV information. Please try again. If the problem persists, please contact your system administrator");
				} else {
					buf.append("A system error occured when updating your skills and CV information. Please try again. If the problem persists, please contact your system administrator");
				}

				buf.append("</span>");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("</table>");
			}

			buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");

			//skills
			buf.append("<tr><td align=\"left\"><label for=\"skills\"><b>Skills:</b></label></td></tr>");
			buf.append("<tr><td><textarea id=\"skills\" rows=\"5\" cols=\"35\" style=\"width:400px\" width=\"400px\" name=\"skills\" />" + skills + "</textarea></td></tr>");

			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");

			//cv
			buf.append("<tr><td align=\"left\"><label for=\"cvfile\"><b>Add/Update your CV:</b></label></td></tr>");
			buf.append("<tr><td colspan=\"2\"><input type=\"file\" id=\"cvfile\" size=\"35\" style=\"width:400px\" width=\"400px\" name=\"cvfile\" value=\"\" /></td></tr>");

			//del cv option
			try {
				if (ETSUtils.isUserCVAvailable(conn, userid)) {
					buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");
					buf.append("<tr><td colspan=\"2\"><input type=\"checkbox\" id=\"delcvfile\" name=\"delcvfile\" value=\"1\" /> &nbsp;");
					buf.append("<label for=\"delcvfile\"><b>Delete CV</b></label></td></tr>");
				}
			} catch (Exception e) {
				logger.error("execption===", e);
				e.printStackTrace();
			}
			buf.append("</table> ");

			buf.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"25\" alt=\"\" /></td></tr>");
			buf.append("<tr>");
			buf.append("<td  align=\"left\">");
			buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Submit\" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			buf.append("</td> ");

			buf.append("<td  align=\"left\">&nbsp;</td>");

			buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + userid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
			buf.append("</td> ");
			buf.append("<td  align=\"left\" valign=\"top\">");
			buf.append("<a href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + userid + "&proj=" + Project.getProjectId() + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to member details</a>");
			buf.append("</td> ");
			buf.append("</tr>");
			buf.append("</table> ");

			buf.append("</form> ");

		} //end of if user
		else {
			buf.append("You are not authorized to perform this action");
		}
		writer.println(buf.toString());

	}

	public String getAccessStatus(Connection conn, String sRequestId, String projectType) throws SQLException, Exception {

		String status = "";

		if (projectType.equals(Defines.ETS_WORKSPACE_TYPE)) {

			ETSProcReqAccessFunctions etsProcFunc = new ETSProcReqAccessFunctions();
			status = etsProcFunc.getStatus(sRequestId, conn);

		}

		if (projectType.equals(Defines.AIC_WORKSPACE_TYPE)) {

			AICProcReqAccessFunctions aicProcFunc = new AICProcReqAccessFunctions();
			status = aicProcFunc.getStatus(sRequestId, conn);

		}

		return status;

	}

} // end of class
