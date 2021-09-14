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

public class ETSDealTracker {

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

	protected String backstr;
	protected boolean editablePlan;
	protected boolean showclose;
	protected String setid;
	protected boolean isSetMet;
	
	protected String setMetName;
		
    protected ETSDatabaseManager databaseManager;
    protected int CurrentCatId;
	protected ETSCat this_current_cat;
	static private final String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
	static private final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	static private final SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");

	public ETSDealTracker(ETSParams parameters){
		ETSDealTrackerMain(parameters);
		this.editablePlan = true;
		this.showclose = false;
		this.setid = "";
		this.backstr = "'Contracts' main'";
		this.isSetMet = false;
		this.setMetName = "";
	}

	public ETSDealTracker(ETSParams parameters,String setid,String setmetname,boolean edit,boolean showclose,String backstr){
		ETSDealTrackerMain(parameters);	
		this.setid = setid;
		this.editablePlan = edit;
		this.showclose = showclose;
		this.backstr = backstr;
		this.isSetMet = true;
		this.setMetName = setmetname;

	}

    public void ETSDealTrackerMain(ETSParams parameters){
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


	public void ETSTrackerHandler(){
		ETSDealTrackerResultObj resobj = new ETSDealTrackerResultObj();
		resobj.setBackStr("'Contracts' main");
		resobj.setIsSetMet(isSetMet);
		resobj.setTrackerType("D");
		if (isSetMet){
			resobj.setTrackerType("M");
			resobj.setSelfId(setid);
		}
		ETSTrackerHandler(resobj);
	}


	public void ETSTrackerHandler(ETSDealTrackerResultObj resobj){
	
	AccessCntrlFuncs acf = new AccessCntrlFuncs();
	String action = ETSDealTrackerCommonFuncs.getParameter(request,"action");
	String staskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
	


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

	StringBuffer b = new StringBuffer();
	b.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
	b.append("<tr><td>");

	ETSDealTrackerFunctions funcs = new ETSDealTrackerFunctions(Params);
	
	Vector notEditor = new Vector(); 
	notEditor.addElement(Defines.WORKSPACE_VISITOR);
	notEditor.addElement(Defines.ETS_EXECUTIVE);
	notEditor.addElement(Defines.WORKSPACE_CLIENT);
	
	
	if (!action.equals("")){
		
		if (action.equals("addtask") || action.equals("additask")){ 
			printHeader(action);
				
			writer.println(b.toString());
			
			if (action.equals("additask"))
				resobj.setIbmOnly(true);
				
			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}


			
			Hashtable h = new Hashtable();
			resobj = funcs.doAddTask(resobj,action,user_external,userRole,notEditor);
			
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
			}
			else if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getBackStr());
			}
			else{
				if (isSetMet)
					writer.println(ETSSelfDealTrackerPrint.doAddTask(Params,CurrentCatId,notEditor,resobj,h,resobj.getIbmOnly(),"").toString());
				else
					writer.println(ETSDealTrackerPrint.doAddTask(Params,CurrentCatId,h,resobj.getIbmOnly(),"").toString());	    
	    
			}
	    }
		else if (action.equals("addtask2")|| action.equals("additask2")){
			
			if (action.equals("additask2"))
				resobj.setIbmOnly(true);

			if (!editablePlan){
				printHeader(action);
				writer.println(b.toString());
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}

			resobj = funcs.verifyTaskForm(resobj,action,user_external,userRole,notEditor,es.gIR_USERN);
			
				
			if (resobj.getErrorFlag()){
				if (resobj.getIbmOnly()){
					action = "additask";
				}
				else{
					action = "addtask";
				}
				
				printHeader(action);
			
				writer.println(b.toString());
				
				if (isSetMet)
					writer.println(ETSSelfDealTrackerPrint.doAddTask(Params,CurrentCatId,notEditor,resobj,resobj.getFormHash(),resobj.getIbmOnly(),resobj.getErrorMsg()).toString());
				else
					writer.println(ETSDealTrackerPrint.doAddTask(Params,CurrentCatId,resobj.getFormHash(),resobj.getIbmOnly(),resobj.getErrorMsg()).toString());
			}
			else if (resobj.getAccessErrorFlag()){
				printHeader(action);
			
				writer.println(b.toString());	
				writer.println(resobj.getAccessErrorMsg());			
				printBackButton(resobj.getBackStr());
				return;
			}
			else{
				printHeader(action);
				
				writer.println(b.toString());
				resobj = funcs.doAddTask2(resobj,resobj.getFormHash(),resobj.getIbmOnly());
				
				if(isSetMet)
					writer.println(ETSSelfDealTrackerPrint.doAddTask2(Params,CurrentCatId,resobj));	
				else
					writer.println(ETSDealTrackerPrint.doAddTask2(Params,CurrentCatId,resobj));	

			}
		}
		else if(action.equals("details")){
			printHeader(action);
				
			writer.println(b.toString());
			resobj = funcs.getTaskDetails(resobj,userRole,user_external);
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			
			if(isSetMet)
				writer.println(ETSSelfDealTrackerPrint.printTaskDetails(Params,CurrentCatId,userRole,es,editablePlan,resobj,user_external,notEditor));
			else
				writer.println(ETSDealTrackerPrint.printTaskDetails(Params,CurrentCatId,es,userRole,resobj,user_external));
		}
		else if(action.equals("addtaskdoc")){
			printHeader(action);
			writer.println(b.toString());
			
			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}
		
			resobj = funcs.doAddTaskDoc(resobj,staskid,user_external,userRole,notEditor);
			
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			else if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			
			ETSDocumentManager docman = new ETSDocumentManager(this.Params);
			docman.ETSDocumentHandler();
		}
		else if(action.equals("deltaskdoc")){
			printHeader(action);
			writer.println(b.toString());
			
			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}
			
			//if(userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)){
			/*if(notEditor.contains(userRole)){
				writer.println("You are not authorized to perform this action");			
				printBackButton(resobj.getBackStr());
				return;
			}*/
			
			String sdocid = ETSDealTrackerCommonFuncs.getParameter(request,"docid");
			
			resobj = funcs.doDelTaskDoc(resobj,userRole,sdocid,staskid,notEditor,user_external);
			
			if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			int docid = new Integer(sdocid).intValue(); 
			int taskid = new Integer(staskid).intValue(); 
			
			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doDelTaskDoc(Params,CurrentCatId,resobj,docid,taskid));
			else
				writer.println(ETSDealTrackerPrint.doDelTaskDoc(Params,CurrentCatId,resobj,docid,taskid));
		}
		else if(action.equals("deltaskdoc2")){
			printHeader(action);
			writer.println(b.toString());

			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}
	
			String sdocid = ETSDealTrackerCommonFuncs.getParameter(request,"docid");
			
			resobj = funcs.doDelTaskDoc2(resobj,userRole,sdocid,staskid,notEditor,user_external);
			if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getSelfId());
				return;	
			}
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}

			int docid = new Integer(sdocid).intValue(); 
			int taskid = new Integer(staskid).intValue(); 
	
			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doDelTaskDoc2(Params,CurrentCatId,resobj,docid,taskid));
			else
				writer.println(ETSDealTrackerPrint.doDelTaskDoc2(Params,CurrentCatId,resobj,docid,taskid));
		}
		else if(action.equals("edetails")){
			//boolean ibmonly = false;
			boolean canEdit = false;
			
			printHeader(action);
			writer.println(b.toString());
			
			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}

			
			resobj = funcs.getTask(resobj,userRole,user_external);
			
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			
			/*
			if (isSuperAdmin || resobj.getTask().getOwnerId().equals(es.gIR_USERN) || resobj.getTask().getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER) || userRole.equals(Defines.WORKSPACE_MANAGER))
				canEdit = true;
			*/
			
			if (ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,resobj.getTask().isIbmOnly(),userRole,notEditor,resobj.getTask().getCreatorId(),resobj.getTask().getOwnerId(),user_external))
				canEdit = true;
			
			if (!canEdit){
				writer.println("Not authorized to perform this function");
				printBackButton(resobj.getBackStr());
				return;	
			}							

			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.editTaskDetails(Params,CurrentCatId,notEditor,resobj,new Hashtable(),resobj.getTask().isIbmOnly(),""));
			else
				writer.println(ETSDealTrackerPrint.editTaskDetails(Params,CurrentCatId,resobj,new Hashtable(),resobj.getTask().isIbmOnly(),""));
		}
		else if(action.equals("edetails2")){
			printHeader(action);
			writer.println(b.toString());
			
			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}

			resobj = funcs.getTaskDetails(resobj,userRole,user_external);
			if (resobj.getErrorFlag()){	
				writer.println(resobj.getErrorMsg());	
				printBackButton(resobj.getBackStr());
				return;
			}
			
			if (resobj.getTask().isIbmOnly())
				resobj.setIbmOnly(true);

			resobj = funcs.verifyTaskForm(resobj,action,user_external,userRole,notEditor,resobj.getTask().getCreatorId());
			
			
			if (resobj.getErrorFlag()){
				//editTaskDetails(resobj,resobj.getFormHash(),resobj.getIbmOnly(),resobj.getErrorMsg());
				if (isSetMet)
					writer.println(ETSSelfDealTrackerPrint.editTaskDetails(Params,CurrentCatId,notEditor,resobj,resobj.getFormHash(),resobj.getTask().isIbmOnly(),resobj.getErrorMsg()));
				else
					writer.println(ETSDealTrackerPrint.editTaskDetails(Params,CurrentCatId,resobj,resobj.getFormHash(),resobj.getTask().isIbmOnly(),resobj.getErrorMsg()));

			}
			else if (resobj.getAccessErrorFlag()){	
				writer.println(resobj.getAccessErrorMsg());	
				printBackButton(resobj.getBackStr());
				return;
			}
			else{
				resobj = funcs.editTaskDetails(resobj);
				if (isSetMet)
					writer.println(ETSSelfDealTrackerPrint.editTaskDetails2(Params,CurrentCatId,resobj.getTask(),resobj.getFormHash(),resobj.getIbmOnly()));
				else
					writer.println(ETSDealTrackerPrint.editTaskDetails2(Params,CurrentCatId,resobj.getTask(),resobj.getFormHash(),resobj.getIbmOnly(),resobj));
			}
		}
		else if(action.equals("addtaskcomm")){
			printHeader(action);
			writer.println(b.toString());
			
			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}
			
			String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");

			resobj = funcs.getTaskDetails(resobj,userRole,user_external);
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			
			ETSTask t = resobj.getTask();
			if (!(isSuperAdmin || t.getOwnerId().equals(es.gIR_USERN) || t.getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER)|| userRole.equals(Defines.WORKSPACE_MANAGER))){
				writer.println("You are not authorized to perform this action");
				printBackButton(resobj.getBackStr());
				return;	
			}

						
			if (isSetMet)
					writer.println(ETSSelfDealTrackerPrint.doAddTaskComment(Params,CurrentCatId,t,setid,"",""));
			else
					writer.println(ETSDealTrackerPrint.doAddTaskComment(Params,CurrentCatId,taskid,"",""));
		}
		else if(action.equals("addtaskcomm2")){

			if (!editablePlan){
				printHeader("addtaskcomm");
				writer.println(b.toString());
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}

			resobj = funcs.doAddTaskComment2(resobj,userRole,notEditor,user_external);
			
			if (resobj.getAccessErrorFlag()){
				printHeader("addtaskcomm");
				writer.println(b.toString());
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			if (resobj.getErrorFlag()){
				printHeader("addtaskcomm");
				writer.println(b.toString());
				if (isSetMet)
					writer.println(ETSSelfDealTrackerPrint.doAddTaskComment(Params,CurrentCatId,resobj.getTask(),setid,resobj.getTaskCommStr(),resobj.getErrorMsg()));
				else
					writer.println(ETSDealTrackerPrint.doAddTaskComment(Params,CurrentCatId,String.valueOf(resobj.getTaskId()),resobj.getTaskCommStr(),resobj.getErrorMsg()));
				return;	
			}
			
			printHeader(action);
			writer.println(b.toString());
			
			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doAddTaskComment2(Params,CurrentCatId,resobj));
			else
			//doAddTaskComment2(resobj);
				writer.println(ETSDealTrackerPrint.doAddTaskComment2(Params,CurrentCatId,resobj));	
		
		}
		else if(action.equals("adddtask")){
			printHeader(action);
			writer.println(b.toString());

			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}

			String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
			System.out.println("taskid="+taskid);
			
			resobj = funcs.doAddDepTask(resobj,userRole,taskid,user_external,notEditor);
			
			if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			if (resobj.getErrorFlag()){
				printBackButton(resobj.getBackStr());
				return;	
			}

			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doAddDepTask(Params,CurrentCatId,resobj,user_external,""));
			else
				writer.println(ETSDealTrackerPrint.doAddDepTask(Params,CurrentCatId,resobj,user_external,""));
			
		}
		else if(action.equals("adddtask2")){
			printHeader(action);
			writer.println(b.toString());

			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}

			String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
			String[] depTasks = request.getParameterValues("deptasks");
			
			resobj = funcs.doAddDepTask2(resobj,userRole,taskid,depTasks,user_external,notEditor);
			if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			
			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doAddDepTask2(Params,CurrentCatId,resobj,taskid,user_external,""));
			else
				writer.println(ETSDealTrackerPrint.doAddDepTask2(Params,CurrentCatId,resobj,taskid,user_external,""));
			
		}
		else if(action.equals("deldtask")){
			printHeader(action);
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
				printBackButton(resobj.getSelfId());
				return;	
			}
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getSelfId());
				return;	
			}
			
			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doDelDepTask(Params,CurrentCatId,resobj,user_external,""));
			else
				writer.println(ETSDealTrackerPrint.doDelDepTask(Params,CurrentCatId,resobj,user_external,""));
		}
		else if(action.equals("deldtask2")){
			printHeader(action);
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
				printBackButton(resobj.getSelfId());
				return;	
			}
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getSelfId());
				return;	
			}

			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doDelDepTask2(Params,CurrentCatId,resobj));
			else
				writer.println(ETSDealTrackerPrint.doDelDepTask2(Params,CurrentCatId,resobj));
			
		}
		else if(action.equals("deltask")){
			printHeader(action);
			writer.println(b.toString());

			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}

			resobj = funcs.doDelTask(resobj,userRole,staskid,notEditor,user_external);
			if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			if (resobj.getErrorFlag()){
				writer.println(b.toString());
				printBackButton(resobj.getBackStr());
				return;	
			}
			
			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doDelTask(Params,CurrentCatId,resobj));
			else
				writer.println(ETSDealTrackerPrint.doDelTask(Params,CurrentCatId,resobj));
		}
		else if(action.equals("deltask2")){
			printHeader(action);
			writer.println(b.toString());

			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}

			resobj = funcs.doDelTask2(resobj,userRole,staskid,notEditor,user_external);
			if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			
			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doDelTask2(Params,CurrentCatId,resobj));
			else
				writer.println(ETSDealTrackerPrint.doDelTask2(Params, CurrentCatId,resobj));
	
		}
	    else if(action.equals("editdash")){
			printHeader(action);
			writer.println(b.toString());

			if (!editablePlan){
				writer.println(ETSSelfDealTrackerPrint.notEditableError(Params,CurrentCatId,resobj));
				return;
			}

			resobj = funcs.doEditDash(resobj,userRole,notEditor,user_external);
			
			if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}
			if (resobj.getErrorFlag()){
				writer.println(resobj.getErrorMsg());
				printBackButton(resobj.getBackStr());
				return;	
			}

			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doEditDash(Params,CurrentCatId,notEditor,resobj,"",false));
			else
				writer.println(ETSDealTrackerPrint.doEditDash(Params,CurrentCatId,resobj,"",false));
	    }
		else if(action.equals("editdash2")){
			printHeader(action);
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
					printBackButton(resobj.getBackStr());
					return;	
				}
				if (isSetMet)
					writer.println(ETSSelfDealTrackerPrint.doEditDash(Params,CurrentCatId,notEditor,resobj,msg,true));
				else
					writer.println(ETSDealTrackerPrint.doEditDash(Params,CurrentCatId,resobj,msg,true));
				return;	
			}
			if (resobj.getAccessErrorFlag()){
				writer.println(resobj.getAccessErrorMsg());
				printBackButton(resobj.getSelfId());
				return;	
			}
			if (isSetMet)
				writer.println(ETSSelfDealTrackerPrint.doEditDash2(Params,CurrentCatId,resobj));
			else
				writer.println(ETSDealTrackerPrint.doEditDash2(Params,CurrentCatId,resobj));
		}
		else if(action.equals("print")){
			doDashboardView(resobj,user_external,true,notEditor);
		}
		else if (action.equals("importtask")) {
			printHeader(action);
		    writer.println(ETSDealTrackerPrint.doImportTask(Params,CurrentCatId,resobj.getFormHash(),resobj.getIbmOnly(),request.getParameter("error")));
		}
		else if (action.equals("importtask2")) {
			printHeader(action);
		    writer.println(ETSDealTrackerPrint.doImportTask2(Params,CurrentCatId,resobj.getFormHash(),resobj.getIbmOnly(),request.getParameter("error")));
		}
		else if (action.equals("exporttask")) {
			printHeader(action);
		    writer.println(ETSDealTrackerPrint.doExportTask(Params,CurrentCatId,resobj.getFormHash(),resobj.getIbmOnly(),resobj.getErrorMsg()).toString());
		}
	}
	else{ //dashboard
		if (!isSetMet)
			printHeader(action);
		else
			printSetMetHeader("");
			
		doDashboardView(resobj,user_external,false,notEditor);
	}
}



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
		
	if (isSetMet)
		writer.println(ETSSelfDealTrackerPrint.doDashboardView(o,userRole,user_external,printview,Params,editablePlan,showclose,notEditor));
	else
		writer.println(ETSDealTrackerPrint.doDashboardView(o,userRole,user_external,printview,Params,notEditor));
	//writer.println(printDashboardButtons(o,printview,o.getResultTasks().size()>0,user_external));
	
	
		
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
		buf.append("<form action=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&tc="+TopCatId+"&linkid="+linkid+"\" method=\"post\" name=\"editdashForm\">\n");
		buf.append("<input type=\"hidden\" name=\"action\" value=\"editdash2\" />");
		
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"2\" width=\"600\" border=\"0\">\n");
		buf.append("<tr><td colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
			
		Vector tasks = o.getResultTasks();
		for (int t = 0; t < tasks.size(); t++){
			ETSTask task = (ETSTask)tasks.elementAt(t);
			
			buf.append("<tr><td colspan=\"2\" class=\"small\" colspan=\"4\" valign=\"top\"><span style=\"color:#3c5f84\"><b>Task: ");
			
			buf.append(task.getId()+"</b></span>");
			if (task.hasDocs()){
				buf.append(" &nbsp;<img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" />"); //img
			}
			buf.append("</td></tr>");

			
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			
			buf.append("<tr><td class=\"small\" width=\""+w1+"\"><label for=\"title\"><b>Title</b></label></td>");
			buf.append("<td class=\"small\" width=\""+w2+"\"><label for=\"section\"><b>Section</b></label></td></tr>");
			buf.append("<tr><td width=\""+w1+"\"><input type=\"text\" id=\"title\" name=\"title_"+task.getId()+"\" size=\"40\" style=\"width:250px\" width=\"275px\" maxlength=\"175\" value=\""+(sub?getParameter(request,"title_"+task.getId()):task.getTitle())+"\" class=\"iform\" /></td>");
			buf.append("<td width=\""+w2+"\"><input id=\"section\" type=\"text\" name=\"section_"+task.getId()+"\" size=\"25\"  style=\"width:125px\" width=\"125px\" maxlength=\"10\" value=\""+(sub?getParameter(request,"section_"+task.getId()):task.getSection())+"\" class=\"iform\" /></td></tr>");
			
			
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			
			buf.append("<tr><td class=\"small\"><label for=\"owner\"><b>Owner</b></label></td>");
			buf.append("<td class=\"small\"><label for=\"status\"><b>Status</b></label></td></tr>");
			
			buf.append("<tr>");
			String so = (sub?getParameter(request,"owner_"+task.getId()):task.getOwnerId());
			buf.append("<td align=\"left\" valign=\"top\">\n");

			Vector users = new Vector();
			if (task.isIbmOnly()){
				users = o.getIbmUsers();
			}
			else{
				users = o.getUsers();
			}
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
			
			String ss = (sub?getParameter(request,"status_"+task.getId()):task.getStatus());
			buf.append("<td width=\""+w2+"\">");
			buf.append("<select name=\"status_"+task.getId()+"\" id=\"status\" class=\"iform\">");
			if (ss.equals(Defines.GREEN))
				buf.append("<option value=\""+Defines.GREEN+"\" selected=\"selected\">Green</option>");
			else
				buf.append("<option value=\""+Defines.GREEN+"\">Green</option>");
		
			if (ss.equals(Defines.YELLOW))
				buf.append("<option value=\""+Defines.YELLOW+"\" selected=\"selected\">Yellow</option>");
			else
				buf.append("<option value=\""+Defines.YELLOW+"\">Yellow</option>");
	
			if (ss.equals(Defines.RED))
				buf.append("<option value=\""+Defines.RED+"\" selected=\"selected\">Red</option>");
			else
				buf.append("<option value=\""+Defines.RED+"\">Red</option>");
			buf.append("</select>\n");
			buf.append("</td></tr>");
					
					
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
			
			

			buf.append("<tr><td class=\"small\" colspan=\"2\"><label for=\"date\"><b>Due date</b></label></td></tr>");
			buf.append("<tr><td class=\"small\" colspan=\"2\" valign=\"top\">\n");
			
			buf.append("Month: ");
			int im = (sub?(new Integer(getParameter(request,"month_"+task.getId())).intValue()):task.getTaskMonth(task.getDueDate()));
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


			int idy = (sub?(new Integer(getParameter(request,"day_"+task.getId())).intValue()):task.getTaskDay(task.getDueDate()));
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
			int iy = (sub?(new Integer(getParameter(request,"year_"+task.getId())).intValue()):task.getTaskYear(task.getDueDate()));
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
		
			buf.append("<tr><td colspan=\"2\"><table summary=\"\" width=\"600\" border=\"0\">\n");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr><td style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td></tr>");
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
		buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
		buf.append("<tr>");
		buf.append("<td align=\"left\" valign=\"top\" colspan=\"2\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");
		buf.append("<td><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"20\" alt=\"\" /></td>");
		buf.append("<td align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
		buf.append("<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\"  border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td>");
		buf.append("<td align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Cancel</a></td>");
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
			buf.append("<tr height=\"21\"><td colspan=\"2\">You have successfully updated the tasks.</td></tr>");
			
			
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr>");
			buf.append("<td style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
			buf.append("</td></tr>");
			buf.append("</table>\n");

			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
			buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr>");

			buf.append("<td align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
			buf.append("<img src=\"" + Defines.BUTTON_ROOT + "continue.gif\"  border=\"0\"  height=\"21\" width=\"120\" alt=\""+o.getBackStr()+"\" /></a></td>");
			buf.append("</tr>\n");
			buf.append("</table>\n");
			writer.println(buf.toString());
	
	}
*/


	static public Vector getIBMMembers(Vector membs, Connection conn){
		Vector new_members = new Vector();

		for (int i = 0; i<membs.size();i++){
			ETSUser mem = (ETSUser)membs.elementAt(i);
			try{
				String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,mem.getUserId());
				String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
				if (decaftype.equals("I")){
					new_members.addElement(mem);
				}
			}
			catch(AMTException a){
				System.out.println("amt exception in getibmmembers err= "+a);
			}
			catch(SQLException s){
				System.out.println("sql exception in getibmmembers err= "+s);
			}
		}

		return new_members;
	}


	public String[] getHeader(String action,boolean getBMS){
		String str = new String();
		
		if (action.equals("addtask")){
			str = "Add task";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,"Fields marked with <span class=\"ast\"><b>*</b></span> are mandatory fields."};
		}
		else if (action.equals("additask")){
			str = "Add IBM only task";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,"Fields marked with <span class=\"ast\"><b>*</b></span> are mandatory fields."};
		}
		else if (action.equals("addtask2")){
			str = "Add task";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,"Confirmation"};
		}
		else if (action.equals("additask2")){
			str = "Add IBM only task";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,"Confirmation"};
		}
		else if (action.equals("details")){
			str = "Task details";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("edetails")){
			str = "Edit task details";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			
			return new String[]{str,"Fields marked with <span class=\"ast\"><b>*</b></span> are mandatory fields."};
		}
		else if (action.equals("edetails2")){
			str = "Edit task details";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			
			return new String[]{str,""};
		}
		else if (action.equals("addtaskdoc")){
			str = "Add task document";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			
			return new String[]{str,""};
		}
		else if (action.equals("addtaskdoc2")){
			str = "Add task document";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("deltaskdoc") || action.equals("deltaskdoc2")){
			str = "Delete task document";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("addtaskcomm")){
			str = "Add task comment";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("addtaskcomm2")){
			str = "Add task comment";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("deltask")){
			str = "Delete task";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("deltask2")){
			str = "Delete task";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("editdash")){
			str = "Edit overview";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("editdash2")){
			str = "Edit overview";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("adddtask") || action.equals("adddtask2")){
			str = "Add dependent task ";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("deldtask") || action.equals("deldtask2")){
			str = "Remove dependent task";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("importtask")){
			str = "Import Tasks";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,"Fields marked with <span class=\"ast\"><b>*</b></span> are mandatory fields."};
		}
		else if (action.equals("importtask2")){
			str = "Import Tasks";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("exporttask")){
			str = "Export Tasks";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);
			return new String[]{str,""};
		}
		else if (action.equals("")){
			//return new String[]{"Contracts","<span class=\"ast\"><b>*</b></span>Denotes IBM employee"};
			//return new String[]{ETSUtils.getBookMarkString("Contracts","", true),"<span class=\"ast\"><b>*</b></span>Denotes IBM employee"};
			str = "Contracts";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", true);
			return new String[]{str,""};
		}
		else{
			str = "Contracts";
			if (getBMS)
				str = ETSUtils.getBookMarkString(str,"", false);

			return new String[]{str,""};
		}
	}
	private void printHeader(String action){
		if (isSetMet){
			printSetMetHeader(action);
			return;	
		}
		
		StringBuffer buf = new StringBuffer();
			
		try{
			//gutter between content and right column
			writer.println("<td rowspan=\"4\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			// Right column start
			writer.println("<td rowspan=\"4\" width=\"150\" valign=\"top\">");
		 	ETSContact contact = new ETSContact(Project.getProjectId(), request);
		 	contact.printContactBox(writer);
		 	writer.println("</td></tr>");
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
		String[] header = getHeader(action, true);
		
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"top\">");
		//buf.append("<span class=\"subtitle\">"+header[0]+"</span></td></tr>");
		buf.append(header[0]+"</td></tr>");
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\" class=\"small\">");
		buf.append(header[1]+"</td></tr>");
		
		// v2srikau: Added new lines in the header.
		if (header.length > 2) {
			for (int i=2; i < header.length; i++) {
			    buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\" class=\"small\">");
				buf.append(header[i]+"</td></tr>");
			}
		}
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\">");
		buf.append("<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"443\" valign=\"bottom\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>");				
		writer.println(buf.toString());
		
	}
	
	
	private void printSetMetHeader(String action){
		StringBuffer buf = new StringBuffer();
			
		buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
		
		if (!action.equals("")){
			String str = ((String[])getHeader(action,false))[0];
			buf.append("<tr><td valign=\"middle\" class=\"subtitle\">");
			buf.append("<span style=\"greytext\">"+str+"</span></td></tr>");
		}
		
		buf.append("<tr valign=\"middle\">");
		buf.append("<td class=\"tdblue\" height=\"18\">&nbsp;" + this.setMetName + "</td>");
		buf.append("</tr>");
		buf.append("</table>");	
		writer.println(buf.toString());
	}


	private void printBackButton(String backStr){
		if (isSetMet)
			writer.println(ETSSelfDealTrackerPrint.printBackButton(Params,CurrentCatId,"M",setid));
		else{
			StringBuffer buf = new StringBuffer();
			
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"30\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr>");
			buf.append("<td style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" />");
			buf.append("</td></tr>");
			buf.append("</table>\n");
	
			buf.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");
			buf.append("<tr>");
			buf.append("<td align=\"right\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">");
			buf.append("<img src=\"" + Defines.ICON_ROOT + "bk_c.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Back to "+backStr+"\" /></a></td>");
			buf.append("<td align=\"left\" valign=\"middle\">&nbsp;<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"\">Back to "+backStr+"</a></td>");
			buf.append("</tr>\n");
			buf.append("</table>\n");
		
			writer.println(buf.toString());
		}
	}


} // end of class







