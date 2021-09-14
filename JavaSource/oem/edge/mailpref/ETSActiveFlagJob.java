/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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
/*
 * Created on Oct 6, 2004
 *
 */
package oem.edge.mailpref;

import java.sql.Connection;
import java.sql.SQLException;

import oem.edge.amt.AMTException;
import oem.edge.common.Global;
import oem.edge.ets.fe.acmgt.bdlg.WrkSpcActiveFlagImpl;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcMailHandler;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2ravik
 *
 */
public class ETSActiveFlagJob implements JobInterface {

	private static Log logger = EtsLogger.getLogger(ETSActiveFlagJob.class);
	public static final String VERSION = "1.6";

	/* (non-Javadoc)
	 * @see oem.edge.mailpref.JobInterface#JobImplementation(java.sql.Connection, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String JobImplementation(Connection conn, String user, String division, String function, String start_date, String start_time, String end_date, String end_time) throws SQLException {
		// TODO Auto-generated method stub

		
		// REJECT

		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
		WrkSpcActiveFlagImpl actFlagImpl = new WrkSpcActiveFlagImpl();

		try {
			
			Global.println(" ACTIVE FLAG JOB STARTS");

			logger.debug("START  run time for active flag job:::" + Global.getCurrentDate());

			actFlagImpl.updateWrkSpcUsers();

			logger.debug("END run time for active flag  job:::" + Global.getCurrentDate());
			
			Global.println(" ACTIVE FLAG JOB ENDS");

		} catch (AMTException amtEx) {

			logger.fatal("AMTException in EtsActiveFlagJob", amtEx);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN ACTIVE FLAG JOB", "FATAL AMT Exception in start of ACTIVE FLAG JOB");
			amtEx.printStackTrace();

		} catch (SQLException sqlEx) {

			logger.fatal("SQLException in EtsActiveFlagJob", sqlEx);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN ACTIVE FLAG JOB", "FATAL SQL Exception in start of ACTIVE FLAG JOB");
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.fatal("SQLException in EtsActiveFlagJob", ex);
			mailHandler.sendMailOnErrorToSupp("FATAL ERROR IN ACTIVE FLAG JOB", "Exception in start of ACTIVE FLAG JOB");
			ex.printStackTrace();
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see oem.edge.mailpref.JobInterface#mailFormat(java.lang.String, java.sql.Connection, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String mailFormat(String str, Connection conn, String user, String division, String function, String tag) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
