//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\ActionStep_UpdateIssue_Submit.java

package oem.edge.ets.fe.ismgt.middleware;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

//import com.tivoli.pd.jasn1.boolean32;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;

public class ActionStep_UpdateIssue_Action implements ActionStep {
	public static final String VERSION = "1.0";
	
	/**
	 * @roseuid 42753BE50021
	 */
	public ActionStep_UpdateIssue_Action() {

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
			if (ComLogGen.generateCommLog(mwIssue) && ETSMW_IssueDAO.updateActiononIssueRecord(mwIssue))
						flg=true;
					
		}
		catch (Exception e) {
		}
		return flg;
	}
}
