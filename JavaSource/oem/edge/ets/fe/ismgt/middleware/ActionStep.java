//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\ActionStep.java

package oem.edge.ets.fe.ismgt.middleware;

import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;


public interface ActionStep 
{
	public static final String VERSION = "1.0";
   /**
    * @roseuid 4271386401E1
    */
   public boolean executeActionStep(ETSIssue usr1issue , EtsIssObjectKey issobjkey);

}
