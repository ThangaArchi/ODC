/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
package oem.edge.ets.fe.ismgt.cntrl;

import java.sql.SQLException;
import java.util.List;

import oem.edge.common.Global;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcMailHandler;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.bdlg.RemindIssueBdlg;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IssueRemindJob {

	private static Log logger = EtsLogger.getLogger(IssueRemindJob.class);
	public static final String VERSION = "1.2";

	/**
	 * 
	 */
	public IssueRemindJob() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();

		try {

			logger.debug("START  run time for Remind job:::" + Global.getCurrentDate());

			RemindIssueBdlg issueBdlg = new RemindIssueBdlg();

			logger.debug("start getting all remind recs");
			List remindList = issueBdlg.getRemindIssueRecs();
			logger.debug("end getting all remind recs");

			logger.debug(" start sending emails for  all remind recs");
			issueBdlg.sendRemindEmails(remindList);
			logger.debug(" end sending emails for  all remind recs");

			//////////////PMO ISSUES////

			logger.debug("start getting all PMO remind recs");
			List pmoRemindList = issueBdlg.getRemindPmoIssueRecs();
			logger.debug("end getting all PMO remind recs");

			logger.debug(" start sending emails for  all PMO remind recs");
			issueBdlg.sendRemindPMOEmails(pmoRemindList);
			logger.debug(" end sending emails for  all PMO remind recs");

			logger.debug("END run time for Remind job:::" + Global.getCurrentDate());

		} catch (SQLException sqlEx) {

			logger.fatal("SQLException in IssueRemindJob", sqlEx);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN ISSUE REMIND JOB", "FATAL SQL Exception in start of REMIND JOB");
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.fatal("Exception in IssueRemindJob", ex);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN ISSUE REMIND JOB", "Exception in start of REMIND JOB");
			ex.printStackTrace();
		}

	}
}
