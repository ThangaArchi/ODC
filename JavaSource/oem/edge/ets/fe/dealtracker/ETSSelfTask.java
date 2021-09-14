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

public class ETSSelfTask extends ETSTask{

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "5.1.1";


	ETSSelfTask(){
		super();	
	}
	ETSSelfTask(ETSTask t){
		super();	
		setVars(t);
	}
	private void setVars(ETSTask t){
		id = t.getId();
		project_id = t.getProjectId();
		self_id = t.getSelfId();
		creator_id = t.getCreatorId();
		section = t.getSection();
		title = t.getTitle();
		description = t.getDescription();
		status = t.getStatus();
		owner_id = t.getOwnerId();
		work_required = t.getWorkRequired();
		action_required = t.getActionRequired();
		ibmOnly =  t.getIbmOnly();
		parent_task_id = t.getParentTaskId();
		last_userid = t.getLastUserid();
		company = t.getCompany();
		hasDoc = t.hasDocs();
		created_date = t.getCreatedDate();
		due_date = t.getDueDate();
		trackerType = t.getTrackerType();
	}

	
    public void setSection(String section){
		this.section = section;
    }
    public String getSection(){
		return section;
    }

	public void setStatus(String status){
		this.status = status;
    }
    public String getStatus(){
		return status;
    }
	public String getStatusString(){
		if (status.equals(Defines.GREEN)){
			return "Complete";		
		}
		else if(status.equals(Defines.YELLOW)){
			return "In progress";
		}
		else if (status.equals(Defines.RED)){
			return "Not started";
		}
		else{
			return status;
		}
	}
	public String getStatusColor(){
		if (status.equals(Defines.GREEN)){
			return "<span style=\"color:#33cc33\"><b>Complete</b></span>";		
		}
		else if(status.equals(Defines.YELLOW)){
			return "<span style=\"color:#ffcc00\"><b>In progress</b></span>";
		}
		else if (status.equals(Defines.RED)){
			return "<span style=\"color:#ff3333\"><b>Not started</b></span>";
		}
		else{
			return "N/A";
		}
	}


}


