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
 * Date: 1/24/2005
 */

package oem.edge.ets.fe.dealtracker;

import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSComparator;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;

public class ETSSelfDealTrackerPrint {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "4.4.1";
	
	static private final String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
	static private final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	static private final SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");
	static private final String[] rel_attr = new String[]{"Quality","Delivery","Cost/Price","Technology & design","Reponsiveness","Key leverages","Positive efforts"};
	


	static public StringBuffer doDashboardView(ETSDealTrackerResultObj resobj,String userRole,boolean user_external,boolean printview,ETSParams params,boolean editablePlan,boolean showClose,Vector notEditor){
		
		StringBuffer buf = new StringBuffer();
		boolean hasIbmOnly = false;
		boolean hasTaskShown = false;
		
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		buf.append("<tr><td>");
		buf.append("<br />");
		
		String cvStr = "&self="+resobj.getSelfId()+"&etsop=action";
		if (resobj.isSetMet()){
			cvStr = "&set="+resobj.getSelfId()+"&etsop=action";
		}
    
		Vector tasks= new Vector();
		tasks = resobj.getResultTasks();
		String userid = params.getEdgeAccessCntrl().gIR_USERN;
		
		try{
			int w1=12;  //img
			int w2=30;	//id
			int w3=175; //title
			int w4=80;  //section
			int w5=153; //owner
			int w6=75;  //date
			int w7=85;   //status
		
			buf.append("<table summary=\"\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
			
			if(!printview)
				buf.append("<tr><td headers=\"\" colspan=\"7\" class=\"small\">Click on the column heading to sort</td></tr>\n");
		
			buf.append("<tr><td headers=\"\" colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");

			buf.append("<tr><th id=\"taskid\" colspan=\"2\" align=\"left\" valign=\"middle\" height=\"16\">");
			//sort by taskid
			if (resobj.getSortBy().equals(Defines.SORT_BY_DT_TASKID_STR)){ 
			   if (resobj.getAD().equals(Defines.SORT_ASC_STR)){
				   buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   if (!printview){
					   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_TASKID_STR+"&sort="+Defines.SORT_DES_STR+"\" class=\"fbox\">");
					   buf.append("Task</a>");
				   }
				   else{
						buf.append("Task");
			
				   }
				   buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
				   buf.append("</table></td>");
			   }
			   else{
				   buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   if (!printview){
					   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_TASKID_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
					   buf.append("Task</a>");
				   }
				   else{
						buf.append("Task");
				   }
				   buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");				
				   buf.append("</table></td>");
			   }
			  }
			else{
				if (!printview){
				   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_TASKID_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
				   buf.append("Task</a></th>");
				}
				else{
					buf.append("Task</th>");
				}
			}
			//sort by title
			buf.append("<th id=\"title\" align=\"left\" valign=\"middle\" height=\"16\">");
			if (resobj.getSortBy().equals(Defines.SORT_BY_DT_TITLE_STR)){ 
			   if (resobj.getAD().equals(Defines.SORT_ASC_STR)){
				   buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   if (!printview){
					   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_TITLE_STR+"&sort="+Defines.SORT_DES_STR+"\" class=\"fbox\">");
					   buf.append("Title</a>");
				   }
				   else{
						buf.append("Title");
				   }		   
				   buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
				   buf.append("</table></td>");
			   }
			   else{
				   buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   if(!printview){
					   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_TITLE_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
					   buf.append("Title</a>");
				   }
				   else{
						buf.append("Title");
				   }			   
				   buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");				
				   buf.append("</table></td>");
			   }
			  }
			else{
				if (!printview){
				   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_TITLE_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
				   buf.append("Title</a></th>");
				}
				else{
					buf.append("Title</th>");	
				}
		
			}
	
			//sort by section
			buf.append("<th id=\"section\" align=\"left\" valign=\"middle\" height=\"16\">");
			if (resobj.getSortBy().equals(Defines.SORT_BY_DT_SECT_STR)){ 
			   if (resobj.getAD().equals(Defines.SORT_ASC_STR)){
				   buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   if(!printview){
					   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_SECT_STR+"&sort="+Defines.SORT_DES_STR+"\" class=\"fbox\">");
					   buf.append("Section</a>");
				   }
				   else{
						buf.append("Section");
				   }
				   buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
				   buf.append("</table></td>");
			   }
			   else{
				   buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   if(!printview){
					   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_SECT_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
					   buf.append("Section</a>");
				   }
				   else{
						buf.append("Section");
				   }
				   buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");				
				   buf.append("</table></td>");
			   }
			  }
			else{
				if (!printview){
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_SECT_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
					buf.append("Section</a></th>");
				}
				else{
					buf.append("Section</th>");	
				}
			}

			//sort by owner
			buf.append("<th id=\"section\" align=\"left\" valign=\"middle\" height=\"16\">");
			if (resobj.getSortBy().equals(Defines.SORT_BY_DT_OWNER_STR)){ 
			   if (resobj.getAD().equals(Defines.SORT_ASC_STR)){
				   buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   if (!printview){
					   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_OWNER_STR+"&sort="+Defines.SORT_DES_STR+"\" class=\"fbox\">");
					   buf.append("Owner</a>");
				   }
				   else{
						buf.append("Owner");
				   }
				   buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
				   buf.append("</table></td>");
			   }
			   else{
				   buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   if (!printview){
					   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_OWNER_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
					   buf.append("Owner</a>");
				   }
				   else{
						buf.append("Owner");
				   }
				   buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
				   buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");				
				   buf.append("</table></td>");
			   }
			  }
			else{
				if (!printview){
				   buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_OWNER_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
				   buf.append("Owner</a></th>");
				}
				else{
					buf.append("Owner</th>");	
				}

			}
		
		
			//sort by duedate
			buf.append("<th id=\"section\" align=\"left\" valign=\"middle\" height=\"16\">");
			if (resobj.getSortBy().equals(Defines.SORT_BY_DT_DATE_STR)){ 
				if (resobj.getAD().equals(Defines.SORT_ASC_STR)){
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
					if (!printview){
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_DATE_STR+"&sort="+Defines.SORT_DES_STR+"\" class=\"fbox\">");
						buf.append("Date</a>");
					}
					else{
						buf.append("Date");	
					}
					buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></td>");
				}
				else{
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
					if (!printview){
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_DATE_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
						buf.append("Date</a>");
					}
					else{
						buf.append("Date");	
					}
					buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");				
					buf.append("</table></td>");
				}
			}
			else{
				if (!printview){
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_DATE_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
					buf.append("Date</a></th>");
				}
				else{
					buf.append("Date</th>");	
				}
		
			}
		
		
			//sort by status
			buf.append("<th id=\"section\" align=\"left\" valign=\"middle\" height=\"16\">");
			if (resobj.getSortBy().equals(Defines.SORT_BY_DT_STATUS_STR)){ 
				if (resobj.getAD().equals(Defines.SORT_ASC_STR)){
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
					if (!printview){
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_STATUS_STR+"&sort="+Defines.SORT_DES_STR+"\" class=\"fbox\">");
						buf.append("Status</a>");
					}
					else{
						buf.append("Status");
					}
					buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></td>");
				}
				else{
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
					if (!printview){
						buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_STATUS_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
						buf.append("Status</a>");
					}
					else{
						buf.append("Status");	
					}
					buf.append("</td><td headers=\"\" align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");				
					buf.append("</table></td>");
				}
			}
			else{
				if (!printview){
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&sort_by="+Defines.SORT_BY_DT_STATUS_STR+"&sort="+Defines.SORT_ASC_STR+"\">");
					buf.append("Status</a></th>");
				}
				else{
					buf.append("Status</th>");
				}	
		
			}

			buf.append("</tr>\n");	    
		

			boolean owner_submitter = false;
	    
			if (tasks.size()>0){
				if (resobj.getSortBy().equals(Defines.SORT_BY_DT_OWNER_STR)){
					byte sortOrder = ETSComparator.getSortOrder(resobj.getSortBy());
					byte sortAD = ETSComparator.getSortBy(resobj.getAD()); 	
					Collections.sort(tasks,new ETSComparator(sortOrder,sortAD)); 
				}
        
				boolean gray_flag = true;
				
				for (int t=0; t<tasks.size();t++){
					ETSTask task = (ETSTask)tasks.elementAt(t);
					task = new ETSSelfTask(task);	
					
					if (!(task.isIbmOnly() && (user_external || userRole.equals(Defines.WORKSPACE_CLIENT)))){
						if (task.getCreatorId().equals(userid) || task.getOwnerId().equals(userid))
							owner_submitter = true;
						
						hasTaskShown = true;
						if (gray_flag){
							buf.append("<tr style=\"background-color:#eeeeee\" height=\"19\">");
							gray_flag=false;
						}
						else{
							buf.append("<tr height=\"19\">");
							gray_flag=true;
						}
	
		    	
						if (task.hasDocs()){
							buf.append("<td headers=\"\" width=\""+w1+"\" align=\"left\"><img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>"); //img
						}
						else{
							buf.append("<td headers=\"\" width=\""+w1+"\" align=\"left\">&nbsp;</td>"); //img
						}
						buf.append("<td headers=\"\" width=\""+w2+"\"  align=\"left\">"+task.getId()+"</td>");
						buf.append("<td headers=\"\" width=\""+w3+"\" >");
						if (!printview)
							buf.append("<a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"\" class=\"fbox\">"+task.getTitle()+"</a>");
						else
							buf.append(task.getTitle());
						
						System.out.println("id="+task.getId()+"  io="+task.isIbmOnly()+"  sm="+resobj.isSetMet());
	
						if (task.isIbmOnly() && resobj.isSetMet()){
							buf.append("<span class=\"ast\"><b>*</b></span></td>");
							hasIbmOnly = true;
						}
						else
							buf.append("</td>");
							
						buf.append("<td headers=\"\" width=\""+w4+"\" >"+task.getSection()+"</td>");
						buf.append("<td headers=\"\" width=\""+w5+"\" >"+ETSUtils.getUsersName(params.getConnection(),task.getOwnerId()));
						buf.append("</td>");
	
						java.util.Date date = new java.util.Date(task.getDueDate());
						String dateStr = df.format(date);
						buf.append("<td headers=\"\" width=\""+w6+"\" >"+dateStr+"</td>");
						buf.append("<td headers=\"\" width=\""+w7+"\" >"+task.getStatusColor()+"</td>");
						buf.append("</tr>\n");
					}		    	
				}
	    
			}
			
			if (!hasTaskShown){
				buf.append("<tr><td headers=\"\" colspan=\"7\"style=\"background-color:#eeeeee\">There are no current tasks.</td></tr>");
			}
			
			buf.append(printDashboardButtons(resobj,userRole,params,printview,hasTaskShown,user_external,editablePlan,showClose,notEditor,owner_submitter));
			
			
			if (resobj.isSetMet() && hasIbmOnly)
				buf.insert(0,"<span class=\"ast\"><b>*</b></span><span class=\"small\">Denotes IBM only task</span><br /><br />");
			else if (!resobj.isSetMet())
				buf.insert(0,"<span class=\"ast\"><b>*</b></span><span class=\"small\">All tasks are restricted</span><br /><br />");
			
			
		}
		catch(Exception e){
			System.out.println("exception="+e);
			e.printStackTrace();
		
		}
    		 
		return buf;
	}


	static public StringBuffer printDashboardButtons(ETSDealTrackerResultObj resobj,String userRole,ETSParams params, boolean printview,boolean hasTaskShown,boolean user_external, boolean editablePlan,boolean showClose,Vector notEditor,boolean owner_submitter){
		StringBuffer buf = new StringBuffer();
		
		String cvStr = "&self="+resobj.getSelfId()+"&etsop=action";
		String cvStr_close = "&self="+resobj.getSelfId()+"&etsop=closeap";
		if (resobj.isSetMet()){
			cvStr = "&set="+resobj.getSelfId()+"&etsop=action";
			cvStr_close = "&set="+resobj.getSelfId()+"&etsop=closeap";
		}
	
		if(!printview){
				
			buf.append("<tr><td headers=\"\" colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td headers=\"\" colspan=\"7\"><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td headers=\"\" colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			buf.append("<tr><td headers=\"\" colspan=\"7\">");
			buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>\n");
				
			//if ((!(userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR) || userRole.equals(Defines.WORKSPACE_CLIENT) || user_external)) && editablePlan){
			if (!(notEditor.contains(userRole)) && (resobj.isSetMet() || !user_external) && editablePlan){
				String addIbmStr = "Add task";
				if (resobj.isSetMet()){
					addIbmStr = "Add IBM only task";
					buf.append("<td headers=\"\" valign=\"top\" algin=\"right\"><a href=\"ETSProjectsServlet.wss?action=addtask&proj="+params.getETSProj().getProjectId()+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+cvStr+"&linkid="+params.getLinkId()+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"add new task\" /></a></td>");
					buf.append("<td headers=\"\" valign=\"top\" algin=\"left\"><a href=\"ETSProjectsServlet.wss?action=addtask&proj="+params.getETSProj().getProjectId()+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+cvStr+"&linkid="+params.getLinkId()+"\" class=\"fbox\">Add task</a></td>");
					buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
				}
				
				if (!user_external && !userRole.equals(Defines.WORKSPACE_CLIENT)){
					buf.append("<td headers=\"\" valign=\"top\" algin=\"right\"><a href=\"ETSProjectsServlet.wss?action=additask&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\""+addIbmStr+"\" /></a></td>");
					buf.append("<td headers=\"\" valign=\"top\" algin=\"left\"><a href=\"ETSProjectsServlet.wss?action=additask&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"\" class=\"fbox\">"+addIbmStr+"</a></td>");				
					buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
				}
				
				if (hasTaskShown && (owner_submitter || params.isSuperAdmin() || userRole.equals(Defines.WORKSPACE_OWNER))){
					buf.append("<td headers=\"\" valign=\"top\" algin=\"right\"><a href=\"ETSProjectsServlet.wss?action=editdash&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"edit this view\" /></a></td>");
					buf.append("<td headers=\"\" valign=\"top\" algin=\"left\"><a href=\"ETSProjectsServlet.wss?action=editdash&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"\" class=\"fbox\">Edit this view</a></td>");
					buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
				}
			}	
	
			if (showClose){
				buf.append("<td headers=\"\" valign=\"top\" algin=\"right\"><a href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr_close+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"close action plan\" /></a></td>");
				buf.append("<td headers=\"\" valign=\"top\" algin=\"left\"><a href=\"ETSProjectsServlet.wss?proj="+params.getETSProj().getProjectId()+cvStr_close+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"\" class=\"fbox\">Close action plan</a></td>");
				buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
			}
	
	
			buf.append("<td headers=\"\" valign=\"top\" algin=\"right\">" +
				"<a href=\"ETSProjectsServlet.wss?action=print&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"\" class=\"fbox\" target=\"new\" " +
					"onclick=\"window.open('ETSProjectsServlet.wss?action=print&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=650,height=500,top=200');return false;\" " +
					"onkeypress=\"window.open('ETSProjectsServlet.wss?action=print&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=650,height=500,top=200');return false;\">" +
				"<img src=\"" + Defines.ICON_ROOT + "print.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"printable version\" /></a></td>");
			buf.append("<td headers=\"\" valign=\"top\" algin=\"left\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=print&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"\" class=\"fbox\" target=\"new\" " +
				"onclick=\"window.open('ETSProjectsServlet.wss?action=print&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=650,height=500,top=200');return false;\" " +
				"onkeypress=\"window.open('ETSProjectsServlet.wss?action=print&proj="+params.getETSProj().getProjectId()+cvStr+"&tc="+params.getTopCat()+"&cc="+params.getTopCat()+"&linkid="+params.getLinkId()+"&skip=Y&sort_by="+resobj.getSortBy()+"&sort="+resobj.getAD()+"','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=650,height=500,top=200');return false;\">Printable version</a></td>");
				
				
			buf.append("</tr></table>\n");
			buf.append("</td></tr>\n");
			
			buf.append("<tr><td headers=\"\" colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("</td></tr></table>\n");
		}
		return buf;
	}



static public String doAddTask(ETSParams params, int CurrentCatId, Vector notEditors,ETSDealTrackerResultObj o,Hashtable h, boolean ibmonly,String msg){
	StringBuffer buf = new StringBuffer();
	int w0 = 3;
	int w1 = 100;
	int w2 = 50; 
	int w3 = 85; 
	int w4 = 50;
	int w5 = 82; 
	int w6 = 60; 
	int w7 = 80; 
	int rspacer = 5;
	
	String cvStr = "&self="+o.getSelfId()+"&etsop=action";
	if (o.isSetMet())
		cvStr = "&set="+o.getSelfId()+"&etsop=action";

	
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();
	
	buf.append("<form action=\"ETSProjectsServlet.wss\" name=\"addTaskForm\" method=\"post\">\n");
	if(ibmonly)
		buf.append("<input type=\"hidden\" name=\"action\" value=\"additask2\" />");
	else
		buf.append("<input type=\"hidden\" name=\"action\" value=\"addtask2\" />");
			
	buf.append("<input type=\"hidden\" name=\"proj\" value=\""+Project.getProjectId()+"\" />");
	buf.append("<input type=\"hidden\" name=\"tc\" value=\""+TopCatId+"\" />");
	buf.append("<input type=\"hidden\" name=\"cc\" value=\""+CurrentCatId+"\" />");
	buf.append("<input type=\"hidden\" name=\"linkid\" value=\""+linkid+"\" />");
	buf.append("<input type=\"hidden\" name=\"etsop\" value=\"action\" />");
	if(o.isSetMet())
		buf.append("<input type=\"hidden\" name=\"set\" value=\""+o.getSelfId()+"\" />");
	else
		buf.append("<input type=\"hidden\" name=\"self\" value=\""+o.getSelfId()+"\" />");

	if(!msg.equals("")){
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("<tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr>");
		buf.append("</table>\n");
	}
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">\n");
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" width=\""+w0+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w1+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w2+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w3+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w4+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w5+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w6+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w7+"\">&nbsp;</td>");
	buf.append("</tr>\n");

	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"title\"><b>Task title:</b></label></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\"><input id=\"title\" type=\"text\" name=\"title\" size=\"42\" style=\"width:300px\" width=\"300px\" maxlength=\"100\" value=\""+ETSDealTrackerCommonFuncs.getHashStrValue(h,"title")+"\" class=\"iform\" /></td>");
	buf.append("</tr>\n");
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\""+rspacer+"\" width=\"1\" alt=\"\" /></td></tr>\n");
		
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"date\"><b>Due date:</b></label></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\">Month:</td>\n");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\">\n");
		
	String sm = ETSDealTrackerCommonFuncs.getHashStrValue(h,"month");
	int im = -1;
	if (!sm.equals("")){
		im = Integer.parseInt(sm);	
	}
	buf.append("<select name=\"month\" id=\"date\" class=\"iform\">");
	buf.append("<option value=\"0\">&nbsp;</option>");
	for (int m = 0; m < 12; m++){
		if(im==m){
			buf.append("<option value=\""+m+"\" selected=\"selected\">"+months[m]+"</option>");
		}
		else{
			buf.append("<option value=\""+m+"\">"+months[m]+"</option>");
		}
	}
	buf.append("</select>\n");
	buf.append("</td>");


	buf.append("<td headers=\"\" align=\"right\" valign=\"top\">Day:</td>\n");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\">\n");
	String sd = ETSDealTrackerCommonFuncs.getHashStrValue(h,"day");
	int idy = -1;
	if (!sd.equals("")){
		idy = Integer.parseInt(sd);	
	}
	buf.append("&nbsp;&nbsp;");
	buf.append("<select name=\"day\" id=\"date\" class=\"iform\">");
	buf.append("<option value=\"0\">&nbsp;</option>");
	for (int d = 1; d <= 31; d++){
		if(idy==d){
			buf.append("<option value=\""+d+"\" selected=\"selected\">"+d+"</option>");
		}
		else{
			buf.append("<option value=\""+d+"\">"+d+"</option>");
		}
	}
	buf.append("</select>\n");
	buf.append("</td>");
		
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\">Year:</td>\n");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\">\n");
	String sy = ETSDealTrackerCommonFuncs.getHashStrValue(h,"year");
	int iy = -1;
	if (!sy.equals("")){
		iy = Integer.parseInt(sy);	
	}
	Calendar cal = Calendar.getInstance();
	int year = (cal.get(Calendar.YEAR)) - 1;

	buf.append("&nbsp;&nbsp;");
	buf.append("<select name=\"year\" id=\"date\" class=\"iform\">");
	buf.append("<option value=\"0\">&nbsp;</option>");
	for (int c = year; c <= year+4; c++){
		if(iy==c){
			buf.append("<option value=\""+c+"\" selected=\"selected\">"+c+"</option>");
		}
		else{
			buf.append("<option value=\""+c+"\">"+c+"</option>");
		}
	}
	buf.append("</select>\n");
	buf.append("</td>");
	buf.append("</tr>\n");
		
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\""+rspacer+"\" width=\"1\" alt=\"\" /></td></tr>\n");
		
		
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></label></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"status\"><b>Status:</b></label></td>");
	String ss = ETSDealTrackerCommonFuncs.getHashStrValue(h,"status");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">");
	buf.append("<select name=\"status\" id=\"status\" class=\"iform\">");
	if (ss.equals(Defines.GREEN))
		buf.append("<option value=\""+Defines.GREEN+"\" selected=\"selected\">Complete</option>");
	else
		buf.append("<option value=\""+Defines.GREEN+"\">Complete</option>");
	
	if (ss.equals(Defines.YELLOW))
		buf.append("<option value=\""+Defines.YELLOW+"\" selected=\"selected\">In progress</option>");
	else
		buf.append("<option value=\""+Defines.YELLOW+"\">In progress</option>");

	if (ss.equals(Defines.RED))
		buf.append("<option value=\""+Defines.RED+"\" selected=\"selected\">Not started</option>");
	else
		buf.append("<option value=\""+Defines.RED+"\">Not started</option>");
	buf.append("</select>\n");
	buf.append("</td>");
	buf.append("</tr>\n");
		
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\""+rspacer+"\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"owner\"><b>Owner:</b></label></td>");
	String so = ETSDealTrackerCommonFuncs.getHashStrValue(h,"owner");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">\n");
	Vector users = new Vector();
	try{
		//users = ETSDatabaseManager.getProjMembersWithOutPriv(Project.getProjectId(),Defines.VISITOR,true, conn);
		//if (ibmonly){
		//	users = ETSDealTrackerCommonFuncs.getOwnMembers(Project,users,conn,ibmonly,notEditors);
		//}
	
		users = ETSDatabaseManager.getProjMembersWithOutPriv(Project.getProjectId(),Defines.VISITOR,true, conn);
		users = ETSDealTrackerCommonFuncs.getOwnMembers(Project,users,conn,ibmonly,notEditors);
		
		if (users.size()>0){
			buf.append("<select name=\"owner\" id=\"owner\" class=\"iform\">");
			buf.append("<option value=\"0\">&nbsp;</option>");
			for (int i =0; i<users.size(); i++){
				ETSUser user = (ETSUser)users.elementAt(i);
				if (user.getUserId().equals(so))
					buf.append("<option value=\""+user.getUserId()+"\" selected=\"selected\">"+ETSUtils.getUsersName(conn,user.getUserId())+" ["+user.getUserId()+"]</option>");
				else
					buf.append("<option value=\""+user.getUserId()+"\">"+ETSUtils.getUsersName(conn,user.getUserId())+" ["+user.getUserId()+"]</option>");
			}
			buf.append("</select>\n");
		}
		else{
			buf.append("No valid owners");
		}
	}
	catch(Exception e){
		buf.append("error getting memebers");
		System.out.println("error="+e);
	
	}
	buf.append("</td>");
	buf.append("</tr>\n");
		
	
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\""+rspacer+"\" width=\"1\" alt=\"\" /></td></tr>\n");

	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"company\"><b>Company:</b></label></td>");
	String sc = ETSDealTrackerCommonFuncs.getHashStrValue(h,"company");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">\n");
	buf.append("<select name=\"company\" id=\"company\" class=\"iform\">");
	buf.append("<option value=\"0\">&nbsp;</option>");
	if (sc.equals("IBM"))
		buf.append("<option value=\"IBM\" selected=\"selected\">IBM</option>");
	else
		buf.append("<option value=\"IBM\">IBM</option>");

	if(!Project.getCompany().equals("IBM")){
		if (sc.equals(Project.getCompany()))
			buf.append("<option value=\""+Project.getCompany()+"\" selected=\"selected\">"+Project.getCompany()+"</option>");
		else
			buf.append("<option value=\""+Project.getCompany()+"\">"+Project.getCompany()+"</option>");
	
		if (sc.equals("Both"))
			buf.append("<option value=\"Both\" selected=\"selected\">Both</option>");
		else
			buf.append("<option value=\"Both\">Both</option>");
	}
	buf.append("</select>\n");

	buf.append("</td>");
	buf.append("</tr>\n");


	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>\n");
		
		
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"section\"><b>Relevant attributes:</b></label>&nbsp;&nbsp;</td>");
	String ssection = ETSDealTrackerCommonFuncs.getHashStrValue(h,"section");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">");
	buf.append("<select name=\"section\" id=\"section\" class=\"iform\">");
	for (int ra = 0; ra<rel_attr.length;ra++){
		if (ssection.equals(rel_attr[ra]))
			buf.append("<option value=\""+rel_attr[ra]+"\" selected=\"selected\">"+rel_attr[ra]+"</option>");
		else
			buf.append("<option value=\""+rel_attr[ra]+"\">"+rel_attr[ra]+"</option>");
	}
	buf.append("</select>\n");
	buf.append("</td>");
	buf.append("</tr>\n");
		
		
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>\n");
		
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"2\"><label for=\"areq\"><b>Action required:</b></label></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">");
	String sareq = ETSDealTrackerCommonFuncs.getHashStrValue(h,"areq");
	buf.append("<textarea rows=\"5\" cols=\"30\" id=\"areq\" name=\"areq\" style=\"width:300px\" width=\"300px\" class=\"iform\">"+sareq+"</textarea>");
	buf.append("</td>");
	buf.append("</tr>\n");

	buf.append("</table>\n");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");
	buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
		
	buf.append("</form>\n");
	//writer.println(buf.toString());
	return buf.toString();
}



static public String doAddTask2(ETSParams params,int CurrentCatId,ETSDealTrackerResultObj o){
	StringBuffer buf = new StringBuffer();
		
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();		
		
	if (o.getErrorFlag()){
		//error page	
		return "error";
	}
	else{
		ETSSelfTask task = new ETSSelfTask(o.getTask());
			
		String str = "";
		if(o.getTrackerType().equals("A") || o.getTrackerType().equals("S"))
			str = "&self="+o.getSelfId();
		else if (o.getTrackerType().equals("M"))
			str = "&set="+o.getSelfId();
		
		buf.append("<br /><br />");	
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
		buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">You have successfully added task:</td></tr>");
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Task:</b></td><td headers=\"\" align=\"left\">"+task.getId()+"</td></tr>");
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Relavent attribute:</b></td><td headers=\"\" align=\"left\">"+task.getSection()+"</td></tr>");
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Title:</b></td><td headers=\"\" align=\"left\">"+task.getTitle()+"</td></tr>");
		try{
			buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\">"+ETSUtils.getUsersName(conn,task.getOwnerId())+"</td></tr>");
		}
		catch(Exception e){
			buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\">"+task.getOwnerId()+"</td></tr>");
		}			
		buf.append("</table>");

		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>\n");
		
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
		buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+Project.getProjectId()+str+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
		buf.append("<img src=\"" + Defines.ICON_ROOT + "fw_c.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Edit Task\" /></a></td>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+Project.getProjectId()+str+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Work with this task</a></td>");

		buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
		buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+str+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
		buf.append("<img src=\"" + Defines.ICON_ROOT + "bk.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Back to main\" /></a></td>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+str+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Back to action plan</a></td>");
		buf.append("</tr>\n");
		buf.append("</table>\n");
		//writer.println(buf.toString());
		return buf.toString();
	}
}


static public String printTaskDetails(ETSParams params, int CurrentCatId,String userRole,EdgeAccessCntrl es,boolean isEditablePlan,ETSDealTrackerResultObj o, boolean user_external,Vector notEditor){
//get task
//see if task internal and user external
//print the details

  ETSProj Project = params.getETSProj();
  int TopCatId = params.getTopCat();
  String linkid = params.getLinkId();
  Connection conn = params.getConnection();


  String cvStr = "&self="+o.getSelfId()+"&etsop=action";
  if (o.isSetMet()){
	  cvStr = "&set="+o.getSelfId()+"&etsop=action";
  }

	boolean canEdit = false;
	StringBuffer buf = new StringBuffer();
					
	try{
		ETSSelfTask task = new ETSSelfTask(o.getTask());
			
		//if (isEditablePlan && (params.isSuperAdmin() || task.getOwnerId().equals(es.gIR_USERN) || task.getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER) || userRole.equals(Defines.WORKSPACE_MANAGER)))
		if (isEditablePlan && (ETSDealTrackerCommonFuncs.canEdit(es,params.isSuperAdmin(),task.isIbmOnly(),userRole,notEditor,task.getCreatorId(),task.getOwnerId(),user_external)))
			canEdit = true;
			
		int w1=110;
		int w2=180;
		int w3=150;
		int w4=160;
			
			
		if(task.isIbmOnly() && o.isSetMet()){
			buf.append("<br /><span style=\"color:#ff3333\">This task is marked IBM Only.</span>");
		}
		buf.append("<br /><br />");
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
		buf.append("<tr><td headers=\"\" align=\"left\" width=\""+w1+"\"><b>Status:</b></td><td headers=\"\" colspan=\"3\">"+task.getStatusColor()+"</td></tr>");
			
		buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		
		buf.append("<tr><td headers=\"\" align=\"left\" width=\""+w1+"\"><b>Task:</b></td><td headers=\"\" align=\"left\">"+task.getId()+"</td>");
		buf.append("<td headers=\"\" align=\"left\" width=\""+w3+"\"><b>Relavent attribute:</b></td><td headers=\"\" align=\"left\" width=\""+w4+"\">"+task.getSection()+"</td></tr>");
		buf.append("<tr><td headers=\"\" align=\"left\" width=\""+w1+"\"><b>Title:</b></td><td headers=\"\" colspan=\"3\" align=\"left\">"+task.getTitle()+"</td></tr>");
		java.util.Date date = new java.util.Date(task.getDueDate());
		String dateStr = df.format(date);
		buf.append("<tr><td headers=\"\" align=\"left\" width=\""+w1+"\"><b>Due date:</b></td><td headers=\"\" align=\"left\" width=\""+w2+"\">"+dateStr+"</td>");
		buf.append("<td headers=\"\" colspan=\"2\">&nbsp;</td></tr>");
		buf.append("<tr><td headers=\"\" align=\"left\" width=\""+w1+"\"><b>Submitter:</b></td><td headers=\"\" colspan=\"3\" align=\"left\">"+ETSUtils.getUsersName(conn,task.getCreatorId())+"</td></tr>");
		buf.append("<tr><td headers=\"\" align=\"left\" width=\""+w1+"\"><b>Owner:</b></td><td headers=\"\" colspan=\"3\" align=\"left\">"+ETSUtils.getUsersName(conn,task.getOwnerId())+"</td></tr>");
		buf.append("<tr><td><b>Company:</b></td><td headers=\"\" colspan=\"3\" align=\"left\">"+task.getCompany()+"</td></tr>");
	
		buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
			
		buf.append("<tr><td headers=\"\" align=\"left\" width=\""+w1+"\" valign=\"top\"><b>Action required:</b></td><td headers=\"\" colspan=\"3\" align=\"left\">"+task.getActionRequired()+"</td></tr>");
		buf.append("</table>");
			
		if (canEdit){
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");				
			buf.append("<tr><td headers=\"\" colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			buf.append("<tr><td headers=\"\" align=\"right\" width=\"16\"><img src=\""+Defines.ICON_ROOT+"fw.gif\" height=\"16\" width=\"16\" alt=\"\" /></td>");
			buf.append("<td headers=\"\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=edetails&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Edit task details</a></td>");
		
			buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"10\" alt=\"\" /></td>\n");
			buf.append("<td headers=\"\" align=\"right\" width=\"16\"><img src=\""+Defines.ICON_ROOT+"fw.gif\" height=\"16\" width=\"16\" alt=\"\" /></td>");
			buf.append("<td headers=\"\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=deltask&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Delete this task</a></td></tr>");
				
			buf.append("</table>");
		}
				
		buf.append("<table summary=\"\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");				
		buf.append("<tr><td headers=\"\" colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr height=\"18\"><td headers=\"\" class=\"tdblue\" colspan=\"5\" align=\"left\">&nbsp;Documents</td></tr>");
		buf.append("<tr>");
		buf.append("<td headers=\"\" align=\"left\"><b>Name</b></td>");
		buf.append("<td headers=\"\" align=\"left\"><b>Date</b></td>");
		buf.append("<td headers=\"\" align=\"left\"><b>Type</b></td>");
		buf.append("<td headers=\"\" align=\"left\"><b>Author</b></td>");
		buf.append("<td headers=\"\" align=\"left\">&nbsp;</td>");
		
		buf.append("</tr>");

		Vector docs = o.getTaskDocs();

		if (docs.size() > 0){
			boolean gray_flag = true;
			for (int i = 0; i < docs.size(); i++){
				if (gray_flag){
					buf.append("<tr style=\"background-color:#eeeeee\">");
					gray_flag=false;
				}
				else{
					buf.append("<tr>");
					gray_flag=true;
				}
				
				ETSDoc doc = (ETSDoc)docs.elementAt(i);
				buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><a href=\"ETSContentDeliveryServlet.wss/"+URLEncoder.encode(doc.getFileName())+"?projid="+Project.getProjectId()+"&docid="+doc.getId()+"&linkid="+linkid+"\" target=\"new\" class=\"fbox\">"+doc.getName()+"</a></td>");
				java.util.Date docdate = new java.util.Date(doc.getUploadDate());
				String docdateStr = df.format(docdate);
				buf.append("<td headers=\"\" align=\"left\" valign=\"top\">"+docdateStr+"</td>");
				buf.append("<td headers=\"\" align=\"left\" valign=\"top\">"+doc.getFileType()+"</td>");
				buf.append("<td headers=\"\" align=\"left\" valign=\"top\">"+ETSUtils.getUsersName(conn,doc.getUserId())+"</td>");
				if(canEdit)
					buf.append("<td headers=\"\" align=\"left\" valign=\"top\" class=\"small\"><a href=\"ETSProjectsServlet.wss?action=deltaskdoc&docid="+doc.getId()+"&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&linkid="+linkid+"\">Delete</a></td>");
				else
					buf.append("<td headers=\"\" class=\"small\">&nbsp;</td>");
				buf.append("</tr>");	
			}
		}
		else{
			buf.append("<tr style=\"background-color:#eeeeee\"><td headers=\"\" colspan=\"5\" align=\"left\">No documents uploaded</td></tr>");	
		}			
		buf.append("</table>");
			
		if (canEdit){
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");				
			buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td headers=\"\" width=\"16\" align=\"right\"><img src=\""+Defines.ICON_ROOT+"fw.gif\" height=\"16\" width=\"16\" alt=\"\" border=\"0\"/></td>");
			buf.append("<td headers=\"\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=addtaskdoc&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Add document</a></td</tr>");
			buf.append("</table>");
		}


		buf.append("<table summary=\"\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");				
		buf.append("<tr><td headers=\"\" colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" height=\"30\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr height=\"18\"><td headers=\"\" class=\"tdblue\" colspan=\"3\" align=\"left\">&nbsp;Comment history</td></tr>");
		buf.append("<tr>");
		buf.append("<td headers=\"\" align=\"left\" width=\"150\"><b>Date</b></td>");
		buf.append("<td headers=\"\" align=\"left\" width=\"150\"><b>Author</b></td>");
		buf.append("<td headers=\"\" align=\"left\" width=\"310\"><b>Comment</b></td>");

		buf.append("</tr>");
			
		Vector coms = o.getTaskComms();
		if (coms.size() > 0){
			boolean cgray_flag = true;
			for (int i = 0; i < coms.size(); i++){
				if (cgray_flag){
					buf.append("<tr style=\"background-color:#eeeeee\">");
					cgray_flag=false;
				}
				else{
					buf.append("<tr>");
					cgray_flag=true;
				}
				ETSTaskComment com = (ETSTaskComment)coms.elementAt(i);
				java.util.Date comdate = new java.util.Date(com.getLastTimestamp());
				String comdateStr = dtf.format(comdate);
				buf.append("<td headers=\"\" align=\"left\" valign=\"top\">"+comdateStr+"</td>");
				buf.append("<td headers=\"\" align=\"left\" valign=\"top\">"+ETSUtils.getUsersName(conn,com.getLastUserid())+"</td>");
				buf.append("<td headers=\"\" align=\"left\" valign=\"top\">"+com.getComment()+"</td>");
				buf.append("</tr>");	
			}
		}
		else{
			buf.append("<tr style=\"background-color:#eeeeee\"><td headers=\"\" colspan=\"3\" align=\"left\">No comments available</td></tr>");	
		}			
		buf.append("</table>");
						
		if (canEdit){	
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");				
			buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td headers=\"\" width=\"16\" align=\"right\"><img src=\""+Defines.ICON_ROOT+"fw.gif\" height=\"16\" width=\"16\" alt=\"\" border=\"0\"/></td>");
			buf.append("<td headers=\"\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=addtaskcomm&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Add comment</a></td</tr>");
			buf.append("</table>");
		}


		int cols = 6;
		//int dw1=12;
		int dw2=35;
		int dw3=195;
		int dw4=80;
		int dw5=155;
		int dw6=90;  
		int dw7=45;
		int dw8=0;

		if (canEdit){
			cols=7;
			dw2=35;
			dw3=190;
			dw4=70;
			dw5=150;
			dw6=80;  
			dw7=40;
			dw8=35;
		}

		buf.append("<table summary=\"\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");				
		buf.append("<tr><td headers=\"\" colspan=\""+cols+"\"><img src=\"//www.ibm.com/i/c.gif\" height=\"30\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr height=\"18\"><td headers=\"\" class=\"tdblue\" colspan=\""+cols+"\" align=\"left\">&nbsp;Dependent tasks</td></tr>");
			
		buf.append("<tr><th id=\"taskid\" align=\"left\" valign=\"top\" height=\"16\">");
		buf.append("Task</a></th>");
		buf.append("<th id=\"title\" align=\"left\" valign=\"top\" height=\"16\">Title</a></th>");
		buf.append("<th id=\"section\" align=\"left\" valign=\"top\" height=\"16\">Section</a></th>");
		buf.append("<th id=\"section\" align=\"left\" valign=\"top\" height=\"16\">Owner</a></th>");
		buf.append("<th id=\"section\" align=\"left\" valign=\"top\" height=\"16\">Date</a></th>");
		buf.append("<th id=\"section\" align=\"left\" valign=\"top\" height=\"16\">Status</a></th>");
		if (canEdit){
			buf.append("<th id=\"section\" align=\"left\" valign=\"top\" height=\"16\">&nbsp;</a></th>");
		}
		buf.append("</tr>\n");	  



		boolean hasDTaskShown = false;
		boolean hasDIbmOnly = false;
		Vector dTasks = o.getDepTasks();
		if (dTasks.size() > 0){
			boolean cgray_flag = true;
			for (int i = 0; i < dTasks.size(); i++){
				ETSSelfTask dTask = new ETSSelfTask((ETSTask)dTasks.elementAt(i));
				if (!(dTask.isIbmOnly() && (user_external|| userRole.equals(Defines.WORKSPACE_CLIENT)))){
					hasDTaskShown = true;
				//}

					if (cgray_flag){
						buf.append("<tr style=\"background-color:#eeeeee\">");
						cgray_flag=false;
					}
					else{
						buf.append("<tr>");
						cgray_flag=true;
					}
	
		    	
					buf.append("<td headers=\"\" width=\""+dw2+"\"  align=\"left\" valign=\"top\">"+dTask.getId()+"</td>");
					buf.append("<td headers=\"\" width=\""+dw3+"\"  valign=\"top\">"+dTask.getTitle());
					if (dTask.isIbmOnly() && o.isSetMet()){
					buf.append("<span class=\"ast\"><b>*</b></span></td>");
						hasDIbmOnly = true;
					}
					else
						buf.append("</td>");
					buf.append("<td headers=\"\" width=\""+dw4+"\" valign=\"top\" >"+dTask.getSection()+"</td>");
					buf.append("<td headers=\"\" width=\""+dw5+"\" valign=\"top\" >"+ETSUtils.getUsersName(conn,dTask.getOwnerId()));
					buf.append("</td>");
	
					java.util.Date dtdate = new java.util.Date(dTask.getDueDate());
					String dtdateStr = df.format(dtdate);
					buf.append("<td headers=\"\" width=\""+dw6+"\" valign=\"top\" >"+dtdateStr+"</td>");
					buf.append("<td headers=\"\" width=\""+dw7+"\" valign=\"top\" >"+dTask.getStatusColor()+"</td>");
						
					if (canEdit){
						buf.append("<td headers=\"\" width=\""+dw8+"\" align=\"left\" valign=\"top\" class=\"small\"><a href=\"ETSProjectsServlet.wss?action=deldtask&dtaskid="+dTask.getId()+"&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&linkid="+linkid+"\">Remove</a></td>");
					}
						
					buf.append("</tr>\n");
				}
			}
		}
		else{
			buf.append("<tr style=\"background-color:#eeeeee\"><td headers=\"\" colspan=\"7\" align=\"left\">No current dependent tasks</td></tr>");	
		}
		if(!hasDTaskShown && dTasks.size()>0){
			buf.append("<tr style=\"background-color:#eeeeee\"><td headers=\"\" colspan=\"7\" align=\"left\">No current dependent tasks</td></tr>");
		}			
		buf.append("</table>");
		
		if (canEdit){	
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");				
			buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td headers=\"\" width=\"16\" align=\"right\"><img src=\""+Defines.ICON_ROOT+"fw.gif\" height=\"16\" width=\"16\" alt=\"\" border=\"0\"/></td>");
			buf.append("<td headers=\"\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=adddtask&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Add dependent task</a></td</tr>");
			buf.append("</table>");
		}
		if (hasDIbmOnly){	
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");				
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td headers=\"\" align=\"left\" class=\"small\"><span class=\"ast\">*</span>Denotes IBM only task</td></tr>");
			buf.append("</table>");
		}
			
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"30\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>\n");
	
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
		buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
		buf.append("<img src=\"" + Defines.ICON_ROOT + "bk_c.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Action plan\" /></a></td>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Back to action plan</a></td>");
		buf.append("</tr>\n");
		buf.append("</table>\n");
		
		//writer.println(buf.toString());
		
	}
	catch(Exception e){
		e.printStackTrace();
	}	
	return buf.toString();
			
}


 static public String doDelTaskDoc(ETSParams params, int CurrentCatId,ETSDealTrackerResultObj o,int docid,int taskid){
	ETSProj Project = params.getETSProj();
	 int TopCatId = params.getTopCat();
	 String linkid = params.getLinkId();
	 Connection conn = params.getConnection();

	StringBuffer buf = new StringBuffer();

	try{
		ETSDoc doc = o.getDoc();
		ETSTask task = o.getTask();
		buf.append("<br /><span style=\"color:#ff3333\">This action can not be undone.</span>");
			
		buf.append("<br />");	
		String cvStr = "&self=";
		if (o.trackerType.equals("M"))
			cvStr = "&set=";
			
		buf.append("<form action=\"ETSProjectsServlet.wss?taskid="+taskid+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&linkid="+linkid+"\" method=\"post\" name=\"deldoc\">");
		buf.append("<input type=\"hidden\" name=\"docid\" value=\""+doc.getId()+"\" />"); 
		buf.append("<input type=\"hidden\" name=\"action\" value=\"deltaskdoc2\" />");
			
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
		buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">You are about to delete the document:</td></tr>");
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Name:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+doc.getName()+"</td></tr>");
			
		java.util.Date date = new java.util.Date(doc.getUploadDate());
		String dateStr = df.format(date); 
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Date:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dateStr+"</td></tr>");
		try{
			buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Author:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+ETSUtils.getUsersName(conn,doc.getUserId())+"</td></tr>");
		}
		catch(Exception e){
			buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Author:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+doc.getUserId()+"</td></tr>");
		}		
			
		buf.append("</table>");
	
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>\n");
	
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
		buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"2\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");

		buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
		buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
		buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
		buf.append("</tr>\n");
		buf.append("</table>\n");
		buf.append("</form>");
		//writer.println(buf.toString());
	}
	catch(Exception e){
	
	}
	return buf.toString();

}


 static public String doDelTaskDoc2(ETSParams params, int CurrentCatId,ETSDealTrackerResultObj o,int docid,int taskid){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();
	 
	 ETSDoc doc = o.getDoc();
	ETSTask task = o.getTask();
		
	String name = doc.getName();
	String author = doc.getUserId();
	java.util.Date date = new java.util.Date(doc.getUploadDate());
	String dateStr = df.format(date);
						
	StringBuffer buf = new StringBuffer();
	String cvStr = "&self=";
	if (task.getTrackerType().equals("M"))
		cvStr = "&set=";
		
	buf.append("<br />");	
	buf.append("<form action=\"ETSProjectsServlet.wss?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&linkid="+linkid+"\" method=\"post\" name=\"deldoc\">");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">You have successfully deleted the document:</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Name:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+name+"</td></tr>");
	buf.append("</td>");
		
		 
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Date:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dateStr+"</td></tr>");
	try{
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Author:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+ETSUtils.getUsersName(conn,author)+"</td></tr>");
	}
	catch(Exception e){
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Author:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+author+"</td></tr>");
	}		
		
	buf.append("</table>");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"2\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"continue\" /></td>");

	//buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	//buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	//buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
	//buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
	buf.append("</form>");
	//writer.println(buf.toString());
	return buf.toString();
		
}

static public String editTaskDetails(ETSParams params, int CurrentCatId,Vector notEditors, ETSDealTrackerResultObj o, Hashtable h,boolean ibmonly, String msg){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();
 
	
	StringBuffer buf = new StringBuffer();
	int w0 = 3;
	int w1 = 100;
	int w2 = 50; 
	int w3 = 85; 
	int w4 = 50;
	int w5 = 82; 
	int w6 = 60; 
	int w7 = 80; 
	int rspacer = 5;
		
	ETSSelfTask t = new ETSSelfTask(o.getTask());
		
	if (h.isEmpty()){
		h = t.getHashTask();
	}
			

	buf.append("<form action=\"ETSProjectsServlet.wss\" name=\"editTaskForm\" method=\"post\">\n");
	buf.append("<input type=\"hidden\" name=\"action\" value=\"edetails2\" />");
			
	buf.append("<input type=\"hidden\" name=\"proj\" value=\""+Project.getProjectId()+"\" />");
	buf.append("<input type=\"hidden\" name=\"taskid\" value=\""+ETSDealTrackerCommonFuncs.getHashStrValue(h,"taskid")+"\" />");		
	buf.append("<input type=\"hidden\" name=\"tc\" value=\""+TopCatId+"\" />");
	buf.append("<input type=\"hidden\" name=\"cc\" value=\""+CurrentCatId+"\" />");
	buf.append("<input type=\"hidden\" name=\"linkid\" value=\""+linkid+"\" />");
	if(o.isSetMet())
		buf.append("<input type=\"hidden\" name=\"set\" value=\""+o.getSelfId()+"\" />");
	else
		buf.append("<input type=\"hidden\" name=\"self\" value=\""+o.getSelfId()+"\" />");
	buf.append("<input type=\"hidden\" name=\"etsop\" value=\"action\" />");


	if(!msg.equals("")){
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("<tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr>");
		buf.append("</table>\n");
	}
		
		
		
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">\n");
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" width=\""+w0+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w1+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w2+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w3+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w4+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w5+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w6+"\">&nbsp;</td>");
	buf.append("<td headers=\"\" width=\""+w7+"\">&nbsp;</td>");
	buf.append("</tr>\n");

	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"title\"><b>Task title:</b></label></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\"><input id=\"title\" type=\"text\" name=\"title\" size=\"42\" style=\"width:300px\" width=\"300px\" maxlength=\"100\" value=\""+ETSDealTrackerCommonFuncs.getHashStrValue(h,"title")+"\" class=\"iform\" /></td>");
	buf.append("</tr>\n");
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\""+rspacer+"\" width=\"1\" alt=\"\" /></td></tr>\n");
		
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"date\"><b>Due date:</b></label></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\">Month:</td>\n");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\">\n");
		
	String sm = ETSDealTrackerCommonFuncs.getHashStrValue(h,"month");
	int im = -1;
	if (!sm.equals("")){
		im = Integer.parseInt(sm);	
	}
	buf.append("<select name=\"month\" id=\"date\" class=\"iform\">");
	buf.append("<option value=\"0\">&nbsp;</option>");
	for (int m = 0; m < 12; m++){
		if(im==m){
			buf.append("<option value=\""+m+"\" selected=\"selected\">"+months[m]+"</option>");
		}
		else{
			buf.append("<option value=\""+m+"\">"+months[m]+"</option>");
		}
	}
	buf.append("</select>\n");
	buf.append("</td>");


	buf.append("<td headers=\"\" align=\"right\" valign=\"top\">Day:</td>\n");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\">\n");
	String sd = ETSDealTrackerCommonFuncs.getHashStrValue(h,"day");
	int idy = -1;
	if (!sd.equals("")){
		idy = Integer.parseInt(sd);	
	}
	buf.append("&nbsp;&nbsp;");
	buf.append("<select name=\"day\" id=\"date\" class=\"iform\">");
	buf.append("<option value=\"0\">&nbsp;</option>");
	for (int d = 1; d <= 31; d++){
		if(idy==d){
			buf.append("<option value=\""+d+"\" selected=\"selected\">"+d+"</option>");
		}
		else{
			buf.append("<option value=\""+d+"\">"+d+"</option>");
		}
	}
	buf.append("</select>\n");
	buf.append("</td>");
		
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\">Year:</td>\n");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\">\n");
	String sy = ETSDealTrackerCommonFuncs.getHashStrValue(h,"year");
	int iy = -1;
	if (!sy.equals("")){
		iy = Integer.parseInt(sy);	
	}
	Calendar cal = Calendar.getInstance();
	int year = (cal.get(Calendar.YEAR)) - 1;

	buf.append("&nbsp;&nbsp;");
	buf.append("<select name=\"year\" id=\"date\" class=\"iform\">");
	buf.append("<option value=\"0\">&nbsp;</option>");
	for (int c = year; c <= year+4; c++){
		if(iy==c){
			buf.append("<option value=\""+c+"\" selected=\"selected\">"+c+"</option>");
		}
		else{
			buf.append("<option value=\""+c+"\">"+c+"</option>");
		}
	}
	buf.append("</select>\n");
	buf.append("</td>");
	buf.append("</tr>\n");
		
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\""+rspacer+"\" width=\"1\" alt=\"\" /></td></tr>\n");
		
		
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></label></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"status\"><b>Status:</b></label></td>");
	String ss = ETSDealTrackerCommonFuncs.getHashStrValue(h,"status");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">");
	buf.append("<select name=\"status\" id=\"status\" class=\"iform\">");
	if (ss.equals(Defines.GREEN))
		buf.append("<option value=\""+Defines.GREEN+"\" selected=\"selected\">Complete</option>");
	else
		buf.append("<option value=\""+Defines.GREEN+"\">Complete</option>");
	
	if (ss.equals(Defines.YELLOW))
		buf.append("<option value=\""+Defines.YELLOW+"\" selected=\"selected\">In progress</option>");
	else
		buf.append("<option value=\""+Defines.YELLOW+"\">In progress</option>");

	if (ss.equals(Defines.RED))
		buf.append("<option value=\""+Defines.RED+"\" selected=\"selected\">Not started</option>");
	else
		buf.append("<option value=\""+Defines.RED+"\">Not started</option>");
	buf.append("</select>\n");
	buf.append("</td>");
	buf.append("</tr>\n");
		
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\""+rspacer+"\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"owner\"><b>Owner:</b></label></td>");
	String so = ETSDealTrackerCommonFuncs.getHashStrValue(h,"owner");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">\n");
	Vector users = new Vector();
	try{
		users = ETSDatabaseManager.getProjMembersWithOutPriv(Project.getProjectId(),Defines.VISITOR,true, conn);
		//users = ETSDealTrackerCommonFuncs.getOwnMembers(users,conn);
		users = ETSDealTrackerCommonFuncs.getOwnMembers(Project,users,conn,t.isIbmOnly(),notEditors);

		if (users.size()>0){
			buf.append("<select name=\"owner\" id=\"owner\" class=\"iform\">");
			buf.append("<option value=\"0\">&nbsp;</option>");
			for (int i =0; i<users.size(); i++){
				ETSUser user = (ETSUser)users.elementAt(i);
				if (user.getUserId().equals(so))
					buf.append("<option value=\""+user.getUserId()+"\" selected=\"selected\">"+ETSUtils.getUsersName(conn,user.getUserId())+" ["+user.getUserId()+"]</option>");
				else
					buf.append("<option value=\""+user.getUserId()+"\">"+ETSUtils.getUsersName(conn,user.getUserId())+" ["+user.getUserId()+"]</option>");
			}
			buf.append("</select>\n");
		}
		else{
			buf.append("No valid owners");
		}
	}
	catch(Exception e){
		buf.append("error getting memebers");
		System.out.println("error="+e);
	
	}
	buf.append("</td>");
	buf.append("</tr>\n");
		
	
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\""+rspacer+"\" width=\"1\" alt=\"\" /></td></tr>\n");

	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"company\"><b>Company:</b></label></td>");
	String sc = ETSDealTrackerCommonFuncs.getHashStrValue(h,"company");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">\n");
	buf.append("<select name=\"company\" id=\"company\" class=\"iform\">");
	buf.append("<option value=\"0\">&nbsp;</option>");
	if (sc.equals("IBM"))
		buf.append("<option value=\"IBM\" selected=\"selected\">IBM</option>");
	else
		buf.append("<option value=\"IBM\">IBM</option>");

	if(!Project.getCompany().equals("IBM")){
		if (sc.equals(Project.getCompany()))
			buf.append("<option value=\""+Project.getCompany()+"\" selected=\"selected\">"+Project.getCompany()+"</option>");
		else
			buf.append("<option value=\""+Project.getCompany()+"\">"+Project.getCompany()+"</option>");
	
		if (sc.equals("Both"))
			buf.append("<option value=\"Both\" selected=\"selected\">Both</option>");
		else
			buf.append("<option value=\"Both\">Both</option>");
	}
	buf.append("</select>\n");

	buf.append("</td>");
	buf.append("</tr>\n");


	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>\n");
		
		
	buf.append("<tr height=\"21\">");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"ast\"><b>*</b></span></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><label for=\"section\"><b>Relevant attributes:</b></label>&nbsp;&nbsp;</td>");
	String ssection = ETSDealTrackerCommonFuncs.getHashStrValue(h,"section");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">");
	buf.append("<select name=\"section\" id=\"section\" class=\"iform\">");
	for (int ra = 0; ra<rel_attr.length;ra++){
		if (ssection.equals(rel_attr[ra]))
			buf.append("<option value=\""+rel_attr[ra]+"\" selected=\"selected\">"+rel_attr[ra]+"</option>");
		else
			buf.append("<option value=\""+rel_attr[ra]+"\">"+rel_attr[ra]+"</option>");
	}
	buf.append("</select>\n");
	buf.append("</td>");
	buf.append("</tr>\n");
		
		
		
	buf.append("<tr><td headers=\"\" colspan=\"8\"><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>\n");
		
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"2\"><label for=\"areq\"><b>Action required:</b></label></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"6\">");
	String sareq = ETSDealTrackerCommonFuncs.getHashStrValue(h,"areq");
	buf.append("<textarea rows=\"5\" cols=\"30\" id=\"areq\" name=\"areq\" style=\"width:300px\" width=\"300px\" class=\"iform\">"+sareq+"</textarea>");
	buf.append("</td>");
	buf.append("</tr>\n");

	buf.append("</table>\n");
		
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");
	
	
	String cvStr = "&self=";
	if (o.isSetMet())
		cvStr = "&set=";
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");
	buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
		
	buf.append("</form>\n");
	//writer.println(buf.toString());
	return buf.toString();
			
}


static public String editTaskDetails2(ETSParams params,int CurrentCatId,ETSTask task,Hashtable h, boolean ibmonly){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();
 	String cvStr = "&self=";
 	if(task.getTrackerType().equals("M"))
 		cvStr = "&set=";
 
	StringBuffer buf = new StringBuffer();
				
	buf.append("<br /><br />");	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">You have successfully updated task:</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Task:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+task.getId()+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Relavent attribute:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+task.getSection()+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Title:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+task.getTitle()+"</td></tr>");
	try{
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+ETSUtils.getUsersName(conn,task.getOwnerId())+"</td></tr>");
	}
	catch(Exception e){
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+task.getOwnerId()+"</td></tr>");
	}			
	buf.append("</table>");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");
	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\" colspan=\"2\"><a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+task.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "continue.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"Task details\" /></a></td>");
		
	buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+task.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.ICON_ROOT + "bk.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Back to action plan\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+task.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Back to action plan</a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
	//writer.println(buf.toString());
	return buf.toString();
}


static public String doAddTaskComment(ETSParams params, int CurrentCatId,ETSTask task, String selfid,String comment,String msg){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	
	String cvStr = "&self=";
	if(task.getTrackerType().equals("M"))
		cvStr = "&set=";
 
	StringBuffer buf = new StringBuffer();
	buf.append("<br /><br />");
	

	buf.append("<form action=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+selfid+"&etsop=action&tc="+TopCatId+"&linkid="+linkid+"\" name=\"addCommForm\" method=\"post\">");
	buf.append("<input type=\"hidden\" name=\"action\" value=\"addtaskcomm2\" />");
	buf.append("<input type=\"hidden\" name=\"taskid\" value=\""+task.getId()+"\" />");
	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr>");
	buf.append("</table>");
	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\"><b>Comment:</b></td></tr>");
	buf.append("<tr height=\"21\"><td><textarea cols=\"50\" rows=\"5\" name=\"comment\">"+comment+"</textarea></td></tr>");
	buf.append("</table>");
	
	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");
	buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+selfid+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+selfid+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
	buf.append("</tr>\n");
	
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("</table>\n");
	
	buf.append("</form");
	
	//writer.println(buf.toString());
	return buf.toString();
}

static public String doAddTaskComment2(ETSParams params, int CurrentCatId, ETSDealTrackerResultObj o){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	
	StringBuffer buf = new StringBuffer();
	buf.append("<br /><br />");	
	ETSTask t = o.getTask();
		
	ETSTaskComment taskcomm = o.getTaskComm();
	
	String cvStr = "&self=";
	if(t.getTrackerType().equals("M"))
		cvStr = "&set=";
		
	buf.append("<br /><br />");	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td>You have successfully added the comment:</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Comment:</b></td></tr>");
	buf.append("<tr><td headers=\"\" align=\"left\">"+taskcomm.getComment()+"</td></tr>");
	buf.append("</table>");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");
	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\" colspan=\"2\"><a href=\"ETSProjectsServlet.wss?action=details&taskid="+t.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "continue.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"Task details\" /></a></td>");
	//buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=edittask&taskid="+task.getId()+"&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Work with this task</a></td>");

	buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.ICON_ROOT + "bk.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Back to main\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Back to action plan</a></td>");
	buf.append("</tr>\n");
	
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("</table>\n");
	//writer.println(buf.toString());
	return buf.toString();	
}


static public String doAddDepTask(ETSParams params, int CurrentCatId,ETSDealTrackerResultObj o,boolean user_external, String msg){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();

	StringBuffer buf = new StringBuffer();
	buf.append("<br /><br />");
	try{
		ETSTask t = o.getTask();

		String cvStr = "&self=";
		if(t.getTrackerType().equals("M"))
			cvStr = "&set=";

		buf.append("<form action=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&linkid="+linkid+"\" name=\"addCommForm\" method=\"post\">");
		buf.append("<input type=\"hidden\" name=\"action\" value=\"adddtask2\" />");
		buf.append("<input type=\"hidden\" name=\"taskid\" value=\""+t.getId()+"\" />");
			
		if (!msg.equals("")){
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
			buf.append("<tr height=\"21\"><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr>");
			buf.append("</table>");
		}
			
		buf.append("<table summary=\"\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
			
		Vector tasks = o.getResultTasks();
		boolean shownIbmOnly = false;
		boolean taskShown = false;
		for (int tcnt=0;tcnt<tasks.size();tcnt++){
			taskShown = true;
			if (tcnt==0){
				buf.append("<tr><td headers=\"\" colspan=\"3\">All eligible tasks are displayed below</td></tr>");
				buf.append("<tr class=\"tdblue\"><td>&nbsp;</td><td><b>Task</b></td><td><b>Title</b></td></tr>");
					
			}
			ETSSelfTask task= new ETSSelfTask((ETSTask)tasks.elementAt(tcnt));
				
			buf.append("<tr>");
			buf.append("<td><input type=\"checkbox\" id=\""+task.getId()+"\" name=\"deptasks\" value=\""+task.getId()+"\" /></td>");
			buf.append("<td>"+task.getId()+"</td>");
			String sIbmOnly = "";
			if(task.isIbmOnly() && o.isSetMet()){
				shownIbmOnly = true;
				sIbmOnly = " <span class=\"ast\">*</span>";
			}
			buf.append("<td><label for=\""+task.getId()+"\">"+task.getTitle()+sIbmOnly+"</label></td>");
			buf.append("</tr>");
		}		
		buf.append("</table>\n");
			
			
		if(shownIbmOnly && o.isSetMet()){
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"8\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td headers=\"\" class=\"small\"><span class=\"ast\">*</span>Denotes IBM only task</td></tr>");
			//buf.append("<tr><td headers=\"\" class=\"small\"><span class=\"ast\">*</span>All tasks are restricted</td></tr>");
			buf.append("</table>\n");	
		}
			
		if (!taskShown){
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
			buf.append("<tr><td>There are no eligible tasks to choose</td></tr>");
			buf.append("</table>\n");	
		}
			
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>\n");
		
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
		buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		if (taskShown){
			buf.append("<td headers=\"\" align=\"left\" valign=\"top\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");
			buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
		}
		buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?action=details&taskid="+t.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
		buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=details&taskid="+t.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
		buf.append("</tr>\n");
		buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("</table>\n");
			
		buf.append("</form");
	}
	catch(Exception e){
		e.printStackTrace();
		buf.append("Error occurred while retrieving task information.  Please try again.");	
	}		
	//writer.println(buf.toString());
	return buf.toString();
}


static public String doAddDepTask2(ETSParams params, int CurrentCatId, ETSDealTrackerResultObj o,String taskid, boolean user_external,String msg){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();
	
	//do add to db2 table here and display confirmation page
	StringBuffer buf = new StringBuffer();
	buf.append("<br /><br />");	
	ETSTask t = o.getTask();
	
	String cvStr = "&self=";
	if(t.getTrackerType().equals("M"))
		cvStr = "&set=";

	buf.append("<br /><br />");	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td>You have successfully added the task(s):</td></tr>");
	//buf.append("<tr><td headers=\"\" align=\"left\">"+dtasks+"</td></tr>");
	buf.append("</table>");
		
	buf.append("<table summary=\"\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");				
	buf.append("<tr><td headers=\"\" colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" height=\"30\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr height=\"18\"><td headers=\"\" class=\"tdblue\" colspan=\"7\" align=\"left\">&nbsp;Dependent tasks</td></tr>");

	buf.append("<tr><th id=\"taskid\" colspan=\"2\" align=\"left\" valign=\"middle\" height=\"16\">");
	buf.append("Task</a></th>");
	buf.append("<th id=\"title\" align=\"left\" valign=\"middle\" height=\"16\">Title</a></th>");
	buf.append("<th id=\"section\" align=\"left\" valign=\"middle\" height=\"16\">Section</a></th>");
	buf.append("<th id=\"section\" align=\"left\" valign=\"middle\" height=\"16\">Owner</a></th>");
	buf.append("<th id=\"section\" align=\"left\" valign=\"middle\" height=\"16\">Date</a></th>");
	buf.append("<th id=\"section\" align=\"left\" valign=\"middle\" height=\"16\">Status</a></th>");
	buf.append("</tr>\n");	  

	int dw1=12;
	int dw2=30;
	int dw3=190;
	int dw4=80;
	int dw5=153;
	int dw6=90;  
	int dw7=45;

	boolean hasDTaskShown = false;
	boolean hasDIbmOnly = false;
	Vector dTasks = o.getResultTasks();

	if (dTasks.size() > 0){
		boolean cgray_flag = true;
		for (int i = 0; i < dTasks.size(); i++){
			ETSSelfTask dTask = new ETSSelfTask((ETSTask)dTasks.elementAt(i));
			if (!(dTask.isIbmOnly() && user_external)){
				hasDTaskShown = true;
			}

			if (cgray_flag){
				buf.append("<tr style=\"background-color:#eeeeee\">");
				cgray_flag=false;
			}
			else{
				buf.append("<tr>");
				cgray_flag=true;
			}


			if (dTask.hasDocs()){
				buf.append("<td headers=\"\" width=\""+dw1+"\" align=\"left\"><img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>"); //img
			}
			else{
				buf.append("<td headers=\"\" width=\""+dw1+"\" align=\"left\">&nbsp;</td>"); //img
			}
			buf.append("<td headers=\"\" width=\""+dw2+"\"  align=\"left\">"+dTask.getId()+"</td>");
			buf.append("<td headers=\"\" width=\""+dw3+"\" >"+dTask.getTitle());
			if (dTask.isIbmOnly() && o.isSetMet()){
				buf.append("<span class=\"ast\"><b>*</b></span></td>");
				hasDIbmOnly = true;
			}
			else
				buf.append("</td>");
	
			buf.append("<td headers=\"\" width=\""+dw4+"\" >"+dTask.getSection()+"</td>");
			try{
				buf.append("<td headers=\"\" width=\""+dw5+"\" >"+ETSUtils.getUsersName(conn,dTask.getOwnerId()));
			}
			catch(Exception e){
				buf.append("<td headers=\"\" width=\""+dw5+"\" >"+dTask.getOwnerId());
			}
			buf.append("</td>");

			java.util.Date dtdate = new java.util.Date(dTask.getDueDate());
			String dtdateStr = df.format(dtdate);
			buf.append("<td headers=\"\" width=\""+dw6+"\" >"+dtdateStr+"</td>");
			buf.append("<td headers=\"\" width=\""+dw7+"\" >"+dTask.getStatusColor()+"</td>");
			buf.append("</tr>\n");
		}
	}
	else{
		buf.append("<tr style=\"background-color:#eeeeee\"><td headers=\"\" colspan=\"7\" align=\"left\">No current dependent tasks</td></tr>");	
	}			
		
	buf.append("</table>");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");
	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\" colspan=\"2\"><a href=\"ETSProjectsServlet.wss?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "continue.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"Task details\" /></a></td>");
		
	buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.ICON_ROOT + "bk.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Back to main\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Back to action plan</a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
	//writer.println(buf.toString());
	return buf.toString();
				
}

static public String doDelDepTask(ETSParams params, int CurrentCatId, ETSDealTrackerResultObj o,boolean user_external,String msg){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();

	ETSSelfTask task = new ETSSelfTask(o.getTask());
	ETSSelfTask dTask = new ETSSelfTask(o.getDepTask());
	
	String cvStr = "&self=";
	if(task.getTrackerType().equals("M"))
		cvStr = "&set=";
		
	StringBuffer buf = new StringBuffer();

	buf.append("<br />");
	buf.append("<form action=\"ETSProjectsServlet.wss?taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&linkid="+linkid+"\" method=\"post\" name=\"deldoc\">");
	buf.append("<input type=\"hidden\" name=\"dtaskid\" value=\""+dTask.getId()+"\" />"); 
	buf.append("<input type=\"hidden\" name=\"action\" value=\"deldtask2\" />");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
		
	buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">You are about to remove the dependecy for this task. <br />" +
		" This will not delete the task from the system.</td></tr>");
		
	buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");

	buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">Dependecy to be removed:</td></tr>");
		
		
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Task id:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dTask.getId()+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Section:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dTask.getSection()+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Title:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dTask.getTitle()+"</td></tr>");
		
	try{
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+ETSUtils.getUsersName(conn,dTask.getOwnerId())+"</td></tr>");
	}
	catch(Exception e){
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dTask.getOwnerId()+"</td></tr>");
	}		
		
	buf.append("</table>");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"2\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");

	buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
	buf.append("</form>");
	//writer.println(buf.toString());
	return buf.toString();
}

static public String doDelDepTask2(ETSParams params,int CurrentCatId,ETSDealTrackerResultObj o){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();

	ETSSelfTask task = new ETSSelfTask(o.getTask());
	ETSSelfTask dTask = new ETSSelfTask(o.getDepTask());
	
	String cvStr = "&self=";
	if(task.getTrackerType().equals("M"))
		cvStr = "&set=";
		
	StringBuffer buf = new StringBuffer();

	buf.append("<br />");	

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">You have successfully removed the dependent task:</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Task:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dTask.getId()+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Relevant attribute:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dTask.getSection()+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Title:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dTask.getTitle()+"</td></tr>");
	try{
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+ETSUtils.getUsersName(conn,dTask.getOwnerId())+"</td></tr>");
	}
	catch(Exception e){
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+dTask.getOwnerId()+"</td></tr>");
	}		

	buf.append("</table>");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.ICON_ROOT + "bk_c.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Back to details\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Back to task details</a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
	buf.append("</form>");
	//writer.println(buf.toString());
	return buf.toString();
}

static public String doDelTask(ETSParams params, int CurrentCatId,ETSDealTrackerResultObj o){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();
	
	ETSTask task = o.getTask();
	
	String cvStr = "&self=";
	if (task.getTrackerType().equals("M"))
	   cvStr = "&set=";
		
	StringBuffer buf = new StringBuffer();
	buf.append("<br /><span style=\"color:#ff3333\">This action can not be undone.</span>");
		
	buf.append("<br />");	
	buf.append("<form action=\"ETSProjectsServlet.wss?taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&linkid="+linkid+"\" method=\"post\" name=\"deldoc\">");
	buf.append("<input type=\"hidden\" name=\"action\" value=\"deltask2\" />");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">You are about to delete the task:</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Task:</b></td><td headers=\"\" align=\"left\">"+task.getId()+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Relevant attribute:</b></td><td headers=\"\" align=\"left\">"+task.getSection()+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Title:</b></td><td headers=\"\" align=\"left\">"+task.getTitle()+"</td></tr>");
	try{
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\">"+ETSUtils.getUsersName(conn,task.getOwnerId())+"</td></tr>");
	}
	catch(Exception e){
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\">"+task.getOwnerId()+"</td></tr>");
	}		
		
	buf.append("</td>");
	buf.append("</table>");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr><td>Deleting this task will also delete all documents and comments associated with it.</td></tr>");
	buf.append("</table>\n");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"2\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");

	buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?action=details&taskid="+task.getId()+"&proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
	buf.append("</form>");
	//writer.println(buf.toString());
	return buf.toString();
}

static public String doDelTask2(ETSParams params, int CurrentCatId,ETSDealTrackerResultObj o){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();
	
	ETSTask task = o.getTask();
		
	String cvStr = "&self=";
	if (task.getTrackerType().equals("M"))
	   cvStr = "&set=";
	   
	int taskid = task.getId();
	String section = task.getSection();
	String title = task.getTitle();
	String ownerid = task.getOwnerId();
			
	StringBuffer buf = new StringBuffer();

	buf.append("<br />");	

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">You have successfully deleted the task:</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Task:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+taskid+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Relevant attribute:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+section+"</td></tr>");
	buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Title:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+title+"</td></tr>");
	try{
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+ETSUtils.getUsersName(conn,ownerid)+"</td></tr>");
	}
	catch(Exception e){
		buf.append("<tr height=\"21\"><td headers=\"\" align=\"left\" width=\"90\"><b>Owner:</b></td><td headers=\"\" align=\"left\" width=\"510\">"+ownerid+"</td></tr>");
	}		

	buf.append("</table>");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.ICON_ROOT + "bk_c.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Action plan\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Back to action plan</a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
	buf.append("</form>");
	//writer.println(buf.toString());
	return buf.toString();
}

static public String doEditDash(ETSParams params, int CurrentCatId,Vector notEditor, ETSDealTrackerResultObj o,String msg,boolean sub){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	Connection conn = params.getConnection();
	HttpServletRequest request = params.getRequest();

	StringBuffer buf = new StringBuffer();	
	boolean gray_flag = true;
	String taskids = "";
	
	String cvStr = "&self=";
	if (o.isSetMet())
	   cvStr = "&set=";
		
	int w1=400;  //doc img
	int w2=200;  //taskid
		
	buf.append("<br />");
	if(!msg.equals("")){
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("<tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr>");
		buf.append("</table>\n");
	}
	buf.append("<form action=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&linkid="+linkid+"\" method=\"post\" name=\"editdashForm\">\n");
	buf.append("<input type=\"hidden\" name=\"action\" value=\"editdash2\" />");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"2\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
			
	Vector tasks = o.getResultTasks();
	for (int t = 0; t < tasks.size(); t++){
		ETSTask task = (ETSTask)tasks.elementAt(t);
			
		buf.append("<tr><td headers=\"\" colspan=\"2\" class=\"small\" colspan=\"4\" valign=\"top\"><span style=\"color:#3c5f84\"><b>Task: ");
			
		buf.append(task.getId()+"</b></span>");
		if (task.hasDocs()){
			buf.append(" &nbsp;<img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" />"); //img
		}
		buf.append("</td></tr>");

			
		buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			
		buf.append("<tr><td headers=\"\" class=\"small\" width=\""+w1+"\"><label for=\"title\"><b>Title</b></label></td>");
		buf.append("<td headers=\"\" class=\"small\"><label for=\"status\"><b>Relevant attribute</b></label></td></tr>");
		buf.append("<tr><td headers=\"\" width=\""+w1+"\"><input type=\"text\" id=\"title\" name=\"title_"+task.getId()+"\" size=\"40\" style=\"width:250px\" width=\"275px\" maxlength=\"175\" value=\""+(sub?ETSDealTrackerCommonFuncs.getParameter(request,"title_"+task.getId()):task.getTitle())+"\" class=\"iform\" /></td>");
			
		buf.append("<td headers=\"\" width=\""+w2+"\">");
		String ssection = (sub?ETSDealTrackerCommonFuncs.getParameter(request,"section_"+task.getId()):task.getSection());
		buf.append("<select name=\"section_"+task.getId()+"\" id=\"section\" class=\"iform\">");
		for (int ra = 0; ra<rel_attr.length;ra++){
			if (ssection.equals(rel_attr[ra]))
				buf.append("<option value=\""+rel_attr[ra]+"\" selected=\"selected\">"+rel_attr[ra]+"</option>");
			else
				buf.append("<option value=\""+rel_attr[ra]+"\">"+rel_attr[ra]+"</option>");
		}
		buf.append("</select>\n");
		buf.append("</td></tr>");
			
					
					
			
		buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			
		buf.append("<tr><td headers=\"\" class=\"small\"><label for=\"owner\"><b>Owner</b></label></td>");
		buf.append("<td headers=\"\" class=\"small\" width=\""+w2+"\"><label for=\"section\"><b>Status</b></label></td></tr>");
			
		buf.append("<tr>");
		String so = (sub?ETSDealTrackerCommonFuncs.getParameter(request,"owner_"+task.getId()):task.getOwnerId());
		buf.append("<td headers=\"\" align=\"left\" valign=\"top\">\n");

		Vector users = new Vector();
		//users = getOwnMembers(o.getIbmUsers(),conn);  //5.2 SPN SPN maybe have to change
		users = ETSDealTrackerCommonFuncs.getOwnMembers(Project,o.getUsers(),conn,task.isIbmOnly(),notEditor);	
		
		if (users.size()>0){
			buf.append("<select name=\"owner_"+task.getId()+"\" id=\"owner\" class=\"iform\">");
			buf.append("<option value=\"0\">&nbsp;</option>");
			for (int i =0; i<users.size(); i++){
				ETSUser user = (ETSUser)users.elementAt(i);
				String username = user.getUserId();
				try{
					username = ETSUtils.getUsersName(conn,user.getUserId())+" ["+user.getUserId()+"]";
				}
				catch(Exception e){
					username = " ["+user.getUserId()+"]";
				}
				if (user.getUserId().equals(so))
					buf.append("<option value=\""+user.getUserId()+"\" selected=\"selected\">"+username+"</option>");
				else
					buf.append("<option value=\""+user.getUserId()+"\">"+username+"</option>");
			}
			buf.append("</select>\n");
		}
		else{
			buf.append("No valid owners");
		}
		buf.append("</td>");
		String ss = (sub?ETSDealTrackerCommonFuncs.getParameter(request,"status_"+task.getId()):task.getStatus());
		buf.append("<td headers=\"\" width=\""+w2+"\">");
		buf.append("<select name=\"status_"+task.getId()+"\" id=\"status\" class=\"iform\">");
		if (ss.equals(Defines.GREEN))
			buf.append("<option value=\""+Defines.GREEN+"\" selected=\"selected\">Complete</option>");
		else
			buf.append("<option value=\""+Defines.GREEN+"\">Complete</option>");

		if (ss.equals(Defines.YELLOW))
			buf.append("<option value=\""+Defines.YELLOW+"\" selected=\"selected\">In progress</option>");
		else
			buf.append("<option value=\""+Defines.YELLOW+"\">In progress</option>");

		if (ss.equals(Defines.RED))
			buf.append("<option value=\""+Defines.RED+"\" selected=\"selected\">Not started</option>");
		else
			buf.append("<option value=\""+Defines.RED+"\">Not started</option>");
		buf.append("</select>\n");
		buf.append("</td></tr>");
			
					
					
		buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			

		buf.append("<tr><td headers=\"\" class=\"small\" colspan=\"2\"><label for=\"date\"><b>Due date</b></label></td></tr>");
		buf.append("<tr><td headers=\"\" class=\"small\" colspan=\"2\" valign=\"top\">\n");
			
		buf.append("Month: ");
		int im = (sub?(new Integer(ETSDealTrackerCommonFuncs.getParameter(request,"month_"+task.getId())).intValue()):task.getTaskMonth(task.getDueDate()));
		buf.append("<select name=\"month_"+task.getId()+"\" id=\"date\" class=\"iform\">");
		buf.append("<option value=\"0\">&nbsp;</option>");
		for (int m = 0; m < 12; m++){
			if(im==m){
				buf.append("<option value=\""+m+"\" selected=\"selected\">"+months[m]+"</option>");
			}
			else{
				buf.append("<option value=\""+m+"\">"+months[m]+"</option>");
			}
		}
		buf.append("</select>\n");


		int idy = (sub?(new Integer(ETSDealTrackerCommonFuncs.getParameter(request,"day_"+task.getId())).intValue()):task.getTaskDay(task.getDueDate()));
		buf.append("&nbsp;&nbsp;");
		buf.append("Day: ");
		buf.append("<select name=\"day_"+task.getId()+"\" id=\"date\" class=\"iform\">");
		buf.append("<option value=\"0\">&nbsp;</option>");
		for (int d = 1; d <= 31; d++){
			if(idy==d){
				buf.append("<option value=\""+d+"\" selected=\"selected\">"+d+"</option>");
			}
			else{
				buf.append("<option value=\""+d+"\">"+d+"</option>");
			}
		}
		buf.append("</select>\n");

		buf.append("&nbsp;&nbsp;");
		buf.append("Year: ");
		int iy = (sub?(new Integer(ETSDealTrackerCommonFuncs.getParameter(request,"year_"+task.getId())).intValue()):task.getTaskYear(task.getDueDate()));
		Calendar cal = Calendar.getInstance();
		int year = (cal.get(Calendar.YEAR)) - 1;

		buf.append("<select name=\"year_"+task.getId()+"\" id=\"date\" class=\"iform\">");
		buf.append("<option value=\"0\">&nbsp;</option>");
		for (int c = year; c <= year+4; c++){
			if(iy==c){
				buf.append("<option value=\""+c+"\" selected=\"selected\">"+c+"</option>");
			}
			else{
				buf.append("<option value=\""+c+"\">"+c+"</option>");
			}
		}
		buf.append("</select>\n");
		buf.append("</td>");
		buf.append("</tr>\n");
		
		buf.append("<tr><td headers=\"\" colspan=\"2\"><table summary=\"\" width=\"600\" border=\"0\">\n");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr><td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td></tr>");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("</table></td></tr>\n");
		taskids = taskids+task.getId()+",";
			
		String sio = "0";
		if (task.isIbmOnly())
			sio = "1";
			
		buf.append("<input type=\"hidden\" name=\"ibmonly_"+task.getId()+"\" value=\""+sio+"\" />");
		
	}
	
	if (tasks.size()<=0){
		buf.append("<tr><td>There are no tasks to edit.</td></tr>\n");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
		
	}
	buf.append("</table>");
		
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	if (tasks.size()>0){
		buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"2\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");
		buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
	}
	else{
		buf.append("<tr>");	
	}
	
	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
	buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
	buf.append("</tr></table>\n");
	buf.append("<input type=\"hidden\" name=\"taskids\" value=\""+taskids+"\" />");
	
	
	buf.append("</form>");
		
	//writer.println(buf.toString());
	return buf.toString();		
}

static public String doEditDash2(ETSParams params, int CurrentCatId, ETSDealTrackerResultObj o){
	ETSProj Project = params.getETSProj();
	int TopCatId = params.getTopCat();
	String linkid = params.getLinkId();
	StringBuffer buf = new StringBuffer();	
	
	String cvStr = "&self=";
	if (o.isSetMet())
	   cvStr = "&set=";

	buf.append("<br /><br />");	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr height=\"21\"><td headers=\"\" colspan=\"2\">You have successfully updated the action plan tasks.</td></tr>");
	
	
	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
	buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");
	buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>\n");

	buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
	buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
	buf.append("<tr>");

	buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
	buf.append("<img src=\"" + Defines.BUTTON_ROOT + "continue.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"action plan\" /></a></td>");
	buf.append("</tr>\n");
	buf.append("</table>\n");
	//writer.println(buf.toString());
	return buf.toString();
}






	static public String notEditableError(ETSParams params, int CurrentCatId,ETSDealTrackerResultObj o){
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("Action plan is not editable");
		buf.append(ETSSelfDealTrackerPrint.printBackButton(params,CurrentCatId,o.getTrackerType(),o.getSelfId()));
		return buf.toString();
	}

 static public String printBackButton(ETSParams params, int CurrentCatId, String trackerType, String selfid){
	ETSProj Project = params.getETSProj();
	 int TopCatId = params.getTopCat();
	 String linkid = params.getLinkId();
	 
	 String cvStr = "&self=";
	 if (trackerType.equals("M"))
	 	cvStr = "&set=";
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"30\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>\n");

		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
		buf.append("<tr><td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+selfid+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
		buf.append("<img src=\"" + Defines.ICON_ROOT + "bk_c.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Action plan\" /></a></td>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+cvStr+selfid+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Back to action plan</a></td>");
		buf.append("</tr>\n");
		buf.append("</table>\n");
	
		//writer.println(buf.toString());
		return buf.toString();
	}



} // end of class







