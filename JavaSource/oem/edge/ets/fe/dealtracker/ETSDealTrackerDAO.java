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


package oem.edge.ets.fe.dealtracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;



public class ETSDealTrackerDAO extends ETSDatabaseManager{

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.29";



    // IMPORTANT - DO NOT CHANGE THESE VARIABLES - EVER !!!
    private static final int MAX_DOC_VERSIONS = 1000;
    private static final int STARTING_DOC_ID  = 10000 * MAX_DOC_VERSIONS;
    private static final int MAXIMUM_DOC_ID   = 99999 * MAX_DOC_VERSIONS;

    static final char FALSE_FLAG    = '0';
    static final char TRUE_FLAG     = '1';
    static final char NOT_SET_FLAG  = 'x';

    private static final int IS_LATEST_VERSION_FLAG = 0;
    private static final int HAS_PREVIOUS_VERSION_FLAG = 1;


    static {
	if ( ! Global.loaded )
	    Global.Init();
    }
 
	
	public static ETSTask getTask(String taskid, String projectid, String selfid) throws SQLException {
	   Connection connection = null;
	   ETSTask task = null; //new ETSTask();
	   try{
		   connection = ETSDBUtils.getConnection();
		   task = getTask(taskid, projectid,selfid, connection);
	   }
	   catch (SQLException e) {
			e.printStackTrace();
		   throw e;
	   }
	   catch (Exception ex) {
		   //throw ex;
	   }
	   finally{
		   ETSDBUtils.close(connection);
	   }
		   return task;
	   }
   static ETSTask getTask(String taskid, String projectid,String selfid,Connection conn) throws SQLException {
	   PreparedStatement getTaskSt = conn.prepareStatement("select * from ETS.ETS_TASK_MAIN where task_id=? and project_id=? and self_id=? order by task_id with ur");
	
	System.out.println("si="+selfid);
	   getTaskSt.setInt(1, Integer.parseInt(taskid));
	   getTaskSt.setString(2, projectid);
	   getTaskSt.setString(3, selfid);
	   ResultSet rs = getTaskSt.executeQuery();
	
	   ETSTask task = null;
	   if (rs.next()){
	   	System.out.println("here");
		   task = getTask(rs,false);
	   }
	   
	   rs.close();
	   getTaskSt.close();
	   return task;
   }


   public static Vector getTasks(String projectid,String selfid,boolean user_external) throws SQLException {
		Connection connection = null;
		Vector tasks = new Vector();
		try{
			connection = ETSDBUtils.getConnection();
			tasks= getTasks(projectid, selfid,user_external,connection);
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return tasks;
		}
	static Vector getTasks(String projectid, String selfid,boolean user_external,Connection conn) throws SQLException {
		
		PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) " +
			"from ets.ets_doc d " +
			"where d.project_id=? and d.self_id=t.self_id and cast(d.meeting_id as smallint)= t.task_id " +
			"and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  " +
			"from ETS.ETS_TASK_MAIN t where t.project_id=? and t.self_id=?" +
			"order by t.task_id with ur");
	
		getTasksSt.setString(1, projectid);
		getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
		getTasksSt.setString(3, projectid);
		getTasksSt.setString(4, selfid);
		ResultSet rs = getTasksSt.executeQuery();
	
		Vector tasks = new Vector();
		tasks = getTasks(rs,true);
	
		rs.close();
		getTasksSt.close();
		return tasks;
	}
	

	public static Vector getTasks(String projectid,String selfid,boolean user_external,String sortby, String ad) throws SQLException {
		Connection connection = null;
		Vector tasks = new Vector();
		try{
			connection = ETSDBUtils.getConnection();
			tasks= getTasks(projectid, selfid, user_external,sortby, ad, connection);
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return tasks;
		}
	static Vector getTasks(String projectid, String selfid,boolean user_external,String sortby, String ad,Connection conn) throws SQLException {
		String sb = "t.task_id";
		
		if (sortby.equals(Defines.SORT_BY_DT_DATE_STR)){
			sb = "t.due_date";
		}
		else if (sortby.equals(Defines.SORT_BY_DT_SECT_STR)){
			sb = "t.section";
		}
		else if (sortby.equals(Defines.SORT_BY_DT_STATUS_STR)){
			sb = "t.status "+ad+",t.task_id";
		}
		else if (sortby.equals(Defines.SORT_BY_DT_TITLE_STR)){
			sb = "t.title";
		}
		else if (sortby.equals(Defines.SORT_BY_DT_OWNER_STR)){
			sb = "t.owner_id";
		}

		
		String extStr = "";
		if (user_external){
			extStr = " and t.ibm_only!='1' ";	
		}
		
		System.out.println("sb="+sb+"   ad="+ad+"   ext="+user_external);
		
		PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d "+
				"where d.project_id=? and d.self_id=t.self_id and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  "+
				"from ETS.ETS_TASK_MAIN t where t.project_id=? and t.self_id=? order by "+sb+" "+ad+" with ur");
	
		
		/*PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d "+
			"where d.project_id=? and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  "+
			"from ETS.ETS_TASK_MAIN t " +
			"where t.project_id=? " +
			//extStr+
			"order by "+sb+" "+ad+" with ur");*/
	
		getTasksSt.setString(1, projectid);
		getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
		getTasksSt.setString(3, projectid);
		getTasksSt.setString(4,selfid);
		ResultSet rs = getTasksSt.executeQuery();
	
		Vector tasks = new Vector();
		tasks = getTasks(rs,true);
	
		rs.close();
		getTasksSt.close();
		return tasks;
	}

	
	public static Vector getTasks(String taskids, String projectid,String selfid) throws SQLException {
		Connection connection = null;
		Vector tasks = new Vector();
		try{
			connection = ETSDBUtils.getConnection();
			tasks= getTasks(taskids,projectid, selfid,connection);
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return tasks;
		}
	//fixed in 5.2.1
	static Vector getTasks(String taskids,String projectid, String selfid,Connection conn) throws SQLException {
		PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d " +
			"where d.project_id=? and d.self_id=t.self_id and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  " +
			"from ETS.ETS_TASK_MAIN t where t.project_id=? and t.task_id in ("+taskids+") and t.self_id=? order by t.task_id with ur");
	
		getTasksSt.setString(1, projectid);
		getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
		getTasksSt.setString(3, projectid);
		getTasksSt.setString(4, selfid);
		ResultSet rs = getTasksSt.executeQuery();
	
		Vector tasks = new Vector();
		tasks = getTasks(rs,true);
	
		rs.close();
		getTasksSt.close();
		return tasks;
	}





	public static Vector getTaskDocs(int taskid,String projectid,String selfid) throws SQLException {
		Connection connection = null;
		Vector taskdocs = new Vector();
		try{
			connection = ETSDBUtils.getConnection();
			taskdocs= getTaskDocs(taskid, projectid,selfid,connection);
		}
		catch (SQLException e) {
			System.out.println("sql e = "+e);
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			System.out.println("ex e = "+ex);
			
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return taskdocs;
		}
	static Vector getTaskDocs(int taskid, String projectid, String selfid,Connection conn) throws SQLException {
		//PreparedStatement getTasksSt = conn.prepareStatement("select * from ETS.ETS_DOC where project_id=? and meeting_id=? order by doc_id with ur");
		PreparedStatement getTasksSt = conn.prepareStatement("select d.*,f.docfile_name,f.docfile_update_date," +
			"(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " +
			"from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id=? and meeting_id=? and cat_id=? " +
			"and doc_type=? and d.doc_id = f.doc_id and d.self_id=? and delete_flag !='"+TRUE_FLAG+"' order by d.doc_name with ur");

		getTasksSt.setString(1, projectid);
		getTasksSt.setString(2, String.valueOf(taskid));
		getTasksSt.setInt(3,-3);
		getTasksSt.setInt(4,Defines.TASK);
		getTasksSt.setString(5,selfid);

		ResultSet rs = getTasksSt.executeQuery();
	
		Vector tasks = new Vector();
		tasks = ETSDatabaseManager.getDocs(rs);
	
		rs.close();
		getTasksSt.close();
		return tasks;
	}


	public static synchronized String addTask(ETSTask t) throws SQLException {
		Connection connection = null;
		String success = "0";
		try{
			connection = ETSDBUtils.getConnection();
			success= addTask(t, connection);
		}
		catch (SQLException e) {
			throw e;
		}
		catch (Exception ex) {
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return success;
		}
	 
	static synchronized String addTask(ETSTask t, Connection conn) throws SQLException {
		String success = "0";
		int taskid = getTaskId(t.getProjectId(),t.getSelfId(),conn);
		
		System.out.println("AA="+taskid);
		
		//boolean dup = isDuplicateTask(t,conn);
		
		PreparedStatement addTaskSt = conn.prepareStatement("insert into ets.ets_task_main(task_id,project_id,self_id,creator_id,created_date,section,title,description,status,due_date,owner_id,work_required,action_required,ibm_only,parent_task_id,company,last_userid,last_timestamp,tracker_type) values(?,?,?,?,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?)");
		
		try{
			addTaskSt.setInt(1, taskid);
			addTaskSt.setString(2,t.getProjectId());
			addTaskSt.setString(3,t.getSelfId());
			addTaskSt.setString(4,t.getCreatorId());
			//addTaskSt.setTimestamp(4,t.getCreatedDate());
			addTaskSt.setString(5,t.getSection());
			addTaskSt.setString(6,t.getTitle());
			addTaskSt.setString(7,t.getDescription());
			addTaskSt.setString(8,t.getStatus());
			addTaskSt.setTimestamp(9,t.getTSDueDate());
			addTaskSt.setString(10,t.getOwnerId());
			addTaskSt.setString(11,t.getWorkRequired());
			addTaskSt.setString(12,t.getActionRequired());
			addTaskSt.setString(13,String.valueOf(t.getIbmOnly()));
			addTaskSt.setInt(14,t.getParentTaskId());
			addTaskSt.setString(15,t.getCompany());
			addTaskSt.setString(16,t.getLastUserid());
			addTaskSt.setString(17,t.getTrackerType());
			
			addTaskSt.executeUpdate();
			addTaskSt.close();

			
			String taskidStr = String.valueOf(taskid);
			//addContentLog(catid,'C',cat.getProjectId(),Defines.ADD_CAT,cat.getUserId());
			success = taskidStr;
		}
		catch (SQLException se) {
			System.out.println("sql error ="+se);
			se.printStackTrace(System.out);
			return "0";
		}

		System.out.println("BB="+success);
		
		return success;
	}
	
	
	private static synchronized int getTaskId(String proj_id,String selfid,Connection conn) throws SQLException {
		int task_id=1;
		Statement statement = conn.createStatement();
	
		ResultSet rs = statement.executeQuery("select max(task_id) as task_id from ets.ets_task_main where project_id='"+proj_id+"' and self_id='"+selfid+"' with ur");
	
		if(rs.next()) {
			task_id = rs.getInt("task_id") + 1;
		}
	
		System.out.println("task_id="+task_id);

		rs.close();
		statement.close();
		return task_id;
	}


	public static synchronized boolean editTask(ETSTask t) throws SQLException {
		Connection connection = null;
		boolean success = false;
		try{
			connection = ETSDBUtils.getConnection();
			success= editTask(t, connection);
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return success;
		}
	 
	static synchronized boolean editTask(ETSTask t, Connection conn) throws SQLException {
		boolean success = false;
		
		PreparedStatement taskSt = conn.prepareStatement(
		        "update ets.ets_task_main set section=?,title=?,description=?," +
		        "status=?,due_date=?,owner_id=?,work_required=?,action_required=?," +
		        "ibm_only=?,company=?,last_userid=?,last_timestamp=current timestamp, " +
		        "is_late=? " +
		        "where task_id=? and project_id=? and self_id=?");
	

		try{
			taskSt.setString(1,t.getSection());
			taskSt.setString(2,t.getTitle());
			taskSt.setString(3,t.getDescription());
			taskSt.setString(4,t.getStatus());
			taskSt.setTimestamp(5,t.getTSDueDate());
			taskSt.setString(6,t.getOwnerId());
			taskSt.setString(7,t.getWorkRequired());
			taskSt.setString(8,t.getActionRequired());
			taskSt.setString(9,String.valueOf(t.getIbmOnly()));
			taskSt.setString(10,t.getCompany());
			taskSt.setString(11,t.getLastUserid());

			Date dtDueDate = new Date(t.getDueDate());
	        Calendar c = Calendar.getInstance();
	        c.set(Calendar.HOUR_OF_DAY, 0);
	        c.set(Calendar.MINUTE, 0);
	        c.set(Calendar.SECOND, 0);
	        c.set(Calendar.MILLISECOND, 0);

	        Date dtCurrentDate = c.getTime();
			
			if (dtDueDate.after(dtCurrentDate)) {
			    taskSt.setNull(12, java.sql.Types.VARCHAR);
			}
			else {
			    taskSt.setString(12, "Y");
			}
			
			taskSt.setInt(13,t.getId());
			taskSt.setString(14,t.getProjectId());
			taskSt.setString(15,t.getSelfId());
			
			taskSt.executeUpdate();
			taskSt.close();

			//addContentLog(catid,'C',cat.getProjectId(),Defines.ADD_CAT,cat.getUserId());
			success = true;
		}
		catch (SQLException se) {
			System.out.println("sql error ="+se);
			return false;
		}

		return success;
	}

	
	static public Vector getTaskComments(int taskid,String projectid,String selfid) throws SQLException {
		Connection connection = null;
		Vector comments = new Vector();
		try{
			connection = ETSDBUtils.getConnection();
			comments= getTaskComments(taskid, projectid,selfid,connection);
		}
		catch (SQLException e) {
			System.out.println("sql e = "+e);
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			System.out.println("ex e = "+ex);
				
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return comments;
		}
	static Vector getTaskComments(int taskid, String projectid, String selfid,Connection conn) throws SQLException {
		Vector v = new Vector();
		PreparedStatement tasksSt = conn.prepareStatement("select * from ets.ets_task_comments where  task_id=? and project_id=? and self_id=?order by seq_no desc with ur");
	
		tasksSt.setInt(1, taskid);
		tasksSt.setString(2, projectid);
		tasksSt.setString(3, selfid);
		
		ResultSet rs = tasksSt.executeQuery();
		while (rs.next()){
			ETSTaskComment t = new ETSTaskComment();
		  	t.setLastTimestamp(rs.getTimestamp("last_timestamp"));
			t.setLastUserid(rs.getString("last_userid"));
			t.setComment(rs.getString("comments"));

			v.addElement(t);
		}		
		
		rs.close();
		tasksSt.close();
		return v;
	}

	public static synchronized int addTaskComment(ETSTaskComment t) throws SQLException {
		Connection connection = null;
		int success = 0;
		try{
			connection = ETSDBUtils.getConnection();
			success= addTaskComment(t, connection);
		}
		catch (SQLException e) {
			success = 0;
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			success = 0;
			ex.printStackTrace();
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return success;
		}
	 
	static synchronized int addTaskComment(ETSTaskComment t, Connection conn) throws SQLException {
		int success = 0;
		
		int seqno = getSeqNo(t.getProjectId(),t.getTaskId(),t.getSelfId(),conn);
		
		PreparedStatement addTaskSt = conn.prepareStatement("insert into ets.ets_task_comments(task_id,project_id,self_id,seq_no,comments,last_userid,last_timestamp) values(?,?,?,?,?,?,current timestamp)");
	

		try{
			addTaskSt.setInt(1, t.getTaskId());
			addTaskSt.setString(2,t.getProjectId());
			addTaskSt.setString(3,t.getSelfId());
			addTaskSt.setInt(4,seqno);
			addTaskSt.setString(5,t.getComment());
			addTaskSt.setString(6,t.getLastUserid());
			
			addTaskSt.executeUpdate();
			addTaskSt.close();

			success = seqno;
		}
		catch (SQLException se) {
			System.out.println("sql error ="+se);
			se.printStackTrace();
			return 0;
		}

		return success;
	}
	private static synchronized int getSeqNo(String proj_id,int taskid,String selfid,Connection conn) throws SQLException {
		int seqno=1;
		Statement statement = conn.createStatement();
	
		ResultSet rs = statement.executeQuery("select max(seq_no) as seqno from ets.ets_task_comments where project_id='"+proj_id+"' and task_id="+taskid+" and self_id='"+selfid+"' with ur");
	
		if(rs.next()) {
			seqno = rs.getInt("seqno") + 1;
		}
	
		System.out.println("seqno=="+seqno);
		
		rs.close();
		statement.close();
		return seqno;
	}
	
	
	public static synchronized boolean delTask(int taskid,String projectid,String selfid) throws SQLException {
		boolean success = false;
		Connection connection = null;
		
		try{
			connection = ETSDBUtils.getConnection();
			success= delTask(taskid,projectid, selfid,connection);
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return success;
		}
		 
	static synchronized boolean delTask(int taskid, String projectid, String selfid,Connection conn) throws SQLException {
		boolean success = false;
		
		try{
			success = delTaskDocs(taskid,projectid,selfid,conn);
			if (success){
				success = delTaskComms(taskid,projectid,selfid,conn);
				success = removeDelTaskDepTask(taskid,projectid,selfid,conn);
				if (success){	
					PreparedStatement taskSt = conn.prepareStatement("delete from ets.ets_task_main where project_id=? and task_id=? and self_id=?");
					taskSt.setString(1, projectid);
					taskSt.setInt(2, taskid);
					taskSt.setString(3, selfid);
						
					taskSt.executeUpdate();
					taskSt.close();
			
					//addContentLog(catid,'C',cat.getProjectId(),Defines.ADD_CAT,cat.getUserId());
					success = true;
				}
			}
		}
		catch (SQLException se) {
			System.out.println("sql error ="+se);
			return false;
		}
	
		return success;
	}
	
	
	public static synchronized boolean delTaskDocs(int taskid, String projectid, String selfid,Connection conn) throws SQLException {
		boolean success = false;
		PreparedStatement taskSt = conn.prepareStatement("delete from ets.ets_doc where project_id=? and meeting_id=? and cat_id=? and doc_type=? and self_id=?");


		try{
			taskSt.setString(1, projectid);
			taskSt.setString(2, String.valueOf(taskid));
			taskSt.setInt(3,-3);
			taskSt.setInt(4,Defines.TASK);
			taskSt.setString(5,selfid);
	
			taskSt.executeUpdate();
			taskSt.close();

			//addContentLog(catid,'C',cat.getProjectId(),Defines.DEL_DOC,cat.getUserId());
			success = true;
		}
		catch (SQLException se) {
			se.printStackTrace();
			System.out.println("sql error ="+se);
			return false;
		}
		return success;
	}


	static synchronized boolean delTaskComms(int taskid, String projectid, String selfid,Connection conn) throws SQLException {
		boolean success = false;
		PreparedStatement taskSt = conn.prepareStatement("delete from ets.ets_task_comments where project_id=? and task_id=? and self_id=?");

		try{
			taskSt.setString(1, projectid);
			taskSt.setInt(2, taskid);
			taskSt.setString(3, selfid);
			taskSt.executeUpdate();
			taskSt.close();

			//addContentLog(catid,'C',cat.getProjectId(),Defines.DEL_DOC,cat.getUserId());
			success = true;
		}
		catch (SQLException se) {
			se.printStackTrace();
			System.out.println("sql error ="+se);
			return false;
		}
		return success;
	}



	
	public static Vector getDepTasks(int taskid,String projectid,String selfid) throws SQLException {
		Connection connection = null;
		Vector tasks = new Vector();
		try{
			connection = ETSDBUtils.getConnection();
			tasks= getDepTasks(taskid,projectid, selfid,connection);
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return tasks;
		}
	static Vector getDepTasks(int taskid,String projectid, String selfid,Connection conn) throws SQLException {
		PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d where d.project_id=? " +
			"and d.self_id=t.self_id and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  " +
			"from ETS.ETS_TASK_MAIN t where t.project_id=? and t.parent_task_id=? and t.self_id=? order by t.task_id with ur");
	
		getTasksSt.setString(1, projectid);
		getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
		getTasksSt.setString(3, projectid);
		getTasksSt.setInt(4, taskid);
		getTasksSt.setString(5,selfid);
		ResultSet rs = getTasksSt.executeQuery();
	
		Vector tasks = new Vector();
		tasks = getTasks(rs,true);
	
		rs.close();
		getTasksSt.close();
		return tasks;
	}



	
	public static Vector getEligibleTasks(int taskid,boolean isIbmOnly,String projectid,String selfid,boolean userexternal) throws SQLException, Exception{
		Connection connection = null;
		Vector tasks = new Vector();
		try{
			connection = ETSDBUtils.getConnection();
			tasks= getEligibleTasks(taskid,isIbmOnly,projectid,selfid,userexternal,connection);
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return tasks;
		}
		
	static Vector getEligibleTasks(int taskid,boolean isIbmOnly,String projectid,String selfid,boolean userexternal,Connection conn) throws SQLException, Exception{
		Vector tasks = new Vector();
		Vector notInIds = getNotEligibleIds(taskid,projectid,selfid,conn);
		String sNotIds = "";
		
		for (int i=0; i< notInIds.size(); i++){
			if(i==0){
				sNotIds = (String)notInIds.elementAt(i);	
			}
			else{
				sNotIds = sNotIds + "," +(String)notInIds.elementAt(i);	
			}
			System.out.println(i+"::"+sNotIds);
		}
		
		String setNotIds = "";
		if (!sNotIds.equals("")){
			setNotIds = "and t.task_id not in ("+sNotIds+")";
		}
		
		String setIbmOnly="";
		if (isIbmOnly && userexternal){
			//error
			setIbmOnly = "t.ibm_only='Q'";
		}
		else if (isIbmOnly && !userexternal){
			setIbmOnly = " and t.ibm_only='"+TRUE_FLAG+"' ";
		}
		else if (!isIbmOnly && userexternal){
			setIbmOnly = " and t.ibm_only!='"+TRUE_FLAG+"' ";
		}
			
		PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d where d.project_id=? " +
			"and d.self_id=t.self_id and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  " +
			"from ETS.ETS_TASK_MAIN t where t.project_id=? and self_id=? and t.parent_task_id=?"+setNotIds+setIbmOnly+"order by t.task_id with ur");

		getTasksSt.setString(1, projectid);
		getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
		getTasksSt.setString(3, projectid);
		getTasksSt.setString(4,selfid);
		getTasksSt.setInt(5, 0);
		//getTasksSt.setString(5, sNotIds);
		
		ResultSet rs = getTasksSt.executeQuery();

		tasks = getTasks(rs,true);

		rs.close();
		getTasksSt.close();
		return tasks;
	}

	static Vector getNotEligibleIds(int taskid,String projectid,String selfid,Connection conn) throws SQLException, Exception{
		Vector ids = new Vector();
		ETSTask t = getTask(String.valueOf(taskid),projectid,selfid,conn);
		//ids.addElement(new Integer(taskid));
		ids.addElement(String.valueOf(taskid));
		if (t.getParentTaskId()!=0){
			System.out.println("here in not eligible ids parent");
			getNotEligibleIdsRec(t.getParentTaskId(),projectid,selfid,ids,conn);
		}
		return ids;
	}
	static Vector getNotEligibleIdsRec(int taskid,String projectid,String selfid,Vector ids,Connection conn) throws SQLException, Exception{
		
		ETSTask t = getTask(String.valueOf(taskid),projectid,selfid,conn);
		//ids.addElement(new Integer(taskid));
		if (t != null){
			if (!ids.contains(String.valueOf(t.getId()))){
				ids.addElement(String.valueOf(taskid));
				if (t.getParentTaskId()!=0){
					ids=getNotEligibleIdsRec(t.getParentTaskId(),projectid,selfid,ids,conn);
				}
			}
		}
		return ids;
	}




		
		
	//fixed in 5.2.1
	public static boolean addDepTasks(String taskid,String dtasks, String projectid,String selfid) throws SQLException{
		Connection connection = null;
		boolean success = false;
		try{
			connection = ETSDBUtils.getConnection();
			success= addDepTasks(taskid, dtasks, projectid, selfid,connection);
		}
		catch (SQLException e) {
			throw e;
		}
		catch (Exception ex) {
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return success;
		}
	 
	static boolean addDepTasks(String taskid, String dtasks, String projectid,String selfid,Connection conn) throws SQLException {
		boolean success = false;
		
		PreparedStatement taskSt = conn.prepareStatement("update ets.ets_task_main " +
			"set parent_task_id=? where task_id in("+dtasks+") and project_id=? and self_id=?");
	

		try{
			taskSt.setInt(1,new Integer(taskid).intValue());
			//taskSt.setString(2,dtasks);
			taskSt.setString(2,projectid);
			taskSt.setString(3,selfid);
			taskSt.executeUpdate();
			taskSt.close();
			
			success = true;
		}
		catch (SQLException se) {
			System.out.println("sql error ="+se);
			se.printStackTrace();
			return false;
		}

		return success;
	}


	
	public static boolean removeDepTask(int taskid, String projectid,String selfid) throws SQLException {
		Connection connection = null;
		boolean success = false;
		try{
			connection = ETSDBUtils.getConnection();
			success= removeDepTask(taskid, projectid, selfid,connection);
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return success;
		}
	 
	static boolean removeDepTask(int taskid,String projectid, String selfid,Connection conn) throws SQLException {
		boolean success = false;
		
		PreparedStatement taskSt = conn.prepareStatement("update ets.ets_task_main " +
			"set parent_task_id=? where task_id=? and project_id=? and self_id=?");
	

		try{
			taskSt.setInt(1,0);
			taskSt.setInt(2,taskid);
			taskSt.setString(3,projectid);
			taskSt.setString(4,selfid);
			
			taskSt.executeUpdate();
			taskSt.close();
			success = true;
		}
		catch (SQLException se) {
			System.out.println("sql error ="+se);
			return false;
		}
		return success;
	}

	static boolean removeDelTaskDepTask(int taskid,String projectid, String selfid,Connection conn) throws SQLException {
			boolean success = false;
		
			PreparedStatement taskSt = conn.prepareStatement("update ets.ets_task_main " +
				"set parent_task_id=? where parent_task_id=? and project_id=? and self_id=?");
	

			try{
				taskSt.setInt(1,0);
				taskSt.setInt(2,taskid);
				taskSt.setString(3,projectid);
				taskSt.setString(4,selfid);
			
				taskSt.executeUpdate();
				taskSt.close();
				success = true;
			}
			catch (SQLException se) {
				System.out.println("sql error ="+se);
				return false;
			}
			return success;
		}



	private static Vector getTasks(ResultSet rs, boolean includeCnt) throws SQLException {
	 Vector v = new Vector();

	 while (rs.next()) {
	 	
			 ETSTask task = getTask(rs, includeCnt);
			 v.addElement(task);
	 }
	 return v;
	}
	 
	 private static ETSTask getTask(ResultSet rs, boolean includeCnt) throws SQLException {
		 ETSTask task = new ETSTask();
		System.out.println("here here here");
		 task.setId(rs.getInt("TASK_ID"));
		 System.out.println("taskid="+task.getId());
		 task.setProjectId(rs.getString("PROJECT_ID"));
		 task.setSelfId(rs.getString("SELF_ID"));
		 task.setCreatorId(rs.getString("CREATOR_ID"));
		 task.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
		 task.setSection(rs.getString("SECTION"));
		 task.setTitle(rs.getString("TITLE"));
		 task.setDescription(rs.getString("DESCRIPTION"));
		 task.setStatus(rs.getString("STATUS"));
		 task.setDueDate(rs.getTimestamp("DUE_DATE"));
		 task.setOwnerId(rs.getString("OWNER_ID"));
		 task.setWorkRequired(rs.getString("WORK_REQUIRED"));
		 task.setActionRequired(rs.getString("ACTION_REQUIRED"));
		 task.setIbmOnly(rs.getString("IBM_ONLY"));
		 task.setParentTaskId(rs.getInt("PARENT_TASK_ID"));
		 task.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
		 task.setLastUserid(rs.getString("LAST_USERID"));
		 task.setCompany(rs.getString("COMPANY"));
		 task.setTrackerType(rs.getString("TRACKER_TYPE"));
		 
		 // Added for 6.3 - SRIKANTH
		 task.setLate(rs.getBoolean("IS_LATE"));
		 
		 if (includeCnt){
		 	System.out.println("cnt="+rs.getInt("CNT"));
		 	task.setHasDocs(rs.getInt("CNT"));
		 }
		 System.out.println("t.getid in gettaskrs="+task.getId());
		 return task;
	 }

	
//self assessment 

  public static String getSelfAssessTitle(String projectid,String selfid) throws SQLException{
	  Connection connection = null;
	  String res = "";
	  try{
		  connection = ETSDBUtils.getConnection();
		  res= getSelfAssessTitle(projectid,selfid,connection);
	  }
	  catch (SQLException e) {
		  throw e;
	  }
	  catch (Exception ex) {
		  //throw ex;
	  }
	  finally{
		  ETSDBUtils.close(connection);
	  }
		  return res;
	  }
	 
  static String getSelfAssessTitle(String projectid,String selfid,Connection conn) throws SQLException {
	String res = "Self Assessment";
	  PreparedStatement st = conn.prepareStatement("select self_name from ets.ets_self_main where project_id=? and self_id=?");

	  try{
		  st.setString(1,projectid);
		  st.setString(2,selfid);

		  ResultSet rs = st.executeQuery();
		  if (rs.next()){
			res = rs.getString("self_name");
		  }		
			
		  rs.close();
		  st.close();
  		  return res;
		
		  
	  }
	  catch (SQLException se) {
		  System.out.println("sql error ="+se);
		  se.printStackTrace();
		  throw se;
	  }
	  }




	public static String getSetMetTitle(String projectid,String sid) throws SQLException{
		Connection connection = null;
		String res = "";
		try{
			connection = ETSDBUtils.getConnection();
			res= getSetMetTitle(projectid,sid,connection);
		}
		catch (SQLException e) {
			throw e;
		}
		catch (Exception ex) {
			//throw ex;
		}
		finally{
			ETSDBUtils.close(connection);
		}
			return res;
	}
	 
	static String getSetMetTitle(String projectid,String sid,Connection conn) throws SQLException {
	  String res = "Self Assessment";
	  //modified by v2sagar qbr_name instead of self_name
		PreparedStatement st = conn.prepareStatement("select qbr_name from ets.ets_qbr_main where project_id=? and qbr_id=?");

		try{
			st.setString(1,projectid);
			st.setString(2,sid);

			ResultSet rs = st.executeQuery();
			if (rs.next()){
			  res = rs.getString("qbr_name");
			}		
			
			rs.close();
			st.close();
			return res;
		
		  
		}
		catch (SQLException se) {
			System.out.println("sql error ="+se);
			se.printStackTrace();
			throw se;
		}
		}
}



