/*                       Copyright Header Check                             
*/
/*   --------------------------------------------------------------------   
*/
/*                                                                          
*/
/*     OCO Source Materials                                                 
*/
/*                                                                          
*/
/*     Product(s): PROFIT                                                   
*/
/*                                                                          
*/
/*     (C)Copyright IBM Corp. 2005                                          
*/
/*                                                                          
*/
/*     All Rights Reserved                                                  
*/
/*     US Government Users Restricted Rigts                                 
*/
/*                                                                          
*/
/*     The source code for this program is not published or otherwise       
*/
/*     divested of its trade secrets, irrespective of what has been         
*/
/*     deposited with the US Copyright Office.                              
*/
/*                                                                          
*/
/*   --------------------------------------------------------------------   
*/
/*     Please do not remove any of these commented lines  20 lines          
*/
/*   --------------------------------------------------------------------   
*/
/*                       Copyright Footer Check                             
*/
//Source file: C:\\Documents and Settings\\Administrator\\My 


package oem.edge.ets.fe.ismgt.middleware;

import java.sql.SQLException;
import java.util.Vector;

import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;

class ETS_IssMWProcessor implements IssMWProcessor
{
   public static final String VERSION = "1.7";
   private ETSMWIssue currentIssue;
   private ETS_Workflow ets_workflow = null;
   private EtsIssObjectKey issobjkey;

   /**
    * @roseuid 4276545B02CD
    */
   public ETS_IssMWProcessor(EtsIssObjectKey _issObjectKey)
   {
    issobjkey=_issObjectKey;
   }

   /**
    * This method uses the workflow object to get the next state for the 
action being
    * processed
    * @return java.lang.Void)
    * @roseuid 427130FF0166
    */
   private void setNewState()
   {

   }

   /**
    * @return Boolean
    * @roseuid 4271371C02B1
    */
   private Boolean executeSubActions()
   {
    return null;
   }

   /**
    * @roseuid 42750F4801B9
    */

	public boolean processRequest() throws SQLException, Exception{
		boolean flg=false;
		try {
			Workflow ets_workflow = new ETS_Workflow();
			String nextState = 
ets_workflow.getNextStateforAction(currentIssue.problem_state);

			if(! currentIssue.problem_class.equalsIgnoreCase("Feedback")){
				// To handle actions that do not change state

				if(nextState.equalsIgnoreCase("unchanged"))
					nextState = ETSMW_IssueDAO. getCurrentState(currentIssue);
				currentIssue.nextState= nextState;
				Vector actionsteps = 
ets_workflow.getStepstoProcessAction(currentIssue.problem_state);
				int actionsize = actionsteps.size();
				if (actionsize > 0) {
					ActionStep_Creator createStep =  new ActionStep_Creator();
					for( int i=0;i < actionsize; i++){
						ActionStep action = 
createStep.factoryMethod((String)actionsteps.elementAt(i));
						if( action.executeActionStep(currentIssue,issobjkey))
						break;
					}
				flg = true;
				// Emails to be sentfor each action
				ActionStep_Notify notify = new ActionStep_Notify();
				notify.setWorkflow(ets_workflow);
				notify.executeActionStep(currentIssue,issobjkey);

				} else {
					System.out.println(" ERROR :Could not get any action objects for processing this action");
				}
			}

			else {
				currentIssue.nextState = "Submitted";
				if(currentIssue.problem_state.equalsIgnoreCase("Submit"))
					flg =new 
ActionStep_UpdateFeedback_Submit().executeActionStep(currentIssue,issobjkey);
				else
					flg =new 
ActionStep_UpdateFeedback_Modify().executeActionStep(currentIssue,issobjkey);
			}



		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("MW Exception: Could not process request :" + 
e.getMessage());
		}
		return flg;
	}

	public void setIssue(ETSIssue _issue) {

		currentIssue = (ETSMWIssue)_issue;


		}

}

