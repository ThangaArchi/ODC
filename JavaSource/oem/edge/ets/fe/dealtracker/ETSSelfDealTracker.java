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
 * Date: 5/10/2004
 */

package oem.edge.ets.fe.dealtracker;

import oem.edge.common.*;
import oem.edge.ets.fe.*;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.net.URLEncoder;

import oem.edge.amt.*;

public class ETSSelfDealTracker {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "5.1.1";


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
	protected boolean canView;
		
    protected int CurrentCatId;
	protected ETSCat this_current_cat;
	static private final String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
	static private final String[] rel_attr = new String[]{"Quality","Delivery","Cost/Price","Technology & design","Responsiveness","Key leverages","Positive efforts"};
	static private final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	static private final SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");

	protected String selfId;
	protected String selfName;
	protected boolean editablePlan;
	protected boolean showClose;
	protected String strTrackerType;

	public ETSSelfDealTracker(ETSParams parameters,String selfid,String selfname,boolean edit,boolean showclose, boolean bIsSurvey){
		this(parameters, selfid, selfname, edit, showclose);
		if (bIsSurvey) {
			strTrackerType = "S";
		}
		else {
			strTrackerType = "A";
		}
	}

    public ETSSelfDealTracker(ETSParams parameters,String selfid,String selfname,boolean edit,boolean showclose){
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
		this.selfId = selfid;
		
		String currentCatIdStr = ETSDealTrackerCommonFuncs.getParameter(request,"cc");
		if (!currentCatIdStr.equals("")){
		    this.CurrentCatId = (new Integer(currentCatIdStr)).intValue();
		}
		else{
		    this.CurrentCatId = TopCatId;
		}
		
		this.canView = false;
		this.selfId = selfid;
		this.selfName = selfname;
		this.editablePlan = edit;
		this.showClose = showclose;
		this.strTrackerType = "A";
		
	}

	public void SelfTrackerHandler(){
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		String action = ETSDealTrackerCommonFuncs.getParameter(request,"action");
		String staskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
		//String selfname = "Self Assessment";

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

		ETSDealTrackerResultObj resobj = new ETSDealTrackerResultObj();
		resobj.setSelfId(selfId);
		resobj.setTrackerType(strTrackerType);

		boolean user_external = false;
		if (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I")){
				user_external = true;
		}

		StringBuffer b = new StringBuffer();
		b.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		b.append("<tr><td>");

		ETSDealTrackerFunctions funcs = new ETSDealTrackerFunctions(Params);
		//selfname = funcs.getSelfName(selfId,Project.getProjectId());
		
		canView = canView(user_external);
		Vector notEditor = new Vector(); 
		notEditor.addElement(Defines.WORKSPACE_VISITOR);
		notEditor.addElement(Defines.ETS_EXECUTIVE);
		notEditor.addElement(Defines.WORKSPACE_CLIENT);
		
		if(!canView){
			printHeader("Self Assessement",selfName);
			writer.println(b.toString());
			writer.println("Unauthorized access");
			writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
			return;	
		}

		if (!action.equals("")){
	
			if (action.equals("additask")){
				printHeader("Add task",selfName);
				writer.println(b.toString());
		
				resobj.setIbmOnly(true);
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				
				Hashtable h = new Hashtable();
				resobj = funcs.doAddTask(resobj,action,user_external,userRole,notEditor);
		
		
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
				}
				else if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
				}
				else
					writer.println(ETSSelfDealTrackerPrint.doAddTask(Params,CurrentCatId,notEditor,resobj,h,resobj.getIbmOnly(),""));	    
			}
			else if (action.equals("additask2")){
				if ("S".equals(strTrackerType)) {
					resobj.setIbmOnly(false);
				}
				else {
					resobj.setIbmOnly(true);
				}
				
				if (!editablePlan){
					printHeader("Add task",selfName);
					writer.println(b.toString());
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}

				resobj = funcs.verifyTaskForm(resobj,action,user_external,userRole,notEditor,es.gIR_USERN);
			
				if (resobj.getErrorFlag()){
					action = "additask";
					
					printHeader("Add task",selfName);
					writer.println(b.toString());
		
					writer.println(ETSSelfDealTrackerPrint.doAddTask(Params,CurrentCatId,notEditor,resobj,resobj.getFormHash(),resobj.getIbmOnly(),resobj.getErrorMsg()));
				}
				else if (resobj.getAccessErrorFlag()){
					printHeader("Add task",selfName);
					writer.println(b.toString());	
					writer.println(resobj.getAccessErrorMsg());			
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;
				}
				else{
					printHeader("Add task",selfName);
					writer.println(b.toString());
					resobj = funcs.doAddTask2(resobj,resobj.getFormHash(),resobj.getIbmOnly());
					writer.println(ETSSelfDealTrackerPrint.doAddTask2(Params,CurrentCatId,resobj));	

				}
			}
			else if(action.equals("details")){
				printHeader("Task details",selfName);
				writer.println(b.toString());
				resobj = funcs.getTaskDetails(resobj,userRole,user_external);
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				writer.println(ETSSelfDealTrackerPrint.printTaskDetails(Params,CurrentCatId,userRole,es,editablePlan,resobj,user_external,notEditor));
			}
			else if(action.equals("edetails")){
				boolean ibmonly = false;
				boolean canEdit = false;
				printHeader("Edit task details",selfName);
				writer.println(b.toString());

				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				
				resobj = funcs.getTask(resobj,userRole,user_external);
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				
				if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,resobj.getTask().isIbmOnly(),userRole,notEditor,resobj.getTask().getCreatorId(),resobj.getTask().getOwnerId(),user_external)){
					writer.println("Not authorized to perform this function");
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}							
	
					writer.println(ETSSelfDealTrackerPrint.editTaskDetails(Params,CurrentCatId,notEditor,resobj,new Hashtable(),ibmonly,""));
			}
			else if(action.equals("edetails2")){
				printHeader("Edit task details",selfName);
				writer.println(b.toString());
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				
				resobj = funcs.getTaskDetails(resobj,userRole,user_external);
				
				
				if (resobj.getErrorFlag()){	
					writer.println(resobj.getErrorMsg());	
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;
				}
				
				/*if (!(isSuperAdmin || resobj.getTask().getOwnerId().equals(es.gIR_USERN) || resobj.getTask().getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER) || userRole.equals(Defines.WORKSPACE_MANAGER))){
					writer.println("Not authorized to perform this function");
					printBackButton(resobj.getSelfId());
					return;
				}*/
					
					
				if (resobj.getTask().isIbmOnly())
					resobj.setIbmOnly(true);
	
				
				resobj = funcs.verifyTaskForm(resobj,action,user_external,userRole,notEditor,resobj.getTask().getCreatorId());
				
				
				
				if (resobj.getErrorFlag()){
					//editTaskDetails(resobj,resobj.getFormHash(),resobj.getIbmOnly(),resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.editTaskDetails(Params,CurrentCatId,notEditor,resobj,resobj.getFormHash(),resobj.getIbmOnly(),resobj.getErrorMsg()));
				}
				else if (resobj.getAccessErrorFlag()){	
					writer.println(resobj.getAccessErrorMsg());	
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;
				}
				else{
					resobj = funcs.editTaskDetails(resobj);
					writer.println(ETSSelfDealTrackerPrint.editTaskDetails2(Params,CurrentCatId,resobj.getTask(),resobj.getFormHash(),resobj.getIbmOnly()));
				}
			}
			else if(action.equals("addtaskcomm")){
				printHeader("Add task comment",selfName);
				writer.println(b.toString());
				String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}

				resobj = funcs.getTask(resobj,userRole,user_external);
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
	
				ETSTask t = resobj.getTask();
				if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,resobj.getTask().isIbmOnly(),userRole,notEditor,resobj.getTask().getCreatorId(),resobj.getTask().getOwnerId(),user_external)){
					writer.println("You are not authorized to perform this action");
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}

				
				writer.println(ETSSelfDealTrackerPrint.doAddTaskComment(Params,CurrentCatId,t,resobj.getSelfId(),"",""));
			}
			else if(action.equals("addtaskcomm2")){
				printHeader("Add task comment",selfName);
				writer.println(b.toString());
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
			
				resobj = funcs.doAddTaskComment2(resobj,userRole,notEditor,user_external);
				
				
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(ETSSelfDealTrackerPrint.doAddTaskComment(Params,CurrentCatId,resobj.getTask(),resobj.getSelfId(),resobj.getTaskCommStr(),resobj.getErrorMsg()));
					return;	
				}
	
				writer.println(ETSSelfDealTrackerPrint.doAddTaskComment2(Params,CurrentCatId,resobj));	

			}
			else if(action.equals("adddtask")){
				printHeader("Add dependant task",selfName);
				writer.println(b.toString());
				String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
	
				resobj = funcs.doAddDepTask(resobj,userRole,taskid,user_external,notEditor);
	
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}

				writer.println(ETSSelfDealTrackerPrint.doAddDepTask(Params,CurrentCatId,resobj,user_external,""));
	
			}
			else if(action.equals("adddtask2")){
				printHeader("Add dependant task",selfName);
				writer.println(b.toString());
				String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				
				String[] depTasks = request.getParameterValues("deptasks");
				resobj = funcs.doAddDepTask2(resobj,userRole,taskid,depTasks,user_external,notEditor);
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}

				writer.println(ETSSelfDealTrackerPrint.doAddDepTask2(Params, CurrentCatId,resobj,taskid,user_external,""));
	
			}
			else if(action.equals("deldtask")){
				printHeader("Remove dependant task",selfName);
				writer.println(b.toString());
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				
				String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
				String dtaskid = ETSDealTrackerCommonFuncs.getParameter(request,"dtaskid");
				
				resobj = funcs.doDelDepTask(resobj,userRole,taskid,dtaskid,user_external,notEditor);
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				
				writer.println(ETSSelfDealTrackerPrint.doDelDepTask(Params,CurrentCatId,resobj,user_external,""));
			}
			else if(action.equals("deldtask2")){
				printHeader("Remove dependant task",selfName);
				writer.println(b.toString());
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				
				String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
				String dtaskid = ETSDealTrackerCommonFuncs.getParameter(request,"dtaskid");

				resobj = funcs.doDelDepTask2(resobj,userRole,taskid,dtaskid,notEditor,user_external);
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}

				writer.println(ETSSelfDealTrackerPrint.doDelDepTask2(Params,CurrentCatId,resobj));
	
			}
			else if(action.equals("deltask")){
				printHeader("Delete task",selfName);
				writer.println(b.toString());
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				
				resobj = funcs.doDelTask(resobj,userRole,staskid,notEditor,user_external);
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(b.toString());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				writer.println(ETSSelfDealTrackerPrint.doDelTask(Params,CurrentCatId,resobj));
			}
			else if(action.equals("deltask2")){
				printHeader("Delete task",selfName);
				writer.println(b.toString());
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				
				resobj = funcs.doDelTask2(resobj,userRole,staskid,notEditor,user_external);
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
	
				writer.println(ETSSelfDealTrackerPrint.doDelTask2(Params,CurrentCatId,resobj));

			}
			else if(action.equals("editdash")){
				printHeader("Edit action plan overview",selfName);
				writer.println(b.toString());
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				
				resobj = funcs.doEditDash(resobj,userRole,notEditor,user_external);
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}

				writer.println(ETSSelfDealTrackerPrint.doEditDash(Params,CurrentCatId,notEditor,resobj,"",false));
			}
			else if(action.equals("editdash2")){
				printHeader("Edit action plan overview",selfName);
				writer.println(b.toString());
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				resobj = funcs.doEditDash2(resobj,userRole,notEditor,user_external);
				if (resobj.getErrorFlag()){
					String msg = resobj.getErrorMsg();
					resobj = funcs.doEditDash(resobj,userRole,notEditor,user_external);
					if (resobj.getErrorFlag()){
						writer.println(resobj.getErrorMsg());
						writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
						return;
					}
					writer.println(ETSSelfDealTrackerPrint.doEditDash(Params,CurrentCatId,notEditor,resobj,msg,true));
					return;	
				}
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				
				writer.println(ETSSelfDealTrackerPrint.doEditDash2(Params, CurrentCatId,resobj));
			}
			else if(action.equals("print")){
				doDashboardView(resobj,user_external,true,notEditor);
			}
			else if(action.equals("addtaskdoc")){
				printHeader("Add task document",selfName);
				writer.println(b.toString());
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
					
				resobj = funcs.doAddTaskDoc(resobj,staskid,user_external,userRole,notEditor);
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				ETSDocumentManager docman = new ETSDocumentManager(this.Params);
				docman.ETSDocumentHandler();

			}
			else if(action.equals("deltaskdoc")){
				printHeader("Delete task document",selfName);
				writer.println(b.toString());
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
							
				/*if(userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)){
					writer.println("You are not authorized to perform this action");			
					printBackButton(resobj.getSelfId());
					return;
				}*/
			
				String sdocid = ETSDealTrackerCommonFuncs.getParameter(request,"docid");
			
				resobj = funcs.doDelTaskDoc(resobj,userRole,sdocid,staskid,notEditor,user_external);
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				int docid = new Integer(sdocid).intValue(); 
				int taskid = new Integer(staskid).intValue(); 

				writer.println(ETSSelfDealTrackerPrint.doDelTaskDoc(Params,CurrentCatId,resobj,docid,taskid));
			}
			else if(action.equals("deltaskdoc2")){
				printHeader("Delete task document",selfName);
				writer.println(b.toString());
				
				if (!editablePlan){
					writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
					return;
				}
				String sdocid = ETSDealTrackerCommonFuncs.getParameter(request,"docid");
			
				resobj = funcs.doDelTaskDoc2(resobj,userRole,sdocid,staskid,notEditor,user_external);
				if (resobj.getAccessErrorFlag()){
					writer.println(resobj.getAccessErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}
				if (resobj.getErrorFlag()){
					writer.println(resobj.getErrorMsg());
					writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",resobj.getSelfId()));
					return;	
				}

				int docid = new Integer(sdocid).intValue(); 
				int taskid = new Integer(staskid).intValue(); 

				writer.println(ETSSelfDealTrackerPrint.doDelTaskDoc2(Params,CurrentCatId,resobj,docid,taskid));
			}
		}//action!=""
		else{
			printHeader("&nbsp;",selfName);
			doDashboardView(resobj,user_external,false,notEditor);	
			
		}
	}  //end of handler



	private void doDashboardView(ETSDealTrackerResultObj o,boolean user_external,boolean printview,Vector notEditor){
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
	
	    StringBuffer b = new StringBuffer();
		b.append(ETSSelfDealTrackerPrint.doDashboardView(o,userRole,user_external,printview,Params,editablePlan,showClose,notEditor));
		//b.append(printDashboardButtons(o,printview,o.getResultTasks().size()>0,user_external));
		writer.println(b.toString());
		 
	
	}


/*
	private void doEditDash(ETSDealTrackerResultObj o,String msg,boolean sub){
		StringBuffer buf = new StringBuffer();	
		boolean gray_flag = true;
		String taskids = "";
		
		
		int w1=400;  //doc img
		int w2=200;  //taskid
		
		buf.append("<br />");
		if(!msg.equals("")){
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("<tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr>");
			buf.append("</table>\n");
		}
		buf.append("<form action=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&self="+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&linkid="+linkid+"\" method=\"post\" name=\"editdashForm\">\n");
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
			
			buf.append("<td headers=\"\" width=\""+w2+"\">");			String ssection = (sub?ETSDealTrackerCommonFuncs.getParameter(request,"section_"+task.getId()):task.getSection());
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
			//SPN SPN 5.this needs to be uncommented 
			//users = getOwnMembers(o.getIbmUsers(),conn);
			
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
		buf.append("</table>");
		
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
		buf.append("<tr><td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"top\" colspan=\"2\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");
		buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
		buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&self="+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
		buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
		buf.append("<td headers=\"\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&self="+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
		buf.append("</tr></table>\n");
		buf.append("<input type=\"hidden\" name=\"taskids\" value=\""+taskids+"\" />");
		buf.append("</form>");
		
		writer.println(buf.toString());		
	}
*/	
/*	
	private void doEditDash2(ETSDealTrackerResultObj o){
		StringBuffer buf = new StringBuffer();	

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

		buf.append("<td headers=\"\" align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&self="+o.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
		buf.append("<img src=\"" + Defines.BUTTON_ROOT + "continue.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"action plan\" /></a></td>");
		buf.append("</tr>\n");
		buf.append("</table>\n");
		writer.println(buf.toString());

	}
*/

// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		private void printHeader(String actionStr,String selfname){
		StringBuffer buf = new StringBuffer();
			
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		buf.append("<tr><td headers=\"\" valign=\"middle\" class=\"subtitle\">");
		buf.append("<span style=\"greytext\">"+actionStr+"</span></td></tr>");
		
		buf.append("<tr valign=\"middle\">");
		buf.append("<td headers=\"\" class=\"tdblue\" height=\"18\">&nbsp;"+selfname+"</td>");
		buf.append("</tr>");
		buf.append("</table>");	
		writer.println(buf.toString());
	}


/*
	private Vector getOwnMembers(Vector membs, Connection conn){
		Vector new_members = new Vector();
	
		for (int i = 0; i<membs.size();i++){
			ETSUser mem = (ETSUser)membs.elementAt(i);
			try{
				String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,mem.getUserId());
				String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
				String sRole = getUserRole(mem.getUserId(),Project.getProjectId());
				if (decaftype.equals("I") 
					&& !sRole.equals(Defines.WORKSPACE_CLIENT) 
					&& !sRole.equals(Defines.ETS_EXECUTIVE) 
					&& !sRole.equals(Defines.WORKSPACE_VISITOR)) {
					new_members.addElement(mem);
				}
			}
			catch(AMTException a){
				System.out.println("amt exception in getownmembers err= "+a);
			}
			catch(SQLException s){
				System.out.println("sql exception in getownmembers err= "+s);
			}
			catch(Exception e){
				System.out.println("exception in getownmembers err= "+e);
			}
		}
	
		return new_members;
	}
	*/
	
	String getUserRole(String ir_userid,String sProjectId) throws Exception {
		Vector userents = new Vector();
		String sRole = Defines.INVALID_USER;

		try {
			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(conn,ir_userid);
			userents = AccessCntrlFuncs.getUserEntitlements(conn,edgeuserid,true, true);

			if (userents.contains(Defines.ETS_ADMIN_ENTITLEMENT)) {
				sRole = Defines.ETS_ADMIN;
			} else if (userents.contains(Defines.ETS_ENTITLEMENT)) {
				if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.OWNER)) {
					sRole = Defines.WORKSPACE_OWNER;
				} else if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.ADMIN)) {
					sRole = Defines.WORKSPACE_MANAGER;
				} else if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.CLIENT)) {
					sRole = Defines.WORKSPACE_CLIENT;
				} else if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.USER)) {
					sRole = Defines.WORKSPACE_MEMBER;
				} else if (ETSDatabaseManager.hasProjectPriv(ir_userid,sProjectId,Defines.VISITOR)) {
					sRole = Defines.WORKSPACE_VISITOR;
				} else {
					if (userents.contains(Defines.ETS_EXECUTIVE_ENTITLEMENT)) {
						sRole = Defines.ETS_EXECUTIVE;
					}
				}
			} else if (userents.contains(Defines.ETS_EXECUTIVE_ENTITLEMENT)) {
				sRole = Defines.ETS_EXECUTIVE;
			}

		} catch (Exception e) {
		   throw e;
		}

		return sRole;
	 }

/*
	private void notEditableError(ETSDealTrackerResultObj o){
		writer.println("Action plan is not editable");
		writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"A",o.getSelfId()));
		return;
	}
*/
	private boolean canView(boolean user_external){
		boolean b = false;
		if (userRole.equals(Defines.WORKSPACE_CLIENT) || user_external){
				b= false;
		}
		else{
			b=true;	
		}
			
		return b;
	}

	
} // end of class







