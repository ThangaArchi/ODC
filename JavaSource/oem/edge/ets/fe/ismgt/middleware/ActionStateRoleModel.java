/*
 * Created on May 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.ismgt.middleware;

import java.util.Hashtable;

import oem.edge.ets.fe.Defines;

/**
 * @author jetendra
 * @description This class encapsulates the various action matrices 
 * required for the various roles on an issue and the various states on an issue
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ActionStateRoleModel {

	public static final String VERSION = "1.5";
// Create/Modify/resolve/Reject/Close/Comment/Withdraw/view/Subscribe/Changeowner

	static ActionMatrix role_ISSUE_SUBMITTER= new ActionMatrix(true,true,false,true,true,true,true,true,false,false);
	static ActionMatrix role_ISSUE_OWNER= new ActionMatrix(false,true,true,false,false,true,false,true,false,true);
	static ActionMatrix role_WORKSPACE_MEMBER= new ActionMatrix(true,false,false,false,false,true,false,true,true,false);
	static ActionMatrix role_WORKSPACE_VISITOR= new ActionMatrix(false,false,false,false,false,false,false,true,false,false);
	static ActionMatrix role_WORKSPACE_OWNER= new ActionMatrix(true,true,true,true,true,true,true,true,true,true);
	static ActionMatrix role_WORKSPACE_MANAGER= new ActionMatrix(true,true,true,true,true,true,true,true,true,true);
	static ActionMatrix role_WORKSPACE_CLIENT= new ActionMatrix(true,true,false,true,true,true,true,true,false,false);
	static ActionMatrix role_ETS_ADMIN= new ActionMatrix(true,true,true,true,true,true,true,true,true,true);
	static ActionMatrix role_ETS_EXECUTIVE= new ActionMatrix(true,true,false,true,true,true,true,true,false,false);
	
	static ActionMatrix state_Assigned= new ActionMatrix(false,true,true,false,false,true,true,true,true,true);
	static ActionMatrix state_Resolved= new ActionMatrix(false,false,false,true,true,true,true,true,true,true);
	static ActionMatrix state_Rejected= new ActionMatrix(false,true,true,false,true,true,true,true,true,true);
	static ActionMatrix state_Closed= new ActionMatrix(false,false,false,false,false,true,false,true,false,false);
    static ActionMatrix state_Withdrawn= new ActionMatrix(false,false,false,false,false,true,false,true,false,false);

	
	static private Hashtable RoleActionMap = new Hashtable();
	static private Hashtable StateActionMap= new Hashtable();
	// Initializes the matrices for the various workspace roles and states
	static {
		RoleActionMap.put("ISSUE_SUBMITTER",role_ISSUE_SUBMITTER);
		RoleActionMap.put("ISSUE_OWNER",role_ISSUE_OWNER);
		RoleActionMap.put(Defines.WORKSPACE_MEMBER,role_WORKSPACE_MEMBER);
		RoleActionMap.put(Defines.WORKSPACE_VISITOR,role_WORKSPACE_VISITOR);
		RoleActionMap.put(Defines.WORKSPACE_OWNER,role_WORKSPACE_OWNER);
		RoleActionMap.put(Defines.WORKSPACE_MANAGER,role_WORKSPACE_MANAGER);
		RoleActionMap.put(Defines.WORKSPACE_CLIENT,role_WORKSPACE_CLIENT);
		RoleActionMap.put(Defines.ETS_ADMIN,role_ETS_ADMIN);
		RoleActionMap.put(Defines.ETS_EXECUTIVE,role_ETS_EXECUTIVE);
		
		StateActionMap.put("Assigned",state_Assigned);
		StateActionMap.put("Resolved",state_Resolved);
		StateActionMap.put("Rejected",state_Rejected);
		StateActionMap.put("Closed", state_Closed);
		StateActionMap.put("Withdrawn",state_Withdrawn);
		
	}
	static ActionMatrix getActionMatrixforRole(String role){
		
		ActionMatrix temp = null;
		try{
			temp =  (ActionMatrix)RoleActionMap.get(role);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
		
	}
	static ActionMatrix getActionMatrixforState(String role){
	
			ActionMatrix temp = null;
			try{
				temp =  (ActionMatrix)StateActionMap.get(role);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return temp;
		
		}
	 
}
