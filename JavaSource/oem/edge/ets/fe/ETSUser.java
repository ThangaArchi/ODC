/*   ------------------------------------------------------------------          */
/*   IBM                                                                                     */
/*                                                                                               */  
/*   OCO Source Materials                                                          */
/*                                                                                               */
/*   Product(s): ICC/PROFIT                                                       */
/*                                                                                               */
/*   (C)Copyright IBM Corp. 2002,2003 		              */ 
/*                                                                                               */  
/*   The source code for this program is not published or otherwise */
/*   divested of its trade secrets, irrespective of what has been        */
/*   deposited with the US Copyright Office.                                  */
/*   ------------------------------------------------------------------           */

package oem.edge.ets.fe;

import java.text.SimpleDateFormat;
import java.util.Date;
//import com.ibm.as400.webaccess.common.*;
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

public class ETSUser extends ETSDetailedObj implements ETSObj
{
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";

    protected int roleid;
    //spn 0312 projid
    protected String projectid;
    protected String userid;

    protected String user_job;
    protected char primary_contact;
    protected String last_userid;
    protected long last_timestamp;
	
	protected String username;
	protected String company;
	protected String rolename;
	
	protected String active_flag;
	
	private String userType;
	

    public ETSUser(){
	userid = "";
	//spn 0312 projectid = 0;
	projectid = "";
	roleid = 0;

	user_job = "";
	primary_contact = Defines.NOT_SET_FLAG;
	last_userid = "";
	last_timestamp = 0;
	username = "";
	company = "";
	
	active_flag = "";
    }

    public void setUserId(String id){
	this.userid = id;
    }
    public String getUserId(){
	return userid;
    }

    //spn 0312 projid
    public void setProjectId(String projectid){
	this.projectid = projectid;
    }
    public String getProjectId(){
	return projectid;
    }

    public void setRoleId(int roleid){
	this.roleid = roleid;
    }
    public int getRoleId(){
	return roleid;
    }


    public void setUserJob(String job){
	this.user_job = job;
    }
    public String getUserJob(){
	return user_job;
    } 

    public void setPrimaryContact(char c) {
	primary_contact = c;
    }
    public void setPrimaryContact(String s){
	if (s != null && !s.equals("")){
	    char c = s.charAt(0);
	    primary_contact = c;
	}
	else{
	    primary_contact = Defines.NOT_SET_FLAG;
	}
    }
    public char getPrimaryContact() {
        return primary_contact;
    }
    public boolean isPrimaryContact() {
	if(primary_contact == Defines.YES)
	    return true;
	else // if (primary_contact == Defines.NO || primary_contact == null)
	    return false;
	//else
	//throw new RuntimeException("flag: primary_contact: not true, not false, and not set");
	
	/*
	  if(primary_contact == Defines.TRUE_FLAG)
	  return true;
	  else if(primary_contact == Defines.FALSE_FLAG || primary_contact == Defines.NOT_SET_FLAG)
	  return false;
	  else
	  throw new RuntimeException("flag: primary_contact: not true, not false, and not set");
	*/
    }   


    public void setLastUserId(String lastuserid){
	this.last_userid = lastuserid;
    }
    public String getLastUserId(){
	return last_userid;
    }


	public String getFormattedLastTimestamp() {
		Date dtLastTimestamp = new Date(last_timestamp);
		SimpleDateFormat pdDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		return pdDateFormat.format(dtLastTimestamp);
	}
    public long getLastTimestamp(){
	return last_timestamp;
    }
    public void setLastTimestamp(){
	this.last_timestamp = new Date().getTime();
    }
    public void setLastTimestamp(long d){
	this.last_timestamp = d;
    }
    public void setLastTimestamp(java.sql.Timestamp d){
	this.last_timestamp = d.getTime();
    }
    
    
	public void setUserName(String name){
	this.username= name;
	}
	public String getUserName(){
	return username;
	}
	public void setCompany(String company){
	this.company = company;
	}
	public String getCompany(){
	return company;
	}
	public void setRoleName(String rn){
	this.rolename = rn;
	}
	public String getRoleName(){
	return rolename;
	}
	
	
	public void setActiveFlag(String flag){
		this.active_flag = flag;
	}
	public String getActiveFlag(){
		return active_flag;
	}


	public String getStringKey(String key){
		if (key.equals(Defines.SORT_BY_COMP_STR))
			return company;
		else
			return "";
			
	}
	public int getIntKey(String key){
		if (key.equals(Defines.SORT_BY_ACCLEV_STR))
			return roleid;
		else
			return 0;
	}   
	/**
	 * @return
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param string
	 */
	public void setUserType(String string) {
		userType = string;
	}

}
