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

package oem.edge.ets.fe;



import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Date;

public class ETSGroup extends ETSDetailedObj implements ETSObj
{
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";

    //spn 0312 projid
    protected String projectid;
    protected String userid;

    protected String group_id;
    protected String group_name;
    protected String group_description;
    protected String group_type;
    protected String group_securityClassification;
    protected String group_owner;
    protected Vector vt_groupMembers;
    protected long last_timestamp;

    private String userType;


    public ETSGroup(){
	userid = "";
	projectid = "";
	group_id = "";

	group_name = "";
	group_description = "";
	last_timestamp = 0;
	group_owner = "";
	group_description = "";

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

    public void setGroupId(String groupid){
	this.group_id = groupid;
    }
    public String getGroupId(){
	return group_id;
    }


    public void setGroupName(String groupName){
		this.group_name = groupName;
    }
    public String getGroupName(){
		return group_name;
    }

    public void setGroupOwner(String groupOwner){
	this.group_owner = groupOwner;
    }
    public String getGroupOwner(){
	return group_owner;
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


	public void setGroupDescription(String groupdescription){
	this.group_description = groupdescription;
	}
	public String getGroupDescription(){
	return group_description;
	}
	public void setGroupType(String grouptype){
	this.group_type = grouptype;
	}
	public String getGroupType(){
	return group_type;
	}

	public void setGroupMembers(Vector grpMembers) {
		this.vt_groupMembers = grpMembers;
	}
	public Vector getGroupMembers() {
		return vt_groupMembers;
	}

	public void setGroupSecurityClassification(String secClassification) {
	/*	if (secClassification.equals("0") || secClassification.equals("1")) {
			setGroupType("PUBLIC");
		} else {
			int sec = Integer.parseInt(secClassification) - 2;
			secClassification = String.valueOf(sec);
			setGroupType("PRIVATE");
		}*/
			
		this.group_securityClassification = secClassification;
	}
	public String getGroupSecurityClassification() {
		return group_securityClassification;
	}

	public boolean isGroupPrivate(){
		String strGrpType = getGroupType();
		if (strGrpType.equalsIgnoreCase("PRIVATE")){
			return true;
		}else {
			return false;
		}
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

	public String getStringKey(String key) {
		if (key.equals(Defines.SORT_BY_NAME_STR))
			return group_name;
		//else if (key.equals(Defines.SORT_BY_TYPE_STR))
		//	return last_timestamp;
		else if (key.equals(Defines.SORT_BY_AUTH_STR))
			return group_owner;
		else
			return "";

	}
	public int getIntKey(String key) {
		return 0;
	}


}
