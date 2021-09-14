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
/*
* Created on Jun 9, 2005
*
* To change the template for this generated file go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/
package oem.edge.ets.fe.ismgt.middleware;

import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;

/**
* @author jetendra
*
* To change the template for this generated type comment go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/
public class ActionStep_UpdateFeedback_Submit implements ActionStep {
	public static final String VERSION = "1.1";
	/* (non-Javadoc)
	 * @see 
oem.edge.ets.fe.ismgt.middleware.ActionStep#executeActionStep(oem.edge.ets.fe.ismgt.model.ETSIssue, 
oem.edge.ets.fe.ismgt.model.EtsIssObjectKey)
	 */
	public boolean executeActionStep(ETSIssue usr1issue, EtsIssObjectKey 
issobjkey) {
		boolean flg = false;
		ETSMW_IssueDAO mwdao = new ETSMW_IssueDAO();
		ETSMWIssue mwIssue = (ETSMWIssue)usr1issue;
		try {
			if (ComLogGen.generateFeedbackCommLog(mwIssue) && ETSMW_IssueDAO.insertFeedbackRecord(mwIssue))
						flg=true;

		}
		catch (Exception e) {
		}
		return flg;
	}

}

