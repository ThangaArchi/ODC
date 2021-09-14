/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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


/*
 * Created on Jan 17, 2005
 */
 
package oem.edge.ets.fe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.DocumentsHelper;
import oem.edge.ets.fe.ismgt.helpers.UserProfileTimeZone;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSHeaderFooter {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.18";
	private static Log logger = EtsLogger.getLogger(ETSHeaderFooter.class);
	
	protected StringBuffer sHeader = new StringBuffer("");
	protected StringBuffer sFooter = new StringBuffer("");
	protected StringBuffer sOnlyHeader = new StringBuffer("");
	protected StringBuffer sTabStr = new StringBuffer("");	 
	protected ETSCat topCat = new ETSCat();
	
	public static final int MAX_TOP_TABS = 5;
	
	public ETSHeaderFooter() {
		super();
	}
	
	
	public void init(HttpServletRequest request, HttpServletResponse response) throws SQLException, Exception {
		
		Connection con = null;
		
		try {		
			
			EdgeAccessCntrl es = new EdgeAccessCntrl();
			
			//v2sagar
			String sDate = "";
			sDate=UserProfileTimeZone.getUTCHeaderDate();
			 
			con = ETSDBUtils.getConnection(); 
			if (!es.GetProfile(response, request, con)) {
				return;
			}

			String projectidStr = DocumentsHelper.getProjectID(request);	// request.getParameter("proj");
			ETSProj proj = ETSUtils.getProjectDetails(con, projectidStr);
			
			UnbrandedProperties unBrandedProp = PropertyFactory.getProperty(proj.getProjectType());
			
			Hashtable hs = ETSUtils.getServletParameters(request);
					
			String sLink = DocumentsHelper.getLinkID(request);
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", unBrandedProp.getLinkID());
				sLink = unBrandedProp.getLinkID();
			}
			else {
				hs.put("linkid", sLink);
			}
			
			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, con, hs);
			EdgeHeader.setPopUp("J");
			
			// for help...
			ETSProperties properties = new ETSProperties();
			
			if (properties.displayHelp() == true) {
				EdgeHeader.setPageHelp(Defines.SERVLET_PATH + "ETSHelpServlet.wss?proj=" + proj.getProjectId());
			}
			// end of help..
			
			boolean bInternal = false;
			boolean bClientRole = false;
			
			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				bInternal = true;
			}
			
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equalsIgnoreCase(Defines.WORKSPACE_CLIENT)) {
				bClientRole = true;
			}
			
			// FOR BLADE CENTER
			Vector graphTabs = new Vector();
			Vector graphTabs1 = ETSDatabaseManager.getTopCats(proj.getProjectId(),con);
			Vector userprivs = ETSDatabaseManager.getUserPrivs(proj.getProjectId(), es.gIR_USERN,con);
			if (ETSUtils.checkUserRole(es, proj.getProjectId(),con).equals(Defines.ETS_ADMIN)) {
				graphTabs = graphTabs1;
			} else {
				for (int igt = 0; igt < graphTabs1.size(); igt++) {
					boolean bAdd = false;
					Vector privs = ((ETSCat) (graphTabs1.elementAt(igt))).getPrivsInts();
					if (privs == null) {
						bAdd = true;
					} else if (privs.size() <= 0) {
						bAdd = true;
					} else {
						for (int p = 0; p < privs.size(); p++) {
							if (privs.elementAt(p).toString().equalsIgnoreCase(String.valueOf(Defines.IBM_ONLY))) {
								if (bInternal && !bClientRole){
									bAdd = true;
									break;
								}
							 } else if (userprivs.contains(privs.elementAt(p))) {
								bAdd = true;
								break;
							}
						}
					}
					if (bAdd)
						graphTabs.addElement(graphTabs1.elementAt(igt));
				}
			}
			// END OF BLADE
			
			if (graphTabs.size() == 0) {
				sHeader.append("There are no folders associated with this project.");
				return;
			}
			
			int topCatId = 0;
			
			String topCatStr = ETSUtils.checkNull(DocumentsHelper.getTopCatID(request)); 	//request.getParameter("tc");
			if (!topCatStr.equals("")) {
				topCatId = (new Integer(topCatStr)).intValue();
			} else {
				topCatId = ((ETSCat) graphTabs.elementAt(0)).getId();
				topCatStr = String.valueOf(topCatId);
			}
			
			ETSCat topCat = (ETSCat) ETSDatabaseManager.getCat(topCatId,con);
			
			setTopCat(topCat);
			
			String sPrinterFriendly = request.getParameter("skip");
			if (sPrinterFriendly == null) {
				sPrinterFriendly = "N";
			} else {
				sPrinterFriendly = sPrinterFriendly.trim();
			}
			
			String sArchive = "";
			if (proj.getProject_status().equalsIgnoreCase("A")) {
				// archieved workspace.
				sArchive = " [ <span style=\"color:#cc6600\">Archived</span> ]";
			}
			
			boolean bAdmin = false;
			
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equalsIgnoreCase(Defines.ETS_ADMIN)) {
				bAdmin = true;
			}
			//es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT, "tg_member=MD");
			boolean bExecutive = false;
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),con).equalsIgnoreCase(Defines.ETS_EXECUTIVE)) {
				bExecutive = true;
			}
			//es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD");			
			
			int iCount = 0;
			
			if (bAdmin || bExecutive) {
				iCount = 2;
			} else {
				iCount = getWorkspaceCount(con,es.gIR_USERN, proj.getProjectType());
			}
			
			String sBegin = "";
			String sITAR = "";
			String sSwitch = "";
			
			String strParentWS = "";
			if (!proj.getParent_id().equalsIgnoreCase("0")) {
			    ETSProj parentProj = ETSUtils.getProjectDetails(con, proj.getParent_id());
			    strParentWS = "<br />(Parent workspace is <a href=\""+Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + parentProj.getProjectId()+ "&linkid=" + sLink + "\">" + parentProj.getName() + "</a>)"; 
				sBegin = "Subworkspace for ";
			} else {
				sBegin = "Workspace for ";
			}
			
			String strITARWarning = "";
			
			if (proj.isITAR()) {
			    strITARWarning = "<br /><span style=\"color:#ff3333\">ITAR information contained in this workspace is export restricted and must only be accessed when physically located in the U.S.</span>";
			}
			if (proj.getProjectOrProposal().trim().equalsIgnoreCase("P")) {
				EdgeHeader.setPageTitle(unBrandedProp.getAppName()+ " - " + proj.getName() + " - " + topCat.getName());
				EdgeHeader.setHeader(unBrandedProp.getProjectTitle());
				EdgeHeader.setSubHeader(sBegin + proj.getName() + sArchive + strParentWS + strITARWarning);
			} else if (proj.getProjectOrProposal().trim().equalsIgnoreCase("O")) {
				EdgeHeader.setPageTitle(unBrandedProp.getAppName()+ " - " + proj.getName() + " - " + topCat.getName());
				EdgeHeader.setHeader(unBrandedProp.getProposalTitle());
				EdgeHeader.setSubHeader(sBegin  + proj.getName() + sArchive + strParentWS + strITARWarning);
			} else if (proj.getProjectOrProposal().trim().equalsIgnoreCase("M")) {
				EdgeHeader.setPageTitle(unBrandedProp.getAppName()+ " - " + proj.getName() + " - " + topCat.getName());
				EdgeHeader.setHeader(unBrandedProp.getMetricsTitle());
				EdgeHeader.setSubHeader("Workspace for metrics");
			} else if (proj.getProjectOrProposal().trim().equalsIgnoreCase("C")) {
				EdgeHeader.setPageTitle(unBrandedProp.getAppName() + " - Client Voice - " + proj.getCompany() + " - " + topCat.getName());
				EdgeHeader.setHeader(unBrandedProp.getClientVoiceTitle());
				EdgeHeader.setSubHeader(sBegin  + proj.getName() + sArchive + strParentWS + strITARWarning);
			}

			
			this.sHeader.insert(0, EdgeHeader.printSubHeader());
			this.sHeader.insert(0, EdgeHeader.printBullsEyeLeftNav());

			String curCatStr = ETSUtils.checkNull(DocumentsHelper.getCurrentCatID(request));	// request.getParameter("cc");
			if(curCatStr != null) {
				curCatStr = curCatStr.trim();
				if(curCatStr.length() == 0){
					curCatStr = null;
				}
			}
			if (curCatStr != null && topCat.getViewType() == Defines.DOCUMENTS_VT) {
				try {
					Integer.parseInt(curCatStr);
					String sCatName = getCatName(con, curCatStr); // get the cat name
					this.sHeader.insert(0, ETSSearchCommon.getMasthead(EdgeHeader, proj, topCat, sLink, curCatStr, sCatName));
				}
				catch(NumberFormatException e) {
					String sCatName = getCatName(con, topCatStr);
					this.sHeader.insert(0, ETSSearchCommon.getMasthead(EdgeHeader, proj, topCat, sLink, topCatStr, sCatName));
				}
			} else {
				this.sHeader.insert(0, ETSSearchCommon.getMasthead(EdgeHeader, proj, topCat, sLink));
			}

			//copy only header without tabstr to another var sOnlyTabStr
			this.sOnlyHeader.append(this.sHeader.toString());
			
			if (sPrinterFriendly.trim().equalsIgnoreCase("N")) { //SPN451
				
				if(proj.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)){
					if((es.gDECAFTYPE.trim().toUpperCase().equals("I")) && (isProjectIBMOnly(proj.getProjectId(),con).equalsIgnoreCase("N"))){ 
						this.sTabStr.append("<table><tr><td align=\"right\"><span style=\"color:#006400;font-size:10\"><b>Note: This workspace may also be used by external customers.</b></span></td></tr></table>");
					}
				}

				if (iCount > 1) {
					this.sTabStr.append("<table><tr><td align=\"right\" ><a href=\"" + Defines.SERVLET_PATH + unBrandedProp.getLandingPageURL() + "?linkid=" + sLink + "\">" + unBrandedProp.getSwitchWorkspaceName() + "</a></td></tr></table>");
				}
				
				// top table to define the content and right sides..
				this.sTabStr.append("<script type=\"text/javascript\" language=\"javascript\" src=\""+Global.WebRoot+"/js/UserTimeZoneCookie.js\"></script>");
				this.sTabStr.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				
			/*	this.sTabStr.append("<tr valign=\"top\"><td width=\"443\" valign=\"top\" class=\"small\"><table summary=\"\" width=\"100%\"><tr><td width=\"60%\">" + es.gIR_USERN + "</td>" +
						"<td headers=\"\" width=\"40%\" align=\"right\"><div id=\"sTime\">" + sDate + "<a href=\""
					+ Defines.SERVLET_PATH
					+ "ETSHelpServlet.wss?proj=ETSTimeZone\" target=\"new\" onclick=\"window.open('"
					+ Defines.SERVLET_PATH
					+ "ETSHelpServlet.wss?proj=ETSTimeZone','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=400,height=400,left=300,top=150'); return false;\" onkeypress=\"window.open('"
					+ Defines.SERVLET_PATH
					+ "ETSHelpServlet.wss?proj=ETSTimeZone','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=400,height=400,left=300,top=150'); return false;\">" 
					+ "<img src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" alt=\"Timezone help\" border=\"0\" /></a></div></td>");
				*/
				
				//Server's Time				
				this.sTabStr.append("<tr valign=\"top\"><td width=\"443\" valign=\"top\" class=\"small\">" +
						"<table summary=\"\" width=\"100%\"><tr><td width=\"60%\">" + es.gIR_USERN + "</td>" +	
						"<td headers=\"\" width=\"25%\" align=\"right\"><div id=\"sTime\">" + sDate +"</div></td>");
			
					
				this.sTabStr.append("<script type=\"text/javascript\"  language=\"javascript\">"); 
				this.sTabStr.append("var newTime;");				
				this.sTabStr.append("  newTime =firstTime();");				
				this.sTabStr.append("document.getElementById(\"sTime\").innerHTML = newTime;");
				this.sTabStr.append("</script>");
				

				this.sTabStr.append("</tr></table></td>");
				this.sTabStr.append("<td width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				if (proj.isITAR()) {
					this.sTabStr.append("<td class=\"small\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td width=\"16\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + proj.getProjectType() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + proj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + proj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><span style=\"color:#ff3333\">This workspace contains ITAR unclassified data</span></a></td></tr></table></td></tr>");
				} else {
					this.sTabStr.append("<td class=\"small\" align=\"right\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td width=\"16\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\" width=\"90\" align=\"right\">Secure content</td></tr></table></td></tr>");
				}
				
				this.sTabStr.append("<tr valign=\"top\"><td width=\"443\" valign=\"top\">");
				this.sTabStr.append(createGraphTabs(graphTabs, topCatId, sLink,con));
				
				//copy tab str to header
				this.sHeader.append(this.sTabStr.toString());				
			}
			
			if (sPrinterFriendly.trim().equalsIgnoreCase("N")){  //SPN451
				if (!proj.isITAR()) {
					this.sFooter.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td width=\"16\" align=\"left\"><img alt=\"protected content\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\" align=\"left\"><span class=\"fnt\">A key icon displayed in a page indicates that the page is secure and password-protected.</span></td></tr></table>");
				}
			}
			
			this.sFooter.append(EdgeHeader.printBullsEyeFooter());
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (con != null) {
				ETSDBUtils.close(con);
			}
		}
		
	}

	/**
	  * 03/04/2004
	  * changed by Navneet
	  * changed signature of method: createGraphTabs from private to public static to enable other classes to use it
	  * also the instance variable: databaseManager was changed to static
	  * and its assignment was moved out of init() to enable this change
	  */
	 public static StringBuffer createGraphTabs(Vector graphTabs, int topCat, String sLink,Connection con) {

		 StringBuffer buf = new StringBuffer();

		 int size = graphTabs.size();
		 int indexCat = 0;
		 ETSCat tCat = (ETSCat) graphTabs.elementAt(0);
		 boolean foundflag = false;

		 if (topCat != 0) {
			 try {
				 //System.err.println("inside try");
				 tCat = (ETSCat) ETSDatabaseManager.getCat(topCat,con);
				 if (tCat != null) {
					 for (int i = 0; i < size; i++) {
						 ETSCat c = (ETSCat) graphTabs.elementAt(i);
						 if (c.getId() == topCat) {
							 foundflag = true;
							 indexCat = i;
							 break;
						 }
					 }
				 }

				 //indexCat = graphTabs.indexOf(tCat);
				 if (!foundflag) {
					 System.err.println("******************* in !foundflag");
					 indexCat = 0;
					 tCat = (ETSCat) graphTabs.elementAt(0);
				 }
			 } catch (SQLException se) {
				 System.out.println("sqlex in createGrTabs. e=" + se);
				 indexCat = 0;
				 tCat = (ETSCat) graphTabs.elementAt(0);
			 }
		 }

		 buf.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		 buf.append("<tr>");
		 int iTabSize = graphTabs.size();
	     Vector tmpTopTabs = new Vector();
	     Vector tmpLowerTabs = new Vector();
		 for(int i=0; i < graphTabs.size(); i++) {
	         ETSCat cat = (ETSCat) graphTabs.elementAt(i);
	         if (cat.getId() == topCat) {
	             if (tmpTopTabs.size() >= MAX_TOP_TABS) {
	                 tmpLowerTabs.insertElementAt(tmpTopTabs.elementAt(MAX_TOP_TABS-1), 0);
	                 tmpTopTabs.removeElementAt(MAX_TOP_TABS-1);
	             }
                 tmpTopTabs.add(cat);
	         }
	         else {
	             if (tmpTopTabs.size() >= MAX_TOP_TABS) {
	                 tmpLowerTabs.add(cat);
	             }
	             else {
	                 tmpTopTabs.add(cat);
	             }
	         }
	     }
		 for(int i=0; i < tmpTopTabs.size(); i++) {
		     String gtabClass = "";
		     String aClass = "";
		     String tdClass = "";
		     ETSCat cat = (ETSCat) tmpTopTabs.elementAt(i);
		     boolean bIsTopCat = (cat.getId() == topCat);
		     boolean bIsNextCatTopCat = false;
		     if (i < (tmpTopTabs.size() -1)) {
		         ETSCat nextcat = (ETSCat) tmpTopTabs.elementAt(i+1);
		         if (nextcat.getId() == topCat) {
		             bIsNextCatTopCat = true;
		     	 }
		     }
		     String url = "";
			 if (cat.getName().equals(EtsIssFilterConstants.ETSISSUECATHEADER)) {
				 url = "EtsIssFilterCntrlServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink + "&istyp=iss&opn=10";
			 } else if (cat.getName().equalsIgnoreCase("Feedback")) {
				 url = "ETSProjectsServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink + "&actionType=feedback&subactionType=welcome";
			 } else {
				 url = "ETSProjectsServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink;
			 }

			 if (bIsTopCat) {
		         gtabClass = "v14-graphic-tab-selected";
		         aClass="v14-tab-link-selected";
		     }
		     else {
		         gtabClass="v14-graphic-tab-unselected";
		         aClass="v14-tab-link-unselected";
		     }
			 if (i==0) {
			     if (bIsTopCat) {
				     buf.append("<td class=\"v14-graphic-tab-selected\"><img class=\"display-img\" alt=\"\" height=\"19\" width=\"9\" src=\"//www.ibm.com/i/c.gif\" /></td>");
			     }
			     else {
				     buf.append("<td class=\"v14-graphic-tab-unselected\"><img class=\"display-img\" alt=\"\" height=\"19\" width=\"9\" src=\"//www.ibm.com/i/c.gif\" /></td>");
			     }
			 }
			 buf.append("<td class=\""+gtabClass+"\"><a class=\""+aClass+"\" href=\""+url+"\">"+cat.getName()+"</a></td>");
			 if (i < (tmpTopTabs.size()-1)) {
				 if (bIsTopCat) {
					 if (i != (tmpTopTabs.size() -1)) {
					     buf.append("<td class=\"v14-tab-hlrt\"><img alt=\"\" height=\"1\" width=\"30\" src=\"//www.ibm.com/i/c.gif\"/></td>");
					 }
					 else {
					     //buf.append("<td class=\"v14-tab-hlrt-end\"><img alt=\"\" height=\"1\" width=\"30\" src=\"//www.ibm.com/i/c.gif\"/></td>");
					 }
				 }
				 else {
					 if (bIsNextCatTopCat) {
					     buf.append("<td class=\"v14-tab-hllt\"><img alt=\"\" height=\"1\" width=\"30\" src=\"//www.ibm.com/i/c.gif\"/></td>");
					 }
					 else {
					     buf.append("<td class=\"v14-tab-dmrt\"><img alt=\"\" height=\"1\" width=\"30\" src=\"//www.ibm.com/i/c.gif\"/></td>");   
					 }
				 }
			 }
			 else {
			     if (bIsTopCat) {
			         buf.append("<td class=\"v14-tab-hlrt-end\"><img alt=\"\" height=\"1\" width=\"30\" src=\"//www.ibm.com/i/c.gif\"/></td>");
			     }
			     else {
			         buf.append("<td class=\"v14-tab-dmrt-end\"><img alt=\"\" height=\"1\" width=\"30\" src=\"//www.ibm.com/i/c.gif\"/></td>");
			     }
			 }
		 }
		 StringBuffer strLowerTabs = new StringBuffer("");
		 if (tmpLowerTabs.size() > 0) {
			 strLowerTabs.append("More tabs (");
			 for(int i=0; i < tmpLowerTabs.size(); i++) {
			     ETSCat cat = (ETSCat) tmpLowerTabs.elementAt(i);
			     String url = "";
			 if (cat.getName().equals(EtsIssFilterConstants.ETSISSUECATHEADER)) {
					 url = "EtsIssFilterCntrlServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink + "&istyp=iss&opn=10";
			 } else if (cat.getName().equalsIgnoreCase("Feedback")) {
					 url = "ETSProjectsServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink + "&actionType=feedback&subactionType=welcome";
			 } else {
					 url = "ETSProjectsServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink;
			 }
				 strLowerTabs.append("&nbsp;");
				 strLowerTabs.append("<a href=\""+ url +"\" class=\"link\">" + cat.getName() + "</a>");
				 if (i < tmpLowerTabs.size()-1) {
				     strLowerTabs.append("&nbsp;|");
		 }
		 }
			 strLowerTabs.append("&nbsp;)");
		 }
		 buf.append("</tr>");
		 buf.append("</table>");
		 buf.append("<table width=\"443\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		 buf.append("<tr>");
		 buf.append("<td class=\"v14-graphic-tab-selected\" align=\"right\"><img src=\"//www.ibm.com/i/c.gif\" width=\"443\" height=\"4\" alt=\"\" class=\"display-img\" /></td>");
		 buf.append("</tr>");
		 buf.append("<tr>");
		 buf.append("<td class=\"data\" align=\"right\">" + strLowerTabs + "</td>");
		 buf.append("</tr>");
		 buf.append("<tr>");
		 buf.append("<td class=\"data\" align=\"right\"><img src=\"//www.ibm.com/i/c.gif\" width=\"443\" height=\"4\" alt=\"\" class=\"display-img\" /></td>");
		 buf.append("</tr>");
		 buf.append("</table>");

		 return buf;
	 }


	/**
	 * Method getCatName.
	 * @param conn
	 * @param projectidStr
	 * @param curCatStr
	 * @return String
	 */
	private String getCatName(Connection conn, String curCatStr) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sCatName = "";

		try {


			sQuery.append("SELECT CAT_NAME FROM ETS.ETS_CAT WHERE CAT_ID=" + curCatStr + " with ur");

			logger.debug("ETSHeaderFooter::getCatName()::QUERY : " + sQuery.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {
				 sCatName = ETSUtils.checkNull(rs.getString("CAT_NAME"));
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return sCatName;

	}	

	/**
	 * @return
	 */
	public StringBuffer getFooter() {
		return this.sFooter;
	}

	/**
	 * @return
	 */
	public StringBuffer getHeader() {
		return this.sHeader;
	}

	/**
	 * @return
	 */
	public ETSCat getTopCat() {
		return this.topCat;
	}

	/**
	 * @param cat
	 */
	public void setTopCat(ETSCat cat) {
		this.topCat = cat;
	}

	private static int getWorkspaceCount(Connection con, String sUserId, String sProjectType) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "' AND ACTIVE_FLAG = '" + Defines.USER_ENTITLED + "') AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' AND PROJECT_TYPE = '" + sProjectType + "' with ur");

			logger.debug("ETSConnectServlet::getWorkspaceCount::Query : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			if (rs.next()) {

				iCount = rs.getInt(1);
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return iCount;
	}
	
	public static String isProjectIBMOnly(String strProjectId, Connection con)
	throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		String isProjIBMonly = "";
		try {

			sQuery.append("SELECT IBM_ONLY FROM ETS.ETS_PROJECTS WHERE PROJECT_ID = ? with ur");
			
			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1,strProjectId);
			
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				isProjIBMonly = rs.getString(1).trim();
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		
		return isProjIBMonly;

	}
	
	public StringBuffer getOnlyHeader() {
		return this.sOnlyHeader;
	}

	public StringBuffer getTabStr() {
		return this.sTabStr;
	}	

}
