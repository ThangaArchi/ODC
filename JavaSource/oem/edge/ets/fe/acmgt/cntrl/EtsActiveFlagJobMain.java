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
package oem.edge.ets.fe.acmgt.cntrl;

import java.sql.SQLException;

import oem.edge.amt.AMTException;
import oem.edge.common.Global;
import oem.edge.ets.fe.acmgt.bdlg.WrkSpcActiveFlagImpl;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcMailHandler;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsActiveFlagJobMain {

	private static Log logger = EtsLogger.getLogger(EtsActiveFlagJobMain.class);
	public static final String VERSION = "1.4";

	/**
	 * 
	 */
	public EtsActiveFlagJobMain() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
		WrkSpcActiveFlagImpl actFlagImpl = new WrkSpcActiveFlagImpl();

		try {

			logger.debug("START  run time for active flag job:::" + Global.getCurrentDate());

			actFlagImpl.updateWrkSpcUsers();

			logger.debug("END run time for invite job:::" + Global.getCurrentDate());

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
	}

}
