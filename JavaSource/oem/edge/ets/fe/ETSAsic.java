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

/**
 * Author: Sandra Nava
 * Date: 1/21/2004
 */

package oem.edge.ets.fe;

import oem.edge.common.*;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URLEncoder;

import oem.edge.amt.*;
import oem.edge.ets.fe.dealtracker.*;

public class ETSAsic {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "4.4.1";


    protected ETSParams Params;
    protected ETSProj Project;
    protected int TopCatId;
    protected String linkid;
    protected Connection conn;
    protected EdgeAccessCntrl es;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected PrintWriter writer;
	protected boolean isSuperAdmin;
	protected boolean isExecutive;
	protected String userRole;
	protected boolean shownIbmonly;
		
    protected ETSDatabaseManager databaseManager;
    protected int CurrentCatId;
	protected ETSCat this_current_cat;
	static private final String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
	static private final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	static private final SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");

    ETSAsic(ETSParams parameters){
		this.Params = parameters;
		this.Project = parameters.getETSProj();
		this.TopCatId = parameters.getTopCat();
		this.linkid = parameters.getLinkId();
		this.conn = parameters.getConnection();
		this.es = parameters.getEdgeAccessCntrl();
		this.request = parameters.getRequest();
		this.response = parameters.getResponse();
		this.writer = parameters.getWriter();
		this.isSuperAdmin = parameters.isSuperAdmin();
		this.isExecutive = parameters.isExecutive();
		
		this.databaseManager = new ETSDatabaseManager();
		String currentCatIdStr = ETSDealTrackerCommonFuncs.getParameter(request,"cc");
		if (!currentCatIdStr.equals("")){
		    this.CurrentCatId = (new Integer(currentCatIdStr)).intValue();
		}
		else{
		    this.CurrentCatId = TopCatId;
		}
	}


	public void ETSAsicHandler(){
	
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		String action = ETSDealTrackerCommonFuncs.getParameter(request,"action");
	
	
		try{
			this_current_cat = ETSDatabaseManager.getCat(CurrentCatId);
			this.userRole = ETSUtils.checkUserRole(es,Project.getProjectId());
	
		}
		catch(Exception e){
			this_current_cat = null;
		}
		if (this_current_cat == null){
			writer.println("invalid folder: current category is null");
			return;
		}
	
		boolean user_external = false;
		if (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I")){
			    user_external = true;
		}
	
		Vector notEditor = new Vector(); 
		notEditor.addElement(Defines.WORKSPACE_VISITOR);
		notEditor.addElement(Defines.ETS_EXECUTIVE);
		notEditor.addElement(Defines.WORKSPACE_CLIENT);
	
		if (!action.equals("")){
			ETSDealTracker dt = new ETSDealTracker(Params);
			ETSDealTrackerResultObj resobj = new ETSDealTrackerResultObj();
			resobj.setBackStr("'ASIC' main");
			dt.ETSTrackerHandler(resobj);
		}
		else{
		
			StringBuffer b = new StringBuffer();
			
			printHeader();
			ETSDealTrackerResultObj resobj = new ETSDealTrackerResultObj();
			
			if (action.equals("")){
				b.append(doDefaultView(resobj,user_external,notEditor));	
			}
			writer.println(b.toString());
		}
	
	}



	private StringBuffer doDefaultView(ETSDealTrackerResultObj o,boolean user_external,Vector notEditor){
		StringBuffer buf = new StringBuffer();
	
		//buf.append("<img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" />");
		
		buf.append("<table border=\"0\" width=\"443\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("<tr><td align=\"right\"><table border=\"0\" ellspacing=\"0\" cellpadding=\"0\">");
		buf.append("<tr><td align=\"right\" valign=\"middle\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "d.gif\" width=\"16\" height=\"16\" border=\"0\" /></td>");
		buf.append("<td align=\"right\" valign=\"middle\"><a href=\"#actitems\">Action items</a></td>");
		buf.append("</tr>");
		buf.append("</table></tr></td></table>");
		
		buf.append(getAsicLinks(user_external));
		buf.append("<a name=\"actitems\" id=\"actitems\" href=\"#actitems\"></a>");
		buf.append("<table cellspacing=\"0\" cellpadding=\"1\" border=\"0\" width=\"600\">");
		buf.append("<tr><td><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" /></td></tr>");
		buf.append("<tr valign=\"middle\"><td width=\"600\" valign=\"middle\" class=\"tblue\" height=\"18\">");
		buf.append("&nbsp;Action items</td></tr></table>");
		buf.append(doDashboardView(o,user_external,false,notEditor));
		//buf.append(printDashboardButtons(o,false,o.getResultTasks().size()>0,user_external));
		
		
		return buf;
	}




	public StringBuffer doDashboardView(ETSDealTrackerResultObj o, boolean user_external,boolean printview,Vector notEditor){
		
		boolean hasIbmOnly = false;
		boolean hasTaskShown = false;
	
	
		String sortby = request.getParameter("sort_by");
		String ad = request.getParameter("sort");

		if (sortby == null) {
			sortby = Defines.SORT_BY_DT_TASKID_STR;
		}
		if (ad == null) {
			ad = Defines.SORT_ASC_STR;
		}

		o.setSortBy(sortby);
		o.setAD(ad);

		ETSDealTrackerFunctions funcs = new ETSDealTrackerFunctions(Params);
		o = funcs.getDashboardTasks(o,userRole,user_external,printview);
		
		   
		return ETSDealTrackerPrint.doDashboardView(o,userRole,user_external,printview,Params,notEditor);
    		 
		
	}

	public StringBuffer printDashboardButtons(ETSDealTrackerResultObj resobj,boolean printview,boolean hasTaskShown,boolean user_external,Vector notEditor){
		StringBuffer buf = new StringBuffer();
	
		if(!printview){
			buf.append("<tr><td colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td colspan=\"7\"><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			buf.append("<tr><td colspan=\"7\">");
			buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>\n");
				
			//if (!(userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR))){
			if (!notEditor.contains(userRole)){
				buf.append("<td valign=\"top\" algin=\"right\"><a href=\"ETSProjectsServlet.wss?action=addtask&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"add new task\" /></a></td>");
				buf.append("<td valign=\"top\" algin=\"left\"><a href=\"ETSProjectsServlet.wss?action=addtask&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"\" class=\"fbox\">Add task</a></td>");
				if (!user_external){
					buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
					buf.append("<td valign=\"top\" algin=\"right\"><a href=\"ETSProjectsServlet.wss?action=additask&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"add IBM only task\" /></a></td>");
					buf.append("<td valign=\"top\" algin=\"left\"><a href=\"ETSProjectsServlet.wss?action=additask&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"\" class=\"fbox\">Add IBM only task</a></td>");				
				}
				
				if (hasTaskShown){
					buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
					buf.append("<td valign=\"top\" algin=\"right\"><a href=\"ETSProjectsServlet.wss?action=editdash&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"edit this view\" /></a></td>");
					buf.append("<td valign=\"top\" algin=\"left\"><a href=\"ETSProjectsServlet.wss?action=editdash&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"\" class=\"fbox\">Edit this view</a></td>");
				}
				buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
			}	
	
			
			buf.append("<td valign=\"top\" algin=\"right\">" +
				"<a href=\"ETSProjectsServlet.wss?action=print&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"\" class=\"fbox\" target=\"new\" " +
					"onclick=\"window.open('ETSProjectsServlet.wss?action=print&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=650,height=500,top=200');return false;\" " +
					"onkeypress=\"window.open('ETSProjectsServlet.wss?action=print&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=650,height=500,top=200');return false;\">" +
				"<img src=\"" + Defines.ICON_ROOT + "print.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"printable version\" /></a></td>");
			buf.append("<td valign=\"top\" algin=\"left\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=print&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"\" class=\"fbox\" target=\"new\" " +
				"onclick=\"window.open('ETSProjectsServlet.wss?action=print&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=650,height=500,top=200');return false;\" " +
				"onkeypress=\"window.open('ETSProjectsServlet.wss?action=print&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+TopCatId+"&linkid="+linkid+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=650,height=500,top=200');return false;\">Printable version</a></td>");
				
			buf.append("</tr></table>\n");
			buf.append("</td></tr>\n");
			
			buf.append("<tr><td colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("</td></tr></table>\n");
		}
		return buf;
		}

	private StringBuffer getAsicLinks(boolean user_external){
		StringBuffer s = new StringBuffer();
		shownIbmonly = false;
		s.append("<img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" />");
		s.append(drawTable("Initial Engagemenet","Design and Release",ETSAsicLinkList.getInitEngagementList(),ETSAsicLinkList.getDesignReleaseList(),user_external));
		s.append("<img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" />");
		s.append(drawTable("Prototype and Production","General Resources",ETSAsicLinkList.getPrototypeProductionList(),ETSAsicLinkList.getGeneralResourcesList(),user_external));
		if(shownIbmonly){
			s.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"443\">");
			s.append("<tr>");
			s.append("<td class=\"small\"><span class=\"ast\"><b>*</b></span>Denotes IBM/IBM Partner link</td>");
			s.append("</tr>");
			s.append("</table>");
		}
		return s;
	}


	private StringBuffer drawTable(String aTitle, String bTitle,ETSAsicLinkListValue[] a,ETSAsicLinkListValue[] b,boolean user_external){
		StringBuffer s = new StringBuffer();
		StringBuffer stemp = new StringBuffer();
		
	
		int aCnt = ETSAsicLinkList.getCount(a);
		int bCnt = ETSAsicLinkList.getCount(b);
		
		s.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"443\">");
		s.append("<tr class=\"tblue\" height=\"18\">");
		s.append("<td colspan=\"2\" width=\"218\">&nbsp;"+aTitle+"</td>");
		s.append("<td width=\"7\">&nbsp;</td>");
		s.append("<td colspan=\"2\" width=\"218\">"+bTitle+"</td>");
		s.append("</tr>");
		s.append("</table>");
				
		s.append("<table cellspacing=\"0\" cellpadding=\"1\" border=\"0\" width=\"443\">");
		s.append("<tr>");
		s.append("<td headers=\"\" valign=\"top\" width=\"100%\" style=\"background-color:#98b1c4\">");
		s.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"background-color:#ffffff\">");
		s.append("<tr valign=\"top\">");
		s.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"3\" /></td>");
		s.append("</tr>");
		
		int ai =0,bi=0;
		boolean aprint=false,bprint=false;
		boolean print1=true;
		
		while (ai<aCnt || bi<bCnt){
			aprint = false;
			bprint = false;
			stemp = new StringBuffer();
			stemp.append("<tr>");
			
			while (ai<aCnt && !aprint){
				if (!a[ai].isRestriced() || es.userLinks.contains(a[ai].getLinkId())){
					aprint= true;
					stemp.append("<td rowspan=\"2\" valign=\"top\" width=\"16\"><img src=\""+Defines.ICON_ROOT+"popup.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"\" /></td>");
					String title = "<a href=\""+Global.getUrl(a[ai].getLink())+"\" target=\"new\">"+a[ai].getTitle()+"</a>";
					/*if (a[ai].isRestriced()){
						title = title +"<span class=\"ast\">*</span>";
						shownRes = true;
					}*/
					if (a[ai].isIbmOnly()){
						title = title +"<span class=\"ast\">*</span>";
						shownIbmonly = true;
					}
					stemp.append("<td valign=\"top\" width=\"202\">"+title+"</td>");
				}
				else
					ai++;
			}
		
			if (!aprint){
				stemp.append("<td rowspan=\"2\" valign=\"top\" width=\"16\">&nbsp;</td>");
				stemp.append("<td rowspan=\"2\"  valign=\"top\" width=\"202\">&nbsp;</td>");
			}	
			
			stemp.append("<td rowspan=\"2\" width=\"7\" align=\"center\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" width=\"1\" height=\"18\" alt=\"\" border=\"0\" /></td>");
			
			while (bi<bCnt && !bprint){
			
				if (!b[bi].isRestriced() || es.userLinks.contains(b[bi].getLinkId())){
					bprint= true;
					stemp.append("<td rowspan=\"2\" valign=\"top\" width=\"16\"><img src=\""+Defines.ICON_ROOT+"popup.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"\" /></td>");
					String title = "<a href=\""+Global.getUrl(b[bi].getLink())+"\" target=\"new\">"+b[bi].getTitle()+"</a>";
					/*if (b[bi].isRestriced()){
						title = title + "<span class=\"ast\">*</span>";
						shownRes = true;	
					}*/
					if (b[bi].isIbmOnly()){
						title = title + "<span class=\"ast\">*</span>";
						shownIbmonly = true;	
					}
					stemp.append("<td valign=\"top\" width=\"202\">"+title+"</td>");
				}
				else
					bi++;
			}
		
			if (!bprint){
				stemp.append("<td rowspan=\"2\" valign=\"top\" width=\"16\">&nbsp;</td>");
				stemp.append("<td rowspan=\"2\"  valign=\"top\" width=\"202\">&nbsp;</td>");
			}	
			
			stemp.append("</tr>");
			
			stemp.append("<tr>");
			if (aprint){
				stemp.append("<td valign=\"top\">"+a[ai].getDescription()+"</td>");
			}
			if (bprint){
				stemp.append("<td valign=\"top\">"+b[bi].getDescription()+"</td>");
			}
			stemp.append("</tr>");
			
			
			stemp.append("<tr valign=\"top\">");
			stemp.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"3\" /></td>");
			stemp.append("</tr>");

			if (aprint || bprint){
				ai++;
				bi++;
				if(!print1){
					s.append("<tr valign=\"top\">");
				  	s.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" width=\"441\" height=\"1\" /></td>");
				  	s.append("</tr>");
					s.append("<tr valign=\"top\">");
					s.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"3\" /></td>");
					s.append("</tr>");
				}
				print1=false;
				s.append(stemp);
			}


		} 
		s.append("<tr valign=\"top\">");
		s.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"3\" /></td>");
		s.append("</tr>");
		s.append("</table>");
		s.append("</td></tr></table>");
		
		return s;
	}

/*
	private StringBuffer drawTable(String aTitle, String bTitle,ETSAsicLinkListValue[] a,ETSAsicLinkListValue[] b){
		StringBuffer s = new StringBuffer();
		
	
		int aCnt = ETSAsicLinkList.getCount(a);
		int bCnt = ETSAsicLinkList.getCount(b);
		
		s.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"443\">");
		s.append("<tr class=\"tblue\">");
		s.append("<td colspan=\"2\" width=\"218\">"+aTitle+"</td>");
		s.append("<td width=\"7\">&nbsp;</td>");
		s.append("<td colspan=\"2\" width=\"218\">"+bTitle+"</td>");
		s.append("</tr>");
		s.append("</table>");
				
		s.append("<table cellspacing=\"0\" cellpadding=\"1\" border=\"0\" width=\"443\">");
		s.append("<tr>");
		s.append("<td headers=\"\" valign=\"top\" width=\"100%\" style=\"background-color:#98b1c4\">");
		s.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"background-color:#ffffff\">");
		s.append("<tr valign=\"top\">");
		s.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"3\" /></td>");
		s.append("</tr>");
		
		for (int i = 0; i<aCnt || i<bCnt; i++){
			s.append("<tr>");
			if (i<aCnt){
				s.append("<td rowspan=\"2\" valign=\"top\" width=\"16\"><img src=\""+Defines.ICON_ROOT+"popup.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"\" /></td>");
				s.append("<td valign=\"top\" width=\"202\">"+a[i].getTitle()+"</td>");
			}
			else{
				s.append("<td rowspan=\"2\" valign=\"top\" width=\"16\">&nbsp;</td>");
				s.append("<td rowspan=\"2\"  valign=\"top\" width=\"202\">&nbsp;</td>");
			}
			s.append("<td rowspan=\"2\" width=\"7\" align=\"center\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" width=\"1\" height=\"18\" alt=\"\" border=\"0\" /></td>");
			if (i<bCnt){
				s.append("<td rowspan=\"2\" valign=\"top\" width=\"16\"><img src=\""+Defines.ICON_ROOT+"popup.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"\" /></td>");
				s.append("<td valign=\"top\" width=\"202\">"+b[i].getTitle()+"</td>");
			}
			else{
				s.append("<td rowspan=\"2\" valign=\"top\" width=\"16\">&nbsp;</td>");
				s.append("<td rowspan=\"2\"  valign=\"top\" width=\"202\">&nbsp;</td>");
			}
			s.append("</tr>");
			
			s.append("<tr>");
			if (i<aCnt){
				s.append("<td valign=\"top\">"+a[i].getDescription()+"</td>");
			}
			if (i<bCnt){
				s.append("<td valign=\"top\">"+b[i].getDescription()+"</td>");
			}
			s.append("</tr>");
			
			s.append("<tr valign=\"top\">");
			s.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"3\" /></td>");
			s.append("</tr>");

			if ((i+1)<aCnt || (i+1)< bCnt){
				s.append("<tr valign=\"top\">");
				s.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" width=\"441\" height=\"1\" /></td>");
				s.append("</tr>");
			}

			s.append("<tr valign=\"top\">");
			s.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"3\" /></td>");
			s.append("</tr>");

		} 
		
		s.append("</table>");
		s.append("</td></tr></table>");
		
		return s;
	}*/


	private StringBuffer getTopVerbage(){
		StringBuffer s = new StringBuffer();
		
		s.append("IBM's chip design portal providing a comprehensive suite of " +			"e-business applications supporting all stages of the ASIC engagement.<br /><br />");
		s.append("You must be registered and entitled to acess each e-business application.  Please " +			"contact your IBM ASIC representative for additional access.<br /><br />");
		
		return s;	
	}



	private void printHeader(){
		StringBuffer buf = new StringBuffer();
			
		try{
			System.out.println("here");
			//gutter between content and right column
			writer.println("<td rowspan=\"4\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			// Right column start
			writer.println("<td rowspan=\"4\" width=\"150\" valign=\"top\">");
			ETSContact contact = new ETSContact(Project.getProjectId(), request);
			contact.printContactBox(writer);
			writer.println("</td></tr>");
		}
		catch(Exception e){
			
		}
		
		buf.append("<tr valign=\"middle\"><td width=\"443\" valign=\"middle\" class=\"tblue\" height=\"18\">");
		buf.append("&nbsp;ASIC Connect</td></tr>");
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\" class=\"small\">");
		buf.append(getTopVerbage()+"</td></tr>");
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\">");
		buf.append("<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"443\" valign=\"bottom\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>");				
		writer.println(buf.toString());
	
	}

} // end of class







