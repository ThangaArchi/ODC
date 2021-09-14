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

public class ETSTask implements ETSObj{

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "4.4.1";


    protected int id;
	protected String project_id;
	protected String self_id;
    protected String creator_id;
   protected long created_date;
    protected String section;
    protected String title;
    protected String description;
    protected String status;
	protected long due_date;
	protected String owner_id;
	private String ownerName;
	private String creatorName;
	protected String work_required;
	protected String action_required;
	protected char ibmOnly;  //0,1,x
	protected int parent_task_id;
	protected String last_userid;
    protected long last_timestamp;
	protected boolean hasDoc;
	protected String company;
	protected String trackerType;

	protected boolean bLate = false;
	
    public ETSTask(){
		id = 0;
		project_id = "";
		self_id = "";
		creator_id = "";
		section = "";
		title = "";
		description = "";
		status = Defines.GREEN;
		owner_id ="";
		work_required ="";
		action_required ="";
		ibmOnly =  Defines.NOT_SET_FLAG;
		parent_task_id = 0;
		last_userid = "";
		company = "";
		hasDoc = false;
		trackerType = "D";
    }
    

    public void setId(int id){
		this.id = id;
    }
    public int getId(){
		return id;
    }

    public void setProjectId(String projectid){
		this.project_id = projectid;
    }
    public String getProjectId(){
		return project_id;
    }
    
	public void setSelfId(String sid){
		this.self_id = sid;
	}
	public String getSelfId(){
		return self_id;
	}

    public void setCreatorId(String creatorid){
		this.creator_id = creatorid;
    }
    public String getCreatorId(){
		return creator_id;
    }

	public long getCreatedDate(){
		return created_date;
	}
	public void setCreatedDate(){
		this.created_date = new Date().getTime();
	}
	public void setCreatedDate(long c_date){
		this.created_date = c_date;
	}
	public void setCreatedDate(java.sql.Timestamp d){
		this.created_date = d.getTime();
	}


    public void setSection(String section){
		this.section = section;
    }
    public String getSection(){
		return section;
    }

    public void setTitle(String title){
		this.title = title;
    }
    public String getTitle(){
		return title;
    }

	public void setDescription(String desc){
		this.description = desc;
	}
	public String getDescription(){
		return description;
	}

	
	public void setStatus(String status){
		if (status.equalsIgnoreCase(Defines.GREEN_STATUS)){
			this.status = Defines.GREEN;		
		}
		else if(status.equalsIgnoreCase(Defines.YELLOW_STATUS)){
		    this.status = Defines.YELLOW;
		}
		else if (status.equalsIgnoreCase(Defines.RED_STATUS)){
		    this.status = Defines.RED;
		}
		else{
		this.status = status;
    }
    }
    public String getStatus(){
		return status;
    }
	public String getStatusString(){
		if (status.equals(Defines.GREEN)){
			return Defines.GREEN_STATUS;		
		}
		else if(status.equals(Defines.YELLOW)){
			return Defines.YELLOW_STATUS;
		}
		else if (status.equals(Defines.RED)){
			return Defines.RED_STATUS;
		}
		else{
			return status;
		}
	}
	public String getStatusColor(){
	    String strLateFlag = "";
	    if (bLate && !status.equals(Defines.GREEN)) {
	        strLateFlag = " - Late";
	    }
	    
	    String strColor = "";
		if (status.equals(Defines.GREEN)){
			strColor = "#33cc33";		
		}
		else if(status.equals(Defines.YELLOW)){
		    strColor = "#ffcc00";
		}
		else if (status.equals(Defines.RED)){
		    strColor = "#ff3333";
		}
		
		if (bLate) {
		    strColor = "#ff3333";
		}

		if (status.equals(Defines.GREEN)){
			return "<span style=\"color:" + strColor + "\"><b>Complete"+ strLateFlag +"</b></span>";		
		}
		else if(status.equals(Defines.YELLOW)){
			return "<span style=\"color:" + strColor + "\"><b>In Progress"+ strLateFlag +"</b></span>";
		}
		else if (status.equals(Defines.RED)){
			return "<span style=\"color:" + strColor + "\"><b>Not Started"+ strLateFlag +"</b></span>";
		}
		else{
			return "N/A";
		}
	}

    
	public long getDueDate(){
		return due_date;
	}
	public Timestamp getTSDueDate(){
		return new Timestamp(due_date);
	}
	public String[] getDueDateStrs(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(this.due_date));
		String[] s = new String[3];
		String m =	String.valueOf(c.get(Calendar.MONTH));
		String d =	String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		String y =	String.valueOf(c.get(Calendar.YEAR));
		s[0] = m;
		s[1] = d;
		s[2] = y;
		return s;
	}
	public void setDueDate(){
		this.due_date = new Date().getTime();
	}
	public void setDueDate(String smonth, String sday, String syear){
		int month = Integer.parseInt(smonth.trim());
		int day = Integer.parseInt(sday.trim());
		int year = Integer.parseInt(syear.trim());
		
		Calendar c = Calendar.getInstance();
		c.set(year,month,day);
		Date d = c.getTime();
		this.due_date = d.getTime();		
		
	}
	public void setDueDate(long c_date){
		this.due_date = c_date;
	}
	public void setDueDate(java.sql.Timestamp d){
		this.due_date = d.getTime();
	}

    public void setOwnerId(String ownerid){
		this.owner_id = ownerid;
    }
    public String getOwnerId(){
		return owner_id;
    }

    public void setWorkRequired(String workreq){
		this.work_required = workreq;
    }
    public String getWorkRequired(){
		return work_required;
    }

	public void setActionRequired(String actreq){
		this.action_required = actreq;
	}
	public String getActionRequired(){
		return action_required;
	}
	
	public void setIbmOnly(char c) {
		ibmOnly = c;
	}
	public void setIbmOnly(boolean b) {
		if (b)
			ibmOnly = Defines.TRUE_FLAG;
		else
			ibmOnly = Defines.FALSE_FLAG;			
	}
	public void setIbmOnly(String s){
		if (s != null && !s.equals("")){
			char c = s.charAt(0);
			ibmOnly = c;
		}
		else{
			ibmOnly = Defines.NOT_SET_FLAG;
		}
	}
	public char getIbmOnly() {
		return ibmOnly;
	}
	public boolean isIbmOnly() {
		if(ibmOnly == Defines.TRUE_FLAG)
			return true;
		else //if(ibmOnly == ETSDatabaseManager.FALSE_FLAG || ibmOnly == ETSDatabaseManager.NOT_SET_FLAG)
			return false;
		//else
		  //  throw new RuntimeException("flag: ibmOnly: not true, not false, and not set");
	}

	public void setParentTaskId(int parenttaskid){
		this.parent_task_id = parenttaskid;
    }
    public int getParentTaskId(){
		return parent_task_id;
    }

	public void setCompany(String company){
		this.company = company;
	}
	public String getCompany(){
		 return company;
	}


    public void setLastUserid(String lastuserid){
		this.last_userid = lastuserid;
    }
    public String getLastUserid(){
		return last_userid;
    }

	public long getLastTimestamp(){
		return last_timestamp;
    }
	public void setLastTimestamp(){
		this.last_timestamp = new Date().getTime();
    }
	public void setLastTimestamp(long l_timestamp){
		this.last_timestamp = l_timestamp;
    }
	public void setLastTimestamp(java.sql.Timestamp d){
		this.last_timestamp = d.getTime();
    }

	public void setHasDocs(int cnt){
		if (cnt>0)
			this.hasDoc = true;	
		else
			this.hasDoc = false;
	}
	public boolean hasDocs(){
		return hasDoc;	
	}
	
	public Hashtable getHashTask(){
		Hashtable h = new Hashtable();
		try{
			h.put("taskid",(new Integer(id)).toString());
			h.put("section",section);
			h.put("title",title);
			h.put("month",(new Integer(getTaskMonth(due_date))).toString());
			h.put("day",(new Integer(getTaskDay(due_date))).toString());
			h.put("year",(new Integer(getTaskYear(due_date))).toString());
			h.put("status",status);
			h.put("owner",owner_id);
			h.put("company",company);
			h.put("wreq",work_required);
			h.put("areq",action_required);
		}
		catch(Exception e){
			System.out.println("e="+e);
			e.printStackTrace();	
		}
		return h;
	}

	public int getTaskMonth(long date){
		Calendar c = Calendar.getInstance();
		java.util.Date d = new java.util.Date(date);
		c.setTime(d);
		return c.get(Calendar.MONTH);
	}

	public int getTaskDay(long date){
		Calendar c = Calendar.getInstance();
		java.util.Date d = new java.util.Date(date);
		c.setTime(d);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public int getTaskYear(long date){
		Calendar c = Calendar.getInstance();
		java.util.Date d = new java.util.Date(date);
		c.setTime(d);
		return c.get(Calendar.YEAR);
	}


	public void setTrackerType(String s){
		trackerType = s;
	}
	public String getTrackerType(){
		return trackerType;
	}



	public String getStringKey(String key){
	 	if (key.equals(Defines.SORT_BY_DT_OWNER_STR))
			return owner_id;
		else
			return "";
			
	}
	public int getIntKey(String key){
		return 0;
	}


	/**
	 * @return
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * @return
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param string
	 */
	public void setCreatorName(String string) {
		creatorName = string;
	}

	/**
	 * @param string
	 */
	public void setOwnerName(String string) {
		ownerName = string;
	}

	/**
	 * @param bIsLate
	 */
	public void setLate(boolean bIsLate) {
	    bLate = bIsLate;
	}
	
	/**
	 * @return
	 */
	public boolean isLate() {
	    return bLate;
	}

}


