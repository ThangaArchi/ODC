//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\ActionStep_Creator.java

package oem.edge.ets.fe.ismgt.middleware;

//import org.exolab.castor.builder.binding.Interface;


public  class ActionStep_Creator 
{
	public static final String VERSION = "1.0";
   /**
    * @roseuid 42753BE402A1
    */
   public ActionStep_Creator() 
   {
    
   }
   
   /**
    * @description This class is a factory class that create the appropriate action 
    * class based on a string that determines tha action that needs to be done, decided
    * by the workflow class.
    * @return oem.edge.ets.fe.ismgt.middleware.ActionStep_Product
    * @roseuid 427137F40208
    */
   public oem.edge.ets.fe.ismgt.middleware.ActionStep factoryMethod(String actionobjname){
  
  		ActionStep step = null;
  		// object for processing submit action
  		if (actionobjname.equalsIgnoreCase("UpdateIssue_Submit")) {
		step= new ActionStep_UpdateIssue_Submit();
		}
		// object for processing mofify action
		if (actionobjname.equalsIgnoreCase("UpdateIssue_Modify")) {
			step= new ActionStep_UpdateIssue_Modify();
		}
		// Objects for various actions
		if (actionobjname.equalsIgnoreCase("UpdateIssue_Resolve")
			|| actionobjname.equalsIgnoreCase("UpdateIssue_Reject")
			|| actionobjname.equalsIgnoreCase("UpdateIssue_Close")
			|| actionobjname.equalsIgnoreCase("UpdateIssue_Withdraw")
            || actionobjname.equalsIgnoreCase("UpdateIssue_Comment") ) {
				step= new ActionStep_UpdateIssue_Action();
			}
		// Special object for the changeowner action	
		if (actionobjname.equalsIgnoreCase("UpdateIssue_ChangeOwner") ) {
					step= new ActionStep_UpdateIssue_Changeowner();
				}
		if (actionobjname.equalsIgnoreCase("UpdateIssueType")) {
			step= new ActionStep_UpdateIssueType();
			}
		if (actionobjname.equalsIgnoreCase("IssueNotify")) {
				step= new ActionStep_Notify();
				}
			
		return step;
   }
}
