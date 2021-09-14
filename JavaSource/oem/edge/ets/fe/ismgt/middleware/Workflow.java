//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\Workflow.java

package oem.edge.ets.fe.ismgt.middleware;

import java.util.Vector;

import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;


public interface Workflow 
{
	public static final String VERSION = "1.0";
   /**
    * @return String
    * @roseuid 42712E060144
    */
   public String getNextStateforAction(String state);
   
   /**
    * @return java.util.Vector)
    * @roseuid 42712EA602CB
    */
   public java.util.Vector getStepstoProcessAction(String action);
   
   /**
    * This method gets all the possible actions that can be taken when an issue is in 
    * a current state
    * @return Vector
    * @roseuid 42710AAC01A5
    */
   public Object getActionsforState(String state , EtsIssObjectKey issobjkey);
   
}
