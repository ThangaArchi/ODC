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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.Metrics;
import oem.edge.common.Global;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSQuickLinksEditServlet extends HttpServlet {

	public final static String Copyright ="(C) Copyright IBM Corp.  2002, 2003";
	private static final String CLASS_VERSION = "1.18";
	public static final String VERSION = "1.18";
	private static Log logger = EtsLogger.getLogger(ETSQuickLinksEditServlet.class);
    
	protected  ETSDatabaseManager databaseManager;
	private    String mailhost;

	public void service(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		Connection conn = null;
		String Msg = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		Hashtable params;
		StringBuffer sHeader = new StringBuffer("");
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
			
			String projectidStr = getParameter(request, "project_id");
			ETSProj proj = ETSUtils.getProjectDetails(conn, projectidStr);

			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());
			
			Hashtable hs = ETSUtils.getServletParameters(request);
			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", unBrandedprop.getLinkID());
				sLink = unBrandedprop.getLinkID();
			}


			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.INVALID_USER)) {
				response.sendRedirect(unBrandedprop.getLandingPageURL());
				return;
			}

			// check for deleted projects
			if (proj.getProject_status().equalsIgnoreCase("D")) {
				response.sendRedirect(unBrandedprop.getLandingPageURL());
				return;
			}
			
			

			ETSProjectInfoBean projBean =
				(ETSProjectInfoBean) request.getSession(false).getAttribute(
					"ETSProjInfo");
			if (projBean == null || !projBean.isLoaded()) {
				projBean = ETSUtils.getProjInfoBean(conn);
				request.getSession(false).setAttribute("ETSProjInfo", projBean);
			}

			String topCatStr = getParameter(request, "tc");
			if (!topCatStr.equals("")) {
				topCatId = (new Integer(topCatStr)).intValue();
			}
			Calendar cal = Calendar.getInstance();
			String sInMonth = projBean.getSMonth();
			String sInYear = projBean.getSYear();
			if (!sInMonth.trim().equals("") && !sInYear.trim().equals("")) {
				projBean.setSMonth(sInMonth);
				projBean.setSYear(sInYear);
				cal.set(
					Integer.parseInt(sInYear),
					Integer.parseInt(sInMonth),
					01);
			}
			ETSParams parameters = new ETSParams();
			parameters.setConnection(conn);
			parameters.setEdgeAccessCntrl(es);
			parameters.setETSProj(proj);
			parameters.setRequest(request);
			parameters.setResponse(response);
			parameters.setTopCat(topCatId);
			parameters.setWriter(writer);
			parameters.setLinkId(sLink);
			parameters.setProjBeanInfo(projBean);

			String step = request.getParameter("step");
			if (step == null || !step.equals("2")) {
			} else {
				processLinks(request, parameters);
				response.sendRedirect("ETSProjectsServlet.wss?tc="+topCatStr+"&proj="+projectidStr+"&linkid="+sLink);
				return;
			}

			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, conn, hs);
			EdgeHeader.setPopUp("J");
			EdgeHeader.setHeader("Edit quick links");
			EdgeHeader.setSubHeader(proj.getName());
			sHeader.insert(0, EdgeHeader.printSubHeader());
			sHeader.insert(0, EdgeHeader.printBullsEyeLeftNav());
			sHeader.insert(
				0,
				EdgeHeader.printETSBullsEyeHeader(
					projectidStr,
					proj.getName(),
					sLink));

			// top table to define the content and right sides..
			sHeader.append(
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			sHeader.append(
				"<tr valign=\"top\"><td headers=\"\" width=\"443\" valign=\"top\">");
			writer.println(sHeader.toString());
			Metrics.appLog(conn, es.gIR_USERN, "ETS_Projects");

			// start content...
			//... work starts here
			ETSQuickLinks qkELinks = new ETSQuickLinks();
			ETSDatabaseManager dbManager = new ETSDatabaseManager();
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)
			 || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)
			 || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER)
			 ) {

			if (step == null || !step.equals("2")) {
				writer.println("<form name=\"linkedit\" method=\"post\" action=\"ETSQuickLinksEditServlet.wss\">");

				// modify essential links...

				qkELinks.setLinkType(qkELinks.ESSENTIAL_LINKS);
				qkELinks.setProjectId(projectidStr);
				qkELinks.extractQuickLinks(conn);
				int linkCnt = qkELinks.getLinkVect().size();
				if (linkCnt > 0) {
					// has essential links
					writer.println("<table summary=\"\" width=\"600\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
					writer.println("<tr><td headers=\"\" align=\"left\" class=\"tblue\">Suggested links</td></tr>");
					writer.println("</table>");
					writer.println("<table summary=\"\" width=\"600\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
					//writer.println("<tr><td  headers=\"\" colspan=\"2\" align=\"left\" class=\"tblue\">Suggested links</td></tr>");
					writer.println("<tr><td headers=\"\" width=\"50\"><span class=\"small\"><b><label for=\"label_show\">Show</label></b></span></td><td headers=\"\" align=\"left\" ><span class=\"small\"><b>Name</b></td></tr>");
					String bgColor = "#eeeeee";
					for (int i = 0; i < linkCnt; i++) {
						ETSQuickLink qkELnk = (ETSQuickLink) qkELinks.getLink(i);
						writer.println(	"<tr style=\"background-color: " + bgColor + "\">");
						String checked = "";
						if (qkELnk.isShowLink()) {
							checked = "checked=\"true\" ";
						}
						writer.println("<td  headers=\"\" align=\"center\"><input type=\"checkbox\" "	+ checked+ " id=\"label_show\" name=\"S_E\" value=\""+ qkELnk.getLinkId()+ "\" /></td>");
						writer.println("<td headers=\"\" >" + qkELnk.getLinkName() + "</td>");
						writer.println("</tr>");

						bgColor =(bgColor.equals("#eeeeee")) ? "#ffffff" : "#eeeeee"; // reset the bgcolor

					}
					writer.println("</table>");
					writer.println("<br />");
				}

				// entitlement list
				String company = EntitledStatic.getValue(conn, "select company from ets.ets_projects where project_id = '"+projectidStr+"'");
				
				StringBuffer sbQry= new StringBuffer();
                
                
								sbQry.append("select ltrim(rtrim(roles_name))||'$'||roles_desc from decaf.roles ");
								sbQry.append("	where roles_id in ");
								sbQry.append("		(select roles_id from decaf.project_roles ");
								sbQry.append("			where project_id in ");
								sbQry.append("				(select project_id from decaf.poc_project_scope ");
								sbQry.append("					where poc_id in ");
								sbQry.append("					(select decaf_id  from decaf.user_roles ");
								sbQry.append("						where roles_id = ");
								sbQry.append("						(select roles_id from decaf.roles ");
								sbQry.append("							where roles_name = 'PointOfContact' ");
								sbQry.append("						) ");
								sbQry.append("					and roles_level_val = '"+company+"' ");
								sbQry.append("					) ");
								sbQry.append("				) ");
								sbQry.append("		) ");
								sbQry.append(" union ");
								sbQry.append(" select ltrim(rtrim(roles_name))||'$'||roles_desc from decaf.roles ");
								sbQry.append("		where roles_id in ");
								sbQry.append("		(select roles_id from decaf.project_roles ");
								sbQry.append("			where project_id in ");
								sbQry.append("			(select project_id from decaf.poc_project_scope ");
								sbQry.append("				where poc_id in ");
								sbQry.append("					(select decaf_id  from decaf.user_roles ");
								sbQry.append("						where roles_id = ");
								sbQry.append("							(select roles_id from decaf.roles ");
								sbQry.append("								where roles_name = 'MultiPOC' ");
								sbQry.append("							) ");
								sbQry.append("					) ");
								sbQry.append("			) ");
								sbQry.append("		) ");
								sbQry.append("order by 1 with ur ");


								//Vector accessList = EntitledStatic.getValues(conn, "select ltrim(rtrim(roles_name))||'$'||roles_desc from decaf.roles where roles_id in (select roles_id from decaf.project_roles where project_id in (select project_id from decaf.poc_project_scope where poc_id in (select decaf_id  from decaf.user_roles where roles_id = (select roles_id from decaf.roles where roles_name = 'PointOfContact') and roles_level_val = '"+company+"'))) order by roles_desc with ur");
								Vector accessList = EntitledStatic.getValues(conn, sbQry.toString());

				

				// modify custom links...
				ETSQuickLinks qkOLinks = new ETSQuickLinks();
				qkOLinks.setLinkType(qkOLinks.OPTIONAL_LINKS);
				qkOLinks.setProjectId(projectidStr);
				qkOLinks.extractQuickLinks(conn);
				linkCnt = qkOLinks.getLinkVect().size();
				if (linkCnt > 0) {
					// get the optional links

					writer.println("<table summary=\"\" width=\"600\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
					writer.println("<tr class=\"tdblue\"><td  headers=\"\" align=\"left\" class=\"tblue\">Custom links</td></tr>");
					writer.println("</table>");

					writer.println(	"<table summary=\"\" width=\"443\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
//					writer.println("<tr class=\"tdblue\"><td  headers=\"\" colspan=\"6\" align=\"left\" class=\"tblue\">Custom links</td></tr>");
					writer.println("<tr><td headers=\"\"><span class=\"small\"><b><label for=\"label_show1\">Show</label></b></span></td>");
					writer.println("<td headers=\"\"><span class=\"small\"><b><label for=\"label_delete\">Delete</label></b></span></td>");
					writer.println("<td headers=\"\"><span class=\"small\"><b><label for=\"label_popup\">Popup</label></b></span></td>");
					writer.println("<td headers=\"\" align=\"left\"><span class=\"small\"><b><label for=\"label_name\">Name</label></b></span></td>");
					writer.println("<td headers=\"\" align=\"left\"><span class=\"small\"><b><label for=\"label_url\">URL</label></b></span></td>");
					writer.println("<td headers=\"\" align=\"left\"><span class=\"small\"><b><label for=\"label_access\">Access</label></b></span></td>");

					String bgColor = "#eeeeee";

					for (int i=0;i < linkCnt; i++) {
						ETSQuickLink qkOlnk =(ETSQuickLink) qkOLinks.getLink(i);
						String checked = "";
						if (qkOlnk.isShowLink()) {
							checked = "checked=\"true\" ";
						}
						writer.println("<tr style=\"background-color: " + bgColor + "\">");
						writer.println("<td  headers=\"\" align=\"center\"><input type=\"checkbox\" align=\"center\" id=\"label_show1\" "+ checked+ " name=\"S_0\" value=\""+ qkOlnk.getLinkId()+"\" /></td>");
						writer.println("<td  headers=\"\" align=\"center\"><input type=\"checkbox\" align=\"center\" id=\"label_delete\" name=\"D_0\" value=\""+ qkOlnk.getLinkId()+ "\" /></td>");
						writer.println("<td  headers=\"\" align=\"center\"><input type=\"checkbox\"  id=\"label_popup\" "+(qkOlnk.isPopUp()?"checked=\"checked\"":"")+" align=\"center\" name=\"P_0\" value=\""+ qkOlnk.getLinkId().trim()+ "\" /></td>");
						writer.println("<td headers=\"\" ><input type=\"text\"  id=\"label_name\" length=\"10\" maxlength=\"40\" name=\"N_"+ qkOlnk.getLinkId().trim()+"\" value=\""+ qkOlnk.getLinkName()+ "\" /></td>");
						writer.println("<td headers=\"\" ><input type=\"text\"  id=\"label_url\" length=\"10\" maxlength=\"255\" name=\"L_"+ qkOlnk.getLinkId().trim()+"\" value=\""+ qkOlnk.getLinkURL()+ "\" /></td>");
						String entLnk = EntitledStatic.getValue(conn,"select entitlement from ets.quick_links_access where project_id='"+projectidStr+"' and link_id = '"+qkOlnk.getLinkId().trim()+"'");
						writer.println("<td headers=\"\" ><select name=\"Z_ENT_"+qkOlnk.getLinkId().trim()+"\" id=\"label_access\" size=\"1\">");

						writer.println("<option value=\"(none)\">Select an access level</option>");
						for (int k=0; k < accessList.size(); k++){
							String accessVal = (String)accessList.elementAt(k);
							if (entLnk.equals(accessVal.substring(0,accessVal.indexOf("$")))){
								writer.println("<option selected value=\""+accessVal.substring(0,accessVal.indexOf("$"))+"\">"+accessVal.substring(accessVal.indexOf("$")+1)+"</option>");
							} else {
								writer.println("<option value=\""+accessVal.substring(0,accessVal.indexOf("$"))+"\">"+accessVal.substring(accessVal.indexOf("$")+1)+"</option>");
							}

						}
						writer.println("</select></td>");
						writer.println("</tr>");
						bgColor =
							(bgColor.equals("#eeeeee")) ? "#ffffff" : "#eeeeee";
					}
					writer.println("</table>");
					writer.println("<br />");
				}

				// create a new custom link...
				writer.println("<table summary=\"\" width=\"600\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
				writer.println("<tr><td  headers=\"\" align=\"left\" class=\"tblue\">Suggested links</td></tr>");
				writer.println("</table>");

				writer.println("<table summary=\"\" width=\"443\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
//				writer.println("<tr><td  headers=\"\" colspan=\"4\" align=\"left\" class=\"tblue\">Create a custom link</td></tr>");
				writer.println("<br />");
				writer.println("<tr>");
				writer.println("<td headers=\"\"><span class=\"small\"><b><label for=\"label_show2\">Show</label></b></span></td>");
				writer.println("<td headers=\"\"><span class=\"small\"><b><label for=\"label_popup2\">Popup</label></b></span></td>");
				writer.println("<td headers=\"\" align=\"left\"><span class=\"small\"><b><label for=\"label_name2\">Name</label></b></span></td>");
				writer.println("<td headers=\"\" align=\"left\"><span class=\"small\"><b><label for=\"label_url2\">URL</label></b></span></td>");
				writer.println("<td headers=\"\" align=\"left\"><span class=\"small\"><b><label for=\"label_access2\">Access</label></b></span></td>");

				writer.println("<br />");
				writer.println("<tr style=\"background-color: #eeeeee\">");
				writer.println("<td  headers=\"\" align=\"center\"><input type=\"checkbox\" id=\"label_show2\" checked=\"true\" name=\"A_S_0\" value=\"Y\"/></td>");
				writer.println("<td  headers=\"\" align=\"center\"><input type=\"checkbox\" id=\"label_popup2\" checked=\"true\" name=\"A_P_0\" value=\"Y\"/></td>");
				writer.println("<td headers=\"\" ><input type=\"text\" length=\"10\" id=\"label_name2\" maxlength=\"40\" name=\"A_N_0\"/></td>");
				writer.println("<td headers=\"\" ><input type=\"text\" length=\"10\" id=\"label_url2\" maxlength=\"255\" name=\"A_U_0\"/></td>");
				writer.println("<td headers=\"\" ><select name=\"A_U_ENT\" size=\"1\" id=\"label_access2\" >");
				writer.println("<option value=\"(none)\" >Select an access level</option>");
				for (int i=0; i < accessList.size(); i++){
					String accessVal = (String)accessList.elementAt(i);
					writer.println("<option value=\""+accessVal.substring(0,accessVal.indexOf("$"))+"\">"+accessVal.substring(accessVal.indexOf("$")+1)+"</option>");
				}
				writer.println("</select></td>");

				writer.println("</tr>");
				writer.println("</table>");
				writer.println("<input type=\"hidden\" name=\"project_id\" value=\""+ projectidStr+ "\"/>");
				writer.println("<input type=\"hidden\" name=\"step\" value=\"2\"/>");
				writer.println("<br />");
				if (proj.getProjectType().equals("AIC")) {
	                writer.println("<table summary=\"\" width=\"600\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
					writer.println("<tr class=\"small\"><td  headers=\"\" align=\"left\" class=\"small\"><b>Note:</b> The customized quicklinks will only be available to internal users.</td></tr>");
					writer.println("<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\"  height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
					writer.println("</table>");
					writer.println("<br />");
				}

				writer.println("<table summary=\"\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
				writer.println("<tr width=\"200\"><td headers=\"\" ><input type=\"image\" src=\""	+ Defines.BUTTON_ROOT+ "submit.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"\"/></td><td headers=\"\" ><a href=\""	+ Defines.SERVLET_PATH+"ETSProjectsServlet.wss?proj="+projectidStr+"&linkid="+ sLink +"\"><img src=\""+ Defines.BUTTON_ROOT+ "cancel_rd.gif\" width=\"21\" height=\"21\" border=\"0\" alt=\"Cancel\"/>Cancel</a></td></tr>");
				writer.println("</table>");
				writer.println("</form>");
				writer.println("</td>");
			}
			} else {
				// user is not a work space manager
				ETSMessgae etsMsg = new ETSMessgae();
				etsMsg.setErrType(etsMsg.TYPE_INFO);
				etsMsg.setErrorCode("Information");
				etsMsg.setErrMsg("You are not workspace manager");
				etsMsg.setErrTxt("Only workspace manager is allowed to edit the quick links");
				etsMsg.setReturnURL(Defines.SERVLET_PATH+"ETSProjectsServlet.wss?proj="+projectidStr+"&linkid="+sLink);
				writer.println(etsMsg.getMessageWindow());
			}
			//...ends here

			// gutter between content and right column
			writer.println("<td  headers=\"\" width=\"7\"><img alt=\"\" src=\""+ Defines.TOP_IMAGE_ROOT+ "c.gif\" width=\"7\" height=\"1\" /></td>");

				// Right column start
//			writer.println("<td headers=\"\"  width=\"150\" valign=\"top\">");
//			ETSContact contact = new ETSContact(proj.getProjectId(), request);
//			contact.printContactBox(writer);
//			writer.println("<br />");
//
//			  ETSCalendar projCalendar = new ETSCalendar();
//			projCalendar.displayCalendar(parameters);
//			writer.println("<br />");
//
//			  ETSProjectHome.displaySiteHelp(parameters);
//			writer.println("<br />");
//
//			  // end right column...
//			writer.println("</td>");
			writer.println("</tr>");
			writer.println("</table>");
			writer.println(EdgeHeader.printBullsEyeFooter());
		} catch (SQLException e) {
			logger.error(this,e);
			ETSUtils.displayError(writer,ETSErrorCodes.getErrorCode(e),"Error occurred on ETSQuickLinkEditServlet.");
		} catch (Exception e) {
			logger.error(this,e);
			ETSUtils.displayError(writer,ETSErrorCodes.getErrorCode(e),"Error occurred on ETSQuickLinkEditServlet.");
		} finally {
			ETSDBUtils.close(conn);
			writer.flush();
			writer.close();
		}
	}
	public void processLinks(HttpServletRequest req, ETSParams etsParms)
		throws SQLException, Exception {

		Connection conn = etsParms.con;
		String projectId = req.getParameter("project_id");
		ETSQuickLinks etsQkLinks = new ETSQuickLinks();
		etsQkLinks.setProjectId(projectId);
		etsQkLinks.setUserId(etsParms.getEdgeAccessCntrl().gIR_USERN);

		// show/no-show essential links...
		EntitledStatic.fireUpdate(conn,"update ets.quick_links set display='N' where link_type like '%"+"E"+"%' and project_id = '"+ projectId	+ "'");
		String[] essLnk = req.getParameterValues("S_E");
		for (int i = 0; (essLnk != null) && (i <  essLnk.length); i++) {
			EntitledStatic.fireUpdate(conn, "update ets.quick_links set display='Y' where link_id = '"+essLnk[i]+"' and project_id = '"+projectId+"'");
		}

		// delete the selected optional links...
		String[] opnDLnk = req.getParameterValues("D_0");
		for (int i=0; ( opnDLnk!=null) && (i < opnDLnk.length); i++){
			 ETSQuickLink qkLink = new ETSQuickLink();
			 qkLink.setProjectId(projectId);
			 qkLink.setLinkId(opnDLnk[i]);
			 etsQkLinks.deleteLink(conn, qkLink);
			 EntitledStatic.fireUpdate(conn,"delete from ets.quick_links_access where project_id = '"+projectId+"' and link_id = '"+opnDLnk[i]+"'");
		}

		// process custom links   // reset show/noshow and popup to 'N'
		EntitledStatic.fireUpdate(conn,"update ets.quick_links set display='N', popup='N' where project_id='"+projectId+"' and link_type='O'");

		String[] opnSLnk = req.getParameterValues("S_0");
		String[] popLnk = req.getParameterValues("P_0");

		Hashtable hashSLnk = null;
		if (opnSLnk!=null){
			hashSLnk = new Hashtable();
			for (int i=0; i < opnSLnk.length; i++){
				hashSLnk.put(opnSLnk[i],opnSLnk[i]);
			}
		}

		Hashtable hashPLnk = null;
		if (popLnk!=null){
			hashPLnk = new Hashtable();
			for (int i=0; i < popLnk.length; i++){
				hashPLnk.put(popLnk[i],popLnk[i]);
			}
		}

		// ..a less looping logic...populate both the string arrays
		// into vectors or hash and then check if there is a key like that

		etsQkLinks.setLinkType(etsQkLinks.OPTIONAL_LINKS);
		etsQkLinks.extractQuickLinks(conn);
		Vector linkVect = etsQkLinks.getLinkVect();
		for (int i=0; i < linkVect.size(); i++){
			String linkId = ((ETSQuickLink)linkVect.get(i)).getLinkId();
			ETSQuickLink qkLink = new ETSQuickLink();
			qkLink.setProjectId(projectId);
			qkLink.setLinkId(linkId);

			qkLink.setLinkName(req.getParameter("N_"+linkId));
			qkLink.setLinkURL(req.getParameter("L_"+linkId));

			if (hashSLnk != null){
				if (hashSLnk.containsValue(linkId)) {
					qkLink.setShowLink(true);
				} else {
					 qkLink.setShowLink(false);
				}
				hashSLnk.remove(linkId);
			}
			if (hashPLnk != null){
				if (hashPLnk.containsValue(linkId)) {
					qkLink.setPopUp(true);
				} else {
					qkLink.setPopUp(false);
				}
				hashPLnk.remove(linkId);
			}

//			  boolean skip = false;
//			  for (int j=0;(opnSLnk!=null) && (j < opnSLnk.length) && !skip; j++){
//				  if (opnSLnk[j].equals(linkId)){qkLink.setShowLink(true); skip=true;}
//			  }
//			  skip = false;
//			  for (int j=0;(popLnk!=null) && (j < popLnk.length) && !skip; j++){
//				  if (popLnk[j].equals(linkId)){qkLink.setPopUp(true); skip=true;}
//			  }
			etsQkLinks.updateLink(conn,qkLink);
			String linkEnt = req.getParameter("Z_ENT_"+linkId);
			if (!linkEnt.equals("(none)")){
				String count = EntitledStatic.getValue(conn,"select count(*) from  ets.quick_links_access where project_id = '"+projectId+"' and link_id = '"+linkId+"'");
				if (Integer.parseInt(count)==0){
					// if there is nothing insert
					EntitledStatic.fireUpdate(conn,"insert into ets.quick_links_access (project_id, link_id, entitlement, last_user, last_timestamp) values ('"+projectId+"','"+linkId+"','"+linkEnt+"','"+etsParms.getEdgeAccessCntrl().gIR_USERN+"',current timestamp)");
				} else {
					// else update the existing
					EntitledStatic.fireUpdate(conn,"update ets.quick_links_access set entitlement = '"+linkEnt+"' where project_id = '"+projectId+"' and link_id = '"+linkId+"'");
				}
			}else {
				// delete any way
				EntitledStatic.fireUpdate(conn,"delete from ets.quick_links_access where project_id = '"+projectId+"' and link_id = '"+linkId+"'");
			}
		}

		// add a custom link - start
		ETSQuickLink newLink = new ETSQuickLink();
		newLink.setLinkName(req.getParameter("A_N_0"));
		//newLink.setPopUp(req.getParameter("A_P_0"));
		newLink.setLinkURL(req.getParameter("A_U_0"));
		//.....only if link name and url is typed
		if (!(newLink.getLinkName().equals("")&&newLink.getLinkURL().equals(""))){
			newLink.setProjectId(projectId);
			newLink.setLinkId(etsQkLinks.getNextLinkId(conn));
			newLink.setLinkType("O");
			if (req.getParameter("A_S_0")!=null)
				{newLink.setShowLink(true);}
			else{newLink.setShowLink(false);}
			if (req.getParameter("A_P_0")!=null)
				{newLink.setPopUp(true);}
			else{newLink.setPopUp(false);}

			newLink.setLastUser(etsParms.getEdgeAccessCntrl().gIR_USERN);
			newLink.setLastTime();
			etsQkLinks.addLink(conn, newLink); // add the link
			String linkEnt = req.getParameter("A_U_ENT");
			if (!linkEnt.equals("(none)")){
				EntitledStatic.fireUpdate(conn,"insert into ets.quick_links_access (project_id, link_id, entitlement, last_user, last_timestamp) values ('"+projectId+"','"+newLink.getLinkId()+"','"+linkEnt+"','"+etsParms.getEdgeAccessCntrl().gIR_USERN+"',current timestamp)");
			}
		}

		/* add a custom link - end */

	}
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			databaseManager = new ETSDatabaseManager();
			mailhost = Global.mailHost;
			if (mailhost == null) {
				mailhost = "us.ibm.com";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}
	private String getParameter(HttpServletRequest req, String key) {
		String value = req.getParameter(key);
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}
}
