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


import java.util.*;
import java.sql.Timestamp;

public class ETSTaskComment {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "4.4.1";

	protected int task_id;
	protected String project_id;
	protected String selfId;
    protected String comment;
	protected String last_userid;
    protected long last_timestamp;

    public ETSTaskComment(){
		task_id = 0;
		project_id = "";
		selfId="";
		comment ="";
		last_userid = "";
    }
    

    public void setTaskId(int id){
		this.task_id = id;
    }
	public void setTaskId(String id){
		this.task_id = new Integer(id).intValue();
	}
    public int getTaskId(){
		return task_id;
    }


    public void setProjectId(String projectid){
		this.project_id = projectid;
    }
    public String getProjectId(){
		return project_id;
    }


	public void setSelfId(String s){
		this.selfId = s;
	}
	public String getSelfId(){
		return selfId;
	}

    public void setComment(String c){
		this.comment = c;
    }
    public String getComment(){
		return comment;
    }


    public void setLastUserid(String lastuserid){
		this.last_userid = lastuserid;
    }
    public String getLastUserid(){
		return last_userid;
    }


    long getLastTimestamp(){
		return last_timestamp;
    }
    void setLastTimestamp(){
		this.last_timestamp = new Date().getTime();
    }
    void setLastTimestamp(long l_timestamp){
		this.last_timestamp = l_timestamp;
    }
    void setLastTimestamp(java.sql.Timestamp d){
		this.last_timestamp = d.getTime();
    }


}


