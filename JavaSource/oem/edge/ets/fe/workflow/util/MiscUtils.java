/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import org.apache.commons.logging.Log;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import oem.edge.common.Global;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;


/**
 * Class       : MiscUtils
 * Package     : oem.edge.ets.fe.workflow.util
 * Description : 
 * Date		   : Oct 18, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class MiscUtils {

	public static final String TC_MAIN = "WorkFlow main";
	public static final String TC_MEETINGS = "Meetings";
	public static final String TC_ASSESSMENT = "Assessment";
	public static final String TC_TEAM = "Team";
	public static void removeDuplicates(ArrayList list)
	{
		Set set = new HashSet();
		set.addAll(list);
		list.clear();
		list.addAll(set);
	}
	public static String reformatDate(String d)
	{
		//input : yyyy-mm-dd
		//output: mm/dd/yyyy
		String[] temp = d.split("-");
		if(temp.length==3)
			return temp[1]+"/"+temp[2]+"/"+temp[0];
		else
			return d;
	}
	public static ArrayList getIssueTypes() {
		ArrayList list = new ArrayList();
		SelectControl s= null;
		s= new SelectControl(); s.setValue("AR"); s.setLable("AR");list.add(s);
		s= new SelectControl(); s.setValue("Audit"); s.setLable("Audit");list.add(s);
		s= new SelectControl(); s.setValue("Client"); s.setLable("Client");list.add(s);
		s= new SelectControl(); s.setValue("Communication");s.setLable("Communication");list.add(s);
		s= new SelectControl(); s.setValue("Contract"); s.setLable("Contract");list.add(s);
		s= new SelectControl(); s.setValue("Delivery"); s.setLable("Delivery");list.add(s);
		s= new SelectControl(); s.setValue("Ebiz"); s.setLable("Ebiz");list.add(s);
		s= new SelectControl(); s.setValue("IP"); s.setLable("IP");list.add(s);
		s= new SelectControl(); s.setValue("Mfg"); s.setLable("Mfg");list.add(s);
		s= new SelectControl(); s.setValue("OM"); s.setLable("OM");list.add(s);
		s= new SelectControl(); s.setValue("Price"); s.setLable("Price");list.add(s);
		s= new SelectControl(); s.setValue("Project"); s.setLable("Project");list.add(s);
		s= new SelectControl(); s.setValue("Qual/Del"); s.setLable("Qual/Del");list.add(s);
		s= new SelectControl(); s.setValue("Quality"); s.setLable("Quality");list.add(s);
		s= new SelectControl(); s.setValue("Rohs"); s.setLable("Rohs");list.add(s);
		s= new SelectControl(); s.setValue("Sales"); s.setLable("Sales");list.add(s);
		s= new SelectControl(); s.setValue("Tech"); s.setLable("Tech");list.add(s);
		return list;
	}
	public static ArrayList getIssueCategories() {
		ArrayList list = new ArrayList();
		SelectControl s= null;
		s= new SelectControl(); s.setValue("CritSits"); s.setLable("CritSits");list.add(s);
		s= new SelectControl(); s.setValue("Statement"); s.setLable("Statement");list.add(s);
		s= new SelectControl(); s.setValue("Action"); s.setLable("Action");list.add(s);
		return list;
	}
	
	public static boolean isValidDate(String y, String m, String d)
	{
		Date da1 = new Date(Integer.parseInt(y)-1900,Integer.parseInt(m)-1,Integer.parseInt(d));
		System.out.println(Integer.parseInt(m));
		if(d.equalsIgnoreCase(da1.getDate()+"") && m.equalsIgnoreCase(Integer.parseInt(m)+"") &&  y.equalsIgnoreCase((da1.getYear()+1900)+""))
				return true;
		else return false;
	}
	public static boolean isValidProject(String projectID, DBAccess db)
	{
		
		boolean flag = false;
		try{
			db.prepareDirectQuery("select * from ets.ets_projects where project_id='"+projectID+"' with ur");
			int rows = db.execute();
			
			if(rows == 1)
			{
				flag = true;
			}
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}
		return flag;
	}
	public static boolean isValidWorkflow(String projectID, String workflowID, DBAccess db)
	{
		boolean flag = false;
		try{
			db.prepareDirectQuery("select * from ets.wf_def where project_id='"+projectID+"' and wf_id ='"+workflowID+"' with ur");
			int rows = db.execute();
			
			if(rows == 1)
			{
				flag = true;
			}
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}
		return flag;
	}
	public static boolean isValidIssue(String issueID, DBAccess db)
	{
		
		boolean flag = false;
		try{
			db.prepareDirectQuery("select * from ets.wf_issue where issue_id='"+issueID+"' with ur");
			int rows = db.execute();
			
			if(rows == 1)
			{
				flag = true;
			}
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}
		return flag;
	}
	public static void setDB(HttpServletRequest request)
	{
	 //HttpSession session = request.getSession();
		try {
			if (request.getAttribute("WFdb") == null
					|| ((DBAccess) request.getAttribute("WFdb")).getConnection().isClosed())
			{
				
				DBAccess db = new DBAccess();
				request.removeAttribute("WFdb");
				request.setAttribute("WFdb", db);

			}
		} catch (Exception e) {
			System.err.println(e);
			System.err
					.println("This error ocurred while setting WFdb in request.");
		}
	
	}
	
	public static String getWorkflowStageID(String projectID,String workflowID,String tname){
		String stageID= null;
		DBAccess db = null;
		String tableName = "";
		
		if(tname.equalsIgnoreCase(WorkflowConstants.PREPARE))
		   tableName = WorkflowConstants.SCHEMA+".WF_STAGE_PREPARE_SETMET";
		if(tname.equalsIgnoreCase(WorkflowConstants.DOCUMENT))
		   tableName= WorkflowConstants.SCHEMA+".WF_STAGE_DOCUMENT_SETMET";
		if(tname.equalsIgnoreCase(WorkflowConstants.VALIDATE))
		  tableName = WorkflowConstants.SCHEMA+".WF_STAGE_VALIDATE_SETMET";
		  
		try{
			db = new DBAccess();
			db.prepareDirectQuery("SELECT WF_STAGE_ID STG FROM "+tableName+"  WHERE PROJECT_ID='"+projectID+"' AND WF_ID='"+workflowID+"'");
			int rows= db.execute();
			if(rows>0)
				stageID = db.getString(0,"STG");
			db.doCommit();
			db.close();
			db = null;
		}catch(Exception ex){
			logger.debug("The exception in Misc Utils of stageID",ex);
		}finally{
			 try{
			 	if(db!=null){
			 		db.close();
			 		db =  null;
			 	}
			 }catch(Exception ex1){
			    logger.debug("The exception in Misc Utils finally of stageID",ex1);
			 }
		}
		
		return stageID;
	}
	
	public static String getTc(String projectID, String tabname)
	{
		String tc = "";
		DBAccess db = null;
		try{
			db = new DBAccess();
			tc = getTc(projectID,tabname,db);
			db.doCommit();
			db.close();
			db=null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(db!=null)
			{
				try{db.close();}catch(Exception ex){ex.printStackTrace();}
				db=null;
			}
		}
		return tc;
	}
	public static String getTc(String projectID, String tabname, DBAccess db)
	{
		try{
		db.prepareDirectQuery("select cat_id from ets.ets_cat  where project_id='"+projectID+"' and cat_name='"+tabname+"' with ur");
		int rows = db.execute();
		if(rows==0)
			return "";
		else
			return db.getString(0,0);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * This first gets the parameter under the name item. If found null, gets the attribute under the name item.
	 * 
	 */
	public static String getPA(HttpServletRequest request, String item)
	{
		if(request==null||item==null) return null;
		String value = request.getParameter(item);
		if(value==null)value=(String)request.getAttribute(item);
		return value;
	}
	/**
	 * This first gets the attribute under the name item. If found null, gets the parameter under the name item.
	 * 
	 */
	public static String getAP(HttpServletRequest request, String item)
	{
		if(request==null||item==null) return null;
		String value=(String)request.getAttribute(item); 
		if(value==null)value = request.getParameter(item);
		return value;
	}
	/**
	 * 
	 * 
	 */
	public static boolean isValidProject(String projectID)
	{
		String tc = "";
		DBAccess db = null;
		boolean validityFlag=false;
		try{
			db = new DBAccess();
			validityFlag = isValidProject(projectID,db);
			db.doCommit();
			db.close();
			db=null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(db!=null)
			{
				try{db.close();}catch(Exception ex){ex.printStackTrace();}
				db=null;
			}
		}
		return validityFlag;
	}
	public static boolean isValidWorkflow(String projectID, String workflowID)
	{
		String tc = "";
		DBAccess db = null;
		boolean validityFlag=false;
		try{
			db = new DBAccess();
			validityFlag = isValidWorkflow(projectID,workflowID,db);
			db.doCommit();
			db.close();
			db=null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(db!=null)
			{
				try{db.close();}catch(Exception ex){ex.printStackTrace();}
				db=null;
			}
		}
		return validityFlag;
	}
	private static Log logger = WorkflowLogger.getLogger(MiscUtils.class);
	public static ArrayList getissueCategories() {
		ArrayList list = new ArrayList();
		SelectControl s= null;
		s= new SelectControl(); s.setValue("Category1"); s.setLable("Category1");list.add(s);
		s= new SelectControl(); s.setValue("Category2"); s.setLable("Category2");list.add(s);
		return list;
	}
	
	public static boolean canCreateQBR(String projectID, DBAccess db) throws Exception 
	{
		String q = "select count(wf_id)  from ets.wf_def where wf_curr_stage_name not in ('Complete','COMPLETE','Cancelled','Closed') and project_id='"+projectID+"' and wf_type='QBR' with ur";
		db.prepareDirectQuery(q);
		
		if(db.execute()==1)
			if(db.getInt(0,0)==0)
				return true;
		return false;
	}
	public static boolean canCreateQBR(String projectID)
	{
		boolean result = false;
		DBAccess db = null;
		try{
			db = new DBAccess();
			result = canCreateQBR(projectID,db);
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return result;
	}
	public static boolean canCreateSA(String projectID, DBAccess db) throws Exception 
	{
		String q = "select count(wf_id)  from ets.wf_def where wf_curr_stage_name not in ('Complete','COMPLETE','Cancelled','Closed') and project_id='"+projectID+"' and wf_type='SELF ASSESSMENT' with ur";
		db.prepareDirectQuery(q);
		
		if(db.execute()==1)
			if(db.getInt(0,0)==0)
				return true;
		return false;
	}
	public static boolean canCreateSA(String projectID)
	{
		boolean result = false;
		DBAccess db = null;
		try{
			db = new DBAccess();
			result = canCreateSA(projectID,db);
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return result;
	}
	public static String imageURI(String imgFileName)
	{
			return Global.getUrl("").substring(0,Global.getUrl("").length())+"images/"+imgFileName;
	}
}

