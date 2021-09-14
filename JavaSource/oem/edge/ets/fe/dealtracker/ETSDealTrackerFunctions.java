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
 * Date: 1/19/2004
 */

package oem.edge.ets.fe.dealtracker;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.CommonEmailHelper;

public class ETSDealTrackerFunctions {

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
	//protected String userRole;
		
    protected ETSDatabaseManager databaseManager;
    protected int CurrentCatId;
	protected ETSCat this_current_cat;
	static private final String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
	static private final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	static private final SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");

    public ETSDealTrackerFunctions(ETSParams parameters){
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




	public ETSDealTrackerResultObj getDashboardTasks(ETSDealTrackerResultObj o, String userRole, boolean user_external,boolean printview){
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
		Vector tasks= new Vector();
		
		try{
			tasks = ETSDealTrackerDAO.getTasks(Project.getProjectId(),o.getSelfId(),user_external,sortby,ad);
			o.setResultTasks(tasks);
		}
		catch(SQLException se){
			System.out.println("sql error in tracker.taskhandler = "+se);
		}
		catch(Exception e){
			System.out.println("exception error in tracker.taskhandler = "+e);
		}
    
			 
		return o;
	}


	public ETSDealTrackerResultObj doAddTask(ETSDealTrackerResultObj o,String action,boolean user_external,String userRole,Vector notEditors){
		boolean ibmonly = false;
		if (action.equals("additask"))
			ibmonly = true;
	
	 
		if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,ibmonly,userRole,notEditors,es.gIR_USERN,es.gIR_USERN,user_external)){
			o.setErrorFlag(true);
			o.setErrorMsg("You are not entitled to perform this action");
			return o;
		}
		

		return o;
	}


	public ETSDealTrackerResultObj verifyTaskForm(ETSDealTrackerResultObj o,String action,boolean user_external,String userRole,Vector notEditor,String creatorId){
		
		Hashtable h = new Hashtable();
		
		h.put("taskid",ETSDealTrackerCommonFuncs.getParameter(request,"taskid"));
		h.put("section",ETSDealTrackerCommonFuncs.getParameter(request,"section"));
		h.put("title",ETSDealTrackerCommonFuncs.getParameter(request,"title"));
		h.put("month",ETSDealTrackerCommonFuncs.getParameter(request,"month"));
		h.put("day",ETSDealTrackerCommonFuncs.getParameter(request,"day"));
		h.put("year",ETSDealTrackerCommonFuncs.getParameter(request,"year"));
		h.put("status",ETSDealTrackerCommonFuncs.getParameter(request,"status"));
		h.put("owner",ETSDealTrackerCommonFuncs.getParameter(request,"owner"));
		h.put("company",ETSDealTrackerCommonFuncs.getParameter(request,"company"));
		h.put("wreq",ETSDealTrackerCommonFuncs.getParameter(request,"wreq"));
		h.put("areq",ETSDealTrackerCommonFuncs.getParameter(request,"areq"));
		o.setFormHash(h);				
		String[] s = verifyAddTaskFields(h,o.getIbmOnly(),"");
			
		if (s[0].equals("0")){
			o.setErrorFlag(true);
			o.setErrorMsg(s[1]);
		}
		else if (!(ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,o.getIbmOnly(),userRole,notEditor,creatorId,ETSDealTrackerCommonFuncs.getParameter(request,"owner"),user_external))){
			o.setAccessErrorFlag(true);
			o.setAccessErrorMsg("You are not authorized to perform this action");
		}
		
		

		return o;
	}


	public String []verifyAddTaskFields(ETSTask task) {
	    String strId = "";
	    
	    if (task.getId() != 0) {
	        strId = Integer.toString(task.getId());
	    }
	    
	    Hashtable hash = new Hashtable();
	    hash.put("taskid",strId);
		hash.put("section",task.getSection());
		hash.put("title",task.getTitle());
		hash.put("month",task.getDueDateStrs()[0]);
		hash.put("day",task.getDueDateStrs()[1]);
		hash.put("year",task.getDueDateStrs()[2]);
		hash.put("status",task.getStatus());
		hash.put("owner",task.getOwnerId());
		hash.put("company",task.getCompany());
	    
	    return verifyAddTaskFields(hash, task.isIbmOnly(), strId);
	}

	private String[] verifyAddTaskFields(Hashtable h, boolean ibmonly, String id){
		String[] s = new String[]{"1",""};
		//check title, valid due date,status,owner,company
		
		//section
		String section= ((String)h.get("section")).trim();
		if (section.length()>30){
			s[0]="0";
			if (id.equals(""))
				s[1]="Input Error: \"The section must be 0-30 characters long.\"";				
			else
				s[1]="Input Error: \"The section must be 0-30 characters long for task="+id+".\"";
			return s;
		}
		
		//title
		String title = ((String)h.get("title")).trim();
		if (title.equals("")){
			s[0]="0";
			if (id.equals(""))
				s[1]="Input Error: \"A title must be specified.\"";	
			else
				s[1]="Input Error: \"A title must be specified for task="+id+".\"";	
	
			return s;
		}
		else if (title.length()==0 || title.length()>100){
			s[0]="0";
			if (id.equals(""))
				s[1]="Input Error: \"The title must be 1-100 characters long.\"";				
			else
				s[1]="Input Error: \"The title must be 1-100 characters long for task="+id+".\"";
			return s;
		}
		
		//date
		String smonth = (String)h.get("month");
		String sday = (String)h.get("day");
		String syear = (String)h.get("year");
		System.out.println("month="+smonth);
		System.out.println("day="+sday);
		System.out.println("year="+syear);
		
		if (smonth.equals("")){
			s[0]="0";
			if (id.equals(""))
				s[1]="Input Error: \"A valid month for due date must be specified.\"";
			else
				s[1]="Input Error: \"A valid month for due date must be specified for task="+id+".\"";
			return s;
		}
		else if (sday.equals("")){
			s[0]="0";
			if (id.equals(""))
				s[1]="Input Error: \"A valid day for due date must be specified.\"";
			else
				s[1]="Input Error: \"A valid day for due date must be specified for task="+id+".\"";
			return s;
		}
		else if (syear.equals("")){
			s[0]="0";
			if (id.equals(""))
				s[1]="Input Error: \"A valid year for due date must be specified.\"";
			else
				s[1]="Input Error: \"A valid year for due date must be specified for task="+id+".\"";
			return s;
		}
		else{
			int month = Integer.parseInt(smonth.trim());
			int day = Integer.parseInt(sday.trim());
			int year = Integer.parseInt(syear.trim());
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR,year);
			cal.set(Calendar.MONTH,month);
			int iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			int iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			System.out.println("max  for '"+cal.get(Calendar.MONTH)+"'= "+iMaxDaysInMonth);
			System.out.println("min  for '"+cal.get(Calendar.MONTH)+"'= "+iMinDaysInMonth);
			
			if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
				cal.set(Calendar.DAY_OF_MONTH,day);				
			}
			else{
				s[0]="0";
				if (id.equals(""))
					s[1]="Input Error: \"Invalid due date specified.  Please review date entered.\"";
				else
					s[1]="Input Error: \"Invalid due date specified for task="+id+".  Please review date entered.\"";
				return s;
			}
		}

		//status
		String status = (String)h.get("status");
		if (status.equals("") || status.equals("0")){
			s[0]="0";
			
			s[1]="Input Error: \"The status of the task must be specified.\"";	
			return s;
		}

		try{
			//owner
			String owner = ((String)h.get("owner")).trim();

			String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, owner);
			String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
							
			if (owner.equals("") || owner.equals("0")){
				s[0]="0";
				if (id.equals(""))
					s[1]="Input Error: \"An owner must be assigned to the task.\"";	
				else
					s[1]="Input Error: \"An owner must be assigned to the task for task="+id+".\"";	
				return s;
			}
			else if(ibmonly){
				if(!decaftype.equalsIgnoreCase("I")){
					s[0]="0";
					if (id.equals(""))
						s[1]="Input Error: \"External owner assigned to IBM only task.\"";	
					else
						s[1]="Input Error: \"External owner assigned to IBM only task for task="+id+".\"";	
					return s;
				}
			}
		
			//company
			String company = (String)h.get("company");  //0=none, 1=IBM, 2=company, 3=both
		
			if (company.equals("") || company.equals("0")){
				s[0]="0";
				if (id.equals(""))
					s[1]="Input Error: \"A company must be specified.\"";	
				else
					s[1]="Input Error: \"A company must be specified for task="+id+".\"";	
				return s;
			}
		}
		catch(Exception e){
			
		}		
		
		return s;
	}


	public ETSDealTrackerResultObj doAddTask2(ETSDealTrackerResultObj o, Hashtable h, boolean ibmonly){
		ETSTask task = new ETSTask();
		task.setProjectId(Project.getProjectId());
		task.setSelfId(o.getSelfId());
		task.setCreatorId(es.gIR_USERN);
		//task.setCreatedDate();
		task.setSection(ETSDealTrackerCommonFuncs.getHashStrValue(h,"section"));
		task.setTitle(ETSDealTrackerCommonFuncs.getHashStrValue(h,"title"));
		task.setStatus(ETSDealTrackerCommonFuncs.getHashStrValue(h,"status"));
		task.setDueDate(ETSDealTrackerCommonFuncs.getHashStrValue(h,"month"),ETSDealTrackerCommonFuncs.getHashStrValue(h,"day"),ETSDealTrackerCommonFuncs.getHashStrValue(h,"year"));
		task.setOwnerId(ETSDealTrackerCommonFuncs.getHashStrValue(h,"owner"));
		task.setWorkRequired(ETSDealTrackerCommonFuncs.getHashStrValue(h,"wreq"));
		task.setActionRequired(ETSDealTrackerCommonFuncs.getHashStrValue(h,"areq"));
		System.out.println("ibmonly = "+String.valueOf(ibmonly));
		task.setIbmOnly(ibmonly);
		task.setParentTaskId(0);
		task.setCompany(ETSDealTrackerCommonFuncs.getHashStrValue(h,"company"));
		task.setLastUserid(es.gIR_USERN);
		task.setCreatedDate();
		task.setTrackerType(o.getTrackerType());
		//task.setLastTimestamp();
		
		String success = "0";
		try{
			success = ETSDealTrackerDAO.addTask(task);
		}
		catch(Exception e){
			success = "0";	
		}
		
		if (success.equals("0")){
			//error page
			o.setErrorFlag(true);
			o.setErrorMsg("Error adding task");
			
		}
		else{
			String taskid = success;
			task.setId(new Integer(taskid).intValue());
			o.setTask(task);
			newTaskEmail(taskid, task);
					
		}
		
		return o;
	}


	public ETSDealTrackerResultObj getTaskDetails(ETSDealTrackerResultObj o,String userRole,boolean user_external){
		try{
			String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
			if (taskid.equals("")){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid task request made.");	
				return o;
			}
				
			ETSTask task = ETSDealTrackerDAO.getTask(taskid,Project.getProjectId(),o.getSelfId());
			if (task == null){
				System.out.println("null null null");
				o.setErrorFlag(true);
				o.setErrorMsg("Task not found");
				return o;	
			}
			else{
				if (task.isIbmOnly() && (user_external || userRole.equals(Defines.WORKSPACE_CLIENT))){
					o.setErrorFlag(true);
					o.setErrorMsg("You are not authorized to view this task");
					return o;	
				}
				
				
				o.setTask(task);
				o.setTaskDocs(ETSDealTrackerDAO.getTaskDocs(task.getId(),Project.getProjectId(),o.getSelfId()));
				o.setTaskComms(ETSDealTrackerDAO.getTaskComments(task.getId(),Project.getProjectId(),o.getSelfId()));
				o.setDepTasks(ETSDealTrackerDAO.getDepTasks(task.getId(),Project.getProjectId(),o.getSelfId()));
			}
		}
		catch(Exception e){
			
		}	
		return o;
			
	}
	
	public ETSDealTrackerResultObj getTask(ETSDealTrackerResultObj o,String userRole,boolean user_external){
		try{
			String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
			if (taskid.equals("")){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid task request made.");	
				return o;
			}
			
			ETSTask task = ETSDealTrackerDAO.getTask(taskid,Project.getProjectId(),o.getSelfId());
			if (task == null){
				System.out.println("null null null");
				o.setErrorFlag(true);
				o.setErrorMsg("Task not found");
				return o;	
			}
			else{
				if (task.isIbmOnly() && (user_external || userRole.equals(Defines.WORKSPACE_CLIENT))){
					o.setErrorFlag(true);
					o.setErrorMsg("You are not authorized to view this task");
					return o;	
				}
			
			
				o.setTask(task);
			}
		}
		catch(Exception e){
		
		}	
		return o;
		
	}


	public ETSDealTrackerResultObj editTaskDetails(ETSDealTrackerResultObj o){
		
		ETSTask task = o.getTask();
		Hashtable h = o.getFormHash();
		
		task.setProjectId(Project.getProjectId());
		task.setSelfId(o.getSelfId());
		//task.setCreatorId(task.getCreatorId());
		task.setSection(ETSDealTrackerCommonFuncs.getHashStrValue(h,"section"));
		task.setTitle(ETSDealTrackerCommonFuncs.getHashStrValue(h,"title"));
		task.setStatus(ETSDealTrackerCommonFuncs.getHashStrValue(h,"status"));
		task.setDueDate(ETSDealTrackerCommonFuncs.getHashStrValue(h,"month"),ETSDealTrackerCommonFuncs.getHashStrValue(h,"day"),ETSDealTrackerCommonFuncs.getHashStrValue(h,"year"));
		task.setOwnerId(ETSDealTrackerCommonFuncs.getHashStrValue(h,"owner"));
		task.setWorkRequired(ETSDealTrackerCommonFuncs.getHashStrValue(h,"wreq"));
		task.setActionRequired(ETSDealTrackerCommonFuncs.getHashStrValue(h,"areq"));
		task.setCompany(ETSDealTrackerCommonFuncs.getHashStrValue(h,"company"));
		task.setIbmOnly(task.getIbmOnly());  //shouldn't change
		//task.setParentTaskId(0); doesn't get updated
		task.setLastUserid(es.gIR_USERN);
		//task.setLastTimestamp();
		
		boolean success = false;
		try{
			success = ETSDealTrackerDAO.editTask(task);
		}
		catch(Exception e){
			e.printStackTrace();
			success = false;	
		}
		
		if (!success){
			//error page	
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while updating task.  Please try again.");		

		}
		else{
			editTaskEmail(task);
		}
		
		return o;
	}
	
	
	
	public ETSDealTrackerResultObj doAddTaskComment2(ETSDealTrackerResultObj o,String userRole,Vector notEditors,boolean user_external){
		String taskid = ETSDealTrackerCommonFuncs.getParameter(request,"taskid");
		String comm = ETSDealTrackerCommonFuncs.getParameter(request,"comment");
		
		o.setTaskCommStr(comm);
		o.setTaskId(new Integer(taskid).intValue());
		
		if (taskid.equals("")){
			o.setAccessErrorFlag(true);
			o.setAccessErrorMsg("Invalid URL");
			return o;	
		}
		else if (comm.trim().equals("") || comm.length()>1000){
			o.setErrorFlag(true);
			o.setErrorMsg("Comment must 1-1000 characters long");
			return o;
		}
		
		
		ETSTask t = new ETSTask();
		
		ETSTaskComment taskcomm = new ETSTaskComment();

		int success = 0;
		try{
			t = ETSDealTrackerDAO.getTask(taskid,Project.getProjectId(),o.getSelfId());
			if (t == null){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("Invalid task");
				return o;	
			}
			if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,t.isIbmOnly(),userRole,notEditors,t.getCreatorId(),t.getOwnerId(),user_external)){
			//if (!(isSuperAdmin || t.getOwnerId().equals(es.gIR_USERN) || t.getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER)|| userRole.equals(Defines.WORKSPACE_MANAGER))){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");
				return o;	
			}
			taskcomm.setTaskId(taskid);
			taskcomm.setProjectId(Project.getProjectId());
			taskcomm.setComment(comm);
			taskcomm.setSelfId(t.getSelfId());
			taskcomm.setLastUserid(es.gIR_USERN);

			success = ETSDealTrackerDAO.addTaskComment(taskcomm);
		}
		catch(Exception e){
			success = 0;	
		}
		
		if (success == 0){
			o.setAccessErrorFlag(true);
			o.setAccessErrorMsg("Error occurred while adding comment");
			return o;	
		}
		o.setTask(t);
		o.setTaskComm(taskcomm);
		addTaskCommentEmail(t,taskcomm);
		
		return o;
	}
	
	
	
	
	
	public ETSDealTrackerResultObj doAddDepTask(ETSDealTrackerResultObj o,String userRole, String taskid, boolean user_external,Vector notEditors){
		try{

			ETSTask t = ETSDealTrackerDAO.getTask(taskid,Project.getProjectId(),o.getSelfId());	

			if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,t.isIbmOnly(),userRole,notEditors,t.getCreatorId(),t.getOwnerId(),user_external)){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");
				return o;	
			}
			
			/*
			if (!isSuperAdmin && (t.isIbmOnly() && user_external)){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");
				return o;	
			}*/
			o.setTask(t);
			o.setResultTasks(ETSDealTrackerDAO.getEligibleTasks(t.getId(),t.isIbmOnly(),Project.getProjectId(),o.getSelfId(),user_external));
		}
		catch(Exception e){
			e.printStackTrace();
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while performing this action");
			return o;	
			
		}
		return o;
			
	}
	
	
	
	
	
	public ETSDealTrackerResultObj doAddDepTask2(ETSDealTrackerResultObj o,String userRole, String taskid, String[] depTasks, boolean user_external,Vector notEditors){
		ETSTask t = new ETSTask();
		String dtasks = "";
		
		
		boolean success = false;
		try{
			t = ETSDealTrackerDAO.getTask(taskid,Project.getProjectId(),o.getSelfId());
			if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,t.isIbmOnly(),userRole,notEditors,t.getCreatorId(),t.getOwnerId(),user_external)){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");
				return o;	
			}

						
			for(int i =0; i<depTasks.length;i++){
				if (i==0){
					dtasks = depTasks[i];
				}
				else{
					dtasks = dtasks + "," +depTasks[i];	
				}
			}

			success = ETSDealTrackerDAO.addDepTasks(taskid,dtasks,Project.getProjectId(),o.getSelfId());
			
			o.setTask(t);
			if (!success){
				o.setErrorFlag(true);
				o.setErrorMsg("Error occurred while performing this action");
				return o;	
			}
			else{
				Vector dTasks = new Vector();
				dTasks = ETSDealTrackerDAO.getTasks(dtasks,Project.getProjectId(),o.getSelfId());
				o.setResultTasks(dTasks);
			}		
			
		}
		catch(Exception e){
			success = false;
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while performing this action");
			return o;	
		}
		
		return o;	
	}

	public ETSDealTrackerResultObj doDelDepTask(ETSDealTrackerResultObj o,String userRole, String taskid,String dtaskid,boolean user_external,Vector notEditors){
		
		try{
			ETSTask task = ETSDealTrackerDAO.getTask(String.valueOf(taskid),Project.getProjectId(),o.getSelfId());
			ETSTask dTask = ETSDealTrackerDAO.getTask(String.valueOf(dtaskid),Project.getProjectId(),o.getSelfId());
			
			if (task == null || dTask == null){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid request.");
				return o;
			}
			if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,task.isIbmOnly(),userRole,notEditors,task.getCreatorId(),task.getOwnerId(),user_external)){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");			
				return o;
			}
			if ((user_external || userRole.equals(Defines.WORKSPACE_CLIENT)) && dTask.isIbmOnly()){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");			
				return o;
			}
			
			
			o.setTask(task);
			o.setDepTask(dTask);
						
		}
		catch(Exception e){
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while performing this action");
			return o;
		}
		return o;
	}
	
	
	public ETSDealTrackerResultObj doDelDepTask2(ETSDealTrackerResultObj o, String userRole, String staskid,String sdtaskid,Vector notEditors,boolean user_external){
		try{
			ETSTask task = ETSDealTrackerDAO.getTask(staskid,Project.getProjectId(),o.getSelfId());
			ETSTask dTask = ETSDealTrackerDAO.getTask(sdtaskid,Project.getProjectId(),o.getSelfId());
			
			if (task == null || dTask == null){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid request specified");
				return o;	
			}
			
			/*boolean canEdit = false;
			if (isSuperAdmin || task.getOwnerId().equals(es.gIR_USERN) || task.getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER) || userRole.equals(Defines.WORKSPACE_MANAGER)){
				canEdit = true;
			}
			if (!(user_external && (task.isIbmOnly() || dTask.isIbmOnly()))){
				canEdit = true;
			}

			if (!canEdit){
				o.setErrorFlag(true);
				o.setErrorMsg("You are not authorized to perform this action.");
				return o;
			}*/
			
			if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,task.isIbmOnly(),userRole,notEditors,task.getCreatorId(),task.getOwnerId(),user_external)){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");			
				return o;
			}
			if ((user_external || userRole.equals(Defines.WORKSPACE_CLIENT)) && dTask.isIbmOnly()){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");			
				return o;
			}
	

			int dtaskid = dTask.getId();
				
			boolean success = ETSDealTrackerDAO.removeDepTask(dtaskid,Project.getProjectId(),o.getSelfId());
			
			o.setTask(task);
			o.setDepTask(dTask);

		}
		catch(Exception e){
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while performing this action");
		}
		return o;
	}








	public ETSDealTrackerResultObj doDelTask(ETSDealTrackerResultObj o,String userRole, String taskid,Vector notEditors,boolean user_external){
		//check if user allowed to delete
		
		try{
			ETSTask task = ETSDealTrackerDAO.getTask(taskid,Project.getProjectId(),o.getSelfId());
			
			if (task == null){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid request specified");
				return o;	
			}
			
			/*boolean canEdit = false;
			if (isSuperAdmin || task.getOwnerId().equals(es.gIR_USERN) || task.getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER) || userRole.equals(Defines.WORKSPACE_MANAGER)){
				canEdit = true;
			}
			if (!canEdit){
				o.setErrorFlag(true);
				o.setErrorMsg("You are not authorized to perform this action");
				return o;
			}*/
			
			if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,task.isIbmOnly(),userRole,notEditors,task.getCreatorId(),task.getOwnerId(),user_external)){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");			
				return o;
			}

			o.setTask(task);
			
		}
		catch(Exception e){
			o.setErrorFlag(true);
			o.setErrorMsg("You are not authorized to perform this action");
			return o;
		}
		return o;
	}


	public ETSDealTrackerResultObj doDelTask2(ETSDealTrackerResultObj o,String userRole, String staskid,Vector notEditors, boolean user_external){
		//check if user allowed to delete
		try{
			ETSTask task = ETSDealTrackerDAO.getTask(staskid,Project.getProjectId(),o.getSelfId());
			
			if (task == null){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid request specified");
				return o;	
			}
			
			if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,task.isIbmOnly(),userRole,notEditors,task.getCreatorId(),task.getOwnerId(),user_external)){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");			
				return o;
			}

							
			boolean success = ETSDealTrackerDAO.delTask(task.getId(),Project.getProjectId(),o.getSelfId());
			if (!success){
				o.setErrorFlag(true);
				o.setErrorMsg("Error occurred while performing this action");
				return o;
			}
			o.setTask(task);
		}
		catch(Exception e){
			e.printStackTrace();
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while performing this action");
			return o;
		}

		return o;
	}





	public ETSDealTrackerResultObj doEditDash(ETSDealTrackerResultObj o,String userRole,Vector notEditors,boolean user_external){
		Vector editTasks = new Vector();
		Vector users = new Vector();
		Vector iusers = new Vector();
		o.setErrorFlag(false);
		try{
			Vector tasks = ETSDealTrackerDAO.getTasks(Project.getProjectId(),o.getSelfId(),false);
			users = ETSDatabaseManager.getProjMembersWithOutPriv(Project.getProjectId(),Defines.VISITOR,true, conn);
			iusers = getIBMMembers(users,conn);
			
			for (int t = 0; t < tasks.size(); t++){
				ETSTask task = (ETSTask)tasks.elementAt(t);
				if (ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,task.isIbmOnly(),userRole,notEditors,task.getCreatorId(),task.getOwnerId(),user_external)){
					editTasks.addElement(task);
				}
				/*
				if (isSuperAdmin || task.getOwnerId().equals(es.gIR_USERN) || task.getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER)|| userRole.equals(Defines.WORKSPACE_MANAGER)){
					editTasks.addElement(task);
				}
				*/
			}

		}
		catch (Exception e){
			e.printStackTrace();	
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while performing this action");
		}
		
		o.setResultTasks(editTasks);
		o.setUsers(users);
		o.setIbmUsers(iusers);
		return o;
	}


	public ETSDealTrackerResultObj doEditDash2(ETSDealTrackerResultObj o,String userRole,Vector notEditors,boolean user_external){
		Vector v = new Vector();
		String error = new String("");
		boolean errorFlag = false;

		String sids = ETSDealTrackerCommonFuncs.getParameter(request,"taskids");
		StringTokenizer st = new StringTokenizer(sids, ",");
		Vector ids = new Vector();
		
		while (st.hasMoreTokens()){
			String id = st.nextToken();
			ids.addElement(id);
		}
		
		try{
			
			for (int i = 0; i < ids.size(); i++){
				String id = (String)ids.elementAt(i);
				
				Hashtable h = new Hashtable();
				String section = ETSDealTrackerCommonFuncs.getParameter(request,"section_"+id);
				String title = ETSDealTrackerCommonFuncs.getParameter(request,"title_"+id);
				String month = ETSDealTrackerCommonFuncs.getParameter(request,"month_"+id);
				String day = ETSDealTrackerCommonFuncs.getParameter(request,"day_"+id);
				String year = ETSDealTrackerCommonFuncs.getParameter(request,"year_"+id);
				String status = ETSDealTrackerCommonFuncs.getParameter(request,"status_"+id);
				String owner= ETSDealTrackerCommonFuncs.getParameter(request,"owner_"+id);
				//String company = getParameter(request,"company_"+id);
				
				h.put("section",section);
				h.put("title",title);
				h.put("month",month);
				h.put("day",day);
				h.put("year",year);
				h.put("status",status);
				h.put("owner",owner);
				//h.put("company",company);
				
				String sibmonly = ETSDealTrackerCommonFuncs.getParameter(request,"ibmonly_"+id);
				String[] s = verifyAddTaskFields(h,(sibmonly=="1"),id);
							
				if (s[0].equals("0")){
					o.setErrorFlag(true);
					o.setErrorMsg(s[1]);
					//doEditDash(s[1],true);
					return o;
				}
				else{
					ETSTask t = ETSDealTrackerDAO.getTask(id,Project.getProjectId(),o.getSelfId());
					boolean chFlag = false;
					//if (isSuperAdmin || t.getOwnerId().equals(es.gIR_USERN) || t.getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER)|| userRole.equals(Defines.WORKSPACE_MANAGER)){
					if (ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,t.isIbmOnly(),userRole,notEditors,t.getCreatorId(),t.getOwnerId(),user_external)){
						if (!(t.getSection().equals(section))){
							t.setSection(section);
							chFlag=true;
						}
						if (!(t.getTitle().equals(title))){
							t.setTitle(title);
							chFlag=true;						
						}
						
						String[] ddate = t.getDueDateStrs();
						
						if (!(ddate[0].equals(month)) || !(ddate[1].equals(day)) || !(ddate[2].equals(year))){
							t.setDueDate(month,day,year);
							chFlag=true;						
						}
						if (!(t.getStatus().equals(status))){
							t.setStatus(status);
							chFlag=true;						
						}
						if (!(t.getOwnerId().equals(owner))){
							t.setOwnerId(owner);
							chFlag=true;						
						}
						//t.setCompany(company);
						if (chFlag){
							v.addElement(t);
						}
					}	
				}
			}
			
		
			boolean success = false;
			
			for (int j=0; j<v.size(); j++){
				ETSTask t = (ETSTask)v.elementAt(j);
				try{
					success = ETSDealTrackerDAO.editTask(t);
					editTaskEmail(t);
				}
				catch(Exception e){
					o.setErrorFlag(true);
					o.setErrorMsg("Error occurred while processing this action");
					return o;
				}
			}
			
			if (!success && (v.size()>0)){
				o.setErrorFlag(true);
				o.setErrorMsg("Error occurred while processing this action");
				return o;
			}
			else{
					
				
			}
				


		}
		catch(Exception e){
			e.printStackTrace();
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while processing this action");
			return o;
		}
		
		return o;
	}



//DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS
	public ETSDealTrackerResultObj doAddTaskDoc(ETSDealTrackerResultObj o,String staskid,boolean user_external,String userRole,Vector notEditors){
		ETSTask task;
		try{
			task = ETSDealTrackerDAO.getTask(staskid,Project.getProjectId(),o.getSelfId());
		}
		catch(Exception e){
			e.printStackTrace();
			task = null;	
		}
		
		if (task == null){
			o.setErrorFlag(true);
			o.setErrorMsg("Invalid request");	
			return o;
		}

		o.setTask(task);

		if(!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,task.isIbmOnly(),userRole,notEditors,task.getCreatorId(),task.getOwnerId(),user_external)){
			o.setAccessErrorFlag(true);
			o.setAccessErrorMsg("You are not authorized to perform this action");			
			return o;
		}
		return o;
	}



	public ETSDealTrackerResultObj doDelTaskDoc(ETSDealTrackerResultObj o,String userRole, String docid,String taskid,Vector notEditors,boolean user_external){
		try{
			
			if (docid.equals("") || taskid.equals("")){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid url");
				return o;
			}
			
			
			ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject((new Integer(docid).intValue()),Project.getProjectId());
			ETSTask task = ETSDealTrackerDAO.getTask(String.valueOf(taskid),Project.getProjectId(),o.getSelfId());
			if (doc == null){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid document specified");
				return o;	
			}

			o.setDoc(doc);
			o.setTask(task);

			//if (!(isSuperAdmin || task.getOwnerId().equals(es.gIR_USERN) || task.getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER)|| userRole.equals(Defines.WORKSPACE_MANAGER))){
			if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,task.isIbmOnly(),userRole,notEditors,task.getCreatorId(),task.getOwnerId(),user_external)){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");			
				return o;
			}			
		}
		catch(Exception e){
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while processing this action.");			
			return o;
		}
		
		return o;

	}
	
	public ETSDealTrackerResultObj doDelTaskDoc2(ETSDealTrackerResultObj o,String userRole, String docid,String taskid,Vector notEditors,boolean user_external){
		
		try{
			if (docid.equals("") || taskid.equals("")){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid url");				
				return o;
			}
			
			ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject((new Integer(docid).intValue()),Project.getProjectId());
			ETSTask task = ETSDealTrackerDAO.getTask(taskid,Project.getProjectId(),o.getSelfId());
			if (doc == null){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid document specified");
				return o;	
			}
			if (task == null){
				o.setErrorFlag(true);
				o.setErrorMsg("Invalid task specified");
				return o;	
			}

			o.setDoc(doc);
			o.setTask(task);

			//if (!(isSuperAdmin || task.getOwnerId().equals(es.gIR_USERN) || task.getCreatorId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER)|| userRole.equals(Defines.WORKSPACE_MANAGER))){
			if (!ETSDealTrackerCommonFuncs.canEdit(es,isSuperAdmin,task.isIbmOnly(),userRole,notEditors,task.getCreatorId(),task.getOwnerId(),user_external)){
				o.setAccessErrorFlag(true);
				o.setAccessErrorMsg("You are not authorized to perform this action");			
				return o;	
			}		
			
			boolean success = ETSDatabaseManager.delDoc(doc,true,es.gIR_USERN);
			
			if (success)
				return o;
			else{
				o.setErrorFlag(true);
				o.setErrorMsg("Error occurred while processing this action.");			
				return o;	
			}
		}
		catch(Exception e){
			o.setErrorFlag(true);
			o.setErrorMsg("Error occurred while processing this action.");			
			return o;	
		}

	}


//end DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS DOCS

	private StringBuffer mainTaskDetails(String taskid,ETSTask t, boolean cv, boolean edit){
		StringBuffer message = new StringBuffer();
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			java.util.Date duedate = new java.util.Date(t.getDueDate());
			java.util.Date cdate = new java.util.Date(t.getCreatedDate());
			java.util.Date now =  new java.util.Date();
		
			String duedateStr=df.format(duedate);
			String cdateStr=df.format(cdate);
			String nowStr=df.format(now);
		
			try{
				if(cv){
					if (t.getTrackerType().equals("M"))
						message.append(" Set/Met:         " + ETSUtils.formatEmailStr(ETSDealTrackerDAO.getSetMetTitle(t.getProjectId(),t.getSelfId())) + "\n");
					else if (t.getTrackerType().equals("A"))
						message.append(" Self Assessment: " + ETSUtils.formatEmailStr(ETSDealTrackerDAO.getSelfAssessTitle(t.getProjectId(),t.getSelfId())) + "\n");
				}
		
		
				message.append(" Task Id:         " + ETSUtils.formatEmailStr(taskid) + "\n");
				message.append(" Title:           " + ETSUtils.formatEmailStr(t.getTitle()) + "\n");
				if(cv)
					message.append(" Attribute:       " + ETSUtils.formatEmailStr(t.getSection()) + " \n");
				else
					message.append(" Section  :       " + ETSUtils.formatEmailStr(t.getSection()) + " \n");
			
				message.append(" Due Date:        " + duedateStr + " (mm/dd/yyyy)\n\n");

				message.append(" Created By:      " + ETSUtils.getUsersName(conn, t.getCreatorId()) + "\n");
				message.append(" Created Date:    " + cdateStr + " (mm/dd/yyyy)\n\n");

				message.append(" Owner:           " + ETSUtils.getUsersName(conn, t.getOwnerId()) + "\n");
			
				if (!cv)
					message.append(" Work Required:   " + ETSUtils.formatEmailStr(t.getWorkRequired()) + " \n");
				
				message.append(" Action Required: " + ETSUtils.formatEmailStr(t.getActionRequired()) + "\n\n");
			
				if(edit){
					message.append(" Modified By:     " + ETSUtils.getUsersName(conn, es.gIR_USERN) + "\n");
					message.append(" Modified Date:   " + nowStr + " (mm/dd/yyyy)\n\n");
				}
				if (t.isIbmOnly())
					message.append("  This task is marked IBM Only\n\n");
			
			}
			catch(Exception e){

			}
			return message;
		}
			


	public void newTaskEmail(String taskid,ETSTask t){
		boolean selfFlag = false;
		StringBuffer message = new StringBuffer();
		
		try{
			if(!t.getSelfId().equals("")){
				message = newTaskEmail(taskid,new ETSSelfTask(t),true,false);
			}
			else{
				message.append("\n\n");
				message.append("A new task was added to the project: \n");
				message.append(Project.getName()+" \n\n");
				message.append("The details of the task are as follows: \n\n");
				message.append("==============================================================\n");

				message.append(mainTaskDetails(taskid,t,false,false));
				
							
				message.append("To view this task, click on the following  URL:  \n");
				String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid;
				message.append(url+"\n\n");
			}
			
			message.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));
		
		
			String emailids = "";	
			try{
				String cUserEmail = ETSUtils.getUserEmail(conn,t.getCreatorId());
				String oUserEmail = ETSUtils.getUserEmail(conn,t.getOwnerId());
				emailids = cUserEmail;
				if (!t.getCreatorId().equals(t.getOwnerId())){
				  emailids = emailids +","+oUserEmail;
				}
			}
			catch(AMTException ae){
				//writer.println("amt exception caught. e= "+ae);
			}
		
			String subject = "E&TS Connect - New Task: "+t.getTitle();
			subject = ETSUtils.formatEmailSubject(subject);
		
			String toList = "";
			toList = emailids;
			//toList = "sandieps@us.ibm.com";
			boolean bSent = false;
		
			if (!toList.trim().equals("")) {
				bSent = ETSUtils.sendEMail(es.gEMAIL,toList,"","", Global.mailHost,message.toString(),subject,es.gEMAIL);
			}
		
			if (bSent){
				ETSDatabaseManager.addEmailLog("Task",String.valueOf(taskid),"Add task",es.gIR_USERN,Project.getProjectId(),subject,toList,"");
				//writer.println("All team members have been notified of new document.");
			}
			else{
				System.out.println("Error occurred while notifying owner,submitter of new task.");
			}
		}
		catch(Exception e){
			e.printStackTrace(System.out);
		}

	
	}	

	private StringBuffer newTaskEmail(String taskid,ETSSelfTask t, boolean cv,boolean edit){
		StringBuffer message = new StringBuffer();
			
		String cvStr = "&self=";
		String cvNameStr = "self assessement";
		if (t.getTrackerType().equals("M")){
			cvStr = "&set=";	
			cvNameStr = "Set/Met";
		}
		else if (t.getTrackerType().equals("S")) {
			cvNameStr = "Survey";
		}
		message.append("\n\n");
		message.append("A new task was added to the "+cvNameStr+" action plan for: \n");
		message.append(Project.getCompany()+" \n\n");
		message.append("The details of the task are as follows: \n\n");
		message.append("==============================================================\n");
		
		message.append(mainTaskDetails(taskid,t,cv,edit));
	
		
		message.append("To view this task, click on the following  URL:  \n");
		String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+cvStr+t.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid;
		message.append(url+"\n\n");

		return message;
	}
	
			
			

	public void editTaskEmail(ETSTask t){
		StringBuffer message = new StringBuffer();
		
		try{
			if(!t.getSelfId().equals("")){
				message = editTaskEmail(new ETSSelfTask(t),true,true);
			}
			else{
				String taskid = String.valueOf(t.getId());
			
				message.append("\n\n");
				message.append("A task has been modified to the project: \n");
				message.append(Project.getName()+" \n\n");
				message.append("The details of this task are now as follows: \n\n");
				message.append("==============================================================\n");
			
				message.append(mainTaskDetails(taskid,t,false,true));
				
				
				message.append("To view this task, click on the following  URL:  \n");
				String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid;
				message.append(url+"\n\n");
			}
					
			message.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));
		
		
			String emailids = "";	
			try{
				String cUserEmail = ETSUtils.getUserEmail(conn,t.getCreatorId());
				String oUserEmail = ETSUtils.getUserEmail(conn,t.getOwnerId());
				String mUserEmail = ETSUtils.getUserEmail(conn,es.gIR_USERN);
				emailids = cUserEmail;
				if (!t.getCreatorId().equals(t.getOwnerId())){
					emailids = emailids+","+oUserEmail;
				}
				if (!t.getCreatorId().equals(es.gIR_USERN) && !t.getOwnerId().equals(es.gIR_USERN)){
					emailids = emailids+","+mUserEmail;
				}
							
			}
			catch(AMTException ae){
				//writer.println("amt exception caught. e= "+ae);
			}
		
			String subject = "E&TS Connect - Updated Task: "+t.getTitle();
			subject = ETSUtils.formatEmailSubject(subject);
		
			String toList = "";
			toList = emailids;
			//toList = "sandieps@us.ibm.com";
			boolean bSent = false;
		
			if (!toList.trim().equals("")) {
				bSent = ETSUtils.sendEMail(es.gEMAIL,toList,"","", Global.mailHost,message.toString(),subject,es.gEMAIL);
			}
		
			if (bSent){
				ETSDatabaseManager.addEmailLog("Task",String.valueOf(t.getId()),"Edit task",es.gIR_USERN,Project.getProjectId(),subject,toList,"");
			}
			else{
				System.out.println("Error occurred while notifying owner,submitter of edit task.");
			}
		}
		catch(Exception e){
			
		}

	
	}	

	private StringBuffer editTaskEmail(ETSTask t,boolean cv,boolean edit){
		StringBuffer message = new StringBuffer();
		
		String cvStr = "&self=";
		String cvNameStr = "self assessement";
		if (t.getTrackerType().equals("M")){
			cvStr = "&set=";	
			cvNameStr = "Set/Met";
		}
		else if (t.getTrackerType().equals("S")){
			cvNameStr = "Survey";
		}
		
		try{
				String taskid = String.valueOf(t.getId());
			
				message.append("\n\n");
				message.append("A task has been modified to the "+cvNameStr+" action plan for: \n");
				message.append(Project.getCompany()+" \n\n");
				message.append("The details of this task are now as follows: \n\n");
				message.append("==============================================================\n");
			
				message.append(mainTaskDetails(taskid,t,cv,edit));
				
			
				message.append("To view this task, click on the following  URL:  \n");
				String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+cvStr+t.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid;
				message.append(url+"\n\n");
		}
		catch(Exception e){
			
		}

		return message;
	}	




	private void addTaskCommentEmail(ETSTask t,ETSTaskComment tcomm){
		
		StringBuffer message = new StringBuffer();
		String taskid = String.valueOf(t.getId());
		
		try{
			if(!t.getSelfId().equals("")){
				message = addTaskCommentEmail(t,tcomm,true);
			}
			else{
				message.append("\n\n");
				message.append("A comment has been added to a task in the project: \n");
				message.append(Project.getName()+" \n\n");
				message.append("The details of task are now as follows: \n\n");
				message.append("==============================================================\n");
			
				message.append(mainTaskDetails(taskid,t,false,false));
				
				message.append(" Comment By:      " + ETSUtils.getUsersName(conn, es.gIR_USERN) + "\n");
				message.append(" Comment:         " + ETSUtils.formatEmailStr(tcomm.getComment()) + "\n\n");
	
				message.append("To view this task, click on the following  URL:  \n");
				String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid;
				message.append(url+"\n\n");
			}
			message.append(CommonEmailHelper.getEmailFooter("E&TS Connect"));
		
		
			String emailids = "";	
			try{
				String cUserEmail = ETSUtils.getUserEmail(conn,t.getCreatorId());
				String oUserEmail = ETSUtils.getUserEmail(conn,t.getOwnerId());
				String mUserEmail = ETSUtils.getUserEmail(conn,es.gIR_USERN);
				emailids = cUserEmail;
				if (!t.getCreatorId().equals(t.getOwnerId())){
					emailids = emailids+","+oUserEmail;
				}
				if (!t.getCreatorId().equals(es.gIR_USERN) && !t.getOwnerId().equals(es.gIR_USERN)){
					emailids = emailids+","+mUserEmail;
				}
							
			}
			catch(AMTException ae){
				//writer.println("amt exception caught. e= "+ae);
			}
		
			String subject = "E&TS Connect - Task comment added to: "+t.getTitle();
			subject = ETSUtils.formatEmailSubject(subject);
		
			String toList = "";
			toList = emailids;
			//toList = "sandieps@us.ibm.com";
			boolean bSent = false;
		
			if (!toList.trim().equals("")) {
				bSent = ETSUtils.sendEMail(es.gEMAIL,toList,"","", Global.mailHost,message.toString(),subject,es.gEMAIL);
			}
		
			if (bSent){
				ETSDatabaseManager.addEmailLog("Task",String.valueOf(taskid),"Add task comment",es.gIR_USERN,Project.getProjectId(),subject,toList,"");
			}
			else{
				System.out.println("Error occurred while notifying owner,submitter of new task comment.");
			}
		}
		catch(Exception e){
			
		}
	}	

	private StringBuffer addTaskCommentEmail(ETSTask t,ETSTaskComment tcomm,boolean cv){
		
		StringBuffer message = new StringBuffer();
		
	
		String cvStr = "&self=";
		String cvNameStr = "self assessement";
		if (t.getTrackerType().equals("M")){
			cvStr = "&set=";	
			cvNameStr = "Set/Met";
		}
		if (t.getTrackerType().equals("S")){
			cvNameStr = "Survey";
		}

		String taskid = String.valueOf(t.getId());
		message.append("\n\n");
		message.append("A comment has been added to a task in the "+cvNameStr+" action plan for: \n");
		message.append(Project.getCompany()+" \n\n");
		message.append("The details of task are now as follows: \n\n");
		message.append("==============================================================\n");
	
		message.append(mainTaskDetails(taskid,t,cv,false));
		try{
			message.append(" Comment:         " + ETSUtils.formatEmailStr(tcomm.getComment()) + "\n\n");
			message.append(" Comment By:      " + ETSUtils.getUsersName(conn, es.gIR_USERN) + "\n");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		message.append("To view this task, click on the following  URL:  \n");
		String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&taskid="+taskid+"&proj="+Project.getProjectId()+cvStr+t.getSelfId()+"&etsop=action&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid;
		message.append(url+"\n\n");
		
		return message;
		
	}
	
	

////////////////////////////////////////////////////////////////////////////////
//not done
////////////////////////////////////////////////////////////////////////////////




	private Vector getIBMMembers(Vector membs, Connection conn){
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


	
	
	
	public String getSelfName(String selfid,String projectid){
		String s = "Self Assessment";
		try{
			s = ETSDealTrackerDAO.getSelfAssessTitle(projectid,selfid);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return s;	
	}





} // end of class







