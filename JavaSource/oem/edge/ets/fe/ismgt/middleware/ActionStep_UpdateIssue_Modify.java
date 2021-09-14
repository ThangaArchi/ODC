//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\ActionStep_UpdateIssue_Modify.java

package oem.edge.ets.fe.ismgt.middleware;

import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;


public class ActionStep_UpdateIssue_Modify implements ActionStep 
{
	public static final String VERSION = "1.0";
   /**
    * @roseuid 42753BE50085
    */
   public ActionStep_UpdateIssue_Modify() 
   {
    
   }
   
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.middleware.ActionStep#executeActionStep(oem.edge.ets.fe.ismgt.model.ETSIssue, oem.edge.ets.fe.ismgt.model.EtsIssObjectKey)
	 */
	public boolean executeActionStep(
		ETSIssue usr1issue,
		EtsIssObjectKey issobjkey) {
			boolean flg = false;
			ETSMW_IssueDAO mwdao = new ETSMW_IssueDAO();
			ETSMWIssue mwIssue = (ETSMWIssue)usr1issue;
			try {
				if (ComLogGen.generateCommLog(mwIssue) && ETSMW_IssueDAO.updateIssueRecord(mwIssue))
							flg=true;
					
					
			}
			catch (Exception e) {
			}
			return flg;
		
		
	}

}
