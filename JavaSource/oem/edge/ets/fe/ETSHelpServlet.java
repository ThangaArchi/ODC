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

/**
 * @author: Sathish
 */

import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;

public class ETSHelpServlet extends javax.servlet.http.HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.15";


	public ETSHelpServlet() {
		super();
	}

	public static String getClassVersion() {
		return CLASS_VERSION;
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

		DbConnect db = null;
		Connection conn = null;
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			String tz="";
			tz=ETSUtils.checkNull(request.getParameter("proj"));
			
			EdgeAccessCntrl es = new EdgeAccessCntrl();
			if (!es.GetProfile(response, request)) {
				SysLog.log(SysLog.DEBUG, this, "Authentication Process Failed");
				return;
			}

			if (!Global.loaded) {
				Global.Init();
			}

			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");

			
			String sFieldName = ETSUtils.checkNull(request.getParameter("field_name"));	
			
			String sProjType = ETSUtils.checkNull(request.getParameter("proj_type"));
			
			if (sProjType.equalsIgnoreCase("")) {
				sProjType = Defines.ETS_WORKSPACE_TYPE;
			}
			
			UnbrandedProperties prop = PropertyFactory.getProperty(sProjType);
			
			PopupHeaderFooter header = new PopupHeaderFooter();
			header.setPageTitle(prop.getAppName() + " - Help");
			out.println(header.printPopupHeader());
						
			if (sFieldName.equalsIgnoreCase("q1")) {
				ETSUtils.popupHeaderLeft(prop.getAppName() + " help","Request new company - Help for question 2",out);
			} else if (sFieldName.equalsIgnoreCase("q2")) {
				ETSUtils.popupHeaderLeft(prop.getAppName() + " help","Request new company - Help for question 3",out);
			} else if (sFieldName.equalsIgnoreCase("q3")) {
				ETSUtils.popupHeaderLeft(prop.getAppName() + " help","Request new company - Help for question 4",out);
			} else if (sFieldName.equalsIgnoreCase("q4")) {
				ETSUtils.popupHeaderLeft(prop.getAppName() + " help","Request new company - Help for question 5",out);
			} else if (sFieldName.equalsIgnoreCase("q5")) {
				ETSUtils.popupHeaderLeft(prop.getAppName() + " help","Request new company - Help for question 6",out);	
			} else if (sFieldName.equalsIgnoreCase("projtype")) {
				ETSUtils.popupHeaderLeft(prop.getAppName() + " help","Workspace type",out);
			} else if (sFieldName.equalsIgnoreCase("itar")) {
				ETSUtils.popupHeaderLeft(prop.getAppName() + " help","ITAR unclassified workspace",out);
			} else if (sFieldName.equalsIgnoreCase("create_company")) {
				ETSUtils.popupHeaderLeft(prop.getAppName() + " help","Request to add new company",out);
			} else if (sFieldName.equalsIgnoreCase("copyto")) {
				ETSUtils.popupHeaderLeft(prop.getAppName() + " help","Copy to help",out);
			}
			
			displayHelp(out,es,sFieldName,prop.getAppName());
			
			//v2sagar
			if(tz.equalsIgnoreCase("ETSTimeZone"))
			{
				displayHelpForTimeZone(out,es,tz);
			}
			
			ETSUtils.popupHeaderRight(out);
			out.println(header.printPopupFooter());


		} catch (Exception e) {
			SysLog.log(SysLog.ERR, this, e);
			ETSUtils.displayError(response.getWriter(), ETSErrorCodes.getErrorCode(e), "Error occurred on ETSHelpServlet.");
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	
	//v2sagar
	
	private void displayHelpForTimeZone(PrintWriter out, EdgeAccessCntrl es, String timeZone) {
		if (timeZone.equalsIgnoreCase("ETSTimeZone")) {
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><br />You have disabled the javascript setting of your browser." +
					"<br />The date/time displayed are in server's timezone(GMT)." +
					"<br />Enable the javascript of your browser to display the date/time in your timezone. </td></tr>");
			out.println("<tr><td headers=\"\" align=\"left\"><br /><br />To enable javascript in IE" +
					"<ul TYPE=CIRCLE><li>From Tools select Internet Options" +
					"<li>Under the Security tab select the Internet icon " +
					"<li>Click the Custom level button " +
					"<li>Enable 'Active Scripting' Under 'Scripting' " +
					"<li>Cick OK, Answer Yes to the prompt box that appears Click OK again </ul> </td>");
			out.println("</tr><br />");
			out.println("<tr><td headers=\"\" align=\"left\"><br /><br />To enable Javascript in Firefox" +
					"<ul TYPE=CIRCLE><li>From Tools select Options " +
					"<li>Select the check box to 'Enable JavaScript'" +
					"<li>Click OK </ul> </td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			

		}		
	}


	/**
	 * @param out
	 * @param sFieldName
	 */
	private void displayHelp(PrintWriter out, EdgeAccessCntrl es, String sFieldName, String sAppName) {
		
		if (sFieldName.equalsIgnoreCase("q1")) {
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Do you know or have any reason to believe that your customer is engaged in any of these activities? If \"YES\" you must answer YES. </td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Nuclear explosive activities:</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Research on or development, design, manufacture, construction, testing or maintenance of any nuclear, explosive device, or components or subsystems of such a device.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Nuclear activities:</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Research on, or development, design, manufacture, construction, operation, or maintenance of any \"nuclear reactor\" (including for non-weapon-related nuclear power generation), critical facility, facility for the fabrication of nuclear fuel, facility for the conversion of nuclear material from one chemical form to another, or separate storage.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");


			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Sensitive nuclear activities are: </b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Nuclear fuel cycle activities, including research on or development, design, manufacture, construction, operation or maintenance of any of the following facilities, or components for such facilities:</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><ul><li>Facilities for the chemical processing of irradiated special nuclear or source material; </li></ul></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><ul><li>Facilities for the production of heavy water; </li></ul></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><ul><li>Facilities for the separation of isotopes of source and special nuclear material; or </li></ul></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><ul><li>Facilities for the fabrication of nuclear reactor fuel containing plutonium</li></ul></td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br /><br />");
			
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Chemical or Biological weapons:</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">To the best of your knowledge or belief is your customer involved with the design, development, production, stockpiling or use of chemical or biological weapons?</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br /><br />");

			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Certain Missile Technology projects</b> have been identified in the following countries:</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><ol><li>China: M Series Missiles CSS-2</li>");
			out.println("<li>India: Agni, Prithvi, SLV-3 Satellite Launch Vehicle, Augmented Satellite Launch Vehicle (ASLV), Polar Satellite Launch Vehicle (PSLV),  Geostationary Satellite Launch Vehicle (GSLV)</li>");
			out.println("<li>Iran: Surface-to-Surface Missile Project, Scud Development Project</li>");
			out.println("<li>Korea, North: No Dong 1, Scud Development Project</li>");
			out.println("<li>Pakistan: Haft Series Missiles China: M Series Missiles CSS-2</li>");
			out.println("</ol></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("</table>");
			
			out.println("<br /><br />");
	
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">If you still require export compliance help contact your local Export Regulations Coordinator (ERC); or Joe Morris MD and E&TS ERC. </td>");
			out.println("</tr>");
			out.println("</table>");

			
		} else if (sFieldName.equalsIgnoreCase("q2")) {
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Do you know or have any reason to believe that your customer is engaged in any diversion activities? If \"YES\"  you must answer YES.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Diversion potential Indicators:</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Some indicators or \"red flags\" which may indicate an illegal export transaction include, but are not limited to: </td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><ol><li>The order (hardware & software) does not match the customer's business requirements.</li>");
			out.println("<li>The customer is not using normal installation, training and maintenance services. </li>");
			out.println("<li>The customer's business needs and use of IBM's products is not well known and understood by IBM. </li>");
			out.println("<li>The customer has requested unusual payment or delivery terms and conditions. </li>");
			out.println("<li>There is an indication that the products are destined for an embargoed or terrorist country  (Cuba, Iran, Iraq, N. Korea, Libya, Sudan, Syria). </li>");
			out.println("</ol></td>");
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<br /><br />");
	
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">If you still require export compliance help contact your local Export Regulations Coordinator (ERC); or Joe Morris MD and E&TS ERC. </td>");
			out.println("</tr>");
			out.println("</table>");

		} else if (sFieldName.equalsIgnoreCase("q3")) {
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Do you know or have any reason to believe that your customer is a foreign government entity? If \"YES\"  you must answer YES.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>A government entity is:</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Any foreign central, regional or local government department, agency, or other entity performing governmental functions; including governmental research institutions, governmental corporations or their separate business units. Not included as  a governmental entity are : utilities (including telecommunications and Internet service providers); banks and financial institutions; ransportation; broadcast or entertainment; educational organizations; civil health and medical organizations.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br /><br />");
	
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">If you still require export compliance help contact your local Export Regulations Coordinator (ERC); or Joe Morris MD and E&TS ERC. </td>");
			out.println("</tr>");
			out.println("</table>");
		
		} else if (sFieldName.equalsIgnoreCase("q4")) {
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Do you know or have any reason to believe that your customer is a military end user or this work has a military end use?  If \"YES\"  you must answer YES.  </td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Military End Use.</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Employment or application for military purpose. Any entity, military or civilian, using products for military purposes is involved in a military end-use. Military purposes includes the broadest definition of activities: designing, developing, manufacturing for, distributing, managing, repairing, maintaining, transporting, advising, consulting, servicing any military user for any military purpose.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Military End User.</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Any entity related to and involved with the armed forces of a country. Includes all branches; army, navy, air force, strategic forces, intelligence agencies, security forces, interior and border forces, reserve forces and paramilitary forces as well as governmental agencies, ministry, or departments involved with governance and management of forces. Individual country definitions of 'military' may also include functions or departments not usually associated with military designations. In some countries, research and development institutions, railroad, air transport capabilities and communications facilities are under direct or partial control of the military and, in these countries, these entities are to be considered military end users.</td>");
			out.println("</tr>");
			out.println("</table>");

			
			out.println("<br /><br />");
	
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">If you still require export compliance help contact your local Export Regulations Coordinator (ERC); or Joe Morris MD and E&TS ERC. </td>");
			out.println("</tr>");
			out.println("</table>");
			
		} else if (sFieldName.equalsIgnoreCase("q5")) {
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Do you know or have any reason to believe that your customer has a written assurance for technology and software under restriction (TSR) on file for this entity?  If \"YES\"  you must answer YES.  </td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Letter of Assurance:</b></td>");
			out.println("</tr>");
			out.println("</table>");
						
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><ol><li>When required, it is the responsibility of the requestor to obtain the Letter of Assurance.</li>");
			out.println("<li>A Letter of Assurance (LOA) must be signed by the recipient of software or technology exported under license exception TSR. </li>");
			out.println("<li>It must be received by the IBM exporter prior to export. </li>");
			out.println("<li>Through the LOA, the recipient of the controlled software or technology assures the exporter that the software or technology will not be reexported to countries or their foreign nationals who are not eligible to receive them. </li>");
			out.println("<li>Samples and existing LOAs can be found at: http://w3-1.ibm.com/chq/ero/ero.nsf/all+web+pages/chapter+4</li> </li>");
			out.println("</ol></td>");
			out.println("</tr>");
			out.println("</table>");

			
			out.println("<br /><br />");
	
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">If you still require export compliance help contact your local Export Regulations Coordinator (ERC); or Joe Morris MD and E&TS ERC. </td>");
			out.println("</tr>");
			out.println("</table>");
		
		} else if (sFieldName.equalsIgnoreCase("projtype")) {
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">There are several types of workspaces available. All workspaces include a main tab and a team tab.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Client Voice</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">This workspace helps manage client satisfaction. Includes set/met and self assessments templates along with feedback capabilities.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Proposal - Main workspace</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">This type of workspace is designed for practice/sales phase helping to manage the steps that would lead up to a contract. It supports the following tabs: contracts tab for task management, documents repository, issues/change management, ASIC tabs. </td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Project - Main workspace</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">These workspaces are designed for the delivery phase after the contract has been signed. It supports the following tabs: meetings,  documents repository, issues/change management, ASIC tabs. </td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Proposal - Sub workspace and Project - Sub workspace</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Sub workspaces function much like the main workspace with an owner, tab options, membership options and provide a way to have different teams of the project separate workspace. A main workspace can have multiple sub workspaces and they will be grouped with the main workspace on the E&TS Connect landing page that lists all the workspaces. Membership of the sub workspace can be chosen from members of the main workspace. Issue management and document repositories will be completely separate from the main workspace.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");

		} else if (sFieldName.equalsIgnoreCase("itar")) {
			
			ETSProperties prop = new ETSProperties();
			
			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				// internal
				
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">" + prop.getITARInternalHelp() + "</td>");
				out.println("</tr>");
				out.println("</table>");
			
				out.println("<br />");
 
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "sout.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Outside IBM content\" /></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + prop.getITARInternalLink1() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + prop.getITARInternalLink1() + "','ITAR'); return false;\" onkeypress=\"window.open('" + prop.getITARInternalLink1() + "'); return false;\">" + prop.getITARInternalLink1() + "</a></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "sout.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Outside IBM content\" /></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + prop.getITARInternalLink2() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + prop.getITARInternalLink2() + "','ITAR'); return false;\" onkeypress=\"window.open('" + prop.getITARInternalLink2() + "','ITAR'); return false;\">" + prop.getITARInternalLink2() + "</a></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "sout.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Outside IBM content\" /></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"http://www.siaed.org/webdocs/itar.pdf\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + prop.getITARInternalLink3() + "','ITAR'); return false;\" onkeypress=\"window.open('" + prop.getITARInternalLink3() + "','ITAR'); return false;\">" + prop.getITARInternalLink3() + "</a></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "sout.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Outside IBM content\" /></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + prop.getITARInternalLink4() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + prop.getITARInternalLink4() + "','ITAR'); return false;\" onkeypress=\"window.open('" + prop.getITARInternalLink4() + "','ITAR'); return false;\">" + prop.getITARInternalLink4() + "</a></td>");
				out.println("</tr>");
				out.println("</table>");
				
			} else {
				
				// external customers...
				
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">" + prop.getITARExternalHelp() + "</td>");
				out.println("</tr>");
				out.println("</table>");
			
				out.println("<br />");
 
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "sout.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Outside IBM content\" /></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + prop.getITARExternalLink1() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + prop.getITARExternalLink1() + "','ITAR'); return false;\" onkeypress=\"window.open('" + prop.getITARExternalLink1() + "','ITAR'); return false;\">" + prop.getITARExternalLink1() + "</a></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "sout.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Outside IBM content\" /></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + prop.getITARExternalLink2() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + prop.getITARExternalLink2() + "','ITAR'); return false;\" onkeypress=\"window.open('" + prop.getITARExternalLink2() + "','ITAR'); return false;\">" + prop.getITARExternalLink2() + "</a></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "sout.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Outside IBM content\" /></td>");
				out.println("<td headers=\"\" align=\"left\"><a href=\"" + prop.getITARExternalLink3() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + prop.getITARExternalLink3() + "','ITAR'); return false;\" onkeypress=\"window.open('" + prop.getITARExternalLink3() + "','ITAR'); return false;\">" + prop.getITARExternalLink3() + "</a></td>");
				out.println("</tr>");
				out.println("</table>");
				
			}
		} else if (sFieldName.equalsIgnoreCase("create_company")) {
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Standard company names are assigned by the IBM STG-Technology Group's SAP database which is the billing system for E&TS and Technology Group. The information you provide will be e-mailed to etsadmin@us.ibm.com who will submit a formal request to have it added. Typically it will take three business days for processing.<br /><br />On the form all questions must be answered by an IBMer who is personally familiar with the company. Include a brief description of the product being accessed, or if services, the IBM technology description. More than ever before it is extremely important for IBMers to know  our customers, our product/technology/software/services being sold, and the END USE of our products. The documentation to support these answers will be retained per corporate record retention requirements. </td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
		} else if (sFieldName.equalsIgnoreCase("copyto")) {
			
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">In the <b>Invitees</b> field, select team members to invite to your meeting. Those you select will receive e-mail invitations.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">In the <b>Copy to</b> field, you can choose additional people to invite to your meeting. Enter e-mail addresses for anyone within or outside of IBM. They will receive e-mail invitations, in the same manner as the invitees.</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">People you can copy can:</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><ul>");
			out.println("<li>Attend a telephone-conference meeting only if the meeting has a call-in number.</li>");
			out.println("<li>Attend a Web conference only if they have valid IDs and entitlement</li>");
			out.println("<li>View meeting details only if they have valid IDs, entitlement and access to " + sAppName + ".</li>");
			out.println("</ul>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			
			out.println("<br />");
			
		}
		
		
	}

	private static boolean displayHelp(Connection con, HttpServletResponse res, String sProjId) throws SQLException, Exception {

		StringBuffer sQuery = new StringBuffer("");
		Statement stmt = null;
		ResultSet rs = null;

		boolean bPrinted = false;

		try {

			sQuery.append("SELECT HELP_DOC,LENGTH(HELP_DOC),HELP_DOC_TYPE FROM ETS.ETS_HELP WHERE PROJECT_ID = '" + sProjId + "' for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				if (rs.getString("HELP_DOC_TYPE").equalsIgnoreCase("PDF")) {
					res.setContentType("application/pdf");
				} else {
					res.setContentType("text/html");
				}

				res.setContentLength(rs.getInt(2));

				//ByteArrayOutputStream out1 = new ByteArrayOutputStream();

				InputStream input = rs.getBinaryStream("HELP_DOC");
				byte buf[] = new byte[512];
				int n = 0;
				int total = 0;
				while ((n = input.read(buf)) > 0) {
					total += n;
					res.getOutputStream().write(buf, 0, n); // new
					res.getOutputStream().flush();

					//out1.write(buf, 0, n);
					//out1.flush();
				}
				input.close();
				res.getOutputStream().flush();
				//out.println(out1.toString());
				bPrinted = true;

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return bPrinted;
	}
}
