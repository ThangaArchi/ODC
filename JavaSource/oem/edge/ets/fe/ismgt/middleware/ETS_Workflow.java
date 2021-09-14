//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\ETS_Workflow.java

package oem.edge.ets.fe.ismgt.middleware;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssUserActionsModel;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;

import com.ibm.nio.cs.ISCII91;
import com.ibm.ws.webservices.engine.enum.Enum;
import com.ibm.ws.webservices.engine.enum.Use;


public class ETS_Workflow implements Workflow 
{
	public static final String VERSION = "1.0";
	private Hashtable actiontostateMap = null;
	
	
	
   /**
    * @roseuid 42764DE90343
    */
   public ETS_Workflow() 
   {
    init_actionstateMap();
   }
   private void init_actionstateMap() 
	  {
    	actiontostateMap =  new Hashtable();
    	actiontostateMap.put("Submit","Assigned");
		actiontostateMap.put("Modify","Assigned");
		actiontostateMap.put("Reject","Rejected");
		actiontostateMap.put("Resolve","Resolved");
		actiontostateMap.put("Close","Closed");
		actiontostateMap.put("Withdraw","Withdrawn");
		actiontostateMap.put("Changeowner","unchanged");
		actiontostateMap.put("Comment","unchanged");
		
	  }
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.middleware.Workflow#getNextStateforAction(java.lang.String)
	 */
	public String getNextStateforAction(String action) {
		String newstate = "unchanged";
		newstate = (String)actiontostateMap.get(action);
		return newstate;
 
	}
	private int actiontoInt(String action) {
			// TODO Auto-generated method stub
 		
		int actionint = 0;
			if(action.equals("Assigned"))
				actionint=0;
			if(action.equals("Reject"))
				actionint=1;
			if(action.equals("Withdraw"))
				actionint=2;
			if(action.equals("Close"))
				actionint=3;
			if(action.equals("Re-Submit"))
				actionint=4;
			if(action.equals("Info-reply"))
				actionint=5;
			if(action.equals("Resolve"))
				actionint=6;
			if(action.equals("Accept"))
				actionint=7;
			if(action.equals("Comment"))
				actionint=8;
			if(action.equals("Changeowner"))
								actionint=9;
			
			return actionint;
		}
		
	private int statetoInt(String state) {
				// TODO Auto-generated method stub
 		
			int stateint = 0;
				if(state.equals("Assigned"))
					stateint=0;
				if(state.equals("Reject"))
					stateint=1;
				if(state.equals("Withdraw"))
					stateint=2;
				if(state.equals("Close"))
					stateint=3;
				if(state.equals("Re-Submit"))
					stateint=4;
				if(state.equals("Info-reply"))
					stateint=5;
				if(state.equals("Resolve"))
					stateint=6;
				if(state.equals("Accept"))
					stateint=7;
				if(state.equals("Comment"))
					stateint=8;
				if(state.equals("Changeowner"))
									stateint=9;
			
				return stateint;
			}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.middleware.Workflow#getStepstoProcessAction(java.lang.String)
	 */
	public Vector getStepstoProcessAction(String action) {
		
		Vector actionsteps = new Vector();
		if(! action.equalsIgnoreCase("UpdateIssueType") && ! action.equalsIgnoreCase("Subscribe")){
		
			actionsteps.add("UpdateIssue"+ "_"+ action);
			actionsteps.add("Notify");
		}
		if(action.equalsIgnoreCase("UpdateIssueType") ){
		
					actionsteps.add("UpdateIssueType");
					
				}
		if(action.equalsIgnoreCase("Subscribe")){
		
					actionsteps.add("UpdateSubscribe");
				}
		
		return actionsteps;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.middleware.Workflow#getActionsforState(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Object getActionsforCurrentUser(
		String state,
		EtsIssObjectKey issobjkey ) {
		
		EtsIssUserActionsModel actionmodel= new EtsIssUserActionsModel();
		
		EtsIssUserRolesModel rolesmodel=issobjkey.getUsrRolesModel();
		ActionMatrix currentActions = new ActionMatrix();
		
		String edge_problem_id=(String) issobjkey.getParams().get("edge_problem_id");
		if(verifyIssueAccess(issobjkey,edge_problem_id)){

		boolean isOwner=false;
		boolean isSubmitter=false;
		
		String currentUserId=issobjkey.getEs().gUSERN;
		// Determine if current user is an owner
		if( edge_problem_id != null)
		isOwner=isUserOwner(issobjkey,edge_problem_id);
		
		// Determine if the current user is Submitter
		if( edge_problem_id != null)
			isSubmitter=isUserSubmitter(issobjkey,edge_problem_id);
		
		try {
//			get user workspace role
				 currentActions = currentActions.Add(ActionStateRoleModel.getActionMatrixforRole(getUsersWorkspaceRole(issobjkey)));
		
				 if(isSubmitter)
					 currentActions = currentActions.Add(ActionStateRoleModel.getActionMatrixforRole("ISSUE_SUBMITTER"));
			
				 if(isOwner)
				 currentActions = currentActions.Add(ActionStateRoleModel.getActionMatrixforRole("ISSUE_OWNER"));
				 // get user state based role
				 currentActions = currentActions.Filter(ActionStateRoleModel.getActionMatrixforState(state));
				 
				 if (isOwner || isSubmitter)
				 	currentActions.Subscribe = false;
		}
		catch (Exception e) {
		}
		
		}
		actionmodel.setActionavailable(true);
		actionmodel.setUsrSubmitIssue(currentActions.Create);
		actionmodel.setUsrModifyIssue(currentActions.Modify);
		actionmodel.setUsrResolveIssue(currentActions.Resolve);
		actionmodel.setUsrRejectIssue(currentActions.Reject);
		actionmodel.setUsrCloseIssue(currentActions.Close);
		actionmodel.setUsrCommentIssue(currentActions.Comment);
		actionmodel.setUsrChangeOwner(currentActions.Changeowner);
		actionmodel.setUsrWithDraw(currentActions.Withdraw);
		actionmodel.setUsrSubscribe(currentActions.Subscribe);
		actionmodel.setUsrViewIssue(currentActions.View);
	
		
		
		
		return actionmodel;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.middleware.Workflow#getActionsforState(java.lang.String, oem.edge.ets.fe.ismgt.model.EtsIssObjectKey)
	 */
	public Object getActionsforStatebasedonRole(String state, String role) {
		ActionMatrix temp = new ActionMatrix();
		temp=temp.Add(ActionStateRoleModel.getActionMatrixforRole(role));
		temp=temp.Filter(ActionStateRoleModel.getActionMatrixforState(state));
		return temp;
	}
	
	private String getUsersWorkspaceRole(EtsIssObjectKey issobjkey) {
			// TODO Auto-generated method stu
		//	String userRole = Defines.WORKSPACE_VISITOR;
			String userRole = EtsIssFilterUtils.checkUserRole(issobjkey.getEs(),issobjkey.getProj().getProjectId());
			if(userRole.equalsIgnoreCase(Defines.WORKSPACE_VISITOR) && issobjkey.isProjBladeType())
				userRole=Defines.WORKSPACE_MEMBER;
			return userRole;
			
}
	private boolean isUserSubmitter(EtsIssObjectKey issobjkey, String edge_problem_id) {
			boolean isSubmitter = false;
			try {
				String currentUserId=issobjkey.getEs().gUSERN;
				String submitterId=ETSIssuesManager.getIssue(edge_problem_id).problem_creator.trim();
				isSubmitter = currentUserId.equalsIgnoreCase(submitterId);
			
			}
			catch (Exception e) {
			}
			 return isSubmitter;
}
	private boolean verifyIssueAccess(EtsIssObjectKey issobjkey, String edge_problem_id) {
			boolean isIBMonly = false;
			boolean isUsrInternal = false;
			boolean hasAccess =true;
			try {
				String currentUserId=issobjkey.getEs().gUSERN;
				String issue_access=ETSIssuesManager.getIssue(edge_problem_id).issue_access;
				isIBMonly = issue_access.equalsIgnoreCase("IBM");
				isUsrInternal = issobjkey.getUsrRolesModel().isUsrInternal();
			
			}
			catch (Exception e) {
			}
			if(isIBMonly && (! isUsrInternal))
				hasAccess = false;
			 return hasAccess;
}
	private boolean isUserOwner(EtsIssObjectKey issobjkey , String edge_problem_id) {
			boolean isOwner=false;
			try {
				String userid =(issobjkey.getEs().gUSERN);
				Vector ownerRecords =ETSMW_IssueDAO.getOwnerRecord(edge_problem_id);
				if(ownerRecords != null){
					for(int i=0;i<ownerRecords.size();i++){
						ETSMWOwnerRecord ownerRecord= (ETSMWOwnerRecord)ownerRecords.elementAt(i);
						if(ownerRecord.getOwner_id().equals(userid))
							isOwner=true;
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return isOwner;
			 
}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.middleware.Workflow#getActionsforState(java.lang.String, oem.edge.ets.fe.ismgt.model.EtsIssObjectKey)
	 */
	public Object getActionsforState(String state, EtsIssObjectKey issobjkey) {
		// TODO Auto-generated method stub
		return null;
	}

	public String generateTypicalNextAction(String state , String role) {

				
		String issueclass = "issue";
		String nextaction = null;
		boolean isOwner= false;
		
		
						if(role.equals("ISSUE_OWNER"))
							isOwner=true;
				
						//sEmailStr.append("Having clicked on the above url, you may take the following actions by clicking on the 'Actions' tab: \n\n");

						if(state.equalsIgnoreCase("Assigned")){
							if(isOwner){
								 nextaction="Resolve";
							}
						}	
						else if(state.equals("Rejected")){
							if(isOwner){
									nextaction="Resolve";
							}
						}
						return nextaction;
		
						   

	}

}
