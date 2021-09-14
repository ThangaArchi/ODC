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


import oem.edge.ets.fe.*;

import java.util.*;
import java.sql.Timestamp;

public class ETSDealTrackerResultObj implements ETSObj{

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "5.1.1";


	protected String sortby;
	protected String ad;
	protected String selfid;
	protected Vector resultTasks;
	protected boolean errorFlag;
	protected String errorMsg;
	protected boolean accessErrorFlag;
	protected String accessErrorMsg;
	protected Hashtable formHash;
	protected boolean ibmonly;
	protected ETSTask task;
	protected ETSTask deptask;
	protected Vector users;
	protected Vector ibmusers;
	protected Vector docs;
	protected Vector comms;
	protected Vector deptasks;
	protected ETSTaskComment taskcomm;	
	protected String taskcommstr;	
	protected int taskid;
	protected ETSDoc doc;
	protected String backStr;
	
	//protected String setid;
	protected boolean isSetMet;
	protected String trackerType;

	public ETSDealTrackerResultObj(){
		
		taskid = 0;
		selfid = "";
		errorFlag = false;
		errorMsg = "";
		accessErrorFlag = false;
		accessErrorMsg = "";
		ibmonly = false;
		docs = new Vector();
		comms = new Vector();
		deptasks = new Vector();
		taskcomm = new ETSTaskComment();
		taskcommstr = "";
		resultTasks = new Vector();
		backStr = "'Contracts' main'";
		//setid = "";
		isSetMet = false;
		trackerType = "D";
   }
    
   	public void setTaskId(int i){
		taskid = i;
	}
	public int getTaskId(){
		return taskid;
	}


   public void setSelfId(String s){
	   selfid = s;
   }
   public String getSelfId(){
	   return selfid;
   }

   	public void setSortBy(String sb){
		sortby = sb;
	}
	public String getSortBy(){
		return sortby;
	}
    
	public void setAD(String s){
		ad = s;
	}
	public String getAD(){
		return ad;
	}
	
	public void setIbmOnly(boolean b){
		ibmonly = b;	
	}
	public boolean getIbmOnly(){
		return ibmonly;	
	}
	
	
  	public void setErrorFlag(boolean b){
  		errorFlag = b;	
  	}
  	public boolean getErrorFlag(){
  		return errorFlag;	
  	}
	public void setErrorMsg(String s){
		errorMsg = s;
	}
	public String getErrorMsg(){
		return errorMsg;
	}
	
	
	public void setAccessErrorFlag(boolean b){
		accessErrorFlag = b;	
	}
	public boolean getAccessErrorFlag(){
		return accessErrorFlag;	
	}
	public void setAccessErrorMsg(String s){
		accessErrorMsg = s;
	}
	public String getAccessErrorMsg(){
		return accessErrorMsg;
	}

 
	public void setTask(ETSTask t){
		task = t;
	}
	public ETSTask getTask(){
		return task;
	}
	
	public void setDepTask(ETSTask t){
		deptask = t;
	}
	public ETSTask getDepTask(){
		return deptask;
	}
  	
	public void setTaskComm(ETSTaskComment t){
		taskcomm = t;
	}
	public ETSTaskComment getTaskComm(){
		return taskcomm;
	}
	public void setTaskCommStr(String t){
		taskcommstr = t;
	}
	public String getTaskCommStr(){
		return taskcommstr;
	}


	public void setFormHash(Hashtable h){
		formHash = h;
	}
	public Hashtable getFormHash(){
		return formHash;
	}


	public void setResultTasks(Vector v){
    	resultTasks = v;
    }
	public Vector getResultTasks(){
		return resultTasks;
    }



	public void setUsers(Vector v){
		users = v;
	}
	public Vector getUsers(){
		return users;
	}

	public void setIbmUsers(Vector v){
		ibmusers = v;
	}
	public Vector getIbmUsers(){
		return ibmusers;
	}


	public void setTaskDocs(Vector v){
		docs = v;
	}
	public Vector getTaskDocs(){
		return docs;
	}
	public void setTaskComms(Vector v){
		comms = v;
	}
	public Vector getTaskComms(){
		return comms;
	}
	public void setDepTasks(Vector v){
		deptasks = v;
	}
	public Vector getDepTasks(){
		return deptasks;
	}


	public void setDoc(ETSDoc d){
		doc = d;	
	}
	public ETSDoc getDoc(){
		return doc;	
	}

	public void setBackStr(String s){
		backStr = s;
	}
	public String getBackStr(){
		return backStr;
	}



	public void setIsSetMet(boolean b){
		isSetMet = b;	
	}
	public boolean isSetMet(){
		return isSetMet;	
	}

	public void setTrackerType(String s){
		trackerType = s;
	}
	public String getTrackerType(){
		return trackerType;
	}

 
	public String getStringKey(String key){
	 	if (key.equals(Defines.SORT_BY_DT_OWNER_STR))
			return "";
		else
			return "";
			
	}
	public int getIntKey(String key){
		return 0;
	}


}


